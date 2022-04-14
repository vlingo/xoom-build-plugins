// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.maven.schemata.api;

import io.vlingo.xoom.maven.schemata.SchemaVersion;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.net.URL;

public class SchemaVersionAPI extends API {

  public SchemaVersionAPI() {
    super(0);
  }

  public void create(final URL baseURL, final String route, final SchemaVersion schemaVersion) throws IOException, MojoExecutionException {
    post(SchemaVersion.class.getSimpleName(), baseURL, route, schemaVersion);
  }

}
