package com.chinatower.product.api.modules.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.product.api.modules.dto.HeadersConstant;
import com.chinatower.product.api.modules.mapper.third.ThirdPartyAlarmMapper;
import com.chinatower.product.api.modules.util.SignUtils;
import com.chinatower.product.api.modules.dto.MultipleSignContextData;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * video-service-api
 * @Author: songxinfeng
 * @Date: 2024/2/21 9:48
 */
@RestController
@RequestMapping("/third/alarm")
@Slf4j
public class ThirdApiCheckController {

    @Autowired
    private IReceiveAlarmNewService iTradeWarningOrderService;

    @Autowired
    private ThirdPartyAlarmMapper thirdPartyAlarmMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @RequestMapping("/addThirdAlarmOrder")
    public Result addThirdAlarmOrder(@RequestBody List<ReceiveAlarmVO> receiveAlarmVO, HttpServletRequest request){

        try{
            MultipleSignContextData multipleSignContext = MultipleSignContextData.builder()
                    .authorization(request.getHeader(HeadersConstant.SOUTH_AUTHORIZATION))
                    .appKey(request.getHeader(HeadersConstant.HEADER_KEY_APP_KEY))
                    .signature(request.getHeader(HeadersConstant.HEADER_KEY_SIGN))
                    .msgId(request.getHeader(HeadersConstant.HEADER_KEY_MSG_ID))
                    .appVersionCode(request.getHeader(HeadersConstant.HEADER_KEY_APP_CODE))
                    .requestTime(request.getHeader(HeadersConstant.HEADER_KEY_REQUES_TTIME))
                    .path(request.getRequestURI())
                    .customerCode(request.getHeader(HeadersConstant.HEADER_KEY_CUSTOMER_CODE))
                    .userCode(request.getHeader(HeadersConstant.HEADER_KEY_USER_CODE))
                    .industryCode(request.getHeader(HeadersConstant.HEADER_KEY_INDUSTRY_CODE))
                    .build();
            log.info("运管推送第三方告警请求头信息:{}", JSON.toJSONString(multipleSignContext));

            String platformCode ="";
            //获取当前节点
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigKey("platform_code");
            AjaxResult resultCode = systemFeignClient.getConfigInfo(sysConfig);
            if(resultCode != null && 200 == Integer.valueOf(String.valueOf(resultCode.get("code")))) {
                SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(resultCode.get("data")), SysConfig.class);
                if(sysConfigInfo != null){
                    platformCode = sysConfigInfo.getConfigValue();
                }
            }

            //查询该租户是否订购
            int orderCount = thirdPartyAlarmMapper.countOrder(receiveAlarmVO.get(0).getTenantId(),receiveAlarmVO.get(0).getIndustryCode(),platformCode);

            if(orderCount > 0){
                //根据租户加行业查询客户配置得SK
                String sk = thirdPartyAlarmMapper.getSkForThirdOperate(receiveAlarmVO.get(0).getTenantId(),receiveAlarmVO.get(0).getIndustryCode());
                if(StringUtils.isEmpty(sk)){
                    return Result.fail("该租户未配置网关调用AK");
                }
                // 拿到aksk后，做签名校验
                String sign = SignUtils.getGenerateSign(sk, multipleSignContext);
                boolean result = Objects.equals(sign, multipleSignContext.getSignature());
                if(result){
                    return iTradeWarningOrderService.addThirdAlarmOrder(receiveAlarmVO);
                }else{
                    return Result.fail("AK验证失败");
                }
            }else{
                return Result.fail("该租户未订购");
            }
        }catch (Exception e){
            log.error("addThirdAlarmOrder异常:{}",e);
            return Result.fail("系统异常");
        }
    }

}
