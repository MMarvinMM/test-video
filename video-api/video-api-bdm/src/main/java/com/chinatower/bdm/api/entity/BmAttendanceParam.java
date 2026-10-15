package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询入参
 */
@Data
public class BmAttendanceParam extends BaseDataScopeEntity implements Serializable {

    private String startDate;
    private String endDate;
    private String gridId;
    private String keeperName;
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 条数限制
     */
    private Integer pageSize;

    private List<String> keeperIdList;

    private static final long serialVersionUID = 1L;
}
