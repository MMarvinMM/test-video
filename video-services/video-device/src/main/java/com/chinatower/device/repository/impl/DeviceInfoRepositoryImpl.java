package com.chinatower.device.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.dto.SiteInfoDto;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.DeviceInfoDO;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.mapper.SnapShotSimpleMapper;
import com.chinatower.device.mapper.TenantDeviceAttachmentMapper;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.service.IVideoChannelInfoService;
import com.chinatower.device.service.IVideoDeviceInfoService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmOrderParmaDto;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.device.ChannelVideoPlanVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.QueryStorageDayVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceAttachmentVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.VideoControlAPI;
import com.chinatower.video.api.VideoQueryAPI;
import com.chinatower.video.api.entity.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
@Slf4j
@Service
public class DeviceInfoRepositoryImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfoDO> implements IDeviceInfoRepository {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private IVideoDeviceInfoService videoDeviceInfoService;

    @Autowired
    private IVideoChannelInfoService videoChannelInfoService;
    @Resource
    private SystemFeignClient systemFeignClient;

    @Autowired
    private VideoQueryAPI videoQueryAPI;

    @Autowired
    private VideoControlAPI videoControlAPI;

    @Resource
    private TenantDeviceAttachmentMapper tenantDeviceAttachmentMapper;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private SnapShotSimpleMapper snapShotSimpleMapper;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void updateDeviceInfoBySiteInfo(SiteInfoDto siteInfoDto) {
        if (StringUtils.isBlank(siteInfoDto.getSiteCode())) {
            throw new CustomException(Constants.SITE_CODE_IS_EMPTY);
        }
        DeviceInfoDO uptDeviceInfo = new DeviceInfoDO();
        uptDeviceInfo.setLocation(siteInfoDto.getLocation());
        uptDeviceInfo.setLongitude(String.valueOf(siteInfoDto.getLongitude()));
        uptDeviceInfo.setLatitude(String.valueOf(siteInfoDto.getLatitude()));
        uptDeviceInfo.setSiteName(siteInfoDto.getSiteName());
        uptDeviceInfo.setAreaCode(siteInfoDto.getAreaCode());
        uptDeviceInfo.setAdmCode(siteInfoDto.getAdmCode());
        uptDeviceInfo.setOrgCode(siteInfoDto.getOrgCode());
        LambdaUpdateWrapper<DeviceInfoDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DeviceInfoDO::getSiteCode, siteInfoDto.getSiteCode()).eq(DeviceInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        updateWrapper.set(DeviceInfoDO::getModifierDate, new Date());
        this.baseMapper.update(uptDeviceInfo, updateWrapper);
    }

    @DataScope(deviceColumn="dataTable.device_code", tenantColumn="dataTable.tenant_id",alarmSourceVal = "4",
            areaPro="dataTable.province_code",areaCity="dataTable.city_code", industryCode="dataTable.industry_code",acceptColumn="dataTable.accept_node")
    @Override
    public List<DeviceTreeVO> getMonitorTree(DeviceReqDTO deviceReqDTO) {
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info(startMillis + "getMonitorTree 开始时间"+ startMillis);
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = baseMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        log.info("getMonitorTree sql入参的deviceReqDTO"+ JSONObject.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> treeList = baseMapper.getMonitorTree(deviceReqDTO);
        log.info("getMonitorTree sql返回的treeList"+ JSONObject.toJSONString(treeList));
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        log.info("getMonitorTree sql结束,耗时"+ (startMillis2 - startMillis) +"毫秒！");
        //格式化区域树
        this.formatTreeList(treeList, deviceReqDTO.getDeviceOrChannel());
        log.info("getMonitorTree 格式化区域树结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis2) +"毫秒！");
        return treeList;
    }

    /**
     * 格式化区域设备树
     * @param treeList
     * @return
     */
    public List<DeviceTreeVO> formatTreeList(List<DeviceTreeVO> treeList, Integer deviceOrChannel) {
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info(startMillis + "formatTreeList 开始时间"+ startMillis);
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
        log.info("treeList 遍历结束,耗时"+ (startMillis2 - startMillis) +"毫秒！");
        //加密
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        long startMillis3 = Calendar.getInstance().getTimeInMillis();
        log.info(IndustryConstants.DEAL_OBS_TIME+ (startMillis3 - startMillis2) +"毫秒！");
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        List<DeviceTreeVO> townList = new ArrayList<>(country.getList().size());
                        List<DeviceTreeVO> countryListNew = new ArrayList<DeviceTreeVO>();
                        for (int i = 0; i <= country.getList().size() - 1; i++) {
//                        for (int i = country.getList().size() - 1; i >= 0; i--) {
                            DeviceTreeVO dev = country.getList().get(i);
                            if (!StringUtils.isEmpty(dev.getImageUrl())) {
                                dev.setImageUrl(result.get(dev.getImageUrl()));
                            }
                            // 统计摄像机的在线数，而不是通道的在线数--2024-2-27添加
//                            log.info("dev.getCountyName{}",dev.getCountyName());
                            if (Objects.nonNull(deviceOrChannel) && deviceOrChannel.intValue() == Constants.ONE_NUM) {
                                Map<String,List<DeviceTreeVO>> listMap = dev.getList().stream().collect(Collectors.groupingBy(deviceTreeVO -> deviceTreeVO.getDeviceCode()));
                                AtomicReference<Integer> onlineNum = new AtomicReference<>(0);
                                if(Objects.equals(Constants.ZERO,dev.getStatus())){
                                    onlineNum.getAndSet(onlineNum.get() + 1);
                                }
                                dev.setOnlineNum(onlineNum.get());
                                dev.setNum(listMap.keySet().size());
                                dev.setVideoNum((int) dev.getList().stream().map(DeviceTreeVO::getDeviceCode).distinct().count());
                            } else {
                                // 根据摄像机状态设置在线数和总数
                                dev.setOnlineNum((int) dev.getList().stream().filter(deviceTreeVO -> Objects.equals(Constants.ZERO, deviceTreeVO.getStatus())).count());
                                dev.setNum(dev.getList().size());
                                dev.setVideoNum((int) dev.getList().stream().map(DeviceTreeVO::getDeviceCode).distinct().count());
                            }


                            // 20211208 设备、通道别名处理，有乡镇时候构建乡镇信息
                            if (!StringUtils.isEmpty(dev.getDeviceAlias())) {
                                dev.setName(dev.getDeviceAlias());
                            }
                            dev.getList().forEach(channel -> {
                                if (!StringUtils.isEmpty(channel.getChannelAlias())) {
                                    channel.setName(channel.getChannelAlias());
                                }
                                if (channel.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                    channel.setIsMonitorNum(1);
                                }
                                if (channel.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                    channel.setIsTagCollectNum(1);
                                }
                            });
                            int monitorNum = dev.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                            int tagCollectNum = dev.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum();
                            dev.setIsMonitorNum(monitorNum);
                            dev.setIsTagCollectNum(tagCollectNum);
                            //子节点都收藏标记上级节点为收藏
                            if (Objects.nonNull(deviceOrChannel) && deviceOrChannel.intValue() == Constants.ONE_NUM){
                                if (dev.getList().size() == monitorNum) {
                                    dev.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                                }
                            }else{
                                if (dev.getNum() == monitorNum) {
                                    dev.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                                }
                            }

                            if (Objects.nonNull(deviceOrChannel) && deviceOrChannel.intValue() == Constants.ONE_NUM){
                                if (dev.getList().size() == tagCollectNum) {
                                    dev.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                                }
                            }else{
                                if (dev.getNum() == tagCollectNum) {
                                    dev.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                                }
                            }

                            if (!StringUtils.isEmpty(dev.getTownName())) {
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
                                }else{
                                    if (dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                        town.setIsMonitorNum(town.getIsMonitorNum() + dev.getIsMonitorNum());
                                    }
                                    if (dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                        town.setIsTagCollectNum(town.getIsTagCollectNum() + dev.getIsTagCollectNum());
                                    }
                                    if(!dev.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION)) &&
                                            town.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))){
                                        town.setIsMonitor(String.valueOf(IndustryConstants.NO_COLLECTION));
                                    }
                                    if(!dev.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION)) &&
                                            town.getTagCollect().equals(String.valueOf(IndustryConstants.COLLECTION))){
                                        town.setTagCollect(String.valueOf(IndustryConstants.NO_COLLECTION));
                                    }
                                }
                                town.getList().add(dev);
//                                country.getList().remove(i);
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
                        int monitorNum = country.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                        int tagCollectNum = country.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum();
                        country.setIsMonitorNum(monitorNum);
                        country.setIsTagCollectNum(tagCollectNum);
                        if (monitorNum == country.getNum()) {
                            country.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                        }
                        if (tagCollectNum == country.getNum()) {
                            country.setTagCollect(String.valueOf(IndustryConstants.COLLECTION));
                        }
                    });
                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    city.setVideoNum(city.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                    int monitorNum = city.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                    int tagCollectNum = city.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum();
                    //子节点都收藏标记上级节点为收藏
                    city.setIsMonitorNum(monitorNum);
                    city.setIsTagCollectNum(tagCollectNum);
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
                int tagCollectNum = pro.getList().stream().mapToInt(DeviceTreeVO::getIsTagCollectNum).sum();
                //子节点都收藏标记上级节点为收藏
                pro.setIsMonitorNum(monitorNum);
                pro.setIsTagCollectNum(tagCollectNum);
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
        log.info(IndustryConstants.DEAL_OBS_TIME+ (startMillis4 - startMillis3) +"毫秒！");
        return treeList;
    }

    /**
     * 保存上传的附件（图片/视频）
     * @param hornDeviceDTO
     */
    @Override
    public void saveDeviceAttachmentInfo(HornDeviceDTO hornDeviceDTO) {
        try {
            String tenantId = hornDeviceDTO.getTenantId();
            String industryCode = hornDeviceDTO.getIndustryCode();
            String deviceCode = hornDeviceDTO.getDeviceCode();
            //设备类型：1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达
            int deviceType = hornDeviceDTO.getDeviceType();
            tenantDeviceAttachmentMapper.deleteByDeviceCode(tenantId, industryCode, deviceCode, deviceType);
            if (!CollectionUtils.isEmpty(hornDeviceDTO.getFileUrlList())) {
                tenantDeviceAttachmentMapper.batchInsert(hornDeviceDTO.getFileUrlList().stream().map(x -> {
                    TenantDeviceAttachmentVO attachmentVO = new TenantDeviceAttachmentVO();
                    attachmentVO.setDeviceCode(deviceCode);
                    attachmentVO.setDeviceType(deviceType);
                    attachmentVO.setFileUrl(x);
                    attachmentVO.setTenantId(tenantId);
                    attachmentVO.setIndustryCode(industryCode);
                    attachmentVO.setAppVerCode(hornDeviceDTO.getAppVerCode());
                    attachmentVO.setCreator(hornDeviceDTO.getCreator());
                    attachmentVO.setCreateTime(new Date());
                    return attachmentVO;
                }).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("IHornDeviceRepositoryImpl saveDeviceAttachmentInfo error, e:", e);
        }
    }

    @Override
    public DeviceInfoDO getDeviceByCode(String deviceCode) {
        DeviceInfoDO deviceInfoDO = null;
        if (StringUtils.isNotBlank(deviceCode)) {
            LambdaQueryWrapper<DeviceInfoDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeviceInfoDO::getDeviceCode, deviceCode).eq(DeviceInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
            List<DeviceInfoDO> deviceInfoDOs = this.baseMapper.selectList(queryWrapper);
            if (!deviceInfoDOs.isEmpty()) {
                deviceInfoDO = deviceInfoDOs.get(0);
            }
        }
        return deviceInfoDO;
    }

    @DataScope(tenantColumn="gg.tenant_id",areaPro="gg.province_id",areaCity="gg.city_id",deptColumn="gg.dept_id",
            industryCode="gg.industry_code",acceptColumn="vod.accept_node", deviceColumn = "dd.device_code")
    @Override
    public List<DeviceTreeVO> getMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info("getMonitorOrgTree开始时间"+ startMillis);
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = baseMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        log.info("查询摄像头组织树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        //组织与绑定的设备
        List<DeviceTreeVO> monitorList = baseMapper.getMonitorOrgTree(deviceReqDTO);
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        log.info("getMonitorOrgTreesql结束,耗时"+ (startMillis2 - startMillis) +"毫秒！");
        List<DeviceTreeVO> treeList = this.formatOrgTreeList(monitorList);
        log.info("查询摄像头组织树---返回报文------{}", JSON.toJSONString(treeList));
        log.info("getMonitorOrgTree格式化组织树结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis2) +"毫秒！");
        return treeList;

    }

    /**
     * 格式化设备组织树
     * @param monitorList
     * @return
     */
    @Override
    public List<DeviceTreeVO> formatOrgTreeList(List<DeviceTreeVO> monitorList) {
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info("formatOrgTreeList开始时间"+ startMillis);
        Set<String> fileUrls = new HashSet<>();
        monitorList.forEach(org -> org.getList().forEach(dev -> {
            fileUrls.add(dev.getImageUrl());
        }));
        //加密
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        log.info(IndustryConstants.DEAL_OBS_TIME+ (startMillis2 - startMillis) +"毫秒！");
        List<DeviceTreeVO> treeList;
        Set<String> orgList = new HashSet<>();
        //获取有设备权限的所有组织编码
        monitorList.forEach(e -> orgList.add(e.getCode()));
        // 设置数量
        monitorList.forEach(org -> org.getList().forEach(dev -> {
            if (!StringUtils.isEmpty(dev.getImageUrl())) {
                dev.setImageUrl(result.get(dev.getImageUrl()));
            }
            dev.setOnlineNum((int) dev.getList().stream().filter(deviceTreeVO -> Objects.equals(deviceTreeVO.getStatus(), Constants.CHANNEL_ONLINE)).count());
            dev.setNum(dev.getList().size());
            if (!CollectionUtils.isEmpty(org.getList())) {
                dev.getList().forEach(chan -> {
                    DeviceTreeVO monVO = new DeviceTreeVO();
                    monVO.setChannelCode(chan.getChannelCode());
                    monVO.setStatus(chan.getStatus());
                    if (chan.getIsMonitor().equals(IndustryConstants.COLLECTION.toString())) {
                        chan.setIsMonitorNum(1);
                    }
                });
            }
            int monitorNum = dev.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
            dev.setIsMonitorNum(monitorNum);
            if (monitorNum == dev.getNum()) {
                dev.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
            }
        }));
        //查询有设备权限组织的上级组织直至更结点
        Set<String> orgIdList = getParentsList(orgList);
        //去重
        Set<String> orgResList = orgIdList.stream().filter(org -> !orgList.contains(org)).collect(Collectors.toSet());
        List<DeviceTreeVO> parOrgList;
        if (!CollectionUtils.isEmpty(orgResList)) {
            DeviceReqDTO reqDTO = new DeviceReqDTO();
            reqDTO.setOrgIdList(orgResList);
            //父节点直至根节点集合
            parOrgList = baseMapper.getCustOrganInfo(reqDTO);
            monitorList.addAll(parOrgList);
        }

        //按照层级正序排列
        List<DeviceTreeVO> sortList = monitorList.stream().sorted(Comparator.comparing(DeviceTreeVO::getLevel)).collect(Collectors.toList());
        //递归更新数量
        List<DeviceTreeVO> res = getDevNumList(sortList, monitorList);

        //列表转换为树形结构
        //获取父节点，0表示父节点
        treeList = res.stream().filter(e -> e.getLevel().equals(DeviceConstants.ONLINE_STATUS_ONE)).map(e -> {
            if (!CollectionUtils.isEmpty(e.getList())) {
                e.getList().addAll(getChildrens(e, sortList));
            } else {
                e.setList(getChildrens(e, sortList));
            }
            return e;
        }).collect(Collectors.toList());
        long startMillis3 = Calendar.getInstance().getTimeInMillis();
        log.info("遍历OrgTreeList,耗时"+ (startMillis3 - startMillis2) +"毫秒！");
        return treeList;
    }

    @Override
    public List<DeviceTreeVO> formatOrgTreeList4Device(List<DeviceTreeVO> monitorList, Map<String, String> deviceFlightStatusMap) {
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info("formatOrgTreeList开始时间:{}", startMillis);
        Set<String> fileUrls = new HashSet<>();
        monitorList.forEach(org -> org.getList().forEach(dev -> {
            fileUrls.add(dev.getImageUrl());
        }));
        //加密
        Map<String, String> result;
        if (!fileUrls.isEmpty()) {
            result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        } else {
            result = Maps.newHashMap();
        }
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        log.info(IndustryConstants.DEAL_OBS_TIME+ (startMillis2 - startMillis) +"毫秒！");
        List<DeviceTreeVO> treeList;
        Set<String> orgList = new HashSet<>();
        //获取有设备权限的所有组织编码
        monitorList.forEach(e -> orgList.add(e.getCode()));
        // 设置数量
        monitorList.forEach(org -> org.getList().forEach(dev -> {
            if (!StringUtils.isEmpty(dev.getImageUrl())) {
                dev.setImageUrl(result.get(dev.getImageUrl()));
            }
            if (StringUtils.isNotEmpty(dev.getUavNestStatus())) {
                dev.setOnlineNum(Objects.equals(dev.getUavNestStatus(), Constants.CHANNEL_ONLINE) ? 1 : 0);
            } else {
                dev.setOnlineNum(Objects.equals(dev.getStatus(), Constants.CHANNEL_ONLINE) ? 1 : 0);
            }
            dev.setNum(1);
            if (dev.getIsMonitor().equals(IndustryConstants.COLLECTION.toString())) {
                dev.setIsMonitorNum(1);
            }
            if (deviceFlightStatusMap != null) {
                dev.setFlyStatus(deviceFlightStatusMap.get(dev.getDeviceCode()));
            }
        }));
        //查询有设备权限组织的上级组织直至更结点
        Set<String> orgIdList = getParentsList(orgList);
        //去重
        Set<String> orgResList = orgIdList.stream().filter(org -> !orgList.contains(org)).collect(Collectors.toSet());
        List<DeviceTreeVO> parOrgList;
        if (!CollectionUtils.isEmpty(orgResList)) {
            DeviceReqDTO reqDTO = new DeviceReqDTO();
            reqDTO.setOrgIdList(orgResList);
            //父节点直至根节点集合
            parOrgList = baseMapper.getCustOrganInfo(reqDTO);
            monitorList.addAll(parOrgList);
        }

        //按照层级正序排列
        List<DeviceTreeVO> sortList = monitorList.stream().sorted(Comparator.comparing(DeviceTreeVO::getLevel)).collect(Collectors.toList());
        //递归更新数量
        List<DeviceTreeVO> res = getDevNumListV2(sortList, monitorList);

        //列表转换为树形结构
        //获取父节点，0表示父节点
        treeList = res.stream().filter(e -> e.getLevel().equals(DeviceConstants.ONLINE_STATUS_ONE)).map(e -> {
            if (!CollectionUtils.isEmpty(e.getList())) {
                e.getList().addAll(getChildrens(e, sortList));
            } else {
                e.setList(getChildrens(e, sortList));
            }
            return e;
        }).collect(Collectors.toList());
        long startMillis3 = Calendar.getInstance().getTimeInMillis();
        log.info("遍历OrgTreeList,耗时"+ (startMillis3 - startMillis2) +"毫秒！");
        return treeList;
    }

    @DataScope(areaPro="m.province_id",areaCity="m.city_id",tenantColumn="ord.tenant_id", industryCode="ord.industry_code", deviceColumn="ord.device_code",acceptColumn="ord.accept_node")
    @Override
    public List<DeviceTreeVO> getTenantMonitorLevel(DeviceReqDTO deviceReqDTO) {
        log.info("查查询租户管理区域---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        deviceReqDTO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<DeviceTreeVO> treeList = baseMapper.getTenantArea(deviceReqDTO);
        log.info("查询租户管理区域---返回报文------{}", treeList);
        return treeList;
    }

    /**
     * 根据行业版本编码查询设备信息
     *
     * @param pageDeviceVO 行业版本编码
     * @return 设备数据和分页结果
     * @author chenmf
     * @date 2022/1/27
     */
    @Override
    @DataScope(appVerCode="op.app_ver_code",acceptColumn="op.accept_node")
    public List<DeviceByPlatformVO> pageDeviceByPlatform(PageDeviceVO pageDeviceVO) {
        List<DeviceByPlatformVO> list = deviceInfoMapper.pageDeviceByPlatform(pageDeviceVO);
        return list;
    }

    /**
     * 递归查询部门的所属组织
     *
     * @param allOrgList
     * @return
     */
    private List<DeviceTreeVO> getBelongOrg(DeviceTreeVO devVO, List<DeviceTreeVO> allOrgList, List<DeviceTreeVO> resOrgList) {
        for (DeviceTreeVO orgVO : allOrgList) {
            if (devVO.getParentCode().equals(orgVO.getCode()) && "1".equals(orgVO.getOrgType())) {
                resOrgList.add(orgVO);
            } else if (devVO.getParentCode().equals(orgVO.getCode()) && "2".equals(orgVO.getOrgType())) {
                getBelongOrg(orgVO, allOrgList, resOrgList);
            }
        }
        return resOrgList;
    }

    /**
     * 查询部门组织的所有上级节点
     *
     * @param orgList
     * @return
     */
    private Set<String> getParentsList(Set<String> orgList) {
        Set<String> orgIdList = new HashSet<>();
        // 获取组织父节点直至根节点
        for (String parentOrgId : orgList) {
            boolean stopFlag = true;
            while (stopFlag) {
                if (parentOrgId.split("-")[1].length() == 2) {
                    stopFlag = false;
                } else {
                    parentOrgId = parentOrgId.substring(0, parentOrgId.length() - 2);
                    orgIdList.add(parentOrgId);
                }
            }
        }
        return orgIdList;
    }

    /**
     * 查询部门组织的所有上级节点
     *
     * @param treeList
     * @return
     */
    private List<DeviceTreeVO> getDevNumList(List<DeviceTreeVO> sortList, List<DeviceTreeVO> treeList) {
        for (DeviceTreeVO sortVO : sortList) {
            //提取所有下属节点
            List<DeviceTreeVO> resList = new ArrayList<>();
            resList = getOrgChildNodeList(sortVO.getCode(), treeList, resList);
            resList.add(sortVO);
            //将所有下属节点内的通道信息提取出来
            Set<DeviceTreeVO> channelList = new HashSet<>();
            resList.forEach(org -> {
                if (!CollectionUtils.isEmpty(org.getList())) {
                    org.getList().forEach(dev -> {
                        if (!CollectionUtils.isEmpty(org.getList())) {
                            dev.getList().forEach(chan -> {
                                DeviceTreeVO monVO = new DeviceTreeVO();
                                monVO.setChannelCode(chan.getChannelCode());
                                monVO.setStatus(chan.getStatus());
                                channelList.add(monVO);
                            });
                        }
                    });
                }
            });
            sortVO.setNum(channelList.size());
            sortVO.setOnlineNum((int) channelList.stream().filter(mon -> Objects.equals(Constants.ZERO, mon.getStatus())).count());
            //子节点都收藏标记上级节点为收藏
            resList.forEach(item -> {
                if (!CollectionUtils.isEmpty(item.getList())) {
                    sortVO.setIsMonitorNum(item.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum() + sortVO.getIsMonitorNum());
                }
            });
            if (sortVO.getIsMonitorNum() == sortVO.getNum()) {
                sortVO.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
            }
        }
        return sortList;
    }

    private List<DeviceTreeVO> getDevNumListV2(List<DeviceTreeVO> sortList, List<DeviceTreeVO> treeList) {
        for (DeviceTreeVO sortVO : sortList) {
            //提取所有下属节点
            List<DeviceTreeVO> resList = new ArrayList<>();
            resList = getOrgChildNodeList(sortVO.getCode(), treeList, resList);
            resList.add(sortVO);
            //将所有下属节点内的设备信息提取出来
            Set<DeviceTreeVO> deviceList = new HashSet<>();
            resList.forEach(org -> {
                if (!CollectionUtils.isEmpty(org.getList())) {
                    deviceList.addAll(org.getList());
                }
            });
            sortVO.setNum(deviceList.size());

//            sortVO.setOnlineNum((int) deviceList.stream().filter(mon -> Objects.equals(Constants.ZERO, mon.getStatus())).count());
            sortVO.setOnlineNum(deviceList.stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
            //子节点都收藏标记上级节点为收藏
            resList.forEach(item -> {
                if (!CollectionUtils.isEmpty(item.getList())) {
                    sortVO.setIsMonitorNum(item.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum() + sortVO.getIsMonitorNum());
                }
            });
            if (sortVO.getIsMonitorNum() == sortVO.getNum()) {
                sortVO.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
            }
        }
        return sortList;
    }

    private List<IndustryDeviceTree> getDevNumTreeList(List<IndustryDeviceTree> sortList, List<IndustryDeviceTree> treeList) {
        for (IndustryDeviceTree sortVO : sortList) {
            //提取所有下属节点
            List<IndustryDeviceTree> resList = new ArrayList<>();
            resList = getOrgChildNodeList1(sortVO.getCode(), treeList, resList);
            resList.add(sortVO);
            //将所有下属节点内的通道信息提取出来
            Set<IndustryDeviceTree> channelList = new HashSet<>();
            Set<IndustryDeviceTree> deviceList = new HashSet<>();
            resList.forEach(org -> {
                if (!CollectionUtils.isEmpty(org.getList())) {
                    org.getList().forEach(dev -> {
                        if (!CollectionUtils.isEmpty(org.getList())) {
                            IndustryDeviceTree device = new IndustryDeviceTree();
                            device.setDeviceCode(dev.getDeviceCode());
                            device.setDeviceStatus(dev.getDeviceStatus());
                            deviceList.add(device);
                            dev.getList().forEach(chan -> {
                                IndustryDeviceTree monVO = new IndustryDeviceTree();
                                monVO.setChannelCode(chan.getChannelCode());
                                monVO.setChannelStatus(chan.getChannelStatus());
                                channelList.add(monVO);
                            });
                        }
                    });
                }
            });
            sortVO.setNumber((long) channelList.size());
            sortVO.setOnLineNumber(channelList.stream().filter(c -> c.getChannelStatus().equals(DeviceConstants.ZERO_NUM)).count());
            // 摄像机数量
            sortVO.setDevNumber((long) deviceList.size());
            sortVO.setDevOnLineNumber(deviceList.stream().filter(c -> Objects.equals(Constants.ZERO, c.getDeviceStatus())).count());
        }
        return sortList;
    }

    /**
     * 查询部门组织的所有下级节点
     *
     * @param treeList
     * @return
     */
    private List<DeviceTreeVO> getOrgChildNodeList(String orgId, List<DeviceTreeVO> treeList,
                                                   List<DeviceTreeVO> resList) {
        for (DeviceTreeVO treeVO : treeList) {
            if (orgId.equals(treeVO.getParentCode())) {
                resList.add(treeVO);
                getOrgChildNodeList(treeVO.getCode(), treeList, resList);
            }
        }
        return resList;
    }

    private List<IndustryDeviceTree> getOrgChildNodeList1(String orgId, List<IndustryDeviceTree> treeList,
                                                          List<IndustryDeviceTree> resList) {
        for (IndustryDeviceTree treeVO : treeList) {
            if (orgId.equals(treeVO.getParentCode())) {
                resList.add(treeVO);
                getOrgChildNodeList1(treeVO.getCode(), treeList, resList);
            }
        }
        return resList;
    }

    /**
     * 递归查询子节点
     *
     * @param root
     * @param treeList
     * @return
     */
    private List<DeviceTreeVO> getChildrens(DeviceTreeVO root, List<DeviceTreeVO> treeList) {
        return treeList.stream().filter(e -> Objects.equals(e.getParentCode(), root.getCode())).map(e -> {
            if (!CollectionUtils.isEmpty(e.getList())) {
                e.getList().addAll(getChildrens(e, treeList));
            } else {
                e.setList(getChildrens(e, treeList));
            }
            return e;
        }).collect(Collectors.toList());
    }

    private List<IndustryDeviceTree> getTreeChildrens(IndustryDeviceTree root, List<IndustryDeviceTree> treeList) {
        List<IndustryDeviceTree> childrenList = treeList.stream().filter(e -> Objects.equals(e.getParentCode(), root.getCode())).map(e -> {
            if (!CollectionUtils.isEmpty(e.getList())) {
                e.getList().addAll(getTreeChildrens(e, treeList));
            } else {
                e.setList(getTreeChildrens(e, treeList));
            }
            return e;
        }).collect(Collectors.toList());
        return childrenList;
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    @Override
    public List<SiteResultVO> querySitesForWE(SiteRequestDTO siteRequestDTO) {
        return this.baseMapper.querySitesForWE(siteRequestDTO);
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    @Override
    public List<DeviceSiteVO> queryDevicesByName(SiteRequestDTO siteRequestDTO) {
        int pageNum = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageNum()), 0);
        int pageSize = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageSize()), 0);
        if (0 != pageNum && 0 != pageSize) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<DeviceSiteVO> deviceSiteVOS = this.baseMapper.queryDevicesByName(siteRequestDTO);
        //地图打点
        //(根据设备编码集合查询设备是否有未完结告警)
        List<String> deviceCodes = new ArrayList<>();
        deviceSiteVOS.forEach(dev -> {
                deviceCodes.add(dev.getDeviceCode());
        });
        SysUser user = SecurityUtils.getLoginUser().getUser();
        AlarmOrderParmaDto alarmOrderParmaDto = new AlarmOrderParmaDto();
        alarmOrderParmaDto.setTenantId(user.getTenantId());
        alarmOrderParmaDto.setIndustryCode(user.getIndustryCode());
        alarmOrderParmaDto.setDeviceCodes(deviceCodes);
        log.info("queryDevicesByName-getOrderNumByDeviceCode入参" + JSON.toJSONString(alarmOrderParmaDto));
        Result<List<OrderCompleteVO>> orderNumByDeviceCode = iTradeWarningOrderService.getOrderNumByDeviceCode(alarmOrderParmaDto);
        log.info("queryDevicesByName-getOrderNumByDeviceCode出参" + JSON.toJSONString(orderNumByDeviceCode));

        if (ResultCode.SUCCESS.getCode() == orderNumByDeviceCode.getCode()) {
            List<OrderCompleteVO> data = orderNumByDeviceCode.getData();
            deviceSiteVOS.forEach(device -> {
                    OrderCompleteVO deviceMap = data.stream().filter(map -> Objects.equals(device.getDeviceCode(), map.getDeviceCode())).findFirst().orElse(null);
                    String isCompleteFlag = Objects.nonNull(deviceMap) ? deviceMap.getIsCompleteFlag() : Constants.ZERO;
                    device.setIsCompleteFlag(isCompleteFlag);
            });
        }
        return deviceSiteVOS;
    }


    /**
     * 功能描述: 预警事件-查询摄像头集合【APP用】
     *
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/11/21
     * @version: 1.5.30
     */
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code", alarmSourceVal = "4",acceptColumn="t1.accept_node")
    @Override
    public Map<String,List> queryDevicesApp(SiteRequestDTO siteRequestDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser sysUser = baseMapper.selectUserOnlyByUserName(user.getUserName());
        siteRequestDTO.setUserId(sysUser.getUserId());
        PageHelper.startPage(siteRequestDTO.getPageNum(), siteRequestDTO.getPageSize());
        List<String> devInfoList = this.baseMapper.queryDevicesInfoPage(siteRequestDTO);
        List<DevChanVO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(devInfoList)) {
            siteRequestDTO.setDeviceCodeList(devInfoList);
            list = this.baseMapper.queryDevicesApp(siteRequestDTO);
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(dev -> {
                    Long numLong = dev.getList().stream().count();
                    dev.setNum(numLong.intValue());
                    int monitorNum = dev.getList().stream().mapToInt(DevChanVO::getIsMonitorNum).sum();
                    dev.setIsMonitorNum(monitorNum);
                    //子节点都收藏标记上级节点为收藏
                    if (dev.getNum() == monitorNum) {
                        dev.setIsMonitor(Constants.ONE);
                    } else {
                        dev.setIsMonitor(Constants.ZERO);
                    }

                    if(!StringUtils.isEmpty(siteRequestDTO.getOrderLong()) &&
                            !StringUtils.isEmpty(siteRequestDTO.getOrderLat())){
                        // 查询处理距离
                        if(StringUtils.isNotEmpty(String.valueOf(dev.getRangeMeter()))){
                            if(dev.getRangeMeter()<= 1000){
                                dev.setDistance(dev.getRangeMeter()+"米");
                            }else {
                                BigDecimal bg = new BigDecimal(dev.getRangeMeter());
                                BigDecimal bg1 = new BigDecimal(1000);
                                dev.setDistance(bg.divide(bg1).setScale(2, BigDecimal.ROUND_HALF_UP)+"千米");
                            }
                        }
                    }
                });
            }
        }

        Map<String,List> map = new HashMap<>();
        map.put(Constants.DEV_INFO_LIST,devInfoList);
        map.put(Constants.LIST,list);
        return map;
    }


    @Override
    @DataScope(tenantColumn="od.tenant_id",industryCode = "od.industry_code",alarmSourceVal = "4",acceptColumn="od.accept_node",deviceColumn = "od.device_code")
    public DeviceChannelInfoVO queryDevicesForWE(SiteRequestDTO siteRequestDTO) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        siteRequestDTO.setTenantId(sysUser.getTenantId());
        siteRequestDTO.setIndustryCode(sysUser.getIndustryCode());

        List<String> orderStatusList = Arrays.asList("0","1","2");
        if(StringUtils.isEmpty(siteRequestDTO.getOrderStatus()) || orderStatusList.contains(siteRequestDTO.getOrderStatus())){
            siteRequestDTO.setOrderStatus(Constants.ONE);
        }
        DeviceChannelInfoVO deviceChannelInfo = this.baseMapper.queryDevicesForWE(siteRequestDTO);
        List<ChannelInfoScrVO> channels = this.baseMapper.queryChannelList(siteRequestDTO);
        if (deviceChannelInfo != null) {
            String configValue = VideoConstants.ZERO_STR;
            //视图融合开关
            AjaxResult ajaxResult = systemFeignClient.getConfValue("device_registration_switch");
            if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)){
                    configValue = sysConfig.getConfigValue();
                }
            }
            deviceChannelInfo.setRegistrationSwitch(Constants.ZERO);
            //开关开启
            if(VideoConstants.ONE_STR.equals(configValue)){
                List<DeviceRegistrationVO> deviceRegistrationVOS = deviceInfoMapper.queryRegistrationInfo(siteRequestDTO.getDeviceCode());
                if(!CollectionUtils.isEmpty(deviceRegistrationVOS)){
                    boolean flag = deviceRegistrationVOS.stream().anyMatch(item->Constants.ONE.equals(item.getRegistrationSwitch()));
                    deviceChannelInfo.setRegistrationSwitch(flag ? Constants.ONE : Constants.ZERO);

                    List<DeviceRegistrationVO> collect = deviceRegistrationVOS.stream().filter(e -> sysUser.getTenantId().equals(e.getTenantId())
                            && sysUser.getIndustryCode().equals(e.getIndustryCode())).collect(Collectors.toList());

                    if(CollectionUtils.isEmpty(collect)){
                        DeviceRegistrationVO vo = deviceRegistrationVOS.get(Constants.ZERO_NUM);
                        DeviceRegistrationVO deviceRegistrationVO = new DeviceRegistrationVO();
                        deviceRegistrationVO.setDeviceCode(vo.getDeviceCode());
                        deviceRegistrationVO.setChannelCode(vo.getChannelCode());
                        deviceRegistrationVO.setUrl(vo.getUrl());
                        deviceRegistrationVO.setRegistrationSwitch(vo.getRegistrationSwitch());
                        deviceRegistrationVO.setRegistrationStatus(vo.getRegistrationStatus());
                        deviceRegistrationVO.setRegistrationType(vo.getRegistrationType());
                        deviceChannelInfo.setDeviceRegistration(deviceRegistrationVO);
                    }else{
                        deviceChannelInfo.setDeviceRegistration(collect.get(Constants.ZERO_NUM));
                    }
                }
            }

            deviceChannelInfo.setChannels(channels);
            // 查询设备的收藏状态
            LoginUser currentUser = SecurityUtils.getLoginUser();
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = baseMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            Integer coll = this.baseMapper.queryDeviceCollStatus(siteRequestDTO.getDeviceCode(), user.getUserId());
            if(channels.size() == Constants.ZERO_NUM){
                deviceChannelInfo.setIsMonitor("0");
            }else{
                deviceChannelInfo.setIsMonitor(channels.size() == coll ? "1" : "0");
            }
            // 查询ar标签收藏
            if(null !=siteRequestDTO.getArTagFlag() && siteRequestDTO.getArTagFlag() == Constants.ONE_NUM){
                Integer arTagColl = this.baseMapper.queryArTagCollStatus(siteRequestDTO.getDeviceCode(), user.getUserId());
                if(channels.size() == Constants.ZERO_NUM){
                    deviceChannelInfo.setTagCollect("0");
                }else{
                    deviceChannelInfo.setTagCollect(channels.size() == arTagColl ? "1" : "0");
                }
            }
            //增加返回设备关联的标签名称
            deviceChannelInfo.setLabelNameList(this.baseMapper.queryLabelNameList(siteRequestDTO));

            //查询所属组织
            DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
            BeanUtils.copyProperties(siteRequestDTO, deviceInfoVO);
            List<Map<String, String>> orgInfoList = deviceInfoMapper.queryOrgInfoList(deviceInfoVO);
            if (!CollectionUtils.isEmpty(orgInfoList)) {
                Map<String, String> map = orgInfoList.get(0);
                deviceChannelInfo.setOrgName(map.get(Constants.PARAM_ORG_NAME));
                deviceChannelInfo.setOrgCode(map.get(Constants.PARAM_ORG_ID));
                deviceChannelInfo.setOrgRelId(map.get(Constants.PARAM_ID));
            }

            // 补充设备附件信息
            try {
                List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCode(
                        sysUser.getTenantId(), sysUser.getIndustryCode(), siteRequestDTO.getDeviceCode(), IndustryConstants.ONE_NUM);
                if (fileUrlList != null && !fileUrlList.isEmpty()) {
                    Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
                    // 处理Obs文件URL加密返回
                    if (stringStringMap != null && !stringStringMap.isEmpty()) {
                        deviceChannelInfo.setFileUrlList(new ArrayList<>(stringStringMap.values()));
                    }
                }
            } catch (Exception e) {
                log.error("DeviceInfoRepositoryImpl getDeviceInfo getFileInfo error, deviceCode: {}, error: {}", 
                        siteRequestDTO.getDeviceCode(), e.getMessage(), e);
            }

            try {
                //根据设备编码集合查询设备是否有未完结告警
                List<String> deviceCodes = new ArrayList<>();
                deviceCodes.add(siteRequestDTO.getDeviceCode());
                AlarmOrderParmaDto alarmOrderParmaDto = new AlarmOrderParmaDto();
                alarmOrderParmaDto.setTenantId(siteRequestDTO.getTenantId());
                alarmOrderParmaDto.setIndustryCode(siteRequestDTO.getIndustryCode());
                alarmOrderParmaDto.setDeviceCodes(deviceCodes);
                log.info("queryDevicesForWE-getOrderNumByDeviceCode入参" + JSON.toJSONString(alarmOrderParmaDto));
                Result<List<OrderCompleteVO>> orderNumByDeviceCode = iTradeWarningOrderService.getOrderNumByDeviceCode(alarmOrderParmaDto);
                log.info("queryDevicesForWE-getOrderNumByDeviceCode出参" + JSON.toJSONString(orderNumByDeviceCode));

                if (ResultCode.SUCCESS.getCode() == orderNumByDeviceCode.getCode()) {
                    List<OrderCompleteVO> data = orderNumByDeviceCode.getData();
                    if(!CollectionUtils.isEmpty(data)){
                        OrderCompleteVO orderCompleteVO = data.get(Constants.ZERO_NUM);
                        String isCompleteFlag = Objects.nonNull(orderCompleteVO) ? orderCompleteVO.getIsCompleteFlag() : Constants.ZERO;
                        deviceChannelInfo.setIsCompleteFlag(isCompleteFlag);
                    }
                }
            } catch (Exception e) {
                log.error("iTradeWarningOrderService.getOrderNumByDeviceCode error, e:", e);
            }
        }
        return deviceChannelInfo;
    }


    @Override
    @DataScope(deviceColumn="aa.device_code",tenantColumn="aa.tenant_id",industryCode = "aa.industry_code",alarmSourceVal = "4",acceptColumn="aa.accept_node")
    public List<ChannelInfoScrVO> queryChannnelInfoForApp(SiteRequestDTO siteRequestDTO) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = baseMapper.selectUserOnlyByUserName(sysUser.getUserName());
        siteRequestDTO.setUserId(user.getUserId());
        int pageNum = siteRequestDTO.getPageNum();
        int pageSize = siteRequestDTO.getPageSize();
        if (0 != pageNum && 0 != pageSize) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<ChannelInfoScrVO> channels = this.baseMapper.queryChannelListCollection(siteRequestDTO);
        if(!StringUtils.isEmpty(siteRequestDTO.getOrderLong()) && !StringUtils.isEmpty(siteRequestDTO.getOrderLat())){
            for (ChannelInfoScrVO deviceInfo : channels) {
                // 查询处理距离
                if(StringUtils.isNotEmpty(String.valueOf(deviceInfo.getRangeMeter()))){
                    if( deviceInfo.getRangeMeter()<= 1000){
                        deviceInfo.setDistance(deviceInfo.getRangeMeter()+"米");
                    }else {
                        BigDecimal bg = new BigDecimal(deviceInfo.getRangeMeter());
                        BigDecimal bg1 = new BigDecimal(1000);
                        deviceInfo.setDistance(bg.divide(bg1).setScale(2, BigDecimal.ROUND_HALF_UP)+"千米");
                    }
                }
            }
        }

        //增加摄像机抓图返回
        if(!CollectionUtils.isEmpty(channels)){
            List<String> channelList = new ArrayList<>();
            for (ChannelInfoScrVO channel : channels) {
                channelList.add(channel.getChannelCode());
            }
            QueryEntity query = new QueryEntity(VideoConstants.SNAP_SHOT_SIMPLE_COLLECTION, SnapShotSimpleRes.class);
            query.in(VideoConstants.CHANNELCODE,channelList.toArray(new String[channelList.size()]));
            List<SnapShotSimpleRes> resultList = snapShotSimpleMapper.findList(query,null, MongoDBType.PRIMARY);
            if(!CollectionUtils.isEmpty(resultList)) {
                //url去空判断
                Set<String> fileUrls = resultList.stream().filter(pe ->
                        !org.springframework.util.StringUtils.isEmpty(pe.getUrl())).map(SnapShotSimpleRes::getUrl).collect(Collectors.toSet());
                // 处理Obs文件URL加密返回
                //add by liuxt video-file改造
                log.info("fileUrls请求=" + JSONObject.toJSONString(fileUrls));
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
                log.info("fileUrls返回=" + JSONObject.toJSONString(result));
                for (ChannelInfoScrVO channel : channels) {
                    for (SnapShotSimpleRes f : resultList) {
                        if (f.getChannelCode().equals(channel.getChannelCode())
                                && !org.springframework.util.StringUtils.isEmpty(f.getUrl())) {
                            channel.setImgUrl(result.get(f.getUrl()));
                        }
                    }
                }
            }
        }

        return channels;
    }


    @DataScope(tenantColumn="d.tenant_id",deviceColumn = "d.device_code", industryCode = "d.industry_code",alarmSourceVal = "4",acceptColumn="d.accept_node")
    @Override
    public List<DeviceInfoDO> getDeviceList(SiteRequestDTO siteRequestDTO) {
        return this.baseMapper.getDeviceList(siteRequestDTO);
    }

    /**
     * 查询物联设备列表，只返回编号、名称【下拉筛选】
     * @param siteRequestDTO
     * @return List<IotDeviceVO>
     * @author lyy
     * @date: 2024/01/08
     * @version v1.6.40
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @Override
    public List<IotDeviceVO> getIotDeviceList(SiteRequestDTO siteRequestDTO) {
        return this.baseMapper.getIotDeviceList(siteRequestDTO);
    }

    /**
     * 查询通道录像计划校验（返回冲突的设备通道列表和录像保存天数列表）
     *
     * @param queryRecordPlanCheckDTO
     * @return
     * @author: chenmf
     * @date: 2022/3/13
     */
    @Override
    public RecordPlanCheckDTO queryRecordPlanCheck(QueryRecordPlanCheckDTO queryRecordPlanCheckDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        queryRecordPlanCheckDTO.setTenantId(user.getTenantId());
        queryRecordPlanCheckDTO.setIndustryCode(user.getIndustryCode());
        log.info("查询通道录像计划校验接口请求报文------{}", JSON.toJSONString(queryRecordPlanCheckDTO));
        List<ChekcChannelVideoPlanVO> channelVideoPlanVOList = queryRecordPlanCheckDTO.getChannelVideoPlanVOList();

        String tenantId = queryRecordPlanCheckDTO.getTenantId();
        //冲突的设备通道列表
        List<ChannelVideoPlanVO> conflictedlist = deviceInfoMapper.queryVideoPlan(queryRecordPlanCheckDTO);
        //小于入参存储时长的录像最大保存天数列表
        ChannelStorageTimeVO channelStorageTimeVO = new ChannelStorageTimeVO();
        channelStorageTimeVO.setChannelVideoPlanVOList(channelVideoPlanVOList);
        channelStorageTimeVO.setCustomerCode(tenantId);
        channelStorageTimeVO.setIndustryCode(user.getIndustryCode());
        channelStorageTimeVO.setStorageDay(String.valueOf(queryRecordPlanCheckDTO.getStorageDay()));
        List<ChannelStorageTimeVO> storageDaylist = deviceInfoMapper.queryRecordPlanStorageTime(channelStorageTimeVO);

        RecordPlanCheckDTO recordPlanCheckDTO = new RecordPlanCheckDTO();
        recordPlanCheckDTO.setConflictedlist(conflictedlist);
        recordPlanCheckDTO.setStorageDaylist(storageDaylist);
        return recordPlanCheckDTO;

    }

    /**
     * 5.3.故障告警消息报文
     *
     * @param deviceErrorVO
     * @return
     */
    @Override
    public Result insertDeviceError(DeviceErrorVO deviceErrorVO) {
        log.info("************5.3.故障告警消息报文****************：{}", JSON.toJSONString(deviceErrorVO));
        try {
            int result = deviceInfoMapper.insertDeviceError(deviceErrorVO);

            // 如果设备告警编码是202 更新设备表与通道表
            if ("202".equals(deviceErrorVO.getAlarmType())) {

                List<SaveOrUpdateDeviceDTO> deviceDTOList = new ArrayList<>();
                SaveOrUpdateDeviceDTO saveOrUpdateDeviceDTO = new SaveOrUpdateDeviceDTO();
                saveOrUpdateDeviceDTO.setDeviceCode(deviceErrorVO.getDeviceCode());
                saveOrUpdateDeviceDTO.setDeviceStatus(DeviceConstants.OFF_LINE_FLAG);
                deviceDTOList.add(saveOrUpdateDeviceDTO);
                videoDeviceInfoService.batchSaveOrUpdateDevice(deviceDTOList);

                List<SaveOrUpdateChannelDTO> channelDTOList = new ArrayList<>();
                SaveOrUpdateChannelDTO saveOrUpdateChannelDTO = new SaveOrUpdateChannelDTO();
                saveOrUpdateChannelDTO.setDeviceCode(deviceErrorVO.getDeviceCode());
                saveOrUpdateChannelDTO.setChannelCode(deviceErrorVO.getChannelCode());
                saveOrUpdateChannelDTO.setChannelStatus(DeviceConstants.OFF_LINE_FLAG);
                channelDTOList.add(saveOrUpdateChannelDTO);
                videoChannelInfoService.batchSaveOrUpdateChannel(channelDTOList);
            }

            // 如果设备告警编码是205，更新通道表
            if ("205".equals(deviceErrorVO.getAlarmType())) {
                List<SaveOrUpdateChannelDTO> channelDTOList = new ArrayList<>();
                SaveOrUpdateChannelDTO saveOrUpdateChannelDTO = new SaveOrUpdateChannelDTO();
                saveOrUpdateChannelDTO.setChannelCode(deviceErrorVO.getChannelCode());
                saveOrUpdateChannelDTO.setChannelStatus(DeviceConstants.OFF_LINE_FLAG);
                channelDTOList.add(saveOrUpdateChannelDTO);
                videoChannelInfoService.batchSaveOrUpdateChannel(channelDTOList);
            }

            if (result <= 0) {
                log.info("5.3.故障告警消息报文操作失败：更新客户变更信息失败");
                return Result.fail(1004, "更新客户变更信息失败");
            }
        } catch (Exception e) {
            log.error("5.3.故障告警消息报文操作失败：{}", e);
            return Result.fail(1004, "故障告警消息报文失败" + e.getLocalizedMessage());
        }
        log.info("5.3.故障告警消息报文操作成功");
        return Result.success("");
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    @Override
    public List<SiteResultVO> querySitesByLocal(SiteRequestDTO siteRequestDTO) {
        List<SiteResultVO> list = new ArrayList();
        //接口合并兼容，QueryType=1 兼容linkageDeviceList方法，默认走原逻辑
        if(null == siteRequestDTO.getQueryType()){
            //distance【半径】参数：
            //1、传"-1"查询全部数据
            //2、传空/不传值默认从后台枚举值获取枚举值的数据
            //3、传其他值且经纬度不为空，使用参数传的值
            String distance = siteRequestDTO.getDistance();
            if(StringUtils.isEmpty(distance)){
                //单位：米
                distance = "10000";
                Result<List<SysDictDataResponse>> result = systemFeignClient.dictDataType(DeviceConstants.EMUM_TYPE_ID_74);
                if (result.getCode() != ResultCode.SUCCESS.getCode()) {
                    throw new CustomException(result.getMsg());
                }
                List<SysDictDataResponse> sysDictDataList = result.getData();
                if (sysDictDataList != null && !sysDictDataList.isEmpty()) {
                    distance = sysDictDataList.get(0).getDictValue();
                }
                siteRequestDTO.setDistance(String.valueOf(Integer.parseInt(distance)));
            }else if("-1".equals(distance)){
                siteRequestDTO.setDistance(null);
            }
        }
        if(null != siteRequestDTO.getQueryType() && 1 == siteRequestDTO.getQueryType()){
            list = baseMapper.linkageDeviceNew(siteRequestDTO);
        }else{
            list =  baseMapper.querySitesByCode(siteRequestDTO);
        }
        return list;
    }

    /**
     * 获取视频存储时长
     *
     * @return
     * @author chenmf
     * @date 2022/3/21
     * @version
     */
    @Override
    public List<String> queryRecordPlanStorageDay(QueryStorageDayVO queryStorageDayVO) {

        return deviceInfoMapper.queryRecordPlanStorageDay(queryStorageDayVO);
    }


    /**
     * 获取设备标签树
     *
     * @return List<DeviceTreeVO>
     * @author trk
     * @date 2022/3/22
     * @version
     */
    @DataScope(tenantColumn="dataTable.tenant_id",deviceColumn = "dataTable.device_code",
            industryCode = "dataTable.industry_code",alarmSourceVal = "4",acceptColumn="vod.accept_node")
    @Override
    public List<DeviceTreeVO> getMonitorLabelTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = baseMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        List<DeviceTreeVO> list = baseMapper.getMonitorLabelTree(deviceReqDTO);
        list.forEach(label -> {
            Map<String,Integer> labelNumMap = new HashMap<>();
            labelNumMap.put(DeviceNorthIntfConstants.LABEL_NUM,0);
            labelNumMap.put(DeviceNorthIntfConstants.NUM,0);
            labelNumMap.put(DeviceNorthIntfConstants.IS_MONITOR_NUM,0);
            label.getList().forEach(item -> {
                int onlineNum = 0;
                int isMonitorNum = 0;
                //去重处理
                List<DeviceTreeVO> childList = item.getList().stream().collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(DeviceTreeVO::getCode))), ArrayList::new));
                Iterator<DeviceTreeVO> iterator = childList.iterator();
                while (iterator.hasNext()) {
                    DeviceTreeVO child = iterator.next();
                    //设置在线设备
                    if (Objects.equals(Constants.ZERO, child.getStatus())) {
                        onlineNum++;
                    }
                    //设置收藏设备
                    if (String.valueOf(IndustryConstants.COLLECTION).equals(child.getIsMonitor())) {
                        child.setIsMonitorNum(1);
                        isMonitorNum++;
                    }
                }
                //设置在线设备数量
                item.setOnlineNum(onlineNum);
                labelNumMap.put(DeviceNorthIntfConstants.LABEL_NUM,labelNumMap.get(DeviceNorthIntfConstants.LABEL_NUM)+onlineNum);
                //设置总数量
                item.setNum(childList.size());
                labelNumMap.put(DeviceNorthIntfConstants.NUM,labelNumMap.get(DeviceNorthIntfConstants.NUM)+childList.size());
                item.setIsMonitorNum(isMonitorNum);
                labelNumMap.put(DeviceNorthIntfConstants.IS_MONITOR_NUM,labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM)+isMonitorNum);
                //子节点都收藏标记上级节点为收藏
                if (item.getNum() == isMonitorNum) {
                    item.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                }
                item.setList(childList);
            });
            label.setOnlineNum(labelNumMap.get(DeviceNorthIntfConstants.LABEL_NUM));
            label.setNum(labelNumMap.get(DeviceNorthIntfConstants.NUM));
            label.setIsMonitorNum(labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM));
            //子节点都收藏标记上级节点为收藏
            if (label.getNum() == labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM)) {
                label.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
            }
        });
        return list;
    }

    /**
     * 获取标签设备树NEW-增加标签类型
     *
     * @return List<DeviceTreeVO>
     * @author trk
     * @date 2022/3/22
     * @version
     */
    @DataScope(tenantColumn="dataTable.tenant_id",deviceColumn = "dataTable.device_code",
            industryCode = "dataTable.industry_code",alarmSourceVal = "4",acceptColumn="vod.accept_node")
    @Override
    public List<DeviceTreeVO> getMonitorLabelTreeNew(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = baseMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        log.info("获取设备标签树接口请求报文------{}", JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> resList = new ArrayList<>();
        List<DeviceTreeVO> list = baseMapper.getMonitorLabelTreeNew(deviceReqDTO);
        Set<String> fileUrls = new HashSet<>();
        list.forEach(labelType ->{
            //摄像机标签
            labelType.getList().forEach(label -> {
                //设备
                label.getList().forEach(item -> {
                    fileUrls.add(item.getImageUrl());
                });
            });
        });
        //加密
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        //全部标签
        Set<DeviceTreeVO> allLabelList = new HashSet<>();
        //标签类型
        list.forEach(labelType ->{
            Set<DeviceTreeVO> labelTypeList = new HashSet<>();
            //摄像机标签
            labelType.getList().forEach(label -> {
                Map<String,Integer> labelNumMap = new HashMap<>();
                labelNumMap.put(DeviceNorthIntfConstants.LABEL_NUM,0);
                labelNumMap.put(DeviceNorthIntfConstants.NUM,0);
                labelNumMap.put(DeviceNorthIntfConstants.IS_MONITOR_NUM,0);
                //设备
                label.getList().forEach(item -> {
                    if (!StringUtils.isEmpty(item.getImageUrl())) {
                        item.setImageUrl(result.get(item.getImageUrl()));
                    }
                    int onlineNum = 0;
                    int isMonitorNum = 0;
                    //去重处理
                    List<DeviceTreeVO> childList = item.getList().stream().collect(Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(DeviceTreeVO::getCode))), ArrayList::new));
                    Iterator<DeviceTreeVO> iterator = childList.iterator();
                    while (iterator.hasNext()) {
                        DeviceTreeVO child = iterator.next();
                        //设置在线设备
                        if (Objects.equals(Constants.ZERO, child.getStatus())) {
                            onlineNum++;
                        }
                        //设置收藏设备
                        if (String.valueOf(IndustryConstants.COLLECTION).equals(child.getIsMonitor())) {
                            child.setIsMonitorNum(1);
                            isMonitorNum++;
                        }
                        labelTypeList.add(child);
                    }
                    //设置在线设备数量
                    item.setOnlineNum(onlineNum);
                    labelNumMap.put(DeviceNorthIntfConstants.LABEL_NUM,labelNumMap.get(DeviceNorthIntfConstants.LABEL_NUM)+onlineNum);
                    //设置总数量
                    item.setNum(childList.size());
                    labelNumMap.put(DeviceNorthIntfConstants.NUM,labelNumMap.get(DeviceNorthIntfConstants.NUM)+childList.size());
                    item.setIsMonitorNum(isMonitorNum);
                    labelNumMap.put(DeviceNorthIntfConstants.IS_MONITOR_NUM,labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM)+isMonitorNum);
                    //子节点都收藏标记上级节点为收藏
                    if (item.getNum() == isMonitorNum) {
                        item.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                    }
                    item.setList(childList);
                });
                label.setOnlineNum(labelNumMap.get(DeviceNorthIntfConstants.LABEL_NUM));
                label.setNum(labelNumMap.get(DeviceNorthIntfConstants.NUM));
                label.setIsMonitorNum(labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM));
                //子节点都收藏标记上级节点为收藏
                if (label.getNum() == labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM)) {
                    label.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                }
            });

            Long typeNum = labelTypeList.stream().filter(item -> Objects.equals(Constants.ZERO, item.getStatus())).count();
            labelType.setOnlineNum(typeNum.intValue());
            labelType.setNum(labelTypeList.size());
            Long typeIsMonitorNum = labelTypeList.stream().filter(item -> String.valueOf(IndustryConstants.COLLECTION).equals(item.getIsMonitor())).count();
            labelType.setIsMonitorNum(typeIsMonitorNum.intValue());
            //子节点都收藏标记上级节点为收藏
            if (labelType.getNum() == labelType.getIsMonitorNum()) {
                labelType.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
            }

            allLabelList.addAll(labelTypeList);
        });

        if (!CollectionUtils.isEmpty(list)) {
            DeviceTreeVO resLabelVO = new DeviceTreeVO();
            resLabelVO.setCode(IndustryConstants.LABEL_ROOT_CODE);
            resLabelVO.setName(IndustryConstants.LABEL_ROOT_NAME);
            resLabelVO.setList(list);
            Long typeNum = allLabelList.stream().filter(item -> Objects.equals(Constants.ZERO, item.getStatus())).count();
            resLabelVO.setOnlineNum(typeNum.intValue());
            resLabelVO.setNum(allLabelList.size());
            resList.add(resLabelVO);
        }
        return resList;
    }

    @Override
    @DataScope(deviceColumn = "od.device_code",tenantColumn = "od.tenant_id",industryCode = "od.industry_code",alarmSourceVal = "4",acceptColumn="od.accept_node")
    public List<DeviceIndDTO> listDeviceChannelByCond(DeviceIndReqDTO deviceReq) {
        return baseMapper.listDeviceChannelByCond(deviceReq);
    }

    /**
     * 功能描述: 查询行业应用摄像机树
     *
     * @param: MonitorTreeInfReq
     * @return: List < IndustryDeviceTree>
     * @auther: hued
     * @date: 2022年8月11日10:04:35
     * @version: release_v1.5
     * @requestID:
     */
    @Override
    @DataScope(tenantColumn = "ord.tenant_id", deviceColumn = "ord.device_code",industryCode = "ord.industry_code",alarmSourceVal = "4",acceptColumn="ord.accept_node")
    public List<IndustryDeviceTree> queryMonitorIndTree(MonitorTreeInfReq monitorTreeInfReq) {
        List<IndustryDeviceTree> trees = null;
        if (Objects.equals(monitorTreeInfReq.getDisplayMode(), IndustryConstants.NORMAL)) {
            trees = baseMapper.queryMonitorIndTree(monitorTreeInfReq);
            trees.forEach(pro -> {
                pro.getList().forEach(city -> {
                    city.getList().forEach(county -> {
                        county.getList().forEach(device -> {
                            device.getList().forEach(channel -> {
                                if (Constants.ONE.equals(pro.getIsCollection()) || Constants.ONE.equals(city.getIsCollection())
                                        || Constants.ONE.equals(county.getIsCollection()) || Constants.ONE.equals(device.getIsCollection())) {
                                    channel.setIsCollection(Constants.ONE);
                                }
                            });
                            device.setOnLineNumber(device.getList().stream().filter(item -> Objects.equals(item.getChannelStatus(), IndustryConstants.CHANNEL_ONLINE)).count());
                            device.setNumber((long) device.getList().size());
                            if (Constants.ONE.equals(pro.getIsCollection()) || Constants.ONE.equals(city.getIsCollection()) || Constants.ONE.equals(county.getIsCollection())) {
                                device.setIsCollection(Constants.ONE);
                            }
                        });
                        county.setOnLineNumber(county.getList().stream().mapToLong(IndustryDeviceTree::getOnLineNumber).sum());
                        county.setNumber(county.getList().stream().mapToLong(IndustryDeviceTree::getNumber).sum());
                        // 摄像机数量
                        county.setDevOnLineNumber(county.getList().stream().filter(item -> Objects.equals(Constants.ZERO, item.getDeviceStatus())).count());
                        county.setDevNumber((long)county.getList().size());
                        if (Constants.ONE.equals(pro.getIsCollection()) || Constants.ONE.equals(city.getIsCollection())) {
                            county.setIsCollection(Constants.ONE);
                        }
                    });
                    city.setOnLineNumber(city.getList().stream().mapToLong(IndustryDeviceTree::getOnLineNumber).sum());
                    city.setNumber(city.getList().stream().mapToLong(IndustryDeviceTree::getNumber).sum());
                    // 摄像机数量
                    city.setDevOnLineNumber(city.getList().stream().mapToLong(IndustryDeviceTree::getDevOnLineNumber).sum());
                    city.setDevNumber(city.getList().stream().mapToLong(IndustryDeviceTree::getDevNumber).sum());
                    if (Constants.ONE.equals(pro.getIsCollection())) {
                        city.setIsCollection(Constants.ONE);
                    }
                });
                pro.setOnLineNumber(pro.getList().stream().mapToLong(IndustryDeviceTree::getOnLineNumber).sum());
                pro.setNumber(pro.getList().stream().mapToLong(IndustryDeviceTree::getNumber).sum());
                // 摄像机数量
                pro.setDevOnLineNumber(pro.getList().stream().mapToLong(IndustryDeviceTree::getDevOnLineNumber).sum());
                pro.setDevNumber(pro.getList().stream().mapToLong(IndustryDeviceTree::getDevNumber).sum());
            });
        }
        if (Objects.equals(monitorTreeInfReq.getDisplayMode(), IndustryConstants.LABEL)) {
            trees = baseMapper.queryMonitorLabelOrdTree(monitorTreeInfReq);
            trees.forEach(label -> {
                label.getList().forEach(device -> {
                    device.getList().forEach(channel -> {
                        if (Constants.ONE.equals(label.getIsCollection()) || Constants.ONE.equals(device.getIsCollection())) {
                            channel.setIsCollection(Constants.ONE);
                        }
                    });
                    device.setOnLineNumber(device.getList().stream().filter(item -> Objects.equals(item.getChannelStatus(), IndustryConstants.CHANNEL_ONLINE)).count());
                    device.setNumber((long) device.getList().size());
                    if (Constants.ONE.equals(label.getIsCollection())) {
                        device.setIsCollection(Constants.ONE);
                    }
                });
                label.setOnLineNumber(label.getList().stream().mapToLong(IndustryDeviceTree::getOnLineNumber).sum());
                label.setNumber(label.getList().stream().mapToLong(IndustryDeviceTree::getNumber).sum());
                // 摄像机数量
                label.setDevOnLineNumber(label.getList().stream().filter(item -> Objects.equals(Constants.ZERO, item.getDeviceStatus())).count());
                label.setDevNumber((long)label.getList().size());
            });
        }
        return trees;
    }

    /**
     * 功能描述: 查询组织摄像机树
     *
     * @param: MonitorTreeInfReq
     * @return: List<IndustryDeviceTree>
     * @auther: hued
     * @date: 2022年8月11日10:04:35
     * @version: release_v1.5
     * @requestID:
     */
    @Override
    @DataScope(tenantColumn = "ord.tenant_id", deptColumn = "sd.DEPT_ID",industryCode = "ord.industry_code",acceptColumn="ord.accept_node")
    public List<IndustryDeviceTree> queryMonitorIndDeptTree(MonitorTreeInfReq monitorTreeInfReq) {
        Set<String> orgList = new HashSet<>();
        //组织与绑定的设备
        List<IndustryDeviceTree> monitorList = baseMapper.queryMonitorIndOrdTree(monitorTreeInfReq);
        //获取有设备权限的所有组织编码
        for (IndustryDeviceTree treeVO : monitorList) {
            orgList.add(treeVO.getCode());
        }
        monitorList.forEach(org -> {
                    org.getList().forEach(dev -> {
                        dev.setOnLineNumber(dev.getList().stream().filter(deviceTreeVO -> Objects.equals(deviceTreeVO.getChannelStatus(), IndustryConstants.CHANNEL_ONLINE)).count());
                        dev.setNumber((long) dev.getList().size());
                    });
                    org.setDevOnLineNumber(org.getList().stream().filter(item -> Objects.equals(Constants.ZERO, item.getDeviceStatus())).count());
                    org.setDevNumber((long) org.getList().size());
                }
        );
        //查询有设备权限组织的上级组织直至更结点
        Set<String> orgIdList = getParentsList(orgList);
        //去重
        Set<String> orgResList = orgIdList.stream().filter(org -> !orgList.contains(org)).collect(Collectors.toSet());
        List<IndustryDeviceTree> parOrgList;
        if (!CollectionUtils.isEmpty(orgResList)) {
            DeviceReqDTO reqDTO = new DeviceReqDTO();
            reqDTO.setOrgIdList(orgResList);
            //父节点直至根节点集合
            parOrgList = baseMapper.getIndCustOrganInfo(reqDTO);
            monitorList.addAll(parOrgList);
        }
        //按照层级正序排列
        List<IndustryDeviceTree> sortList = monitorList.stream().sorted(Comparator.comparing(IndustryDeviceTree::getLevel)).collect(Collectors.toList());
        //递归更新数量
        List<IndustryDeviceTree> res = getDevNumTreeList(sortList, monitorList);

        //列表转换为树形结构
        //获取父节点，0表示父节点
        return res.stream().filter(e -> e.getLevel().equals(DeviceConstants.ONLINE_STATUS_ONE)).map(e -> {
            if (!CollectionUtils.isEmpty(e.getList())) {
                e.getList().addAll(getTreeChildrens(e, sortList));
            } else {
                e.setList(getTreeChildrens(e, sortList));
            }
            return e;
        }).collect(Collectors.toList());
    }

    /**
     * 功能描述: 通过站址编码获取设备信息
     *
     * @param: deviceIndReqDTO
     * @return: List<DeviceIndDTO>
     * @auther: hued
     * @date: 2022年8月11日10:04:35
     * @version: release_v1.5
     * @requestID:
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", deviceColumn = "od.device_code",industryCode = "od.industry_code",alarmSourceVal = "4",acceptColumn="od.accept_node")
    public List<DeviceIndDTO> queryDevicesBySite(DeviceIndReqDTO deviceIndReqDTO) {
        return baseMapper.queryDevicesBySite(deviceIndReqDTO);
    }

    /**
     * 功能描述: 查询摄像机图层
     *
     * @param: deviceLayerReq
     * @return: List<IndustryDeviceTree>
     * @auther: hued
     * @date: 2022年8月11日10:04:35
     * @version: release_v1.5
     * @requestID:
     */
    @Override
    @DataScope(deviceColumn = "ord.device_code",tenantColumn = "ord.tenant_id",industryCode = "ord.industry_code",alarmSourceVal = "4",acceptColumn="ord.accept_node")
    public List<IndustryDeviceTree> queryDeviceLayer(DeviceLayerReq deviceLayerReq) {
        return baseMapper.queryDeviceLayer(deviceLayerReq);
    }

    /**
     * 功能描述: 查询摄像机图层
     *
     * @param: deviceLayerReq
     * @return: List<IndustryDeviceTree>
     * @auther: hued
     * @date: 2022年8月11日10:04:35
     * @version: release_v1.5
     * @requestID:
     */
    @Override
    @DataScope(deviceColumn = "ord.device_code",tenantColumn = "ord.tenant_id",industryCode = "ord.industry_code",alarmSourceVal = "4",acceptColumn="ord.accept_node")
    public PageInfo<IndustryDeviceTree> pageDeviceLayer(DeviceLayerReq deviceLayerReq) {
        if (Objects.isNull(deviceLayerReq.getPageNum())) {
            deviceLayerReq.setPageNum(DeviceConstants.PAGE_NUM);
        }
        if (Objects.isNull(deviceLayerReq.getPageSize())) {
            deviceLayerReq.setPageSize(DeviceConstants.PAGE_SIZE);
        }
        PageHelper.startPage(deviceLayerReq.getPageNum(), deviceLayerReq.getPageSize());
        List<IndustryDeviceTree> siteList = baseMapper.queryDeviceSiteList(deviceLayerReq);
        PageInfo<IndustryDeviceTree> pageInfo = new PageInfo<>(siteList);
        if (!CollectionUtils.isEmpty(siteList)) {
            List<String> siteCodeList = siteList.stream().map(IndustryDeviceTree::getSiteCode).collect(Collectors.toList());
            deviceLayerReq.setSiteCodeList(siteCodeList);
            List<IndustryDeviceTree> list = baseMapper.queryDeviceLayer(deviceLayerReq);
            pageInfo.setList(list);
        }
        return pageInfo;
    }

    /**
     * 功能描述 查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序
     *
     * @param deviceLayerReq
     * @return IndustryDeviceTree
     * @author wanglei
     * @date 2022/12/15 10:33
     * @version: release_v1.5
     */
    @Override
    public List<IndustryDeviceTree> queryDevice3km(DeviceLayerReq deviceLayerReq) {
        List<IndustryDeviceTree> list = baseMapper.queryDevice3km(deviceLayerReq);
        List<IndustryDeviceTree> listResult = list.stream().filter(device ->
                device.getChannelInfoVOList() != null && device.getChannelInfoVOList().size() > DeviceConstants.ZERO_NUM).collect(Collectors.toList());
        return listResult;
    }


    /**
     * 查看设备属性
     *
     * @param deviceIndReqDTO
     * @return DeviceIndDTO
     * @author trk
     * @date 2022/4/1
     */
    @Override
    public DeviceIndDTO getDeviceBasicInfo(DeviceIndReqDTO deviceIndReqDTO) {
        return baseMapper.getDeviceBasicInfo(deviceIndReqDTO);
    }

    /**
     * 查看通道属性
     *
     * @param deviceIndReqDTO
     * @return DeviceIndDTO
     * @author trk
     * @date 2022/4/2
     */
    @Override
    public DeviceIndDTO getChannelBasicInfo(DeviceIndReqDTO deviceIndReqDTO) {
        return baseMapper.getChannelBasicInfo(deviceIndReqDTO);
    }

    @Override
    public List<DeviceModelDTO> getDeviceModel(DeviceIndReqDTO reqCond) {
        return baseMapper.getDeviceModel(reqCond);
    }

    @Override
    public List<DeviceInfoVO> linkageDevice(DeviceReqDTO deviceReqDTO) {
        return baseMapper.linkageDevice(deviceReqDTO);
    }

    @Override
    public DeviceModelDTO getDeviceCalibration(DeviceIndReqDTO deviceInfoReq) {
        return baseMapper.getDeviceCalibration(deviceInfoReq);
    }

    @Override
    public DeviceDetailVO queryVideoDetail(DeviceTreeVO deviceTreeVO) {
        return deviceInfoMapper.queryVideoDetail(deviceTreeVO.getDeviceCode(),deviceTreeVO.getChannelCode());
    }

    @Override
    public DeviceDetailVO queryVideoDeviceDetail(DeviceTreeVO deviceTreeVO) {
        return deviceInfoMapper.queryVideoDeviceDetail(deviceTreeVO.getDeviceCode());
    }

    @Override
    public List<Map<String, String>> getLableByDeviceCode(String deviceCode,String tenantId) {
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        return deviceInfoMapper.getLableByDeviceCode(deviceCode,tenantId,industryCode);
    }

    /**
     * 摄像机设备信息列表
     * @param entity
     * @return
     */
    @Override
    public List<DeviceAttrVO> getVideoDeviceList(BaseIndustryEntity entity) {
        return deviceInfoMapper.getVideoDeviceList(entity);
    }

    /**
     * 雷达设备信息列表
     * @param entity
     * @return
     */
    @Override
    public List<DeviceAttrVO> getRadarDeviceList(BaseIndustryEntity entity) {
        return deviceInfoMapper.getRadarDeviceList(entity);
    }

    /**
     * 物联设备信息列表
     * @param entity
     * @return
     */
    @Override
    public List<DeviceAttrVO> getSensorDeviceList(BaseIndustryEntity entity) {
        return deviceInfoMapper.getSensorDeviceList(entity);
    }

    /**
     * 卫星服务信息列表
     * @param entity
     * @return
     */
    @Override
    public List<ServiceAttrVO> getSatelliteServiceList(BaseIndustryEntity entity) {
        return deviceInfoMapper.getSatelliteServiceList(entity);
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    @Override
    public List<IndustryDeviceTree> querySiteCodeAndDevices(SiteRequestDTO siteRequestDTO) {
        List<IndustryDeviceTree> industryDeviceTrees = this.baseMapper.querySiteCodeAndDevices(siteRequestDTO);
        Set<String> fileUrls = new HashSet<>();
        industryDeviceTrees.forEach(site -> {
            site.getList().forEach(dev -> {
                fileUrls.add(dev.getImageUrl());
            });
        });
        //加密
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        industryDeviceTrees.forEach(site -> {
            site.getList().forEach(dev -> {
                if (!StringUtils.isEmpty(dev.getImageUrl())) {
                    dev.setImageUrl(result.get(dev.getImageUrl()));
                }
            });
        });
        return industryDeviceTrees;
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    @Override
    public List<IndustryDeviceTree> querySiteCodeAndDevicesForComponent(SiteRequestDTO siteRequestDTO) {
        //区域全不选
        if(Constants.TWO.equals(siteRequestDTO.getIsTownCode())){
            return new ArrayList<>();
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        siteRequestDTO.setTenantId(user.getTenantId());
        siteRequestDTO.setIndustryCode(user.getIndustryCode());
        siteRequestDTO.setLocalizationFlag(this.getLocalizationFlag());
        List<IndustryDeviceTree> industryDeviceTrees = this.baseMapper.querySiteCodeAndDevicesForComponent(siteRequestDTO);
        Set<String> fileUrls = new HashSet<>();
        List<String> deviceCodes = new ArrayList<>();
        industryDeviceTrees.forEach(site -> {
            site.getList().forEach(dev -> {
                fileUrls.add(dev.getImageUrl());
                deviceCodes.add(dev.getDeviceCode());
            });
        });
        //加密
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        industryDeviceTrees.forEach(site -> {
            site.getList().forEach(dev -> {
                if (!StringUtils.isEmpty(dev.getImageUrl())) {
                    dev.setImageUrl(result.get(dev.getImageUrl()));
                }
            });
        });

        //地图打点
        //(根据设备编码集合查询设备是否有未完结告警)
        AlarmOrderParmaDto alarmOrderParmaDto = new AlarmOrderParmaDto();
        alarmOrderParmaDto.setTenantId(siteRequestDTO.getTenantId());
        alarmOrderParmaDto.setIndustryCode(siteRequestDTO.getIndustryCode());
        alarmOrderParmaDto.setDeviceCodes(deviceCodes);
        log.info("querySiteCodeAndDevicesForComponent-getOrderNumByDeviceCode入参" + JSON.toJSONString(alarmOrderParmaDto));
        Result<List<OrderCompleteVO>> orderNumByDeviceCode = iTradeWarningOrderService.getOrderNumByDeviceCode(alarmOrderParmaDto);
        log.info("querySiteCodeAndDevicesForComponent-getOrderNumByDeviceCode出参" + JSON.toJSONString(orderNumByDeviceCode));

        if (ResultCode.SUCCESS.getCode() == orderNumByDeviceCode.getCode()) {
            List<OrderCompleteVO> data = orderNumByDeviceCode.getData();
            industryDeviceTrees.forEach(item -> {
                item.getList().forEach(device -> {
                    OrderCompleteVO deviceMap = data.stream().filter(map -> Objects.equals(device.getDeviceCode(), map.getDeviceCode())).findFirst().orElse(null);
                    String isCompleteFlag = Objects.nonNull(deviceMap) ? deviceMap.getIsCompleteFlag() : Constants.ZERO;
                    device.setIsCompleteFlag(isCompleteFlag);
                });
                //只要有一个未完结的就展示告警角标
                IndustryDeviceTree childTree = item.getList().stream().filter(child -> com.chinatower.platform.common.utils.StringUtils.equals(child.getIsCompleteFlag(), Constants.ONE)).findFirst().orElse(null);
                String completeFlag = Objects.nonNull(childTree) ? Constants.ONE : Constants.ZERO;
                item.setIsCompleteFlag(completeFlag);
            });
        }
        return industryDeviceTrees;
    }

    @Override
    @DataScope(deviceColumn = "ord.device_code",tenantColumn = "ord.tenant_id",industryCode = "ord.industry_code",alarmSourceVal = "4",acceptColumn="ord.accept_node")
    public DetectResultVO lookHere(DeviceLayerReq deviceLayerReq){
        log.info("看这里sdk请求报文={}",JSONObject.toJSONString(deviceLayerReq));
        DetectResultVO resultVO = new DetectResultVO();
        List<SiteVO> siteList = new ArrayList<>();
        List<UrlVO> urlList = new ArrayList<>();
        List<Map> realtimeUrlList = new ArrayList<>();

        //播放实时视频路数，最多播放5路
        deviceLayerReq.setUrlNum(deviceLayerReq.getUrlNum() > Constants.FIVE_NUM ? Constants.FIVE_NUM : deviceLayerReq.getUrlNum());
        int urlNum = deviceLayerReq.getUrlNum();

        //入参半径为空 或 超过10公里，默认3000
        String distance = deviceLayerReq.getDistance();
        if(StringUtils.isEmpty(distance) || Integer.parseInt(distance) > 10000){
            deviceLayerReq.setDistance("3000");
        }
        List<IndustryDeviceTree> list = baseMapper.queryDeviceLayer(deviceLayerReq);

        if (!CollectionUtils.isEmpty(list)) {
            int netType = Constants.TWO_NUM;
            // 网域类型字段
            AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.NETTYPE_PARAM);
            if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)){
                    netType = Integer.parseInt(sysConfig.getConfigValue());
                }
            }

            //循环站址
            for (IndustryDeviceTree siteInfo : list) {
                SiteVO siteVO = new SiteVO();
                BeanUtils.copyProperties(siteInfo, siteVO);
                //默认离线
                siteVO.setStatus(Constants.ONE);
                //站址下只有一个设备，且该设备状态为离线，将站址状态置为离线
                List<String> collect = siteInfo.getList().stream().map(IndustryDeviceTree::getDeviceStatus).collect(Collectors.toList());
                if(collect.contains(Constants.ZERO)){
                    siteVO.setStatus(Constants.ZERO);
                }
                siteList.add(siteVO);

                //取距离最近的urlNum个通道
                if (urlList.size() < urlNum) {
                    //循环站址下设备
                    device: for(IndustryDeviceTree deviceInfo : siteInfo.getList()) {
                        //循环站址下设备下通道
                        for(IndChannelInfoVO channelInfo : deviceInfo.getChannelInfoVOList()) {
                            if (channelInfo.getChannelStatus() == Constants.ZERO_NUM && (Constants.ZERO.equals(deviceInfo.getDeviceStatus()))) {
                                UrlVO urlVO = new UrlVO();
                                urlVO.setDeviceCode(deviceInfo.getDeviceCode());
                                urlVO.setChannelCode(channelInfo.getChannelCode());
                                urlVO.setLatitude(siteInfo.getLatitude());
                                urlVO.setLongitude(siteInfo.getLongitude());
                                urlList.add(urlVO);

                                Map map = new HashMap<>();
                                map.put(Constants.DEVICECODE,deviceInfo.getDeviceCode());
                                map.put(DeviceConstants.CHANNELCODE,channelInfo.getChannelCode());
                                map.put(DeviceConstants.STREAM_TYPE,Constants.ONE_NUM);
                                map.put(Constants.STREAM_UUID, IdUtils.fastSimpleUUID());
                                map.put(Constants.PROTOCOL_TYPE,Constants.NINE_NUM);
                                map.put(Constants.NET_TYPE,netType);
                                realtimeUrlList.add(map);
                            }

                            if (urlList.size() == urlNum) {
                                break device;
                            }
                        }
                    }
                }
            }

            if(!CollectionUtils.isEmpty(realtimeUrlList)) {
                //批量获取需要播放的设备通道的实时视频url
                log.info("========getVideoRealtimeUrlsForLookHere请求报文：{}", JSONObject.toJSONString(realtimeUrlList));
                Result result = videoQueryAPI.getVideoRealtimeUrlsForLookHere(realtimeUrlList);
                log.info("========getVideoRealtimeUrlsForLookHere返回报文：{}", JSONObject.toJSONString(result));
                if (result.getCode() == 200) {
                    List<LinkedHashMap> data = (List<LinkedHashMap>) result.getData();
                    urlList.stream().map(item -> {
                        data.stream().filter(area -> Objects.equals(item.getDeviceCode(), area.get(Constants.DEVICECODE)) &&
                                Objects.equals(item.getChannelCode(), area.get(DeviceConstants.CHANNELCODE))
                        ).forEach(area -> {
                            item.setStreamUrl((String) area.get(Constants.STREAM_URL));
                        });
                        return item;
                    }).collect(Collectors.toList());
                }

                //同步转向定位点
                this.turnToPtzByPointAngle(urlList, deviceLayerReq);

                //异步转向定位点
                //CompletableFutureHelper.orTimeout(
                //        CompletableFuture.runAsync(() -> this.turnToPtzByPointAngle(urlList, deviceLayerReq), threadPoolTaskExecutor)
                //                .exceptionally(e -> {
                //                    log.error("看这里转向定位点失败={}", e);
                //                    return null;
                //                }),
                //        3, TimeUnit.SECONDS).exceptionally(e -> {
                //            log.error("看这里转向定位点超时={}", e);
                //            return null;
                //        }
                //);
            }
        }
        resultVO.setSiteList(siteList);
        resultVO.setUrlList(urlList);
        log.info("看这里sdk返回报文={}",JSONObject.toJSONString(resultVO));
        return resultVO;
    }

    public void turnToPtzByPointAngle(List<UrlVO> urlList,DeviceLayerReq deviceLayerReq){
        for (UrlVO urlVO : urlList) {
            if(!StringUtils.isEmpty(urlVO.getStreamUrl()) && !StringUtils.isEmpty(urlVO.getLongitude()) && !StringUtils.isEmpty(urlVO.getLatitude())){
                VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
                videoPtzQueryReq.setPointLon(deviceLayerReq.getLongitude());
                videoPtzQueryReq.setPointLat(deviceLayerReq.getLatitude());
                videoPtzQueryReq.setCameraLon(Double.parseDouble(urlVO.getLongitude()));
                videoPtzQueryReq.setCameraLat(Double.parseDouble(urlVO.getLatitude()));
                videoPtzQueryReq.setDeviceCode(urlVO.getDeviceCode());
                videoPtzQueryReq.setChannelCode(urlVO.getChannelCode());
                Result<PresetData> presetDataResult = videoControlAPI.turnToPtzByPointAngle(videoPtzQueryReq);
                log.info("看这里转向定位点报文，入参{}，出参{}",JSONObject.toJSONString(videoPtzQueryReq),JSONObject.toJSONString(presetDataResult));
                if(presetDataResult.getCode() == 200 && !ObjectUtils.isEmpty(presetDataResult.getData())){
                    PresetData data = presetDataResult.getData();
                    urlVO.setPan(data.getPan());
                    urlVO.setTilt(data.getTilt());
                    urlVO.setZoom(data.getZoom());
                }
            }
        }
    }

    /**
     * 根据关键字搜索查询摄像机
     * @param deviceComprehensiveReq
     * @return
     */
    @Override
    @DataScope(deviceColumn = "od.device_code",tenantColumn = "od.tenant_id",industryCode = "od.industry_code",alarmSourceVal = "4",acceptColumn="od.accept_node")
    public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq) {
        PageHelper.startPage(deviceComprehensiveReq.getPageNum(), deviceComprehensiveReq.getPageSize());
        List<DeviceComprehensiveVO> list = baseMapper.queryDeviceListByKeyWord(deviceComprehensiveReq);
        //obs加密处理
        if (!CollectionUtils.isEmpty(list)) {
            List<String> imgUrlList = list.stream().map(DeviceComprehensiveVO::getImageUrl).collect(Collectors.toList());
            Map<String, String> stringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(imgUrlList);
            list.forEach(device -> {
                if (!StringUtils.isEmpty(device.getImageUrl())) {
                    device.setImageUrl(stringMap.get(device.getImageUrl()));
                }
            });
        }
        PageInfo<DeviceComprehensiveVO> pageInfo = new PageInfo<>(list);
        Result<PageInfo<DeviceComprehensiveVO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    /**
     * 查询范围内距离最近的设备，以树的形式返回
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> linkageDeviceTree(DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> treeList = baseMapper.linkageDeviceTree(deviceReqDTO);
        this.formatTreeList(treeList, deviceReqDTO.getDeviceOrChannel());
        return treeList;
    }

    /**
     *  该接口查询摄像机树，不缺分用户级权限
     * @param deviceReqDTO
     * @return
     */
    @DataScope( tenantColumn="dataTable.tenant_id",alarmSourceVal = "4",
            areaPro="dataTable.province_code",areaCity="dataTable.city_code", industryCode="dataTable.industry_code",acceptColumn="dataTable.accept_node")
    @Override
    public List<DeviceTreeVO> getMonitorTreeWithOutUserId(DeviceReqDTO deviceReqDTO) {
        log.info("查询摄像头树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> treeList = baseMapper.getMonitorTree(deviceReqDTO);
        //格式化区域树
        this.formatTreeList(treeList, deviceReqDTO.getDeviceOrChannel());
        //log.info("查询摄像头树---返回报文------{}", treeList);
        return treeList;
    }

    @Override
    public Page<SnapShotSimpleRes> querySnapShotSimpleForAppByChannelCode(SiteRequestDTO siteRequestDTO) {
        Integer pageNum = siteRequestDTO.getPageNum();
        Integer pageSize = siteRequestDTO.getPageSize();
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        //由于mysql的时候pageNo是1开始的，mongodb需要减一
        siteRequestDTO.setPageNum(pageNum-1);
        siteRequestDTO.setPageSize(pageSize);
        //获取分页参数
        Page page = new Page(siteRequestDTO.getPageNum(), siteRequestDTO.getPageSize());
        QueryEntity query = new QueryEntity(VideoConstants.SNAP_SHOT_SIMPLE_COLLECTION, SnapShotSimpleRes.class);
        query.eq(VideoConstants.CHANNELCODE, siteRequestDTO.getChannelCode());
        query.eq(VideoConstants.DEVICECODE, siteRequestDTO.getDeviceCode());
        if(siteRequestDTO.getEndTime() != null && siteRequestDTO.getStartTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timeStr = formatter.format(siteRequestDTO.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) + ".999";
            DateTimeFormatter formatterWithMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatterWithMillis);
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            query.between(VideoConstants.LASTURLDATE, siteRequestDTO.getStartTime(), date);
        }

        // 排序
        SortEntity sortEntity = new SortEntity();
        sortEntity.desc(VideoConstants.LASTURLDATE);
        page.setSortEntity(sortEntity);

        Page<SnapShotSimpleRes> pageSnapShotSimpleRes = snapShotSimpleMapper.findPage(query, page,null, MongoDBType.PRIMARY);
        List<SnapShotSimpleRes> resultList = pageSnapShotSimpleRes.getList();
        log.info("查询MongoDB简约模式抓图列表---返回报文------{}", JSONObject.toJSONString(resultList));
        if(!CollectionUtils.isEmpty(resultList)) {
            //url去空判断
            List<String> fileUrls = resultList.stream().map(SnapShotSimpleRes::getUrl).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            // 处理Obs文件URL加密返回
            log.info("fileUrls请求=" + JSONObject.toJSONString(fileUrls));
            Map<String, String> fileResult = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
            log.info("fileUrls返回=" + JSONObject.toJSONString(fileResult));
            for (SnapShotSimpleRes snapShotSimpleRes : resultList) {
                    snapShotSimpleRes.setUrl(fileResult.get(snapShotSimpleRes.getUrl()));
            }
        }
        return pageSnapShotSimpleRes;
    }

    @Override
    public List<OrderDevicesVO> getOrderDevices(DeviceIndReqDTO deviceIndReqDTO) {
        return deviceInfoMapper.getOrderDevices(deviceIndReqDTO);
    }

    @Override
    public String getConfigByType(String configKey) {
        return deviceInfoMapper.getConfigByType(configKey);
    }

    /**
     * 获取是否为国产化标识 0-否、1-是
     * @return String
     */
    private String getLocalizationFlag() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(com.chinatower.event.constants.Constants.LOCALIZATION_FLAG);
        try {
            AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
            if (Objects.nonNull(configInfo) && Objects.nonNull(configInfo.get(com.chinatower.event.constants.Constants.RESULT_DATA))) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(com.chinatower.event.constants.Constants.RESULT_DATA)));
                return jsonObject.getString(com.chinatower.event.constants.Constants.PARAM_CONFIG_VALUE);
            }
        } catch (Exception e) {
            log.error("获取国产化标识配置失败", e);
        }
        return "0";
    }

    @Override
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    public List<DeviceAggregationLayerRes> queryDeviceAggregation(DevicesForGlobalReq devicesForGlobalReq) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        devicesForGlobalReq.setTenantId(user.getTenantId());
        devicesForGlobalReq.setIndustryCode(user.getIndustryCode());
        devicesForGlobalReq.setPlatformCode(user.getPlatformCode());
        List<DeviceAggregationLayerRes> results = baseMapper.queryDeviceAggregation(devicesForGlobalReq);
        log.info("设备聚合层结果：{}", JSONObject.toJSONString(results));
        if(!CollectionUtils.isEmpty(results)){
            Set<String> areaCodes = results.stream()
                    .flatMap(res -> Stream.of(res.getProvinceId(), res.getCityId(), res.getCountyId(), res.getTownCode()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Map<String, BasicDeviceDTO> areaInfoMap = baseMapper.queryAreaInfo(new ArrayList<>(areaCodes)).stream()
                    .collect(Collectors.toMap(BasicDeviceDTO::getAreaCode, dto -> dto));

            for (DeviceAggregationLayerRes vo : results) {
                String[] ids = {vo.getProvinceId(), vo.getCityId(), vo.getCountyId(), vo.getTownCode()};
                for (String id : ids) {
                    BasicDeviceDTO deviceDTO = areaInfoMap.get(id);
                    if (deviceDTO != null) {
                        vo.setLongitude(deviceDTO.getLongitude());
                        vo.setLatitude(deviceDTO.getLatitude());
                        vo.setAreaName(deviceDTO.getAreaName());
                        break;
                    }
                }
            }
        }
        return results;
    }

    @Override
    @DataScope(tenantColumn="ord.tenant_id",deviceColumn = "ord.device_code", industryCode = "ord.industry_code",alarmSourceVal = "4",acceptColumn="ord.accept_node")
    public List<OrderDeviceCategoryRes> queryOrderDeviceCategory(SiteRequestDTO siteRequestDTO){
        return this.baseMapper.queryOrderDeviceCategory(siteRequestDTO);
    }

    @Override
    @DataScope(tenantColumn="ord.tenant_id",deviceColumn = "ord.device_code", industryCode = "ord.industry_code",alarmSourceVal = "4",acceptColumn="ord.accept_node")
    public List<DeviceCommonInfoVO> queryOrderDeviceModel(SiteRequestDTO siteRequestDTO){
        return this.baseMapper.queryOrderDeviceModel(siteRequestDTO);
    }

    @Override
    @DataScope(tenantColumn="t1.tenant_id",industryCode="t1.industry_code",deviceColumn = "t1.device_code",acceptColumn="t1.accept_node")
    public List<ScopeDeviceInfoVO> queryScopeDeviceList(ScopeDeviceInfoVO siteRequestDTO) {
        return this.queryScopeDeviceListCommon(siteRequestDTO);
    }

    @Override
    public List<ScopeDeviceInfoVO> queryScopeDeviceListTower(ScopeDeviceInfoVO siteRequestDTO) {
        siteRequestDTO.setPlatformCode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
        return this.queryScopeDeviceListCommon(siteRequestDTO);
    }


    private List<ScopeDeviceInfoVO> queryScopeDeviceListCommon(ScopeDeviceInfoVO siteRequestDTO) {
        int limit = 300;
        String limitStr = deviceInfoMapper.getSysConfig("device_list_limit");
        if(!StringUtils.isEmpty(limitStr)){
            limit = Integer.parseInt(limitStr);
        }
        List<ScopeDeviceInfoVO> resultList = new ArrayList<>();
        siteRequestDTO.setLimit(limit);
        Integer alarmSuppressId = siteRequestDTO.getAlarmSuppressId();
        if(StringUtils.isEmpty(siteRequestDTO.getDeviceName()) && !ObjectUtils.isEmpty(alarmSuppressId)){
            siteRequestDTO.setFlag(Constants.ONE);
            List<ScopeDeviceInfoVO> checkedList = this.baseMapper.queryScopeDeviceList(siteRequestDTO);

            limit = limit - checkedList.size();
            if(limit > 0){
                siteRequestDTO.setLimit(limit);
                siteRequestDTO.setFlag(Constants.ZERO);
                List<ScopeDeviceInfoVO> unCheckedList = this.baseMapper.queryScopeDeviceList(siteRequestDTO);
                resultList.addAll(unCheckedList);
            }

            resultList.addAll(checkedList);
            return resultList;
        }
        return this.baseMapper.queryScopeDeviceList(siteRequestDTO);
    }

    @Override
    @DataScope(deviceColumn = "t5.device_code", tenantColumn = "t5.tenant_id", industryCode = "t5.industry_code")
    public List<IndustryAlgorithmDTO> queryAlgorithmByConfig(IndustryAlgorithmQueryVo industryAlgorithmQueryVo){
        return this.baseMapper.queryAlgorithmByConfig(industryAlgorithmQueryVo);
    }
}
