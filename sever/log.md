//user service
2025-10-24T23:03:25.664+07:00 DEBUG 6844 --- [user-service] [pool-2-thread-4] o.s.a.r.listener.BlockingQueueConsumer   : Storing delivery for consumerTag: 'amq.ctag-nO51c1rrK0aDegY2XHSF2t0YeA3FGA9m' with deliveryTag: '1' in Consumer@1b250e51: tags=[[amq.ctag-nO51c1rrK0aDegY2XHSF2t0YeA3FGA9m]], channel=Cached Rabbit Channel: AMQChannel(amqp://hbbdqfqz@3.114.210.215:5671/hbbdqfqz,19), conn: Proxy@78b5c29f Shared Rabbit Connection: SimpleConnection@24df8abf [delegate=amqp://hbbdqfqz@3.114.210.215:5671/hbbdqfqz, localPort=50880], acknowledgeMode=AUTO local queue size=0
2025-10-24T23:03:25.725+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] o.s.a.r.listener.BlockingQueueConsumer   : Received message: (Body:'[B@67c0d18d(byte[341])' MessageProperties [headers={__TypeId__=java.lang.String}, correlationId=1, replyTo=amq.direct.reply-to.amq.ctag-Xs7HUlLqhMeFJAW2rH7cOEZ8OMmTH1GH, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=user.exchange, receivedRoutingKey=user.authenticate, deliveryTag=1, consumerTag=amq.ctag-nO51c1rrK0aDegY2XHSF2t0YeA3FGA9m, consumerQueue=user.authenticate.queue])
2025-10-24T23:03:25.760+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] .a.r.l.a.MessagingMessageListenerAdapter : Processing [GenericMessage [payload={"payload":{"email":"hainguyenhoang1205@gmail.com","currentPassword":"1"},"header":{"correlationId":"858376d2-6819-466f-b9b7-556f03766190","replyTo":"auth.reply.queue","replyExchange":"auth.exchange","sourceService":"auth-service","targetService":"user-service","status":null,"timestamp":1761321801745}}, headers={amqp_receivedDeliveryMode=PERSISTENT, amqp_receivedExchange=user.exchange, amqp_deliveryTag=1, amqp_replyTo=amq.direct.reply-to.amq.ctag-Xs7HUlLqhMeFJAW2rH7cOEZ8OMmTH1GH, amqp_consumerQueue=user.authenticate.queue, amqp_redelivered=false, amqp_receivedRoutingKey=user.authenticate, amqp_contentEncoding=UTF-8, amqp_correlationId=1, id=236c5af5-b496-8831-118f-b0ab17ace24d, amqp_consumerTag=amq.ctag-nO51c1rrK0aDegY2XHSF2t0YeA3FGA9m, amqp_lastInBatch=false, contentType=application/json, __TypeId__=java.lang.String, timestamp=1761321805758}]]
2025-10-24T23:03:25.792+07:00  INFO 6844 --- [user-service] [ntContainer#5-1] c.e.u.services.consumers.UserConsumer    : ≡ƒô¿ [Authenticate] Received request - correlationId: 858376d2-6819-466f-b9b7-556f03766190
2025-10-24T23:03:25.809+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] c.e.u.services.consumers.UserConsumer    : ≡ƒöÉ [Authenticate] Authenticating user - email: hainguyenhoang1205@gmail.com
2025-10-24T23:03:25.809+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] c.e.u.services.apis.UserService          : Authenticating user: hainguyenhoang1205@gmail.com
Hibernate:
    select
        u1_0.id,
        u1_0.avatar_public_id,
        u1_0.avatar_url,
        u1_0.email,
        u1_0.fullname,
        u1_0.isoauth_user,
        u1_0.oauth_provider,
        u1_0.oauth_provider_id,
        u1_0.password,
        u1_0.role,
        u1_0.status,
        u1_0.username
    from
        users u1_0
    where
        u1_0.email=?
2025-10-24T23:03:26.160+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] c.e.u.services.apis.UserService          : User authenticated successfully: hainguyenhoang1205@gmail.com      
2025-10-24T23:03:26.374+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] o.s.a.r.c.CachingConnectionFactory       : Creating cached Rabbit Channel from AMQChannel(amqp://hbbdqfqz@3.114.210.215:5671/hbbdqfqz,34)
2025-10-24T23:03:26.376+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] o.s.amqp.rabbit.core.RabbitTemplate      : Executing callback RabbitTemplate$$Lambda/0x0000018abfd03f10 on RabbitMQ Channel: Cached Rabbit Channel: AMQChannel(amqp://hbbdqfqz@3.114.210.215:5671/hbbdqfqz,34), conn: Proxy@78b5c29f Shared Rabbit Connection: SimpleConnection@24df8abf [delegate=amqp://hbbdqfqz@3.114.210.215:5671/hbbdqfqz, localPort=50880]
2025-10-24T23:03:26.377+07:00 DEBUG 6844 --- [user-service] [ntContainer#5-1] o.s.amqp.rabbit.core.RabbitTemplate      : Publishing message [(Body:'[B@303934(byte[490])' MessageProperties [headers={}, correlationId=1, contentType=application/json, contentLength=0, deliveryMode=PERSISTENT, priority=0, deliveryTag=0])] on exchange [], routingKey = [amq.direct.reply-to.amq.ctag-Xs7HUlLqhMeFJAW2rH7cOEZ8OMmTH1GH]
2025-10-24T23:03:26.397+07:00  INFO 6844 --- [user-service] [ntContainer#5-1] c.e.u.services.consumers.UserConsumer    : Γ£à [Authenticate] Success - email: hainguyenhoang1205@gmail.com, userId: 6c0c264f-c636-4444-9a6f-966ddd8c52ad

//auth service
at com.example.authservice.services.apis.AuthService.login(AuthService.java:51) ~[classes/:na]
        at com.example.authservice.controllers.AuthController.login(AuthController.java:25) ~[classes/:na]
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
        at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
        at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:352) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:713) ~[spring-aop-6.1.1.jar:6.1.1]
        at com.example.authservice.controllers.AuthController$$SpringCGLIB$$0.login(<generated>) ~[classes/:na]
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
        at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
        at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:254) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:182) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118) ~[spring-webmvc-6.1.1.jar:6.1.1]     
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:917) ~[spring-webmvc-6.1.1.jar:6.1.1]   
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:829) ~[spring-webmvc-6.1.1.jar:6.1.1]        
        at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590) ~[tomcat-embed-core-10.1.16.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.16.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:205) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:110) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.security.web.FilterChainProxy.lambda$doFilterInternal$3(FilterChainProxy.java:231) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$FilterObservation$SimpleFilterObservation.lambda$wrap$1(ObservationFilterChainDecorator.java:479) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$AroundFilterObservation$SimpleAroundFilterObservation.lambda$wrap$1(ObservationFilterChainDecorator.java:340) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator.lambda$wrapSecured$0(ObservationFilterChainDecorator.java:82) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:128) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.access.intercept.AuthorizationFilter.doFilter(AuthorizationFilter.java:100) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:126) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:120) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:131) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:85) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:100) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:179) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at com.example.authservice.securitys.JwtAuthenticationFilter.doFilterInternal(JwtAuthenticationFilter.java:49) ~[classes/:na]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:221) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.doFilterInternal(OAuth2AuthorizationRequestRedirectFilter.java:181) ~[spring-security-oauth2-client-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:107) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:93) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:90) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:75) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:82) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:69) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:62) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.session.DisableEncodeUrlFilter.doFilterInternal(DisableEncodeUrlFilter.java:42) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$AroundFilterObservation$SimpleAroundFilterObservation.lambda$wrap$0(ObservationFilterChainDecorator.java:323) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:224) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:233) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:191) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:352) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:268) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:109) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:482) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:340) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:391) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:896) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1744) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.lang.RuntimeException: RPC call failed
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$1(RabbitRPCService.java:150) ~[rabbit-common-1.0.0.jar:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1768) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1760) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387) ~[na:na] 
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1312) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1843) ~[na:na]  
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1808) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:188) ~[na:na]
Caused by: com.fasterxml.jackson.core.JsonParseException: Unexpected character ('h' (code 104)): was expecting double-quote to start field name
 at [Source: (String)"{header={correlationId=1, replyTo=null, replyExchange=null, sourceService=null, targetService=null, status=SUCCESS, timestamp=1761321806163}, payload={code=200, message=Success, data={id=6c0c264f-c636-4444-9a6f-966ddd8c52ad, username=rootadmin, password=null, email=hainguyenhoang1205@gmail.com, fullname=Administrator, status=active, role=admin, oauthProvider=null, oauthProviderId=null, avatarUrl=null, avatarPublicId=null, oauthUser=false}}}"; line: 1, column: 3]
        at com.fasterxml.jackson.core.JsonParser._constructError(JsonParser.java:2477) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.base.ParserMinimalBase._reportError(ParserMinimalBase.java:750) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.base.ParserMinimalBase._reportUnexpectedChar(ParserMinimalBase.java:674) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._handleOddName(ReaderBasedJsonParser.java:1940) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser.nextFieldName(ReaderBasedJsonParser.java:968) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer._readAndBindStringKeyMap(MapDeserializer.java:596) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:449) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:32) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.readRootValue(DefaultDeserializationContext.java:323) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4825) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3772) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3740) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$1(RabbitRPCService.java:130) ~[rabbit-common-1.0.0.jar:na]
        ... 7 common frames omitted

2025-10-24T23:03:26.650+07:00 ERROR 8732 --- [auth-service] [nio-8082-exec-1] c.e.a.services.apis.AuthService          : Login failed with unexpected error for email hainguyenhoang1205@gmail.com

java.lang.RuntimeException: RPC call failed: java.lang.RuntimeException: RPC call failed      
        at com.example.rabbitmq.services.RabbitRPCService.sendAndReceive(RabbitRPCService.java:181) ~[rabbit-common-1.0.0.jar:na]
        at com.example.authservice.services.producers.UserProducer.authenticateUser(UserProducer.java:148) ~[classes/:na]
        at com.example.authservice.services.apis.AuthService.login(AuthService.java:51) ~[classes/:na]
        at com.example.authservice.controllers.AuthController.login(AuthController.java:25) ~[classes/:na]
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
        at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
        at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:352) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:713) ~[spring-aop-6.1.1.jar:6.1.1]
        at com.example.authservice.controllers.AuthController$$SpringCGLIB$$0.login(<generated>) ~[classes/:na]
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
        at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
        at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:254) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:182) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118) ~[spring-webmvc-6.1.1.jar:6.1.1]     
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:917) ~[spring-webmvc-6.1.1.jar:6.1.1]   
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:829) ~[spring-webmvc-6.1.1.jar:6.1.1]        
        at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590) ~[tomcat-embed-core-10.1.16.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.1.jar:6.1.1]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.16.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:205) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:110) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.security.web.FilterChainProxy.lambda$doFilterInternal$3(FilterChainProxy.java:231) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$FilterObservation$SimpleFilterObservation.lambda$wrap$1(ObservationFilterChainDecorator.java:479) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$AroundFilterObservation$SimpleAroundFilterObservation.lambda$wrap$1(ObservationFilterChainDecorator.java:340) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator.lambda$wrapSecured$0(ObservationFilterChainDecorator.java:82) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:128) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.access.intercept.AuthorizationFilter.doFilter(AuthorizationFilter.java:100) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:126) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:120) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:131) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:85) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:100) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:179) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at com.example.authservice.securitys.JwtAuthenticationFilter.doFilterInternal(JwtAuthenticationFilter.java:49) ~[classes/:na]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:221) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.doFilterInternal(OAuth2AuthorizationRequestRedirectFilter.java:181) ~[spring-security-oauth2-client-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:107) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:93) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:90) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:75) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:82) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:69) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:62) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.session.DisableEncodeUrlFilter.doFilterInternal(DisableEncodeUrlFilter.java:42) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]   
        at org.springframework.security.web.ObservationFilterChainDecorator$AroundFilterObservation$SimpleAroundFilterObservation.lambda$wrap$0(ObservationFilterChainDecorator.java:323) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:224) ~[spring-security-web-6.2.0.jar:6.2.0]     
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]    
        at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:233) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:191) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:352) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:268) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:109) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:482) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:340) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:391) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:896) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1744) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) ~[tomcat-embed-core-10.1.16.jar:10.1.16]
        at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.util.concurrent.ExecutionException: java.lang.RuntimeException: RPC call failed
        at java.base/java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:396) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture.get(CompletableFuture.java:2096) ~[na:na]
        at com.example.rabbitmq.services.RabbitRPCService.sendAndReceive(RabbitRPCService.java:175) ~[rabbit-common-1.0.0.jar:na]
        ... 142 common frames omitted
Caused by: java.lang.RuntimeException: RPC call failed
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$1(RabbitRPCService.java:150) ~[rabbit-common-1.0.0.jar:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1768) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1760) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387) ~[na:na] 
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1312) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1843) ~[na:na]  
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1808) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:188) ~[na:na]
Caused by: com.fasterxml.jackson.core.JsonParseException: Unexpected character ('h' (code 104)): was expecting double-quote to start field name
 at [Source: (String)"{header={correlationId=1, replyTo=null, replyExchange=null, sourceService=null, targetService=null, status=SUCCESS, timestamp=1761321806163}, payload={code=200, message=Success, data={id=6c0c264f-c636-4444-9a6f-966ddd8c52ad, username=rootadmin, password=null, email=hainguyenhoang1205@gmail.com, fullname=Administrator, status=active, role=admin, oauthProvider=null, oauthProviderId=null, avatarUrl=null, avatarPublicId=null, oauthUser=false}}}"; line: 1, column: 3]
        at com.fasterxml.jackson.core.JsonParser._constructError(JsonParser.java:2477) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.base.ParserMinimalBase._reportError(ParserMinimalBase.java:750) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.base.ParserMinimalBase._reportUnexpectedChar(ParserMinimalBase.java:674) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._handleOddName(ReaderBasedJsonParser.java:1940) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser.nextFieldName(ReaderBasedJsonParser.java:968) ~[jackson-core-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer._readAndBindStringKeyMap(MapDeserializer.java:596) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:449) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:32) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.readRootValue(DefaultDeserializationContext.java:323) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4825) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3772) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3740) ~[jackson-databind-2.15.3.jar:2.15.3]
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$1(RabbitRPCService.java:130) ~[rabbit-common-1.0.0.jar:na]
        ... 7 common frames omitted

java.lang.RuntimeException: RPC call failed: java.lang.RuntimeException: RPC call failed
        at com.example.rabbitmq.services.RabbitRPCService.sendAndReceive(RabbitRPCService.java:181)
        at com.example.authservice.services.producers.UserProducer.authenticateUser(UserProducer.java:148)
        at com.example.authservice.services.apis.AuthService.login(AuthService.java:51)       
        at com.example.authservice.controllers.AuthController.login(AuthController.java:25)   
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
        at java.base/java.lang.reflect.Method.invoke(Method.java:580)
        at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:352)
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:713)
        at com.example.authservice.controllers.AuthController$$SpringCGLIB$$0.login(<generated>)
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
        at java.base/java.lang.reflect.Method.invoke(Method.java:580)
        at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:254)
        at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:182)
        at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:917)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:829)
        at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)
        at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914) 
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:205)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:110)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.springframework.security.web.FilterChainProxy.lambda$doFilterInternal$3(FilterChainProxy.java:231)
        at org.springframework.security.web.ObservationFilterChainDecorator$FilterObservation$SimpleFilterObservation.lambda$wrap$1(ObservationFilterChainDecorator.java:479)
        at org.springframework.security.web.ObservationFilterChainDecorator$AroundFilterObservation$SimpleAroundFilterObservation.lambda$wrap$1(ObservationFilterChainDecorator.java:340)   
        at org.springframework.security.web.ObservationFilterChainDecorator.lambda$wrapSecured$0(ObservationFilterChainDecorator.java:82)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:128)
        at org.springframework.security.web.access.intercept.AuthorizationFilter.doFilter(AuthorizationFilter.java:100)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:126)
        at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:120)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:131)
        at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:85)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:100)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:179)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at com.example.authservice.securitys.JwtAuthenticationFilter.doFilterInternal(JwtAuthenticationFilter.java:49)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:227)
        at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:221)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.doFilterInternal(OAuth2AuthorizationRequestRedirectFilter.java:181)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:107)
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:93)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:90)
        at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:75)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:82)
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:69)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:62)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.session.DisableEncodeUrlFilter.doFilterInternal(DisableEncodeUrlFilter.java:42)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$AroundFilterObservation$SimpleAroundFilterObservation.lambda$wrap$0(ObservationFilterChainDecorator.java:323)   
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:224)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:233)
        at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:191)
        at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:352)
        at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:268)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:109)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) 
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:482)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115)
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)       
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)   
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:340)        
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:391)
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)   
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:896)
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1744)
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)    
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191)
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) 
        at java.base/java.lang.Thread.run(Thread.java:1583)
Caused by: java.util.concurrent.ExecutionException: java.lang.RuntimeException: RPC call failed
        at java.base/java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:396)
        at java.base/java.util.concurrent.CompletableFuture.get(CompletableFuture.java:2096)  
        at com.example.rabbitmq.services.RabbitRPCService.sendAndReceive(RabbitRPCService.java:175)
        ... 142 more
Caused by: java.lang.RuntimeException: RPC call failed
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$1(RabbitRPCService.java:150)
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1768)
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1760)
        at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1312)
        at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1843)
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1808)      
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:188)
Caused by: com.fasterxml.jackson.core.JsonParseException: Unexpected character ('h' (code 104)): was expecting double-quote to start field name
 at [Source: (String)"{header={correlationId=1, replyTo=null, replyExchange=null, sourceService=null, targetService=null, status=SUCCESS, timestamp=1761321806163}, payload={code=200, message=Success, data={id=6c0c264f-c636-4444-9a6f-966ddd8c52ad, username=rootadmin, password=null, email=hainguyenhoang1205@gmail.com, fullname=Administrator, status=active, role=admin, oauthProvider=null, oauthProviderId=null, avatarUrl=null, avatarPublicId=null, oauthUser=false}}}"; line: 1, column: 3]
        at com.fasterxml.jackson.core.JsonParser._constructError(JsonParser.java:2477)        
        at com.fasterxml.jackson.core.base.ParserMinimalBase._reportError(ParserMinimalBase.java:750)
        at com.fasterxml.jackson.core.base.ParserMinimalBase._reportUnexpectedChar(ParserMinimalBase.java:674)
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._handleOddName(ReaderBasedJsonParser.java:1940)
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser.nextFieldName(ReaderBasedJsonParser.java:968)
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer._readAndBindStringKeyMap(MapDeserializer.java:596)
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:449)
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:32)
        at com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.readRootValue(DefaultDeserializationContext.java:323)
        at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4825)
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3772)      
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3740)      
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$1(RabbitRPCService.java:130)
        ... 7 more