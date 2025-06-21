# Chat API Documentation

This document provides comprehensive information about the chat API endpoints available in the application, including both REST API and WebSocket endpoints.

## Authentication

All endpoints require authentication:

- **REST API**: Include a JWT token in the `Authorization` header
- **WebSocket**: Connect with a valid JWT token as a query parameter or header, then use the established connection

## REST API Endpoints

### Send a Message

```
POST /api/messages/send
```

**Request Headers:**
- `Authorization`: JWT token

**Request Body:**
```json
{
  "receiverId": 123,
  "content": "Hello, how are you?"
}
```

**Response:**
```json
{
  "id": 456,
  "senderId": 789,
  "receiverId": 123,
  "content": "Hello, how are you?",
  "sentAt": "2023-07-15T14:30:45",
  "read": false,
  "sender": {
    "id": 789,
    "username": "sender_user",
    "name": "Sender Name",
    "userImage": "path/to/image.jpg",
    "email": "sender@example.com"
  },
  "receiver": {
    "id": 123,
    "username": "receiver_user",
    "name": "Receiver Name",
    "userImage": "path/to/image.jpg",
    "email": "receiver@example.com"
  }
}
```

### Get Conversation with a User

```
GET /api/messages/conversation/{userId}
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `userId`: ID of the user to get conversation with

**Response:**
```json
[
  {
    "id": 456,
    "senderId": 789,
    "receiverId": 123,
    "content": "Hello, how are you?",
    "sentAt": "2023-07-15T14:30:45",
    "read": true,
    "sender": {
      "id": 789,
      "username": "sender_user",
      "name": "Sender Name",
      "userImage": "path/to/image.jpg",
      "email": "sender@example.com"
    },
    "receiver": {
      "id": 123,
      "username": "receiver_user",
      "name": "Receiver Name",
      "userImage": "path/to/image.jpg",
      "email": "receiver@example.com"
    }
  }
]
```

Additional messages would follow the same format.

### Get Recent Chats

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
    "name": "User One",
    "userImage": "path/to/image.jpg",
    "email": "user1@example.com",
    "lastMessage": "Hello, how are you?",
    "sentAt": "2023-07-15T14:30:45"
  }
]
```

### Mark Message as Read

```
PUT /api/messages/messages/{messageId}/read
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `messageId`: ID of the message to mark as read

**Response:**
- Status 200 OK (no body)

### Delete Message

```
DELETE /api/messages/messages/{messageId}
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `messageId`: ID of the message to delete

**Response:**
- Status 200 OK (no body)

### Get Unread Messages

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
    "content": "Hello, how are you?",
    "sentAt": "2023-07-15T14:30:45",
    "read": false,
    "sender": {
      "id": 789,
      "username": "sender_user",
      "name": "Sender Name",
      "userImage": "path/to/image.jpg",
      "email": "sender@example.com"
    },
    "receiver": {
      "id": 123,
      "username": "receiver_user",
      "name": "Receiver Name",
      "userImage": "path/to/image.jpg",
      "email": "receiver@example.com"
    }
  }
]
```

### Get Unread Message Counts

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

### Mark All Messages from a User as Read

```
PUT /api/messages/read-all/{userId}
```

**Request Headers:**
- `Authorization`: JWT token

**Path Parameters:**
- `userId`: ID of the user whose messages to mark as read

**Response:**
```json
{
  "message": "All messages marked as read"
}
```

## WebSocket Endpoints

### Connect to WebSocket

```
WebSocket: /ws
```

Use SockJS for compatibility. Connect with a valid JWT token.

### Send a Message (WebSocket)

```
SEND /app/chat.send
```

**Message Body:**
```json
{
  "receiverId": 123,
  "content": "Hello, how are you?"
}
```

**Subscription to Receive Messages:**
```
SUBSCRIBE /user/{userId}/queue/messages
```

Where `{userId}` is your user ID.

**Received Message Format:**
```json
{
  "id": 456,
  "senderId": 789,
  "receiverId": 123,
  "content": "Hello, how are you?",
  "sentAt": "2023-07-15T14:30:45",
  "read": false,
  "sender": {
    "id": 789,
    "username": "sender_user",
    "name": "Sender Name",
    "userImage": "path/to/image.jpg",
    "email": "sender@example.com"
  },
  "receiver": {
    "id": 123,
    "username": "receiver_user",
    "name": "Receiver Name",
    "userImage": "path/to/image.jpg",
    "email": "receiver@example.com"
  }
}
```

### Delete a Message (WebSocket)

```
SEND /app/chat.delete
```

**Message Body:**
```json
123
```

The message body should contain only the messageId as a number.

**Subscription to Receive Deletion Events:**
```
SUBSCRIBE /user/{userId}/queue/messages
```

Where `{userId}` is your user ID.

**Received Deletion Event Format:**
```json
{
  "messageId": 123,
  "senderId": 789,
  "receiverId": 456,
  "eventType": "MESSAGE_DELETED"
}
```

## Example Usage (JavaScript with SockJS and STOMP)

```javascript
// Connect to WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

// Connect with JWT token
const headers = {
  'Authorization': 'Bearer your-jwt-token'
};

stompClient.connect(headers, function(frame) {
  console.log('Connected: ' + frame);

  // Subscribe to receive messages
  stompClient.subscribe('/user/' + userId + '/queue/messages', function(message) {
    const messageData = JSON.parse(message.body);

    // Check if it's a deletion event
    if (messageData.eventType === 'MESSAGE_DELETED') {
      console.log('Message deleted:', messageData.messageId);
      // Handle message deletion in UI
    } else {
      console.log('New message received:', messageData);
      // Handle new message in UI
    }
  });

  // Send a message
  stompClient.send("/app/chat.send", {}, JSON.stringify({
    receiverId: 123,
    content: "Hello, how are you?"
  }));

  // Delete a message
  stompClient.send("/app/chat.delete", {}, JSON.stringify(123)); // messageId
});
```

## REST API Example (Fetch API)

```javascript
// Send a message
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

// Get conversation with a user
async function getConversation(userId) {
  const response = await fetch(`/api/messages/conversation/${userId}`, {
    method: 'GET',
    headers: {
      'Authorization': 'Bearer your-jwt-token'
    }
  });

  return await response.json();
}

// Mark message as read
async function markAsRead(messageId) {
  await fetch(`/api/messages/messages/${messageId}/read`, {
    method: 'PUT',
    headers: {
      'Authorization': 'Bearer your-jwt-token'
    }
  });
}
```

## Notes for Frontend Implementation

1. Always handle WebSocket connection errors and reconnect as needed
2. Store the JWT token securely and include it in all requests
3. Update the UI in real-time when receiving WebSocket messages or deletion events
4. Consider implementing offline message queuing for better user experience
5. Implement read receipts using the mark-as-read endpoint
6. Use the unread count endpoint to show notification badges
