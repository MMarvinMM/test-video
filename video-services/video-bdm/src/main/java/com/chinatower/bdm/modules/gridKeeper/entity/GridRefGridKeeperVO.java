package com.chinatower.bdm.modules.gridKeeper.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 网格配置vo
 * @author zl
 */
@Data
@ToString
public class GridRefGridKeeperVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 网格ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gridId;

    /**
     * 网格编码。人工填写，编码规则业务规定，不允许重复。
     */
    private String gridCode;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 网格长id
     */
    private Long keeperId;

    private static final long serialVersionUID = 1L;

}
