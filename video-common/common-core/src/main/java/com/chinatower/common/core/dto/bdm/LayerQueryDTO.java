package com.chinatower.common.core.dto.bdm;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @desription: 图层搜索条件
 * @data 创建时间：2023/4/7 9:14
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerQueryDTO extends BaseDataScopeEntity {
    /** 自定义图层名称 */
    @ApiModelProperty(value = "自定义图层名称")
    private String customName;
    /** 图层类别 */
    @ApiModelProperty(value = "图层类别")
    private String layerTypeId;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;
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

    @ApiModelProperty(value = "创建开始时间")
    private String createBeginTime;

    @ApiModelProperty(value = "创建结束时间")
    private String createEndTime;

    @ApiModelProperty(value = "修改开始时间")
    private String updateBeginTime;

    @ApiModelProperty(value = "修改结束时间")
    private String updateEndTime;

    @ApiModelProperty(value = "类型简称")
    private String abbreviation;

    /**
     * 图层id
     */
    private String id;

    /**
     * 1-矢量 2-非矢量 3-DEM 4-DSM 5-倾斜摄影 6-激光点云
     */
    private String category;

    /**
     * 模糊匹配 node
     */
    private String nodeCode;

    /**
     * 套合图层标记  1-是  0-不是
     */
    private String nestFlag;

    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String isAsc = "asc";

    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }

    /**
     * 多种类型  支持2,3,4,5,6
     */
    private List<Integer> categoryList;

    /**
     * 标签关联查询
     */
    private String userName;

    /**
     * ar标签收藏标识
     */
    private Integer arTagFlag;

    /**
     * 发布状态 0:发布中；1发布成功：2发布失败
     */
    private String publishStatus;


    /**
     * 上传类型 1:图层链接；2上传文件
     */
    private String uploadType;

    /**
     * 大屏行业查询发布状态标识
     */
    private String publishStatusFlag;

}
