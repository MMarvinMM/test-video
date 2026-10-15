package com.chinatower.platform.entity.device;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName FindDeviceCodeVO
 * @Author chenmf
 * @Date 2022/3/5 10:19
 * @Version 1.0
 **/
@Data
@ApiModel(value = "根据行业版本编码查询设备信息实体",description = "根据行业版本编码查询设备信息实体")
public class PageDeviceVO extends CommonFeignEntity implements Serializable {

    /**
     * 行业版本编码
     */
    @NotBlank(message = "行业版本编码不能为空")
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页展示条数
     */
    private Integer pageSize;
    
}
