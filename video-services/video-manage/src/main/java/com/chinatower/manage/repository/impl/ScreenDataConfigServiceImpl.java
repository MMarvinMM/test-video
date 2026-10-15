package com.chinatower.manage.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.entity.Result;

import com.chinatower.manage.dto.ScreenDataConfigCountVO;
import com.chinatower.manage.dto.ScreenDataConfigParticipationVO;
import com.chinatower.manage.dto.ScreenDataConfigVO;
import com.chinatower.manage.mapper.ScreenDataConfigMapper;
import com.chinatower.manage.repository.IScreenDataConfigService;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: 大屏数据配置实现类
 * @Author: wangyt
 * @CreateTime: 2025-01-06  09:54
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Service
public class ScreenDataConfigServiceImpl implements IScreenDataConfigService {

    @Resource
    private SystemFeignClient systemFeignClient;

    @Autowired
    private ScreenDataConfigMapper screenDataConfigMapper;

    private static final String BATCH_INSERT_FAIL = "批量插入失败";
    private static final String BATCH_DELETE_FAIL = "批量删除失败";
    private static final int INT_ZERO = 0;

    @Override
    public List<ScreenDataConfigCountVO> selectScreenDataConfigCount(ScreenDataConfigCountVO screenDataConfigCount) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        screenDataConfigCount.setTenantId(user.getTenantId());
        screenDataConfigCount.setIndustryCode(user.getIndustryCode());
        screenDataConfigCount.setAppVerCode(user.getAppVerCode());
        if(ObjectUtils.isEmpty(screenDataConfigCount.getPageNum())){
            screenDataConfigCount.setPageNum(1);
        }
        if(ObjectUtils.isEmpty(screenDataConfigCount.getPageSize())){
            screenDataConfigCount.setPageSize(20);
        }
        PageHelper.startPage(screenDataConfigCount.getPageNum(), screenDataConfigCount.getPageSize());
        // 只查询行业大屏
        List<ScreenDataConfigCountVO> threePartyScreenList = screenDataConfigMapper.selectAllScreenData(screenDataConfigCount);

        // 返回前端数据
        return threePartyScreenList;
    }

    @Override
    public Result selectConfigById(ScreenDataConfigVO screenDataConfig) {
        // 先根据配置id查询各个数据类型的编码
        Long screenDataConfigId = screenDataConfig.getScreenDataConfigId();
        String screenId = screenDataConfig.getScreenId();
        String screenType = screenDataConfig.getScreenType();
        if(ObjectUtils.isEmpty(screenDataConfigId) && (StringUtils.isEmpty(screenId) || StringUtils.isEmpty(screenType))){
            return Result.fail("大屏配置id参数不能为空");
        }

        // 查询配置详情
        if(!ObjectUtils.isEmpty(screenDataConfigId)){
            screenDataConfig = screenDataConfigMapper.selectScreenDataConfigCountById(screenDataConfigId);
        }else{
            // 根据当前登录用户获取
            SysUser user = SecurityUtils.getLoginUser().getUser();
            screenDataConfig = screenDataConfigMapper.selectScreenDataConfigCountByIdAndType(screenId, screenType, user.getTenantId(), user.getIndustryCode(), user.getAppVerCode());
            if(ObjectUtils.isEmpty(screenDataConfig)){
                // 如果没有查到，则返回一个默认对象
                ScreenDataConfigVO returnData = new ScreenDataConfigVO();
                returnData.setTenantId(user.getTenantId());
                returnData.setIndustryCode(user.getIndustryCode());
                returnData.setAppVerCode(user.getAppVerCode());
                returnData.setIsCustomize(Constants.ZERO);
                returnData.setScreenId(screenId);
                returnData.setScreenType(screenType);
                return Result.data(returnData);
            }
            screenDataConfigId = screenDataConfig.getId();
        }

        List<ScreenDataConfigParticipationVO> screenDataConfigList = screenDataConfigMapper.selectScreenDataConfigByConfigId(screenDataConfigId);
        Map<String, List<ScreenDataConfigParticipationVO>> screenDataConfigListMap = screenDataConfigList.stream().collect(Collectors.groupingBy(ScreenDataConfigParticipationVO::getDataType));
        // 设备的要查询省市区的编码
        List<ScreenDataConfigParticipationVO> cameraCodeList = screenDataConfigListMap.get(Constants.ZERO);
        if(!CollectionUtils.isEmpty(cameraCodeList)){
            List<String> cameraCodeListNew = cameraCodeList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setCameraCodeList(cameraCodeListNew);
        }
        List<ScreenDataConfigParticipationVO> iotCodeList = screenDataConfigListMap.get(Constants.ONE);
        if(!CollectionUtils.isEmpty(iotCodeList)){
            List<String> iotCodeListNew = iotCodeList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setIotCodeList(iotCodeListNew);
        }
        List<ScreenDataConfigParticipationVO> radarCodeList = screenDataConfigListMap.get(Constants.TWO);
        if(!CollectionUtils.isEmpty(radarCodeList)){
            List<String> radarCodeListNew = radarCodeList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setRadarCodeList(radarCodeListNew);
        }
        List<ScreenDataConfigParticipationVO> speakerCodeList = screenDataConfigListMap.get(Constants.THREE);
        if(!CollectionUtils.isEmpty(speakerCodeList)){
            List<String> speakerCodeListNew = speakerCodeList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setSpeakerCodeList(speakerCodeListNew);
        }
        List<ScreenDataConfigParticipationVO> uavCodeList = screenDataConfigListMap.get(Constants.FOUR);
        if(!CollectionUtils.isEmpty(uavCodeList)){
            List<String> uavCodeListNew = uavCodeList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setUavCodeList(uavCodeListNew);
        }
        List<ScreenDataConfigParticipationVO> recorderCodeList = screenDataConfigListMap.get(Constants.FIVE);
        if(!CollectionUtils.isEmpty(recorderCodeList)){
            List<String> recorderCodeListNew = recorderCodeList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setRecorderCodeList(recorderCodeListNew);
        }
        List<ScreenDataConfigParticipationVO> gridCodeList = screenDataConfigListMap.get(Constants.SIX);
        if(!CollectionUtils.isEmpty(gridCodeList)){
            List<String> gridCodeListNew = gridCodeList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setGridCodeList(gridCodeListNew);
        }
        List<ScreenDataConfigParticipationVO> gridKeeperList = screenDataConfigListMap.get(Constants.SEVEN);
        if(!CollectionUtils.isEmpty(gridKeeperList)){
            // 网格员可能有多个父级，需要把父级id传给前端
            screenDataConfig.setGridKeeperList(gridKeeperList);
        }
        List<ScreenDataConfigParticipationVO> resourcesList = screenDataConfigListMap.get(Constants.EIGHT);
        if(!CollectionUtils.isEmpty(resourcesList)){
            List<String> resourcesListNew = resourcesList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            screenDataConfig.setResourcesCodeList(resourcesListNew);
        }
        List<ScreenDataConfigParticipationVO> alarmTypeList = screenDataConfigListMap.get(Constants.NINE);
        screenDataConfig.setAlarmTypeList(alarmTypeList);

        // 返回前端数据
        return Result.data(screenDataConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addConfig(List<ScreenDataConfigVO> screenDataConfigList) {
        log.info("新增大屏配置参数:{}", JSONObject.toJSONString(screenDataConfigList));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = "";
        if(!ObjectUtils.isEmpty(loginUser)){
            userName = loginUser.getUser().getUserName();
        }
        // 先查询此客户此行业开启大屏配置的大屏
        List<ScreenDataConfigVO> tietaList = new ArrayList<>();
        List<ScreenDataConfigVO> tenantList = new ArrayList<>();
        List<ScreenDataConfigCountVO> openIsCustomizeScreenDataConfigList = new ArrayList<>();
        // 如果客户id和行业都为null 则为铁塔视角新增的告警类型 需要给所有客户都加上
        screenDataConfigList.forEach(e -> {
            if(StringUtils.isEmpty(e.getTenantId()) && StringUtils.isEmpty(e.getIndustryCode())){
                tietaList.add(e);
            }else{
                tenantList.add(e);
            }
        });

        if(!CollectionUtils.isEmpty(tietaList)){
            List<ScreenDataConfigCountVO> openIsCustomizeScreenDataConfigTietaList = screenDataConfigMapper.selectOpenIsCustomizeScreenDataConfigTieta(tietaList);
            openIsCustomizeScreenDataConfigList = openIsCustomizeScreenDataConfigTietaList;
        }else if(!CollectionUtils.isEmpty(tenantList)){
            List<ScreenDataConfigCountVO> openIsCustomizeScreenDataConfigTenantList = screenDataConfigMapper.selectOpenIsCustomizeScreenDataConfig(tenantList);
            openIsCustomizeScreenDataConfigList = openIsCustomizeScreenDataConfigTenantList;
        }

        if(!CollectionUtils.isEmpty(openIsCustomizeScreenDataConfigList)){
            // 每个开启大屏配置的大屏都要增加新增的数据
            List<ScreenDataConfigVO> insertList = new ArrayList<>();
            for (ScreenDataConfigCountVO vo:openIsCustomizeScreenDataConfigList) {
                for (ScreenDataConfigVO screenDataConfig:screenDataConfigList) {
                    if((StringUtils.isEmpty(screenDataConfig.getTenantId()) && StringUtils.isEmpty(screenDataConfig.getIndustryCode()))
                            || (vo.getTenantId().equals(screenDataConfig.getTenantId()) && vo.getIndustryCode().equals(screenDataConfig.getIndustryCode()))){
                        List<String> cameraCodeList = screenDataConfig.getCameraCodeList();
                        List<String> iotCodeList = screenDataConfig.getIotCodeList();
                        List<String> radarCodeList = screenDataConfig.getRadarCodeList();
                        List<String> speakerCodeList = screenDataConfig.getSpeakerCodeList();
                        List<String> uavCodeList = screenDataConfig.getUavCodeList();
                        List<String> recorderCodeList = screenDataConfig.getRecorderCodeList();
                        List<String> gridCodeList = screenDataConfig.getGridCodeList();
                        List<ScreenDataConfigParticipationVO> gridKeeperList = screenDataConfig.getGridKeeperList();
                        List<String> resourcesCodeList = screenDataConfig.getResourcesCodeList();
                        List<ScreenDataConfigParticipationVO> alarmTypeList = screenDataConfig.getAlarmTypeList();
                        if(!CollectionUtils.isEmpty(cameraCodeList)){
                            vo.setCameraNum(vo.getCameraNum() + cameraCodeList.size());
                            organizationalParametersString(cameraCodeList, vo.getId(), Constants.ZERO, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(iotCodeList)){
                            vo.setIotNum(vo.getIotNum() + iotCodeList.size());
                            organizationalParametersString(iotCodeList, vo.getId(), Constants.ONE, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(radarCodeList)){
                            vo.setRadarNum(vo.getRadarNum() + radarCodeList.size());
                            organizationalParametersString(radarCodeList, vo.getId(), Constants.TWO, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(speakerCodeList)){
                            vo.setSpeakerNum(vo.getSpeakerNum() + speakerCodeList.size());
                            organizationalParametersString(speakerCodeList, vo.getId(), Constants.THREE, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(uavCodeList)){
                            vo.setUavNum(vo.getUavNum() + uavCodeList.size());
                            organizationalParametersString(uavCodeList, vo.getId(), Constants.FOUR, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(recorderCodeList)){
                            vo.setRecorderNum(vo.getRecorderNum() + recorderCodeList.size());
                            organizationalParametersString(recorderCodeList, vo.getId(), Constants.FIVE, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(gridCodeList)){
                            vo.setGridNum(vo.getGridNum() + gridCodeList.size());
                            organizationalParametersString(gridCodeList, vo.getId(), Constants.SIX, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(gridKeeperList)){
                            vo.setGridKeeperNum(vo.getGridKeeperNum() + gridKeeperList.size());
                            organizationalParametersObject(gridKeeperList, vo.getId(), Constants.SEVEN, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(resourcesCodeList)){
                            vo.setResourcesNum(vo.getResourcesNum() + resourcesCodeList.size());
                            organizationalParametersString(resourcesCodeList, vo.getId(), Constants.EIGHT, insertList, userName);
                        }
                        if(!CollectionUtils.isEmpty(alarmTypeList)){
                            // 获取特定值的映射关系
                            Map<String, Set<String>> alarmTypeMapping = systemFeignClient.getAlarmTypeMap().getData();
                            // 创建处理后的列表
                            List<ScreenDataConfigParticipationVO> processedAlarmTypeList = new ArrayList<>();
                            for (ScreenDataConfigParticipationVO alarmType : alarmTypeList) {
                                String code = alarmType.getCode();
                                // 检查是否是特定值
                                if (alarmTypeMapping.containsKey(code)) {
                                    // 如果是特定值，获取对应的值列表
                                    Set<String> mappedCodes = alarmTypeMapping.get(code);
                                    // 为每个映射值创建新的记录
                                    for (String mappedCode : mappedCodes) {
                                        ScreenDataConfigParticipationVO newAlarmType = new ScreenDataConfigParticipationVO();
                                        // 复制所有属性
                                        BeanUtils.copyProperties(alarmType, newAlarmType);
                                        // 设置映射后的 code
                                        newAlarmType.setCode(mappedCode);
                                        processedAlarmTypeList.add(newAlarmType);
                                    }
                                }
                                processedAlarmTypeList.add(alarmType);
                            }
                            vo.setAlarmTypeNum(vo.getAlarmTypeNum() + processedAlarmTypeList.size());
                            organizationalParametersObject(processedAlarmTypeList, vo.getId(), Constants.NINE, insertList, userName);
                        }
                    }
                }
            }

            batchInsert(insertList);
            // 更新数量
            screenDataConfigMapper.batchUpdateScreenDataConfigCount(openIsCustomizeScreenDataConfigList);
        }
        return Result.success("保存成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editConfig(ScreenDataConfigVO screenDataConfig) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 如果ScreenDataConfigId是空的 那就是新增操作
        Long screenDataConfigId = screenDataConfig.getScreenDataConfigId();
        // 新增时多加一个校验，防止多用户同时新增
        if(ObjectUtils.isEmpty(screenDataConfigId)){
            ScreenDataConfigVO screenDataConfigTemp = screenDataConfigMapper.selectScreenDataConfigCountByIdAndType(screenDataConfig.getScreenId(), screenDataConfig.getScreenType(), user.getTenantId(), user.getIndustryCode(), user.getAppVerCode());
            if(!ObjectUtils.isEmpty(screenDataConfigTemp)){
                screenDataConfigId = screenDataConfigTemp.getId();
            }
        }
        if(ObjectUtils.isEmpty(screenDataConfigId)){
            ScreenDataConfigCountVO screenDataConfigCount = new ScreenDataConfigCountVO();
            screenDataConfigCount.setScreenId(screenDataConfig.getScreenId());
            screenDataConfigCount.setScreenType(screenDataConfig.getScreenType());
            screenDataConfigCount.setIsCustomize(screenDataConfig.getIsCustomize());
            screenDataConfigCount.setCreateBy(user.getUserName());
            screenDataConfigCount.setCreateTime(new Date());
            screenDataConfigCount.setUpdateBy(user.getUserName());
            screenDataConfigCount.setUpdateTime(new Date());
            screenDataConfigCount.setDeleteFlag(Constants.ZERO);
            screenDataConfigCount.setIndustryCode(user.getIndustryCode());
            screenDataConfigCount.setTenantId(user.getTenantId());
            screenDataConfigCount.setAppVerCode(user.getAppVerCode());
            screenDataConfigMapper.insertScreenDataConfigCount(screenDataConfigCount);
            screenDataConfigId = screenDataConfigCount.getId();
        }else{
            // 先删除旧的数据
            screenDataConfigMapper.deleteByScreenDataConfigId(screenDataConfigId);
        }

        // 在根据前端传过来的数据新增
        ScreenDataConfigCountVO updateNum = new ScreenDataConfigCountVO();
        updateNum.setId(screenDataConfigId);
        updateNum.setIsCustomize(screenDataConfig.getIsCustomize());
        updateNum.setScreenType(screenDataConfig.getScreenType());
        updateNum.setUpdateBy(user.getUserName());
        Long finalScreenDataConfigId = screenDataConfigId;
        List<ScreenDataConfigVO> insertList = new ArrayList<>();
        // 0-摄像机
        List<String> cameraCodeList = screenDataConfig.getCameraCodeList();
        if(!CollectionUtils.isEmpty(cameraCodeList)){
            updateNum.setCameraNum(cameraCodeList.size());
            organizationalParametersString(cameraCodeList, finalScreenDataConfigId, Constants.ZERO, insertList, user.getUserName());
        }

        //1-物联设备
        List<String> iotCodeList = screenDataConfig.getIotCodeList();
        if(!CollectionUtils.isEmpty(iotCodeList)){
            updateNum.setIotNum(iotCodeList.size());
            organizationalParametersString(iotCodeList, finalScreenDataConfigId, Constants.ONE, insertList, user.getUserName());
        }

        //2-雷达
        List<String> radarCodeList = screenDataConfig.getRadarCodeList();
        if(!CollectionUtils.isEmpty(radarCodeList)){
            updateNum.setRadarNum(radarCodeList.size());
            organizationalParametersString(radarCodeList, finalScreenDataConfigId, Constants.TWO, insertList, user.getUserName());
        }

        //3-喇叭
        List<String> speakerCodeList = screenDataConfig.getSpeakerCodeList();
        if(!CollectionUtils.isEmpty(speakerCodeList)){
            updateNum.setSpeakerNum(speakerCodeList.size());
            organizationalParametersString(speakerCodeList, finalScreenDataConfigId, Constants.THREE, insertList, user.getUserName());
        }

        //4-无人机
        List<String> uavCodeList = screenDataConfig.getUavCodeList();
        if(!CollectionUtils.isEmpty(uavCodeList)){
            updateNum.setUavNum(uavCodeList.size());
            organizationalParametersString(uavCodeList, finalScreenDataConfigId, Constants.FOUR, insertList, user.getUserName());
        }

        //5-执法记录仪
        List<String> recorderCodeList = screenDataConfig.getRecorderCodeList();
        if(!CollectionUtils.isEmpty(recorderCodeList)){
            updateNum.setRecorderNum(recorderCodeList.size());
            organizationalParametersString(recorderCodeList, finalScreenDataConfigId, Constants.FIVE, insertList, user.getUserName());
        }

        //6-网格
        List<String> gridCodeList = screenDataConfig.getGridCodeList();
        if(!CollectionUtils.isEmpty(gridCodeList)){
            updateNum.setGridNum(gridCodeList.size());
            organizationalParametersString(gridCodeList, finalScreenDataConfigId, Constants.SIX, insertList, user.getUserName());
        }

        //7-网格员
        List<ScreenDataConfigParticipationVO> gridKeeperList = screenDataConfig.getGridKeeperList();
        if(!CollectionUtils.isEmpty(gridKeeperList)){
            updateNum.setGridKeeperNum(gridKeeperList.size());
            organizationalParametersObject(gridKeeperList, finalScreenDataConfigId, Constants.SEVEN, insertList, user.getUserName());
        }

        //8-资源
        List<String> resourcesCodeList = screenDataConfig.getResourcesCodeList();
        if(!CollectionUtils.isEmpty(resourcesCodeList)){
            updateNum.setResourcesNum(resourcesCodeList.size());
            organizationalParametersString(resourcesCodeList, finalScreenDataConfigId, Constants.EIGHT, insertList, user.getUserName());
        }

        //9-告警类型
        List<ScreenDataConfigParticipationVO> alarmTypeList = screenDataConfig.getAlarmTypeList();
        if(!CollectionUtils.isEmpty(alarmTypeList)){
            // 获取特定值的映射关系
            Map<String, Set<String>> alarmTypeMapping = systemFeignClient.getAlarmTypeMap().getData();
            // 创建处理后的列表
            List<ScreenDataConfigParticipationVO> processedAlarmTypeList = new ArrayList<>();
            for (ScreenDataConfigParticipationVO alarmType : alarmTypeList) {
                String code = alarmType.getCode();
                // 检查是否是特定值
                if (alarmTypeMapping.containsKey(code)) {
                    // 如果是特定值，获取对应的值列表
                    Set<String> mappedCodes = alarmTypeMapping.get(code);
                    // 为每个映射值创建新的记录
                    for (String mappedCode : mappedCodes) {
                        ScreenDataConfigParticipationVO newAlarmType = new ScreenDataConfigParticipationVO();
                        // 复制所有属性
                        BeanUtils.copyProperties(alarmType, newAlarmType);
                        // 设置映射后的 code
                        newAlarmType.setCode(mappedCode);
                        processedAlarmTypeList.add(newAlarmType);
                    }
                }
                processedAlarmTypeList.add(alarmType);
            }
            updateNum.setAlarmTypeNum(processedAlarmTypeList.size());
            organizationalParametersObject(processedAlarmTypeList, finalScreenDataConfigId, Constants.NINE, insertList, user.getUserName());
        }

        batchInsert(insertList);
        // 更新数量 和 是否自定义状态, 如果是未开启自定义，则数量全部设为0
        if(updateNum.getIsCustomize().equals(Constants.ZERO)){
            updateNum.setCameraNum(INT_ZERO);
            updateNum.setIotNum(INT_ZERO);
            updateNum.setRadarNum(INT_ZERO);
            updateNum.setSpeakerNum(INT_ZERO);
            updateNum.setUavNum(INT_ZERO);
            updateNum.setRecorderNum(INT_ZERO);
            updateNum.setGridNum(INT_ZERO);
            updateNum.setGridKeeperNum(INT_ZERO);
            updateNum.setResourcesNum(INT_ZERO);
            updateNum.setAlarmTypeNum(INT_ZERO);
        }
        screenDataConfigMapper.updateScreenDataConfigCount(updateNum);
        // 返回前端
        return Result.success("编辑成功！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editThirdPartyAlarmConfig(List<ScreenDataConfigVO> screenDataConfigList) {
        log.info("第三方告警大屏配置参数:{}", JSONObject.toJSONString(screenDataConfigList));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(!CollectionUtils.isEmpty(screenDataConfigList)){
            ScreenDataConfigVO addVO = new ScreenDataConfigVO();
            ScreenDataConfigVO deleteVO = new ScreenDataConfigVO();
            for (ScreenDataConfigVO vo:screenDataConfigList) {
                if(vo.getDeleteFlag().equals(Constants.ONE)){
                    deleteVO = vo;
                }else{
                    addVO = vo;
                }
            }
            List<ScreenDataConfigCountVO> openIsCustomizeScreenDataConfigTenantList = screenDataConfigMapper.selectOpenIsCustomizeScreenDataConfig(screenDataConfigList);
            List<ScreenDataConfigVO> insertList = new ArrayList<>();
            List<ScreenDataConfigVO> deleteList = new ArrayList<>();
            List<ScreenDataConfigParticipationVO> insertAlarmTypeList = addVO.getAlarmTypeList();
            List<ScreenDataConfigParticipationVO> deleteAlarmTypeList = deleteVO.getAlarmTypeList();
            if(!CollectionUtils.isEmpty(insertAlarmTypeList)){
                for (ScreenDataConfigCountVO countVO:openIsCustomizeScreenDataConfigTenantList) {
                    countVO.setAlarmTypeNum(countVO.getAlarmTypeNum() + insertAlarmTypeList.size());
                    for (ScreenDataConfigParticipationVO addTempVO:insertAlarmTypeList) {
                        ScreenDataConfigVO insertModel = new ScreenDataConfigVO();
                        insertModel.setScreenDataConfigId(countVO.getId());
                        insertModel.setDataType(Constants.NINE);
                        insertModel.setDataSource(Constants.ELEVEN);
                        insertModel.setDataCode(addTempVO.getCode());
                        insertModel.setCreateBy(user.getUserName());
                        insertModel.setUpdateBy(user.getUserName());
                        insertList.add(insertModel);
                    }
                }
            }
            if(!CollectionUtils.isEmpty(deleteAlarmTypeList)){
                for (ScreenDataConfigCountVO countVO:openIsCustomizeScreenDataConfigTenantList) {
                    countVO.setAlarmTypeNum(countVO.getAlarmTypeNum() - deleteAlarmTypeList.size());
                    for (ScreenDataConfigParticipationVO deleteTempVO:deleteAlarmTypeList) {
                        ScreenDataConfigVO deleteModel = new ScreenDataConfigVO();
                        deleteModel.setScreenDataConfigId(countVO.getId());
                        deleteModel.setDataType(Constants.NINE);
                        deleteModel.setDataSource(Constants.ELEVEN);
                        deleteModel.setDataCode(deleteTempVO.getCode());
                        deleteList.add(deleteModel);
                    }
                }
            }

            if(!CollectionUtils.isEmpty(deleteList)){
                try {
                    // 删除数据
                    List<List<ScreenDataConfigVO>> deleteVOList = Lists.partition(deleteList, 5000);
                    deleteVOList.forEach(item ->
                            screenDataConfigMapper.batchDeleteScreenDataConfig(item)
                    );
                }catch (Exception e){
                    log.error(BATCH_DELETE_FAIL,e);
                }
            }
            batchInsert(insertList);
            // 更新告警类型数量
            screenDataConfigMapper.batchUpdateScreenDataConfigAlarmCount(openIsCustomizeScreenDataConfigTenantList);
        }

        return Result.success("请求成功！");
    }

    public void organizationalParametersString(List<String> codeList, Long configId, String dataType, List<ScreenDataConfigVO> insertList, String userName){
        codeList.stream().forEach(e ->{
            ScreenDataConfigVO insertModel = new ScreenDataConfigVO();
            insertModel.setScreenDataConfigId(configId);
            insertModel.setDataType(dataType);
            insertModel.setDataCode(e);
            insertModel.setCreateBy(userName);
            insertModel.setUpdateBy(userName);
            insertList.add(insertModel);
        });
    }

    public void organizationalParametersObject(List<ScreenDataConfigParticipationVO> objList, Long configId, String dataType, List<ScreenDataConfigVO> insertList, String userName){
        objList.stream().forEach(e ->{
            ScreenDataConfigVO insertModel = new ScreenDataConfigVO();
            insertModel.setScreenDataConfigId(configId);
            insertModel.setDataType(dataType);
            insertModel.setDataSource(e.getSource());
            insertModel.setDataCode(e.getCode());
            insertModel.setCreateBy(userName);
            insertModel.setUpdateBy(userName);
            insertList.add(insertModel);
        });
    }

    public void batchInsert(List<ScreenDataConfigVO> insertList){
        if(!CollectionUtils.isEmpty(insertList)){
            try {
                // 新增数据
                List<List<ScreenDataConfigVO>> insertVOList = Lists.partition(insertList, 5000);
                insertVOList.forEach(item ->
                        screenDataConfigMapper.batchInsertScreenDataConfig(item)
                );
            }catch (Exception e){
                log.error(BATCH_INSERT_FAIL,e);
            }
        }
    }
}
