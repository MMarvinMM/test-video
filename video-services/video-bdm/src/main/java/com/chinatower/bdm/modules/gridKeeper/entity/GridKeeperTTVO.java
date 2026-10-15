package com.chinatower.bdm.modules.gridKeeper.entity;

import com.chinatower.bdm.api.entity.GridKeeperBaseVO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 网格员vo
 * @author zl
 */
@Data
@ToString
public class GridKeeperTTVO extends GridKeeperBaseVO implements Serializable {

    private List<GridRefGridKeeperVO> gridRefGridKeeperVOList;

    private static final long serialVersionUID = 1L;
}
