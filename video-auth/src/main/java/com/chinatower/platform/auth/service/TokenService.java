package com.chinatower.platform.auth.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.auth.api.entity.WhiteIpDto;
import com.chinatower.common.core.comparator.PinYinComparator;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.utils.ServletUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.auth.common.constant.MessageConstant;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.auth.framework.manager.AsyncManager;
import com.chinatower.platform.auth.framework.manager.factory.AsyncFactory;
import com.chinatower.platform.auth.model.LoginVo;
import com.chinatower.platform.auth.project.entity.resp.CheckPwdResponse;
import com.chinatower.platform.auth.project.mapper.SysUserMapper;
import com.chinatower.platform.auth.project.service.ISysUserService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.ip.IpUtils;
import com.chinatower.platform.constants.AuthConstants;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysUserWhiteListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 功能说明: oauth2 token生成
 *
 * @author bruce
 * @date 2021-11-18
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class TokenService {

    private static final long MILLIS_SECOND = 1000;

    private static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    @Autowired
    private SysUserMapper userMapper;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private FileUrlEncryptUtil obsFileUrlEncryptDeal;
    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private StoreTokenService storeTokenService;

    @Autowired
    ISysUserService userService;

    private final String TOKEN_USER_NAME_KEY = "user_name";

    private final String IP_SPLIT_COMMA = ",";

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    public Map<String, Object> login(String username, String password, String appId) {
        //1、获取当前账号登录失败次数
        String loginFailNumKey = AuthConstants.getLoginFailNumKey(username);
        String loginFailNumStr = RedisUtil.get(loginFailNumKey, RedisDBType.PRIMARY);
        int loginFailNum = StrUtil.isNotBlank(loginFailNumStr) ? Integer.parseInt(loginFailNumStr) : 0;
        //2、判断登录失败次数，如果已经达到5次，则禁止登录直接返回错误
        if (AuthConstants.isLoginCountLimit(loginFailNum)) {
            throw new CustomException(AuthConstants.LOGIN_COUNT_MOREERROR_MSG);
        }

        SysUser user = userMapper.selectUserOnlyByUserName(username);
        if (user == null) {
            log.error("登录->用户{}不存在", username);
            loginFailNum = loginFailNum + 1;
            RedisUtil.set(loginFailNumKey, loginFailNum + "", AuthConstants.LOGIN_COUNT_DURATION_SECONDS, RedisDBType.PRIMARY);
            throw new CustomException(AuthConstants.getLoginCountMsg(loginFailNum));
        }
        if (user.getStatus().equalsIgnoreCase("1")) {
            throw new CustomException("该账号已注销不可用！");
        }
        //判断ip网段
        List<WhiteIpDto> whiteIpList = userMapper.findWhiteIpList(username);
        //需要进行白名单判断
        if (!whiteIpList.isEmpty()) {
            String userIp = IpUtils.getIpAddr(ServletUtils.getRequest());
            if (userIp.contains(IP_SPLIT_COMMA)) {
                userIp = userIp.split(IP_SPLIT_COMMA)[0];
            }
            log.info("当前用户的登录ip:{}", userIp);
            if (!userIpIsWhite(userIp, whiteIpList)) {
                throw new CustomException("该用户无登录权限");
            }
        }
        Map<String, Object> tokenMap = null;
        //3、获取oauth2 token
        try {
            log.info("{}开始获取oauth2 token", username);
            tokenMap = getOauth2Token(username, password, appId);
            log.info("{}结束获取oauth2 token", username);
            storeTokenService.cacheToken(tokenMap);
        } catch (Exception e) {
            log.error("登录异常{}", e);
            if (e.getMessage().contains("invalid_grant")) {
                loginFailNum = loginFailNum + 1;
                RedisUtil.set(loginFailNumKey, loginFailNum + "", AuthConstants.LOGIN_COUNT_DURATION_SECONDS, RedisDBType.PRIMARY);
                //安全漏洞修复,要求统一模糊提示
                String msg = AuthConstants.isLoginCountLimit(loginFailNum) ? AuthConstants.LOGIN_COUNT_ERROR_MSG : AuthConstants.getLoginCountMsg(loginFailNum);
                throw new CustomException(msg);
            } else {
                throw new CustomException("认证失败！", e);
            }
        }
        //4、登录成功后，登录失败记录清零
        RedisUtil.delete(loginFailNumKey, RedisDBType.PRIMARY);
        // 生成token，刷新缓存中的token，并返回token
        //5. 查询用户的行业信息
        String clientId = appId.split(":")[0];
        String cacheKey = CacheConstants.USER_INDUSTRY_CODE + clientId + ":" + tokenMap.get("userId");
        Map<Object, Object> cacheMap = RedisUtil.hGetAll(cacheKey, RedisDBType.PRIMARY);
        String originDeptId = user.getDeptId();
        LoginUser loginUser = chooseDefaultLoginUserFromCache(user, tokenMap, cacheMap, cacheKey);
        if (loginUser == null) {
            return tokenMap;
        }
        addTradeVersions(loginUser, tokenMap, username, originDeptId);
        if (tokenMap.containsKey(TOKEN_USER_NAME_KEY)) {
            String user_name = String.valueOf(tokenMap.get(TOKEN_USER_NAME_KEY));
            tokenMap.put(TOKEN_USER_NAME_KEY, com.chinatower.platform.common.utils.security.AESUtil.encryptBase64(user_name));
        }

        return tokenMap;
    }

    private LoginUser chooseDefaultLoginUserFromCache(SysUser user, Map<String, Object> tokenMap, Map<Object, Object> cacheMap, String cacheKey) {
        // 默认获取第一个
        String userCacheStr = null;
        //tt视角登录默认取999999
        String curChooseIndustryCode = "";
        if (user.getUserType().equalsIgnoreCase("0")) {
            userCacheStr = Objects.toString(cacheMap.get("999999"));
            curChooseIndustryCode = "999999";
            log.info("TokenService get ttuser,cacheKey={},userCacheStr={}", cacheKey, userCacheStr);
        } else {
            for (Map.Entry<Object, Object> entry : cacheMap.entrySet()) {
                userCacheStr = Objects.toString(entry.getValue());
                curChooseIndustryCode = (String) entry.getKey();
                break;
            }
        }
        tokenMap.put("curChooseIndustryCode", curChooseIndustryCode);
        log.info("cacheKey={},curChooseIndustryCode={},userCacheStr={},", cacheKey, curChooseIndustryCode, userCacheStr);
        if (StrUtil.isBlank(userCacheStr)) {
            tokenMap.put("tradeVersions", new ArrayList<>());
            return null;
        }
        return JSON.parseObject(userCacheStr, LoginUser.class);
    }

    private void addTradeVersions(LoginUser loginUser, Map<String, Object> tokenMap, String username, String originDeptId) {
        //6.根据行业信息查询出对应的版本信息
        if ("2".equals(loginUser.getUser().getUserType())) {//如果用户是租户
            //TODO 7-30 表结构修改这边逻辑不要了
            if (StringUtils.isEmpty(originDeptId)) {
                log.info("组织id为空不获取tradeVersions");
                tokenMap.put("tradeVersions", new ArrayList<>());
            } else {
                log.info("{}开始根据行业信息查询出对应的版本信息,originDeptId:{}", username, originDeptId);
                String platformCode = "";
                SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
                if (ObjectUtil.isNotNull(sysConfig)) {
                    platformCode = sysConfig.getConfigValue();
                }
                log.info("platformCode="+platformCode);
                List<SysIndustryViewRel> list = userMapper.selectUserAppVerCodesByDeptId(originDeptId,platformCode);
                log.info("{}结束根据行业信息查询出对应的版本信息,结果：{}", username, list);
                if (list != null && list.size() > 1) {
                    list.sort(Comparator.comparing(SysIndustryViewRel::getIndustryName, new PinYinComparator()).thenComparing(SysIndustryViewRel::getAppVerCode, String::compareTo));
                    Set<String> collect = list.stream().filter(e -> !StringUtils.isEmpty(e.getImgUrl())).map(SysIndustryViewRel::getImgUrl).collect(Collectors.toSet());
                    Map<String, String> urlMap = obsFileUrlEncryptDeal.dealObsFileUrlBatch(new ArrayList<String>(collect));
                    list.forEach(r -> {
                        if (!StringUtils.isEmpty(r.getImgUrl())) {
                            r.setImgUrl(urlMap.get(r.getImgUrl()));
                        }
                    });
                }
                log.info("结束处理应用图标地址，结果：{}", list);
                //如果是1条数据，则直接不选择版本，否则需要选择版本
                tokenMap.put("tradeVersions", CollectionUtils.size(list) <= 1 ? new ArrayList<>() : list);
            }
        } else {
            log.info("铁塔用户不获取tradeVersions");
            tokenMap.put("tradeVersions", new ArrayList<>());
        }
    }


//    public List<SysIndustryViewRel> selectUserAppVerCodesByDeptId(String deptId) {
//        return userMapper.selectUserAppVerCodesByDeptId(deptId);
//    }


    /**
     * 获取OauthToken
     *
     * @param username
     * @param password
     * @param appId
     * @return oauthTokenMap
     */
    public Map<String, Object> getOauth2Token(String username, String password, String appId) {
        String clientId = appId.split(":")[0];
        String clientSecret = appId.split(":")[1];
        log.info("clientId=" + clientId + ",clientSecret=" + clientSecret);
        //获取令牌的url
        String path = "http://127.0.0.1:" + serverPort + "/oauth/token";
        //定义body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        //授权方式
        formData.add("grant_type", "password");
        //账号
        formData.add("username", clientId + "|" + username);//将用户名和appId拼接传到认证服务端
        //密码
        formData.add("password", password);
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

    /**
     * 校验密码是否正确
     *
     * @param vo
     * @return
     */
    public Result checkPassword(LoginVo vo) {
        String appId = vo.getAppId();
        if(Constants.ONE.equals(vo.getSource())){
            appId = Constants.APP_ID;
        }
        String clientType = appId.split(":")[0];
        log.info("校验密码是否正确->入参：{}", vo.toOriginStringForLog());
        String username = vo.getUsername();

        //1、获取当前账号登录失败次数
        String loginFailNumKey = AuthConstants.getLoginFailNumKey(username);
        String loginFailNumStr = RedisUtil.get(loginFailNumKey, RedisDBType.PRIMARY);
        int loginFailNum = StrUtil.isNotBlank(loginFailNumStr) ? Integer.parseInt(loginFailNumStr) : 0;
        //2、判断登录失败次数，如果已经达到5次，则禁止登录直接返回错误
        if (AuthConstants.isLoginCountLimit(loginFailNum)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, AuthConstants.LOGIN_COUNT_MOREERROR_MSG, clientType));
            return Result.fail(AuthConstants.LOGIN_COUNT_MOREERROR_MSG);
        }
        SysUser user = userMapper.selectUserWithPwdByUserName(username, "");
        if (user == null) {
            log.error("校验密码是否正确->用户{}不存在", username);
            loginFailNum = loginFailNum + 1;
            RedisUtil.set(loginFailNumKey, loginFailNum + "", AuthConstants.LOGIN_COUNT_DURATION_SECONDS, RedisDBType.PRIMARY);
            String msg = AuthConstants.getLoginCountMsg(loginFailNum);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, msg, clientType));
            return Result.fail(msg);
        }
        if ("1".equals(user.getStatus())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, MessageConstant.ACCOUNT_DISABLED, clientType));
            return Result.fail(MessageConstant.ACCOUNT_DISABLED);
        }

        String encryptPassword = SecurityUtils.encryptPassword(vo.getPassword());
        log.info("校验密码是否正确->解密后的username：{}，加密密码：{}", username, encryptPassword);
        if (!encryptPassword.equals(user.getPassword())) {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            sysUser.setLoginFailNum(user.getLoginFailNum() == null ? 0 : user.getLoginFailNum() + 1);
            sysUser.setUpdateTime(new Date());
            userMapper.updateSysUser(sysUser);
            //增加错误次数
            loginFailNum = loginFailNum + 1;
            RedisUtil.set(loginFailNumKey, loginFailNum + "", AuthConstants.LOGIN_COUNT_DURATION_SECONDS, RedisDBType.PRIMARY);
            String msg = AuthConstants.isLoginCountLimit(loginFailNum) ? AuthConstants.LOGIN_COUNT_ERROR_MSG : AuthConstants.getLoginCountMsg(loginFailNum);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, msg, clientType));
            return Result.fail(msg);
        }
//        return Result.success(user.getPhonenumber());

        CheckPwdResponse resp = new CheckPwdResponse();
        resp.setUserName(AESUtil.encrypt(user.getUserName()));
        String phonenumber = user.getPhonenumber();
        resp.setPhonenumber(AESUtil.encrypt(phonenumber));
        try {
            resp.setAvatar(obsFileUrlEncryptDeal.dealObsFileUrl(user.getAvatar()));
        } catch (Exception e) {
            log.error("文件服务错误", e);
        }
        // 校验用户是否为白名单
        Result<SysUserWhiteListVO> userWhite = systemFeignClient.getUserWhite(username);
        resp.setIsWhite(0);
        if (userWhite != null && userWhite.getData() != null) {
            resp.setIsWhite(1);
        }
        return Result.data(resp);
    }

    /**
     * 校验密码是否正确
     *
     * @param vo
     * @return
     */
    @Deprecated
    public Result checkPwd(LoginVo vo) {
        SysUser user = userMapper.selectUserWithPwdByUserName(vo.getUsername(), "");
        if (user == null) {
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        if ("1".equals(user.getStatus())) {
            return Result.fail(MessageConstant.ACCOUNT_LOCKED);
        }
        if (!SecurityUtils.encryptPassword(vo.getPassword()).equals(user.getPassword())) {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(vo.getUsername());
            sysUser.setLoginFailNum(user.getLoginFailNum() == null ? 0 : user.getLoginFailNum() + 1);
            sysUser.setUpdateTime(new Date());
            userMapper.updateSysUser(sysUser);
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        CheckPwdResponse resp = new CheckPwdResponse();
        resp.setUserName(user.getUserName());
        resp.setPhonenumber(user.getPhonenumber());
        try {
            resp.setAvatar(obsFileUrlEncryptDeal.dealObsFileUrl(user.getAvatar()));
        } catch (Exception e) {
            log.error("文件服务错误", e);
        }
        // 校验用户是否为白名单
        Result<SysUserWhiteListVO> userWhite = systemFeignClient.getUserWhite(vo.getUsername());
        resp.setIsWhite(0);
        if (userWhite != null && userWhite.getData() != null) {
            resp.setIsWhite(1);
        }
        return Result.data(resp);
    }

    public Result checkPlatformCode(LoginVo vo){
        try {
            AjaxResult ajaxResult = systemFeignClient.getConfValue("platform_code");
            if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)){
                    String platformCode = sysConfig.getConfigValue();
                    log.info("checkPlatformCode==" + vo.getAppId().split(":")[0] + vo.getUsername());
                    SysUser sysUser = userService.selectUserByUserName(vo.getUsername());
                    if(!ObjectUtils.isEmpty(sysUser)){
                        if(!com.chinatower.common.core.utils.StringUtils.isEmpty(platformCode) && Constants.BATTERY_ORG_TYPE_2.equals(sysUser.getUserType())){
                            String s = userService.checkPlatformCode(sysUser.getTenantId());
                            if(!com.chinatower.common.core.utils.StringUtils.isEmpty(s) && !platformCode.equals(s)){
                                return Result.fail(MessageConstant.USERNAME_NOT_RIGHT);
                            }
                        }
                    }else{
                        return Result.fail(MessageConstant.USERNAME_NOT_RIGHT);
                    }
                }
            }
        }catch (Exception e1){
            log.error("登录查询平台编码异常=",e1);
        }
        return Result.data("校验通过！");
    }

    /**
     * 将IPv4地址字符串转换为Long型数值表示。
     * @param ipStr IP地址字符串，如 "192.168.1.1"
     * @return 对应的Long型数值
     */
    private long ipToLong(String ipStr) {
        try {
            byte[] bytes = InetAddress.getByName(ipStr).getAddress();
            long result = 0;
            for (byte b : bytes) {
                result <<= 8;
                result |= (b & 0xFF);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Invalid IP: " + ipStr, e);
        }
    }

    /**
     * 判断一个IP地址是否在指定的范围内。
     * @param ip 需要检查的IP地址字符串
     * @param startIp 范围的起始IP地址字符串
     * @param endIp 范围的结束IP地址字符串
     * @return 如果IP在范围内则返回true，否则返回false
     */
    private boolean isIpInRange(String ip, String startIp, String endIp) {
        long ipLong = ipToLong(ip);
        long startIpLong = ipToLong(startIp);
        long endIpLong = ipToLong(endIp);
        log.info("startIpLong:{}--endIpLong:{}--ipLong{}", startIpLong, endIpLong, ipLong);
        return ipLong >= startIpLong && ipLong <= endIpLong;
    }


    /**
     * 判断ip是否在白名单范围内
     * @param ip user ip
     * @param whiteIpList white ip range list
     * @return
     */
    private boolean userIpIsWhite(String ip, List<WhiteIpDto> whiteIpList) {
        for (WhiteIpDto whiteIpDto : whiteIpList) {
            if (isIpInRange(ip, whiteIpDto.getIpStart(), whiteIpDto.getIpEnd())) {
                log.info("ip:{}符合网段:{}", ip, whiteIpDto.getIpStart() + "-" + whiteIpDto.getIpEnd());
                return true;
            }
        }
        log.info("ip:{}不符合网段:{}", ip, JSON.toJSONString(whiteIpList));
        return false;
    }
}