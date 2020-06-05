// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.maven.codegen;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ProtocolScannerTest {
    private File file;

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();
        file = testFolder.newFile("input.java");
    }

    @Test
    public void shouldFindProxiesAndProtocolsFromADirectory() {
        ProtocolScanner scanner = new ProtocolScanner(new File("src/test/resources/ProxyScannerTest/shouldFindProxiesAndProtocolsFromADirectory"));
        Set<String> reflectionClasses = scanner.scan();

        assertEquals(Data.FAKE_CLASSES, reflectionClasses);
    }

    @Test
    public void shouldReturnProxiesAndProtocolsWhenASingleProtocolIsSpecified() throws IOException {
        String aLine = "return stage.actorFor(Ping.class, PingActor.class, PingActor::new);";
        Files.write(file.toPath(), aLine.getBytes());

        ProtocolScanner scanner = new ProtocolScanner(file.getParentFile());


        assertEquals(new HashSet<>(asList("Ping", "Ping__Proxy")), scanner.scan());
    }

    @Test
    public void shouldReturnProxiesAndProtocolsWhenMultipleProtocolsAreSpecified() throws IOException {
        String aLine = "stage.actorFor(" +
                "new Class<?>[]{StateStore.class, DispatcherControl.class},\n" +
                "Definition.has(InMemoryStateStoreActor.class, Definition.parameters(Arrays.asList(dispatcher))))";
        List<String> expected = asList("StateStore", "StateStore__Proxy", "DispatcherControl", "DispatcherControl__Proxy");

        Files.write(file.toPath(), aLine.getBytes());

        ProtocolScanner scanner = new ProtocolScanner(file.getParentFile());

        assertEquals(new HashSet<>(expected), scanner.scan());
    }

    @Test
    public void shouldReturnEmptyWhenNoProtocolsFound() throws IOException {
        String aLine = "Definition.has(InMemoryStateStoreActor.class, Definition.parameters(Arrays.asList(dispatcher)))";

        Files.write(file.toPath(), aLine.getBytes());

        ProtocolScanner scanner = new ProtocolScanner(file.getParentFile());

        assertEquals(Collections.emptySet(), scanner.scan());
    }
}
