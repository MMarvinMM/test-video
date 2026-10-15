package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 喇叭设备查询实体类
 */
@Data
public class HornDeviceDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 喇叭主键ID
     */
    private Long id;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     */
    private Integer deviceStatus;

    /**
     * 设备厂家编码
     */
    private String manufacturerCode;

    /**
     * 设备型号编码
     */
    private String modelCode;

    /**
     * 设备类型
     */
    private Integer deviceType;

    /**
     * 设备类型集合
     */
    private List<String> deviceTypeList;

    /**
     * 订购状态
     */
    private String orderStatus;

    /**
     * 关联组织
     */
    private List<OrganDeviceRelVO> orgRels;

    /**
     * 展示组织名称
     */
    private String orgName;

    /**
     * 所属组织
     */
    private List<String> deptIds;

    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 页数限制
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String countyCode;

    /**
     * 街道编码
     */
    private String townCode;

    /**
     * 所属区域id集合
     */
    private List<Long> deptProvinceCity;

    /**
     * 所选组织id
     */
    private Long chooseOrgId;

    /**
     * 是否展示摄像机数据
     * @required true
     */
    private boolean needDevice = false;

    /**
     * 标签集合
     */
    private List<Long> labelIdList;

    /**
     * 是否查询收藏  0-收藏，1-所有
     * @required true
     */
    private String isMonitorFlag;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 站址id
     */
    private String siteCode;

    /**
     * 编号或名称--APP入参
     */
    private String codeOrName;

    /**
     * 喇叭状态集合--APP入参
     */
    private List<String> deviceStatusList;

    /**
     * 设备厂家编码集合--APP入参
     */
    private List<String> manufacturerCodeList;

    /**
     * 用户经度-APP入参
     */
    private String longitude;

    /**
     * 用户纬度--APP入参
     */
    private String latitude;

    /**
     * 附件地址列表
     */
    private List<String> fileUrlList;

    /**
     * 接入节点编码
     */
    private String locationCode;

    /**
     * 操作类型 1：同步
     */
    @ApiModelProperty(value = "操作类型 1：同步")
    private Integer optType;

    /**
     * 是否是导出excel
     */
    private boolean isExport = false;

    /**
     * app 传入标识
     */
    private String appFlag;

    /**
     * 查询标识
     */
    private Integer queryFlag;

    /**
     * ar标签标识
     */
    private Integer arTagFlag;

    /**
     * 设备编码集合
     */
    private List<String> deviceCodeList;

    /**
     * ar标签是否收藏标识0显示收藏
     */
    private String isArTagFlag;
}
