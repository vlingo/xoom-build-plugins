// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.codegen;

import static java.util.stream.Collectors.toSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import io.vlingo.xoom.actors.ProxyGenerator;

@Mojo(name="nativeActorProxyGen", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class NativeActorProxyGenerator extends AbstractMojo {
  private static final Path REFLECTION_JSON = new File("target/classes/reflection.json").toPath();

  @Parameter(defaultValue = "src/main/java")
  private String sourceRoot;
  @Parameter()
  private String[] additionalProtocols;
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject mavenProject;

  private final io.vlingo.xoom.actors.Logger logger;

  public NativeActorProxyGenerator() {
    this.logger = io.vlingo.xoom.actors.Logger.basicLogger();
    logger.info("vlingo/maven: Native actor proxy generator loaded.");
  }

  @Override
  public void execute() throws MojoExecutionException {
    final ProtocolScanner scanner = new ProtocolScanner(new File(sourceRoot));
    final Set<String> protocolsAndProxies = new HashSet<>(scanner.scan());
    final ProxyGenerator proxyGenerator = newGenerator();
    final NativeImageReflectionConfigurationGenerator reflectionConfigurationGenerator = new NativeImageReflectionConfigurationGenerator(protocolsAndProxies);
    final Set<String> actorProtocols = protocolsAndProxies.stream().filter(className -> !className.endsWith("__Proxy")).collect(toSet());

    try {
      actorProtocols.forEach(proxyGenerator::generateFor);

      if (additionalProtocols != null) {
        protocolsAndProxies.addAll(Arrays.asList(additionalProtocols));
      }

      protocolsAndProxies.addAll(additionalProtocolsFromLibraries());
      Files.write(REFLECTION_JSON, reflectionConfigurationGenerator.generate().getBytes());
    } catch (Exception e) {
      final String message = "Proxy generator failed because: " + e.getMessage();
      logger.error(message, e);
      e.printStackTrace();
      throw new MojoExecutionException(message, e);
    }
  }

  private ProxyGenerator newGenerator() throws MojoExecutionException {
    try {
      return ProxyGenerator.forMain(getClass().getClassLoader(), true, logger);
    } catch (Exception e) {
      final String message = "Proxy generator failed because: " + e.getMessage();
      logger.error(message, e);
      e.printStackTrace();
      throw new MojoExecutionException(message, e);
    }
  }

  private Set<String> additionalProtocolsFromLibraries() {
    return mavenProject.getArtifacts().stream()
            .peek(e -> {
              logger.info(e.toString());
              logger.info(e.getType());
              logger.info(e.getFile().toString());
            })
            .filter(Artifact::isResolved)
            .map(Artifact::getFile)
            .map(this::contentFromVlingoReflectionList)
            .filter(Objects::nonNull)
            .flatMap(fileContent -> Arrays.stream(fileContent.split("\n")))
            .map(String::trim)
            .collect(toSet());
  }

  private String contentFromVlingoReflectionList(File file) {
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
      ZipEntry zipEntry = zis.getNextEntry();
      byte[] buffer = new byte[1024];
      while (zipEntry != null) {
        if (zipEntry.getName().equals("xoom-reflection")) {
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          int len;
          while ((len = zis.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
          }
          bos.close();
          return new String(bos.toByteArray());
        } else {
          zipEntry = zis.getNextEntry();
        }
      }
    } catch (Exception e) {
      logger.error("Could not load reflection list from " + file.toString(), e);
      return null;
    }

    return null;
  }
}
