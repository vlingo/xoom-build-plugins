package io.vlingo.maven.schemata;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class Schemata {

    @Parameter(property = "schemata", required = true)
    private List<Schema> schemata;

    public List<Schema> getSchemata() {
        return schemata;
    }

    public void setSchemata(List<Schema> schemata) {
        this.schemata = schemata;
    }

    @Override
    public String toString() {
        return "Schemata{" +
                "schemata=" + schemata +
                '}';
    }
}
