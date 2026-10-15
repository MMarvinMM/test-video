package com.chinatower.auth.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxColonyCodeVo implements Serializable {

    private String encStr;

    private String unionId;

    private String openId;

    private String miniOpenId;

    private String colonyCode;

}
