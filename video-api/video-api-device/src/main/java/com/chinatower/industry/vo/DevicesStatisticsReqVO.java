package com.chinatower.industry.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DevicesStatisticsReqVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 组织编码list
     */
    private List<String> deptList;

}
