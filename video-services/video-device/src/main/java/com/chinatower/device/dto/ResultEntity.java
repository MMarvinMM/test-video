package com.chinatower.device.dto;

import lombok.Data;

/**
* @Description: 雷达同步接口返回对象
**/
@Data
public class ResultEntity {

    /**
     * Y 返回编码->请求成功：0 /请求失败：500.
     */
    private String code;

    /**
     * Y 返回信息.  ->请求成功：success /请求失败：错误的说明.
     */
    private String msg;


    /**
     * 返回任务ID
     */
    private String data;

    public ResultEntity() {
    }

    public ResultEntity(String code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
