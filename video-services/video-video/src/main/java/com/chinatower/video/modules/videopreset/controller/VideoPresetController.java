package com.chinatower.video.modules.videopreset.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.chinatower.common.core.utils.AssembleUrlUtil;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.entity.dto.TranslnglatPeopleDTO;
import com.chinatower.event.entity.vo.SecondExtraFieldInfoVo;
import com.chinatower.event.entity.vo.SecondRecogFileVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.common.dao.VideoCommonDAO;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetSnapshotVO;
import com.github.pagehelper.PageInfo;
import com.chinatower.video.modules.videopreset.dataobject.*;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.modules.query.dao.PresetSnapshotMongoDAO;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.videopreset.mapper.VideoPresetMapper;
import com.chinatower.video.modules.videopreset.mapper.OrderAlgorithmConfigMapper;
import com.chinatower.video.modules.videopreset.repository.IVideoPresetRepository;
import com.chinatower.video.modules.videopreset.service.IVideoPresetInfoService;
import com.chinatower.video.modules.videopreset.service.YjbSnapshotCallbackService;
import com.chinatower.video.modules.videopreset.upload.BASE64DecodedMultipartFile;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName VideoPresetController
 * @Description
 * @Author Chensw
 * @Date 2022/1/24
 **/
@RestController
@Slf4j
@Api(value = "预置位模块", tags = "预置位模块")
public class VideoPresetController {

    public static final String NOT_AUTH_OPERATION_PRESET = "暂无预置位操作权限";

    public static final String DEL_PRESET_SNAPSHOT_INFO_SUCCESS = "预置位抓图配置删除成功";

    @Autowired
    private IVideoPresetInfoService videoPresetInfoService;

    @Autowired
    private IVideoPresetRepository videoPresetRepository;

    @Autowired
    private VideoPresetMapper videoPresetMapper;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private VideoQueryDAO videoQueryDAO;

    @Autowired
    PresetSnapshotMongoDAO presetSnapshotMongoDAO;

    @Resource
    SystemFeignClient systemFeignClient;

    @Resource
    private YjbSnapshotCallbackService yjbSnapshotCallbackService;

    @Resource
    VideoCommonDAO videoCommonDAO;

    @Autowired
    VideoControlService videoControlService;

    @Autowired
    ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    OrderAlgorithmConfigMapper orderAlgorithmConfigMapper;

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
    @PostMapping("/video/patrol/addCameraPreset")
    public Result<String> addCameraPreset(@Validated @RequestBody AddPresetReq addPresetReq) {
        log.info("addCameraPreset" + VideoConstants.CONTROLLERREQ + addPresetReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        addPresetReq.setIndustryCode(currentUser.getIndustryCode());
        addPresetReq.setAppVerCode(currentUser.getAppVerCode());
        addPresetReq.setUserCode(currentUser.getUserId());
        addPresetReq.setTenantId(currentUser.getTenantId());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        //判断当前时间是否在该租户、用户操控时间范围内
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(user.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                addPresetReq.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.TWO)) {
            return Result.fail(NOT_AUTH_OPERATION_PRESET);
        }
        //增加预置位名称是否重复校验
        List<QueryPresetListData> presetList = videoQueryDAO.queryPresetByName(
                currentUser.getTenantId(),
                currentUser.getIndustryCode(),
                addPresetReq.getPresetName(),
                addPresetReq.getDeviceCode(),
                addPresetReq.getChannelCode()
        );
        if(presetList.size()>0){
            // 请求来源 0:pc端  1:app端
            String reqFrom = addPresetReq.getReqFrom();
            if( reqFrom != null && Constants.ZERO.equals(reqFrom) )
            {
                return Result.fail(VideoConstants.PRESET_NAME_EXIST);
            }
            else
            {
                return Result.fail(VideoConstants.PRESET_NAME_REPEAT);
            }
        }

        videoPresetInfoService.addCameraPreset(addPresetReq,headerMap);
        return Result.success("增加预置点成功!");
    }



    @ApiOperation(value = "增加预置点", notes = "增加预置点")
    @PostMapping("/video/patrol/addCameraPresetMCP")
    public Result addCameraPresetMcp(@Validated @RequestBody AddPresetReq addPresetReq) {
        log.info("addCameraPreset" + VideoConstants.CONTROLLERREQ + addPresetReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        addPresetReq.setIndustryCode(currentUser.getIndustryCode());
        addPresetReq.setAppVerCode(currentUser.getAppVerCode());
        addPresetReq.setUserCode(currentUser.getUserId());
        addPresetReq.setTenantId(currentUser.getTenantId());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        //判断当前时间是否在该租户、用户操控时间范围内
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(user.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                addPresetReq.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.TWO)) {
            return Result.fail(NOT_AUTH_OPERATION_PRESET);
        }
        //增加预置位名称是否重复校验
        List<QueryPresetListData> presetList = videoQueryDAO.queryPresetByName(
                currentUser.getTenantId(),
                currentUser.getIndustryCode(),
                addPresetReq.getPresetName(),
                addPresetReq.getDeviceCode(),
                addPresetReq.getChannelCode()
        );
        if(presetList.size()>0){
            // 请求来源 0:pc端  1:app端
            String reqFrom = addPresetReq.getReqFrom();
            if( reqFrom != null && Constants.ZERO.equals(reqFrom) )
            {
                return Result.fail(VideoConstants.PRESET_NAME_EXIST);
            }
            else
            {
                return Result.fail(VideoConstants.PRESET_NAME_REPEAT);
            }
        }

        long l = videoPresetInfoService.addCameraPreset(addPresetReq, headerMap);
        return Result.data(l);
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
    @PostMapping("/video/patrol/delCameraPreset")
    public Result<String> delCameraPreset(@Validated @RequestBody DelPresetReq presetDTO) {
        log.info("delCameraPreset" + VideoConstants.CONTROLLERREQ + presetDTO);
        //判断当前时间是否在该租户、用户操控时间范围内
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(user.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                presetDTO.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.TWO)) {
            return Result.fail(NOT_AUTH_OPERATION_PRESET);
        }
        videoPresetInfoService.delCameraPreset(presetDTO);

        //更新预置位配准信息
        videoPresetInfoService.updateDeviceRegistration(
                presetDTO.getDeviceCode(), presetDTO.getChannelCode(), presetDTO.getPresetId(), Constants.TWO);
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
    @PostMapping("/video/patrol/batchDelCameraPreset")
    public Result<String> batchDelCameraPreset(@Validated @RequestBody BatchDelPresetReq presetDTO) {
        log.info("batchDelCameraPreset" + VideoConstants.CONTROLLERREQ + presetDTO);
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
    @PostMapping("/video/patrol/updatePresetInfo")
    public Result<String> updatePresetInfo(@Validated @RequestBody ModPresetReq modPresetReq) {
        log.info("updatePresetInfo" + VideoConstants.CONTROLLERREQ + modPresetReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        modPresetReq.setIndustryCode(currentUser.getIndustryCode());
        modPresetReq.setAppVerCode(currentUser.getAppVerCode());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        //判断当前时间是否在该租户、用户操控时间范围内
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(user.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                modPresetReq.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.TWO)) {
            return Result.fail(NOT_AUTH_OPERATION_PRESET);
        }
        // 校验预置位名称是否重复
        if( videoQueryDAO.queryPresetNameCnt(modPresetReq) > VideoConstants.ZERO )
        {
            // 请求来源 0:pc端  1:app端
            String reqFrom = modPresetReq.getReqFrom();
            if( reqFrom != null && Constants.ZERO.equals(reqFrom) )
            {
                return Result.fail(VideoConstants.PRESET_NAME_EXIST);
            }
            else
            {
                return Result.fail(VideoConstants.PRESET_NAME_REPEAT);
            }
        }

        videoPresetInfoService.updatePresetInfo(modPresetReq,headerMap);

        //更新预置位配准信息
        videoPresetInfoService.updateDeviceRegistration(
                modPresetReq.getDeviceCode(), modPresetReq.getChannelCode(), modPresetReq.getPresetId(), Constants.ONE);

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
    @PostMapping("/video/patrol/queryOrderPresetInfo")
    public Result<List<PresetDTO>> queryOrderPresetInfo(@RequestBody QueryOrderDeviceSetDTO presetDTO) {
        log.info("queryOrderPresetInfo" + VideoConstants.CONTROLLERREQ + presetDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        presetDTO.setIndustryCode(currentUser.getIndustryCode());
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
    @PostMapping("/video/algorithm/getPresetImg")
    Result<PresetVO> getPresetImg(@RequestBody PresetDTO presetDTO) {
        log.info("getPresetImg" + VideoConstants.CONTROLLERREQ + presetDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        presetDTO.setTenantId(currentUser.getTenantId());
        PresetVO presetVO = videoPresetInfoService.getPresetImg(presetDTO,headerMap);
        if (presetVO.getIsPlot() == 1) {
            return Result.data(HttpStatus.NO_CONTENT, presetVO, "您无需标绘，可直接点击暂存按钮完成通道算法配置后保存。如需进行标绘，请选择“实时标绘”后进行。");
        }
        if (StringUtils.isBlank(presetVO.getImgUrl())) {
            return Result.fail(DeviceConstants.WARN_MESS2);
        }
        //add by liuxt video-file改造
        presetVO.setImgUrl(fileUrlEncryptUtil.dealObsFileUrl(presetVO.getImgUrl(),true,null));
        return Result.data(presetVO);
    }

    @PostMapping("/video/algorithm/snapShot")
    Result snapShot(@RequestBody PresetDTO presetDTO) {
        log.info("snapShot" + VideoConstants.CONTROLLERREQ + presetDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        return videoPresetInfoService.snapShot(presetDTO,headerMap);
    }

    @PostMapping("/video/algorithm/snapShotSample")
    Result snapShotForSample(@RequestParam("list") List<PlayerSnapShotReq> list) {
        log.info("snapShotForSample" + VideoConstants.CONTROLLERREQ + list);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getTenantId());
        for (PlayerSnapShotReq x:list){
            videoPresetInfoService.snapShotForSample(x,headerMap);
        }
        return Result.success("极简抓图成功");
    }

    /**
     * 上传预置位图片 迁移
     * @param: data
     * @return: Result<Map>
     * @author: zheng.yan
     * @date: 2022/3/18
     * @version:
     */
    @PostMapping(value = "/upload/presetImg",consumes = "application/json")
    public Result<Map<String, String>> presetImg(@RequestBody SnapshotBackVO snapshotBackVO) {
        log.info("上传预置位图片请求参数:" + JSONObject.toJSON(snapshotBackVO));
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String id = request.getParameter("presetId");
        String snapshotTimeJob = null;
        if( id != null && id.contains(VideoConstants.COMMA) )
        {
            String[] paramAry = id.split(VideoConstants.COMMA);
            id = paramAry[VideoConstants.ZERO];
            snapshotTimeJob = paramAry[VideoConstants.ONE];
        }
        log.info("=========上传预置位图片请求参数:id:" + id + VideoConstants.SNAPSHOT_TIME_JOB + snapshotTimeJob);
        if (!DeviceConstants.ZERO.equals(snapshotBackVO.getError_code())) {
            log.info("========上传预置位图片失败error_code!=0");
            return Result.fail("上传失败");
        }
        Map<String, String> uploadResult = new HashMap<>(1);
        try {
            //0-私有化  1-云化 2-下沉节点
            boolean correctLocationFlag = true;
            AjaxResult ajaxResult = systemFeignClient.getConfValue("dependence_manage");
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                    correctLocationFlag = false;
                }
            }
            log.info("correctLocationFlag=" + correctLocationFlag);
            //图片url拼接接入节点
            String picUrl = this.getPathUrl(snapshotBackVO.getAccessNode(),snapshotBackVO.getChannelCode(), snapshotBackVO.getImagePath(),correctLocationFlag);
            VideoPresetDO uptPreset = new VideoPresetDO();
            uptPreset.setId(Long.valueOf(id));
            uptPreset.setImgUrl(picUrl);
            videoPresetRepository.updateById(uptPreset);

            VideoPresetDO videoPresetDO = videoPresetInfoService.selectById(Long.valueOf(id));
            // 定时任务抓图，图片保存到mongo中
            if( StringUtils.isNotEmpty(snapshotTimeJob) )
            {
                List<VmVideoPresetSnapshotMongo> insertList = new ArrayList<>();

                String taskId = "";
                String isAlgorithmIdentify = videoPresetDO.getIsAlgorithmIdentify();
                VmVideoPresetSnapshotMongo presetSnapshotMongo = new VmVideoPresetSnapshotMongo();
                presetSnapshotMongo.setPresetId(videoPresetDO.getPresetId().intValue());
                presetSnapshotMongo.setImgUrl(picUrl);
                presetSnapshotMongo.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                presetSnapshotMongo.setCreateTime(new Date());
                presetSnapshotMongo.setSnapshotTime(new Date(Long.parseLong(snapshotTimeJob)));
                presetSnapshotMongo.setDeviceCode(videoPresetDO.getDeviceCode());
                presetSnapshotMongo.setChannelCode(videoPresetDO.getChannelCode());
                presetSnapshotMongo.setTenantId(videoPresetDO.getTenantId());
                presetSnapshotMongo.setIndustryCode(videoPresetDO.getIndustryCode());
                //开启了算法识别任务，保存算法编码、任务编码
                if(Constants.ONE.equals(isAlgorithmIdentify)){
                    taskId = IdUtils.fastSimpleUUID();
                    presetSnapshotMongo.setTaskId(taskId);
                    presetSnapshotMongo.setAlgorithmCode(videoPresetDO.getAlgorithmCode());
                }
                insertList.add(presetSnapshotMongo);

                List<SnapshotBackAssistVO> assistPic = snapshotBackVO.getAssistPic();
                if(!CollectionUtils.isEmpty(assistPic)){
                    for (SnapshotBackAssistVO snapshotBackAssistVO : assistPic) {
                        VmVideoPresetSnapshotMongo vo = new VmVideoPresetSnapshotMongo();
                        BeanUtils.copyProperties(presetSnapshotMongo, vo);
                        vo.setImgUrl(this.getPathUrl(snapshotBackVO.getAccessNode(),snapshotBackAssistVO.getChannelCode(), snapshotBackAssistVO.getImagePath(),correctLocationFlag));
                        vo.setChannelCode(snapshotBackAssistVO.getChannelCode());
                        vo.setTaskId(null);
                        vo.setAlgorithmCode(null);
                        insertList.add(vo);
                    }
                }
                presetSnapshotMongoDAO.insertAll(insertList, "vm_video_preset_snapshot", MongoDBType.PRIMARY);

                //开启了算法识别任务，调未传sdk，同步给算法仓
                if(Constants.ONE.equals(isAlgorithmIdentify)){
                    //构造入参
                    String newId = presetSnapshotMongo.getId();
                    List<SecondRecogSendVO> list = this.buildReq(videoPresetDO, taskId, picUrl, newId);
                    //调用微创sdk
                    if(!CollectionUtils.isEmpty(list)){
                        // 先校验摄像机与算法是否仍存在有效的订购关系（vm_order_devices + vm_order_algorithms，同一订单套餐下），存在才下发算法仓
                        int relCnt = videoPresetMapper.countValidOrderAlgorithmRelation(
                                videoPresetDO.getTenantId(), videoPresetDO.getIndustryCode(),
                                videoPresetDO.getDeviceCode(), videoPresetDO.getAlgorithmCode());
                        if (relCnt > 0) {
                            log.info("publicSynAnalysisTask请求报文：{}", JSON.toJSON(list));
                            Result result = iTradeWarningOrderService.publicSynAnalysisTask(list);
                            log.info("publicSynAnalysisTask返回报文：{}", JSON.toJSON(result));
                        } else {
                            log.info("摄像机{}与算法{}订购关系不存在，跳过publicSynAnalysisTask",
                                    videoPresetDO.getDeviceCode(), videoPresetDO.getAlgorithmCode());
                        }
                    }
                } else {
                    log.info("摄像机{}与算法{}订购关系不存在，跳过publicSynAnalysisTask",
                            videoPresetDO.getDeviceCode(), videoPresetDO.getAlgorithmCode());
                }
            }

            //更新算法配置表图片
            uptPreset.setDeviceCode(videoPresetDO.getDeviceCode());
            uptPreset.setPresetId(videoPresetDO.getPresetId());
            videoPresetMapper.updateAlgorithmImg(uptPreset);

            uploadResult.put("fileUrl", picUrl);
        } catch (Exception e) {
            log.error("========上传预置位图片失败", e);
            return Result.fail("上传失败");

        }
        log.info("========上传预置位图片返回报文：uploadResult:" + uploadResult);
        return Result.data(uploadResult);
    }

    private String getPathUrl(String accessNode, String channelCode, String imagePath, boolean correctLocationFlag) {
        String picUrl = AssembleUrlUtil.getAssembleUrl(accessNode, imagePath, correctLocationFlag);
        log.info("channelCode={},picUrl={}" ,channelCode, picUrl);
        return picUrl;
    }

    private List<SecondRecogSendVO> buildReq(VideoPresetDO videoPresetDO, String taskId, String picUrl, String newId){
        List<SecondRecogSendVO> resultList =  new ArrayList<>();
        String deviceCode = videoPresetDO.getDeviceCode();
        DeviceInfoVO deviceInfo = videoPresetMapper.getDeviceInfo(deviceCode);

        // 查询条件
        Page page = new Page(0, 5);
        CustomQueryEntity query = new CustomQueryEntity(VmVideoPresetSnapshotMongo.class);
        query.eq(VideoConstants.DEVICECODE,videoPresetDO.getDeviceCode());
        query.eq(VideoConstants.PRESET_ID_FIELD, videoPresetDO.getPresetId());
        query.eq(VideoConstants.TENANTID,videoPresetDO.getTenantId());
        query.eq(VideoConstants.INDUSTRYCODE,videoPresetDO.getIndustryCode());
        query.eq(VideoConstants.DELETE_FLAG_FIELD,DeviceConstants.DELETE_FLAG_NORMAL_INT);
        query.ne(VideoConstants.TASK_ID,null);

        // 排序
        SortEntity sortEntity = new SortEntity();
        sortEntity.desc(VideoConstants.SNAPSHOT_TIME_FIELD);
        page.setSortEntity(sortEntity);
        Page<VmVideoPresetSnapshotMongo> pageInfo = presetSnapshotMongoDAO.findPage(query, page, null, MongoDBType.PRIMARY);
        List<VmVideoPresetSnapshotMongo> list = pageInfo.getList();
        VmVideoPresetSnapshotMongo vo = new VmVideoPresetSnapshotMongo();
        if(!CollectionUtils.isEmpty(list)){
            if(list.size() > 1){
                vo = list.get(Constants.ONE_NUM);
            } else {
                return new ArrayList<>();
            }
        }

        SecondRecogSendVO alarmTaskInfo = new SecondRecogSendVO();
        alarmTaskInfo.setMakeAlarmFlag(Constants.ONE);
        alarmTaskInfo.setTaskId(taskId);
        alarmTaskInfo.setTaskType(Constants.THREE);
        alarmTaskInfo.setAlgorithmCode(videoPresetDO.getAlgorithmCode());
        alarmTaskInfo.setLongitude(deviceInfo.getLongitude());
        alarmTaskInfo.setLatitude(deviceInfo.getLatitude());
        alarmTaskInfo.setAccessNode(deviceInfo.getAccessNode());
        alarmTaskInfo.setSelectAlgorithm(videoPresetDO.getAlgorithmCode());
        alarmTaskInfo.setReferenceId(vo.getId());
        alarmTaskInfo.setTenantId(videoPresetDO.getTenantId());
        alarmTaskInfo.setIndustryCode(videoPresetDO.getIndustryCode());
        alarmTaskInfo.setAppVerCode(videoPresetDO.getAppVerCode());

        if(!ObjectUtils.isEmpty(videoPresetDO.getPan()) &&
                !ObjectUtils.isEmpty(videoPresetDO.getTilt()) &&
                !ObjectUtils.isEmpty(videoPresetDO.getZoom())){
            TranslnglatPeopleDTO translnglatPeopleDTO = new TranslnglatPeopleDTO();
            translnglatPeopleDTO.setPan(String.valueOf(videoPresetDO.getPan()));
            translnglatPeopleDTO.setTilt(String.valueOf(videoPresetDO.getTilt()));
            translnglatPeopleDTO.setZoom(String.valueOf(videoPresetDO.getZoom()));
            translnglatPeopleDTO.setPresetId(String.valueOf(videoPresetDO.getPresetId()));
            alarmTaskInfo.setTranslnglatPeopleDTO(translnglatPeopleDTO);
        }

        SecondRecogFileVO fileInfo = new SecondRecogFileVO();
        fileInfo.setType("image");
        fileInfo.setImgUrls(picUrl);
        fileInfo.setBaselinePicUrl(vo.getImgUrl());
        alarmTaskInfo.setFileInfo(fileInfo);

        SecondExtraFieldInfoVo extraFieldInfo = new SecondExtraFieldInfoVo();
        extraFieldInfo.setDeviceCode(videoPresetDO.getDeviceCode());
        extraFieldInfo.setChannelCode(videoPresetDO.getChannelCode());
        extraFieldInfo.setPicId(newId);
        alarmTaskInfo.setExtraFieldInfo(extraFieldInfo);
        resultList.add(alarmTaskInfo);
        return resultList;
    }

    public static void main(String[] args) {
        System.out.println(IdUtils.fastSimpleUUID());
    }


    @PostMapping(value = "/upload/imgCallback",consumes = "application/json")
    public Result<Map<String, String>> imgCallback(@RequestBody SnapshotBackVO snapshotBackVO) {
        log.info("presetImg 告警抓图回调,{}", JSON.toJSONString(snapshotBackVO));
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String batchId = request.getParameter("batchId");
        Map<String, String> uploadResult = new HashMap<>(1);
        try {
            //0-私有化  1-云化 2-下沉节点
            boolean correctLocationFlag = true;
            AjaxResult ajaxResult = systemFeignClient.getConfValue("dependence_manage");
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                    correctLocationFlag = false;
                }
            }
            log.info("correctLocationFlag=" + correctLocationFlag);
            //上传图片
            String picUrl = AssembleUrlUtil.getAssembleUrl(snapshotBackVO.getAccessNode(),snapshotBackVO.getImagePath(), correctLocationFlag);
            log.info("拼接节点后:{}", JSON.toJSONString(picUrl));
            List<String> list = Collections.singletonList(picUrl);
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(list);
            String encUrl = stringStringMap.get(picUrl);
            log.info("加密后图片:{}", JSON.toJSONString(encUrl));
            uploadResult.put("fileUrl", encUrl);
            uploadResult.put("originUrl", picUrl);
            //保存到redis 保存30s让App可查询 反显
            RedisUtil.set("app_alarm_img_batch_id_" + batchId, uploadResult, 30 , TimeUnit.SECONDS, RedisDBType.PRIMARY);
        } catch (Exception e) {
            log.info("========上传预置位图片失败", e);
            return Result.fail("上传失败");

        }
        log.info("========上传预置位图片返回报文：uploadResult:" + uploadResult);
        return Result.data(uploadResult);
    }

    /** 应急部摄像机抓图回调入口。 */
    @PostMapping(value = "/upload/presetImgForYJB", consumes = "application/json")
    public Result<Map<String, String>> presetImgForYJB(@RequestBody SnapshotBackVO snapshotBackVO) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uniqueId = attributes == null ? null : attributes.getRequest().getParameter("uniqueId");
        return yjbSnapshotCallbackService.callback(uniqueId, snapshotBackVO);
    }

    @PostMapping(value = "/video/patrol/selectPTZByIdList")
    public List<AlgorithmParamPresetVO>  selectPTZByIdList(@RequestBody List<AlgorithmConfigVO> pos) {
        log.info("selectPTZByIdList" + VideoConstants.CONTROLLERREQ + pos);
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        return videoPresetInfoService.selectPTZByIdList(pos,industryCode);
    }

    @PostMapping(value = "/video/patrol/selectById")
    public VideoPresetDO selectById(@RequestBody VideoPresetDO videoPresetDO) {
        log.info("selectById" + VideoConstants.SDKREQ + JSON.toJSONString(videoPresetDO));
        return videoPresetInfoService.selectByIdAndDeviceCode(videoPresetDO.getPresetId(),videoPresetDO.getDeviceCode());
    }

    @ApiOperation(value = "查询最大默认名")
    @PostMapping("/video/patrol/getPresetName")
    public Result<String> getPresetName(@RequestBody AddPresetReq addPresetReq) {
        return videoPresetInfoService.getPresetName(addPresetReq);
    }

    /**
     * 功能描述: 预置位抓图设置
     *
     * @param: modPresetSnapshotReq
     * @return: com.chinatower.common.entity.Result
     * @auther: wanglei
     * @date: 2023/6/7
     * @version: 1.5
     */
    @ApiOperation(value = "预置位抓图设置", notes = "预置位抓图设置")
    @PostMapping("/video/patrol/updatePresetSnapshotInfo")
    public Result<String> updatePresetSnapshotInfo(@Validated @RequestBody ModPresetSnapshotReq modPresetSnapshotReq) {
        log.info(VideoConstants.UPDATE_PRESET_SNAPSHOT_INFO + VideoConstants.CONTROLLERREQ + modPresetSnapshotReq);
        videoPresetInfoService.updatePresetSnapshotInfo(modPresetSnapshotReq);
        return Result.success(VideoConstants.UPDATE_PRESET_SNAPSHOT_INFO_SUCC);
    }

    /**
     * 功能描述: 编辑预置位名称
     *
     * @param: modPresetReq
     * @return: com.chinatower.common.entity.Result
     * @auther: wanglei
     * @date: 2023/6/7
     * @version: 1.5
     */
    @ApiOperation(value = "编辑预置位名称", notes = "编辑预置位名称")
    @PostMapping("/video/patrol/updatePresetName")
    public Result<String> updatePresetName(@Validated @RequestBody ModPresetReq modPresetReq) {
        log.info(VideoConstants.UPDATE_PRESET_NAME + VideoConstants.CONTROLLERREQ + modPresetReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        modPresetReq.setTenantId(currentUser.getTenantId());
        modPresetReq.setIndustryCode(currentUser.getIndustryCode());
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(currentUser.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                modPresetReq.getDeviceCode(),
                currentUser.getTenantId(),
                currentUser.getIndustryCode(),
                userId,
                Constants.TWO)) {
            return Result.fail(NOT_AUTH_OPERATION_PRESET);
        }
        Result<String> result = videoPresetInfoService.updatePresetName(modPresetReq);
        return result;
    }

    /**
     * 功能描述: 删除预置位校验
     *
     * @param: presetDTO
     * @return: Result<DelCameraPresetCheck>
     * @auther: wanglei
     * @date: 2023/6/8
     * @version: 1.5
     */
    @ApiOperation(value = "删除预置位校验", notes = "删除预置位校验")
    @PostMapping("/video/patrol/delCameraPresetCheck")
    public Result<DelCameraPresetCheck> delCameraPresetCheck(@Validated @RequestBody DelPresetReq presetDTO) {
        log.info(VideoConstants.DEL_CAMERA_PRESET_CHECK + VideoConstants.CONTROLLERREQ + presetDTO);
        DelCameraPresetCheck delCameraPresetCheck = videoPresetInfoService.delCameraPresetCheck(presetDTO);
        return Result.data(delCameraPresetCheck);
    }

    /**
     * 功能描述: 查询预置位详情
     *
     * @param: presetId
     * @return: Result<VideoPresetDO>
     * @auther: wanglei
     * @date: 2023/6/9
     * @version: 1.5
     */
    @PostMapping(value = "/video/patrol/getPresetDetail/{presetId}/{deviceCode}")
    public Result<VideoPresetDO> getPresetDetail(@PathVariable("presetId") Long presetId,@PathVariable("deviceCode") String deviceCode) {
        log.info(VideoConstants.GET_PRESET_DETAIL + VideoConstants.CONTROLLERREQ + presetId + VideoConstants.COMMA + deviceCode);
        VideoPresetDO videoPresetDO = videoPresetInfoService.selectByIdAndDeviceCode(presetId,deviceCode);
        videoPresetDO.setImgUrl(fileUrlEncryptUtil.dealObsFileUrl(videoPresetDO.getImgUrl(),true,null));
        return Result.data(videoPresetDO);
    }

    /**
     * 功能描述: 分页查询预置位定时抓的图片
     *
     * @param: presetImgJobDTO
     * @auther: wanglei
     * @date: 2023/6/11
     * @version: 1.5
     */
    @ApiOperation(value = "分页查询预置位定时抓的图片", notes = "分页查询预置位定时抓的图片")
    @PostMapping("/video/patrol/queryPresetImgJob")
    public Result<Page<VmVideoPresetSnapshotMongo>> queryPresetImgJob(@RequestBody PresetImgJobDTO presetImgJobDTO)
    {
        log.info(VideoConstants.QUERY_PRESET_IMG_JOB + VideoConstants.CONTROLLERREQ + presetImgJobDTO);
        Page<VmVideoPresetSnapshotMongo> pageInfo = videoPresetInfoService.queryPresetImgJob(presetImgJobDTO);
        return Result.data(pageInfo);
    }

    /**
     * 功能描述: 预置位定时抓图
     *
     * @param: param
     * @return: ReturnT<String>
     * @auther: wanglei
     * @date: 2023/6/10
     * @version: 1.5
     */
    @XxlJob("presetSnapshotJob")
    @PostMapping("/video/patrol/presetSnapshotJob")
    public ReturnT<String> presetSnapshotJob(String param) {
        return videoPresetInfoService.presetSnapshotJob(param);
    }


    /**
     * 查询预置位定时抓图设置列表
     * @param snapshotVO
     * @return
     */
    @PostMapping("/video/patrol/queryPresetSnapshotList")
    Result<PageInfo<VideoPresetSnapshotVO>> queryPresetSnapshotList(@RequestBody VideoPresetSnapshotVO snapshotVO){
        return Result.data(videoPresetInfoService.queryPresetSnapshotList(snapshotVO));
    }


    /**
     * 批量保存预置位定时抓图设置
     * @param presetSnapshotList
     * @return
     */
    @PostMapping("/video/patrol/insertPresetSnapshotList")
    public Result<String> insertPresetSnapshotList(@RequestBody List<VideoPresetSnapshotVO> presetSnapshotList) {
        videoPresetInfoService.insertPresetSnapshotList(presetSnapshotList);
        return Result.success(VideoConstants.UPDATE_PRESET_SNAPSHOT_INFO_SUCC);
    }

    /**
     * 批量更新预置位定时抓图设置
     * @param presetSnapshotList
     * @return
     */
    @PostMapping("/video/patrol/updatePresetSnapshotList")
    public Result<String> updatePresetSnapshotList(@RequestBody List<VideoPresetSnapshotVO> presetSnapshotList) {
        videoPresetInfoService.updatePresetSnapshotList(presetSnapshotList);
        return Result.success(VideoConstants.UPDATE_PRESET_SNAPSHOT_INFO_SUCC);
    }

    /**
     * 删除预置位定时抓图设置
     * @param videoPresetSnapshotVO
     * @return
     */
    @PostMapping("/video/patrol/deletePresetSnapshot")
    public Result<String> deletePresetSnapshot(@RequestBody VideoPresetSnapshotVO videoPresetSnapshotVO) {
        videoPresetInfoService.deletePresetSnapshot(videoPresetSnapshotVO);
        return Result.success(DEL_PRESET_SNAPSHOT_INFO_SUCCESS);
    }

    @PostMapping("/video/patrol/deletePresetSnapshotList")
    public Result<String> deletePresetSnapshotList(@RequestBody VideoPresetSnapshotVO videoPresetSnapshotVO) {
        videoPresetInfoService.deletePresetSnapshotList(videoPresetSnapshotVO);
        return Result.success(DEL_PRESET_SNAPSHOT_INFO_SUCCESS);
    }

    /**
     * 检查预置位抓图设置
     * @param videoPresetSnapshotVO
     * @return
     */
    @PostMapping("/video/patrol/checkPresetSnapshot")
    public Result<String> checkPresetSnapshot(@RequestBody VideoPresetSnapshotVO videoPresetSnapshotVO) {
        return videoPresetInfoService.checkPresetSnapshot(videoPresetSnapshotVO);
    }

    /**
     * 查询用户订购的ai告警
     * @return
     */
    @PostMapping("/video/alarm/getAiAlarmType")
    public Result<List<AlarmTypeDO>> getAiAlarmType() {
        return videoPresetInfoService.getAiAlarmType();
    }

    /**
     * 单次识别产生告警，更新预置位抓图mongo表
     * @return
     */
    @PostMapping("/video/preset/updateWarningOrderId")
    public Result<String> updateWarningOrderId(@RequestBody PresetImgVO presetImgVO) {
        log.info("updateWarningOrderId入参：" + JSON.toJSONString(presetImgVO));
        return Result.success(videoPresetInfoService.updateWarningOrderId(presetImgVO));
    }
}
