package com.chinatower.video.modules.videopreset.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.video.api.entity.BatchDelPresetReq;
import com.chinatower.video.api.entity.PresetDTO;
import com.chinatower.video.api.entity.QueryOrderDeviceSetDTO;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import com.chinatower.video.modules.videopreset.mapper.VideoPresetMapper;
import com.chinatower.video.modules.videopreset.repository.IVideoPresetRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;




/**
 * <p>
 * 预置点信息表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Service
public class VideoPresetRepositoryImpl extends ServiceImpl<VideoPresetMapper, VideoPresetDO> implements IVideoPresetRepository {

    @Resource
    VideoPresetMapper videoPresetMapper;

    @Override
    public String getImgUrl() {
        return baseMapper.getImgUrl();
    }

    /**
     * 查询订单预置点信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/15
     * @version
     */
    @Override
    public List<VideoPresetDO> queryOrderPresetInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {

        QueryWrapper<VideoPresetDO> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("order_code", queryOrderDeviceSetDTO.getOrderCode());
        queryWrapper.eq("tenant_id", queryOrderDeviceSetDTO.getTenantId());
        queryWrapper.eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL_INT);
        if (!StringUtils.isEmpty(queryOrderDeviceSetDTO.getIndustryCode())) {
            queryWrapper.eq("industry_code", queryOrderDeviceSetDTO.getIndustryCode());
        }
        List<String> deviceCodeList = queryOrderDeviceSetDTO.getDeviceCodeList();
        if (!CollectionUtils.isEmpty(deviceCodeList)) {
            queryWrapper.in("device_code", deviceCodeList);
        }

        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public VideoPresetDO getPresetImg(PresetDTO presetDTO) {
        return baseMapper.getPresetImg(presetDTO);
    }

    /**
     *  根据联合索引查询 preset_id device_code 查询
     * @param videoPresetDO
     * @return
     */
    @Override
    public Integer updateVideoPresetByCompositeKeys(VideoPresetDO videoPresetDO) {
        Integer count = videoPresetMapper.updateVideoPresetByCompositeKeys(videoPresetDO);
        return count;
    }

    /**
     *  根据联合索引删除 preset_id device_code 查询
     * @param videoPresetDO
     * @return
     */
    @Override
    public Integer removeVideoPresetByCompositeKeys(VideoPresetDO videoPresetDO) {
        Integer count = videoPresetMapper.deleteVideoPresetByCompositeKeys(videoPresetDO);
        return count;
    }

    /**
     * 根据联合索引批量删除
     * @param videoPresetDO
     * @return
     */
    @Override
    public Boolean removeBatchVideoPresetByCompositeKeys(BatchDelPresetReq videoPresetDO) {
        List<Long> presets = videoPresetDO.getPresets();
        // List<String> deviceCodes = videoPresetDO.getDeviceCodes();
        Integer count = videoPresetMapper.deleteBatchVideoPresetByCompositeKeys(presets, videoPresetDO.getDeviceCode());
        if(videoPresetDO.getPresets().size() == count) {
            return true;
        }
        return false;
    }
}
