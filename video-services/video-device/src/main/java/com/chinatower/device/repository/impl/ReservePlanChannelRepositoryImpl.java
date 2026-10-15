package com.chinatower.device.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.device.dataobject.ReservePlanChannelDO;
import com.chinatower.device.dto.ReservePlanChannelDTO;
import com.chinatower.device.mapper.ReservePlanChannelMapper;
import com.chinatower.device.repository.IReservePlanChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ReservePlanRepositoryImpl
 * @Deacription 视频预案
 * @Author chenmf
 * @Date 2022/4/18 15:33
 * @Version 1.0
 **/

@Service
public class ReservePlanChannelRepositoryImpl extends ServiceImpl<ReservePlanChannelMapper, ReservePlanChannelDO> implements IReservePlanChannelRepository {
    
    @Autowired
    private ReservePlanChannelMapper reservePlanChannelMapper;
    
    /**
     * 根据planId批量删除
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    @Override
    public void deleteByPlanId(String planId) {
        QueryWrapper<ReservePlanChannelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id", planId);
        reservePlanChannelMapper.delete(queryWrapper);
    }
    
    /**
     * 根据planId查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/19
     * @version
     */
    @Override
    public List<ReservePlanChannelDTO> queryReservePlanChannel(String planId, BaseDataScopeEntity param) {
        return reservePlanChannelMapper.queryReservePlanChannel(planId, param);
    }

    @Override
    public List<ReservePlanChannelDTO> selectByPlanIds(List<String> planIds, BaseDataScopeEntity param) {
        return reservePlanChannelMapper.selectByPlanIds(planIds, param);
    }

}
