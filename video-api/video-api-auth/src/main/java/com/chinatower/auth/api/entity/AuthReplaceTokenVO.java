package com.chinatower.auth.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.model.dto.LoginUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class AuthReplaceTokenVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平台编码
     */
    @NotBlank(message = "平台编码不能为空")
    private String platformCode;

    /**
     * 切换的客户编码
     */
    private String userId;

    /**
     * 切换的客户账号
     */
    @NotBlank(message = "客户账号不能为空")
    private String userName;

    /**
     * 铁塔账号信息
     */
    private LoginUser towerUser;

    /**
     * 切换的行业版本编码
     */
    @NotBlank(message = "行业版本编码不能为空")
    private String chooseAppVerCode;

    /**
     * 切换的行业编码
     */
    @NotBlank(message = "行业编码不能为空")
    private String chooseTradeId;

    @NotBlank(message = "行业名称不能为空")
    private String industryName;
}
