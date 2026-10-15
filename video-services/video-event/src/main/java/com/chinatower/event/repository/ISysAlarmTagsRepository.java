package com.chinatower.event.repository;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.event.dataobject.SysAlarmTagsDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;

import java.util.List;

/**
 * <p>
 * 告警类型标签表 服务类
 * </p>
 *
 * @author chenmf
 * @since 2022-04-26
 */
public interface ISysAlarmTagsRepository extends IService<SysAlarmTagsDO> {

    /**
     * 查询告警类型标签数量
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    int countTagName(SysAlarmTagsDO sysAlarmTagsDO);

    /**
     * 获取全部告警类型标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    List<AlarmTagsTypeRelDTO> getAllTagsByTenantId(String tenantId, String industryCode);

    /**
     * 获取告警类型为可见的告警类型标签
     *
     * @return
     * @author tianhao
     * @date 2022/11/11
     * @version
     */
    List<AlarmTagsTypeRelDTO> getVisibleTags(String tenantId, String industryCode, String appVerCode);

    /**
     * 标签个数
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    int countTag(String tenantId, String industryCode);

    /**
     * 查询标签id是否存在
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    List<Long> getByIds(List<Long> idList, String tenantId);

    /**
     * 查询标签id对应的告警类型
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    List<AlarmTagsTypeRelDTO> getAlarmTypeByIds(List<String> idList, String tenantId);
}
