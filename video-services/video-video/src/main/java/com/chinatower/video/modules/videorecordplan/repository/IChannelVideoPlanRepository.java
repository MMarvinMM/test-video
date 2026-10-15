package com.chinatower.video.modules.videorecordplan.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videorecordplan.dataobject.ChannelVideoPlanDO;

import java.util.List;

/**
 * <p>
 * 通道录像计划表 服务类
 * </p>
 *
 * @author chenmf
 * @since 2022-01-19
 */
public interface IChannelVideoPlanRepository extends IService<ChannelVideoPlanDO> {
    
    /**
     * 查询订单通道录像计划
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    List<ChannelVideoPlanDO> queryOrderRecordPlanInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO);
    
    /**
     * 查询录像计划列表
     *
     * @return 
     * @author chenmf
     * @date 2022/4/14
     * @version
     */
    List<ChannelVideoPlanDO> queryVideoPlanInfo(ChannelVideoPlanVO tempChannelVideoPlanVO);
    
}
