package com.yz.netty.socket;

import com.alibaba.fastjson.JSON;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * socket链接管理
 * @author yz
 * @date 2019/07/18
 */
public class NettySocketHolder {
    private static final Map<String, NioSocketChannel> MAP = new ConcurrentHashMap<>(16);

    public static void put(String id, NioSocketChannel socketChannel) {
        MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(String id) {
        return MAP.get(id);
    }

    public static Map<String, NioSocketChannel> getMAP() {
        return MAP;
    }

    public static void sentMsg(SocketProtocol protocol){
        MAP.forEach((key, value) -> value.writeAndFlush(JSON.toJSONString(protocol)));
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        MAP.entrySet().stream()
                .filter(entry -> entry.getValue() == nioSocketChannel)
                .forEach(entry -> MAP.remove(entry.getKey()));
    }

}
