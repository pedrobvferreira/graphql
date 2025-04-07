package com.example.component;

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
    private static final String QUERY_FILE = "query.graphql";
    private static final String MUTATION_FILE = "mutation.graphql";
    private static final String CONTROLLERS_PACKAGE = "com.example.controllers";

    // Tracks input types already registered
    private final Set<Class<?>> collectedInputTypes = new HashSet<>();

    /**
     * Invoked on application startup to export the GraphQL SDL files.
     */
    @PostConstruct
    public void generateSDL() {
        try {
            Set<Class<?>> controllers = scanControllers();

            String querySDL = buildSDLFromAnnotations(controllers, QueryMapping.class, "type Query");
            String mutationSDL = buildSDLFromAnnotations(controllers, MutationMapping.class, "type Mutation");

            if (!querySDL.trim().equals("type Query {\n}")) {
                saveSDL(QUERY_FILE, querySDL);
            } else {
                LOG.info("No @QueryMapping methods found. Skipping query.graphql");
            }

            if (!mutationSDL.trim().equals("type Mutation {\n}")) {
                saveSDL(MUTATION_FILE, mutationSDL);
            } else {
                LOG.info("No @MutationMapping methods found. Skipping mutation.graphql");
            }
        } catch (Exception e) {
            LOG.error("Failed to export SDL files: {}", e.getMessage(), e);
        }
    }

    /**
     * Scans the given package for @Controller classes.
     */
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

    /**
     * Builds SDL for methods with @QueryMapping or @MutationMapping.
     */
    private String buildSDLFromAnnotations(Set<Class<?>> classes, Class<? extends Annotation> annotationClass, String typeName) {
        StringBuilder sdl = new StringBuilder();
        StringBuilder typeBlock = new StringBuilder(typeName + " {\n");

        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(annotationClass)) continue;

                String methodName = method.getName();
                Parameter[] parameters = method.getParameters();

                StringJoiner argsJoiner = new StringJoiner(", ");
                for (Parameter param : parameters) {
                    String paramName = param.getName();
                    String gqlType = mapToGraphQLType(param, sdl);
                    boolean required = isRequired(param);

                    argsJoiner.add(paramName + ": " + gqlType + (required ? "!" : ""));
                }

                String returnType = mapReturnType(method);

                typeBlock.append("  ")
                        .append(methodName)
                        .append(parameters.length > 0 ? "(" + argsJoiner + ")" : "")
                        .append(": ")
                        .append(returnType)
                        .append("\n");
            }
        }

        typeBlock.append("}\n");
        sdl.append(typeBlock);
        return sdl.toString();
    }

    /**
     * Maps a Java method parameter to its equivalent GraphQL SDL type.
     */
    private String mapToGraphQLType(Parameter param, StringBuilder sdl) {

        String name = param.getName();
        Class<?> type = param.getType();
        Type genericType = param.getParameterizedType();

        // Handle list types (e.g., List<SubjectRequest> => [SubjectRequest])
        if (List.class.isAssignableFrom(type)) {
            String generic = "String";
            if (genericType instanceof ParameterizedType parameterizedType) {
                Type arg = parameterizedType.getActualTypeArguments()[0];
                if (arg instanceof Class<?> cls) {
                    generic = cls.getSimpleName();
                } else {
                    String raw = arg.getTypeName();
                    generic = raw.substring(raw.lastIndexOf('.') + 1);
                }
            }
            return "[" + generic + "]";
        }

        // Custom or scalar
        if (isCustomType(type)) {
            registerInputTypeIfNeeded(type, sdl);
        }

        return resolveGraphQLType(type, name); // fallback to custom object type
    }

    /**
     * Registers a class as an input type if it hasn't been registered yet.
     */
    private void registerInputTypeIfNeeded(Class<?> inputClass, StringBuilder inputBlock) {
        if (collectedInputTypes.contains(inputClass)) return;
        collectedInputTypes.add(inputClass);

        inputBlock.append("input ").append(inputClass.getSimpleName()).append(" {\n");

        for (Field field : inputClass.getDeclaredFields()) {
            String fieldName = field.getName();
            String gqlType = resolveGraphQLType(field.getType(), fieldName);
            boolean isNonNull = isNonNullField(field);

            inputBlock.append("  ").append(fieldName).append(" : ").append(gqlType);
            if (isNonNull) inputBlock.append("!");
            inputBlock.append("\n");
        }

        inputBlock.append("}\n\n");
    }

    private boolean isNonNullField(Field field) {
        return field.isAnnotationPresent(NotNull.class) || field.isAnnotationPresent(NotBlank.class);
    }

    private boolean isCustomType(Class<?> type) {
        return !(type.isPrimitive()
                || type.equals(String.class)
                || Number.class.isAssignableFrom(type)
                || type.equals(Boolean.class)
                || Collection.class.isAssignableFrom(type)
                || type.getName().startsWith("java."));
    }

    /**
     * Maps basic Java types to GraphQL scalars.
     */
    private String resolveGraphQLType(Class<?> type, String name) {
        if (name.equalsIgnoreCase("id") || type == Long.class || type.getSimpleName().equals("UUID")) return "ID";
        if (type == String.class || type == long.class) return "String";
        if (type == Integer.class || type == int.class) return "Int";
        if (type == Double.class || type == double.class) return "Float";
        if (type == Boolean.class || type == boolean.class) return "Boolean";

        return type.getSimpleName();
    }

    /**
     * Checks if a parameter is required (e.g., annotated with @NotNull or @NonNull).
     */
    private boolean isRequired(Parameter param) {
        return Arrays.stream(param.getAnnotations())
                .map(a -> a.annotationType().getSimpleName())
                .anyMatch(name -> name.equals("NotNull") || name.equals("NonNull"));
    }

    /**
     * Maps a method return type to GraphQL SDL type.
     */
    private String mapReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) return "Void";

        if (List.class.isAssignableFrom(returnType)) {
            String generic = "String";
            Type type = method.getGenericReturnType();
            if (type instanceof ParameterizedType parameterizedType) {
                Type arg = parameterizedType.getActualTypeArguments()[0];
                if (arg instanceof Class<?> cls) {
                    generic = cls.getSimpleName();
                }
            }
            return "[" + generic + "]";
        }

        return returnType.getSimpleName();
    }

    /**
     * Saves SDL content to the specified file inside the output directory.
     */
    private void saveSDL(String fileName, String content) throws Exception {
        Path outputPath = Paths.get(OUTPUT_DIR, fileName);
        Files.createDirectories(outputPath.getParent());

        try (OutputStream os = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(content.getBytes(StandardCharsets.UTF_8));
            LOG.info("SDL '{}' successfully exported!", outputPath);
        }
    }
}
