package com.chinatower.bdm.api.entity;

import java.io.Serializable;

import java.util.List;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 功能说明：资源实体类,保存资源信息与数据库 resource_conf 表对应。
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceConf extends BaseDataScopeEntity implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 资源编码
     */
    private String resourceId;

    /**
     * 资源名称
     */
    @ApiModelProperty(value = "资源名称")
    @NotEmpty(message = "资源名称不能为空")
    private String resourceName;

    /**
     * 资源类型编码
     */
    @NotEmpty(message = "资源类型不能为空")
    private String resourceTypeId;

    /**
     * 显示顺序
     */
    private Long orderNum;

    /**
     * 类型状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
//    private short deleteFlag;


//    /**
//     * 租户ID
//     */
//    private String tenantId;
//
    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    @NotEmpty(message = "经度不能为空")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    @NotEmpty(message = "纬度不能为空")
    private String latitude;

    /**
     * 高程
     */
    private String elevation;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    @NotEmpty(message = "详细地址不能为空")
    private String addr;

    /**
     * 省份
     */
    private String provinceName;


    /**
     * 地市
     */
    private String cityName;

    /**
     * 资源类型名称
     */
    @ApiModelProperty(value = "资源类型名称")
    private String resourceTypeName;


    /**
     * 范围半径
     */
    @ApiModelProperty(value = "范围半径")
    private String radius;


    /**
     * 图片地址
     */
    @ApiModelProperty(value = "图片地址")
    private String icon;

    /**
     * 经度查询参数
     */
    @ApiModelProperty(value = "经度查询参数")
    private String longitudeParam;


    /**
     * 纬度查询参数
     */
    @ApiModelProperty(value = "纬度查询参数")
    private String latitudeParam;


    /**
     * 资源类型集合
     */
    @ApiModelProperty(value = "资源类型集合")
    private List<String> resourceTypeList;


    /**
     * 资源位置到中心点距离
     */
    @ApiModelProperty(value = "资源位置到中心点距离(米)")
    private String distance;
    
    /**
     * 行业编码
     */
    private String industryCode;
    
    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 图片url
     */
    private String imgUrl;

    /**
     * 视频url
     */
    private String videoUrl;


    /**
     * 资源类型层级路径
     */
    @ApiModelProperty(value = "资源类型层级路径")
    private String typeLevelUrl;
    private Integer pageNum=1;
    private Integer pageSize=10;
    private static final long serialVersionUID = 1L;

    private String prop;
    private String order;

    /**
     * 资源属性:点资源1、线资源2、面资源3
     */
    private String resourceProperty;

    private String admin;

    private String tel;

    private String desTel;

    /**
     * 绘制资源范围
     */
    private String geometry;

    /**
     * 绘制资源颜色
     */
    private String color;

    private Integer isMonitor;

    /**
     * 是否需要分页，0：不分页，1：分页
     */
    private String isPage;
}
