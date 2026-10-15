package com.chinatower.event.core.flow;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description TODO
 * @Author guyuefei
 * @Date 2022/7/13
 * @Version TODO
 **/
@Component("flowLinkActionExcute")
public class FlowLinkActionExcute {

    @Autowired
    private List<IFlowLinkAction> flowLinkActionList;

    /**
     * 给环节节点定义执行类
     * @param linkType
     */
    public IFlowLinkAction jdugeFlowLink(String linkType){
        for (IFlowLinkAction flowLinkAction : flowLinkActionList) {
            if(flowLinkAction.jdugeFlowLink(linkType)){
                return flowLinkAction;
            }
        }
        return null;
    }

    /**
     * 判断当前节点用户类型，保存处理用户、角色关联关系
     * @param flowLinkAttrDto
     */
    public void handleFlowLinkUserType(VmFlowLinkAttrDto flowLinkAttrDto){
        throw new UnsupportedOperationException("Judge the user type of the current node, save and process the user and role association");
    }

    /**
     * 判断当前是否通知领导处理
     * @param flowLinkAttrDto
     */
    public void handleFlowLinkToLeader(VmFlowLinkAttrDto flowLinkAttrDto){
        throw new UnsupportedOperationException("Judge whether to notify the leader for handling");
    }

    /**
     * 处理会签
     * @param flowLinkAttrDto
     */
    public void handleFlowLinkSign(VmFlowLinkAttrDto flowLinkAttrDto){
        throw new UnsupportedOperationException("Processing countersignature");
    }

    /**
     * 发送消息
     * @param flowLinkAttrDto
     */
    public void handleFlowLinkNotify(VmFlowLinkAttrDto flowLinkAttrDto){
        throw new UnsupportedOperationException("send message");
    }
}
