package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BasePageDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：告警事件大屏入参
 * <p>
 * 类名称: BigScreenOrderListDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/11 09:20
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class BigScreenOrderListDTO extends BasePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     * @required true
     */
    private String userId;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 角色ID
     * @required false
     */
    private String roleId;

    /**
     * 组织ID
     * @required false
     */
    private String orgId;

    /**
     * 查询几天内的数据
     * @required false
     */
    private String dayNum;

    /**
     * 告警类型ID
     * @required false
     */
    private String warningTypeId;

    /**
     * 告警等级ID
     * @required false
     */
    private String emergencyLevel;

    /**
     * 告警状态
     * @required false
     */
    private String orderStatus;

    /**
     * 是否是管理员
     * @required false
     */
    private boolean isAdmin;

    /**
     * 告警标题
     * @required false
     */
    private String alarmBody;


    /**
     * 查询类型:0-全部；1待办;2已办
     * @required true
     */
    private String searchType;

    /**
     * 经度
     * @required false
     */
    private String longitude;

    /**
     * 纬度
     * @required false
     */
    private String latitude;

    /**
     * 摄像机编码
     * @required false
     */
    private String deviceCode;

    /**
     * 当前用户组织ID与上级所有组织ID
     * @required false
     */
    private List<String> orgIds;

    /**
     * 不包含的告警状态
     * @required false
     */
    private List<String> orderStatuses;

    /**
     * 封装用户待办SQL
     */
    private String orderStatusSql;

    /**
     * 自定义时间段：开始时间 年月日
     * @required true
     */
    private String webStartDate;

    /**
     * 自定义时间段：结束时间 年月日
     * @required true
     */
    private String webEndDate;

    /**
     * 告警重要等级 1:1级,2:2级
     * @required false
     */
    private String importanceLevel;

    /**
     * 来源：0铁塔 1租户
     * @required true
     */
    private String source;

    /**
     * 收藏创建人
     * @required false
     */
    private String collectionCreator;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 行业版本
     * @required false
     */
    private String appVerCode;

    /**
     * 环节配置和环节id
     * @required false
     */
    private String flowLinkIds;

    /**
     * 查询来源，BigScreen表示大屏
     * @required false
     */
    private String querySource;

    /**
     * 告警事件编码
     * @required true
     */
    private String warningOrderId;

    /**
     *  告警标签id列表
     *  @required false
     */
    private List<String> lableIdList;

    /**
     * 告警工单ID集合
     * @required false
     */
    private List<String> warningOrderIds;

    /**
     * 受理状态（1：待受理）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 设备编码集合
     * @required false
     */
    private List<String> deviceCodeList;

    /**
     * 告警状态集合
     * @required false
     */
    private List<String> orderStatusList;

    /**
     * 告警来源集合
     * @required false
     */
    private List<String> warningSourceList;

    /**
     * 告警类型集合
     * @required false
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码列表（实际触发，与 warningTypeIdList 为 AND）
     * @required false
     */
    private List<String> subSceneIdList;

    /**
     * 研判结果多选 0误报  1真警  2重复告警
     * */
    private List<String> judgeResultList;
}
