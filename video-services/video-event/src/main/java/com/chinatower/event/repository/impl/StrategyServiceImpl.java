package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.StrategyService;
import com.chinatower.event.utils.PTZUtils;
import com.chinatower.event.vo.entity.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 功能说明: 告警压制实现类 impl
 * @author: li.xufu
 * @date: 2022/5/13
 * @requestID 需求单号及需求名称
 */
@Slf4j
@Component
public class StrategyServiceImpl implements StrategyService {

    @Autowired
    private VmAlarmSuppressStrategyMapper strategyMapper;
    @Autowired
    private VmAlarmDeviceSuppressMapper deviceSuppressMapper;

    @Autowired
    private VmAlarmAreaSuppressMapper areaSuppressMapper;

    @Autowired
    private VmAlarmTypeSuppressMapper typeSuppressMapper;

    @Autowired
    private SysMapper sysMapper;


    /**
     * 功能说明: 告警策略新增
     * @author:
     * @date:
     * @param strategyVo
     * @return: Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS(EbikeDataBaseConstant.BASE)
    public Result add(VmAlarmSuppressStrategyVO strategyVo) {
        // 获取当前登录人
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(user != null && (!StringUtils.isEmpty(user.getTenantId())
                || !StringUtils.isEmpty(strategyVo.getTenantId()))){
            strategyVo.setCreaterBy(user.getUserName());
            if(StringUtils.isEmpty(strategyVo.getTenantId())){
                strategyVo.setTenantId(user.getTenantId());
            }
            if(StringUtils.isEmpty(strategyVo.getIndustryCode())){
                strategyVo.setIndustryCode(user.getIndustryCode());
            }
            if(StringUtils.isEmpty(strategyVo.getAppVerCode())){
                strategyVo.setAppVerCode(user.getAppVerCode());
            }
        }else{
            return Result.fail(ResultCode.CURRENT_USER_FAIL.getCode(),ResultCode.CURRENT_USER_FAIL.getMsg());
        }
        strategyMapper.insert(strategyVo);

        //批量新增告警策略关联的设备、告警类型和告警区域的关联关系
        Result result = this.batchHandleRelation(strategyVo);
        if(ResultCode.FAILURE.getCode() == result.getCode()){
            return result;
        }
        return Result.success("告警压制策略新增成功");
    }

    /**
     * 功能说明: 批量处理告警策略关联的设备、告警类型和告警区域的关联关系
     * @author:
     * @date:
     * @param strategyVo
     * @return: void
     */
    private Result batchHandleRelation(VmAlarmSuppressStrategyVO strategyVo) {
        log.info("批量处理告警策略关联关系入参：{}",strategyVo.toString());
        String userId =strategyVo.getCreaterBy();
        String tenantId =strategyVo.getTenantId();
        //1、批量插入告警设备或者告警类型数据信息
        List<VmAlarmDeviceSuppressVO> deviceList =  strategyVo.getDeviceSuppressVoList();
        List<VmAlarmTypeSuppressVO> typeList = strategyVo.getTypeSuppressVoList();
        List<VmAlarmAreaSuppressVO> areaList = strategyVo.getAreaSuppressVoList();
        if(deviceList!=null && !deviceList.isEmpty()){
            for(VmAlarmDeviceSuppressVO deviceTemp:deviceList){
                deviceTemp.setAlarmSuppressId(strategyVo.getId());
                deviceTemp.setTenantId(tenantId);
                deviceTemp.setCreaterBy(userId);
            }
            deviceSuppressMapper.batchInsert(deviceList);
        }
        //2、批量插入告警类型数据信息
        if(typeList!=null && !typeList.isEmpty()){
            for(VmAlarmTypeSuppressVO typeTemp:typeList){
                typeTemp.setAlarmSuppressId(strategyVo.getId());
                typeTemp.setTenantId(tenantId);
                typeTemp.setCreaterBy(userId);
            }
            typeSuppressMapper.batchInsert(typeList);
        }
        //3、批量插入告警屏蔽区域数据信息
        if(areaList!=null && !areaList.isEmpty()){
            for(VmAlarmAreaSuppressVO areaTemp:areaList){
                areaTemp.setAlarmSuppressId(strategyVo.getId());
                areaTemp.setTenantId(tenantId);
                areaTemp.setCreaterBy(userId);
                areaTemp.setPoints(JSONObject.toJSONString(areaTemp.getSuppressPointList()));
            }
            //对标绘区域和pt范围进行压制区域范围处理
            areaSuppressMapper.batchInsert(areaList);
        }
        if(!CollectionUtils.isEmpty(strategyVo.getUavAreaSuppressVoList())){

            for (VmAlarmUavAreaSuppressVO uavArea:strategyVo.getUavAreaSuppressVoList()) {
                uavArea.setTenantId(tenantId);
                uavArea.setIndustryCode(strategyVo.getIndustryCode());
                uavArea.setAlarmSuppressId(strategyVo.getId());
                uavArea.setCreateUser(userId);
            }
            areaSuppressMapper.batchUavInsert(strategyVo.getUavAreaSuppressVoList());
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 功能说明: 处理转换告警压制区域的PT值
     * @author:
     * @date:
     * @param areaSuppressVo
     * @return: void
     * @version:
     */
    @Override
    public Result tranformPT(VmAlarmAreaSuppressVO areaSuppressVo) {
        log.info("处理转换告警压制区域的PT值入参：{}",areaSuppressVo.toString());
        Result result = null;
        if(areaSuppressVo.getDeviceCode()!=null ){ //计算标绘区域ptz
            List<DeviceModelVO> deviceList = areaSuppressMapper.selectDeviceInfo(areaSuppressVo.getDeviceCode());
            if(Constants.ALARM_AREA_TYPE1.equals(areaSuppressVo.getAreaType())  ){//告警区域类型实时绘制坐标点处理
                DeviceModelVO modelVo = deviceList.get(Constants.ZERO_NUM);
                if (Objects.isNull(modelVo) || Objects.isNull(modelVo.getMinFocus()) || Objects.isNull(modelVo.getPicSensorLength()) || Objects.isNull(modelVo.getPicSensorWidth())) {
                    log.info("设备厂商对应参数信息不全,无法获取最小焦距、摄像机图像高度和宽度，无法进行PTZ处理");
                    return Result.fail("设备厂商对应参数信息不全,无法获取最小焦距、摄像机图像高度和宽度，无法进行PTZ处理");
                }
                List<SuppressPointPTVO> list =new ArrayList<>();
                if(areaSuppressVo.getSuppressPointList()!=null){
                    areaSuppressVo.setPoints(JSONObject.toJSONString(areaSuppressVo.getSuppressPointList()));
                    List<SuppressPointVO> pointsList = areaSuppressVo.getSuppressPointList();
                    for(SuppressPointVO pointTemp:pointsList){
                        SuppressPointPTVO pointPTVo = PTZUtils.pointTranformPT(pointTemp,areaSuppressVo,modelVo);
                        list.add(pointPTVo);
                    }
                }
                String jsonPoint = JSONObject.toJSONString(list);
                log.info("设备:{} 设置标绘点转义后的PTZ值:{}",areaSuppressVo.getDeviceCode(),jsonPoint);
                areaSuppressVo.setPointsPt(jsonPoint);
            }else if(Constants.ALARM_AREA_TYPE2.equals(areaSuppressVo.getAreaType())){//告警压制区域类型PT值范围
                //根据告警压制区域类型PT值范围组合PT
                this.groupPT(areaSuppressVo);
            }
            result = Result.success("查询成功");
            result.setData(areaSuppressVo);
        }
        return result;
    }

    public void groupPT(VmAlarmAreaSuppressVO areaTemp){
        if(areaTemp.getPMin()!=null && !"".equals(areaTemp.getPMin()) && areaTemp.getPMax()!=null && !"".equals(areaTemp.getPMax())){
            List<SuppressPointPTVO> list =new ArrayList<>();
            SuppressPointPTVO pointVo1 = new SuppressPointPTVO();
            pointVo1.setSuppressPointId(Constants.ONE);
            pointVo1.setPointP(areaTemp.getPMin());
            if(areaTemp.getTMax()!=null && !"".equals(areaTemp.getTMax()) ){
                pointVo1.setPointT(areaTemp.getTMax());
            }else{
                pointVo1.setPointT(String.valueOf(Constants.T_JUST_90));
            }
            list.add(pointVo1);
            SuppressPointPTVO pointVo2 = new SuppressPointPTVO();
            pointVo2.setSuppressPointId(Constants.TWO);
            pointVo2.setPointP(areaTemp.getPMin());
            if(areaTemp.getTMin()!=null && !"".equals(areaTemp.getTMin()) ){
                pointVo2.setPointT(areaTemp.getTMin());
            }else{
                pointVo2.setPointT(String.valueOf(Constants.T_LOST_90));
            }
            list.add(pointVo2);
            SuppressPointPTVO pointVo3 = new SuppressPointPTVO();
            pointVo3.setSuppressPointId(Constants.THREE);
            pointVo3.setPointP(areaTemp.getPMax());
            if(areaTemp.getTMin()!=null && !"".equals(areaTemp.getTMin()) ){
                pointVo3.setPointT(areaTemp.getTMin());
            }else{
                pointVo3.setPointT(String.valueOf(Constants.T_LOST_90));
            }
            list.add(pointVo3);
            SuppressPointPTVO pointVo4 = new SuppressPointPTVO();
            pointVo4.setSuppressPointId(Constants.FOUR);
            pointVo4.setPointP(areaTemp.getPMax());
            if(areaTemp.getTMax()!=null && !"".equals(areaTemp.getTMax()) ){
                pointVo4.setPointT(areaTemp.getTMax());
            }else{
                pointVo4.setPointT(String.valueOf(Constants.T_JUST_90));
            }
            list.add(pointVo4);
            SuppressPointPTVO pointVo5 = new SuppressPointPTVO();
            pointVo5.setSuppressPointId(Constants.FIVE);
            pointVo5.setPointP(areaTemp.getPMin());
            if(areaTemp.getTMax()!=null && !"".equals(areaTemp.getTMax()) ){
                pointVo5.setPointT(areaTemp.getTMax());
            }else{
                pointVo5.setPointT(String.valueOf(Constants.T_JUST_90));
            }
            list.add(pointVo5);
            String jsonPoint = JSONObject.toJSONString(list);
            areaTemp.setPointsPt(jsonPoint);
        }
    }

    /**
     * 功能说明: 告警策略修改
     * @author:
     * @date:
     * @param strategyVo
     * @return: Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS(EbikeDataBaseConstant.BASE)
    public Result mod(VmAlarmSuppressStrategyVO strategyVo) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if(currentUser != null && (!StringUtils.isEmpty(currentUser.getTenantId())
                || !StringUtils.isEmpty(strategyVo.getTenantId()))){
            if(StringUtils.isEmpty(strategyVo.getTenantId())){
                strategyVo.setTenantId(currentUser.getTenantId());
            }
            SysUser user = sysMapper.selectUserOnlyByUserId(currentUser.getUserId());
            strategyVo.setUpdateBy(user.getUserName());
            strategyVo.setUpdateTime(new Date());
        }else{
            return Result.fail(Constants.CHECK_TENANT_ID);
        }
        strategyMapper.update(strategyVo);
        //批量重置告警策略关联的设备、告警类型和告警区域的关联关系
        this.batchDeleteRelation(strategyVo);
        this.batchHandleRelation(strategyVo);
        return Result.success("告警压制策略修改成功");
    }

    /**
     * 功能说明: 告警策略删除
     * @author:
     * @date:
     * @param strategyVo
     * @return: Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @DS(EbikeDataBaseConstant.BASE)
    public Result del(VmAlarmSuppressStrategyVO strategyVo) {
        // 获取当前登录人
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if(currentUser != null && (!StringUtils.isEmpty(currentUser.getTenantId())
                || !StringUtils.isEmpty(strategyVo.getTenantId())
                 || currentUser.getUserType().equals("0"))){
            strategyVo.setUpdateBy(currentUser.getUserName());
            strategyVo.setUpdateTime(new Date());
        }else{
            return Result.fail("租户编码不能为空");
        }
        strategyMapper.deleteById(strategyVo);
        //批量删除告警策略关联的设备、告警类型和告警区域的关联关系
        this.batchDeleteRelation(strategyVo);
        return Result.success("压制策略删除成功");
    }

    /**
     * 功能说明: PT转换
     * @author:
     * @date:
     * @param areaSuppressVo
     * @return: Result
     */
    @Override
    public Result ptTransform(VmAlarmAreaSuppressVO areaSuppressVo) {
        log.info("ptTransform,{}",areaSuppressVo.toString());
        List<DeviceModelVO> deviceList = areaSuppressMapper.selectDeviceInfo(areaSuppressVo.getDeviceCode());
        DeviceModelVO modelVo = deviceList.get(Constants.ZERO_NUM);
        List<SuppressPointVO> list =new ArrayList<>();
        //根据告警压制区域类型PT值范围组合PT
        this.groupPT(areaSuppressVo);
        String pointsPt = areaSuppressVo.getPointsPt();
        List<SuppressPointPTVO>  pointsPtList = JSONObject.parseArray(pointsPt, SuppressPointPTVO.class);
        for(SuppressPointPTVO pointPtTemp:pointsPtList){
            SuppressPointVO pointVo = PTZUtils.ptTranformPoint(pointPtTemp,areaSuppressVo,modelVo);
            list.add(pointVo);
        }
        String jsonPoint = JSONObject.toJSONString(list);
        log.info("设备:{} 设置标绘点PT转义后的百分比坐标值:{}",areaSuppressVo.getDeviceCode(),jsonPoint);
        areaSuppressVo.setPoints(jsonPoint);
        areaSuppressVo.setSuppressPointList(list);
        return Result.data(areaSuppressVo);
    }

    /**
     * 功能说明: 批量删除告警策略关联的设备、告警类型和告警区域的关联关系
     * @author:
     * @date:
      * @param strategyVo
     * @return: void
     */
    private void batchDeleteRelation(VmAlarmSuppressStrategyVO strategyVo) {
        deviceSuppressMapper.deleteByStrategyId(strategyVo);
        typeSuppressMapper.deleteByStrategyId(strategyVo);
        areaSuppressMapper.deleteByStrategyId(strategyVo);
        areaSuppressMapper.deleteUavByStrategyId(strategyVo);
    }

}
