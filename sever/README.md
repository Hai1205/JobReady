# 🚀 Spring Microservices Project

Dự án mẫu Spring Boot Microservice với kiến trúc hoàn chỉnh bao gồm Service Discovery, API Gateway, Authentication Service, User Service và Message Broker.

## 🏗️ Kiến trúc tổng thể

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Eureka Server │    │  Gateway Service│    │   Auth Service  │
│     :8761       │◄──►│     :8080       │◄──►│     :8082       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                        │
                                │                        │
                                ▼                        ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │   User Service  │    │    RabbitMQ     │
                       │     :8083       │◄──►│     :5672       │
                       └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │     MySQL       │
                       │     :3306       │
                       └─────────────────┘
```

## 🧩 Các thành phần

### 1. **Eureka Server** (Port: 8761)

- Service Discovery và Registry
- Quản lý đăng ký và khám phá các microservices

### 2. **Gateway Service** (Port: 8080)

- API Gateway với Spring Cloud Gateway
- JWT Authentication Filter
- Route tới các service backend
- AuthController để xử lý login

### 3. **Auth Service** (Port: 8082)

- Sinh JWT token bằng RSA private key
- Publish login events qua RabbitMQ
- Xác thực và validate token

### 4. **User Service** (Port: 8083)

- CRUD operations cho User entity
- Kết nối MySQL database
- Listen RabbitMQ events
- Verify JWT bằng RSA public key

### 5. **MySQL Database** (Port: 3306)

- Lưu trữ thông tin user
- Database: `jobready`

### 6. **RabbitMQ** (Port: 5672, Management: 15672)

- Message broker cho async communication
- Exchange: `user.exchange`
- Queue: `user.login.queue`

## 🔐 Bảo mật

### JWT Token Authentication

- **JWT Token**: Sử dụng RSA 2048-bit key pair
- **Private Key**: Lưu trong biến môi trường, chỉ Auth Service có quyền truy cập
- **Public Key**: Lưu trong biến môi trường, chia sẻ cho các service khác để verify

### OAuth2 Social Login

- **Supported Providers**: Google, Facebook, GitHub
- **Authorization Flow**: OAuth2 Authorization Code Grant
- **User Integration**: Tự động tạo hoặc cập nhật user sau OAuth2 login
- **JWT Generation**: OAuth2 login cũng tạo JWT token để sử dụng tiếp trong hệ thống

### Authentication Flows

#### 1. Traditional Login Flow:

1. Client gửi login request đến Gateway
2. Gateway validate credentials với User Service
3. Gateway request token từ Auth Service
4. Auth Service publish login event qua RabbitMQ
5. User Service nhận và log login event

#### 2. OAuth2 Login Flow:

1. Client redirect đến `/oauth2/authorize/{provider}` (Google/Facebook/GitHub)
2. User xác thực với OAuth2 provider
3. Provider callback về `/oauth2/callback/{provider}`
4. Auth Service lấy user info từ provider
5. Auth Service tạo/cập nhật user trong database
6. Auth Service tạo JWT token
7. Redirect client với JWT token

## 🚀 Cách chạy

### Prerequisites

- Docker và Docker Compose
- Java 21
- Maven 3.6+

### 1. Chuẩn bị OAuth2 Provider Apps

Trước khi chạy hệ thống với OAuth2, bạn cần đăng ký ứng dụng với các OAuth2 providers. Xem hướng dẫn chi tiết trong [`docs/OAUTH2_SETUP_GUIDE.md`](docs/OAUTH2_SETUP_GUIDE.md).

### 2. Cấu hình OAuth2 Credentials

Tạo file `auth-service/src/main/resources/application-oauth2.properties`:

```properties
# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
spring.security.oauth2.client.registration.google.scope=profile,email

# Facebook OAuth2
spring.security.oauth2.client.registration.facebook.client-id=your-facebook-app-id
spring.security.oauth2.client.registration.facebook.client-secret=your-facebook-app-secret
spring.security.oauth2.client.registration.facebook.scope=email,public_profile

# GitHub OAuth2
spring.security.oauth2.client.registration.github.client-id=your-github-client-id
spring.security.oauth2.client.registration.github.client-secret=your-github-client-secret
spring.security.oauth2.client.registration.github.scope=user:email
```

### 3. Create Key

```bash
node -e "const { generateKeyPairSync } = require('crypto');
const { privateKey, publicKey } = generateKeyPairSync('rsa', {
  modulusLength: 2048,
  publicKeyEncoding: { type: 'pkcs1', format: 'pem' },
  privateKeyEncoding: { type: 'pkcs1', format: 'pem' },
});
console.log('PRIVATE KEY:\\n', privateKey);
console.log('PUBLIC KEY:\\n', publicKey);"
```

### 4. Build project

```bash
mvn clean package -DskipTests
```

### 5. Install package

```bash
# Discovery Service
mvn clean install -pl discovery-service
```

```bash
# Gateway Service
mvn clean install -pl gateway-service
```

```bash
# User Service
mvn clean install -pl user-service
```

```bash
# CV Service
mvn clean install -pl cv-service
```

```bash
# Auth Service
mvn clean install -pl auth-service
```

### 6. Chạy các service (mở terminal riêng cho mỗi service):

```bash
# Gen Key
javac KeyGenerator.java && java KeyGenerator

# RabiitMQ - MySQL
docker-compose up -d

# Discovery Service
mvn spring-boot:run -pl discovery-service

# Gateway Service
mvn spring-boot:run -pl gateway-service

# User Service
mvn spring-boot:run -pl user-service

# CV Service
mvn spring-boot:run -pl cv-service

# Auth Service
mvn spring-boot:run -pl auth-service
# mvn spring-boot:run -pl auth-service -Dspring-boot.run.profiles=oauth2
```

### 7. Kiểm tra services

- Eureka Dashboard: http://localhost:8761
- Gateway Health: http://localhost:8080/actuator/health
- RabbitMQ Management: http://localhost:15672 (guest/guest)

### 8. Test OAuth2 Login

#### Google Login:

```
http://localhost:8080/oauth2/authorize/google
```

#### Facebook Login:

```
http://localhost:8080/oauth2/authorize/facebook
```

#### GitHub Login:

```
http://localhost:8080/oauth2/authorize/github
```

**Kết quả**: Sau khi đăng nhập thành công, user sẽ được redirect với JWT token và thông tin user được lưu trong database.

## 🔧 Cấu hình Environment

### Development

- Tất cả services chạy trên localhost
- MySQL: root/password
- RabbitMQ: guest/guest

### Production

Cập nhật các environment variables trong `.env` file và `docker-compose.yml`:

- Database credentials
- RabbitMQ credentials
- JWT RSA keys (định dạng PEM bao gồm header)
  - `JWT_PRIVATE_KEY` - Private key cho Auth Service
  - `JWT_PUBLIC_KEY` - Public key cho tất cả services

## 📝 API Endpoints

### Gateway Service (Port: 8080)

#### Traditional Authentication:

- `POST /gateway/auth/login` - Đăng nhập bằng username/password
- `POST /gateway/auth/validate` - Validate JWT token
- `GET /gateway/auth/health` - Health check

#### OAuth2 Authentication:

- `GET /oauth2/authorize/{provider}` - Khởi tạo OAuth2 login (provider: google, facebook, github)
- `GET /oauth2/callback/{provider}` - OAuth2 callback endpoint (được provider gọi tự động)

### User Service (qua Gateway)

- `GET /users` - Lấy danh sách users
- `POST /users` - Tạo user mới
- `GET /users/{id}` - Lấy user theo ID
- `PUT /users/{id}` - Cập nhật user
- `DELETE /users/{id}` - Xóa user
- `POST /users/authenticate` - Xác thực user bằng username/password

#### OAuth2 User Endpoints:

- `POST /users/oauth2` - Tạo hoặc cập nhật OAuth2 user
- `GET /users/oauth2/{provider}/{providerId}` - Tìm user bằng OAuth2 provider info

### Auth Service (qua Gateway)

- `POST /auth/login` - Đăng nhập traditional
- `POST /auth/validate` - Validate JWT token

## 🧪 Testing

### Traditional Authentication Test

```bash
# 1. Tạo user
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullname": "Test User"
  }'

# 2. Login traditional
curl -X POST http://localhost:8080/gateway/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 3. Sử dụng JWT token nhận được để gọi protected endpoints
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### OAuth2 Authentication Test

1. **Google Login**: Truy cập `http://localhost:8080/oauth2/authorize/google` trên browser
2. **Facebook Login**: Truy cập `http://localhost:8080/oauth2/authorize/facebook` trên browser
3. **GitHub Login**: Truy cập `http://localhost:8080/oauth2/authorize/github` trên browser

Sau khi đăng nhập thành công, check database để thấy user mới được tạo với thông tin OAuth2.

### Postman Collection

Import file `api-test.postman_collection.json` vào Postman để test toàn bộ flow.

## 🔄 RabbitMQ Events

### Login Events

Khi user login thành công (cả traditional và OAuth2):

1. Auth Service publish `UserLoginEvent` vào exchange `user.exchange`
2. User Service listen queue `user.login.queue`
3. Event chứa thông tin: username, timestamp, loginType (TRADITIONAL/OAUTH2)

### Event Schema

```json
{
  "username": "user@example.com",
  "timestamp": "2024-01-01T12:00:00Z",
  "loginType": "OAUTH2",
  "provider": "google" // chỉ có khi loginType = OAUTH2
}
```

## 📊 Monitoring

- **Actuator Endpoints**: `/actuator/health`, `/actuator/metrics`
- **Eureka Dashboard**: Xem trạng thái các services
- **RabbitMQ Management**: Monitor queues và messages

## 🔧 Thêm Service mới

1. Tạo Maven module mới trong parent POM
2. Thêm Eureka Client dependency
3. Cấu hình `application.properties`
4. Tạo Dockerfile
5. Thêm service vào `docker-compose.yml`
6. Cập nhật Gateway routes nếu cần

## 🚨 Troubleshooting

### Service không register với Eureka

- Kiểm tra network connectivity
- Verify Eureka URL trong config

### JWT validation failed

- Kiểm tra public/private key paths
- Verify token format (Bearer prefix)

### OAuth2 Login Issues

- **"Invalid redirect_uri"**: Kiểm tra redirect URI trong OAuth2 provider console phải khớp với callback URL
- **"Invalid client_id/secret"**: Verify credentials trong `application-oauth2.properties`
- **"Scope not granted"**: Đảm bảo scope được khai báo chính xác trong provider app
- **User info extraction failed**: Check provider response format và mapping trong `OAuth2LoginService`

### RabbitMQ connection failed

- Kiểm tra RabbitMQ service status
- Verify credentials và host config

### Database connection issues

- Kiểm tra MySQL service status
- Verify database URL và credentials
- Đảm bảo database `jobready` đã được tạo
- Check User table có các cột OAuth2 fields: `oauth_provider`, `oauth_provider_id`, `avatar_url`, `is_oauth_user`

## 📚 Tech Stack

- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Security 6+** (với OAuth2 Client support)
- **Java 21**
- **MySQL 8.0**
- **RabbitMQ 3.11**
- **JWT (JJWT 0.12.3)**
- **Docker & Docker Compose**

## 🔍 Features

### ✅ Implemented

- [x] Service Discovery với Eureka
- [x] API Gateway với Spring Cloud Gateway
- [x] JWT Authentication với RSA key pair
- [x] User Management với MySQL
- [x] Async Messaging với RabbitMQ
- [x] **OAuth2 Social Login** (Google, Facebook, GitHub)
- [x] **Automatic User Registration** từ OAuth2 providers
- [x] **JWT Token Generation** cho OAuth2 users
- [x] **Inter-service Communication** cho OAuth2 user management

### 🔮 Planned

- [ ] Redis Cache Integration
- [ ] Email Notification Service
- [ ] File Upload Service
- [ ] Monitoring với Prometheus/Grafana
- [ ] API Rate Limiting
- [ ] OAuth2 Token Refresh

## 📖 Documentation

- [OAuth2 Setup Guide](docs/OAUTH2_SETUP_GUIDE.md) - Hướng dẫn chi tiết setup OAuth2 providers
- [API Documentation](docs/API_DOCUMENTATION.md) - Chi tiết về tất cả endpoints
- [Architecture Guide](docs/ARCHITECTURE.md) - Kiến trúc và design patterns được sử dụng

## 📄 License

This project is licensed under the MIT License.
