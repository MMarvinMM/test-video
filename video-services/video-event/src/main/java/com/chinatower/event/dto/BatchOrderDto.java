package com.chinatower.event.dto;

import lombok.Data;

@Data
public class BatchOrderDto {

    private JudgeWarningOrderDTO judgeWarningOrderDTO;
    private String tenantId;
    private BatchJudgeWarningOrderDTO dto;
}
