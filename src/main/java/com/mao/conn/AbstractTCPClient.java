package com.mao.conn;

public abstract class AbstractTCPClient implements ITCPClient{
    private String host;
    private int port;

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

    public AbstractTCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
