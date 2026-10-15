package com.chinatower.video.modules.videopatrolplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.video.api.entity.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolInfoDO;
import com.chinatower.video.modules.videopatrolplan.dto.PatrolAlgoRelDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 巡航路径信息表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface PatrolInfoMapper extends BaseMapper<PatrolInfoDO> {
    List<QueryPatrolData> getPatrolListNew(VideoPatrolVO videoPatrolVO);

    /**
     * 统计数量
     * @param presetId
     * @return
     */
    int getPatrolCount(@Param("presetId") Long presetId, @Param("deviceCode") String deviceCode);

    DeviceInfoVO getDeviceInfo(String deviceCode);

    List<PatrolInfoVO> getPatrolInfoList(PatrolInfoVO vo);

    List<PatrolInfoVO> getPatrolMergeList(PatrolInfoVO vo);

    List<PatrolInfoVO> getPatrolMergeListById(PatrolInfoVO vo);

    List<PatrolInfoVO> getPatrolMergeListNew(PatrolInfoVO vo);

    int deletePatrolMerge(PatrolInfoVO vo);

    int deletePatrolPresetMerge(PatrolInfoVO vo);

    int savePatrolMerge(PatrolInfoVO vo);

    int updatePatrolMerge(PatrolInfoVO vo);

//    int savePatrolPresetMerge(PresetDTO vo);
    int batchSavePatrolPresetMerge(List<PresetDTO> list);

//    List<AddPatrolPresetReq> getPatrolPresetMergeList(PatrolInfoVO vo);

    List<AddPatrolPresetReq> getPatrolPresetList(PatrolInfoVO vo);
    List<QueryPatrolListData> getPatrolPresetListNew(VideoPatrolVO videoPatrolVO);

    List<PatrolInfoVO> getStartPatrolInfoList(PatrolInfoVO vo);

    int updatePatrolInfo(PatrolInfoDO vo);

    int updatePatrolInfoBatch(List<PatrolInfoDO> list);

    int chkOrderDevices(SttPatrolReq patrolDTO);

    List<PatrolInfoVO> chkOrder(SttPatrolReq patrolDTO);

    int isExistByPresetId(@Param("presetId") Long presetId, @Param("deviceCode") String deviceCode);

    List<String> queryARCruisePlan(SttPatrolReq vo);

    int isExistARByPresetId(@Param("presetId") Long presetId, @Param("deviceCode") String deviceCode);

    List<PatrolInfoVO> selectPatrolMerge(PatrolInfoVO vo);

    String getMinChannelCode(String deviceCode);

    String getRealChannelCode(String deviceCode);

    List<String> getExistTimePeriod(VideoBaseReq patrolDTO);

    int updatePatrol(List<VideoPatrolVO> list);

    int updateMerge(List<VideoPatrolVO> list);

    List<PatrolInfoVO> getPatrolMerge();

    boolean updatePatrolStartTimeBatch(List<PatrolInfoDO> list);

    List<Long> patrolAlgoRel(PatrolAlgoRelDTO dto);
}
