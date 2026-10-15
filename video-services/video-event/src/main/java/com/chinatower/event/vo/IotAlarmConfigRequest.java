package com.chinatower.event.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.event.dataobject.IotAlarmDeviceDo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className: IotAlarmConfigRequest
 * @description:
 * @author: yongqi.han
 * @date: 2022/9/6
 * @requestID 需求单号及需求名称
 */
@Data
public class IotAlarmConfigRequest  extends BaseDataScopeEntity implements Serializable {

    /**
     * 主键
     */
    private String id;

    /**
     * 物联告警配置名称
     */
    private String iotAlarmConfigName;

    /**
     * 配置方式
     */
    private String configMethod;

    /**
     * 启用状态
     */
    private String configStatus;


    /**
     * 租户名称
     */
    private String tenantName;


    /**
     * 应用版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 物联因子
     */
    private List<IotFactorVo> iotFactorVoList;

    /**
     * 物联设备
     */
    private List<IotAlarmDeviceDo> iotAlarmDeviceDoList;

    /**
     * 物联设备类型
     */
    private String deviceType;

    /**
     * 物联设备类型list
     */
    private List<String> deviceTypeList;

}
