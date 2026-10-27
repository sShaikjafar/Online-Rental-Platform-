package com.rental.platform.websocket;

import com.rental.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        String token = request.getURI().getQuery();
        if (token != null && token.startsWith("token=")) {
            token = token.substring(6);
            try {
                String email = jwtUtil.extractEmail(token);
                if (email != null) {
                    attributes.put("userEmail", email);
                    return true;
                }
            } catch (Exception e) {
                System.err.println("WebSocket auth failed: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
    }
}