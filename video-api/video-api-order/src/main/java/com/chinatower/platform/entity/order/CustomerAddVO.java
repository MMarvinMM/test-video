package com.chinatower.platform.entity.order;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CustomerAddVO implements Serializable {
    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private String customerCode;
    /**
     * 客户名称
     */
    @NotNull(message = "客户名称不能为空")
    private String customerName;
    /**
     * 客户账号
     */
    @NotNull(message = "客户账号不能为空")
    private String admin;
    /**
     * 所属省份
     */
    @NotNull(message = "所属省份不能为空")
    private String cusRegistryProvince;
    /**
     * 所属地市
     */
    @NotNull(message = "所属地市不能为空")
    private String cusRegistryCity;
    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    private String phoneNum;
    /**
     * 所属管理区域
     */
    private String admArea;

}
