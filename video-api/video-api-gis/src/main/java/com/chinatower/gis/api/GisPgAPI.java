package com.chinatower.gis.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.bdm.LayerClassifyAreaStatisticsVO;
import com.chinatower.common.core.dto.bdm.LayerSlzyStatisticsDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.CameraCoordRes;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.api.entity.gis.GisFireCombustibleRes;
import com.chinatower.gis.api.entity.gis.GisFireGradeRes;
import com.chinatower.gis.api.entity.gis.GisSlopeAspectRes;
import com.chinatower.gis.api.entity.gis.GisVegetationDataRes;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.api.factory.GisPgFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "GisPgAPI",
        value = ServiceNameConstants.GIS_SERVICE,
        fallbackFactory = GisPgFallbackFactory.class)
public interface GisPgAPI {

    /**
     * 查询对应经纬度的海拔高度
     * @apiNote 海拔高度
     * @apiVersion 2.3.1
     * @tag 二、gis能力
     * @param lon 经度
     * @param lat 维度
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码 200成功 500失败
     * 		msg：返回描述
     * 		data：返回内容 海拔高度
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1705030819602,
     * 	"data": 1
     * }
     * @return Result<Integer>
     */
    @GetMapping("/pg/altitudeHeightQuery")
    Result<Integer> altitudeHeightQuery(@RequestParam("lon")String lon, @RequestParam("lat")String lat);
    /**
     * 查询对应经纬度的海拔高度 批量
     * @apiNote 批量海拔高度
     * @apiVersion 2.3.2
     * @tag 二、gis能力
     * @requestDemo
     * 返回参数解析：
     * 		code：返回编码 200成功 500失败
     * 		msg：返回描述
     * 		data：返回内容 海拔高度
     * 	      altitudesHeight：海拔高度
     * 	      lonLat：经纬度
     * 	      deviceId：设备ID
     *
     * {
     *     "lonLat":[
     *         {
     *             "lon":"115.22",
     *             "lat":"38.49",
     *             "deviceId":"1"
     *         },
     *         {
     *             "lon":"114.27",
     *             "lat":"39.63",
     *             "deviceId":"2"
     *         },
     *         {
     *             "lon":"124.27",
     *             "lat":"39.63",
     *             "deviceId":"3"
     *         }
     *     ]
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1705030819602,
     * 	"data":
     * 	 [
     *         {
     * 			"altitudesHeight": 1255,
     * 			"lonLat": "114.27,39.63",
     * 			"deviceId": "2"
     *        },
     *        {
     * 			"altitudesHeight": 0,
     * 			"lonLat": "124.27,39.63",
     * 			"deviceId": ""
     *        },
     *        {
     * 			"altitudesHeight": 20,
     * 			"lonLat": "115.22,38.49",
     * 			"deviceId": "1"
     *        }
     * 	  ]
     *  }
     *
     * @param req
     * @return Result<List<AltitudesRes>>
     */
    @PostMapping("/pg/altitudeHeightQuerys")
    Result<List<AltitudesRes>> altitudeHeightQuerys(@RequestBody AltitudesReq req);

    /**
     * 根据图层ID查询对应的图斑列表信息
     * @apiNote 根据图层ID查询对应的图斑列表信息
     * @apiVersion 2.3.3
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "layerId":"xxxx",
     *     "bsm":"xxx",
     *     "pageNum":1,
     *     "pageSize":10,
     * }
     * @param req
     *
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码 200成功 500失败
     * 		msg：返回描述
     * 	    data：返回内容 （因为每个图层类型的字段不一致，所以无法确认反正字段）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1705030819602,
     * 	"data":
     * 	 [
     *         {
     * 			"xxx": '字段信息数据',
     * 			"xxx": "字段信息数据",
     * 		    ...
     *        }
     *   ]
     *  }
     * @return Result
     */
    @PostMapping("/pg/tableNameList")
    Result<List<Map<String,Object>>> tableNameList(@RequestBody TableNameListReq req);


    /**
     * 提供视频视图联动功能实时视频画面中增加测高度
     * @apiNote 提供视频视图联动功能实时视频画面中增加测高度
     * @apiVersion 2.3.4
     * @requestDemo {
     *   "deviceCode": "11010800001327000075",
     *   "channelCode": "",
     *   "percentX": "1",
     *   "percentY": "1"
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1705030819602,
     * 	"data":
     *         {
     * 			"difheight": 1.0,
     * 			"coord": "126.66,36.11",
     * 			"coordtesxt": "文本"
     *        }
     *  }
     * @tag 二、gis能力
     * @param req 参数
     * @return Result<CameraCoordRes>
     */
    @PostMapping("/pg/getCameraCoord")
    Result<CameraCoordRes> getCameraCoord(@RequestBody CamaraDeviceParamsInfoVO req);

    /**
     * 根据图层名称按照code字段进行分类面积统计
     * @apiNote 根据表名按照code字段进行分类面积统计
     * @apiVersion 2.3.5
     * @requestDemo {
     *     "tableNames":['123','234'],
     *     "code":"yzsh"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码 200成功 500失败
     * 		msg：返回描述
     * 	    data：返回内容
     * 	        code: 字段名称
     * 	        value: 字段值
     * 	        area: 面积
     * 	        count: 数量
     *
     *[{
     *   "code": "yzsh",
     *   "value": "1",
     *   "area": "1",
     *   "count": "1"
     * }]
     * @tag 二、gis能力
     * @param vo 参数
     * @return Result<List<LayerSlzyStatisticsDTO>>
     */
    @PostMapping("/pg/layerClassifyAreaStatistics")
    Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(@RequestBody LayerClassifyAreaStatisticsVO vo);


    /**
     * 土地利用现状图层的耕地类型面积统计
     * @apiNote 耕地类型面积统计
     * @apiVersion 2.3.6
     * @requestDemo {
     *     "tableName":"xxxx",
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码 200成功 500失败
     * 		msg：返回描述
     * 	    data：返回内容
     * 	        codeName: 统计类型名称
     * 	        codeValue: 统计的类型编码值
     * 	        area: 面积
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717565708938,
     * 	"data": [
     * 		  {
     * 			"codeName": "水田",
     * 			"codeValue": "0101",
     * 			"area": 0.36
     *        }
     * 	]
     * }
     * @tag 二、gis能力
     * @param vo 参数
     * @return Result<List<LayerXztbDlbmAreaRes>>
     */
    @PostMapping("/pg/layerXztbDlbmAreaRes")
    Result<List<LayerXztbDlbmAreaRes>> layerXztbDlbmAreaRes(@RequestBody LayerYearTableName vo);

    /**
     * 土地利用现状图层的耕地等别面积比例统计
     * @apiNote 耕地等别面积比例统计
     * @apiVersion 2.3.7
     * @requestDemo {
     *     "tableName":"xxx",
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码 200成功 500失败
     * 		msg：返回描述
     * 	    data：返回内容
     * 	        name: 耕地等别名称
     * 	        area: 面积
     * 	        ratio: 比例
     *
     *{
     *  "avgGddb":"xxx",
     *  "childRes":[
     *      {
     *          "name":"低等地",
     *          "area":2515,
     *          "ratio":"25.36"
     *      }
     *  ]
     *}
     * @tag 二、gis能力
     * @param vo 参数
     * @return Result<LayerXztbGddbAreaRes>
     */
    @PostMapping("/pg/layerXztbGddbAreaRes")
    Result<LayerXztbGddbAreaRes> layerXztbGddbAreaRes(@RequestBody LayerYearTableName vo);

    /**
     * 查询基本农田年度变化
     * @apiNote 年度变化
     * @apiVersion 2.3.8
     * @requestDemo
     * 返回参数解析：
     *        year: 年份
     *        area: 面积
     *
     * {
     *     "type":"1",
     *     "tableNames":[
     *         {
     *             "year":"2023",
     *             "tableName":"polygon_yjjbnt_1694765316180_7uysj5"
     *         },
     *         {
     *             "year":"2022",
     *             "tableName":"polygon_yjjbnt_1698051336218_0x9jhl"
     *         }
     *     ]
     * }
     * @responseDemo
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717558001043,
     * 	"data": [
     * 		        {
     * 			"year": 2023,
     * 			"area": 632.45
     *        },
     *        {
     * 			"year": 2022,
     * 			"area": 0.46
     *        }
     * 	]
     * }
     * @tag 二、gis能力
     * @param vo 参数
     * @return Result
     */
    @PostMapping("/pg/layerAnnualChangesRes")
    Result<List<LayerAnnualChangesRes>> layerAnnualChangesRes(@RequestBody LayerAnnualChangesReq vo);

    /**
     *  返回对应经纬度的坡度坡向
     * @apiNote 坡度坡向查询
     * @apiVersion 2.3.9
     * @responseDemo
     * 返回参数解析：
     *      slope: 坡度
     *      aspect: 坡向
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717558001043,
     * 	"data": {
     * 	    "slope":"1",//坡度
     * 	    "aspect":"2"//坡向
     * 	}
     * }
     * @tag 二、gis能力
     * @param lon 参数 经度
     * @param lat 参数 维度
     * @return Result<GisSlopeAspectRes>
     */
    @GetMapping("/pg/GisSlopeAspectRes")
    Result<GisSlopeAspectRes> GisSlopeAspectRes(@RequestParam("lon")String lon, @RequestParam("lat")String lat);

    /**
     *  根据经纬度查询火势可燃物
     * @apiNote 火势可燃物查询
     * @apiVersion 2.3.10
     * @responseDemo
     * 返回参数解析：
     *    slope: 坡度
     *    aspect: 坡向
     *    moistureOneHour: 是1小时时间内死燃料中水分的部分
     *    moistureTenHour: 是10小时时间内死燃料中水分的部分
     *    moistureHundredHour: 是100小时时间内死燃料中水分的部分
     *    moistureLiveHerbaceous: 是活草本和草本中水分的部分
     *    moistureLiveWoody: 是灌木的叶子和非常细的茎中水分的部分
     *    canopyCover: 树冠覆盖百分比
     *    canopyHeight: 树高
     *    crownRatio: 树冠比
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717558001043,
     * 	"data": {
     * 	    "slope":"xx",
     * 	    "aspect":"xx",//坡向
     * 	    "moistureOneHour":"xx",
     * 	    "moistureTenHour":"xx",
     * 	    "moistureHundredHour":"xx",
     * 	    "moistureLiveHerbaceous":"xx",
     * 	    "moistureLiveWoody":"xx",
     * 	    "canopyCover":"xx",
     * 	    "canopyHeight":"xx",
     * 	    "crownRatio":"xx"
     * 	}
     * }
     * @tag 二、gis能力
     * @param lon 参数 经度
     * @param lat 参数 维度
     * @return Result<GisFireCombustibleRes>
     */
    @GetMapping("/pg/gisFireCombustibleRes")
    Result<GisFireCombustibleRes> gisFireCombustibleRes(@RequestParam("lon")String lon, @RequestParam("lat")String lat);

    /**
     * 根据经纬度对应点坐标的坡度坡向草地级别和森林占比海拔信息
     * @apiNote 火灾等级查询
     * @apiVersion 2.3.11
     * @responseDemo
     * 返回参数解析：
     *      slope: 坡度
     *      aspect: 坡向
     *      plantsSpecies: 林种，1为针叶林；2为阔叶林；3为混交林；4为竹林
     *      plantsDensity: 郁闭度  0-1之间变化，林地内树冠的垂直投影面积与林地面积之比
     *      altitude: 海拔
     *      plantsDegreeOfDrought: 温度植被干旱指数    0-1之间变化，值越大越干旱
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717558001043,
     * 	"data": {
     * 	    "slope":"xx",//坡度
     * 	    "aspect":"xx",//坡向
     * 	    "plantsSpecies":"xx",//林种，1为针叶林；2为阔叶林；3为混交林；4为竹林；5为特灌
     * 	    "plantsDensity":"xx",//郁闭度  0-1之间变化，林地内树冠的垂直投影面积与林地面积之比
     * 	    "altitude":"xx",//海拔
     * 	    "plantsDegreeOfDrought":"xx"//温度植被干旱指数    0-1之间变化，值越大越干旱
     * 	}
     * }
     * @tag 二、gis能力
     * @param lon 参数 经度
     * @param lat 参数 维度
     * @return Result<GisFireGradeRes>
     */
    @GetMapping("/pg/gisFireGradeRes")
    Result<GisFireGradeRes> gisFireGradeRes(@RequestParam("lon")String lon, @RequestParam("lat")String lat);

    /**
     * 经纬度查询植被覆盖
     */
    @GetMapping("/pg/gisVegetationDataRes")
    Result<List<GisVegetationDataRes>> gisVegetationDataRes(@RequestParam("lon")String lon, @RequestParam("lat")String lat,@RequestParam("distance")Integer distance);

}
