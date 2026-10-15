package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDeviceCollectionQueryVO implements Serializable {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 收藏类型 1行政区域 2租户组织 3标签 4管理区域 5摄像机 6通道 7物联设备
     */
    private String collObjType;

    /**
     * 收藏对象编码
     */
    private String collObjCode;

    /**
     * 收藏id
     */
    private Long userDeviceCollId;

    /**
     * 收藏夹id
     */
    private Long collectFolderId;

    /**
     * 别名
     */
    private String aliasName;

    /**
     * 排序
     */
    private Integer sortNo;
}
