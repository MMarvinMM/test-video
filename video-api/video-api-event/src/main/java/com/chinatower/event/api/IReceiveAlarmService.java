package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.factory.ReceiveAlarmFallbackFactory;
import com.chinatower.event.entity.dto.IotAlarmConfigDTO;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "receiveAlarmService", value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = ReceiveAlarmFallbackFactory.class)
public interface IReceiveAlarmService {

    /**
     * 物联设备上报告警接口
     * @apiNote 物联设备上报告警接口 详细信息
     * @apiVersion 1.68
     * @tag 一、告警事件能力
     * @requestDemo {[{"alarmTime":"2023-08-09 22:49:03","deviceCode":"1234","deviceName":"111","alarmCode":"111","importanceLevel":"1","alarmBody":"111","monitor":"{"so2":"111"}"}]}
     * @responseDemo {"code": 200,"msg": "接收告警成功","time": 1673254838741,"data": []}
     * @param receiveIotAlarmVOList 接收告警信息集合
     * @return Result
     */
    @PostMapping(value = "/alarm/receive/receiveIotAlarm")
    Result receiveIotAlarm(@RequestBody List<ReceiveIotAlarmVO> receiveIotAlarmVOList);

    @PostMapping(value = "/alarm/receive/receiveRegionAlarm")
    Result receiveRegionAlarm(@RequestBody List<ReceiveRegionAlarmDto> receiveIotAlarmVOList);

    /**
     *  需要获取该租户下所有已开启物联设备所配置的物联告警配置中，配置的告警类型对应的有效设备、设备类型、阈值、物联因子关系等信息。
     *  @apiVersion 6.25
     *  @requestDemo {"pageNum":2,"pageSize":10,"createDateOrder":null,"updateDateOrder":null,"total":13,"tenantId":"120046990","industryCode":"000000","appVerCode":"1.0"}
     *  @responseDemo {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1719307823392,
     *     "data": {
     *         "records": [
     *             {
     *                 "id": "17",
     *                 "iotAlarmConfigName": "物联告警配置6",
     *                 "configMethod": "1",
     *                 "configStatus": "0",
     *                 "creator": "fanzw500",
     *                 "createTime": "2024-02-06 15:19:25",
     *                 "modifier": "fanzw500",
     *                 "updateTime": "2024-02-06 15:19:36",
     *                 "deleteFlag": 0,
     *                 "tenantId": "120046990",
     *                 "userId": "9163651856694185b178d08d762aaf1d",
     *                 "appVerCode": "1.0",
     *                 "industryCode": "000000",
     *                 "iotAlarmType": "RGSB-876ba930-f167-473c-9ca2-97e750b19ccf",
     *                 "deviceType": "0001",
     *                 "receiveStartTime": null,
     *                 "receiveEndTime": null,
     *                 "receivePeriodTime": "",
     *                 "relation": "2"
     *             },
     *             {
     *                 "id": "10",
     *                 "iotAlarmConfigName": "物联告警配置4ceshi",
     *                 "configMethod": "1",
     *                 "configStatus": "0",
     *                 "creator": "weichuang001",
     *                 "createTime": "2023-12-14 09:57:30",
     *                 "modifier": "weichuang001",
     *                 "updateTime": "2023-12-14 10:00:12",
     *                 "deleteFlag": 0,
     *                 "tenantId": "120046990",
     *                 "userId": "9163651856694185b178d08d762aaf1d",
     *                 "appVerCode": "1.0",
     *                 "industryCode": "000000",
     *                 "iotAlarmType": "WLGJ-LLGJ",
     *                 "deviceType": "2002",
     *                 "receiveStartTime": null,
     *                 "receiveEndTime": null,
     *                 "receivePeriodTime": "",
     *                 "relation": "2"
     *             },
     *             {
     *                 "id": "2",
     *                 "iotAlarmConfigName": "物联告警配置3",
     *                 "configMethod": "1",
     *                 "configStatus": "0",
     *                 "creator": "weichuang001",
     *                 "createTime": "2023-12-14 09:46:19",
     *                 "modifier": "weichuang001",
     *                 "updateTime": "2023-12-14 10:22:44",
     *                 "deleteFlag": 0,
     *                 "tenantId": "120046990",
     *                 "userId": "9163651856694185b178d08d762aaf1d",
     *                 "appVerCode": "1.0",
     *                 "industryCode": "000000",
     *                 "iotAlarmType": "WLGJ-2",
     *                 "deviceType": "2002",
     *                 "receiveStartTime": null,
     *                 "receiveEndTime": null,
     *                 "receivePeriodTime": "",
     *                 "relation": "2"
     *             }
     *         ],
     *         "total": 13,
     *         "size": 10,
     *         "current": 2,
     *         "orders": [],
     *         "optimizeCountSql": true,
     *         "searchCount": true,
     *         "countId": null,
     *         "maxLimit": null,
     *         "pages": 2
     *     }
     * }
     * @param model 入参
     * @return
     */
    @PostMapping("/iotAlarmConfig/queryIotAlarmConfig")
    Result<Object> queryIotAlarmConfigList(IotAlarmConfigDTO model);
}
