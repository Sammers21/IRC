package com.github.drankov.irc;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class ChannelTest extends IRCServiceTestBase {

    static final String SUCC_ENTERED = "successfully entered";
    static final String MSG = "hello world1";

    @Test
    public void simpleMessageSentTest() throws IOException {
        testIRCClient.sendLine("/login login password");
        testIRCClient.sendLine("/join chat1");
        testIRCClient.readLine();
        assertTrue(testIRCClient.readLine().contains(SUCC_ENTERED));

        TestIRCClient testIRCClient2 = new TestIRCClient(HOST, PORT);
        testIRCClient2.sendLine("/login login1 password");
        testIRCClient2.sendLine("/join chat1");
        testIRCClient2.readLine();
        assertTrue(testIRCClient2.readLine().contains(SUCC_ENTERED));

        testIRCClient.sendLine(MSG);
        assertTrue(testIRCClient2.readLine().contains(MSG));
        testIRCClient2.close();
    }

    @Test
    public void lastSavedMessagesLimit() throws IOException {
        testIRCClient.sendLine("/login login password");
        testIRCClient.sendLine("/join chat1");
        testIRCClient.readLine();
        assertTrue(testIRCClient.readLine().contains(SUCC_ENTERED));

        for (int i = 0; i < App.DEFAULT_LAST_MESSAGES_STORED + 1; i++) {
            testIRCClient.sendLine(MSG);
        }

        TestIRCClient testIRCClient2 = new TestIRCClient(HOST, PORT);
        testIRCClient2.sendLine("/login login1 password");
        testIRCClient2.sendLine("/join chat1");
        testIRCClient2.readLine();
        assertTrue(testIRCClient2.readLine().contains(SUCC_ENTERED));

        for (int i = 0; i < App.DEFAULT_LAST_MESSAGES_STORED; i++) {
            assertTrue(testIRCClient2.readLine().contains(MSG));
        }

        testIRCClient2.close();
    }

    @Test
    public void maxConnectedClients() throws IOException {
        ArrayList<TestIRCClient> testIRCClients = new ArrayList<>();
        for (int i = 0; i <= App.DEFAULT_ALLOWED_CLIENTS_IN_ONE_CHANNEL; i++) {
            TestIRCClient client = new TestIRCClient(HOST, PORT);
            client.sendLine("/login login" + i + " password");
            client.sendLine("/join chat1");
            client.readLine();
            if (i != App.DEFAULT_ALLOWED_CLIENTS_IN_ONE_CHANNEL) {
                assertTrue(client.readLine().contains(SUCC_ENTERED));
            } else {
                assertTrue(client.readLine().contains("full"));
            }
            testIRCClients.add(client);
        }

        testIRCClients.forEach(client -> {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void userList() throws IOException {
        testIRCClient.sendLine("/login login password");
        testIRCClient.sendLine("/join chat1");
        testIRCClient.readLine();
        assertTrue(testIRCClient.readLine().contains(SUCC_ENTERED));


        TestIRCClient testIRCClient2 = new TestIRCClient(HOST, PORT);
        testIRCClient2.sendLine("/login login1 password");
        testIRCClient2.sendLine("/join chat1");
        testIRCClient2.readLine();
        assertTrue(testIRCClient2.readLine().contains(SUCC_ENTERED));

        testIRCClient2.sendLine("/users");
        ArrayList<String> responses = new ArrayList<>(2);
        responses.add(testIRCClient2.readLine());
        responses.add(testIRCClient2.readLine());
        assertTrue(responses.contains("login1"));
        assertTrue(responses.contains("login"));

        testIRCClient2.close();
    }

}