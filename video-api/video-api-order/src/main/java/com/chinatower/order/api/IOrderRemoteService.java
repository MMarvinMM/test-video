package com.chinatower.order.api;

import com.chinatower.common.core.dto.OrderRelationshipDTO;
import com.chinatower.common.core.dto.TransferIndustryDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.order.api.factory.OrderRemoteFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "orderRemoteService", value = "order", fallbackFactory = OrderRemoteFallbackFactory.class)
public interface IOrderRemoteService {

    @PostMapping("/video/message/orderRelationship")
    Result orderRelationship(@Validated @RequestBody OrderRelationshipDTO dto);

    @PostMapping("/video/order/transferIndustry")
    Result transferIndustry(@RequestBody TransferIndustryDTO param);
}