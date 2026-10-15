package com.chinatower.product.api.modules.sdktoapi.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.auth.api.AuthFeignClientNew;
import com.chinatower.auth.api.entity.SdkToApiLoginVo;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.product.api.modules.common.Constants;
import com.chinatower.product.api.modules.sdktoapi.SdkToApiRepository;
import com.chinatower.product.api.modules.sdktoapi.entity.SdkToApiEntityVo;
import com.chinatower.product.api.modules.sdktoapi.mapper.SdkToApiMapper;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;
import static com.chinatower.product.api.modules.sdktoapi.config.ScanAllAnnotation.HANDLER_MAP;

/**
 * 请求注解类型
 *
 * @GetMapping
 * @PathVariable 多个 common/getSeqByName/{name}/{number}
 * @RequestParam
 * @PostMapping
 * @RequestBody AnalysisVO analysisVO
 * @RequestBody List<String> urlList
 * @RequestPart("file") MultipartFile file
 * @PutMapping(path = USER_PREFIX + "/updateVideoUser")
 * com.chinatower.system.api.feign.client.SystemFeignClient#updateVideoUser
 * @DeleteMapping(path = "/system/dept/del/{deptId}")
 * com.chinatower.system.api.feign.client.SystemFeignClient#remove
 */
@Component
@Slf4j
public class ApiRequestInterceptor implements HandlerInterceptor {
    @Resource
    private ApplicationContext applicationContext;
    @Autowired
    private SdkToApiRepository sdkToApiRepository;
    public static ThreadLocal<String> USER_CODE_LOCAL = new ThreadLocal<String>();

    @Value("${security.web.business.token_validity:10800}")
    private int webTokenValiditySeconds;

    @Resource
    private SdkToApiMapper sdkToApiMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        response.setContentType("application/json;charset=UTF-8");
        Object object = null;
        try {
            String pathUrl = request.getHeader("pathUrl");
            String xUserCode = request.getHeader(Constants.USER_CODE);
            String xCustomerCode = request.getHeader(Constants.CUSTOMER_CODE);
            String xAppCode = request.getHeader(Constants.APP_CODE);
            log.info("sdkToApi入参,pathUrl={},X-UserCode={},X-CustomerCode={},xAppCode={}",pathUrl,xUserCode,xCustomerCode,xAppCode);

            //设置线程变量
            USER_CODE_LOCAL.set(xUserCode);
            String key = Constants.SDK_TO_API + xUserCode;
            String cacheKey = CacheConstants.getUserCacheKey("web_manage", xUserCode);
            log.info("preHandle-redisKey-cacheKey=" + cacheKey);
            log.info("preHandle-redisKey-key=" + key);
            String s = Objects.toString(RedisUtil.hGet(key, Constants.INDUSTRY_CODE, RedisDBType.PRIMARY),"");
            if(!RedisUtil.hasKey(cacheKey,RedisDBType.PRIMARY)//无登录
                    || (RedisUtil.hasKey(cacheKey,RedisDBType.PRIMARY) && !RedisUtil.hasKey(key,RedisDBType.PRIMARY))
                    || (RedisUtil.hasKey(cacheKey,RedisDBType.PRIMARY) && RedisUtil.hasKey(key,RedisDBType.PRIMARY) && !s.equals(xAppCode))
            ){//非api模拟登录
                AuthFeignClientNew authFeignClientNew = applicationContext.getBean(AuthFeignClientNew.class);

                String platformCode = "";
                SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
                if (ObjectUtil.isNotNull(sysConfig)) {
                    platformCode = sysConfig.getConfigValue();
                }

                SdkToApiLoginVo vo = new SdkToApiLoginVo();
                vo.setUserId(xUserCode);
                vo.setTradeId(xAppCode);
                vo.setAppVerCode(sdkToApiMapper.getAppVerCode(xCustomerCode, xAppCode, platformCode));
                Result sdkToApiResult = authFeignClientNew.sdkToApiLogin(vo);
                log.info("sdkToApiLogin出参=" + JSONObject.toJSONString(sdkToApiResult));
                if(ResultCode.SUCCESS.getCode() == sdkToApiResult.getCode() && !ObjectUtils.isEmpty(sdkToApiResult.getData())) {
                    Map map = (Map)sdkToApiResult.getData();
                    RedisUtil.hset(key,Constants.INDUSTRY_CODE, xAppCode, RedisDBType.PRIMARY);
                    RedisUtil.hset(key,Constants.TOKEN, "Bearer " + map.get("access_token"), RedisDBType.PRIMARY);
                    RedisUtil.hset(key,Constants.USER_CODE, "web_manage:" + xUserCode, RedisDBType.PRIMARY);
                    RedisUtil.expire(key, webTokenValiditySeconds, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                }else{
                    //异常场景返回报文
                    throw new CustomException("模拟登陆异常!");
                }
            }

            //转发请求
            log.info("send start");
            object = send(pathUrl, request);
            log.info("send end" + JSONObject.toJSONString(object));
            try{
                //保存es
                SdkToApiEntityVo sdkToApiEntityVo = new SdkToApiEntityVo();
                sdkToApiEntityVo.setId(IdUtils.fastSimpleUUID());
                sdkToApiEntityVo.setTenantId(xCustomerCode);
                sdkToApiEntityVo.setUserId(xUserCode);
                sdkToApiEntityVo.setPathUrl(pathUrl);
                sdkToApiEntityVo.setRequestTime(DateUtils.getTime());
                sdkToApiRepository.save(sdkToApiEntityVo);
                log.info("保存es成功！");
            }catch (Exception e){
                log.info("保存es异常:",e);
            }
        } catch (Exception e) {
            log.error("api interceptor error:", e);
        } finally {
            try {
                USER_CODE_LOCAL.remove();
                if(ObjectUtils.isEmpty(object)){
                    response.getWriter().write(JSONObject.toJSONString(Result.fail("请求失败！")));
                }else{
                    response.getWriter().write(JSONObject.toJSONString(object));
                }
            } catch (Exception e) {
                log.error("构建出参异常，{}",e);
            }
        }
        return false;
    }

    private Object send(String pathUrl,HttpServletRequest request) {
        // 添加实参
        Method method = HANDLER_MAP.get(pathUrl);
        if (method != null) {
            Class<?> clazz = method.getDeclaringClass();
            Object bean = applicationContext.getBean(clazz);
            List<Object> objects = Lists.newArrayList();
            Parameter[] parameters = method.getParameters();
            JSONObject jsonObject = readBody(request);
            List<Object> list = new ArrayList();
            if(ObjectUtils.isEmpty(jsonObject)){
                list =  readBodyAsArray(request);
            }
            // 创建一个 final 或 effectively final 的副本供 lambda 使用
            final List<Object> finalList = new ArrayList<>(list);
            if (parameters != null && parameters.length > 0) {
                Arrays.stream(parameters).forEach(p -> {
                    if (p.getAnnotation(PathVariable.class) != null) {
                        String value = p.getAnnotation(PathVariable.class).value();
                        String pathName = StringUtils.isEmpty(value) ? p.getName() : value;
                        Object o = parserParam(jsonObject.get(pathName), p.getType());
                        objects.add(o);
                    } else if (p.getAnnotation(RequestParam.class) != null) {
                        String value = p.getAnnotation(RequestParam.class).value();
                        String pathName = StringUtils.isEmpty(value) ? p.getName() : value;
                        Object parameter = parserParam(jsonObject.get(pathName), p.getType());
                        objects.add(parameter);
                    } else if (p.getAnnotation(RequestBody.class) != null) {
                        if(CollectionUtils.isEmpty(finalList)){
                            Object o = parserParam(jsonObject, p.getType());
                            objects.add(o);
                        }else{
                            objects.addAll(finalList);
                        }
                    } else if(p.getAnnotation(RequestPart.class) != null){
                        RequestPart requestPart = p.getAnnotation(RequestPart.class);
                        if (requestPart != null) {
                            String value = requestPart.value();
                            String pathName = StringUtils.isEmpty(value) ? p.getName() : value;
                            MultipartFile file = ((MultipartHttpServletRequest) request).getFile(pathName);
                            objects.add(file);
                        }
                    }else{//不带注解
                        if(CollectionUtils.isEmpty(finalList)){
                            Object o = parserParam(jsonObject, p.getType());
                            objects.add(o);
                        }else{
                            objects.addAll(finalList);
                        }
                    }
                });
            }
            if(!CollectionUtils.isEmpty(finalList)){
                return ReflectionUtils.invokeMethod(method, bean, objects);
            }
            return ReflectionUtils.invokeMethod(method, bean, objects.toArray());
        }else{
            log.error("pathUrl匹配失败！" + pathUrl);
        }
        return null;
    }

    private Object parserParam(Object o, Class<?> type) {
        return JSONObject.parseObject(JSONObject.toJSONString(o), type);
    }

    private JSONObject readBody(HttpServletRequest request) {
        try {
            String requestBody = IoUtil.read(request.getInputStream(), StandardCharsets.UTF_8);
            return JSONObject.parseObject(requestBody);
        } catch (Exception e) {
            log.warn("readBody error:", e);
        }
        return new JSONObject();
    }


    /**
     * 将请求体解析为List<Object>
     * @param request HttpServletRequest对象
     * @return 解析成功的List<Object>，解析失败返回空List<Object>
     */
    private List<Object> readBodyAsArray(HttpServletRequest request) {
        try {
            String requestBody = IoUtil.read(request.getInputStream(), StandardCharsets.UTF_8);
            return JSONArray.parseArray(requestBody, Object.class);
        } catch (Exception e) {
            log.warn("readBodyAsArray error:", e);
        }
        return new ArrayList<>();
    }
}
