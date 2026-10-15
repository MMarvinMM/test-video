package com.chinatower.common.core.inteceptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.chinatower.common.core.utils.GsonDeserialization;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.core.dto.BuryPointDTO;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.google.gson.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.chinatower.common.core.inteceptor.GlobalEmbeddedPointConfig.EmbeddedPointInteceptor.HOLDER;

@Slf4j
public class GlobalEmbeddedPointConfig implements ImportSelector {

    // 不获取入参信息
    private static Set<String> setFilter = new HashSet<>();

    // 不做记录
    private static Set<String> noRecord = new HashSet<>();

    // 返回异步的、流式的Flux数据,不做拦截处理
    private static Set<String> noFilter = new HashSet<>();

    private static final String ANSWER_URL = "/numberModel/queryProblemAnswerStream";


    static {
        setFilter.add("/upload/snapShotSimple");
        setFilter.add("/alarm/receive/upload/warnOrderImg");
        setFilter.add("/alarm/receive/upload/warnOrderSatelliteImg");
        setFilter.add("/upload/presetImg");
        setFilter.add("/burypoint/saveOne");
        setFilter.add("/provider/log/set");
        setFilter.add("/provider/sysLog/add");
        setFilter.add("/getCasCadeByDeviceCode");
        setFilter.add(ANSWER_URL);
        setFilter.add("/layerInfo/addLayer");
        setFilter.add("/layer/addOrModify");

        noRecord.add("/burypoint/saveOne");
        noRecord.add("/provider/log/set");
        noRecord.add("/provider/sysLog/add");
        noRecord.add(ANSWER_URL);
        noRecord.add("/layerInfo/addLayer");
        noRecord.add("/layer/addOrModify");
        noRecord.add("/system/area/queryAllAreaCode");

        // 返回异步的、流式的Flux数据,不做拦截处理
        noFilter.add(ANSWER_URL);
        noFilter.add("/chat/stream");
        noFilter.add("/api/sdkToApi");
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                EmbeddedPointInteceptor.class.getName(),
                InterceptorConfig.class.getName(),
                PointFilter.class.getName(),
                BuryPointKafkaProducer.class.getName()
        };
    }

    @WebFilter("/*")
    public static class PointFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            try {
                try {
                    buildInTraceId(request);
                    // 包装HttpServletRequest，以便多次读取请求体
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    String url = httpRequest.getRequestURI();
                    log.info("PointFilter doFilter httpRequest.getRequestURI():{}", url);
                    String contentType = httpRequest.getContentType();
                    log.info("PointFilter doFilter httpRequest.getContentType():{}", contentType);
                    EmbeddedPointInteceptor.EmbeddedContext embeddedContext = new EmbeddedPointInteceptor.EmbeddedContext();
                    HOLDER.set(embeddedContext);
                    if (noFilter.contains(url)) {
                        chain.doFilter(request, response);
                        return;
                    }
                    // 包装响应对象
                    ResponseWrapper responseWrapper = new ResponseWrapper(response);
                    if (setFilter.contains(url) || StringUtils.isEmpty(contentType) ||
                            (StringUtils.isNotEmpty(contentType) && !contentType.contains(MediaType.APPLICATION_JSON_VALUE))) {

                        try {
                            log.info("继续请求 chain.doFilter(request, responseWrapper)");
                            // 继续处理请求
                            chain.doFilter(request, responseWrapper);
                        } catch (Exception e){
                            log.error("PointFilter doFilter chain doFilter：{}", e);
                        }finally {
                            responseWrapper.copyBodyToResponse();
                            HOLDER.remove();
                        }
                        return;
                    } else if (StringUtils.isNotEmpty(contentType) && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                        RequestWrapper wrappedRequest = new RequestWrapper(httpRequest);

                        // 获取请求体内容
                        String requestBody = getRequestBody(wrappedRequest);

                        // 打印请求体内容（在实际应用中，你可以将请求体内容存储到日志中或进行其他处理）
                        log.info("Request Body: {}" + requestBody);
                        try {
                            log.info("继续请求 chain.doFilter(wrappedRequest, responseWrapper)");
                            // 继续处理请求
                            chain.doFilter(wrappedRequest, responseWrapper);
                        } catch (Exception e){
                            log.error("PointFilter doFilter chain doFilter：{}", e);
                        }finally {
                            responseWrapper.copyBodyToResponse();
                            HOLDER.remove();
                        }
                        return;
                    }
                } catch (Exception e) {
                    log.error("PointFilter doFilter：{}", e);
                }
                chain.doFilter(request, response);
            } finally {
                MDC.clear();
            }
        }

        private static void buildInTraceId(ServletRequest request) {
            try {
                String traceId = null;
                if (request instanceof HttpServletRequest) {
                    traceId = ((HttpServletRequest) request).getHeader("TraceId");
                }
                MDC.put("TraceId", ObjectUtil.defaultIfBlank(traceId, RandomUtil.randomString(32)));
            } catch (Exception e) {
                log.error("add traceId error: ", e);
            }
        }

        public static String getRequestBody(HttpServletRequest request) throws IOException {
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            return requestBody.toString();
        }
    }

    public static class EmbeddedPointInteceptor implements HandlerInterceptor, Ordered {

        @Value("${spring.application.name}")
        private String serviceName;

        private static final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Result.class, new GsonDeserialization()).create();

        @Autowired
        private BuryPointKafkaProducer buryPointKafkaProducer;

        public static final ThreadLocal<EmbeddedContext> HOLDER = new ThreadLocal<>();
        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                1, 2, 1000,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
                ThreadFactoryBuilder.create().setNamePrefix("EmbeddedPointInteceptor-").build(),
                // 埋点日志可丢弃，队列满时静默丢弃，绝不抛异常影响业务
                new ThreadPoolExecutor.DiscardPolicy());

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            log.info("EmbeddedPointInteceptor preHandle");
            String url = request.getRequestURI();
            if (!noRecord.contains(url)) {
                //这里记录开始时间
                HOLDER.get().startTime = DateUtil.current();
            }
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            String url = request.getRequestURI();
            log.info("url:{}", url);
            if (noRecord.contains(url)) {
                HOLDER.remove();
                return;
            }
            long startTime = HOLDER.get().startTime;
            long endTime = DateUtil.current();
            log.info("接口{}耗时:{}", request.getRequestURI(), endTime - startTime);
            String operParam = request.getQueryString();
            String requestMethod = request.getMethod();
            log.info("请求方式{},get参数:{}", requestMethod, operParam);
//            String requestBody = HOLDER.get().getRequestBody();
            RequestWrapper wrappedRequest = new RequestWrapper(request);
            // 获取请求体内容
            String requestBody = PointFilter.getRequestBody(wrappedRequest);
            log.info("post参数:{}", requestBody);



            String ip = request.getRemoteAddr();
            // 小程序
            String clientType = request.getHeader(FeignConstants.HEADER_CLIENT_TYPE);
            // app
            String clientName = request.getHeader(FeignConstants.HEADER_CLIENT_NAME);
            String contentType = request.getHeader(StringUtils.lowerCase(HttpHeaders.CONTENT_TYPE));
            int status = response.getStatus();
            Result result = Result.success(ResultCode.SUCCESS);

            Collection<String> headNames = response.getHeaderNames();
            log.info("PointFilter doFilter headNames size:{} ", headNames.size());
            headNames.forEach(headName -> {
                log.info("PointFilter doFilter 响应头headName:{},内容:{}", headName, response.getHeader(headName));
            });
            String responseContentType =  response.getContentType();
            log.info("响应ContentType:{}", responseContentType);
            if (StringUtils.isEmpty(responseContentType) ) {
                responseContentType  = response.getHeader(HttpHeaders.CONTENT_TYPE);
            } else {
                response.setHeader(HttpHeaders.CONTENT_TYPE, responseContentType);
            }
            log.info("响应头responseContentType:{}", responseContentType);
            String contentDisposition =  response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
            log.info("响应头contentDisposition:{}", contentDisposition);
            if (StringUtils.isNotEmpty(responseContentType) && responseContentType.contains(MediaType.APPLICATION_OCTET_STREAM_VALUE)){
                log.info("附件下载");
            }else if (StringUtils.isNotEmpty(contentDisposition) && contentDisposition.toLowerCase().contains("attachment")) {
                log.info("附件下载");
            }else if(StringUtils.isNotEmpty(responseContentType)
                    && responseContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                ResponseWrapper wrappedResponse = (ResponseWrapper) response;
                String responseBody = wrappedResponse.getResponseBody();

                // 创建优化的Gson实例
//                Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(Result.class, new JsonDeserializer<Result>() {
//                        @Override
//                        public Result deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                            JsonObject jsonObject = json.getAsJsonObject();
//                            Result result = Result.success("success");
//                            if (jsonObject.has("code")) {
//                                result.setCode(jsonObject.get("code").getAsInt());
//                            }
//                            if (jsonObject.has("msg")) {
//                                result.setMsg(jsonObject.get("msg").getAsString());
//                            }
//                            return result;
//                        }
//                    }).create();
                result = gson.fromJson(responseBody, Result.class);
                log.info("不获取data PointFilter doFilter result:{}", result);
                status = result.getCode();
            } else {
                log.info("响应头不匹配或响应内容无法转换");
            }
            //这里进行记录
            try {
                // 获取当前的用户
                LoginUser loginUser = SecurityUtils.getLoginUser();
                log.info("******************************************************");
                int finalStatus = status;
                Result finalResult = result;
                EXECUTOR.execute(() -> {

                    log.info("request.getRequestURI() is {}, response.getStatus() is {}", request.getRequestURI(), response.getStatus());
                    BuryPointDTO buryPointDTO = new BuryPointDTO();
                    log.info("url:{}", url);
                    buryPointDTO.setOperUrl(url);
                    buryPointDTO.setSpendTime(endTime - startTime);
                    buryPointDTO.setRequestMethod(requestMethod);
                    buryPointDTO.setTitle(serviceName);
                    buryPointDTO.setOperTime(new Date(startTime));


                    //解析header
                    log.info("contentType:{}", contentType);
                    if (requestMethod.toUpperCase().equals(HttpMethod.POST.name()) ||
                            (StringUtils.isNotEmpty(contentType) && contentType.contains(MediaType.APPLICATION_JSON_VALUE))) {
                        log.info("post 请求，非get请求");
                        buryPointDTO.setOperParam(requestBody);
                    } else {
                        buryPointDTO.setOperParam(operParam);
                    }
                    // 来源ip
                    log.info("ip :{}", ip);
                    buryPointDTO.setOperIp(ip);
                    if (Objects.nonNull(loginUser)) {
                        log.info("登录用户：{}", loginUser.getUsername());
                        // 租户、应用版本、行业编码
                        SysUser user = loginUser.getUser();
                        String userType = user.getUserType();
                        buryPointDTO.setUserType(userType);
                        buryPointDTO.setDeptName(user.getDept().getDeptName());
                        buryPointDTO.setOperName(loginUser.getUsername());
                        if (userType.equals(VideoSystemEnums.UserType.TENANT_USER.getIndex())) {
                            buryPointDTO.setUserType(VideoSystemEnums.UserType.TENANT_USER.getIndex());
                            // 租户
                            String tenantId = user.getTenantId();
                            String appVerCode = user.getAppVerCode();
                            String industryCode = user.getIndustryCode();
                            log.info("industryCode:{},tenantId:{},appVerCode:{}", industryCode, tenantId, appVerCode);
                            buryPointDTO.setTenantId(tenantId);
                            buryPointDTO.setAppVerCode(appVerCode);
                            buryPointDTO.setIndustryCode(industryCode);
                        } else {
                            // 铁塔用戶
                            buryPointDTO.setUserType(VideoSystemEnums.UserType.TOWER_USER.getIndex());
                        }

                    }

                    // 状态
                    if (finalStatus == HttpStatus.OK.value()) {
                        // 成功
                        buryPointDTO.setStatus(0);
                    } else {
                        buryPointDTO.setStatus(1);
                        buryPointDTO.setErrorMsg(finalResult.getMsg());
                    }

                    if (StringUtils.isNotBlank(clientType)) {
                        buryPointDTO.setClientName(clientType);
                    } else if (StringUtils.isNotBlank(clientName)) {
                        buryPointDTO.setClientName(clientName);
                    } else {
                        // 否则全是pc
                        buryPointDTO.setClientName(FeignConstants.HEADER_CLIENT_PC);
                    }
                    //记录
                    log.info("buryPointDTO:{}", buryPointDTO);
                    // 统一走 Kafka，完全解耦，log 服务阻塞不影响任何调用方
                    buryPointKafkaProducer.send(buryPointDTO);
                });
            } catch (Exception e) {
                log.error("afterCompletion exception:{}", e);
            } finally {
                HOLDER.remove();
            }
        }

        @Override
        public int getOrder() {
            return Integer.MAX_VALUE;
        }

        @Data
        private static class EmbeddedContext {
            private long startTime = DateUtil.current();
        }

    }


    public static class InterceptorConfig implements WebMvcConfigurer {

        @Resource
        EmbeddedPointInteceptor globalInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            // 添加全局拦截器并指定拦截路径
            registry.addInterceptor(globalInterceptor).addPathPatterns("/**");
        }
    }
}
