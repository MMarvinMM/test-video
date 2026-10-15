package com.chinatower.platform.modules.alarm.service;


import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.resp.SysDictDataResponse;

import java.util.List;
import java.util.Map;
/**
 * @className: InterceptService
 * @description: 回收站service
 */
public interface InterceptService {

    //告警压制数量排行按小时
    List<Map> getEventNumListByInterceptHour(DateVO dateVO, Result<List<SysDictDataResponse>> suppressDictData);
    //告警压制数量排行按小时（新）
    List<Map> getEventNumListByInterceptHourNew(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData);
    //告警压制数量统计
    List<Map> getInterceptNum(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData);
    //告警压制数量统计（新）
    List<Map> getInterceptNumNew(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData);
    //告警压制数量排行按小时2
    List<Map> getEventNumListByInterceptHour2(DateVO dateVO, Result<List<SysDictDataResponse>> suppressDictData);
    List<Map> getEventNumListByInterceptHourNew2(DateVO dateVO);


}
