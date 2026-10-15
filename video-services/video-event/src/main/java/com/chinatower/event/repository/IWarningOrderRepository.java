package com.chinatower.event.repository;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.model.AiAlgorithmConfigReq;
import com.chinatower.event.api.model.IntelligentJudgeTaskRequest;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.dataobject.OrderLeaderAuditDO;
import com.chinatower.event.dataobject.WarningOrderDO;
import com.chinatower.event.dto.AlarmOrderDuplicateDTO;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.dto.WarningOrderDTO;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.gridinfo.GridInfoReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.vo.AlarmDetailConfigVo;
import com.chinatower.event.vo.LeaderInfoByOrderIdVO;
import com.chinatower.event.vo.WarningOrderVO;
import com.chinatower.system.api.resp.SysDictDataResponse;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-20
 */
public interface IWarningOrderRepository extends IService<WarningOrderDO> {

    /**
     *
     * 功能描述:
     * 工作台工单列表查询
     */
    TableDataInfo getNoScopeOrderList(AlarmOrderDTO dto, ServletRequest request);

    /**
     *
     * 功能描述:
     * 工作台工单列表查询
     */
    TableDataInfo getOrderList(AlarmOrderDTO dto, ServletRequest request);

    /**
     * 根据网格id获取当天告警事件总数量
     * @param dto
     * @return
     */
    String getOrderListCount(AlarmOrderDTO dto);

    LabelAndColStatusResp getLabelAndColStatusByOrders(LabelAndColStatusReq req);

    /**
     *
     * 功能描述:
     * 工作台工单列表查询
     */
    TableDataInfo getOrderListByDeviceChannelAndPresetId(AlarmOrderParmaDto dto);

    /**
     *
     * 功能描述:
     * 工作台工单列表查询
     */
    List<WarningOrderResultCountVo> getOrderListByDeviceChannelAndPresetIdGroup(AlarmOrderParmaDto dto);

    /**
     *
     * 功能描述:
     * 工作台工单列表查询
     */
    List<WarningOrderResultCountVo> getOrderListByLot(AlarmOrderParmaDto dto);

    /**
     *获取可编辑权限
     */
    String getIfEdit(IfEditReq dto);

    /**
     * 根据工单ID查询事件，返回收藏的事件来源
     * @param dto
     * @return
     */
    Map<String, Object> getCollectionSourceByOrderId(AlarmOrderDTO dto);

    /**
     *
     * 功能描述:
     * 获取领导批示列表
     */
    List<LeaderAuidtListVO> getLeaderAuditList(LeaderAuidtListDTO dto);

    /**
     * 获取告警类型名称列表
     * @return
     */
    List<Map> getAlarmTypeList();

    /**
     * 根据枚举类型获取枚举列表
     * @param enumTypeId
     * @return
     */
    List<SysDictDataResponse> getEnumList(int enumTypeId);

    /**
     * 获取告警详情
     * @param dto
     * @return
     */
    WarningOrderInfoVO getOrderInfo(AlarmOrderDTO dto) throws Exception;

    /**
     * 通过摄像机查询预置位&告警数量列表
     * @param dto
     * @return
     */
    List<OrderInfoVO> getPresetListAndEventCount(AlarmOrderDTO dto);

    /**
     * 查询结果转化
     * @param resultList
     * @param alarmOrderListReq
     * @param userId
     * @param flowLinkIds
     */
    void resultHandle(List<WarningOrderListVO> resultList, AlarmOrderDTO alarmOrderListReq, String userId,String flowLinkIds);

    /**
     * 通过告警工单ID获取处理环节信息
     * @param dto
     * @return
     */
    List<Map> getRecordByOrderId(AlarmOrderDTO dto);

    /**
     * 通过告警工单ID获取处理环节信息
     * @param dto
     * @return
     */
    List<Map> getRecordByOrderIdAsc(AlarmOrderDTO dto);

    /**
     * 获取从告警数据列表
     * @author: chenmf
     * @date: 2021/12/02
     * @return
     */
    List<WarningOrderVO> warningOrderList(WarningOrderDTO dto);

    /**
     * 事件处理时间轴
     *
     * @param dto
     * @return
     */
    List<Map> getTimeAxis(AlarmOrderDTO dto);

    /**
     * 获取流程流转过程数据
     *
     * @param dto
     * orderId
     * linkId
     * @return
     */
    List<FlowRecordVO> getRecordByType(AlarmOrderDTO dto);

    /**
     * 通过告警工单id获取领导批示列表
     * @param dto
     * orderId
     * @return
     */
    List<LeaderInfoByOrderIdVO> getLeaderListByOrderId(AlarmOrderDTO dto);

    /**
     * 大屏查询结果转化
     * @param resultList
     */
    void resultMapHandle(List<Map> resultList);

    /**
     * 获取领导批示详情
     * @param dto
     * @return
     */
    Map getLeaderAufitInfo(OrderLeaderAuditDO dto) throws Exception;

    /**
     * 添加报警事件-人工上报
     * @param receiveAlarmVO
     * @return
     */
    Result addAlarmOrder(ReceiveAlarmVO receiveAlarmVO);

    Result addThirdAlarmOrder(List<ReceiveAlarmVO> receiveAlarmVO);

    /**
     * 插入环节/受限超期数据
     * @param alarms
     */
    void saveOverdueRemindData(List<ReceiveAlarmVO> alarms);

    /**
     * 获取告警事件导出数据
     * @param dto
     * @return
     */
    List<WarningOrderExport> exportWarningOrderList(AlarmOrderDTO dto);

    /**
     * 封装用户待办SQL
     * @param userId
     * @param roleIds
     * @return
     */
     String getOrderStatusSql(String userId, Long[] roleIds);

    /**
     * 查询用户代办事项数量
     * @param req
     * @return
     */
     Result getTodoNum(AppOrderListByUserDTO req);

    /**
     * 根据告警时间按创建分月表
     * @param alarmTime
     */
    void createNewTable(String alarmTime);

    /**
     * 根据告警时间按创建分月表
     * @param alarmTimeList
     */
    void createNewTable(List<String> alarmTimeList);

    /**
     * 创建流程
     * @param customers
     * @return
     */
    List<Map> createRecord(List<ReceiveAlarmVO> customers);

    /**
     * 回填告警表当前环节相关字段
     * @param alarms
     * @param recordList
     */
    void setWarningLinkInfo(List<ReceiveAlarmVO> alarms, List<Map> recordList);

    /**
     * 获取流程
     * @param alarmFlowConfigModel
     * @return
     */
    FlowModel getAlarmFlowConfig(AlarmFlowConfigModel alarmFlowConfigModel);

    /**
     * 获取流程（新）
     * @param alarmFlowConfigModel
     * @return
     */
    FlowModel getAlarmFlowConfigNew(AlarmFlowConfigModel alarmFlowConfigModel);

    /**
     * 查询当前流程环节信息
     * @param alarmFlowConfigModel
     * @return
     */
    FlowModel getFlowLink(AlarmFlowConfigModel alarmFlowConfigModel);

    /**
     * 告警事件调度
     * @param dto
     * @return
     */
    Result sendAlarmOrder(SendAlarmOrderDTO dto);

    /**
     * 告警事件转派
     * @param dto
     * @return
     */
    Result reassignment(TradeReassignmenOrdertDto dto);

    /**
     * 告警事件受理
     * @param dto
     * @return
     */
    Result accept(TradeAcceptOrderDto dto);

    /**
     * 研判：假警、真警、重复告警处理
     * @param dto
     * @return
     */
    Result checkAlarmOrder(JudgeAlarmOrderDTO dto);

    /**
     * 督办
     * @param dto
     * @return
     */
    Result urgeToHandleAlarmOrder(SendSupervisionMsgDTO dto);

    /**
     * 督办记录
     * @param dto
     * @return
     */
    List<HandleRecordVO> getHandleRecordByOrderId(AlarmOrderDTO dto);

    /**
     * 校验工单状态是否异常
     * @param orderId
     * @param tableName
     * @param orderStatus
     * @return
     */
    WarnOrderStatusDTO checkOrderStatus(String orderId, String tableName, String orderStatus,String linkId);

    /**
     * 校验工单状态是否是研判环节（批量）
     * @param orderIds
     * @param tableName
     * @return
     */
    List<WarnOrderStatusDTO> selectCheckOrderStatus(List<String> orderIds, String tableName);

    /**
     * 校验工单状态是否在待受理
     * @param orderId
     * @param tableName
     * @return
     */
    WarnOrderStatusDTO checkOrderAccept(String orderId, String tableName, String linkId,String enableAcceptanceMode);
    /**
     *
     * @param alarmOrderVO
     * @return
     */
    Result updateAlarmOrder(SendAlarmOrderDTO alarmOrderVO);

    /**
     * 判断告警事件是否会签完成
     * @param warningOrderId
     * @return
     */
    boolean finishSign(String warningOrderId);

    /**
     * 研判为重复告警时，获取事件列表
     * @param dto
     * @return
     */
    TableDataInfo getOrderListForDuplication(AlarmOrderDuplicateDTO dto);

    /**
     * 获取设备、通道别名
     * @param orderInfoVO
     * @return
     */
    void getDeviceRel(OrderInfoVO orderInfoVO);

    /**
     * 获取告警列表-告警事件大屏右侧列表
     * @param req
     * @return
     */
    TableDataInfo getAlarmListForBigScreen(BigScreenOrderListDTO req);

    /**
     * 根据租户按照经纬度递归查询告警信息
     * @param dto
     * @param request
     * @return
     */
    Result searchOrderNumByLongitude(SearchOrderDTO dto, ServletRequest request);

    /**
     * 获取告警列表-告警事件大屏左侧查询
     * @param dto
     * @return
     */
    TableDataInfo getOrderListForBigScreen(BigScreenOrderListDTO dto);

    /**
     * 根据时间范围获取告警事件总数
     * @param req
     * @return TableDataInfo
     */
    TableDataInfo getAlarmListCount(BigScreenOrderListDTO req);

    /**
     * 根据设备编码集合查询设备是否有未完结告警
     * @param dto
     * @return Result
     */
    Result<List<OrderCompleteVO>> getOrderNumByDeviceCode(AlarmOrderParmaDto dto);

    /**
     * 根据时间范围、分页获取经纬度列表
     * @param dto
     * @return TableDataInfo
     */
    TableDataInfo getOrderLongitudeList(BigScreenOrderListDTO dto);

    /**
     * APP周边检索
     * @param dto
     * @return
     */
    List<WarningOrderListVO> getOrderListByPeriphery(AlarmOrderDTO dto);

    /**
     * 根据经纬度递归查询告警信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/18
     * @version
     */
    TableDataInfo getOrderByLongitude(BigScreenOrderListDTO dto);


    /**
     * 获取告警数据
     *
     * @return
     * @author chenmf
     * @date 2022/5/9
     * @version
     */
    OrderInfoVO getWarningOrderById(String warningOrderId,String tableName);

    List<OrderInfoVO> getWarningOrderById(String[] warningOrderId,String tableName);
//    /**
//     * 保存告警数据到mongOodb
//     *
//     * @return
//     * @author chenmf
//     * @date 2022/3/29
//     * @version
//     */
//    int saveWarningOrder(List<WarnOrderDTO> warnOrderList);

    /**
     * 判断是否有编辑权限 1:可编辑   0：不可编辑
     * @param isAdmin
     * @param userId
     * @param vo
     * @param flowLinkIds
     * @return
     */
    int ifEditHandle(boolean isAdmin, String userId, WarningOrderListVO vo,String flowLinkIds);

    /**
     * 物联告警事件图片更新
     *
     * @return
     * @author chenmf
     * @date 2022/6/6
     * @version
     */
    void updateWarningOrderImg(String warningOrderId, JSONObject data,String tableType,String tableName);


    /**
     * 卫星告警事件图片更新
     *
     * @return
     * @author fanzw
     * @version
     */
    void updateSatelliteWarningImg(String[] warningOrderId, String tableName, JSONObject data,String tableType,String selectAlgorithm);

    void updateSatelliteAlarmImg(List<OrderInfoVO> orderInfoVOs , String tableName);

    /**
     * 二次识别结果更新
     *
     * @return
     * @author cuisj
     * @version
     */
    void updateSecondRecogResult(List<SecondRecogBackVO> list);

    /**
     * 二次识别任务告警批量查询
     *
     * @return
     * @author cuisj
     * @version
     */
    List<SecondRecogBackVO> alarmByTaskBatchId (String taskBatchId,String platFormCode,String toPlatformCode,String type);

    /**
     * 二次识别下发
     *
     * @return
     * @author cuisj
     * @version
     */
    String synAnalysisTask(List<SecondRecogSendVO> list);

    /**
     * 无人机二次识别下发
     *
     * @return
     * @author cuisj
     * @version
     */
    String uavSynAnalysisTask(List<SecondRecogSendVO> list);


    /**
     * 公共二次识别下发
     *
     * @return
     * @author cuisj
     * @version
     */
    String publicSynAnalysisTask(List<SecondRecogSendVO> list);


    /**
     * 校验工单状态是否异常
     * @param alarmOrderDTO
     * @return
     */
    List<WarnOrderStatusDTO> checkOrderStatusList(AlarmOrderDTO alarmOrderDTO);

    /**
     * 处理传入的组织条件
     * @param dto
     */
     void handleOrgIdList(AlarmOrderDTO dto);

    /**
     * 根据位置及租户id行业版本获取网格信息
     * @param req
     * @return
     */
    GridInfo getGridInfoByLongitudeAndLatitude(GridInfoReq req);

    /**
     * 获取图斑
     * @return
     */
    Result getPolygonOperate(PolygonOperateDTO polygonOperateDTO);

    /**
     * 获取音频文件播放时长
     * @return
     */
    Result getDurationByAddress(VoiceDurationDTO voiceDuration);

    /**
     * 功能描述 根据设备、通道、有效参考点、型号信息等参数将百分比坐标集合转化为经纬度坐标集合返回
     * @return
     */
    Result<DevicePointTurn> turnLatitudeByPoint(DevicePointTurn devicePointTurn);

    /**
     * 获取设备参数信息
     *
     * @param camaraDeviceParamsQueryDTO 查询参数
     */
    Result<CameraCoordRes> getCamaraDeviceParamsInfo(CamaraDeviceParamsQueryDTO camaraDeviceParamsQueryDTO);

    /**
     * 针对告警来源为“AI告警”
     *  在“事件描述”字段中展示相应算法配置的参数
     *  （即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
     *
     * @param orderInfoVO 传入参数 格式
     *                       {
     *                          "warningOrderId": "", //工单编号
     *                          "deviceCode": "",   //摄像机编号
     *                          "channelCode": "",  //通道编号
     *                          "taskId": "",       //任务ID
     *                          "algorithmCode": "" //算法编码
     *                       }
     *
     */
    String buildAlgorithmConfigDescField(OrderInfoVO orderInfoVO);

    /**
     * 针对告警来源为“AI告警”
     *  在“事件描述”字段中展示相应算法配置的参数
     *  （即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
     *
     * @param req 传入参数 格式
     *                       [{
     *                          "warningOrderId": "", //工单编号
     *                          "deviceCode": "",   //摄像机编号
     *                          "channelCode": "",  //通道编号
     *                          "taskId": "",       //任务ID
     *                          "algorithmCode": "" //算法编码
     *                       }]
     *             orderIdAlias 工单ID别名
     *             orderSourceAlias 工单状态别名
     *
     *
     *
     * @retrun 返回map key->warningOrderId
     *                value->拼装好的描述
     */
    Map<String, String> buildAlgorithmConfigDescFieldMap(AiAlgorithmConfigReq req);

    /**
     * 返回所属组织
     * @param reqList
     * @return
     */
    List<Map<String, String>> getOrgInfoList(List<OrganInfoReq> reqList);

    /**
     * 返回产生过告警的告警类型编码
     * @param dto
     * @return
     */
    List<String> findAlarmTypesByTime(AlarmOrderDTO dto);

    /**
     * 查询顺序配置
     * @param configVo
     * @return
     */
    Result queryOrderConfig(AlarmDetailConfigVo configVo);

    /**
     * 更新保存顺序配置
     * @param list
     * @return
     */
    Result updateOrderConfig(List<AlarmDetailConfigVo> list);

    /**
     * 智能研判任务识别
     * @param taskRequest
     * @return
     */
    JSONObject synIntelligentJudgeTask(IntelligentJudgeTaskRequest taskRequest);

    void updateSecondRecogResultV2(List<SecondRecogBackVO> list);
}
