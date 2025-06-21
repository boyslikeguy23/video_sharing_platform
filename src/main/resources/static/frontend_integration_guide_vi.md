# Hướng dẫn tích hợp cho Frontend

## Câu hỏi: Làm sao để có thể truyền dữ liệu sang frontend?

Để truyền dữ liệu từ backend sang frontend, bạn có thể sử dụng các API endpoints đã được cung cấp. Dưới đây là tổng quan về các endpoints có sẵn:

### 1. REST API Endpoints

REST API là cách truyền thống để giao tiếp giữa frontend và backend. Các endpoints sau đây có sẵn cho chức năng chat:

- **Gửi tin nhắn**: `POST /api/messages/send`
- **Lấy cuộc trò chuyện**: `GET /api/messages/conversation/{userId}`
- **Lấy các cuộc trò chuyện gần đây**: `GET /api/messages/recent`
- **Đánh dấu tin nhắn đã đọc**: `PUT /api/messages/messages/{messageId}/read`
- **Xóa tin nhắn**: `DELETE /api/messages/messages/{messageId}`
- **Lấy tin nhắn chưa đọc**: `GET /api/messages/unread`
- **Lấy số lượng tin nhắn chưa đọc**: `GET /api/messages/unread/count`
- **Đánh dấu tất cả tin nhắn từ một người dùng là đã đọc**: `PUT /api/messages/read-all/{userId}`

### 2. WebSocket Endpoints

WebSocket cho phép giao tiếp hai chiều theo thời gian thực giữa frontend và backend. Các endpoints sau đây có sẵn:

- **Kết nối WebSocket**: `/ws` (sử dụng SockJS)
- **Gửi tin nhắn**: `SEND /app/chat.send`
- **Xóa tin nhắn**: `SEND /app/chat.delete`
- **Nhận tin nhắn và sự kiện**: `SUBSCRIBE /user/{userId}/queue/messages`

## Tài liệu chi tiết

Để biết thông tin chi tiết về cách sử dụng các endpoints này, bao gồm định dạng request/response, yêu cầu xác thực và ví dụ code, vui lòng tham khảo:

- [Tài liệu API Chat (Tiếng Việt)](chat_api_documentation_vi.md)
- [Tài liệu API Chat (Tiếng Anh)](chat_api_documentation.md)

## Lưu ý quan trọng

1. Tất cả các endpoints đều yêu cầu xác thực bằng JWT token
2. Đối với REST API, token JWT phải được bao gồm trong header `Authorization`
3. Đối với WebSocket, token JWT phải được cung cấp khi thiết lập kết nối
4. Sử dụng WebSocket cho các tính năng thời gian thực như nhận tin nhắn mới và thông báo xóa tin nhắn
5. Sử dụng REST API cho các hoạt động không thời gian thực như tải lịch sử trò chuyện

## Ví dụ tích hợp

Các tài liệu API đã cung cấp ví dụ code đầy đủ cho cả REST API và WebSocket. Bạn có thể sử dụng các ví dụ này làm điểm khởi đầu cho việc tích hợp frontend.