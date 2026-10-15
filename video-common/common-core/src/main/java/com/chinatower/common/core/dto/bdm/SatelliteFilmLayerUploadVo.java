package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @desription: 卫片图层上传
 * @data 创建时间：2024/5/6 14:31
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SatelliteFilmLayerUploadVo {
    /**
     * 图层名称
     */
    @ApiModelProperty(value = "图层名称")
    @NotBlank(message = "图层名称不能为空")
    @Length(max = 20,message = "图层名称限制最多支持输入20个文字")
    private String customName;
    /**
     * 图层类别ID
     */
    @ApiModelProperty(value = "图层类别")
    private String layerTypeId;
    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String fileUrl;
    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    private String layerTypeSimple;
}
