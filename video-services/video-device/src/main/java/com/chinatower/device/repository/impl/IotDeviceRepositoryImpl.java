package com.chinatower.device.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.IotDeviceDO;
import com.chinatower.device.dataobject.OrganIotDevRelDO;
import com.chinatower.device.dataobject.RadarDeviceRelDO;
import com.chinatower.device.dataobject.SensorAttrResultVO;
import com.chinatower.device.dataobject.SensorDeviceTypeVO;
import com.chinatower.device.dataobject.SensorMonitorInfo;
import com.chinatower.device.dataobject.SensorPeriodPageResultVO;
import com.chinatower.device.dataobject.TenantIotDeviceRelDO;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.*;
import com.chinatower.device.mongodb.RadarDeviceConfigRelMongoDAO;
import com.chinatower.device.mongodb.SensorAttrValueAllMongoDAO;
import com.chinatower.device.mongodb.SensorAttrValueMongoDAO;
import com.chinatower.device.mongodb.dto.RadarDeviceConfigRelDTO;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.repository.IOrgIotDevRelRepository;
import com.chinatower.device.repository.ITenantIotDeviceRelRepository;
import com.chinatower.device.service.ITenantDeviceAttachmentService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmOrderParmaDto;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.industry.dto.DeviceLayerReq;
import com.chinatower.industry.dto.RadarDeviceReqDTO;
import com.chinatower.industry.dto.SensorDeviceCountReqDTO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.bulk.BulkWriteResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 物联设备表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Slf4j
@Service
public class IotDeviceRepositoryImpl extends ServiceImpl<IotDeviceMapper, IotDeviceDO> implements IIotDeviceRepository {

    @Autowired
    IOrgIotDevRelRepository orgIotDevRelRepository;
    @Autowired
    ITenantIotDeviceRelRepository tenantIotDeviceRelRepository;
    @Autowired
    SensorAttrValueMongoDAO<Object> sensorAttrValueMongoDAO;
    @Autowired
    IDeviceInfoRepository deviceInfoRepository;
    @Autowired
    RadarDeviceRelMapper radarDeviceRelMapper;
    @Autowired
    private RadarDeviceConfigRelMongoDAO radarDeviceConfigRelMongoDAO;
    @Autowired
    DeviceInfoMapper deviceInfoMapper;
    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private SensorAttrValueAllMongoDAO sensorAttrValueAllMongoDAO;

    @Resource
    private SystemFeignClient systemFeignClient;
    @Autowired
    private HornDeviceMapper hornDeviceMapper;

    @Resource
    private TenantDeviceAttachmentMapper tenantDeviceAttachmentMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private ITenantDeviceAttachmentService tenantDeviceAttachmentService;

    @Resource
    private IotDeviceMapper iotDeviceMapper;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    private static final String LAT_LONG_CAN_NOT_EMPTY_MSG = "经纬度、探索半径参数不能为空";

    private static final String SPECIES_MSG = "species";

    private static final String SPECIES_NUM_MSG = "speciesNum";


    /**
     *
     * @param deviceInfoVO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    public List<DeviceTreeVO> getIotMonitorTree(IotDeviceVO deviceInfoVO) {
        log.info("----getIotMonitorTree---req: {}", deviceInfoVO);
        return baseMapper.getIotMonitorTree(deviceInfoVO);
    }

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    public List<DeviceTreeVO> getIotMonitorOrgTree(IotDeviceVO deviceInfoVO) {
        List<DeviceTreeVO> deviceTreeVOList = baseMapper.getIotMonitorOrgTree(deviceInfoVO);
        setIsCompleteFlag(deviceTreeVOList);
        return formatOrgTreeList(deviceTreeVOList);
    }

    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code",acceptColumn = "od.accept_node", deviceColumn = "od.device_code", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryIotDeviceInfo(IotDeviceVO iotDeviceVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser tokenUser = currentUser.getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = hornDeviceMapper.selectUserOnlyByUserName(tokenUser.getUserName());
        iotDeviceVO.setUserId(user.getUserId());
        if (!StringUtils.isEmpty(iotDeviceVO.getAppFlag()) && iotDeviceVO.getAppFlag().equals(Constants.ONE)) {
            IotDeviceVO deviceVO = baseMapper.queryIotDeviceByCode(iotDeviceVO);
            iotDeviceVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_8));
            iotDeviceVO.setIndustryCode(tokenUser.getIndustryCode());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            iotDeviceVO.setDeviceCode(deviceVO.getDeviceCode());

            //查询设备关联的组织信息
            List<OrganDeviceRelVO> organDeviceRelVOS = orgIotDevRelRepository.queryOrgIotDevRels(iotDeviceVO);
            if (!CollectionUtils.isEmpty(organDeviceRelVOS)){
                deviceVO.setOrgRels(organDeviceRelVOS.stream().filter(organDeviceRelVO -> deviceVO.getDeviceCode().equals(organDeviceRelVO.getDeviceCode())).collect(Collectors.toList()));
                deviceVO.setOrgName(deviceVO.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }
            List<IotDeviceVO> iotDeviceVOList = Lists.newArrayList();
            iotDeviceVOList.add(deviceVO);
            return iotDeviceVOList;
        } else {
            List<IotDeviceVO> iotDeviceVOList = baseMapper.queryIotDeviceInfo(iotDeviceVO);
            //查询上传的附件信息
            dealObsFileUrlBatch(iotDeviceVOList, IndustryConstants.EIGHT_NUM);
            return iotDeviceVOList;
        }
    }

    /**
     * 对物联设备附件做批量加密处理
     * @param iotDeviceVOList
     * @return
     */
    private void dealObsFileUrlBatch(List<IotDeviceVO> iotDeviceVOList, int deviceType) {
        if (!CollectionUtils.isEmpty(iotDeviceVOList)) {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            List<String> deviceCodeList = iotDeviceVOList.stream().map(IotDeviceVO::getDeviceCode).collect(Collectors.toList());
            // 1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达 12-执法记录仪
            Map<String, List<String>> encryptUrlMap = tenantDeviceAttachmentService.getFileUrlByDeviceCodeList(user.getTenantId(), user.getIndustryCode(), deviceCodeList, deviceType);
            if (MapUtils.isNotEmpty(encryptUrlMap)) {
                iotDeviceVOList.forEach(vo -> {
                    List<String> encryptUrlList = encryptUrlMap.get(vo.getDeviceCode());
                    if (!CollectionUtils.isEmpty(encryptUrlList)) {
                        // 增加默认排序，视频放前面
                        encryptUrlList.sort((o1, o2) -> {
                            boolean o1ContainsMp4 = o1.contains(DeviceSyncConstants.MP4_SUFFIX);
                            boolean o2ContainsMp4 = o2.contains(DeviceSyncConstants.MP4_SUFFIX);
                            if (o1ContainsMp4 && o2ContainsMp4) {
                                return o1.compareTo(o2);
                            } else if (o1ContainsMp4) {
                                return -1;
                            } else if (o2ContainsMp4) {
                                return 1;
                            } else {
                                return o1.compareTo(o2);
                            }
                        });
                    }
                    vo.setFileUrlList(encryptUrlList);
                });
            }
        }
    }

    /**
     * 对物联设备附件做批量加密处理
     * @param iotDeviceVOList
     * @return
     */
    private void dealChildListObsFileUrlBatch(List<IotDeviceVO> iotDeviceVOList, int deviceType) {
        if (!CollectionUtils.isEmpty(iotDeviceVOList)) {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            List<String> deviceCodeList = Lists.newArrayList();
            for (IotDeviceVO iotDeviceVO : iotDeviceVOList) {
                if (!CollectionUtils.isEmpty(iotDeviceVO.getList())) {
                    deviceCodeList.addAll(iotDeviceVO.getList().stream().map(IotDeviceVO::getDeviceCode).collect(Collectors.toList()));
                }
            }
            // 1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达 12-执法记录仪
            Map<String, List<String>> encryptUrlMap = tenantDeviceAttachmentService.getFileUrlByDeviceCodeList(user.getTenantId(), user.getIndustryCode(), deviceCodeList, deviceType);
            if (MapUtils.isNotEmpty(encryptUrlMap)) {
                iotDeviceVOList.forEach(vo -> {
                    if (!CollectionUtils.isEmpty(vo.getList())) {
                        vo.getList().forEach(device -> {
                            List<String> encryptUrlList = encryptUrlMap.get(device.getDeviceCode());
                            if (!CollectionUtils.isEmpty(encryptUrlList)) {
                                // 增加默认排序，视频放前面
                                encryptUrlList.sort((o1, o2) -> {
                                    boolean o1ContainsMp4 = o1.contains(DeviceSyncConstants.MP4_SUFFIX);
                                    boolean o2ContainsMp4 = o2.contains(DeviceSyncConstants.MP4_SUFFIX);
                                    if (o1ContainsMp4 && o2ContainsMp4) {
                                        return o1.compareTo(o2);
                                    } else if (o1ContainsMp4) {
                                        return -1;
                                    } else if (o2ContainsMp4) {
                                        return 1;
                                    } else {
                                        return o1.compareTo(o2);
                                    }
                                });
                            }
                            device.setFileUrlList(encryptUrlList);
                        });
                    }
                });
            }
        }
    }

    /**
     * 对物联设备附件做批量加密处理
     * @param deviceSiteVOList
     * @return
     */
    private void encryptObsFileUrlBatch(List<DeviceSiteVO> deviceSiteVOList, int deviceType) {
        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            List<String> deviceCodeList = Lists.newArrayList();
            for (DeviceSiteVO deviceSiteVO : deviceSiteVOList) {
                if (!CollectionUtils.isEmpty(deviceSiteVO.getList())) {
                    deviceCodeList.addAll(deviceSiteVO.getList().stream().map(DeviceSiteVO::getDeviceCode).collect(Collectors.toList()));
                }
            }
            // 1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达 12-执法记录仪
            Map<String, List<String>> encryptUrlMap = tenantDeviceAttachmentService.getFileUrlByDeviceCodeList(user.getTenantId(), user.getIndustryCode(), deviceCodeList, deviceType);
            if (MapUtils.isNotEmpty(encryptUrlMap)) {
                deviceSiteVOList.forEach(vo -> {
                    if (!CollectionUtils.isEmpty(vo.getList())) {
                        vo.getList().forEach(device -> {
                            List<String> encryptUrlList = encryptUrlMap.get(device.getDeviceCode());
                            if (!CollectionUtils.isEmpty(encryptUrlList)) {
                                // 增加默认排序，视频放前面
                                encryptUrlList.sort((o1, o2) -> {
                                    boolean o1ContainsMp4 = o1.contains(DeviceSyncConstants.MP4_SUFFIX);
                                    boolean o2ContainsMp4 = o2.contains(DeviceSyncConstants.MP4_SUFFIX);
                                    if (o1ContainsMp4 && o2ContainsMp4) {
                                        return o1.compareTo(o2);
                                    } else if (o1ContainsMp4) {
                                        return -1;
                                    } else if (o2ContainsMp4) {
                                        return 1;
                                    } else {
                                        return o1.compareTo(o2);
                                    }
                                });
                            }
                            device.setFileUrlList(encryptUrlList);
                        });
                    }
                });
            }
        }
    }

    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code",acceptColumn = "od.accept_node", deviceColumn = "od.device_code", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryRadarDeviceInfo(IotDeviceVO iotDeviceVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser tokenUser = currentUser.getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = hornDeviceMapper.selectUserOnlyByUserName(tokenUser.getUserName());
        iotDeviceVO.setUserId(user.getUserId());
        if (!StringUtils.isEmpty(iotDeviceVO.getAppFlag()) && iotDeviceVO.getAppFlag().equals(Constants.ONE)){
            IotDeviceVO radarDevice = baseMapper.queryRadarDeviceByCode(iotDeviceVO);
            iotDeviceVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_10));
            iotDeviceVO.setIndustryCode(tokenUser.getIndustryCode());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            //查询设备关联的组织信息
            List<OrganDeviceRelVO> organDeviceRelVOS = orgIotDevRelRepository.queryOrgIotDevRels(iotDeviceVO);
            if (!CollectionUtils.isEmpty(organDeviceRelVOS)){
                radarDevice.setOrgRels(organDeviceRelVOS.stream().filter(organDeviceRelVO -> radarDevice.getDeviceCode().equals(organDeviceRelVO.getDeviceCode())).collect(Collectors.toList()));
                radarDevice.setOrgName(radarDevice.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }
            List<IotDeviceVO> radarDeviceVOList = Lists.newArrayList();
            radarDeviceVOList.add(radarDevice);
            return radarDeviceVOList;
        }else {
            List<IotDeviceVO> radarDeviceVOList = baseMapper.queryRadarDeviceInfo(iotDeviceVO);
            //查询上传的附件信息
            dealObsFileUrlBatch(radarDeviceVOList, IndustryConstants.TEN_NUM);
            return radarDeviceVOList;
        }
    }

    /**
     * 格式化设备组织树
     * @param monitorList
     * @return
     */
    public List<DeviceTreeVO> formatOrgTreeList(List<DeviceTreeVO> monitorList) {
        List<DeviceTreeVO> treeList;
        Set<String> orgList = new HashSet<>();
        //获取有设备权限的所有组织编码
        monitorList.forEach(e -> orgList.add(e.getCode()));
        // 设置数量
        monitorList.forEach(info -> {
            info.getList().forEach(dev -> {
                dev.setStatus(Constants.ZERO.equals(dev.getStatus()) ? Constants.ZERO : Constants.ONE);
            });
            info.setOnlineNum((int) info.getList()
                    .stream()
                    .filter(devCodeVo -> Constants.ZERO.equals(devCodeVo.getStatus())).count());
            info.setNum(info.getList().size());
            info.getList().forEach(device -> {
                if (device.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                    device.setIsMonitorNum(1);
                }
            });
            info.setIsMonitorNum(info.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
            if (info.getNum() == info.getIsMonitorNum()) {
                info.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
            }
        });
        //查询有设备权限组织的上级组织直至更结点
        Set<String> orgIdList = getParentsList(orgList);
        //去重
        Set<String> orgResList = orgIdList.stream().filter(org -> !orgList.contains(org)).collect(Collectors.toSet());
        List<DeviceTreeVO> parOrgList;
        if (!CollectionUtils.isEmpty(orgResList)) {
            DeviceReqDTO reqDTO = new DeviceReqDTO();
            reqDTO.setOrgIdList(orgResList);
            //父节点直至根节点集合
            parOrgList = deviceInfoMapper.getCustOrganInfo(reqDTO);
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
        return treeList;
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
            Set<DeviceTreeVO> deviceList = new HashSet<>();
            resList.forEach(org -> {
                if (!CollectionUtils.isEmpty(org.getList())) {
                    org.getList().forEach(dev -> {
                        DeviceTreeVO monVO = new DeviceTreeVO();
                        monVO.setDeviceCode(dev.getDeviceCode());
                        monVO.setStatus(dev.getStatus());
                        deviceList.add(monVO);
                    });
                }
            });
            sortVO.setNum(deviceList.size());
            sortVO.setOnlineNum((int) deviceList.stream().filter(mon -> Constants.ZERO.equals(mon.getStatus())).count());
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

    /**
     *
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code",acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryIotDeviceBySite(IotDeviceVO iotDeviceVO) {
        List<IotDeviceVO> iotDeviceVOList = baseMapper.queryIotDeviceBySite(iotDeviceVO);
        //查询上传的附件信息,根据站址查询如果只返回一条结果就直接取该接口的返回数据，如果有多条还要查询详情接口。所以只需要对返回一条接口的情况查询附件列表信息。
        dealChildListObsFileUrlBatch(iotDeviceVOList, IndustryConstants.EIGHT_NUM);
        return iotDeviceVOList;
    }

    /**
     *
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code",acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryIotDeviceByArea(IotDeviceVO iotDeviceVO) {
        return baseMapper.queryIotDeviceByArea(iotDeviceVO);
    }

    /**
     * 物联设备图层打点节点
     * @param siteRequestDTO
     * @return
     */
    @Override
    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code",acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public List<IndustryDeviceTree> queryIotDeviceSites(SiteRequestDTO siteRequestDTO) {
        return baseMapper.queryIotDeviceSites(siteRequestDTO);
    }

    /**
     * 分页查询物联设备图层打点节点
     * @param siteRequestDTO
     * @return
     */
    @Override
    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code",acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public PageInfo<IndustryDeviceTree> queryIotRadarDeviceSites(SiteRequestDTO siteRequestDTO) {
        if (Objects.isNull(siteRequestDTO.getPageSize())) {
            siteRequestDTO.setPageSize(10);
        }
        if (Objects.isNull(siteRequestDTO.getPageNum())) {
            siteRequestDTO.setPageNum(1);
        }
        PageHelper.startPage(siteRequestDTO.getPageNum(), siteRequestDTO.getPageSize());
        //分页查询站址数量
        List<IndustryDeviceTree> list = baseMapper.queryIotRadarSiteList(siteRequestDTO);
        PageInfo<IndustryDeviceTree> pageInfo = new PageInfo<>(list);
        if (!CollectionUtils.isEmpty(list)) {
            List<String> siteCodeList = list.stream().map(IndustryDeviceTree::getSiteCode).collect(Collectors.toList());
            siteRequestDTO.setSiteCodeList(siteCodeList);
            //查询图层
            List<IndustryDeviceTree> industryDeviceTreeList = baseMapper.queryIotRadarDeviceSites(siteRequestDTO);
            pageInfo.setList(industryDeviceTreeList);
        }
        return pageInfo;
    }

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10",
            areaPro="m.province_id", areaCity="m.city_id")
    public List<DeviceTreeVO> getRadarMonitorTree(IotDeviceVO deviceInfoVO) {
        return baseMapper.getRadarMonitorTree(deviceInfoVO);
    }

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code",  acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    public List<DeviceTreeVO> getRadarMonitorOrgTree(IotDeviceVO deviceInfoVO) {
        List<DeviceTreeVO> deviceTreeVOList = baseMapper.getRadarMonitorOrgTree(deviceInfoVO);
        setIsCompleteFlag(deviceTreeVOList);
        return formatOrgTreeList(deviceTreeVOList);
    }

    /**
     * 设备得告警事件是否完结
     * @param deviceTreeVOList
     * @return
     */
    private void setIsCompleteFlag(List<DeviceTreeVO> deviceTreeVOList) {
        if (CollectionUtils.isNotEmpty(deviceTreeVOList)) {
            List<String> deviceCodeList = Lists.newArrayList();
            deviceTreeVOList.forEach(org -> {
                if (CollectionUtils.isNotEmpty(org.getList())) {
                    List<String> list = org.getList().stream().filter(device -> StringUtils.isNotBlank(device.getDeviceCode())).map(DeviceTreeVO::getDeviceCode).collect(Collectors.toList());
                    deviceCodeList.addAll(list);
                }
            });
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            AlarmOrderParmaDto dto = new AlarmOrderParmaDto();
            dto.setDeviceCodes(deviceCodeList);
            dto.setTenantId(sysUser.getTenantId());
            dto.setIndustryCode(sysUser.getIndustryCode());
            log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 请求参数：{}", dto);
            Result<List<OrderCompleteVO>> result = iTradeWarningOrderService.getOrderNumByDeviceCode(dto);
            log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 返回参数：{}", result);
            if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                deviceTreeVOList.forEach(org -> {
                    if (CollectionUtils.isNotEmpty(org.getList())) {
                        org.getList().forEach(device -> {
                            String isCompleteFlag = Constants.ZERO;
                            if (CollectionUtils.isNotEmpty(result.getData())) {
                                OrderCompleteVO orderCompleteVO = result.getData().stream().filter(vo -> Objects.equals(device.getDeviceCode(), String.valueOf(vo.getDeviceCode()))).findFirst().orElse(null);
                                isCompleteFlag = Objects.nonNull(orderCompleteVO) ? String.valueOf(orderCompleteVO.getIsCompleteFlag()) : Constants.ZERO;
                            }
                            device.setIsCompleteFlag(isCompleteFlag);
                        });
                    }
                });
            }
        }

    }

    /**
     *
     * @param iotDeviceVO
     * @return
     */
    @Override
    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryRadarDeviceBySite(IotDeviceVO iotDeviceVO) {
        List<IotDeviceVO> iotDeviceVOList = baseMapper.queryRadarDeviceBySite(iotDeviceVO);
        //查询上传的附件信息
        dealChildListObsFileUrlBatch(iotDeviceVOList, IndustryConstants.TEN_NUM);
        return iotDeviceVOList;
    }

    /**
     *
     * @param iotDeviceVO
     * @return
     */
    @Override
    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryRadarDeviceByArea(IotDeviceVO iotDeviceVO) {
        return baseMapper.queryRadarDeviceByArea(iotDeviceVO);
    }

    @Override
    public List<DeviceTreeVO> getDeviceMonitorTree(DeviceReqDTO deviceInfoDTO) {
        return baseMapper.getDeviceMonitorTree(deviceInfoDTO);
    }

    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "4")
    public List<DeviceTreeVO> getDeviceChannelMonitorTree(DeviceReqDTO deviceInfoDTO, String platformCode) {
        return baseMapper.getDeviceChannelMonitorTree(deviceInfoDTO, platformCode);
    }

    @Override
    public List<DeviceTreeVO> getDeviceChannelList(DeviceReqDTO deviceInfoDTO, String platformCode) {
        return baseMapper.getDeviceChannelList(deviceInfoDTO, platformCode);
    }

    @Override
    public List<IotDeviceVO> queryIotDeviceListNew(IotDeviceVO iotDevice) {

        if(StringUtils.isNotEmpty(iotDevice.getIsMonitorFlag())) {
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(SecurityUtils.getLoginUser().getUser().getUserName());
            iotDevice.setUserId(user.getUserId());
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<IotDeviceVO> iotDeviceList;
        if (iotDevice.getCategoryCode() != null && Objects.equals(iotDevice.getCategoryCode(),Constants.ONE_NUM)){
            iotDeviceList = baseMapper.queryRadarListPage(iotDevice);
            iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_10));
        }else {
            iotDeviceList = baseMapper.queryIotDeviceList(iotDevice);
            iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_8));
        }
        if (iotDeviceList.isEmpty()) {
            return iotDeviceList;
        }
        iotDevice.setIndustryCode(user.getIndustryCode());
        List<OrganDeviceRelVO> orgIotDevRels = orgIotDevRelRepository.queryOrgIotDevRels(iotDevice);
        if (!orgIotDevRels.isEmpty()) {
            for (IotDeviceVO iotDeviceVO : iotDeviceList) {
                iotDeviceVO.setOrgRels(orgIotDevRels.stream().filter(e -> iotDeviceVO.getDeviceCode().equals(e.getDeviceCode())).collect(Collectors.toList()));
                iotDeviceVO.setOrgName(iotDeviceVO.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }
        }
        return iotDeviceList;
    }

    @Override
    public List<IotDeviceVO> queryIotRadarDeviceList(IotDeviceVO iotDevice) {
        iotDevice.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<String> radarDeviceTypeList = iotDevice.getRadarDeviceTypeList();
        List<IotDeviceVO> list = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(iotDevice.getIotDeviceTypeList())) {
            iotDevice.setCategoryCode(2);
            iotDevice.setRadarDeviceTypeList(null);
            List<IotDeviceVO> iotDeviceVOList = baseMapper.queryIotRadarDeviceList(iotDevice);
            list.addAll(iotDeviceVOList);
        }
        if (!CollectionUtils.isEmpty(radarDeviceTypeList)) {
            iotDevice.setCategoryCode(1);
            iotDevice.setIotDeviceTypeList(null);
            iotDevice.setRadarDeviceTypeList(radarDeviceTypeList);
            List<IotDeviceVO> radarDeviceList = baseMapper.queryIotRadarDeviceList(iotDevice);
            list.addAll(radarDeviceList);
        }
        return list;
    }

    @Override
    @Deprecated
    public List<IotDeviceVO> queryIotDeviceList(IotDeviceVO iotDevice) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<IotDeviceVO> iotDeviceList = baseMapper.queryIotDeviceList(iotDevice);
        if (iotDeviceList.isEmpty()) {
            return iotDeviceList;
        }
        iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_8));
        iotDevice.setIndustryCode(user.getIndustryCode());
        List<OrganDeviceRelVO> orgIotDevRels = orgIotDevRelRepository.queryOrgIotDevRels(iotDevice);
        if (!orgIotDevRels.isEmpty()) {
            for (IotDeviceVO iotDeviceVO : iotDeviceList) {
                iotDeviceVO.setOrgRels(orgIotDevRels.stream().filter(e -> iotDeviceVO.getDeviceCode().equals(e.getDeviceCode())).collect(Collectors.toList()));
                iotDeviceVO.setOrgName(iotDeviceVO.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }
        }
        return iotDeviceList;
    }


    @DataScope(deptColumn = "vod.org_id", deviceColumn = "m.device_code", areaPro = "m.province_code", areaCity = "m.city_id", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryIotDeviceLists(IotDeviceVO iotDevice) {
        return baseMapper.queryIotDeviceList(iotDevice);
    }

    @DataScope(deptColumn = "vod.org_id", deviceColumn = "m.device_code", alarmSourceVal = "8,10",
            areaPro = "m.province_code", areaCity = "m.city_id", tenantColumn = "od.tenant_id")
    @Override
    public List<IotDeviceVO> queryIotDeviceListSimple(IotDeviceVO iotDevice) {
        if (iotDevice.getPageSize() != null && iotDevice.getPageNum() != null) {
            PageHelper.startPage(iotDevice.getPageNum(), iotDevice.getPageSize(), null);
        }
        return baseMapper.queryIotDeviceListSimple(iotDevice);
    }

    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code",acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @Override
    public List<IotDeviceVO> querySensorList(IotDeviceVO iotDevice) {
        if (iotDevice.getPageSize() != null && iotDevice.getPageNum() != null) {
            PageHelper.startPage(iotDevice.getPageNum(), iotDevice.getPageSize(), null);
        }
        return baseMapper.querySensorList(iotDevice);
    }

    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code",acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    public List<PageIotDeviceVO> queryIotDeviceInfo(PageIotDeviceDTO dto) {
        if (Boolean.TRUE.equals(dto.getEnablePage())){
            if ( null == dto.getPageNum() || dto.getPageNum() <= 0){
                dto.setPageNum(1);
            }
            if (null == dto.getPageSize() || dto.getPageSize() <= 0){
                dto.setPageSize(15);
            }
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        }
        return baseMapper.getIotInfoByDeviceType(dto);
    }

    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node")
    public List<PageAlarmTypeVO> queryAlarmType(PageIotDeviceDTO dto) {
        if (Boolean.TRUE.equals(dto.getEnablePage())){
            if ( null == dto.getPageNum() || dto.getPageNum() <= 0){
                dto.setPageNum(1);
            }
            if (null == dto.getPageSize() || dto.getPageSize() <= 0){
                dto.setPageSize(15);
            }
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        }
        return baseMapper.getAlarmTypeList(dto);
    }

    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code",acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @Override
    public List<IotDeviceVO> queryRadarList(IotDeviceVO iotDevice) {
        if (iotDevice.getPageSize() != null && iotDevice.getPageNum() != null) {
            PageHelper.startPage(iotDevice.getPageNum(), iotDevice.getPageSize(), null);
        }
        return baseMapper.queryRadarList(iotDevice);
    }

    /**
     *
     * @param deviceVO
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code",acceptColumn = "osd.accept_node", deviceColumn = "m.device_code",
            areaPro="m.province_id",areaCity="m.city_id", alarmSourceVal = "8,10")
    public List<DeviceTreeVO> getIotOrRadarDeviceTree(IotDeviceVO deviceVO) {
        return baseMapper.getIotOrRadarDeviceTree(deviceVO);
    }

    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code",acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "8,10")
    public SensorDeviceCountVO getSensorDeviceCount(SensorDeviceCountReqDTO request) {
        return baseMapper.getSensorDeviceCount(request);
    }

    @Override
    @DataScope(deviceColumn = "m.device_code", areaPro = "m.province_code", areaCity = "m.city_id", tenantColumn = "od.tenant_id", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryIndIotDeviceListSimple(IotDeviceVO iotDevice) {
        if (iotDevice.getPageSize() != null && iotDevice.getPageNum() != null) {
            PageHelper.startPage(iotDevice.getPageNum(), iotDevice.getPageSize(), null);
        }
        return baseMapper.queryIndIotDeviceListSimple(iotDevice);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateIotDevice(IotDeviceVO iotDevice) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 设备别名、乡镇信息保存
        TenantIotDeviceRelDO uptRel = new TenantIotDeviceRelDO();
        uptRel.setDeviceName(iotDevice.getDeviceName());
        uptRel.setIndustryCode(user.getIndustryCode());
        uptRel.setTenantId(user.getTenantId());
        LambdaQueryWrapper<TenantIotDeviceRelDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantIotDeviceRelDO::getDeviceCode, iotDevice.getDeviceCode());
        queryWrapper.eq(TenantIotDeviceRelDO::getCategoryCode, iotDevice.getCategoryCode());
        queryWrapper.eq(TenantIotDeviceRelDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        queryWrapper.eq(TenantIotDeviceRelDO::getIndustryCode, uptRel.getIndustryCode());
        queryWrapper.eq(TenantIotDeviceRelDO::getTenantId,uptRel.getTenantId());
        List<TenantIotDeviceRelDO> tenIotDevRels = tenantIotDeviceRelRepository.list(queryWrapper);
        if (!tenIotDevRels.isEmpty()) {
            uptRel.setTenIotDevRelId(tenIotDevRels.get(0).getTenIotDevRelId());
            uptRel.setModifierDate(new Date());
            tenantIotDeviceRelRepository.updateById(uptRel);
        } else {
            uptRel.setDeviceCode(iotDevice.getDeviceCode());
            uptRel.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
            uptRel.setTownCode(iotDevice.getTownCode());
            uptRel.setTownName(iotDevice.getTownName());
            uptRel.setCategoryCode(iotDevice.getCategoryCode());
            uptRel.setAppVerCode(user.getAppVerCode());
            uptRel.setCreatorDate(new Date());
            tenantIotDeviceRelRepository.save(uptRel);
        }
        // 所属组织处理
        List<OrganIotDevRelDO> orgRels = new ArrayList<>();
        // 原来无所属组织，现在有
        if (iotDevice.getDeptIds() != null && !iotDevice.getDeptIds().isEmpty()
                && iotDevice.getOrgRels() == null || iotDevice.getOrgRels().isEmpty()) {
            for (String deptId : iotDevice.getDeptIds()) {
                orgRels.add(buildNewOrgRel(iotDevice, deptId));
            }
        }
        // 原来有所属组织，现在没有了
        else if (iotDevice.getOrgRels() != null && !iotDevice.getOrgRels().isEmpty() && iotDevice.getDeptIds().isEmpty()) {
            LambdaQueryWrapper<OrganIotDevRelDO> delQryWrapper = new LambdaQueryWrapper<>();
            delQryWrapper.eq(OrganIotDevRelDO::getDeviceCode, iotDevice.getDeviceCode());
            delQryWrapper.eq(OrganIotDevRelDO::getDeviceType, iotDevice.getDeviceType());
            delQryWrapper.eq(OrganIotDevRelDO::getIndustryCode, user.getIndustryCode());
            delQryWrapper.eq(OrganIotDevRelDO::getTenantId, user.getTenantId());
            orgIotDevRelRepository.remove(delQryWrapper);
        } else if (iotDevice.getOrgRels() != null && iotDevice.getDeptIds() != null) {
            for (String deptId : iotDevice.getDeptIds()) {
                boolean existFlag = false;
                for (int i = iotDevice.getOrgRels().size() - 1; i >= 0; i--) {
                    if (iotDevice.getOrgRels().get(i).getDeptId().equals(deptId)) {
                        existFlag = true;
                        iotDevice.getOrgRels().remove(i);
                        break;
                    }
                }
                if (!existFlag) {
                    orgRels.add(buildNewOrgRel(iotDevice, deptId));
                }
            }
            if (!iotDevice.getOrgRels().isEmpty()) {
                orgIotDevRelRepository.removeByIds(iotDevice.getOrgRels().stream().map(OrganDeviceRelVO::getId).collect(Collectors.toList()));
            }
        }
        if (!orgRels.isEmpty()) {
            orgIotDevRelRepository.saveBatch(orgRels);
        }
        //保存上传的附件信息
        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        hornDeviceDTO.setTenantId(iotDevice.getTenantId());
        hornDeviceDTO.setIndustryCode(user.getIndustryCode());
        hornDeviceDTO.setAppVerCode(user.getAppVerCode());
        hornDeviceDTO.setCreator(user.getUserName());
        hornDeviceDTO.setDeviceCode(iotDevice.getDeviceCode());
        hornDeviceDTO.setDeviceType(Integer.valueOf(iotDevice.getDeviceType()));
        hornDeviceDTO.setFileUrlList(iotDevice.getFileUrlList());
        deviceInfoRepository.saveDeviceAttachmentInfo(hornDeviceDTO);
    }

    private OrganIotDevRelDO buildNewOrgRel(IotDeviceVO iotDevice, String deptId) {
        OrganIotDevRelDO newOrgRel = new OrganIotDevRelDO();
        newOrgRel.setDeviceCode(iotDevice.getDeviceCode());
        newOrgRel.setTenantId(iotDevice.getTenantId());
        newOrgRel.setDeptId(deptId);
        newOrgRel.setUpdateTime(new Date());
        newOrgRel.setUpdateBy(SecurityUtils.getOperator());
        newOrgRel.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
        newOrgRel.setDeviceType(Integer.valueOf(iotDevice.getDeviceType()));
        newOrgRel.setCreateBy(SecurityUtils.getOperator());
        newOrgRel.setCreateTime(new Date());
        newOrgRel.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        return newOrgRel;
    }

    @Override
    public List<SensorAttrMapVO> listSensorAttrValue(DeviceReqDTO deviceReqDTO) {

        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DEVICECODE).is(deviceReqDTO.getDeviceCode()));
        //不传该字段 默认为null--查实时
        if (StringUtils.isEmpty(deviceReqDTO.getTimeFrequency())){
            deviceReqDTO.setTimeFrequency("null");
        }
        String tableName = sensorAttrValueAllMongoDAO.getTableName(deviceReqDTO.getTimeFrequency(), false, null);
        List<SensorAttrMapVO> sensorAttrValueDTOs = sensorAttrValueMongoDAO
                .findList(query, MongoDBType.PRIMARY, SensorAttrMapVO.class, tableName);
        return sensorAttrValueDTOs;
    }

    @Override
    public Result<String> insertSensorData(SensorAttrInfoVO sensorAttrInfoVO) {
        log.info("新增监测数据入参：{}",JSON.toJSONString(sensorAttrInfoVO));
        List<SensorAttrVO> sensorAttrVOList = sensorAttrInfoVO.getSensorCommitData();
        if (StringUtils.isEmpty(sensorAttrInfoVO.getTimeFrequency())){
            Result.fail("时间频次不能为空！");
        }
        String timeFrequency = sensorAttrInfoVO.getTimeFrequency();
        Map<String,String> insertMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(sensorAttrVOList)){
            for (SensorAttrVO sensorAttrVO : sensorAttrVOList) {
                insertMap.put(assembleMongoSensorAttributeAttrCode(sensorAttrVO.getSensorCode(), sensorAttrVO.getAttrCode()),sensorAttrVO.getAttrValue());
                if (insertMap.get(Constants.DEVICECODE) == null || insertMap.get(Constants.DEVICECODE).equals("")){
                    insertMap.put(Constants.DEVICECODE,sensorAttrVO.getDeviceCode());
                }
                if (insertMap.get(Constants.MONITOR_TIME) == null || insertMap.get(Constants.MONITOR_TIME).equals("")){
                    String monitorTime = sensorAttrVO.getMonitorTime();
                    String appTimeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9])$";
                    String pcTimeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
                    if (!monitorTime.matches(appTimeRegex) && !monitorTime.matches(pcTimeRegex)){
                        return Result.fail("输入的时间格式不正确!请输入正确格式:yyyy-MM-dd HH:mm:ss");
                    }
                    if (monitorTime.matches(appTimeRegex)){
                        monitorTime = monitorTime + ":" +"00";
                    }
                    insertMap.put(Constants.MONITOR_TIME,monitorTime);
                }
            }
        }
        String tableName = "";
        String allTableName = "";
        String timeFlag = insertMap.get(Constants.MONITOR_TIME);
        String dateYM = DateUtils.dateToStringYM(new Date());
        switch (timeFrequency){
            case "1":
                //分钟均值--插入分钟库 分钟全量库
                tableName = DeviceConstants.TABLE_NAME_MOR + DeviceConstants.MINUTE;
                allTableName = DeviceConstants.TABLE_NAME_MINUTE_ALL;
                timeFlag = timeFlag.substring(0,16);  //取分钟
                break;
            case "2":
                //小时均值--插入小时库 小时全量库
                tableName = DeviceConstants.TABLE_NAME_MOR + DeviceConstants.HOUR;
                allTableName = DeviceConstants.TABLE_NAME_HOUR_ALL;
                timeFlag = timeFlag.substring(0,13);  //取小时
                break;
            case "3":
                //日均值--插入日均值库 日均值全量库
                tableName = DeviceConstants.TABLE_NAME_MOR + DeviceConstants.DAY;
                allTableName = DeviceConstants.TABLE_NAME_DAY_ALL;
                timeFlag = timeFlag.substring(0,10); //取日期
                break;
            default:
                //其他条件均为原表--实时
                tableName = DeviceConstants.TABLE_NAME;
                allTableName = DeviceConstants.TABLE_SENSOR_ATTRIBUTE_REPORT_ALL + dateYM;
        }
        Criteria criteria = Criteria.where(Constants.DEVICECODE).is(insertMap.get(Constants.DEVICECODE));
        Query query = Query.query(criteria);
        List<SensorAttrMapVO> list = sensorAttrValueMongoDAO.findList(query, MongoDBType.PRIMARY, SensorAttrMapVO.class, tableName);
        log.info("数据库中存在的监测值:{}",JSON.toJSONString(list));
        if (CollectionUtils.isEmpty(list)){
            sensorAttrValueMongoDAO.mongoInsert(insertMap, MongoDBType.PRIMARY, tableName);
        }else {
            SensorAttrMapVO sensorAttrMapVO = list.get(0);
            if (!sensorAttrMapVO.get(Constants.MONITOR_TIME).equals("") && sensorAttrMapVO.get(Constants.MONITOR_TIME) != null){
                String newMonitorTime = insertMap.get(Constants.MONITOR_TIME);
                String oldMonitorTime = String.valueOf(sensorAttrMapVO.get(Constants.MONITOR_TIME));
                if (newMonitorTime.compareTo(oldMonitorTime) > 0){
                    sensorAttrValueMongoDAO.remove(query,MongoDBType.PRIMARY,tableName);
                    sensorAttrValueMongoDAO.mongoInsert(insertMap, MongoDBType.PRIMARY, tableName);
                }
            }
        }
        //插入全量表之前先判断是否有重复数据
        Criteria criteriaAll = Criteria.where(Constants.DEVICECODE).is(insertMap.get(Constants.DEVICECODE))
                .and(Constants.TIMEFLAG).is(timeFlag);
        Query queryAll = Query.query(criteriaAll);
        List<SensorAttrMapVO> listAll = sensorAttrValueMongoDAO.findList(queryAll, MongoDBType.PRIMARY, SensorAttrMapVO.class, allTableName);
        //插入全量表--判断是否有重复数据的标识
        insertMap.put(Constants.TIMEFLAG,timeFlag);
        if (!CollectionUtils.isEmpty(listAll)) {
            //覆盖原来的数据
            sensorAttrValueMongoDAO.remove(queryAll, MongoDBType.PRIMARY, allTableName);
        }
        sensorAttrValueMongoDAO.mongoInsert(insertMap,MongoDBType.PRIMARY,allTableName);

        return Result.success("");
    }

    @Override
    public List<SensorAttrVO> querySensorAttr(DeviceReqDTO deviceInfoDTO) {
        return this.baseMapper.querySensorAttr(deviceInfoDTO);
    }

    @Override
    public List<IotDeviceDO> listIotDeviceByCodes(List<String> deviceCodes) {
        List<IotDeviceDO> deviceInfoDOList = new ArrayList<>(0);
        if (deviceCodes == null || deviceCodes.isEmpty()) {
            return deviceInfoDOList;
        }
        QueryWrapper<IotDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("iot_device_id", "device_code", "app_mod_flag", "category_code");
        queryWrapper.eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL).in("device_code", deviceCodes);
        deviceInfoDOList = this.baseMapper.selectList(queryWrapper);
        return deviceInfoDOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveOrUpdateIotDevice(List<IotDeviceMsgDTO> iotDevices) {
        if (iotDevices.isEmpty()) {
            return;
        }
        List<IotDeviceDO> iotDeviceDOList = new ArrayList<>(iotDevices.size());
        List<String> iotDeviceCodes = new ArrayList<>(iotDevices.size());
        iotDevices.forEach(e -> iotDeviceCodes.add(e.getDeviceCode()));
        List<IotDeviceDO> sameDeviceInfoList = this.listIotDeviceByCodes(iotDeviceCodes);
        for (IotDeviceMsgDTO iotDeviceMsg : iotDevices) {
            IotDeviceDO iotDeviceDO = this.buildIotDeviceDo(iotDeviceMsg, sameDeviceInfoList);
            if (!DeviceConstants.YES_FLAG.equals(iotDeviceDO.getAppModFlag())) {
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(String.format("%s,%s", iotDeviceDO.getLongitude(), iotDeviceDO.getLatitude()));
                Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("batchSaveOrUpdateIotDevice gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                    try {
                        PoiContraryGeocodingQueryRes data = result.getData().get(0);
                        if (data.getRegion() != null) {
                            Region region = data.getRegion();
                            if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                                iotDeviceDO.setProvinceId(region.getProvinceCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                                iotDeviceDO.setCityId(region.getCityCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                                iotDeviceDO.setCountyId(region.getCountyCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                                iotDeviceDO.setTownCode(region.getTownCode());
                            }
                        }
                    } catch (Exception e) {
                        log.error("batchSaveOrUpdateIotDevice gisMapApi poiContraryGeocodingQuery error, e:", e);
                    }
                }
            }
            iotDeviceDOList.add(iotDeviceDO);
        }
        if (!iotDeviceDOList.isEmpty()) {
            this.saveOrUpdateBatch(iotDeviceDOList);
        }
    }

    @Override
    public IotDeviceDO buildIotDeviceDo(IotDeviceMsgDTO iotDeviceMsg, List<IotDeviceDO> sameDeviceInfoList) {
        IotDeviceDO iotDeviceDO = new IotDeviceDO();
        IotDeviceDO sameDevice = this.getSameIotDeviceByCode(iotDeviceMsg.getDeviceCode(), iotDeviceMsg.getCategoryCode(), sameDeviceInfoList);
        if (sameDevice != null) {
            // 应用修改后，只能修改状态
            if (DeviceConstants.YES_FLAG.equals(sameDevice.getAppModFlag())) {
                iotDeviceDO.setDeviceStatus(sameDevice.getDeviceStatus());
                iotDeviceDO.setAppModFlag(sameDevice.getAppModFlag());
            } else {
                BeanUtils.copyProperties(iotDeviceMsg, iotDeviceDO);
            }
            iotDeviceDO.setIotDeviceId(sameDevice.getIotDeviceId());
            iotDeviceDO.setUpdateTime(new Date());
        } else {
            BeanUtils.copyProperties(iotDeviceMsg, iotDeviceDO);
            iotDeviceDO.setCreateTime(new Date());
            iotDeviceDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
        }
        return iotDeviceDO;
    }

    @Override
    @DataScope(deviceColumn = "de.device_code", tenantColumn = "ord.tenant_id")
    public List<IndustryDeviceTree> querySensorLayer(DeviceLayerReq deviceLayerReq) {
        return this.baseMapper.querySensorLayer(deviceLayerReq);
    }

    private IotDeviceDO getSameIotDeviceByCode(String deviceCode, Integer categoryCode, List<IotDeviceDO> deviceInfoList) {
        IotDeviceDO sameDevice = null;
        if (StringUtils.isNotBlank(deviceCode)) {
            for (IotDeviceDO deviceInfoDO : deviceInfoList) {
                if (deviceCode.equals(deviceInfoDO.getDeviceCode()) && categoryCode.equals(deviceInfoDO.getCategoryCode())) {
                    sameDevice = deviceInfoDO;
                    break;
                }
            }
        }
        return sameDevice;
    }

    @DataScope(deptColumn = "t.dept_id")
    @Override
    public List<SysDept> selectDeptTree(SysDept sysDept) {
        JSONArray treeAry = TreeUtils.listToTree(this.baseMapper.selectDeptList(sysDept), "deptId", "parentId");
        return JSON.parseArray(JSON.toJSONString(treeAry), SysDept.class);
    }


    @Override
    @Deprecated
    public List<IotDeviceVO> queryRadarListPage(IotDeviceVO iotDevice) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        iotDevice.setIndustryCode(user.getIndustryCode());
        List<IotDeviceVO> iotDeviceList = baseMapper.queryRadarListPage(iotDevice);
        if (iotDeviceList.isEmpty()) {
            return iotDeviceList;
        }
        iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_10));
        List<OrganDeviceRelVO> orgIotDevRels = orgIotDevRelRepository.queryOrgIotDevRels(iotDevice);
        if (!orgIotDevRels.isEmpty()) {
            for (IotDeviceVO iotDeviceVO : iotDeviceList) {
                iotDeviceVO.setOrgRels(orgIotDevRels.stream().filter(e -> iotDeviceVO.getDeviceCode().equals(e.getDeviceCode())).collect(Collectors.toList()));
                iotDeviceVO.setOrgName(iotDeviceVO.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }
        }
        return iotDeviceList;
    }

    @Override
    public Result<PageInfo<IotDeviceVO>> queryRadarOrIotDeviceList(IotDeviceVO deviceInfoVO,Integer categoryCode) {

        try {
            if (null == deviceInfoVO.getPageNum() || deviceInfoVO.getPageNum() <= Constants.ZERO_NUM) {
                deviceInfoVO.setPageNum(Constants.ONE_NUM);
            }
            if (null == deviceInfoVO.getPageSize() || deviceInfoVO.getPageSize() <= Constants.ZERO_NUM) {
                deviceInfoVO.setPageSize(Constants.TEN_NUM);
            }
            PageMethod.startPage(deviceInfoVO.getPageNum(),deviceInfoVO.getPageSize());
            Result<PageInfo<IotDeviceVO>> result;
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            deviceInfoVO.setUserId(loginUser.getUserId());
            deviceInfoVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
            List<IotDeviceVO> iotDeviceList = baseMapper.queryRadarOrIotDeviceList(deviceInfoVO,categoryCode);
            if (Objects.equals(Constants.ONE_NUM,categoryCode)){
                deviceInfoVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_10));
            }else {
                for (IotDeviceVO iotDeviceVO : iotDeviceList){
                    if (iotDeviceVO.getRangeMeter() != null){
                        if (iotDeviceVO.getRangeMeter() <= 1000){
                            iotDeviceVO.setDistance(iotDeviceVO.getRangeMeter() + "米");
                        }else {
                            BigDecimal bg = new BigDecimal(iotDeviceVO.getRangeMeter());
                            BigDecimal bg1 = new BigDecimal(1000);
                            iotDeviceVO.setDistance(bg.divide(bg1,2,BigDecimal.ROUND_HALF_UP)+"千米");
                        }
                    }
                }
                deviceInfoVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_8));
            }
            List<OrganDeviceRelVO> orgIotDevRels = orgIotDevRelRepository.queryOrgIotDevRels(deviceInfoVO);
            if (!orgIotDevRels.isEmpty()) {
                for (IotDeviceVO iotDeviceVO : iotDeviceList) {
                    iotDeviceVO.setOrgRels(orgIotDevRels.stream().filter(e -> iotDeviceVO.getDeviceCode().equals(e.getDeviceCode())).collect(Collectors.toList()));
                    iotDeviceVO.setOrgName(iotDeviceVO.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
                }
            }
            PageInfo<IotDeviceVO> pageInfo = new PageInfo<>(iotDeviceList);
            result = Result.success("查询成功");
            result.setData(pageInfo);
            return result;
        }catch (Exception e){
            log.info("查询列表异常：", e);
            return Result.fail("列表查询异常");
        }



    }

    @Override
    @Deprecated
    public Result<IotDeviceVO> queryIotDeviceByCode(IotDeviceVO deviceInfoVO) {
        if(StringUtils.isEmpty(deviceInfoVO.getDeviceCode())){
            return Result.fail("设备编码不能为空！");
        }
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser tokenUser = currentUser.getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = hornDeviceMapper.selectUserOnlyByUserName(tokenUser.getUserName());
        deviceInfoVO.setUserId(user.getUserId());
        IotDeviceVO iotDeviceVO = baseMapper.queryIotDeviceByCode(deviceInfoVO);
        deviceInfoVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_8));
        deviceInfoVO.setIndustryCode(tokenUser.getIndustryCode());
        deviceInfoVO.setTenantId(tokenUser.getTenantId());
        deviceInfoVO.setDeviceCode(iotDeviceVO.getDeviceCode());

        //查询设备关联的组织信息
        List<OrganDeviceRelVO> organDeviceRelVOS = orgIotDevRelRepository.queryOrgIotDevRels(deviceInfoVO);
        if (!CollectionUtils.isEmpty(organDeviceRelVOS)){
            iotDeviceVO.setOrgRels(organDeviceRelVOS.stream().filter(organDeviceRelVO -> iotDeviceVO.getDeviceCode().equals(organDeviceRelVO.getDeviceCode())).collect(Collectors.toList()));
            iotDeviceVO.setOrgName(iotDeviceVO.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
        }

        return Result.data(iotDeviceVO);
    }


    @DataScope(deptColumn = "vod.org_id", deviceColumn = "m.device_code", areaPro = "m.province_code", areaCity = "m.city_id", tenantColumn = "od.tenant_id")
    public List<IotDeviceVO> queryRadarListPages(IotDeviceVO iotDevice) {
        return baseMapper.queryRadarListPage(iotDevice);
    }

    @Override
    public Result getDeviceListByRadar(RadarDeviceRelDTO radarDeviceRelDTO) {
        Result<Object> result = null;
        if (radarDeviceRelDTO.isEnablePage()) {
            Integer pageNum = radarDeviceRelDTO.getPageNum();
            Integer pageSize = radarDeviceRelDTO.getPageSize();
            if (null == pageNum || pageNum <= 0) {
                pageNum = 1;
            }
            if (null == pageSize || pageSize <= 0) {
                pageSize = 15;
            }
            PageMethod.startPage(pageNum, pageSize);
            List<RadarDeviceRelVO> resultList = radarDeviceRelMapper.getDeviceListByRadar(radarDeviceRelDTO);
            PageInfo<RadarDeviceRelVO> pageInfo = new PageInfo(resultList);
            result = Result.success("查询成功");
            result.setData(pageInfo);
        } else {
            List<RadarDeviceRelVO> resultList = radarDeviceRelMapper.getDeviceListByRadar(radarDeviceRelDTO);
            result = Result.success("查询成功");
            result.setData(resultList);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveOrUpdateRel(RadarDeviceRelDTO radarDeviceRelDTO) {
        if (CollectionUtils.isEmpty(radarDeviceRelDTO.getDeviceCodeList())) {
            return Result.fail(ResultCode.FAILURE, "至少选择一个关联摄像机");
        }
        if (StringUtils.isEmpty(radarDeviceRelDTO.getRadarDeviceCode())) {
            return Result.fail(ResultCode.FAILURE, "雷达编码不能为空");
        }
        List<RadarDeviceRelDO> radarDeviceRelDOList = new ArrayList();
        radarDeviceRelDTO.getDeviceCodeList().forEach(deviceCode -> {
            RadarDeviceRelDO radarDeviceRelDO = new RadarDeviceRelDO();
            radarDeviceRelDO.setRadarDeviceCode(radarDeviceRelDTO.getRadarDeviceCode());
            radarDeviceRelDO.setDeviceRelCode(deviceCode);
            radarDeviceRelDO.setTenantId(radarDeviceRelDTO.getTenantId());
            radarDeviceRelDO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
            radarDeviceRelDO.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
            radarDeviceRelDOList.add(radarDeviceRelDO);
        });
        QueryWrapper<RadarDeviceRelDO> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(RadarDeviceRelDO::getRadarDeviceCode, radarDeviceRelDTO.getRadarDeviceCode());
        queryWrapper.lambda().eq(RadarDeviceRelDO::getTenantId, radarDeviceRelDTO.getTenantId());
        queryWrapper.lambda().eq(RadarDeviceRelDO::getIndustryCode, SecurityUtils.getLoginUser().getUser().getIndustryCode());
        radarDeviceRelMapper.delete(queryWrapper);
        radarDeviceRelMapper.saveRelBatch(radarDeviceRelDOList);
        return Result.success("保存成功");
    }

    @Override
    public Result<List<RadarDeviceVO>> getRadarDeviceConfigInfo(RadarDeviceReqDTO radarDeviceReqDTO) {
        if (radarDeviceReqDTO == null || CollectionUtils.isEmpty(radarDeviceReqDTO.getRadarIds())) {
            return Result.fail(ResultCode.FAILURE, "雷达编码不能为空");
        }
        // 返回结果
        List<RadarDeviceVO> resultList = Lists.newArrayList();
        // 雷达id列表
        List<String> radarDeviceCodeList = radarDeviceReqDTO.getRadarIds();

        List<String> mongoExistsDeviceCodeList = Lists.newArrayList();
        Date now = new Date();
        QueryEntity query = new QueryEntity(DeviceConstants.TABLE_RADAR_DEVICE_CONFIG_REL, RadarDeviceConfigRelDTO.class);
        query.in(Constants.DEVICECODE, radarDeviceCodeList.toArray(new String[0]));
        List<RadarDeviceConfigRelDTO> radarDeviceConfigRelMongoList = radarDeviceConfigRelMongoDAO.findList(query, null, MongoDBType.PRIMARY);
        log.info("IotDeviceRepositoryImpl getRadarDeviceConfigInfo findList param:{}, result:{}", JSON.toJSONString(radarDeviceCodeList), JSON.toJSONString(radarDeviceConfigRelMongoList));
        if (!CollectionUtils.isEmpty(radarDeviceConfigRelMongoList)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MINUTE, -10);
            Date expireTime = calendar.getTime();
            for (RadarDeviceConfigRelDTO radarDeviceConfigRelDTO : radarDeviceConfigRelMongoList) {
                // 如果未超过10分钟
                if (radarDeviceConfigRelDTO.getUpdateTime().after(expireTime)) {
                    resultList.add(radarDeviceConfigRelDTO.getRadarDeviceInfo());
                    radarDeviceCodeList.remove(radarDeviceConfigRelDTO.getDeviceCode());
                } else {
                    mongoExistsDeviceCodeList.add(radarDeviceConfigRelDTO.getDeviceCode());
                }
            }
        }
        log.info("IotDeviceRepositoryImpl getRadarDeviceConfigInfo radarDeviceCodeList:{}, mongoExistsDeviceCodeList:{}",
                JSON.toJSONString(radarDeviceCodeList), JSON.toJSONString(mongoExistsDeviceCodeList));
        // 增加mongodb缓存
        if (!CollectionUtils.isEmpty(radarDeviceCodeList)) {
            List<RadarDeviceVO> radarOrderInfoList = baseMapper.getRadarDeviceConfigInfoNew(radarDeviceCodeList);
            log.info("IotDeviceRepositoryImpl getRadarDeviceConfigInfo getRadarDeviceConfigInfoNew radarDeviceCodeList:{}, radarOrderInfoList:{}",
                    JSON.toJSONString(radarDeviceCodeList), JSON.toJSONString(radarOrderInfoList));
            if (!CollectionUtils.isEmpty(radarOrderInfoList)) {
                resultList.addAll(radarOrderInfoList);
                try {
                    List<RadarDeviceConfigRelDTO> insertDTOList = new ArrayList<>();
                    List<String> radarIdList = new ArrayList<>();
                    List<RadarDeviceVO> updateRadarDeviceList = new ArrayList<>();
                    for (RadarDeviceVO radarDeviceVO : radarOrderInfoList) {
                        if (mongoExistsDeviceCodeList.contains(radarDeviceVO.getRadarId())) {
                            radarIdList.add(radarDeviceVO.getRadarId());
                            updateRadarDeviceList.add(radarDeviceVO);
//                            // 更新mongodb
//                            QueryEntity queryEntity = new QueryEntity(DeviceConstants.TABLE_RADAR_DEVICE_CONFIG_REL, RadarDeviceConfigRelDTO.class);
//                            queryEntity.eq(Constants.DEVICECODE, radarDeviceVO.getRadarId());
//                            UpdateEntity updateEntity = new UpdateEntity();
//                            updateEntity.set("radarDeviceInfo", radarDeviceVO);
//                            updateEntity.set("updateTime", now);
//                            radarDeviceConfigRelMongoDAO.updateMulti(queryEntity, updateEntity, MongoDBType.PRIMARY);
                        } else {
                            // 新增mongodb
                            RadarDeviceConfigRelDTO insertDto = new RadarDeviceConfigRelDTO();
                            insertDto.setDeviceCode(radarDeviceVO.getRadarId());
                            insertDto.setUpdateTime(now);
                            insertDto.setRadarDeviceInfo(radarDeviceVO);
                            insertDTOList.add(insertDto);
                        }
                    }
                    if (!CollectionUtils.isEmpty(insertDTOList)){
                        radarDeviceConfigRelMongoDAO.insertAll(insertDTOList,DeviceConstants.TABLE_RADAR_DEVICE_CONFIG_REL,MongoDBType.PRIMARY);
                    }
                    if (!CollectionUtils.isEmpty(updateRadarDeviceList)){
                        updateMongoDBBatch(radarIdList,updateRadarDeviceList);
                    }
                } catch (Exception e) {
                    log.error("IotDeviceRepositoryImpl getRadarDeviceConfigInfo mongo error:", e);
                }
            }
        }
        return Result.data(resultList);
    }

    private void updateMongoDBBatch(List<String> idList,List<RadarDeviceVO> updateList){
        Date now = new Date();
        MongoTemplate mongoTemplate = sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY);
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, RadarDeviceVO.class, DeviceConstants.TABLE_RADAR_DEVICE_CONFIG_REL);
        Map<String, RadarDeviceVO> radarDeviceMap = updateList.stream().collect(Collectors.toMap(RadarDeviceVO::getRadarId, p -> p, (a, b) -> a));
        for (String deviceCode : idList){
            Query query = new Query(Criteria.where(Constants.DEVICECODE).is(deviceCode));
            Update updates = new Update();

            //添加需要更新的字段值
            updates.set("radarDeviceInfo",radarDeviceMap.get(deviceCode));
            updates.set("updateTime",now);
            // 将更新操作加入批量操作
            bulkOperations.updateOne(query, updates);
        }
        //执行批量更新操作
        BulkWriteResult execute = bulkOperations.execute();
        log.info("批量更新操作结果：{}",JSON.toJSONString(execute.getModifiedCount()));

    }

    @Override
    @DataScope(industryCode = "c.industry_code", tenantColumn = "c.tenant_id",acceptColumn = "c.accept_node", deviceColumn = "c.device_code", alarmSourceVal = "8,10")
    public Result<List<IotDeviceEntity>> getIotDeviceByDept(IotDeviceEntity iotDeviceEntity) {
        return Result.data(baseMapper.getIotDeviceByDept(iotDeviceEntity));
    }

    @Override
    @DataScope(industryCode = "c.industry_code", tenantColumn = "c.tenant_id",acceptColumn = "c.accept_node", deviceColumn = "c.device_code", alarmSourceVal = "8,10")
    public Result<List<IotDeviceTypeData>> getIotGroupByDeviceType(IotDeviceAreaEntity iotDeviceAreaEntity) {
        return Result.data(baseMapper.getIotGroupByDeviceType(iotDeviceAreaEntity));
    }

    @Override
    @DataScope(industryCode = "o.industry_code", tenantColumn = "o.tenant_id",acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "8,10")
    public Result<PageInfo<PageIotMonitorVO>> pageIotMonitorInfo(PageIotMonitorDTO param) {
        if (null == param.getPageNum() || param.getPageNum() <= Constants.ZERO_NUM) {
            param.setPageNum(Constants.ONE_NUM);
        }
        if (null == param.getPageSize() || param.getPageSize() <= Constants.ZERO_NUM) {
            param.setPageSize(Constants.TEN_NUM);
        }
        PageMethod.startPage(param.getPageNum(), param.getPageSize());
        List<String> deviceCodeList = baseMapper.pageIotMonitorDeviceCode(param);
        if (CollectionUtils.isEmpty(deviceCodeList)) {
            return Result.data(PageInfo.of(Lists.newArrayList()));
        }
        PageInfo<String> deviceCodePageInfo = new PageInfo<>(deviceCodeList);
        log.info("IotDeviceRepositoryImpl pageIotMonitorInfo deviceCodePageInfo:{}", JSON.toJSONString(deviceCodePageInfo));
        Result<PageInfo<PageIotMonitorVO>> result = Result.success("查询成功");
        List<PageIotMonitorVO> resultList = baseMapper.pageIotMonitorInfo(deviceCodeList);
        PageInfo<PageIotMonitorVO> pageInfo = new PageInfo<>(resultList);
        log.info("IotDeviceRepositoryImpl pageIotMonitorInfo pageInfo:{}", JSON.toJSONString(pageInfo));
        result.setData(pageInfo);
        return result;
    }

    /**
     * 周边分析-周边物联设备信息搜索
     * @param param
     * @return
     */
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code",acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "8,10")
    public List<DeviceDistanceVO> queryIotDevDistanceInfo(DeviceDistanceDTO param) {
        if (param == null
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLongitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLatitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getRadius())) {
            throw new CustomException(LAT_LONG_CAN_NOT_EMPTY_MSG);
        }
        return baseMapper.queryIotDevDistanceInfo(param);
    }

    private List<DeviceDistanceVO> queryIotDevDistanceInfo4Calculate(DeviceDistance4CalculateDTO param) {
        if (param == null
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLongitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLatitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getRadius())) {
            throw new CustomException(LAT_LONG_CAN_NOT_EMPTY_MSG);
        }
        return baseMapper.queryIotDevDistanceInfo4Calculate(param);
    }

    @Override
    public List<IotDeviceSensorCalculateVO> querySensorPeriodData4Calculate(DeviceDistance4CalculateDTO param) {
        if (param == null
                || StringUtils.isEmpty(param.getStartDate())
                || StringUtils.isEmpty(param.getEndDate())
                || CollectionUtils.isEmpty(param.getDeviceTypeList())
                || StringUtils.isEmpty(param.getLongitude())
                || StringUtils.isEmpty(param.getLatitude())
                || StringUtils.isEmpty(param.getRadius())) {
            throw new CustomException(LAT_LONG_CAN_NOT_EMPTY_MSG);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = null;
        Date end = null;
        try {
            start = simpleDateFormat.parse(param.getStartDate());
            end = simpleDateFormat.parse(param.getEndDate());
        } catch (ParseException e) {
            log.error("日期转换异常", e);
        }
        if (start == null || end == null) {
            throw new CustomException("日期格式有误");
        }

        List<IotDeviceSensorCalculateVO> resultList = Lists.newArrayList();
        // 根据设备类型、经纬度、距离查询满足条件的物联设备
        List<DeviceDistanceVO> deviceDistanceInfoList = queryIotDevDistanceInfo4Calculate(param);
        if (CollectionUtils.isEmpty(deviceDistanceInfoList)) {
            return resultList;
        }

        List<String> deviceCodeList = deviceDistanceInfoList.stream().map(DeviceDistanceVO::getDeviceCode).distinct().collect(Collectors.toList());
        DeviceReqDTO deviceReq = new DeviceReqDTO();
        deviceReq.setDeviceCodes(deviceCodeList);
        deviceReq.setStartDate(param.getStartDate());
        deviceReq.setEndDate(param.getEndDate());
        String tableDate = DateUtils.parseDateToStr(DeviceConstants.CURRENT_MONTH_FORMAT, start);
        List<SensorAttrMapVO> sensorAttrInfoList = sensorAttrValueAllMongoDAO.listSensorPeriodData(deviceReq, tableDate);
        // key -> deviceCode values -> sensorAttrList
        Map<String, List<SensorAttrMapVO>> sensorAttrMap = convert2SensorAttrMap(sensorAttrInfoList);
        for (DeviceDistanceVO deviceDistanceVO : deviceDistanceInfoList) {
            String deviceCode = deviceDistanceVO.getDeviceCode();
            IotDeviceSensorCalculateVO data = new IotDeviceSensorCalculateVO();
            data.setDeviceCode(deviceCode);
            data.setDistance(deviceDistanceVO.getDistance());
            data.setSensorAttrList(sensorAttrMap.get(deviceCode));
            resultList.add(data);
        }
        return resultList;
    }

    private Map<String, List<SensorAttrMapVO>> convert2SensorAttrMap(List<SensorAttrMapVO> sensorAttrInfoList) {
        Map<String, List<SensorAttrMapVO>> sensorAttrMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(sensorAttrInfoList)) {
            for (SensorAttrMapVO sensorAttrMapVO : sensorAttrInfoList) {
                String deviceCode = String.valueOf(sensorAttrMapVO.get(Constants.DEVICECODE));
                if (StringUtils.isBlank(deviceCode)) {
                    continue;
                }
                if (sensorAttrMap.containsKey(deviceCode)) {
                    sensorAttrMap.get(deviceCode).add(sensorAttrMapVO);
                } else {
                    sensorAttrMap.put(deviceCode, Lists.newArrayList(sensorAttrMapVO));
                }
            }
        }
        return sensorAttrMap;
    }

    /**
     *
     * 周边分析-周边物联设备信息搜索
     * @param param
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code",acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "8,10")
    public List<DeviceDistanceVO> queryIotDevDistanceInfo4App(DeviceDistanceDTO param) {
        if (param == null
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLongitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLatitude())) {
            throw new CustomException("经纬度参数不能为空");
        }

        if (StringUtils.isBlank(param.getRadius())) {
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
            param.setRadius(String.valueOf(Integer.parseInt(distance)));
        }
        param.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        return queryIotDevDistanceInfo(param);
    }

    /**
     * 周边分析-周边雷达信息搜索
     * @param param
     * @return
     */
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code",acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "8,10")
    public List<DeviceDistanceVO> queryRadarDevDistanceInfo(DeviceDistanceDTO param) {
        if (param == null
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLongitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLatitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getRadius())) {
            throw new CustomException(LAT_LONG_CAN_NOT_EMPTY_MSG);
        }
        return baseMapper.queryRadarDevDistanceInfo(param);
    }

    /**
     *
     * @param param
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code",acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "8,10")
    public List<DeviceDistanceVO> queryRadarDevDistanceInfo4App(DeviceDistanceDTO param) {
        if (param == null
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLongitude())
                || com.chinatower.common.core.utils.StringUtils.isEmpty(param.getLatitude())) {
            throw new CustomException("经纬度参数不能为空");
        }
        if (StringUtils.isBlank(param.getRadius())) {
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
            param.setRadius(String.valueOf(Integer.parseInt(distance)));
        }
        param.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        return queryRadarDevDistanceInfo(param);
    }

    public Result<IotDeviceVO> queryRadarDeviceByCode(IotDeviceVO iotDeviceVO) {
        if(com.chinatower.common.core.utils.StringUtils.isEmpty(iotDeviceVO.getDeviceCode())){
            return Result.fail("设备编码不能为空！");
        }
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser tokenUser = currentUser.getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = hornDeviceMapper.selectUserOnlyByUserName(tokenUser.getUserName());
        iotDeviceVO.setUserId(user.getUserId());
        IotDeviceVO radarDevice = baseMapper.queryRadarDeviceByCode(iotDeviceVO);
        iotDeviceVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_10));
        iotDeviceVO.setIndustryCode(tokenUser.getIndustryCode());
        iotDeviceVO.setTenantId(tokenUser.getTenantId());
        //查询设备关联的组织信息
        List<OrganDeviceRelVO> organDeviceRelVOS = orgIotDevRelRepository.queryOrgIotDevRels(iotDeviceVO);
        if (!CollectionUtils.isEmpty(organDeviceRelVOS)){
            radarDevice.setOrgRels(organDeviceRelVOS.stream().filter(organDeviceRelVO -> radarDevice.getDeviceCode().equals(organDeviceRelVO.getDeviceCode())).collect(Collectors.toList()));
            radarDevice.setOrgName(radarDevice.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
        }

        return Result.data(radarDevice);
    }

    /**
     * 根据关键字查询雷达设备
     * @param deviceComprehensiveReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code",acceptColumn = "od.accept_node", deviceColumn = "od.device_code", alarmSourceVal = "8,10")
    public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq) {
        PageHelper.startPage(deviceComprehensiveReq.getPageNum(), deviceComprehensiveReq.getPageSize());
        List<DeviceComprehensiveVO> list = baseMapper.queryDeviceListByKeyWord(deviceComprehensiveReq);
        PageInfo<DeviceComprehensiveVO> pageInfo = new PageInfo<>(list);
        Result<PageInfo<DeviceComprehensiveVO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    @Override
    public List<SensorAttrMapVO> getSensorAttrValueBatch(DeviceReqDTO deviceReqDTO) {

        Query query = new Query();
        if (StringUtils.isNotBlank(deviceReqDTO.getDeviceCode())) {
            query.addCriteria(Criteria.where(Constants.DEVICECODE).is(deviceReqDTO.getDeviceCode()));
        }
        if (!CollectionUtils.isEmpty(deviceReqDTO.getDeviceCodes())) {
            query.addCriteria(Criteria.where((Constants.DEVICECODE)).in(deviceReqDTO.getDeviceCodes()));
        }
        //不传该字段 默认为null--查实时
        if (StringUtils.isEmpty(deviceReqDTO.getTimeFrequency())){
            deviceReqDTO.setTimeFrequency("null");
        }
        String tableName = sensorAttrValueAllMongoDAO.getTableName(deviceReqDTO.getTimeFrequency(), false, null);
        List<SensorAttrMapVO> sensorAttrValueDTOs = sensorAttrValueMongoDAO
                .findList(query, MongoDBType.PRIMARY, SensorAttrMapVO.class, tableName);
        return sensorAttrValueDTOs;
    }

    @Override
    public SensorEventInfoVO getSensorEventList(DeviceReqDTO deviceReqDTO) {

        SensorEventInfoVO sensorEventInfoVO = new SensorEventInfoVO();

        Query query = new Query();
        if (StringUtils.isNotBlank(deviceReqDTO.getDeviceCode())){
            query.addCriteria(Criteria.where(Constants.DEVICECODE).is(deviceReqDTO.getDeviceCode()));
        }
        if (StringUtils.isNotBlank(deviceReqDTO.getStartDate()) && StringUtils.isNotBlank(deviceReqDTO.getEndDate())){
            query.addCriteria(Criteria.where(Constants.MONITOR_TIME).gte(deviceReqDTO.getStartDate()).lte(deviceReqDTO.getEndDate()));
        }
        if (StringUtils.isNotBlank(deviceReqDTO.getEventId())){
            query.addCriteria(Criteria.where(Constants.EVENT_ID).is(deviceReqDTO.getEventId()));
        }
        sensorEventInfoVO.setCount(sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).count(query,SensorEventVO.class,
                DeviceConstants.SENSOR_EVENT_TABLE_NAME));
        PageRequest pageRequest = PageRequest.of(deviceReqDTO.getPageNo() - 1,deviceReqDTO.getPageSize(), Sort.by(Sort.Direction.DESC,Constants.MONITOR_TIME));
        query.with(pageRequest);
        List<SensorEventVO> sensorEventList = sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).find(query, SensorEventVO.class, DeviceConstants.SENSOR_EVENT_TABLE_NAME);
        log.info("传感器监测事件mongodb查询结果:{}",JSON.toJSONString(sensorEventList));
        //获取昆虫种类、事件类型字典值
        List<SysDictData> speciesTypeDictData = systemFeignClient.getDictDataByType(Constants.SPECIES_TYPE).getData();
        List<SysDictData> eventTypeDictData = systemFeignClient.getDictDataByType(Constants.SENSOR_EVENT_TYPE).getData();
        Map<String, String> speciesTypeListMap = new HashMap<>();
        Map<String, String> eventTypeListMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(speciesTypeDictData)){
            speciesTypeListMap = speciesTypeDictData.stream().filter(item -> StringUtils.isNotBlank(item.getDictValue()))
                    .collect(Collectors.toMap(SysDictData::getDictValue,SysDictData::getDictLabel));
        }
        if (!CollectionUtils.isEmpty(eventTypeDictData)){
            eventTypeListMap = eventTypeDictData.stream().filter(item -> StringUtils.isNotBlank(item.getDictValue()))
                    .collect(Collectors.toMap(SysDictData::getDictValue,SysDictData::getDictLabel));
        }
        //拼接上报结果、事件类型
        for (SensorEventVO sensorEventVO : sensorEventList){
            //封装事件类型中文名  1-噪声超上 2-噪声超下线 3-虫情算法识别
            if (StringUtils.isNotBlank(eventTypeListMap.get(sensorEventVO.getEventType()))) {
                sensorEventVO.setEventTypeName(eventTypeListMap.get(sensorEventVO.getEventType()));
            }
            if (!CollectionUtils.isEmpty(sensorEventVO.getExtraAttribute())){
                Map<String,String> extraAttributeMap = sensorEventVO.getExtraAttribute().get(0);
                //拼接上报结果--物种信息需要拼接中文
                if (StringUtils.isNotBlank(extraAttributeMap.get(SPECIES_MSG))
                        && (StringUtils.isNotBlank(extraAttributeMap.get(SPECIES_NUM_MSG)))){
                    String speciesTypeName = "";
                    if (MapUtils.isNotEmpty(speciesTypeListMap)){
                        if (StringUtils.isNotBlank(speciesTypeListMap.get(extraAttributeMap.get(SPECIES_MSG)))){
                            speciesTypeName = speciesTypeListMap.get(extraAttributeMap.get(SPECIES_MSG));
                        }
                    }
                    String extraAttributeName = "昆虫种类: "+speciesTypeName+"; "+"昆虫数量: "+ extraAttributeMap.get(SPECIES_NUM_MSG);
                    // 过滤掉 extraAttributeMap 中键为 SPECIES_MSG 和 speciesNum 的条目
                    extraAttributeMap = extraAttributeMap.entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(SPECIES_MSG) && !entry.getKey().equals(SPECIES_NUM_MSG))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    // 拼接其它属性
                    if (MapUtils.isNotEmpty(extraAttributeMap)) {
                        for (String key : extraAttributeMap.keySet()) {
                            extraAttributeName = extraAttributeName + "; " + key + ": " + speciesTypeListMap.get(extraAttributeMap.get(key));
                        }
                    }
                    sensorEventVO.setExtraAttributeName(extraAttributeName);
                }else {
                    String extraAttributeName = "";
                    for (String key : extraAttributeMap.keySet()){
                        extraAttributeName = extraAttributeName + key + "：" + extraAttributeMap.get(key) + "；";
                    }

                    sensorEventVO.setExtraAttributeName(extraAttributeName);
                }
            }
        }
        sensorEventInfoVO.setSensorEventList(sensorEventList);
        return sensorEventInfoVO;
    }

    @Override
    public List<SensorEventFileVO> getSensorEventFileList(DeviceReqDTO deviceReqDTO, List<String> eventIdList) {
        Query query = new Query();
        if (StringUtils.isNotBlank(deviceReqDTO.getDeviceCode())){
            query.addCriteria(Criteria.where(Constants.DEVICECODE).is(deviceReqDTO.getDeviceCode()));
        }
        if (CollectionUtils.isNotEmpty(eventIdList)) {
            query.addCriteria(Criteria.where(Constants.RELATION_ID).in(eventIdList));
        }
        List<SensorEventFileVO> eventFileVOList = sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).find(query, SensorEventFileVO.class, DeviceConstants.SENSOR_EVENT_FILE_TABLE_NAME);
        log.info("传感器监测事件文件信息mongodb查询结果:{}",JSON.toJSONString(eventFileVOList));
        //对文件地址加密
        List<String> pathList = Lists.newArrayList();
        for (SensorEventFileVO sensorEventFileVO : eventFileVOList){
            if (CollectionUtils.isNotEmpty(sensorEventFileVO.getFiles())) {
                FileInfoVO files = sensorEventFileVO.getFiles().get(0);
                pathList.add(files.getPath());
            }
        }
        Map<String, String> stringStringMap = Maps.newHashMap();
        Lists.partition(pathList, 100).forEach(subList -> {
            Map<String, String> subMap = fileUrlEncryptUtil.dealObsFileUrlBatch(subList, false, null);
            if (MapUtils.isNotEmpty(subMap)) {
                stringStringMap.putAll(subMap);
            }
        });

        for (SensorEventFileVO eventFileVO : eventFileVOList){
            List<FileInfoVO> files = eventFileVO.getFiles();
            if (CollectionUtils.isNotEmpty(files)) {
                for (FileInfoVO fileInfoVO : files){
                    fileInfoVO.setPath(stringStringMap.get(fileInfoVO.getPath()));
                }
            }
        }
        log.info("传感器监测事件文件地址加密后信息:{}",JSON.toJSONString(eventFileVOList));
        return eventFileVOList;
    }

    @Override
    public List<SensorStatusInfoVO> querySensorStatusInfo(SensorStatusInfoDTO param) {
        if (param == null || CollectionUtils.isEmpty(param.getDeviceCodeList())) {
            throw new CustomException("最少查询1个设备的状态信息");
        }
        if (param.getDeviceCodeList().size() > 100) {
            throw new CustomException("最多查询100个设备的状态信息");
        }
        List<SensorStatusInfoVO> resultList = this.baseMapper.querySensorStatusInfo(param);
        // key -> sensorCode  value -> sensorTypeCode
        Map<String, String> sensorMap = assembleSensorMap(resultList);
        if (MapUtils.isNotEmpty(sensorMap)) {
            List<String> sensorTypeCodeList = sensorMap.values().stream().distinct().collect(Collectors.toList());
            // key -> sensorTypeCode value -> sensorTypeName
            Map<String, String> sensorTypeMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(sensorTypeCodeList)) {
                List<SensorDeviceTypeVO> sensorDeviceTypeVOList = iotDeviceMapper.getSensorDeviceTypeByCode(sensorTypeCodeList);
                if (CollectionUtils.isNotEmpty(sensorDeviceTypeVOList)) {
                    sensorTypeMap = sensorDeviceTypeVOList.stream().collect(Collectors.toMap(
                            SensorDeviceTypeVO::getTypeCode, SensorDeviceTypeVO::getTypeName, (a, b) -> a));
                }
            }
            for (SensorStatusInfoVO data : resultList) {
                // 传感器类型编码
                String sensorTypeCode = sensorMap.get(data.getSensorCode());
                data.setSensorTypeCode(sensorTypeCode);
                if (StringUtils.isNotEmpty(sensorTypeCode)) {
                    // 传感器类型名称
                    String sensorTypeName = sensorTypeMap.get(sensorTypeCode);
                    if (StringUtils.isNotEmpty(sensorTypeName)) {
                        data.setSensorTypeName(sensorTypeName);
                    }
                }
            }
        }
        return resultList;
    }

    private Map<String, String> assembleSensorMap(List<SensorStatusInfoVO> resultList) {
        Map<String, String> sensorMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(resultList)) {
            return sensorMap;
        }
        for (SensorStatusInfoVO sensorData : resultList) {
            String sensorCode = sensorData.getSensorCode();
            if (sensorCode == null || !sensorCode.contains("_") || sensorMap.containsKey(sensorCode)) {
                continue;
            }
            // 找到最后一个下划线的索引
            int lastIndex = sensorCode.lastIndexOf('_');

            // 如果找到了下划线，则截取之前的部分
            if (lastIndex != -1) {
                String sensorTypeCode = sensorCode.substring(0, lastIndex);
                sensorMap.put(sensorCode, sensorTypeCode);
            }
        }
        return sensorMap;
    }

    @Override
    public Result<SensorAttrResultVO> getSensorDataV2(DeviceReqDTO deviceReqDTO) {
        SensorAttrResultVO result = new SensorAttrResultVO();
        List<SensorAttrMapVO> list = listSensorAttrValue(deviceReqDTO);
        List<SensorAttrVO> sensorAttrs = querySensorAttr(deviceReqDTO);
        result.setHaveSensor(this.judgeIsHaveSensor(sensorAttrs));
        List<String> sensorTypeCodeList = this.assembleSensorTypeCodeList(sensorAttrs);
        Map<String, String> sensorTypeMap = this.assembleSensorVOMap(sensorTypeCodeList, sensorAttrs);
        for (SensorAttrVO sensorAttrVO : sensorAttrs) {
            if (!sensorAttrVO.getAttrUnits().isEmpty()){
                sensorAttrVO.setAttrName(sensorAttrVO.getAttrName() + "(" + sensorAttrVO.getAttrUnits() + ")");
            }
            if (!list.isEmpty()) {
                sensorAttrVO.setAttrValue(com.chinatower.common.core.utils.StringUtils.toStringNoNull(MapUtils.getString(list.get(0), getMonitorCode(sensorAttrVO.getSensorCode(), sensorAttrVO.getAttrCode()))));
                sensorAttrVO.setMonitorTime(com.chinatower.common.core.utils.StringUtils.toStringNoNull(MapUtils.getString(list.get(0), "monitorTime")));
            }
            if (StringUtils.isNotEmpty(sensorAttrVO.getSensorTypeCode()) && sensorTypeMap.containsKey(sensorAttrVO.getSensorTypeCode())) {
                sensorAttrVO.setSensorName(sensorTypeMap.get(sensorAttrVO.getSensorTypeCode()) + sensorAttrVO.getSuffix());
            }
            else {
                sensorAttrVO.setSensorName(sensorAttrVO.getSensorCode());
            }
            sensorAttrVO.setValueKey(getMonitorCode(sensorAttrVO.getSensorCode(), sensorAttrVO.getAttrCode()));
        }
        result.setSensorAttrsList(sensorAttrs);
        log.info("QuerySensorAttrValueVO param {} ,listSensorAttrValue  param {} ,result {}",
                JSON.toJSONString(deviceReqDTO), JSON.toJSONString(list), JSON.toJSONString(sensorAttrs));
        return Result.data(result);
    }

    @Override
    public Result<SensorPeriodPageResultVO> pageSensorPeriodDataV2(DeviceReqDTO deviceReqDTO) {
        if (StringUtils.isBlank(deviceReqDTO.getDeviceCode())) {
            return Result.fail("查询的设备编码不能为空");
        }
        SensorPeriodPageResultVO result = new SensorPeriodPageResultVO();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = null;
        Date end = null;
        try {
            start = simpleDateFormat.parse(deviceReqDTO.getStartDate());
            end = simpleDateFormat.parse(deviceReqDTO.getEndDate());
        } catch (ParseException e) {
            log.error("日期转换异常", e);
        }
        if (start == null || end == null) {
            return Result.fail("日期格式有误");
        }
        String tableDate = DateUtils.parseDateToStr(DeviceConstants.CURRENT_MONTH_FORMAT, start);
        Page<SensorAttrMapVO> pageInfo =  sensorAttrValueAllMongoDAO.pageSensorPeriodData(deviceReqDTO, tableDate);
        result.setMonitorDataList(pageInfo.getList());
        List<SensorAttrVO> sensorAttrs = querySensorAttr(deviceReqDTO);
        sensorAttrs.forEach(sensorAttrVO -> {
            if (StringUtils.isNotEmpty(sensorAttrVO.getSensorCode()) ) {
                sensorAttrVO.setAttrCode(sensorAttrVO.getSensorCode() + "#" + sensorAttrVO.getAttrCode());
            }
        });
        List<String> sensorTypeCodeList = this.assembleSensorTypeCodeList(sensorAttrs);
        Map<String, String> sensorTypeMap = this.assembleSensorVOMap(sensorTypeCodeList, sensorAttrs);
        List<SensorMonitorInfo> sensorMonitorInfo = this.assembleSensorMonitorInfo(sensorAttrs, sensorTypeMap);
        result.setMonitorDataList(pageInfo.getList());
        result.setSensorMonitorInfo(sensorMonitorInfo);
        result.setCount(pageInfo.getCount());
        return Result.data(result);
    }

    @Override
    public Result<List<SensorMonitorRelInfoVo>> querySensorMonitorRelInfo(DeviceReqDTO deviceReqDTO) {
        if (CollectionUtils.isEmpty(deviceReqDTO.getDeviceCodes())) {
            return Result.fail("设备编码参数为空");
        }
        List<SensorMonitorRelInfoVo> items = iotDeviceMapper.queryMonitorInfoByDeviceCodes(deviceReqDTO);
        List<String> sensorTypeCodeList = this.assembleSensorTypeCodeList(items);
        Map<String, String> sensorTypeMap = this.assembleSensorVOMap(sensorTypeCodeList, items);
        for (SensorMonitorRelInfoVo item : items) {
            if (StringUtils.isNotEmpty(item.getSensorTypeCode())) {
                if (sensorTypeMap.containsKey(item.getSensorTypeCode())) {
                    item.setSensorName(sensorTypeMap.get(item.getSensorTypeCode()) + item.getSuffix());
                }
                else {
                    item.setSensorName(item.getSensorCode());
                }
            }
            else {
                item.setSensorName("");
                item.setSensorCode("");
                item.setSensorTypeCode("");
                item.setSensorTypeName("");
            }
        }
        return Result.data(items);
    }

    @Override
    public Result<List<SensorAttrVO>> getSensorDataBatch(DeviceReqDTO deviceReqDTO) {
        try {
            List<SensorAttrMapVO> sensorAttrMapVOList = getSensorAttrValueBatch(deviceReqDTO);
            List<SensorAttrVO> sensorAttrs = querySensorAttr(deviceReqDTO);
            for (SensorAttrVO sensorAttrVO : sensorAttrs) {
                if (!StringUtils.isBlank(sensorAttrVO.getAttrUnits())){
                    sensorAttrVO.setAttrName(sensorAttrVO.getAttrName() + "(" + sensorAttrVO.getAttrUnits() + ")");
                }
                if (!CollectionUtils.isEmpty(sensorAttrMapVOList)) {
                    for (SensorAttrMapVO sensorAttrMapVO : sensorAttrMapVOList) {
                        if (Objects.equals(MapUtils.getString(sensorAttrMapVO, Constants.DEVICECODE), sensorAttrVO.getDeviceCode())) {
                            sensorAttrVO.setAttrValue(com.chinatower.common.core.utils.StringUtils.toStringNoNull(MapUtils.getString(sensorAttrMapVO, getMonitorCode(sensorAttrVO.getSensorCode(), sensorAttrVO.getAttrCode()))));
                            sensorAttrVO.setMonitorTime(com.chinatower.common.core.utils.StringUtils.toStringNoNull(MapUtils.getString(sensorAttrMapVO, Constants.MONITOR_TIME)));
                            break;
                        }
                    }
                }
            }
            return Result.data(sensorAttrs);
        } catch (Exception e) {
            log.error("批量查询传感器最新数据异常, e:", e);
            return Result.fail("批量查询传感器最新数据失败异常");
        }
    }

    @Override
    public Result<PageInfo<SenSorMonitorConfigDO>> qrySensorMonitor(IotDeviceVO iotDeviceVO) {
        if ( null == iotDeviceVO.getPageNum() || iotDeviceVO.getPageNum() <= 0){
            iotDeviceVO.setPageNum(1);
        }
        if (null == iotDeviceVO.getPageSize() || iotDeviceVO.getPageSize() <= 0){
            iotDeviceVO.setPageSize(10);
        }
        if (iotDeviceVO.getPageSize() > 1000) {
            return Result.fail("单次最大查询数为1000条");
        }
        PageHelper.startPage(iotDeviceVO.getPageNum(), iotDeviceVO.getPageSize());
        List<SenSorMonitorConfigDO> list = iotDeviceMapper.qrySensorMonitor();
        PageInfo<SenSorMonitorConfigDO> pageInfo = new PageInfo<>(list);
        Result<PageInfo<SenSorMonitorConfigDO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    /**
     * 监测数据存值规则
     * 当传感器编码不为空的时候存sensorCode + # + monitorCode
     * 当传感器编码为空就只存monitorCode
     *
     * @return attrCode
     */
    private String assembleMongoSensorAttributeAttrCode(String sensorCode, String monitorCode) {
        String attrCode;
        if (StringUtils.isEmpty(sensorCode)) {
            attrCode = monitorCode;
        }
        else {
            attrCode = sensorCode + "#" + monitorCode;
        }
        return attrCode;
    }

    private String getMonitorCode(String sensorCode, String attrCode) {
        return StringUtil.isEmpty(sensorCode) ? attrCode : sensorCode + "#" + attrCode;
    }

    /**
     * 构建一个List<String> [LJ_QJ,JL_QS] 全是传感器类型的编码集合
     * @param sensorAttrs 物联因子列表
     * @return [LJ_QJ,JL_QS]
     */
    private <T  extends SensorMonitorRelInfoVo> List<String> assembleSensorTypeCodeList(List<T> sensorAttrs) {
        List<String> sensorTypeCodeList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(sensorAttrs)) {
            return sensorTypeCodeList;
        }
        for (T sensorAttr : sensorAttrs) {
            try {
                String sensorCode = sensorAttr.getSensorCode();
                if (StringUtils.isEmpty(sensorCode)) {
                    continue;
                }
                int lastIndex = sensorCode.lastIndexOf('_');
                if (lastIndex != -1) {
                    sensorTypeCodeList.add(sensorCode.substring(0, lastIndex));
                    sensorAttr.setSensorTypeCode(sensorCode.substring(0, lastIndex));
                    sensorAttr.setSuffix(sensorCode.substring(lastIndex));
                }
            } catch (Exception e) {
                log.error("处理传感器名称异常", e);
            }
        }
        return sensorTypeCodeList;
    }


    /**
     * 判断是否包含传感器
     * @param sensorAttrs 物联因子列表
     * @return 包含true 不包含false
     */
    private boolean judgeIsHaveSensor(List<SensorAttrVO> sensorAttrs) {
        return sensorAttrs.stream()
                .anyMatch(sensorAttrVO -> StringUtils.isNotEmpty(sensorAttrVO.getSensorCode()));
    }

    /**
     * 根据查询出来的物联因子列表获取所有的传感器编码 通过assembleSensorCodeMap方法放回的map 构建一个传感器类型为key，传感器类型名称为value的map
     * @param sensorTypeCodeList sensorTypeCodeList
     * @return 构建一个{LJ_QJ:测试}的map为了转换传感器名称的map
     */
    private <T  extends SensorMonitorRelInfoVo> Map<String, String> assembleSensorVOMap(List<String> sensorTypeCodeList, List<T> sensorAttrs) {
        Map<String, String> sensorTypeMap = new HashMap<>(16);
        List<String> sensorCodeList = new ArrayList<>(sensorTypeCodeList);
        if (CollectionUtils.isEmpty(sensorCodeList)) {
            return sensorTypeMap;
        }
        List<SensorDeviceTypeVO> sensorDeviceTypeVOList = iotDeviceMapper.getSensorDeviceTypeByCode(sensorCodeList);
        sensorTypeMap = sensorDeviceTypeVOList.stream()
                .collect(Collectors.toMap(
                        SensorDeviceTypeVO::getTypeCode,
                        SensorDeviceTypeVO::getTypeName
                ));
        for (T sensorAttr : sensorAttrs) {
            sensorAttr.setSensorTypeName(sensorTypeMap.get(sensorAttr.getSensorTypeCode()));
        }
        return sensorTypeMap;
    }


    private List<SensorMonitorInfo> assembleSensorMonitorInfo(List<SensorAttrVO> sensorAttrs, Map<String, String> sensorTypeMap) {
        List<SensorMonitorInfo> sensorMonitorInfoList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(sensorAttrs)) {
            return sensorMonitorInfoList;
        }
        //没有传感器就只有一个集合
        if (this.judgeIsHaveSensor(sensorAttrs)) {
            Map<String, List<SensorAttrVO>> groupedBySensorCode = sensorAttrs.stream()
                    .map(sensor -> {
                        String key = StringUtil.isEmpty(sensor.getSensorCode())
                                ? "tempKey"
                                : sensor.getSensorCode();
                        return new AbstractMap.SimpleEntry<>(key, sensor);
                    })
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.mapping(Map.Entry::getValue, Collectors.toList()) //
                    ));
            for (Map.Entry<String, List<SensorAttrVO>> entry : groupedBySensorCode.entrySet()) {
                //前端结构 有传感器就set名字和code
                SensorMonitorInfo sensorMonitorInfo = new SensorMonitorInfo();
                String sensorCode = entry.getKey();
                if (!StringUtils.equals("tempKey", sensorCode)) {
                    sensorMonitorInfo.setSensorCode(sensorCode);
                    try {
                        int lastIndex = sensorCode.lastIndexOf('_');
                        if (sensorTypeMap.containsKey(sensorCode.substring(0, lastIndex))) {
                            sensorMonitorInfo.setSensorName(sensorTypeMap.get(sensorCode.substring(0, lastIndex)) + sensorCode.substring(lastIndex));
                        } else {
                            sensorMonitorInfo.setSensorName(sensorCode);
                        }
                    } catch (Exception e) {
                        log.error("处理传感器名称异常", e);
                    }
                }
                sensorMonitorInfo.setMonitorList(entry.getValue());
                sensorMonitorInfoList.add(sensorMonitorInfo);
            }
        }
        else {
            SensorMonitorInfo sensorMonitorInfo = new SensorMonitorInfo();
            sensorMonitorInfo.setMonitorList(sensorAttrs);
            sensorMonitorInfoList.add(sensorMonitorInfo);
        }
        return sensorMonitorInfoList;
    }

    /**
     * 根据条件查询物联/雷达设备基本信息
     * @param deviceInfoVO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code",acceptColumn = "od.accept_node", deviceColumn = "od.device_code", alarmSourceVal = "8,10")
    public List<IotDeviceVO> queryIotRadarList(IotDeviceVO deviceInfoVO) {
        return iotDeviceMapper.queryIotRadarList(deviceInfoVO);
    }
}
