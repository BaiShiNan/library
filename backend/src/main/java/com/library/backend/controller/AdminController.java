package com.library.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.entity.Book;
import com.library.backend.entity.User;
import com.library.backend.mapper.BookMapper;
import com.library.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/books")
    public ResponseEntity<?> createBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("description") String description,
            @RequestParam("isbn") String isbn,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setDescription(description);
            book.setIsbn(isbn);
            book.setCategoryId(categoryId);
            book.setCreatedAt(LocalDateTime.now());
            book.setRating(BigDecimal.valueOf(0)); // Default rating

            if (cover != null && !cover.isEmpty()) {
                String fileName = saveFile(cover);
                book.setCoverUrl("/document/" + fileName);
            }

            if (file != null && !file.isEmpty()) {
                String fileName = saveFile(file);
                book.setFileUrl("/document/" + fileName);
            }

            bookMapper.insert(book);
            return ResponseEntity.ok(book);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer id) {
        bookMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = new Page<>(page, size);
        IPage<User> result = userMapper.selectPage(userPage, new QueryWrapper<>());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 使用简单的时间戳文件名，避免 URL 编码问题
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}
