# Instagram-like Chat Component

This is a React component that implements Instagram-like direct messaging functionality. It allows users to send messages, view conversation history, see recent chats, mark messages as read, delete messages, and start new conversations.

## Features

- Display recent chats with unread message counts
- View conversation history with a selected user
- Send messages to users
- Delete messages (sender only)
- Mark messages as read automatically when viewing a conversation
- Start new conversations by searching for users
- Real-time updates with polling
- Responsive design for mobile and desktop

## Installation

1. Place the `Chat.jsx` and `Chat.css` files in your React project.
2. Make sure you have the required dependencies installed:
   - React
   - React Router
   - Axios

```bash
npm install react react-router-dom axios
```

3. Configure the base URL in the Chat.jsx file to match your backend server:

```javascript
// In Chat.jsx
axios.defaults.baseURL = 'http://localhost:8080'; // Change this to your backend URL
```

This ensures that all API requests are sent to the correct server. By default, it's set to `http://localhost:8080` which is the typical port for a Spring Boot application.

## Usage

### Integration with React Router

Add the Chat component to your routes in your main router configuration:

```jsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Chat from './path/to/Chat';

function App() {
  return (
    <Router>
      <Routes>
        {/* Other routes */}
        <Route path="/chat" element={<Chat />} />
        <Route path="/chat/:userId" element={<Chat />} />
        {/* Other routes */}
      </Routes>
    </Router>
  );
}

export default App;
```

### Authentication

The component expects a JWT token to be stored in localStorage under the key 'token'. Make sure your authentication system sets this token when a user logs in.

```jsx
// Example login function
const login = async (username, password) => {
  try {
    const response = await axios.post('/api/auth/login', { username, password });
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('userId', response.data.userId);
    // Redirect to home or dashboard
  } catch (error) {
    console.error('Login failed:', error);
  }
};
```

### Navigation to Chat

You can navigate to the chat component from anywhere in your application:

```jsx
import { useNavigate } from 'react-router-dom';

function SomeComponent() {
  const navigate = useNavigate();

  // Navigate to chat with a specific user
  const openChatWithUser = (userId) => {
    navigate(`/chat/${userId}`);
  };

  // Navigate to the general chat page
  const openChat = () => {
    navigate('/chat');
  };

  // Navigate to start a new chat
  const startNewChat = () => {
    navigate('/chat/new');
  };

  return (
    <div>
      <button onClick={openChat}>Messages</button>
      <button onClick={() => openChatWithUser(123)}>Chat with User 123</button>
      <button onClick={startNewChat}>New Message</button>
    </div>
  );
}
```

## API Requirements

The component expects the following API endpoints to be available:

- `GET /api/messages/recent` - Get recent chats
- `GET /api/messages/conversation/{userId}` - Get conversation with a specific user
- `POST /api/messages/send` - Send a message
- `DELETE /api/messages/messages/{messageId}` - Delete a message
- `PUT /api/messages/read-all/{userId}` - Mark all messages from a user as read
- `GET /api/messages/unread/count` - Get unread message counts
- `GET /api/users/search?query={searchTerm}` - Search for users

All API requests include an Authorization header with the JWT token.

## Customization

You can customize the appearance of the chat component by modifying the `Chat.css` file. The CSS is organized into sections for different parts of the component:

- Chat container
- Sidebar
- Conversation area
- Messages
- Input area
- New chat form
- Responsive adjustments

## Dependencies

- React 16.8+ (for hooks)
- React Router 6+
- Axios for API requests
- Font Awesome (for icons)

## Browser Support

The component is compatible with all modern browsers:
- Chrome
- Firefox
- Safari
- Edge

## License

This component is available for use under the MIT License.
