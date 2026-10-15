package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysRedis;
import com.chinatower.platform.project.system.domain.vo.SysRedisQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface SysRedisMapper {

    SysRedis selectById(Long id);

    List<SysRedis> selectList(SysRedisQuery query);

    int insert(SysRedis sysRedis);

}
