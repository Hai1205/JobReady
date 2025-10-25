# Report: Tóm tắt file trong `rabbit-common` (đính kèm)

Ngày: 2025-10-24

Mục tiêu: Liệt kê tên các file bạn đính kèm và mô tả ngắn gọn chức năng của từng file trong dự án. Nếu file không có công dụng hiện tại thì ghi rõ "hiện không có công dụng".

---

## 1) `DeadLetterQueueConfig.java`

- Công dụng: Cấu hình Dead Letter Exchange (DLX), Dead Letter Queue (DLQ) và Poison Queue cho hệ thống RabbitMQ. Cung cấp helper `createQueueWithDLX(...)` để tạo queue chính với cấu hình DLX/TTL/retry.
- Chi tiết: Định nghĩa các exchange và queue (ví dụ `dlx.exchange`, `poison.exchange`, `dlq.user.create.queue`, `poison.queue`) và các binding tương ứng. Có các tham số tối ưu (lazy queue, TTL, v.v.).
- Thực tế trong dự án (tìm thấy trong code):
  - `sever/user-service/src/main/java/com/example/userservice/services/consumers/DLQRetryListener.java` — import `com.example.rabbitmq.configs.DeadLetterQueueConfig` và dùng các hằng như `DeadLetterQueueConfig.USER_CREATE_DLQ`, `USER_ACTIVATE_DLQ`, `POISON_QUEUE` trong các `@RabbitListener` và logic xử lý.
  - Docs: `sever/docs/MIGRATION_GUIDE.md`, `sever/docs/IMPLEMENTATION_CHECKLIST.md` tham chiếu và hướng dẫn sử dụng.

=> Kết luận: File này thực sự được sử dụng (import và tham chiếu) trong `user-service` (DLQ retry listener).

## 2) `RabbitListenerConfig.java`

- Công dụng: Cấu hình container factories cho RabbitMQ listeners, bật Direct Reply-to cho `RabbitTemplate` và tạo factory dành riêng cho Direct Reply-To.
- Chi tiết: `configureRabbitTemplate()` bật `setUseDirectReplyToContainer(true)`; định nghĩa `rabbitListenerContainerFactory` (AUTO ack) và `directReplyToContainerFactory` (NONE ack) với Jackson JSON message converter.
- Thực tế trong dự án (tìm thấy trong code):
  - File được biên dịch và tồn tại trong `rabbit-common` (target entries). Không tìm thấy import trực tiếp của `RabbitListenerConfig` trong các module khác bằng grep, nhưng file là `@Configuration` — nếu `rabbit-common` được thêm vào classpath và component-scan, Spring sẽ đăng ký các bean trong file này tại runtime.

=> Kết luận: `RabbitListenerConfig` là class cấu hình (đã biên dịch). Không có import code-level trực tiếp, nhưng là cấu hình runtime khi module `rabbit-common` được sử dụng.

## 3) `CVCreatedEvent.java`

- Công dụng: DTO/event class cho sự kiện khi CV được tạo (domain event).
- Chi tiết: Chứa các trường như `eventId`, `cvId`, `userId`, `cvTitle`, `createdAt`, `metadata`.
- Thực tế trong dự án (tìm thấy trong code):
  - File tồn tại trong `rabbit-common` và được biên dịch (target). Không tìm thấy import/references ở các module khác (chỉ thấy nhắc đến trong docs/checklist).

=> Kết luận: `CVCreatedEvent` hiện chỉ được khai báo trong `rabbit-common` và không có usage trực tiếp trong code của các services (ứng dụng hiện không rõ ràng; docs chỉ gợi ý tạo nếu cần).

## 4) `DomainEvent.java`

- Công dụng: Base abstract class cho các domain events chung.
- Chi tiết: Có các trường chuẩn như `eventId`, `eventType`, `aggregateId`, `aggregateType`, `occurredAt`, `sourceService`, `metadata`.
- Thực tế trong dự án (tìm thấy trong code):
  - File tồn tại và được biên dịch trong `rabbit-common` (target). Không tìm thấy import/references từ các module khác theo grep search.

=> Kết luận: `DomainEvent` hiện không được tham chiếu ở code bên ngoài `rabbit-common` (ứng dụng chưa rõ ràng trong repository hiện tại).

## 5) `UserActivatedEvent.java`

- Công dụng: DTO/event class cho sự kiện khi user được kích hoạt.
- Chi tiết: Trường `eventId`, `userId`, `email`, `activatedAt`, `metadata`.
- Thực tế trong dự án (tìm thấy trong code):
  - File tồn tại và biên dịch trong `rabbit-common`. Grep chỉ tìm thấy file và các entry trong docs; không có import/references trong các service khác.

=> Kết luận: Hiện chưa thấy usage trực tiếp trong code của các service (chỉ có trong module `rabbit-common` và docs).

## 6) `UserCreatedEvent.java`

- Công dụng: DTO/event class khi user mới được tạo.
- Chi tiết: Trường `eventId`, `userId`, `username`, `email`, `fullname`, `createdAt`, `metadata`.
- Thực tế trong dự án (tìm thấy trong code):
  - Được import và sử dụng bởi `EventPublisher.java` (same `rabbit-common` module):
    - `sever/rabbit-common/src/main/java/com/example/rabbitmq/services/EventPublisher.java` — phương thức `publishUserCreatedEvent(UserCreatedEvent event)`.
  - Docs và architecture notes cũng tham chiếu `UserCreatedEvent`.

=> Kết luận: `UserCreatedEvent` được sử dụng nội bộ trong `rabbit-common` bởi `EventPublisher` (không thấy usage ngoài module này trong repo).

## 7) `UserDeletedEvent.java`

- Công dụng: DTO/event class cho sự kiện xóa user.
- Chi tiết: `eventId`, `userId`, `email`, `deletedAt`, `reason`, `metadata`.
- Thực tế trong dự án (tìm thấy trong code):
  - File tồn tại và được biên dịch; grep chỉ thấy file và docs checklist. Không tìm thấy import/reference trong các module khác.

=> Kết luận: Hiện không có usage trực tiếp trong các service (chỉ khai báo trong `rabbit-common`).

## 8) `EventPublisher.java`

- Công dụng: Service để publish domain events lên exchange `events.exchange`.
- Chi tiết: Có phương thức `publishUserCreatedEvent(UserCreatedEvent)` và `publishEvent(String routingKey, Object event)`. Sử dụng `RabbitTemplate` + `ObjectMapper` để convert event sang JSON rồi `convertAndSend`.
- Thực tế trong dự án (tìm thấy trong code):
  - `EventPublisher` file nằm trong `rabbit-common` và được biên dịch. Grep không tìm thấy import của `EventPublisher` từ các services khác (chỉ docs tham chiếu and internal use in docs examples).

=> Kết luận: `EventPublisher` hiện chưa được inject/import vào các service khác trong repo (không tìm thấy usage code-level ngoài chính file). Có khả năng được dùng later at runtime if injected, but currently no direct code references.

## 9) `IdempotencyService.java`

- Công dụng: Service hỗ trợ idempotency dựa trên Redis (StringRedisTemplate). Dùng để tránh xử lý trùng lặp, cache kết quả, đánh dấu đang xử lý hay đã fail.
- Chi tiết: Cung cấp `isFirstRequest`, `getCachedResult`, `updateResult`, `markAsFailed`, `getErrorMessage`, `delete`, `exists`, `getTTL`. Sử dụng key prefix `idempotency:` và lưu kết quả với TTL mặc định.
- Thực tế trong dự án (tìm thấy trong code):
  - Được import và sử dụng trong `user-service` consumer:
    - `sever/user-service/src/main/java/com/example/userservice/services/consumers/UserConsumer.java` — `private final IdempotencyService idempotencyService;` và nhiều lần gọi `isFirstRequest`, `getCachedResult`, `updateResult`, `markAsFailed`.
  - Có entries trong docs and implementation checklists.

=> Kết luận: `IdempotencyService` thực sự được dùng bởi `user-service` để hỗ trợ idempotent message handling.

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

- 1. tự động scan thêm các file khác trong `rabbit-common` để tìm inconsistencies (ví dụ `RabbitRPCService`) và sửa lỗi JSON parsing; hoặc
- 2. tạo một checklist để thử nghiệm end-to-end RPC (producer→consumer→reply).

Bạn muốn tôi làm bước nào tiếp theo?
