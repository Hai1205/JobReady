package com.example.authservice.service.producers;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.authservice.dto.UserDto;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dto.RabbitHeader;
import com.example.rabbitmq.service.RabbitRPCService;

@Service
@RequiredArgsConstructor
public class UserProducer {
    private final RabbitRPCService rpcService;

    public UserDto findUserByEmail(String email) {
        RabbitHeader header = RabbitHeader.builder()
                .correlationId(UUID.randomUUID().toString())
                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                .timestamp(System.currentTimeMillis())
                .sourceService("auth-service")
                .targetService("user-service")
                .build();

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);

        return rpcService.sendAndReceive(
                RabbitConstants.USER_EXCHANGE,
                RabbitConstants.USER_FIND_BY_EMAIL,
                header,
                params,
                UserDto.class);
    }
}
