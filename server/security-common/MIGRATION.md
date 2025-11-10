# Migration Guide to Security Common

Hướng dẫn migrate các service để sử dụng module `security-common`.

## Bước 1: Cập nhật pom.xml

### Xóa các dependency cũ:

```xml
<!-- XÓA CÁC DEPENDENCY NÀY -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
</dependency>
```

### Thêm dependency mới:

```xml
<!-- Security Common Module -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>security-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Bước 2: Cập nhật SecurityConfig

### Thay đổi imports:

```java
// TRƯỚC
import com.example.[service-name].securities.JwtAuthenticationFilter;
import com.example.[service-name].securities.JsonAccessDeniedHandler;
import com.example.[service-name].securities.JsonAuthenticationEntryPoint;

// SAU
import com.example.securitycommon.filter.JwtAuthenticationFilter;
import com.example.securitycommon.handler.JsonAccessDeniedHandler;
import com.example.securitycommon.handler.JsonAuthenticationEntryPoint;
```

### Xóa PasswordEncoder bean (nếu có):

```java
// XÓA BEAN NÀY (đã có trong SecurityCommonAutoConfiguration)
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## Bước 3: Xóa các class security cũ

Xóa toàn bộ package `securities` hoặc `securitys` trong service:

- `JwtTokenProvider.java`
- `JwtAuthenticationFilter.java`
- `JwtValidationException.java`
- `JsonAuthenticationEntryPoint.java`
- `JsonAccessDeniedHandler.java`
- `AuthenticatedUser.java`
- `SecurityUtils.java` (nếu có)

## Bước 4: Cập nhật code sử dụng

### Thay đổi imports trong Controllers và Services:

```java
// TRƯỚC
import com.example.[service-name].securities.AuthenticatedUser;
import com.example.[service-name].securities.SecurityUtils;

// SAU
import com.example.securitycommon.model.AuthenticatedUser;
import com.example.securitycommon.util.SecurityUtils;
```

## Bước 5: Build và test

```bash
# Build service
mvn clean install -DskipTests

# Hoặc build toàn bộ project
cd server
mvn clean install -DskipTests
```

## Services đã migrate:

- ✅ user-service
- ✅ cv-service
- ✅ auth-service
- ✅ ai-service

## Services cần migrate:

- ⏳ mail-service (nếu có security)
- ⏳ chat-service (nếu có security)

## Lợi ích sau khi migrate:

1. **Giảm duplicate code**: Không cần maintain security code ở nhiều service
2. **Dễ cập nhật**: Cập nhật security logic một chỗ, tất cả services được cập nhật
3. **Consistency**: Đảm bảo tất cả services sử dụng cùng một logic security
4. **Dễ test**: Security logic được test tập trung
5. **Clean architecture**: Tách biệt concerns rõ ràng

## Troubleshooting

### Lỗi: Cannot resolve com.example.securitycommon

**Giải pháp**: Build lại security-common module

```bash
mvn clean install -pl security-common -DskipTests
```

### Lỗi: Bean của security-common không được inject

**Giải pháp**: Kiểm tra `@ComponentScan` trong main application class, đảm bảo spring.factories được cấu hình đúng.

### Lỗi: JWT_PUBLIC_KEY not found

**Giải pháp**: Đảm bảo JWT_PUBLIC_KEY được cấu hình trong application.properties/yml của service.
