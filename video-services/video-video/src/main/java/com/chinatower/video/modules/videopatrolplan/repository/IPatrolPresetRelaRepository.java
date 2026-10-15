package com.chinatower.video.modules.videopatrolplan.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolPresetRelaDO;

import java.util.List;

/**
 * <p>
 * 巡航路径预置点位关系表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-22
 */
public interface IPatrolPresetRelaRepository extends IService<PatrolPresetRelaDO> {

    boolean removeByPatrols(List<Long> patrolIds);
}
