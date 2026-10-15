package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString
public class CheckpointConditionVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = -1627413503664728307L;

    @ApiModelProperty(value = "卡口摄像机编码")
    @NotNull(message = "卡口摄像机编码不能为空")
    /**
     * 卡口摄像机编码
     * @required true
     */
    private String deviceCode;

    /**
     * 卡口设备编码集合
     * @required false
     */
    private List<String> validDeviceCodes;

}
