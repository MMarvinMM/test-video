package com.chinatower.gis.modules.gis.entity.layer;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 行政区域表
 * @TaoheTmpSelectValue sys_area
 */
@Data
@ApiModel(value = "SysArea",description = "行政区域实体")
public class TaoheTmpSelectValue extends CommonFeignEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标识码
     */
    private String bsm;

    /**
     * 相交面积
     */
    private double calArea;

    /**
     * 相交面积比例
     */
    private double coverRatio;

    /**
     * 类别
     */
    private String serial;

    /**
     * 表名
     */
    private String tbName;
}
