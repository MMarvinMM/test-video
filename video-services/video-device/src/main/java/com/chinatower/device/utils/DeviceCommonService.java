package com.chinatower.device.utils;

import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * video-video公共服务类
 * Created by zhujun on 2022/1/14.
 */
@Service
@Slf4j
public class DeviceCommonService {
    /**
     * 接口方法路径集合
     */
    public static final Map<String, String> urlMap;

    private static final String SUCCESS_CODE = "0000";

    @Autowired
    private SystemFeignClient systemFeignClient;

    static {
        urlMap = new HashMap<String, String>();
        urlMap.put("addAlarmLinkageList", "/linkage/devices/channels/addAlarmLinkageList");
        urlMap.put("updateAlarmLinkageList", "/linkage/devices/channels/updateAlarmLinkageList");
        urlMap.put("delAlarmLinkageList", "/linkage/devices/channels/delAlarmLinkageList");

    }


    /**
     * 判断接口返回是否为成功状态
     *
     * @param cr
     * @return
     */
    public static boolean crIsSuccess(ResultEntity cr) {
        return cr != null && SUCCESS_CODE.equals(cr.getReturnCode());
    }

    public List<SysDictDataResponse> getDict(String dataType) {
        //获取需要特殊处理行业版本
        List<SysDictDataResponse> dictList = new ArrayList<>();
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(dataType);
        if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
            dictList.addAll(sysResult.getData());
        }

        return dictList;
    }
}
