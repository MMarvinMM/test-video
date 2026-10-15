package com.chinatower.stdu.common.entity;

import com.chinatower.video.api.entity.VideoConstants;
import lombok.Data;

/**
* @Description: 南向接口返回对象
**/
@Data
public class ResultEntity<T> {

    /**
     * Y 返回编码->请求成功：0000 /请求失败：8888.
     */
    private String returnCode;

    /**
     * Y 返回信息.  ->请求成功：0000 /请求失败：8888.
     */
    private String returnMsg;

    /**
     * 错误编码. retureCode为8888时必须写明错误代码.
     */

    private String errorCode;

    /**
     * 错误消息->retureCode为8888时必须写明错误代码.
     */
    private String errorMsg;

    private T data;

    protected ResultEntity() {
    }

    protected ResultEntity(String returnCode, String returnMsg, T data) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.data = data;
    }

    protected ResultEntity(String returnCode, String returnMsg, String errorCode, String errorMsg, T data) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public static <T> ResultEntity<T> success(T data){
        return new ResultEntity(VideoConstants.SUCCESS_CODE, VideoConstants.SUCCESS_MESSAGE, data);
    }

    public static <T> ResultEntity<T> fail(){
        return new ResultEntity(VideoConstants.FAIL_CODE, VideoConstants.FAIL_MESSAGE, null);
    }

    public static <T> ResultEntity<T> fail(String errorCode, String errorMsg){
        return new ResultEntity(VideoConstants.FAIL_CODE, VideoConstants.FAIL_MESSAGE, errorCode, errorMsg, null);
    }
}
