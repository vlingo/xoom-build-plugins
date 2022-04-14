// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata.api;

public class ContextData {

  public final String contextId;
  public final String namespace;
  public final String description;

  public ContextData(final String namespace) {
    this(null, namespace);
  }

  public ContextData(final String contextId, final String namespace) {
    this.contextId = contextId;
    this.namespace = namespace;
    this.description = namespace;
  }

}
