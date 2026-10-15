package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物联设备联动配置表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Data
public class TenantIotDeviceConfVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物联设备联动ID
     */
    private Long iotDeviceConfId;

    /**
     * 物联设备编码
     */
    @NotBlank(message = "物联设备编码不能为空")
    @ApiModelProperty(value = "物联设备编码", required = true)
    private String deviceCode;

    /**
     * 租户编码
     */
    @NotBlank(message = "租户编码不能为空")
    @ApiModelProperty(value = "租户编码", required = true)
    private String tenantId;

    /**
     * 触发条件,逗号分隔1:发生告警
     */
    private String triggerCond;

    /**
     * 联动动作,逗号分隔1:摄像机看向告警位置2:抓图3:抓视频
     */
    private String linkAction;

    /**
     * 联动摄像机类型1:指定摄像机2:最近的摄像机3:周边指定范围摄像机
     */
    private String linkConfType;

    /**
     * 1:转到预置位2:转到经纬度
     */
    private String linkConfSubType;

    /**
     * 联动摄像机距离
     */
    private String linkDistance;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 关联配置信息
     */
    List<TenantIotConfRelVO> confRels;

    /**
     * 设备编码列表
     */
    private List<String> deviceCodeList;
    //告警上报传 1
    private String alarmReporting;
    //根据租户批量查询
    private List<String> tenantIdList;

    // 查询物联级联配置条件 - 内部使用
    private List<Long> iotDeviceConfIdList;
}
