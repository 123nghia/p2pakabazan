# WebSocket Client - Node.js Example

## Cài đặt dependencies

```bash
npm install sockjs-client stompjs axios
# hoặc
yarn add sockjs-client stompjs axios
```

## Code Example

### 1. Chat WebSocket Manager với Fallback Polling

```javascript
const SockJS = require('sockjs-client');
const Stomp = require('stompjs');
const axios = require('axios');

class ChatWebSocketManager {
    constructor(tradeId, apiBaseUrl = 'http://localhost:8080/api', wsBaseUrl = 'http://localhost:8081') {
        this.tradeId = tradeId;
        this.apiBaseUrl = apiBaseUrl;
        this.wsBaseUrl = wsBaseUrl;
        this.stompClient = null;
        this.pollingInterval = null;
        this.lastMessageTimestamp = null;
        this.isPolling = false;
        this.isConnected = false;
        this.messageHandlers = [];
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 3000; // 3 seconds
    }

    /**
     * Subscribe để nhận messages
     */
    onMessage(handler) {
        this.messageHandlers.push(handler);
    }

    /**
     * Connect WebSocket
     */
    connect() {
        if (this.isConnected) {
            console.log('Already connected');
            return;
        }

        const socket = new SockJS(`${this.wsBaseUrl}/ws`);
        this.stompClient = Stomp.over(socket);
        
        // Disable debug logs
        this.stompClient.debug = () => {};

        this.stompClient.connect({}, 
            () => {
                console.log('WebSocket connected');
                this.isConnected = true;
                this.reconnectAttempts = 0;
                this.stopPolling();
                this.subscribe();
            },
            (error) => {
                console.error('WebSocket connection error:', error);
                this.isConnected = false;
                this.fallbackToPolling();
            }
        );

        socket.onclose = () => {
            console.log('WebSocket closed');
            this.isConnected = false;
            this.fallbackToPolling();
        };

        socket.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.isConnected = false;
            this.fallbackToPolling();
        };
    }

    /**
     * Subscribe to chat topic
     */
    subscribe() {
        if (!this.stompClient || !this.isConnected) {
            return;
        }

        const topic = `/topic/trades/${this.tradeId}/chat`;
        this.stompClient.subscribe(topic, (message) => {
            try {
                const chatMessage = JSON.parse(message.body);
                this.lastMessageTimestamp = chatMessage.timestamp;
                this.notifyHandlers(chatMessage);
            } catch (error) {
                console.error('Error parsing message:', error);
            }
        });
        console.log(`Subscribed to ${topic}`);
    }

    /**
     * Fallback to polling khi WebSocket disconnect
     */
    fallbackToPolling() {
        if (this.isPolling) {
            return;
        }
        console.log('Falling back to polling...');
        this.isPolling = true;
        this.pollingInterval = setInterval(() => {
            this.fetchMessages(this.lastMessageTimestamp);
        }, 2000); // Poll every 2 seconds
    }

    /**
     * Stop polling
     */
    stopPolling() {
        if (this.pollingInterval) {
            clearInterval(this.pollingInterval);
            this.pollingInterval = null;
            this.isPolling = false;
            console.log('Polling stopped');
        }
    }

    /**
     * Fetch messages từ API (polling)
     */
    async fetchMessages(since) {
        try {
            let url = `${this.apiBaseUrl}/p2p/trades/${this.tradeId}/chat`;
            if (since) {
                const sinceISO = new Date(since).toISOString();
                url += `?since=${encodeURIComponent(sinceISO)}`;
            }

            const response = await axios.get(url, {
                headers: {
                    'Authorization': `Bearer ${this.getAuthToken()}` // Thêm token nếu cần
                }
            });

            const messages = response.data;
            if (messages && messages.length > 0) {
                messages.forEach(msg => {
                    this.lastMessageTimestamp = msg.timestamp;
                    this.notifyHandlers(msg);
                });
            }
        } catch (error) {
            console.error('Error fetching messages:', error.message);
        }
    }

    /**
     * Reconnect WebSocket
     */
    reconnect() {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.error('Max reconnect attempts reached');
            return;
        }

        this.reconnectAttempts++;
        console.log(`Reconnecting... (attempt ${this.reconnectAttempts})`);

        this.stopPolling();
        
        // Sync missed messages once
        if (this.lastMessageTimestamp) {
            this.fetchMessages(this.lastMessageTimestamp);
        }

        // Try WebSocket again after delay
        setTimeout(() => {
            this.connect();
        }, this.reconnectDelay);
    }

    /**
     * Send message
     */
    async sendMessage(message) {
        try {
            const response = await axios.post(
                `${this.apiBaseUrl}/p2p/trades/${this.tradeId}/chat`,
                { messages: message },
                {
                    headers: {
                        'Authorization': `Bearer ${this.getAuthToken()}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            return response.data;
        } catch (error) {
            console.error('Error sending message:', error.message);
            throw error;
        }
    }

    /**
     * Notify all handlers
     */
    notifyHandlers(message) {
        this.messageHandlers.forEach(handler => {
            try {
                handler(message);
            } catch (error) {
                console.error('Error in message handler:', error);
            }
        });
    }

    /**
     * Get auth token (implement theo cách bạn lưu token)
     */
    getAuthToken() {
        // TODO: Implement cách lấy token của bạn
        // Ví dụ: return localStorage.getItem('token') hoặc từ config
        return process.env.JWT_TOKEN || '';
    }

    /**
     * Disconnect
     */
    disconnect() {
        this.stopPolling();
        if (this.stompClient) {
            this.stompClient.disconnect(() => {
                console.log('WebSocket disconnected');
            });
            this.stompClient = null;
        }
        this.isConnected = false;
    }
}

module.exports = ChatWebSocketManager;
```

### 2. Notification WebSocket Manager

```javascript
const SockJS = require('sockjs-client');
const Stomp = require('stompjs');

class NotificationWebSocketManager {
    constructor(userId, wsBaseUrl = 'http://localhost:8081') {
        this.userId = userId;
        this.wsBaseUrl = wsBaseUrl;
        this.stompClient = null;
        this.isConnected = false;
        this.notificationHandlers = [];
    }

    onNotification(handler) {
        this.notificationHandlers.push(handler);
    }

    connect() {
        if (this.isConnected) {
            return;
        }

        const socket = new SockJS(`${this.wsBaseUrl}/ws`);
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = () => {};

        this.stompClient.connect({}, 
            () => {
                console.log('Notification WebSocket connected');
                this.isConnected = true;
                this.subscribe();
            },
            (error) => {
                console.error('Notification WebSocket error:', error);
                this.isConnected = false;
            }
        );
    }

    subscribe() {
        if (!this.stompClient || !this.isConnected) {
            return;
        }

        const topic = `/topic/users/${this.userId}/notifications`;
        this.stompClient.subscribe(topic, (message) => {
            try {
                const notification = JSON.parse(message.body);
                this.notifyHandlers(notification);
            } catch (error) {
                console.error('Error parsing notification:', error);
            }
        });
        console.log(`Subscribed to ${topic}`);
    }

    notifyHandlers(notification) {
        this.notificationHandlers.forEach(handler => {
            try {
                handler(notification);
            } catch (error) {
                console.error('Error in notification handler:', error);
            }
        });
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect(() => {
                console.log('Notification WebSocket disconnected');
            });
            this.stompClient = null;
        }
        this.isConnected = false;
    }
}

module.exports = NotificationWebSocketManager;
```

### 3. Usage Example

```javascript
const ChatWebSocketManager = require('./ChatWebSocketManager');
const NotificationWebSocketManager = require('./NotificationWebSocketManager');

// Chat Manager
const chatManager = new ChatWebSocketManager(
    'trade-uuid-here',
    'http://localhost:8080/api',  // API base URL
    'http://localhost:8081'        // WebSocket server URL
);

// Handle incoming messages
chatManager.onMessage((message) => {
    console.log('New message:', message);
    // Update UI, show notification, etc.
});

// Connect
chatManager.connect();

// Send message
chatManager.sendMessage('Hello, this is a test message')
    .then(response => {
        console.log('Message sent:', response);
    })
    .catch(error => {
        console.error('Failed to send message:', error);
    });

// Notification Manager
const notificationManager = new NotificationWebSocketManager(
    'user-uuid-here',
    'http://localhost:8081'
);

notificationManager.onNotification((notification) => {
    console.log('New notification:', notification);
    // Show toast, update badge, etc.
});

notificationManager.connect();

// Cleanup khi app shutdown
process.on('SIGINT', () => {
    chatManager.disconnect();
    notificationManager.disconnect();
    process.exit();
});
```

### 4. Express.js Integration Example

```javascript
const express = require('express');
const ChatWebSocketManager = require('./ChatWebSocketManager');

const app = express();
const chatManagers = new Map(); // Store managers per trade

// Middleware để lấy tradeId từ request
app.use('/trades/:tradeId/chat', (req, res, next) => {
    const tradeId = req.params.tradeId;
    
    // Get or create chat manager
    if (!chatManagers.has(tradeId)) {
        const manager = new ChatWebSocketManager(tradeId);
        manager.connect();
        chatManagers.set(tradeId, manager);
    }
    
    req.chatManager = chatManagers.get(tradeId);
    next();
});

// API endpoint để send message
app.post('/trades/:tradeId/chat', async (req, res) => {
    try {
        const result = await req.chatManager.sendMessage(req.body.message);
        res.json(result);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Cleanup khi trade kết thúc
app.post('/trades/:tradeId/close', (req, res) => {
    const tradeId = req.params.tradeId;
    const manager = chatManagers.get(tradeId);
    if (manager) {
        manager.disconnect();
        chatManagers.delete(tradeId);
    }
    res.json({ success: true });
});

app.listen(3000, () => {
    console.log('Server running on port 3000');
});
```

## Environment Variables

```bash
# .env file
API_BASE_URL=http://localhost:8080/api
WS_BASE_URL=http://localhost:8081
JWT_TOKEN=your-jwt-token-here
```

## Error Handling Best Practices

1. **Auto-reconnect**: Implement exponential backoff
2. **Connection state**: Track connection state để UI update
3. **Message queue**: Queue messages khi disconnect, send khi reconnect
4. **Health check**: Ping/pong để detect dead connections

## Testing

```javascript
// Test script
const ChatWebSocketManager = require('./ChatWebSocketManager');

const manager = new ChatWebSocketManager('test-trade-id');
manager.onMessage((msg) => {
    console.log('Received:', msg);
});

manager.connect();

// Test send after 2 seconds
setTimeout(() => {
    manager.sendMessage('Test message')
        .then(() => console.log('Sent successfully'))
        .catch(err => console.error('Send failed:', err));
}, 2000);

// Disconnect after 10 seconds
setTimeout(() => {
    manager.disconnect();
    process.exit();
}, 10000);
```

