package com.yz.netty.client;

import com.alibaba.fastjson.JSON;
import com.yz.netty.socket.SocketProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.UUID;


public class ClientStringHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf HEART_BEAT =  Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(JSON.toJSONString(new SocketProtocol(UUID.randomUUID().toString().replace("-", ""),0,"pong")), CharsetUtil.UTF_8));

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(new Date() + "client:" + msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("长期没收到服务器推送数据");
                //可以选择重新连接
                SocketUtils.reconnect(ctx);
            }
//            else if (event.state().equals(IdleState.WRITER_IDLE)) {
//                System.out.println("长期未向服务器发送数据");
//                //发送心跳包
//                ctx.writeAndFlush(HEART_BEAT);
//            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("掉线了...");
        //使用过程中断线重连
        SocketUtils.reconnect(ctx);
        super.channelInactive(ctx);
    }

}
