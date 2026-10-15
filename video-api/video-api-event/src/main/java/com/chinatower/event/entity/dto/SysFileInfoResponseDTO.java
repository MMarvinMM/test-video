package com.chinatower.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SysFileInfoResponseDTO",description = "文件信息返回参数")
public class SysFileInfoResponseDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     * @required true
     */
    @ApiModelProperty(value = "文件id")
    private String fileId; //唯一主键

    /**
     * 文件字节
     * @required true
     */
    @ApiModelProperty(value = "文件字节")
    private byte[] fileContent;

    /**
     * 文件大小
     * @required true
     */
    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    /**
     * 文件名称
     * @required true
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件路径
     * @required true
     */
    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    /**
     * 文件后缀
     * @required true
     */
    @ApiModelProperty(value = "文件后缀")
    private String fileSuffix;
}
