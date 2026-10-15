package com.chinatower.video.modules.videocontrol.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.VideoControlCustomAPI;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.common.dao.VideoCommonDAO;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.VideoCommonUtil;
import com.chinatower.video.modules.videocontrol.dto.Customer;
import com.chinatower.video.modules.videocontrol.dto.LockTokenData;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.service.impl.VideoPatrolCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 视频控制controller
 * Created by zhujun on 2022/1/11.
 */
@Api(value = "视频控制接口-Api",tags = "视频控制请求处理")
@Slf4j
@RestController
public class VideoControlController implements VideoControlCustomAPI {

    @Autowired
    VideoControlService videoControlService;

    @Autowired
    VideoCommonService videoCommonService;

    @Autowired
    private VideoCommonDAO videoCommonDAO;

    /**
     * 启用/停用巡航路径
     * 先获取控制权
     *
     * @param doPatrolReq
     * @return
     */
    @ApiOperation(value = "启用/停用巡航路径",notes = "启用/停用巡航路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doPatrolReq",value = "启用/停用巡航路径请求实体",dataType = "DoPatrolReq",required = true,paramType = "query")
    })
    @Override
    public Result doPatrol(@RequestBody DoPatrolReq doPatrolReq) {
        Result res;
        log.info(VideoConstants.DOPATROL_OP + VideoConstants.CONTROLLERREQ + doPatrolReq);
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.DOPATROL_OP + SecurityUtils.getOperator());
            checkAuth(doPatrolReq);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            // 调用云台转动方法
            res = videoControlService.doPatrol(doPatrolReq,headerMap);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.DOPATROL_ERROR));
            log.error(VideoConstants.DOPATROL_ERROR , e);
        }
        log.info(VideoConstants.DOPATROL_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doPatrolReq) + JSON.toJSONString(res));
        return res;
    }

    /**
     * 获取云台控制权
     * @param videoBaseControlReq
     * @return
     */
    @ApiOperation(value = "获取云台控制权",notes = "获取云台控制权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseControlReq",value = "获取云台控制权请求实体",dataType = "VideoBaseControlReq",required = true,paramType = "query")
    })
    @Override
    public Result doLock(@RequestBody VideoBaseControlReq videoBaseControlReq) {
        Result res;;
        log.info(VideoConstants.LOCKPTZ_OP + VideoConstants.CONTROLLERREQ + videoBaseControlReq);
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.LOCKPTZ_OP + SecurityUtils.getOperator());
            // 校验是否具备设备使用权限
            checkAuth(videoBaseControlReq);
            Map<String, String> headerMap = new HashMap<>();
            videoBaseControlReq.setTenantId(currentUser.getUser().getTenantId());
            videoBaseControlReq.setUserId(currentUser.getUser().getUserId());
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());

            String duration = videoControlService.selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME_996);
            videoBaseControlReq.setDuration(Integer.parseInt(duration));
            res = videoControlService.doLockPtz(videoBaseControlReq,headerMap,false, Constants.ONE);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.DOLOCKORRELEASEPTZ_ERROR));
            log.error(VideoConstants.DOLOCKORRELEASEPTZ_ERROR , e);
        }
        log.info(VideoConstants.DOLOCKORRELEASEPTZ_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(videoBaseControlReq) + JSON.toJSONString(res));
        return res;
    }

    /**
     * 释放云台控制权
     * @param videoBaseControlReq
     * @return
     */
    @ApiOperation(value = "释放云台控制权",notes = "释放云台控制权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseControlReq",value = "释放云台控制权请求实体",dataType = "VideoBaseControlReq",required = true,paramType = "query")
    })
    @Override
    public Result doRelease(@RequestBody VideoBaseControlReq videoBaseControlReq) {
        Result res;
        log.info(VideoConstants.LOCKPTZ_OP + VideoConstants.CONTROLLERREQ + videoBaseControlReq);
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.LOCKPTZ_OP + SecurityUtils.getOperator());
            checkAuth(videoBaseControlReq);
            Map<String, String> headerMap = new HashMap<>();
            videoBaseControlReq.setTenantId(currentUser.getUser().getTenantId());
            videoBaseControlReq.setUserId(currentUser.getUser().getUserId());
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            res = videoControlService.doReleasePtz(videoBaseControlReq,headerMap);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.DOLOCKORRELEASEPTZ_ERROR));
            log.error(VideoConstants.DOLOCKORRELEASEPTZ_ERROR , e);
        }
        log.info(VideoConstants.DOLOCKORRELEASEPTZ_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(videoBaseControlReq) + JSON.toJSONString(res));
        return res;
    }

    /**
     * 开始/结束云台转动
     * 先获取控制权
     *
     * @param doPtzCmdsReq
     * @return
     */
    @ApiOperation(value = "开始/结束云台转动",notes = "开始/结束云台转动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doPtzCmdsReq",value = "开始/结束云台转动请求实体",dataType = "DoPtzCmdsReq",required = true,paramType = "query")
    })
    @Override
    public Result doPtzCmds(@RequestBody DoPtzCmdsReq doPtzCmdsReq) {
        Result res;
        log.info(VideoConstants.PTZCMDS_OP + VideoConstants.CONTROLLERREQ + doPtzCmdsReq);
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.PTZCMDS_OP + SecurityUtils.getOperator());
            // 校验是否具备设备使用权限
            checkAuth(doPtzCmdsReq);
            Map<String, String> headerMap = new HashMap<>();
            doPtzCmdsReq.setTenantId(currentUser.getUser().getTenantId());
            doPtzCmdsReq.setUserId(currentUser.getUser().getUserId());
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN, doPtzCmdsReq.getLockToken());
            headerMap.put(VideoConstants.X_LOCK_TOKEN, doPtzCmdsReq.getLockToken());
            // 调用云台转动方法
            res = videoControlService.doPtzCmds(doPtzCmdsReq,headerMap,true);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.PTZCMDS_ERROR));
            log.error(VideoConstants.PTZCMDS_ERROR , e);
        }
        log.info(VideoConstants.PTZCMDS_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doPtzCmdsReq) + JSON.toJSONString(res));
        return res;
    }

    /**
     * 开始/结束聚焦(focus)/缩放(zoom)
     *
     * @param doCameraFocusOrZoomReq
     * @return
     */
    @ApiOperation(value = "开始/结束聚焦(focus)/缩放(zoom)",notes = "开始/结束聚焦(focus)/缩放(zoom)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doCameraFocusOrZoomReq",value = "开始/结束聚焦(focus)/缩放(zoom)请求实体",dataType = "DoCameraFocusOrZoomReq",required = true,paramType = "query")
    })
    @Override
    public Result doCameraFocusOrZoom(@RequestBody DoCameraFocusOrZoomReq doCameraFocusOrZoomReq) {
        Result res;
        log.info(VideoConstants.FOCUSORZOOM_OP + VideoConstants.CONTROLLERREQ + doCameraFocusOrZoomReq);
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.FOCUSORZOOM_OP + SecurityUtils.getOperator());
            // 校验是否具备设备使用权限
            checkAuth(doCameraFocusOrZoomReq);
            Map<String, String> headerMap = new HashMap<>();
            doCameraFocusOrZoomReq.setTenantId(currentUser.getUser().getTenantId());
            doCameraFocusOrZoomReq.setUserId(currentUser.getUser().getUserId());
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN,doCameraFocusOrZoomReq.getLockToken());
            // 调用云台转动方法
            res = videoControlService.doCameraFocusOrZoom(doCameraFocusOrZoomReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.FOCUSORZOOM_ERROR));
            log.error(VideoConstants.FOCUSORZOOM_ERROR , e);
        }
        log.info(VideoConstants.FOCUSORZOOM_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doCameraFocusOrZoomReq) + JSON.toJSONString(res));
        return res;
    }

    /**
     * 转到指定PTZ位置
     *
     * @param doPtzLocationCmdReq
     * @return
     */
    @ApiOperation(value = "转到指定PTZ位置",notes = "转到指定PTZ位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doPtzLocationCmdReq",value = "转到指定PTZ位置请求实体",dataType = "DoPtzLocationCmdReq",required = true,paramType = "query")
    })
    @Override
    public Result doPtzLocationCmd(@RequestBody DoPtzLocationCmdReq doPtzLocationCmdReq) {
        Result res;
        log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.CONTROLLERREQ + doPtzLocationCmdReq);
        try {
            //此接口只校验订购，不需要校验用户权限，由上游页面控制
            checkNoAuth(doPtzLocationCmdReq);

            Map<String, String> headerMap = new HashMap<>();
            if (StringUtils.isEmpty(doPtzLocationCmdReq.getTenantId())){
                LoginUser currentUser = SecurityUtils.getLoginUser();
                log.info(VideoConstants.PTZLOCATIONCMD_OP + SecurityUtils.getOperator());
                doPtzLocationCmdReq.setTenantId(currentUser.getUser().getTenantId());
                doPtzLocationCmdReq.setUserId(currentUser.getUser().getUserId());
            }else {
                log.info(VideoConstants.PTZLOCATIONCMD_OP + doPtzLocationCmdReq.getTenantId());
                doPtzLocationCmdReq.setTenantId(doPtzLocationCmdReq.getTenantId());
            }
            headerMap.put(VideoConstants.CUSTOMERCODE,doPtzLocationCmdReq.getTenantId());
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN,doPtzLocationCmdReq.getLockToken());
            // 调用转到指定PTZ位置
            res = videoControlService.doPtzLocationCmd(doPtzLocationCmdReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.PTZLOCATIONCMD_ERROR));
            log.error(VideoConstants.PTZLOCATIONCMD_ERROR , e);
        }
        log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doPtzLocationCmdReq) + JSON.toJSONString(res));
        return res;
    }

    /**
     * 转到指定PTZ位置
     *
     * @param doPtzLocationCmdReq
     * @return
     */
    @ApiOperation(value = "转到指定PTZ位置",notes = "转到指定PTZ位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doPtzLocationCmdReq",value = "转到指定PTZ位置请求实体",dataType = "DoPtzLocationCmdReq",required = true,paramType = "query")
    })
    public Result doPtzLocationCmd2(@RequestBody DoPtzLocationCmdReq doPtzLocationCmdReq) {
        Result res;
        log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.CONTROLLERREQ + doPtzLocationCmdReq);
        try {
            // 校验是否具备设备使用权限
            checkAuth(doPtzLocationCmdReq);
            Map<String, String> headerMap = new HashMap<>();
            if (StringUtils.isEmpty(doPtzLocationCmdReq.getTenantId())){
                doPtzLocationCmdReq.setTenantId(doPtzLocationCmdReq.getTenantId());
                doPtzLocationCmdReq.setUserId(doPtzLocationCmdReq.getUserId());
            }else {
                log.info(VideoConstants.PTZLOCATIONCMD_OP + doPtzLocationCmdReq.getTenantId());
                doPtzLocationCmdReq.setTenantId(doPtzLocationCmdReq.getTenantId());
            }
            headerMap.put(VideoConstants.CUSTOMERCODE,doPtzLocationCmdReq.getTenantId());
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN,doPtzLocationCmdReq.getLockToken());
            // 调用转到指定PTZ位置
            res = videoControlService.doPtzLocationCmd(doPtzLocationCmdReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.PTZLOCATIONCMD_ERROR));
            log.error(VideoConstants.PTZLOCATIONCMD_ERROR , e);
        }
        log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doPtzLocationCmdReq) + JSON.toJSONString(res));
        return res;
    }
    
    /**
     * 转到预置点
     *
     * @param doControlPresetReq
     * @return
     */
    @ApiOperation(value = "转到预置点",notes = "转到预置点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doControlPresetReq",value = "转到预置点请求实体",dataType = "DoControlPresetReq",required = true,paramType = "query")
    })
    @Override
    public Result doControlPreset(@RequestBody DoControlPresetReq doControlPresetReq) {
        Result res;
        log.info(VideoConstants.CONTROLPRESET_OP + VideoConstants.CONTROLLERREQ + doControlPresetReq);
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.CONTROLPRESET_OP + SecurityUtils.getOperator());

            //此接口只校验订购，不需要校验用户权限，由上游页面控制
            checkNoAuth(doControlPresetReq);

            Map<String, String> headerMap = new HashMap<>();
            doControlPresetReq.setTenantId(currentUser.getUser().getTenantId());
            doControlPresetReq.setUserId(currentUser.getUser().getUserId());
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN,doControlPresetReq.getLockToken());
            // 调用转到预置点方法
            res = videoControlService.doControlPreset(doControlPresetReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.CONTROLPRESET_ERROR));
            log.error(VideoConstants.CONTROLPRESET_ERROR , e);
        }
        log.info(VideoConstants.CONTROLPRESET_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doControlPresetReq) + JSON.toJSONString(res));
        return res;
    }

    @ApiOperation(value = "开启/关闭灯",notes = "开启/关闭灯")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "playerDoSwitchLightReq",value = "开启/关闭灯请求实体",dataType = "PlayerDoSwitchLightReq",required = true,paramType = "query")
    })
    @Override
    public Result doSwitchLight(@RequestBody PlayerDoSwitchLightReq playerDoSwitchLightReq) {
        Result res;
        log.info(VideoConstants.DO_SWITCH_LIGHT_OP + VideoConstants.CONTROLLERREQ + playerDoSwitchLightReq);
        try{
            // 获取当前用户信息
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.DO_SWITCH_LIGHT_OP + SecurityUtils.getOperator());
            checkAuth(playerDoSwitchLightReq);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            res = videoControlService.doSwitchLight(playerDoSwitchLightReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.DO_SWITCH_LIGHT_ERRO));
            log.error(VideoConstants.DO_SWITCH_LIGHT_ERRO , e);
        }
        return res;
    }

    /**
     * 设置通道参数
     * @param doSetChannelParamReq
     * @return
     */
    @ApiOperation(value = "设置通道参数",notes = "设置通道参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "doSetChannelParamReq",value = "设置通道参数请求实体",dataType = "DoSetChannelParamReq",required = true,paramType = "query")
    })
    @Override
    public Result doSetChannelParam(@RequestBody DoSetChannelParamReq doSetChannelParamReq) {
        Result res;
        log.info(VideoConstants.SETCHANNELPARAM_OP + VideoConstants.CONTROLLERREQ + doSetChannelParamReq);
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.SETCHANNELPARAM_OP + SecurityUtils.getOperator());
            checkAuth(doSetChannelParamReq);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            // 设置通道参数
            res = videoControlService.doSetChannelParam(doSetChannelParamReq,headerMap);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.SETCHANNELPARAM_ERROR));
            log.error(VideoConstants.SETCHANNELPARAM_ERROR , e);
        }
        log.info(VideoConstants.SETCHANNELPARAM_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doSetChannelParamReq) + JSON.toJSONString(res));
        return res;
    }

    private void checkAuth(VideoBaseControlReq v){
        Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(v);
        if(deviceOrderInfo == null){
            // 返回错误 无权限使用
            log.info(VideoConstants.LOCKPTZ + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
            throw new RuntimeException("鉴权失败");
        }else{
            v.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
        }
    }

    private void checkNoAuth(VideoBaseControlReq v){
        Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceNoAuth(v);
        if(deviceOrderInfo == null){
            // 返回错误 无权限使用
            log.info(VideoConstants.LOCKPTZ + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
            throw new RuntimeException("鉴权失败");
        }else{
            v.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
        }
    }


    /**
     * 功能描述: 查询云台锁信息接口
     * @param: videoBaseControlReq
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2022/11/23
     * @version: release_v1.5.30
     */
    @ApiOperation(value = "查询云台锁信息接口",notes = "查询云台锁信息接口")
    @Override
    public Result queryPlatFormLockInfo(@RequestBody VideoBaseControlReq videoBaseControlReq) {
        log.info(VideoConstants.QUERY_PLATFORM_LOCK_INFO + VideoConstants.CONTROLLERREQ + videoBaseControlReq);
        String tokenKey = String.format(VideoConstants.KEY_FOTMAT_CHANNEL_QUEUE, videoBaseControlReq.getDeviceCode(),
                videoBaseControlReq.getChannelCode());
        DoLockOrReleasePtzData lockData = new DoLockOrReleasePtzData();
        if(RedisUtil.hasKey(tokenKey,RedisDBType.PRIMARY)){
            String lockStr = RedisUtil.get(tokenKey, RedisDBType.PRIMARY);
            log.info(VideoConstants.QUERY_PLATFORM_LOCK_INFO + VideoConstants.REDIS_KEY + tokenKey +
                    VideoConstants.REDIS_VALUE + lockStr);
            String lockToken = lockStr.replace(VideoConstants.GATEWAY_LOCKTOKENDATA, VideoConstants.CONTROL_LOCKTOKENDATA);
            LockTokenData lockTokenData = JSONObject.parseObject(lockToken, LockTokenData.class);
            Customer customer = lockTokenData.getCustomer();
            String userId = NullProcessUtil.nvlToString(customer.getUserCode(),"");
            lockData.setUserCode(userId);
            String customerCode = customer.getCustomerCode();
            if (!StringUtils.isEmpty(userId)) {
                SysUser user = SecurityUtils.getLoginUser().getUser();
                String tenantId = user.getTenantId();
                String industryCode = user.getIndustryCode();

                SysUserDTO sysUserDTO = new SysUserDTO();
                sysUserDTO.setUserId(userId);
                SysUserDTO userInfo = videoCommonDAO.getUserInfo(sysUserDTO);
                if(!ObjectUtils.isEmpty(userInfo)){
                    lockData.setUserName(userInfo.getDesensitizeName());
                }else{
                    lockData.setUserName("定**");
                }
                if(!StringUtils.isEmpty(customerCode) && !StringUtils.isEmpty(tenantId) &&
                        tenantId.equals(customerCode) && !userId.equals(user.getUserId())){
                    lockData.setRealUserName(userInfo.getUserName());
                    String deptId = userInfo.getDeptId();
                    if(!StringUtils.isEmpty(deptId)){
                        List<String> deptList = new ArrayList<>();
                        if(deptId.contains(Constants.COMMA)){
                            deptList.addAll(Arrays.stream(deptId.split(Constants.COMMA)).collect(Collectors.toList()));
                        }else{
                            deptList.add(deptId);
                        }
                        lockData.setDeptName(videoCommonDAO.getDeptInfo(deptList,industryCode));
                    }
                }
            }
            lockData.setWaitDuration(RedisUtil.getExpire(tokenKey, TimeUnit.SECONDS, RedisDBType.PRIMARY).intValue());
        }
        return Result.data(lockData);
    }

    @Override
    public Result<String> getControlPriority(@RequestBody VideoBaseControlReq req) {
        return Result.data(videoControlService.getControlPriority(req,"1"));
    }

    @Autowired
    VideoPatrolCommonService videoPatrolCommonService;
    @PostMapping("/video/control/patrolPresetHandle")
    public List<AddPatrolPresetReq> patrolPresetHandle(@RequestBody PatrolInfoVO updatePatrolInfo) {
        return videoPatrolCommonService.patrolPresetHandle(updatePatrolInfo);
    }

    @PostMapping("/video/control/ifPriority")
    public void ifPriority(){
        videoControlService.ifPriority("37010500001327000015+41010200001187000012",
                "410102100000000011180000019","123112888","000000",
                "7b29ca07d89e44efa50fcc84b58cb56a","03e6ad74d2354cbab9bf2b51fdfaa079","3");
        log.info("");
    }

}
