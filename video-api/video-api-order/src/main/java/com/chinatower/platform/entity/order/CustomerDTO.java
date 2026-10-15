package com.chinatower.platform.entity.order;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName CustomerDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/27 14:35
 * @Version 1.0
 **/

@Data
public class CustomerDTO implements Serializable {
    
    /**
     * 客户编码
     * @required true
     */
    @NotNull(message = "客户编码不能为空")
    private String tenantId;

    /**
     * 设备编码
     * @required true
     */
    @NotNull(message = "设备编码不能为空")
    private String deviceCode;

    /**
     * 行业编码
     * @required false
     */
    private String industryCode;

    /**
     * 算法编码
     */
    private String algorithmName;

    /**
     * 是否包含大模型算法 0:否（默认） 1：是
     */
    private int isHasBigModel;
}
