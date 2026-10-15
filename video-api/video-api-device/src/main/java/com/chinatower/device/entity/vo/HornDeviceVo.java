package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 喇叭设备实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HornDeviceVo extends BaseDataScopeEntity implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备编号
     * @required true
     */
    private String deviceCode;

    /**
     * 设备名称
     * @required true
     */
    private String deviceName;

    /**
     * 设备状态
     * @required true
     */
    private String deviceStatus;

    /**
     * 设备状态名称
     */
    private String deviceStatusName;

    /**
     * 设备厂家编码
     * @required true
     */
    private String manufacturerCode;

    /**
     * 设备厂家名称
     * @required true
     */
    private String manufacturerName;

    /**
     * 设备型号编码
     * @required true
     */
    private String modelCode;

    /**
     * 设备类型
     * @required true
     */
    private String deviceType;

    /**
     * 设备类型名称
     * @required true
     */
    private String deviceTypeName;

    /**
     * 订购状态
     */
    private String orderStatus;

    /**
     * 订购状态名称
     */
    private String orderStatusName;

    /**
     * 关联组织
     */
    private List<OrganDeviceRelVO> orgRels;

    /**
     * 所属组织
     */
    private String orgName;


    /**
     * 省份/省公司
     */
    private String provinceCode;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 省公司
     */
    private String provinceCompany;

    /**
     * 地市/市公司
     */
    private String cityCode;
    /**
     * 地市名称
     */
    private String cityName;
    /**
     * 地市公司
     */
    private String cityCompany;

    /**
     * 区县
     */
    private String countyCode;
    /**
     * 区县名称
     */
    private String countyName;
    /**
     * 区县公司
     */
    private String countyCompany;

    /**
     * 街道行政区域编码
     */
    private String townCode;
    /**
     * 街道行政区域名称
     */
    private String townName;

    /**
     * 关联客户数
     */
    private Integer custCount;

    /**
     * 设备挂高
     * @required true
     */
    private String height;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 设备地址
     * @required true
     */
    private String location;

    /**
     * 接入协议
     */
    private String accessProtocols;

    /**
     * 共享属性
     */
    private String shareProperty;

    /**
     * 安装日期
     */
    private String installTime;

    /**
     * 重量
     */
    private String weight;

    /**
     * 挂高类型
     */
    private String heightType;

    /**
     * 业务系统站址编码
     */
    private String siteCode;

    /**
     * 业务系统站址名称
     */
    private String siteName;

    /**
     * 是否收藏
     * 0未收藏 1已收藏
     */
    private String isMonitor = "0";

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 子节点列表
     */
    private List<IndustryDeviceTree> list;

    /**
     * 喇叭资源编码
     */
    private String broadcastResourceCode;

    /**
     * 用户到设备距离--APP使用
     */
    private String distance;

    /**
     * 附件地址列表
     */
    private List<String> fileUrlList;

    /**
     * 接入节点--属地化新增
     */
    private String locationCode;

    /**
     * 操作类型 1：同步
     */
    @ApiModelProperty(value = "操作类型 1：同步")
    private Integer optType;

    /**
     * 所属管理区域
     */
    private String areaCode;

    /**
     *行政区域
     */
    private String admCode;

    /**
     * 接入节点名称
     */
    private String locationName;

    /**
     * 组织ID--管理区域
     */
    private String deptId;

    /**
     * 属地化同步类型
     */
    private String opType;

    /**
     * ar标签是否收藏 0未收藏 1已收藏
     */
    private String tagCollect;

    /**
     * 接入节点
     */
    private String accessNode;

}
