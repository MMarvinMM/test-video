package com.chinatower.platform.devstatus.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.consumer.dao.SensorStatusDAO;
import com.chinatower.platform.consumer.item.Constants;
import com.chinatower.platform.consumer.service.SensorAttributeService;
import com.chinatower.platform.consumer.service.SensorStatusService;
import com.chinatower.platform.devstatus.item.SensorStatusRes;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询传感器状态
 * Created by zhujun on 2022/4/1.
 */
@Service
@Slf4j
public class DeviceStatusService {

//    @Autowired
//    DeviceQueryService deviceQueryService;

    @Autowired
    SensorStatusService sensorStatusService;

    @Autowired
    SensorAttributeService sensorAttributeService;

    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    /**
     * 查询设备状态
     * @param deviceCodes
     * @return
     */
    @Deprecated
    public Result queryStatus(String deviceCodes) {
        Result result;
        String resStr = null;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("deviceCodes", deviceCodes);
            String reqBody = JSON.toJSONString(map);
//            resStr = deviceQueryService.queryDeviceStatus(deviceCodes);
            SensorStatusRes res = JSON.parseObject(resStr, SensorStatusRes.class);
            if ("0000".equals(res.getReturnCode()) && res.getData() != null) {
                /*for (SensorStatusReport sensorStatusReport : res.getData()) {

                }*/
                sensorStatusService.dealSensorData(res.getData());
                result = Result.data(res.getData());
            } else {
                result = Result.fail(Constants.DEVICE_STATUS_FAIL);
            }
        } catch (Exception e) {
            log.error("查询设备状态失败:{}", e.getMessage());
            result = Result.fail(Constants.DEVICE_STATUS_FAIL);
        }
        return result;
    }

    /**
     * 查询传感器变量
     * @param deviceCodes
     * @return
     */
    public Result<List<Monitor>> queryAttribute(String deviceCodes) {
        Result<List<Monitor>> result;
        try {
            if (!StringUtils.isEmpty(deviceCodes)) {
                result = Result.data(sensorAttributeService.queryAttribute(deviceCodes));
            } else {
                result = Result.fail(Constants.DEVICE_STATUS_FAIL);
            }
        } catch (Exception e) {
            log.error("查询传感器变量失败:", e);
            result = Result.fail(Constants.DEVICE_STATUS_FAIL);
        }
        return result;
    }

    /**
     * 查询传感器变量
     * @param deviceCodes
     * @return
     */
    public Result<List<Map<String, Object>>> queryAttributeMap(String deviceCodes) {
        Result<List<Map<String, Object>>> result;
        try {
            if (!StringUtils.isEmpty(deviceCodes)) {
                result = Result.data(sensorAttributeService.queryAttributeMap(deviceCodes));
            } else {
                result = Result.fail(Constants.DEVICE_STATUS_FAIL);
            }
        } catch (Exception e) {
            log.error("查询传感器变量失败:{}", e.getMessage());
            result = Result.fail(Constants.DEVICE_STATUS_FAIL);
        }
        return result;
    }

    /**
     * 查询传感器状态站址
     * @param queryStatusFakeReq
     * @return
     */
    @DataScope(tenantColumn = "d.tenant_id",industryCode = "d.industry_code", deviceColumn = "iot.device_code")
    public Result<List<MonitorFake>> queryStatusFake(QueryStatusFakeReq queryStatusFakeReq) {
        Result<List<MonitorFake>> result;
        try {
            if (!StringUtils.isEmpty(queryStatusFakeReq.getSiteCodes())) {
                String[] siteCode = queryStatusFakeReq.getSiteCodes().split(",");
                queryStatusFakeReq.setSiteCodeList(Arrays.asList(siteCode));
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && loginUser.getUser() != null) {
                queryStatusFakeReq.setTenantId(loginUser.getUser().getTenantId());
                queryStatusFakeReq.setIndustryCode(loginUser.getUser().getIndustryCode());
            }
            List<MonitorFake> resultList = sensorStatusDAO.fakeQuery(queryStatusFakeReq);
            // 填充额外信息
            fillMonitorExternalInfo(resultList);
            result = Result.data(resultList);
        } catch (Exception e) {
            log.error("传感器状态站址查询失败: ", e);
            result = Result.fail("传感器状态站址查询失败");
        }
        return result;
    }

    /**
     * 查询传感器设备组织信息
     * @param resultList
     */
    private void fillMonitorExternalInfo(List<MonitorFake> resultList) {
        try {
            if (CollectionUtils.isNotEmpty(resultList)) {
                SysUser sysUser = SecurityUtils.getLoginUser().getUser();
                String userId = sysUser.getUserId();
                String tenantId = sysUser.getTenantId();
                String industryCode = sysUser.getIndustryCode();
                List<String> deviceCodeList = resultList.stream().map(MonitorFake::getDeviceCode)
                        .distinct().collect(Collectors.toList());

                // 获取传感器设备收藏列表
                List<String> collectDeviceCodeList = sensorStatusDAO.querySensorDeviceCollectionInfo(userId, deviceCodeList);
                // 获取传感器设备组织信息
                List<MonitorFakeDeptSubVO> deptInfoList = sensorStatusDAO.querySensorDeptInfoList(tenantId, industryCode, deviceCodeList);
                // key -> deviceCode value -> deptInfoList
                Map<String, List<MonitorFakeDeptSubVO>> deviceDeptMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(deptInfoList)) {
                    deviceDeptMap = deptInfoList.stream().collect(Collectors.groupingBy(MonitorFakeDeptSubVO::getDeviceCode));
                }
                for (MonitorFake monitorFake : resultList) {
                    monitorFake.setIsCollect(collectDeviceCodeList.contains(monitorFake.getDeviceCode()) ? 1 : 0);
                    monitorFake.setDeptInfoList(deviceDeptMap.get(monitorFake.getDeviceCode()));
                }
            }
        } catch (Exception e) {
            log.error("DeviceStatusService queryStatusFake fillMonitorExternalInfo error:", e);
        }
    }

    /**
     * 分页查询传感器状态站址
     * @param queryStatusFakeReq
     * @return
     */
    @DataScope(tenantColumn = "d.tenant_id",industryCode = "d.industry_code", deviceColumn = "iot.device_code")
    public Result<PageInfo<MonitorFake>> queryStatusFakePage(QueryStatusFakeReq queryStatusFakeReq) {
        Result<PageInfo<MonitorFake>> result;
        try {
            if (!StringUtils.isEmpty(queryStatusFakeReq.getSiteCodes())) {
                String[] siteCode = queryStatusFakeReq.getSiteCodes().split(",");
                queryStatusFakeReq.setSiteCodeList(Arrays.asList(siteCode));
            }
            if (queryStatusFakeReq.getPageNum() != null || queryStatusFakeReq.getPageSize() != null) {
                //分页开始的数
                int offset = (queryStatusFakeReq.getPageNum() - 1) * queryStatusFakeReq.getPageSize();
                queryStatusFakeReq.setLimit(queryStatusFakeReq.getPageSize());
                queryStatusFakeReq.setOffset(offset);
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && loginUser.getUser() != null) {
                queryStatusFakeReq.setTenantId(loginUser.getUser().getTenantId());
                queryStatusFakeReq.setIndustryCode(loginUser.getUser().getIndustryCode());
            }

            List<MonitorFake> monitorFakeList = sensorStatusDAO.fakeQuery(queryStatusFakeReq);
            PageInfo<MonitorFake> pageInfo = new PageInfo<>(monitorFakeList);
            pageInfo.setPageNum(queryStatusFakeReq.getPageNum());
            pageInfo.setPageSize(queryStatusFakeReq.getPageSize());
            pageInfo.setTotal(sensorStatusDAO.fakeQueryCount(queryStatusFakeReq));
            result = Result.data(pageInfo);
        } catch (Exception e) {
            log.error("传感器状态站址查询失败:{}", e.getMessage());
            result = Result.fail("传感器状态站址查询失败");
        }
        return result;
    }

    /**
     * 查询传感器数量
     * @param querySensorCountReq
     * @return
     */
    public Result<Integer> querySensorCount(QuerySensorCountReq querySensorCountReq) {
        Result<Integer> result;
        try {
            Integer integer = sensorStatusDAO.querySensorCount(querySensorCountReq);
            result = Result.data(integer);
        } catch (Exception e) {
            log.error("传感器数量查询失败:{}", e.getMessage());
            result = Result.fail("传感器数量查询失败");
        }
        return result;
    }
}
