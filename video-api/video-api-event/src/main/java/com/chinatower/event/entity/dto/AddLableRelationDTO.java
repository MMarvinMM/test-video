package com.chinatower.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName AddTagRelationDTO
 * @Deacription
 * @Author yhb
 * @Date 2022/12/29 17:20
 * @Version 1.0
 **/
@Data
@ApiModel(value = "新增告警标签关联对象",description = "新增告警标签关联对象")
public class AddLableRelationDTO implements Serializable {

    /**
     * 告警事件编码
     * @required true
     */
    @ApiModelProperty(value = "告警ID")
    private String waringOrderId;

    /**
     * 告警事件编码
     * @required true
     */
    @ApiModelProperty(value = "告警ID集合")
    private List<String> waringOrderIdList;


    /**
     * 标签id
     * @required true
     */
    private List<Long> lableIdList;

    /**
     * 标签集合
     * @required true
     */
    @Valid
    @Size(max = 20, message = "关联标签超过20个")
    private List<ModifyLableDTO> lables;
}
