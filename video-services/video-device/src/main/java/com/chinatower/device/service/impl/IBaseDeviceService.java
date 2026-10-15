package com.chinatower.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.utils.FormatDeviceTreeUtils;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmOrderParmaDto;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class IBaseDeviceService {

    @Autowired
    private IHornDeviceRepository iHornDeviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private SysAreaRootService sysAreaRootService;


    /**
     * 补充中国根节点
     * @param treeList
     * @return
     */
    public List<DeviceTreeVO> buildMonitorTree(List<DeviceTreeVO> treeList, boolean needDevice, boolean isHorn) {
        List<DeviceTreeVO> list = FormatDeviceTreeUtils.buildIotMonitorTree(treeList, needDevice, isHorn);
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (CollectionUtil.isNotEmpty(treeList)) {
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
        return rootTree;
    }

    /**
     * 设置tenantId,userId
     * @param entity
     * @return
     */
    public void fillUserInfo(BaseIndustryEntity entity) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser){
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            entity.setTenantId(tokenUser.getTenantId());
            entity.setUserId(sysUser.getUserId());
        }
    }

    /**
     * 设置查询半径
     * @param deviceDistanceDTO
     * @return
     */
    public DeviceDistanceDTO setRadiusAndUserInfo(DeviceDistanceDTO deviceDistanceDTO) {
        if (com.chinatower.common.core.utils.StringUtils.isBlank(deviceDistanceDTO.getRadius())) {
            //单位：米
            String distance = "10000";
            Result<List<SysDictDataResponse>> result = systemFeignClient.dictDataType(DeviceConstants.EMUM_TYPE_ID_74);
            if (result.getCode() != ResultCode.SUCCESS.getCode()) {
                throw new CustomException(result.getMsg());
            }
            List<SysDictDataResponse> sysDictDataList = result.getData();
            if (sysDictDataList != null && !sysDictDataList.isEmpty()) {
                distance = sysDictDataList.get(0).getDictValue();
            }
            deviceDistanceDTO.setRadius(String.valueOf(Integer.parseInt(distance)));
        }
        this.fillUserInfo(deviceDistanceDTO);
        return deviceDistanceDTO;
    }

    protected List<DeviceSiteVO> transferVO(List<IotDeviceVO> iotDeviceVOList) {
        List<DeviceSiteVO> deviceSiteVOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(iotDeviceVOList)) {
            iotDeviceVOList.stream().forEach(item -> {
                DeviceSiteVO deviceSiteVO = new DeviceSiteVO();
                BeanUtils.copyProperties(item, deviceSiteVO);
                if (Objects.nonNull(item.getDeviceStatus())) {
                    deviceSiteVO.setDeviceStatus(String.valueOf(item.getDeviceStatus()));
                }
                if (!CollectionUtils.isEmpty(item.getList())) {
                    List<DeviceSiteVO> childList = Lists.newArrayList();
                    item.getList().stream().forEach(child -> {
                        DeviceSiteVO childDeviceVO = new DeviceSiteVO();
                        BeanUtils.copyProperties(child, childDeviceVO);
                        if (Objects.nonNull(child.getDeviceStatus())) {
                            childDeviceVO.setDeviceStatus(String.valueOf(child.getDeviceStatus()));
                        }
                        childList.add(childDeviceVO);
                    });
                    deviceSiteVO.setList(childList);
                }
                deviceSiteVOList.add(deviceSiteVO);
            });
        }
        return deviceSiteVOList;
    }

    /**
     * 设置设备得告警事件是否完结
     */
    protected void setDeviceAlarmIsCompleted(List<IndustryDeviceTree> industryDeviceTreeList) {
        if (CollectionUtils.isNotEmpty(industryDeviceTreeList)) {
            List<String> deviceCodeList = Lists.newArrayList();
            industryDeviceTreeList.forEach(item -> {
                //获取所有设备编码
                List<String> list = item.getList().stream().map(IndustryDeviceTree::getDeviceCode).collect(Collectors.toList());
                deviceCodeList.addAll(list);
            });
            Result<List<OrderCompleteVO>> result = getOrderNumByDeviceCode(deviceCodeList);
            if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                //回填设备告警是否完结字段
                industryDeviceTreeList.forEach(item -> {
                    item.getList().forEach(device -> {
                        String isCompleteFlag = Constants.ZERO;
                        if (CollectionUtils.isNotEmpty(result.getData())) {
                            OrderCompleteVO orderCompleteVO = result.getData().stream().filter(vo -> Objects.equals(device.getDeviceCode(), String.valueOf(vo.getDeviceCode()))).findFirst().orElse(null);
                            isCompleteFlag = Objects.nonNull(orderCompleteVO) ? String.valueOf(orderCompleteVO.getIsCompleteFlag()) : Constants.ZERO;
                        }
                        device.setIsCompleteFlag(isCompleteFlag);
                    });
                    //只要有一个未完结的就展示告警角标
                    IndustryDeviceTree childTree = item.getList().stream().filter(child -> StringUtils.equals(child.getIsCompleteFlag(), Constants.ONE)).findFirst().orElse(null);
                    String completeFlag = Objects.nonNull(childTree) ? Constants.ONE : Constants.ZERO;
                    item.setIsCompleteFlag(completeFlag);
                });
            }
        }
    }

    /**
     * 设备得告警事件是否完结
     * @param deviceCodeList
     * @return
     */
    private Result<List<OrderCompleteVO>> getOrderNumByDeviceCode(List<String> deviceCodeList) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        AlarmOrderParmaDto dto = new AlarmOrderParmaDto();
        dto.setDeviceCodes(deviceCodeList);
        dto.setTenantId(sysUser.getTenantId());
        dto.setIndustryCode(sysUser.getIndustryCode());
        log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 请求参数：{}", dto);
        Result<List<OrderCompleteVO>> result = iTradeWarningOrderService.getOrderNumByDeviceCode(dto);
        log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 返回参数：{}", result);
        return result;
    }

    /**
     * 设备树回填告警事件是否完结
     */
    protected void setDeviceTreeAlarmIsCompleted(List<DeviceTreeVO> deviceTreeVOList) {
        if (CollectionUtils.isNotEmpty(deviceTreeVOList)) {
            List<String> deviceCodeList = Lists.newArrayList();
            //获取树上所有设备编码
            analysisTree(deviceTreeVOList, deviceCodeList, null);
            Result<List<OrderCompleteVO>> result = getOrderNumByDeviceCode(deviceCodeList);
            if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                //回填设备告警是否完结字段
                analysisTree(deviceTreeVOList, null, result);
            }
        }
    }

    /**
     * 设置告警是否完结标识
     * @param deviceCode
     * @param result
     */
    private String getIsCompleteFlag(String deviceCode, Result<List<OrderCompleteVO>> result) {
        String isCompleteFlag = Constants.ZERO;
        if (CollectionUtils.isNotEmpty(result.getData())) {
            OrderCompleteVO orderCompleteVO = result.getData().stream().filter(vo -> Objects.equals(deviceCode, String.valueOf(vo.getDeviceCode()))).findFirst().orElse(null);
            isCompleteFlag = Objects.nonNull(orderCompleteVO) ? String.valueOf(orderCompleteVO.getIsCompleteFlag()) : Constants.ZERO;
        }
        return  isCompleteFlag;
    }

    /**
     * 解析树
     * @param deviceTreeVOList
     * @param deviceCodeList
     */
    private void analysisTree(List<DeviceTreeVO> deviceTreeVOList, List<String> deviceCodeList, Result<List<OrderCompleteVO>> result) {
        //省份
        deviceTreeVOList.forEach(pro -> {
            //地市
            pro.getList().forEach(city -> {
                //区县
                city.getList().forEach(county -> {
                    //乡镇
                    county.getList().forEach(town -> {
                        if (CollectionUtils.isNotEmpty(town.getList())) {
                            if (Objects.isNull(result)) {
                                List<String> deviceList = town.getList().stream().filter(device -> Objects.nonNull(device.getDeviceCode())).map(DeviceTreeVO::getDeviceCode).collect(Collectors.toList());
                                deviceCodeList.addAll(deviceList);
                            }
                            //回填设备告警是否完结字段
                            else {
                                //设备层级
                                for (DeviceTreeVO tree : town.getList()) {
                                    String isCompleteFlag = getIsCompleteFlag(tree.getDeviceCode(), result);
                                    tree.setIsCompleteFlag(isCompleteFlag);
                                }
                            }
                        }
                        else if (StringUtils.isNoneBlank(town.getDeviceCode())) {
                            if (Objects.isNull(result)) {
                                deviceCodeList.add(town.getDeviceCode());
                            }
                            //回填设备告警是否完结字段
                            else {
                                String isCompleteFlag = getIsCompleteFlag(town.getDeviceCode(), result);
                                town.setIsCompleteFlag(isCompleteFlag);
                            }
                        }
                    });
                });
            });
        });
    }


    /**
     * 设置设备得告警事件是否完结
     */
    protected void setDeviceAlarmIsCompleted(List<DeviceSiteVO> deviceSiteVOList, String type) {
        if (CollectionUtils.isNotEmpty(deviceSiteVOList)) {
            List<String> deviceCodeList = Lists.newArrayList();
            //无人机取第一级节点
            if (StringUtils.equals(Constants.ZERO, type)) {
                List<String> list = deviceSiteVOList.stream().filter(device -> Objects.nonNull(device.getDeviceCode())).map(DeviceSiteVO::getDeviceCode).collect(Collectors.toList());
                deviceCodeList.addAll(list);
            }
            //物联雷达取子节点
            else {
                deviceSiteVOList.forEach(item -> {
                    //获取所有设备编码
                    List<String> list = item.getList().stream().filter(device -> Objects.nonNull(device.getDeviceCode())).map(DeviceSiteVO::getDeviceCode).collect(Collectors.toList());
                    deviceCodeList.addAll(list);
                });
            }
            Result<List<OrderCompleteVO>> result = getOrderNumByDeviceCode(deviceCodeList);
            if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                //无人机取第一级节点
                if (StringUtils.equals(Constants.ZERO, type)) {
                    deviceSiteVOList.forEach(siteVO -> {
                        String isCompleteFlag = getIsCompleteFlag(siteVO.getDeviceCode(), result);
                        siteVO.setIsCompleteFlag(isCompleteFlag);
                    });
                }
                //物联雷达取子节点
                else {
                    deviceSiteVOList.forEach(item -> {
                        item.getList().forEach(siteVO -> {
                            String isCompleteFlag = getIsCompleteFlag(siteVO.getDeviceCode(), result);
                            siteVO.setIsCompleteFlag(isCompleteFlag);
                        });
                    });
                }
            }
        }
    }
}
