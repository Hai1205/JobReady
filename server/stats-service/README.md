# Stats Service

Service thống kê cho JobReady platform, sử dụng gRPC để giao tiếp với User Service và CV Service.

## Tính năng

- Lấy thống kê tổng quan về users (tổng số, active, pending, banned)
- Lấy thống kê về CVs (tổng số, public, private)
- Thống kê users và CVs được tạo trong tháng
- Lấy danh sách hoạt động gần đây (user đăng ký, CV được tạo)

## Prerequisites

- Java 21
- Maven 3.6+
- MySQL database (cho user-service và cv-service)

## Configuration

Cấu hình trong `src/main/resources/application.properties`:

```properties
# Stats Service Configuration
server.port=8087

# Application name
spring.application.name=stats-service

# Eureka client configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true

# gRPC Client Configuration
grpc.client.user-service.address=static://localhost:9091
grpc.client.user-service.negotiationType=PLAINTEXT
grpc.client.cv-service.address=static://localhost:9093
grpc.client.cv-service.negotiationType=PLAINTEXT

# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

# Logging
logging.level.com.example.statsservice=DEBUG
logging.level.io.grpc=DEBUG
```

## Build

Từ thư mục root của project (server):

```bash
mvn clean install -DskipTests
```

Hoặc build riêng stats-service:

```bash
cd stats-service
mvn clean package
```

## Run

Trước khi chạy stats-service, đảm bảo các service sau đã chạy:

1. **Discovery Service** (Eureka) - port 8761
2. **User Service** (gRPC port 9091) - port 8083
3. **CV Service** (gRPC port 9093) - port 8084
4. **Gateway Service** - port 8080 (optional, nếu muốn truy cập qua gateway)

Chạy stats-service:

```bash
cd stats-service
mvn spring-boot:run
```

Hoặc:

```bash
java -jar target/stats-service-1.0.0.jar
```

## API Endpoints

### Get Dashboard Statistics

**Endpoint:** `GET /api/stats/dashboard`

**Response:**

```json
{
  "statusCode": 200,
  "message": "Dashboard statistics retrieved successfully",
  "data": {
    "totalUsers": 100,
    "activeUsers": 85,
    "pendingUsers": 10,
    "bannedUsers": 5,
    "usersCreatedThisMonth": 15,
    "totalCVs": 250,
    "publicCVs": 180,
    "privateCVs": 70,
    "cvsCreatedThisMonth": 30,
    "recentActivities": [
      {
        "id": "user-1",
        "type": "user_registered",
        "description": "New user registered: John Doe",
        "timestamp": "2024-01-15T10:30:00Z",
        "userId": "uuid-1"
      },
      {
        "id": "cv-1",
        "type": "cv_created",
        "description": "New CV created: Software Developer CV",
        "timestamp": "2024-01-15T10:25:00Z",
        "userId": "uuid-2"
      }
    ]
  }
}
```

### Health Check

**Endpoint:** `GET /api/stats/health`

**Response:**

```text
Stats Service is running
```

## Access via Gateway

Nếu truy cập qua Gateway Service (port 8080):

```bash
curl http://localhost:8080/api/stats/dashboard
```

## Docker

Build Docker image:

```bash
docker build -t stats-service:latest .
```

Run Docker container:

```bash
docker run -p 8087:8087 \
  -e EUREKA_SERVER_URL=http://discovery-service:8761/eureka/ \
  -e GRPC_CLIENT_USER_SERVICE_ADDRESS=static://user-service:9091 \
  -e GRPC_CLIENT_CV_SERVICE_ADDRESS=static://cv-service:9093 \
  stats-service:latest
```

## Troubleshooting

### gRPC Connection Issues

Nếu gặp lỗi kết nối gRPC:

1. Kiểm tra user-service và cv-service đã bật gRPC server chưa
2. Xác nhận ports đúng (9091 cho user-service, 9093 cho cv-service)
3. Check firewall settings

### Eureka Registration Issues

Nếu service không đăng ký được với Eureka:

1. Kiểm tra Eureka server đang chạy ở `http://localhost:8761`
2. Xem logs để tìm lỗi connection
3. Đảm bảo network configuration đúng

## Development

### Adding New Statistics

1. Thêm method mới vào proto file nếu cần (cv_service.proto, user_service.proto)
2. Implement method trong respective gRPC service
3. Update StatsService để gọi method mới
4. Update DashboardStatsDto nếu cần thêm field
5. Update StatsController nếu cần endpoint mới

## Testing

Run tests:

```bash
mvn test
```

## Monitoring

Stats service expose Actuator endpoints:

- Health: `http://localhost:8087/actuator/health`
- Info: `http://localhost:8087/actuator/info`
- Metrics: `http://localhost:8087/actuator/metrics`
