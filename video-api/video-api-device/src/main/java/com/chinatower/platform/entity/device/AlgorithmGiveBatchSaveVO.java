package com.chinatower.platform.entity.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlgorithmGiveBatchSaveVO implements Serializable {

    private String tenantId;

    private String industryCode;

    private List<AlgorithmGiveConfigSaveVO> list;
}
