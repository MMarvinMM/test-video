package com.chinatower.receive.service;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 18:40
 */
public interface IFlowLinkAttrRepository {

    /**
     * 查询流程环节属性配置
     * @param flowLinkAttrDto
     * @return
     */
    VmFlowLinkAttrDto getFlowLinkAttrByCondition(VmFlowLinkAttrDto flowLinkAttrDto);
}
