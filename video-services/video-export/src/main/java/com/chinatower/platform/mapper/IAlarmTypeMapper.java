package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeDTO;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 告警类型表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
public interface IAlarmTypeMapper extends BaseMapper<AlarmTypeDO> {

    /**
     * @Description 查询所有订购类型
     * @Param [body]
     **/
    List<AlarmTypeDO> findOrderAlarmAll(@Param("body") AlarmTypeDTO body);

    Integer devByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    int uavByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    Integer iotDevByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    Integer serviceByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    /**
     * 查询标签id
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @DS(TableEnum.PLATFORM)
    List<AlarmLableRelDTO> getAlarmLableByIds(@Param("idList")List<String> idList, @Param("waringOrderId") String waringOrderId);

    /**
     * 通过工单ID集合获取关联的告警标签
     * @param orderIdList
     * @return
     */
    @DS(TableEnum.PLATFORM)
    List<AlarmLableRelDTO> getAlarmLableByOrderIds(@Param("orderIdList")List<String> orderIdList);
}
