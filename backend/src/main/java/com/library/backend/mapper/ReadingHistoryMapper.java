package com.library.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.backend.entity.ReadingHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReadingHistoryMapper extends BaseMapper<ReadingHistory> {
}
