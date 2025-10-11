# 🔐 OAuth2 Provider Setup Guide

Hướng dẫn chi tiết cách đăng ký và cấu hình OAuth2 applications cho Google, Facebook, và GitHub.

## 📋 Tổng quan

Để sử dụng OAuth2 Login, bạn cần:

1. Đăng ký OAuth2 application trên từng provider
2. Lấy Client ID và Client Secret
3. Cấu hình Redirect URIs
4. Cập nhật `application.properties`

---

## 🟦 Google OAuth2 Setup

### Bước 1: Tạo Google Cloud Project

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn existing project
3. Enable Google+ API hoặc Google Identity Service

### Bước 2: Tạo OAuth2 Credentials

1. Vào **APIs & Services** > **Credentials**
2. Click **+ CREATE CREDENTIALS** > **OAuth 2.0 Client IDs**
3. Application type: **Web application**
4. Name: `Spring Microservices OAuth2`

### Bước 3: Cấu hình Redirect URIs

```
Authorized JavaScript origins:
- http://localhost:8080
- http://localhost:3000 (nếu có frontend)

Authorized redirect URIs:
- http://localhost:8080/oauth2/callback/google
- http://localhost:8080/login/oauth2/code/google
```

### Bước 4: Copy Credentials

```properties
# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

### 📝 Test URL

```
http://localhost:8080/oauth2/authorize/google
```

---

## 🟦 Facebook OAuth2 Setup

### Bước 1: Tạo Facebook App

1. Truy cập [Facebook Developers](https://developers.facebook.com/)
2. Click **My Apps** > **Create App**
3. App type: **Consumer**
4. App name: `Spring Microservices`

### Bước 2: Add Facebook Login Product

1. Trong App Dashboard, click **Add a Product**
2. Chọn **Facebook Login** > **Set Up**
3. Platform: **Web**

### Bước 3: Cấu hình Facebook Login Settings

```
Valid OAuth Redirect URIs:
- http://localhost:8080/oauth2/callback/facebook
- http://localhost:8080/login/oauth2/code/facebook

Valid Origins:
- http://localhost:8080
```

### Bước 4: Copy App Credentials

1. Vào **Settings** > **Basic**
2. Copy **App ID** và **App Secret**

```properties
# Facebook OAuth2 Configuration
spring.security.oauth2.client.registration.facebook.client-id=YOUR_FACEBOOK_APP_ID
spring.security.oauth2.client.registration.facebook.client-secret=YOUR_FACEBOOK_APP_SECRET
```

### 📝 Test URL

```
http://localhost:8080/oauth2/authorize/facebook
```

---

## 🟦 GitHub OAuth2 Setup

### Bước 1: Tạo GitHub OAuth App

1. Truy cập [GitHub Settings](https://github.com/settings/applications/new)
2. Hoặc: Profile → Settings → Developer settings → OAuth Apps → **New OAuth App**

### Bước 2: Fill Application Details

```
Application name: Spring Microservices OAuth2
Homepage URL: http://localhost:8080
Application description: Spring Boot Microservices with OAuth2 Login
Authorization callback URL: http://localhost:8080/oauth2/callback/github
```

### Bước 3: Generate Client Secret

1. After creating app, click **Generate a new client secret**
2. Copy both **Client ID** và **Client secret**

```properties
# GitHub OAuth2 Configuration
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
```

### 📝 Test URL

```
http://localhost:8080/oauth2/authorize/github
```

---

## 🛠️ Development Configuration

### Complete application.properties Example

```properties
# Google OAuth2 Client
spring.security.oauth2.client.registration.google.client-id=1234567890-abcdefghijklmnop.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=GOCSPX-abcdefghijklmnopqrstuvwx
spring.security.oauth2.client.registration.google.scope=openid,email,profile
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/oauth2/callback/google

# Facebook OAuth2 Client
spring.security.oauth2.client.registration.facebook.client-id=1234567890123456
spring.security.oauth2.client.registration.facebook.client-secret=abcdef1234567890abcdef1234567890
spring.security.oauth2.client.registration.facebook.scope=email,public_profile
spring.security.oauth2.client.registration.facebook.redirect-uri=http://localhost:8080/oauth2/callback/facebook

# GitHub OAuth2 Client
spring.security.oauth2.client.registration.github.client-id=Iv1.abcdefgh12345678
spring.security.oauth2.client.registration.github.client-secret=abcdef1234567890abcdef1234567890abcdef12
spring.security.oauth2.client.registration.github.scope=user:email
spring.security.oauth2.client.registration.github.redirect-uri=http://localhost:8080/oauth2/callback/github
```

---

## 🚀 Production Configuration

### HTTPS Requirements

Trong production, OAuth2 providers yêu cầu HTTPS:

```properties
# Production Redirect URIs
spring.security.oauth2.client.registration.google.redirect-uri=https://yourdomain.com/oauth2/callback/google
spring.security.oauth2.client.registration.facebook.redirect-uri=https://yourdomain.com/oauth2/callback/facebook
spring.security.oauth2.client.registration.github.redirect-uri=https://yourdomain.com/oauth2/callback/github
```

### Environment Variables

Khuyến nghị sử dụng environment variables trong production:

```bash
# Google
export GOOGLE_CLIENT_ID="your-google-client-id"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"

# Facebook
export FACEBOOK_CLIENT_ID="your-facebook-app-id"
export FACEBOOK_CLIENT_SECRET="your-facebook-app-secret"

# GitHub
export GITHUB_CLIENT_ID="your-github-client-id"
export GITHUB_CLIENT_SECRET="your-github-client-secret"
```

```properties
# Use environment variables
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.facebook.client-id=${FACEBOOK_CLIENT_ID}
spring.security.oauth2.client.registration.facebook.client-secret=${FACEBOOK_CLIENT_SECRET}
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
```

---

## 🧪 Testing OAuth2 Setup

### 1. Test Configuration

```bash
# Check OAuth2 configuration
curl http://localhost:8080/oauth2/test/all
```

### 2. Test Individual Providers

```bash
# Google
curl http://localhost:8080/oauth2/test/google

# Facebook
curl http://localhost:8080/oauth2/test/facebook

# GitHub
curl http://localhost:8080/oauth2/test/github
```

### 3. Test Authorization Flow

Open in browser:

- `http://localhost:8080/oauth2/authorize/google`
- `http://localhost:8080/oauth2/authorize/facebook`
- `http://localhost:8080/oauth2/authorize/github`

---

## ❗ Common Issues

### 1. Redirect URI Mismatch

**Error**: `redirect_uri_mismatch`
**Solution**: Ensure redirect URIs match exactly in provider settings

### 2. Invalid Client ID/Secret

**Error**: `invalid_client`
**Solution**: Double-check client credentials copy/paste

### 3. Scope Issues

**Error**: `invalid_scope`
**Solution**: Verify requested scopes are enabled in provider settings

### 4. CORS Issues

**Error**: CORS errors in browser
**Solution**: Add proper origins in provider settings

---

## 📞 Support

For issues:

1. Check provider-specific documentation
2. Verify redirect URIs match exactly
3. Test with dummy credentials first
4. Check logs for detailed error messages
