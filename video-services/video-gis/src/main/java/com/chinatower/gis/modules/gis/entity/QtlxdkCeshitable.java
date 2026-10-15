package com.chinatower.gis.modules.gis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @desription: 其他类型 地块
 * @data 创建时间：2023/8/15 9:56
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("qtlxdk_ceshitable")
@ApiModel(value="Qtlxdk对象", description="其他类型")
public class QtlxdkCeshitable extends LayerTempBase implements Serializable {

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
     * 属性json字符串
     */
    private String properties;
}
