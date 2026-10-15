package com.chinatower.event.controller;

import com.chinatower.common.core.dto.AlarmType;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.entity.dto.IotAlarmConfigDTO;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.repository.IAlarmTypeClassService;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.repository.IotAlarmConfigService;
import com.chinatower.event.vo.IotAlarmConfigRequest;
import com.chinatower.event.vo.IotAlarmConfigVo;
import com.chinatower.event.vo.IotDeviceInfoVo;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * video-event
 * 物联告警配置controller
 * @author lishihao
 * @date 2022-7-20
 * @requestID 物联告警配置（铁塔&客户）
 */
@RestController
@RequestMapping("/iotAlarmConfig")
@Slf4j
public class IotAlarmConfigController extends BaseController {

    @Autowired
    private IotAlarmConfigService iotAlarmConfigService;
    @Autowired
    private ISysService sysService;

    @Autowired
    private SysMapper sysMapper;

    @Autowired
    private IAlarmTypeClassService iAlarmTypeClassService;
    /**
     * 功能描述：分页查询物联网告警配置信息
     * @author lishihao
     * @param dto
     * @return Result<Object>
     * @date 2022-7-20
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/queryIotAlarmConfig")
    Result<Object> queryIotAlarmConfig(@RequestBody IotAlarmConfigDTO dto){
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null){
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(dto.getTenantId())){
            dto.setTenantId(loginUser.getUser().getTenantId());
        }
        return iotAlarmConfigService.queryIotAlarmConfigList(dto);
    }

    /**
     * 功能描述：新增物联告警配置
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-20
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/insertIotAlarmConfig")
    Result<Object> insertIotAlarmConfig(@RequestBody IotAlarmConfigVo iotAlarmConfigVo){
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = sysMapper.selectUserOnlyByUserId(loginUser.getUser().getUserId());
        if (loginUser == null || loginUser.getUser() == null){
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(iotAlarmConfigVo.getTenantId())){
            iotAlarmConfigVo.setTenantId(user.getTenantId());
        }
        if (StringUtils.isEmpty(iotAlarmConfigVo.getUserId())){
            iotAlarmConfigVo.setUserId(user.getUserId());
        }
        if (StringUtils.isEmpty(iotAlarmConfigVo.getAppVerCode())){
            iotAlarmConfigVo.setAppVerCode(user.getAppVerCode());
        }
        if (StringUtils.isEmpty(iotAlarmConfigVo.getIndustryCode())){
            iotAlarmConfigVo.setIndustryCode(user.getIndustryCode());
        }
        iotAlarmConfigVo.setCreator(user.getUserName());
        Result<Object> result;
        try {
            result = iotAlarmConfigService.insertIotAlarmConfig(iotAlarmConfigVo);
            //同步修改告警等级
            this.updateAlarmLevel(iotAlarmConfigVo);
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"新增物联告警配置异常"+e);
        }
        return result;
    }

    /**
     * 功能描述：编辑物联告警配置
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/updateIotAlarmConfig")
    Result<Object> updateIotAlarmConfig(@RequestBody IotAlarmConfigVo iotAlarmConfigVo){

        Result<Object> result;
        try {
            result = iotAlarmConfigService.updateIotAlarmConfig(iotAlarmConfigVo);
            //同步修改告警等级
            this.updateAlarmLevel(iotAlarmConfigVo);
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"编辑物联告警配置异常:"+e);
        }
        return result;
    }

    //修改告警等级
    public void updateAlarmLevel(IotAlarmConfigVo iotAlarmConfigVo){
        AlarmTypeClassDTO alarmTypeClassDTO = new AlarmTypeClassDTO();
        List<AlarmType> typeList = new ArrayList<>();
        AlarmType alarmType = new AlarmType();
        alarmType.setTypeValue(iotAlarmConfigVo.getIotAlarmType());
        typeList.add(alarmType);
        alarmTypeClassDTO.setTypeList(typeList);
        alarmTypeClassDTO.setAlarmLevel(iotAlarmConfigVo.getAlarmLevel());
        log.info("修改告警类型对应告警等级入参{}",alarmTypeClassDTO);
        iAlarmTypeClassService.modifyAlarmLevel(alarmTypeClassDTO);
    }
    /**
     * 功能描述：删除物联告警配置
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<String>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/deleteIotAlarmConfig")
    Result<String> deleteIotAlarmConfig(@RequestBody IotAlarmConfigVo iotAlarmConfigVo){

        Result<String> result;
        try {
            result = iotAlarmConfigService.deleteIotAlarmConfig(iotAlarmConfigVo.getId());
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"删除物联告警配置异常:"+e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：查询单个告警配置信息
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<IotAlarmConfigVo>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/getIotAlarmConfigById")
    Result<IotAlarmConfigVo> getIotAlarmConfigById(@RequestBody IotAlarmConfigVo iotAlarmConfigVo){
        Result<IotAlarmConfigVo> result;
        try {
            result = iotAlarmConfigService.getIotAlarmConfig(iotAlarmConfigVo.getId());
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"查询单个告警配置信息异常:"+e);
        }
        return result;
    }

    /**
     * 功能描述：改变物联告警状态
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/changeIotAlarmStatus")
    Result<Object> changeIotAlarmStatus(@RequestBody IotAlarmConfigVo iotAlarmConfigVo){
        Result<Object> result;
        try {
            result = iotAlarmConfigService.changeIotAlarmStatus(iotAlarmConfigVo);
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"改变物联告警状态异常:"+e);
        }
        return result;
    }

    /**
     * 功能描述：获取当前客户下所有订购的有效的物联设备
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-24
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/getIotDevice")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="a.tenant_id", industryCode = "a.industry_code",deviceColumn="a.device_code")
    Result<Object> getIotDevice(@RequestBody IotAlarmConfigRequest iotAlarmConfigVo){
        Result<Object> result;
        try {
            result = iotAlarmConfigService.getIotDevice(iotAlarmConfigVo);
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"获取所有设备异常:"+e);
        }
        return result;
    }

    /**
     * 功能描述：获取物联设备对应的物联因子
     * @author lishihao
     * @param iotDeviceInfoVoList
     * @return Result<Object>
     * @date 2022-7-24
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/getIotFactor")
    Result<Object> getIotFactor(@RequestBody List<IotDeviceInfoVo> iotDeviceInfoVoList){

        Result<Object> result;
        try {
            result = iotAlarmConfigService.getIotFactorByDeviceCode(iotDeviceInfoVoList);
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"获取物联设备对应的物联因子失败:"+e);
        }
        return result;
    }

    /**
     * 功能描述：获取客户所有的物联因子
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-24
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/getAllIotFactor")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="t1.tenant_id", industryCode = "t1.industry_code",deviceColumn="t1.device_code")
    Result<Object> getAllIotFactor(@RequestBody IotAlarmConfigRequest iotAlarmConfigVo){
        Result<Object> result;
        try {
            result = iotAlarmConfigService.getAllFactor(iotAlarmConfigVo);
        }catch (Exception e){
            result = Result.fail(ResultCode.ERROR.getCode(),"获取客户所有的物联因子失败:"+e);
        }
        return result;
    }

    /**
     * 功能描述：校验物联告警配置名称是否重复
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-8-8
     * @requestID 物联告警配置（铁塔&客户）
     */
    @PostMapping("/checkConfigName")
    Result<Object> checkIotAlarmConfigName(@RequestBody IotAlarmConfigVo iotAlarmConfigVo){
        return iotAlarmConfigService.checkIotAlarmConfigName(iotAlarmConfigVo);
    }

    /**
     * 功能描述：校验物联告警类型配置是否重复
     * @author cuisj
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2023-11-02
     * @requestID 物联告警配置（组户）
     */
    @PostMapping("/checkIotAlarmType")
    Result<Object> checkIotAlarmType(@RequestBody IotAlarmConfigVo iotAlarmConfigVo){
        return iotAlarmConfigService.checkIotAlarmType(iotAlarmConfigVo);
    }

    /**
     * 功能描述：获取默认名称
     * @author liusq
     * @return Result<Object>
     * @date 2022-8-23
     * @requestID 物联告警配置（铁塔&客户）
     */
    @GetMapping("/getAutoConfigName")
    Result<Object> getAutoConfigName(){
        return iotAlarmConfigService.getAutoConfigName();
    }

    /**
     * 功能描述：查询物联设备配置信息 （提供给行业调用）
     * SDK接口包括：设备编码、设备状态以及该设备配置的物联因子名称，因子编码，最大值 ，最小值 ，最小值符号,最大值符号（无，<=，<）
     * @param params
     * @return
     * @vesion v1.6.0
     */
    @PostMapping("/queryIotDeviceAndFactorInfo")
    Result<Object> queryIotDeviceAndFactorInfo (@RequestBody Map<String, Object> params) {
        return iotAlarmConfigService.queryIotDeviceAndFactorInfo(params);
    }


    /**
     * 功能描述：根据告警类型查询告警等级
     * @param alarmTypeLevelDO
     * @return
     */
    @PostMapping("/queryAlarmLevelByTypeId")
    Result<Object> queryAlarmLevelByTypeId (@RequestBody AlarmTypeLevelDO alarmTypeLevelDO) {
        return iotAlarmConfigService.queryAlarmLevelByTypeId(alarmTypeLevelDO);
    }

    /**
     * 功能描述：查询已订购物联设备设备类型
     * @return
     */
    @PostMapping("/queryIotDeviceType")
    Result<Object> queryIotDeviceType () {
        return iotAlarmConfigService.queryIotDeviceType();
    }
}
