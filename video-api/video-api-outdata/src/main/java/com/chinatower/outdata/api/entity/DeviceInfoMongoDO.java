package com.chinatower.outdata.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
@Data
@Document(collection="vm_device_info_t")
public class DeviceInfoMongoDO extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "device_id", type = IdType.AUTO)
    private Long deviceId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;

    /**
     * 省份
     */
    private String provinceId;

    /**
     * 地市
     */
    private String cityId;

    /**
     * 区县
     */
    private String countyId;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private Integer categoryCode;

    /**
     * 字段说明
     */
    private String remark;

    /**
     * 是否具备抓拍功能：0-否；1-是
     */
    private Integer enableSnap;

    /**
     * 设备IP
     */
    private String ip;

    /**
     * 设备端口
     */
    private String port;

    /**
     * 设备状态:1-在建；2-在网；3-退网；4-闲置
     */
    private Integer businStatus;

    /**
     * 接入方式:0-国标；1-onvif；3-私有协议
     */
    private Integer collectType;

    /**
     * 业务系统站址编码
     */
    private String siteCode;

    /**
     * 业务系统站址名称
     */
    private String siteName;

    /**
     * 公共库站址编码
     */
    private String publicSiteCode;

    /**
     * 设备归属平台编码
     */
    private String platFormCode;

    /**
     * 设备厂家code
     */
    private String modelCode;

    /**
     * 设备登录用户名
     */
    private String devUserName;

    /**
     * 设备登录密码
     */
    private String devPassword;

    /**
     * 设备扩展信息
     */
    private String devExInfo;

    /**
     * 国标编码
     */
    private String gb28181Code;

    /**
     * 设备名称缩写
     */
    private String devNameAbbr;

    /**
     * 设备产权
     */
    private String owner;

    /**
     * 接入方式 0-ip接入；1-设备编号接入
     */
    private Integer collectMethod;

    /**
     * 归属组织
     */
    private String orgCode;

    /**
     * 水平范围最小值
     */
    private String horizonMin;

    /**
     * 水平范围最大值
     */
    private String horizonMax;

    /**
     * 垂直范围从最小值
     */
    private String verticalMin;

    /**
     * 垂直范围最大值
     */
    private String verticalMax;

    /**
     * 视角场最小值
     */
    private String visualMin;

    /**
     * 视角场最大值
     */
    private String visualMax;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 0正常1删除
     */
    private String deleteFlag;

    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 站址地址
     */
    private String location;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 所属管理区域
     */
    private String areaCode;

    /**
     * 行政区域
     */
    private String admCode;
    
    /**
     * 网域类型:1-公网(默认)；0-局域内网
     */
    private Integer netType;
    
    /**
     * 级联标识
     * 是否为级联平台接入设备
     * 0非级联;
     * 1级联设备
     */
    private Integer isCascade;
    
    /**
     * 级联平台唯一id
     */
    private Integer platId;

    /**
     * 告警定位（1:开启 0关闭）
     */
    private String alarmLocation;
    /**
     * 1:按时间段整合2:按预置位整合
     */
    private String partrolMode;
    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    private String appModFlag;
    /**
     * 摄像机型号编码
     */
    private String deviceModelCode;
    /**
     * 乡镇
     */
    private String townCode;

    /**
     * 可视半径
     */
    private String visualRadius;

    /**
     * 同步状态，0未同步，1同步成功
     */
    private Integer synStatus;
    /**
     * 同步时间
     */
    private Date synTime;
    /**
     * 接入协议，根据选择厂家获取该厂家下的接入协议
     */
    private Integer collectProtocol;
    /**
     * 接入节点，基础提供
     */
    private Integer collectNode;
    /**
     * 共享属性，0共享、1私有
     */
    private Integer shareAttr;
    /**
     * 安装日期
     */
    private Date installTime;
    /**
     * 重量，支持保留2位小数，单位kg
     */
    private String weight;
    /**
     * 挂高类型，0存量铁塔、1新建铁塔、2社会塔、3社会挂高、4卡口低点
     */
    private Integer heightType;
    /**
     * is_cascade = 1 级联编码 is_cascade = 2 nvr设备编码
     */
    private String extCode;
    /**
     * 注册ID
     */
    private String registerId;
    /**
     * 接入节点
     */
    private String accessNode;
    /**
     * 海拔 单位：米
     */
    private String altitude;
    /**
     * 摄像机图标
     */
    private String imageUrl;
    /**
     * 可见节点 1 中心节点；2 省份节点；3 地市节点
     */
    private String enableVideoNode;

}
