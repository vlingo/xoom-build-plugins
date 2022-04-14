// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Iterates over a source directory with Java classes and finds out which interfaces are meant to be Proxy interfaces.
 * It tries to detect cases where .actorFor is used to instantiate actors and resolves imports to detect the full qualified
 * name of the proxy class.
 *
 * @since 1.0.0
 */
public class ProtocolScanner {
    private final File directory;

    public ProtocolScanner(final File directory) {
        this.directory = directory;
    }

    /**
     * @return the list of all interfaces and proxies that need to be loaded during runtime
     */
    public Set<String> scan() {
        return scanDirectory(directory).collect(toSet());
    }

    private Stream<String> scanDirectory(File directory) {
        final File[] values = directory.listFiles();
        if (values == null) {
            return Stream.empty();
        }

        return Stream.of(values).flatMap(file -> {
            if (file.isDirectory()) {
                return scanDirectory(file);
            } else {
                return scanFile(file);
            }
        });
    }

    private Stream<String> scanFile(File file) {
        if (!file.getName().endsWith(".java")) {
            return Stream.empty();
        }

        String javaCode = load(file);
        String packageName = packageName(javaCode);
        Map<String, String> imports = importsOf(javaCode);
        Set<String> proxies = usagesAsProxies(javaCode);

        return proxies.stream()
                .map(protocol -> imports.getOrDefault(protocol, fromPackageName(packageName, protocol)))
                .flatMap(protocol -> Stream.of(protocol, protocol + "__Proxy"));
    }

    private String packageName(String javaCode) {
        return extractFromWrappingTokens(javaCode, "package", ";");
    }

    private Map<String, String> importsOf(String javaCode) {
        return Arrays.stream(javaCode.split("\n"))
                .map(String::trim)
                .filter(line -> line.startsWith("import") && line.endsWith(";"))
                .map(line -> extractFromWrappingTokens(javaCode, "import", ";"))
                .collect(groupingBy(javaImport -> javaImport.substring(javaImport.lastIndexOf(".") + 1)))
                .entrySet()
                .stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue().get(0)));
    }

    private Set<String> usagesAsProxies(String javaCode) {
        return Arrays.stream(javaCode.split("\n"))
                .map(String::trim)
                .filter(line -> line.contains(".actorFor"))
                .flatMap(line -> extractProtocols(javaCode))
                .collect(toSet());
    }

    private Stream<String> extractProtocols(String javaCode) {
        if (!javaCode.contains("new Class")) {
            return Stream.of(extractFromWrappingTokens(javaCode, ".actorFor(", ".class"));
        }
        final String multipleProtocols = "\\{(.*?)\\}";
        Pattern p = Pattern.compile(multipleProtocols);
        Matcher m = p.matcher(javaCode);
        if (m.find()) {
            String[] protocols = m.group(1).replaceAll(".class", "").split(",");
            return Arrays.stream(protocols).map(String::trim);
        }
        return Stream.empty();
    }

    private String load(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String extractFromWrappingTokens(String javaCode, String startToken, String endToken) {
        int packageStart = javaCode.indexOf(startToken);
        if (packageStart == -1) {
            return "";
        }

        packageStart += startToken.length();
        int packageEnd = javaCode.indexOf(endToken, packageStart);
        if (packageEnd == -1) {
            return "";
        }

        return javaCode.substring(packageStart, packageEnd).trim();
    }

    private String fromPackageName(String packageName, String proxy) {
        if (packageName.equals("")) {
            return proxy;
        } else {
            return packageName + "." + proxy;
        }
    }
}
