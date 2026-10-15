package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkageAlgorithmVO {
    /** 联动算法编码 */
    private String linkageAlgorithmCode;
    /** 配置参数集合 */
    private List<ModelConfigVO> modelConfig;
}
