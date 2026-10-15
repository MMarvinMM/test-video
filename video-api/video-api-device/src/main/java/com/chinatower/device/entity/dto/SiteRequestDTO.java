package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SiteRequestDTO
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/10
 **/
@Data
public class SiteRequestDTO extends BaseDataScopeEntity {

    /**
     * 站址id
     */
    private String siteCode;

    /**
     * 区县id
     */
    private String countyId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 0在线 1离线
     */
    private String status;
    /**
     * 权限类型
     */
    private String authType;
    /**
     * 设备集合
     */
    private List<String> devList;
    /**
     * 组织编码
     */
    private String orgId;
    /**
     * 部门编码
     */
    private String deptId;
    /**
     * 租户集合
     */
    private List<String> tenantList;
    /**
     * 组织级别
     */
    private String orgLevel;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 方圆几公里内
     */
    private String distance;
    /**
     * 省
     */
    private String provinceId;
    /**
     * 市
     */
    private String cityId;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 组织编码集合
     */
    private List<String> orgIdList;
    /**
     * 设备集合
     */
    private List<String> deviceCodeList;
    /**
     * 标签集合
     */
    private List<Long> labelIdList;
    /**
     * 页码
     */
    private Integer pageSize;
    /**
     * 页长
     */
    private Integer pageNum;

    /**
     * 行业版本编码
     */
    @ApiModelProperty("行业版本编码")
    private String appVerCode;
    /**
     * 设备名称
     */
    private String devName;
    /**
     * 设备厂家编码
     */
    private String modelCode;
    /**
     * 设备厂家名称
     */
    private String modelName;
    /**
     * 乡镇
     */
    private String townCode;
    /**
     * 设备厂家编码集合
     */
    private List<String> modelCodeList;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 设备或通道名称
     */
    private String devChanName;
    /**
     * 是否收藏
     */
    private String isMonitorFlag;

    /**
     * 经度
     */
    private String orderLong;

    /**
     * 纬度
     */
    private String orderLat;
    /**
     * 设备类型集合
     */
    private List<Integer> categoryCodeList;
    /**
     * 算法编码集合
     */
    private List<String> algorithmCodeList;
    /**
     * 部门编码集合
     */
    private List<String> deptIdList;
    /**
     * 设备状态集合
     */
    private List<Integer> deviceStatusList;

    /**
     * 关键字检索-物联设备编号/名称（APP/小程序使用）
     */
    private String codeOrName;

    /**
     * 设备厂家编码集合
     */
    private List<String> modelCodes;

    /**
     * 是否全选，1：全选
     */
    private String isAllAlgorithm;

    /**
     * 勾选标签，1：全部勾选 2:全不选 3：部分勾选
     */
    private String isLabel;

    /**
     * 勾选组织，1：全部勾选 2:全不选 3：部分勾选
     */
    private String isDept;

    /**
     * 摄像机区域类型（1省份-2地市-3区县-4乡镇）
     */
    @ApiModelProperty(value = "摄像机区域类型（1省份-2地市-3区县-4乡镇）")
    private String deviceType;

    private List<String> deviceTypeList;

    /**
     * 摄像机区域编码（省份-地市-区县-乡镇四级）
     */
    @ApiModelProperty(value = "摄像机区域编码（省份-地市-区县-乡镇四级）")
    private String deviceArea;

    /**
     * 摄像机状态
     */
    private String deviceStatus;

    /**
     * 接到编码列表
     */
    private List<DeviceInfoVO> townCodeList;

    /**
     * 关键字
     */
    private String keyWords;

    private String categoryCode;

    /**
     * 勾选组织，1：全部勾选 2:全不选 3：部分勾选
     */
    private String isTownCode;

    /**
     * 设备类型 1-无人机 2-执法记录仪 3-雷达 4-物联 5-喇叭
     */
    private Integer queryType;

    /**
     * 查询标识
     */
    private Integer queryFlag;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 创建开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;

    /**
     * 创建结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    /**
     * ar收藏标识
     */
    private Integer arTagFlag;

    /**
     * 订购状态
     */
    private String orderStatus;

    private List<String> siteCodeList;
    /**
     * 不包含的机巢编码
     */
    private List<String> notIncludeNestCodeList;
}
