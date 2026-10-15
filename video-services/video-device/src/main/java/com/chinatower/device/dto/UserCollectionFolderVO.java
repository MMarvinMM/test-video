package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 收藏夹
 */
@Data
public class UserCollectionFolderVO implements Serializable {

    private String treeId;

    /**
     * 类型 1.收藏夹 2.收藏夹下设备资源信息
     */
    private String type;

    /**
     * 收藏类型 1行政区域2租户组织3标签4管理区域5摄像机6通道7物联8雷达9无人机通道10喇叭11无人机12执法记录仪13执法记录仪通道14资源
     */
    private String collObjType;

    /**
     * 收藏对象编码
     */
    private String collObjCode;

    /**
     * 收藏对象名称
     */
    private String collObjName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

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

    /**
     * 状态
     */
    private String status;

    private String deviceCode;

    private String channelCode;

    private List<UserCollectionFolderVO> children;
}
