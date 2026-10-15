package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 二次识别配置参数
 * @author cuisj
 * @date 2023-12-26
 * @requestID 二次识别配置相关接口
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmTypeSecondRecogVO implements Serializable {


    private Integer id;

    /**
     * 二次识别配置ID
     */
    private String alarmSecondRecogId;

    /**
     * 告警类型
     */
    private String alarmTypeValue;

    /**
     * 告警来源
     */
    private String alarmSourceValue;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 删除标识 0正常  1删除
     */
    private Integer deleteFlag;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业应用版本
     */
    private String appVerCode;
}
