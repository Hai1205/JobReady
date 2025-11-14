# 🚀 JobReady Backend - Spring Boot Microservices

Hệ thống backend JobReady với kiến trúc microservices hoàn chỉnh, tích hợp AI-powered CV processing và authentication system.

## 🏗️ Kiến trúc tổng thể

![Structure](assets/images/structure.png)

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
- Hỗ trợ OAuth2 Social Login (Google, Facebook, GitHub)

### 4. **User Service** (Port: 8083)

- CRUD operations cho User entity
- Kết nối MySQL database
- Listen RabbitMQ events
- Verify JWT bằng RSA public key

### 5. **CV Service** (Port: 8084) ⭐ **NEW**

- **AI-Powered CV Processing**: Tích hợp OpenRouter API với Llama-3.2-3b-instruct model
- **File Import**: Hỗ trợ upload và parse PDF, DOCX, TXT files
- **CV Analyze**: Phân tích CV và đưa ra suggestions cải thiện
- **Job Description Matching**: So sánh CV với job description
- **Smart Improvements**: AI-generated suggestions cho từng section của CV
- **File Parsing**: Sử dụng Apache PDFBox và POI để extract text

### 6. **MySQL Database** (Port: 3306)

- Lưu trữ thông tin user và CV data

### 7. **RabbitMQ** (Port: 5672, Management: 15672)

- Message broker cho async communication

### 8. **OpenRouter AI** (External API)

- AI model: `meta-llama/llama-3.2-3b-instruct`
- Sử dụng cho CV analyze và improvement suggestions
- API Key required trong environment variables

## 🔐 Bảo mật & Authentication

### JWT Token Authentication

- **JWT Token**: Sử dụng RSA 2048-bit key pair
- **Private Key**: Lưu trong biến môi trường, chỉ Auth Service có quyền truy cập
- **Public Key**: Lưu trong biến môi trường, chia sẻ cho các service khác để verify

### OAuth2 Social Login

- **Supported Providers**: Google, Facebook, GitHub
- **Authorization Flow**: OAuth2 Authorization Code Grant
- **User Integration**: Tự động tạo hoặc cập nhật user sau OAuth2 login
- **JWT Generation**: OAuth2 login cũng tạo JWT token

## 🚀 Cách chạy

### Prerequisites

- Docker và Docker Compose
- Java 21
- Maven 3.6+
- OpenRouter API Key (cho CV Service)

### 1. Chuẩn bị Environment Variables

Tạo file `.env` trong thư mục `server`.

### 2. Chuẩn bị OAuth2 (Optional)

Xem hướng dẫn trong [`docs/OAUTH2_SETUP_GUIDE.md`](docs/OAUTH2_SETUP_GUIDE.md) để setup OAuth2 providers.

### 3. Build và Install

```bash
# Build tất cả modules
mvn clean package -DskipTests

# Hoặc build từng module
mvn clean install -pl rabbit-common
mvn clean install -pl grpc-common
mvn clean install -pl security-common
mvn clean install -pl redis-common
mvn clean install -pl discovery-service
mvn clean install -pl gateway-service
mvn clean install -pl user-service
mvn clean install -pl auth-service
mvn clean install -pl mail-service
mvn clean install -pl cv-service
mvn clean install -pl ai-service
mvn clean install -pl stats-service
```

### 4. Chạy Services

```bash
# 1. Tạo JWT Keys
cd config/keys && javac KeyGenerator.java && java KeyGenerator

# 2. Khởi động Infrastructure
docker-compose up -d

# 3. Chạy Services (mỗi service trong terminal riêng)
mvn spring-boot:run -pl discovery-service
mvn spring-boot:run -pl gateway-service
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl auth-service
mvn spring-boot:run -pl mail-service
mvn spring-boot:run -pl cv-service
mvn spring-boot:run -pl ai-service
mvn spring-boot:run -pl stats-service
```

### 5. Kiểm tra Services

- **Eureka Dashboard**: http://localhost:8761
- **Gateway Health**: http://localhost:8080/actuator/health
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **CV Service Health**: http://localhost:8084/actuator/health

## 📝 API Endpoints

### CV Service (Port: 8084, qua Gateway: /cv)

#### File Import & Analyze

- `POST /cv/import` - Upload và parse CV file (PDF/DOCX/TXT)
- `POST /cv/analyze` - Phân tích CV và đưa ra suggestions
- `POST /cv/analyze-with-jd` - Phân tích CV so với Job Description
- `POST /cv/improve` - Cải thiện CV dựa trên AI suggestions

#### CV Management

- `GET /cv` - Lấy danh sách CV của user
- `GET /cv/{id}` - Lấy CV theo ID
- `POST /cv` - Tạo CV mới
- `PUT /cv/{id}` - Cập nhật CV
- `DELETE /cv/{id}` - Xóa CV

### Authentication Endpoints

- `POST /gateway/auth/login` - Đăng nhập
- `POST /gateway/auth/validate` - Validate JWT token
- `GET /oauth2/authorize/{provider}` - OAuth2 login (google/facebook/github)

### User Management

- `GET /users` - Lấy danh sách users
- `POST /users` - Tạo user mới
- `GET /users/{id}` - Lấy user theo ID
- `PUT /users/{id}` - Cập nhật user
- `DELETE /users/{id}` - Xóa user

## 🔧 Cấu hình Environment

### Development

- Tất cả services chạy trên localhost
- MySQL: root/password
- RabbitMQ: guest/guest
- OpenRouter API: Cần API key

### Production

- Sử dụng environment variables
- Database credentials từ secrets
- JWT keys từ secure storage
- OpenRouter API key từ environment

## 📊 Monitoring & Health Checks

- **Actuator Endpoints**: `/actuator/health`, `/actuator/metrics`, `/actuator/info`
- **Eureka Dashboard**: Trạng thái các services
- **RabbitMQ Management**: Monitor queues và messages
- **CV Service Metrics**: AI API usage, file processing stats

## 🚨 Troubleshooting

### CV Service Issues

- **"OpenRouter API Error"**: Kiểm tra API key và network connectivity
- **"File parsing failed"**: Verify file format (PDF/DOCX/TXT only)
- **"AI model timeout"**: Check OpenRouter service status

### Common Issues

- **Service registration failed**: Kiểm tra Eureka connectivity
- **JWT validation failed**: Verify RSA key pair
- **Database connection**: Check MySQL service và credentials
- **OAuth2 redirect error**: Verify callback URLs trong provider console

## 🧪 Testing

### Chạy Tests

```bash
mvn test
```

### Test Coverage

- **UserServiceTest**: 42 test cases - CRUD operations, validation, edge cases
- **AuthServiceTest**: 35 unit tests + integration tests - JWT, OAuth2, authentication
- **OtpServiceTest**: 23 test cases - OTP generation, validation, Redis operations
- **MailServiceTest**: 12 test cases - Email sending, templates, error handling
- **AIServiceTest**: 20 test cases - AI processing, null/empty inputs, error scenarios

**Tổng cộng: 132 test cases** với STT numbering trong `TESTCASE.md`

### Test Scripts

- `run-tests.ps1`: PowerShell script để chạy tất cả tests (có option skip auth-service nếu cần)
- `run-tests.bat`: Batch script đơn giản cho Windows
- `TESTCASE.md`: Documentation đầy đủ tất cả test cases với STT numbering

### Troubleshooting Tests

- **Auth Service Integration Tests Fail**: Có thể bỏ qua bằng `.\run-tests.ps1 -SkipAuthService`
- **AI Service Tests Fail**: Kiểm tra Mockito configuration và null input handling
- **Database Tests Fail**: Đảm bảo MySQL container đang chạy

- **Spring Boot 3.2.0** - Framework chính
- **Spring Cloud 2023.0.0** - Microservices support
- **Spring Security 6+** - Authentication & OAuth2
- **Java 21** - Runtime
- **MySQL 8.0** - Database
- **RabbitMQ 3.11** - Message broker
- **Apache PDFBox 2.0.29** - PDF parsing
- **Apache POI 5.2.5** - DOCX parsing
- **OpenRouter AI** - AI processing
- **JWT (JJWT 0.12.3)** - Token management
- **Docker & Docker Compose** - Containerization

## 🔍 Key Features

### ✅ Implemented

- [x] Service Discovery với Eureka
- [x] API Gateway với JWT authentication
- [x] User Management với MySQL
- [x] OAuth2 Social Login (Google, Facebook, GitHub)
- [x] Async Messaging với RabbitMQ
- [x] **AI-Powered CV Processing** ⭐
- [x] **Multi-format File Import** (PDF, DOCX, TXT)
- [x] **CV Analyze & Improvement**
- [x] **Job Description Matching**
- [x] **Smart AI Suggestions**

### 🔮 Future Enhancements

- [ ] Redis caching cho AI responses
- [ ] Batch CV processing
- [ ] Advanced AI models integration
- [ ] CV template generation
- [ ] Interview preparation features

## 📖 Documentation

- [CV Service API Guide](CV_AI_FEATURES_README.md) - Chi tiết AI features
- [OAuth2 Setup Guide](docs/OAUTH2_SETUP_GUIDE.md) - Setup OAuth2 providers
- [Architecture Guide](docs/ARCHITECTURE.md) - System architecture
- [API Documentation](docs/API_DOCUMENTATION.md) - Complete API reference

## 📄 License

This project is licensed under the MIT License.
