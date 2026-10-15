package com.chinatower.receive.mapper;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 18:41
 */
@Mapper
public interface IVmFlowLinkAttrMapper {

    /**
     * 查询流程环节属性配置
     * @param flowLinkAttrDto
     * @return
     */
    VmFlowLinkAttrDto getFlowLinkAttrByCondition(VmFlowLinkAttrDto flowLinkAttrDto);
}
