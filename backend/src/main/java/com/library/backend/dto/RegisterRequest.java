package com.library.backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String account;
    private String password;
    private String name;
    private String avatarUrl;
}
