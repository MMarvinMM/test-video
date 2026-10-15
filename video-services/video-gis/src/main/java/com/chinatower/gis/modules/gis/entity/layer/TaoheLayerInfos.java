package com.chinatower.gis.modules.gis.entity.layer;

import com.chinatower.gis.modules.gis.mapper.LayerTaoheMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

import static com.chinatower.gis.api.entity.layer.LayerConstants.*;

/**
 * @desription: 套合分析的有效图层
 *
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheLayerInfos extends ArrayList<TaoheLayerInfo> {
    public TaoheLayerInfos(){

    }

    public TaoheLayerInfos(ArrayList<TaoheLayerInfo> lst) {
        this.addAll(lst);
    }
    private TaoheLayerInfo usedXZTB;
    private int usedXZTBIntersectsCount;
    private TaoheLayerInfo usedJSYDGZQ;
    private int usedJSYDGZQIntersectsCount;
    private TaoheLayerInfo usedYJJBNT;
    private int usedYJJBNTIntersectsCount;
    private TaoheLayerInfo usedSTBHHX;
    private int usedSTBHHXIntersectsCount;
    private TaoheLayerInfo usedTDBP;
    private int usedTDBPIntersectsCount;

    public void calIntersects(LayerTaoheMapper layerTaoheMapper, String wkt, double distance, int maxIntersectsCount) {

        for(TaoheLayerInfo layerInfo: this) {
            String type = layerInfo.getLayerTypeSimple();

            switch (type) {
                case CONST_XZTB:    // 土地利用现状 XZTB - 图斑面积	TBMJ
                    if(usedXZTB == null) {
                        usedXZTBIntersectsCount = layerTaoheMapper.selectCalIntersectsCount(layerInfo.getLayerName(), wkt, distance, maxIntersectsCount);
                        if(usedXZTBIntersectsCount > 0)
                            usedXZTB = layerInfo;
                    }
                    break;
                case CONST_JSYDGZQ:    // 土地利用总体规划（建设用地管制区）JSYDGZQ -管制区面积 GZQMJ
                    if(usedJSYDGZQ == null) {
                        usedJSYDGZQIntersectsCount = layerTaoheMapper.selectCalIntersectsCount(layerInfo.getLayerName(), wkt, distance, maxIntersectsCount);
                        if(usedJSYDGZQIntersectsCount > 0)
                            usedJSYDGZQ = layerInfo;
                    }
                    break;
                case CONST_YJJBNT:      // 永久基本农田 YJJBNT -永久基本农田图斑面积 YJJBNTTBMJ
                    if(usedYJJBNT == null) {
                        usedYJJBNTIntersectsCount = layerTaoheMapper.selectCalIntersectsCount(layerInfo.getLayerName(), wkt, distance, maxIntersectsCount);
                        if(usedYJJBNTIntersectsCount > 0)
                            usedYJJBNT = layerInfo;
                    }
                    break;
                case CONST_STBHHX:      // 生态保护红线 stbhhx- 面积 mj
                    if(usedSTBHHX == null) {
                        usedSTBHHXIntersectsCount = layerTaoheMapper.selectCalIntersectsCount(layerInfo.getLayerName(), wkt, distance, maxIntersectsCount);
                        if(usedSTBHHXIntersectsCount > 0)
                            usedSTBHHX = layerInfo;
                    }
                    break;
                case CONST_TDBP:      // 土地报批 tdbp- 用地面积 YDMJ ydmj
                    if(usedTDBP == null) {
                        usedTDBPIntersectsCount = layerTaoheMapper.selectCalIntersectsCount(layerInfo.getLayerName(), wkt, distance, maxIntersectsCount);
                        if(usedTDBPIntersectsCount > 0)
                            usedTDBP = layerInfo;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
