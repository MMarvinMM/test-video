package com.chinatower.receive.vo.dto.flow.link;


import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.springframework.stereotype.Component;

/**
 * @Description 中间节点
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Component("middleFlowLinkAction")
public class MiddleFlowLinkAction extends AbstractBaseFlowLinkAction {

    @Override
    public void action(VmFlowLinkAttrDto flowLinkAttrDto) {
        //如果有并行节点处理并行节点

        //保存生成下一个节点
    }

    @Override
    public boolean jdugeFlowLink(String linkType) {
        return "2".equals(linkType);
    }
}
