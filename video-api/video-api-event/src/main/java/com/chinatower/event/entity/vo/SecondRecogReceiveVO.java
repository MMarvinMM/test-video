package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 二次识别配置参数
 * @author cuisj
 * @date 2023-12-26
 * @requestID 二次识别配置相关接口
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogReceiveVO extends BaseDataScopeEntity implements Serializable {

    /**
     * id主键
     */
    private Integer id;

    /**
     * 二次识别配置名称
     */
    private String secondRecogName;

    /**
     * 告警来源
     */
    private String alarmSourceValue;


    /**
     * 告警类型
     */
    private String alarmTypeValue;

    /**
     * 联动摄像机
     */
    private String linkageCamera;

    /**
     * 联动动作 0:抓图 1:抓视频
     */
    private String linkageAction;

    /**
     * 选择算法
     */
    private String selectAlgorithm;

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
