package com.chinatower.platform.auth.project.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.auth.api.entity.AuthReplaceTokenVO;
import com.chinatower.auth.api.entity.SdkToApiLoginVo;
import com.chinatower.cascade.api.CascadeConfigAPI;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.dto.IndustryReqModel;
import com.chinatower.common.core.enums.ClientType;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.manage.api.IIndustryService;
import com.chinatower.platform.auth.common.constant.MessageConstant;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.auth.config.AuthProperties;
import com.chinatower.platform.auth.framework.manager.AsyncManager;
import com.chinatower.platform.auth.framework.manager.factory.AsyncFactory;
import com.chinatower.platform.auth.model.*;
import com.chinatower.platform.auth.project.entity.dto.CheckPasswordDTO;
import com.chinatower.platform.auth.project.entity.dto.ForgotPasswordDTO;
import com.chinatower.platform.auth.project.service.ISysUserService;
import com.chinatower.platform.auth.service.TokenService;
import com.chinatower.platform.common.utils.HttpServletUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.CustomSmsTemplateConstants;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.platform.dto.ColonyApp;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.enums.SmsResultCode;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.model.dto.AuthConfig;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysUserWhiteListVO;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Preconditions;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * video-auth
 * 功能说明: 登录相关接口
 *
 * @author bruce
 * @date 2021-11-18
 */
@RestController
@Slf4j
public class LoginController {

    @Value("${token.universalCaptcha:}")
    private String universalCaptcha;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    ISysUserService userService;

    @Resource
    IIndustryService iIndustryService;

    @Resource
    private AuthProperties authProperties;

    @Autowired
    private MsgFeignClient msgFeignClient;

    private static final String Bearer = "Bearer ";
    private static final String CLIENT_ID = "client_id";
    private static final String USER_ID = "userId";

    private final static String CHAR_BASE = "123456789qwertyuiopasdfghjklzxcvbnm";

    private final String PLATFORM_CODE = "platform_code";

    private final String CURRENT_FLAG = "currentFlag";

    public final String GENERAL_URL = "generalInternetUrl";

    @Resource
    private CascadeConfigAPI cascadeConfigAPI;


    @Autowired
    private MongoTemplate mongoTemplate;


    private boolean inWhiteList(LoginVo vo) {
        Result<SysUserWhiteListVO> userWhite = systemFeignClient.getUserWhite(vo.getUsername());
        if (userWhite != null && userWhite.getData() != null) {
            return true;
        }
        return false;
    }

    /**
     * 单点登录鉴权
     *
     * @param uuid
     * @return
     * @throws IOException
     */
    @GetMapping("/loginTmp")
    public Result loginTmp(String uuid,String appFlag,HttpServletResponse httpServletResponse) throws IOException {
        log.info("======================齐河临时登录方案=============================");
        log.info("uuid:" + uuid + ",appFlag:" + appFlag);
        Preconditions.checkArgument(StrUtil.isNotBlank(uuid), "uuid不能为空");
        if (StringUtils.isBlank(uuid)) {
            return Result.fail("uuid不能为空");
        }
        Result result = userService.ssoLogin(uuid,appFlag);
        return result;
    }


    /**
     * 登录方法
     *
     * @return 结果
     */
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginVo vo) {
        Result ajax = Result.success(Constants.AUTH_SUCCESS);

        String appId = vo.getAppId();
        if(Constants.ONE.equals(vo.getSource())){
            appId = Constants.APP_ID;
        }

        // pad_manage 客户端固定走短信验证码登录，不受 sysConfig.loginType 配置控制
        boolean isPadManage = appId != null && appId.startsWith(OauthConstants.PAD_MANAGE_CLIENT_ID);

        String loginType = OauthConstants.LOGIN_TYPE_GRAPH;// 1表示图形验证码登录
        String cacheCaptcha = null;

        if (isPadManage) {
            loginType = OauthConstants.LOGIN_TYPE_SMS;
        } else {
            AjaxResult ajaxResult = systemFeignClient.getConfValue(OauthConstants.LOGIN_TYPE);
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                loginType = sysConfig.getConfigValue();
            }
        }

        String clientType = appId.split(":")[0];

        if ((OauthConstants.LOGIN_TYPE_GRAPH.equals(loginType) || inWhiteList(vo)) && !isPadManage) {
            log.info("图形验证码登录：{}", vo.getUsername());
            String verifyKey = OauthConstants.CAPTCHA_CODE_KEY + vo.getUuid();
            try {
                cacheCaptcha = RedisUtil.get(verifyKey, RedisDBType.PRIMARY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RedisUtil.delete(verifyKey, RedisDBType.PRIMARY);
            // 验证是否为默认码，如果是则放过验证直接登录（此功能方便调试，生产不需配此参数）
            if (StrUtil.isNotBlank(universalCaptcha) && StrUtil.equals(universalCaptcha, vo.getCode())) {
                return prepareToken(vo.getUsername(), vo.getPassword(), appId);
            }

            if (cacheCaptcha == null) {// 图形验证码为空
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, "验证码已失效", clientType));
                ajax = Result.fail(ResultCode.VERIFY_CODE_ERROR.getCode(), "验证码已失效");
            } else if (!cacheCaptcha.equalsIgnoreCase(vo.getCode())) {// 图形验证码输入错误
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, "验证码错误", clientType));
                ajax = Result.fail(ResultCode.VERIFY_CODE_ERROR);
            } else {
                return prepareToken(vo.getUsername(), vo.getPassword(), appId);
            }

        } else {// 短信验证码登录
            log.info("短信验证码登录账号：{}, 手机号:{}, 验证码:{}", vo.getUsername(), vo.getPhoneNumber(), vo.getCode());
            //查询手机号码
            SysUser user = userService.selectUserByUserName(vo.getUsername());
            if (user == null) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, MessageConstant.USERNAME_PASSWORD_ERROR, clientType));
                ajax = Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
            } else {
                log.info("用户账号 {} 手机号 {}", user.getUserName(), user.getPhonenumber());
                Result result = msgFeignClient.checkPhoneCode(user.getPhonenumber(), vo.getCode(), CustomSmsTemplateConstants.LOGIN_KEY, true);
                int resultCode = result.getCode();
                if (resultCode == ResultCode.SUCCESS.getCode()) {
                    ajax = prepareToken(vo.getUsername(), vo.getPassword(), appId);
                } else {
                    if (resultCode == SmsResultCode.VERIFY_TIME_OUT.getCode()) {
                        AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, "短信验证码过期", clientType));
                        ajax = Result.fail("短信验证码过期");
                    } else {
                        AsyncManager.me().execute(AsyncFactory.recordLogininfor(vo.getUsername(), OauthConstants.LOGIN_FAIL, "短信验证码错误", clientType));
                        ajax = Result.fail("短信验证码错误");
                    }
                }
            }

        }
        return ajax;
    }

    /**
     * 校验密码
     *
     * @param vo
     * @return
     */
    @PostMapping("/checkPassword")
    public Result checkPassword(@Validated @RequestBody LoginVo vo) {
        return tokenService.checkPassword(vo);
    }

    /**
     * 校验验证码
     *
     * @param vo 入参
     * @return
     */
    @PostMapping("/checkPwd")
    @Deprecated
    public Result checkPwd(@Validated @RequestBody LoginVo vo) {
        return tokenService.checkPwd(vo);
    }

    /**
     * 根据用户名获取手机号
     *
     * @param username
     * @return
     */
    @GetMapping("/findPhoneByUserName")
    public Result findPhoneByUserName(@RequestParam("username") String username) {
        SysUser phone1 = userService.selectUserByUserName(username);
        if (phone1 != null) {
            if (StringUtils.isBlank(phone1.getPhonenumber())) {
                return Result.fail(ResultCode.NOT_FOUND);
            }
            if (phone1.getStatus().equalsIgnoreCase("1")) {
                return Result.fail("该账号已停用！");
            }
            String p = phone1.getPhonenumber().replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2");
            return Result.data(p);
        }
        return Result.fail(ResultCode.USER_PASSWORD_ERROR);
    }

    /**
     * 忘记密码接口
     *
     * @param dto
     * @return
     */
    @PostMapping("updatePassword")
    public Result updatePassword(@Validated @RequestBody ForgotPasswordDTO dto) {
        return userService.forgetUpdate(dto);
    }

    /**
     * 根据用户名获取到手机号，并发送验证码
     *
     * @param username
     * @return
     */
    @GetMapping("/sendMsgCodeByUserName")
    public Result sendMsgCodeByUserName(@RequestParam("username") String username, @RequestParam("imageCode") String imageCode, @RequestParam("uuid") String uuid) {
        //保存验证码信息
        String verifyKey = OauthConstants.CAPTCHA_CODE_KEY + uuid;
        String s = RedisUtil.get(verifyKey, RedisDBType.PRIMARY);
        if (StringUtils.isBlank(s) || !s.equalsIgnoreCase(imageCode)) {
            return Result.fail("图片验证码错误");
        }
        SysUser user = userService.selectUserByUserName(username);
        if (user != null) {
            if (StringUtils.isBlank(user.getPhonenumber())) {
                return Result.fail(ResultCode.NOT_FOUND);
            }
            return userService.sendMsg2User(user.getPhonenumber(), CustomSmsTemplateConstants.UPDATE_PASSWORD_KEY);
        }
        return Result.fail(ResultCode.USER_PASSWORD_ERROR);
    }


    private Result prepareToken(String username, String password, String appId) {
        return userService.prepareToken(username, password, appId);
    }

    /**
     * 选择行业
     *
     * @param vo
     * @return
     */
    @PostMapping("/chooseTrade")
    public Result chooseTrade(@RequestBody LoginVo vo, @RequestHeader("Authorization") String token) {
        String tradeId = vo.getTradeId();
        String appVerCode = vo.getAppVerCode();
        if (StringUtils.isEmpty(tradeId)) {
            log.error("chooseTrade==error==>{}", vo);
            return Result.fail("选择行业编码不能为空");
        }
        if (StringUtils.isEmpty(appVerCode)) {
            log.error("chooseTrade==error==>{}", vo);
            return Result.fail("选择行业版本不能为空");
        }
        String realToken = token.replace(Bearer, "");
        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(realToken);
            String payLoad = jwsObject.getPayload().toString();
            JSONObject json = JSONObject.parseObject(payLoad);
            String clientId = json.getString(CLIENT_ID);
            String userId = json.getString(USER_ID);
            String cacheKey = CacheConstants.USER_INDUSTRY_CODE + clientId + ":" + userId;
            LoginUser loginUser = null;
            if (StringUtils.isNotEmpty(vo.getSwitchFlag())) {
                log.info("choose trade,cache user from db,userid->{}", userId);
                SysUser user = userService.selectUserWithPwdByUserId(userId, "");
                List<SysIndustryViewRel> list = userService.selectUserAppVerCodesByDeptId(user.getDeptId());
                if (CollectionUtils.isEmpty(list) || list.stream().noneMatch(x -> StringUtils.equalsIgnoreCase(vo.getTradeId(), x.getIndustryCode()) && StringUtils.equalsIgnoreCase(appVerCode, x.getAppVerCode()))) {
                    log.error("选择行业失败，无权限,list->{},vo->{}", JSON.toJSONString(list), JSON.toJSONString(vo));
                    return Result.fail("选择行业失败,请退出后重试");
                }
                Set<String> permissions = userService.getMenuPermission(user);
                loginUser = new LoginUser(user, permissions);
            } else {
                String userCacheStr = RedisUtil.hGet(cacheKey, tradeId, RedisDBType.PRIMARY);
                log.info("cacheKey={},industryCode={},userCacheStr={}", cacheKey, tradeId, userCacheStr);
                if (StrUtil.isNotBlank(userCacheStr)) {
                    loginUser = JSON.parseObject(userCacheStr, LoginUser.class);
                }
            }

            if (null != loginUser) {
                loginUser.getUser().setIndustryCode(tradeId);
                loginUser.getUser().setAppVerCode(appVerCode);
                loginUser.getUser().setChooseTradeId(tradeId);
                loginUser.getUser().setChooseAppVerCode(appVerCode);
                //获取当前进入的组织信息
                SysDept sysDept = userService.selectCurrentDept(loginUser.getUser());
                loginUser.getUser().setDept(sysDept);
                loginUser.getUser().setDeptId(sysDept.getDeptId());
                List<String> strings = new ArrayList<>();
                strings.add(sysDept.getDeptId());
                loginUser.getUser().setDeptIds(strings);

                try {
                    List<String> permissionsList = null;
                    IndustryReqModel industryReqModel = new IndustryReqModel();
                    industryReqModel.setVersion(appVerCode);
                    industryReqModel.setIndustryCode(tradeId);
                    permissionsList = iIndustryService.getAuthUrlList(industryReqModel).getData();
                    if (permissionsList != null) {
                        loginUser.getPermissions().addAll(permissionsList);
                    }

                    //查询租户对应的区域树
                    List<TreeSelect> areaTree = userService.selectAreaTree(sysDept.getDeptId());
                    if (areaTree != null) {
                        loginUser.getUser().setAreaTree(areaTree);
                    }
                } catch (Exception e) {
                    log.info("根据行业编码、版本号获取鉴权url获取失败");
                    e.printStackTrace();
                }
                if (clientId.startsWith(OauthConstants.USER_LOGIN_TYPE_WEB)) {// web端用户
                    //RedisUtil.setEx(redisKey, loginUser, jwtTokenEnhancer.getWebTokenValiditySeconds(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
                    RedisUtil.hset(cacheKey, tradeId,
                            JSON.toJSONString(loginUser), RedisDBType.PRIMARY);
                    RedisUtil.expire(cacheKey, authProperties.getWebTokenValiditySeconds(), TimeUnit.SECONDS, RedisDBType.PRIMARY);

                    RedisUtil.setEx(CacheConstants.USER_LAST_INDUSTRY_CODE + clientId + ":" + userId, tradeId,
                            authProperties.getWebTokenValiditySeconds(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
                } else {// app端用户
                    //RedisUtil.setEx(redisKey, loginUser, jwtTokenEnhancer.getAppTokenValiditySeconds(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
                    RedisUtil.hset(cacheKey, tradeId,
                            JSON.toJSONString(loginUser), RedisDBType.PRIMARY);
                    RedisUtil.expire(cacheKey, authProperties.getAppTokenValiditySeconds(), TimeUnit.SECONDS, RedisDBType.PRIMARY);

                    RedisUtil.setEx(CacheConstants.USER_LAST_INDUSTRY_CODE + clientId + ":" + userId, tradeId,
                            authProperties.getAppTokenValiditySeconds(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
                }
            } else {
                return Result.fail("选择行业失败");
            }
        } catch (ParseException e) {
            log.error("选择行业失败-->", e);
            return Result.fail("选择行业失败");
        }
        return Result.success("选择行业成功");
    }

    /**
     * 切换视角续期
     *
     * @param token
     * @return
     */
    @PostMapping("/delaySwitch")
    public Result delaySwitch(@RequestHeader("Authorization") String token) {
        // 这里返回一个时间
        Result ajax = Result.success("续期成功！");
        String realToken = token.replace(Bearer, "");
        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(realToken);
            String payLoad = jwsObject.getPayload().toString();
            JSONObject json = JSONObject.parseObject(payLoad);
            String clientId = json.getString(CLIENT_ID);
            String userId = json.getString(USER_ID);

            int tokenValiditySeconds = 0;
            if (clientId.startsWith(OauthConstants.USER_LOGIN_TYPE_WEB)) {// web端用户
                tokenValiditySeconds = authProperties.getWebTokenValiditySeconds();
            } else {// app端用户
                tokenValiditySeconds = authProperties.getAppTokenValiditySeconds();
            }

            //String redisKey = OauthConstants.LOGIN_TOKEN_KEY + clientId + ":" + userId;
            //LoginUser loginUser = JSON.parseObject(RedisUtil.get(redisKey, RedisDBType.PRIMARY), LoginUser.class);

            LoginUser loginUser = null;
            String cacheKey = CacheConstants.USER_INDUSTRY_CODE + clientId + ":" + userId;
            String industryCode = HttpServletUtils.getRequestHeader(FeignConstants.HEADER_INDUSTRY_CODE);
            if (com.chinatower.platform.common.utils.StringUtils.isBlank(industryCode)) {
                // 如果请求头中不存在，则取最后一次选择的行业code
                String industryCodeKey = CacheConstants.USER_LAST_INDUSTRY_CODE + clientId + ":" + userId;
                industryCode = RedisUtil.get(industryCodeKey, RedisDBType.PRIMARY);
            }
            // 再次判断行业code是否存在
            if (com.chinatower.platform.common.utils.StringUtils.isBlank(industryCode)) {
                log.warn("切换用户续期->获取行业编码为空，使用默认行业，cacheKey:{}", cacheKey);
                // 默认行业code
                industryCode = "000000";
            }

            String userCacheStr = RedisUtil.hGet(cacheKey, industryCode, RedisDBType.PRIMARY);
            if (StrUtil.isNotBlank(userCacheStr)) {
                loginUser = JSON.parseObject(userCacheStr, LoginUser.class);
            }

            if (loginUser == null) {
                return Result.fail("切换失败");
            }

            //切换后的用户id不变，需要查库
            SysUser switchUser = userService.selectUserByUserName(loginUser.getUsername());
            String switchUserId = switchUser.getUserId();
            String SWITCH_USER_KEY = OauthConstants.SWITCH_USER_KEY + clientId + ":" + userId;
            String SWITCH_USER_KEY_DELAY_COUNT = SWITCH_USER_KEY + ":delaycount:" + switchUserId;

            Integer delaycount = RedisUtil.get(SWITCH_USER_KEY_DELAY_COUNT, Integer.class, RedisDBType.PRIMARY);
            if (delaycount == null) {
                Long exTime = RedisUtil.get(SWITCH_USER_KEY, Long.class, RedisDBType.PRIMARY);
                RedisUtil.setEx(SWITCH_USER_KEY, exTime + 60 * 10 * 1000, tokenValiditySeconds, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                RedisUtil.setEx(SWITCH_USER_KEY_DELAY_COUNT, 1, tokenValiditySeconds, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            } else {
                if (delaycount < 3) {
                    Long exTime = RedisUtil.get(SWITCH_USER_KEY, Long.class, RedisDBType.PRIMARY);
                    RedisUtil.setEx(SWITCH_USER_KEY, exTime + 60 * 10 * 1000, tokenValiditySeconds, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                    RedisUtil.setEx(SWITCH_USER_KEY_DELAY_COUNT, delaycount + 1, tokenValiditySeconds, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                } else {
                    return Result.success("已经超过3次。续期失败！");
                }
            }

            return ajax;
        } catch (ParseException e) {
            log.error("切换异常", e);
            return Result.fail("切换失败");
        }
    }

    /**
     * 查询切换视角剩余时间
     *
     * @param token
     * @return
     */
    @PostMapping("/delaySwitchEndTime")
    public Result getEndTime(@RequestHeader("Authorization") String token) {
        // 这里返回一个时间
        Result ajax = Result.success("获取结束时间成功！");
        String realToken = token.replace(Bearer, "");
        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(realToken);
            String payLoad = jwsObject.getPayload().toString();
            JSONObject json = JSONObject.parseObject(payLoad);
            String clientId = json.getString(CLIENT_ID);
            String userId = json.getString(USER_ID);

            String SWITCH_USER_KEY = OauthConstants.SWITCH_USER_KEY + clientId + ":" + userId;

            Long exTime = RedisUtil.get(SWITCH_USER_KEY, Long.class, RedisDBType.PRIMARY);
            Map<String, Object> data = new HashMap();
            data.put("exTime", exTime);
            data.put("switchStatus", SecurityUtils.isSwitch());
            ajax.setData(data);
            return ajax;
        } catch (ParseException e) {
            log.error("获取结束时间异常", e);
            return Result.fail("获取结束时间失败");
        }
    }

    /**
     * 切换视角
     *
     * @param vo
     * @param token
     * @return
     */
    @PostMapping("/switchUser")
    public Result switchUser(@Validated @RequestBody SwitchUserRequest vo, @RequestHeader("Authorization") String token) {
        // 这里返回一个时间
        Result ajax = Result.success(Constants.AUTH_SUCCESS);
        String realToken = token.replace(Bearer, "");
        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(realToken);
            String payLoad = jwsObject.getPayload().toString();
            JSONObject json = JSONObject.parseObject(payLoad);
            String clientId = json.getString(CLIENT_ID);
            String userId = json.getString(USER_ID);
            int tokenValiditySeconds = 0;
            if (clientId.startsWith(OauthConstants.USER_LOGIN_TYPE_WEB)) {// web端用户
                tokenValiditySeconds = authProperties.getWebTokenValiditySeconds();
            } else {// app端用户
                tokenValiditySeconds = authProperties.getAppTokenValiditySeconds();
            }
            String SWITCH_USER_KEY = OauthConstants.SWITCH_USER_KEY + clientId + ":" + userId;


            SysUser sysUser;
            //1.6.50增加用户名加密解密
            if(StringUtils.isNotBlank(vo.getDecryptFlag()) && "1".equals(vo.getDecryptFlag())){
                String loginUserName = com.chinatower.platform.common.utils.security.AESUtil.decryptBase64(vo.getUserName());
                // 1.获取铁塔管理员下对应的租户管理员信息
                 sysUser = userService.selectUserWithPwdByUserName(loginUserName, "");
            }else{
                // 1.获取铁塔管理员下对应的租户管理员信息
                 sysUser = userService.selectUserWithPwdByUserName(vo.getUserName(), "");
            }



            //切换视角一定有行业编码
            String cacheKey = CacheConstants.USER_INDUSTRY_CODE + clientId + ":" + userId;
            String chooseTradeId = SecurityUtils.getLoginUser().getUser().getChooseTradeId();
            LoginUser loginUser = JSON.parseObject(RedisUtil.hGet(cacheKey, chooseTradeId, RedisDBType.PRIMARY), LoginUser.class);
            log.info("AuthorizationManager ==> switchUser: 1 getUser:cache key:{}, industryCode:{}", cacheKey, chooseTradeId);
            if (null != loginUser) {
                if (StrUtil.isNotBlank(vo.getChooseTradeId()) && StrUtil.isNotBlank(vo.getChooseAppVerCode())) {
                    LoginUser switchdUser = new LoginUser();
                    BeanUtils.copyProperties(loginUser, switchdUser);
                    try {
                        sysUser.setIndustryCode(vo.getChooseTradeId());
                        sysUser.setAppVerCode(vo.getChooseAppVerCode());
                        sysUser.setChooseAppVerCode(vo.getChooseAppVerCode());
                        sysUser.setChooseTradeId(vo.getChooseTradeId());
                        Set<String> permissions = userService.getMenuPermission(sysUser);
                        IndustryReqModel industryReqModel = new IndustryReqModel();
                        industryReqModel.setVersion(vo.getChooseAppVerCode());
                        industryReqModel.setIndustryCode(vo.getChooseTradeId());
                        List<String> permissionsList = iIndustryService.getAuthUrlList(industryReqModel).getData();
                        if (permissionsList != null) {
                            permissions.addAll(permissionsList);
                        }

                        //获取当前进入的组织信息
                        SysDept sysDept = userService.selectCurrentDept(sysUser);
                        sysUser.setDept(sysDept);
                        sysUser.setDeptId(sysDept.getDeptId());
                        List<String> strings = new ArrayList<>();
                        strings.add(sysDept.getDeptId());
                        sysUser.setDeptIds(strings);

                        //查询本级平台编码
                        String platformCode = null;
                        String platformLevel = null;
                        String platformName = null;
                        try {
                            AjaxResult ajaxResult = systemFeignClient.getConfValue(PLATFORM_CODE);
                            if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
                                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                                if(!ObjectUtils.isEmpty(sysConfig)){
                                    platformCode = sysConfig.getConfigValue();

                                    //查询平台级别
                                    List<String> list = new ArrayList<>();
                                    list.add(platformCode);
                                    Result<List<PlatformDataConfigVO>> result = cascadeConfigAPI.getInfo(list);
                                    if(ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                                        PlatformDataConfigVO platformDataConfigVO = result.getData().get(Constants.ZERO_NUM);
                                        platformLevel = platformDataConfigVO.getPlatformLevel();
                                        platformName = platformDataConfigVO.getPlatformName();
                                    }
                                }
                            }
                        }catch (Exception e1){
                            log.error("切换视角查询平台编码异常=",e1);
                        }
                        sysUser.setPlatformCode(platformCode);
                        sysUser.setPlatformLevel(platformLevel);
                        sysUser.setPlatformName(platformName);

                        // 网关过滤器也要改
                        // 切面也要改
                        //将当前登录对象中的信息除了userId全部替换为切换的目标用户信息,为兼容已有逻辑，switchdLoginUser暂时保留
                        SysUser fromUser = loginUser.getUser();
                        String userId1 = loginUser.getUser().getUserId();
                        SysUser newUser = new SysUser();
                        BeanUtils.copyProperties(sysUser, newUser);
                        newUser.setUserId(userId1);
                        loginUser.setUser(newUser);
                        loginUser.setPermissions(permissions);
                        if ("2".equals(vo.getSwitchType())) {// 租户切回到铁塔
                            //当前视角是客户视角，目标用户是铁塔用户
                            if (!(Constants.ZERO.equals(sysUser.getUserType()) && Constants.TWO.equals(fromUser.getUserType()))) {
                                log.info("切换视角失败，当前视角不是客户视角");
                                return Result.fail(ResultCode.FAILURE);
                            }

                            // 查询此租户的管理员用户信息
                            boolean flag = RedisUtil.hasKey(SWITCH_USER_KEY, RedisDBType.PRIMARY);
                            // 2.校验租户是否有权限切换到此铁塔用户
                            if (flag && sysUser.getUserId().equalsIgnoreCase(userId1)) {
                                // 登录
                                loginUser.setUser(sysUser);
                                loginUser.setSwitchdLoginUser(null);
                                RedisUtil.delete(SWITCH_USER_KEY, RedisDBType.PRIMARY);
                            } else {
                                return Result.fail(ResultCode.FAILURE);
                            }
                        } else {
                            //当前视角是铁塔视角，目标用户是客户用户
                            if (!(Constants.TWO.equals(sysUser.getUserType()) && Constants.ZERO.equals(fromUser.getUserType()))) {
                                log.info("切换视角失败，当前视角不是铁塔视角");
                                return Result.fail(ResultCode.FAILURE);
                            }

                            //铁塔账号 切换了取switchdLoginUser的userName，否则取user的
                            String userName;
                            if (!SecurityUtils.isSwitch()) {
                                loginUser.setSwitchdLoginUser(switchdUser);
                                userName = switchdUser.getUser().getUserName();
                            }else{
                                userName = switchdUser.getSwitchdLoginUser().getUser().getUserName();
                            }

                            //从redis中取数据，如果能取到，说明是跨节点切换，则不需要校验是否有权限切换，replaceToken里已经校验了
//                            String towerUserCacheKey = CacheConstants.TOWER_USER + "web_manage:" + LoginUserName;
//                            log.info("towerUserCacheKey="+towerUserCacheKey);
//                            String towerUserCacheStr = RedisUtil.get(towerUserCacheKey, RedisDBType.PRIMARY);
//                            if (StrUtil.isBlank(towerUserCacheStr)) {
//                                //校验切换权限
//                                SwitchUserDTO switchUserDTO = new SwitchUserDTO();
//                                switchUserDTO.setUserName(vo.getUserName());
//                                boolean b = userService.validateSwitchUser(switchUserDTO);
//                                if (!b) {
//                                    return Result.fail("无权限切换该用户");
//                                }
//                            }

                            if (StringUtils.isEmpty(vo.getExpireFlag())) {
                                RedisUtil.setEx(SWITCH_USER_KEY, DateUtil.current() + 60 * 10 * 1000, tokenValiditySeconds, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                            } else {
                                RedisUtil.set(SWITCH_USER_KEY, -1, RedisDBType.PRIMARY);
                            }
                        }
                        //RedisUtil.setEx(redisKey, loginUser, tokenValiditySeconds, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                        RedisUtil.hset(cacheKey, vo.getChooseTradeId(),
                                JSON.toJSONString(loginUser), RedisDBType.PRIMARY);
                        RedisUtil.expire(cacheKey, authProperties.getWebTokenValiditySeconds(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
                        log.info("AuthorizationManager ==> switchUser: 2 switchUser:cache key:{}, industryCode:{}", cacheKey, vo.getChooseTradeId());


                    } catch (Exception e) {
                        log.error("根据行业编码、版本号获取鉴权url获取失败", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return ajax;
        } catch (ParseException e) {
            log.error("切换异常");
            log.error(e.getMessage(), e);
            e.printStackTrace();
            return Result.fail("切换失败");
        }
    }

    public Result buildLoginInfo(SysUser targetUser) {
        String userName = targetUser.getUserName();
        String password = SecurityUtils.decryptPassword(targetUser.getPassword());
        return prepareToken(userName, password, new LoginVo().getAppId());
    }

    /**
     * 通过用户名判断当前用户状态
     *
     * @param username
     * @return
     * @deprecated 安全漏洞，废弃
     */
    @GetMapping("/getUserStateByUserName")
    @Deprecated
    public Result getUserStateByUserName(@RequestParam("username") String username) {
//		Result resultObj = userService.getUserStateByUserName(username);
//
//		/*脱敏处理*/
//		if (resultObj.getCode() == 200) {
//			String replacePhone = resultObj.getData().toString().replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2");
//			resultObj.setData(replacePhone);
//		}
        return Result.success("请求成功");
    }

    /**
     * 判断用户名和手机号是否匹配
     *
     * @return
     */
    @PostMapping("/getStateByUserNameAndPhone")
    public Result getStateByUserNameAndPhone(@RequestBody CheckPasswordDTO dto) {
        return userService.getStateByUserNameAndPhone(dto);
    }

    /**
     * 发送手机验证码
     *
     * @return
     */
    @PostMapping("/sendVerifyCode")
    @Deprecated
    public Result sendVerifyCode(@RequestBody CheckPasswordDTO dto) { // phone手机号加密
        return userService.sendVerifyCode(dto);
    }

    /**
     * 校验手机验证码
     *
     * @param username
     * @param verifyCode
     * @return
     */
    @GetMapping("/checkVerifyCode")
    public Result checkVerifyCode(@RequestParam("username") String username, @RequestParam("verifyCode") String verifyCode) {
        return userService.checkVerifyCode(username, verifyCode);
    }

    /**
     * 校验输入的新密码和确认密码
     *
     * @param dto
     * @return
     */
    @PostMapping("/checkPasswordRepeat")
    public Result checkPasswordRepeat(@RequestBody ForgotPasswordDTO dto) {
        return userService.checkPasswordRepeat(dto);
    }

    /**
     * 校验输入的新密码和确认密码,只校验密码
     * 场景：前端失焦
     *
     * @param dto
     * @return
     */
    @PostMapping("/checkOnlyPasswordRepeat")
    public Result checkOnlyPasswordRepeatOnly(@RequestBody ForgotPasswordDTO dto) {
        return userService.checkOnlyPasswordRepeat(dto);
    }

    /**
     * 在‘忘记密码’中更新用户密码
     *
     * @param dto
     * @return
     */
    @PostMapping("/updateByForgerPassword")
    public Result updateByForgerPassword(@RequestBody ForgotPasswordDTO dto) {
        return userService.updateByForgerPassword(dto);
    }

    /**
     * 验证当前token是否有效
     */
    @PostMapping("/valid")
    public Result valid() {
        //不用实现。网关判断
        return Result.success("success");
    }

    /**
     * 获取登录相关配置
     */
    @PostMapping("/getConfig")
    public Result<AuthConfig> getConfig() {
        AuthConfig config = new AuthConfig();
        BeanUtils.copyProperties(authProperties, config);
        return Result.data(config);
    }

    /**
     * 生成二维码code
     */
    @GetMapping("/getQrCode")
    public Result<QrLoginDTO> getQrCode() {
        return userService.getQrCode();
    }

    /**
     * 校验二维码数据
     */
    @PostMapping("/checkQrCode")
    public Result<QrLoginDTO> checkQrCode(@Validated @RequestBody QrLoginReq qrLoginDTO) {
        return userService.checkQrCode(qrLoginDTO);
    }

    /**
     * 更新二维码数据
     */
    @PostMapping("/updateQrInfo")
    public Result<String> updateQrInfo(@Validated @RequestBody QrLoginDTO qrLoginDTO) {
        return userService.updateQrInfo(qrLoginDTO);
    }

    /**
     * 扫码登录
     */
    @PostMapping("/doQrLogin")
    public Result doQrLogin(@Validated @RequestBody QrLoginReq qrLoginDTO) {
        return userService.doQrLogin(qrLoginDTO);
    }

    /**
     *  根据token返回登录后的必要信息
     * 下沉节点接口，中心节点扫码登录时调用下沉节点该接口，返回登录后的必要信息
     * @param token token  加密串
     * @return
     */
    @PostMapping("/userIdLogin")
    public Result userIdLogin(@RequestParam String token) {
        log.info("userIdLogin入参=" + token);
        return userService.userIdLogin(token);
    }

    /**
     * 切换视角虚拟登录
     */
    @PostMapping("/doSwitchLogin")
    public Result<String> doSwitchLogin(@RequestBody Map<String, String> param) {
        return userService.doSwitchLogin(param);
    }

    /**
     * 校验加密串
     */
    @PostMapping("/checkSwitchId")
    public Result<SwitchLoginVo> checkSwitchId(@RequestBody SwitchLoginVo switchLoginVO) {
        return userService.checkSwitchId(switchLoginVO.getUuid());
    }

    /**
     * 统一登录接口
     * 1.6.40统一登录改造  app pc使用
     *
     * @param vo vo
     * @return Result
     */
    @PostMapping("/loginGenerateCode")
    public Result loginGenerateCode(@Validated @RequestBody LoginVo vo) {
        Result ajax = login(vo);
        String appId = vo.getAppId();
        if(Constants.ONE.equals(vo.getSource())){
            appId = Constants.APP_ID;
        }
        String clientId = appId.split(":")[0];
        //web登录重置最大登录次数
        try {
            if (ClientType.WEB_MANAGE.getClientType().equals(clientId)){
                //如果是web端 重置连续刷新过期时间次数0
                SysUser user = userService.selectUserByUserName(vo.getUsername());
                String cacheKey = CacheConstants.getUserCacheKey(clientId, user.getUserId());
                log.info("开始重置刷新次数:{}", cacheKey);
                //更新用户对应web端刷新次数
                BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.common.core.constant.Constants.REFRESH_COUNT_PC);
                Query updateQuery = new Query();
                updateQuery.addCriteria(Criteria.where(com.chinatower.common.core.constant.Constants.USER_KEY).is(cacheKey));
                Update update = new Update();
                update.set(com.chinatower.common.core.constant.Constants.USER_REFRESH_COUNT,0);
                bulkOperations.upsert(updateQuery, update);
                bulkOperations.execute();
                log.info("重置成功:{}", cacheKey);
            }
        }catch (Exception e){
            log.info("重置失败");
        }
        HttpServletRequest request = RequestUtils.getRequest();
        String toNode = request.getHeader("preToNode");
        log.info("入参:pre to node : {}", toNode);
        if (ObjectUtil.isNotEmpty(ajax.getData()) && StringUtils.isNotEmpty(toNode)) {
            Map<String, Object> data = (Map<String, Object>) ajax.getData();
            //增加节点标识
            String currentFlag = request.getHeader("current-flag");
            if (ObjectUtil.isNotEmpty(currentFlag)) {
                //(1-是本节点 0-不是本节点)
                data.put(CURRENT_FLAG, currentFlag);
            } else {
                data.put(CURRENT_FLAG, "1");
            }

            String redirectUrl = "";
            if (RedisUtil.hasKey(Constants.COLONY_RELATION_ALIAS_ALL + toNode, RedisDBType.PRIMARY)) {
                String str = RedisUtil.get(Constants.COLONY_RELATION_ALIAS_ALL + toNode, RedisDBType.PRIMARY);
                ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);
                for (ColonyApp app : cascadeConfigSyncVO.getApps()) {
                    if ("general".equals(app.getAppId())) {
                        String extra = app.getExtra();
                        if (StringUtil.isNotEmpty(extra)) {
                            JSONObject parse = JSON.parseObject(extra);
                            if (parse.containsKey(GENERAL_URL)) {
                                String generalInternetUrl = (String) parse.get(GENERAL_URL);
                                if (StringUtils.isNotEmpty(generalInternetUrl)) {
                                    redirectUrl = generalInternetUrl;
                                }
                            }
                        } else {
                            redirectUrl = app.getAppAddr();
                        }
                    }
                }
            }
            data.put("redirectUrl", redirectUrl);
            //生成短token 并且返回
            String uuid = userService.tokenChange(data);
            data.put("uuid", uuid);
        }
        return ajax;
    }

    /**
     * sdk转api模拟登录
     * sdk转api需求，判断userId失效，则调用此方法，模拟登录，获取token
     * @param vo
     * @return
     */
    @PostMapping("/sdkToApiLogin")
    public Result sdkToApiLogin(@RequestBody SdkToApiLoginVo vo) {
        Result result = userService.sdkToApiLogin(vo.getUserId());
        if(ResultCode.SUCCESS.getCode() == result.getCode() && !ObjectUtils.isEmpty(result.getData())) {
            Map map = (Map)result.getData();
            String token = Bearer + map.get("access_token");
            LoginVo loginVo = new LoginVo();
            BeanUtils.copyProperties(vo,loginVo);
            //选择行业
            this.chooseTrade(loginVo,token);
        }
        return result;
    }


    /**
     * 首页切换下沉节点登录,模拟扫码登录调用doQrLogin
     * @param authReplaceTokenVO
     * @return
     */
    @PostMapping("/doUserIdLogin")
    public Result doUserIdLogin(@RequestBody AuthReplaceTokenVO authReplaceTokenVO) {
        log.info("doUserIdLogin=" + JSON.toJSONString(authReplaceTokenVO));
        return userService.doUserIdLogin(authReplaceTokenVO);
    }
}
