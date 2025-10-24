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
                RabbitHeader header = rpcService.generateHeader(
                                RabbitConstants.AUTH_REPLY_QUEUE,
                                RabbitConstants.AUTH_EXCHANGE,
                                RabbitConstants.AUTH_SERVICE,
                                RabbitConstants.USER_SERVICE);

                Map<String, Object> params = new HashMap<>();
                params.put("userId", userId);

                UserDto userDto = rpcService.sendAndReceive(
                                RabbitConstants.USER_EXCHANGE,
                                RabbitConstants.USER_FIND_BY_ID,
                                header,
                                params,
                                UserDto.class);

                return userDto;
        }
}
