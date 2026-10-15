package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.bdm.api.entity.InspectionConfStatisticsVO;
import com.chinatower.bdm.api.factory.GridFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "InspectStatisticsAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = GridFallbackFactory.class)
public interface InspectStatisticsAPI {
    /**
     * 巡查统计总览
     * @apiNote 巡查统计总览
     * @apiVersion 13.26
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-12-30 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null}
     * @responseDemo {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1735552132284,
     *     "data": {
     *         "gridNum": 30,
     *         "taskCompletedNum": 0,
     *         "gridKeeperNum": 32,
     *         "gridKeeperTwoNum": 24,
     *         "normalCount": 0,
     *         "taskIncompleteNum": 0,
     *         "workDay": 50,
     *         "gridMarkNum": 15,
     *         "lockInNum": 0,
     *         "inspectionTaskNum": 0,
     *         "noNormalCount": 32,
     *         "attendanceRate": 0,
     *         "gridNoMarkNum": 15,
     *         "gridKeeperOneNum": 8
     *     }
     * }
     * 返回参数data下字段解析：
     *      gridNum: 网格数量
     *      taskCompletedNum:
     *      gridKeeperNum: 网格员数量
     *      gridKeeperTwoNum: 普通网格员数量
     *      normalCount: 正常人数
     *      taskIncompleteNum: 待完成数量
     *      workDay: 应签到总数量
     *      gridMarkNum: 已标绘数量
     *      lockInNum: 签到数量
     *      inspectionTaskNum: 巡查任务数量
     *      noNormalCount: 异常人数
     *      attendanceRate: 出勤率
     *      gridNoMarkNum: 未标绘数量
     *      gridKeeperOneNum: 网格长数量
     * @return
     */
    @PostMapping("/statistics/inspectOverview")
    Result inspectOverview(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格层级数量排行
     * @apiNote 网格层级数量排行
     * @apiVersion 13.27
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 13,
     *     "rows": [
     *         {
     *             "gridNum": 1,
     *             "gridLevelName": "一级网格",
     *             "totalNum": 30,
     *             "gridLevel": "1"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      gridNum: 网格数量
     *      gridLevelName: 网格等级名称
     *      totalNum: 总数量
     *      gridLevel: 网格等级
     * @return
     */
    @PostMapping("/statistics/getGridLevel")
    TableDataInfo getGridLevel(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格性别统计
     * @apiNote 网格性别统计
     * @apiVersion 13.28
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null}
     * @responseDemo {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1735552414188,
     *     "data": {
     *         "unknownNum": 18,
     *         "gridKeeperNum": 32,
     *         "unknownRate": 56,
     *         "womanRate": 31,
     *         "manRate": 13,
     *         "gridKeeperWomanNum": 10,
     *         "gridKeeperManNum": 4
     *     }
     * }
     * 返回参数data下字段解析：
     *      unknownNum: 未知性别网格员数量
     *      gridKeeperNum: 网格员总数量
     *      unknownRate: 未知网格员占比
     *      womanRate: 女网格员数量占比
     *      manRate: 男网格员数量占比
     *      gridKeeperWomanNum: 女网格员数量
     *      gridKeeperManNum: 男网格员数量
     * @return
     */
    @PostMapping("/statistics/getGridKeeperGender")
    Result getGridKeeperGender(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格员年龄分布
     * @apiNote 网格员年龄分布
     * @apiVersion 13.29
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null}
     * @responseDemo {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1735552414206,
     *     "data": {
     *         "unknownNum": 18,
     *         "thirtyToForty": 1,
     *         "lessTwentyRate": 6,
     *         "moreFifty": 4,
     *         "fortyToFiftyRate": 6,
     *         "moreFiftyRate": 13,
     *         "unknownRate": 56,
     *         "twentyToThirty": 5,
     *         "thirtyToFortyRate": 3,
     *         "lessTwenty": 2,
     *         "fortyToFifty": 2,
     *         "twentyToThirtyRate": 16
     *     }
     * }
     * 返回参数data下字段解析：
     *      unknownNum: 未知年龄网格员数量
     *      thirtyToForty: 31~40岁网格员数量
     *      lessTwentyRate: 20岁及以下网格员数量百分比
     *      moreFifty: 50岁以上网格员数量
     *      fortyToFiftyRate: 41~50岁网格员数量百分比
     *      moreFiftyRate: 50岁以上网格员数量百分比
     *      unknownRate: 未知年龄网格员数量百分比
     *      twentyToThirty: 21~30岁网格员数量
     *      thirtyToFortyRate: 31~40岁网格员数量百分比
     *      lessTwenty: 20岁及以下网格员数量
     *      fortyToFifty: 41~50岁网格员数量
     *      twentyToThirtyRate: 21~30岁网格员数量百分比
     * @return
     */
    @PostMapping("/statistics/getGridKeeperAge")
    Result getGridKeeperAge(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格统计
     * @apiNote 网格统计
     * @apiVersion 13.30
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 30,
     *     "rows": [
     *         {
     *             "gridLevelName": "一级网格",
     *             "gridKeeperNum": 5,
     *             "gridName": "测试客户786868768768768768768768768760010-通用平台版本-1.0",
     *             "keeperName": "雷达北京市",
     *             "gridId": "202302231607492948",
     *             "pointStatus": "1",
     *             "gridLevel": "1"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      gridLevelName: 网格层级名称
     *      gridKeeperNum: 网格员数量
     *      gridName: 网格名称
     *      keeperName: 网格长姓名
     *      gridId: 网格id
     *      pointStatus: 标绘状态（0未标绘，1已标绘）
     *      gridLevel: 网格等级
     * @return
     */
    @PostMapping("/statistics/getGridStatistics")
    TableDataInfo getGridStatistics(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格出勤率排行
     * @apiNote 网格出勤率排行
     * @apiVersion 13.31
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 30,
     *     "rows": [
     *         {
     *             "gridName": "测试客户786868768768768768768768768760010-通用平台版本-1.0",
     *             "attendanceRates": 0.0000,
     *             "attendanceRate": 0,
     *             "gridId": "202302231607492948",
     *             "clockInNum": 0,
     *             "workDay": 1790,
     *             "gridLevel": "1"
     *         },
     *         {
     *             "gridName": "57485793574957495749579759875897597597535749574395",
     *             "attendanceRates": 0.0000,
     *             "attendanceRate": 0,
     *             "gridId": "202306271443216076",
     *             "clockInNum": 0,
     *             "workDay": 2002,
     *             "gridLevel": "2"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      gridName: 网格名称
     *      attendanceRates: 出勤率，保留小数点后3位
     *      attendanceRate: 出勤率
     *      gridId: 网格id
     *      clockInNum: 签到数量
     *      workDay: 出勤总数量
     *      gridLevel: 网格层级
     * @return
     */
    @PostMapping("/statistics/getGridAttendance")
    TableDataInfo getGridAttendance(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格长/网格员勤率排行
     * @apiNote 网格长/网格员勤率排行
     * @apiVersion 13.32
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 8,
     *     "rows": [
     *         {
     *             "attendanceRates": 0.0000,
     *             "attendanceRate": 0,
     *             "keeperName": "lisg",
     *             "keeperId": "202307021201203630",
     *             "workDay": 358,
     *             "clockInNum": 0
     *         },
     *         {
     *             "attendanceRates": 0.0000,
     *             "attendanceRate": 0,
     *             "keeperName": "海淀区网格统计测试",
     *             "keeperId": "202406051419225484",
     *             "workDay": 203,
     *             "clockInNum": 0
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      keeperName: 网格长/网格员姓名
     *      attendanceRates: 出勤率，保留小数点后4位
     *      attendanceRate: 出勤率
     *      keeperId: 网格长/网格员id
     *      clockInNum: 签到数量
     *      workDay: 出勤总数量
     *      gridLevel: 网格层级
     * @return
     */
    @PostMapping("/statistics/getGridKeeperAttendance")
    TableDataInfo getGridKeeperAttendance(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格长/网格员勤率排行
     * @apiNote 网格长/网格员勤率排行
     * @apiVersion 13.33
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 8,
     *     "rows": [
     *         {
     *             "attendanceRates": 0.0000,
     *             "attendanceRate": 0,
     *             "keeperName": "lisg",
     *             "keeperId": "202307021201203630",
     *             "workDay": 358,
     *             "clockInNum": 0
     *         },
     *         {
     *             "attendanceRates": 0.0000,
     *             "attendanceRate": 0,
     *             "keeperName": "海淀区网格统计测试",
     *             "keeperId": "202406051419225484",
     *             "workDay": 203,
     *             "clockInNum": 0
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      keeperName: 网格长/网格员姓名
     *      attendanceRates: 出勤率，保留小数点后4位
     *      attendanceRate: 出勤率
     *      keeperId: 网格长/网格员id
     *      clockInNum: 签到数量
     *      workDay: 出勤总数量
     *      gridLevel: 网格层级
     * @return
     */
    @PostMapping("/statistics/getGridKeeperAttendanceByTwo")
    TableDataInfo getGridKeeperAttendanceByTwo(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格打卡状态统计
     * @apiNote 网格打卡状态统计
     * @apiVersion 13.34
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5, sort: null, sortOrder: null}
     * @responseDemo {
     *     "total": 30,
     *     "rows": [
     *         {
     *             "gridLevelName": "一级网格",
     *             "gridName": "测试客户786868768768768768768768768760010-通用平台版本-1.0",
     *             "attendanceRates": 0.0000,
     *             "earlyNum": 0,
     *             "outNum": 0,
     *             "clockInNum": 0,
     *             "workDay": 1790,
     *             "noInNum": 1775,
     *             "noOutNum": 1775,
     *             "attendanceRate": 0,
     *             "lateNum": 0,
     *             "gridId": "202302231607492948",
     *             "inNum": 0,
     *             "gridLevel": "1"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      gridLevelName: 网格层级名称
     *      gridName: 网格名称
     *      attendanceRates: 出勤率，保留小数点后4位
     *      earlyNum: 早退次数
     *      outNum: 未签到次数
     *      clockInNum: 签到次数
     *      workDay: 出勤总次数
     *      noInNum: 正常签退次数
     *      noOutNum: 未签退次数
     *      attendanceRate: 出勤率
     *      lateNum: 迟到次数
     *      gridId: 网格id
     *      inNum: 正常签到次数
     *      gridLevel: 网格层级
     * @return
     */
    @PostMapping("/statistics/getGridClockState")
    TableDataInfo getGridClockState(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格员打卡状态统计
     * @apiNote 网格员打卡状态统计
     * @apiVersion 13.35
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5, sort: null, sortOrder: null}
     * @responseDemo {
     *     "total": 32,
     *     "rows": [
     *         {
     *             "gridLevelName": "二级网格",
     *             "gridName": "57485793574957495749579759875897597597535749574395",
     *             "attendanceRates": 0.0000,
     *             "earlyNum": 0,
     *             "outNum": 0,
     *             "keeperId": "202307021201203630",
     *             "workDay": 31,
     *             "clockInNum": 0,
     *             "noInNum": 30,
     *             "noOutNum": 30,
     *             "loginName": "QweQ",
     *             "attendanceRate": 0,
     *             "lateNum": 0,
     *             "keeperName": "lisg",
     *             "inNum": 0,
     *             "gridLevel": "2"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      gridLevelName: 网格层级名称
     *      gridName: 网格名称
     *      attendanceRates: 出勤率，保留小数点后4位
     *      earlyNum: 早退次数
     *      outNum: 未签到次数
     *      keeperId: 网格长/网格员账号
     *      clockInNum: 签到次数
     *      workDay: 出勤总次数
     *      noInNum: 正常签退次数
     *      noOutNum: 未签退次数
     *      attendanceRate: 出勤率
     *      lateNum: 迟到次数
     *      keeperName: 网格长/网格员姓名
     *      inNum: 正常签到次数
     *      gridLevel: 网格层级
     * @return
     */
    @PostMapping("/statistics/getGridKeeperClockState")
    TableDataInfo getGridKeeperClockState(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格巡查任务数量排行
     * @apiNote 网格巡查任务数量排行
     * @apiVersion 13.36
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 30,
     *     "rows": [
     *         {
     *             "inspectionTaskNum": 31,
     *             "taskCompletedNum": 1,
     *             "gridName": "57485793574957495749579759875897597597535749574395",
     *             "gridId": "202306271443216076",
     *             "taskIncompleteNum": 30
     *         },
     *         {
     *             "inspectionTaskNum": 10,
     *             "taskCompletedNum": 1,
     *             "gridName": "测试客户786868768768768768768768768760010-通用平台版本-1.0",
     *             "gridId": "202302231607492948",
     *             "taskIncompleteNum": 9
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      inspectionTaskNum: 巡查任务总数量
     *      taskCompletedNum: 巡查任务已完成数量
     *      gridName: 网格名称
     *      gridId: 网格id
     *      taskIncompleteNum: 巡查任务未完成数量
     * @return
     */
    @PostMapping("/statistics/getGridInspectTask")
    TableDataInfo getGridInspectTask(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 网格任务完成率排行
     * @apiNote 网格任务完成率排行
     * @apiVersion 13.37
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 30,
     *     "rows": [
     *         {
     *             "inspectionTaskNum": 10,
     *             "taskCompletedNum": 1,
     *             "gridName": "测试客户786868768768768768768768768760010-通用平台版本-1.0",
     *             "taskCompletedRate": 10,
     *             "gridId": "202302231607492948",
     *             "taskCompletedRates": 10.0000,
     *             "gridLevel": "1"
     *         },
     *         {
     *             "inspectionTaskNum": 31,
     *             "taskCompletedNum": 1,
     *             "gridName": "57485793574957495749579759875897597597535749574395",
     *             "taskCompletedRate": 3,
     *             "gridId": "202306271443216076",
     *             "taskCompletedRates": 3.2258,
     *             "gridLevel": "2"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      inspectionTaskNum: 巡查任务总数量
     *      taskCompletedNum: 巡查任务已完成数量
     *      gridName: 网格名称
     *      gridId: 网格id
     *      taskCompletedRates: 巡查任务完成率
     *      gridLevel: 网格层级
     * @return
     */
    @PostMapping("/statistics/getGridInspectTaskCompletionRate")
    TableDataInfo getGridInspectTaskCompletionRate(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 根据网格员类型统计网格任务完成率（提供给国土行业）
     * @apiNote 网格任务完成率统计
     * @apiVersion 13.38
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 30,
     *     "rows": [
     *         {
     *             "inspectionTaskNum": 10,
     *             "taskCompletedNum": 1,
     *             "gridName": "测试客户786868768768768768768768768760010-通用平台版本-1.0",
     *             "taskCompletedRate": 10,
     *             "gridId": "202302231607492948",
     *             "taskCompletedRates": 10.0000,
     *             "gridLevel": "1"
     *         },
     *         {
     *             "inspectionTaskNum": 31,
     *             "taskCompletedNum": 1,
     *             "gridName": "57485793574957495749579759875897597597535749574395",
     *             "taskCompletedRate": 3,
     *             "gridId": "202306271443216076",
     *             "taskCompletedRates": 3.2258,
     *             "gridLevel": "2"
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      inspectionTaskNum: 巡查任务总数量
     *      taskCompletedNum: 巡查任务已完成数量
     *      gridName: 网格名称
     *      gridId: 网格id
     *      taskCompletedRates: 巡查任务完成率
     *      gridLevel: 网格层级
     * @return
     */
    @PostMapping("/statistics/getGridTaskCompletionRateByType")
    Result getGridTaskCompletionRateByType(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 根据网格员类型分页统计网格任务完成率
     * @apiNote 网格任务完成率统计
     * @apiVersion 13.39
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {"code":200,"msg":"请求成功","time":1739778442775,"data":[[{"inspectionTaskNum":0,"taskCompletedNum":0,"gridName":"aa","keeper_type":"1","keeperType":"网格长","taskCompletedRate":0,"gridId":"202406040921269001","keeperId":0,"taskIncompleteNum":0,"keeperNum":0},{"inspectionTaskNum":0,"taskCompletedNum":0,"gridName":"aa","keeper_type":"2","keeperType":"网格员","taskCompletedRate":0,"gridId":"202406040921269001","taskIncompleteNum":0,"keeperNum":0}],[{"inspectionTaskNum":0,"taskCompletedNum":0,"gridName":"aa1","keeper_type":"1","keeperType":"网格长","taskCompletedRate":0,"gridId":"202406040922496836","keeperId":0,"taskIncompleteNum":0,"keeperNum":0},{"inspectionTaskNum":0,"taskCompletedNum":0,"gridName":"aa1","keeper_type":"2","keeperType":"网格员","taskCompletedRate":0,"gridId":"202406040922496836","taskIncompleteNum":0,"keeperNum":0}]]}
     * 返回参数rows下字段解析：
     *      inspectionTaskNum: 巡查任务总数量
     *      taskCompletedNum: 巡查任务已完成数量
     *      gridName: 网格名称
     *      gridId: 网格id
     *      keeper_type: 网格长/网格员
     *      keeperType: 网格长/网格员
     *      keeperId: 网格长/网格员账号
     *      taskIncompleteNum: 待完成数量
     *      keeperNum: 网格员数量
     * @return
     */
    @PostMapping("/statistics/getGridTaskCompletionRateToPage")
    TableDataInfo getGridTaskCompletionRateToPage(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 巡查任务反馈端统计
     * @apiNote 巡查任务反馈端统计
     * @apiVersion 13.40
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null}
     * @responseDemo {
     *     "total": 1,
     *     "rows": [
     *         {
     *             "inspectionTaskNum": 5,
     *             "inspectionTaskNumApp": 0,
     *             "inspectionTaskNumPc": 5,
     *             "pcRate": 100,
     *             "appRate": 0
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      inspectionTaskNum: 巡查任务总数量
     *      inspectionTaskNumApp: APP端巡查任务数量
     *      inspectionTaskNumPc: PC端巡查任务数量
     *      pcRate: PC端巡查任务百分比
     *      appRate: APP端巡查任务百分比
     * @return
     */
    @PostMapping("/statistics/getGridInspectTaskBack")
    TableDataInfo getGridInspectTaskBack(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 巡查反馈24小时分布
     * @apiNote 巡查反馈24小时分布
     * @apiVersion 13.41
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null}
     * @responseDemo {
     *     "total": 24,
     *     "rows": [
     *         {
     *             "label": "0:00-1:00",
     *             "inspectionTaskBackNum": 0
     *         },
     *         {
     *             "label": "1:00-2:00",
     *             "inspectionTaskBackNum": 0
     *         }
     *         {
     *             "label": "22:00-23:00",
     *             "inspectionTaskBackNum": 0
     *         },
     *         {
     *             "label": "23:00-24:00",
     *             "inspectionTaskBackNum": 0
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      label: 时间段
     *      inspectionTaskBackNum: 巡查反馈数量
     * @return
     */
    @PostMapping("/statistics/getGridInspectTaskBackByHour")
    TableDataInfo getGridInspectTaskBackByHour(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 巡查任务按日期统计
     * @apiNote 巡查任务按日期统计
     * @apiVersion 13.42
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null}
     * @responseDemo {
     *     "total": 366,
     *     "rows": [
     *         {
     *             "assignedDate": "2024-01-01",
     *             "inspectionTaskNum": 1,
     *             "taskCompletedNum": 0,
     *             "taskIncompleteNum": 1
     *         },
     *         {
     *             "assignedDate": "2024-12-31",
     *             "inspectionTaskNum": 0,
     *             "taskCompletedNum": 0,
     *             "taskIncompleteNum": 0
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      assignedDate: 巡查日期
     *      inspectionTaskNum: 巡查任务数量
     *      taskCompletedNum: 已完成数量
     *      taskIncompleteNum: 待完成数量
     * @return
     */
    @PostMapping("/statistics/getGridInspectTaskByDay")
    TableDataInfo getGridInspectTaskByDay(@RequestBody InspectStatisticsVo inspectStatisticsVo);

    /**
     * 巡查人任务数量排行
     * @apiNote 巡查人任务数量排行
     * @apiVersion 13.43
     * @tag 十三、网格能力
     * @param inspectStatisticsVo
     * @requestDemo {queryStartTime: "2024-01-01 00:00:00", queryEndTime: "2024-12-30 23:59:59", gridId: null, pageNum: 1, pageSize: 5}
     * @responseDemo {
     *     "total": 9,
     *     "rows": [
     *         {
     *             "inspectionTaskNum": 14,
     *             "taskCompletedNum": 4,
     *             "completedRate": 29,
     *             "keeperName": "weichuang001",
     *             "keeperId": "202307181100055724",
     *             "taskIncompleteNum": 10,
     *             "completedRates": 28.5714
     *         }
     *         {
     *             "inspectionTaskNum": 1,
     *             "taskCompletedNum": 0,
     *             "completedRate": 0,
     *             "keeperName": "海淀区网格统计测试",
     *             "keeperId": "202406051419225484",
     *             "taskIncompleteNum": 1,
     *             "completedRates": 0.0000
     *         }
     *     ],
     *     "code": 200
     * }
     * 返回参数rows下字段解析：
     *      inspectionTaskNum: 巡查任务总数量
     *      taskCompletedNum: 已完成数量
     *      completedRate: 巡查任务完成率
     *      keeperName: 巡查人姓名
     *      keeperId: 巡查人账号
     *      taskIncompleteNum: 待完成数量
     *      completedRates: 巡查任务完成率，保留小数点后4位
     * @return
     */
    @PostMapping("/statistics/getGridInspectTaskByPerson")
    TableDataInfo getGridInspectTaskByPerson(@RequestBody InspectStatisticsVo inspectStatisticsVo);


    /**
     * 人工巡查计划数量统计，返回用户范围内的巡查计划总数量
     * @apiNote 人工巡查计划数量统计，返回用户范围内的巡查计划总数量
     * 返回参数解析：
     * 	allNum：总数
     * @apiVersion 4.1
     * @tag 四、巡查计划能力
     * @requestDemo
     * {
     * }
     * @responseDemo
     * allNum：巡查计划总数量
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1707200971129,
     *     "data": {
     *         "allNum": 36
     *     }
     * }
     * 返回参数rows下字段解析：
     *      allNum: 用户范围内的巡查计划总数量
     * @return
     */
    @PostMapping("/statistics/getInspectionConf")
    Result<InspectionConfStatisticsVO> getInspectionConf(@RequestBody InspectionConfStatisticsVO inspectionConfParam);
}
