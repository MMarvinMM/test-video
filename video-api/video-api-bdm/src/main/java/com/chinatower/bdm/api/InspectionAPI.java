package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.api.factory.InspectionFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "InspectionAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = InspectionFallbackFactory.class)
public interface InspectionAPI {

    /**
     * 行业大屏获取巡查任务列表
     * @apiNote 行业大屏获取巡查任务列表
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		isGridManagerList：网格长列表
     * 		noGridManagerList：网格员列表
     * 		toDoFlag：待办标识 1代表是待办任务
     * 	    feedbackKeeperId: 反馈网格员id(已办巡查任务才会返回该字段)
     * 	    feedbackEnd: 反馈端（1.PC 2.APP 已办巡查任务才会返回该字段）
     * @apiVersion 13.18
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"createStartTime": "2024-06-05 00:00:00",
     * 	"createEndTime": "2024-06-05 23:59:59",
     * 	"taskName":"小刀测试",
     * 	"taskStatus":"2",
     * 	"gridId":"202302091714031050"
     * }
     * @responseDemo
     * code: 请求的状态码，表示结果的状态（整数，例如 200）
     * msg: 返回的消息，表示请求的结果描述（字符串，例如“请求成功”）
     * time: 时间戳，表示请求完成的时间（整数，例如 1718264163209）
     * data: 任务数据列表（数组，可能包含多个任务对象）
     *      taskId: 任务 ID（字符串，例如“011b3a288246456d99f686ab967252b0”）
     *      taskName: 任务名称（字符串，例如“正常巡查轨迹且无巡查时段”）
     *      taskDesc: 任务描述（字符串或 null，表示任务的详细信息）
     *      gridName: 网格名称（字符串，例如“五路居慧科大厦”）
     *      keeperName: 巡查员名称（字符串，例如“测试林业”）
     *      startTime: 任务开始时间（字符串或 null，例如 null）
     *      endTime: 任务结束时间（字符串或 null，例如 null）
     *      createDate: 任务创建日期（字符串，例如“2024-06-13”）
     *      inspectionPeriod: 巡查时段（字符串或 null）
     *      createTime: 任务创建时间（字符串，例如“2024-06-13 11:01:00”）
     *      retroaction: 反馈信息（字符串或 null）
     *      inspectionStartTime: 巡查开始时间（字符串或 null）
     *      inspectionEndTime: 巡查结束时间（字符串或 null）
     *      feedbackKeeperId: 巡查员 ID（字符串，例如“202406050931488654”）
     *      feedbackKeeperName: 巡查员反馈名称（字符串或 null）
     *      inspectionTime: 巡查时间（字符串或 null）
     *      feedbackEnd: 反馈状态（字符串，例如“1”）
     *      feedbackEndName: 反馈状态名称（字符串或 null）
     *      taskStatus: 任务状态（字符串，例如“1”）
     *      keeperId: 巡查员 ID（字符串，例如“202406050931488654”）
     *      isGridManagerList: 属于网格管理员的列表（数组，例如 ["测试林业"]）
     *      noGridManagerList: 不属于网格管理员的列表（数组，可能为空或包含字符串）
     *      toDoFlag: 待办标识（字符串或 null）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1718264163209,
     * 	"data": [
     *                {
     * 			"taskId": "011b3a288246456d99f686ab967252b0",
     * 			"taskName": "正常巡查轨迹且无巡查时段",
     * 			"taskDesc": null,
     * 			"gridName": "五路居慧科大厦",
     * 			"keeperName": "测试林业",
     * 			"startTime": null,
     * 			"endTime": null,
     * 			"createDate": "2024-06-13",
     * 			"inspectionPeriod": null,
     * 			"createTime": "2024-06-13 11:01:00",
     * 			"retroaction": null,
     * 			"inspectionStartTime": null,
     * 			"inspectionEndTime": null,
     * 			"feedbackKeeperId": "202406050931488654",
     * 			"feedbackKeeperName": null,
     * 			"inspectionTime": null,
     * 			"feedbackEnd": "1",
     * 			"feedbackEndName": null,
     * 			"taskStatus": "1",
     * 			"keeperId": "202406050931488654",
     * 			"isGridManagerList": [
     * 				"测试林业"
     * 			],
     * 			"noGridManagerList": [],
     * 			"toDoFlag": null
     *        }
     * 	]
     * }
     * @param inspectionTaskNoPageParam
     * @return
     */
    @PostMapping("/inspectionTask/getTaskListByCondition")
    Result<List<InspectionTaskListVO>> getTaskListByCondition(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam);


    /**
     * 我的待办
     * @apiNote 我的待办
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		isGridManagerList：网格长列表
     * 		noGridManagerList：网格员列表
     * @apiVersion 13.19
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"createStartTime": "2024-06-05 00:00:00",
     * 	"createEndTime": "2024-06-05 23:59:59",
     * 	"taskName":"小刀测试",
     * 	"taskStatus":"2",
     * 	"gridId":"202302091714031050"
     * }
     * @responseDemo
     * code: 请求的状态码，表示结果的状态（整数，例如 200）
     * msg: 返回的消息，表示请求的结果描述（字符串，例如“请求成功”）
     * time: 时间戳，表示请求完成的时间（整数，例如 1718264163209）
     * data: 任务数据列表（数组，可能包含多个任务对象）
     *      taskId: 任务 ID（字符串，例如“011b3a288246456d99f686ab967252b0”）
     *      taskName: 任务名称（字符串，例如“正常巡查轨迹且无巡查时段”）
     *      taskDesc: 任务描述（字符串或 null，表示任务的详细信息）
     *      gridName: 网格名称（字符串，例如“五路居慧科大厦”）
     *      keeperName: 巡查员名称（字符串，例如“测试林业”）
     *      startTime: 任务开始时间（字符串或 null，例如 null）
     *      endTime: 任务结束时间（字符串或 null，例如 null）
     *      createDate: 任务创建日期（字符串，例如“2024-06-13”）
     *      inspectionPeriod: 巡查时段（字符串或 null）
     *      createTime: 任务创建时间（字符串，例如“2024-06-13 11:01:00”）
     *      retroaction: 反馈信息（字符串或 null）
     *      inspectionStartTime: 巡查开始时间（字符串或 null）
     *      inspectionEndTime: 巡查结束时间（字符串或 null）
     *      feedbackKeeperId: 巡查员 ID（字符串，例如“202406050931488654”）
     *      feedbackKeeperName: 巡查员反馈名称（字符串或 null）
     *      inspectionTime: 巡查时间（字符串或 null）
     *      feedbackEnd: 反馈状态（字符串，例如“1”）
     *      feedbackEndName: 反馈状态名称（字符串或 null）
     *      taskStatus: 任务状态（字符串，例如“1”）
     *      keeperId: 巡查员 ID（字符串，例如“202406050931488654”）
     *      isGridManagerList: 属于网格管理员的列表（数组，例如 ["测试林业"]）
     *      noGridManagerList: 不属于网格管理员的列表（数组，可能为空或包含字符串）
     *      toDoFlag: 待办标识（字符串或 null）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717586523192,
     * 	"data": [
     *                {
     * 			"taskId": "ecffe64af18d4fdc8df4b86154c2ad28",
     * 			"taskName": "小刀测试0605002",
     * 			"taskDesc": "111",
     * 			"gridName": "测试新增客0323-林业版本-1.0",
     * 			"keeperName": "junnan五五,测试新增客0323lxt111",
     * 			"startTime": "2024-06-05 00:00:00",
     * 			"endTime": "2024-06-05 00:00:00",
     * 			"createDate": "2024-06-05",
     * 			"inspectionPeriod": null,
     * 			"createTime": "2024-06-05 19:13:00",
     * 			"retroaction": null,
     * 			"inspectionStartTime": "19:14:18",
     * 			"inspectionEndTime": "20:12:18",
     * 			"feedbackKeeperId": null,
     * 			"feedbackKeeperName": null,
     * 			"inspectionTime": null,
     * 			"feedbackEnd": null,
     * 			"feedbackEndName": null,
     * 			"taskStatus": "2",
     * 			"keeperId": "202302161117035649,202302161116183464",
     * 			"isGridManagerList": [
     * 				"junnan五五"
     * 			],
     * 			"noGridManagerList": [
     * 				"测试新增客0323lxt111"
     * 			],
     * 			"toDoFlag": null
     *        }
     * 	]
     * }
     * @param inspectionTaskNoPageParam
     * @return
     */
    @PostMapping("/inspectionTask/getToDoTaskList")
    Result<List<InspectionTaskListVO>> getToDoTaskList(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam);


    /**
     * 巡查任务详情
     * @apiNote 巡查任务详情（Get方法 传taskId 任务id参数）
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 	curGridName：当前网格名称（不返回父级网格）
     * 	feedbackKeeperType：反馈人员类型 1.网格长 否则网格员
     * 	isGridManagerList：巡查人网格长列表
     * 	noGridManagerList：巡查人网格员列表
     * @apiVersion 13.20
     * @tag 十三、网格能力
     * @requestDemo
     * {}
     * @responseDemo
     * code: 请求的状态码，表示结果的状态（整数，例如 200）
     * msg: 返回的消息，表示请求的结果描述（字符串，例如“请求成功”）
     * time: 时间戳，表示请求完成的时间（整数，例如 1718264163209）
     * data: 任务数据列表（数组，可能包含多个任务对象）
     *       taskId: 任务 ID（字符串，例如“011b3a288246456d99f686ab967252b0”）
     *       taskName: 任务名称（字符串，例如“正常巡查轨迹且无巡查时段”）
     *       taskDesc: 任务描述（字符串或 null，表示任务的详细信息）
     *       gridName: 网格名称（字符串，例如“测试新增客0323-林业版本-1.0>北京>海淀区>五路居慧科大厦”）
     *       keeperName: 巡查员名称（字符串，例如“测试林业”）
     *       startTime: 任务开始时间（字符串或 null，例如 null）
     *       endTime: 任务结束时间（字符串或 null，例如 null）
     *       createDate: 任务创建日期（字符串，例如“2024-06-12”）
     *       inspectionPeriod: 巡查时段（字符串或 null）
     *       createTime: 任务创建时间（字符串，例如“2024-06-12 16:15:00”）
     *       retroaction: 反馈信息（字符串或 null）
     *       inspectionStartTime: 巡查开始时间（字符串或 null）
     *       inspectionEndTime: 巡查结束时间（字符串或 null）
     *       feedbackKeeperId: 巡查员 ID（字符串，例如“202406050931488654”）
     *       feedbackKeeperName: 巡查员反馈名称（字符串或 null）
     *       inspectionTime: 巡查时间（字符串或 null）
     *       feedbackEnd: 反馈状态（字符串，例如“1”）
     *       feedbackEndName: 反馈状态名称（字符串或 null）
     *       taskStatus: 任务状态（字符串，例如“1”）
     *       keeperId: 巡查员 ID（字符串，例如“202406050931488654”）
     *       isGridManagerList: 属于网格管理员的列表（数组，例如 ["测试林业"]）
     *       noGridManagerList: 不属于网格管理员的列表（数组，可能为空或包含字符串）
     *       toDoFlag: 待办标识（字符串或 null）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1718355114493,
     * 	"data": {
     * 		"taskId": "0c66009db94549aa882ad4044aafecae",
     * 		"confId": "26f377da7996491b8a42d94509319bc8",
     * 		"cnofName": null,
     * 		"tenantId": "122035807",
     * 		"gridId": "202406041242092356",
     * 		"gridName": "测试新增客0323-林业版本-1.0>北京>海淀区>五路居慧科大厦",
     * 		"curGridName": "五路居慧科大厦",
     * 		"taskStatus": "1",
     * 		"createTime": "2024-06-12 16:15:00",
     * 		"startTime": "2024-06-12 00:00:00",
     * 		"endTime": "2024-06-12 00:00:00",
     * 		"keeperId": "202406041643508862,202406050931488654,202406050941115705,202406050941336400",
     * 		"keeperName": "测试,测试林业,林业网格员,林业网格员2",
     * 		"retroaction": "超长超长巡查计划名称超长超长巡查计划名称超长超长巡查计划名称超长超长巡查计划名称超长超长巡查计划名超",
     * 		"fjId": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/bb08f84d9ddf54459ea7b705612bafa1/f5d48f4c30ddfe957f0183a291e65aed/226583026c784652be606ee91e9beeed.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1718355706&Signature=o4IubYUw6WWsxNHrVdxhLxYKaZI%3D,https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/bb08f84d9ddf54459ea7b705612bafa1/f5d48f4c30ddfe957f0183a291e65aed/b34312eb19ba4326b044e5c88d0fb713.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1718355706&Signature=nIlgNQAWpwlS7hpymfV3yt6ORo4%3D",
     * 		"videoUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/bb08f84d9ddf54459ea7b705612bafa1/f5d48f4c30ddfe957f0183a291e65aed/f71e839e656c4de8b4f54129de3ddd19.mp4?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1718355710&Signature=IxMa3ZVkp%2BtSBQugn%2BZjYaZZ4BU%3D",
     * 		"coverUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/bb08f84d9ddf54459ea7b705612bafa1/f5d48f4c30ddfe957f0183a291e65aed/74b5d71ed506486abdb9d2a714755698.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1718355714&Signature=RltNYAi/9i6uRwOjI7zobcwL3JU%3D",
     * 		"voiceUrl": null,
     * 		"inspectionTime": "2024-06-13 17:24:36",
     * 		"promoter": null,
     * 		"industryCode": null,
     * 		"confDesc": null,
     * 		"taskDesc": "收到VS的VS不是",
     * 		"taskStatusName": null,
     * 		"taskName": "跨天反馈巡查计划",
     * 		"createDate": "2024-06-12",
     * 		"inspectionStartTime": "16:15:28",
     * 		"inspectionEndTime": "19:13:28",
     * 		"feedbackKeeperId": "202406050931488654",
     * 		"feedbackKeeperName": "测试林业",
     * 		"feedbackEnd": "1",
     * 		"feedbackEndName": null,
     * 		"gridRange": "[[[116.269937,39.932423],[116.269891,39.932346],[116.269944,39.931861],[116.270967,39.93191],[116.271049,39.93197],[116.271067,39.932072],[116.271017,39.932379],[116.270935,39.932456],[116.269937,39.932423]]]",
     * 		"gridCenter": "[[116.2706009510597,39.9321769238956]]",
     * 		"trailStartTime": "2024-06-12 16:15:28",
     * 		"appUrl": null,
     * 		"trailEndTime": "2024-06-12 23:59:59",
     * 		"sendFlag": null,
     * 		"keeperIdList": null,
     * 		"keeperPhoneNumber": null,
     * 		"isLine": null,
     * 		"avatar": null,
     * 		"color": "#00b0ff",
     * 		"borderColor": "#00b0ff",
     * 		"borderColorTransparency": "0.5",
     * 		"borderColorLineThickness": "1",
     * 		"colorTransparency": "0.5",
     * 		"isGridManagerList": [
     * 			"测试林业"
     * 		],
     * 		"noGridManagerList": [
     * 			"测试",
     * 			"林业网格员",
     * 			"林业网格员2"
     * 		],
     * 		"feedbackKeeperType": "1",
     * 		"userId": null
     *        }
     * }
     * @param taskId
     * @return
     */
    @GetMapping("/inspectionTask/getTaskInfo")
    Result<InspectionTaskVO> getTaskInfo(@RequestParam String taskId);


    /**
     * 处理巡查任务
     * @apiNote 处理巡查任务
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.21
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"taskId": "ecffe64af18d4fdc8df4b86154c2ad28",
     * 	"feedbackEnd": 1,
     * 	"taskStatus": "1",
     * 	"retroaction": "巡查任务未发现异常情况1111。",
     * 	"fjId": [
     *                {
     * 			"tagPictureUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/bb08f84d9ddf54459ea7b705612bafa1/8192390e2d5a9263926b73e55b982af7/426296cf44b84ff3ae928270b86488e8.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1717648833&Signature=H/p/qC0%2BTe6%2Bcjb37stE2DavE70%3D"
     *        },
     *        {
     * 			"tagPictureUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/bb08f84d9ddf54459ea7b705612bafa1/8192390e2d5a9263926b73e55b982af7/4dea6590d04d451b86a2cd2272f3fbe2.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1717648836&Signature=wj4HREdCD9hedsckIH%2Bghi2OVNs%3D"
     *        }
     * 	],
     * 	"videoUrlList": []
     * }
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     *
     * {
     * 	"code": 200,
     * 	"msg": "操作成功。",
     * 	"time": 1717648477230
     * }
     * @param inspectionTaskParamVO
     * @return
     */
    @PostMapping("/inspectionTask/claimTask")
    Result claimTask(@RequestBody InspectionTaskParamVO inspectionTaskParamVO);


    /**
     * 查询巡查轨迹
     * @apiNote 查询巡查轨迹
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.22
     * @tag 十三、网格能力
     * @requestDemo
     * taskId 任务id
     * keeperIdList 网格员id列表
     * flag 页面标识 1为待办 2为已办  1需要前台传轨迹开始时间和轨迹结束时间两个参数 2不需要前台传参
     * trailStartTime 轨迹开始时间
     * trailEndTime 轨迹结束时间
     * {
     *     "keeperIdList":["202302161117035649","202302161116183464"],
     *     "taskId":"c63ef5c210c645d68914880ac77db209",
     *     "flag":"2"
     * }
     *
     * {
     *     "keeperIdList":["202302161117035649","202302161116183464"],
     *     "taskId":"2dcec9b6a1794023884cd9ed65261395",
     *     "flag":"1",
     *     "trailStartTime":"2024-06-05 13:56:21",
     *     "trailEndTime":"2024-06-05 14:05:23"
     * }
     * @responseDemo
     *   code: 请求的状态码，表示结果的状态（整数，例如 200）
     *   msg: 返回的消息，表示请求的结果描述（字符串，例如“请求成功”）
     *   time: 时间戳，表示请求完成的时间（整数，例如 1717739316751）
     *   data: 数据列表（数组，包含多个子数组，每个子数组内包含若干记录）
     *      collectionName: 数据集合名称（字符串或 null）
     *      keeperId: 巡查员 ID（字符串，例如“202302161116183464”）
     *      longitude: 经度（字符串，例如“123.48059478909205”）
     *      latitude: 纬度（字符串，例如“41.805672886031026”）
     *      createTime: 数据创建时间（字符串，例如“2024-06-05 13:59:24”）
     *      tenantId: 租户 ID（字符串，例如“122035807”）
     *      industryCode: 行业代码（字符串，例如“100000”）
     *      appVerCode: 应用版本号（字符串，例如“1.0”）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717739316751,
     * 	"data": [
     * 		[
     *                        {
     * 				"collectionName": null,
     * 				"keeperId": "202302161116183464",
     * 				"longitude": "123.48059478909205",
     * 				"latitude": "41.805672886031026",
     * 				"createTime": "2024-06-05 13:59:24",
     * 				"tenantId": "122035807",
     * 				"industryCode": "100000",
     * 				"appVerCode": "1.0"
     *            }
     * 		],
     * 		[
     *            {
     * 				"collectionName": null,
     * 				"keeperId": "202302161117035649",
     * 				"longitude": "123.48067807775891",
     * 				"latitude": "41.8057481265243",
     * 				"createTime": "2024-06-05 13:57:21",
     * 				"tenantId": "122035807",
     * 				"industryCode": "100000",
     * 				"appVerCode": "1.0"
     *            },
     *            {
     * 				"collectionName": null,
     * 				"keeperId": "202302161117035649",
     * 				"longitude": "123.48069312890148",
     * 				"latitude": "41.80576916982326",
     * 				"createTime": "2024-06-05 13:58:23",
     * 				"tenantId": "122035807",
     * 				"industryCode": "100000",
     * 				"appVerCode": "1.0"
     *            }
     * 		]
     * 	]
     * }
     * @param inspectionTaskNoPageParam
     * @return
     */
    @PostMapping("/inspectionTask/getTaskTrail")
    Result<List<List<GridKeeperTrailNew>>> getTaskTrail(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam);




    /**
     * 根据网格id查询巡查任务详情(取巡查日期最新的一条)
     * @apiNote 根据网格id查询巡查任务详情(取巡查日期最新的一条)（post方法 传网格id 网格id参数）
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 	curGridName：当前网格名称（不返回父级网格）
     *  feedbackKeeperType：反馈人员类型 1.网格长 否则网格员
     * 	isGridManagerList：巡查人网格长列表
     * 	noGridManagerList：巡查人网格员列表
     * @apiVersion 13.23
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "gridId":"202406041242092356"
     * }
     * @responseDemo
     * code: 请求的状态码，表示结果的状态（整数，例如 200）
     * msg: 返回的消息，表示请求的结果描述（字符串，例如“请求成功”）
     * time: 时间戳，表示请求完成的时间（整数，例如 1718355368202）
     * data: 任务数据对象（包含任务详情）
     *      taskId: 任务 ID（字符串，例如“e05f9ae10fd1489e8a06a698beefa086”）
     *      confId: 配置 ID（字符串，例如“820a47678f46426797ef46efe26138a8”）
     *      cnofName: 配置名称（字符串或 null）
     *      tenantId: 租户 ID（字符串，例如“122035807”）
     *      gridId: 网格 ID（字符串，例如“202406041242092356”）
     *      gridName: 网格名称（字符串，例如“测试新增客0323-林业版本-1.0>北京>海淀区>五路居慧科大厦”）
     *      curGridName: 当前网格名称（字符串，例如“五路居慧科大厦”）
     *      taskStatus: 任务状态（字符串，例如“2”）
     *      createTime: 创建时间（字符串，例如“2024-06-14 10:23:00”）
     *      startTime: 任务开始时间（字符串，例如“2024-06-13 00:00:00”）
     *      endTime: 任务结束时间（字符串，例如“2024-06-20 00:00:00”）
     *      keeperId: 巡查员 ID 列表（字符串，例如“202406041643508862,202406050931488654”）
     *      keeperName: 巡查员名称列表（字符串，例如“测试,测试林业”）
     *      retroaction: 反馈信息（字符串或 null）
     *      fjId: 附件 ID（字符串或 null）
     *      videoUrl: 视频 URL（字符串或 null）
     *      coverUrl: 封面图 URL（字符串或 null）
     *      voiceUrl: 语音 URL（字符串或 null）
     *      inspectionTime: 巡查时间（字符串或 null）
     *      promoter: 推动人（字符串或 null）
     *      industryCode: 行业代码（字符串或 null）
     *      confDesc: 配置描述（字符串或 null）
     *      taskDesc: 任务描述（字符串，例如“长巡查计划名称超长巡查计划名称...”）
     *      taskStatusName: 任务状态名称（字符串或 null）
     *      taskName: 任务名称（字符串，例如“超长巡查计划名称...”）
     *      createDate: 创建日期（字符串，例如“2024-06-14”）
     *      inspectionStartTime: 巡查开始时间（字符串，例如“10:23:00”）
     *      inspectionEndTime: 巡查结束时间（字符串，例如“21:27:07”）
     *      feedbackKeeperId: 反馈巡查员 ID（字符串或 null）
     *      feedbackKeeperName: 反馈巡查员名称（字符串或 null）
     *      feedbackEnd: 反馈结束状态（字符串或 null）
     *      feedbackEndName: 反馈结束名称（字符串或 null）
     *      gridRange: 网格范围（字符串，格式为 JSON 数组）
     *      gridCenter: 网格中心点（字符串，格式为 JSON 数组）
     *      trailStartTime: 轨迹开始时间（字符串或 null）
     *      trailEndTime: 轨迹结束时间（字符串或 null）
     *      appUrl: 应用 URL（字符串或 null）
     *      sendFlag: 发送标识（字符串或 null）
     *      keeperIdList: 巡查员 ID 列表（字符串或 null）
     *      keeperPhoneNumber: 巡查员电话号码（字符串或 null）
     *      isLine: 是否是线路（布尔值或 null）
     *      avatar: 头像 URL（字符串或 null）
     *      color: 颜色（字符串，例如“#00b0ff”）
     *      borderColor: 边框颜色（字符串，例如“#00b0ff”）
     *      borderColorTransparency: 边框透明度（字符串，例如“0.5”）
     *      borderColorLineThickness: 边框线条厚度（字符串，例如“1”）
     *      colorTransparency: 颜色透明度（字符串，例如“0.5”）
     *      isGridManagerList: 网格管理员列表（数组，包含字符串，例如 [“测试林业”]）
     *      noGridManagerList: 非网格管理员列表（数组，可能为空或包含字符串，例如 [“测试”, “林业网格员”]）
     *      feedbackKeeperType: 反馈巡查员类型（字符串或 null）
     *      userId: 用户 ID（字符串或 null）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1718355368202,
     * 	"data": {
     * 		"taskId": "e05f9ae10fd1489e8a06a698beefa086",
     * 		"confId": "820a47678f46426797ef46efe26138a8",
     * 		"cnofName": null,
     * 		"tenantId": "122035807",
     * 		"gridId": "202406041242092356",
     * 		"gridName": "测试新增客0323-林业版本-1.0>北京>海淀区>五路居慧科大厦",
     * 		"curGridName": "五路居慧科大厦",
     * 		"taskStatus": "2",
     * 		"createTime": "2024-06-14 10:23:00",
     * 		"startTime": "2024-06-13 00:00:00",
     * 		"endTime": "2024-06-20 00:00:00",
     * 		"keeperId": "202406041643508862,202406050931488654,202406050941115705,202406050941336400,202406050941564373,202406050943123321",
     * 		"keeperName": "测试,测试林业,林业网格员,林业网格员2,林业网格员3,林业网格员4",
     * 		"retroaction": null,
     * 		"fjId": null,
     * 		"videoUrl": null,
     * 		"coverUrl": null,
     * 		"voiceUrl": null,
     * 		"inspectionTime": null,
     * 		"promoter": null,
     * 		"industryCode": null,
     * 		"confDesc": null,
     * 		"taskDesc": "长巡查计划名称超长巡查计划名称超长巡查计划名称超长巡查计划名称超长巡查计划名称超长巡查计划名称超长",
     * 		"taskStatusName": null,
     * 		"taskName": "超长巡查计划名称超长巡查计划名称超长巡查计划名称超长巡查计划名称超长巡查计划名称超长巡查计划名称超长",
     * 		"createDate": "2024-06-14",
     * 		"inspectionStartTime": "10:23:00",
     * 		"inspectionEndTime": "21:27:07",
     * 		"feedbackKeeperId": null,
     * 		"feedbackKeeperName": null,
     * 		"feedbackEnd": null,
     * 		"feedbackEndName": null,
     * 		"gridRange": "[[[116.269937,39.932423],[116.269891,39.932346],[116.269944,39.931861],[116.270967,39.93191],[116.271049,39.93197],[116.271067,39.932072],[116.271017,39.932379],[116.270935,39.932456],[116.269937,39.932423]]]",
     * 		"gridCenter": "[[116.2706009510597,39.9321769238956]]",
     * 		"trailStartTime": null,
     * 		"appUrl": null,
     * 		"trailEndTime": null,
     * 		"sendFlag": null,
     * 		"keeperIdList": null,
     * 		"keeperPhoneNumber": null,
     * 		"isLine": null,
     * 		"avatar": null,
     * 		"color": "#00b0ff",
     * 		"borderColor": "#00b0ff",
     * 		"borderColorTransparency": "0.5",
     * 		"borderColorLineThickness": "1",
     * 		"colorTransparency": "0.5",
     * 		"isGridManagerList": [
     * 			"测试林业"
     * 		],
     * 		"noGridManagerList": [
     * 			"测试",
     * 			"林业网格员",
     * 			"林业网格员2",
     * 			"林业网格员3",
     * 			"林业网格员4"
     * 		],
     * 		"feedbackKeeperType": null,
     * 		"userId": null
     *        }
     * }
     * @param inspectionTaskNoPageParam
     * @return
     */
    @PostMapping("/inspectionTask/getTaskInfoByCondition")
    Result<InspectionTaskVO> getTaskInfoByCondition(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam);


}
