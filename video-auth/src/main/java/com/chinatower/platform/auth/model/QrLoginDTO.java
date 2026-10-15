package com.chinatower.platform.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrLoginDTO {

    /**
     * 二维码唯一标识
     */
    private String code;

    /**
     * 0 未扫码，1 扫码成功，2 登录成功，3 登录失败
     */
    @NotBlank(message = "状态码不能为空")
    private String status;

    /**
     * 用户ID加密串
     */
    @NotBlank(message = "用户编码不能为空")
    private String userId;

    /**
     * 二维码生成时间
     */
    private String createTime;

    /**
     * 节点编码
     */
    private String appLoginNode;

    private String chooseAppVerCode;

    private String chooseTradeId;

    /**
     * 属地节点token
     */
    private String token;

    public QrLoginDTO() {
    }

    public QrLoginDTO(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public QrLoginDTO(String code, String status, String appLoginNode) {
        this.code = code;
        this.status = status;
        this.appLoginNode = appLoginNode;
    }
}
