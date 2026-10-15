package com.chinatower.system.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.feign.factory.NoticeRuleClientFallbackFactory;
import com.chinatower.system.api.model.NoticeRuleFilterResultVO;
import com.chinatower.system.api.model.NoticeRuleFilterVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "NoticeRuleClient", value = ServiceNameConstants.SYSTEM_SERVICE,
        fallbackFactory = NoticeRuleClientFallbackFactory.class)
public interface NoticeRuleClient {

    /**
     * 筛选用户通知规则 for微创
     * @apiNote 筛选用户通知规则 for微创
     * @apiVersion 6.95
     * @tag 六、基础功能能力
     * @requestDemo
     * [
     *        {
     * 		"warningSource": "3",
     * 		"isNow": "1",
     * 		"orderId": "2023031712204939324312541",
     * 		"warningTypeId": "1010100",
     * 		"alarmTime": "2023-03-17 02:23:13",
     * 		"userId": " ",
     * 		"industryCode": "000000"
     *    }
     * ]
     * @responseDemo
     * industryCode 行业编码
     * msgType 推送类型 0:消息类 1:通知类
     * userId 用户ID
     * {
     *     "code": 200,
     *     "data": [
     *         {
     *             "delayTime": "",
     *             "industryCode": "000000",
     *             "isDelay": "0",
     *             "msgType": "1",
     *             "sound": "0000",
     *             "userId": "47610796b99b4523981a351f0e401044"
     *         }
     *     ],
     *     "msg": "请求成功",
     *     "time": 1675410749244
     * }
     * @param noticeRuleFilterVOList
     * @return Result<List<NoticeRuleFilterResultVO>>
     */
    @PostMapping(value = "/noticeRule/filterUserList")
    Result<List<NoticeRuleFilterResultVO>> filterUserList(@RequestBody List<NoticeRuleFilterVO> noticeRuleFilterVOList);
}
