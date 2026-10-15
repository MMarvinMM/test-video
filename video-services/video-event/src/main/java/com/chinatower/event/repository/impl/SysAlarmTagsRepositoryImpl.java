package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.SysAlarmTagsDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.mapper.SysAlarmTagsMapper;
import com.chinatower.event.repository.ISysAlarmTagsRepository;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
public class SysAlarmTagsRepositoryImpl extends ServiceImpl<SysAlarmTagsMapper, SysAlarmTagsDO> implements ISysAlarmTagsRepository {

    @Autowired
    private SysAlarmTagsMapper sysAlarmTagsMapper;

    /**
     * 查询告警类型标签数量
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    @Override
    public int countTagName(SysAlarmTagsDO sysAlarmTagsDO) {

        QueryWrapper<SysAlarmTagsDO> queryWrapper = new QueryWrapper();
        queryWrapper.eq("tag_name", sysAlarmTagsDO.getTagName());
        queryWrapper.eq("tenant_id", sysAlarmTagsDO.getTenantId());
        queryWrapper.eq("industry_code",sysAlarmTagsDO.getIndustryCode());
        queryWrapper.eq("delete_flag", Constants.VALID_STATE);
        if (null != sysAlarmTagsDO.getTagId()) {
            queryWrapper.ne("tag_id", sysAlarmTagsDO.getTagId());
        }

        return sysAlarmTagsMapper.selectCount(queryWrapper);
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
    public List<AlarmTagsTypeRelDTO> getAllTagsByTenantId(String tenantId, String industryCode) {

        return sysAlarmTagsMapper.getAllTagsByTenantId(tenantId, industryCode);
    }

    /**
     * 获取告警类型为可见的告警类型标签
     *
     * @return
     * @author tianhao
     * @date 2022/11/11
     * @version
     */
    @Override
    public List<AlarmTagsTypeRelDTO> getVisibleTags(String tenantId, String industryCode, String appVerCode) {

        return sysAlarmTagsMapper.getVisibleTags(tenantId, industryCode, appVerCode);
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
    public int countTag(String tenantId, String industryCode) {
        QueryWrapper<SysAlarmTagsDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.eq("industry_code", industryCode);
        queryWrapper.eq("delete_flag", Constants.VALID_STATE);
        return sysAlarmTagsMapper.selectCount(queryWrapper);
    }

    /**
     * 查询标签id是否存在
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    @Override
    public List<Long> getByIds(List<Long> idList, String tenantId) {
        return sysAlarmTagsMapper.getByIds(idList, tenantId);
    }

    /**
     * 查询标签id对应的告警类型
     *
     * @param idList
     * @param tenantId
     * @return
     * @author sutn
     * @date 2022/4/27
     * @version
     */
    @Override
    @DS(EbikeDataBaseConstant.BASE)
    public List<AlarmTagsTypeRelDTO> getAlarmTypeByIds(List<String> idList, String tenantId) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        return sysAlarmTagsMapper.getAlarmTypeByIds(idList, tenantId);
    }
}
