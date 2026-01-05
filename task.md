- tìm và đổi toàn bộ "planName" thành "planTitle"
- tất cả các user mặc định khi được tạo thì sẽ có free plan, riêng root user sẽ là ultra (plan exp vô hạn, có thể set ở 1 khoảng thời gian cực xa)
- sửa lại page /plans:
+ để "Most Popular" vào bên trong card luôn thay vì để ở vị trí hiện tại.
+ cần thêm giấu hiệu để người dùng biết được hiện đang sử dụng gói nào ví dụ như thêm "gói hiện tại" bên trong card, free plan sẽ không hiện nút thanh toán.
+ nút thanh toán phải hiện chữ "Đăng ký gói" 
- sửa lại page /payment:
+ giao diện hiện tại quá xấu không phù hơp với webapp, đặc biệt là màu nền, gợi ý nên để màu nền giống /plans, có thể tham khảo trang "cursor" đang làm.
+ không sử dụng param để truyền dữ liệu nửa mà bây giờ sẽ thông qua payment store
- sửa lại page /setting:
+ ứng dụng phân trang chỉ hiện tối đa 5/trang
+ màu chữ và màu của badge "thất bại" đang khá bị chìm không thấy rõ cần thay đổi chút sắc tố cho dễ nhìn
+ khi nhấn thử lại thì sẽ đưa người dùng đến trang /payment
+ hoàn thiện tính năng tải hóa đơn
+ sửa lại logic của component "PlanManagementTab"
- sửa lại page /admin:
+ component "RevenueStats" sẽ luôn xuất hiện nếu không có dữ liệu thì các dữ liệu mặc định là 0, thì vì như hiện tại là ẩn đi
+ thêm dialog để xem hóa đơn khi nhấn vào onView
- sửa lỗi payment service:
Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.

2025-12-31T17:26:11.327Z ERROR 7 --- [user-service] [           main] o.s.b.d.LoggingFailureAnalysisReporter   : 


***************************

APPLICATION FAILED TO START

***************************


Description:


Failed to bind properties under 'vnpay' to com.example.paymentservice.configs.VNPayConfig$$SpringCGLIB$$0:


    Property: vnpay.command

    Value: "pay"

    Origin: System Environment Property "VNPAY_COMMAND"

    Reason: java.lang.IllegalStateException: No setter found for property: command


Action:


Update your application's configuration
- build lại cả client và server cho đến khi nào hết lỗi thì thôi