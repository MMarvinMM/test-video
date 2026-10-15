package com.chinatower.analysis.api;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.factory.AnalysisFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "AnalysisAPI",
        value = ServiceNameConstants.STATICS_SERVICE,
        fallbackFactory = AnalysisFallbackFactory.class)
public interface AnalysisAPI {

    /**
     * 获取事件统计数量
     * @apiNote 获取事件统计数量
     * @apiVersion 7.124
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","industryCode":"","dimensionFlag":"2"}
     * @responseDemo {"code":200,"msg":"请求成功","time":1648517453712,"data":{"todayAll":601,"todayProcess":607,"todayComplete":0,"totalProcess":601,
     * "totalComplete":0,"penddingJudged":0,"pendingDispatched":0,"peddingDisposal":0,"pendingApprove":0,"penddingLeaderApprove":0,"overtime":0}}
     *      返回参数data下字段解析：
     *      todayAll：今日告警数量
     * 	    todayProcess：今日处理中数量
     *      todayComplete：今日已完结数量
     * 	    totalProcess：近半年处理中数量
     *      totalComplete：近半年已完结数量
     * 	    penddingJudged：今日待研判数量
     *      pendingDispatched：今日待调度数量
     * 	    peddingDisposal：今日待处置数量
     *      pendingApprove：今日待核实数量
     * 	    penddingLeaderApprove：今日待批示数量
     * 	    overtime：今日已超期数量
     * 	    @param analysisVO 入参
     * @return Result<EventNumVO>
     */
    @PostMapping("/workbench/getEventNum")
    Result<EventNumVO> getEventNum(@RequestBody AnalysisVO analysisVO);

    /**
     * 获取事件统计数量
     * @apiNote 获取事件统计数量（行业）
     * @apiVersion 7.125
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","industryCode":"","dimensionFlag":"2"}
     * @responseDemo {"code":200,"msg":"请求成功","time":1648517453712,"data":{"todayAll":601,"todayProcess":607,"todayComplete":0,"totalProcess":601,
     * "totalComplete":0,"penddingJudged":0,"pendingDispatched":0,"peddingDisposal":0,"pendingApprove":0,"penddingLeaderApprove":0,"overtime":0}}
     *      返回参数data下字段解析：
     *      todayAll：今日告警数量
     * 	    todayProcess：今日处理中数量
     *      todayComplete：今日已完结数量
     * 	    totalProcess：近半年处理中数量
     *      totalComplete：近半年已完结数量
     * 	    penddingJudged：今日待研判数量
     *      pendingDispatched：今日待调度数量
     * 	    peddingDisposal：今日待处置数量
     *      pendingApprove：今日待核实数量
     * 	    penddingLeaderApprove：今日待批示数量
     * 	    overtime：今日已超期数量
     * @param analysisVO 入参
     * @return Result<EventNumVO>
     */
    @PostMapping("/industry/workbench/getEventNum")
    Result<EventNumVO> getIndustryEventNum(@RequestBody AnalysisVO analysisVO);

    /**
     * 按租户-摄像机数量统计
     * @apiNote 按租户-摄像机数量统计
     * @apiVersion 7.126
     * @tag 七、统计能力
     * @requestDemo {"orgIdList":["11010800831327000008","10210010010000"],"areaCodeLists":["110000","210000"]}
     * @responseDemo {"code":200,"msg":"请求成功","time":1648517453712,"data":{"channelCountOffline":0,"devCountOffline":0,"deviceCountTotal":5,
     * "onlinePercentageDev":"1.00","channelCountOnline":5,"onlinePercentage":"1.00","channelCountTotal":5,"devCountOnline":5}}
     *      返回参数data下字段解析：
     *	    channelCountOffline：通道离线数
     *	    channelCountOnline：通道在线数
     *	    channelCountTotal：通道总数
     *	    devCountOffline：摄像机离线数
     *	    devCountOnline： 摄像机在线数
     *     	deviceCountTotal：摄像机总数
     *     	onlinePercentage：通道在线率
     *     	onlinePercentageDev：设备在线率
     * @param analysisVO 入参
     * @return Result<DeviceStatisticsVO>
     */
    @PostMapping("/workbench/getDeviceStatistics")
    Result<DeviceStatisticsVO> getDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 按租户-摄像机数量统计
     * @apiNote 按租户-摄像机数量统计（行业）
     * @apiVersion 7.127
     * @tag 七、统计能力
     * @requestDemo {"orgIdList":["11010800831327000008","10210010010000"],"areaCodeLists":["110000","210000"]}
     * @responseDemo {"code":200,"msg":"请求成功","time":1648517453712,"data":{"channelCountOffline":0,"devCountOffline":0,"deviceCountTotal":5,
     * "onlinePercentageDev":"1.00","channelCountOnline":5,"onlinePercentage":"1.00","channelCountTotal":5,"devCountOnline":5}}
     *      返回参数data下字段解析：
     *	    channelCountOffline：通道离线数
     *	    channelCountOnline：通道在线数
     *	    channelCountTotal：通道总数
     *	    devCountOffline：摄像机离线数
     *	    devCountOnline： 摄像机在线数
     *     	deviceCountTotal：摄像机总数
     *     	onlinePercentage：通道在线率
     *     	onlinePercentageDev：设备在线率
     * @param analysisVO 入参
     * @return Result<DeviceStatisticsVO>
     */
    @PostMapping("/industry/workbench/getDeviceStatistics")
    Result<DeviceStatisticsVO> getIndustryDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 获取设备统计
     * @apiNote 获取设备统计
     * @apiVersion 7.128
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","provinceId":"110000","cityId":"110100","startDate":"2023-08-04 18:27:50","endDate":"2023-08-09 17:00:30"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1649401351518,"data": [{"areaCode": "410581","areaName": "林州市","longitude": null,"latitude": null,"deviceNum": 1,"devOnlineNum": 1}]}
     *      返回参数data下字段解析：
     *	    areaCode：区域编码
     *	    areaName：区域名称
     *	    longitude：经度
     *	    latitude：纬度
     *	    deviceNum： 设备数量
     *     	devOnlineNum：设备在线数量
     * @param vo 入参
     * @return Result<List<DevicesDataVO>>
     */
    @PostMapping("/statistic/getDevicesStatistics")
    Result<List<DevicesDataVO>> getDevicesStatistics(@RequestBody QueryParamVO vo);

    /**
     * 获取设备统计
     * @apiNote 获取设备统计（行业）
     * @apiVersion 7.129
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","provinceId":"110000","cityId":"110100","startDate":"2023-08-04 18:27:50","endDate":"2023-08-09 17:00:30"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1649401351518,"data": [{"areaCode": "410581","areaName": "林州市","longitude": null,"latitude": null,"deviceNum": 1,"devOnlineNum": 1}]}
     *      返回参数data下字段解析：
     *	    areaCode：区域编码
     *	    areaName：区域名称
     *	    longitude：经度
     *	    latitude：纬度
     *	    deviceNum： 设备数量
     *     	devOnlineNum：设备在线数量
     * @param vo 入参
     * @return Result<List<DevicesDataVO>>
     */
    @PostMapping("/industry/statistic/getDevicesStatistics")
    Result<List<DevicesDataVO>> getIndustryDevicesStatistics(@RequestBody QueryParamVO vo);

    /**
     * 获取告警统计
     * @apiNote 获取告警统计
     * @apiVersion 7.130
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","provinceId":"110000","cityId":"110100","startDate":"2023-08-04","endDate":"2023-08-09"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1649401379833,"data": [{"areaCode": "410581","areaName": "林州市","longitude": null,"latitude": null,"judgeNum": 15,"dispatchNum": 0,
     * "grabNum": 0,"handleNum": 0,"approveNum": 0,"orderCompleteNum": 0}]}
     *      返回参数data下字段解析：
     *	    areaCode：区域编码
     *	    areaName：区域名称
     *	    longitude：经度
     *	    latitude：纬度
     *	    judgeNum： 研判数量
     *     	dispatchNum：调度数量
     *	    grabNum：待认领数量
     *	    handleNum：待处置数量
     *	    approveNum：待核实数量
     *	    orderCompleteNum：已完结数量
     * @param vo 入参
     * @return Result<List<EventDataVO>>
     */
    @PostMapping("/statistic/getEventStatistics")
    Result<List<EventDataVO>> getEventStatistics(@RequestBody QueryParamVO vo);

    /**
     * 获取告警统计
     * @apiNote 获取告警统计（行业）
     * @apiVersion 7.131
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","provinceId":"110000","cityId":"110100","startDate":"2023-08-04","endDate":"2023-08-09"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1649401379833,"data": [{"areaCode": "410581","areaName": "林州市","longitude": null,"latitude": null,"judgeNum": 15,"dispatchNum": 0,
     * "grabNum": 0,"handleNum": 0,"approveNum": 0,"orderCompleteNum": 0}]}
     *      返回参数data下字段解析：
     *	    areaCode：区域编码
     *	    areaName：区域名称
     *	    longitude：经度
     *	    latitude：纬度
     *	    judgeNum： 研判数量
     *     	dispatchNum：调度数量
     *	    grabNum：待认领数量
     *	    handleNum：待处置数量
     *	    approveNum：待核实数量
     *	    orderCompleteNum：已完结数量
     * @param vo 入参
     * @return Result<List<EventDataVO>>
     */
    @PostMapping("/industry/statistic/getEventStatistics")
    Result<List<EventDataVO>> getIndustryEventStatistics(@RequestBody QueryParamVO vo);

    /**
     * 获取告警看板统计
     * @apiNote 获取告警看板统计
     * @apiVersion 7.132
     * @tag 七、统计能力
     * @requestDemo {"industryCode":"","appVerCode":"","areaCode":"110000","date":"202308"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1649647512093,"data": [{"year": "2023","month": "8","day": "3","todayAll": 1}]}
     *      返回参数data下字段解析：
     *	    year：年
     *	    month：月
     *	    day：日
     *	    todayAll：告警数量
     * @param vo 入参
     * @return Result<List<EventBoardDataVO>>
     */
    @PostMapping("/statistic/getEventBoardStatistics")
    Result<List<EventBoardDataVO>> getEventBoardStatistics(@RequestBody EventBoardParamVO vo);

    /**
     * 获取告警看板统计
     * @apiNote 获取告警看板统计（行业）
     * @apiVersion 7.133
     * @tag 七、统计能力
     * @requestDemo {"industryCode":"","appVerCode":"","areaCode":"110000","date":"202308"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1649647512093,"data": [{"year": "2023","month": "8","day": "3","todayAll": 1}]}
     *      返回参数data下字段解析：
     *	    year：年
     *	    month：月
     *	    day：日
     *	    todayAll：告警数量
     * @param vo 入参
     * @return Result<List<EventBoardDataVO>>
     */
    @PostMapping("/industry/statistic/getEventBoardStatistics")
    Result<List<EventBoardDataVO>> getIndustryEventBoardStatistics(@RequestBody EventBoardParamVO vo);

    /**
     * 站点维度top10
     * @apiNote 站点维度top10（行业）
     * @apiVersion 7.134
     * @tag 七、统计能力
     * @requestDemo {"industryCode":"","appVerCode":"","startDate":"2023-08-04","endDate":"2023-08-09","areaCode":"110000","areaType":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1650271818267,"data": [{"deviceCode": "36040200831327000005","deviceName": "九江十里区赛阳凤凰站点","total": 119,"ranking": 1}]}
     *      返回参数data下字段解析：
     *	    deviceCode：设备编码
     *	    deviceName：设备名称
     *	    total：告警总数
     *	    ranking：排名
     * @param vo 入参
     * @return Result<List<DeviceTopTenDataVO>>
     */
    @PostMapping("/industry/statistic/getDeviceTopTen")
    Result<List<DeviceTopTenDataVO>> getIndustryDeviceTopTen(@RequestBody @Valid DeviceTopTenParamVO vo);

    /**
     * 站点维度top10
     * @apiNote 站点维度top10
     * @apiVersion 7.135
     * @tag 七、统计能力
     * @requestDemo {"industryCode":"","appVerCode":"","startDate":"2023-08-04","endDate":"2023-08-09","areaCode":"110000","areaType":"2"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1650271818267,"data": [{"deviceCode": "36040200831327000005","deviceName": "九江十里区赛阳凤凰站点","total": 119,"ranking": 1}]}
     *      返回参数data下字段解析：
     *	    deviceCode：设备编码
     *	    deviceName：设备名称
     *	    total：告警总数
     *	    ranking：排名
     * @param vo 入参
     * @return Result<List<DeviceTopTenDataVO>>
     */
    @PostMapping("/statistic/getDeviceTopTen")
    Result<List<DeviceTopTenDataVO>> getDeviceTopTen(@RequestBody @Valid DeviceTopTenParamVO vo);

    /**
     * 告警类型统计
     * @apiNote 告警类型统计
     * @apiVersion 7.136
     * @tag 七、统计能力
     * @requestDemo {"startTime":"2023-08-04","endTime":"2023-08-09","areaCode":"110000"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1650271818267,"data": [{"typeId": "1010105","typeName": "扬尘粉尘可视化监控告警","eventDate": "2023-08-01","num": 10}]}
     *      返回参数data下字段解析：
     *	    typeId：告警类型Id
     *	    typeName：告警类型名称
     *	    eventDate：日期
     *	    num：告警数量
     * @param vo 入参
     * @return Result<List<EventStatisticDataVO>>
     */
    @PostMapping("/statistic/getEventTypeStatistics")
    Result<List<EventStatisticDataVO>> getEventTypeStatistics(@RequestBody @Valid EventStatisticParamVO vo);

    /**
     * 告警时间统计-告警数量折线图
     * @apiNote 告警时间统计-告警数量折线图
     * @apiVersion 7.137
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101","townId":""}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": [{"label": "2021","num": 1000},{"label": "2022","num": 2000}]}
     *      返回参数data下字段解析：
     *	    label：时间段
     *	    num：告警数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventNumListApp")
    Result getEventNumListApp(@RequestBody AnalysisVO analysisVO);

    /**
     * 获取压制事件统计数量折线图
     * @apiNote 告警压制统计日期折线图
     * @apiVersion 7.138
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101","townId":""}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": [{"label": "2021","num": 1000},{"label": "2022","num": 2000}]}
     *      返回参数data下字段解析：
     *	    label：时间段
     *	    num：告警压制数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventInterceptNumApp")
    Result getEventInterceptNumApp(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警事件中拥有订购摄像机数量
     * @apiNote 告警事件中拥有订购摄像机数量信息
     * @apiVersion 7.139
     * @tag 七、统计能力
     * @requestDemo {"deviceCodeList":["11010800831327000008","10210010010000"]}
     * @responseDemo {"total": 1,"rows":[{"deviceNum":"10"}],"code": 200}
     *      返回参数data下字段解析：
     *	    deviceNum：设备数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/device/getDeviceNumInEvent")
    TableDataInfo getDeviceNumInEvent(@RequestBody AnalysisVO analysisVO);

    /**
     * 国土SDK-查询告警地块面积
     * @apiNote 查询告警地块面积
     * @apiVersion 7.140
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","industryCode":""}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1692089399542,"data": [{"plotAreaSum": 0,"tenantId": "122035797","industryCode": "100000"},{"plotAreaSum": 3,"tenantId": "122035797","industryCode": "000000"}]}
     *      返回参数data下字段解析：
     *	    plotAreaSum：地块面积
     *	    tenantId：租户Id
     *	    industryCode：行业编码
     * @param paramVO 入参
     * @return Result<List<Map<String, Object>>>
     */
    @PostMapping("/statistic/getEventPlotAreaStatistics")
    Result<List<Map<String, Object>>> getEventPlotAreaStatistics(@RequestBody EventBoardParamVO paramVO);

    /**
     * 告警来源统计（国土行业）
     * @apiNote 告警来源统计（行业）
     * @apiVersion 7.141
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04 00:00:00","queryEndTime":"2023-08-09 23:59:59","provinceId":"","cityId":"","countyId":"",
     * "warningSourceList":["1","2"],"warningTypeList":[],"deviceCodeIndustryList":[],"orderStatusList":["1","2","4"]}
     * @responseDemo {"total":7,"rows":[{"warningSource":"10","warningSourceName":"雷达告警","num":144125},{"warningSource":"8","warningSourceName":"物联告警","num":30619},
     * {"warningSource":"1","warningSourceName":"AI告警","num":6577},{"warningSource":"9","warningSourceName":"卫星告警","num":339},{"warningSource":"4","warningSourceName":"摄像机告警","num":43},
     * {"warningSource":"2","warningSourceName":"人工上报","num":41},{"warningSource":"3","warningSourceName":"一键告警","num":19}],"code":200}
     *      返回参数data下字段解析：
     *	    warningSource：告警来源Id
     *	    warningSourceName：告警来源名称
     *	    num：告警数量
     * @param analysisVO 入参
     * @return List<Map>
     */
    @PostMapping("/workbench/getEventAlarmNumSourceIndustry")
    TableDataInfo getEventAlarmNumSourceIndustry(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警类型统计（国土行业）
     * @apiNote 告警类型统计（行业）
     * @apiVersion 7.142
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04 00:00:00","queryEndTime":"2023-08-09 23:59:59","provinceId":"","cityId":"","countyId":"",
     * "warningSourceList":["1","2"],"warningTypeList":["1212102"],"deviceCodeIndustryList":["42282500831327000027"],"orderStatusList":["1","2"]}
     * @responseDemo {"total":34,"rows":[{"num":76196,"typeName":"雷达、AIS目标在电子围栏区域内航速过低","typeId":"2121103"},{"num":48112,"typeName":"雷达、AIS目标在电子围栏内航速过高","typeId":"2121102"},
     * {"num":13987,"typeName":"浓度超标报警","typeId":"3010101"},{"num":9605,"typeName":"物联告警220","typeId":"WLGJ-WLGJ220"},{"num":8114,"typeName":null,"typeId":"2121112"},
     * {"num":6517,"typeName":"人员闯入监控告警","typeId":"1212102"},{"num":5806,"typeName":"雨量告警","typeId":"WLGJ-YLGJ"},{"num":4193,"typeName":"雷达、AIS目标从电子围栏闯出外部","typeId":"2121101"}],"code":200}
     *      返回参数data下字段解析：
     *	    typeId：告警类型Id
     *	    typeName：告警类型名称
     *	    num：告警数量
     * @param analysisVO 入参
     * @return List<Map>
     */
    @PostMapping("/workbench/getEventAlarmNumTypeIndustry")
    TableDataInfo getEventAlarmNumTypeIndustry(@RequestBody AnalysisVO analysisVO);

    /**
     * 行政区域完结率统计
     * @apiNote 行政区域完结率统计
     * @apiVersion 7.143
     * @tag 七、统计能力
     * @apiNote
     * 返回参数解析：
     * 		total：总条数
     * 		allNum：告警事件数量
     * 		regionName：区域名称
     * @requestDemo {"queryStartTime":"2023-08-04 00:00:00","queryEndTime":"2023-08-09 23:59:59","warningSourceList":["1","2"],"warningTypeList":["1212102"],"deviceCodeIndustryList":["11010800831327000008","10210010010000"],"orderStatusList":[]}
     * @responseDemo {"total": 1,"rows": [{"falseNum":13,"acceptanceNum":818.0,"realNum":38,"avgTimeDay":"9时54分","dispatchNum":31,"completionRates":0.3006,"regionName":"北京市","repeatCompleteNum":8,"accuracy":78,
     * "cityId":"110100","townId":"110105010000","completionRate":0,"handleNum":5,"provinceId":"110000","allNum":11978,"duration":1282886.0,"avgTimes":593.9287037037037,"avgTime":594.0,"orderCompleteNum":36,"countyId":"110105",
     * "judgeNum":11086,"accuracys":77.9661,"processNum":11942.0,"approveNum":2}],"code": 200}
     *      返回参数data下字段解析：
     *      falseNum：误报数量
     *	    acceptanceNum：待受理数量
     *	    realNum：真警数量
     *	    avgTimeDay：处理时长天时分格式
     *	    dispatchNum：待调度数量
     *	    completionRates： 完结率
     *     	regionName：告警区域名称
     *	    repeatCompleteNum：重复告警数量
     *     	accuracy：准确率(保留整数)
     *     	cityId：市Id
     *     	townId：乡镇Id
     *     	completionRate：完结率(保留整数)
     *     	handleNum：待处置数量
     *     	provinceId：省Id
     *     	allNum：告警总数量
     *     	duration：完结事件总处理时长/秒
     *     	avgTimes：完结事件平均处理时长/分钟
     *     	avgTime：完结事件平均处理时长/分钟(保留整数)
     *     	orderCompleteNum：已完结数量
     *     	countyId：区Id
     *     	judgeNum：研判数量
     *     	accuracys：准确率
     *     	processNum：处理中数量
     *     	approveNum：待核实数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenAreaCompletionIndustry")
    TableDataInfo getEvenAreaCompletionIndustry(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警统计月份折线图
     * @apiNote 告警统计月份折线图
     * @apiVersion 7.144
     * @tag    七、统计能力
     * @requestDemo {"queryStartTime":"2023-01-01 00:00:00","queryEndTime":"2023-08-30 23:59:59","warningSourceList":["1","2"],"warningTypeList":["1212102"],"deviceCodeIndustryList":["11010800831327000008","10210010010000"],"orderStatusList":[]}
     * @responseDemo {"code":200,"msg":"请求成功","time":1695197600634,"data":[{"assignedDate":"1","assignedMonth":"2023-01","num":34},{"assignedDate":"2","assignedMonth":"2023-02","num":54},{"assignedDate":"3","assignedMonth":"2023-03","num":238},{"assignedDate":"4","assignedMonth":"2023-04","num":5209},{"assignedDate":"5","assignedMonth":"2023-05","num":1828},{"assignedDate":"6","assignedMonth":"2023-06","num":5775},{"assignedDate":"7","assignedMonth":"2023-07","num":268},{"assignedDate":"8","assignedMonth":"2023-08","num":5498}]}
     *      返回参数data下字段解析：
     *	    assignedDate：排名
     *	    assignedMonth：日期
     *	    num：告警数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEventNumListAppMonth")
    Result getEventNumListAppMonth(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询告警事件类型top10
     * @apiNote  查询告警事件类型top10
     * @apiVersion 7.145
     * @tag 七、统计能力
     * @requestDemo
     * {
     *   "queryStartTime": "2023-11-22 00:00:00",
     *   "queryEndTime": "2023-11-28 23:59:59",
     *   "warningSourceList": [],
     *   "warningTypeList": [],
     *   "orderStatusList": [],
     *   "deviceCodeIndustryList": [],
     *   "appVerCode": "1.0"
     * }
     * @responseDemo
     * {
     *   "total": 1,
     *   "rows": [
     *     {
     *       "num": 1,
     *       "typeName": "移动侦测告警告警",
     *       "typeId": "1818100"
     *     }
     *   ],
     *   "code": 200
     * }
     *      返回参数data下字段解析：
     *	    num：告警数量
     *	    typeName：告警类型名称
     *	    typeId：告警类型Id
     */
    @PostMapping("/workbench/getDevTypeYearMonthAppTop")
    TableDataInfo getDevTypeYearMonthAppTop(@RequestBody AnalysisVO analysisVO);

    /**
     * 根据标签查询标签下告警时间数并增加返回网格字段
     * @apiNote  根据标签查询标签下告警时间数并增加返回网格字段
     * @apiVersion 7.146
     * @tag 七、统计能力
     * @requestDemo
     * {
     *   "queryStartTime": "2023-11-22 00:00:00",
     *   "queryEndTime": "2023-11-28 23:59:59",
     *   "labelIds": [],
     *   "gridIdList": [],
     *       "pageNum": 1,
     *     "pageSize": 5,
     * }
     * @responseDemo
     *{
     * 	"total": 26,
     * 	"rows": [
     *                {
     * 			"tagId": "1188683",
     * 			"tagName": "测试66555",
     * 			"num": "2",
     * 			"gridId": "202305111512247002",
     * 			"gridName": "北京部分网格"
     *        },
     *        {
     * 			"tagId": "1188685",
     * 			"tagName": "测试74444444",
     * 			"num": "2",
     * 			"gridId": "202305111512247002",
     * 			"gridName": "北京部分网格"
     *        },
     *        {
     * 			"tagId": "1188680",
     * 			"tagName": "2222",
     * 			"num": "1",
     * 			"gridId": "202306271446361559",
     * 			"gridName": "5"
     *        },
     *        {
     * 			"tagId": "1188685",
     * 			"tagName": "测试74444444",
     * 			"num": "1",
     * 			"gridId": "202306271446361559",
     * 			"gridName": "5"
     *        },
     *        {
     * 			"tagId": "1188640",
     * 			"tagName": "测试数据26",
     * 			"num": "2",
     * 			"gridId": "202305111512247002",
     * 			"gridName": "北京部分网格"
     *        }
     * 	],
     * 	"code": 200
     * }
     *      返回参数data下字段解析：
     *	    tagId：标签Id
     *	    tagName：标签名称
     *	    num：告警数量
     *	    gridId：网格Id
     *	    gridName：网格名称
     */
    @PostMapping("/workbench/getAlarmNumByLabel")
    TableDataInfo getAlarmNumByLabel(@RequestBody StatisticsVO statisticsVO);


    /**
     * 查询设备昼夜数量(物联设备)
     * @apiNote  查询设备昼夜数量(物联设备)
     * @apiVersion 7.147
     * @tag 七、统计能力
     *
     * @requestDemo
     *{
     *     "queryStartTime": "2023-11-01 00:00:00",
     *     "queryEndTime": "2024-11-08 23:59:59",
     *     "deviceCode": "water-uvkz-6244-25",
     *     "areaCodeLists":[],
     *       "dayQueryStartTime": "2024-05-10 10:00:00",
     *     "dayQueryEndTime": "2024-05-10 10:59:59",
     *       "nightQueryStartTime": "2023-05-10 09:00:00",
     *     "nightQueryEndTime": "2024-05-10 09:59:59",
     *
     * }
     * @responseDemo
     *{
     * 	"total": 1,
     * 	"rows": [
     *                {
     * 			"dayProcessNum": 0,
     * 			"nightProcessNum": 3586,
     * 			"nightCompleteNum": 0,
     * 			"dayCompleteNum": 0,
     * 			"deviceTypeName": "物联设备",
     * 			"dayNum": 0,
     * 			"deviceCode": "water-uvkz-6244-25",
     * 			"nightNum": 3586,
     * 			"deviceName": "water-uvkz-6244-25"
     *        }
     * 	],
     * 	"code": 200
     * }
     * 返回参数解析：
     * 		total：总条数
     * 		deviceName：设备名称
     * 		dayNum：昼数量
     * 	    dayCompleteNum：昼已解决数量
     * 	    dayProcessNum：昼未解决数量
     * 	    nightNum：夜数量
     * 	    nightProcessNum：夜未解决数量
     * 	    nightCompleteNum：夜已解决数量
     */
    @PostMapping("/workbench/getDeviceDayAndNightNum")
    TableDataInfo getDeviceDayAndNightNum(@RequestBody StatisticsVO statisticsVO);


    /**
     * 告警进度处理统计
     * @apiNote  告警进度处理统计
     * @apiVersion 7.148
     * @tag 七、统计能力
     * @requestDemo
     *{
     *     "queryStartTime": "2023-11-01 00:00:00",
     *     "queryEndTime": "2024-11-08 23:59:59",
     *     "orderStatusList": [],
     *     "judgeResultList": [],
     *     "warningSourceList": [],
     *     "warningTypeList": [],
     *     "deviceCodeIndustryList": []
     * }
     * @responseDemo
     *{
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717759329593,
     *     "data": {
     *         "judgeNum": 0,
     *         "dispatchNum": 0,
     *         "handleNum": 0,
     *         "approveNum": 0,
     *         "acceptanceNum": 0,
     *         "orderCompleteNum": 0
     *     }
     * }
     * 返回参数解析：
     * 		judgeNum：待研判数量
     * 		dispatchNum：待调度数量
     * 		handleNum：待处置数量
     * 		approveNum：待核实数量
     * 		acceptanceNum：待受理数量
     * 		orderCompleteNum：已完结数量
     */
    @PostMapping("/workbench/getAlarmStatisticsProcessingNum")
    Result getAlarmStatisticsProcessingNum(@RequestBody StatisticsVO statisticsVO);


    /**
     * 查询摄像机设备告警数量排行
     * @apiNote  查询摄像机设备告警数量排行
     * @apiVersion 7.149
     * @tag 七、统计能力
     * @requestDemo
     *{
     *     "queryStartTime": "2023-11-01 00:00:00",
     *     "queryEndTime": "2024-11-08 23:59:59",
     *     "pageNum": 1,
     *     "pageSize": 10,
     *     "orderStatusList": [],
     *     "judgeResultList": [],
     *     "warningSourceList": [],
     *     "warningTypeList": [],
     *     "deviceCodeIndustryList": []
     * }
     * @responseDemo
     *{
     *     "total": 5,
     *     "rows": [
     *         {
     *             "allNum": 877,
     *             "deviceName": "海康3公里热成像",
     *             "deviceCode": "51150200831327000261"
     *         },
     *         {
     *             "allNum": 42,
     *             "deviceName": "铁塔产业园-11号楼-大来咯来咯华243",
     *             "deviceCode": "11010800831327000008"
     *         },
     *         {
     *             "allNum": 40,
     *             "deviceName": "福州连江敖江岱云-勿动超长超长超长超长超长超长超长超长超长",
     *             "deviceCode": "35012200001327000011"
     *         },
     *         {
     *             "allNum": 36,
     *             "deviceName": "铁塔园区-7号楼",
     *             "deviceCode": "12011100001327000017"
     *         },
     *         {
     *             "allNum": 1,
     *             "deviceName": "东亚村-湘阴渔政",
     *             "deviceCode": "43062400831327000001"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数解析：
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		allNum：告警总数
     *
     */
    @PostMapping("/workbench/getCamaraDeviceAreaDistributionNum")
    TableDataInfo getCamaraDeviceAreaDistributionNum(@RequestBody StatisticsVO statisticsVO);


    /**
     * 查询IOT物联设备告警数量排行
     * @apiNote  查询IOT物联设备告警数量排行
     * @apiVersion 7.150
     * @tag 七、统计能力
     * @requestDemo
     *{
     *     "queryStartTime": "2023-11-01 00:00:00",
     *     "queryEndTime": "2024-11-08 23:59:59",
     *     "pageNum": 1,
     *     "pageSize": 10,
     *     "orderStatusList": [],
     *     "judgeResultList": [],
     *     "warningSourceList": [],
     *     "warningTypeList": [],
     *     "deviceCodeIndustryList": []
     * }
     * @responseDemo
     *{
     *     "total": 5,
     *     "rows": [
     *         {
     *             "allNum": 877,
     *             "deviceName": "海康3公里热成像",
     *             "deviceCode": "51150200831327000261"
     *         },
     *         {
     *             "allNum": 42,
     *             "deviceName": "铁塔产业园-11号楼-大来咯来咯华243",
     *             "deviceCode": "11010800831327000008"
     *         },
     *         {
     *             "allNum": 40,
     *             "deviceName": "福州连江敖江岱云-勿动超长超长超长超长超长超长超长超长超长",
     *             "deviceCode": "35012200001327000011"
     *         },
     *         {
     *             "allNum": 36,
     *             "deviceName": "铁塔园区-7号楼",
     *             "deviceCode": "12011100001327000017"
     *         },
     *         {
     *             "allNum": 1,
     *             "deviceName": "东亚村-湘阴渔政",
     *             "deviceCode": "43062400831327000001"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数解析：
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		allNum：告警总数
     *
     */
    @PostMapping("/workbench/getIotDeviceAreaDistributionNum")
    TableDataInfo getIotDeviceAreaDistributionNum(@RequestBody StatisticsVO statisticsVO);


    /**
     * 24小时告警趋势
     * @apiNote  24小时告警趋势
     * @apiVersion 7.151
     * @tag 七、统计能力
     * @requestDemo
     *{
     *     "queryStartTime": "2023-11-01 00:00:00",
     *     "queryEndTime": "2024-11-08 23:59:59",
     *     "orderStatusList": [],
     *     "judgeResultList": [],
     *     "warningSourceList": [],
     *     "warningTypeList": [],
     *     "deviceCodeIndustryList": []
     * }
     * @responseDemo
     *{
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717759299933,
     *     "data": [
     *         {
     *             "num": 0,
     *             "label": "0:00-1:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "1:00-2:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "2:00-3:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "3:00-4:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "4:00-5:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "5:00-6:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "6:00-7:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "7:00-8:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "8:00-9:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "9:00-10:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "10:00-11:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "11:00-12:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "12:00-13:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "13:00-14:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "14:00-15:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "15:00-16:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "16:00-17:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "17:00-18:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "18:00-19:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "19:00-20:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "20:00-21:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "21:00-22:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "22:00-23:00"
     *         },
     *         {
     *             "num": 0,
     *             "label": "23:00-24:00"
     *         }
     *     ]
     * }
     * 返回参数解析：
     * 		label：时间段
     * 		num：告警工单数量
     *
     */
    @PostMapping("/workbench/getAlarmEventTrendNumByHour")
    Result getAlarmEventTrendNumByHour(@RequestBody AnalysisVO analysisVO);


    /**
     * 获取事件统计数量小时/日期折线图(含告警等级对应数量)
     * @apiNote 获取事件统计数量小时/日期折线图(含告警等级对应数量)
     * @apiVersion 7.152
     * @tag 七、统计能力
     * @requestDemo
     * {
     *     "queryStartTime": "2025-02-01 00:00:00",
     *     "queryEndTime": "2025-02-21 23:59:59",
     *     "dateType": "1",
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1740128528167,"data":[{"twoLevelNum":4,"num":51,"threeLevelNum":5,"label":"2025-02-21","oneLevelNum":10,"fourLevelNum":32,"fiveLevelNum":0}]}
     *      返回参数data下字段解析：
     *	    label：时间段
     *	    num：告警数量
     *	    oneLevelNum：告警数量(告警等级一级)
     *	    twoLevelNum 告警数量(告警等级二级)
     *	    threeLevelNum 告警数量(告警等级三级)
     *	    fourLevelNum 告警数量(告警等级四级)
     *	    fiveLevelNum 告警数量(告警等级五级)
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventNumLevelList")
    Result getEventNumLevelList(@RequestBody AnalysisVO analysisVO);



    /**
     * 查询设备未处理最高告警等级
     * @apiNote 查询设备未处理最高告警等级
     * @apiVersion 7.153
     * @tag 七、统计能力
     * @requestDemo
     * {
     *     "queryStartTime": "2025-02-01 00:00:00",
     *     "queryEndTime": "2025-02-21 23:59:59",
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1740129167941,"data":[{"alarmLevel":"4","deviceCode":"SK10E50D1306102"},{"alarmLevel":"1","deviceCode":"37010500001327000015+41010200001187000012"},{"alarmLevel":"2","deviceCode":"41010200001327000039"},{"alarmLevel":"2","deviceCode":"11010800001327000075"},{"alarmLevel":"3","deviceCode":"11010800001327000077+21068100001187000003"},{"alarmLevel":"4","deviceCode":"SK10E50D1306101"}]}
     *      返回参数data下字段解析：
     *	    alarmLevel：告警等级
     *	    deviceCode：设备编码
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getDeviceMaxLevelList")
    Result getDeviceMaxLevelList(@RequestBody AnalysisVO analysisVO);


    /**
     * 获取事件统计数量小时/日期折线图(含告警类型对应数量)
     * @apiNote 获取事件统计数量小时/日期折线图(含告警类型对应数量)
     * @apiVersion 7.154
     * @tag 七、统计能力
     * @requestDemo
     * {
     *     "queryStartTime": "2025-02-01 00:00:00",
     *     "queryEndTime": "2025-02-21 23:59:59",
     * }
     * @responseDemo {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1745223569781,
     * 	"data": [
     *                {
     * 			"label": "2025-04-09",
     * 			"totalNum": 7,
     * 			"numByTypeList": [
     *                {
     * 					"typeId": "4110100",
     * 					"typeName": "卫星告警-火点侦测",
     * 					"num": 4
     *                },
     *                {
     * 					"typeId": "1010100",
     * 					"typeName": "烟火侦测（可见光）告警",
     * 					"num": 2
     *                },
     *                {
     * 					"typeId": "1010105",
     * 					"typeName": "扬尘粉尘可视化监控告警",
     * 					"num": 1
     *                }
     * 			]
     *        }
     * 	]
     * }
     *      返回参数data下字段解析：
     *      label：时间段
     *      totalNum：总告警数
     *	    typeId：告警类型Id
     *	    typeName：告警类型名称
     *	    num：告警数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventNumTypeList")
    Result<List<AlarmNumByDateAndTypeVO>> getEventNumTypeList(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询告警事件类型top10
     * @apiNote  查询告警事件类型top10
     * @apiVersion 7.155
     * @tag 七、统计能力
     * @requestDemo
     * {
     *   "queryStartTime": "2023-11-22 00:00:00",
     *   "queryEndTime": "2023-11-28 23:59:59",
     *   "warningSourceList": [],
     *   "warningTypeList": [],
     *   "orderStatusList": [],
     *   "deviceCodeIndustryList": [],
     *   "appVerCode": "1.0"
     * }
     * @responseDemo
     * {
     *   "total": 1,
     *   "rows": [
     *     {
     *       "num": 1,
     *       "typeName": "移动侦测告警告警",
     *       "typeId": "1818100"
     *     }
     *   ],
     *   "code": 200
     * }
     *      返回参数data下字段解析：
     *	    num：告警数量
     *	    typeName：告警类型名称
     *	    typeId：告警类型Id
     */
    @PostMapping("/workbench/getDevTypeYearMonthAppScreen")
    TableDataInfo getDevTypeYearMonthAppScreen(@RequestBody AnalysisVO analysisVO);
}
