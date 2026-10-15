package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客户订购客户信息
 * @author guyuefei
 * @date 2022-05-12 10:20:28
 * @version v1.1
 */
@Data
public class OrderCustomerVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 客户编码/租户id */
    private String tenantId;
    /** 租户名称 */
    private String tenantName;
    /** 客户名称 */
    private String customerName;
    /** 运营研判模式：0:关闭 */
    private String isOperation;
    /** 行业版本编码 */
    private String appVerCode;

    /** 网格id */
    private String gridId;
    /** 网格名称 */
    private String gridName;
    private List<String> gridUserList;
    /** 网格层级 */
    private String gridLevel;
    /** 行业编码 **/
    private String industryCode;
    /** 设备编码 **/
    private String  deviceCode;
    /** 并行研判告警类型字符串 */
    private String parallelAlarmTypeStr;
    /** 串行研判告警类型字符串 */
    private String serialAlarmTypeStr;
    /** 运营研判模式 */
    private String operationType;
    /** 已配置运营研判的所有告警来源  -1代表全部 */
    private String operationAlarmSource;
    /** 定位点信息json格式 */
    private String points;
    /** 管线信息json格式 */
    private String  pipelines;

    /**
     * 研判节点
     */
    private String operationNode;

    /**
     * 并行研判节点
     */
    private String parallelOperationNode;

    /**
     * 研判组织
     */
    private String operationDept;

    /**
     * 并行研判组织
     */
    private String parallelOperationDept;

    /**
     * 水位告警阈值
     */
    private String triggerRule;
}
