# Tài liệu API Chat

Tài liệu này cung cấp thông tin toàn diện về các endpoint API chat có sẵn trong ứng dụng, bao gồm cả REST API và WebSocket endpoints.

## Xác thực

Tất cả các endpoint đều yêu cầu xác thực:

- **REST API**: Bao gồm token JWT trong header `Authorization`
- **WebSocket**: Kết nối với token JWT hợp lệ dưới dạng tham số truy vấn hoặc header, sau đó sử dụng kết nối đã thiết lập

## Các Endpoint REST API

### Gửi tin nhắn

```
POST /api/messages/send
```

**Request Headers:**
- `Authorization`: JWT token

**Request Body:**
```json
{
  "receiverId": 123,
  "content": "Xin chào, bạn khỏe không?"
}
```

**Response:**
```json
{
  "id": 456,
  "senderId": 789,
  "receiverId": 123,
  "content": "Xin chào, bạn khỏe không?",
  "sentAt": "2023-07-15T14:30:45",
  "read": false,
  "sender": {
    "id": 789,
    "username": "sender_user",
    "name": "Tên Người Gửi",
    "userImage": "path/to/image.jpg",
    "email": "sender@example.com"
  },
  "receiver": {
    "id": 123,
    "username": "receiver_user",
    "name": "Tên Người Nhận",
    "userImage": "path/to/image.jpg",
    "email": "receiver@example.com"
  }
}
```

### Lấy cuộc trò chuyện với một người dùng

```
GET /api/messages/conversation/{userId}
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `userId`: ID của người dùng để lấy cuộc trò chuyện

**Response:**
```json
[
  {
    "id": 456,
    "senderId": 789,
    "receiverId": 123,
    "content": "Xin chào, bạn khỏe không?",
    "sentAt": "2023-07-15T14:30:45",
    "read": true,
    "sender": {
      "id": 789,
      "username": "sender_user",
      "name": "Tên Người Gửi",
      "userImage": "path/to/image.jpg",
      "email": "sender@example.com"
    },
    "receiver": {
      "id": 123,
      "username": "receiver_user",
      "name": "Tên Người Nhận",
      "userImage": "path/to/image.jpg",
      "email": "receiver@example.com"
    }
  }
]
```

Các tin nhắn khác sẽ theo cùng một định dạng.

### Lấy các cuộc trò chuyện gần đây

```
GET /api/messages/recent
```

**Request Headers:**
- `Authorization`: JWT token

**Response:**
```json
[
  {
    "id": 123,
    "username": "user1",
    "name": "Người Dùng Một",
    "userImage": "path/to/image.jpg",
    "email": "user1@example.com",
    "lastMessage": "Xin chào, bạn khỏe không?",
    "sentAt": "2023-07-15T14:30:45"
  }
]
```

### Đánh dấu tin nhắn đã đọc

```
PUT /api/messages/messages/{messageId}/read
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `messageId`: ID của tin nhắn để đánh dấu là đã đọc

**Response:**
- Status 200 OK (không có nội dung)

### Xóa tin nhắn

```
DELETE /api/messages/messages/{messageId}
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `messageId`: ID của tin nhắn để xóa

**Response:**
- Status 200 OK (không có nội dung)

### Lấy tin nhắn chưa đọc

```
GET /api/messages/unread
```

**Request Headers:**
- `Authorization`: JWT token

**Response:**
```json
[
  {
    "id": 456,
    "senderId": 789,
    "receiverId": 123,
    "content": "Xin chào, bạn khỏe không?",
    "sentAt": "2023-07-15T14:30:45",
    "read": false,
    "sender": {
      "id": 789,
      "username": "sender_user",
      "name": "Tên Người Gửi",
      "userImage": "path/to/image.jpg",
      "email": "sender@example.com"
    },
    "receiver": {
      "id": 123,
      "username": "receiver_user",
      "name": "Tên Người Nhận",
      "userImage": "path/to/image.jpg",
      "email": "receiver@example.com"
    }
  }
]
```

### Lấy số lượng tin nhắn chưa đọc

```
GET /api/messages/unread/count
```

**Request Headers:**
- `Authorization`: JWT token

**Response:**
```json
{
  "totalUnread": 5,
  "unreadCountsBySender": {
    "123": 2,
    "456": 3
  }
}
```

### Đánh dấu tất cả tin nhắn từ một người dùng là đã đọc

```
PUT /api/messages/read-all/{userId}
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `userId`: ID của người dùng có tin nhắn cần đánh dấu là đã đọc

**Response:**
```json
{
  "message": "All messages marked as read"
}
```

## Các Endpoint WebSocket

### Kết nối đến WebSocket

```
WebSocket: /ws
```

Sử dụng SockJS để tương thích. Kết nối với token JWT hợp lệ.

### Gửi tin nhắn (WebSocket)

```
SEND /app/chat.send
```

**Message Body:**
```json
{
  "receiverId": 123,
  "content": "Xin chào, bạn khỏe không?"
}
```

**Đăng ký để nhận tin nhắn:**
```
SUBSCRIBE /user/{userId}/queue/messages
```

Trong đó `{userId}` là ID người dùng của bạn.

**Định dạng tin nhắn nhận được:**
```json
{
  "id": 456,
  "senderId": 789,
  "receiverId": 123,
  "content": "Xin chào, bạn khỏe không?",
  "sentAt": "2023-07-15T14:30:45",
  "read": false,
  "sender": {
    "id": 789,
    "username": "sender_user",
    "name": "Tên Người Gửi",
    "userImage": "path/to/image.jpg",
    "email": "sender@example.com"
  },
  "receiver": {
    "id": 123,
    "username": "receiver_user",
    "name": "Tên Người Nhận",
    "userImage": "path/to/image.jpg",
    "email": "receiver@example.com"
  }
}
```

### Xóa tin nhắn (WebSocket)

```
SEND /app/chat.delete
```

**Message Body:**
```json
123
```

Message body chỉ nên chứa messageId dưới dạng số.

**Đăng ký để nhận sự kiện xóa:**
```
SUBSCRIBE /user/{userId}/queue/messages
```

Trong đó `{userId}` là ID người dùng của bạn.

**Định dạng sự kiện xóa nhận được:**
```json
{
  "messageId": 123,
  "senderId": 789,
  "receiverId": 456,
  "eventType": "MESSAGE_DELETED"
}
```

## Ví dụ sử dụng (JavaScript với SockJS và STOMP)

```javascript
// Kết nối đến WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

// Kết nối với token JWT
const headers = {
  'Authorization': 'Bearer your-jwt-token'
};

stompClient.connect(headers, function(frame) {
  console.log('Đã kết nối: ' + frame);
  
  // Đăng ký nhận tin nhắn
  stompClient.subscribe('/user/' + userId + '/queue/messages', function(message) {
    const messageData = JSON.parse(message.body);
    
    // Kiểm tra xem đó có phải là sự kiện xóa không
    if (messageData.eventType === 'MESSAGE_DELETED') {
      console.log('Tin nhắn đã bị xóa:', messageData.messageId);
      // Xử lý xóa tin nhắn trong UI
    } else {
      console.log('Tin nhắn mới nhận được:', messageData);
      // Xử lý tin nhắn mới trong UI
    }
  });
  
  // Gửi tin nhắn
  stompClient.send("/app/chat.send", {}, JSON.stringify({
    receiverId: 123,
    content: "Xin chào, bạn khỏe không?"
  }));
  
  // Xóa tin nhắn
  stompClient.send("/app/chat.delete", {}, JSON.stringify(123)); // messageId
});
```

## Ví dụ REST API (Fetch API)

```javascript
// Gửi tin nhắn
async function sendMessage(receiverId, content) {
  const response = await fetch('/api/messages/send', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer your-jwt-token'
    },
    body: JSON.stringify({
      receiverId: receiverId,
      content: content
    })
  });
  
  return await response.json();
}

// Lấy cuộc trò chuyện với một người dùng
async function getConversation(userId) {
  const response = await fetch(`/api/messages/conversation/${userId}`, {
    method: 'GET',
    headers: {
      'Authorization': 'Bearer your-jwt-token'
    }
  });
  
  return await response.json();
}

// Đánh dấu tin nhắn là đã đọc
async function markAsRead(messageId) {
  await fetch(`/api/messages/messages/${messageId}/read`, {
    method: 'PUT',
    headers: {
      'Authorization': 'Bearer your-jwt-token'
    }
  });
}
```

## Lưu ý cho việc triển khai Frontend

1. Luôn xử lý lỗi kết nối WebSocket và kết nối lại khi cần thiết
2. Lưu trữ token JWT an toàn và bao gồm nó trong tất cả các yêu cầu
3. Cập nhật UI theo thời gian thực khi nhận tin nhắn WebSocket hoặc sự kiện xóa
4. Xem xét việc triển khai hàng đợi tin nhắn ngoại tuyến để có trải nghiệm người dùng tốt hơn
5. Triển khai biên nhận đã đọc bằng cách sử dụng endpoint đánh dấu là đã đọc
6. Sử dụng endpoint số lượng chưa đọc để hiển thị huy hiệu thông báo