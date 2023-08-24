package com.mao.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SampleTCPClient extends AbstractTCPClient {
    private Socket socket;
    private int timeout;

    public SampleTCPClient(String host, int port, int timeout) throws IOException {
        super(host, port);
        this.timeout = timeout;
        this.connect();
    }

    @Override
    public byte[] request(byte[] request) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(request);
        out.flush();

        InputStream in = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        byte[] response = new byte[len];
        System.arraycopy(buffer, 0, response, 0, len);
        return response;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(getHost(), getPort());
        socket.setSoTimeout(timeout);
    }
}
