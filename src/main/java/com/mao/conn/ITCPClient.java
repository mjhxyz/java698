package com.mao.conn;

import java.io.IOException;

public interface ITCPClient {
    byte[] request(byte[] request) throws IOException;
    void connect() throws IOException;
}
