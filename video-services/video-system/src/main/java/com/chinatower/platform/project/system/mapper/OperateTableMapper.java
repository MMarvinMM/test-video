package com.chinatower.platform.project.system.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperateTableMapper {

    @Select("${sql}")
    String executeSelectString(@Param("sql") String sql);

    @Select("${sql}")
    List<Map<String,Object>> executeSelectList(@Param("sql") String sql);

    @Select("${sql}")
    Map<String,Object> executeSelectMap(@Param("sql") String sql);

}
