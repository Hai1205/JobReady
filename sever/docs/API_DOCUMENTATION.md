# OAuth2 API Documentation

## 📋 Tổng quan

Hệ thống OAuth2 hỗ trợ đăng nhập với 3 providers: **Google**, **Facebook**, và **GitHub**. Sau khi đăng nhập thành công, hệ thống tự động tạo/cập nhật user trong database và trả về JWT token để sử dụng cho các API calls tiếp theo.

---

## 🔐 OAuth2 Authentication Endpoints

### 1. Khởi tạo OAuth2 Login

#### **GET** `/oauth2/authorize/{provider}`

Redirect user đến OAuth2 provider để xác thực.

**Parameters:**

- `provider` _(path)_ - Provider name: `google`, `facebook`, hoặc `github`

**Example Requests:**

```bash
# Google Login
GET http://localhost:8080/oauth2/authorize/google

# Facebook Login
GET http://localhost:8080/oauth2/authorize/facebook

# GitHub Login
GET http://localhost:8080/oauth2/authorize/github
```

**Response:**

- `302 Redirect` đến OAuth2 provider authorization URL
- Browser sẽ được redirect đến provider (Google/Facebook/GitHub)

**Error Responses:**

```json
// 400 Bad Request - Unsupported provider
{
  "error": "Unsupported OAuth2 provider: {provider}"
}
```

---

### 2. OAuth2 Callback Handler

#### **GET** `/oauth2/callback/{provider}`

Endpoint được gọi bởi OAuth2 provider sau khi user xác thực thành công.

**Parameters:**

- `provider` _(path)_ - Provider name: `google`, `facebook`, hoặc `github`
- `code` _(query)_ - Authorization code từ provider (automatic)
- `state` _(query)_ - CSRF protection state (automatic)

**Success Response:**

```
302 Redirect to: http://localhost:3000/login-success?token={jwt_token}
```

**Error Responses:**

```
302 Redirect to: http://localhost:3000/login-error?error={error_type}
```

Error types:

- `authentication_failed` - Không có authentication context
- `processing_failed` - Lỗi xử lý user data
- `provider_error` - Lỗi từ OAuth2 provider

---

## 👤 User Management Endpoints

### 3. Tạo/Cập nhật OAuth2 User

#### **POST** `/users/oauth2`

Tạo user mới hoặc cập nhật thông tin user từ OAuth2 login.

**Request Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "email": "user@example.com",
  "name": "Full Name",
  "firstName": "First",
  "lastName": "Last",
  "provider": "google",
  "providerId": "123456789",
  "avatarUrl": "https://example.com/avatar.jpg",
  "username": "user_google_123456789"
}
```

**Success Response:**

```json
// 201 Created hoặc 200 OK
{
  "id": 1,
  "email": "user@example.com",
  "name": "Full Name",
  "username": "user_google_123456789",
  "oauthProvider": "google",
  "oauthProviderId": "123456789",
  "avatarUrl": "https://example.com/avatar.jpg",
  "isOAuthUser": true,
  "createdDate": "2024-01-01T10:00:00Z",
  "lastModifiedDate": "2024-01-01T10:00:00Z"
}
```

**Error Responses:**

```json
// 400 Bad Request
{
  "error": "Invalid user data",
  "details": "Email is required"
}

// 500 Internal Server Error
{
  "error": "Database connection failed"
}
```

---

### 4. Tìm User bằng OAuth2 Provider Info

#### **GET** `/users/oauth2/{provider}/{providerId}`

Tìm user dựa trên OAuth2 provider và provider ID.

**Parameters:**

- `provider` _(path)_ - Provider name: `google`, `facebook`, `github`
- `providerId` _(path)_ - Provider-specific user ID

**Example Request:**

```bash
GET http://localhost:8080/users/oauth2/google/123456789
```

**Success Response:**

```json
// 200 OK
{
  "id": 1,
  "email": "user@example.com",
  "name": "Full Name",
  "username": "user_google_123456789",
  "oauthProvider": "google",
  "oauthProviderId": "123456789",
  "avatarUrl": "https://example.com/avatar.jpg",
  "isOAuthUser": true,
  "createdDate": "2024-01-01T10:00:00Z",
  "lastModifiedDate": "2024-01-01T10:00:00Z"
}
```

**Error Responses:**

```json
// 404 Not Found
{
  "error": "User not found",
  "provider": "google",
  "providerId": "123456789"
}
```

---

## 🔑 Traditional Authentication Endpoints

### 5. Traditional Login

#### **POST** `/gateway/auth/login`

Đăng nhập bằng username/password truyền thống.

**Request Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Success Response:**

```json
// 200 OK
{
  "token": "eyJhbGciOiJSUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

**Error Responses:**

```json
// 401 Unauthorized
{
  "error": "Invalid credentials"
}
```

---

### 6. Validate JWT Token

#### **POST** `/gateway/auth/validate`

Kiểm tra tính hợp lệ của JWT token.

**Request Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9..."
}
```

**Success Response:**

```json
// 200 OK
{
  "valid": true,
  "username": "user@example.com",
  "userId": 1,
  "expiresAt": "2024-01-02T10:00:00Z"
}
```

**Error Responses:**

```json
// 401 Unauthorized
{
  "valid": false,
  "error": "Token expired"
}
```

---

## 📊 User CRUD Endpoints

### 7. Lấy danh sách Users

#### **GET** `/users`

**Request Headers:**

```
Authorization: Bearer {jwt_token}
```

**Success Response:**

```json
// 200 OK
[
  {
    "id": 1,
    "email": "user1@example.com",
    "name": "User One",
    "isOAuthUser": true,
    "oauthProvider": "google"
  },
  {
    "id": 2,
    "email": "user2@example.com",
    "name": "User Two",
    "isOAuthUser": false,
    "oauthProvider": null
  }
]
```

---

### 8. Lấy User theo ID

#### **GET** `/users/{id}`

**Parameters:**

- `id` _(path)_ - User ID

**Request Headers:**

```
Authorization: Bearer {jwt_token}
```

**Success Response:**

```json
// 200 OK
{
  "id": 1,
  "email": "user@example.com",
  "name": "Full Name",
  "username": "user_google_123456789",
  "oauthProvider": "google",
  "oauthProviderId": "123456789",
  "avatarUrl": "https://example.com/avatar.jpg",
  "isOAuthUser": true,
  "createdDate": "2024-01-01T10:00:00Z",
  "lastModifiedDate": "2024-01-01T10:00:00Z"
}
```

---

### 9. Tạo User mới (Traditional)

#### **POST** `/users`

**Request Headers:**

```
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

**Request Body:**

```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "New User"
}
```

**Success Response:**

```json
// 201 Created
{
  "id": 3,
  "username": "newuser",
  "email": "newuser@example.com",
  "fullName": "New User",
  "isOAuthUser": false,
  "createdDate": "2024-01-01T10:00:00Z"
}
```

---

### 10. Cập nhật User

#### **PUT** `/users/{id}`

**Parameters:**

- `id` _(path)_ - User ID

**Request Headers:**

```
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

**Request Body:**

```json
{
  "email": "updated@example.com",
  "fullName": "Updated Name"
}
```

**Success Response:**

```json
// 200 OK
{
  "id": 1,
  "email": "updated@example.com",
  "fullName": "Updated Name",
  "lastModifiedDate": "2024-01-01T11:00:00Z"
}
```

---

### 11. Xóa User

#### **DELETE** `/users/{id}`

**Parameters:**

- `id` _(path)_ - User ID

**Request Headers:**

```
Authorization: Bearer {jwt_token}
```

**Success Response:**

```json
// 200 OK
{
  "message": "User deleted successfully",
  "deletedUserId": 1
}
```

---

## 🔍 Workflow Examples

### OAuth2 Login Flow

1. **Initiate OAuth2 Login:**

   ```bash
   # Redirect user browser to:
   http://localhost:8080/oauth2/authorize/google
   ```

2. **User authenticates with Google**

   - User enters Google credentials
   - Google redirects back to callback

3. **Handle OAuth2 Callback:**

   ```bash
   # Automatic callback (handled by browser):
   GET http://localhost:8080/oauth2/callback/google?code=AUTH_CODE&state=STATE
   ```

4. **Success Redirect:**

   ```bash
   # User browser redirected to:
   http://localhost:3000/login-success?token=eyJhbGciOiJSUzI1NiJ9...
   ```

5. **Use JWT Token:**
   ```bash
   # Use returned token for protected API calls:
   curl -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9..." \
        http://localhost:8080/users
   ```

---

### Traditional Login Flow

1. **Login Request:**

   ```bash
   curl -X POST http://localhost:8080/gateway/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"testuser","password":"password123"}'
   ```

2. **Get JWT Token:**

   ```json
   {
     "token": "eyJhbGciOiJSUzI1NiJ9...",
     "type": "Bearer",
     "expiresIn": 86400000
   }
   ```

3. **Use Token for API Calls:**
   ```bash
   curl -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9..." \
        http://localhost:8080/users/1
   ```

---

## ❌ Common Error Codes

| Status Code | Error Type            | Description                                        |
| ----------- | --------------------- | -------------------------------------------------- |
| 400         | Bad Request           | Invalid request parameters or unsupported provider |
| 401         | Unauthorized          | Invalid or expired JWT token                       |
| 403         | Forbidden             | Insufficient permissions                           |
| 404         | Not Found             | User or resource not found                         |
| 500         | Internal Server Error | Database or service communication error            |

## 🔧 Configuration

### Required Environment Variables

```bash
# OAuth2 Provider Credentials
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
FACEBOOK_APP_ID=your_facebook_app_id
FACEBOOK_APP_SECRET=your_facebook_app_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Frontend URLs
FRONTEND_SUCCESS_URL=http://localhost:3000/login-success
FRONTEND_ERROR_URL=http://localhost:3000/login-error
```

### Security Notes

1. **JWT Tokens** expire sau 24 giờ (có thể config)
2. **OAuth2 state parameter** được sử dụng để chống CSRF attacks
3. **Redirect URIs** phải được config chính xác trong OAuth2 provider console
4. **HTTPS required** trong production cho OAuth2 callbacks

---

## 📞 Support

Để được hỗ trợ hoặc báo cáo lỗi, vui lòng tạo issue trong repository hoặc liên hệ development team.
