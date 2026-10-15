package com.chinatower.event.mapper;

import com.chinatower.event.dataobject.SysAlarmReasonRelDO;
import com.chinatower.event.entity.dto.AlarmReasonQueryDTO;
import com.chinatower.event.entity.vo.AlarmReasonVO;
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
public interface SysAlarmReasonMapper {

    List<AlarmReasonVO> listByCondition(@Param("param") AlarmReasonQueryDTO param);

    List<AlarmReasonVO> queryAlarmReason(@Param("param") AlarmReasonQueryDTO param);

    int insertBatch(@Param("list") List<SysAlarmReasonRelDO> list);

    int updateByCondition(@Param("param") SysAlarmReasonRelDO param);

    int deleteByCondition(@Param("typeValue") String typeValue, @Param("deleteIdList") List<Long> deleteIdList,
                          @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    int countByCondition(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                         @Param("typeValue") String typeValue);

    int countExistReasonName(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                             @Param("typeValue") String typeValue, @Param("reasonList") List<String> reasonList,
                             @Param("excludeReasonIdList") List<Long> excludeReasonIdList);
}