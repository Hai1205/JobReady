# Report: Tóm tắt file trong `rabbit-common` (đính kèm)

Ngày: 2025-10-24

Mục tiêu: Liệt kê tên các file bạn đính kèm và mô tả ngắn gọn chức năng của từng file trong dự án. Nếu file không có công dụng hiện tại thì ghi rõ "hiện không có công dụng".

---

## 1) `DeadLetterQueueConfig.java`
- Công dụng: Cấu hình Dead Letter Exchange (DLX), Dead Letter Queue (DLQ) và Poison Queue cho hệ thống RabbitMQ. Cung cấp helper `createQueueWithDLX(...)` để tạo queue chính với cấu hình DLX/TTL/retry.
- Chi tiết: Định nghĩa các exchange và queue (ví dụ `dlx.exchange`, `poison.exchange`, `dlq.user.create.queue`, `poison.queue`) và các binding tương ứng. Có các tham số tối ưu (lazy queue, TTL, v.v.).
- Ứng dụng trong dự án: Rất hữu ích cho cơ chế retry và xử lý lỗi bất đồng bộ trên RabbitMQ; hỗ trợ chuyển thông điệp lỗi vào DLQ/Poison queue.

## 2) `RabbitListenerConfig.java`
- Công dụng: Cấu hình container factories cho RabbitMQ listeners, bật Direct Reply-to cho `RabbitTemplate` và tạo factory dành riêng cho Direct Reply-To.
- Chi tiết: `configureRabbitTemplate()` bật `setUseDirectReplyToContainer(true)`; định nghĩa `rabbitListenerContainerFactory` (AUTO ack) và `directReplyToContainerFactory` (NONE ack) với Jackson JSON message converter.
- Ứng dụng trong dự án: Quan trọng để hỗ trợ pattern RPC bằng Direct Reply-To (low-latency RPC) và đảm bảo message converter phù hợp. Rất liên quan tới phần RPC giữa các service.

## 3) `CVCreatedEvent.java`
- Công dụng: DTO/event class cho sự kiện khi CV được tạo (domain event).
- Chi tiết: Chứa các trường như `eventId`, `cvId`, `userId`, `cvTitle`, `createdAt`, `metadata`.
- Ứng dụng trong dự án: Dùng để publish/propagate event khi CV được tạo (Event-driven architecture). Nếu project sử dụng EventPublisher, file này là payload cho sự kiện `user.created`/`cv.created`.

## 4) `DomainEvent.java`
- Công dụng: Base abstract class cho các domain events chung.
- Chi tiết: Có các trường chuẩn như `eventId`, `eventType`, `aggregateId`, `aggregateType`, `occurredAt`, `sourceService`, `metadata`.
- Ứng dụng trong dự án: Dùng làm chuẩn chung cho các event (giúp chuẩn hóa các event, dễ logging, tracing, versioning). Nếu các event kế thừa (UserCreatedEvent, CVCreatedEvent) thì file này có công dụng.

## 5) `UserActivatedEvent.java`
- Công dụng: DTO/event class cho sự kiện khi user được kích hoạt.
- Chi tiết: Trường `eventId`, `userId`, `email`, `activatedAt`, `metadata`.
- Ứng dụng trong dự án: Dùng để publish sự kiện kích hoạt user; hữu ích cho audit, notification hoặc downstream processing.

## 6) `UserCreatedEvent.java`
- Công dụng: DTO/event class khi user mới được tạo.
- Chi tiết: Trường `eventId`, `userId`, `username`, `email`, `fullname`, `createdAt`, `metadata`.
- Ứng dụng trong dự án: Dùng để publish event khi tạo user, ví dụ để sync tới các service khác hoặc gửi welcome email.

## 7) `UserDeletedEvent.java`
- Công dụng: DTO/event class cho sự kiện xóa user.
- Chi tiết: `eventId`, `userId`, `email`, `deletedAt`, `reason`, `metadata`.
- Ứng dụng trong dự án: Dùng để publish hành động xóa user; hữu ích cho audit, cleanup, hoặc đồng bộ.

## 8) `EventPublisher.java`
- Công dụng: Service để publish domain events lên exchange `events.exchange`.
- Chi tiết: Có phương thức `publishUserCreatedEvent(UserCreatedEvent)` và `publishEvent(String routingKey, Object event)`. Sử dụng `RabbitTemplate` + `ObjectMapper` để convert event sang JSON rồi `convertAndSend`.
- Ứng dụng trong dự án: Rất quan trọng nếu dự án dùng event-driven approach; chịu trách nhiệm gửi events ra RabbitMQ. Nếu các service khác subscribe vào `events.exchange` thì file này là trung tâm phát sự kiện.
- Gợi ý: `EventPublisher` hiện convert event thành JSON string rồi gọi `convertAndSend` — vì `RabbitTemplate` trong `BaseRabbitConfig` đã có `Jackson2JsonMessageConverter`, bạn có thể gửi object trực tiếp (không cần serialize thủ công) hoặc giữ cách hiện tại nếu muốn kiểm soát chặt chẽ payload string.

## 9) `IdempotencyService.java`
- Công dụng: Service hỗ trợ idempotency dựa trên Redis (StringRedisTemplate). Dùng để tránh xử lý trùng lặp, cache kết quả, đánh dấu đang xử lý hay đã fail.
- Chi tiết: Cung cấp `isFirstRequest`, `getCachedResult`, `updateResult`, `markAsFailed`, `getErrorMessage`, `delete`, `exists`, `getTTL`. Sử dụng key prefix `idempotency:` và lưu kết quả với TTL mặc định.
- Ứng dụng trong dự án: Rất hữu ích cho xử lý message/HTTP request idempotent (như RPC/async handlers), đặc biệt khi retry hoặc duplicate messages có thể xảy ra.

---

### Tổng kết nhanh
- Các file bạn gửi thuộc phần `rabbit-common` và liên quan trực tiếp tới messaging (RabbitMQ), event model và idempotency.
- Các file này đều có công dụng rõ rệt: cấu hình DLQ, cấu hình listener (Direct Reply-to), định nghĩa domain events, publisher event, và idempotency.
- Những file quan trọng nhất cho RPC/event-driven: `RabbitListenerConfig.java`, `EventPublisher.java`, `IdempotencyService.java`, `DeadLetterQueueConfig.java`.

### Gợi ý / Next steps (nếu bạn muốn tôi làm tiếp):
1. Kiểm tra luồng RPC thực tế (send/receive) để đảm bảo message gửi/nhận là JSON hợp lệ (đã có dấu hiệu JsonParseException trong log trước đó). Tôi có thể rà soát `RabbitRPCService` và các consumer producer để đảm bảo không dùng Map.toString() hoặc `toString()` khi gửi reply.
2. Chạy unit/integration test (nếu có) hoặc tạo small test harness để demo gửi/nhận RPC bằng Direct Reply-To.
3. Kiểm tra cấu hình Redis/Idempotency và thêm tests cho idempotency logic.

---

Nếu bạn muốn, tôi sẽ:
- 1) tự động scan thêm các file khác trong `rabbit-common` để tìm inconsistencies (ví dụ `RabbitRPCService`) và sửa lỗi JSON parsing; hoặc
- 2) tạo một checklist để thử nghiệm end-to-end RPC (producer→consumer→reply).

Bạn muốn tôi làm bước nào tiếp theo?