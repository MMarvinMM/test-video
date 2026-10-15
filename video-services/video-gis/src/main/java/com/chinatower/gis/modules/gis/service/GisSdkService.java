package com.chinatower.gis.modules.gis.service;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.GisSdkReq;
import com.chinatower.gis.api.entity.MeasureDistanceBatchVO;
import org.springframework.web.bind.annotation.RequestBody;

public interface GisSdkService {

    /**
     * 求取多边形范围内栅格值范围
     */
    Result polygonClipRasterStats(GisSdkReq req);

    /**
     * 剖面分析内插点
     */
    Result profileAnalysis(GisSdkReq req);

    /**
     * 关键点提取 粗版
     */
    Result measureDistanceLow(GisSdkReq req);

    /**
     * 关键点提取 高精度
     */
    Result measureDistanceHigh(GisSdkReq req);

    /**
     * 批量查询
     */
    Result measureDistanceBatch(MeasureDistanceBatchVO req);

    /**
     * 贴地侧面 粗版
     */
    Result measureAreaLow(GisSdkReq req);

    /**
     * 贴地侧面 高精度
     */
    Result measureAreaHigh(GisSdkReq req);
}
