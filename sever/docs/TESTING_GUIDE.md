# System Testing Guide

## 🧪 OAuth2 System Testing Guide

### Prerequisites

1. **Hoàn thành setup OAuth2 providers** theo [OAuth2 Setup Guide](OAUTH2_SETUP_GUIDE.md)
2. **Tất cả services đang chạy:**
   - Eureka Server (8761)
   - Gateway Service (8080)
   - Auth Service (8082)
   - User Service (8083)
   - MySQL (3306)
   - RabbitMQ (5672)

---

## 🔧 Manual Testing

### 1. Test OAuth2 Google Login

1. **Mở browser và truy cập:**

   ```
   http://localhost:8080/oauth2/authorize/google
   ```

2. **Kiểm tra redirect:**

   - Browser should redirect to Google login page
   - URL should contain your Google Client ID
   - Check for proper scope (profile, email)

3. **Đăng nhập Google account**

   - Enter valid Google credentials
   - Allow permissions when prompted

4. **Verify callback:**

   - Should redirect to: `http://localhost:3000/login-success?token=...`
   - JWT token should be present in URL
   - Token format: `eyJhbGciOiJSUzI1NiJ9...`

5. **Check database:**
   ```sql
   SELECT * FROM users WHERE oauth_provider = 'google';
   ```
   Should show new user with Google info.

---

### 2. Test OAuth2 Facebook Login

1. **Truy cập Facebook OAuth2:**

   ```
   http://localhost:8080/oauth2/authorize/facebook
   ```

2. **Verify Facebook redirect and login**

3. **Check callback và token generation**

4. **Verify database entry:**
   ```sql
   SELECT * FROM users WHERE oauth_provider = 'facebook';
   ```

---

### 3. Test OAuth2 GitHub Login

1. **Truy cập GitHub OAuth2:**

   ```
   http://localhost:8080/oauth2/authorize/github
   ```

2. **Complete GitHub OAuth2 flow**

3. **Verify results in database:**
   ```sql
   SELECT * FROM users WHERE oauth_provider = 'github';
   ```

---

### 4. Test JWT Token Usage

1. **Copy JWT token từ login success URL**

2. **Test protected endpoint:**

   ```bash
   curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
        http://localhost:8080/users
   ```

3. **Expected response:**
   ```json
   [
     {
       "id": 1,
       "email": "user@gmail.com",
       "name": "User Name",
       "oauthProvider": "google",
       "isOAuthUser": true
     }
   ]
   ```

---

### 5. Test Traditional Login

1. **Tạo traditional user:**

   ```bash
   curl -X POST http://localhost:8080/users \
        -H "Content-Type: application/json" \
        -d '{
          "username": "testuser",
          "email": "test@example.com",
          "password": "password123",
          "fullname": "Test User"
        }'
   ```

2. **Login với traditional method:**

   ```bash
   curl -X POST http://localhost:8080/gateway/auth/login \
        -H "Content-Type: application/json" \
        -d '{
          "username": "testuser",
          "password": "password123"
        }'
   ```

3. **Verify JWT token response**

---

## 🤖 Automated Testing

### Run Unit Tests

```bash
# Test Auth Service OAuth2 logic
cd auth-service
mvn test -Dtest=OAuth2LoginServiceTest

# Test User Service OAuth2 endpoints
cd user-service
mvn test -Dtest=OAuth2UserControllerTest
```

### Run Integration Tests

```bash
# Test complete OAuth2 flow
cd auth-service
mvn test -Dtest=OAuth2IntegrationTest
```

---

## 🔍 Verification Checklist

### ✅ OAuth2 Provider Setup

- [ ] Google OAuth2 app configured với correct redirect URI
- [ ] Facebook app configured với correct redirect URI
- [ ] GitHub OAuth app configured với correct redirect URI
- [ ] All client IDs và secrets trong application-oauth2.properties

### ✅ Service Communication

- [ ] All services registered trong Eureka Dashboard
- [ ] Gateway routing OAuth2 requests đến auth-service
- [ ] Auth-service communicating với user-service
- [ ] RabbitMQ login events được published và consumed

### ✅ Database Integration

- [ ] User table có OAuth2 columns (oauth_provider, oauth_provider_id, avatar_url, is_oauth_user)
- [ ] OAuth2 users được tạo với correct provider info
- [ ] JWT tokens generated với real user IDs từ database

### ✅ Security

- [ ] JWT tokens có correct RSA signature
- [ ] OAuth2 state parameter được sử dụng cho CSRF protection
- [ ] Protected endpoints require valid JWT token
- [ ] Unauthorized requests return 401 status

### ✅ Error Handling

- [ ] Invalid provider returns 400 error
- [ ] Authentication failures redirect đến error URL
- [ ] Database errors handled gracefully
- [ ] Service communication failures handled

---

## 🐛 Common Issues & Solutions

### Issue: "Invalid redirect_uri" error

**Cause:** Redirect URI in OAuth2 provider console doesn't match callback URL

**Solution:**

1. Check OAuth2 provider console settings
2. Ensure redirect URI exactly matches: `http://localhost:8080/oauth2/callback/{provider}`
3. For production: Use HTTPS URLs

### Issue: "Client authentication failed" error

**Cause:** Incorrect client ID or secret

**Solution:**

1. Verify credentials trong `application-oauth2.properties`
2. Check for typos in client ID/secret
3. Ensure credentials match OAuth2 provider console

### Issue: JWT token verification failed

**Cause:** RSA key mismatch between auth-service và gateway

**Solution:**

1. Ensure same `public-key.pem` file trong both services
2. Check key file paths trong configuration
3. Verify key file permissions

### Issue: User not created in database

**Cause:** User-service communication failure

**Solution:**

1. Check user-service logs for errors
2. Verify database connection
3. Check RabbitMQ connectivity
4. Ensure OAuth2 DTO fields are correct

### Issue: RabbitMQ connection failed

**Cause:** RabbitMQ service not running hoặc wrong credentials

**Solution:**

1. Start RabbitMQ: `docker-compose up rabbitmq`
2. Check RabbitMQ management UI: http://localhost:15672
3. Verify credentials (guest/guest cho development)

---

## 📊 Monitoring & Logs

### Check Service Health

```bash
# Eureka Dashboard
http://localhost:8761

# Gateway Health
http://localhost:8080/actuator/health

# Auth Service Health
http://localhost:8082/actuator/health

# User Service Health
http://localhost:8083/actuator/health

# RabbitMQ Management
http://localhost:15672 (guest/guest)
```

### Important Logs to Monitor

**Auth Service:**

- OAuth2 authentication success/failure
- JWT token generation
- Provider user data extraction
- RabbitMQ message publishing

**User Service:**

- OAuth2 user creation/update
- Database operations
- RabbitMQ message consumption

**Gateway Service:**

- Request routing
- JWT token validation
- CORS handling

---

## 🚀 Performance Testing

### Load Testing OAuth2 Login

```bash
# Install Apache Bench
apt-get install apache2-utils

# Test OAuth2 callback processing (simulation)
ab -n 100 -c 10 -H "Authorization: Bearer valid_jwt_token" \
   http://localhost:8080/users
```

### Database Performance

```sql
-- Check OAuth2 user query performance
EXPLAIN SELECT * FROM users WHERE oauth_provider = 'google' AND oauth_provider_id = '123456789';

-- Add index if needed
CREATE INDEX idx_oauth_provider ON users(oauth_provider, oauth_provider_id);
```

---

## 🔄 Continuous Testing

### CI/CD Pipeline Tests

```yaml
# Example GitHub Actions workflow
test-oauth2:
  runs-on: ubuntu-latest
  services:
    mysql:
      image: mysql:8.0
      env:
        MYSQL_ROOT_PASSWORD: password
        MYSQL_DATABASE: jobready
    rabbitmq:
      image: rabbitmq:3.11-management

  steps:
    - name: Run OAuth2 Unit Tests
      run: mvn test -Dtest=OAuth2*Test

    - name: Run Integration Tests
      run: mvn test -Dtest=*IntegrationTest
```

### Smoke Tests

Create simple smoke tests để verify basic OAuth2 functionality after deployment:

```bash
#!/bin/bash
# smoke-test.sh

echo "Testing OAuth2 endpoints..."

# Test Google authorize endpoint
curl -s -o /dev/null -w "%{http_code}" \
  http://localhost:8080/oauth2/authorize/google | grep -q "302" && \
  echo "✅ Google OAuth2 authorize working" || \
  echo "❌ Google OAuth2 authorize failed"

# Test protected endpoint với invalid token
curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer invalid_token" \
  http://localhost:8080/users | grep -q "401" && \
  echo "✅ JWT validation working" || \
  echo "❌ JWT validation failed"

echo "Smoke tests completed"
```

---

## 📝 Test Documentation

Document tất cả test cases và expected behaviors để ensure consistent testing across team members và deployments.
