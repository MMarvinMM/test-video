package com.chinatower.video.modules.casCade.service;

import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.GroupByNodeVO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ICasCadeQueryService {
    CasCadeVO getCasCadeByDeviceCode(String deviceCode);

    CasCadeVO getCasCadeByDeviceInfo(String deviceCode, Integer deviceType);

    String getCurrentPlatformCode();
    List<CasCadeVO> getCasCadeByDeviceCodeBatch(List<String> list);
    CasCadeVO getCasCadeByTaskId(String taskId);
    List<CasCadeVO> getCasCadeByChannelCodeBatch(List<String> list);
    Map<String,String> getCasCadeMapByDeviceCodeBatch(List<String> list);
    Map<String,String> getCasCadeMapByChannelCodeBatch(List<String> list);
    List<Map> groupByNode(GroupByNodeVO groupByNodeVO);
}
