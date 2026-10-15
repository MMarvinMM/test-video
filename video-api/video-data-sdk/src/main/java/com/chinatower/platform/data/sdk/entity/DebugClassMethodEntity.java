package com.chinatower.platform.data.sdk.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DebugClassMethodEntity implements Serializable {

    private String classMethod;

    private String params;
}
