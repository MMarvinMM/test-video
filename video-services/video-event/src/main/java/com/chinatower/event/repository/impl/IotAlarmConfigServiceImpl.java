package com.chinatower.event.repository.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.deprecated.ISensorDeviceServiceMigrate;
import com.chinatower.device.entity.vo.SensorMonitorRelInfoVo;
import com.chinatower.event.constant.CommonConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.*;
import com.chinatower.event.dto.IotDeviceTree;
import com.chinatower.event.entity.dto.IotAlarmConfigDTO;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.IotAlarmConfigService;
import com.chinatower.event.vo.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.util.StringUtil;
import io.seata.common.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@DS(EbikeDataBaseConstant.BASE)
public class IotAlarmConfigServiceImpl extends ServiceImpl<IotAlarmConfigMapper, IotAlarmConfigDo> implements IotAlarmConfigService {

    @Autowired
    private IotAlarmFactorMapper iotAlarmFactorMapper;

    @Autowired
    private IotAlarmDeviceMapper iotAlarmDeviceMapper;

    @Autowired
    private ISensorDeviceServiceMigrate iSensorDeviceServiceMigrate;

    @Autowired
    private IotAlarmConfigMapper iotAlarmConfigMapper;

    @Autowired
    private AlarmTypeLevelMapper alarmTypeLevelMapper;

    @Autowired
    private SysMapper sysMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="tenant_id", industryCode="industry_code")
    public Result<Object> queryIotAlarmConfigList(IotAlarmConfigDTO model) {
        Map<String,Object> map = new HashMap<>();
        QueryWrapper<IotAlarmConfigDo> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(model.getIotAlarmConfigName())){
            queryWrapper.lambda().like(IotAlarmConfigDo::getIotAlarmConfigName,model.getIotAlarmConfigName());
        }
        if (!StringUtils.isEmpty(model.getCreator())){
            String userName = model.getCreator();
            List<SysUser> sysUsers = iotAlarmConfigMapper.queryUserInfoList(null, userName);
            if (!sysUsers.isEmpty()){
                List<String> userIdList = sysUsers.stream().map(SysUser::getUserName).collect(Collectors.toList());
                queryWrapper.lambda().in(IotAlarmConfigDo::getCreator,userIdList);
            }else {
                map.put(CommonConstant.RECORDS,null);
                map.put(CommonConstant.CURRENT,null);
                map.put(CommonConstant.TOTAL,0);
                return Result.data(map);
            }
        }
        if (!StringUtils.isEmpty(model.getConfigStatus())){
            queryWrapper.lambda().eq(IotAlarmConfigDo::getConfigStatus,model.getConfigStatus());
        }
        if (!StringUtils.isEmpty(model.getModifier())){
            String modifier = model.getModifier();
            List<SysUser> sysUsers = iotAlarmConfigMapper.queryUserInfoList(null, modifier);
            if (!sysUsers.isEmpty()){
                List<String> userIdList = sysUsers.stream().map(SysUser::getUserName).collect(Collectors.toList());
                queryWrapper.lambda().in(IotAlarmConfigDo::getModifier,userIdList);
            }else {
                map.put(CommonConstant.RECORDS,null);
                map.put(CommonConstant.CURRENT,null);
                map.put(CommonConstant.TOTAL,0);
                return Result.data(map);
            }
        }
        if (!ObjectUtil.isEmpty(model.getCreateTimeBegin())){
            queryWrapper.lambda().ge(IotAlarmConfigDo::getCreateTime,model.getCreateTimeBegin());
        }
        if (!ObjectUtil.isEmpty(model.getCreateTimeEnd())){
            queryWrapper.lambda().le(IotAlarmConfigDo::getCreateTime,model.getCreateTimeEnd());
        }
        if (!ObjectUtil.isEmpty(model.getUpdateTimeBegin())){
            queryWrapper.lambda().ge(IotAlarmConfigDo::getUpdateTime,model.getUpdateTimeBegin());
        }
        if (!ObjectUtil.isEmpty(model.getUpdateTimeEnd())){
            queryWrapper.lambda().le(IotAlarmConfigDo::getUpdateTime,model.getUpdateTimeEnd());
        }
        if(!StringUtils.isEmpty(model.getTenantId())){
            queryWrapper.lambda().eq(IotAlarmConfigDo::getTenantId,model.getTenantId());
        }
        if (!StringUtils.isEmpty(model.getIndustryCode())){
            queryWrapper.lambda().eq(IotAlarmConfigDo::getIndustryCode,model.getIndustryCode());
        }
        queryWrapper.lambda().eq(IotAlarmConfigDo::getDeleteFlag, Constants.ZERO_NUM);
        //拼接权限条件
        String dataScope = model.getDataScope();
        if (StringUtils.isNotEmpty(dataScope)) {
            queryWrapper.apply(" EXISTS (SELECT 1) " + dataScope);
        }
        //设置排序规则
        if (StringUtils.isNotEmpty(model.getCreateDateOrder()) && model.getCreateDateOrder().equals("asc")) {
            queryWrapper.select().orderByAsc("create_time");
        } else if (StringUtils.isNotEmpty(model.getUpdateDateOrder()) && model.getUpdateDateOrder().equals("asc")) {
            queryWrapper.select().orderByAsc("update_time");
        } else if (StringUtils.isNotEmpty(model.getUpdateDateOrder()) && model.getUpdateDateOrder().equals("desc")) {
            queryWrapper.select().orderByDesc("update_time");
        } else {
            queryWrapper.select().orderByDesc("create_time");
        }

        Page<IotAlarmConfigDo> page = new Page<>(model.getPageNum(),model.getPageSize());
        //分页查询物联告警配置信息（vm_iot_alarm_config_t）
        IPage<IotAlarmConfigDo> iPage = this.baseMapper.selectPage(page,queryWrapper);
        List<IotAlarmConfigDo> records = iPage.getRecords();
        Map<String, String> userMap = setName(records);

        //物联告警配置列表添加阈值和物联因子关系
        List<String> configIdList = records.stream().map(IotAlarmConfigDo::getId).collect(Collectors.toList());
        if (configIdList.isEmpty()){
            return Result.data(iPage);
        }
        //查询物联告警多组规则（vm_iot_alarm_groups_t）
        List<IotAlarmConfigGroupsDo> groupsDoList = iotAlarmConfigMapper.queryConfigGroupsById(configIdList);
        List<String> groupsIdList = groupsDoList.stream().map(v -> v.getId()).collect(Collectors.toList());
        // 获取物联告警配置中对应的物联设备（vm_iot_alarm_device_t）
        List<IotAlarmDeviceDo> deviceDoList = iotAlarmConfigMapper.queryConfigDeviceById(groupsIdList);
        // 获取物联告警配置中对应的物联因子（vm_iot_alarm_factor_t）
        List<IotFactorVo> factorVoList = iotAlarmConfigMapper.queryConfigFactorById(groupsIdList);

        for (IotFactorVo iotAlarmFactorDo : factorVoList) {
            iotAlarmFactorDo.setIsChoose(Constants.ONE);
        }
        //匹配物联多组规则对应的因子及设备
        for (IotAlarmConfigGroupsDo groupsDo : groupsDoList) {
            List<IotAlarmDeviceDo> deviceList = deviceDoList.stream().filter(v -> v.getGroupId().equals(groupsDo.getId())).collect(Collectors.toList());
            List<IotFactorVo> factorList = factorVoList.stream().filter(v -> v.getGroupId().equals(groupsDo.getId())).collect(Collectors.toList());
            groupsDo.setIotAlarmDeviceDoList(deviceList);
            groupsDo.setIotFactorVoList(factorList);
        }
        //匹配物联配置对应多组规则
        for (IotAlarmConfigDo record : records) {
            //转换告警生效设备组合
            String groupsJson = record.getDeviceGroupsJson();
            List<String> iotDeviceGroups = JSON.parseObject(groupsJson, List.class);
            record.setIotDeviceGroups(iotDeviceGroups);
            List<IotAlarmConfigGroupsDo> groupsDoListFilter = groupsDoList.stream().filter(v -> v.getIotConfigId().equals(record.getId())).collect(Collectors.toList());
            record.setIotAlarmConfigGroupsList(groupsDoListFilter);
            if (null != record.getCreator() && userMap.containsKey(record.getCreator())){
                record.setCreator(userMap.get(record.getCreator()));
            }
            if (null != record.getModifier() && userMap.containsKey(record.getModifier())){
                record.setModifier(userMap.get(record.getModifier()));
            }
        }
        iPage.setRecords(records);
        return Result.data(iPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<Object> insertIotAlarmConfig(IotAlarmConfigVo iotAlarmConfigVo) {
        IotAlarmConfigDo iotAlarmConfigDo = new IotAlarmConfigDo();
        if (iotAlarmConfigVo.getIotAlarmConfigName() == null || iotAlarmConfigVo.getIotAlarmConfigName().equals("")){
            return Result.fail(Constants.IOT_ALARM_CONFIG_NAME_CANNOT_BE_EMPTY);
        }
        if (iotAlarmConfigVo.getConfigMethod() == null || iotAlarmConfigVo.getConfigMethod().equals("")){
            return Result.fail(Constants.IOT_ALARM_CONFIG_METHOD_CANNOT_BE_EMPTY);
        }
        BeanUtils.copyProperties(iotAlarmConfigVo,iotAlarmConfigDo);

        if (iotAlarmConfigDo.getConfigStatus() == null || iotAlarmConfigDo.getConfigStatus().equals("")){
            iotAlarmConfigDo.setConfigStatus(Constants.ZERO);
        }
        if (iotAlarmConfigDo.getDeleteFlag() == null || iotAlarmConfigDo.getDeleteFlag().equals("")){
            iotAlarmConfigDo.setDeleteFlag(Constants.ZERO_NUM);
        }
        String  iotDeviceGroupsJson = "";
        //转换告警生效设备组合
        List<String> deviceGroups = iotAlarmConfigVo.getIotDeviceGroups();
        if (CollectionUtils.isNotEmpty(deviceGroups)){
            iotDeviceGroupsJson = JSON.toJSONString(deviceGroups);
        }
        iotAlarmConfigDo.setDeviceGroupsJson(iotDeviceGroupsJson);
        //新增物联告警配置信息（vm_iot_alarm_config_t）
        this.baseMapper.insert(iotAlarmConfigDo);
        //新增物联告警对应的因子和设备
        Result<String> stringResult = this.insertDeviceAndFactor(iotAlarmConfigVo, iotAlarmConfigDo);
        String msg = stringResult.getMsg();
        return Result.success(msg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<Object> updateIotAlarmConfig(IotAlarmConfigVo iotAlarmConfigVo) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = sysMapper.selectUserOnlyByUserId(loginUser.getUser().getUserId());
        if (loginUser == null || loginUser.getUser() == null){
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            throw new CustomException(Constants.NO_MANAGE_PERMISSION);
        }
        IotAlarmConfigDo iotAlarmConfigDo = new IotAlarmConfigDo();
        if (iotAlarmConfigVo.getIotAlarmConfigName() == null || iotAlarmConfigVo.getIotAlarmConfigName().equals("")){
            return Result.fail(Constants.IOT_ALARM_CONFIG_NAME_CANNOT_BE_EMPTY);
        }
        if (iotAlarmConfigVo.getConfigMethod() == null || iotAlarmConfigVo.getConfigMethod().equals("")){
            return Result.fail(Constants.IOT_ALARM_CONFIG_METHOD_CANNOT_BE_EMPTY);
        }
        if (iotAlarmConfigVo.getTenantId() == null || iotAlarmConfigVo.getTenantId().equals("")){
            iotAlarmConfigVo.setTenantId(loginUser.getUser().getTenantId());
        }
        BeanUtils.copyProperties(iotAlarmConfigVo,iotAlarmConfigDo);
//        String userId = String.valueOf(loginUser.getUser().getUserId());
//        iotAlarmConfigDo.setModifier(userId);
        //0：覆盖   新增创建时间  删除修改时间
        if ("0".equals(iotAlarmConfigVo.getUpdateRemark())){
            iotAlarmConfigDo.setUpdateTime(null);
            iotAlarmConfigDo.setModifier(null);
            iotAlarmConfigDo.setCreateTime(new Date());
            iotAlarmConfigDo.setCreator(user.getUserName());
        }else{
            iotAlarmConfigDo.setUpdateTime(new Date());
            iotAlarmConfigDo.setModifier(user.getUserName());
        }
        String  iotDeviceGroupsJson = "";
        //转换告警生效设备组合
        List<String> deviceGroups = iotAlarmConfigVo.getIotDeviceGroups();
        if (CollectionUtils.isNotEmpty(deviceGroups)){
            iotDeviceGroupsJson = JSON.toJSONString(deviceGroups);
        }
        iotAlarmConfigDo.setDeviceGroupsJson(iotDeviceGroupsJson);
        //修改物联告警配置信息（vm_iot_alarm_config_t）
        this.baseMapper.updateById(iotAlarmConfigDo);
        List<String> groupsIdList = iotAlarmConfigMapper.queryGroupIdsById(iotAlarmConfigDo.getId());
        //删除物联告警多组规则（vm_iot_alarm_groups_t）
        iotAlarmConfigMapper.deleteConfigGroups(Integer.parseInt(iotAlarmConfigDo.getId()));
        //删除物联告警配置对应的物联设备信息（vm_iot_alarm_device_t）
        iotAlarmConfigMapper.deleteConfigDevice(groupsIdList);
        //删除物联告警配置对应的物联因子信息（vm_iot_alarm_factor_t）
        iotAlarmConfigMapper.deleteConfigFactor(groupsIdList);
        //查询物联告警配置详细信息（vm_iot_alarm_config_t）
        IotAlarmConfigDo iot = this.baseMapper.selectById(iotAlarmConfigDo.getId());
        Result<String> stringResult = this.insertDeviceAndFactor(iotAlarmConfigVo, iot);
        String msg = stringResult.getMsg();
        return Result.success(msg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> deleteIotAlarmConfig(String configId) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            throw new CustomException(Constants.NO_MANAGE_PERMISSION);
        }
        if (configId == null || configId.equals("")){
            return Result.fail(Constants.IOT_ALARM_CONFIG_ID_CANNOT_BE_EMPTY);
        }
        UpdateWrapper<IotAlarmConfigDo> configWrapper = new UpdateWrapper<>();
        configWrapper.nested(w -> w.eq("id", configId));
        configWrapper.lambda().set(IotAlarmConfigDo::getDeleteFlag,Constants.ONE);
        //逻辑删除物联告警配置--修改delete-flag字段为1（vm_iot_alarm_config_t）
        int row = this.baseMapper.update(null,configWrapper);
        if (row <= 0) {
            throw new CustomException("无法对该物联告警配置进行删除操作！");
        }
        //逻辑删除物联多组规则--修改delete-flag字段为1（vm_iot_alarm_groups_t）
        iotAlarmConfigMapper.updateConfigGroups(Integer.parseInt(configId));
        List<String> groupsIdList = iotAlarmConfigMapper.queryGroupIdsById(configId);
        //逻辑删除物联告警因子信息--修改delete-flag字段为1（vm_iot_alarm_factor_t）
        iotAlarmConfigMapper.updateConfigFactor(groupsIdList);
        //逻辑删除物联告警设备信息--修改delete-flag字段为1（vm_iot_alarm_device_t）
        iotAlarmConfigMapper.updateConfigDevice(groupsIdList);
        return Result.success("物联告警配置删除成功！");
    }

    public static List<IotDeviceTree> assembleDevice(List<IotDeviceInfoVo> iotDeviceInfoVoList){

        List<IotDeviceTree> result = new ArrayList<>();
        List<String> provinceId = new ArrayList<>();
        IotDeviceTree iotDeviceTree;
        // 创建全国节点
        IotDeviceTree iotDeviceTreeFirst = new IotDeviceTree();
        iotDeviceTreeFirst.setChildren(new ArrayList<>()); // 初始化 children 属性为一个空的列表
        iotDeviceTreeFirst.setName("全国");
        iotDeviceTreeFirst.setId("100000");
        for (IotDeviceInfoVo iotDeviceInfoVo : iotDeviceInfoVoList){
            if(iotDeviceInfoVo.getProvinceId()!=null ){
                if (!provinceId.contains(iotDeviceInfoVo.getProvinceId())) {
                    iotDeviceTree = new IotDeviceTree();
                    iotDeviceTree.setName(iotDeviceInfoVo.getProvinceName());
                    iotDeviceTree.setId(iotDeviceInfoVo.getProvinceId());
                    iotDeviceTree.setChildren(setCityChildren(iotDeviceInfoVoList,iotDeviceInfoVo.getProvinceId()));
                    iotDeviceTreeFirst.getChildren().add(iotDeviceTree); // 将省份节点作为全国节点的子节点
                    provinceId.add(iotDeviceInfoVo.getProvinceId());
                }
            }
        }
        result.add(iotDeviceTreeFirst);
        return result;
    }

    @Override
    public Result<Object> changeIotAlarmStatus(IotAlarmConfigVo iotAlarmConfigVo) {
        if (iotAlarmConfigVo.getId() == null || iotAlarmConfigVo.getId().equals("")){
            return Result.fail(Constants.IOT_ALARM_CONFIG_ID_CANNOT_BE_EMPTY);
        }
        IotAlarmConfigDo iotAlarmConfigDo = this.baseMapper.selectById(iotAlarmConfigVo.getId());
        if (iotAlarmConfigDo.getConfigStatus().equals("1")){
            iotAlarmConfigDo.setConfigStatus("0");
        }else {
            iotAlarmConfigDo.setConfigStatus("1");
        }
        try {
            //修改物联告警配置的状态（vm_iot_alarm_config_t）
            this.baseMapper.updateById(iotAlarmConfigDo);
        }catch (Exception e){
            return Result.fail("修改状态失败");
        }
        return Result.success("修改状态成功");
    }

    @Override
    public Result<IotAlarmConfigVo> getIotAlarmConfig(String configId) {

        IotAlarmConfigVo iotAlarmConfigVo = new IotAlarmConfigVo();
        if (configId == null || configId.equals("")){
            return Result.fail(Constants.IOT_ALARM_CONFIG_ID_CANNOT_BE_EMPTY);
        }
        IotAlarmConfigDo iotAlarmConfigDo = this.baseMapper.selectById(configId);
        if (iotAlarmConfigDo == null || iotAlarmConfigDo.getDeleteFlag() == 1){
            return Result.fail(Constants.IOT_ALARM_CONFIG_IS_EMPTY);
        }
        BeanUtils.copyProperties(iotAlarmConfigDo,iotAlarmConfigVo);

        // 查询告警等级（sys_alarm_type_level）
        String iotAlarmType = iotAlarmConfigVo.getIotAlarmType();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        AlarmTypeLevelDO body = new AlarmTypeLevelDO();
        body.setTypeValue(iotAlarmType);
        body.setTenantId(user.getTenantId());
        body.setIndustryCode(user.getIndustryCode());
        AlarmLevelVO alarmLevelVO = alarmTypeLevelMapper.getAlarmLevelByTypeValue(body);
        if (alarmLevelVO != null){
            iotAlarmConfigVo.setAlarmLevel(alarmLevelVO.getAlarmLevel());
        }

        //转换告警生效设备组合返回
        String deviceGroupsJson = iotAlarmConfigDo.getDeviceGroupsJson();
        List<String> iotDeviceGroups = JSON.parseObject(deviceGroupsJson, List.class);
        iotAlarmConfigVo.setIotDeviceGroups(iotDeviceGroups);
        //查询物联告警多组规则（vm_iot_alarm_groups_t）
        List<String> idList = Arrays.asList(configId);
        List<IotAlarmConfigGroupsDo> groupsDoList = iotAlarmConfigMapper.queryConfigGroupsById(idList);
        List<String> groupsIdList = groupsDoList.stream().map(v -> v.getId()).collect(Collectors.toList());
        //查询物联告警配置对应的物联设备（vm_iot_alarm_device_t）
        List<IotAlarmDeviceDo> deviceDoList = iotAlarmConfigMapper.queryConfigDeviceById(groupsIdList);
        //获取物联告警配置中对应的物联因子（vm_iot_alarm_factor_t）
        List<IotFactorVo> factorDoList = iotAlarmConfigMapper.queryConfigFactorById(groupsIdList);
        for (IotFactorVo iotAlarmFactorDo : factorDoList) {
            iotAlarmFactorDo.setIsChoose(Constants.ONE);
        }
        //匹配物联多组规则对应的因子及设备
        for (IotAlarmConfigGroupsDo groupsDo : groupsDoList) {
            List<IotAlarmDeviceDo> deviceList = deviceDoList.stream().filter(v -> v.getGroupId().equals(groupsDo.getId())).collect(Collectors.toList());
            List<IotFactorVo> factorList = factorDoList.stream().filter(v -> v.getGroupId().equals(groupsDo.getId())).collect(Collectors.toList());
            groupsDo.setIotAlarmDeviceDoList(deviceList);
            groupsDo.setIotFactorVoList(factorList);
        }
        iotAlarmConfigVo.setIotAlarmConfigGroupsList(groupsDoList);
        return Result.data(iotAlarmConfigVo);
    }

    @Override
    public Result<Object> getIotFactorByDeviceCode(List<IotDeviceInfoVo> iotDeviceInfoVoList) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null){
            return Result.fail(Constants.NO_USER_INFO);
        }
        List<String> deviceCodeList = new ArrayList<>();
        for (IotDeviceInfoVo iotDeviceInfoVo : iotDeviceInfoVoList) {
            if (iotDeviceInfoVo.getDeviceCode() == null || iotDeviceInfoVo.getDeviceCode().equals("")){
                return Result.fail("设备编码不能为空");
            }
            deviceCodeList.add(iotDeviceInfoVo.getDeviceCode());
        }
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        deviceReqDTO.setDeviceCodes(deviceCodeList);
        log.info("根据设备编码查询因子及对应传感器信息入参{}",deviceReqDTO);
        Result<List<SensorMonitorRelInfoVo>> result = iSensorDeviceServiceMigrate.querySensorMonitorRelInfo(deviceReqDTO);
        log.info("根据设备编码查询因子及对应传感器信息返回结果{}", JSONArray.toJSONString(result));
        List<IotFactorSensorVo> iotAlarmSensorList = new ArrayList<>();

        if (result!= null && CollectionUtil.isNotEmpty(result.getData())){
            List<SensorMonitorRelInfoVo> data = result.getData();
            List<IotFactorVo> iotAlarmFactorList = new ArrayList<>();
            for (SensorMonitorRelInfoVo vo : data) {
                IotFactorVo iotFactorVo = new IotFactorVo();
                iotFactorVo.setFactorCode(vo.getMonitorCode());
                iotFactorVo.setFactorName(vo.getMonitorName());
                iotFactorVo.setSensorTypeCode(vo.getSensorTypeCode());
                iotFactorVo.setSensorTypeName(vo.getSensorTypeName());
                iotFactorVo.setMonitorUnits(vo.getMonitorUnits());
                iotAlarmFactorList.add(iotFactorVo);
            }
            iotAlarmFactorList = iotAlarmFactorList.stream().distinct().collect(Collectors.toList());
            // 按照sensorType进行分组
            Map<String, List<IotFactorVo>> groupedMap = iotAlarmFactorList.stream()
                    .collect(Collectors.groupingBy(IotFactorVo::getSensorTypeCode));
            // 遍历分组后的每组数据来创建IotFactorSensorVo对象并处理
            groupedMap.forEach((sensorType, factorVoList) -> {
                String sensorTypeName = factorVoList.get(0).getSensorTypeName();
                IotFactorSensorVo iotFactorSensorVo = new IotFactorSensorVo();
                iotFactorSensorVo.setSensorType(sensorType);
                if (StringUtils.isNotEmpty(sensorTypeName)){
                    iotFactorSensorVo.setSensorTypeName(sensorTypeName);
                }else {
                    iotFactorSensorVo.setSensorTypeName(sensorType);
                }
                iotFactorSensorVo.setFactorVoList(factorVoList);
                iotAlarmSensorList.add(iotFactorSensorVo);
            });
        }
        return Result.data(iotAlarmSensorList);
    }

    @Override
    public Result<Object> getAllFactor(IotAlarmConfigRequest iotAlarmConfigVo) {

        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null){
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (iotAlarmConfigVo.getTenantId() == null || iotAlarmConfigVo.getTenantId().equals("")){
            iotAlarmConfigVo.setTenantId(loginUser.getUser().getTenantId());
        }
        //查询客户下所有的物联因子信息（vm_order_sensor_devices、vm_sensor_monitor_status、vm_sensor_monitor_config）
        List<IotFactorVo> allFactorInfo = iotAlarmConfigMapper.getAllFactor(iotAlarmConfigVo);
        for (IotFactorVo iotFactorVo : allFactorInfo) {
            iotFactorVo.setIsChoose(Constants.ZERO);
        }
        return Result.data(allFactorInfo);
    }

    @Override
    public Result<Object> getIotDevice(IotAlarmConfigRequest iotAlarmConfigVo) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null){
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (iotAlarmConfigVo.getTenantId() == null || iotAlarmConfigVo.getTenantId().equals("")){
            iotAlarmConfigVo.setTenantId(loginUser.getUser().getTenantId());
        }
        //查询客户订购的所有在线设备的详细信息（vm_order_sensor_devices、vm_iot_device_t、sys_area）
        List<IotDeviceInfoVo> iotDeviceInfoList = iotAlarmConfigMapper.getIotDeviceInfoVoList(iotAlarmConfigVo);
        List<IotDeviceTree> iotDeviceTrees = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(iotDeviceInfoList)){
            iotDeviceTrees = assembleDevice(iotDeviceInfoList);
        }
        return Result.data(iotDeviceTrees);

    }

    @Override
    public Result<Object> checkIotAlarmConfigName(IotAlarmConfigVo iotAlarmConfigVo) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (iotAlarmConfigVo.getTenantId() == null || iotAlarmConfigVo.getTenantId().equals("")){
            iotAlarmConfigVo.setTenantId(loginUser.getUser().getTenantId());
        }
        if (iotAlarmConfigVo.getIndustryCode() == null || iotAlarmConfigVo.getIndustryCode().equals("")){
            iotAlarmConfigVo.setIndustryCode(loginUser.getUser().getIndustryCode());
        }

        QueryWrapper<IotAlarmConfigDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(IotAlarmConfigDo::getIotAlarmConfigName,iotAlarmConfigVo.getIotAlarmConfigName());
        queryWrapper.lambda().eq(IotAlarmConfigDo::getTenantId,iotAlarmConfigVo.getTenantId());
        queryWrapper.lambda().eq(IotAlarmConfigDo::getIndustryCode,iotAlarmConfigVo.getIndustryCode());
        queryWrapper.lambda().eq(IotAlarmConfigDo::getDeleteFlag,0);
        if (!StringUtils.isEmpty(iotAlarmConfigVo.getId())){
            queryWrapper.lambda().ne(IotAlarmConfigDo::getId,iotAlarmConfigVo.getId());
        }
        //根据名称和客户ID查询对应物联告警配置信息
        List<IotAlarmConfigDo> iotAlarmConfigDos = this.baseMapper.selectList(queryWrapper);
        if (!iotAlarmConfigDos.isEmpty()){
            return Result.fail(400,"物联告警配置名称已存在！");
        }
        return Result.success(ResultCode.SUCCESS);
    }

    @Override
    public Result<Object> getAutoConfigName() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        IotAlarmConfigDo vo = new IotAlarmConfigDo();
        vo.setTenantId(user.getTenantId());
        vo.setIndustryCode(user.getIndustryCode());
        //vo.setAppVerCode(user.getAppVerCode());
        return Result.data(iotAlarmConfigMapper.getAutoConfigName(vo));
    }

    /**
     * 查询物联告警配置的设备以及物联因子信息
     * @param params
     * @return
     */
    @Override
    public Result<Object> queryIotDeviceAndFactorInfo(Map<String, Object> params) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null){
            return Result.fail(Constants.NO_USER_INFO);
        }
        params.put("tenantId", loginUser.getUser().getTenantId());
        params.put("industryCode", loginUser.getUser().getIndustryCode());
        //查询该租户该行业下的所有物联因子以及设备信息
        List<IotFactorVo> factorInfos  = iotAlarmConfigMapper.selectFactorInfos(params);
        List<Map> deviceInfos = iotAlarmConfigMapper.selectDeviceInfos(params);
        Map<String, List<Map<String, Object>>> resultList = new HashMap<>();
        List<Map<String, Object>> infosList = new ArrayList<>();
        deviceInfos.forEach(devMap -> {
            Map<String, Object> infosMap = new HashMap<>();
            infosMap.put("deviceCode", devMap.get("deviceCode"));
            infosMap.put("deviceStatus", devMap.get("deviceStatus"));
            List<String> factorCodeList = Arrays.asList(devMap.get("monitorCode").toString().split(","));
            List<IotFactorVo> factorList = factorCodeList.stream()
                    .flatMap(s -> factorInfos.stream()
                            .filter(item -> s.equals(item.getFactorCode()))
                            .map(item -> {
                                IotFactorVo factorVo = new IotFactorVo();
                                factorVo.setFactorCode(item.getFactorCode());
                                factorVo.setFactorName(item.getFactorName());
                                factorVo.setMinChar(item.getMinChar());
                                factorVo.setMinNum(item.getMinNum());
                                factorVo.setMaxChar(item.getMaxChar());
                                factorVo.setMaxNum(item.getMaxNum());
                                return factorVo;
                            }))
                    .collect(Collectors.toList());
            infosMap.put("factorList", factorList);
            infosList.add(infosMap);
        });
        resultList.put("deviceList", infosList);
        return Result.data(resultList);
    }

    @Override
    public Result<Object> queryAlarmLevelByTypeId(AlarmTypeLevelDO alarmTypeLevelDO) {
        if (StringUtil.isEmpty(alarmTypeLevelDO.getTenantId())){
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            alarmTypeLevelDO.setTenantId(loginUser.getUser().getTenantId());
            alarmTypeLevelDO.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        AlarmLevelVO levelVO = new AlarmLevelVO();
        AlarmLevelVO alarmLevelVO = iotAlarmConfigMapper.queryAlarmLevelByTypeId(alarmTypeLevelDO);
        if (ObjectUtil.isNull(alarmLevelVO)){
            levelVO.setAlarmLevel(Constants.ONE);
            levelVO.setTypeValue(alarmTypeLevelDO.getTypeValue());
        }else {
            levelVO = alarmLevelVO;
        }
        return Result.data(levelVO);
    }

    @Override
    public Result<Object> queryIotDeviceType() {
        List<SysDictData> data = new ArrayList<>();
        //查询已订购物联设备设备类型
        IotAlarmConfigRequest request = new IotAlarmConfigRequest();
        List<String> list = iotAlarmConfigMapper.queryIotDeviceType(request);
        //查询所有物联设备类型
        Result<List<SysDictData>> result = systemFeignClient.getDictDataByType("iot_device_model_type");
        if (!CollectionUtils.isEmpty(result.getData())) {
            data = result.getData().stream().filter(v -> list.contains(v.getDictValue())).collect(Collectors.toList());
        }
        return Result.data(data);
    }


    public static List<IotDeviceTree> setCityChildren(List<IotDeviceInfoVo> iotDeviceInfoVoList,String provinceId){
        List<IotDeviceTree> result = new ArrayList<>();
        IotDeviceTree iotDeviceTree;
        List<String> cityId = new ArrayList<>();
        for (IotDeviceInfoVo iotDeviceInfoVo:iotDeviceInfoVoList){
            if(iotDeviceInfoVo.getCityId()!=null){
                if (!cityId.contains(iotDeviceInfoVo.getCityId()) && iotDeviceInfoVo.getCityParentId().equals(provinceId)){
                    iotDeviceTree = new IotDeviceTree();
                    iotDeviceTree.setName(iotDeviceInfoVo.getCityName());
                    iotDeviceTree.setId(iotDeviceInfoVo.getCityId());
                    iotDeviceTree.setChildren(setCountyChildren(iotDeviceInfoVoList,iotDeviceInfoVo.getCityId()));
                    result.add(iotDeviceTree);
                    cityId.add(iotDeviceInfoVo.getCityId());
                }
            }
        }
        return result;
    }

    @Override
    public Result<Object> checkIotAlarmType(IotAlarmConfigVo iotAlarmConfigVo) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (iotAlarmConfigVo.getTenantId() == null || iotAlarmConfigVo.getTenantId().equals("")){
            iotAlarmConfigVo.setTenantId(loginUser.getUser().getTenantId());
        }
        if (iotAlarmConfigVo.getIndustryCode() == null || iotAlarmConfigVo.getIndustryCode().equals("")){
            iotAlarmConfigVo.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        //判断物联告警类型是否重复
        QueryWrapper<IotAlarmConfigDo> queryWrapperType = new QueryWrapper<>();
        queryWrapperType.lambda().eq(IotAlarmConfigDo::getTenantId,iotAlarmConfigVo.getTenantId());
        queryWrapperType.lambda().eq(IotAlarmConfigDo::getIndustryCode,iotAlarmConfigVo.getIndustryCode());
        queryWrapperType.lambda().eq(IotAlarmConfigDo::getDeleteFlag,0);
        if (!StringUtils.isEmpty(iotAlarmConfigVo.getId())){
            queryWrapperType.lambda().ne(IotAlarmConfigDo::getId,iotAlarmConfigVo.getId());
        }
        if (!StringUtils.isEmpty(iotAlarmConfigVo.getIotAlarmType())){
            queryWrapperType.lambda().eq(IotAlarmConfigDo::getIotAlarmType,iotAlarmConfigVo.getIotAlarmType());
        }
        //根据物联告警类型，客户ID，行业，查询对应物联告警配置信息
        List<IotAlarmConfigDo> iotAlarmConfigDosType = this.baseMapper.selectList(queryWrapperType);
        if (CollectionUtils.isNotEmpty(iotAlarmConfigDosType)){
            Result<List<IotAlarmConfigDo>> data = Result.data(iotAlarmConfigDosType);
            data.setCode(400);
            return Result.data(data);
        }

        return Result.success(ResultCode.SUCCESS);
    }

    public static List<IotDeviceTree> setCountyChildren(List<IotDeviceInfoVo> iotDeviceInfoVoList,String cityId){
        List<IotDeviceTree> result = new ArrayList<>();
        IotDeviceTree iotDeviceTree;
        List<String> countyIdList = new ArrayList<>();
        for (IotDeviceInfoVo iotDeviceInfoVo:iotDeviceInfoVoList){
            if(iotDeviceInfoVo.getCountyId()!=null){
                if (!countyIdList.contains(iotDeviceInfoVo.getCountyId()) && iotDeviceInfoVo.getCountyParentId().equals(cityId)){
                    iotDeviceTree = new IotDeviceTree();
                    iotDeviceTree.setName(iotDeviceInfoVo.getCountyName());
                    iotDeviceTree.setId(iotDeviceInfoVo.getCountyId());
                    iotDeviceTree.setChildren(setDeviceChildren(iotDeviceInfoVoList,iotDeviceInfoVo.getCountyId()));
                    result.add(iotDeviceTree);
                    countyIdList.add(iotDeviceInfoVo.getCountyId());
                }
            }
        }
        return result;
    }

    public static List<IotDeviceTree> setDeviceChildren(List<IotDeviceInfoVo> iotDeviceInfoVoList,String countyId){
        List<String> deviceCode = new ArrayList<>();
        IotDeviceTree deviceInfo;
        List<IotDeviceTree> deviceResult = new ArrayList<>();
        for (IotDeviceInfoVo iotDeviceInfoVo : iotDeviceInfoVoList) {
            if(iotDeviceInfoVo.getDeviceCode()!=null){
                if (!deviceCode.contains(iotDeviceInfoVo.getDeviceCode())&&iotDeviceInfoVo.getCountyId().equals(countyId)){
                    deviceInfo = new IotDeviceTree();
                    deviceInfo.setDeviceCode(iotDeviceInfoVo.getDeviceCode());
                    deviceInfo.setName(iotDeviceInfoVo.getDeviceName());
                    deviceInfo.setId(String.valueOf(iotDeviceInfoVo.getIotDeviceId()));
                    deviceResult.add(deviceInfo);
                    deviceCode.add(iotDeviceInfoVo.getDeviceCode());
                }
            }
        }
        return deviceResult;
    }

    public Result<String> insertDeviceAndFactor(IotAlarmConfigVo iotAlarmConfigVo, IotAlarmConfigDo iotAlarmConfigDo){
        //多组规则
        List<IotAlarmConfigGroupsDo> configGroupsList = iotAlarmConfigVo.getIotAlarmConfigGroupsList();
        for (IotAlarmConfigGroupsDo configGroupsDo : configGroupsList) {
            configGroupsDo.setIotConfigId(iotAlarmConfigDo.getId());
            configGroupsDo.setCreator(iotAlarmConfigDo.getCreator());
            configGroupsDo.setCreateTime(iotAlarmConfigDo.getCreateTime());
            configGroupsDo.setModifier(iotAlarmConfigDo.getModifier());
            configGroupsDo.setUpdateTime(iotAlarmConfigDo.getUpdateTime());
            configGroupsDo.setDeleteFlag(Constants.ZERO_NUM);
            iotAlarmConfigMapper.insertConfigGroups(configGroupsDo);
            List<IotAlarmDeviceDo> iotAlarmDeviceDoList = configGroupsDo.getIotAlarmDeviceDoList();
            for (IotAlarmDeviceDo iotAlarmDeviceDo : iotAlarmDeviceDoList) {
                iotAlarmDeviceDo.setGroupId(String.valueOf(configGroupsDo.getId()));
                iotAlarmDeviceDo.setTenantId(iotAlarmConfigDo.getTenantId());
                iotAlarmDeviceDo.setCreator(iotAlarmConfigDo.getCreator());
                iotAlarmDeviceDo.setCreateTime(iotAlarmConfigDo.getCreateTime());
                iotAlarmDeviceDo.setModifier(iotAlarmConfigDo.getModifier());
                iotAlarmDeviceDo.setUpdateTime(iotAlarmConfigDo.getUpdateTime());
                iotAlarmDeviceDo.setDeleteFlag(Constants.ZERO_NUM);
            }
            //新增物联设备（vm_iot_alarm_device_t）
            iotAlarmConfigMapper.insertDevice(iotAlarmDeviceDoList);
            List<IotFactorVo> iotAlarmFactorVoList = configGroupsDo.getIotFactorVoList();
            for (IotFactorVo iotFactorVo : iotAlarmFactorVoList) {
                if (iotFactorVo.getIsChoose().equals("1")){
                    iotFactorVo.setGroupId(String.valueOf(configGroupsDo.getId()));
                    iotFactorVo.setCreator(iotAlarmConfigDo.getCreator());
                    iotFactorVo.setModifier(iotAlarmConfigDo.getModifier());
                    iotFactorVo.setDeleteFlag(Constants.ZERO_NUM);
                    iotFactorVo.setCreateTime(iotAlarmConfigDo.getCreateTime());
                    iotFactorVo.setUpdateTime(iotAlarmConfigDo.getUpdateTime());
                }
            }
            //新增物联因子（vm_iot_alarm_factor_t）
            iotAlarmConfigMapper.insertFactor(iotAlarmFactorVoList);
        }
        return Result.success("成功！");
    }

    public Map<String,String> setName(List<IotAlarmConfigDo> records){
        List<String> creatorList = records.stream().filter(iotAlarmConfigDo -> StringUtils.isNotEmpty(iotAlarmConfigDo.getCreator())).map(IotAlarmConfigDo::getCreator).collect(Collectors.toList());
        List<String> modifierList = records.stream().filter(iotAlarmConfigDo -> StringUtils.isNotEmpty(iotAlarmConfigDo.getModifier())).map(IotAlarmConfigDo::getModifier).collect(Collectors.toList());
        List<String> userIdList = new LinkedList<>();
        userIdList.addAll(creatorList);
        userIdList.addAll(modifierList);
        Map<String,String> userMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userIdList)){
            //根据账号id查询账号名称
            List<SysUser> usersList = iotAlarmConfigMapper.queryUserInfoList(userIdList,null);
            Map<String,String> resultMap = usersList.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getUserName));
            userMap.putAll(resultMap);
        }
        return userMap;
    }
}
