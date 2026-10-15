package com.chinatower.platform.modules.uav.service;

import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.modules.uav.entity.AirLinePhotoTask;
import com.chinatower.platform.modules.uav.entity.UavAlarmInfo;
import com.chinatower.platform.modules.uav.mapper.AirUavMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/2/18 14:55
 */
@Service
public class AirUavService {
    @Resource
    private AirUavMapper airUavMapper;

    /**
     *
     * @return
     */
    public List<UavAlarmInfo> queryUavAlarmList() {
        return airUavMapper.queryUavAlarmList();
    }

    /**
     * 查询用户信息
     * @param list
     * @return
     */
    public List<UserInfoDto> queryUserInfo(List<String> list) {
        return airUavMapper.queryUserInfo(list);
    }

    /**
     * 更新报警信息
     * @param ids
     */
    public void updateUavAlarmInfo(List<String> ids) {
        airUavMapper.updateUavAlarmInfo(ids);
    }

    /**
     *
     * @return
     */
    public List<AirLinePhotoTask> queryUavAirLinePhotoCompare() {
        return airUavMapper.queryUavAirLinePhotoCompare();
    }

    /**
     * 更新任务状态
     * @param ids
     */
    public void updatePhotoCompareTaskStatus(List<String> ids) {
        airUavMapper.updatePhotoCompareTaskStatus(ids);
    }
}
