// cv service
2025-10-29T12:28:45.765+07:00  INFO 4228 --- [cv-service] [           main] c.e.cvservice.CVServiceApplication       : Started CVServiceApplication in 16.422 seconds (process running for 17.044)      
2025-10-29T12:33:45.337+07:00  INFO 4228 --- [cv-service] [trap-executor-0] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
2025-10-29T12:38:45.382+07:00  INFO 4228 --- [cv-service] [trap-executor-0] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
2025-10-29T12:41:35.852+07:00  INFO 4228 --- [cv-service] [nio-8084-exec-1] o.apache.tomcat.util.http.parser.Cookie  : A cookie header was received [g_state={"i_l":0};] that contained an invalid cookie. That cookie will be ignored.
 Note: further occurrences of this error will be logged at DEBUG level.
2025-10-29T12:41:35.889+07:00  INFO 4228 --- [cv-service] [nio-8084-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-10-29T12:41:35.892+07:00  INFO 4228 --- [cv-service] [nio-8084-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-10-29T12:41:35.962+07:00  INFO 4228 --- [cv-service] [nio-8084-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 69 ms
2025-10-29T12:41:36.036+07:00  WARN 4228 --- [cv-service] [nio-8084-exec-1] o.s.w.s.h.HandlerMappingIntrospector     : Cache miss for REQUEST dispatch to '/cvs/users/4e938fee-cf19-4a6d-8cc0-5ce54edf42e4' (previous null). Performing CorsConfiguration lookup. This is logged once only at WARN level, and every time at TRACE.
2025-10-29T12:41:36.235+07:00  WARN 4228 --- [cv-service] [nio-8084-exec-1] org.apache.catalina.connector.Request    : Creating the temporary upload location [C:\Users\ASUS\AppData\Local\Temp\tomcat.8084.15627696262103451959\work\Tomcat\localhost\ROOT] as it is required by the servlet [dispatcherServlet]
2025-10-29T12:41:36.629+07:00  INFO 4228 --- [cv-service] [nio-8084-exec-1] c.e.cvservice.services.apis.CVService    : Received createCV request for userId=4e938fee-cf19-4a6d-8cc0-5ce54edf42e4        
2025-10-29T12:41:36.630+07:00  INFO 4228 --- [cv-service] [nio-8084-exec-1] c.e.cvservice.services.apis.CVService    : Creating CV for userId=4e938fee-cf19-4a6d-8cc0-5ce54edf42e4 title='Senior Software Developer CV' experiencesCount=1 educationsCount=1
2025-10-29T12:41:36.652+07:00  INFO 4228 --- [cv-service] [onPool-worker-1] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [fuji.lmq.cloudamqp.com:5671]
2025-10-29T12:41:44.275+07:00 ERROR 4228 --- [cv-service] [onPool-worker-1] c.e.rabbitmq.services.RabbitRPCService   : Γ¥î RPC call failed: java.util.concurrent.TimeoutException
2025-10-29T12:41:44.277+07:00 ERROR 4228 --- [cv-service] [nio-8084-exec-1] c.e.rabbitmq.services.RabbitRPCService   : Γ¥î RPC call failed: java.lang.RuntimeException: RPC call failed

java.util.concurrent.ExecutionException: java.lang.RuntimeException: RPC call failed
        at java.base/java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:396) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture.get(CompletableFuture.java:2096) ~[na:na]
        at com.example.rabbitmq.services.RabbitRPCService.sendAndReceive(RabbitRPCService.java:200) ~[rabbit-common-1.0.0.jar:na]
        at com.example.cvservice.services.producers.UserProducer.findUserById(UserProducer.java:31) ~[classes/:na]
        at com.example.cvservice.services.apis.CVService.handleCreateCV(CVService.java:93) ~[classes/:na]
        at com.example.cvservice.services.apis.CVService.createCV(CVService.java:163) ~[classes/:na]
        at com.example.cvservice.controllers.CVController.createCV(CVController.java:28) ~[classes/:na]
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
        at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
        at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:352) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:765) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor.invoke(AuthorizationManagerBeforeMethodInterceptor.java:198) ~[spring-security-core-6.2.0.jar:6.2.0]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:765) ~[spring-aop-6.1.1.jar:6.1.1]
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:717) ~[spring-aop-6.1.1.jar:6.1.1]
        at com.example.cvservice.controllers.CVController$$SpringCGLIB$$0.createCV(<generated>) ~[classes/:na]
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
        at com.example.cvservice.configs.JwtAuthenticationFilter.doFilterInternal(JwtAuthenticationFilter.java:91) ~[classes/:na]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:107) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:93) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137) ~[spring-security-web-6.2.0.jar:6.2.0]
        at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91) ~[spring-web-6.1.1.jar:6.1.1]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.1.jar:6.1.1]
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
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$0(RabbitRPCService.java:175) ~[rabbit-common-1.0.0.jar:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1768) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1760) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387) ~[na:na]       
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1312) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1843) ~[na:na]        
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1808) ~[na:na]   
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:188) ~[na:na]
Caused by: org.springframework.amqp.AmqpTimeoutException: java.util.concurrent.TimeoutException     
        at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException(RabbitExceptionTranslator.java:73) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:594) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection(CachingConnectionFactory.java:711) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils.createConnection(ConnectionFactoryUtils.java:257) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.doExecute(RabbitTemplate.java:2225) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2198) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2178) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.useDirectReplyTo(RabbitTemplate.java:1062) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.evaluateFastReplyTo(RabbitTemplate.java:1034) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.doSendAndReceive(RabbitTemplate.java:1895) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.sendAndReceive(RabbitTemplate.java:1643) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.core.RabbitTemplate.sendAndReceive(RabbitTemplate.java:1636) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$0(RabbitRPCService.java:121) ~[rabbit-common-1.0.0.jar:na]
        ... 7 common frames omitted
Caused by: java.util.concurrent.TimeoutException: null
        at com.rabbitmq.utility.BlockingCell.get(BlockingCell.java:77) ~[amqp-client-5.19.0.jar:5.19.0]
        at com.rabbitmq.utility.BlockingCell.uninterruptibleGet(BlockingCell.java:120) ~[amqp-client-5.19.0.jar:5.19.0]
        at com.rabbitmq.utility.BlockingValueOrException.uninterruptibleGetValue(BlockingValueOrException.java:36) ~[amqp-client-5.19.0.jar:5.19.0]
        at com.rabbitmq.client.impl.AMQChannel$BlockingRpcContinuation.getReply(AMQChannel.java:552) ~[amqp-client-5.19.0.jar:5.19.0]
        at com.rabbitmq.client.impl.AMQConnection.start(AMQConnection.java:336) ~[amqp-client-5.19.0.jar:5.19.0]
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1251) ~[amqp-client-5.19.0.jar:5.19.0]
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1198) ~[amqp-client-5.19.0.jar:5.19.0]
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:632) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:607) ~[spring-rabbit-3.1.0.jar:3.1.0]
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:557) ~[spring-rabbit-3.1.0.jar:3.1.0]
        ... 18 common frames omitted

java.lang.RuntimeException: RPC call failed: java.lang.RuntimeException: RPC call failed
        at com.example.rabbitmq.services.RabbitRPCService.sendAndReceive(RabbitRPCService.java:206) 
        at com.example.cvservice.services.producers.UserProducer.findUserById(UserProducer.java:31) 
        at com.example.cvservice.services.apis.CVService.handleCreateCV(CVService.java:93)
        at com.example.cvservice.services.apis.CVService.createCV(CVService.java:163)
        at com.example.cvservice.controllers.CVController.createCV(CVController.java:28)
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
        at java.base/java.lang.reflect.Method.invoke(Method.java:580)
        at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:352)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:765)
        at org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor.invoke(AuthorizationManagerBeforeMethodInterceptor.java:198)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:765)
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:717)
        at com.example.cvservice.controllers.CVController$$SpringCGLIB$$0.createCV(<generated>)     
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
        at com.example.cvservice.configs.JwtAuthenticationFilter.doFilterInternal(JwtAuthenticationFilter.java:91)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:107)
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:93)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.wrapFilter(ObservationFilterChainDecorator.java:240)
        at org.springframework.security.web.ObservationFilterChainDecorator$ObservationFilter.doFilter(ObservationFilterChainDecorator.java:227)
        at org.springframework.security.web.ObservationFilterChainDecorator$VirtualFilterChain.doFilter(ObservationFilterChainDecorator.java:137)
        at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
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
        at com.example.rabbitmq.services.RabbitRPCService.sendAndReceive(RabbitRPCService.java:200) 
        ... 144 more
Caused by: java.lang.RuntimeException: RPC call failed
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$0(RabbitRPCService.java:175)
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1768)
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1760)
        at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1312)
        at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1843)
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1808)
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:188)   
Caused by: org.springframework.amqp.AmqpTimeoutException: java.util.concurrent.TimeoutException     
        at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException(RabbitExceptionTranslator.java:73)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:594)
        at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection(CachingConnectionFactory.java:711)
        at org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils.createConnection(ConnectionFactoryUtils.java:257)
        at org.springframework.amqp.rabbit.core.RabbitTemplate.doExecute(RabbitTemplate.java:2225)  
        at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2198)    
        at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2178)    
        at org.springframework.amqp.rabbit.core.RabbitTemplate.useDirectReplyTo(RabbitTemplate.java:1062)
        at org.springframework.amqp.rabbit.core.RabbitTemplate.evaluateFastReplyTo(RabbitTemplate.java:1034)
        at org.springframework.amqp.rabbit.core.RabbitTemplate.doSendAndReceive(RabbitTemplate.java:1895)
        at org.springframework.amqp.rabbit.core.RabbitTemplate.sendAndReceive(RabbitTemplate.java:1643)
        at org.springframework.amqp.rabbit.core.RabbitTemplate.sendAndReceive(RabbitTemplate.java:1636)
        at com.example.rabbitmq.services.RabbitRPCService.lambda$sendAndReceiveAsync$0(RabbitRPCService.java:121)
        ... 7 more
Caused by: java.util.concurrent.TimeoutException
        at com.rabbitmq.utility.BlockingCell.get(BlockingCell.java:77)
        at com.rabbitmq.utility.BlockingCell.uninterruptibleGet(BlockingCell.java:120)
        at com.rabbitmq.utility.BlockingValueOrException.uninterruptibleGetValue(BlockingValueOrException.java:36)
        at com.rabbitmq.client.impl.AMQChannel$BlockingRpcContinuation.getReply(AMQChannel.java:552)
        at com.rabbitmq.client.impl.AMQConnection.start(AMQConnection.java:336)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1251)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1198)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:632)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:607)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:557)
        ... 18 more

        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1251)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1198)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:632)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:607)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:557)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1251)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1198)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:632)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConn        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1251)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1198)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(Abs        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1251)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1251)
        at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1198)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:632)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:607)
        at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:557)
        ... 18 more


//user service
2025-10-29T12:40:58.287+07:00  INFO 15248 --- [user-service] [ntContainer#9-1] c.e.u.services.consumers.UserConsumer    : ≡ƒô¿ [FindByEmail] Received request - correlationId: be65c365-6a9e-43cd-8e1b-19e690a58b0a
2025-10-29T12:40:58.345+07:00 DEBUG 15248 --- [user-service] [ntContainer#9-1] c.e.u.services.consumers.UserConsumer    : ≡ƒöì [FindByEmail] Looking up email: hainguyenhoang1205@gmail.com
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
2025-10-29T12:40:58.720+07:00 DEBUG 15248 --- [user-service] [ntContainer#9-1] o.s.amqp.rabbit.core.RabbitTemplate      : Executing callback RabbitTemplate$$Lambda/0x0000024e91d08930 on RabbitMQ Channel: Cached Rabbit Channel: AMQChannel(amqp://hbbdqfqz@3.114.210.215:5671/hbbdqfqz,34), conn: Proxy@4dc2e04e Shared Rabbit Connection: SimpleConnection@c2e9e33 [delegate=amqp://hbbdqfqz@3.114.210.215:5671/hbbdqfqz, localPort=58589]
2025-10-29T12:40:58.722+07:00 DEBUG 15248 --- [user-service] [ntContainer#9-1] o.s.amqp.rabbit.core.RabbitTemplate      : Publishing message [(Body:'[B@7ce2f60(byte[490])' MessageProperties [headers={}, correlationId=2, contentType=application/json, contentLength=0, deliveryMode=PERSISTENT, priority=0, deliveryTag=0])] on exchange [], routingKey = [amq.direct.reply-to.amq.ctag-cpkUoA7GXUrBDa0kKvMDBQiaSXr5C-nw]
2025-10-29T12:40:58.732+07:00  INFO 15248 --- [user-service] [ntContainer#9-1] c.e.u.services.consumers.UserConsumer    : Γ£à [FindByEmail] Success - email: hainguyenhoang1205@gmail.com, userId: 4e938fee-cf19-4a6d-8cc0-5ce54edf42e4