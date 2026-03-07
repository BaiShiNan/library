package com.library.backend.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.backend.entity.Book;
import com.library.backend.mapper.BookMapper;
import com.library.backend.mapper.FavoriteMapper;
import com.library.backend.entity.Favorite;
import com.library.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookMapper bookMapper;
    private final FavoriteMapper favoriteMapper;

    @GetMapping
    public ResponseEntity<PageInfo<Book>> getBooks(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        PageHelper.startPage(page, limit);
        List<Book> books = bookMapper.selectList(categoryId, search);
        return ResponseEntity.ok(new PageInfo<>(books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(
            @PathVariable Integer id,
            @AuthenticationPrincipal User user
    ) {
        Favorite favorite = favoriteMapper.selectByUserAndBook(user.getId(), id);
        
        if (favorite != null) {
            favoriteMapper.deleteById(favorite.getId());
            return ResponseEntity.ok("已取消收藏");
        } else {
            Favorite newFavorite = new Favorite();
            newFavorite.setUserId(user.getId());
            newFavorite.setBookId(id);
            newFavorite.setCreatedAt(LocalDateTime.now());
            favoriteMapper.insert(newFavorite);
            return ResponseEntity.ok("已添加到收藏");
        }
    }
}
