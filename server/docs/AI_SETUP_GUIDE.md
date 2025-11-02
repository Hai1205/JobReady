# 🚀 Setup Nhanh - Tính Năng AI

## ⚡ Bước 1: Cấu Hình Backend

### 1.1 Tạo File .env

```bash
cd server/cv-service
cp .env.example .env
```

### 1.2 Điền Thông Tin OpenRouter

Mở file `.env` và điền:

```bash
# OpenRouter AI Configuration
OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions
OPENROUTER_API_KEY=sk-or-v1-xxxxxxxxxxxxxxxxxxxxxxxxx  # ← Điền API key của bạn
OPENROUTER_API_MODEL=openai/gpt-3.5-turbo
```

### 1.3 Lấy OpenRouter API Key

1. Truy cập: https://openrouter.ai
2. Đăng ký/Đăng nhập
3. Vào Settings → API Keys
4. Tạo key mới
5. Copy và paste vào `.env`

**Recommended Models:**

- `openai/gpt-3.5-turbo` - Nhanh, rẻ ($0.0015/1K tokens)
- `openai/gpt-4-turbo` - Chất lượng cao ($0.01/1K tokens)
- `anthropic/claude-3-haiku` - Balance ($0.00025/1K tokens)

---

## ⚡ Bước 2: Restart Backend

```bash
# Stop cv-service (Ctrl+C)

# Start lại
cd server/cv-service
mvn spring-boot:run

# Hoặc nếu dùng Docker
docker-compose restart cv-service
```

---

## ⚡ Bước 3: Test Frontend

```bash
cd client
npm run dev
```

Mở: http://localhost:3000/cv-builder

---

## 🧪 Test Nhanh

### Test 1: Kiểm tra Backend

```bash
# Check health
curl http://localhost:8084/actuator/health

# Expected: {"status":"UP"}
```

### Test 2: Test AI Endpoint

```bash
curl -X POST http://localhost:8084/cvs/analyze \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F 'data={"title":"Test CV","personalInfo":{"fullname":"Test","email":"test@example.com","phone":"123","location":"HN","summary":"Test"},"experiences":[{"company":"ABC","position":"Dev","startDate":"2020-01","endDate":"2024-01","description":"Test"}],"educations":[{"school":"XYZ","degree":"Bachelor","field":"CS","startDate":"2016-09","endDate":"2020-06"}],"skills":["JavaScript","React"]}'
```

### Test 3: Test UI

1. Vào /cv-builder
2. Điền thông tin CV
3. Click "Phân Tích CV Nhanh"
4. Đợi 5-10s
5. ✅ Thấy toast success + suggestions

---

## ⚠️ Troubleshooting

### Lỗi: "Error calling OpenRouter API"

**Nguyên nhân:** API key sai hoặc không có credit

**Giải pháp:**

1. Check API key trong `.env`
2. Login vào OpenRouter.ai
3. Check credits: https://openrouter.ai/credits
4. Nạp tiền nếu cần ($5 minimum)

### Lỗi: "OPENROUTER_API_KEY is not set"

**Nguyên nhân:** Environment variable không load

**Giải pháp:**

```bash
# Export manually
export OPENROUTER_API_KEY=sk-or-v1-xxxxx
export OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions
export OPENROUTER_API_MODEL=openai/gpt-3.5-turbo

# Restart service
mvn spring-boot:run
```

### Lỗi: "CV not found" hoặc "No suggestions"

**Nguyên nhân:** CV data thiếu hoặc không đầy đủ

**Giải pháp:**

- Đảm bảo có ít nhất 1 experience
- Đảm bảo có ít nhất 1 education
- Summary không để trống
- Skills có ít nhất 1 item

### Lỗi: Timeout

**Nguyên nhân:** AI response chậm

**Giải pháp:**

- Đợi lâu hơn (10-15s)
- Thử model khác (gpt-3.5-turbo nhanh hơn)
- Check network connection

---

## 💰 Chi Phí Ước Tính

### OpenRouter Pricing

```
1 CV analysis ≈ 1000-2000 tokens
Cost per analysis:
- GPT-3.5-Turbo: $0.002-0.003 (~50 VNĐ)
- GPT-4-Turbo: $0.01-0.02 (~250 VNĐ)
- Claude-3-Haiku: $0.0005-0.001 (~12 VNĐ)

100 analyses/day ≈ $0.20-0.30/day (~5,000-7,500 VNĐ/day)
```

**Khuyến nghị:** Start với GPT-3.5-Turbo, nâng cấp sau

---

## 📋 Checklist

### Backend

- [ ] File `.env` đã tạo
- [ ] `OPENROUTER_API_KEY` đã điền
- [ ] API key valid (check trên OpenRouter.ai)
- [ ] Credits available (ít nhất $1)
- [ ] cv-service đang chạy
- [ ] Health check OK

### Frontend

- [ ] npm run dev đang chạy
- [ ] Truy cập /cv-builder OK
- [ ] Sidebar "Công Cụ AI" hiển thị
- [ ] Nút "Phân Tích CV Nhanh" có thể click

### Test

- [ ] Test Quick Analyze thành công
- [ ] Test Job Match thành công
- [ ] Test Apply Suggestion thành công
- [ ] Suggestions hiển thị trong tab "Gợi Ý"

---

## 🎯 Next Steps

Sau khi setup thành công:

1. **Optimize prompts** - Cải thiện system prompts trong `CVService.java`
2. **Add caching** - Cache AI responses để giảm cost
3. **Add rate limiting** - Giới hạn số lần analyze/user
4. **Monitor usage** - Theo dõi API usage và cost
5. **A/B testing** - Test các models khác nhau

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề:

1. Check console logs (backend và frontend)
2. Check network tab (F12 → Network)
3. Check OpenRouter dashboard: https://openrouter.ai/activity
4. Review file: `AI_ANALYSIS_CHECK_REPORT.md`

---

**Setup xong là có thể dùng ngay! 🎉**
