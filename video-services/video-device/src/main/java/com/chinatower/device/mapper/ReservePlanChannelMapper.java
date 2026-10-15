package com.chinatower.device.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.device.dataobject.ReservePlanChannelDO;
import com.chinatower.device.dto.ReservePlanChannelDTO;

/**
 * <p>
 * 视频预案设备通道关系信息表 Mapper 接口
 * </p>
 *
 * @author chenmf
 * @since 2022-04-19
 */
public interface ReservePlanChannelMapper extends BaseMapper<ReservePlanChannelDO> {
    
    /**
     * 根据planId查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/19
     * @version
     */
    List<ReservePlanChannelDTO> queryReservePlanChannel(@Param("planId") String planId, @Param("param") BaseDataScopeEntity param);

    List<ReservePlanChannelDTO> selectByPlanIds(@Param("planIds") List<String> planIds, @Param("param") BaseDataScopeEntity param);
}
