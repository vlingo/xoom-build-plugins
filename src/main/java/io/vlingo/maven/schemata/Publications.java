package io.vlingo.maven.schemata;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.net.URL;

public class Publications {

    @Parameter(property = "schemataDirectory", defaultValue = "${basedir}/src/main/vlingo/schemata", required = true)
    protected File schemataDirectory;

    public File getSchemataDirectory() {
        return schemataDirectory;
    }

    public void setSchemataDirectory(File schemataDirectory) {
        this.schemataDirectory = schemataDirectory;
    }

    @Override
    public String toString() {
        return "Publications{" +
                "schemataDirectory=" + schemataDirectory +
                '}';
    }
}
