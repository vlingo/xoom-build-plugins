package io.vlingo.maven.codegen;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates a reflection.json as a String for the provided list of classes.
 *
 * @since 1.0.0
 */
public class NativeImageReflectionConfigurationGenerator {
    private static final String START_FILE = "[\n";
    private static final String START_CONFIG_LINE = "  {\n";
    private static final String CONFIG_LINE = "    \"name\" : \"%%CLASS_NAME%%\", \"allDeclaredConstructors\" : true, \"allPublicConstructors\" : true, \"allDeclaredMethods\" : true, \"allPublicMethods\" : true, \"allDeclaredClasses\" : true, \"allPublicClasses\" : true\n";
    private static final String END_CONFIG_LINE = "  }";
    private static final String END_FILE = "\n]";

    private final Set<String> classNames;

    public NativeImageReflectionConfigurationGenerator(Set<String> classNames) {
        this.classNames = classNames;
    }

    public String generate() {
        return START_FILE +
                classNames.stream().map(name -> CONFIG_LINE.replaceAll("%%CLASS_NAME%%", name)).map(line -> START_CONFIG_LINE + line + END_CONFIG_LINE).collect(Collectors.joining(",\n")) +
                END_FILE;
    }
}
