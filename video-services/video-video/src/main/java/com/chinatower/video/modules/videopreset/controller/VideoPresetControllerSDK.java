package com.chinatower.video.modules.videopreset.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.ObsClientService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import com.chinatower.video.modules.videopreset.repository.IVideoPresetRepository;
import com.chinatower.video.modules.videopreset.service.IVideoPresetInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ClassName VideoPresetController
 * @Description
 * @Author Chensw
 * @Date 2022/1/24
 **/
@RestController
@Slf4j
@Api(value = "预置位模块", tags = "预置位模块")
public class VideoPresetControllerSDK {

    @Autowired
    private IVideoPresetInfoService videoPresetInfoService;

    @Autowired
    private IVideoPresetRepository videoPresetRepository;

    @Autowired
    private ObsClientService obsClientService;

    /**
     * 功能描述: 增加预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    @ApiOperation(value = "增加预置点", notes = "增加预置点")
    @PostMapping("/video/patrol/sdk/addCameraPreset")
    public Result<String> addCameraPreset(@Validated @RequestBody AddPresetReq addPresetReq) {
        log.info("addCameraPreset" + VideoConstants.SDKREQ + addPresetReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        addPresetReq.setIndustryCode(currentUser.getIndustryCode());
        addPresetReq.setAppVerCode(currentUser.getAppVerCode());
        addPresetReq.setUserCode(currentUser.getUserId());
        addPresetReq.setTenantId(currentUser.getTenantId());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        videoPresetInfoService.addCameraPreset(addPresetReq,headerMap);
        return Result.success("增加预置点成功!");
    }

    /**
     * 功能描述: 删除预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    @ApiOperation(value = "删除预置点", notes = "删除预置点")
    @PostMapping("/video/patrol/sdk/delCameraPreset")
    public Result<String> delCameraPreset(@Validated @RequestBody DelPresetReq presetDTO) {
        log.info("delCameraPreset" + VideoConstants.SDKREQ + presetDTO);
        videoPresetInfoService.delCameraPreset(presetDTO);
        return Result.success("删除预置点成功!");
    }

    /**
     * 功能描述: 批量删除预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    @ApiOperation(value = "批量删除预置点", notes = "批量删除预置点")
    @PostMapping("/video/patrol/sdk/batchDelCameraPreset")
    public Result<String> batchDelCameraPreset(@Validated @RequestBody BatchDelPresetReq presetDTO) {
        log.info("batchDelCameraPreset" + VideoConstants.SDKREQ + presetDTO);
        videoPresetInfoService.batchDelCameraPreset(presetDTO);
        return Result.success("批量删除预置点成功!");
    }

    /**
     * 功能描述: 修改预置点
     *
     * @param: presetDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/1/24
     * @version: 1.0
     */
    @ApiOperation(value = "修改预置点", notes = "修改预置点")
    @PostMapping("/video/patrol/sdk/updatePresetInfo")
    public Result<String> updatePresetInfo(@Validated @RequestBody ModPresetReq modPresetReq) {
        log.info("updatePresetInfo" + VideoConstants.SDKREQ + modPresetReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        modPresetReq.setIndustryCode(currentUser.getIndustryCode());
        modPresetReq.setAppVerCode(currentUser.getAppVerCode());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        videoPresetInfoService.updatePresetInfo(modPresetReq,headerMap);
        return Result.success("修改预置点成功!");
    }

    /**
     * 查询订单预置点信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/15
     * @version
     */
    @PostMapping("/video/patrol/sdk/queryOrderPresetInfo")
    public Result<List<PresetDTO>> queryOrderPresetInfo(@RequestBody QueryOrderDeviceSetDTO presetDTO) {
        log.info("queryOrderPresetInfo" + VideoConstants.SDKREQ + presetDTO);
        if (StringUtils.isEmpty(presetDTO.getIndustryCode())) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser.getUser();
            presetDTO.setIndustryCode(currentUser.getIndustryCode());
        }
        List<PresetDTO> list = videoPresetInfoService.queryOrderPresetInfo(presetDTO);
        return Result.data(list);
    }

    /**
     * 功能描述: 获得预置位图片
     *
     * @param presetDTO
     * @return: Result<PresetVO>
     * @auther: chenshaowu
     * @date: 2022/3/13
     * @version: 1.0
     */
    @PostMapping("/video/algorithm/sdk/getPresetImg")
    Result<PresetVO> getPresetImg(@RequestBody PresetDTO presetDTO) {
        log.info("getPresetImg" + VideoConstants.SDKREQ + presetDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        PresetVO presetVO = videoPresetInfoService.getPresetImg(presetDTO,headerMap);
        if (presetVO.getIsPlot() == 1) {
            return Result.data(HttpStatus.NO_CONTENT, presetVO, "您无需标绘，可直接点击暂存按钮完成通道算法配置后保存。如需进行标绘，请选择“实时标绘”后进行。");
        }
        if (StringUtils.isBlank(presetVO.getImgUrl())) {
            return Result.fail(DeviceConstants.WARN_MESS2);
        }
        return Result.data(presetVO);
    }

    @PostMapping(value = "/video/patrol/sdk/selectPTZByIdList")
    public List<AlgorithmParamPresetVO>  selectPTZByIdList(@RequestBody List<AlgorithmConfigVO> pos) {
        log.info("selectPTZByIdList" + VideoConstants.SDKREQ + pos);
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        return videoPresetInfoService.selectPTZByIdList(pos,industryCode);
    }

    @PostMapping(value = "/video/patrol/sdk/selectById")
    public VideoPresetDO selectById(@RequestBody VideoPresetDO videoPresetDO) {
        log.info("selectById" + VideoConstants.SDKREQ + JSON.toJSONString(videoPresetDO));
        return videoPresetInfoService.selectByIdAndDeviceCode(videoPresetDO.getPresetId(),videoPresetDO.getDeviceCode());
    }
    
    /**
     * 功能描述: 分页查询预置位定时抓的图片
     *
     * @param: presetListImgJobDTO
     * @auther: wanglei
     * @date: 2023/9/15
     * @version: 1.6.10
     */
    @ApiOperation(value = "分页查询预置位定时抓的图片", notes = "分页查询预置位定时抓的图片")
    @PostMapping("/video/patrol/sdk/queryPresetListImgJob")
    public Result<Page<VmVideoPresetSnapshotMongoVO>> queryPresetListImgJob(@Validated @RequestBody PresetListImgJobDTO presetListImgJobDTO)
    {
    	log.info(VideoConstants.QUERY_PRESET_LIST_IMG_JOB + VideoConstants.CONTROLLERREQ + presetListImgJobDTO);
    	Page<VmVideoPresetSnapshotMongoVO> pageInfo = videoPresetInfoService.queryPresetListImgJob(presetListImgJobDTO);
    	return Result.data(pageInfo);
    }
}
