package com.chinatower.device.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.SensorAttrResultVO;
import com.chinatower.device.dataobject.SensorPeriodPageResultVO;
import com.chinatower.device.dto.SensorEventInfoVO;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.device.entity.dto.SenSorMonitorConfigDO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mongodb.SensorAttrValueAllMongoDAO;
import com.chinatower.device.mongodb.dto.VmIotDeviceStatus;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.repository.ITenantIotDeviceConfRepository;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * video-device
 * 物联设备 前端控制器
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@RestController
@Slf4j
@Api(value = "物联设备相关模块", tags = "物联设备相关模块")
@RequestMapping("/iotdevice")
public class IotDeviceController extends TowerBaseController {

    @Autowired
    IIotDeviceRepository iotDeviceRepository;

    @Autowired
    ITenantIotDeviceConfRepository tenantIotDeviceConfRepository;

    @Autowired
    SensorAttrValueAllMongoDAO sensorAttrValueAllMongoDAO;

    @Autowired
    IHornDeviceRepository iHornDeviceRepository;
    @Autowired
    private SysAreaRootService sysAreaRootService;


    /**
     * 查询物联设备详情--不关联订购
     * @param iotDeviceVO
     * @return
     */
    @PostMapping("/queryIotDeviceInfo")
    public Result<List<IotDeviceVO>> queryIotDeviceInfo(@RequestBody IotDeviceVO iotDeviceVO){
        List<IotDeviceVO> iotDeviceVOS = iotDeviceRepository.queryIotDeviceInfo(iotDeviceVO);
        return Result.data(iotDeviceVOS);
    }

    /**
     *
     * 根据站址编码查询物联设备列表
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @PostMapping("/queryIotDeviceBySite")
    public Result<List<IotDeviceVO>> queryIotDeviceBySite(@RequestBody IotDeviceVO iotDeviceVO){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser){
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            iotDeviceVO.setUserId(sysUser.getUserId());
        }
        List<IotDeviceVO> iotDeviceVOS = iotDeviceRepository.queryIotDeviceBySite(iotDeviceVO);
        return Result.data(iotDeviceVOS);
    }

    /**
     *
     * 根据区域编码查询物联设备列表
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @PostMapping("queryIotDeviceByArea")
    public Result<List<IotDeviceVO>> queryIotDeviceByArea(@RequestBody IotDeviceVO iotDeviceVO){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser){
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            iotDeviceVO.setUserId(sysUser.getUserId());
        }
        List<IotDeviceVO> iotDeviceVOS = iotDeviceRepository.queryIotDeviceByArea(iotDeviceVO);
        return Result.data(iotDeviceVOS);
    }

    /**
     *
     * 查询行政组织物联设备树
     *
     * @param iotDeviceVO 查询条件实体类
     * @return Result<List<DeviceTreeVO>>
     */
    @PostMapping("/getIotMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getIotMonitorOrgTree(@RequestBody IotDeviceVO iotDeviceVO){
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (null != currentUser){
                SysUser tokenUser = currentUser.getUser();
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
                iotDeviceVO.setTenantId(tokenUser.getTenantId());
                iotDeviceVO.setUserId(sysUser.getUserId());
            }
            List<DeviceTreeVO> treeList = iotDeviceRepository.getIotMonitorOrgTree(iotDeviceVO);
            if (!treeList.isEmpty()) {
                treeList.get(0).setFirst(1);
            }
            return Result.data(treeList);
        }catch (Exception e){
            log.error("查询行政组织物联设备树形失败, e:", e);
            return Result.fail("查询行政组织物联设备树形失败");
        }

    }

    /**
     *
     * 查询设备所属的行政区域树形
     *
     * @param deviceInfoVO
     * @return
     */
    @Deprecated
    @PostMapping("/getIotMonitorTree")
    public Result<List<DeviceTreeVO>> getIotMonitorTree(@RequestBody IotDeviceVO deviceInfoVO) {
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (null != currentUser){
                SysUser tokenUser = currentUser.getUser();
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
                deviceInfoVO.setTenantId(tokenUser.getTenantId());
                deviceInfoVO.setUserId(sysUser.getUserId());
            }
            List<DeviceTreeVO> treeList = iotDeviceRepository.getIotMonitorTree(deviceInfoVO);
            List<DeviceTreeVO> list = buildIotMonitorTree(treeList,deviceInfoVO.isNeedDevice());
            List<DeviceTreeVO> rootTree = new ArrayList<>(1);
            if (!treeList.isEmpty()) {
                // 补充中国根节点
                DeviceTreeVO root = new DeviceTreeVO();
                sysAreaRootService.createRootTreeVO(root);
                root.setOnlineNum(list.stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                root.setNum(list.stream().mapToInt(DeviceTreeVO::getNum).sum());
                //去掉异常省份
                list = list.stream().filter(dto ->
                        StringUtils.isNotEmpty(dto.getCode())).collect(Collectors.toList());
                root.setList(list);
                rootTree.add(root);
            }
            return Result.data(rootTree);
        } catch (Exception e) {
            log.error("查询设备所属的行政区域树形失败, e:", e);
            return Result.fail("查询设备所属的行政区域树形失败");
        }
    }


    private List<DeviceTreeVO> buildIotMonitorTree(List<DeviceTreeVO> treeList,Boolean backDevFlag) {
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        country.getList().forEach(townCode -> {
                            townCode.setOnlineNum((int) townCode.getList().stream().filter(townCodeVo -> Objects.equals(Constants.ZERO, townCodeVo.getStatus())).count());
                            townCode.setNum(townCode.getList().size());
                            townCode.getList().forEach(deviceVo ->{
                                if (deviceVo.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))){
                                    deviceVo.setIsMonitorNum(1);
                                }
                            });
                            townCode.setIsMonitorNum(townCode.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                            if (townCode.getIsMonitorNum() == townCode.getNum()){
                                townCode.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                            }
                            if (backDevFlag != null && !backDevFlag) {
                                townCode.getList().clear();
                            }
                            if (backDevFlag != null && backDevFlag) {
                                // 过滤掉没有在离线的设备
                                townCode.setList(townCode.getList().stream().filter(iotDevice -> com.chinatower.common.core.utils.StringUtils.isNotEmpty(iotDevice.getStatus())
                                        && (iotDevice.getStatus().equals(Constants.ZERO) || iotDevice.getStatus().equals(Constants.ONE))
                                ).collect(Collectors.toList()));
                            }
                        });
                        country.setOnlineNum(country.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                        country.setNum(country.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                        country.setIsMonitorNum(country.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                        if (country.getNum() == country.getIsMonitorNum()){
                            country.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                        }
                        if (!backDevFlag) {
                            //去掉异常乡镇
                            country.setList(country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getCode()) && com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getName())).collect(Collectors.toList()));
                        } else {
                            //将有异常的乡镇数据移动到区县目录下
                            List<DeviceTreeVO> list = country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isEmpty(townDto.getCode())).collect(Collectors.toList());
                            list.forEach(deviceTreeVO -> {
                                country.getList().addAll(deviceTreeVO.getList());
                            });
                            country.setList(country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getCode())).collect(Collectors.toList()));
                        }
                    });
                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    city.setIsMonitorNum(city.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                    if (city.getIsMonitorNum() == city.getNum()){
                        city.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                    }
                    //去掉异常区县
                    city.setList(city.getList().stream().filter(areaDto ->
                            StringUtils.isNotEmpty(areaDto.getCode())).collect(Collectors.toList()));
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                pro.setIsMonitorNum(pro.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                if (pro.getIsMonitorNum() == pro.getNum()){
                    pro.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                }
                //去掉异常地市
                pro.setList(pro.getList().stream().filter(prioDto ->
                        StringUtils.isNotEmpty(prioDto.getCode())).collect(Collectors.toList()));
            } else {
                pro.setOnlineNum(0);
                pro.setNum(0);
                pro.setIsMonitor("0");
            }
        });
        return treeList;
    }

    /**
     * 根据条件分页查询 雷达/物联 设备信息
     *
     * @param: deviceInfoVO
     * @return: TableDataInfo
     * @author: zheng.yan
     * @date: 2022/5/7
     * @version:
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @PostMapping("/queryIotDevicePage")
    public TableDataInfo queryIotDevicePage(@RequestBody IotDeviceVO deviceInfoVO) {
        try {
            if(deviceInfoVO.isExport()){
                deviceInfoVO.setPageNum(null);
                deviceInfoVO.setPageSize(null);
            } else {
                if(StringUtils.isNotNull(deviceInfoVO.getPageNum()) && StringUtils.isNotNull(deviceInfoVO.getPageSize())){
                    PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
                }else{
                    startPage();
                }
            }
            deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
            List<IotDeviceVO> list = iotDeviceRepository.queryIotDeviceListNew(deviceInfoVO);
            return getDataTable(list);
        } catch (Exception e) {
            log.error("根据条件分页查询设备信息失败, e:", e);
            return null;
        }
    }

    /**
     * 根据条件查询 雷达和物联 设备信息
     *
     * @param: deviceInfoVO
     * @version:
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @PostMapping("/queryIotRadarDeviceList")
    public Result<List<IotDeviceVO> > queryIotRadarDeviceList(@RequestBody IotDeviceVO deviceInfoVO) {
        try {
            List<IotDeviceVO> list = iotDeviceRepository.queryIotRadarDeviceList(deviceInfoVO);
            return Result.data(list);
        } catch (Exception e) {
            log.error("根据条件分页查询设备信息失败, e:", e);
            return null;
        }
    }

    /**
     * 根据条件分页查询 雷达/物联 设备信息 --APP使用
     *
     * @param deviceInfoVO
     * @return TableDataInfo
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @PostMapping("/queryIotDeviceList")
    public Result<PageInfo<IotDeviceVO>> queryIotDeviceList(@RequestBody IotDeviceVO deviceInfoVO) {
            deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        int categoryCode = Constants.TWO_NUM;
        if (deviceInfoVO.getCategoryCode() != null && Objects.equals(deviceInfoVO.getCategoryCode(),Constants.ONE_NUM)){
            categoryCode = Constants.ONE_NUM;
        }
        return iotDeviceRepository.queryRadarOrIotDeviceList(deviceInfoVO,categoryCode);
    }

    /**
     * 根据编码查询物联设备详情--APP端用  合并到/deviceCommon/getDeviceInfo
     * @param iotDeviceVO 入参-设备编码
     * @return IotDeviceVO 喇叭详情
     */
    @Deprecated
    @PostMapping("/queryIotDeviceByCode")
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    public Result<IotDeviceVO> queryIotDeviceByCode(@RequestBody IotDeviceVO iotDeviceVO){
        return iotDeviceRepository.queryIotDeviceByCode(iotDeviceVO);
    }

    /**
     * 查询物联设备联动配置
     *
     * @param: qryCond
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/5/7
     * @version:
     */
    @PostMapping("/getTenantIotDevConf")
    @DataScope(deviceColumn = "t.device_code", tenantColumn = "t.tenant_id", industryCode = "t.industry_code", alarmSourceVal = "8,10")
    public Result<TenantIotDeviceConfVO> getTenantIotDevConf(@Valid @RequestBody TenantIotDeviceConfVO qryCond) {
        try {
            return Result.data(tenantIotDeviceConfRepository.getTenantIotDevConf(qryCond));
        } catch (Exception e) {
            log.error("查询物联设备联动配置失败, e:", e);
            return Result.fail("查询物联设备联动配置失败");
        }
    }

    /**
     * 批量查询物联设备联动数据v1
     * @param qryCond
     * @return
     */
    @PostMapping("/getTenantIotDevConfBatch")
    public Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatch(@RequestBody TenantIotDeviceConfVO qryCond) {
        try {
            if (StringUtils.isEmpty(qryCond.getDeviceCode()) && CollectionUtils.isEmpty(qryCond.getDeviceCodeList())) {
                return Result.fail("设备编码不能为空");
            }
            return Result.data(tenantIotDeviceConfRepository.getTenantIotDevConfBatch(qryCond));
        } catch (Exception e) {
            log.error("批量查询物联设备联动配置失败, e:", e);
            return Result.fail("批量查询物联设备联动配置失败");
        }
    }

    /**
     * 批量查询物联设备联动数据v2
     */
    @PostMapping("/getTenantIotDevConfBatchV2")
    public Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatchV2(@RequestBody TenantIotDeviceConfVO qryCond) {
        try {
            if (StringUtils.isEmpty(qryCond.getDeviceCode()) && CollectionUtils.isEmpty(qryCond.getDeviceCodeList())) {
                return Result.fail("设备编码不能为空");
            }

            List<TenantIotDeviceConfVO> resultList = tenantIotDeviceConfRepository.getTenantIotDevConfBatchV2(qryCond);

            if (CollectionUtils.isNotEmpty(resultList)) {
                List<Long> iotDeviceConfIdList = resultList.stream().map(TenantIotDeviceConfVO::getIotDeviceConfId).distinct().collect(Collectors.toList());
                qryCond.setIotDeviceConfIdList(iotDeviceConfIdList);
                // 填充 confRelList
                List<TenantIotConfRelVO> confRelList = tenantIotDeviceConfRepository.getTenantIotRelConf(qryCond);
                if (CollectionUtils.isNotEmpty(confRelList)) {
                    Map<Long, List<TenantIotConfRelVO>> confRelMap = confRelList.stream().collect(Collectors.groupingBy(TenantIotConfRelVO::getIotDeviceConfId));
                    for (TenantIotDeviceConfVO iotDeviceConf : resultList) {
                        iotDeviceConf.setConfRels(confRelMap.get(iotDeviceConf.getIotDeviceConfId()));
                    }
                }
            }

            return Result.data(resultList);
        } catch (Exception e) {
            log.error("批量查询物联设备联动配置V2失败, e:", e);
            return Result.fail("批量查询物联设备联动配置V2失败");
        }
    }

    /**
     * 修改物联设备信息
     *
     * @param: qryCond
     * @return: Result<TenantIotDeviceConfVO>
     * @author: zheng.yan
     * @date: 2022/5/9
     * @version:
     */
    @PostMapping("/updateIotDevice")
    @TowerLog(title = "修改物联设备信息", businessType = BusinessType.UPDATE)
    public Result updateIotDevice(@RequestBody IotDeviceVO iotDevice) {
        try {
            if (StringUtils.isEmpty(iotDevice.getTenantId())) {
                iotDevice.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
            }
            iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_8));
            iotDeviceRepository.updateIotDevice(iotDevice);
            return Result.success("");
        } catch (Exception e) {
            log.error("修改物联设备信息失败, e:", e);
            return Result.fail("修改物联设备信息失败");
        }
    }

    /**
     * 保存联动配置
     *
     * @param: tenIotDevConf
     * @return: TenantIotDeviceConfVO
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    @PostMapping("/saveTenantIotDevConf")
    @TowerLog(title = "保存联动配置", businessType = BusinessType.UPDATE)
    public Result saveTenantIotDevConf(@RequestBody TenantIotDeviceConfVO tenIotDevConf) {
        try {
            //1.6.60需求，添加多组联动设备增加校验，多组摄像机信息中不允许选择相同的摄像机、通道及预置位信息
            if (CollectionUtils.isEmpty(tenIotDevConf.getConfRels())) {
                return Result.fail("没有添加联动设备，保存联动配置失败！");
            }
            Map<String, List<TenantIotConfRelVO>> tenIotDevConfMap = tenIotDevConf.getConfRels().stream().collect(Collectors.groupingBy(conf -> (conf.getDeviceCode() + "_" + conf.getChannelCode() + "_" + conf.getPresetId())));
            for(Map.Entry<String, List<TenantIotConfRelVO>> entry : tenIotDevConfMap.entrySet()) {
                //多组摄像机信息中不允许选择相同的摄像机、通道及预置位信息，否则提交时提示用户“xxx、xxx中的摄像机、通道及预置位信息重复，请修改后提交
                if (entry.getValue().size() > 1) {
                    String commonDevDevice = entry.getValue().stream().map(TenantIotConfRelVO::getConfRelName).collect(Collectors.joining("、"));
                    return Result.fail(commonDevDevice + "中的摄像机、通道及预置位信息重复，请修改后提交。");
                }
            }
            tenIotDevConf.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
            tenantIotDeviceConfRepository.saveTenantIotDevConf(tenIotDevConf);
            return Result.success("");
        } catch (Exception e) {
            log.error("保存联动配置失败, e:", e);
            return Result.fail("保存联动配置失败");
        }
    }

    /**
     * 新增传感器监测数据
     * @param sensorAttrInfoVO
     * @return
     */
    @ApiOperation(value = "新增传感器监测数据")
    @PostMapping("/insertSensorData")
    public Result<String> insertSensorData(@RequestBody SensorAttrInfoVO sensorAttrInfoVO){

        try {
            return iotDeviceRepository.insertSensorData(sensorAttrInfoVO);
        }catch (Exception e){
            log.error("新增传感器监测数据失败, e:", e);
            return Result.fail("新增传感器监测数据失败");
        }

    }


    /**
     * 查询传感器最新数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    @ApiOperation(value = "查询传感器最新数据")
    @PostMapping("/getSensorData")
    public Result<List<SensorAttrVO>> getSensorData(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            List<SensorAttrMapVO> list = iotDeviceRepository.listSensorAttrValue(deviceReqDTO);
            List<SensorAttrVO> sensorAttrs = iotDeviceRepository.querySensorAttr(deviceReqDTO);
            for (SensorAttrVO sensorAttrVO : sensorAttrs) {
                if (!StringUtils.isBlank(sensorAttrVO.getAttrUnits())){
                    sensorAttrVO.setAttrName(sensorAttrVO.getAttrName() + "(" + sensorAttrVO.getAttrUnits() + ")");
                }
                if (!list.isEmpty()) {
                    String valueKey = StringUtils.isNotEmpty(sensorAttrVO.getSensorCode()) ?
                            sensorAttrVO.getSensorCode() + "#" + sensorAttrVO.getAttrCode() : sensorAttrVO.getAttrCode();
                    sensorAttrVO.setAttrCode(valueKey);
                    sensorAttrVO.setAttrValue(StringUtils.toStringNoNull(MapUtils.getString(list.get(0), valueKey)));
                    sensorAttrVO.setMonitorTime(StringUtils.toStringNoNull(MapUtils.getString(list.get(0), Constants.MONITOR_TIME)));
                }
            }
            log.info("QuerySensorAttrValueVO param {} ,listSensorAttrValue  param {} ,result {}",
                    JSON.toJSONString(deviceReqDTO), JSON.toJSONString(list), JSON.toJSONString(sensorAttrs));
            return Result.data(sensorAttrs);
        } catch (Exception e) {
            log.error("查询传感器最新数据失败, e:", e);
            return Result.fail("查询传感器最新数据失败");
        }
    }

    /**
     * 查询传感器状态信息
     */
    @ApiOperation(value = "查询传感器状态信息")
    @PostMapping("/querySensorStatusInfo")
    public Result<List<SensorStatusInfoVO>> querySensorStatusInfo(@RequestBody SensorStatusInfoDTO param) {
        return Result.data(iotDeviceRepository.querySensorStatusInfo(param));
    }


    /**
     * 查询传感器时间段数据
     *
     * @return
     * @author zheng.yan
     * @date 2022/5/11
     * @version
     */
    @ApiOperation(value = "查询传感器时间段数据")
    @PostMapping("/getSensorPeriodData")
    public Result<List<SensorAttrMapVO>> getSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            if (StringUtils.isBlank(deviceReqDTO.getDeviceCode()) && CollectionUtils.isEmpty(deviceReqDTO.getDeviceCodes())) {
                return Result.fail(Constants.DEVICE_CODE_NOT_EMPTY);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = null;
            Date end = null;
            try {
                start = simpleDateFormat.parse(deviceReqDTO.getStartDate());
                end = simpleDateFormat.parse(deviceReqDTO.getEndDate());
            } catch (ParseException e) {
                log.error("日期转换异常", e);
            }
            if (start == null || end == null) {
                return Result.fail("日期格式有误");
            }
            String tableDate = DateUtils.parseDateToStr(DeviceConstants.CURRENT_MONTH_FORMAT, start);
            return Result.data(sensorAttrValueAllMongoDAO.listSensorPeriodData(deviceReqDTO, tableDate));
        } catch (Exception e) {
            log.error("查询传感器时间段数据失败, e:", e);
            return Result.fail("查询传感器时间段数据失败");
        }
    }

    /**
     * 通过设备编码查询设备状态记录
     *
     * @param deviceReqDTO
     * @return
     * @author tianhao
     */
    @ApiOperation(value = "通过设备编码查询设备状态记录")
    @PostMapping("/getDeviceStatusList")
    public Result<List<VmIotDeviceStatus>> getDeviceStatusList(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            if (StringUtils.isBlank(deviceReqDTO.getDeviceCode())) {
                return Result.fail(Constants.DEVICE_CODE_NOT_EMPTY);
            }
            return Result.data(sensorAttrValueAllMongoDAO.listDeviceStatusData(deviceReqDTO));
        } catch (Exception e) {
            log.error("通过设备编码查询设备状态记录失败, e:", e);
            return Result.fail("通过设备编码查询设备状态记录失败");
        }
    }

    /**
     * 查询传感器时间段数据
     *
     * @return
     * @author zheng.yan
     * @date 2022/5/12
     * @version
     */
    @ApiOperation(value = "查询传感器时间段数据")
    @PostMapping("/pageSensorPeriodData")
    public Result<Page<SensorAttrMapVO>> pageSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            if (StringUtils.isBlank(deviceReqDTO.getDeviceCode()) && CollectionUtils.isEmpty(deviceReqDTO.getDeviceCodes())) {
                return Result.fail(Constants.DEVICE_CODE_NOT_EMPTY);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = null;
            Date end = null;
            try {
                start = simpleDateFormat.parse(deviceReqDTO.getStartDate());
                end = simpleDateFormat.parse(deviceReqDTO.getEndDate());
            } catch (ParseException e) {
                log.error("日期转换异常", e);
            }
            if (start == null || end == null) {
                return Result.fail("日期格式有误");
            }
            String tableDate = DateUtils.parseDateToStr(DeviceConstants.CURRENT_MONTH_FORMAT, start);
            return Result.data(sensorAttrValueAllMongoDAO.pageSensorPeriodData(deviceReqDTO, tableDate));
        } catch (Exception e) {
            log.error("查询传感器时间段数据失败, e:", e);
            return Result.fail("查询传感器时间段数据失败");
        }
    }

    /**
     * 林业-查询范围内物联设备
     */
    @PostMapping("/querySensorPeriodData4Calculate")
    public Result<List<IotDeviceSensorCalculateVO>> querySensorPeriodData4Calculate(@RequestBody DeviceDistance4CalculateDTO param) {
        return Result.data(iotDeviceRepository.querySensorPeriodData4Calculate(param));
    }

    /**
     * 批量保存和更新设备信息
     *
     * @param: iotDevices
     * @return: Result<String>
     * @author: zheng.yan
     * @date: 2022/5/10
     * @version:
     */
    @PostMapping("/batchSaveOrUpdateIotDevice")
    @TowerLog(title = "批量保存和更新设备信息", businessType = BusinessType.UPDATE)
    public Result<String> batchSaveOrUpdateIotDevice(@RequestBody List<IotDeviceMsgDTO> iotDevices) {
        try {
            iotDeviceRepository.batchSaveOrUpdateIotDevice(iotDevices);
            return Result.success("批量保存/更新设备信息成功!");
        } catch (Exception e) {
            log.error("批量保存/更新设备信息失败, e:", e);
            return Result.fail("批量保存/更新设备信息失败!");
        }
    }

    /**
     *
     * 周边分析-周边物联设备信息搜索
     * @param param
     * @return
     */
    @Deprecated
    @PostMapping("/queryIotDevDistanceInfo")
    Result<List<DeviceDistanceVO>> queryIotDevDistanceInfo(@RequestBody DeviceDistanceDTO param) {
        return Result.data(iotDeviceRepository.queryIotDevDistanceInfo(param));
    }

    /**
     *
     * 周边分析-周边物联设备信息搜索
     * @param param
     * @return
     */
    @Deprecated
    @PostMapping("/queryIotDevDistanceInfo4App")
    Result<List<DeviceDistanceVO>> queryIotDevDistanceInfo4App(@RequestBody DeviceDistanceDTO param) {
        return Result.data(iotDeviceRepository.queryIotDevDistanceInfo4App(param));
    }

    /**
     * 批量查询传感器最新数据
     * @param deviceReqDTO
     * @return
     */
    @ApiOperation(value = "批量查询传感器最新数据")
    @PostMapping("/getSensorDataBatch")
    public Result<List<SensorAttrVO>> getSensorDataBatch(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            return iotDeviceRepository.getSensorDataBatch(deviceReqDTO);
        } catch (Exception e) {
            log.error("批量查询传感器最新数据异常, e:", e);
            return Result.fail("批量查询传感器最新数据失败异常");
        }
    }

    /**
     * 监测事件列表查询
     * @param deviceReqDTO
     * @return
     */
    @ApiOperation(value = "物联设备监测事件列表查询")
    @PostMapping("/getSensorEvent")
    public Result<SensorEventInfoVO> getSensorEvent(@RequestBody DeviceReqDTO deviceReqDTO) {

        try {
            log.info("传感器监测事件查询入参:{}",JSON.toJSONString(deviceReqDTO));
            SensorEventInfoVO sensorEventInfoVO = iotDeviceRepository.getSensorEventList(deviceReqDTO);
            log.info("传感器监测事件信息:{}",JSON.toJSONString(sensorEventInfoVO));
            if (!CollectionUtils.isEmpty(sensorEventInfoVO.getSensorEventList())){
                List<SensorEventVO> sensorEventList = sensorEventInfoVO.getSensorEventList();
                // eventId列表
                List<String> eventIdList = sensorEventList.stream()
                        .map(SensorEventVO::getEventId)
                        .filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
                List<SensorEventFileVO> sensorEventFileList = iotDeviceRepository.getSensorEventFileList(deviceReqDTO, eventIdList);
                if (!CollectionUtils.isEmpty(sensorEventFileList)){
                    Map<String, List<SensorEventFileVO>> sensorEventFileVOMap = sensorEventFileList.stream()
                            .filter(item -> StringUtils.isNotEmpty(item.getRelationId()))
                            .collect(Collectors.groupingBy(SensorEventFileVO::getRelationId));
                    for (SensorEventVO sensorEventVO : sensorEventList){
                        if (!CollectionUtils.isEmpty(sensorEventFileVOMap.get(sensorEventVO.getEventId()))){
                            List<SensorEventFileVO> eventFileList = sensorEventFileVOMap.get(sensorEventVO.getEventId());
                            List<FileInfoVO> fileInfoList = Lists.newArrayList();
                            for (SensorEventFileVO sensorEventFileVO : eventFileList){
                                if (!CollectionUtils.isEmpty(sensorEventFileVO.getFiles())){
                                    fileInfoList.addAll(sensorEventFileVO.getFiles());
                                }
                            }
                            sensorEventVO.setFile(fileInfoList);
                        }
                    }
                }
            }
            return Result.data(sensorEventInfoVO);
        }catch (Exception e){
            log.error("查询传感器监测事件失败, e:", e);
            return Result.fail("查询传感器监测事件失败");
        }
    }

    /**
     * 查询传感器最新数据V2
     *
     * @param deviceReqDTO 设备请求DTO，用于传递查询参数
     * @return 返回一个Result对象，包含一个SensorAttrVO列表，该列表包含传感器的最新数据
     */
    @ApiOperation(value = "查询传感器最新数据V2")
    @PostMapping("/getSensorData/v2")
    public Result<SensorAttrResultVO> getSensorDataV2(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            return iotDeviceRepository.getSensorDataV2(deviceReqDTO);
        } catch (Exception e) {
            log.error("查询传感器最新数据V2失败, e:", e);
            return Result.fail("获取传感器最新数据失败");
        }
    }

    /**
     * 物联设备管理-监测数据
     *
     * @return
     * @author zheng.yan
     * @date 2022/5/11
     * @version
     */
    @ApiOperation(value = "物联设备管理-监测数据v2")
    @PostMapping("/pageSensorPeriodData/v2")
    public Result<SensorPeriodPageResultVO> pageSensorPeriodDataV2(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            return iotDeviceRepository.pageSensorPeriodDataV2(deviceReqDTO);
        } catch (Exception e) {
            log.error("分页查询传感器监测数据异常, e:", e);
            return Result.fail("查询传感器监测数据失败");
        }
    }


    /**
     * 根据物联设备编码集合获取所包含的传感器和物联因子的关联信息
     *
     * @param deviceReqDTO 物联设备编码集合
     * @return 返回一个Result对象
     */
    @ApiOperation(value = "根据物联设备编码集合获取所包含的传感器和物联因子的关联信息")
    @PostMapping("/querySensorMonitorRelInfo")
    public Result<List<SensorMonitorRelInfoVo>> querySensorMonitorRelInfo(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            return iotDeviceRepository.querySensorMonitorRelInfo(deviceReqDTO);
        } catch (Exception e) {
            log.error("查询传感器最新数据失败, e:", e);
            return Result.fail("查询传感器最新数据失败");
        }
    }

    /**
     * 物联传感因子列表查询
     */
    @ApiOperation(value = "物联传感因子列表查询")
    @PostMapping("/qrySensorMonitor")
    public Result<PageInfo<SenSorMonitorConfigDO>> qrySensorMonitor(@RequestBody IotDeviceVO iotDeviceVO) {
        return iotDeviceRepository.qrySensorMonitor(iotDeviceVO);
    }

    /**
     * 物联传感因子列表查询
     */
    @ApiOperation(value = "分页查询物联/雷达图层")
    @PostMapping("/queryIotRadarDeviceSites")
    public Result<PageInfo<IndustryDeviceTree>> queryIotRadarDeviceSites(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(iotDeviceRepository.queryIotRadarDeviceSites(siteRequestDTO));

    }
}
