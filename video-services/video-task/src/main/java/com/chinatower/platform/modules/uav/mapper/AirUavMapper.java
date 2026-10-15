package com.chinatower.platform.modules.uav.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.modules.uav.entity.AirLinePhotoTask;
import com.chinatower.platform.modules.uav.entity.UavAlarmInfo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/2/18 15:01
 */
@Mapper
public interface AirUavMapper {

    /**
     * 查询无人机告警信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<UavAlarmInfo> queryUavAlarmList();

    /**
     * 查询用户信息
     * @param list
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<UserInfoDto> queryUserInfo(List<String> list);

    /**
     * 更新无人机告警信息
     * @param ids
     */
    @DS(EbikeDataBaseConstant.BASE)
    void updateUavAlarmInfo(List<String> ids);

    /**
     * 查询无人机航线照片对比
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<AirLinePhotoTask> queryUavAirLinePhotoCompare();

    /**
     * 更新航线照片对比任务状态
     * @param list
     */
    @DS(EbikeDataBaseConstant.BASE)
    void updatePhotoCompareTaskStatus(List<String> list);
}
