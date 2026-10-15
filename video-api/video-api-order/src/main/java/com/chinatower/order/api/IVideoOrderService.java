package com.chinatower.order.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.order.CustomerDTO;
import com.chinatower.order.api.factory.VideoOrderFallbackFactory;
import com.chinatower.platform.entity.order.FindDeviceCodeVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.platform.entity.order.TenantOrderInfoVO;
import com.chinatower.platform.entity.order.VideoOrderReq;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "videoOrderService", value = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = VideoOrderFallbackFactory.class)
public interface IVideoOrderService {
    
    @PostMapping(value = "/video/order/getVideoOrderInfo")
    Result<VideoOrderResp> getVideoOrderInfo(@Validated @RequestBody  VideoOrderReq videoOrderReq);

    @PostMapping(value = "/video/order/relationshipListByTenantId")
    Result<TenantOrderInfoVO> getOrderRelationshipListByTenantId(@Validated @RequestBody QueryOrderRelationshipVO queryOrderRelationshipVO);

    @PostMapping("/video/order/queryAlgorithmList")
    Result<List<OrderAlgorithmsVO>> queryAlgorithmList(@Validated @RequestBody CustomerDTO customerDTO);
    
    /**
     * 根据用户编号查询用户订购的行业版本下的设备编号
     * @apiNote 根据用户编号查询用户订购的行业版本下的设备编号
     * @apiVersion 8.1
     * @tag 八、订购能力
     * @requestDemo {
     *     "userId":"4542180008af47488c65b8767066a9b2",
     *     "appVerCode":"1.0"
     * }
     * @responseDem
     * 返回参数解析：
     *     设备编码列表
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1692003983105,
     *     "data": [
     *         "15030200831327000013"
     *     ]
     * }
     */
    @PostMapping(value = "/video/order/findDeviceCodeByUser")
    Result<List<String>> findDeviceCodeByUser(@Validated @RequestBody FindDeviceCodeVO findDeviceCodeVO);
}
