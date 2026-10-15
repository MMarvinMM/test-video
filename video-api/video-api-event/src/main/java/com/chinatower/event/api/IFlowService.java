package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.FlowReq;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.factory.FlowFallbackFactory;
import com.chinatower.event.api.model.ApiAlarmFlowConfigModel;
import com.chinatower.event.api.model.ApiAlarmOrderRecordModel;
import com.chinatower.event.api.model.ApiFlowLinkAttrModel;
import com.chinatower.event.api.model.ApiFlowLinkConfigModel;
import com.chinatower.event.api.model.ApiFlowModel;
import com.chinatower.event.entity.dto.CheckCurentReq;
import com.chinatower.event.entity.dto.CheckCurrentResp;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigModel;
import com.chinatower.system.api.resp.SysDictDataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "flowService", value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = FlowFallbackFactory.class)
public interface IFlowService {

    /**
     * 生成AI告警, 人工上报,一键生成,三个默认流程
     * @apiNote 生成AI告警, 人工上报,一键生成,三个默认流程接口
     * @apiVersion 1.76
     * @tag 一、告警事件能力
     * @requestDemo {"tenantId":"121051052"}
     * @responseDemo {"data":{121061973_1:10043}}
     * 返回参数data下字段解析：
     *      租户编码_告警来源: 告警流程id
     * @param flowReq 入参
     * @return Result<Map<String, Long>>
     */
    @PostMapping(value = "/flow/alarm/genDefaultFlowInfo")
    Result<Map<String, Long>> genDefaultFlowInfo(@Validated @RequestBody FlowReq flowReq);

    /**
     * 查询下一步所有环节
     * @apiNote 查询下一步所有环节接口
     * @apiVersion 1.77
     * @tag 一、告警事件能力
     * @requestDemo {"flowId":"10016","curLink":"1","templateId":"315785070878329067","warningSource":"1"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741,"data":[{"userId": "0cc2c60a8c7141cdbf48b927a3aaafac,320f2884cad54856bdfba0aaff7d509f","flowId": 10914,"flowName": "研判调度处置核实流程",
     * "linkType": "2","userType": "1",msgNotifyType": "1,3,4","leaderFlag": "1","rollbackFlag": "1","linkId": 2,"flowLinkId": 11533,"alarmFlowId": 10914,"preLink": "1","nextLink": "4","linkName": "调度",
     * "curLink": 2,"turnFlag": "0","realLinkType":"","dataFilterPermiss":"","customAdmin":"","enableAcceptanceMode":"0","timeLimitAcceptance":"","attrId":"2"}]}
     * 返回参数data下字段解析：
     * 		flowLinkId: 流程环节主键id
     * 		flowId: 流程配置ID
     * 		curLink: 当前环节ID
     * 		linkId: 当前环节Id
     * 		linkName: 环节名称
     * 		linkType: 环节属性，1开始环节，2中间环节，3结束环节
     * 		preLink: 上一个环节
     * 		nextLink: 下一个环节
     * 		userType: 用户类型，1用户，2角色
     * 		roleId: 角色ID，多个以,分隔
     * 		orgId: 组织ID，多个以,分隔
     * 		rootOrgId: 根组织ID，多个以,分隔
     * 		alarmFlowId: 告警流程配置表ID
     * 		msgNotifyType: 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     * 		notifyAdminFlag: 是否通知管理员，0，不通知，1通知
     * 		leaderFlag: 是否通知领导，0，不通知，1通知
     * 		signFlag: 会签标识，0，不会签，1会签
     * 		rollbackFlag: 回退标识 0-不回退，1-可回退
     * 		timeLimit: 时限，单位分钟
     * 		attrFlag: 0为默认属性，1为配置后属性
     * 		enablePage: 查询开启分页：true-开启，false-关闭
     * 		userId: 用户ID,可存在多个，中间用逗号隔开
     * 		leaderId: 通知领导ID，多个ID中间用逗号隔开
     * 		userInfos : [{
     * 				userId: 用户id
     * 				userName: 用户名称
     * 				nickName: 昵称
     * 				roleId: 角色id
     * 				roleName: 角色名称
     * 				deptId: 部门id
     * 				parentId: 父级id
     * 				deptName: 部门名称
     * 				nodeId: 节点id
     * 				nodeName: 节点名称
     * 				avatar: 图像
     * 				purviewFlag: 过滤数据权限标识（0是）
     * 				tenantAdmin: 是否租户管理员(0否,1是)
     * 				status: 状态
     * 				phonenumber: 手机号
     *       }]
     * 		tenantId: 租户ID
     * 		msgNotifyLimit: 短信通知间隔 单位分钟
     * 		msgNotifyCompany: 短信通知间隔 单位
     * 		linkList: 环节集合
     * 		warningSource: 告警来源
     * 		templateId: 模板id
     * 		deviceCode: 设备编码
     * 		cityId: 市编码
     * 		countyId: 县编码
     * 		provinceId: 省编码
     * 		statusFlag: 调度选择人标记位（行业为1）
     * 		turnFlag: 是否转派标识（0-否，1-是）
     * 		realLinkType: 真实环节类型(1研判2调度4处置5核实6办结)
     * 		gridId: 网格ID
     * 		gridFlag: 是否有网格员（1：有）
     * 		dataFilterPermiss: 是否过滤数据权限（0否,1是）
     * 		customAdmin: 是否展示客户管理员（0否,1是）
     * 		userName: 用户昵称（用于选择处理人模糊查询）
     * 		attrId: 流程环节属性id
     * 		enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     * 		timeLimitAcceptance: 受理时限
     * 		warningTypeId: 告警类型id
     * 		appVerCode: 应用版本编码
     * 		industryCode: 行业编码
     * @param model 入参
     * @return Result<List<ApiFlowLinkAttrModel>>
     */
    @PostMapping("/api/flow/alarm/queryNextLink")
    Result<List<ApiFlowLinkAttrModel>> queryNextLink(@RequestBody ApiFlowLinkAttrModel model);

    /**
     * 查询流程环节
     * @apiNote 查询流程环节接口
     * @apiVersion 1.78
     * @tag 一、告警事件能力
     * @requestDemo {"flowId":"10016","appVerCode":"1.0","industryCode":"000000","linkId":"1","templateId":"315785070878329067","warningSource":"1"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741,"data":[{"userId": "0cc2c60a8c7141cdbf48b927a3aaafac,320f2884cad54856bdfba0aaff7d509f","flowId": 10914,"flowName": "研判调度处置核实流程",
     * "linkType": "2","userType": "1",msgNotifyType": "1,3,4","leaderFlag": "1","rollbackFlag": "1","linkId": 2,"flowLinkId": 11533,"alarmFlowId": 10914,"preLink": "1","nextLink": "4","linkName": "调度",
     * "curLink": 2,"turnFlag": "0","realLinkType":"","dataFilterPermiss":"","customAdmin":"","enableAcceptanceMode":"0","timeLimitAcceptance":"","attrId":"2"}]}
     * 返回参数data下字段解析：
     *		flowLinkId: 流程环节主键id
     * 		flowId: 流程配置ID
     * 		curLink: 当前环节ID
     * 		linkId: 当前环节Id
     * 		linkName: 环节名称
     * 		linkType: 环节属性，1开始环节，2中间环节，3结束环节
     * 		preLink: 上一个环节
     * 		nextLink: 下一个环节
     * 		userType: 用户类型，1用户，2角色
     * 		roleId: 角色ID，多个以,分隔
     * 		orgId: 组织ID，多个以,分隔
     * 		rootOrgId: 根组织ID，多个以,分隔
     * 		alarmFlowId: 告警流程配置表ID
     * 		msgNotifyType: 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     * 		notifyAdminFlag: 是否通知管理员，0，不通知，1通知
     * 		leaderFlag: 是否通知领导，0，不通知，1通知
     * 		signFlag: 会签标识，0，不会签，1会签
     * 		rollbackFlag: 回退标识 0-不回退，1-可回退
     * 		timeLimit: 时限，单位分钟
     * 		attrFlag: 0为默认属性，1为配置后属性
     * 		enablePage: 查询开启分页：true-开启，false-关闭
     * 		userId: 用户ID,可存在多个，中间用逗号隔开
     * 		leaderId: 通知领导ID，多个ID中间用逗号隔开
     * 		userInfos : [{
     * 				userId: 用户id
     * 				userName: 用户名称
     * 				nickName: 昵称
     * 				roleId: 角色id
     * 				roleName: 角色名称
     * 				deptId: 部门id
     * 				parentId: 父级id
     * 				deptName: 部门名称
     * 				nodeId: 节点id
     * 				nodeName: 节点名称
     * 				avatar: 图像
     * 				purviewFlag: 过滤数据权限标识（0是）
     * 				tenantAdmin: 是否租户管理员(0否,1是)
     * 				status: 状态
     * 				phonenumber: 手机号
     *      }]
     * 		tenantId: 租户ID
     * 		msgNotifyLimit: 短信通知间隔 单位分钟
     * 		msgNotifyCompany: 短信通知间隔 单位
     * 		linkList: 环节集合
     * 		warningSource: 告警来源
     * 		templateId: 模板id
     * 		deviceCode: 设备编码
     * 		cityId: 市编码
     * 		countyId: 县编码
     * 		provinceId: 省编码
     * 		statusFlag: 调度选择人标记位（行业为1）
     * 		turnFlag: 是否转派标识（0-否，1-是）
     * 		realLinkType: 真实环节类型(1研判2调度4处置5核实6办结)
     * 		gridId: 网格ID
     * 		gridFlag: 是否有网格员（1：有）
     * 		dataFilterPermiss: 是否过滤数据权限（0否,1是）
     * 		customAdmin: 是否展示客户管理员（0否,1是）
     * 		userName: 用户昵称（用于选择处理人模糊查询）
     * 		attrId: 流程环节属性id
     * 		enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     * 		timeLimitAcceptance: 受理时限
     * 		warningTypeId: 告警类型id
     * 		appVerCode: 应用版本编码
     * 		industryCode: 行业编码
     * @param model 入参
     * @return Result<List<ApiFlowModel>>
     */
    @PostMapping("/api/flow/alarm/queryFlowLink")
    Result<List<ApiFlowModel>> queryFlowLink(@RequestBody ApiAlarmFlowConfigModel model);


    /**
     * 查询回退环节
     * @apiNote 查询回退环节接口
     * @apiVersion 1.79
     * @tag 一、告警事件能力
     * @requestDemo {"orderId":"202308141220357971647483","appVerCode":"1.0","industryCode":"000000","linkId":"1"}
     * @responseDemo {"code": 200,"msg": "操作成功","time": 1673254838741,"data":[{"userId": "0cc2c60a8c7141cdbf48b927a3aaafac,320f2884cad54856bdfba0aaff7d509f","flowId": 10914,"flowName": "研判调度处置核实流程",
     * "linkType": "2","userType": "1",msgNotifyType": "1,3,4","leaderFlag": "1","rollbackFlag": "1","linkId": 2,"flowLinkId": 11533,"alarmFlowId": 10914,"preLink": "1","nextLink": "4","linkName": "调度",
     * "curLink": 2,"turnFlag": "0","realLinkType":"","dataFilterPermiss":"","customAdmin":"","enableAcceptanceMode":"0","timeLimitAcceptance":"","attrId":"2"}]}
     * 返回参数data下字段解析：
     *		linkId: 当前环节Id
     * 		linkName: 环节名称
     * 		linkPriority: 环节优先级
     * 		createDate: 创建时间
     * 		updateDate: 修改时间
     * 		status: 状态：1生效 0失效
     * 		turnFlag: 是否转派标识（0-否，1-是）
     * 		realLinkType: 真实环节类型(1研判2调度4处置5核实6办结)
     * 		flowId: 流程配置ID
     * 		msgNotifyType: 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     * 		gridId: 网格ID
     * 		gridFlag: 是否有网格员（1：有）
     * 		dataFilterPermiss: 是否过滤数据权限（0否,1是）
     * 		customAdmin: 是否展示客户管理员（0否,1是）
     * 		userName: 用户昵称（用于选择处理人模糊查询）
     * 		attrId: 流程环节属性id
     * 		enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     * 		timeLimitAcceptance: 受理时限
     * 		appVerCode: 应用版本编码
     * 		industryCode: 行业编码
     * @param model 入参
     * @return Result<List<ApiFlowLinkConfigModel>>
     */
    @PostMapping("/api/flow/alarm/queryRollbackLink")
    Result<List<ApiFlowLinkConfigModel>> queryRollbackLink(@RequestBody ApiAlarmOrderRecordModel model);

    /**
     * 订购初始化流程配置
     */
    @PostMapping("/alarm/flowdeploy/addAlarmFlowTypeAndConfig")
    Result<Object> addAlarmFlowTypeAndConfig(VmAlarmFlowConfigModel flowConfigModel);

    /**
     * 根据工单号&当前环节id获取工单是否在当前环节
     * @apiNote 根据工单号&当前环节id获取工单是否在当前环节接口
     * @apiVersion 1.80
     * @tag 一、告警事件能力
     * @requestDemo {"linkId":"1","orderId":"202308141220357971647483"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1673254838741,"data":{"ifCurrentLink":"1"}}
     * 返回参数data下字段解析：
     *      ifCurrentLink: 是否当前环节 1:是 0:否
     * @param req 入参
     * @return Result<CheckCurrentResp>
     */
    @PostMapping("/flow/order/getCurrentByOrderIdAndLinkId")
    Result<CheckCurrentResp> getCurrentByOrderIdAndLinkId(CheckCurentReq req);

    /**
     * 查询所有的告警来源（含第三方告警来源）
     * @apiNote 查询所有的告警来源接口
     * @apiVersion 1.81
     * @tag 一、告警事件能力
     * @requestDemo {}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1673254838741,"data":[{"createBy":"1","createTime":"2022-06-17 11:30:57","default":false,
     * "dictCode":31722,"dictLabel":"AI告警","dictSort":1,"dictType":"warning_source_common","dictValue":"1","orderBy":"","remark":"摄像机类型","status":"0"}]}
     * 返回参数data下字段解析：
     *		dictCode: 字典编码
     * 		dictSort: 字典排序
     * 		dictLabel: 字典标签
     * 		dictValue: 字典键值
     * 		dictType: 字典类型
     * 		cssClass: 样式属性
     * 		listClass: 表格字典样式
     * 		isDefault: 是否默认（Y是 N否）
     * 		status: 状态（0正常 1停用）
     * 		remark: 备注
     * 		createBy: 创建人
     * 		createTime: 创建时间
     * 		updateBy: 更新者
     * 		updateTime: 更新时间
     * @return Result<CheckCurrentResp>
     */
    @PostMapping("/alarm/flowconfig/findAlarmSourceAll")
    Result<List<SysDictDataResponse>> findAlarmSourceAll();


    /**
     * 获取告警流程详情
     * @apiNote 获取告警流程详情&编辑回显接口
     * @apiVersion 1.82
     * @tag 一、告警事件能力
     * @requestDemo {"id":"12016","configType":"1","tenantId":"120046990","appVerCode":"1.0","industryCode":"000000"}
     * @responseDemo {"code": 200,"msg": "请求成功","time": 1673254838741,"data":[{"creator": "c5cbeda41f6f439d84f46b9d8f63c26a",
     * "createTime": "2025-01-17 14:44:00","createBy": null,"updateBy": null,"modifier": "","updateTime": null,"deleteFlag": null,
     * "tenantId": "120046990","userId": null,"appVerCode": "1.0","industryCode": "000000","platformCode": null,"id": "12016",
     * "alarmFlowName": "告警流程名称40909","templateId": "531603081142616872","flowName": "新增测试1113","status": null,"flowTemplateTypeList":
     * [{"creator": "c5cbeda41f6f439d84f46b9d8f63c26a","createTime": "2025-01-17 14:44:00","createBy": null,"updateBy": null,
     * "modifier": "c5cbeda41f6f439d84f46b9d8f63c26a","updateTime": "2025-01-17 14:44:00","deleteFlag": 0,"tenantId": "120046990",
     * "userId": null,"appVerCode": null,"industryCode": null,"platformCode": null,"id": "666818","templateId": "531603081142616872",
     * "alarmFlowId": 12016,"warningSource": "2","alarmType": "1010100"}],"flowLinkAttrList":[{"creator": null,"createTime": null,
     * "createBy": null,"updateBy": null,"modifier": null,"updateTime": null,"deleteFlag": null,"tenantId": "120046990","userId": null,
     * "appVerCode": null,"industryCode": "000000","platformCode": null,"id": "50160","templateId": "531603081142616872","linkId": 5693,
     * "linkName": "处置1","roleId": null,"attrFlag": null,"alarmFlowId": 12016,"userType": "1","msgNotifyType": "3","msgNotifyLimit": 30,
     * "msgNotifyCompany": "1","leaderFlag": "1","signFlag": null,"rollbackFlag": "1","timeLimit": 30,"notifyAdminFlag": null,"flowLinkUserList":
     * [{"creator": null,"createTime": null,"createBy": null,"updateBy": null,"modifier": null,"updateTime": null,"deleteFlag": null,"tenantId": "120046990",
     * "userId": "c5cbeda41f6f439d84f46b9d8f63c26a","appVerCode": null,"industryCode": null,"platformCode": null,"linkUserId": "90663","orgId": "120046990-0140",
     * "templateId": "531603081142616872","linkId": 5693,"attrId": "50160","gridSign": "0"}],"flowLinkRoleList": null,"flowLinkOverdueRemindList": null,
     * "msgSendTimeInterval": "00:00:00-23:59:59","linkAlias": "处置1","turnFlag": 0,"realLinkType": "4","gridUserType": "2","dataFilterPermiss": "1",
     * "customAdmin": "0","timeLimitCompany": "1","timeStartAlram": "00:00:00","timeEndAlram": "23:59:59","voiceAlram": "1","timeRemind": "1","timeRemindAuto": "0",
     * "timeRemindNumber": "5","ifLaterRemind": "0","laterRemind": "5","laterRemindCompany": "1","timePushType": "0","timeLimitPush": null,"gridSignRole": null,
     * "enableAcceptanceMode": "0","timeLimitAcceptance": 30,"acceptanceCompany": "1"}],"configType": null,"operType": null}]
     * 返回参数data下字段解析：
     *          creator: 创建人
     *          createTime: 创建时间
     *          createBy: 创建者
     *          updateBy: 修改者
     *          modifier: 修改人
     *          updateTime: 修改时间
     *          deleteFlag: 删除标识
     *          tenantId: 租户ID
     *          userId: 用户ID
     *          appVerCode: 行业版本
     *          industryCode: 行业编码
     *          platformCode: 节点编码
     *          alarmFlowName: 告警流程配置名称
     *          templateId: 模板ID
     *          flowName: 流程名称
     *          status: 状态
     *          flowTemplateTypeList: 流程模板关联告警类型集合
     *                  templateId: 模板ID
     *                  alarmFlowId: 告警流程配置ID
     *                  warningSource: 告警来源编码
     *                  alarmType: 告警类型编码
     *          flowLinkAttrList: 流程环节属性集合
     *                  templateId: 模板ID
     *                  linkId: 环节ID
     *                  linkName: 环节名称
     *                  roleId: 角色ID
     *                  attrFlag: 属性：0为默认属性，1为算法配置属性
     *                  alarmFlowId: 告警流程配置表ID
     *                  userType: 用户类型,2角色,3 网格员
     *                  msgNotifyType: 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     *                  msgNotifyLimit: 短信通知间隔 单位分钟
     *                  msgNotifyCompany: 短信通知间隔 单位
     *                  leaderFlag: 是否通知领导，0，不通知，1通知
     *                  signFlag: 会签标识，0，不会签，1会签
     *                  rollbackFlag: 回退标识 0-不回退，1-可回退
     *                  timeLimit: 时限，单位分钟
     *                  notifyAdminFlag: 是否通知管理员，0，不通知，1通知
     *                  flowLinkUserList: 流程环节用户集合
     *                          linkUserId: 环节用户ID
     *                          orgId: 组织ID
     *                          templateId: 模板ID
     *                          linkId: 环节ID
     *                          attrId: 流程环节属性ID
     *                          gridSign: 网格员适用类型标识 默认0  0正式 1网格
     *                  flowLinkRoleList: 流程环节角色集合
     *                          linkRoleId: 环节角色ID
     *                          orgId: 组织ID
     *                          templateId: 模板ID
     *                          linkId: 环节ID
     *                          attrId: 流程环节属性ID
     *                          gridSign: 网格员适用类型标识 默认0  0正式 1网格
     *                  flowLinkOverdueRemindList: 流程环节处理超期提醒集合
     *                          attrId: 流程ID
     *                          timeLimitRemind: 超期提醒时间
     *                          timeLimitRemindCompany: 超期提醒单位, 0: 秒，1：分钟，2：小时，3：天
     *                          timeLimitRemindName: 超期提醒人
     *                          timeLimitRemindNames: 超期提醒人姓名集合 (提供给app)
     *                          overdueRemindSign: 超期提醒标识（0:环节处理超期提醒；1：受理超期提醒）
     *                  msgSendTimeInterval: 短信通知时间区间
     *                  linkAlias: 环节别名
     *                  turnFlag: 是否转派标识（0-否，1-是）
     *                  realLinkType: 真实环节类型(1研判2调度4处置5核实6办结)
     *                  gridUserType: 网格员-用户类型，1用户,2角色
     *                  dataFilterPermiss: 是否过滤数据权限（0否,1是）
     *                  customAdmin: 是否显示客户管理员,0否,1是
     *                  timeLimitCompany: 环节处理时限单位, 0: 秒，1：分钟，2：小时，3：天
     *                  timeStartAlram: 告警声音提醒开始时段
     *                  timeEndAlram: 告警声音提醒结束时段
     *                  voiceAlram: 告警提示音
     *                  timeRemind: 提醒时长类型，0： 手动关闭、1：自动停止
     *                  timeRemindAuto: 提醒时长自动停止单位 0: 秒，1：分钟
     *                  timeRemindNumber: 提醒时长自动停止
     *                  ifLaterRemind: 稍后提醒0否,1是
     *                  laterRemind: 稍后提醒
     *                  laterRemindCompany: 稍后提醒单位 0: 秒，1：分钟，2：小时
     *                  timePushType: 短信推送时效 实时推送0 定时推送1
     *                  timeLimitPush: 短信通知上限
     *                  gridSignRole: 网格员角色
     *                  enableAcceptanceMode: 是否开启受理模式（0:不开启 1:开启）
     *                  timeLimitAcceptance: 受理时限
     *                  acceptanceCompany: 受理时限单位, 0: 秒，1：分钟，2：小时，3：天
     *          configType: ”0“：默认流程   ”1“：自定义流程
     *          operType: 操作类型：0-查询，1-编辑
     * @param configModel 入参
     * @return Result<Object>
     */
    @PostMapping("/alarm/flowconfig/queryAlarmFlowConfigDetail")
    Result<Object> queryAlarmFlowConfigDetail(@RequestBody VmAlarmFlowConfigModel configModel);
}
