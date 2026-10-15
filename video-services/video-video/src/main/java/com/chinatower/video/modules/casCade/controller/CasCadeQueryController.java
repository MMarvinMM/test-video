package com.chinatower.video.modules.casCade.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.CasCadeQueryAPI;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.GroupByNodeVO;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
@Slf4j
public class CasCadeQueryController implements CasCadeQueryAPI {
    @Resource
    private ICasCadeQueryService casCadeQueryService;
    @Override
    public Result<CasCadeVO> getCasCadeByDeviceCode(String deviceCode) {
        return Result.data(casCadeQueryService.getCasCadeByDeviceCode(deviceCode));
    }

    @Override
    public Result<CasCadeVO> getCasCadeByDeviceInfo(String deviceCode, Integer deviceType) {
        return Result.data(casCadeQueryService.getCasCadeByDeviceInfo(deviceCode, deviceType));
    }

    @Override
    public Result<String> getCurrentPlatformCode() {
        return Result.data(casCadeQueryService.getCurrentPlatformCode());
    }

    @Override
    public Result<List<CasCadeVO>> getCasCadeByDeviceCodeBatch(List<String> list){
        return Result.data(casCadeQueryService.getCasCadeByDeviceCodeBatch(list));
    }

    @Override
    public Result<CasCadeVO> getCasCadeByTaskId(String taskId){
        return Result.data(casCadeQueryService.getCasCadeByTaskId(taskId));
    }

    @Override
    public Result<List<CasCadeVO>> getCasCadeByChannelCodeBatch(List<String> list){
        return Result.data(casCadeQueryService.getCasCadeByChannelCodeBatch(list));
    }
    @Override
    public Result<Map<String,String>> getCasCadeMapByDeviceCodeBatch(List<String> list) {
        return Result.data(casCadeQueryService.getCasCadeMapByDeviceCodeBatch(list));
    }
    @Override
    public Result<Map<String,String>> getCasCadeMapByChannelCodeBatch(List<String> list) {
        return Result.data(casCadeQueryService.getCasCadeMapByChannelCodeBatch(list));
    }
//    @Override
//    public Result<List<Map>> groupByNode(@RequestBody GroupByNodeVO groupByNodeVO){
//        return Result.data(casCadeQueryService.groupByNode(groupByNodeVO));
//    }
}
