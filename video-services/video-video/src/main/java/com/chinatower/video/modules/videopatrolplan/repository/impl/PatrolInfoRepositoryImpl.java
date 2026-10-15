package com.chinatower.video.modules.videopatrolplan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolInfoDO;
import com.chinatower.video.modules.videopatrolplan.mapper.PatrolInfoMapper;
import com.chinatower.video.modules.videopatrolplan.repository.IPatrolInfoRepository;
import com.chinatower.video.modules.videopatrolplan.repository.IPatrolPresetRelaRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 巡航路径信息表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Service
public class PatrolInfoRepositoryImpl extends ServiceImpl<PatrolInfoMapper, PatrolInfoDO> implements IPatrolInfoRepository {
    @Autowired
    IPatrolPresetRelaRepository patrolPresetRelaRepository;

    @Autowired
    PatrolInfoMapper patrolInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePatrols(List<Long> patrolIds) {
        if (patrolIds == null || patrolIds.isEmpty()) {
            return false;
        }
        this.removeByIds(patrolIds);
        patrolPresetRelaRepository.removeByPatrols(patrolIds);
        return true;
    }

    @Override
    public boolean updatePatrolStartTime(PatrolInfoDO patrolInfo) {
        if (patrolInfo.getPatrolId() == null) {
            return false;
        }
        patrolInfo.setStartStopDate(new Date());
        if (StringUtils.isNotBlank(patrolInfo.getStartStop())) {
            patrolInfo.setStartStop(patrolInfo.getStartStop());
        }
        if (StringUtils.isNotBlank(patrolInfo.getIfCruise())) {
            patrolInfo.setIfCruise(patrolInfo.getIfCruise());
        }
        patrolInfo.setOperationTime(new Date());
        try {
            patrolInfo.setOperator(SecurityUtils.getOperator());
        }catch (Exception e){
            log.error("获取操作人失败", e);
        }
        return this.updateById(patrolInfo);
    }

    @Override
    public boolean updatePatrolStartTimeBatch(List<PatrolInfoDO> list) {
        for (PatrolInfoDO patrolInfo : list) {
            if (patrolInfo.getPatrolId() == null) {
                return false;
            }
            patrolInfo.setStartStopDate(new Date());
            if (StringUtils.isNotBlank(patrolInfo.getStartStop())) {
                patrolInfo.setStartStop(patrolInfo.getStartStop());
            }
            if (StringUtils.isNotBlank(patrolInfo.getIfCruise())) {
                patrolInfo.setIfCruise(patrolInfo.getIfCruise());
            }
            patrolInfo.setOperationTime(new Date());
            try {
                patrolInfo.setOperator(SecurityUtils.getOperator());
            }catch (Exception e){
                log.error("获取操作人失败", e);
            }
        }
        return patrolInfoMapper.updatePatrolStartTimeBatch(list);
    }

    /**
     * 查询订单巡航路径
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @Override
    public List<PatrolInfoDO> queryOrderPatrolInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {
        QueryWrapper<PatrolInfoDO> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("order_code", queryOrderDeviceSetDTO.getOrderCode());
        queryWrapper.eq("tenant_id", queryOrderDeviceSetDTO.getTenantId());
        queryWrapper.eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL_INT);
        if (!StringUtils.isEmpty(queryOrderDeviceSetDTO.getIndustryCode())) {
            queryWrapper.eq("industry_code", queryOrderDeviceSetDTO.getIndustryCode());
        }
        List<String> deviceCodeList = queryOrderDeviceSetDTO.getDeviceCodeList();
        if (!CollectionUtils.isEmpty(deviceCodeList)) {
            queryWrapper.in("device_code", deviceCodeList);
        }

        return baseMapper.selectList(queryWrapper);
    }
}
