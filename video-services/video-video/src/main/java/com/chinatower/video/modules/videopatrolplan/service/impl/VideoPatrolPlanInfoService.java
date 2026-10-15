package com.chinatower.video.modules.videopatrolplan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.order.api.IVideoOrderService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.constants.DeviceTableConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.order.VideoOrderReq;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.annotation.DeviceOrdAuthScope;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.dao.VideoCommonDAO;
import com.chinatower.video.modules.common.entity.ResultEntity;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.ApiInfoUtils;
import com.chinatower.video.modules.common.util.VideoCommonUtil;
import com.chinatower.video.modules.constants.IndustryConstants;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.query.service.VideoQueryService;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolInfoDO;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolPresetRelaDO;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;
import com.chinatower.video.modules.videopatrolplan.dto.PatrolAlgoRelDTO;
import com.chinatower.video.modules.videopatrolplan.mapper.PatrolInfoMapper;
import com.chinatower.video.modules.videopatrolplan.repository.IPatrolInfoRepository;
import com.chinatower.video.modules.videopatrolplan.repository.IPatrolPresetRelaRepository;
import com.chinatower.video.modules.videopatrolplan.service.IVideoPatrolPlanInfoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by zheng.yan on 2022/1/18.
 */
@Service
@Slf4j
public class VideoPatrolPlanInfoService implements IVideoPatrolPlanInfoService {

    public static final String NOT_AUTH_OPERATION_PATROL = "暂无巡航计划操作权限";
    @Autowired
    private IVideoOrderService videoOrderService;
    @Autowired
    private IPatrolInfoRepository patrolInfoRepository;
    @Autowired
    private IPatrolPresetRelaRepository patrolPresetRelaRepository;
    @Autowired
    private ApiInfoUtils apiInfoUtils;
    @Autowired
    private VideoQueryService videoQueryService;
    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    PatrolInfoMapper patrolInfoMapper;

    @Autowired
    private VideoQueryDAO videoQueryDAO;
    @Resource
    private NetConnectUtils connectUtils;
    @Autowired
    VideoCommonService videoCommonService;
    @Autowired
    VideoControlService videoControlService;
    @Autowired
    VideoPatrolCommonService videoPatrolCommonService;
    @Resource
    ICasCadeQueryService iCasCadeQueryService;

    @Resource
    VideoCommonDAO videoCommonDAO;

    /**
     * 默认巡航路径时间
     */
    public static final String PATROL_TIME_PERIOD =
            "00:00,00:10,00:20,00:30,00:40,00:50," +
                    "01:00,01:10,01:20,01:30,01:40,01:50," +
                    "02:00,02:10,02:20,02:30,02:40,02:50," +
                    "03:00,03:10,03:20,03:30,03:40,03:50," +
                    "04:00,04:10,04:20,04:30,04:40,04:50," +
                    "05:00,05:10,05:20,05:30,05:40,05:50," +
                    "06:00,06:10,06:20,06:30,06:40,06:50," +
                    "07:00,07:10,07:20,07:30,07:40,07:50," +
                    "08:00,08:10,08:20,08:30,08:40,08:50," +
                    "09:00,09:10,09:20,09:30,09:40,09:50," +
                    "10:00,10:10,10:20,10:30,10:40,10:50," +
                    "11:00,11:10,11:20,11:30,11:40,11:50," +
                    "12:00,12:10,12:20,12:30,12:40,12:50," +
                    "13:00,13:10,13:20,13:30,13:40,13:50," +
                    "14:00,14:10,14:20,14:30,14:40,14:50," +
                    "15:00,15:10,15:20,15:30,15:40,15:50," +
                    "16:00,16:10,16:20,16:30,16:40,16:50," +
                    "17:00,17:10,17:20,17:30,17:40,17:50," +
                    "18:00,18:10,18:20,18:30,18:40,18:50," +
                    "19:00,19:10,19:20,19:30,19:40,19:50," +
                    "20:00,20:10,20:20,20:30,20:40,20:50," +
                    "21:00,21:10,21:20,21:30,21:40,21:50," +
                    "22:00,22:10,22:20,22:30,22:40,22:50," +
                    "23:00,23:10,23:20,23:30,23:40,23:50";
    public static final String PATROL_NAME_REPEAT = "巡航计划名称已存在。";

    public static final String MAO_HAO = ":";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public void addCruisePatrol(AddPatrolReq patrolDTO) {
        //查询摄像机下的可见光通道（通道编码较小的）
        String channelCode = patrolInfoMapper.getMinChannelCode(patrolDTO.getDeviceCode());
        if(!StringUtils.isEmpty(channelCode)){
            patrolDTO.setChannelCode(channelCode);
        }else{
            throw new CustomException("无可用通道!");
        }

        if (checkSamePatrol(null, patrolDTO)) {
            throw new CustomException(PATROL_NAME_REPEAT);
        }

        Long patrolId = System.currentTimeMillis()+ buildRandom(8);

        VideoOrderReq videoOrderReq = new VideoOrderReq();
        videoOrderReq.setTenantId(patrolDTO.getTenantId());
        videoOrderReq.setChannelCode(patrolDTO.getChannelCode());
        videoOrderReq.setDeviceCode(patrolDTO.getDeviceCode());
        videoOrderReq.setIndustryCode(patrolDTO.getIndustryCode());
        Result<VideoOrderResp> resp = videoOrderService.getVideoOrderInfo(videoOrderReq);

        if (!ObjectUtils.isEmpty(resp) && !ObjectUtils.isEmpty(resp.getData())
                && !StringUtils.isEmpty(resp.getData().getOrderCode()) ) {
            String orderCode = resp.getData().getOrderCode();
            patrolDTO.setOrderCode(orderCode);
        } else {
            throw new CustomException("订单信息不存在!");
        }

        PatrolInfoDO patrolInfo = new PatrolInfoDO();
        patrolDTO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        BeanUtils.copyProperties(patrolDTO, patrolInfo);
        patrolInfo.setPatrolId(patrolId);
        patrolInfo.setCreateTime(new Date());
//        patrolInfo.setUpdateTime(new Date());
        patrolInfo.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        // 获取当前的用户
        patrolInfo.setCreator(patrolDTO.getUserName());
//        patrolInfo.setModifier(patrolDTO.getUserName());
        patrolInfo.setStartStop(IndustryConstants.PATROL_INFO_STOP);
        patrolInfo.setIfCruise(IndustryConstants.PATROL_CRUISE_STOP);
        String startTimePeriod = patrolInfo.getStartTimePeriod();
        if (StringUtils.isBlank(startTimePeriod)) {
            patrolInfo.setStartTimePeriod(PATROL_TIME_PERIOD);
        }else if(!startTimePeriod.contains(MAO_HAO)){
            patrolInfo.setStartTimePeriod(this.getNewMinList(startTimePeriod));
        }
        String industryCode = patrolDTO.getIndustryCode();
        String appVerCode = patrolDTO.getAppVerCode();
        patrolInfo.setIndustryCode(industryCode);
        patrolInfo.setAppVerCode(appVerCode);
        patrolInfo.setInitStartTimePeriod(patrolInfo.getStartTimePeriod());

        patrolInfoRepository.save(patrolInfo);
        this.savePatrolPresetRela(patrolInfo, patrolDTO.getPresets());
    }

    /**
     * 小时转为分钟
     *
     * @return
     * @author wangyt
     * @date 2022/9/16
     * @version
     */
    public String getNewMinList(String startTimePeriod){
        List<String> time = new ArrayList<>(Arrays.asList(startTimePeriod.split(DeviceConstants.COMMA)));
        List<String> newTime = new ArrayList<>();
        String[] m = new String[]{":00",":10",":20",":30",":40",":50"};
        for (String s : time) {
            for (String string : m) {
                newTime.add(s + string);
            }
        }
        return newTime.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public VideoPatrolVO getPatrol(DelPatrolReq patrolDTO)
    {
    	VideoPatrolVO videoPatrolVO = new VideoPatrolVO();
    	videoPatrolVO.setPatrolId(patrolDTO.getPatrolId());
    	return videoQueryDAO.getPatrol(videoPatrolVO);
    }

    @Override
    public List<VideoPatrolVO> getPatrolByIdList(DelPatrolReq patrolDTO) {
        return videoQueryDAO.getPatrolByIdList(patrolDTO.getPatrolIdList());
    }

    @Override
    @DeviceOrdAuthScope
    public void delPatrol(DelPatrolReq patrolDTO) {
        List<Long> patrolIds = new ArrayList<>(1);
        patrolIds.add(patrolDTO.getPatrolId());
        patrolInfoRepository.removePatrols(patrolIds);
    }

    @Override
    @DeviceOrdAuthScope
    public void batchDelPatrolNew(DelPatrolReq patrolDTO) {
        patrolInfoRepository.removePatrols(patrolDTO.getPatrolIdList());
    }

    @Override
    @DeviceOrdAuthScope
    public void batchDelPatrol(BatchDelPatrolReq patrolDTO) {

        patrolInfoRepository.removePatrols(patrolDTO.getPatrolIds());

        JSONObject inJson = (JSONObject) JSON.toJSON(patrolDTO);
        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_BATCHDELPATROL);
        apiInfoDTO.setDeviceCode(patrolDTO.getDeviceCode());
        apiInfoDTO.setInJsonStr(inJson.toJSONString());
        apiInfoDTO.setTowerCode("");
        apiInfoDTO.setOrderCode(patrolDTO.getOrderCode());
        apiInfoDTO.setChannelCode(patrolDTO.getChannelCode());
        apiInfoDTO.setUserCode(patrolDTO.getUserCode());
        apiInfoDTO.setTenantId(patrolDTO.getTenantId());
        apiInfoDTO.setTowerCode(patrolDTO.getTowerCode());

        Result<JSONObject> res = null;
        try {
            NetConnectUtils.RequestParamter<BatchDelPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(patrolDTO.getDeviceCode());
            //属地化数据统一改造 add by wangyt 20240118
            patrolDTO.setAcceptNode(casCadeVO.getPlatformCode());
            patrolDTO.setAccessNode(casCadeVO.getCascadeToCode());
            // 请求参数
            paramter.setBody(patrolDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.BATCH_DEL_PATROL);
            // 接口路径参数
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.BATCH_DEL_PATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.BATCH_DEL_PATROL_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.BATCH_DEL_PATROL_ERROR +VideoConstants.EQUAL_INFO, e);
        }
        if (ResultCode.SUCCESS.getCode() != res.getCode()) {
            throw new CustomException(res.getMsg(), HttpStatus.ERROR_4001);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public void updatePatrol(ModPatrolReq patrolDTO) {
        Long patrolId = patrolDTO.getPatrolId();

        PatrolInfoDO dbVo = patrolInfoRepository.getById(patrolId);
        PatrolInfoDO uptPatrolInfo = new PatrolInfoDO();
        patrolDTO.setChannelCode(dbVo.getChannelCode());
        patrolDTO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        BeanUtils.copyProperties(patrolDTO, uptPatrolInfo);

        if (checkSamePatrol(patrolId, patrolDTO)) {
            throw new CustomException(PATROL_NAME_REPEAT);
        }
        // 获取当前的用户

        Date now = new Date();

        //uptPatrolInfo.setCreator(patrolDTO.getUserName());
        uptPatrolInfo.setModifier(patrolDTO.getUserName());
        uptPatrolInfo.setUpdateTime(now);
        String industryCode = patrolDTO.getIndustryCode();
        String appVerCode = patrolDTO.getAppVerCode();
        uptPatrolInfo.setIndustryCode(industryCode);
        uptPatrolInfo.setAppVerCode(appVerCode);
        uptPatrolInfo.setDeleteFlag(0);
        String startTimePeriod = uptPatrolInfo.getStartTimePeriod();
        if (StringUtils.isBlank(startTimePeriod)) {
            uptPatrolInfo.setStartTimePeriod(PATROL_TIME_PERIOD);
        }else if(!startTimePeriod.contains(MAO_HAO)){
            uptPatrolInfo.setStartTimePeriod(this.getNewMinList(startTimePeriod));
        }
        uptPatrolInfo.setInitStartTimePeriod(uptPatrolInfo.getStartTimePeriod());
        patrolInfoRepository.updateById(uptPatrolInfo);
        // 先删再增关联表
        List<Long> patrolIds = new ArrayList<>(1);
        patrolIds.add(patrolDTO.getPatrolId());
        patrolPresetRelaRepository.removeByPatrols(patrolIds);
        this.savePatrolPresetRela(uptPatrolInfo, patrolDTO.getPresets());

        PatrolInfoVO vo = new PatrolInfoVO();
        vo.setDeviceCode(patrolDTO.getDeviceCode());
        vo.setChannelCode(patrolDTO.getChannelCode());
        vo.setPatrolId(patrolDTO.getPatrolId());
        // 查询修改的这个巡航计划有没有正在巡航
        List<PatrolInfoVO> patrolInfoList = patrolInfoMapper.selectPatrolMerge(vo);
        if(!CollectionUtils.isEmpty(patrolInfoList)){
            this.mergePatrol(vo);
        }

    }

    /**
     * 校验巡航计划名称是否重复
     * @param: patrolId
     * @param: patrolDTO
     * @return: boolean
     * @author: zheng.yan
     * @date: 2022/3/20
     * @version:
     */
    public boolean checkSamePatrol(Long patrolId, AddPatrolReq patrolDTO) {
        boolean result = false;
        String industryCode = patrolDTO.getIndustryCode();
        //判断名称是否重复  20220111 add by zzj R-ZBZB-202112310835397_视频管理优化_需求
        if (!StringUtils.isBlank(patrolDTO.getPatrolName())) {
            LambdaQueryWrapper<PatrolInfoDO> queryPatrol = new LambdaQueryWrapper<>();
            queryPatrol.eq(PatrolInfoDO::getChannelCode, patrolDTO.getChannelCode());
            queryPatrol.eq(PatrolInfoDO::getDeviceCode, patrolDTO.getDeviceCode());
            queryPatrol.eq(PatrolInfoDO::getTenantId, patrolDTO.getTenantId());
            queryPatrol.eq(PatrolInfoDO::getPatrolName, patrolDTO.getPatrolName());
            queryPatrol.eq(PatrolInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
            queryPatrol.eq(PatrolInfoDO::getIndustryCode, industryCode);
            if (patrolId != null) {
                queryPatrol.notIn(PatrolInfoDO::getPatrolId, patrolId);
            }
            result = patrolInfoRepository.count(queryPatrol) > 0;
        }
        return result;
    }

    private PatrolPresetRelaDO buildRelaBO(PatrolInfoDO patrolInfo, AddPatrolPresetReq preset) {
        PatrolPresetRelaDO patrolPresetRela = new PatrolPresetRelaDO();
        patrolPresetRela.setPatrolId(patrolInfo.getPatrolId());
        patrolPresetRela.setDeviceCode(patrolInfo.getDeviceCode());
        patrolPresetRela.setChannelCode(preset.getChannelCode());
        patrolPresetRela.setPresetId(preset.getPresetId());
        patrolPresetRela.setStaySeconds(preset.getStaySeconds().intValue());
        patrolPresetRela.setSpeed(preset.getSpeed());
        patrolPresetRela.setSort(preset.getSort());
        patrolPresetRela.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        return patrolPresetRela;
    }

    private void savePatrolPresetRela(PatrolInfoDO patrolInfo, List<AddPatrolPresetReq> presets) {
        if (patrolInfo != null && presets != null && !presets.isEmpty()) {
            List<PatrolPresetRelaDO> relaList = new ArrayList<>(presets.size());
            for (AddPatrolPresetReq preset : presets) {
                relaList.add(buildRelaBO(patrolInfo, preset));
            }
            patrolPresetRelaRepository.saveBatch(relaList);
        }
    }

    @Override
    @DeviceOrdAuthScope
    @Transactional(rollbackFor = Exception.class)
    public Result<String> doPatrol(SttPatrolReq patrolDTO) {
        if (patrolDTO.getPatrolId() == null) {
            throw new CustomException(DeviceConstants.PATROL_ID_NOT_NULL);
        }

        String tenantId;
        String industryCode;
        String userId;
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
            SysUserDTO sysUserDTO = new SysUserDTO();
            sysUserDTO.setUserName(user.getUserName());
            SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
            userId = userVO.getUserId();
        } catch (Exception e) {
            SysUserDTO sysUserDTO = new SysUserDTO();
            sysUserDTO.setIsTenantAdmin(VideoConstants.ONE);
            sysUserDTO.setTenantId(patrolDTO.getTenantId());
            userId = videoCommonService.getUserName(sysUserDTO).getUserId();
            //第三方调用，开放API，默认行业编码为通用
            industryCode = VideoConstants.INDUSTRU_CODE;
            tenantId = patrolDTO.getTenantId();
        }

        //判断当前时间是否在该租户、用户操控时间范围内
        if (!videoControlService.ifCurrentInOperatePeriod(
                patrolDTO.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.THREE)) {
            return Result.fail(NOT_AUTH_OPERATION_PATROL);
        }

        Long patrolId = patrolDTO.getPatrolId();

        PatrolInfoDO dbVo = patrolInfoRepository.getById(patrolId);
        patrolDTO.setChannelCode(dbVo.getChannelCode());

        //add by liuxt 20231010
        if(StringUtils.isEmpty(patrolDTO.getIndustryCode())){//铁塔视角传行业编码，其余不传行业编码
            patrolDTO.setIndustryCode(industryCode);
        }

        //1:订单启停批量调用 2：通用页面调用：3：SDK调用
        if(!Constants.ONE.equals(patrolDTO.getSource())){
            //查询出有效的订购编码
            List<PatrolInfoVO> chkOrderList = patrolInfoMapper.chkOrder(patrolDTO);
            if(!CollectionUtils.isEmpty(chkOrderList)){
                if(patrolDTO.getPatrolType() == Constants.TWO_NUM){//启用
                    String orderCode = chkOrderList.get(0).getOrderCode();
                    patrolDTO.setOrderCode(orderCode);
                }
            }else{
                return Result.fail(DeviceConstants.CHK_ORDER_DEVICES);
            }

            //判断是否开启AR巡航
            List<String> patrolInfoVOS = patrolInfoMapper.queryARCruisePlan(patrolDTO);
            if (!patrolInfoVOS.isEmpty()) {
                throw new CustomException(DeviceConstants.OTHER_PATROL_EXEC);
            }
        }

        String msg="";

        QueryWrapper<PatrolInfoDO> patrolQuery = new QueryWrapper<>();
        patrolQuery.eq("patrol_id", patrolDTO.getPatrolId()).eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
        List<PatrolInfoDO> patrolList = patrolInfoRepository.list(patrolQuery);
        if (CollectionUtils.isEmpty(patrolList)) {
            throw new CustomException(DeviceConstants.PATROL_NOT_EXIST);
        }

        //获取设备信息
        String deviceCode = patrolDTO.getDeviceCode();
        DeviceInfoVO deviceInfoVO = patrolInfoMapper.getDeviceInfo(deviceCode);

        //如果该通道下有其他的巡航计划在巡航,需提示“该巡航仅可在某个时间段进行巡航”
        if (DeviceConstants.PATROL_PLAN_STATUS_START == patrolDTO.getPatrolType()) {
            QueryWrapper<PatrolInfoDO> startedPatrolQuery = new QueryWrapper<>();
            startedPatrolQuery.eq(DeviceTableConstants.DEVICE_CODE, patrolDTO.getDeviceCode()).eq(DeviceTableConstants.CHANNEL_CODE, patrolDTO.getChannelCode());
            startedPatrolQuery.eq("start_stop", DeviceConstants.PATROL_PLAN_STATUS_START).eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
            List<PatrolInfoDO> startedPatrolResList = patrolInfoRepository.list(startedPatrolQuery);

            //其他巡航计划正在巡航 && 按时间段整合
            if (!CollectionUtils.isEmpty(startedPatrolResList) && DeviceConstants.ONE.equals(deviceInfoVO.getPartrolMode())) {
                //时段冲突的巡航list
                List<PatrolInfoDO> conflictPeriodList = new ArrayList<>();
                List<String> operatorList = new ArrayList<>();
                //当前巡航的时段
                List<String> paIdList = new ArrayList<>(Arrays.asList(patrolList.get(0).getStartTimePeriod().split(DeviceConstants.COMMA)));
                for (PatrolInfoDO pa : startedPatrolResList) {
                    String startTimePeriod = pa.getStartTimePeriod();
                    if(!StringUtils.isEmpty(startTimePeriod)){
                        List<String> list = Arrays.asList(startTimePeriod.split(DeviceConstants.COMMA));
                        //有交集即有冲突，将巡航放到冲突的list里
                        if (!Collections.disjoint(paIdList, list)) {
                            conflictPeriodList.add(pa);
                            operatorList.add(pa.getOperator());
                        }
                    }
                }

                //比较当前巡航时段 和 已启动的巡航时段 有冲突
                if (!CollectionUtils.isEmpty(conflictPeriodList)) {
                    log.info("有启动冲突的巡航");
                    List<PatrolInfoDO> updateList = new ArrayList<>();
                    //已被占用时段
                    List<String> timeList = new ArrayList<>();

                    //查询已启动状态的巡航操作人id
                    Map<String,String> userMap = this.getUserMap(operatorList);

                    for (PatrolInfoDO patrolInfoDO : conflictPeriodList) {
                        //当前巡航权限高
                        if (videoControlService.ifPriority(deviceCode,patrolDTO.getChannelCode(),tenantId,industryCode,
                                userId,userMap.get(patrolInfoDO.getOperator()),Constants.THREE)) {
                            log.info("巡航{}比{}权限高",patrolDTO.getPatrolId(),patrolInfoDO.getPatrolId());
                            //当前巡航的时段
                            List<String> currentTime = paIdList;
                            //已启动巡航时段
                            List<String> startedList = Arrays.asList(patrolInfoDO.getStartTimePeriod().split(DeviceConstants.COMMA));
                            //将已启动的权限低的巡航去掉与当前巡航相同的时段
                            List<String> list = startedList.stream().filter(time -> !currentTime.contains(time)).collect(Collectors.toList());
                            String newStartTimePeriod = list.stream().map(String::valueOf).collect(Collectors.joining(","));
                            PatrolInfoDO uptPatrol = new PatrolInfoDO();
                            uptPatrol.setPatrolId(patrolInfoDO.getPatrolId());
                            uptPatrol.setStartTimePeriod(newStartTimePeriod.isEmpty() ? null : newStartTimePeriod);
                            updateList.add(uptPatrol);
                        }else{
                            log.info("巡航{}比{}权限低",patrolDTO.getPatrolId(),patrolInfoDO.getPatrolId());
                            //已启动巡航时段
                            List<String> startedList = Arrays.asList(patrolInfoDO.getStartTimePeriod().split(DeviceConstants.COMMA));
                            //已被占用时段
                            timeList.addAll(paIdList.stream().filter(time -> startedList.contains(time)).collect(Collectors.toList()));
                            //当前巡航的时段
                            paIdList = paIdList.stream().filter(time -> !startedList.contains(time)).collect(Collectors.toList());
                        }
                    }

                    //被占用，提示无法启动
                    if (CollectionUtils.isEmpty(paIdList)) {
                        return Result.data(204,"",DeviceConstants.PATROL_OCCUPY_NOT_START);
                    } else {
                        if(!CollectionUtils.isEmpty(timeList)){
                            //提示可巡航时间段
                            StringBuilder sb = new StringBuilder("启动成功，该巡航计划在");
                            sb = appendMsg(sb, timeList);
                            sb.append("已被占用，仅可在");
                            sb = appendMsg(sb, paIdList);
                            sb.append("执行该计划。");
                            msg = sb.toString();
                        }
                        String newStartTimePeriod = paIdList.stream().map(String::valueOf).collect(Collectors.joining(","));

                        PatrolInfoDO uptPatrol = new PatrolInfoDO();
                        uptPatrol.setPatrolId(patrolDTO.getPatrolId());
                        uptPatrol.setStartTimePeriod(newStartTimePeriod);
                        updateList.add(uptPatrol);
                    }
                    //按照操作权限优先级别，更新冲突时段
                    log.info("抢占后，需要更新的巡航信息，{}",JSON.toJSONString(updateList));
                    if(!CollectionUtils.isEmpty(updateList)){
                        patrolInfoRepository.updatePatrolStartTimeBatch(updateList);
                    }
                }
            }
        }

        //判断当前时间是否在启动时间段内
        if (StringUtils.isNotBlank(patrolList.get(0).getStartTimePeriod())) {
            String[] startTimePeriodArr = patrolList.get(0).getStartTimePeriod().split(DeviceConstants.COMMA);
            //获取真实的巡航时段
            String hour = videoCommonService.getActualHour();
            //不在时间段内，更新巡航计划为【启动】，等待定时任务执行
            if (!Arrays.asList(startTimePeriodArr).contains(hour)) {
                if (DeviceConstants.PATROL_PLAN_STATUS_START == patrolDTO.getPatrolType()) {
                    msg = DeviceConstants.PATROL_NOT_ARRIVE_START_TIME;
                }
            }
        }

        PatrolInfoDO uptPatrol = new PatrolInfoDO();
        uptPatrol.setPatrolId(patrolDTO.getPatrolId());
        uptPatrol.setStartStop(patrolDTO.getPatrolType() + "");
        //1:订单启停批量调用 2：通用页面调用：3：SDK调用
        if(!Constants.ONE.equals(patrolDTO.getSource())){
            //add by liuxt 20231010
            uptPatrol.setOrderCode(patrolDTO.getOrderCode());
        }

        patrolInfoRepository.updatePatrolStartTime(uptPatrol);

        //整合巡航计划
        PatrolInfoVO vo = new PatrolInfoVO();
        vo.setDeviceCode(patrolDTO.getDeviceCode());
        vo.setChannelCode(patrolDTO.getChannelCode());
        vo.setPatrolId(patrolDTO.getPatrolId());
        this.mergePatrol(vo);

        //如果是停用操作，将实际执行时间端，还原为设置的时间段
        if (DeviceConstants.PATROL_PLAN_STATUS_STOP == patrolDTO.getPatrolType()) {
            List<PatrolInfoDO> list = new ArrayList<>();
            PatrolInfoDO patrolInfoDO = new PatrolInfoDO();
            patrolInfoDO.setPatrolId(patrolList.get(0).getPatrolId());
            patrolInfoDO.setStartTimePeriod(patrolList.get(0).getInitStartTimePeriod());
            list.add(patrolInfoDO);
            patrolInfoMapper.updatePatrolStartTimeBatch(list);
        }

        if(com.chinatower.platform.common.utils.StringUtils.isNotEmpty(msg)){
            return Result.success(msg);
        }else {
        	if (DeviceConstants.PATROL_PLAN_STATUS_START == patrolDTO.getPatrolType())
        	{
        		return Result.success("启用巡航路径成功!");
        	}
        	else
        	{
        		return Result.success("停用巡航路径成功!");
        	}
        }
    }

    /**
     * 比较两个list有没有相同的值
     *
     * @return
     * @author wangyt
     * @date 2022/9/16
     * @version
     */
    private boolean listCompareValue(List<String> list1, List<String> list2){
        boolean flag = true;
        compare : for (String value1:list1) {
            for (String value2:list2) {
                if(value1.equals(value2)){
                    flag = false;
                    break compare;
                }
            }
        }
        return flag;
    }

    /**
     * 编辑返回信息
     *
     * @return
     * @author wangyt
     * @date 2022/9/16
     * @version
     */
    private StringBuilder appendMsg(StringBuilder sb, List<String> dataList){
        if(dataList.size() == 1){
            String s = dataList.get(0);
            sb.append(s).append("~").append(this.addTenMinute(s));
        }else{
        	dataList.sort(Comparator.comparing(String::toString));
            for(int i=0; i<=dataList.size()-1; i++){
                String s = dataList.get(i);
                if(i == 0){
                    sb.append(s).append("~");
                }
                if(i+1 == dataList.size()){
                    sb.append(this.addTenMinute(s));
                }else{
                    String s1 = dataList.get(i+1);
                    if(!this.ifTenMinuteDiff(s,s1)){
                        sb.append(this.addTenMinute(s)).append(",");
                        sb.append(s1).append("~");
                    }
                }
            }
        }
        return sb;
    }

    /**
     * 巡航时段+10分钟
     * @param time
     * @return
     */
    private String addTenMinute(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 10);
            Date newDate = calendar.getTime();
            return sdf.format(newDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断相邻的时间段是否相差10分钟
     * @param start
     * @param end
     * @return
     */
    private boolean ifTenMinuteDiff(String start, String end){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date date = sdf.parse(start);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 10);
            Date newDate = calendar.getTime();
            return sdf.format(newDate).equals(end);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询订单巡航路径
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @Override
    public List<PatrolDTO> queryOrderPatrolInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {

        String industryCode = queryOrderDeviceSetDTO.getIndustryCode();
        queryOrderDeviceSetDTO.setIndustryCode(industryCode);
        List<PatrolDTO> patrolDTOList = new ArrayList<>();
        List<PatrolInfoDO> list = patrolInfoRepository.queryOrderPatrolInfo(queryOrderDeviceSetDTO);
        Iterator<PatrolInfoDO> it = list.iterator();
        while (it.hasNext()) {
            PatrolInfoDO videoPresetDO = it.next();
            PatrolDTO patrolDTO = new PatrolDTO();
            com.chinatower.common.core.utils.bean.BeanUtils.copyProperties(videoPresetDO, patrolDTO);
            patrolDTOList.add(patrolDTO);
        }

        return patrolDTOList;
    }

    /**
     * 功能描述: 查询巡航路径列表
     *
     * @param
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/28
     * @version: 1.0
     */
    @Override
    @DeviceOrdAuthScope
    public JSONObject queryPatrol(VideoQueryPatrolData videoQueryPatrol) {
        JSONObject returnObject = new JSONObject();
        try{

            VideoPatrolVO queryPatrolVO = new VideoPatrolVO();
            List<QueryPatrolData> videoPatrolVOList = new ArrayList<>();
            List<QueryPatrolData> finalList = new ArrayList<>();

            queryPatrolVO.setDeviceCode(videoQueryPatrol.getDeviceCode());
            queryPatrolVO.setChannelCode(videoQueryPatrol.getChannelCode());
            queryPatrolVO.setCustomerCode(videoQueryPatrol.getTenantId());
            queryPatrolVO.setIndustryCode(videoQueryPatrol.getIndustryCode());
            queryPatrolVO.setDeleteFlag(Integer.valueOf(VideoConstants.VALID_STATE));
            videoPatrolVOList = patrolInfoMapper.getPatrolListNew(queryPatrolVO);
            if(!CollectionUtils.isEmpty(videoPatrolVOList)){
                List<Long> patrolIdList = videoPatrolVOList.stream().map(o -> Long.valueOf(o.getPatrolId())).collect(Collectors.toList());
                VideoPatrolVO queryVo = new VideoPatrolVO();
                queryVo.setPatrolIdList(patrolIdList);
                List<QueryPatrolListData> list = patrolInfoMapper.getPatrolPresetListNew(queryVo);
                for(QueryPatrolData qpVo:videoPatrolVOList){
                    List<QueryPatrolListData> curList = list.stream().filter(n -> n.getPatrolId().equals(Long.valueOf(qpVo.getPatrolId()))).collect(Collectors.toList());
                    qpVo.setPresets(curList);
                    if (!StringUtils.isEmpty(qpVo.getStartTimePeriod())){
                        qpVo.setStartTimePeriodArr(qpVo.getStartTimePeriod().split(VideoConstants.COMMA));
                    }
                    finalList.add(qpVo);
                }
            }

            returnObject.put(DeviceConstants.DATA, finalList);
            return returnObject;

        } catch (Exception e) {
            log.error(DeviceConstants.BUSINESS_PROCESS_EXCEPTION , e);
            throw new CustomException(e.getLocalizedMessage());
        }

    }


    /**
     * 整合巡航路径
     * @param patrolInfoVO
     * 描述：
     * //1.当所有巡航都停止时
     * //1.1启动巡航，当前时间在巡航时间内，当前状态应为（执行中）启动状态应为（启动中）
     * //1.2启动巡航，当前时间不在巡航时间内，当前状态应为（未执行）启动状态应为（启动中）
     *
     * //2.巡航A已预约已启动
     * //2.1启动巡航B，当前时间在巡航时间内，当前状态应为（未执行）启动状态应为（启动中）
     * //2.2启动巡航B，当前时间不在巡航时间内，当前状态应为（未执行）启动状态应为（启动中）
     *
     * //3.巡航A已预约未启动
     * //3.1启动巡航B，当前时间在巡航时间内，当前状态应为（执行中）启动状态应为（启动中）
     * //3.2启动巡航B，当前时间不在巡航时间内，当前状态应为（未执行）启动状态应为（启动中）
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergePatrol(PatrolInfoVO patrolInfoVO) {
        log.info(VideoConstants.ID+patrolInfoVO.getPatrolId()+VideoConstants.DEVICE+ patrolInfoVO.getDeviceCode() + "：整合开始！");
        String deviceCode = patrolInfoVO.getDeviceCode();
        //1.根据设备编码获取设备信息，以及巡航配置partrol_mode 1:按时间段整合 2:按预置位整合'，以及设备接入节点
        DeviceInfoVO deviceInfoVO = patrolInfoMapper.getDeviceInfo(deviceCode);

        //获取来源节点编码,即本节点
        AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.PLATFORM_CODE);
        if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if(!ObjectUtils.isEmpty(sysConfig)){
                deviceInfoVO.setPlatformCode(sysConfig.getConfigValue());
            }
        }

        //获取真实的巡航时段
        String hour = videoCommonService.getActualHour();
        patrolInfoVO.setStartTimePeriod(hour);

        if(!ObjectUtils.isEmpty(deviceInfoVO)){
            //2.通过设备编码、通道编码查vm_patrol_info_t表中是否包含当前时间段且已预约启动的所有巡航计划
            List<PatrolInfoVO> patrolList = patrolInfoMapper.getPatrolInfoList(patrolInfoVO);
            //2.1 不存在包含当前时间段且已预约启动的所有巡航计划
            if(patrolList.size()==0){
                //2.1.1 根据设备编码、通道编码查vm_patrol_info_merge_t表，是否存在整合巡航计划
                List<PatrolInfoVO> patrolMergeList = patrolInfoMapper.getPatrolMergeListById(patrolInfoVO);
                //2.1.2 若存在，把整合的巡航计划停止并删除；不存在，则不做处理
                if(!CollectionUtils.isEmpty(patrolMergeList) && patrolMergeList.size() > 0) {
                    PatrolInfoVO queryPatrolInfoVO = patrolMergeList.get(0);
                    queryPatrolInfoVO.setIndustryCode(patrolInfoVO.getIndustryCode());
                    //删除巡航计划整合表
                    patrolInfoMapper.deletePatrolMerge(queryPatrolInfoVO);
                    //删除巡航计划预置点关系表
                    patrolInfoMapper.deletePatrolPresetMerge(queryPatrolInfoVO);
                    //调用基础子平台，6.32.查询巡航路径接口；并判断巡航路径状态，若该巡航计划正在启动中，先停用巡航计划
                    Result<JSONObject> mergeQueryAndDoPatrolRes = videoPatrolCommonService.mergeQueryAndDoPatrol(queryPatrolInfoVO,deviceInfoVO);
                    if (ResultCode.SUCCESS.getCode() != mergeQueryAndDoPatrolRes.getCode()) {
                        //add by liuxt 增加基础报错信息记录 start
                        try{
                            BaseErrorRecordVO vo = new BaseErrorRecordVO();
                            vo.setDeviceCode(queryPatrolInfoVO.getDeviceCode());
                            vo.setChannelCode(queryPatrolInfoVO.getChannelCode());
                            vo.setKeywords(DeviceConstants.PATROL_ID);
                            vo.setKeywordsValue(queryPatrolInfoVO.getPatrolId().toString());
                            vo.setRequest(JSONObject.toJSONString(queryPatrolInfoVO));
                            vo.setResponse(JSONObject.toJSONString(mergeQueryAndDoPatrolRes));
                            vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
                            videoCommonService.saveBaseErrorRecord(vo);
                        }catch (Exception e){
                            log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
                        }
                        //add by liuxt 增加基础报错信息记录 end
                        throw new CustomException(mergeQueryAndDoPatrolRes.getMsg(), HttpStatus.ERROR_4001);
                    }
                    //调用基础子平台，6.29.删除巡航路径接口
                    Result<JSONObject> mergeDelPatrolRes = videoPatrolCommonService.mergeDelPatrol(queryPatrolInfoVO,deviceInfoVO);
                    if (ResultCode.SUCCESS.getCode() != mergeDelPatrolRes.getCode()) {
                        //add by liuxt 增加基础报错信息记录 start
                        try{
                            BaseErrorRecordVO vo = new BaseErrorRecordVO();
                            vo.setDeviceCode(queryPatrolInfoVO.getDeviceCode());
                            vo.setChannelCode(queryPatrolInfoVO.getChannelCode());
                            vo.setKeywords(DeviceConstants.PATROL_ID);
                            vo.setKeywordsValue(queryPatrolInfoVO.getPatrolId().toString());
                            vo.setRequest(JSONObject.toJSONString(queryPatrolInfoVO));
                            vo.setResponse(JSONObject.toJSONString(mergeDelPatrolRes));
                            vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
                            videoCommonService.saveBaseErrorRecord(vo);
                        }catch (Exception e){
                            log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
                        }
                        //add by liuxt 增加基础报错信息记录 end
                        throw new CustomException(mergeDelPatrolRes.getMsg(), HttpStatus.ERROR_4001);
                    }
                }
            } else {
                //2.2 存在包含当前时间段且已预约启动的所有巡航计划
                String[] startTimePeriodArr = patrolList.get(0).getStartTimePeriod().split(DeviceConstants.COMMA);

                //2.2.1 根据设备编码、通道编码查vm_patrol_info_merge_t表，是否存在整合且已预约启动的巡航计划
                List<PatrolInfoVO> patrolMergeList = patrolInfoMapper.getPatrolMergeList(patrolInfoVO);
                //2.2.1.1 存在整合且已预约启动的巡航计划,做更新操作。
                if(patrolMergeList != null && patrolMergeList.size() >0 ) {
                    PatrolInfoVO updatePatrolInfo = patrolList.get(0);
                    PatrolInfoVO potrolMergeInfoVO = patrolMergeList.get(0);
                    potrolMergeInfoVO.setIndustryCode(patrolInfoVO.getIndustryCode());
                    //判断是否需要执行更新
                    // 1 当前已启动的巡航计划与本次更新的巡航计划是同一个，且更新的巡航计划的修改时间大于巡航计划的启动时间，则需要做处理。a.after(b):a在b之后为true；
                    // 2 当前已启动的巡航计划与本次更新的巡航计划不是同一个
                    //如果整合表没有正在执行的计划，或者执行的计划与整合计划是同一个，则更新
                    if ((Constants.ONE).equals(potrolMergeInfoVO.getIfCruise())
                        || potrolMergeInfoVO.getSourcePatrolId().indexOf(updatePatrolInfo.getPatrolId().toString())!=-1
                        || Arrays.asList(startTimePeriodArr).contains(hour) ) {
                        PatrolInfoVO patrolInfoVOTemp = new PatrolInfoVO();
                        patrolInfoVOTemp.setDeviceCode(patrolInfoVO.getDeviceCode());
                        patrolInfoVOTemp.setChannelCode(patrolInfoVO.getChannelCode());
                        patrolInfoVOTemp.setStartTimePeriod(hour);

                        //更新巡航计划整合表
                        PatrolInfoVO uptPatrolInfo = new PatrolInfoVO();
                        BeanUtils.copyProperties(updatePatrolInfo, uptPatrolInfo);
                        uptPatrolInfo.setPatrolId(potrolMergeInfoVO.getPatrolId());
                        uptPatrolInfo.setPatrolName(potrolMergeInfoVO.getPatrolName());

                        //按照1按时间整合，sourcePatrolId为巡航基础表中的patrolId；
                        // 2:按预置位整合，sourcePatrolId为所有巡航基础表中的patrolId拼接而成
                        PatrolInfoVO patrolHourInfo = videoPatrolCommonService.getPatrolPresetInfo(patrolList);
                        if (!ObjectUtils.isEmpty(patrolHourInfo)) {
                            uptPatrolInfo.setStartTimePeriod(patrolHourInfo.getStartTimePeriod());
                            uptPatrolInfo.setSourcePatrolId(patrolHourInfo.getSourcePatrolId());
                            updatePatrolInfo.setPatrolIdList(patrolHourInfo.getPatrolIdList());
                        }
                        //更新整合表
                        patrolInfoMapper.updatePatrolMerge(uptPatrolInfo);
                        //先删再增关联表
                        patrolInfoMapper.deletePatrolPresetMerge(uptPatrolInfo);
                        //查询预置位信息，并将预置位集合重新排序，按照预约时间的先后顺序
                        List<AddPatrolPresetReq> presetVOList = videoPatrolCommonService.patrolPresetHandle(updatePatrolInfo);
                        //批量插入巡航计划预置点位关系整合表
                        videoPatrolCommonService.batchSavePatrolPresetMerge(potrolMergeInfoVO, presetVOList);

                        //调用基础子平台，6.32.查询巡航路径接口；并判断巡航路径状态，若该巡航计划正在启动中，先停用巡航计划
                        Result<JSONObject> mergeQueryAndDoPatrolRes = videoPatrolCommonService.mergeQueryAndDoPatrol(potrolMergeInfoVO,deviceInfoVO);
                        if (ResultCode.SUCCESS.getCode() != mergeQueryAndDoPatrolRes.getCode()) {
                            //add by liuxt 增加基础报错信息记录 start
                            try{
                                BaseErrorRecordVO vo = new BaseErrorRecordVO();
                                vo.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
                                vo.setChannelCode(potrolMergeInfoVO.getChannelCode());
                                vo.setKeywords(DeviceConstants.PATROL_ID);
                                vo.setKeywordsValue(potrolMergeInfoVO.getPatrolId().toString());
                                vo.setRequest(JSONObject.toJSONString(potrolMergeInfoVO));
                                vo.setResponse(JSONObject.toJSONString(mergeQueryAndDoPatrolRes));
                                vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
                                videoCommonService.saveBaseErrorRecord(vo);
                            }catch (Exception e){
                                log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
                            }
                            //add by liuxt 增加基础报错信息记录 end
                            throw new CustomException(mergeQueryAndDoPatrolRes.getMsg(), HttpStatus.ERROR_4001);
                        }

                        //调用基础子平台，6.31.修改巡航路径接口
                        Result<JSONObject> mergeUpdatePatrolRes = videoPatrolCommonService.mergeUpdatePatrol(potrolMergeInfoVO,presetVOList,updatePatrolInfo,deviceInfoVO);
                        if (ResultCode.SUCCESS.getCode() != mergeUpdatePatrolRes.getCode()) {
                            //add by liuxt 增加基础报错信息记录 start
                            try{
                                BaseErrorRecordVO vo = new BaseErrorRecordVO();
                                vo.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
                                vo.setChannelCode(potrolMergeInfoVO.getChannelCode());
                                vo.setKeywords(DeviceConstants.PATROL_ID);
                                vo.setKeywordsValue(potrolMergeInfoVO.getPatrolId().toString());
                                vo.setRequest(JSONObject.toJSONString(potrolMergeInfoVO));
                                vo.setResponse(JSONObject.toJSONString(mergeUpdatePatrolRes));
                                vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
                                videoCommonService.saveBaseErrorRecord(vo);
                            }catch (Exception e){
                                log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
                            }
                            //add by liuxt 增加基础报错信息记录 end
                            throw new CustomException(mergeUpdatePatrolRes.getMsg(), HttpStatus.ERROR_4001);
                        }else{
                            //如在时间段内，启动巡航，反之，则等待定时任务执行
                            if (Arrays.asList(startTimePeriodArr).contains(hour)) {
                                //调用基础子平台，6.33.启用/停用巡航路径接口
                                Result<JSONObject> mergeDoPatrolRes = videoPatrolCommonService.mergeDoPatrol(potrolMergeInfoVO.getPatrolId(),updatePatrolInfo,deviceInfoVO);
                                if (ResultCode.SUCCESS.getCode() != mergeDoPatrolRes.getCode()) {
                                    //add by liuxt 增加基础报错信息记录 start
                                    try{
                                        BaseErrorRecordVO vo = new BaseErrorRecordVO();
                                        vo.setDeviceCode(updatePatrolInfo.getDeviceCode());
                                        vo.setChannelCode(updatePatrolInfo.getChannelCode());
                                        vo.setKeywords(DeviceConstants.PATROL_ID);
                                        vo.setKeywordsValue(potrolMergeInfoVO.getPatrolId().toString());
                                        vo.setRequest(JSONObject.toJSONString(updatePatrolInfo));
                                        vo.setResponse(JSONObject.toJSONString(mergeDoPatrolRes));
                                        vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
                                        videoCommonService.saveBaseErrorRecord(vo);
                                    }catch (Exception e){
                                        log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
                                    }
                                    //add by liuxt 增加基础报错信息记录 end
                                    throw new CustomException(mergeDoPatrolRes.getMsg(), HttpStatus.ERROR_4001);
                                } else {
                                    //启动巡航计划成功之后，将整合表巡航状态更新为启动
                                    PatrolInfoVO uptPatrolInfo1 = new PatrolInfoVO();
                                    uptPatrolInfo1.setPatrolId(potrolMergeInfoVO.getPatrolId());
                                    uptPatrolInfo1.setIfCruise(Constants.TWO);
                                    uptPatrolInfo1.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
                                    patrolInfoMapper.updatePatrolMerge(uptPatrolInfo1);
                                }
                            }
                        }
                    }
                } else {
                    //2.2.1.2 不存在整合且已预约启动的巡航计划,做新增操作。
                    PatrolInfoVO insertPatrolInfoVO = patrolList.get(0);
                    AddPatrolReq addPatrolDTO = new AddPatrolReq();
                    String patrolName = VideoConstants.MERGE_PATROL_NAME + System.currentTimeMillis()+ buildRandom(8);
                    addPatrolDTO.setPatrolName(patrolName);
                    //按照1按时间整合，sourcePatrolId为巡航基础表中的patrolId；
                    // 2:按预置位整合，sourcePatrolId为所有巡航基础表中的patrolId拼接而成
                    PatrolInfoVO patrolHourInfo = videoPatrolCommonService.getPatrolPresetInfo(patrolList);
                    if (!ObjectUtils.isEmpty(patrolHourInfo)) {
                        insertPatrolInfoVO.setStartTimePeriod(patrolHourInfo.getStartTimePeriod());
                        insertPatrolInfoVO.setSourcePatrolId(patrolHourInfo.getSourcePatrolId());
                        insertPatrolInfoVO.setPatrolIdList(patrolHourInfo.getPatrolIdList());
                    }
                    //查询预置位信息，并将预置位集合重新排序，按照预约时间的先后顺序
                    List<AddPatrolPresetReq> presetVOList = videoPatrolCommonService.patrolPresetHandle(insertPatrolInfoVO);
                    addPatrolDTO.setPresets(presetVOList);

                    //调用基础子平台，6.28.增加巡航路径接口
                    Result<JSONObject> mergeAddCruisePatrolRes = videoPatrolCommonService.mergeAddCruisePatrol(addPatrolDTO,insertPatrolInfoVO,deviceInfoVO);
                    if (ResultCode.SUCCESS.getCode() == mergeAddCruisePatrolRes.getCode()) {
                        Long patrolId = mergeAddCruisePatrolRes.getData().getJSONObject(DeviceNorthIntfConstants.DATA).getLong(DeviceNorthIntfConstants.PATROL_ID);
                        insertPatrolInfoVO.setPatrolId(patrolId);
                        insertPatrolInfoVO.setPatrolName(patrolName);

                        //插入巡航计划整合表
                        patrolInfoMapper.savePatrolMerge(insertPatrolInfoVO);
                        //批量插入巡航计划预置点位关系整合表
                        videoPatrolCommonService.batchSavePatrolPresetMerge(insertPatrolInfoVO, presetVOList);

                        //如在时间段内，启动巡航，反之，则等待定时任务执行
                        if (Arrays.asList(startTimePeriodArr).contains(hour)) {
                            //调用基础子平台，6.33.启用/停用巡航路径接口
                            Result<JSONObject> mergeDoPatrolRes = videoPatrolCommonService.mergeDoPatrol(patrolId,insertPatrolInfoVO,deviceInfoVO);
                            if (ResultCode.SUCCESS.getCode() != mergeDoPatrolRes.getCode()) {
                                //add by liuxt 20231020 新增巡航计划成功但启用失败，则调用基础子平台，删除该巡航计划，保证应用和基础数据一致
                                PatrolInfoVO newPatrolInfoVO = new PatrolInfoVO();
                                newPatrolInfoVO.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
                                newPatrolInfoVO.setChannelCode(insertPatrolInfoVO.getChannelCode());
                                newPatrolInfoVO.setPatrolId(insertPatrolInfoVO.getPatrolId());
                                log.info("启用失败删除该巡航计划" + VideoConstants.REQUEST_LOG + JSONObject.toJSONString(newPatrolInfoVO));
                                videoPatrolCommonService.deletePatrol(newPatrolInfoVO,deviceInfoVO);
                                //add by liuxt 增加基础报错信息记录 start
                                try{
                                    BaseErrorRecordVO vo = new BaseErrorRecordVO();
                                    vo.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
                                    vo.setChannelCode(insertPatrolInfoVO.getChannelCode());
                                    vo.setKeywords(DeviceConstants.PATROL_ID);
                                    vo.setKeywordsValue(patrolId.toString());
                                    vo.setRequest(JSONObject.toJSONString(insertPatrolInfoVO));
                                    vo.setResponse(JSONObject.toJSONString(mergeDoPatrolRes));
                                    vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
                                    videoCommonService.saveBaseErrorRecord(vo);
                                }catch (Exception e){
                                    log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
                                }
                                //add by liuxt 增加基础报错信息记录 end
                                throw new CustomException(mergeDoPatrolRes.getMsg(), HttpStatus.ERROR_4001);
                            }else {
                                //将整合表巡航状态更新为启动
                                PatrolInfoVO uptPatrolInfo1 = new PatrolInfoVO();
                                uptPatrolInfo1.setPatrolId(patrolId);
                                uptPatrolInfo1.setIfCruise(Constants.TWO);
                                uptPatrolInfo1.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
                                patrolInfoMapper.updatePatrolMerge(uptPatrolInfo1);
                            }
                        }
                    } else {
                        //add by liuxt 增加基础报错信息记录 start
                        try{
                            BaseErrorRecordVO vo = new BaseErrorRecordVO();
                            vo.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
                            vo.setChannelCode(insertPatrolInfoVO.getChannelCode());
                            vo.setKeywords(DeviceConstants.PATROL_ID);
                            vo.setKeywordsValue("");
                            vo.setRequest(JSONObject.toJSONString(insertPatrolInfoVO));
                            vo.setResponse(JSONObject.toJSONString(mergeAddCruisePatrolRes));
                            vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
                            videoCommonService.saveBaseErrorRecord(vo);
                        }catch (Exception e){
                            log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
                        }
                        //add by liuxt 增加基础报错信息记录 end
                        throw new CustomException(mergeAddCruisePatrolRes.getMsg(), HttpStatus.ERROR_4001);
                    }
                }
            }
        }
        //更新巡航计划表的执行状态;     先将巡航基础表该设备下所有的巡航启动状态都置为1-停用
        PatrolInfoDO upInfo = new PatrolInfoDO();
        upInfo.setDeviceCode(patrolInfoVO.getDeviceCode());
        upInfo.setChannelCode(patrolInfoVO.getChannelCode());
        upInfo.setIfCruise(Constants.ONE);
        patrolInfoMapper.updatePatrolInfo(upInfo);
        //再根据整合表中的sourcePatrolId字段，将巡航基础表已启动的巡航计划置为2-启用
        List<PatrolInfoVO> mergeList = patrolInfoMapper.getPatrolMergeListNew(patrolInfoVO);
        if(mergeList.size()>0){
            List<String> partolList = Arrays.asList(mergeList.get(0).getSourcePatrolId().split(VideoConstants.COMMA));
            List<PatrolInfoDO> list = new ArrayList<>();
            for(String pId:partolList) {
                PatrolInfoDO upInfoNew = new PatrolInfoDO();
                upInfoNew.setPatrolId(Long.parseLong(pId));
                upInfoNew.setIfCruise(Constants.TWO);
                list.add(upInfoNew);
            }
            patrolInfoRepository.updateBatchById(list);

        }
        log.info(VideoConstants.ID+patrolInfoVO.getPatrolId()+VideoConstants.DEVICE+ patrolInfoVO.getDeviceCode() + "：整合结束！");
    }


    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    private static int buildRandom(int length) {
        int num = 1;
        double random = new SecureRandom().nextDouble();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) (random * num);
    }



    /**
     * 暂停恢复巡航计划接口
     * @param patrolDTO
     * @return
     */
    @Override
    @DeviceOrdAuthScope
    @Transactional(rollbackFor = Exception.class)
    public Result pauseResumePatrol(SttPatrolPauseReq patrolDTO) {
        log.info(VideoConstants.PAUSE_RESUME_PATROL + VideoConstants.CONTROLLERREQ + patrolDTO);

        //根据摄像机编码查询实际配置的巡航通道
        String realChannelCode = patrolInfoMapper.getRealChannelCode(patrolDTO.getDeviceCode());
        patrolDTO.setChannelCode(realChannelCode);
        log.info("realChannelCode=" + realChannelCode);

        patrolDTO.setPatrolType(Long.valueOf(patrolDTO.getPauseResumeStatus()));
        String keyA = VideoConstants.PAUSERESUME_A_ +patrolDTO.getDeviceCode() + VideoConstants.UNDERLINE_STR
                + patrolDTO.getChannelCode();

        //查询基础平台，巡航任务状态接口
        VideoQueryPatrolData videoQueryPatrol = new VideoQueryPatrolData();
        videoQueryPatrol.setDeviceCode(patrolDTO.getDeviceCode());
        videoQueryPatrol.setChannelCode(patrolDTO.getChannelCode());
        Result<List<QueryPatrolData>> listResult = videoQueryService.queryPatrolNorth(videoQueryPatrol);
        List<QueryPatrolData> listData = listResult.getData();

        //暂停/恢复状态:3-暂停；4-恢复
        if (patrolDTO.ifPauseResumeStatusThree()) {
            //巡航路径暂停逻辑处理
            return getPauseResult(patrolDTO, keyA, listData);
        } else if (patrolDTO.ifPauseResumeStatusFour()) {
            //恢复巡航通用方法
            return resumePatrolCommon(keyA, patrolDTO, listData);
        }

        return Result.fail(VideoConstants.PARAM_DATA_ERROR);

    }


    /**
     * 巡航路径暂停逻辑处理
     * @param
     * @return
     */
    private Result getPauseResult(SttPatrolPauseReq patrolDTO, String keyA, List<QueryPatrolData> listData) {
        //不存在巡航，返回报错
        if (CollectionUtils.isEmpty(listData)) {
            return Result.fail(VideoConstants.PAUSE_ERROR);
        }

        Long pauseDuration  = Long.valueOf(videoControlService.selectDictDataByType(VideoConstants.PAUSE_DURATION_TIME));

        //只要存在正在巡航的ID
        if (ifExistPatrol(listData)){
            Result resultEntity = doPauseResumePatrol(patrolDTO, listData);
            if (resultEntity != null) return resultEntity;
        } else {
            patrolDTO.setPatrolId(listData.get(0).getPatrolId());
        }

        //落缓存
        RedisUtil.setEx(keyA, patrolDTO.getPatrolId(), pauseDuration*VideoConstants.INT_60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
        return Result.data(pauseDuration*VideoConstants.INT_60);
    }


    /**
     * 判断基础返回数据是否存在正在巡航数据；
     * 存在，获取巡航ID，调用暂停或恢复接口
     * @param
     * @return
     */
    private Result doPauseResumePatrol(SttPatrolPauseReq patrolDTO, List<QueryPatrolData> listData) {
        List<QueryPatrolData> collect = listData.stream().filter(ent -> 1 == ent.getIsCruise()).collect(Collectors.toList());
        patrolDTO.setPatrolId(collect.get(0).getPatrolId());
        //调基础子平台接口
        ResultEntity resultEntity = pauseResumePatrolNorth(patrolDTO);
        //成功
        if (!VideoCommonUtil.crIsSuccess(resultEntity)) {
            return Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsgNew(resultEntity, VideoConstants.DOPATROL_ERROR));
        }
        return null;
    }


    /**
     * 7.33.6.33.启用/停用巡航路径 (3-暂停；4-恢复)
     * @param
     * @return
     */
    public ResultEntity pauseResumePatrolNorth(SttPatrolPauseReq patrolDTO) {
        ResultEntity cr = null;
        try{
            NetConnectUtils.RequestParamter<SttPatrolPauseReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by wangyt 20240118
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(patrolDTO.getDeviceCode());
            patrolDTO.setAcceptNode(casCadeVO.getPlatformCode());
            patrolDTO.setAccessNode(casCadeVO.getCascadeToCode());
            // 请求参数
            paramter.setBody(patrolDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_PATROL);
            // 接口路径参数
            path.setDeviceCode(patrolDTO.getDeviceCode());
            path.setChannelCode(patrolDTO.getChannelCode());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.DOPATROL + VideoConstants.REQUEST_LOG + paramter);
            cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DOPATROL + VideoConstants.RESPONSE_LOG + cr);

        } catch (Exception e) {
            log.error(VideoConstants.DOPATROL_ERROR, e);
        }

        return cr;
    }


    /**
     * 恢复巡航通用方法
     * @param
     * @return
     */
    public Result resumePatrolCommon(String key, SttPatrolPauseReq patrolDTO, List<QueryPatrolData> listData) {
        Result res = Result.success(VideoConstants.OPERATE_SUCCESS);
        //不存在巡航计划或者存在正在巡航的ID
        if (!CollectionUtils.isEmpty(listData) && ifExistPatrol(listData)) {
            Result resultEntity = doPauseResumePatrol(patrolDTO, listData);
            if (resultEntity != null) return resultEntity;
        } else {
            res = Result.success(VideoConstants.DEVICE_NOT_RESUME_STATUS_PATROL);
        }
        //redis中是否存在这个KEY
        if (RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
            RedisUtil.delete(key,RedisDBType.PRIMARY);
        }
        return res;
    }


    /**
     * 列表中是否存在正在巡航的数据
     * @param
     * @return
     */
    public boolean ifExistPatrol(List<QueryPatrolData> listData) {
        return listData.stream().filter(vo -> 1==vo.getIsCruise()).findAny().isPresent();
    }

    @Override
    public List<String> getExistTimePeriod(VideoBaseReq patrolDTO){
        List<String> list = new ArrayList<>();
        DeviceInfoVO deviceInfo = patrolInfoMapper.getDeviceInfo(patrolDTO.getDeviceCode());
        //巡航类型 1:按时间段整合2:按预置位整合
        if(DeviceConstants.ONE.equals(deviceInfo.getPartrolMode())){
            List<String> patrolInfoList = patrolInfoMapper.getExistTimePeriod(patrolDTO);
            for (String startTimePeriod : patrolInfoList) {
                list.addAll(Arrays.asList(startTimePeriod.split(Constants.COMMA)));
            }
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public Result<String> updateStartTimePeriod(){
        VideoPatrolVO videoPatrolVO = new VideoPatrolVO();
        videoPatrolVO.setDeleteFlag(Constants.ZERO_NUM);
        List<QueryPatrolData> patrolListNew = patrolInfoMapper.getPatrolListNew(videoPatrolVO);
        String[] m = new String[]{":00",":10",":20",":30",":40",":50"};
        List<VideoPatrolVO> updateList = new ArrayList<>();
        for (QueryPatrolData queryPatrolData : patrolListNew) {
            String startTimePeriod = queryPatrolData.getStartTimePeriod();
            if(!StringUtils.isEmpty(startTimePeriod) && !startTimePeriod.contains(MAO_HAO)){
                VideoPatrolVO updateVo = new VideoPatrolVO();
                updateVo.setPatrolId(queryPatrolData.getPatrolId());
                String[] split = startTimePeriod.split(Constants.COMMA);
                StringBuilder stp = new StringBuilder();
                for (String s : split) {
                    for (String string : m) {
                        stp.append(s).append(string).append(Constants.COMMA);
                    }
                }
                updateVo.setStartTimePeriod(stp.substring(0,stp.length()-1));
                updateList.add(updateVo);
            }
        }

        List<PatrolInfoVO> patrolMerge = patrolInfoMapper.getPatrolMerge();
        List<VideoPatrolVO> updateMergeList = new ArrayList<>();
        for (PatrolInfoVO queryPatrolData : patrolMerge) {
            String startTimePeriod = queryPatrolData.getStartTimePeriod();
            if(!StringUtils.isEmpty(startTimePeriod) && !startTimePeriod.contains(MAO_HAO)){
                VideoPatrolVO updateVo = new VideoPatrolVO();
                updateVo.setId(queryPatrolData.getId());
                String[] split = startTimePeriod.split(Constants.COMMA);
                StringBuilder stp = new StringBuilder();
                for (String s : split) {
                    for (String string : m) {
                        stp.append(s).append(string).append(Constants.COMMA);
                    }
                }
                updateVo.setStartTimePeriod(stp.substring(0,stp.length()-1));
                updateMergeList.add(updateVo);
            }
        }

        try {
            if(!CollectionUtils.isEmpty(updateList)){
                List<List<VideoPatrolVO>> finalList = Lists.partition(updateList, 5000);
                finalList.forEach(item ->
                        patrolInfoMapper.updatePatrol(item)
                );
            }
            if(!CollectionUtils.isEmpty(updateMergeList)){
                List<List<VideoPatrolVO>> finalList = Lists.partition(updateMergeList, 5000);
                finalList.forEach(item ->
                        patrolInfoMapper.updateMerge(item)
                );
            }
        }catch (Exception e){
            log.error("修复异常",e);
        }

        return Result.success("修复成功！");
    }

    @Override
    public Result<DeviceInfoVO> getPatrolMode(VideoBaseReq patrolDTO){
        return Result.data(patrolInfoMapper.getDeviceInfo(patrolDTO.getDeviceCode()));
    }

    @Override
    public List<Long> patrolAlgoRel(PatrolAlgoRelDTO dto) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String industryCode = user.getIndustryCode();
        String tenantId = user.getTenantId();
        dto.setTenantId(tenantId);
        dto.setIndustryCode(industryCode);
        //根据巡航id和预置位id查询是否绑定指定算法
        return patrolInfoMapper.patrolAlgoRel(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DeviceOrdAuthScope
    public Long addCruisePatrolMCP(AddPatrolReq patrolDTO) {
        //查询摄像机下的可见光通道（通道编码较小的）
        String channelCode = patrolInfoMapper.getMinChannelCode(patrolDTO.getDeviceCode());
        if(!StringUtils.isEmpty(channelCode)){
            patrolDTO.setChannelCode(channelCode);
        }else{
            throw new CustomException("无可用通道!");
        }

        if (checkSamePatrol(null, patrolDTO)) {
            throw new CustomException(PATROL_NAME_REPEAT);
        }

        Long patrolId = System.currentTimeMillis()+ buildRandom(8);

        VideoOrderReq videoOrderReq = new VideoOrderReq();
        videoOrderReq.setTenantId(patrolDTO.getTenantId());
        videoOrderReq.setChannelCode(patrolDTO.getChannelCode());
        videoOrderReq.setDeviceCode(patrolDTO.getDeviceCode());
        videoOrderReq.setIndustryCode(patrolDTO.getIndustryCode());
        Result<VideoOrderResp> resp = videoOrderService.getVideoOrderInfo(videoOrderReq);

        if (!ObjectUtils.isEmpty(resp) && !ObjectUtils.isEmpty(resp.getData())
                && !StringUtils.isEmpty(resp.getData().getOrderCode()) ) {
            String orderCode = resp.getData().getOrderCode();
            patrolDTO.setOrderCode(orderCode);
        } else {
            throw new CustomException("订单信息不存在!");
        }

        PatrolInfoDO patrolInfo = new PatrolInfoDO();
        patrolDTO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        BeanUtils.copyProperties(patrolDTO, patrolInfo);
        patrolInfo.setPatrolId(patrolId);
        patrolInfo.setCreateTime(new Date());
//        patrolInfo.setUpdateTime(new Date());
        patrolInfo.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        // 获取当前的用户
        patrolInfo.setCreator(patrolDTO.getUserName());
//        patrolInfo.setModifier(patrolDTO.getUserName());
        patrolInfo.setStartStop(IndustryConstants.PATROL_INFO_STOP);
        patrolInfo.setIfCruise(IndustryConstants.PATROL_CRUISE_STOP);
        String startTimePeriod = patrolInfo.getStartTimePeriod();
        if (StringUtils.isBlank(startTimePeriod)) {
            patrolInfo.setStartTimePeriod(PATROL_TIME_PERIOD);
        }else if(!startTimePeriod.contains(MAO_HAO)){
            patrolInfo.setStartTimePeriod(this.getNewMinList(startTimePeriod));
        }
        String industryCode = patrolDTO.getIndustryCode();
        String appVerCode = patrolDTO.getAppVerCode();
        patrolInfo.setIndustryCode(industryCode);
        patrolInfo.setAppVerCode(appVerCode);
        patrolInfo.setInitStartTimePeriod(patrolInfo.getStartTimePeriod());

        patrolInfoRepository.save(patrolInfo);
        this.savePatrolPresetRela(patrolInfo, patrolDTO.getPresets());
        return patrolId;
    }

    /**
     * 查询已启动状态的巡航操作人id
     * @param operatorList
     * @return
     */
    public Map<String,String> getUserMap(List<String> operatorList){
        //查询已启动状态的巡航操作人id
        List<SysUserDTO> userIdBatch = videoCommonDAO.getUserIdBatch(operatorList);
        Map<String,String> userMap = new HashMap<>();
        for (SysUserDTO idBatch : userIdBatch) {
            userMap.put(idBatch.getUserName(),idBatch.getUserId());
        }
        return userMap;
    }
}














