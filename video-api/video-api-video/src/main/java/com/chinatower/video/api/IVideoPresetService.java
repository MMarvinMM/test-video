package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.request.Page;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.api.factory.VideoPresetFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;



@FeignClient(contextId = "videoPresetService", value = ServiceNameConstants.VIDEO_SERVICE, fallbackFactory = VideoPresetFallbackFactory.class)
public interface IVideoPresetService {

	/**
	 * 增加预置点
	 * @apiNote 增加预置点
	 * @apiVersion 5.1
	 * @tag 五、视频能力
	 * @requestDemo
	 * {
	 * 	"tenantId": "122049393",
	 * 	"deviceCode": "33060200001327000016",
	 * 	"channelCode": "330602330000000191310000007",
	 * 	"presetName": "测试预置点0816",
	 * 	"reqFrom":"0"
	 * }
	 * @responseDemo
	 * code枚举
	 * 200  操作成功
	 * 400	业务异常
	 * 401	未认证
	 * 404	服务未找到
	 * 500	服务异常
	 *
	 * code：状态码
	 * msg：返回消息
	 * time：时间戳
	 *  {
	 * 	"code": 200,
	 * 	"msg": "增加预置点成功!",
	 * 	"time": 1692103003683
	 * }
	 * @param addPresetReq
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/addCameraPreset")
    Result addCameraPreset(@RequestBody AddPresetReq addPresetReq);

	/**
	 * 删除预置点
	 * @apiNote 删除预置点
	 * @apiVersion 5.2
	 * @tag 五、视频能力
	 * @requestDemo
	 * {
	 * 	"tenantId": "122035807",
	 * 	"deviceCode": "33060200001327000016",
	 * 	"channelCode": "330602330000000191310000007",
	 * 	"presetId": 2793332
	 * }
	 * @responseDemo
	 * code枚举
	 * 200  操作成功
	 * 400	业务异常
	 * 401	未认证
	 * 404	服务未找到
	 * 500	服务异常
	 * code：状态码
	 * msg：返回消息
	 * time：时间戳
	 *
	 * {
	 * 	"code": 200,
	 * 	"msg": "删除预置点成功!",
	 * 	"time": 1692103003683
	 * }
	 * @param presetDTO
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/delCameraPreset")
    Result delCameraPreset(@RequestBody DelPresetReq presetDTO);


	/**
	 * 批量删除 预置点
	 * @apiNote 批量删除 预置点
	 * @apiVersion 5.3
	 * @tag 五、视频能力
	 * @requestDemo
	 *        {
	 * 		"deviceCode": "11010800831327000008",
	 * 		"channelCode": "11010800831310000008",
	 * 		"presets": [2793332,2793331]
	 *    }
	 * @responseDemo
	 * code枚举
	 * 200  操作成功
	 * 400	业务异常
	 * 401	未认证
	 * 404	服务未找到
	 * 500	服务异常
	 * code：状态码
	 * msg：返回消息
	 * time：时间戳
	 * {
	 * 	"code": 200,
	 * 	"msg": "批量删除预置点成功!",
	 * 	"time": 1694682022478
	 * }
	 * @param presetDTO
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/batchDelCameraPreset")
    Result batchDelCameraPreset(@RequestBody BatchDelPresetReq presetDTO);

	/**
	 * 修改预置点
	 * @apiNote 修改预置点
	 * @apiVersion 5.4
	 * @tag 五、视频能力
	 * @requestDemo
	 * {
	 * 	"tenantId": "122035807",
	 * 	"deviceCode": "33060200001327000016",
	 * 	"channelCode": "330602330000000191310000007",
	 * 	"presetId": 2793332,
	 * 	"presetName": "预置位34",
	 * 	"reqFrom":"0"
	 * }
	 * @responseDemo
	 * code枚举
	 * 200  操作成功
	 * 400	业务异常
	 * 401	未认证
	 * 404	服务未找到
	 * 500	服务异常
	 * code：状态码
	 * msg：返回消息
	 * time：时间戳
	 * {
	 * 	"code": 200,
	 * 	"msg": "修改预置点成功!",
	 * 	"time": 1692103003683
	 * }
	 * @param modPresetReq
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/updatePresetInfo")
    Result updatePresetInfo(@RequestBody ModPresetReq modPresetReq);


	/**
	 * 查询订单预置点信息
	 * @apiNote 查询订单预置点信息
	 * @apiVersion 5.5
	 * @tag 五、视频能力
	 * @requestDemo
	 * {
	 * 	"tenantId": "122035807",
	 * 	"orderCode": "PO12203580720230307824272879616"
	 * }
	 * @responseDemo
	 * code：状态码
	 * msg：返回消息
	 * time：时间戳
	 * {
	 * 	"code": 200,
	 * 	"msg": "请求成功",
	 * 	"time": 1694682941270,
	 * 	"data": [
	 *{
	 *     * presetId：预置点ID
	 *     * presetName：预置点名称
	 *     * presetCode：预置点编码
	 *     * moveSpeed：转动速度（范围：0~254）
	 *     * pan：水平参数（范围：[0, 3600]）
	 *     * tilt：垂直参数（范围：[-1800, 1800]）
	 *     * zoom：变倍参数（范围：[0, 128]）
	 *     * imgUrl：预置位图片URL
	 *     * deviceCode：设备编码
	 *     * channelCode：通道编码
	 *     * tenantId：客户编码
	 *     * userCode：用户编码
	 *     * modifyTime：修改时间
	 *     * orderCode：订单编码
	 *     * moveSpreed：转动速度（冗余字段）
	 *     * presets：预置点数组
	 *     * staySeconds：停留时间（单位：秒）
	 *     * lockToken：云台控制令牌（解锁时必填）
	 *     * patrolId：巡航路径ID
	 *     * speed：巡航速度（到目标预置点的速度：0-全速；1-1/2倍速；2-1/4倍速；3-1/8倍速；4-1/16倍速）
	 *     * sort：预置位排序
	 *     * devIdList：设备ID列表
	 *     * streamType：码流类型
	 *     * imageUpUrl：图片回传服务地址
	 *     * imageName：图片名称
	 *     * isplot：是否需要标绘（0-否，1-是）
	 *     * industryCode：行业代码
	 *     * appVerCode：应用版本代码
	 *     * presetSnapshotJob：是否是定时任务抓图（1-是定时抓图，其他值-否）
	 * }
	 * 	]
	 * }
	 * @param presetDTO
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/queryOrderPresetInfo")
    Result<List<PresetDTO>> queryOrderPresetInfo(@RequestBody QueryOrderDeviceSetDTO presetDTO);



	/**
	 * 获得预置位图片
	 * @apiNote 获得预置位图片
	 * @apiVersion 5.6
	 * @tag 五、视频能力
	 * @requestDemo
	 * {
	 * 	"presetId": "52979",
	 * 	"deviceCode":"33060200001327000016",
	 * 	"channelCode":"330602330000000191310000007"
	 * }
	 * @responseDemo
	 * code枚举
	 * 200  操作成功
	 * 400	业务异常
	 * 401	未认证
	 * 404	服务未找到
	 * 500	服务异常
	 * imgUrl预置位图片url
	 * {
	 * 	"code": 200,
	 * 	"msg": "请求成功",
	 * 	"time": 1694683102483,
	 * 	"data": {
	 * 		"imgUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/daf1baf4abc3452757006ba5ed5a7fec/bb63623f0fab3df043d86d12097a96b8/6c8db2620318457d9d375dace0144a9e.png"
	 *        }
	 * }
	 * @param presetDTO
	 * @return
	 */
    @PostMapping("/video/algorithm/sdk/getPresetImg")
    Result<PresetVO> getPresetImg(@RequestBody PresetDTO presetDTO);


	/**
	 * 根据预置位id查询ptz信息
	 * @apiNote 根据预置位id查询ptz信息
	 * @apiVersion 5.7
	 * @tag 五、视频能力
	 * @requestDemo
	 * [
	 *        {
	 * 	    "posType":2,
	 * 		"presetId": 55272
	 *    }
	 * ]
	 * @responseDemo
	 * pan：水平参数
	 * tilt：垂直参数
	 * zoom：变倍参数
	 * priestId：预置位id
	 * presetName：预置位名称
	 * [
	 *    {
	 * 		"pan": 0,
	 * 		"tilt": 0,
	 * 		"zoom": 0,
	 * 		"presetId": 55272,
	 * 		"presetName": null
	 *    }
	 * ]
	 * @param pos
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/selectPTZByIdList")
    List<AlgorithmParamPresetVO> selectPTZByIdList(@RequestBody List<AlgorithmConfigVO> pos);


	/**
	 * 根据预置位id查询预置位信息
	 * @apiNote 根据预置位id查询预置位信息
	 * @apiVersion 5.8
	 * @tag 五、视频能力
	 * @requestDemo
	 * 55272
	 * @responseDemo
	 * presetId：预置点ID
	 * presetName：预置点名称
	 * presetCode：预置点编码
	 * moveSpeed：转动速度（范围：0~254）
	 * pan：水平参数（范围：[0, 3600]）
	 * tilt：垂直参数（范围：[-1800, 1800]）
	 * zoom：变倍参数（范围：[0, 128]）
	 * imgUrl：预置位图片URL
	 * deviceCode：设备编码
	 * channelCode：通道编码
	 * tenantId：客户编码
	 * userCode：用户编码
	 * createTime：创建时间
	 * modifyTime：修改时间
	 * orderCode：订单编码
	 * deleteFlag：删除标识（正常0，删除1）
	 * isGuard：是否为看守位（0-否，1-是）
	 * industryCode：行业代码
	 * appVerCode：应用版本代码
	 *
	 * {
	 * 	"presetId": 55272,
	 * 	"presetName": "预置位1",
	 * 	"presetCode": null,
	 * 	"moveSpeed": null,
	 * 	"pan": null,
	 * 	"tilt": null,
	 * 	"zoom": null,
	 * 	"imgUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/baseplatform/ca3ede80c21cd96bba96774974b67185/6e1c7d3c8b2ecee93b44042e28181c61/1168f7bd30c542b9a456d17309572185.png",
	 * 	"deviceCode": "43062400831327000001",
	 * 	"channelCode": "43062400001130000001",
	 * 	"tenantId": "122035807",
	 * 	"userCode": "8f39282ee6bb4dedb32ab6027ff167fc",
	 * 	"createTime": "2023-09-12 11:46:45",
	 * 	"updateTime": "2023-09-12 11:46:45",
	 * 	"orderCode": "PO12203580720230907745803513856",
	 * 	"deleteFlag": 0,
	 * 	"isGuard": "0",
	 * 	"industryCode": "000000",
	 * 	"appVerCode": "2.0",
	 * 	"creator": null,
	 * 	"modifier": null,
	 * 	"staySeconds": null,
	 * 	"snapshotCycle": 1,
	 * 	"snapshotDay": null,
	 * 	"snapshotTime": null
	 * }
	 * @param videoPresetDO
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/selectById")
    VideoPresetDO selectById(@RequestBody VideoPresetDO videoPresetDO);

	/**
	 * 分页查询预置位定时抓的图片
	 * @apiNote 分页查询预置位定时抓的图片
	 * @apiVersion 5.9
	 * @tag 五、视频能力
	 * @requestDemo
	 * {
	 *    "deviceCode": "51150200831327000256+51150200831187000005",
	 *    "channelCode": "51150200001180000007",
	 *    "presetIdList": [53906,53905],
	 *    "startTime": "2023-09-15 00:00:00",
	 *    "endTime": "2023-09-15 23:59:59",
	 *    "pageNum": 1,
	 *    "pageSize": 100
	 * }
	 * @responseDemo
	 * presetId：预置点ID
	 * deviceCode：设备编码
	 * imgUrl：预置位图片URL
	 * channelCode：通道编码
	 * snapshotTime：抓图时间（格式：yyyy-MM-dd HH:mm:ss，时区：GMT+8）
	 * deleteFlag：删除标识（正常0，删除1）
	 *
	 * {
	 *    "code": 200,
	 *    "msg": "请求成功",
	 *    "time": 1694794349500,
	 *    "data": {
	 *        "pageNo": 0,
	 *        "pageSize": 100,
	 *        "count": 1,
	 *        "sortEntity": {
	 *            "snapshotTime": "DESC"
	 *        },
	 *        "list": [
	 *            {
	 *                "collectionName": null,
	 *                "presetId": 53922,
	 *                "deviceCode": "51150200831327000256+51150200831187000005",
	 *                "imgUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/baseplatform/ca3ede80c21cd96bba96774974b67185/decb33d0c1f923ed7d9e5fd482d720fa/c0c9fe110df648a3a0e51d222b0e5cef.png?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1694794949&Signature=m9AV0MKGl3C6Vx4SgdUePTMcsds%3D",
	 *                "channelCode": "51150200001180000007",
	 *                "snapshotTime": "2023-09-15 23:20:05",
	 *                "deleteFlag": 0
	 *            }
	 *        ]
	 *    }
	 * }
	 * @param presetListImgJobDTO
	 * @return
	 */
    @PostMapping("/video/patrol/sdk/queryPresetListImgJob")
    Result<Page<VmVideoPresetSnapshotMongoVO>> queryPresetListImgJob(@Validated @RequestBody PresetListImgJobDTO presetListImgJobDTO);

	/**
	 * 功能描述: 分页查询预置位定时抓的图片
	 *
	 * @param: presetImgJobDTO
	 * @auther: wanglei
	 * @date: 2023/6/11
	 * @version: 1.5
	 */
	@PostMapping("/video/patrol/queryPresetImgJob")
	Result<Page<VmVideoPresetSnapshotMongo>> queryPresetImgJob(@RequestBody PresetImgJobDTO presetImgJobDTO);

	@PostMapping("/video/preset/updateWarningOrderId")
	Result<String> updateWarningOrderId(@RequestBody PresetImgVO presetImgVO);
}
