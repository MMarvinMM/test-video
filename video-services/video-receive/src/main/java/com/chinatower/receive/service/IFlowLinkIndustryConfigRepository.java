package com.chinatower.receive.service;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;

/**
 * @Description 告警流程环节行业初始化配置属性 Dao层业务类
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
public interface IFlowLinkIndustryConfigRepository {

    /**
     * 查询流程环节行业初始化属性配置
     * 根据 行业编码、行业应用编码、环节id查询属性配置
     * @param flowLinkAttrDto
     * @return
     */
    VmFlowLinkAttrDto getFlowLinkIndustryConfigByCondition(VmFlowLinkAttrDto flowLinkAttrDto);
}
