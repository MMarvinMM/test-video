package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class GridListVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 父级名称
     */
    private String parentName;
    /**
     * 网格id
     */
    private String gridId;
    /**
     * 是否最后一级:0否 1是
     */
    private String isEnd;
    /**
     * 网格编码
     */
    private String gridCode;
    /**
     * 网格名称
     */
    private String gridName;
    /**
     * 上级网格ID
     */
    private String parentId;
    /**
     * 上级网格编码
     */
    private String parentCode;
    /**
     * 网格层级
     */
    private String gridLevel;
    /**
     * 标绘状态 0=未标绘,1=已标绘
     */
    private String pointStatus;
    /**
     * 网格填充颜色
     */
    private String color;
    /**
     * 网格中心坐标
     */
    private String gridCenter;
    /**
     * 网格范围
     */
    private String gridRange;
    /**
     * 网格长姓名
     */
    private String keeperName;
    /**
     * 网格长id
     */
    private String keeperId;
    /**
     * 网格长联系电话
     */
    private String keeperPhoneNumber;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 省份编码
     */
    private String provinceCode;
    /**
     * 地市编码
     */
    private String cityCode;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 地市名称
     */
    private String cityName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String keeperPhotoUrl;

    private String isLine;

    private String phonePlainText;

    private String gridEmCenter;

    /**
     * 网格边框颜色
     * @required false
     */
    private String borderColor;

    /**
     * 网格边框颜色透明度 单位% 取值0-100 默认50
     * @required false
     */
    private String borderColorTransparency;

    /**
     * 网格边框线条粗细 取值1 2 3 4 5 默认1
     * @required false
     */
    private String borderColorLineThickness;

    /**
     * 网格填充颜色透明度 单位% 取值0-100 默认50
     * @required false
     */
    private String colorTransparency;


}
