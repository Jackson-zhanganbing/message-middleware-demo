package com.zab.message.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生产者
 *
 * @author zab
 * @date 2020-04-26 21:02
 */
@RestController
@RequestMapping("/activemq")
public class ActiveMqProducer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @PostMapping("send")
    @ResponseBody
    public String testProduce(String message,String topic,String queue){

        ActiveMQTopic destination = new ActiveMQTopic(topic);
        jmsMessagingTemplate.convertAndSend(destination,message);

        ActiveMQQueue des = new ActiveMQQueue(queue);
        jmsMessagingTemplate.convertAndSend(des,queue);

        return "OK";

    }
}
