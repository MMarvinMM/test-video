package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: pg 图层模板表
 * @data 创建时间：2023/8/11 23:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum PGLayerTempEnums {

    XZTB_CESHITABLE("xztb","xztb_ceshitable","土地利用现状","com.chinatower.gis.modules.gis.entity.XztbCeshitable","xztbCeshitableMapper","TBMJ"),
    STBHHX_CESHITABLE("stbhhx","stbhhx_ceshitable","生态保护红线","com.chinatower.gis.modules.gis.entity.StbhhxCeshitable","stbhhxCeshitableMapper","MJ"),
    JSYDGZQ_CESHITABLE("jsydgzq","jsydgzq_ceshitable","建设用地管制区","com.chinatower.gis.modules.gis.entity.JsydgzqCeshitable","jsydgzqCeshitableMapper","GZQMJ"),
    YJJBNT_CESHITABLE("yjjbnt","yjjbnt_ceshitable","永久基本农田","com.chinatower.gis.modules.gis.entity.YjjbntCeshitable","yjjbntCeshitableMapper","YJJBNTTBMJ"),
    TDBP_CESHITABLE("tdbp","tdbp_ceshitable","土地报批","com.chinatower.gis.modules.gis.entity.TdbpCeshitable","tdbpCeshitableMapper","YDMJ"),
    QTLXDK("qtlxdk","qtlxdk_ceshitable","其他类型地块","com.chinatower.gis.modules.gis.entity.QtlxdkCeshitable","qtlxdkCeshitableMapper","calarea"),
    SLZY("slzy","slzy_ceshitable","森林资源","com.chinatower.gis.modules.gis.entity.SlzyCeshitable","slzyCeshitableMapper","xbmj"),
    TKQ("tkq","tkq_ceshitable","探矿权","","","txmj"),
    CKQ("ckq","ckq_ceshitable","采矿权","","","kqmj"),
    TEWC("tewc","tewc_ceshitable","探而未采","","","txmj"),
    ;

    /**
     * 类型名称
     */
    private String layerType;
    /**
     * 模板表名称
     */
    private String tableName;
    /**
     * 主备
     */
    private String remark;
    /**
     * 对应模板实体
     */
    private String entity;
    /**
     * 模板Mapper 名称
     */
    private String mapper;
    /**
     * 面积字段
     */
    private String tableMj;


    public static PGLayerTempEnums get(String layerType){
        for (PGLayerTempEnums tempEnums : PGLayerTempEnums.values()){
            if (tempEnums.getLayerType().equals(layerType)){
                return tempEnums;
            }
        }
        return QTLXDK;
    }
}
