package com.chinatower.device.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.device.dataobject.ReservePlanDO;
import com.chinatower.device.dto.ReservePlanDTO;
import com.chinatower.device.mapper.ReservePlanMapper;
import com.chinatower.device.repository.IReservePlanRepository;
import com.chinatower.platform.constants.DeviceConstants;

/**
 * @ClassName ReservePlanRepositoryImpl
 * @Deacription 视频预案
 * @Author chenmf
 * @Date 2022/4/18 15:33
 * @Version 1.0
 **/

@Service
public class ReservePlanRepositoryImpl extends ServiceImpl<ReservePlanMapper, ReservePlanDO> implements IReservePlanRepository {
    
    @Autowired
    private ReservePlanMapper reservePlanMapper;
    
    /**
     * 查询视频预案是否同名
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    @Override
    public int queryReservePlanName(String tenantId, String planId, String planName) {
    
        QueryWrapper<ReservePlanDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        if (StringUtils.isNotEmpty(planId)) {
            queryWrapper.ne("plan_id", planId);
        }
        queryWrapper.eq("plan_name", planName);
        queryWrapper.eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL);
    
        return reservePlanMapper.selectCount(queryWrapper);
    }
    
    /**
     * 查询视频预案
     *
     * @return
     * @author chenmf
     * @date 2022/4/19
     * @version
     */
    @Override
    public List<ReservePlanDTO> queryReservePlan(String tenantId, String userId, String industryCode,ReservePlanDTO reservePlanDTO) {
        
        return reservePlanMapper.queryReservePlan(tenantId, userId, industryCode,reservePlanDTO);
    }
    
    public String selectUserId(String userName)
    {
    	return reservePlanMapper.selectUserId(userName);
    }
}
