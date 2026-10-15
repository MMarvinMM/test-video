package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.event.entity.dto.ModifyLableDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.factory.TradeWarningOrderFallbackFactory;
import com.chinatower.event.api.model.AiAlgorithmConfigReq;
import com.chinatower.event.api.model.AlarmOrderQueryDTO;
import com.chinatower.event.api.model.CheckAlarmByDeviceVO;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.api.model.receive.MessagePushVo;
import com.chinatower.event.api.model.receive.PushThirdReq;
import com.chinatower.event.api.model.receive.WarningLinkInfoReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "warningOrderService", value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = TradeWarningOrderFallbackFactory.class)

public interface ITradeWarningOrderService {

    /**
     * 查询告警事件列表
     * @apiNote 查询告警事件列表接口
     * @apiVersion 1.17
     * @tag 一、告警事件能力
     * @requestDemo {"orderId":"2023081612006598768857108","pageNum":1,"pageSize":6,"warningSource":"8","isCollection":"1","orderStatusList":["1", "5", "4"],"alarmTimeStart":"2023-7-17 00:00:00","alarmTimeEnd":"2023-8-16 23:59:59","warningTypeIdList":
     * ["1010105", "1010104", "1010103"],"searchType":"0","provinceId":"110000","cityId":"110100","countyId":"110101","isSecondJudge":"1","secondJudgeResult":"1","importanceLevel":"1","deviceCode":[],"lableIdList":[]}
     * @responseDemo {"total": 985903,"rows": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052","latitude": "28.049137465573217"}],"code": 200}
     * 返回参数data下字段解析：
     *           confidenceLevel: 置信度
     *           gridName: 网格名称
     *           realLinkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     *           warningTypeName: 告警类型名称
     *           orderId: 告警单ID(yyyyMM+租户id+)
     *           flowNodeTime: 节点时间
     *           cityId: 地市编码
     *           deviceName: 设备名称
     *           industryCode: 行业编码
     *           deleteFlag: 删除标识 0正常 1删除
     *           orderStatusName: 订购状态名称
     *           emergencyLevel: 事件等级，关联枚举值表
     *           enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *           keeperName: 网格员名称
     *           longitude: 经度
     *           isHandleDisplay: 是否显示督办提示图标（0-否，1-是）
     *           orgName: 所属组织名称
     *           alarmLableRelDTOList: 告警标签集合
     *           importanceLevel: 告警重要等级 1:1级,2:2级
     *           showTime: 超期时长
     *           ifEdit:  是否可编辑  0否 1是
     *           provinceId: 省份编码
     *           ifTimeout: 是否超期  1已超期 2未超期
     *           gridId: 网格id
     *           provinceName: 省名称
     *           alarmBody: 事件标题
     *           latitude: 纬度
     *           flowTemplate: 流程模板配置
     *           icon: 图标
     *           isCollection: 是否收藏 1:收藏 0：未收藏
     *           orderStatus: 告警状态 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           beforeDispatchFlag: 是否指定选择人
     *           moreNextFlag: 下个环节是否多个标记为（0：不是  1：是）
     *           duration: 处理时长
     *           modifierDate: 修改时间
     *           warningSource: 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     *           noticeResource: 通知类型 1-调度通知；2-设备端通知
     *           cityName: 地市名称
     *           tenantName: 租户名称
     *           videoUrl: 视频路径
     *           processId: 处理人
     *           countyId: 区县编码
     *           emergencyLevelName: 告警等级名称
     *           flowId: 流程ID
     *           channelCode: 通道编码
     *           countyName: 县名称
     *           createDate: 创建时间 yyyy-MM-dd HH:mm:ss
     *           warningSourceName: 告警来源名称
     *           address: 详细地址
     *           fileImgUrlIcon: 框图
     *           isSecondJudge: 是否二次研判 1-是；0-否
     *           alarmTime: 形成告警时间
     *           leaderNum: 领导批示数量
     *           timeLimitAcceptance: 受限时限
     *           deviceCode: 设备编码
     *           fileImgUrlSrc: 原图
     *           imgUrl: 原图地址
     *           timeLimit: 完成时限;单位：分钟
     *           noticeResourceName: 通知类型名称
     *           linkId: 环节ID
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户id
     *           channelName: 摄像机通道名称
     *           importanceLevelName: 重要等级名称
     *           reciveTime: 接收时间
     *           taskId: 第三方任务ID
     *           compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/orderForTrade/getOrderList")
    TableDataInfo getOrderList(@RequestBody TradeAlarmOrderDTO dto);

    /**
     * 根据网格id查询告警事件列表总数量（提供给君南）
     * @apiNote 查根据网格id查询告警事件列表总数量
     * @apiVersion 1.18
     * @tag 一、告警事件能力
     * @requestDemo {"alarmTimeStart":"2023-7-17 00:00:00","alarmTimeEnd":"2023-8-16 23:59:59","gridId":"202307101414545480"}
     * @responseDemo {"985903"}
     *返回参数data下字段解析：
     *              告警事件列表总数量
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/orderForTrade/getOrderListCount")
    String getOrderListCount(@RequestBody TradeAlarmOrderDTO dto);


    /**
     * 下载图片，查询告警事件列表
     * @apiNote 下载图片，查询告警事件列表接口
     * @apiVersion 1.19
     * @tag 一、告警事件能力
     * @requestDemo {"downLoadType":"0","orderIdDownLoadType":"1","downLoadOrderDTO":{"cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":6,"confidenceLevelMin":"","confidenceLevelMax":"",
     * "minChar":"100","maxChar":"100","alarmTimeStart":"2023-7-19 00:00:00","alarmTimeEnd":"2023-8-18 23:59:59","searchType":"0","areaCodeList":[],"deviceCode":[],"lableIdList":[]},
     * "warningOrderIds":["2023081812203737269749028","2023081812205121869749029","2023081812208775269749019","2023081812201469369748990","2023081812201469369748993","2023081812112379569748975"],"name":"中国铁塔"}
     * @responseDemo {"total": 985903,"rows": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052","latitude": "28.049137465573217"}],"code": 200}
     * 返回参数data下字段解析：
     *           confidenceLevel: 置信度
     *           gridName: 网格名称
     *           realLinkType: 真实环节类型(1研判2调度4处置5核实6办结)
     *           warningTypeName: 告警类型名称
     *           orderId: 事件ID
     *           flowNodeTime: 当前环节开始时间
     *           cityId: 地市编码
     *           deviceName: 设备名称
     *           industryCode: 行业编码
     *           deleteFlag: 删除标识 0正常 1删除
     *           orderStatusName: 订购状态名称
     *           emergencyLevel: 告警等级
     *           enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *           longitude: 经度
     *           isHandleDisplay: 是否显示督办提示图标（0-否，1-是）
     *           orgName: 所属组织名称
     *           alarmLableRelDTOList: 告警标签集合
     *           importanceLevel: 告警重要等级 1:1级,2:2级
     *           showTime: 超期时长
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省份编码
     *           ifTimeout: 是否超期：1-超期；2-未超期
     *           gridId: 网格id
     *           provinceName: 省名称
     *           alarmBody: 告警标题
     *           latitude: 纬度
     *           flowTemplate: 流程模板配置
     *           icon: 图标
     *           isCollection: 是否收藏 1:收藏 0：未收藏
     *           orderStatus: 告警状态 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           beforeDispatchFlag: 上个环节是否调度（0：不是 1：是）
     *           moreNextFlag: 下个环节是否多个标记为（0：不是  1：是）
     *           duration: 处理时长
     *           modifierDate: 最后更新时间
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           noticeResource: 通知类型
     *           cityName: 市名称
     *           tenantName: 租户名称
     *           videoUrl: 视频URL
     *           processId: 处理人
     *           countyId: 县编码
     *           emergencyLevelName: 告警等级名称
     *           flowId: 流程ID
     *           channelCode: 通道编码
     *           countyName: 县名称
     *           createDate: 创建时间
     *           warningSourceName: 告警来源名称
     *           address: 详细地址
     *           fileImgUrlIcon: 框图
     *           isSecondJudge: 是否二次研判 1-是；0-否
     *           alarmTime: 形成告警时间
     *           leaderNum: 领导批示数量
     *           timeLimitAcceptance: 受理时限
     *           deviceCode: 设备编码
     *           fileImgUrlSrc: 原图
     *           imgUrl: 图片
     *           timeLimit: 完成时限;单位：分钟
     *           noticeResourceName: 通知类型名称
     *           linkId: 环节ID
     *           warningTypeId: 告警类型
     *           tenantId: 租户编码
     *           channelName: 通道名称
     *           importanceLevelName: 重要等级名称
     *           reciveTime: 接收时间
     *           taskId: 第三方任务ID
     *           compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/orderForTrade/getOrderListDownImgs")
    TableDataInfo getOrderListDownImgs(@RequestBody AlarmDownloadReq dto);

    /**
     * 查询回收站事件列表
     * @apiNote 查询回收站事件列表接口
     * @apiVersion 1.20
     * @tag 一、告警事件能力
     * @requestDemo {"downLoadType":"0","orderIdDownLoadType":"2","downLoadWarningInterceptDTO":{"cityId":null,"provinceId":null,"countyId":null,"pageNum":1,"pageSize":6,"confidenceLevelMin":"","confidenceLevelMax":"",
     * "minChar":"100","maxChar":"100","alarmTimeStart":"2023-7-19 00:00:00","alarmTimeEnd":"2023-8-18 23:59:59","searchType":"0","areaCodeList":[],"deviceCode":[],"lableIdList":[]},
     * "warningOrderIds":["2023081812203737269749028","2023081812205121869749029","2023081812208775269749019","2023081812201469369748990","2023081812201469369748993","2023081812112379569748975"]}
     * @responseDemo {"total": 985903,"rows": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625",
     * "cityCode":"431200","cityName":"怀化市","provinceCode":"430000","provinceName":"湖南省","warningTypeId":"1212122","warningTypeName":"非法垂钓监控告警","intercept":"1","interceptName":"置信度压制",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","deviceCode":"43128100831327000290","deviceName":"YZHJ0018洪江市黔城镇柳溪村","channelCode":"43128100011130000116",
     * "channelName":"洪江市黔城镇柳溪村_0","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052","latitude": "28.049137465573217"}],"code": 200}
     * 返回参数data下字段解析：
     *           confidenceLevel: 置信度
     *           gridName: 网格名称
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
     *           height: 设备挂高，单位：米
     *           longitude: 经度
     *           alarmLableRelDTOS: 告警标签
     *           orgName: 所属组织名称
     *           importanceLevel: 告警重要等级 1:1级,2:2级
     *           ptzT: T位置
     *           ifEdit: 是否可编辑  0否 1是
     *           ptzP: P位置
     *           appVerCode: 行业版本编码
     *           confidenceLevelMax: 最大置信度
     *           ifTimeout: 是否超期  1已超期 2未超期
     *           intercept: 告警压制
     *           gridId: 网格id
     *           provinceName: 所在省名称
     *           presetId: 预置位Id
     *           alarmBody: 告警内容
     *           msgType: 消息类型
     *           cityCode: 地市编码
     *           latitude: 纬度
     *           icon: 告警类型图标
     *           warningOrderId: 事件ID
     *           algorithmCode: 算法编码
     *           modifierDate: 更新时间
     *           warningSource: 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     *           noticeResource: 通知类型 1-调度通知；2-设备端通知；
     *           cityName: 市名称
     *           tenantName: 租户名称
     *           videoUrl: 视频URL
     *           horiAzimuthAngle: 水平方位角
     *           interceptName: 告警压制原因
     *           channelCode: 通道编码
     *           countyName: 区县名称
     *           warningSourceName: 告警来源名称
     *           address: 详细地址
     *           extraField: 行业扩展字段
     *           fileImgUrlIcon: 框图
     *           videoLongitude: 设备经度
     *           isSecondJudge: 是否二次研判 1-是；0-否
     *           provinceCode: 省编码
     *           alarmTime: 形成告警时间
     *           deviceCode: 设备编码
     *           imgUrl: 图片
     *           modelName: 设备厂家名称
     *           countyCode: 县编码
     *           createTime: 创建时间
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户ID
     *           channelName: 通道名称
     *           importanceLevelName:
     *           taskId: 第三方任务id
     *           compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/orderForTrade/getInterceptList")
    TableDataInfo getInterceptList(@RequestBody AlarmDownloadReq dto);

    /**
     * 通过摄像机查询预置位&告警数量列表
     * @apiNote 通过摄像机查询预置位&告警数量列表接口
     * @apiVersion 1.21
     * @tag 一、告警事件能力
     * @requestDemo {"alarmTimeStart":"2023-8-1 00:00:00","alarmTimeEnd":"2023-8-16 23:59:59","deviceCode":["10210010010000","11010800831327000008"],"orderStatusList":["1","4"]}
     * @responseDemo {"total":1,"rows":[{presetId:"50354",//预置位ID presetName:"名称123123",//预置位名称  presetAlarmCount:"21" // 该预置位下未处置完成的告警数}],"code":200}
     * 返回参数data下字段解析：
     *           presetName: 该预置位下未处置完成的告警数
     *           presetAlarmCount: 预置位名称
     *           presetId: 预置位ID
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping(value = "/alarm/orderForTrade/getPresetListAndEventCount")
    TableDataInfo getPresetListAndEventCount(@RequestBody TradeAlarmOrderDTO dto);

    /**
     * 查询告警事件详情
     * @apiNote 查询告警事件详情接口
     * @apiVersion 1.22
     * @tag 一、告警事件能力
     * @requestDemo {"warningOrderId":"2023081612209782868940288","appVerCode":"1.0","industryCode":"000000","userId":"acfe9b1394c0443c84f5a1863d602d03"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1692168930283,"data": {"order":{"warningOrderId": "20220325TEN100001000000000000160","warningTypeId": "1010107","warningTypeName": "林木被毁监控告警","alarmBody": "88","warningSource": "2","alarmTime": "2022-03-25 11:01:17","provinceId": "110000",
     * "cityId": "","countyId": "","provinceName": "北京市","cityName": null,"countyName": null,"address": "北京市","longitude": "","latitude": "","emergencyLevel": "04","emergencyLevelName": "非常严重","tenantId": "TEN100001","orderStatus": "2","orderStatusName": "待调度","findName": "hjtest","findMobile": "13523302583",
     * "videoUrl": "https://powerexchange-fs.obs.cn-north-4.myhuaweicloud.com:443/6175494747ff26e9205efff1829f6ccf/02a98f23627c72cc59ad25a65ce58af3/5423e7fe461141c19cac8110cb6a2968.mp4","gridId": "","gridName": "","isCollection": "0","ifSend": "0","keeperName": null,"flowId": "9354","linkId": "2",
     * "polygonOperate":{"monitorAreaId": "16953018998139991223101879","layerId": "74353be0c92e443b83bcc32bc01a665c","apotArea": "3452.32","layerName": "生态保护红线20230915","featureId": "2344513441132","geometry": "[[[[116.30946668,40.034453582],[116.314606316,40.030646444],[116.313464175,40.029504303],[116.311846141,40.028552518],[116.30946668,40.034453582]]]]"}},
     * "imgs":[{"fileUrl":"https://slw-base-video.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1212127001_a0a59c75d7214f1d9ca7e82a912865b2388b60853ad64d4783bc69f9e6e35b76_02.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1692169530&Signature=XAK7Tk8dPyDYn1kR2wOY9WsEA4s%3D"}],
     * "video":{"fileImageUrl":"https://slw-base-video.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/video/ZGTT-1212127001_da4a5efce1914fa792f32d0a76e08963102e0b8805fd417dabc20f1f69b03014.mp4?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1692169530&Signature=qhu8fABplAe7Hpja9gowxmbomV8%3D"}}}
     * 返回参数data下字段解析：
     *           imgs: 图片地址
     *                     fileUrl: 图片地址url
     *           records: 环节记录
     *                     realLinkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     *                     flowEndTime: 环节结束时间
     *                     flowNodeTime: 当前环节开始时间
     *                     remark: 备注
     *                     leaders: 领导用户ID
     *                     linkName: 流程环节名称
     *                     nextLinkProcessName: 下个环节处理人集合
     *                     linkId: 环节ID
     *                     processName: 处理人姓名
     *                     dispatchProcessName: 处置环节处理人
     *                     ifCurrentLink: 是否当前环节 1:是 0:否
     *                     orderFlowId: 工单流程ID
     *                     acceptProcessName: 受理人姓名
     *                     enableAcceptanceMode: 受理状态
     *                     orderFileList: 带水印附件
     *                     closeRemark: 关闭说明
     *                     status: 状态
     *           video: 视频列表
     *                     reldevice: 设备关联的摄像机信息
     *                     videoList:
     *                               src: 视频地址
     *                     TIvideo: 兼容APP旧版本
     *                     fileImageUrl: 路径
     *           order:
     *                     iotDeviceList: 设备编码列表
     *                     confidenceLevel: 置信度
     *                     gridName: 网格名称
     *                     realLinkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     *                     modelId: 摄像机厂家
     *                     confidenceLevelMin: 最小置信度
     *                     cityId: 地市编码
     *                     alarmCode: 告警编码
     *                     relDevice: 摄像机信息
     *                     orderStatusName: 告警状态名称
     *                     emergencyLevel: 告警等级
     *                     height: 设备挂高，单位：米
     *                     isHandleDisplay: 是否显示督办提示图标（0-否，1-是）
     *                     transZoom: 转义后Z位置
     *                     importanceLevel: 告警重要等级 1:1级,2:2级
     *                     frontAlarmVedioUrl: 前端告警视频
     *                     ifEdit: 是否可编辑（0否、1是）
     *                     confidenceLevelMax: 最大置信度
     *                     ifTimeout: 是否超时 0否1是
     *                     gridId: 网格id
     *                     provinceName: 省名称
     *                     incidentDistance: 事发地点距离 单位: 米
     *                     alarmBody: 告警内容
     *                     msgType: 消息类型
     *                     videoLatitude: 设备纬度
     *                     modelFactoryName: 设备厂家
     *                     latitude: 纬度
     *                     icon: 告警类型图标
     *                     warningOrderId: 告警单ID(yyyyMM+租户id+)
     *                     enlargeFlag: 是否3D放大
     *                     modifierDate: 修改时间
     *                     deviceAddress: 设备地址
     *                     noticeResource: 通知类型 1-调度通知；2-设备端通知
     *                     videoUrl: 视频URL
     *                     processId: 处理人
     *                     countyName: 县名称
     *                     industryName: 行业应用名称
     *                     address: 详细地址
     *                     videoLongitude: 设备经度
     *                     alarmTime: 告警时间
     *                     fileImgUrlSrc: 原图
     *                     focusRange: 焦距范围
     *                     noticeResourceName: 通知类型名称
     *                     tenantId: 租户编码
     *                     channelName: 通道名称
     *                     reciveTime: 接收时间
     *                     deviceLongitude: 设备经度
     *                     waitTime: 当前环节停留时长
     *                     deviceImageUrl: 摄像机图标地址
     *                     warningTypeName: 告警类型名称
     *                     flowNodeTime: 当前环节开始时间
     *                     tilt: T位置
     *                     deviceName: 设备名称
     *                     transPan: 转义后的P位置
     *                     deleteFlag: 删除标识 0正常  1删除
     *                     deviceLatitude: 设备纬度
     *                     vertiDownNeg: 水平向下为负 0否1是.
     *                     enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *                     keeperName: 网格员姓名
     *                     pan: P位置
     *                     transTitle: T位置
     *                     deviceCodeCollect: 设备收藏标记
     *                     longitude: 经度
     *                     showTime: 超期时间
     *                     provinceId: 省份编码
     *                     appVerCode: 行业版本编码
     *                     ifSend: 检查是否要发送短信通知
     *                     files: 文件
     *                     frontAlarmPicUrl: 前端告警图片
     *                     flowTemplate: 流程模板配置
     *                     isCollection: 是否收藏（1:收藏 0：未收藏）
     *                     description: 事件描述
     *                     orderStatus: 事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *                     beforeDispatchFlag: 是否是调度操作（1：是  其他：不是）
     *                     algorithmCode: 算法编码
     *                     warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *                     cityName: 所在市名称
     *                     tenantName: 客户名称
     *                     countyId: 区编码
     *                     emergencyLevelName: 告警等级名称
     *                     flowId: 流程ID
     *                     channelCode: 通道编码
     *                     warningSourceName: 告警来源名称
     *                     satelliteDeviceList: 卫星告警联动摄像机信息
     *                     gridInfo: 网格对象信息
     *                               borderColor: 网格边框颜色
     *                               borderColorLineThickness: 网格边框线条粗细 取值1 2 3 4 5 默认1
     *                               gridName: 网格名称
     *                               color: 网格填充颜色
     *                               gridRange: 网格坐标
     *                               cityCode: 市编码
     *                               modifier: 修改人
     *                               pageSize: 条数限制
     *                               industryCode: 行业编码
     *                               deleteFlag:
     *                               gridCenter: 网格中心坐标
     *                               parentCode: 上级网格编码
     *                               pointStatus: 标绘状态
     *                               gridLevel: 网格层级。网格层级表的层级
     *                               creator: 创建人
     *                               colorTransparency: 网格填充颜色透明度 单位% 取值0-1 默认0.5
     *                               provinceCode: 省编码
     *                               gridLevelCode: 网格层级编码 原attribute6
     *                               updateTime: 更新时间
     *                               pageNum: 当前页
     *                               excludeGridId: 列表排除的网格id，不查询此id
     *                               parentId: 上级网格ID
     *                               borderColorTransparency: 网格边框颜色透明度 单位% 取值0-1 默认0.5
     *                               parentName: 上级网格名称
     *                               createTime: 创建时间
     *                               tenantId: 租户id
     *                               gridId: 网格ID
     *                               gridCode: 网格编码。人工填写，编码规则业务规定，不允许重复。
     *                     fileImgUrlIcon: 框图
     *                     isSecondJudge: 是否二次研判 1-是；0-否
     *                     msgTypeName: 消息类型名称
     *                     zoom: Z位置
     *                     deviceCode: 设备编码
     *                     timeLimit: 完成时限;单位：分钟
     *                     linkId: 当前环节ID
     *                     radarDevice: 雷达设备
     *                     warningTypeId: 告警类型
     *                     importanceLevelName: 重要等级名称
     *                     taskId: 任务id
     *                     tradeId: 行业ID
     *                     compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return Result<WarningOrderInfoVO>
     */
    @PostMapping(value = "/alarm/orderForTrade/getOrderInfo")
    Result<WarningOrderInfoVO> getOrderInfo(@RequestBody TradeAlarmOrderDTO dto);

    /**
     * 转领导批示
     * @apiNote 转领导批示接口
     * @apiVersion 1.23
     * @tag 一、告警事件能力
     * @requestDemo {"appVerCode":"1.0","industryCode":"000000","userId":"acfe9b1394c0443c84f5a1863d602d03","orderId":"202308011210630161483806","content":"转领导批示","leaderId":"309739"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741}
     * 返回参数data下字段解析：
     *           操作成功，返回成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping(value = "/alarm/orderForTrade/addLeaderAudit")
    Result addLeaderAudit(@RequestBody TradeOrderLeaderAuditDTO dto);

    /**
     * 事件流程流转
     * @apiNote 告警事件流程流转接口
     * @apiVersion 1.24
     * @tag 一、告警事件能力
     * @requestDemo {"warningOrderId":"202308011210630161483806","appVerCode":"1.0","industryCode":"000000","userId":"acfe9b1394c0443c84f5a1863d602d03",
     * "warningTypeId":"1010104","currentLink":"1","linkId":"1","linkName":"研判1","orderStatus":"1","isNextLink":"1","processId":"27f298c528e94112bb8fffa824ded4b6",
     * "processName":"chenquanjiang","realLinkType":"1","flowLinkType":"0","judgeResult":"1","duplicateOrderId":"202308141210633511647501","enableAcceptanceMode":"1",
     * "timeLimitAcceptance":"5","nextEnableAcceptanceMode":"0"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741}
     * 返回参数data下字段解析：
     *           操作成功，返回成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping(value = "/alarm/orderForTrade/submitProcess")
    Result submitProcess(@RequestBody TradeSendAlarmOrderDTO dto);

    /**
     * 新增/取消收藏
     * @apiNote 新增/取消收藏接口
     * @apiVersion 1.25
     * @tag 一、告警事件能力
     * @requestDemo {"warningOrderId":"2023081612301514168951555","alarmTime":"2023-08-16 15:21:27"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741}
     * 返回参数data下字段解析：
     *           操作成功，返回成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping(value = "/alarm/orderForTrade/warningOrderCollection")
    Result warningOrderCollection(@RequestBody WarningOrderCollectionDTO dto);

    /**
     * 我的收藏-查询列表
     * @apiNote 我的收藏-查询列表接口
     * @apiVersion 1.26
     * @tag 一、告警事件能力
     * @requestDemo {"orderId":"2023081612006598768857108","pageNum":1,"pageSize":6,"warningSource":"8","isCollection":"1","orderStatusList":["1", "5", "4"],"alarmTimeStart":"2023-7-17 00:00:00","alarmTimeEnd":"2023-8-16 23:59:59","warningTypeIdList":
     * ["1010105", "1010104", "1010103"],"searchType":"0","provinceId":"110000","cityId":"110100","countyId":"110101","isSecondJudge":"1","secondJudgeResult":"1","importanceLevel":"1","deviceCode":[],"lableIdList":[]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1689491058930,"data": [{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625",
     * "cityCode":"431200","cityName":"怀化市","provinceCode":"430000","provinceName":"湖南省","warningTypeId":"1212122","warningTypeName":"非法垂钓监控告警","intercept":"1","interceptName":"置信度压制",
     * "orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","deviceCode":"43128100831327000290","deviceName":"YZHJ0018洪江市黔城镇柳溪村","channelCode":"43128100011130000116",
     * "channelName":"洪江市黔城镇柳溪村_0","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052","latitude": "28.049137465573217"}]}
     * 返回参数data下字段解析：
     *           confidenceLevel: 置信度
     *           gridName: 网格名称
     *           warningTypeName: 告警类型名称
     *           orderId: 告警单ID(yyyyMM+租户id+)
     *           confidenceLevelMin: 最小置信度
     *           flowNodeTime: 当前环节开始时间
     *           cityId: 市编码
     *           deviceName: 设备名称
     *           industryCode: 行业编码
     *           deleteFlag: 删除标识 0正常 1删除
     *           orderStatusName: 工单状态名称
     *           emergencyLevel: 告警等级名称
     *           enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *           longitude: 经度
     *           isHandleDisplay: 是否显示督办提示图标（0-否，1-是）
     *           orgName: 所属组织名称
     *           alarmLableRelDTOList: 告警标签集合
     *           importanceLevel: 重要等级
     *           showTime: 超期时长
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省编码
     *           ifTimeout: 是否超期：1-超期；2-未超期
     *           alarmBody: 告警内容
     *           latitude: 纬度
     *           icon: 告警类型图标
     *           isCollection: 是否收藏（1:收藏 0：未收藏）
     *           orderStatus: 告警状态
     *           moreNextFlag: 下个环节是否多个标记为（0：不是  1：是）
     *           duration: 处理时长
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           tenantName: 客户名称
     *           processId: 处理人
     *           countyId: 县编码
     *           emergencyLevelName: 告警等级名称
     *           channelCode: 通道编码
     *           createDate: 创建时间
     *           warningSourceName: 告警来源名称
     *           address: 详细地址
     *           extraField: 行业扩展字段
     *           fileImgUrlIcon: 框图
     *           isSecondJudge: 是否二次研判
     *           alarmTime: 形成告警时间
     *           leaderNum: 领导批示数量
     *           timeLimitAcceptance: 受理时限
     *           deviceCode: 设备编码
     *           imgUrl: 图片地址
     *           timeLimit: 完成时限;单位：分钟
     *           linkId: 当前环节ID
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户id
     *           channelName: 摄像机通道名称
     *           importanceLevelName: 重要等级名称
     *           reciveTime: 接收时间
     *           compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/qryWarningOrderCollection")
    public Result qryWarningOrderCollection(@RequestBody TradeAlarmOrderDTO dto);

    /**
     * 我的收藏-查询列表（下载图片）
     */
    @PostMapping("/alarm/orderForTrade/qryWarningOrderCollectionImgs")
    public Result qryWarningOrderCollectionImgs(@RequestBody AlarmDownloadReq dto);

    /**
     * 查询订购关联的告警类型
     * @apiNote 查询订购关联的告警类型枚举接口
     * @apiVersion 1.27
     * @tag 一、告警事件能力
     * @responseDemo
     * 返回参数data下字段解析：
     *      alarmTypeId: 告警类型ID
     *      alarmTypeClassId: 类型分类表ID 1-算法告警；2-设备告警
     *      typeName: 类型名称
     *      typeValue: 告警类型编码
     *      typeCode: 类型编码
     *      alarmSource: 告警来源列表
     *      tenantId: 租户id
     *      source: 来源: 0-初始化;1-操作;2-铁塔新增
     *      ifFree: 是否赠送 0:否  1:是
     *      icon: 图标
     *      creator: 创建人
     *      createTime: 创建时间
     *      deleteFlag: 删除标识 0正常 1删除
     *      industryCode: 行业编码
     * 返回参数示例：
     * {
     *   "code": 200,
     *   "msg": "",
     *   "time": 1692170741212,
     *   "data": [
     *    {
     *       "alarmTypeId": null,
     *       "alarmTypeClassId": null,
     *       "typeName": "烟火侦测（可见光）告警",
     *       "typeValue": "1010100",
     *       "typeCode": null,
     *       "alarmSource": "1",
     *       "tenantId": null,
     *       "source": null,
     *       "ifFree": null,
     *       "icon": "icon-gnjieganjinshaojiankong",
     *       "creator": null,
     *       "createTime": null,
     *       "deleteFlag": null,
     *       "industryCode": null
     *     }
     *   ]
     * }
     */
    @PostMapping("/alarm/orderForTrade/findOrderAlarm")
    public Result findOrderAlarm(@RequestBody AlarmTypeDTO dto);

    /**
     * 研判为重复告警时，获取事件列表
     * @apiNote 查询重复告警事件列表接口
     * @apiVersion 1.28
     * @tag 一、告警事件能力
     * @requestDemo {"alarmTimeStart":"2023-8-1 00:00:00","alarmTimeEnd":"2023-8-16 23:59:59","tenantId":"120046990","longitude":"112.84533764","latitude":"29.78239645","orderId":"202308141210633511647490",
     * "pageNum":"1","pageSize":"10","keyword":"AI告警","alarmBody":"电子围栏低速","spaceRange":"3000","alarmRangeP":"10","alarmRangeT":"50","orderStatusList":["2","4"],"provinceId":"110000","cityId":"110100","countyId":"110108"}
     * @responseDemo {"total": 26,"rows": [{"orderId":"202308141210633511647490","warningSource": "1","warningTypeId": "1212102","alarmTime": "2022-03-24 04:37:14","tenantId": "TEN100001","tenantName": "算法仓02","longitude": "1143.794872",
     * "latitude": "24.192697","alarmBody": "人员闯入监控告警", "orderStatus": "2","address": "北京市朝阳区常营中街与常通路十字路口西北角1111","imgUrl": "http://120.46.145.37:10120//ThirdAlarm/pic//KLYR-1212102001_0932e8346e7045799ea114b630f0aedd45c10f4ad0534d92b8988652a05de5b0_01_880.jpg",
     * "warningTypeName": "人员闯入监控告警","emergencyLevelName": "一般","orderStatusName": "待调度","warningSourceName": "AI上报","judgeResult": "2","realLinkType":"2","confidenceLevel":"0.6894647"}],"code": 200}
     * 返回参数data下字段解析：
     *           alarmBody: 告警内容
     *           confidenceLevel: 最小置信度
     *           address: 详细地址
     *           realLinkType: 真实环节id(1研判2调度4处置5核实6办结)
     *           warningTypeName: 告警类型名称
     *           orderId: 告警单ID(yyyyMM+租户id+)
     *           importanceLevel: 重要等级
     *           alarmTime: 形成告警时间
     *           confidenceLevelMin: 最小置信度
     *           latitude: 纬度
     *           leaderNum: 领导批示数量
     *           icon: 告警类型图标
     *           orderStatus: 事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           ifEdit: 是否可编辑  0否 1是
     *           imgUrl: 原图地址
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           orderStatusName: 工单状态名称
     *           ifTimeout: 是否超时 0否1是
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户ID
     *           importanceLevelName: 重要等级名称
     *           compConfidenceLevel: 综合置信度
     *           longitude: 经度
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping("/alarm/orderForTrade/getOrderListForDuplication")
    public TableDataInfo getOrderListForDuplication(@RequestBody AlarmOrderDuplicateDTO dto);

    /**
     * 流程回退
     * @apiNote 流程回退接口
     * @apiVersion 1.29
     * @tag 一、告警事件能力
     * @requestDemo {"warningOrderId":"202308141210633511647490","realLinkType":"1"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1692170741212}
     * 返回参数data下字段解析：
     *                操作成功，返回成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/rollBack")
    public Result rollBack(@RequestBody TradeSendAlarmOrderDTO dto);

    /**
     * 获取流程流转过程数据
     * @apiNote 获取流程流转过程数据接口
     * @apiVersion 1.30
     * @tag 一、告警事件能力
     * @requestDemo {"warningOrderId":"202308141220357971647473","linkId":"1","realLinkType":"1"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1651049172858,"data": [{"fileName": null,"flowEndTime": "2022-04-27 15:15:57","flowNodeTime": "2022-04-27 15:15:57","remark": "HHHHH",
     * "pendingHandleName": null,（处置人）"pendingHandleId": null,（处置人）"resourceUrl": null,"processId": "309739",（调度人）"processName": "bjshyygc",（调度人）\"orderFlowId": "202204121061973000246125",
     * "orderFileList": [{"fileName": "附件","flowEndTime": null,"flowNodeTime": null,"remark": null,"pendingHandleName": null,"pendingHandleId": null,"resourceUrl": "https://fanyi.youdao.com/","processId": null,
     * "processName": null,"orderFlowId": null,"orderFileList": null,"resourceType": "3","status": null}],"resourceType": null,"status": "0"（处置状态）}]}
     * 返回参数data下字段解析：
     *           voice: 语音
     *           realLinkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     *           orderId: 告警单ID(yyyyMM+租户id+)
     *           userInfoList: 处理人信息
     *           groupId: 小组id，组织编码
     *           flowNodeTime: 当前环节开始时间
     *           judgeResult: 研判结果 0：误判 1：真实 2：重复报警
     *           linkName: 当前环节名称
     *           deleteFlag: 删除标识 0正常 1删除
     *           nextLinkId: 所选下个环节id
     *           pendingHandleId: 处置人ID
     *           processName: 处理人姓名
     *           approveOrderId: 审批单ID
     *           duplicateAlarmBody: 重复告警 告警内容
     *           acceptProcessName: 受理人姓名
     *           enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *           orderFileList: 附件列表
     *           judgedName: 研判人名称
     *           processNameList: 指定处置人账户集
     *           orgName: 所属组织名称
     *           duplicateOrderId: 重复报警事件ID
     *           mobilePhone: 移动电话
     *           ifTimeout: 是否超时 0否1是
     *           dispatchProcessName: 调度人名称
     *           approvePerson: 审批人
     *           judgeDeptName:  研判组织名称
     *           nextLinkName: 所选下个环节名称
     *           status: 状态
     *           dispatchRemark: 调度备注
     *           remark: 备注
     *           judgedType: 研判类型
     *           warningProcessId: 处理人ID
     *           nextLinkProcessName: 下个环节处理人集合
     *           flowLinkType: 流转类型（0默认、1转派、2回退）
     *           processId: 处理人
     *           handleProcessName: 处置人
     *           orderFlowId: 工单流程ID
     *           flowRecordName: 流程记录名称
     *           closeRemark: 关闭说明
     *           creator: 创建人
     *           handleTime: 处理时间
     *           flowEndTime: 环节结束时间
     *           ifAttachment:  查看展示按钮 1是0否
     *           processOrderId: 处理单ID
     *           judgedPhone: 研判人电话
     *           handleRemark: 督办意见
     *           pendingHandleName: 处置人名称
     *           orderHandleFileList: 处置环节附件列表
     *           timeLimit: 完成时限;单位：分钟
     *           dispatchTime: 调度时间
     *           acceptProcessId: 受理人id
     *           linkId: 环节ID
     *           ifCurrentLink: 是否当前环节  1:是 0:否
     *           acceptProcessPhone: 受理人电话
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getRecordByType")
    public Result getRecordByType(@RequestBody TradeSendAlarmOrderDTO dto);

    /**
     * 查询领导批示用户数据
     * @apiNote 查询领导批示用户数据接口
     * @apiVersion 1.31
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"120046990"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1692170741212,"data":{"userId":"0094f25787a04ba4bc0d2dbbd9fdea71","userName":"test4",
     * "tenantId":"120046990","roleId":"999059"}}
     * 返回参数data下字段解析：
     *           roleId:  角色ID
     *           tenantId: 租户id
     *           admin: 是否管理员
     *           orderBy: 排序
     *           userName: 用户账号
     *           userId: 用户id
     *           adminNew: 管理员拥有所有权限
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getLeaderByTenantId")
    public Result getLeaderByTenantId(@RequestBody SysUserDTO dto);

    /**
     * 查询告警图层
     * @apiNote 查询告警图层接口
     * @apiVersion 1.32
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"120046990","alarmTimeStart":"2023-7-18 00:00:00","alarmTimeEnd":"2023-8-17 23:59:59","pageNum":1,"pageSize":6,"appVerCode":"1.0","industryCode":"000000","provinceId":"110000","cityId":"110100","countyId":"110108",
     * "longitude":"116.30576686828336","latitude":"39.95981617277608","roleFlag":"8","radius":"3000","warningSourceList":["8","10"],"orderStatusList":["1","3"],"warningTypeList":["1212100","1212111"]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1651049172858,"data":[{"warningOrderId":"202308141210633511647490","warningSource": "1","warningTypeId": "1212102","alarmTime": "2022-03-24 04:37:14","tenantId": "TEN100001","tenantName": "算法仓02","provinceId":"110000","cityId":"110100",
     * "countyId":"110101","longitude": "1143.794872","latitude": "24.192697","alarmBody": "人员闯入监控告警", "orderStatus": "2","orderStatusName":"待研判","address": "北京市朝阳区常营中街与常通路十字路口西北角1111","processId":"18a550a3d40742339ca56a15908ae493,...","provinceName":"青海省","flowNodeTime":"2023-08-17 10:41:10",
     * "videoUrl": "http://120.46.145.37:10120//ThirdAlarm/pic//KLYR-1212102001_0932e8346e7045799ea114b630f0aedd45c10f4ad0534d92b8988652a05de5b0_01_880.jpg","gridId":"","gridName":"","linkId":"1","flowId":"1001","deviceCode":"1234","deviceName":"111",
     * "alarmCode":"111","channelCode":"11010800831310000008","channelName","铁塔产业园-11号楼-大华243_0","warningTypeName": "人员闯入监控告警","emergencyLevelName": "一般","orderStatusName": "待调度","warningSourceName": "AI上报","judgeResult": "2","realLinkType":"2","confidenceLevel":"0.6894647"}]}
     * 返回参数data下字段解析：
     *           alarmBody: 告警内容
     *           gridName: 网格名称
     *           latitude: 纬度
     *           flowNodeTime: 当前环节开始时间
     *           orderStatus: 事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           warningOrderId: 告警单ID(yyyyMM+租户id+)
     *           cityId: 地市编码
     *           deviceName: 设备名称
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           cityName: 地市名称
     *           emergencyLevel: 告警等级
     *           tenantName: 租户名称
     *           videoUrl: 视频URL
     *           processId: 处理人
     *           countyId: 区县编码
     *           enableAcceptanceMode: 是否开启受理模式(0:默认，不开启；1:开启)
     *           flowId: 流程ID
     *           channelCode: 通道编码
     *           countyName: 县名称
     *           createDate: 创建时间
     *           longitude: 经度
     *           address: 详细地址
     *           fileImgUrlIcon: 框图
     *           alarmTime: 告警时间
     *           leaderNum: 领导批示数量
     *           deviceCode: 设备编码
     *           fileImgUrlSrc: 原图
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省编码
     *           imgUrl: 原图
     *           timeLimit: 完成时限;单位：分钟
     *           linkId: 环节ID
     *           checkTime: 研判完成时间
     *           ifTimeout: 是否超时 0否1是
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户编码
     *           channelName: 通道名称
     *           gridId: 网格id
     *           provinceName: 省份名称
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getWarningOrderLayer")
    public Result getWarningOrderLayer(@RequestBody WarningOrderLayerDTO dto);

    /**
     * 修改告警事件
     * @apiNote 更新告警事件信息接口
     * @apiVersion 1.33
     * @tag 一、告警事件能力
     * @requestDemo {"warningOrderId":"202308011210630161483806","appVerCode":"1.0","tenantId":"120046990","warningTypeId":"1212116","provinceName":"北京市","provinceCode":"110000",
     * "cityName":"北京市","cityCode":"110100","countyCode":"110108","countyName":"海淀区","longitude":"116.30576686828336","latitude":"39.95981617277608","address":"北京市海淀区四季青镇常青路中国铁塔产业园",
     * "linkId":"1","linkName":"研判1","emergencyLevel":"01","importanceLevel":"1","alarmParam":"","alarmParamValue":"",}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741}
     * 返回参数data下字段解析：
     *                操作成功，返回成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/modifyWarningOrderInfo")
    public Result modifyWarningOrderInfo(@RequestBody WarningOrderDTO dto);

    /**
     * 查询关联告警事件信息
     * @apiNote 查询关联的告警事件详细信息接口
     * @apiVersion 1.34
     * @tag 一、告警事件能力
     * @requestDemo {"duplicateOrderId":"202308141220357971647466","alarmTimeStart":"2023-08-01 00:00:00","alarmTimeEnd":"2023-08-01 23:59:59"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1651049172858,"data":[{"warningOrderId":"202308141210633511647490","warningSource": "1","warningTypeId": "1212102","alarmTime": "2022-03-24 04:37:14","tenantId": "TEN100001","tenantName": "算法仓02","provinceId":"110000","cityId":"110100",
     * "countyId":"110101","longitude": "1143.794872","latitude": "24.192697","alarmBody": "人员闯入监控告警", "orderStatus": "2","address": "北京市朝阳区常营中街与常通路十字路口西北角1111","videoUrl": "http://120.46.145.37:10120//ThirdAlarm/pic//KLYR-1212102001_0932e8346e7045799ea114b630f0aedd45c10f4ad0534d92b8988652a05de5b0_01_880.jpg",
     * "deviceCode":"1234","deviceName":"111","alarmCode":"111","channelCode":"11010800831310000008","channelName","铁塔产业园-11号楼-大华243_0","warningTypeName": "人员闯入监控告警","emergencyLevelName": "一般","orderStatusName": "待调度","warningSourceName": "AI上报","judgeResult": "2","realLinkType":"2","confidenceLevel":"0.6894647"}]}
     * 返回参数data下字段解析：
     *           alarmBody: 告警内容
     *           gridName: 网格名称
     *           latitude: 纬度
     *           flowNodeTime: 当前环节开始时间
     *           orderStatus: 事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           warningOrderId: 告警单ID(yyyyMM+租户id+)
     *           cityId: 地市编码
     *           deviceName: 设备名称
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           cityName: 地市名称
     *           emergencyLevel: 告警等级
     *           tenantName: 租户名称
     *           videoUrl: 视频URL
     *           processId: 处理人
     *           countyId: 区县编码
     *           enableAcceptanceMode: 是否开启受理模式(0:默认，不开启；1:开启)
     *           flowId: 流程ID
     *           channelCode: 通道编码
     *           countyName: 县名称
     *           createDate: 创建时间
     *           longitude: 经度
     *           address: 详细地址
     *           fileImgUrlIcon: 框图
     *           alarmTime: 告警时间
     *           leaderNum: 领导批示数量
     *           deviceCode: 设备编码
     *           fileImgUrlSrc: 原图
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省编码
     *           imgUrl: 原图
     *           timeLimit: 完成时限;单位：分钟
     *           linkId: 环节ID
     *           checkTime: 研判完成时间
     *           ifTimeout: 是否超时 0否1是
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户编码
     *           channelName: 通道名称
     *           gridId: 网格id
     *           provinceName: 省份名称
     * @param dto 入参
     * @return Result<List<WarningOrderInfoVO>>
     */
    @PostMapping(value = "/alarm/orderForTrade/warningOrderInfoRel")
    public Result<List<WarningOrderInfoVO>> warningOrderInfoRel(@RequestBody WarningOrderRelDTO dto);

    /**
     * 生成告警压制
     */
    @PostMapping("/alarm/orderForTrade/createIntercept")
    public Result createIntercept(@RequestBody InterceptConfigDTO dto);

    /**
     * 查询用户代办事项数量
     * @apiNote 查询用户代办事项数量接口
     * @apiVersion 1.35
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"120046990","userId":"","provinceId":"110000","cityId":"110100","countyId":"","alarmTimeStart":"2023-7-18 00:00:00","alarmTimeEnd":"2023-8-17 23:59:59"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1692170741212,"data":{"eventNum":"200","leanderNum":"20"}}
     * 返回参数data下字段解析：
     *           leanderNum: 领导批示待办数量
     *           eventNum: 告警待办数量
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getTodoNum")
    public Result getTodoNum(@RequestBody AppOrderListByUserDTO dto);

    /**
     * 批量关闭告警
     * @apiNote 批量关闭告警接口
     * @apiVersion 1.36
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"122035797","warningOrderList":[{"warningOrderId":"202308141220357971647483","currentLink":"1"}]}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741}
     * 返回参数data下字段解析：
     *                操作成功，返回成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/batchEndWarningOrder")
    public Result batchEndWarningOrder(@Validated @RequestBody BatchEndWarningOrderDTO dto);

    /**
     * 根据流程ID，租户ID获取用户树
     */
    @PostMapping("/alarm/orderForTrade/getFlowByIdUserTree")
    public Result<SysInfoVo> getFlowByIdUserTree(@RequestBody FlowLinkAttrModelDTO model);

    /**
     * 根据流程ID，租户ID获取用户树
     * @apiNote 根据流程ID+租户ID获取用户树接口
     * @apiVersion 1.37
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"122035797","gridId":"202306271446361559","gridFlag":"1","flowId":"11373","curLink":"1","provinceId":"110000","cityId":"110100",
     * "countyId":"110101","deviceCode":"11010800831327000008","warningSource":"10"}
     * @responseDemo {"code": 200,"msg": "查询成功","time": 1679367633596,"data": {"nodeName": "测试用-通用3.0版本-3.0",
     * "sysAllInfo": {"nodeName": "测试用-通用3.0版本-3.0","deptName": "测试用-通用3.0版本-3.0","children": [{"nodeName": "lsg006","tenantAdmin": "0","nickName": "lsg006",
     * "purviewFlag": "1","deptId": "119124759-02","userName": "李四","nodeId": "298a5ef22f784dcb9aee00090eb667c1","userId": "298a5ef22f784dcb9aee00090eb667c1"}],"deptId": "119124759-02",
     * "nodeId": "119124759-02","parentId": "000000"},"deptName": "测试用-通用3.0版本-3.0",
     * "children": [{"nodeName": "hnyz","tenantAdmin": "0","nickName": "hnyz","purviewFlag": "1","deptId": "119124759-01,119124759-02","userName": "测试用","nodeId": "aeb7d9ce1dac4b7d874cba6cb67fdcf2","userId": "aeb7d9ce1dac4b7d874cba6cb67fdcf2"}],
     * "sysPowerInfo": {"nodeName": "测试用-通用3.0版本-3.0","deptName": "测试用-通用3.0版本-3.0","children": [{"nodeName": "hnyz","tenantAdmin": "0","nickName": "hnyz","purviewFlag": "1","deptId": "119124759-01,119124759-02","userName": "测试用",
     * "nodeId": "aeb7d9ce1dac4b7d874cba6cb67fdcf2","userId": "aeb7d9ce1dac4b7d874cba6cb67fdcf2"}],"deptId": "119124759-02","nodeId": "119124759-02","parentId": "000000"},"deptId": "119124759-02","nodeId": "119124759-02","parentId": "000000"}}
     * 返回参数data下字段解析：
     *           nodeName: 节点名称
     *           deptName: 部门名称
     *           sysAllList: 所有可选人物
     *           sysPowerList: 所有有权限列表人物
     *           nickName: 用户昵称
     *           roleId: 角色ID
     *           purviewFlag: 该用户是否有数据权限（0否，1是）
     *           deptId: 部门ID
     *           phonenumber: 手机号码
     *           avatar: 头像地址
     *           userName: 用户名称
     *           userId: 用户ID
     *           parentId: 父级组织ID
     *           sysAllInfo: 所有配置用户
     *           tenantAdmin: 是否租户管理员(0否,1是)
     *           children:
     * 				  nodeName: 节点名称
     * 				  deptName: 部门名称
     * 				  sysAllList: 所有可选人物
     * 				  sysPowerList: 所有有权限列表人物
     * 				  nickName: 用户昵称
     * 				  roleId: 角色ID
     * 				  purviewFlag: 该用户是否有数据权限（0否，1是）
     * 				  deptId: 部门ID
     * 				  phonenumber: 手机号码
     * 				  avatar: 头像地址
     * 				  userName: 用户名称
     * 				  userId: 用户ID
     * 				  parentId: 父级组织ID
     * 				  sysAllInfo: 所有配置用户
     * 				  tenantAdmin: 是否租户管理员(0否,1是)
     * 				  sysPowerInfo: 所有有权限的用户
     * 				  roleName: 角色名称
     * 				  nodeId: 节点ID
     * 				  status: 状态
     *           sysPowerInfo: 所有有权限的用户
     *           roleName: 角色名称
     *           nodeId: 节点ID
     *           status: 状态
     * @param model 入参
     * @return Result<SysInfoVo>
     */
    @PostMapping("/alarm/orderForTrade/getFlowByIdUserTreeNew")
    public Result<SysInfoVo> getFlowByIdUserTreeNew(@RequestBody FlowLinkAttrModel model);

    /**
     * 查询领导批示用户数据(根据组织节点展示树状数据)
     * @apiNote 查询领导批示用户数据(根据组织节点展示树状数据)
     * @apiVersion 1.38
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"122035797","provinceId":"110000","cityId":"110100","countyId":"110101","deviceCode":"11010800831327000008","warningSource":"2","roleFlag":"8"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1673254838741,"data":[{"nodeName": "软件园腾飞发展有限公司（腾飞园区）","deptName": "软件园腾飞发展有限公司（腾飞园区）",
     * "children": [{"nickName": "dsada","roleId": 8,"deptId": "121063380-01","userName": "张管理员","userId": "xadsdad111"}],"deptId": "121063380-01","nodeId": "121063380-01","parentId": "000000"}]}
     * 返回参数data下字段解析：
     *           nodeName: 节点名称
     *           deptName: 部门名称
     *           children: 子级
     *                nickName: 昵称
     *                roleId: 角色ID
     * 			      nodeName: 节点名称
     * 			      deptId:   部门ID
     * 			      userName:  用户名
     * 			      userId: 用户ID
     *           deptId: 部门ID
     *           nodeId: 节点ID
     *           parentId: 父级ID
     * @param model 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getLeaderByTenantIdShowTree")
    public Result getLeaderByTenantIdShowTree(@RequestBody ApiLeaderByTenantIdTreeReq model);

    /**
     * 根据工单号，选择环节id获取当前环节最后一个处理人信息
     * @apiNote 查询该工单所选环节最后一个处理人信息接口
     * @apiVersion 1.39
     * @tag 一、告警事件能力
     * @requestDemo {"linkId":"1","orderId":"202308141220357971647483"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1673254838741,"data":{"processId":"839124rehuifsd09122jweidq1","processName":"tower","groupId":null}}
     * 返回参数data下字段解析：
     *           processId: 处理人
     *           processName: 处理人名称
     *           groupId: 部门id
     * @param model 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/queryLastProcess")
    public Result queryLastProcess(@RequestBody ApiSelectLastRrocessReq model);

    /**
     * 告警事件转派接口
     * @apiNote 告警事件转派接口
     * @apiVersion 1.40
     * @tag 一、告警事件能力
     * @requestDemo {"alarmFlowId":"10016","appVerCode":"1.0","industryCode":"000000","beforLink":"1","flowTemplate":"315785070878329067","curLink":"2","linkName":"调度","reallinkType":"1",
     * "warningOrderId":"202308141220357971647483","remark":"转派到研判环节","processDTOList":[{"processId":"01092494026d4a519f1ded907865d7ab,019aca627cb74223a95dbec3d964e397","processName":""}]}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741}
     * 返回参数data下字段解析：
     *                操作成功，返回成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/reassignment")
    public Result reassignment(@RequestBody TradeReassignmenOrdertDto dto);

    /**
     * 查询可转派环节信息接口
     * @apiNote 查询可转派环节信息接口
     * @apiVersion 1.41
     * @tag 一、告警事件能力
     * @requestDemo {"alarmFlowId":"10016","curLink":"2","templateId":"315785070878329068","industryCode":"000000","appVerCode":"1.0","warningSource":"1"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1667197473238,"data": [{"userId": "0cc2c60a8c7141cdbf48b927a3aaafac,320f2884cad54856bdfba0aaff7d509f",
     * "appVerCode": null,"industryCode": null,"flowId": 10914,"tenantName": null,"flowName": "研判调度处置核实流程","linkType": "2","userType": "1","roleId": null,"msgNotifyType": "1,3,4",
     * "leaderFlag": "1","signFlag": null,"rollbackFlag": "1","linkId": 2,"flowLinkId": 11533,"alarmFlowId": 10914,"preLink": "1","nextLink": "4","linkName": "调度","timeLimit": null,
     * "orgId": null,"userName": "卫星东城区,微创测试二","nickName": null,"alarmCode": null,"warningSource": null,"curLink": 2,"turnFlag": "0","realLinkType":"","dataFilterPermiss":"","customAdmin":""}]}
     * 返回参数data下字段解析：
     *           curLink: 当前环节ID
     *           signFlag: 会签标识，0，不会签，1会签
     *           customAdmin: 是否显示客户管理员,0否,1是
     *           realLinkType:  真实环节类型(1研判2调度4处置5核实6办结)
     *           preLink: 上一个环节
     *           alarmCode: 告警编码
     *           linkName: 流程环节名称
     *           orgId: 组织id
     *           industryCode: 行业编码
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           tenantName: 租户名称
     *           flowId: 流程ID
     *           alarmFlowId: 流程id
     *           dataFilterPermiss: 是否过滤数据权限（0否,1是）
     *           roleId: 关联角色ID
     *           nickName: 用户昵称
     *           leaderFlag: 转领导批示，1：是，0：否
     *           userName: 用户账号
     *           userId: 用户id
     *           flowName: 流程模板名称
     *           appVerCode: 行业版本编码
     *           nextLink: 下一个环节
     *           timeLimit: 时限，单位分钟
     *           turnFlag: 是否转派标识（0-否，1-是）
     *           linkId: 环节ID
     *           rollbackFlag: 回退标识 0-不回退，1-可回退
     *           flowLinkId: 流程环节主键id
     *           msgNotifyType: 消息通知类型，1：APP,2：短信,3：PC系统消息,4：PC告警声音
     *           linkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     *           userType:  用户类型,2角色,3 网格员
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/queryReassignLink")
    public Result queryReassignLink(@RequestBody TradeQueryReassignLinkDto dto);

    /**
     * 行业查询告警事件处置核实信息
     * @apiNote 查询告警事件处置核实信息接口
     * @apiVersion 1.42
     * @tag 一、告警事件能力
     * @requestDemo {"processOrderId":"315785070878346827"}
     * @responseDemo {"code": 200,"msg": "","time": 1667197473238,"data": {"process": {"voice": "","sendOrderTime": "2022-08-18 09:56:33", //抢单时间 "orgName": "测试202203220114测试长度测试测试长度测试",//部门名称 "mobilePhone": null,//父部门名称 "orderId": "202208141220357973286253",//工单ID
     * "processId": "2c3effd3f53a41698ef8a3bcaedf66b0",//处理人id "processName": "测试客户zbz",//用户昵称 "processOrderId": "315785070878328832",//处理单ID "orderStatus": "1",//处理单状态0处理中 1处理完成 2无法处理 "processOrderTime": "2022-08-18 09:56:56",//处理完成时间 "processOpinion": "Hai "//处理意见},
     * "approve": {"approveStatus": "2",//审批状态 0:审批失败 1:待审批 2:审批通过 "approvePersonName": "测试客户zbz",//用户昵称 "approveResult": "需求检查测试",//审批建议 "approvePerson": "2c3effd3f53a41698ef8a3bcaedf66b0",//审批人 "approveTime": "2022-08-18 15:45:35",//审批时间 "approveId": "315785070878328833"//审批单ID},
     * "files": [{"resourceUrl": "https://obs-test1-zs.obs.cn-north-4.myhuaweicloud.com:443/slyy2/1.0/daf1baf4abc3452757006ba5ed5a7fec/acb0b87546d3184fc9a9c966c3c68775/20f8a48e8d8c4c288a84906116267977.jpg",//资源url "resourceType": "1"//1:photo 2:video}]}}
     * 返回参数data下字段解析：
     *           process:
     *                     voice: 语音
     *                     sendOrderTime: 抢单时间
     *                     orgName: 部门名称
     *                     mobilePhone: 电话号码
     *                     orderId: 工单ID
     *                     processId: 处理人id
     *                     processName:处理人名称
     *                     processOrderId: 处理单ID
     *                     orderStatus: 处理单状态0处理中 1处理完成 2无法处理
     *                     processOrderTime: 处理完成时间
     *                     processOpinion: 处理意见
     *           approve:
     *                     approveStatus: 审批状态 0:审批失败 1:待审批 2:审批通过
     *                     approvePersonName: 用户昵称
     *                     approveResult: 审批建议
     *                     approvePerson: 审批人
     *                     approveTime: 审批时间
     *                     approveId: 审批单ID
     *           files:
     *                     resourceUrl: 资源url
     *                     resourceType: 1:photo 2:video
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getProcessDetail")
    public Result getProcessDetail(@RequestBody ProcessOrderDTO dto);

    /**
     * 批量研判
     */
    @PostMapping("/alarm/orderBatch/batchJudgeWarningOrder")
    public Result batchJudgeWarningOrder(@RequestBody BatchJudgeWarningOrderDTO dto);

    /**
     * 导出告警事件列表
     */
    @PostMapping("/alarm/order/exportWarningOrderListV2")
    Result<SysFileInfoResponseDTO> exportWarningOrderListV2(@RequestBody AlarmOrderQuery dto);

    /**
     * 通过告警工单ID获取处理环节信息
     * @apiNote 通过告警工单ID获取处理环节详细信息接口
     * @apiVersion 1.43
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"120046990","confidenceLevelMin":"","confidenceLevelMax":"","minChar":"100","maxChar":"100","warningOrderIds":["2023081712201469369214030", "2023081712305995169213597", "2023081712006449869213992",…],"alarmTimeStart":"2023-7-18 00:00:00","alarmTimeEnd":"2023-8-17 23:59:59",
     * "provinceId":"110000","cityId":"110100","countyId":"110101","searchType":"0","warningSource":"2","judgeResult:"1","isCollection":"1","warningTypeIdList":[],"deviceCode":[],"lableIdList":[],"isSecondJudge":"1","secondJudgeResult":"0"}
     * @responseDemo {"code": 200,"msg": "操作成功","data":{"total":"20","rows":[{"realLinkType": "1","flowEndTime": "","flowNodeTime": "2022-12-01 09:12:48","remark": "","leaders": [],"linkName": "研判","nextLinkProcessName": "",
     * "linkId": "1","processName": "","dispatchProcessName": "","ifCurrentLink": "1","orderFlowId": "202212121062580000438106","orderFileList": [],"status": "3"}]},"pageNum":"1"}
     * 返回参数data下字段解析：
     *           realLinkType: 真实环节id
     *           flowEndTime: 环节结束时间
     *           flowNodeTime: 当前环节开始时间
     *           remark: 备注
     *           leaders: 领导批示信息
     *           linkName: 流程环节名称
     *           nextLinkProcessName: 下个环节处理人集合
     *           linkId: 当前环节ID
     *           processName: 处理人名称
     *           dispatchProcessName: 调度人名称
     *           ifCurrentLink: 是否当前环节 1:是 0:否
     *           orderFlowId: 工单流程ID
     *           orderFileList: 附件列表
     *           status: 状态
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/order/getRecordByOrderId")
    Result getRecordByOrderId(@RequestBody AlarmOrderQueryDTO dto);

    /**
     * 获取全部告警标签
     * @apiNote 获取全部告警标签接口
     * @apiVersion 1.44
     * @tag 一、告警事件能力
     * @responseDemo {"code":200,"msg":"请求成功","time":1735633771754,"data":[{"lableRelId":null,"lableId":1188648,"lableName":"7-17测试1111","waringOrderId":null,"tableType":null,"tableTime":null}]}
     * 返回参数data下字段解析：
     *           tableType: 表名标识
     *           lableName: 标签名称
     *           tableTime: 表时间
     *           lableRelId: 告警标签关联ID
     *           waringOrderId: 告警事件编码
     *           lableId:标签ID
     * @return Result<List<AlarmLableRelDTO>>
     */
    @GetMapping("alarm/lable/getAllLables")
    public Result<List<AlarmLableRelDTO>> getAllAlarmTags();

    /**
     * 新增告警标签
     * @apiNote 新增告警标签接口
     * @apiVersion 1.45
     * @tag 一、告警事件能力
     * @requestDemo {"lableName":"人工测试"}
     * @responseDemo {"code": 200,"msg": "告警标签新增成功!","time":1679385210118}
     * 返回参数data下字段解析：
     *                操作成功，告警标签新增成功
     * @param addLableDTO 入参
     * @return Result
     */
    @PostMapping("alarm/lable/addLable")
    public Result addTag(@Validated @RequestBody AddLableDTO addLableDTO);

    /**
     * 告警标签修改
     */
    @PostMapping("alarm/lable/modifyLable")
    public Result modifyTag(@Validated @RequestBody ModifyLableDTO modifyTagDTO);

    /**
     * 告警标签删除
     */
    @PostMapping("alarm/lable/deleteLable")
    public Result deleteTag(@Validated @RequestBody DeleteLableDTO deleteLableDTO);

    /**
     * 告警关联标签保存
     */
    @PostMapping("alarm/lable/saveLableRelation")
    public Result saveTagRelation(@Validated @RequestBody AddLableRelationDTO addLableRelationDTO);

    /**
     * 行业告警关联标签保存
     */
    @PostMapping("trade/alarm/lable/saveLableRelation")
    public Result tradeSaveTagRelation(@RequestBody TradAddLableDto tradAddLableDto);

    /**
     * 告警对应标签查询
     * @apiNote 查询告警对应标签接口
     * @apiVersion 1.46
     * @tag 一、告警事件能力
     * @requestDemo {"waringOrderId":"202308011210630161483816"}
     * @responseDemo {"code": 200,"msg": "请求成功!","time":1679385210118,"data":[{"lableRelId":"1189594","lableId":"1188157","lableName":"YH23467","waringOrderId":"202308011210630161483816"}]}
     * 返回参数data下字段解析：
     *           tableType: 表名标识
     *           lableName: 标签名称
     *           tableTime: 表时间
     *           lableRelId: 告警标签关联ID
     *           waringOrderId: 告警事件编码
     *           lableId: 标签ID
     * @param waringOrderId 告警事件编码
     * @return Result<List<AlarmLableRelDTO>>
     */
    @GetMapping("alarm/lable/getLableByAlarm/{waringOrderId}")
    public Result<List<AlarmLableRelDTO>> getAlarmTagsByOrderId(@PathVariable(value="waringOrderId") String waringOrderId);


    /**
     * 查询事件处理常用语
     */
    @PostMapping("commonWords/getProcessCommonWords")
    public Result<List<ProcessCommonWordsDTO>> getProcessCommonWords();

    /**
     * 编辑保存事件处理常用语
     */
    @PostMapping("commonWords/updateProcessCommonWords")
    public Result updateProcessCommonWords(@RequestBody List<ProcessCommonWordsDTO> commonWordsDTOList);

    /**
     * 告警分享新增
     */
    @PostMapping("alarm/share/addShare")
    public Result<AlarmShareDTO> addShare(@Validated @RequestBody AddShareDTO addShareDTO);

    /**
     * 根据位置及租户id行业版本获取网格信息
     * @apiNote 根据位置及租户id行业版本获取网格信息接口
     * @apiVersion 1.47
     * @tag 一、告警事件能力
     * @requestDemo {"longitude": "1143.794872","latitude": "24.192697","deviceLongitude": "121.5146365017361","deviceLatitude": "38.84760932074653"}
     * @responseDemo {"code": 200,"msg": "请求成功!","time":1679385210118,"data":{"gridId":"202306271446361559","tenantName":"tower","industryName":"通用平台","gridCode":"4312220000000","gridName":"网格002","parentId":"202208191808247843","parentName":"",
     * "parentCode":"202208191808247843","gridClassification":"","gridLevel":"1","gridLevelCode":"202308021130197877,202308021133091208","gridProperty":"02","isEnd":"0","gridAddress":"辽宁省大连市","gridIntroduce":"网格介绍","pointStatus":"1","gridEmCenter":"122098991-010901",
     * "gridBeginTime":"2022-10-01 00:00:00","gridEndTime":"2022-10-31 00:00:00","gridArea":"150000","color":"#00b0ff","gridCenter":"[[111.485279,27.257788]]","gridRange":"[111.477168,27.255767]]],...","tenantList":[],"gridIdList":[],"keeperName":"测试客户zbz","keeperId":"202208191435313215",
     * "countyCode":"110108","countyName":"海淀区","cityName":"北京市","cityCode":"110100","incidentDistance":""}}
     * 返回参数data下字段解析：
     *           borderColor: 网格边框颜色
     *           gridName: 网格名称
     *           gridRange: 网格坐标范围
     *           modifier: 修改人
     *           gridEndTime: 终止日期
     *           pageSize: 页长
     *           keeperId: 网格员id
     *           keeperPhotoUrl: 网格照片地址
     *           industryCode: 行业编码
     *           deleteFlag: 删除标识 0正常 1删除
     *           keeperName: 网格员姓名
     *           isLine: 是否在线 1 在线  0 离线
     *           pointStatus: 标绘状态 1 已标绘
     *           gridLevel: 网格层级
     *           gridArea: 面积
     *           gbCode: 行政区划的国标编码
     *           gridBeginTime: 初始日期
     *           gridKeeperName: 网格长姓名
     *           criteriaList: 数据权限mongo条件
     *           isEnd: 最后一级
     *           appVerCode: 行业版本编码
     *           parentName: 上级网格名称
     *           gridIdList: 网格id多选
     *           gridId: 网格id
     *           provinceName: 所在省名称
     *           gridClassification: 网格分类。1基础网格 2环保网格 3防火网格 4城管网格 5执法网格 6安监网格等
     *           incidentDistance: 事发地点距离 单位: 米
     *           platformCode: 节点编码
     *           gridCode: 网格编码
     *           gridAddress: 网格地址
     *           phonePlainText: 手机号码
     *           borderColorLineThickness: 网格边框线条粗细 取值1 2 3 4 5 默认1
     *           color: 网格颜色
     *           keeperPhoneNumber: 手机号码
     *           gridIntroduce: 网格介绍
     *           cityCode: 市编码
     *           gridPicture: 宣传图片
     *           gridCenter: 网格中心坐标
     *           tenantName: 租户名称
     *           cityName: 所在市名称
     *           updateBy: 修改人
     *           parentCode: 父级网格编码
     *           topTreeName: 顶级树节点名称
     *           industryName: 行业应用名称
     *           creator: 创建人
     *           colorTransparency: 网格填充颜色透明度 单位% 取值0-100 默认50
     *           provinceCode: 省份编码
     *           isValid: 是否有效
     *           gridLevelCode: 网格层级编码
     *           tenantList: 租户集合
     *           updateTime: 更新时间
     *           dataScope: 数据权限
     *           userId: 用户id
     *           pageNum: 页码
     *           parentId: 父级网格id
     *           excludeGridId: 列表排除的网格id
     *           gridProperty: 网格属性。01村庄、02小区、03企业、04学校（幼儿园）、05医院、06驻镇（街）单位、07其他
     *           gridSort: 展示顺序
     *           gridEmCenterName: 所属组织
     *           borderColorTransparency: 网格边框颜色透明度 单位% 取值0-1 默认0.5
     *           createBy: 创建人
     *           createTime: 创建时间
     *           tenantId: 租户编码
     *           gridEmCenter: 组织编码
     *           reginScopeDesc: 地图区域范围
     * @param req 入参
     * @return Result
     */
    @PostMapping("alarm/order/getGridInfoByLongitudeAndLatitude")
    public Result getGridInfoByLongitudeAndLatitude(@RequestBody GridInfoReq req);

    /**
     * 告警事件受理
     * @apiNote 告警事件受理接口
     * @apiVersion 1.48
     * @tag 一、告警事件能力
     * @requestDemo {"warningOrderId":"2023081612209782868940288","linkId":"1","linkName":"研判1","flowId":"9354","timeLimit": null,
     * "address":"北京市海淀区紫竹院街道","orderStatus":"1","warningTypeId":"1212116","appVerCode":"1.0","industryCode":"000000","alarmTime":"2023-08-08 14:45:23"}
     * @responseDemo {"code": 200,"msg": "操作成功!","time":1679385210118}
     *返回参数data下字段解析：
     *                操作成功
     * @param dto 入参
     * @return Result
     */
    @PostMapping("alarm/order/accept")
    public Result accept(@RequestBody TradeAcceptOrderDto dto);

    /**
     * 新增人工上报/一键告警
     * @apiNote 新增人工上报/一键告警接口
     * @apiVersion 1.49
     * @tag 一、告警事件能力
     * @requestDemo {"warningSource":"3","alarmBody":"扬尘粉尘监控告警","warningTypeId":"1010105","emergencyLevel":"","alarmTime":"2023-06-07 14:08:28","provinceId":"110000","cityId":"110100","countyId":"110108","address":"北京市北京市海淀区四季青镇常润路中国铁塔产业园","longitude":"116.266446","latitude":"39.956909","description":"发生扬尘粉尘监控告警123","remark":"","imgs":[{"uid":1686118106211,"type":"image/png","name":"铁塔产业园区-车库入口_0-20230607140825.png",
     * "url":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/pre/1.0/5198cd91c73f929d0547ad6cd49038ff/289ea5add1b93e70854e7b23137e1d5c/c5b8d89546fc4dca8ecd65874d91432d.png?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1686118709&Signature=xh2x2DBXH6I5sfhVySF5vjr/mfU%3D",
     * "fileName":"铁塔产业园区-车库入口_0-20230607140825.png","tagPictureUrl":"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/pre/1.0/5198cd91c73f929d0547ad6cd49038ff/289ea5add1b93e70854e7b23137e1d5c/c5b8d89546fc4dca8ecd65874d91432d.png?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1686118709&Signature=xh2x2DBXH6I5sfhVySF5vjr/mfU%3D","status":"success"}],
     * "videos":[],"deviceCode":"11010800831327000004","deviceName":"铁塔产业园区-车库入口","channelCode":"11010800831310000003","channelName":"铁塔产业园区-车库入口_0","importanceLevel":"1"}
     * @responseDemo {"code":200,"msg":"保存成功","time":1685585958773}
     * 返回参数data下字段解析：
     *                操作成功，保存成功
     * @param receiveAlarmVO 入参
     * @return Result
     */
    @PostMapping("alarm/orderForTrade/addAlarmOrder")
    public Result addAlarmOrder(@RequestBody ReceiveAlarmVO receiveAlarmVO);

    /**
     * 第三方告警上报
     */
    @PostMapping("alarm/order/addThirdAlarmOrder")
    public Result addThirdAlarmOrder(@RequestBody List<ReceiveAlarmVO> receiveAlarmVO);

    /**
     * 根据租户按照经纬度递归查询告警数量信息
     * @apiNote 根据租户按照经纬度递归查询告警数量接口
     * @apiVersion 1.50
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"121051052","cityId":"620700","longitude":"116.266446","latitude":"39.956909","webStartDate":"2023-06-07","webEndDate":"2023-06-20","flag":1,"orderStatus":"1","provinceId":"","countyId":"",
     * "deviceCode":["62072500831327000046"],"enableAcceptanceMode":"1","warningSourceList":["1","3"],"orderStatusList":["2","4"],"warningTypeIdList":["1010119","1010120"]}
     * @responseDemo {"code":200,"msg": "请求成功","time": 1648886714765,"data":{"list":[{"longitude":"101.068241521","latitude":"38.266430687","num":"352"}],"cityId":"620700"}}
     * 返回参数data下字段解析：
     *           latitude: 纬度
     *           num: 数量
     *           longitude: 经度
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/order/searchOrderNumByLongitude4Industry")
    Result searchOrderNumByLongitude4Industry(@RequestBody SearchOrderDTO dto);

    /**
     * 根据经纬度递归查询告警信息列表
     * @apiNote 根据经纬度递归查询告警信息列表接口
     * @apiVersion 1.51
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"121035797","warningOrderId":"2023081612209782868940288","warningSource":"3","alarmBody":"扬尘粉尘监控告警","warningTypeId":"1010105","emergencyLevel":"","webStartDate":"2023-06-07","webEndDate":"2023-06-20","pageNum":1,"pageSize":1,"roleId":"[object Object]"
     * "orderStatus":"1","longitude":"116.266446","latitude":"39.956909","emergencyLevel":"","searchType":"0","deviceCode":"62072500831327000046","deviceName":"铁塔产业园区-车库入口","channelCode":"11010800831310000003","channelName":"铁塔产业园区-车库入口_0","enableAcceptanceMode":"1","lableIdList":[],"warningOrderIds":[],"importanceLevel":"1"}
     * @responseDemo {"total":10,"code":200,"rows":[{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625","address":"甘肃省张掖市山丹县中牧公司山丹马场358乡道",
     * "provinceId":"110000","cityId":"110100","countyId":"110101","orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","warningSource": "1","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052",
     * "latitude": "28.049137465573217","deviceCode":"62072500831327000046","deviceName":"铁塔产业园区-车库入口","channelCode":"11010800831310000003","channelName":"铁塔产业园区-车库入口_0","processId":"925e53ea88f74cd0a613c70045aae74e","realLinkType":"1","flowId":"15177","linkId":"1","timeLimit":1800,"enableAcceptanceMode":"0"}]}
     * 返回参数data下字段解析：
     *           alarmBody: 告警内容
     *           confidenceLevel: 置信度
     *           warningTypeName: 告警类型名称
     *           realLinkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     *           orderId: 告警单ID(yyyyMM+租户id+)
     *           latitude: 纬度
     *           cityId: 市编码
     *           deviceName: 设备名称
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           duration: 处理时长
     *           orderStatusName: 工单状态名称
     *           tenantName: 租户名称
     *           processId: 处理人
     *           countyId: 县编码
     *           enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *           flowId: 流程ID
     *           longitude: 经度
     *           channelCode: 通道编码
     *           warningSourceName: 告警来源名称
     *           address: 详细地址
     *           importanceLevel: 告警重要等级
     *           alarmTime: 告警时间
     *           deviceCode: 设备编码
     *           provinceId: 省编码
     *           timeLimit: 时限，单位分钟
     *           linkId: 环节ID
     *           tenantId: 租户编码
     *           channelName: 摄像机通道名称
     *           compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping("/alarm/order/searchOrderListByLongitude")
    TableDataInfo searchOrderListByLongitude(@RequestBody BigScreenOrderListDTO dto);

    /**
     * 根据设备编码集合查询设备是否有未完结告警
     * @apiNote 地图打点：根据设备编码集合查询设备是否有未完结告警
     * @apiVersion 1.52
     * @tag 一、告警事件能力
     * @requestDemo {"deviceCodes":["11010800831327000003", "43100300003007000001",…],"alarmTimeStart":"2025-01-01 00:00:00","alarmTimeEnd":"2025-01-07 23:59:59"}
     * @responseDemo {"code":200,"msg": "请求成功","time": 1648886714765,"data":[{"deviceCode":"11010800831327000003","isCompleteFlag":"1"}, {"deviceCode":"43100300003007000001","isCompleteFlag":"0"}]
     * 返回参数data下字段解析：
     *           deviceCode: 设备编码
     *           isCompleteFlag: 是否有未完结告警标识（0-否、1-是）
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/order/getOrderNumByDeviceCode")
    Result<List<OrderCompleteVO>> getOrderNumByDeviceCode(@RequestBody AlarmOrderParmaDto dto);

    /**
     * 国土SDk：大屏右侧，获取摄像机关联的告警事件列表
     * @apiNote 查询摄像机关联的告警事件列表接口
     * @apiVersion 1.53
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"121035797","warningOrderId":"2023081612209782868940288","warningSource":"3","alarmBody":"扬尘粉尘监控告警","warningTypeId":"1010105","emergencyLevel":"","webStartDate":"2023-06-07","webEndDate":"2023-06-20","pageNum":1,"pageSize":1,"roleId":"[object Object]"
     * "orderStatus":"1","longitude":"116.266446","latitude":"39.956909","emergencyLevel":"","searchType":"0","deviceCode":"62072500831327000046","deviceName":"铁塔产业园区-车库入口","channelCode":"11010800831310000003","channelName":"铁塔产业园区-车库入口_0","enableAcceptanceMode":"1","lableIdList":[],"warningOrderIds":[],"importanceLevel":"1"}
     * @responseDemo {"total":10,"code":200,"rows":[{"tenantId": "122098991","tenantName": "怀化市林业局","orderId": "2023081612209899168861781","alarmBody": "烟火告警","warningTypeName": "烟火侦测（可见光）告警","compConfidenceLevel": "0.8625","address":"甘肃省张掖市山丹县中牧公司山丹马场358乡道",
     * "provinceId":"110000","cityId":"110100","countyId":"110101","orderStatusName": "待研判","alarmTime": "2023-08-16 11:34:44","warningSource": "1","warningSourceName": "AI告警","duration": "0小时0分2秒","importanceLevel": "1","confidenceLevel": "0.8625","longitude": "110.76697665449052",
     * "latitude": "28.049137465573217","deviceCode":"62072500831327000046","deviceName":"铁塔产业园区-车库入口","channelCode":"11010800831310000003","channelName":"铁塔产业园区-车库入口_0","processId":"925e53ea88f74cd0a613c70045aae74e","realLinkType":"1","flowId":"15177","linkId":"1","timeLimit":1800,"enableAcceptanceMode":"0"}]}
     * 返回参数rows下字段解析：
     * 		orderId: 告警编码
     * 		taskId: 任务id
     * 		alarmBody: 告警标题
     * 		warningSource: 告警来源
     * 		warningSourceName: 告警来源名称
     * 		warningTypeId: 告警类型编号
     * 		warningTypeName: 告警类型名称
     * 		flowNodeTime: 当前环节开始时间
     * 		cityId: 市编码
     * 		deviceName: 设备名称
     * 		industryCode: 行业编码
     * 		deleteFlag: 删除标识（0-正常、1-删除）
     * 		orderStatus: 告警状态
     * 		orderStatusName: 告警状态名称
     * 		realLinkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     * 		emergencyLevel: 事件等级
     * 		enableAcceptanceMode: 是否开始模式及受理状态（0：不开启 1：开启（待受理）2：已受理 3：受理完成）
     * 		longitude: 经度
     * 		isHandleDisplay: 是否显示督办提示图标（0-否，1-是）
     * 		orgName: 组织名称
     * 		alarmLableRelDTOList: [
     * 			lableRelId：告警标签关联ID
     * 			lableId：标签ID
     * 			lableName：标签名称
     * 			waringOrderId：告警编码
     * 			tableType：表名标识
     * 			tableTime：表时间
     * 		],
     * 		importanceLevel: 告警重要等级
     * 		importanceLevelName: 告警重要等级名称
     * 		showTime: 超期时长
     * 		ifEdit: 是否可编辑（0否、1是）
     * 		provinceId: 省编码
     * 		ifTimeout: 超期（1已超期、2未超期）
     * 		gridId: 网格ID
     * 		gridName: 网格名称
     * 		provinceName: 省名称
     * 		latitude: 纬度
     * 		flowTemplate: 流程模板配置
     * 		icon: 图标
     * 		isCollection: 是否收藏（1:收藏 0：未收藏）
     * 		beforeDispatchFlag: 是否指派人（0：否 1：是）
     * 		moreNextFlag: 下个环节是否多个标记为（0：不是  1：是）
     * 		duration: 处理时长 单位：秒
     * 		modifierDate: 修改时间
     * 		noticeResource: 通知类型（1-调度通知；2-设备端通知）
     * 		cityName: 市名称
     * 		tenantName: 租户名称
     * 		videoUrl: 视频url
     * 		processId: 处理人
     * 		countyId: 县编码
     * 		countyName: 县名称
     * 		emergencyLevelName: 事件等级名称,
     * 		flowId: 流程id
     * 		channelCode: 通道编码
     * 		channelName: 通道名称
     * 		createDate: 创建时间
     * 		address: 事发地点
     * 		fileImgUrlIcon: 框图
     * 		isSecondJudge: 是否二次研判 1-是；0-否
     * 		alarmTime: 告警时间
     * 		leaderNum: 领导批示数量
     * 		timeLimitAcceptance: 受理时限（秒）
     * 		deviceCode: 设备编号
     * 		fileImgUrlSrc: 原图
     * 		imgUrl: 图片url
     * 		timeLimit: 完成时限;单位：分钟
     * 		noticeResourceName: AI告警来源（设备告警/设备告警（二次研判为假）/设备告警（二次研判为真）/算法告警） 通知类型 1-设备告警；2-算法告警
     * 		linkId: 当前环节ID
     * 		tenantId: 租户编码
     * 		reciveTime: 接收时间
     * 		confidenceLevel: 置信度
     * 		compConfidenceLevel: 综合置信度
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping("/alarm/order/getAlarmListForBigScreen")
    TableDataInfo getAlarmListForBigScreen(@RequestBody BigScreenOrderListDTO dto);

    /**
     * 国土SDk：根据通道编码&预置位查询工单列表
     * @apiNote 根据通道编码&预置位查询工单列表接口
     * @apiVersion 1.54
     * @tag 一、告警事件能力
     * @requestDemo {"presetId":"121035797","alarmTimeStart":"2023-06-07","alarmTimeEnd":"2023-06-20","flag":1,"orderStatus":"1",
     * "deviceCode":"62072500831327000046","channelCode":"11010800831310000003","enableAcceptanceMode":"1","orderStatusList":[""1,"3"],"warningTypeIdList":["1010119","1010120"]}
     * @responseDemo {"total":10,"code":200,"rows":[{"orderId": "2023081612209899168861781","alarmTime": "2023-08-16 11:34:44","alarmBody": "烟火告警","warningSource": "1","deviceCode":"62072500831327000046","warningTypeId":"1010119","warningTypeName": "烟火侦测（可见光）告警","warningSource": "1",
     * "orderStatus":"1","orderStatusName": "待研判","warningSourceName": "AI告警","enableAcceptanceMode":"0","frontAlarmPicUrl":"","frontAlarmVedioUrl":"","fileImgUrlSrc":"",
     * "fileImgUrlIcon":"https://slw-base-video.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1010119001_772bfd2448424c4c9608adb05ddc8e7cad7466ab93c044bba686ebd981e754c3_02_001.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1692326878&Signature=wtEnIXwyu6QTw/oWndbAWptaObU%3D"}]}
     * 返回参数rows下字段解析：
     * 		orderId：工单号
     * 		alarmBody: 告警标题
     * 		fileImgUrlIcon: 框图
     * 		fileImgUrlSrc: 原图
     * 		videoUrl: 视频url
     * 		frontAlarmPicUrl：前端告警图片
     * 		frontAlarmVedioUrl：前端告警视频
     * 		warningTypeId: 告警类型编号
     * 		warningTypeName: 告警类型名称
     * 		alarmTime: 告警时间
     * 		orderStatus: 告警状态
     * 		orderStatusName: 告警状态名称
     * 		processId: 处理人
     * 		ifEdit: 是否可编辑（0否、1是）
     * 		deviceCode: 设备编号
     * 		warningSource: 告警来源
     * 		warningSourceName: 告警来源名称
     * 		enableAcceptanceMode: 是否开始模式及受理状态（0：不开启 1：开启（待受理）2：已受理 3：受理完成）
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping("/alarm/order/getOrderListByDeviceChannelAndPresetId")
    TableDataInfo getOrderListByDeviceChannelAndPresetId(@RequestBody AlarmOrderParmaDto dto);

    /**
     * 国土SDK：工具箱-告警筛选，根据租户按照经纬度查询告警信息
     * @apiNote 告警筛选，根据租户按照经纬度查询告警信息接口
     * @apiVersion 1.55
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"121051052","cityId":"620700","longitude":"116.266446","latitude":"39.956909","webStartDate":"2023-06-07","webEndDate":"2023-06-20","flag":1,"orderStatus":"1","provinceId":"","countyId":"",
     * "deviceCode":["62072500831327000046"],"enableAcceptanceMode":"1","warningSourceList":["1","3"],"orderStatusList":["2","4"],"warningTypeIdList":["1010119","1010120"]}
     * @responseDemo {"code":200,"msg": "请求成功","time": 1648886714765,"data":{"list":[{"longitude":"101.068241521","latitude":"38.266430687","num":"352"}],"cityId":"620700"}}
     * 返回参数data下字段解析：
     *      cityId: 市编码
     * 		list: [{
     * 			longitude：经度
     * 			latitude：纬度
     * 			waringOrderId：告警编码
     * 			num：告警数量
     *      }]
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/order/searchOrderNumByLongitude")
    Result searchOrderNumByLongitude(@RequestBody SearchOrderDTO dto);

    /**
     * 国土SDK：工具箱-告警热力图，根据时间范围获取告警事件总数
     * @apiNote 告警热力图-根据时间范围获取告警事件总数接口
     * @apiVersion 1.56
     * @tag 一、告警事件能力
     * @requestDemo {"alarmTimeStart":"2023-06-07 00:00:00","alarmTimeEnd":"2023-06-20 23:59:59"}
     * @responseDemo {"total":3284,"code":200,"rows": null}
     * 返回参数total 字段解析：
     *      total：指定时间范围下的告警总数量
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping("/alarm/order/getAlarmListCount")
    TableDataInfo getAlarmListCount(@RequestBody BigScreenOrderListDTO dto);

    /**
     * 国土SDK：工具箱-告警热力图，根据时间范围、分页获取经纬度列表
     * @apiNote 告警热力图-根据时间范围、分页获取经纬度列表接口
     * @apiVersion 1.57
     * @tag 一、告警事件能力
     * @requestDemo {"alarmTimeStart":"2023-06-07 00:00:00","alarmTimeEnd":"2023-06-20 23:59:59","pageNum":1,"pageSize":10}
     * @responseDemo {"total":10,"code":200,"rows":[{"longitude":"101.068241521","latitude":"38.266430687","warningOrderId":"202308071220358071502460"}]}
     * 返回参数rows下字段解析：
     * 			longitude：经度
     * 			latitude：纬度
     * 			waringOrderId：告警编码
     * @param dto 入参
     * @return TableDataInfo
     */
    @PostMapping("/alarm/order/getOrderLongitudeList")
    TableDataInfo getOrderLongitudeList(@RequestBody BigScreenOrderListDTO dto);

    /**
     * 林业SDK：卡口大屏，按照卡口设备分组，查询告警数量排行
     * @apiNote 卡口大屏，按照卡口设备分组，查询告警数量排行
     * @apiVersion 1.58
     * @tag 一、告警事件能力
     * @requestDemo {
     *     "alarmTimeStart": "2022-12-01",
     *     "alarmTimeEnd": "2023-12-31",
     *     "orderBy":"desc",
     *     "provinceCode":"430000",
     *     "cityCode":"430600",
     *     "townCode":"370831002000"
     * }
     * @responseDemo {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1703748401432,
     * 	"data": [
     *                {
     * 			"deviceCode": "11010800831327000008",
     * 			"deviceName": "铁塔产业园-11号楼-大来咯来咯华243",
     * 			"count": "4"
     *        }
     * 	]
     * }
     * 返回参数data下字段解析：
     * 		deviceCode: 设备编号
     * 		deviceName: 设备名称
     * 	    count: 卡口告警数量
     * @param dto 入参
     * @return CheckAlarmByDeviceVO
     */
    @PostMapping("/alarm/checkpoint/getCheckAlarmByDevice")
    Result<List<CheckAlarmByDeviceVO>> getCheckAlarmByDevice(@RequestBody CheckAlarmByDeviceVO dto);

    /**
     * 乡镇SDK：按照批量的设备编码、时间范围、批量的车牌号，根据设备编码、时间（到日）、以及车牌号，分组统计次数，并返回对应的过车记录次数
     * @apiNote 按照批量的设备编码、时间范围、批量的车牌号，根据设备编码、时间（到日）、以及车牌号，分组统计次数，并返回对应的过车记录次数
     * @apiVersion 1.59
     * @tag 一、告警事件能力
     * @requestDemo {
     *     "deviceCodes": [
     *         "42010300011327000001"
     *     ],
     *     "queryStartTime": "2022-05-24",
     *     "queryEndTime": "2022-08-22",
     *     "vehicleNumbers": [
     *         "京A12345"，"京A45678"
     *
     * @responseDemo {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715166388551,
     *     "data": [
     *         {
     *             "deviceCode": "42010300011327000001",
     *             "count": 1,
     *             "vehicleNumber": "京A12345",
     *             "time": "2022-05-24"
     *         },
     *         {
     *             "deviceCode": "42010300011327000001",
     *             "count": 2,
     *             "vehicleNumber": "京A45678",
     *             "time": "2022-05-24"
     *         }
     *     ]
     * }
     * 返回参数data下字段解析：
     * 		deviceCode: 设备编号
     * 	    count: 告警数量
     * 	    vehicleNumber: 车牌号
     * 	    time: 告警时间
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getCheckpointAlarmInfo")
    Result getCheckpointAlarmInfo(@RequestBody CheckpointAlarmDTO dto);

    /**
     * 根据工单号获取对应得标签和收藏状态
     * @apiNote 根据工单号获取对应得标签和收藏状态
     * @apiVersion 1.60
     * @tag 一、告警事件能力
     * @requestDemo{"orderIds":[ "202404171200469902829616","202405071200469903011658"]}
     * @responseDemo{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1715143472091,
     * 	"data": {
     * 		"resList": [
     *                        {
     * 				"orderId": "202404171200469902829616",//事件编号
     * 				"labelList": [],//告警标签对象
     * 				"status": "1"//是否收藏（0：否  1：是）
     *            },
     *            {
     * 				"orderId": "202405071200469903011658",//事件编号
     * 				"labelList": [
     *                    {
     * 						"labelId": "1188640",//标签id
     * 						"labelName": "测试数据26"//标签名称
     *                    },
     *                    {
     * 						"labelId": "1188644",,//标签id
     * 						"labelName": "7-1添加11111"//标签名称
     *                    }
     * 				],
     * 				"status": "1"//是否收藏（0：否  1：是）
     *            }
     * 		]
     * 	}
     * }
     * @param req
     * @return
     */
    @PostMapping(value = "/alarm/orderForTrade/getLabelAndColStatusByOrders")
    Result getLabelAndColStatusByOrders(@RequestBody LabelAndColStatusReq req);


    @PostMapping(value = "/alarm/intercept/getInterceptInfoApi")
    Result<Map> getInterceptInfoApi(@RequestBody ExportOrderInfoReq dto);

    /**
     * 回填告警表当前环节相关字段
     * @apiNote 回填告警表当前环节相关字段
     * @apiVersion 1.61
     * @tag 一、告警事件能力
     * @requestDemo {"alarms":[{"acceptNode":"10000000001","accessNode":"35000000031","address":"福建省三明市明溪胡坊镇柏亨村","alarmBody":"烟雾告警","alarmCode":"b9cf5358-d7c1-40d1-9a59-8fc8da79ecf0","alarmNode":"10000000001",
     * "alarmSource":"4","alarmTime":"2025-02-19 10:28:37","alarmType":"1818119","algorithmCode":"","appVerCode":"1.0","channelCode":"35042100001130000015","channelName":"三明明溪明溪胡坊镇柏亨_0","cityCode":"350400","cityName":"三明市",
     * "countyCode":"350421","countyName":"明溪县","deviceCode":"35042100111327000007","deviceName":"LYJ-三明明溪明溪胡坊镇柏亨","emergencyLevel":"01","flowNodeTime":"2025-02-19 10:28:37","imgs":[],"industryCode":"100000","isOperation":"2",
     * "isSecondJudge":"0","judgeType":"2","latitude":"26.178199000","localNode":"10000000001","longitude":"117.190679000","notifyType":"1","operationAlarmSource":"-1","operationNode":"10000000001","operationNodeName":"中心节点","operationType":"2",
     * "pan":"13334","parallelAlarmTypeStr":"","parallelOperationNode":"10000000001","provinceCode":"350000","provinceName":"福建省","tenantId":"123076169","tenantName":"福建尚康科技有限公司","tilt":"13685","townCode":"350421102000","townName":"胡坊镇",
     * "warningOrderId":"20250219123076169236136911","warningSource":"4","warningTypeId":"1818119","zoom":"16"}],"recordList":[{"acceptNode":"10000000001","accessNode":"35000000031","address":"福建省三明市将乐高唐箭竹山","alarmBody":"烟雾告警","alarmCode":"bcd99487-9339-450a-8f36-56a23f7064ec",
     * "alarmNode":"10000000001","alarmSource":"4","alarmTime":"2025-02-19 10:20:16","alarmType":"1818119","algorithmCode":"","appVerCode":"1.0","channelCode":"35042800001130000027",
     * "channelName":"三明将乐高唐镇高唐村_0","cityCode":"350400","cityName":"三明市","countyCode":"350428","countyName":"将乐县","deviceCode":"35042800111327000017","deviceName":"LYJ-三明将乐高唐镇高唐村",
     * "emergencyLevel":"01","flowNodeTime":"2025-02-19 10:20:17","imgs":[],"industryCode":"100000","isOperation":"2","isSecondJudge":"0","judgeType":"2","latitude":"26.806022000","localNode":"10000000001",
     * "longitude":"117.585005000","notifyType":"1","operationAlarmSource":"-1","operationNode":"10000000001","operationNodeName":"中心节点","operationType":"2","pan":"4758","parallelAlarmTypeStr":"",
     * "parallelOperationNode":"10000000001","provinceCode":"350000","provinceName":"福建省","tenantId":"123076169","tenantName":"福建尚康科技有限公司","tilt":"22","townCode":"350428102000","townName":"高唐镇",
     * "warningOrderId":"20250219123076169236136403","warningSource":"4","warningTypeId":"1818119","zoom":"16"}]}
     * @param req
     */
    @PostMapping("/alarm/order/setWarningLinkInfo")
    void setWarningLinkInfo(@RequestBody WarningLinkInfoReq req);

    /**
     * 创建流程
     * @apiNote 创建流程
     * @apiVersion 1.62
     * @tag 一、告警事件能力
     * @requestDemo [{"acceptNode":"10000000001","accessNode":"35000000031","address":"福建省三明市将乐高唐箭竹山","alarmBody":"烟雾告警","alarmCode":"bcd99487-9339-450a-8f36-56a23f7064ec",
     * "alarmNode":"10000000001","alarmSource":"4","alarmTime":"2025-02-19 10:20:16","alarmType":"1818119","algorithmCode":"","appVerCode":"1.0","channelCode":"35042800001130000027",
     * "channelName":"三明将乐高唐镇高唐村_0","cityCode":"350400","cityName":"三明市","countyCode":"350428","countyName":"将乐县","deviceCode":"35042800111327000017","deviceName":"LYJ-三明将乐高唐镇高唐村",
     * "emergencyLevel":"01","flowNodeTime":"2025-02-19 10:20:17","imgs":[],"industryCode":"100000","isOperation":"2","isSecondJudge":"0","judgeType":"2","latitude":"26.806022000","localNode":"10000000001",
     * "longitude":"117.585005000","notifyType":"1","operationAlarmSource":"-1","operationNode":"10000000001","operationNodeName":"中心节点","operationType":"2","pan":"4758","parallelAlarmTypeStr":"",
     * "parallelOperationNode":"10000000001","provinceCode":"350000","provinceName":"福建省","tenantId":"123076169","tenantName":"福建尚康科技有限公司","tilt":"22","townCode":"350428102000","townName":"高唐镇",
     * "warningOrderId":"20250219123076169236136403","warningSource":"4","warningTypeId":"1818119","zoom":"16"}]
     * @responseDemo [{"tenantId":"123048327","flowId":"1001","linkId":"1","linkName":"研判1","orderStatus":"1","flowNodeTime": "2025-02-19 10:28:18","roleId": ",4,,","groupId":"","userType": "2","msgNotifyType": "1,3,4","appVerCode": "1.0","alarmType": "1010100","industryCode": "400001","realLinkType": "1"}]
     * 返回参数data下字段解析：
     *          tenantId: 租户编码
     *          flowId: 流程id
     *          linkId: 环节id
     *          linkName: 环节名称
     *          orderStatus: 告警状态
     *          flowNodeTime: 当前环节开始时间
     *          roleId: 默认关联角色ID，多个角色中间用逗号隔开
     *          groupId: 小组id，组织编码
     *          userType: 用户类型,2角色,3 网格员
     *          msgNotifyType: 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     *          appVerCode: 应用版本
     *          alarmType: 告警类型
     *          industryCode: 行业编码
     *          realLinkType: 环节类型(1研判2调度4处置5核实6办结7开始)
     * @param customers
     * @return List<Map>
     */
    @PostMapping("/alarm/order/createRecord")
    List<Map> createRecord(@RequestBody List<ReceiveAlarmVO> customers);

    /**
     * 告警推送第三方
     * @apiNote 告警推送第三方
     * @apiVersion 1.63
     * @tag 一、告警事件能力
     * @requestDemo {"alarmList":[{"acceptNode":"10000000001","accessNode":"10000000001","address":"河北省沧州市黄骅市新村回族乡场一路","alarmArea":[{"alarmAreaId":"232"}],"alarmBody":"电子围栏闯入",
     * "alarmCode":"2cd624cdf8dc43769764472f177ed5366704b51c00794256b39ad90d0f08415b","alarmNode":"10000000001","alarmSource":"10","alarmTime":"2025-02-19 09:52:08","alarmType":"2121100","algorithmCode":"0",
     * "appVerCode":"1.0","beforeDispatchFlag":"1","channelCode":"","channelName":"","cityCode":"130900","cityName":"沧州市","countyCode":"130983","countyName":"黄骅市","currentLinkId":"1","currentLinkName":"研判1",
     * "deviceCode":"","deviceName":"","emergencyLevel":"01","enableAcceptanceMode":"0","extendField":{"msg":[],"radarCodes":["771","842"],"targetName":"JIN YUAN 28","targetId":"2502189069100068995","sClass":"AIS_A",
     * "targetType":"电子围栏闯入","alarmName":"闯入-多边形区域:17397748256288wu9u","speed":10.4,"MMSI":413542070,"areaName":"渤海告警区域:173977482551729hc3:1739774825832m93of","targetLength":179.0,"shipType":2,"alarmId":"1891378821320544256","course":58.2},
     * "extraField":"{\"msg\":[],\"radarCodes\":[\"771\",\"842\"],\"targetName\":\"JIN YUAN 28\",\"targetId\":\"2502189069100068995\",\"sClass\":\"AIS_A\",\"targetType\":\"电子围栏闯入\",\"alarmName\":\"闯入-多边形区域:17397748256288wu9u\",\"speed\":10.4,
     * \"MMSI\":413542070,\"areaName\":\"渤海告警区域:173977482551729hc3:1739774825832m93of\",\"targetLength\":179.0,\"shipType\":2,\"alarmId\":\"1891378821320544256\",\"course\":58.2}","flowId":"315785070878329067","flowName":"研判1","flowNodeTime":"2025-02-19 10:02:24",
     * "flowTemplate":"315785070878329067","imgs":[],"importanceLevel":"1","industryCode":"300000","judgeType":"0","latitude":"38.3680004","linkId":"1","linkName":"研判1","localNode":"10000000001","longitude":"117.88891724","msgNotifyType":"3","notifyAdminFlag":"null",
     * "notifyType":"1","operationNodeName":"中心节点","orderStatus":"1","processId":"","provinceCode":"130000","provinceName":"河北省","realLinkType":"1","roleId":",4,,","taskId":"1739774825878e0m4k","tenantId":"122035807","tenantName":"测试新增客0323","timeLimit":"1800",
     * "townCode":"130983206000","townName":"新村回族乡","userType":"2","warningOrderId":"202502191220358072534247","warningSource":"10","warningTypeId":"2121100"}],"originalJson":"null"}
     * @responseDemo void
     * @param req
     */
    @PostMapping("/alarm/order/pushThirdOrder")
    void pushThirdOrder(@RequestBody PushThirdReq req);

    /**
     * 人工上报消息推送
     * @apiVersion 1.64
     * @tag 一、告警事件能力
     * @requestDemo {"address":"福建省龙岩市新罗区西陂街道金龙中路","alarmFlowId":"15366","alarmTime":"2025-02-19 09:22:42","appVerCode":"1.0","creator":"8b449c8cddee47c9b71d1cfe89590c78","enableAcceptanceMode":"0","flowFlag":"1","flowOverTime":"2025-02-19 09:24:46",
     * "flowTemplate":"1004","industryCode":"000000","linkId":"4","msgId":"20250219123091516236132781","msgNotifyType":"3,4,2","notifyTitle":"烟火告警","notifyType":"1","orderId":"20250219123091516236132781","orderStatus":"4","realLinkType":"4","roleId":"5864256894057895755",
     * "tenantId":"123091516","typeValue":"null","userId":"0b7e9845878946b6a7ff4f6792a58902,15831ab2638145d98490cb297810c809,2ce4deac89a844259000084dc313845f,62078e940abf4a9eb5789dfff2c5cf95,6c91dfacbb174a169e82c7537db30201,7de58c1e082445bcb5770515d2a3a3ed,
     * cce84bed317e4e3fab3f23c5ee01a1e2,fc041fa65d0e4e67a48b37dec1b22601","userType":"2","warningTypeId":"1010100"}
     * @responseDemo void
     * @param vo
     */
    @PostMapping("/alarm/order/messagePush")
    void messagePush(@RequestBody MessagePushVo vo);



    /**
     * 针对告警来源为“AI告警”
     *  在“事件描述”字段中展示相应算法配置的参数
     *  （即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
     *
     * @apiNote 针对告警来源为“AI告警” 在“事件描述”字段中展示相应算法配置的参数
     * @apiVersion 1.65
     * @tag 一、告警事件能力
     *
     * @requestDemo obj 传入参数 格式
     *                       [{
     *                          "warningOrderId": "", //工单编号
     *                          "deviceCode": "",   //摄像机编号
     *                          "channelCode": "",  //通道编号
     *                          "taskId": "",       //任务ID
     *                          "algorithmCode": "", //算法编码
     *                          "warningSource" : "" //告警类型
     *                       }]
     *
     *
     * @retrun 返回map key->warningOrderId
     *                value->拼装好的描述
     */
    @PostMapping(value = "/alarm/order/buildAlgorithmConfigDescFieldMap")
    Result<Map<String, String>> buildAlgorithmConfigDescFieldMap(@RequestBody AiAlgorithmConfigReq req);

    /**
     * 返回所属组织
     * @apiNote 返回所属组织
     * @apiVersion 1.66
     * @tag 一、告警事件能力
     * @requestDemo [{"tenantId":"122035797","industryCode":"000000","deviceCode":"11111010101112011","findName":"weichuang001","provinceId": "110000","cityId": "110100","countyId": "110105"}]
     * @responseDemo [{"202502191221122192534241":"信阳市疾病预防控制中心_备电-地灾智保版本-1.0, 重庆"},{"202502191221122192534243":"信阳市疾病预防控制中心_备电-地灾智保版本-1.0, 重庆"}]
     * 返回参数data下字段解析：
     *              工单号: 所属组织
     * @param reqList
     * @return List<Map<String, String>>
     */
    @PostMapping("/alarm/order/getOrgInfoList")
    List<Map<String, String>> getOrgInfoList(@RequestBody List<OrganInfoReq> reqList);

    /**
     * 新增告警关联标签
     */
    @PostMapping("alarm/lable/addNewTagRelation")
    public Result addNewTagRelation(@Validated @RequestBody AddLableRelationDTO addLableRelationDTO);

    /**
     * 根据时间查询该租户行业下产生过告警的告警类型
     * @apiNote 根据时间查询该租户行业下产生过告警的告警类型
     * @apiVersion 1.69
     * @tag 一、告警事件能力
     * @requestDemo {"alarmTimeStart": "2025-10-01 00:00:00","alarmTimeEnd": "2025-10-31 23:59:59"}
     * @responseDemo [{"typeId": "1010100"},{"typeId": "1010104"}]
     * 返回参数data下字段解析：
     *              typeId：告警类型编码
     * @param dto
     * @return List<Map<String, String>>
     */
    @PostMapping("/alarm/order/findAlarmTypesByTime")
    List<String> findAlarmTypesByTime(@RequestBody TradeAlarmOrderDTO dto);

    /**
     * 查询车辆进出记录客户视角
     * @apiNote 查询车辆进出记录客户视角
     * @apiVersion 1.67
     * @tag 一、告警事件能力
     * @requestDemo {
     *   "vehicleNumber": "黑C0724C",
     *   "vehicleType": "1",
     *   "deviceName": "实验室-紫光-国标231",
     *   "deviceCode": "11010800011327000012",
     *   "channelName": "",
     *   "channelCode": "",
     *   "pageNum": 1,
     *   "pageSize": 10,
     *   "alarmTimeStart": "2025-03-01 00:00:00",
     *   "alarmTimeEnd": "2025-03-01 00:00:00",
     *   "provinceCode": "130000",
     *   "cityCode": "130900",
     *   "countyCode": "130929"
     * }
     *
     * @responseDemo 返回参数data下字段解析：
     *   alarmId 告警Id
     *   alarmTime 告警时间,
     *   deviceCode 摄像机编码
     *   deviceName 摄像机名称
     *   originalPictureUrl 车牌号图片地址
     *   cityCode 城市编码
     *   countyCode 区县编码
     *   provinceCode 省份编码
     *   cityName 城市名称
     *   countyName 区县名称
     *   provinceName 省份名称
     *   vehicleNumber 车牌号,
     *   vehicleType 车辆类型
     *   areaName 区域名称
     *   vehicleColor 车辆颜色
     *   vehicleNumColor 车牌颜色
     * {
     *
     *   "alarmId": 28667,
     *   "alarmTime": "2024-09-10 10:39:48",
     *   "deviceCode": "11010500011327000005",
     *   "deviceName": "朝阳区东坝乡水管站西侧林地",
     *   "originalPictureUrl": "http://slw-base-video.obs.cn-north-4.myhuaweicloud.com:80/bayonet_alarm/12010300001327000003/12010300001310000001/MotorVehicles/20240910/20240910103948_1014_12010300001327000003_12010300001310000001_bigMotorVehicles.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1741166304&Signature=8OyqHddaC7Tm3QjZp0jTPBRbYwE%3D,http://slw-base-video.obs.cn-north-4.myhuaweicloud.com:80/bayonet_alarm/12010300001327000003/12010300001310000001/MotorVehicles/20240910/20240910103948_1014_12010300001327000003_12010300001310000001_smallMotorVehicles.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1741166304&Signature=dFOVkrKVKtlEEj%2BJp80mkKHHbyo%3D",
     *   "alarmPictureUrl": null,
     *   "cityCode": "210200",
     *   "countyCode": "210211",
     *   "provinceCode": "210000",
     *   "cityName": "大连市",
     *   "countyName": "甘井子区",
     *   "provinceName": "辽宁省",
     *   "vehicleNumber": "黑C0724C",
     *   "vehicleType": "微型轿车",
     *   "areaName": "辽宁省大连市甘井子区",
     *   "vehicleColor": "Unknown",
     *   "vehicleNumColor": "Blue"
     * }
     * @return Result
     */
    @PostMapping("/alarm/checkpoint/getCheckpointAlarmList")
    TableDataInfo getCheckpointAlarmListForCustomer(@RequestBody CheckpointAlarmDTO vo);

    /**
     * 功能描述：根据设备、通道、有效参考点、型号信息等参数将百分比坐标集合转化为经纬度坐标集合返回（PTZ/经纬度可自定义参数，没有的时候以设备的PTZ经纬度为准）
     * @param devicePointTurn
     * @return
     */
    @PostMapping("/alarm/order/turnLatitudeByPoint")
    Result<DevicePointTurn> turnLatitudeByPoint(@RequestBody DevicePointTurn devicePointTurn);

    /**
     * 根据摄像机及通道查询告警区域信息
     * @param req
     * @return
     */
    @PostMapping("/alarm/getAreaList")
    Result<AlarmAreaResp> getAreaList(@RequestBody AlarmAreaReq req);

    /**
     * 根据区域id修改位置经纬度信息
     * @param req
     * @return
     */
    @PostMapping("/alarm/updateAreaPosition")
    Result updateAreaPosition(@RequestBody List<AlarmAreaUpdateReq> req);

    @PostMapping("/alarm/receive/algorithms/publicSynAnalysisTask")
    Result publicSynAnalysisTask(@RequestBody List<SecondRecogSendVO> list);

    /**
     * 功能描述：无人机二次识别下发
     * @param: data
     * @return: Result<Map>
     */
    @PostMapping("/alarm/receive/algorithms/uavSynAnalysisTask")
    Result uavSynAnalysisTask(@RequestBody List<SecondRecogSendVO> list);


    /**
     * 查询告警图层
     * @apiNote 查询告警图层接口
     * @apiVersion 1.68
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"120046990","alarmTimeStart":"2023-7-18 00:00:00","alarmTimeEnd":"2023-8-17 23:59:59","pageNum":1,"pageSize":6,"appVerCode":"1.0","industryCode":"000000","provinceId":"110000","cityId":"110100","countyId":"110108",
     * "longitude":"116.30576686828336","latitude":"39.95981617277608","roleFlag":"8","radius":"3000","warningSourceList":["8","10"],"orderStatusList":["1","3"],"warningTypeList":["1212100","1212111"]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1651049172858,"data":[{"warningOrderId":"202308141210633511647490","warningSource": "1","warningTypeId": "1212102","alarmTime": "2022-03-24 04:37:14","tenantId": "TEN100001","tenantName": "算法仓02","provinceId":"110000","cityId":"110100",
     * "countyId":"110101","longitude": "1143.794872","latitude": "24.192697","alarmBody": "人员闯入监控告警", "orderStatus": "2","orderStatusName":"待研判","address": "北京市朝阳区常营中街与常通路十字路口西北角1111","processId":"18a550a3d40742339ca56a15908ae493,...","provinceName":"青海省","flowNodeTime":"2023-08-17 10:41:10",
     * "videoUrl": "http://120.46.145.37:10120//ThirdAlarm/pic//KLYR-1212102001_0932e8346e7045799ea114b630f0aedd45c10f4ad0534d92b8988652a05de5b0_01_880.jpg","gridId":"","gridName":"","linkId":"1","flowId":"1001","deviceCode":"1234","deviceName":"111",
     * "alarmCode":"111","channelCode":"11010800831310000008","channelName","铁塔产业园-11号楼-大华243_0","warningTypeName": "人员闯入监控告警","emergencyLevelName": "一般","orderStatusName": "待调度","warningSourceName": "AI上报","judgeResult": "2","realLinkType":"2","confidenceLevel":"0.6894647"}]}
     * 返回参数data下字段解析：
     *           alarmBody: 告警内容
     *           gridName: 网格名称
     *           latitude: 纬度
     *           flowNodeTime: 当前环节开始时间
     *           orderStatus: 事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     *           warningOrderId: 告警单ID(yyyyMM+租户id+)
     *           cityId: 地市编码
     *           deviceName: 设备名称
     *           warningSource: 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     *           cityName: 地市名称
     *           emergencyLevel: 告警等级
     *           tenantName: 租户名称
     *           videoUrl: 视频URL
     *           processId: 处理人
     *           countyId: 区县编码
     *           enableAcceptanceMode: 是否开启受理模式(0:默认，不开启；1:开启)
     *           flowId: 流程ID
     *           channelCode: 通道编码
     *           countyName: 县名称
     *           createDate: 创建时间
     *           longitude: 经度
     *           address: 详细地址
     *           fileImgUrlIcon: 框图
     *           alarmTime: 告警时间
     *           leaderNum: 领导批示数量
     *           deviceCode: 设备编码
     *           fileImgUrlSrc: 原图
     *           ifEdit: 是否可编辑  0否 1是
     *           provinceId: 省编码
     *           imgUrl: 原图
     *           timeLimit: 完成时限;单位：分钟
     *           linkId: 环节ID
     *           checkTime: 研判完成时间
     *           ifTimeout: 是否超时 0否1是
     *           warningTypeId: 告警类型编码
     *           tenantId: 租户编码
     *           channelName: 通道名称
     *           gridId: 网格id
     *           provinceName: 省份名称
     * @param dto 入参
     * @return Result
     */
    @PostMapping("/alarm/orderForTrade/getWarningOrderLayerNoFile")
    public Result getWarningOrderLayerNoFile(@RequestBody WarningOrderLayerDTO dto);
}
