package com.yz.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * 监听启动时是否链接失败
 */
public class ConnectionListener implements ChannelFutureListener {

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(() -> {
                Channel connect = null;
                System.err.println("服务端链接不上，开始重连操作...");
                do {
                    Client client = new Client();
                    try {
                        client.send();
                        ImConnection imConnection = new ImConnection();
                        connect = imConnection.connect(SocketConfig.socketHost, SocketConfig.socketPort);
                        Thread.sleep(60000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (connect == null);
            }, 1L, TimeUnit.SECONDS);
        } else {
            System.err.println("服务端链接成功...");
        }
    }
}