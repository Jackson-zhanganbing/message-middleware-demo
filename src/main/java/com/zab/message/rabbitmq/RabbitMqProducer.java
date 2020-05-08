package com.zab.message.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * rabbitmq
 *
 * @author zab
 * @date 2020-05-08 22:19
 */
@RestController
@RequestMapping("/rabbitmq")
public class RabbitMqProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("send")
    @ResponseBody
    public String testProduce(String message, String exchange, String routingKey) {

        rabbitTemplate.convertAndSend(exchange, routingKey, message);

        return "OK";

    }
}
