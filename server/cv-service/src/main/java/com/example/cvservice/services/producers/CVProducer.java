package com.example.cvservice.services.producers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.cvservice.dtos.UserDto;
import com.example.rabbitcommon.dtos.RPCResponse;

@Component
public class CVProducer {
    private final RabbitTemplate rabbitTemplate;

    public CVProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RPCResponse<UserDto> findUserById(UUID userId) {
        String exchange = "user.profile.exchange";
        String routingKey = "user.profile.find-by-id.request";

        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);

        return (RPCResponse<UserDto>) rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
    }
}
