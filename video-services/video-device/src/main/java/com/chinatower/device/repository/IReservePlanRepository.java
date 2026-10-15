package com.chinatower.device.repository;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.dataobject.ReservePlanDO;
import com.chinatower.device.dto.ReservePlanDTO;

/**
 * 视频预案
 * 
 * @return 
 * @author chenmf
 * @date 2022/4/18
 * @version
 */
public interface IReservePlanRepository extends IService<ReservePlanDO> {
    
    /**
     * 查询视频预案是否同名
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    int queryReservePlanName(String tenantId, String planId, String planName);
    
    List<ReservePlanDTO> queryReservePlan(String tenantId, String userId, String industryCode,ReservePlanDTO reservePlanDTO);
    
    String selectUserId(String userName);
}
