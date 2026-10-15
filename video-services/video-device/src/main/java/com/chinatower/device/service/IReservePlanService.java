package com.chinatower.device.service;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.device.dto.*;

import java.util.List;


/**
 * @ClassName OrganDeviceRelService
 * @Deacription 视频预案相关
 * @Author chenmf
 * @Date 2022/2/19 15:27
 * @Version 1.0
 **/
public interface IReservePlanService {
    
    /**
     * 增加预案信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    void addReservePlan(AddReservePlanDTO reservePlanDTO);
    
    /**
     * 删除预案信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    void delReservePlan(String planId);
    
    /**
     * 修改预案信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    void updateReservePlan(ModifyReservePlanDTO reservePlanDTO);
    
    /**
     * 查询预案列表信息
     *
     * @return
     * @author chenmf
     * @date 2022/4/18
     * @version
     */
    ReservePlanVO queryReservePlan(ReservePlanDTO param);

    /**
     * 分页查询预案列表信息
     * @Author chenmf
     * @param
     * @return
     */
    List<ReservePlanDTO> queryReservePlanList(ReservePlanDTO reservePlanDTO);

    List<ReservePlanChannelDTO> queryReservePlanDetail(ReservePlanDTO reservePlanDTO);
}
