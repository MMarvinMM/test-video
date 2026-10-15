package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDiyTreeResultVO extends BaseDataScopeEntity {
    private int id;

    /**
     * 自定义树编码
     * @required true
     */
    private String treeId;

    /**
     * 节点类型 1文本 2设备
     */
    private String nodeType;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 摄像头状态
     */
    private String status;

    /**
     * 摄像头状态
     */
    private String deviceStatus; //摄像头状态

    /**
     * 乡镇编码
     */
    private String townCode;
    /**
     * 乡镇名称
     */
    private String townName;
    /**
     * 设备别名
     */
    private String deviceAlias;
    /**
     * 渠道别名
     */
    private String channelAlias;

    /**
     * 节点编码
     */
    private String code;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点层级
     */
    private String level;

    private String parentCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;


    //前台使用，默认 false
    private Boolean checked;

    /**
     * 设备数量
     */
    private Integer num = 0;

    /**
     * 在线数量
     * onlineNum
     */
    private Integer onlineNum = 0;

    /**
     * 标注作为集合的第一条数据
     */
    private Integer first;

    /**
     * 是否收藏
     * 0未收藏 1已收藏
     */
    private String isMonitor = "0";

    /**
     * 收藏数量
     */
    private int isMonitorNum = 0;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private String categoryCode;

    private String facade;

    private String siteCode;

    private int deviceStatusCount;

    private String uniqueCode;

    private String isMonitorFlag;

    private List<DeviceDiyTreeResultVO> list = new ArrayList();

    /**
     * 设备类型
     */
    private List<Integer> categoryCodeList;
    /** 在离线状态 */
    private List<Integer> statusList;

    private String imageUrl;
}
