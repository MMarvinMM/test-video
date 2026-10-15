package com.chinatower.platform.service;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 15:00
 */
public interface VmFlowLinkIndustryConfigRepository {


    /**
     * 查询流程环节行业初始化属性配置
     * 根据 行业编码、行业应用编码、环节id查询属性配置
     * @param flowLinkAttrDto
     * @return
     */
    VmFlowLinkAttrDto getFlowLinkIndustryConfigByCondition(VmFlowLinkAttrDto flowLinkAttrDto);

}
