// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.maven.codegen;

import io.vlingo.actors.ProxyGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Mojo(name="nativeActorProxyGen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class NativeActorProxyGenerator extends AbstractMojo {
  private static final Path REFLECTION_JSON = new File("target/classes/reflection.json").toPath();

  @org.apache.maven.plugins.annotations.Parameter(required=true)
  private String sourceRoot;
  @org.apache.maven.plugins.annotations.Parameter(defaultValue = "[]")
  private String[] additionalProtocols;

  private final io.vlingo.actors.Logger logger;

  public NativeActorProxyGenerator() {
    this.logger = io.vlingo.actors.Logger.basicLogger();
    logger.info("vlingo/maven: Native actor proxy generator loaded.");
  }

  @Override
  public void execute() throws MojoExecutionException {
    final ProtocolScanner scanner = new ProtocolScanner(new File(sourceRoot));
    final Set<String> protocolsAndProxies = new HashSet<>(scanner.scan());
    protocolsAndProxies.addAll(Arrays.asList(additionalProtocols));

    final ProxyGenerator proxyGenerator = newGenerator();
    final NativeImageReflectionConfigurationGenerator reflectionConfigurationGenerator = new NativeImageReflectionConfigurationGenerator(protocolsAndProxies);
    final Set<String> actorProtocols = protocolsAndProxies.stream().filter(className -> !className.endsWith("__Proxy")).collect(toSet());

    try {
      actorProtocols.forEach(proxyGenerator::generateFor);
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
      return ProxyGenerator.forMain(true, logger);
    } catch (Exception e) {
      final String message = "Proxy generator failed because: " + e.getMessage();
      logger.error(message, e);
      e.printStackTrace();
      throw new MojoExecutionException(message, e);
    }
  }
}
