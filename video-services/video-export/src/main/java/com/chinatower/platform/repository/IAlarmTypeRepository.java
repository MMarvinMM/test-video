package com.chinatower.platform.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeDTO;
import com.chinatower.event.dataobject.AlarmTypeDO;

import java.util.List;

/**
 * <p>
 * 告警类型表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
public interface IAlarmTypeRepository extends IService<AlarmTypeDO> {

    /**
     * @Description 查询租户行业下所有订购类型
     * @Param [body]
     **/
    public List<AlarmTypeDO> findOrderAlarmAll(AlarmTypeDTO body);

    /**
     * 查询客户的告警来源
     * @param body
     * @return
     */
    List<String> findAlarmSource(AlarmSourceSubTypeDTO body);

    /**
     * 查询标签id对应的告警
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getAlarmLableByIds(List<String> idList, String waringOrderId);

}
