package com.ewallet.auth_service.service;

import com.ewallet.auth_service.config.RabbitMQConfig;
import com.ewallet.auth_service.dto.AuthResponse;
import com.ewallet.auth_service.dto.LoginRequest;
import com.ewallet.auth_service.dto.RegisterRequest;
import com.ewallet.auth_service.entity.User;
import com.ewallet.auth_service.event.NotificationEvent;
import com.ewallet.auth_service.event.UserEvent;
import com.ewallet.auth_service.event.WalletEvent;
import com.ewallet.auth_service.repository.UserRepository;
import com.ewallet.auth_service.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RabbitTemplate rabbitTemplate;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(),user.getEmail(), user.getRole().name());

        UserEvent event = new UserEvent(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getDateOfBirth());
        WalletEvent walletEvent = new WalletEvent(user.getId(), user.getEmail());
        NotificationEvent notificationEvent = new NotificationEvent(user.getEmail(), user.getFirstName()+" "+user.getLastName());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.USER_ROUTING_KEY,
                event);
        log.info("Published to user service for  email: {}", event.getEmail());


        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.WALLET_ROUTING_KEY,
                walletEvent);
        log.info("Published to wallet service for email: {}", walletEvent.getEmail());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                notificationEvent);
        log.info("Published to notification service for email: {}", notificationEvent.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(),user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
