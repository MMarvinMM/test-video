package com.chinatower.video.modules.videorecordplan.service.impl;

import com.chinatower.common.entity.Result;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.constants.IndustryConstants;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.query.entity.HabitVO;
import com.chinatower.video.modules.videorecordplan.dataobject.VideoScreenConfigDO;
import com.chinatower.video.modules.videorecordplan.dto.VideoScreenConfigChannelVO;
import com.chinatower.video.modules.videorecordplan.mapper.VideoScreenConfigMapper;
import com.chinatower.video.modules.videorecordplan.repository.IVideoScreenConfigRepository;
import com.chinatower.video.modules.videorecordplan.service.IVideoScreenConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述: 视频管理--视频设置服务类
 *
 * @auther: zhaozj
 * @date: 2022/10/12
 * @version: releae_v1.5
 * @requestID: 视频管理--视频设置
 */
@Service
@Slf4j
public class VideoScreenConfigService implements IVideoScreenConfigService {
    @Resource
    private VideoScreenConfigMapper videoScreenConfigMapper;
    @Resource
    private IVideoScreenConfigRepository videoScreenConfigRepository;
    @Resource
    private VideoQueryDAO videoQueryDAO;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 功能描述: 新增视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: String
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addScreenConfig(VideoScreenConfigDO videoScreenConfigDO) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            videoScreenConfigDO.setUserId(user.getUserId());
            videoScreenConfigDO.setCreator(SecurityUtils.getOperator());
            videoScreenConfigDO.setModifier(SecurityUtils.getOperator());
            videoScreenConfigDO.setCreateTime(new Date());
            videoScreenConfigDO.setUpdateTime(new Date());
            if (!SecurityUtils.isSwitch()) {
                if (IndustryConstants.TWO_FLAG.equals(user.getUserType())) {
                    videoScreenConfigDO.setIndustryCode(user.getIndustryCode());
                    videoScreenConfigDO.setAppVerCode(user.getAppVerCode());
                    videoScreenConfigDO.setTenantId(user.getTenantId());
                    //客户视角按照客户+行业维度更新占位图
                    videoScreenConfigMapper.updateVideoInitUrl(videoScreenConfigDO);
                }
            }
            videoScreenConfigRepository.save(videoScreenConfigDO);
            // 新增逻辑，增加自定义通道信息
            Long id = videoScreenConfigDO.getId();
            if(null != videoScreenConfigDO.getVideoPreloaderModel() && 3 == videoScreenConfigDO.getVideoPreloaderModel() && !CollectionUtils.isEmpty(videoScreenConfigDO.getDeviceList())){
                //配置自定义通道信息
                videoScreenConfigMapper.addVideoScreenConfigChannel(videoScreenConfigDO);
            }
            //清除记忆模式
            HabitVO habitVO = new HabitVO();
            habitVO.setHabitKey(user.getUserId() + IndustryConstants.VIDEO_MEMORY_KEY);
            videoQueryDAO.delUserHabit(habitVO);
            return Result.data("新增视频屏数设置信息接口成功");
        } catch (Exception e){
            log.error("addScreenConfig错误：",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("新增视频屏数设置信息接口异常");
        }
    }


    /**
     * 功能描述: 更新视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: String
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateScreenConfig(VideoScreenConfigDO videoScreenConfigDO) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            videoScreenConfigDO.setModifier(SecurityUtils.getOperator());
            videoScreenConfigDO.setUpdateTime(new Date());
            videoScreenConfigMapper.updateScreenConfig(videoScreenConfigDO);//重写更新方法，不再使用mybatis-plus自带方法，防止由有水印更新成无水印时数据没更新
            if(null != videoScreenConfigDO.getVideoPreloaderModel()  && 3 == videoScreenConfigDO.getVideoPreloaderModel()){
                // 先删除原有数据，在插入当次数据
                videoScreenConfigMapper.delVideoScreenConfigChannel(videoScreenConfigDO);
                if(!CollectionUtils.isEmpty(videoScreenConfigDO.getDeviceList())){
                    //配置自定义通道信息
                    videoScreenConfigDO.setCreator(SecurityUtils.getOperator());
                    videoScreenConfigMapper.addVideoScreenConfigChannel(videoScreenConfigDO);
                }
            }else if(null != videoScreenConfigDO.getVideoPreloaderModel() && 3 != videoScreenConfigDO.getVideoPreloaderModel()){
                //先查询是否有配置自定义通道，如果有需要删除自定义通道信息
                VideoScreenConfigVO configVO = new VideoScreenConfigVO();
                configVO.setId(videoScreenConfigDO.getId());
                List<VideoScreenConfigChannelVO> configChannelList = videoScreenConfigMapper.queryScreenConfigChannel(configVO);
                if(!CollectionUtils.isEmpty(configChannelList)){
                    videoScreenConfigMapper.delVideoScreenConfigChannel(videoScreenConfigDO);
                }
            }
            if (!SecurityUtils.isSwitch()) {
                if (IndustryConstants.TWO_FLAG.equals(user.getUserType())) {
                    videoScreenConfigDO.setIndustryCode(user.getIndustryCode());
                    videoScreenConfigDO.setTenantId(user.getTenantId());

                    //客户视角按照客户+行业维度更新占位图
                    videoScreenConfigMapper.updateVideoInitUrl(videoScreenConfigDO);
                }
            }

            //清除记忆模式
            HabitVO habitVO = new HabitVO();
            habitVO.setHabitKey(user.getUserId() + IndustryConstants.VIDEO_MEMORY_KEY);
            videoQueryDAO.delUserHabit(habitVO);
            return Result.data("更新视频屏数设置信息接口成功");
        } catch (Exception e){
            log.error("updateScreenConfig错误：",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("更新视频屏数设置信息接口异常");
        }
    }


    /**
     * 功能描述: 查询视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: List<VideoScreenConfigVO>
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    @Override
    public Result<List<VideoScreenConfigVO>> queryScreenConfig(@RequestBody VideoScreenConfigVO videoScreenConfigVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //是否是铁塔账号 0否 1是
        String isTower = Constants.ZERO;
        if (!SecurityUtils.isSwitch()) {
            if (IndustryConstants.TWO_FLAG.equals(user.getUserType())) {
                videoScreenConfigVO.setIndustryCode(user.getIndustryCode());
                videoScreenConfigVO.setTenantId(user.getTenantId());
            } else {
                isTower = Constants.ONE;
            }
        }else{
            isTower = Constants.ONE;
        }
        videoScreenConfigVO.setUserId(user.getUserId());
        videoScreenConfigVO.setIsTower(isTower);
        List<VideoScreenConfigVO> configList = videoScreenConfigMapper.queryScreenConfig(videoScreenConfigVO);
        if(!CollectionUtils.isEmpty(configList) && configList.get(0).getVideoPreloaderModel() != null && 3 == configList.get(0).getVideoPreloaderModel()){
            VideoScreenConfigVO configVO = configList.get(0);
            Long configId = configVO.getId();
            videoScreenConfigVO.setId(configId);
            List configChannelList = videoScreenConfigMapper.queryScreenConfigChannel(videoScreenConfigVO);
            configVO.setDeviceList(configChannelList);
        }
        //视频播放占位图加密
        List<String> collect = configList.stream().map(VideoScreenConfigVO::getVideoInitUrl).filter(videoInitUrl -> !StringUtils.isEmpty(videoInitUrl)).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(collect)){
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(collect);
            // 处理Obs文件URL加密返回
            configList.forEach(item -> item.setVideoInitUrl(stringStringMap.get(item.getVideoInitUrl())));
        }
        return Result.data(configList);
    }

    @Override
    public Result<VideoScreenConfigVO> queryWaterMark(VideoScreenConfigVO videoScreenConfigVO) {
        ;
        return Result.data(videoScreenConfigMapper.queryScreenConfigLast(videoScreenConfigVO));
    }

}
