package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @desription: 图层 详情
 * @data 创建时间：2023/4/7 10:29
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LayerInfoDTO {
    /**
     * 自定义图层名称
     */
    @ApiModelProperty(value = "自定义图层名称")
    private String customName;
    /**
     * 图层类别ID
     */
    @ApiModelProperty(value = "图层类别")
    private String layerTypeId;
    /**
     * 图层类别名称
     */
    @ApiModelProperty(value = "图层类别名称")
    private String layerTypeName;
    /**
     * 图层文件id
     */
    @ApiModelProperty(value = "图层文件id")
    private String fileId;
    /**
     * 图层文件名称
     */
    @ApiModelProperty(value = "图层文件名称")
    private String fileName;

    private String category;

    private String nodeCode;

    private String nodeName;

    private String nestFlag;

    private String fileUrl;

    private String notes;

    /**
     * 上传类型 1:图层链接；2上传文件
     */
    private String uploadType;

    /**
     * 发布状态 0:发布中；1发布成功：2发布失败
     */
    private String publishStatus;

    /**
     * 上传obs地址
     */
    private String obsUrl;

    private String originUrl;


}
