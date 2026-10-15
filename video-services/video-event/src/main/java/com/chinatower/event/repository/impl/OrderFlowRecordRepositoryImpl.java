package com.chinatower.event.repository.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.core.dto.ProcessApproveDTO;
import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.SelectLastProcess.SelectLastRrocessReq;
import com.chinatower.event.dto.current.CheckCurrentReq;
import com.chinatower.event.dto.current.CheckCurrentResp;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.mapper.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.model.ApiAlarmOrderRecordModel;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dataobject.OrderFlowRecordDO;
import com.chinatower.event.entity.vo.FlowRecordVO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.IOrderFlowRecordRepository;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.event.vo.OrderFlowRecordVO;

import javax.annotation.Resource;

/**
 * <p>
 * 告警事件工单流程表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
@Slf4j
@Service
public class OrderFlowRecordRepositoryImpl extends ServiceImpl<OrderFlowRecordMapper, OrderFlowRecordDO> implements IOrderFlowRecordRepository {

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;

    @Autowired
    private ProcessOrderMapper processOrderMapper;

    @Autowired
    private OrderFileMapper orderFileMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private FlowLinkAttrMapper flowLinkAttrMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private SysMapper sysMapper;

    @Override
    public List<FlowLinkAttrModel> queryRollbackLink(ApiAlarmOrderRecordModel vo) {
        log.info("获取可回退环节接口入参：{}", JSON.toJSONString(vo));
        List<FlowLinkAttrModel> flowLinkAttrModels = new ArrayList<>();

        //根据工单id生成按月分表表名
        String orderId = vo.getOrderId();
        String processTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + orderId.substring(0, 6);
        String warnTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);

        //若流程配置id为空使用默认模板
        List<String> curLinks = new ArrayList<>();
        //查询当前环节是否开启回退
        curLinks = orderFlowRecordMapper.checkRollbackLink(processTableName, warnTableName, orderId);
        //没有则查初始化配置流程
        if(CollectionUtil.isEmpty(curLinks)){
            curLinks = orderFlowRecordMapper.checkRollbackLinkByIndustry(processTableName, warnTableName, orderId);
        }
        //未开启回退返回空列表数据
        if (CollectionUtil.isEmpty(curLinks)) {
            return flowLinkAttrModels;
        }

        vo.setTableName(processTableName);
//        //查询工单对应的流程环节
//        List<FlowLinkAttrModel> linkAttrList = flowLinkAttrMapper.queryOrderFlowLink(warnTableName, orderId);
//        //查询不到则查初始化配置流程环节
//        if(CollectionUtils.isEmpty(linkAttrList)){
//            linkAttrList = flowLinkAttrMapper.queryOrderIndustryFlowLink(warnTableName, orderId);
//        }
        AlarmOrderDTO dto = new AlarmOrderDTO();
        dto.setOrderId(orderId);
        dto.setTableName(warnTableName);
        OrderInfoVO orderInfo = warningOrderMapper.getOrderInfo(dto);
        AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
        configModel.setTemplateId(Long.valueOf(orderInfo.getFlowTemplate()));
        configModel.setId(Long.valueOf(orderInfo.getFlowId()));
        configModel.setWarningSource(orderInfo.getWarningSource());
        List<FlowLinkAttrModel> linkAttrList = getFlowLinkAttr(configModel);
        if (CollectionUtils.isNotEmpty(linkAttrList)) {
            //构造流程前后环节关联
            HashMap<String, FlowLinkAttrModel> flowMap = new HashMap<>();
            for (FlowLinkAttrModel flowLinkAttrModel : linkAttrList) {
                flowMap.put(flowLinkAttrModel.getCurLink().toString(), flowLinkAttrModel);
            }

            FlowLinkAttrModel flowLinkAttrModel = flowMap.get(curLinks.get(0));
            //遍历当前环节之前的环节信息
            String preLink = flowLinkAttrModel.getPreLink();
            while (StringUtils.isNotEmpty(preLink)) {
                FlowLinkAttrModel preFlowLinkAttrModel = flowMap.get(preLink);
//
//                FlowLinkConfig flowLinkConfig = new FlowLinkConfig();
//                flowLinkConfig.setLinkId(Long.valueOf(preFlowLinkAttrModel.getCurLink()));
//                flowLinkConfig.setLinkName(preFlowLinkAttrModel.getLinkName());
                //上个环节为空，或者上个环节数据错误（上个环节的上个环节与上个环节的当前环节一致）
                if(preFlowLinkAttrModel != null && !String.valueOf(preFlowLinkAttrModel.getCurLink()).equals(preFlowLinkAttrModel.getPreLink())){
                    flowLinkAttrModels.add(preFlowLinkAttrModel);
                    preLink = preFlowLinkAttrModel.getPreLink();
                }else{
                    preLink =null;
                }

            }

        }
        //过滤开始环节
        flowLinkAttrModels = flowLinkAttrModels.stream().filter(f ->
                7!= (f.getCurLink())).collect(Collectors.toList());
        return flowLinkAttrModels;
    }


    @Override
    public Result queryLastProcess(SelectLastRrocessReq dto) {
        String tableName = TableUtil.getTableName(DateUtils.stringToDateYM(dto.getOrderId().substring(0, 6)), TableConstant.WARN_ORDER_PROCESS_TEBLE);
        ProcessDTO processDTO =orderFlowRecordMapper.queryLastProcess(tableName, dto.getOrderId(),dto.getLinkId());
        if(processDTO != null){
            // 供app使用
            SysUser sysUser = sysMapper.selectUserOnlyByUserName(processDTO.getProcessName());
            if(ObjectUtil.isNotNull(sysUser)){
                processDTO.setProcessNickName(sysUser.getNickName());
            }
        }
        return Result.data(processDTO);
    }


    public void getNextLinkProcessNameList(OrderFlowRecordVO vo) {
        if (!StringUtils.isEmpty(vo.getDispatchProcessName()) || !StringUtils.isEmpty(vo.getAcceptProcessId())) {
            List<String> thisUserIdList = new ArrayList<>();
            if(!StringUtils.isEmpty(vo.getDispatchProcessName())){
                thisUserIdList = new ArrayList<>(Arrays.asList(vo.getDispatchProcessName().split(",")));
            }
            if(!StringUtils.isEmpty(vo.getAcceptProcessId())){
                thisUserIdList.add(vo.getAcceptProcessId());
            }
            if(CollectionUtils.isEmpty(thisUserIdList)){
                return;
            }
            List<UserInfoDto> userList = sysMapper.queryUserListInfo(thisUserIdList);
            for (UserInfoDto user : userList) {
                if (StringUtils.isEmpty(user.getAvatar())) {
                    user.setAvatar("");
                }
            }
            //返回该环节所有选择处理人信息
            vo.setUserInfoList(userList);
            String nextLinkProcessName = "";
            for (UserInfoDto user : userList) {
                if (StringUtils.isEmpty(nextLinkProcessName)) {
                    nextLinkProcessName = user.getNickName();
                } else {
                    nextLinkProcessName += "," + user.getNickName();
                }
                if(!StringUtils.isEmpty(vo.getAcceptProcessId()) && vo.getAcceptProcessId().equals(user.getUserId())){
                    vo.setAcceptProcessName(user.getNickName());
                    vo.setAcceptProcessPhone(user.getPhonenumber());
                }
            }
            vo.setNextLinkProcessName(nextLinkProcessName);
        }
    }

    @Resource
    ProcessApproveMapper processApproveMapper;

    /***
     * 通过主键获取处理环节信息
     * @param dto
     * @return
     */
    @Override
    public Result<OrderFlowRecordVO> getRecordById(OrderFlowRecordDTO dto) {
        if (StringUtils.isEmpty(dto.getOrderId()) || StringUtils.isEmpty(dto.getOrderFlowId())) {
            Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }
        String tableName = TableUtil.getTableName(DateUtils.stringToDateYM(dto.getOrderId().substring(0, 6)), TableConstant.WARN_ORDER_PROCESS_TEBLE);
        dto.setTableName(tableName);
        OrderFlowRecordVO orderFlowRecordVO = orderFlowRecordMapper.getPendingCheckById(dto);
        if(orderFlowRecordVO !=null){
            ProcessApproveDTO processApproveDTO = new ProcessApproveDTO();
            processApproveDTO.setApproveId(orderFlowRecordVO.getApproveOrderId());
            ProcessApproveDTO orderApprove = processApproveMapper.getOrderApprove(processApproveDTO);
            if (orderApprove != null) {
                orderFlowRecordVO.setStatus(orderApprove.getApproveStatus());
                orderFlowRecordVO.setProcessOrderId(orderApprove.getProcessOrderId());
            }
            this.getNextLinkProcessNameList(orderFlowRecordVO);
            if(!StringUtils.isEmpty(orderFlowRecordVO.getProcessId())){
                SysUser sysUser =sysMapper.selectUserOnlyByUserId(orderFlowRecordVO.getProcessId());
                if(sysUser != null){
                    orderFlowRecordVO.setProcessName(sysUser.getNickName());
                }
            }
            dto.setProcessOrderId(orderFlowRecordVO.getProcessOrderId());
        }else{
            orderFlowRecordVO =new OrderFlowRecordVO();
        }
        List<String> userIdList = new ArrayList<>();
        List<FlowRecordVO> handles = warningOrderMapper.getPendingHandle(dto);
        if (!CollectionUtils.isEmpty(handles)) {
            FlowRecordVO flowRecordVO = handles.get(0);
            if (null != flowRecordVO) {
                userIdList.add(flowRecordVO.getProcessId());
                List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
                if (CollectionUtils.isNotEmpty(sysUserVOList)) {
                    orderFlowRecordVO.setHandleProcessName(sysUserVOList.get(0).getNickName());
                }
                orderFlowRecordVO.setHandleTime(flowRecordVO.getFlowEndTime());
                orderFlowRecordVO.setHandleRemark(flowRecordVO.getRemark());
            }
        }

        // 拼装调度信息
//        param.put("linkId", EnumUtil.FlowNode.PENDING_DISPATCH.getCode());
//        param.put("nodeTime", orderFlowRecordVO.getFlowNodeTime());
        //dto.setLinkId(EnumUtil.FlowNode.PENDING_DISPATCH.getCode());
        dto.setRealLinkType(EnumUtil.FlowNode.PENDING_DISPATCH.getCode());
        dto.setNodeTime(orderFlowRecordVO.getFlowNodeTime());
        OrderFlowRecordVO dispatchMap = orderFlowRecordMapper.getRecord(dto);
        if (null != dispatchMap) {
            userIdList.add(dispatchMap.getProcessId());
            List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
            if (CollectionUtils.isNotEmpty(sysUserVOList)) {
                orderFlowRecordVO.setDispatchProcessName(sysUserVOList.get(0).getNickName());
            }
            orderFlowRecordVO.setDispatchProcessName(dispatchMap.getProcessName());
            orderFlowRecordVO.setDispatchTime(dispatchMap.getFlowEndTime());
            orderFlowRecordVO.setDispatchRemark(dispatchMap.getRemark());
        }
        // 核实人电话
        if (!StringUtils.isEmpty(orderFlowRecordVO.getProcessId())) {
            userIdList.add(orderFlowRecordVO.getProcessId());
            List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
            if (CollectionUtils.isNotEmpty(sysUserVOList)) {
                orderFlowRecordVO.setMobilePhone(sysUserVOList.get(0).getPhonenumber());
            }
        }
        //查询附件
        OrderFileDO orderFileDO = new OrderFileDO();
        orderFileDO.setWarningOrderId(dto.getOrderId());
        orderFileDO.setOrderFlowId(dto.getOrderFlowId());
        QueryWrapper<OrderFileDO> wrapper = new QueryWrapper();
        wrapper.eq(Constants.RES_WARNING_ORDER_ID, dto.getOrderId());
        wrapper.eq(Constants.PARAM_ORDER_FLOW_ID, dto.getOrderFlowId());
        orderFlowRecordVO.setFiles(orderFileMapper.selectList(wrapper));
        return Result.data(orderFlowRecordVO);
    }

    @Override
    public Result<CheckCurrentResp> getCurrentByOrderIdAndLinkId(CheckCurrentReq req) {
        log.info("OrderFlowRecordRepositoryImpl-getCurrentByOrderIdAndLinkId-req:{}", JSON.toJSONString(req));
        CheckCurrentResp resp = new CheckCurrentResp();
        String tableName = TableUtil.getTableName(DateUtils.stringToDateYM(req.getOrderId().substring(0, 6)), TableConstant.WARN_ORDER_PROCESS_TEBLE);
        req.setTableName(tableName);
        String ifCurrentLink =orderFlowRecordMapper.getCurrentByOrderIdAndLinkId(req);
        resp.setIfCurrentLink(StringUtils.isEmpty(ifCurrentLink)?Constants.ZERO:ifCurrentLink);
        log.info("OrderFlowRecordRepositoryImpl-getCurrentByOrderIdAndLinkId-resp:{}", JSON.toJSONString(req));
        return Result.data(resp);
    }

    /**
     * 获取流程环节信息
     * @param configModel
     * @return
     */
    private List<FlowLinkAttrModel> getFlowLinkAttr(AlarmFlowConfigModel configModel){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        configModel.setAppVerCode(currentUser.getAppVerCode());
        configModel.setIndustryCode(currentUser.getIndustryCode());
        List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
        return BeanUtil.copyToList(flowModelList,FlowLinkAttrModel.class);
    }
}
