package com.chinatower.auth.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhiteIpDto implements Serializable {

    private String userId;

    private String whiteIpFlag;

    private String ipStart;

    private String ipEnd;
}
