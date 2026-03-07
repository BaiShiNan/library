package com.library.backend.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.backend.entity.Book;
import com.library.backend.entity.User;
import com.library.backend.mapper.BookMapper;
import com.library.backend.mapper.FavoriteMapper;
import com.library.backend.mapper.ReadingHistoryMapper;
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
import java.util.Map;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;
    private final ReadingHistoryMapper readingHistoryMapper;

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
                String fileName = saveFile(cover, "covers");
                book.setCoverUrl("/document/" + fileName);
            }

            if (file != null && !file.isEmpty()) {
                String fileName = saveFile(file, "books");
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

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable Integer id,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("description") String description,
            @RequestParam("isbn") String isbn,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            if (file != null && file.getSize() > 500 * 1024 * 1024) {
                 return ResponseEntity.badRequest().body("文件大小超过限制 (500MB)");
            }

            book.setTitle(title);
            book.setAuthor(author);
            book.setDescription(description);
            book.setIsbn(isbn);
            book.setCategoryId(categoryId);

            if (cover != null && !cover.isEmpty()) {
                String fileName = saveFile(cover, "covers");
                book.setCoverUrl("/document/" + fileName);
            }

            if (file != null && !file.isEmpty()) {
                 String fileName = saveFile(file, "books");
                 book.setFileUrl("/document/" + fileName);
            }

            bookMapper.update(book);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
             e.printStackTrace();
             return ResponseEntity.internalServerError().body("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/books/{id}")
    @CacheEvict(value = "book", key = "#id")
    public ResponseEntity<?> deleteBook(@PathVariable Integer id) {
        Book book = bookMapper.selectById(id);
        if (book != null) {
            deleteBookFiles(book);
            // Delete related records manually to avoid FK constraint issues
            favoriteMapper.deleteByBookId(id);
            readingHistoryMapper.deleteByBookId(id);
            bookMapper.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/books/batch-delete")
    @CacheEvict(value = "book", allEntries = true)
    public ResponseEntity<?> deleteBooks(@RequestBody Map<String, List<Integer>> payload) {
        List<Integer> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("ID列表不能为空");
        }
        try {
            for (Integer id : ids) {
                Book book = bookMapper.selectById(id);
                if (book != null) {
                    deleteBookFiles(book);
                    // Delete related records manually to avoid FK constraint issues
                    favoriteMapper.deleteByBookId(id);
                    readingHistoryMapper.deleteByBookId(id);
                    bookMapper.deleteById(id);
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("批量删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String search) {
        PageHelper.startPage(page, size);
        List<User> users = userMapper.selectList(search);
        return ResponseEntity.ok(new PageInfo<>(users));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            favoriteMapper.deleteByUserId(id);
            readingHistoryMapper.deleteByUserId(id);
            userMapper.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/users/batch-delete")
    public ResponseEntity<?> deleteUsers(@RequestBody Map<String, List<Integer>> payload) {
        List<Integer> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("ID列表不能为空");
        }
        try {
            for (Integer id : ids) {
                favoriteMapper.deleteByUserId(id);
                readingHistoryMapper.deleteByUserId(id);
                userMapper.deleteById(id);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("批量删除失败: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subDir);
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
        return subDir + "/" + fileName;
    }

    private void deleteBookFiles(Book book) {
        try {
            if (book.getCoverUrl() != null && book.getCoverUrl().startsWith("/document/")) {
                String relativePath = book.getCoverUrl().replace("/document/", "");
                // Prevent directory traversal
                if (!relativePath.contains("..")) {
                     Path path = Paths.get(uploadDir, relativePath);
                     Files.deleteIfExists(path);
                }
            }
            if (book.getFileUrl() != null && book.getFileUrl().startsWith("/document/")) {
                String relativePath = book.getFileUrl().replace("/document/", "");
                if (!relativePath.contains("..")) {
                    Path path = Paths.get(uploadDir, relativePath);
                    Files.deleteIfExists(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
