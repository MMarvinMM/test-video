package com.chinatower.gis.modules.gis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @desription: 土地利用现状 模板表 现状图斑
 * @data 创建时间：2023/8/11 9:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("xztb_ceshitable")
@ApiModel(value="XztbCeshitable对象", description="土地利用现状")
public class XztbCeshitable extends LayerTempBase implements Serializable {

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
     * 图斑编号
     */
    private String tbbh;

    /**
     * 耕地类型
     */
    private String gdlx;

    /**
     * 扣除类型
     */
    private String kclx;

    /**
     * 耕地坡度级
     */
    private String gdpdj;

    /**
     * 扣除地类编码
     */
    private String kcdlbm;

    /**
     * 扣除地类系数
     */
    private Float tkxs;

    /**
     * 图斑面积
     */
    private Float tbmj;

    /**
     * 线状地物面积
     */
    private Float xzdwmj;

    /**
     * 零星地物面积
     */
    private Float lxdwmj;

    /**
     * 扣除地类面积
     */
    private Float tkmj;

    /**
     * 图斑地类面积
     */
    private Float tbdlmj;

    /**
     * 批注文号
     */
    private String pzwh;
}
