package com.chinatower.platform.repository.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeDTO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.platform.mapper.IAlarmTypeMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.repository.IAlarmTypeRepository;
import com.chinatower.platform.repository.ISysService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 告警类型表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
@Service
@Slf4j
public class IAlarmTypeRepositoryImpl extends ServiceImpl<IAlarmTypeMapper, AlarmTypeDO> implements IAlarmTypeRepository {

    @Autowired
    private IAlarmTypeMapper alarmTypeMapper;

    @Autowired
    private ISysService sysService;

    /**
     * @param body
     * @Description 查询所有订购类型
     * @Param [body]
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AlarmTypeDO> findOrderAlarmAll(AlarmTypeDTO body) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(body.getTenantId())) {
            body.setTenantId(loginUser.getUser().getTenantId());
        }
        body.setAppVerCode(loginUser.getUser().getAppVerCode());
        body.setIndustryCode(loginUser.getUser().getIndustryCode());
        //  查询客户的告警来源
        body.setAlarmSources(findAlarmSource());
        return alarmTypeMapper.findOrderAlarmAll(body);
    }

    /**
     * @Description 查询客户的告警来源
     */
    private List<String> findAlarmSource() {
        AlarmSourceSubTypeDTO body = new AlarmSourceSubTypeDTO();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(Constants.NO_USER_INFO);
        }
        body.setTenantId(loginUser.getUser().getTenantId());
        body.setUserId(sysService.getLoginUserId());
        body.setIndustryCode(loginUser.getUser().getIndustryCode());
        body.setAppVerCode(loginUser.getUser().getAppVerCode());
        return findAlarmSource(body);
    }

    /**
     * 查询客户的告警来源
     *
     * @param body
     * @return
     */
    @Override
    public List<String> findAlarmSource(AlarmSourceSubTypeDTO body) {
        List<String> alarmSources = new ArrayList<>();
        //人工上报和一件告警为固定告警来源
        alarmSources.add(WarningSourceEnum.ARTIFICIAL.getTypeValue());
        alarmSources.add(WarningSourceEnum.UAV_ALARM.getTypeValue());
        //1、是否有摄像机告警来源
        if (alarmTypeMapper.devByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.CAMERA_ALARM.getTypeValue());
        }
        // 是否有无人机告警来源
        if (alarmTypeMapper.uavByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue());
        }
        //2、是否有物联设备、雷达告警来源
        body.setCategoryCode(String.valueOf(OrderConstants.ORDER_SENSOR_CODE));
        if (alarmTypeMapper.iotDevByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.IOT_ALARM.getTypeValue());
        }
        body.setCategoryCode(String.valueOf(OrderConstants.ORDER_RADAR_CODE));
        if (alarmTypeMapper.iotDevByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.RADAR_ALARM.getTypeValue());
        }

        //3、是否有卫星告警来源 跟志伟和学斌总确认，卫星告警类型查询不卡订购 0516
//        if (alarmTypeMapper.serviceByTenantIdCount(body) > 0) {
        alarmSources.add(WarningSourceEnum.SATELLITE_ALARM.getTypeValue());
//        }
        return alarmSources;
    }

    /**
     * 查询联标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    public List<AlarmLableRelDTO> getAlarmLableByIds(List<String> idList, String waringOrderId) {
        List<AlarmLableRelDTO> alarmLableByIds = alarmTypeMapper.getAlarmLableByIds(idList, waringOrderId);
        if(CollectionUtils.isEmpty(alarmLableByIds)){
            return new ArrayList<>();
        }
        return alarmLableByIds;
    }
}
