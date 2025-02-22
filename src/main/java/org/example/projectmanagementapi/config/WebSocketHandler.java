package org.example.projectmanagementapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.projectmanagementapi.entity.Notification;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
  private final Set<WebSocketSession> socketSessions = new HashSet<>();
  private final ObjectMapper objectMapper;

  public WebSocketHandler() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    socketSessions.add(session);
    System.out.println("New WebSocket connection: " + session.getId());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    socketSessions.remove(session);
    System.out.println("WebSocket closed: " + session.getId());
  }

  public void sendNotification(Notification notification) throws IOException {
    String notificationJson = objectMapper.writeValueAsString(notification);
    for (WebSocketSession session : socketSessions) {
      if (session.isOpen()) {
        session.sendMessage(new TextMessage(notificationJson));
      }
    }
  }
}
