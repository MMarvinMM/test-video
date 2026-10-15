package com.chinatower.sync.consumer.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

import java.util.List;

/**
 * 4.8.设备/通道同步-到其他系统-队列推送 请求实体类
 */
@Data
public class AIotDeviceSync implements Serializable {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 设备来源 1 能源；2 运管；3一体
     */
    private String source;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 资源系统的设备编码
     */
    private String resourceDeviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 父设备编码
     */
    private String parentDeviceCode;

    /**
     * 操作类型
     * 0：新增，1：修改，2：删除其他平台推送给设备中心时有此字段，其他平台从设备中心拉取数据时无此字段，由各平台根据设备编码和删除标记自行判断操作类型
     */
    private int optType;

    /**
     * 设备归属平台编码
     * 从平台管理获取
     * 能源和业务类型一致
     */
    private String platformCode;

    /**
     * 产品编码
     */
    private String productionCode;

    /**
     * 设备名称缩写
     */
    private String deviceNameAbbr;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 设备序列号
     */
    private String sn;

    /**
     * 业务类型
     * 当来源为运管录入（source=2）
     * 5 视联类
     * 录入
     * 1 备电
     * 2 换电
     * 5 视联类
     * 7 光伏
     * 0 发电
     * 4 低速充电
     * 6 车电
     * 8 汽车充电
     * 9 边缘网关
     */
    private int businessType;

    /**
     * 协议类型
     * 字典值，详见字典同步接口，字典类型为：
     * 2：行业标准
     * 0：国际标准
     * 3：私有协议
     * 1：铁塔标准
     */
    private String protocolType;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    /**
     * 属地节点编码
     */
    private String locationCode;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 站址资源编码
     */
    private String publicSiteCode;

    /**
     * 站址名称
     */
    private String siteName;

    /**
     * 站址地址
     */
    private String address;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 省编码（行政区）
     */
    private String adminAreaProvince;

    /**
     * 市编码（行政区）
     */
    private String adminAreaCity;

    /**
     * 区县编码（行政区）
     */
    private String adminArea;

    /**
     * 省编码（管理区）
     */
    private String manageAreaProvince;

    /**
     * 市编码（管理区）
     */
    private String manageAreaCity;

    /**
     * 区县编码（管理区）
     */
    private String manageArea;

    /**
     * 通道信息
     */
    private List<AIotDeviceChannel> channels;

    /**
     * 设备扩展信息
     */
    private String deviceExtInfo;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 允许删除
     */
    private int delPermit;

    /**
     * 删除原因
     */
    private String reason;

    /**
     * 厂商编码
     */
    private String factoryCode;

    /**
     * 厂商名称
     */
    private String factoryName;

    /**
     * 设备来源版本
     */
    private Long sourceVersion;

    /**
     * 设备业务类型编码(一级)
     */
    private String businessCode;

    /**
     * 设备资源类型编码(二级)
     */
    private String resourceCode;

    /**
     * 设备类型（三级）
     */
    private String deviceType;

    /**
     * 设备子类型编码（四级） 4001 音柱、4002 喇叭
     */
    private String subDevType;

    /**
     * 设备子类型编码（四级）名称
     */
    private String subDevTypeName;

    /**
     * 协议编码
     */
    private String protocolCode;

    public void setDelFlag(String delFlag) {
        if (StringUtils.equals("2", delFlag)) {
            delFlag = "1";
        }
        this.delFlag = delFlag;
    }
}

