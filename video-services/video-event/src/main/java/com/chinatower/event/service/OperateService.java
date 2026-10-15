package com.chinatower.event.service;

import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.vo.AlarmOperateVO;
import java.util.List;
import java.util.Map;

public interface OperateService {

    /**
     * 查询运营研判详情
     * @param warningOrderId 告警编号
     * @param userId 登录用户ID
     * @return
     */
    AlarmOperateDTO getOperateInfo(String warningOrderId, String userId);

    /**
     * 根据工单ID查询运营研判信息
     * @param list 入参
     * @return
     */
    List<AlarmOperateDTO> getOperateListByOrderIds(List<String> list);

}
