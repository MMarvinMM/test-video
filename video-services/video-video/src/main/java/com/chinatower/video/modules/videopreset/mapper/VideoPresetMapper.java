package com.chinatower.video.modules.videopreset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetSnapshotVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 预置点信息表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface VideoPresetMapper extends BaseMapper<VideoPresetDO> {

    String getImgUrl();

    // 该方法废除 2024-2-18 feng.ty
    List<AlgorithmParamPresetVO> selectPTZByIdList(@Param("list") List<Long> list, @Param("deviceCodeList") List<String> deviceCodeList, @Param("industryCode") String industryCode);

    // 替代 上面的同名方法
    List<AlgorithmParamPresetVO> selectPTZByIdList(@Param("pos") List<AlgorithmConfigVO> pos, @Param("industryCode") String industryCode);

    VideoPresetDO getPresetImg(PresetDTO presetDTO);

    void updateByPresetId(PresetDTO presetDTO);

    int stopGuard(GuardDTO guardDTO);

    int saveGuard(GuardDTO guardDTO);

    int resetPreset(GuardDTO guardDTO);

    int hasOtherUserGuard(GuardDTO guardDTO);

    void updateAlgorithmImg(VideoPresetDO videoPresetDO);

    List<String> getPresetName(AddPresetReq addPresetReq);

    Integer updateVideoPresetByCompositeKeys(VideoPresetDO videoPresetDO);

    Integer deleteVideoPresetByCompositeKeys(VideoPresetDO videoPresetDO);

    Integer deleteBatchVideoPresetByCompositeKeys(@Param("presets") List<Long> presets, @Param("deviceCode") String deviceCode);


    VideoPresetDO selectByCombinedIndex(@Param("presetId") Long presetId, @Param("deviceCode") String deviceCode);

    int getPresetId(long presetId,String deviceCode);

    VideoPresetDO selectByIdAndDeviceCode(Long presetId,String deviceCode);

    List<GuardDTO> getOtherGuardInfo(GuardDTO guardDTO);

    List<GuardDTO> otherGuardList(GuardDTO guardDTO);


    List<GuardDTO> queryGuardCount(GuardDTO guardDTO);

    List<PresetDTO> queryPresetInfoBatch(List<VmVideoPresetSnapshotMongo> list);

    List<DeviceInfoVO> queryChannelInfoBatch(List<VmVideoPresetSnapshotMongo> list);

    List<DeviceInfoVO> queryDeviceInfoBatch(List<VmVideoPresetSnapshotMongo> list);

    int updateDeviceRegistration(String deviceCode, String channelCode, Long presetId);

    int deleteRegistrationInfo(String deviceCode, String channelCode, Long presetId);

    List<VideoPresetSnapshotVO> queryPresetSnapshotList(VideoPresetSnapshotVO videoPresetSnapshotVO);

    DeviceInfoVO getDeviceInfo(String deviceCode);
    void updatePresetSnapshotList(List<VideoPresetSnapshotVO> presetSnapshotList);

    void deletePresetSnapshot(VideoPresetSnapshotVO VideoPresetSnapshotVO);

    void deletePresetSnapshotList(List<Integer> idList);

    String getPresetSnapshotTime(VideoPresetSnapshotVO videoPresetSnapshotVO);

    List<AlarmTypeDO> getAiAlarmType(SysUser user);

    /**
     * 根据租户、订单、设备查询受理节点(accept_node)
     */
    String selectAcceptNodeByOrderDevice(@Param("tenantId") String tenantId,
                                         @Param("industryCode") String industryCode,
                                         @Param("deviceCode") String deviceCode);

    /**
     * 校验摄像机与算法是否仍存在有效的订购关系（vm_order_devices + vm_order_algorithms，同一订单套餐下）
     *
     * @param tenantId      租户id
     * @param industryCode  行业编码
     * @param deviceCode    设备编码
     * @param algorithmCode 算法编码
     * @return 关系记录数（>0 表示存在有效订购关系）
     */
    int countValidOrderAlgorithmRelation(@Param("tenantId") String tenantId,
                                         @Param("industryCode") String industryCode,
                                         @Param("deviceCode") String deviceCode,
                                         @Param("algorithmCode") String algorithmCode);
}

