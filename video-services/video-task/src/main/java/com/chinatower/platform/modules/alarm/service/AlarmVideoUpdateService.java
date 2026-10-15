package com.chinatower.platform.modules.alarm.service;


import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.SecondRecogBackVO;
import com.chinatower.event.entity.vo.SecondRecogBatchVO;

import java.util.List;
import java.util.Map;

public interface AlarmVideoUpdateService {

    /**
     * 抓视频定时更新视频路径
     */
    boolean  alarmVideoUpdate();

    /**
     * taskBatchId查询定时更新二次识别结果
     */
    boolean  secondResultHandler();

    /**
     * 生产测试接口
     */
     Result presetSecondRecong(Map<String, Object> o);
}
