// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.maven.codegen;

import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ProtocolScannerTest {
    @Test
    public void shouldFindProxiesAndProtocolsFromADirectory() {
        ProtocolScanner scanner = new ProtocolScanner(new File("src/test/resources/ProxyScannerTest/shouldFindProxiesAndProtocolsFromADirectory"));
        Set<String> reflectionClasses = scanner.scan();

        assertEquals(Data.FAKE_CLASSES, reflectionClasses);

    }
}
