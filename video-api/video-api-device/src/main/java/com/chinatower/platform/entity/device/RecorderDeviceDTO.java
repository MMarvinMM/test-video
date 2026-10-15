package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import com.chinatower.common.annotation.Excel;

/**
 * 执法记录仪DTO
 */
@Data
public class RecorderDeviceDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 设备编码
     */
    @Excel(name = "设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;

    /**
     * 设备状态
     */
    @Excel(name = "设备状态")
    private String deviceStatusName;

    /**
     * 设备地址
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
     * 设备厂家编码
     */
    private String manufacturerCode;

    /**
     * 设备厂家名称
     */
    @Excel(name = "设备厂家")
    private String manufacturerName;

    /**
     * 设备型号编码
     */
    @Excel(name = "设备型号")
    private String modelCode;

    /**
     * 接入协议
     */
    private String accessProtocols;

    /**
     * 接入节点（中心、下沉点）
     */
    private String accessNodeCode;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 级联标识:0-直连
     */
    private Integer isCascade;

    /**
     * 接入类型:0-国标；1-onvif；3-私有协议
     */
    private Integer collectType;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 设备登录名
     */
    private String devUserName;

    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    private String appModFlag;

    /**
     * 设备所属组织
     */
    @Excel(name = "所属组织")
    private String orgName;

    /**
     * 组织编码
     */
    private String orgCode;

    /**
     * 组织关系ID
     */
    private String orgRelId;

    /**
     * 订购状态
     */
    private Integer orderStatus;

    /**
     * 订购状态名称
     */
    @Excel(name = "订购状态")
    private String orderStatusName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 省份名称
     */
    @Excel(name = "省份")
    private String provinceName;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 市名称
     */
    @Excel(name = "地市")
    private String cityName;

    /**
     * 区县编码
     */
    private String countyCode;

    /**
     * 区县名称
     */
    @Excel(name = "区县")
    private String countyName;

    /**
     * 乡镇编码
     */
    private String townCode;

    /**
     * 乡镇名称
     */
    @Excel(name = "乡镇")
    private String townName;

    /**
     * 是否收藏
     */
    private String isMonitor;

    /**
     * 距离
     */
    private String distance;

    /**
     * 关联组织
     */
    @ApiModelProperty(value = "关联组织")
    private List<OrganDeviceRelVO> orgRels;

    /**
     * 上传的附件列表
     */
    private List<String> fileUrlList;

    /**
     * 通道
     */
    private List<ChannelInfoVO> channelList;


}
