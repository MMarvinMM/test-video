package com.chinatower.sync.consumer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.domain.OrderAlgorithmsDO;
import com.chinatower.common.core.domain.OrderCustomerDO;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.AlgoCommonReq;
import com.chinatower.device.entity.dto.AlgoInfoReq;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.sync.consumer.dao.AlgorithmConfigMapper;
import com.chinatower.sync.consumer.entity.*;
import com.chinatower.sync.consumer.item.DeviceSyncConstants;
import com.chinatower.sync.consumer.item.IndustryConstants;
import com.chinatower.sync.consumer.service.IAlgorithmConfigInfoService;
import com.chinatower.sync.consumer.service.IOrderAlgorithmsRepository;
import com.chinatower.sync.consumer.service.IOrderCustomerRepository;
import com.chinatower.sync.consumer.util.ApiInfoUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * @ClassName AlgorithmConfigService
 * @Description
 * @Author chenshaowu
 * @Date 2022/1/27
 **/
@Service
@Slf4j
public class AlgorithmConfigInfoService extends ServiceImpl<AlgorithmConfigMapper, AlgorithmConfigDO> implements IAlgorithmConfigInfoService {

    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    IOrderCustomerRepository iOrderCustomerRepository;
    @Autowired
    IOrderAlgorithmsRepository iOrderAlgorithmsRepository;

    @Resource
    AlgorithmConfigMapper algorithmConfigMapper;

    @Value("${spring.kafka2.consumer.threadNum}")
    private int threadNum;

    @Resource
    ApiInfoUtils apiInfoUtils;

    @Resource
    private NetConnectUtils connectUtils;

    @Resource
    private GisPgAPI gisPgAPI;

    @Resource
    GisMapApi gisMapApi;

    @Resource
    MongoTemplate mongoTemplate;

    /**
     * 属地化版本需要同步到订单算法表中
     * @param req
     */
    @Override
    @Async
    public void synOrderAlgorithm(AlgoCommonReq<AlgoInfoReq> req){
        try {
            // 是否属地化
            Boolean isDependenceFlag = false;
            AjaxResult ajaxResult = systemFeignClient.getConfValue(IndustryConstants.DEPENDENCE_MANAGE);
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                    isDependenceFlag = true;
                }
            }
//            isDependenceFlag = true;
            log.info("属地化版本需要同步到订单算法表中,isDependenceFlag:{}", isDependenceFlag);
            if (isDependenceFlag) {
                LambdaQueryWrapper<OrderCustomerDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(OrderCustomerDO::getIndustryCode, IndustryConstants.INDUSTRY_CODE);
                queryWrapper.eq(OrderCustomerDO::getAppVerCode, IndustryConstants.APP_VER_CODE);
                queryWrapper.eq(OrderCustomerDO::getDeleteFlag, IndustryConstants.DELETE_FLAG_NORMAL);
                List<Integer> list=new ArrayList<>();
                Collections.addAll(list,IndustryConstants.ZERO_NUM,IndustryConstants.ONE_NUM,IndustryConstants.TWO_NUM);
                queryWrapper.in(OrderCustomerDO::getOrdStatus, list);
                queryWrapper.eq(OrderCustomerDO::getTenantId, IndustryConstants.TENANT_ID);
                queryWrapper.orderByDesc(OrderCustomerDO::getCreateTime);
                queryWrapper.last("limit 1");
                OrderCustomerDO orderCustomerDO = iOrderCustomerRepository.getOne(queryWrapper);
                if(Objects.nonNull(orderCustomerDO)) {
                    List<String> algorithmCodes = req.getData().stream().filter(algoInfoReq -> (algoInfoReq.getOptType() == 1 || algoInfoReq.getOptType() == 2))
                            .map(algoInfoReq -> algoInfoReq.getAlgorithmCode()).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(algorithmCodes)) {
                        saveOrderAlgorithms(req, orderCustomerDO, algorithmCodes);
                    }
                    // 获取租户
                    String tenantId = orderCustomerDO.getTenantId();
                    // 删除
                    List<String> deleteCodes = req.getData().stream().filter(algoInfoReq -> (algoInfoReq.getOptType() == 3))
                            .map(algoInfoReq -> algoInfoReq.getAlgorithmCode()).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(deleteCodes)) {
                        LambdaUpdateWrapper<OrderAlgorithmsDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                        lambdaUpdateWrapper.eq(OrderAlgorithmsDO::getIndustryCode, IndustryConstants.INDUSTRY_CODE)
                                .eq(OrderAlgorithmsDO::getAppVerCode, IndustryConstants.APP_VER_CODE)
                                .eq(OrderAlgorithmsDO::getTenantId, tenantId)
                                .eq(OrderAlgorithmsDO::getPackageCode, IndustryConstants.INDUSTRY_CODE)
                                .eq(OrderAlgorithmsDO::getDeleteFlag, IndustryConstants.DELETE_FLAG_NORMAL)
                                .in(OrderAlgorithmsDO::getAlgorithmCode, deleteCodes)
                                .set(OrderAlgorithmsDO::getDeleteFlag, IndustryConstants.DELETE_FLAG_EXCEPTION);
                        iOrderAlgorithmsRepository.update(lambdaUpdateWrapper);
                    }
                } else {
                    log.info("根据应用编码和版本，查询不到客户");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("synOrderAlgorithm异常",e);
        }
    }

    private void saveOrderAlgorithms(AlgoCommonReq<AlgoInfoReq> req, OrderCustomerDO orderCustomerDO, List<String> algorithmCodes) {
        // 获取租户
        String tenantId = orderCustomerDO.getTenantId();
        // 校验设备订购表
        LambdaQueryWrapper<OrderAlgorithmsDO> deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.eq(OrderAlgorithmsDO::getIndustryCode, IndustryConstants.INDUSTRY_CODE);
        deviceWrapper.eq(OrderAlgorithmsDO::getAppVerCode, IndustryConstants.APP_VER_CODE);
        deviceWrapper.eq(OrderAlgorithmsDO::getTenantId, tenantId);
        // 默认使用行业编码
        deviceWrapper.eq(OrderAlgorithmsDO::getPackageCode, IndustryConstants.INDUSTRY_CODE);
        deviceWrapper.eq(OrderAlgorithmsDO::getDeleteFlag, IndustryConstants.DELETE_FLAG_NORMAL);

        // 根据算法编码分组
        final Map<String, List<AlgoInfoReq>> listMap = req.getData().stream().filter(algoInfoReq -> (algoInfoReq.getOptType() == 1 || algoInfoReq.getOptType() == 2))
                .collect(Collectors.groupingBy(algoInfoReq -> algoInfoReq.getAlgorithmCode()));
        // 求差集
        deviceWrapper.in(OrderAlgorithmsDO::getAlgorithmCode, algorithmCodes);
        List<OrderAlgorithmsDO> existList = iOrderAlgorithmsRepository.list(deviceWrapper);
        List<String> addAlgorithmCodes = null;
        if (!CollectionUtils.isEmpty(existList)) {
            List<String> existAlgorithmCodes = existList.stream().map(orderAlgorithmsDO -> orderAlgorithmsDO.getAlgorithmCode()).collect(Collectors.toList());
            addAlgorithmCodes = algorithmCodes.stream().filter(algorithmCode -> !existAlgorithmCodes.contains(algorithmCode)).collect(Collectors.toList());
        } else {
            addAlgorithmCodes = algorithmCodes;
        }

        if (!CollectionUtils.isEmpty(addAlgorithmCodes)) {
            List<OrderAlgorithmsDO> orderAlgorithmsDOS = new ArrayList<>();
            Date now = new Date();
            for (String addCode : addAlgorithmCodes) {
                OrderAlgorithmsDO addDO = new OrderAlgorithmsDO();
                addDO.setAlgorithmCode(addCode);
                addDO.setAlgorithmName(listMap.get(addCode).get(0).getAlgorithmName());
                addDO.setCreateTime(now);
                addDO.setAppVerCode(IndustryConstants.APP_VER_CODE);
                addDO.setOrderCode(orderCustomerDO.getOrderCode());
                addDO.setIndustryCode( IndustryConstants.INDUSTRY_CODE);
                addDO.setOrdStatus(orderCustomerDO.getOrdStatus());
                addDO.setPackageCode(IndustryConstants.INDUSTRY_CODE);
                addDO.setTenantId(tenantId);
                addDO.setMoveSign(Constants.ZERO_NUM);
                addDO.setDeleteFlag(Integer.parseInt(IndustryConstants.DELETE_FLAG_NORMAL));
                orderAlgorithmsDOS.add(addDO);
            }
            iOrderAlgorithmsRepository.saveBatch(orderAlgorithmsDOS);
        }
    }

    @Override
    public void mathFactoryConsumerList(List<String> messages) {
        long start = System.currentTimeMillis();
        log.info(start + "mathFactoryConsumerList=" + messages.size());
        try {
            List<TaskIdAndStatusDto> taskIdAndPushStatusList = new ArrayList<>();
            messages.forEach(kafkaMessage -> {
                ScChannelRelationDO scChannelRelationDO = JSON.parseObject(kafkaMessage,ScChannelRelationDO.class);
                List<ScChannelRelationDO.DataDTO> data = scChannelRelationDO.getData();
                if(!CollectionUtils.isEmpty(data)){
                    for (ScChannelRelationDO.DataDTO dataDTO:data){
                        String taskId = dataDTO.getTaskId();
                        TaskIdAndStatusDto taskIdAndStatusDto = new TaskIdAndStatusDto();
                        taskIdAndStatusDto.setTaskId(taskId);
                        taskIdAndStatusDto.setPushStatus(dataDTO.getPushStatus());
                        //增加时间
                        taskIdAndStatusDto.setTs(scChannelRelationDO.getTs());
                        //                taskIdAndPushStatusList.add(taskIdAndStatusDto);
                        taskIdAndPushStatusList.add(taskIdAndStatusDto);
                        log.info(start + "算法仓kafka消息={}", JSONObject.toJSONString(taskIdAndStatusDto));
                    }
                }else{
                    log.error(start + "kafkaMessage数据异常:" + kafkaMessage);
                }
            });

            if(!CollectionUtils.isEmpty(taskIdAndPushStatusList)){
                //根据taskId去重 并取ts时间最大的一条
                List<TaskIdAndStatusDto> noRepeatTaskByTime = new ArrayList<>();
                taskIdAndPushStatusList.stream().collect(Collectors.groupingBy(TaskIdAndStatusDto::getTaskId, Collectors.toList())).forEach((taskId, value) -> value.stream().reduce((a, b) -> {
                    TaskIdAndStatusDto task;
                    if (a.getTs() > b.getTs()) {
                        task = new TaskIdAndStatusDto(a.getTaskId(), a.getAlgorithmStatus(), a.getPushStatus(), a.getTs());
                    } else {
                        task = new TaskIdAndStatusDto(b.getTaskId(), b.getAlgorithmStatus(), b.getPushStatus(), b.getTs());
                    }
                    return task;
                }).ifPresent(noRepeatTaskByTime::add));
                //多线程处理  平均拆分5个线程
                List<List<TaskIdAndStatusDto>> threadList = splitListForNum(noRepeatTaskByTime, Math.min(noRepeatTaskByTime.size(), threadNum));
                log.info("一次总数据量:{}", noRepeatTaskByTime.size());
                log.info(start + "mathFactoryConsumerList整体耗时=" + (System.currentTimeMillis() - start));

                ThreadParallelUtil.run(threadList, it -> {
                    log.info("线程处理数据量:{}", it.size());
                    updateAlgorithmConfigStatusByKafka(it);
                    return null;
                }, ThreadParallelUtil.ConfigFactory.ALGORITHM_CONFIG_STATUS);
            }
        } catch (Exception e) {
            log.info("mathFactoryConsumerList异常", e);
        }
    }

    /**
     * List均分为多个List
     * @param source 初始list
     * @param num 拆分数量
     * @return 拆分后的List<List>
     */
    public <T> List<List<T>> splitListForNum(List<T> source, int num) {
        List<List<T>> result = new ArrayList<>();
        int remaider = source.size() % num;  //(先计算出余数)
        int number = source.size() / num;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < num; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * kafka推送后，更新算法状态
     * @param source 批量taskId status
     */
    private void updateAlgorithmConfigStatusByKafka(List<TaskIdAndStatusDto> source) {
        long start = System.currentTimeMillis();
        log.info(start + "updateAlgorithmConfigStatusByKafka=" + source.size());

        try {
            //根据taskId查询所有task对应的状态
            List<TaskIdAndStatusDto> taskIdAndAlgorithmStatusList = algorithmConfigMapper.getAlgorithmStatusByTaskIds(source);
            Map<String, TaskIdAndStatusDto> taskIdMap = taskIdAndAlgorithmStatusList.stream()
                    .collect(Collectors.toMap(TaskIdAndStatusDto::getTaskId, task -> task));
            //最后更新的 任务及状态list
            List<TaskIdAndStatusDto> finalTaskStatus = new ArrayList<>();
            source.forEach(item -> {
                TaskIdAndStatusDto taskIdAndAlgorithmStatus = taskIdMap.get(item.getTaskId());
                Optional<?> taskIdAndAlgorithmStatusOptional = Optional.ofNullable(taskIdAndAlgorithmStatus);
                if (taskIdAndAlgorithmStatusOptional.isPresent()) {
                    String algorithmStatus = taskIdAndAlgorithmStatus.getAlgorithmStatus();
                    if (DeviceSyncConstants.THREE_STR.equals(algorithmStatus)
                            ||DeviceSyncConstants.TWO_STR.equals(algorithmStatus)
                            ||DeviceSyncConstants.FOUR_STR.equals(algorithmStatus)){
                        String pushStatus = item.getPushStatus();
                        //1-成功 2-失败 3-新接收 4-下发中
                        if (DeviceSyncConstants.ONE_STR.equals(pushStatus)
                                || DeviceSyncConstants.TWO_STR.equals(pushStatus)
                                || DeviceSyncConstants.FOUR_STR.equals(pushStatus)) {
                            TaskIdAndStatusDto res = new TaskIdAndStatusDto();
                            res.setPushStatus(pushStatus);
                            res.setTaskId(item.getTaskId());
                            //增加判断时间
                            res.setTs(item.getTs());
                            finalTaskStatus.add(res);
                        }
                    }
                }
            });

            log.info("最后需要更新的task:{}", finalTaskStatus);
            if (!finalTaskStatus.isEmpty()) {
                //批量更新状态
                algorithmConfigMapper.updateAlgorithmStatusBatch(finalTaskStatus);
            }
        } catch (Exception e) {
            log.error("updateAlgorithmConfigStatusByKafka异常",e);
        }
        log.info(start + "updateAlgorithmConfigStatusByKafka整体耗时=" + (System.currentTimeMillis() - start));
    }

    @Override
    public void queryYgDeviceAndChannel(String[] array, DeviceSyncErrorDto deviceSyncError){
        Result<JSONObject> res = null;
        try {
            NetConnectUtils.RequestParamter<String[]> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(array);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_DEVICE_BY_CHANNEL_CODE);
            // 接口路径参数
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.QUERY_YG_DEVICE_AND_CHANNEL_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class);
            log.info(VideoConstants.QUERY_YG_DEVICE_AND_CHANNEL_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.QUERY_YG_DEVICE_AND_CHANNEL_ERROR +VideoConstants.EQUAL_INFO, e);
        }

        if (ResultCode.SUCCESS.getCode() == res.getCode()) {
            ChannelCanalDTO result = JSON.parseObject(String.valueOf(res.getData()), ChannelCanalDTO.class);
            if (result.getData().isEmpty()) {
                updateMongoRetryCount(array, deviceSyncError);
                return;
            }
            dealChannelCanal(result.getData(), deviceSyncError, array);
        }else{
            updateMongoRetryCount(array, deviceSyncError);
        }
    }

    private void updateMongoRetryCount(String[] array, DeviceSyncErrorDto deviceSyncError) {
        if (null == deviceSyncError) {
            //将错误数据保存到mongo
            if (0 < array.length) {
                DeviceSyncErrorDto deviceSyncErrorDto = new DeviceSyncErrorDto();
                deviceSyncErrorDto.setDate(DateUtil.now());
                deviceSyncErrorDto.setList(Arrays.asList(array));
                deviceSyncErrorDto.setRetryCount(0);
                mongoTemplate.insert(deviceSyncErrorDto, DeviceSyncConstants.VIDEO_DEVICE_ERR_LIST);
                log.info("设备更新失败,保存到mongo中,失败数据为:{}", (Object[]) array);
            }
        } else {
            //更新mongo错误次数
            Query query = new Query();
            query.addCriteria(Criteria.where(DeviceSyncConstants.LINE_ID).is(deviceSyncError.getId()));
            Update update = new Update();
            update.set("retryCount", deviceSyncError.getRetryCount() + 1);
            mongoTemplate.updateFirst(query, update, DeviceSyncConstants.VIDEO_DEVICE_ERR_LIST);
            log.info("通道重试数据{}失败;失败次数+1,失败数为:{}", deviceSyncError.getId(), deviceSyncError.getRetryCount() + 1);
        }
    }

    /**
     * 修改成批量操作
     * @param deviceInfoDTOList deviceList
     * @param deviceSyncError 错误mongo 数据
     * @param array channelCodes 通道code编码
     */
    public void dealChannelCanal(List<DeviceInfoDTO> deviceInfoDTOList, DeviceSyncErrorDto deviceSyncError, String[] array){

        //更新device
        List<DeviceInfoDTO> updateDevices = new ArrayList<>();
        //新增device
        List<DeviceInfoDTO> insertDevices = new ArrayList<>();

        if (deviceInfoDTOList.isEmpty()) {
            return;
        }
        //2022-12-07 修改为 物理删除 将数据保存到删除表
        List<String> deleteDevices = new ArrayList<>();
        List<String> deleteChannels = new ArrayList<>();
        deviceInfoDTOList.forEach(deviceInfo -> {
            //判断device是否删除
            String deviceDeleteFlag = deviceInfo.getDeleteFlag();
            if (StringUtils.isNotEmpty(deviceDeleteFlag)) {
                if (Constants.DEVICE_DELETE_FLAG_1.equals(deviceDeleteFlag)) {
                    //设备删除 通过设备删通道
                    deleteDevices.add(deviceInfo.getDeviceCode());
                } else {
                    //设备正常 通道删除 通过通道删通道
                    List<ChannelInfoDTO> basicDeviceChannelBo = deviceInfo.getBasicDeviceChannelBo();
                    if (!basicDeviceChannelBo.isEmpty()) {
                        basicDeviceChannelBo.forEach(channelInfo-> {
                            String channelDeleteFlag = channelInfo.getDeleteFlag();
                            if (Constants.DEVICE_DELETE_FLAG_1.equals(channelDeleteFlag)) {
                                deleteChannels.add(channelInfo.getChannelCode());
                            }
                        });
                    }
                }
            }

        });

        //更新失败的channel
        List<String> errChannelList = new ArrayList<>();

        //因为会出现 一个设备即使删除 又是更新 故 先删除
        //删除设备、通道
        try {
            log.info("需要删除的设备:{}", deleteDevices);
            if (!deleteDevices.isEmpty()) {
                //1- 挪表  device 先查询 后新增
                List<DeviceInfoDTO> delDeviceList = algorithmConfigMapper.selectByDeviceCodes(deleteDevices);
                //新增device del表数据
                if(CollectionUtil.isNotEmpty(delDeviceList)){
                    algorithmConfigMapper.insertDeviceInfoDelBatch(delDeviceList);
                }
//                algorithmConfigMapper.insertSelectDeviceInfoByDevice(deleteDevices);
                //2- 删除数据 device
                algorithmConfigMapper.deleteDeviceInfoByDevice(deleteDevices);


                //3- 挪表  channel  先查询 后新增
                List<ChannelInfoDTO> delChannelList = algorithmConfigMapper.selectChannelByDeviceCodes(deleteDevices);
                //新增channel del表数据
                if(CollectionUtil.isNotEmpty(delChannelList)){
                    algorithmConfigMapper.insertChannelInfoDelBatch(delChannelList);
                }
//                algorithmConfigMapper.insertSelectChannelInfoByDevice(deleteDevices);
                //4- 删除数据 channel
                algorithmConfigMapper.deleteChannelInfoByDevice(deleteDevices);
                //增加删除设备通道别名表数据
                algorithmConfigMapper.deleteTenantDeviceChannelByDevice(deleteDevices);
            }
        } catch (Exception e) {
            log.info("批量删除设备通道失败:{}", e);
            List<String> channelCodes = algorithmConfigMapper.getChannelCodesByDevices(deleteDevices);
            if (!channelCodes.isEmpty()) {
                log.info("删除设备通道失败，保存到错误列表:{}", channelCodes);
                errChannelList.addAll(channelCodes);
            }
        }
        //删除通道
        try {
            log.info("需要删除的通道:{}", deleteChannels);
            if (!deleteChannels.isEmpty()) {
                //1- 挪表  channel  先查询 后新增
                List<ChannelInfoDTO> delChannelList = algorithmConfigMapper.selectChannelInfoByChannel(deleteChannels);
                if(CollectionUtil.isNotEmpty(delChannelList)){
                    algorithmConfigMapper.insertChannelInfoDelBatch(delChannelList);
                }
//                algorithmConfigMapper.insertSelectChannelInfoByChannel(deleteChannels);
                //2- 删除数据 channel
                algorithmConfigMapper.deleteChannelInfoByChannel(deleteChannels);
                //增加删除设备通道别名表数据
                algorithmConfigMapper.deleteTenantDeviceChannelByChannel(deleteChannels);
            }
        } catch (Exception e) {
            log.info("批量删除通道失败:{}", e);
            if (!deleteChannels.isEmpty()) {
                log.info("删除通道失败，保存到错误列表:{}", deleteChannels);
                errChannelList.addAll(deleteChannels);
            }
        }

        //获取到所有 appModFlag =1的设备编码  (只需要更新状态的设备编号)
        List<String> deviceCodeList = algorithmConfigMapper.queryModFlagByList(deviceInfoDTOList);

        //正常逻辑处理list  剔除app_mod_flag=1
        List<DeviceInfoDTO> batchUpdateDeviceList = new ArrayList<>();
        //app_mod_flag=1 list 只更新在离线状态
        List<DeviceInfoDTO> onlyUpdateStatusList = new ArrayList<>();
        deviceInfoDTOList.forEach(item -> {
            if (deviceCodeList.contains(item.getDeviceCode())) {
                log.info("app_mod_code=1,接收的数据为:{}", JSON.toJSONString(item));
                DeviceInfoDTO device = new DeviceInfoDTO();
                device.setDeviceCode(item.getDeviceCode());
                device.setDeviceStatus(item.getDeviceStatus());
                device.setAccessNode(item.getAccessNode());
                device.setEnableVideoNode(item.getEnableVideoNode());
                device.setCusCodeShareProperty(item.getCusCodeShareProperty());
                device.setDevicePropertyId(item.getDevicePropertyId());
                log.info("app_mod_code=1,更新的数据为:{}", JSON.toJSONString(device));
                onlyUpdateStatusList.add(device);
            } else {
                batchUpdateDeviceList.add(item);
            }
        });
        log.info("最后需要更新的设备:,{}", JSON.toJSONString(batchUpdateDeviceList));
        log.info("最后更新app_mod_flag=1的设备:{}", JSON.toJSONString(onlyUpdateStatusList));

        if (!batchUpdateDeviceList.isEmpty()) {
            //查询 需要更新的设备中 已存在的设备   deviceCode  lon  lat
            List<DeviceCodeAndLonAndLatDto> updateDeviceCodes = algorithmConfigMapper.getDeviceCodeByCodes(batchUpdateDeviceList);

            //转换 map   deviceCode : {code,lon,lat,provinceId,cityId,countyId}
            Map<String, DeviceCodeAndLonAndLatDto> isExistDeviceMap = updateDeviceCodes.stream()
                    .collect(Collectors.toMap(DeviceCodeAndLonAndLatDto::getDeviceCode, device -> device));



            //设备更新操作
            batchUpdateDeviceList.forEach(deviceInfoDTO -> {

                try {
                    String deviceDeleteFlag = deviceInfoDTO.getDeleteFlag();
                    if (StringUtils.isNotEmpty(deviceDeleteFlag)) {
                    /*
                    判断device是否删除   无法直接从list移除需要删除的设备，
                    因为同一个设备可能即有删除，也有更新(2+条数)，故在循环中判断如果为不删除的数据，才进入逻辑
                    */
                        if (!Constants.DEVICE_DELETE_FLAG_1.equals(deviceDeleteFlag)) {
                        /*
                        获取到update设备列表

                        获取到insert设备列表

                        获取到区域更新列表
                         */

                            //经纬度转火星坐标
                            //if (!StringUtils.isEmpty(deviceInfoDTO.getLongitude()) && !StringUtils.isEmpty(deviceInfoDTO.getLatitude())) {
                            //    GPS gps = GPSConverterUtils.gps84_To_Gcj02(Double.parseDouble(deviceInfoDTO.getLatitude()), Double.parseDouble(deviceInfoDTO.getLongitude()));
                            //    log.info("设备：{}的84坐标：{},{}", deviceInfoDTO.getDeviceCode(), deviceInfoDTO.getLongitude(), deviceInfoDTO.getLatitude());
                            //    log.info(" -> 转换后的火星坐标：{},{}",gps.getLon(),gps.getLat());
                            //    deviceInfoDTO.setLongitude(String.valueOf(gps.getLon()));
                            //    deviceInfoDTO.setLatitude(String.valueOf(gps.getLat()));
                            //}
                            //计算海拔
                            String altitude = "0";
                            if (!StringUtils.isEmpty(deviceInfoDTO.getLongitude()) && !StringUtils.isEmpty(deviceInfoDTO.getLatitude())) {
                                Result<Integer> result = gisPgAPI.altitudeHeightQuery(deviceInfoDTO.getLongitude(), deviceInfoDTO.getLatitude());
                                log.info("/pg/altitudeHeightQuery返回报文：" + JSON.toJSONString(result));
                                if(result.getCode() == ResultCode.SUCCESS.getCode()){
                                    if(!ObjectUtils.isEmpty(result.getData())){
                                        altitude = String.valueOf(result.getData());
                                    }
                                }
                            }
                            deviceInfoDTO.setAltitude(altitude);

                            if (isExistDeviceMap.containsKey(deviceInfoDTO.getDeviceCode())) {
                                //判断经纬度是否相等
                                String lon = NullProcessUtil.nvlToString(isExistDeviceMap.get(deviceInfoDTO.getDeviceCode()).getLongitude(), "");
                                String lat = NullProcessUtil.nvlToString(isExistDeviceMap.get(deviceInfoDTO.getDeviceCode()).getLatitude(), "");
                                String provinceId = NullProcessUtil.nvlToString(isExistDeviceMap.get(deviceInfoDTO.getDeviceCode()).getProvinceId(), "");
                                String cityId = NullProcessUtil.nvlToString(isExistDeviceMap.get(deviceInfoDTO.getDeviceCode()).getCityId(), "");
                                String countyId = NullProcessUtil.nvlToString(isExistDeviceMap.get(deviceInfoDTO.getDeviceCode()).getCountyId(), "");

                                if (!StringUtils.isEmpty(provinceId)) {
                                    deviceInfoDTO.setProvinceId(provinceId);
                                }
                                if (!StringUtils.isEmpty(cityId)) {
                                    deviceInfoDTO.setCityId(cityId);
                                }
                                if (!StringUtils.isEmpty(countyId)) {
                                    deviceInfoDTO.setCountyId(countyId);
                                }

                                //如果不相等
                                //经纬度判断  无经纬度 直接放到中国   省:北京 市:北京 区:海淀
                                if (StringUtils.isEmpty(deviceInfoDTO.getLongitude()) || StringUtils.isEmpty(deviceInfoDTO.getLatitude())) {
                                    deviceInfoDTO.setProvinceId(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                                    deviceInfoDTO.setCityId(DeviceNorthIntfConstants.BEIJING_CITY);
                                    deviceInfoDTO.setCountyId(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
                                } else if (!lon.equals(deviceInfoDTO.getLongitude()) || !lat.equals(deviceInfoDTO.getLatitude()) || StringUtils.isEmpty(provinceId) || StringUtils.isEmpty(cityId) || StringUtils.isEmpty(countyId)) {
                                    //查询gis 接口 设置device 和 需要查询的area
                                    setDeviceAndAreaList(deviceInfoDTO);
                                }
                                //校验经纬度是否小数点后9位
                                setLonAndLatBy9(deviceInfoDTO);
                                //放入更新list
                                updateDevices.add(deviceInfoDTO);
                            } else {
                                //新增
                                //查询gis 接口 设置device 和 需要查询的area
                                setDeviceAndAreaList(deviceInfoDTO);
                                //校验经纬度是否小数点后9位
                                setLonAndLatBy9(deviceInfoDTO);
                                insertDevices.add(deviceInfoDTO);
                            }
                        }
                    }

                } catch (Exception e) {
                    //设备错误 放到错误列表
                    if (null != deviceInfoDTO) {
                        log.info("设备查询报错，设备为：{}", deviceInfoDTO.getDeviceCode());
                        List<ChannelInfoDTO> basicDeviceChannelBo = deviceInfoDTO.getBasicDeviceChannelBo();
                        basicDeviceChannelBo.forEach(item -> {
                            log.info("查询失败的通道保存到失败集合:{}", item.getChannelCode());
                            errChannelList.add(item.getChannelCode());
                        });
                    }
                    errChannelList.add(deviceInfoDTO.getDeviceCode());
                }
            });
        }

        // 保存逻辑
        try {
            //device
            if (!batchUpdateDeviceList.isEmpty()) {
                //更新设备信息的list
                if (!updateDevices.isEmpty()) {
                    //去重
                    List<DeviceInfoDTO> collect = updateDevices.stream().filter(distinctByKey(DeviceInfoDTO::getDeviceCode)).collect(Collectors.toList());
                    log.info("更新设备信息:{}", collect);
                    algorithmConfigMapper.updateDeviceInfoBatch(collect);
                }
                //新增设备信息的list
                if (!insertDevices.isEmpty()) {
                    //去重
                    List<DeviceInfoDTO> collect = insertDevices.stream().filter(distinctByKey(DeviceInfoDTO::getDeviceCode)).collect(Collectors.toList());
                    log.info("新增设备信息:{}", collect);
                    algorithmConfigMapper.insertDeviceInfoBatch(collect);
                }
            }

            //app_mod_flag=1的list更新
            if (!onlyUpdateStatusList.isEmpty()) {
                List<DeviceInfoDTO> collect = onlyUpdateStatusList.stream().distinct().collect(Collectors.toList());
                log.info("app_mod_flag=1的更新设备信息:{}", collect);
                algorithmConfigMapper.updateDeviceInfoBatch(collect);
            }

            //channel
            if (!deviceInfoDTOList.isEmpty()) {
                //channel list
                List<ChannelInfoDTO> channelInfoList = new ArrayList<>();

                deviceInfoDTOList.forEach(item -> {
                    if (null != item.getBasicDeviceChannelBo()) {
                        //设备状态为 未删除状态的才能加入到通道处理列表
                        if (Constants.DEVICE_DELETE_FLAG_0.equals(item.getDeleteFlag())) {
                            channelInfoList.addAll(item.getBasicDeviceChannelBo());
                        }
                    }
                });
                if (!channelInfoList.isEmpty()) {
                    //当前存在的channel
                    List<String> updateChannelCodes = algorithmConfigMapper.getChannelCodeByChannelCodes(channelInfoList);

                    //更新channel信息 list
                    List<ChannelInfoDTO> updateChannelInfoList = channelInfoList.stream()
                            .filter(filter -> updateChannelCodes.contains(filter.getChannelCode()))
                            .filter(distinctByKey(ChannelInfoDTO::getChannelCode))
                            //去除是删除标记 deleteFlag = 1 的数据
                            .filter(filter -> Constants.DEVICE_DELETE_FLAG_0.equals(filter.getDeleteFlag()))
                            .collect(Collectors.toList());
                    log.info("需要更新的通道:{}", updateChannelInfoList);
                    if (!updateChannelInfoList.isEmpty()) {
                        algorithmConfigMapper.updateChannelInfoBatch(updateChannelInfoList);
                    }
                    //新增channel信息 list
                    List<ChannelInfoDTO> insertChannelInfoList = channelInfoList.stream()
                            .filter(filter -> !updateChannelCodes.contains(filter.getChannelCode()))
                            //去除是删除标记 deleteFlag = 1 的数据
                            .filter(filter -> Constants.DEVICE_DELETE_FLAG_0.equals(filter.getDeleteFlag()))
                            .filter(distinctByKey(ChannelInfoDTO::getChannelCode))
                            .collect(Collectors.toList());
                    log.info("需要新增的通道:{}", insertChannelInfoList);
                    if (!insertChannelInfoList.isEmpty()) {
                        algorithmConfigMapper.insertChannelInfoBatch(insertChannelInfoList);
                    }
                }
            }
        } catch (Exception e) {
            //更新数据时出现异常
            log.info("批量更新失败:{}", e);
            //清空errChannelList 将全部通道数据放入errChannelList
            errChannelList.clear();
            errChannelList.addAll(Arrays.asList(array));
        }


        //出错的设备  保存到mongo
        if (!errChannelList.isEmpty()) {
            List<String> collect = errChannelList.stream().distinct().collect(Collectors.toList());
            log.info("更新失败的channel集合:{}", collect);
            String[] errArray = new String[collect.size()];
            updateMongoRetryCount(collect.toArray(errArray), deviceSyncError);
        } else {
            //deviceSyncError!=null 代表是重试逻辑 逻辑完成 删除mongo中的数据
            if (null != deviceSyncError) {
                Query query = Query.query(Criteria.where(DeviceSyncConstants.LINE_ID).is(deviceSyncError.getId()));
                mongoTemplate.remove(query, DeviceSyncConstants.VIDEO_DEVICE_ERR_LIST);
            }
        }
    }

    private void setLonAndLatBy9(DeviceInfoDTO deviceInfoDTO) {
        if (!StringUtils.isEmpty(deviceInfoDTO.getLatitude()) && !StringUtils.isEmpty(deviceInfoDTO.getLongitude())) {
            String latitude9 = roundByScale(Double.parseDouble(deviceInfoDTO.getLatitude()), 9);
            deviceInfoDTO.setLatitude(latitude9);
            String longitude9 = roundByScale(Double.parseDouble(deviceInfoDTO.getLongitude()), 9);
            deviceInfoDTO.setLongitude(longitude9);
        }
    }

    private String roundByScale(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if (scale == 0) {
            return new DecimalFormat(DeviceSyncConstants.ZERO_STR).format(v);
        }
        StringBuilder formatStr = new StringBuilder("0.");
        for (int i = 0; i < scale; i++) {
            formatStr.append(DeviceSyncConstants.ZERO_STR);
        }
        return new DecimalFormat(formatStr.toString()).format(v);
    }

    /**
     * 集合 根据 key去重
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void setDeviceAndAreaList(DeviceInfoDTO deviceInfoDTO) {
        //查询接口
        PoiContraryGeocodingQueryReq poiContraryGeocodingQueryReq = new PoiContraryGeocodingQueryReq();
        poiContraryGeocodingQueryReq.setLocation(deviceInfoDTO.getLongitude() + "," + deviceInfoDTO.getLatitude());

        log.info("查询的设备编号:{}", deviceInfoDTO.getDeviceCode());
        Result<List<PoiContraryGeocodingQueryRes>> listResult = gisMapApi.poiContraryGeocodingQuery(poiContraryGeocodingQueryReq);

        log.info("将经纬度坐标转换为详细的结构化地址:"+listResult);

        if (null != listResult && ResultCode.SUCCESS.getCode() == listResult.getCode() && listResult.getData() != null
                && !listResult.getData().isEmpty()) {
            PoiContraryGeocodingQueryRes poiContraryGeocodingQueryRes = listResult.getData().get(0);
            Region region = poiContraryGeocodingQueryRes.getRegion();
            if (null != region) {
                String provinceCode = NullProcessUtil.nvlToString(region.getProvinceCode(), "");
                String cityCode = NullProcessUtil.nvlToString(region.getCityCode(), "");
                String countyCode = NullProcessUtil.nvlToString(region.getCountyCode(), "");
                String townCode = NullProcessUtil.nvlToString(region.getTownCode(), "");

                //反查回来 省/市/区 编码为空 则落到 省:北京 市:北京 区:海淀
                if (StringUtils.isEmpty(provinceCode)) {
                    deviceInfoDTO.setProvinceId(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                    deviceInfoDTO.setCityId(DeviceNorthIntfConstants.BEIJING_CITY);
                    deviceInfoDTO.setCountyId(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
                } else {
                    deviceInfoDTO.setProvinceId(provinceCode);
                    //市、区 无编码  取上一级 编码
                    if (StringUtils.isEmpty(cityCode)) {
                        deviceInfoDTO.setCityId(provinceCode);
                    } else {
                        deviceInfoDTO.setCityId(cityCode);
                    }
                    if (StringUtils.isEmpty(countyCode)) {
                        if (StringUtils.isEmpty(cityCode)) {
                            deviceInfoDTO.setCountyId(provinceCode);
                        } else {
                            deviceInfoDTO.setCountyId(cityCode);
                        }
                    } else {
                        deviceInfoDTO.setCountyId(countyCode);
                    }
                    deviceInfoDTO.setTownCode(townCode);
                }
            }
        } else {
            deviceInfoDTO.setProvinceId(DeviceNorthIntfConstants.BEIJING_PROVINCE);
            deviceInfoDTO.setCityId(DeviceNorthIntfConstants.BEIJING_CITY);
            deviceInfoDTO.setCountyId(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
        }
        log.info("补全省市区后的设备信息:{}", JSON.toJSONString(deviceInfoDTO));
    }
}




