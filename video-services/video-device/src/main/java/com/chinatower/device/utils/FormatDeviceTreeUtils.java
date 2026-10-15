package com.chinatower.device.utils;

import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FormatDeviceTreeUtils {

    private FormatDeviceTreeUtils() {

    }

    public static List<DeviceTreeVO> buildMonitorTree4Customer(List<DeviceTreeVO> treeList, Boolean needDevice) {
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        country.setOnlineNum((int) country.getList().stream().filter(townCodeVo -> Objects.equals(Constants.ZERO, townCodeVo.getStatus())).count());
                        country.setNum(country.getList().size());
                        if (BooleanUtils.isFalse(needDevice)) {
                            country.getList().clear();
                        }
                        if (BooleanUtils.isTrue(needDevice)) {
                            // 过滤掉没有在离线的设备
                            country.setList(country.getList().stream().filter(iotDevice -> com.chinatower.common.core.utils.StringUtils.isNotEmpty(iotDevice.getStatus())
                                    && (iotDevice.getStatus().equals(Constants.ZERO) || iotDevice.getStatus().equals(Constants.ONE))
                            ).collect(Collectors.toList()));
                        }
                    });
                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    //去掉异常区县
                    city.setList(city.getList().stream().filter(areaDto ->
                            StringUtils.isNotEmpty(areaDto.getCode())).collect(Collectors.toList()));
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                //去掉异常地市
                pro.setList(pro.getList().stream().filter(proDto ->
                        StringUtils.isNotEmpty(proDto.getCode())).collect(Collectors.toList()));
            } else {
                pro.setOnlineNum(0);
                pro.setNum(0);
            }
        });
        return treeList;
    }

    public static List<DeviceTreeVO> buildIotMonitorTree(List<DeviceTreeVO> treeList, Boolean backDevFlag, boolean isHorn) {
        List<String> onlineNum = Arrays.asList("1","2","3");
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        country.getList().forEach(townCode -> {
                            if (isHorn) {
                                townCode.setOnlineNum((int) townCode.getList()
                                        .stream()
                                        .filter(townCodeVo -> onlineNum.contains(townCodeVo.getStatus())).count());
                            }
                            else {
                                townCode.setOnlineNum((int) townCode.getList().stream().filter(townCodeVo -> Objects.equals(Constants.ZERO, townCodeVo.getStatus())).count());
                            }
                            townCode.setNum(townCode.getList().size());
                            townCode.getList().forEach(device ->{
                                if (device.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))){
                                    device.setIsMonitorNum(1);
                                }
                                if (device.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))){
                                    device.setIsTagCollectNum(1);
                                }
                            });
                            townCode.setIsMonitorNum(townCode.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                            townCode.setIsTagCollectNum(townCode.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
                            //如果下级设备都为收藏，则上级设置为收藏
                            if (Objects.equals(townCode.getNum(),townCode.getIsMonitorNum())){
                                townCode.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                            }
                            if (Objects.equals(townCode.getNum(),townCode.getIsTagCollectNum())){
                                townCode.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                            }
                            if (isHorn) {
                                townCode.getList().forEach(dev -> {
                                    dev.setStatus(onlineNum.contains(dev.getStatus()) ? Constants.ZERO : Constants.ONE);
                                });
                            }
                            if (backDevFlag != null && !backDevFlag) {
                                townCode.getList().clear();
                            }
                            if (backDevFlag != null && backDevFlag) {
                                // 过滤掉没有在离线的设备
                                townCode.setList(townCode.getList().stream().filter(iotDevice -> com.chinatower.common.core.utils.StringUtils.isNotEmpty(iotDevice.getStatus())
                                        && (iotDevice.getStatus().equals(Constants.ZERO) || iotDevice.getStatus().equals(Constants.ONE))
                                ).collect(Collectors.toList()));
                            }
                        });
                        country.setOnlineNum(country.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                        country.setNum(country.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                        country.setIsMonitorNum(country.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                        country.setIsTagCollectNum(country.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
                        //如果下级设备都为收藏，则上级设置为收藏
                        if (Objects.equals(country.getNum(),country.getIsMonitorNum())){
                            country.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                        }
                        if (Objects.equals(country.getNum(),country.getIsTagCollectNum())){
                            country.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                        }
                        if (!backDevFlag) {
                            //去掉异常乡镇
                            country.setList(country.getList().stream().filter(townDto ->
                                    StringUtils.isNotEmpty(townDto.getCode()) && StringUtils.isNotEmpty(townDto.getName())).collect(Collectors.toList()));
                        } else {
                            //将有异常的乡镇数据移动到区县目录下
                            List<DeviceTreeVO> list = country.getList().stream().filter(townDto ->
                                    StringUtils.isEmpty(townDto.getCode())).collect(Collectors.toList());
                            list.forEach(deviceTreeVO -> {
                                country.getList().addAll(deviceTreeVO.getList());
                            });
                            country.setList(country.getList().stream().filter(townDto ->
                                    StringUtils.isNotEmpty(townDto.getCode())).collect(Collectors.toList()));
                        }
                    });

                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    city.setIsMonitorNum(city.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                    city.setIsTagCollectNum(city.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
                    //如果下级设备都为收藏，则上级设置为收藏
                    if (Objects.equals(city.getNum(),city.getIsMonitorNum())){
                        city.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                    }
                    if (Objects.equals(city.getNum(),city.getIsTagCollectNum())){
                        city.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                    }
                    //去掉异常区县
                    city.setList(city.getList().stream().filter(areaDto ->
                            StringUtils.isNotEmpty(areaDto.getCode())).collect(Collectors.toList()));
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                pro.setIsMonitorNum(pro.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                pro.setIsTagCollectNum(pro.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
                //如果下级设备都为收藏，则上级设置为收藏
                if (Objects.equals(pro.getNum(),pro.getIsMonitorNum())){
                    pro.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                }
                if (Objects.equals(pro.getNum(),pro.getIsTagCollectNum())){
                    pro.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                }
                //去掉异常地市
                pro.setList(pro.getList().stream().filter(prioDto ->
                        StringUtils.isNotEmpty(prioDto.getCode())).collect(Collectors.toList()));
            } else {
                pro.setTagCollect("0");
                pro.setOnlineNum(0);
                pro.setNum(0);
                pro.setIsMonitor("0");
            }
        });
        return treeList;
    }
}
