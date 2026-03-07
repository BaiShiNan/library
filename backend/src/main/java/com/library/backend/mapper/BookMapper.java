package com.library.backend.mapper;

import com.library.backend.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    Book selectById(Integer id);
    List<Book> selectList(@Param("categoryId") Integer categoryId, @Param("search") String search);
    List<Book> selectBatchIds(@Param("ids") List<Integer> ids);
    int insert(Book book);
    int update(Book book);
    int deleteById(Integer id);
}
