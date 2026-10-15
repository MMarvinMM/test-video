package com.chinatower.device.repository.impl;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.mapper.UavFlightControlPasswordConfMapper;
import com.chinatower.device.mapper.UavSecureStrategyMapper;
import com.chinatower.device.repository.UavSecureStrategyService;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UavSecureStrategyServiceImpl implements UavSecureStrategyService {

    @Resource
    private UavSecureStrategyMapper uavSecureStrategyMapper;
    @Resource
    private UavFlightControlPasswordConfMapper uavFlightControlPasswordConfMapper;
    @Resource
    RedisTemplate redisTemplate;


    @Override
    public UavSecureStrategyInfoVO queryUavSecureStrategyDetail(UavDeviceDTO param) {
        if (StringUtils.isEmpty(param.getDeviceCode())) {
            throw new CustomException("设备编码不能为空");
        }
        UavSecureStrategyInfoVO uavSecureStrategyInfoVO = new UavSecureStrategyInfoVO();
        uavSecureStrategyInfoVO.setOpen(Constants.ZERO_NUM);
        //安全策略
        List<VmUavSecureStrategyDTO> uavSecureStrategyDTOS = uavSecureStrategyMapper.queryVmUavSecureStrategy(param);
        uavSecureStrategyInfoVO.setSecureStrategyInfos(uavSecureStrategyDTOS);
        if (CollectionUtils.isNotEmpty(uavSecureStrategyDTOS)) {
            uavSecureStrategyInfoVO.setOpen(uavSecureStrategyDTOS.get(Constants.ZERO_NUM).getOpen());
        }
        //失联告警
        List<VmUavUnconnectedAlarmDTO> uavUnconnectedAlarmDTOS = uavSecureStrategyMapper.queryVmUavUnconnectedAlarm(param);
        UavUnconnectedAlarmInfoVO unconnectedAlarmInfoVO = new UavUnconnectedAlarmInfoVO();
        unconnectedAlarmInfoVO.setUnconnectedOpen(Constants.ZERO_NUM);
        unconnectedAlarmInfoVO.setUnconnectedOpen(Constants.ZERO_NUM);
        if (CollectionUtils.isNotEmpty(uavUnconnectedAlarmDTOS)) {
            VmUavUnconnectedAlarmDTO one = uavUnconnectedAlarmDTOS.get(0);
            unconnectedAlarmInfoVO.setTimeInfo(one);
            unconnectedAlarmInfoVO.setDelayOpen(one.getDelayOpen());
            unconnectedAlarmInfoVO.setUnconnectedOpen(one.getUnconnectedOpen());
        }
        uavSecureStrategyInfoVO.setUnconnectedAlarmInfo(unconnectedAlarmInfoVO);
        // 飞行控制密码配置信息
        List<VmUavFlightControlPasswordConfDTO> uavFlightControlPasswordConfDTOS = uavFlightControlPasswordConfMapper.queryUavFlightControlPasswordConf(param);
        if (CollectionUtils.isNotEmpty(uavFlightControlPasswordConfDTOS)) {
            VmUavFlightControlPasswordConfDTO uavFlightControlPasswordConfInfo = uavFlightControlPasswordConfDTOS.get(0);
            // 处理密码， 先解密，然后加密返回前端能解密的内容
            dealResponsePassword(uavFlightControlPasswordConfInfo);

            uavSecureStrategyInfoVO.setUavFlightControlPasswordConfInfo(uavFlightControlPasswordConfInfo);
        }

        return uavSecureStrategyInfoVO;
    }

    /**
     * 批量添加无人机安全策略
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchModifyUavSecureStrategy(UavSecureStrategyInfoVO param) {
        if (CollectionUtils.isEmpty(param.getDeviceCodes())) {
            return Result.fail("设备编码不能为空");
        }
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String tenantId = sysUser.getTenantId();
        String industryCode = sysUser.getIndustryCode();
        //对设备编码去重
        List<String> deviceCodeList = param.getDeviceCodes().stream().distinct().collect(Collectors.toList());
        //关闭安全策略
        if (Objects.equals(param.getOpen(), Constants.ZERO_NUM)) {
            //删除安全策略
            batchDeleteStrategy(deviceCodeList, tenantId, industryCode);
            //删除失联告警
            batchDeleteAlarm(deviceCodeList, tenantId, industryCode);
            // 批量删除无人机飞行控制密码配置
            batchDeleteFlightControl(deviceCodeList);
        }
        //开启安全策略
        else {
            if (CollectionUtils.isEmpty(param.getSecureStrategyInfos())) {
                return Result.fail("安全策略不能为空");
            }
            //校验阈值之间是否有重复交叉的范围
            if (!checkParam(param)) {
                return Result.fail("当前阈值范围已存在");
            }
            List<VmUavSecureStrategyDTO> vmUavSecureStrategyDTOS = param.getSecureStrategyInfos().stream().filter(item -> StringUtils.isBlank(item.getMinValue()) && StringUtils.isBlank(item.getMaxValue())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(vmUavSecureStrategyDTOS)) {
                return Result.fail("阈值最大值和最小值不能同时为空!");
            }

            // 批量修改无人机飞行控制配置
            Result<String> dealResult = batchModifyUavFlightControl(deviceCodeList, sysUser, param.getUavFlightControlPasswordConfInfo());
            if (ResultCode.SUCCESS.getCode() != dealResult.getCode()) {
                return dealResult;
            }

            List<VmUavSecureStrategyDTO> list = Lists.newArrayList();
            //设置租户编码等信息
            deviceCodeList.forEach(deviceCode -> {
                param.getSecureStrategyInfos().forEach(vmUavSecureStrategyDTO -> {
                    VmUavSecureStrategyDTO dto = new VmUavSecureStrategyDTO();
                    BeanUtils.copyProperties(vmUavSecureStrategyDTO, dto);
                    dto.setDeviceCode(deviceCode);
                    dto.setCreator(sysUser.getUserName());
                    dto.setTenantId(sysUser.getTenantId());
                    dto.setIndustryCode(sysUser.getIndustryCode());
                    dto.setOpen(Constants.ONE_NUM);
                    dto.setAppVerCode(sysUser.getAppVerCode());
                    list.add(dto);
                });
            });
            //批量删除安全策略
            batchDeleteStrategy(deviceCodeList, tenantId, industryCode);
            uavSecureStrategyMapper.batchInsertStrategy(list);

            //处理失联告警配置
            dealAlarm(deviceCodeList, param.getUnconnectedAlarmInfo(), sysUser);
        }


        return Result.success("操作成功");
    }

    /**
     * 处理失联告警数据
     * @param deviceCodeList
     * @param unconnectedAlarmInfo
     * @param sysUser
     */
    public void dealAlarm(List<String> deviceCodeList, UavUnconnectedAlarmInfoVO unconnectedAlarmInfo, SysUser sysUser) {
        List<VmUavUnconnectedAlarmDTO> addList = Lists.newArrayList();
        if (Objects.nonNull(unconnectedAlarmInfo) && !Objects.equals(unconnectedAlarmInfo.getUnconnectedOpen(), Constants.ZERO_NUM)) {
            VmUavUnconnectedAlarmDTO vmUavUnconnectedAlarmDTO = unconnectedAlarmInfo.getTimeInfo();
            deviceCodeList.forEach(deviceCode -> {
                VmUavUnconnectedAlarmDTO dto = new VmUavUnconnectedAlarmDTO();
                if (Objects.nonNull(vmUavUnconnectedAlarmDTO)) {
                    BeanUtils.copyProperties(vmUavUnconnectedAlarmDTO, dto);
                }
                //关闭了则不保存延时时间
                if (Objects.equals(unconnectedAlarmInfo.getDelayOpen(), Constants.ZERO_NUM)) {
                    dto.setAlarmDelayHour(null);
                    dto.setAlarmDelayMinute(null);
                }
                //组装失联告警入库信息
                dto.setDeviceCode(deviceCode);
                dto.setTenantId(sysUser.getTenantId());
                dto.setIndustryCode(sysUser.getIndustryCode());
                dto.setDelayOpen(unconnectedAlarmInfo.getDelayOpen());
                dto.setUnconnectedOpen(unconnectedAlarmInfo.getUnconnectedOpen());
                dto.setCreator(sysUser.getUserName());
                dto.setAppVerCode(sysUser.getAppVerCode());
                addList.add(dto);
            });
        }
        //删除失联告警
        batchDeleteAlarm(deviceCodeList, sysUser.getTenantId(), sysUser.getIndustryCode());
        //批量新增
        if (CollectionUtils.isNotEmpty(addList)) {
            uavSecureStrategyMapper.batchInsertAlarm(addList);
        }

    }

    /**
     * 批量删除失联告警
     * @param deviceCodes
     */
    public void batchDeleteAlarm(List<String> deviceCodes, String tenantId, String industryCode) {
        VmUavUnconnectedAlarmDTO vmUavUnconnectedAlarmDTO = new VmUavUnconnectedAlarmDTO();
        vmUavUnconnectedAlarmDTO.setDeviceCodes(deviceCodes);
        vmUavUnconnectedAlarmDTO.setTenantId(tenantId);
        vmUavUnconnectedAlarmDTO.setIndustryCode(industryCode);
        //批量删除
        uavSecureStrategyMapper.batchDeleteAlarm(vmUavUnconnectedAlarmDTO);
    }

    /**
     * 校验各个阈值之间是否有交叉重复的范围
     *
     * @param param
     * @return
     */
    private boolean checkParam(UavSecureStrategyInfoVO param) {
        //根据策略类型分组，要求每组的阈值不能重复
        Map<String, List<VmUavSecureStrategyDTO>> mapList = param.getSecureStrategyInfos().stream().collect(Collectors.groupingBy(VmUavSecureStrategyDTO::getStrategyType));
        for (Map.Entry<String, List<VmUavSecureStrategyDTO>> entry : mapList.entrySet()) {
            List<VmUavSecureStrategyDTO> strategyDTOList = entry.getValue();
            if (CollectionUtils.isNotEmpty(strategyDTOList)) {
                //循环比对各个阈值之间是否重复,由于顺序循环，前一个批次的循环已经比对过，所以下次对比的数直接从起始值后一位开始。
                for (int i = 0; i < strategyDTOList.size() - 1; i++) {
                    for (int j = i + 1; j < strategyDTOList.size(); j++) {
                        VmUavSecureStrategyDTO secureStrategyDTO = strategyDTOList.get(i);
                        VmUavSecureStrategyDTO nextSecureStrategyDTO = strategyDTOList.get(j);
                        //两组的最大值和最小值完全相等才认为有重复的范围
                        if (isRange(secureStrategyDTO, nextSecureStrategyDTO)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 批量删除安全策略
     * @param deviceCodeList
     */
    public void batchDeleteStrategy(List<String> deviceCodeList, String tenantId, String industryCode) {
        //批量关闭安全策略
        VmUavSecureStrategyDTO dto = new VmUavSecureStrategyDTO();
        dto.setDeviceCodes(deviceCodeList);
        dto.setTenantId(tenantId);
        dto.setIndustryCode(industryCode);
        //删除安全策略
        uavSecureStrategyMapper.batchDeleteStrategy(dto);
    }

    /**
     * 比对两个区间是否有重合,判断第二组的区间是否在上一组的区间内
     *
     * @param secureStrategyDTO
     * @param nextSecureStrategyDTO
     * @return
     */
    private boolean isRange(VmUavSecureStrategyDTO secureStrategyDTO, VmUavSecureStrategyDTO nextSecureStrategyDTO) {
        //最小值
        BigDecimal min = new BigDecimal(Integer.MIN_VALUE);
        //最大值
        BigDecimal max = new BigDecimal(Integer.MAX_VALUE);
        //下一组最小值
        BigDecimal nextMin = new BigDecimal(Integer.MIN_VALUE);
        //下一组最大值
        BigDecimal nextMax = new BigDecimal(Integer.MAX_VALUE);

        //最小值
        if (StringUtils.isNotBlank(secureStrategyDTO.getMinSymbol()) && StringUtils.isNotBlank(secureStrategyDTO.getMinValue())) {
            min = new BigDecimal(secureStrategyDTO.getMinValue());
        }
        //最大值
        if (StringUtils.isNotBlank(secureStrategyDTO.getMaxSymbol()) && StringUtils.isNotBlank(secureStrategyDTO.getMaxValue())) {
            max = new BigDecimal(secureStrategyDTO.getMaxValue());
        }
        //第二组最小值
        if (StringUtils.isNotBlank(nextSecureStrategyDTO.getMinSymbol()) && StringUtils.isNotBlank(nextSecureStrategyDTO.getMinValue())) {
            nextMin = new BigDecimal(nextSecureStrategyDTO.getMinValue());
        }
        //第二组最大值
        if (StringUtils.isNotBlank(nextSecureStrategyDTO.getMaxSymbol()) && StringUtils.isNotBlank(nextSecureStrategyDTO.getMaxValue())) {
            nextMax = new BigDecimal(nextSecureStrategyDTO.getMaxValue());
        }
        //两组的最小值和最大值完全相等则认为有重复的范围
        if (min.compareTo(nextMin) == 0 && max.compareTo(nextMax) == 0) {
            return true;
        }




        return false;


    }

    /**
     * 批量修改无人机飞行控制配置
     *
     * @param deviceCodeList 设备编码列表
     * @param sysUser 操作用户信息
     * @param uavFlightControlPasswordConfInfo 无人机飞行控制密码配置信息
     * @return 操作结果
     */
    public Result<String> batchModifyUavFlightControl(List<String> deviceCodeList, SysUser sysUser, VmUavFlightControlPasswordConfDTO uavFlightControlPasswordConfInfo) {
        // 处理配置为空的情况
        if (uavFlightControlPasswordConfInfo == null) {
            return Result.success("操作成功");
        }

        // 飞行控制密码状态处理
        if (Objects.equals(uavFlightControlPasswordConfInfo.getPasswordStatus(), Constants.ZERO_NUM)) {
            batchDeleteFlightControl(deviceCodeList);
            return Result.success("操作成功");
        }

        // 参数校验
        Result<String> validationResult = validateRequiredParams(uavFlightControlPasswordConfInfo);
        if (ResultCode.SUCCESS.getCode() != validationResult.getCode()) {
            return validationResult;
        }

        // 处理前端请求密码
        dealRequestPassword(uavFlightControlPasswordConfInfo);

        // 构建配置列表
        List<VmUavFlightControlPasswordConfDTO> configList = buildConfigList(deviceCodeList, sysUser, uavFlightControlPasswordConfInfo);

        // 执行更新操作
        batchDeleteFlightControl(deviceCodeList);
        uavFlightControlPasswordConfMapper.batchInsertFlightControlPasswordConf(configList);

        // 删除Redis缓存
        removeRedisCache(deviceCodeList, sysUser);

        return Result.success("操作成功");
    }

    /**
     * 校验必要参数的方法
     *
     * @param config 包含密码、错误次数限制、锁定时间和锁定时间单位的VM UAV飞行控制密码配置DTO对象
     * @return 校验结果，包含成功或失败的信息
     */
    private Result<String> validateRequiredParams(VmUavFlightControlPasswordConfDTO config) {
        if (StringUtils.isEmpty(config.getPassword())) {
            return Result.fail("密码不能为空");
        }
        if (StringUtils.isEmpty(config.getErrorTimesLimit())) {
            return Result.fail("错误次数限制不能为空");
        }
        if (config.getLockTime() == null) {
            return Result.fail("锁定时间不能为空");
        }
        if (StringUtils.isEmpty(config.getLockTimeUnit())) {
            return Result.fail("锁定时间单位不能为空");
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 构建配置列表方法
     *
     * @param deviceCodeList 设备编码列表
     * @param sysUser        用户信息
     * @param sourceConfig   源配置信息
     * @return 配置列表
     */
    private List<VmUavFlightControlPasswordConfDTO> buildConfigList(List<String> deviceCodeList, SysUser sysUser, VmUavFlightControlPasswordConfDTO sourceConfig) {
        return deviceCodeList.stream()
                .map(deviceCode -> {
                    VmUavFlightControlPasswordConfDTO config = new VmUavFlightControlPasswordConfDTO();
                    BeanUtils.copyProperties(sourceConfig, config);
                    config.setDeviceCode(deviceCode);
                    config.setCreator(sysUser.getUserName());
                    config.setTenantId(sysUser.getTenantId());
                    config.setIndustryCode(sysUser.getIndustryCode());
                    config.setAppVerCode(sysUser.getAppVerCode());
                    return config;
                })
                .collect(Collectors.toList());
    }

    /**
     * 批量删除无人机飞行控制密码配置
     *
     * @param deviceCodeList 无人机设备编码列表
     */
    public void batchDeleteFlightControl(List<String> deviceCodeList) {
        VmUavFlightControlPasswordConfDTO dto = new VmUavFlightControlPasswordConfDTO();
        dto.setDeviceCodes(deviceCodeList);
        // 批量删除无人机飞行控制密码配置
        uavFlightControlPasswordConfMapper.batchDeleteFlightControlPasswordConf(dto);
    }

    /**
     * 处理请求密码
     *
     * @param uavFlightControlPasswordConfInfo UAV飞行控制密码配置信息
     */
    public void dealRequestPassword(VmUavFlightControlPasswordConfDTO uavFlightControlPasswordConfInfo) {
        String password = getDecryptPassword(uavFlightControlPasswordConfInfo.getPassword());
        uavFlightControlPasswordConfInfo.setPassword(SecurityUtils.encryptPassword(password));
    }

    /**
     * 处理密码响应
     *
     * @param uavFlightControlPasswordConfInfo UAV飞控密码配置信息
     */
    public void dealResponsePassword(VmUavFlightControlPasswordConfDTO uavFlightControlPasswordConfInfo) {
        String password = SecurityUtils.decryptPassword(uavFlightControlPasswordConfInfo.getPassword());
        uavFlightControlPasswordConfInfo.setPassword(getEncryptPassword(password));
    }

    /**
     * 从私钥加密的密码中获取密码
     *
     * @param pwdStr 私钥加密的密码
     * @return 密码
     */
    public String getDecryptPassword(String pwdStr) {
        try {
            if (StrUtil.isNotEmpty(pwdStr)) {
                return RsaUtils.decryptByPrivateKey(pwdStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pwdStr;
    }

    /**
     * 获取加密后的密码
     *
     * @param pwdStr 待加密的密码字符串
     * @return 加密后的密码字符串
     */
    public String getEncryptPassword(String pwdStr) {
        try {
            if (StrUtil.isNotEmpty(pwdStr)) {
                return RsaUtils.encryptByPublicKey(pwdStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pwdStr;
    }

    /**
     * 从Redis中删除指定用户设备的密码错误、空闲、锁定状态缓存
     *
     * @param deviceCodeList 设备编码列表
     * @param sysUser        用户信息
     */
    public void removeRedisCache(List<String> deviceCodeList, SysUser sysUser) {
        if (CollectionUtils.isEmpty(deviceCodeList)) {
            return; // 空列表直接返回，避免无效操作
        }
        String userId = sysUser.getUserId();
        String platformCode = sysUser.getPlatformCode();
        try {
            Set<String> keysToDelete = new HashSet<>();
            for (String deviceCode : deviceCodeList) {
                keysToDelete.add("uav:pwd:error:" + userId + platformCode + deviceCode);
                keysToDelete.add("uav:pwd:free:" + userId + platformCode + deviceCode);
                keysToDelete.add("uav:pwd:lock:" + userId + platformCode + deviceCode);
            }
            // 一次性删除所有Key
            redisTemplate.delete(keysToDelete);
        } catch (Exception e) {
            // 建议使用日志记录异常，避免直接打印堆栈
            log.error("删除Redis缓存失败，用户ID：{}，设备数量：{}", userId, deviceCodeList.size(), e);
        }
    }
}
