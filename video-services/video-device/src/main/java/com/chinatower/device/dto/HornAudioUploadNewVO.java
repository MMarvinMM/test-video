package com.chinatower.device.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;


/**
 * 广播上传音频接口实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HornAudioUploadNewVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "音频场景")
    private String audioScene = "Broadcast";

    @ApiModelProperty(value = "音频描述")
    private String desc;

    @ApiModelProperty(value = "音频文件")
    private String multipartFile;

    @ApiModelProperty(value = "区域编码")
    private String regionCode;

    @ApiModelProperty(value = "接入节点")
    private String accessNode;

    @ApiModelProperty(value = "受理节点")
    private String acceptNode;

}
