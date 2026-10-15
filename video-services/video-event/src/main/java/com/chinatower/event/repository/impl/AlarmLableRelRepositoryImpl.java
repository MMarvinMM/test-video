package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dataobject.AlarmLableDO;
import com.chinatower.event.dataobject.AlarmLableRelDO;
import com.chinatower.event.entity.dto.AddLableRelationDTO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.mapper.AlarmLableRelMapper;
import com.chinatower.event.repository.IAlarmLableRelRepository;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 告警类型标签表 服务实现类
 * </p>
 *
 * @author yhb
 * @since 2022-12-29
 */
@Service
@Slf4j
@DS(EbikeDataBaseConstant.PLATFORM)
public class AlarmLableRelRepositoryImpl extends ServiceImpl<AlarmLableRelMapper, AlarmLableRelDO> implements IAlarmLableRelRepository {

    @Autowired
    private AlarmLableRelMapper alarmLableRelMapper;

    /**
     * 告警对应标签查询
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    public List<AlarmLableRelDTO> getTagsByAlarmType(String waringOrderId) {

        return alarmLableRelMapper.getTagsByWaringId(waringOrderId);
    }

    /**
     * 当前标签已关联标签数量
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    public int selectLableRelCount(Long tagId) {

        QueryWrapper<AlarmLableRelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lable_id", tagId);

        return alarmLableRelMapper.selectCount(queryWrapper);
    }

    /**
     * 修改关联标签表中的标签名
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    public void modifyRelTagName(AlarmLableDO alarmLableDO) {

        AlarmLableRelDO alarmLableRelDO = new AlarmLableRelDO();
        alarmLableRelDO.setLableName(alarmLableDO.getLableName());
        alarmLableRelDO.setUpdateTime(new Date());
        alarmLableRelDO.setModifier(alarmLableDO.getModifier());

        QueryWrapper<AlarmLableRelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lable_id", alarmLableDO.getLableId());

        alarmLableRelMapper.update(alarmLableRelDO, queryWrapper);
    }

    /**
     * 删除关联标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    public void deleteLableRel(String waringOrderId) {

        alarmLableRelMapper.deleteAlarmLableRel(waringOrderId);
    }

    /**
     * 批量删除关联标签
     *
     * @return
     * @author cuisj
     * @date 2024/07/23
     * @version
     */
    @Override
    public void deleteLableRelBatch(AddLableRelationDTO addLableRelationDTO) {

        alarmLableRelMapper.deleteLableRelBatch(addLableRelationDTO);
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
    public List<AlarmLableRelDTO> getAlarmLableByIds(List<String> idList,String waringOrderId) {
        List<AlarmLableRelDTO> alarmLableByIds = alarmLableRelMapper.getAlarmLableByIds(idList,waringOrderId);
        if(CollectionUtils.isEmpty(alarmLableByIds)){
            return new ArrayList<>();
        }
        return alarmLableByIds;
    }
}
