package com.chinatower.receive.vo.dto.flow.link;


import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.receive.vo.dto.flow.FlowLinkActionExcute;
import com.chinatower.receive.vo.dto.flow.IFlowLinkAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 流程节点处理动作模板类
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Component("abstractBaseFlowLinkAction")
public abstract class AbstractBaseFlowLinkAction implements IFlowLinkAction {

    @Autowired
    private FlowLinkActionExcute flowLinkActionExcute;

    /**
     * 公共处理逻辑
     * @param flowLinkAttrDto
     */
    @Override
    public void doAction(VmFlowLinkAttrDto flowLinkAttrDto) {
        //判断当前节点用户类型，保存处理用户、角色关联关系
        flowLinkActionExcute.handleFlowLinkUserType(flowLinkAttrDto);
        //判断当前是否通知领导处理
        flowLinkActionExcute.handleFlowLinkToLeader(flowLinkAttrDto);
        //处理会签
        flowLinkActionExcute.handleFlowLinkSign(flowLinkAttrDto);
        //根据消息通知类型，发送消息
        flowLinkActionExcute.handleFlowLinkNotify(flowLinkAttrDto);
        //执行环节节点逻辑
        action(flowLinkAttrDto);
    }


    /**
     * 不同节点类型的特殊处理
     * @param flowLinkAttrDto
     */
    public abstract void action(VmFlowLinkAttrDto flowLinkAttrDto);
}
