package com.chinatower.platform.auth.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.auth.config.AuthProperties;
import com.chinatower.platform.auth.project.entity.dto.StatisticsUserOnlineTimeDTO;
import com.chinatower.platform.auth.project.entity.resp.RefreshPcResponse;
import com.chinatower.platform.auth.project.mapper.SysLogininforMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 功能说明: oauth2刷新token
 *
 * @author bruce
 * @date 2021-11-18
 */
@Slf4j
@Component
public class RefreshService extends AbstractBaseMongoDAO {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SysLogininforMapper sysLogininforMapper;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private AuthProperties authProperties;

    public Map<String, Object> refreshToken(String refreshToken, String appId) {

        String clientId = appId.split(":")[0];
        String clientSecret = appId.split(":")[1];
        //获取令牌的url
        String path = "http://127.0.0.1:" + serverPort + "/oauth/token";
        //定义body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        //授权方式
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        //定义头
        //直接通过用户传过来的clientId来组织认证中心的client
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        if (StrUtil.isNotEmpty(clientSecret)) {
            header.add("Authorization", httpBasicValue(clientId, clientSecret));
        } else {
            throw new InvalidClientException("client ID does not match authenticated client");
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(path, HttpMethod.POST,
                new HttpEntity(formData, header),
                Map.class);
        //获取响应数据
        return mapResponseEntity.getBody();

    }


    /**
     * 根据httpBasic协议规定内容封装
     *
     * @param clientId
     * @param clientSecurect
     * @return
     */
    private String httpBasicValue(String clientId, String clientSecurect) {
        //组织客户端id和客户端秘钥内容格式
        String value = clientId + ":" + clientSecurect;
        //使用base64编码
        byte[] encode = Base64Utils.encode(value.getBytes());
        //组成httpBasic协议内容格式
        return "Basic " + new String(encode);
    }


    //刷新用户web端登录时效
    public Result refreshPcLoginTime() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userId = loginUser.getUser().getUserId();
        String key = CacheConstants.USER_INDUSTRY_CODE + "web_manage" + ":" + userId;
        log.info("刷新用户web端登录时效key{}", key);
        //登录中刷新最大次数 默认200次
        Long refreshMaxNum = 200L;
        //刷新redis缓存过期时间 默认5分钟
        Long refreshRedisExpireTime = 5L;
        Result<List<SysDictDataResponse>> systemTypeList = systemFeignClient.dictDataType(Constants.LOGIN_TIME_WEB_CONFIG);
        if (systemTypeList != null && systemTypeList.getData() != null && !systemTypeList.getData().isEmpty()) {
            for (SysDictDataResponse datum : systemTypeList.getData()) {
                log.info("查询字典login_time_web_config{}", JSONArray.toJSONString(systemTypeList.getData()));
                if ("refreshMaxCount".equals(datum.getDictLabel())){
                    refreshMaxNum = Long.parseLong(datum.getDictValue());
                }else if ("refreshRedisExpireTime".equals(datum.getDictLabel())){
                    refreshRedisExpireTime = Long.parseLong(datum.getDictValue());
                }
            }
        }
        //计算登录时长
        Long durationSeconds = this.getLoginTime(key, refreshRedisExpireTime);
        //登录次数MongoDB处理
        Integer count = this.refreshCountPcMongo(key,refreshMaxNum);
        //大于连续刷新次数，不再更新登录时效，默认200次
        if (count < refreshMaxNum){
            //更新该用户redis登录信息过期时间
            RedisUtil.expire(key, refreshRedisExpireTime, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        }else {
            RedisUtil.delete(key,RedisDBType.PRIMARY);
        }

        //更新web登录时长数据
        Function<Map<String,String>,Result> fun1= this::updatePcLoginTime;
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        Map<String,String> map = new HashMap<>();
        map.put("userId",userId);
        map.put("durationSeconds", String.valueOf(durationSeconds));
        paramMap.put(fun1,map);
        List<Function> listFunc= Arrays.asList(fun1);
        ThreadParallelUtil.run(listFunc, f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.USER_LOGIN_WEB);
        return Result.success("success");
    }

    public Long getLoginTime(String key,Long redisTotalTime){
        //min转换s
        redisTotalTime = redisTotalTime * 60;
        //获取剩余过期时间
        Long expire = RedisUtil.getExpire(key, TimeUnit.SECONDS, RedisDBType.PRIMARY);
        //获取今日时长
        int nowMaxTtl = LocalTime.now().toSecondOfDay();
        //第一次登录取auth配置过期时间
        if (expire > redisTotalTime){
            redisTotalTime = Long.parseLong(String.valueOf(authProperties.getWebTokenValiditySeconds()));
        }
        //获取用户在线时长，用redis总过期时长-剩余过期时长。保底处理在线时长大于今天时间，取今日时间
        Long durationSeconds = Math.min(nowMaxTtl, Math.max(0, redisTotalTime - expire));
        return durationSeconds;
    }

    //登录次数MongoDB处理
    public Integer refreshCountPcMongo(String key,Long refreshMaxNum){
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.REFRESH_COUNT_PC);
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.USER_KEY).is(key));
        //获取当前用户web端登录次数，有+1.没有默认为1
        RefreshPcResponse refreshPcResponse = this.getTemplate(MongoDBType.PRIMARY).findOne(query, RefreshPcResponse.class, Constants.REFRESH_COUNT_PC);
        log.info("refresh_count_pc获取当前用户web端登录次数结果{}", JSON.toJSONString(refreshPcResponse));
        Integer count = 1;
        Integer realCount = 1;
        if (ObjectUtil.isNotNull(refreshPcResponse)){
            count = refreshPcResponse.getUserRefreshCount() + 1;
            realCount = refreshPcResponse.getUserRefreshCount() + 1;
        }
        //大于连续刷新次数，连续刷新次数重置0
        if (count >= refreshMaxNum){
            count = 0;
        }
        //更新用户对应web端刷新次数
        Query updateQuery = new Query();
        updateQuery.addCriteria(Criteria.where(Constants.USER_KEY).is(key));
        Update update = new Update();
        update.set(Constants.USER_REFRESH_COUNT,count);
        bulkOperations.upsert(updateQuery, update);
        bulkOperations.execute();
        return realCount;
    }


    //更新web登录时长数据
    public Result updatePcLoginTime(Map<String,String> mapReq){
        String userId = mapReq.get("userId");
        Long durationSeconds = Long.parseLong(mapReq.get("durationSeconds"));
        //登录总时长
        String onlineTime = "";
        //上一次登录web端时长
        String lastRecordJson = "";
        //登录web端总时长
        String webOnlineTime = "";
        StatisticsUserOnlineTimeDTO onlineTimeDTO = new StatisticsUserOnlineTimeDTO();
        // 查询用户今日历史在线时长
        String loginTime = LocalDate.now().toString();
        StatisticsUserOnlineTimeDTO userInfo = sysLogininforMapper.selectUserLoginInfo(userId);
        log.info("updatePcLoginTime-查询用户信息selectUserLoginInfo结果{}", JSON.toJSONString(userInfo));
        if (ObjectUtil.isNotNull(userInfo)){
            StatisticsUserOnlineTimeDTO userOnlineTime = sysLogininforMapper.selectUserOnlineTime(userId, loginTime);
            log.info("updatePcLoginTime-查询用户历史在线时长selectUserOnlineTime结果{}", JSON.toJSONString(userOnlineTime));
            Map<String, Long> map = new HashMap<>();
            map.put("WEB_MANAGE",durationSeconds);
            lastRecordJson = JSON.toJSONString(map);
            onlineTimeDTO.setLastRecordJson(lastRecordJson);
            onlineTimeDTO.setDeptId(userInfo.getDeptId());
            onlineTimeDTO.setTenantId(userInfo.getTenantId());
            onlineTimeDTO.setNickName(userInfo.getNickName());
            onlineTimeDTO.setUserId(userId);
            onlineTimeDTO.setLoginTime(loginTime);
            //如果查询用户今日历史在线时长不为空就修改 反之新增
            if (ObjectUtil.isNotNull(userOnlineTime)){
                if (StringUtils.isEmpty(userOnlineTime.getWebOnlineTime())){
                    userOnlineTime.setWebOnlineTime(Constants.ZERO);
                }
                onlineTime = String.valueOf(Long.parseLong(userOnlineTime.getOnlineTime()) + durationSeconds);
                webOnlineTime = String.valueOf(Long.parseLong(userOnlineTime.getWebOnlineTime()) + durationSeconds);
                onlineTimeDTO.setId(userOnlineTime.getId());
                onlineTimeDTO.setOnlineTime(onlineTime);
                onlineTimeDTO.setWebOnlineTime(webOnlineTime);
                log.info("updatePcLoginTime-修改用户登录时长updateUserOnlineTime入参{}", JSON.toJSONString(onlineTimeDTO));
                sysLogininforMapper.updateUserOnlineTime(onlineTimeDTO);
            }else {
                onlineTime = String.valueOf(durationSeconds);
                webOnlineTime = String.valueOf(durationSeconds);
                onlineTimeDTO.setOnlineTime(onlineTime);
                onlineTimeDTO.setWebOnlineTime(webOnlineTime);
                log.info("updatePcLoginTime-新增用户登录时长addUserOnlineTime入参{}", JSON.toJSONString(onlineTimeDTO));
                sysLogininforMapper.addUserOnlineTime(onlineTimeDTO);
            }
        }
        return Result.success("");
    }

}
