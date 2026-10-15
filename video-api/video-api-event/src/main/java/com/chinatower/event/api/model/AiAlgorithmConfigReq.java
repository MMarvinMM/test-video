package com.chinatower.event.api.model;

import lombok.Data;

/**
 * 针对告警来源为“AI告警” 列表
 *      * 在“事件描述”字段中展示相应算法配置的参数
 *      * （即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
 *
 * @author lisy
 */
@Data
public class AiAlgorithmConfigReq {

    /**
     * 工单ID别名
     */
    private String orderIdAlias;

    /**
     * 工单状态别名
     */
    private String orderSourceAlias;

    /**
     * 任务ID别名
     */
    private String taskIdAlias;

    /**
     * 工单状态别名
     */
    private String teanatIdAlias;

    /**
     * 相机编码别名
     */
    private String deviceCodeAlias;

    /**
     * 通道编码别名
     */
    private String channelCodeAlias;

    /**
     * 算法编码别名
     */
    private String algorithmCodeAlias;

    /**
     * 传入参数 格式
     *      *                       [{
     *      *                          "warningOrderId": "", //工单编号
     *      *                          "taskId": "",       //任务ID
     *                                 "tenantId":""  //租户ID
     *      *                       }]
     */
    private Object params;

}
