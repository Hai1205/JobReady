# Security Common Module

Module chung chứa các thành phần security được sử dụng bởi tất cả các microservices trong hệ thống JobReady.

## Mục đích

Tránh duplicate code về security configuration, JWT handling, và authentication logic giữa các microservices.

## Các thành phần chính

### 1. JWT Components

- **JwtTokenProvider**: Xác thực và parse JWT tokens
- **JwtAuthenticationFilter**: Filter để xác thực requests với JWT
- **JwtValidationException**: Custom exception cho JWT validation errors

### 2. Security Handlers

- **JsonAuthenticationEntryPoint**: Xử lý authentication errors (401)
- **JsonAccessDeniedHandler**: Xử lý access denied errors (403)

### 3. Models

- **AuthenticatedUser**: Model đại diện cho user đã authenticated, implements UserDetails

### 4. Utilities

- **SecurityUtils**: Utility methods để lấy thông tin user hiện tại từ SecurityContext

## Cách sử dụng

### 1. Thêm dependency vào pom.xml của service

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>security-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Tạo SecurityConfig trong service

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            JsonAuthenticationEntryPoint authenticationEntryPoint,
            JsonAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 3. Sử dụng SecurityUtils để lấy thông tin user

```java
@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        UUID userId = SecurityUtils.getCurrentUserId();
        String email = SecurityUtils.getCurrentUserEmail();
        String role = SecurityUtils.getCurrentUserRole();

        // Use the information...
        return ResponseEntity.ok(data);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<?> adminEndpoint() {
        return ResponseEntity.ok("Admin content");
    }
}
```

## Yêu cầu

- JWT_PUBLIC_KEY phải được cấu hình trong application.properties/yml của mỗi service
- Spring Boot 3.2.0+
- Java 21+

## Auto-configuration

Module này sử dụng Spring Boot auto-configuration. Tất cả các beans sẽ được tự động scan và đăng ký khi thêm dependency.
