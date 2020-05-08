package com.zab.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class AcitveMqConsumer {

    /**
     * 用这个注解去监听 监听的队列
     */
    @JmsListener(destination = "myqueue")
    public void receiver(String msg) {
        System.out.println("消费者成功获取到生产者的点对点消息，msg" + msg);
    }

    @JmsListener(destination = "mytopic0")
    public void processMessage1(String content) {
        System.out.println(content);
    }

    @JmsListener(destination = "mytopic1")
    public void processMessage2(String content) {
        System.out.println(content);
    }

    @JmsListener(destination = "mytopic2")
    public void processMessage3(String content) {
        System.out.println(content);
    }
}
