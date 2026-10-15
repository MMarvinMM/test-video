package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.factory.ReceiveAlarmFallbackFactory;
import com.chinatower.event.entity.dto.DelayMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/10/21 11:44
 */
@FeignClient(contextId = "communicationService", value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = ReceiveAlarmFallbackFactory.class)
public interface CommunicationService {
    /**
     * 查询融合通信列表
     */
    @PostMapping("/communication/queryList")
    TableDataInfo queryList(@RequestBody CommunicationVo communicationVo);

    /**
     * 通过号码查询用户信息
     */
    @PostMapping("/communication/queryInfoByTel")
    Result queryInfoByTel(@RequestBody CommunicationVo communicationVo);
}
