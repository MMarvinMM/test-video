package com.chinatower.receive.vo.entity;

import lombok.Data;

/**
 * @Description 查询告警流程环节参数 业务对象
 * @Author guyuefei
 * @Date 2022/7/25
 * @Version release_v1.5
 **/
@Data
public class VmFlowLinkSearchDto {
    /** 模板ID */
    private Long templateId;
    /** 流程配置id */
    private Long alarmFlowId;
    /** 当前环节Id */
    private Integer linkId;
    /**
     * 标识该流程模板是否为行业默认模板   1 是 0否
     */
    private String signFlag;
    /** 行业编码 */
    private String industryCode;
    /** 版本编码 */
    private String appVerCode;

    /**
     * 真实环节类型
     */
    private Long realLinkType;
}
