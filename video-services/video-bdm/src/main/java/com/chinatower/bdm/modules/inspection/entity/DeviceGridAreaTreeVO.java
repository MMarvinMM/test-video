package com.chinatower.bdm.modules.inspection.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/2 16:09
 */
@Data
@ToString
public class DeviceGridAreaTreeVO implements Serializable {
    private static final long serialVersionUID = 2936316192567989878L;

    private String code;
    private String name;
    private String level;
    private List<DeviceGridAreaTreeVO> children;
}
