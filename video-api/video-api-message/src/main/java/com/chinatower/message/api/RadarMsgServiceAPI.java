package com.chinatower.message.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.RadarBindUserModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "RadarMsgServiceAPI",
        value = ServiceNameConstants.MESSAGE_SOCKET_SERVICE)
public interface RadarMsgServiceAPI {

    @PostMapping("/msgApi/sendRadarMsg2Client")
    Result<Boolean> sendRadarMsg2Client(@RequestBody RadarBindUserModel model);

    
}
