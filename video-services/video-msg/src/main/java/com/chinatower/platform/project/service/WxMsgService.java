package com.chinatower.platform.project.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.request.WxPushRequest;

import java.util.List;

/**
 * @Description: 微信公众号推送 service
 * @Author: 朱君
 * @Date: 2022/11/15
 **/
public interface WxMsgService {

	/**
	 * 微信公众号推送告警信息接口
	 * @param wxPushRequest
	 * @return
	 */
	Result sendEventMessage(WxPushRequest wxPushRequest);

	/**
	 * 微信公众号推送告警信息接口
	 * @param wxPushRequest
	 * @return
	 */
	Result sendMultipleWxMessage(List<WxPushRequest> wxPushRequest);
}
