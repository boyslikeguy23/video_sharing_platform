# Guide to Testing Chat Feature with Postman

This guide explains how to test both the REST API endpoints and WebSocket functionality of the chat feature using Postman.

## Prerequisites

1. [Postman](https://www.postman.com/downloads/) installed on your computer
2. The application running on your local machine (default port: 5454)

## Part 1: Testing REST API Endpoints

### Step 1: Authentication

Before testing the chat endpoints, you need to authenticate and get a JWT token:

1. Create a new request in Postman:
   - Method: GET
   - URL: `http://localhost:5454/signin`
   - Auth: Basic Auth
     - Username: [your email]
     - Password: [your password]

2. Send the request. If successful, you'll receive a user object in the response.

3. In the response headers, look for the `Authorization` header. This contains your JWT token.

4. Copy this token (without the "Bearer " prefix if it's included) for use in subsequent requests.

### Step 2: Testing Chat REST Endpoints

#### Send a Message

1. Create a new request:
   - Method: POST
   - URL: `http://localhost:5454/api/messages/send`
   - Headers:
     - Authorization: Bearer [your JWT token]
   - Body (raw JSON):
     ```json
     {
       "receiverId": 2,
       "content": "Hello, this is a test message!"
     }
     ```

2. Send the request. If successful, you'll receive the created message object in the response.

#### Get Conversation

1. Create a new request:
   - Method: GET
   - URL: `http://localhost:5454/api/messages/conversation/{userId}`
     - Replace `{userId}` with the ID of the user you want to see the conversation with
   - Headers:
     - Authorization: Bearer [your JWT token]

2. Send the request. If successful, you'll receive a list of messages between you and the specified user.

#### Get Recent Chats

1. Create a new request:
   - Method: GET
   - URL: `http://localhost:5454/api/messages/recent`
   - Headers:
     - Authorization: Bearer [your JWT token]

2. Send the request. If successful, you'll receive a list of users you've recently chatted with.

#### Mark Message as Read

1. Create a new request:
   - Method: PUT
   - URL: `http://localhost:5454/api/messages/messages/{messageId}/read`
     - Replace `{messageId}` with the ID of the message you want to mark as read
   - Headers:
     - Authorization: Bearer [your JWT token]

2. Send the request. If successful, you'll receive a 200 OK response.

#### Delete Message

1. Create a new request:
   - Method: DELETE
   - URL: `http://localhost:5454/api/messages/messages/{messageId}`
     - Replace `{messageId}` with the ID of the message you want to delete
   - Headers:
     - Authorization: Bearer [your JWT token]

2. Send the request. If successful, you'll receive a 200 OK response.

#### Get Unread Messages

1. Create a new request:
   - Method: GET
   - URL: `http://localhost:5454/api/messages/unread`
   - Headers:
     - Authorization: Bearer [your JWT token]

2. Send the request. If successful, you'll receive a list of unread messages.

#### Get Unread Message Counts

1. Create a new request:
   - Method: GET
   - URL: `http://localhost:5454/api/messages/unread/count`
   - Headers:
     - Authorization: Bearer [your JWT token]

2. Send the request. If successful, you'll receive the total count of unread messages and counts per sender.

#### Mark All Messages as Read

1. Create a new request:
   - Method: PUT
   - URL: `http://localhost:5454/api/messages/read-all/{userId}`
     - Replace `{userId}` with the ID of the user whose messages you want to mark as read
   - Headers:
     - Authorization: Bearer [your JWT token]

2. Send the request. If successful, you'll receive a 200 OK response with a message.

## Part 2: Testing WebSocket Functionality

Testing WebSockets in Postman requires using the WebSocket Request feature, which is available in recent versions of Postman.

### Step 1: Create a WebSocket Request

1. In Postman, click on "New" and select "WebSocket Request"

2. Enter the WebSocket URL: `ws://localhost:5454/ws`

3. Click "Connect" to establish the WebSocket connection

### Step 2: Add Connection Headers

Before connecting, you need to add the Authorization header:

1. Go to the "Headers" tab in your WebSocket request
2. Add a new header:
   - Key: Authorization
   - Value: Bearer [your JWT token]

### Step 3: Subscribe to User-Specific Messages

After connecting, you need to subscribe to your user-specific message queue:

1. In the "Message" field, enter a STOMP frame to subscribe:
   ```
   SUBSCRIBE
   id: sub-0
   destination: /user/queue/messages


   ```
   (Note: The two empty lines at the end are important)

2. Click "Send" to subscribe to your message queue

### Step 4: Send a Message

To send a message to another user:

1. In the "Message" field, enter a STOMP frame to send a message:
   ```
   SEND
   destination: /app/chat.send
   content-type: application/json

   {"receiverId": 2, "content": "Hello via WebSocket!"}
   ```
   (Note: The empty line before the JSON payload is important)

2. Click "Send" to send the message

### Step 5: Receive Messages

When someone sends you a message, you'll see it appear in the "Messages" panel of your WebSocket request.

## Troubleshooting

### Common Issues with WebSockets in Postman

1. **Connection Refused**: Make sure the application is running and the WebSocket endpoint is correctly configured.

2. **Authentication Failed**: Ensure your JWT token is valid and correctly formatted in the Authorization header.

3. **STOMP Frame Format**: STOMP frames are sensitive to formatting. Make sure you have empty lines in the right places.

4. **SockJS Support**: The application uses SockJS, which might require additional configuration in Postman.

### Testing with Alternative Tools

If you encounter issues with Postman's WebSocket support, consider these alternatives:

1. **Included HTML Client**: This project includes a simple HTML client for testing WebSocket chat functionality. Access it at:
   ```
   http://localhost:5454/websocket_test_client.html
   ```
   This client provides a user-friendly interface for:
   - Authenticating with email/password
   - Connecting to WebSocket
   - Viewing recent chats
   - Sending and receiving messages in real-time

2. **WebSocket King Client**: A Chrome extension specifically designed for testing WebSockets.

3. **wscat**: A command-line tool for testing WebSockets.

## Conclusion

This guide covers the basics of testing both REST API endpoints and WebSocket functionality for the chat feature. For more complex scenarios or automated testing, consider using specialized tools or writing custom test scripts.
