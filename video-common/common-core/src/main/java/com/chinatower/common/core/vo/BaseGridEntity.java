package com.chinatower.common.core.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 网格配置vo
 * @author zl
 */
@Data
@ToString
public class BaseGridEntity extends BaseDataScopeEntity implements Serializable {

    /**
     * 网格ID
     * @required true
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gridId;

    /**
     * 租户名称
     * @required true
     */
    private String tenantName;

    /**
     * 行业名称
     * @required true
     */
    private String industryName;

    /**
     * 网格编码。人工填写，编码规则业务规定，不允许重复。
     * @required true
     */
    private String gridCode;

    /**
     * 网格名称
     * @required true
     */
    private String gridName;

    /**
     * 行政区划的国标编码
     * @required true
     */
    private String gbCode;

    /**
     * 上级网格ID
     * @required true
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 上级网格名称
     * @required true
     */
    private String parentName;

    /**
     * 上级网格编码
     * @required true
     */
    private String parentCode;

    /**
     * 网格分类。1基础网格 2环保网格 3防火网格 4城管网格 5执法网格 6安监网格等
     * @required true
     */
    private String gridClassification;

    /**
     * 网格层级。网格层级表的层级
     * 根据【网格层级表】的层级关系动态展示。
     * 查询【网格层级表】层级 条件：上层级=网格树返回的网格层级
     * @required true
     */
    private String gridLevel;

    /**
     * 网格层级编码 原attribute6
     * @required true
     */
    private String gridLevelCode;

    /**
     * 网格属性。01村庄、02小区、03企业、04学校（幼儿园）、05医院、06驻镇（街）单位、07其他
     * @required true
     */
    private String gridProperty;

    /**
     * 是否最后一级:0否 1是
     * @required true
     */
    private String isEnd;

    /**
     * 网格地址
     * @required true
     */
    private String gridAddress;

    /**
     * 网格介绍
     * @required true
     */
    private String gridIntroduce;

    /**
     * 宣传图片
     * @required false
     */
    private String gridPicture;

    /**
     * 地图区域范围
     * @required false
     */
    private String reginScopeDesc;

    /**
     * 展示顺序
     * @required false
     */
    private Integer gridSort;

    /**
     * 标绘状态
     * @required true
     */
    private String pointStatus;

    /**
     * 事件上报中心
     * @required true
     */
    private String gridEmCenter;

    /**
     * 所属组织
     * @required true
     */
    private String gridEmCenterName;

    /**
     * 初始日期
     * @required false
     */
    private String gridBeginTime;

    /**
     * 终止日期
     * @required false
     */
    private String gridEndTime;

    /**
     * 面积
     * @required false
     */
    private String gridArea;

    /**
     * 是否有效
     * @required true
     */
    private String isValid;

    /**
     * 当前页
     * @required false
     */
    private int pageNum = 1;

    /**
     * 条数限制
     * @required false
     */
    private int pageSize = 10;

    /**
     * 网格填充颜色
     * @required false
     */
    private String color;

    /**
     * 网格中心坐标
     * @required false
     */
    private String gridCenter;

    /**
     * 网格坐标
     * @required false
     */
    private String gridRange;

    /**
     * 租户集合
     * @required false
     */
    private List<String> tenantList;

    /**
     * 网络id集合
     * @required false
     */
    private List<Long> gridIdList;

    /**
     * 列表排除的网格id，不查询此id
     * @required false
     */
    private long excludeGridId;

    /**
     * 网格长姓名
     * @required true
     */
    private String keeperName;

    /**
     * 网格长id
     * @required true
     */
    private String keeperId;

    /**
     * 网格长联系电话
     * @required false
     */
    private String keeperPhoneNumber;

    /**
     * 省编码
     * @required false
     */
    private String provinceCode;

    /**
     * 市编码
     * @required false
     */
    private String cityCode;

    /**
     * 省份名称
     * @required false
     */
    private String provinceName;

    /**
     * 地市名称
     * @required false
     */
    private String cityName;

    private String topTreeName;

    private String keeperPhotoUrl;

    private String isLine;

    private String phonePlainText;

    /**
     * 网格边框颜色
     * @required false
     */
    private String borderColor;

    /**
     * 网格边框颜色透明度 单位% 取值0-1 默认0.5
     * @required false
     */
    private String borderColorTransparency;

    /**
     * 网格边框线条粗细 取值1 2 3 4 5 默认1
     * @required false
     */
    private String borderColorLineThickness;

    /**
     * 网格填充颜色透明度 单位% 取值0-1 默认0.5
     * @required false
     */
    private String colorTransparency;

    private static final long serialVersionUID = 1L;

}
