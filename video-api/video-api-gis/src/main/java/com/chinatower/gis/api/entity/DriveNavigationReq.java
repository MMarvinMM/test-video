package com.chinatower.gis.api.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @desription: 请求导航
 * @data 创建时间：2023/8/8 16:30
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DriveNavigationReq extends GisBaseReq{

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
     * 起点处道路类型
     *
     * 填入此值可以辅助更精准的起点算路
     *
     * 0：普通道路
     *
     * 1：高架上
     *
     * 2：高架下
     *
     * 3：主路
     *
     * 4：辅路
     *
     * 5：隧道
     *
     * 7：环岛
     *
     * 9：停车场内部
     *
     * 可选
     * @required false
     */
    private Integer originType;
    /**
     * 驾车算路策略
     * 0：速度优先（只返回一条路线），此路线不一定距离最短
     *
     * 1：费用优先（只返回一条路线），不走收费路段，且耗时最少的路线
     *
     * 2：距离优先（只返回一条路线），仅走距离最短的路线，但是可能存在穿越小路/小区的情况
     *
     * 3：速度优先（只返回一条路线），不走快速路，例如京通快速路
     *
     * 32：默认，高德推荐，同高德地图APP默认
     *
     * 33：躲避拥堵
     *
     * 34：高速优先
     *
     * 35：不走高速
     *
     * 36：少收费
     *
     * 37：大路优先
     *
     * 38：速度最快
     *
     * 39：躲避拥堵＋高速优先
     *
     * 40：躲避拥堵＋不走高速
     *
     * 41：躲避拥堵＋少收费
     *
     * 42：少收费＋不走高速
     *
     * 43：躲避拥堵＋少收费＋不走高速
     *
     * 44：躲避拥堵＋大路优先
     *
     * 45：躲避拥堵＋速度最快
     * 可选
     * @required false
     */
    private Integer strategy;
    /**
     *避让区域
     * 区域避让，默认支持1个避让区域，每个区域最多可有16个顶点；多个区域坐标按顺序以英文竖线符号分隔，如果是四边形则有四个坐标点，如果是五边形则有五个坐标点；最大支持32个避让区域。
     *
     * 同时传入避让区域及避让道路，仅支持避让道路；
     *
     * 每个避让区域不能超过81平方公里，否则避让区域会失效。
     * 可选
     * @required false
     */
    private String waypoints;
    /**
     * 避让道路名    只支持一条避让道路
     * @required false
     */
    private String avoidroad;
    /**
     * 车牌号码 车牌号，如 京AHA322，支持6位传统车牌和7位新能源车牌，用于判断限行相关。
     * @required false
     */
    private String plate;
    /**
     * 车辆类型
     *
     * 0：普通燃油汽车
     *
     * 1：纯电动汽车
     *
     * 2：插电式混动汽车
     *
     * 可选
     * @required false
     */
    private Integer cartype;
    /**
     * 是否使用轮渡
     *
     * 0:使用渡轮
     *
     * 1:不使用渡轮
     *
     * 可选
     * @required false
     */
    private Integer ferry;

    /**
     * 返回结果控制
     *
     * show_fields用来筛选response结果中可选字段。show_fields的使用需要遵循如下规则：
     *
     * 1、具体可指定返回的字段类请见下方返回结果说明中的“show_fields”内字段类型；
     *
     * 2、多个字段间采用“,”进行分割；
     *
     * 3、show_fields未设置时，只返回基础信息类内字段；
     * @required false
     */
    private String showFields;
}
