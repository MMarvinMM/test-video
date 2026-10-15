package com.chinatower.event.repository;


import com.chinatower.event.entity.dto.AddLableDTO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.DeleteLableDTO;
import com.chinatower.event.entity.dto.ModifyLableDTO;

import java.util.List;

/**
 * 告警标签业务接口
 * @author yhb
 * @version 1.0
 * @date 2022/12/29 11:22
 */
public interface IAlarmLableService {

    /**
     * 告警标签新增
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    void addLable(AddLableDTO addLableDTO);

    /**
     * 告警类型标签修改
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    void modifyLable(ModifyLableDTO modifyLableDTO);

    /**
     * 告警标签删除
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    void deleteLable(DeleteLableDTO deleteLableDTO);

    /**
     * 获取全部告警标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getAllTagsByTenantId();


}
