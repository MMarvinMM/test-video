package com.chinatower.platform.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dataobject.SysAlarmTagsDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.platform.mapper.ISysAlarmTagsMapper;
import com.chinatower.platform.repository.ISysAlarmTagsRepository;
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
public class ISysAlarmTagsRepositoryImpl extends ServiceImpl<ISysAlarmTagsMapper, SysAlarmTagsDO> implements ISysAlarmTagsRepository {

    @Autowired
    private ISysAlarmTagsMapper sysAlarmTagsMapper;

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
    public List<AlarmTagsTypeRelDTO> getAlarmTypeByIds(List<String> idList, String tenantId) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        return sysAlarmTagsMapper.getAlarmTypeByIds(idList, tenantId);
    }
}
