package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 功能说明：资源Vo
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Data
public class ResourceVO extends BaseDataScopeEntity implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 资源编码
     */
    private String resourceId;

    /**
     * 资源类型名称
     */
    @Excel(name = "资源类型")
    private String resourceTypeName;

    /**
     * 资源属性:点资源1、线资源2、面资源3
     */
    @Excel(name = "资源属性")
    private String resourceProperty;

    /**
     * 资源名称
     */
    @Excel(name = "资源名称")
    private String resourceName;

    /**
     * 资源类型编码
     */
    private String resourceTypeId;

    /**
     * 显示顺序
     */
    private Long orderNum;

    /**
     * 类型状态（0正常 1停用）
     */
    private String status;

//    /**
//     * 租户ID
//     */
//    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;



    /**
     * 经度
     */
    @Excel(name = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @Excel(name = "纬度")
    private String latitude;

    /**
     * 高程
     */
    @Excel(name = "高程(m)")
    private String elevation;

    @Excel(name = "管理员")
    private String admin;

    @Excel(name = "联系电话")
    private String tel;

    /**
     * 备注
     */
    @Excel(name = "描述")
    private String remark;

    /**
     * 地址
     */
    @Excel(name = "详细地址")
    private String addr;


    /**
     * 省份
     */
    private String provinceName;


    /**
     * 地市
     */
    private String cityName;

    private static final long serialVersionUID = 1L;


    /**
     * 绘制资源范围
     */
    private String geometry;

    /**
     * 绘制资源颜色
     */
    private String color;
}