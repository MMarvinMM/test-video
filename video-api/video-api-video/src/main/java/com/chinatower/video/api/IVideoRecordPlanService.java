package com.chinatower.video.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.BatchDeleteChannelVideoPlanVO;
import com.chinatower.video.api.entity.BatchStartOrEndRecordPlanVO;
import com.chinatower.video.api.entity.ChannelVideoPlanRequestVO;
import com.chinatower.video.api.entity.ChannelVideoPlanVO;
import com.chinatower.video.api.entity.DeleteChannelVideoPlanVO;
import com.chinatower.video.api.entity.QueryOrderDeviceSetDTO;
import com.chinatower.video.api.factory.VideoRecordPlanFallbackFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(tags = "视频通道录像计划模块")
@FeignClient(contextId = "videoRecordPlanService", value = ServiceNameConstants.VIDEO_SERVICE, fallbackFactory = VideoRecordPlanFallbackFactory.class)
public interface IVideoRecordPlanService {


    /**
     * 新增通道录像计划
     * @apiNote 新增通道录像计划
     * @apiVersion 5.30
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"recordPlanList": [{
     * 		"deviceCode": "12345678909879870987-00000000832127000018",
     * 		"channelCode": "67890987654567656787@00000000832127000018",
     * 		"customerCode": "122035807",
     * 		"tenantId": "122035807",
     * 		"streamType": 1,
     * 		"storageType": 1,
     * 		"storageDay": 7,
     * 		"isAudio": 0,
     * 		"isAllDay": 0,
     * 		"recordPlanName": "录像计划_20230915095712816",
     * 		"recordPlanStatus": 1,
     * 		"startDate": "2023-08-01",
     * 		"endDate": "2023-08-11",
     * 		"timeSections": [{
     * 			"dayOfWeek": 1,
     * 			"startTime": "000000",
     * 			"endTime": "170000"
     *                }, {
     * 			"dayOfWeek": 2,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        }, {
     * 			"dayOfWeek": 3,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        }, {
     * 			"dayOfWeek": 4,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        }, {
     * 			"dayOfWeek": 5,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        }, {
     * 			"dayOfWeek": 6,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        }, {
     * 			"dayOfWeek": 7,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        }]    * 	}]
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "新增通道录像计划成功!",
     * 	"time": 1694743052080
     * }
     * @param channelVideoPlanRequestVO
     * @return
     */
    @ApiOperation(value = "新增通道录像计划")
    @PostMapping("/video/sdk/addRecordPlan")
    Result addRecordPlan(@Validated @RequestBody ChannelVideoPlanRequestVO channelVideoPlanRequestVO);


    /**
     * 修改通道录像计划
     * @apiNote 修改通道录像计划
     * @apiVersion 5.31
     * @tag 五、视频能力
     * @requestDemo
     * {
     *     "recordPlanId":"1690039520213",
     * 	"deviceCode": "51150200831327000006",
     * 	"channelCode": "51150200001310000002",
     * 	"customerCode": "122035807",
     * 	"tenantId": "122035807",
     * 	"streamType": 0,
     * 	"storageType": 1,
     * 	"storageDay": 7,
     * 	"isAudio": 0,
     * 	"isAllDay": 1,
     * 	"recordPlanName": "录像计划_20230914175531712",
     * 	"recordPlanStatus": 1,
     * 	"startDate": "2023-07-22",
     * 	"endDate": "2023-07-31",
     * 	"timeSections": [
     *                {
     * 			"dayOfWeek": 1,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        },
     *        {
     * 			"dayOfWeek": 2,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        },
     *        {
     * 			"dayOfWeek": 3,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        },
     *        {
     * 			"dayOfWeek": 4,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        },
     *        {
     * 			"dayOfWeek": 5,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        },
     *        {
     * 			"dayOfWeek": 6,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        },
     *        {
     * 			"dayOfWeek": 7,
     * 			"startTime": "000000",
     * 			"endTime": "235959"
     *        }
     * 	]
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "新修改通道录像计划成功!",
     * 	"time": 1694685087035
     * }
     * @param channelVideoPlanVO
     * @return
     */
    @ApiOperation(value = "修改通道录像计划")
    @PostMapping("/video/sdk/updateRecordPlan")
    Result updateRecordPlan(@Validated @RequestBody ChannelVideoPlanVO channelVideoPlanVO);


    /**
     * 删除通道录像计划
     * @apiNote 删除通道录像计划
     * @apiVersion 5.32
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "51150200831327000006",
     * 	"channelCode": "51150200001310000002",
     * 	"recordPlanId": "1690039520213",
     * 	"tenantId": "122035807"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "删除通道录像计划成功!",
     * 	"time": 1694743120247
     * }
     * @param deleteChannelVideoPlanVO
     * @return
     */
    @ApiOperation(value = "删除通道录像计划")
    @PostMapping("/video/sdk/deleteRecordPlan")
    Result deleteRecordPlan(@Validated @RequestBody DeleteChannelVideoPlanVO deleteChannelVideoPlanVO);


    /**
     * 批量删除通道录像计划
     * @apiNote 批量删除通道录像计划
     * @apiVersion 5.33
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"recordPlanIdList": [
     * 		"1690622095734"
     * 	]
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "批量删除通道录像计划成功!",
     * 	"time": 1694744922258
     * }
     * @param batchDeleteChannelVideoPlanVO
     * @return
     */
    @ApiOperation(value = "批量删除通道录像计划")
    @PostMapping("/video/sdk/batchDeleteRecordPlan")
    Result batchDeleteRecordPlan(@Validated @RequestBody BatchDeleteChannelVideoPlanVO batchDeleteChannelVideoPlanVO);


    /**
     * 批量停用/启用通道录像计划
     * @apiNote 批量停用/启用通道录像计划
     * @apiVersion 5.34
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "46900200831327000053",
     * 	"channelCode": "46900200001310000007",
     * 	"tenantId": "122035807",
     * 	"recordPlanIdList": [
     * 		"1691489847574"
     * 	],
     * 	"startEndStatus": "1"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "批量启用通道录像计划成功!",
     * 	"time": 1694745463100
     * }
     * @param batchStartOrEndRecordPlanVO
     * @return
     */
    @ApiOperation(value = "批量停用/启用通道录像计划")
    @PostMapping("/video/sdk/batchStartOrEndRecordPlan")
    Result batchStartOrEndRecordPlan(@Validated @RequestBody BatchStartOrEndRecordPlanVO batchStartOrEndRecordPlanVO);


    /**
     * 查询订单通道录像计划
     * @apiNote 查询订单通道录像计划
     * @apiVersion 5.35
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"deviceCodeList": [
     * 		"46900200831327000053"
     * 	],
     * 	"orderCode": "PO12203580720230509838550081536"
     * }
     * @responseDemo
     * id主键
     * isAllDay是否整天
     * timeSections时间段
     *    recordPlanId录像计划id
     *    recordPlanIds录像计划ids
     *    dayOfWeek星期几
     *    startTime开始时间
     *    endTime结束时间
     *    deleteFlag删除标记
     * streamType流类型   1-主码流；0-子码流；
     * storageType储存类型  1-中心存储；0-前端存储；
     * storageDay储存日
     * isAudio是否为音频
     * recordPlanId录像计划id
     * recordPlanName录像计划名称
     * recordPlanStatus录像计划状态
     * startDate开始时间
     * endDate结束时间
     * deviceCode设备code
     * channelCode通道code
     * channelName通道名称
     * tenantId租户id
     * orderCode订单code
     *
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694745770671,
     * 	"data": [
     *                {
     * 			"id": 11558,
     * 			"isAllDay": 1,
     * 			"timeSections": [
     *                {
     * 					"recordPlanId": 1691489847574,
     * 					"recordPlanIds": null,
     * 					"dayOfWeek": 1,
     * 					"startTime": "000000",
     * 					"endTime": "240000",
     * 					"deleteFlag": "0"
     *                },
     *                {
     * 					"recordPlanId": 1691489847574,
     * 					"recordPlanIds": null,
     * 					"dayOfWeek": 2,
     * 					"startTime": "000000",
     * 					"endTime": "240000",
     * 					"deleteFlag": "0"
     *                },
     *                {
     * 					"recordPlanId": 1691489847574,
     * 					"recordPlanIds": null,
     * 					"dayOfWeek": 3,
     * 					"startTime": "000000",
     * 					"endTime": "240000",
     * 					"deleteFlag": "0"
     *                },
     *                {
     * 					"recordPlanId": 1691489847574,
     * 					"recordPlanIds": null,
     * 					"dayOfWeek": 4,
     * 					"startTime": "000000",
     * 					"endTime": "240000",
     * 					"deleteFlag": "0"
     *                },
     *                {
     * 					"recordPlanId": 1691489847574,
     * 					"recordPlanIds": null,
     * 					"dayOfWeek": 5,
     * 					"startTime": "000000",
     * 					"endTime": "240000",
     * 					"deleteFlag": "0"
     *                },
     *                {
     * 					"recordPlanId": 1691489847574,
     * 					"recordPlanIds": null,
     * 					"dayOfWeek": 6,
     * 					"startTime": "000000",
     * 					"endTime": "240000",
     * 					"deleteFlag": "0"
     *                },
     *                {
     * 					"recordPlanId": 1691489847574,
     * 					"recordPlanIds": null,
     * 					"dayOfWeek": 7,
     * 					"startTime": "000000",
     * 					"endTime": "240000",
     * 					"deleteFlag": "0"
     *                }
     * 			],
     * 			"streamType": 1,
     * 			"storageType": 1,
     * 			"storageDay": 5,
     * 			"isAudio": 0,
     * 			"recordPlanId": 1691489847574,
     * 			"recordPlanName": "录像计划_20230915103613500",
     * 			"recordPlanStatus": 1,
     * 			"startDate": "2023-08-08",
     * 			"endDate": "2023-08-23",
     * 			"deviceCode": "46900200831327000053",
     * 			"channelCode": "46900200001310000007",
     * 			"channelName": null,
     * 			"tenantId": "122035807",
     * 			"orderCode": "PO12203580720230509838550081536",
     *        }
     * 	]
     * }
     * @param queryOrderDeviceSetDTO
     * @return
     */
    @PostMapping("/video/sdk/queryOrderRecordPlanInfo")
    Result<List<ChannelVideoPlanVO>> queryOrderRecordPlanInfo(@RequestBody QueryOrderDeviceSetDTO queryOrderDeviceSetDTO);


    /**
     * 批量停用/启用通道录像计划
     * @apiNote 批量停用/启用通道录像计划
     * @apiVersion 5.36
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"recordPlanList": [
     *                {
     * 			"deviceCode": "46900200831327000053",
     * 			"channelCode": "46900200001310000007",
     * 			"tenantId": "122035807",
     * 			"streamType": 1,
     * 			"storageType": 1,
     * 			"storageDay": 5,
     * 			"isAudio": 0,
     * 			"isAllDay": 1,
     * 			"recordPlanId": "1691489847574",
     * 			"recordPlanName": "录像计划_20230915105109641",
     * 			"recordPlanStatus": 1,
     * 			"startDate": "2023-08-08",
     * 			"endDate": "2023-08-23"
     *        }
     * 	],
     * 	"industryCode": "000000"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * {
     * 	"code": 200,
     * 	"msg": "批量停用/启用通道录像计划成功!",
     * 	"time": 1694746823867
     * }
     * @param channelVideoPlanRequestVO
     * @return
     */
    @ApiOperation(value = "批量停用/启用通道录像计划")
    @PostMapping("/video/batchStartEndRecordPlan")
    Result<String> batchStartEndRecordPlan(@Validated @RequestBody ChannelVideoPlanRequestVO channelVideoPlanRequestVO);
}
