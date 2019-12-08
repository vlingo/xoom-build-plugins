// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.maven.schemata;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name = "pull-schemata", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class PullSchemataMojo extends AbstractMojo {

    @Parameter(name = "schemataService")
    private SchemataService schemataService;

    private final io.vlingo.actors.Logger logger;

    public PullSchemataMojo() {
        this.logger = io.vlingo.actors.Logger.basicLogger();
        logger.info("vlingo/maven: Pulling code generated from vlingo/schemata registry.");
    }

    @Override
    public void execute() throws MojoExecutionException {
        logger.info(schemataService.toString());
    }
}
