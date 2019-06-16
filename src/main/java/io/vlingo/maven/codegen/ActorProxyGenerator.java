// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.maven.codegen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import io.vlingo.actors.ProxyGenerator;

@Mojo(name="actorProxyGen")
public class ActorProxyGenerator extends AbstractMojo {

  @org.apache.maven.plugins.annotations.Parameter(required=true)
  private String[] actorProtocols;
  private final io.vlingo.actors.Logger logger;

  public ActorProxyGenerator() {
    this.logger = io.vlingo.actors.Logger.basicLogger();
    logger.info("vlingo/maven: Actor proxy generator loaded.");
  }

  @Override
  public void execute() throws MojoExecutionException {
    try (final ProxyGenerator generator = ProxyGenerator.forMain(true, logger)) {
      for (final String actorProtocol : actorProtocols) {
        logger.info("vlingo/maven: Generating proxy for: " + actorProtocol);
        generator.generateFor(actorProtocol);
        logger.info("vlingo/maven: Generation done.");
      }
    } catch (Exception e) {
      final String message = "Proxy generator failed because: " + e.getMessage();
      logger.error(message, e);
      e.printStackTrace();
      throw new MojoExecutionException(message, e);
    }
  }
}
