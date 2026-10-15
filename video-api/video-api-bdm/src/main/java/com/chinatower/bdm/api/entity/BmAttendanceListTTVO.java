package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;

/**
 * 铁塔视角考勤列表VO
 */
@Data
public class BmAttendanceListTTVO extends BmAttendanceListVO {
    @Excel(name = "客户ID")
    private String tenantId;
    @Excel(name = "客户名称")
    private String tenantName;

    private String provinceId;
    @Excel(name = "省市")
    private String provinceName;

    private String cityId;
    @Excel(name = "地市")
    private String cityName;
}
