package com.library.backend.mapper;

import com.library.backend.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {
    Favorite selectById(Integer id);
    Favorite selectByUserAndBook(@Param("userId") Integer userId, @Param("bookId") Integer bookId);
    java.util.List<Favorite> selectByUserId(Integer userId);
    int insert(Favorite favorite);
    int deleteById(Integer id);
}
