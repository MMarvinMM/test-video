package com.chinatower.event.repository;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.event.dataobject.AlarmLableDO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;

import java.util.List;

/**
 * <p>
 * 告警类型标签表 服务类
 * </p>
 *
 * @author chenmf
 * @since 2022-04-26
 */
public interface IAlarmLableRepository extends IService<AlarmLableDO> {

    /**
     * 查询告警类型标签数量
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    int countTagName(AlarmLableDO alarmLableDO);

    /**
     * 获取全部告警类型标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getAllTagsByTenantId(String tenantId, String industryCode);

    /**
     * 标签个数
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    int countTag(AlarmLableDO alarmLableDO);

    /**
     * 查询标签id是否存在
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<Long> getByIds(List<Long> idList, String tenantId);

}
