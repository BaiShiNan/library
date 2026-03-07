package com.library.backend.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.backend.entity.Book;
import com.library.backend.entity.User;
import com.library.backend.mapper.BookMapper;
import com.library.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;

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
            // Check file size manually if needed, though Spring handles it
            if (file != null && file.getSize() > 500 * 1024 * 1024) {
                 return ResponseEntity.badRequest().body("文件大小超过限制 (500MB)");
            }

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setDescription(description);
            book.setIsbn(isbn);
            book.setCategoryId(categoryId);
            book.setCreatedAt(LocalDateTime.now());
            book.setRating(BigDecimal.valueOf(0)); // Default rating
            book.setPageCount(0); // Default page count
            book.setPublishDate(java.time.LocalDate.now()); // Default publish date
            
            // Set default fileUrl to avoid NOT NULL constraint if file is missing (though front-end checks it)
            // But we will set it below.
            book.setFileUrl("");

            if (cover != null && !cover.isEmpty()) {
                String fileName = saveFile(cover);
                book.setCoverUrl("/document/" + fileName);
            }

            if (file != null && !file.isEmpty()) {
                String fileName = saveFile(file);
                book.setFileUrl("/document/" + fileName);
            } else {
                return ResponseEntity.badRequest().body("图书文件不能为空");
            }

            bookMapper.insert(book);
            return ResponseEntity.ok(book);
        } catch (org.springframework.dao.DuplicateKeyException e) {
             return ResponseEntity.badRequest().body("上传失败：ISBN 已存在");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
             e.printStackTrace();
             return ResponseEntity.internalServerError().body("系统错误 (" + e.getClass().getSimpleName() + "): " + e.getMessage());
        }
    }

    @DeleteMapping("/books/{id}")
    @CacheEvict(value = "book", key = "#id")
    public ResponseEntity<?> deleteBook(@PathVariable Integer id) {
        bookMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        PageHelper.startPage(page, size);
        List<User> users = userMapper.selectList();
        return ResponseEntity.ok(new PageInfo<>(users));
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
