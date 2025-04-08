
package com.example.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GraphQLMappingExporter {

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLMappingExporter.class);
    private static final String OUTPUT_DIR = "src/main/resources/graphql";
    private static final String QUERY_FILE = "query.graphqls";
    private static final String MUTATION_FILE = "mutation.graphqls";
    private static final String CONTROLLERS_PACKAGE = "com.example";

    private final Set<Class<?>> processedTypes = new HashSet<>();
    private final Map<Class<?>, String> typeDefinitions = new LinkedHashMap<>();

    @PostConstruct
    public void generateSDL() {
        try {
            if (sdlFilesExist()) return;

            Set<Class<?>> controllers = scanControllers();

            String querySDL = buildSDLFromAnnotations(controllers, QueryMapping.class, "type Query");
            String mutationSDL = buildSDLFromAnnotations(controllers, MutationMapping.class, "type Mutation");

            if (!querySDL.trim().equals("type Query {\n}")) {
                saveSDL(QUERY_FILE, querySDL);
            }
            if (!mutationSDL.trim().equals("type Mutation {\n}")) {
                saveSDL(MUTATION_FILE, mutationSDL);
            }
        } catch (Exception e) {
            LOG.error("Failed to export SDL files: {}", e.getMessage(), e);
        }
    }

    private boolean sdlFilesExist() {
        Path queryPath = Paths.get(OUTPUT_DIR, QUERY_FILE);
        Path mutationPath = Paths.get(OUTPUT_DIR, MUTATION_FILE);
        boolean exists = Files.exists(queryPath) && Files.exists(mutationPath);
        if (exists) {
            LOG.info("Both SDL files already exist. Skipping SDL generation.");
        }
        return exists;
    }

    private Set<Class<?>> scanControllers() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

        return scanner.findCandidateComponents(CONTROLLERS_PACKAGE).stream()
                .map(beanDef -> {
                    try {
                        return Class.forName(beanDef.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        LOG.warn("Could not load class {}", beanDef.getBeanClassName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String buildSDLFromAnnotations(Set<Class<?>> classes, Class<? extends Annotation> mapping, String rootType) {
        StringBuilder typeBlock = new StringBuilder(rootType + " {\n");

        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(mapping)) continue;

                String methodName = method.getName();
                Parameter[] parameters = method.getParameters();

                StringJoiner argsJoiner = new StringJoiner(", ");
                for (Parameter param : parameters) {
                    String paramName = param.getName();
                    String gqlType = resolveGraphQLType(param.getParameterizedType(), param.getName());
                    boolean required = isRequired(param);
                    argsJoiner.add(paramName + " : " + gqlType + (required ? "!" : ""));
                    scanType(param.getType());
                }

                Class<?> returnType = method.getReturnType();
                scanType(returnType);

                String returnTypeName = resolveGraphQLType(returnType, methodName);
                typeBlock.append("  ")
                        .append(methodName)
                        .append(parameters.length > 0 ? "(" + argsJoiner + ")" : "")
                        .append(" : ")
                        .append(returnTypeName)
                        .append("\n");
            }
        }

        typeBlock.append("}\n\n");

        return typeBlock + String.join("\n", typeDefinitions.values());
    }

    private void scanType(Class<?> type) {
        if (!isCustomType(type) || processedTypes.contains(type)) return;
        processedTypes.add(type);

        if (type.isEnum()) {
            StringBuilder enumBlock = new StringBuilder();
            enumBlock.append("enum ").append(type.getSimpleName()).append(" {\n");
            for (Object constant : type.getEnumConstants()) {
                enumBlock.append("  ").append(constant.toString()).append("\n");
            }
            enumBlock.append("}\n");
            typeDefinitions.put(type, enumBlock.toString());
            return;
        }

        String kind = type.getSimpleName().endsWith("Request") ? "input" : "type";
        StringBuilder sdl = new StringBuilder();
        sdl.append(kind).append(" ").append(type.getSimpleName()).append(" {\n");

        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonIgnore.class) || Modifier.isStatic(field.getModifiers())) continue;

            String fieldName = field.getName();
            String gqlType = resolveGraphQLType(field.getGenericType(), fieldName);
            boolean isRequired = field.isAnnotationPresent(NotNull.class) || field.isAnnotationPresent(NotBlank.class);

            sdl.append("  ").append(fieldName).append(" : ").append(gqlType);
            if (isRequired) sdl.append("!");
            sdl.append("\n");

            if (isCustomType(field.getType())) {
                scanType(field.getType());
            }

            if (Collection.class.isAssignableFrom(field.getType())) {
                Type generic = field.getGenericType();
                if (generic instanceof ParameterizedType pt) {
                    Type actual = pt.getActualTypeArguments()[0];
                    if (actual instanceof Class<?> actualClass) {
                        scanType(actualClass);
                    }
                }
            }
        }

        sdl.append("}\n");
        typeDefinitions.put(type, sdl.toString());
    }

    private boolean isCustomType(Class<?> type) {
        return !(type.isPrimitive()
                || type.equals(String.class)
                || Number.class.isAssignableFrom(type)
                || type.equals(Boolean.class)
                || type.isEnum()
                || Collection.class.isAssignableFrom(type)
                || type.getName().startsWith("java."));
    }

    private boolean isRequired(Parameter param) {
        return Arrays.stream(param.getAnnotations())
                .map(a -> a.annotationType().getSimpleName())
                .anyMatch(name -> name.equals("NotNull") || name.equals("NonNull"));
    }

    private String resolveGraphQLType(Type type, String name) {
        if (type instanceof ParameterizedType pt && pt.getRawType() == List.class) {
            Type actualType = pt.getActualTypeArguments()[0];
            return "[" + resolveGraphQLType(actualType, name) + "]";
        }

        if (type instanceof Class<?> cls) {
            if (name.equalsIgnoreCase("id") || cls == Long.class || cls.getSimpleName().equals("UUID")) return "ID";
            if (cls == String.class || cls == long.class) return "String";
            if (cls == Integer.class || cls == int.class) return "Int";
            if (cls == Double.class || cls == double.class) return "Float";
            if (cls == Boolean.class || cls == boolean.class) return "Boolean";

            return cls.getSimpleName();
        }

        return "String";
    }

    private void saveSDL(String fileName, String content) throws Exception {
        Path outputPath = Paths.get(OUTPUT_DIR, fileName);
        Files.createDirectories(outputPath.getParent());
        if (Files.exists(outputPath)) {
            LOG.info("SDL '{}' already exists. Skipping export.", outputPath);
            return;
        }

        try (OutputStream os = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(content.getBytes(StandardCharsets.UTF_8));
            LOG.info("SDL '{}' successfully exported!", outputPath);
        }
    }
}
