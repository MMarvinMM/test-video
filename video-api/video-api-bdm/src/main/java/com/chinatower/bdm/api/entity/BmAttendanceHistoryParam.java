package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 考勤历史打卡记录入参
 */
@Data
public class BmAttendanceHistoryParam extends BaseDataScopeEntity implements Serializable {
    private String startDate;
    private String endDate;
    private String status;
    private String keeperId;
    /**
     * 当前页
     */
    private Integer pageNum = 1;
    /**
     * 条数限制
     */
    private Integer pageSize = 10;

    private static final long serialVersionUID = 1L;

    private String gridId;
}
