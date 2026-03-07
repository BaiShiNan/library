package com.library.backend.mapper;

import com.library.backend.entity.ReadingHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReadingHistoryMapper {
    ReadingHistory selectById(Integer id);
    ReadingHistory selectByUserAndBook(@Param("userId") Integer userId, @Param("bookId") Integer bookId);
    List<ReadingHistory> selectByUserId(Integer userId);
    int insert(ReadingHistory history);
    int update(ReadingHistory history);
    int deleteById(Integer id);
    int deleteByBookId(Integer bookId);
    int deleteByUserId(Integer userId);
}
