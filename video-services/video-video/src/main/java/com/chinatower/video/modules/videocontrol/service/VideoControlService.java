package com.chinatower.video.modules.videocontrol.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceVideoDTO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceOptAuthVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.entity.ResultEntity;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.VideoCommonUtil;
import com.chinatower.video.modules.query.dao.VideoPtzQueryDAO;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.videocontrol.dto.Customer;
import com.chinatower.video.modules.videocontrol.dto.LockTokenData;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 视频控制服务类
 * Created by zhujun on 2022/1/14.
 */
@Service
@Slf4j
public class VideoControlService{

    public static final String NOT_AUTH_OPERATION_PATROL = "暂无巡航计划操作权限";
    public static final String NOT_AUTH_OPERATION_GUARD = "暂无看守位操作权限";
    public static final String NOT_AUTH_OPERATION_PRESET = "暂无预置位操作权限";
    public static final String NOT_AUTH_OPERATION_CAMERA = "暂无云台控制操作权限";
    @Autowired
    VideoCommonService videoCommonService;
    @Autowired
    IVideoDeviceService iVideoDeviceService;
    @Autowired
    VideoPtzQueryDAO videoPtzQueryDAO;
    @Autowired
    VideoQueryDAO videoQueryDAO;
    @Resource
    private NetConnectUtils connectUtils;
    @Resource
    ICasCadeQueryService iCasCadeQueryService;
    @Autowired
    private SystemFeignClient systemFeignClient;


    /**
     * 获取云台控制权
     * 若redis中存在有效token，则直接使用，否则调用接口申请并放到redis中
     * @param videoBaseControlReq
     * @param headerMap
     * @param useRedis  true:优先使用redis，false:调用接口新申请控制
     * @return
     * type 1:云台 2：预置位 3：巡航 4：看守位
     */
    public Result<DoLockOrReleasePtzData> doLockPtz(VideoBaseControlReq videoBaseControlReq, Map<String, String> headerMap, boolean useRedis,String type) {
        // 返回对象
        Result<DoLockOrReleasePtzData> res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.LOCK_ERROR);
        String notAuthOperation= "";
        // 云台控制请求参数
        Map<String,Object> paramMap = new HashMap<>();
        // redis中放置的token对应key值
        String tokenKey = String.format(VideoConstants.KEY_FOTMAT_CHANNEL_QUEUE, videoBaseControlReq.getDeviceCode(),
                videoBaseControlReq.getChannelCode());
        // 锁类型
        String lockTypeKey = String.format("currentLockType:%s:%s", videoBaseControlReq.getDeviceCode(), videoBaseControlReq.getChannelCode());
        // 控制权的token
        String redisValueObj = "";
        String msg="";
        try {
            //若第三方调用，从缓存去userId报错，取租户管理员的用户ID
            String userId = "";
            String industryCode = "";
            SysUserDTO sysUserDTO = new SysUserDTO();
            try {
                SysUser user = SecurityUtils.getLoginUser().getUser();
                industryCode = user.getIndustryCode();
                log.info("行业编码为：" + industryCode);
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                sysUserDTO.setUserName(user.getUserName());
                userId = videoCommonService.getUserName(sysUserDTO).getUserId();
            } catch (Exception e) {
                sysUserDTO.setIsTenantAdmin(VideoConstants.ONE);
                sysUserDTO.setTenantId(videoBaseControlReq.getTenantId());
                userId = videoCommonService.getUserName(sysUserDTO).getUserId();
                //第三方调用，开放API，默认行业编码为通用
                industryCode = VideoConstants.INDUSTRU_CODE;
            }

            String deviceCode = videoBaseControlReq.getDeviceCode();
            String tenantId = videoBaseControlReq.getTenantId();
            if(!VideoConstants.FROM_WEICHUANG.equals(videoBaseControlReq.getFromSource())){
                //判断当前时间是否在该租户、用户操控时间范围内
                if (!ifCurrentInOperatePeriod(deviceCode,tenantId,industryCode,userId,type)) {
                    if(Constants.ONE.equals(type)){
                        notAuthOperation = NOT_AUTH_OPERATION_CAMERA;
                    }else if(Constants.TWO.equals(type)){
                        notAuthOperation = NOT_AUTH_OPERATION_PRESET;
                    }else if(Constants.THREE.equals(type)){
                        notAuthOperation = NOT_AUTH_OPERATION_PATROL;
                    }else if(Constants.FOUR.equals(type)){
                        notAuthOperation = NOT_AUTH_OPERATION_GUARD;
                    }
                    return Result.fail(notAuthOperation);
                }
            }
            //判断缓存是否存在并获取缓存
            if(RedisUtil.hasKey(tokenKey,RedisDBType.PRIMARY)){
                //取以前的锁类型
                String typeOld =  RedisUtil.get(lockTypeKey,RedisDBType.PRIMARY);
                String lockStr = RedisUtil.get(tokenKey, RedisDBType.PRIMARY);
                log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.REDIS_KEY + tokenKey + VideoConstants.REDIS_VALUE + lockStr);
                String lockToken = lockStr.replace(VideoConstants.GATEWAY_LOCKTOKENDATA, VideoConstants.CONTROL_LOCKTOKENDATA);
                LockTokenData lockTokenData = JSONObject.parseObject(lockToken, LockTokenData.class);
                String oldUser = lockTokenData.getCustomer().getUserCode();
                int time1 = RedisUtil.getExpire(tokenKey, TimeUnit.SECONDS, RedisDBType.PRIMARY).intValue();
                //微创调用
                if(VideoConstants.FROM_WEICHUANG.equals(videoBaseControlReq.getFromSource())){
                    userId = oldUser;
                    headerMap.put(VideoConstants.CUSTOMERCODE,lockTokenData.getCustomer().getCustomerCode());
                    redisValueObj = lockTokenData.getToken();
                }else if(userId.equals(oldUser)){
                    //同一个用户操作
                    userId = oldUser;
                    headerMap.put(VideoConstants.CUSTOMERCODE,lockTokenData.getCustomer().getCustomerCode());
                    redisValueObj = lockTokenData.getToken();
                    //有锁、但锁类型与当前操作不匹配，判断是否是同一账号
                    //1）同一账号，不同操作，释放当前类型锁，给当前操作重新加锁，更新锁类型，锁定时间继承当前时间
                    if(!type.equals(typeOld)){
                        //释放类型锁
                        doReleaseNew(videoBaseControlReq,userId,headerMap,redisValueObj,lockTypeKey,type);
                        // 重新加锁
                        //获取当前过期时间，更新时设置该值为过期时间; 过期时间小于5秒，默认2分钟；否则，沿用之前剩余时间
                        int time = 0;
                        try {
                            time = RedisUtil.getExpire(tokenKey, TimeUnit.SECONDS, RedisDBType.PRIMARY).intValue();
                            time = time < VideoConstants.FIVE ? VideoConstants.ONE_HUNDRED_TWENTY : time;
                        } catch (Exception e) {
                            log.error(VideoConstants.GET_LOCK_TIME_PARSE_ERROR, e);
                            time = VideoConstants.ONE_HUNDRED_TWENTY;
                        }
                        videoBaseControlReq.setDuration(time);
                        dealLockNew(videoBaseControlReq,userId,headerMap,industryCode,type);
                        redisValueObj = videoBaseControlReq.getLockToken();
                    }
                }else {
                    //不同帐号，不同操作不允许操作
                    if(!type.equals(typeOld)){
                        SysUserDTO sysUser = new SysUserDTO();
                        sysUser.setUserId(lockTokenData.getCustomer().getUserCode());
                        SysUserDTO sysUser1 = videoCommonService.getUserName(sysUser);
                        if(!ObjectUtils.isEmpty(sysUser1)){
                            msg = sysUser1.getDesensitizeName() +VideoConstants.CURRENT_PLATFORM_SURPLUS + time1 + VideoConstants.SECOND_STOP;
                        }else{
                            msg = VideoConstants.DEAL_LOCK_REMINDER;
                        }
                        return Result.fail(msg);
                    }
                    //判断当前申请锁用户与目前已经占用锁用户的优先级高低；true：可以进行抢占，直接更新redis中租户用户编码
                    if (ifPriority(deviceCode,videoBaseControlReq.getChannelCode(),tenantId,industryCode,userId,oldUser,type)) {
                        Customer customer = lockTokenData.getCustomer();
                        customer.setCustomerCode(tenantId);
                        customer.setUserCode(userId);
                        lockTokenData.setCustomer(customer);
                        //获取当前过期时间，更新时设置该值为过期时间; 过期时间小于5秒，默认2分钟；否则，沿用之前剩余时间
                        int time = 0;
                        try {
                            time = RedisUtil.getExpire(tokenKey, TimeUnit.SECONDS, RedisDBType.PRIMARY).intValue();
                            time = time < VideoConstants.FIVE ? VideoConstants.ONE_HUNDRED_TWENTY : time;
                        } catch (Exception e) {
                            log.error(VideoConstants.GET_LOCK_TIME_PARSE_ERROR, e);
                            time = VideoConstants.ONE_HUNDRED_TWENTY;
                        }
                        log.info(VideoConstants.LOCK_REDIS_UPDATE + JSON.toJSONString(lockTokenData));
                        RedisUtil.set(tokenKey, lockTokenData, time, RedisDBType.PRIMARY);
                        redisValueObj = lockTokenData.getToken();
                    }
                }
            }

            // 申明需调用接口或无token，执行接口申请
            if(!useRedis || ObjectUtils.isEmpty(redisValueObj)){
                NetConnectUtils.RequestParamter<Map<String,Object>> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数;      获取枚举值配置得云台锁的锁定时间
                paramMap.put(VideoConstants.DURATION, videoBaseControlReq.getDuration());
                paramMap.put(VideoConstants.USERCODE, userId);
                paramter.setBody(paramMap);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_LOCK_OR_RELEASE_PTZ);
                // 接口路径参数
                path.setDeviceCode(videoBaseControlReq.getDeviceCode());
                path.setChannelCode(videoBaseControlReq.getChannelCode());
                path.setControlType(VideoConstants.VALID_STATE);
                paramter.setPath(path);
                //请求头参数
                headerMap.put(VideoConstants.X_DURATION, videoBaseControlReq.getDuration() + "");
                headerMap.put(VideoConstants.X_USERCODE_HEADER, userId);

                //获取登录人云台操控权限级别
                VideoBaseControlReq req = new VideoBaseControlReq();
                req.setTenantId(headerMap.get(VideoConstants.CUSTOMERCODE));
                req.setIndustryCode(industryCode);
                req.setUserId(userId);
                req.setDeviceCode(videoBaseControlReq.getDeviceCode());
                //对应类型信息 type
                String controlPriority = this.getControlPriority(req,type);

                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseControlReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.REQUEST_LOG + paramter);
                DoLockOrReleasePtzData.DoLockOrReleasePtzDataEntity cr = connectUtils.post(paramter, DoLockOrReleasePtzData.DoLockOrReleasePtzDataEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_DURATION.of(videoBaseControlReq.getDuration() + ""),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(userId),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)),
                        NetConnectUtils.HeaderKeyEnum.X_level.of(controlPriority));
                log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.RESPONSE_LOG + cr);
                res = dealLockRes(cr, videoBaseControlReq,type);
            }else{
                videoBaseControlReq.setLockToken(redisValueObj);
                res = Result.success(VideoConstants.SUCCESS_MSG);
            }
            headerMap.put(VideoConstants.X_USERCODE_HEADER, userId);
            headerMap.put(VideoConstants.XTOKEN, videoBaseControlReq.getLockToken());
            headerMap.put(VideoConstants.X_LOCK_TOKEN, videoBaseControlReq.getLockToken());
        } catch (Exception e) {
            log.error(VideoConstants.DO_LOCK_PTZ + VideoConstants.LOCK_ERROR + VideoCommonUtil.getLogData(videoBaseControlReq),e);
        }
        log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.LOCKPTZ_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }

    private Result dealLockNew(VideoBaseControlReq videoBaseControlReq,String userId,Map<String, String> headerMap,String industryCode,String type){
        Result<DoLockOrReleasePtzData> res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.LOCK_ERROR);
        try {
            Map<String,Object> paramMap = new HashMap<>();
            NetConnectUtils.RequestParamter<Map<String,Object>> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数;      获取枚举值配置得云台锁的锁定时间
            paramMap.put(VideoConstants.DURATION, videoBaseControlReq.getDuration());
            paramMap.put(VideoConstants.USERCODE, userId);
            paramter.setBody(paramMap);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_LOCK_OR_RELEASE_PTZ);
            // 接口路径参数
            path.setDeviceCode(videoBaseControlReq.getDeviceCode());
            path.setChannelCode(videoBaseControlReq.getChannelCode());
            path.setControlType(VideoConstants.VALID_STATE);
            paramter.setPath(path);
            //请求头参数
            headerMap.put(VideoConstants.X_DURATION, videoBaseControlReq.getDuration() + "");
            headerMap.put(VideoConstants.X_USERCODE_HEADER, userId);

            //获取登录人云台操控权限级别
            VideoBaseControlReq req = new VideoBaseControlReq();
            req.setTenantId(headerMap.get(VideoConstants.CUSTOMERCODE));
            req.setIndustryCode(industryCode);
            req.setUserId(userId);
            req.setDeviceCode(videoBaseControlReq.getDeviceCode());
            //对应类型信息 type
            String controlPriority = this.getControlPriority(req,type);

            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseControlReq.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.REQUEST_LOG + paramter);
            DoLockOrReleasePtzData.DoLockOrReleasePtzDataEntity cr = connectUtils.post(paramter, DoLockOrReleasePtzData.DoLockOrReleasePtzDataEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                    NetConnectUtils.HeaderKeyEnum.X_DURATION.of(videoBaseControlReq.getDuration() + ""),
                    NetConnectUtils.HeaderKeyEnum.X_UserCode.of(userId),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)),
                    NetConnectUtils.HeaderKeyEnum.X_level.of(controlPriority));
            log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.RESPONSE_LOG + cr);
            res = dealLockRes(cr, videoBaseControlReq,type);
        }catch (Exception e) {
            log.error(VideoConstants.DO_LOCK_PTZ + VideoConstants.LOCK_ERROR + VideoCommonUtil.getLogData(videoBaseControlReq),e);
        }
        return res;
    }


    private Result doReleaseNew(VideoBaseControlReq videoBaseControlReq,String userId,Map<String, String> headerMap,String redisValueObj,String tokenKey,String type){
        // 获取当前用户信息
        LoginUser currentUser = SecurityUtils.getLoginUser();
        // 返回对象
        Result res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.LOCK_ERROR);
        try {
            NetConnectUtils.RequestParamter<Map<String,String>> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数;      获取枚举值配置得云台锁的锁定时间
            headerMap.put(VideoConstants.TOKEN, redisValueObj);
            headerMap.put(VideoConstants.USERCODE, userId);
            paramter.setBody(headerMap);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_LOCK_OR_RELEASE_PTZ);
            // 接口路径参数
            path.setDeviceCode(videoBaseControlReq.getDeviceCode());
            path.setChannelCode(videoBaseControlReq.getChannelCode());
            path.setControlType(VideoConstants.OPERATOR_TYPE);
            paramter.setPath(path);
            //请求头参数
            headerMap.put(VideoConstants.XTOKEN, redisValueObj);
            headerMap.put(VideoConstants.X_USERCODE_HEADER, userId);

            //获取登录人云台操控权限级别
            VideoBaseControlReq req = new VideoBaseControlReq();
            req.setTenantId(headerMap.get(VideoConstants.CUSTOMERCODE));
            req.setIndustryCode(currentUser.getUser().getIndustryCode());
            req.setUserId(userId);
            req.setDeviceCode(videoBaseControlReq.getDeviceCode());
            String controlPriority = this.getControlPriority(req,type);

            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseControlReq.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.DO_RELEASE_PTZ + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                    NetConnectUtils.HeaderKeyEnum.X_UserCode.of(userId),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)),
                    NetConnectUtils.HeaderKeyEnum.X_level.of(controlPriority));
            log.info(VideoConstants.DO_RELEASE_PTZ + VideoConstants.RESPONSE_LOG + cr);
            res = dealReleaseRes(cr, tokenKey);
        } catch (Exception e) {
        log.error(VideoConstants.DO_RELEASE_PTZ + VideoConstants.LOCK_ERROR + VideoCommonUtil.getLogData(videoBaseControlReq),e);
    }
        return res;
    }




    public Result<DoLockOrReleasePtzData> doLockPtzNoRequest(VideoBaseControlReq videoBaseControlReq, Map<String, String> headerMap, boolean useRedis) {
        // 返回对象
        Result<DoLockOrReleasePtzData> res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.LOCK_ERROR);
        // 云台控制请求参数
        Map<String,Object> paramMap = new HashMap<>();
        // redis中放置的token对应key值
        String tokenKey = String.format(VideoConstants.KEY_FOTMAT_CHANNEL_QUEUE, videoBaseControlReq.getDeviceCode(),
                videoBaseControlReq.getChannelCode());
        // 控制权的token
        String redisValueObj = "";
        try {
            String industryCode = videoBaseControlReq.getIndustryCode();
            log.info("行业编码为：" + industryCode);
            String userId = videoBaseControlReq.getUserId();

            String deviceCode = videoBaseControlReq.getDeviceCode();
            String tenantId = videoBaseControlReq.getTenantId();

            //判断当前时间是否在该租户、用户操控时间范围内
            if (!ifCurrentInOperatePeriod(deviceCode,tenantId,industryCode,userId,Constants.TWO)) {
                return Result.fail(NOT_AUTH_OPERATION_PRESET);
            }

            //判断缓存是否存在并获取缓存
            if(RedisUtil.hasKey(tokenKey,RedisDBType.PRIMARY)){
                String lockStr = RedisUtil.get(tokenKey, RedisDBType.PRIMARY);
                log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.REDIS_KEY + tokenKey + VideoConstants.REDIS_VALUE + lockStr);
                String lockToken = lockStr.replace(VideoConstants.GATEWAY_LOCKTOKENDATA, VideoConstants.CONTROL_LOCKTOKENDATA);
                LockTokenData lockTokenData = JSONObject.parseObject(lockToken, LockTokenData.class);
                String oldUser = lockTokenData.getCustomer().getUserCode();
                if (VideoConstants.FROM_WEICHUANG.equals(videoBaseControlReq.getFromSource()) || userId.equals(oldUser)) {
                    userId = oldUser;
                    headerMap.put(VideoConstants.CUSTOMERCODE,lockTokenData.getCustomer().getCustomerCode());
                    log.info("redis中控制锁信息，userId={},tenantId={}",userId,headerMap.get(VideoConstants.CUSTOMERCODE));
                    redisValueObj = lockTokenData.getToken();
                } else {
                    //判断当前申请锁用户与目前已经占用锁用户的优先级高低；true：可以进行抢占，直接更新redis中租户用户编码
                    if (ifPriority(deviceCode,videoBaseControlReq.getChannelCode(),tenantId,industryCode,userId,oldUser,Constants.TWO)) {
                        Customer customer = lockTokenData.getCustomer();
                        customer.setCustomerCode(tenantId);
                        customer.setUserCode(userId);
                        lockTokenData.setCustomer(customer);
                        //获取当前过期时间，更新时设置该值为过期时间; 过期时间小于5秒，默认2分钟；否则，沿用之前剩余时间
                        int time = 0;
                        try {
                            time = RedisUtil.getExpire(tokenKey, TimeUnit.SECONDS, RedisDBType.PRIMARY).intValue();
                            time = time < VideoConstants.FIVE ? VideoConstants.ONE_HUNDRED_TWENTY : time;
                        } catch (Exception e) {
                            log.error(VideoConstants.GET_LOCK_TIME_PARSE_ERROR, e);
                            time = VideoConstants.ONE_HUNDRED_TWENTY;
                        }
                        log.info(VideoConstants.LOCK_REDIS_UPDATE + JSON.toJSONString(lockTokenData));
                        RedisUtil.set(tokenKey, lockTokenData, time, RedisDBType.PRIMARY);
                        redisValueObj = lockTokenData.getToken();
                        log.info(VideoConstants.LOCK_REDIS_UPDATE + "获取新的redis锁信息：" + RedisUtil.get(tokenKey, RedisDBType.PRIMARY));
                    }
                }
            }

            // 申明需调用接口或无token，执行接口申请
            if(!useRedis || ObjectUtils.isEmpty(redisValueObj)){
                NetConnectUtils.RequestParamter<Map<String,Object>> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数;      获取枚举值配置得云台锁的锁定时间
                paramMap.put(VideoConstants.DURATION, videoBaseControlReq.getDuration());
                paramMap.put(VideoConstants.USERCODE, userId);
                paramter.setBody(paramMap);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_LOCK_OR_RELEASE_PTZ);
                // 接口路径参数
                path.setDeviceCode(videoBaseControlReq.getDeviceCode());
                path.setChannelCode(videoBaseControlReq.getChannelCode());
                path.setControlType(VideoConstants.VALID_STATE);
                paramter.setPath(path);
                //请求头参数
                headerMap.put(VideoConstants.X_DURATION, videoBaseControlReq.getDuration() + "");
                headerMap.put(VideoConstants.X_USERCODE_HEADER, userId);

                //获取登录人云台操控权限级别
                VideoBaseControlReq req = new VideoBaseControlReq();
                req.setTenantId(headerMap.get(VideoConstants.CUSTOMERCODE));
                req.setIndustryCode(industryCode);
                req.setUserId(userId);
                req.setDeviceCode(videoBaseControlReq.getDeviceCode());
                String controlPriority = this.getControlPriority(req,"2");

                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseControlReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.REQUEST_LOG + paramter);
                DoLockOrReleasePtzData.DoLockOrReleasePtzDataEntity cr = connectUtils.post(paramter, DoLockOrReleasePtzData.DoLockOrReleasePtzDataEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_DURATION.of(videoBaseControlReq.getDuration() + ""),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(userId),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)),
                        NetConnectUtils.HeaderKeyEnum.X_level.of(controlPriority));
                log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.RESPONSE_LOG + cr);
                res = dealLockRes(cr, videoBaseControlReq,"2");
            }else{
                videoBaseControlReq.setLockToken(redisValueObj);
                res = Result.success(VideoConstants.SUCCESS_MSG);
            }
            headerMap.put(VideoConstants.X_USERCODE_HEADER, userId);
            headerMap.put(VideoConstants.XTOKEN, videoBaseControlReq.getLockToken());
            headerMap.put(VideoConstants.X_LOCK_TOKEN, videoBaseControlReq.getLockToken());
        } catch (Exception e) {
            log.error(VideoConstants.DO_LOCK_PTZ + VideoConstants.LOCK_ERROR + VideoCommonUtil.getLogData(videoBaseControlReq),e);
        }
        log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.LOCKPTZ_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }


    /**
     * 释放云台控制权
     * @param videoBaseControlReq
     * @return
     */
    public Result doReleasePtz(VideoBaseControlReq videoBaseControlReq, Map<String, String> headerMap) {
        // 获取当前用户信息
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if(!ObjectUtils.isEmpty(currentUser)){
            log.info(VideoConstants.DO_RELEASE_PTZ + VideoConstants.DOLOCKORRELEASEPTZ_OP + SecurityUtils.getOperator());
        }
        // 返回对象
        Result res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.LOCK_ERROR);
        // 云台控制请求参数
        Map<String,String> paramMap = new HashMap<>();
        // redis中放置的token对应key值
        String tokenKey = String.format(VideoConstants.KEY_FOTMAT_CHANNEL_QUEUE, videoBaseControlReq.getDeviceCode(),
                videoBaseControlReq.getChannelCode());
        // 控制权的token
        String redisValueObj = "";
        try {
//            String userId = currentUser.getUser().getUserId();
            //若第三方调用，从缓存去userId报错，取租户管理员的用户ID
            String userId = "";
            SysUserDTO sysUserDTO = new SysUserDTO();
            try {
                SysUser user = currentUser.getUser();
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                sysUserDTO.setUserName(user.getUserName());
                userId = videoCommonService.getUserName(sysUserDTO).getUserId();
            } catch (Exception e) {
                sysUserDTO.setIsTenantAdmin(VideoConstants.ONE);
                sysUserDTO.setTenantId(videoBaseControlReq.getTenantId());
                userId = videoCommonService.getUserName(sysUserDTO).getUserId();
            }

            //判断缓存是否存在并获取缓存
            if(RedisUtil.hasKey(tokenKey,RedisDBType.PRIMARY)){
                String lockStr = RedisUtil.get(tokenKey, RedisDBType.PRIMARY);
                log.info(VideoConstants.DO_LOCK_PTZ + VideoConstants.REDIS_KEY + tokenKey + VideoConstants.REDIS_VALUE + lockStr);
                String lockToken = lockStr.replace(VideoConstants.GATEWAY_LOCKTOKENDATA, VideoConstants.CONTROL_LOCKTOKENDATA);
                LockTokenData lockTokenData = JSONObject.parseObject(lockToken, LockTokenData.class);
                if (userId.equals(lockTokenData.getCustomer().getUserCode())) {
                    redisValueObj = lockTokenData.getToken();
                }
            }
            // 无token则不用请求解锁，直接返回成功
            if(ObjectUtils.isEmpty(redisValueObj)) {
                res = Result.success(VideoConstants.SUCCESS_MSG);
            }else{
                NetConnectUtils.RequestParamter<Map<String,String>> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数;      获取枚举值配置得云台锁的锁定时间
                paramMap.put(VideoConstants.TOKEN, redisValueObj);
                paramMap.put(VideoConstants.USERCODE, userId);
                paramter.setBody(paramMap);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_LOCK_OR_RELEASE_PTZ);
                // 接口路径参数
                path.setDeviceCode(videoBaseControlReq.getDeviceCode());
                path.setChannelCode(videoBaseControlReq.getChannelCode());
                path.setControlType(VideoConstants.OPERATOR_TYPE);
                paramter.setPath(path);
                //请求头参数
                headerMap.put(VideoConstants.XTOKEN, redisValueObj);
                headerMap.put(VideoConstants.X_USERCODE_HEADER, userId);

                //获取登录人云台操控权限级别
                VideoBaseControlReq req = new VideoBaseControlReq();
                req.setTenantId(headerMap.get(VideoConstants.CUSTOMERCODE));
                req.setIndustryCode(currentUser.getUser().getIndustryCode());
                req.setUserId(userId);
                req.setDeviceCode(videoBaseControlReq.getDeviceCode());
                String controlPriority = this.getControlPriority(req,"1");

                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseControlReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.DO_RELEASE_PTZ + VideoConstants.REQUEST_LOG + paramter);
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(userId),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)),
                        NetConnectUtils.HeaderKeyEnum.X_level.of(controlPriority));
                log.info(VideoConstants.DO_RELEASE_PTZ + VideoConstants.RESPONSE_LOG + cr);
                res = dealReleaseRes(cr, tokenKey);
            }
        } catch (Exception e) {
            log.error(VideoConstants.DO_RELEASE_PTZ + VideoConstants.LOCK_ERROR + VideoCommonUtil.getLogData(videoBaseControlReq),e);
        }
        log.info(VideoConstants.DO_RELEASE_PTZ + VideoConstants.LOCKPTZ_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }

    /**
     * 启用/停用巡航路径
     *
     * @param doPatrolReq
     * @return
     */
    public Result doPatrol(DoPatrolReq doPatrolReq, Map<String, String> headerMap) {
        Result res;
        // 设置云台每分钟操作用户数
        setControlUserNum(doPatrolReq);
        try{

            NetConnectUtils.RequestParamter<DoPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doPatrolReq.getDeviceCode());
            //属地化数据统一改造 add by wangyt 20240118
            doPatrolReq.setAcceptNode(casCadeVO.getPlatformCode());
            doPatrolReq.setAccessNode(casCadeVO.getCascadeToCode());
            // 请求参数
            paramter.setBody(doPatrolReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_PATROL);
            // 接口路径参数
            path.setDeviceCode(doPatrolReq.getDeviceCode());
            path.setChannelCode(doPatrolReq.getChannelCode());
            paramter.setPath(path);

            // 调用统一网关接口
            log.info(VideoConstants.DOPATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DOPATROL_OP + VideoConstants.RESPONSE_LOG + cr);
            // 启用/停用巡航路径成功
            if (VideoCommonUtil.crIsSuccess(cr)) {
                res = Result.success(VideoConstants.SUCCESS_MSG);
                operatorCount(VideoConstants.OPERATOR_TYPE,doPatrolReq.getDeviceCode());
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsgNew(cr, VideoConstants.DOPATROL_ERROR));
            }
        }catch (Exception e){
            log.error(VideoConstants.GET_VIDEO_REAL_TIME_URL_ERRO + VideoCommonUtil.getLogData(doPatrolReq),e);
            res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.DOPATROL_ERROR);
        }
        log.info(VideoConstants.DOPATROL_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }

    /**
     * 调用接口转动云台
     *
     * @param doPtzCmdsReq
     * @param headerMap
     * @param retryCall 是否允许返回token失效重新再次调用 true:允许;false:不允许.
     * @return
     */
    public Result doPtzCmds(DoPtzCmdsReq doPtzCmdsReq, Map<String, String> headerMap, boolean retryCall) {
        // 先申请权限控制，申请成功后videoControlReq对象中会存入token
        String duration = selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME);
        doPtzCmdsReq.setDuration(Integer.parseInt(duration));
        Result<DoLockOrReleasePtzData> res = doLockPtz(doPtzCmdsReq, headerMap, retryCall,Constants.ONE);
        // 成功申请控制权则开始转动
        if (res.getCode() == ResultCode.SUCCESS.getCode()) {
            // 设置云台每分钟操作用户数
            setControlUserNum(doPtzCmdsReq);
            try{
                NetConnectUtils.RequestParamter<DoPtzCmdsReq> paramter = new NetConnectUtils.RequestParamter<>();
                doPtzCmdsReq.setOperTimestamp(String.valueOf(System.currentTimeMillis()));
                // 请求参数;
                paramter.setBody(doPtzCmdsReq);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_PTZ_CMDS);
                // 接口路径参数
                path.setDeviceCode(doPtzCmdsReq.getDeviceCode());
                path.setChannelCode(doPtzCmdsReq.getChannelCode());
                paramter.setPath(path);

                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doPtzCmdsReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.PTZCMDS_OP + VideoConstants.REQUEST_LOG + paramter);
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(headerMap.get(VideoConstants.XUSERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_LOCK_TOKEN.of(headerMap.get(VideoConstants.X_LOCK_TOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.PTZCMDS_OP + VideoConstants.RESPONSE_LOG + cr);
                // 转动成功
                if (VideoCommonUtil.crIsSuccess(cr)) {
                    res = Result.success(VideoConstants.SUCCESS_MSG);
                    operatorCount(VideoConstants.OPERATOR_TYPE,doPtzCmdsReq.getDeviceCode());
                }else if (VideoCommonUtil.bpTokenError(cr)) {// token失效状态3006
                    if(retryCall){
                        log.info(VideoConstants.AGAIN_TRY_MSG);
                        // 重新调用申请token
                        res = doPtzCmds(doPtzCmdsReq, headerMap, false);
                    }else{
                        res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.PTZCMDS_ERROR));
                    }
                } else {
                    res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.PTZCMDS_ERROR));
                }
            } catch (Exception e) {
                log.error(VideoConstants.PTZCMDS_ERROR + VideoCommonUtil.getLogData(doPtzCmdsReq),e);
                res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.PTZCMDS_ERROR);
            }
        }
        log.info(VideoConstants.PTZCMDS_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }


    /**
     * 开始/结束聚焦(focus)/缩放(zoom)
     *
     * @param doCameraFocusOrZoomReq
     * @param headerMap 请求头信息
     * @param retryCall 是否允许返回token失效重新再次调用 true:允许;false:不允许.
     * @return
     */
    public Result doCameraFocusOrZoom(DoCameraFocusOrZoomReq doCameraFocusOrZoomReq,Map<String, String> headerMap, boolean retryCall) {
        // 先申请权限控制，申请成功后videoControlReq对象中会存入token
        String duration = selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME);
        doCameraFocusOrZoomReq.setDuration(Integer.parseInt(duration));
        Result<DoLockOrReleasePtzData> res = doLockPtz(doCameraFocusOrZoomReq, headerMap, retryCall,Constants.ONE);
        // 成功申请控制权则开始转动
        if (res.getCode() == ResultCode.SUCCESS.getCode()) {
            try {
                NetConnectUtils.RequestParamter<DoCameraFocusOrZoomReq> paramter = new NetConnectUtils.RequestParamter<>();
                doCameraFocusOrZoomReq.setOperTimestamp(String.valueOf(System.currentTimeMillis()));
                // 请求参数
                paramter.setBody(doCameraFocusOrZoomReq);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_CAMERA_FOCUS_OR_ZOOM);
                // 接口路径参数
                path.setDeviceCode(doCameraFocusOrZoomReq.getDeviceCode());
                path.setChannelCode(doCameraFocusOrZoomReq.getChannelCode());
                paramter.setPath(path);
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doCameraFocusOrZoomReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.FOCUSORZOOM_OP + VideoConstants.REQUEST_LOG + paramter);
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(headerMap.get(VideoConstants.XUSERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.FOCUSORZOOM_OP + VideoConstants.RESPONSE_LOG + cr);
                // 聚焦缩放成功
                if (VideoCommonUtil.crIsSuccess(cr)) {
                    res = Result.success(VideoConstants.SUCCESS_MSG);
                    operatorCount(VideoConstants.OPERATOR_TYPE,doCameraFocusOrZoomReq.getDeviceCode());
                }else if (VideoCommonUtil.bpTokenError(cr)) {// token失效状态3006
                    if(retryCall){
                        log.info(VideoConstants.AGAIN_TRY_MSG);
                        // 重新调用申请token
                        res = doCameraFocusOrZoom(doCameraFocusOrZoomReq, headerMap, false);
                    }else{
                        res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.FOCUSORZOOM_ERROR));
                    }
                } else {
                    res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.FOCUSORZOOM_ERROR));
                }
            } catch (Exception e) {
                log.error(VideoConstants.FOCUSORZOOM_ERROR + VideoCommonUtil.getLogData(doCameraFocusOrZoomReq),e);
                res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.FOCUSORZOOM_ERROR);
            }
        }
        log.info(VideoConstants.FOCUSORZOOM_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }

    /**
     * 转到指定PTZ位置
     *
     * @param doPtzLocationCmdReq
     * @param headerMap
     * @param retryCall 是否允许返回token失效重新再次调用 true:允许;false:不允许.
     * @return
     */
    public Result doPtzLocationCmd(DoPtzLocationCmdReq doPtzLocationCmdReq, Map<String, String> headerMap, boolean retryCall) {
        Result<DoLockOrReleasePtzData> res;
        String dictType = null;
        if( VideoConstants.FROM_WEICHUANG.equals(doPtzLocationCmdReq.getFromSource()) ) {
            dictType = VideoConstants.PLATFORM_LOCK_TIME_WEICHUANG;
        } else {
            dictType = VideoConstants.PLATFORM_LOCK_TIME;
        }
        String duration = selectDictDataByType(dictType);
        doPtzLocationCmdReq.setDuration(Integer.parseInt(duration));
        res = doLockPtz(doPtzLocationCmdReq, headerMap, retryCall,Constants.TWO);

        // 先申请权限控制，申请成功后videoControlReq对象中会存入token
        // 成功申请控制权则开始转动
        if (res.getCode() == ResultCode.SUCCESS.getCode()) {
            try {
                // ptz 逆转换参数
                paramTransReverse(doPtzLocationCmdReq);

                NetConnectUtils.RequestParamter<DoPtzLocationCmdReq> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(doPtzLocationCmdReq);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_PTZ_LOCATION_CMD);
                // 接口路径参数
                path.setDeviceCode(doPtzLocationCmdReq.getDeviceCode());
                path.setChannelCode(doPtzLocationCmdReq.getChannelCode());
                paramter.setPath(path);
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doPtzLocationCmdReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.REQUEST_LOG + paramter);
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(headerMap.get(VideoConstants.XUSERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.RESPONSE_LOG + cr);

                // 转到指定PTZ位置成功
                if (VideoCommonUtil.crIsSuccess(cr)) {
                    res = Result.success(VideoConstants.SUCCESS_MSG);
                    operatorCount(VideoConstants.OPERATOR_TYPE,doPtzLocationCmdReq.getDeviceCode());
                }else if (VideoCommonUtil.bpTokenError(cr)) {// token失效状态3006
                    if(retryCall){
                        log.info(VideoConstants.AGAIN_TRY_MSG);
                        // 重新调用申请token
                        res = doPtzLocationCmd(doPtzLocationCmdReq, headerMap, false);
                    }else{
                        res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.PTZLOCATIONCMD_ERROR));
                    }
                } else {
                    res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.PTZLOCATIONCMD_ERROR));
                }
            } catch (Exception e) {
                log.error(VideoConstants.PTZLOCATIONCMD_ERROR + VideoCommonUtil.getLogData(doPtzLocationCmdReq),e);
                res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.PTZLOCATIONCMD_ERROR);
            }
        }
        log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }

    /**
     * ptz逆转换
     * @param doPtzLocationCmdReq
     */
    private void paramTransReverse(DoPtzLocationCmdReq doPtzLocationCmdReq) {
        VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
        videoPtzQueryReq.setDeviceCode(doPtzLocationCmdReq.getDeviceCode());
        DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
        if(StringUtils.isEmpty(doPtzLocationCmdReq.getPtzIsEsc())){
            doPtzLocationCmdReq.setPan(Float.valueOf(LongLatAngleUtils.paramTransReverse(String.valueOf(doPtzLocationCmdReq.getPan()), deviceModel, "P")));
            doPtzLocationCmdReq.setTilt(Float.valueOf(LongLatAngleUtils.paramTransReverse(String.valueOf(doPtzLocationCmdReq.getTilt()), deviceModel, "T")));
            doPtzLocationCmdReq.setZoom(Float.valueOf(LongLatAngleUtils.paramTransReverse(String.valueOf(doPtzLocationCmdReq.getZoom()), deviceModel, "Z")));
        }else{
            log.info("不转义ptz，直接就是入参ptz");
        }
    }

    public Result doControlPreset(DoControlPresetReq doControlPresetReq, PresetDTO presetDTO, boolean retryCall) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,presetDTO.getTenantId());
        return this.doControlPreset(doControlPresetReq, headerMap, retryCall);
    }
    /**
     * 转到预置点
     *
     * @param doControlPresetReq
     * @param headerMap
     * @param retryCall 是否允许返回token失效重新再次调用 true:允许;false:不允许.
     * @return
     */
    public Result doControlPreset(DoControlPresetReq doControlPresetReq, Map<String, String> headerMap, boolean retryCall) {
        Result<DoLockOrReleasePtzData> res = null;
        try{
            String duration = selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME);
            doControlPresetReq.setDuration(Integer.parseInt(duration));
            // 先申请权限控制，申请成功后videoControlReq对象中会存入token
            res = doLockPtz(doControlPresetReq, headerMap, retryCall,Constants.TWO);
            // 成功申请控制权则开始转动
            if (res.getCode() == ResultCode.SUCCESS.getCode()) {
                NetConnectUtils.RequestParamter<DoControlPresetReq> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(doControlPresetReq);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_CONTROL_PRESET);
                // 接口路径参数
                path.setDeviceCode(doControlPresetReq.getDeviceCode());
                path.setChannelCode(doControlPresetReq.getChannelCode());
                paramter.setPath(path);

                //属地化数据统一改造 add by wangyt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doControlPresetReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
                DoControlPresetReq.DoControlPresetReqA cr = connectUtils.post(paramter, DoControlPresetReq.DoControlPresetReqA.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(headerMap.get(VideoConstants.XUSERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + paramter);

                // 转到预置点成功
                if (VideoCommonUtil.crIsSuccess(cr)) {
                    res = Result.success(VideoConstants.PRESET_SUCCESS_MSG);
                    operatorCount(VideoConstants.OPERATOR_TYPE,doControlPresetReq.getDeviceCode());
                } else if (VideoCommonUtil.bpTokenError(cr)) { // token失效状态3006
                    if(retryCall){
                        log.info(VideoConstants.AGAIN_TRY_MSG);
                        // 重新调用申请token
                        res = doControlPreset(doControlPresetReq, headerMap, false);
                    }else{
                        res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.CONTROLPRESET_ERROR));
                    }
                } else {
                    res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.CONTROLPRESET_ERROR));
                }
            }
        } catch (Exception e) {
            log.error(VideoConstants.CONTROLPRESET_ERROR + VideoCommonUtil.getLogData(doControlPresetReq) + e.getMessage());
        }

        log.info(VideoConstants.CONTROLPRESET_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }

    public Result doControlPresetNoRequest(DoControlPresetReq doControlPresetReq, Map<String, String> headerMap, boolean retryCall) {
        Result<DoLockOrReleasePtzData> res = null;
        try{
            String duration = selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME);
            doControlPresetReq.setDuration(Integer.parseInt(duration));
            // 先申请权限控制，申请成功后videoControlReq对象中会存入token
            res = doLockPtzNoRequest(doControlPresetReq, headerMap, retryCall);
            // 成功申请控制权则开始转动
            if (res.getCode() == ResultCode.SUCCESS.getCode()) {
                NetConnectUtils.RequestParamter<DoControlPresetReq> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(doControlPresetReq);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_CONTROL_PRESET);
                // 接口路径参数
                path.setDeviceCode(doControlPresetReq.getDeviceCode());
                path.setChannelCode(doControlPresetReq.getChannelCode());
                paramter.setPath(path);
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doControlPresetReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
                DoControlPresetReq.DoControlPresetReqA cr = connectUtils.post(paramter, DoControlPresetReq.DoControlPresetReqA.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_UserCode.of(headerMap.get(VideoConstants.XUSERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Token.of(headerMap.get(VideoConstants.XTOKEN)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + paramter);

                // 转到预置点成功
                if (VideoCommonUtil.crIsSuccess(cr)) {
                    res = Result.success(VideoConstants.PRESET_SUCCESS_MSG);
                    operatorCount(VideoConstants.OPERATOR_TYPE,doControlPresetReq.getDeviceCode());
                } else if (VideoCommonUtil.bpTokenError(cr)) { // token失效状态3006
                    if(retryCall){
                        log.info(VideoConstants.AGAIN_TRY_MSG);
                        // 重新调用申请token
                        res = doControlPresetNoRequest(doControlPresetReq, headerMap, false);
                    }else{
                        res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.CONTROLPRESET_ERROR));
                    }
                } else {
                    res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.CONTROLPRESET_ERROR));
                }
            }
        } catch (Exception e) {
            log.error(VideoConstants.CONTROLPRESET_ERROR + VideoCommonUtil.getLogData(doControlPresetReq) + e.getMessage());
        }

        log.info(VideoConstants.CONTROLPRESET_OP + VideoConstants.RESPONSE_LOG + res);
        return res;
    }


    /**
     * 开启关闭灯doSwitchLight
     *
     * @param playerDoSwitchLightReq
     * @return
     */
    public Result doSwitchLight(PlayerDoSwitchLightReq playerDoSwitchLightReq,Map<String, String> headerMap) {
        Result<DoLockOrReleasePtzData> res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.DO_SWITCH_LIGHT_ERRO);
        // 先申请权限控制，申请成功后videoControlReq对象中会存入token
        String duration = selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME);
        playerDoSwitchLightReq.setDuration(Integer.parseInt(duration));
        res = doLockPtz(playerDoSwitchLightReq, headerMap, false, Constants.ONE);
        // 成功申请控制权
        if (res.getCode() == ResultCode.SUCCESS.getCode()) {
            try{
                NetConnectUtils.RequestParamter<PlayerDoSwitchLightReq> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(playerDoSwitchLightReq);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_SWITCH_LIGHT);
                // 接口路径参数
                path.setDeviceCode(playerDoSwitchLightReq.getDeviceCode());
                path.setChannelCode(playerDoSwitchLightReq.getChannelCode());
                paramter.setPath(path);
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(playerDoSwitchLightReq.getDeviceCode());
                // 调用统一网关接口
                log.info(VideoConstants.DO_SWITCH_LIGHT_OP + VideoConstants.REQUEST_LOG + paramter);
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.DO_SWITCH_LIGHT_OP + VideoConstants.RESPONSE_LOG + cr);

                // 开启关闭灯成功
                if (VideoCommonUtil.crIsSuccess(cr)) {
                    res = Result.success(VideoConstants.SUCCESS_MSG);
                    operatorCount(VideoConstants.OPERATOR_TYPE,playerDoSwitchLightReq.getDeviceCode());
                } else {
                    res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.DO_SWITCH_LIGHT_ERRO));
                }
            } catch (Exception e) {
                log.error(VideoConstants.DO_SWITCH_LIGHT_ERRO + VideoCommonUtil.getLogData(playerDoSwitchLightReq));
            }
        }
        return res;
    }

    /**
     * 设置通道参数doSetChannelParam
     *
     * @param doSetChannelParamReq
     * @return
     */
    public Result doSetChannelParam(DoSetChannelParamReq doSetChannelParamReq,Map<String, String> headerMap) {
        Result res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.DO_SET_CHANNEL_PARAM_ERRO);
        try{
            NetConnectUtils.RequestParamter<DoSetChannelParamReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(doSetChannelParamReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_SET_CHANNEL_PARAM);
            // 接口路径参数
            path.setDeviceCode(doSetChannelParamReq.getDeviceCode());
            path.setChannelCode(doSetChannelParamReq.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doSetChannelParamReq.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.DO_SET_CHANNEL_PARAM_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DO_SET_CHANNEL_PARAM_OP + VideoConstants.RESPONSE_LOG + cr);

            // 开启关闭灯成功
            if (VideoCommonUtil.crIsSuccess(cr)) {
                res = Result.success(VideoConstants.SUCCESS_MSG);
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.DO_SET_CHANNEL_PARAM_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.DO_SET_CHANNEL_PARAM_ERRO + VideoCommonUtil.getLogData(doSetChannelParamReq));
        }
        return res;
    }
    /**
     * 设置云台每分钟操作用户数
     *
     * @param videoBaseControlReq
     */
    public void setControlUserNum(VideoBaseControlReq videoBaseControlReq) {
        String time = DateUtils.dateTimeNow().substring(0, 12);
        String key = VideoConstants.VIDEO_OPERATOR_NUM + "_" + time + "_" + videoBaseControlReq.getDeviceCode();
        long num = 0;
        if(RedisUtil.get(key, RedisDBType.PRIMARY) != null){
            num = Long.valueOf(RedisUtil.get(key, RedisDBType.PRIMARY));
        }
        RedisUtil.set(key, num + 1, 11 * 60L, RedisDBType.PRIMARY);
    }

    /**
     * 处理获取云台控制权接口返回数据
     *      应用不落缓存，取运管落缓存的值
     * @param cr
     * @return
     */
    private Result dealLockRes(DoLockOrReleasePtzData.DoLockOrReleasePtzDataEntity cr, VideoBaseControlReq videoBaseControlReq,String type) {
        // 返回对象
        Result res;
        // 加锁成功
        if (VideoCommonUtil.crIsSuccess(cr) && !ObjectUtils.isEmpty(cr.getData()) && cr.getData().getLock() == VideoConstants.ONE) {
            res = Result.data(cr.getData());
            videoBaseControlReq.setLockToken(cr.getData().getToken());
            // 锁类型对应的key值
            String lockTypeKey = String.format("currentLockType:%s:%s", videoBaseControlReq.getDeviceCode(), videoBaseControlReq.getChannelCode());
            RedisUtil.set(lockTypeKey,type,120,RedisDBType.PRIMARY);
        }else if(VideoCommonUtil.crIsSuccess(cr) && !ObjectUtils.isEmpty(cr.getData()) && cr.getData().getLock() == VideoConstants.ZERO){
            String currentExecuteUserCode = cr.getData().getCurrentExecuteUserCode();
            if (!ObjectUtils.isEmpty(currentExecuteUserCode)) {
                SysUserDTO sysUserDTO = new SysUserDTO();
                sysUserDTO.setUserId(currentExecuteUserCode);
                SysUserDTO sysUser = videoCommonService.getUserName(sysUserDTO);
                if(!ObjectUtils.isEmpty(sysUser)){
                    res = Result.data(HttpStatus.NO_CONTENT, cr.getData(), sysUser.getDesensitizeName() +
                            VideoConstants.CURRENT_PLATFORM_SURPLUS + cr.getData().getWaitDuration() + VideoConstants.SECOND_STOP);
                }else{
                    res = Result.data(HttpStatus.NO_CONTENT, cr.getData(), VideoConstants.DEAL_LOCK_REMINDER);
                }
            } else {
                res = Result.data(HttpStatus.NO_CONTENT, cr.getData(), VideoConstants.DEAL_LOCK_REMINDER);
            }
        } else {
            res = Result.fail(HttpStatus.NO_CONTENT,VideoConstants.ERROR_PRE + VideoConstants.LOCK_ERROR);
        }
        return res;
    }

    /**
     * 释放云台控制权接口返回数据
     * 若成功返回token，将redis中token释放
     *
     * @param cr
     * @param tokenKey
     * @return
     */
    private Result dealReleaseRes(ResultEntity cr, String tokenKey) {
        // 返回对象
        Result res;
        // 释放控制成功
        if (VideoCommonUtil.crIsSuccess(cr)) {
            RedisUtil.delete(tokenKey, RedisDBType.PRIMARY);
            res = Result.success(VideoConstants.SUCCESS_MSG);
        } else {
            res = Result.fail(HttpStatus.NO_CONTENT, VideoConstants.ERROR_PRE + VideoConstants.OPERATE_FAIL);
        }
        return res;
    }

    public void operatorCount(String type,String deviceCode){
        DeviceVideoDTO deviceVideoDTO = new DeviceVideoDTO();
        if ("1".equals(type)){
            deviceVideoDTO.setVideoType(DeviceConstants.VIDEO_OPERATOR_NUM);
        }else {
            deviceVideoDTO.setVideoType(DeviceConstants.VIDEO_PLAY_NUM);
        }
        deviceVideoDTO.setDeviceCode(deviceCode);
        log.info(VideoConstants.OPERATORCOUNT + deviceVideoDTO.toString());
        iVideoDeviceService.updateDeviceUserNum(deviceVideoDTO);
    }


    /**
     * 功能描述: 查询灭举止配置的云台锁锁定时间
     * @param: dictType
     * @return: String
     * @auther: zhaozj
     * @date: 2022/11/23
     * @version: release_v1.5.30
     */
    public String selectDictDataByType(String dictType) {
        QueryGetEnumInfoReq enumVO = new QueryGetEnumInfoReq();
        enumVO.setDictType(dictType);
        List<QueryGetEnumInfoReq> list = videoQueryDAO.getEnumInfo(enumVO);
        return list.get(0).getDictValue();
    }


    /**
     * 功能描述: 判断当前时间是否在该租户、用户操控时间范围内共同方法提取
     * @param: deviceCode
     * @return: boolean
     * @auther: zhaozj
     * @date: 2023/08/03
     * @version: release_v1.5.30
     * type 1:云台 2：预置位 3：巡航 4：看守位
     */
    public boolean ifCurrentCommon(DeviceOptAuthVO vo,String type) {
        boolean resFlag = true;
        List<DeviceOptAuthVO> list = videoQueryDAO.queryDeviceOptAuthInfo(vo);
        log.info(VideoConstants.IF_CURRENT_OPERATE_PERIOD_COMMON + JSON.toJSONString(list));
        if (!CollectionUtils.isEmpty(list) && !ObjectUtils.isEmpty(list.get(VideoConstants.ZERO))) {
            DeviceOptAuthVO deviceOptAuthVO = list.get(VideoConstants.ZERO);
            String period = "";
            String status = "";

            if(Constants.ONE.equals(type)){
                status = deviceOptAuthVO.getCameraStatus();
                period = deviceOptAuthVO.getOperatePeriod();
            }
            if(Constants.TWO.equals(type)){
                status = deviceOptAuthVO.getPresetStatus();
                period = deviceOptAuthVO.getPresetOperatePeriod();
            }
            if(Constants.THREE.equals(type)){
                status = deviceOptAuthVO.getPatrolStatus();
                period = deviceOptAuthVO.getPatrolOperatePeriod();
            }
            if(Constants.FOUR.equals(type)){
                status = deviceOptAuthVO.getGuardStatus();
                period = deviceOptAuthVO.getGuardOperatePeriod();
            }
            //0 开启 1关闭
            if(Constants.ONE.equals(status)){
                return false;
            }

            try {
                List<String> operatePeriodList = JSONArray.parseArray(period,String.class);
                String nowTimeStr = DateUtils.getTime();
                String startTimeStr = DateUtils.getDate() + VideoConstants.BLANK_SPACE + operatePeriodList.get(VideoConstants.ZERO);
                String endTimeStr = DateUtils.getDate() + VideoConstants.BLANK_SPACE + operatePeriodList.get(VideoConstants.ONE);
                resFlag = DateUtils.isEffectiveDateHMS(nowTimeStr, startTimeStr, endTimeStr);
            } catch (Exception e) {
                log.error(VideoConstants.TIME_PARSE_ERROR, e);
            }
        }else if(StringUtils.isNotEmpty(vo.getUserId())){
            //在验证历史数据合规性
            List<String> hisUserAuth = videoQueryDAO.queryHisAuth(vo.getUserId());
            if(CollectionUtil.isNotEmpty(hisUserAuth)){
                if(hisUserAuth.contains(type)){
                    resFlag =false;
                }
            }
        }
        return resFlag;
    }

    /**
     * 功能描述: 判断当前时间是否在该租户、用户操控时间范围内
     *          true   当前时间在该租户、用户操控时间范围内
     *          false  当前时间不在该租户、用户操控时间范围内
     * @param: deviceCode
     * @return: boolean
     * @auther: zhaozj
     * @date: 2023/08/03
     * @version: release_v1.5.30
     * type 1:云台 2：预置位 3：巡航 4：看守位
     */
    public boolean ifCurrentInOperatePeriod(String deviceCode,String tenantId,String industryCode,String userId,String type) {
        boolean resFlag = true;
        DeviceOptAuthVO vo = new DeviceOptAuthVO();
        vo.setDeviceCode(deviceCode);
        vo.setTenantId(tenantId);
        vo.setIndustryCode(industryCode);
        vo.setConfigType(VideoConstants.OPERATOR_TYPE);
        type = StringUtils.isEmpty(type) ? Constants.ONE : type;
        //判断该用户所属的租户是否在操作范围内
        resFlag = ifCurrentCommon(vo,type);

        //如果客户符合时间段要求，再判断用户的时间段是否符合
        if (resFlag) {
            vo.setUserId(userId);
            vo.setConfigType(VideoConstants.TWO_STR);
            resFlag = ifCurrentCommon(vo,type);

        }

        return resFlag;
    }


    /**
     * 功能描述: 判断当前申请锁用户与目前已经占用锁用户的优先级高低
     *          true   当前申请锁用户优先级比目前已经占用锁用户高
     *          false  当前申请锁用户优先级没有目前已经占用锁用户高
     * @param: deviceCode
     * @return: boolean
     * @auther: zhaozj
     * @date: 2023/08/03
     * @version: release_v1.5.30
     */
    public boolean ifPriority(String deviceCode,String channelCode,String tenantId,String industryCode,String userId,String oldUser,String type) {
        boolean resFlag = false;
        try {
            DeviceOptAuthVO vo = new DeviceOptAuthVO();
            vo.setUserId(oldUser);
            //获取目前占用锁用户的租户编码与行业编码
            List<DeviceOptAuthVO> oldUserList = videoQueryDAO.queryTenantIndustryByUser(vo);
            DeviceOptAuthVO userVO = new DeviceOptAuthVO();
            //oldUserList为空，说明是定时任务加锁存的token
            if(CollectionUtils.isEmpty(oldUserList)){
                userVO.setUserType(Constants.ONE);
                userVO.setUserName(null);
            }else {
                userVO = oldUserList.get(0);
            }

            //如果申请锁用户与目前占用锁用户属于统一租户行业，比较租户下用户的优先级高低
            if (VideoConstants.TWO_STR.equals(userVO.getUserType()) && tenantId.equals(userVO.getTenantId()) && industryCode.equals(userVO.getIndustryCode())) {
                vo.setDeviceCode(deviceCode);
                vo.setTenantId(tenantId);
                vo.setIndustryCode(industryCode);
                vo.setConfigType(VideoConstants.TWO_STR);
                vo.setUserId(userId);
                //获取申请锁用户A优先级信息，list1
                List<DeviceOptAuthVO> list1 = videoQueryDAO.queryDeviceOptAuthInfo(vo);
                vo.setUserId(oldUser);
                //获取目前占用锁用户B优先级信息，list2
                List<DeviceOptAuthVO> list2 = videoQueryDAO.queryDeviceOptAuthInfo(vo);
                log.info(VideoConstants.IF_PRIORITY_QUERY_QESPONSE + JSON.toJSONString(list1) + VideoConstants.IF_PRIORITY_QUERY_QESPONSE_AND + JSON.toJSONString(list2));
                //云台操作
                if(type.equals(Constants.ONE)){
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getPriority()) ?
                                Integer.valueOf(list1.get(0).getPriority()) > Integer.valueOf(list2.get(0).getPriority()) :
                                (!Constants.SEVEN.equals(list1.get(0).getPriority()) && !Constants.FOUR.equals(list1.get(0).getPriority())
                                        && !Constants.ONE.equals(list1.get(0).getPriority()));
                    }
                }else if(type.equals(Constants.TWO)){
                    // 预置位操作
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPresetPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getPresetPriority()) ?
                                Integer.valueOf(list1.get(0).getPresetPriority()) > Integer.valueOf(list2.get(0).getPresetPriority()) :
                                (!Constants.SEVEN.equals(list1.get(0).getPresetPriority()) && !Constants.FOUR.equals(list1.get(0).getPresetPriority())
                                        && !Constants.ONE.equals(list1.get(0).getPresetPriority()));
                    }
                }else if(type.equals(Constants.THREE)){
                    // 巡航计划操作
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPatrolPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getPatrolPriority()) ?
                                Integer.valueOf(list1.get(0).getPatrolPriority()) > Integer.valueOf(list2.get(0).getPatrolPriority()) :
                                (!Constants.SEVEN.equals(list1.get(0).getPatrolPriority()) && !Constants.FOUR.equals(list1.get(0).getPatrolPriority())
                                        && !Constants.ONE.equals(list1.get(0).getPatrolPriority()));
                    }
                }else if(type.equals(Constants.FOUR)){
                    // 看守卫操作
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getGuardPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getGuardPriority()) ?
                                Integer.valueOf(list1.get(0).getGuardPriority()) > Integer.valueOf(list2.get(0).getGuardPriority()) :
                                (!Constants.SEVEN.equals(list1.get(0).getGuardPriority()) && !Constants.FOUR.equals(list1.get(0).getGuardPriority())
                                        && !Constants.ONE.equals(list1.get(0).getGuardPriority()));
                    }
                }

            } else if (VideoConstants.TWO_STR.equals(userVO.getUserType())) {
                //否则，比较所属租户的优先级高低
                vo.setDeviceCode(deviceCode);
                vo.setTenantId(tenantId);
                vo.setIndustryCode(industryCode);
                vo.setConfigType(VideoConstants.OPERATOR_TYPE);
                vo.setUserId("");
                //获取申请锁租户A优先级信息，list1
                List<DeviceOptAuthVO> list1 = videoQueryDAO.queryDeviceOptAuthInfo(vo);
                vo.setTenantId(userVO.getTenantId());
                vo.setIndustryCode(userVO.getIndustryCode());
                //获取目前占用锁租户B优先级信息，list2
                List<DeviceOptAuthVO> list2 = videoQueryDAO.queryDeviceOptAuthInfo(vo);
                log.info(VideoConstants.IF_PRIORITY_QUERY_QESPONSE + JSON.toJSONString(list1) + VideoConstants.IF_PRIORITY_QUERY_QESPONSE_AND + JSON.toJSONString(list2));
                if(type.equals(Constants.ONE)){
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getPresetPriority()) ?
                                Integer.valueOf(list1.get(0).getPresetPriority()) > Integer.valueOf(list2.get(0).getPresetPriority()) :
                                Integer.valueOf(list1.get(0).getPresetPriority()) > 3;
                    }
                }else if(type.equals(Constants.TWO)){
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPresetPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getPresetPriority()) ?
                                Integer.valueOf(list1.get(0).getPresetPriority()) > Integer.valueOf(list2.get(0).getPresetPriority()) :
                                Integer.valueOf(list1.get(0).getPresetPriority()) > 3;
                    }
                }else if(type.equals(Constants.THREE)){
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPatrolPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getPatrolPriority()) ?
                                Integer.valueOf(list1.get(0).getPatrolPriority()) > Integer.valueOf(list2.get(0).getPatrolPriority()) :
                                Integer.valueOf(list1.get(0).getPatrolPriority()) > 3;
                    }
                }else if(type.equals(Constants.FOUR)){
                    if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getGuardPriority())) {
                        resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getGuardPriority()) ?
                                Integer.valueOf(list1.get(0).getGuardPriority()) > Integer.valueOf(list2.get(0).getGuardPriority()) :
                                Integer.valueOf(list1.get(0).getGuardPriority()) > 3;
                    }
                }
            } else {
                //占用云台锁的用户为铁塔账号
                String userName = userVO.getUserName();
                List<QueryGetEnumInfoReq> dictInfo = new ArrayList<>();
                if(!StringUtils.isEmpty(userName)){
                    //判断当前铁塔用户是否配置摄像机操作权限铁塔账号白名单
                    QueryGetEnumInfoReq enumVO = new QueryGetEnumInfoReq();
                    enumVO.setDictType(VideoConstants.DEVICE_OPERATE_AUTH);
                    enumVO.setDictValue(userName);
                    dictInfo = videoQueryDAO.getEnumInfo(enumVO);
                }
                if (CollectionUtils.isEmpty(dictInfo)) {
                    vo.setDeviceCode(deviceCode);
                    vo.setTenantId(tenantId);
                    vo.setIndustryCode(industryCode);
                    vo.setConfigType(VideoConstants.TWO_STR);
                    vo.setUserId(userId);
                    //判断该用户所属的租户是否在操作范围内
                    List<DeviceOptAuthVO> list1 = videoQueryDAO.queryDeviceOptAuthInfo(vo);

                    //userName为空，说明是定时任务加锁
                    List<QueryGetEnumInfoReq> list2 = new ArrayList<>();
                    if(StringUtils.isEmpty(userName)){
                        QueryGetEnumInfoReq dictInfoReq = new QueryGetEnumInfoReq();
                        //查询占用锁的定时任务操作级别
                        List<String> lockLevelList = videoQueryDAO.getLockLevel(deviceCode, channelCode);
                        if(!CollectionUtils.isEmpty(lockLevelList)){
                            if(lockLevelList.size() > Constants.ONE_NUM){
                                throw new CustomException("执行中任务数量异常，{}",lockLevelList.size());
                            }
                        }else{
                            throw new CustomException("执行中任务数量为0");
                        }
                        dictInfoReq.setDictValue(lockLevelList.get(Constants.ZERO_NUM));
                        list2.add(dictInfoReq);
                    }else{
                        // 判断当前占用锁的铁塔用户是否配置在“铁塔系统配置-字典配置”
                        QueryGetEnumInfoReq enumVOTower = new QueryGetEnumInfoReq();
                        enumVOTower.setDictType(VideoConstants.DEVICE_OPERATE_AUTH_TOWER);
                        enumVOTower.setDictLabelEqual(userName);
                        list2 = videoQueryDAO.getEnumInfo(enumVOTower);
                    }

                    log.info(VideoConstants.IF_PRIORITY_QUERY_QESPONSE + JSON.toJSONString(list1) + VideoConstants.IF_PRIORITY_QUERY_QESPONSE_AND + JSON.toJSONString(list2));

                    if(type.equals(Constants.ONE)){
                        if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPriority())) {
                            resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getDictValue()) ?
                                    Integer.valueOf(list1.get(0).getPriority()) > Integer.valueOf(list2.get(0).getDictValue()) :
                                    Integer.valueOf(list1.get(0).getPriority()) > VideoConstants.ONE;
                        }
                        else
                        {
                            vo.setConfigType(VideoConstants.OPERATOR_TYPE);
                            vo.setUserId("");
                            list1 = videoQueryDAO.queryDeviceOptAuthInfo(vo);
                            if(!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPriority())){
                                resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getDictValue()) ?
                                        Integer.valueOf(list1.get(0).getPriority()) - 2 > Integer.valueOf(list2.get(0).getDictValue()) :
                                        Integer.valueOf(list1.get(0).getPriority()) - 2 > VideoConstants.ONE;
                            }
                        }
                    } else if (type.equals(Constants.TWO)) {
                        if (!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPresetPriority())) {
                            resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getDictValue()) ?
                                    Integer.valueOf(list1.get(0).getPresetPriority()) > Integer.valueOf(list2.get(0).getDictValue()) :
                                    Integer.valueOf(list1.get(0).getPresetPriority()) > VideoConstants.ONE;
                        }
                        else
                        {
                            vo.setConfigType(VideoConstants.OPERATOR_TYPE);
                            vo.setUserId("");
                            list1 = videoQueryDAO.queryDeviceOptAuthInfo(vo);
                            if(!CollectionUtils.isEmpty(list1) && StringUtils.isNotEmpty(list1.get(0).getPresetPriority())){
                                resFlag = !CollectionUtils.isEmpty(list2) && StringUtils.isNotEmpty(list2.get(0).getDictValue()) ?
                                        Integer.valueOf(list1.get(0).getPresetPriority()) - 2 > Integer.valueOf(list2.get(0).getDictValue()) :
                                        Integer.valueOf(list1.get(0).getPresetPriority()) - 2 > VideoConstants.ONE;
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            log.error(VideoConstants.PRIORITY_CONPARE_ERROR, e);
        }
        return resFlag;
    }

    public String getControlPriority(VideoBaseControlReq req,String type){
        String userPriority = Constants.ONE;

        String userId = req.getUserId();
        String industryCode = req.getIndustryCode();
        try {
            //铁塔
            if(Constants.CORE_TOWER_INDUSTRY_CODE.equals(industryCode)) {
                String userName = videoQueryDAO.getUserName(userId);
                Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(VideoConstants.DEVICE_OPERATE_AUTH_TOWER);
                if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
                    List<SysDictDataResponse> data = sysResult.getData();
                    if(!CollectionUtils.isEmpty(data)){
                        for (SysDictDataResponse datum : data) {
                            if(datum.getDictLabel().equals(userName)){
                                userPriority = datum.getDictValue();
                                break;
                            }
                        }
                    }
                }
            }
            //客户
            else{
                DeviceOptAuthVO vo = new DeviceOptAuthVO();
                vo.setUserId(userId);
                vo.setDeviceCode(req.getDeviceCode());
                vo.setTenantId(req.getTenantId());
                vo.setIndustryCode(req.getIndustryCode());
                vo.setConfigType(Constants.TWO);
                // 根据类型判断获取什么级别
                if(type.equals("1")){
                    userPriority = videoQueryDAO.getControlPriority(vo);
                }else if(type.equals("2")){
                    userPriority = videoQueryDAO.getControlPresetPriority(vo);
                }else if(type.equals("3")){
                    userPriority = videoQueryDAO.getControlPatrolPriority(vo);
                }else if(type.equals("4")){
                    userPriority = videoQueryDAO.getControlGuardPriority(vo);
                }
                if(StringUtils.isEmpty(userPriority)){
                    vo.setConfigType(Constants.ONE);
                    vo.setUserId(null);
                    String tenantPriority ="";
                    // 根据类型判断获取什么级别
                    if(type.equals("1")){
                        tenantPriority = videoQueryDAO.getControlPriority(vo);
                    }else if(type.equals("2")){
                        tenantPriority = videoQueryDAO.getControlPresetPriority(vo);
                    }else if(type.equals("3")){
                        tenantPriority = videoQueryDAO.getControlPatrolPriority(vo);
                    }else if(type.equals("4")){
                        tenantPriority = videoQueryDAO.getControlGuardPriority(vo);
                    }
                    if(StringUtils.isEmpty(tenantPriority)){
                        tenantPriority = Constants.THREE;
                    }
                    switch (tenantPriority){
                        case Constants.THREE:
                            userPriority = Constants.ONE;
                            break;
                        case Constants.SIX:
                            userPriority = Constants.FOUR;
                            break;
                        case Constants.NINE:
                            userPriority = Constants.SEVEN;
                            break;
                        default:
                            userPriority = Constants.ONE;
                            break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取登录人云台操控权限级别异常",e);
        }
        return userPriority;
    }
}
