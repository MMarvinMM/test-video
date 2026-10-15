package com.chinatower.event.mapper;

import java.util.List;
import java.util.Map;

import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.common.core.vo.AlarmClassTimeVO;
import com.chinatower.event.dataobject.WarningOrderDO;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.AlarmOrderDuplicateDTO;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.dto.WarningOrderDTO;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.vo.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.device.AlgorithmConfigVO;
import com.chinatower.platform.entity.device.AlgorithmGiveConfigSaveVO;
import org.apache.ibatis.annotations.Param;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.kafka.common.protocol.types.Field;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-20
 */
public interface WarningOrderMapper extends BaseMapper<WarningOrderDO> {

    /**
     * 工作台列表查询计数
     * @param dto
     * @Auther yxb
     * @return
     */
    int queryAlarmOrderCount(@Param("param") AlarmOrderDTO dto);

    /**
     * 工作台列表查询计数-国产化
     * @param dto
     * @Auther yxb
     * @return
     */
    int queryAlarmOrderCountLocal(@Param("param") AlarmOrderDTO dto);

    /**
     * 查询告警列表数量
     * @param dto
     * @return
     */
    int selectWarningOrderCount(@Param("param") AlarmOrderDTO dto);

    String getIfEdit(@Param("param") IfEditReq dto);


    /**
     * 工作台列表查询
     * @Auther yxb
     * @return
     */
    List<WarningOrderResultCountVo> queryAlarmOrderGroup(@Param("param") AlarmOrderParmaDto alarmOrderParmaDto);

    /**
     * 工作台列表查询
     * @Auther yxb
     * @return
     */
    List<WarningOrderResultCountVo> queryAlarmOrderLot(@Param("param") AlarmOrderParmaDto alarmOrderParmaDto);
    /**
     * 工作台列表查询计数
     * @param dto
     * @Auther yxb
     * @return
     */
    int queryAlarmOrderCountTwo(@Param("param") AlarmOrderParmaDto dto);

    /**
     * 工作台列表查询
     * @Auther yxb
     * @return
     */
    List<WarningOrderResultListVo> queryAlarmOrderTwo(@Param("param") AlarmOrderParmaDto alarmOrderParmaDto);

    /**
     * 工作台列表查询
     * @param alarmOrderListReq
     * @Auther yxb
     * @return
     */
    List<WarningOrderListVO> queryAlarmOrder(@Param("param") AlarmOrderDTO alarmOrderListReq);

    /**
     * 工作台列表查询-国产化
     * @param alarmOrderListReq
     * @Auther yxb
     * @return
     */
    List<WarningOrderListVO> queryAlarmOrderLocal(@Param("param") AlarmOrderDTO alarmOrderListReq);

    /**
     * 根据工单号查询告警数据
     * @param alarmOrderListReq
     * @return
     */
    List<WarningOrderListVO> queryAlarmOrderByWarningOrderIds(@Param("param") AlarmOrderDTO alarmOrderListReq);
    /**
     * 工作台列表查询
     * @param alarmOrderListReq
     * @Auther yxb
     * @return
     */
    List<WarningOrderListVO> queryAlarmOrderNoScope(@Param("param") AlarmOrderDTO alarmOrderListReq);

    /**
     * app我的收藏使用
     * @param alarmOrderListReq
     * @Auther yxb
     * @return
     */
    List<WarningOrderListVO> queryAlarmOrderByOrderList(@Param("param") AlarmOrderDTO alarmOrderListReq);

    /**
     * 工单查询领导批示数量
     * @param resultList
     * @return
     */
    List<OrderLeaderNumDTO> getLeaderAuditNum(@Param("list") List<WarningOrderListVO> resultList);


    /**
     * 领导批示列表
     *
     * @param dto
     * @return
     */
    List<LeaderAuidtListVO> getLeaderAuditList(@Param("param") LeaderAuidtListDTO dto);

    /**
     * 根据工单号查领导批示表
     *
     * @param dto
     * @return
     */
    List<LeaderAuidtListVO> getLeaderAuditListOnlyByOrderId(LeaderAuidtListDTO dto);


    /**
     * 获取预置位列表
     * @param dto
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<OrderInfoVO> getPresetListByDevice(@Param("param") AlarmOrderDTO dto);

    /**
     * 获取告警数量以预置位为维度聚合
     * @param dto
     * @return
     */
    List<OrderInfoVO> queryAlarmGroupByPreset(@Param("param") AlarmOrderDTO dto);

    /**
     * 获取告警工单详情
     * @param dto
     * @return
     */
    OrderInfoVO getOrderInfo(@Param("param") AlarmOrderDTO dto);

    /**
     * 查询该工单各环节上传的所有附件
     * @param orderId
     * @return
     */
    List<OrderFileDO> getOrderFileList(@Param("orderId") String orderId, @Param("tableName") String tableName);

    /**
     * 获取工单表或者回收站表原图和透传字段
     * @param dto
     * @return
     */
    List<OrderInfoVO> getOrderAlarmInfo(@Param("param") AlarmOrderDTO dto);

    /**
     * 从研判表中获取告警工单详情
     * @param dto
     * @return
     */
    OrderInfoVO getOrderInfo4AlarmOperate(@Param("param") AlarmOrderDTO dto);

    /**
     * 更新图片
     * @param map
     * @return
     */
    int updateAlarmImg(Map map);

    /**
     * 更新图片
     * @param
     * @return
     */
    int updateIotAlarmImg(@Param("orderInfoVO")OrderInfoVO orderInfoVO, @Param("tableName") String tableName);

    /**
     * 更新卫星告警图片
     * @param orderInfoVOs
     * @param tableName
     * @return
     */
    int updateSatelliteAlarmImg(@Param("orderInfoVOs") List<OrderInfoVO> orderInfoVOs , @Param("tableName") String tableName);

    /**
     * 更新二次识别结果
     * @param updateList
     * @return
     */
    int updateSecondRecogResult(List<SecondRecogBackVO> updateList);


    /**
     * 通过告警工单ID获取处理环节信息
     * @param orderId,tableName
     * @return
     */
    List<Map> getRecordByOrderId(@Param("orderId") String orderId, @Param("tableName") String tableName);

    /**
     * 通过告警工单ID获取处理环节信息升序
     * @param orderId,tableName
     * @return
     */
    List<Map> getRecordByOrderIdAsc(@Param("orderId") String orderId, @Param("tableName") String tableName);

    /**
     * 获取处理环节状态
     * @param processOrderIdList
     * @return
     */
    List<Map> getProcessStatusById(@Param("processOrderIdList") List<String> processOrderIdList);

    /**
     * 获取核实环节状态
     * @param approveOrderIdList
     * @return
     */
    List<Map> getApproveStatusById(@Param("list") List<String> approveOrderIdList);

    /**
     * 事件处理时间轴
     * @param dto
     * @return
     */
    List<Map> getTimeAxis(@Param("param") AlarmOrderDTO dto);

    /**
     * 通过告警工单ID获取研判过程
     * @param map
     * @return
     */
    List<FlowRecordVO> getPendingJudgeList(@Param("param") Map map);

    /**
     * 查询告警内容
     * @param map
     * @return
     */
    OrderInfoVO getAlarmBody(Map map);

    /**
     * 调度过程
     * @param map
     * @return
     */
    List<FlowRecordVO> getPendingDispatchByParam(@Param("param") Map map);

    /**
     * 通过告警工单ID获取处置过程
     * @param dto
     * @return
     */
    List<FlowRecordVO> getPendingHandle(@Param("param") OrderFlowRecordDTO dto);

    /**
     * 核实过程
     * @param map
     * @return
     */
    List<FlowRecordVO> getPendingCheckByParam(@Param("param")Map map);

    /**
     * 通过告警工单id获取领导批示列表
     * @param tableName
     * @param orderId
     * @return
     */
    List<LeaderInfoByOrderIdVO> getLeaderAuditListByOrderId(@Param("tableName")String tableName, @Param("orderId")String orderId);

    /**
     * 保存AI告警信息-批量保存
     * @param receiveAlarmDTO
     * @return
     */
    int insertWarningOrders(ReceiveAlarmDTO receiveAlarmDTO);

    /**
     * 创建表
     * @param tableNewName
     * @param tableOldName
     * @return
     */
    int createNewTable(@Param("tableNewName") String tableNewName, @Param("tableOldName") String tableOldName);

    // 根据组织id 查询用户id
    public List<UsersVO> queryUserByOrgId(UsersVO usersVO);

    /**
     * @Description 获取流程时间信息
     * @author wjd
     * @Date 15:42 2021/7/7
     * @Param [alarmClassTimeVO]
     **/
    List<Map<String, Object>> getTimeLimitByInfo(AlarmClassTimeVO alarmClassTimeVO);

    /**
     * @Description 获取通知方式
     * @author wjd
     * @Date 17:07 2021/7/7
     * @Param [body]
     **/
    Map<String, String> findNoticeModel(@Param("body") AlarmClassTimeVO body);

    /**
     * 查询告警数据
     * @param a
     * @return
     */
    WarnOrderStatusDTO getAlarmOrder(AlarmOrderDTO a);

    /**
     * 查询研判告警数据
     * @param a
     * @return
     */
    List<WarnOrderStatusDTO> getAlarmOrderListByJudge(AlarmOrderDTO a);

    /**
     * 查询告警数据
     * @param a
     * @return
     */
    List<WarnOrderStatusDTO> getAlarmOrderList(AlarmOrderDTO a);

    /**
     *
     * @param orgInfoVO
     * @return
     */
    List<OrgInfoVO> getOrgAndDeviceInfoById(OrgInfoVO orgInfoVO);

    /**
     *
     * @param orgInfoVO
     * @return
     */
    List<OrgInfoVO> getOrgInfoById(OrgInfoVO orgInfoVO);

    /**
     * 告警列表
     * @param dto
     * @return
     */
    List<DuplicateOrderVO> getOrderList(@Param("param") OrderListDTO dto);


    /**
     * 根据告警id查询告警列表
     * @param map
     * @return
     */
    List<Map> getWarningOrderList(Map map);
    /**
     * 事项数量
     * @param req
     * @return
     */
    int getOrderListCount(@Param("param") OrderListDTO req);

    /**
     * 查询当前用户待批示数量
     * @param param
     * @return
     */
    int getTodoNumForLeader(@Param("param") AppOrderListByUserDTO param);

    int getTodoNumLeader(@Param("param") OrderListDTO param);

    /**
     * 查询设备高度
     * @param deviceCode
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    String getDeviceInfo(String deviceCode);

    /**
     *
     * @param deviceCode
     * @return
     */
    Map getDiviceInfo(@Param("deviceCode") String deviceCode);

    /**
     *
     * @param a
     * @return
     */
    int updateByPrimaryKeySelective(SendAlarmOrderDTO a);

    /**
     *
     * @param
     * @return
     */
    int updateByPrimaryKeySelectiveByList(@Param("list") List<SendAlarmOrderDTO> list);

    /**
     * 领导批示表：根据工单号更新告警原因
     * @param list
     * @return
     */
    int updateLeaderAuditByOrderId(@Param("list") List<LeaderAuditDto> list);

    /**
     * 重复告警总数
     * @param dto
     * @return
     */
    int getOrderListForDuplicationCOUNT(@Param("param") AlarmOrderDuplicateDTO dto);

    /**
     * 重复告警时查询告警事件列表
     * @param dto
     * @return
     */
    List<WarningOrderListVO> getOrderListForDuplication(@Param("param") AlarmOrderDuplicateDTO dto);


    /**
     * 获取告警列表-告警事件大屏列表用
     * @param req
     * @return
     */
    List<BigScreenOrderListVO> getOrderListForBigScreen(@Param("param") BigScreenOrderListDTO req);

    /**
     * 获取告警列表-告警事件大屏列表用
     * @param req
     * @return
     */
    int getOrderListForBigScreenCount(@Param("param") BigScreenOrderListDTO req);

    /**
     * 根据经纬度查询数据
     * @param searchOrderVO
     * @return
     */
    List<Map> searchOrderByLongitude(@Param("param") SearchOrderDTO searchOrderVO);

    /**
     * APP周边检索
     *
     * @return
     * @author chenmf
     * @date 2022/3/17
     * @version
     */
    List<WarningOrderListVO> getOrderListByPeriphery(@Param("param") AlarmOrderDTO dto);

    /**
     * 更新删除状态根据ID
     * @param dto
     * @return
     */
    int updateDeleteFlagById(@Param("param") WarningOrderDTO dto);


    /**
     * 事项数量
     * @param req
     * @return
     */
    int getOrderCount(@Param("param") OrderListDTO req);

    /**
     * 事项数量
     * @param tenantIds
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<UserInfoVO> getUserIdByTenantIdBatch(@Param("tenantIds") List<String> tenantIds);

    /**
     * 获取图斑
     * @param polygonOperateDTO
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map<String,Object>> getPolygonOperate(@Param("param") PolygonOperateDTO polygonOperateDTO);

    /**
     * 查询卡口车牌库
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<CheckpointVehicleLicensePlate> queryLicensePlateList(Map map);

    /**
     * 获取算法配置中配置的黑白名单json
     * @param map
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<CheckpointVehicleLicensePlate> getBlackWhiteJson(Map map);

    /**
     * 获取后端算法配置，租户和行业下的
     * @param map 查询参数
     *            deviceCode-摄像机编码
     *            channelCode-通道编码
     *
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<AlarmAlgorithmConfigVO> queryAlgorithmConfig(Map map);

    /**
     * 获取前端算法配置 根据租户，行业，告警类型，摄像机编码，通道编码查询出前端配置算法
     * @param map 查询参数
     *            alarmType-告警类型
     *            deviceCode-摄像机编码
     *            channelCode-通道编码
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<AlgorithmGiveConfigSaveVO> queryAlgorithmGiveConfig(Map map);

    /**
     * 查询顺序配置
     * @param userId
     * @return
     */
    List<AlarmDetailConfigVo> queryOrderConfig(@Param("userId") String userId);

    /**
     * 新增默认顺序配置
     * @param list
     * @return
     */
    int insertDefaultConfig(@Param("list") List<AlarmDetailConfigVo> list);

    /**
     * 修改顺序配置
     * @param list
     * @return
     */
    int updateOrderConfig(@Param("list") List<AlarmDetailConfigVo> list);

    /**
     * 返回产生过告警的告警类型编码
     * @param dto
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<String> findAlarmTypesByTime(@Param("param") AlarmOrderDTO dto);
}
