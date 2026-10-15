package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.entity.admin.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface RadarTargetFollowDAO {

    /**
     * 查询设置的阈值
     * @param configKey
     * @return
     */
    @DS("base")
    SysConfig getSysConfig(@Param("configKey")String configKey);

    /**
     * 根据时间删除
     * @param expireCreateTime
     * @return
     */
    @DS("base")
    int deleteByExpireCreateTime(@Param("expireCreateTime") Date expireCreateTime);
}
