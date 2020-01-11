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
