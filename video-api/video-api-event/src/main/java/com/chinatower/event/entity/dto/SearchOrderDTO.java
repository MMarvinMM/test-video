package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BasePageDTO;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: SearchOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/12 20:24
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class SearchOrderDTO extends BasePageDTO {

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 市编码
     * @required true
     */
    private String cityId;

    /**
     * 区编码
     * @required false
     */
    private String countyId;

    /**
     * 乡镇编码
     * @required false
     */
    private String townCode;

    /**
     * 场景（摄像机标签ID List）
     * @required false
     */
    private List<String> scene;

    /**
     * 所选组织ID List
     * @required false
     */
    private List<String> organization;

    /**
     * 纬度
     * @required false
     */
    private String latitude;

    /**
     * 经度
     * @required false
     */
    private String longitude;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    private String isQuery;

    private List<String> countyIdList;

    private String tableName;

    private String cityDate;

    /**
     * 查询几天内的数据
     */
    private String dayNum;

    /**
     * 自定义时间段：开始时间 年月日
     * @required false
     */
    private String webStartDate;

    /**
     * 自定义时间段：结束时间 年月日
     * @required false
     */
    private String webEndDate;

    /**
     * 设备编码集合
     * @required false
     */
    private List<String> deviceCode;

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
     * 受理状态（1：待受理）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 研判结果多选   0误报  1真警  2重复告警
     * */
    private List<String> judgeResultList;

    /**
     * 网格id批量
     *
     */
    private List<String> gridIdList;

    /**
     * 订单id批量
     *
     */
    private List<String> warningOrderIdList;
}
