package com.chinatower.gis.modules.gis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @desription: 生态保护红线 模板表
 * @data 创建时间：2023/8/11 9:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tdbp_ceshitable")
@ApiModel(value="TdbpCeshitable对象", description="土地报批")
public class TdbpCeshitable  extends LayerTempBase implements Serializable {
    /**
     * 数据ID
     */
    @ApiModelProperty(value = "数据ID")
    private String gid;
    /**
     * calarea
     */
    private Float calarea;
    /**
     * 行政区代码
     */
    private String xzqdm;

    /**
     * 行政区名称
     */
    private String xzqmc;

    /**
     * 事项代码
     */
    private String flowsn;

    /**
     * 项目名称
     */
    private String xmmc;

    /**
     * 勘测编号
     */
    private String kcid;

    /**
     * 地块编号
     */
    private String dkid;

    /**
     * 勘测名称
     */
    private String kcmc;

    /**
     * 地块名称
     */
    private String dkmc;

    /**
     * 审批状态
     */
    private String spzt;

    /**
     * 批准日期
     */
    private Date pzrq;

    /**
     * 批准文号
     */
    private String pzwh;

    /**
     * 批准机构
     */
    private String pzjg;

    /**
     * 地块用途
     */
    private String dkyt;

    /**
     * 用地面积
     */
    private Float ydmj;
}
