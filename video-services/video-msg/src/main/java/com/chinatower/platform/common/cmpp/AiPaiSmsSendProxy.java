package com.chinatower.platform.common.cmpp;

import com.huawei.insa2.comm.cmpp.message.CMPPDeliverMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.util.Args;
import com.huawei.smproxy.SMProxy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiPaiSmsSendProxy  extends SMProxy {

    private AiPaiSmsCompont gatewaySmsService;

    public AiPaiSmsSendProxy(Args args, AiPaiSmsCompont gatewaySmsService) {
        super(args);
        this.gatewaySmsService = gatewaySmsService;
    }

    @Override
    public CMPPMessage onDeliver(CMPPDeliverMessage msg) {
        log.debug("启动接收短信");
        gatewaySmsService.processRecvDeliverMsg(msg);
        return super.onDeliver(msg);
    }

    @Override
    public void onTerminate() {
        gatewaySmsService.onTerminate();
    }
}
