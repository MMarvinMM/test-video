package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 级联平台同步信息
 */
@Data
public class CasCadeDeleteVO implements Serializable {
    /**
     * 平台id数组
     */
    private List<String> platAsIds;
}
