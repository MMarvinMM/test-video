package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.OrderLeaderAuditDO;
import com.chinatower.event.dto.OrderLeaderAuditDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
public interface OrderLeaderAuditMapper extends BaseMapper<OrderLeaderAuditDO> {

    /**
     * 获取指定环节领导批示列表
     * @param map
     * @return
     */
    List<Map> getLeaderListByFlow(Map map);

    /**
     * 领导批示详情
     *
     * @param auditId
     * @return
     */
    Map getLeaderAuditInfo(String auditId);

    /**
     * 领导批示
     *
     * @param orderLeaderAuditDO
     * @return
     */
    int updateLeaderAudit(OrderLeaderAuditDO orderLeaderAuditDO);

    /**
     * 领导批示表工单状态修改
     *
     * @param orderLeaderAuditDO
     * @return
     */
    int updateLeaderAuditOrderStatus(OrderLeaderAuditDO orderLeaderAuditDO);

    /**
     * 转领导批示
     *
     * @param auditDTO
     * @return
     */
    int addLeaderAudit(OrderLeaderAuditDTO auditDTO);

    /**
     * 转领导批示
     *
     * @param list
     * @return
     */
    int addLeaderAuditList(List<OrderLeaderAuditDTO> list);
}
