package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.vo.AlarmLevelVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS(EbikeDataBaseConstant.BASE)
public interface AlarmTypeLevelMapper extends BaseMapper<AlarmTypeLevelDO> {

    /**
     * 批量删除告警类型等级
     * @param tenantId 客户ID-客户视角人工上报告警类型用到
     * @param industryCode 行业编码-客户视角人工上报告警类型用到
     * @param typeValueList 告警类型编码集合
     */
    void deleteAlarmTypeLevelBatch(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("typeValueList") List<String> typeValueList);

    /**
     * 批量新增告警类型等级
     * @param list 告警类型等级DTO
     */
    void addAlarmTypeLevelBatch(@Param("list") List<AlarmTypeLevelDO> list);

    /**
     * 根据告警类型编码查询告警等级
     * @param body 告警类型编码
     * @return 告警等级
     */
    AlarmLevelVO getAlarmLevelByTypeValue(@Param("body") AlarmTypeLevelDO body);

    /**
     * 新增告警类型等级
     * @param body 告警类型等级DO对象
     * @return 新增结果
     */
    int insertAlarmLevel(@Param("body") AlarmTypeLevelDO body);

    /**
     * 根据告警类型编码修改告警等级
     * @param body 告警类型编码
     * @return 修改结果
     */
    int updateAlarmLevelByTypeValue(@Param("body") AlarmTypeLevelDO body);
}
