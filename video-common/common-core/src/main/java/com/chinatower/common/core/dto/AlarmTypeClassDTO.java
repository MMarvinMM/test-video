package com.chinatower.common.core.dto;

import com.chinatower.common.core.vo.AlarmClassParamVO;
import com.chinatower.common.core.vo.AlarmClassTimeVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmTypeClassDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/24 20:11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmTypeClassDTO extends BaseModel implements Serializable {

    /**
     * 类型分类ID
     */
    private String typeClassId;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;

    /**
     * 告警类型ID
     */
    private String alarmTypeId;

    /**
     * 状态
     */
    private String state;

    /**
     * 告警类型数量
     */
    private Integer alarmTypeCount;

    /**
     * 通知类型
     */
    private String noticeMode;

    /**
     * 图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 来源
     */
    private String source;

    /**
     * 租户
     */
    private String tenantor;

    /**
     * 类型值
     */
    private String typeValue;

    /**
     * 告警等级
     */
    private String alarmLevel;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     * 城市ID
     */
    private String cityId;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 客户编码集合
     */
    private List<String> tenantList;

    /**
     * 组织等级
     */
    private String orgLevel;

    /**
     * 告警类型list
     */
    private List<AlarmType> typeList;

    /**
     * 时限配置list
     */
    private List<AlarmClassTimeVO> alarmClassTimeVOS;

    /**
     * 阈值配置
     */
    private AlarmClassParamVO alarmClassParam;

    /**
     * 告警类型标签id列表
     */
    private List<Long> tagIdList;

    /**
     * 是否通知客户管理员
     */
    private String notifyManager;

    /**
     * 通知环节
     */
    private String notificationLink;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 关联标签
     */
    private List<ModifyTagDTO> tagList;

    /*
     * 告警来源list
     */
    private List<String> alarmSources;

    /*
     * 行业编码
     */
    private String industryCode;

    /**
     * 是否可见
     */
    private String isVisible;

    /**
     * 排序字段
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;

    /**
     * 是否包含算法模型配置
     */
    private Boolean withAlgorithmModelConfig;

    /**
     * 大模型算法编码列表
     */
    private List<String> modelAlgorithmCodeList;
}
