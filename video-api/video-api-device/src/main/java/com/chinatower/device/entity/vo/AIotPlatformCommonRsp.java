package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AIot能力平台通用响应对象
 */
@Data
public class AIotPlatformCommonRsp implements Serializable {

    /**
     * 状态码
     */
    private String code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 返回数据集合
     */
    private List<AIotPlatformDataRsp> data;

}
