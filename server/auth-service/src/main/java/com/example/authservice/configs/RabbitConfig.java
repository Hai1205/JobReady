package com.example.authservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.authservice.dtos.responses.RPCResponse;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();

        // Map user-service RPCResponse to auth-service RPCResponse
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.example.authservice.dtos.responses.RPCResponse", RPCResponse.class);
        classMapper.setIdClassMapping(idClassMapping);

        // Trust all packages (allows dynamic deserialization)
        classMapper.setTrustedPackages("*");

        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setReplyTimeout(60000);
        return template;
    }
}