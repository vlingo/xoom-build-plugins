// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata.api;

import org.codehaus.plexus.util.StringUtils;

public class SchemaData {

  public final String name;
  public final String category;
  public final String description;

  public SchemaData(final String name, final String category) {
    this.name = name;
    this.description = name;
    this.category = StringUtils.capitalise(category.toLowerCase());
  }

  public boolean match(final String name, final String category) {
    return this.name.equals(name) && this.category.equals(category);
  }
}
