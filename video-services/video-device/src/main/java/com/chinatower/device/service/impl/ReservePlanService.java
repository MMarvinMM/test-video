package com.chinatower.device.service.impl;

import java.util.*;

import com.chinatower.common.core.constant.Constants;
import com.chinatower.device.mapper.ReservePlanMapper;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.ReservePlanChannelDO;
import com.chinatower.device.dataobject.ReservePlanDO;
import com.chinatower.device.dto.AddReservePlanDTO;
import com.chinatower.device.dto.ModifyReservePlanDTO;
import com.chinatower.device.dto.ReservePlanChannelDTO;
import com.chinatower.device.dto.ReservePlanDTO;
import com.chinatower.device.dto.ReservePlanVO;
import com.chinatower.device.repository.IReservePlanChannelRepository;
import com.chinatower.device.repository.IReservePlanRepository;
import com.chinatower.device.service.IReservePlanService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @ClassName ReservePlanService
 * @Deacription 视频预案相关
 * @Author chenmf
 * @Date 2022/4/18 15:09
 * @Version 1.0
 **/
@Service
@Slf4j
public class ReservePlanService implements IReservePlanService {
    
    @Autowired
    private IReservePlanRepository reservePlanRepository;

    @Autowired
    private IReservePlanChannelRepository reservePlanChannelRepository;

    @Resource
    private ReservePlanMapper reservePlanMapper;


    
    /**
     * 增加预案信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReservePlan(AddReservePlanDTO reservePlanDTO) {
    
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (loginUser == null) {
            throw new CustomException(DeviceNorthIntfConstants.GET_USER_INFO_ERROR);
        }
    
        //铁塔账号不可操作！
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            throw new CustomException(DeviceNorthIntfConstants.TOWER_CANNOT_OPERATE);
        }

        String userId = reservePlanRepository.selectUserId(loginUser.getUser().getUserName());
        //校验预案名称唯一，不可重复
        //修改成如果是私有预案，预案名称就是用户维度，每个用户下的名称唯一，不同用户可以有重复的名称。公有预案，整个客户视角下名称唯一。
        ReservePlanDTO qryReservePlanDTO = new ReservePlanDTO();
        qryReservePlanDTO.setPlanName(reservePlanDTO.getPlanName());
        if(Constants.TWO.equals(reservePlanDTO.getPlanType())){
            qryReservePlanDTO.setUserCode(userId);
        }
        qryReservePlanDTO.setTenantId(user.getTenantId());
        qryReservePlanDTO.setIndustryCode(user.getIndustryCode());
        int num = reservePlanMapper.queryReservePlanNameCount(qryReservePlanDTO);
        if (num > 0) {
            throw new CustomException(DeviceNorthIntfConstants.RESERVE_PLAN_NAME_UNIPUE);
        }

        String uuid = IdUtil.simpleUUID();
        ReservePlanDO reservePlanDO = new ReservePlanDO();
        BeanUtils.copyProperties(reservePlanDTO, reservePlanDO);
        reservePlanDO.setCreatorDate(new Date());
//        reservePlanDO.setModifierDate(new Date());
        reservePlanDO.setPlanId(uuid);
        reservePlanDO.setTenantId(user.getTenantId());
        reservePlanDO.setUserCode(userId);
//        reservePlanDO.setModifier(SecurityUtils.getOperator());
        reservePlanDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
        reservePlanDO.setIndustryCode(user.getIndustryCode());
        reservePlanDO.setAppVerCode(user.getAppVerCode());
        reservePlanRepository.save(reservePlanDO);
        
        List<ReservePlanChannelDTO> planChannelList = reservePlanDTO.getPlanChannelList();
        if (!CollectionUtils.isEmpty(planChannelList)) {
            List<ReservePlanChannelDO> planChannelDOList = new ArrayList<>();
            for (ReservePlanChannelDTO reservePlanChannelDTO : planChannelList) {
                ReservePlanChannelDO reservePlanChannelDO = new ReservePlanChannelDO();
                
                BeanUtils.copyProperties(reservePlanChannelDTO, reservePlanChannelDO);
                
                reservePlanChannelDO.setPlanId(uuid);
                reservePlanChannelDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                reservePlanChannelDO.setCreatorDate(new Date());
                reservePlanChannelDO.setModifierDate(new Date());
                planChannelDOList.add(reservePlanChannelDO);
            }
            reservePlanChannelRepository.saveBatch(planChannelDOList);
        }
    }
    
    /**
     * 删除预案信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delReservePlan(String planId) {
    
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(DeviceNorthIntfConstants.GET_USER_INFO_ERROR);
        }
    
        //铁塔账号不可操作！
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            throw new CustomException(DeviceNorthIntfConstants.TOWER_CANNOT_OPERATE);
        }
    
        ReservePlanDO reservePlanDO = reservePlanRepository.getById(planId);
        if (null == reservePlanDO) {
            throw new CustomException(DeviceNorthIntfConstants.RESERVE_PLAN_NOT_EXIST);
        }
    
        reservePlanRepository.removeById(planId);
        reservePlanChannelRepository.deleteByPlanId(planId);
    
    }
    
    /**
     * 修改预案信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReservePlan(ModifyReservePlanDTO reservePlanDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (loginUser == null) {
            throw new CustomException(DeviceNorthIntfConstants.GET_USER_INFO_ERROR);
        }
    
        //铁塔账号不可操作！
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            throw new CustomException(DeviceNorthIntfConstants.TOWER_CANNOT_OPERATE);
        }

        String userId = reservePlanRepository.selectUserId(loginUser.getUser().getUserName());
        //校验预案名称唯一，不可重复
        //修改如果是私有预案，预案名称就是用户维度，每个用户下的名称唯一，不同用户可以有重复的名称。公有预案，整个铁塔视角下名称唯一。
        ReservePlanDTO qryReservePlanDTO = new ReservePlanDTO();
        qryReservePlanDTO.setPlanName(reservePlanDTO.getPlanName());
        qryReservePlanDTO.setPlanId(reservePlanDTO.getPlanId());
        if(Constants.TWO.equals(reservePlanDTO.getPlanType())){
            qryReservePlanDTO.setUserCode(userId);
        }
        qryReservePlanDTO.setTenantId(user.getTenantId());
        qryReservePlanDTO.setIndustryCode(user.getIndustryCode());
        int num = reservePlanMapper.queryReservePlanNameCount(qryReservePlanDTO);
        if (num > 0) {
            throw new CustomException(DeviceNorthIntfConstants.RESERVE_PLAN_NAME_UNIPUE);
        }

    
        ReservePlanDO reservePlanDO = reservePlanRepository.getById(reservePlanDTO.getPlanId());
        if (null == reservePlanDO) {
            throw new CustomException(DeviceNorthIntfConstants.RESERVE_PLAN_NOT_EXIST);
        }
        
        BeanUtils.copyProperties(reservePlanDTO, reservePlanDO);
        reservePlanDO.setModifierDate(new Date());
        reservePlanDO.setModifier(SecurityUtils.getOperator());
        reservePlanRepository.updateById(reservePlanDO);
    
        //先删除数据
        reservePlanChannelRepository.deleteByPlanId(reservePlanDTO.getPlanId());
        //在新增
        List<ReservePlanChannelDTO> planChannelList = reservePlanDTO.getPlanChannelList();
        if (!CollectionUtils.isEmpty(planChannelList)) {
            List<ReservePlanChannelDO> planChannelDOList = new ArrayList<>();
            for (ReservePlanChannelDTO reservePlanChannelDTO : planChannelList) {
                ReservePlanChannelDO reservePlanChannelDO = new ReservePlanChannelDO();
            
                BeanUtils.copyProperties(reservePlanChannelDTO, reservePlanChannelDO);
            
                reservePlanChannelDO.setPlanId(reservePlanDTO.getPlanId());
                reservePlanChannelDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                reservePlanChannelDO.setCreatorDate(new Date());
                reservePlanChannelDO.setModifierDate(new Date());
                planChannelDOList.add(reservePlanChannelDO);
            }
            reservePlanChannelRepository.saveBatch(planChannelDOList);
        }
    }
    
    /**
     * 查询预案列表信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    @Override
    @DataScope(deviceColumn="a.device_code",tenantColumn="b.tenant_id",industryCode="b.industry_code",alarmSourceVal = "4",acceptColumn="c.accept_node")
    public ReservePlanVO queryReservePlan(ReservePlanDTO param) {
        ReservePlanVO reservePlanVO = new ReservePlanVO();
        List<ReservePlanDTO> publicPlanList = new ArrayList<>();
        List<ReservePlanDTO> privatePlanList = new ArrayList<>();

        Result result = getResult(param);
        // 查询所有
        List<ReservePlanChannelDTO> list =  reservePlanChannelRepository.selectByPlanIds(result.planIds, param);
        Map<String, List<ReservePlanChannelDTO>> planChannelsMap = getPlanChannelsMap(list);
        if (!CollectionUtils.isEmpty(result.planList)) {
            for (ReservePlanDTO planDTO : result.planList) {
                List<ReservePlanChannelDTO> planChannelList = planChannelsMap.getOrDefault(planDTO.getPlanId(), Collections.emptyList());
                planDTO.setPlanChannelList(planChannelList);
                if (IndustryConstants.PLAN_PUBLIC_TYPE.equals(planDTO.getPlanType())) {
                    publicPlanList.add(planDTO);
                } else {
                    privatePlanList.add(planDTO);
                }
            }
        }
        reservePlanVO.setPublicPlanList(publicPlanList);
        reservePlanVO.setPrivatePlanList(privatePlanList);
        return reservePlanVO;
    }

    private Result getResult(ReservePlanDTO reservePlanDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(DeviceNorthIntfConstants.GET_USER_INFO_ERROR);
        }
        //铁塔账号不可操作！
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            throw new CustomException(DeviceNorthIntfConstants.TOWER_CANNOT_OPERATE);
        }
        String userId = reservePlanRepository.selectUserId(loginUser.getUser().getUserName());
        if (reservePlanDTO.getPageNum() != null && reservePlanDTO.getPageSize() != null) {
            PageHelper.startPage(reservePlanDTO.getPageNum(), reservePlanDTO.getPageSize());
        }
        List<ReservePlanDTO> planList = reservePlanRepository.queryReservePlan(loginUser.getUser().getTenantId(),
        		userId, loginUser.getUser().getIndustryCode(), reservePlanDTO);
        // for 循环优化
        List<String> planIds = new ArrayList<>();
        for (ReservePlanDTO planDTO : planList) {
            planIds.add(planDTO.getPlanId());
        }
        return new Result(planList, planIds);
    }

    private static class Result {
        public final List<ReservePlanDTO> planList;
        public final List<String> planIds;

        public Result(List<ReservePlanDTO> planList, List<String> planIds) {
            this.planList = planList;
            this.planIds = planIds;
        }
    }

    @Override
    @DataScope(deviceColumn="a.device_code",tenantColumn="b.tenant_id",industryCode="b.industry_code",alarmSourceVal = "4",acceptColumn="c.accept_node")
    public List<ReservePlanDTO> queryReservePlanList(ReservePlanDTO reservePlanDTO) {
        Result result = getResult(reservePlanDTO);
        return result.planList;
    }

    @Override
    @DataScope(deviceColumn="a.device_code",tenantColumn="b.tenant_id",industryCode="b.industry_code",alarmSourceVal = "4",acceptColumn="c.accept_node")
    public List<ReservePlanChannelDTO> queryReservePlanDetail(ReservePlanDTO reservePlanDTO) {
        if (reservePlanDTO.getPageNum() != null && reservePlanDTO.getPageSize() != null) {
            PageHelper.startPage(reservePlanDTO.getPageNum(), reservePlanDTO.getPageSize());
        }
        List<String> planIds = new ArrayList<>();
        planIds.add(reservePlanDTO.getPlanId());
        return reservePlanChannelRepository.selectByPlanIds(planIds, reservePlanDTO);
    }


    public Map<String, List<ReservePlanChannelDTO>> getPlanChannelsMap (List<ReservePlanChannelDTO> list) {
        Map<String, List<ReservePlanChannelDTO>> planChannelsMap = new HashMap<>();
        for (ReservePlanChannelDTO channel : list) {
            planChannelsMap.computeIfAbsent(channel.getPlanId(), k -> new ArrayList<>()).add(channel);
        }
        return planChannelsMap;
    }
}
