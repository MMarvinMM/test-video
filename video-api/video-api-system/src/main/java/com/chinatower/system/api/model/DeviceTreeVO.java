package com.chinatower.system.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DeviceTreeVO
 * @Description 摄像头树实体
 * @Author chensw
 * @Date 2022/1/18
 */

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTreeVO implements Serializable {

    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通达CODE
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 摄像头状态
     */
    private String status;              //

    /**
     * 省级编码
     */
    private String provinceId;          //

    /**
     * 省级名称
     */
    private String provinceName;        //

    /**
     * 省级父类编码
     */
    private String provinceParentCode;  //

    /**
     * 市级编码
     */
    private String cityId;              //

    /**
     * 市级名称
     */
    private String cityName;            //

    /**
     * 市级父类编码
     */
    private String cityParentCode;      //

    /**
     * 区级编码
     */
    private String countyId;            //

    /**
     * 区级名称
     */
    private String countyName;          //

    /**
     * 区级父类编码
     */
    private String countyParentCode;    //

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
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 等级
     */
    private String level;

    /**
     * 升级编码
     */
    private String parentCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

    /**
     * 组织类型
     */
    private String orgType;

    /**
     * 前台使用，默认 false
     */
    private Boolean checked;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 在线数量
     */
    private Integer onlineNum;

    /**
     * 设备列表
     */
    private List<DeviceTreeVO> list;

    /**
     * 设备管理界面来源：1-运营管理-设备管理
     */
    private String devicePage;
    /**
     * 标签id
     */
    private Integer labelId;
    /**
     * 标签名称
     */
    private String labelName;

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
     * 行业
     */
    private String industryCode;
    /**
     * 版本
     */
    private String appVersion;
    /**
     * 区域CODE
     */
    private String areaCode;
    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 用于前端唯一性标识
     */
    private String uniqueCode;
}
