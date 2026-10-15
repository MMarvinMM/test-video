package com.chinatower.analysis.api;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.factory.AnalysisExportTTFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "AnalysisExportTTAPI",
        value = ServiceNameConstants.STATICS_TT_SERVICE,
        fallbackFactory = AnalysisExportTTFallbackFactory.class)
public interface AnalysisExportTTAPI {

    /**
     * 告警统计总览（铁塔视角）
     * @apiNote 告警统计总览-tt
     * @apiVersion 7.65
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","industryCode":"","dimensionFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"]}
     * @responseDemo {"code":200,"msg":"请求成功","time":1648517453712,"data":{"allNum":601,"judgeNum":607,"dispatchNum":0,"handleNum":601,"approveNum":0,
     * "orderCompleteNum":0,"realNum":0,"falseNum":0,"repeatCompleteNum":0,"processNum":0,"acceptanceNum":0,"completionRate":601,"accuracy":0,"suppressNum":101}}
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
     * @return Result
     */
    @PostMapping("/workbench/getNumAll")
    Result getNumAll(@RequestBody StatisticsVO statisticsVO);

    /**
     * 摄像机告警统计总览
     * @apiNote 摄像机告警统计总览-tt
     * @apiVersion 7.66
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
     * @apiNote 告警时间统计-告警数量折线图-tt（App）
     * @apiVersion 7.67
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
     * @apiNote 获取压制事件统计数量折线图-tt
     * @apiVersion 7.68
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
     * @apiNote 按年或月查询告警事件类型-tt
     * @apiVersion 7.69
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
     * @apiNote 事件高发区域-tt
     * @apiVersion 7.70
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
     * @apiNote 查询告警来源统计-tt
     * @apiVersion 7.71
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
     * @apiNote 今日事件高发时段24时分布-tt
     * @apiVersion 7.72
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
     * @apiNote 24h告警压制统计-tt
     * @apiVersion 7.73
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
     * 获取摄像机下事件统计数量
     * @apiNote 获取摄像机下事件统计数量-tt
     * @apiVersion 7.74
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-04","queryEndTime":"2023-08-09","provinceId":"110000","cityId":"110100","countyId":"110101",
     * "townId":"","areaCodeLists":[],"orgIdList":[],"radarList":[],"deviceCodeList":["rain-yicc-8297-04","water-bhmz-2605-02"],"deviceCode":""}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data":[{"deviceCode":"43098100831327000271","deviceName":"沅江市龙虎山林场路口XJ","num":6}]}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    deviceName：设备名称
     *     	num：告警压制数量
     * @param statisticsVO 入参
     * @return Result
     */
    @PostMapping("/workbench/getEventNumDevApp")
    TableDataInfo getEventNumDevApp(@RequestBody StatisticsVO statisticsVO);

    /**
     * 告警准确率分析
     * @apiNote 告警准确率分析-tt
     * @apiVersion 7.75
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
     * @apiNote 告警区域准确率排行-tt
     * @apiVersion 7.76
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
     * @apiNote 告警类型准确率排行-tt
     * @apiVersion 7.77
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
     * @apiNote 告警来源准确率排行-tt
     * @apiVersion 7.78
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
     * 告警准确率按客户排行
     * @apiNote 告警准确率按客户排行-tt
     * @apiVersion 7.79
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59","tenantId":"","pageNum":1,"pageSize":5}
     * @responseDemo {"total":369,"code":200,"rows":[{"tenantId":"123028066","tenantName":"朝阳市自然资源局","allNum":55,"realNum":1,"falseNum":0,"repeatCompleteNum":0,"accuracy": 100,"accuracys":100}]}
     *      返回参数data下字段解析：
     *      tenantId：租户Id
     * 	    tenantName：租户名称
     * 	    allNum：告警数量
     * 	    realNum：真警数量
     * 	    falseNum：误报数量
     * 	    repeatCompleteNum：重复告警数量
     * 	    accuracy：准确率(保留整数)
     *      accuracys：准确率
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenAccuracyByTenant")
    TableDataInfo getEventAccuracyByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警完结率按客户排行
     * @apiNote 告警完结率按客户排行-tt
     * @apiVersion 7.80
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59","tenantId":"","pageNum":1,"pageSize":5}
     * @responseDemo {"total":369,"code":200,"rows":[{"tenantId":"121123982","tenantName":"大埔县三河镇人民政府","allNum":1,"orderCompleteNum":1,"completionRate": 100,"completionRates":100}]}
     *      返回参数data下字段解析：
     *      tenantId：租户Id
     * 	    tenantName：租户名称
     * 	    allNum：告警数量
     * 	    orderCompleteNum：真警数量
     * 	    completionRate：准确率(保留整数)
     *      completionRates：准确率
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEvenCompletionByTenant")
    TableDataInfo getEvenCompletionByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 告警压制按客户排行
     * @apiNote 告警压制按客户排行-tt
     * @apiVersion 7.81
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59","tenantId":"","pageNum":1,"pageSize":5}
     * @responseDemo {"total":350,"code":200,"rows":[{"tenantId":"121123982","tenantName":"大埔县三河镇人民政府","num":49021}]}
     *      返回参数data下字段解析：
     *      tenantId：租户Id
     * 	    tenantName：租户名称
     * 	    num：告警压制数量
     * @param statisticsVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getEventInterceptNumTenant")
    TableDataInfo getEventInterceptNumTenant(@RequestBody StatisticsVO statisticsVO);

    /**
     * 用户总览
     * @apiNote 用户总览-tt
     * @apiVersion 7.82
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": {"tenantTotalNum":"2731","userTotalNum":12407,"validTenantNum":1135,
     * "expireTenantNum":1596,"startUserNum":12332,"stopUserNum":75},"code": 200}
     *  返回参数data下字段解析：
     *      validTenantNum: 有效客户数
     *      userTotalNum: 客户用户总数
     *      tenantTotalNum: 客户总数
     *      startUserNum: 启用用户数
     *      expireTenantNum: 失效客户数
     *      stopUserNum: 停用用户数
     * @param customVO 入参
     * @return Result
     */
    @PostMapping("/statistic/getCustomNum")
    Result getCustomNum(@RequestBody CustomVO customVO);

    /**
     * 客户用户数量排行
     * @apiNote 客户用户数量排行-tt
     * @apiVersion 7.83
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"rows": [{"tenantId":"122098991","tenantName":"怀化市林业局","userNum":828}],"code": 200}
     * 返回参数rows下字段解析：
     *      tenantId: 租户id
     *      tenantName: 租户名称
     *      userNum: 用户数
     * @param customVO 入参
     * @return Result
     */
    @PostMapping("/statistic/getCustomUserRank")
    TableDataInfo getCustomUserRank(@RequestBody CustomVO customVO);

    /**
     * 用户登录时间统计
     * @apiNote 用户登录时间统计-tt
     * @apiVersion 7.84
     * @tag 七、统计能力
     * @requestDemo {"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 1,"rows": [{"loginTotalCount":812,"loginNum":[{"loginDate":"2023/08/22","loginTypeName":"wx_manage","loginCount":18},
     * {"loginDate":"2023/08/22","loginTypeName":"app_manage","loginCount":114},{"loginDate":"2023/08/22","loginTypeName":"web_manage","loginCount":680}]}],"code": 200}
     * 返回参数rows下字段解析：
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
     * @apiNote 用户登录次数排行-tt
     * @apiVersion 7.85
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
     * @apiNote 用户登录时长排行-tt
     * @apiVersion 7.86
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
     * 用户登录次数按客户排行
     * @apiNote 用户登录次数按客户排行-tt
     * @apiVersion 7.87
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 187,"rows": [{"tenantId":"122098991","tenantName":"怀化市林业局","loginCount":45}，{"tenantId":"121012137","tenantName":"岳阳北晟慧华信息技术有限公司","loginCount":46}],"code": 200}
     * 返回参数rows下字段解析：
     *      tenantId: 租户编码
     *      tenantName: 租户名称
     *      loginCount: 登录次数
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginNumRankByTenant")
    TableDataInfo getLoginNumRankByTenant(@RequestBody CustomVO customVO);

    /**
     * 用户登录24小时分布统计
     * @apiNote 用户登录24小时分布统计-tt
     * @apiVersion 7.88
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
     * @apiNote PC操作系统统计-tt
     * @apiVersion 7.89
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
     * @apiNote PC浏览器排行-tt
     * @apiVersion 7.90
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
     * @apiNote APP操作系统统计-tt
     * @apiVersion 7.91
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
     * @apiNote APP手机型号排行-tt
     * @apiVersion 7.92
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
     * @apiNote APP版本排行-tt
     * @apiVersion 7.93
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
     * @apiNote 登录成功率分析-tt
     * @apiVersion 7.94
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
     * @apiNote 登录失败原因分析-tt
     * @apiVersion 7.95
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
     * 登录成功率按客户排行
     * @apiNote 登录成功率按客户排行-tt
     * @apiVersion 7.96
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"queryStartTime":"2023-08-22 00:00:00","queryEndTime":"2023-08-22 23:59:59"}
     * @responseDemo {"total": 5,"rows": [{"tenantId":"123075868","tenantName":"江西省自然资源执法局","loginCount":5,"successRate":"100%"},
     * {"tenantId":"123029104","tenantName":"桂林市水利局","loginCount":1,"successRate":"100%"}],"code": 200}
     * 返回参数rows下字段解析：
     *      tenantId: 租户编码
     *      tenantName: 租户名称
     *      loginCount: 登录次数
     *      successRate: 登录成功率
     * @param customVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/statistic/getLoginSuccessByTenant")
    TableDataInfo getLoginSuccessByTenant(@RequestBody CustomVO customVO);

    /**
     * 统计监控设备数量
     * @apiNote 统计监控设备数量-tt
     * @apiVersion 7.97
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
     * @apiNote 摄像机数量区域排行(租户)-tt
     * @apiVersion 7.98
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
     * @apiNote 摄像机订购算法排行-tt
     * @apiVersion 7.99
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
     * 客户订购摄像机排行(铁塔)
     * @apiNote 客户订购摄像机排行(铁塔)
     * @apiVersion 7.100
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"tenantId":""}
     * @responseDemo {"total": 10436,"rows": [{"tenantId":"123046734","tenantName":"应急管理部","deviceNum":2467},
     * {"tenantId":"122098991","tenantName":"怀化市林业局","deviceNum":1010}],"code": 200}
     *      返回参数data下字段解析：
     *      tenantId：租户Id
     *	    tenantName：租户名称
     *	    deviceNum：设备数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/device/getOrderDeviceByTenant")
    TableDataInfo getOrderDeviceByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 摄像机订购按客户数量排行(铁塔)
     * @apiNote 摄像机订购按客户数量排行(铁塔)
     * @apiVersion 7.101
     * @tag 七、统计能力
     * @requestDemo {"pageNum":1,"pageSize":5,"tenantId":""}
     * @responseDemo {"total": 10436,"rows": [{"deviceCode":"43098100831327000001","device_name":"桃江县龙溪无线机房","tenantNum":5},
     * {"deviceCode":"43098100831327000087","device_name":"东湖脑","deviceNum":5}],"code": 200}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    device_name：设备名称
     *	    deviceNum：设备数量
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/device/getTenantOrderDeviceRank")
    TableDataInfo getTenantOrderDeviceRank(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询物联告警统计总览
     * @apiNote 查询物联告警统计总览
     * @apiVersion 7.102
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
     * @apiVersion 7.103
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115"
     *   ]
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1699601961120,"data":{"hornDeviceCount":1,"hornDeviceWorkCount":0,"hornDeviceOfflineCount":1,"hornDeviceFreeCount":0,"hornDeviceFailCount":0,"hornDeviceNoConnectCount":0,"hornDeviceOnlineCount":0,"hornDeviceOnlinePercent":"0.00"}}
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
     * @apiVersion 7.104
     * @tag 七、统计能力
     * @requestDemo {
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
     * @apiVersion 7.105
     * @tag 七、统计能力
     * @requestDemo {
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
     * @apiVersion 7.106
     * @tag 七、统计能力
     * @requestDemo {
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
     * @apiVersion 7.107
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
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
     * @apiVersion 7.108
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
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
     * @apiVersion 7.109
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
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
     * @apiVersion 7.110
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
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
     * 客户订购物联设备数量排行
     * @apiNote 客户订购物联设备数量排行
     * @apiVersion 7.111
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":1,"rows":[{"deviceNum":4,"tenantName":"测试客户0010","tenantId":"120046990"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    tenantName：租户名称
     *	    tenantId：租户Id
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderIotDeviceByTenant")
    TableDataInfo getOrderIotDeviceByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 客户订购无人机设备数量排行
     * @apiNote 客户订购无人机设备数量排行
     * @apiVersion 7.112
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":1,"rows":[{"deviceNum":2,"tenantName":"测试客户0010","tenantId":"120046990"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    tenantName：租户名称
     *	    tenantId：租户Id
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderUavDeviceByTenant")
    TableDataInfo getOrderUavDeviceByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 客户订购雷达数量排行
     * @apiNote 客户订购雷达数量排行
     * @apiVersion 7.113
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":1,"rows":[{"deviceNum":5,"tenantName":"测试客户0010","tenantId":"120046990"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    tenantName：租户名称
     *	    tenantId：租户Id
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderRadarDeviceByTenant")
    TableDataInfo getOrderRadarDeviceByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 客户订购喇叭数量排行
     * @apiNote 客户订购喇叭数量排行
     * @apiVersion 7.114
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":1,"rows":[{"deviceNum":4,"tenantName":"测试客户0010","tenantId":"120046990"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    tenantName：租户名称
     *	    tenantId：租户Id
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderHornDeviceByTenant")
    TableDataInfo getOrderHornDeviceByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 物联设备订购按客户数量排行
     * @apiNote 物联设备订购按客户数量排行
     * @apiVersion 7.115
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":4,"rows":[{"deviceCode":"10010112120029","tenantNum":1,"deviceName":"空气微站029"},{"deviceCode":"10210010010000","tenantNum":1,"deviceName":"龙威气象1"},{"deviceCode":"rain-frgd-9596-01","tenantNum":1,"deviceName":"城三5G海淀永定路70号院515号楼1ZNR19031652"},{"deviceCode":"rain-kvmk-9271-02","tenantNum":1,"deviceName":"城三5G海淀百乐宾馆1ZNR19031526"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    tenantNum：租户数量
     *	    deviceName：设备名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderTenantByIotDevice")
    TableDataInfo getOrderTenantByIotDevice(@RequestBody AnalysisVO analysisVO);

    /**
     * 无人机订购按客户数量排行
     * @apiNote 无人机订购按客户数量排行
     * @apiVersion 7.116
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":2,"rows":[{"deviceCode":"51150200003007000001","tenantNum":1,"deviceName":"三江新区沙坪街道诚志立交"},{"deviceCode":"51150200833007000001","tenantNum":1,"deviceName":"三江新区白莲"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    tenantNum：租户数量
     *	    deviceName：设备名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderTenantByUavDevice")
    TableDataInfo getOrderTenantByUavDevice(@RequestBody AnalysisVO analysisVO);

    /**
     * 雷达订购按客户数量排行
     * @apiNote 雷达订购按客户数量排行
     * @apiVersion 7.117
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":5,"rows":[{"deviceCode":"122234567890112","tenantNum":1,"deviceName":"大羊坊北桥修改名字一次啊啊\n哈"},{"deviceCode":"123321123QWE-123","tenantNum":1,"deviceName":"动物园站铁塔修改"},{"deviceCode":"12342567890112","tenantNum":1,"deviceName":"由由之星223铁塔修改"},{"deviceCode":"123456789013","tenantNum":1,"deviceName":"海龙国际"},{"deviceCode":"123qweasdzxc","tenantNum":1,"deviceName":"琼海铁塔牛路岭水坝阁楼(测试环境)铁塔pc修改"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    tenantNum：租户数量
     *	    deviceName：设备名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderTenantByRadarDevice")
    TableDataInfo getOrderTenantByRadarDevice(@RequestBody AnalysisVO analysisVO);

    /**
     * 喇叭订购按客户数量排行
     * @apiNote 喇叭订购按客户数量排行
     * @apiVersion 7.118
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":23,"rows":[{"deviceCode":"51150200831378000000","tenantNum":11,"deviceName":"三江新区沙坪街道诚志立交"},{"deviceCode":"51030200831377000001","tenantNum":11,"deviceName":"五路居慧科大厦"},{"deviceCode":"51101100831377000001","tenantNum":10,"deviceName":"御湖熙谷"},{"deviceCode":"31011200831377000003","tenantNum":7,"deviceName":"大连腾飞园区6号喇叭"},{"deviceCode":"170188117358","tenantNum":6,"deviceName":"海淀益园文创基地A区5号楼-铁塔pc修改"},{"deviceCode":"1234578978945","tenantNum":4,"deviceName":"大连腾飞园区5号喇叭"},{"deviceCode":"51150200831378000000111","tenantNum":1,"deviceName":"24343545"},{"deviceCode":"51102500831377000003","tenantNum":1,"deviceName":"银山镇党委"},{"deviceCode":"51101100831377000021","tenantNum":1,"deviceName":"喇叭6"},{"deviceCode":"51101100831377000020","tenantNum":1,"deviceName":"更改经纬度勿动(测试图层顺序)"},{"deviceCode":"51101100831377000019","tenantNum":1,"deviceName":"喇叭7"},{"deviceCode":"51101100831377000018","tenantNum":1,"deviceName":"喇叭8"},{"deviceCode":"51101100831377000017","tenantNum":1,"deviceName":"喇叭9"},{"deviceCode":"51101100831377000016","tenantNum":1,"deviceName":"喇叭5"},{"deviceCode":"51101100831377000015","tenantNum":1,"deviceName":"大连市中山广场4号大喇叭"},{"deviceCode":"51101100831377000014","tenantNum":1,"deviceName":"喇叭3"},{"deviceCode":"51101100831377000013","tenantNum":1,"deviceName":"喇叭2"},{"deviceCode":"51101100831377000012","tenantNum":1,"deviceName":"喇叭1"},{"deviceCode":"51030200831377000004","tenantNum":1,"deviceName":"轻化工大学"},{"deviceCode":"51030200831377000003","tenantNum":1,"deviceName":"南湖救助站"},{"deviceCode":"51030200831377000001111","tenantNum":1,"deviceName":"大连高新技术产业园区管理委员会"},{"deviceCode":"35092300831378000000","tenantNum":1,"deviceName":"屏南县双溪镇北村村文山"},{"deviceCode":"170288054232","tenantNum":1,"deviceName":"0616测试1"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    tenantNum：租户数量
     *	    deviceName：设备名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderTenantByHornDevice")
    TableDataInfo getOrderTenantByHornDevice(@RequestBody AnalysisVO analysisVO);


    /**
     * 设备-执法记录仪设备总览
     * @apiNote 设备-执法记录仪设备总览
     * @apiVersion 7.119
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1719370228660,
     * 	"data": {
     * 		"channelCountOffline": 6,
     * 		"channelCountOnline": 7,
     * 		"channelCountTotal": 13,
     * 		"onlinePercentage": "0.54",
     * 		"devCountOffline": 6,
     * 		"devCountOnline": 4,
     * 		"deviceCountTotal": 19,
     * 		"onlinePercentageDev": "0.21",
     * 		"deviceNoConnectCount": "9",
     * 		"disAlgorithmTaskNum": 0,
     * 		"upAlgorithmTaskNum": 0,
     * 		"orderDeviceNum": 0,
     * 		"alarmLocationDevNum": 0
     *        }
     * }
     * @param analysisVO 入参
     * @return Result
     */
    @PostMapping("workbench/getRecorderDeviceStatistics")
    Result<DeviceStatisticsVO> getRecorderDeviceStatistics(@RequestBody AnalysisVO analysisVO);

    /**
     * 查询执法记录仪数量统计区域排行
     * @apiNote 查询执法记录仪数量统计区域排行
     * @apiVersion 7.120
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaBoxNum": "0",
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
    @PostMapping("/workbench/getRecorderDeviceStatisticsByArea")
    TableDataInfo getRecorderDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO);

    /**
     * 客户订购执法记录仪数量排行
     * @apiNote 客户订购执法记录仪数量排行
     * @apiVersion 7.121
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @requestDemo {"total":1,"rows":[{"deviceNum":5,"tenantName":"测试客户0010","tenantId":"120046990"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceNum：设备数量
     *	    tenantName：租户名称
     *	    tenantId：租户Id
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderRecorderDeviceByTenant")
    TableDataInfo getOrderRecorderDeviceByTenant(@RequestBody AnalysisVO analysisVO);

    /**
     * 执法记录仪订购按客户数量排行
     * @apiNote 执法记录仪订购按客户数量排行
     * @apiVersion 7.122
     * @tag 七、统计能力
     * @requestDemo {
     *   "areaCodeLists": [
     *     "120115",
     *     "120118",
     *   ],
     *   "pageNum": 1,
     *   "pageSize": 5
     * }
     * @responseDemo {"total":23,"rows":[{"deviceCode":"51150200831378000000","tenantNum":11,"deviceName":"三江新区沙坪街道诚志立交"},{"deviceCode":"51030200831377000001","tenantNum":11,"deviceName":"五路居慧科大厦"},{"deviceCode":"51101100831377000001","tenantNum":10,"deviceName":"御湖熙谷"},{"deviceCode":"31011200831377000003","tenantNum":7,"deviceName":"大连腾飞园区6号喇叭"},{"deviceCode":"170188117358","tenantNum":6,"deviceName":"海淀益园文创基地A区5号楼-铁塔pc修改"},{"deviceCode":"1234578978945","tenantNum":4,"deviceName":"大连腾飞园区5号喇叭"},{"deviceCode":"51150200831378000000111","tenantNum":1,"deviceName":"24343545"},{"deviceCode":"51102500831377000003","tenantNum":1,"deviceName":"银山镇党委"},{"deviceCode":"51101100831377000021","tenantNum":1,"deviceName":"喇叭6"},{"deviceCode":"51101100831377000020","tenantNum":1,"deviceName":"更改经纬度勿动(测试图层顺序)"},{"deviceCode":"51101100831377000019","tenantNum":1,"deviceName":"喇叭7"},{"deviceCode":"51101100831377000018","tenantNum":1,"deviceName":"喇叭8"},{"deviceCode":"51101100831377000017","tenantNum":1,"deviceName":"喇叭9"},{"deviceCode":"51101100831377000016","tenantNum":1,"deviceName":"喇叭5"},{"deviceCode":"51101100831377000015","tenantNum":1,"deviceName":"大连市中山广场4号大喇叭"},{"deviceCode":"51101100831377000014","tenantNum":1,"deviceName":"喇叭3"},{"deviceCode":"51101100831377000013","tenantNum":1,"deviceName":"喇叭2"},{"deviceCode":"51101100831377000012","tenantNum":1,"deviceName":"喇叭1"},{"deviceCode":"51030200831377000004","tenantNum":1,"deviceName":"轻化工大学"},{"deviceCode":"51030200831377000003","tenantNum":1,"deviceName":"南湖救助站"},{"deviceCode":"51030200831377000001111","tenantNum":1,"deviceName":"大连高新技术产业园区管理委员会"},{"deviceCode":"35092300831378000000","tenantNum":1,"deviceName":"屏南县双溪镇北村村文山"},{"deviceCode":"170288054232","tenantNum":1,"deviceName":"0616测试1"}],"code":200}
     *      返回参数data下字段解析：
     *      deviceCode：设备编码
     *	    tenantNum：租户名称
     *	    deviceName：设备名称
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getOrderTenantByRecorderDevice")
    TableDataInfo getOrderTenantByRecorderDevice(@RequestBody AnalysisVO analysisVO);


    /**
     * 告警统计总览(大批量设备编码查询)
     * @apiNote 告警统计总览(大批量设备编码查询)
     * @apiVersion 7.123
     * @tag 七、统计能力
     * @requestDemo {
     *   "queryStartTime": "2024-03-01 00:00:00",
     *   "queryEndTime": "2025-03-31 23:59:59",
     *   "deviceCodeList":[
     *     "21010200001327000001",
     *     "11111222223333344444-00000000832127000010"
     *   ]
     * }
     * @responseDemo {"code":200,"msg":"请求成功","time":1750645287793,"data":{"todayAll":0,"todayProcess":0,"todayComplete":0,"totalProcess":0,"totalComplete":0,"penddingJudged":0,"pendingDispatched":0,"peddingDisposal":0,"pendingApprove":0,"penddingLeaderApprove":0,"overtime":0,"judgeNum":0,"dispatchNum":0,"handleNum":0,"approveNum":0,"orderCompleteNum":10950,"allNum":205333,"realNum":0,"repeatCompleteNum":0,"falseNum":0,"processNum":194383,"accuracy":null,"completionRate":null,"accuracys":null,"completionRates":null,"suppressNum":0,"acceptanceNum":0,"avgNum":0,"deviceName":null,"deviceCode":null,"deviceCodeList":null}}
     *      返回参数data下字段解析：
     *      allNum：告警数量
     *	    processNum：处理中数量
     *	    orderCompleteNum：已完结数量
     *
     * @param analysisVO 入参
     * @return TableDataInfo
     */
    @PostMapping("/workbench/getNumAllByDeviceCode")
    Result<EventNumVO> getNumAllByDeviceCode(@RequestBody AnalysisVO analysisVO);
}
