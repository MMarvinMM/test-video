package com.chinatower.event.entity.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.chinatower.common.core.dto.BasePageDTO;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmTypeDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/26 14:12
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmTypeDTO extends BasePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警类型ID
     */
    private String alarmTypeId;

    /**
     * 告警类型编码
     */
    private Long typeCode;

    /**
     * 类型名称
     * @required false
     */
    private String typeName;

    /**
     * 通知类型
     */
    private String noticeType;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 类型分类表ID
     */
    private String typeClassId;

    /**
     * 告警来源1、AI告警2、人工上报3、一键告警4.摄像机类型5、车辆卡口6、人脸卡口8、物联设备告警9、卫星告警10、雷达告警
     */
    private String source;

    /**
     * 应用版本编码集合
     */
    private List<String> appVerCodes;

    /**
     * 算法编码集合
     */
    private List<String> algorithmCodes;

    /**
     * 告警来源编码集合
     */
    private List<String> alarmSources;

    /**
     * 告警类型集合
     */
    private List<AlarmTypeDO> typeList;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 0-否（查询客户所订购且生效的）、1-是（查询所有的，用于回显）、2-查询所有（包括客户设置成不可见的）
     * @required false
     */
    private String isQueryAll;

    /**
     * 是否查询人工上报告警类型标志 （默认（不传）:查询 、 1 :不查询）
     * @required false
     */
    private String findFlag;

    /**
     * 是否只包含卫星火点
     */
    private Boolean onlySatelliteFirePointFlag;

    /**
     * 是否查询全部数据 true-不分页
     */
    private Boolean searchAllFlag;

    /**
     * 只过滤产生过告警事件的告警类型
     */
    private Boolean filterAlarmEvent;

    /**
     * 告警结束时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeEnd;

    /**
     * 告警开始时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeStart;

    /**
     * 告警类型编码列表
     */
    private List<String> typeValueList;

    /**
     * 是否包含算法模型配置
     */
    private Boolean withAlgorithmModelConfig;
}
