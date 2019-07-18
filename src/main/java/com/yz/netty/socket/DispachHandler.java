package com.yz.netty.socket;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * @author yz
 * @date 2019/07/18
 */
public class DispachHandler extends SimpleChannelInboundHandler<String> {

    private static final ByteBuf HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(JSON.toJSONString(new SocketProtocol(UUID.randomUUID().toString().replace("-", ""), 0, "ping")), CharsetUtil.UTF_8));

    /**
     * 消息处理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server received: " + msg);
        SocketProtocol protocol = JSON.parseObject(msg, SocketProtocol.class);
        NettySocketHolder.put(protocol.getSerialUID(), (NioSocketChannel) ctx.channel());
        switch (protocol.getCode()) {
            // 地磅数据
            case 204:
                NettySocketHolder.sentMsg(protocol);
                break;
            //  心跳
            case 0:
                ctx.writeAndFlush(HEART_BEAT);
                break;
            default:
                break;
        }
    }

    /**
     * 每一次数据读取完成之后处理
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(ChannelFutureListener.CLOSE);
    }

    /**
     * 异常处理 包括链接断开
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 链接关闭
     * 取消绑定
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    /**
     * 链接开启
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(HEART_BEAT);
        super.channelActive(ctx);
    }

    /**
     * 心跳处理
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                //向客户端发送消息
                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }


}
