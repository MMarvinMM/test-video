package com.chinatower.system.api.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件信息对象 sys_file_info
 *
 * @author wangzic
 * @date 2020-04-03
 */
@Data
@ApiModel(value = "SysFileInfoResponse", description = "文件信息返回参数")
public class SysFileInfoResponse {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一主键
     */
    @ApiModelProperty(value = "文件id")
    private String fileId;

    /**
     * 文件字节
     */
    @ApiModelProperty(value = "文件字节")
    private byte[] fileContent;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    /**
     * 文件后缀
     */
    @ApiModelProperty(value = "文件后缀")
    private String fileSuffix;

}
