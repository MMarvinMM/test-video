package com.chinatower.gis.modules.gis.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.bdm.UpdateAreaDTO;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.api.entity.gis.EditLayerVO;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.factory.GisMapServiceFactory;
import com.chinatower.gis.modules.gis.entity.layer.*;
import com.chinatower.gis.modules.gis.service.*;
import com.chinatower.platform.common.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * controller
 * Created by ldl on 2022/2/8.
 */
@Api(value = "调用外部地图-Api", tags = "调用外部地图接口")
@Slf4j
@RestController
@SuppressWarnings("all")
public class GisLayerController extends BaseController implements GisLayerAPI {
    @Autowired
    GisMapServiceFactory gisMapServiceFactory;

    @Autowired
    GisService gisService;

    @Autowired
    GisLayerService gisLayerService;

    @Autowired
    GisLayerTaoheService gisLayerTaoheService;

    @Autowired
    GisAreaService gisAreaService;

    @Resource
    PgService pgService;



    @ApiOperation(value = "套合明细的结果列表", notes = "套合明细的结果列表")
    @Override
    @PostMapping("/taoheAnalysis/taoheAnalysisLayerTable")
    public Result taoheAnalysisLayerTable(TaoheAnalysisLayerTableReq taoheAnalysisLayerTableReq) {
        Result res;
        try {
            GeojsonStruckRes geojsonStruckRes = gisLayerTaoheService.taoheAnalysisLayerTable(taoheAnalysisLayerTableReq);
            res = Result.data(geojsonStruckRes);
        } catch (NoLayerException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (NoSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(Constants.TAOHE_ANALYSIS_LAYERTABLE_ERROR);
            log.error(Constants.TAOHE_ANALYSIS_LAYERTABLE_ERROR, e);
        }
        return res;
    }

    @ApiOperation(value = "设置参数", notes = "套合明细的设置参数结果列表")
    @Override
    @PostMapping("/taoheAnalysis/taoheAnalysisSetOptions")
    public Result taoheAnalysisSetOptions(TaoheAnalysisSetOptionsReq taoheAnalysisSetOptionsReq) {
        Result res;
        try {
            gisLayerTaoheService.taoheAnalysisSetOptions(taoheAnalysisSetOptionsReq);
            res = Result.success(Constants.TAOHE_ANALYSIS_SET_OPTIONS_SUCCESS);
        } catch (Exception e) {
            res = Result.fail(Constants.TAOHE_ANALYSIS_SET_OPTIONS_ERROR);
            log.error(Constants.TAOHE_ANALYSIS_SET_OPTIONS_ERROR, e);
        }
        return res;
    }


    /*
{
    "code": 200,
    "data": {
        "allCount": 0,
        "geoData": {
            "features": [
                {
                    "properties": {
                        "bsm": "tdbp",
                        "pzwh": "批复文号1111",
                        "xmmc": "我的项目1111",
                        "ydmj": 98346.61003291611
                    },
                    "type": "Feature"
                },
                {
                    "properties": {
                        "bsm": "tdbp",
                        "pzwh": "批复文号2222",
                        "xmmc": "我的项目2222",
                        "ydmj": 98346.61003291611
                    },
                    "type": "Feature"
                },
                {
                    "properties": {
                        "bsm": "tdbp",
                        "pzwh": "批复文号3333",
                        "xmmc": "我的项目3333",
                        "ydmj": 98346.61003291611
                    },
                    "type": "Feature"
                },
                {
                    "properties": {
                        "bsm": "tdbp",
                        "pzwh": "批复文号4444",
                        "xmmc": "我的项目4444",
                        "ydmj": 98346.61003291611
                    },
                    "type": "Feature"
                }
            ],
            "id": "212",
            "type": "FeatureCollection"
        }
    },
    "msg": "请求成功",
    "time": 1692347006976
}
    * */



    @ApiOperation(value = "套合明细的饼图", notes = "套合明细的饼图")
    @Override
    @PostMapping("/taoheAnalysis/taoheAnalysisLayerStatistics")
    public Result taoheAnalysisLayerStatistics(TaoheAnalysisLayerStatisticsReq taoheAnalysisLayerStatisticsReq) {
        Result res = null;
        try {
            ArrayList<TaoheAnalysisSerialItem> serialItems = gisLayerTaoheService.taoheAnalysisLayerStatistics(taoheAnalysisLayerStatisticsReq);
            res = Result.data(serialItems);
        } catch (NoLayerException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (NoSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(Constants.TAOHE_ANALYSIS_DETAILS_PIECHART_ERROR);
            log.error(Constants.TAOHE_ANALYSIS_DETAILS_PIECHART_ERROR, e);
        }
        return res;
    }

    /*
{
    "code": 200,
    "data": [
        {
            "lable": "我的项目1111",
            "name": "tdbp",
            "radio": 0.2391050153749686,
            "value": 24297.605285387042
        },
        {
            "lable": "我的项目2222",
            "name": "tdbp",
            "radio": 0.2391050153749686,
            "value": 24297.605285387042
        },
        {
            "lable": "我的项目3333",
            "name": "tdbp",
            "radio": 0.2391050153749686,
            "value": 24297.605285387042
        },
        {
            "lable": "我的项目4444",
            "name": "tdbp",
            "radio": 0.2391050153749686,
            "value": 24297.605285387042
        },
        {
            "name": "未报批",
            "radio": 0.04357993850012565,
            "value": 4428.54844545183
        }
    ],
    "msg": "请求成功",
    "time": 1692585809301
}
    * */


    @ApiOperation(value = "套合结果，包含五种大类", notes = "套合结果，包含五种大类")
    @Override
    @PostMapping("/taoheAnalysis/taoheAnalysisAll")
    public Result taoheAnalysisAll(TaoheAnalysisAllReq taoheAnalysisAllReq) {

        Result res;
        try {
            TaoheAnalysisRes taoheAnalysisRes = gisLayerTaoheService.taoheAnalysisAll(taoheAnalysisAllReq);
            res = Result.data(taoheAnalysisRes);
        } catch (ErrorLayerSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (NoAreaColNameException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return res;
    }

    /**
     * 相交分析，输入图形和半径，图层ID，计算并返回相交面积，和该图层面积
     */
    @ApiOperation(value = "相交分析", notes = "相交分析")
    @Override
    @PostMapping("/taoheAnalysis/intersectAnalysis")
    public Result intersectAnalysis(IntersectAnalysisReq intersectAnalysisReq) throws Exception{
        Result res;
        try {
            IntersectAnalysisRes intersectAnalysisRes = gisLayerTaoheService.intersectAnalysis(intersectAnalysisReq);
            res = Result.data(intersectAnalysisRes);
        } catch (Exception e) {
            res = Result.fail(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return res;
    }

    /*
入参：
{
    "tenantId": "122035807",
    "industryCode": "500001",
    "analysisType": 1,
    "inputGeoItems": [
        {
            "distance": 1,
            "wkt": "MULTIPOLYGON(((116.18905363905925 28.52220505783694,116.19024090180365 28.52145215951122,116.19024090180365 28.52145215951122,116.19021194417574 28.520873006952975,116.18883645684991 28.520641345929675,116.18905363905925 28.52220505783694)))"
        },
        {
            "distance": 2,
            "wkt": "MULTIPOLYGON(((116.18846000768704 28.520221460324947,116.18970518568727 28.520120108627253,116.18970518568727 28.520120108627253,116.18941560940816 28.51961335013879,116.18848896531496 28.519671265394614,116.18846000768704 28.520221460324947)))"
        },
        {
            "distance": 3,
            "wkt": "MULTIPOLYGON(((116.18922738482672 28.520916443394842,116.18945904585001 28.520380727278464,116.1891984271988 28.51991740523187,116.18890885091969 28.519902926417913,116.18922738482672 28.520916443394842)))"
        }
    ],
    "layerItems": [
        {
            "abbreviation": "yjjbnt",
            "layerID": "6686ea804fe149adafeee897fc01f829"
        }
    ]
}


结果：
{
    "code": 200,
    "msg": "请求成功",
    "time": 1733471710991,
    "data": {
        "items": [
            {
                "layerArea": "650947035.11",
                "intersectArea": "18459.86",
                "inputArea": "24942.10",
                "radio": "0.0000",
                "layerId": "6686ea804fe149adafeee897fc01f829"
            }
        ]
    }
}
    * */

    @ApiOperation(value = "获取默认套盒图层", notes = "获取默认套盒图层")
    @Override
    @PostMapping("/taoheAnalysis/taoheAnalysisDefaultLayers")
    public Result taoheAnalysisDefaultLayers(TaoheAnalysisAllReq taoheAnalysisAllReq) {

        Result res;
        try {
            TaoheAnalysisDefaultLayersRes taoheAnalysisRes = gisLayerTaoheService.taoheAnalysisDefaultLayers(taoheAnalysisAllReq);
            res = Result.data(taoheAnalysisRes);
        } catch (ErrorLayerSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (NoAreaColNameException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return res;
    }

    /*

{
    "adCode": "420100",
    "appVerCode": "1.0",
    "customerCode": "121063300",
    "industryCode": "500001",
    "nickName": "卓本科技（武汉）有限公司",
    "tenantId": "121063300",
    "userId": "a4e5ae0431f744288df005d02f8492fd",
    "userName": "guotucs",
    "wkt": "POLYGON((115.8974088397611 28.77447583968285,115.92183283304234 28.078797528453705,116.65726640851109 28.094359795754215,116.70068684101109 28.767339546307923,115.8974088397611 28.77447583968285))",
    "guid": "3ea6de5fd28d420b9102d1626f2990ff",
    "layerItems": [
        {
            "abbreviation": "xztb",
            "layerID": "a5fa40e7b29e421ea203f78de8005156"
        },
        {
            "abbreviation": "yjjbnt",
            "layerID": "a5fa40e7b29e421ea203f78de8005150"
        }
    ]
}

    *
    */


    @ApiOperation(value = "计算输入几何多边形的面积", notes = "计算输入几何多边形的面积")
    @PostMapping("/gisCalculate/area")
    public Result<String> gisCalculateArea(GisCalculateAeaReq gisCalculateAeaReq) {

        Result res;
        try {
            res = gisLayerTaoheService.gisCalculateAea(gisCalculateAeaReq);
        } catch (Exception e) {
            res = Result.fail(Constants.GIS_CALCULATE_AREA_ERROR);
            log.error(Constants.GIS_CALCULATE_AREA_ERROR, e);
        }
        return res;
    }

/*
{
    "code": 200,
    "data": {
        "guid": "169276148998461",
        "serials": [
            {
                "name": "xztb",
                "serialDatas": [
                    {
                        "name": "水田",
                        "radio": 0.4782100307499372,
                        "value": 48595.210570774085
                    },
                    {
                        "name": "旱地",
                        "radio": 1.4346300922498116,
                        "value": 145785.63171232227
                    },
                    {
                        "name": "水浇地",
                        "radio": 0.9564200614998744,
                        "value": 97190.42114154817
                    },
                    {
                        "name": "暂无",
                        "radio": -1.8692601844996226,
                        "value": -189952.29383806713
                    }
                ]
            },
            {
                "name": "jsydgzq",
                "serialDatas": [
                    {
                        "name": "040",
                        "radio": 0.9564200614998744,
                        "value": 97190.42114154817
                    },
                    {
                        "name": "030",
                        "radio": 1.4346300922498116,
                        "value": 145785.63171232227
                    },
                    {
                        "name": "020",
                        "radio": 0.4782100307499372,
                        "value": 48595.210570774085
                    },
                    {
                        "name": "010",
                        "radio": 0.9564200614998744,
                        "value": 97190.42114154817
                    },
                    {
                        "name": "暂无",
                        "radio": -2.825680245999497,
                        "value": -287142.7149796153
                    }
                ]
            },
            {
                "name": "yjjbnt",
                "serialDatas": [
                    {
                        "name": "占用基本农田",
                        "radio": 2.8692601844996233,
                        "value": 291571.26342464454
                    },
                    {
                        "name": "未占用基本农田",
                        "radio": -1.8692601844996233,
                        "value": -189952.29383806718
                    }
                ]
            },
            {
                "name": "stbhhx",
                "serialDatas": [
                    {
                        "name": "占用生态保护红线",
                        "radio": 1.912840122999749,
                        "value": 194380.84228309637
                    },
                    {
                        "name": "未占用生态保护红线",
                        "radio": -0.9128401229997489,
                        "value": -92761.87269651901
                    }
                ]
            },
            {
                "name": "tdbp",
                "serialDatas": [
                    {
                        "name": "有报批",
                        "radio": 1.912840122999749,
                        "value": 194380.84228309637
                    },
                    {
                        "name": "未报批",
                        "radio": -0.9128401229997489,
                        "value": -92761.87269651901
                    }
                ]
            }
        ]
    },
    "msg": "请求成功",
    "time": 1692761737237
}
* */



    @ApiOperation(value = "geoserver操作，删除图层", notes = "geoserver操作，删除图层")
    public Result<String> deleteLayer(DeleteLayerReq req){
        Result res;
        try {
            res = pgService.deleteLayer(req);
        } catch (Exception e) {
            res = Result.fail(Constants.DELETE_LAYER_ERROR);
            log.error(Constants.DELETE_LAYER_ERROR , e);
        }
        return res;
    }



    @ApiOperation(value = "geoserver操作，添加图层", notes = "geoserver操作，添加图层")
    public Result<LayerPGRes> addLayer(LayerPGDTO pgdto){
        Result res;
        try {
            StopWatch stopWatch =new StopWatch("addLayer");
            stopWatch.start("addLayer");
            res = pgService.addLayer(pgdto);
            stopWatch.stop();
            log.info("addLayer uuid:{} time:{} ",pgdto.getUuid(), stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            res = Result.fail(Constants.ADD_LAYER_ERROR);
            log.error(Constants.ADD_LAYER_ERROR , e);
        }
        return res;
    }
    @ApiOperation(value = "计算图层面积", notes = "计算图层面积")
    @Override
    @PostMapping("/layer/layerArea")
    public Result<UpdateAreaDTO> layerArea(LayerPGDTO pgdto){
        try {
            return Result.data(pgService.layerArea(pgdto));
        } catch (Exception e) {
            log.error(Constants.GET_LAYER_AREA_ERROR, e);
        }
        return Result.fail(Constants.GET_LAYER_AREA_ERROR);
    }




    @ApiOperation(value = "创建表", notes = "创建表")
    @Override
    @PostMapping("/layer/createLayerInfo")
    public Result createLayerInfo(LayerPGDTO pgdto){
        try {
            return pgService.createLayerInfo(pgdto);
        } catch (Exception e) {
            log.error(Constants.CREATE_LAYERINFO_ERROR , e);
        }
        return Result.fail(Constants.CREATE_LAYERINFO_ERROR);
    }

    @ApiOperation(value = "创建模板表", notes = "创建模板表")
    @Override
    @PostMapping("/layer/createModelLayerTable")
    public Result createModelLayerTable(LayerPGDTO pgdto){
        try {
            return pgService.createModelLayerTable(pgdto);
        } catch (Exception e) {
            log.error(Constants.CREATE_LAYERINFO_ERROR , e);
        }
        return Result.fail(Constants.CREATE_LAYERINFO_ERROR);
    }

    @ApiOperation(value = "插入图层信息表", notes = "插入图层信息表")
    @Override
    @PostMapping("/layer/insertLayerInfo")
    public Result insertLayerInfo(LayerPGDTO pgdto){
        try {
            return pgService.insertLayerInfo(pgdto);
        } catch (Exception e) {
            log.error(Constants.CREATE_LAYERINFO_ERROR , e);
        }
        return Result.fail(Constants.CREATE_LAYERINFO_ERROR);
    }

    @ApiOperation(value = "耕地现状统计", notes = "耕地现状统计")
    @Override
    @PostMapping("/getFeatures/statisticGdxzmj")
    public Result statisticGdxzmj(StatisticGdxzmjReq statisticGdxzmjReq){
        Result res;
        try {
            String area = gisAreaService.statisticGdxzmj(statisticGdxzmjReq);
            res = Result.data(area);
        } catch (Exception e) {
            res = Result.fail(Constants.STATISTIC_GDXZMJ_ERROR);
            log.error(Constants.STATISTIC_GDXZMJ_ERROR + e);
        }
        return res;
    }

    /*
{
    "code": 200,
    "data": {
        "sum": 3123.12
    },
    "msg": "请求成功",
    "time": 1691572041634
}
    * */


    @ApiOperation(value = "获取下拉数据要素列表", notes = "获取下拉数据要素列表")
    @Override
    @PostMapping("/getFeatures/getXlsjFeatures")
    public Result<GeojsonStruckRes> getXlsjFeatures(GetXlsjFeaturesReq req){
        Result res;
        try {
            return pgService.getXlsjFeatures(req);
        } catch (Exception e) {
            res = Result.fail(Constants.GET_XLSJ_FEATURES_ERROR);
            log.error(Constants.GET_XLSJ_FEATURES_ERROR + e);
        }
        return res;
    }

    /*

{
    "code": 200,
    "data": {
        "allCount": 2131,
        "geoData": {
            "features": [
                {
                    "id": 16,
                    "properties": {
                        "area": 12.11,
                        "bsm": "标识码1",
                        "tbbh": "图斑名称1"
                    },
                    "type": "Feature"
                }
            ],
            "type": "FeatureCollection"
        }
    },
    "msg": "请求成功",
    "time": 1691571747610
}

    * */




    @ApiOperation(value = "图斑面积统计", notes = "图斑面积统计")
    @Override
    @PostMapping("/getFeatures/statisticDkmj")
    public Result statisticDkmj(StatisticDkmjReq statisticDkmjReq){
        Result res;
        try {
            String area = gisAreaService.statisticDkmj(statisticDkmjReq);
            res = Result.data(area);
        } catch (Exception e) {
            res = Result.fail(Constants.STATISTIC_DKMJ_ERROR);
            log.error(Constants.STATISTIC_DKMJ_ERROR + e);
        }
        return res;
    }

    /*
{
    "code": 200,
    "data": {
        "sum": 3123.12
    },
    "msg": "请求成功",
    "time": 1691572041634
}
    * */

    @ApiOperation(value = "根据图层ID及条件返回对应图斑数据", notes = "根据图层ID及条件返回对应图斑数据")
    @Override
    @PostMapping("/getFeatures/getXjxlFeaturesByIds")
    public Result<GeojsonStruckRes> getXjxlFeaturesByIds(GetXjxlFeaturesByIdsReq getXjxlFeaturesByIdsReq){
        Result res;
        try {
            GeojsonStruckRes features = pgService.getXjxlFeaturesByIds(getXjxlFeaturesByIdsReq);
            return Result.data(features);
        } catch (Exception e) {
            log.error(Constants.GET_XJXL_FEATURES_BY_IDS_ERROR , e);
            res = Result.fail(Constants.GET_XJXL_FEATURES_BY_IDS_ERROR);
        }
        return res;
    }

    /*

{
    "code": 200,
    "data": {
        "allCount": 2131,
        "geoData": {
            "features": [
                {
                    "id": 16,
                    "properties": {
                        "bsm": "标识码1",
                        "tbbh": "图斑编号1",
                        "zldwmc": "坐落单位名称",
                        "dlmc": "地类名称"
                    },
                    "type": "Feature"
                }
            ],
            "type": "FeatureCollection"
        }
    },
    "msg": "请求成功",
    "time": 1691571747610
}

    * */

    @ApiOperation(value = "根据过滤条件查询 Features", notes = "根据过滤条件查询 Features")
    public Result getFeaturesByFilterAPI(GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq){
        Result res;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.GET_FEATURES_BY_FILTER_API_ERROR);
            log.error(Constants.GET_FEATURES_BY_FILTER_API_ERROR + e);
        }
        return res;
    }

    /*  taoheAnalysisLayerTable   getFeaturesByFilterAPI

{
    "code": 200,
    "data": {
        "allCount": 2131,
        "geoData": {
            "features": [
                {
                    "geometry": {
                        "coordinates": [
                            [
                                [
                                    123.73501999997313,
                                    41.524288000291051
                                ],
                                [
                                    123.74589999988518,
                                    41.527358999818034
                                ],
                                [
                                    123.75603999984617,
                                    41.533210999887785
                                ],
                                [
                                    123.73501999997313,
                                    41.524288000291051
                                ]
                            ]
                        ],
                        "type": "Polygon"
                    },
                    "id": 16,
                    "properties": {
                        "BSM": "标识码",
                        "GZQLXDM": "管制区类型代码",
                        "GZQMJ": 234454.32,
                        "SM": "说明",
                        "YSDM": "要素代码"
                    },
                    "type": "Feature"
                }
            ],
            "type": "FeatureCollection"
        }
    },
    "msg": "请求成功",
    "time": 1691571747610
}

    * */

    @ApiOperation(value = "根据过滤条件查询 Features", notes = "根据过滤条件查询 Features")
    @Override
    @PostMapping("/getFeatures/getFeaturesByFilter")
    public Result getFeaturesByFilter(GetFeaturesByFilterReq getFeaturesByFilterReq){
        Result res;
        try {
            GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
            BeanUtils.copyProperties(getFeaturesByFilterReq, getFeaturesByFilterAPIReq);
            getFeaturesByFilterAPIReq.setFilter(getFeaturesByFilterReq.filterString());
            res = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        } catch (Exception e) {
            res = Result.fail(Constants.GET_FEATURES_BY_FILTER_ERROR);
            log.error(Constants.GET_FEATURES_BY_FILTER_ERROR + e);
        }
        return res;
    }


    /**
     * 根据空间范围过滤条件查询 Features
     * @param getFeaturesByWktReq
     * @return
     */

    @ApiOperation(value = "根据空间范围过滤条件查询 Features", notes = "根据空间范围过滤条件查询 Features")
    @Override
    @PostMapping("/getFeatures/getFeaturesByWkt")
    public Result getFeaturesByWkt(GetFeaturesByWktReq getFeaturesByWktReq){
        Result res;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.getFeaturesByWkt(getFeaturesByWktReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.GET_FEATURES_BY_WKT_ERROR);
            log.error(Constants.GET_FEATURES_BY_WKT_ERROR + e);
        }
        return res;
    }

    @ApiOperation(value = "土地利用现状查询", notes = "土地利用现状查询")
    @Override
    @PostMapping("/getFeatures/searcXZTB")
    public Result searchXZTB(SearchXZTBReq  searchXZTBReq){
        Result res;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.searcXZTB(searchXZTBReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_XZTB_ERROR);
            log.error(Constants.SEARCH_XZTB_ERROR + e);
        }
        return res;
    }

    @ApiOperation(value = "基本农田查询", notes = "基本农田查询")
    @Override
    @PostMapping("/getFeatures/searchJBNT")
    public Result searchJBNT(SearchJBNTReq searchJBNTReq){
        Result res;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.searchJBNT(searchJBNTReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_JBNT_ERROR);
            log.error(Constants.SEARCH_JBNT_ERROR + e);
        }
        return res;
    }

    @ApiOperation(value = "生态保护红线查询", notes = "生态保护红线查询")
    @Override
    @PostMapping("/getFeatures/searchSTBHHX")
    public Result searchSTBHHX(SearchSTBHHXReq searchSTBHHXReq){
        Result res;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.searchSTBHHX(searchSTBHHXReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_STBHHX_ERROR);
            log.error(Constants.SEARCH_STBHHX_ERROR + e);
        }
        return res;
    }

    @ApiOperation(value = "建设用地管制区查询,土地利用总体规划", notes = "建设用地管制区查询,土地利用总体规划")
    @Override
    @PostMapping("/getFeatures/searcJSYDGZQ")
    public Result searcJSYDGZQ(SearcJSYDGZQReq searcJSYDGZQReq){
        Result res;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.searcJSYDGZQ(searcJSYDGZQReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_JSYDGZQ_ERROR);
            log.error(Constants.SEARCH_JSYDGZQ_ERROR + e);
        }
        return res;
    }


    @ApiOperation(value = "土地报批查询", notes = "土地报批查询")
    @Override
    @PostMapping("/getFeatures/searchTDBPTReq")
    public Result searcTDBP(@RequestBody SearchTDBPTReq searchTDBPTReq){
        Result res = null;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.searcTDBP(searchTDBPTReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_TDBP_ERROR);
            log.error(Constants.SEARCH_TDBP_ERROR + e);
        }
        return res;
    }

    @ApiOperation(value = "图层地块查询", notes = "图层地块查询")
    @PostMapping("/getFeatures/layerParcelQuery")
    public Result layerParcelQuery(@RequestBody LayerParcelQueryDTO filter){
        Result res = null;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.layerParcelQuery(filter);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            log.error("地块查询错误" , e);
            if (e instanceof CustomException){
                throw e;
            }else {
                res = Result.fail("地块查询错误");
            }
        }
        return res;
    }


    @ApiOperation(value = "其他类型查询", notes = "其他类型查询")
    @Override
    @PostMapping("/getFeatures/searchOther")
    public Result searchOther(SearcOtherReq searcOtherReq){
        Result res;
        try {
            GeojsonStruckRes returnFeatures = gisLayerService.searcOther(searcOtherReq);
            res = Result.data(returnFeatures);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_OTHER_ERROR);
            log.error(Constants.SEARCH_OTHER_ERROR + e);
        }
        return res;
    }

    @ApiOperation(value = "查询Geometrys", notes = "查询Geometrys")
    @Override
    @PostMapping("/getFeatures/getGeometrys")
    public Result<List<GeometrysRes>> getGeometrys(GeometrysReq req) {
        return pgService.getGeometrys(req);
    }

    @ApiOperation(value = "深林资源 小班列表", notes = "深林资源 小班列表")
    @Override
    @PostMapping("/slzy/forestLayerXmList")
    public TableDataInfo forestLayerXmList(ForestLayerXmReq req) {
        return pgService.forestLayerXmList(req);
    }

    @ApiOperation(value = "深林资源 图层图例统计", notes = "深林资源 图层图例统计")
    @Override
    @PostMapping("/slzy/forestLayerLegendRes")
    public Result<List<ForestLayerLegendRes>> forestLayerLegendRes(ForestLayerLegendReq req) {
        List<ForestLayerLegendRes> list = pgService.forestLayerLegendRes(req);
        return Result.data(list);
    }

    @ApiOperation(value = "深林资源 小班详情", notes = "深林资源 小班详情")
    @Override
    @PostMapping("/slzy/forestLayerXmInfo")
    public Result<Map<String,Object>> forestLayerXmInfo(@RequestBody ForestLayerXmInfoReq req) {
        Map<String,Object> info = pgService.forestLayerXmInfo(req);
        return Result.data(info);
    }

    @ApiOperation(value = "探而未采图层计算", notes = "探而未采图层计算")
    @Override
    @PostMapping("/layer/calculateLayer")
    public Result<LayerInfoAddDTO> calculateLayer(@RequestBody MineralsLayerReq req) {
         return Result.data(pgService.calculateLayer(req));
    }

    @ApiOperation(value = "修改探矿权、采矿权、探而未采过期状态")
    @Override
    @PostMapping("/layer/updateExpire")
    public Result updateExpire() {
        pgService.updateExpire();
        return Result.success("计算完成");
    }

    @ApiOperation(value = "查询土地报批状态", notes = "查询土地报批状态")
    @Override
    @PostMapping("/alarm/gis/searchTdbpState")
    public Result<Map<String, String>>  searchTdbpState(@RequestBody SearchTdbpStateReq searchTdbpStateReq){
        Result res;
        try {
            Map<String, String> map = gisLayerTaoheService.searchTdbpState(searchTdbpStateReq);
            res = Result.data(map);
        } catch (NoSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_TDBPSTATE_ERROR);
            log.error(Constants.SEARCH_TDBPSTATE_ERROR + e);
        }
        return res;
    }
    /*
    查询土地报批状态
    /alarm/gis/searchTdbpState

    {
    "items": [
        {
            "id": "ssss",
            "wkt": "POINT(116.35078687738458 28.624970356710893)"
        },
        {
            "id": "wwwww",
            "wkt": "POINT(116.35078687738458 28.624970356710893)"
        },
        {
            "id": "rrrrrr",
            "wkt": "POINT(116.35078687738458 28.624970356710893)"
        },
        {
            "id": "uuuuu",
            "wkt": "POINT(116.35078687738458 28.624970356710893)"
        },
    ]
}

{
    "code": 200,
    "msg": "请求成功",
    "time": 1717639331275,
    "data": {
        "uuuuu": "未报批",
        "wwwww": "未报批",
        "rrrrrr": "已报批",
        "ssss": "已报批"
    }
}
    * */

    /**
     * 启动耕地的进出平衡计算
     * @apiNote 启动耕地的进出平衡计算
     * @apiVersion 6.2.28
     * @tag gis 能力
     * @param calInOutBalanceReq
     * @return Result
     */
    @ApiOperation(value = "启动耕地的进出平衡计算", notes = "启动耕地的进出平衡计算")
    @Override
    @PostMapping("/layer/calInOutBalance")
    public Result<CalInOutBalanceRes> calInOutBalance(@RequestBody CalInOutBalanceReq calInOutBalanceReq){
        Result res;
        try {
            CalInOutBalanceRes calInOutBalance = gisLayerTaoheService.calInOutBalance(calInOutBalanceReq);
            res = Result.data(calInOutBalance);
        } catch (NoSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(Constants.CALCULATE_INOUTBALANCE_ERROR);
            log.error(Constants.CALCULATE_INOUTBALANCE_ERROR + e);
        }
        return res;
    }

    /*  进出平衡的管理表的建表语句

DROP TABLE IF EXISTS t_tmp_jcphmanage;

CREATE TABLE IF NOT EXISTS t_tmp_jcphmanage
(
	guid character varying(200) COLLATE pg_catalog."default" NOT NULL,	    -- 耕地的进出平衡计算结果的GUID
	preLayerName character varying(200) COLLATE pg_catalog.default,			-- 之前的图层表名
	curLayerName character varying(200) COLLATE pg_catalog.default,			-- 当前的图层表名
	g1 character varying(200) COLLATE pg_catalog.default,					-- 之前的耕地图斑表
	f1 character varying(200) COLLATE pg_catalog.default,					-- 之前的非耕农用地图斑表
	g2 character varying(200) COLLATE pg_catalog.default,					-- 当前的耕地图斑表
	f2 character varying(200) COLLATE pg_catalog.default,					-- 当前的非耕农用地图斑表
	gdlc character varying(200) COLLATE pg_catalog.default,					-- 耕地流出图斑表
	gdlr character varying(200) COLLATE pg_catalog.default,					-- 耕地流入图斑表
	lc0101 double precision,												-- 耕地流出面积中水田的面积
	lc0102 double precision,												-- 耕地流出面积中水浇地的面积
	lc0103 double precision,												-- 耕地流出面积中旱地的面积
	lr0101 double precision,												-- 耕地流入面积中水田的面积
	lr0102 double precision,												-- 耕地流入面积中水浇地的面积
	lr0103 double precision,												-- 耕地流入面积中旱地的面积
	creator character varying(200) COLLATE pg_catalog."default",			-- 创建人
	create_time timestamp with time zone,									-- 创建时间
	update_time timestamp with time zone,									-- 更新时间
	delete_time timestamp with time zone,									-- 删除时间
	delete_flag character varying(1) COLLATE pg_catalog.default,						-- 删除状态
	tenant_id character varying(50) COLLATE pg_catalog."default",			-- 租户id
    industry_code character varying(50) COLLATE pg_catalog."default",		-- 行业编码
    app_ver_code character varying(50) COLLATE pg_catalog."default",		-- 行业版本编码
	CONSTRAINT t_tmp_jcphmanage_pkey PRIMARY KEY (guid)
);


    * */

    /*
     启动耕地的进出平衡计算
     /layer/calInOutBalance

    之前的图层表名
    private String preLayerName;

    当前的图层表名
    private String curLayerName;

    入参
     {
         "preLayerName": "polygon_xztb_1717560143044_vk2hny",
         "curLayerName": "polygon_xztb_1717560182068_bftzy2"
     }

    结果
     {
         "code": 200,
         "msg": "请求成功",
         "time": 1717642217105,
         "data": {
            "guid": "d627210aba414f93ae0dd5ce34e312cc"
         }
     }
    * */


    /**
     * 获取耕地的进出平衡的计算结果
     * @apiNote 获取耕地的进出平衡的计算结果
     * @apiVersion 6.2.29
     * @tag gis 能力
     * @param getInOutBalanceReq
     * @return Result
     */
    @ApiOperation(value = "获取耕地的进出平衡的计算结果", notes = "获取耕地的进出平衡的计算结果")
    @Override
    @PostMapping("/layer/getInOutBalance")
    public Result<GetInOutBalanceRes> getInOutBalance(@RequestBody GetInOutBalanceReq getInOutBalanceReq){
        Result res;
        try {
            GetInOutBalanceRes getInOutBalanceRes = gisLayerTaoheService.getInOutBalance(getInOutBalanceReq);
            res = Result.data(getInOutBalanceRes);
        } catch (NoSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(Constants.SEARCH_INOUTBALANCE_ERROR);
            log.error(Constants.SEARCH_INOUTBALANCE_ERROR + e);
        }
        return res;
    }

    @Override
    @ApiOperation(value = "编辑图层 旧表备份", notes = "编辑图层 旧表备份")
    public Result editLayer(EditLayerVO vo) {
        try {
            log.info("旧表备份 {}", JSON.toJSONString(vo));
            if (StringUtils.isEmpty(vo.getLayerName()) || StringUtils.isEmpty(vo.getBakLayerName())){
                return Result.fail("备份表失败");
            }
            pgService.editLayer(vo.getLayerName(),vo.getBakLayerName());
            return Result.success("成功");
        }catch (Exception e){
            log.error("备份表失败",e);
        }
        return Result.fail("备份表失败");
    }

    @Override
    public Result editBackLayer(EditLayerVO vo) {
        try {
            if (StringUtils.isEmpty(vo.getLayerName()) || StringUtils.isEmpty(vo.getBakLayerName())){
                return Result.fail("备份表还原失败");
            }
            pgService.editBackLayer(vo.getLayerName(),vo.getBakLayerName());
            return Result.success("成功");
        }catch (Exception e){
            log.error("备份表还原失败",e);
        }
        return Result.fail("备份表还原失败");
    }

    /*
    获取耕地的进出平衡的计算结果
    /layer/getInOutBalance

    耕地流出面积中水田的面积
    private String lc0101;

    耕地流出面积中水浇地的面积
    private String lc0102;


   耕地流出面积中旱地的面积
    private String lc0103;

    耕地流入面积中水田的面积
    private String lr0101;


    耕地流入面积中水浇地的面积
    private String lr0102;

    耕地流入面积中旱地的面积
    private String lr0103;

    入参
    {
        "guid": "d627210aba414f93ae0dd5ce34e312cc"
    }

    结果
    {
        "code": 200,
        "msg": "请求成功",
        "time": 1717644728980,
        "data": {
            "lc0101": "1000",
            "lc0102": "1001",
            "lc0103": "1002",
            "lr0101": "1000",
            "lr0102": "1001",
            "lr0103": "1002"
        }
    }
    *
    * */

    /**
     * 自动比较图层图斑的不同，用于图层比对编辑
     * 1、比较任意bsm字段内容长度，是否为32、39位字符长度，如果不是，则无需做任何操作
     * 2、如果是32、39位字符长度，则为系统自动填充bsm，需要进行图斑覆盖比例判断
     * 3、当某新一个图斑覆盖原图斑的95%以上时，更新为原图斑bsm字段
     * @apiNote 自动比较图层图斑的不同，用于图层比对编辑
     * @apiVersion
     * @tag gis 能力
     * @param calcAutoCompareLayerFeaturesReq
     * @return Result
     */
    @ApiOperation(value = "自动比较图层图斑的不同，用于图层比对编辑", notes = "自动比较图层图斑的不同，用于图层比对编辑")
    @Override
    @PostMapping("/layer/autoCompareLayerFeatures")
    public Result autoCompareLayerFeatures(@RequestBody CalInOutBalanceReq calcAutoCompareLayerFeaturesReq){
        Result res;
        try {
            int changeCount = gisLayerTaoheService.autoCompareLayerFeatures(calcAutoCompareLayerFeaturesReq);
            res = Result.data(changeCount);
        } catch (NoSpotException e) {
            String mess = e.getMessage();
            res = Result.fail(mess);
            log.error(mess, e);
        } catch (Exception e) {
            res = Result.fail(Constants.CALCULATE_AUTOCOMPARELAYERFEATURES_ERROR);
            log.error(Constants.CALCULATE_AUTOCOMPARELAYERFEATURES_ERROR + e);
        }
        return res;
    }

    /*
    入参
     {
        "preLayerName": "polygon_tdbp_1747387651567_d8azuw",
        "curLayerName": "polygon_tdbp_1747625694506_j2obzb"
     }

    结果
     {
         "code": 200,
         "msg": "请求成功",
         "time": 1747635044404,
         "data": 7
     }
    *
    * */

    /**
     * 表里是否有bsm字段
     * @apiVersion
     * @tag gis 能力
     * @param layerName
     * @return Result<LayerHasCustomBsmFieldRes>
     */
    @ApiOperation(value = "表里是否有bsm字段", notes = "表里是否有bsm字段")
    @Override
    @PostMapping("/layer/isLayerHasCustomBsmField")
    public Result<LayerHasCustomBsmFieldRes> isLayerHasCustomBsmField(String layerId){
        Result res;
        try {
            LayerHasCustomBsmFieldRes layerHasCustomBsmFieldRes = gisLayerTaoheService.isLayerHasCustomBsmField(layerId);
            res = Result.data(layerHasCustomBsmFieldRes);
        } catch (Exception e) {
            res = Result.fail(Constants.CALCULATE_LAYERHASCUSTOMBSMFIELD_ERROR);
            log.error(Constants.CALCULATE_LAYERHASCUSTOMBSMFIELD_ERROR + e);
        }
        return res;
    }

    /**
     * 根据用户行业、租户获取所有相交图斑列表
     */
    @Override
    public Result getIntersectingPlot(GetIntersectingPlotReq getIntersectingPlotReq) {
        Result res;
        try {
            List<GeojsonStruckRes> rslst = gisLayerTaoheService.getIntersectingPlot(gisLayerService, getIntersectingPlotReq);
            res = Result.data(rslst);
        } catch (Exception e) {
            res = Result.fail(Constants.CALCULATE_INTERSECTINGPLOT_ERROR);
            log.error(Constants.CALCULATE_INTERSECTINGPLOT_ERROR + e);
        }
        return res;
    }

    /**
     * 根据用户行业、租户获取所有有相交图斑的图层列表
     */
    @Override
    public Result getIntersectingLayers(GetIntersectingPlotReq getIntersectingPlotReq) {
        Result res;
        try {
            List<TaoheLayerInfo> rslst = gisLayerTaoheService.getIntersectingLayers(getIntersectingPlotReq);
            res = Result.data(rslst);
        } catch (Exception e) {
            res = Result.fail(Constants.CALCULATE_INTERSECTINGLAYERS_ERROR);
            log.error(Constants.CALCULATE_INTERSECTINGLAYERS_ERROR + e);
        }
        return res;
    }
}


