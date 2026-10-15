package com.chinatower.event.core.flow.link;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.springframework.stereotype.Component;

/**
 * @Description 结束节点
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Component("endFlowLinkAction")
public class EndFlowLinkAction extends AbstractBaseFlowLinkAction {

    @Override
    public void action(VmFlowLinkAttrDto flowLinkAttrDto) {
        throw new UnsupportedOperationException("This is action method");
    }

    @Override
    public boolean jdugeFlowLink(String linkType) {
        return "3".equals(linkType);
    }
}
