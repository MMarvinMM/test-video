package com.chinatower.platform.auth.project.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.auth.api.AuthFeignClient;
import com.chinatower.auth.api.entity.WxColonyCodeVo;
import com.chinatower.auth.api.entity.WxUnionIdVo;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.auth.model.WxLoginVo;
import com.chinatower.platform.auth.model.WxLogoutVo;
import com.chinatower.platform.auth.project.service.WxService;
import com.chinatower.platform.auth.service.TokenService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.chinatower.platform.constants.Constants.PLATFORM_CODE;

/**
 * video-auth
 *
 * 功能说明: 微信端登录相关接口
 *
 * @author zhujun
 * @date 2022-11-21
 */
@RestController
@Slf4j
@AllArgsConstructor
public class WxLoginController {

    private static final String WX_MANAGE = "wx_manage";
    private static final String USER_NAME = "userName";

    public static final String MIDDLE_LINE = "-";

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LogOutController logOutController;

    /**
     * 微信公众号推送消息业务层接口
     */
    @Autowired
    private WxService wxService;

    /**
     * 微信公众号推送消息业务层接口
     */
    @Autowired
    private LoginController loginController;

    @Resource
    SystemFeignClient systemFeignClient;

    private static final String ERROR_MSG = "登录异常！";

    /**
     * 缓存codekey
     */
    private static final String WX_LOGIN_CODE_KEY = "wxLogin:code:key:";

    /**
     * 缓存时间 2小时
     */
    private static final int WX_LOGIN_CODE_KEY_TIME = 7200;
    private static final String INDUSTRYCODE = "industryCode";


    private static final int STATUS_TWO_HUNDRED = 200;

    private static final String CENTER_CODE = "10000000001";

    private static final String COMMA = ",";

    private static final Integer ZERO = 0;

    private static final String USER_NAME_LINE = "user_name";

    private static final String USER_NAME_SMALL = "username";

    private static final String OPEN_ID = "openId";

    private static final String WX = "wx";
    /**
     * 微信公众号账号绑定接口
     * @source mp微信公众号 miniApp小程序
     */
    @PostMapping("/wxBind")
    public Result wxBind(@Validated @RequestBody WxLoginVo vo) {
        log.info("wxBind入参报文：{}",JSON.toJSONString(vo));
        Result loginResult = Result.fail("请求参数错误！");

        if (StringUtils.isBlank(vo.getUnionId())) {
            return loginResult;
        }
        String unionId = RsaUtils.decryptByPrivateKey(vo.getUnionId());
        String openId = RsaUtils.decryptByPrivateKey(vo.getOpenId());
        String miniOpenId = RsaUtils.decryptByPrivateKey(vo.getMiniOpenId());
        String nickName = RsaUtils.decryptByPrivateKey(vo.getNickName());

        log.info("解密后的unionId:{},openId:{},miniOpenId:{}", unionId, openId, miniOpenId);

        WxLoginVo wxVo = new WxLoginVo();
        wxVo.setUserName(vo.getUsername());
        wxVo.setUnionId(unionId);
        wxVo.setOpenId(openId);
        wxVo.setMiniOpenId(miniOpenId);
        wxVo.setNickName(nickName);
        wxVo.setAppId(vo.getAppId());
        try {
            // 判断微信用户是否存在
            int hasUser = wxService.getWxUserCount(wxVo);
            if(hasUser > 0){
                wxService.updateWxUser(wxVo);
            }else{
                wxService.insertWxUser(wxVo);
            }
            loginResult = loginController.login(vo);
            if(loginResult.getCode() != STATUS_TWO_HUNDRED){
                return loginResult;
            }else{
                String curChooseIndustryCode = ((Map<String, Object>)loginResult.getData()).get("curChooseIndustryCode").toString();
                wxVo.setTradeId(curChooseIndustryCode);
                // 解除绑定的用户
                wxService.releaseBindUser(wxVo);
                List<Map<String, String>> userInfo = wxService.getBandUserByWx(wxVo);
                if(userInfo != null && !userInfo.isEmpty()){
                    wxService.updateBindUser(wxVo);
                }else{
                    wxService.bindUser(wxVo);
                }
                if(vo.getOpenId() != null){
                    // 发送绑定成功欢迎语
                    wxService.sendBandMsg(vo.getOpenId());
                }
                // 获取接收微信通知状态
                wxService.getReceiveMsgState(wxVo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.fail(ERROR_MSG);
        }
        Map data = (Map)loginResult.getData();
        if(data == null){
            data = new HashMap();
            wxVo.setUserName(AESUtil.encryptBase64(wxVo.getUserName()));
            data.put("wx", wxVo);
            loginResult.setData(data);
        }else{
            String user_name = "";
            if(!ObjectUtils.isEmpty(data.get(USER_NAME_LINE))){
                user_name = (String)data.get(USER_NAME_LINE);
                data.put(USER_NAME_LINE,AESUtil.encryptBase64(user_name));
            }
            wxVo.setUserName(AESUtil.encryptBase64(wxVo.getUserName()));
            data.put("wx", wxVo);
            CascadeConfigSyncVO cascade = getCascade();
            data.put("cascade", cascade);
        }

        try {
            String platformCode = getCascadeCode();
            /*
            中心 -> 不做处理
            属地 -> 调用级联网关 发送到中心节点的接口
         */
            if (StringUtils.isNotEmpty(platformCode) && !CENTER_CODE.equals(platformCode)) {
                String str = unionId + COMMA
                        + openId + COMMA
                        + miniOpenId + COMMA
                        + platformCode + COMMA
                        + DateUtil.format(new Date(), "yyyyMMddHHmmss");
                String encStr = AESUtil.encryptBase64(str);
                WxColonyCodeVo wxColonyCodeVo = new WxColonyCodeVo();
                wxColonyCodeVo.setEncStr(encStr);
                log.info("加密串==>" + encStr);
                RemoteNodeUtils.create(CENTER_CODE)
                        .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                        .colonyBind(wxColonyCodeVo);
            }
        } catch (Exception e) {
            log.info("属地节点微信登陆保存信息到中心节点出错===" + e);
        }
        return loginResult;
    }


    /**
     * 微信公众号/小程序登陆接口
     * @source miniApp小程序
     */
    @PostMapping("/wxLogin")
    public Result wxLogin(@RequestBody WxUnionIdVo wxUnionIdVo) {
        String code = wxUnionIdVo.getCode();
        String source = wxUnionIdVo.getSource();
        log.info("wxLogin入参报文：code:"+wxUnionIdVo.getCode()+",source:"+source+",vo:" + JSON.toJSONString(wxUnionIdVo));
        Result loginResult = Result.fail("请求参数错误！");
        if (StringUtils.isBlank(code)) {
            return loginResult;
        }
        WxLoginVo vo = new WxLoginVo();
        if (!ObjectUtils.isEmpty(wxUnionIdVo)) {
            vo.setUnionId(AESUtil.decryptBase64(wxUnionIdVo.getUnionId()));
            vo.setOpenId(AESUtil.decryptBase64(wxUnionIdVo.getOpenId()));
            vo.setMiniOpenId(AESUtil.decryptBase64(wxUnionIdVo.getMiniOpenId()));
            vo.setNickName(wxUnionIdVo.getNickName());
        }
        //属地账号openId
        String colonyOpenId = null;
        try {
            if("miniApp".equals(source)){
                if (StringUtils.isEmpty(vo.getUnionId())
                        && StringUtils.isEmpty(vo.getMiniOpenId())
                        && StringUtils.isEmpty(vo.getSessionKey())) {
                    log.info("未传入unionId,调用wx获取未传入unionId");
                    WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
                    vo.setMiniOpenId(session.getOpenid());
                    vo.setUnionId(session.getUnionid());
                    vo.setSessionKey(session.getSessionKey());
                    //获取中心节点bs_wx_user表中的 openId
                    String openId = wxService.getOpenIdByUnionId(session.getUnionid());
                    log.info("根据unionId获取中心的openId:{},{}", session.getUnionid(), openId);
                    if (StringUtils.isNotEmpty(openId)) {
                        colonyOpenId = openId;
                    }
                    log.info("colonyOpenId={}", colonyOpenId);
                    wxMaService.getSubscribeService();
                }
            }else if("superTest".equals(source)){
                vo.setUnionId(code);
            }else {
                if (StringUtils.isEmpty(vo.getUnionId())
                        && StringUtils.isEmpty(vo.getOpenId())
                        && StringUtils.isEmpty(vo.getNickName())) {
                    log.info("未传入unionId,调用wx获取accessToken");
                    WxOAuth2Service oAuth2Service = wxMpService.getOAuth2Service();
                    // 2023-01-05 判断是否可以通过code获取accessToken
                    String codeCacheKey = WX_LOGIN_CODE_KEY + code;
                    String redisToken = RedisUtil.get(codeCacheKey, RedisDBType.PRIMARY);
                    // 利用code获取accessToken
                    WxOAuth2AccessToken accessToken;
                    if(StringUtils.isNotBlank(redisToken)){
                        accessToken = JSONObject.parseObject(redisToken,WxOAuth2AccessToken.class);
                    }else {
                        accessToken = oAuth2Service.getAccessToken(code);
                        //设置缓存过期时间
                        RedisUtil.set(codeCacheKey,JSONObject.toJSONString(accessToken),WX_LOGIN_CODE_KEY_TIME, TimeUnit.SECONDS,RedisDBType.PRIMARY);
                    }

                    // 利用accessToken获取用户信息
                    WxOAuth2UserInfo userInfo = oAuth2Service.getUserInfo(accessToken, null);
                    vo.setOpenId(userInfo.getOpenid());
                    vo.setUnionId(userInfo.getUnionId());
                    vo.setNickName(userInfo.getNickname());
                }
            }
            log.info("WxLoginVo:"+JSON.toJSONString(vo));
            loginResult = prepareWxToken(vo);
            log.info("loginResult:"+JSON.toJSONString(loginResult));
            //如果失败
            if (ResultCode.SUCCESS.getCode() == loginResult.getCode()) {
                if(STATUS_TWO_HUNDRED == (loginResult.getCode())){
                    List<Map<String, String>> bindUserNames = wxService.getBandUserByWx(vo);
                    //加密用户名
                    if(!CollectionUtils.isEmpty(bindUserNames)){
                        for (int i = 0; i < bindUserNames.size(); i++) {
                            Map<String, String> bindUserName = bindUserNames.get(i);
                            bindUserName.put(USER_NAME_SMALL, AESUtil.encryptBase64(bindUserName.get(USER_NAME_SMALL)));
                        }
                    }
                    vo.setBindUserNames(bindUserNames);
                    // 设置上次登录的行业编码
                    Map<String,String> bandUserInfo = wxService.getIndustryCodeByWx(vo);
                    if(bandUserInfo != null){
                        Map<String, Object> tokenObj = (Map<String, Object>)loginResult.getData();
                        tokenObj.put("curChooseIndustryCode",bandUserInfo.get(INDUSTRYCODE));
                        vo.setTradeId(bandUserInfo.get(INDUSTRYCODE));
                        for (Object trade :(List)tokenObj.get("tradeVersions")) {
                            if(bandUserInfo.get(INDUSTRYCODE).equals(((SysIndustryViewRel)trade).getIndustryCode())){
                                vo.setAppVerCode(((SysIndustryViewRel)trade).getAppVerCode());
                                break;
                            }
                        }
                        if(vo.getAppVerCode() != null){
                            loginController.chooseTrade(vo, "Bearer " + tokenObj.get("access_token").toString());
                        }
                    }
                    // 获取接收微信通知状态
                    wxService.getReceiveMsgState(vo);
                }
            } else {
                //判断是否是属地节点登录 最开始判断   vx code 只能使用一次
                String colonyCode = selectColonyCodeByUnionId(vo.getUnionId());
                if (StringUtils.isNotEmpty(colonyCode) && !colonyCode.equals(CENTER_CODE)) {
                    WxUnionIdVo remoteUnion = new WxUnionIdVo();
                    remoteUnion.setUnionId(AESUtil.encryptBase64(vo.getUnionId()));
                    remoteUnion.setOpenId(AESUtil.encryptBase64(vo.getOpenId()));
                    remoteUnion.setMiniOpenId(AESUtil.encryptBase64(vo.getMiniOpenId()));
                    remoteUnion.setCode(code);
                    remoteUnion.setSource(source);

                    Result result = RemoteNodeUtils.create(colonyCode)
                            .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                            .wxLogin(remoteUnion);
                    log.info("请求属地节点wxLogin返回:{}", JSON.toJSONString(result));
                    if (result.getCode() == STATUS_TWO_HUNDRED) {
                        Object data = result.getData();
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(data));
                        log.info("jsonObject:{}", JSON.toJSONString(jsonObject));
                        if (jsonObject.containsKey(WX)) {
                            JSONObject wx = jsonObject.getJSONObject(WX);
                            log.info("wx:{}", JSON.toJSONString(wx));
                            if (wx.containsKey(OPEN_ID)) {
                                String openId = wx.getString(OPEN_ID);
                                if (StringUtils.isEmpty(openId)) {
                                    wx.put(OPEN_ID, colonyOpenId);
                                }
                            } else {
                                wx.put(OPEN_ID, colonyOpenId);
                            }
                            jsonObject.put(WX, wx);
                        }
                        result.setData(jsonObject);
                    }
                    log.info("返回属地节点wxLogin,添加openId后的数据:{}", JSON.toJSONString(result));
                    return result;
                }
            }
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return Result.fail(ERROR_MSG);
        }
        if(loginResult.getCode() != STATUS_TWO_HUNDRED){
            Map data = new HashMap();
            data.put("wx",vo);
            loginResult.setData(data);
            loginResult.setCode(ResultCode.SUCCESS.getCode());
        }else{
            Map data = (Map)loginResult.getData();
            String user_name = "";
            if(!ObjectUtils.isEmpty(data.get(USER_NAME_LINE))){
                user_name = (String)data.get(USER_NAME_LINE);
                data.put(USER_NAME_LINE,AESUtil.encryptBase64(user_name));
            }
            data.put("wx",vo);
            CascadeConfigSyncVO cascade = getCascade();
            data.put("cascade", cascade);
        }
        return loginResult;
    }

    /**
     * 微信公众号/小程序登出接口
     * @Param unionId
     * @source miniApp小程序
     */
    @PostMapping("/wxLogout")
    public Result wxLogout(@RequestBody WxLoginVo vo) {
        try {
            log.info("登出unionId:{}",vo.getUnionId());
            String userId = SecurityUtils.getLoginUser().getUser().getUserId();
            String cacheKey = CacheConstants.USER_INDUSTRY_CODE + WX_MANAGE + ":" + userId;
            String lastKey = CacheConstants.USER_LAST_INDUSTRY_CODE + WX_MANAGE + ":" + userId;
            if (RedisUtil.hasKey(lastKey, RedisDBType.PRIMARY)) {
                RedisUtil.delete(lastKey, RedisDBType.PRIMARY);
            }
            RedisUtil.delete(cacheKey, RedisDBType.PRIMARY);
            logOutController.logout();
            // 解除绑定的用户
            wxService.releaseBindUser(vo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.fail(ERROR_MSG);
        }
        return Result.success("操作成功");
    }

    /**
     * 微信公众号接收消息控制开关
     * @Param vo
     * @source miniApp小程序
     */
    @PostMapping("/wxUpdateMsgControl")
    public Result wxUpdateMsgControl(@RequestBody WxLoginVo vo) {
        try {
            if(StringUtils.isEmpty(vo.getUnionId())){
                return Result.fail("用户身份参数不正确");
            }
            if(StringUtils.isEmpty(vo.getReceiveMsg())
                    || !"Y".equals(vo.getReceiveMsg())
                    && !"N".equals(vo.getReceiveMsg())){
                return Result.fail("请正确传参开关变量");
            }
            log.info("微信公众号接收通知开关:{}",JSON.toJSONString(vo));
            // 更改开关
            wxService.wxUpdateMsgControl(vo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.fail("接收消息变更失败");
        }
        return Result.success("操作成功");
    }

    private Result prepareWxToken(@RequestBody WxLoginVo wxLoginVo) {
        Result tokenResult;
        Map<String, String> userMap = null;
        Map<String, Object> tokenMap;
        try {
            userMap = wxService.loginByWx(wxLoginVo.getUnionId());
            if (userMap == null) {
                tokenResult = Result.fail("未检测到系统绑定账户！");
            } else {
                if (userMap.get(OPEN_ID) == null && wxLoginVo.getOpenId() != null
                        || userMap.get("miniOpenId") == null && wxLoginVo.getMiniOpenId() != null
                        || userMap.get("nickName") == null && wxLoginVo.getNickName() != null
                ) {
                    wxService.updateWxUser(wxLoginVo);
                }
                String passwd = SecurityUtils.decryptPassword(userMap.get("passwd"));
                tokenMap = tokenService.login(userMap.get(USER_NAME), passwd, WX_MANAGE + ":123456");
                tokenResult = Result.data(tokenMap);
            }
        } catch (CustomException e) {
            log.error(e.getMessage(), e);
            tokenResult = Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            tokenResult = Result.fail("认证失败！");
        }
        return tokenResult;
    }

    private String selectColonyCodeByUnionId(String unionId) {
        return wxService.selectColonyCodeByUnionId(unionId);
    }

    private CascadeConfigSyncVO getCascade() {
        String platformCode = getCascadeCode();
        if (StringUtils.isNotEmpty(platformCode)) {
            return wxService.findCascadeByCode(platformCode);
        }
        return null;
    }

    private String getCascadeCode() {
        String platformCode = null;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PLATFORM_CODE);
        if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if(!ObjectUtils.isEmpty(sysConfig)) {
                platformCode = sysConfig.getConfigValue();
            }
        }
        return platformCode;
    }

    /**
     * 创建微信公众号底部菜单
     * @return
     */
    @ApiOperation(value = "创建微信公众号底部菜单", produces = "application/json")
    @PostMapping("/wx/createMenu")
    public Result createMenu() {
        try {
            return wxService.createMenu();
        } catch (Exception e) {
            log.error("创建微信公众号底部菜单异常");
            log.error(e.getMessage(), e);
            return Result.fail("创建微信公众号底部菜单异常，请联系管理员");
        }
    }

    /**
     * 选择行业（小程序）
     * @param vo
     * @return
     */
    @PostMapping("/wx/chooseTrade")
    public Result chooseTrade(@RequestBody WxLoginVo vo, @RequestHeader("Authorization") String token) {
        Result result = loginController.chooseTrade(vo, token);
        if("选择行业成功".equals(result.getMsg())){
            wxService.updateIndustryCode(vo);
        }
        return result ;
    }

    /**
     * 将用户失效，并发送重新登录的微信提示
     * @param vo
     * @return
     */
    @PostMapping("/wx/invalidUser")
    public Result invalidUser(@RequestBody WxLogoutVo vo) {
        try {
            log.info("将用户失效，并发送重新登录的微信提示参数{}",vo);
            if(wxService.invalidUser(vo.getUserId(), vo.getUserName())){
                return Result.success("操作成功");
            }else {
                return Result.fail("将用户失效，并发送重新登录的微信提示失败，请联系管理员");
            }
        } catch (Exception e) {
            log.error("将用户失效，并发送重新登录的微信提示失败");
            log.error(e.getMessage(), e);
            return Result.fail("将用户失效，并发送重新登录的微信提示失败，请联系管理员");
        }
    }

    /**
     * 属地调用中心   colony bind
     * @param vo
     * @return
     */
    @PostMapping("/wx/colonyBind")
    public Result<String> colonyBind(@RequestBody WxColonyCodeVo vo) {
        log.info("属地调用中心wx信息绑定:{}", JSON.toJSONString(vo));
        return wxService.colonyBind(vo);
    }

}
