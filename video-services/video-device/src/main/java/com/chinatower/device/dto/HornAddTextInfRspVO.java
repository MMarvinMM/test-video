package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 广播子平台
 * 4.13.创建文本响应参数实体类
 * @author huang.xiangyu
 */
@Data
public class HornAddTextInfRspVO implements Serializable {

    //调用创建文本接口返回的文本id
    private String txtId;
}
