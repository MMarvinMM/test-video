package com.chinatower.device.api;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IAlgorithmInfoServiceMigrate;
import com.chinatower.device.entity.dto.AlgoCommonReq;
import com.chinatower.device.entity.dto.AlgoInfoReq;
import com.chinatower.device.entity.dto.AlgoTypeReq;
import com.chinatower.device.entity.dto.FuncTypeReq;
import com.chinatower.device.entity.vo.FunctionTypeVO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class IAlgorithmInfoService {

    @Resource
    IAlgorithmInfoServiceMigrate iAlgorithmInfoServiceMigrate;

    @Resource
    IAlgorithmInfoSyncService iAlgorithmInfoSyncService;

    /**
     * 算法基本信息推送
     * @apiNote 算法基本信息推送
     * @apiVersion 3.36
     * @tag 三、设备能力
     * @requestDemo
     * [
     *     {
     *         "algorithmCode":"1001111",
     *         "algorithmName":"人脸识别",
     *         "algoMinTypeCode":1,
     *         "productName":"厂家1",
     *         "productCode":"1101112",
     *         "chipPlatform":1,
     *         "algorithmDescription":"人脸识别使用",
     *         "appScenarios":"场景一",
     *         "documentUrl":"http://localost:8080/dssd/wew.jpg",
     *         "logoUrl":" http://localost:8080/dssd/wew.jpg ",
     *         "version":"v1.0",
     *         "publishTime":"2012-02-04 23:23:23",
     *         "algorithmShape":2,
     *         "optType":1,
     *         "alarmTypeCodeList":[
     *             {
     *                 "alarmTypeCode":"xxxx"
     *             }
     *         ]
     *     }
     * ]
     * @responseDemo
     * {
     *     "returnCode":"0000",
     *     "returnMsg":"ok",
     *     "errorCode":"",
     *     "errorMsg":"",
     *     "data":{
     *
     *     }
     * }
     */
    @PostMapping("/video/algorithm/pushAlgorithmInfo")
    public Result<String> pushAlgorithmInfo(@RequestBody AlgoCommonReq<AlgoInfoReq> req) {
        return iAlgorithmInfoSyncService.pushAlgorithmInfo(req);
    }
    /**
     * 算法类别信息推送
     * @apiNote 算法类别信息推送
     * @apiVersion 3.37
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "eventId":"914",
     *     "data":[
     *         {
     *             "optType":1,
     *             "algoTypeCode":"10",
     *             "algoTypeName":"人员监控",
     *             "algoSubTypeCode":"1010",
     *             "algoSubTypeName":"人员行为监控",
     *             "algoMinTypeCode":"1010100",
     *             "algoMinTypeName":"人员聚集"
     *         }
     *     ]
     * }
     * @responseDemo
     * {
     *     "returnCode":"0000",
     *     "returnMsg":"ok",
     *     "errorCode":"",
     *     "errorMsg":"",
     *     "data":{
     *
     *     }
     * }
     */
    @PostMapping("/video/algorithm/pushAlgorithmType")
    public Result<String> pushAlgorithmType(@RequestBody AlgoCommonReq<AlgoTypeReq> req){
        return iAlgorithmInfoSyncService.pushAlgorithmType(req);
    }
    /**
     * 功能类别信息推送
     * @apiNote 功能类别信息推送
     * @apiVersion 3.38
     * @tag 三、设备能力
     * @requestDemo
     * [
     *     {
     *         "optType":1,
     *         "funcTypeCode":"10",
     *         "funcTypeName":"人员监控",
     *         "funcSubTypeCode":"1010",
     *         "funcSubTypeName":"人员行为监控",
     *         "funcMinTypeCode":"1010100",
     *         "funcMinTypeName":"人员聚集",
     *         "status":1,
     *         "logoUrl":"http://xxxx/xxxx.jpg",
     *         "logoImageName":"xxxx.svg",
     *         "desImageUrlList":[
     *             {
     *                 "imageUrl":"http://xxxx/xxxx.jpg"
     *             }
     *         ],
     *         "desVideoUrlList":[
     *             {
     *                 "videoUrl":"http://xxxx/xxxx.mp4"
     *             }
     *         ],
     *         "labelList":[
     *             {
     *                 "id":"10001",
     *                 "name":"xxxxx"
     *             }
     *         ],
     *         "functionDiscribtion":"人员行为监控算法",
     *         "unAlgoFunctionUrl":"http://xxxx/xxxx.jpg",
     *         "algoMinTypeCodeList":[
     *             {
     *                 "algoMinTypeCode":"1010100",
     *                 "algoMinTypeName":"人员聚集"
     *             }
     *         ]
     *     }
     * ]
     * @responseDemo
     * {
     *     "returnCode":"0000",
     *     "returnMsg":"ok",
     *     "errorCode":"",
     *     "errorMsg":"",
     *     "data":{
     *
     *     }
     * }
     */
    @PostMapping("/video/algorithm/pushFunctionType")
    public Result<String> pushFunctionType(@RequestBody AlgoCommonReq<FuncTypeReq> req){
        return iAlgorithmInfoSyncService.pushFunctionType(req);
    }

    /**
     * 获取功能信息
     * @apiNote 获取功能信息
     * @apiVersion 3.39
     * @tag 三、设备能力
     * @requestDemo
     * "122035807"
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694679536743,
     *     "data":{
     *         "noorder":[
     *             {
     *                 "id":467,
     *                 "functionType":"12",
     *                 "functionTypeName":"人员及行为监控",
     *                 "subFunctionType":"1212",
     *                 "subFunctionTypeName":"人员及行为监控",
     *                 "functionCode":"1212127",
     *                 "functionCodeName":"人员徘徊监控",
     *                 "functionDetail":"对指定区域的人员徘徊的行为进行监控。",
     *                 "iconCode":"icon-gnzhiliupaihuaijiance",
     *                 "iconUrl":null,
     *                 "iconColour":"#1ECAF7",
     *                 "createDate":null,
     *                 "updateDate":null,
     *                 "url":null,
     *                 "deleteFlag":null,
     *                 "status":null,
     *                 "desImageUrl":null,
     *                 "desVideoUrl":null,
     *                 "functionLabel":"测试",
     *                 "labelList":[
     *                     "测试"
     *                 ],
     *                 "desImageUrlList":null,
     *                 "desVideoUrlList":null,
     *                 "algorithmList":null
     *             }
     *         ]
     *     }
     * }
     */
    @PostMapping("/video/algorithm/getFunction")
    public Result<Map> getFunction(String tenantId){
        return iAlgorithmInfoServiceMigrate.getFunction(tenantId);
    }
    /**
     * 获取功能详情
     * @apiNote 获取功能详情
     * @apiVersion 3.40
     * @tag 三、设备能力
     * @requestDemo
     * "389"
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694679668050,
     *     "data":{
     *         "id":389,
     *         "functionType":"10",
     *         "functionTypeName":"环境感知类监控",
     *         "subFunctionType":"1010",
     *         "subFunctionTypeName":"环境感知监控",
     *         "functionCode":"1010108",
     *         "functionCodeName":"湿地植被监控",
     *         "functionDetail":"对湿地及湿地常见竹林、芦苇、灌丛等常见植物的面积进行周期性监测，对面积变化进行识别",
     *         "iconCode":"icon-gnshidizhibeizhence",
     *         "iconUrl":null,
     *         "iconColour":"#07CF32",
     *         "createDate":null,
     *         "updateDate":null,
     *         "url":null,
     *         "deleteFlag":null,
     *         "status":null,
     *         "desImageUrl":null,
     *         "desVideoUrl":null,
     *         "functionLabel":null,
     *         "labelList":null,
     *         "desImageUrlList":null,
     *         "desVideoUrlList":null,
     *         "algorithmList":null
     *     }
     * }
     */
    @PostMapping("/video/algorithm/getFunctionDetail/{id}")
    public Result<FunctionTypeVO> getFunctionDetail(@PathVariable("id") String id){
        return iAlgorithmInfoServiceMigrate.getFunctionDetail(id);
    }

}
