package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.ReservePlanDO;
import com.chinatower.device.dto.ReservePlanDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 视频预案表 Mapper 接口
 * </p>
 *
 * @author chenmf
 * @since 2022-04-19
 */
public interface ReservePlanMapper extends BaseMapper<ReservePlanDO> {
    
    /**
     * 查询视频预案
     *
     * @return
     * @author chenmf
     * @date 2022/4/19
     * @version
     */
    List<ReservePlanDTO> queryReservePlan(@Param("tenantId") String tenantId, @Param("userId")String userId, @Param("industryCode")String industryCode, @Param("reservePlanDTO")ReservePlanDTO reservePlanDTO);
    
    String selectUserId(@Param("userName") String userName);

    int queryReservePlanNameCount(ReservePlanDTO reservePlanDTO);
}
