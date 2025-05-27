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
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Lấy token từ header
            List<String> authorization = accessor.getNativeHeader("Authorization");
            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0);
                // Xóa prefix "Bearer " nếu có
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                // Xác thực token
                try {
                    // Sử dụng getClaimsFromToken để verify và lấy thông tin từ token
                    JwtTokenClaims claims = jwtTokenProvider.getClaimsFromToken(token);
                    String email = claims.getUsername();

                    // Lấy user ID từ email
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        final Integer userId = userOpt.get().getId();

                        accessor.setUser(new Principal() {
                            @Override
                            public String getName() {
                                return userId.toString(); // Trả về ID thay vì email
                            }
                        });
                    } else {
                        // Không tìm thấy user
                        return null;
                    }
                } catch (Exception e) {
                    // Token không hợp lệ hoặc hết hạn
                    System.out.println("Lỗi xác thực token: " + e.getMessage());
                    return null;
                }
            }
        }
        return message;
    }

}


