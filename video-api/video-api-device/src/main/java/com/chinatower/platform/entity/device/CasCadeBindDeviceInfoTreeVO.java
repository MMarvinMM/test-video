package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CasCadeBindDeviceInfoTreeVO implements Serializable {
    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 父节点编码
     */
    private String parentCode;

    /**
     * 平台编码
     */
    private String platCode;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 节点层级
     */
    private Integer level;

    /**
     * 1-组织 2-设备
     */
    private Integer orgType;

    /**
     * 客户编码
     */
    private String customCode;

    /**
     * 子节点列表
     */
    private List<CasCadeBindDeviceInfoTreeVO> list;
}
