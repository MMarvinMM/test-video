package com.chinatower.platform.entity.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderAlgorithmConfigVO implements Serializable {

    private String deviceCode;

    private String algorithmCode;

    private String algorithmName;
}
