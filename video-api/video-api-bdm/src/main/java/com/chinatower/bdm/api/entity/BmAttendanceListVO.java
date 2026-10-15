package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class BmAttendanceListVO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 1L;
//    @Excel(name = "网格员ID")
    private String keeperId;
    @Excel(name = "账号")
    private String loginName;
    @Excel(name = "网格员名称")
    private String keeperName;
    @Excel(name = "所属网格")
    private String gridName;
    @Excel(name = "正常次数")
    private String normalCount;
    @Excel(name = "异常次数")
    private String dbnormalCount;
    private String gridId;
}
