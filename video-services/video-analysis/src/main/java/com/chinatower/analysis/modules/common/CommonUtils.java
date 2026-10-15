package com.chinatower.analysis.modules.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.analysis.modules.statistic.dao.DeviceStatisticsDAO;
import com.chinatower.analysis.modules.workbench.dao.WorkbenchDAO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysDeptDetailVO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommonUtils {

    @Resource
    private SystemFeignClient systemFeignClient;
    @Resource
    private DeviceStatisticsDAO deviceStatisticsDAO;

    /**
     * 处理传入的组织条件
     * @param statisticsVO
     */
    public void handleOrgIdList(AnalysisVO analysisVO, StatisticsVO statisticsVO){
        AnalysisVO dto = new AnalysisVO();
        if (StringUtils.isNotNull(analysisVO) && StringUtils.isNotEmpty(analysisVO.getOrgIdList())){
            dto.setOrgIdList(analysisVO.getOrgIdList());
        }
        if (StringUtils.isNotNull(statisticsVO) && StringUtils.isNotEmpty(statisticsVO.getOrgIdList())){
            dto.setOrgIdList(statisticsVO.getOrgIdList());
        }
        if(!CollUtil.isEmpty(dto.getOrgIdList())){
            List<String> orgIdList = dto.getOrgIdList();
            List<String> deviceCodes = new ArrayList<>();
            List<SysDeptDeviceVO> satelliteList = new ArrayList<>();
            List<SysDeptDeviceVO> radarList = new ArrayList<>();
            //批量查询组织权限
            Result<SysDeptDetailVO> deptDeviceDetail = systemFeignClient.deptDeviceDetailBatch(orgIdList);
            if(!ObjectUtil.isNull(deptDeviceDetail) && 200 == deptDeviceDetail.getCode() && !ObjectUtil.isNull(deptDeviceDetail.getData())){
                    SysDeptDetailVO data = deptDeviceDetail.getData();
                    List<SysDeptDeviceVO> cameras = data.getCameras();
                    List<SysDeptDeviceVO> radars = data.getRadars();
                    List<SysDeptDeviceVO> sensors = data.getSensors();
                    List<SysDeptDeviceVO> satellites = data.getSatellites();
                    List<SysDeptDeviceVO> uavs = data.getUavs();
                    //摄像机、雷达、物联设备编码集合
                    deviceCodes.addAll(cameras.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
                    //deviceCodes.addAll(radars.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
                    deviceCodes.addAll(sensors.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
                    //1.8.00 20251208 添加无人机设备集合
                    deviceCodes.addAll(uavs.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));

                    //卫星区域集合（去重）
                    satelliteList.addAll(satellites.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                            new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + "-" + o.getCityId()+"-" + o.getCountyId()))), ArrayList::new)));
                    //雷达区域集合（去重）
                    radarList.addAll(radars.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                            new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + "-" + o.getCityId()+"-" + o.getCountyId()))), ArrayList::new)));

                }
                if(!CollUtil.isEmpty(satelliteList)){
                    if (StringUtils.isNotNull(analysisVO)){
                        analysisVO.setSatelliteList(satelliteList);
                    }else {
                        statisticsVO.setSatelliteList(satelliteList);
                    }
                }
                if(!CollUtil.isEmpty(radarList)){
                    if (StringUtils.isNotNull(analysisVO)){
                        analysisVO.setRadarList(radarList);
                    }else {
                        statisticsVO.setRadarList(radarList);
                    }
                }
                if(!CollUtil.isEmpty(deviceCodes)){
                    if (StringUtils.isNotNull(analysisVO)){
                        analysisVO.setDeviceCodeList(deviceCodes);
                    }else {
                        statisticsVO.setDeviceCodeList(deviceCodes);
                    }
                }
        }
    }

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
            List<String> deviceCodes = deviceStatisticsDAO.getDeviceCodeListByDeptIds(dto);
            if(!CollectionUtils.isEmpty(deviceCodes)){
                if (ObjectUtil.isNotNull(analysisVO)){
                    analysisVO.setDeviceCodeList(deviceCodes);
                }else {
                    statisticsVO.setDeviceCodeList(deviceCodes);
                }
            }
        }
    }

}
