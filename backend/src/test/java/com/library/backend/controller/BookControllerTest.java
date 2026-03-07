package com.library.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.backend.entity.Book;
import com.library.backend.mapper.BookMapper;
import com.library.backend.mapper.FavoriteMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookMapper bookMapper;

    @MockBean
    private FavoriteMapper favoriteMapper;

    @Test
    @WithMockUser
    public void shouldReturnPaginatedBooks() throws Exception {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        
        List<Book> books = Collections.singletonList(book);
        
        when(bookMapper.selectList(any(), any())).thenReturn(books);

        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.list[0].title").value("Test Book"))
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.pageNum").exists());
    }
}
