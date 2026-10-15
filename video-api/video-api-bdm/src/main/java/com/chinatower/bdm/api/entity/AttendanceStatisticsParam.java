package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AttendanceStatisticsParam extends BaseIndustryEntity implements Serializable {

    private String month;

    private String keeperId;

    private String attendanceDate;

    private String gridId;
}
