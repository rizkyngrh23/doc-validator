package com.egov.docvalidation.controller;

import com.egov.docvalidation.entity.User;
import com.egov.docvalidation.service.UserService;
import com.egov.docvalidation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        String role = payload.getOrDefault("role", "USER");
        User user = User.builder()
                .email(email)
                .passwordHash(password)
                .role(User.Role.valueOf(role.toUpperCase()))
                .build();
        userService.saveUser(user);
        return "User registered";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        return userService.findByEmail(email)
                .filter(user -> new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(password, user.getPasswordHash()))
                .map(user -> {
                    String token = jwtUtil.generateToken(String.valueOf(user.getUserId()), user.getEmail(), user.getRole().name());
                    Map<String, String> resp = new HashMap<>();
                    resp.put("token", token);
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
}
