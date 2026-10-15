package com.chinatower.video.modules.videopreset.service;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.mongodb.request.Page;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetSnapshotVO;
import com.github.pagehelper.PageInfo;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface IVideoPresetInfoService {

    /**
     * 功能描述: 增加预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    long addCameraPreset(AddPresetReq addPresetReq, Map<String, String> headerMap);

    /**
     * 功能描述: 删除预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    void delCameraPreset(DelPresetReq presetDTO);

    /**
     * 功能描述: 批量 停用 启用 删除 预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    void batchDelCameraPreset(BatchDelPresetReq presetDTO);

    /**
     * 功能描述: 修改预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    void updatePresetInfo(ModPresetReq modPresetReq,Map<String, String> headerMap);

    /**
     * 查询订单预置点信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/15
     * @version
     */
    List<PresetDTO> queryOrderPresetInfo(QueryOrderDeviceSetDTO presetDTO);

    /**
     * 功能描述: 算法获得预置位图片
     *
     * @param presetDTO
     * @return: PresetVo
     * @auther: chenshaowu
     * @date: 2022/3/13
     * @version: 1.0
     */
    PresetVO getPresetImg(PresetDTO presetDTO,Map<String, String> headerMap);

    Result snapShot(PresetDTO presetDTO,Map<String, String> headerMap);

    void snapShotForSample(PlayerSnapShotReq x,Map<String, String> headerMap);

    List<AlgorithmParamPresetVO>  selectPTZByIdList(List<AlgorithmConfigVO> pos,String industryCode);

    VideoPresetDO selectById(@RequestBody Long presetId);

    Result getPresetName(AddPresetReq addPresetReq);
    
    void updatePresetSnapshotInfo(ModPresetSnapshotReq modPresetSnapshotReq);
    
    Result<String> updatePresetName(ModPresetReq modPresetReq);
    
    DelCameraPresetCheck delCameraPresetCheck(DelPresetReq presetDTO);
    
    ReturnT<String> presetSnapshotJob(String param);
    
    Page<VmVideoPresetSnapshotMongo> queryPresetImgJob(PresetImgJobDTO presetImgJobDTO);
    
    /**
     * 功能描述: 分页查询预置位定时抓的图片
     *
     * @param: presetListImgJobDTO
     * @auther: wanglei
     * @date: 2023/9/15
     * @version: 1.6.10
     */
    Page<VmVideoPresetSnapshotMongoVO> queryPresetListImgJob(PresetListImgJobDTO presetListImgJobDTO);

    VideoPresetDO selectByIdAndDeviceCode(Long presetId,String deviceCode);

    /**
     * 功能描述: 更新预置位配准信息
     * @param deviceCode
     * @param channelCode
     * @param presetId
     * @param flag 1：预置位编辑  2：预置位删除
     */
    void updateDeviceRegistration(String deviceCode, String channelCode, Long presetId, String flag);


    PageInfo<VideoPresetSnapshotVO> queryPresetSnapshotList(VideoPresetSnapshotVO videoPresetSnapshotVO);

    void insertPresetSnapshotList(List<VideoPresetSnapshotVO> presetSnapshotList);

    void updatePresetSnapshotList(List<VideoPresetSnapshotVO> presetSnapshotList);

    void deletePresetSnapshot(VideoPresetSnapshotVO videoPresetSnapshotVO);
    void deletePresetSnapshotList(VideoPresetSnapshotVO videoPresetSnapshotVO);

    Result<String> checkPresetSnapshot(VideoPresetSnapshotVO videoPresetSnapshotVO);

    Result<List<AlarmTypeDO>> getAiAlarmType();
    String updateWarningOrderId(PresetImgVO presetImgVO);
}
