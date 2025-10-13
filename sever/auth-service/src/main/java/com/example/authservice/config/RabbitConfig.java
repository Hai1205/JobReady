package com.example.authservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String USER_EXCHANGE = "user.exchange";

    public static final String USER_LOGIN_QUEUE = "user.login.queue";
    public static final String USER_LOGIN_ROUTING_KEY = "user.login";

    public static final String USER_REGISTER_QUEUE = "user.register.queue";
    public static final String USER_REGISTER_ROUTING_KEY = "user.register";

    public static final String USER_AUTHENTICATE_QUEUE = "user.authenticate.queue";
    public static final String USER_AUTHENTICATE_ROUTING_KEY = "user.authenticate";
    public static final String USER_AUTHENTICATE_REPLY_QUEUE = "user.authenticate.reply.queue";

    // OAuth2 related queues and routing keys
    public static final String USER_OAUTH2_CHECK_QUEUE = "user.oauth2.check.queue";
    public static final String USER_OAUTH2_CHECK_ROUTING_KEY = "user.oauth2.check";
    public static final String USER_OAUTH2_CHECK_REPLY_QUEUE = "user.oauth2.check.reply.queue";

    public static final String USER_OAUTH2_CREATE_QUEUE = "user.oauth2.create.queue";
    public static final String USER_OAUTH2_CREATE_ROUTING_KEY = "user.oauth2.create";
    public static final String USER_OAUTH2_CREATE_REPLY_QUEUE = "user.oauth2.create.reply.queue";

    public static final String USER_OAUTH2_UPDATE_QUEUE = "user.oauth2.update.queue";
    public static final String USER_OAUTH2_UPDATE_ROUTING_KEY = "user.oauth2.update";
    public static final String USER_OAUTH2_UPDATE_REPLY_QUEUE = "user.oauth2.update.reply.queue";

    // Thêm các queue và routing key cho các chức năng mới
    public static final String USER_FIND_BY_EMAIL_QUEUE = "user.find.by.email.queue";
    public static final String USER_FIND_BY_EMAIL_ROUTING_KEY = "user.find.by.email";
    public static final String USER_FIND_BY_EMAIL_REPLY_QUEUE = "user.find.by.email.reply.queue";

    public static final String USER_RESET_PASSWORD_QUEUE = "user.reset.password.queue";
    public static final String USER_RESET_PASSWORD_ROUTING_KEY = "user.reset.password";
    public static final String USER_RESET_PASSWORD_REPLY_QUEUE = "user.reset.password.reply.queue";

    public static final String USER_CHANGE_PASSWORD_QUEUE = "user.change.password.queue";
    public static final String USER_CHANGE_PASSWORD_ROUTING_KEY = "user.change.password";
    public static final String USER_CHANGE_PASSWORD_REPLY_QUEUE = "user.change.password.reply.queue";

    public static final String USER_CHANGE_STATUS_QUEUE = "user.change.status.queue";
    public static final String USER_CHANGE_STATUS_ROUTING_KEY = "user.change.status";
    public static final String USER_CHANGE_STATUS_REPLY_QUEUE = "user.change.status.reply.queue";

    public static final String USER_FORGOT_PASSWORD_QUEUE = "user.forgot.password.queue";
    public static final String USER_FORGOT_PASSWORD_ROUTING_KEY = "user.forgot.password";
    public static final String USER_FORGOT_PASSWORD_REPLY_QUEUE = "user.forgot.password.reply.queue";

    // Process OAuth2 user beans
    public static final String USER_PROCESS_OAUTH2_QUEUE = "user.process.oauth2.queue";
    public static final String USER_PROCESS_OAUTH2_ROUTING_KEY = "user.process.oauth2";
    public static final String USER_PROCESS_OAUTH2_REPLY_QUEUE = "user.process.oauth2.reply.queue";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userLoginQueue() {
        return QueueBuilder.durable(USER_LOGIN_QUEUE).build();
    }

    @Bean
    public Queue userRegisterQueue() {
        return QueueBuilder.durable(USER_REGISTER_QUEUE).build();
    }

    @Bean
    public Binding userLoginBinding() {
        return BindingBuilder.bind(userLoginQueue())
                .to(userExchange())
                .with(USER_LOGIN_ROUTING_KEY);
    }

    @Bean
    public Binding userRegisterBinding() {
        return BindingBuilder.bind(userRegisterQueue())
                .to(userExchange())
                .with(USER_REGISTER_ROUTING_KEY);
    }

    @Bean
    public Queue userAuthenticateQueue() {
        return QueueBuilder.durable(USER_AUTHENTICATE_QUEUE).build();
    }

    @Bean
    public Queue userAuthenticateReplyQueue() {
        return QueueBuilder.durable(USER_AUTHENTICATE_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userAuthenticateBinding() {
        return BindingBuilder.bind(userAuthenticateQueue())
                .to(userExchange())
                .with(USER_AUTHENTICATE_ROUTING_KEY);
    }

    // OAuth2 check user beans
    @Bean
    public Queue userOAuth2CheckQueue() {
        return QueueBuilder.durable(USER_OAUTH2_CHECK_QUEUE).build();
    }

    @Bean
    public Queue userOAuth2CheckReplyQueue() {
        return QueueBuilder.durable(USER_OAUTH2_CHECK_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userOAuth2CheckBinding() {
        return BindingBuilder.bind(userOAuth2CheckQueue())
                .to(userExchange())
                .with(USER_OAUTH2_CHECK_ROUTING_KEY);
    }

    // OAuth2 create user beans
    @Bean
    public Queue userOAuth2CreateQueue() {
        return QueueBuilder.durable(USER_OAUTH2_CREATE_QUEUE).build();
    }

    @Bean
    public Queue userOAuth2CreateReplyQueue() {
        return QueueBuilder.durable(USER_OAUTH2_CREATE_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userOAuth2CreateBinding() {
        return BindingBuilder.bind(userOAuth2CreateQueue())
                .to(userExchange())
                .with(USER_OAUTH2_CREATE_ROUTING_KEY);
    }

    // OAuth2 update user beans
    @Bean
    public Queue userOAuth2UpdateQueue() {
        return QueueBuilder.durable(USER_OAUTH2_UPDATE_QUEUE).build();
    }

    @Bean
    public Queue userOAuth2UpdateReplyQueue() {
        return QueueBuilder.durable(USER_OAUTH2_UPDATE_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userOAuth2UpdateBinding() {
        return BindingBuilder.bind(userOAuth2UpdateQueue())
                .to(userExchange())
                .with(USER_OAUTH2_UPDATE_ROUTING_KEY);
    }

    // Find user by email beans
    @Bean
    public Queue userFindByEmailQueue() {
        return QueueBuilder.durable(USER_FIND_BY_EMAIL_QUEUE).build();
    }

    @Bean
    public Queue userFindByEmailReplyQueue() {
        return QueueBuilder.durable(USER_FIND_BY_EMAIL_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userFindByEmailBinding() {
        return BindingBuilder.bind(userFindByEmailQueue())
                .to(userExchange())
                .with(USER_FIND_BY_EMAIL_ROUTING_KEY);
    }

    // Reset password beans
    @Bean
    public Queue userResetPasswordQueue() {
        return QueueBuilder.durable(USER_RESET_PASSWORD_QUEUE).build();
    }

    @Bean
    public Queue userResetPasswordReplyQueue() {
        return QueueBuilder.durable(USER_RESET_PASSWORD_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userResetPasswordBinding() {
        return BindingBuilder.bind(userResetPasswordQueue())
                .to(userExchange())
                .with(USER_RESET_PASSWORD_ROUTING_KEY);
    }

    // Change password beans
    @Bean
    public Queue userChangePasswordQueue() {
        return QueueBuilder.durable(USER_CHANGE_PASSWORD_QUEUE).build();
    }

    @Bean
    public Queue userChangePasswordReplyQueue() {
        return QueueBuilder.durable(USER_CHANGE_PASSWORD_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userChangePasswordBinding() {
        return BindingBuilder.bind(userChangePasswordQueue())
                .to(userExchange())
                .with(USER_CHANGE_PASSWORD_ROUTING_KEY);
    }

    // Change user status beans
    @Bean
    public Queue userChangeStatusQueue() {
        return QueueBuilder.durable(USER_CHANGE_STATUS_QUEUE).build();
    }

    @Bean
    public Queue userChangeStatusReplyQueue() {
        return QueueBuilder.durable(USER_CHANGE_STATUS_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userChangeStatusBinding() {
        return BindingBuilder.bind(userChangeStatusQueue())
                .to(userExchange())
                .with(USER_CHANGE_STATUS_ROUTING_KEY);
    }

    // Forgot password beans
    @Bean
    public Queue userForgotPasswordQueue() {
        return QueueBuilder.durable(USER_FORGOT_PASSWORD_QUEUE).build();
    }

    @Bean
    public Queue userForgotPasswordReplyQueue() {
        return QueueBuilder.durable(USER_FORGOT_PASSWORD_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userForgotPasswordBinding() {
        return BindingBuilder.bind(userForgotPasswordQueue())
                .to(userExchange())
                .with(USER_FORGOT_PASSWORD_ROUTING_KEY);
    }

    // Process OAuth2 user beans
    @Bean
    public Queue userProcessOAuth2Queue() {
        return QueueBuilder.durable(USER_PROCESS_OAUTH2_QUEUE).build();
    }

    @Bean
    public Queue userProcessOAuth2ReplyQueue() {
        return QueueBuilder.durable(USER_PROCESS_OAUTH2_REPLY_QUEUE).build();
    }

    @Bean
    public Binding userProcessOAuth2Binding() {
        return BindingBuilder.bind(userProcessOAuth2Queue())
                .to(userExchange())
                .with(USER_PROCESS_OAUTH2_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}