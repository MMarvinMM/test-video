package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.AlgorithmInfoSyncFallbackFactory;
import com.chinatower.device.entity.dto.AlgoCommonReq;
import com.chinatower.device.entity.dto.AlgoInfoReq;
import com.chinatower.device.entity.dto.AlgoTypeReq;
import com.chinatower.device.entity.dto.FuncTypeReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "IAlgorithmInfoSyncService", value = ServiceNameConstants.DEVICE_SERVICE_SYNC, fallbackFactory = AlgorithmInfoSyncFallbackFactory.class)
public interface IAlgorithmInfoSyncService {
    /**
     * 算法基本信息推送
     * @apiNote 算法基本信息推送
     * @apiVersion 3.5
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
    Result<String> pushAlgorithmInfo(@RequestBody AlgoCommonReq<AlgoInfoReq> req);
    /**
     * 算法类别信息推送
     * @apiNote 算法类别信息推送
     * @apiVersion 3.6
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
    Result<String> pushAlgorithmType(@RequestBody AlgoCommonReq<AlgoTypeReq> req);
    /**
     * 功能类别信息推送
     * @apiNote 功能类别信息推送
     * @apiVersion 3.7
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
    Result<String> pushFunctionType(@RequestBody AlgoCommonReq<FuncTypeReq> req);
}
