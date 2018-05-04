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

import org.junit.Test;

import java.io.IOException;

import static com.github.drankov.irc.IRCSever.ALREADY_AUTHORIZED;
import static com.github.drankov.irc.IRCSever.INVALID_PASSWORD;
import static com.github.drankov.irc.IRCSever.SUCCESS_AUTH;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class LoginTest extends IRCServiceTestBase {

    @Test
    public void loginTest() throws IOException {
        testIRCClient.sendLine("/login login password");
        String response = testIRCClient.readLine();
        assertTrue(response.contains(SUCCESS_AUTH));

        testIRCClient.sendLine("/login login password2");
        String response2 = testIRCClient.readLine();
        assertTrue(response2.contains(INVALID_PASSWORD));
    }

    @Test
    public void cantLoginOnTwoDevices() throws IOException {
        testIRCClient.sendLine("/login login password");
        String response = testIRCClient.readLine();
        assertTrue(response.contains(SUCCESS_AUTH));

        TestIRCClient testIRCClient2 = new TestIRCClient(HOST, PORT);
        testIRCClient2.sendLine("/login login password");
        String response2 = testIRCClient2.readLine();
        assertTrue(response2.contains(ALREADY_AUTHORIZED));
        testIRCClient2.close();
    }

    @Test
    public void leaveTest() throws IOException, InterruptedException {
        testIRCClient.sendLine("/leave");
        assertNull(testIRCClient.readLine());
    }
}
