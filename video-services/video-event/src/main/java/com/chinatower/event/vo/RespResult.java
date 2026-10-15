package com.chinatower.event.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("API返回消息体")
public class RespResult {

    @ApiModelProperty(value = "API接口是否调用成功", notes = "0000调用成功 8888调用失败")
    private String returnCode = "0000";
    @ApiModelProperty(value = "API接口执行结果描述", notes = "")
    private String returnMsg;
    @ApiModelProperty(value = "错误编码", notes = "retureCode为8888时必须写明错误代码")
    private String errorCode;
    @ApiModelProperty(value = "错误消息", notes = "retureCode为8888时必须写明错误代码")
    private String errorMsg;
    @ApiModelProperty("API接口返回数据")
    private Object data;

    public static final String ERROR_CODE_1001 = "1001";
    public static final String ERROR_CODE_1004 = "1004";
    public static final String ERROR_CODE_8888 = "8888";

    /**
     * 执行成功返回数据
     *
     * @param data 返回数据对象
     * @return
     */
    public static RespResult SUCCESS(Object data) {
        return RespResult.SUCCESS(data, "执行成功");
    }

    public static RespResult SUCCESS(Object data, String returnMsg) {
        RespResult response = new RespResult();
        response.returnMsg = returnMsg;
        response.data = data;
        return response;
    }

    /**
     * 执行失败返回
     *
     * @param returnMsg 失败提示信息
     * @return
     */
    public static RespResult FAIL(String returnMsg, String errorCode, String errorMsg) {
        return RespResult.FAIL("8888", returnMsg, errorCode, errorMsg);
    }

    /**
     * 执行失败返回
     *
     * @param returnCode 失败状态码
     * @param returnMsg  失败提示信息
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
}
