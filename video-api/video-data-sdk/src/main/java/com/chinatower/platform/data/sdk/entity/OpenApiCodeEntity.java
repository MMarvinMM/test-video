package com.chinatower.platform.data.sdk.entity;

import com.chinatower.common.entity.Result;
import lombok.Data;

import java.io.Serializable;

@Data
public class OpenApiCodeEntity implements Serializable {

    /**
     * java调用示例
     */
    private String javaCodeExample;

    /**
     * pom引入
     */
    private String inductPom;


    private Result result;
}
