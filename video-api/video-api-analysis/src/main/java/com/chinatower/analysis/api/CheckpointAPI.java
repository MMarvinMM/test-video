package com.chinatower.analysis.api;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.factory.CheckpointFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "CheckpointAPI",
        value = ServiceNameConstants.STATICS_SERVICE,
        fallbackFactory = CheckpointFallbackFactory.class)
public interface CheckpointAPI {

    /**
     * 查询卡口摄像机当日识别次数
     * @apiNote 查询卡口摄像机当日识别次数
     * @apiVersion 7.53
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","deviceCode": "","validDeviceCodes":["36040200831327000005","36040200831327000006"]}
     * @responseDemo {"code": 200,"msg":"请求成功","time": 1648458254956,"data":{"num":1}}
     *      返回参数data下字段解析：
     *     	num：识别次数
     * @param checkpointConditionVO 入参
     * @return Result<String>
     */
    @PostMapping("/checkpoint/getCheckpointEventNumByDay")
    public Result<String> getCheckpointEventNumByDay(@Validated @RequestBody CheckpointConditionVO checkpointConditionVO);

    /**
     * 查询卡口摄像机当日识别次数
     * @apiNote 查询卡口摄像机当日识别次数（行业）
     * @apiVersion 7.54
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","deviceCode": "36040200831327000005","validDeviceCodes":[]}
     * @responseDemo {"code": 200,"msg":"请求成功","time": 1648458254956,"data":{"num":1}}
     *      返回参数data下字段解析：
     *     	num：识别次数
     * @param checkpointConditionVO 入参
     * @return Result<String>
     */
    @PostMapping("/industry/checkpoint/getCheckpointEventNumByDay")
    public Result<String> getIndustryCheckpointEventNumByDay(@Validated @RequestBody CheckpointConditionVO checkpointConditionVO);

    /**
     * 实时抓拍记录查询
     * @apiNote 实时抓拍记录查询
     * @apiVersion 7.55
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"5","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","deviceCode": "36040200831327000005",
     * "deviceName": "九江十里区赛阳凤凰站点","vehicleNumber":"京A888888","pageNum":"1","pageSize":"10"}
     * @responseDemo {"total": 3,"rows": [{"alarmId": "19","alarmType": "101","alarmTime": "2020-08-27 18:32:52","alarmBody": "非法闯入","deviceCode": "42010300011327000001","deviceName": "湖北铁塔视联测试2","channelCode": "63460196358464234155",
     * "channelName": "湖北铁塔视联测试2_1","pan": "24","tilt": "45","zoom": "224","longitude": null,"latitude": "346.1232","alarmCode": "1101010000131060688011010100001180000011","algorithmCode": "1101010000131060688011010100001180000011",
     * "originalPictureUrl": "http://localost:8080/dssd/wew.jpg","taskId": "qwwqwrwe4fwereeet43dfgdgd","alarmVideoUrl": "http://localost:8080/dssd/wew.mp4","alarmPictureUrl": "","cityCode": "420100","countyCode": "420113","provinceCode": "420000",
     * "cityName": "武汉市","countyName": "汉南区","provinceName": "湖北省","importanceLevel": "0","alarmSource": null,"deleteFlag": "0","vehicleNumber": null,"vehicleType": null,"vehicleNumColor": null,"vehicleColor": null}],"code": 200}
     *      返回参数data下字段解析：
     *     	alarmId：告警ID
     *     	alarmType：告警类型
     *     	alarmTime：告警时间
     *     	alarmBody：告警标题
     *     	deviceCode：设备编码
     *     	deviceName：设备名称
     *     	channelCode：通道编码
     *     	channelName：团队名称
     *     	pan：水平参数
     *     	tilt：垂直参数
     *     	zoom：变备参数
     *     	longitude：经度
     *     	latitude：纬度
     *     	alarmCode：告警编码
     *     	algorithmCode：算法编码
     *     	originalPictureUrl：原始图片文件url
     *     	taskId：任务编码
     *     	alarmVideoUrl：视频URL
     *     	alarmPictureUrl：框图url
     *     	cityCode：市编码
     *     	countyCode：区编码
     *     	provinceCode：省编码
     *     	cityName：城市名称
     *     	countyName：区名称
     *     	provinceName：省名称
     *     	importanceLevel：重要等级
     *     	alarmSource：告警来源
     *     	deleteFlag：0正常1删除
     *     	vehicleNumber：车牌号
     *     	vehicleType：车牌类型
     *     	vehicleNumColor：车牌颜色
     *     	vehicleColor：车牌颜色
     *
     * @param checkpointBatchVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/checkpoint/getCaptureRecord")
    public TableDataInfo getCaptureRecord(@Validated @RequestBody CheckpointBatchVO checkpointBatchVO);

    /**
     * 实时抓拍记录查询
     * @apiNote 实时抓拍记录查询（行业）
     * @apiVersion 7.56
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"5","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","deviceCode": "36040200831327000005",
     * "deviceName": "九江十里区赛阳凤凰站点","vehicleNumber":"京A888888","pageNum":"1","pageSize":"10"}
     * @responseDemo {"total": 3,"rows": [{"alarmId": "19","alarmType": "101","alarmTime": "2020-08-27 18:32:52","alarmBody": "非法闯入","deviceCode": "42010300011327000001","deviceName": "湖北铁塔视联测试2","channelCode": "63460196358464234155",
     * "channelName": "湖北铁塔视联测试2_1","pan": "24","tilt": "45","zoom": "224","longitude": null,"latitude": "346.1232","alarmCode": "1101010000131060688011010100001180000011","algorithmCode": "1101010000131060688011010100001180000011",
     * "originalPictureUrl": "http://localost:8080/dssd/wew.jpg","taskId": "qwwqwrwe4fwereeet43dfgdgd","alarmVideoUrl": "http://localost:8080/dssd/wew.mp4","alarmPictureUrl": "","cityCode": "420100","countyCode": "420113","provinceCode": "420000",
     * "cityName": "武汉市","countyName": "汉南区","provinceName": "湖北省","importanceLevel": "0","alarmSource": null,"deleteFlag": "0","vehicleNumber": null,"vehicleType": null,"vehicleNumColor": null,"vehicleColor": null}],"code": 200}
     *      返回参数data下字段解析：
     *     	alarmId：告警ID
     *     	alarmType：告警类型
     *     	alarmTime：告警时间
     *     	alarmBody：告警标题
     *     	deviceCode：设备编码
     *     	deviceName：设备名称
     *     	channelCode：通道编码
     *     	channelName：团队名称
     *     	pan：水平参数
     *     	tilt：垂直参数
     *     	zoom：变备参数
     *     	longitude：经度
     *     	latitude：纬度
     *     	alarmCode：告警编码
     *     	algorithmCode：算法编码
     *     	originalPictureUrl：原始图片文件url
     *     	taskId：任务编码
     *     	alarmVideoUrl：视频URL
     *     	alarmPictureUrl：框图url
     *     	cityCode：市编码
     *     	countyCode：区编码
     *     	provinceCode：省编码
     *     	cityName：城市名称
     *     	countyName：区名称
     *     	provinceName：省名称
     *     	importanceLevel：重要等级
     *     	alarmSource：告警来源
     *     	deleteFlag：0正常1删除
     *     	vehicleNumber：车牌号
     *     	vehicleType：车牌类型
     *     	vehicleNumColor：车牌颜色
     *     	vehicleColor：车牌颜色
     *
     * @param checkpointBatchVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/industry/checkpoint/getCaptureRecord")
    public TableDataInfo getIndustryCaptureRecord(@Validated @RequestBody CheckpointBatchVO checkpointBatchVO);

    /**
     * 查询车流趋势统计
     * @apiNote 查询车流趋势统计
     * @apiVersion 7.57
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"label":"2022-02-01 18:07:55","num": 1}}
     *      返回参数data下字段解析：
     *     	label：时间
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointTrendDataVO>>
     */
    @PostMapping("/checkpoint/getTrafficTrendStatistics")
    public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流趋势统计
     * @apiNote 查询车流趋势统计（行业）
     * @apiVersion 7.58
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"label":"2022-02-01 18:07:55","num": 1}}
     *      返回参数data下字段解析：
     *     	label：时间
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointTrendDataVO>>
     */
    @PostMapping("/industry/checkpoint/getTrafficTrendStatistics")
    public Result<List<CheckpointTrendDataVO>> getIndustryTrafficTrendStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流高频时段统计
     * @apiNote 查询车流高频时段统计
     * @apiVersion 7.59
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"label":"2022-02-01 18:07:55","num": 1}}
     *      返回参数data下字段解析：
     *     	label：时间段
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointTrendDataVO>>
     */
    @PostMapping("/checkpoint/getHighFrequencyPeriodStatistics")
    public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流高频时段统计
     * @apiNote 查询车流高频时段统计（行业）
     * @apiVersion 7.60
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"label":"2022-02-01 18:07:55","num": 1}}
     *      返回参数data下字段解析：
     *     	label：时间段
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointTrendDataVO>>
     */
    @PostMapping("/industry/checkpoint/getHighFrequencyPeriodStatistics")
    public Result<List<CheckpointTrendDataVO>> getIndustryHighFrequencyPeriodStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询卡口车流排行统计
     * @apiNote 查询车流高频时段统计
     * @apiVersion 7.61
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648458254956,"data":{"num":1,"deviceCode":"11111","deviceName":"2222"}}
     *      返回参数data下字段解析：
     *     	deviceCode：设备编码
     *    	deviceName：设备名称
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointDeviceRankingDataVO>>
     */
    @PostMapping("/checkpoint/getTrafficFlowRankingStatistics")
    public Result<List<CheckpointDeviceRankingDataVO>> getTrafficFlowRankingStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询卡口车流排行统计
     * @apiNote 查询车流高频时段统计（行业）
     * @apiVersion 7.62
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648458254956,"data":{"num":1,"deviceCode":"11111","deviceName":"2222"}}
     *      返回参数data下字段解析：
     *     	deviceCode：设备编码
     *    	deviceName：设备名称
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointDeviceRankingDataVO>>
     */
    @PostMapping("/industry/checkpoint/getTrafficFlowRankingStatistics")
    public Result<List<CheckpointDeviceRankingDataVO>> getIndustryTrafficFlowRankingStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流趋势统计(支持自定义时间查询)
     * @apiNote 查询车流趋势统计(支持自定义时间查询)
     * @apiVersion 7.63
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"label":"2022-02-01 18:07:55","num": 1}}
     *      返回参数data下字段解析：
     *     	label：日期
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointTrendDataVO>>
     */
    @PostMapping("/checkpoint/getTrafficTrendStatisticsByTime")
    public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatisticsByTime(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流高频时段统计(支持自定义时间查询)
     * @apiNote 查询车流高频时段统计(支持自定义时间查询)
     * @apiVersion 7.64
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648458254956,"data":{"num":1,"deviceCode":"11111","deviceName":"2222"}}
     *      返回参数data下字段解析：
     *     	label：日期
     *     	num：数量
     * @param checkpointStatisticsConditionVO 入参
     * @return Result<List<CheckpointDeviceRankingDataVO>>
     */
    @PostMapping("/checkpoint/getHighFrequencyPeriodStatisticsByTime")
    public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatisticsByTime(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

}
