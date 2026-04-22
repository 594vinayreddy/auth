package com.ewallet.auth_service.service;

import com.ewallet.auth_service.dto.LoginRequest;
import com.ewallet.auth_service.dto.RegisterRequest;
import com.ewallet.auth_service.entity.User;
import com.ewallet.auth_service.repository.UserRepository;
import com.ewallet.auth_service.service.AuthService;
import com.ewallet.auth_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServicetest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("2712vinayreddy30@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(1L,"2712vinayreddy30@gmail.com", "USER")).thenReturn("mock-jwt-token");

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("2712vinayreddy30@gmail.com", response.getEmail());
        assertEquals("mock-jwt-token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("exsiting@test.com");
        request.setPassword("pass");

        when(userRepository.existsByEmail("exsiting@test.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldThrow_WhenPasswordWrong(){
        LoginRequest request = new LoginRequest();
        request.setEmail("2712vinayreddy30@gmail.com");
        request.setPassword("wrongpassword");

        User user = User.builder()
                .email("2712vinayreddy30@gmail.com")
                .password("encodedPassword")
                .role(User.Role.USER)
                .build();

        when(userRepository.findByEmail("2712vinayreddy30@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
