package com.chinatower.event.entity.dto;

import java.io.Serializable;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * 告警压制配置
 * <p>
 * 类名称: InterceptConfigDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/5/12 10:24
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class InterceptConfigDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 设备编号
     * @required true
     */
    private String deviceCode;

    /**
     * 告警类型
     * @required true
     */
    private String warningTypeId;

    /**
     * 压制时长 单位：分钟
     * @required true
     */
    private int interceptTime;

    /**
     * 行业版本编码列表
     */
//    private List<String> appVerCodeList;
}
