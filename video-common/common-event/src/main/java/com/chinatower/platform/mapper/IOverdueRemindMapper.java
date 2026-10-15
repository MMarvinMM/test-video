package com.chinatower.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.dto.OverdueRemindDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IOverdueRemindMapper extends BaseMapper<OverdueRemindDto> {

    /**
     * 批量新增超期提醒数据
     * @param overdueRemindDtoList
     * @return
     */
    int insertOverdueRemindList(@Param("list") List<OverdueRemindDto> overdueRemindDtoList);

}
