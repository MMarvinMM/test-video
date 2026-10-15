package com.chinatower.event.dto;

import com.chinatower.event.entity.dto.ModifyLableDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: JudgeWarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/31 15:01
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class JudgeWarningOrderDTO implements Serializable {
    //告警ID
    private String warningOrderId;

    /**
     * 流程ID
     */
    @NotNull(message = "flowId不能为空")
    private String flowId;

    /**
     * 当前环节ID
     */
    @NotNull(message = "currentLink不能为空")
    private String currentLink;

    private String gridId;

    //省编码
    private String provinceId;

    //市编码
    private String cityId;

    //县编码
    private String countyId;

    //告警时间
    private String alarmTime;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String warningSource;

    /**
     * 行业版本
     */
    private String tradeVerCode;

    /**
     * 行业版本编码
     */
    private String industryCode;

    private String NextEnableAcceptanceMode;

    private String warningTypeId;

    /**
     * 标签集合
     * @required true
     */
    @Valid
    @Size(max = 20, message = "关联标签超过20个")
    private List<ModifyLableDTO> lables;

}
