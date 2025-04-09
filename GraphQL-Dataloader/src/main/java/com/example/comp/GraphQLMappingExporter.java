package com.example.comp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class GraphQLMappingExporter implements ApplicationRunner {

    private final ApplicationContext applicationContext;
    private final Set<Class<?>> discoveredTypes = new HashSet<>();
    private final Map<String, StringBuilder> sdlFiles = new HashMap<>();
    private final Map<String, List<Method>> schemaMappingsByType = new HashMap<>();
    private final Set<Class<?>> typesToBuild = new HashSet<>();
    private final Set<String> schemaMappingTypeNames = new HashSet<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        indexSchemaMappings();
        scanAndIndexMappings();
        resolveSchemaMappingTypeNames();
        buildAllTypes();

        saveSDL("query.graphqls", closeBlock(sdlFiles.get("Query")));
        saveSDL("mutation.graphqls", closeBlock(sdlFiles.get("Mutation")));
        if (sdlFiles.containsKey("types")) {
            saveSDL("types.graphqls", sdlFiles.get("types"));
        }
    }

    private StringBuilder closeBlock(StringBuilder block) {
        if (block != null && !block.toString().endsWith("}\n")) {
            block.append("}\n");
        }
        return block;
    }

    private void indexSchemaMappings() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Controller.class);

        for (Object bean : beans.values()) {
            Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SchemaMapping.class)) {
                    SchemaMapping sm = method.getAnnotation(SchemaMapping.class);
                    String typeName = sm.typeName();
                    schemaMappingsByType.computeIfAbsent(typeName, k -> new ArrayList<>()).add(method);
                    schemaMappingTypeNames.add(typeName);
                }
            }
        }
    }

    private void scanAndIndexMappings() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Controller.class);

        for (Object bean : beans.values()) {
            Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(QueryMapping.class)) {
                    appendToSDL("Query", buildSDLFromMethod(method));
                } else if (method.isAnnotationPresent(MutationMapping.class)) {
                    appendToSDL("Mutation", buildSDLFromMethod(method));
                }

                typesToBuild.add(getWrappedReturnType(method));
                for (Parameter param : method.getParameters()) {
                    if (param.isAnnotationPresent(Argument.class)) {
                        typesToBuild.add(getWrappedParameterType(param));
                    }
                }
            }
        }
    }

    private void resolveSchemaMappingTypeNames() {
        for (String typeName : schemaMappingTypeNames) {
            typesToBuild.stream()
                    .filter(c -> c.getSimpleName().equals(typeName))
                    .findFirst()
                    .ifPresent(typesToBuild::add);
        }
    }

    private void buildAllTypes() {
        for (Class<?> type : typesToBuild) {
            registerTypeIfNeeded(type);
        }
    }

    private void registerTypeIfNeeded(Class<?> clazz) {
        if (clazz == null || isScalar(clazz) || discoveredTypes.contains(clazz)
                || isSpringInternal(clazz) || isJavaCoreType(clazz) || isFrameworkWrapper(clazz)) return;

        discoveredTypes.add(clazz);

        if (clazz.isEnum()) {
            buildEnumType(clazz);
        } else {
            buildObjectType(clazz);
        }
    }

    private void buildObjectType(Class<?> clazz) {
        String typeName = clazz.getSimpleName();
        StringBuilder builder = new StringBuilder("type " + typeName + " {\n");
        Set<String> addedFields = new HashSet<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(JsonIgnore.class)) continue;

            String name = field.getName();
            builder.append("  ").append(name).append(": ").append(resolveTypeFromField(field)).append("\n");
            addedFields.add(name);

            registerTypeIfNeeded(getWrappedFieldType(field));
        }

        for (Method method : schemaMappingsByType.getOrDefault(typeName, List.of())) {
            SchemaMapping sm = method.getAnnotation(SchemaMapping.class);
            String fieldName = sm.field().isEmpty() ? method.getName() : sm.field();

            if (addedFields.contains(fieldName)) continue;

            List<String> args = new ArrayList<>();
            for (Parameter param : method.getParameters()) {
                if (param.isAnnotationPresent(Argument.class)) {
                    String name = param.getAnnotation(Argument.class).name();
                    if (name.isEmpty()) name = param.getName();
                    args.add(name + ": " + resolveTypeFromParameter(param));
                }
            }

            builder.append("  ").append(fieldName);
            if (!args.isEmpty()) builder.append("(").append(String.join(", ", args)).append(")");
            builder.append(": ").append(resolveTypeFromReturn(method)).append("\n");

            registerTypeIfNeeded(getWrappedReturnType(method));
        }

        builder.append("}\n\n");
        sdlFiles.computeIfAbsent("types", k -> new StringBuilder()).append(builder);
    }

    private StringBuilder buildSDLFromMethod(Method method) {
        StringBuilder builder = new StringBuilder();
        String methodName = method.getName();
        builder.append("  ").append(methodName);

        List<String> args = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            if (!param.isAnnotationPresent(Argument.class)) continue;
            String name = param.getAnnotation(Argument.class).name();
            if (name.isEmpty()) name = param.getName();
            args.add(name + ": " + resolveTypeFromParameter(param));
        }

        builder.append("(").append(String.join(", ", args)).append("): ");
        builder.append(resolveTypeFromReturn(method));
        builder.append("\n");
        return builder;
    }

    private void appendToSDL(String typeName, StringBuilder block) {
        sdlFiles.computeIfAbsent(typeName, k -> new StringBuilder("type " + typeName + " {\n"))
                .append(block);
    }

    private void buildEnumType(Class<?> clazz) {
        StringBuilder builder = new StringBuilder("enum ").append(clazz.getSimpleName()).append(" {\n");
        for (Object constant : clazz.getEnumConstants()) {
            builder.append("  ").append(constant.toString()).append("\n");
        }
        builder.append("}\n\n");

        sdlFiles.computeIfAbsent("types", k -> new StringBuilder()).append(builder);
    }

    private String resolveTypeFromField(Field field) {
        if (Collection.class.isAssignableFrom(field.getType())) {
            if (field.getGenericType() instanceof ParameterizedType pt) {
                Type inner = pt.getActualTypeArguments()[0];
                if (inner instanceof Class<?> innerClass) {
                    registerTypeIfNeeded(innerClass);
                    return "[" + resolveGraphQLScalarType(innerClass) + "]";
                }
            }
            return "[Unknown]";
        }

        return applyNullability(resolveGraphQLScalarType(field.getType()), field);
    }

    private String resolveTypeFromParameter(Parameter param) {
        if (Collection.class.isAssignableFrom(param.getType())) {
            if (param.getParameterizedType() instanceof ParameterizedType pt) {
                Type inner = pt.getActualTypeArguments()[0];
                if (inner instanceof Class<?> innerClass) {
                    registerTypeIfNeeded(innerClass);
                    return "[" + resolveGraphQLScalarType(innerClass) + "]";
                }
            }
            return "[Unknown]";
        }

        return applyNullability(resolveGraphQLScalarType(param.getType()), param);
    }

    private String resolveTypeFromReturn(Method method) {
        Class<?> actualType = getWrappedReturnType(method);
        registerTypeIfNeeded(actualType);
        return resolveGraphQLScalarType(actualType);
    }

    private String applyNullability(String type, AnnotatedElement annotated) {
        return (annotated.isAnnotationPresent(NotNull.class) || annotated.isAnnotationPresent(NotBlank.class)) ? type + "!" : type;
    }

    private String resolveGraphQLScalarType(Class<?> type) {
        if (type == String.class) return "String";
        if (type == Long.class || type == long.class) return "ID";
        if (type == Integer.class || type == int.class) return "Int";
        if (type == Boolean.class || type == boolean.class) return "Boolean";
        if (type == Double.class || type == double.class || type == Float.class || type == float.class) return "Float";
        if (type.isEnum()) return type.getSimpleName();
        return type.getSimpleName();
    }

    private Class<?> getWrappedReturnType(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType pt) {
            Class<?> raw = (Class<?>) pt.getRawType();
            if (raw == CompletableFuture.class || raw == ResponseEntity.class || raw == Optional.class) {
                Type inner = pt.getActualTypeArguments()[0];
                if (inner instanceof Class<?> innerClass) return innerClass;
            }
        }
        return method.getReturnType();
    }

    private Class<?> getWrappedParameterType(Parameter param) {
        Type type = param.getParameterizedType();
        if (type instanceof ParameterizedType pt) {
            Class<?> raw = (Class<?>) pt.getRawType();
            if (raw == Optional.class || raw == ResponseEntity.class) {
                Type inner = pt.getActualTypeArguments()[0];
                if (inner instanceof Class<?> innerClass) return innerClass;
            }
        }
        return param.getType();
    }

    private Class<?> getWrappedFieldType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType pt) {
            Type inner = pt.getActualTypeArguments()[0];
            if (inner instanceof Class<?> innerClass) return innerClass;
        }
        return field.getType();
    }

    private boolean isScalar(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Integer.class || clazz == Long.class
                || clazz == Boolean.class || clazz == Double.class || clazz == Float.class;
    }

    private boolean isFrameworkWrapper(Class<?> clazz) {
        return clazz == CompletableFuture.class
                || clazz == Optional.class
                || clazz == ResponseEntity.class
                || clazz == ModelAndView.class
                || clazz == ModelMap.class;
    }

    private boolean isSpringInternal(Class<?> clazz) {
        String name = clazz.getName();
        return name.startsWith("org.springframework.")
                || name.startsWith("jakarta.servlet.")
                || name.startsWith("org.apache.")
                || name.contains("ErrorProperties");
    }

    private boolean isJavaCoreType(Class<?> clazz) {
        return clazz == Object.class
                || clazz == List.class
                || clazz == Set.class
                || clazz == Map.class
                || clazz.getName().startsWith("java.lang.reflect");
    }

    private void saveSDL(String fileName, StringBuilder content) throws IOException {
        if (content == null) return;
        Path path = Path.of("src/main/resources/generated-sdl/", fileName);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content.toString(), StandardCharsets.UTF_8);
    }
}