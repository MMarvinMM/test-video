package com.chinatower.sync.consumer.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * 北向接口返回转换
     * @param: apiInfo
     * @param: httpResponse
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/2/24
     * @version:
     */
    public static Result<JSONObject> transResult(ResultEntity cr, List<SysDictDataResponse> respMsgData) {
        JSONObject crJson = JSON.parseObject(JSONObject.toJSONString(cr));
        if (crIsSuccess(cr)) {
            return Result.data(crJson);
        }
        else {
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
                errorMsg.append(cr.getErrorMsg());
            }
            errorMsg.append("_").append(errorCode);
            return Result.fail(errorMsg.toString());
        }
    }


    public static boolean crIsSuccess(ResultEntity cr) {
        return cr != null && DeviceNorthIntfConstants.SUCCESS_STATUS_CODE.equals(cr.getReturnCode());
    }

}
