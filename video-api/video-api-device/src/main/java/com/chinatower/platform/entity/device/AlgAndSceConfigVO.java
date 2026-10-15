package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgAndSceConfigVO {
    /** 配置编号 */
    String configId;
    /** 预置位ID */
    String presetId;

}
