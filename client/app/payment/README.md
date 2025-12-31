# Hệ thống Thanh toán - JobReady

## Tổng quan

Hệ thống thanh toán được thiết kế để hỗ trợ nhiều phương thức thanh toán phổ biến tại Việt Nam và quốc tế.

## Các trang

### 1. Trang Thanh toán (`/payment`)

- **Chức năng**: Cho phép người dùng chọn phương thức thanh toán
- **Phương thức hỗ trợ**:
  - 💰 **MoMo**: Ví điện tử phổ biến tại Việt Nam
  - 💳 **VNPay**: Cổng thanh toán trực tuyến
  - 🌐 **PayPal**: Thanh toán quốc tế
- **Query Parameters**:
  - `planId`: ID của gói đăng ký
  - `planName`: Tên gói (hiển thị)
  - `amount`: Số tiền thanh toán

### 2. Trang Callback (`/payment/callback`)

- **Chức năng**: Xử lý callback từ cổng thanh toán
- **Flow**:
  1. Nhận thông tin từ payment gateway
  2. Xác thực với backend
  3. Redirect đến success/failed page

### 3. Trang Thành công (`/payment/success`)

- **Chức năng**: Hiển thị thông báo thanh toán thành công
- **Features**:
  - Hiệu ứng confetti celebration
  - Hiển thị chi tiết giao dịch
  - Danh sách quyền lợi
  - Auto-redirect sau 10 giây
  - Download hóa đơn

### 4. Trang Thất bại (`/payment/failed`)

- **Chức năng**: Hiển thị thông báo thanh toán thất bại
- **Features**:
  - Hiển thị lý do thất bại
  - Gợi ý khắc phục
  - Nút thử lại
  - Auto-redirect sau 15 giây

## Components

### PaymentButton

Nút thanh toán có thể tái sử dụng:

```tsx
<PaymentButton planId="pro-plan" planName="Pro Plan" amount={199000} />
```

### PaymentIcon

Icon cho các phương thức thanh toán:

```tsx
<PaymentIcon method={EPaymentMethod.MOMO} size={40} />
```

### Currency

Format hiển thị tiền tệ:

```tsx
<Currency amount={199000} currency="VNĐ" />
```

## API Endpoints (Backend)

### 1. Tạo thanh toán

```
POST /api/payment/create
Body: {
  planId: string,
  amount: number,
  paymentMethod: "momo" | "vnpay" | "paypal"
}
Response: {
  paymentUrl: string
}
```

### 2. Xác thực thanh toán

```
POST /api/payment/verify
Body: {
  // Query params from payment gateway
}
Response: {
  success: boolean,
  transactionId: string,
  planName: string,
  amount: string,
  errorCode?: string
}
```

## Flow hoàn chỉnh

1. **User chọn gói** `/plans`
2. **Click "Nâng cấp"** Navigate với params: `/payment?planId=xxx&planName=xxx&amount=xxx`
3. **Chọn phương thức** MoMo/VNPay/PayPal
4. **Click "Thanh toán"** Call API create payment Nhận `paymentUrl`
5. **Redirect** Payment Gateway (MoMo/VNPay/PayPal)
6. **User thanh toán** Gateway xử lý
7. **Gateway callback** `/payment/callback?...params`
8. **Verify payment** Call API verify
9. **Redirect kết quả**:
   - Success `/payment/success?transactionId=xxx&...`
   - Failed `/payment/failed?error=xxx&...`

## Error Codes

- `insufficient_funds`: Không đủ số dư
- `card_declined`: Thẻ bị từ chối
- `expired_card`: Thẻ hết hạn
- `invalid_card`: Thông tin thẻ không hợp lệ
- `transaction_timeout`: Timeout
- `bank_error`: Lỗi ngân hàng
- `cancelled`: User hủy giao dịch
- `unknown_error`: Lỗi không xác định

## Cài đặt

```bash
# Cài đặt dependencies
npm install canvas-confetti
npm install --save-dev @types/canvas-confetti
```

## Security Notes

- Tất cả giao dịch được mã hóa SSL/TLS
- Xác thực callback từ payment gateway
- Không lưu thông tin thẻ trên client
- Token-based authentication
- Rate limiting trên API

## TODO

- [ ] Implement backend payment APIs
- [ ] Integrate với MoMo SDK
- [ ] Integrate với VNPay SDK
- [ ] Integrate với PayPal SDK
- [ ] Add payment history page
- [ ] Add invoice download functionality
- [ ] Add refund functionality
- [ ] Add webhook handlers
- [ ] Add payment analytics

## Support

Liên hệ: support@jobready.com
