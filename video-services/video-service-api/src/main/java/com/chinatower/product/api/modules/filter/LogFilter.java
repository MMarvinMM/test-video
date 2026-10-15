package com.chinatower.product.api.modules.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinatower.common.core.utils.HMAC_SHA1;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.product.api.modules.common.Constants;
import com.chinatower.product.api.modules.common.RespResult;
import com.chinatower.product.api.modules.common.StringUtilsNew;
import com.chinatower.product.api.modules.devices.dao.DevicesDAO;
import com.chinatower.common.entity.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@WebFilter(filterName = "logFilter", urlPatterns = "/*")
@RefreshScope
public class LogFilter implements Filter {
    @Resource
    private DevicesDAO devicesDAO;
    
    @Value("#{'${apiauth.allow}'.split(',')}")
    private List<String> apiauthAllow;
    
    @Value("${apiauth.enable}")
    private boolean apiauthEnable;

    @Value("${api.sk}")
    private String secretKey;

    @Resource
    private SystemFeignClient systemFeignClient;

    //校验签名的url请求
    private static final Set<String> checkSignUrl = new HashSet<>();
    //无需校验特定客户的url请求
    private static final Set<String> checkCustomerUrl = new HashSet<>();

    static {
        checkSignUrl.add("/device/getDeviceListForYJB");
        checkSignUrl.add("/device/createSnapshotTaskForYJB");
        checkSignUrl.add("/device/querySnapshotTaskForYJB");

        checkCustomerUrl.add("/third/alarm/addThirdAlarmOrder");
        checkCustomerUrl.add("/api/sdkToApi");
        checkCustomerUrl.add("/openApi/getAngByDeviceParam");
        checkCustomerUrl.add("/device/getDeviceListForYJB");
        checkCustomerUrl.add("/device/createSnapshotTaskForYJB");
        checkCustomerUrl.add("/device/querySnapshotTaskForYJB");
        checkCustomerUrl.add("/device/getSignatureForYJB");

    };

    public final String SIGNATURE_ERROR = "验签失败！";

    public final String X_MSG_ID = "X-MsgId";

    public final String X_REQUEST_TIME = "X-RequestTime";

    public void destroy() {
        log.info("doDestroy");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest)request);
        String body = requestWrapper.getBody();
        ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);

        Map logData = new HashMap();
        // 调用时间
        long startTime = System.currentTimeMillis();
        
        // apiauthEnable:true权限校验；false不校验权限。当为true时，apiauthAllow里配置的客户编码有权限访问；没配置的无权访问。
        String customerCode = req.getHeader(Constants.CUSTOMER_CODE);
        //第三方告警请求放行
        String url = req.getRequestURI();
        log.info("LogFilter-doFilter-url:{}",url);
        if( apiauthEnable && !apiauthAllow.contains(customerCode) && !checkCustomerUrl.contains(url))
        {
        	RespResult respResult = RespResult.FAIL(Constants.ERROR_QQSB, Constants.ERROR_CODE_1002, Constants.ERROR_WQFW);
        	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        	response.getOutputStream().write(JSON.toJSONString(respResult).getBytes());
        	return;
        }

        //验签
        if(checkSignUrl.contains(url)){
            String checkResult = this.checkSign(req);
            if(!StringUtils.isEmpty(checkResult)){
                Result<Object> respResult = Result.fail(1003, checkResult);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getOutputStream().write(JSON.toJSONString(respResult).getBytes());
                return;
            }
        }
        
        chain.doFilter(requestWrapper,wrapper);

        logData.put("ip",getCliectIp(req));
        // 请求url
        logData.put("url",req.getRequestURI());
        // 结束时间
        long endTime = System.currentTimeMillis();
        logData.put("duration",endTime-startTime);
        // headers
        Map headers = new HashMap<>();
        headers.put(X_MSG_ID,req.getHeader(X_MSG_ID));
        headers.put(X_REQUEST_TIME,req.getHeader(X_REQUEST_TIME));
        headers.put("X-AppCode",req.getHeader("X-AppCode"));
        headers.put("X-CustomerCode",req.getHeader("X-CustomerCode"));
        headers.put("X-UserCode",req.getHeader("X-UserCode"));
        headers.put("X-Token",req.getHeader("X-Token"));
        headers.put("X-StreamUuid",req.getHeader("X-StreamUuid"));
        headers.put("X-PlugId",req.getHeader("X-PlugId"));
        headers.put("pathUrl",req.getHeader("pathUrl"));
        logData.put("headers",headers.toString());

        // 参数
        if(StringUtilsNew.isNotEmpty(body)) {
            logData.put("params", JSONObject.parse(body).toString());
        }
        // 返回结果
        String result = wrapper.getResponseData(response.getCharacterEncoding());
        response.getOutputStream().write(result.getBytes());
        logData.put("result",result);

        log.info(logData.toString());
        // 记录log
//        devicesDAO.insertLog(logData);
        this.destroy();
    }

    /**
     * 获取客户端ip地址
     *
     * @param request
     * @return
     */
    public static String getCliectIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.trim() == "" || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.trim() == "" || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.trim() == "" || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ip.split(",");
        for (final String str : arr) {
            if (!Constants.UNKNOWN.equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }

    private String checkSign(HttpServletRequest req){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            String url = req.getRequestURI();
            String xRequestTime = req.getHeader(X_REQUEST_TIME);
            String xAppKey = req.getHeader("X-AppKey");
            String xMsgId = req.getHeader(X_MSG_ID);
            String xSignature = req.getHeader("X-Signature");
            log.info("url={},X-RequestTime={},X-AppKey={},X-MsgId={},X-Signature={}",url,xRequestTime,xAppKey,xMsgId,xSignature);

            long now = System.currentTimeMillis();
            long date = df.parse(xRequestTime).getTime();

            long expirationTime = 60;
            AjaxResult ajaxResult = systemFeignClient.getConfValue("api_expiration_time");
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                expirationTime = Long.parseLong(StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "60"));
            }
            //判断时间戳和当前时间是否超时
            if((now - date) > expirationTime * 1000){
                return "请求失效！";
            }
            String signature = url + xAppKey + xMsgId + xRequestTime;
            String mySignature = HMAC_SHA1.signByHmacSHA1(secretKey,(signature).toUpperCase());
            log.info("mySignature={}", mySignature);
            if(!mySignature.equals(xSignature)){
                return SIGNATURE_ERROR;
            }
        }catch (Exception e){
            log.error(SIGNATURE_ERROR, e);
            return SIGNATURE_ERROR;
        }
        return null;
    }

}

