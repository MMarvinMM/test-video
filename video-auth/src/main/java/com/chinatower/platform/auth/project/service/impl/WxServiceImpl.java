package com.chinatower.platform.auth.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.auth.api.entity.WxColonyCodeVo;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.config.WxProperties;
import com.chinatower.platform.auth.model.WxLoginVo;
import com.chinatower.platform.auth.project.entity.ButtomTreeVo;
import com.chinatower.platform.auth.project.entity.WxBttomMenuEntity;
import com.chinatower.platform.auth.project.mapper.WxMapper;
import com.chinatower.platform.auth.project.mapper.WxMenuMapper;
import com.chinatower.platform.auth.project.service.WxService;
import com.chinatower.platform.auth.project.util.BaseTreeHelper;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.json.GsonParser;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 微信公众号推送 service
 * @Author: 朱君
 * @Date: 2022/11/15
 **/
@Service
@Slf4j
@RefreshScope
@AllArgsConstructor
public class WxServiceImpl extends ServiceImpl<WxMenuMapper, WxBttomMenuEntity> implements WxService {

    @Resource
    private WxMpService wxMpService;


    private static final String COMMA = ",";
    /**
     * 微信公众号推送消息模块dao
     */
    @Resource
    private WxMapper wxMapper;

    /**
     * 微信公众号底部菜单
     */
    @Resource
    private WxService wxService;

    // 配置参数
    private final WxProperties properties;

    @Override
    public Result<String> createMenu() {
        //查询所有菜单
        List<WxBttomMenuEntity> menus = wxService.list(new LambdaQueryWrapper<WxBttomMenuEntity>()
                .eq(WxBttomMenuEntity::getDeleteFlag, "0")
                .orderByAsc(WxBttomMenuEntity::getOrderNum));

        List<ButtomTreeVo> wxMenus = new ArrayList<>();
        menus.forEach(menu -> {
            ButtomTreeVo mm = ButtomTreeVo.builder().build();
            BeanUtil.copyProperties(menu, mm);
            mm.setName(menu.getName());
            mm.setPid(menu.getParentId());
            wxMenus.add(mm);
        });
        //生成树形菜单，子节点在children里面
        List<ButtomTreeVo> list = BaseTreeHelper.generateTrees(wxMenus);

        WxMenu wxMenu = new WxMenu();
        for (ButtomTreeVo vo : list) {
            //如果有子节点进行处理
            if (vo.getChildren() != null) {
                //设置二级按钮节点
                setSubButtonList(wxMenu, vo);
            } else {
                //没有子节点的处理方式
                WxMenuButton mainBtn2 = new WxMenuButton();
                mainBtn2.setType(vo.getFuncType());
                mainBtn2.setName(vo.getName());
                mainBtn2.setUrl(properties.getServerHost()+vo.getLinkUrl());
                // 判断是否为小程序菜单
                if(WxConsts.MenuButtonType.MINIPROGRAM.equals(vo.getFuncType())){
                    mainBtn2.setAppId(properties.getMiniAppId());
                    mainBtn2.setPagePath(vo.getLinkUrl());
                }else if(StringUtils.isNotEmpty(vo.getLinkUrl())){
                    String url = wxMpService.getOAuth2Service().buildAuthorizationUrl(
                            properties.getServerHost()+vo.getLinkUrl(),
                            WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
                    mainBtn2.setUrl(url);
                }
                wxMenu.getButtons().add(mainBtn2);
            }
        }
        try {
            log.info(JSON.toJSONString(wxMenu));
            this.wxMpService.getMenuService().menuCreate(wxMenu);
            return Result.success("创建菜单成功");
        } catch (WxErrorException e) {
            log.error("创建菜单失败：{}", e.getMessage());
            return Result.fail("创建菜单失败");
        }
    }

    /**
     * 设置二级按钮节点
     * @param wxMenu
     * @param vo
     */
    private void setSubButtonList(WxMenu wxMenu, ButtomTreeVo vo) {
        WxMenuButton mainBtn1 = new WxMenuButton();
        mainBtn1.setName(vo.getName());
        List<WxMenuButton> buttomTreeVos = new ArrayList<>();
        for (ButtomTreeVo vv : vo.getChildren()) {
            WxMenuButton wxMenuButton = new WxMenuButton();
            wxMenuButton.setName(vv.getName());
            wxMenuButton.setType(vv.getFuncType());
            wxMenuButton.setUrl(properties.getServerHost()+vv.getLinkUrl());
            // 判断是否为小程序菜单
            if(WxConsts.MenuButtonType.MINIPROGRAM.equals(vv.getFuncType())){
                wxMenuButton.setAppId(properties.getMiniAppId());
                wxMenuButton.setPagePath(properties.getMiniAppEventUrl());
            }else if(StringUtils.isNotEmpty(vv.getLinkUrl())){
                String url = wxMpService.getOAuth2Service().buildAuthorizationUrl(
                        properties.getServerHost()+vv.getLinkUrl(),
                        WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
                wxMenuButton.setUrl(url);
            }
            buttomTreeVos.add(wxMenuButton);
        }
        //放入数组中
        WxMenuButton[] array = new WxMenuButton[buttomTreeVos.size()];
        for (int i = 0; i < buttomTreeVos.size(); i++) {
            array[i] = buttomTreeVos.get(i);
        }
        mainBtn1.setSubButtons(buttomTreeVos);
        wxMenu.getButtons().add(mainBtn1);
    }

    public Map<String, String> loginByWx(String unionId) {
        return wxMapper.loginByWx(unionId);
    }

    public List<Map<String, String>> getBandUserByWx(WxLoginVo vo){
        return wxMapper.getBandUserByWx(vo);
    }

    public Map<String, String> getIndustryCodeByWx(WxLoginVo vo){
        return wxMapper.getIndustryCodeByWx(vo);
    }

    public int releaseBindUser(WxLoginVo vo) {
        return wxMapper.releaseBindUser(vo);
    }

    public int bindUser(WxLoginVo vo) {
        return wxMapper.bindUser(vo);
    }

    public int updateBindUser(WxLoginVo vo) {
        return wxMapper.updateBindUser(vo);
    }

    public int getWxUserCount(WxLoginVo vo) {
        return wxMapper.getWxUserCount(vo);
    }

    public int insertWxUser(WxLoginVo vo) {
        return wxMapper.insertWxUser(vo);
    }

    public int updateWxUser(WxLoginVo vo) {
        return wxMapper.updateWxUser(vo);
    }

    public int updateUnSubscribe(String openId) {
        return wxMapper.updateUnSubscribe(openId, "1");
    }

    public int updateSubscribe(String openId) {
        return wxMapper.updateUnSubscribe(openId, "0");
    }

    public int updateIndustryCode(WxLoginVo vo) {
        return wxMapper.updateIndustryCode(vo);
    }

    /**
     * 将用户失效，并发送重新登录的微信提示
     * @param userId
     * @return
     */
    public boolean invalidUser(String userId,String userName) throws WxErrorException {
        //根据userId查询查询微信用户信息
        List<Map<String,String>> openIds = wxMapper.findUserByUserId(userId);

        //修改微信用户和账号的绑定状态为N
        wxMapper.invalidUser(userId);

        String cacheKey = CacheConstants.USER_INDUSTRY_CODE + "wx_manage:" + userId;
        //清除用户token的方法，使token失效无法重新请求
        RedisUtil.delete(cacheKey, RedisDBType.PRIMARY);

        //发送微信公众号文字消息(判断是否关注过公众号)
        if(!openIds.isEmpty()){
            for (Map<String,String> openIdMap : openIds) {
                // 创建模板消息，设置模板id、指定模板消息要发送的目标用户
                WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                        .templateId(properties.getPwdTemplateId())
                        .toUser(openIdMap.get("openId"))
                        .build();
                // 填充模板消息中的变量
                wxMpTemplateMessage.addData(new WxMpTemplateData("keyword1", userName));
                wxMpTemplateMessage.addData(new WxMpTemplateData("keyword2", DateUtils.getTime()));
                wxMpTemplateMessage.addData(new WxMpTemplateData("remark", "您的账号已于APP端口/PC端修改了密码。请重新登录"));
                // 发送模板消息，返回消息id
                this.sendTemplateMsg(wxMpTemplateMessage,true);
            }

        }

        return true;
    }

    /**
     * token
     */
    private static final String ACCESS_TOKEN_KEY = "wx:accessToken:";
    /**
     * token缓存时间 2小时
     */
    private static final int ACCESS_TOKEN_KEY_CACHE_TIME = 7200;

    /**
     * 自定义发送
     * @param templateMessage
     * @return
     * @throws WxErrorException
     */
    public JSONObject sendTemplateMsg(WxMpTemplateMessage templateMessage,boolean firstSend) throws WxErrorException {
        String cacheKey = ACCESS_TOKEN_KEY + properties.getAppId();
        //获取token
        String accessToken = RedisUtil.get(cacheKey, RedisDBType.PRIMARY);
        if(StringUtils.isBlank(accessToken)){
            accessToken  = this.wxMpService.getAccessToken(true);
            RedisUtil.set(cacheKey,accessToken,ACCESS_TOKEN_KEY_CACHE_TIME, TimeUnit.SECONDS,RedisDBType.PRIMARY);
        }

        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        log.info("发送公众号模板消息url：{}",requestUrl);
        log.info("发送公众号模板消息参数：{}",templateMessage.toJson());
        String responseContent = HttpUtil.post(requestUrl, templateMessage.toJson());
        log.info("发送公众号模板消息返回：{}",responseContent);
        JsonObject jsonObject = GsonParser.parse(responseContent);
        //添加token失效重试逻辑
        if (jsonObject.get("errcode").getAsInt() == 40001 && firstSend) {
            log.info("发送公众号模板消息token失效，进入重试逻辑");
            //删除现有缓存token
            RedisUtil.delete(cacheKey,RedisDBType.PRIMARY);
            //重试一次
            log.info("发送公众号模板消息token失效，递归调用重试一次");
            return this.sendTemplateMsg(templateMessage,false);
        }else if (jsonObject.get("errcode").getAsInt() == 0) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("msgId",jsonObject.get("msgid").getAsString());
            returnJson.put("requestJson",templateMessage.toJson());
            returnJson.put("requestUrl",requestUrl);
            return returnJson;
        } else {
            throw new WxErrorException(WxError.fromJson(responseContent, WxType.MP));
        }
    }

    public boolean sendBandMsg(String openid) {
        String content = "您已成功登录铁塔视联，可点击下方蓝字打开铁塔视联小程序\n\n"
                + "<a data-miniprogram-appid=\""
                + properties.getMiniAppId()
                + "\" data-miniprogram-path=\""
                + properties.getMiniAppEventUrl()
                + "\" href=\"\" data-miniprogram-type=\"text\">点击此处打开铁塔视联小程序</a>";
        WxMpKefuMessage message = WxMpKefuMessage.TEXT().toUser(openid).content(content).build();
        try {
            wxMpService.getKefuService().sendKefuMessage(message);
        }catch (WxErrorException e){
            log.error("发送公众号客服消息失败" ,e);
            return false;
        }
        return true;
    }

    /**
     * 变更接收微信通知状态
     * @param vo
     * @return
     */
    @Override
    public int wxUpdateMsgControl(WxLoginVo vo) {
        return wxMapper.wxUpdateMsgControl(vo);
    }

    /**
     * 获取接收微信通知状态
     * @param vo
     * @return
     */
    @Override
    public void getReceiveMsgState(WxLoginVo vo) {
        String receiveMsgState = wxMapper.getReceiveMsgState(vo);
        vo.setReceiveMsg(receiveMsgState);
    }

    @Override
    public Result<String> colonyBind(WxColonyCodeVo vo) {
        if (null == vo.getEncStr()) {
            throw new CustomException("入参错误", 400);
        }
        String decrypt = AESUtil.decryptBase64(vo.getEncStr());
        log.info("解密后的信息: {}", decrypt);
        String[] split = decrypt.split(COMMA);
        if (4 >= split.length) {
            throw new CustomException("解析错误", 400);
        }
        String time = split[4];
        DateTime parse = DateUtil.parse(time, "yyyyMMddHHmmss");
        DateTime now = DateUtil.dateSecond();
        long between = DateUtil.between(parse, now, DateUnit.SECOND);
        //跨系统 考虑到系统时间误差  设置为3分钟
        if (180 < between) {
            throw new CustomException("加密过期", 400);
        }
        String unionId = split[0];
        String openId = split[1];
        String miniOpenId = split[2];
        String colonyCode = split[3];
        vo.setUnionId(unionId);
        vo.setOpenId(openId);
        vo.setMiniOpenId(miniOpenId);
        vo.setColonyCode(colonyCode);

        //判断原数据是否存在
        int count = wxMapper.selectColonyUser(vo);
        if (0 == count) {
            wxMapper.colonyBind(vo);
        } else {
            wxMapper.update(vo);
        }
        return Result.success("属地微信账号绑定成功");
    }

    @Override
    public String selectColonyCodeByUnionId(String unionId) {
        return wxMapper.selectColonyCodeByUnionId(unionId);
    }

    @Override
    public CascadeConfigSyncVO findCascadeByCode(String platformCode) {
        return wxMapper.findCascadeByCode(platformCode);
    }

    @Override
    public String getOpenIdByUnionId(String unionId) {
        return wxMapper.getOpenIdByUnionId(unionId);
    }
}
