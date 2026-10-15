package com.chinatower.video.modules.videopatrolplan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolPresetRelaDO;
import com.chinatower.video.modules.videopatrolplan.mapper.PatrolPresetRelaMapper;
import com.chinatower.video.modules.videopatrolplan.repository.IPatrolPresetRelaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 巡航路径预置点位关系表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-22
 */
@Service
public class PatrolPresetRelaRepositoryImpl extends ServiceImpl<PatrolPresetRelaMapper, PatrolPresetRelaDO> implements IPatrolPresetRelaRepository {

    @Override
    public boolean removeByPatrols(List<Long> patrolIds) {
        if (patrolIds == null || patrolIds.isEmpty()) {
            return false;
        }
        QueryWrapper<PatrolPresetRelaDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("patrol_id", patrolIds);
        this.baseMapper.delete(queryWrapper);
        return true;
    }
}
