package com.library.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.io.Serializable;

@Data
@TableName("books")
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String author;
    private String isbn;
    private Integer categoryId;
    private String description;
    private String coverUrl;
    private String fileUrl;
    private BigDecimal rating;
    private Integer pageCount;
    private LocalDate publishDate;
    private LocalDateTime createdAt;
}
