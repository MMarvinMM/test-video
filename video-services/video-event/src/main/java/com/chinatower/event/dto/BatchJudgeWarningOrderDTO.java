package com.chinatower.event.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: BatchJudgeWarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/31 14:53
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class BatchJudgeWarningOrderDTO extends BaseDataScopeEntity implements Serializable {

    private List<JudgeWarningOrderDTO> warningOrderList;
    //租户ID
    @NotNull(message = "tenantId不能为空")
    private String tenantId;

    //行业ID
    private String tradeId;

    //行业版本
    private String appVerCode;

    /**
     * 研判说明
     */
    private String remark;

    /**
     * 研判结果0：假警  1：真警
     */
    private String judgeResult;
}
