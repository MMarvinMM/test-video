package com.chinatower.device.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.device.dataobject.ReservePlanChannelDO;
import com.chinatower.device.dto.ReservePlanChannelDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 视频预案
 * 
 * @return 
 * @author chenmf
 * @date 2022/4/18
 * @version
 */
public interface IReservePlanChannelRepository extends IService<ReservePlanChannelDO> {
    
    
    /**
     * 根据planId批量删除
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    void deleteByPlanId(String planId);
    
    /**
     * 根据planId查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/19
     * @version
     */
    List<ReservePlanChannelDTO> queryReservePlanChannel(String planId, BaseDataScopeEntity param);

    List<ReservePlanChannelDTO> selectByPlanIds(List<String> planIds, BaseDataScopeEntity param);
}
