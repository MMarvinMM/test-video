package com.chinatower.gis.modules.gis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @desription: 基本农田 模板表
 * @data 创建时间：2023/8/11 9:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yjjbnt_ceshitable")
@ApiModel(value="YjjbntCeshitable对象", description="基本农田")
public class YjjbntCeshitable extends LayerTempBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据ID
     */
    @ApiModelProperty(value = "数据ID")
    private String gid;

    /**
     * 计算面积
     */
    private Float calarea;

    /**
     * 标识码
     */
    private String bsm;

    /**
     * 要素代码
     */
    private String ysdm;

    /**
     * 行政区代码
     */
    private String xzqdm;

    /**
     * 行政区名称
     */
    private String xzqmc;

    /**
     * 永久基本农田图斑编号
     */
    private String yjjbnttbbh;

    /**
     * 图斑编号
     */
    private String tbbh;

    /**
     * 地类编码
     */
    private String dlbm;

    /**
     * 地类名称
     */
    private String dlmc;

    /**
     * 权属性质
     */
    private String qsxz;

    /**
     * 权属单位代码
     */
    private String qsdwdm;

    /**
     * 权属单位名称
     */
    private String qsdwmc;

    /**
     * 座落单位代码
     */
    private String zldwdm;

    /**
     * 座落单位名称
     */
    private String zldwmc;

    /**
     * 永久基本农田图斑面积
     */
    private Float yjjbnttbmj;

    /**
     * 扣除地类编码
     */
    private String kcdlbm;

    /**
     * 扣除地类系数
     */
    private Float tkxs;

    /**
     * 扣除地类面积
     */
    private Float tkmj;

    /**
     * 永久基本农田面积
     */
    private Float yjjbntmj;

    /**
     * 耕地类型
     */
    private String gdlx;

    /**
     * 永久基本农田类型
     */
    private String yjjbntlx;

    /**
     * 耕地坡度级别
     */
    private String gdpdjb;

    /**
     * 图斑细化代码
     */
    private String tbxhdm;

    /**
     * 图斑细化名称
     */
    private String tbxhmc;

    /**
     * 种植属性代码
     */
    private String gdzzsxdm;

    /**
     * 种植属性名称
     */
    private String gdzzsxmc;

    /**
     * 耕地质量等别
     */
    private Integer gddb;

    /**
     * 耕地质量等级
     */
    private Integer gddj;

    /**
     * 飞入地标识
     */
    private String frdbs;

    /**
     * 数据年份
     */
    private Integer sjnf;

    /**
     * 备注
     */
    private String bz;
}
