package com.chinatower.event.service;

import com.chinatower.event.entity.dto.IntercepiResultVo;
import com.chinatower.event.entity.dto.WarningInterceptDTO;
import com.chinatower.event.entity.vo.InterceptVO;

public interface InterceptService {

    /**
     * 查询回收站列表
     * @param dto 入参
     * @return
     */
    IntercepiResultVo getInterceptList(WarningInterceptDTO dto);

    /**
     * 查询回收站详情
     * @param warningOrderId 告警编号
     * @param userId 登录用户ID
     * @return
     */
    InterceptVO getInterceptInfo(String warningOrderId,String userId);
}
