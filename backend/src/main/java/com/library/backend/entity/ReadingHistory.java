package com.library.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReadingHistory {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private BigDecimal progress;
    private LocalDateTime lastReadAt;
}
