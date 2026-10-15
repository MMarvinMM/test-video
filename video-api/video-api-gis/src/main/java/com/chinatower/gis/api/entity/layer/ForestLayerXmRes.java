package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 深林图层下班列表
 * @data 创建时间：2023/12/5 10:05
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ForestLayerXmRes {
    /**
     * 图斑ID
     */
    private String layerId;
    /**
     * geometry
     */
    private Object geometry;
    /**
     * 图斑ID
     */
    private Integer gid;

    /**
     * minx
     */
    private String minx;

    /**
     * miny
     */
    private String miny;

    /**
     * maxx
     */
    private String maxx;

    /**
     * maxy
     */
    private String maxy;

    /**
     * bsm
     */
    private String bsm;
    /**
     * layertype
     */
    private String layertype;
    /**
     * 图斑编号
     */
    private String tbbh;
    /**
     * 省
     */
    private String sheng;
    /**
     * 县
     */
    private String xian;

    /**
     * 乡
     */
    private String xiang;

    /**
     * 村
     */
    private String cun;

    /**
     * 林业局(场)
     */
    private String linYeJu;

    /**
     * 林场(分场)
     */
    private String linChang;

    /**
     * 地貌
     */
    private String diMao;

    /**
     * 海拔
     */
    private String haiBa;

    /**
     * 坡向
     */
    private String poXiang;

    /**
     * 坡位
     */
    private String poWei;

    /**
     * 坡度
     */
    private Integer poDu;

    /**
     * 交通区位
     */
    private String jtQw;

    /**
     * 主体功能区
     */
    private String qykz;

    /**
     * 土壤类型(名称)
     */
    private String tuRangLx;

    /**
     * 土层厚度
     */
    private Integer tuCengHd;

    /**
     * 土地所有权属
     */
    private String ldQs;

    /**
     * 土地使用权属
     */
    private String tdsyqs;

    /**
     * 林班
     */
    private String linBan;

    /**
     * 小班
     */
    private String xiaoBan;

    /**
     * 小班面积
     */
    private Float xbmj;

    /**
     * 林带宽度
     */
    private Float ldKd;

    /**
     * 林带长度
     */
    private Float ldCd;

    /**
     * 地类
     */
    private String diLei;

    /**
     * 植被覆盖类型
     */
    private String zbfglx;

    /**
     * 优势树(灌)种
     */
    private String youShiSz;

    /**
     * 起源
     */
    private String qiYuan;

    /**
     * 龄组
     */
    private String lingZu;

    /**
     * 乔木郁闭度/灌木覆盖度
     */
    private Float yuBiDu;

    /**
     * 灾害等级
     */
    private String disasterC;

    /**
     * 灾害类型
     */
    private String dispe;

    /**
     * 平均胸径
     */
    private Float pingjunXj;

    /**
     * 平均树高
     */
    private Float pingjunSg;

    /**
     * 每公顷株数
     */
    private Integer meiGqZs;

    /**
     * 每公顷蓄积
     */
    private Float meiGqXj;

    /**
     * 蓄积量
     */
    private Float huoLmxj;

    /**
     * 生物量
     */
    private Float shengWuL;

    /**
     * 碳储量
     */
    private Float tanChuL;

    /**
     * 林木使用权属
     */
    private String lmsyqs;

    /**
     * 林木所有权属
     */
    private String lmqs;

    /**
     * 生态区位
     */
    private String stqw;

    /**
     * 生态区位名称
     */
    private String stqwmc;

    /**
     * 工程类别
     */
    private String gChengLb;

    /**
     * 森林类别
     */
    private String senLinLb;

    /**
     * 林种
     */
    private String linZhong;

    /**
     * 国家级公益林保护等级
     */
    private String gjgylBhdj;

    /**
     * 事权等级
     */
    private String shiquanD;

    /**
     * 是否天保区公益林
     */
    private String sfTbq;

    /**
     * 林地功能分区
     */
    private String lyfq;

    /**
     * 林地规划管理
     */
    private String ldghGl;

    /**
     * 林地保护等级
     */
    private String bhDj;

    /**
     * 林地质量等级
     */
    private String zlDj;

    /**
     * 土地退化类型
     */
    private String tdThLx;
}
