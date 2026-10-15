package com.chinatower.video.api;

import java.util.List;

import com.chinatower.video.api.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.factory.VideoPatrolPlanFallbackFactory;

import io.swagger.annotations.ApiOperation;


@FeignClient(contextId = "videoPatrolPlanService", value = ServiceNameConstants.VIDEO_SERVICE, fallbackFactory = VideoPatrolPlanFallbackFactory.class)
public interface IVideoPatrolPlanService {


    /**
     * 增加巡航路径
     * @apiNote 增加巡航路径
     * @apiVersion 5.20
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122049393",
     * 	"patrolName": "小刀巡航0815",
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"startTimePeriod":"00,01",
     * 	"presets": [
     *                {
     *          "channelCode": "330602330000000191310000007",
     * 			"presetId": 53749,
     * 			"staySeconds": 10,
     * 			"speed": 2,
     * 			"sort": 1
     *        }
     * 	]
     * }
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * {
     * 	"code": 200,
     * 	"msg": "增加巡航路径成功!",
     * 	"time": 1692103003683
     * }
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "增加巡航路径", notes = "增加巡航路径")
    @PostMapping("/video/patrol/sdk/addCruisePatrol")
    Result addCruisePatrol(@RequestBody AddPatrolReq patrolDTO);

    /**
     * 删除巡航路径
     * @apiNote 删除巡航路径
     * @apiVersion 5.21
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122049393",
     * 	"patrolId": 1734721615825,
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007"
     * }
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * {
     * 	"code": 200,
     * 	"msg": "删除巡航路径成功!",
     * 	"time": 1692103826886
     * }
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "删除巡航路径", notes = "删除巡航路径")
    @PostMapping("/video/patrol/sdk/delPatrol")
    Result delPatrol(@RequestBody DelPatrolReq patrolDTO);


    /**
     * 批量删除巡航路径
     * @apiNote 批量删除巡航路径
     * @apiVersion 5.22
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"patrolIds": [
     * 		1694625090098
     * 	]
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "批量删除巡航路径成功!",
     * 	"time": 1692103003683
     * }
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "批量删除巡航路径", notes = "批量删除巡航路径")
    @PostMapping("/video/patrol/sdk/batchDelPatrol")
    Result batchDelPatrol(@RequestBody BatchDelPatrolReq patrolDTO);

    /**
     * 修改巡航路径
     * @apiNote 修改巡航路径
     * @apiVersion 5.23
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122049393",
     * 	"patrolId": 1734772341721,
     * 	"patrolName": "小刀巡航0816111",
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"startTimePeriod":"00,01",
     *    "presets": [
     *     {
     *       "presetId": 8,
     *       "staySeconds": 60,
     *       "speed": 0,
     *       "sort": 1,
     *       "channelCode": "330602330000000191310000007"
     *     }
     *   ]
     * }
     *
     *
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * {
     * 	"code": 200,
     * 	"msg": "修改巡航路径成功!",
     * 	"time": 1692152578706
     * }
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "修改巡航路径", notes = "修改巡航路径")
    @PostMapping("/video/patrol/sdk/updatePatrol")
    Result updatePatrol(@RequestBody ModPatrolReq patrolDTO);


    /**
     * 停用/启用巡航路径
     * @apiNote 停用/启用巡航路径
     * @apiVersion 5.24
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007",
     * 	"tenantId": "122049393",
     * 	"patrolId": 1734772341721,
     * 	"patrolType": 2
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * {
     * 	"code": 200,
     * 	"msg": "启用/停用巡航路径成功!",
     * 	"time": 1694674153790
     * }
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "停用/启用巡航路径", notes = "停用/启用巡航路径")
    @PostMapping("/video/patrol/sdk/doPatrol")
    Result doPatrol(@RequestBody SttPatrolReq patrolDTO);


    /**
     * 查询订单巡航路径
     * @apiNote 查询订单巡航路径
     * @apiVersion 5.25
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"orderCode": "PO12203580720230307824272879616"
     * }
     * @responseDemo
     * orderCode：订单编码
     * tenantId：客户编码
     * patrolId：巡航编码
     * deviceCode：摄像机编码
     * channelCode：通道编码
     * patrolName：巡航名称
     * creator：创建人账号
     * startTimePeriod：执行时段
     * startStop：预约启动状态：1-停用；2-启用；
     * ifCruise：启动状态：1-停用；2-启用；
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694678793293,
     * 	"data": [
     *        {
     * 			"orderCode": "PO12203580720230307824272879616",
     * 			"tenantId": "122035807",
     * 			"patrolId": 1694734130182,
     * 			"deviceCode": "11010800831327000008",
     * 			"channelCode": "11010800831310000008",
     * 			"patrolName": "巡航计划5",
     * 			"creator": "xiaoci",
     * 			"startTimePeriod": "16",
     * 			"startStop": "1",
     * 			"ifCruise": "1"
     *        }
     * 	]
     * }
     * @param presetDTO
     * @return
     */
    @ApiOperation(value = "查询订单巡航路径", notes = "查询订单巡航路径")
    @PostMapping("/video/patrol/sdk/queryOrderPatrolInfo")
    Result<List<PatrolDTO>> queryOrderPatrolInfo(@RequestBody QueryOrderDeviceSetDTO presetDTO);


    /**
     * 查询巡航路径列表
     * @apiNote 查询巡航路径列表
     * @apiVersion 5.26
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * patrolId巡航计划编码
     * isCruise是否正在巡航巡 0-未执行；1-执行中；
     * patrolName巡航名称
     * startStop预约启动状态：1-停用；2-启用
     * startTimePeriodArr巡航计划时间段(list)
     * startTimePeriod巡航计划时间段(String)
     *      00:00-01:00 记为00
     *      01:00-02:00 记为01
     *      02:00-03:00 记为02
     *      ....
     * presets预置位列表
     *  presetId预置位编码
     *  presetName预置位名称
     *  staySeconds停留时长
     *  speed速度
     *  sort顺序
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1694681531422,
     *     "data": [
     *         {
     *             "isCruise": 0,
     *             "presets": [
     *                 {
     *                     "presetId": 55271,
     *                     "presetName": "预置位101245",
     *                     "staySeconds": 60,
     *                     "speed": 1,
     *                     "sort": 1,
     *                     "patrolId": 1680036546212
     *                 }
     *             ],
     *             "patrolName": "测试续航zzzzzzzzzzz",
     *             "patrolId": 1680036546212,
     *             "creator": "tower",
     *             "creatorDate": null,
     *             "startStop": "2",
     *             "startTimePeriodArr": [
     *                 "11",
     *                 "14"
     *             ],
     *             "startTimePeriod": "11,14",
     *             "modifier": null,
     *             "updateTime": null
     *         }
     *     ]
     * }
     * @param queryPatrolReq
     * @return
     */
    @ApiOperation(value = "查询巡航路径列表", notes = "查询巡航路径列表")
    @PostMapping("/video/patrol/sdk/queryPatrol")
    Result<JSONObject> queryPatrol(@RequestBody QueryPatrolReq queryPatrolReq);

    /**
     * 批量停用/启用巡航路径
     * @apiNote 批量停用/启用巡航路径
     * @apiVersion 5.27
     * @tag 五、视频能力
     * @requestDemo
     * [
     *        {
     * 		"deviceCode": "11010800831327000008",
     * 		"channelCode": "11010800831310000008",
     * 		"tenantId": "122035807",
     * 		"patrolId": 1694734130182,
     * 		"patrolType": 1
     *    }
     * ]
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "批量停用/启用巡航计划成功!",
     * 	"time": 1694682022478
     * }
     * @param patrolDTOList
     * @return
     */
    @ApiOperation(value = "批量停用/启用巡航计划", notes = "批量停用/启用巡航计划")
    @PostMapping("/video/patrol/batchStartEndPatrol")
    Result<String> batchStartEndPatrol(@RequestBody List<SttPatrolReq> patrolDTOList);

    /**
     * 暂停恢复巡航路径
     * @apiNote 暂停恢复巡航路径
     * @apiVersion 5.28
     * @tag 五、视频能力
     * @requestDemo
     * {
     *     "deviceCode": "35011200001327000002",
     *     "pauseResumeStatus": "3"
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1736319178310,
     *     "data": 300
     * }
     * @param patrolDTO
     * @return
     */
    @PostMapping("/video/patrol/pauseResumePatrol")
    Result pauseResumePatrol(@Validated @RequestBody SttPatrolPauseReq patrolDTO);
}
