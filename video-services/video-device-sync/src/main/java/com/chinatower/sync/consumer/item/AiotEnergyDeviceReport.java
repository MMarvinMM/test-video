package com.chinatower.sync.consumer.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiotEnergyDeviceReport implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * 设备名称
     */
    private String devName;
    /**
     * 设备编码
     */
    private String devCode;
    /**
     * 设备区分： 10 低速充电; 11 汽车充电
     */
    private Integer deviceType;
    /**
     * 站址编码
     */
    private String stationCode;
    /**
     * 站址名称
     */
    private String stationName;
    /**
     * 站址公共库编码
     */
    private String stationPubCode;
    /**
     * 设备型号
     */
    private String model;
    /**
     * 设备厂家
     */
    private String factory;
    /**
     * 运营状态
     */
    private String status;
    /**
     * 入网验证状态
     */
    private String netStatus;
    /**
     * 备注
     */
    private String remark;
    /**
     * 设备类型
     */
    private String devType;
    /**
     * 管理区域省
     */
    private String provinceId;
    /**
     * 管理区域市
     */
    private String cityId;
    /**
     * 管理区域区
     */
    private String countyId;
    /**
     * 设备来源
     */
    private String resource;
    /**
     * 行政区域省
     */
    private String crewProvinceId;
    /**
     * 行政区域市
     */
    private String crewCityId;
    /**
     * 行政区域区域
     */
    private String crewCountyId;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 在线状态
     */
    private Integer onlineStatus;
    /**
     * 逻辑删除标识0未删除 1、已删除
     */
    private Integer delFlag;
    /**
     * 接入协议
     */
    private String collectProtocol;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 设备属地节点
     */
    private String locationCode;

    private String longitude;

    private String latitude;

    private String townCode;

    private String townName;

    private String location;

    /**
     * canal类型
     */
    private String canalType;

    /**
     * 执行时间
     */
    private long ts;
}
