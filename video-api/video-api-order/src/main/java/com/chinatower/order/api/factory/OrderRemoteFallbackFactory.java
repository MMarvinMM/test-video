package com.chinatower.order.api.factory;

import com.chinatower.common.core.dto.OrderRelationshipDTO;
import com.chinatower.common.core.dto.TransferIndustryDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.order.api.IOrderRemoteService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 订单服务降级处理
 *
 * @author chenmf
 */
@Component
public class OrderRemoteFallbackFactory implements FallbackFactory<IOrderRemoteService>
{
    
    private static final Logger log = LoggerFactory.getLogger(OrderRemoteFallbackFactory.class);
    
    @Override
    public IOrderRemoteService create(Throwable throwable) {
        log.error("订单远程服务调用失败:{}", throwable.getMessage());
        return new IOrderRemoteService() {
            @Override
            public Result<String> orderRelationship(OrderRelationshipDTO dto) {
                return Result.fail("推送订购信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> transferIndustry(TransferIndustryDTO param) {
                return Result.fail("客户行业版本转换失败:" + throwable.getMessage());
            }
        };
    }
}
