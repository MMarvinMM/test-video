package com.chinatower.event.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.SysAlarmTagsDO;
import com.chinatower.event.dataobject.SysAlarmTagsTypeRelDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
import com.chinatower.event.mapper.SysAlarmTagsTypeRelMapper;
import com.chinatower.event.repository.ISysAlarmTagsTypeRelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
public class SysAlarmTagsTypeRelRepositoryImpl extends ServiceImpl<SysAlarmTagsTypeRelMapper, SysAlarmTagsTypeRelDO> implements ISysAlarmTagsTypeRelRepository {
    
    @Autowired
    private SysAlarmTagsTypeRelMapper sysAlarmTagsTypeRelMapper;
    
    /**
     * 告警类型对应标签查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    @Override
    public List<AlarmTagsTypeRelDTO> getTagsByAlarmType(String alarmTypeId, List<String> alarmTypeIdList,String tenantId, String industryCode) {
    
        return sysAlarmTagsTypeRelMapper.getTagsByAlarmType(alarmTypeId,alarmTypeIdList ,tenantId, industryCode);
    }

    @Override
    public List<AlarmTypeTagInfoVO> selectRelateTagInfoByAlarmType(String tenantId, String industryCode,
                                                                   List<String> typeValueList, String dataScope) {
        return sysAlarmTagsTypeRelMapper.selectRelateTagInfoByAlarmType(tenantId, industryCode, typeValueList, dataScope);
    }

    @Override
    public List<AlarmTypeTagInfoVO> selectTagInfoByTenant(String tenantId, String industryCode) {
        return sysAlarmTagsTypeRelMapper.selectTagInfoByTenant(tenantId, industryCode);
    }

    /**
     * 当前标签已关联标签数量
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    @Override
    public int selectTagRelCount(Long tagId, String tenantId) {
    
        QueryWrapper<SysAlarmTagsTypeRelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_id", tagId);
        queryWrapper.eq("tenant_id" ,tenantId);
        queryWrapper.eq("delete_flag", Constants.VALID_STATE);
        
        return sysAlarmTagsTypeRelMapper.selectCount(queryWrapper);
    }
    
    /**
     * 修改关联标签表中的标签名
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    @Override
    public void modifyRelTagName(SysAlarmTagsDO sysAlarmTagsDO) {
    
        SysAlarmTagsTypeRelDO sysAlarmTagsTypeRelDO = new SysAlarmTagsTypeRelDO();
        sysAlarmTagsTypeRelDO.setTagName(sysAlarmTagsDO.getTagName());
        sysAlarmTagsTypeRelDO.setUpdateTime(new Date());
        sysAlarmTagsTypeRelDO.setModifier(sysAlarmTagsDO.getModifier());
        
        QueryWrapper<SysAlarmTagsTypeRelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_id", sysAlarmTagsDO.getTagId());
        queryWrapper.eq("delete_flag", Constants.VALID_STATE);
    
        sysAlarmTagsTypeRelMapper.update(sysAlarmTagsTypeRelDO, queryWrapper);
    }
    
    /**
     * 删除关联标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    @Override
    public void deleteTagsTypeRel(String alarmTypeId, String tenantId, String industryCode) {
    
        sysAlarmTagsTypeRelMapper.deleteTagsTypeRel(alarmTypeId, tenantId, industryCode);
    }

    /**
     * 批量删除关联标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    @Override
    public void deleteTagsTypeRelBatch(List<String> alarmTypeIdList, String tenantId, String industryCode) {

        sysAlarmTagsTypeRelMapper.deleteTagsTypeRelBatch(alarmTypeIdList, tenantId, industryCode);
    }
}
