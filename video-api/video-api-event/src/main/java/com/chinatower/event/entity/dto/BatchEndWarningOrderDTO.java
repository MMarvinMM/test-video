package com.chinatower.event.entity.dto;

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

    /**
     * 需要完结的告警事件编码集合
     * @required true
     */
    @NotNull(message = "事件不能为空")
    private List<EndWarningOrderDTO> warningOrderList;

    /**
     * 租户ID
     * @required true
     */
    @NotNull(message = "tenantId不能为空")
    private String tenantId;

    /**
     * 行业编码
     * @required false
     */
    private String tradeId;

    /**
     * 行业应用版本
     * @required false
     */
    private String appVerCode;

    /**
     * 详细地址
     * @required false
     */
    private String address;

    /**
     * 经度
     * @required false
     */
    private String longitude;

    /**
     * 纬度
     * @required false
     */
    private String latitude;

    /**
     * 流程环节属性ID
     * @required false
     */
    private String attrId;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 受理时限
     * @required false
     */
    private String timeLimitAcceptance;

    /**
     * 下一环节是否开启受理模式（0:不开启 1:开启）
     * @required false
     */
    private String nextEnableAcceptanceMode;

    /**
     * 网格ID
     * @required false
     */
    private Long gridId;

    /**
     * 网格名称
     * @required false
     */
    private String gridName;

    /**
     * 水平方位角
     * @required false
     */
    private String horiAzimuthAngle;

    /**
     * 告警类型编码
     * @required false
     */
    private String warningTypeId;

    /**
     * 是否修改网格信息（1-是）
     * @required false
     */
    private String checkGridFlag;

    /**
     * 关闭说明
     */
    private String closeRemark;
}
