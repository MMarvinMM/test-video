package com.chinatower.platform.project.system.service;


import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.domain.SysAdvice;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.model.SysOrderAdviceVO;

/**
 * 意见反馈
 */
public interface ISysAdviceService {

    int insert(SysAdvice sysAdvice);
    Result adviceSyncOrderSystem(JSONObject sysAdvice);
}
