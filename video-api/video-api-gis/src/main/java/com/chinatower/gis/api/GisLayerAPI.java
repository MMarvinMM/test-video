package com.chinatower.gis.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.bdm.UpdateAreaDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.api.entity.gis.EditLayerVO;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.api.factory.GisGuotuFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "GisGuotuAPI",
        value = ServiceNameConstants.GIS_SERVICE,
        fallbackFactory = GisGuotuFallbackFactory.class)
public interface GisLayerAPI {

    /**
     * 根据 guid和图层id 返回统计结果，形成套合明细的结果列表
     * @apiNote 根据 guid和图层id 返回统计结果，形成套合明细的结果列表
     * @apiVersion 2.1.1
     * @tag 二、gis能力
     * @requestDemo
     * 请求参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        tenantId: 租户ID
     *        industryCode: 行业编码
     *        firstIndex: 起始页码
     *        count: 每页行数
     *        guid: 请求的GUID
     *        layerID: 请求图层的ID
     *
     * {
     *     "industryCode": "500001",
     *     "tenantId": "121063300",
     *     "firstIndex": "1",
     *     "count": "5",
     *     "guid": "bb73506992554a59b2ca6583f9e55131",
     *     "layerID": "a3ba16bc8fc047a38779b845df9ccb44"
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        geoType: 图层类型
     *        allCount: 查询结果总数
     *        geoData: geojson 对象
     *        features 图斑数据
     *        properties 属性数据 每个图层类型都不一样
     *
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1735803820973,
     *     "data": {
     *         "geoData": {
     *             "features": [
     *                 {
     *                     "type": "Feature",
     *                     "properties": {
     *                         "tbbh": "572",
     *                         "tkxs": 0,
     *                         "zldwmc": "温圳镇沥背村",
     *                         "xzqmc": "进贤县",
     *                         "qsxz": "30",
     *                         "yjjbntmj": 1912.81,
     *                         "kcdlbm": "1203",
     *                         "gddj": 5,
     *                         "bz": "",
     *                         "gdlx": "TT",
     *                         "ysdm": "2005010300",
     *                         "qsdwmc": "温圳镇沥背村",
     *                         "zldwdm": "3601241022120000000",
     *                         "tkmj": 0,
     *                         "yjjbnttbmj": 2031.02,
     *                         "yjjbntlx": "",
     *                         "tbxhdm": "",
     *                         "qsdwdm": "3601241022120000000",
     *                         "gddb": 9,
     *                         "xzqdm": "360124",
     *                         "gdzzsxdm": "",
     *                         "dlmc": "旱地",
     *                         "gdpdjb": "2",
     *                         "yjjbnttbbh": "360124102212005618",
     *                         "sjnf": 2020,
     *                         "tbxhmc": "",
     *                         "frdbs": "0",
     *                         "dlbm": "0103",
     *                         "gdzzsxmc": "",
     *                         "bsm": "360124251000005618",
     *                         "properties": "{\"tbbh\":\"572\",\"gid\":\"5585\",\"tkxs\":\"0.0\",\"zldwmc\":\"温圳镇沥背村\",\"xzqmc\":\"进贤县\",\"qsxz\":\"30\",\"yjjbntmj\":\"1912.81\",\"layertype\":\"yjjbnt\",\"kcdlbm\":\"1203\",\"gddj\":\"5\",\"miny\":\"28.343143580018495\",\"minx\":\"116.13699569388635\",\"bz\":\"\",\"gdlx\":\"TT\",\"ysdm\":\"2005010300\",\"qsdwmc\":\"温圳镇沥背村\",\"mj\":\"2631.70760596\",\"zldwdm\":\"3601241022120000000\",\"tkmj\":\"0.0\",\"yjjbnttbmj\":\"2031.02\",\"maxy\":\"28.34390929033039\",\"yjjbntlx\":\"\",\"maxx\":\"116.13754701058959\",\"tbxhdm\":\"\",\"qsdwdm\":\"3601241022120000000\",\"gddb\":\"9\",\"xzqdm\":\"360124\",\"gdzzsxdm\":\"\",\"dlmc\":\"旱地\",\"gdpdjb\":\"2\",\"calarea\":\"2631.70760593\",\"yjjbnttbbh\":\"360124102212005618\",\"sjnf\":\"2020\",\"tbxhmc\":\"\",\"frdbs\":\"0\",\"dlbm\":\"0103\",\"gdzzsxmc\":\"\",\"bsm\":\"360124251000005618\"}"
     *                     }
     *                 },
     *                 {
     *                     "type": "Feature",
     *                     "properties": {
     *                         "tbbh": "570",
     *                         "tkxs": 0,
     *                         "zldwmc": "温圳镇沥背村",
     *                         "xzqmc": "进贤县",
     *                         "qsxz": "30",
     *                         "yjjbntmj": 6390.4,
     *                         "kcdlbm": "1203",
     *                         "gddj": 5,
     *                         "bz": "",
     *                         "gdlx": "TT",
     *                         "ysdm": "2005010300",
     *                         "qsdwmc": "温圳镇沥背村",
     *                         "zldwdm": "3601241022120000000",
     *                         "tkmj": 0,
     *                         "yjjbnttbmj": 6785.31,
     *                         "yjjbntlx": "",
     *                         "tbxhdm": "",
     *                         "qsdwdm": "3601241022120000000",
     *                         "gddb": 9,
     *                         "xzqdm": "360124",
     *                         "gdzzsxdm": "",
     *                         "dlmc": "旱地",
     *                         "gdpdjb": "2",
     *                         "yjjbnttbbh": "360124102212005616",
     *                         "sjnf": 2020,
     *                         "tbxhmc": "",
     *                         "frdbs": "0",
     *                         "dlbm": "0103",
     *                         "gdzzsxmc": "",
     *                         "bsm": "360124251000005616",
     *                         "properties": "{\"tbbh\":\"570\",\"gid\":\"5583\",\"tkxs\":\"0.0\",\"zldwmc\":\"温圳镇沥背村\",\"xzqmc\":\"进贤县\",\"qsxz\":\"30\",\"yjjbntmj\":\"6390.4\",\"layertype\":\"yjjbnt\",\"kcdlbm\":\"1203\",\"gddj\":\"5\",\"miny\":\"28.339875591689026\",\"minx\":\"116.14603785688561\",\"bz\":\"\",\"gdlx\":\"TT\",\"ysdm\":\"2005010300\",\"qsdwmc\":\"温圳镇沥背村\",\"mj\":\"8791.62130199\",\"zldwdm\":\"3601241022120000000\",\"tkmj\":\"0.0\",\"yjjbnttbmj\":\"6785.31\",\"maxy\":\"28.340838936849664\",\"yjjbntlx\":\"\",\"maxx\":\"116.14715459190046\",\"tbxhdm\":\"\",\"qsdwdm\":\"3601241022120000000\",\"gddb\":\"9\",\"xzqdm\":\"360124\",\"gdzzsxdm\":\"\",\"dlmc\":\"旱地\",\"gdpdjb\":\"2\",\"calarea\":\"8791.62130209\",\"yjjbnttbbh\":\"360124102212005616\",\"sjnf\":\"2020\",\"tbxhmc\":\"\",\"frdbs\":\"0\",\"dlbm\":\"0103\",\"gdzzsxmc\":\"\",\"bsm\":\"360124251000005616\"}"
     *                     }
     *                 },
     *                 {
     *                     "type": "Feature",
     *                     "properties": {
     *                         "tbbh": "64",
     *                         "tkxs": 0,
     *                         "zldwmc": "温圳镇联里村",
     *                         "xzqmc": "进贤县",
     *                         "qsxz": "30",
     *                         "yjjbntmj": 2607.12,
     *                         "kcdlbm": "",
     *                         "gddj": 5,
     *                         "bz": "",
     *                         "gdlx": "",
     *                         "ysdm": "2005010300",
     *                         "qsdwmc": "温圳镇联里村",
     *                         "zldwdm": "3601241022070000000",
     *                         "tkmj": 0,
     *                         "yjjbnttbmj": 2607.12,
     *                         "yjjbntlx": "",
     *                         "tbxhdm": "",
     *                         "qsdwdm": "3601241022070000000",
     *                         "gddb": 9,
     *                         "xzqdm": "360124",
     *                         "gdzzsxdm": "",
     *                         "dlmc": "水田",
     *                         "gdpdjb": "1",
     *                         "yjjbnttbbh": "360124102207005626",
     *                         "sjnf": 2020,
     *                         "tbxhmc": "",
     *                         "frdbs": "0",
     *                         "dlbm": "0101",
     *                         "gdzzsxmc": "",
     *                         "bsm": "360124251000005626",
     *                         "properties": "{\"tbbh\":\"64\",\"gid\":\"5593\",\"tkxs\":\"0.0\",\"zldwmc\":\"温圳镇联里村\",\"xzqmc\":\"进贤县\",\"qsxz\":\"30\",\"yjjbntmj\":\"2607.12\",\"layertype\":\"yjjbnt\",\"kcdlbm\":\"\",\"gddj\":\"5\",\"miny\":\"28.342281366455026\",\"minx\":\"116.09587243264963\",\"bz\":\"\",\"gdlx\":\"\",\"ysdm\":\"2005010300\",\"qsdwmc\":\"温圳镇联里村\",\"mj\":\"3378.15745721\",\"zldwdm\":\"3601241022070000000\",\"tkmj\":\"0.0\",\"yjjbnttbmj\":\"2607.12\",\"maxy\":\"28.34307361557294\",\"yjjbntlx\":\"\",\"maxx\":\"116.09632051467342\",\"tbxhdm\":\"\",\"qsdwdm\":\"3601241022070000000\",\"gddb\":\"9\",\"xzqdm\":\"360124\",\"gdzzsxdm\":\"\",\"dlmc\":\"水田\",\"gdpdjb\":\"1\",\"calarea\":\"3378.15745729\",\"yjjbnttbbh\":\"360124102207005626\",\"sjnf\":\"2020\",\"tbxhmc\":\"\",\"frdbs\":\"0\",\"dlbm\":\"0101\",\"gdzzsxmc\":\"\",\"bsm\":\"360124251000005626\"}"
     *                     }
     *                 },
     *                 {
     *                     "type": "Feature",
     *                     "properties": {
     *                         "tbbh": "387",
     *                         "tkxs": 0,
     *                         "zldwmc": "泉岭乡长岗村",
     *                         "xzqmc": "进贤县",
     *                         "qsxz": "30",
     *                         "yjjbntmj": 2412.59,
     *                         "kcdlbm": "",
     *                         "gddj": 5,
     *                         "bz": "",
     *                         "gdlx": "",
     *                         "ysdm": "2005010300",
     *                         "qsdwmc": "泉岭乡长岗村",
     *                         "zldwdm": "3601242112100000000",
     *                         "tkmj": 0,
     *                         "yjjbnttbmj": 2412.59,
     *                         "yjjbntlx": "",
     *                         "tbxhdm": "",
     *                         "qsdwdm": "3601242112100000000",
     *                         "gddb": 9,
     *                         "xzqdm": "360124",
     *                         "gdzzsxdm": "",
     *                         "dlmc": "水田",
     *                         "gdpdjb": "1",
     *                         "yjjbnttbbh": "360124211210003155",
     *                         "sjnf": 2020,
     *                         "tbxhmc": "",
     *                         "frdbs": "0",
     *                         "dlbm": "0101",
     *                         "gdzzsxmc": "",
     *                         "bsm": "360124251000003155",
     *                         "properties": "{\"tbbh\":\"387\",\"gid\":\"3607\",\"tkxs\":\"0.0\",\"zldwmc\":\"泉岭乡长岗村\",\"xzqmc\":\"进贤县\",\"qsxz\":\"30\",\"yjjbntmj\":\"2412.59\",\"layertype\":\"yjjbnt\",\"kcdlbm\":\"\",\"gddj\":\"5\",\"miny\":\"28.39439535221305\",\"minx\":\"116.11880534385097\",\"bz\":\"\",\"gdlx\":\"\",\"ysdm\":\"2005010300\",\"qsdwmc\":\"泉岭乡长岗村\",\"mj\":\"3129.12579172\",\"zldwdm\":\"3601242112100000000\",\"tkmj\":\"0.0\",\"yjjbnttbmj\":\"2412.59\",\"maxy\":\"28.394901820225893\",\"yjjbntlx\":\"\",\"maxx\":\"116.11942548696034\",\"tbxhdm\":\"\",\"qsdwdm\":\"3601242112100000000\",\"gddb\":\"9\",\"xzqdm\":\"360124\",\"gdzzsxdm\":\"\",\"dlmc\":\"水田\",\"gdpdjb\":\"1\",\"calarea\":\"3129.12579178\",\"yjjbnttbbh\":\"360124211210003155\",\"sjnf\":\"2020\",\"tbxhmc\":\"\",\"frdbs\":\"0\",\"dlbm\":\"0101\",\"gdzzsxmc\":\"\",\"bsm\":\"360124251000003155\"}"
     *                     }
     *                 },
     *                 {
     *                     "type": "Feature",
     *                     "properties": {
     *                         "tbbh": "364",
     *                         "tkxs": 0,
     *                         "zldwmc": "李渡镇红桥村",
     *                         "xzqmc": "进贤县",
     *                         "qsxz": "30",
     *                         "yjjbntmj": 480.94,
     *                         "kcdlbm": "",
     *                         "gddj": 5,
     *                         "bz": "",
     *                         "gdlx": "",
     *                         "ysdm": "2005010300",
     *                         "qsdwmc": "李渡镇红桥村",
     *                         "zldwdm": "3601241012030000000",
     *                         "tkmj": 0,
     *                         "yjjbnttbmj": 480.94,
     *                         "yjjbntlx": "",
     *                         "tbxhdm": "",
     *                         "qsdwdm": "3601241012030000000",
     *                         "gddb": 9,
     *                         "xzqdm": "360124",
     *                         "gdzzsxdm": "",
     *                         "dlmc": "水田",
     *                         "gdpdjb": "1",
     *                         "yjjbnttbbh": "360124101203012186",
     *                         "sjnf": 2020,
     *                         "tbxhmc": "",
     *                         "frdbs": "0",
     *                         "dlbm": "0101",
     *                         "gdzzsxmc": "",
     *                         "bsm": "360124251000012186",
     *                         "properties": "{\"tbbh\":\"364\",\"gid\":\"10671\",\"tkxs\":\"0.0\",\"zldwmc\":\"李渡镇红桥村\",\"xzqmc\":\"进贤县\",\"qsxz\":\"30\",\"yjjbntmj\":\"480.94\",\"layertype\":\"yjjbnt\",\"kcdlbm\":\"\",\"gddj\":\"5\",\"miny\":\"28.242702430395912\",\"minx\":\"116.1524542223609\",\"bz\":\"\",\"gdlx\":\"\",\"ysdm\":\"2005010300\",\"qsdwmc\":\"李渡镇红桥村\",\"mj\":\"622.017033416\",\"zldwdm\":\"3601241012030000000\",\"tkmj\":\"0.0\",\"yjjbnttbmj\":\"480.94\",\"maxy\":\"28.242964412704126\",\"yjjbntlx\":\"\",\"maxx\":\"116.15277657697436\",\"tbxhdm\":\"\",\"qsdwdm\":\"3601241012030000000\",\"gddb\":\"9\",\"xzqdm\":\"360124\",\"gdzzsxdm\":\"\",\"dlmc\":\"水田\",\"gdpdjb\":\"1\",\"calarea\":\"622.017033377783\",\"yjjbnttbbh\":\"360124101203012186\",\"sjnf\":\"2020\",\"tbxhmc\":\"\",\"frdbs\":\"0\",\"dlbm\":\"0101\",\"gdzzsxmc\":\"\",\"bsm\":\"360124251000012186\"}"
     *                     }
     *                 }
     *             ],
     *             "id": "212",
     *             "type": "FeatureCollection"
     *         },
     *         "allCount": 5
     *     }
     * }
     * @param taoheAnalysisLayerTableReq
     * @return Result
     */
    @PostMapping("/taoheAnalysis/taoheAnalysisLayerTable")
    Result taoheAnalysisLayerTable(@RequestBody TaoheAnalysisLayerTableReq taoheAnalysisLayerTableReq);

    /**
     * 计算输入几何多边形的面积
     *
     * @param gisCalculateAeaReq
     * @return
     */
    @PostMapping("/gisCalculate/area")
    Result gisCalculateArea(@RequestBody GisCalculateAeaReq gisCalculateAeaReq);

    /**
     * 根据 guid和图层id 返回统计结果，形成套合明细的饼图
     * @apiNote 根据 guid和图层id 返回统计结果，形成套合明细的饼图
     * @apiVersion 2.1.2
     * @tag 二、gis能力
     * @requestDemo
     * 请求参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        tenantId: 租户ID
     *        industryCode: 行业编码
     *        userId: 用户ID
     *        guid: 请求的GUID
     *        layerID: 请求图层的ID
     *
     * {
     *     "industryCode": "500001",
     *     "tenantId": "TY0000115",
     *     "userId": "4d8863654b844217b24c005a3b2e9fc6",
     *     "guid": "36286f7c55a644128d81e24ae3b80c53",
     *     "layerID": "71c8458bfcd944568a5be990ba560bd5"
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *              name: 统计结果名称
     *              lable: 显示名称
     *              value: 统计结果面积，单位平方米
     *              radio: 面积占输入多边形的比例
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1735802006296,
     *     "data": [
     *         {
     *             "name": "131444434",
     *             "lable": "1",
     *             "value": "837.32241030177579",
     *             "radio": "0.0045295659690889065"
     *         },
     *         {
     *             "name": "131444435",
     *             "lable": "2",
     *             "value": "37859.3667475416441",
     *             "radio": "0.204803427116104636"
     *         },
     *         {
     *             "name": "131444436",
     *             "lable": "3",
     *             "value": "7793.40278192661481",
     *             "radio": "0.0421590675110379773"
     *         },
     *         {
     *             "name": "131444457",
     *             "lable": "24",
     *             "value": "5265.68930473637192",
     *             "radio": "0.0284851889607650108"
     *         },
     *         {
     *             "name": "未叠加",
     *             "value": "37792.29",
     *             "radio": "0.2044"
     *         }
     *     ]
     * }
     * @param taoheAnalysisLayerStatisticsReq
     * @return Result
     */
    @PostMapping("/taoheAnalysis/taoheAnalysisLayerStatistics")
    Result taoheAnalysisLayerStatistics(@RequestBody TaoheAnalysisLayerStatisticsReq taoheAnalysisLayerStatisticsReq);

    /**
     * 查询套合结果，包含五种大类
     * @apiNote 查询套合结果，包含五种大类
     * @apiVersion 2.1.3
     * @tag 二、gis能力
     * @requestDemo
     * 请求参数解析：
     *        tenantId: 租户ID
     *        industryCode: 行业编码
     *        userId: 用户ID
     *        wkt: 请求几何的WKT形式表达，默认坐标为4326
     *        distance: 图形的缓冲区距离，默认坐标为0
     *        layerID: 标识码坐在的图层id，和bms一起使用，和入参wkt二选一，如果同时输入，使用layerID+bsm作为查询多边形
     *        bsm: 标识码
     *        analysisType: 分析类型，默认值为 1，代表“国土”
     *
     * {
     *     "wkt": "POLYGON((116.26468727677602 39.96118506975944,116.26021863817203 39.957384733687114,116.26716010030763 39.954568479206976,116.26468727677602 39.96118506975944))",
     *     "industryCode": "500001",
     *     "tenantId": "TY0000115",
     *     "userId": "4d8863654b844217b24c005a3b2e9fc6",
     *     "analysisType": "1"
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        guid: 查询结果GUID
     *        serials: 五种类型统计分析结果
     *          name: 序列名称
     *          type: 统计类型枚举
     *          serialDatas: 统计结果
     *              name: 统计结果名称
     *              value: 统计结果面积，单位平方米
     *              radio: 面积占输入多边形的比例
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1735801245532,
     *     "data": {
     *         "serials": [
     *             {
     *                 "name": "xztb",
     *                 "type": "土地利用现状分析",
     *                 "serialDatas": [
     *                     {
     *                         "name": "城镇住宅用地",
     *                         "value": "5265.69",
     *                         "radio": "0.0285"
     *                     },
     *                     {
     *                         "name": "旱地",
     *                         "value": "40599.42",
     *                         "radio": "0.2196"
     *                     },
     *                     {
     *                         "name": "水田",
     *                         "value": "101199.70",
     *                         "radio": "0.5474"
     *                     },
     *                     {
     *                         "name": "暂无",
     *                         "value": "37792.29",
     *                         "radio": "0.2044"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "name": "jsydgzq",
     *                 "type": "土地利用总体规划分析",
     *                 "serialDatas": [
     *                     {
     *                         "name": "暂无",
     *                         "value": "184857.10",
     *                         "radio": "1.0000"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "name": "yjjbnt",
     *                 "type": "基本农田分析",
     *                 "serialDatas": [
     *                     {
     *                         "name": "占用基本农田",
     *                         "value": "25686.24",
     *                         "radio": "0.1390"
     *                     },
     *                     {
     *                         "name": "未占用基本农田",
     *                         "value": "159170.86",
     *                         "radio": "0.8610"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "name": "stbhhx",
     *                 "type": "生态保护红线分析",
     *                 "serialDatas": [
     *                     {
     *                         "name": "占用生态保护红线",
     *                         "value": "0.00",
     *                         "radio": "0.0000"
     *                     },
     *                     {
     *                         "name": "未占用生态保护红线",
     *                         "value": "184857.10",
     *                         "radio": "1.0000"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "name": "tdbp",
     *                 "type": "土地报批分析",
     *                 "serialDatas": [
     *                     {
     *                         "name": "已报批",
     *                         "value": "0.00",
     *                         "radio": "0.0000"
     *                     },
     *                     {
     *                         "name": "未报批",
     *                         "value": "184857.10",
     *                         "radio": "1.0000"
     *                     }
     *                 ]
     *             }
     *         ],
     *         "guid": "36286f7c55a644128d81e24ae3b80c53"
     *     }
     * }
     * @param taoheAnalysisAllReq
     * @return Result
     */
    @PostMapping("/taoheAnalysis/taoheAnalysisAll")
    Result taoheAnalysisAll(@RequestBody TaoheAnalysisAllReq taoheAnalysisAllReq) throws Exception;


    /**
     * 相交分析，输入图形和半径，图层ID，计算并返回相交面积，和该图层面积
     * @apiNote 相交分析，输入图形和半径，图层ID，计算并返回相交面积，和该图层面积
     * @apiVersion 2.1.4
     * @tag 二、gis能力
     * @param intersectAnalysisReq
     * @return Result
     */
    @PostMapping("/taoheAnalysis/intersectAnalysis")
    Result intersectAnalysis(@RequestBody IntersectAnalysisReq intersectAnalysisReq) throws Exception;

    /**
     * 获取默认套盒图层
     * @apiNote 获取默认套盒图层
     * @apiVersion 2.1.5
     * @tag 二、gis能力
     * @param taoheAnalysisAllReq
     * @return Result
     */
    @PostMapping("/taoheAnalysis/taoheAnalysisDefaultLayers")
    Result taoheAnalysisDefaultLayers(@RequestBody TaoheAnalysisAllReq taoheAnalysisAllReq) throws Exception;

    /**
     * 设置参数
     *
     * @param taoheAnalysisSetOptionsReq
     * @return
     */
    @PostMapping("/taoheAnalysis/taoheAnalysisSetOptions")
    Result taoheAnalysisSetOptions(@RequestBody TaoheAnalysisSetOptionsReq taoheAnalysisSetOptionsReq) throws Exception;


    /**
     * geoserver操作，删除图层
     */
    @PostMapping("/layerInfo/deleteLayer")
    Result<String> deleteLayer(@RequestBody DeleteLayerReq req);

    /**
     * 创建表
     */
    @PostMapping("/layer/createLayerInfo")
    Result createLayerInfo(@RequestBody LayerPGDTO pgdto);

    /**
     * 创建表 新
     */
    @PostMapping("/layer/createModelLayerTable")
    Result createModelLayerTable(@RequestBody LayerPGDTO pgdto);

    /**
     * 图层基础信息新增
     */
    @PostMapping("/layer/insertLayerInfo")
    Result insertLayerInfo(@RequestBody LayerPGDTO pgdto);


    /**
     * 计算图层面积
     */
    @PostMapping("/layer/layerArea")
    Result<UpdateAreaDTO> layerArea(@RequestBody LayerPGDTO pgdto);



    /**
     * 添加图层
     */
    @PostMapping("/layerInfo/addLayer")
    Result<LayerPGRes> addLayer(@RequestBody LayerPGDTO pgdto);



    /**
     * 统计租户 + 行业下所有耕地 的地类名称为 '水田','水浇地','旱地'的面积
     * @apiNote 统计租户 + 行业下所有耕地 的地类名称为 '水田','水浇地','旱地'的面积
     * @apiVersion 2.1.6
     * @tag 二、gis能力
     * @requestDemo
     *{
     *     "tenantId":"121063377",
     *     "industryCode":"500000"
     * }
     * @responseDemo
     *  返回参数解析：
     *      code:200 成功 500失败
     *      msg: 消息提示
     *      data: 统计的面积
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1735279216169,
     * 	"data": "247505532.83"
     * }
     * @param statisticGdxzmjReq
     * @return Result
     */
    @PostMapping("/getFeatures/statisticGdxzmj")
    Result statisticGdxzmj(@RequestBody StatisticGdxzmjReq statisticGdxzmjReq);

    /**
     * 获取下拉数据要素列表
     * @apiNote 获取下拉数据要素列表
     * @apiVersion 2.1.7
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "layerID":"c06a7758b84743eba657cde84969b771",
     *     "returnGeometry":true,
     *     "firstIndex":1,
     *     "count":1
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        geometry: 返回的几何信息 根据请求参数 returnGeometry 判断是否返回
     *        area: 面积
     *        tbbh: 图斑编号
     *        bsm: 标识码唯一编号
     *        geoType: 几何类型
     *        total: 总数
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1735279432757,
     * 	"data": {
     * 		"geoData": {
     * 			"features": [
     * 				                {
     * 					"geometry": {
     * 						"coordinates": [
     * 							[
     * 								[
     * 									[
     * 										116.175886525,
     * 										28.284056962
     * 									],
     * 									[
     * 										116.176097846,
     * 										28.284091846
     * 									],
     * 									[
     * 										116.176700224,
     * 										28.284119938
     * 									],
     * 									[
     * 										116.176901616,
     * 										28.283997659
     * 									],
     * 									[
     * 										116.176588843,
     * 										28.282504299
     * 									],
     * 									[
     * 										116.176342802,
     * 										28.281423493
     * 									],
     * 									[
     * 										116.175870951,
     * 										28.281634758
     * 									],
     * 									[
     * 										116.175164254,
     * 										28.281882835
     * 									],
     * 									[
     * 										116.174603382,
     * 										28.282034206
     * 									],
     * 									[
     * 										116.174508721,
     * 										28.282000606
     * 									],
     * 									[
     * 										116.174365953,
     * 										28.282067231
     * 									],
     * 									[
     * 										116.174108972,
     * 										28.282086266
     * 									],
     * 									[
     * 										116.173842472,
     * 										28.282143373
     * 									],
     * 									[
     * 										116.173604526,
     * 										28.28220048
     * 									],
     * 									[
     * 										116.174094821,
     * 										28.283393306
     * 									],
     * 									[
     * 										116.174220663,
     * 										28.283781037
     * 									],
     * 									[
     * 										116.174286052,
     * 										28.28404282
     * 									],
     * 									[
     * 										116.174617982,
     * 										28.284002052
     * 									],
     * 									[
     * 										116.174683737,
     * 										28.284298243
     * 									],
     * 									[
     * 										116.175660475,
     * 										28.284106357
     * 									],
     * 									[
     * 										116.17570734,
     * 										28.284149272
     * 									],
     * 									[
     * 										116.175886525,
     * 										28.284056962
     * 									]
     * 								]
     * 							]
     * 						],
     * 						"type": "MultiPolygon"
     *                    },
     * 					"type": "Feature",
     * 					"properties": {
     * 						"area": 67550.59,
     * 						"tbbh": "4",
     * 						"bsm": "131444437"
     *                    }
     *                }
     * 			],
     * 			"id": "212",
     * 			"type": "FeatureCollection"
     * 		},
     * 		"allCount": 22,
     * 		"geoType": "MULTIPOLYGON"
     * 	}
     * }
     * @param getXlsjFeaturesReq
     * @return Result
     */
    @PostMapping("/getFeatures/getXlsjFeatures")
    Result<GeojsonStruckRes> getXlsjFeatures(@RequestBody GetXlsjFeaturesReq getXlsjFeaturesReq);

    /**
     * 根据图层ID返回对应参数的条件下的面积
     * @apiNote 查询图层面积统计
     * @apiVersion 2.1.8
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "layerID":"c06a7758b84743eba657cde84969b771",
     *     "featureIDs":["131444437","131444433"]
     * }
     * @responseDemo
     *  返回参数解析：
     *      code:200 成功 500失败
     *      msg: 消息提示
     *      data: 统计的面积
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1735280171783,
     * 	"data": "93447.41"
     * }
     * @param statisticDkmjReq
     * @return Result
     */
    @PostMapping("/getFeatures/statisticDkmj")
    Result statisticDkmj(@RequestBody StatisticDkmjReq statisticDkmjReq);

    /**
     * 根据图层ID及条件返回对应图斑数据
     * @apiNote 根据图层ID及条件返回对应图斑数据
     * @apiVersion 2.1.9
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "layerID":"c06a7758b84743eba657cde84969b771",
     *     "tbbhs":["131444437","131444433"],
     *     "returnAllCols":true,
     *     "returnGeometry":true,
     *     "pageNum":1,
     *     "pageSize":1
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        geometry: 返回的几何信息 根据请求参数 returnGeometry 判断是否返回
     *        geoType: 几何类型
     *        total: 总数
     *        properties: 图层类型字段数据 根据所属类型返回对应字段
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1735280371812,
     * 	"data": {
     * 		"geoData": {
     * 			"features": [
     * 				                {
     * 					"geometry": {
     * 						"coordinates": [
     * 							[
     * 								[
     * 									[
     * 										116.175886525,
     * 										28.284056962
     * 									],
     * 									[
     * 										116.176097846,
     * 										28.284091846
     * 									],
     * 									[
     * 										116.176700224,
     * 										28.284119938
     * 									],
     * 									[
     * 										116.176901616,
     * 										28.283997659
     * 									],
     * 									[
     * 										116.176588843,
     * 										28.282504299
     * 									],
     * 									[
     * 										116.176342802,
     * 										28.281423493
     * 									],
     * 									[
     * 										116.175870951,
     * 										28.281634758
     * 									],
     * 									[
     * 										116.175164254,
     * 										28.281882835
     * 									],
     * 									[
     * 										116.174603382,
     * 										28.282034206
     * 									],
     * 									[
     * 										116.174508721,
     * 										28.282000606
     * 									],
     * 									[
     * 										116.174365953,
     * 										28.282067231
     * 									],
     * 									[
     * 										116.174108972,
     * 										28.282086266
     * 									],
     * 									[
     * 										116.173842472,
     * 										28.282143373
     * 									],
     * 									[
     * 										116.173604526,
     * 										28.28220048
     * 									],
     * 									[
     * 										116.174094821,
     * 										28.283393306
     * 									],
     * 									[
     * 										116.174220663,
     * 										28.283781037
     * 									],
     * 									[
     * 										116.174286052,
     * 										28.28404282
     * 									],
     * 									[
     * 										116.174617982,
     * 										28.284002052
     * 									],
     * 									[
     * 										116.174683737,
     * 										28.284298243
     * 									],
     * 									[
     * 										116.175660475,
     * 										28.284106357
     * 									],
     * 									[
     * 										116.17570734,
     * 										28.284149272
     * 									],
     * 									[
     * 										116.175886525,
     * 										28.284056962
     * 									]
     * 								]
     * 							]
     * 						],
     * 						"type": "MultiPolygon"
     *                    },
     * 					"type": "Feature",
     * 					"properties": {
     * 						"tbybh": null,
     * 						"tbbh": "4",
     * 						"gid": 5,
     * 						"xzdwkd": null,
     * 						"kcmj": 67550.59,
     * 						"czcsxm": null,
     * 						"zldwmc": "223444",
     * 						"qsxz": "10",
     * 						"layertype": "xztb",
     * 						"kcdlbm": "44115",
     * 						"miny": 28.281423493161526,
     * 						"minx": 116.17360452588662,
     * 						"tbdlmj": 0,
     * 						"kclx": "20",
     * 						"gdpdjb2": "5",
     * 						"bz": null,
     * 						"xzdwmj": 67550.59,
     * 						"gdlx": "20",
     * 						"ysdm": "23341131316",
     * 						"qsdwmc": "南昌市进贤县",
     * 						"zldwdm": "",
     * 						"kcxs": 0,
     * 						"hdmc": null,
     * 						"gdpdj": "5",
     * 						"maxy": 28.28429824335001,
     * 						"maxx": 116.17690161629514,
     * 						"zzsxmc": null,
     * 						"tbxhdm": null,
     * 						"qsdwdm": "223444",
     * 						"lxdwmj": 67550.59,
     * 						"gddb": null,
     * 						"pzwh": "33414117",
     * 						"dlmc": "旱地",
     * 						"zzsxdm": null,
     * 						"mssm": null,
     * 						"calarea": 67561.50703220919,
     * 						"sjnf": null,
     * 						"tbxhmc": null,
     * 						"frdbs": null,
     * 						"dlbm": "0103",
     * 						"bsm": "131444437",
     * 						"tbmj": 67550.59
     *                    }
     *                }
     * 			],
     * 			"id": "212",
     * 			"type": "FeatureCollection"
     * 		},
     * 		"allCount": 2,
     * 		"geoType": "MULTIPOLYGON"
     * 	}
     * }
     * @param getXjxlFeaturesByIdsReq
     * @return Result
     */
    @PostMapping("/getFeatures/getXjxlFeaturesByIds")
    Result<GeojsonStruckRes> getXjxlFeaturesByIds(@RequestBody GetXjxlFeaturesByIdsReq getXjxlFeaturesByIdsReq);

    /**
     * 根据过滤条件查询
     * @deprecated 已废弃，请使用 layerParcelQuery
     */
    @PostMapping("/getFeatures/getFeaturesByFilter")
    Result getFeaturesByFilter(@RequestBody GetFeaturesByFilterReq getFeaturesByFilterReq);


    /**
     * 根据空间范围过滤条件查询
     * @deprecated 已废弃，请使用 layerParcelQuery
     */
    @PostMapping("/getFeatures/getFeaturesByWkt")
    Result getFeaturesByWkt(@RequestBody GetFeaturesByWktReq getFeaturesByWktReq);

    /**
     * 生态保护红线查询
     */
    @PostMapping("/getFeatures/searchSTBHHX")
    Result searchSTBHHX(@RequestBody SearchSTBHHXReq searchSTBHHXReq);

    /**
     * 永久基本农田查询
     */
    @PostMapping("/getFeatures/searchJBNT")
    Result searchJBNT(@RequestBody SearchJBNTReq searchJBNTReq);


    /**
     * 土地利用现状查询
     */
    @PostMapping("/getFeatures/searcXZTB")
    Result searchXZTB(@RequestBody SearchXZTBReq searchXZTBReq);

    /**
     * 建设用地管制区查询,土地利用总体规划
     */
    @PostMapping("/getFeatures/searcJSYDGZQ")
    Result searcJSYDGZQ(@RequestBody SearcJSYDGZQReq searcJSYDGZQReq);

    /**
     * 土地报批查询
     */
    @PostMapping("/getFeatures/searchTDBPTReq")
    Result searcTDBP(@RequestBody SearchTDBPTReq searchTDBPTReq);



    /**
     * 地块查询其他类型查询
     */
    @PostMapping("/getFeatures/searchOther")
    Result searchOther(@RequestBody SearcOtherReq searcOtherReq);

    /**
     * 返回所需图层一条图斑数据
     * @apiNote 返回所需图层一条图斑数据
     * @apiVersion 2.1.10
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "layerIds":["c06a7758b84743eba657cde84969b771","2541e82a836c430ab0d031751f72c073"]
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        geometry: 返回的几何信息
     *        layerId:图层id
     *        tableName:表名
     *        geoType:几何类型
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1735281543892,
     * 	"data": [
     * 		        {
     * 			"layerId": "c06a7758b84743eba657cde84969b771",
     * 			"tableName": "polygon_xztb_1709603417968_hlpgh6",
     * 			"geoType": "MULTIPOLYGON",
     * 			"geometry": {
     * 				"coordinates": [
     * 					[
     * 						[
     * 							[
     * 								116.173505696,
     * 								28.281708346
     * 							],
     * 							[
     * 								116.173479204,
     * 								28.281576886
     * 							],
     * 							[
     * 								116.173470197,
     * 								28.281486114
     * 							],
     * 							[
     * 								116.17346113,
     * 								28.281402981
     * 							],
     * 							[
     * 								116.17344237,
     * 								28.281317881
     * 							],
     * 							[
     * 								116.173420134,
     * 								28.281194084
     * 							],
     * 							[
     * 								116.173406594,
     * 								28.281131455
     * 							],
     * 							[
     * 								116.173394034,
     * 								28.281081724
     * 							],
     * 							[
     * 								116.173381935,
     * 								28.281042022
     * 							],
     * 							[
     * 								116.173372105,
     * 								28.28098801
     * 							],
     * 							[
     * 								116.173363187,
     * 								28.280955488
     * 							],
     * 							[
     * 								116.173357442,
     * 								28.280931104
     * 							],
     * 							[
     * 								116.173355976,
     * 								28.280911519
     * 							],
     * 							[
     * 								116.173356211,
     * 								28.280880963
     * 							],
     * 							[
     * 								116.173356444,
     * 								28.280850885
     * 							],
     * 							[
     * 								116.173359567,
     * 								28.280795518
     * 							],
     * 							[
     * 								116.173367796,
     * 								28.280718726
     * 							],
     * 							[
     * 								116.173325093,
     * 								28.280722444
     * 							],
     * 							[
     * 								116.17319632,
     * 								28.280740285
     * 							],
     * 							[
     * 								116.173111677,
     * 								28.280753142
     * 							],
     * 							[
     * 								116.173020577,
     * 								28.28076393
     * 							],
     * 							[
     * 								116.172862639,
     * 								28.280786847
     * 							],
     * 							[
     * 								116.17279919,
     * 								28.280796609
     * 							],
     * 							[
     * 								116.172726981,
     * 								28.28080572
     * 							],
     * 							[
     * 								116.172666911,
     * 								28.280814309
     * 							],
     * 							[
     * 								116.172635831,
     * 								28.280823073
     * 							],
     * 							[
     * 								116.172593234,
     * 								28.280838929
     * 							],
     * 							[
     * 								116.172539186,
     * 								28.280853522
     * 							],
     * 							[
     * 								116.172447966,
     * 								28.280879827
     * 							],
     * 							[
     * 								116.172396605,
     * 								28.28089563
     * 							],
     * 							[
     * 								116.172368208,
     * 								28.2809062
     * 							],
     * 							[
     * 								116.172344533,
     * 								28.280916203
     * 							],
     * 							[
     * 								116.172322919,
     * 								28.280921443
     * 							],
     * 							[
     * 								116.172290531,
     * 								28.280924828
     * 							],
     * 							[
     * 								116.172126633,
     * 								28.280942515
     * 							],
     * 							[
     * 								116.171955775,
     * 								28.280962249
     * 							],
     * 							[
     * 								116.171835264,
     * 								28.280973797
     * 							],
     * 							[
     * 								116.171801134,
     * 								28.280977068
     * 							],
     * 							[
     * 								116.171533945,
     * 								28.281004095
     * 							],
     * 							[
     * 								116.171365547,
     * 								28.281019545
     * 							],
     * 							[
     * 								116.171289475,
     * 								28.281022426
     * 							],
     * 							[
     * 								116.171263834,
     * 								28.281025255
     * 							],
     * 							[
     * 								116.171254368,
     * 								28.281028779
     * 							],
     * 							[
     * 								116.171248245,
     * 								28.281035903
     * 							],
     * 							[
     * 								116.171249254,
     * 								28.281051588
     * 							],
     * 							[
     * 								116.17125005,
     * 								28.281063965
     * 							],
     * 							[
     * 								116.171254262,
     * 								28.281081438
     * 							],
     * 							[
     * 								116.171307533,
     * 								28.281302445
     * 							],
     * 							[
     * 								116.171336231,
     * 								28.281427355
     * 							],
     * 							[
     * 								116.171376625,
     * 								28.281609034
     * 							],
     * 							[
     * 								116.171409178,
     * 								28.281758436
     * 							],
     * 							[
     * 								116.17144912,
     * 								28.281911464
     * 							],
     * 							[
     * 								116.171477069,
     * 								28.282045919
     * 							],
     * 							[
     * 								116.171519444,
     * 								28.282232981
     * 							],
     * 							[
     * 								116.171535621,
     * 								28.282306968
     * 							],
     * 							[
     * 								116.171553107,
     * 								28.282373383
     * 							],
     * 							[
     * 								116.171741247,
     * 								28.282307618
     * 							],
     * 							[
     * 								116.171865068,
     * 								28.282268
     * 							],
     * 							[
     * 								116.172028121,
     * 								28.282220089
     * 							],
     * 							[
     * 								116.172046166,
     * 								28.282214787
     * 							],
     * 							[
     * 								116.172218526,
     * 								28.28215794
     * 							],
     * 							[
     * 								116.172421992,
     * 								28.282089345
     * 							],
     * 							[
     * 								116.172576741,
     * 								28.282043134
     * 							],
     * 							[
     * 								116.172724775,
     * 								28.2819933
     * 							],
     * 							[
     * 								116.172834236,
     * 								28.281962331
     * 							],
     * 							[
     * 								116.172974832,
     * 								28.281915437
     * 							],
     * 							[
     * 								116.173074853,
     * 								28.281884411
     * 							],
     * 							[
     * 								116.173191811,
     * 								28.281842744
     * 							],
     * 							[
     * 								116.173331068,
     * 								28.281794648
     * 							],
     * 							[
     * 								116.173477019,
     * 								28.281752561
     * 							],
     * 							[
     * 								116.173515064,
     * 								28.281739844
     * 							],
     * 							[
     * 								116.173512501,
     * 								28.281735125
     * 							],
     * 							[
     * 								116.173505696,
     * 								28.281708346
     * 							]
     * 						],
     * 						[
     * 							[
     * 								116.171805017,
     * 								28.281579915
     * 							],
     * 							[
     * 								116.171770685,
     * 								28.281573141
     * 							],
     * 							[
     * 								116.171751134,
     * 								28.281573022
     * 							],
     * 							[
     * 								116.171726841,
     * 								28.28157586
     * 							],
     * 							[
     * 								116.171707965,
     * 								28.281575745
     * 							],
     * 							[
     * 								116.171688427,
     * 								28.281573836
     * 							],
     * 							[
     * 								116.171678348,
     * 								28.281569597
     * 							],
     * 							[
     * 								116.171664951,
     * 								28.281558176
     * 							],
     * 							[
     * 								116.17166107,
     * 								28.281534773
     * 							],
     * 							[
     * 								116.171661136,
     * 								28.281526216
     * 							],
     * 							[
     * 								116.171654391,
     * 								28.281491797
     * 							],
     * 							[
     * 								116.171655029,
     * 								28.281469647
     * 							],
     * 							[
     * 								116.171658872,
     * 								28.281461076
     * 							],
     * 							[
     * 								116.1716735,
     * 								28.281452571
     * 							],
     * 							[
     * 								116.171687001,
     * 								28.281450265
     * 							],
     * 							[
     * 								116.171725264,
     * 								28.281454317
     * 							],
     * 							[
     * 								116.17175473,
     * 								28.281456971
     * 							],
     * 							[
     * 								116.171883918,
     * 								28.281468606
     * 							],
     * 							[
     * 								116.171975352,
     * 								28.281476841
     * 							],
     * 							[
     * 								116.172000139,
     * 								28.281479856
     * 							],
     * 							[
     * 								116.172009774,
     * 								28.281489464
     * 							],
     * 							[
     * 								116.172015033,
     * 								28.281506684
     * 							],
     * 							[
     * 								116.17201673,
     * 								28.281534347
     * 							],
     * 							[
     * 								116.172016804,
     * 								28.281568366
     * 							],
     * 							[
     * 								116.172015342,
     * 								28.28158298
     * 							],
     * 							[
     * 								116.172011215,
     * 								28.281593697
     * 							],
     * 							[
     * 								116.171992875,
     * 								28.281611192
     * 							],
     * 							[
     * 								116.17198239,
     * 								28.281615904
     * 							],
     * 							[
     * 								116.171963164,
     * 								28.28161728
     * 							],
     * 							[
     * 								116.171953074,
     * 								28.281614234
     * 							],
     * 							[
     * 								116.171942976,
     * 								28.281612382
     * 							],
     * 							[
     * 								116.171929502,
     * 								28.281611107
     * 							],
     * 							[
     * 								116.171913331,
     * 								28.281609815
     * 							],
     * 							[
     * 								116.171911001,
     * 								28.281609744
     * 							],
     * 							[
     * 								116.171889065,
     * 								28.281609072
     * 							],
     * 							[
     * 								116.17185877,
     * 								28.281603516
     * 							],
     * 							[
     * 								116.171843611,
     * 								28.281594304
     * 							],
     * 							[
     * 								116.171835954,
     * 								28.281589651
     * 							],
     * 							[
     * 								116.171805017,
     * 								28.281579915
     * 							]
     * 						],
     * 						[
     * 							[
     * 								116.172218119,
     * 								28.281478902
     * 							],
     * 							[
     * 								116.172235873,
     * 								28.281449765
     * 							],
     * 							[
     * 								116.172236745,
     * 								28.281424107
     * 							],
     * 							[
     * 								116.172236925,
     * 								28.281400832
     * 							],
     * 							[
     * 								116.172243737,
     * 								28.281391921
     * 							],
     * 							[
     * 								116.172255272,
     * 								28.281382442
     * 							],
     * 							[
     * 								116.172269536,
     * 								28.281368802
     * 							],
     * 							[
     * 								116.172288679,
     * 								28.281334302
     * 							],
     * 							[
     * 								116.172306489,
     * 								28.281298004
     * 							],
     * 							[
     * 								116.172322175,
     * 								28.281274823
     * 							],
     * 							[
     * 								116.172352009,
     * 								28.281252921
     * 							],
     * 							[
     * 								116.172367529,
     * 								28.281251225
     * 							],
     * 							[
     * 								116.172387656,
     * 								28.281263879
     * 							],
     * 							[
     * 								116.172414457,
     * 								28.281285528
     * 							],
     * 							[
     * 								116.172436478,
     * 								28.281314905
     * 							],
     * 							[
     * 								116.172446346,
     * 								28.281346597
     * 							],
     * 							[
     * 								116.172448831,
     * 								28.281374066
     * 							],
     * 							[
     * 								116.172446715,
     * 								28.281385989
     * 							],
     * 							[
     * 								116.17243785,
     * 								28.281399065
     * 							],
     * 							[
     * 								116.172422912,
     * 								28.281412702
     * 							],
     * 							[
     * 								116.172403297,
     * 								28.281420938
     * 							],
     * 							[
     * 								116.172370873,
     * 								28.281429098
     * 							],
     * 							[
     * 								116.172332981,
     * 								28.281446772
     * 							],
     * 							[
     * 								116.1723086,
     * 								28.281460948
     * 							],
     * 							[
     * 								116.172298428,
     * 								28.281468645
     * 							],
     * 							[
     * 								116.172294918,
     * 								28.281486529
     * 							],
     * 							[
     * 								116.172300825,
     * 								28.281507453
     * 							],
     * 							[
     * 								116.172311353,
     * 								28.281540939
     * 							],
     * 							[
     * 								116.172319304,
     * 								28.281558892
     * 							],
     * 							[
     * 								116.172319221,
     * 								28.281569634
     * 							],
     * 							[
     * 								116.172315052,
     * 								28.281585723
     * 							],
     * 							[
     * 								116.172299449,
     * 								28.281598162
     * 							],
     * 							[
     * 								116.172285073,
     * 								28.281608817
     * 							],
     * 							[
     * 								116.172256893,
     * 								28.281608647
     * 							],
     * 							[
     * 								116.1722253,
     * 								28.281596519
     * 							],
     * 							[
     * 								116.172195871,
     * 								28.281565903
     * 							],
     * 							[
     * 								116.172189292,
     * 								28.281544975
     * 							],
     * 							[
     * 								116.17218545,
     * 								28.28151869
     * 							],
     * 							[
     * 								116.172193701,
     * 								28.281497852
     * 							],
     * 							[
     * 								116.172218119,
     * 								28.281478902
     * 							]
     * 						]
     * 					]
     * 				],
     * 				"type": "MultiPolygon"
     *            }
     *        },
     *        {
     * 			"layerId": "2541e82a836c430ab0d031751f72c073",
     * 			"tableName": "polygon_xztb_1696669980469_5spdgq",
     * 			"geoType": "MULTIPOLYGON",
     * 			"geometry": {
     * 				"coordinates": [
     * 					[
     * 						[
     * 							[
     * 								116.281722165,
     * 								40.03269278
     * 							],
     * 							[
     * 								116.286397806,
     * 								40.032228786
     * 							],
     * 							[
     * 								116.286719033,
     * 								40.030087271
     * 							],
     * 							[
     * 								116.282792923,
     * 								40.028623902
     * 							],
     * 							[
     * 								116.281722165,
     * 								40.03269278
     * 							]
     * 						]
     * 					]
     * 				],
     * 				"type": "MultiPolygon"
     *            }
     *        }
     * 	]
     * }
     * @param req
     * @return Result
     */
    @PostMapping("/getFeatures/getGeometrys")
    Result<List<GeometrysRes>> getGeometrys(@RequestBody GeometrysReq req);

    /**
     * 深林资源 小班列表
     * @apiNote 深林资源 小班列表
     * @apiVersion 2.1.11
     * @tag 二、gis能力
     * @param req
     * @requestDemo
     * {
     *     "layerId":"图层ID",
     *     "pageNum":1,
     *     "pageSize":5,
     *     "diLei":"优势树(灌)种",
     *     "youShiSz":"编码",
     *     "xiaoBan":"小班"
     * }
     * @responseDemo
     * 返回参数解析：
     *  mei_gq_zs:每公顷株数
     * zl_dj:林地质量等级
     * tu_ceng_hd:土层厚度
     * sheng:省(区、市)
     * you_shi_sz:优势树(灌)种
     * yu_bi_du_1:乔木郁闭度/灌木覆盖度
     * po_wei:坡位
     * stqw:生态区位
     * hai_ba:海拔
     * di_mao:地貌
     * shiquan_d:事权等级
     * bsm:标识码测试字符长度大于6个时
     * qykz:主体功能区
     * yu_bi_du:乔木郁闭度/灌木覆盖度
     * mei_gq_xj:每公顷蓄积
     * lin_chang:林场(分场)
     * zbfglx:植被覆盖类型
     * xbmj:小班面积
     * pingjun_xj:平均胸径
     * g_cheng_lb:工程类别
     * xian:县(市、旗)
     * ld_qs:土地所有权属
     * sheng_wu_l:生物量
     * disaster_c:灾害等级
     * tdsyqs:土地使用权属
     * lmqs:林木所有权属
     * lin_zhong:林种
     * bh_dj:林地保护等级
     * lin_ban:林班
     * stqwmc:生态区位名称
     * dispe:灾害类型
     * lin_ye_ju:林业局(场)
     * ldgh_gl:林地规划管理
     * gjgyl_bhdj:国家级公益林保护等级国家级公益林保护等级国家级公益林保护等级
     * ld_kd:林带宽度
     * jt_qw:交通区位
     * di_lei:地类
     * tbbh:图斑编号
     * td_th_lx:土地退化类型
     * pingjun_sg:平均树高
     * sf_tbq:是否天保区公益林
     * sen_lin_lb:森林类别
     * xiang:乡
     * lyfq:林地功能分区
     * po_du:坡度
     * tu_rang_lx:土壤类型(名称)
     * po_xiang:坡向
     * ld_cd:林带长度
     * huo_lmxj:蓄积量
     * lmsyqs:林木使用权属
     * cun:村
     * ling_zu:龄组
     * qi_yuan:起源
     * tan_chu_l:碳储量
     * xiao_ban:小班
     *
     * {
     * 	"total": 3,
     * 	"rows": [
     *                {
     * 			"layerId": "d145f5ecc3224f9da83a3a68d75c227f",
     * 			"geometry": {
     * 				"coordinates": [
     * 					[
     * 						[
     * 							[
     * 								117.478315322,
     * 								35.573240575
     * 							]
     * 						]
     * 					]
     * 				],
     * 				"type": "MultiPolygon"
     *            },
     * 			"gid": 5771,
     * 			"minx": "117.47726366300003",
     * 			"miny": "35.57252007300008",
     * 			"maxx": "117.47868032400004",
     * 			"maxy": "35.57328793700003",
     * 			"bsm": "370831211000044721",
     * 			"layertype": "slzy",
     * 			"tbbh": "100",
     * 			"sheng": "37",
     * 			"xian": "370831",
     * 			"xiang": "108",
     * 			"cun": "250",
     * 			"linYeJu": "",
     * 			"linChang": "",
     * 			"diMao": "4",
     * 			"haiBa": null,
     * 			"poXiang": "7",
     * 			"poWei": "2",
     * 			"poDu": 1,
     * 			"jtQw": null,
     * 			"qykz": "3",
     * 			"tuRangLx": "122",
     * 			"tuCengHd": 3,
     * 			"ldQs": "20",
     * 			"tdsyqs": "2",
     * 			"linBan": "",
     * 			"xiaoBan": "00067",
     * 			"xbmj": 5000.05,
     * 			"ldKd": 0,
     * 			"ldCd": 0,
     * 			"diLei": "0141",
     * 			"zbfglx": null,
     * 			"youShiSz": "530000",
     * 			"qiYuan": "21",
     * 			"lingZu": " ",
     * 			"yuBiDu": 0,
     * 			"disasterC": "0",
     * 			"dispe": "00",
     * 			"pingjunXj": 0,
     * 			"pingjunSg": null,
     * 			"meiGqZs": 765,
     * 			"meiGqXj": null,
     * 			"huoLmxj": null,
     * 			"shengWuL": null,
     * 			"tanChuL": null,
     * 			"lmsyqs": "2",
     * 			"lmqs": "2",
     * 			"stqw": "",
     * 			"stqwmc": "",
     * 			"senLinLb": "022",
     * 			"linZhong": "",
     * 			"gjgylBhdj": " ",
     * 			"shiquanD": null,
     * 			"sfTbq": null,
     * 			"lyfq": "泗南砂石山丘水土保持",
     * 			"ldghGl": null,
     * 			"bhDj": "4",
     * 			"zlDj": "3",
     * 			"tdThLx": "0",
     * 			"gchengLb": "00"
     *        }
     * 	],
     * 	"code": 200,
     * 	"msg": null
     * }
     * @return TableDataInfo
     */
    @PostMapping("/slzy/forestLayerXmList")
    TableDataInfo forestLayerXmList(@RequestBody ForestLayerXmReq req);

    /**
     *深林资源 图层图例统计
     * @apiNote 深林资源 图层图例统计
     * @apiVersion 2.1.12
     * @tag 二、gis能力
     * @param req
     * @requestDemo
     * {
     *        "layerId":"图层ID",
     *        "type":专题统计类型 1- 优势树(灌)种 2-乔木郁闭度/灌木覆盖度 3林种 4森林类别 5地类 6地貌 7植被覆盖类型 8林地保护等级,
     *        "needRatio":1-需要计算比例 默认0不计算
     * }
     * @responseDemo
     *    返回参数解析：
     *      "fieldCode": "深林专题统计字段",
     *      "fieldValue": "深林专题统计字段值",
     *      "fieldName": "深林专题统计字段名称",
     *      "totalMj": "总面积",
     *      "ratio": "比例"
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1701915039748,
     * 	"data": [
     *                {
     * 			"fieldCode": "you_shi_sz 深林专题统计字段",
     * 			"fieldValue": "530000 深林专题统计字段值",
     * 			"fieldName": null,
     * 			"totalMj": 266340.76,
     * 			"ratio": "30.49"
     *        }"ratio": "0.00"
     *        }
     * 	]
     * }
     * @return Result<List<ForestLayerLegendRes>>
     */
    @PostMapping("/slzy/forestLayerLegendRes")
    Result<List<ForestLayerLegendRes>> forestLayerLegendRes(@RequestBody ForestLayerLegendReq req);

    /**
     * 深林资源 小班详情
     * @apiNote 深林资源 小班详情
     *
     * @apiVersion 2.1.13
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "layerId":"b707fa1968e04b6c9faedec3860c913d",
     *     "bsm":"370831211000171853"
     * }
     * @responseDemo
     * 返回参数解析:
     * mei_gq_zs:每公顷株数
     * zl_dj:林地质量等级
     * tu_ceng_hd:土层厚度
     * sheng:省(区、市)
     * you_shi_sz:优势树(灌)种
     * yu_bi_du_1:乔木郁闭度/灌木覆盖度
     * po_wei:坡位
     * stqw:生态区位
     * hai_ba:海拔
     * di_mao:地貌
     * shiquan_d:事权等级
     * bsm:标识码测试字符长度大于6个时
     * qykz:主体功能区
     * yu_bi_du:乔木郁闭度/灌木覆盖度
     * mei_gq_xj:每公顷蓄积
     * lin_chang:林场(分场)
     * zbfglx:植被覆盖类型
     * xbmj:小班面积
     * pingjun_xj:平均胸径
     * g_cheng_lb:工程类别
     * xian:县(市、旗)
     * ld_qs:土地所有权属
     * sheng_wu_l:生物量
     * disaster_c:灾害等级
     * tdsyqs:土地使用权属
     * lmqs:林木所有权属
     * lin_zhong:林种
     * bh_dj:林地保护等级
     * lin_ban:林班
     * stqwmc:生态区位名称
     * dispe:灾害类型
     * lin_ye_ju:林业局(场)
     * ldgh_gl:林地规划管理
     * gjgyl_bhdj:国家级公益林保护等级国家级公益林保护等级国家级公益林保护等级
     * ld_kd:林带宽度
     * jt_qw:交通区位
     * di_lei:地类
     * tbbh:图斑编号
     * td_th_lx:土地退化类型
     * pingjun_sg:平均树高
     * sf_tbq:是否天保区公益林
     * sen_lin_lb:森林类别
     * xiang:乡
     * lyfq:林地功能分区
     * po_du:坡度
     * tu_rang_lx:土壤类型(名称)
     * po_xiang:坡向
     * ld_cd:林带长度
     * huo_lmxj:蓄积量
     * lmsyqs:林木使用权属
     * cun:村
     * ling_zu:龄组
     * qi_yuan:起源
     * tan_chu_l:碳储量
     * xiao_ban:小班
     *
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1735782774494,
     * 	"data": {
     * 		"tbbh": "357",
     * 		"xiang": "苗馆镇",
     * 		"lmsyqs": "2",
     * 		"bh_dj": "2",
     * 		"td_th_lx": "0",
     * 		"po_du": 1,
     * 		"gjgyl_bhdj": "2",
     * 		"you_shi_sz": "570",
     * 		"mei_gq_xj": 0,
     * 		"yu_bi_du_1": 3,
     * 		"pingjun_sg": 0,
     * 		"ld_kd": 0,
     * 		"qykz": "4",
     * 		"mei_gq_zs": 612,
     * 		"sheng_wu_l": 0,
     * 		"maxy": 35.56179531000006,
     * 		"maxx": 117.43630260900008,
     * 		"ld_cd": 0,
     * 		"ling_zu": "4",
     * 		"ldgh_gl": "",
     * 		"lyfq": "泗中石灰岩山丘水源林",
     * 		"lin_ye_ju": "",
     * 		"tu_rang_lx": "122",
     * 		"jt_qw": "",
     * 		"dispe": "00",
     * 		"shiquan_d": "",
     * 		"layerId": "b707fa1968e04b6c9faedec3860c913d",
     * 		"po_xiang": "1",
     * 		"di_mao": "5",
     * 		"pingjun_xj": 10.8,
     * 		"bsm": "370831211000171853",
     * 		"cun": null,
     * 		"xbmj": 302131.92,
     * 		"lin_chang": "",
     * 		"gid": 1166,
     * 		"stqwmc": "上述范围外2001年试点",
     * 		"hai_ba": "",
     * 		"di_lei": "030302",
     * 		"sf_tbq": "",
     * 		"qi_yuan": "21",
     * 		"g_cheng_lb": "00",
     * 		"tdsyqs": "2",
     * 		"lmqs": "2",
     * 		"layertype": "slzy",
     * 		"sen_lin_lb": "1",
     * 		"miny": 35.56086789800006,
     * 		"minx": 117.43551139600008,
     * 		"zl_dj": "3",
     * 		"huo_lmxj": 0,
     * 		"tu_ceng_hd": 2,
     * 		"lin_zhong": "112",
     * 		"xiao_ban": "00120",
     * 		"xian": "泗水县",
     * 		"ld_qs": "20",
     * 		"lin_ban": "",
     * 		"sheng": "山东省",
     * 		"stqw": "900",
     * 		"zbfglx": "1320",
     * 		"disaster_c": "0",
     * 		"calarea": 0,
     * 		"geometry": {
     * 			"coordinates": [
     * 				[
     * 					[
     * 						117.436302609,
     * 						35.560935091
     * 					],
     * 					[
     * 						117.436299921,
     * 						35.560930409
     * 					],
     * 					[
     * 						117.436282276,
     * 						35.560905433
     * 					],
     * 					[
     * 						117.436242743,
     * 						35.560880538
     * 					],
     * 					[
     * 						117.436219212,
     * 						35.560875542
     * 					],
     * 					[
     * 						117.436199357,
     * 						35.560871327
     * 					],
     * 					[
     * 						117.436089335,
     * 						35.560867898
     * 					],
     * 					[
     * 						117.436016615,
     * 						35.560873421
     * 					],
     * 					[
     * 						117.435998213,
     * 						35.56087892
     * 					],
     * 					[
     * 						117.435964011,
     * 						35.560905565
     * 					],
     * 					[
     * 						117.435945926,
     * 						35.560945664
     * 					],
     * 					[
     * 						117.435937078,
     * 						35.56100341
     * 					],
     * 					[
     * 						117.435926978,
     * 						35.5610401
     * 					],
     * 					[
     * 						117.435923408,
     * 						35.56108065
     * 					],
     * 					[
     * 						117.435914109,
     * 						35.561136098
     * 					],
     * 					[
     * 						117.435901999,
     * 						35.561191915
     * 					],
     * 					[
     * 						117.435879087,
     * 						35.561251507
     * 					],
     * 					[
     * 						117.435849975,
     * 						35.561320799
     * 					],
     * 					[
     * 						117.435823564,
     * 						35.56137804
     * 					],
     * 					[
     * 						117.435794585,
     * 						35.561408776
     * 					],
     * 					[
     * 						117.435787625,
     * 						35.561421858
     * 					],
     * 					[
     * 						117.435785481,
     * 						35.561446916
     * 					],
     * 					[
     * 						117.435790358,
     * 						35.561475348
     * 					],
     * 					[
     * 						117.43580133,
     * 						35.561500733
     * 					],
     * 					[
     * 						117.435803817,
     * 						35.561511522
     * 					],
     * 					[
     * 						117.435815809,
     * 						35.561532646
     * 					],
     * 					[
     * 						117.435824055,
     * 						35.561544014
     * 					],
     * 					[
     * 						117.435831022,
     * 						35.561559652
     * 					],
     * 					[
     * 						117.435831173,
     * 						35.561585794
     * 					],
     * 					[
     * 						117.435827704,
     * 						35.56159511
     * 					],
     * 					[
     * 						117.435798123,
     * 						35.561634559
     * 					],
     * 					[
     * 						117.435606231,
     * 						35.561787557
     * 					],
     * 					[
     * 						117.435597103,
     * 						35.561792034
     * 					],
     * 					[
     * 						117.435583935,
     * 						35.56179531
     * 					],
     * 					[
     * 						117.435568524,
     * 						35.561794452
     * 					],
     * 					[
     * 						117.435556191,
     * 						35.561790158
     * 					],
     * 					[
     * 						117.435541042,
     * 						35.561779165
     * 					],
     * 					[
     * 						117.435529598,
     * 						35.561767739
     * 					],
     * 					[
     * 						117.435511396,
     * 						35.561737656
     * 					]
     * 				]
     * 			],
     * 			"type": "MultiLineString"
     * 		        },
     * 		"po_wei": "3",
     * 		"tan_chu_l": 0,
     * 		"yu_bi_du": 0.35    * 	}
     * }
     * @param req
     * @return
     */
    @PostMapping("/slzy/forestLayerXmInfo")
    Result<Map<String,Object>> forestLayerXmInfo(@RequestBody ForestLayerXmInfoReq req);


    /**
     * 图层聚合查询
     * @apiNote 图层聚合查询
     * @apiVersion 2.1.14
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "layerID":"866de8c6ee864639b76b1400030fbe86",
     *     "firstIndex":1,
     *     "count":5,
     *     "filters":[
     *         {
     *             "code":"bsm",
     *             "searchType":1,
     *             "searchValue":"334",
     *             "valueType":1
     *         },
     *         {
     *             "code":"dlmc",
     *             "searchType":2,
     *             "searchValue":"旱地",
     *             "valueType":3
     *         }
     *     ]
     * }
     * @responseDemo
     *     返回参数解析：
     *     geometry: 返回的几何信息
     *     properties: 属性信息 每个类型都不一样
     *     geoType: 几何类型
     *
     *
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1706149393537,
     * 	"data": {
     * 		"geoData": {
     * 			"features": [
     *                                {
     * 					"geometry": {
     * 						"coordinates": [
     * 							[
     * 								[
     * 									[
     * 										116.290002689,
     * 										40.026982074
     * 									],
     * 									[
     * 										116.292215588,
     * 										40.027053458
     * 									],
     * 									[
     * 										116.292001437,
     * 										40.02541163
     * 									],
     * 									[
     * 										116.292858043,
     * 										40.023698418
     * 									],
     * 									[
     * 										116.291144831,
     * 										40.023341499
     * 									],
     * 									[
     * 										116.290002689,
     * 										40.026982074
     * 									]
     * 								]
     * 							]
     * 						],
     * 						"type": "MultiPolygon"
     *                    },
     * 					"id": 1,
     * 					"type": "feature",
     * 					"properties": {
     * 						"tbbh": "567.0",
     * 						"tkxs": 0.2,
     * 						"zldwmc": "北京市四叶草镇",
     * 						"xzqmc": "北京市",
     * 						"qsxz": "10.0",
     * 						"yjjbntmj": 45673.456,
     * 						"kcdlbm": "33234.0",
     * 						"gddj": 1,
     * 						"bz": "",
     * 						"gdlx": "10.0",
     * 						"ysdm": "1.23332146E10",
     * 						"qsdwmc": "北京市四叶草镇",
     * 						"zldwdm": "223444.0",
     * 						"tkmj": 3455.32,
     * 						"yjjbnttbmj": 45673.456,
     * 						"yjjbntlx": "10.0",
     * 						"tbxhdm": "HDGD",
     * 						"qsdwdm": "223444.0",
     * 						"gddb": 1,
     * 						"xzqdm": "223123.0",
     * 						"gdzzsxdm": "LS",
     * 						"dlmc": "旱地",
     * 						"gdpdjb": "1.0",
     * 						"yjjbnttbbh": "1234567814569",
     * 						"sjnf": 2003,
     * 						"tbxhmc": "河道耕地",
     * 						"frdbs": "344531.0",
     * 						"dlbm": "2345.0",
     * 						"gdzzsxmc": "种植粮食作物",
     * 						"bsm": "2344513441334"
     *                    }
     *                }
     * 			],
     * 			"type": "FeatureCollection"
     * 		},
     * 		"allCount": 4
     * 	}
     * }
     * @return
     */
    @PostMapping("/getFeatures/layerParcelQuery")
    Result layerParcelQuery(@RequestBody LayerParcelQueryDTO filter);

    /**
     * 计算探而未采图层
     */
    @PostMapping("/layer/calculateLayer")
    Result<LayerInfoAddDTO> calculateLayer(@RequestBody MineralsLayerReq filter);

    /**
     * 修改探矿权、采矿权、探而未采过期状态
     */
    @PostMapping("/layer/updateExpire")
    Result updateExpire();

    /**
     * 查询土地报批状态
     * @apiNote 查询土地报批状态
     * @apiVersion 2.1.15
     * @tag 二、gis能力
     * @requestDemo
     * 请求参数解析：
     *        id: 标识
     *        wkt: 查询图形的 wkt 字符串
     *
     * {
     *     "items": [
     *         {
     *             "id": "ssss",
     *             "wkt": "POINT(116.35078687738458 28.624970356710893)"
     *         },
     *         {
     *             "id": "wwwww",
     *             "wkt": "POINT(116.35078687738458 28.624970356710893)"
     *         },
     *         {
     *             "id": "rrrrrr",
     *             "wkt": "POINT(116.35078687738458 28.624970356710893)"
     *         },
     *         {
     *             "id": "uuuuu",
     *             "wkt": "POINT(116.35078687738458 28.624970356710893)"
     *         },
     *     ]
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        标识: 状态，未报批，已报批
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717639331275,
     *     "data": {
     *         "uuuuu": "未报批",
     *         "wwwww": "未报批",
     *         "rrrrrr": "已报批",
     *         "ssss": "已报批"
     *     }
     * }
     * @param searchTdbpStateReq
     * @return Result
     */
    @PostMapping("/alarm/gis/searchTdbpState")
    Result<Map<String, String>>  searchTdbpState(@RequestBody SearchTdbpStateReq searchTdbpStateReq);

    /**
     * 启动耕地的进出平衡计算
     * @apiNote 启动耕地的进出平衡计算
     * @apiVersion 2.1.16
     * @tag 二、gis能力
     * @requestDemo
     * 请求参数解析：
     *        preLayerName: 之前的图层表名
     *        curLayerName: 当前的图层表名
     *
     * {
     *     "preLayerName": "polygon_xztb_1717560143044_vk2hny",
     *     "curLayerName": "polygon_xztb_1717560182068_bftzy2"
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        guid: 耕地的进出平衡计算结果的GUID
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717642217105,
     *     "data": {
     * 	        "guid": "d627210aba414f93ae0dd5ce34e312cc"
     *     }
     * }
     * @param calInOutBalanceReq
     * @return Result
     */
    @PostMapping("/layer/calInOutBalance")
    Result<CalInOutBalanceRes> calInOutBalance(@RequestBody CalInOutBalanceReq calInOutBalanceReq);

    /**
     * 获取耕地的进出平衡的计算结果
     * @apiNote 获取耕地的进出平衡的计算结果
     * 请求参数解析：
     *        guid: 耕地的进出平衡计算结果的GUID
     * @apiVersion 2.1.17
     * @tag 二、gis能力
     * @requestDemo
     * {
     *     "guid": "d627210aba414f93ae0dd5ce34e312cc"
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        lc0101: 耕地流出面积中水田的面积
     *        lc0102: 耕地流出面积中水浇地的面积
     *        lc0103: 耕地流出面积中旱地的面积
     *        lr0101: 耕地流入面积中水田的面积
     *        lr0102: 耕地流入面积中水浇地的面积
     *        lr0103: 耕地流入面积中旱地的面积
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717644728980,
     *     "data": {
     *         "lc0101": "1000",
     *         "lc0102": "1001",
     *         "lc0103": "1002",
     *         "lr0101": "1000",
     *         "lr0102": "1001",
     *         "lr0103": "1002"
     *     }
     * }
     * @param getInOutBalanceReq
     * @return Result
     */
    @PostMapping("/layer/getInOutBalance")
    Result<GetInOutBalanceRes> getInOutBalance(@RequestBody GetInOutBalanceReq getInOutBalanceReq);

    /**
     * 图层编辑 备份旧表
     */
    @PostMapping("/layer/editLayer")
    Result editLayer(@RequestBody EditLayerVO vo);

    /**
     * 图层编辑 备份还原
     */
    @PostMapping("/layer/editBackLayer")
    Result editBackLayer(@RequestBody EditLayerVO vo);


    /**
     * 自动比较图层图斑的不同，用于图层比对编辑
     * 1、比较任意bsm字段内容长度，是否为32、39位字符长度，如果不是，则无需做任何操作
     * 2、如果是32、39位字符长度，则为系统自动填充bsm，需要进行图斑覆盖比例判断
     * 3、当某新一个图斑覆盖原图斑的95%以上时，更新为原图斑bsm字段
     * @apiNote 自动比较图层图斑的不同，用于图层比对编辑，
     * 请求参数解析：
     * {
     *     "preLayerName": "polygon_tdbp_1747387651567_d8azuw",
     *     "curLayerName": "polygon_tdbp_1747625694506_j2obzb"
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717642217105,
     *     "data": 7
     * }
     * @param calcAutoCompareLayerFeaturesReq
     * @return Result
     */
    @PostMapping("/layer/autoCompareLayerFeatures")
    Result autoCompareLayerFeatures(@RequestBody CalInOutBalanceReq calcAutoCompareLayerFeaturesReq);

    /**
     * 表里是否有bsm字段
     * @apiNote 表里是否有bsm字段
     * 请求参数解析：
     * {
     *     "layerName": "30ef83a1db394ca09d19f5956c6f48a0"
     * }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *        rowCount: 该表的记录数
     *        fieldLength: 耕地流出面积中水浇地的面积
     *        custom: 是否是自定义bsm字段
     *
     * 返回参数解析：
        {
            "code": 200,
            "msg": "请求成功",
            "time": 1747728074906,
            "data": {
                "rowCount": 10,
                "fieldLength": 第一条记录的长度,
                "custom": false
            }
        }
     * @param layerId
     * @return Result<LayerHasCustomBsmFieldRes>
     */
    @PostMapping("/layer/isLayerHasCustomBsmField")
    Result<LayerHasCustomBsmFieldRes> isLayerHasCustomBsmField(@RequestParam("layerId") String layerId);

    /**
     * 根据用户行业、租户获取所有相交图斑列表
     * @apiNote 根据用户行业、租户获取所有相交图斑列表
     * @apiVersion
     * @tag 二、gis能力
     * @requestDemo
     * 请求参数解析：
     *        wkt: 请求几何的WKT形式表达，默认坐标为4326
     *        distance: 图形的缓冲区距离，默认坐标为0
     *        layerItems: 图层列表
     *          layerID: 图层ID
     *
    {
        "wkt": "POINT(116.28784 40.04499)",
        "distance": "500",
        "layerItems":[
            {
                "layerID": "904d62ad9470436d83a9dd563b86a292"
            }
        ]
    }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *          layerID: 图层ID
     *          allCount: 当前图层Feature总数
     *          geoType: 图层类型
     *          geoData: geojson 对象
     *
    {
        "code": 200,
        "msg": "请求成功",
        "time": 1754536967181,
        "data": [
            {
                "geoData": {
                    "features": [
                        {
                            "geometry": {
                                "coordinates": [
                                [
                                ],
                                "type": "MultiPolygon"
                            },
                            "id": 4,
                            "type": "feature",
                            "properties": {
                                "tbbh": "14",
                                "bsm": "3344513441337"
                            }
                        }
                    ],
                    "type": "FeatureCollection"
                },
                "allCount": 1,
                "geoType": "MULTIPOLYGON",
                "layerID": "904d62ad9470436d83a9dd563b86a292"
            }
        ]
    }
     * @param getIntersectingPlotReq
     * @return Result
     */
    @PostMapping("/layer/getIntersectingPlot")
    Result getIntersectingPlot(@RequestBody GetIntersectingPlotReq getIntersectingPlotReq);

    /**
     * 根据用户行业、租户获取所有有相交图斑的图层列表
     * @apiNote 根据用户行业、租户获取所有有相交图斑的图层列表
     * @apiVersion
     * @tag 二、gis能力
     * @requestDemo
     * 请求参数解析：
     *        wkt: 请求几何的WKT形式表达，默认坐标为4326
     *        distance: 图形的缓冲区距离，默认坐标为0
     *
    {
        "wkt": "POINT(116.28784 40.04499)",
        "distance": "500"
    }
     * @responseDemo
     * 返回参数解析：
     *        code:200 成功 500失败
     *        msg: 消息提示
     *        data: 返回数据
     *          layerName: 图层表名
     *          layerId: 图层ID
     *          layerCustomName: 自定义图层名
     *
    {
        "code": 200,
        "msg": "请求成功",
        "time": 1754535639866,
        "data": [
            {
                "layerName": "polygon_xztb_1713340514727_guu3ql",
                "layerId": "8568a969aae940479bbdf55cbc401b55",
                "layerCustomName": "土地利用现状0417"
            },
            {
                "layerName": "polygon_yjjbnt_1713774970619_mtoeqe",
                "layerId": "904d62ad9470436d83a9dd563b86a292",
                "layerCustomName": "422测试"
            },
            {
                "layerName": "polygon_xztb_1730787671086_ngbrzs",
                "layerId": "a06b52628a8a40999e38860e871e6acd",
                "layerCustomName": "测试1105002"
            }
        ]
    }
     * @param getIntersectingPlotReq
     * @return Result
     */
    @PostMapping("/layer/getIntersectingLayers")
    Result getIntersectingLayers(@RequestBody GetIntersectingPlotReq getIntersectingPlotReq);
}
