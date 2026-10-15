package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.dataobject.OrganIotDevRelDO;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.UavDeviceQueryVO;
import com.chinatower.device.mapper.*;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.IOrgIotDevRelRepository;
import com.chinatower.device.service.IRecorderDeviceService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceRelVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.TownShipDictData;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 执法记录仪业务层
 */
@Slf4j
@Service
public class RecorderDeviceServiceImpl implements IRecorderDeviceService {

    @Resource
    private RecorderDeviceMapper recorderDeviceMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    IOrgIotDevRelRepository orgIotDevRelRepository;

    @Autowired
    IDeviceInfoRepository deviceInfoRepository;

    @Resource
    private TenantDeviceAttachmentMapper tenantDeviceAttachmentMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private UavDeviceMapper uavDeviceMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Resource
    private TenantChannelRelMapper tenantChannelRelMapper;

    @Autowired
    private SysAreaRootService sysAreaRootService;


    /**
     * 分页查询执法记录仪列表
     * @param recorderDeviceVO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "13")
    public Result<PageInfo<RecorderDeviceDTO>> getRecorderDeviceListPage(RecorderDeviceVO recorderDeviceVO) {
        if (Objects.isNull(recorderDeviceVO.getPageNum())) {
            recorderDeviceVO.setPageNum(1);
        }
        if (Objects.isNull(recorderDeviceVO.getPageSize())) {
            recorderDeviceVO.setPageSize(10);
        }
        PageMethod.startPage(recorderDeviceVO.getPageNum(), recorderDeviceVO.getPageSize());
        List<RecorderDeviceDTO> recorderDeviceDTOList = this.getList(recorderDeviceVO);
        PageInfo<RecorderDeviceDTO> pageInfo = new PageInfo<>(recorderDeviceDTOList);
        Result<PageInfo<RecorderDeviceDTO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    /**
     * 根据条件查询所有执法记录仪数据
     * @param recorderDeviceVO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "13")
    public List<RecorderDeviceDTO> getRecorderDeviceList(RecorderDeviceVO recorderDeviceVO) {
        return getList(recorderDeviceVO);
    }

    /**
     * 根据条件查询
     * @param recorderDeviceVO
     * @return
     */
    private List<RecorderDeviceDTO> getList(RecorderDeviceVO recorderDeviceVO) {
        if(StringUtils.isNotEmpty(recorderDeviceVO.getIsMonitorFlag())) {
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(SecurityUtils.getLoginUser().getUser().getUserName());
            recorderDeviceVO.setUserId(user.getUserId());
        }

        List<RecorderDeviceDTO> recorderDeviceDTOList = recorderDeviceMapper.getRecorderDeviceListPage(recorderDeviceVO);
        if (!CollectionUtils.isEmpty(recorderDeviceDTOList)) {
            String[] dictTypes = {DeviceConstants.RECORDER_DEVICE_STATUS, DeviceConstants.RECORDER_DEVICE_MFGR_CODE};
            //无人机设备编码
            List<String> deviceCodeList = recorderDeviceDTOList.stream().map(RecorderDeviceDTO::getDeviceCode).collect(Collectors.toList());
            //无人机通道信息
            List<ChannelInfoVO> channelInfoVOList = recorderDeviceMapper.getChannelAliasByDeviceCodeList(deviceCodeList, recorderDeviceVO.getTenantId(), recorderDeviceVO.getIndustryCode());

            Map<String, List<TownShipDictData>> resultMap = systemFeignClient.dictType(dictTypes).getData();
            //执法记录仪设备状态枚举值
            List<TownShipDictData> deviceStatusList = null;
            //执法记录仪设备厂家枚举值
            List<TownShipDictData> mfgrCodeList = null;
            if (MapUtils.isNotEmpty(resultMap)) {
                deviceStatusList = resultMap.get(DeviceConstants.RECORDER_DEVICE_STATUS);
                mfgrCodeList = resultMap.get(DeviceConstants.RECORDER_DEVICE_MFGR_CODE);
            }
            //组织信息
            List<OrganDeviceRelVO> orgDevRels = getRecorderOrgInfo(recorderDeviceVO);
            //执法记录仪设置组织信息
            for (RecorderDeviceDTO device : recorderDeviceDTOList) {
                //设置通道信息
                if (!CollectionUtils.isEmpty(channelInfoVOList)) {
                    device.setChannelList(channelInfoVOList.stream().filter(channel -> channel.getParentDeviceCode().equals(device.getDeviceCode())).collect(Collectors.toList()));
                }
                if (!CollectionUtils.isEmpty(orgDevRels)) {
                    device.setOrgRels(orgDevRels.stream().filter(e -> device.getDeviceCode().equals(e.getDeviceCode())).collect(Collectors.toList()));
                    device.setOrgName(device.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
                }
                //翻译执法记录设备状态名称
                if (!CollectionUtils.isEmpty(deviceStatusList)) {
                    List<TownShipDictData> recorderDeviceStatusList = deviceStatusList.stream().filter(e -> Objects.equals(String.valueOf(device.getDeviceStatus()), e.getDictValue())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(recorderDeviceStatusList)) {
                        device.setDeviceStatusName(recorderDeviceStatusList.get(0).getDictLabel());
                    }
                }
                //翻译直返记录仪厂家名称
                if (!CollectionUtils.isEmpty(mfgrCodeList)) {
                    List<TownShipDictData> recorderMfgrCodeList = mfgrCodeList.stream().filter(e -> Objects.equals(String.valueOf(device.getManufacturerCode()), e.getDictValue())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(recorderMfgrCodeList)) {
                        device.setManufacturerName(recorderMfgrCodeList.get(0).getDictLabel());
                    }
                }
            }
        }
        return recorderDeviceDTOList;
    }



    /**
     * 查询执法记录仪设备组织关系
     * @param recorderDeviceVO
     * @return
     */
    private List<OrganDeviceRelVO> getRecorderOrgInfo(RecorderDeviceVO recorderDeviceVO) {
        recorderDeviceVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_13));
        return recorderDeviceMapper.queryOrgRecorderDevRels(recorderDeviceVO);
    }

    /**
     * 组装返回执法记录仪设备区域树
     * @param treeList
     * @param backDevFlag
     * @return
     */
    private List<DeviceTreeVO> buildMonitorTree(List<DeviceTreeVO> treeList, Boolean backDevFlag) {
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        country.getList().forEach(townCode -> {
                            townCode.setOnlineNum((int) townCode.getList().stream().filter(townCodeVo -> Objects.equals(Constants.ZERO, townCodeVo.getStatus())).count());
                            townCode.setNum(townCode.getList().size());
                            if (backDevFlag != null && !backDevFlag) {
                                townCode.getList().clear();
                            }
                            if (backDevFlag) {
                                // 过滤掉没有在离线的设备
                                townCode.setList(townCode.getList().stream().filter(iotDevice -> StringUtils.isNotEmpty(iotDevice.getStatus())
                                        && (iotDevice.getStatus().equals(Constants.ZERO) || iotDevice.getStatus().equals(Constants.ONE))
                                ).collect(Collectors.toList()));
                            }
                        });
                        country.setOnlineNum(country.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                        country.setNum(country.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                        if (backDevFlag != null && !backDevFlag) {
                            //去掉异常乡镇
                            country.setList(country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getCode()) && com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getName())).collect(Collectors.toList()));
                        } else {
                            //将有异常的乡镇数据移动到区县目录下
                            List<DeviceTreeVO> list = country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isEmpty(townDto.getCode())).collect(Collectors.toList());
                            list.forEach(deviceTreeVO -> {
                                country.getList().addAll(deviceTreeVO.getList());
                            });
                            country.setList(country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getCode())).collect(Collectors.toList()));
                        }
                    });
                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    //去掉异常区县
                    city.setList(city.getList().stream().filter(areaDto ->
                            com.chinatower.platform.common.utils.StringUtils.isNotEmpty(areaDto.getCode())).collect(Collectors.toList()));
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                //去掉异常地市
                pro.setList(pro.getList().stream().filter(prioDto ->
                        com.chinatower.platform.common.utils.StringUtils.isNotEmpty(prioDto.getCode())).collect(Collectors.toList()));
            } else {
                pro.setOnlineNum(Constants.ZERO_NUM);
                pro.setNum(Constants.ZERO_NUM);
            }
        });
        return treeList;
    }

    /**
     * 工作设备区域树
     * @param recorderDeviceVO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "13")
    public List<DeviceTreeVO> getAreaTree(RecorderDeviceVO recorderDeviceVO) {
        List<DeviceTreeVO> deviceTreeVOList = recorderDeviceMapper.getAreaTree(recorderDeviceVO);

        List<DeviceTreeVO> list = buildMonitorTree(deviceTreeVOList, recorderDeviceVO.isNeedDevice());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!CollectionUtils.isEmpty(list)) {
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
     * 修改执法记录仪设备信息
     * @param recorderDeviceVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "13")
    public Result modify(RecorderDeviceVO recorderDeviceVO) {

        RecorderDeviceDTO recorderDeviceDTO = recorderDeviceMapper.getByDeviceCode(recorderDeviceVO);
        if (Objects.isNull(recorderDeviceDTO)) {
            return Result.fail("修改失败，设备不存在或没有权限修改！");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        recorderDeviceVO.setModifier(user.getUserName());
        recorderDeviceVO.setTenantId(user.getTenantId());
        recorderDeviceVO.setIndustryCode(user.getIndustryCode());
        recorderDeviceVO.setAppVerCode(user.getAppVerCode());
        recorderDeviceVO.setCreator(user.getUserName());
        recorderDeviceVO.setDeviceType(Constants.THREE);
        //查询摄像机是否存在别名
        RecorderDeviceDTO aliasDTO = recorderDeviceMapper.getAliasByDeviceCode(recorderDeviceVO);
        //不存在别名就新增，否则就update
        if (Objects.isNull(aliasDTO)) {
            recorderDeviceMapper.addAlias(recorderDeviceVO);
        }
        else {
            recorderDeviceMapper.updateAlias(recorderDeviceVO);

        }
        //通道处理
        if (!CollectionUtils.isEmpty(recorderDeviceVO.getChannelList())) {
            operateTenantDeviceRel(recorderDeviceVO);
        }
        // 所属组织处理
        List<OrganIotDevRelDO> addOrgRels = new ArrayList<>();
        List<String> delRelIdList = null;
        //要修改的组织id
        List<String> deptIds = recorderDeviceVO.getDeptIds();
        //设备存在的组织关系
        List<OrganDeviceRelVO> existOrgRels = getRecorderOrgInfo(recorderDeviceVO);
        //页面传过来的组织id比数据库多的需要做新增
        if (!CollectionUtils.isEmpty(deptIds)) {
            if (!CollectionUtils.isEmpty(existOrgRels)) {
                List<String> existDeptIds = existOrgRels.stream().map(OrganDeviceRelVO::getDeptId).collect(Collectors.toList());
                //需要新增的组织
                deptIds.stream().filter(deptId -> !existDeptIds.contains(deptId)).forEach(addDeptId -> addOrgRels.add(buildNewOrgRel(recorderDeviceVO, addDeptId)));
            }
            else {
                deptIds.stream().forEach(addDeptId -> addOrgRels.add(buildNewOrgRel(recorderDeviceVO, addDeptId)));
            }

        }
        //数据库比页面传过来的组织id多的部分需要做删除
        if (!CollectionUtils.isEmpty(existOrgRels)) {
            if (!CollectionUtils.isEmpty(deptIds)) {
                delRelIdList = existOrgRels.stream().filter(org -> !deptIds.contains(org.getDeptId())).map(OrganDeviceRelVO::getId).collect(Collectors.toList());
            }
            else {
                delRelIdList = existOrgRels.stream().map(OrganDeviceRelVO::getId).collect(Collectors.toList());
            }
        }
        //删除组织与设备的关系
        if (!CollectionUtils.isEmpty(delRelIdList)) {
            orgIotDevRelRepository.removeByIds(delRelIdList);
        }
        //新增组织与设备的关系
        if (!CollectionUtils.isEmpty(addOrgRels)) {
            orgIotDevRelRepository.saveBatch(addOrgRels);
        }
        //新增上传的附件信息
        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        hornDeviceDTO.setTenantId(recorderDeviceVO.getTenantId());
        hornDeviceDTO.setIndustryCode(recorderDeviceVO.getIndustryCode());
        hornDeviceDTO.setAppVerCode(recorderDeviceVO.getAppVerCode());
        hornDeviceDTO.setDeviceCode(recorderDeviceVO.getDeviceCode());
        //设备类型：1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达 12-执法记录仪
        hornDeviceDTO.setDeviceType(DeviceConstants.DEVICETYPE_12);
        hornDeviceDTO.setFileUrlList(recorderDeviceVO.getFileUrlList());
        hornDeviceDTO.setCreator(recorderDeviceVO.getCreator());
        deviceInfoRepository.saveDeviceAttachmentInfo(hornDeviceDTO);
        return Result.success("修改成功!");
    }

    /**
     * 更新執法記錄儀通道
     * @param recorderDeviceVO
     */
    private void operateTenantDeviceRel(RecorderDeviceVO recorderDeviceVO) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        TenantDeviceRelVO tenantDeviceRelVO = new TenantDeviceRelVO();
        tenantDeviceRelVO.setTenantId(recorderDeviceVO.getTenantId());
        tenantDeviceRelVO.setIndustryCode(recorderDeviceVO.getIndustryCode());
        tenantDeviceRelVO.setDeviceCode(recorderDeviceVO.getDeviceCode());
        tenantDeviceRelVO.setAppVerCode(sysUser.getAppVerCode());
        tenantDeviceRelVO.setCreator(SecurityUtils.getOperator());
        tenantDeviceRelVO.setDeviceType(Constants.ONE_NUM);
        List<String> channelCodeList = recorderDeviceVO.getChannelList().stream().map(ChannelInfoVO::getChannelCode).distinct().collect(Collectors.toList());
        tenantDeviceRelVO.setChannelCodeList(channelCodeList);
        //根据条件查询设备信息
        List<TenantDeviceRelVO> tenantDeviceRelList = tenantChannelRelMapper.getTenantDeviceRelList(tenantDeviceRelVO);
        List<TenantDeviceRelVO> addList = new LinkedList<>();
        List<TenantDeviceRelVO> updateList = new LinkedList<>();

        if (CollectionUtils.isEmpty(tenantDeviceRelList)) {
            for (ChannelInfoVO vo : recorderDeviceVO.getChannelList()) {
                tenantDeviceRelVO.setChannelCode(vo.getChannelCode());
                tenantDeviceRelVO.setChannelName(vo.getChannelName());
                TenantDeviceRelVO tempVO = new TenantDeviceRelVO();
                BeanUtils.copyProperties(tenantDeviceRelVO, tempVO);
                addList.add(tempVO);
            }
        } else {
            for (ChannelInfoVO vo : recorderDeviceVO.getChannelList()) {
                boolean flag = false;
                Long id = 0L;
                for (TenantDeviceRelVO relVO : tenantDeviceRelList) {
                    if (vo.getParentDeviceCode().equals(relVO.getDeviceCode()) &&
                            vo.getChannelCode().equals(relVO.getChannelCode())) {
                        flag = true;
                        id = relVO.getId();
                    }
                }
                tenantDeviceRelVO.setChannelCode(vo.getChannelCode());
                tenantDeviceRelVO.setChannelName(vo.getChannelName());
                TenantDeviceRelVO tempVO = new TenantDeviceRelVO();
                BeanUtils.copyProperties(tenantDeviceRelVO,tempVO);
                if (flag) {
                    tempVO.setId(id);
                    updateList.add(tempVO);
                } else {
                    addList.add(tempVO);
                }
            }
        }

        if (!CollectionUtils.isEmpty(addList)) {
            //新增租户设备通道关系信息
            tenantChannelRelMapper.addTenantChannelCommonRel(addList);
        }
        if (!CollectionUtils.isEmpty(updateList)) {
            //更新租户设备通道关系信息
            tenantChannelRelMapper.updateTenantChannelCommonRel(updateList);
        }

    }

    /**
     * 组装新增组织参数
     * @param deviceVO
     * @param deptId
     * @return
     */
    private OrganIotDevRelDO buildNewOrgRel(RecorderDeviceVO deviceVO, String deptId) {
        OrganIotDevRelDO newOrgRel = new OrganIotDevRelDO();
        newOrgRel.setDeviceCode(deviceVO.getDeviceCode());
        newOrgRel.setTenantId(deviceVO.getTenantId());
        newOrgRel.setDeptId(deptId);
        newOrgRel.setUpdateTime(DateUtils.getNowDate());
        newOrgRel.setUpdateBy(deviceVO.getTenantId());
        newOrgRel.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
        newOrgRel.setDeviceType(DeviceConstants.DEVICETYPE_13);
        newOrgRel.setCreateBy(deviceVO.getTenantId());
        newOrgRel.setCreateTime(new Date());
        newOrgRel.setIndustryCode(deviceVO.getIndustryCode());
        return newOrgRel;
    }

    /**
     * 查询执法记录仪详情
     * @param recorderDeviceVO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "13")
    public RecorderDeviceDTO detail(RecorderDeviceVO recorderDeviceVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            recorderDeviceVO.setUserId(user.getUserId());
        }
        //查询设备详情
        RecorderDeviceDTO recorderDeviceDTO = recorderDeviceMapper.getByDeviceCode(recorderDeviceVO);
        if (Objects.nonNull(recorderDeviceDTO)) {
            DeviceInfoVO qryOrgVO = new DeviceInfoVO();
            qryOrgVO.setDeviceCode(recorderDeviceVO.getDeviceCode());
            qryOrgVO.setTenantId(currentUser.getUser().getTenantId());
            qryOrgVO.setIndustryCode(currentUser.getUser().getIndustryCode());
            qryOrgVO.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_13));
            //查询所属组织
            UavDeviceQueryVO orgVo = uavDeviceMapper.queryOrgInfoList(qryOrgVO);
            if (Objects.nonNull(orgVo)) {
                recorderDeviceDTO.setOrgName(orgVo.getOrgName());
                recorderDeviceDTO.setOrgRelId(orgVo.getOrgRelId());
                recorderDeviceDTO.setOrgCode(orgVo.getOrgCode());
            }
            //查询上传的附件信息
            try {
                List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCode(
                        currentUser.getUser().getTenantId(), currentUser.getUser().getIndustryCode(), recorderDeviceVO.getDeviceCode(), DeviceConstants.DEVICETYPE_12);
                if (!CollectionUtils.isEmpty(fileUrlList)) {
                    Map<String, String> obsFileUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
                    // 处理Obs文件URL加密返回
                    if (MapUtils.isNotEmpty(obsFileUrlMap)) {
                        List<String> encryptUrlList = Lists.newArrayList(obsFileUrlMap.values());
                        // 判断是否有视频优先展示的标识
                        if (BooleanUtils.isTrue(recorderDeviceVO.getIsVideoFirst()) && !CollectionUtils.isEmpty(encryptUrlList)) {
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
                        recorderDeviceDTO.setFileUrlList(encryptUrlList);
                    }
                }
            } catch (Exception e) {
                log.error("查询执法记录仪附件信息失败, e:", e);
            }
        }
        return recorderDeviceDTO;
    }

    /**
     * 执法记录仪设备组织树
     * @param deviceReqDTO
     * @return
     */
    @Override
    @DataScope(tenantColumn="gg.tenant_id",areaPro="gg.province_id",areaCity="gg.city_id",deptColumn="gg.dept_id",
            industryCode="gg.industry_code", acceptColumn = "vod.accept_node", deviceColumn = "dd.device_code")
    public List<DeviceTreeVO> getRecorderMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        log.info("查询无人机组织树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        //组织与绑定的设备
        List<DeviceTreeVO> monitorList = recorderDeviceMapper.getRecorderMonitorOrgTree(deviceReqDTO);
        List<DeviceTreeVO> treeList = deviceInfoRepository.formatOrgTreeList(monitorList);
        log.info("查询无人机组织树---返回报文------{}", JSON.toJSONString(treeList));
        return treeList;
    }

    /**
     * 根据站址编码查询无人机设备列表
     * @param siteRequestDTO
     * @return
     */
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node", alarmSourceVal = "13")
    @Override
    public List<DeviceSiteVO> getRecorderDevicesBySiteCode(SiteRequestDTO siteRequestDTO) {
        int pageNum = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageNum()), 0);
        int pageSize = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageSize()), 0);
        if (0 != pageNum && 0 != pageSize) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return recorderDeviceMapper.getRecorderDevicesBySiteCode(siteRequestDTO);
    }

    /**
     * 查询执法记录仪设备距离信息
     * @param param
     * @return
     */
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "13")
    public List<DeviceDistanceVO> getRecorderDeviceDistanceInfo(DeviceDistanceDTO param) {
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

        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            param.setUserId(user.getUserId());
        }
        return recorderDeviceMapper.getRecorderDeviceDistanceInfo(param);
    }

    /**
     * 执法记录仪设备区域树查询----大屏使用
     * @param deviceReqDTO
     * @return
     */
    @Override
    @DataScope(deviceColumn="dataTable.device_code", tenantColumn="dataTable.tenant_id", alarmSourceVal = "13",
            areaPro="dataTable.province_code",areaCity="dataTable.city_code", industryCode="dataTable.industry_code", acceptColumn = "dataTable.accept_node")
    public List<DeviceTreeVO> getRecorderMonitorAreaTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
            deviceReqDTO.setTenantId(tokenUser.getTenantId());
            deviceReqDTO.setIndustryCode(tokenUser.getIndustryCode());
        }

        log.info("查询无人机树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> treeList = recorderDeviceMapper.getRecorderMonitorAreaTree(deviceReqDTO);
        //格式化区域设备树
        deviceInfoRepository.formatTreeList(treeList, null);
        // 补充中国根节点
        DeviceTreeVO root = new DeviceTreeVO();
        sysAreaRootService.createRootTreeVO(root);
        root.setList(treeList);
        root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
        root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if(!CollectionUtils.isEmpty(treeList))
        {
            rootTree.add(root);
        }
        return rootTree;
    }

    /**
     * 大屏--聚合打点，以站址为聚合点
     * @param siteRequestDTO
     * @return
     */
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node")
    @Override
    public List<IndustryDeviceTree> getRecorderSites(SiteRequestDTO siteRequestDTO) {
        return recorderDeviceMapper.getRecorderSites(siteRequestDTO);
    }

    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "recorder.device_code", alarmSourceVal = "13")
    public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq) {
        PageMethod.startPage(deviceComprehensiveReq.getPageNum(), deviceComprehensiveReq.getPageSize());
        //查询设备集合
        List<DeviceComprehensiveVO> hornDeviceList = recorderDeviceMapper.queryDeviceListByKeyWord(deviceComprehensiveReq);
        PageInfo<DeviceComprehensiveVO> pageInfo = new PageInfo<>(hornDeviceList);
        Result<PageInfo<DeviceComprehensiveVO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    /**
     * 查询执法记录仪列表基本信息
     * @param recorderDeviceVO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "13")
    public List<RecorderDeviceDTO> queryRecorderListByCondition(RecorderDeviceVO recorderDeviceVO) {
        return recorderDeviceMapper.queryRecorderListByCondition(recorderDeviceVO);
    }
}
