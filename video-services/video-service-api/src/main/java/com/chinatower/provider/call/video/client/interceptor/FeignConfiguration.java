package com.chinatower.provider.call.video.client.interceptor;

import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.product.api.modules.common.HMAC_SHA1;
import com.chinatower.product.api.modules.commonservice.dao.CommonDao;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@Slf4j
public class FeignConfiguration implements RequestInterceptor {

    @Value("${yy.appKey}")
    private String appKey;

    @Value("${yy.secretKey}")
    private String secretKey;

    @Value("${yy.appCode}")
    private String appCode;

    @Autowired
    CommonDao commonDao;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Override
    public void apply(RequestTemplate template) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
//                .getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        Enumeration<String> headerNames = request.getHeaderNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                String name = headerNames.nextElement();
//                String values = request.getHeader(name);
//                log.info("name==="+name+",values==="+values);
//                if(!("X-MsgId").equals(name)&&!("x-msgid").equals(name)){
//                    template.header(name, values);
//                }
//            }
//        }

        String customerCode = "";
        String userCode = "";
//        String serverCode = "";
        String orderCode = "";
        Map<String, Collection<String>> names = template.headers();
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
//        if(names.containsKey("X-ServerCode")){
//            for (String name : names.get("X-ServerCode")) {
//                serverCode = name;
//            }
//        }
        if(names.containsKey("X-deviceCode")&&names.containsKey("X-channelCode")){
//            for (String name : names.get("X-OrderCode")) {
                String deviceCode = "";
                String channelCode = "";
                for (String device : names.get("X-deviceCode")) {
                    deviceCode = device;
                }
                for (String channel : names.get("X-channelCode")) {
                    channelCode = channel;
                }
                if(!"".equals(customerCode)&&!"".equals(deviceCode)&&!"".equals(channelCode)){
                    List<String> orderCodeList = commonDao.getOrderCode(customerCode,deviceCode);
                    if (orderCodeList != null && orderCodeList.size() > 0) {
                        orderCode = orderCodeList.get(0);
                    }
                }
//            }
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
        template.header("X-AppKey", appKey);
        template.header("X-Signature", xSignature);
        template.header("X-MsgId", xMsgId);
        template.header("X-RequestTime", xRequestTime);
        template.header("X-AppCode",appCode);

        //以下请求头参数在调用监控服务接口，以及云台锁申请接口时需要传入
        template.header("X-OrderCode",orderCode);
//        template.header("X-ServerCode",serverCode);
        log.info("X-MsgId==="+xMsgId);
        log.info("signature==="+signature);
        log.info("X-Signature==="+xSignature);
    }

}

