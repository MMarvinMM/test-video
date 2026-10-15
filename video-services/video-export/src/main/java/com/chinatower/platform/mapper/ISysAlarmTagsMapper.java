package com.chinatower.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.SysAlarmTagsDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 告警类型标签mapper
 * @author chenmf
 * @version 1.0
 * @date 2022/3/11 14:15
 */
@Mapper
public interface ISysAlarmTagsMapper extends BaseMapper<SysAlarmTagsDO> {

    /**
     * 查询标签id是否存在
     *
     * @return
     * @author sutn
     * @date 2022/5/10
     * @version
     */
    List<AlarmTagsTypeRelDTO> getAlarmTypeByIds(@Param("idList")List<String> idList, @Param("tenantId") String tenantId);
}
