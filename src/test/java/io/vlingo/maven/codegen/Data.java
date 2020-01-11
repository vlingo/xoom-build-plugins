package io.vlingo.maven.codegen;

import java.util.LinkedHashSet;
import java.util.Set;

public final class Data {
    public static final Set<String> FAKE_CLASSES = new LinkedHashSet<>();
    static {
        FAKE_CLASSES.add("io.vlingo.nativeexample.ping.Ping");
        FAKE_CLASSES.add("io.vlingo.nativeexample.ping.Ping__Proxy");
        FAKE_CLASSES.add("io.vlingo.nativeexample.pong.Pong");
        FAKE_CLASSES.add("io.vlingo.nativeexample.pong.Pong__Proxy");
    }
}
