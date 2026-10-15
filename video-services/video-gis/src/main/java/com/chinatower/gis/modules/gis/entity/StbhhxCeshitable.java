package com.chinatower.gis.modules.gis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @desription: 生态保护红线 模板表
 * @data 创建时间：2023/8/11 9:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stbhhx_ceshitable")
@ApiModel(value="StbhhxCeshitable对象", description="生态保护红线")
public class StbhhxCeshitable extends LayerTempBase implements Serializable {

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
     * 省
     */
    private String sheng;

    /**
     * 市
     */
    private String shi;

    /**
     * 县（区）
     */
    private String xian;

    /**
     * 红线编码
     */
    private String hxbm;

    /**
     * 红线名称
     */
    private String hxmc;

    /**
     * 红线类型
     */
    private String hxlx;

    /**
     * 类型编码
     */
    private String lxbm;

    /**
     * 面积
     */
    private Float mj;

    /**
     * 自然保护地名称
     */
    private String zrbhdmc;

    /**
     * 自然保护地级别
     */
    private String zrbhdjb;

    /**
     * 自然保护地类型
     */
    private String zrbhdlx;

    /**
     * 自然保护地分区
     */
    private String zrbhdfq;

    /**
     * 生态系统与植被类型（陆地）
     */
    private String xtyzblx;

    /**
     * 管控措施（海洋）
     */
    private String gkcs;

    /**
     * 所在县级行政区代码
     */
    private String szxjxzqdm;

    /**
     * 所在县级行政区名称
     */
    private String szxjxzqmc;

    /**
     * 备注
     */
    private String bz;
}
