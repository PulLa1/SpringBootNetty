package com.yz.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

public class SocketUtils {
    public static void reconnect(ChannelHandlerContext ctx) {
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                Channel connect = null;
                do {
                    Client client = new Client();
                    try {
                        client.send();
                        ImConnection imConnection = new ImConnection();
                        connect = imConnection.connect(SocketConfig.socketHost, SocketConfig.socketPort);
                        Thread.sleep(30000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (connect == null);
            }
        }, 1L, TimeUnit.SECONDS);
    }
}
