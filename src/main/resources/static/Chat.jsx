import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import './Chat.css';
import { BASE_URL } from '../../Config/api';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
axios.defaults.baseURL = BASE_URL;

const Chat = () => {
  const navigate = useNavigate();
  const { userId } = useParams();
  const [recentChats, setRecentChats] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [unreadCounts, setUnreadCounts] = useState({});
  const messagesEndRef = useRef(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const [showNewChatModal, setShowNewChatModal] = useState(false);
  const [suggestedUsers, setSuggestedUsers] = useState([]);

  // Get the JWT token from localStorage
  const getToken = () => {
    const token = localStorage.getItem('token');
    if (!token) return '';
    // If token already has "Bearer " prefix, return as is, otherwise add it
    return token.startsWith('Bearer ') ? token : `Bearer ${token}`;
  };

  // API call configuration with authorization header
  const apiConfig = {
    headers: {
      'Authorization': getToken(),
      'Content-Type': 'application/json'
    }
  };

  // Fetch recent chats
  const fetchRecentChats = async () => {
    try {
      const response = await axios.get('/api/messages/recent', apiConfig);
      setRecentChats(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching recent chats:', err);
      setError('Failed to load recent chats');
      setLoading(false);
    }
  };

  // Fetch unread message counts
  const fetchUnreadCounts = async () => {
    try {
      const response = await axios.get('/api/messages/unread/count', apiConfig);
      setUnreadCounts(response.data.unreadCountsBySender || {});
    } catch (err) {
      console.error('Error fetching unread counts:', err);
    }
  };

  // Fetch conversation with a specific user
  const fetchConversation = async (userId) => {
    try {
      const response = await axios.get(`/api/messages/conversation/${userId}`, apiConfig);
      setMessages(response.data);
      // Mark all messages as read
      await axios.put(`/api/messages/read-all/${userId}`, {}, apiConfig);
      // Update unread counts
      fetchUnreadCounts();
    } catch (err) {
      console.error('Error fetching conversation:', err);
      setError('Failed to load conversation');
    }
  };

  const fetchSuggestedUsers = async () => {
    const myId = localStorage.getItem('userId');
    if (!myId || myId === 'null' || isNaN(Number(myId))) {
      setSuggestedUsers([]);
      alert('Không tìm thấy userId. Vui lòng đăng nhập lại!');
      return;
    }
    // Lấy danh sách following
    const followingRes = await axios.get(`/api/users/${myId}/following`, apiConfig);
    // Lấy danh sách follower
    const followerRes = await axios.get(`/api/users/${myId}/follower`, apiConfig);
    // Gộp và loại trùng
    const all = [...followingRes.data, ...followerRes.data];
    const unique = [];
    const ids = new Set();
    for (const u of all) {
      if (!ids.has(u.id)) {
        unique.push(u);
        ids.add(u.id);
      }
    }
    setSuggestedUsers(unique);
  };

  // Send a message
  const sendMessage = async () => {
    if (!newMessage.trim() || !selectedUser) return;

    try {
      if (stompClient && stompClient.active) {
        // Send message via WebSocket
        const chatMessage = {
          receiverId: selectedUser.id,
          content: newMessage
        };

        stompClient.publish({
          destination: '/app/chat.send',
          body: JSON.stringify(chatMessage)
        });

        // Create a temporary message object for UI
        const currentUserId = parseInt(localStorage.getItem('userId'));
        const tempMessage = {
          id: Date.now(), // Temporary ID
          senderId: currentUserId,
          receiverId: selectedUser.id,
          content: newMessage,
          sentAt: new Date().toISOString(),
          read: false,
          sender: {
            id: currentUserId,
            name: localStorage.getItem('name') || '',
            username: localStorage.getItem('username') || '',
            userImage: localStorage.getItem('userImage') || ''
          },
          receiver: {
            id: selectedUser.id,
            name: selectedUser.name || '',
            username: selectedUser.username || '',
            userImage: selectedUser.userImage || ''
          }
        };

        // Add message to UI
        setMessages([...messages, tempMessage]);
        setNewMessage('');

        // Scroll to bottom
        scrollToBottom();
      } else {
        // If WebSocket is not connected, try to reconnect
        connectWebSocket();

        // Fallback to REST API
        const response = await axios.post('/api/messages/send', {
          receiverId: selectedUser.id,
          content: newMessage
        }, apiConfig);

        // Add the new message to the conversation
        setMessages([...messages, response.data]);
        setNewMessage('');

        // Scroll to bottom
        scrollToBottom();
      }
    } catch (err) {
      console.error('Error sending message:', err);
      setError('Failed to send message');
    }
  };

  // Delete a message
  const deleteMessage = async (messageId) => {
    try {
      await axios.delete(`/api/messages/messages/${messageId}`, apiConfig);
      // Remove the message from the UI
      setMessages(messages.filter(msg => msg.id !== messageId));
    } catch (err) {
      console.error('Error deleting message:', err);
      setError('Failed to delete message');
    }
  };

  // Delete a message using WebSocket
  const handleDeleteMessage = (messageId) => {
    if (stompClient && stompClient.active) {
      stompClient.publish({
        destination: "/app/chat.delete",
        body: JSON.stringify(messageId)
      });
      console.log('Delete message request sent via WebSocket');
    } else {
      console.error('WebSocket not connected');
      // Fallback to REST API
      deleteMessage(messageId);
    }
  };

  // Select a user to chat with
  const selectUser = (user) => {
    setSelectedUser(user);
    fetchConversation(user.id);

    // Ensure WebSocket connection is active
    if (!stompClient || !stompClient.active) {
      connectWebSocket();
    }

    // Update URL without reloading the page
    navigate(`/chat/${user.id}`);
    localStorage.setItem('userId', user.id);
  };

  // Start a new chat
  const startNewChat = () => {
    setShowNewChatModal(true);
    fetchSuggestedUsers();
  };

  const closeNewChatModal = () => {
    setShowNewChatModal(false);
  };

  // Scroll to bottom of messages
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // Connect to WebSocket
  const connectWebSocket = () => {
    // Disconnect if already connected
    if (stompClient) {
      stompClient.deactivate();
    }

    // Get the JWT token
    const token = getToken();
    if (!token) {
      console.error('No token found');
      return;
    }

    // Create SockJS and STOMP client
    const socket = new SockJS(`${BASE_URL}/ws`);
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        'Authorization': token.replace(/^Bearer\s/, '')
      },
      debug: function (str) {
        // console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // Set up connection handlers
    client.onConnect = (frame) => {
      console.log('Connected to WebSocket');
      setStompClient(client);

      // Subscribe to user-specific messages
      client.subscribe('/user/queue/messages', message => {
        const messageData = JSON.parse(message.body);
        console.log('Received message:', messageData);

        // Check if this is a message deletion event
        if (messageData.eventType === 'MESSAGE_DELETED') {
          // Remove the deleted message from the UI
          setMessages(prevMessages => prevMessages.filter(msg => msg.id !== messageData.messageId));
          return;
        }

        // Add message to UI if it's from the selected user
        if (selectedUser && messageData.senderId === selectedUser.id) {
          setMessages(prevMessages => [...prevMessages, messageData]);

          // Scroll to bottom
          scrollToBottom();
        } else {
          // Update unread counts if message is not from selected user
          fetchUnreadCounts();
        }
      });
    };

    client.onStompError = (frame) => {
      console.error('STOMP error:', frame);
      setError('Failed to connect to WebSocket');
    };

    // Activate the client
    client.activate();
  };

  // Check WebSocket connection status and reconnect if necessary
  const checkWebSocketConnection = () => {
    if (!stompClient || !stompClient.active) {
      console.log('WebSocket disconnected, reconnecting...');
      connectWebSocket();
    }
  };

  // Initial load
  useEffect(() => {
    fetchRecentChats();
    fetchUnreadCounts();
    connectWebSocket();

    // If userId is provided in URL, load that conversation
    if (userId && userId !== 'new') {
      // Find the user in recent chats
      const interval = setInterval(() => {
        if (recentChats.length > 0) {
          const user = recentChats.find(u => u.id === parseInt(userId));
          if (user) {
            setSelectedUser(user);
            fetchConversation(user.id);
            clearInterval(interval);
          }
        }
      }, 500);

      // Clear interval after 5 seconds if user not found
      setTimeout(() => clearInterval(interval), 5000);
    }

    // Check WebSocket connection every 30 seconds
    const connectionCheckInterval = setInterval(checkWebSocketConnection, 30000);

    return () => {
      // Disconnect WebSocket when component unmounts
      if (stompClient) {
        stompClient.deactivate();
      }
      clearInterval(connectionCheckInterval);
    };
  }, [userId]);

  // Scroll to bottom when messages change
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Handle Enter key press to send message
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      sendMessage();
    }
  };

  // Render timestamp in a readable format
  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  // New Chat Modal
  const NewChatModal = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);

    const searchUsers = async () => {
      if (!searchTerm.trim()) return;
      try {
        const response = await axios.get(`/api/users/search?q=${searchTerm}`, apiConfig);
        setSearchResults(response.data);
      } catch (err) {
        setSearchResults([]);
      }
    };

    // If not searching, show suggestedUsers, if searching, show searchResults
    const displayUsers = searchTerm.trim() ? searchResults : suggestedUsers;

    return (
      <div className="modal-overlay">
        <div className="modal-content">
          <div className="modal-header">
            <span>Tin nhắn mới</span>
            <button className="close-btn" onClick={closeNewChatModal}>×</button>
          </div>
          <div className="modal-search">
            <input
              type="text"
              placeholder="Tìm kiếm..."
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && searchUsers()}
            />
            <button onClick={searchUsers}>Tìm</button>
          </div>
          <div className="modal-suggest-list">
            {displayUsers.map(user => (
              <div
                key={user.id}
                className="modal-user-item"
                onClick={() => {
                  setShowNewChatModal(false);
                  selectUser(user);
                }}
              >
                <img
                  src={
                    (user.userImage || user.image)
                      ? (user.userImage || user.image).startsWith('http')
                        ? (user.userImage || user.image)
                        : `${BASE_URL.replace(/\/$/, '')}/${(user.userImage || user.image).replace(/^\//, '')}`
                      : 'https://res.cloudinary.com/mxtungfinalproject/image/upload/v1749298887/default_avatar_pb0sdc.jpg'
                  }
                  alt={user.username}
                  onError={e => { e.target.onerror = null; e.target.src = 'https://res.cloudinary.com/mxtungfinalproject/image/upload/v1749298887/default_avatar_pb0sdc.jpg'; }}
                />
                <div>
                  <div className="username">{user.username}</div>
                  <div className="name">{user.name}</div>
                </div>
              </div>
            ))}
            {displayUsers.length === 0 && (
              <div style={{ padding: 16, color: '#888' }}>Không có gợi ý</div>
            )}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="chat-container">
      {/* Left sidebar with recent chats */}
      <div className="sidebar">
        <div className="sidebar-header">
          <h2>Messages</h2>
          <button className="new-chat-btn" onClick={startNewChat}>
            <i className="fa fa-edit"></i>
          </button>
        </div>

        {loading ? (
          <div className="loading">Loading...</div>
        ) : error ? (
          <div className="error">{error}</div>
        ) : (
          <div className="recent-chats">
            {recentChats.map(user => (
              <div 
                key={user.id} 
                className={`chat-item ${selectedUser?.id === user.id ? 'active' : ''}`}
                onClick={() => selectUser(user)}
              >
                <div className="avatar-container">
                  <img
                    src={
                      (user.userImage || user.image)
                        ? (user.userImage || user.image).startsWith('http')
                          ? (user.userImage || user.image)
                          : `${BASE_URL.replace(/\/$/, '')}/${(user.userImage || user.image).replace(/^\//, '')}`
                        : 'https://res.cloudinary.com/mxtungfinalproject/image/upload/v1749298887/default_avatar_pb0sdc.jpg'
                    }
                    alt={user.username}
                    onError={e => { e.target.onerror = null; e.target.src = 'https://res.cloudinary.com/mxtungfinalproject/image/upload/v1749298887/default_avatar_pb0sdc.jpg'; }}
                  />
                  {unreadCounts[user.id] > 0 && (
                    <span className="unread-badge">{unreadCounts[user.id]}</span>
                  )}
                </div>
                <div className="user-info">
                  <span className="username">{user.username}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Right side with conversation */}
      <div className="conversation">
        {selectedUser ? (
          <>
            <div className="conversation-header">
              <img
                src={
                  (selectedUser.userImage || selectedUser.image)
                    ? (selectedUser.userImage || selectedUser.image).startsWith('http')
                      ? (selectedUser.userImage || selectedUser.image)
                      : `${BASE_URL.replace(/\/$/, '')}/${(selectedUser.userImage || selectedUser.image).replace(/^\//, '')}`
                    : 'https://res.cloudinary.com/mxtungfinalproject/image/upload/v1749298887/default_avatar_pb0sdc.jpg'
                }
                alt={selectedUser.username}
                onError={e => { e.target.onerror = null; e.target.src = 'https://res.cloudinary.com/mxtungfinalproject/image/upload/v1749298887/default_avatar_pb0sdc.jpg'; }}
              />
              <span className="username">{selectedUser.username}</span>
            </div>

            <div className="messages-container">
              {messages.map(message => (
                <div 
                  key={message.id} 
                  className={`message ${message.senderId === parseInt(localStorage.getItem('userId')) ? 'sent' : 'received'}`}
                >
                  <div className="message-content">
                    {message.content}
                    <span className="message-time">{formatTime(message.sentAt)}</span>
                    {message.senderId === parseInt(localStorage.getItem('userId')) && (
                      <button 
                        className="delete-btn" 
                        onClick={() => handleDeleteMessage(message.id)}
                      >
                        <i className="fa fa-trash"></i>
                      </button>
                    )}
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>

            <div className="message-input">
              <input
                type="text"
                placeholder="Message..."
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                onKeyPress={handleKeyPress}
              />
              <button onClick={sendMessage}>Send</button>
            </div>
          </>
        ) : userId === 'new' ? (
          <NewChatModal />
        ) : (
          <div className="no-conversation">
            <p>Select a conversation or start a new one</p>
          </div>
        )}
      </div>
      {showNewChatModal && <NewChatModal />}
    </div>
  );
};

export default Chat;
