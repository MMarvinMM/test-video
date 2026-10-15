package com.chinatower.platform.project.sysbusiness.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.common.utils.DeviceTypeEnum;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.custom.entity.OrganDeviceVO;
import com.chinatower.platform.project.custom.entity.SatelliteInfoVO;
import com.chinatower.platform.project.custom.entity.ScreenDataVO;
import com.chinatower.platform.project.sysbusiness.common.Constants;
import com.chinatower.platform.project.sysbusiness.entity.LayerUrlVo;
import com.chinatower.platform.project.sysbusiness.entity.ScreenConfigVO;
import com.chinatower.platform.project.sysbusiness.entity.SysAreaBusinessVO;
import com.chinatower.platform.project.sysbusiness.mapper.DeptBusinessMapper;
import com.chinatower.platform.project.sysbusiness.service.IDeptBusinessService;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.domain.SysDeptStreetRel;
import com.chinatower.platform.project.system.domain.VmAlgorithmInfoT;
import com.chinatower.platform.project.system.domain.vo.SysDeptDetailVO;
import com.chinatower.platform.project.system.domain.vo.SysDeptStreetVO;
import com.chinatower.platform.project.system.mapper.SysDeptDeviceMapper;
import com.chinatower.platform.project.system.mapper.SysDeptMapper;
import com.chinatower.platform.project.system.mapper.SysDeptStreetRelMapper;
import com.chinatower.platform.project.system.mapper.SysDictDataMapper;
import com.chinatower.platform.project.system.service.ISysAreaService;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.project.system.service.ISysDictDataService;
import com.chinatower.platform.project.tool.kafka.KafkaProducer;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.*;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.ibatis.annotations.Case;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.platform.constants.Constants.PLATFORM_CODE;
import static com.chinatower.platform.constants.DeviceConstants.*;

@Slf4j
@Service(Constants.DEPT_BUSINESS_SERVICE)
public class DeptBusinessServiceImpl implements IDeptBusinessService {
    @Autowired
    DeptBusinessMapper deptBusinessMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private ISysAreaService areaService;

    @Autowired
    private ISysDeptService deptService;

    @Resource
    private SysDeptStreetRelMapper sysDeptStreetRelMapper;

    @Autowired
    private SysDeptDeviceMapper deptDeviceMapper;

    @Autowired
    private KafkaProducer kafkaProducer;

    private static final String TOPIC = "tenantAdministrativeRegionRelationship";

    @Autowired
    public AuthBaseMapper authBaseMapper;

    @Autowired
    public SysDictDataMapper sysDictDataMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDictDataService dictDataService;

    private void clearData(OrganDeviceVO organDeviceVO) {
        /** 清理雷达、摄像机、物联设备组织设备对应关系 */
        deptBusinessMapper.delOrganDevice(organDeviceVO);
        //删除组织与告警类型关系
        deptBusinessMapper.delOrganAlarmType(organDeviceVO);
        /** 清理卫星组织对应关系 */
        deptBusinessMapper.delOrganSatellite(organDeviceVO);
        /** 清理管理区域 */
        sysDeptStreetRelMapper.deleteDeptStreetRelByOrgId(organDeviceVO.getOrgId());
        /** 清理图层组织对应关系 */
        deptBusinessMapper.delOrganLayer(organDeviceVO);

    }


    /**
     * 获取告警类型map
     */
    @Override
    public Map<String, Set<String>>  getAlarmTypeMap() {
        // 1. 获取算法编码列表
        List<String> algorithmCodeList = getAlgorithmCodeList();
        // 2. 查询算法信息，构造map
        Map<String, Set<String>> map = buildParentChildAlarmTypeMap(algorithmCodeList);
        return map;
    }


    /**
     * 获取算法编码列表
     */
    public List<String> getAlgorithmCodeList() {
        return deptBusinessMapper.getLargeModelAlgorithms();
    }


    /**
     * 查询算法信息，存储到大型模型关系表
     */
    public Map<String, Set<String>> buildParentChildAlarmTypeMap(List<String> algorithmCodeList) {
        // 初始化返回的Map
        Map<String, Set<String>> parentChildAlarmTypeMap = new HashMap<>();
        if (CollectionUtils.isEmpty(algorithmCodeList)) {
            return parentChildAlarmTypeMap;
        }
        try {
            // 查询算法信息
            List<VmAlgorithmInfoT> algorithmInfos = deptBusinessMapper.selectByAlgorithmCodes(algorithmCodeList);
            for (VmAlgorithmInfoT algorithmInfo : algorithmInfos) {
                String parentAlarmTypeCode = algorithmInfo.getAlgoMinTypeCode();
                String modelConfig = algorithmInfo.getModelConfig();
                if (org.apache.commons.lang3.StringUtils.isBlank(parentAlarmTypeCode) || org.apache.commons.lang3.StringUtils.isBlank(modelConfig)) {
                    log.info("算法配置信息不全，算法编码：{}，父告警类型：{}，modelConfig：{}",
                            algorithmInfo.getAlgorithmCode(), parentAlarmTypeCode,
                            org.apache.commons.lang3.StringUtils.isBlank(modelConfig) ? "空" : "非空");
                    continue;
                }
                try {
                    // 解析model_config JSON数组
                    JSONArray modelConfigArray = JSON.parseArray(modelConfig);
                    // 为当前父告警类型创建一个新的Set
                    Set<String> currentChildCodes = new HashSet<>();
                    for (int i = 0; i < modelConfigArray.size(); i++) {
                        JSONObject configObj = modelConfigArray.getJSONObject(i);
                        String code = configObj.getString("code");
                        // 检查是否为dict-cvModeType或dict-moreModeType
                        if (code.toLowerCase().contains("modetype")) {
                            String defaultValue = configObj.getString("defaultValue");
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(defaultValue)) {
                                // 处理逗号分隔的字符串，拆分为多个子告警类型
                                String[] childCodes = defaultValue.split(",");
                                for (String childCode : childCodes) {
                                    String trimmedCode = childCode.trim();
                                    if (!trimmedCode.isEmpty()) {
                                        currentChildCodes.add(trimmedCode);
                                    }
                                }
                            }
                        }
                    }
                    // 如果当前算法有子告警类型
                    if (!currentChildCodes.isEmpty()) {
                        // 检查Map中是否已有这个父告警类型
                        if (parentChildAlarmTypeMap.containsKey(parentAlarmTypeCode)) {
                            // 如果已有，合并到已有的Set中
                            parentChildAlarmTypeMap.get(parentAlarmTypeCode).addAll(currentChildCodes);
                        } else {
                            // 如果没有，直接放入Map
                            parentChildAlarmTypeMap.put(parentAlarmTypeCode, currentChildCodes);
                        }
                    }
                } catch (Exception e) {
                    log.error("解析算法model_config失败，算法编码：{}", algorithmInfo.getAlgorithmCode(), e);
                    continue;
                }
            }
            log.info("构建父子告警类型映射完成，共 {} 个父告警类型", parentChildAlarmTypeMap.size());
            return parentChildAlarmTypeMap;
        } catch (Exception e) {
            log.error("构建父子告警类型映射失败", e);
            throw e;
        }
    }


    private void wrapSatelliteList(List<SatelliteInfoVO> list) {
        if ((null == list) || (list.isEmpty())) {
            return;
        }
        List<SysArea> sysAreas = areaService.selectSysAreas(list);
        Map<String, SysArea> stringSysAreaMap = sysAreas.stream().collect(Collectors.toMap(SysArea::getAreaCode, item -> item));
        list.forEach(v -> {
            if (StringUtils.isBlank(v.getCountyCode())) {
                return;
            }
//            SysArea sysArea = areaService.selectSysAreaById(v.getCountyCode());
            SysArea sysArea = stringSysAreaMap.get(v.getCountyCode());
                    switch (sysArea.getAreaLevel()) {
                case "2":
                    v.setProvinceCode(sysArea.getAreaCode());
                    v.setCountyCode(null);
                    break;
                case "3":
                    v.setProvinceCode(sysArea.getAreaParentCode());
                    v.setCityCode(sysArea.getAreaCode());
                    v.setCountyCode(null);
                    break;
                case "4":
                    v.setCityCode(sysArea.getAreaParentCode());
                    SysArea cityArea = areaService.selectSysAreaById(sysArea.getAreaParentCode());
                    v.setProvinceCode(cityArea.getAreaParentCode());
                    break;
                case "5":
                    v.setCountyCode(sysArea.getAreaParentCode());
                    v.setStreetCode(sysArea.getAreaCode());
                    SysArea countyArea = areaService.selectSysAreaById(sysArea.getAreaParentCode());
                    v.setCityCode(countyArea.getAreaParentCode());
                    SysArea cityArea2 = areaService.selectSysAreaById(countyArea.getAreaParentCode());
                    v.setProvinceCode(cityArea2.getAreaParentCode());
                    break;
                default:
                    break;
            }
        });
        log.info("list:{}", JSON.toJSONString(list));
    }

    private static final int BATCH_SIZE = 500;

    private void insertData(OrganDeviceVO organDeviceVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String operator = SecurityUtils.getOperator();
        organDeviceVO.setIndustryCode(user.getIndustryCode());
        organDeviceVO.setAppVerCode(user.getAppVerCode());
        List<SysDeptStreetRel> deptStreetRels = new ArrayList<>();
        /** 插入摄像机数据 - 分批处理避免 IN 子句过长 */
        if (CollUtil.isNotEmpty(organDeviceVO.getCameraCodeList())) {
            List<List<String>> partitions = Lists.partition(organDeviceVO.getCameraCodeList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setCameraCodeList(batch);
                deptBusinessMapper.addOrganCamera(batchVO);
                batchVO.setDeviceCodeList(batch);
                deptStreetRels.addAll(deptBusinessMapper.queryAreaByDevice(batchVO));
            }
        }
        /** 插入物联设备数据 - 分批处理 */
        if (CollUtil.isNotEmpty(organDeviceVO.getSensorCodeList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.SENSOR.getType());
            List<List<String>> partitions = Lists.partition(organDeviceVO.getSensorCodeList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setDeviceCodeList(batch);
                batchVO.setSensorCodeList(batch);
                deptBusinessMapper.addOrganDevice(batchVO);
                deptStreetRels.addAll(deptBusinessMapper.queryAreaBySensor(batchVO));
            }
        }
        /** 插入雷达数据 - 分批处理 */
        if (CollUtil.isNotEmpty(organDeviceVO.getRadarList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.RADAR.getType());
            List<List<String>> partitions = Lists.partition(organDeviceVO.getRadarList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setDeviceCodeList(batch);
                deptBusinessMapper.addOrganDevice(batchVO);
                deptStreetRels.addAll(deptBusinessMapper.queryAreaByRadar(batchVO));
            }
        }
        /** 插入卫星数据 */
        if (CollUtil.isNotEmpty(organDeviceVO.getSatelliteResultList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.SATELLITE.getType());
            wrapSatelliteList(organDeviceVO.getSatelliteResultList());
            List<List<SatelliteInfoVO>> partitions = Lists.partition(organDeviceVO.getSatelliteResultList(), BATCH_SIZE);
            for (List<SatelliteInfoVO> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setSatelliteResultList(batch);
                deptBusinessMapper.addOrganSatellite(batchVO);
                batch.forEach(s -> {
                    SysDeptStreetRel rel = new SysDeptStreetRel(organDeviceVO.getOrgId(), s.getStreetCode());
                    rel.setCityCode(s.getCityCode());
                    rel.setCountyCode(s.getCountyCode());
                    rel.setProvinceCode(s.getProvinceCode());
                    rel.setIndustryCode(organDeviceVO.getIndustryCode());
                    rel.setAppVerCode(organDeviceVO.getAppVerCode());
                    rel.setTenantId(organDeviceVO.getTenantId());
                    rel.setCreateBy(user.getUserName());
                    rel.setCreateTime(new Date());
                    deptStreetRels.add(rel);
                });
            }
        }

        //add by liuxt 组织管理优化需求 start 20230612
        /** 插入无人机数据 - 分批处理 */
        if (CollectionUtils.isNotEmpty(organDeviceVO.getUavList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.UAV.getType());
            List<List<String>> partitions = Lists.partition(organDeviceVO.getUavList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setDeviceCodeList(batch);
                deptBusinessMapper.addOrganDevice(batchVO);
                deptStreetRels.addAll(deptBusinessMapper.queryAreaByUav(batchVO));
            }
        }

        /** 插入无人机机场数据 - 分批处理 */
        if (CollectionUtils.isNotEmpty(organDeviceVO.getUavNestList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.NEST.getType());
            List<List<String>> partitions = Lists.partition(organDeviceVO.getUavNestList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setDeviceCodeList(batch);
                deptBusinessMapper.addOrganDevice(batchVO);
                deptStreetRels.addAll(deptBusinessMapper.queryAreaByUavNest(batchVO));
            }
        }
        /** 插入大喇叭数据 - 分批处理 */
        if (CollectionUtils.isNotEmpty(organDeviceVO.getHornList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.HORN.getType());
            List<List<String>> partitions = Lists.partition(organDeviceVO.getHornList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setDeviceCodeList(batch);
                deptBusinessMapper.addOrganDevice(batchVO);
                deptStreetRels.addAll(deptBusinessMapper.queryAreaByHorn(batchVO));
            }
        }
        //add by liuxt 组织管理优化需求 end 20230612

        /** 插入告警类型数据 - 分批处理 */
        if (CollectionUtils.isNotEmpty(organDeviceVO.getAlarmTypeList())) {
            Map<String, Set<String>> typeValueMap = getAlarmTypeMap();
            // 收集所有需要插入的告警类型记录
            List<OrganAlarmTypeVO> allAlarmTypesToInsert = new ArrayList<>();
            organDeviceVO.getAlarmTypeList().stream().forEach(i -> {
                i.setTenantId(user.getTenantId());
                i.setIndustryCode(user.getIndustryCode());
                i.setAppVerCode(user.getAppVerCode());
                i.setOrgId(organDeviceVO.getOrgId());
                i.setCreator(operator);
                String typeValue = i.getTypeValue();
                // 检查是否为特定值
                if (typeValueMap.containsKey(typeValue)) {
                    // 如果是特定值，获取对应的值列表
                    Set<String> mappedValues = typeValueMap.get(typeValue);
                    // 为每个映射值创建一条新记录
                    for (String mappedValue : mappedValues) {
                        OrganAlarmTypeVO newAlarmType = new OrganAlarmTypeVO();
                        // 复制所有属性
                        BeanUtils.copyProperties(i, newAlarmType);
                        // 设置映射后的 typeValue
                        newAlarmType.setTypeValue(mappedValue);
                        allAlarmTypesToInsert.add(newAlarmType);
                    }
                }
                allAlarmTypesToInsert.add(i);
            });
            // 分批插入告警类型
            if (!allAlarmTypesToInsert.isEmpty()) {
                List<List<OrganAlarmTypeVO>> partitions = Lists.partition(allAlarmTypesToInsert, BATCH_SIZE);
                for (List<OrganAlarmTypeVO> batch : partitions) {
                    OrganDeviceVO insertVO = new OrganDeviceVO();
                    BeanUtils.copyProperties(organDeviceVO, insertVO);
                    insertVO.setAlarmTypeList(batch);
                    deptBusinessMapper.addOrganAlarmType(insertVO);
                }
            }
        }

        /** 插入执法记录仪数据 - 分批处理 */
        if (CollectionUtils.isNotEmpty(organDeviceVO.getLERList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.LER.getType());
            List<List<String>> partitions = Lists.partition(organDeviceVO.getLERList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setDeviceCodeList(batch);
                deptBusinessMapper.addOrganDevice(batchVO);
                deptStreetRels.addAll(deptBusinessMapper.queryAreaByLER(batchVO));
            }
        }
        /** 插入图层数据 - 分批处理 */
        if (CollUtil.isNotEmpty(organDeviceVO.getLayerList())) {
            organDeviceVO.setCategoryCode(DeviceTypeEnum.LAYER.getType());
            List<List<String>> partitions = Lists.partition(organDeviceVO.getLayerList(), BATCH_SIZE);
            for (List<String> batch : partitions) {
                OrganDeviceVO batchVO = new OrganDeviceVO();
                BeanUtils.copyProperties(organDeviceVO, batchVO);
                batchVO.setDeviceCodeList(batch);
                deptBusinessMapper.addOrganLayer(batchVO);
            }
        }
        /** 插入管理区域数据 - 分批处理 */
        if (CollUtil.isNotEmpty(deptStreetRels)) {

            deptStreetRels.stream().forEach(sysDeptStreetRel -> {
                sysDeptStreetRel.setTenantId(user.getTenantId());
                sysDeptStreetRel.setIndustryCode(user.getIndustryCode());
                sysDeptStreetRel.setAppVerCode(user.getAppVerCode());
                sysDeptStreetRel.setDeptId(organDeviceVO.getOrgId());
            });
            List<List<SysDeptStreetRel>> partitions = Lists.partition(deptStreetRels, BATCH_SIZE);
            for (List<SysDeptStreetRel> batch : partitions) {
                sysDeptStreetRelMapper.insertBatchDeptStreetRelTenant(batch);
            }
        }

    }

    public void updateScreenData(SysUser user) {
        SysDept dept = new SysDept();
        dept.setIndustryCode(user.getIndustryCode());
        dept.setAppVerCode(user.getAppVerCode());
        dept.setTenantId(user.getTenantId());
        //查询开启自定义的大屏配置
        List<ScreenDataVO> screenData = deptBusinessMapper.getScreenData(dept);
        //查询所有组织下权限
        List<String> deptIdList = deptService.getDeptIdList(dept);
        SysDeptDetailVO sysDeptDetailVO = deptService.deptDeviceDetailBatch(deptIdList);
        //按照大屏类型分组
        Map<String, List<ScreenDataVO>> idCollect = screenData.stream().collect(Collectors.groupingBy(ScreenDataVO::getCountId));
        List<ScreenDataVO> needUpdateList = new ArrayList<>();
        List<ScreenDataVO> needDeleteList = new ArrayList<>();
        for (String key : idCollect.keySet()) {
            List<ScreenDataVO> screenDataVOS = idCollect.get(key);
            ScreenDataVO screenDataVO = screenDataVOS.get(0);
            //按照数据类型分组  0-摄像机1-物联设备2-雷达3-喇叭4-无人机5-执法记录仪6-网格7-网格员8-资源9-告警类型
            Map<String, List<ScreenDataVO>> typeCollect = screenDataVOS.stream().collect(Collectors.groupingBy(ScreenDataVO::getDataType));
            for (String typekey : typeCollect.keySet()) {
                List<String> dataCodeList = typeCollect.get(typekey).stream().map(ScreenDataVO::getDataCode).collect(Collectors.toList());
                //判断是否有去除的权限
                switch (typekey) {
                    case DeviceConstants.VALID_STATE:
                        int cameraNum = setNeedDeleteList(screenDataVO.getCameraNum(),key, typekey, dataCodeList, sysDeptDetailVO.getCameras(), needDeleteList);
                        screenDataVO.setCameraNum(String.valueOf(cameraNum));
                        break;
                    case DeviceConstants.YES_FLAG:
                        int iotNum = setNeedDeleteList(screenDataVO.getIotNum(),key, typekey, dataCodeList, sysDeptDetailVO.getSensors(), needDeleteList);
                        screenDataVO.setIotNum(String.valueOf(iotNum));
                        break;
                    case DeviceConstants.TWO_FLAG:
                        int radarNum = setNeedDeleteList(screenDataVO.getRadarNum(),key, typekey, dataCodeList, sysDeptDetailVO.getRadars(), needDeleteList);
                        screenDataVO.setRadarNum(String.valueOf(radarNum));
                        break;
                    case DeviceConstants.THREE_FLAG:
                        int speakerNum = setNeedDeleteList(screenDataVO.getSpeakerNum(),key, typekey, dataCodeList, sysDeptDetailVO.getHorns(), needDeleteList);
                        screenDataVO.setSpeakerNum(String.valueOf(speakerNum));
                        break;
                    case DeviceConstants.FOUR_FLAG:
                        int uavNum = setNeedDeleteList(screenDataVO.getUavNum(),key, typekey, dataCodeList, sysDeptDetailVO.getUavs(), needDeleteList);
                        screenDataVO.setUavNum(String.valueOf(uavNum));
                        break;
                    case DeviceConstants.FIVE_FLAG:
                        int recorderNum =  setNeedDeleteList(screenDataVO.getRecorderNum(),key, typekey, dataCodeList, sysDeptDetailVO.getLERs(), needDeleteList);
                        screenDataVO.setRecorderNum(String.valueOf(recorderNum));
                        break;
                    case DeviceConstants.NINE_FLAG:
                        if(!sysDeptDetailVO.getAlarmTypeList().isEmpty()){
                            List<OrganAlarmTypeVO> alarmTypeList = sysDeptDetailVO.getAlarmTypeList();
                            Map<String, List<OrganAlarmTypeVO>> newAlarmCollect = alarmTypeList.stream().collect(Collectors.groupingBy(OrganAlarmTypeVO::getWarningSource));
                            Map<String, List<ScreenDataVO>> oldAlarmCollect = typeCollect.get(typekey).stream().collect(Collectors.groupingBy(ScreenDataVO::getDataSource));
                            int alarmNum = 0;
                            for (String alarm : oldAlarmCollect.keySet()) {
                                List<OrganAlarmTypeVO> organAlarmTypeVOS = newAlarmCollect.get(alarm);
                                List<ScreenDataVO> screenDataVOList = oldAlarmCollect.get(alarm);
                                List<String> oldList = screenDataVOList.stream().map(ScreenDataVO::getDataCode).collect(Collectors.toList());
                                //判断告警来源是否有数剧，没有直接删除
                                int num = 0;
                                if(CollectionUtils.isNotEmpty(organAlarmTypeVOS) && !organAlarmTypeVOS.isEmpty()){
                                    List<String> newList = organAlarmTypeVOS.stream().map(OrganAlarmTypeVO::getTypeValue).collect(Collectors.toList());
                                    List<String> unknownDataCodes = oldList.stream()
                                            .filter(code -> !newList.contains(code))
                                            .collect(Collectors.toList());
                                    if(CollectionUtils.isNotEmpty(unknownDataCodes) && !unknownDataCodes.isEmpty()){
                                        for (String unknownDataCode : unknownDataCodes) {
                                            ScreenDataVO dataVO = getScreenDataVO(key, typekey, unknownDataCode);
                                            dataVO.setDataSource(alarm);
                                            needDeleteList.add(dataVO);
                                            num += 1;
                                        }
                                    }
                                }else{
                                    //没权限数据全部删除
                                    for (String unknownDataCode : oldList) {
                                        ScreenDataVO dataVO = getScreenDataVO(key, typekey, unknownDataCode);
                                        dataVO.setDataSource(alarm);
                                        needDeleteList.add(dataVO);
                                    }
                                    num = oldList.size();
                                }
                                alarmNum += num;
                            }
                            screenDataVO.setAlarmTypeNum(String.valueOf(Integer.parseInt(screenDataVO.getAlarmTypeNum()) - alarmNum));
                        }
                        break;
                    default:
                        break;
                }
            }
            needUpdateList.add(screenDataVO);
        }
        //更新大屏数量及删除详情
        if(!needUpdateList.isEmpty()){
            deptBusinessMapper.updateScreenData(needUpdateList);
        }
        if(!needDeleteList.isEmpty()){
            deptBusinessMapper.deleteScreenDataConfig(needDeleteList);
        }
    }

    private static int setNeedDeleteList(String dataNum , String key, String typekey, List<String> dataCodeList, List<SysDeptDeviceVO> list, List<ScreenDataVO> needDeleteList) {
        //有权限数据比较删除
        int num = 0;
        if(CollectionUtils.isNotEmpty(list) && !list.isEmpty()){
            List<String> collect = list.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList());
            List<String> unknownDataCodes = dataCodeList.stream()
                    .filter(code -> !collect.contains(code))
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(unknownDataCodes) && !unknownDataCodes.isEmpty()){
                for (String unknownDataCode : unknownDataCodes) {
                    ScreenDataVO screenDataVO = getScreenDataVO(key, typekey, unknownDataCode);
                    needDeleteList.add(screenDataVO);
                    num += 1;
                }
            }
        }else{
            //没权限数据全部删除
            for (String unknownDataCode : dataCodeList) {
                ScreenDataVO screenDataVO = getScreenDataVO(key, typekey, unknownDataCode);
                needDeleteList.add(screenDataVO);
            }
            num = dataCodeList.size();
        }
        return  Integer.parseInt(dataNum) - num;
    }

    private static ScreenDataVO getScreenDataVO(String key, String typekey, String unknownDataCode) {
        ScreenDataVO screenDataVO = new ScreenDataVO();
        screenDataVO.setCountId(key);
        screenDataVO.setDataType(typekey);
        screenDataVO.setDataCode(unknownDataCode);
        return screenDataVO;
    }

    /**
     * 保存组织设备关系
     *
     * @param
     * @return 保存组织设备关系
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveDeptDeviceRef(OrganDeviceVO organDeviceVO) {
        log.info("保存组织设备关系:{}", organDeviceVO);
        LoginUser currentUser = SecurityUtils.getLoginUser();
        organDeviceVO.setCreator(currentUser.getUser().getUserId());
        organDeviceVO.setModifier(currentUser.getUser().getUserId());
        organDeviceVO.setTenantId(currentUser.getUser().getTenantId());
        String tenantId = currentUser.getUser().getTenantId();
        /** 清理数据 */
        clearData(organDeviceVO);
        /** 重新插入数据 */
        insertData(organDeviceVO);
        /** 更新大屏数据配置 */
        try {
            updateScreenData(currentUser.getUser());
        } catch (Exception e) {
            log.error("更新大屏数据配置失败", e);
        }
        /** 发送Kafka消息 */
        try {
            JSONObject json = new JSONObject();
            json.put("tenantId", tenantId);
            kafkaProducer.sendChannelMessage(TOPIC, json.toJSONString());
        } catch (Exception e) {
            log.error("发送Kafka消息失败", e);
        }
        return Result.success(Constants.SUCCESS_MSG);
    }


    /**
     * 获取组织设备关系详情树
     *
     * @param
     * @return 获取组织设备关系详情树
     */
    @Override
    public Result getDeviceTree(OrganDeviceVO organDeviceVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        organDeviceVO.setTenantId(currentUser.getUser().getTenantId());
        if (StringUtils.isEmpty(organDeviceVO.getTenantId())) {
            SysDept sysDeptVO = null;
            if (currentUser.getUser().getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
                sysDeptVO = deptMapper.selectDeptById(organDeviceVO.getOrgId(), null, null);
            } else {
                sysDeptVO = deptMapper.selectDeptById(organDeviceVO.getOrgId(), currentUser.getUser().getIndustryCode(), currentUser.getUser().getAppVerCode());
            }
            if (sysDeptVO != null) {
                organDeviceVO.setTenantId(sysDeptVO.getTenantId());
            }

        }

        List<TreeSelect> tree = new ArrayList<>();
        List<SysAreaBusinessVO> chanList;
        if (organDeviceVO.getCategoryCode() == 8) {
            chanList = deptBusinessMapper.getSensorList(organDeviceVO);
        } else if (organDeviceVO.getCategoryCode() == 99) {
            chanList = deptBusinessMapper.getAreaList(organDeviceVO);
        } else if (organDeviceVO.getCategoryCode() == 9) {
            chanList = deptBusinessMapper.getSatelliteList(organDeviceVO);
        } else {
            chanList = deptBusinessMapper.getDeviceList(organDeviceVO);
        }
        Set<SysAreaBusinessVO> areaSet = new HashSet();
        List<SysAreaBusinessVO> arealist = deptBusinessMapper.selectSysArea();
        List<SysAreaBusinessVO> removeArealist = new ArrayList<>();
        for (SysAreaBusinessVO sysAreaBusinessVO : arealist) {
            for (SysAreaBusinessVO vo : chanList) {
                if (vo != null) {
                    if (StringUtils.isEmpty(vo.getProvinceId()) || StringUtils.isEmpty(vo.getCityId()) || StringUtils.isEmpty(vo.getCountyId()) || StringUtils.isEmpty(vo.getTownId())) {
                        removeArealist.add(vo);
                    } else if (vo.getProvinceId().equals(sysAreaBusinessVO.getAreaCode()) || vo.getCityId().equals(sysAreaBusinessVO.getAreaCode()) || vo.getCountyId().equals(sysAreaBusinessVO.getAreaCode()) || vo.getTownId().equals(sysAreaBusinessVO.getAreaCode())) {
                        areaSet.add(sysAreaBusinessVO);
                    }
                }
            }
        }
        if (organDeviceVO.getCategoryCode() == 99 || organDeviceVO.getCategoryCode() == 9) {
            List<SysAreaBusinessVO> area = new ArrayList<>(areaSet);
            for (SysAreaBusinessVO vo : area) {
                tree.add(new TreeSelect(vo.getAreaCode(), vo.getAreaName(), vo.getAreaParentCode(), vo.getAreaLevel()));
            }
        } else {
            chanList.removeAll(removeArealist);
            chanList.addAll(areaSet);
            for (SysAreaBusinessVO vo : chanList) {
                tree.add(new TreeSelect(vo.getAreaCode(), vo.getAreaName(), vo.getAreaParentCode(), vo.getAreaLevel()));
            }
        }

        return Result.data(TreeUtils.listToTreeSelect(tree));


    }


    /**
     * 获取设备组织关系
     *
     * @param
     * @return 获取设备组织关系
     */
    @Override
    public Result getDeptDeviceRefList(OrganDeviceVO organDeviceVO) {
        List<String> result;
        if (organDeviceVO.getCategoryCode() == 9) {
            result = deptBusinessMapper.getDeptSatelliteRefList(organDeviceVO);
        } else {
            result = deptBusinessMapper.getDeptDeviceRefList(organDeviceVO);
        }
        return Result.data(result);
    }

    /**
     * 判断人员是否在流程处置中
     *
     * @param
     * @return 获取设备组织关系
     */
    @Override
    @DS(Constants.DATA_SOURCE_9)
    public Result checkProcessByUser(String[] userIds) {
        int difMonth = this.getDifMonth(DateUtils.dateTime(DateUtils.DATE_TIME_YM_FORMAT, Constants.START_DATE_STR), DateUtils.getNowDate());
        if (difMonth > Constants.SIX) {
            difMonth = Constants.SIX;
        }
        String time = DateUtils.dateToStringYM(DateUtils.getNowDate());
        for (int i = Constants.ZERO; i < difMonth; i++) {
            if (i > Constants.ZERO) {
                time = DateUtils.getlastMonth(time);
            }
            String tableName = Constants.WARNING_TABLE_NAME_STR + time;
            int count = deptBusinessMapper.getProcessByUserCount(userIds, tableName);
            if (count > Constants.ZERO) {
                return Result.data(false);
            }
        }
        return Result.data(true);
    }

    @Override
    public Result<List<LayerUrlVo>> getLayerUrl() {
        List<LayerUrlVo> resultList = new ArrayList<>();
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();//由行业版本编码改为行业编码
        log.info("行业编码industryCode:" + industryCode);
        if (!StringUtils.isEmpty(industryCode)) {
            String appLayerUrl = sysDictDataMapper.selectDictLabel("app_layer_url", industryCode);
            if(!StringUtils.isEmpty(appLayerUrl)){
                String[] split = appLayerUrl.split(",");
                Arrays.sort(split);
                for (String s : split) {
                    LayerUrlVo layerUrlVo = new LayerUrlVo();
                    layerUrlVo.setCode(s);
                    layerUrlVo.setIndustryCode(industryCode);
                    resultList.add(layerUrlVo);
                }
            }
        }
        return Result.data(resultList);
    }

    /**
     * 获取月份差
     *
     * @param
     * @return 获取月份差
     */
    public static Integer getDifMonth(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);
        int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result);
    }

    @Override
    public AuthObjDataVO getDataScopeObj(SysUser sysUser) {
        AuthObjDataVO resObj = new AuthObjDataVO();
        String tenantId = sysUser.getTenantId();
        String industryCode = sysUser.getIndustryCode();
        String platformCode = getCascadeCode();
        long s1 = System.currentTimeMillis();
        Set<DeptVO> deptList = deptService.getDeptListByUserDataScope(sysUser);
        Set<String> dataScopes = sysUser.getRoles().stream().map(BaseDataScopeEntity::getDataScope).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        //是否所有权限
        boolean dataScopeAll = false;
        if (dataScopes.contains(Constants.DATA_SCOPE_ALL)) {
            dataScopeAll = true;
        }
        long s2 = System.currentTimeMillis();
        resObj.setDeptList(deptList);
        List<String> deptIds = null;
        if (deptList != null && !deptList.isEmpty()){
            deptIds = new ArrayList<>();
            for(DeptVO d: deptList){
                deptIds.add(d.getDeptId());
            }
        }

        List<DeviceVO> cameras = null;
        if (!dataScopeAll){
            cameras = deptDeviceMapper.getCamerasByTenantIdUser(tenantId, industryCode, deptIds ,platformCode);
        }else {
            cameras = deptDeviceMapper.getCamerasByTenantId(tenantId, industryCode,platformCode);
        }
        long s3 = System.currentTimeMillis();
        List<DeviceVO> raders = null;
        if (!dataScopeAll){
            raders = deptDeviceMapper.getRadersByTenantIdUser(tenantId, industryCode, deptIds,platformCode);
        }else {
            raders = deptDeviceMapper.getRadersByTenantId(tenantId, industryCode,platformCode);
        }
        long s4 = System.currentTimeMillis();
        List<DeviceVO> sensors = null;
        if (!dataScopeAll){
            sensors = deptDeviceMapper.getSensorsByTenantIdUser(tenantId, industryCode, deptIds,platformCode);
        }else {
            sensors = deptDeviceMapper.getSensorsByTenantId(tenantId, industryCode,platformCode);
        }
        long s5 = System.currentTimeMillis();
        List<DeviceVO> satellites = null;
        if (!dataScopeAll){
            satellites = deptDeviceMapper.getSatellitesByTenantIdUser(tenantId, industryCode, deptIds,platformCode);
        }else {
            satellites = deptDeviceMapper.getSatellitesByTenantId(tenantId, industryCode,platformCode);
        }

        List<SysDeptDeviceVO> uavs= null;
        if (!dataScopeAll){
            uavs = deptDeviceMapper.getUavsByTenantIdUser(tenantId, industryCode, deptIds,platformCode);
        }else {
            uavs = deptDeviceMapper.getUavsScop(tenantId, industryCode,platformCode);
        }

        List<SysDeptDeviceVO> horns = null;
        if (!dataScopeAll){
            horns = deptDeviceMapper.getHornsByTenantIdUser(tenantId, industryCode, deptIds,platformCode);
        }else {
            horns = deptDeviceMapper.getHornsScop(tenantId, industryCode,platformCode);
        }

        List<SysDeptDeviceVO> LERs = null;
        if (!dataScopeAll){
            LERs = deptDeviceMapper.getLERsByTenantIdUser(tenantId, industryCode, deptIds,platformCode);
        }else {
            LERs = deptDeviceMapper.getLERsScop(tenantId, industryCode,platformCode);
        }

        resObj.setCameras(cameras);
        resObj.setRadars(raders);
        resObj.setSatellites(satellites);
        resObj.setSensors(sensors);
        resObj.setUavs(uavs);
        resObj.setHorns(horns);
        resObj.setLERs(LERs);
        resObj.setLawEnforce(LERs);

        long s6 = System.currentTimeMillis();
        List<AreaDataVO> areas = this.getDeptStreetScop(resObj);
        long s7 = System.currentTimeMillis();
        resObj.setAreaList(areas);

        log.info("getDataScopeObj:{}-{}查询组织耗时{}ms", tenantId, industryCode, (s2 - s1));
        log.info("getDataScopeObj:{}-{}查询摄像机耗时{}ms", tenantId, industryCode, (s3 - s2));
        log.info("getDataScopeObj:{}-{}查询雷达耗时{}ms", tenantId, industryCode, (s4 - s3));
        log.info("getDataScopeObj:{}-{}查询物联设备耗时{}ms", tenantId, industryCode, (s5 - s4));
        log.info("getDataScopeObj:{}-{}查询卫星火点耗时{}ms", tenantId, industryCode, (s6 - s5));
        log.info("getDataScopeObj:{}-{}查询管理区域耗时{}ms", tenantId, industryCode, (s7 - s6));


        return resObj;
    }


    @Override
    public AuthObjDataVO getDataScopeObj(String tenantId, String industryCode) {
        AuthObjDataVO resObj = new AuthObjDataVO();
        String platformCode = getCascadeCode();
        List<DeviceVO> cameras = deptDeviceMapper.getCamerasByTenantIdUser(tenantId, industryCode, null,platformCode);
        List<DeviceVO> raders = deptDeviceMapper.getRadersByTenantIdUser(tenantId, industryCode, null,platformCode);
        List<DeviceVO> sensors = deptDeviceMapper.getSensorsByTenantIdUser(tenantId, industryCode, null,platformCode);
        List<DeviceVO> satellites = deptDeviceMapper.getSatellitesByTenantIdUser(tenantId, industryCode, null,platformCode);
        List<SysDeptDeviceVO> uavs = deptDeviceMapper.getUavsScop(tenantId, industryCode,platformCode);
        List<SysDeptDeviceVO> horns = deptDeviceMapper.getHornsScop(tenantId, industryCode,platformCode);
        List<SysDeptDeviceVO> LERs = deptDeviceMapper.getLERsScop(tenantId, industryCode,platformCode);
        resObj.setCameras(cameras);
        resObj.setRadars(raders);
        resObj.setSatellites(satellites);
        resObj.setSensors(sensors);
        resObj.setUavs(uavs);
        resObj.setHorns(horns);
        resObj.setLERs(LERs);
        resObj.setAreaList(this.getDeptStreetScop(resObj));
        return resObj;
    }

    private String getCascadeCode() {
        String platformCode = null;
        SysConfig sysConfig = configService.selectConfigByKey(PLATFORM_CODE);
        if(!ObjectUtils.isEmpty(sysConfig)) {
            platformCode = sysConfig.getConfigValue();
        }
        return platformCode;
    }

    private Set<String> getAreaCode(List<SysDeptStreetVO> list) {
        Set<String> set = new HashSet<>();
        if (list != null) {
            list.forEach(s -> {
                if (StringUtils.isNotBlank(s.getProvinceCode())) {
                    set.add(s.getProvinceCode());
                }
                if (StringUtils.isNotBlank(s.getCityCode())) {
                    set.add(s.getCityCode());
                }
                if (StringUtils.isNotBlank(s.getCountyCode())) {
                    set.add(s.getCountyCode());
                }
                if (StringUtils.isNotBlank(s.getStreetCode())) {
                    set.add(s.getStreetCode());
                }
            });
        }
        return set;
    }

    /**
     * 功能说明:根据当前用户权限获取行政区划树
     *
     * @param
     * @return List<TreeSelect>
     * @throws
     */
    @Override
    public List<TreeSelect> getAreaTreeSelect() {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        List<TreeSelect> tree = new ArrayList<>();
        SysDept dept = sysUser.getDept();
        dept.setIndustryCode(sysUser.getIndustryCode());
        dept.setTenantId(sysUser.getTenantId());
        List<SysDeptStreetVO> streets = sysDeptStreetRelMapper.selectStreet(sysUser.getDept());
        if (streets != null && streets.size() > 0) {
            Set<String> areaSets = getAreaCode(streets);
            List<SysArea> list = deptBusinessMapper.getSysAreaListAreaCode(areaSets);

            for (SysArea item : list) {
                if (Objects.isNull(item.getAreaCode()) || Objects.isNull(item.getAreaParentCode())) {
                    log.error("sys_area数据异常->{}", JSON.toJSONString(item));
                    continue;
                }
                tree.add(new TreeSelect(item.getAreaCode(), item.getAreaName(), item.getAreaParentCode(), item.getDeptId(), item.getAreaLevel()));
            }
        }

        return TreeUtils.listToTreeSelect(tree);
    }

    /**
     * 匹配url路径
     *
     * @param urls
     * @param requestPath
     * @return
     */
    public static boolean matchUrl(Set<String> urls, String requestPath) {
        if (null == urls || urls.isEmpty()) {
            return false;
        }
        if (urls.contains("/**")) {
            return true;
        }
        String urlStr = urls.toString();
        int count = urlStr.indexOf(requestPath);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<SysAreaVO> getAreaStreet(String regions) {
//        String[] region = regions.split(",");
//        List<SysAreaVO> areaList = new ArrayList<>();
//        for (String regionCode : region) {
//            SysAreaVO sysAreaVO = sysDeptStreetRelMapper.getSysProvinceTown(regionCode);
//            sysAreaVO.setRegionCode(regionCode);
//            List<String> list = sysDeptStreetRelMapper.getSysStreet(regionCode);
//            sysAreaVO.setStreetCodes(list);
//            areaList.add(sysAreaVO);
//        }
        List<String> regionCodeList = Arrays.asList(regions.split(","));
        List<SysAreaVO> areaList = sysDeptStreetRelMapper.getSysProvinceTown(regionCodeList);
        List<SysArea> childList = sysDeptStreetRelMapper.getSysStreet(regionCodeList);
        for (SysAreaVO parentArea : areaList) {
            List<String> streetCodes = new ArrayList<>();
            for(SysArea childArea : childList) {
                if(parentArea.getRegionCode().equals(childArea.getAreaParentCode())){
                    streetCodes.add(childArea.getAreaCode());
                }
            }
            parentArea.setStreetCodes(streetCodes);
        }
        return areaList;
    }

    @Override
    public AuthObjDataVO getDataPermissionsObj(SysUser sysUser) {
        AuthObjDataVO resObj = new AuthObjDataVO();
        String tenantId = sysUser.getTenantId();
        String industryCode = sysUser.getIndustryCode();
        long s1 = System.currentTimeMillis();
        Set<DeptVO> deptList = deptService.getDeptListByUserDataScope(sysUser);
        Set<String> dataScopes = sysUser.getRoles().stream().map(BaseDataScopeEntity::getDataScope).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        //是否所有权限
        boolean dataScopeAll = false;
        if (dataScopes.contains(Constants.DATA_SCOPE_ALL)) {
            dataScopeAll = true;
        }
        resObj.setDeptList(deptList);
        List<String> deptIds = null;
        if (deptList != null && !deptList.isEmpty()){
            deptIds = new ArrayList<>();
            for(DeptVO d: deptList){
                deptIds.add(d.getDeptId());
            }
        }

        List<SysDeptDeviceVO> layers = null;
        if (!dataScopeAll){
            layers = deptDeviceMapper.getLayersByTenantIdUser(tenantId, industryCode, deptIds);
        }else {
            layers = deptDeviceMapper.getLayerScop(tenantId, industryCode);
        }

        resObj.setLayars(layers);

        return resObj;
    }

    @Override
    public AuthObjDataVO getDataPermissionsObj(String tenantId, String industryCode) {
        //只返回图层逻辑
        AuthObjDataVO resObj = new AuthObjDataVO();

        List<SysDeptDeviceVO> layers = deptDeviceMapper.getLayerScop(tenantId, industryCode);

        resObj.setLayars(layers);
        return resObj;
    }

    private void joinRoleDeptParameter(List<List<Long>> partition, StringBuffer stringBuffer, int i) {
        List<Long> strings = partition.get(i);
        stringBuffer.append("(");
        for (Long id : strings
        ) {
            stringBuffer.append(id + ",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    private String getRoleDeptInParameter(Long roleId) {
        List<Long> list = RedisUtil.getList(CacheConstants.ROLE_SCOPE_LIST_PREFIX + roleId, Long.class, RedisDBType.PRIMARY);
        StringBuffer stringBuffer = new StringBuffer();
        if (CollUtil.isNotEmpty(list)) {
            List<List<Long>> partition = ListUtils.partition(list, 900);
            if (CollUtil.isNotEmpty(partition)) {
                for (int i = 0; i < partition.size(); i++) {
                    if (i != partition.size() - 1) {
                        joinRoleDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(") or ");
                    }
                    if (i == partition.size() - 1) {
                        joinRoleDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(")");
                    }
                }
            }
        }
        return stringBuffer.toString();
    }

    private void joinDeptParameter(List<List<String>> partition, StringBuffer stringBuffer, int i) {
        List<String> strings = partition.get(i);
        stringBuffer.append("(");
        for (String id : strings
        ) {
            stringBuffer.append("'" + id + "',");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    private String getTenantDeptInParameter() {
        List<String> childDeptIdList = SecurityUtils.getTenantChildDeptIdList();
        StringBuffer stringBuffer = new StringBuffer();
        if (CollUtil.isNotEmpty(childDeptIdList)) {
            List<List<String>> partition = ListUtils.partition(childDeptIdList, 900);
            if (CollUtil.isNotEmpty(partition)) {
                for (int i = 0; i < partition.size(); i++) {
                    if (i != partition.size() - 1) {
                        joinDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(") or ");
                    }
                    if (i == partition.size() - 1) {
                        joinDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(")");
                    }
                }
            }
        }
        return stringBuffer.toString();
    }

//    @Override
//    public UserAuthData getUserAuthData(SysUser sysUser) {
//        UserAuthData userAuthData = new UserAuthData();
//        String tenantId = sysUser.getTenantId();
//        String userId = sysUser.getUserId();
//        String isTenantAdmin = sysUser.getIsTenantAdmin();
//        //根据数据权限获取deptId部分sql
//        String deptIdStr = null;
//        List<String> deptList = new ArrayList<>();
//        List<String> deptResultList = new ArrayList<>();
//        if (VideoSystemEnums.IsOrNotType.NOT.getIndex().equals(isTenantAdmin)) {//老方法获取deptId
//            boolean flag = true;
////            HttpServletRequest request = RequestUtils.getRequest();
////            String requestURI = request.getRequestURI();
//            SysDept dept = sysUser.getDept();
//            String parentId = dept.getParentId();
//            String deptId = sysUser.getDeptId();
//            StringBuffer deptIdStrBuffer = new StringBuffer();
//            deptIdStrBuffer.append(" and ( ");
//            for (SysRole role : sysUser.getRoles()) {
//                //根据请求资源url陪陪每个角色对应的权限
////                Set<String> permissions = role.getPermissions();
////                boolean matched = TenantForDataScopeAspect.matchUrl(permissions, requestURI);
////                //如果未匹配上,则继续查看下个角色权限
////                if (!matched) {
////                    continue;
////                }
//                String roleDataScope = StrUtil.isNotBlank(role.getDataScope()) ? role.getDataScope() : TenantConstants.DATA_SCOPE_DEPT;
//                switch (roleDataScope) {
//                    case TenantConstants.DATA_SCOPE_ALL:
//                        flag = false;
//                        break;
//                    case TenantConstants.DATA_SCOPE_CUSTOM:
//                        String roleDeptInParameter = getRoleDeptInParameter(role.getRoleId());
//                        List<Long> roleDeptList = RedisUtil.getList(CacheConstants.ROLE_SCOPE_LIST_PREFIX + role.getRoleId(), Long.class, RedisDBType.PRIMARY);
//                        for (Long x : roleDeptList) {
//                            deptList.add(String.valueOf(x));
//                        }
//                        //不为空时,设置该用户对应角色的部门及以下部门数据权限
//                        if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(roleDeptInParameter)) {
//                            deptIdStrBuffer.append(com.chinatower.platform.common.utils.StringUtils.format(" or {} IN {} ", TenantConstants.ORG_STR, roleDeptInParameter));
//                        }
//                        break;
//                    case TenantConstants.DATA_SCOPE_DEPT:
//                        deptList.add(deptId);
//                        deptIdStrBuffer.append(com.chinatower.platform.common.utils.StringUtils.format(" or {} = '{}' ", TenantConstants.ORG_STR, deptId));
//                        break;
//                    case TenantConstants.DATA_SCOPE_DEPT_AND_CHILD:
//                        //如果不是根节点,加deptId数据过滤
//                        if (!DeptConstants.ROOT_DEPT.equals(parentId)) {
//                            String deptInParameter = getTenantDeptInParameter();
//                            //不为空时,设置该用户对应的部门及以下部门数据权限
//                            if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(deptInParameter)) {
//                                deptList.addAll(SecurityUtils.getTenantChildDeptIdList());
//                                deptIdStrBuffer.append(com.chinatower.platform.common.utils.StringUtils.format(" or {} IN {}", TenantConstants.ORG_STR, deptInParameter));
//                            }
//                            //为空时,设置该用户对应的部门数据权限
//                            if (com.chinatower.platform.common.utils.StringUtils.isEmpty(deptInParameter)) {
//                                deptList.add(deptId);
//                                deptIdStrBuffer.append(com.chinatower.platform.common.utils.StringUtils.format(" or {} = '{}' ", TenantConstants.ORG_STR, deptId));
//                            }
//                        }
//                        break;
//                    case TenantConstants.DATA_SCOPE_SELF:
//                        break;
//                    default:
//                }
//            }
//            deptIdStrBuffer.append(" )");
//            if (flag && !" and (  )".equals(deptIdStrBuffer.toString())) {
//                deptIdStr = deptIdStrBuffer.toString().replaceFirst("or", com.chinatower.platform.constants.Constants.BLANK_STR);
//            }
//            List<DeviceTypeAndCodeVO> deviceCodes = formatListMethod(authBaseMapper.getDevideCodes(sysUser.getAppVerCode(), sysUser.getTenantId(), deptIdStr), "1");
//            List<DeviceTypeAndCodeVO> sensorCodes = formatListMethod(authBaseMapper.getSensorCodes(sysUser.getAppVerCode(), sysUser.getTenantId(), deptIdStr), "8");
//            List<DeviceTypeAndCodeVO> satelliteCodes = formatListMethod(authBaseMapper.getSatelliteCodes(sysUser.getAppVerCode(), sysUser.getTenantId(), deptIdStr), "9");
//            String personDeptIdStr = null;
//            if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(deptIdStr)) {
//                personDeptIdStr = deptIdStr.replace(TenantConstants.ORG_STR, "a.DEPT_ID");
//            }
//            List<String> personCodes = authBaseMapper.getPersonCodes(sysUser.getAppVerCode(), sysUser.getTenantId(), personDeptIdStr);
//            List<DeviceTypeAndCodeVO> deviceList = new ArrayList<>();
//            deviceList.addAll(deviceCodes);
//            deviceList.addAll(sensorCodes);
//            userAuthData.setAreaList(personCodes);
//            userAuthData.setDeviceList(deviceList);
//            userAuthData.setSatelliteList(satelliteCodes);
//        }
//        deptResultList = deptList.stream().distinct().collect(Collectors.toList());
//        userAuthData.setTenantId(tenantId);
//        userAuthData.setUserId(userId);
//        userAuthData.setIsTenantAdmin(isTenantAdmin);
//        userAuthData.setDeptList(deptResultList);
//        return userAuthData;
//    }

    private List<DeviceTypeAndCodeVO> formatListMethod(List<String> list, String type) {
        List<DeviceTypeAndCodeVO> resultList = new ArrayList<>();
        list.stream().forEach(item -> resultList.add(new DeviceTypeAndCodeVO(item, type)));
        return resultList;
    }

    private List<AreaDataVO> getDeptStreetScop(AuthObjDataVO vo) {
        List<AreaDataVO> list = new ArrayList<>();
        filterAddStreet(list, getDeptStreet(vo.getCameras()));
        filterAddStreet(list, getDeptStreet(vo.getRadars()));
        filterAddStreet(list, getDeptStreet(vo.getSensors()));
        filterAddStreet(list, getDeptStreet(vo.getSatellites()));
        //add by liuxt 组织管理优化需求 start
        filterAddStreet(list, getDeptStreetNew(vo.getUavs()));
        filterAddStreet(list, getDeptStreetNew(vo.getHorns()));
        //add by liuxt 组织管理优化需求 end
        filterAddStreet(list, getDeptStreetNew(vo.getLERs()));
        return list;
    }

    private void filterAddStreet(List<AreaDataVO> list, List<AreaDataVO> add) {
        if (add != null && add.size() > 0) {
            list.addAll(add);
        }
    }

    private List<AreaDataVO> getDeptStreet(List<DeviceVO> deviceScop) {
        if (deviceScop == null || deviceScop.size() == 0) {
            return null;
        }
        List<AreaDataVO> list = new ArrayList<>();

        deviceScop.stream().forEach(d -> {

            if ((d == null) || (com.chinatower.platform.common.utils.StringUtils.isBlank(d.getCityId()) && com.chinatower.platform.common.utils.StringUtils.isBlank(d.getProvinceId()) && com.chinatower.platform.common.utils.StringUtils.isBlank(d.getCountyId()) && com.chinatower.platform.common.utils.StringUtils.isBlank(d.getTownCode()))) {
                return;
            }
            AreaDataVO vo = new AreaDataVO();
            vo.setCityCode(d.getCityId());
            vo.setProvinceCode(d.getProvinceId());
            vo.setCountyCode(d.getCountyId());
            vo.setStreetCode(d.getTownCode());
            list.add(vo);
        });
        return list;
    }

    private List<AreaDataVO> getDeptStreetNew(List<com.chinatower.system.api.model.SysDeptDeviceVO> deviceScop) {
        if (deviceScop == null || deviceScop.size() == 0) {
            return null;
        }
        List<AreaDataVO> list = new ArrayList<>();

        deviceScop.stream().forEach(d -> {

            if ((d == null) || (com.chinatower.platform.common.utils.StringUtils.isBlank(d.getCityId()) && com.chinatower.platform.common.utils.StringUtils.isBlank(d.getProvinceId()) && com.chinatower.platform.common.utils.StringUtils.isBlank(d.getCountyId()) && com.chinatower.platform.common.utils.StringUtils.isBlank(d.getTownCode()))) {
                return;
            }
            AreaDataVO vo = new AreaDataVO();
            vo.setCityCode(d.getCityId());
            vo.setProvinceCode(d.getProvinceId());
            vo.setCountyCode(d.getCountyId());
            vo.setStreetCode(d.getTownCode());
            list.add(vo);
        });
        return list;
    }


    /**
     * 给历史组织的大数据告警类型增加子级告警类型
     */
    @Override
    public void addChildDeptAlarmType() {
        long start = System.currentTimeMillis();
        Map<String, Set<String>> alarmTypeMap = getAlarmTypeMap();
        if (alarmTypeMap.isEmpty()) {
            log.info("告警类型映射关系为空，无需处理");
            return;
        }
        // 1. 处理大屏配置表数据
        processScreenConfigData(alarmTypeMap);
        // 2. 原始逻辑：处理组织告警类型关系
        processOrganAlarmTypeData(alarmTypeMap);
        log.info(start + "addChildDeptAlarmType整体耗时=" + (System.currentTimeMillis() - start));
    }


    /**
     * 处理大屏配置表数据
     */
    private void processScreenConfigData(Map<String, Set<String>> alarmTypeMap) {
        long screenStart = System.currentTimeMillis();
        try {
            // 1.1 查询需要处理的大屏配置数据（直接查询特定的父告警类型）
            Set<String> alarmTypeCodes = alarmTypeMap.keySet();
            List<ScreenConfigVO> parentConfigs = deptBusinessMapper.selectScreenConfigForProcess(alarmTypeCodes);
            if (CollectionUtils.isEmpty(parentConfigs)) {
                log.info("没有需要处理的父告警类型大屏配置数据");
                return;
            }
            log.info("查询到父告警类型大屏配置数据数量：{}", parentConfigs.size());
            // 1.2 收集需要插入的子告警类型配置
            List<ScreenConfigVO> childConfigsToInsert = new ArrayList<>();
            Set<Long> processedConfigIds = new HashSet<>();
            for (ScreenConfigVO parentConfig : parentConfigs) {
                String parentTypeCode = parentConfig.getDataCode();
                Set<String> childTypeCodes = alarmTypeMap.get(parentTypeCode);
                // 为每个子告警类型创建配置记录
                for (String childTypeCode : childTypeCodes) {
                    ScreenConfigVO childConfig = new ScreenConfigVO();
                    BeanUtils.copyProperties(parentConfig, childConfig);
                    childConfig.setDataCode(childTypeCode);
                    childConfigsToInsert.add(childConfig);
                }
                processedConfigIds.add(parentConfig.getScreenDataConfigId());
            }
            // 1.3 批量插入子告警类型配置
            if (!CollectionUtils.isEmpty(childConfigsToInsert)) {
                log.info("准备插入子告警类型大屏配置数据数量：{}", childConfigsToInsert.size());
                // 去重：避免重复插入
                Map<String, ScreenConfigVO> uniqueMap = new HashMap<>();
                for (ScreenConfigVO config : childConfigsToInsert) {
                    String key = config.getScreenDataConfigId() + "_" + config.getDataCode() + "_" + config.getDataSource();
                    uniqueMap.putIfAbsent(key, config);
                }
                List<ScreenConfigVO> uniqueConfigs = new ArrayList<>(uniqueMap.values());
                log.info("去重后子告警类型大屏配置数据数量：{}", uniqueConfigs.size());
                // 先删除已存在的记录
                if (!CollectionUtils.isEmpty(uniqueConfigs)) {
                    log.info("开始删除已存在的配置记录...");
                    List<List<ScreenConfigVO>> deletePartitionList = Lists.partition(uniqueConfigs, Constants.ONE_THOUSAND);
                    for (List<ScreenConfigVO> deleteBatch : deletePartitionList) {
                        deptBusinessMapper.deleteExistingScreenConfig(deleteBatch);
                    }
                    log.info("删除已存在的配置记录完成");
                }
                // 分批插入
                List<List<ScreenConfigVO>> partitionList = Lists.partition(uniqueConfigs, Constants.ONE_THOUSAND);
                for (List<ScreenConfigVO> batch : partitionList) {
                    deptBusinessMapper.insertScreenConfigBatch(batch);
                }
                log.info("插入子告警类型大屏配置数据完成");
            }
        } catch (Exception e) {
            log.error("处理大屏配置数据失败", e);
            // 这里可以根据业务需求决定是否抛出异常
        }
        log.info("处理大屏配置数据耗时：{}ms", (System.currentTimeMillis() - screenStart));
    }


    /**
     * 处理组织告警类型关系（原有逻辑）
     */
    private void processOrganAlarmTypeData(Map<String, Set<String>> alarmTypeMap) {
        long organStart = System.currentTimeMillis();
        try {
            // 直接使用 keySet() 返回的 Set 查询配置了该告警类型编码的组织有哪些
            Set<String> typeValueSet = alarmTypeMap.keySet();
            List<OrganAlarmTypeVO> deptList = deptBusinessMapper.getOrganAlarmTypeList(typeValueSet);
            List<OrganAlarmTypeVO> finalList = new ArrayList<>();
            for (OrganAlarmTypeVO parentAlarmType : deptList) {
                String parentTypeValue = parentAlarmType.getTypeValue();
                // 检查是否是特定的父告警类型
                if (alarmTypeMap.containsKey(parentTypeValue)) {
                    // 获取该父告警类型对应的所有子告警类型
                    Set<String> childTypeValues = alarmTypeMap.get(parentTypeValue);
                    // 为每个子告警类型创建一条新记录
                    for (String childTypeValue : childTypeValues) {
                        OrganAlarmTypeVO childAlarmType = new OrganAlarmTypeVO();
                        // 复制所有属性（除了typeValue）
                        childAlarmType.setTenantId(parentAlarmType.getTenantId());
                        childAlarmType.setOrgId(parentAlarmType.getOrgId());
                        childAlarmType.setDeptId(parentAlarmType.getDeptId());
                        childAlarmType.setCreator(parentAlarmType.getCreator());
                        childAlarmType.setWarningSource(parentAlarmType.getWarningSource());
                        childAlarmType.setIndustryCode(parentAlarmType.getIndustryCode());
                        childAlarmType.setAppVerCode(parentAlarmType.getAppVerCode());
                        // 设置子告警类型
                        childAlarmType.setTypeValue(childTypeValue);
                        finalList.add(childAlarmType);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(finalList)) {
                try {
                    log.info(organStart + "批量新增前整体耗时=" + (System.currentTimeMillis() - organStart));
                    List<List<OrganAlarmTypeVO>> partitionList = Lists.partition(finalList, Constants.ONE_THOUSAND);

                    // 先删除已存在的记录
                    partitionList.forEach(item ->
                            deptBusinessMapper.deleteExistingAlarmTypes(item)
                    );

                    // 批量插入新记录
                    partitionList.forEach(item ->
                            deptMapper.insertOrganAlarmTypeBatch(item)
                    );

                } catch (Exception e) {
                    log.error(Constants.BATCH_INSERT_FAIL, e);
                }
            }
            log.info("处理组织告警类型关系耗时：{}ms", (System.currentTimeMillis() - organStart));
        } catch (Exception e) {
            log.error("处理组织告警类型关系失败", e);
            throw e;
        }
    }
}
