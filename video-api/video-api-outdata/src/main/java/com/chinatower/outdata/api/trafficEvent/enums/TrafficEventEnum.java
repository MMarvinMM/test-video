package com.chinatower.outdata.api.trafficEvent.enums;



import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 百度交通接口枚举
 */
public interface TrafficEventEnum {

    String TRAFFIC_BAIDU_STATUS = "status";

    String TRAFFIC_BAIDU_MESSAGE = "message";

    String TRAFFIC_BAIDU_RESULT = "result";

    String TRAFFIC_BAIDU_SUCCESS_STATUS = "0";

    @Getter
    @AllArgsConstructor
    enum InterfaceConfig {
        TRAFFIC_UGCLIST("https://jiaotong.baidu.com/openapi/v2/event/ugclist",  "获取指定辖区的UGC用户上报事件列表"),
        TRAFFIC_ALARMLIST("https://jiaotong.baidu.com/openapi/v2/event/alarmlist",  "获取指定辖区拥堵预警列表"),
        TRAFFIC_ALARMDETAIL("https://jiaotong.baidu.com/openapi/v2/event/alarmdetail",  "获取指定拥堵预警的拥堵详情"),
        TRAFFIC_ALARMCURVE("https://jiaotong.baidu.com/openapi/v2/event/alarmcurve",  "获取指定拥堵预警的拥堵指标曲线数据");

        @ApiModelProperty(value = "接口编码")
        private final String infUrl;
        @ApiModelProperty(value = "接口描述")
        private final String infDesc;
    }

}
