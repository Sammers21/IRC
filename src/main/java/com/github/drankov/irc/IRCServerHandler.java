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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler embedded into the netty pipeline.
 */
public class IRCServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Pattern LOGIN_COMMAND = Pattern.compile("^/login (\\w+) (\\w+)$");
    private static final Pattern JOIN_COMMAND = Pattern.compile("^/join (\\w+)$");
    private static final Pattern LEAVE_COMMAND = Pattern.compile("^/leave$");
    private static final Pattern USERS_COMMAND = Pattern.compile("^/users$");
    private final IRCSever ircSever;

    /**
     * Construct a new handler, which delegates all user events to the given {@link IRCSever} instance
     *
     * @param ircSever the server to delegate all events
     */
    IRCServerHandler(IRCSever ircSever) {
        this.ircSever = ircSever;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) {
        String reqWithTrim = request.trim();
        Matcher leave = LEAVE_COMMAND.matcher(reqWithTrim);
        if (leave.matches()) {
            ctx.disconnect();
            ctx.close();
            return;
        }
        Matcher login = LOGIN_COMMAND.matcher(reqWithTrim);
        if (login.matches()) {
            ircSever.handleLogin(ctx, login.group(1), login.group(2));
            return;
        }
        Matcher join = JOIN_COMMAND.matcher(reqWithTrim);
        if (join.matches()) {
            ircSever.handleJoin(ctx, join.group(1));
            return;
        }
        Matcher users = USERS_COMMAND.matcher(reqWithTrim);
        if (users.matches()) {
            ircSever.handleUsers(ctx);
            return;
        }

        // at this point we can assume that the user sent a common text message
        ircSever.handleInComingMessage(ctx, request);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("You have just entered the Pavel Drankov IRC server!");
        ctx.write("The server is a open source project");
        ctx.write("Check out the github page: https://github.com/Sammers21/IRC");
        ctx.write("Command list:");
        ctx.write("Command set for this server:");
        ctx.write("\t/login name password — if user not exists create profile else login");
        ctx.write("\t/join channel — try to join channel (max 1000 active clients per channel is allowed). ");
        ctx.write("\t\tIf client’s limit exceeded - error is send, otherwise join channel and send last 100000 messages of activity.");
        ctx.write("\t\tIf room not exits - it is created first then try to join.");
        ctx.write("\t/leave - disconnect client");
        ctx.write("\t/users — show users in the channel");
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}