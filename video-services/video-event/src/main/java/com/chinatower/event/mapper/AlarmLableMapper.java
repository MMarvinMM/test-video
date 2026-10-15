package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.AlarmLableDO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
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
public interface AlarmLableMapper extends BaseMapper<AlarmLableDO> {

    /**
     * 获取全部告警类型标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getAllTagsByTenantId(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 查询标签id是否存在
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<Long> getByIds(@Param("idList")List<Long> idList,@Param("tenantId") String tenantId);

    /**
     * 查询标签名称查询标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableDO> getByLableNames(@Param("nameList")List<String> nameList,@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);
}
