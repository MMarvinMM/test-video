package com.chinatower.common.core.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 流程请求参数接收类
 * @author chmod
 * @version 1.0
 * @date 2022/3/2 11:19
 */
@Data
public class FlowReq implements Serializable {
    private static final long serialVersionUID = -6771154910566147049L;

    /**
     * 租户ID
     * @required true
     */
    @NotBlank(message = "租户Id不能为空")
    public String tenantId;

    /**
     * 告警来源
     * @required false
     */
    public String warningSource;
}
