package com.chinatower.bdm.modules.gridKeeper.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GridKeeperParamVO extends BaseDataScopeEntity implements Serializable {
    private List<String> gridIdList;
    private String gridId;
    private Long keeperId;
    private String keeperName;
    private String staffId;
}
