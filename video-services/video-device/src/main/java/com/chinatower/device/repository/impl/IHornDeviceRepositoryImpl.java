package com.chinatower.device.repository.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.HornDeviceDo;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.dto.HornCommonEmptyRspVO;
import com.chinatower.device.dto.TalkV2DTO;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.mapper.HornDeviceMapper;
import com.chinatower.device.mapper.TenantDeviceAttachmentMapper;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.repository.IOrgIotDevRelRepository;
import com.chinatower.device.service.ITenantDeviceAttachmentService;
import com.chinatower.device.utils.HornRequestUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.VideoConstants;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 喇叭管理 接口实现类
 */
@Service
@Slf4j
public class IHornDeviceRepositoryImpl extends ServiceImpl<HornDeviceMapper, HornDeviceDo> implements IHornDeviceRepository {

    @Autowired
    HornDeviceMapper hornDeviceMapper;

    @Autowired
    IOrgIotDevRelRepository orgIotDevRelRepository;

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;

    @Autowired
    DeviceInfoMapper deviceInfoMapper;

    @Resource
    private NetConnectUtils connectUtils;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private TenantDeviceAttachmentMapper tenantDeviceAttachmentMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    ICasCadeInfoService iCasCadeInfoService;

    @Autowired
    private ITenantDeviceAttachmentService tenantDeviceAttachmentService;

    @Autowired
    private IHornDeviceRepository iHornDeviceRepository;

    @Autowired
    private HornRequestUtils hornRequestUtils;

    private static final List<String> ON_LINENUM = Arrays.asList("1","2","3");

    private static final String DEVICE_CODE_CAN_NOT_EMPTY_MSG = "设备编码不能为空！";

    private static final String HORN_DEVICE_QUERY_ERROR = "喇叭列表查询异常";

    private static final String TERMINAL_MSG = "Terminal";

    private static final String HORN_WEB_TALK = "HORN_WEB_TALK:";

    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node",
            deviceColumn = "m.device_code", alarmSourceVal = "12")
    @Override
    public Result<PageInfo<HornDeviceVo>> queryHornDevicePage(HornDeviceDTO hornDeviceDTO) {
        try {
            SysUser tokenUser = SecurityUtils.getLoginUser().getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            if(hornDeviceDTO.isExport()){
                hornDeviceDTO.setPageNum(null);
                hornDeviceDTO.setPageSize(null);
            } else {
                if (null == hornDeviceDTO.getPageNum() || hornDeviceDTO.getPageNum() <= Constants.ZERO_NUM) {
                    hornDeviceDTO.setPageNum(Constants.ONE_NUM);
                }
                if (null == hornDeviceDTO.getPageSize() || hornDeviceDTO.getPageSize() <= Constants.ZERO_NUM) {
                    hornDeviceDTO.setPageSize(Constants.TEN_NUM);
                }
                PageMethod.startPage(hornDeviceDTO.getPageNum(), hornDeviceDTO.getPageSize());
            }
            hornDeviceDTO.setTenantId(tokenUser.getTenantId());
            hornDeviceDTO.setIndustryCode(tokenUser.getIndustryCode());
            hornDeviceDTO.setAppVerCode(tokenUser.getAppVerCode());
            hornDeviceDTO.setUserId(user.getUserId());
            //查询设备集合
            List<HornDeviceVo> hornDeviceList = hornDeviceMapper.queryHornDeviceList(hornDeviceDTO);
            IotDeviceVO iotDeviceVO = new IotDeviceVO();
            iotDeviceVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_12));
            iotDeviceVO.setIndustryCode(tokenUser.getIndustryCode());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            if (!StringUtils.isEmpty(hornDeviceDTO.getDeviceCode())) {
                iotDeviceVO.setDeviceCode(hornDeviceDTO.getDeviceCode());
            }
            //查询设备关联的组织信息
            List<OrganDeviceRelVO> organDeviceRelVOS = orgIotDevRelRepository.queryOrgIotDevRels(iotDeviceVO);
            if (!CollectionUtils.isEmpty(organDeviceRelVOS)) {
                for (HornDeviceVo hornDeviceVo : hornDeviceList) {
                    hornDeviceVo.setOrgRels(organDeviceRelVOS.stream().filter(organDeviceRelVO -> hornDeviceVo.getDeviceCode().equals(organDeviceRelVO.getDeviceCode())).collect(Collectors.toList()));
                    hornDeviceVo.setOrgName(hornDeviceVo.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
                }
            }
            PageInfo<HornDeviceVo> pageInfo = new PageInfo<>(hornDeviceList);
            Result<PageInfo<HornDeviceVo>> result = Result.success("查询成功");
            result.setData(pageInfo);
            return result;
        } catch (Exception e) {
            log.error("queryHornDevicePage 查询喇叭列表异常：", e);
            return Result.fail(HORN_DEVICE_QUERY_ERROR);
        }

    }

    /**
     *
     * @param hornDeviceDTO 喇叭设备查询信息
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "12")
    public Result<PageInfo<HornDeviceVo>> queryHornDeviceList(HornDeviceDTO hornDeviceDTO) {
        try {
            if (null == hornDeviceDTO.getPageNum() || hornDeviceDTO.getPageNum() <= Constants.ZERO_NUM) {
                hornDeviceDTO.setPageNum(Constants.ONE_NUM);
            }
            if (null == hornDeviceDTO.getPageSize() || hornDeviceDTO.getPageSize() <= Constants.ZERO_NUM) {
                hornDeviceDTO.setPageSize(Constants.TEN_NUM);
            }
            PageMethod.startPage(hornDeviceDTO.getPageNum(),hornDeviceDTO.getPageSize());
            Result<PageInfo<HornDeviceVo>> result;
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            hornDeviceDTO.setUserId(loginUser.getUserId());
            //查询设备集合
            List<HornDeviceVo> hornDeviceList = hornDeviceMapper.queryHornList(hornDeviceDTO);
            PageInfo<HornDeviceVo> pageInfo = new PageInfo<>(hornDeviceList);
            result = Result.success("查询成功");
            result.setData(pageInfo);
            return result;
        } catch (Exception e) {
            log.error("queryHornDeviceList 查询喇叭列表异常：", e);
            return Result.fail(HORN_DEVICE_QUERY_ERROR);
        }
    }

    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "12")
    public Result<HornDeviceVo> queryHornDeviceDetail(HornDeviceDTO hornDeviceDTO) {
        if (StringUtils.isEmpty(hornDeviceDTO.getDeviceCode())) {
            return Result.fail("喇叭编码必传！");
        }
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser tokenUser = currentUser.getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = baseMapper.selectUserOnlyByUserName(tokenUser.getUserName());
        hornDeviceDTO.setUserId(user.getUserId());
        HornDeviceVo hornDeviceVo = hornDeviceMapper.queryHornDeviceDetail(hornDeviceDTO);
        if (Objects.nonNull(hornDeviceVo)) {

            IotDeviceVO iotDeviceVO = new IotDeviceVO();
            iotDeviceVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_12));
            iotDeviceVO.setIndustryCode(tokenUser.getIndustryCode());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            iotDeviceVO.setDeviceCode(hornDeviceDTO.getDeviceCode());
            //查询设备关联的组织信息
            List<OrganDeviceRelVO> organDeviceRelVOS = orgIotDevRelRepository.queryOrgIotDevRels(iotDeviceVO);
            if (!CollectionUtils.isEmpty(organDeviceRelVOS)){
                hornDeviceVo.setOrgRels(organDeviceRelVOS.stream().filter(organDeviceRelVO -> hornDeviceVo.getDeviceCode().equals(organDeviceRelVO.getDeviceCode())).collect(Collectors.toList()));
                hornDeviceVo.setOrgName(hornDeviceVo.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }

            List<String> encryptUrlList = dealObsFileUrlBatch(hornDeviceVo.getDeviceCode());
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
            hornDeviceVo.setFileUrlList(encryptUrlList);
        }
        return Result.data(hornDeviceVo);
    }

    /**
     * 根据设备编号查询上传的附件信息
     * @param hornDeviceDTO
     * @return
     */
    @Override
    public Result<List<String>> getFileUrlByDeviceCode(HornDeviceDTO hornDeviceDTO) {
        List<String> fileUrl = null;
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            //查询上传的附件信息
            List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCode(
                    user.getTenantId(), user.getIndustryCode(), hornDeviceDTO.getDeviceCode(), hornDeviceDTO.getDeviceType());
            if (!CollectionUtils.isEmpty(fileUrlList)) {
                Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
                // 处理Obs文件URL加密返回
                if (stringStringMap != null && stringStringMap.size() > 0) {
                    fileUrl = Lists.newArrayList(stringStringMap.values());
                }
            }
        } catch (Exception e) {
            log.error("IHornDeviceRepositoryImpl getFileUrlByDeviceCode error, e:", e);
        }
        return Result.data(fileUrl);
    }

    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node",
            deviceColumn = "m.device_code", alarmSourceVal = "12",
            areaPro="m.province_code", areaCity="m.city_code")
    public List<DeviceTreeVO> getHornMonitorTree(HornDeviceDTO hornDeviceDTO) {
        return hornDeviceMapper.getHornMonitorTree(hornDeviceDTO);
    }

    @Override
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node",
            deviceColumn = "m.device_code", deptColumn = "dd.dept_id", alarmSourceVal = "12")
    public List<DeviceTreeVO> getHornMonitorOrgTree(HornDeviceDTO hornDeviceDTO) {
        return formatOrgTreeList(hornDeviceMapper.getHornMonitorOrgTree(hornDeviceDTO));
    }

    private List<DeviceTreeVO> buildIotMonitorOrgTree(List<DeviceTreeVO> treeList) {
        List<String> onlineNum = Arrays.asList("1","2","3");
        List<DeviceTreeVO> resTreeList = new ArrayList<>();
        treeList.forEach(info -> {
            info.setOnlineNum((int) info.getList()
                    .stream()
                    .filter(devCodeVo -> onlineNum.contains(devCodeVo.getStatus())).count());
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
        treeList.forEach(info -> {
            if (treeList.stream().filter(tree -> tree.getCode().equals(info.getParentCode())).count() == 0) {
                resTreeList.add(info);
            }
        });
        resTreeList.forEach(res -> {
            List<DeviceTreeVO> childList = getChild(res.getCode(),treeList);
            if (!CollectionUtils.isEmpty(childList)) {
                res.getList().addAll(childList);
                res.setNum(res.getNum() + childList.stream().mapToInt(DeviceTreeVO::getNum).sum());
                res.setOnlineNum(res.getOnlineNum() + childList.stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                res.setIsMonitorNum(res.getIsMonitorNum() + childList.stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                //子节点都收藏标记上级节点为收藏
                if (res.getNum() == res.getIsMonitorNum()) {
                    res.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                } else {
                    res.setIsMonitor(String.valueOf(IndustryConstants.NO_COLLECTION));
                }
            }
        });
        return resTreeList;
    }

    /**
     * 格式化设备组织树
     * @param monitorList
     * @return
     */
    @Override
    public List<DeviceTreeVO> formatOrgTreeList(List<DeviceTreeVO> monitorList) {
        List<DeviceTreeVO> treeList;
        Set<String> orgList = new HashSet<>();
        //获取有设备权限的所有组织编码
        monitorList.forEach(e -> orgList.add(e.getCode()));
        // 设置数量
        monitorList.forEach(info -> {
            info.setOnlineNum((int) info.getList()
                    .stream()
                    .filter(devCodeVo -> ON_LINENUM.contains(devCodeVo.getStatus())).count());
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
            info.getList().forEach(dev -> {
                dev.setStatus(ON_LINENUM.contains(dev.getStatus()) ? Constants.ZERO : Constants.ONE);
            });
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
            sortVO.setOnlineNum((int) deviceList.stream().filter(mon -> Objects.equals(Constants.ZERO, mon.getStatus())).count());
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

    private List<DeviceTreeVO> getChild(String parentCode,List<DeviceTreeVO> treeList) {
        List<DeviceTreeVO> childList = treeList.stream().filter(tree -> parentCode.equals(tree.getParentCode())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(childList)) {
            childList.forEach(child -> {
                List<DeviceTreeVO> childList1 = getChild(child.getCode(),treeList);
                child.getList().addAll(childList1);
                child.setNum(child.getNum() + childList1.stream().mapToInt(DeviceTreeVO::getNum).sum());
                child.setOnlineNum(child.getOnlineNum() + childList1.stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                child.setIsMonitorNum(child.getIsMonitorNum() + childList1.stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                //子节点都收藏标记上级节点为收藏
                if (child.getNum() == child.getIsMonitorNum()) {
                    child.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                } else {
                    child.setIsMonitor(String.valueOf(IndustryConstants.NO_COLLECTION));
                }
            });
        }
        return childList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateHornDevice(HornDeviceDTO hornDeviceDTO) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            if (StringUtils.isEmpty(hornDeviceDTO.getDeviceCode())) {
                return Result.fail("设备名称不能为空!");
            }
            if (StringUtils.isEmpty(hornDeviceDTO.getTenantId())) {
                hornDeviceDTO.setTenantId(user.getTenantId());
            }
            hornDeviceDTO.setIndustryCode(user.getIndustryCode());
            hornDeviceDTO.setDeviceType(Constants.TWO_NUM);
            //客户视角修改设备别名，不修改主表名称
            //先查询设备是否有别名
            int count = hornDeviceMapper.queryHornAliasName(hornDeviceDTO);
            //存在直接修改别名
            if (count > Constants.ZERO_NUM) {
                hornDeviceDTO.setModifier(user.getUserName());
                hornDeviceDTO.setUpdateTime(DateUtils.getNowDate());
                hornDeviceMapper.updateHornAliasName(hornDeviceDTO);
            } else {
                //不存在则新增一个别名
                hornDeviceDTO.setAppVerCode(user.getAppVerCode());
                hornDeviceDTO.setCreator(user.getUserName());
                hornDeviceDTO.setCreateTime(DateUtils.getNowDate());
                hornDeviceDTO.setDeleteFlag(0);
                hornDeviceMapper.addHornAliasName(hornDeviceDTO);
            }
            //所属组织处理
            //先删除设备原有组织信息
            OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
            organDeviceRelVO.setDeviceType(DeviceConstants.DEVICETYPE_12);
            organDeviceRelVO.setIndustryCode(user.getIndustryCode());
            organDeviceRelVO.setTenantId(user.getTenantId());
            organDeviceRelVO.setDeviceCode(hornDeviceDTO.getDeviceCode());
            hornDeviceMapper.deleteHornOrgList(organDeviceRelVO);
            //如果新的设备组织信息不为空 则重新添加 若为空 则直接返回
            if (!CollectionUtils.isEmpty(hornDeviceDTO.getDeptIds())) {
                List<String> deptIds = hornDeviceDTO.getDeptIds();
                OrganDeviceRelVO hornOrgRelVO;
                List<OrganDeviceRelVO> orgRelList = new ArrayList<>();
                for (String deptId : deptIds) {
                    hornOrgRelVO = new OrganDeviceRelVO();
                    hornOrgRelVO.setDeptId(deptId);
                    hornOrgRelVO.setDeviceCode(hornDeviceDTO.getDeviceCode());
                    hornOrgRelVO.setDeviceType(DeviceConstants.DEVICETYPE_12);
                    hornOrgRelVO.setCreateBy(user.getUserName());
                    hornOrgRelVO.setCreateTime(DateUtils.getNowDate());
                    hornOrgRelVO.setUpdateBy(user.getUserName());
                    hornOrgRelVO.setUpdateTime(DateUtils.getNowDate());
                    hornOrgRelVO.setDeleteFlag(Constants.ZERO);
                    hornOrgRelVO.setIndustryCode(user.getIndustryCode());
                    hornOrgRelVO.setTenantId(user.getTenantId());
                    hornOrgRelVO.setAppVerCode(user.getAppVerCode());
                    orgRelList.add(hornOrgRelVO);
                }
                hornDeviceMapper.addHornOrgRelList(orgRelList);
                //保存上传的附件信息
                hornDeviceDTO.setTenantId(user.getTenantId());
                hornDeviceDTO.setIndustryCode(user.getIndustryCode());
                hornDeviceDTO.setAppVerCode(user.getAppVerCode());
                hornDeviceDTO.setCreator(user.getUserName());
                //设备类型：1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达
                hornDeviceDTO.setDeviceType(IndustryConstants.THREE_NUM);
                deviceInfoRepository.saveDeviceAttachmentInfo(hornDeviceDTO);
            }
            return Result.success("修改成功！");
        } catch (Exception e) {
            log.info("修改喇叭设备信息失败：", e);
            return Result.fail("修改喇叭设备信息失败");
        }
    }

    @Override
    public Result<LockResultVo> startTalk(TalkReq talkReq) {

        try {
            if (StringUtils.isEmpty(talkReq.getDeviceCode())) {
                return Result.fail(DEVICE_CODE_CAN_NOT_EMPTY_MSG);
            }
            NetConnectUtils.RequestParamter<TalkReq> paramter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("点击播放接口入参{}", JSON.toJSONString(talkReq));
            //1.抢占设备控制权限（加锁）
            //调用加锁接口
            LockResultVo lockResultVo = getLock(talkReq, paramter, path);
            if (null == lockResultVo || Constants.ERROR_CODE.equals(lockResultVo.getReturnCode())) {
                return Result.fail(null == lockResultVo ? "抢占设备控制权限（加锁）返回为空" : lockResultVo.getErrorMsg());
            }
            String lockId = lockResultVo.getData().getLockId();
            //2.执行播发
            if (StringUtils.isEmpty(lockId)) {
                return Result.fail("未能拿到锁ID,不能执行播发！");
            }
            talkReq.setLockId(lockId);
            //调用执行播发接口
            LockResultVo playResult = startPlay(talkReq, paramter, path);
            if (null == playResult || Constants.ERROR_CODE.equals(playResult.getReturnCode())) {
                //播发失败需要释放锁
                releaseLock(talkReq, paramter, path);
                return Result.fail(null == playResult ? "执行播发返回为空" : playResult.getErrorMsg());
            }
            //前两步执行成功后将锁ID存到设备与锁表中vm_horn_device_lock_t
            talkReq.setCreateTime(DateUtils.getNowDate());
            hornDeviceMapper.addHornDeviceLockInfo(talkReq);
            playResult.getData().setLockId(lockId);
            return Result.data(playResult);
        } catch (Exception e) {
            log.info("喊话接口调用失败：", e);
            return Result.fail("喊话接口调用失败" + e.getMessage());
        }

    }

    @Override
    public Result<String> stopTalk(TalkReq talkReq) {
        Result<String> result = null;
        try {
            if (StringUtils.isEmpty(talkReq.getDeviceCode())) {
                return Result.fail(DEVICE_CODE_CAN_NOT_EMPTY_MSG);
            }
            if (StringUtils.isEmpty(talkReq.getLockId())) {
                return Result.fail("锁ID不能为空！");
            }
            NetConnectUtils.RequestParamter<TalkReq> paramter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("停止播放接口入参{}", JSON.toJSONString(talkReq));
            //调用释放锁接口
            LockResultVo lockResultVo = releaseLock(talkReq, paramter, path);
            if (null == lockResultVo || Constants.ERROR_CODE.equals(lockResultVo.getReturnCode())) {
                return Result.fail(null == lockResultVo ? "释放锁返回数据为空" : lockResultVo.getErrorMsg());
            }
            //成功释放锁后，删除设备锁表中对应数据
            List<TalkReq> talkReqList = new ArrayList<>();
            talkReqList.add(talkReq);
            hornDeviceMapper.deleteLockInfo(talkReqList);
            result = Result.success(lockResultVo.getReturnMsg());
        } catch (Exception e) {
            log.info("停止喊话接口调用失败：", e);
            result = Result.fail("停止喊话接口调用失败" + e.getMessage());
        }
        return result;

    }

    /**
     * {
     *     "broadcastType": "WEB_STREAM",
     *     "isRecord": 0,
     *     "priority": 0,
     *     "rangeInfoList": [
     *         {
     *             "devType": "Terminal",
     *             "order": 0,
     *             "rangeDevInfoList": [
     *                 {
     *                     "deviceCode": "150-015-03378-20211227-60011",
     *                     "order": 0
     *                 }
     *             ]
     *         }
     *     ],
     *     "streamCodeId": 6,
     *     "volume": 5
     * }
     */
    @Override
    public Result<TalkResultVO<TalkV2VO>> startTalkV2(TalkV2DTO request) {

        TalkV2Req talkReq = new TalkV2Req();
        talkReq.setBroadcastType("WEB_STREAM");
        talkReq.setIsRecord(0);
        talkReq.setPriority(0);
        List<HornRangeInfoDTO> rangeInfoList = Lists.newArrayList();
        HornRangeInfoDTO rangeInfo = new HornRangeInfoDTO();
        rangeInfo.setOrder(0);
        rangeInfo.setDevType(TERMINAL_MSG);
        rangeInfo.setRangeDevInfoList(request.getDeviceInfoList());
        rangeInfoList.add(rangeInfo);
        talkReq.setRangeInfoList(rangeInfoList);
        talkReq.setStreamCodeId(request.getStreamCodeId());
        talkReq.setVolume(request.getVolume());

        Result<TalkResultVO<TalkV2VO>> result = null;
        try {
            List<String> deviceCodeList = request.getDeviceInfoList().stream().map(HornRangeDevInfoDTO::getDeviceCode).collect(Collectors.toList());
            TalkResultVO<TalkV2VO> resultVO = hornRequestUtils.callHornSystem(talkReq, NetConnectUtils.RequestParamter.URLMappingEnum.START_PLAY_V2, TalkV2VO.class, deviceCodeList);
            log.info("startTalkV2接口返回报文{}", JSON.toJSONString(resultVO));
            if (null == resultVO || Constants.ERROR_CODE.equals(resultVO.getReturnCode())) {
                return Result.fail(null == resultVO ? "startTalkV2返回数据为空" : resultVO.getErrorMsg());
            }
            if (Constants.SUCCESS_CODE.equals(resultVO.getReturnCode())) {
                String redisKey = HORN_WEB_TALK + resultVO.getData().getSessionId();
                RedisUtil.set(redisKey , deviceCodeList, 2, TimeUnit.HOURS, RedisDBType.PRIMARY);
            }
            result = Result.data(resultVO);
        } catch (Exception e) {
            log.info("startTalkV2接口调用失败：", e);
            result = Result.fail("startTalkV2接口调用失败" + e.getMessage());
        }
        return result;
    }

    @Override
    public Result<String> stopTalkV2(TalkV2Req talkReq) {
        Result<String> result = null;
        try {
            String redisKey = HORN_WEB_TALK + talkReq.getSessionId();
            List<String> deviceCodeList = RedisUtil.getList(redisKey, String.class, RedisDBType.PRIMARY);
            TalkResultVO<HornCommonEmptyRspVO> resultVO = hornRequestUtils.callHornSystem(talkReq, NetConnectUtils.RequestParamter.URLMappingEnum.STOP_PLAY_V2, HornCommonEmptyRspVO.class, deviceCodeList);
            log.info("stopTalkV2接口返回报文{}", JSON.toJSONString(resultVO));
            if (null == resultVO || Constants.ERROR_CODE.equals(resultVO.getReturnCode())) {
                return Result.fail(null == resultVO ? "stopTalkV2返回数据为空" : resultVO.getErrorMsg());
            }
            if (Constants.SUCCESS_CODE.equals(resultVO.getReturnCode())) {
                RedisUtil.delete(redisKey, RedisDBType.PRIMARY);
            }
            result = Result.success(resultVO.getReturnMsg());
        } catch (Exception e) {
            log.info("stopTalkV2接口调用失败：", e);
            result = Result.fail("stopTalkV2接口调用失败" + e.getMessage());
        }
        return result;
    }

    @Override
    public Result<TalkResultVO<TalkV2VO>> startTalkAppV2(TalkV2DTO request) {

        TalkV2Req talkReq = new TalkV2Req();
        talkReq.setBroadcastType("APP_STREAM");
        talkReq.setIsRecord(0);
        talkReq.setPriority(0);
        List<HornRangeInfoDTO> rangeInfoList = Lists.newArrayList();
        HornRangeInfoDTO rangeInfo = new HornRangeInfoDTO();
        rangeInfo.setOrder(0);
        rangeInfo.setDevType(TERMINAL_MSG);
        rangeInfo.setRangeDevInfoList(request.getDeviceInfoList());
        rangeInfoList.add(rangeInfo);
        talkReq.setRangeInfoList(rangeInfoList);
        talkReq.setVolume(request.getVolume());
        // 为true时，终端忽略音量参数，强制以最大音量播放 默认false
        talkReq.setEmergency(false);
        if (request.getEmergency() != null) {
            talkReq.setEmergency(request.getEmergency());
        }

        Result<TalkResultVO<TalkV2VO>> result = null;
        try {
            List<String> deviceCodeList = request.getDeviceInfoList().stream().map(HornRangeDevInfoDTO::getDeviceCode).collect(Collectors.toList());
            TalkResultVO<TalkV2VO> resultVO = hornRequestUtils.callHornSystem(talkReq, NetConnectUtils.RequestParamter.URLMappingEnum.START_PLAY_APP_V2, TalkV2VO.class, deviceCodeList);
            log.info("startTalkAppV2接口返回报文{}", JSON.toJSONString(resultVO));
            if (null == resultVO || Constants.ERROR_CODE.equals(resultVO.getReturnCode())) {
                return Result.fail(null == resultVO ? "startTalkAppV2返回数据为空" : resultVO.getErrorMsg());
            }
            if (Constants.SUCCESS_CODE.equals(resultVO.getReturnCode())) {
                String redisKey = HORN_WEB_TALK + resultVO.getData().getSessionId();
                RedisUtil.set(redisKey , deviceCodeList, 2, TimeUnit.HOURS, RedisDBType.PRIMARY);
            }
            result = Result.data(resultVO);
        } catch (Exception e) {
            log.info("startTalkAppV2接口调用失败：", e);
            result = Result.fail("startTalkAppV2接口调用失败" + e.getMessage());
        }
        return result;
    }

    @Override
    public Result<String> stopTalkAppV2(TalkV2Req talkReq) {
        Result<String> result = null;
        try {
            String redisKey = HORN_WEB_TALK + talkReq.getSessionId();
            List<String> deviceCodeList = RedisUtil.getList(redisKey, String.class, RedisDBType.PRIMARY);
            TalkResultVO<HornCommonEmptyRspVO> resultVO = hornRequestUtils.callHornSystem(talkReq, NetConnectUtils.RequestParamter.URLMappingEnum.STOP_PLAY_APP_V2, HornCommonEmptyRspVO.class, deviceCodeList);
            log.info("stopTalkAppV2接口返回报文{}", JSON.toJSONString(resultVO));
            if (null == resultVO || Constants.ERROR_CODE.equals(resultVO.getReturnCode())) {
                return Result.fail(null == resultVO ? "stopTalkAppV2返回数据为空" : resultVO.getErrorMsg());
            }
            if (Constants.SUCCESS_CODE.equals(resultVO.getReturnCode())) {
                RedisUtil.delete(redisKey, RedisDBType.PRIMARY);
            }
            result = Result.success(resultVO.getReturnMsg());
        } catch (Exception e) {
            log.info("stopTalkAppV2接口调用失败：", e);
            result = Result.fail("stopTalkAppV2接口调用失败" + e.getMessage());
        }
        return result;
    }

    @Override
    public Result<String> heartbeatV2(TalkV2Req talkReq) {
        Result<String> result = null;
        try {

            TalkResultVO<HornCommonEmptyRspVO> resultVO = hornRequestUtils.callHornSystem(talkReq, NetConnectUtils.RequestParamter.URLMappingEnum.HEARTBEAT_V2, HornCommonEmptyRspVO.class, null);

            log.info("heartbeatV2接口返回报文{}", JSON.toJSONString(resultVO));

            if (null == resultVO || Constants.ERROR_CODE.equals(resultVO.getReturnCode())) {
                return Result.fail(null == resultVO ? "heartbeatV2返回数据为空" : resultVO.getErrorMsg());
            }
            result = Result.success(resultVO.getReturnMsg());
        } catch (Exception e) {
            log.info("heartbeatV2接口调用失败：", e);
            result = Result.fail("heartbeatV2接口调用失败" + e.getMessage());
        }
        return result;
    }

    @Override
    public Result<TalkV3VO> startTalkV3(TalkV2DTO request) {
        TalkV3VO talk3Req = new TalkV3VO();
        // 参数map key -> deviceCode value -> 设备信息
        Map<String, HornRangeDevInfoDTO> paramDeviceMap = request.getDeviceInfoList().stream().collect(Collectors.toMap(HornRangeDevInfoDTO::getDeviceCode, p -> (p), (a, b) -> a));
        // 设备编码列表
        List<String> deviceCodeList = Lists.newArrayList(paramDeviceMap.keySet());
        // 数据库设备信息
        List<DeviceTreeVO> deviceInfoList = hornDeviceMapper.queryAccessNodeInfoByDeviceCodeList(deviceCodeList);
        // 处于工作中的设备
        List<DeviceTreeVO> isWorkList = deviceInfoList.stream().filter(x -> StringUtil.equals("2", x.getStatus())).collect(Collectors.toList());
        talk3Req.setIsWorkingDeviceNameList(isWorkList.stream().map(DeviceTreeVO::getDeviceName).collect(Collectors.toList()));
        deviceInfoList.removeAll(isWorkList);
        // 数据库设备map key -> 接入节点 value -> 设备信息列表
        Map<String, List<DeviceTreeVO>> deviceMap = deviceInfoList.stream().collect(Collectors.groupingBy(DeviceTreeVO::getAccessNode));

        List<TalkV2DTO> requestList = Lists.newArrayList();
        for (Map.Entry<String, List<DeviceTreeVO>> entry : deviceMap.entrySet()) {
            String accessNode = entry.getKey();
            List<String> deviceNameList = new ArrayList<>(entry.getValue().size());
            List<HornRangeDevInfoDTO> subDeviceInfoList = entry.getValue().stream().map(x -> {
                String deviceCode = x.getDeviceCode();
                HornRangeDevInfoDTO devInfo = new HornRangeDevInfoDTO();
                devInfo.setDeviceCode(deviceCode);
                devInfo.setOrder(0);
                if (paramDeviceMap.containsKey(deviceCode)) {
                    devInfo.setOrder(paramDeviceMap.get(deviceCode).getOrder());
                }
                deviceNameList.add(x.getDeviceName());
                return devInfo;
            }).collect(Collectors.toList());
            TalkV2DTO subRequest = new TalkV2DTO();
            subRequest.setDeviceInfoList(subDeviceInfoList);
            subRequest.setAccessNode(accessNode);
            subRequest.setStreamCodeId(request.getStreamCodeId());
            subRequest.setVolume(request.getVolume());
            subRequest.setDeviceNameList(deviceNameList);
            requestList.add(subRequest);
        }
        if (CollectionUtils.isEmpty(requestList)) {
            return Result.data(talk3Req);
        }
        List<TalkV2VO> list = ThreadParallelUtil.run(requestList, it -> {
            TalkV2Req talkReq = new TalkV2Req();
            talkReq.setBroadcastType("WEB_STREAM");
            talkReq.setIsRecord(0);
            talkReq.setPriority(0);
            List<HornRangeInfoDTO> rangeInfoList = Lists.newArrayList();
            HornRangeInfoDTO rangeInfo = new HornRangeInfoDTO();
            rangeInfo.setOrder(0);
            rangeInfo.setDevType(TERMINAL_MSG);
            rangeInfo.setRangeDevInfoList(it.getDeviceInfoList());
            rangeInfoList.add(rangeInfo);
            talkReq.setRangeInfoList(rangeInfoList);
            talkReq.setStreamCodeId(it.getStreamCodeId());
            talkReq.setVolume(it.getVolume());
            TalkV2VO data = new TalkV2VO();
            try {
                List<String> deviceCodes = it.getDeviceInfoList().stream().map(HornRangeDevInfoDTO::getDeviceCode).collect(Collectors.toList());
                TalkResultVO<TalkV2VO> resultVO = hornRequestUtils.callHornSystemDiffNode(talkReq, NetConnectUtils.RequestParamter.URLMappingEnum.START_PLAY_V2, TalkV2VO.class, it.getAccessNode(), deviceCodes);
                log.info("startTalkV3接口返回报文{}", JSON.toJSONString(resultVO));
                if (Constants.SUCCESS_CODE.equals(resultVO.getReturnCode()) && Objects.nonNull(resultVO.getData())) {
                    String redisKey = HORN_WEB_TALK + resultVO.getData().getSessionId();
                    RedisUtil.set(redisKey, deviceCodeList, 2, TimeUnit.HOURS, RedisDBType.PRIMARY);
                }
                data = resultVO.getData();
            } catch (Exception e) {
                data.setMsg(e.getMessage());
                log.info("startTalkV3接口调用失败：", e);
            }
            data.setAccessNode(it.getAccessNode());
            data.setDeviceNameList(it.getDeviceNameList());
            return data;
        }, ThreadParallelUtil.ConfigFactory.HORN_START_TALK);
        talk3Req.setLiveList(list);
        return Result.data(talk3Req);
    }

    @Override
    public Result<List<TalkV2VO>> stopTalkV3(List<TalkV2Req> request) {
        List<TalkV2VO> list = ThreadParallelUtil.run(request, it -> {
            TalkV2VO result = new TalkV2VO();
            result.setSessionId(it.getSessionId());
            result.setAccessNode(it.getAccessNode());
            try {
                String redisKey = HORN_WEB_TALK + it.getSessionId();
                List<String> deviceCodeList = RedisUtil.getList(redisKey, String.class, RedisDBType.PRIMARY);
                TalkResultVO<HornCommonEmptyRspVO> resultVO = hornRequestUtils.callHornSystemDiffNode(it, NetConnectUtils.RequestParamter.URLMappingEnum.STOP_PLAY_V2, HornCommonEmptyRspVO.class, it.getAccessNode(), deviceCodeList);
                log.info("stopTalkV3接口返回报文{}", JSON.toJSONString(resultVO));
                if (null == resultVO || Constants.ERROR_CODE.equals(resultVO.getReturnCode())) {
                    result.setMsg(null == resultVO ? "stopTalkV3返回数据为空" : resultVO.getErrorMsg());
                    return result;
                }
                if (Constants.SUCCESS_CODE.equals(resultVO.getReturnCode())) {
                    RedisUtil.delete(redisKey, RedisDBType.PRIMARY);
                }
                result.setMsg(resultVO.getReturnMsg());
            } catch (Exception e) {
                log.info("stopTalkV3接口调用失败：", e);
                result.setMsg("stopTalkV3接口调用失败" + e.getMessage());
            }
            return result;

        }, ThreadParallelUtil.ConfigFactory.HORN_START_TALK);
        return Result.data(list);
    }

    @Override
    public Result<List<TalkV2VO>> heartbeatV3(List<TalkV2Req> request) {
        List<TalkV2VO> list = ThreadParallelUtil.run(request, it -> {
            TalkV2VO result = new TalkV2VO();
            result.setSessionId(it.getSessionId());
            result.setAccessNode(it.getAccessNode());
            try {
                TalkResultVO<HornCommonEmptyRspVO> resultVO = hornRequestUtils.callHornSystemDiffNode(it, NetConnectUtils.RequestParamter.URLMappingEnum.HEARTBEAT_V2, HornCommonEmptyRspVO.class, it.getAccessNode(), null);
                log.info("heartbeatV2接口返回报文{}", JSON.toJSONString(resultVO));
                if (null == resultVO || Constants.ERROR_CODE.equals(resultVO.getReturnCode())) {
                    result.setMsg(null == resultVO ? "heartbeatV2返回数据为空" : resultVO.getErrorMsg());
                    return result;
                }
                result.setMsg(resultVO.getReturnMsg());
            } catch (Exception e) {
                log.info("heartbeatV2接口调用失败：", e);
                result.setMsg("heartbeatV2接口调用失败" + e.getMessage());
            }
            return result;

        }, ThreadParallelUtil.ConfigFactory.HORN_START_TALK);
        return Result.data(list);
    }

    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "12")
    public List<DeviceDistanceVO> queryHornDeviceDistanceInfo(DeviceDistanceDTO param) {
        if (param == null
                || StringUtils.isEmpty(param.getLongitude())
                || StringUtils.isEmpty(param.getLatitude())
                || StringUtils.isEmpty(param.getRadius())) {
            throw new CustomException("经纬度、探索半径参数不能为空");
        }
        return hornDeviceMapper.queryHornDeviceDistanceInfo(param);
    }

    /**
     *
     * @param param 参数
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "12")
    public List<DeviceDistanceVO> queryHornDeviceDistanceInfo4App(DeviceDistanceDTO param) {
        if (param == null
                || StringUtils.isEmpty(param.getLongitude())
                || StringUtils.isEmpty(param.getLatitude())) {
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

        return queryHornDeviceDistanceInfo(param);
    }


    /**
     *
     * @param hornDeviceDTO 入参-设备编码
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "12")
    public Result<HornDeviceVo> queryHornInfoByCode(HornDeviceDTO hornDeviceDTO) {
        if (StringUtils.isEmpty(hornDeviceDTO.getDeviceCode())){
            return Result.fail(DEVICE_CODE_CAN_NOT_EMPTY_MSG);
        }
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser tokenUser = currentUser.getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = baseMapper.selectUserOnlyByUserName(tokenUser.getUserName());
        hornDeviceDTO.setUserId(user.getUserId());
        HornDeviceVo hornDeviceVo = hornDeviceMapper.queryHornDeviceDetail(hornDeviceDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        iotDeviceVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_12));
        iotDeviceVO.setIndustryCode(tokenUser.getIndustryCode());
        iotDeviceVO.setTenantId(tokenUser.getTenantId());
        iotDeviceVO.setDeviceCode(hornDeviceDTO.getDeviceCode());

        //查询设备关联的组织信息
        List<OrganDeviceRelVO> organDeviceRelVOS = orgIotDevRelRepository.queryOrgIotDevRels(iotDeviceVO);
        if (!CollectionUtils.isEmpty(organDeviceRelVOS)){
            hornDeviceVo.setOrgRels(organDeviceRelVOS.stream().filter(organDeviceRelVO -> hornDeviceVo.getDeviceCode().equals(organDeviceRelVO.getDeviceCode())).collect(Collectors.toList()));
            hornDeviceVo.setOrgName(hornDeviceVo.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
        }
        return Result.data(hornDeviceVo);
    }

    //抢锁
    public LockResultVo getLock(TalkReq talkReq, NetConnectUtils.RequestParamter<TalkReq> param, NetConnectUtils.RequestParamter.Path path) throws Exception {

        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.ADD_LOCK);
        //接口路径参数
        path.setDeviceCode(talkReq.getDeviceCode());
        //设置地址
        param.setPath(path);

        //属地化数据统一改造 add by wangyt 20240116
        Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceInfo(talkReq.getDeviceCode(),
                com.chinatower.common.core.constant.Constants.DEVICE_TYPE_HORN);
        log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
        CasCadeVO casCadeVO = new CasCadeVO();
        if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
            casCadeVO = casCadeVOResult.getData();
        }

        //调用外部接口
        log.info("抢锁接口入参{}", JSON.toJSONString(param));
        LockResultVo lockResultVo = connectUtils.post(param, LockResultVo.class,
                NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_AIOT),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(param)));
        log.info("抢锁接口返回报文{}", JSON.toJSONString(lockResultVo));
        return lockResultVo;
    }

    //执行播发
    public LockResultVo startPlay(TalkReq talkReq, NetConnectUtils.RequestParamter<TalkReq> paramter, NetConnectUtils.RequestParamter.Path path) throws Exception {

        //设置请求参数
        paramter.setBody(talkReq);
        //获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.START_PLAY);
        //接口路径参数
        path.setDeviceCode(talkReq.getDeviceCode());
        //设置地址
        paramter.setPath(path);

        //属地化数据统一改造 add by wangyt 20240116
        Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceInfo(talkReq.getDeviceCode(),
                com.chinatower.common.core.constant.Constants.DEVICE_TYPE_HORN);
        log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
        CasCadeVO casCadeVO = new CasCadeVO();
        if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
            casCadeVO = casCadeVOResult.getData();
        }

        //调用外部接口
        log.info("执行播发接口入参{}", JSON.toJSONString(paramter));
        LockResultVo lockResultVo = connectUtils.post(paramter, LockResultVo.class,
                NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_AIOT),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
        log.info("执行播发接口返回报文{}", JSON.toJSONString(lockResultVo));
        return lockResultVo;
    }

    //释放设备控制权限（解锁）
    public LockResultVo releaseLock(TalkReq talkReq, NetConnectUtils.RequestParamter<TalkReq> paramter, NetConnectUtils.RequestParamter.Path path) throws Exception {
        //设置请求参数
        paramter.setBody(talkReq);
        //获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.RELEASE_LOCK);
        //接口路径参数
        path.setDeviceCode(talkReq.getDeviceCode());
        //设置地址
        paramter.setPath(path);

        //属地化数据统一改造 add by wangyt 20240116
        Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceInfo(talkReq.getDeviceCode(),
                com.chinatower.common.core.constant.Constants.DEVICE_TYPE_HORN);
        log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
        CasCadeVO casCadeVO = new CasCadeVO();
        if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
            casCadeVO = casCadeVOResult.getData();
        }

        log.info("解锁接口入参{}", JSON.toJSONString(paramter));
        LockResultVo lockResultVo = connectUtils.post(paramter, LockResultVo.class,
                NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_AIOT),
                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
        log.info("解锁接口返回报文{}", JSON.toJSONString(lockResultVo));
        return lockResultVo;
    }

    @Override
    public SysUser selectUserOnlyByUserName(String userName) {
        return hornDeviceMapper.selectUserOnlyByUserName(userName);
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", acceptColumn = "t1.accept_node", industryCode = "t1.industry_code", alarmSourceVal = "12")
    @Override
    public List<HornDeviceVo> queryHornForArea(HornDeviceDTO hornDeviceDTO) {
        return hornDeviceMapper.queryHornForArea(hornDeviceDTO);
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", acceptColumn = "t1.accept_node", industryCode = "t1.industry_code", alarmSourceVal = "12")
    @Override
    public List<HornDeviceVo> querySiteDevTree(HornDeviceDTO hornDeviceDTO) {
        return hornDeviceMapper.querySiteDevTree(hornDeviceDTO);
    }

    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", alarmSourceVal = "12")
    @Override
    public List<HornDeviceVo> queryHornForSite(HornDeviceDTO hornDeviceDTO) {
        List<HornDeviceVo> hornDeviceVoList = hornDeviceMapper.queryHornForSite(hornDeviceDTO);
        //查询上传的附件信息
        if (!CollectionUtils.isEmpty(hornDeviceVoList)) {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            List<String> deviceCodeList = hornDeviceVoList.stream().map(HornDeviceVo::getDeviceCode).collect(Collectors.toList());
            // 1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达 12-执法记录仪
            Map<String, List<String>> encryptUrlMap = tenantDeviceAttachmentService.getFileUrlByDeviceCodeList(user.getTenantId(), user.getIndustryCode(), deviceCodeList, IndustryConstants.THREE_NUM);
            if (MapUtils.isNotEmpty(encryptUrlMap)) {
                hornDeviceVoList.forEach(vo -> {
                    vo.setFileUrlList(encryptUrlMap.get(vo.getDeviceCode()));
                });
            }
        }

        return hornDeviceVoList;
    }

    /**
     * 对喇叭附件做批量加密处理
     * @param deviceCode
     * @return
     */
    private List<String> dealObsFileUrlBatch(String deviceCode) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCode(
                user.getTenantId(), user.getIndustryCode(), deviceCode, IndustryConstants.THREE_NUM);
        if (!CollectionUtils.isEmpty(fileUrlList)) {
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
            // 处理Obs文件URL加密返回
            return Lists.newArrayList(stringStringMap.values());
        }
        return null;
    }

    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "12")
    public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq) {
        try {
            PageMethod.startPage(deviceComprehensiveReq.getPageNum(), deviceComprehensiveReq.getPageSize());
            //查询设备集合
            List<DeviceComprehensiveVO> hornDeviceList = hornDeviceMapper.queryDeviceListByKeyWord(deviceComprehensiveReq);
            PageInfo<DeviceComprehensiveVO> pageInfo = new PageInfo<>(hornDeviceList);
            Result<PageInfo<DeviceComprehensiveVO>> result = Result.success("查询成功");
            result.setData(pageInfo);
            return result;
        } catch (Exception e) {
            log.error("queryDeviceListByKeyWord 查询喇叭列表异常：", e);
            return Result.fail(HORN_DEVICE_QUERY_ERROR);
        }
    }

    /**
     * 根据条件查询喇叭数据
     * @param hornDeviceDTO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "12")
    public List<HornDeviceVo> queryHornDeviceListByCondition(HornDeviceDTO hornDeviceDTO) {
        return hornDeviceMapper.queryHornDeviceListByCondition(hornDeviceDTO);
    }
}
