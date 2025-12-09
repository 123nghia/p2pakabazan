# Plan: Tách riêng WebSocket Server + Fallback Polling

## Mục tiêu
Tách riêng WebSocket server thành một microservice độc lập (`p2p_websocket`) để xử lý tất cả real-time messaging, kết hợp với fallback polling để đảm bảo reliability.

## Kiến trúc mới

### Module Structure
```
p2p_service/      → Business logic → Publish events → RabbitMQ
p2p_notification/ → Notification DB (giữ nguyên, chỉ lưu DB)
p2p_websocket/    → WebSocket server → Listen RabbitMQ → Push to clients
p2p_p2p/          → API server (hỗ trợ polling fallback)
```

### Event Flow qua RabbitMQ
- **Trade Events**: `TradeStatusEvent` (đã có)
- **Chat Events**: `ChatMessageEvent` (mới)
- **Notification Events**: `NotificationEvent` (mới)

## Sơ đồ luồng (Flow Diagram)

### 1. Chat Message Flow (WebSocket Primary)
```
Client (Buyer) 
    ↓
TradeChatController.sendMessage()
    ↓
TradeChatServiceImpl.sendMessage()
    ↓
Save to DB (TradeChat)
    ↓
Publish ChatMessageEvent → RabbitMQ (exchange: websocket.events, routing: chat.message)
    ↓
p2p_websocket: ChatEventListener
    ↓
Push via WebSocket → /topic/trades/{tradeId}/chat
    ↓
Clients (Buyer + Seller) receive real-time
```

### 2. Chat Message Flow (Polling Fallback)
```
Client (WebSocket disconnected)
    ↓
Detect disconnect → Start polling interval (2-3s)
    ↓
GET /p2p/trades/{tradeId}/chat?since={lastMessageTimestamp}
    ↓
TradeChatController.getMessages() with since param
    ↓
TradeChatServiceImpl.getMessages(tradeId, since)
    ↓
Filter messages after timestamp
    ↓
Return new messages
    ↓
Client receives messages
    ↓
When WebSocket reconnects → Stop polling → Sync missed messages once
```

### 3. System Message Flow
```
TradeServiceImpl.appendSystemMessage()
    ↓
Save to DB (TradeChat with senderId=null)
    ↓
Publish ChatMessageEvent → RabbitMQ (routing: chat.system)
    ↓
p2p_websocket: ChatEventListener
    ↓
Push via WebSocket → /topic/trades/{tradeId}/chat
    ↓
Clients (Buyer/Seller based on recipientRole) receive real-time
```

### 4. Notification Flow
```
NotificationServiceImpl.notifyUser() / notifyUsers()
    ↓
Save to DB (Notification)
    ↓
Publish NotificationEvent → RabbitMQ (routing: notification.new)
    ↓
p2p_websocket: NotificationEventListener
    ↓
Push via WebSocket → /topic/users/{userId}/notifications
    ↓
Client receives real-time (toast + badge update)
```

### 5. Trade Status Flow (đã có, giữ nguyên)
```
TradeServiceImpl → TradeEventPublisher
    ↓
Publish TradeStatusEvent → RabbitMQ (routing: trade.events.status)
    ↓
p2p_websocket: TradeEventListener (move từ p2p_notification)
    ↓
Push via WebSocket → /topic/trades/{tradeId} + /topic/users/{userId}/trades
    ↓
Clients receive real-time
```

## Sequence Diagram

### Chat Message với Fallback Polling
```
┌──────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────┐
│Client│     │Controller│     │ Service  │     │ RabbitMQ │     │WebSocket │     │Client│
└──┬───┘     └────┬─────┘     └────┬─────┘     └────┬─────┘     └────┬─────┘     └──┬───┘
   │             │                 │                 │                 │             │
   │ POST /chat  │                 │                 │                 │             │
   ├────────────>│                 │                 │                 │             │
   │             │ sendMessage()   │                 │                 │             │
   │             ├────────────────>│                 │                 │             │
   │             │                 │ Save to DB      │                 │             │
   │             │                 ├─────────────────┤                 │             │
   │             │                 │                 │                 │             │
   │             │                 │ Publish Event   │                 │             │
   │             │                 ├────────────────>│                 │             │
   │             │                 │                 │                 │             │
   │             │                 │                 │ Queue Event     │             │
   │             │                 │                 ├────────────────>│             │
   │             │                 │                 │                 │             │
   │             │                 │                 │                 │ Push WS     │
   │             │                 │                 │                 ├─────────────>│
   │             │                 │                 │                 │             │
   │<────────────┼─────────────────┼─────────────────┼─────────────────┼─────────────┤
   │ 200 OK      │                 │                 │                 │             │
   │             │                 │                 │                 │             │
   │ (WebSocket disconnected)      │                 │                 │             │
   │             │                 │                 │                 │             │
   │ GET /chat?since=...           │                 │                 │             │
   ├────────────>│                 │                 │                 │             │
   │             │ getMessages()  │                 │                 │             │
   │             ├────────────────>│                 │                 │             │
   │             │                 │ Query DB        │                 │             │
   │             │                 ├─────────────────┤                 │             │
   │<────────────┼─────────────────┼─────────────────┼─────────────────┼─────────────┤
   │ 200 OK      │                 │                 │                 │             │
   │ (messages)  │                 │                 │                 │             │
```

## Các thay đổi cần thực hiện

### 1. Tạo module mới: `p2p_websocket`
**Files cần tạo:**
- `p2p_websocket/pom.xml` - Maven module
- `p2p_websocket/src/main/java/com/akabazan/websocket/WebSocketApplication.java` - Spring Boot app
- `p2p_websocket/src/main/java/com/akabazan/websocket/config/WebSocketConfig.java` - WebSocket config
- `p2p_websocket/src/main/java/com/akabazan/websocket/listener/TradeEventListener.java` - Move từ p2p_notification
- `p2p_websocket/src/main/java/com/akabazan/websocket/listener/ChatEventListener.java` - Mới
- `p2p_websocket/src/main/java/com/akabazan/websocket/listener/NotificationEventListener.java` - Mới
- `p2p_websocket/src/main/resources/application.properties` - Config

**Dependencies:**
- `spring-boot-starter-websocket`
- `spring-boot-starter-amqp` (RabbitMQ)
- `p2p_common` (cho events/DTOs)

### 2. Tạo Event Classes trong `p2p_common`
**Files cần tạo:**
- `p2p_common/src/main/java/com/akabazan/common/event/ChatMessageEvent.java`
  - Fields: `tradeId`, `chatId`, `senderId`, `message`, `timestamp`, `recipientRole`, `isSystemMessage`
- `p2p_common/src/main/java/com/akabazan/common/event/NotificationEvent.java`
  - Fields: `userId`, `notificationId`, `type`, `message`, `timestamp`

### 3. Cập nhật `p2p_service`
**Files cần sửa:**
- `TradeChatServiceImpl.java`: 
  - Thêm publish `ChatMessageEvent` sau khi save
  - Inject `ChatEventPublisher`
- `TradeServiceImpl.java`: 
  - Thêm publish `ChatMessageEvent` trong `appendSystemMessage()`
  - Inject `ChatEventPublisher`
- Tạo `ChatEventPublisher` (tương tự `TradeEventPublisher`)
  - File: `p2p_service/src/main/java/com/akabazan/service/event/ChatEventPublisher.java`

### 4. Cập nhật `p2p_notification`
**Files cần sửa:**
- `NotificationServiceImpl.java`: 
  - Thêm publish `NotificationEvent` sau khi save
  - Inject `NotificationEventPublisher`
- Tạo `NotificationEventPublisher`
  - File: `p2p_notification/src/main/java/com/akabazan/notification/event/NotificationEventPublisher.java`
- **Xóa** `TradeEventListener.java` (move sang p2p_websocket)

### 5. Cập nhật API để hỗ trợ Polling Fallback
**Files cần sửa:**

- `p2p_p2p/src/main/java/com/akabazan/api/controller/TradeChatController.java`:
  ```java
  @GetMapping("/{tradeId}/chat")
  public ResponseEntity<List<TradeChatResponse>> getMessages(
      @PathVariable UUID tradeId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since
  ) {
      List<TradeChatResult> messages = tradeChatService.getMessages(tradeId, since);
      return ResponseEntity.ok(TradeChatResponseMapper.fromList(messages));
  }
  ```

- `p2p_service/src/main/java/com/akabazan/service/TradeChatService.java`:
  ```java
  List<TradeChatResult> getMessages(UUID tradeId, LocalDateTime since);
  ```

- `p2p_service/src/main/java/com/akabazan/service/impl/TradeChatServiceImpl.java`:
  - Implement method với `since` parameter
  - Nếu `since == null`: trả về tất cả messages (behavior hiện tại)
  - Nếu `since != null`: filter messages có `timestamp > since`

- `p2p_repository/src/main/java/com/akabazan/repository/TradeChatRepository.java`:
  ```java
  List<TradeChat> findByTradeIdAndTimestampAfterOrderByTimestampAsc(
      UUID tradeId, 
      LocalDateTime since
  );
  ```

### 6. RabbitMQ Configuration
**Exchange & Queues:**
- Exchange: `websocket.events.exchange` (topic)
- Queues:
  - `websocket.trade.events.queue` - Trade status events
  - `websocket.chat.messages.queue` - Chat messages (user + system)
  - `websocket.notifications.queue` - Notifications

**Routing Keys:**
- `trade.events.status` - Trade status changes
- `chat.message` - User chat messages
- `chat.system` - System messages
- `notification.new` - New notifications

## WebSocket Topics (giữ nguyên pattern)

### Trade Events
- `/topic/trades` - Broadcast tất cả trade events
- `/topic/trades/{tradeId}` - Events cho trade cụ thể
- `/topic/users/{userId}/trades` - Events cho user cụ thể

### Chat Messages
- `/topic/trades/{tradeId}/chat` - Chat messages cho trade cụ thể

### Notifications
- `/topic/users/{userId}/notifications` - Notifications cho user cụ thể

## Frontend Implementation (Client-side)

### WebSocket Connection Manager
```javascript
class ChatWebSocketManager {
  constructor(tradeId) {
    this.tradeId = tradeId;
    this.ws = null;
    this.pollingInterval = null;
    this.lastMessageTimestamp = null;
    this.isPolling = false;
  }
  
  connect() {
    // Connect WebSocket
    this.ws = new SockJS('/ws');
    this.ws.onopen = () => {
      this.subscribe(`/topic/trades/${this.tradeId}/chat`);
      this.stopPolling(); // Stop polling if active
    };
    this.ws.onmessage = (msg) => {
      const chatMessage = JSON.parse(msg.data);
      this.lastMessageTimestamp = chatMessage.timestamp;
      this.handleMessage(chatMessage);
    };
    this.ws.onerror = () => this.fallbackToPolling();
    this.ws.onclose = () => this.fallbackToPolling();
  }
  
  fallbackToPolling() {
    if (this.isPolling) return;
    this.isPolling = true;
    this.pollingInterval = setInterval(() => {
      this.fetchMessages(this.lastMessageTimestamp);
    }, 2000); // Poll every 2 seconds
  }
  
  stopPolling() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
      this.isPolling = false;
    }
  }
  
  async fetchMessages(since) {
    const url = since 
      ? `/api/p2p/trades/${this.tradeId}/chat?since=${since.toISOString()}`
      : `/api/p2p/trades/${this.tradeId}/chat`;
    const response = await fetch(url);
    const messages = await response.json();
    messages.forEach(msg => this.handleMessage(msg));
  }
  
  reconnect() {
    this.stopPolling();
    // Sync missed messages once
    if (this.lastMessageTimestamp) {
      this.fetchMessages(this.lastMessageTimestamp);
    }
    this.connect(); // Try WebSocket again
  }
}
```

## Deployment Architecture

### Port Configuration
- `p2p_p2p`: Port 8080 (API server)
- `p2p_websocket`: Port 8081 (WebSocket server)
- `p2p_notification`: Port 8082 (Notification service - optional)

### Environment Variables
```properties
# p2p_websocket/application.properties
server.port=8081
spring.rabbitmq.host=${RABBITMQ_HOST}
spring.rabbitmq.port=${RABBITMQ_PORT}
app.rabbitmq.websocket.exchange=websocket.events.exchange
app.rabbitmq.websocket.trade.queue=websocket.trade.events.queue
app.rabbitmq.websocket.chat.queue=websocket.chat.messages.queue
app.rabbitmq.websocket.notification.queue=websocket.notifications.queue
```

## Component Diagram

```
┌─────────────────┐
│   p2p_p2p       │
│  (API Server)   │
│   Port: 8080    │
│                 │
│  - REST API     │
│  - Polling      │
│    Fallback     │
└────────┬────────┘
         │
         │ HTTP/REST
         │
┌────────▼────────┐
│  p2p_service    │
│ (Business Logic)│
└────────┬────────┘
         │
         │ Publish Events
         │
┌────────▼────────┐
│   RabbitMQ      │
│  (Message Bus)  │
└────────┬────────┘
         │
         │ Subscribe Events
         │
┌────────▼────────┐
│ p2p_websocket   │
│ (WebSocket)     │
│  Port: 8081     │
└────────┬────────┘
         │
         │ WebSocket
         │
┌────────▼────────┐
│    Clients      │
│  (Browser/App)  │
│                 │
│  - WebSocket    │
│    (Primary)    │
│  - Polling      │
│    (Fallback)   │
└─────────────────┘
```

## Migration Steps
1. Tạo module `p2p_websocket`
2. Move `TradeEventListener` từ `p2p_notification` sang `p2p_websocket`
3. Tạo event classes mới trong `p2p_common`
4. Tạo publishers trong `p2p_service` và `p2p_notification`
5. Cập nhật services để publish events
6. Cập nhật API để hỗ trợ `since` parameter
7. Cập nhật repository để query với timestamp
8. Test từng flow (WebSocket + Polling fallback)
9. Deploy `p2p_websocket` service
10. Update client để connect tới WebSocket server mới + implement fallback

## Testing Strategy
1. Test WebSocket connection từ client
2. Test chat message flow end-to-end (WebSocket)
3. Test polling fallback khi WebSocket disconnect
4. Test sync missed messages khi reconnect
5. Test system message flow
6. Test notification flow
7. Test trade status event flow (đã có)
8. Load test WebSocket server với nhiều connections
9. Test với network interruption (simulate disconnect)

## Lợi ích của kiến trúc mới
- **Tách biệt trách nhiệm**: WebSocket server chỉ lo real-time messaging
- **Scale độc lập**: Scale WebSocket riêng khi cần
- **Dễ maintain**: Code WebSocket tập trung một nơi
- **Reliability cao**: Fallback polling đảm bảo không mất messages
- **UX tốt**: Real-time khi có WebSocket, vẫn hoạt động khi disconnect

## Độ phức tạp
- **Backend**: Trung bình (thêm module mới + API update)
- **Frontend**: Trung bình (WebSocket + fallback logic)
- **Tổng thể**: Trung bình - đáng làm vì tăng reliability đáng kể

