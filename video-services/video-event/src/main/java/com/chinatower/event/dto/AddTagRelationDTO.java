package com.chinatower.event.dto;

import com.chinatower.common.core.dto.ModifyTagDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName AddTagRelationDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/4/26 17:20
 * @Version 1.0
 **/
@Data
@ApiModel(value = "新增告警类型标签关联对象",description = "新增告警类型标签关联对象")
public class AddTagRelationDTO implements Serializable {
    
    @NotBlank(message = "告警类型ID不能为空")
    @ApiModelProperty(value = "告警类型ID")
    private String alarmTypeId;
    
    @NotBlank(message = "告警类型值不能为空")
    @ApiModelProperty(value = "告警类型值")
    private String typeValue;

    @ApiModelProperty(value = "告警等级")
    private String alarmLevel;

    @Valid
    @Size(max = 20, message = "关联标签超过20个")
    private List<ModifyTagDTO> tags;
}
