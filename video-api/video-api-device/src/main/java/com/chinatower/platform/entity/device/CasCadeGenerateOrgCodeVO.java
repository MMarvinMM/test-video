package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

/**
 * 级联平台同步信息
 */
@Data
public class CasCadeGenerateOrgCodeVO implements Serializable {

    /**
     * 业务组织、虚拟组织编码规则：1-6位区域编码，
     * 7-8位：00，
     * 9-10位：行业编码，
     * 11-13:业务组织215，虚拟组织216，
     * 14位任意数字，15-20随机数字，编码在整个级联设备树唯一。
     */

    private String countyCode;

    /**
     * 类型 1-业务组织 2-虚拟组织
     */
    private Integer type;
}
