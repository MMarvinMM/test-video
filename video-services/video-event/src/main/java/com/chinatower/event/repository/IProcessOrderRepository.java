package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.ProcessOrderDO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
public interface IProcessOrderRepository extends IService<ProcessOrderDO> {

    /**
     * 告警事件处理(处置)
     * @param dto
     * @return
     */
    Result submitProcessOrder(SendAlarmOrderDTO dto);

    /**
     * 获取处置单详情
     * @param dto
     * @return
     */
    Result getProcessDetail(ProcessOrderDTO dto);

    /**
     * 流程回退
     * @param dto
     * @return
     */
    Result rollBack(SendAlarmOrderDTO dto);
}
