package com.icia.Taeumproject.util;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketHandler {  
  //웹 소켓 연결
  void afterConnectionEstablished(WebSocketSession session) throws Exception;  

  //양방향 데이터 통신
  void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception;  

  //소켓 통신 에러
  void handleTransportError(WebSocketSession session, Throwable exception) throws Exception;  

  //소켓 연결 종료
  void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception;  

  //부분 메시지를 지원하는지 여부
  boolean supportsPartialMessages();  
}