package com.library.backend.controller;

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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        System.out.println("Fetching history for user: " + user.getId());
        
        List<ReadingHistory> history = readingHistoryMapper.selectByUserId(user.getId());
        System.out.println("Found history records: " + history.size());
        
        List<Integer> bookIds = history.stream()
                                       .map(ReadingHistory::getBookId)
                                       .collect(Collectors.toList());
        
        if (bookIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        
        Map<Integer, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        List<Book> sortedBooks = bookIds.stream()
                .map(bookMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedBooks);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Book>> getFavorites(@AuthenticationPrincipal User user) {
        List<Favorite> favorites = favoriteMapper.selectByUserId(user.getId());
        
        List<Integer> bookIds = favorites.stream()
                                         .map(Favorite::getBookId)
                                         .collect(Collectors.toList());

        if (bookIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<Book> books = bookMapper.selectBatchIds(bookIds);

        Map<Integer, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        List<Book> sortedBooks = bookIds.stream()
                .map(bookMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedBooks);
    }
}
