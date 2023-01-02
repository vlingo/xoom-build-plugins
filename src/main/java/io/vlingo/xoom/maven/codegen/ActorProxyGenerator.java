// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.codegen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.vlingo.xoom.actors.ProxyGenerator;

@Mojo(name="actorProxyGen")
public class ActorProxyGenerator extends AbstractMojo {

  @Parameter(required=true)
  private String[] actorProtocols;
  private final io.vlingo.xoom.actors.Logger logger;

  public ActorProxyGenerator() {
    this.logger = io.vlingo.xoom.actors.Logger.basicLogger();
    logger.info("XOOM: Actor proxy generator loaded.");
  }

  @Override
  public void execute() throws MojoExecutionException {
    try (final ProxyGenerator generator = ProxyGenerator.forMain(getClass().getClassLoader(), true, logger)) {
      for (final String actorProtocol : actorProtocols) {
        logger.info("XOOM: Generating proxy for: " + actorProtocol);
        generator.generateFor(actorProtocol);
        logger.info("XOOM: Generation done.");
      }
    } catch (Exception e) {
      final String message = "Proxy generator failed because: " + e.getMessage();
      logger.error(message, e);
      e.printStackTrace();
      throw new MojoExecutionException(message, e);
    }
  }
}
