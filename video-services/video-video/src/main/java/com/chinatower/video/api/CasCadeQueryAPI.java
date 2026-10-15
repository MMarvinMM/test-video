package com.chinatower.video.api;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.GroupByNodeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface CasCadeQueryAPI {
    /**
     * 根据设备编码查询接入节点、受理节点
     * @param deviceCode
     * @return
     */
    @GetMapping("/getCasCadeByDeviceCode")
    Result<CasCadeVO> getCasCadeByDeviceCode(@RequestParam("deviceCode") String deviceCode);

    /**
     * 根据设备信息查询接入节点、受理节点
     * @param deviceCode 设备编码
     * @param deviceType 设备类型
     * @return 接入、受理节点信息
     */
    @GetMapping("/getCasCadeByDeviceInfo")
    Result<CasCadeVO> getCasCadeByDeviceInfo(@RequestParam("deviceCode") String deviceCode,
                                             @RequestParam("deviceType") Integer deviceType);

    /**
     * 获取当前系统的平台节点编码
     * @return 当前系统的平台节点编码
     */
    @GetMapping("/getCurrentPlatformCode")
    Result<String> getCurrentPlatformCode();

    /**
     * 根据设备编码批量查询接入节点、受理节点
     * @param list
     * @return
     */
    @PostMapping(value = "/getCasCadeByDeviceCodeBatch")
    Result<List<CasCadeVO>> getCasCadeByDeviceCodeBatch(@RequestBody List<String> list);

    /**
     * 根据taskId查询接入节点、受理节点
     * @param taskId
     * @return
     */
    @GetMapping(value = "/getCasCadeByTaskId")
    Result<CasCadeVO> getCasCadeByTaskId(@RequestParam("taskId") String taskId);

    /**
     * 根据通道编码批量查询接入节点、受理节点
     * @param list
     * @return
     */
    @PostMapping(value = "/getCasCadeByChannelCodeBatch")
    Result<List<CasCadeVO>> getCasCadeByChannelCodeBatch(@RequestBody List<String> list);

    @PostMapping(value = "/getCasCadeMapByDeviceCodeBatch")
    Result<Map<String,String>> getCasCadeMapByDeviceCodeBatch(@RequestBody List<String> list);

    @PostMapping(value = "/getCasCadeMapByChannelCodeBatch")
    Result<Map<String,String>> getCasCadeMapByChannelCodeBatch(@RequestBody List<String> list);

//    @PostMapping(value = "/groupByNode")
//    Result<List<Map>> groupByNode(@RequestBody GroupByNodeVO groupByNodeVO);
}
