package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.OrderLeaderAuditDO;
import com.chinatower.event.dto.OrderLeaderAuditDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
public interface IOrderLeaderAuditRepository extends IService<OrderLeaderAuditDO> {

    /**
     * 领导批示
     * @param dto
     * @return
     */
    int updateLeaderAudit(OrderLeaderAuditDO dto);

    /**
     * 转领导批示
     * @param dto
     * @return
     */
    Result addLeaderAudit(OrderLeaderAuditDTO dto);
}
