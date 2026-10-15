package com.chinatower.platform.repository;


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
     * 查询标签id对应的告警类型
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    List<AlarmTagsTypeRelDTO> getAlarmTypeByIds(List<String> idList, String tenantId);
}
