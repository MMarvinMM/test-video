package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @desription: 公交导航信息
 * @data 创建时间：2023/8/9 10:56
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationReq extends GisBaseReq{

    /**
     * 起点经纬度 经度在前，纬度在后，经度和纬度用","分割，经纬度小数点后不得超过6位。
     * @required true
     */
    @NotBlank(message = "起点不能为空")
    private String origin;
    /**
     * 目的地 经度在前，纬度在后，经度和纬度用","分割，经纬度小数点后不得超过6位。
     * @required true
     */
    @NotBlank(message = "目的地不能为空")
    private String destination;
    /**
     * 起点POI ID 起点为POI时，建议填充此值，可提升路线规划准确性
     * @required false
     *
     * 可选
     */
    private String originId;
    /**
     * 高德Key
     * @required false
     */
    private String destinationId;
    /**
     * 起点所在行政区域编码
     * @required false
     */
    private String ad1;
    /**
     * 终点所在行政区域编码
     * @required false
     */
    private String ad2;
    /**
     * 起点所在城市 仅支持citycode，相同时代表同城，不同时代表跨城
     * @required false
     */
    private String city1;
    /**
     * 目的地所在城市 仅支持citycode，相同时代表同城，不同时代表跨城
     * @required false
     */
    private String city2;

    /**
     * 可选值：
     *
     * 0：推荐模式，综合权重，同高德APP默认
     * @required false
     *
     * 1：最经济模式，票价最低
     *
     * 2：最少换乘模式，换乘次数少
     *
     * 3：最少步行模式，尽可能减少步行距离
     *
     * 4：最舒适模式，尽可能乘坐空调车
     *
     * 5：不乘地铁模式，不乘坐地铁路线
     *
     * 6：地铁图模式，起终点都是地铁站
     *
     * （地铁图模式下originpoi及destinationpoi为必填项）
     *
     * 7：地铁优先模式，步行距离不超过4KM
     *
     * 8：时间短模式，方案花费总时间最少
     * @required false
     */
    private Integer strategy;
    /**
     * 返回方案条数
     * @required false
     */
    private Integer AlternativeRoute;
    /**
     * 地铁出入口数量
     * @required false
     */
    private Integer multiexport;
    /**
     * 0：直达
     *
     * 1：最多换乘1次
     *
     * 2：最多换乘2次
     *
     * 3：最多换乘3次
     *
     * 4：最多换乘4次
     * @required false
     */
    private Integer maxTrans;
    /**
     *
     * 可选值：
     *
     * 0：不考虑夜班车
     *
     * 1：考虑夜班车
     * @required false
     */
    private Integer nightflag;
    private String date;
    private String time;
    /**
     * show_fields用来筛选response结果中可选字段。show_fields的使用需要遵循如下规则：
     *
     * 1、具体可指定返回的字段类请见下方返回结果说明中的“show_fields”内字段类型；
     *
     * 2、多个字段间采用“,”进行分割；
     *
     * 3、show_fields未设置时，只返回基础信息类内字段
     * @required false
     */
    private String showFields;
}
