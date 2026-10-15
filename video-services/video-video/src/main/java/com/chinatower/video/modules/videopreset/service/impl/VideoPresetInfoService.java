package com.chinatower.video.modules.videopreset.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IAlgorithmConfigService;
import com.chinatower.device.entity.dto.ExtendJson;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.order.api.IVideoOrderService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.order.VideoOrderReq;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.annotation.DeviceOrdAuthScope;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.util.ApiInfoUtils;
import com.chinatower.video.modules.common.util.InterfaceLogUtil;
import com.chinatower.video.modules.player.service.VideoPlayerService;
import com.chinatower.video.modules.query.dao.PresetListSnapshotMongoDAO;
import com.chinatower.video.modules.query.dao.PresetSnapshotMongoDAO;
import com.chinatower.video.modules.query.dao.SecondRecogTaskInfoMongoDAO;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.query.service.VideoQueryService;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;
import com.chinatower.video.modules.videopatrolplan.mapper.PatrolInfoMapper;
import com.chinatower.video.modules.videopreset.dataobject.CustomQueryEntity;
import com.chinatower.video.modules.videopreset.dataobject.SecondRecogTaskInfoMongo;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetSnapshotVO;
import com.chinatower.video.modules.videopreset.mapper.VideoPresetMapper;
import com.chinatower.video.modules.videopreset.repository.IVideoPresetRepository;
import com.chinatower.video.modules.videopreset.service.IVideoPresetInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.core.biz.model.ReturnT;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName VideoPresetService
 * @Description
 * @Author Chensw
 * @Date 2022/1/24
 **/
@Service
@Slf4j
public class VideoPresetInfoService implements IVideoPresetInfoService {

    @Resource
    IVideoOrderService videoOrderService;
    @Resource
    IVideoPresetRepository videoPresetRepository;
    @Resource
    PatrolInfoMapper patrolInfoMapper;
    @Resource
    VideoPlayerService videoPlayerService;
    @Resource
    VideoQueryService videoQueryService;
    @Resource
    VideoControlService videoControlService;
    @Resource
    VideoPresetMapper presetMapper;
    @Resource
    ApiInfoUtils apiInfoUtils;
    @Resource
    IAlgorithmConfigService iAlgorithmConfigService;

    @Resource
    SystemFeignClient systemFeignClient;

    @Resource
    private NetConnectUtils connectUtils;

    @Autowired
    private VideoQueryDAO videoQueryDAO;

    @Autowired
    PresetSnapshotMongoDAO presetSnapshotMongoDAO;

    @Autowired
    PresetListSnapshotMongoDAO presetListSnapshotMongoDAO;

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    ICasCadeQueryService iCasCadeQueryService;

    @Autowired
    SecondRecogTaskInfoMongoDAO secondRecogTaskInfoMongoDAO;


    public static final String WAIT_TIME = "5";

    public static final Timer timer = new Timer();

    public static final ThreadPoolExecutor poolExecutor =
            new ThreadPoolExecutor(10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public long addCameraPreset(AddPresetReq addPresetReq, Map<String, String> headerMap) {

        //属地化数据统一改造 add by liuxt 20240116
        CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(addPresetReq.getDeviceCode());
        //南向加字段 add by liuxt 20240117
        addPresetReq.setAcceptNode(casCadeVO.getPlatformCode());
        addPresetReq.setAccessNode(casCadeVO.getCascadeToCode());
        String industryCode = addPresetReq.getIndustryCode();
        String appVerCode = addPresetReq.getAppVerCode();
        String userCode = addPresetReq.getUserCode();
        Long presetId;
        PresetDTO presetDTO = new PresetDTO();
        BeanUtils.copyProperties(addPresetReq, presetDTO);
        log.info(InterfaceLogUtil.reqTransLog(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + DeviceConstants.REQUEST_MESSAGE + JSON.toJSONString(addPresetReq)));
        //调用6.15查询PTZ
        log.info("添加预置点请求参数:" + JSON.toJSONString(addPresetReq));
        VideoBaseReq videoBaseReq = new VideoBaseReq();
        videoBaseReq.setChannelCode(addPresetReq.getChannelCode());
        videoBaseReq.setDeviceCode(addPresetReq.getDeviceCode());
        Result<PresetData> result = videoQueryService.queryPtzPosition(videoBaseReq,headerMap);
        log.info(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + "添加预置点返回报文：" + result);
        if (ResultCode.SUCCESS.getCode() == result.getCode()) {
            presetDTO.setPan(result.getData().getPan());
            presetDTO.setTilt(result.getData().getTilt());
            presetDTO.setZoom(result.getData().getZoom());
        }
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(presetDTO));
        log.info(new Exception().getStackTrace()[0].getMethodName() + "请求报文:" + jsonObject);
        // 调用北向接口

        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_ADD_CAMERA_PRESET);
        apiInfoDTO.setInJsonStr(JSON.toJSONString(presetDTO));
        apiInfoDTO.setDeviceCode(presetDTO.getDeviceCode());
        apiInfoDTO.setChannelCode(presetDTO.getChannelCode());
        apiInfoDTO.setOrderCode(presetDTO.getOrderCode());
        apiInfoDTO.setTenantId(presetDTO.getTenantId());
        apiInfoDTO.setUserCode(presetDTO.getUserCode());
        apiInfoDTO.setTowerCode(addPresetReq.getTowerCode());

        Result<JSONObject> res = null;
        try {
            NetConnectUtils.RequestParamter<AddPresetReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(addPresetReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.ADD_CAMERA_PRESET);
            // 接口路径参数
            path.setDeviceCode(presetDTO.getDeviceCode());
            path.setChannelCode(presetDTO.getChannelCode());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.ADD_CAMERA_PRESET_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.ADD_CAMERA_PRESET_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.ADD_CAMERA_PRESET_ERROR +VideoConstants.EQUAL_INFO, e);
        }

        if (ResultCode.SUCCESS.getCode() == res.getCode()) {
            JSONObject data = res.getData();
            presetId = data.getJSONObject(DeviceNorthIntfConstants.DATA).getLong(DeviceNorthIntfConstants.PRESET_ID);
            presetDTO.setPresetId(presetId);

            VideoOrderReq videoOrderReq = new VideoOrderReq();
            videoOrderReq.setDeviceCode(presetDTO.getDeviceCode());
            videoOrderReq.setChannelCode(presetDTO.getChannelCode());
            videoOrderReq.setTenantId(presetDTO.getTenantId());
            videoOrderReq.setIndustryCode(industryCode);

            Result<VideoOrderResp> videoOrderInfo = videoOrderService.getVideoOrderInfo(videoOrderReq);
            if (!ObjectUtils.isEmpty(videoOrderInfo) && !ObjectUtils.isEmpty(videoOrderInfo.getData())
                    && !StringUtils.isEmpty(videoOrderInfo.getData().getOrderCode()) ) {
                presetDTO.setOrderCode(videoOrderInfo.getData().getOrderCode());
            }
            VideoPresetDO videoPresetDO = new VideoPresetDO();
            presetDTO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
            BeanUtils.copyProperties(presetDTO, videoPresetDO);
            Date currDate = new Date();
            videoPresetDO.setCreateTime(currDate);
            videoPresetDO.setUpdateTime(currDate);
            videoPresetDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
            videoPresetDO.setIndustryCode(industryCode);
            videoPresetDO.setAppVerCode(appVerCode);
            videoPresetDO.setUserCode(userCode);
            videoPresetDO.setCreator(SecurityUtils.getOperator());
            videoPresetRepository.save(videoPresetDO);
        } else {
            log.info(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + "调用北向接口增加预置点失败");
            throw new CustomException(res.getMsg(), HttpStatus.ERROR_4001);
        }
        //调用4.56.抓图
        snapShot(presetDTO,headerMap);
        return presetId;
    }

    @Override
    @ApiOperation("删除预置点")
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public void delCameraPreset(DelPresetReq presetDTO) {
        log.info(InterfaceLogUtil.reqTransLog(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + DeviceConstants.REQUEST_MESSAGE + JSON.toJSONString(presetDTO)));
        //add by liuxt 20211014 start PC端视频管理-预置位功能优化

        //判断该预置位上是否配置了算法
        com.chinatower.platform.entity.device.DelPresetReq dto = new com.chinatower.platform.entity.device.DelPresetReq();
        BeanUtil.copyProperties(presetDTO, dto);
        int algorithmConfigCount = iAlgorithmConfigService.getAlgorithmConfigCount(dto);
        //判断该预置位上是否配置了巡航计划
        int patrolCount = patrolInfoMapper.getPatrolCount(presetDTO.getPresetId(), presetDTO.getDeviceCode());
        //查询该预置位是否绑定图斑与电子围栏
        int polyCount = patrolInfoMapper.isExistByPresetId(presetDTO.getPresetId(), presetDTO.getDeviceCode());
        //查询该预置位是否设置ar标签  >0 设置ar标签  ==0 未设置
        int arCount = patrolInfoMapper.isExistARByPresetId(presetDTO.getPresetId(), presetDTO.getDeviceCode());


        StringBuffer presetStr = new StringBuffer();
        if (algorithmConfigCount > VideoConstants.ZERO) {
            presetStr.append(VideoConstants.ALGO_TASK);
        }
        if (patrolCount > VideoConstants.ZERO) {
            presetStr.append(VideoConstants.PATROL_INFO);
        }
        if (arCount > VideoConstants.ZERO) {
            presetStr.append(VideoConstants.AR_TAG);
        }
        if (polyCount > VideoConstants.ZERO) {
            presetStr.append(VideoConstants.POLY_PATCH);
        }
        if (StringUtils.isNotEmpty(presetStr)) {
            String presetS = presetStr.toString();
            if(presetS.startsWith(VideoConstants.CAESURA_SIGN)) {
                presetS = presetS.substring(VideoConstants.ONE);
            }
            throw new CustomException(VideoConstants.THE_PRESET_SET + presetS + VideoConstants.NOT_DEL + presetS + VideoConstants.DEL_TRY);
        }  else {
            VideoPresetDO presetDO = new VideoPresetDO();
            presetDO.setPresetId(presetDTO.getPresetId());
            presetDO.setDeviceCode(presetDTO.getDeviceCode());
            videoPresetRepository.removeVideoPresetByCompositeKeys(presetDO);
            // 删除预置位的历史抓图
            Query query = Query.query(Criteria.where(VideoConstants.PRESET_ID_FIELD).is(presetDTO.getPresetId()).and("deviceCode").is(presetDTO.getDeviceCode()));
            mongoTemplate.remove(query, VideoConstants.VM_VIDEO_PRESET_SNAPSHOT);
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(presetDTO.getDeviceCode());
            // 调用北向接口
            ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
            apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_DEL_CAMERA_PRESET);
            apiInfoDTO.setInJsonStr(JSON.toJSONString(presetDTO));
            apiInfoDTO.setDeviceCode(presetDTO.getDeviceCode());
            apiInfoDTO.setChannelCode(presetDTO.getChannelCode());
            apiInfoDTO.setOrderCode(presetDTO.getOrderCode());
            apiInfoDTO.setTenantId(presetDTO.getTenantId());
            apiInfoDTO.setUserCode(presetDTO.getUserCode());
            apiInfoDTO.setTowerCode(presetDTO.getTowerCode());
            //属地化数据统一改造 add by ZLQ 20240118
            apiInfoDTO.setAcceptNode(casCadeVO.getPlatformCode());
            apiInfoDTO.setAccessNode(casCadeVO.getCascadeToCode());

            Result<JSONObject> res = null;
            try {
                NetConnectUtils.RequestParamter<ApiInfoDTO> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(apiInfoDTO);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DEL_CAMERA_PRESET);
                // 接口路径参数
                path.setDeviceCode(presetDTO.getDeviceCode());
                path.setChannelCode(presetDTO.getChannelCode());
                path.setPresetId(presetDTO.getPresetId());
                paramter.setPath(path);

                // 调用统一网关接口
                log.info(VideoConstants.DEL_CAMERA_PRESET_OP + VideoConstants.REQUEST_LOG + paramter);
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.DEL_CAMERA_PRESET_OP + VideoConstants.RESPONSE_LOG + cr);
                Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
                res = apiInfoUtils.transResult(cr, respDictData.getData());
            }catch (Exception e){
                log.error(VideoConstants.DEL_CAMERA_PRESET_ERROR +VideoConstants.EQUAL_INFO, e);
            }
            if (ResultCode.SUCCESS.getCode() != res.getCode()) {
                throw new CustomException(res.getMsg(), HttpStatus.ERROR_4001);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public void batchDelCameraPreset(BatchDelPresetReq presetDTO) {
        boolean remove = videoPresetRepository.removeBatchVideoPresetByCompositeKeys(presetDTO);
        if (remove) {
            // 调用北向接口
            ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
            apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_BATCH_DEL_CAMERA_PRESET);
            apiInfoDTO.setInJsonStr(JSON.toJSONString(presetDTO));
            apiInfoDTO.setDeviceCode(presetDTO.getDeviceCode());
            apiInfoDTO.setChannelCode(presetDTO.getChannelCode());
            apiInfoDTO.setOrderCode(presetDTO.getOrderCode());
            apiInfoDTO.setTenantId(presetDTO.getTenantId());
            apiInfoDTO.setUserCode(presetDTO.getUserCode());
            apiInfoDTO.setTowerCode(presetDTO.getTowerCode());

            Result<JSONObject> res = null;
            try {
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(presetDTO.getDeviceCode());
                //属地化数据统一改造 add by ZLQ 20240226
                presetDTO.setAcceptNode(casCadeVO.getPlatformCode());
                presetDTO.setAccessNode(casCadeVO.getCascadeToCode());

                NetConnectUtils.RequestParamter<BatchDelPresetReq> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(presetDTO);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.BATCH_DEL_CAMERA_PRESET);
                // 接口路径参数
                paramter.setPath(path);
                // 调用统一网关接口
                log.info(VideoConstants.BATCH_DEL_CAMERA_PRESET_OP + VideoConstants.REQUEST_LOG + paramter);
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.BATCH_DEL_CAMERA_PRESET_OP + VideoConstants.RESPONSE_LOG + cr);
                Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
                res = apiInfoUtils.transResult(cr, respDictData.getData());
            }catch (Exception e){
                log.error(VideoConstants.BATCH_DEL_CAMERA_PRESET_ERROR +VideoConstants.EQUAL_INFO, e);
            }
            if (ResultCode.SUCCESS.getCode() != res.getCode()) {
                throw new CustomException(res.getMsg(), HttpStatus.ERROR_4001);
            }
        } else {
            throw new CustomException("批量删除预置点失败！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public void updatePresetInfo(ModPresetReq modPresetReq,Map<String, String> headerMap) {
        //属地化数据统一改造 add by liuxt 20240116
        CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(modPresetReq.getDeviceCode());
        //南向加字段 add by liuxt 20240117
        modPresetReq.setAcceptNode(casCadeVO.getPlatformCode());
        modPresetReq.setAccessNode(casCadeVO.getCascadeToCode());
        String industryCode = modPresetReq.getIndustryCode();
        String appVerCode = modPresetReq.getAppVerCode();
        if (modPresetReq.getPresetId() == null) {
            throw new CustomException("预置点列未选择");
        }
        PresetDTO presetDTO = new PresetDTO();
        BeanUtils.copyProperties(modPresetReq, presetDTO);
        presetDTO.setUpdateTime(new Date());
        VideoPresetDO videoPresetDO = new VideoPresetDO();
        //调用6.15查询PTZ
        log.info("6.15查询PTZ请求参数:" + JSON.toJSONString(modPresetReq));
        VideoBaseReq videoBaseReq = new VideoBaseReq();
        videoBaseReq.setChannelCode(modPresetReq.getChannelCode());
        videoBaseReq.setDeviceCode(modPresetReq.getDeviceCode());
        Result ptzPosition = videoQueryService.queryPtzPosition(videoBaseReq,headerMap);
        log.info(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + "6.15查询PTZ返回报文：" + ptzPosition);
        if (ResultCode.SUCCESS.getCode() == ptzPosition.getCode()) {
            JSONObject ptz = JSON.parseObject(JSON.toJSONString(ptzPosition.getData()));
            presetDTO.setPan(Float.valueOf(ptz.get(DeviceConstants.PTZ_PAN_STR).toString()));
            presetDTO.setTilt(Float.valueOf(ptz.get(DeviceConstants.PTZ_TILT_STR).toString()));
            presetDTO.setZoom(Float.valueOf(ptz.get(DeviceConstants.PTZ_ZOOM_STR).toString()));
        }

        presetDTO.setDeleteFlag(DeviceConstants.ZERO_NUM);
        BeanUtils.copyProperties(presetDTO, videoPresetDO);
        videoPresetDO.setUpdateTime(new Date());
        videoPresetDO.setIndustryCode(industryCode);
        videoPresetDO.setAppVerCode(appVerCode);

        // 调用北向接口
        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_UPDATE_PRESET_INFO);
        apiInfoDTO.setInJsonStr(JSON.toJSONString(presetDTO));
        apiInfoDTO.setDeviceCode(presetDTO.getDeviceCode());
        apiInfoDTO.setChannelCode(presetDTO.getChannelCode());
        apiInfoDTO.setOrderCode(presetDTO.getOrderCode());
        apiInfoDTO.setTenantId(presetDTO.getTenantId());
        apiInfoDTO.setUserCode(presetDTO.getUserCode());
        apiInfoDTO.setTowerCode(modPresetReq.getTowerCode());

        Result<JSONObject> res = null;
        try {
            NetConnectUtils.RequestParamter<ModPresetReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(modPresetReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UPDATE_PRESET_INFO);
            // 接口路径参数
            path.setDeviceCode(modPresetReq.getDeviceCode());
            path.setChannelCode(modPresetReq.getChannelCode());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.UPDATE_PRESET_INFO_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.UPDATE_PRESET_INFO_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.UPDATE_PRESET_INFO_ERROR +VideoConstants.EQUAL_INFO, e);
        }
        if (ResultCode.SUCCESS.getCode() != res.getCode()) {
            throw new CustomException(res.getMsg(), HttpStatus.ERROR_4001);
        }

        videoPresetRepository.updateVideoPresetByCompositeKeys(videoPresetDO);

        //调用4.56.抓图
        snapShot(presetDTO,headerMap);
    }

    public Result snapShot(PresetDTO presetDTO) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,presetDTO.getTenantId());
        if (!StringUtils.isEmpty(presetDTO.getDoubleImgAnalysis())) {
            presetDTO.setDoubleChannelSnapshotType(Integer.parseInt(presetDTO.getDoubleImgAnalysis()));
        }
        return this.snapShot(presetDTO, headerMap);
    }
    /**
     * 抓图
     * @param presetDTO
     * @return
     */
    @Override
    public Result snapShot(@NotNull PresetDTO presetDTO,Map<String, String> headerMap) {
        Result<List<SysDictDataResponse>> dictResult = systemFeignClient.dictDataType("preset_snap_shot_back_url");
        if (dictResult.getData() == null || dictResult.getData().isEmpty()) {
            return Result.fail("未配置抓图回调地址");
        }
        //查询主键
        int id = presetMapper.getPresetId(presetDTO.getPresetId(),presetDTO.getDeviceCode());

        // 修改命名规则防止重复文件名称
        // 获取当前时间戳（毫秒）
        long timestamp = System.currentTimeMillis();
        String timestampStr = String.valueOf(timestamp);
        /**
         * 图片名称
         *  有预置位：设备编码_通道编码_预置位id_2位序号；
         *  无预置位：设备编码_通道编码_2位序号；
         * */
        String imgName = presetDTO.getDeviceCode() + presetDTO.getChannelCode() + presetDTO.getPresetId() + timestampStr;
        presetDTO.setStreamType(1);
        presetDTO.setImageUpUrl(dictResult.getData().get(0).getDictValue() + id);
        // 用来区分是否是定时任务抓图，如果值是1则是定时抓图，否则不是
        String presetSnapshotJob = presetDTO.getPresetSnapshotJob();
        if( presetSnapshotJob != null && Constants.ONE.equals(presetSnapshotJob) )
        {
            // 定时抓图，图片回传地址后面加上抓图时间参数
            presetDTO.setImageUpUrl(presetDTO.getImageUpUrl() + VideoConstants.COMMA + System.currentTimeMillis());
        }
        presetDTO.setImageName(imgName);

        PlayerSnapShotReq snapShotReq = new PlayerSnapShotReq();
        snapShotReq.setDeviceCode(presetDTO.getDeviceCode());
        snapShotReq.setChannelCode(presetDTO.getChannelCode());
        snapShotReq.setImageUpUrl(presetDTO.getImageUpUrl());
        snapShotReq.setImageName(presetDTO.getImageName());
        snapShotReq.setStreamType(presetDTO.getStreamType());
        snapShotReq.setDoubleChannelSnapshotType(presetDTO.getDoubleChannelSnapshotType());
        snapShotReq.setIsNeedOfs(Constants.ONE);
        log.info("4.56.抓图请求参数：" + JSON.toJSONString(snapShotReq));
        Result result = videoPlayerService.snapShot(snapShotReq,headerMap);
        log.info(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + "4.56.抓图返回报文：" + JSON.toJSONString(result));
        return result;
    }


    @Override
    public void snapShotForSample(PlayerSnapShotReq x,Map<String, String> headerMap) {
        Result<List<SysDictDataResponse>> dictResult = systemFeignClient.dictDataType("preset_snap_shot_back_url");
        if (dictResult.getData() == null || dictResult.getData().isEmpty()) {
            log.info("未配置抓图回调地址");
        }
        SecureRandom rd = new SecureRandom();
        int a = rd.nextInt(10);
        int b = rd.nextInt(10);
        /**
         * 图片名称
         *  无预置位：设备编码_通道编码_2位序号；
         * */
        String imgName = x.getDeviceCode() + x.getChannelCode() + a + b;
        x.setStreamType(1);
        x.setImageUpUrl(dictResult.getData().get(0).getDictValue() + SecurityUtils.getLoginUser().getUser().getUserId() + "&channelCode=" + x.getChannelCode());
        x.setImageName(imgName);
        x.setIsNeedOfs(Constants.ONE);
        log.info("极简模式抓图：" + JSON.toJSONString(x));
        Result result = videoPlayerService.snapShot(x,headerMap);
        log.info(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + "极简模式抓图：" + JSON.toJSONString(result));
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
    public List<PresetDTO> queryOrderPresetInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {
        String industryCode = queryOrderDeviceSetDTO.getIndustryCode();
        queryOrderDeviceSetDTO.setIndustryCode(industryCode);
        List<PresetDTO> presetDTOList = new ArrayList<>();
        List<VideoPresetDO> list = videoPresetRepository.queryOrderPresetInfo(queryOrderDeviceSetDTO);
        Iterator<VideoPresetDO> it = list.iterator();
        while (it.hasNext()) {
            VideoPresetDO videoPresetDO = it.next();
            PresetDTO presetDTO = new PresetDTO();
            BeanUtils.copyProperties(videoPresetDO, presetDTO);
            presetDTOList.add(presetDTO);
        }

        return presetDTOList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PresetVO getPresetImg(PresetDTO presetDTO,Map<String, String> headerMap) {
        if (ObjectUtils.isEmpty(presetDTO.getPresetId()) && NullProcessUtil.isNull(presetDTO.getChannelCode())) {
            throw new CustomException("预置位和通道不得为空");
        }
        VideoPresetDO a = videoPresetRepository.getPresetImg(presetDTO);
        if (!ObjectUtils.isEmpty(presetDTO.getPresetId()) && presetDTO.getPresetId() != 0) {
            if (a == null || a.getPan() == null || a.getZoom() == null) {
                if (NullProcessUtil.isNull(presetDTO.getDeviceCode())) {
                    com.chinatower.platform.entity.device.PresetDTO dto = new com.chinatower.platform.entity.device.PresetDTO();
                    BeanUtil.copyProperties(presetDTO, dto);
                    presetDTO.setDeviceCode(iAlgorithmConfigService.getDeviceCode(dto));
                    //调用6.15查询PTZ
                    VideoBaseReq req = new VideoBaseReq();
                    BeanUtils.copyProperties(presetDTO, req);
                    Result<PresetData> result = videoQueryService.queryPtzPosition(req,headerMap);
                    if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                        PresetData data = result.getData();
                        presetDTO.setPan(data.getPan());
                        presetDTO.setTilt(data.getTilt());
                        presetDTO.setZoom(data.getZoom());
                    }
                    presetMapper.updateByPresetId(presetDTO);
                }
            }
        }
        if (a != null && !NullProcessUtil.isNull(a.getImgUrl())) {
            PresetVO presetVO = new PresetVO();
            BeanUtils.copyProperties(a, presetVO);
            return presetVO;
        } else {
            if (ObjectUtils.isEmpty(presetDTO.getPresetId()) || presetDTO.getPresetId() == 0) {
                PresetVO presetVO = new PresetVO();
                BeanUtils.copyProperties(presetDTO, presetVO);
                presetVO.setIsPlot(1);
                return presetVO;
            }
            //6.23转到预置位
            DoControlPresetReq req = new DoControlPresetReq();
            BeanUtils.copyProperties(presetDTO, req);
            req.setPresetId(presetDTO.getPresetId().intValue());
            headerMap.put(VideoConstants.XTOKEN,req.getLockToken());
            videoControlService.doControlPreset(req,headerMap,false);
            //调用6.38抓图
            Result result = snapShot(presetDTO,headerMap);
            if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    throw new RuntimeException(e.getStackTrace()[0].getMethodName() + e.getLocalizedMessage());
                }
                VideoPresetDO pr = videoPresetRepository.getPresetImg(presetDTO);
                if (pr != null && StringUtils.isNotBlank(pr.getImgUrl())) {
                    PresetVO p = new PresetVO();
                    p.setImgUrl(pr.getImgUrl());
                    return p;
                } else {
                    throw new CustomException(DeviceConstants.WARN_MESS2);
                }
            } else {
                throw new CustomException(DeviceConstants.WARN_MESS2);
            }
        }
    }

    @Override
    public List<AlgorithmParamPresetVO>  selectPTZByIdList(List<AlgorithmConfigVO> pos,String industryCode) {

//        List<Long> idList = pos.stream().filter(e -> e.getPosType() == AlgorithmParamBodyVO.POSTYPE_PRESET).map(AlgorithmConfigVO::getPresetId).collect(Collectors.toList());
//        List<String> deviceCodeList = pos.stream().filter(e -> e.getPosType() == AlgorithmParamBodyVO.POSTYPE_PRESET).map(AlgorithmConfigVO::getDeviceCode).collect(Collectors.toList());
        List<AlgorithmParamPresetVO> ptzMapList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(pos)) {
            ptzMapList = presetMapper.selectPTZByIdList(pos, industryCode);
        }
        /*if (!CollectionUtils.isEmpty(idList)) {
            ptzMapList = presetMapper.selectPTZByIdList(idList, deviceCodeList, industryCode);
        }*/
        return ptzMapList;
    }

    @Override
    public VideoPresetDO selectById(Long presetId) {
        return presetMapper.selectById(presetId);
    }

    @Override
    public Result<String> getPresetName(AddPresetReq addPresetReq){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        addPresetReq.setPresetName(Constants.PRESET_NAME);
        addPresetReq.setTenantId(user.getTenantId());
        addPresetReq.setIndustryCode(user.getIndustryCode());
        List<String> presetList = presetMapper.getPresetName(addPresetReq);

        StringBuffer maxPresetName = new StringBuffer(Constants.PRESET_NAME);
        if(CollectionUtils.isEmpty(presetList)){
            maxPresetName.append(Constants.ONE);
        }else{
            //取默认规则名称后部分，重新组合成list
            List<String> presetNameList = presetList.stream().map(s -> s.substring(Constants.PRESET_NAME.length()).replaceAll("\\s*", ""))
                    .filter(temp -> !StringUtils.isEmpty(temp) && this.isNumber(temp)).collect(Collectors.toList());

            if(CollectionUtils.isEmpty(presetNameList)){
                maxPresetName.append(Constants.ONE);
            }else{
                //List<String>转为List<Integer>
                List<Long> codesInteger = presetNameList.stream().map(Long::parseLong).collect(Collectors.toList());
                //倒序排序
                Collections.sort(codesInteger,Collections.reverseOrder());
                maxPresetName.append(codesInteger.get(Constants.ZERO_NUM) + Constants.ONE_NUM);
            }
        }
        return Result.data(maxPresetName.toString());
    }

    /**
     * 判断字符串是否是纯数字
     * @param str
     * @return
     */
    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str.replaceAll("\\s*", "")).matches();
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public void updatePresetSnapshotInfo(ModPresetSnapshotReq modPresetSnapshotReq) {
        VideoPresetDO videoPresetDO = new VideoPresetDO();
        videoPresetDO.setPresetId(modPresetSnapshotReq.getPresetId());
        videoPresetDO.setSnapshotCycle(modPresetSnapshotReq.getSnapshotCycle());
        videoPresetDO.setSnapshotDay(modPresetSnapshotReq.getSnapshotDay());
        videoPresetDO.setSnapshotTime(modPresetSnapshotReq.getSnapshotTime());
        videoPresetDO.setDeviceCode(modPresetSnapshotReq.getDeviceCode());
        videoPresetDO.setIsAlgorithmIdentify(modPresetSnapshotReq.getIsAlgorithmIdentify());
        videoPresetDO.setAlgorithmCode(modPresetSnapshotReq.getAlgorithmCode());
        videoPresetDO.setUpdateTime(new Date());
        videoPresetDO.setModifier(SecurityUtils.getOperator());
        videoPresetDO.setDoubleImgAnalysis(modPresetSnapshotReq.getDoubleImgAnalysis());
        videoPresetRepository.updateVideoPresetByCompositeKeys(videoPresetDO);
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public Result<String> updatePresetName(ModPresetReq modPresetReq) {
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
        // 更新预置位名称
        VideoPresetDO videoPresetDO = new VideoPresetDO();
        videoPresetDO.setPresetId(modPresetReq.getPresetId());
        videoPresetDO.setPresetName(modPresetReq.getPresetName());
        videoPresetDO.setDeviceCode(modPresetReq.getDeviceCode());
        videoPresetDO.setUpdateTime(new Date());
        videoPresetRepository.updateVideoPresetByCompositeKeys(videoPresetDO);
        return Result.success(VideoConstants.UPDATE_PRESET_NAME_SUCC);
    }

    /**
     * 功能描述: 删除预置位校验
     *
     * @param: presetDTO
     * @return: DelCameraPresetCheck
     * @auther: wanglei
     * @date: 2023/6/8
     * @version: 1.5
     */
    @Override
    public DelCameraPresetCheck delCameraPresetCheck(DelPresetReq presetDTO) {
        //查询该预置位是否绑定图斑与电子围栏  >0 绑定图斑与电子围栏  ==0 没绑定
        int polyCount = patrolInfoMapper.isExistByPresetId(presetDTO.getPresetId(), presetDTO.getDeviceCode());
        //查询该预置位是否设置ar标签  >0 设置ar标签  ==0 未设置
        int arCount = patrolInfoMapper.isExistARByPresetId(presetDTO.getPresetId(), presetDTO.getDeviceCode());
        //判断该预置位上是否配置了巡航计划  >0 配置了巡航计划  ==0 没配置
        int patrolCount = patrolInfoMapper.getPatrolCount(presetDTO.getPresetId(), presetDTO.getDeviceCode());
        // 查询是否配置了看守位  >0 配置了看守位 ==0 没配置
        VideoPresetDO videoPresetDO = presetMapper.selectByIdAndDeviceCode(presetDTO.getPresetId(), presetDTO.getDeviceCode());
        String isGuard = videoPresetDO.getIsGuard();
        int guardCount = 0;
        if( isGuard != null && Constants.ONE.equals(isGuard) )
        {
            guardCount = 1;
        }
        //判断该预置位上是否配置了算法  >0 配置了算法  ==0 没配置
        com.chinatower.platform.entity.device.DelPresetReq dto = new com.chinatower.platform.entity.device.DelPresetReq();
        BeanUtil.copyProperties(presetDTO, dto);
        int algorithmConfigCount = iAlgorithmConfigService.getAlgorithmConfigCount(dto);

        DelCameraPresetCheck checkResult = new DelCameraPresetCheck();
        checkResult.setPolyCount(polyCount);
        checkResult.setArCount(arCount);
        checkResult.setPatrolCount(patrolCount);
        checkResult.setGuardCount(guardCount);
        checkResult.setAlgorithmConfigCount(algorithmConfigCount);
        return checkResult;
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
    public ReturnT<String> presetSnapshotJob(String param) {
        // 当前日期时间
        Date dateTime = new Date();
        // 每月几号
        String day = DateUtils.parseDateToStr(VideoConstants.DATE_DD, dateTime);
        // 时间 时:分
        String time = DateUtils.parseDateToStr(DateUtils.HHMM, dateTime);

        Result<List<SysDictDataResponse>> dictResult = systemFeignClient.dictDataType("preset_snapshot_minute_interval");
        if (dictResult.getData() == null || dictResult.getData().isEmpty()) {
            // 对时间进行修正1分钟
            if(!time.endsWith(VideoConstants.ZERO_STR) && !time.endsWith(VideoConstants.FIVE_STR)){
                if(time.endsWith(VideoConstants.ONE_STR) || time.endsWith(VideoConstants.SIX_STR)){
                    dateTime = DateUtils.addMinutes(dateTime, -1);
                }else if(time.endsWith(VideoConstants.FOUR_STR) || time.endsWith(VideoConstants.NINE_STR)){
                    dateTime = DateUtils.addMinutes(dateTime, 1);
                }
                time = DateUtils.parseDateToStr(DateUtils.HHMM, dateTime);
                log.info("修正后的时间为==={}", time);
            }
        }

        // 周几
        int weekday = getWeekday(LocalDateTime.now());
        log.info(VideoConstants.PRESET_SNAPSHOT_JOB_TIME,
                DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, dateTime), day, time, weekday);
        // 查询本次需要抓图的预置位列表
        List<PresetDTO> presetList = videoQueryDAO.queryPresetSnapshotJob(time, weekday, day);
        log.info(VideoConstants.PRESET_SNAPSHOT_ALL_SIZE + presetList.size());
        if( CollectionUtils.isEmpty(presetList) )
        {
            return ReturnT.SUCCESS;
        }
        // 获取转到预置位等待几秒抓图的参数值
        String waitStr = getConfigValue(VideoConstants.DO_CONTROL_PRESET_WAIT);
        if( StringUtils.isEmpty(waitStr) || ! StringUtils.isNumeric(waitStr) )
        {
            waitStr = WAIT_TIME;
        }
        int wait = Integer.parseInt(waitStr);
        // 按照设备、通道分组
        Map<String, List<PresetDTO>> presetMap =
                presetList.stream().collect(Collectors.groupingBy(PresetDTO::getDeviceCode));
        ArrayList<String> channelCodeList = new ArrayList<>(presetMap.keySet());
        ThreadParallelUtil.run(channelCodeList, it -> {
            doControlPresetSnapshot(presetMap.get(it), wait);
            return null;
        }, ThreadParallelUtil.ConfigFactory.PRESET_SNAPSHOT_JOB);
        return ReturnT.SUCCESS;
    }

    public static int getWeekday(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    /**
     * 功能描述: 获取参数配置的值
     *
     * @param: key
     * @auther: wanglei
     * @date: 2023/6/10
     * @version: 1.5
     */
    public String getConfigValue(String key){
        String configValue = Constants.BLANK_STR;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(key);
        if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            configValue = StringUtils.defaultIfBlank(sysConfig.getConfigValue(), Constants.BLANK_STR);
        }
        return configValue;
    }

    /**
     * 功能描述: 转到预置位并抓图
     *
     * @param: presetList
     * @auther: wanglei
     * @date: 2023/6/10
     * @version: 1.5
     */
    public void doControlPresetSnapshot(List<PresetDTO> presetList, int wait)
    {
        if (CollectionUtils.isEmpty(presetList)) {
            return;
        }
        log.info("多线程执行，预置位size:" + presetList.size());
        // 删除列表中的第一预置位同时返回第一个预置位信息
        PresetDTO presetDTO = presetList.remove(VideoConstants.ZERO);
        log.info(VideoConstants.PRESET_PROCESS_INFO + JSON.toJSONString(presetDTO));

        // 转到预置位
        DoControlPresetReq doControlPresetReq = buildRequest(presetDTO);
        // 调用转到预置点方法
        log.info(VideoConstants.ZHUAN_PRESET_TIME,
                presetDTO.getPresetId(), presetDTO.getPresetName(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date()));
        Result doResult = videoControlService.doControlPreset(doControlPresetReq, presetDTO, false);
        if (!isSuccess(doResult)) {
            // 转到预置位失败
            log.error(VideoConstants.DO_CONTROL_PRESET_FAIL,
                    presetDTO.getPresetId(), presetDTO.getPresetName(), JSON.toJSONString(doResult));
            poolExecutor.execute(() -> {
                // 递归调用下一个预置位
                doControlPresetSnapshot(presetList, wait);
            });
        }
        // 转到预置位成功,等待5秒后抓图
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                poolExecutor.execute(() -> {
                    try {
                        // 转到预置位成功,抓图
                        // 用来区分是否是定时任务抓图，如果值是1则是定时抓图，否则不是
                        presetDTO.setPresetSnapshotJob(Constants.ONE);
                        log.info(VideoConstants.PRESET_SNAPSHOT_TIME,
                                presetDTO.getPresetId(), presetDTO.getPresetName(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date()));
                        snapShot(presetDTO);
                    } catch (Exception e) {
                        log.error(VideoConstants.DO_CONTROL_PRESET_SNAPSHOT_FAIL + presetDTO.getPresetId() + presetDTO.getPresetName(), e);
                    } finally {
                        poolExecutor.execute(() -> {
                            //继续调度执行
                            doControlPresetSnapshot(presetList, wait);
                        });
                    }
                });
            }
        }, DateUtil.offset(DateUtil.date(), DateField.SECOND, wait));
    }

    private static boolean isSuccess(Result doResult) {
        return (doResult != null) && (ResultCode.SUCCESS.getCode() == doResult.getCode());
    }

    private static DoControlPresetReq buildRequest(PresetDTO presetDTO) {
        DoControlPresetReq doControlPresetReq = new DoControlPresetReq();
        doControlPresetReq.setDeviceCode(presetDTO.getDeviceCode());
        doControlPresetReq.setChannelCode(presetDTO.getChannelCode());
        doControlPresetReq.setPresetId(presetDTO.getPresetId().intValue());
        doControlPresetReq.setTenantId(presetDTO.getTenantId());
        return doControlPresetReq;
    }

    /**
     * 功能描述: 分页查询预置位定时抓的图片
     *
     * @param: presetImgJobDTO
     * @auther: wanglei
     * @date: 2023/6/11
     * @version: 1.5
     */
    public Page<VmVideoPresetSnapshotMongo> queryPresetImgJob(PresetImgJobDTO presetImgJobDTO)
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();

        Integer pageNum = presetImgJobDTO.getPageNum();
        Integer pageSize = presetImgJobDTO.getPageSize();
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        //由于mysql的时候pageNo是1开始的，mongodb需要减一
        presetImgJobDTO.setPageNum(pageNum-1);
        presetImgJobDTO.setPageSize(pageSize);
        //获取分页参数
        Page page = new Page(presetImgJobDTO.getPageNum(), presetImgJobDTO.getPageSize());

        List<AlgorithmAlarmVO> algorithmAlarmList = videoQueryDAO.queryAlgorithmAlarmList();

        //构造查询条件
        CustomQueryEntity query = this.buildCondition(presetImgJobDTO, user, algorithmAlarmList);
        if(ObjectUtils.isEmpty(query)){
            return new Page<>();
        }
        // 排序
        SortEntity sortEntity = new SortEntity();
        if(Constants.ZERO.equals(presetImgJobDTO.getSort())){
            sortEntity.asc(VideoConstants.SNAPSHOT_TIME_FIELD);
        }else{
            sortEntity.desc(VideoConstants.SNAPSHOT_TIME_FIELD);
        }
        sortEntity.desc(VideoConstants.TASK_ID);
        page.setSortEntity(sortEntity);
        Page<VmVideoPresetSnapshotMongo> pageInfo = presetSnapshotMongoDAO.findPage(query, page, null, MongoDBType.PRIMARY);
        //构造返回值
        this.buildResult(pageInfo.getList(), algorithmAlarmList);
        return pageInfo;
    }

    private CustomQueryEntity buildCondition(PresetImgJobDTO presetImgJobDTO, SysUser user, List<AlgorithmAlarmVO> algorithmAlarmList){
        // 查询条件
        CustomQueryEntity query = new CustomQueryEntity(VmVideoPresetSnapshotMongo.class);

        //查询来源 0：单屏模式 1：视频抓图-摄像机 2：视频抓图-预置位
        if(Constants.ONE.equals(presetImgJobDTO.getQueryType())){
            //视频抓图-摄像机，未勾选摄像机，则返回空数据
            if(!CollectionUtils.isEmpty(presetImgJobDTO.getChannelCodeList())){
                query.in(VideoConstants.CHANNELCODE, presetImgJobDTO.getChannelCodeList().toArray(new String[]{}));
            }else{
                return null;
            }
        }

        if(!StringUtils.isEmpty(presetImgJobDTO.getDeviceCode())){
            query.eq(VideoConstants.DEVICECODE,presetImgJobDTO.getDeviceCode());
        }

        if(!StringUtils.isEmpty(presetImgJobDTO.getChannelCode())){
            query.eq(VideoConstants.CHANNELCODE,presetImgJobDTO.getChannelCode());
        }

        if(!CollectionUtils.isEmpty(presetImgJobDTO.getPresetIdList())){
            Integer[] presetIdList = presetImgJobDTO.getPresetIdList().toArray(new Integer[]{});
            query.in(VideoConstants.PRESET_ID_FIELD, presetIdList);
        }

        if(!ObjectUtils.isEmpty(presetImgJobDTO.getPresetId())){
            query.eq(VideoConstants.PRESET_ID_FIELD, presetImgJobDTO.getPresetId());
        }

        List<String> typeValueList = presetImgJobDTO.getTypeValueList();
        if(!CollectionUtils.isEmpty(typeValueList)){
            List<String> collect = new ArrayList<>();
            for (String s : typeValueList) {
                collect.addAll(algorithmAlarmList.stream()
                        .filter(e -> s.equals(e.getTypeValue()))
                        .map(AlgorithmAlarmVO::getAlgorithmCode)
                        .collect(Collectors.toList()));
            }


            if(CollectionUtils.isEmpty(collect)){
                query.in(VideoConstants.ALGORITHM_CODE, new String[]{Constants.EMPTY});
            }else {
                String[] algorithmCodeList = collect.toArray(new String[]{});
                query.in(VideoConstants.ALGORITHM_CODE, algorithmCodeList);
            }
            query.ne(VideoConstants.WARNING_ORDER_ID,null);
        }

        query.eq(VideoConstants.TENANTID,user.getTenantId());
        query.eq(VideoConstants.INDUSTRYCODE,user.getIndustryCode());
        query.eq(VideoConstants.DELETE_FLAG_FIELD,DeviceConstants.DELETE_FLAG_NORMAL_INT);

        if(!ObjectUtils.isEmpty(presetImgJobDTO.getStartTime()) && !ObjectUtils.isEmpty(presetImgJobDTO.getEndTime())){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timeStr = formatter.format(presetImgJobDTO.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) + ".999";
            DateTimeFormatter formatterWithMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatterWithMillis);
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            query.between(VideoConstants.SNAPSHOT_TIME_FIELD, presetImgJobDTO.getStartTime(), date);
        }
        return query;
    }

    private void buildResult(List<VmVideoPresetSnapshotMongo> list, List<AlgorithmAlarmVO> algorithmAlarmList){
        if(!CollectionUtils.isEmpty(list)){
            // 批量obs加密
            Set<String> fileUrlList = list.stream().map(VmVideoPresetSnapshotMongo::getImgUrl).collect(Collectors.toSet());
            Map<String,String> fileUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrlList));
            for( VmVideoPresetSnapshotMongo temp : list )
            {
                temp.setImgUrl(fileUrlMap.get(temp.getImgUrl()));
            }

            // 批量查询通道名称
            List<DeviceInfoVO> deviceInfoVOS = presetMapper.queryChannelInfoBatch(list);
            for( VmVideoPresetSnapshotMongo temp : list )
            {
                for (DeviceInfoVO deviceInfoVO : deviceInfoVOS) {
                    if(temp.getChannelCode().equals(deviceInfoVO.getChannelCode())){
                        temp.setChannelName(deviceInfoVO.getChannelName());
                    }
                }
            }

            // 批量查询预置位名称
            List<PresetDTO> presetDTOS = presetMapper.queryPresetInfoBatch(list);
            for( VmVideoPresetSnapshotMongo temp : list )
            {
                for (PresetDTO presetDTO : presetDTOS) {
                    if(temp.getDeviceCode().equals(presetDTO.getDeviceCode()) && temp.getPresetId().equals(presetDTO.getPresetId().intValue())){
                        temp.setPresetName(presetDTO.getPresetName());
                    }
                }
            }

            // 批量查询设备信息
            List<DeviceInfoVO> deviceInfoS = presetMapper.queryDeviceInfoBatch(list);
            for( VmVideoPresetSnapshotMongo temp : list )
            {
                for (DeviceInfoVO deviceInfoVO : deviceInfoS) {
                    if(temp.getDeviceCode().equals(deviceInfoVO.getDeviceCode())){
                        temp.setLongitude(deviceInfoVO.getLongitude());
                        temp.setLatitude(deviceInfoVO.getLatitude());
                    }
                }
            }


            // 使用Map存储algorithmCode到typeName的映射
            Map<String, String> algorithmCodeToTypeName = algorithmAlarmList.stream()
                    .collect(HashMap::new,
                            (map, alarm) -> map.put(alarm.getAlgorithmCode(), alarm.getTypeName()),
                            HashMap::putAll);

            // 处理列表中的每个元素
            list.forEach(snapshot -> {
                // 设置typeName（仅当warningOrderId不为空时）
                if (!StringUtils.isEmpty(snapshot.getWarningOrderId())) {
                    snapshot.setTypeName(algorithmCodeToTypeName.getOrDefault(snapshot.getAlgorithmCode(), null));
                }
            });
        }
    }

    /**
     * 功能描述: 分页查询预置位定时抓的图片
     *
     * @param: presetListImgJobDTO
     * @auther: wanglei
     * @date: 2023/9/15
     * @version: 1.6.10
     */
    public Page<VmVideoPresetSnapshotMongoVO> queryPresetListImgJob(PresetListImgJobDTO presetListImgJobDTO)
    {
        Integer pageNum = presetListImgJobDTO.getPageNum();
        Integer pageSize = presetListImgJobDTO.getPageSize();
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        // 限制最大只能查1000条
        if( pageSize > 1000 )
        {
            throw new CustomException(VideoConstants.PAGE_SIZE_1000);
        }
        //获取当前租户
        SysUser user = SecurityUtils.getLoginUser().getUser();

        //由于mysql的时候pageNo是1开始的，mongodb需要减一
        presetListImgJobDTO.setPageNum(pageNum-1);
        presetListImgJobDTO.setPageSize(pageSize);
        //获取分页参数
        Page page = new Page(presetListImgJobDTO.getPageNum(), presetListImgJobDTO.getPageSize());
        // 查询条件
        QueryEntity query = new QueryEntity(VmVideoPresetSnapshotMongoVO.class);
        query.eq(VideoConstants.TENANTID,user.getTenantId());
        query.eq(VideoConstants.INDUSTRYCODE,user.getIndustryCode());
        query.eq(VideoConstants.DEVICECODE,presetListImgJobDTO.getDeviceCode());
        query.eq(VideoConstants.CHANNELCODE,presetListImgJobDTO.getChannelCode());
        List<Integer> presetIdList = presetListImgJobDTO.getPresetIdList();
        if( ! CollectionUtils.isEmpty(presetIdList) )
        {
            query.in(VideoConstants.PRESET_ID_FIELD,presetIdList.toArray(new Integer[presetIdList.size()]));
        }
        query.eq(VideoConstants.DELETE_FLAG_FIELD,DeviceConstants.DELETE_FLAG_NORMAL_INT);
        query.between(VideoConstants.SNAPSHOT_TIME_FIELD, presetListImgJobDTO.getStartTime(), presetListImgJobDTO.getEndTime());
        // 排序
        SortEntity sortEntity = new SortEntity();
        sortEntity.desc(VideoConstants.SNAPSHOT_TIME_FIELD);
        page.setSortEntity(sortEntity);
        Page<VmVideoPresetSnapshotMongoVO> pageInfo = presetListSnapshotMongoDAO.findPage(query, page, null, MongoDBType.PRIMARY);
        List<VmVideoPresetSnapshotMongoVO> list = pageInfo.getList();
        if( ! CollectionUtils.isEmpty(list) )
        {
            // 批量obs加密
            List<String> fileUrlList = list.stream().map(VmVideoPresetSnapshotMongoVO::getImgUrl).collect(Collectors.toList());
            Map<String,String> fileUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
            for( VmVideoPresetSnapshotMongoVO temp : list )
            {
                temp.setOriginUrl(temp.getImgUrl());
                temp.setImgUrl(fileUrlMap.get(temp.getImgUrl()));
            }
        }
        return pageInfo;
    }

    @Override
    public VideoPresetDO selectByIdAndDeviceCode(Long presetId,String deviceCode) {
        return presetMapper.selectByIdAndDeviceCode(presetId,deviceCode);
    }

    /**
     * 功能描述: 更新预置位配准信息
     * @param deviceCode
     * @param channelCode
     * @param presetId
     * @param flag 1：预置位编辑  2：预置位删除
     */
    @Override
    public void updateDeviceRegistration(String deviceCode, String channelCode, Long presetId, String flag) {
        try {
            //1：预置位编辑  2：预置位删除
            if (Constants.ONE.equals(flag)) {
                //  配准状态（registration_status）更新为0：未配准，
                //	配准开关（registration_switch）更新为0：关闭，
                //	配准信息（registration_info）更新为null。
                presetMapper.updateDeviceRegistration(deviceCode, channelCode, presetId);
            } else if (Constants.TWO.equals(flag)) {
                // 将配准记录物理删除
                presetMapper.deleteRegistrationInfo(deviceCode, channelCode, presetId);
            }
        } catch (Exception e) {
            log.error("更新预置位配准信息异常", e);
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertPresetSnapshotList(List<VideoPresetSnapshotVO> presetSnapshotList) {
        presetSnapshotBatch(presetSnapshotList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePresetSnapshotList(List<VideoPresetSnapshotVO> presetSnapshotList) {
        presetSnapshotBatch(presetSnapshotList);
    }

    @Override
    public void deletePresetSnapshot(VideoPresetSnapshotVO videoPresetSnapshotVO) {
        // 调用删除预置快照的Mapper方法
        presetMapper.deletePresetSnapshot(videoPresetSnapshotVO);
    }

    @Override
    public void deletePresetSnapshotList(VideoPresetSnapshotVO videoPresetSnapshotVO) {
        // 判断videoPresetSnapshotVO的idList是否不为空
        if(CollectionUtil.isNotEmpty(videoPresetSnapshotVO.getIdList())){
            // 调用presetMapper的deletePresetSnapshotList方法，传入idList参数
            presetMapper.deletePresetSnapshotList(videoPresetSnapshotVO.getIdList());
        }
    }

    @Override
    public Result<String> checkPresetSnapshot(VideoPresetSnapshotVO videoPresetSnapshotVO) {
        String presetSnapshotTime =  presetMapper.getPresetSnapshotTime(videoPresetSnapshotVO);
        if(StringUtils.isNotEmpty(presetSnapshotTime)){
            return Result.fail("该预置位已存在抓图配置，请勿重复添加");
        }else{
            return Result.success("该预置位不存在配置，可以添加");
        }
    }


    @Override
    @DataScope(deviceColumn="t.device_code", acceptColumn = "vod.accept_node", tenantColumn="t.tenant_id",alarmSourceVal = "4", industryCode="t.industry_code")
    public PageInfo<VideoPresetSnapshotVO> queryPresetSnapshotList(VideoPresetSnapshotVO videoPresetSnapshotVO) {
        // 设置分页信息
        PageHelper.startPage(videoPresetSnapshotVO.getPageNum(), videoPresetSnapshotVO.getPageSize());

        // 查询视频预设快照列表
        // 算法信息优先取 vm_order_algorithm_config，匹配不上则回退 vm_order_algorithms，都不匹配则为 null（SQL 层 COALESCE 实现）
        List<VideoPresetSnapshotVO> presetSnapshotList = presetMapper.queryPresetSnapshotList(videoPresetSnapshotVO);

        List<String> deviceCodeList = new ArrayList<>();
        presetSnapshotList.forEach(item -> {
            String supportDoubleImgAnalysis = Constants.ZERO;
            String extendJson = item.getExtendJson();
            if (!org.springframework.util.StringUtils.isEmpty(extendJson)) {
                ExtendJson ej = JSONObject.parseObject(extendJson,ExtendJson.class);
                if(!ObjectUtils.isEmpty(ej)){
                    String o2 = ej.getSupportDoubleImgAnalysis();
                    if(!ObjectUtils.isEmpty(o2)){
                        supportDoubleImgAnalysis = o2;
                    }
                }
            }
            item.setSupportDoubleImgAnalysis(supportDoubleImgAnalysis);
            item.setExtendJson(null);
            deviceCodeList.add(item.getDeviceCode());
        });

        if(!CollectionUtils.isEmpty(deviceCodeList)){
            List<DeviceInfoVO> channelsByDeviceCode = videoQueryDAO.getChannelsByDeviceCode(deviceCodeList);
            Map<String, List<DeviceInfoVO>> deviceMap =
                    channelsByDeviceCode.stream().collect(Collectors.groupingBy(DeviceInfoVO::getDeviceCode));

            for (VideoPresetSnapshotVO presetSnapshotVO : presetSnapshotList) {
                presetSnapshotVO.setIfVisibleLight(false);
                List<DeviceInfoVO> deviceInfoVOS = deviceMap.get(presetSnapshotVO.getDeviceCode());
                if(!CollectionUtils.isEmpty(deviceInfoVOS) && deviceInfoVOS.size() > Constants.ONE_NUM){
                    String channelCode = channelsByDeviceCode.get(Constants.ZERO_NUM).getChannelCode();
                    if(presetSnapshotVO.getChannelCode().equals(channelCode)){
                        presetSnapshotVO.setIfVisibleLight(true);
                    }
                }
            }
        }
        // 创建分页信息对象
        PageInfo<VideoPresetSnapshotVO> pageInfo = new PageInfo<VideoPresetSnapshotVO>(presetSnapshotList);
        // 返回分页信息
        return pageInfo;
    }

    public void presetSnapshotBatch(List<VideoPresetSnapshotVO> presetSnapshotList){
        // 判断列表是否为空
        if(CollectionUtil.isNotEmpty(presetSnapshotList)){
            // 遍历预设快照列表
            for (VideoPresetSnapshotVO presetSnapshot : presetSnapshotList) {
                // 设置更新时间为当前时间
                presetSnapshot.setUpdateTime(new Date());
                // 设置修改人为当前登录用户名
                presetSnapshot.setModifier(SecurityUtils.getOperator());
            }
            // 调用预设快照Mapper方法，将预设快照列表插入数据库
            presetMapper.updatePresetSnapshotList(presetSnapshotList);
        }
    }

    @Override
    public Result<List<AlarmTypeDO>> getAiAlarmType(){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        return Result.data(presetMapper.getAiAlarmType(user));
    }

    @Override
    public String updateWarningOrderId(PresetImgVO presetImgVO){
        Query query = new Query();
        query.addCriteria(Criteria.where(VideoConstants.TASK_ID).is(presetImgVO.getTaskId()));
        Update update = new Update();
        update.set(VideoConstants.WARNING_ORDER_ID, presetImgVO.getWarningOrderId());
        mongoTemplate.updateFirst(query, update, VideoConstants.VM_VIDEO_PRESET_SNAPSHOT);
        return VideoConstants.OPERATE_SUCCESS;
    }
}
