package com.chinatower.bdm.modules.unionTargetJs.entity;

import lombok.Data;

/**
* @Description: 南向接口返回对象
**/
@Data
public class ResultEntity<T> {

    /**
     * Y 返回编码->请求成功：0 /请求失败：500.
     */
    private String code;

    /**
     * Y 返回信息.  ->请求成功：success /请求失败：错误的说明.
     */
    private String msg;



    private T data;

    protected ResultEntity() {
    }

    protected ResultEntity(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
