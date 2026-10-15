package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.dto.AddShareDTO;
import com.chinatower.event.entity.dto.AlarmShareDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 告警类型标签mapper
 * @author chenmf
 * @version 1.0
 * @date 2022/3/11 14:15
 */
@Mapper
public interface AlarmShareMapper extends BaseMapper<AlarmShareDTO> {

    /**
     * 插入告警类型标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    int addAlarmShare(AddShareDTO addShareDTO);


    AlarmShareDTO getByIds(@Param("shareId") String shareId);

}
