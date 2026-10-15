package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 导入导出网格参数VO
 * @author zl
 */
@Data
@ToString
public class GridExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gridId;
    @Excel(name = "网格编码")
    private String gridCode;
    @Excel(name = "网格名称")
    private String gridName;
    @Excel(name = "创建日期",type=Excel.Type.EXPORT)
    private String createTime;
    @Excel(name = "标绘状态",readConverterExp = "0=未标绘,1=已标绘",type=Excel.Type.EXPORT)
    private String pointStatus;
    @Excel(name = "组织编码",type=Excel.Type.IMPORT)
    private String gridEmCenter;
//    @Excel(name = "所属组织名称",type=Excel.Type.EXPORT)
//    private String orgName;
    @Excel(name = "上级网格编码")
    private Long parentId;
    @Excel(name = "上级网格名称",type=Excel.Type.EXPORT)
    private String parentName;
    @Excel(name = "当前层级",readConverterExp = "1=一级网格,2=二级网格,3=三级网格,4=四级网格,5=五级网格,6=六级网格",type=Excel.Type.EXPORT)
    private String gridLevel;
    @Excel(name = "展示顺序")
    private String gridSort;
    @Excel(name = "网格长",type=Excel.Type.EXPORT)
    private String keeperName;
    @Excel(name = "面积")
    private String gridArea;
    @Excel(name = "初始日期",type=Excel.Type.IMPORT,dateFormat = "yyyy-MM-dd")
    private Date gridBeginTime;
    @Excel(name = "终止日期",type=Excel.Type.IMPORT,dateFormat = "yyyy-MM-dd")
    private Date gridEndTime;
    @Excel(name = "日期范围",type=Excel.Type.EXPORT)
    private String gridTime;
    /**
     * @Excel(name = "最后一级",readConverterExp = "0=否,1=是",type=Excel.Type.EXPORT)---
     */
    private String isEnd;
    @Excel(name = "网格地址")
    private String gridAddress;
    @Excel(name = "网格介绍")
    private String gridIntroduce;
    @Excel(name = "地图区域范围")
    private String reginScopeDesc;
    @ApiModelProperty(value = "数据权限",hidden = true)
    private String dataScope;

}
