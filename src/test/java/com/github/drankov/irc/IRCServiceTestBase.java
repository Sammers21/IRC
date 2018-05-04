/*
 * Copyright 2018 Drankov Pavel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
