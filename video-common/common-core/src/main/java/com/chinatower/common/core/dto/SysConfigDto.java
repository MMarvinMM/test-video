package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenxinhui
 * @Date 2021/11/1
 */
@Data
public class SysConfigDto implements Serializable {
    private String configName;
    private String configKey;
    private String configValue;
}
