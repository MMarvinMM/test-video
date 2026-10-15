package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.web.page.PageDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询网格入参
 * @author zl
 */
@Data
public class ListByConditionVO extends PageDomain  {

    /**
     * 类型名称
     */
    @ApiModelProperty("类型名称")
    private String typeName;
    /**
     * 图层ID
     */
    private String id;


    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String creator;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty("创建开始时间")
    private String createBeginTime;

    @ApiModelProperty("创建结束时间")
    private String createEndTime;

    @ApiModelProperty(value = "修改开始时间")
    private String updateBeginTime;

    @ApiModelProperty(value = "修改结束时间")
    private String updateEndTime;

    @ApiModelProperty(value = "图层类型")
    private Integer category;

}
