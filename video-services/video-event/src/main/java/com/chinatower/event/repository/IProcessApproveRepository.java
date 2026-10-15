package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.ProcessApproveDO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;

/**
 * <p>
 * 事件审批表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-15
 */
public interface IProcessApproveRepository extends IService<ProcessApproveDO> {


    /**
     * 告警事件核实
     * @param dto
     * @return
     */
    Result submitOrderApproveInfo(SendAlarmOrderDTO dto);

    /**
     * 会签完成流转环节处理
     * @param dto
     * @return
     */
    Result signSubmitOrderApproveInfo(SendAlarmOrderDTO dto);
}
