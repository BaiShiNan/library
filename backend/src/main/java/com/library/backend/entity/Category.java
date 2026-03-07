package com.library.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Integer id;
    private String name;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
