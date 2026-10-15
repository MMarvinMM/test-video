package com.chinatower.video.api;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.deprecated.VideoQueryAPIMigrate;
import com.chinatower.video.api.entity.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
public class VideoQueryAPI {

    @Resource
    VideoQueryAPIMigrate videoQueryAPIMigrate;

    @Resource
    VideoQueryStduAPI videoQueryStduAPI;

    /**
     * 查询权限
     * @apiNote 查询权限
     * @apiVersion 13.53
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"roleId": "2"
     * }
     * @responseDemo
     * creator：创建者
     * deleteFlag：删除标识（0-正常，1-删除）
     * roleId：角色ID
     * roleName：角色名称
     * roleStatus：角色状态（0-启用，1-禁用）
     * creatorDate：创建时间（格式：yyyy-MM-dd HH:mm:ss）
     * roleType：角色类型
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694758405920,
     * 	"data": {
     * 		"creator": "admin",
     * 		"deleteFlag": 0,
     * 		"roleId": "2",
     * 		"roleName": "客户管理员",
     * 		"roleStatus": "0",
     * 		"creatorDate": "2022-08-12 17:52:01",
     * 		"roleType": "1"
     *        }
     * }
     * @param queryRoleReq
     * @return
     */
    @PostMapping("/video/query/sdk/selectRoleById")
    public Result selectRoleById(@RequestBody QueryRoleReq queryRoleReq){
        return videoQueryAPIMigrate.selectRoleById(queryRoleReq);
    };

    /**
     * 查询通道录像计划
     * @apiNote 查询通道录像计划
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		isAllDay：是否全程录像
     * 		timeSections：通道录像计划时间段
     * 		storageType：存储类型
     * 		storageDay：存储时长
     * 		isAudio：是否开启音频
     * 		recordPlanId：录像计划id
     * 		recordPlanName：录像计划名称
     * 		recordPlanStatus：录像计划状态
     * 		startDate：生效开始日期
     * 		endDate：生效结束日期
     * 		streamType：码流类型
     * 		dayOfWeek：日期
     * 		startTime：开始时间
     * 		endTime：结束时间
     * @apiVersion 13.21
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008"
     * }
     * @responseDemo
     * isAllDay：是否全程录像（必填，true-是，false-否）
     * timeSections：通道录像计划时间段（非必填）
     * streamType：码流类型（必填）
     * storageType：存储类型（必填）
     * storageDay：存储时长（单位：天，必填）
     * isAudio：是否开启音频（必填，true-是，false-否）
     * recordPlanId：录像计划ID（必填）
     * recordPlanName：录像计划名称（必填）
     * recordPlanStatus：录像计划状态（必填）
     * startDate：生效开始日期（格式：yyyy-MM-dd，必填）
     * endDate：生效结束日期（格式：yyyy-MM-dd，必填）
     * acceptNode：受理节点（必填）
     * accessNode：接入节点（必填）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1648458254956,
     * 	"data": [
     *                {
     * 			"isAllDay": 1,
     * 			"streamType": 0,
     * 			"storageType": 0,
     * 			"storageDay": 30,
     * 			"isAudio": 0,
     * 			"recordPlanName": "录像计划",
     * 			"recordPlanId": 12343,
     * 			"recordPlanStatus": 1,
     * 			"startDate": "2021-01-01",
     * 			"endDate": "2022-01-01",
     * 			"timeSections": [
     *                {
     * 					"dayOfWeek": 1,
     * 					"startTime": "063000",
     * 					"endTime": "180000"
     *                },
     *                {
     * 					"dayOfWeek": 2,
     * 					"startTime": "063000",
     * 					"endTime": "180000"
     *                }
     * 			]
     *        }
     * 	]
     * }
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/sdk/queryRecordPlan")
    public Result<List<QueryRecordPlanData>> queryRecordPlan(@RequestBody VideoBaseReq videoBaseReq){
        return videoQueryAPIMigrate.queryRecordPlan(videoBaseReq);
    };

    /**
     * 查询全部录像计划
     * @apiNote 查询全部录像计划
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		isAllDay：是否全程录像
     * 		timeSections：通道录像计划时间段
     * 		storageType：存储类型
     * 		storageDay：存储时长
     * 		isAudio：是否开启音频
     * 		recordPlanId：录像计划id
     * 		recordPlanName：录像计划名称
     * 		recordPlanStatus：录像计划状态
     * 		startDate：生效开始日期
     * 		endDate：生效结束日期
     * 		streamType：码流类型
     * 		dayOfWeek：日期
     * 		startTime：开始时间
     * 		endTime：结束时间
     * @apiVersion 13.48
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"customerCode": "122035807",
     * 	"tenantId": "122035807",
     * 	"queryFlag": "1",
     * 	"source": "1",
     * 	"pageNum": 1,
     * 	"pageSize": 10
     * }
     * @responseDemo
     * creator：创建者
     * createTime：创建时间（格式：yyyy-MM-dd HH:mm:ss）
     * createBy：创建者ID
     * updateBy：更新者ID
     * modifier：修改者
     * updateTime：更新时间（格式：yyyy-MM-dd HH:mm:ss）
     * deleteFlag：删除标识（0-正常，1-删除）
     * tenantId：客户编码
     * userId：用户ID
     * appVerCode：应用版本代码
     * industryCode：行业编码
     * dataScope：数据范围
     * criteriaList：查询条件列表
     * deviceCode：设备编码
     * channelCode：通道编码
     * orderCode：订单编码
     * userCode：用户编码
     * deviceType：设备类型
     * isAllDay：是否全程录像（1-是，0-否）
     * timeSections：时间段列表
     * streamType：码流类型
     * storageType：存储类型
     * storageDay：存储时长（天）
     * isAudio：是否开启音频（1-是，0-否）
     * recordPlanId：录像计划ID
     * recordPlanName：录像计划名称
     * recordPlanStatus：录像计划状态
     * startDate：生效开始日期（格式：yyyy-MM-dd）
     * endDate：生效结束日期（格式：yyyy-MM-dd）
     * customerCode：客户编码
     * packageCode：套餐编码
     * dayOfWeek：星期几
     * startTime：开始时间
     * endTime：结束时间
     * matchFlag：匹配标识
     * recordPlanIds：录像计划ID列表
     * creatorDate：创建日期
     * modifierDate：修改日期
     * queryFlag：查询标识
     * devName：设备名称
     * channelName：通道名称
     * source：数据来源
     * tenantList：客户列表
     * provinceId：省份ID
     * provinceName：省份名称
     * cityId：城市ID
     * cityName：城市名称
     * tenantName：客户名称
     * keyword：关键字
     * ordStatus：订单状态
     * devIdList：设备ID列表
     * pageNum：当前页码
     * pageSize：每页条数
     * size：当前页条数
     * startRow：开始行
     * endRow：结束行
     * pages：总页数
     * prePage：上一页
     * nextPage：下一页
     * isFirstPage：是否是第一页
     * isLastPage：是否是最后一页
     * hasPreviousPage：是否有上一页
     * hasNextPage：是否有下一页
     * navigatePages：导航页数
     * navigatepageNums：导航页码数组
     * navigateFirstPage：导航第一页
     * navigateLastPage：导航最后一页
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694748716546,
     * 	"data": {
     * 		"total": 3,
     * 		"list": [{
     * 			"creator": "junnan01",
     * 			"createTime": "2023-03-06 11:28:21",
     * 			"createBy": null,
     * 			"updateBy": null,
     * 			"modifier": "admin",
     * 			"updateTime": "2023-03-21 11:40:47",
     * 			"deleteFlag": 0,
     * 			"tenantId": "122035807",
     * 			"userId": null,
     * 			"appVerCode": null,
     * 			"industryCode": null,
     * 			"dataScope": null,
     * 			"criteriaList": null,
     * 			"deviceCode": "11010800831327000004",
     * 			"channelCode": "11010800001310000047",
     * 			"orderCode": null,
     * 			"userCode": null,
     * 			"deviceType": null,
     * 			"isAllDay": 1,
     * 			"timeSections": null,
     * 			"streamType": 0,
     * 			"storageType": 1,
     * 			"storageDay": 60,
     * 			"isAudio": 0,
     * 			"recordPlanId": "1678087573276",
     * 			"recordPlanName": "录像计划_20230321114046627",
     * 			"recordPlanStatus": 1,
     * 			"startDate": "2023-03-06",
     * 			"endDate": "2023-03-31",
     * 			"customerCode": null,
     * 			"packageCode": null,
     * 			"dayOfWeek": 0,
     * 			"startTime": null,
     * 			"endTime": null,
     * 			"matchFlag": null,
     * 			"recordPlanIds": null,
     * 			"creatorDate": null,
     * 			"modifierDate": null,
     * 			"queryFlag": null,
     * 			"devName": "铁塔产业园区-车库入口1",
     * 			"channelName": "铁塔产业园区-车库入口1_0",
     * 			"source": null,
     * 			"tenantList": null,
     * 			"provinceId": "420000",
     * 			"provinceName": "湖北省",
     * 			"cityId": "420100",
     * 			"cityName": "武汉市",
     * 			"tenantName": "测试新增客0323",
     * 			"keyword": null,
     * 			"ordStatus": 0,
     * 			"devIdList": null,
     * 			"pageNum": 0,
     * 			"pageSize": 0,
     * 			"creatorName": "测试新增客0323lxt111",
     * 			"modifierName": null
     *                }],
     * 		"pageNum": 1,
     * 		"pageSize": 10,
     * 		"size": 3,
     * 		"startRow": 1,
     * 		"endRow": 3,
     * 		"pages": 1,
     * 		"prePage": 0,
     * 		"nextPage": 0,
     * 		"isFirstPage": true,
     * 		"isLastPage": true,
     * 		"hasPreviousPage": false,
     * 		"hasNextPage": false,
     * 		"navigatePages": 8,
     * 		"navigatepageNums": [
     * 			1
     * 		],
     * 		"navigateFirstPage": 1,
     * 		"navigateLastPage": 1    * 	}
     * }
     * @param channelVideoPlanVO
     * @return
     */
    @PostMapping("/video/query/sdk/queryRecordPlanForPage")
    public Result queryRecordPlanForPage(@RequestBody VideoChannelVideoPlanVO channelVideoPlanVO){
        return videoQueryAPIMigrate.queryRecordPlanForPage(channelVideoPlanVO);
    };

    /**
     * 查询PTZ位置
     * @apiNote 查询PTZ位置
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		pan：平参数[0, 3600]
     * 		tilt：垂直参数[-1800,1800]
     * 		zoom：变倍参数[0, 128]
     * @apiVersion 13.22
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008"
     * }
     * @responseDemo
     * pan：平参数[0, 3600]
     * tilt：垂直参数[-1800,1800]
     * zoom：变倍参数[0, 128]
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1648458254956,
     * 	"data": {
     * 		"pan": 1800,
     * 		"tilt": 600,
     * 		"zoom": 1
     *        }
     * }
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/sdk/queryPtzPosition")
    public Result<PresetData> queryPtzPosition(@RequestBody VideoBaseReq videoBaseReq){
        return videoQueryAPIMigrate.queryPtzPosition(videoBaseReq);
    };

    /**
     * 查询预置点列表
     * @apiNote 查询预置点列表
     * @apiVersion 13.23
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "33060200001327000016",
     * 	"channelCode": "330602330000000191310000007"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * presetId：预置点id
     * presetName：预置点名称
     * preset：PTZ坐标（转义前）
     *      pan：平参数[0, 3600]
     *      tilt：垂直参数[-1800,1800]
     *      zoom：变倍参数[0, 128]
     * provinceCode省份编码
     * provinceName省份名称
     * cityCode地市编码
     * cityName地市名称
     * deviceCode设备编码
     * deviceName设备名称
     * imgUrl预置位图片url
     * pan 转义后p值
     * tilt 转义后t值
     * zoom 转义后z值
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692178371669,
     * 	"data": [
     *                {
     * 			"presetId": 114526,
     * 			"presetName": "预置位1",
     * 			"preset": {
     * 				"pan": 40,
     * 				"tilt": -150,
     * 				"zoom": 6
     *            },
     * 			"provinceCode": "110000",
     * 			"provinceName": "北京市",
     * 			"cityCode": "110100",
     * 			"cityName": "北京市",
     * 			"tenantId": "121086253",
     * 			"tenantName": "测试33",
     * 			"deviceCode": "11010800831327000004",
     * 			"deviceName": "铁塔产业园区-车库入口",
     * 			"industryCode": null,
     * 			"appVerCode": null,
     * 			"industryName": null,
     * 			"imgUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/prd/baseplatform/fb9b874a9ab7a611f9dae400dacabc6e/4a24068deb67aafc8a39b3d9b3116941/9fefceb0990a4444bfabcceea43ef70d.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1692178971&Signature=zUhvaEcSjG17mJXqRXiHrK%2Bsjbo%3D",
     * 			"createTime": "2022-12-02 01:51:09",
     * 			"pan": 356,
     * 			"tilt": 15,
     * 			"zoom": 2
     *        }
     * 	]
     * }
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/sdk/queryPresetList")
    public Result<List<QueryPresetListData>> queryPresetList(@RequestBody VideoBaseReq videoBaseReq){
        return videoQueryAPIMigrate.queryPresetList(videoBaseReq);
    };

    /**
     * 查询看守位
     * @apiNote 查询看守位
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		guardId：看守位id
     * 		staySeconds：停留时间
     * 	    isStart：是否已经启动
     * @apiVersion 13.24
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008"
     * }
     * @responseDemo
     * presetId：预置点ID（长整型）
     * guardId：看守位ID（长整型，必填）
     * guardName：看守位名称（字符串，必填）
     * staySeconds：停留时间（整型，必填，单位：秒）
     * isStart：是否已经启动（整型，必填，1-启动，0-未启动）
     * isGuard：是否为看守位（整型，1-是，0-否）
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692178371669,
     * 	"data": {}
     * }
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/sdk/queryGuardList")
    public Result<QueryGuardListData> queryGuardList(@RequestBody VideoBaseReq videoBaseReq){
        return videoQueryAPIMigrate.queryGuardList(videoBaseReq);
    };

    /**
     * 查询巡航路径
     * @apiNote 查询巡航路径
     * @apiVersion 13.25
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008",
     * 	"patrolId": 111
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
     * presets预置位列表
     *      presetId预置位编码
     *      presetName预置位名称
     *      staySeconds停留时长
     *      speed速度
     *      sort顺序
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1648458254956,
     * 	"data": [
     *                {
     * 			"patrolId": 11,
     * 			"isCruise": 1,
     * 			"patrolName": "巡航名称",
     * 			"presets": [
     *                {
     * 					"presetId": 1,
     * 					"presetName": "preset1",
     * 					"staySeconds": 10,
     * 					"speed": 10,
     * 					"sort ": 1
     *                }
     * 			]
     *        }
     * 	]
     * }
     * @param videoQueryPatrol
     * @return
     */
    @PostMapping("/video/query/sdk/queryPatrol")
    public Result<List<QueryPatrolData>> queryPatrol(@RequestBody VideoQueryPatrolData videoQueryPatrol){
        return videoQueryAPIMigrate.queryPatrol(videoQueryPatrol);
    };

    /**
     * 获取视频格式字典表，用于切换
     * @apiNote 获取视频格式字典表，用于切换
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		dictCode：字典编码
     * 		dictLabel：字典标签
     * 		dictType：字典类型
     * @apiVersion 13.49
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"dictType": "99",
     * 	"pageNum": 1,
     * 	"pageSize": 10,
     * }
     * @responseDemo
     * code：状态码，表示请求的处理结果（整型，200表示成功）
     * msg：响应消息（字符串，“请求成功”）
     * time：时间戳，表示响应生成的时间（长整型）
     * data：数据对象，包含分页和字典项信息
     * total：总记录数（整型）
     * list：字典项列表（数组）
     *      dictCode：字典项编码（整型）
     *      dictSort：字典项排序（整型）
     *      dictLabel：字典项标签（字符串）
     *      dictValue：字典项值（字符串）
     *      dictType：字典类型（字符串）
     *      status：状态（字符串，“0”表示正常）
     *      pageNum：当前页码（整型）
     *      pageSize：每页记录数（整型）
     *      size：当前页记录数（整型）
     *      startRow：当前页起始行数（整型）
     *      endRow：当前页结束行数（整型）
     *      pages：总页数（整型）
     *      prePage：前一页页码（整型，0表示无）
     *      nextPage：下一页页码（整型，0表示无）
     *      isFirstPage：是否为第一页（布尔型）
     *      isLastPage：是否为最后一页（布尔型）
     *      hasPreviousPage：是否有前一页（布尔型）
     *      hasNextPage：是否有下一页（布尔型）
     *      navigatePages：导航页数（整型）
     *      navigatepageNums：导航页码列表（数组）
     *      navigateFirstPage：导航第一页页码（整型）
     *      navigateLastPage：导航最后一页页码（整型）
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694749093273,
     * 	"data": {
     * 		"total": 3,
     * 		"list": [
     *                        {
     * 				"dictCode": 3863,
     * 				"dictSort": 1,
     * 				"dictLabel": "hls",
     * 				"dictValue": "3",
     * 				"dictType": "99",
     * 				"status": "0"
     *            },
     *            {
     * 				"dictCode": 3864,
     * 				"dictSort": 2,
     * 				"dictLabel": "flv",
     * 				"dictValue": "4",
     * 				"dictType": "99",
     * 				"status": "0"
     *            },
     *            {
     * 				"dictCode": 3865,
     * 				"dictSort": 3,
     * 				"dictLabel": "wss",
     * 				"dictValue": "5",
     * 				"dictType": "99",
     * 				"status": "0"
     *            }
     * 		],
     * 		"pageNum": 1,
     * 		"pageSize": 10,
     * 		"size": 3,
     * 		"startRow": 1,
     * 		"endRow": 3,
     * 		"pages": 1,
     * 		"prePage": 0,
     * 		"nextPage": 0,
     * 		"isFirstPage": true,
     * 		"isLastPage": true,
     * 		"hasPreviousPage": false,
     * 		"hasNextPage": false,
     * 		"navigatePages": 8,
     * 		"navigatepageNums": [
     * 			1
     * 		],
     * 		"navigateFirstPage": 1,
     * 		"navigateLastPage": 1
     * 	}
     * }
     * @param queryGetEnumInfoReq
     * @return
     */
    @PostMapping("/video/query/sdk/getEnumInfo")
    public Result getEnumInfo(@RequestBody QueryGetEnumInfoReq videoPtzQueryReq){
        return videoQueryAPIMigrate.getEnumInfo(videoPtzQueryReq);
    };

    /**
     * 根据定位点转动Ptz
     * @apiNote 根据定位点转动Ptz
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		pan：平参数[0, 3600]
     * 		tilt：垂直参数[-1800,1800]
     * 		zoom：变倍参数[0, 128]
     * @apiVersion 13.50
     * @tag 视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "11010800831327000008",
     * 	"channelCode": "11010800831310000008",
     * 	"cameraLon": 113.679072,
     * 	"cameraLat": 34.790439,
     * 	"pointLon": 113.679072,
     * 	"pointLat": 34.790439,
     * 	"tenantId": "122035807"
     * }
     * @responseDemo
     * code：状态码，表示请求的处理结果（整型，200表示成功）
     * msg：响应消息（字符串，“请求成功”）
     * time：时间戳，表示响应生成的时间（长整型）
     * data：数据对象，包含云台控制参数
     *      pan：水平参数（整型，取值范围[0, 3600]）
     *      tilt：垂直参数（整型，取值范围[-1800, 1800]）
     *      zoom：变倍参数（整型，取值范围[0, 128]）
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694749349543,
     * 	"data": {
     * 		"pan": 45,
     * 		"tilt": -5,
     * 		"zoom": 10
     *        }
     * }
     * @param videoPtzQueryReq
     * @return
     */
    @PostMapping("/video/query/turnToPtzByPointAngle2")
    public Result<PresetData> turnToPtzByPointAngle2(@RequestBody VideoPtzQueryRequest videoPtzQueryReq){
        return videoQueryAPIMigrate.turnToPtzByPointAngle2(videoPtzQueryReq);
    };

    /**
     * 批量获取通道实时流URL地址 看这里sdk用
     * @apiNote 批量获取通道实时流URL地址 看这里sdk用
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		streamUrl：视频流url地址
     * @apiVersion 13.51
     * @tag 视频能力
     * @requestDemo
     * [
     *        {
     * 		"deviceCode": "11010800831327000008",
     * 		"channelCode": "11010800831310000008",
     * 		"customerCode": "122035807",
     * 		"streamType": 1,
     * 		"protocolType": 3,
     * 		"netType": 2
     *    }
     * ]
     * @responseDemo
     * code：状态码，表示请求的处理结果（整型，200表示成功）
     * msg：响应消息（字符串，“请求成功”）
     * time：时间戳，表示响应生成的时间（长整型）
     * data：数据数组，包含流媒体信息
     *      streamUrl：视频流地址（字符串，例如“http://slsptest.tower0788.cn:9515/live/11010800831327000008_0_0_null.m3u8”）
     *      returnMsg：返回信息，表示流媒体请求的结果（字符串，例如“ok”）
     *      deviceCode：设备编码（字符串，例如“11010800831327000008”）
     *      channelCode：通道编码（字符串，例如“11010800831310000008”）
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694757077606,
     * 	"data": [
     *                {
     * 			"streamUrl": "http://slsptest.tower0788.cn:9515/live/11010800831327000008_0_0_null.m3u8",
     * 			"returnMsg": "ok",
     * 			"deviceCode": "11010800831327000008",
     * 			"channelCode": "11010800831310000008"
     *        }
     * 	]
     * }
     * @param list
     * @return
     */
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForLookHere")
    public Result getVideoRealtimeUrlsForLookHere(@RequestBody List<Map> list){
        return videoQueryStduAPI.getVideoRealtimeUrlsForLookHere(list);
    };

    /**
     * 全域感知大屏，圈选摄像机，批量拉取实时流
     * @param reqList
     * @return
     */
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForComponent")
    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList){
        return videoQueryStduAPI.getVideoRealtimeUrlsForComponent(reqList);
    };
}
