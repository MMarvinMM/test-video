package com.chinatower.platform.auth.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QrLoginReq {

    /**
     * 二维码唯一标识
     */
    @NotBlank(message = "二维码唯一标识不能为空")
    private String code;
}
