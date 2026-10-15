package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dataobject.AlarmLableDO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.mapper.AlarmLableMapper;
import com.chinatower.event.repository.IAlarmLableRepository;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 告警类型标签表 服务实现类
 * </p>
 *
 * @author chenmf
 * @since 2022-01-26
 */
@Service
@Slf4j
@DS(EbikeDataBaseConstant.PLATFORM)
public class AlarmLableRepositoryImpl extends ServiceImpl<AlarmLableMapper, AlarmLableDO> implements IAlarmLableRepository {

    @Autowired
    private AlarmLableMapper alarmLableMapper;

    /**
     * 查询告警标签数量
     *
     * @return
     * @author yhb
     * @date 2022/4/26
     * @version
     */
    @Override
    public int countTagName(AlarmLableDO alarmLableDO) {

        QueryWrapper<AlarmLableDO> queryWrapper = new QueryWrapper();
        queryWrapper.eq("lable_name", alarmLableDO.getLableName());
        queryWrapper.eq("tenant_id", alarmLableDO.getTenantId());
        queryWrapper.eq("industry_code", alarmLableDO.getIndustryCode());

        return alarmLableMapper.selectCount(queryWrapper);
    }

    /**
     * 获取全部告警类型标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    @Override
    public List<AlarmLableRelDTO> getAllTagsByTenantId(String tenantId, String industryCode) {

        return alarmLableMapper.getAllTagsByTenantId(tenantId, industryCode);
    }

    /**
     * 标签个数
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    @Override
    public int countTag(AlarmLableDO alarmLableDO) {
        QueryWrapper<AlarmLableDO> queryWrapper = new QueryWrapper();
        queryWrapper.eq("tenant_id", alarmLableDO.getTenantId());
        queryWrapper.eq("industry_code", alarmLableDO.getIndustryCode());
        return alarmLableMapper.selectCount(queryWrapper);
    }

    /**
     * 查询标签id是否存在
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    public List<Long> getByIds(List<Long> idList, String tenantId) {
        return alarmLableMapper.getByIds(idList, tenantId);
    }
}
