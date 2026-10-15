package com.chinatower.video.modules.videopreset.service;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;

import java.util.Map;

/**
 * @ClassName IGuardService
 * @Description 看守卫服务
 * @Author chenshaowu
 * @Date 2022/2/21
 **/
public interface IGuardInfoService {
    
    /**
     * 功能描述: 设置看守卫
     *
     * @param: guardDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/21
     * @version: 1.0
     */
    long doSetGuard(GuardDTO guardDTO, Map<String, String> headerMap);

    /**
     * 6.48.	查询事件告警详情
     * @param videoRequestDTO
     * @return
     * @auther: sutn
     * @date: 2022/3/18
     */
    Result<JSONObject> getAlarmInfo(VideoRequestDTO videoRequestDTO);
}
