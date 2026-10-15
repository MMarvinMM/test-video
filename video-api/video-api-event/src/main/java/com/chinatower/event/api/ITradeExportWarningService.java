package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.event.api.factory.TradeExportWarningFallbackFactory;
import com.chinatower.event.api.model.DownLoadUrlsReq;
import com.chinatower.event.entity.dto.*;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FeignClient(contextId = "exportWarningOrderService", value = ServiceNameConstants.EXPORT_SERVICE, fallbackFactory = TradeExportWarningFallbackFactory.class)

public interface ITradeExportWarningService {

    /**
     * 告警图片下载
     * @apiNote 下载告警图片接口
     * @apiVersion 1.1
     * @tag 一、告警事件能力
     * @requestDemo {"downLoadType":"0","orderIdDownLoadType":"1","downLoadOrderDTO":{"cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":6,"noticeResource":null,"confidenceLevelMin":"",
     * "confidenceLevelMax":"","minChar":"100","maxChar":"100","algorithmCode":null,"alarmTimeStart":"2023-8-14 00:00:00","alarmTimeEnd":"2023-9-13 23:59:59","warningTypeIdList":[],"searchType":"0","areaCodeList":[],
     * "tenantId":"121062862","deviceCode":[],"lableIdList":[]},"warningOrderIds":["202309131210628621772677","202309131210628621772672"]}
     * @responseDemo {"code":200,"msg": "请求成功","time": 1648886714765,"data":{}}
     * @param req 入参
     * @return Result
     */
    @PostMapping("alarm/order/downloadImgsStream")
    ResponseEntity<byte[]> downloadImgs(@RequestParam("request")HttpServletRequest request, @RequestBody AlarmDownloadReq req);

    /**
     * 下载告警图片
     * @apiNote 下载告警图片接口
     * @apiVersion 1.2
     * @tag 一、告警事件能力
     * @requestDemo {"downLoadType":"0","orderIdDownLoadType":"1","downLoadOrderDTO":{"cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":6,"confidenceLevelMin":"","confidenceLevelMax":"",
     * "minChar":"100","maxChar":"100","alarmTimeStart":"2023-7-19 00:00:00","alarmTimeEnd":"2023-8-18 23:59:59","searchType":"0","areaCodeList":[],"deviceCode":[],"lableIdList":[]},
     * "warningOrderIds":["2023081812203737269749028","2023081812205121869749029","2023081812208775269749019","2023081812201469369748990","2023081812201469369748993","2023081812112379569748975"],"name":"中国铁塔"}
     * @responseDemo {"code":200,"msg": "请求成功","time": 1648886714765,"data":{}}
     * @param req 入参
     * @return Result
     */
    @PostMapping("alarm/order/downloadImgsResponse")
    Response downloadImgsResponse(@RequestParam("response")HttpServletResponse response, @RequestParam("request")HttpServletRequest request, @RequestBody AlarmDownloadReq req);

    /**
     * 原图/框图复合油管线图处理
     * @apiNote 原图/框图复合油管线图处理
     * @apiVersion 1.3
     * @tag 一、告警事件能力
     * @requestDemo {"interceptsRemark":false,"listRemark":false,"operationsRemark":false,"pipelines":"[{\"oilGasId\":\"17286283390339993326583655\",\"color\":\"rgba(233,236,4,0.6)\",\"direction\":[{\"pointId\":1,\"percentX\":0.999,\"percentY\":0.441},{\"pointId\":2,\"percentX\":0.0,\"percentY\":0.509}]}]"}
     * @param handleAlarmImgDTO
     */
    @PostMapping("alarm/order/handleReceiveAlarmImg")
    void handleReceiveAlarmImg(@RequestBody HandleAlarmImgDTO handleAlarmImgDTO);

    /**
     * 通过地址下载 飞行成果图片/视频下载
     *
     * @param req
     * @return
     */
    @PostMapping("/air/task/downloadByUrls")
    void downloadByUrls(@RequestBody DownLoadUrlsReq req);

}
