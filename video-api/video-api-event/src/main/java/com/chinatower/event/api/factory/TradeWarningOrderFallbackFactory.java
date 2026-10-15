package com.chinatower.event.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.event.entity.dto.ModifyLableDTO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.model.AiAlgorithmConfigReq;
import com.chinatower.event.api.model.AlarmOrderQueryDTO;
import com.chinatower.event.api.model.CheckAlarmByDeviceVO;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.api.model.receive.MessagePushVo;
import com.chinatower.event.api.model.receive.PushThirdReq;
import com.chinatower.event.api.model.receive.WarningLinkInfoReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderFallbackFactory.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/21 10:23
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Component
public class TradeWarningOrderFallbackFactory implements FallbackFactory<ITradeWarningOrderService> {

    @Override
    public ITradeWarningOrderService create(Throwable throwable) {
        return new ITradeWarningOrderService() {

            @Override
            public TableDataInfo getOrderList(TradeAlarmOrderDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public String getOrderListCount(TradeAlarmOrderDTO dto) {
                return "根据网格id获取当日告警事件总数量失败:" + throwable.getMessage();
            }

            @Override
            public Result getLabelAndColStatusByOrders(LabelAndColStatusReq req) {
                return Result.fail("getLabelAndColStatusByOrders失败:" + throwable.getMessage());
            }

            @Override
            public Result<Map> getInterceptInfoApi(ExportOrderInfoReq dto) {
                return Result.fail("getInterceptInfoApi失败:" + throwable.getMessage());
            }

            @Override
            public void setWarningLinkInfo(WarningLinkInfoReq req) {
                Result.fail("setWarningLinkInfo:" + throwable.getMessage());
            }


            @Override
            public List<Map> createRecord(List<ReceiveAlarmVO> customers) {
                 Result.fail("createRecord:" + throwable.getMessage());
                return new ArrayList<>();
            }

            @Override
            public void pushThirdOrder(PushThirdReq req) {
                Result.fail("pushThirdOrder:" + throwable.getMessage());
            }


            @Override
            public void messagePush(MessagePushVo vo) {
                Result.fail("messagePush:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getOrderListDownImgs(AlarmDownloadReq dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getInterceptList(AlarmDownloadReq dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getPresetListAndEventCount(TradeAlarmOrderDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<WarningOrderInfoVO> getOrderInfo(TradeAlarmOrderDTO dto) {
                return Result.fail("获取告警详情失败:" + throwable.getMessage());
            }

            @Override
            public Result addLeaderAudit(TradeOrderLeaderAuditDTO dto) {
                return Result.fail("转领导批示失败:" + throwable.getMessage());
            }

            @Override
            public Result submitProcess(TradeSendAlarmOrderDTO dto) {
                return Result.fail("事件流程流转失败:" + throwable.getMessage());
            }

            /**
             * 新增/取消收藏
             *
             * @param dto
             * @return
             */
            @Override
            public Result warningOrderCollection(WarningOrderCollectionDTO dto) {
                return Result.fail("新增/取消收藏失败:" + throwable.getMessage());
            }

            /**
             * 我的收藏-查询列表
             *
             * @param dto
             * @return
             */
            @Override
            public Result qryWarningOrderCollection(TradeAlarmOrderDTO dto) {
                return Result.fail("我的收藏-查询列表失败:" + throwable.getMessage());
            }

            @Override
            public Result qryWarningOrderCollectionImgs(AlarmDownloadReq dto) {
                return Result.fail("我的收藏-查询列表失败:" + throwable.getMessage());
            }

            /**
             * 查询订购关联的告警类型
             *
             * @param dto
             * @return
             */
            @Override
            public Result findOrderAlarm(AlarmTypeDTO dto) {
                return Result.fail("查询订购关联的告警类型失败:" + throwable.getMessage());
            }

            /**
             * 研判为重复告警时，获取事件列表
             *
             * @param dto
             */
            @Override
            public TableDataInfo getOrderListForDuplication(AlarmOrderDuplicateDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 流程回退
             *
             * @param dto
             * @return
             */
            @Override
            public Result rollBack(TradeSendAlarmOrderDTO dto) {
                return Result.fail("流程回退失败:" + throwable.getMessage());
            }

            /**
             * 获取执行办理过程数据
             *
             * @param dto
             * @return
             */
            @Override
            public Result getRecordByType(TradeSendAlarmOrderDTO dto) {
                return Result.fail("获取流程流转过程据失败:" + throwable.getMessage());
            }

            /**
             * 查询领导批示用户数据
             *
             * @param dto
             * @return
             */
            @Override
            public Result getLeaderByTenantId(SysUserDTO dto) {
                return Result.fail("查询领导批示用户数据失败:" + throwable.getMessage());
            }

            /**
             * 查询告警图层
             *
             * @param dto
             * @return
             */
            @Override
            public Result getWarningOrderLayer(WarningOrderLayerDTO dto) {
                return Result.fail("查询告警图层失败:" + throwable.getMessage());
            }

            /**
             * 更新告警事件
             *
             * @param dto
             * @return
             */
            @Override
            public Result modifyWarningOrderInfo(WarningOrderDTO dto) {
                return Result.fail("调用更新告警事件接口失败:" + throwable.getMessage());
            }

            /**
             * 查询关联告警事件信息
             *
             * @param dto
             * @return
             */
            @Override
            public Result<List<WarningOrderInfoVO>> warningOrderInfoRel(WarningOrderRelDTO dto) {
                return Result.fail("查询关联告警事件信息接口失败:" + throwable.getMessage());
            }

            /**
             * 生成告警压制
             *
             * @param dto
             * @return
             */
            @Override
            public Result createIntercept(InterceptConfigDTO dto) {
                return Result.fail("生成告警压制接口失败:" + throwable.getMessage());
            }

            /**
             * 查询用户代办事项数量
             *
             * @param dto
             * @return
             */
            @Override
            public Result getTodoNum(AppOrderListByUserDTO dto) {
                return Result.fail("查询用户代办事项数量接口失败:" + throwable.getMessage());
            }

            /**
             * 批量关闭告警
             *
             * @param dto
             * @return
             */
            @Override
            public Result batchEndWarningOrder(BatchEndWarningOrderDTO dto) {
                return Result.fail("批量关闭告警接口失败:" + throwable.getMessage());
            }

            /**
             * 根据流程ID，租户ID获取用户树
             *
             * @param model
             * @return
             */
            @Override
            public Result<SysInfoVo> getFlowByIdUserTree(FlowLinkAttrModelDTO model) {
                return Result.fail("获取用户树接口失败:" + throwable.getMessage());
            }

            @Override
            public Result<SysInfoVo> getFlowByIdUserTreeNew(FlowLinkAttrModel model) {
                return Result.fail("获取新用户树接口失败:" + throwable.getMessage());
            }

            @Override
            public Result getLeaderByTenantIdShowTree(ApiLeaderByTenantIdTreeReq model) {
                return Result.fail("获取领导树接口失败:" + throwable.getMessage());
            }

            @Override
            public Result queryLastProcess(ApiSelectLastRrocessReq model) {
                return Result.fail("获取当前工单所选环节最后一个处理人失败:" + throwable.getMessage());
            }

            @Override
            public Result reassignment(TradeReassignmenOrdertDto dto) {
                return Result.fail("转派调用接口失败:" + throwable.getMessage());
            }

            @Override
            public Result queryReassignLink(TradeQueryReassignLinkDto dto) {
                return Result.fail("查询可转派环节接口失败:" + throwable.getMessage());
            }

            /**
             * 行业查询处置单详情
             *
             * @param dto
             * @return
             */
            @Override
            public Result getProcessDetail(ProcessOrderDTO dto) {
                return Result.fail("获取处置单详情失败:" + throwable.getMessage());
            }

            @Override
            public Result batchJudgeWarningOrder(BatchJudgeWarningOrderDTO dto) {
                return Result.fail("告警批量研判失败:" + throwable.getMessage());
            }

            @Override
            public Result<SysFileInfoResponseDTO> exportWarningOrderListV2(AlarmOrderQuery dto) {
                return Result.fail("导出告警事件列表:" + throwable.getMessage());
            }

            @Override
            public Result<List<Map>> getRecordByOrderId(AlarmOrderQueryDTO dto) {
                return Result.fail("通过告警工单ID获取处理环节信息:" + throwable.getMessage());
            }

            @Override
            public Result<List<AlarmLableRelDTO>> getAllAlarmTags() {
                return Result.fail("获取全部告警标签:" + throwable.getMessage());
            }

            @Override
            public Result addTag(AddLableDTO addLableDTO) {
                return Result.fail("告警标签新增:" + throwable.getMessage());
            }

            @Override
            public Result modifyTag(ModifyLableDTO modifyTagDTO) {
                return Result.fail("告警标签修改:" + throwable.getMessage());
            }

            @Override
            public Result deleteTag(DeleteLableDTO deleteLableDTO) {
                return Result.fail("告警标签删除:" + throwable.getMessage());
            }

            @Override
            public Result saveTagRelation(@Validated @RequestBody AddLableRelationDTO addLableRelationDTO){
                return Result.fail("告警关联标签保存:" + throwable.getMessage());
            }

            @Override
            public Result tradeSaveTagRelation(TradAddLableDto tradAddLableDto) {
                return Result.fail("行业告警关联标签保存:" + throwable.getMessage());
            }

            @Override
            public Result<List<AlarmLableRelDTO>> getAlarmTagsByOrderId(@PathVariable(value="waringOrderId") String waringOrderId){
                return Result.fail("告警对应标签查询:" + throwable.getMessage());
            }

            @Override
            public Result<List<ProcessCommonWordsDTO>> getProcessCommonWords() {
                return Result.fail("事件处理常用语查询:" + throwable.getMessage());
            }

            @Override
            public Result updateProcessCommonWords(List<ProcessCommonWordsDTO> commonWordsDTOList) {
                return Result.fail("编辑保存事件处理常用语:" + throwable.getMessage());
            }

            @Override
            public Result addShare(AddShareDTO addShareDTO) {
                return Result.fail("告警分享新增:" + throwable.getMessage());
            }

            @Override
            public Result getGridInfoByLongitudeAndLatitude(GridInfoReq req) {
                return Result.fail("根据位置及租户id行业版本获取网格信息:" + throwable.getMessage());
            }

            @Override
            public Result accept(@RequestBody TradeAcceptOrderDto dto) {
                return Result.fail("告警事件受理:" + throwable.getMessage());
            }

            @Override
            public Result addAlarmOrder(@RequestBody ReceiveAlarmVO receiveAlarmVO) {
                return Result.fail("人工上报:" + throwable.getMessage());
            }

            @Override
            public Result addThirdAlarmOrder(List<ReceiveAlarmVO> receiveAlarmVO) {
                return Result.fail("第三方告警上报异常:" + throwable.getMessage());
            }

            @Override
            public Result searchOrderNumByLongitude4Industry(SearchOrderDTO dto) {
                return Result.fail("根据租户按照经纬度递归查询告警信息:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo searchOrderListByLongitude(BigScreenOrderListDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public Result<List<OrderCompleteVO>> getOrderNumByDeviceCode(AlarmOrderParmaDto dto) {
                return Result.fail("根据设备编码集合查询设备是否有未完结告警:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getAlarmListForBigScreen(BigScreenOrderListDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderListByDeviceChannelAndPresetId(AlarmOrderParmaDto dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public Result searchOrderNumByLongitude(SearchOrderDTO dto) {
                return Result.fail("根据租户按照经纬度查询告警信息:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getAlarmListCount(BigScreenOrderListDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderLongitudeList(BigScreenOrderListDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                return rspData;
            }

            @Override
            public Result<List<CheckAlarmByDeviceVO>> getCheckAlarmByDevice(CheckAlarmByDeviceVO dto){
                return Result.fail("根据租户按照经纬度查询告警信息:" + throwable.getMessage());
            }
            @Override
            public Result getCheckpointAlarmInfo(CheckpointAlarmDTO dto){
                return Result.fail("查询车辆信息异常:" + throwable.getMessage());
            }

            /**
             * 针对告警来源为“AI告警”
             * 在“事件描述”字段中展示相应算法配置的参数
             * （即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
             *
             * @param req 传入参数 格式
             *            [{
             *            "warningOrderId": "", //工单编号
             *            "deviceCode": "",   //摄像机编号
             *            "channelCode": "",  //通道编号
             *            "taskId": "",       //任务ID
             *            "algorithmCode": "", //算法编码
             *            "warningSource" : "" //告警类型
             *            }]
             * @retrun 返回map key->warningOrderId
             * value->拼装好的描述
             */
            @Override
            public Result<Map<String, String>> buildAlgorithmConfigDescFieldMap(AiAlgorithmConfigReq req) {
                return Result.fail("获取算法配置参数失败:" + throwable.getMessage());
            }

            @Override
            public List<Map<String, String>> getOrgInfoList(List<OrganInfoReq> reqList) {
                Result.fail("获取所属组织失败:" + throwable.getMessage());
                return new ArrayList<>();
            }

            @Override
            public Result addNewTagRelation(@Validated @RequestBody AddLableRelationDTO addLableRelationDTO){
                return Result.fail("新增告警关联标签:" + throwable.getMessage());
            }

            @Override
            public List<String> findAlarmTypesByTime(TradeAlarmOrderDTO dto) {
                Result.fail("获取该租户行业下产生过告警的告警类型编码:" + throwable.getMessage());
                return new ArrayList<>();
            }

            @Override
            public TableDataInfo getCheckpointAlarmListForCustomer(CheckpointAlarmDTO vo) {
                return null;
            }

            @Override
            public Result<DevicePointTurn> turnLatitudeByPoint(DevicePointTurn devicePointTurn) {
                return Result.fail("查询百分比点位对应的经纬度异常:" + throwable.getMessage());
            }

            @Override
            public Result<AlarmAreaResp> getAreaList(AlarmAreaReq req) {
                return Result.fail("查询告警区域点位失败:" + throwable.getMessage());
            }

            @Override
            public Result updateAreaPosition(List<AlarmAreaUpdateReq> req) {
                return Result.fail("修改告警区域位置信息失败:" + throwable.getMessage());
            }

            @Override
            public Result  publicSynAnalysisTask(List<SecondRecogSendVO> list){
                return Result.fail("公共二次识别下发失败:" + throwable.getMessage());
            }

            @Override
            public Result uavSynAnalysisTask(List<SecondRecogSendVO> list) {
                return Result.fail("无人机二次识别下发失败:" + throwable.getMessage());
            }

            @Override
            public Result getWarningOrderLayerNoFile(WarningOrderLayerDTO dto) {
                return Result.fail("查询告警图层失败:" + throwable.getMessage());
            }

        };
    }
}
