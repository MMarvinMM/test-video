package com.chinatower.platform.entity.device;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 级联平台绑定摄像机下发信息
 */
@Data
public class CasCadeBindVO implements Serializable {
    /**
     * 是否开启
     */
    @NotNull(message = "级联状态不能为空")
    private String cascadeStatus;
    /**
     * 租户id
     */
    @NotNull(message = "租户编码不能为空")
    private String customerCode;

    /**
     * 租户名称
     */
    private String customerName;

    /**
     * 级联平台修改人
     */
    private String cascadeModifier;

    /**
     * 级联平台修改人名称
     */
    private String cascadeModifierName;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 设备信息列表
     */
    private List<CasCadeBindPlatInfoVO> platInfoList;
}
