package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * 喇叭管理信息上报
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceBroadcastReport extends BaseMongoDTO {
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备更新时间
     */
    private Date lastTime;
    /**
     * 在线状态: 0在线 1离线；2从未连接
     */
    private String onlineStatus;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 所属站址编码
     */
    private String siteCode;
    /**
     * 设备厂商
     */
    private String manufacturer;
    /**
     * 接入协议
     */
    private String accessProtocols;
    /**
     * 设备型号
     */
    private String deviceModel;
    /**
     * 4:广播设备
     */
    private String deviceType;
    /**
     * 挂高(米)
     */
    private String hangingHeight;
    /**
     * 产权客户id
     */
    private String rightsCustomerId;
    /**
     * 产权性质
     */
    private String rightsProperties;
    /**
     * 共享属性(共享-1、独享-0)
     */
    private String adaptability;
    /**
     * 专属客户id
     */
    private String customerId;
    /**
     * 安装时间
     */
    private Date installDate;
    /**
     * PMS立项单号
     */
    private String pmsNumber;
    /**
     * 周边环境,多个用逗号分割
     */
    private String surroundingEnvironment;
    /**
     * 描述
     */
    private String remark;
    /**
     * 删除状态，0：不删除；1：删除
     */
    private String delFlag;
    /**
     * 1-岸基雷达，2-物联网
     */
    private String categoryCode;
    /**
     * 站址名称
     */
    private String siteName;
    /**
     * 所属管理区域
     */
    private String manageArea;
    /**
     * 行政区域
     */
    private String adminArea;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 地址
     */
    private String address;
    /**
     * 所属组织
     */
    private String dept;
    /**
     * 4001 音柱，4002 喇叭
     */
    private String catalogType;
    /**
     * 同步信息
     */
    private String sygnMessage;
    /**
     * 同步状态，0：同步成功，1：同步失败
     */
    private String sygnStatus;
    /**
     * 设备大类
     */
    private String deviceBigType;
    /**
     * 雷达天线扫描周期 单位：秒
     */
    private String devicePeriod;
    /**
     * 雷达覆盖范围 单位：海里
     */
    private String wZone;
    /**
     * 相控角度
     */
    private String parAngle;
    /**
     * 广播资源编码
     */
    private String broadcastResourceCode;

    /**
     * 属地化同步新增-接入节点
     */
    private String locationCode;

    //省份
    private String provinceCode;
    //地市
    private String cityCode;
    //区县
    private String countyCode;
    //乡镇编码
    private String townCode;
    //应用修改标识（1:已修改 0未修改）
    private String appModFlag;
    //创建人
    private String createBy;
    //修改人
    private String updateBy;
    //创建时间
    private Date createTime;
    // 更新时间 格式yyyy-MM-dd HH:mm:ss
    private String updateTime;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;
}
