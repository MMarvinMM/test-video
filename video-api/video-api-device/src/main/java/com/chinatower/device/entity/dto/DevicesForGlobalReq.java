package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;

@Data
public class DevicesForGlobalReq extends BaseDataScopeEntity {

    /**
     * 算法编码勾选方式，0：默认 1：部分(algorithmCodeList必填)  2：全部
     */
    private String algorithmChooseType;

    /**
     * 算法编码集合
     */
    private List<String> algorithmCodeList;

    /**
     * 设备厂家编码集合
     */
    private List<String> modelCodeList;

    /**
     * 设备状态集合
     */
    private List<Integer> deviceStatusList;

    /**
     * 摄像机质量集合
     */
    private List<Integer> deviceQualityList;

    /**
     * 是否包含高质量参数 0：不包含 1：包含
     */
    private String isHighQuality;

    /**
     * 标签是勾选方式，0：默认 1：部分(labelIdList必填)  2：全部(parentLabelId必填，一级标签传"0")
     */
    private String labelChooseType;

    /**
     * 父节点标签编码
     */
    private String parentLabelId;

    /**
     * 标签集合
     */
    private List<Long> labelIdList;


    /**
     * 组织是勾选方式，0：默认 1：部分(deptIdList必填)  2：全部(parentDeptId必填，一级组织传"0")
     */
    private String deptChooseType;

    /**
     * 父节点组织编码
     */
    private String parentDeptId;

    /**
     * 部门编码集合
     */
    private List<String> deptIdList;

    /**
     * 省份编码
     */
    private String provinceId;
    /**
     * 地市编码
     */
    private String cityId;

    /**
     * 区县编码
     */
    private String countyId;

    /**
     * 街道编码
     */
    private String townCode;

    /**
     * 聚合层级 0：省份 1：地市 2：区县 3：街道
     */
    private int aggregationLevel;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private List<Integer> categoryCodeList;

}
