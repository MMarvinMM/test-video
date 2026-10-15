package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.mapper.AlarmSuppressConfigMapper;
import com.chinatower.event.repository.IAlarmSuppressConfigRepository;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class AlarmSuppressConfigRepositoryImpl implements IAlarmSuppressConfigRepository {

    @Resource
    private AlarmSuppressConfigMapper configMapper;

    /**
     * 新增/修改告警压制规则
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveOrUpdateConfig(AlarmSuppressConfigDTO dto) {
        Date suppressBeginTime = new Date();
        Date suppressEndTime = DateUtils.dateAdd(dto.getSuppressIntervalType(), dto.getSuppressContinuousTime(), suppressBeginTime);
        if (null == suppressEndTime) {
            return Result.fail("告警压制时长单位不合法");
        }
        dto.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        //查询同一个租户同一个设备是否存在压制规则，存在就修改，不存在则新增
        AlarmSuppressConfigDTO configDTO = configMapper.querySuppressConfig(dto);
        String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
        dto.setSuppressBeginTime(suppressBeginTime);
        dto.setSuppressEndTime(suppressEndTime);
        if (null == configDTO) {
            dto.setCreateTime(suppressBeginTime);
            dto.setCreateBy(tenantId);
            configMapper.save(dto);
        }
        else {
            dto.setUpdateTime(suppressBeginTime);
            dto.setUpdateBy(tenantId);
            configMapper.update(dto);
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 删除告警压制规则
     * @param deviceCode 设备编码
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delete(String deviceCode) {
        AlarmSuppressConfigDTO dto = new AlarmSuppressConfigDTO();
        dto.setDeviceCode(deviceCode);
        dto.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        configMapper.delete(dto);
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 删除告警压制规则
     * @param deviceCode 设备编码
     * @return
     */
    @Override
    public AlarmSuppressConfigDTO querySuppressConfig(String deviceCode) {
        AlarmSuppressConfigDTO dto = new AlarmSuppressConfigDTO();
        dto.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        dto.setDeviceCode(deviceCode);
        return configMapper.querySuppressConfig(dto);
    }

    /**
     * 删除告警压制规则
     * @param tenantIdList 租户id
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public List<AlarmSuppressConfigDTO> querySuppressConfigByTenantIds(List<String> tenantIdList) {
        return configMapper.querySuppressConfigByTenantIds(tenantIdList);
    }
}
