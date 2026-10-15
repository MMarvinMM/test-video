package com.chinatower.video.modules.videopreset.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceOptAuthVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;

import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.dao.VideoCommonDAO;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.video.modules.annotation.DeviceOrdAuthScope;
import com.chinatower.video.modules.common.util.ApiInfoUtils;
import com.chinatower.video.modules.common.util.InterfaceLogUtil;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;
import com.chinatower.video.modules.videopreset.mapper.VideoPresetMapper;
import com.chinatower.video.modules.videopreset.service.IGuardInfoService;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName GuardService
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/21
 **/

@Service
@Slf4j
public class GuardInfoService implements IGuardInfoService {

    @Resource
    ApiInfoUtils apiInfoUtils;

    @Resource
    VideoPresetMapper presetMapper;

    @Resource
    VideoControlService videoControlService;

    @Resource
    private NetConnectUtils connectUtils;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    ICasCadeQueryService iCasCadeQueryService;

    @Resource
    private VideoCommonDAO videoCommonDAO;

    private static final String SUCCESS_CODE = "0000";

    private static final String GUARD_ERROR = "设置看守位失败：已有其他用户设置过看守位";
    /**
     * 功能描述: 设置看守卫
     *
     * @param: guardDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/21
     * @version: 1.0
     */
    @Override
    @DeviceOrdAuthScope
    public long doSetGuard(GuardDTO guardDTO, Map<String, String> headerMap) {
        //当前摄像机是否有看守卫
        List<GuardDTO> guardList = presetMapper.queryGuardCount(guardDTO);
        if(CollectionUtils.isNotEmpty(guardList)){
            //判断当前操作租户权限高低，是否可以抢占
            DeviceOptAuthVO optAuthVO = new DeviceOptAuthVO();
            SysUser user = SecurityUtils.getLoginUser().getUser();
            optAuthVO.setTenantId(guardDTO.getTenantId());
            optAuthVO.setDeviceCode(guardDTO.getDeviceCode());
            optAuthVO.setIndustryCode(user.getIndustryCode());
            optAuthVO.setUserId(null);
            optAuthVO.setConfigType(Constants.ONE);
            List<DeviceOptAuthVO> deviceOptAuthVOList = videoCommonDAO.queryDeviceOptAuthInfo(optAuthVO);
            optAuthVO.setUserId(guardDTO.getUserCode());
            optAuthVO.setConfigType(Constants.TWO);
            List<DeviceOptAuthVO> deviceOptAuthVOList1 = videoCommonDAO.queryDeviceOptAuthInfo(optAuthVO);
            //判断当前操作租户权限高低，是否可以抢占
            if(CollectionUtils.isNotEmpty(deviceOptAuthVOList)){
                //判断是同一租户，比较下级用户权限
                if(guardList.get(0).getTenantId().equals(deviceOptAuthVOList.get(0).getTenantId())&& guardList.get(0).getIndustryCode().equals(deviceOptAuthVOList.get(0).getIndustryCode())){
                    if(CollectionUtils.isNotEmpty(deviceOptAuthVOList1)){
                        //如果不是同一个账号，比较权限
                        if(!guardList.get(0).getUserCode().equals(deviceOptAuthVOList1.get(0).getUserId())){
                            optAuthVO.setUserId(guardList.get(0).getUserCode());
                            optAuthVO.setConfigType(Constants.TWO);
                            List<DeviceOptAuthVO> deviceOptAuthVOList2 = videoCommonDAO.queryDeviceOptAuthInfo(optAuthVO);
                            if(CollectionUtils.isNotEmpty(deviceOptAuthVOList2)){
                                if(Integer.parseInt(deviceOptAuthVOList2.get(0).getGuardPriority()) > Integer.parseInt(deviceOptAuthVOList1.get(0).getGuardPriority())){
                                    log.info("用户权限级别低无法设置看守卫");
                                    return -2;
                                }
                            }

                        }
                    }
                }else{
                    //不是同一租户，查询原租户权限
                    optAuthVO.setTenantId(guardList.get(0).getTenantId());
                    optAuthVO.setIndustryCode(guardList.get(0).getIndustryCode());
                    optAuthVO.setUserId(null);
                    optAuthVO.setConfigType(Constants.ONE);
                    List<DeviceOptAuthVO> deviceOptAuthVOList3 = videoCommonDAO.queryDeviceOptAuthInfo(optAuthVO);
                    if(CollectionUtils.isNotEmpty(deviceOptAuthVOList3)){
                        if(Integer.parseInt(deviceOptAuthVOList3.get(0).getGuardPriority()) > Integer.parseInt(deviceOptAuthVOList.get(0).getGuardPriority())){
                            log.info("租户权限级别低无法设置看守卫");
                            return -2;
                        }
                    }
                }
            }else if(CollectionUtils.isNotEmpty(deviceOptAuthVOList1)){
                //如果不是同一个账号，比较权限
                if(!guardList.get(0).getUserCode().equals(deviceOptAuthVOList1.get(0).getUserId())){
                    optAuthVO.setUserId(guardList.get(0).getUserCode());
                    optAuthVO.setConfigType(Constants.TWO);
                    List<DeviceOptAuthVO> deviceOptAuthVOList2 = videoCommonDAO.queryDeviceOptAuthInfo(optAuthVO);
                    if(CollectionUtils.isNotEmpty(deviceOptAuthVOList2)){
                        if(Integer.parseInt(deviceOptAuthVOList2.get(0).getGuardPriority()) > Integer.parseInt(deviceOptAuthVOList1.get(0).getGuardPriority())){
                            log.info("用户权限级别低无法设置看守卫");
                            return -2;
                        }
                    }
                }
            }else{
                int other1 = presetMapper.hasOtherUserGuard(guardDTO);
                if(other1 > 0){
                    return -2;
                }
            }
        }
        long guardResult = 0;
        String tenantId = guardDTO.getTenantId();
        String userCode = guardDTO.getUserCode();
        // 获取token
        VideoBaseControlReq videoBaseControlReq = new VideoBaseControlReq();
        videoBaseControlReq.setDeviceCode(guardDTO.getDeviceCode()); //设备编码
        videoBaseControlReq.setChannelCode(guardDTO.getChannelCode()); //通道编码
        videoBaseControlReq.setTenantId(tenantId); //租户id
        String duration = videoControlService.selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME);
        videoBaseControlReq.setDuration(Integer.parseInt(duration));//有效时长单位秒
        Result<DoLockOrReleasePtzData> lockResut = videoControlService.doLockPtz(videoBaseControlReq,headerMap,false,Constants.FOUR);
        if(lockResut.getCode() != 200 || lockResut.getData() == null || lockResut.getData().getToken() == null){
            return -1;
        }
        String token = lockResut.getData().getToken();
        log.info("调用北向接口增加看守位token:{}",token);
        Result<JSONObject> res = null;
        try {
            NetConnectUtils.RequestParamter<GuardDTO> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(guardDTO.getDeviceCode());
            //属地化数据统一改造 add by wangyt 20240118
            guardDTO.setAcceptNode(casCadeVO.getPlatformCode());
            guardDTO.setAccessNode(casCadeVO.getCascadeToCode());
            // 请求参数
            paramter.setBody(guardDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_SET_GUARD);
            // 接口路径参数
            path.setDeviceCode(guardDTO.getDeviceCode());
            path.setChannelCode(guardDTO.getChannelCode());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.DO_SET_GUARD_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(guardDTO.getTenantId()),
                    NetConnectUtils.HeaderKeyEnum.X_Token.of(token),
                    NetConnectUtils.HeaderKeyEnum.X_UserCode.of(guardDTO.getUserCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter))
            );
            log.info(VideoConstants.DO_SET_GUARD_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
            if (res.getCode() == 200) {
                if("1".equals(guardDTO.getGuardStatus())) {
                    presetMapper.saveGuard(guardDTO);
                    //调基础删除其他看守位
                    List<GuardDTO> otherGuardList = presetMapper.getOtherGuardInfo(guardDTO);
                    if(!CollectionUtils.isEmpty(otherGuardList)){
                        GuardDTO otherGuardInfo = otherGuardList.get(0);
                        Map<String, String> newHeaderMap = new HashMap<>();
                        otherGuardInfo.setTenantId(tenantId);
                        otherGuardInfo.setAcceptNode(guardDTO.getAcceptNode());
                        otherGuardInfo.setAccessNode(guardDTO.getAccessNode());
                        otherGuardInfo.setGuardStatus(Constants.ZERO);
                        otherGuardInfo.setUserCode(guardDTO.getUserCode());
                        doDelGuard(otherGuardInfo,newHeaderMap);
                    }
                    // 将其他看守位信息恢复为预置点
                    presetMapper.resetPreset(guardDTO);
                }else{
                    presetMapper.stopGuard(guardDTO);
                }
                guardResult = 1;
            } else {
                log.info(DeviceConstants.EQUAL_SEPARATOR + new Exception().getStackTrace()[0].getMethodName() + "调用北向接口增加看守位失败");
                throw new com.chinatower.platform.common.exception.CustomException(res.getMsg(), HttpStatus.ERROR_4001);
            }
            return guardResult;
        }catch (Exception e){
            log.error(VideoConstants.DO_SET_GUARD_ERROR +"===:{}", e);
            return guardResult;
        }
    }




    /**
     * 功能描述: 删除看守位
     *
     * @param: guardDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: liuxt
     * @date: 2024/4/12
     * @version: 1.0
     */
    public void doDelGuard(GuardDTO guardDTO, Map<String, String> headerMap) {
        try {
            log.info("删除看守位guardDTO:"+JSONObject.toJSONString(guardDTO));
            String tenantId = guardDTO.getTenantId();
            // 获取token
            VideoBaseControlReq videoBaseControlReq = new VideoBaseControlReq();
            videoBaseControlReq.setDeviceCode(guardDTO.getDeviceCode()); //设备编码
            videoBaseControlReq.setChannelCode(guardDTO.getChannelCode()); //通道编码
            videoBaseControlReq.setTenantId(tenantId); //租户id
            String duration = videoControlService.selectDictDataByType(VideoConstants.PLATFORM_LOCK_TIME);
            videoBaseControlReq.setDuration(Integer.parseInt(duration));//有效时长单位秒
            Result<DoLockOrReleasePtzData> lockResut = videoControlService.doLockPtz(videoBaseControlReq,headerMap,false,Constants.FOUR);
            if(lockResut.getCode() != 200 || lockResut.getData() == null || lockResut.getData().getToken() == null){
                return;
            }
            String token = lockResut.getData().getToken();
            log.info("调用北向接口增加看守位token:{}",token);
            Result<JSONObject> res = null;
            NetConnectUtils.RequestParamter<GuardDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(guardDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_SET_GUARD);
            // 接口路径参数
            path.setDeviceCode(guardDTO.getDeviceCode());
            path.setChannelCode(guardDTO.getChannelCode());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.DO_SET_GUARD_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(guardDTO.getTenantId()),
                    NetConnectUtils.HeaderKeyEnum.X_Token.of(token),
                    NetConnectUtils.HeaderKeyEnum.X_UserCode.of(guardDTO.getUserCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(guardDTO.getAcceptNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(guardDTO.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter))
            );
            log.info(VideoConstants.DO_SET_GUARD_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData2 = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData2.getData());
        }catch (Exception e){
            log.error(VideoConstants.DO_SET_GUARD_ERROR +"===:{}", e);
        }
    }




    /**
     * 6.48.	查询事件告警详情
     * @param videoRequestDTO
     * @return
     * @auther: sutn
     * @date: 2022/3/18
     */
  /*  @Override
    public Result<JSONObject> getAlarmInfo(VideoRequestDTO videoRequestDTO) {
        log.info(InterfaceLogUtil.reqTransLog(new Exception().getStackTrace()[0].getMethodName() + "请求报文:" + JSON.toJSONString(videoRequestDTO)));
        // 调用北向接口
        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_GET_ALARM_INFOL);
        apiInfoDTO.setDeviceCode(videoRequestDTO.getDeviceCode());
        apiInfoDTO.setChannelCode(videoRequestDTO.getChannelCode());

        Result<JSONObject> intfResp = null;
        try {
            NetConnectUtils.RequestParamter<ApiInfoDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(apiInfoDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_ALARM_INFO);
            // 接口路径参数
            path.setDeviceCode(videoRequestDTO.getDeviceCode());
            path.setChannelCode(videoRequestDTO.getChannelCode());
            path.setAlarmId(videoRequestDTO.getAlarmId());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.GET_ALARM_INFO_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class);
            log.info(VideoConstants.GET_ALARM_INFO_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            intfResp = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.GET_ALARM_INFO_ERROR +"===:{}", e);
        }
        if (ResultCode.SUCCESS.getCode() != intfResp.getCode()) {
            return Result.fail(HttpStatus.ERROR_4001, intfResp.getMsg());
        }
        return Result.data(intfResp.getData());
    }*/
    @Override
    public Result<JSONObject> getAlarmInfo(VideoRequestDTO videoRequestDTO){
        // 调用北向接口
        try{
            ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
            apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_GET_ALARM_INFOL);
            apiInfoDTO.setDeviceCode(videoRequestDTO.getDeviceCode());
            apiInfoDTO.setChannelCode(videoRequestDTO.getChannelCode());
            GuardDTO guardDTO = new GuardDTO();
            guardDTO.setDeviceCode(videoRequestDTO.getDeviceCode());
            guardDTO.setChannelCode(videoRequestDTO.getChannelCode());
            NetConnectUtils.RequestParamter<GuardDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(guardDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SERVICE_NAME_GET_ALARM_INFOL);
            // 接口路径参数
            path.setDeviceCode(apiInfoDTO.getDeviceCode());
            path.setChannelCode(apiInfoDTO.getChannelCode());
            path.setAlarmId(videoRequestDTO.getAlarmId());
            paramter.setPath(path);
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(apiInfoDTO.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.DO_SET_GUARD_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info("getAlarmInfo:{}",JSON.toJSONString(cr));
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            return this.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error("getAlarmInfo:",e);
        }
        return Result.fail();
    }

    /**
     * @Description: 判断成功
     * @Author: wangyt
     * @Date: 2023/4/11
     * @param cr
     * @return boolean
     **/
    public static boolean crIsSuccess(ResultEntity cr) {
        return cr != null && DeviceNorthIntfConstants.SUCCESS_STATUS_CODE.equals(cr.getReturnCode());
    }

    /**
     * @Description: 北向接口返回转换
     * @Author: wangyt
     * @Date: 2023/4/11
     * @param cr
     * @param respMsgData
     * @return com.chinatower.common.entity.Result<com.alibaba.fastjson.JSONObject>
     **/
    public static Result<JSONObject> transResult(ResultEntity cr, List<SysDictDataResponse> respMsgData) {
        JSONObject crJson = JSON.parseObject(JSONObject.toJSONString(cr));
        if (crIsSuccess(cr)) {
            return Result.data(crJson);
        }
        else {
            // 转换报错信息
            String errorCode = cr.getErrorCode();
            StringBuffer errorMsg = new StringBuffer();
            if (respMsgData != null && !respMsgData.isEmpty()) {
                for (SysDictDataResponse respMsg : respMsgData) {
                    if (respMsg.getDictValue().equals(errorCode)) {
                        errorMsg.append(respMsg.getDictLabel());
                        break;
                    }
                }
            }
            if (errorMsg.length() == 0) {
                errorMsg.append(cr.getErrorMsg());
            }
            errorMsg.append("_").append(errorCode);
            return Result.fail(errorMsg.toString());
        }
    }
}
