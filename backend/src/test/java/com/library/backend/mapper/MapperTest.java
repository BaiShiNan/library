package com.library.backend.mapper;

import com.library.backend.entity.Book;
import com.library.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Test
    public void testUserMapper() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPasswordHash("hash");
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        assertNotNull(user.getId());

        User found = userMapper.selectById(user.getId());
        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());

        User foundByEmail = userMapper.selectByEmail("test@example.com");
        assertNotNull(foundByEmail);
        assertEquals(user.getId(), foundByEmail.getId());

        List<User> list = userMapper.selectList(null);
        assertFalse(list.isEmpty());
    }

    @Test
    public void testBookMapper() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setIsbn("1234567890");
        book.setFileUrl("url");
        book.setCreatedAt(LocalDateTime.now());
        book.setPublishDate(LocalDate.now());
        book.setRating(BigDecimal.ZERO);
        book.setPageCount(100);

        bookMapper.insert(book);
        assertNotNull(book.getId());

        Book found = bookMapper.selectById(book.getId());
        assertNotNull(found);
        assertEquals("Test Book", found.getTitle());

        List<Book> list = bookMapper.selectList(null, "Test");
        assertFalse(list.isEmpty());
    }

    @Test
    public void benchmark() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setName("User " + i);
            user.setPasswordHash("hash");
            userMapper.insert(user);
        }
        long end = System.currentTimeMillis();
        System.out.println("Inserted 100 users in " + (end - start) + "ms");
    }
}
