package com.chinatower.video.modules.videopatrolplan.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolInfoDO;

import java.util.List;

/**
 * <p>
 * 巡航路径信息表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface IPatrolInfoRepository extends IService<PatrolInfoDO> {
    /**
     * 删除巡航计划及关联表
     * @param patrolIds
     * @return boolean
     * @author zheng.yan
     * @date 2022/1/20
     */
    boolean removePatrols(List<Long> patrolIds);

    boolean updatePatrolStartTime(PatrolInfoDO patrolInfo);

    boolean updatePatrolStartTimeBatch(List<PatrolInfoDO> list);
    
    /**
     * 查询订单巡航路径
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    List<PatrolInfoDO> queryOrderPatrolInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO);
}
