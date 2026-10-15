package com.chinatower.receive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.AlarmFlowConfigDO;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 19:10
 */
public interface IAlarmFlowConfigMapper extends BaseMapper<AlarmFlowConfigDO> {

    /**
     * 根据条件查询所有下一个流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryAllNextFlowLink(AlarmFlowConfigModel model);

    /**
     * 根据条件查询流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryFlowLink(AlarmFlowConfigModel model);
}
