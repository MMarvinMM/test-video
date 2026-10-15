package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.SysAlarmTagsTypeRelDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
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
public interface SysAlarmTagsTypeRelMapper extends BaseMapper<SysAlarmTagsTypeRelDO> {
    
    /**
     * 告警类型对应标签查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    List<AlarmTagsTypeRelDTO> getTagsByAlarmType(@Param("alarmTypeId") String alarmTypeId,@Param("list") List<String> alarmTypeIdList,@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 根据告警类型编码查询告警类型标签关联信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param typeValueList 告警类型编码列表
     * @return 告警类型标签信息
     */
    List<AlarmTypeTagInfoVO> selectRelateTagInfoByAlarmType(@Param("tenantId") String tenantId,
                                                            @Param("industryCode") String industryCode,
                                                            @Param("typeValueList")List<String> typeValueList,
                                                            @Param("dataScope")String dataScope);

    /**
     * 根据客户信息查询告警类型标签信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @return 告警类型标签信息
     */
    List<AlarmTypeTagInfoVO> selectTagInfoByTenant(@Param("tenantId") String tenantId,
                                                   @Param("industryCode") String industryCode);

    /**
     * @Description 删除关联标签
     * @author peng.haibo
     * @date 2022/9/16
     * @param alarmTypeId
     * @param tenantId 参数描述
     * @return int
     */
    int deleteTagsTypeRel(@Param("alarmTypeId") String alarmTypeId, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * @Description 批量删除关联标签
     * @author peng.haibo
     * @date 2022/9/16
     * @param alarmTypeIdList
     * @param tenantId 参数描述
     * @return int
     */
    int deleteTagsTypeRelBatch(@Param("list") List<String> alarmTypeIdList, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);
}
