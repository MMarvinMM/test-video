package com.chinatower.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.CommonService;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.dto.AlgorithmGiveConfigDO;
import com.chinatower.device.dto.AlgorithmGiveMergeDO;
import com.chinatower.device.dto.GiveMergeReq;
import com.chinatower.device.mapper.AlgorithmGiveConfigMapper;
import com.chinatower.device.mapper.AlgorithmGiveMergeMapper;
import com.chinatower.device.service.IAlgorithmGiveConfigService;
import com.chinatower.device.service.IAlgorithmGiveMergeService;
import com.chinatower.device.utils.ApiInfoUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.VideoConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

@Service
@Slf4j
public class AlgorithmGiveConfigService extends ServiceImpl<AlgorithmGiveConfigMapper, AlgorithmGiveConfigDO> implements IAlgorithmGiveConfigService {

    @Resource
    private AlgorithmGiveConfigMapper algorithmGiveConfigMapper;

    @Resource
    private IAlgorithmGiveMergeService algorithmGiveMergeService;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private AlgorithmGiveMergeMapper algorithmGiveMergeMapper;

    @Resource
    ApiInfoUtils apiInfoUtils;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    NetConnectUtils connectUtils;

    @Resource
    ICasCadeInfoService iCasCadeInfoService;

    @Resource
    CommonService commonService;

    public static final String IS_WU = "无";

    public static final String SQUARE_LEFT = "{";
    public static final String SQUARE_RIGHT = "}";

    public static final String BRACKET_LEFT = "(";
    public static final String BRACKET_RIGHT = ")";

    public static final String AND = " and ";

    public static final String OR = " or ";

    public static final String SPACE = " ";

    public static final String ALARM_TYPE_WATER = "1818128";

    public static final String ALARM_TYPE_WATER_LEVEL = "1818127";

    public static final String LINE = "-";


    @Override
    public Result<AlgorithmGiveConfigSaveVO> queryById(int id) {
        // SysUser user = SecurityUtils.getLoginUser().getUser();
        AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
        algorithmGiveConfigDO.setId(id);
        List<AlgorithmGiveConfigSaveVO> list = algorithmGiveConfigMapper.queryById(algorithmGiveConfigDO);
        log.info("+++++++++:{}", JSON.toJSONString(list));
        if(CollectionUtils.isEmpty(list)){
            return Result.fail("未查询到前端算法配置！");
        }
        //优化for循环查询
        BlackWhiteReq blackWhiteReq = new BlackWhiteReq();
        blackWhiteReq.setId(id);
        blackWhiteReq.setTenantId(list.get(0).getTenantId());
        blackWhiteReq.setIndustryCode(list.get(0).getIndustryCode());
        List<BlackWhiteVO> giveLicenseRelList = algorithmGiveConfigMapper.selectGiveLicenseRel(blackWhiteReq);
        for (AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO : list) {
            if(!StringUtils.isEmpty(giveLicenseRelList)){
                Map<Integer, List<BlackWhiteVO>> map = giveLicenseRelList.stream().collect(Collectors.groupingBy(item->item.getBlackOrWhite()));
                Map<String, List<BlackWhiteVO>> resultMap = new HashMap<>();
                for (Map.Entry<Integer, List<BlackWhiteVO>> integerListEntry : map.entrySet()) {
                    resultMap.put(integerListEntry.getKey() == Constants.ONE_NUM ? Constants.BLACK : Constants.WHITE , integerListEntry.getValue());
                }
                algorithmGiveConfigSaveVO.setBlackWhiteList(resultMap);
            }
        }
        return Result.data(list.get(0));
    }

    /**
     * 批量查询前端算法
     * @param algorithmGiveConfigDO
     * @return
     */
    private List<AlgorithmGiveConfigSaveVO> queryByIds(AlgorithmGiveConfigDO algorithmGiveConfigDO) {
        List<AlgorithmGiveConfigSaveVO> list = algorithmGiveConfigMapper.queryById(algorithmGiveConfigDO);
        log.info("+++++++++:{}", JSON.toJSONString(list));
        if(!CollectionUtils.isEmpty(list)){
            //优化for循环查询
            BlackWhiteReq blackWhiteReq = new BlackWhiteReq();
            blackWhiteReq.setIds(algorithmGiveConfigDO.getIds());
            blackWhiteReq.setTenantId(list.get(0).getTenantId());
            blackWhiteReq.setIndustryCode(list.get(0).getIndustryCode());
            List<BlackWhiteVO> giveLicenseRelList = algorithmGiveConfigMapper.selectGiveLicenseRel(blackWhiteReq);
            Map<Integer, List<BlackWhiteVO>> idMap = giveLicenseRelList.stream().collect(Collectors.groupingBy(BlackWhiteVO::getConfigId));
            for (AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO : list) {
                if(!StringUtils.isEmpty(giveLicenseRelList)){
                    List<BlackWhiteVO> blackWhiteVOS = idMap.get(algorithmGiveConfigSaveVO.getId());
                    Map<Integer, List<BlackWhiteVO>> map = blackWhiteVOS.stream().collect(Collectors.groupingBy(BlackWhiteVO::getBlackOrWhite));
                    Map<String, List<BlackWhiteVO>> resultMap = new HashMap<>();
                    for (Map.Entry<Integer, List<BlackWhiteVO>> integerListEntry : map.entrySet()) {
                        resultMap.put(integerListEntry.getKey() == Constants.ONE_NUM ? Constants.BLACK : Constants.WHITE , integerListEntry.getValue());
                    }
                    algorithmGiveConfigSaveVO.setBlackWhiteList(resultMap);
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    public Result<Integer> add(AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO) {
        log.info("前端算法新增请求报文" + JSONObject.toJSONString(algorithmGiveConfigSaveVO));
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();

        AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
        BeanUtils.copyProperties(algorithmGiveConfigSaveVO,algorithmGiveConfigDO);
        algorithmGiveConfigDO.setTenantId(user.getTenantId());
        algorithmGiveConfigDO.setTenantName(user.getTenantInfo().getTenantName());
        algorithmGiveConfigDO.setCreator(SecurityUtils.getOperator());
        algorithmGiveConfigDO.setIndustryCode(user.getIndustryCode());
        algorithmGiveConfigDO.setAppVerCode(user.getAppVerCode());
        algorithmGiveConfigDO.setCreateTime(new Date());
        if(!ObjectUtils.isEmpty(algorithmGiveConfigSaveVO.getTriggerRule())){
            algorithmGiveConfigDO.setTriggerRule(JSON.toJSONString(algorithmGiveConfigSaveVO.getTriggerRule()));
        }
        //根据客户编码、设备编码查询有效订单号
        String orderCode = algorithmGiveConfigMapper.queryOrderCode(algorithmGiveConfigDO);
        if(StringUtils.isEmpty(orderCode)){
            return Result.fail("未查询到有效订单！");
        }else{
            algorithmGiveConfigDO.setOrderCode(orderCode);
        }

        this.save(algorithmGiveConfigDO);

        Map<String, List<BlackWhiteVO>> blackWhiteList = algorithmGiveConfigSaveVO.getBlackWhiteList();
        if(!StringUtils.isEmpty(blackWhiteList)){
            List<BlackWhiteVO> list = new ArrayList<>();
            for (Map.Entry<String, List<BlackWhiteVO>> es : blackWhiteList.entrySet()) {
                for (BlackWhiteVO blackWhiteVO : es.getValue()) {
                    blackWhiteVO.setConfigId(algorithmGiveConfigDO.getId());
                    blackWhiteVO.setCreator(SecurityUtils.getOperator());
                    blackWhiteVO.setBlackOrWhite((Constants.BLACK).equals(es.getKey()) ? 1 : 2);
                }
                list.addAll(es.getValue());
            }
            algorithmGiveConfigMapper.insertGiveLicenseRel(list);
        }

        //异步整合前端告警逻辑
        List<DeviceChannelVO> deviceChannelList = new ArrayList<>();
        DeviceChannelVO deviceChannelVO = new DeviceChannelVO();
        deviceChannelVO.setDeviceCode(algorithmGiveConfigSaveVO.getDeviceCode());
        deviceChannelVO.setChannelCode(algorithmGiveConfigSaveVO.getChannelCode());
        deviceChannelList.add(deviceChannelVO);
        //前端算法整合
        this.algorithmGiveConfigMerge(deviceChannelList);

        return Result.data(algorithmGiveConfigDO.getId(),"前端算法新增成功！");
    }

    @Override
    @Transactional
    public Result<String> edit(AlgorithmGiveConfigUpdateVO algorithmGiveConfigUpdateVO) {
        log.info("前端算法编辑请求报文" + JSONObject.toJSONString(algorithmGiveConfigUpdateVO));
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();

        AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();

        algorithmGiveConfigDO.setId(algorithmGiveConfigUpdateVO.getId());
        algorithmGiveConfigDO.setSecondSwitch(algorithmGiveConfigUpdateVO.getSecondSwitch());
        if ((DeviceConstants.DELETE_FLAG_NORMAL).equals(algorithmGiveConfigDO.getSecondSwitch())) {
            algorithmGiveConfigDO.setAlgorithmCode(algorithmGiveConfigUpdateVO.getAlgorithmCode());
            algorithmGiveConfigDO.setAlgorithmName(algorithmGiveConfigUpdateVO.getAlgorithmName());
        }
        algorithmGiveConfigDO.setModifier(SecurityUtils.getOperator());
        algorithmGiveConfigDO.setUpdateTime(new Date());
        algorithmGiveConfigDO.setTriggerRule(JSON.toJSONString(algorithmGiveConfigUpdateVO.getTriggerRule()));

        AlgorithmGiveConfigDO algorithmGiveConfigDODB = this.getById(algorithmGiveConfigUpdateVO.getId());

        Map<String, List<BlackWhiteVO>> blackWhiteList = algorithmGiveConfigUpdateVO.getBlackWhiteList();
        algorithmGiveConfigMapper.deleteGiveLicenseRel(algorithmGiveConfigUpdateVO.getId());
        if(!StringUtils.isEmpty(blackWhiteList)){
            List<BlackWhiteVO> list = new ArrayList<>();
            for (Map.Entry<String, List<BlackWhiteVO>> es : blackWhiteList.entrySet()) {
                for (BlackWhiteVO blackWhiteVO : es.getValue()) {
                    blackWhiteVO.setConfigId(algorithmGiveConfigDO.getId());
                    blackWhiteVO.setCreator(SecurityUtils.getOperator());
                    blackWhiteVO.setBlackOrWhite((Constants.BLACK).equals(es.getKey()) ? 1 : 2);
                }
                list.addAll(es.getValue());
            }
            algorithmGiveConfigMapper.insertGiveLicenseRel(list);
        }

        algorithmGiveConfigMapper.editById(algorithmGiveConfigDO);

        //异步整合前端告警逻辑
        List<DeviceChannelVO> deviceChannelList = new ArrayList<>();
        DeviceChannelVO deviceChannelVO = new DeviceChannelVO();
        deviceChannelVO.setChannelCode(algorithmGiveConfigDODB.getChannelCode());
        deviceChannelVO.setDeviceCode(algorithmGiveConfigDODB.getDeviceCode());
        deviceChannelList.add(deviceChannelVO);
        //前端算法整合
        this.algorithmGiveConfigMerge(deviceChannelList);

        return Result.success("前端算法更新成功！");
    }
    @Override
    @Transactional
    public Result<String> status(AlgorithmGiveConfigStatusVO algorithmGiveConfigStatusVO){
        log.info("前端算法启停请求报文" + JSONObject.toJSONString(algorithmGiveConfigStatusVO));
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();

        AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
        BeanUtils.copyProperties(algorithmGiveConfigStatusVO,algorithmGiveConfigDO);
        algorithmGiveConfigDO.setModifier(user.getUserName());
        algorithmGiveConfigDO.setUpdateTime(new Date());
        this.updateById(algorithmGiveConfigDO);

        AlgorithmGiveConfigDO algorithmGiveConfigDODB = this.getById(algorithmGiveConfigStatusVO.getId());

        //异步整合前端告警逻辑
        List<DeviceChannelVO> deviceChannelList = new ArrayList<>();
        DeviceChannelVO deviceChannelVO = new DeviceChannelVO();
        deviceChannelVO.setChannelCode(algorithmGiveConfigDODB.getChannelCode());
        deviceChannelVO.setDeviceCode(algorithmGiveConfigDODB.getDeviceCode());
        deviceChannelList.add(deviceChannelVO);
        //前端算法整合
        this.algorithmGiveConfigMerge(deviceChannelList);

        return Result.success("前端算法" +
                (algorithmGiveConfigStatusVO.getStatus().equals(DeviceConstants.DELETE_FLAG_NORMAL) ? "启用" : "停用")
                + "成功！");
    }

    @Override
    public Result<String> delete(int id){
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();

        AlgorithmGiveConfigDO algorithmGiveConfigDODB = this.getById(id);
        if((DeviceConstants.DELETE_FLAG_NORMAL).equals(algorithmGiveConfigDODB.getStatus())){
            return Result.success("启用状态不允许删除算法！");
        }

        AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
        algorithmGiveConfigDO.setId(id);
        algorithmGiveConfigDO.setDeleteFlag(DeviceConstants.YES_FLAG);
        algorithmGiveConfigDO.setModifier(user.getUserName());
        algorithmGiveConfigDO.setUpdateTime(new Date());
        this.updateById(algorithmGiveConfigDO);

        return Result.success("前端算法删除成功！");
    }

    @DataScope(alarmSourceColumn = "alarm_source", typeValueColumn = "type_value")
    @Override
    public List<AlarmTypeInfoVO> queryAlarmType(AlarmTypeReq alarmTypeReq){
        List<AlarmTypeInfoVO> resultList = new ArrayList<>();
        List<String> enumCodeList = new ArrayList<>();
        //获取赠送算法
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(OrderConstants.SYS_GIVE_ALGORITHM);
        List<SysDictDataResponse> dictList = sysResult.getData();
        if (!CollectionUtils.isEmpty(dictList)) {
            for (SysDictDataResponse sysDictData : dictList) {
                enumCodeList.add(sysDictData.getDictValue());
            }
        }
        if(!CollectionUtils.isEmpty(enumCodeList)){
            alarmTypeReq.setEnumCodeList(enumCodeList);
            resultList = algorithmGiveConfigMapper.queryAlarmType(alarmTypeReq);
        }
        return resultList;
    }

    @Override
    public Result<String> checkAlarmType(AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO){
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();
        algorithmGiveConfigSaveVO.setTenantId(user.getTenantId());
        algorithmGiveConfigSaveVO.setIndustryCode(user.getIndustryCode());
        int count = algorithmGiveConfigMapper.checkAlarmType(algorithmGiveConfigSaveVO);
        if (count > 0) {
            return Result.fail("该前端算法已配置，不可重复配置");
        }
        return Result.success("校验通过！");
    }

    @Override
    @Transactional
    public void algorithmGiveMergeOrder(AlgorithmGiveMergeOrderVO algorithmGiveMergeOrderVO){
        log.info("订单设备退订整合前端算法入参" + JSONObject.toJSONString(algorithmGiveMergeOrderVO));
        List<String> deviceCodeList = algorithmGiveMergeOrderVO.getDeviceCodeList();
        // 收集需要整合的设备通道（第1步退订设备通道 + 第2步待更新的give_config通道）
        Map<String, DeviceChannelVO> mergeChannelMap = new LinkedHashMap<>();

        // 1.取deviceCodeList字段，将退订的设备相关的前端算法停用（原逻辑）
        if(!CollectionUtils.isEmpty(deviceCodeList)){
            //校验订单下的该设备是否有启用的前端算法
            int i = algorithmGiveConfigMapper.selectDoStopBatch(algorithmGiveMergeOrderVO);
            if(i > 0){
                algorithmGiveConfigMapper.doStopBatch(algorithmGiveMergeOrderVO);
                // 收集退订设备的通道
                List<DeviceChannelVO> stopChannelList = algorithmGiveConfigMapper.getDevChanInfo(deviceCodeList);
                if(!CollectionUtils.isEmpty(stopChannelList)){
                    for(DeviceChannelVO vo : stopChannelList){
                        mergeChannelMap.put(vo.getDeviceCode() + LINE + vo.getChannelCode(), vo);
                    }
                }
            }
        }

        // 2.根据orderCode、tenantId、industryCode查询vm_order_algorithm_config表的摄像机数据
        List<AlgorithmGiveConfigDO> configList = algorithmGiveConfigMapper.selectOrderAlgorithmConfig(algorithmGiveMergeOrderVO);
        if(!CollectionUtils.isEmpty(configList)){
            // 精准匹配：先查待更新的设备通道，有则更新
            List<DeviceChannelVO> channelsToUpdate = algorithmGiveConfigMapper.selectGiveConfigChannelsNotInPrecise(algorithmGiveMergeOrderVO);
            if(!CollectionUtils.isEmpty(channelsToUpdate)){
                algorithmGiveConfigMapper.updateGiveConfigNotInPrecise(algorithmGiveMergeOrderVO);
                for(DeviceChannelVO vo : channelsToUpdate){
                    mergeChannelMap.put(vo.getDeviceCode() + LINE + vo.getChannelCode(), vo);
                }
            }
            algorithmGiveConfigMapper.updatePresetNotInPrecise(algorithmGiveMergeOrderVO);
        }else{
            // 非精准匹配：先查待更新的设备通道，有则更新
            List<DeviceChannelVO> channelsToUpdate = algorithmGiveConfigMapper.selectGiveConfigChannelsNotInOrder(algorithmGiveMergeOrderVO);
            if(!CollectionUtils.isEmpty(channelsToUpdate)){
                algorithmGiveConfigMapper.updateGiveConfigNotInOrder(algorithmGiveMergeOrderVO);
                for(DeviceChannelVO vo : channelsToUpdate){
                    mergeChannelMap.put(vo.getDeviceCode() + LINE + vo.getChannelCode(), vo);
                }
            }
            algorithmGiveConfigMapper.updatePresetNotInOrder(algorithmGiveMergeOrderVO);
        }

        // 3.判断收集的通道列表，有则整合，否则不需要整合
        if(!mergeChannelMap.isEmpty()){
            List<DeviceChannelVO> deviceChannelList = new ArrayList<>(mergeChannelMap.values());
            log.info("整合通道列表->{}", JSON.toJSONString(deviceChannelList));
            this.algorithmGiveConfigMerge(deviceChannelList);
        }
    }
    /**
     * 功能描述: 前端告警整合
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @Override
    public void algorithmGiveConfigMerge(List<DeviceChannelVO> deviceChannelList){
        log.info("前端算法整合开始" + JSONArray.toJSONString(deviceChannelList));
        //需要新增整合表记录集合
        List<AlgorithmGiveMergeDO> saveList = new ArrayList<>();
        //需要更新整合表记录集合
        List<AlgorithmGiveMergeDO> updateList = new ArrayList<>();
        //需要删除整合表记录集合
        List<AlgorithmGiveMergeDO> deleteList  = new ArrayList<>();

        //查询卡口告警list
        GiveMergeReq giveMergeReq = new GiveMergeReq();
        giveMergeReq.setDeviceChannelList(deviceChannelList);
        //前端算法配置表重新整合
        List<AlgorithmGiveMergeDO> configList = algorithmGiveConfigMapper.algorithmGiveConfigMerge(giveMergeReq);
        //获取triggerRule
        List<AlgorithmGiveMergeDO> triggerRules = algorithmGiveConfigMapper.findTriggerListByDeviceChannel(giveMergeReq);
        //构建map
        Map<String, List<String>> deviceChannelTrigger = new HashMap<>();
        if (!triggerRules.isEmpty()) {
            triggerRules.forEach(area -> {
                if (null != area) {
                    if (!area.getTriggerRule().isEmpty()) {
                        if (deviceChannelTrigger.containsKey(area.getDeviceCode() + LINE + area.getChannelCode())) {
                            List<String> strings = deviceChannelTrigger.get(area.getDeviceCode() + LINE + area.getChannelCode());
                            strings.add(area.getTriggerRule());
                            deviceChannelTrigger.put(area.getDeviceCode() + LINE + area.getChannelCode(), strings);
                        } else {
                            List<String> list = new ArrayList<>();
                            list.add(area.getTriggerRule());
                            deviceChannelTrigger.put(area.getDeviceCode() + LINE + area.getChannelCode(), list);
                        }
                    }
                }
            });
        }
        log.info("构建tri map:{}", JSON.toJSONString(deviceChannelTrigger));
        configList.forEach(item -> {
            String key = item.getDeviceCode() + LINE + item.getChannelCode();
            if (deviceChannelTrigger.containsKey(key)) {
                item.setTriggerRule(deviceChannelTrigger.get(key).toString());
                item.setTriggerRuleList(deviceChannelTrigger.get(key));
            }
        });

        //查询设备对应的全部通道
        List<String> deviceCodeList = configList.stream().map(AlgorithmGiveMergeDO::getDeviceCode).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(deviceCodeList)){
            List<DeviceChannelVO> channelList = algorithmGiveConfigMapper.getDevChanInfo(deviceCodeList);

            //匹配自身通道外的通道
            configList.stream().map(item -> {
                        List<DeviceChannelVO> deviceChannelDOList = new ArrayList();
                        channelList.stream().filter(area-> Objects.equals(item.getDeviceCode(),area.getDeviceCode())
                                //&& !Objects.equals(item.getChannelCode(),area.getChannelCode())
                        ).forEach(area-> {
                            DeviceChannelVO vo = new DeviceChannelVO();
                            vo.setDeviceCode(area.getDeviceCode());
                            vo.setChannelCode(area.getChannelCode());
                            vo.setTriggerRule(area.getTriggerRule());
                            deviceChannelDOList.add(vo);
                        });
                        item.setDeviceChannelList(deviceChannelDOList);
                        return item;
                    }).collect(Collectors.toList())
                    .forEach(item -> {
                        List<DeviceChannelVO> getDeviceChannelList = item.getDeviceChannelList();
                        String deviceChannelStr;
                        if(!CollectionUtils.isEmpty(getDeviceChannelList)){
                            ObjectMapper mapper2 = new ObjectMapper();
                            try {
                                deviceChannelStr = mapper2.writeValueAsString(getDeviceChannelList);
                                item.setPictureInfo(deviceChannelStr);
                                item.setVideoSetInfo(deviceChannelStr);
                            } catch (JsonProcessingException e) {
                                log.error("实体转换为json异常",e);
                            }
                        }
                    });
        }

        //查询整合表记录
        List<AlgorithmGiveMergeDO> mergeList = algorithmGiveMergeMapper.queryAlgorithmGiveMerge(deviceChannelList);

        if(CollectionUtils.isEmpty(mergeList)){
            saveList.addAll(configList);
        }else{
            saveList = configList.stream().filter(pe ->
                            containEntity(pe.getDeviceCode(),pe.getChannelCode(), mergeList) <= -1).
                    collect(Collectors.toList());

            updateList = configList.stream().map(item -> {
                mergeList.stream().filter(area-> Objects.equals(item.getDeviceCode(),area.getDeviceCode()) &&
                        Objects.equals(item.getChannelCode(),area.getChannelCode())
                ).forEach(area-> {
                    item.setId(area.getId());
                    item.setMergeId(area.getMergeId());
                });
                return item;
            }).collect(Collectors.toList()).stream().filter(pe -> !ObjectUtils.isEmpty(pe.getId())).collect(Collectors.toList());

            deleteList = mergeList.stream().filter(pe ->
                            containEntity(pe.getDeviceCode(),pe.getChannelCode(), configList) <= -1).
                    collect(Collectors.toList());
        }

        if(!CollectionUtils.isEmpty(saveList)){
            saveList.stream().forEach(item -> item.setCreateTime(new Date()));
            //新增整合表
            algorithmGiveMergeService.saveBatch(saveList);
        }
        if(!CollectionUtils.isEmpty(updateList)){
            updateList.stream().forEach(item -> item.setUpdateTime(new Date()));
            //更新整合表
            algorithmGiveMergeMapper.editBatchById(updateList);
        }

        if(!CollectionUtils.isEmpty(deleteList)){
            deleteList.stream().forEach(item -> item.setUpdateTime(new Date()));
            //删除整合数据
            algorithmGiveMergeMapper.deleteBatchById(deleteList);
        }

        //********************************* 前端算法整合结果同步基础平台 start *********************************
        List<AlgorithmGiveMergeDO> saveSyncList = new ArrayList<>();
        List<AlgorithmGiveMergeDO> updateSyncList = new ArrayList<>();
        List<AlgorithmGiveMergeDO> deleteSyncList = new ArrayList<>();

        //添加新增集合
        saveSyncList.addAll(saveList);
        //添加更新集合中mergeId为空的数据，同步接口按照【新增】处理
        saveSyncList.addAll(updateList.stream().filter(item -> ObjectUtils.isEmpty(item.getMergeId())).collect(Collectors.toList()));
        //添加更新集合中mergeId不为空的数据，同步接口按照【更新】处理
        updateSyncList.addAll(updateList.stream().filter(item -> !ObjectUtils.isEmpty(item.getMergeId())).collect(Collectors.toList()));
        //添加删除集合中mergeId不为空的数据，同步接口按照【删除】处理
        deleteSyncList.addAll(deleteList.stream().filter(item -> !ObjectUtils.isEmpty(item.getMergeId())).collect(Collectors.toList()));

        if (!CollectionUtils.isEmpty(saveSyncList)) {
            //6.36.	异步调用【增加通道告警联动预案策略】
            CompletableFuture<Result<JSONObject>> futureResult = CompletableFutureHelper.orTimeout(
                    CompletableFuture.supplyAsync(() -> this.addAlarmLinkageListNorthPost(this.getInJsonStr(saveSyncList)), threadPoolTaskExecutor)
                            .exceptionally(e -> {
                                log.error(DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_ERROR , e);
                                return Result.fail(DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_ERROR + e.getMessage());
                            }),
                    60, TimeUnit.SECONDS).exceptionally(e -> {
                log.error(DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_TIMEOUT, e);
                return Result.fail(DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_TIMEOUT); }
            );
            futureResult.whenComplete((t,u)->{
                try {
                    //获取异步同步结果
                    this.getSyncResult(futureResult.get(),saveSyncList);
                    //更新配置表状态
                    List<AlgorithmGiveConfigDO> configResult = this.getConfigResult(futureResult.get());
                    if(configResult.size() > DeviceConstants.ZERO_NUM){
                        algorithmGiveConfigMapper.editBatchConfigByChan(configResult);
                    }
                } catch (Exception e) {
                    log.error("增加通道告警联动预案策略异常捕获", e);
                }
                //更新表
                algorithmGiveMergeMapper.editBatchSyncById(saveSyncList);
            }).exceptionally((e)->{
                log.error(DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_ERROR, e);
                return Result.fail(DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_ERROR+e.getMessage());
            });
        }

        if (!CollectionUtils.isEmpty(updateSyncList)) {
            //6.37.	异步调用【修改通道告警联动预案策略】
            CompletableFuture<Result<JSONObject>> futureResult = CompletableFutureHelper.orTimeout(
                    CompletableFuture.supplyAsync(() -> this.updateAlarmLinkageListNorthPost(this.getInJsonStr(updateSyncList)), threadPoolTaskExecutor)
                            .exceptionally(e -> {
                                log.error(DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_ERROR , e);
                                return Result.fail(DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_ERROR+ e.getMessage());
                            }),
                    60, TimeUnit.SECONDS).exceptionally(e -> {
                log.error(DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_TIMEOUT, e);
                return Result.fail(DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_TIMEOUT); }
            );

            futureResult.whenComplete((t,u)->{
                try {
                    //获取异步同步结果
                    this.getSyncResult(futureResult.get(),updateSyncList);
                    //更新配置表状态
                    List<AlgorithmGiveConfigDO> configResult = this.getConfigResult(futureResult.get());
                    if(configResult.size() > DeviceConstants.ZERO_NUM){
                        algorithmGiveConfigMapper.editBatchConfigByChan(configResult);
                    }
                } catch (Exception e) {
                    log.error("修改通道告警联动预案策略异常捕获", e);
                }
                //更新表
                algorithmGiveMergeMapper.editBatchSyncById(updateSyncList);
            }).exceptionally((e)->{
                log.error(DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_ERROR, e);
                return Result.fail(DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_ERROR+e.getMessage());
            });
        }

        if (!CollectionUtils.isEmpty(deleteSyncList)) {
            //6.38.	异步调用【删除通道告警联动预案策略】
            CompletableFuture<Result<JSONObject>> futureResult = CompletableFutureHelper.orTimeout(
                    CompletableFuture.supplyAsync(() -> this.delAlarmLinkageListNorthPost(this.getInJsonStrDel(deleteSyncList)), threadPoolTaskExecutor)
                            .exceptionally(e -> {
                                log.error(DeviceNorthIntfConstants.DELETE_ALARM_LINKAGE_ERROR , e);
                                return Result.fail(DeviceNorthIntfConstants.DELETE_ALARM_LINKAGE_ERROR+ e.getMessage());
                            }),
                    60, TimeUnit.SECONDS).exceptionally(e -> {
                log.error(DeviceNorthIntfConstants.DELETE_ALARM_LINKAGE_TIMEOUT, e);
                return Result.fail(DeviceNorthIntfConstants.DELETE_ALARM_LINKAGE_TIMEOUT); }
            );

            futureResult.whenComplete((t,u)->{
                try {
                    //获取异步同步结果
                    this.getSyncResult(futureResult.get(),deleteSyncList);
                    //更新配置表状态
                    List<AlgorithmGiveConfigDO> configResult = this.getConfigResult(futureResult.get());
                    if(configResult.size() > DeviceConstants.ZERO_NUM){
                        algorithmGiveConfigMapper.editBatchConfigByChan(configResult);
                    }
                } catch (Exception e) {
                    log.error("删除通道告警联动预案策略异常捕获", e);
                }
                //更新表
                algorithmGiveMergeMapper.editBatchSyncById(deleteSyncList);
            }).exceptionally((e)->{
                log.error(DeviceNorthIntfConstants.DELETE_ALARM_LINKAGE_ERROR, e);
                return Result.fail(DeviceNorthIntfConstants.DELETE_ALARM_LINKAGE_ERROR+e.getMessage());
            });
        }
        //********************************* 前端算法整合结果同步基础平台 end *********************************

        log.info("前端算法整合结束");
    }

    public int containEntity(String deviceCode, String channelCode, List<AlgorithmGiveMergeDO> list) {
        int res = -1;
        for (int i = 0; i < list.size(); i++) {
            if (deviceCode.equals(list.get(i).getDeviceCode()) &&
                    channelCode.equals(list.get(i).getChannelCode())) {
                res = i;
                break;
            }
        }
        return res;
    }


    public void getSyncResult(Result<JSONObject> result,List<AlgorithmGiveMergeDO> list){
        Date date = new Date();
        JSONObject data = (JSONObject)result.getData().get(DeviceConstants.DATA);
        if (ResultCode.SUCCESS.getCode() == result.getCode()) {
            List<JSONObject> success = (List<JSONObject>)data.get(DeviceConstants.SUCCESS_SMALL);
            if(!CollectionUtils.isEmpty(success)){
                list.stream().map(item -> {
                    success.stream().filter(area-> Objects.equals(item.getChannelCode(),area.get(DeviceConstants.CHANNELCODE))
                    ).forEach(area-> {
                        if(!ObjectUtils.isEmpty(area.getInteger(DeviceConstants.ALARMLINKAGEID))){
                            item.setMergeId(area.getInteger(DeviceConstants.ALARMLINKAGEID));
                        }
                        item.setSyncStatus(DeviceConstants.CUSTOMER_STATUS_0);
                        item.setFailReason(null);
                        item.setUpdateTime(date);
                    });
                    return item;
                }).collect(Collectors.toList());
            }

            List<JSONObject> fail = (List<JSONObject>)data.get(DeviceConstants.FAIL_SMALL);
            if(!CollectionUtils.isEmpty(fail)){
                list.stream().map(item -> {
                    fail.stream().filter(area-> Objects.equals(item.getChannelCode(),area.get(DeviceConstants.CHANNELCODE))
                    ).forEach(area-> {
                        item.setSyncStatus(DeviceConstants.CUSTOMER_STATUS_1);
                        item.setFailReason(null);
                        item.setUpdateTime(date);
                    });
                    return item;
                }).collect(Collectors.toList());
            }
        }else{
            list.stream().forEach(item -> {
                item.setUpdateTime(date);
                item.setSyncStatus(DeviceConstants.CUSTOMER_STATUS_1);
                item.setFailReason(result.getMsg());
            });
        }
    }


    public List<AlgorithmGiveSyncVO> getInJsonStr(List<AlgorithmGiveMergeDO> configList){
        List<AlgorithmGiveSyncVO> resultList = new ArrayList<>();
        for (int i = 0; i < configList.size(); i++) {
            AlgorithmGiveMergeDO algorithmGiveMergeDO = configList.get(i);
            AlgorithmGiveSyncVO algorithmGiveSyncVO = new AlgorithmGiveSyncVO();

            String pictureInfo = algorithmGiveMergeDO.getPictureInfo();
            String videoSetInfo = algorithmGiveMergeDO.getVideoSetInfo();
            if(!StringUtils.isEmpty(pictureInfo)){
                algorithmGiveSyncVO.setPictureInfo(JSONObject.parseArray(pictureInfo, DeviceChannelVO.class));
            }
            if(!StringUtils.isEmpty(videoSetInfo)){
                algorithmGiveSyncVO.setVideoSetInfo(JSONObject.parseArray(videoSetInfo, DeviceChannelVO.class));
            }

            algorithmGiveSyncVO.setDeviceCode(algorithmGiveMergeDO.getDeviceCode());
            algorithmGiveSyncVO.setChannelCode(algorithmGiveMergeDO.getChannelCode());
            algorithmGiveSyncVO.setAlarmLinkageId(algorithmGiveMergeDO.getMergeId());

            SecondAnalysisVO secondAnalysisVO = new SecondAnalysisVO();
            String alarmType = algorithmGiveMergeDO.getAlarmType();
            String alarmTypeNoalg = algorithmGiveMergeDO.getAlarmTypeNoalg();
            String algorithmCode = algorithmGiveMergeDO.getAlgorithmCode();
            //开启
            AlarmAlgorithmVO enable = new AlarmAlgorithmVO();
            if(!StringUtils.isEmpty(alarmType) && !StringUtils.isEmpty(algorithmCode)){
                //1-length，为开启二次研判开启对应的告警类型
                enable.setAlarmType(Arrays.asList(alarmType.split(DeviceConstants.COMMA)));
                enable.setAlgorithmCode(Arrays.asList(algorithmCode.split(DeviceConstants.COMMA)));
                secondAnalysisVO.setEnable(enable);
            }

            //关闭
            AlarmAlgorithmVO disable = new AlarmAlgorithmVO();
            if(!StringUtils.isEmpty(alarmTypeNoalg)){
                //length--end，为开启二次研判关闭对应的告警类型
                disable.setAlarmType(Arrays.asList(alarmTypeNoalg.split(DeviceConstants.COMMA)));
                secondAnalysisVO.setDisable(disable);
            }
            algorithmGiveSyncVO.setSecondAnalysis(secondAnalysisVO);
            //设置trigger rule 转换符号
            if(StringUtils.isNotEmpty(algorithmGiveMergeDO.getAlarmTypeNoalg())){
                if (algorithmGiveMergeDO.getAlarmTypeNoalg().contains(ALARM_TYPE_WATER)
                        || algorithmGiveMergeDO.getAlarmTypeNoalg().contains(ALARM_TYPE_WATER_LEVEL)) {
                    List<ThresholdConfig> thresholdConfig = new ArrayList<>();
                    ThresholdConfig config = new ThresholdConfig();
                    //设置水质检测告警
                    config.setAlarmType(
                            algorithmGiveMergeDO.getAlarmTypeNoalg().contains(ALARM_TYPE_WATER) ? ALARM_TYPE_WATER : ALARM_TYPE_WATER_LEVEL);

                    //查询trigger rule
                    List<String> split = algorithmGiveMergeDO.getTriggerRuleList();
                    log.info("triggerRule:{}", JSON.toJSONString(split));
                    //拼接 triggerRule
                    StringBuilder triggerRuleStr = new StringBuilder();
                    for (String s : split) {
                        log.info("trigger:{}",s);
                        if (StringUtils.isNotEmpty(s)) {
                            if (triggerRuleStr.length() != 0) {
                                triggerRuleStr.append(OR);
                            }
                            TriggerRuleJson triggerRuleJson = JSON.parseObject(s, TriggerRuleJson.class);
                            StringBuilder s1 = rangeUnity(triggerRuleJson);
                            triggerRuleStr.append(s1);
                        }
                    }
                    config.setTriggerRule(triggerRuleStr.toString());
                    thresholdConfig.add(config);
                    log.info("配置完thresholdConfig:{}", JSON.toJSONString(thresholdConfig));
                    algorithmGiveSyncVO.setThresholdConfig(thresholdConfig);
                }
            }
            resultList.add(algorithmGiveSyncVO);
        }
        return resultList;
    }


    /**
     * 构建入参
     * @param triggerRuleJson 范围  与或
     * @return
     */
    private StringBuilder rangeUnity(TriggerRuleJson triggerRuleJson){
        List<RangeJson> rangeRule = triggerRuleJson.getRangeRule();
        StringBuilder sb = new StringBuilder();
        sb.append(BRACKET_LEFT);
        for (RangeJson rangeJson : rangeRule) {
            if (sb.length() > 1) {
                sb.append(SPACE).append(triggerRuleJson.getRelation()).append(SPACE);
            }
            sb.append(BRACKET_LEFT);
            if (null == rangeJson.getMin() && null == rangeJson.getMax()) {
                return null;
            }
            if (null != rangeJson.getMin() && StringUtils.isNotEmpty(rangeJson.getMin().getSymbol()) && !IS_WU.equals(rangeJson.getMin().getSymbol())) {
                sb.append(rangeJson.getMin().getValue())
                        .append(rangeJson.getMin().getSymbol())
                        .append(SQUARE_LEFT)
                        .append(rangeJson.getField()).append(SQUARE_RIGHT);
            }
            if (sb.length() > DeviceConstants.ONE_NUM) {
                if (null != rangeJson.getMax() && StringUtils.isNotEmpty(rangeJson.getMax().getSymbol()) && !IS_WU.equals(rangeJson.getMax().getSymbol())) {
                    if (null != rangeJson.getMin() && StringUtils.isNotEmpty(rangeJson.getMin().getSymbol()) && !IS_WU.equals(rangeJson.getMin().getSymbol())) {
                        sb.append(AND);
                    }
                    sb.append(SQUARE_LEFT)
                            .append(rangeJson.getField())
                            .append(SQUARE_RIGHT)
                            .append(rangeJson.getMax().getSymbol())
                            .append(rangeJson.getMax().getValue());
                }
            } else {
                if (null != rangeJson.getMax()) {
                    sb.append(SQUARE_LEFT).append(rangeJson.getField()).append(SQUARE_RIGHT).append(rangeJson.getMax().getSymbol()).append(rangeJson.getMax().getValue());
                }
            }
            sb.append(BRACKET_RIGHT);
        }
        sb.append(BRACKET_RIGHT);
        log.info("rangeUnity =="+sb);
        return sb;

    }



    public List<DevChanSyncVO> getInJsonStrDel(List<AlgorithmGiveMergeDO> deleteList){
        List<DevChanSyncVO> list = new ArrayList<>();
        for(AlgorithmGiveMergeDO vo : deleteList){
            DevChanSyncVO devChanSyncVO = new DevChanSyncVO();
            BeanUtils.copyProperties(vo,devChanSyncVO);
            list.add(devChanSyncVO);
        }
        return list;
    }

    public List<AlgorithmGiveConfigDO> getConfigResult(Result<JSONObject> result){
        List<AlgorithmGiveConfigDO> list = new ArrayList<>();
        JSONObject data = (JSONObject)result.getData().get(DeviceConstants.DATA);
        if (ResultCode.SUCCESS.getCode() == result.getCode()) {
            List<JSONObject> success = (List<JSONObject>)data.get(DeviceConstants.SUCCESS_SMALL);
            if(!CollectionUtils.isEmpty(success)){
                for (int i = 0; i < success.size(); i++) {
                    AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
                    algorithmGiveConfigDO.setChannelCode(success.get(i).getString(DeviceConstants.CHANNELCODE));
                    algorithmGiveConfigDO.setSyncStatus(DeviceConstants.YES_FLAG);
                    list.add(algorithmGiveConfigDO);
                }
            }

            List<JSONObject> fail = (List<JSONObject>)data.get(DeviceConstants.FAIL_SMALL);
            if(!CollectionUtils.isEmpty(fail)){
                for (int i = 0; i < fail.size(); i++) {
                    AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
                    algorithmGiveConfigDO.setChannelCode(fail.get(i).getString(DeviceConstants.CHANNELCODE));
                    algorithmGiveConfigDO.setSyncStatus(DeviceConstants.TWO_FLAG);
                    list.add(algorithmGiveConfigDO);
                }
            }
        }
        return list;
    }

    /**
     * 功能描述: 前端算法整合定时任务
     *
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @XxlJob("algorithmGiveConfigMergeHandle")
    @Transactional
    @Override
    public ReturnT<String> algorithmGiveConfigMergeHandle(String param) {
        log.info("前端算法整合定时任务开始");
        XxlJobLogger.log("前端算法整合定时");

        List<AlgorithmGiveMergeDO> list = algorithmGiveMergeMapper.queryAlgorithmGiveMergeXXLJob();
        //同步失败 and mergeId is null and deleteFlag = 0
        List<AlgorithmGiveMergeDO> saveSyncList = list.stream().filter(item -> ObjectUtils.isEmpty(item.getMergeId()) &&
                (DeviceConstants.DELETE_FLAG_NORMAL).equals(item.getDeleteFlag()) ).collect(Collectors.toList());
        //同步失败 and mergeId is not null and deleteFlag = 0
        List<AlgorithmGiveMergeDO> updateSyncList = list.stream().filter(item -> !ObjectUtils.isEmpty(item.getMergeId()) &&
                (DeviceConstants.DELETE_FLAG_NORMAL).equals(item.getDeleteFlag())).collect(Collectors.toList());
        //同步失败 and mergeId is not null and deleteFlag = 1
        List<AlgorithmGiveMergeDO> deleteSyncList = list.stream().filter(item -> !ObjectUtils.isEmpty(item.getMergeId()) &&
                (DeviceConstants.YES_FLAG).equals(item.getDeleteFlag())).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(deleteSyncList)) {
            //6.38.	异步调用【删除通道告警联动预案策略】
            Result<JSONObject> result = this.delAlarmLinkageListNorthPost(this.getInJsonStrDel(deleteSyncList));
            //获取异步同步结果
            this.getSyncResult(result,deleteSyncList);
            //更新表
            algorithmGiveMergeMapper.editBatchSyncById(deleteSyncList);
            //更新配置表状态
            List<AlgorithmGiveConfigDO> configResult = this.getConfigResult(result);
            if(configResult.size() > DeviceConstants.ZERO_NUM){
                algorithmGiveConfigMapper.editBatchConfigByChan(configResult);
            }
        }

        if (!CollectionUtils.isEmpty(saveSyncList)) {
            //6.36.	异步调用【增加通道告警联动预案策略】
            Result<JSONObject> result = this.addAlarmLinkageListNorthPost(this.getInJsonStr(saveSyncList));
            //获取异步同步结果
            this.getSyncResult(result,saveSyncList);
            //更新表
            algorithmGiveMergeMapper.editBatchSyncById(saveSyncList);
            //更新配置表状态
            List<AlgorithmGiveConfigDO> configResult = this.getConfigResult(result);
            if(configResult.size() > DeviceConstants.ZERO_NUM){
                algorithmGiveConfigMapper.editBatchConfigByChan(configResult);
            }
        }

        if (!CollectionUtils.isEmpty(updateSyncList)) {
            //6.37.	异步调用【修改通道告警联动预案策略】
            Result<JSONObject> result = this.updateAlarmLinkageListNorthPost(this.getInJsonStr(updateSyncList));
            //获取异步同步结果
            this.getSyncResult(result,updateSyncList);
            //更新表
            algorithmGiveMergeMapper.editBatchSyncById(updateSyncList);
            //更新配置表状态
            List<AlgorithmGiveConfigDO> configResult = this.getConfigResult(result);
            if(configResult.size() > DeviceConstants.ZERO_NUM){
                algorithmGiveConfigMapper.editBatchConfigByChan(configResult);
            }
        }
        log.info("前端算法整合定时任务结束");
        return ReturnT.SUCCESS;
    }



    /**
     * 功能描述: 增加通道告警联动预案策略
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    public Result<JSONObject> addAlarmLinkageListNorthPost(List<AlgorithmGiveSyncVO> deviceChannelList){
        JSONObject res = new JSONObject();
        try {
            String platformCode = "";
            SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
            if(!ObjectUtils.isEmpty(config)) {
                platformCode = config.getConfigValue();
            }
            final String fromCode = platformCode;

            //根据to-code对入参分组 分组后格式：[{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
            List<Map> threadList = commonService.groupByNode(deviceChannelList, Constants.ITEM_DEVICE_CODE, iCasCadeInfoService::getCasCadeMapByDeviceCodeBatch);

            //多线程处理
            List<Result<JSONObject>> resultData = ThreadParallelUtil.run(threadList, it -> {
                log.info("addAlarmLinkageListNorthPost线程处理数据量:{}", it.size());
                return sendAlarmLinkageListNorthPost(it, fromCode, DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_LIST);
            }, ThreadParallelUtil.ConfigFactory.ADD_ALARM_LINKAGE_LIST);
            //融合返回结果
            res = this.getRes(resultData);
            log.info("addAlarmLinkageListNorthPost.resultData=" + JSONObject.toJSON(res));
        } catch (Exception e) {
            log.error(DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_ERROR, e);
        }
        return Result.data(res);
    }

    /**
     * 功能描述: 修改通道告警联动预案策略
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    public Result<JSONObject> updateAlarmLinkageListNorthPost(List<AlgorithmGiveSyncVO> deviceChannelList){
        JSONObject res = new JSONObject();
        try {
            String platformCode = "";
            SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
            if(!ObjectUtils.isEmpty(config)) {
                platformCode = config.getConfigValue();
            }
            final String fromCode = platformCode;

            //根据to-code对入参分组 分组后格式：[{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
            List<Map> threadList = commonService.groupByNode(deviceChannelList, Constants.ITEM_DEVICE_CODE, iCasCadeInfoService::getCasCadeMapByDeviceCodeBatch);

            //多线程处理
            List<Result<JSONObject>> resultData = ThreadParallelUtil.run(threadList, it -> {
                log.info("updateAlarmLinkageListNorthPost线程处理数据量:{}", it.size());
                return sendAlarmLinkageListNorthPost(it, fromCode, DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_LIST);
            }, ThreadParallelUtil.ConfigFactory.UPDATE_ALARM_LINKAGE_LIST);
            //融合返回结果
            res = this.getRes(resultData);
            log.info("updateAlarmLinkageListNorthPost.resultData=" + JSONObject.toJSON(res));
        } catch (Exception e) {
            log.error(DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_ERROR, e);
        }
        return Result.data(res);
    }


    /**
     * 功能描述: 删除通道告警联动预案策略
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    public Result<JSONObject> delAlarmLinkageListNorthPost(List<DevChanSyncVO> deviceChannelList){
        JSONObject res = new JSONObject();
        try {
            String platformCode = "";
            SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
            if(!ObjectUtils.isEmpty(config)) {
                platformCode = config.getConfigValue();
            }
            final String fromCode = platformCode;

            //根据to-code对入参分组 分组后格式：[{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
            List<Map> threadList = commonService.groupByNode(deviceChannelList, Constants.ITEM_DEVICE_CODE, iCasCadeInfoService::getCasCadeMapByDeviceCodeBatch);

            //多线程处理
            List<Result<JSONObject>> resultData = ThreadParallelUtil.run(threadList, it -> {
                log.info("delAlarmLinkageListNorthPost线程处理数据量:{}", it.size());
                return sendAlarmLinkageListNorthPost(it, fromCode, DeviceNorthIntfConstants.DEL_ALARM_LINKAGE_LIST);
            }, ThreadParallelUtil.ConfigFactory.DEL_ALARM_LINKAGE_LIST);
            //融合返回结果
            res = this.getRes(resultData);
            log.info("delAlarmLinkageListNorthPost.resultData=" + JSONObject.toJSON(res));
        } catch (Exception e) {
            log.error(DeviceNorthIntfConstants.DELETE_ALARM_LINKAGE_ERROR, e);
        }
        return Result.data(res);
    }

    private Result<JSONObject> sendAlarmLinkageListNorthPost(Map map,String fromCode,String logUrl){
        try {
            String toCode = (String)map.get(VideoConstants.KEY);
            List list = (List)map.get(VideoConstants.VALUE);
            if(!DeviceNorthIntfConstants.DEL_ALARM_LINKAGE_LIST.equals(logUrl)){
                List<AlgorithmGiveSyncVO> deviceChannelList = list;
                for (AlgorithmGiveSyncVO algorithmGiveSyncTemp : deviceChannelList) {
                    algorithmGiveSyncTemp.setAcceptNode(fromCode);
                    algorithmGiveSyncTemp.setAccessNode(toCode);
                }
                list = deviceChannelList;
            }
            NetConnectUtils.RequestParamter.URLMappingEnum url = null;
            switch (logUrl)
            {
                case DeviceNorthIntfConstants.ADD_ALARM_LINKAGE_LIST:
                    url = NetConnectUtils.RequestParamter.URLMappingEnum.ADD_ALARM_LINKAGE_LIST;
                    break;
                case DeviceNorthIntfConstants.UPDATE_ALARM_LINKAGE_LIST:
                    url = NetConnectUtils.RequestParamter.URLMappingEnum.UPDATE_ALARM_LINKAGE_LIST;
                    break;
                case DeviceNorthIntfConstants.DEL_ALARM_LINKAGE_LIST:
                    url = NetConnectUtils.RequestParamter.URLMappingEnum.DEL_ALARM_LINKAGE_LIST;
                    break;
                default:
                    break;
            }

            NetConnectUtils.RequestParamter<List> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(list);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(url);
            // 接口路径参数
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(logUrl + DeviceNorthIntfConstants.REQUEST_LOG + JSONObject.toJSON(paramter));
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(fromCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(logUrl + DeviceNorthIntfConstants.RESPONSE_LOG + JSONObject.toJSON(cr));
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            Result<JSONObject> res = apiInfoUtils.transResult(cr, respDictData.getData());
            return res;
        } catch (Exception e) {
            log.error("多线程sendAlarmLinkageListNorthPost异常" , e);
            throw new RuntimeException(e);
        }
    }

    private JSONObject getRes(List<Result<JSONObject>> resultData){
        List<JSONObject> success = new ArrayList<>();
        List<JSONObject> fail = new ArrayList<>();
        for (Result<JSONObject> datum : resultData) {
            JSONObject data = (JSONObject)datum.getData().get(DeviceConstants.DATA);
            success.addAll((List<JSONObject>)data.get(DeviceConstants.SUCCESS_SMALL));
            fail.addAll((List<JSONObject>)data.get(DeviceConstants.FAIL_SMALL));
        }
        JSONObject res = resultData.get(Constants.ZERO_NUM).getData();
        JSONObject data = new JSONObject();
        data.put(DeviceConstants.SUCCESS_SMALL,success);
        data.put(DeviceConstants.FAIL_SMALL,fail);
        res.put(DeviceConstants.DATA,data);
        return res;
    }

    @Override
    public List<BlackWhiteVO> getBlackWhiteList(BlackWhiteReq blackWhiteReq){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        blackWhiteReq.setBlackOrWhiteInt((Constants.BLACK).equals(blackWhiteReq.getBlackOrWhite()) ? 1 : 2);
        blackWhiteReq.setTenantId(user.getTenantId());
        blackWhiteReq.setIndustryCode(user.getIndustryCode());
        return algorithmGiveConfigMapper.selectGiveLicenseRel(blackWhiteReq);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> batchAddOrUpdate(AlgorithmGiveBatchSaveVO algorithmGiveBatchSave) {

        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();
        algorithmGiveBatchSave.setTenantId(user.getTenantId());
        algorithmGiveBatchSave.setIndustryCode(user.getIndustryCode());

        List<AlgorithmGiveConfigSaveVO> addOrUpdate = algorithmGiveBatchSave.getList();
        List<AlgorithmGiveConfigSaveVO> addList = new ArrayList<>();
        List<AlgorithmGiveConfigSaveVO> updateList = new ArrayList<>();

        //整合list
        List<DeviceChannelVO> mergeList = new ArrayList<>();

        //区分新增 修改
        List<AlgorithmGiveConfigSaveVO> alarmTypeDevChanList = algorithmGiveConfigMapper.checkAlarmTypeByList(algorithmGiveBatchSave);
        Map<String, Integer> keyGiveIdMap = alarmTypeDevChanList.stream()
                .collect(Collectors.toMap(item -> item.getAlarmType() + "-" + item.getDeviceCode() + "-" + item.getChannelCode(), AlgorithmGiveConfigSaveVO::getId));

        addOrUpdate.forEach(item -> {
            String key = item.getAlarmType() + "-" + item.getDeviceCode() + "-" + item.getChannelCode();
            if (keyGiveIdMap.containsKey(key)) {
                item.setId(keyGiveIdMap.get(key));
                updateList.add(item);

            } else {
                addList.add(item);
            }
        });

        //批量新增
        if (CollectionUtil.isNotEmpty(addList)) {
            log.info("批量操作-前端算法批量新增报文" + JSONObject.toJSONString(addList));

            //根据客户编码、设备编码查询有效订单号
            List<AlgorithmGiveConfigDO> orderCodeList = algorithmGiveConfigMapper.queryOrderCodeByDevices(user.getTenantId(), user.getIndustryCode(), addList);
            Map<String, String> orderMap = orderCodeList.stream().collect(Collectors.toMap(AlgorithmGiveConfigDO::getDeviceCode, AlgorithmGiveConfigDO::getOrderCode));

            List<AlgorithmGiveConfigDO> save = new ArrayList<>();
            addList.forEach(item -> {
                AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
                BeanUtils.copyProperties(item, algorithmGiveConfigDO);
                algorithmGiveConfigDO.setTenantId(user.getTenantId());
                algorithmGiveConfigDO.setTenantName(user.getTenantInfo().getTenantName());
                algorithmGiveConfigDO.setCreator(SecurityUtils.getOperator());
                algorithmGiveConfigDO.setIndustryCode(user.getIndustryCode());
                algorithmGiveConfigDO.setAppVerCode(user.getAppVerCode());
                algorithmGiveConfigDO.setCreateTime(new Date());
                if (orderMap.containsKey(algorithmGiveConfigDO.getDeviceCode())) {
                    algorithmGiveConfigDO.setOrderCode(orderMap.get(algorithmGiveConfigDO.getDeviceCode()));
                } else {
                    log.info("设备编码:{}-未查询到有效订单,订单列表:{}", algorithmGiveConfigDO.getDeviceCode(), JSONObject.toJSONString(orderMap));
                }
                save.add(algorithmGiveConfigDO);
                //保存整合数据
                DeviceChannelVO deviceChannelVO = new DeviceChannelVO();
                deviceChannelVO.setDeviceCode(algorithmGiveConfigDO.getDeviceCode());
                deviceChannelVO.setChannelCode(algorithmGiveConfigDO.getChannelCode());
                mergeList.add(deviceChannelVO);
            });
            //保存
            if (CollectionUtil.isNotEmpty(save)) {
                this.saveBatch(save);
                log.info("批量新增前端算法-条数:{}-详情:{}", save.size(), JSON.toJSONString(save));
            }
        }
        //修改
        if (CollectionUtil.isNotEmpty(updateList)) {
            log.info("批量操作-前端算法批量修改报文" + JSONObject.toJSONString(updateList));

            //卡口告警黑白名单
            List<AlgorithmGiveConfigSaveVO> giveLicenseRelList = new ArrayList<>();

            List<AlgorithmGiveConfigDO> giveConfigs = algorithmGiveConfigMapper.getByIds(updateList);
            Map<Integer, AlgorithmGiveConfigDO> giveConfigMap = giveConfigs.stream().collect(Collectors.toMap(AlgorithmGiveConfigDO::getId, item -> item));

            List<AlgorithmGiveConfigDO> update = new ArrayList<>();
            updateList.forEach(item -> {
                AlgorithmGiveConfigDO algorithmGiveConfigDO = new AlgorithmGiveConfigDO();
                algorithmGiveConfigDO.setId(item.getId());
                algorithmGiveConfigDO.setSecondSwitch(item.getSecondSwitch());
                if ((DeviceConstants.DELETE_FLAG_NORMAL).equals(algorithmGiveConfigDO.getSecondSwitch())) {
                    algorithmGiveConfigDO.setAlgorithmCode(item.getAlgorithmCode());
                    algorithmGiveConfigDO.setAlgorithmName(item.getAlgorithmName());
                }
                algorithmGiveConfigDO.setModifier(SecurityUtils.getOperator());
                algorithmGiveConfigDO.setUpdateTime(new Date());


                if (giveConfigMap.containsKey(item.getId())) {
                    AlgorithmGiveConfigDO givenDo = giveConfigMap.get(item.getId());
                    giveLicenseRelList.add(item);
                    update.add(algorithmGiveConfigDO);
                    //保存整合数据
                    DeviceChannelVO deviceChannelVO = new DeviceChannelVO();
                    deviceChannelVO.setDeviceCode(givenDo.getDeviceCode());
                    deviceChannelVO.setChannelCode(givenDo.getChannelCode());
                    mergeList.add(deviceChannelVO);
                }

            });

            //删除黑白名单
            if (CollectionUtil.isNotEmpty(giveLicenseRelList)) {
                algorithmGiveConfigMapper.deleteGiveLicenseRelBatch(giveLicenseRelList);
            }
            //更新
            if (CollectionUtil.isNotEmpty(update)) {
                this.updateBatchById(update);
                log.info("批量更新前端算法-条数:{}-详情:{}", update.size(), JSON.toJSONString(update));
            }
        }
        //整合
        if (CollectionUtil.isNotEmpty(mergeList)) {
            log.info("需要整合的前端算法:{}",mergeList.size());
            //前端算法整合
            this.algorithmGiveConfigMerge(mergeList);
        }
        return Result.success("提交成功");
    }

    @Override
    public List<AlgorithmGiveConfigSaveVO> getAlgorithmGiveDetailMCP(AlgorithmGiveConfigSaveVO algorithmInfoDO){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        algorithmInfoDO.setTenantId(user.getTenantId());
        algorithmInfoDO.setIndustryCode(user.getIndustryCode());
        return this.algorithmGiveConfigMapper.getAlgorithmGiveListMCP(algorithmInfoDO);
    }

    public Result<List<AlgorithmGiveConfigSaveVO>> insertGivenAlgoConfig(List<AlgorithmGiveConfigAdd> algorithmGiveConfigAdd){
        log.info("insertGivenAlgoConfig入参：{}",JSONObject.toJSONString(algorithmGiveConfigAdd));
        List<AlgorithmGiveConfigSaveVO> algorithmAreaVOS = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        if(CollectionUtils.isEmpty(algorithmGiveConfigAdd)){
            return Result.fail("请求参数错误！");
        }
        AlgorithmGiveConfigAdd giveConfigAdd = algorithmGiveConfigAdd.get(Constants.ZERO_NUM);

        AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO = new AlgorithmGiveConfigSaveVO();
        BeanUtils.copyProperties(giveConfigAdd, algorithmGiveConfigSaveVO);

        String triggerRule = giveConfigAdd.getTriggerRuleStr();
        if(!StringUtils.isEmpty(triggerRule)){
            algorithmGiveConfigSaveVO.setTriggerRule(JSON.parseObject(triggerRule, TriggerRuleJson.class));
        }

        //优化for循环查询
        BlackWhiteReq blackWhiteReq = new BlackWhiteReq();
        blackWhiteReq.setId(giveConfigAdd.getId());
        List<BlackWhiteVO> giveLicenseRelList = algorithmGiveConfigMapper.selectGiveLicenseRel(blackWhiteReq);
        if(!StringUtils.isEmpty(giveLicenseRelList)){
            Map<Integer, List<BlackWhiteVO>> map = giveLicenseRelList.stream().collect(Collectors.groupingBy(item->item.getBlackOrWhite()));
            Map<String, List<BlackWhiteVO>> resultMap = new HashMap<>();
            for (Map.Entry<Integer, List<BlackWhiteVO>> integerListEntry : map.entrySet()) {
                resultMap.put(integerListEntry.getKey() == Constants.ONE_NUM ? Constants.BLACK : Constants.WHITE , integerListEntry.getValue());
            }
            algorithmGiveConfigSaveVO.setBlackWhiteList(resultMap);
        }

        //校验告警类型是否已配置算法
        Result<String> checkResult = this.checkAlarmType(algorithmGiveConfigSaveVO);
        if(checkResult.getCode() != ResultCode.SUCCESS.getCode()){
            log.error("checkGiveAlgorithm异常：" + checkResult.getMsg() + ",{}", JSONObject.toJSONString(algorithmGiveConfigSaveVO));
            return Result.fail("该告警类型已配置算法！");
        }
        Result<Integer> save = this.add(algorithmGiveConfigSaveVO);
        if(save.getCode() != ResultCode.SUCCESS.getCode()){
            log.error("saveGiveAlgorithm异常：" + save.getMsg() + ",{}", JSONObject.toJSONString(algorithmGiveConfigSaveVO));
        }else{
            ids.add(save.getData());
        }

        //根据configIds查询前端算法配置
        if(!CollectionUtils.isEmpty(ids)){
            log.info("ids:{}",ids);
            AlgorithmGiveConfigDO algorithmConfigDTO = new AlgorithmGiveConfigDO();
            algorithmConfigDTO.setIds(ids);
            algorithmAreaVOS = this.queryByIds(algorithmConfigDTO);
        }
        return Result.data(algorithmAreaVOS);
    }
}
