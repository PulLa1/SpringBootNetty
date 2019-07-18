package com.yz.netty.client;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
public class Client {



    public static void main(String[] args) throws InterruptedException {
//        String host = "192.168.8.100";
        Client client = new Client();
        client.send();

    }

    @Async("asyncServiceExecutor")
    public void send() throws InterruptedException {
        String host = "127.0.0.1";
        int port = 8081;
        ImConnection imConnection = new ImConnection();
        Channel channel = imConnection.connect(host, port);
//        for (int i = 0; i<100 ; i++) {
//            channel.writeAndFlush("{\"code\":204,\"serialUID\":\"test01\",\"data\":{\"weigh\":0.07}}");
//            Thread.sleep(5000);
//        }
    }
}
