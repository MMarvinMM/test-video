package com.chinatower.device.repository.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.UavFlightStatusVO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.UavDeviceService;
import com.chinatower.device.repository.UavNestCombineService;
import com.chinatower.device.repository.UavNestDeviceService;
import com.chinatower.device.service.impl.UavDevice2ServiceImpl;
import com.chinatower.device.utils.DeviceCommonUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.service.SysAreaRootService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UavNestCombineServiceImpl implements UavNestCombineService {

    @Autowired
    private UavDeviceService uavDeviceService;

    @Autowired
    private UavNestDeviceService uavNestDeviceService;

    @Autowired
    private SysAreaRootService sysAreaRootService;

    @Autowired
    private DeviceCommonUtils deviceCommonUtils;

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private UavDevice2ServiceImpl uavDevice2Service;

    @Override
    public List<DeviceTreeVO> getUavMonitorAreaTreeWithNest(DeviceReqDTO param) {
        List<DeviceTreeVO> uavList = uavDeviceService.getUavMonitorAreaTreeWithoutFormat(param);
        // key -> deviceCode value -> flightStatus
        Map<String, String> deviceFlightStatusMap = Maps.newHashMap();

        List<String> uavNestCodeList = Lists.newArrayList();
        List<String> deviceCodeList = Lists.newArrayList();
        for (DeviceTreeVO deviceTreeVO : uavList) {
            deviceCodeList.add(deviceTreeVO.getDeviceCode());
            if (StringUtils.isNotEmpty(deviceTreeVO.getUavNestCode())) {
                uavNestCodeList.add(deviceTreeVO.getUavNestCode());
            }
        }

        if (BooleanUtils.isTrue(param.getWithFlightStatus())) {
            if (CollectionUtils.isNotEmpty(uavList)) {
                deviceFlightStatusMap = getDeviceFlightStatusMap(deviceCodeList);
            }
        }

        // 不查询无人机关联的机巢
        param.setNotIncludeNestCodeList(uavNestCodeList);

        List<DeviceTreeVO> uavNestList = uavNestDeviceService.getUavNestMonitorAreaTreeWithoutFormat(param);
        if (CollectionUtils.isNotEmpty(uavNestList)) {
            uavList.addAll(uavNestList);
        }

        // list转树
        List<DeviceTreeVO> uavTreeList = buildAreaTreeOptimized(uavList);
        log.info("getUavMonitorAreaTreeWithNest list 2 tree, param:{}, list:{}, tree:{}", JSON.toJSONString(param), JSON.toJSONString(uavList), JSON.toJSONString(uavTreeList));

        //格式化区域设备树
        formatTreeList(uavTreeList, null, deviceFlightStatusMap);
        // 补充中国根节点
        DeviceTreeVO root = new DeviceTreeVO();
        sysAreaRootService.createRootTreeVO(root);
        root.setList(uavTreeList);
        root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
        root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!CollectionUtils.isEmpty(uavTreeList)) {
            rootTree.add(root);
        }
        return rootTree;
    }

    @Override
    public List<IndustryDeviceTree> getDeviceSitesWithNest(SiteRequestDTO siteRequestDTO) {
        List<IndustryDeviceTree> uavList = uavDevice2Service.getDeviceSites(siteRequestDTO);
        // 无人机关联的机巢编码
        List<String> uavNestCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(uavList)) {
            for (IndustryDeviceTree orgInfo : uavList) {
                if (StringUtils.isNotEmpty(orgInfo.getList())) {
                    for (IndustryDeviceTree deviceInfo : orgInfo.getList()) {
                        if (StringUtils.isNotEmpty(deviceInfo.getUavNestCode())) {
                            uavNestCodeList.add(deviceInfo.getUavNestCode());
                        }
                    }
                }
            }
        }

        // 不查询无人机关联的机巢
        siteRequestDTO.setNotIncludeNestCodeList(uavNestCodeList);

        List<IndustryDeviceTree> uavNestList = uavNestDeviceService.queryUavNestSites(siteRequestDTO);
        if (CollectionUtils.isNotEmpty(uavNestList)) {
            uavList.addAll(uavNestList);
        }

        // 使用Map按siteCode分组
        Map<String, IndustryDeviceTree> siteMap = new LinkedHashMap<>();

        for (IndustryDeviceTree site : uavList) {
            String siteCode = site.getSiteCode();

            if (siteMap.containsKey(siteCode)) {
                // 如果已存在相同siteCode，合并list
                IndustryDeviceTree existingSite = siteMap.get(siteCode);
                existingSite.getList().addAll(site.getList());
            } else {
                // 如果不存在，添加到Map中
                siteMap.put(siteCode, site);
            }
        }
        // 将Map转换为List返回
        return Lists.newArrayList(siteMap.values());
    }

    @Override
    public List<DeviceSiteVO> getDeviceListBySiteCodeWithNest(SiteRequestDTO siteRequestDTO) {
        List<DeviceSiteVO> uavList = uavDevice2Service.getDeviceListBySiteCode(siteRequestDTO);
        List<String> uavNestCodeList = uavList.stream().map(DeviceSiteVO::getUavNestCode).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        // 不查询无人机关联的机巢
        siteRequestDTO.setNotIncludeNestCodeList(uavNestCodeList);
        List<DeviceSiteVO> uavNestList = uavNestDeviceService.queryUavNestDevicesBySiteCode(siteRequestDTO);
        if (CollectionUtils.isNotEmpty(uavNestList)) {
            uavList.addAll(uavNestList);
        }
        return uavList;
    }

    @Override
    public List<DeviceTreeVO> getUavMonitorOrgTreeWithNest(DeviceReqDTO param) {
        List<DeviceTreeVO> uavList = uavDeviceService.getUavMonitorOrgTreeWithoutFormat(param);
        // key -> deviceCode value -> flightStatus
        Map<String, String> deviceFlightStatusMap = Maps.newHashMap();

        List<String> uavNestCodeList = Lists.newArrayList();
        List<String> deviceCodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(uavList)) {
            uavList.forEach(dev -> {
                deviceCodeList.add(dev.getDeviceCode());
                if (StringUtils.isNotEmpty(dev.getUavNestCode())) {
                    uavNestCodeList.add(dev.getUavNestCode());
                }
            });
        }

        if (BooleanUtils.isTrue(param.getWithFlightStatus())) {
            if (CollectionUtils.isNotEmpty(uavList)) {
                deviceFlightStatusMap = getDeviceFlightStatusMap(deviceCodeList);
            }
        }

        // 不查询无人机关联的机巢
        param.setNotIncludeNestCodeList(uavNestCodeList);

        List<DeviceTreeVO> uavNestList = uavNestDeviceService.getUavNestMonitorOrgTreeWithoutFormat(param);

        if (CollectionUtils.isNotEmpty(uavNestList)) {
            uavList.addAll(uavNestList);
        }

        // list转树
        List<DeviceTreeVO> uavTreeList = buildOrgTreeOptimized(uavList);
        log.info("getUavMonitorOrgTreeWithNest list 2 tree, list:{}, tree:{}", JSON.toJSONString(uavList), JSON.toJSONString(uavTreeList));

        List<DeviceTreeVO> treeList = deviceInfoRepository.formatOrgTreeList4Device(uavTreeList, deviceFlightStatusMap);
        log.info("查询无人机组织树---返回报文------{}", JSON.toJSONString(treeList));

        if (!CollectionUtils.isEmpty(treeList)) {
            treeList.get(0).setFirst(1);
        }
        return treeList;
    }

    /**
     * 更高效的构建方法 - 使用Map缓存
     */
    public List<DeviceTreeVO> buildOrgTreeOptimized(List<DeviceTreeVO> areaList) {
        if (areaList == null || areaList.isEmpty()) {
            return new ArrayList<>();
        }

        List<DeviceTreeVO> dataList = Lists.newArrayList();

        // 创建所有节点Map
        Map<String, DeviceTreeVO> nodeMap = new HashMap<>();
        List<DeviceTreeVO> rootNodes = new ArrayList<>();

        // 第一遍：创建所有节点
        for (DeviceTreeVO data : areaList) {
            // 省
            if (!nodeMap.containsKey(data.getOrgId())) {
                DeviceTreeVO orgNode = new DeviceTreeVO();
                orgNode.setCode(data.getOrgId());
                orgNode.setName(data.getOrgName());
                orgNode.setLevel(data.getOrgLevel());
                orgNode.setParentCode(data.getParentOrgId());
                nodeMap.put(data.getOrgId(), orgNode);

                dataList.add(orgNode);
            }

            // 设备
            if (!nodeMap.containsKey(data.getDeviceCode())) {
                DeviceTreeVO deviceNode = new DeviceTreeVO();
                deviceNode.setParentCode(data.getOrgId());
                deviceNode.setCode(data.getDeviceCode());
                deviceNode.setName(data.getDeviceName());
                deviceNode.setDeviceCode(data.getDeviceCode());
                deviceNode.setDeviceName(data.getDeviceName());
                deviceNode.setUavNestCode(data.getUavNestCode());
                deviceNode.setUavNestName(data.getUavNestName());
                deviceNode.setUavNestStatus(data.getUavNestStatus());
                deviceNode.setUavNestPositionFix(data.getUavNestPositionFix());
                deviceNode.setUavNestCalibrationStatus(data.getUavNestCalibrationStatus());
                deviceNode.setLevel(data.getDevLevel());
                deviceNode.setSiteCode(data.getSiteCode());
                deviceNode.setStatus(data.getDeviceStatus());
                deviceNode.setIsMonitor(data.getIsMonitor());
                deviceNode.setLongitude(data.getLongitude());
                deviceNode.setLatitude(data.getLatitude());
                deviceNode.setAccessNode(data.getAccessNode());
                deviceNode.setIsNestDevice(data.getIsNestDevice());
                nodeMap.put(data.getDeviceCode(), deviceNode);

                dataList.add(deviceNode);
            }
        }

        // 第二遍：建立父子关系
        for (DeviceTreeVO area : dataList) {
            DeviceTreeVO currentNode = nodeMap.get(area.getCode());

            if (StringUtils.equals(area.getLevel(), "1")) {
                // 省级节点作为根节点
                rootNodes.add(currentNode);
            } else {
                // 查找父节点
                DeviceTreeVO parentNode = nodeMap.get(area.getParentCode());
                if (parentNode != null) {
                    parentNode.getList().add(currentNode);
                }
            }
        }

        return rootNodes;
    }

    public List<DeviceTreeVO> buildAreaTreeOptimized(List<DeviceTreeVO> areaList) {
        if (areaList == null || areaList.isEmpty()) {
            return new ArrayList<>();
        }

        List<DeviceTreeVO> dataList = Lists.newArrayList();

        // 创建所有节点Map
        Map<String, DeviceTreeVO> nodeMap = new HashMap<>();
        List<DeviceTreeVO> rootNodes = new ArrayList<>();

        // 第一遍：创建所有节点
        for (DeviceTreeVO data : areaList) {
            // 省
            if (!nodeMap.containsKey(data.getProvinceId())) {
                DeviceTreeVO provinceNode = new DeviceTreeVO();
                provinceNode.setCode(data.getProvinceId());
                provinceNode.setName(data.getProvinceName());
                provinceNode.setLevel(data.getProLevel());
                nodeMap.put(data.getProvinceId(), provinceNode);

                dataList.add(provinceNode);
            }

            // 市
            if (!nodeMap.containsKey(data.getCityId())) {
                DeviceTreeVO cityNode = new DeviceTreeVO();
                cityNode.setParentCode(data.getProvinceId());
                cityNode.setCode(data.getCityId());
                cityNode.setName(data.getCityName());
                cityNode.setLevel(data.getCityLevel());
                nodeMap.put(data.getCityId(), cityNode);

                dataList.add(cityNode);
            }

            // 区
            if (!nodeMap.containsKey(data.getCountyId())) {
                DeviceTreeVO countyNode = new DeviceTreeVO();
                countyNode.setParentCode(data.getCityId());
                countyNode.setCode(data.getCountyId());
                countyNode.setName(data.getCountyName());
                countyNode.setLevel(data.getCountryLevel());
                nodeMap.put(data.getCountyId(), countyNode);

                dataList.add(countyNode);
            }

            // 设备
            if (!nodeMap.containsKey(data.getDeviceCode())) {
                DeviceTreeVO deviceNode = new DeviceTreeVO();
                deviceNode.setParentCode(data.getCountyId());
                deviceNode.setCode(data.getDeviceCode());
                deviceNode.setName(data.getDeviceName());
                deviceNode.setDeviceCode(data.getDeviceCode());
                deviceNode.setDeviceName(data.getDeviceName());
                deviceNode.setUavNestCode(data.getUavNestCode());
                deviceNode.setUavNestName(data.getUavNestName());
                deviceNode.setUavNestStatus(data.getUavNestStatus());
                deviceNode.setUavNestPositionFix(data.getUavNestPositionFix());
                deviceNode.setUavNestCalibrationStatus(data.getUavNestCalibrationStatus());
                deviceNode.setAltitude(data.getAltitude());
                deviceNode.setDeviceAlias(data.getDeviceAlias());
                deviceNode.setDeviceModelCode(data.getDeviceModelCode());
                deviceNode.setLevel(data.getDevLevel());
                deviceNode.setTownCode(data.getTownCode());
                deviceNode.setTownName(data.getTownName());
                deviceNode.setSiteCode(data.getSiteCode());
                deviceNode.setStatus(data.getDeviceStatus());
                deviceNode.setIsMonitor(data.getIsMonitor());
                deviceNode.setTagCollect(data.getTagCollect());
                deviceNode.setLongitude(data.getLongitude());
                deviceNode.setLatitude(data.getLatitude());
                deviceNode.setAccessNode(data.getAccessNode());
                deviceNode.setIsNestDevice(data.getIsNestDevice());
                nodeMap.put(data.getDeviceCode(), deviceNode);

                dataList.add(deviceNode);
            }
        }

        // 第二遍：建立父子关系
        for (DeviceTreeVO area : dataList) {
            DeviceTreeVO currentNode = nodeMap.get(area.getCode());

            if (StringUtils.equals(area.getLevel(), "1")) {
                // 省级节点作为根节点
                rootNodes.add(currentNode);
            } else {
                // 查找父节点
                DeviceTreeVO parentNode = nodeMap.get(area.getParentCode());
                if (parentNode != null) {
                    parentNode.getList().add(currentNode);
                }
            }
        }

        return rootNodes;
    }

    private Map<String, String> getDeviceFlightStatusMap(List<String> deviceCodeList) {
        Map<String, String> deviceFlightStatusMap = Maps.newHashMap();
        try {
            if (CollectionUtils.isEmpty(deviceCodeList)) {
                return deviceFlightStatusMap;
            }

            List<UavFlightStatusVO> uavFlightStatusList = deviceCommonUtils.getUavFlightStatus(deviceCodeList);
            log.info("getDeviceFlightStatusMap UavDeviceServiceImpl param:{}, result:{}", JSON.toJSONString(deviceCodeList), JSON.toJSONString(uavFlightStatusList));

            if (CollectionUtils.isEmpty(uavFlightStatusList)) {
                return deviceFlightStatusMap;
            }
            deviceFlightStatusMap = uavFlightStatusList.stream().collect(Collectors.toMap(UavFlightStatusVO::getDeviceCode, UavFlightStatusVO::getFlightStatus, (a, b) -> a));
        } catch (Exception e) {
            log.error("UavDeviceServiceImpl getDeviceFlightStatusMap error:", e);
        }
        return deviceFlightStatusMap;
    }

    public List<DeviceTreeVO> formatTreeList(List<DeviceTreeVO> treeList, Integer deviceOrChannel,
                                             Map<String, String> deviceFlightStatusMap) {
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info(startMillis + "formatTreeList 开始时间" + startMillis);
        Set<String> fileUrls = new HashSet<>();
        treeList.forEach(pro -> {
            pro.getList().forEach(city -> {
                city.getList().forEach(country -> {
                    country.getList().forEach(dev -> {
                        fileUrls.add(dev.getImageUrl());
                    });
                });
            });
        });
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        log.info("treeList 遍历结束,耗时" + (startMillis2 - startMillis) + "毫秒！");
        //加密
        Map<String, String> result;
        if (!fileUrls.isEmpty()) {
            result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        } else {
            result = Maps.newHashMap();
        }
        long startMillis3 = Calendar.getInstance().getTimeInMillis();
        log.info(IndustryConstants.DEAL_OBS_TIME + (startMillis3 - startMillis2) + "毫秒！");
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        List<DeviceTreeVO> townList = new ArrayList<>(country.getList().size());
                        List<DeviceTreeVO> countryListNew = new ArrayList<DeviceTreeVO>();
                        for (int i = 0; i <= country.getList().size() - 1; i++) {
                            DeviceTreeVO dev = country.getList().get(i);
                            if (!org.apache.commons.lang3.StringUtils.isEmpty(dev.getImageUrl())) {
                                dev.setImageUrl(result.get(dev.getImageUrl()));
                            }
                            // 根据摄像机状态设置在线数和总数
                            if (StringUtils.isNotEmpty(dev.getUavNestStatus())) {
                                dev.setOnlineNum(Objects.equals(Constants.ZERO, dev.getUavNestStatus()) ? 1 : 0);
                            } else {
                                dev.setOnlineNum(Objects.equals(Constants.ZERO, dev.getStatus()) ? 1 : 0);
                            }
                            dev.setNum(1);
                            dev.setVideoNum(1);

                            // 20211208 设备、通道别名处理，有乡镇时候构建乡镇信息
                            if (!org.apache.commons.lang3.StringUtils.isEmpty(dev.getDeviceAlias())) {
                                dev.setName(dev.getDeviceAlias());
                            }
                            if (dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                dev.setIsMonitorNum(1);
                            }
                            if (dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                dev.setIsTagCollectNum(1);
                            }

                            if (!org.apache.commons.lang3.StringUtils.isEmpty(dev.getTownName())) {
                                DeviceTreeVO town = townList.stream().filter(item -> item.getTownCode().equals(dev.getTownCode())).findAny().orElse(null);
                                if (town == null) {
                                    town = new DeviceTreeVO();
                                    town.setCode(dev.getTownCode());
                                    town.setTownCode(dev.getTownCode());
                                    town.setName(dev.getTownName());
                                    town.setLevel("6");
                                    town.setTownName(dev.getTownName());
                                    town.setList(new ArrayList<>(country.getList().size()));
                                    town.setIsMonitor(dev.getIsMonitor());
                                    town.setTagCollect(dev.getTagCollect());
                                    town.setIsMonitorNum(dev.getIsMonitorNum());
                                    town.setIsTagCollectNum(dev.getIsTagCollectNum());
                                    townList.add(town);
                                } else {
                                    if (dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                        town.setIsMonitorNum(town.getIsMonitorNum() + dev.getIsMonitorNum());
                                    }
                                    if (!dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION)) &&
                                            town.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                        town.setIsMonitor(String.valueOf(IndustryConstants.NO_COLLECTION));
                                    }

                                    if (dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                        town.setIsTagCollectNum(town.getIsTagCollectNum() + dev.getIsTagCollectNum());
                                    }
                                    if (!dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION)) &&
                                            town.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                        town.setTagCollect(String.valueOf(IndustryConstants.NO_COLLECTION));
                                    }
                                }
                                town.getList().add(dev);
                            } else {
                                countryListNew.add(dev);
                            }
                            dev.setFlyStatus(deviceFlightStatusMap.get(dev.getDeviceCode()));
                        }
                        country.setList(countryListNew);
                        if (!townList.isEmpty()) {
                            for (DeviceTreeVO town : townList) {
                                town.setOnlineNum(town.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                                town.setNum(town.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                                town.setVideoNum(town.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                            }
                            country.getList().addAll(townList);
                        }
                        country.setOnlineNum(country.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                        country.setNum(country.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                        country.setVideoNum(country.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                        //子节点都收藏标记上级节点为收藏
                        int monitorNum = country.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                        country.setIsMonitorNum(monitorNum);
                        country.setIsTagCollectNum(country.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
                        if (monitorNum == country.getNum()) {
                            country.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                        }
                        if (country.getIsTagCollectNum() == country.getNum()) {
                            country.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                        }
                    });
                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    city.setVideoNum(city.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                    int monitorNum = city.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                    //子节点都收藏标记上级节点为收藏
                    city.setIsMonitorNum(monitorNum);
                    city.setIsTagCollectNum(city.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
                    if (city.getIsMonitorNum() == city.getNum()) {
                        city.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                    }
                    if (city.getIsTagCollectNum() == city.getNum()) {
                        city.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                    }
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                pro.setVideoNum(pro.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                int monitorNum = pro.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                //子节点都收藏标记上级节点为收藏
                pro.setIsMonitorNum(monitorNum);
                pro.setIsTagCollectNum(pro.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
                if (pro.getIsMonitorNum() == pro.getNum()) {
                    pro.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                }
                if (pro.getIsTagCollectNum() == pro.getNum()) {
                    pro.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                }
            } else {
                pro.setOnlineNum(0);
                pro.setNum(0);
                pro.setIsMonitor("0");
                pro.setTagCollect("0");
            }
        });
        long startMillis4 = Calendar.getInstance().getTimeInMillis();
        log.info(IndustryConstants.DEAL_OBS_TIME + (startMillis4 - startMillis3) + "毫秒！");
        return treeList;
    }
}
