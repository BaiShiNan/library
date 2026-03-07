package com.library.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private LocalDateTime createdAt;
}
