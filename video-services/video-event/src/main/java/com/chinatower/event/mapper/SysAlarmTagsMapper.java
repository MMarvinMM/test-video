package com.chinatower.event.mapper;

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
public interface SysAlarmTagsMapper extends BaseMapper<SysAlarmTagsDO> {

    /**
     * 获取全部告警类型标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    List<AlarmTagsTypeRelDTO> getAllTagsByTenantId(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 获取告警类型为可见的告警类型标签
     *
     * @return
     * @author tianhao
     * @date 2022/11/11
     * @version
     */
    List<AlarmTagsTypeRelDTO> getVisibleTags(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("appVerCode") String appVerCode);

    /**
     * 查询标签id是否存在
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    List<Long> getByIds(@Param("idList")List<Long> idList,@Param("tenantId") String tenantId);

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
