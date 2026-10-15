package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class AlgorithmGiveConfigUpdateVO extends BaseDataScopeEntity {

    @NotNull(message = "主键不能为空")
    @ApiModelProperty(value = "主键", required = true)
    private int id;

    @NotNull(message = "是否二次研判不能为空")
    @ApiModelProperty(value = "是否二次研判 0：是  1：否", required = true)
    private String secondSwitch;

    @ApiModelProperty(value = "算法编码", required = true)
    private String algorithmCode;

    @ApiModelProperty(value = "算法名称", required = true)
    private String algorithmName;

    /** 黑白名单集合 */
    private Map<String, List<BlackWhiteVO>> blackWhiteList;


    /**
     * 规则
     */
    private TriggerRuleJson triggerRule;
}
