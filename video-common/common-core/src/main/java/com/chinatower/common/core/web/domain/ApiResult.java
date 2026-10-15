package com.chinatower.common.core.web.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
@ApiModel("API返回消息体")
public class ApiResult<T> implements Serializable {

    @ApiModelProperty(value = "API接口是否调用成功", notes = "0000调用成功 8888调用失败")
    private String returnCode = "0000";
    @ApiModelProperty(value = "API接口执行结果描述", notes = "")
    private String returnMsg;
    @ApiModelProperty(value = "错误编码", notes = "retureCode为8888时必须写明错误代码")
    private String errorCode;
    @ApiModelProperty(value = "错误消息", notes = "retureCode为8888时必须写明错误代码")
    private String errorMsg;
    @ApiModelProperty("API接口返回数据")
    private T data;

    public static final String SUCCESS_CODE_0000 = "0000";
    public static final String RESULT_DATA = "data";
    public static final String ERROR_CODE_1001 = "1001";
    public static final String ERROR_CODE_1004 = "1004";
    public static final String RETURN_CODE_ERR_MSG = "请求失败";
    public static final String RETURN_CODE_SUCCESS_MSG = "请求成功";

    /**
     * 执行成功返回数据
     *
     * @param data 返回数据对象
     * @return
     */
    public static  <T>  ApiResult<T>  SUCCESS(T data) {
        return ApiResult.SUCCESS(data, "执行成功");
    }

    public static  <T>  ApiResult<T>  SUCCESS(T data, String returnMsg) {
        ApiResult response = new ApiResult();
        response.returnMsg = returnMsg;
        response.data = data;
        return response;
    }

    public static <T> ApiResult<T> FAIL(String errorMsg) {
        ApiResult<T> response = new ApiResult<>();
        response.returnCode = "8888";
        response.returnMsg = "请求失败";
        response.errorCode = "-1";
        response.errorMsg = errorMsg;
        return response;
    }

    /**
     * 执行失败返回
     *
     * @param returnMsg 失败提示信息
     * @return
     */
    public static ApiResult FAIL(String returnMsg, String errorCode, String errorMsg) {
        return ApiResult.FAIL("8888", returnMsg, errorCode, errorMsg);
    }

    /**
     * 执行失败返回
     *
     * @param returnCode 失败状态码
     * @param returnMsg  失败提示信息
     * @return
     */
    public static ApiResult FAIL(String returnCode, String returnMsg, String errorCode, String errorMsg) {
        ApiResult response = new ApiResult();
        response.returnCode = returnCode;
        response.returnMsg = returnMsg;
        response.errorCode = errorCode;
        response.errorMsg = errorMsg;
        return response;
    }
}
