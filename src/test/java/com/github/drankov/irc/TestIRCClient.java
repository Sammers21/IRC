package com.github.drankov.irc;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestIRCClient implements Closeable {

    public Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    TestIRCClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        socket.setKeepAlive(true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

    }

    public void sendLine(String line) {
        writer.println(line);
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
