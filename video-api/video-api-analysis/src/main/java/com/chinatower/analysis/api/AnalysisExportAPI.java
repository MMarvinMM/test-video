package com.chinatower.analysis.api;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.factory.AnalysisExportFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "AnalysisExportAPI",
        value = ServiceNameConstants.STATICS_SERVICE,
        fallbackFactory = AnalysisExportFallbackFactory.class)
public interface AnalysisExportAPI {

    /**
     * 告警统计总览
     * @apiNote 告警统计总览
     * @tag 七、统计能力
     * @apiVersion 7.7
     * @requestDemo {"tenantId":"","industryCode":"","dimensionFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"]}
     * @responseDemo {"code":200,"msg":"请求成功","time":1648517453712,"data":{"allNum":601,"judgeNum":607,"dispatchNum":0,"handleNum":601,"approveNum":0,
     * "orderCompleteNum":0,"realNum":0,"falseNum":0,"repeatCompleteNum":0,"processNum":0,"acceptanceNum":0,"completionRate":601,"accuracy":0,"suppressNum":101}}
     *      返回参数data下字段解析：
     *      judgeNum：待研判
     * 	    dispatchNum：待调度
     * 	    handleNum：待处置
     * 	    approveNum：待核实
     * 	    orderCompleteNum：已完结数量
     * 	    allNum：告警数量
     * 	    realNum：真警数量
     * 	    repeatCompleteNum：重复告警数量
     * 	    falseNum：误报数量
     * 	    processNum ：处理中数量
     * 	    acceptanceNum：待受理数量
     * 	    accuracy：准确率
     * 	    completionRate：完结率
     * 	    suppressNum：告警压制数量
     *
     * @param statisticsVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getNumAll")
    Result getNumAll(@RequestBody StatisticsVO statisticsVO);

    /**
     * 摄像机告警统计总览
     * @apiNote 摄像机告警统计总览
     * @apiVersion 7.8
     * @tag 七、统计能力
     * @requestDemo {"pageNums":1,"pageSizes":10,"dimensionFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"total":2345,"code":200,"rows":{"allNum":601,"judgeNum":607,"dispatchNum":0,"handleNum":601,"approveNum":0,"orderCompleteNum":0,"realNum":0,"deviceName":"朝阳化工大学食堂+XJ00024",
     * "falseNum":0,"repeatCompleteNum":0,"processNum":0,"acceptanceNum":0,"completionRate":0,"completionRates":0,"accuracy":0,"accuracys":0,"avgNum":101,"suppressNum":13}}
     *      返回参数data下字段解析：
     *      allNum：告警总数量
     * 	    judgeNum：研判数量
     * 	    dispatchNum：调度数量
     * 	    handleNum： 处置数量
     * 	    approveNum：核实数量
     * 	    orderCompleteNum：已完结数量
     * 	    realNum：真警数量
     * 	    deviceName：设备名称
     * 	    falseNum：误报数量
     * 	    repeatCompleteNum ：重复告警数量
     * 	    processNum：处理中数量
     * 	    acceptanceNum：待受理数量
     * 	    completionRate：完结率(保留整数)
     * 	    completionRates：完结率
     * 	    accuracy：准确率(保留整数)
     * 	    accuracys：准确率
     * 	    avgNum：告警平均数量
     * 	    suppressNum：告警压制数量
     *
     * @param statisticsVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getDeviceNumAll")
    TableDataInfo getDeviceNumAll(@RequestBody StatisticsVO statisticsVO);

    /**
     * 告警时间统计-告警数量折线图
     * @apiNote 告警时间统计-告警数量折线图（App）
     * @apiVersion 7.9
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101","townId":"","warningSource": "3",
     * "warningTypeId":"WLGJ-YLGJ","deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"areaCodeLists":[],"orgIdList":[],"radarList":[]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": [{"assignedDate": "2023-07-01","num": 1000},{"assignedDate": "2023-07-02","num": 2000}]}
     *      返回参数data下字段解析：
     *      assignedDate：日期
     * 	    num：告警数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventNumListApp")
    Result getEventNumListApp(@RequestBody AnalysisVO analysisVO);

    /**
     * 获取压制事件统计数量折线图
     * @apiNote 获取压制事件统计数量折线图
     * @apiVersion 7.10
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101","townId":"","warningSource": "3",
     * "warningTypeId":"WLGJ-YLGJ","deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"areaCodeLists":[],"orgIdList":[],"radarList":[]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": [{"assignedDate": "2023-07-01","num": 1000},{"assignedDate": "2023-07-02","num": 2000}]}
     *      返回参数data下字段解析：
     *      assignedDate：日期
     * 	    num：告警数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventInterceptNumApp")
    Result getEventInterceptNumApp(@RequestBody AnalysisVO analysisVO);

    /**
     * 按年或月查询告警事件类型
     * @apiNote 按年或月查询告警事件类型
     * @apiVersion 7.11
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"total":1,"code":200,"rows":[{"typeId": "1010105","typeName": "扬尘粉尘可视化监控告警","accuracy": 50,"num": 10}]}
     *      返回参数data下字段解析：
     *      typeId：告警类型Id
     * 	    typeName：告警类型名称
     * 	    accuracy：准确率
     * 	    num：告警数量
     * @param statisticsVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getDevTypeYearMonthApp")
    TableDataInfo getDevTypeYearMonthApp(@RequestBody StatisticsVO statisticsVO);

    /**
     * 事件高发区域
     * @apiNote 事件高发区域
     * @apiVersion 7.12
     * @tag 七、统计能力
     * @requestDemo {"areaBoxNum":0,"dimensionFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"total":1,"code":200,"rows":[{"provinceId":"110000","cityId":"110100","countyId":"110101","streetId": "","regionName": "其它","accuracy": 66,"num": 10}]}
     *      返回参数data下字段解析：
     *      provinceId：省Id
     *	    cityId：市Id
     *	    countyId：区Id
     *	    streetId：乡镇Id
     *	    regionName：街道名称
     *	    accuracy： 准确率
     *     	num：告警数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEventNumByArea")
    TableDataInfo getEventNumByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询告警来源统计
     * @apiNote 查询告警来源统计
     * 返回参数data下字段解析：
     *      warningSourceName：告警来源名称
     * 	    orderNum：告警事件数量
     *
     * @apiVersion 7.13
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"122035797","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"total":1,"code":200,"rows":[{"warningSource": "1","warningSourceName": "AI告警","accuracy": 50,"num": 10}]}
     *      返回参数data下字段解析：
     *      warningSource：告警来源Id
     *	    warningSourceName：告警来源名称
     *	    accuracy： 准确率
     *     	num：告警数量
     * @param statisticsVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEventAlarmNumSource")
    TableDataInfo getEventAlarmNumSource(@RequestBody StatisticsVO statisticsVO);

    /**
     * 今日事件高发时段24时分布
     * @apiNote 今日事件高发时段24时分布
     * @apiVersion 7.14
     * @tag 七、统计能力
     * @requestDemo {"dimensionFlag":"4","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101","townId":"","warningSource": "3",
     * "warningTypeId":"WLGJ-YLGJ","deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"areaCodeLists":[],"orgIdList":[],"radarList":[]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": [{"label":"0:00-1:00","accuracy": 0,"num": 259},{"label":"1:00-2:00","accuracy": 0,"num": 191}]}
     *      返回参数data下字段解析：
     *      label：时间段
     *	    accuracy：准确率
     *     	num：告警数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventNumByHour")
    Result getEventNumByHour(@RequestBody AnalysisVO analysisVO);

    /**
     * 24h告警压制统计
     * @apiNote 24h告警压制统计
     * @apiVersion 7.15
     * @tag 七、统计能力
     * @requestDemo {"dimensionFlag":"4","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101","townId":"","warningSource": "3",
     * "warningTypeId":"WLGJ-YLGJ","deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"areaCodeLists":[],"orgIdList":[],"radarList":[]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": [{"label":"0:00-1:00","num": 427},{"label":"1:00-2:00","num": 175}]}
     *      返回参数data下字段解析：
     *      label：时间段
     *	    accuracy：准确率
     *     	num：告警压制数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventNumByInterceptHour")
    Result getEventNumByInterceptHour(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警准确率分析
     * @apiNote 告警准确率分析
     * @apiVersion 7.16
     * @tag 七、统计能力
     * @requestDemo {"dimensionFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data":{"allNum":601,"realNum":0,"falseNum":0,"repeatCompleteNum":0,"accuracys":0}}
     *      返回参数data下字段解析：
     *      allNum：告警总数量
     *	    realNum：真警数量
     *     	falseNum：误报数量
     *     	repeatCompleteNum：重复告警数量
     *     	accuracys：准确率
     * @param statisticsVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEvenAccuracy")
    Result getEvenAccuracy(@RequestBody StatisticsVO statisticsVO);

    /**
     * 告警区域准确率排行
     * @apiNote 告警区域准确率排行
     * @apiVersion 7.17
     * @tag 七、统计能力
     * @requestDemo {"areaBoxNum":0,"dimensionFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"total":1,"code":200,"rows":[{"provinceId":"110000","cityId":"110100","countyId":"110101","streetId": "","regionName": "其它","num":601,"realNum":0,"falseNum":0,"repeatCompleteNum":0,"accuracy": 66,"accuracys":0}]}
     *      返回参数data下字段解析：
     *      provinceId：省Id
     *	    cityId：市Id
     *	    countyId：区Id
     *	    streetId：乡镇Id
     *	    regionName：街道名称
     *	    accuracy： 准确率
     *     	num：告警数量
     *	    realNum：真警数量
     *     	falseNum：误报数量
     *     	repeatCompleteNum：重复告警数量
     *     	accuracy：准确率(保留整数)
     *     	accuracys：准确率
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenAreaAccuracy")
    TableDataInfo getEvenAreaAccuracy(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警类型准确率排行
     * @apiNote 告警类型准确率排行
     * @apiVersion 7.18
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"total":1,"code":200,"rows":[{"warningTypeId":"3010101","typeName":"扬尘","num":601,"realNum":0,"falseNum":0,"repeatCompleteNum":0,"accuracy": 66,"accuracys":0}]}
     *      返回参数data下字段解析：
     *      warningTypeId：告警类型Id
     * 	    typeName：告警类型名称
     * 	    num：告警数量
     * 	    realNum：真警数量
     * 	    falseNum：误报数量
     * 	    repeatCompleteNum：重复告警数量
     * 	    accuracy：准确率(保留整数)
     *      accuracys：准确率
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenTypeAccuracy")
    TableDataInfo getEvenTypeAccuracy(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警来源准确率排行
     * @apiNote 告警来源准确率排行
     * @apiVersion 7.19
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"total":1,"code":200,"rows":[{"sourceId":"1","sourceName":"AI告警","num":601,"realNum":0,"falseNum":0,"repeatCompleteNum":0,"accuracy": 66,"accuracys":0}]}
     *      返回参数data下字段解析：
     *      sourceId：告警来源Id
     * 	    sourceName：告警来源名称
     * 	    num：告警数量
     * 	    realNum：真警数量
     * 	    falseNum：误报数量
     * 	    repeatCompleteNum：重复告警数量
     * 	    accuracy：准确率(保留整数)
     *      accuracys：准确率
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenSourceAccuracy")
    TableDataInfo getEvenSourceAccuracy(@RequestBody AnalysisVO analysisVO);

    /**
     * 行政区域完结率统计
     * @apiNote 行政区域完结率统计
     * @apiVersion 7.20
     * @tag 七、统计能力
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
    @PostMapping("/workbench/getEvenAreaCompletion")
    TableDataInfo getEvenAreaCompletion(@RequestBody AnalysisVO analysisVO);

    /**
     * 组织完结率统计
     * @apiNote 组织完结率统计
     * @apiVersion 7.21
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","industryCode":"000000","deptId":"","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"]}
     * @responseDemo {"total":1,"rows":[{"falseNum":0,"acceptanceNum":72,"realNum":1,"deptName":"柚子与芙芙","avgTimeDay":"-","dispatchNum":1,"completionRates":0,"deptId":"120046990-01","repeatCompleteNum":0,"accuracy":100,"completionRate":0,"handleNum":0,"allNum":73,"duration":0,"avgTimes":"0","avgTime":"0","orderCompleteNum":0,"judgeNum":0,"processNum":73,"approveNum":0}],"code":200}
     *      返回参数data下字段解析：
     *      falseNum：误报数量
     *	    acceptanceNum：待受理数量
     *	    realNum：真警数量
     *	    deptName：组织名称
     *	    avgTimeDay：处理时长天时分格式
     *	    dispatchNum：待调度数量
     *	    completionRates： 完结率
     *	    repeatCompleteNum：重复告警数量
     *     	accuracy：准确率(保留整数)
     *     	completionRate：完结率(保留整数)
     *     	handleNum：待处置数量
     *     	allNum：告警总数量
     *     	duration：完结事件总处理时长/秒
     *     	avgTimes：完结事件平均处理时长/分钟
     *     	avgTime：完结事件平均处理时长/分钟(保留整数)
     *     	orderCompleteNum：已完结数量
     *     	judgeNum：研判数量
     *     	processNum：处理中数量
     *     	approveNum：待核实数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenDeptCompletion")
    TableDataInfo getEvenDeptCompletion(@RequestBody AnalysisVO analysisVO);

    /**
     * 用户总览
     * @apiNote 用户总览
     * @apiVersion 7.22
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"tenantName":"客户1","userTotalNum":1596,"startUserNum":1500,"stopUserNum":96},"code": 200}
     * 返回参数data下字段解析：
     *      tenantName: 租户名称
     *      deptNum: 用户下启用组织数量
     *      userTotalNum: 用户总数
     *      startUserNum: 启用用户总数
     *      stopUserNum: 停用用户总数
     * @param customVO 入参
     * @return Result
     */
    @PostMapping("/statistic/getCustomNum")
    Result getCustomNum(@RequestBody CustomVO customVO);

    /**
     * 用户登录时间统计
     * @apiNote 用户登录时间统计
     * @apiVersion 7.23
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 1,"rows": [{"loginTotalCount":812,"loginNum":[{"loginDate":"2023/08/22","loginTypeName":"wx_manage","loginCount":18},
     * {"loginDate":"2023/08/22","loginTypeName":"app_manage","loginCount":114},{"loginDate":"2023/08/22","loginTypeName":"web_manage","loginCount":680}]}],"code": 200}
     * 返回参数data下字段解析：
     *      loginTotalCount: 登录总次数
     *      loginNum: [{
     *          loginDate: 登录时间
     *          loginTypeName: 登录类型名称（pc端/APP客户端/小程序公众号）
     *          loginCount: 登录次数
     *      }]
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getCustomNumByLoginTime")
    TableDataInfo getCustomNumByLoginTime(@RequestBody CustomVO customVO);

    /**
     * 用户登录次数排行
     * @apiNote 用户登录次数排行
     * @apiVersion 7.24
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 333,"rows": [{"loginName":"张三","loginCount":45}，{"loginName":"李四","loginCount":11}],"code": 200}
     * 返回参数rows下字段解析：
     *      loginUserName: 登录用户
     *      tenantName: 租户名称
     *      loginName: 登录用户名称
     *      tenantId: 租户编码
     *      userId: 用户id
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginNumRank")
    TableDataInfo getLoginNumRank(@RequestBody CustomVO customVO);

    /**
     * 用户登录时长排行
     * @apiNote 用户登录时长排行
     * @apiVersion 7.25
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 3895,"rows": [{"loginDuration":"1天12时0分","loginName":"张三","onlineTime":129600}],"code": 200}
     * 返回参数rows下字段解析：
     *      loginName: 登录人
     *      tenantId: 租户编码
     *      onlineTime: 在线时长，秒
     *      loginDuration: 登录时长
     *      userId: 用户id
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginDurationRank")
    TableDataInfo getLoginDurationRank(@RequestBody CustomVO customVO);

    /**
     * 用户登录24小时分布统计
     * @apiNote 用户登录24小时分布统计
     * @apiVersion 7.26
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 1,"rows": [{"loginTimeSlot":"0:00-1:00","loginCount":0},{"loginTimeSlot":"1:00-2:00","loginCount":0}],"code": 200}
     * 返回参数rows下字段解析：
     *      loginTimeSlot: 时间段
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginNumByHour")
    TableDataInfo getLoginNumByHour(@RequestBody CustomVO customVO);

    /**
     * PC操作系统统计
     * @apiNote PC操作系统统计
     * @apiVersion 7.27
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 1,"rows": [{"pcOsName":"Windows 10","loginCount":343},{"pcOsName":"Windows 11","loginCount":189}],"code": 200}
     * 返回参数rows下字段解析：
     *      pcOsName: PC操作系统名称
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginTypeByPcOs")
    TableDataInfo getLoginTypeByPcOs(@RequestBody CustomVO customVO);

    /**
     * PC浏览器排行
     * @apiNote PC浏览器排行
     * @apiVersion 7.28
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 9,"rows": [{"browserName":"Chrome 11","loginCount":381},{"browserName":"Chrome 10","loginCount":195}],"code": 200}
     * 返回参数rows下字段解析：
     *      browserName: PC浏览器名称
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginTypeByPcBrower")
    TableDataInfo getLoginTypeByPcBrower(@RequestBody CustomVO customVO);

    /**
     * APP操作系统统计
     * @apiNote APP操作系统统计
     * @apiVersion 7.29
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 1,"rows": [{"appOsName":"Android","loginCount":82},{"appOsName":"iOS","loginCount":20}],"code": 200}
     * 返回参数rows下字段解析：
     *      appOsName: APP操作系统名称
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginTypeByAppOs")
    TableDataInfo getLoginTypeByAppOs(@RequestBody CustomVO customVO);

    /**
     * APP手机型号排行
     * @apiNote APP手机型号排行
     * @apiVersion 7.30
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 38,"rows": [{"phoneModelName":"iPhone 11","loginCount":7},{"phoneModelName":"PDEM30","loginCount":7}],"code": 200}
     * 返回参数rows下字段解析：
     *      phoneModelName: APP手机型号名称
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginTypeByPhoneModel")
    TableDataInfo getLoginTypeByPhoneModel(@RequestBody CustomVO customVO);

    /**
     * APP版本排行
     * @apiNote APP版本排行
     * @apiVersion 7.31
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 5,"rows": [{"versionName":"2.0.90","loginCount":63},{"versionName":"2.0.70","loginCount":39}],"code": 200}
     * 返回参数rows下字段解析：
     *      versionName: APP版本名称
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginTypeByAppVersion")
    TableDataInfo getLoginTypeByAppVersion(@RequestBody CustomVO customVO);

    /**
     * 登录成功率分析
     * @apiNote 登录成功率分析
     * @apiVersion 7.32
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 3,"rows": [{"loginStatus":"0","loginCount":508},{"loginStatus":"1","loginCount":304},{"successRate":"63%"}],"code": 200}
     * 返回参数rows下字段解析：
     *      successRate: 登录成功率
     *      loginStatus: 登录状态（0-成功、1-失败）
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginSuccessRate")
    TableDataInfo getLoginSuccessRate(@RequestBody CustomVO customVO);

    /**
     * 登录失败原因分析
     * @apiNote 登录失败原因分析
     * @apiVersion 7.33
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 9,"rows": [{"loginCause":"账号或密码错误，4次之后账号将被锁定30分钟","failCauseNum":114,"failRatio":"37.50%"},
     * {"loginCause":"短信验证码错误","failCauseNum":62,"failRatio":"20.39%"}],"code": 200}
     * 返回参数rows下字段解析：
     *      loginCause: 登录失败原因
     *      failCauseNum: 登录失败数量
     *      failRatio: 登录失败率
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginFailureRate")
    TableDataInfo getLoginFailureRate(@RequestBody CustomVO customVO);

    /**
     * 统计监控设备数量
     * @apiNote 统计监控设备数量
     * @apiVersion 7.34
     * @tag 七、统计能力
     * @requestDemo {"tenantId":""}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"alarmLocationDevNum":"2281","channelCountOffline":32547,"channelCountOnline":34087,"channelCountTotal":66634,
     * "devCountOffline":23113,"devCountOnline":30149,"deviceCountTotal":53262,"disAlgorithmTaskNum":78379,"onlinePercentage":"0.51","onlinePercentageDev":"0.57","orderDeviceNum":"16218","upAlgorithmTaskNum":70118},"code": 200}
     *      返回参数data下字段解析：
     *      alarmLocationDevNum：开启告警定位摄像机数量
     *	    channelCountOffline：通道离线数
     *	    channelCountOnline：通道在线数
     *	    channelCountTotal：通道总数
     *	    devCountOffline：摄像机离线数
     *	    devCountOnline： 摄像机在线数
     *     	deviceCountTotal：摄像机总数
     *	    disAlgorithmTaskNum：通道配置算法任务数量
     *     	onlinePercentage：通道在线率
     *     	onlinePercentageDev：设备在线率
     *     	orderDeviceNum：订购摄像机数量
     *     	upAlgorithmTaskNum：通道启用算法任务数量
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getDeviceStatistics")
    Result<DeviceStatisticsVO> getDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 摄像机数量区域排行(租户)
     * @apiNote 摄像机数量区域排行(租户)
     * @apiVersion 7.35
     * @tag 七、统计能力
     * @requestDemo {"areaBoxNum":"0","pageNum":1,"pageSize":5,"tenantId":"122035797"}
     * @responseDemo {"total": 31,"rows": [{"regionName":"湖南省","deviceNum":12563},{"regionName":"云南省","deviceNum":5332}],"code": 200}
     *      返回参数data下字段解析：
     *      regionName：设备区域名称
     *	    deviceNum：设备数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/device/getCameraNumByArea")
    TableDataInfo getCameraNumByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 摄像机订购算法排行
     * @apiNote 摄像机订购算法排行
     * @apiVersion 7.36
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"tenantId":""}
     * @responseDemo {"total": 10436,"rows": [{"deviceCode":"51052100831327000001","deviceName":"泸县奇峰镇人民政府(大华)","algorithmNum":28},
     * {"deviceCode":"21080400831327000009","algorithmNum":25,"deviceName":"营口金沙滩假日宾馆南资源点"}],"code": 200}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    deviceName：设备名称
     *	    algorithmNum：订购算法数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/device/getOrderAlgorithmRank")
    TableDataInfo getOrderAlgorithmRank(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询物联告警统计总览
     * @apiNote 查询物联告警统计总览
     * @apiVersion 7.37
     * @tag 七、统计能力
     * @requestDemo {
     * "queryStartTime":"2024-09-18 00:00:00",
     * "queryEndTime":"2024-09-18 23:59:59",
     * }
     * @responseDemo {"total":1,"rows":[{"todayAll":0,"todayProcess":0,"todayComplete":0,"totalProcess":0,"totalComplete":0,"penddingJudged":0,"pendingDispatched":0,"peddingDisposal":0,"pendingApprove":0,"penddingLeaderApprove":0,"overtime":0,"judgeNum":11196,"dispatchNum":2,"handleNum":0,"approveNum":1,"orderCompleteNum":0,"allNum":11206,"realNum":10,"repeatCompleteNum":0,"falseNum":0,"processNum":11206,"accuracy":"100","completionRate":"0","accuracys":"100.0000","completionRates":"0.0000","suppressNum":0,"acceptanceNum":7,"avgNum":1401,"deviceName":"城三5G海淀永定路70号院515号楼1ZNR19031652","deviceCode":"rain-frgd-9596-01"}],"code":200}
     *      返回参数data下字段解析：
     *      judgeNum：研判数量
     *	    dispatchNum：待调度数量
     *     	handleNum：待处置数量
     *     	approveNum：待核实数量
     *     	orderCompleteNum：已完结数量
     *     	allNum：告警总数量
     *	    realNum：真警数量
     *	    repeatCompleteNum：重复告警数量
     *      falseNum：误报数量
     *     	processNum：处理中数量
     *     	accuracy：准确率(保留整数)
     *     	completionRate：完结率(保留整数)
     *     	accuracys：准确率
     *	    completionRates： 完结率
     * 	    suppressNum：告警压制数量
     *	    acceptanceNum：待受理数量
     *	    avgNum：日均告警数量
     *     	deviceName：设备名称
     *     	deviceCode：设备编码
     *
     * @param statisticsVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getIotDeviceNumAll")
    TableDataInfo getIotDeviceNumAll(@RequestBody StatisticsVO statisticsVO);

    /**
     * 设备-物联设备总览
     * @apiNote 设备-物联设备总览
     * @apiVersion 7.38
     * @tag 七、统计能力
     * @requestDemo {
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115"
     *   ]
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1735542517649,"data":{"iotDeviceCount":17,"iotDeviceOnlineCount":17,"iotDeviceOfflineCount":0,"iotDeviceOnlinePercent":"1.00"}}
     *      返回参数data下字段解析：
     *      iotDeviceCount：物联设备总数
     *	    iotDeviceOfflineCount：物联设备离线数
     *     	iotDeviceOnlineCount：物联设备在线数
     *     	iotDeviceOnlinePercent：物联设备在线率
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("workbench/getIotDeviceStatistics")
    Result<IotDeviceStatisticsVo> getIotDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 设备-无人机设备总览
     * @apiNote 设备-无人机设备总览
     * @apiVersion 7.39
     * @tag 七、统计能力
     * @requestDemo {
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115"
     *   ]
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1699602135828,"data":{"uavDeviceCount":1,"uavDeviceOnlineCount":0,"uavDeviceOfflineCount":1,"uavDeviceOnlinePercent":"0.00","uavChannelCount":1,"uavChannelOnlineCount":0,"uavChannelOnlinePercent":"0.00","uavChannelOfflineCount":1}}
     *      返回参数data下字段解析：
     *      uavDeviceCount：无人机设备总数
     *	    uavDeviceOnlineCount：无人机设备在线数
     *     	uavDeviceOfflineCount：无人机设备离线数
     *     	uavDeviceOnlinePercent：无人机设备在线率
     *      uavChannelCount：无人机通道总数
     *	    uavChannelOnlineCount：无人机通道在线数
     *     	uavChannelOfflineCount：无人机通道离线数
     *     	uavChannelOnlinePercent：无人机通道在线率
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("workbench/getUavDeviceStatistics")
    Result<UavDeviceStatisticsVo> getUavDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 设备-雷达设备总览
     * @apiNote 设备-雷达设备总览
     * @apiVersion 7.40
     * @tag 七、统计能力
     * @requestDemo {
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115"
     *   ]
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1699602300025,"data":{"radarDeviceCount":5,"radarDeviceOnlineCount":5,"radarDeviceOfflineCount":0,"radarDeviceOnlinePercent":"1.00"}}
     *      返回参数data下字段解析：
     *      radarDeviceCount：雷达设备总数
     *	    radarDeviceOnlineCount：雷达设备在线数
     *     	radarDeviceOfflineCount：雷达设备离线数
     *     	radarDeviceOnlinePercent：雷达设备在线率
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("workbench/getRadarDeviceStatistics")
    Result<RadarDeviceStatisticsVo> getRadarDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 设备-大喇叭设备总览
     * @apiNote 设备-大喇叭设备总览
     * @apiVersion 7.41
     * @tag 七、统计能力
     * @requestDemo {
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115"
     *   ]
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1699602374469,"data":{"hornDeviceCount":1,"hornDeviceWorkCount":0,"hornDeviceOfflineCount":1,"hornDeviceFreeCount":0,"hornDeviceFailCount":0,"hornDeviceNoConnectCount":0,"hornDeviceOnlineCount":0,"hornDeviceOnlinePercent":"0.00"}}
     *      返回参数data下字段解析：
     *      hornDeviceCount：喇叭数量
     *	    hornDeviceWorkCount：喇叭工作数量
     *     	hornDeviceOfflineCount：喇叭离线数量
     *     	hornDeviceFreeCount：喇叭空闲数量
     *     	hornDeviceFailCount：喇叭故障数量
     *     	hornDeviceNoConnectCount：喇叭从未连接数
     *	    hornDeviceOnlineCount：喇叭在线数
     *	    hornDeviceOnlinePercent：喇叭在线率
     *
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("workbench/getHornDeviceStatistics")
    Result<HornDeviceStatisticsVo> getHornDeviceStatistics(@RequestBody AnalysisVO analysisVO);


    /**
     * 查询物联设备数量统计区域排行
     * @apiNote 查询物联设备数量统计区域排行
     * @apiVersion 7.42
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":4,"rows":[{"deviceNum":1,"regionName":"北京市北京市海淀区永定路街道"},{"deviceNum":1,"regionName":"北京市北京市海淀区田村路街道"},{"deviceNum":1,"regionName":"北京市北京市海淀区四季青镇"},{"deviceNum":1,"regionName":"辽宁省大连市甘井子区其他"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    regionName：设备区域名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getIotDeviceStatisticsByArea")
    TableDataInfo getIotDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询无人机设备数量统计区域排行
     * @apiNote 查询无人机设备数量统计区域排行
     * @apiVersion 7.43
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":1,"rows":[{"deviceNum":1,"regionName":"四川省宜宾市翠屏区沙坪街道"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    regionName：设备区域名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getUavDeviceStatisticsByArea")
    TableDataInfo getUavDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询雷达设备数量统计区域排行
     * @apiNote 查询雷达设备数量统计区域排行
     * @apiVersion 7.44
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":5,"rows":[{"deviceNum":1,"regionName":"北京市北京市西城区其他"},{"deviceNum":1,"regionName":"北京市北京市朝阳区其他"},{"deviceNum":1,"regionName":"北京市北京市西城区展览路街道"},{"deviceNum":1,"regionName":"北京市北京市朝阳区十八里店乡"},{"deviceNum":1,"regionName":"辽宁省大连市甘井子区其他"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    regionName：设备区域名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getRadarDeviceStatisticsByArea")
    TableDataInfo getRadarDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询大喇叭设备数量统计区域排行
     * @apiNote 查询大喇叭设备数量统计区域排行
     * @apiVersion 7.45
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":1,"rows":[{"deviceNum":1,"regionName":"北京市北京市海淀区"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    regionName：设备区域名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getHornDeviceStatisticsByArea")
    TableDataInfo getHornDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 用户数量组织排行
     * @apiNote 用户数量组织排行
     * @apiVersion 7.46
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":17,"rows":[{"userNum":60,"deptName":"测试客户0010-通用平台版本-1.0","deptId":"120046990-01"},{"userNum":6,"deptName":"雷达北京市","deptId":"120046990-0112"},{"userNum":3,"deptName":"测试张","deptId":"120046990-0114"},{"userNum":3,"deptName":"摄像机西城区","deptId":"120046990-0102"},{"userNum":3,"deptName":"摄像机北京市","deptId":"120046990-0103"}],"code":200}
     * 返回参数rows下字段解析：
     *      userNum: 用户数
     *      deptName: 部门名称
     *      deptId: 部门id
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getCustomNumByDept")
    TableDataInfo getCustomNumByDept(@RequestBody CustomVO customVO);


    /**
     * 各区县告警情况:用户所在区域告警类型数量和处理数量
     * @apiNote 各区县告警情况:用户所在区域告警类型数量和处理数量
     * @apiVersion 7.47
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":""}
     * @responseDemo {
     * 	"total": 1,
     * 	"rows": [
     *                {
     * 			"countyId": "511502",
     * 			"countyName": "翠屏区",
     * 			"alarms": [
     *                {
     * 					"num": "1734",
     * 					"processedNum": "7",
     * 					"warningTypeId": "1313100",
     * 					"warningTypeName": "车辆闯入监控告警"
     *                }
     * 			]
     *        }
     * 	],
     * 	"code": 200
     * }
     * 返回参数rows下字段解析：
     *      num: 告警数量
     *      processedNum: 处理中数量
     *      warningTypeId: 告警类型Id
     *      warningTypeName: 告警类型名称
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEventNumByAreaAndType")
    TableDataInfo getEventNumByAreaAndType(@RequestBody AnalysisVO analysisVO);

    /**
     * 网格完结率统计
     * @apiNote 网格完结率统计
     * @apiVersion 7.48
     * @tag 七、统计能力
     * @requestDemo {
     * "queryEndTime":"2024-03-01 00:00:00",
     * "queryStartTime":"2024-02-01 00:00:00",
     * "judgeResultList":[],
     * "gridIdList":[],
     * "pageSizes":1,
     * "pageNums":5,
     * }
     * @responseDemo {
     * 	"total": 35,
     * 	"rows": [
     *                {
     * 			"acceptanceNum": 0,
     * 			"falseNum": 0,
     * 			"realNum": 1,
     * 			"avgTimeDay": "23时7分",
     * 			"gridName": "旅顺口区",
     * 			"dispatchNum": 0,
     * 			"completionRates": 100,
     * 			"repeatCompleteNum": 0,
     * 			"accuracy": 100,
     * 			"completionRate": 100,
     * 			"handleNum": 0,
     * 			"allNum": 1,
     * 			"duration": 83245,
     * 			"avgTimes": 1387.4166666666667,
     * 			"avgTime": 1387,
     * 			"orderCompleteNum": 1,
     * 			"gridId": "202303281741061812",
     * 			"judgeNum": 0,
     * 			"accuracys": 100,
     * 			"gridLevel": "3",
     * 			"processNum": 0,
     * 			"approveNum": 0
     *        }
     * 	],
     * 	"code": 200
     * }
     *返回参数data下字段解析：
     *	    acceptanceNum：待受理数量
     *      falseNum：误报数量
     *	    realNum：真警数量
     *	    avgTimeDay：处理时长天时分格式
     *	    gridName：网格名称
     *	    dispatchNum：待调度数量
     *	    completionRates： 完结率
     *	    repeatCompleteNum：重复告警数量
     *     	accuracy：准确率(保留整数)
     *     	completionRate：完结率(保留整数)
     *     	handleNum：待处置数量
     *     	allNum：告警总数量
     *     	duration：完结事件总处理时长/秒
     *     	avgTimes：完结事件平均处理时长/分钟
     *     	avgTime：完结事件平均处理时长/分钟(保留整数)
     *     	orderCompleteNum：已完结数量
     * 		gridId: 网格Id
     *     	judgeNum：研判数量
     *   	accuracys: 准确率
     * 	    gridLevel: 网格等级
     *     	processNum：处理中数量
     *     	approveNum：待核实数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenGridCompletion")
    TableDataInfo getEvenGridCompletion(@RequestBody AnalysisVO analysisVO);

    /**
     * 通道启用后端算法任务排行
     * @apiNote 通道启用后端算法任务排行
     * @apiVersion 7.49
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"tenantId":""}
     * @responseDemo {
     *   "total": 5,
     *   "rows": [
     *     {
     *       "startAfterAlgorithmTaskNum": 9,
     *       "channelName": "铁塔产业园-11号楼-大华243_0",
     *       "deviceCode": "11010800831327000008"
     *     },
     *     {
     *       "startAfterAlgorithmTaskNum": 3,
     *       "channelName": "dasd",
     *       "deviceCode": "12345678909879870987-00000000832127000018"
     *     },
     *     {
     *       "startAfterAlgorithmTaskNum": 2,
     *       "channelName": "铁塔产业园-12号楼角落-大华245_0",
     *       "deviceCode": "11010800831327000003"
     *     },
     *     {
     *       "startAfterAlgorithmTaskNum": 1,
     *       "channelName": "海康3公里热成像_0",
     *       "deviceCode": "51150200831327000261"
     *     },
     *     {
     *       "startAfterAlgorithmTaskNum": 1,
     *       "channelName": "四川-宜宾-南溪区金鸡村二站_0",
     *       "deviceCode": "51150300991327000001"
     *     }
     *   ],
     *   "code": 200
     * }
     *返回参数data下字段解析：
     *	    startAfterAlgorithmTaskNum：通道启用的后端算法任务数量
     *      channelName：通道名称
     *	    deviceCode：设备编码
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/device/getStartAfterAlgorithmTaskRank")
    TableDataInfo getStartAfterAlgorithmTaskRank(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询执法记录仪设备数量统计区域排行
     * @apiNote 查询大喇叭设备数量统计区域排行
     * @apiVersion 7.50
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":1,"rows":[{"deviceNum":1,"regionName":"北京市北京市海淀区"}],"code":200}
     *返回参数data下字段解析：
     *	    deviceNum：设备名称
     *      regionName：设备区域名称
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getRecorderDeviceStatisticsByArea")
    TableDataInfo getRecorderDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 设备-执法记录仪设备总览
     * @apiNote 设备-无人机设备总览
     * @apiVersion 7.51
     * @tag 七、统计能力
     * @requestDemo {
     *   "orgIdList": [
     *     "120046990-01"
     *   ],
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ]
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1699602135828,"data":{"deviceCountTotal":1,"devCountOnline":0,"devCountOffline":1,"onlinePercentage":"0.00","channelCountTotal":1,"channelCountOnline":0,"onlinePercentage":"0.00","channelCountOffline":1}}
     *      返回参数data下字段解析：
     *     	deviceCountTotal：执法记录仪总数
     *	    devCountOnline： 执法记录仪在线数
     *	    devCountOffline：执法记录仪离线数
     *	    channelCountTotal：通道总数
     *	    channelCountOnline：通道在线数
     *	    channelCountOffline：通道离线数
     *     	onlinePercentage：设备在线率
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getRecorderDeviceStatistics")
    Result<DeviceStatisticsVO> getRecorderDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询告警数量根据告警标签分类
     * @apiNote 查询告警数量根据告警标签分类
     * @apiVersion 7.52
     * @tag 七、统计能力
     * @requestDemo {
     * "queryStartTime":"2024-09-18 00:00:00",
     * "queryEndTime":"2024-09-18 23:59:59"
     * }
     * @responseDemo {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1721377951468,
     * 	"data": [
     *                {
     * 			"tagId": "1188128",
     * 			"num": 8,
     * 			"tagName": "12344"
     *        },
     *        {
     * 			"tagId": "1188393",
     * 			"num": 6,
     * 			"tagName": "迈克尔乔丹"
     *        },
     *        {
     * 			"tagId": "1188124",
     * 			"num": 5,
     * 			"tagName": "侧围是"
     *        },
     *        {
     * 			"tagId": "1188121",
     * 			"num": 5,
     * 			"tagName": "测试更改"
     *        },
     *        {
     * 			"tagId": "1188115",
     * 			"num": 5,
     * 			"tagName": "测试"
     *        },
     *        {
     * 			"tagId": "aaa666",
     * 			"num": 27,
     * 			"tagName": "其他"
     *        }
     * 	]
     * }
     *      返回参数data下字段解析：
     *     	tagId：标签Id
     *	    num： 告警视联
     *	    tagName：标签名称
     * @param statisticsVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getAlarmTagYearMonth")
    Result getAlarmTagYearMonth(@RequestBody StatisticsVO statisticsVO);
}
