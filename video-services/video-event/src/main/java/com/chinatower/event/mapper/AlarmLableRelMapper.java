package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.AlarmLableRelDO;
import com.chinatower.event.entity.dto.AddLableRelationDTO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 告警类型标签关联mapper
 * @author chenmf
 * @version 1.0
 * @date 2022/3/11 14:15
 */
@Mapper
public interface AlarmLableRelMapper extends BaseMapper<AlarmLableRelDO> {

    /**
     * 告警对应标签查询
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getTagsByWaringId(@Param("waringOrderId") String waringOrderId);

    /**
     * @Description 删除关联标签
     * @author yanghb
     * @date 2022/12/29
     * @param waringOrderId
     * @return int
     */
    int deleteAlarmLableRel(@Param("waringOrderId") String waringOrderId);

    /**
     * @Description 删除关联标签
     * @param addLableRelationDTO
     * @return int
     */
    int deleteLableRelBatch(AddLableRelationDTO addLableRelationDTO);

    /**
     * 查询标签id
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getAlarmLableByIds(@Param("idList")List<String> idList,@Param("waringOrderId") String waringOrderId);

    /**
     * 根据告警集合查询标签id
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getAlarmLableByOrdId(@Param("ordIdList")List<String> ordIdList);
}
