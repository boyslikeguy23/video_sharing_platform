package org.example.final_project.security;

import org.example.final_project.models.User;
import org.example.final_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public WebSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("WebSocketAuthInterceptor: preSend called");

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Lấy token từ header
            List<String> authorization = accessor.getNativeHeader("Authorization");
            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0);
                // test log
                System.out.println("WebSocket CONNECT: token = " + token);

                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                try {
                    JwtTokenClaims claims = jwtTokenProvider.getClaimsFromToken(token);
                    String email = claims.getUsername();
                    
                    System.out.println("WebSocket connect: token = " + token);
                    System.out.println("WebSocket connect: email from token = " + email);

                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        final Integer userId = userOpt.get().getId();
                        System.out.println("WebSocket connect: userId = " + userId);

                        accessor.setUser(new Principal() {
                            @Override
                            public String getName() {
                                return userId.toString();
                            }
                        });
                    } else {
                        System.out.println("WebSocket connect: user not found for email " + email);
                        return null;
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi xác thực token: " + e.getMessage());
                    return null;
                }
            }
        }
        return message;
    }

}


