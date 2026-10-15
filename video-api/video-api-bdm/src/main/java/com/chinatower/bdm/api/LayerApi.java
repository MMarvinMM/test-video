package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.api.factory.LayerFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "LayerApi",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = LayerFallbackFactory.class)
public interface LayerApi {

    /**
     * 获得客户的图层列表
     * @apiNote 获得客户的图层列表
     * @apiVersion 13.67
     * @tag 十三、网格能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		layerTypeName：图层类型名称
     * 		layers：图层集合
     *      id：图层id
     * 		customName：自定义图层名称
     * 		layerTypeId：图层类别
     * 		geoserverUrl：geoserver地址
     *      layerName：图层名称
     * 		layerNamespace：图层命名空间
     * 		abbreviation：图层类型简称
     *      maxx：最大x
     * 		maxy：最大y轴
     * 		minx：最小x
     *      miny：最小y
     *      rootGeoserverUrl：geoserver地址
     *      layerArea：图层面积
     *      polygonCount：图斑数量
     *      geoType：几何类型
     *      tenantId: 不用关注
     *      creator: 不用关注
     *      modifier: 不用关注
     *      geoserverUrl: 不用关注
     *      viewUrl: 不用关注
     *      nodeCode: 不用关注
     *      nestFlag: 不用关注
     *      category: 1-矢量 2-非矢量
     *      fileUrl: 文件obs地址
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": {
     *          "layerTypeName":"ads",
     *          "abbreviation": "slzy",
     *         "layers": [
     *           {
     *             "id": "a2caf8c686b9422b8022fa95667f0253",
     *             "customName": "点资源",
     *             "layerTypeName": "森林资源",
     *             "layerTypeId": "1e62d628-e765-11ee-af60-fa163eb07f55",
     *             "tenantId": "120046990",
     *             "tenantName": "测试客户0010",
     *             "industryName": "林业应用",
     *             "appVerCode": "1.0",
     *             "creator": "weichuang001",
     *             "modifier": null,
     *             "geoserverUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *             "viewUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:view_polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *             "layerName": "polygon_slzy_1725526217669_64khwi",
     *             "layerNamespace": "guotu",
     *             "gameKey": null,
     *             "createTime": "2024-09-05 16:50:19",
     *             "updateTime": null,
     *             "maxx": 117.58618927001953,
     *             "maxy": 35.786251068115234,
     *             "minx": 117.09569549560547,
     *             "miny": 35.463050842285156,
     *             "abbreviation": "slzy",
     *             "rootGeoserverUrl": "http://10.43.86.213/geoserver",
     *             "layerArea": 202211.84,
     *             "polygonCount": 1724,
     *             "geoType": "POINT",
     *             "nodeCode": null,
     *             "nodeName": null,
     *             "nestFlag": "0",
     *             "category": "1",
     *             "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ecd0c97ddb7c95b8db34f721db7d595f/c5241266b12ad818caa00b8b6477343d/bc69688924f940cd9131a054aee7546f.zip?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1725529812&Signature=DgitFis%2BIByZNP06XD64w6YiV7I%3D",
     *           }
     *         ]
     *     }
     * }
     *
     *
     * @param dto
     * @return
     */
    @GetMapping("/layer/getTenantList")
    Result<List<TenantLayerListDTO>> getTenantLayerList(@SpringQueryMap LayerQueryDTO dto);


    /**
     * 获得客户的图层图斑列表
     * @deprecated
     */
    @GetMapping("/layerPolygon/getTenantList")
    Result<List<LayerPolygonDTO>> getTenantLayerPolygonList(LayerQueryDTO dto);

    /**
     * 新增图层图斑
     * @deprecated
     */
    @PostMapping("/layerPolygon/add")
    Result<String> addTenantLayerPolygonList(@RequestBody LayerPolygonAddDTO dto);

    /**
     * 全量查询图层中文和拼音对照表
     * @apiNote 全量查询图层中文和拼音对照表
     * @apiVersion 13.68
     * @tag 十三、网格能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		code：编码
     * 	    name：名称
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692351918803,
     * 	"data": [
     *                {
     * 			"code": "111",
     * 			"name": "222"
     *        }
     * 	]
     * }
     * @param polygonAttrDictVO
     * @return
     */
    @PostMapping("/layer/getPolygonAttrDict")
    Result<List<PolygonAttrDictVO>> getPolygonAttrDict(@RequestBody PolygonAttrDictVO polygonAttrDictVO);


    /**
     * 工具箱-地块查询，构造查询弹窗属性
     * @apiNote 工具箱-地块查询，构造查询弹窗属性
     * @apiVersion 13.69
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"abbreviation": "jsydgzq"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		layerTypeId：图层类别
     * 		filterType：筛选类型
     * 		filterAttr：筛选属性
     * 		abbreviation：图层类型简称
     * 		filterAttrKey：字典key
     * 		filterAttrValue：字典value
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1692951917470,
     *     "data": {
     *         "jsydgzq": [
     *             {
     *                 "abbreviation": "jsydgzq",
     *                 "filterType": "GZQLXDM",
     *                 "filterAttrKey": "010",
     *                 "filterAttrValue": "允许建设用地区",
     *                 "showType": "1"
     *             },
     *             {
     *                 "abbreviation": "jsydgzq",
     *                 "filterType": "GZQLXDM",
     *                 "filterAttrKey": "020",
     *                 "filterAttrValue": "有条件建设用地区",
     *                 "showType": "1"
     *             },
     *             {
     *                 "abbreviation": "jsydgzq",
     *                 "filterType": "GZQLXDM",
     *                 "filterAttrKey": "030",
     *                 "filterAttrValue": "限制建设用地区",
     *                 "showType": "1"
     *             },
     *             {
     *                 "abbreviation": "jsydgzq",
     *                 "filterType": "GZQLXDM",
     *                 "filterAttrKey": "040",
     *                 "filterAttrValue": "禁止建设用地区",
     *                 "showType": "1"
     *             }
     *         ]
     *     }
     * }
     * @param layerTypeAttrTilterVO
     * @return Result
     */
    @PostMapping("/layer/getLayerTypeAttrFilter")
    Result<Map<String, List<LayerTypeAttrTilterVO>>> getLayerTypeAttrFilter(@RequestBody LayerTypeAttrTilterVO layerTypeAttrTilterVO);

    /**
     * 永久基本农田面积
     * @apiNote 永久基本农田面积
     * @apiVersion 13.70
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "abbreviation": "yjjbnt",
     *     "layerTypeId":"c8200135ba686a454dbfe3fa1b8be2fe"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		layerTypeId：图层类别
     * 		layerTypeName：图层名称
     * 		area：面积
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694597191738,
     * 	"data": [
     *                {
     * 			"layerTypeId": "c8200135ba686a454dbfe3fa1b8be2fe",
     * 			"layerTypeName": "基本农田",
     * 			"area": "2453"
     *        }
     * 	]
     * }
     * @param layerAreaVO
     * @return Result
     */
    @PostMapping("/layer/getLayerArea")
    Result<List<LayerAreaVO>> getLayerArea(@RequestBody LayerAreaVO layerAreaVO);



    /**
     * 根据图层图斑信息查询设备通道
     * @apiNote 根据图层图斑信息查询设备通道
     * @apiVersion 13.71
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "layerId": "11b320b9f4fbf86bb5f4c56449dc8a0f",
     *     "featureId": "lhx.4"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		deviceCode：设备编码
     * 		channelCode：通道编码
     * 		longitude：经度
     * 	    latitude：纬度
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1693448700205,
     * 	"data": {
     * 		"deviceCode": "42282500831327000025",
     * 		"channelCode": "42282500001310000004",
     * 		"longitude": "109.451461",
     * 		"latitude": "29.988466"
     *        }
     * }
     * @param inspectionRouteRelLayerSdkVO
     * @return
     */
    @PostMapping("/layer/getDeviceByPolygon")
    Result<InspectionRouteRelLayerSdkVO> getDeviceByPolygon(@RequestBody InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO);

    /**
     * 深林资源图层列表
     * @apiNote 深林资源图层列表
     * @apiVersion 13.72
     * @tag 十三、网格能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		layerTypeName：图层类型名称
     * 		layers：图层集合
     *      id：图层id
     * 		customName：自定义图层名称
     * 		layerTypeId：图层类别
     * 		geoserverUrl：geoserver地址
     *      layerName：图层名称
     * 		layerNamespace：图层命名空间
     * 		abbreviation：图层类型简称
     *      maxx：最大x
     * 		maxy：最大y轴
     * 		minx：最小x
     *      miny：最小y
     *      rootGeoserverUrl：geoserver地址
     *      layerArea：图层面积
     *      polygonCount：图斑数量
     *      geoType：几何类型
     *      tenantId: 不用关注
     *      creator: 不用关注
     *      modifier: 不用关注
     *      geoserverUrl: 不用关注
     *      viewUrl: 不用关注
     *      nodeCode: 不用关注
     *      nestFlag: 不用关注
     *      category: 1-矢量 2-非矢量
     *      fileUrl: 文件obs地址
     *
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1736402742544,
     *   "data": [
     *     {
     *       "id": "a2caf8c686b9422b8022fa95667f0253",
     *       "customName": "点资源",
     *       "layerTypeName": "森林资源",
     *       "layerTypeId": "1e62d628-e765-11ee-af60-fa163eb07f55",
     *       "tenantId": "120046990",
     *       "tenantName": "测试客户0010",
     *       "industryName": "林业应用",
     *       "appVerCode": "1.0",
     *       "creator": "weichuang001",
     *       "modifier": null,
     *       "geoserverUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "viewUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:view_polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "layerName": "polygon_slzy_1725526217669_64khwi",
     *       "layerNamespace": "guotu",
     *       "gameKey": null,
     *       "createTime": "2024-09-05 16:50:19",
     *       "updateTime": null,
     *       "maxx": 117.58618927001953,
     *       "maxy": 35.786251068115234,
     *       "minx": 117.09569549560547,
     *       "miny": 35.463050842285156,
     *       "abbreviation": "slzy",
     *       "rootGeoserverUrl": "http://10.43.86.213/geoserver",
     *       "layerArea": 202211.84,
     *       "polygonCount": 1724,
     *       "geoType": "POINT",
     *       "nodeCode": null,
     *       "nodeName": null,
     *       "nestFlag": "0",
     *       "category": "1",
     *       "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ecd0c97ddb7c95b8db34f721db7d595f/c5241266b12ad818caa00b8b6477343d/bc69688924f940cd9131a054aee7546f.zip?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1725529812&Signature=DgitFis%2BIByZNP06XD64w6YiV7I%3D",
     *     }
     *   ]
     * }
     * @param dto
     * @return
     */
    @GetMapping("/layer/slzyLayerList")
    Result<List<LayerResponseDTO>> slzyLayerList(LayerQueryDTO dto);


    /**
     * 上传图层
     * @apiNote 上传图层
     * @apiVersion 13.73
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "customName":"图层自定义名称",
     *     "layerTypeId":"类型ID",
     *     "fileName":"文件名称",
     *     "fileUrl":"文件地址"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     *
     * {
     *  "code": 200,
     *  "msg": "请求成功",
     *  "time": 1736402742544
     * }
     * @param dto
     * @return
     */
    @PostMapping("/layer/addOrModify")
    Result<String> addLayer(@RequestBody LayerAddDTO dto);

    /**
     * 返回对应图层类型简称的全部字段
     * @apiNote 返回对应图层类型简称的全部字段
     * @apiVersion 13.74
     * @tag 十三、网格能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 	    code：字段代码
     * 	    name：字段名称
     * 	    remark：字段描述
     * 	    filterType：筛选类型	0-输入框 1-下拉框
     * 	    type 类型  1:int 2:double 3:varchart 4:timestamp
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1706148515349,
     * 	"data": [
     *       {
     * 			"code": "bsm",
     * 			"name": "标识码",
     * 			"remark": "标识码字段信息",
     * 			"filterType": 0,
     * 			"type": 3
     *        },
     *        {
     * 			"code": "ysdm",
     * 			"name": "要素代码",
     * 			"remark": "要素代码字段信息",
     * 			"filterType": 0,
     * 			"type": 3
     *        }
     * 	]
     * }
     * @param abbreviation 类型简称
     * @return 返回类型全部字段
     */
    @GetMapping("/layerTypeFields/layerFields")
    Result<List<LayerFieldsDTO>> layerFields(@RequestParam("abbreviation")String abbreviation);

    /**
     * 返回图层类型对应的字段的和字典
     * @apiNote 返回图层类型对应的字段的和字典
     * @apiVersion 13.75
     * @tag 十三、网格能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 	    code：字段代码
     * 	    name：字段名称
     * 	    remark：字段描述
     * 	    filterType：筛选类型	0-输入框 1-下拉框
     * 	    childList：下拉框的字典选项
     * 	    filterAttrKey：字典值
     * 	    filterAttrValue：字典描述
     * 	    id: 不用关注
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1706148610625,
     * 	"data": [
     *                {
     * 			"code": "tbbh",
     * 			"name": "图斑编号",
     * 			"remark": "图斑编号字段信息",
     * 			"filterType": 0,
     * 			"childList": [
     *                {
     * 					"id": "21",
     * 					"code": "tbbh",
     * 					"filterAttrKey": null,
     * 					"filterAttrValue": null
     *                }
     * 			]
     *        },
     *        {
     * 			"code": "gdzzsxmc",
     * 			"name": "种植属性名称",
     * 			"remark": "种植属性名称字段信息",
     * 			"filterType": 1,
     * 			"childList": [
     *                {
     * 					"id": "32",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "LS",
     * 					"filterAttrValue": "种植粮食作物"
     *                },
     *                {
     * 					"id": "33",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "FLS",
     * 					"filterAttrValue": "种植非粮食作物"
     *                },
     *                {
     * 					"id": "34",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "LYFL",
     * 					"filterAttrValue": "粮与非粮轮作"
     *                },
     *                {
     * 					"id": "35",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "WG",
     * 					"filterAttrValue": "未耕种"
     *                },
     *                {
     * 					"id": "36",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "XG",
     * 					"filterAttrValue": "休耕"
     *                },
     *                {
     * 					"id": "37",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "LLJZ",
     * 					"filterAttrValue": "林粮间作"
     *                },
     *                {
     * 					"id": "38",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "JKHF",
     * 					"filterAttrValue": "即可恢复"
     *                },
     *                {
     * 					"id": "39",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "GCHF",
     * 					"filterAttrValue": "工程恢复"
     *                }
     * 			]
     *        }
     * 	]
     * }
     * @param abbreviation
     * @return 返回图层可以用于筛选的数据
     */
    @GetMapping("/layerTypeFields/layerFieldsFilters")
    Result<List<LayerFieldsFilterDTO>> layerFieldsFilters(@RequestParam("abbreviation") String abbreviation);


    /**
     * 用于聚合查询的图层字段信息
     * @apiNote 用于聚合查询的图层字段信息
     * @apiVersion 13.76
     * @tag 十三、网格能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 	    code：字段代码
     * 	    name：字段名称
     * 	    remark：字段描述
     * 	    filterType：筛选类型	0-输入框 1-下拉框
     * 	    childList：下拉框的字典选项
     * 	    filterAttrKey：字典值
     * 	    filterAttrValue：字典描述
     * 	    id: 不用关注
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1706148610625,
     * 	"data": [
     *                {
     * 			"code": "tbbh",
     * 			"name": "图斑编号",
     * 			"remark": "图斑编号字段信息",
     * 			"filterType": 0,
     * 			"childList": [
     *                {
     * 					"id": "21",
     * 					"code": "tbbh",
     * 					"filterAttrKey": null,
     * 					"filterAttrValue": null
     *                }
     * 			]
     *        },
     *        {
     * 			"code": "dlmc",
     * 			"name": "地类名称",
     * 			"remark": "地类名称字段信息",
     * 			"filterType": 1,
     * 			"childList": [
     *                {
     * 					"id": "24",
     * 					"code": "dlmc",
     * 					"filterAttrKey": "001",
     * 					"filterAttrValue": "水田"
     *                },
     *                {
     * 					"id": "25",
     * 					"code": "dlmc",
     * 					"filterAttrKey": "013",
     * 					"filterAttrValue": "旱地"
     *                },
     *                {
     * 					"id": "26",
     * 					"code": "dlmc",
     * 					"filterAttrKey": "012",
     * 					"filterAttrValue": "水浇地"
     *                }
     * 			]
     *        },
     *        {
     * 			"code": "gdlx",
     * 			"name": "耕地类型",
     * 			"remark": "耕地类型字段信息",
     * 			"filterType": 1,
     * 			"childList": [
     *                {
     * 					"id": "22",
     * 					"code": "gdlx",
     * 					"filterAttrKey": "PD",
     * 					"filterAttrValue": "坡地耕地"
     *                },
     *                {
     * 					"id": "23",
     * 					"code": "gdlx",
     * 					"filterAttrKey": "TT",
     * 					"filterAttrValue": "梯田耕地"
     *                }
     * 			]
     *        },
     *        {
     * 			"code": "gdpdjb",
     * 			"name": "耕地坡度级别",
     * 			"remark": "耕地坡度级别字段信息",
     * 			"filterType": 1,
     * 			"childList": [
     *                {
     * 					"id": "27",
     * 					"code": "gdpdjb",
     * 					"filterAttrKey": "1",
     * 					"filterAttrValue": "≤2°"
     *                },
     *                {
     * 					"id": "28",
     * 					"code": "gdpdjb",
     * 					"filterAttrKey": "2",
     * 					"filterAttrValue": "（2°～6°]"
     *                },
     *                {
     * 					"id": "29",
     * 					"code": "gdpdjb",
     * 					"filterAttrKey": "3",
     * 					"filterAttrValue": "（6°～15°]"
     *                },
     *                {
     * 					"id": "30",
     * 					"code": "gdpdjb",
     * 					"filterAttrKey": "4",
     * 					"filterAttrValue": "（15°～25°]"
     *                },
     *                {
     * 					"id": "31",
     * 					"code": "gdpdjb",
     * 					"filterAttrKey": "5",
     * 					"filterAttrValue": "＞25°"
     *                }
     * 			]
     *        },
     *        {
     * 			"code": "gdzzsxmc",
     * 			"name": "种植属性名称",
     * 			"remark": "种植属性名称字段信息",
     * 			"filterType": 1,
     * 			"childList": [
     *                {
     * 					"id": "32",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "LS",
     * 					"filterAttrValue": "种植粮食作物"
     *                },
     *                {
     * 					"id": "33",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "FLS",
     * 					"filterAttrValue": "种植非粮食作物"
     *                },
     *                {
     * 					"id": "34",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "LYFL",
     * 					"filterAttrValue": "粮与非粮轮作"
     *                },
     *                {
     * 					"id": "35",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "WG",
     * 					"filterAttrValue": "未耕种"
     *                },
     *                {
     * 					"id": "36",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "XG",
     * 					"filterAttrValue": "休耕"
     *                },
     *                {
     * 					"id": "37",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "LLJZ",
     * 					"filterAttrValue": "林粮间作"
     *                },
     *                {
     * 					"id": "38",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "JKHF",
     * 					"filterAttrValue": "即可恢复"
     *                },
     *                {
     * 					"id": "39",
     * 					"code": "gdzzsxmc",
     * 					"filterAttrKey": "GCHF",
     * 					"filterAttrValue": "工程恢复"
     *                }
     * 			]
     *        },
     *        {
     * 			"code": "gddj",
     * 			"name": "耕地质量等级",
     * 			"remark": "耕地质量等级字段信息",
     * 			"filterType": 1,
     * 			"childList": [
     *                {
     * 					"id": "40",
     * 					"code": "gddj",
     * 					"filterAttrKey": "01",
     * 					"filterAttrValue": "一等地"
     *                },
     *                {
     * 					"id": "41",
     * 					"code": "gddj",
     * 					"filterAttrKey": "02",
     * 					"filterAttrValue": "二等地"
     *                },
     *                {
     * 					"id": "42",
     * 					"code": "gddj",
     * 					"filterAttrKey": "03",
     * 					"filterAttrValue": "三等地"
     *                },
     *                {
     * 					"id": "43",
     * 					"code": "gddj",
     * 					"filterAttrKey": "04",
     * 					"filterAttrValue": "四等地"
     *                },
     *                {
     * 					"id": "44",
     * 					"code": "gddj",
     * 					"filterAttrKey": "05",
     * 					"filterAttrValue": "五等地"
     *                },
     *                {
     * 					"id": "45",
     * 					"code": "gddj",
     * 					"filterAttrKey": "06",
     * 					"filterAttrValue": "六等地"
     *                },
     *                {
     * 					"id": "46",
     * 					"code": "gddj",
     * 					"filterAttrKey": "07",
     * 					"filterAttrValue": "七等地"
     *                },
     *                {
     * 					"id": "47",
     * 					"code": "gddj",
     * 					"filterAttrKey": "08",
     * 					"filterAttrValue": "八等地"
     *                },
     *                {
     * 					"id": "48",
     * 					"code": "gddj",
     * 					"filterAttrKey": "09",
     * 					"filterAttrValue": "九等地"
     *                },
     *                {
     * 					"id": "49",
     * 					"code": "gddj",
     * 					"filterAttrKey": "10",
     * 					"filterAttrValue": "十等地"
     *                }
     * 			]
     *        }
     * 	]
     * }
     * @param abbreviation 类型简称
     * @return 返回图层可以用于筛选的数据
     */
    @GetMapping("/layerTypeFields/advancedQueryLayerFields")
    Result<List<LayerFieldsFilterDTO>> advancedQueryLayerFields(@RequestParam("abbreviation") String abbreviation);


    /**
     * 查询图层类型字段
     * @apiNote 返回字段列表的数据
     * @apiVersion 13.77
     * @tag 十三、网格能力
     * @param abbreviation 类型简称
     * @responseDemo
     * 返回参数解析：
     *             "id": 标识,
     *             "abbreviation": 简称,
     *             "code": 字段代码,
     *             "name": 字段显示名称,
     *             "remark": 备注，说明,
     *             "type": 类型 1:int 2:double 3:varchart 4:timestamp,
     *             "chartLength": 字符类型长度,
     *             "notNullFlag": 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空）,
     *             "filterType": 筛选类型（0文本，1下拉，默认：2空）,
     *             "filterIndex": 属性查询功能的顺序号（默认为0，起始为1）,
     *             "identifyIndex": 识别功能的顺序号,
     *             "resultIndex": 结果列表的顺序号,
     *             "hasArea": 是否面积字段,
     *             "hasTbbh": 是否图斑编号,
     *             "filters": null
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": [
     *         {
     *             "id": "e82fce55-b90a-11ee-af60-fa163eb07f55",
     *             "abbreviation": "xztb",
     *             "code": "dlmc",
     *             "name": "地类名称",
     *             "remark": "地类名称字段信息",
     *             "type": 3,
     *             "chartLength": 20,
     *             "notNullFlag": 1,
     *             "filterType": 1,
     *             "filterIndex": 1,
     *             "identifyIndex": 1,
     *             "resultIndex": 4,
     *             "hasArea": 0,
     *             "hasTbbh": 0,
     *             "filters": null
     *         },
     *         {
     *             "id": "e831f4e3-b90a-11ee-af60-fa163eb07f55",
     *             "abbreviation": "xztb",
     *             "code": "qsxz",
     *             "name": "权属性质",
     *             "remark": "权属性质字段信息",
     *             "type": 3,
     *             "chartLength": 10,
     *             "notNullFlag": 0,
     *             "filterType": 1,
     *             "filterIndex": 0,
     *             "identifyIndex": 0,
     *             "resultIndex": 5,
     *             "hasArea": 0,
     *             "hasTbbh": 0,
     *             "filters": null
     *         },
     *         {
     *             "id": "e8344b11-b90a-11ee-af60-fa163eb07f55",
     *             "abbreviation": "xztb",
     *             "code": "qsdwdm",
     *             "name": "权属单位代码",
     *             "remark": "权属单位代码字段信息",
     *             "type": 3,
     *             "chartLength": 50,
     *             "notNullFlag": 0,
     *             "filterType": 0,
     *             "filterIndex": 0,
     *             "identifyIndex": 0,
     *             "resultIndex": 6,
     *             "hasArea": 0,
     *             "hasTbbh": 0,
     *             "filters": null
     *         }
     *     ]
     * }
     *
     * @return 根据简称获取所有字典
     */
    @GetMapping("/layerType/dictByAbbreviation")
    public Result<List<LayerTypeAttrFilterVO>> dictByAbbreviation(@RequestParam("abbreviation")String abbreviation);


    /**
     * 2.6.1 查询租户、行业下的默认套合图层信息
     *
     * 获取租户、行业矢量图层的所有默认套合图层
     * @param layerQueryDTO  tenantId industryCode
     * @return List<LayerResponseDTO>
     */
    @PostMapping("/layer/getNestList")
    Result<Map<String, LayerResponseDTO>> getNestList(@RequestBody LayerQueryDTO layerQueryDTO);


    /**
     * 获得图层面积图层数量
     * @apiNote 获得图层面积图层数量
     * @apiVersion 13.78
     * @tag 十三、网格能力
     * @param layerIds 图层ID
     * @responseDemo
     *      返回参数解析:
     *          layerId 图层ID
     *          layerName 图层名称
     *          areaCount 图层面积
     *          polygonCount 多边形图层数量
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": [
     *         {
     *             "layerId": "e8395c0d-b90a-11ee-af60-fa163eb07f55",
     *             "layerName": "图层名称",
     *             "areaCount": 0,
     *             "polygonCount": 0
     *         }
     *     ]
     *}
     * @return Result
     */
    @PostMapping("/layer/layerAreaCount")
    Result<List<LayerAreaCountVO>> layerAreaCount(@RequestBody List<String> layerIds);

    /**
     * 查询探而未采图层ID
     * @apiNote 查询探而未采图层ID
     * @apiVersion 13.79
     * @tag 十三、网格能力
     * @responseDemo
     *      返回参数解析:
     *          layerId 图层ID
     *          status 状态 0-计算中 1-计算完成 2-数据变更 3-丢弃 4-图层缺失
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": [
     *         {
     *             "layerId": "e8395c0d-b90a-11ee-af60-fa163eb07f55",
     *             "status": "e8395c0d-b90a-11ee-af60-fa163eb07f55"
     *         }
     *     ]
     *}
     * @return Result
     */
    @PostMapping("/layer/mineralsLayer")
    Result<MineralsLayerVO> mineralsLayer();

    /**
     * 查询非矢量列表
     * @param dto dto
     * @return 非矢量列表
     */
    @ApiOperation(value = "非矢量图层列表", notes = "非矢量图层列表")
    @GetMapping("/layer/nonVector/getTenantList")
    Result<List<LayerResponseDTO>> getNonVectorTenantList(LayerQueryDTO dto);

    /**
     * 图层列表-非矢量图层列表
     * @param dto
     * @return
     */
    @ApiOperation(value = "图层列表", notes = "非矢量图层列表")
    @PostMapping("/layer/getTenantList/multiType")
    Result<Map<String, List<LayerResponseDTO>>> getTenantListMultiType(@RequestBody LayerQueryDTO dto);

    /**
     * 根据图层类型筛选图层列表
     * @apiNote 图层列表查询
     * @apiVersion 13.80
     * @tag 十三、网格能力
     * @param abbreviation 简称
     * @param layerTypeId 图层类型ID
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		layerTypeName：图层类型名称
     * 		layers：图层集合
     *      id：图层id
     * 		customName：自定义图层名称
     * 		layerTypeId：图层类别
     * 		geoserverUrl：geoserver地址
     *      layerName：图层名称
     * 		layerNamespace：图层命名空间
     * 		abbreviation：图层类型简称
     *      maxx：最大x
     * 		maxy：最大y轴
     * 		minx：最小x
     *      miny：最小y
     *      rootGeoserverUrl：geoserver地址
     *      layerArea：图层面积
     *      polygonCount：图斑数量
     *      geoType：几何类型
     *      tenantId: 不用关注
     *      creator: 不用关注
     *      modifier: 不用关注
     *      geoserverUrl: 不用关注
     *      viewUrl: 不用关注
     *      nodeCode: 不用关注
     *      nestFlag: 不用关注
     *      category: 1-矢量 2-非矢量
     *      fileUrl: 文件obs地址
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": [
     *     {
     *       "id": "a2caf8c686b9422b8022fa95667f0253",
     *       "customName": "点资源",
     *       "layerTypeName": "森林资源",
     *       "layerTypeId": "1e62d628-e765-11ee-af60-fa163eb07f55",
     *       "tenantId": "120046990",
     *       "tenantName": "测试客户0010",
     *       "industryName": "林业应用",
     *       "appVerCode": "1.0",
     *       "creator": "weichuang001",
     *       "modifier": null,
     *       "geoserverUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "viewUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:view_polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "layerName": "polygon_slzy_1725526217669_64khwi",
     *       "layerNamespace": "guotu",
     *       "gameKey": null,
     *       "createTime": "2024-09-05 16:50:19",
     *       "updateTime": null,
     *       "maxx": 117.58618927001953,
     *       "maxy": 35.786251068115234,
     *       "minx": 117.09569549560547,
     *       "miny": 35.463050842285156,
     *       "abbreviation": "slzy",
     *       "rootGeoserverUrl": "http://10.43.86.213/geoserver",
     *       "layerArea": 202211.84,
     *       "polygonCount": 1724,
     *       "geoType": "POINT",
     *       "nodeCode": null,
     *       "nodeName": null,
     *       "nestFlag": "0",
     *       "category": "1",
     *       "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ecd0c97ddb7c95b8db34f721db7d595f/c5241266b12ad818caa00b8b6477343d/bc69688924f940cd9131a054aee7546f.zip?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1725529812&Signature=DgitFis%2BIByZNP06XD64w6YiV7I%3D",
     *     }
     *   ]
     * }
     * @return Result
     */
    @GetMapping("/layer/selectLayerList")
    Result<List<LayerResponseDTO>> selectLayerList(@RequestParam("abbreviation")String abbreviation,@RequestParam("layerTypeId")String layerTypeId);


    /**
     * 根据图层类型筛选图层列表
     * @apiNote 图层列表查询
     * @apiVersion 13.80-1
     * @tag 十三、网格能力
     * @param layerId t图层ID
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		layerTypeName：图层类型名称
     * 		layers：图层集合
     *      id：图层id
     * 		customName：自定义图层名称
     * 		layerTypeId：图层类别
     * 		geoserverUrl：geoserver地址
     *      layerName：图层名称
     * 		layerNamespace：图层命名空间
     * 		abbreviation：图层类型简称
     *      maxx：最大x
     * 		maxy：最大y轴
     * 		minx：最小x
     *      miny：最小y
     *      rootGeoserverUrl：geoserver地址
     *      layerArea：图层面积
     *      polygonCount：图斑数量
     *      geoType：几何类型
     *      tenantId: 不用关注
     *      creator: 不用关注
     *      modifier: 不用关注
     *      geoserverUrl: 不用关注
     *      viewUrl: 不用关注
     *      nodeCode: 不用关注
     *      nestFlag: 不用关注
     *      category: 1-矢量 2-非矢量
     *      fileUrl: 文件obs地址
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": [
     *     {
     *       "id": "a2caf8c686b9422b8022fa95667f0253",
     *       "customName": "点资源",
     *       "layerTypeName": "森林资源",
     *       "layerTypeId": "1e62d628-e765-11ee-af60-fa163eb07f55",
     *       "tenantId": "120046990",
     *       "tenantName": "测试客户0010",
     *       "industryName": "林业应用",
     *       "appVerCode": "1.0",
     *       "creator": "weichuang001",
     *       "modifier": null,
     *       "geoserverUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "viewUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:view_polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "layerName": "polygon_slzy_1725526217669_64khwi",
     *       "layerNamespace": "guotu",
     *       "gameKey": null,
     *       "createTime": "2024-09-05 16:50:19",
     *       "updateTime": null,
     *       "maxx": 117.58618927001953,
     *       "maxy": 35.786251068115234,
     *       "minx": 117.09569549560547,
     *       "miny": 35.463050842285156,
     *       "abbreviation": "slzy",
     *       "rootGeoserverUrl": "http://10.43.86.213/geoserver",
     *       "layerArea": 202211.84,
     *       "polygonCount": 1724,
     *       "geoType": "POINT",
     *       "nodeCode": null,
     *       "nodeName": null,
     *       "nestFlag": "0",
     *       "category": "1",
     *       "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ecd0c97ddb7c95b8db34f721db7d595f/c5241266b12ad818caa00b8b6477343d/bc69688924f940cd9131a054aee7546f.zip?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1725529812&Signature=DgitFis%2BIByZNP06XD64w6YiV7I%3D",
     *     }
     *   ]
     * }
     * @return Result
     */
    @GetMapping("/layer/selectLayerById")
    Result<LayerResponseDTO> selectLayerById(@RequestParam("layerId")String layerId);


    /**
     * 租户 + 行业 下 分组面积统计
     * @apiNote 租户 + 行业 下 分组面积统计
     *
     * @apiVersion 13.81
     * @tag 十三、网格能力
     * @param code 字段
     * @param ratioFlag 是否计算比例
     * @responseDemo
     * 返回参数解析：
     *         code：返回编码
     *         msg：返回描述
     *         data：返回内容
     *         code：字段
     *         area：面积
     *         count：数量
     *         name：名称
     *         ratio：比例
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": [
     *         {
     *             "code": bsm,
     *             "area": 35.95,
     *             "count": 10,
     *             "name": "标识码"
     *         }
     *     ]
     *}
     * @return Result
     */
    @GetMapping("/layer/layerClassifyAreaStatistics")
    Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(@RequestParam("code")String code,@RequestParam(value = "ratioFlag",required = false)Integer ratioFlag,@RequestParam("abbreviation")String abbreviation);

    /**
     * 租户 + 行业 下 所有面积和小班数量
     * @apiNote 租户 + 行业 下 所有面积和小班数量
     * @apiVersion 13.82
     * @tag 十三、网格能力
     * @param abbreviation 简称
     * @responseDemo
     * 返回参数解析：
     *         code：返回编码
     *         msg：返回描述
     *         data：返回内容
     *         area：面积
     *         count：数量
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": {
     *         "area": 35.95,
     *         "count": 10
     *     }
     * @return Result
     */
    @GetMapping("/layer/layerCountAndArea")
    Result<LayerCountAndAreaDTO> layerCountAndArea(@RequestParam("abbreviation")String abbreviation);

    /**
     * 探而未采图层计算
     * @apiNote 探而未采图层计算
     * @apiVersion 13.83
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "tenantId": "120046990",
     *     "industryCode": "1002",
     *     "tkq":"图层表名称",
     *     "ckq":"图层表名称",
     *     "userName":"用户名称",
     * }
     * @responseDemo
     * 返回参数解析：
     *         code：返回编码
     *         msg：返回描述
     *         data：返回内容
     *         layerId：图层ID
     *         status：状态 0-计算中 1-计算完成 2-数据变更 3-丢弃 4-图层缺失
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": {
     *         "area": 35.95,
     *         "status": 1
     *     }
     * @return Result
     */
    @PostMapping("/layer/computeTewcLayer")
    Result<MineralsLayerVO> computeTewcLayer(@RequestBody MineralsLayerReqVO vo);

    /**
     * 删除图层
     * @apiNote 删除图层
     * @apiVersion 13.84
     * @tag 十三、网格能力
     * @requestDemo
     * 返回参数解析：
     *         code：返回编码
     *         msg：返回描述
     *         data：返回内容
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": null
     * }
     * @return Result
     */
    @PostMapping("/layer/deleted")
    Result<String> deleted(@RequestBody DeleteDTO vo);

    /**
     * 图层列表
     * @apiNote 图层列表
     * @apiVersion 13.85
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *  "abbreviation":"2",
     *  "customName":"",
     *  "industryCode":"4",
     *  "layerId":"2",
     *  "layerIds":["1","2"],
     *  "layerTypeId":"1",
     *  "tenantId":"3"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		layerTypeName：图层类型名称
     * 		layers：图层集合
     *      id：图层id
     * 		customName：自定义图层名称
     * 		layerTypeId：图层类别
     * 		geoserverUrl：geoserver地址
     *      layerName：图层名称
     * 		layerNamespace：图层命名空间
     * 		abbreviation：图层类型简称
     *      maxx：最大x
     * 		maxy：最大y轴
     * 		minx：最小x
     *      miny：最小y
     *      rootGeoserverUrl：geoserver地址
     *      layerArea：图层面积
     *      polygonCount：图斑数量
     *      geoType：几何类型
     *      tenantId: 不用关注
     *      creator: 不用关注
     *      modifier: 不用关注
     *      geoserverUrl: 不用关注
     *      viewUrl: 不用关注
     *      nodeCode: 不用关注
     *      nestFlag: 不用关注
     *      category: 1-矢量 2-非矢量
     *      fileUrl: 文件obs地址
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1706146040840,
     *     "data": [
     *     {
     *       "id": "a2caf8c686b9422b8022fa95667f0253",
     *       "customName": "点资源",
     *       "layerTypeName": "森林资源",
     *       "layerTypeId": "1e62d628-e765-11ee-af60-fa163eb07f55",
     *       "tenantId": "120046990",
     *       "tenantName": "测试客户0010",
     *       "industryName": "林业应用",
     *       "appVerCode": "1.0",
     *       "creator": "weichuang001",
     *       "modifier": null,
     *       "geoserverUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "viewUrl": "http://10.43.86.213/geoserver/gwc/service/tms/1.0.0/guotu:view_polygon_slzy_1725526217669_64khwi@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf",
     *       "layerName": "polygon_slzy_1725526217669_64khwi",
     *       "layerNamespace": "guotu",
     *       "gameKey": null,
     *       "createTime": "2024-09-05 16:50:19",
     *       "updateTime": null,
     *       "maxx": 117.58618927001953,
     *       "maxy": 35.786251068115234,
     *       "minx": 117.09569549560547,
     *       "miny": 35.463050842285156,
     *       "abbreviation": "slzy",
     *       "rootGeoserverUrl": "http://10.43.86.213/geoserver",
     *       "layerArea": 202211.84,
     *       "polygonCount": 1724,
     *       "geoType": "POINT",
     *       "nodeCode": null,
     *       "nodeName": null,
     *       "nestFlag": "0",
     *       "category": "1",
     *       "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ecd0c97ddb7c95b8db34f721db7d595f/c5241266b12ad818caa00b8b6477343d/bc69688924f940cd9131a054aee7546f.zip?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1725529812&Signature=DgitFis%2BIByZNP06XD64w6YiV7I%3D",
     *     }
     *   ]
     * }
     * @return Result
     */
    @PostMapping("/layer/baseLayerList")
    Result<List<LayerResponseDTO>> baseLayerList(@RequestBody BaseLayerListQueryDTO dto);

    /**
     * 根据图层类型，汇总数量
     * @param dto
     * @return
     */
    @PostMapping("/layer/getLayerCount")
    Result<List<LayerCountRes>> getLayerCount(@RequestBody LayerQueryDTO dto);
}
