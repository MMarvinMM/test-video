package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.platform.entity.device.AnalysisPeriodVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author zhaozj
 * @version 1.6.10
 * @date 2023/08/02 20:29
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceOptAuthVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 8609576632624208259L;
    //唯一编码
    private String configId;
    //租户编码
    private String tenantId;
    //设备编码
    private String deviceCode;
    //行业编码
    private String industryCode;
    //配置类型：1铁塔2租户
    private String configType;
    //用户编码
    private String userId;
    //用户类型（0系统用户 2租户）
    private String userType;
    //优先级：1高2中3低
    private String priority;
    //操控时间
    private String operatePeriod;
    private String presetOperatePeriod;
    private String patrolOperatePeriod;
    private String guardOperatePeriod;

    //操控时间段集合
    private List<String> operatePeriodList;
    //云台操控时间段
    private List<String> cameraOperatePeriodList;
    //预置位操控时间段
    private List<String> presetOperatePeriodList;
    //巡航计划操控时间段
    private List<String> patrolOperatePeriodList;
    //看守卫操控时间段
    private List<String> guardOperatePeriodList;
    //设备编码集合
    private List<String> deviceCodeList;

    private List<String> userIdList;

    private Set<String> userIdSet;

    private Set<String> tenantIdList;

    private Set<String> industryCodeList;

    private List<String> deptIdList;

    //登录名
    private String userName;
    //用户名
    private String nickName;
    //用户状态0正常 1停用
    private String status;

    //云台权限状态
    private String cameraStatus;
    // 云台优先级
    private String cameraPriority;

    //预置位权限状态
    private String presetStatus;

    //预置位优先级
    private String presetPriority;

    //巡航计划权限状态
    private String patrolStatus;
    //巡航计划优先级
    private String patrolPriority;

    //看守卫权限状态
    private String guardStatus;
    //看守卫优先级
    private String guardPriority;

    private String deptName;
    private String deptId;
    protected Integer pageNum;
    protected Integer pageSize;


}
