package com.chinatower.video.modules.videopreset.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;

import java.util.List;

/**
 * <p>
 * 预置点信息表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface IVideoPresetRepository extends IService<VideoPresetDO> {

    String getImgUrl();
    
    /**
     * 查询订单预置点信息
     * 
     * @return 
     * @author chenmf
     * @date 2022/2/15
     * @version
     */
    List<VideoPresetDO> queryOrderPresetInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO);

    VideoPresetDO getPresetImg(PresetDTO presetDTO);

    Integer updateVideoPresetByCompositeKeys(VideoPresetDO videoPresetDO);

    Integer removeVideoPresetByCompositeKeys(VideoPresetDO videoPresetDO);

    Boolean removeBatchVideoPresetByCompositeKeys(BatchDelPresetReq videoPresetDO);
}
