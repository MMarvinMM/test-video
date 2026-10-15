package com.chinatower.device.utils;

import com.chinatower.common.entity.Result;
import lombok.Data;

@Data
public class RespResult {

    private String returnCode = "0000";
    private String returnMsg;
    private String errorCode;
    private String errorMsg;
    private Object data;

    /**
     * 执行成功返回数据
     * @param data 返回数据对象
     * @return
     */
    public static RespResult SUCCESS(Object data) {
        return RespResult.SUCCESS(data,"执行成功");
    }

    public static RespResult SUCCESS(Object data, String returnMsg) {
        RespResult response = new RespResult();
        response.returnMsg = returnMsg;
        response.data = data;
        return response;
    }

    /**
     * 执行失败返回
     * @param returnMsg 失败提示信息
     * @return
     */
    public static RespResult FAIL(String returnMsg, String errorCode, String errorMsg) {
        return RespResult.FAIL("8888",returnMsg,errorCode,errorMsg);
    }

    /**
     * 执行失败返回
     * @param returnCode 失败状态码
     * @param returnMsg 失败提示信息
     * @return
     */
    public static RespResult FAIL(String returnCode, String returnMsg, String errorCode, String errorMsg) {
        RespResult response = new RespResult();
        response.returnCode = returnCode;
        response.returnMsg = returnMsg;
        response.errorCode = errorCode;
        response.errorMsg = errorMsg;
        return response;
    }

    public static RespResult parseRespResultNew(Result result){
        int code = result.getCode();
        if( code == 200 || code == 0) {
            // 成功
            return RespResult.SUCCESS(result.getData(), result.getMsg());
        } else {
            // 失败
            return RespResult.FAIL("请求失败", "1002", result.getMsg());
        }
    }
}