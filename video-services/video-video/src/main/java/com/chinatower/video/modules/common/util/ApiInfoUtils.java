package com.chinatower.video.modules.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ApiInfoService
 * @Deacription 调用北向接口常量类
 * @Author chenmf
 * @Date 2022/2/24 20:38
 * @Version 1.0
 **/
@Slf4j
@Component
public class ApiInfoUtils {


    private static final String RESULT_CODE = "resultCode";

    private static final String RESULT_MSG = "resultMsg";

    /**
     * @Description: 判断成功
     * @Author: wangyt
     * @Date: 2023/4/11
     * @param cr
     * @return boolean
     **/
    public static boolean crIsSuccess(ResultEntity cr) {
        return cr != null && DeviceNorthIntfConstants.SUCCESS_STATUS_CODE.equals(cr.getReturnCode());
    }

    /**
     * @Description: 北向接口返回转换
     * @Author: wangyt
     * @Date: 2023/4/11
     * @param cr
     * @param respMsgData
     * @return com.chinatower.common.entity.Result<com.alibaba.fastjson.JSONObject>
     **/
    public static Result<JSONObject> transResult(ResultEntity cr, List<SysDictDataResponse> respMsgData) {
        JSONObject crJson = JSON.parseObject(JSONObject.toJSONString(cr));
        if (crIsSuccess(cr)) {
            return Result.data(crJson);
        } else {
            // 转换报错信息
            String errorCode = cr.getErrorCode();
            StringBuffer errorMsg = new StringBuffer();
            if (respMsgData != null && !respMsgData.isEmpty()) {
                for (SysDictDataResponse respMsg : respMsgData) {
                    if (respMsg.getDictValue().equals(errorCode)) {
                        errorMsg.append(respMsg.getDictLabel());
                        break;
                    }
                }
            }
            if (errorMsg.length() == 0) {
                if(!StringUtils.isEmpty(cr.getErrorMsg())){
                    errorMsg.append(cr.getErrorMsg());
                }else{
                    errorMsg.append("系统繁忙，请稍后再试!");
                }
            }
            Map<String,String> returnData = new HashMap<>();
            returnData.put(RESULT_CODE, errorCode);
            returnData.put(RESULT_MSG, errorMsg.toString());
            return Result.fail(JSON.toJSONString(returnData));
        }
    }
}
