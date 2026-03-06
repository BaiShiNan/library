package com.library.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.backend.dto.AuthResponse;
import com.library.backend.dto.LoginRequest;
import com.library.backend.dto.RegisterRequest;
import com.library.backend.entity.User;
import com.library.backend.mapper.UserMapper;
import com.library.backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Check if user exists
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        var user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        if ("admin@example.com".equalsIgnoreCase(request.getEmail())) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        
        user.setAvatarUrl(request.getAvatarUrl());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, request.getEmail());
        var user = userMapper.selectOne(queryWrapper);
        if (user == null) {
             throw new RuntimeException("用户不存在");
        }
        
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }
}
