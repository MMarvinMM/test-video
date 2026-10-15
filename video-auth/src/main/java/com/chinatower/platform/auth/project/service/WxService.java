package com.chinatower.platform.auth.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.auth.api.entity.WxColonyCodeVo;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.model.WxLoginVo;
import com.chinatower.platform.auth.project.entity.WxBttomMenuEntity;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import me.chanjar.weixin.common.error.WxErrorException;

import java.util.List;
import java.util.Map;

/**
 * @Description: 微信公众号 service
 * @Author: 朱君
 * @Date: 2022/11/15
 **/
public interface WxService extends IService<WxBttomMenuEntity> {

	/**
	 * 微信公众号创建菜单
	 * @return
	 */
	Result<String> createMenu();

	Map<String, String> loginByWx(String unionId);

	List<Map<String, String>> getBandUserByWx(WxLoginVo vo);

	/**
	 * wx账户释放绑定关系
	 * @param vo
	 * @return
	 */
	int releaseBindUser(WxLoginVo vo);

	/**
	 * wx账户绑定
	 * @param vo
	 * @return
	 */
	int bindUser(WxLoginVo vo);

	/**
	 * 更新wx账户绑定
	 * @param vo
	 * @return
	 */
	int updateBindUser(WxLoginVo vo);

	/**
	 * 获取是否已经存在微信用户信息
	 * @param vo
	 * @return
	 */
	int getWxUserCount(WxLoginVo vo);

	/**
	 * 新增微信用户信息
	 * @param vo
	 * @return
	 */
	int insertWxUser(WxLoginVo vo);

	/**
	 * 更新微信用户信息
	 * @param vo
	 * @return
	 */
	int updateWxUser(WxLoginVo vo);

	/**
	 * 取消关注
	 * @param openId
	 * @return
	 */
	int updateUnSubscribe(String openId);

	/**
	 * 关注
	 * @param openId
	 * @return
	 */
	int updateSubscribe(String openId);

	/**
	 * 获取行业编码
	 * @param vo
	 * @return
	 */
	Map<String, String> getIndustryCodeByWx(WxLoginVo vo);

	/**
	 * 更新登录的行业编码
	 * @param vo
	 * @return
	 */
    int updateIndustryCode(WxLoginVo vo);

	/**
	 * 将用户失效，并发送重新登录的微信提示
	 * @param userId
	 * @return
	 */
	 boolean invalidUser(String userId,String userName) throws WxErrorException;

	/**
	 * 发送客服消息
	 * @param openid
	 */
	boolean sendBandMsg(String openid);

	/**
	 * 微信公众号接收消息控制开关
	 * @param vo
	 */
	int wxUpdateMsgControl(WxLoginVo vo);

	/**
	 * 获取接收微信通知状态
	 * @param vo
	 */
	void getReceiveMsgState(WxLoginVo vo);

	Result<String> colonyBind(WxColonyCodeVo vo);

	String selectColonyCodeByUnionId(String unionId);

	CascadeConfigSyncVO findCascadeByCode(String platformCode);

    String getOpenIdByUnionId(String unionId);
}
