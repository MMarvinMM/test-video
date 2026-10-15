package com.chinatower.order.api.factory;

import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.entity.Result;
import com.chinatower.order.api.IVideoOrderService;
import com.chinatower.platform.entity.order.FindDeviceCodeVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.platform.entity.order.TenantOrderInfoVO;
import com.chinatower.platform.entity.order.VideoOrderReq;
import com.chinatower.platform.entity.order.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import feign.hystrix.FallbackFactory;

import java.util.List;

/**
 * 订单服务降级处理
 *
 * @author chenmf
 */
@Component
public class VideoOrderFallbackFactory implements FallbackFactory<IVideoOrderService>
{
    
    private static final Logger log = LoggerFactory.getLogger(VideoOrderFallbackFactory.class);
    
    @Override
    public IVideoOrderService create(Throwable throwable) {
        log.error("订单服务调用失败:{}", throwable.getMessage());
        return new IVideoOrderService()
        {
            @Override
            public Result<VideoOrderResp> getVideoOrderInfo(VideoOrderReq videoOrderReq)
            {
                return Result.fail("获取订单编码失败:" + throwable.getMessage());
            }
            
            @Override
            public Result<TenantOrderInfoVO> getOrderRelationshipListByTenantId(QueryOrderRelationshipVO queryOrderRelationshipVO) {
                return Result.fail("获取租户订购信息失败:" + throwable.getMessage());
            }
    
            @Override
            public Result queryAlgorithmList(CustomerDTO customerDTO) {
                return Result.fail("查询订购算法列表失败:" + throwable.getMessage());
            }
    
            @Override
            public Result<List<String>> findDeviceCodeByUser(FindDeviceCodeVO findDeviceCodeVO) {
                return Result.fail("根据用户编号查询用户订购的行业版本下的设备编号失败:" + throwable.getMessage());
            }
        };
    }
}
