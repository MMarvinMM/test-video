package com.chinatower.event.repository.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.event.api.model.CheckAlarmByDeviceVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.CheckpointWarningOrderAddDTO;
import com.chinatower.event.mapper.CheckpointAlarmMapper;
import com.chinatower.event.repository.ICheckpointAlarmRepository;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_DICT_KEY;

/**
 * 出入口管理接口。
 */
@Slf4j
@Service
public class CheckpointAlarmRepositoryImpl implements ICheckpointAlarmRepository {

    private static final String VEHICLE_DEMO_REDIS_KEY = "checkpoint:warning:order:demo:all";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Autowired
    private CheckpointAlarmMapper checkpointAlarmMapper;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn = "a.tenant_id", industryCode = "a.industry_code", deviceColumn = "a.device_code", alarmSourceVal = "4")
    public List<CheckpointAlarmDTO> getCheckpointAlarmList(CheckpointAlarmDTO dto) {
        List<SysDictData> vehicleTypeDict = RedisUtil.getList(SYS_DICT_KEY + Constants.SYS_VEHICLE_TYPES, SysDictData.class, RedisDBType.PRIMARY);
        if (CollUtil.isEmpty(vehicleTypeDict)) {
            vehicleTypeDict = checkpointAlarmMapper.getVehicleTypeDict(Constants.SYS_VEHICLE_TYPES);
            RedisUtil.set(SYS_DICT_KEY + Constants.SYS_VEHICLE_TYPES, vehicleTypeDict, RedisDBType.PRIMARY);
        }

        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        dto.setSysDictDataList(vehicleTypeDict);
        log.info("查询车辆进出记录，请求参数：{}", JSONObject.toJSONString(dto));

        List<CheckpointAlarmDTO> checkpointAlarmList = checkpointAlarmMapper.getCheckpointAlarmList(dto);
        if (CollectionUtils.isEmpty(checkpointAlarmList)) {
            return new ArrayList<>();
        }

        encryptPictureUrls(checkpointAlarmList);
        return checkpointAlarmList;
    }

    private void encryptPictureUrls(List<CheckpointAlarmDTO> checkpointAlarmList) {
        List<String> originalPictureUrls = new ArrayList<>();
        List<String> alarmPictureUrls = new ArrayList<>();
        for (CheckpointAlarmDTO alarm : checkpointAlarmList) {
            originalPictureUrls.addAll(strToList(alarm.getOriginalPictureUrl()));
            alarmPictureUrls.addAll(strToList(alarm.getAlarmPictureUrl()));
        }

        encryptOriginalPictureUrls(checkpointAlarmList, originalPictureUrls);
        encryptAlarmPictureUrls(checkpointAlarmList, alarmPictureUrls);
    }

    private void encryptOriginalPictureUrls(List<CheckpointAlarmDTO> checkpointAlarmList, List<String> originalPictureUrls) {
        if (CollectionUtils.isEmpty(originalPictureUrls)) {
            return;
        }
        try {
            Map<String, String> encryptedUrls = fileUrlEncryptUtil.dealObsFileUrlBatch(originalPictureUrls);
            if (encryptedUrls != null && !encryptedUrls.isEmpty()) {
                for (CheckpointAlarmDTO alarm : checkpointAlarmList) {
                    alarm.setOriginalPictureUrl(dealStr(alarm.getOriginalPictureUrl(), encryptedUrls));
                }
            }
        } catch (Exception e) {
            log.error(Constants.FILE_ERROR, e);
        }
    }

    private void encryptAlarmPictureUrls(List<CheckpointAlarmDTO> checkpointAlarmList, List<String> alarmPictureUrls) {
        if (CollectionUtils.isEmpty(alarmPictureUrls)) {
            return;
        }
        try {
            Map<String, String> encryptedUrls = fileUrlEncryptUtil.dealObsFileUrlBatch(alarmPictureUrls);
            if (encryptedUrls != null && !encryptedUrls.isEmpty()) {
                for (CheckpointAlarmDTO alarm : checkpointAlarmList) {
                    alarm.setAlarmPictureUrl(dealStr(alarm.getAlarmPictureUrl(), encryptedUrls));
                }
            }
        } catch (Exception e) {
            log.error(Constants.FILE_ERROR, e);
        }
    }

    public List<String> strToList(String url) {
        if (StringUtils.isEmpty(url)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(url.split(Constants.COMMA)));
    }

    public String dealStr(String url, Map<String, String> encryptedUrls) {
        if (StringUtils.isEmpty(url) || encryptedUrls == null || encryptedUrls.isEmpty()) {
            return url;
        }
        return strToList(url).stream()
            .map(item -> encryptedUrls.getOrDefault(item, item))
            .collect(Collectors.joining(Constants.COMMA));
    }

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code", deviceColumn = "t1.device_code", alarmSourceVal = "4")
    public List<CheckAlarmByDeviceVO> getCheckAlarmByDevice(CheckAlarmByDeviceVO dto) {
        List<CheckAlarmByDeviceVO> result = checkpointAlarmMapper.getDeviceByTown(dto);
        if (CollectionUtils.isEmpty(result)) {
            return result;
        }

        dto.setDeviceList(result.stream().map(CheckAlarmByDeviceVO::getDeviceCode).collect(Collectors.toSet()));
        List<CheckAlarmByDeviceVO> counts = checkpointAlarmMapper.getCheckAlarmByDevice(dto);
        for (CheckAlarmByDeviceVO device : result) {
            counts.stream()
                .filter(count -> Objects.equals(device.getDeviceCode(), count.getDeviceCode()))
                .findFirst()
                .ifPresent(count -> device.setCount(count.getCount()));
        }

        result.sort(Comparator.comparingInt(CheckAlarmByDeviceVO::getCount));
        if ("desc".equals(dto.getOrderBy())) {
            Collections.reverse(result);
        }
        return result;
    }

    @Override
    public List<CheckAlarmByDeviceVO> getCheckpointAlarmInfo(CheckpointAlarmDTO dto) {
        return checkpointAlarmMapper.getCheckpointAlarmInfo(dto);
    }

    @Override
    public String batchAddWarningOrder(List<CheckpointWarningOrderAddDTO> records) {
        boolean scheduledTaskRequest = records == null || records.isEmpty();
        if (scheduledTaskRequest) {
            log.info("传入空列表，读取定时任务车辆Redis配置");
            records = generateScheduledTaskData();
        }
        if (CollectionUtils.isEmpty(records)) {
            String message = scheduledTaskRequest
                ? "车辆演示Redis配置为空或无有效配置，未生成车辆进出记录"
                : "未提供车辆进出记录";
            log.warn(message);
            if (scheduledTaskRequest) {
                throw new IllegalStateException(message);
            }
            return message;
        }

        int totalCount = records.size();
        int failedCount = 0;
        List<CheckpointWarningOrderAddDTO> successList = new ArrayList<>();
        for (CheckpointWarningOrderAddDTO record : records) {
            if (!isValidWarningOrderRecord(record)) {
                log.warn("车辆进出记录字段不完整，车牌号: {}", record == null ? null : record.getVehicleNumber());
                failedCount++;
                continue;
            }
            record.setWarningOrderId(cn.hutool.core.util.IdUtil.simpleUUID());
            record.setAlarmCode(cn.hutool.core.util.IdUtil.simpleUUID());
            successList.add(record);
        }

        int successCount = batchInsert(successList);
        if (successCount == 0 && !successList.isEmpty()) {
            failedCount = totalCount;
        } else {
            failedCount += successList.size() - successCount;
        }
        return String.format("批量添加完成，总共%d条，成功%d条，失败%d条", totalCount, successCount, failedCount);
    }

    private int batchInsert(List<CheckpointWarningOrderAddDTO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return 0;
        }
        try {
            int successCount = checkpointAlarmMapper.batchInsert(records);
            log.info("批量插入车辆进出记录成功，数量: {}", successCount);
            return successCount;
        } catch (Exception e) {
            log.error("批量插入车辆进出记录失败", e);
            return 0;
        }
    }

    private List<CheckpointWarningOrderAddDTO> generateScheduledTaskData() {
        try {
            Object cachedValue = redisTemplate.opsForValue().get(VEHICLE_DEMO_REDIS_KEY);
            if (cachedValue == null) {
                log.warn("车辆演示Redis数据为空，redisKey: {}", VEHICLE_DEMO_REDIS_KEY);
                return Collections.emptyList();
            }
            List<CheckpointWarningOrderAddDTO> records = JSON.parseArray(
                String.valueOf(cachedValue), CheckpointWarningOrderAddDTO.class);
            List<CheckpointWarningOrderAddDTO> validRecords = new ArrayList<>();
            appendValidScheduledTaskRecords(validRecords, VEHICLE_DEMO_REDIS_KEY, records);
            return validRecords;
        } catch (Exception e) {
            log.error("读取车辆演示Redis配置失败，redisKey: {}", VEHICLE_DEMO_REDIS_KEY, e);
            return Collections.emptyList();
        }
    }

    private void appendValidScheduledTaskRecords(List<CheckpointWarningOrderAddDTO> records, String redisKey,
                                                 List<CheckpointWarningOrderAddDTO> configRecords) {
        if (CollectionUtils.isEmpty(configRecords)) {
            log.warn("车辆演示Redis配置未包含记录，redisKey: {}", redisKey);
            return;
        }
        for (CheckpointWarningOrderAddDTO record : configRecords) {
            if (record == null) {
                log.warn("车辆演示Redis配置包含空记录，redisKey: {}", redisKey);
                continue;
            }
            record.setAlarmTime(generateRandomAlarmTime());
            if (isValidWarningOrderRecord(record)) {
                records.add(record);
            } else {
                log.warn("车辆演示Redis配置字段不完整，redisKey: {}，车牌号: {}", redisKey, record.getVehicleNumber());
            }
        }
    }

    private boolean isValidWarningOrderRecord(CheckpointWarningOrderAddDTO config) {
        return config != null
            && !StringUtils.isEmpty(config.getTenantId())
            && !StringUtils.isEmpty(config.getTenantName())
            && !StringUtils.isEmpty(config.getIndustryCode())
            && !StringUtils.isEmpty(config.getDeviceCode())
            && !StringUtils.isEmpty(config.getDeviceName())
            && !StringUtils.isEmpty(config.getChannelCode())
            && !StringUtils.isEmpty(config.getChannelName())
            && !StringUtils.isEmpty(config.getAlarmTime())
            && !StringUtils.isEmpty(config.getProvinceCode())
            && !StringUtils.isEmpty(config.getProvinceName())
            && !StringUtils.isEmpty(config.getCityCode())
            && !StringUtils.isEmpty(config.getCityName())
            && !StringUtils.isEmpty(config.getCountyCode())
            && !StringUtils.isEmpty(config.getCountyName())
            && !StringUtils.isEmpty(config.getLongitude())
            && !StringUtils.isEmpty(config.getLatitude())
            && !StringUtils.isEmpty(config.getVehicleNumber())
            && !StringUtils.isEmpty(config.getVehicleType())
            && !StringUtils.isEmpty(config.getVehicleNumColor())
            && !StringUtils.isEmpty(config.getVehicleColor())
            && !StringUtils.isEmpty(config.getOriginalPictureUrl())
            && !StringUtils.isEmpty(config.getWarningSource())
            && !StringUtils.isEmpty(config.getAlarmType())
            && !StringUtils.isEmpty(config.getAlarmBody())
            && !StringUtils.isEmpty(config.getDeleteFlag())
            && !StringUtils.isEmpty(config.getAppVerCode())
            && !StringUtils.isEmpty(config.getImportanceLevel());
    }

    private String generateRandomAlarmTime() {
        LocalDate today = LocalDate.now();
        int hour = 6 + SECURE_RANDOM.nextInt(16);
        int minute = SECURE_RANDOM.nextInt(60);
        int second = SECURE_RANDOM.nextInt(60);
        return String.format("%s %02d:%02d:%02d", today, hour, minute, second);
    }
}
