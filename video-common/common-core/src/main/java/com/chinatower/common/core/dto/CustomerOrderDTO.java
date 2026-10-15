package com.chinatower.common.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.chinatower.common.core.utils.JsonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客户订购关系.
 *
 * @author: fangjun
 */
@Data
public class CustomerOrderDTO implements Serializable {

    /**
     * 订购编码.
     */
    private String orderCode;

    /**
     * 客户编码.
     */
    private String customerCode;

    /**
     * 订购关系列表.
     */
    private List<OrderParamDTO> orders;

    /**
     * true：有效.
     */
    private Boolean enabled;

    /**
     * 生效时间，为空表示当前立即生效.
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validTime;

    /**
     * 过期时间.
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invalidTime;

    public String toJson() {
        try {
            return JsonUtils.toJson(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
