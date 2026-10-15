package com.chinatower.industry.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Created by zheng.yan on 2022/3/22.
 */
@Data
public class RadarDeviceReqDTO extends CommonFeignEntity {
    /**
     * 设备编码
     */
    @ApiModelProperty(value = "雷达id", required = true)
    @NotEmpty(message = "雷达id不能为空")
    private List<String> radarIds;
}
