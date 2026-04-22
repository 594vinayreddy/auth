package com.ewallet.auth_service.Controller;

import com.ewallet.auth_service.dto.AuthResponse;
import com.ewallet.auth_service.dto.LoginRequest;
import com.ewallet.auth_service.dto.RegisterRequest;
import com.ewallet.auth_service.repository.UserRepository;
import com.ewallet.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
        }
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email or password");
        }
        return ResponseEntity.ok(authService.login(request));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody LoginRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        } else {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                user.setPassword(request.getPassword());
                userRepository.save(user);
            });
            return ResponseEntity.ok("Password updated successfully");
        }
    }
}

