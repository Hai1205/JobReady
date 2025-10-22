package com.example.cvservice.services.producers;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.cvservice.dtos.UserDto;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dtos.RabbitHeader;
import com.example.rabbitmq.services.RabbitRPCService;

@Service
@RequiredArgsConstructor
public class UserProducer {
        private final RabbitRPCService rpcService;

        public UserDto findUserById(UUID userId) {
                RabbitHeader header = RabbitHeader.builder()
                                .correlationId(UUID.randomUUID().toString())
                                .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
                                .replyExchange(RabbitConstants.AUTH_EXCHANGE)
                                .timestamp(System.currentTimeMillis())
                                .sourceService("auth-service")
                                .targetService("user-service")
                                .build();

                Map<String, Object> params = new HashMap<>();
                params.put("userId", userId);

                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_FIND_BY_ID,
                                header,
                                params,
                                UserDto.class);

                System.out.println("Received RabbitMQ response for user: "
                                + (userDto != null ? userDto.toString() : "null"));

                return userDto;
        }
}
