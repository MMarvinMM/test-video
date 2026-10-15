package com.chinatower.common.core.dto.bdm;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @desription: 图层列表
 * @data 创建时间：2024/1/28 15:08
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerTypeListVO {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "图层列表名称")
    private String typeName;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "0-默认 1-自定义")
    private Integer category;

    @ApiModelProperty(value = "简称")
    private String abbreviation;

    @ApiModelProperty(value = "面积字段")
    private String areaRef;

    @ApiModelProperty(value = "图斑编号字段")
    private String tbbhRef;
}
