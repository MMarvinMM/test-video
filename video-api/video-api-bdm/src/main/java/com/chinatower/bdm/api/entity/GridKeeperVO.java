package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/**
 * 网格员vo
 * @author zl
 */
@Data
@ToString
public class GridKeeperVO extends GridKeeperBaseVO implements Serializable {

    /**
     * 网格员id列表
     */
    private List<String> gridIdList;

    /**
     * 网格员id列表
     */
    private List<String> keeperIdList;

    private static final long serialVersionUID = 1L;

    /**
     * 所属网格id
     */
    private Long gridId;

    /**
     * 所属网格名称
     */
    private String gridName;
}
