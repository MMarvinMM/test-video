package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
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
public class DeviceTreeVO extends BaseDataScopeEntity implements Serializable {

    private String tenantId;            //租户编码

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;

    /**
     * 设备名称
     * @required true
     */
    private String deviceName;

    /**
     * 机巢编码
     */
    private String uavNestCode;

    /**
     * 机巢名称
     */
    private String uavNestName;

    /**
     * 机巢状态
     */
    private String uavNestStatus;

    /**
     * 机巢设备类型
     */
    private Integer uavNestPositionFix;

    /**
     * 机巢标定状态
     */
    private Integer uavNestCalibrationStatus;

    /**
     * 通道编码
     * @required true
     */
    private String channelCode;

    /**
     * 通道名称
     * @required true
     */
    private String channelName;

    /**
     * 摄像头状态
     * @required true
     */
    private String status;              //摄像头状态

    private String provinceId;          //省级编码

    private String provinceName;        //省级名称

    private String provinceParentCode;  //省级父类编码

    private String cityId;              //市级编码

    private String cityName;            //市级名称

    private String cityParentCode;      //市级父类编码

    private String countyId;            //区级编码

    private String countyName;          //区级名称

    private String countyParentCode;    //区级父类编码

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
     * @required true
     */
    private String deviceAlias;
    /**
     * 渠道别名
     */
    private String channelAlias;

    /**
     * 节点编码
     * @required true
     */
    private String code;

    /**
     * 节点名称
     * @required true
     */
    private String name;

    /**
     * 节点层级
     * @required true
     */
    private String level;

    /**
     * 父节点编码
     */
    private String parentCode;

    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 组织类别
     */
    private String orgType;

    //前台使用，默认 false
    private Boolean checked;

    /**
     * 设备数量
     * @required true
     */
    private Integer num = 0;

    /**
     * 在线数量
     * onlineNum
     * @required true
     */
    private Integer onlineNum = 0;

    /**
     * 设备数量
     */
    private Integer videoNum = 0;

    /**
     * 子节点列表
     */
    private List<DeviceTreeVO> list = new ArrayList<>();

    private String devicePage; //设备管理界面来源：1-运营管理-设备管理

    /**
     * 标签类型id
     */
    private Integer labelTypeId;
    /**
     * 标签类型名称
     */
    private String labelTypeName;

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
     * @required true
     */
    private String isMonitor = "0";

    /**
     * 标签是否收藏
     * 0未收藏 1已收藏
     * @required true
     */
    private String tagCollect = "0";

    /**
     * 收藏数量
     *@required true
     */
    private int isMonitorNum = 0;


    /**
     * 收藏数量
     *@required true
     */
    private int isTagCollectNum = 0;

    /**
     * 设备厂家编码
     */
    private String modelCode;

    /**
     * 设备厂家名称
     */
    private String modelName;

    /**
     * 设备型号
     */
    private String deviceModelCode;

    /**
     * 设备地址
     */
    private String location;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private String categoryCode;

    /**
     * 雷达最大探测半径
     */
    private String wZone;

    /**
     * 雷达天线扫描周期 单位：秒
     */
    private String devicePeriod;

    /**
     * 相控角度
     */
    private String parAngle;

    /**
     * 外型 1-球机；2-半球；3-枪机
     */
    private String facade;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 设备在线数量
     */
    private int deviceStatusCount;

    /**
     * 唯一编码
     */
    private String uniqueCode;

    private String userId;            //用户编码

    /**
     * 是否查询收藏  0-收藏，1-所有
     */
    private String isMonitorFlag;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 摄像机图标地址
     */
    private String imageUrl;

    /**
     * 设备类型集合
     */
    private List<String> deviceTypeList;

    /**
     * 设备状态集合
     */
    private List<String> deviceStatusList;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 海拔
     */
    private String altitude;

    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     *  是否有未完结告警（0-否、1-是）
     */
    private String isCompleteFlag;

    /**
     * 通道状态 0在线1离线
     */
    private String channelStatus;

    /**
     * 通道在线数量
     */
    private int channelStatusCount;

    /**
     * 查询设备分类列表
     */
    private List<Integer> categoryCodeList;

    /**
     * 查询设备状态列表
     */
    private List<Integer> statusList;

    /**
     * 查询限制数量
     */
    private int limitNum;

    /**
     * 标签类型名称
     */
    private String labelTypeNameOrd;

    /**
     * 标签名称
     */
    private String labelNameOrd;

    /**
     * 通道编码列表
     */
    private List<String> channelCodeList;

    /**
     * 无人机飞行状态
     */
    private String flyStatus;

    /**
     * 父节点唯一标识
     */
    private String parentUniCode;

    /**
     * 是否图标管理全量查询 0-否 1-是
     */
    private Integer isDeviceIcon;

    /**
     * 是否机巢设备 true-是
     */
    private Boolean isNestDevice = false;

    private String proLevel;

    private String cityLevel;

    private String countryLevel;

    private String devLevel;

    private String deviceStatus;

    private String orgId;

    private String orgName;

    private String orgLevel;

    private String parentOrgId;

    /**
     * 分页
     */
    private Integer pageNum;
    /**
     * 分页
     */
    private Integer pageSize;
}
