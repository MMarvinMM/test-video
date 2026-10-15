package com.chinatower.event.entity.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderLayerDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/12 15:12
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningOrderLayerDTO extends CommonFeignEntity implements Serializable {

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
     * 查询半径（公里）
     * @required false
     */
    private String radius;

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
     * 告警来源批量查询
     * @required false
     */
    private List<String> warningSourceList;

    /**
     * 告警状态批量查询
     * @required false
     */
    private List<String> orderStatusList;

    /**
     * 告警类型批量查询
     * @required false
     */
    private List<String> warningTypeIdList;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 县编码
     * @required false
     */
    private String countyId;

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 网格id
     * @required false
     */
    private String gridId;

    /**
     * 条数限制
     */
    private int limitCount;

     /**
      * 关键字检索
      * */
    private String keyword;
}
