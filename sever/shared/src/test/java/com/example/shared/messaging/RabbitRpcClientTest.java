package com.example.shared.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RabbitRpcClientTest {

    private RabbitTemplate rabbitTemplate;
    private RabbitRpcClient rabbitRpcClient;
    private ConnectionFactory connectionFactory;
    private MessageConverter messageConverter;
    private TestRequest testRequest;
    private TestResponse testResponse;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        connectionFactory = mock(ConnectionFactory.class);
        messageConverter = mock(MessageConverter.class);

        when(rabbitTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(rabbitTemplate.getMessageConverter()).thenReturn(messageConverter);

        rabbitRpcClient = new RabbitRpcClient(rabbitTemplate, 1, 1);

        testRequest = new TestRequest("test-data");
        testResponse = new TestResponse("test-response");
    }

    @Test
    void sendAndReceive_shouldReturnResponse_whenSuccessful() throws Exception {
        // Simulate a successful response
        setupMockContainerAndResponse();

        // Call the method
        TestResponse response = rabbitRpcClient.sendAndReceive(
                "test-exchange",
                "test-routing-key",
                testRequest,
                "test-reply-queue",
                TestResponse.class
        );

        // Verify the response
        assertNotNull(response);
        assertEquals("test-response", response.getData());

        // Verify rabbitTemplate.convertAndSend was called with the right parameters
        ArgumentCaptor<MessagePostProcessor> mppCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(rabbitTemplate).convertAndSend(
                eq("test-exchange"),
                eq("test-routing-key"),
                eq(testRequest),
                mppCaptor.capture()
        );

        // Verify correlation ID was set
        MessageProperties props = new MessageProperties();
        mppCaptor.getValue().postProcessMessage(new Message(new byte[0], props));
        assertNotNull(props.getCorrelationId());
        assertEquals("test-reply-queue", props.getReplyTo());
    }

    @Test
    void sendAndReceive_shouldHandleTimeout() throws Exception {
        // Set up mocks but don't supply a response to simulate timeout
        SimpleMessageListenerContainer container = mock(SimpleMessageListenerContainer.class);
        doAnswer(invocation -> container).when(rabbitTemplate).getConnectionFactory();
        
        // Call the method and expect a timeout exception
        assertThrows(RabbitRpcException.class, () -> {
            rabbitRpcClient.sendAndReceive(
                    "test-exchange",
                    "test-routing-key",
                    testRequest,
                    "test-reply-queue",
                    TestResponse.class
            );
        });
        
        // Verify container is stopped even on timeout
        verify(container, timeout(2000)).stop();
    }

    // Helper to set up mocks for successful response
    private void setupMockContainerAndResponse() {
        // When the container is created, capture the message listener
        SimpleMessageListenerContainer container = mock(SimpleMessageListenerContainer.class);
        doNothing().when(container).setQueueNames(anyString());
        doNothing().when(container).setMessageListener(any());
        doNothing().when(container).start();
        doNothing().when(container).stop();

        when(rabbitTemplate.getConnectionFactory()).thenReturn(connectionFactory);

        // Capture the MessagePostProcessor to get the correlation ID
        ArgumentCaptor<MessagePostProcessor> mppCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        doAnswer(invocation -> {
            // Get the parameters from the convertAndSend call
            MessagePostProcessor mpp = invocation.getArgument(3);
            
            // Create a message to get the correlation ID
            MessageProperties props = new MessageProperties();
            Message msg = mpp.postProcessMessage(new Message(new byte[0], props));
            
            // Use the correlation ID to deliver a response
            CompletableFuture.runAsync(() -> {
                try {
                    // Create response message with the same correlation ID
                    MessageProperties responseProps = new MessageProperties();
                    responseProps.setCorrelationId(props.getCorrelationId());
                    Message responseMsg = new Message(new byte[0], responseProps);
                    
                    // Simulate deserialization
                    when(rabbitTemplate.getMessageConverter().fromMessage(any(Message.class))).thenReturn(testResponse);
                    
                    // Find the listener that was registered and invoke it
                    ArgumentCaptor<ChannelAwareMessageListener> listenerCaptor = ArgumentCaptor.forClass(ChannelAwareMessageListener.class);
                    verify(container).setMessageListener(listenerCaptor.capture());
                    
                    // Call the listener with the response message
                    listenerCaptor.getValue().onMessage(responseMsg, null);
                    
                } catch (Exception e) {
                    fail("Exception in async response thread: " + e.getMessage());
                }
            });
            
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(), any(MessagePostProcessor.class));

        when(connectionFactory.createConnection()).thenReturn(null);
    }

    // Sample request class for testing
    static class TestRequest implements CorrelationIdAware {
        private String data;
        private String correlationId;

        public TestRequest(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        @Override
        public String getCorrelationId() {
            return correlationId;
        }

        @Override
        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }
    }

    // Sample response class for testing
    static class TestResponse {
        private String data;

        public TestResponse() {
        }

        public TestResponse(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}