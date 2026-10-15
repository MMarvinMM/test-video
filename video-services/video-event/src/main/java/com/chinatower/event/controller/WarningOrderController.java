package com.chinatower.event.controller;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.api.model.AiAlgorithmConfigReq;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.dto.*;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.dto.AlarmOrderDuplicateDTO;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.dto.WarningOrderDTO;
import com.chinatower.event.dto.gridinfo.GridInfoReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.vo.AlarmDetailConfigVo;
import com.chinatower.event.vo.LeaderInfoByOrderIdVO;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.dataobject.OrderLeaderAuditDO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.WarningOrderVO;
import com.chinatower.platform.common.utils.poi.ExcelUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * video-event
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-20
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/order")
public class WarningOrderController extends TowerBaseController {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IOrderLeaderAuditRepository orderLeaderAuditRepository;

    @Autowired
    private IWarningOrderForTradeRepository warningOrderForTradeRepository;

    @Autowired
    private IWarningInterceptRepository warningInterceptRepository;

    @Autowired
    private IWarningOrderCollectionRepository warningOrderCollectionRepository;

    @Autowired
    private SysMapper sysMapper;
    /**
     * 功能描述：工作台工单列表查询
     *
     * @param dto
     * @param request
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:47
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getOrderList")
    public TableDataInfo getOrderList(@RequestBody AlarmOrderDTO dto, ServletRequest request) {
        log.info("WarningOrderController-getOrderList-req:{}", JSON.toJSONString(dto));
        return warningOrderRepository.getOrderList(dto, request);
    }

    /**
     * 功能描述：根据通道编码预置位查询工单列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:47
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getOrderListByDeviceChannelAndPresetId")
    public TableDataInfo getOrderListByDeviceChannelAndPresetId(@RequestBody AlarmOrderParmaDto dto) {
        return warningOrderRepository.getOrderListByDeviceChannelAndPresetId(dto);
    }

    /**
     * 功能描述：获取设备下工单数量
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:47
     * @version:
     */
    @PostMapping("/getOrderListByLot")
    public Result getOrderListByLot(@RequestBody AlarmOrderParmaDto dto) {
        return Result.data(warningOrderRepository.getOrderListByDeviceChannelAndPresetIdGroup(dto));
    }


    /**
     * 功能描述：获取物联设备下工单
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:47
     * @version:
     */
    @PostMapping("/getOrderListByDeviceChannelAndPresetIdGroup")
    public Result getOrderListByDeviceChannelAndPresetIdGroup(@RequestBody AlarmOrderParmaDto dto) {
        return Result.data(warningOrderRepository.getOrderListByDeviceChannelAndPresetIdGroup(dto));
    }
    /**
     * 功能描述：获取领导批示列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:47
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getLeaderAuditList")
    public TableDataInfo getLeaderAuditList(@RequestBody LeaderAuidtListDTO dto) {
        log.info("WarningOrderController-getLeaderAuditList-req:{}", JSON.toJSONString(dto));
        startPage();
        List<LeaderAuidtListVO> list = warningOrderRepository.getLeaderAuditList(dto);
        if (CollectionUtils.isEmpty(list)) {
            return getDataTable(new ArrayList<>());
        }
        return getDataTable(list);
    }

    /**
     * 功能描述：获取告警详情
     * 原来参数String orderId, String orderStatus
     * @param dto
     * @return Result<Map<List<WarningOrderInfoVO>>>
     * @author zhen.yan
     * @date 2022/6/15 13:48
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getOrderInfo")
    public Result<Map<String, List<WarningOrderInfoVO>>> getOrderInfo(@RequestBody AlarmOrderDTO dto) {
        try {
            Map row = new HashMap();
            List<WarningOrderInfoVO> rows = new ArrayList<>();
            rows.add(warningOrderRepository.getOrderInfo(dto));
            row.put("rows", rows);
            return Result.data(row);
        } catch (Exception e) {
            log.error("获取告警详情失败:", e);
            return Result.fail("获取告警详情失败");
        }
    }

    /**
     * 功能描述：获取告警编辑权限
     * @param dto
     * @return
     */
    @PostMapping("/getOrderIfEdit")
    public Result getOrderIfEdit(@RequestBody IfEditReq dto) {
        try {
            return Result.data(warningOrderRepository.getIfEdit(dto));
        } catch (Exception e) {
            log.error("getOrderIfEdit:", e);
            return Result.fail("获取告警编辑权限失败");
        }
    }

    /**
     * 功能描述：根据工单ID查询事件，返回收藏的事件来源
     * @param dto
     * @return
     */
    @PostMapping("/getCollectionSourceByOrderId")
    public Result getCollectionSourceByOrderId(@RequestBody AlarmOrderDTO dto) {
        try {
            if(StringUtils.isEmpty(dto.getOrderId())){
                return Result.fail("告警事件编码不能为空！");
            }
            return Result.data(warningOrderRepository.getCollectionSourceByOrderId(dto));
        } catch (Exception e) {
            log.error("getCollectionSourceByOrderId:", e);
            return Result.fail("根据工单ID获取收藏事件来源标识失败！");
        }
    }

    /**
     * 功能描述：根据位置及租户id行业版本获取网格信息
     * @param req
     * @return
     */
    @PostMapping("/getGridInfoByLongitudeAndLatitude")
    public Result getGridInfoByLongitudeAndLatitude(@RequestBody GridInfoReq req){
        return Result.data(warningOrderRepository.getGridInfoByLongitudeAndLatitude(req));
    }


    /**
     * 功能描述：通过告警工单ID获取处理环节信息
     *
     * @param dto
     * @return Result<List<Map>>
     * @author zhen.yan
     * @date 2022/6/15 13:48
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getRecordByOrderId")
    public Result<List<Map>> getRecordByOrderId(@RequestBody AlarmOrderDTO dto) {
        return result(warningOrderRepository.getRecordByOrderId(dto));
    }
    /**
     * 功能描述：通过告警工单ID获取处理环节信息(升序)
     *
     * @param dto
     * @return Result<List<Map>>
     * @author song
     * @date 2022/10/25 13:48
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getRecordByOrderIdAsc")
    public Result<List<Map>> getRecordByOrderIdAsc(@RequestBody AlarmOrderDTO dto) {
        return result(warningOrderRepository.getRecordByOrderIdAsc(dto));
    }

    /**
     * 功能描述：获取从告警数据列表
     *
     * @param dto
     * @return TableDataInfo
     * @author chenmf
     * @date 2021/12/02
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/warningOrderList")
    public TableDataInfo warningOrderList(@RequestBody WarningOrderDTO dto) {
        startPage();
        if (StringUtils.isEmpty(dto.getMainAlarmId())) {
            return getDataTable(null);
        }
        List<WarningOrderVO> list = warningOrderRepository.warningOrderList(dto);
        return getDataTable(list);
    }

    /**
     * 功能描述：事件处理时间轴
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:49
     * @version:
     */
    @PostMapping("/getTimeAxis")
    public Result getTimeAxis(@RequestBody AlarmOrderDTO dto) {
        if (StringUtils.isEmpty(dto.getOrderId())) {
            return result(null);
        }
        List<Map> list = warningOrderRepository.getTimeAxis(dto);
        return result(list);
    }

    /**
     * 功能描述：获取流程流转过程数据
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:49
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getRecordByType")
    public Result getRecordByType(@RequestBody AlarmOrderDTO dto) {
        List<FlowRecordVO> list = warningOrderRepository.getRecordByType(dto);
        return result(list);
    }

    /**
     * 功能描述：通过告警工单id获取领导批示列表
     *
     * @param dto
     * @return Result<List<LeaderInfoByOrderIdVO>>
     * @author zhen.yan
     * @date 2022/6/15 13:50
     * @version:
     */
    @PostMapping("/getLeaderListByOrderId")
    public Result<List<LeaderInfoByOrderIdVO>> getLeaderListByOrderId(@RequestBody AlarmOrderDTO dto) {
        List<LeaderInfoByOrderIdVO> list = warningOrderRepository.getLeaderListByOrderId(dto);
        return result(list);
    }

    /**
     * 功能描述：获取领导批示详情
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:50
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getLeaderAufitInfo")
    public Result getLeaderAufitInfo(@RequestBody OrderLeaderAuditDO dto) {
        Map map = new HashMap();
        try {
            map = warningOrderRepository.getLeaderAufitInfo(dto);
        } catch (Exception e) {
            log.error("获取领导批示详情,",e);
            return Result.fail(e.getMessage());
        }
        return Result.data(map);
    }

    /**
     * 功能描述：领导批示
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:51
     * @version:
     */
    @PostMapping("/updateLeaderAudit")
    public Result updateLeaderAudit(@RequestBody OrderLeaderAuditDO dto) {
        if (dto == null || StringUtils.isEmpty(dto.getAuditId())) {
            return Result.fail("参数错误");
        }
        int num = orderLeaderAuditRepository.updateLeaderAudit(dto);
        if (num <= 0) {
            return Result.fail("保存失败");
        }
        return Result.success("");
    }

    /**
     * 功能描述：添加报警事件-人工上报
     *
     * @param receiveAlarmVO
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:51
     * @version:
     */
    @TowerLog(title = "添加报警事件-人工上报", businessType = BusinessType.INSERT)
    //@PostMapping("/addAlarmOrder")
    public Result addAlarmOrder(@RequestBody ReceiveAlarmVO receiveAlarmVO) {
        return warningOrderRepository.addAlarmOrder(receiveAlarmVO);
    }

    /**
     * 功能描述：第三方告警上报
     * @param receiveAlarmVO
     * @return
     */
    @TowerLog(title = "第三方告警上报", businessType = BusinessType.INSERT)
    //@PostMapping("/addThirdAlarmOrder")
    public Result addThirdAlarmOrder(@RequestBody List<ReceiveAlarmVO> receiveAlarmVO) {
        log.info("addThirdAlarmOrder第三方告警接收入参:{}",JSON.toJSONString(receiveAlarmVO));
        return warningOrderRepository.addThirdAlarmOrder(receiveAlarmVO);
    }

    /**
     * 功能描述：导出告警事件列表
     *
     * @param dto
     * @author dencycheng
     * @date 2022/11/11
     * @version:
     */
    @PostMapping("/exportWarningOrderListV2")
    public Result<SysFileInfoResponseDTO> exportWarningOrderListV2(@RequestBody AlarmOrderQuery dto) {

        AlarmOrderDTO dtoTo = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto, dtoTo);
        List<WarningOrderExport> exportList = warningOrderRepository.exportWarningOrderList(dtoTo);
        byte[] data = new ExcelUtil<>(WarningOrderExport.class).exportBytes(exportList);
        SysFileInfoResponseDTO sysFileInfoResponse = new SysFileInfoResponseDTO();
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        os.write(data, 0, data.length);
        byte[] responseBytes = os.toByteArray();
        sysFileInfoResponse.setFileContent(responseBytes);
        return Result.data(sysFileInfoResponse, "下载成功");
    }
    /**
     * 功能描述：导出告警事件列表
     *
     * @param dto
     * @author dencycheng
     * @date 2022/11/11
     * @version:
     */
    @PostMapping("/getExportWarningOrderListV2")
    public Result<List> exportWarningOrderList(@RequestBody AlarmOrderQuery dto) {
        try {
            AlarmOrderDTO dtoTo = new AlarmOrderDTO();
            BeanUtils.copyProperties(dto, dtoTo);
            List<WarningOrderExport> exportList = warningOrderRepository.exportWarningOrderList(dtoTo);
            return Result.data(exportList);
        } catch (Exception e) {
            log.error("导出告警列表异常：", e);
        }
        return Result.fail();
    }

    /**
     * 功能描述：查询用户代办事项数量
     *
     * @param req
     * @param request
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:01
     * @version:
     */
    @PostMapping("/getTodoNum")
    public Result getTodoNum(@RequestBody AppOrderListByUserDTO req, ServletRequest request) {
        return warningOrderRepository.getTodoNum(req);
    }

    /**
     * 功能描述：告警事件调度
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:04
     * @version:
     */
    @TowerLog(title = "告警事件调度", businessType = BusinessType.UPDATE)
    @PostMapping("/sendAlarmOrder")
    public Result sendAlarmOrder(@RequestBody SendAlarmOrderDTO dto) {
        log.info("WarningOrderController-sendAlarmOrder:{}", JSON.toJSONString(dto));
        return warningOrderRepository.sendAlarmOrder(dto);
    }

    /**
     * 功能描述：告警事件受理
     * @param dto
     * @return
     */
    @TowerLog(title = "告警事件受理", businessType = BusinessType.UPDATE)
    @PostMapping("/accept")
    public Result accept(@RequestBody TradeAcceptOrderDto dto) {
        log.info("WarningOrderController-accept:{}", JSON.toJSONString(dto));
        return warningOrderRepository.accept(dto);
    }

    /**
     * 功能描述：告警事件转派
     * @param dto
     * @return
     */
    @TowerLog(title = "告警事件转派", businessType = BusinessType.UPDATE)
    @PostMapping("/reassignment")
    public Result reassignment(@RequestBody TradeReassignmenOrdertDto dto) {
        log.info("WarningOrderController-reassignment:{}", JSON.toJSONString(dto));
        return warningOrderRepository.reassignment(dto);
    }

    /**
     * 功能描述：研判-假警、真警、重复告警处理
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:05
     * @version:
     */
    @TowerLog(title = "研判：假警、真警、重复告警处理", businessType = BusinessType.UPDATE)
    @PostMapping("/checkAlarmOrder")
    public Result checkAlarmOrder(@RequestBody JudgeAlarmOrderDTO dto) {
        log.info("WarningOrderController-checkAlarmOrder入参:{}", JSON.toJSONString(dto));
        return warningOrderRepository.checkAlarmOrder(dto);
    }

    /**
     * 功能描述：督办
     *
     * @param dto
     * @return Result
     * @author lyy
     * @date 2024/06/25 15:58
     * @version v1.6.80
     */
    @PostMapping("/urgeToHandleAlarmOrder")
    public Result urgeToHandleAlarmOrder(@RequestBody SendSupervisionMsgDTO dto) {
        log.info("WarningOrderController-urgeToHandleAlarmOrder入参:{}", JSON.toJSONString(dto));
        return warningOrderRepository.urgeToHandleAlarmOrder(dto);
    }

    /**
     * 功能描述：根据工单ID查询督办记录
     *
     * @param dto
     * @return Result<List<HandleRecordVO>>
     * @author lyy
     * @date 2024/06/26 11:43
     * @version:
     */
    @PostMapping("/getHandleRecordByOrderId")
    public Result<List<HandleRecordVO>> getHandleRecordByOrderId(@RequestBody AlarmOrderDTO dto) {
        List<HandleRecordVO> list = warningOrderRepository.getHandleRecordByOrderId(dto);
        return result(list);
    }

    /**
     * 功能描述：研判为重复告警时，获取事件列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 14:05
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getOrderListForDuplication")
    public TableDataInfo getOrderListForDuplication(@RequestBody AlarmOrderDuplicateDTO dto) {
        return warningOrderRepository.getOrderListForDuplication(dto);
    }

    /**
     * 功能描述：获取告警列表-告警事件大屏右侧列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/7
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getAlarmListForBigScreen")
    public TableDataInfo getAlarmListForBigScreen(@RequestBody BigScreenOrderListDTO dto) {
        return warningOrderRepository.getAlarmListForBigScreen(dto);
    }

    /**
     * 功能描述：根据租户按照经纬度递归查询告警信息
     *
     * @param dto
     * @param request
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:06
     * @version:
     */
    //@EventObsEncrypt
    @PostMapping("/searchOrderNumByLongitude")
    public Result searchOrderNumByLongitude(@RequestBody SearchOrderDTO dto, ServletRequest request) {
        return warningOrderRepository.searchOrderNumByLongitude(dto, request);
    }


    /**
     * 功能描述：根据租户按照经纬度递归查询告警信息
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 14:06
     * @version:
     */
    //@EventObsEncrypt
    @PostMapping("/searchOrderNumByLongitude4Industry")
    public Result searchOrderNumByLongitude4Industry(@RequestBody SearchOrderDTO dto) {
        return warningOrderRepository.searchOrderNumByLongitude(dto, null);
    }


    /**
     * 功能描述：获取告警列表-告警事件大屏左侧查询
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 14:08
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getOrderListForBigScreen")
    public TableDataInfo getOrderListForBigScreen(@RequestBody BigScreenOrderListDTO dto) {
        return warningOrderRepository.getOrderListForBigScreen(dto);
    }

    /**
     * 功能描述：根据时间范围获取告警事件总数
     * @param dto
     * @return TableDataInfo
     * @version v1.6.10
     */
    @PostMapping("/getAlarmListCount")
    public TableDataInfo getAlarmListCount(@RequestBody BigScreenOrderListDTO dto) {
        return warningOrderRepository.getAlarmListCount(dto);
    }

    /**
     * 功能描述：地图打点-根据设备编码集合查询设备是否有未完结告警
     * @param dto
     * @return Result
     * @version v1.6.10
     */
    @PostMapping("/getOrderNumByDeviceCode")
    public Result<List<OrderCompleteVO>> getOrderNumByDeviceCode(@RequestBody AlarmOrderParmaDto dto) {
        return warningOrderRepository.getOrderNumByDeviceCode(dto);
    }

    /**
     * 功能描述：根据时间范围、分页获取经纬度列表
     * @param dto
     * @return TableDataInfo
     * @version v1.6.10
     */
    @PostMapping("/getOrderLongitudeList")
    public TableDataInfo getOrderLongitudeList(@RequestBody BigScreenOrderListDTO dto) {
        return warningOrderRepository.getOrderLongitudeList(dto);
    }

    /**
     * 功能描述：APP周边检索
     *
     * @param dto
     * @return Result<List<WarningOrderListVO>>
     * @author zhen.yan
     * @date 2022/6/15 14:08
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getOrderListByPeriphery")
    public Result<List<WarningOrderListVO>> getOrderListByPeriphery(@RequestBody AlarmOrderDTO dto) {
        List<WarningOrderListVO> list = warningOrderRepository.getOrderListByPeriphery(dto);
        return Result.data(list);
    }

    /**
     * 功能描述：根据经纬度递归查询告警信息列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 14:08
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/searchOrderListByLongitude")
    public TableDataInfo getOrderByLongitude(@RequestBody BigScreenOrderListDTO dto) {
        return warningOrderRepository.getOrderByLongitude(dto);
    }

    /**
     * 功能描述：查询关联告警事件信息
     *
     * @param dto
     * @return Result<List<WarningOrderInfoVO>>
     * @author zhen.yan
     * @date 2022/6/15 14:08
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/warningOrderInfoRel")
    public Result<List<WarningOrderInfoVO>> warningOrderInfoRel(@RequestBody WarningOrderRelDTO dto) throws Exception {
        log.info("WarningOrderController-warningOrderInfoRel-req:{}", JSON.toJSONString(dto));
        return warningOrderForTradeRepository.warningOrderInfoRel(dto);
    }

    /**
     * 功能描述：获取图斑
     * @return
     */
    @PostMapping("/getPolygonOperate")
    public Result getPolygonOperate(@RequestBody PolygonOperateDTO polygonOperateDTO) {
        return warningOrderRepository.getPolygonOperate(polygonOperateDTO);
    }



    /**
     * 功能描述：获取音频文件播放时长
     * @return
     */
    @PostMapping("/getDurationByAddress")
    public Result getDurationByAddress(@RequestBody VoiceDurationDTO voiceDuration) {
        return warningOrderRepository.getDurationByAddress(voiceDuration);
    }

    /**
     * 功能描述：根据设备、通道、有效参考点、型号信息等参数将百分比坐标集合转化为经纬度坐标集合返回
     *
     * @param devicePointTurn
     * @return Result
     * @date 2022/8/21 09:41
     * @version:
     */
    @PostMapping("/turnLatitudeByPoint")
    public Result<DevicePointTurn> turnLatitudeByPoint(@RequestBody DevicePointTurn devicePointTurn) {
        Result<DevicePointTurn> result = warningOrderRepository.turnLatitudeByPoint(devicePointTurn);
        return result;
    }

    /**
     * 功能描述：获取摄像头设备参数信息
     *
     * @param camaraDeviceParamsQueryDTO 查询参数
     */
    @PostMapping("/getCameraCoord")
    public Result<CameraCoordRes> getCamaraDeviceParamsInfo(@RequestBody CamaraDeviceParamsQueryDTO camaraDeviceParamsQueryDTO){
        return warningOrderRepository.getCamaraDeviceParamsInfo(camaraDeviceParamsQueryDTO);
    }

    /**
     * 功能描述：构建扩展字段信息-展示相应算法配置的参数
     * 针对告警来源为“AI告警”,在“事件描述”字段中展示相应算法配置的参数（即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
     *
     * @param obj 传入参数 格式
     *                       [{
     *                          "warningOrderId": "", //工单编号
     *                          "deviceCode": "",   //摄像机编号
     *                          "channelCode": "",  //通道编号
     *                          "taskId": "",       //任务ID
     *                          "algorithmCode": "" //算法编码
     *                       }]
     *
     *
     * @retrun 返回map key->warningOrderId
     *                value->拼装好的描述
     */
    @PostMapping("/buildAlgorithmConfigDescFieldMap")
    public Result<Map<String, String>> buildAlgorithmConfigDescFieldMap(@RequestBody AiAlgorithmConfigReq req){
        return Result.data(warningOrderRepository.buildAlgorithmConfigDescFieldMap(req));
    }

    /**
     * 查询告警所属组织
     * @param reqList
     * @return
     */
    @PostMapping("/getOrgInfoList")
    public List<Map<String, String>> getOrgInfoList(@RequestBody List<OrganInfoReq> reqList){
        return warningOrderRepository.getOrgInfoList(reqList);
    }

    /**
     * 根据时间查询该租户行业下产生过告警的告警类型
     * @param dto
     * @return
     */
    @PostMapping("/findAlarmTypesByTime")
    public List<String> findAlarmTypesByTime(@RequestBody AlarmOrderDTO dto){
        return warningOrderRepository.findAlarmTypesByTime(dto);
    }

    /**
     * 功能描述：查询事件详情图片、视频展示顺序配置
     *
     * @param configVo
     * @return Result
     * @date 2025/04/28 18:31
     * @version v1.7.45
     */
    @PostMapping("/queryOrderConfig")
    public Result queryOrderConfig(@RequestBody AlarmDetailConfigVo configVo) {
        return Result.data(warningOrderRepository.queryOrderConfig(configVo));
    }

    /**
     * 修改保存顺序配置
     * @param configList
     * @return
     */
    @PostMapping("/updateOrderConfig")
    public Result updateOrderConfig(@RequestBody List<AlarmDetailConfigVo> configList) {
        return Result.data(warningOrderRepository.updateOrderConfig(configList));
    }
}
