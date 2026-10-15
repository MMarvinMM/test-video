package com.chinatower.industry.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfoQueryVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 距离 单位：米
     */
    private Integer distance;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;

    /**
     * 是否查询所有设备，非每个站址一条  true: 查询所有设备 false：查询设备每个站址取一条
     */
    private Boolean queryAll;
}
