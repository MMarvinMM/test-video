package com.chinatower.event.api.feign.client;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.feign.factory.AlarmFlowDeployFeignFallbackFactory;
import com.chinatower.event.api.model.AlarmByTaskReq;
import com.chinatower.event.api.model.IntelligentJudgeTaskRequest;
import com.chinatower.event.entity.dto.FlowTemplateIndustryDTO;
import com.chinatower.event.entity.vo.SecondRecogBackVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = AlarmFlowDeployFeignFallbackFactory.class)
public interface AlarmFlowDeployFeign {

    /**
     * 新增行业初始化流程&环节配置信息
     *
     * @param templateIndustryDTO 入参
     * @return Result<Object>
     * @apiNote 新增行业初始化流程&环节配置信息接口
     * @apiVersion 1.69
     * @tag 一、告警事件能力
     * @requestDemo {"templateId": 1322,1121,1212，//配置的所有模板id"industryTemplateId": 1322， //配置的默认初始化模板id"warningSource":"1,2,4"，"appVerCode":"v2.0.3"，
     * "industryCode":"12121321"，"configList":[{"linkId":1，"linkName": "待处理"，"roleId":"1,2,3,4", //多个角色逗号分割"msgNotifyType":"3","realLinkType":"1","leaderFlag":"1"，// 转领导批示：1-是、0-否
     * "turnFlag":"0", //下一环节是否可转派(1:是，0：否)"rollbackFlag":"0"，//0-不回退，1-回退"dataFilterPermiss":"0","customAdmin":"0","timeLimit":2，//时限，单位分钟"msgNotifyLimit":1，//短信通知间隔
     * "msgNotifyCompany":"" ，//短信通知间隔 单位, 1：分钟，2：小时"timeLimitCompany":"","timeLimitRemind":"","timeLimitRemindCompany":"","timeLimitRemindName":"","enableAcceptanceMode":"1","timeLimitAcceptance":3600,
     * "acceptanceCompany":"0","voiceAlram":"1","timeStartAlram":"00:00:00","timeEndAlram":"23:59:59","ifLaterRemind":"0","laterRemind":"","laterRemindCompany":"","overdueRemindSign":"1","msgSendTimeInterval":"","timeLimitPush":""}]}
     * @responseDemo {"code": 200,"msg": "保存成功！","time": 1673254838741}
     */
    @PostMapping("/alarm/flowdeploy/addFlowLinkConfigIndustry")
    Result<Object> addFlowLinkConfigIndustry(@RequestBody FlowTemplateIndustryDTO templateIndustryDTO);

    /**
     * 查询业初始化流程&环节配置信息
     *
     * @param params 入参
     * @return Result<Object>
     * @apiNote 查询业初始化流程&环节配置信息接口
     * @apiVersion 1.70
     * @tag 一、告警事件能力
     * @requestDemo {"industryCode":"000000",appVerCode:"v2.0.3"}
     * @responseDemo {"code": 200,"msg": "查询成功！","time": 1689491058930,"data": {"templateId": 1322,1121,1212，//配置的所有模板id"industryTemplateId": 1322， //配置的默认初始化模板id"warningSource":"1,2,4"，"appVerCode":"v2.0.3"，
     * "industryCode":"12121321"，"configList":[{"linkId":1，"linkName": "待处理"，"roleId":"1,2,3,4", //多个角色逗号分割"msgNotifyType":"3","realLinkType":"1","leaderFlag":"1"，// 转领导批示：1-是、0-否
     * "turnFlag":"0", //下一环节是否可转派(1:是，0：否)"rollbackFlag":"0"，//0-不回退，1-回退"dataFilterPermiss":"0","customAdmin":"0","timeLimit":2，//时限，单位分钟"msgNotifyLimit":1，//短信通知间隔
     * "msgNotifyCompany":"" ，//短信通知间隔 单位, 1：分钟，2：小时"timeLimitCompany":"","timeLimitRemind":"","timeLimitRemindCompany":"","timeLimitRemindName":"","enableAcceptanceMode":"1","timeLimitAcceptance":3600,
     * "acceptanceCompany":"0","voiceAlram":"1","timeStartAlram":"00:00:00","timeEndAlram":"23:59:59","ifLaterRemind":"0","laterRemind":"","laterRemindCompany":"","overdueRemindSign":"1","msgSendTimeInterval":"","timeLimitPush":""}]}}
     * 返回参数data下字段解析：
     * id: 主键id
     * templateId: 模版id
     * industryTemplateId: 默认流程模板id
     * warningSource: 告警来源
     * status: 状态：0-默认不是初始化模板、1-默认初始化模板
     * appVerCode: 应用版本编码
     * industryCode: 行业编码
     * configList: [{
     * id: 主键id
     * linkId: 当前环节Id
     * linkName: 流程环节名称
     * roleId: 默认关联角色ID，多个角色中间用逗号隔开
     * msgNotifyType: 消息通知类型，1：APP,2：短信,3：PC系统消息,4：PC告警声音，多种方式中间用逗号隔开
     * msgNotifyLimit: 短信通知间隔 单位分钟
     * msgNotifyCompany: 短信通知间隔 单位 1：分钟，2：小时
     * leaderFlag: 转领导批示，1：是，0：否
     * signFlag: 会签标识，0，不会签，1会签
     * status: 状态：1生效 0失效
     * screenUrl: 大屏环节URL
     * pcUrl: PC环节URL
     * appUrl: app环节URL
     * attrFlag: 属性：0为默认属性，1为算法配置属性
     * rollbackFlag: 回退标识 0-不回退，1-可回退
     * timeLimit: 时限，单位分钟
     * notifyAdminFlag: 是否通知管理员，0，不通知，1通知
     * gridUserType: 网格员-用户类型，1用户,2角色
     * dataFilterPermiss: 是否过滤数据权限,0否,1是
     * customAdmin: 是否显示客户管理员,0否,1是
     * timeLimitCompany: 环节处理时限单位, 0: 秒，1：分钟，2：小时，3：天
     * timeStartAlram: 告警声音提醒开始时段
     * timeEndAlram: 告警声音提醒结束时段
     * voiceAlram: 告警提示音
     * timeRemind: 提醒时长类型，0： 手动关闭、1：自动停止
     * timeRemindAuto: 提醒时长自动停止单位 0: 秒，1：分钟
     * timeRemindNumber: 提醒时长自动停止
     * ifLaterRemind: 稍后提醒0否,1是
     * laterRemind: 稍后提醒
     * laterRemindCompany: 稍后提醒单位 0: 秒，1：分钟，2：小时
     * timePushType: 短信推送时效 实时推送0 定时推送1
     * timeLimitPush: 短信通知上限
     * gridSignRole: 网格员角色
     * userType: 用户类型,2角色,3 网格员
     * msgSendTimeInterval: 短信通知时间区间
     * }]
     */
    @PostMapping("/alarm/flowdeploy/queryFlowLinkConfigIndustry")
    Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustry(@RequestBody Map<String, String> params);

    /**
     * 查询传感器阈值（物联告警配置）
     *
     * @param params 入参
     * @return Result<Object>
     * @apiNote 查询传感器阈值（物联告警配置）接口
     * @apiVersion 1.71
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId": "122098991","industryCode":"000000","deviceCodeList":["jtwind-gopz-4276-02","jtwind-uyld-3899-04"]}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1689491058930,"data": {"deviceList": [{"factorList": [{"configId": null,"factorId": null,"factorCode": "windSpeed",//物联因子类型 "factorName": "风速",//物联因子名称 "deviceCode": null,//设备编码 "isChoose": null,
     * "minChar": "<",//最小值符号（<  <=  无） "minNum": "50.0000000000",//最小值 "maxChar": "无",//最大值符号（<  <=  无） "maxNum": null,//最大值 "creator": null,"createTime": null,"modifier": null,"updateTime": null,"deleteFlag": null}],"deviceCode": "jtwind-gopz-4276-02","deviceStatus": 0 //设备状态:0在线，1离线，2从未连接}]}}
     * 返回参数data下字段解析：
     * deviceCode: 设备编号
     * deviceStatus: 设备状态:0在线，1离线，2从未连接
     * deviceList: [{
     * factorList: [{
     * factorCode: 物联因子编码
     * factorName: 物联因子名字
     * minChar: 最小值符号（<   <=）
     * minNum: 最小值
     * maxChar: 最大值符号（<   <=）
     * maxNum: 最大值
     * }]
     * }]
     */
    @PostMapping("/iotAlarmConfig/queryIotDeviceAndFactorInfo")
    Result<Object> queryIotDeviceAndFactorInfo(@RequestBody Map<String, Object> params);


    /**
     * 二次识别下发
     *
     * @param list 入参
     * @return Result<String>
     * @apiNote 二次识别下发
     * @apiVersion 1.72
     * @tag 一、告警事件能力
     * @requestDemo [{"algorithmCode":"ZGTT-1313157001","extraFieldInfo":{"airLineId":"1889879970163531776","airTaskId":"1890323364316782592",
     * "deviceCode":"43102500003027000001","picId":"2895"},"fileInfo":{"imgUrls":"https://zsc-uav-obs.obs.cn-north-4.myhuaweicloud.com:443/2025/02/B0/7107ed5be3d6bfc0a0e4403f6ec43e4b.jpeg?AccessKeyId=L5NQ6BQJ67RCFWN6RGH1&Expires=1739524620&Signature=JZ6BhLbcPFpQB%2F3GloUmBDX1EJs%3D","type":"image"}}]
     * @responseDemo {91f896533d674b53926e39ea1d98b5a1}
     * 返回参数data下字段解析：
     * taskBatchId: 任务批次ID
     */
    @PostMapping("/alarm/receive/algorithms/synAnalysisTask")
    Result<String> synAnalysisTask(@RequestBody List<SecondRecogSendVO> list);

    /**
     * 二次识别任务告警批量查询
     *
     * @param taskBatchId 任务批次ID
     * @return Result<List<SecondRecogBackVO>>
     * @apiNote 二次识别任务告警批量查询
     * @apiVersion 1.73
     * @tag 一、告警事件能力
     * @requestDemo {taskBatchId: 91f896533d674b53926e39ea1d98b5a1}
     * @responseDemo [{"alarmArea":[{"alarmAreaId":"1","alarmPointList":[{"alarmPointId":"1","percentX":"0.554027","percentY":"0.099394"},{"alarmPointId":"2","percentX":"0.554027","percentY":"0.31639"},{"alarmPointId":"3","percentX":"0.639739","percentY":"0.31639"},
     * {"alarmPointId":"4","percentX":"0.639739","percentY":"0.099394"}],"confidenceLevel":"0.8784"},{"alarmAreaId":"1","alarmPointList":[{"alarmPointId":"1","percentX":"0.365163","percentY":"0.0"},{"alarmPointId":"2","percentX":"0.365163","percentY":"0.228617"},
     * {"alarmPointId":"3","percentX":"0.469098","percentY":"0.228617"},{"alarmPointId":"4","percentX":"0.469098","percentY":"0.0"}],"confidenceLevel":"0.7408"}],"alarmPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1010100001_b24ae9dbaa0a4daa9e2661da4dd04aa33cfbb78becfe465bb8d6eb00233ba417_02_001.jpg","alarmVideoUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/video/ZGTT-1010100001_b24ae9dbaa0a4daa9e2661da4dd04aa33cfbb78becfe465bb8d6eb00233ba417_000001.mp4",
     * "analyType":"1","extendField":[{"alarmAreaId":"1","attrList":[{"attrType":"catType","attrValue":"小轿车"},{"attrType":"catType","attrValue":"小轿车"}]}],"extraFieldInfo":{"airLineId":"1889879970163531776","airTaskId":"1890323364316782592","deviceCode":"43102500003027000001","picId":"2895"},
     * "originalPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1010100001_b24ae9dbaa0a4daa9e2661da4dd04aa33cfbb78becfe465bb8d6eb00233ba417_01_001.jpg","secondJudgeResult":"1","taskId":"ba7e00/43102500003027000001/ZGTT-1313157001/1890323364316782592/1889879970163531776"}]
     * 返回参数data下字段解析：
     * alarmArea: 报警区域
     * alarmAreaId: 告警区域id
     * alarmPointList: 告警点列表
     * alarmPointId: 告警点位ID
     * percentX: 百分比点位横坐标
     * percentY: 百分比点位纵坐标
     * confidenceLevel: 置信度
     * alarmPictureUrl: 报警框图url
     * alarmVideoUrl: 报警视频url
     * analyType: 分析状态   1-分析中，2-分析完成 3-分析失败
     * extendField: 扩展字段
     * extraFieldInfo: 字段透传
     * airLineId: 飞行航线id
     * airTaskId: 飞行任务id
     * deviceCode: 设备编号
     * picId: 图片id
     * originalPictureUrl: 二次分析图片
     * secondJudgeResult: 二次分析结果
     * taskId: 任务ID
     */
    @PostMapping("/alarm/receive/service/alarmByTaskBatchId")
    Result<List<SecondRecogBackVO>> alarmByTaskBatchId(@RequestBody String taskBatchId);

    /**
     * 二次识别任务告警批量查询
     *
     * @param alarmByTaskReq
     * @return Result<List<SecondRecogBackVO>>
     * @apiNote 二次识别任务告警批量查询
     * @apiVersion 1.74
     * @tag 一、告警事件能力
     * @requestDemo {"taskBatchId":"91f896533d674b53926e39ea1d98b5a1","platFormCode":"","toPlatformCode":"","type":""}
     * @responseDemo [{"alarmArea":[{"alarmAreaId":"1","alarmPointList":[{"alarmPointId":"1","percentX":"0.554027","percentY":"0.099394"},{"alarmPointId":"2","percentX":"0.554027","percentY":"0.31639"},{"alarmPointId":"3","percentX":"0.639739","percentY":"0.31639"},
     * {"alarmPointId":"4","percentX":"0.639739","percentY":"0.099394"}],"confidenceLevel":"0.8784"},{"alarmAreaId":"1","alarmPointList":[{"alarmPointId":"1","percentX":"0.365163","percentY":"0.0"},{"alarmPointId":"2","percentX":"0.365163","percentY":"0.228617"},{"alarmPointId":"3","percentX":"0.469098","percentY":"0.228617"},
     * {"alarmPointId":"4","percentX":"0.469098","percentY":"0.0"}],"confidenceLevel":"0.7408"}],"alarmPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1010100001_b24ae9dbaa0a4daa9e2661da4dd04aa33cfbb78becfe465bb8d6eb00233ba417_02_001.jpg","alarmVideoUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/video/ZGTT-1010100001_b24ae9dbaa0a4daa9e2661da4dd04aa33cfbb78becfe465bb8d6eb00233ba417_000001.mp4",
     * "analyType":"1","extendField":[{"alarmAreaId":"1","attrList":[{"attrType":"catType","attrValue":"小轿车"},{"attrType":"catType","attrValue":"小轿车"}]}],"extraFieldInfo":{"airLineId":"1889879970163531776","airTaskId":"1890323364316782592","deviceCode":"43102500003027000001","picId":"2895"},"originalPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1010100001_b24ae9dbaa0a4daa9e2661da4dd04aa33cfbb78becfe465bb8d6eb00233ba417_01_001.jpg",
     * "secondJudgeResult":"1","taskId":"ba7e00/43102500003027000001/ZGTT-1313157001/1890323364316782592/1889879970163531776"}]
     * 返回参数data下字段解析：
     * alarmArea: 报警区域
     * alarmAreaId: 告警区域id
     * alarmPointList: 告警点列表
     * alarmPointId: 告警点位ID
     * percentX: 百分比点位横坐标
     * percentY: 百分比点位纵坐标
     * confidenceLevel: 置信度
     * alarmPictureUrl: 报警框图url
     * alarmVideoUrl: 报警视频url
     * analyType: 分析状态   1-分析中，2-分析完成 3-分析失败
     * extendField: 扩展字段
     * extraFieldInfo: 字段透传
     * airLineId: 飞行航线id
     * airTaskId: 飞行任务id
     * deviceCode: 设备编号
     * picId: 图片id
     * originalPictureUrl: 二次分析图片
     * secondJudgeResult: 二次分析结果
     * taskId: 任务ID
     */
    @PostMapping("/alarm/receive/service/alarmByTaskBatchIdSelf")
    Result<List<SecondRecogBackVO>> alarmByTaskBatchIdSelf(@RequestBody AlarmByTaskReq alarmByTaskReq);


    /**
     * 二次识别物联，卫星，雷达结果更新
     *
     * @param list
     * @return Result<String>
     * @apiNote 二次识别物联，卫星，雷达结果更新
     * @apiVersion 1.75
     * @tag 一、告警事件能力
     * @requestDemo [{"alarmArea":[{"alarmAreaId":"1","alarmPointList":[{"alarmPointId":"1","percentX":"0.34375","percentY":"0.36018518"},{"alarmPointId":"2","percentX":"0.34375","percentY":"0.4462963"},{"alarmPointId":"3","percentX":"0.36197916","percentY":"0.4462963"},
     * {"alarmPointId":"4","percentX":"0.36197916","percentY":"0.36018518"}],"confidenceLevel":"0.88287115"}],"alarmPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1212102001_01f131b240b547d89fc41bd48943f5b37f32f2932cae43a0a59b37d8babfe02a_02.jpg",
     * "alarmVideoUrl":"","analyType":"1","extendField":[],"extraFieldInfo":{"deviceCode":"11010800831327000004"},"originalPictureUrl":"https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com/ThirdAlarm/pic/ZGTT-1212102001_fd614c82c1144c7da6bd7220591d648e449097d4058246df9852b4642460d024_01.jpg",
     * "secondJudgeResult":"1","taskId":"2025021412208533810413881/2/ZGTT-1212102001/10000000001/1ff9d2"}]
     * @responseDemo {"code":200,"msg":"二次识别回调成功","time":1739496355280}
     */
    @PostMapping("/alarm/receive/algorithms/synAnalysisTaskResult")
    Result<String> secondRecogBack(@RequestBody List<SecondRecogBackVO> list);

    /**
     * 查询业初始化流程&环节配置信息
     *
     * @param params 入参
     * @return Result<Object>
     * @apiNote 查询业初始化流程&环节配置信息接口
     * @apiVersion 1.76
     * @tag 一、告警事件能力
     * @requestDemo {"industryCode":"000000",appVerCode:"v2.0.3"}
     * @responseDemo {"code": 200,"msg": "查询成功！","time": 1689491058930,"data": {"templateId": 1322,1121,1212，//配置的所有模板id"industryTemplateId": 1322， //配置的默认初始化模板id"warningSource":"1,2,4"，"appVerCode":"v2.0.3"，
     * "industryCode":"12121321"，"configList":[{"linkId":1，"linkName": "待处理"，"roleId":"1,2,3,4", //多个角色逗号分割"msgNotifyType":"3","realLinkType":"1","leaderFlag":"1"，// 转领导批示：1-是、0-否
     * "turnFlag":"0", //下一环节是否可转派(1:是，0：否)"rollbackFlag":"0"，//0-不回退，1-回退"dataFilterPermiss":"0","customAdmin":"0","timeLimit":2，//时限，单位分钟"msgNotifyLimit":1，//短信通知间隔
     * "msgNotifyCompany":"" ，//短信通知间隔 单位, 1：分钟，2：小时"timeLimitCompany":"","timeLimitRemind":"","timeLimitRemindCompany":"","timeLimitRemindName":"","enableAcceptanceMode":"1","timeLimitAcceptance":3600,
     * "acceptanceCompany":"0","voiceAlram":"1","timeStartAlram":"00:00:00","timeEndAlram":"23:59:59","ifLaterRemind":"0","laterRemind":"","laterRemindCompany":"","overdueRemindSign":"1","msgSendTimeInterval":"","timeLimitPush":""}]}}
     * 返回参数data下字段解析：
     * id: 主键id
     * templateId: 模版id
     * industryTemplateId: 默认流程模板id
     * warningSource: 告警来源
     * status: 状态：0-默认不是初始化模板、1-默认初始化模板
     * appVerCode: 应用版本编码
     * industryCode: 行业编码
     * configList: [{
     * id: 主键id
     * linkId: 当前环节Id
     * linkName: 流程环节名称
     * roleId: 默认关联角色ID，多个角色中间用逗号隔开
     * msgNotifyType: 消息通知类型，1：APP,2：短信,3：PC系统消息,4：PC告警声音，多种方式中间用逗号隔开
     * msgNotifyLimit: 短信通知间隔 单位分钟
     * msgNotifyCompany: 短信通知间隔 单位 1：分钟，2：小时
     * leaderFlag: 转领导批示，1：是，0：否
     * signFlag: 会签标识，0，不会签，1会签
     * status: 状态：1生效 0失效
     * screenUrl: 大屏环节URL
     * pcUrl: PC环节URL
     * appUrl: app环节URL
     * attrFlag: 属性：0为默认属性，1为算法配置属性
     * rollbackFlag: 回退标识 0-不回退，1-可回退
     * timeLimit: 时限，单位分钟
     * notifyAdminFlag: 是否通知管理员，0，不通知，1通知
     * gridUserType: 网格员-用户类型，1用户,2角色
     * dataFilterPermiss: 是否过滤数据权限,0否,1是
     * customAdmin: 是否显示客户管理员,0否,1是
     * timeLimitCompany: 环节处理时限单位, 0: 秒，1：分钟，2：小时，3：天
     * timeStartAlram: 告警声音提醒开始时段
     * timeEndAlram: 告警声音提醒结束时段
     * voiceAlram: 告警提示音
     * timeRemind: 提醒时长类型，0： 手动关闭、1：自动停止
     * timeRemindAuto: 提醒时长自动停止单位 0: 秒，1：分钟
     * timeRemindNumber: 提醒时长自动停止
     * ifLaterRemind: 稍后提醒0否,1是
     * laterRemind: 稍后提醒
     * laterRemindCompany: 稍后提醒单位 0: 秒，1：分钟，2：小时
     * timePushType: 短信推送时效 实时推送0 定时推送1
     * timeLimitPush: 短信通知上限
     * gridSignRole: 网格员角色
     * userType: 用户类型,2角色,3 网格员
     * msgSendTimeInterval: 短信通知时间区间
     * }]
     */
    @PostMapping("/alarm/flowdeploy/queryFlowLinkConfigIndustryLocalVersion")
    Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustryLocalVersion(@RequestBody Map<String, String> params);

    /**
     * 智能研判任务识别
     *
     * @param taskRequest 任务请求参数
     * @return Result<Object>
     * @apiNote 智能研判任务识别
     * @apiVersion 1.8.00
     * @tag 一、告警事件能力
     * @requestDemo {"taskId":"202501201234567890","algorithmCode":"ZGTT-AI-JUDGE-001","fileInfo":{"type":"image","imgUrls":"https://example.com/image.jpg"},"extraInfo":{"alarmType":"4110101","alarmBody":"告警描述","alarmTime":"2025-01-20 12:34:56","alarmArea":[{"alarmAreaId":"1","alarmPointList":[{"alarmPointId":"1","percentX":"0.5","percentY":"0.5"}],"confidenceLevel":"0.85"}]},"extraFieldInfo":{"tenantId":"123456","industryCode":"000000","deviceCode":"DEV001","channelCode":"CH001"}}
     * @responseDemo {"code": 200,"msg": "智能研判任务下发成功","time": 1739496355280,"data": {"taskId":"202501201234567890","taskBatchId":"91f896533d674b53926e39ea1d98b5a1","status":"SUCCESS"}}
     * 返回参数data下字段解析：
     * taskId: 任务ID
     * taskBatchId: 任务批次ID
     * status: 任务状态
     */
    @PostMapping("/alarm/receive/algorithms/synIntelligentJudgeTask")
    Result<JSONObject> synIntelligentJudgeTask(@RequestBody IntelligentJudgeTaskRequest taskRequest);

    /**
     * 新增行业初始化流程&环节配置信息
     *
     * @param templateIndustryDTO 入参
     * @return Result<Object>
     * @apiNote 新增行业初始化流程&环节配置信息接口
     * @apiVersion 1.77
     * @tag 一、告警事件能力
     * @requestDemo {"templateId": 1322,1121,1212，//配置的所有模板id"industryTemplateId": 1322， //配置的默认初始化模板id"warningSource":"1,2,4"，"appVerCode":"v2.0.3"，
     * "industryCode":"12121321"，"configList":[{"linkId":1，"linkName": "待处理"，"roleId":"1,2,3,4", //多个角色逗号分割"msgNotifyType":"3","realLinkType":"1","leaderFlag":"1"，// 转领导批示：1-是、0-否
     * "turnFlag":"0", //下一环节是否可转派(1:是，0：否)"rollbackFlag":"0"，//0-不回退，1-回退"dataFilterPermiss":"0","customAdmin":"0","timeLimit":2，//时限，单位分钟"msgNotifyLimit":1，//短信通知间隔
     * "msgNotifyCompany":"" ，//短信通知间隔 单位, 1：分钟，2：小时"timeLimitCompany":"","timeLimitRemind":"","timeLimitRemindCompany":"","timeLimitRemindName":"","enableAcceptanceMode":"1","timeLimitAcceptance":3600,
     * "acceptanceCompany":"0","voiceAlram":"1","timeStartAlram":"00:00:00","timeEndAlram":"23:59:59","ifLaterRemind":"0","laterRemind":"","laterRemindCompany":"","overdueRemindSign":"1","msgSendTimeInterval":"","timeLimitPush":""}]}
     * @responseDemo {"code": 200,"msg": "保存成功！","time": 1673254838741}
     */
    @PostMapping("/alarm/flowdeploy/addFlowLinkConfigIndustryLocalVersion")
    Result<Object> addFlowLinkConfigIndustryLocalVersion(@RequestBody FlowTemplateIndustryDTO templateIndustryDTO);
}
