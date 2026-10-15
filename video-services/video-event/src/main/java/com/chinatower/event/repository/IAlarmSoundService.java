package com.chinatower.event.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chinatower.common.core.domain.AlarmSoundDO;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.AlarmSoundDTO;
import com.chinatower.platform.model.dto.LoginUser;

import java.util.List;

public interface IAlarmSoundService {
    /**
     * 分页查询
     *
     * @param model
     * @return
     */
    Result<IPage<AlarmSoundDO>> queryAlarmSoundList(AlarmSoundDTO model);

    /**
     * 修改
     *
     * @param model
     * @return
     */
    Result<String> modifyAlarmSound(AlarmSoundDO model);

    /**
     * 新增
     *
     * @param model
     * @return
     */
    Result<AlarmSoundDO> addAlarmSound(AlarmSoundDO model);

    /**
     * 删除
     *
     * @param alarmSoundId
     * @return
     */
    Result<String> deleteAlarmSound(Long alarmSoundId);

    /**
     * 查询单条信息
     *
     * @param alarmSoundId
     * @return
     */
    Result<AlarmSoundDO> selectByAlarmSoundId(Long alarmSoundId,LoginUser loginUser);

    /**
     * 启用/停用
     *
     * @param model
     * @return
     */
    Result<String> updateStatusCd(AlarmSoundDTO model, LoginUser loginUser);


    /**
     * 恢复默认
     *
     * @param model
     * @return
     */
    Result<String> reset(LoginUser loginUser);

    Result<List<AlarmSoundDO>> enableData(LoginUser loginUser);

    /**
     * 查询租户下的告警声音
     *
     * @param tenantId
     * @return
     */
    Result<AlarmSoundDO> selectByTenantId(LoginUser loginUser);

    Result<AlarmSoundDO> selectById(Long id);

    Result<String> updateDefault(AlarmSoundDO model);

    Result<Boolean> checkAlarmSoundName(AlarmSoundDO model);

    Result<List<AlarmSoundDO>> selectAlarmSoundList(AlarmSoundDTO model);

    Result<List<AlarmSoundDO>> selectDefaultAlarmSoundList(AlarmSoundDTO model);
}
