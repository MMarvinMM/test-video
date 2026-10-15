package com.chinatower.common.core.dto;

import com.chinatower.common.entity.CommonLog;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class BuryPointDTO extends CommonLog {

    private String clientName;

    private String tenantId;

    private String appVerCode;

    private String industryCode;

    /**
     * 耗时 毫秒
     */
    private Long spendTime;
}
