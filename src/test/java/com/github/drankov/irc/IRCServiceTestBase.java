package com.github.drankov.irc;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.net.ServerSocket;

public class IRCServiceTestBase {

    App app = new App();
    TestIRCClient testIRCClient;

    public final String HOST = "localhost";
    public int PORT;

    @Before
    public void before() throws IOException, InterruptedException {
        PORT = randomFreePort();
        app.start(PORT);
        testIRCClient = new TestIRCClient("localhost", PORT);
    }

    @After
    public void after() throws IOException {
        testIRCClient.close();
        app.stop();
    }

    int randomFreePort() throws IOException {
        ServerSocket s = new ServerSocket(0);
        int port = s.getLocalPort();
        s.close();
        return port;
    }
}
