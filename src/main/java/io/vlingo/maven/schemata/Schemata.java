package io.vlingo.maven.schemata;

import java.net.URL;

public class Schemata {

    URL serviceUrl;

    String clientOrganization;

    String clientUnit;

    public URL getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(URL serviceUrl) {
        this.serviceUrl = serviceUrl;
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

    @Override
    public String toString() {
        return "Schemata{" +
                "serviceUrl=" + serviceUrl +
                ", clientOrganization='" + clientOrganization + '\'' +
                ", clientUnit='" + clientUnit + '\'' +
                '}';
    }
}
