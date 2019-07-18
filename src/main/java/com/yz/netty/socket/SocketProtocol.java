package com.yz.netty.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * code socket 协议
 * @author yz
 */
@Data
@AllArgsConstructor
public class SocketProtocol {

    private String serialUID;
    private int code;
    private String data;

}
