package com.chinatower.platform.modules.alarm.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @className: AlarmTypeService
 * @description: 查询告警类型服务
 * @author: peng.haibo
 * @date: 2022/6/24
 */
public interface AlarmTypeService {

    /**
     * @Description 根据告警类型ID查询告警类型名称
     * @author peng.haibo
     * @date 2022/6/24
     * @param warningTypeId 参数描述
     * @return java.lang.String
     */
    String queryWarningTypeNameByWarningTypeId(String warningTypeId);


    /**
     * @Description 通过用户id获取用户相关信息
     * @author peng.haibo
     * @date 2022/6/24
     * @param userId 参数描述
     * @return Map<String, String>
     */
    Map<String, String> getUserInfoById(String userId);
}
