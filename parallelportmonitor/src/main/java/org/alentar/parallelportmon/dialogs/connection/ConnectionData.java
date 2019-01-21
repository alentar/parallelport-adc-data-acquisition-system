package org.alentar.parallelportmon.dialogs.connection;

public class ConnectionData {
    String host;
    int port;

    public ConnectionData(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
