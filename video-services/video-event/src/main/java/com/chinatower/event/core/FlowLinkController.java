package com.chinatower.event.core;

import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.core.flow.FlowLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-event
 * @className: FlowLinkController
 * @description:
 * @author: yongqi.han
 * @date: 2022/8/18
 * @requestID 需求单号及需求名称
 */
@RestController
@Slf4j
@RequestMapping(value = "/test/flow/link")
public class FlowLinkController  extends TowerBaseController {

    @Autowired
    private FlowLinkService flowLinkService;

    /**
     * 功能描述：测试接口-根据行业编码、行业应用版本、租户、告警来源、告警类型等条件获取当前节点信息
     * @return
     */
    @PostMapping("/testFlowLink")
    public String testFlowLink(){
        String appVerCode = "000000";
        String industryCode = "";
        String tenantId = "122035797";
        String warningSource = "1";
        String alarmType = "1010105";
        flowLinkService.getStartFlowLink(appVerCode, industryCode, tenantId, warningSource, alarmType);
        return null ;
    }


}
