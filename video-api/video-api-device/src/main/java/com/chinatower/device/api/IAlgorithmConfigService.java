package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.factory.ChannelInfoFallbackFactory;
import com.chinatower.platform.entity.device.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "algorithmConfigService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = ChannelInfoFallbackFactory.class)
public interface IAlgorithmConfigService {


    /**
     * 新增算法配置
     * @apiNote 新增算法配置
     * @apiVersion 3.9
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800011327000012",
     *     "deviceName":"实验室啊-紫光-国标231",
     *     "channelCode":"91724817445122315474",
     *     "channelName":"实验室啊-紫光-国标231_122",
     *     "customerCode":"121062021",
     *     "positions":[
     *         {
     *             "presetId":"",
     *             "posType":1,
     *             "presetName":"",
     *             "algorithms":[
     *                 {
     *                     "algorithmCode":"DLSZ-1212100001",
     *                     "algorithmName":"人脸识别",
     *                     "img":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/f1679894a06c210c94fde764c1770c89/b151308cd48d40b33c5a0ecfe8903d69/3d7fd896f6394c7a87c7c47da05c597a.png",
     *                     "optType":1,
     *                     "monitorArea":[
     *                         {
     *                             "monitorType":0,
     *                             "moniPointList":[
     *                                 {
     *                                     "percentX":"0.001",
     *                                     "percentY":"0.001",
     *                                     "monitorPointId":1
     *                                 },
     *                                 {
     *                                     "percentX":"0.999",
     *                                     "percentY":"0.001",
     *                                     "monitorPointId":2
     *                                 },
     *                                 {
     *                                     "percentX":"0.999",
     *                                     "percentY":"0.999",
     *                                     "monitorPointId":3
     *                                 },
     *                                 {
     *                                     "percentX":"0.001",
     *                                     "percentY":"0.999",
     *                                     "monitorPointId":4
     *                                 }
     *                             ],
     *                             "monitorAreaId":"16946760199529992147998531"
     *                         }
     *                     ],
     *                     "modelConfigList":[
     *
     *                     ],
     *                     "tripThreadList":[
     *
     *                     ],
     *                     "polygonAreaList":[
     *
     *                     ],
     *                     "analysisPeriodList":[
     *
     *                     ]
     *                 }
     *             ]
     *         }
     *     ],
     *     "callSourceFrom":"算法配置"
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "算法配置新增成功!",
     *     "time": 1694676021001
     * }
     */
    @ApiOperation("算法配置新增")
    @PostMapping("/video/algorithm/save")
    Result save(@RequestBody AlgorithmListVO algorithmListVO);

    /**
     * 算法配置查看
     * @apiNote 算法配置查看
     * @apiVersion 3.10
     * @tag 三、设备能力
     * @requestDemo
     * {"configId":"57f9075d13d2438cb6277d08bcf16d25"}
     * @responseDemo
     * configId：算法任务编码
     * deviceCode：摄像机编码
     * channelCode：通道编码
     * posType：位置类型：1通道，2预置位
     * presetId：预置位编码
     * tenantId：客户编码
     * createTime：创建时间
     * deviceName：摄像机名称
     * channelName：通道名称
     * presetName：预置位名称
     * points：点位信息
     * img：图片url
     * algorithmCode：算法编码
     * algorithmName：算法名称
     * taskId：算法仓任务编码
     * status：算法任务状态：0:启用 1:停用
     * algorithmStatus：算法仓任务状态：1成功；2失败；3处理中；4下发中
     * thresh：置信度
     * modelConfig：高级参数
     * tripThread：伴线点位
     * analysisPeriod：算法分析时段
     * taskType：位置类型：1通道，2预置位
     *
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694676176276,
     *     "data":[
     *         {
     *             "configId":"57f9075d13d2438cb6277d08bcf16d25",
     *             "deviceCode":"11010800011327000012",
     *             "channelCode":"91724817445122315474",
     *             "posType":1,
     *             "presetId":null,
     *             "tenantId":"121062021",
     *             "createTime":"2023-09-14 15:20:20",
     *             "deviceName":"实验室啊-紫光-国标231",
     *             "channelName":"实验室啊-紫光-国标231_122",
     *             "presetName":"",
     *             "points":"[{"monitorAreaId":"16946760199529992147998531","moniPointList":[{"monitorPointId":"1","percentX":0.001,"percentY":0.001},{"monitorPointId":"2","percentX":0.999,"percentY":0.001},{"monitorPointId":"3","percentX":0.999,"percentY":0.999},{"monitorPointId":"4","percentX":0.001,"percentY":0.999}],"monitorType":0,"longitude":null,"latitude":null}]",
     *             "img":null,
     *             "algorithmCode":"DLSZ-1212100001",
     *             "algorithmName":"人脸识别",
     *             "tenantName":null,
     *             "taskId":"1054f073a390438ea27cac92197bad23",
     *             "status":"0",
     *             "algorithmStatus":"2",
     *             "thresh":null,
     *             "modelConfig":"[]",
     *             "tripThread":"[]",
     *             "analysisPeriod":null,
     *             "taskType":"1"
     *         }
     *     ]
     * }
     */
    @PostMapping("/video/algorithm/queryById")
    @ApiOperation("算法配置查看")
    Result<List<AlgorithmConfigVO>> queryById(@RequestBody AlgorithmConfigDTO algorithmConfigDTO);


    /**
     * 算法配置编辑
     * @apiNote 算法配置编辑
     * @apiVersion 3.11
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800011327000012",
     *     "deviceName":"实验室啊-紫光-国标231",
     *     "channelCode":"91724817445122315474",
     *     "channelName":"实验室啊-紫光-国标231_122",
     *     "customerCode":"121062021",
     *     "configId":"57f9075d13d2438cb6277d08bcf16d25",
     *     "presetId":null,
     *     "posType":1,
     *     "presetName":"",
     *     "preset":null,
     *     "algorithms":[
     *         {
     *             "configId":"57f9075d13d2438cb6277d08bcf16d25",
     *             "algorithmCode":"DLSZ-1212100001",
     *             "algorithmName":"人脸识别",
     *             "img":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/f1679894a06c210c94fde764c1770c89/b151308cd48d40b33c5a0ecfe8903d69/3d7fd896f6394c7a87c7c47da05c597a.png",
     *             "optType":1,
     *             "monitorArea":[
     *                 {
     *                     "monitorType":0,
     *                     "moniPointList":[
     *                         {
     *                             "percentX":"0.001",
     *                             "percentY":"0.001",
     *                             "monitorPointId":1
     *                         },
     *                         {
     *                             "percentX":"0.999",
     *                             "percentY":"0.001",
     *                             "monitorPointId":2
     *                         },
     *                         {
     *                             "percentX":"0.999",
     *                             "percentY":"0.999",
     *                             "monitorPointId":3
     *                         },
     *                         {
     *                             "percentX":"0.001",
     *                             "percentY":"0.999",
     *                             "monitorPointId":4
     *                         }
     *                     ],
     *                     "monitorAreaId":"16946760199529992147998531"
     *                 }
     *             ],
     *             "modelConfigList":[
     *
     *             ],
     *             "tripThreadList":[
     *
     *             ],
     *             "polygonAreaList":[
     *
     *             ],
     *             "analysisPeriodList":[
     *
     *             ]
     *         }
     *     ],
     *     "callSourceFrom":"算法配置"
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "算法配置编辑成功!",
     *     "time": 1694676352299
     * }
     */
    @PostMapping("/video/algorithm/update")
    @ApiOperation("算法配置编辑")
    Result update(@RequestBody AlgorithmConfigDTO algorithmConfigDTO);


    /**
     * 算法配置启用
     * @apiNote 算法配置启用
     * @apiVersion 3.12
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "configIdList":[
     *         "57f9075d13d2438cb6277d08bcf16d25"
     *     ],
     *     "singleFlag":"1"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"算法配置启用成功!",
     *     "time":1694677550091
     * }
     */
    @PostMapping("/video/algorithm/enabledStatus")
    @ApiOperation("算法配置启用")
    Result enabledStatus(@RequestBody AlgorithmConfigDTO algorithmConfigDTO);


    /**
     * 算法配置停用
     * @apiNote 算法配置停用
     * @apiVersion 3.13
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "configIdList":[
     *         "57f9075d13d2438cb6277d08bcf16d25"
     *     ],
     *     "singleFlag":"1"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"算法配置停用成功!",
     *     "time":1694677548656
     * }
     */
    @PostMapping("/video/algorithm/disabledStatus")
    @ApiOperation("算法配置停用")
    Result disabledStatus(@RequestBody AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 算法配置删除
     * @apiNote 算法配置删除
     * @apiVersion 3.14
     * @tag 三、设备能力
     * @requestDemo
     * {"configIdList":["57f9075d13d2438cb6277d08bcf16d25"]}
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"算法配置删除成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/algorithm/delete")
    @ApiOperation("算法配置删除")
    Result delete(@RequestBody AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 单查通道下算法配置
     * @apiNote 单查通道下算法配置
     * @apiVersion 3.15
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"14052100831327000005",
     *     "channelCode":"14052100831310000004",
     *     "pageNum":1,
     *     "pageSize":10
     * }
     * @responseDemo
     * configId：算法任务编码
     * deviceCode：摄像机编码
     * deviceName：摄像机名称
     * channelCode：通道编码
     * channelName：通道名称
     * posType：位置类型：1通道，2预置位
     * presetId：预置位编码
     * tenantId：客户编码
     * createTime：创建时间
     * deviceName：摄像机名称
     * channelName：通道名称
     * presetName：预置位名称
     * points：点位信息
     * img：图片url
     * algorithmCode：算法编码
     * algorithmName：算法名称
     * taskId：算法仓任务编码
     * status：算法任务状态：0:启用 1:停用
     * algorithmStatus：算法仓任务状态：1成功；2失败；3处理中；4下发中
     * thresh：置信度
     * modelConfig：高级参数
     * tripThread：伴线点位
     * analysisPeriod：算法分析时段
     * taskType：位置类型：1通道，2预置位
     *
     * {
     *     "total":1,
     *     "rows":[
     *         {
     *             "configId":"06581d3c23854aa5aa6f45741da03246",
     *             "algorithmCode":"ZGTT-1010100001",
     *             "points":"[{"monitorAreaId":"16883744618616662487503715","moniPointList":[{"monitorPointId":"1","percentX":0.001,"percentY":0.001},{"monitorPointId":"2","percentX":0.999,"percentY":0.001},{"monitorPointId":"3","percentX":0.999,"percentY":0.999},{"monitorPointId":"4","percentX":0.001,"percentY":0.999}],"monitorType":0,"longitude":null,"latitude":null}]",
     *             "img":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/pre/baseplatform/ca3ede80c21cd96bba96774974b67185/c62a2753f2ed27278d52fad05cdf819d/6b25115f467d404fa5d4e3bb07ae1f37.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1694678386&Signature=KiIJOI/0Vn2EjqzzZ9n2EZxttEQ%3D",
     *             "channelCode":"14052100831310000004",
     *             "channelName":"沁水嘉峰郭北河道监控_0",
     *             "deviceCode":"14052100831327000005",
     *             "deviceName":"水利河道监控-山西省晋城市沁水嘉峰郭北",
     *             "presetId":"51043",
     *             "presetName":"预置位2大桥",
     *             "algorithmName":"烟火监控（可见光）",
     *             "tenantId":"121062021",
     *             "taskId":"33162b8d8e49471ea2f5b2d74d8c1250",
     *             "status":"0",
     *             "posType":2,
     *             "algorithmStatus":"1",
     *             "updateTime":"2023-07-13 17:33:01",
     *             "createTime":"2023-07-03 16:54:24",
     *             "creator":"lycslhx",
     *             "taskType":"1",
     *             "modelConfig":"[]",
     *             "tripThread":"[]"
     *         }
     *     ],
     *     "code":200
     * }
     */
    @PostMapping("/video/algorithm/queryAlgorithm")
    @ApiOperation("单查通道下算法配置")
    TableDataInfo queryAlgorithm(@RequestBody AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 查询算法状态
     * @apiNote 查询算法状态
     * @apiVersion 3.16
     * @tag 三、设备能力
     * @requestDemo
     * {"taskId":"57f9075d13d2438cb6277d08bcf16d25"}
     * @responseDemo
     * taskId：算法仓任务编码
     * algorithmStatus：算法仓任务状态
     *
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694676176276,
     *     "data":{
     *         "taskId":"57f9075d13d2438cb6277d08bcf16d25",
     *         "algorithmStatus":1
     *     }
     * }
     */
    @PostMapping("/video/algorithm/synchAlgorithmConfigStatus")
    @ApiOperation("查询算法状态")
    Result synchAlgorithmConfigStatus(@RequestBody AlgorithmAreaVO algorithmAreaVO);

    /**
     * 根据预置位编码查询算法任务数量
     * @apiNote 根据预置位编码查询算法任务数量
     * @apiVersion 3.17
     * @tag 三、设备能力
     * @requestDemo
     * {"presetId":53429}
     * @responseDemo
     * 5
     */
    @PostMapping("/video/algorithm/getAlgorithmConfigCount")
    int getAlgorithmConfigCount(@RequestBody DelPresetReq presetDTO);

    /**
     * 根据通道编码获取设备编码
     * @apiNote 根据通道编码获取设备编码
     * @apiVersion 3.18
     * @tag 三、设备能力
     * @requestDemo
     * {"channelCode":"11010800831310000008"}
     * @responseDemo
     * 11010800831327000008
     */
    @PostMapping("/video/algorithm/getDeviceCode")
    String getDeviceCode(@RequestBody PresetDTO presetDTO);

    /**
     * 订购信息整合
     */
    @PostMapping("/algorithm/give/algorithmGiveMergeOrder")
    Result<String> algorithmGiveMergeOrder(@RequestBody AlgorithmGiveMergeOrderVO algorithmGiveMergeOrderVO);

}
