package com.library.backend.mapper;

import com.library.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectById(Integer id);
    User selectByEmail(String email);
    java.util.List<User> selectList(@Param("search") String search);
    int insert(User user);
    int update(User user);
    int deleteById(Integer id);
    int countByEmail(String email);
}
