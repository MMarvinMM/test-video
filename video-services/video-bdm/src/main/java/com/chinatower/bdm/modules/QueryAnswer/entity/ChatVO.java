package com.chinatower.bdm.modules.QueryAnswer.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatVO implements Serializable {

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "picId不能为空")
    private String picId;

    @NotBlank(message = "类型不能为空")
    private String type;

    private String imgUrl;
}
