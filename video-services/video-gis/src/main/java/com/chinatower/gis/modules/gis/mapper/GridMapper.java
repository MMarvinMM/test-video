package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: wjd
 * @Description:
 * @Date: Created in 15:55 2022/3/14
 * @Modified by:
 */
public interface GridMapper {

    @DS("postgres")
    String gridCompress();

    @DS("postgres")
    int updateChouxi(@Param("gridRange")String gridRange);

}
