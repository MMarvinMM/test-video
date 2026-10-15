package com.chinatower.video.modules.query.dao;

import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceOptAuthVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.query.entity.HabitVO;
import com.chinatower.video.modules.query.vo.AlgorithmAreaVO;
import com.chinatower.video.modules.query.vo.QueryPresentPointVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: VideoQueryDAO
 * @Description: 视频查询DAO
 * @Author: ldl
 * @Date: 2022-01-27
 **/
@Mapper
public interface VideoQueryDAO {
    QueryRoleReq getRoleById(QueryRoleReq queryRoleReq);

    List<String> getRoleByIds(String[] roleIds);

    List<QueryGetEnumInfoReq> getEnumInfo(QueryGetEnumInfoReq enumVO);

    void setUserHabit(HabitVO userHabit);

    int delUserHabit(HabitVO userHabit);

    String getUserHabit(HabitVO userHabit);

    List<HabitVO> getUserHabitBatch(List<String> list);

    List<VideoChannelVideoPlanVO> queryVideoPlanInfo(VideoChannelVideoPlanVO channelVideoPlanVO);

    List<VideoTimeSectionVO> queryTimeSectionInfo(VideoTimeSectionVO timeSectionVO);

    VideoPatrolVO getPatrol(VideoPatrolVO videoPatrolVO);

    List<VideoPatrolVO> getPatrolByIdList(@Param("idList") List<Long> idList);

    List<QueryPatrolData> getPatrolList(VideoPatrolVO videoPatrolVO);

    List<QueryPatrolListData> getPatrolAllPresetList(List<QueryPatrolData> list);

    List<QueryPresetListData> queryPreset(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode, @Param("deviceCode") String deviceCode);

    List<QueryPresetListData> queryPresetByName(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode,@Param("presetName")String presetName,
    @Param("deviceCode")String deviceCode,@Param("channelCode")String channelCode);

//    QueryGuardListData getGuardName(@Param("guardId")String guardId, @Param("userCode")String userCode);

    int invalidGuard(@Param("deviceCode") String deviceCode);

    int invalidGuardById(@Param("guardId") String guardId,@Param("deviceCode") String deviceCode);

    QueryGuardListData getGuard(@Param("userCode")String userCode, @Param("deviceCode")String deviceCode,@Param("channelCode")String channelCode);

    PointsAndDeviceModelDto getPointsByDeviceAndChannel(QueryPresentPointVo queryPresentPointVo);

    List<PointsAndDeviceModelDto> getHistoryPoints();

    void updateAlgorithm(List<PointsAndDeviceModelDto> updates);

    int queryPresetNameCnt(ModPresetReq modPresetReq);

    List<PresetDTO> queryPresetSnapshotJob(@Param("time")String time, @Param("weekday")int weekday, @Param("day")String day);

    List<DeviceOptAuthVO> queryDeviceOptAuthInfo(DeviceOptAuthVO deviceOptAuthVO);

    List<DeviceOptAuthVO> queryTenantIndustryByUser(DeviceOptAuthVO deviceOptAuthVO);

    String getControlPriority(DeviceOptAuthVO vo);

    String getControlPresetPriority(DeviceOptAuthVO vo);

    String getControlPatrolPriority(DeviceOptAuthVO vo);

    String getControlGuardPriority(DeviceOptAuthVO vo);
    String getUserName(String userId);
    List<String> getLockLevel(String deviceCode, String channelCode);

    List<HabitVO> getUserHabitList(List<String> list);

    List<QueryRoleReq> getQueryRolesByRoleIds(List<SysRole> list);

    List<VideoTimeSectionVO> queryTimeSectionList(List<VideoChannelVideoPlanVO> list);

    List<String> queryHisAuth(String userId);

    void updateUserHabit(HabitVO userHabit);

    List<AlgorithmAreaVO> findAlgoByIds(List<String> list);

    PresetData findPreset(@Param("presetId")String presetId, @Param("deviceCode")String deviceCode, @Param("channelCode")String channelCode);

    void batchUpdateAlgo(List<AlgorithmAreaVO> list);

    Integer checkUserHabit(HabitVO userHabit);

    List<AlgorithmAlarmVO> queryAlgorithmAlarmList();

    List<OrderDevicesVO> getOrderDevices(VideoBaseReq videoBaseReq);

    List<DeviceInfoVO> getChannelsByDeviceCode(List<String> deviceCodeList);
}
