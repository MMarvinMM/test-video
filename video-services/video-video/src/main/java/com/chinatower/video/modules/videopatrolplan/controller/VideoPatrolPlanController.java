package com.chinatower.video.modules.videopatrolplan.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.common.dao.VideoCommonDAO;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolInfoDO;
import com.chinatower.video.modules.videopatrolplan.dto.PatrolAlgoRelDTO;
import com.chinatower.video.modules.videopatrolplan.mapper.PatrolInfoMapper;
import com.chinatower.video.modules.videopatrolplan.repository.IPatrolInfoRepository;
import com.chinatower.video.modules.videopatrolplan.service.IVideoPatrolPlanInfoService;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoBatchService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author zheng.yan
 * @date 2022/1/18
 */
@RestController
@Slf4j
@Api(value = "视频巡航路径模块",tags = "视频巡航路径模块")
public class VideoPatrolPlanController {

    public static final String NOT_AUTH_OPERATION_PATROL = "暂无巡航计划操作权限";
    @Autowired
    private IVideoPatrolPlanInfoService videoPatrolPlanInfoService;

    @Autowired
    private IPatrolInfoRepository patrolInfoRepository;

    @Resource
    PatrolInfoMapper patrolInfoMapper;

    @Autowired
    private IVideoRecordPlanInfoBatchService videoRecordPlanInfoBatchService;

    @Autowired
    VideoCommonService videoCommonService;
    @Resource
    VideoCommonDAO videoCommonDAO;
    @Autowired
    VideoControlService videoControlService;

    /**
     * 增加巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "增加巡航路径", notes = "增加巡航路径")
    @PostMapping("/video/patrol/addCruisePatrol")
    public Result<String> addCruisePatrol(@Validated @RequestBody AddPatrolReq patrolDTO) {
        log.info("addCruisePatrol" + VideoConstants.CONTROLLERREQ + patrolDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        patrolDTO.setIndustryCode(currentUser.getIndustryCode());
        patrolDTO.setAppVerCode(currentUser.getAppVerCode());
        patrolDTO.setUserName(SecurityUtils.getOperator());
        String tenantId = currentUser.getTenantId();
        String industryCode = currentUser.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(currentUser.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                patrolDTO.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.THREE)) {
            return Result.fail(NOT_AUTH_OPERATION_PATROL);
        }
        videoPatrolPlanInfoService.addCruisePatrol(patrolDTO);
        return Result.success("增加巡航路径成功!");
    }

    @ApiOperation(value = "增加巡航路径mcp使用", notes = "增加巡航路径")
    @PostMapping("/video/patrol/addCruisePatrolMCP")
    public Result<Long> addCruisePatrolMCP(@Validated @RequestBody AddPatrolReq patrolDTO) {
        log.info("addCruisePatrol" + VideoConstants.CONTROLLERREQ + patrolDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        patrolDTO.setIndustryCode(currentUser.getIndustryCode());
        patrolDTO.setAppVerCode(currentUser.getAppVerCode());
        patrolDTO.setUserName(SecurityUtils.getOperator());
        String tenantId = currentUser.getTenantId();
        String industryCode = currentUser.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(currentUser.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                patrolDTO.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.THREE)) {
            return Result.fail(NOT_AUTH_OPERATION_PATROL);
        }
        Long l = videoPatrolPlanInfoService.addCruisePatrolMCP(patrolDTO);
        return Result.data(l);
    }



    /**
     * 删除巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "删除巡航路径", notes = "删除巡航路径")
    @PostMapping("/video/patrol/delPatrol")
    public Result<String> delPatrol(@Validated @RequestBody DelPatrolReq patrolDTO) {
        log.info("delPatrol" + VideoConstants.CONTROLLERREQ + patrolDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        String tenantId = currentUser.getTenantId();
        String industryCode = currentUser.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(currentUser.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                patrolDTO.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.THREE)) {
            return Result.fail(NOT_AUTH_OPERATION_PATROL);
        }
        VideoPatrolVO videoPatrolVO = videoPatrolPlanInfoService.getPatrol(patrolDTO);
        if( VideoConstants.TWO_STR.equals(videoPatrolVO.getStartStop()) )
        {
        	// 该巡航计划正在启动中，无法删除，请巡航结束后再试。
        	return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.DEL_PATROL_ERR);
        }
        videoPatrolPlanInfoService.delPatrol(patrolDTO);
        return Result.success("删除巡航路径成功!");
    }

    /**
     * 批量删除巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "批量删除巡航路径", notes = "批量删除巡航路径")
    @PostMapping("/video/patrol/batchDelPatrolNew")
    public Result<String> batchDelPatrolNew(@RequestBody DelPatrolReq patrolDTO) {
        log.info("batchDelPatrolNew" + VideoConstants.CONTROLLERREQ + patrolDTO);
        if(CollectionUtils.isEmpty(patrolDTO.getPatrolIdList())){
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.PATROL_ID_NULL_ERR);
        }
        List<VideoPatrolVO> videoPatrolVOList = videoPatrolPlanInfoService.getPatrolByIdList(patrolDTO);
        boolean flag = false;
        for (VideoPatrolVO vo:videoPatrolVOList ) {
            if(VideoConstants.TWO_STR.equals(vo.getStartStop())) {
                flag = true;
                break;
            }
        }
        if(flag){
            // 该巡航计划正在启动中，无法删除，请巡航结束后再试。
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.DEL_PATROL_STATUS_ERR);
        }

        videoPatrolPlanInfoService.batchDelPatrolNew(patrolDTO);
        return Result.success("删除巡航路径成功!");
    }

    /**
     * 批量删除巡航路径
     * @param: patrolDTO
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/2/25
     * @version:
     */
    @ApiOperation(value = "批量删除巡航路径", notes = "批量删除巡航路径")
    @PostMapping("/video/patrol/batchDelPatrol")
    public Result<String> batchDelPatrol(@Validated @RequestBody BatchDelPatrolReq patrolDTO) {
        log.info("batchDelPatrol" + VideoConstants.CONTROLLERREQ + patrolDTO);
        videoPatrolPlanInfoService.batchDelPatrol(patrolDTO);
        return Result.success("批量删除巡航路径成功!");
    }

    /**
     * 修改巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "修改巡航路径", notes = "修改巡航路径")
    @PostMapping("/video/patrol/updatePatrol")
    public Result<String> updatePatrol(@Validated @RequestBody ModPatrolReq patrolDTO) {
        log.info("updatePatrol" + VideoConstants.CONTROLLERREQ + JSONObject.toJSON(patrolDTO));
        if(patrolDTO.getPatrolName().length() > DeviceConstants.TWENTY){
            return Result.fail(DeviceConstants.NO_MORE_THAN_TWENTY);
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        String tenantId = currentUser.getTenantId();
        String industryCode = currentUser.getIndustryCode();
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(currentUser.getUserName());
        SysUserDTO userVO = videoCommonDAO.getUserName(sysUserDTO);
        String userId = userVO.getUserId();
        if (!videoControlService.ifCurrentInOperatePeriod(
                patrolDTO.getDeviceCode(),
                tenantId,
                industryCode,
                userId,
                Constants.THREE)) {
            return Result.fail(NOT_AUTH_OPERATION_PATROL);
        }
        patrolDTO.setIndustryCode(currentUser.getIndustryCode());
        patrolDTO.setAppVerCode(currentUser.getAppVerCode());
        patrolDTO.setUserName(SecurityUtils.getOperator());
        videoPatrolPlanInfoService.updatePatrol(patrolDTO);
        return Result.success("修改巡航路径成功!");
    }

    /**
     * 停用/启用巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "停用/启用巡航路径", notes = "停用/启用巡航路径")
    @PostMapping("/video/patrol/doPatrol")
    public Result<String> doPatrol(@Validated @RequestBody SttPatrolReq patrolDTO) {
        log.info("doPatrol" + VideoConstants.CONTROLLERREQ + patrolDTO);
        Result<String> result = videoPatrolPlanInfoService.doPatrol(patrolDTO);
        if(ResultCode.SUCCESS.getCode() == result.getCode() && null != patrolDTO.getPatrolId()){
            String ifCruise = "";
            // 查询巡航执行状态
            DelPatrolReq delPatrolReq = new DelPatrolReq();
            delPatrolReq.setPatrolId(patrolDTO.getPatrolId());
            VideoPatrolVO videoPatrolVO = videoPatrolPlanInfoService.getPatrol(delPatrolReq);
            if(null != videoPatrolVO){
                if(StringUtils.isNotBlank(videoPatrolVO.getIfCruise())){
                    ifCruise = "1".equals(videoPatrolVO.getIfCruise()) ? "0" : "1";
                }

            }
            result.setData(ifCruise);
        }

        return result;
    }

    /**
     * 批量停用/启用巡航路径
     *
     * @param patrolDTOList
     * @return
     */
    @ApiOperation(value = "批量停用/启用巡航路径", notes = "批量停用/启用巡航路径")
    @PostMapping("/video/patrol/batchDoPatrol")
    public Result<List<Map<String, String>>> batchDoPatrol(@RequestBody List<SttPatrolReq> patrolDTOList) {
        log.info("batchDoPatrol" + VideoConstants.CONTROLLERREQ + JSONObject.toJSONString(patrolDTOList));
        List<Map<String, String>> result = videoRecordPlanInfoBatchService.batchDoPatrol(patrolDTOList);
        return Result.data(result);
    }


    /**
     * 查询订单巡航路径
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @ApiOperation(value = "查询订单巡航路径", notes = "查询订单巡航路径")
    @PostMapping("/video/patrol/queryOrderPatrolInfo")
    public Result<List<PatrolDTO>> queryOrderPatrolInfo(@RequestBody QueryOrderDeviceSetDTO presetDTO) {
        log.info("queryOrderPatrolInfo" + VideoConstants.CONTROLLERREQ + presetDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        presetDTO.setIndustryCode(currentUser.getIndustryCode());
        List<PatrolDTO> list = videoPatrolPlanInfoService.queryOrderPatrolInfo(presetDTO);
        return Result.data(list);
    }


    /**
     * 功能描述: 查询巡航路径列表
     *
     * @param queryPatrolReq
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/28
     * @version: 1.0
     */
    @ApiOperation(value = "查询巡航路径列表", notes = "查询巡航路径列表")
    @PostMapping("/video/patrol/queryPatrol")
    public Result<JSONObject> queryPatrol(@RequestBody VideoQueryPatrolData queryPatrolReq) {
        log.info("queryPatrol" + VideoConstants.CONTROLLERREQ + queryPatrolReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        queryPatrolReq.setIndustryCode(currentUser.getIndustryCode());
        queryPatrolReq.setTenantId(currentUser.getTenantId());
        JSONObject json = videoPatrolPlanInfoService.queryPatrol(queryPatrolReq);
        return Result.data(json);
    }

    public static final String PATROL_PLAN_DEFAULT_NAME = "巡航计划";

    @PostMapping("/video/patrol/getDefPatrolName")
    public Result<String> getDefPatrolName(@RequestBody QueryPatrolReq queryPatrolReq) {
        log.info("getDefPatrolName" + VideoConstants.CONTROLLERREQ + queryPatrolReq);
        LambdaQueryWrapper<PatrolInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatrolInfoDO::getChannelCode, queryPatrolReq.getChannelCode());
        queryWrapper.eq(PatrolInfoDO::getDeviceCode, queryPatrolReq.getDeviceCode());
        queryWrapper.eq(PatrolInfoDO::getTenantId, queryPatrolReq.getTenantId());
        queryWrapper.eq(PatrolInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        int patrolCount = patrolInfoRepository.count(queryWrapper);
        return Result.data(PATROL_PLAN_DEFAULT_NAME + (patrolCount > 1 ? patrolCount : 1));
    }

    /**
     * 整合巡航路径
     *
     * @param vo
     * @return
     */
    @ApiOperation(value = "整合巡航路径", notes = "整合巡航路径")
    @PostMapping("/video/patrol/mergePatrol")
    public Result<String> mergePatrol(@RequestBody PatrolInfoVO vo) {
        log.info("mergePatrol" + VideoConstants.CONTROLLERREQ + vo);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        vo.setIndustryCode(user.getIndustryCode());
        videoPatrolPlanInfoService.mergePatrol(vo);
        return Result.success("整合巡航路径成功!");
    }

    /**
     * 定时任务调度整合巡航路径
     *
     * @param
     * @return
     */
    @ApiOperation(value = "定时任务调度整合巡航路径", notes = "定时任务调度整合巡航路径")
    @PostMapping("/video/patrol/mergePatrolHandler")
    @XxlJob("mergePatrolHandler")
    public ReturnT<String> mergePatrolHandler(String param) {
        //获取真实的巡航时段
        String hour = videoCommonService.getActualHour();
        log.info("执行mergePatrolHandler定时任务开始：时: " + hour);
        PatrolInfoVO patrolInfoVO = new PatrolInfoVO();
        patrolInfoVO.setStartTimePeriod(hour);
        //需要重新整合的设备
        List<PatrolInfoVO> patrolList = patrolInfoMapper.getStartPatrolInfoList(patrolInfoVO);
        log.info("执行mergePatrolHandler数量: " + patrolList.size());

        //删除该逻辑，防止出现异常应用基础双方数据不一致
//        //将需要停用的巡航基础表数据停用
//        patrolInfoMapper.updateStopPatrolInfo(patrolInfoVO);

        //判断是否开启AR巡航，已经开启AR巡航的不启用
        List<PatrolInfoVO> collect = new ArrayList<>();
        List<String> patrolInfoVOS = patrolInfoMapper.queryARCruisePlan(new SttPatrolReq());
        if (!CollectionUtils.isEmpty(patrolInfoVOS)) {
            for (PatrolInfoVO infoVO : patrolList) {
                if (!patrolInfoVOS.contains(infoVO.getDeviceCode())) {
                    collect.add(infoVO);
                }
            }
        } else {
            collect = patrolList;
        }

        for(PatrolInfoVO pVO :collect){
            try {
                videoPatrolPlanInfoService.mergePatrol(pVO);
            } catch (CustomException e) {
                log.error("CustomException:" + e.getMessage(), e);
            } catch (Exception e) {
                log.error("Exception:" + e.getMessage(), e);
            }
        }
        log.info("执行mergePatrolHandler定时任务结束！条数：" + (CollectionUtils.isEmpty(collect)?"0":collect.size()));
        return ReturnT.SUCCESS;
    }


    /**
     * 暂停恢复巡航计划接口
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "暂停恢复巡航路径", notes = "暂停恢复巡航路径")
    @PostMapping("/video/patrol/pauseResumePatrol")
    public Result pauseResumePatrol(@Validated @RequestBody SttPatrolPauseReq patrolDTO) {
        return videoPatrolPlanInfoService.pauseResumePatrol(patrolDTO);
    }

    /**
     * 查询巡航计划暂停恢复信息接口
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "查询巡航计划暂停恢复信息接口", notes = "查询巡航计划暂停恢复信息接口")
    @PostMapping("/video/patrol/queryPauseResumePatrolInfo")
    public Result queryPauseResumePatrolInfo(@Validated @RequestBody VideoBaseReq patrolDTO) {
        log.info(VideoConstants.QUERY_PAUSE_RESUME_PATROL_INFO + VideoConstants.CONTROLLERREQ + JSON.toJSONString(patrolDTO));

        //根据摄像机编码查询实际配置的巡航通道
        String realChannelCode = patrolInfoMapper.getRealChannelCode(patrolDTO.getDeviceCode());
        patrolDTO.setChannelCode(realChannelCode);
        log.info("realChannelCode=" + realChannelCode);

        String key = VideoConstants.PAUSERESUME_A_ +patrolDTO.getDeviceCode() + VideoConstants.UNDERLINE_STR
                + patrolDTO.getChannelCode();
        if (RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
            return Result.data(RedisUtil.getExpire(key, TimeUnit.SECONDS, RedisDBType.PRIMARY));
        } else {
            return Result.fail(VideoConstants.OPERATE_ERROR_NOT_EXIST_PATROL);
        }
    }

    /**
     * 查询已占用的巡航时段
     * @param patrolDTO
     * @return
     */
    @PostMapping("/video/patrol/getExistTimePeriod")
    public Result<List<String>> getExistTimePeriod(@RequestBody VideoBaseReq patrolDTO) {
        return Result.data(videoPatrolPlanInfoService.getExistTimePeriod(patrolDTO));
    }

    /**
     * 修复巡航历史数据的start_time_period字段
     * 例：00,01，更新为：00:00,00:10,00:20,00:30,00:40,00:50,01:00,01:10,01:20,01:30,01:40,01:50
     * @param
     * @return
     */
    @PostMapping("/video/patrol/updateStartTimePeriod")
    public Result<String> updateStartTimePeriod() {
        return videoPatrolPlanInfoService.updateStartTimePeriod();
    }

    /**
     * 按设备查询巡航模式
     * @param
     * @return
     */
    @PostMapping("/video/patrol/getPatrolMode")
    public Result<DeviceInfoVO> getPatrolMode(@RequestBody VideoBaseReq patrolDTO) {
        return videoPatrolPlanInfoService.getPatrolMode(patrolDTO);
    }

    /**
     * 返回关联了算法的巡航计划
     * @param
     * @return
     */
    @PostMapping("/video/patrol/patrolAlgoRel")
    public Result<List<Long>> patrolAlgoRel(@RequestBody PatrolAlgoRelDTO dto) {
        return Result.data(videoPatrolPlanInfoService.patrolAlgoRel(dto));
    }

}
