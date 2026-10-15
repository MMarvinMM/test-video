package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 算法与图斑识别区域关系表
 * @author zhaozj
 * @since 2022-04-10
 */
@Data
public class PolygonPatchAlgoAreaVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一编码
     */
    private Long uniqueId;

    /**
     *算法配置唯一编号
     */
    private String configId;

    //识别区域与图斑关系唯一编码
    private String polygonId;

    /**
     *识别区域唯一编码
     */
    private String monitorAreaId;

    private List<String> polygonIdList;

    //电子围栏点集合
    private String polygonPoints;

    //图层id
    private String layerId;

    //图斑id
    private String featureId;

    private String tbbh;

    private List<String> configIdList;
}
