package com.chinatower.platform.entity.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlgorithmIntersectionVO implements Serializable {

    /**
     * 客户编码
     * @required true
     */
    private String tenantId;

    /**
     * 设备编码
     * @required true
     */
    @NotNull(message = "设备编码不能为空")
    private List<String> deviceCodes;

    /**
     * 行业编码
     * @required false
     */
    private String industryCode;

    private String acceptNode;

}
