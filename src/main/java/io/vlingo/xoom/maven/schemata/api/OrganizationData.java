// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata.api;

public class OrganizationData {

  public final String organizationId;
  public final String name;
  public final String description;

  public OrganizationData(final String name) {
    this(null, name);
  }

  public OrganizationData(final String organizationId, final String name) {
    this.organizationId = organizationId;
    this.name = name;
    this.description = name;
  }
}
