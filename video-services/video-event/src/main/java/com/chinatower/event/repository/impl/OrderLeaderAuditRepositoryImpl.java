package com.chinatower.event.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.MessagePushDTO;
import com.chinatower.event.dto.OrderLeaderAuditDTO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.mapper.OrderFlowRecordMapper;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.repository.IReceiveRepository;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dataobject.OrderLeaderAuditDO;
import com.chinatower.event.mapper.OrderLeaderAuditMapper;
import com.chinatower.event.repository.IOrderLeaderAuditRepository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
@Service
@Slf4j
public class OrderLeaderAuditRepositoryImpl extends ServiceImpl<OrderLeaderAuditMapper, OrderLeaderAuditDO> implements IOrderLeaderAuditRepository {

    @Autowired
    private OrderLeaderAuditMapper orderLeaderAuditMapper;

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IReceiveRepository receiveRepository;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private ISysService sysService;
    /**
     * 领导批示
     *
     * @param dto
     * @return
     */
    @Override
    public int updateLeaderAudit(OrderLeaderAuditDO dto) {

        // 获取当前的用户
        dto.setLeaderId(sysService.getLoginUserId());
        return orderLeaderAuditMapper.updateLeaderAudit(dto);
    }

    /**
     * 转领导批示
     *
     * @param dto
     * @return
     */
    @Override
    public Result addLeaderAudit(OrderLeaderAuditDTO dto) {
        if (dto == null || StringUtils.isEmpty(dto.getOrderId())) {
            return Result.fail("参数错误");
        }
        if (StringUtils.isEmpty(dto.getLeaderId()) && CollectionUtils.isEmpty(dto.getLeaderIdList())) {
            return Result.fail("参数错误");
        }

        // 获取表名
        String tableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + dto.getOrderId().substring(0, 6);
        Map param = new HashMap();
        param.put(Constants.PARAM_TABLE_NAME, tableName);
        param.put(Constants.PARAM_ORDER_ID, dto.getOrderId());
        dto.setOrderFlowId(orderFlowRecordMapper.getIdByOrderId(param));

        // 获取表名
        tableName = TableConstant.WARN_ORDER_TEBLE + dto.getOrderId().substring(0, 6);

        // 查询告警详情
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        alarmOrderDTO.setTableName(tableName);
        alarmOrderDTO.setOrderId(dto.getOrderId());

        OrderInfoVO orderInfoVO = warningOrderMapper.getOrderInfo(alarmOrderDTO);
        if (null == orderInfoVO) {
            return Result.fail("工单信息不存在");
        }
        BeanUtils.copyProperties(orderInfoVO, dto);
        if(!StringUtils.isEmpty(orderInfoVO.getTimeLimit())){
            dto.setTimeLimit(Integer.valueOf(orderInfoVO.getTimeLimit()));
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (SecurityUtils.isSwitch()) {
            loginUser = loginUser.getSwitchdLoginUser();
        }
        dto.setUserId(loginUser.getUser().getUserId());
        dto.setEnableAcceptanceMode(orderInfoVO.getEnableAcceptanceMode());
        dto.setOrderId(orderInfoVO.getWarningOrderId());
        dto.setIndustryCode(orderInfoVO.getTradeId());
        dto.setFindName(orderInfoVO.getFindName());
        List<OrderLeaderAuditDTO> orderLeaderAuditDTOList = new ArrayList<>();
        List<String> leaderIdList = dto.getLeaderIdList();
        if (!CollectionUtils.isEmpty(leaderIdList)) {
            for (String leaderId : leaderIdList) {
                dto.setLeaderId(leaderId);
                dto.setAuditId(IdWorker.getIdStr());
                orderLeaderAuditDTOList.add(dto);
            }
        }
        else {
            dto.setAuditId(IdWorker.getIdStr());
            orderLeaderAuditDTOList.add(dto);
        }
        //插入领导批示表
        orderLeaderAuditMapper.addLeaderAuditList(orderLeaderAuditDTOList);
        //消息推送
        orderLeaderAuditDTOList.forEach(leaderAuditDTO -> {
            this.sendMsg(leaderAuditDTO, orderInfoVO);
        });
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 消息推送
     * @param dto
     * @param vo
     */
    private void sendMsg(OrderLeaderAuditDTO dto, OrderInfoVO vo) {
        MessagePushDTO messagePushDTO = new MessagePushDTO();
        messagePushDTO.setOrderId(dto.getOrderId());
        messagePushDTO.setMsgId(dto.getAuditId());
        messagePushDTO.setTenantId(vo.getTenantId());
        messagePushDTO.setAlarmFlowId(vo.getFlowId());
        messagePushDTO.setLinkId(vo.getLinkId());
        messagePushDTO.setCreator("");
        messagePushDTO.setNotifyTitle(vo.getAlarmBody());
        messagePushDTO.setTypeValue(vo.getWarningTypeId());
        messagePushDTO.setAddress(vo.getAddress());
        messagePushDTO.setUserType(Constants.USER_TYPE_1);
        messagePushDTO.setMsgNotifyType(dto.getMsgNotifyType());
        String processId = dto.getLeaderId();
        if (!StringUtils.isEmpty(processId)) {
            if (processId.startsWith(Constants.COMMA)) {
                processId = processId.substring(1, processId.length());
            }
            if (processId.endsWith(Constants.COMMA)) {
                processId = processId.substring(0, processId.length() - 1);
            }
        }
        messagePushDTO.setUserId(processId);
        String roleId = dto.getRoleId();
        if (!StringUtils.isEmpty(roleId)) {
            if (roleId.startsWith(Constants.COMMA)) {
                roleId = roleId.substring(1, roleId.length());
            }
            if (roleId.endsWith(Constants.COMMA)) {
                roleId = roleId.substring(0, roleId.length() - 1);
            }
        }
        messagePushDTO.setRoleId(roleId);
        messagePushDTO.setNotifyType(Constants.SYS_MSG_TYPE_3);
        messagePushDTO.setOrderStatus(vo.getOrderStatus());
        messagePushDTO.setFlowTemplate(vo.getFlowTemplate());
        messagePushDTO.setAlarmTime(vo.getAlarmTime());
        messagePushDTO.setAppVerCode(vo.getAppVerCode());
        messagePushDTO.setIndustryCode(vo.getTradeId());
        messagePushDTO.setLeaderFlag(Constants.YES_FLAG);
        messagePushDTO.setRealLinkType(vo.getRealLinkType());
        messagePushDTO.setFlowFlag(Constants.ONE);
        receiveRepository.messagePush(messagePushDTO);
    }
}
