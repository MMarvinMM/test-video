package com.chinatower.device.repository.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.util.DistanceUtils;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.OrganIotDevRelDO;
import com.chinatower.device.dataobject.UavFlightStatusVO;
import com.chinatower.device.dto.UavCheckNameDTO;
import com.chinatower.device.dto.UavNestDeviceInfoDTO;
import com.chinatower.device.dto.UavNestLocationReq;
import com.chinatower.device.dto.UavNestLocationRsp;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.mapper.TenantDeviceAttachmentMapper;
import com.chinatower.device.mapper.UavDeviceMapper;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.IOrgIotDevRelRepository;
import com.chinatower.device.repository.UavDeviceService;
import com.chinatower.device.utils.DeviceCommonUtils;
import com.chinatower.device.utils.UavNestDataScopeHelper;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
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
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.VideoConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * 功能描述: 客户视角-无人机管理
 *
 * @auther: luo.zhenghong
 * @date: 2023/6/8
 * @version: release_v1.5.95
 * @requestID: 中国铁塔_铁塔视联平台__资源管理-无人机管理
 */
@Service
@Slf4j
public class UavDeviceServiceImpl implements UavDeviceService {

    @Resource
    private UavDeviceMapper uavDeviceMapper;

    @Autowired
    IOrgIotDevRelRepository orgIotDevRelRepository;

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;

    @Resource
    private TenantDeviceAttachmentMapper tenantDeviceAttachmentMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private ICasCadeInfoService iCasCadeInfoService;

    @Autowired
    private NetConnectUtils netConnectUtils;

    @Autowired
    private DeviceCommonUtils deviceCommonUtils;
    @Autowired
    private UavNestDataScopeHelper uavNestDataScopeHelper;
    @Autowired
    private SysAreaRootService sysAreaRootService;

    /**
     * 查询无人机设备及其通道信息
     *
     * @param param
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "uav.device_code", alarmSourceVal = "11")
    public List<UavDeviceQueryVO> queryUavDeviceInfoList(UavDeviceDTO param) {
        if (param.isExport()) {
            param.setPageNum(null);
            param.setPageSize(null);
        } else {
            PageMethod.startPage(param.getPageNum(), param.getPageSize());
        }

        if (StringUtils.isNotEmpty(param.getUavNestName())
                || StringUtils.isNotEmpty(param.getUavNestManufacturerCode())
                || StringUtils.isNotEmpty(param.getUavNestModelCode())
                || StringUtils.isNotEmpty(param.getUavNestStatus())) {
            List<String> paramUavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
            // 填充无人机巢查询条件
            if (CollectionUtils.isNotEmpty(paramUavNestCodeList)) {
                param.setUavNestCodeList(paramUavNestCodeList);
            } else {
                return Lists.newArrayList();
            }
        }

        List<UavDeviceQueryVO> deviceVOList = uavDeviceMapper.queryUavDeviceInfoList(param);
        if (CollectionUtils.isEmpty(deviceVOList)) {
            return deviceVOList;
        }
//        无人机机巢编码
        List<String> uavNestCodeList = deviceVOList.stream().map(UavDeviceQueryVO::getUavNestCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        // 机巢map key -> 机巢编码 value -> 机巢信息
        Map<String, UavNestDeviceInfoDTO> uavNestDeviceMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            List<UavNestDeviceInfoDTO> uavNestDeviceInfoList = uavDeviceMapper.queryUavNestInfoByCodeList(uavNestCodeList,
                    user.getTenantId(), user.getIndustryCode());
            if (CollectionUtils.isNotEmpty(uavNestDeviceInfoList)) {
                uavNestDeviceMap = uavNestDeviceInfoList.stream().collect(Collectors.toMap(UavNestDeviceInfoDTO::getDeviceCode, (p) -> p, (a, b) -> a));
            }
        }

        Set<String> cameraCodeList = Sets.newHashSet();
        List<DeviceCameraModelVO> cameraModelVOList = null;
        //无人机相机型号信息
        deviceVOList.forEach(item -> {
            if (StringUtils.isNotBlank(item.getCameraModelCode())) {
                cameraCodeList.addAll(Arrays.asList(item.getCameraModelCode().split(",")));
            }
        });
        //批量查询相机型号
        if (CollectionUtils.isNotEmpty(cameraCodeList)) {
            //无人机相机型号
            cameraModelVOList = uavDeviceMapper.queryUavCameraModelInfo(Lists.newArrayList(cameraCodeList));
        }

        //组织信息
        List<OrganDeviceRelVO> orgIotDevRels = qryUavOrgInfo(param);
        //设备无人机设备通道信息,组织信息
        for (UavDeviceQueryVO device : deviceVOList) {
            if (StringUtils.isNotEmpty(device.getUavNestCode()) && uavNestDeviceMap.containsKey(device.getUavNestCode())) {
                UavNestDeviceInfoDTO nestDeviceInfo = uavNestDeviceMap.get(device.getUavNestCode());
                device.setUavNestCode(nestDeviceInfo.getDeviceCode());
                device.setUavNestName(nestDeviceInfo.getDeviceName());
                device.setUavNestStatus(nestDeviceInfo.getResourceStatus());
                device.setUavNestManufacturerCode(nestDeviceInfo.getManufacturerCode());
                device.setUavNestModelCode(nestDeviceInfo.getModelCode());
                device.setAltitude(nestDeviceInfo.getAltitude());
                device.setUavNestManufacturerName(nestDeviceInfo.getUavNestManufacturerName());
                device.setUavNestModelName(nestDeviceInfo.getUavNestModelName());
                device.setUavNestStatusName(nestDeviceInfo.getUavNestStatusName());
                device.setUavNestPositionFix(nestDeviceInfo.getUavNestPositionFix());
                device.setUavNestCalibrationStatus(nestDeviceInfo.getUavNestCalibrationStatus());
            }
            if (!CollectionUtils.isEmpty(orgIotDevRels)) {
                device.setOrgRels(orgIotDevRels.stream().filter(e -> device.getDeviceCode().equals(e.getDeviceCode())).collect(Collectors.toList()));
                device.setOrgName(device.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }

            if (StringUtils.isNotEmpty(device.getCameraModelCode()) && CollectionUtils.isNotEmpty(cameraModelVOList)) {
                List<String> cameraModelNameList = Lists.newArrayList();
                for (String cameraModelCode : device.getCameraModelCode().split(",")) {
                    DeviceCameraModelVO uavCameraModelVO = cameraModelVOList.stream().filter(item -> StringUtils.equals(item.getModelCode(), cameraModelCode)).findFirst().orElse(null);
                    if (Objects.nonNull(uavCameraModelVO)) {
                        cameraModelNameList.add(uavCameraModelVO.getModelName());
                    }
                }
                device.setCameraModelName(Joiner.on(",").join(cameraModelNameList));
            }
        }
        return deviceVOList;
    }

    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "uav.device_code", alarmSourceVal = "11,15")
    public List<UavDeviceQueryVO> queryUavNestDeviceInfoList(UavDeviceDTO param) {
        PageMethod.startPage(param.getPageNum(), param.getPageSize());
        return uavDeviceMapper.queryUavNestDeviceInfoList(param);
    }

    /**
     * 查询无人机设备组织关系
     *
     * @param param
     * @return
     */
    private List<OrganDeviceRelVO> qryUavOrgInfo(UavDeviceDTO param) {
        param.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_11));
        return uavDeviceMapper.queryOrgUavDevRels(param);
    }

    /**
     * 根据设备编码查询无人机设备信息
     *
     * @param deviceCode
     * @return
     */
    public UavDeviceQueryVO qryByDeviceCode(String deviceCode) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        UavDeviceQueryVO uavDeviceVO = uavDeviceMapper.qryByDeviceCode(deviceCode, tenantId, industryCode, user.getUserId());
        if (uavDeviceVO == null) {
            return null;
        }
        DeviceInfoVO qryOrg = new DeviceInfoVO();
        qryOrg.setDeviceCode(deviceCode);
        qryOrg.setTenantId(tenantId);
        qryOrg.setIndustryCode(industryCode);
        qryOrg.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_11));
        //查询所属组织
        UavDeviceQueryVO orgVo = uavDeviceMapper.queryOrgInfoList(qryOrg);
        if (null != orgVo) {
            uavDeviceVO.setOrgName(orgVo.getOrgName());
            uavDeviceVO.setOrgRelId(orgVo.getOrgRelId());
            uavDeviceVO.setOrgCode(orgVo.getOrgCode());
        }


        // 填充无人机飞行状态信息
        // key -> deviceCode value -> flightStatus
        Map<String, String> deviceFlightStatusMap = getDeviceFlightStatusMap(Lists.newArrayList(deviceCode));
        uavDeviceVO.setFlyStatus(deviceFlightStatusMap.get(deviceCode));

        // 填充机巢信息
        List<UavNestDeviceInfoDTO> uavNestDeviceInfoList = uavDeviceMapper.queryUavNestInfoByCodeList(
                Lists.newArrayList(uavDeviceVO.getUavNestCode()), tenantId, industryCode);
        if (CollectionUtils.isNotEmpty(uavNestDeviceInfoList)) {
            UavNestDeviceInfoDTO nestDeviceInfo = uavNestDeviceInfoList.get(0);
            uavDeviceVO.setUavNestCode(nestDeviceInfo.getDeviceCode());
            uavDeviceVO.setUavNestName(nestDeviceInfo.getDeviceName());
            uavDeviceVO.setUavNestStatus(nestDeviceInfo.getResourceStatus());
            uavDeviceVO.setUavNestManufacturerCode(nestDeviceInfo.getManufacturerCode());
            uavDeviceVO.setUavNestManufacturerName(nestDeviceInfo.getUavNestManufacturerName());
            uavDeviceVO.setUavNestModelCode(nestDeviceInfo.getModelCode());
            uavDeviceVO.setUavNestModelName(nestDeviceInfo.getUavNestModelName());
            uavDeviceVO.setAltitude(nestDeviceInfo.getAltitude());
            uavDeviceVO.setUavNestPositionFix(nestDeviceInfo.getUavNestPositionFix());
            uavDeviceVO.setUavNestCalibrationStatus(nestDeviceInfo.getUavNestCalibrationStatus());

            uavDeviceVO.setUavNestLatitude(nestDeviceInfo.getUavNestLatitude());
            uavDeviceVO.setUavNestLongitude(nestDeviceInfo.getUavNestLongitude());
            uavDeviceVO.setUavNestProvinceCode(nestDeviceInfo.getUavNestProvinceCode());
            uavDeviceVO.setUavNestCityCode(nestDeviceInfo.getUavNestCityCode());
            uavDeviceVO.setUavNestCountyCode(nestDeviceInfo.getUavNestCountyCode());
            uavDeviceVO.setUavNestTownCode(nestDeviceInfo.getUavNestTownCode());
        }

        //设置无人机相机型号
        if (StringUtils.isNotBlank(uavDeviceVO.getCameraModelCode())) {
            List<DeviceCameraModelVO> cameraModelVOList = uavDeviceMapper.queryUavCameraModelInfo(Arrays.asList(uavDeviceVO.getCameraModelCode().split(",")));
            if (CollectionUtils.isNotEmpty(cameraModelVOList)) {
                List<String> cameraModelNameList = cameraModelVOList.stream().map(DeviceCameraModelVO::getModelName).collect(Collectors.toList());
                uavDeviceVO.setCameraModelName(Joiner.on(",").join(cameraModelNameList));
            }
        }

        //查询上传的附件信息
        try {
            List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCode(
                    user.getTenantId(), user.getIndustryCode(), deviceCode, IndustryConstants.TWO_NUM);
            if (!CollectionUtils.isEmpty(fileUrlList)) {
                Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
                // 处理Obs文件URL加密返回
                if (stringStringMap != null && stringStringMap.size() > 0) {
                    List<String> encryptUrlList = Lists.newArrayList(stringStringMap.values());
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
                    uavDeviceVO.setFileUrlList(encryptUrlList);
                }
            }
        } catch (Exception e) {
            log.error("UavDeviceServiceImpl qryByDeviceCode error, e:", e);
        }
        return uavDeviceVO;
    }

    private void checkUavName(SysUser user, String deviceCode, String deviceName) {
        UavCheckNameDTO param = new UavCheckNameDTO();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        param.setDeviceCode(deviceCode);
        param.setDeviceName(deviceName);
        int deviceNameCount = uavDeviceMapper.countDeviceName(param);
        int aliasNameCount = uavDeviceMapper.countAliasName(param);
        if (deviceNameCount > 0 || aliasNameCount > 0) {
            throw new CustomException("无人机名称已存在");
        }
    }

    private void checkNestName(SysUser user, String nestDeviceCode, String nestDeviceName) {
        UavCheckNameDTO param = new UavCheckNameDTO();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        param.setDeviceCode(nestDeviceCode);
        param.setDeviceName(nestDeviceName);
        int deviceNameCount = uavDeviceMapper.countNestDeviceName(param);
        int aliasNameCount = uavDeviceMapper.countNestAliasName(param);
        if (deviceNameCount > 0 || aliasNameCount > 0) {
            throw new CustomException("机场名称已存在");
        }
    }

    /**
     * 修改无人机设备信息
     *
     * @param param
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateUavDeviceInfo(UavDeviceDTO param) {
        UavDeviceDTO uavParam = new UavDeviceDTO();
        BeanUtils.copyProperties(param, uavParam);
        uavParam.setDeviceType("1");

        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkUavName(user, uavParam.getDeviceCode(), uavParam.getDeviceName());
        if (StringUtils.isNotEmpty(param.getUavNestCode())) {
            checkNestName(user, param.getUavNestCode(), param.getUavNestName());
        }

        //查询摄像机是否存在别名
        UavDeviceQueryVO aliasVO = uavDeviceMapper.qryAliasByDeviceCode(uavParam);
        if (null != aliasVO) {
            uavDeviceMapper.updateUavAlias(uavParam);
        } else {
            //新增摄像机别名
            uavDeviceMapper.addUavAlias(uavParam);
        }

        // 更新机巢客户自定义名称
        if (StringUtils.isNotEmpty(param.getUavNestCode())) {
            //查询机巢是否存在别名
            UavDeviceDTO uavNestParam = new UavDeviceDTO();
            BeanUtils.copyProperties(param, uavNestParam);
            uavNestParam.setDeviceType("4");
            uavNestParam.setDeviceCode(param.getUavNestCode());
            uavNestParam.setDeviceName(param.getUavNestName());
            UavDeviceQueryVO uavNestAliasVO = uavDeviceMapper.qryAliasByDeviceCode(uavNestParam);
            if (null != uavNestAliasVO) {
                uavDeviceMapper.updateUavAlias(uavNestParam);
            } else {
                //新增摄像机别名
                uavDeviceMapper.addUavAlias(uavNestParam);
            }
        }

        // 所属组织处理
        List<OrganIotDevRelDO> addOrgRels = new ArrayList<>();
        List<String> delRelIdList = null;
        //要修改的组织id
        List<String> deptIds = param.getDeptIds();
        //设备存在的组织关系
        List<OrganDeviceRelVO> existOrgRels = qryUavOrgInfo(param);
        //页面传过来的组织id比数据库多的需要做新增
        if (!CollectionUtils.isEmpty(deptIds)) {
            if (!CollectionUtils.isEmpty(existOrgRels)) {
                List<String> existDeptIds = existOrgRels.stream().map(OrganDeviceRelVO::getDeptId).collect(Collectors.toList());
                //需要新增的组织
                deptIds.stream().filter(deptId -> !existDeptIds.contains(deptId)).forEach(addDeptId -> addOrgRels.add(buildNewOrgRel(param, addDeptId)));
            } else {
                deptIds.forEach(addDeptId -> addOrgRels.add(buildNewOrgRel(param, addDeptId)));
            }

        }
        //数据库比页面传过来的组织id多的部分需要做删除
        if (!CollectionUtils.isEmpty(existOrgRels)) {
            if (!CollectionUtils.isEmpty(deptIds)) {
                delRelIdList = existOrgRels.stream().filter(org -> !deptIds.contains(org.getDeptId())).map(OrganDeviceRelVO::getId).collect(Collectors.toList());
            } else {
                delRelIdList = existOrgRels.stream().map(OrganDeviceRelVO::getId).collect(Collectors.toList());
            }
        }
        //删除组织月设备的关系
        if (!CollectionUtils.isEmpty(delRelIdList)) {
            orgIotDevRelRepository.removeByIds(delRelIdList);
        }
        //新增组织与设备的关系
        if (!CollectionUtils.isEmpty(addOrgRels)) {
            orgIotDevRelRepository.saveBatch(addOrgRels);
        }
        //新增上传的附件信息
        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        hornDeviceDTO.setTenantId(param.getTenantId());
        hornDeviceDTO.setIndustryCode(param.getIndustryCode());
        hornDeviceDTO.setAppVerCode(param.getAppVerCode());
        hornDeviceDTO.setDeviceCode(param.getDeviceCode());
        //设备类型：1-摄像机 2-无人机 3-喇叭 8-物联设备 10-雷达
        hornDeviceDTO.setDeviceType(IndustryConstants.TWO_NUM);
        hornDeviceDTO.setFileUrlList(param.getFileUrlList());
        hornDeviceDTO.setCreator(param.getCreator());
        deviceInfoRepository.saveDeviceAttachmentInfo(hornDeviceDTO);
    }

    /**
     * 组装新增组织参数
     *
     * @param param
     * @param deptId
     * @return
     */
    private OrganIotDevRelDO buildNewOrgRel(UavDeviceDTO param, String deptId) {
        OrganIotDevRelDO newOrgRel = new OrganIotDevRelDO();
        newOrgRel.setDeviceCode(param.getDeviceCode());
        newOrgRel.setTenantId(param.getTenantId());
        newOrgRel.setDeptId(deptId);
        newOrgRel.setUpdateTime(new Date());
        newOrgRel.setUpdateBy(param.getTenantId());
        newOrgRel.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
        newOrgRel.setDeviceType(DeviceConstants.DEVICETYPE_11);
        newOrgRel.setCreateBy(param.getTenantId());
        newOrgRel.setCreateTime(new Date());
        newOrgRel.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        return newOrgRel;
    }

    /**
     * 查询无人机设备区域数
     *
     * @param param
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "uav.device_code", alarmSourceVal = "11")
    public List<DeviceTreeVO> getUavMonitorTree(UavDeviceDTO param) {
        // 填充无人机巢查询条件
        LoginUser loginUser = SecurityUtils.getLoginUser();
        param.setTenantId(loginUser.getUser().getTenantId());
        param.setIndustryCode(loginUser.getUser().getIndustryCode());
        // 填充无人机巢查询条件

        if (StringUtils.isNotEmpty(param.getUavNestName())
                || StringUtils.isNotEmpty(param.getUavNestManufacturerCode())
                || StringUtils.isNotEmpty(param.getUavNestModelCode())
                || StringUtils.isNotEmpty(param.getUavNestStatus())
                || CollectionUtils.isNotEmpty(param.getUavNestStatusList())) {
            List<String> uavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
            // 填充无人机巢查询条件
            if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
                param.setUavNestCodeList(uavNestCodeList);
            } else {
                return Lists.newArrayList();
            }
        }

        List<DeviceTreeVO> treeList = uavDeviceMapper.getUavMonitorTree(param);
        log.info("getUavMonitorTree param:{}, result:{}", JSON.toJSONString(param), JSON.toJSONString(treeList));
        List<DeviceTreeVO> list = buildUavMonitorTree(treeList, param.isNeedDevice());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!treeList.isEmpty()) {
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

    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "11")
    public List<DeviceDistanceVO> queryUavDeviceDistanceInfo(DeviceDistanceDTO param) {
        if (param == null
                || StringUtils.isEmpty(param.getLongitude())
                || StringUtils.isEmpty(param.getLatitude())
                || StringUtils.isEmpty(param.getRadius())) {
            throw new CustomException("经纬度、探索半径参数不能为空");
        }

        List<DeviceDistanceVO> resultList = uavDeviceMapper.queryUavDeviceDistanceInfo(param);
        if (BooleanUtils.isTrue(param.getWithFlightStatus())) {
            if (CollectionUtils.isNotEmpty(resultList)) {
                List<String> deviceCodeList = resultList.stream().map(DeviceDistanceVO::getDeviceCode).distinct().collect(Collectors.toList());
                // key -> deviceCode value -> flightStatus
                Map<String, String> deviceFlightStatusMap = getDeviceFlightStatusMap(deviceCodeList);
                resultList.forEach(item -> {
                    item.setFlyStatus(deviceFlightStatusMap.get(item.getDeviceCode()));
                });
            }
        }
        return resultList;
    }

    /**
     *
     * @param param 参数
     * @return
     */
    @Deprecated
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", acceptColumn = "o.accept_node", deviceColumn = "o.device_code", alarmSourceVal = "11")
    public List<DeviceDistanceVO> queryUavDeviceDistanceInfo4App(DeviceDistanceDTO param) {
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

        return queryUavDeviceDistanceInfo(param);
    }

    /**
     * 组装返回无人机设备区域树
     *
     * @param treeList
     * @param backDevFlag
     * @return
     */
    private List<DeviceTreeVO> buildUavMonitorTree(List<DeviceTreeVO> treeList, Boolean backDevFlag) {
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
                            if (BooleanUtils.isTrue(backDevFlag)) {
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
                pro.setOnlineNum(0);
                pro.setNum(0);
            }
        });
        return treeList;
    }

    @ApiOperation("无人机区域设备树查询")
    @Override
    @DataScope(deviceColumn = "m.device_code", tenantColumn = "ord.tenant_id", alarmSourceVal = "11",
            areaPro = "m.province_code", areaCity = "m.city_code", industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    public List<DeviceTreeVO> getUavMonitorAreaTreeWithoutFormat(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
            deviceReqDTO.setTenantId(tokenUser.getTenantId());
        }

        if (StringUtils.isNotEmpty(deviceReqDTO.getUavNestName())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestManufacturerCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestModelCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestStatus())
                || CollectionUtils.isNotEmpty(deviceReqDTO.getUavNestStatusList())) {
            UavDeviceDTO param = new UavDeviceDTO();
            BeanUtils.copyProperties(deviceReqDTO, param);
            List<String> uavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
            // 填充无人机巢查询条件
            if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
                deviceReqDTO.setUavNestCodeList(uavNestCodeList);
            } else {
                return Lists.newArrayList();
            }
        }

        log.info("getUavMonitorAreaTreeWithoutFormat param: {}", JSON.toJSONString(deviceReqDTO));
        return uavDeviceMapper.getUavMonitorAreaTreeWithoutFormat(deviceReqDTO);
    }

    /**
     * 功能描述: 视频树查询
     *
     * @param: deviceReqDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: luozhenghong
     * @date: 2023/8/2
     */
    @ApiOperation("无人机区域设备树查询")
    @Override
    @DataScope(deviceColumn = "m.device_code", tenantColumn = "ord.tenant_id", alarmSourceVal = "11",
            areaPro = "m.province_code", areaCity = "m.city_code", industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    public List<DeviceTreeVO> getUavMonitorAreaTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
            deviceReqDTO.setTenantId(tokenUser.getTenantId());
        }

        if (StringUtils.isNotEmpty(deviceReqDTO.getUavNestName())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestManufacturerCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestModelCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestStatus())
                || CollectionUtils.isNotEmpty(deviceReqDTO.getUavNestStatusList())) {
            UavDeviceDTO param = new UavDeviceDTO();
            BeanUtils.copyProperties(deviceReqDTO, param);
            List<String> uavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
            // 填充无人机巢查询条件
            if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
                deviceReqDTO.setUavNestCodeList(uavNestCodeList);
            } else {
                return Lists.newArrayList();
            }
        }

        log.info("查询无人机树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> treeList = uavDeviceMapper.getUavMonitorAreaTree(deviceReqDTO);

        // key -> deviceCode value -> flightStatus
        Map<String, String> deviceFlightStatusMap = Maps.newHashMap();
        if (BooleanUtils.isTrue(deviceReqDTO.getWithFlightStatus())) {
            if (CollectionUtils.isNotEmpty(treeList)) {
                List<String> deviceCodeList = Lists.newArrayList();
                treeList.forEach(pro -> {
                    if (CollectionUtils.isNotEmpty(pro.getList())) {
                        pro.getList().forEach(city -> {
                            if (CollectionUtils.isNotEmpty(city.getList())) {
                                city.getList().forEach(country -> {
                                    if (CollectionUtils.isNotEmpty(country.getList())) {
                                        country.getList().forEach(dev -> {
                                            deviceCodeList.add(dev.getDeviceCode());
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
                deviceFlightStatusMap = getDeviceFlightStatusMap(deviceCodeList);
            }
        }

        //格式化区域设备树
        formatTreeList(treeList, null, deviceFlightStatusMap);
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

    @Override
    public List<DeviceTreeVO> getUavMonitorAreaTreeWithoutLogin(DeviceReqDTO deviceReqDTO) {
        if (StringUtils.isNotEmpty(deviceReqDTO.getUavNestName())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestManufacturerCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestModelCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestStatus())
                || CollectionUtils.isNotEmpty(deviceReqDTO.getUavNestStatusList())) {
            UavDeviceDTO param = new UavDeviceDTO();
            BeanUtils.copyProperties(deviceReqDTO, param);
            List<String> uavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
            // 填充无人机巢查询条件
            if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
                deviceReqDTO.setUavNestCodeList(uavNestCodeList);
            } else {
                return Lists.newArrayList();
            }
        }
        Result<String> platformCodeResult = iCasCadeInfoService.getCurrentPlatformCode();
        deviceReqDTO.setPlatformCode(platformCodeResult.getData());
        log.info("getUavMonitorAreaTreeWithoutLogin deviceReqDTO:{}", JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> treeList = uavDeviceMapper.getUavMonitorAreaTreeWithoutLogin(deviceReqDTO);
        //格式化区域设备树
        formatTreeList(treeList, null, Maps.newHashMap());
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
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
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

    @Override
    @DataScope(tenantColumn = "gg.tenant_id", areaPro = "gg.province_id", areaCity = "gg.city_id", deptColumn = "gg.dept_id",
            industryCode = "gg.industry_code", acceptColumn = "vod.accept_node", deviceColumn = "dd.device_code")
    public List<DeviceTreeVO> getUavMonitorOrgTreeWithoutFormat(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }

        if (StringUtils.isNotEmpty(deviceReqDTO.getUavNestName())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestManufacturerCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestModelCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestStatus())
                || CollectionUtils.isNotEmpty(deviceReqDTO.getUavNestStatusList())) {
            UavDeviceDTO param = new UavDeviceDTO();
            BeanUtils.copyProperties(deviceReqDTO, param);
            List<String> uavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
            // 填充无人机巢查询条件
            if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
                deviceReqDTO.setUavNestCodeList(uavNestCodeList);
            } else {
                return Lists.newArrayList();
            }
        }

        log.info("getUavMonitorOrgTreeWithoutFormat, param:{}", JSON.toJSONString(deviceReqDTO));
        //组织与绑定的设备
        return uavDeviceMapper.getUavMonitorOrgTreeWithoutFormat(deviceReqDTO);
    }

    /**
     * 无人机设备组织树
     *
     * @param deviceReqDTO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "gg.tenant_id", areaPro = "gg.province_id", areaCity = "gg.city_id", deptColumn = "gg.dept_id",
            industryCode = "gg.industry_code", acceptColumn = "vod.accept_node", deviceColumn = "dd.device_code")
    public List<DeviceTreeVO> getUavMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }

        if (StringUtils.isNotEmpty(deviceReqDTO.getUavNestName())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestManufacturerCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestModelCode())
                || StringUtils.isNotEmpty(deviceReqDTO.getUavNestStatus())
                || CollectionUtils.isNotEmpty(deviceReqDTO.getUavNestStatusList())) {
            UavDeviceDTO param = new UavDeviceDTO();
            BeanUtils.copyProperties(deviceReqDTO, param);
            List<String> uavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
            // 填充无人机巢查询条件
            if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
                deviceReqDTO.setUavNestCodeList(uavNestCodeList);
            } else {
                return Lists.newArrayList();
            }
        }

        log.info("查询无人机组织树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        //组织与绑定的设备
        List<DeviceTreeVO> monitorList = uavDeviceMapper.getUavMonitorOrgTree(deviceReqDTO);

        // key -> deviceCode value -> flightStatus
        Map<String, String> deviceFlightStatusMap = Maps.newHashMap();
        if (BooleanUtils.isTrue(deviceReqDTO.getWithFlightStatus())) {
            if (CollectionUtils.isNotEmpty(monitorList)) {
                List<String> deviceCodeList = Lists.newArrayList();
                monitorList.forEach(org -> {
                    if (CollectionUtils.isNotEmpty(org.getList())) {
                        org.getList().forEach(dev -> {
                            deviceCodeList.add(dev.getDeviceCode());
                        });
                    }
                });
                deviceFlightStatusMap = getDeviceFlightStatusMap(deviceCodeList);
            }
        }

        List<DeviceTreeVO> treeList = deviceInfoRepository.formatOrgTreeList4Device(monitorList, deviceFlightStatusMap);
        log.info("查询无人机组织树---返回报文------{}", JSON.toJSONString(treeList));
        return treeList;

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

    /**
     * 查询无人机分布情况
     *
     * @param siteRequestDTO
     * @return
     */
    @DataScope(tenantColumn = "t1.tenant_id", deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node", alarmSourceVal = "11")
    @Override
    public List<SiteResultVO> queryUavSitesForWE(SiteRequestDTO siteRequestDTO) {
        return uavDeviceMapper.queryUavSitesForWE(siteRequestDTO);
    }

    @DataScope(tenantColumn = "t1.tenant_id", deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node")
    @Override
    public List<IndustryDeviceTree> queryUavSites(SiteRequestDTO siteRequestDTO) {
        return uavDeviceMapper.queryUavSites(siteRequestDTO);
    }

    /**
     * 根据站址编码查询无人机设备列表
     *
     * @param siteRequestDTO
     * @return
     */
    @DataScope(tenantColumn = "t1.tenant_id", deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node", alarmSourceVal = "11")
    @Override
    public List<DeviceSiteVO> queryUavDevicesBySiteCode(SiteRequestDTO siteRequestDTO) {

        if (!Objects.equals(siteRequestDTO.getQueryFlag(), Constants.ONE_NUM)) {
            int pageNum = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageNum()), 0);
            int pageSize = NumberUtils.toInt(String.valueOf(siteRequestDTO.getPageSize()), 0);
            if (0 != pageNum && 0 != pageSize) {
                PageHelper.startPage(pageNum, pageSize);
            }
        }
        return uavDeviceMapper.queryUavDevicesBySiteCode(siteRequestDTO);
    }

    /**
     * 根据关键字查询无人机设备列表
     *
     * @param deviceComprehensiveReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "uav.device_code", alarmSourceVal = "11")
    public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq) {
        PageHelper.startPage(deviceComprehensiveReq.getPageNum(), deviceComprehensiveReq.getPageSize());
        //根据关键字查询设备列表
        List<DeviceComprehensiveVO> deviceComprehensiveVOList = uavDeviceMapper.queryDeviceListByKeyWord(deviceComprehensiveReq);
        PageInfo<DeviceComprehensiveVO> pageInfo = new PageInfo<>(deviceComprehensiveVOList);
        Result<PageInfo<DeviceComprehensiveVO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    @Override
    public List<DeviceCameraModelVO> queryUavCameraModelInfo(UavDeviceDTO param) {
        if (param == null || StringUtils.isEmpty(param.getDeviceCode())) {
            throw new CustomException("设备编码不能为空");
        }
        String cameraModelCode = uavDeviceMapper.queryCameraModelCodeByDeviceCode(param.getDeviceCode());
        if (StringUtils.isEmpty(cameraModelCode)) {
            return Lists.newArrayList();
        }
        List<String> cameraModelCodeList = Arrays.asList(cameraModelCode.split(","));
        List<DeviceCameraModelVO> resultList = uavDeviceMapper.queryUavCameraModelInfo(cameraModelCodeList);
        if (CollectionUtils.isEmpty(resultList)) {
            return Lists.newArrayList();
        }
        return resultList;
    }


    @Override
    public UavDetailVO queryUavDetail(UavDeviceDTO param) {
        if (param == null || StringUtils.isEmpty(param.getDeviceCode())) {
            throw new CustomException("设备编码不能为空");
        }
        //无人机机场基本信息
        UavDeviceDTO uavDeviceDTO = uavDeviceMapper.queryUavDetailInfo(param.getDeviceCode());
        if (Objects.isNull(uavDeviceDTO)) {
            return null;
        }
        UavDetailVO uavDetailVO = new UavDetailVO();
        if (StringUtils.isNotBlank(uavDeviceDTO.getCameraModelCode())) {
            List<String> cameraModeCodeList = Arrays.asList(uavDeviceDTO.getCameraModelCode().split(","));
            //无人机相机型号
            List<DeviceCameraModelVO> cameraModelVOList = uavDeviceMapper.queryUavCameraModelInfo(cameraModeCodeList);
            uavDetailVO.setCameraModelVOList(cameraModelVOList);
        }
        //无人机型号字典
        List<UavModelVO> uavModelVOList = uavDeviceMapper.queryUavModelList();
        //无人机机场型号字典
        List<UavNestModelVO> uavNestModelVOList = uavDeviceMapper.queryUavNestModelList();
        uavDetailVO.setBaseInfo(uavDeviceDTO);
        uavDetailVO.setUavModelVOList(uavModelVOList);
        uavDetailVO.setUavNestModelVOList(uavNestModelVOList);
        return uavDetailVO;
    }

    @Override
    @DataScope(deviceColumn = "m.device_code", tenantColumn = "o1.tenant_id", alarmSourceVal = "15",
            areaPro = "m.province_code", areaCity = "m.city_code", industryCode = "o1.industry_code", acceptColumn = "o1.accept_node")
    public UavDeviceDTO queryUavByNestInfo(UavDeviceDTO param) {
        if (param == null || StringUtils.isEmpty(param.getUavNestCode())) {
            throw new CustomException("机巢编码不能为空");
        }
        return uavDeviceMapper.queryUavByNestInfo(param);
    }

    @Override
    public UavNestLocationVO getUavNestLocationInfo(UavDeviceDTO param) {
        if (StringUtils.isEmpty(param.getUavNestCode())) {
            throw new CustomException("机巢编码不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        List<UavDeviceDTO> uavDeviceDTOS = uavDeviceMapper.queryUavDeviceCodesByUavNestDeviceCode(param.getUavNestCode(), tenantId, industryCode);
        if (CollectionUtils.isEmpty(uavDeviceDTOS)) {
            throw new CustomException("获取无人机信息失败", HttpStatus.ERROR_4001);
        }
        List<String> uavDeviceCodeList = uavDeviceDTOS.stream().map(UavDeviceDTO::getDeviceCode).collect(Collectors.toList());
        log.info("更新无人机编码为：{}", uavDeviceCodeList);
        return commonDealUavNestLocationInfo(param, uavDeviceCodeList);
    }

    /**
     * 获取无人机卡片列表
     *
     * @param deviceReqDTO
     * @return
     */
    @Override
    @DataScope(deviceColumn = "m.device_code", tenantColumn = "ord.tenant_id", alarmSourceVal = "11",
            areaPro = "m.province_code", areaCity = "m.city_code", industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    public List<UavCardQueryVO> queryUavInfoList(DeviceReqDTO deviceReqDTO) {
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (null != currentUser) {
                SysUser tokenUser = currentUser.getUser();
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
                deviceReqDTO.setUserId(user.getUserId());
                deviceReqDTO.setTenantId(tokenUser.getTenantId());
                deviceReqDTO.setIndustryCode(tokenUser.getIndustryCode());
            }

            if (StringUtils.isNotEmpty(deviceReqDTO.getUavNestName())
                    || StringUtils.isNotEmpty(deviceReqDTO.getUavNestManufacturerCode())
                    || StringUtils.isNotEmpty(deviceReqDTO.getUavNestModelCode())
                    || StringUtils.isNotEmpty(deviceReqDTO.getUavNestStatus())
                    || CollectionUtils.isNotEmpty(deviceReqDTO.getUavNestStatusList())) {
                UavDeviceDTO param = new UavDeviceDTO();
                BeanUtils.copyProperties(deviceReqDTO, param);
                List<String> uavNestCodeList = uavDeviceMapper.queryUavNestCodeByCondition(param);
                // 填充无人机巢查询条件
                if (CollectionUtils.isNotEmpty(uavNestCodeList)) {
                    deviceReqDTO.setUavNestCodeList(uavNestCodeList);
                } else {
                    return Lists.newArrayList();
                }
            }

            // 查询租户的空机巢

            // 生成"单机巢口径"数据权限片段：queryUavInfoList 的 SQL 为 UNION ALL，下半部分（单机巢，别名 s/z）
            // 此前未拼数据权限会越权返回。这里用副本 DTO 单独触发一次机巢口径的数据权限切面（详见
            // UavNestDataScopeHelper），拷贝到 nestDataScope，供 XML ${param.nestDataScope} 使用。
            // 必须在 PageHelper.startPage 之前调用——切面内部会执行查询，避免误消费分页。
            DeviceReqDTO nestScopeParam = new DeviceReqDTO();
            BeanUtils.copyProperties(deviceReqDTO, nestScopeParam);
            nestScopeParam.setDataScope("");
            uavNestDataScopeHelper.fillNestDataScope(nestScopeParam);
            deviceReqDTO.setNestDataScope(StringUtils.isEmpty(nestScopeParam.getDataScope()) ? "" : nestScopeParam.getDataScope());

            log.info("开始查询无人机卡片信息列表-req: {}", JSON.toJSONString(deviceReqDTO));
            PageHelper.startPage(deviceReqDTO.getPageNum(), deviceReqDTO.getPageSize());
            List<UavCardQueryVO> uavCardList = uavDeviceMapper.queryUavInfoList(deviceReqDTO);
            log.info("查询无人机卡片信息列表-resp: {}", JSON.toJSONString(uavCardList));
            if (CollectionUtils.isEmpty(uavCardList)) {
                log.info("无人机基础信息列表为空！");
                return Lists.newArrayList();
            }
            //提取无人机设备编码
            List<String> deviceCodes = uavCardList.stream()
                    .map(UavCardQueryVO::getCode)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            //飞行状态业务处理
            List<UavFlightStatusVO> flightStatusList = deviceCommonUtils.getUavFlightStatus(deviceCodes);
            Map<String, UavFlightStatusVO> flightStatusMap = flightStatusList.stream()
                    .filter(statusVO -> StringUtils.isNotEmpty(statusVO.getDeviceCode()))
                    .collect(Collectors.toMap(
                            UavFlightStatusVO::getDeviceCode,
                            statusVO -> statusVO,
                            (oldVal, newVal) -> oldVal
                    ));

            String tableName = TableUtil.getTableName(new Date(), com.chinatower.event.constants.Constants.WARN_ORDER_TEBLE);
            //组装最终结果（含基础信息、飞行状态、告警判断）
            uavCardList.forEach(dev -> {
                if (StringUtils.isNotEmpty(dev.getDeviceAlias())) {
                    dev.setName(dev.getDeviceAlias());
                }
                dev.setOnlineNum(Objects.equals(Constants.ZERO, dev.getStatus()) ? Constants.ONE_NUM : Constants.ZERO_NUM);
                dev.setIsMonitorNum(Objects.equals(Constants.ONE, dev.getIsMonitor()) ? Constants.ONE_NUM : Constants.ZERO_NUM);
                dev.setIsTagCollectNum(Objects.equals(Constants.ONE, dev.getTagCollect()) ? Constants.ONE_NUM : Constants.ZERO_NUM);

                //设置飞行状态
                UavFlightStatusVO flightStatusVO = flightStatusMap.get(dev.getCode());
                if (flightStatusVO != null) {
                    dev.setFlyStatus(flightStatusVO.getFlightStatus());
                    dev.setPlanTime(flightStatusVO.getPlanTimeHHmm());

                    //飞行状态为：飞行中，查询该飞行任务下是否有告警
                    if (Constants.ONE.equals(flightStatusVO.getFlightStatus())) {
                        String planTaskId = flightStatusVO.getPlanTaskId();
                        if (StringUtils.isEmpty(planTaskId)) {
                            log.warn("设备[{}]处于飞行中，但计划任务ID为空，无法查询告警", dev.getCode());
                            dev.setIsExsistAlarm(Constants.ZERO);
                            return;
                        }
                        int alarmCount = uavDeviceMapper.getAlarmOrderCount(tableName, flightStatusVO.getPlanTaskId(), deviceReqDTO.getTenantId(), deviceReqDTO.getIndustryCode());
                        dev.setIsExsistAlarm(alarmCount > Constants.ZERO_NUM ? Constants.ONE : Constants.ZERO);
                    }
                }
            });
            log.info("无人机信息列表查询处理完成，返回 {} 条记录", uavCardList.size());
            return uavCardList;
        } catch (Exception e) {
            log.error("查询无人机信息列表时发生异常", e);
            return Lists.newArrayList();
        }
    }

    private UavNestLocationVO commonDealUavNestLocationInfo(UavDeviceDTO param, List<String> uavDeviceCodeList) {
        UavNestLocationVO info;
        if (deviceCommonUtils.isDependenceFlag()) {
            info = getUavSystemPositionInfo(param.getUavNestCode());
        } else {
            info = getPositionInfo(param.getUavNestCode());
        }

        if (Objects.isNull(info)) {
            return null;
        }
        PoiContraryGeocodingQueryRes regionInfo = deviceCommonUtils.getRegionInfoByLonAndLat(info.getLongitude(), info.getLatitude());
        if (Objects.isNull(regionInfo)) {
            return null;
        }
        info.setAddress(regionInfo.getAddress());
        info.setProvinceCode(regionInfo.getRegion().getProvinceCode());
        info.setCityCode(regionInfo.getRegion().getCityCode());
        info.setCountyCode(regionInfo.getRegion().getCountyCode());
        info.setTownCode(regionInfo.getRegion().getTownCode());
        uavDeviceMapper.updateUavDeviceLocationInfo(info, uavDeviceCodeList);
        uavDeviceMapper.updateUavNestDeviceLocationInfo(info);
        return info;
    }

    /**
     * 通过运管获取机巢最新的位置信息
     *
     * @param uavNestCode 无人机机巢编码
     * @return UavNestLocationVO 位置信息
     */
    private UavNestLocationVO getPositionInfo(String uavNestCode) {
        NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_POSITION_INFO);
        body.setPath(path);
        body.setBody(Lists.newArrayList(uavNestCode));
        try {
            Result<String> platformCodeResult = iCasCadeInfoService.getCurrentPlatformCode();
            String platformCode = platformCodeResult.getData();
            ResultEntity response = netConnectUtils.post(body, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_MsgId.of("msgId"),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(DeviceSyncConstants.CENTER_LOCATION_CODE),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_YG),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(netConnectUtils.getRealUrl(body))
            );
            log.info("获取机巢信息编码为{}，返回报文:{}", uavNestCode, response);
            if (!response.getReturnCode().equals(Constants.SUCCESS_CODE)) {
                throw new CustomException("获取" + uavNestCode + "机巢信息失败:" + response.getErrorMsg(), HttpStatus.ERROR_4001);
            }
            List dataList = (List) response.getData();
            if (CollectionUtils.isEmpty(dataList)) {
                return null;
            }
            return JSONObject.parseObject(JSONObject.toJSONString(dataList.get(0)), UavNestLocationVO.class);
        } catch (Exception e) {
            log.error("从运管获取无人机机巢信息失败：", e);
            throw new CustomException("获取" + uavNestCode + "机巢信息失败", HttpStatus.ERROR_4001);
        }
    }

    /**
     * 通过无人机平台获取机巢最新的位置信息
     *
     * @param uavNestCode 无人机机巢编码
     * @return UavNestLocationVO 位置信息
     */
    public UavNestLocationVO getUavSystemPositionInfo(String uavNestCode) {
        UavNestLocationReq query = new UavNestLocationReq();
        query.setDeviceCodeList(Lists.newArrayList(uavNestCode));
        try {
            NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.NEST_LOCATION);
            body.setPath(path);
            body.setBody(query);
            ResultEntity response = netConnectUtils.post(body, ResultEntity.class);
            log.info("调用南向接口入参返回:{}", response);
            if (!response.getReturnCode().equals(Constants.SUCCESS_CODE)) {
                throw new CustomException("调用南向接口失败:" + response.getErrorMsg(), HttpStatus.ERROR_4001);
            }
            List dataList = (List) response.getData();
            if (CollectionUtils.isEmpty(dataList)) {
                return null;
            }
            UavNestLocationRsp uavNestLocationRsp = JSONObject.parseObject(JSONObject.toJSONString(dataList.get(0)), UavNestLocationRsp.class);
            UavNestLocationVO one = new UavNestLocationVO();
            one.setLongitude(uavNestLocationRsp.getLongitude());
            one.setDeviceCode(uavNestLocationRsp.getDeviceCode());
            one.setLatitude(uavNestLocationRsp.getLatitude());
            one.setAltitude(uavNestLocationRsp.getHeight());
            return one;
        } catch (Exception e) {
            log.info("从无人机平如获取无人机机巢信息失败：", e);
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }
    }

    /**
     * 查询指点飞行可用无人机列表
     * 推荐条件：
     * 1. 无飞行计划：该无人机3小时内执行计划下无飞行任务
     * 2. 空闲：该无人机当前飞行状态为"空闲"
     * 3. 电量充足：根据目的地距离与当前电量确保该无人机可完成本次指点飞行
     * 4. 都满足的无人机按照距离由近到远排序
     *
     * @param queryReqDTO 查询请求参数
     * @return 符合条件的无人机卡片列表
     */
    @Override
    @DataScope(deviceColumn = "m.device_code", tenantColumn = "ord.tenant_id", alarmSourceVal = "11",
            areaPro = "m.province_code", areaCity = "m.city_code", industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    public List<UavPointToFlyCardVO> queryUavPointToFlyList(UavPointToFlyQueryReqDTO queryReqDTO) {
        log.info("查询指点飞行可用无人机列表-req: {}", JSON.toJSONString(queryReqDTO));
        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (currentUser == null) {
                throw new CustomException("用户未登录");
            }
            SysUser tokenUser = currentUser.getUser();
            String tenantId = tokenUser.getTenantId();
            String industryCode = tokenUser.getIndustryCode();

            // 判断是否只传了设备编码（不计算距离）
            boolean onlyDeviceCode = StrUtil.isNotBlank(queryReqDTO.getDeviceCode());
            List<String> deviceCodeList;

            if (onlyDeviceCode) {
                // 只传了设备编码，直接使用该设备编码
                deviceCodeList = Lists.newArrayList(queryReqDTO.getDeviceCode());
            } else {
                // 参数校验
                if (StringUtils.isEmpty(queryReqDTO.getLongitude()) || StringUtils.isEmpty(queryReqDTO.getLatitude())) {
                    throw new CustomException("目的地经纬度不能为空");
                }
                // 根据经纬度和半径查询设备列表
                DeviceDistanceDTO queryUavDeviceDistanceInfo = new DeviceDistanceDTO();
                queryUavDeviceDistanceInfo.setLatitude(queryReqDTO.getLatitude());
                queryUavDeviceDistanceInfo.setLongitude(queryReqDTO.getLongitude());
                queryUavDeviceDistanceInfo.setRadius(queryReqDTO.getRadius());
                List<DeviceDistanceVO> deviceDistanceVOS = queryUavDeviceDistanceInfo(queryUavDeviceDistanceInfo);
                deviceCodeList = deviceDistanceVOS.stream().map(DeviceDistanceVO::getDeviceCode).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(deviceCodeList)) {
                    log.info("未查询到符合条件的无人机设备");
                    return Lists.newArrayList();
                }
            }

            // 查询设备基础信息
            DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
            deviceReqDTO.setDeviceCodes(deviceCodeList);
            deviceReqDTO.setTenantId(tenantId);
            deviceReqDTO.setIndustryCode(industryCode);
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
            deviceReqDTO.setDataScope(queryReqDTO.getDataScope());
            // 机巢状态在线
            deviceReqDTO.setUavNestStatusList(Lists.newArrayList("0"));
            log.info("queryUavInfoList--deviceReqDTO:{}",JSONUtil.toJsonStr(deviceReqDTO));
            List<UavCardQueryVO> uavCardList = uavDeviceMapper.queryUavInfoList(deviceReqDTO);
            if (CollectionUtils.isEmpty(uavCardList)) {
                log.info("未查询到符合条件的无人机设备");
                return Lists.newArrayList();
            }
            log.info("uavCardList:{}", JSONUtil.toJsonStr(uavCardList));

            List<String> deviceCodes = uavCardList.stream()
                    .map(UavCardQueryVO::getCode)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());

            // 查询飞行状态
            List<UavFlightStatusVO> flightStatusList = deviceCommonUtils.getUavFlightStatus(deviceCodes);
            Map<String, UavFlightStatusVO> flightStatusMap = flightStatusList.stream()
                    .filter(statusVO -> StringUtils.isNotEmpty(statusVO.getDeviceCode()))
                    .collect(Collectors.toMap(
                            UavFlightStatusVO::getDeviceCode,
                            statusVO -> statusVO,
                            (oldVal, newVal) -> oldVal
                    ));

            // 查询3小时内的飞行计划
            Date currentTime = new Date();
            Date threeHoursLater = DateUtil.offsetHour(currentTime, 3);
            Map<String, Map<String, Object>> futurePlanInfo = uavDeviceMapper.getFuturePlanTasks(deviceCodes, currentTime, threeHoursLater)
                    .stream()
                    .filter(map -> map != null && StringUtils.isNotEmpty(String.valueOf(map.get("deviceCode"))))
                    .collect(Collectors.toMap(
                            map -> String.valueOf(map.get("deviceCode")),
                            map -> map,
                            (oldVal, newVal) -> oldVal
                    ));

            Map<String, Boolean> recommendCandidateMap = new HashMap<>();
            Map<String, Double> distanceCache = new HashMap<>();

            // 批量查询挂载型号信息
            Set<String> mountModelCodeSet = uavCardList.stream()
                    .map(UavCardQueryVO::getUavMountModel)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
            Map<String, UavMountModelInfoVO> mountModelMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(mountModelCodeSet)) {
                List<UavMountModelInfoVO> mountModelList = uavDeviceMapper.queryUavMountModelInfo(Lists.newArrayList(mountModelCodeSet));
                if (CollectionUtils.isNotEmpty(mountModelList)) {
                    mountModelMap = mountModelList.stream()
                            .filter(model -> StringUtils.isNotEmpty(model.getModelCode()))
                            .collect(Collectors.toMap(
                                    UavMountModelInfoVO::getModelCode,
                                    model -> model,
                                    (oldVal, newVal) -> oldVal
                            ));
                }
            }

            // 构建返回数据
            List<UavPointToFlyCardVO> resultList = new ArrayList<>();
            for (UavCardQueryVO uavCard : uavCardList) {
                String deviceCode = uavCard.getCode();
                if (StringUtils.isEmpty(deviceCode)) {
                    continue;
                }

                // 计算距离（如果只传了设备编码，则不计算距离）
                if (!onlyDeviceCode) {
                    if (StringUtils.isEmpty(uavCard.getLongitude()) || StringUtils.isEmpty(uavCard.getLatitude())) {
                        log.debug("设备[{}]经纬度为空，无法计算距离", deviceCode);
                        continue;
                    }

                    double targetDistance;
                    try {
                        targetDistance = DistanceUtils.calculateDistance(
                                uavCard.getLongitude(), uavCard.getLatitude(),
                                queryReqDTO.getLongitude(), queryReqDTO.getLatitude()
                        );
                    } catch (Exception e) {
                        log.warn("计算设备[{}]到目的地的距离失败: {}", deviceCode, e.getMessage());
                        continue;
                    }
                    double distanceInKm = targetDistance / 1000.0;
                    distanceCache.put(deviceCode, distanceInKm);
                }

                // 查询飞行状态
                UavFlightStatusVO flightStatusVO = flightStatusMap.get(deviceCode);
                String flightStatus = flightStatusVO != null ? flightStatusVO.getFlightStatus() : Constants.TWO;

                // 构建返回对象
                UavPointToFlyCardVO cardVO = new UavPointToFlyCardVO();
                cardVO.setDeviceCode(deviceCode);
                cardVO.setUavName(StringUtils.isNotEmpty(uavCard.getDeviceAlias()) ? uavCard.getDeviceAlias() : uavCard.getName());
                cardVO.setFlightStatus(flightStatus);
                
                // 设置距离（如果只传了设备编码，则不设置距离）
                if (!onlyDeviceCode) {
                    double distanceInKm = distanceCache.get(deviceCode);
                    cardVO.setDistance(String.format("%.1f", distanceInKm));
                }
                
                // 设置经纬度信息
                cardVO.setLongitude(uavCard.getLongitude());
                cardVO.setLatitude(uavCard.getLatitude());
                
                // 设置机巢信息
                cardVO.setUavNestCode(uavCard.getUavNestCode());
                cardVO.setUavNestName(uavCard.getUavNestName());
                cardVO.setUavNestLongitude(uavCard.getUavNestLongitude());
                cardVO.setUavNestLatitude(uavCard.getUavNestLatitude());
                cardVO.setUavNestAltitude(uavCard.getAltitude());
                // 设置节点信息
                cardVO.setAccessNode(uavCard.getAccessNode());
                // 预计飞行时间，前端算
//                cardVO.setEstimatedFlightTime((int) Math.ceil(distanceInKm / 60.0 * 60));
                UavDeviceDTO deviceParam = new UavDeviceDTO();
                deviceParam.setDeviceCode(deviceCode);
                cardVO.setCameraList(queryUavCameraModelInfo(deviceParam));
                // 续航，前端算
//                cardVO.setEndurance("预计0公里 0分钟");
                // 查询挂载设备列表
                String mountModelCode = uavCard.getUavMountModel();
                if (StringUtils.isNotEmpty(mountModelCode)) {
                    UavMountModelInfoVO mountModel = mountModelMap.get(mountModelCode);
                    cardVO.setUavMountModel(mountModel);
                }
                // 电量（百分比），前端ws拿到
//                cardVO.setBatteryPercent(null);
                cardVO.setRecommended(Boolean.FALSE);

                // 计划起飞剩余时间
                Map<String, Object> planInfo = futurePlanInfo.get(deviceCode);
                if (CollUtil.isNotEmpty(planInfo)) {
                    Object planTimeObj = planInfo.get("planTime");
                    Object planNameObj = planInfo.get("planName");
                    if (planTimeObj != null) {
                        cardVO.setPlanTime(planTimeObj.toString());
                    }
                    if (planNameObj != null) {
                        cardVO.setPlanName(planNameObj.toString());
                    }
                }

                // 推荐条件
                boolean noPlanWithinThreeHours = CollUtil.isEmpty(planInfo);
                boolean isIdle = Constants.TWO.equals(flightStatus);
                // 推荐标识
                boolean recommendCandidate = noPlanWithinThreeHours && isIdle;
                recommendCandidateMap.put(deviceCode, recommendCandidate);

                resultList.add(cardVO);
            }

            // 推荐标识处理: 按照距离从近到远（如果只传了设备编码，则不按距离排序）
            if (!onlyDeviceCode) {
                resultList.stream()
                        .filter(card -> Boolean.TRUE.equals(recommendCandidateMap.get(card.getDeviceCode())))
                        .sorted(Comparator.comparing(card -> distanceCache.getOrDefault(card.getDeviceCode(), Double.MAX_VALUE)))
                        .forEach(card -> card.setRecommended(Boolean.TRUE));

                // 结果排序：推荐优先，其次按距离升序
                resultList.sort((card1, card2) -> {
                    boolean card1Recommended = Boolean.TRUE.equals(card1.getRecommended());
                    boolean card2Recommended = Boolean.TRUE.equals(card2.getRecommended());
                    if (card1Recommended != card2Recommended) {
                        return card1Recommended ? -1 : 1;
                    }
                    Double distance1 = distanceCache.getOrDefault(card1.getDeviceCode(), Double.MAX_VALUE);
                    Double distance2 = distanceCache.getOrDefault(card2.getDeviceCode(), Double.MAX_VALUE);
                    return distance1.compareTo(distance2);
                });
            } else {
                // 只传了设备编码时，只设置推荐标识，不排序
                resultList.stream()
                        .filter(card -> Boolean.TRUE.equals(recommendCandidateMap.get(card.getDeviceCode())))
                        .forEach(card -> card.setRecommended(Boolean.TRUE));
            }

            log.info("查询指点飞行可用无人机列表完成，返回 {} 条记录", resultList.size());
            return resultList;
        } catch (Exception e) {
            log.error("查询指点飞行可用无人机列表时发生异常", e);
            throw new CustomException("查询指点飞行可用无人机列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询无人机设备信息
     * @param param
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "uav.device_code", alarmSourceVal = "11")
    public List<UavDeviceQueryVO> queryUavListByCondition(UavDeviceDTO param) {
        return uavDeviceMapper.queryUavListByCondition(param);
    }

    /**
     * 获取当前租户当前行业下的正在飞行中的无人机设备数量
     * @return 正在飞行中的无人机设备数量
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "line.uav_code", alarmSourceVal = "11")
    public int getFlyingUavDeviceCount(UavDeviceDTO param) {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            log.warn("获取登录用户信息失败，返回0");
            return 0;
        }
        
        Date currentTime = new Date();
        
        log.info("统计正在飞行中的无人机数量，租户ID: {}, 行业编码: {}", param.getTenantId(), param.getIndustryCode());
        
        try {
            int count = uavDeviceMapper.countFlyingUavDevices(param, currentTime);
            log.info("当前租户当前行业下正在飞行中的无人机设备数量: {}", count);
            return count;
        } catch (Exception e) {
            log.error("统计正在飞行中的无人机数量时发生异常", e);
            return 0;
        }
    }
}
