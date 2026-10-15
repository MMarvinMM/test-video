package com.chinatower.event.dto;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: BatchEndWarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/30 16:07
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class BatchEndWarningOrderDTO extends BaseDataScopeEntity implements Serializable {

    //需要完结的ID列表
    @NotNull(message = "事件不能为空")
    private List<EndWarningOrderDTO> warningOrderList;

    //租户ID
    @NotNull(message = "tenantId不能为空")
    private String tenantId;

    //行业ID
    private String tradeId;

    //行业版本
    private String appVerCode;

    /**
     * 经纬度
     */
    private String longitude;
    private String latitude;
    /**
     * 网格id
     */
    private Long gridId;
    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 事发地
     */
    private String address;

    /**
     * 是否修改网格信息
     */
    private String checkGridFlag;

    /**
     * 告警类型
     */
    private String warningTypeId;

    /**
     * 重要等级
     */
    private String importanceLevel;

    /**
     * 水平方位角
     */
    private String horiAzimuthAngle;

    private String alarmReason;

    private String devicePitch;

    private String deviceHorizontal;

    /**
     * 关闭说明
     */
    private String closeRemark;
}
