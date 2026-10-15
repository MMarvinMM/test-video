package com.chinatower.platform.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmGiveConfigSaveVO extends BaseDataScopeEntity {

    @ApiModelProperty(value = "主键", required = true)
    private int id;

    @NotNull(message = "设备编码不能为空")
    @ApiModelProperty(value = "设备编码", required = true)
    private String deviceCode;

    @NotNull(message = "设备名称不能为空")
    @ApiModelProperty(value = "设备名称", required = true)
    private String deviceName;

    @NotNull(message = "通道编码不能为空")
    @ApiModelProperty(value = "通道编码", required = true)
    private String channelCode;

    @NotNull(message = "通道名称不能为空")
    @ApiModelProperty(value = "通道名称", required = true)
    private String channelName;

    @NotNull(message = "是否二次研判不能为空")
    @ApiModelProperty(value = "是否二次研判 0：是  1：否", required = true)
    private String secondSwitch;

    @NotNull(message = "告警类型不能为空")
    @ApiModelProperty(value = "告警类型", required = true)
    private String alarmType;

    @NotNull(message = "告警类型名称不能为空")
    @ApiModelProperty(value = "告警类型名称", required = true)
    private String alarmTypeName;

    @ApiModelProperty(value = "算法编码", required = true)
    private String algorithmCode;

    @ApiModelProperty(value = "算法名称", required = true)
    private String algorithmName;

    /*算法任务状态：0:启用 1:停用*/
    private String status;

    /** 创建人 */
    private String creatorName;
    /** 更新人 */
    private String modifierName;

    /** 黑白名单集合 */
    private Map<String, List<BlackWhiteVO>> blackWhiteList;

    /**
     * 规则
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private TriggerRuleJson triggerRule;

    private String triggerRuleStr;
}
