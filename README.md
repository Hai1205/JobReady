# 🚀 JobReady - AI-Powered CV Builder Platform

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

JobReady là một nền tảng xây dựng CV thông minh với tích hợp trí tuệ nhân tạo, giúp người dùng tạo và tối ưu hóa CV chuyên nghiệp một cách dễ dàng thông qua giao diện web hiện đại và công nghệ AI tiên tiến.

## ✨ Tổng quan

JobReady kết hợp sức mạnh của **AI** và **microservices architecture** để cung cấp giải pháp toàn diện cho việc tạo và cải thiện CV. Hệ thống bao gồm:

- **AI-Powered CV Analyze**: Phân tích CV thông minh và đưa ra suggestions cải thiện
- **Job Matching**: So sánh CV với job description để tối ưu hóa cơ hội ứng tuyển
- **Modern Web Interface**: Giao diện người dùng đẹp với Next.js và React
- **Secure Authentication**: JWT authentication với OAuth2 social login
- **Scalable Backend**: Microservices architecture với Spring Boot

## 🏗️ Kiến trúc hệ thống

```
Frontend (Next.js) ─── API Gateway ─── Microservices Backend
       │                      │
       └────────────► Auth Service
                             │
                             ├── User Service
                             ├── CV Service (AI)
                             └── Message Broker
```

**Backend Services**: Auth, User, CV, Gateway, Discovery (Eureka)
**Frontend**: Next.js với TypeScript, Tailwind CSS, shadcn/ui
**Infrastructure**: MySQL, RabbitMQ, Docker

## 🚀 Quick Start

### Yêu cầu hệ thống

- Java 21, Node.js 18+, Docker, Maven 3.6+
- OpenRouter API Key (cho AI features)

### 1. Clone & Setup

```bash
git clone https://github.com/Hai1205/JobReady.git
cd JobReady
```

### 2. Khởi động Infrastructure

```bash
cd sever && docker-compose up -d
```

### 3. Setup Backend

```bash
# Tạo JWT keys
cd sever/config/keys && javac KeyGenerator.java && java KeyGenerator

# Build & run services
mvn clean package -DskipTests
mvn spring-boot:run -pl discovery-service &
mvn spring-boot:run -pl gateway-service &
mvn spring-boot:run -pl user-service &
mvn spring-boot:run -pl cv-service &
mvn spring-boot:run -pl auth-service &
```

### 4. Setup Frontend

```bash
cd client && npm install && npm run dev
```

### 5. Truy cập ứng dụng

- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761

## 🎯 Tính năng chính

### 🤖 AI-Powered Features

- **Smart CV Import**: Upload và tự động phân tích CV từ PDF, DOCX, TXT
- **AI Analyze**: Phân tích CV và đưa ra suggestions cải thiện chi tiết
- **Job Matching**: So sánh CV với job description để tối ưu hóa ứng tuyển
- **Intelligent Suggestions**: AI-generated recommendations cho từng phần của CV

### 🔐 Authentication & Security

- **JWT Authentication**: Bảo mật với JSON Web Tokens và Refresh Tokens
- **OAuth2 Integration**: Đăng nhập với Google, Facebook, GitHub
- **Role-based Access Control**: Phân quyền ADMIN và USER

### 🎨 Modern UI/UX

- **Responsive Design**: Tương thích với mọi thiết bị
- **Real-time Updates**: Cập nhật UI theo thời gian thực
- **Dark/Light Mode**: Chế độ sáng/tối
- **Intuitive Navigation**: Điều hướng dễ dàng

## 📁 Cấu trúc Project

```
JobReady/
├── client/                 # Next.js Frontend Application
│   └── README.md          # Frontend documentation chi tiết
├── sever/                 # Spring Boot Backend Services
│   ├── auth-service/     # Authentication & JWT Management
│   ├── cv-service/       # AI-Powered CV Processing
│   ├── user-service/     # User Management Service
│   ├── gateway-service/  # API Gateway
│   ├── discovery-service/# Eureka Service Discovery
│   └── README.md         # Backend documentation chi tiết
├── docker-compose.yml    # Infrastructure Services
├── LICENSE               # MIT License
└── README.md            # File này
```

## 📚 Tech Stack

### Backend

- **Java 21** + **Spring Boot 3.2.0**
- **Spring Cloud** + **JWT** + **MySQL**
- **RabbitMQ** + **Docker**
- **Apache PDFBox** + **Apache POI**

### Frontend

- **Next.js 15** + **React 18** + **TypeScript**
- **Tailwind CSS** + **shadcn/ui**
- **Zustand** + **Axios**

### AI & Infrastructure

- **OpenRouter API** + **Llama-3.2-3b-instruct**
- **Docker Compose** + **Eureka** + **Spring Cloud Gateway**

## 📖 Documentation

Để biết chi tiết về cách setup, API endpoints, và sử dụng:

- **[📘 Backend Documentation](./sever/README.md)** - Hướng dẫn chi tiết về microservices, API, setup backend
- **[🎨 Frontend Documentation](./client/README.md)** - Hướng dẫn setup frontend, components, state management

## 🔧 Development

### Prerequisites

- Java 21 JDK
- Node.js 18+
- Maven 3.6+
- Docker & Docker Compose
- OpenRouter API Key

### Environment Setup

```bash
# Backend environment variables
cd sever
# Copy and configure .env file (see sever/README.md)

# Frontend environment variables
cd client
# Copy and configure .env.local file (see client/README.md)
```

### Running Tests

```bash
# Backend tests
cd sever && mvn test

# Frontend tests
cd client && npm run test
```

## 🚨 Troubleshooting

### Backend Issues

- **Service registration failed**: Kiểm tra Eureka connectivity
- **JWT validation failed**: Verify RSA key pair
- **Database connection**: Check MySQL service và credentials

### Frontend Issues

- **API connection failed**: Check backend server status
- **Authentication failed**: Verify JWT token validity
- **Build errors**: `npm install` hoặc check dependencies

### AI Features Issues

- **OpenRouter API Error**: Kiểm tra API key và network connectivity
- **File parsing failed**: Verify file format (PDF/DOCX/TXT only)

## 🤝 Contributing

Chúng tôi hoan nghênh mọi đóng góp! Vui lòng đọc documentation chi tiết trong từng thư mục trước khi contribute.

1. Fork repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

- **GitHub Issues**: [Report bugs & request features](https://github.com/Hai1205/JobReady/issues)
- **Documentation**:
  - [Backend Guide](./sever/README.md)
  - [Frontend Guide](./client/README.md)

---

**Made with ❤️ by JobReady Team**
