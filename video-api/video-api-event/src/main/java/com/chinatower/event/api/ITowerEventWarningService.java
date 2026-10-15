package com.chinatower.event.api;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.common.core.vo.TowerCommunicationVo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.factory.TowerEventWarningFallbackFactory;
import com.chinatower.event.api.model.ApiInterceptOrderEntity;
import com.chinatower.event.api.model.ApiOperateOrderEntity;
import com.chinatower.event.api.model.ApiWarningOrderEntity;
import com.chinatower.event.api.model.SatelliteAlarmApiReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.AiJudgeAlarmVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "towerEventWarningService", value = ServiceNameConstants.TTVIEW_EVENT_SERVICE, fallbackFactory = TowerEventWarningFallbackFactory.class)

public interface ITowerEventWarningService {

    /**
     * 查询告警事件列表(下载图片准用)
     * @apiNote 铁塔_查询告警事件列表(下载图片准用)
     * @apiVersion 1.9
     * @tag 一、告警事件能力
     * @requestDemo {"downLoadType":"0","orderIdDownLoadType":"1","downLoadOrderDTO":{"cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":6,"confidenceLevelMin":"","confidenceLevelMax":"",
     * "minChar":"100","maxChar":"100","alarmTimeStart":"2023-7-19 00:00:00","alarmTimeEnd":"2023-8-18 23:59:59","searchType":"0","areaCodeList":[],"deviceCode":[],"lableIdList":[]},
     * "warningOrderIds":["2023081812203737269749028","2023081812205121869749029","2023081812208775269749019","2023081812201469369748990","2023081812201469369748993","2023081812112379569748975"],"name":"中国铁塔"}
     * @responseDemo {"total": 985903,"rows": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052","latitude": "28.049137465573217"}],"code": 200}
     * 返回参数data下字段解析：
     *           alarmBody: 告警内容
     *           warningTypeName: 告警类型名称
     *           orderId: 告警单ID(yyyyMM+租户id+)
     *           groupId: 小组id，组织编码
     *           latitude: 纬度
     *           flowNodeTime: 当前环节开始时间 yyyy-MM-dd HH:mm:ss
     *           icon: 图标
     *           isCollection: 是否收藏 1:收藏 0：未收藏
     *           description: 事件描述
     *           orderStatus: 事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           cityId: 地市编码
     *           findMobile: 事件发现人电话
     *           duration: 处理时长
     *           modifierDate: 最后更新时间 yyyy-MM-dd HH:mm:ss
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           orderStatusName: 工单状态名称
     *           cityName: 地市名称
     *           tenantName: 租户名称
     *           processId: 处理人
     *           countyId: 区县编码
     *           enableAcceptanceMode: 受理状态
     *           flowId: 流程ID
     *           createDate: 创建时间 yyyy-MM-dd HH:mm:ss
     *           longitude: 经度
     *           industryName: 行业名称
     *           isHandleDisplay: 是否显示督办提示图标（0-否，1-是）
     *           warningSourceName:  告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           address: 详细地址
     *           orgName: 组织名称
     *           alarmLableRelDTOList: 告警标签集合
     *           fileImgUrlIcon: 框图
     *           findName: 事件发现人姓名
     *           importanceLevel: 重要等级
     *           alarmTime: 形成告警时间 yyyy-MM-dd HH:mm:ss
     *           leaderNum: 领导批示数量
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省份编码
     *           appVerCode: 版本
     *           linkId: 环节ID
     *           ifTimeout: 是否超时 0否1是
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户ID
     *           importanceLevelName: 重要等级名称
     *           provinceName: 省份名称
     *           reciveTime: 接收时间 yyyy-MM-dd HH:mm:ss
     *           tradeId: 跟踪ID
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/order/getOrderListImgs")
    TableDataInfo getOrderListImgs(@RequestBody AlarmDownloadReq dto);

    /**
     * 查询告警事件列表
     * @apiNote 铁塔_查询告警事件列表接口(提供给君南)
     * @apiVersion 1.10
     * @tag 一、告警事件能力
     * @requestDemo {"orderId":"2023081612006598768857108","pageNum":1,"pageSize":6,"warningSource":"8","isCollection":"1","orderStatusList":["1", "5", "4"],"alarmTimeStart":"2023-7-17 00:00:00","alarmTimeEnd":"2023-8-16 23:59:59","warningTypeIdList":
     * ["1010105", "1010104", "1010103"],"searchType":"0","provinceId":"110000","cityId":"110100","countyId":"110101","isSecondJudge":"1","secondJudgeResult":"1","importanceLevel":"1","deviceCode":[],"lableIdList":[]}
     * @responseDemo {"total":32607,"rows":[{"warningTypeName":"水位告警","orderId":"202412241210631569922857","flowNodeTime":"2024-12-24 09:39:22","cityId":"110100","alarmCode":"202412241210631569922857","deviceName":"（玉渊潭）河道水位计3","orderStatusName":"待研判","emergencyLevel":"01","enableAcceptanceMode":"0","longitude":"116.323330","isHandleDisplay":"0","orgName":"铁塔智联技术有限公司贵州省分公司-水库智保版本-1.0","alarmLableRelDTOList":[],"importanceLevel":"4","showTime":"剩余0天 00:29:53","ifEdit":0,"provinceId":"110000","appVerCode":"1.0","ifTimeout":0,"provinceName":"北京市","alarmBody":"物联设备告警","latitude":"39.897651","icon":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/pre/1.0/f0e6e0ff7f7231a6884fbd7ce65419f7/c33257f945e81c1408efe3e467824c79/ed41353cd9ee4d53b26485dc4c45d01f.svg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1735007968&Signature=pf0Lrz0D7IYa910ztiWQ1iJzAiA%3D","isCollection":"0","orderStatus":"1","duration":"0小时0分6秒","modifierDate":"2024-12-24 09:39:22","warningSource":"8","cityName":"北京市","tenantName":"铁塔智联技术有限公司贵州省分公司","processId":"8af1b84b35214c26a59e80eb44e7b614","countyId":"110108","emergencyLevelName":"一般","flowId":"1001","channelCode":"","createDate":"2024-12-24 09:39:22","industryName":"河湖智保","warningSourceName":"物联告警","address":"玉渊潭公园南","extraField":"{\\"monitorsList\\":[{\\"deviceCode\\":\\"xcsw-ncqq-7391-05\\",\\"monitorList\\":[{\\"factorCode\\":\\"waterLevel\\",\\"factorName\\":\\"水位\\",\\"factorUnit\\":\\"米\\",\\"factorValue\\":\\"0.94\\",\\"highlight\\":true}],\\"monitorTime\\":\\"2024-12-24 09:39:22\\"}],\\"deviceInfo\\":[{\\"iotDeviceCode\\":\\"xcsw-ncqq-7391-05\\"}],\\"monitors\\":[{\\"alarmType\\":\\"WLGJ-SWGJ\\",\\"monitorCode\\":\\"waterLevel\\",\\"monitorName\\":\\"水位\\",\\"monitorValue\\":\\"0.94\\",\\"newMonitors\\":[{\\"itemCode\\":\\"waterLevel\\",\\"itemName\\":\\"监测指标编码\\"},{\\"itemCode\\":\\"水位 (米)\\",\\"itemName\\":\\"监测指标\\"},{\\"itemCode\\":\\"0.94\\",\\"itemName\\":\\"监测值\\"},{\\"itemCode\\":\\"0<\\",\\"itemName\\":\\"告警阈值\\"}],\\"remark\\":\\"87\\",\\"thresholdValue\\":\\"0<\\",\\"warningOrderId\\":\\"202412241210631569922857\\"}]}","alarmTime":"2024-12-24 09:39:22","leaderNum":0,"deviceCode":"xcsw-ncqq-7391-05","timeLimit":1800,"linkId":"1","warningTypeId":"WLGJ-SWGJ","tenantId":"121063156","importanceLevelName":"四级","reciveTime":"2024-12-24 09:39:22","tradeId":"200001"}],"code":200}
     *返回参数data下字段解析：
     *      WarningOrderId：告警事件ID
     *      orderId：告警事件ID
     *      warningTypeName：告警类型名称
     *      flowNodeTime：当前环节开始时间
     *      cityId：市Id
     *      alarmCode：告警编码
     *      deviceName：设备名称
     *      orderStatusName：告警状态名称
     *      isHandleDisplay：是否显示督办提示图标（0-否，1-是）
     *      orgName：所属组织名称
     *      alarmLableRelDTOList：告警标签集合
     *      importanceLevel：告警重要等级
     *      showTime：超期时长
     *      ifEdit：是否可编辑  0否 1是
     *      appVerCode：行业版本编码
     *      ifTimeout：超期  1已超期 2未超期
     *      provinceName：省名称
     *      icon：图片地址
     *      isCollection：是否收藏 1:收藏 0：未收藏
     *      duration：处理时长
     *      modifierDate：修改时间
     *      cityName：市名称
     *      processId：处理人
     *      countyId：区Id
     *      emergencyLevelName：告警等级名称
     *      flowId：流程Id
     *      channelCode：通道编码
     *      createDate：创建时间
     *      industryName：行业名称
     *      warningSourceName：告警来源名称
     *      extraField：透传字段
     *      leaderNum：领导批示数量
     *      timeLimit：完成时限;单位：分钟
     *      linkId：当前环节ID
     *      importanceLevelName：重要等级名称
     *      reciveTime：接收时间
     *      tradeId：行业ID
     *	    warningSource：告警来源: 1-AI上报； 2-人工上报；3-一键告警
     *	    warningTypeId：告警类型编码
     *	    alarmTime：形成告警时间
     *	    tenantId：租户ID
     *	    tenantName：租户名称
     *	    longitude：经度
     *	    latitude：维度
     *	    alarmBody：告警内容
     *	    remark：备注
     *	    description ：事件描述
     *	    address：详细地址
     *	    emergencyLevel：事件等级
     *	    orderStatus：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *	    findName：事件发现人姓名
     *	    findMobile：事件发现人电话
     *	    imgUrl：原图
     *	    deviceCode：设备编码
     *	    channelCode：通道编码
     *	    judgeResult：研判结果
     *	    realLinkType：环节类型(1研判2调度4处置5核实6办结7开始)
     *	    enableAcceptanceMode：是否开始模式及受理状态（0：不开启 1：开启（待受理）2：已受理 3：受理完成）
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/order/getOrderList")
    TableDataInfo getOrderList(@RequestBody TowerAlarmOrderDTO dto);

    /**
     * 查询运营研判事件列表(提供给君南)
     * @apiNote 铁塔_查询运营研判事件列表(提供给君南)
     * @apiVersion 1.11
     * @tag 一、告警事件能力
     * @requestDemo {"operateType":"1","orderId":null,"alarmBody":null,"warningTypeIdList":null,"alarmTimeStart":"2023-08-01 00:00:00","alarmTimeEnd":"2023-08-22 23:59:59","cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":10,"orderStatus":"0",
     * "judgeResult":null,"source":null,"confidenceLevelMin":"","confidenceLevelMax":"","minChar":"100","maxChar":"100","judgeUserList":null,"isSecondJudge":null,"secondJudgeResult":null,"warningSource":null,"custUser":null,"algorithmCodeList":[],"deviceCode":[],"tenantList":[]}
     * @responseDemo {"total": 985903,"rows": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","alarmTime":"2023-08-22 14:58:07","address":"江西省赣州市南康区","warningTypeId":"1717122","warningTypeName": "农田人工湖泊监控告警",
     * "compConfidenceLevel": "0.8625","operateType":"1","orderStatus":"1","orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","warningSource":"1","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","importanceLevelName":"一级","confidenceLevel": "0.8625",
     * "emergencyLevel": "01","emergencyLevelName": "一般","provinceId":"360000","provinceName":"江西省","cityId":"360700","cityName":"赣州市","imgs":[],"source":"0","presetId":"117301","presetName":"预置位2","judgeResult":"3","judgeResultName":"待研判"}],"code": 200}
     * 返回参数data下字段解析：
     *           confidenceLevel: 置信度
     *           warningTypeName: 告警类型名称
     *           orderId: 告警单ID
     *           cityId: 地市编码
     *           judgeResult: 研判结果
     *           alarmCode: 告警编码
     *           deviceName: 设备名称
     *           industryCode: 行业编码
     *           deleteFlag: 删除标识
     *           orderStatusName: 工单状态名称
     *           emergencyLevel: 告警等级名称
     *           relativePathThem: 专题图
     *           longitude: 经度
     *           imgs: 图片地址
     *           townName: 乡镇名称
     *           alarmNode: 告警节点
     *           importanceLevel: 重要等级
     *           operateType: 运营研判标识
     *           judgeTime: 研判时间
     *           provinceId: 省编码
     *           appVerCode: 版本
     *           operationDept: 研判组织
     *           provinceName: 省名称
     *           alarmBody: 告警内容
     *           longitudeAndLatitude: 经纬度
     *           latitude: 纬度
     *           icon: 图标
     *           orderStatus: 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           warningOrderId: 告警事件ID
     *           algorithmCode: 算法编码
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           cityName: 地市名称
     *           tenantName: 租户名称
     *           countyId: 县编码
     *           emergencyLevelName: 告警等级名称
     *           channelCode: 通道编码
     *           countyName: 县名称
     *           editFlag: 能否编辑标识
     *           industryName: 行业名称
     *           warningSourceName: 告警来源名称
     *           address: 地址
     *           extraField: 扩展字段
     *           alarmTime: 形成告警时间 yyyy-MM-dd HH:mm:ss
     *           deviceCode: 设备编码
     *           fileImgUrlSrc: 原图
     *           judgeResultName: 研判结果
     *           imgUrl: 图片地址
     *           operationNode: 研判节点
     *           operationNodeName: 研判节点名称
     *           warningTypeId: 告警类型
     *           tenantId: 租户编码
     *           channelName: 通道名称
     *           importanceLevelName: 重要等级名称
     *           relativePathMir: 红外图
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping("/alarm/order/operate/getOrderListForOperate")
    TableDataInfo getOrderListForOperate(@RequestBody AlarmOperateDTO dto);

    /**
     * 查询运营研判事件列表
     */
    @PostMapping("/alarm/order/operate/getOrderListForOperateApi")
    TableDataInfo getOrderListForOperateApi(@RequestBody AlarmOperateDTO dto);

    /**
     * AI智能研判事件数量
     * @param dto
     * @return
     */
    @PostMapping("/alarm/order/operate/getOrderCountForOperate")
    Result getOrderCountForOperate(@RequestBody AlarmOperateDTO dto);

    /**
     * AI智能研判并保存入库
     * @param alarmVO
     * @return
     */
    @PostMapping("/alarm/order/operate/aiJudgeAlarmAndSave")
    Result<JSONObject> aiJudgeAlarmAndSave(@RequestBody ReceiveAlarmVO alarmVO);

    /**
     * 查询AI智能研判结果列表
     * @param dto
     * @return
     */
    @PostMapping("/alarm/order/operate/selectAiJudgeResultList")
    Result<List<AiJudgeAlarmVO>> selectAiJudgeResultList(@RequestBody AiJudgeResultDTO dto);
    /**
     * 查询回收站告警事件列表(下载图片准用)
     * @apiNote 铁塔_查询回收站告警事件列表(下载图片准用)
     * @apiVersion 1.12
     * @tag 一、告警事件能力
     * @requestDemo {"downLoadType":"0","orderIdDownLoadType":"2","downLoadWarningInterceptDTO":{"cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":6,"confidenceLevelMin":"","confidenceLevelMax":"",
     * "minChar":"100","maxChar":"100","alarmTimeStart":"2023-7-19 00:00:00","alarmTimeEnd":"2023-8-18 23:59:59","searchType":"0","areaCodeList":[],"deviceCode":[],"lableIdList":[]},
     * "warningOrderIds":["2023081812203737269749028","2023081812205121869749029","2023081812208775269749019","2023081812201469369748990","2023081812201469369748993","2023081812112379569748975"]}
     * @responseDemo {"total": 985903,"rows": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625",
     * "cityCode":"431200","cityName":"怀化市","provinceCode":"430000","provinceName":"湖南省","warningTypeId":"1212122","warningTypeName":"非法垂钓监控告警","intercept":"1","interceptName":"置信度压制",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","deviceCode":"43128100831327000290","deviceName":"YZHJ0018洪江市黔城镇柳溪村","channelCode":"43128100011130000116",
     * "channelName":"洪江市黔城镇柳溪村_0","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052","latitude": "28.049137465573217"}],"code": 200}
     *返回参数data下字段解析：
     *           confidenceLevel: 置信度
     *           gridName: 所属网格名称
     *           warningTypeName: 告警类型名称
     *           modelId: 摄像机厂家
     *           confidenceLevelMin: 最小置信度
     *           alarmCode: 告警编码
     *           deviceName: 设备名称
     *           industryCode: 行业编码
     *           deleteFlag: 删除标识 0正常 1删除
     *           ptzZ: Z位置
     *           emergencyLevel: 告警等级
     *           secondJudgeResult: 二次研判结果
     *           longitude: 经度
     *           height: 设备挂高，单位：米
     *           orgName: 展示组织名称
     *           importanceLevel: 重要等级
     *           ptzT: T位置
     *           ifEdit: 是否可编辑  0否 1是
     *           ptzP: P位置
     *           appVerCode: 行业版本编码
     *           confidenceLevelMax: 最大置信度
     *           ifTimeout: 是否超时 0否1是
     *           intercept: 告警压制
     *           gridId: 网格ID
     *           provinceName: 所在省名称
     *           presetId: 预置位id
     *           alarmBody: 告警内容
     *           msgType: 消息类型
     *           videoLatitude: 设备纬度
     *           cityCode: 地市编码
     *           latitude: 纬度
     *           icon: 图标
     *           warningOrderId: 事件ID
     *           algorithmCode: 算法编号
     *           modifierDate: 修改时间
     *           warningSource: 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     *           noticeResource: 通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.
     *           cityName: 市名称
     *           tenantName: 租户名称
     *           videoUrl: 视频路径
     *           horiAzimuthAngle: 水平方位角
     *           interceptName: 告警压制原因
     *           channelCode: 摄像机通道编码
     *           countyName: 区县名称
     *           industryName: 行业应用名称
     *           warningSourceName: 告警来源名称
     *           address: 地址
     *           extraField: 行业扩展字段
     *           fileImgUrlIcon: 框图
     *           videoLongitude: 摄像机经度
     *           provinceCode: 省编码
     *           isSecondJudge: 是否二次研判
     *           alarmTime: 形成告警时间
     *           deviceCode: 设备编码
     *           imgUrl: 原图
     *           modelName: 设备厂家名称
     *           countyCode: 县编码
     *           createTime: 创建时间
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户ID
     *           channelName: 摄像机通道名称
     *           importanceLevelName: 重要等级名称
     *           taskId: 第三方任务ID
     *           tradeId: 行业编码
     *           compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/intercept/getInterceptListImgs")
    TableDataInfo getInterceptListImgs(@RequestBody AlarmDownloadReq dto);

    /**
     * 查询我的收藏告警事件列表(下载图片准用)
     * @apiNote 铁塔_查询我的收藏告警事件列表(下载图片准用)
     * @apiVersion 1.13
     * @tag 一、告警事件能力
     * @requestDemo {"downLoadType":"0","orderIdDownLoadType":"3","downLoadOrderDTO":{"cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":6,"confidenceLevelMin":"","confidenceLevelMax":"",
     * "minChar":"100","maxChar":"100","alarmTimeStart":"2023-7-19 00:00:00","alarmTimeEnd":"2023-8-18 23:59:59","searchType":"0","areaCodeList":[],"deviceCode":[],"lableIdList":[]},
     * "warningOrderIds":["2023081812203737269749028","2023081812205121869749029","2023081812208775269749019","2023081812201469369748990","2023081812201469369748993","2023081812112379569748975"],"name":"中国铁塔"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1689491058930,"data": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625",
     * "cityCode":"431200","cityName":"怀化市","provinceCode":"430000","provinceName":"湖南省","warningTypeId":"1212122","warningTypeName":"非法垂钓监控告警","intercept":"1","interceptName":"置信度压制",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","deviceCode":"43128100831327000290","deviceName":"YZHJ0018洪江市黔城镇柳溪村","channelCode":"43128100011130000116",
     * "channelName":"洪江市黔城镇柳溪村_0","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052","latitude": "28.049137465573217"}]}
     *返回参数data下字段解析：
     *           confidenceLevel: 置信度
     *           gridName: 网格名称
     *           warningTypeName: 告警类型名称
     *           orderId: 事件ID
     *           confidenceLevelMin: 最小置信度
     *           flowNodeTime: 当前环节开始时间
     *           cityId: 地市编码
     *           deviceName: 摄像机设备名称
     *           deleteFlag: 删除标识 0正常 1删除
     *           orderStatusName: 订购状态名称
     *           emergencyLevel: 告警等级ID
     *           enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *           secondJudgeResult: 二次研判结果
     *           longitude: 经度
     *           isHandleDisplay: 是否显示督办提示图标（0-否，1-是）
     *           importanceLevel: 告警重要等级 1:1级,2:2级
     *           showTime: 超期时间
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省份编码
     *           ifTimeout: 是否超时 0否1是
     *           provinceName: 所在省名称
     *           alarmBody: 告警内容
     *           latitude: 纬度
     *           icon: 图标
     *           isCollection: 是否收藏 1:收藏 0：未收藏
     *           orderStatus: 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           duration: 处理时长
     *           warningSource:  告警来源: 1-AI上报； 2-人工上报；3-一键告警
     *           cityName: 市名称
     *           tenantName: 租户名称
     *           processId: 处理人
     *           countyId: 区县编码
     *           emergencyLevelName: 事件等级
     *           channelCode: 通道编码
     *           createDate: 创建时间
     *           warningSourceName: 告警来源名称
     *           address: 详细地址
     *           extraField: 行业扩展字段
     *           fileImgUrlIcon: 框图
     *           isSecondJudge:  是否二次研判
     *           alarmTime: 形成告警时间
     *           leaderNum: 领导批示数量
     *           timeLimitAcceptance: 受限时限
     *           deviceCode: 设备编码
     *           timeLimit: 完成时限;单位：分钟
     *           linkId: 当前环节ID
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户ID
     *           channelName: 摄像机通道名称
     *           importanceLevelName: 重要等级名称
     *           reciveTime: 接收时间 yyyy-MM-dd HH:mm:ss
     *           compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return Result
     */
    @PostMapping(value = "/alarm/order/collection/qryWarningOrderCollectionImgs")
    Result qryWarningOrderCollectionImgs(@RequestBody AlarmDownloadReq dto);

    /**
     * 查询卫星告警二级菜单列表
     * @apiNote 铁塔_查询卫星告警二级菜单列表
     * @apiVersion 1.14
     * @tag 一、告警事件能力
     * @requestDemo {"areaPro":[],"areaCity":[],"userId":"acfe9b1394c0443c84f5a1863d602d03","alarmTimeStart":"2023-08-04","alarmTimeEnd":"2023-08-09","maxChar":"100","minChar":"100"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1689491058930,"data":{"total":19,"list": [{"alarmCode": "FHS20230727214068089","alarmBody": "烟火告警","alarmTime":"2023-07-28 05:40:00","alarmSource": "9","alarmType": "4110100",
     * "channelCode":"11010100001310606880","channelName":"AHI","city":"110100","county":"110118","warningTypeId":"1212122","warningTypeName":"非法垂钓监控告警","intercept":"1","interceptName":"置信度压制",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","deviceCode":"2014-060A","deviceName":"H8","latitude":"40.521304381",
     * "longitude":"116.806130285","province": "110000","pushNum": "0","pushTime": "2023-07-28 06:06:49","warningTypeName": "卫星告警-火点侦测","relativePathPose":"","relativePathThem":"",
     * "relativePathMir": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/prd/baseplatform/ec7c3b0bc5f9b10ff3b4839d88332a78/73a8f27e6b95a13d19ecf3a7374c8129/92c66b9a436e4e7383b07c2f374db767.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1692689689&Signature=IFmGBr8p2mgHgDjmzsZdhTG%2BeHQ%3D"}]}}
     * 返回参数data下字段解析：
     *           alarmBody: 告警内容
     *           warningTypeName: 告警类型名称
     *           city: 地市编码
     *           alarmTime: 告警时间
     *           latitude: 纬度
     *           county: 区编码
     *           pushNum: 推送数量
     *           deviceCode: 设备编码
     *           alarmCode: 告警编码
     *           relativePathPose: 分布图
     *           deviceName: 设备名称
     *           orderStatusName: 订购状态名称
     *           alarmType: 告警类型
     *           province: 省编码
     *           warningTypeId: 告警类型编码
     *           intercept: 告警压制
     *           alarmSource: 告警来源 1-算法AI告警；2-人工上报；3-一键告警；4、摄像机算法告警 5-车辆卡口； 6-人脸卡口；7-行业上报；8-物联设备告警；9-卫星告警；10-雷达告警
     *           channelName: 摄像机通道名称
     *           interceptName: 告警压制原因
     *           pushTime: 推送时间
     *           relativePathThem: 专题图
     *           channelCode: 摄像机通道编码
     *           longitude: 经度
     *           relativePathMir: 红外图
     * @param dto 入参
     * @return Result
     */
    @PostMapping(value = "/alarm/satellite/getApiOrderList")
    Result getSatelliteOrderList(@RequestBody SatelliteAlarmApiReq dto);


    /**
     * 查询告警图层
     * @apiNote 铁塔_查询告警图层接口
     * @apiVersion 1.15
     * @tag 一、告警事件能力
     * @requestDemo {"alarmTimeStart":"2023-7-18 00:00:00","alarmTimeEnd":"2023-8-17 23:59:59","pageNum":1,"pageSize":6,"provinceId":"110000","cityId":"110100","countyId":"110108",
     * "longitude":"116.30576686828336","latitude":"39.95981617277608","roleFlag":"8","radius":"3000","warningSourceList":["8","10"],"orderStatusList":["1","3"],"warningTypeList":["1212100","1212111"]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1651049172858,"data":[{"warningOrderId":"202308141210633511647490","warningSource": "1","warningTypeId": "1212102","alarmTime": "2022-03-24 04:37:14","tenantId": "TEN100001","tenantName": "算法仓02","provinceId":"110000","cityId":"110100",
     * "countyId":"110101","longitude": "1143.794872","latitude": "24.192697","alarmBody": "人员闯入监控告警", "orderStatus": "2","orderStatusName":"待研判","address": "北京市朝阳区常营中街与常通路十字路口西北角1111","processId":"18a550a3d40742339ca56a15908ae493,...","provinceName":"青海省","flowNodeTime":"2023-08-17 10:41:10",
     * "videoUrl": "http://120.46.145.37:10120//ThirdAlarm/pic//KLYR-1212102001_0932e8346e7045799ea114b630f0aedd45c10f4ad0534d92b8988652a05de5b0_01_880.jpg","gridId":"","gridName":"","linkId":"1","flowId":"1001","deviceCode":"1234","deviceName":"111",
     * "alarmCode":"111","channelCode":"11010800831310000008","channelName","铁塔产业园-11号楼-大华243_0","warningTypeName": "人员闯入监控告警","emergencyLevelName": "一般","orderStatusName": "待调度","warningSourceName": "AI上报","judgeResult": "2","realLinkType":"2","confidenceLevel":"0.6894647"}]}
     * 返回参数data下字段解析：
     *           alarmBody: 告警标题
     *           distance: 用户距事发点距离
     *           latitude: 纬度
     *           flowNodeTime: 当前环节开始时间
     *           orderStatus: 告警状态 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           warningOrderId: 事件ID
     *           cityId: 地市编码
     *           deviceName: 设备名称
     *           warningSource: 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     *           cityName: 市名称
     *           emergencyLevel: 告警等级
     *           tenantName: 租户名称
     *           processId: 处理人
     *           countyId: 县编码
     *           enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *           flowId: 流程ID
     *           channelCode: 通道编码
     *           countyName: 县名称
     *           createDate: 创建时间
     *           longitude: 经度
     *           address: 详细地址
     *           alarmTime: 告警时间
     *           leaderNum: 领导批示数量
     *           deviceCode: 设备编码
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省编码
     *           linkId: 环节ID
     *           checkTime: 研判完成时间
     *           ifTimeout:  是否超时 0否1是
     *           warningTypeId: 告警类型
     *           tenantId: 租户ID
     *           channelName: 摄像机通道名称
     *           gridId: 网格id
     *           provinceName: 省名称
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getWarningOrderLayer")
    public Result getWarningOrderLayer(@RequestBody WarningOrderLayerDTO dto);

    /**
     * 属地化运营研判查询
     */
    @PostMapping(value = "/alarm/order/local/getOperateListApi")
    Result getOperateListApi(@RequestBody ApiOperateOrderEntity dto);

    /**
     * 属地化回收站查询
     */
    @PostMapping(value = "/alarm/order/local/getInterceptListApi")
    Result getInterceptListApi(@RequestBody ApiInterceptOrderEntity dto);

    /**
     * 属地化事件查询
     */
    @PostMapping(value = "/alarm/order/local/getOrderListApi")
    Result getOrderListApi(@RequestBody ApiWarningOrderEntity dto);
    /**
     * 运营研判事件详情
     */
    @PostMapping(value = "/alarm/order/operate/getOrderInfoApi")
    Result getOrderInfoApi(@RequestBody ExportOrderInfoReq dto);

    /**
     * 查询融合通信列表
     * @apiNote 查询融合通信列表
     * @apiVersion 1.16
     * @tag 一、告警事件能力
     * @requestDemo {"pageNum":1,"pageSize":10,"nickName":"","userName":"","dispatchDeskNum":"","mobileTerminalNum":"","updateUser":"","updateTime":"","tenantId":"","updateTimeSort":"","operationNode":"","updateStartTime":"2024-12-01 00:00:00","updateEndTime":"2024-12-31 23:59:59"}
     * @responseDemo {
     *     "total": 3,
     *     "rows": [
     *         {
     *             "creator": null,
     *             "createTime": null,
     *             "createBy": null,
     *             "updateBy": null,
     *             "modifier": null,
     *             "updateTime": "2024-12-12 11:11:36",
     *             "deleteFlag": null,
     *             "tenantId": "120046990",
     *             "userId": "c45c14508ad548caaf44e6971905d103",
     *             "appVerCode": "1.0",
     *             "industryCode": "000000",
     *             "platformCode": null,
     *             "dataScope": null,
     *             "criteriaList": null,
     *             "screenId": null,
     *             "screenType": null,
     *             "pageNum": null,
     *             "pageSize": null,
     *             "resourceProperty": null,
     *             "orderByColumn": null,
     *             "isAsc": null,
     *             "communicationId": "154",
     *             "nickName": "李明",
     *             "userName": "kui503",
     *             "dispatchDeskNum": "75464364",
     *             "mobileTerminalNum": null,
     *             "updateUser": "weichuang001",
     *             "updateTimeSort": null,
     *             "updateStartTime": null,
     *             "updateEndTime": null,
     *             "avatar": null,
     *             "orderBy": ""
     *         }
     *     ],
     *     "code": 200,
     *     "msg": "查询成功"
     * }
     * 返回参数rows下字段解析：
     *            creator: 创建者用户名（如 "junnan01"）
     *            createTime: 创建时间（如 "2022-09-01 17:26:58"）
     *            createBy: 创建者
     *            updateBy: 修改者
     *            modifier: 修改者的唯一标识（如 "b1921ab47c25491da9e45d6a7ecaffda"）
     *            updateTime: 修改时间（如 "2023-08-15 15:35:42"）
     *            deleteFlag: 删除标志（0 表示未删除，1 表示已删除）
     *            tenantId: 租户 ID（如 "122035807"）
     *            userId: 用户 ID（可能为空）
     *            appVerCode: 行业应用版本（如 "1.0"）
     *            industryCode: 行业代码（如 "000000"）
     *            platformCode: 节点编码
     *            dataScope: 数据权限
     *            criteriaList: 数据权限mongo条件
     *            screenId: 大屏id
     *            screenType: 大屏类型
     *            pageNum: 页码（1）
     *            pageSize: 页大小（10）
     *            resourceProperty: 资源属性
     *            orderByColumn：排序列
     *            isAsc: 排序规则
     *            communicationId: 融合通信id
     *            nickName: 昵称
     *            userName: 用户名
     *            dispatchDeskNum: 调度台号码
     *            mobileTerminalNum: 移动端号码
     *            updateUser: 修改人
     *            updateTimeSort: 修改时间排序
     *            updateStartTime: 修改开始时间
     *            updateEndTime: 修改结束时间
     *            avatar: 图像
     *            orderBy: 排序
     * @param communicationVo
     * @return TableDataInfo
     */
    @PostMapping("/communication/queryList")
    TableDataInfo queryList(@RequestBody TowerCommunicationVo communicationVo);

}
