package com.chinatower.event.dto;

import com.chinatower.event.core.flow.IFlowLinkAction;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import lombok.Data;

/**
 * @Description 告警流程环节 业务对象
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class VmFlowLinkDto {

    /**
     * 流程环节信息
     */
    private VmFlowTemplateLinkDto flowTemplateLinkDto;

    /**
     * 流程环节属性配置信息
     */
    private VmFlowLinkAttrDto flowLinkAttrDto;

    /**
     * 状态模式接口
     */
    private IFlowLinkAction flowLinkAction;
}
