package com.library.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.entity.Book;
import com.library.backend.entity.Favorite;
import com.library.backend.entity.ReadingHistory;
import com.library.backend.entity.User;
import com.library.backend.mapper.BookMapper;
import com.library.backend.mapper.FavoriteMapper;
import com.library.backend.mapper.ReadingHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final ReadingHistoryMapper readingHistoryMapper;
    private final FavoriteMapper favoriteMapper;
    private final BookMapper bookMapper;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Book>> getReadingHistory(@AuthenticationPrincipal User user) {
        LambdaQueryWrapper<ReadingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingHistory::getUserId, user.getId())
                    .orderByDesc(ReadingHistory::getLastReadAt);
        List<ReadingHistory> history = readingHistoryMapper.selectList(queryWrapper);
        
        List<Integer> bookIds = history.stream()
                                       .map(ReadingHistory::getBookId)
                                       .collect(Collectors.toList());
        
        if (bookIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Book>> getFavorites(@AuthenticationPrincipal User user) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, user.getId())
                    .orderByDesc(Favorite::getCreatedAt);
        List<Favorite> favorites = favoriteMapper.selectList(queryWrapper);
        
        List<Integer> bookIds = favorites.stream()
                                         .map(Favorite::getBookId)
                                         .collect(Collectors.toList());

        if (bookIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<Book> books = bookMapper.selectBatchIds(bookIds);
        return ResponseEntity.ok(books);
    }
}
