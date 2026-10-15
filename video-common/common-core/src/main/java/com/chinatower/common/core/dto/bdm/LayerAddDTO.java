package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @desription: 图层新增
 * @data 创建时间：2023/4/7 9:52
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerAddDTO {
    /**
     * 数据ID
     */
    @ApiModelProperty(value = "数据ID")
    private String id;
    /**
     * 图层名称
     */
    @ApiModelProperty(value = "图层名称")
    @NotBlank(message = "图层名称不能为空")
    @Length(max = 200,message = "图层名称限制最多支持输入200个文字")
    private String customName;
    /**
     * 图层类别ID
     */
    @ApiModelProperty(value = "图层类别")
    private String layerTypeId;

    private String fileUrl;
    private String fileName;

    private int isPg = 1;
    private String layerTypeSimple;
    private String areaRef;

    /**
     * 图层种类 1-矢量  2-非矢量
     */
    private String category;

    private String notes;

    /**
     * 上传类型 1:图层链接；2上传文件
     */
    private String uploadType;

    /**
     * 上传obs地址
     */
    private String obsUrl;

    /**
     * 上传originUrl文件地址
     */
    private String originUrl;
    /**
     * 是否是geojson 上传
     */
    private Boolean geoJsonFlag = Boolean.FALSE;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 行业编码
     */
    private String industryCode;
}
