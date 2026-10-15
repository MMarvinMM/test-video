package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkageAlgorithmMappingVO {
    /** 算法编码 */
    private String algorithmCode;
    /** 算法名称 */
    private String algorithmName;
    /** 联动算法编码 */
    private String linkageAlgorithmCode;
    /** 联动算法名称 */
    private String linkageAlgorithmName;
}
