package com.chinatower.provider.call.video.client.interceptor;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.product.api.modules.common.Constants;
import com.chinatower.product.api.modules.common.HMAC_SHA1;
import com.chinatower.product.api.modules.commonservice.dao.CommonDao;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: FeignNewConfiguration
 * @Author: wangyt
 * @CreateTime: 2022-09-10  11:12
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
@Slf4j
public class FeignNewConfiguration implements RequestInterceptor {

    @Value("${yy.secretKey}")
    private String secretKey;

    @Autowired
    CommonDao commonDao;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Override
    public void apply(RequestTemplate template) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String customerCode = "";
        String userCode = "";
        String orderCode = "";
        String appKey = "";
        String appCode = "000000";
        Map<String, Collection<String>> names = template.headers();
        if(names.containsKey(Constants.XAPPKEY)){
            for (String name : names.get(Constants.XAPPKEY)) {
                appKey = name;
            }
        }
        if(names.containsKey("X-CustomerCode")){
            for (String name : names.get("X-CustomerCode")) {
                customerCode = name;
            }
        }
        if(names.containsKey("X-UserCode")){
            for (String name : names.get("X-UserCode")) {
                userCode = name;
            }
        }
        if(names.containsKey("X-deviceCode")){
            String deviceCode = "";
            for (String device : names.get("X-deviceCode")) {
                deviceCode = device;
            }
            if(!"".equals(customerCode)&&!"".equals(deviceCode)){
                List<String> orderCodeList = commonDao.getOrderCode(customerCode,deviceCode);
                if (orderCodeList != null && orderCodeList.size() > 0) {
                    orderCode = orderCodeList.get(0);
                }
            }
        }

        String url = template.url();
        String xMsgId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String xRequestTime = sdf.format(new Date());
        String signature = url + appKey + xMsgId + xRequestTime;
        if (!("").equals(NullProcessUtil.nvlToString(customerCode, ""))) {
            signature += customerCode;
            if (!("").equals(NullProcessUtil.nvlToString(userCode, ""))) {
                signature += userCode;
            }
        }

        String xSignature = HMAC_SHA1.signByHmacSHA1(secretKey,(signature).toUpperCase());
        template.header(Constants.XAPPKEY, appKey);
        template.header("X-Signature", xSignature);
        template.header("X-MsgId", xMsgId);
        template.header("X-RequestTime", xRequestTime);
        template.header("X-AppCode", appCode);
        template.header("X-AppCode-new", appCode);

        //以下请求头参数在调用监控服务接口，以及云台锁申请接口时需要传入
        template.header("X-OrderCode",orderCode);
        log.info("X-MsgId==="+xMsgId);
        log.info("X-Signature==="+signature);
        log.info("X-AppKey==="+appKey);
        log.info("X-OrderCode==="+orderCode);
        log.info("X-AppCode==="+appCode);
    }
}
