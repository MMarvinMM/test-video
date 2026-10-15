package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.GeoGroupVO;
import com.chinatower.bdm.api.entity.GeoVO;
import com.chinatower.bdm.api.factory.GeoserverFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.GeoserverWfsDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(contextId = "GeoserverAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = GeoserverFallbackFactory.class)
public interface GeoserverAPI {

    /**
     * Gerserver WFS 转发
     * @apiNote Gerserver WFS 转发
     * @apiVersion 13.9
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "workspace":"guotu",
     *     "params":{
     *         "version":"1.1.0",
     *         "request":"GetFeature",
     *         "typeName":"guotu:polygon_yjjbnt_1701332665151_phhlpb",
     *         "outputFormat":"application/json",
     *         "CQL_FILTER":"CONTAINS(geom,SRID=4326;POINT(116.29080453738044%2040.03942317147374))"
     *     }
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容格式统一
     * 	    properties: 根据请求参数的 typeName 不同的图层名称返回对应的图层字段及数据
     * 	    features: 返回的图层图斑数据
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1702968093420,
     * 	"data": {
     * 		"timeStamp": "2023-12-19T06:41:33.574Z",
     * 		"features": [
     *                        {
     * 				"geometry": {
     * 					"coordinates": [
     * 						[
     * 							[
     * 								[
     * 									116.29093068,
     * 									40.04075915
     * 								],
     * 								[
     * 									116.29221559,
     * 									40.03954563
     * 								],
     * 								[
     * 									116.29207282,
     * 									40.03854625
     * 								],
     * 								[
     * 									116.29028822,
     * 									40.03797518
     * 								],
     * 								[
     * 									116.289503,
     * 									40.04047362
     * 								],
     * 								[
     * 									116.29093068,
     * 									40.04075915
     * 								]
     * 							]
     * 						]
     * 					],
     * 					"type": "MultiPolygon"
     *                },
     * 				"id": "polygon_yjjbnt_1701332665151_phhlpb.10",
     * 				"geometry_name": "geom",
     * 				"type": "Feature",
     * 				"properties": {
     * 					"tbbh": "20",
     * 					"tkxs": 9.2,
     * 					"zldwmc": "北京市四叶草镇",
     * 					"xzqmc": "北京市",
     * 					"qsxz": "10",
     * 					"yjjbntmj": 79715.083168,
     * 					"layertype": "yjjbnt",
     * 					"kcdlbm": "22",
     * 					"gddj": 10,
     * 					"miny": 40.03797518385301,
     * 					"minx": 116.28950300255188,
     * 					"bz": "",
     * 					"gdlx": "PD",
     * 					"ysdm": "132313142",
     * 					"qsdwmc": "北京市四叶草镇",
     * 					"zldwdm": "13",
     * 					"tkmj": 3464.32,
     * 					"yjjbnttbmj": 79715.0831680612,
     * 					"maxy": 40.04075915322982,
     * 					"yjjbntlx": "3",
     * 					"maxx": 116.29221558809866,
     * 					"tbxhdm": "HQGD",
     * 					"qsdwdm": "12",
     * 					"gddb": 10,
     * 					"xzqdm": "23222",
     * 					"gdzzsxdm": "LYFL",
     * 					"dlmc": "水耕地",
     * 					"gdpdjb": "1",
     * 					"calarea": 46674.31824818485,
     * 					"yjjbnttbbh": "20",
     * 					"sjnf": 2007,
     * 					"tbxhmc": "林区耕地",
     * 					"frdbs": "3",
     * 					"dlbm": null,
     * 					"gdzzsxmc": "粮与非粮轮作",
     * 					"bsm": "3344513441343"
     *                }
     *            }
     * 		],
     * 		"totalFeatures": 1,
     * 		"crs": {
     * 			"type": "name",
     * 			"properties": {
     * 				"name": "urn:ogc:def:crs:EPSG::4326"
     *            }        * 		},
     * 		"numberReturned": 1,
     * 		"type": "FeatureCollection",
     * 		"numberMatched": 1
     *    }
     * }
     * @param dto
     * @return
     */
    @PostMapping("/geo/queryWfs")
    Result<Object> wfs(@RequestBody GeoserverWfsDTO dto);



    /**
     * Gerserver 上传样式文件
     * @apiNote Gerserver 上传样式文件
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.10
     * @tag 十三、网格能力
     * @param file
     * @return
     */
    @PostMapping(value = "/geo/uploadGeoStyle",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Void> uploadGeoStyle(@RequestPart("file")MultipartFile file);

    /**
     * Gerserver 删除样式文件
     * @apiNote Gerserver 删除样式文件
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.11
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "styleName":"xxx"//样式名称
     * }
     * @return
     */
    @PostMapping("/geo/deleteGeoStyle")
    Result<Void> deleteGeoStyle(@RequestBody GeoVO vo);

    /**
     * Gerserver 修改图层样式
     * @apiNote Gerserver 修改图层样式
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.12
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "styleName":"xxx",//样式名称
     *     "layerName":"xxx"//图层名称
     * }
     * @return
     */
    @ApiOperation(value = "修改图层样式")
    @PostMapping("/geo/modifyLayerStyle")
    Result<Void> modifyLayerStyle(@RequestBody GeoVO vo);

    /**
     * Gerserver 发布shp图层
     * @apiNote Gerserver 发布shp图层
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.13
     * @tag 十三、网格能力
     * @param file zip文件
     * @param styleName 样式名称
     * @return
     */
    @ApiOperation(value = "上传SHP文件")
    @PostMapping(value = "/geo/publishShp",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Void> publishShp(@RequestPart("file")MultipartFile file,@RequestParam("styleName")String styleName);

    /**
     * Gerserver 上传Tif文件
     * @apiNote Gerserver 上传Tif文件
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.14
     * @tag 十三、网格能力
     * @param file zip文件
     * @param styleName 样式名称
     * @return
     */
    @ApiOperation(value = "上传Tif文件")
    @PostMapping(value = "/geo/publishTif",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Void> publishTif(@RequestPart("file")MultipartFile file,@RequestParam("styleName")String styleName);

    /**
     * Gerserver 修改图层样式
     * @apiNote Gerserver 修改图层样式
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.15
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "layerName":"xxx",//图层名称
     *     "storeName":"xxx"//存储仓库名称
     *     "styleName":"xxx",//样式名称
     * }
     * @requestDemo
     * {
     *     "styleName":"xxx",//样式名称
     *     "layerName":"xxx",//图层名称
     *     "storeName":"xxx"//存储仓库名称
     * }
     * @return
     */
    @ApiOperation(value = "发布PostGis图层")
    @PostMapping("/geo/publishPostGis")
    Result<Void> publishPostGis(@RequestBody GeoVO vo);

    /**
     * Gerserver 发布图层组
     * @apiNote Gerserver 发布图层组
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.16
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "layerName":"xxx",//图层名称
     *     "layers":[
     *      {
     *          "layerName":"xx",图层名称
     *      }
     *      ]
     * }
     * @requestDemo
     * {
     *     "layerName":"xxx",//图层名称
     *     "layers":[
     *      {
     *          "layerName":"xx",图层名称
     *          "defaultStyle":"xx"样式名称
     *      }
     *     ]
     * }
     * @return
     */
    @ApiOperation(value = "发布图层组")
    @PostMapping("/geo/publishLayerGroup")
    Result<Void> publishLayerGroup(@RequestBody GeoGroupVO vo);


    /**
     * Gerserver 修改图层组
     * @apiNote Gerserver 修改图层组
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * @apiVersion 13.17
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "layerName":"xxx",//图层名称
     *     "layers":[
     *      {
     *          "layerName":"xx",图层名称
     *      }
     *      ]
     * }
     * @requestDemo
     * {
     *     "layerName":"xxx",//图层名称
     *     "layers":[
     *      {
     *          "layerName":"xx",图层名称
     *          "defaultStyle":"xx"样式名称
     *      }
     *     ]//图层组名称
     * }
     * @return
     */
    @ApiOperation(value = "修改图层组")
    @PostMapping("/geo/modifyLayerGroup")
    Result<Void> modifyLayerGroup(@RequestBody GeoGroupVO vo);
}
