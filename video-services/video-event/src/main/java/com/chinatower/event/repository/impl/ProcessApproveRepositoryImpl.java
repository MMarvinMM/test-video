package com.chinatower.event.repository.impl;

import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.WarnOrderStatusDTO;
import com.chinatower.event.util.DateUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dataobject.ProcessApproveDO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.mapper.OrderFlowRecordMapper;
import com.chinatower.event.mapper.ProcessApproveMapper;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.repository.IProcessApproveRepository;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.event.vo.AlarmOrderRecordVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 事件审批表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-15
 */
@Service
public class ProcessApproveRepositoryImpl extends ServiceImpl<ProcessApproveMapper, ProcessApproveDO> implements IProcessApproveRepository {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;
    /**
     * 告警事件核实
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitOrderApproveInfo(SendAlarmOrderDTO dto) {
        //兼容未更新版本的行业默认流程
        if(StringUtils.isEmpty(dto.getRealLinkType()) && Constants.REAL_LINK_TYPE_ALL.contains(dto.getLinkId())){
            dto.setRealLinkType(dto.getLinkId());
        }
        dto.setFlowLinkType(Constants.ZERO);
        if(StringUtils.isEmpty(dto.getRealLinkType())){
            return Result.fail(Constants.MESS_4);
        }
        // 校验工单表：校验工单表中当面工单是否为待核实状态
        String orderId = dto.getOrderId();
        String processTableName = TableUtil.getTableName(DateUtils.stringToDateYM(orderId.substring(0, 6)),
            TableConstant.WARN_ORDER_PROCESS_TEBLE);
        String warningTableName = TableUtil.getTableName(DateUtils.stringToDateYM(orderId.substring(0, 6)),
            TableConstant.WARN_ORDER_TEBLE);
        AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
        alarmOrderVO.setOrderId(orderId);
        alarmOrderVO.setOrderStatus(EnumUtil.FlowNode.PENDING_CHECK.getCode());// 待审批中
        alarmOrderVO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));;
        alarmOrderVO.setTableName(warningTableName);
        WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderVO);
        if (null == warnOrderStatusDTO) {
            return Result.fail("告警工单状态异常或不存在!");
        }
        if (StringUtils.isEmpty(dto.getTenantId())) {
            dto.setTenantId(warnOrderStatusDTO.getTenantId());
        }
        // 校验环节表:判断当前工单是否在研判环节
        AlarmOrderRecordVO alarmOrderRecordVO = new AlarmOrderRecordVO();
        alarmOrderRecordVO.setOrderId(orderId);
        alarmOrderRecordVO.setIfCurrentLink(Constants.YES_FLAG);
        alarmOrderRecordVO.setRealLinkType(EnumUtil.FlowNode.PENDING_CHECK.getCode());// 处理环节
        alarmOrderRecordVO.setTableName(processTableName);
        int num3 = orderFlowRecordMapper.getAlarmOrderRecordCount(alarmOrderRecordVO);
        if (num3 == 0) {
            return Result.fail("告警工单环节异常!");
        }
        if (!StringUtils.isEmpty(warnOrderStatusDTO.getFlowId())) {
            dto.setFlowId(warnOrderStatusDTO.getFlowId());
        }
        //当前环节ID
        dto.setCurrentLink(warnOrderStatusDTO.getLinkId());
        //下一环节ID
        dto.setOrderStatus(warnOrderStatusDTO.getLinkId());
        dto.setUserRecord(warnOrderStatusDTO.getUserRecord());
        dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
        dto.setWarningTypeId(warnOrderStatusDTO.getWarningTypeId());
        if (!ObjectUtils.isEmpty(warnOrderStatusDTO.getOrgRecord())) {
            dto.setOrgRecord(warnOrderStatusDTO.getOrgRecord());
        }
        return warningOrderRepository.updateAlarmOrder(dto);
    }

    /**
     * 会签完成流转环节处理
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result signSubmitOrderApproveInfo(SendAlarmOrderDTO dto) {
        String orderId = dto.getOrderId();
        String processTableName = TableUtil.getTableName(DateUtils.stringToDateYM(orderId.substring(0, 6)),
                TableConstant.WARN_ORDER_PROCESS_TEBLE);
        String warningTableName = TableUtil.getTableName(DateUtils.stringToDateYM(orderId.substring(0, 6)),
                TableConstant.WARN_ORDER_TEBLE);
        AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
        alarmOrderVO.setOrderId(orderId);
        alarmOrderVO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));;
        alarmOrderVO.setTableName(warningTableName);
        WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderVO);

        //判断告警事件是否会签完成
        boolean flag = warningOrderRepository.finishSign(orderId);
        if (!flag) {
            Result.fail("未会签完成，无法流转流程");
        }
        if (!StringUtils.isEmpty(warnOrderStatusDTO.getFlowId())) {
            dto.setFlowId(warnOrderStatusDTO.getFlowId());
        }
        //当前环节ID
        dto.setCurrentLink(warnOrderStatusDTO.getLinkId());
        //下一环节ID
        dto.setOrderStatus(dto.getLinkId());
        dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
        if (EnumUtil.FlowNode.END.getCode().equals(dto.getLinkId())) {
            dto.setLinkName(EnumUtil.FlowNode.END.getName());
        }
        dto.setUserRecord(warnOrderStatusDTO.getUserRecord());
        if (!ObjectUtils.isEmpty(warnOrderStatusDTO.getOrgRecord())) {
            dto.setOrgRecord(warnOrderStatusDTO.getOrgRecord());
        }

        return warningOrderRepository.updateAlarmOrder(dto);
    }
}
