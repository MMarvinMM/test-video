package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.dto.OverdueRemindDto;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OverdueRemindMapper extends BaseMapper<OverdueRemindDto> {

    /**
     * 查询要发送的超期/受理数据
     * @param
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<OverdueRemindDto> getOverdueRemindList(@Param("remindTime") String remindTime);
}
