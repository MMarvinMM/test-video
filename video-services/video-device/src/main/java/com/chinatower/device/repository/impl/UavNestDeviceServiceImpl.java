package com.chinatower.device.repository.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dto.UavNestDeviceInfoDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.DeviceCameraModelVO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.UavDeviceQueryVO;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.mapper.UavNestDeviceMapper;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.UavNestDeviceService;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UavNestDeviceServiceImpl implements UavNestDeviceService {

    @Resource
    private UavNestDeviceMapper uavNestDeviceMapper;

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private SysAreaRootService sysAreaRootService;

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;

    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "15")
    public List<DeviceDistanceVO> queryUavNestDeviceDistanceInfo(DeviceDistanceDTO param) {
        if (param == null
                || StringUtils.isEmpty(param.getLongitude())
                || StringUtils.isEmpty(param.getLatitude())
                || StringUtils.isEmpty(param.getRadius())) {
            throw new CustomException("经纬度、探索半径参数不能为空");
        }

        return uavNestDeviceMapper.queryUavNestDeviceDistanceInfo(param);
    }

    @DataScope(tenantColumn = "t1.tenant_id", deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node", alarmSourceVal = "15")
    @Override
    public List<DeviceSiteVO> queryUavNestDevicesBySiteCode(SiteRequestDTO siteRequestDTO) {

        if (!Objects.equals(siteRequestDTO.getQueryFlag(), Constants.ONE_NUM)) {
            int pageNum = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageNum()), 0);
            int pageSize = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageSize()), 0);
            if (0 != pageNum && 0 != pageSize) {
                PageHelper.startPage(pageNum, pageSize);
            }
        }
        return uavNestDeviceMapper.queryUavNestDevicesBySiteCode(siteRequestDTO);
    }

    @DataScope(tenantColumn = "t1.tenant_id", deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node")
    @Override
    public List<IndustryDeviceTree> queryUavNestSites(SiteRequestDTO siteRequestDTO) {
        return uavNestDeviceMapper.queryUavNestSites(siteRequestDTO);
    }

    @Override
    public DeviceCommonInfoVO qryByDeviceCode(String deviceCode) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        String userId = user.getUserId();
        log.info("qryByDeviceCode tenantId:{} industryCode:{}, userId:{}, deviceCode:{}", tenantId, industryCode, userId, deviceCode);
        return uavNestDeviceMapper.qryByDeviceCode(deviceCode, tenantId, industryCode, userId);
    }

    @ApiOperation("无人机机巢区域设备树查询")
    @Override
    @DataScope(deviceColumn = "m.device_code", tenantColumn = "ord.tenant_id", alarmSourceVal = "15",
            areaPro = "m.province_code", areaCity = "m.city_code", industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    public List<DeviceTreeVO> getUavNestMonitorAreaTreeWithoutFormat(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
            deviceReqDTO.setTenantId(tokenUser.getTenantId());
        }

        log.info("getUavNestMonitorAreaTreeWithoutFormat param{}", JSON.toJSONString(deviceReqDTO));
        return uavNestDeviceMapper.getUavNestMonitorAreaTreeWithoutFormat(deviceReqDTO);
    }

    @ApiOperation("无人机机巢区域设备树查询")
    @Override
    @DataScope(deviceColumn = "m.device_code", tenantColumn = "ord.tenant_id", alarmSourceVal = "15",
            areaPro = "m.province_code", areaCity = "m.city_code", industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    public List<DeviceTreeVO> getUavNestMonitorAreaTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
            deviceReqDTO.setTenantId(tokenUser.getTenantId());
        }

        log.info("getUavNestMonitorAreaTree param{}", JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> treeList = uavNestDeviceMapper.getUavNestMonitorAreaTree(deviceReqDTO);

        //格式化区域设备树
        formatTreeList(treeList);
        // 补充中国根节点
        DeviceTreeVO root = new DeviceTreeVO();
        sysAreaRootService.createRootTreeVO(root);
        root.setList(treeList);
        root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
        root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!CollectionUtils.isEmpty(treeList)) {
            rootTree.add(root);
        }
        return rootTree;
    }


    public List<DeviceTreeVO> formatTreeList(List<DeviceTreeVO> treeList) {
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        List<DeviceTreeVO> townList = new ArrayList<>(country.getList().size());
                        List<DeviceTreeVO> countryListNew = new ArrayList<DeviceTreeVO>();
                        for (int i = 0; i <= country.getList().size() - 1; i++) {
                            DeviceTreeVO dev = country.getList().get(i);
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
//                            if (dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
//                                dev.setIsMonitorNum(1);
//                            }
//                            if (dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
//                                dev.setIsTagCollectNum(1);
//                            }

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
//                                    town.setIsMonitor(dev.getIsMonitor());
//                                    town.setTagCollect(dev.getTagCollect());
//                                    town.setIsMonitorNum(dev.getIsMonitorNum());
//                                    town.setIsTagCollectNum(dev.getIsTagCollectNum());
                                    townList.add(town);
                                } else {
//                                    if (dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
//                                        town.setIsMonitorNum(town.getIsMonitorNum() + dev.getIsMonitorNum());
//                                    }
//                                    if (!dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION)) &&
//                                            town.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
//                                        town.setIsMonitor(String.valueOf(IndustryConstants.NO_COLLECTION));
//                                    }
//
//                                    if (dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
//                                        town.setIsTagCollectNum(town.getIsTagCollectNum() + dev.getIsTagCollectNum());
//                                    }
//                                    if (!dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION)) &&
//                                            town.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
//                                        town.setTagCollect(String.valueOf(IndustryConstants.NO_COLLECTION));
//                                    }
                                }
                                town.getList().add(dev);
                            } else {
                                countryListNew.add(dev);
                            }
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
//                        int monitorNum = country.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
//                        country.setIsMonitorNum(monitorNum);
//                        country.setIsTagCollectNum(country.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
//                        if (monitorNum == country.getNum()) {
//                            country.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
//                        }
//                        if (country.getIsTagCollectNum() == country.getNum()) {
//                            country.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
//                        }
                    });
                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    city.setVideoNum(city.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                    int monitorNum = city.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                    //子节点都收藏标记上级节点为收藏
//                    city.setIsMonitorNum(monitorNum);
//                    city.setIsTagCollectNum(city.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
//                    if (city.getIsMonitorNum() == city.getNum()) {
//                        city.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
//                    }
//                    if (city.getIsTagCollectNum() == city.getNum()) {
//                        city.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
//                    }
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                pro.setVideoNum(pro.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
//                int monitorNum = pro.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                //子节点都收藏标记上级节点为收藏
//                pro.setIsMonitorNum(monitorNum);
//                pro.setIsTagCollectNum(pro.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum());
//                if (pro.getIsMonitorNum() == pro.getNum()) {
//                    pro.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
//                }
//                if (pro.getIsTagCollectNum() == pro.getNum()) {
//                    pro.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
//                }
            } else {
                pro.setOnlineNum(0);
                pro.setNum(0);
//                pro.setIsMonitor("0");
//                pro.setTagCollect("0");
            }
        });
        return treeList;
    }


    @Override
    @DataScope(tenantColumn = "gg.tenant_id", areaPro = "gg.province_id", areaCity = "gg.city_id", deptColumn = "gg.dept_id",
            industryCode = "gg.industry_code", acceptColumn = "vod.accept_node", deviceColumn = "dd.device_code")
    public List<DeviceTreeVO> getUavNestMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }

        log.info("查询无人机组织树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        //组织与绑定的设备
        List<DeviceTreeVO> monitorList = uavNestDeviceMapper.getUavNestMonitorOrgTree(deviceReqDTO);

        List<DeviceTreeVO> treeList = deviceInfoRepository.formatOrgTreeList4Device(monitorList, null);
        log.info("查询无人机组织树---返回报文------{}", JSON.toJSONString(treeList));
        return treeList;

    }

    @Override
    @DataScope(tenantColumn = "gg.tenant_id", areaPro = "gg.province_id", areaCity = "gg.city_id", deptColumn = "gg.dept_id",
            industryCode = "gg.industry_code", acceptColumn = "vod.accept_node", deviceColumn = "dd.device_code", alarmSourceVal = "15")
    public List<DeviceTreeVO> getUavNestMonitorOrgTreeWithoutFormat(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        log.info("getUavNestMonitorOrgTreeWithoutFormat 参数：{}", JSON.toJSONString(deviceReqDTO));
        //组织与绑定的设备
        return uavNestDeviceMapper.getUavNestMonitorOrgTreeWithoutFormat(deviceReqDTO);
    }
}
