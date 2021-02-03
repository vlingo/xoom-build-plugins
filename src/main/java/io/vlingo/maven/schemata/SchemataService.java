package io.vlingo.maven.schemata;

import java.net.URL;

public class SchemataService {

    private URL url;

    private String clientOrganization;

    private String clientUnit;

    private boolean hierarchicalCascade = false;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getClientOrganization() {
        return clientOrganization;
    }

    public void setClientOrganization(String clientOrganization) {
        this.clientOrganization = clientOrganization;
    }

    public String getClientUnit() {
        return clientUnit;
    }

    public void setClientUnit(String clientUnit) {
        this.clientUnit = clientUnit;
    }

    public void setHierarchicalCascade(boolean createSchemaParents) {
        this.hierarchicalCascade = createSchemaParents;
    }

    public boolean getHierarchicalCascade() {
        return hierarchicalCascade;
    }

    @Override
    public String toString() {
        return "SchemataService{" +
                "url=" + url +
                ", clientOrganization='" + clientOrganization + '\'' +
                ", clientUnit='" + clientUnit + '\'' +
                ", hierarchicalCascade='" + hierarchicalCascade + '\'' +
                '}';
    }
}
