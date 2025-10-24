package com.example.rabbitmq.constants;

/**
 * Constants for RabbitMQ exchanges and queues
 * All services can refer to these constants to ensure consistency
 */
public class RabbitConstants {

    // -- Services --
    public static final String USER_SERVICE = "user-service";
    public static final String AUTH_SERVICE = "auth-service";
    public static final String CV_SERVICE = "cv-service";
    
    // --- Exchanges ---
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String CV_EXCHANGE = "cv.exchange";
    
    // --- Common reply queues ---
    public static final String AUTH_REPLY_QUEUE = "auth.reply.queue";
    public static final String USER_REPLY_QUEUE = "user.reply.queue";
    public static final String CV_REPLY_QUEUE = "cv.reply.queue";
    
    // --- User Service ---
    // --- Routing keys - User Service ---
    public static final String USER_FIND_BY_EMAIL = "user.find.by.email";
    public static final String USER_FIND_BY_ID = "user.find.by.id";
    public static final String USER_CREATE = "user.create";
    public static final String USER_CHANGE_PASSWORD = "user.change.password";
    public static final String USER_AUTHENTICATE = "user.authenticate";
    public static final String USER_RESET_PASSWORD = "user.reset.password";
    public static final String USER_FORGOT_PASSWORD = "user.forgot.password";
    public static final String USER_ACTIVATE = "user.activate";
    // --- Queue names - User Service ---
    public static final String USER_FIND_BY_ID_QUEUE = "user.find.by.id.queue";
    public static final String USER_FIND_BY_EMAIL_QUEUE = "user.find.by.email.queue";
    public static final String USER_CREATE_QUEUE = "user.create.queue";
    public static final String USER_CHANGE_PASSWORD_QUEUE = "user.change.password.queue";
    public static final String USER_AUTHENTICATE_QUEUE = "user.authenticate.queue";
    public static final String USER_RESET_PASSWORD_QUEUE = "user.reset.password.queue";
    public static final String USER_FORGOT_PASSWORD_QUEUE = "user.forgot.password.queue";
    public static final String USER_ACTIVATE_QUEUE = "user.activate.queue";
}