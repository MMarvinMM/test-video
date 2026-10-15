package com.chinatower.analysis.api;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.entity.dto.IndEventTrendAnalysisDTO;
import com.chinatower.analysis.api.entity.dto.IndStatisticAnalysisDTO;
import com.chinatower.analysis.api.entity.dto.IndTenantDeviceAnalysisDTO;
import com.chinatower.analysis.api.factory.AnalysisFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "DeviceAnalysisAPI", value = ServiceNameConstants.DEVICE_SERVICE,
        fallbackFactory = AnalysisFallbackFactory.class)
public interface DeviceAnalysisAPI {

    /**
     * 统计监控设备数量【微创0803api-device迁移api-analysis】
     * @apiNote 统计监控设备数量
     * @apiVersion 7.1
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"",}
     * @responseDemo {"code":200,"msg":"请求成功","time":1735527214666,"data":{"deviceCountTotal":46,"devCountOnline":29,"devCountOffline":17,"channelCountTotal":62,"channelCountOnline":34,"channelCountOffline":28}}
     *返回参数data下字段解析：
     *      deviceCountTotal：摄像机总数
     *	    devCountOnline：摄像机在线数
     *	    devCountOffline：摄像机离线数
     *	    channelCountOnline：通道在线数
     *	    channelCountOffline：通道离线数
     *
     * @param indTenantDeviceDTO 入参
     * @return Result<IndDeviceAnalysisVO>
     */
    @ApiOperation(value = "统计监控设备数量")
    @PostMapping("/industry/device/queryDeviceStatistics")
    Result<IndDeviceAnalysisVO> queryDeviceStatistics(@RequestBody @Valid IndTenantDeviceAnalysisDTO indTenantDeviceDTO);

    /**
     * 告警类型统计【微创0803api-device迁移api-analysis】
     * @apiNote 告警类型统计
     * @apiVersion 7.2
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"5","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648885089984,"data": [{"typeId": "1010100","typeName": "",
     * "num": 2011,"aiNum": 1971,"judgeNum": 2003,"realNum": 1703,"falseNum": 300 },{"typeId": "aaa666","typeName": "其他","num": 41}]}
     *返回参数data下字段解析：
     *      typeId：告警类型Id
     *	    typeName：告警类型名称
     *	    num：告警数量
     *	    aiNum：AI告警数量
     *	    judgeNum：研判数量
     *	    realNum：真警数量
     *	    falseNum：误报数量
     *
     * @param indStatisticDTO 入参
     * @return Result<List<IndDevTypeAnalysisVO>>
     */
    @ApiOperation("告警类型统计")
    @PostMapping("/industry/device/getDevTypeYearMonth")
    Result<List<IndDevTypeAnalysisVO>> getDevTypeYearMonth(@RequestBody @Valid IndStatisticAnalysisDTO indStatisticDTO);

    /**
     * 告警来源统计【微创0803api-device迁移api-analysis】
     * @apiNote 告警来源统计
     * @apiVersion 7.3
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"5","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1691406303301,"data": {"allNum": 1338875,"manNum": 1814,"keyNum": 1788,"sourceList": [{"warningSource": "2","warningSourceName": "人工上报",
     * "orderNum": 1814},{"warningSource": "1","warningSourceName": "AI告警","orderNum": 299016},{"warningSource": "8","warningSourceName": "物联告警","orderNum": 667145},{"warningSource": "4",
     * "warningSourceName": "摄像机告警","orderNum": 106261},{"warningSource": "3","warningSourceName": "一键告警","orderNum": 1788},{"warningSource": "9","warningSourceName": "卫星告警",
     * "orderNum": 24065},{"warningSource": "10","warningSourceName": "雷达告警","orderNum": 238786}],"ainum": 299016}}
     *返回参数data下字段解析：
     *      allNum：告警总数量
     *	    manNum：人工告警数量
     *	    keyNum：一键告警数量
     *	    warningSource：告警来源Id
     *      warningSourceName：告警来源名称
     *     	orderNum：告警数量
     *
     *
     * @param indStatisticDTO 入参
     * @return Result<IndEventAlarmNumAnalysisVO>
     */
    @ApiOperation("告警来源统计")
    @PostMapping("/industry/device/getEventAlarmNumYearMonth")
    Result<IndEventAlarmNumAnalysisVO> getEventAlarmNumYearMonth(@RequestBody @Valid IndStatisticAnalysisDTO indStatisticDTO);

    /**
     * 告警高频区域统计【微创0803api-device迁移api-analysis】
     * @apiNote 告警高频区域统计
     * @apiVersion 7.4
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2","provinceId":"110000","cityId":"110100","countyId":"110101"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886140974,"data": [{"provinceId": "360000","cityId": "360400","countyId": "360402","regionName": "江西省九江市濂溪区","num": 4122}]}
     *返回参数data下字段解析：
     *      provinceId：省Id
     *	    cityId：市Id
     *	    countyId：区Id
     *	    regionName：街道名称
     *     	num：告警数量
     *
     * @param indStatisticDTO 入参
     * @return Result<List<IndEventNumAnalysisVO>>
     */
    @ApiOperation("告警高频区域统计")
    @PostMapping("/industry/device/getEventNumByArea")
    Result<List<IndEventNumAnalysisVO>> getEventNumByArea(@RequestBody @Valid IndStatisticAnalysisDTO indStatisticDTO);

    /**
     * 告警趋势统计【微创0803api-device迁移api-analysis】
     * @apiNote 告警趋势统计
     * @apiVersion 7.5
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","trendFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648886714765,"data": [{"label": "2021","num": 1000},{"label": "2022","num": 2000}]}
     *返回参数data下字段解析：
     *      label：时间段
     *	    num：告警数量
     *
     * @param indEventTrendDTO 入参
     * @return Result<List<IndEventTrendAnalysisVO>>
     */
    @ApiOperation("告警趋势统计")
    @PostMapping("/industry/device/getEventTrend")
    Result<List<IndEventTrendAnalysisVO>> getEventTrend(@RequestBody @Valid IndEventTrendAnalysisDTO indEventTrendDTO);

    /**
     * 告警高频时段统计【微创0803api-device迁移api-analysis】
     * @apiNote 告警高频时段统计
     * @apiVersion 7.6
     * @tag 七、统计能力
     * @requestDemo {"tenantId":"","userId":"","industryCode":"","appVerCode":"","dimensionFlag":"2","queryStartTime":"2023-08-04","queryEndTime":"2023-08-09"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1648887469698,"data": [{"label": "北京市海淀区海淀区","num": 9,"accuracy":0 //准确率}]}
     *返回参数data下字段解析：
     *      label：告警区域名称
     *	    num：告警数量
     *	    accuracy：告警准确率
     *
     * @param IndAnalysisVO 入参
     * @return Result<List<Map>>
     */
    @ApiOperation("告警高频时段统计")
    @PostMapping("/industry/device/getEventNumByHour")
    Result<List<Map>> getEventNumByHour(@RequestBody @Valid IndAnalysisAnalysisVO IndAnalysisVO);

}
