# Rabbit Common Module

Module này cung cấp các cấu hình và tiện ích chung cho việc tích hợp RabbitMQ trong các microservices.

## Mục Tiêu

- Loại bỏ mã lặp lại giữa các service khi sử dụng RabbitMQ
- Đảm bảo nhất quán cấu hình RabbitMQ giữa các service
- Đơn giản hóa việc thêm RabbitMQ vào service mới
- Tạo điểm trung tâm để quản lý tất cả các hằng số và cấu hình RabbitMQ

## Cấu Trúc

Module này chứa:

1. **BaseRabbitConfig**: Lớp cấu hình cơ bản cho RabbitMQ có thể được kế thừa bởi các service
2. **BaseConsumer**: Lớp tiêu thụ cơ bản cung cấp các phương thức trích xuất header và payload từ message
3. **RabbitRPCService**: Service hỗ trợ gửi/nhận message RabbitMQ theo mô hình RPC
4. **RabbitConstants**: Các hằng số chung như tên exchange, queue và routing key
5. **DTO**: Các lớp dùng chung như RabbitHeader và RabbitResponse

## Cách Sử Dụng

### 1. Thêm Phụ Thuộc

Thêm module này vào pom.xml của service:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>rabbit-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Cấu Hình RabbitMQ

Tạo một lớp cấu hình RabbitConfig trong service của bạn kế thừa từ BaseRabbitConfig:

```java
@Configuration
public class RabbitConfig extends BaseRabbitConfig {
    @Bean
    public Declarables declareExchangesQueuesAndBindings() {
        List<ExchangeDef> exchangeDefs = new ArrayList<>();

        // Định nghĩa exchange và queue
        List<QueueDef> myQueues = List.of(
                new QueueDef("my.queue.name", "my.routing.key", true)
        );

        // Thêm exchange vào danh sách
        exchangeDefs.add(new ExchangeDef(RabbitConstants.MY_EXCHANGE, myQueues));

        return createDeclarables(exchangeDefs);
    }
}
```

### 3. Tạo Consumer

Tạo consumer kế thừa từ BaseConsumer:

```java
@Component
public class MyConsumer extends BaseConsumer {
    @RabbitListener(queues = "my.queue.name")
    public void handleMessage(Message message) {
        RabbitHeader header = extractHeader(message);
        MyPayload payload = extractPayload(message, MyPayload.class);

        // Xử lý message

        // Gửi phản hồi nếu cần
        rpcService.sendReply(
            "my.exchange",
            header.getReplyTo(),
            header.getCorrelationId(),
            responseData
        );
    }
}
```

### 4. Sử Dụng RabbitRPCService

Tiêm RabbitRPCService vào service của bạn và sử dụng nó để gửi message:

```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final RabbitRPCService rpcService;

    public void doSomething() {
        RabbitHeader header = RabbitHeader.builder()
                .correlationId(UUID.randomUUID().toString())
                .replyTo("my.reply.queue")
                .sourceService("my-service")
                .targetService("other-service")
                .timestamp(System.currentTimeMillis())
                .build();

        MyResponse response = rpcService.sendAndReceive(
            RabbitConstants.OTHER_EXCHANGE,
            "other.routing.key",
            header,
            payload,
            MyResponse.class
        );

        // Xử lý phản hồi
    }
}
```

## Duy Trì

Khi thêm tính năng mới liên quan đến RabbitMQ, hãy cân nhắc xem chúng có thể được thêm vào module chung này để tái sử dụng không.

Mọi thay đổi lớn đối với module này phải được thông báo cho tất cả các team phát triển service khác vì nó có thể ảnh hưởng đến họ.
