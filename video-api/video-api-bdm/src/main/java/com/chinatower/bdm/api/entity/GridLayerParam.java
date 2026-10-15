package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 查询网格列表入参
 * @author zl
 */
@Data
public class GridLayerParam extends BaseDataScopeEntity {
    private String parentId;
    private String excludeGridId;
    /**
    * 获取本级和本级以下网格可传此参数
    */
    private String gridLevelCode;
    /**
     * 网格id
     */
    private String gridId;
    private String currId;
    private String provinceId;
    private String cityId;

    private List<RegionInfo> regionList;

    /**
     * 内部类，用于存储省市区信息
     */
    @Data
    public static class RegionInfo {
        private String provinceId;
        private String cityId;
    }

}
