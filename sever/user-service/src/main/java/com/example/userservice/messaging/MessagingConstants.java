package com.example.userservice.messaging;

/**
 * Constants for RabbitMQ messaging across services.
 * This keeps queue and exchange names centralized and consistent.
 */
public final class MessagingConstants {
    
    // Main exchange
    public static final String USER_EXCHANGE = "user.exchange";
    
    // User service command patterns
    public static final String ROUTING_PATTERN_USER_COMMAND = "user.command.%s";
    public static final String QUEUE_PATTERN_USER_COMMAND = "user.command.%s.queue";
    public static final String QUEUE_PATTERN_USER_REPLY = "user.command.%s.reply.queue";
    
    // Common commands
    public static final String COMMAND_FIND_USER_BY_EMAIL = "findUserByEmail";
    public static final String COMMAND_AUTHENTICATE = "authenticate";
    public static final String COMMAND_REGISTER = "register";
    public static final String COMMAND_LOGIN = "login";
    public static final String COMMAND_RESET_PASSWORD = "resetPassword";
    public static final String COMMAND_CHANGE_PASSWORD = "changePassword";
    public static final String COMMAND_CHANGE_STATUS = "changeStatus";
    public static final String COMMAND_FORGOT_PASSWORD = "forgotPassword";
    
    // OAuth2 commands
    public static final String COMMAND_OAUTH2_CHECK = "oauth2Check";
    public static final String COMMAND_OAUTH2_CREATE = "oauth2Create";
    public static final String COMMAND_OAUTH2_UPDATE = "oauth2Update";
    public static final String COMMAND_PROCESS_OAUTH2 = "processOAuth2";
    
    // Headers and properties
    public static final String HEADER_COMMAND = "x-command";
    public static final String HEADER_CORRELATION_ID = "x-correlation-id";
    
    private MessagingConstants() {
        // Prevent instantiation
    }
    
    /**
     * Helper method to build routing key for a command
     */
    public static String routingKeyFor(String command) {
        return String.format(ROUTING_PATTERN_USER_COMMAND, command);
    }
    
    /**
     * Helper method to build queue name for a command
     */
    public static String queueNameFor(String command) {
        return String.format(QUEUE_PATTERN_USER_COMMAND, command);
    }
    
    /**
     * Helper method to build reply queue name for a command
     */
    public static String replyQueueNameFor(String command) {
        return String.format(QUEUE_PATTERN_USER_REPLY, command);
    }
}