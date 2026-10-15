package com.chinatower.platform.project.mapper;

import com.chinatower.platform.entity.request.WxPushRequest;

import java.util.List;
import java.util.Map;

/**
 * @title
 * @description 公众号消息通知业务持久层
 * @author zhujun
 * @updateTime 2022/11/15
 * @throws
 */
public interface WxMsgMapper {

    List<Map<String, String>> getUserInfo(WxPushRequest wxPushRequest);

}
