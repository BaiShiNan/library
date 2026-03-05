package com.library.backend.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserMapper userMapper;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            LambdaQueryWrapper<com.library.backend.entity.User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(com.library.backend.entity.User::getEmail, username);
            com.library.backend.entity.User user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return user;
        };
    }
}
