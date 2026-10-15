package com.chinatower.gis.modules.gis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @desription: 建设用地管制区 模板表
 * @data 创建时间：2023/8/11 9:07
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jsydgzq_ceshitable")
@ApiModel(value="JsydgzqCeshitable对象", description="建设用地管制区")
public class JsydgzqCeshitable extends LayerTempBase implements Serializable {

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
     * 管制区类型代码
     */
    private String gzqlxdm;

    /**
     * 管制区面积
     */
    private Float gzqmj;

    /**
     * 说明
     */
    private String sm;

}
