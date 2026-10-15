package com.chinatower.cascade.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hued
 * @date 2023/10/24
 * @requestID
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlatformDataVO implements Serializable {

    private static final long serialVersionUID = -4379048213120041725L;
    /**
     * 当前属地名称
     */
    private String platformName;
    /**
     * 属地编码
     */
    private String platformCode;

    @NotBlank(message = "关键字不能为空")
    private String key;
}
