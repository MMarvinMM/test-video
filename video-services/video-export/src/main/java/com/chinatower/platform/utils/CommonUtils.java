package com.chinatower.platform.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.InterceptVO;
import com.chinatower.event.entity.vo.LeaderAuidtListVO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.StatisticMapper;
import com.chinatower.platform.mapper.SysDeptDeviceMapper;
import com.chinatower.platform.mapper.SysUserMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

@Slf4j
@Component
public class CommonUtils {

    @Autowired
    private StatisticMapper statisticMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptDeviceMapper sysDeptDeviceMapper;

    /**
     * 处理传入的组织条件
     * @param statisticsVO
     */
    public void handleOrgIdDeviceList(AnalysisVO analysisVO, StatisticsVO statisticsVO){
        AnalysisVO dto = new AnalysisVO();
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == loginUser || null == loginUser.getUser()) {
                throw new CustomException("获取用户信息失败", HttpStatus.BAD_REQUEST);
            }
            dto.setTenantId(loginUser.getUser().getTenantId());
            dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        if (ObjectUtil.isNotNull(analysisVO) && StringUtils.isNotEmpty(analysisVO.getOrgIdList())){
            dto.setOrgIdList(analysisVO.getOrgIdList());
        }
        if (ObjectUtil.isNotNull(statisticsVO) && StringUtils.isNotEmpty(statisticsVO.getOrgIdList())){
            dto.setOrgIdList(statisticsVO.getOrgIdList());
        }

        if(!CollectionUtils.isEmpty(dto.getOrgIdList())){
            List<String> deviceCodes = statisticMapper.getDeviceCodeListByDeptIds(dto);
            if(!CollectionUtils.isEmpty(deviceCodes)){
                if (ObjectUtil.isNotNull(analysisVO)){
                    analysisVO.setDeviceCodeList(deviceCodes);
                }else {
                    statisticsVO.setDeviceCodeList(deviceCodes);
                }
            }
        }
    }


    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public SysConfig selectConfigByKey(String configKey) {
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY+configKey, SysConfig.class, RedisDBType.PRIMARY);
        if(ObjectUtil.isNull(config)){
            config = new SysConfig();
            config.setConfigKey(configKey);
            config = userMapper.selectConfig(config);
            if(ObjectUtil.isNotNull(config)){
                RedisUtil.set(SYS_CONFIG_KEY+configKey,config, RedisDBType.PRIMARY);
            }
        }
        return config;
    }

    /**
     * 获取所属组织
     * 1、1-AI、3-一键、4-摄像机、8-物联告警展示设备对应组织的所属组织
     * 2、9-卫星、10-雷达告警展示省市区对应组织的所属组织
     * 3、2-人工上报展示上报人对应的组织
     * 4、11-第三方告警暂不展示组织
     * @param resultList
     */
    public void getOrgInfoList(List<WarningOrderListVO> resultList, List<LeaderAuidtListVO> leaderAuidtList,
                               List<InterceptVO> interceptList, SysUser sysUser) {
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(Constants.TENANT_ID, sysUser.getTenantId());
            paramMap.put(Constants.PARAM_INDUSTRY_CODE, sysUser.getIndustryCode());
            //分别获取设备以及省市区编码
            List<String> deviceCodeList = extractDeviceCodes(resultList, leaderAuidtList, interceptList);
            List<String> regionCodeList = extractRegionCodes(resultList, leaderAuidtList, interceptList);
            List<String> userNameList = extractUserNames(resultList, leaderAuidtList);
            log.info("getOrgInfoList查询入参，设备编号：{}, 省市区编码：{}, 用户名：{}", JSON.toJSONString(deviceCodeList), JSON.toJSONString(regionCodeList), JSON.toJSONString(userNameList));

            List<Map<String, String>> camerasOrganList = null;
            List<Map<String, String>> sensorsOrganList = null;
            List<Map<String, String>> satellitesOrganList = null;
            List<Map<String, String>> raderOrganList = null;
            List<Map<String, String>> userOrganList = null;
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                paramMap.put("deviceCodeList", deviceCodeList);
                //获取AI+一键+摄像机+物联告警设备对应的组织信息
                camerasOrganList = sysDeptDeviceMapper.getCamerasOrganDetails(paramMap);
                sensorsOrganList = sysDeptDeviceMapper.getSensorsOrganDetails(paramMap);
            }
            if (!CollectionUtils.isEmpty(regionCodeList)) {
                paramMap.put("regionCodeList", regionCodeList);
                //获取卫星+雷达告警省市区对应的组织信息
                satellitesOrganList = sysDeptDeviceMapper.getSatellitesOrganDetails(paramMap);
                raderOrganList = sysDeptDeviceMapper.getRadersOrganDetails(paramMap);
            }
            if (!CollectionUtils.isEmpty(userNameList)) {
                paramMap.put("userNameList", userNameList);
                userOrganList = sysDeptDeviceMapper.getOrganUserDetails(paramMap);
            }

            //处理组织信息
            processOrgNames(resultList, camerasOrganList, sensorsOrganList, satellitesOrganList, raderOrganList, userOrganList);
            processOrgNames(leaderAuidtList, camerasOrganList, sensorsOrganList, satellitesOrganList, raderOrganList, userOrganList);
            processOrgNames(interceptList, camerasOrganList, sensorsOrganList, satellitesOrganList, raderOrganList, userOrganList);
        } catch (Exception e) {
            log.error("getOrgInfoList获取所属组织错误：", e);
        }
    }

    private List<String> extractDeviceCodes(List<WarningOrderListVO> resultList, List<LeaderAuidtListVO> leaderAuidtList,
                                            List<InterceptVO> interceptList) {
        return Stream.of(resultList, leaderAuidtList, interceptList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(item -> {
                    if (item instanceof WarningOrderListVO) return ((WarningOrderListVO) item).getDeviceCode();
                    if (item instanceof LeaderAuidtListVO) return ((LeaderAuidtListVO) item).getDeviceCode();
                    if (item instanceof InterceptVO) return ((InterceptVO) item).getDeviceCode();
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> extractRegionCodes(List<WarningOrderListVO> resultList, List<LeaderAuidtListVO> leaderAuidtList,
                                            List<InterceptVO> interceptList) {
        Set<String> regionCodes = new HashSet<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            regionCodes.addAll(resultList.stream()
                    .map(item -> item.getProvinceId() + Constants.SEP + item.getCityId() + Constants.SEP + item.getCountyId())
                    .collect(Collectors.toSet()));
        }
        if (!CollectionUtils.isEmpty(leaderAuidtList)) {
            regionCodes.addAll(leaderAuidtList.stream()
                    .map(item -> item.getProvinceId() + Constants.SEP + item.getCityId() + Constants.SEP + item.getCountyId())
                    .collect(Collectors.toSet()));
        }
        if (!CollectionUtils.isEmpty(interceptList)) {
            regionCodes.addAll(interceptList.stream()
                    .map(item -> item.getProvinceCode() + Constants.SEP + item.getCityCode() + Constants.SEP + item.getCountyCode())
                    .collect(Collectors.toSet()));
        }
        return new ArrayList<>(regionCodes);
    }

    private List<String> extractUserNames(List<WarningOrderListVO> resultList, List<LeaderAuidtListVO> leaderAuidtList) {
        return Stream.of(resultList, leaderAuidtList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(item -> {
                    if (item instanceof WarningOrderListVO) return ((WarningOrderListVO) item).getFindName();
                    if (item instanceof LeaderAuidtListVO) return ((LeaderAuidtListVO) item).getFindName();
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

    }

    private <T> void processOrgNames(List<T> warningList,
                                     List<Map<String, String>> organList,
                                     List<Map<String, String>> camerasOrganList,
                                     List<Map<String, String>> sensorsOrganList,
                                     List<Map<String, String>> satellitesOrganList,
                                     List<Map<String, String>> raderOrganList) {
        if (warningList != null) {
            for (T vo : warningList) {
                String deviceCode = getDeviceCode(vo);
                String regionName = getRegionName(vo);
                String findUserName = getFindUserName(vo);

                switch (getWarningSource(vo)) {
                    case Constants.WARNING_SOURCE_1:
                        setOrgNameFromDeviceCode(camerasOrganList, deviceCode, vo);
                        break;
                    case Constants.WARNING_SOURCE_2:
                        setOrgNameFromfindUser(organList, findUserName, vo);
                        break;
                    case Constants.WARNING_SOURCE_3:
                        setOrgNameFromDeviceCode(camerasOrganList, deviceCode, vo);
                        break;
                    case Constants.WARNING_SOURCE_4:
                        setOrgNameFromDeviceCode(camerasOrganList, deviceCode, vo);
                        break;
                    case Constants.WARNING_SOURCE_8:
                        setOrgNameFromDeviceCode(sensorsOrganList, deviceCode, vo);
                        break;
                    case Constants.WARNING_SOURCE_9:
                        setOrgNameFromRegionName(satellitesOrganList, regionName, vo);
                        break;
                    case Constants.WARNING_SOURCE_10:
                        setOrgNameFromRegionName(raderOrganList, regionName, vo);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private String getDeviceCode(Object vo) {
        if (vo instanceof WarningOrderListVO) {
            return ((WarningOrderListVO) vo).getDeviceCode();
        } else if (vo instanceof LeaderAuidtListVO) {
            return ((LeaderAuidtListVO) vo).getDeviceCode();
        } else if (vo instanceof InterceptVO) {
            return ((InterceptVO) vo).getDeviceCode();
        }
        return null;
    }

    private String getRegionName(Object vo) {
        if (vo instanceof WarningOrderListVO) {
            return ((WarningOrderListVO) vo).getProvinceId() + Constants.SEP +
                    ((WarningOrderListVO) vo).getCityId() + Constants.SEP +
                    ((WarningOrderListVO) vo).getCountyId();
        } else if (vo instanceof LeaderAuidtListVO) {
            return ((LeaderAuidtListVO) vo).getProvinceId() + Constants.SEP +
                    ((LeaderAuidtListVO) vo).getCityId() + Constants.SEP +
                    ((LeaderAuidtListVO) vo).getCountyId();
        } else if (vo instanceof InterceptVO) {
            return ((InterceptVO) vo).getProvinceCode() + Constants.SEP +
                    ((InterceptVO) vo).getCityCode() + Constants.SEP +
                    ((InterceptVO) vo).getCountyCode();
        }
        return null;
    }

    private String getFindUserName(Object vo) {
        if (vo instanceof WarningOrderListVO) {
            return ((WarningOrderListVO) vo).getFindName();
        } else if (vo instanceof LeaderAuidtListVO) {
            return ((LeaderAuidtListVO) vo).getFindName();
        }
        return null;
    }

    private String getWarningSource(Object vo) {
        if (vo instanceof WarningOrderListVO) {
            return ((WarningOrderListVO) vo).getWarningSource();
        } else if (vo instanceof LeaderAuidtListVO) {
            return ((LeaderAuidtListVO) vo).getWarningSource();
        } else if (vo instanceof InterceptVO) {
            return ((InterceptVO) vo).getWarningSource();
        }
        return null;
    }

    private void setOrgNameFromDeviceCode(List<Map<String, String>> organList, String deviceCode, Object vo) {
        String orgName = null;
        for (Map<String, String> item : organList) {
            if (deviceCode.equals(item.get(Constants.PARAM_DEVICE_CODE))) {
                orgName = item.get(Constants.RES_ORG_NAME);
                break;
            }
        }
        setOrgName(vo, orgName);
    }

    private void setOrgNameFromRegionName(List<Map<String, String>> organList, String regionName, Object vo) {
        String orgName = null;
        for (Map<String, String> item : organList) {
            if (regionName.equals(item.get(Constants.REGION_NAME))) {
                orgName = item.get(Constants.RES_ORG_NAME);
                break;
            }
        }
        setOrgName(vo, orgName);
    }

    private void setOrgNameFromfindUser(List<Map<String, String>> userOrganList, String findUserName, Object vo) {
        String orgName = null;
        for (Map<String, String> item : userOrganList) {
            if (findUserName.equals(item.get(Constants.USERNAME))) {
                orgName = item.get(Constants.RES_ORG_NAME);
                break;
            }
        }
        setOrgName(vo, orgName);
    }

    private void setOrgName(Object vo, String orgName) {
        // 根据实际类型设置组织名称
        if (vo instanceof WarningOrderListVO) {
            ((WarningOrderListVO) vo).setOrgName(orgName);
        } else if (vo instanceof LeaderAuidtListVO) {
            ((LeaderAuidtListVO) vo).setOrgName(orgName);
        } else if (vo instanceof InterceptVO) {
            ((InterceptVO) vo).setOrgName(orgName);
        }
    }
}
