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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Entry point of the application.
 */
public class App {

    static final int DEFAULT_PORT = 23;
    static final int DEFAULT_ALLOWED_CLIENTS_IN_ONE_CHANNEL = 10;
    static final int DEFAULT_LAST_MESSAGES_STORED = 10;

    public static void main(String[] args) throws InterruptedException {
        int port = DEFAULT_PORT;

        if (args.length > 1) {
            System.out.println("Too much arguments");
            System.exit(-1);
        } else if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Specified port should be an int");
                System.exit(-1);
            }
        }
        new App().start(port);
    }

    Channel channel;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public void start(int port) throws InterruptedException {
        System.out.println("String an IRC server on port: " + port);
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        IRCSever ircSever = new IRCSever();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());

                        pipeline.addLast(new IRCServerHandler(ircSever));
                    }
                });

        channel = b.bind(port).sync().channel();
    }

    public void stop() {
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
