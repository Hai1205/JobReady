package com.example.rabbitmq.constants;

/**
 * Constants for RabbitMQ exchanges and queues
 * All services can refer to these constants to ensure consistency
 */
public class RabbitConstants {
    
    // --- Exchanges ---
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String CV_EXCHANGE = "cv.exchange";
    
    // --- Common reply queues ---
    public static final String AUTH_REPLY_QUEUE = "auth.reply.queue";
    public static final String USER_REPLY_QUEUE = "user.reply.queue";
    public static final String CV_REPLY_QUEUE = "cv.reply.queue";
    
    // --- Routing keys - User Service ---
    public static final String USER_FIND_BY_EMAIL = "user.find.by.email";
    public static final String USER_FIND_WITH_PARAMS = "user.find.with.params";
    
    // --- Routing keys - Auth Service ---
    public static final String AUTH_LOGIN = "auth.login";
    public static final String AUTH_VERIFY = "auth.verify";
    
    // --- Queue names - User Service ---
    public static final String USER_FIND_BY_EMAIL_QUEUE = "user.find.by.email.queue";
    public static final String USER_FIND_WITH_PARAMS_QUEUE = "user.find.with.params.queue";
    
    // --- Queue names - Auth Service ---
    public static final String AUTH_LOGIN_QUEUE = "auth.login.queue";
    public static final String AUTH_VERIFY_QUEUE = "auth.verify.queue";
}