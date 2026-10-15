package com.chinatower.receive.vo.dto.flow;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;

/**
 * @Description 定义流程节点接口
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
public interface IFlowLinkAction {

    boolean jdugeFlowLink(String linkType);

    /**
     * 流程节点核心处理逻辑
     * @param flowLinkAttrDto
     */
    void doAction(VmFlowLinkAttrDto flowLinkAttrDto);
}
