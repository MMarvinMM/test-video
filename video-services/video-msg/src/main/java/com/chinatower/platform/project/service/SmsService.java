package com.chinatower.platform.project.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.entity.request.MsgRequestVO;
import com.chinatower.platform.project.vo.SmsRecordQuery;

import java.util.List;

/**
 * @Description: 短信service
 * @Author: 张帅
 * @Date: 2021/11/12 16:05
 **/
public interface SmsService {


	/**
	 * 公网发送短信验证码
	 * @param phone
	 * @return
	 */
	Result sendShortMessage(String phone, String header, String aesPhone,String templateKey);

	/**
	 * 内网发送短信接口
	 * @param commonSmsRequest
	 * @return
	 */
	Result inSideSendMessage(CommonSmsRequest commonSmsRequest);

	/**
	 * 内创新平台发送短信接口
	 * @param request
	 * @return
	 */
	Result innovationSendMessage(MsgRequestVO request);

	Result batchSendMessage(List<CommonSmsRequest> list);



	void updateSmsRecordStatByMsgId(BsSmsRecord record);

	List<BsSmsRecord> list(SmsRecordQuery query);

	/**
	 * 校验短信验证码
	 * @param phone 手机
	 * @param code 验证码
	 * @param templateKey 短信模板key(内网发送为空)
	 * @param canRepeatCheck 是否可以重复校验
	 * @return 校验结果
	 */
	Result checkPhoneCode(String phone, String code, String templateKey, boolean canRepeatCheck);

}
