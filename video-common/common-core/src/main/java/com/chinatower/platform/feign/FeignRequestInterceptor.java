package com.chinatower.platform.feign;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.utils.ServletUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ip.IpUtils;
import com.chinatower.platform.constants.FeignConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * feign 请求拦截器
 *
 * @author chinatower
 */
@Component
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor
{
    @Override
    public void apply(RequestTemplate requestTemplate) {
//        HttpServletRequest  httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        log.info("httpServletRequest is not null {}", StringUtils.isNotNull(httpServletRequest));
        log.info("httpServletRequest Object  is null {}", Objects.isNull(httpServletRequest));
        if (StringUtils.isNotNull(httpServletRequest)) {
            buildInTraceId(requestTemplate, httpServletRequest);
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            // 传递用户信息请求头，防止丢失
            String userId = headers.get(FeignConstants.HEADER_USERID.toLowerCase());
            if (StringUtils.isNotEmpty(userId)) {
                requestTemplate.header(FeignConstants.HEADER_USERID, userId);
            }
            String userName = headers.get(CacheConstants.DETAILS_USERNAME);
            if (StringUtils.isNotEmpty(userName)) {
                requestTemplate.header(CacheConstants.DETAILS_USERNAME, userName);
            }
            String authentication = headers.get(CacheConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication)) {
                requestTemplate.header(CacheConstants.AUTHORIZATION_HEADER, authentication);
            }
            String userKey = headers.get(FeignConstants.HEADER_USER_KEY.toLowerCase());
            log.info("ServletUtils getHeaders userKey:{}", userKey);
            if (StringUtils.isNotEmpty(userKey)) {
                requestTemplate.header(FeignConstants.HEADER_USER_KEY, userKey);
            }
            String industryCode = headers.get(FeignConstants.HEADER_INDUSTRY_CODE);
            if(StringUtils.isEmpty(industryCode)){
                industryCode = headers.get(FeignConstants.HEADER_INDUSTRY_CODE.toLowerCase());
            }
            if (StringUtils.isNotEmpty(industryCode)) {
                requestTemplate.header(FeignConstants.HEADER_INDUSTRY_CODE, industryCode);
            }
            //预留字段，下次header中有参数，可直接使用此字段。目前暂时放置HEADER_USER_KEY（其实无用） 。
//            String industryCommonParamJson = headers.get(FeignConstants.HEADER_INDUSTRY_COMMON_JSON);
            String industryCommonParamJson = headers.get(FeignConstants.HEADER_USER_KEY.toLowerCase());
            if (StringUtils.isNotEmpty(userKey)) {
                requestTemplate.header(FeignConstants.HEADER_INDUSTRY_COMMON_JSON, industryCommonParamJson);
            }
            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr(ServletUtils.getRequest()));
        }
    }

    private static void buildInTraceId(RequestTemplate requestTemplate, HttpServletRequest httpServletRequest) {
        try {
            String traceId = MDC.get("TraceId");
            requestTemplate.header("TraceId", ObjectUtil.defaultIfBlank(traceId, RandomUtil.randomString(32)));
        } catch (Exception e) {
            log.error("add traceId error: ", e);
        }
    }
}
