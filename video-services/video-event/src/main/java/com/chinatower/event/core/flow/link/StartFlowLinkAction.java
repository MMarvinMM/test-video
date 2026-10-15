package com.chinatower.event.core.flow.link;

import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.springframework.stereotype.Component;

/**
 * @Description 开始节点
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Component("startFlowLinkAction")
public class StartFlowLinkAction extends AbstractBaseFlowLinkAction {

    @Override
    public void action(VmFlowLinkAttrDto flowLinkAttrDto) {
        //保存生成下一个节点

    }

    @Override
    public boolean jdugeFlowLink(String linkType) {
        return "1".equals(linkType);
    }
}
