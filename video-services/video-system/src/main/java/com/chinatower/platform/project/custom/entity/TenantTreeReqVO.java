package com.chinatower.platform.project.custom.entity;



import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import java.util.List;

@Data
public class TenantTreeReqVO extends BaseDataScopeEntity {
    private String provinceId;
    private String cityId;
    private String customerCode;
    private String isOpen;
    private String customerStatus;
    private String isOperation;
    private String alarmLocation;
    private List<String> operationList;
    private List<String> alarmLocationList;
    private String tenantManager;
    private String[] isOperations;


    /** api创建开始时间 格式：yyyy-MM-dd*/
    private String apiCreateBeginTime;

    /** api创建结束时间 格式：yyyy-MM-dd*/
    private String apiCreateEndTime;

    /** api创建人*/
    private String apiCreateBy;

    private String operationModifier;
    private String operationModifierDate;
    private String openModifier;
    private String openModifierDate;
    private String operationModifierDateStart;
    private String operationModifierDateEnd;
    private String openModifierDateStart;
    private String openModifierDateEnd;
    private String operationAlarmTypeStr;
    private List<String> tenantIds;

    private Integer pageNum;
    private Integer pageSize;

    /**
     * 级联状态
     */
    private String cascadeStatus;

    /**
     * 级联操作人
     */
    private String cascadeModifier;

    /**
     * 级联操作时间
     */
    private String cascadeModifierDateStart;
    private String cascadeModifierDateEnd;

    /**
     * 编号 查询字段
     */
    private String locationCode;

    private List<String> tenantIdList;

    private String tenantId;

    private String industryCode;

    /**
     * 当前登录节点
     */
    private String currentLocationCode;

    private String orderFlag;

    /**
     * 查询未开启运营研判标志
     */
    private Boolean operationFlag = false;

    /**
     * 是否中心节点标志
     */
    private Boolean isCenterNodeFlag = false;

    /**
     * 级联平台编码
     */
    private String platCode;
}
