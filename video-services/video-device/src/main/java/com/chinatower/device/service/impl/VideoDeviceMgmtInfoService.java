package com.chinatower.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinatower.common.CommonService;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.ITowerDeviceService;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.DeviceChannelInfo;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.kafka.KafkaProducer;
import com.chinatower.device.mapper.*;
import com.chinatower.device.service.IVideoDeviceMgmtInfoService;
import com.chinatower.device.utils.ApiInfoUtils;
import com.chinatower.device.utils.PositionUtil;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.AltitudesReq;
import com.chinatower.gis.api.entity.AltitudesRes;
import com.chinatower.gis.api.entity.LonLat;
import com.chinatower.gis.api.entity.map.GisPoiKeyWordQueryReq;
import com.chinatower.gis.api.entity.map.GisPoiKeyWordQueryRes;
import com.chinatower.gis.api.entity.map.PoiKeyWordQueryRes;
import com.chinatower.gis.api.entity.map.Pois;
import com.chinatower.industry.vo.DeviceAttachmentVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.UserRoleVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.VideoQueryAPI;
import com.chinatower.video.api.entity.PlayerGetVideoRealtimeUrlData;
import com.chinatower.video.api.entity.QueryGetEnumInfoReq;
import com.chinatower.video.api.entity.QueryOptAuthEnumResponse;
import com.chinatower.video.api.entity.VideoConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;


/**
 *
 * 功能描述: 租户视角--摄像机管理Service实现类
 *
 * @auther: zhaozj
 * @date: 2022/8/2
 * @version: release_v1.5
 * @requestID: 资源管理_摄像机管理
 */
@Service
@Slf4j
public class VideoDeviceMgmtInfoService extends BaseController implements IVideoDeviceMgmtInfoService {
    private static final String SPLIT = ",";
    private static final String TOPIC = "tenantAdministrativeRegionRelationship";
    private static final int LABEL_COUNT_LIMIT = 20;

    private static final String LABEL_TYPE_ID_CAN_NOT_NULL = "标签类型编码不能为空";

    private static final String DEFAULT_LABEL_TYPE_NAME = "摄像机标签";

    private static final String HIGH = "高";

    private static final String CENTRE = "中";

    private static final String LOW = "低";
    public static final int FIVE_THOUSAND = 5000;
    public static final String MENU_ID_CAMERA = "1000416";
    public static final String MENU_ID_PRESET = "1000417";
    public static final String MENU_ID_PATROL = "1000418";
    public static final String MENU_ID_GUARD = "1000419";

    public static final String MENU_NAME_CAMERA = "云台控制";
    public static final String MENU_NAME_PRESET = "预置位控制";
    public static final String MENU_NAME_PATROL = "巡航计划控制";
    public static final String MENU_NAME_GUARD = "看守位控制";

    private static final String CAMERA_USER_OPS_PREFIX = "camera_user_operations_";

    private static final String CAMERA_USER_HISTORY_PREFIX = "camera_user_viewHistory_";

    @Resource
    private OrganDeviceRelInfoService organDeviceRelInfoService;
    @Resource
    private DeviceInfoMapper deviceInfoMapper;
    @Resource
    private ChannelInfoMapper channelInfoMapper;
    @Resource
    private TenantDeviceRelMapper tenantDeviceRelMapper;
    @Resource
    private TenantLabelConfigMapper tenantLabelConfigMapper;
    @Resource
    private TenantLabelTypeConfigMapper tenantLabelTypeConfigMapper;
    @Resource
    private DeviceLabelsRelMapper deviceLabelsRelMapper;
    @Resource
    private KafkaProducer kafkaProducer;
    @Resource
    private TenantDeviceAttachmentMapper tenantDeviceAttachmentMapper;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private GisPgAPI gisPgAPI;

    @Autowired
    private VideoQueryAPI videoQueryAPI;

    @Resource
    private NetConnectUtils connectUtils;

    @Resource
    ICasCadeInfoService iCasCadeInfoService;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private ITowerDeviceService towerDeviceService;

    @Resource
    GisMapApi gisMapApi;

    @Value("${tjh.appKey}")
    private String appKey;

    @Value("${tjh.secretKey}")
    private String secretKey;

    /**
     * 功能描述: 根据条件分页查询设备信息
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", acceptColumn="od.accept_node", deviceColumn = "od.device_code",industryCode = "od.industry_code")
    public List<DeviceInfoVO> queryDeviceInfoPage(DeviceInfoVO deviceInfoVO) {
        log.info("查询设备---请求报文------{}", JSON.toJSONString(deviceInfoVO));
        if (StringUtils.isNoneBlank(deviceInfoVO.getOrgCode())) {
            // 根据部门查询下级部门
            deviceInfoVO.setDeptIds(deviceInfoMapper.getDeptIds(deviceInfoVO.getOrgCode()));
        }
        if(deviceInfoVO.isExport()){
            deviceInfoVO.setPageNum(null);
            deviceInfoVO.setPageSize(null);
        } else {
            if(StringUtils.isNotNull(deviceInfoVO.getPageNum()) && StringUtils.isNotNull(deviceInfoVO.getPageSize())){
                PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
            }else{
                startPage();
            }
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        deviceInfoVO.setTenantId(user.getTenantId());
        //根据条件分页查询设备信息
        List<DeviceInfoVO> resultList = deviceInfoMapper.queryDeviceInfoList(deviceInfoVO);
        // 补充设备归属组织
        if (!CollectionUtils.isEmpty(resultList)) {
            List<String> deviceCodeList=resultList.stream().map(DeviceInfoVO::getDeviceCode).collect(Collectors.toList());
            //根据设备编码列表调用基础接口查询设备离线时长
            List<JSONObject> list = getDeviceoffLineTime(resultList);
            if(!CollectionUtils.isEmpty(list)){
                for (int i = 0; i < resultList.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if(resultList.get(i).getDeviceCode().equals(list.get(j).getString("deviceCode"))){
                            resultList.get(i).setOffLineTime(list.get(j).getString("offLineTime"));
                        }
                    }
                }
                resultList.stream()
                        .filter(device -> StringUtils.isEmpty(device.getOffLineTime()))
                        .forEach(device -> {
                            String status = device.getStatus();
                            if (Constants.ZERO.equals(status)) {
                                device.setOffLineTime(Constants.ZERO);
                            } else {
                                device.setOffLineTime("-");
                            }
                        });
            }
            DeviceInfoVO qryDevOrg = new DeviceInfoVO();
            qryDevOrg.setDeviceCodeList(deviceCodeList);
            qryDevOrg.setIndustryCode(user.getIndustryCode());
            qryDevOrg.setTenantId(user.getTenantId());

            //增加适配
            if (StringUtils.isEmpty(deviceInfoVO.getQueryType())) {
                //查询组织信息
                List<Map<String, String>> orgInfoList = deviceInfoMapper.queryOrgInfoList(qryDevOrg);
                for (DeviceInfoVO deviceInfo : resultList) {
                    for (Map<String, String> map : orgInfoList) {
                        if (deviceInfo.getDeviceCode().equals(map.get(Constants.PARAM_DEVICE_CODE))) {
                            deviceInfo.setOrgName(map.get(Constants.PARAM_ORG_NAME));
                            deviceInfo.setOrgRelId(map.get(Constants.PARAM_ID));
                            deviceInfo.setOrgCode(map.get(Constants.PARAM_ORG_ID));
                        }
                    }
                }
            }

            qryDevOrg = new DeviceInfoVO();
            qryDevOrg.setDeviceCodeList(deviceCodeList);
            qryDevOrg.setTenantId(user.getTenantId());
            qryDevOrg.setIndustryCode(user.getIndustryCode());
            //增加适配参数
            qryDevOrg.setQueryAddType(deviceInfoVO.getQueryAddType());
            // 补充通道数据
            List<ChannelInfoVO> channelList = channelInfoMapper.listChannelInfoForCustDev(qryDevOrg);
            if (!CollectionUtils.isEmpty(channelList)) {
                for (ChannelInfoVO channelInfoVO : channelList) {
                    for (DeviceInfoVO deviceInfo : resultList) {
                        if (channelInfoVO.getParentDeviceCode().equals(deviceInfo.getDeviceCode())) {
                            List<ChannelInfoVO> channelInfoVOs = deviceInfo.getChildren();
                            if (channelInfoVOs == null) {
                                channelInfoVOs = new ArrayList<>();
                            }
                            channelInfoVOs.add(channelInfoVO);
                            deviceInfo.setChildren(channelInfoVOs);
                            break;
                        }
                    }
                }
            }

            // 新增查询接口合并适配
            if(null == deviceInfoVO.getQueryAddType()){
                // 设备标签信息
                List<TenantDeviceLabelVO> labelInfoList = deviceLabelsRelMapper
                        .selectLabelInfoByDeviceCode(deviceCodeList, user.getTenantId(), user.getIndustryCode());
                // key -> deviceCode value -> labelInfoList
                Map<String, List<TenantDeviceLabelVO>> deviceLabelMap = Maps.newHashMap();
                if (!CollectionUtils.isEmpty(labelInfoList)) {
                    deviceLabelMap = labelInfoList.stream().collect(Collectors.groupingBy(TenantDeviceLabelVO::getDeviceCode));
                }

                // 查询设备的收藏状态
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                SysUser user1 = deviceInfoMapper.selectUserOnlyByUserName(user.getUserName());
                qryDevOrg.setUserId(user1.getUserId());
                List<DeviceInfoVO> devChanVOS = deviceInfoMapper.queryDeviceCollStatusList(qryDevOrg);

                Map<String, List<TenantDeviceLabelVO>> finalDeviceLabelMap = deviceLabelMap;
                resultList.stream().map(item -> {
                    // 填充标签名称信息
                    if (finalDeviceLabelMap.containsKey(item.getDeviceCode())) {
                        item.setLabelNameList(finalDeviceLabelMap.get(item.getDeviceCode()).stream().map(TenantDeviceLabelVO::getLabelName).collect(Collectors.toList()));
                    }
                    devChanVOS.stream().filter(area-> Objects.equals(item.getDeviceCode(),area.getDeviceCode())
                    ).forEach(area-> {
                        item.setIsMonitor(area.getIsMonitor());
                    });
                    return item;
                }).collect(Collectors.toList());
            }
        }
        return resultList;
    }


    /**
     * 根据设备编码列表调用基础接口查询设备离线时长
     * @param resultList
     * @return
     */
    public List<JSONObject> getDeviceoffLineTime(List<DeviceInfoVO> resultList){
        List<JSONObject> list = new ArrayList<>();
        try {
            String platformCode = "";
            SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
            if(!ObjectUtils.isEmpty(config)) {
                platformCode = config.getConfigValue();
            }
            final String fromCode = platformCode;

            //根据to-code对入参分组 分组后格式：[{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
            List<Map> threadList = CommonService.groupByNode(resultList, Constants.ITEM_DEVICE_CODE, iCasCadeInfoService::getCasCadeMapByDeviceCodeBatch);

            //多线程处理
            List<Result<JSONObject>> resultData = ThreadParallelUtil.run(threadList, it -> {
                log.info("getDeviceInfoDetailNorthPost线程处理数据量:{}", it.size());
                return getDeviceInfoDetailNorthPost(it, fromCode);
            }, ThreadParallelUtil.ConfigFactory.GET_DEVICE_INFO_DETAIL);
            //融合返回结果
            list = this.getRes(resultData);
            log.info("getDeviceInfoDetailNorthPost.resultData=" + JSONObject.toJSON(list));
        } catch (Exception e) {
            log.error(DeviceNorthIntfConstants.GET_DEVICE_INFO_DETAIL_ERROR, e);
        }
        return list;
    }


    private Result<JSONObject> getDeviceInfoDetailNorthPost(Map map,String fromCode){
        try {
            String toCode = (String)map.get(VideoConstants.KEY);
            List<DeviceInfoVO> list = (List<DeviceInfoVO>)map.get(VideoConstants.VALUE);
            List<String> deviceCodeList = list.stream().map(DeviceInfoVO::getDeviceCode).collect(Collectors.toList());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("devices",deviceCodeList);
            NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(jsonObject);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DEVICE_INFO_DETAIL);
            // 接口路径参数
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.DEVICE_INFO_DETAIL + DeviceNorthIntfConstants.REQUEST_LOG + JSONObject.toJSON(paramter));
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(fromCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DEVICE_INFO_DETAIL + DeviceNorthIntfConstants.RESPONSE_LOG + JSONObject.toJSON(cr));
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            Result<JSONObject> res = ApiInfoUtils.transResult(cr, respDictData.getData());
            return res;
        } catch (Exception e) {
            log.error("多线程getDeviceInfoDetailNorthPost异常" , e);
            throw new RuntimeException(e);
        }
    }


    private List<JSONObject> getRes(List<Result<JSONObject>> resultData){
        List<JSONObject> returnData = new ArrayList<>();
        for (Result<JSONObject> datum : resultData) {
            if(datum.getData() != null){
                List<JSONObject> data = (List<JSONObject>)datum.getData().get(DeviceConstants.DATA);
                // 判断不为空
                if (!CollectionUtils.isEmpty(data)) {
                    returnData.addAll(data);
                }
            }
        }
        return returnData;
    }


    /**
     * 功能描述: 查询客户设备下通道信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    public Result<List<ChannelInfoVO>> listChannelInfoForCustDev(DeviceInfoVO deviceInfoVO) {
        if (StringUtils.isBlank(deviceInfoVO.getDeviceCode()) && CollectionUtils.isEmpty(deviceInfoVO.getDeviceCodeList())) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        return Result.data(channelInfoMapper.listChannelInfoForCustDev(deviceInfoVO));
    }

    @Override
    public Result<List<ChannelInfoVO>> listChannelInfoForCustomer(DeviceInfoVO deviceInfoVO) {
        if (StringUtils.isBlank(deviceInfoVO.getDeviceCode()) && CollectionUtils.isEmpty(deviceInfoVO.getDeviceCodeList())) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }
        return Result.data(getChannelInfoList(deviceInfoVO));
    }

    private List<ChannelInfoVO> getChannelInfoList(DeviceInfoVO deviceInfoVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        deviceInfoVO.setTenantId(user.getTenantId());
        return channelInfoMapper.listChannelInfoForCustDev(deviceInfoVO);
    }

    @Override
    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlInfo(DeviceInfoVO deviceInfoVO) {
        if (CollectionUtils.isEmpty(deviceInfoVO.getDeviceCodeList())) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }
        List<ChannelInfoVO> channelInfoList = getChannelInfoList(deviceInfoVO);
        if (CollectionUtils.isEmpty(channelInfoList)) {
            return Result.success("请求成功");
        }
        Set<PlayerGetVideoRealtimeUrlData> reqList = channelInfoList.stream().map(x -> {
            PlayerGetVideoRealtimeUrlData videoUrlData = new PlayerGetVideoRealtimeUrlData();
            videoUrlData.setDeviceCode(x.getParentDeviceCode());
            videoUrlData.setChannelCode(x.getChannelCode());
            return videoUrlData;
        }).collect(Collectors.toSet());
        Result<Set<PlayerGetVideoRealtimeUrlData>> result = videoQueryAPI.getVideoRealtimeUrlsForComponent(reqList);
        log.info("VideoDeviceMgmtInfoService getVideoRealtimeUrlInfo param:{}, result:{}",
                JSON.toJSONString(reqList), JSON.toJSONString(result));
        return result;
    }


    /**
     * 功能描述: 查询设备所属的行政区域树形
     * @param: deviceReqDTO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @DataScope(tenantColumn = "ord.tenant_id", acceptColumn="ord.accept_node", deviceColumn = "ord.device_code")
    public List<DeviceAreaTreeVO> queryAreaTree(DeviceReqDTO deviceReqDTO) {
        //增加容错，防止返回区域为空（因为订购表有这设备但是设备表没有）
        return deviceInfoMapper.getDeviceAreaTree(deviceReqDTO);
    }


    /**
     * 功能描述: 根据条件查询设备信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", acceptColumn="od.accept_node", deviceColumn = "od.device_code",industryCode = "od.industry_code")
    public Result<DeviceInfoVO> getDeviceInfo(DeviceInfoVO deviceInfoVO) {
        if (StringUtils.isEmpty(deviceInfoVO.getDeviceCode()) && CollectionUtils.isEmpty(deviceInfoVO.getDeviceCodeList()) ) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }
        String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        deviceInfoVO.setIndustryCode(industryCode);
        //根据条件查询设备信息
        List<DeviceInfoVO> deviceInfoList = deviceInfoMapper.queryDeviceInfoList(deviceInfoVO);
        if (!CollectionUtils.isEmpty(deviceInfoList)) {
            DeviceInfoVO deviceInfo = deviceInfoList.get(0);

            DeviceInfoVO qryOrg = new DeviceInfoVO();
            qryOrg.setDeviceCode(deviceInfo.getDeviceCode());
            qryOrg.setTenantId(tenantId);
            qryOrg.setIndustryCode(industryCode);
            if (StringUtils.isEmpty(deviceInfoVO.getQueryType())) {
                //查询所属组织
                List<Map<String, String>> orgInfoList = deviceInfoMapper.queryOrgInfoList(deviceInfoVO);
                if (!CollectionUtils.isEmpty(orgInfoList)) {
                    Map<String, String> map = orgInfoList.get(0);
                    deviceInfo.setOrgName(map.get(Constants.PARAM_ORG_NAME));
                    deviceInfo.setOrgRelId(map.get(Constants.PARAM_ID));
                    deviceInfo.setOrgCode(map.get(Constants.PARAM_ORG_ID));
                }
            }

            // 补充设备附件信息
            try {
                List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCode(
                        tenantId, industryCode, deviceInfo.getDeviceCode(), IndustryConstants.ONE_NUM);
                if (!CollectionUtils.isEmpty(fileUrlList)) {
                    Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
                    // 处理Obs文件URL加密返回
                    if (stringStringMap != null && stringStringMap.size() > 0) {
                        deviceInfo.setFileUrlList(Lists.newArrayList(stringStringMap.values()));
                    }
                }
            } catch (Exception e) {
                log.error("VideoDeviceMgmtInfoService getDeviceInfo getFileInfo error, e:", e);
            }

            // 补充通道信息
            List<ChannelInfoVO> channels = channelInfoMapper.listChannelInfoForCustDev(qryOrg);
            if (StringUtils.isEmpty(deviceInfoVO.getQueryType())) {
                deviceInfo.setChildren(channels);
            } else {
                deviceInfo.setChannels(channels);
            }
            return Result.data(deviceInfo);
        }
        return Result.data(new DeviceInfoVO());
    }

    @Override
    public Result<DeviceAttachmentVO> getFileInfoByDeviceCode(String deviceCode) {
        if (StringUtils.isEmpty(deviceCode)) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }

        String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCode(
                tenantId, industryCode, deviceCode, IndustryConstants.ONE_NUM);
        DeviceAttachmentVO resultData = null;
        if (!CollectionUtils.isEmpty(fileUrlList)) {
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
            // 处理Obs文件URL加密返回
            if (stringStringMap != null && stringStringMap.size() > 0) {
                List<String> encryptFileUrlList = Lists.newArrayList(stringStringMap.values());
                List<String> mediaFileUrlList = encryptFileUrlList.stream().filter(x -> (x != null && x.toLowerCase().contains(".mp4"))).collect(Collectors.toList());
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(mediaFileUrlList)) {
                    encryptFileUrlList.removeAll(mediaFileUrlList);
                }
                resultData = new DeviceAttachmentVO();
                resultData.setMediaFileUrlList(mediaFileUrlList);
                resultData.setImgFileUrlList(encryptFileUrlList);
            }
        }
        return Result.data(resultData);
    }

    /**
     * 功能描述: 查询摄像机图片
     * @param: deviceInfoVO
     * @return: Result
     * @auther: wanglei
     * @date: 2023/7/31
     * @version: release_v1.6
     * @requestID: 大屏-AR实景优化
     */
    public Result<List<String>> getDeviceImage(DeviceInfoVO deviceInfoVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        List<String> resultList = new ArrayList<String>();
        List<String> fileUrlList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCodeOrder(
                tenantId, industryCode, deviceInfoVO.getDeviceCode(), IndustryConstants.ONE_NUM);
        if (!CollectionUtils.isEmpty(fileUrlList)) {

            // 增加默认排序，视频放前面
            fileUrlList.sort((o1, o2) -> {
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

            // 批量obs加密
            Map<String, String> stringStringMap = null;
            try
            {
                stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
            }
            catch (Exception e) {
                log.error("getDeviceImage批量obs加密异常", e);
                return Result.data(resultList);
            }
            // 处理Obs文件URL加密返回
            if (stringStringMap != null && stringStringMap.size() > 0) {
                for( String fileUrlKey : fileUrlList )
                {
                    resultList.add(stringStringMap.get(fileUrlKey));
                }
            }
        }
        return Result.data(resultList);
    }

    /**
     * 功能描述: 更新设备相关信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateDeviceInfo(DeviceInfoVO deviceInfoVO) {
        logger.info("#####################updateDeviceInfo--time--111"+ System.currentTimeMillis());


        if (StringUtils.isEmpty(deviceInfoVO.getDeviceCode())) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }
        if (StringUtils.isEmpty(deviceInfoVO.getTenantId())) {
            return Result.fail(Constants.TENANT_ID_IS_EMPTY);
        }
        try {
            // 设备通道信息
            String devName = deviceInfoVO.getDevName();
            String deleteFlag = Constants.ZERO;
            String addFlag = Constants.ZERO;
            if (StringUtils.isNotEmpty(devName) && !CollectionUtils.isEmpty(deviceInfoVO.getChannelInfoList())) {
                //更新设备通道信息
                operateTenantDeviceRel(deviceInfoVO);
            }

            logger.info("#####################updateDeviceInfo--time--222"+ System.currentTimeMillis());


            // 所属组织机构
            String orgCode = deviceInfoVO.getOrgCode();
            String oldOrgCode = deviceInfoVO.getOldOrgCode();
            String orgRelId = deviceInfoVO.getOrgRelId();
            if (StringUtils.isNotEmpty(orgCode) && StringUtils.isEmpty(orgRelId)) {
                String[] orgCodeArr = orgCode.split(SPLIT);
                List<OrganDeviceRelVO> organDeviceRelVOList = new LinkedList<>();
                for (String orgCodeStr : orgCodeArr) {
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setDeviceCode(deviceInfoVO.getDeviceCode());
                    organDeviceRelVO.setTenantId(deviceInfoVO.getTenantId());
                    organDeviceRelVO.setDeptId(orgCodeStr);
                    organDeviceRelVO.setCreateBy(SecurityUtils.getOperator());
                    organDeviceRelVO.setUpdateBy(organDeviceRelVO.getCreateBy());
                    organDeviceRelVO.setDeviceType(deviceInfoVO.getCategoryCode());
                    organDeviceRelVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
                    organDeviceRelVOList.add(organDeviceRelVO);
                }
                //批量添加组织设备关系信息
                deviceInfoMapper.addOrganDeviceRelBatch(organDeviceRelVOList);
            } else if (StringUtils.isEmpty(orgCode) && StringUtils.isNotEmpty(orgRelId)) {
                //批量删除组织设备关系信息
                deviceInfoMapper.deleteOrganDeviceRelBatch(Arrays.asList(orgRelId.split(SPLIT)));
            } else if (StringUtils.isNotEmpty(orgCode) && StringUtils.isNotEmpty(orgRelId)) {
                List<String> orgCodeList = Arrays.asList(orgCode.split(SPLIT));
                List<String> oldOrgCodeList = Arrays.asList(oldOrgCode.split(SPLIT));
                List<String> orgRelIdList = Arrays.asList(orgRelId.split(SPLIT));

                List<String> deleteList = new LinkedList<>();
                for (int i = 0; i < oldOrgCodeList.size(); i++) {
                    String oldOrgCodeStr = oldOrgCodeList.get(i);
                    if (!orgCodeList.contains(oldOrgCodeStr)) {
                        deleteList.add(orgRelIdList.get(i));
                    }
                }

                List<String> addList = new LinkedList<>();
                for (String orgCodeStr : orgCodeList) {
                    if (!oldOrgCodeList.contains(orgCodeStr)) {
                        addList.add(orgCodeStr);
                    }
                }

                if (!CollectionUtils.isEmpty(deleteList)) {
                    deleteFlag = Constants.ONE;
                    //批量删除组织设备关系信息
                    deviceInfoMapper.deleteOrganDeviceRelBatch(deleteList);
                }
                if (!CollectionUtils.isEmpty(addList)) {
                    List<OrganDeviceRelVO> organDeviceRelVOList = new LinkedList<>();
                    for (String orgCodeStr : addList) {
                        OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                        organDeviceRelVO.setDeviceCode(deviceInfoVO.getDeviceCode());
                        organDeviceRelVO.setTenantId(deviceInfoVO.getTenantId());
                        organDeviceRelVO.setDeptId(orgCodeStr);
                        organDeviceRelVO.setCreateBy(SecurityUtils.getOperator());
                        organDeviceRelVO.setDeviceType(deviceInfoVO.getCategoryCode());
                        organDeviceRelVO.setUpdateBy(organDeviceRelVO.getCreateBy());
                        organDeviceRelVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
                        organDeviceRelVOList.add(organDeviceRelVO);
                    }
                    addFlag = Constants.ONE;
                    //批量添加组织设备关系信息
                    deviceInfoMapper.addOrganDeviceRelBatch(organDeviceRelVOList);
                }
            }

            // 更新附件信息
            saveDeviceAttachmentInfo(deviceInfoVO);

            logger.info("#####################updateDeviceInfo--time--333"+ System.currentTimeMillis());

            //更新组织行政区域表
            organDeviceRelInfoService.updateDeptStreetRel(deviceInfoVO.getTenantId());

            logger.info("#####################updateDeviceInfo--time--444"+ System.currentTimeMillis());

            //发送kafka
            if (deleteFlag.equals(Constants.ONE)||addFlag.equals(Constants.ONE) ) {
                try{
                    JSONObject json = new JSONObject();
                    json.put(DeviceConstants.TENANT_ID_STR,deviceInfoVO.getTenantId());
                    logger.info("#####################updateDeviceInfo--time--555"+ System.currentTimeMillis());
                    kafkaProducer.sendToKafka(TOPIC,json.toJSONString());
                    logger.info("#####################updateDeviceInfo--time--666"+ System.currentTimeMillis());
                } catch (Exception e) {
                    log.error("send to tenantAdministrativeRegionRelationship error！", e);

                }
            }

            logger.info("#####################updateDeviceInfo--time--777"+ System.currentTimeMillis());

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("更新设备失败！", e);
        }
        return Result.success("新设备相关信息成功!");
    }

    private void saveDeviceAttachmentInfo(DeviceInfoVO deviceInfoVO) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            String tenantId = user.getTenantId();
            String industryCode = user.getIndustryCode();
            String appVerCode = user.getAppVerCode();
            String deviceCode = deviceInfoVO.getDeviceCode();
            int deviceType = IndustryConstants.ONE_NUM;
            String creator = user.getUserName();
            tenantDeviceAttachmentMapper.deleteByDeviceCode(tenantId, industryCode, deviceCode, deviceType);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceInfoVO.getFileUrlList())) {
                Date now = new Date();
                tenantDeviceAttachmentMapper.batchInsert(deviceInfoVO.getFileUrlList().stream().map(x -> {
                    TenantDeviceAttachmentVO attachmentVO = new TenantDeviceAttachmentVO();
                    attachmentVO.setDeviceCode(deviceCode);
                    attachmentVO.setDeviceType(deviceType);
                    attachmentVO.setFileUrl(x);
                    attachmentVO.setTenantId(tenantId);
                    attachmentVO.setIndustryCode(industryCode);
                    attachmentVO.setAppVerCode(appVerCode);
                    attachmentVO.setCreator(creator);
                    attachmentVO.setCreateTime(now);
                    return attachmentVO;
                }).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            logger.error("VideoDeviceMgmtInfoService saveDeviceAttachmentInfo error, e:", e);
        }
    }


    /**
     * 功能描述: 设备编辑时校验设备名称是否重复
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    public Result<Boolean> checkDeviceName(DeviceInfoVO deviceInfoVO) {
        if (StringUtils.isEmpty(deviceInfoVO.getTenantId())) {
            return Result.fail(Constants.TENANT_ID_IS_EMPTY);
        }
        //最新设备名称
        String devName = deviceInfoVO.getDevName();
        //编辑前设备名称
        String oldDevName = deviceInfoVO.getOldDevName();
        boolean unique = true;
        if (StringUtils.isNotEmpty(devName) && !devName.equals(oldDevName)) {
            //设备名称已修改
            List<String> devNameList = tenantDeviceRelMapper.getDeviceNameList(deviceInfoVO);
            if (devNameList.contains(devName)) {
                unique = false;
            }
        }
        return Result.data(unique);
    }

    @Override
    public List<TenantDeviceLabelVO> queryDeviceLabels(TenantDeviceLabelVO tenantDeviceLabelVO) {
        String tenantId = tenantDeviceLabelVO.getTenantId();
        String deviceCode = tenantDeviceLabelVO.getDeviceCode();

        if (StringUtils.isEmpty(tenantId)) {
            throw new CustomException(Constants.TENANT_ID_IS_EMPTY);
        }
        if (StringUtils.isEmpty(deviceCode)) {
            throw new CustomException(Constants.DEVICE_CODE_IS_EMPTY);
        }

        tenantDeviceLabelVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        return tenantLabelConfigMapper.queryLabelList(tenantDeviceLabelVO);
    }

    @Override
    public List<TenantDeviceLabelVO> queryTenantLabelList(TenantDeviceLabelVO tenantDeviceLabelVO) {
        String tenantId = tenantDeviceLabelVO.getTenantId();
        if (StringUtils.isEmpty(tenantId)) {
            throw new CustomException(Constants.TENANT_ID_IS_EMPTY);
        }
        tenantDeviceLabelVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        return tenantLabelConfigMapper.queryTenantLabelList(tenantDeviceLabelVO);
    }


    /**
     * 功能描述: 添加客户设备标签
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @Transactional
    public Result<String> addDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO) {
        String tenantId = tenantDeviceLabelVO.getTenantId();
        String deviceCode = tenantDeviceLabelVO.getDeviceCode();
        String labelName = tenantDeviceLabelVO.getLabelName();
        if (StringUtils.isEmpty(tenantId)) {
            return Result.fail(Constants.TENANT_ID_IS_EMPTY);
        }
        if (StringUtils.isEmpty(deviceCode)) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }
        if (StringUtils.isEmpty(labelName)) {
            return Result.fail("标签名为空");
        }

        //判断标签数是否已到达限制
        TenantLabelConfigVO tenantLabelConfigVO = new TenantLabelConfigVO(tenantId, labelName);
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        tenantLabelConfigVO.setIndustryCode(industryCode);
        //以租户维度统计标签数量
        int count = tenantLabelConfigMapper.countLabel(tenantLabelConfigVO);
        if (count >= LABEL_COUNT_LIMIT) {
            return Result.fail("标签数已到达限制");
        }

        //判断标签名是否唯一
        int countLabelName = tenantLabelConfigMapper.countLabelName(tenantLabelConfigVO);
        if (countLabelName >= 1) {
            return Result.fail("标签名重复，无法添加！");
        }

        tenantLabelConfigVO.setCreator(SecurityUtils.getOperator());
        tenantLabelConfigVO.setModifier(SecurityUtils.getOperator());
        tenantLabelConfigVO.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
        // 2023-07-18 兼容app旧接口，新增标签挂在默认标签类型【摄像机标签】下，如果标签类型不存在，则新增一个标签类型
        tenantLabelConfigVO.setLabelTypeId(getDefaultLabelTypeId(tenantId, industryCode));
        //添加标签
        tenantLabelConfigMapper.addTenantLabelConfig(tenantLabelConfigVO);
        DeviceLabelsRelVO deviceLabelsRelVO = new DeviceLabelsRelVO(tenantLabelConfigVO.getLabelId(), tenantId, deviceCode);
        //添加设备标签关系
        deviceLabelsRelMapper.addDeviceLabelsRel(deviceLabelsRelVO);

        tenantDeviceLabelVO.setLabelId(tenantLabelConfigVO.getLabelId());
        tenantDeviceLabelVO.setLabelRelId(deviceLabelsRelVO.getId());

        return Result.success("添加客户设备标签成功!");
    }

    private Long getDefaultLabelTypeId(String tenantId, String industryCode) {
        Long labelTypeId = tenantLabelTypeConfigMapper.selectLabelTypeIdByName(tenantId, industryCode, DEFAULT_LABEL_TYPE_NAME);
        if (labelTypeId == null) {
            TenantDeviceLabelTypeVO labelTypeParam = new TenantDeviceLabelTypeVO();
            labelTypeParam.setLabelTypeName(DEFAULT_LABEL_TYPE_NAME);
            Result<Long> addResult = addDeviceLabelType(labelTypeParam);
            labelTypeId = addResult.getData();
        }
        return labelTypeId;
    }


    /**
     * 功能描述: 添加客户设备标签关系
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    public Result<String> addDeviceLabelRel(TenantDeviceLabelVO tenantDeviceLabelVO) {
        String tenantId = tenantDeviceLabelVO.getTenantId();
        String deviceCode = tenantDeviceLabelVO.getDeviceCode();
        Long labelId = tenantDeviceLabelVO.getLabelId();
        if (ObjectUtil.isEmpty(labelId) || labelId <= 0) {
            return Result.fail(Constants.LABEL_ID_IS_NOT_LEGAL);
        }
        if (StringUtils.isEmpty(tenantId)) {
            return Result.fail(Constants.TENANT_ID_IS_EMPTY);
        }
        if (StringUtils.isEmpty(deviceCode)) {
            return Result.fail(Constants.DEVICE_CODE_IS_EMPTY);
        }

        DeviceLabelsRelVO deviceLabelsRelVO = new DeviceLabelsRelVO(labelId, tenantId, deviceCode);
        deviceLabelsRelMapper.addDeviceLabelsRel(deviceLabelsRelVO);
        tenantDeviceLabelVO.setLabelRelId(deviceLabelsRelVO.getId());
        return Result.success("添加客户设备标签关系成功!");
    }


    /**
     * 功能描述: 修改客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @Transactional
    public Result<String> updateDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO) {
        Long labelId = tenantDeviceLabelVO.getLabelId();
        String labelName = tenantDeviceLabelVO.getLabelName();
        Long labelRelId = tenantDeviceLabelVO.getLabelRelId();
        if (ObjectUtil.isEmpty(labelId) || labelId <= 0) {
            return Result.fail("非法的标签ID");
        }
        if (StringUtils.isEmpty(labelName)) {
            return Result.fail("标签名为空");
        }

        //判断标签名是否唯一
        SysUser user = SecurityUtils.getLoginUser().getUser();
        TenantLabelConfigVO repeatVO = new TenantLabelConfigVO(user.getTenantId(), labelName);
        repeatVO.setIndustryCode(user.getIndustryCode());
        int countLabelName = tenantLabelConfigMapper.countLabelName(repeatVO);
        if (countLabelName >= 1) {
            return Result.fail("标签名重复，无法添加！");
        }

        TenantLabelConfigVO tenantLabelConfigVO = new TenantLabelConfigVO(labelId, labelName);
        tenantLabelConfigVO.setModifier(SecurityUtils.getOperator());
        //修改客户设备标签
        tenantLabelConfigMapper.updateTenantLabelConfig(tenantLabelConfigVO);

        if (ObjectUtil.isEmpty(labelRelId)) {
            //原来没选中，添加关联关系
            addDeviceLabelRel(tenantDeviceLabelVO);
        }

        return Result.success("修改客户设备标签成功!");
    }


    /**
     * 功能描述: 解除客户设备标签关系
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    public Result<String> relieveDeviceLabelRel(TenantDeviceLabelVO tenantDeviceLabelVO) {
        Long labelRelId = tenantDeviceLabelVO.getLabelRelId();
        if (ObjectUtil.isEmpty(labelRelId)) {
            //之前未选中，现在选中，新增关联
            addDeviceLabelRel(tenantDeviceLabelVO);
        } else {
            //之前选中，现在未选中，删除关联
            deviceLabelsRelMapper.deleteDeviceLabelsRel(labelRelId);
        }

        return Result.success("解除客户设备标签关系成功!");
    }

    /**
     * 功能描述: 解除客户设备标签关系-批量-for app
     * @param: tenantDeviceLabelList 绑定的标签
     * @return: Result
     * @auther: hued
     * @date: 2023年3月29日09:30:59
     * @version: release_v1.5.80
     * @requestID: app客户摄像机管理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> relieveDeviceLabelRelBatch(TenantDeviceLabelBatchVO tenantDeviceLabelBatchVO) {
        log.info("relieveDeviceLabelRelBatch入参：" + JSONObject.toJSONString(tenantDeviceLabelBatchVO));
        String deviceCode = tenantDeviceLabelBatchVO.getDeviceCode();
        String tenantId = tenantDeviceLabelBatchVO.getTenantId();
        List<Long> labelList = tenantDeviceLabelBatchVO.getLabelList();

        List<TenantDeviceLabelVO> tenantDeviceLabelList = new ArrayList<>();
        //删除客户该摄像机下的标签关系
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceLabelsRelMapper.deleteBatch(tenantId,deviceCode, user.getIndustryCode());
        if(!CollectionUtils.isEmpty(labelList)){
            labelList.stream().forEach(e->{
                TenantDeviceLabelVO tenantDeviceLabelVO = new TenantDeviceLabelVO();
                tenantDeviceLabelVO.setDeviceCode(deviceCode);
                tenantDeviceLabelVO.setTenantId(tenantId);
                tenantDeviceLabelVO.setLabelId(e);
                tenantDeviceLabelList.add(tenantDeviceLabelVO);
            });
            //新增客户该摄像机下的标签关系
            deviceLabelsRelMapper.insertBatch(tenantDeviceLabelList);
        }
        return Result.success("摄像机标签保存成功！");
    }

    /**
     * 功能描述: 删除客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @Transactional
    public Result<String> deleteDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO) {
        Long labelId = tenantDeviceLabelVO.getLabelId();
        Long labelRelId = tenantDeviceLabelVO.getLabelRelId();
        /*if (ObjectUtil.isEmpty(labelRelId) || labelId <= 0) {
            return Result.fail("非法的标签ID");
        }*/
        //判断该标签是否被其他设备使用，如果被使用，则不允许删除
        if(deviceLabelsRelMapper.countDeviceLabelsRel(tenantDeviceLabelVO) > 0){
            return Result.fail("该标签被其他设备使用，不能进行删除!");
        }else {
            tenantLabelConfigMapper.deleteTenantLabelConfig(labelId);
            if (!ObjectUtil.isEmpty(labelRelId) && labelRelId > 0) {
                deviceLabelsRelMapper.deleteDeviceLabelsRel(labelRelId);
            }
            return Result.success("删除客户设备标签成功!");
        }
    }


    /**
     * 功能描述: 更新设备通道信息
     * @param: deviceInfoVO
     * @return:
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    private void operateTenantDeviceRel(DeviceInfoVO deviceInfoVO) {
        TenantDeviceRelVO tenantDeviceRelVO = new TenantDeviceRelVO();
        tenantDeviceRelVO.setDeviceCode(deviceInfoVO.getDeviceCode());
        tenantDeviceRelVO.setTenantId(deviceInfoVO.getTenantId());
        tenantDeviceRelVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());

        List<String> channelCodeList = new ArrayList<String>();
        for (ChannelInfoVO vo : deviceInfoVO.getChannelInfoList()) {
            channelCodeList.add(vo.getChannelCode());
        }
        tenantDeviceRelVO.setChannelCodeList(channelCodeList);

        //根据条件查询设备信息
        List<TenantDeviceRelVO> tenantDeviceRelList = tenantDeviceRelMapper.queryTenantDeviceRelList(tenantDeviceRelVO);
        List<TenantDeviceRelVO> addList = new LinkedList<>();
        List<TenantDeviceRelVO> updateList = new LinkedList<>();

        tenantDeviceRelVO.setDeviceName(deviceInfoVO.getDevName());
        tenantDeviceRelVO.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
        tenantDeviceRelVO.setCreator(SecurityUtils.getOperator());
        if (CollectionUtils.isEmpty(tenantDeviceRelList)) {
            for (ChannelInfoVO vo : deviceInfoVO.getChannelInfoList()) {
                tenantDeviceRelVO.setChannelCode(vo.getChannelCode());
                tenantDeviceRelVO.setChannelName(vo.getChannelName());
                TenantDeviceRelVO tempVO = new TenantDeviceRelVO();
                BeanUtils.copyProperties(tenantDeviceRelVO,tempVO);
                addList.add(tempVO);
            }
        } else {
            for (ChannelInfoVO vo : deviceInfoVO.getChannelInfoList()) {
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

        if (!addList.isEmpty()) {
            //新增租户设备通道关系信息
            tenantDeviceRelMapper.addTenantDeviceRel(addList);
        }
        if (!updateList.isEmpty()) {
            //更新租户设备通道关系信息
            tenantDeviceRelMapper.updateTenantDeviceRel(updateList);
        }

    }

    @Override
    public IPage<DevListForAppExtendVO> getDevListForApp(DevListForAppDTO devListForAppDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(null != user){
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser sysUser = deviceInfoMapper.selectUserOnlyByUserName(user.getUserName());
            devListForAppDTO.setUserId(sysUser.getUserId());
            devListForAppDTO.setTenantId(user.getTenantId());
            devListForAppDTO.setIndustryCode(user.getIndustryCode());
        }
        IPage<DevListForAppExtendVO> page = deviceInfoMapper.getDevListForApp(new Page(devListForAppDTO.getPageNo(), devListForAppDTO.getPageSize()), devListForAppDTO);
        List<DevListForAppExtendVO> list = page.getRecords();
        //app使用返回通道信息
        if(StringUtils.isNotEmpty(devListForAppDTO.getOptFlag()) && "app".equals(devListForAppDTO.getOptFlag())){
            List<String> deviceCodeList = list.stream().map(DevListForAppExtendVO::getDeviceCode).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                DeviceInfoVO qryDevOrg = new DeviceInfoVO();
                qryDevOrg.setDeviceCodeList(deviceCodeList);
                qryDevOrg.setTenantId(user.getTenantId());
                qryDevOrg.setIndustryCode(user.getIndustryCode());
                List<ChannelInfoVO> channelList = channelInfoMapper.listChannelInfoForCustDev(qryDevOrg);
                if (!CollectionUtils.isEmpty(channelList)) {
                    // 构建 deviceCode -> DeviceInfoVO 的映射
                    Map<String, DevListForAppExtendVO> deviceMap = list.stream().collect(Collectors.toMap(DevListForAppExtendVO::getDeviceCode, Function.identity()));
                    // 遍历通道，直接定位设备
                    for (ChannelInfoVO channel : channelList) {
                        DevListForAppExtendVO device = deviceMap.get(channel.getParentDeviceCode());
                        if (device != null) {
                            List<ChannelInfoVO> children = device.getChildren();
                            if (children == null) {
                                children = new ArrayList<>();
                                device.setChildren(children);
                            }
                            children.add(channel);
                        }
                    }
                }
            }
        }

        if(!StringUtils.isEmpty(devListForAppDTO.getOrderLong()) && !StringUtils.isEmpty(devListForAppDTO.getOrderLat())){
            for (DevListForAppExtendVO deviceInfo : list) {
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
        page.setRecords(list);
        return page;
    }


    @Override
    @DataScope(deviceColumn="aa.device_code", acceptColumn="aa.accept_node", alarmSourceVal = "4")
    public List<ChannelInfoScrVO> queryChannelList8(SiteRequestDTO siteRequestDTO) {
        siteRequestDTO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        return deviceInfoMapper.queryChannelList8(siteRequestDTO);
    }
    /**
     * @Description: 查询用户权限范围内的所有摄像机信息
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO>>
     * @Author: TangJinBo
     * @Date: 2022/12/14 18:55
     */
    @Override
    @DataScope(tenantColumn = "od.tenant_id", acceptColumn="od.accept_node", deviceColumn = "od.device_code",industryCode = "od.industry_code")
    public List<DeviceInfoVO> selectAllDeviceInfoByUserAuth(DeviceInfoVO deviceInfoVO) {
        return deviceInfoMapper.selectAllDeviceInfoByUserAuth(deviceInfoVO);
    }

    @Override
    public List<DeviceInfoVO> queryDeviceInfoPageNew(DeviceInfoVO deviceInfoVO) {
        log.info("queryDeviceInfoPageNew---请求报文------{}", JSON.toJSONString(deviceInfoVO));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        deviceInfoVO.setTenantId(user.getTenantId());
        //根据条件分页查询设备信息
        List<DeviceInfoVO> resultList = deviceInfoMapper.queryDeviceInfoListNew(deviceInfoVO);
        // 补充设备归属组织
        if (!CollectionUtils.isEmpty(resultList)) {
            List<String> deviceCodeList=resultList.stream().map(DeviceInfoVO::getDeviceCode).collect(Collectors.toList());
            DeviceInfoVO qryDevOrg = new DeviceInfoVO();
            qryDevOrg.setDeviceCodeList(deviceCodeList);
            qryDevOrg.setIndustryCode(user.getIndustryCode());
            qryDevOrg.setTenantId(user.getTenantId());

            //增加适配
            if (StringUtils.isEmpty(deviceInfoVO.getQueryType())) {
                //查询组织信息
                List<Map<String, String>> orgInfoList = deviceInfoMapper.queryOrgInfoList(qryDevOrg);
                for (DeviceInfoVO deviceInfo : resultList) {
                    for (Map<String, String> map : orgInfoList) {
                        if (deviceInfo.getDeviceCode().equals(map.get(Constants.PARAM_DEVICE_CODE))) {
                            deviceInfo.setOrgName(map.get(Constants.PARAM_ORG_NAME));
                            deviceInfo.setOrgRelId(map.get(Constants.PARAM_ID));
                            deviceInfo.setOrgCode(map.get(Constants.PARAM_ORG_ID));
                        }
                    }
                }
            }

            qryDevOrg = new DeviceInfoVO();
            qryDevOrg.setDeviceCodeList(deviceCodeList);
            qryDevOrg.setTenantId(user.getTenantId());
            qryDevOrg.setIndustryCode(user.getIndustryCode());
            // 补充通道数据
            List<ChannelInfoVO> channelList = channelInfoMapper.listChannelInfoForCustDevNew(qryDevOrg);
            if (!CollectionUtils.isEmpty(channelList)) {
                for (ChannelInfoVO channelInfoVO : channelList) {
                    for (DeviceInfoVO deviceInfo : resultList) {
                        if (channelInfoVO.getParentDeviceCode().equals(deviceInfo.getDeviceCode())) {
                            List<ChannelInfoVO> channelInfoVOs = deviceInfo.getChildren();
                            if (channelInfoVOs == null) {
                                channelInfoVOs = new ArrayList<>();
                            }
                            channelInfoVOs.add(channelInfoVO);
                            deviceInfo.setChildren(channelInfoVOs);
                            break;
                        }
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public Result<List<TenantDeviceLabelTypeVO>> listDeviceLabelType() {
        TenantDeviceLabelTypeVO param = new TenantDeviceLabelTypeVO();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        return Result.data(tenantLabelTypeConfigMapper.listDeviceLabelType(param));
    }

    @Override
    public Result<Long> addDeviceLabelType(TenantDeviceLabelTypeVO param) {
        // 校验参数
        checkLabelTypeParam(param);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        int totalCount = tenantLabelTypeConfigMapper.countByTenantInfo(tenantId, industryCode);
        if (totalCount >= 50) {
            throw new CustomException("同一客户同一行业应用下最多支持新增50个标签类型");
        }
        param.setTenantId(tenantId);
        param.setIndustryCode(industryCode);
        param.setAppVerCode(user.getAppVerCode());
        param.setCreator(user.getUserName());
        tenantLabelTypeConfigMapper.addDeviceLabelType(param);
        return Result.data(param.getId(), "新增标签类型成功");
    }

    private void checkLabelTypeParam(TenantDeviceLabelTypeVO param) {
        if (param == null || StringUtils.isEmpty(param.getLabelTypeName()) || param.getLabelTypeName().length() > 50) {
            throw new CustomException("标签类型名称最多支持输入50个字");
        }
        // 名称去空格
        param.setLabelTypeName(param.getLabelTypeName().trim());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        int existNameCount = tenantLabelTypeConfigMapper.countByLabelTypeName(tenantId, industryCode, param.getLabelTypeName(), param.getId());
        if (existNameCount > 0) {
            throw new CustomException("标签类型已存在！");
        }
    }

    @Override
    public Result<String> updateDeviceLabelType(TenantDeviceLabelTypeVO param) {
        if (param == null || param.getId() == null) {
            throw new CustomException(LABEL_TYPE_ID_CAN_NOT_NULL);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 校验权限
        checkDeviceLabelTypePermission(user, param.getId());
        // 校验参数
        checkLabelTypeParam(param);
        param.setModifier(user.getUserName());
        tenantLabelTypeConfigMapper.updateDeviceLabelType(param);
        return Result.success("修改标签类型成功");
    }

    private void checkDeviceLabelTypePermission(SysUser user, Long labelTypeId) {
        TenantDeviceLabelTypeVO labelTypeInfo = tenantLabelTypeConfigMapper.getLabelTypeInfoById(labelTypeId);
        if (labelTypeInfo == null) {
            throw new CustomException("标签类型信息不存在");
        }
        if (!StringUtils.equals(user.getTenantId(), labelTypeInfo.getTenantId())
                || !StringUtils.equals(user.getIndustryCode(), labelTypeInfo.getIndustryCode())) {
            throw new CustomException("当前账号无权操作该标签类型信息");
        }
    }

    @Override
    public Result<String> deleteDeviceLabelType(TenantDeviceLabelTypeVO param) {
        if (param == null || param.getId() == null) {
            throw new CustomException(LABEL_TYPE_ID_CAN_NOT_NULL);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 校验权限
        checkDeviceLabelTypePermission(user, param.getId());
        int labelCount = tenantLabelTypeConfigMapper.countLabelByTypeId(param.getId());
        if (labelCount > 0) {
            throw new CustomException("标签类型下已有标签，请删除标签后再试。");
        }
        param.setModifier(user.getUserName());
        tenantLabelTypeConfigMapper.deleteDeviceLabelType(param);
        return Result.success("删除标签类型成功");
    }

    @Override
    public Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode(TenantDeviceLabelRelVO param) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        List<TenantDeviceLabelDetailVO> deviceLabelList;
        if (!StringUtils.isEmpty(param.getAppFlag()) && param.getAppFlag().equals(Constants.ONE)){
            deviceLabelList = tenantLabelConfigMapper.selectLabelInfoByTenantIdWithEmptyLabelType(tenantId, industryCode);
        }else {
            deviceLabelList = tenantLabelConfigMapper.selectLabelInfoByTenantId(tenantId, industryCode);
        }

        return Result.data(assembleDeviceLabelList(deviceLabelList, tenantId, industryCode, param.getDeviceCode()));
    }

    @Override
    public Result<List<DeviceLabelInfoVO>> queryDeviceLabelInfoByCondition(DeviceLabelReq param) {
        List<DeviceLabelInfoVO> resultList = tenantLabelConfigMapper.selectDeviceLabelInfoByCondition(param);
        return Result.data(resultList);
    }

    private List<TenantDeviceLabelSelectedVO> assembleDeviceLabelList(List<TenantDeviceLabelDetailVO> deviceLabelList, String tenantId,
                                                                      String industryCode, String deviceCode) {
        List<TenantDeviceLabelSelectedVO> resultList = Lists.newArrayList();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceLabelList)) {
            // key -> labelTypeId value -> labelInfo
            LinkedHashMap<Long, List<TenantDeviceLabelDetailVO>> labelMap = deviceLabelList.stream()
                    .collect(Collectors.groupingBy(TenantDeviceLabelDetailVO::getLabelTypeId, LinkedHashMap::new, Collectors.toList()));
            log.info("VideoDeviceMgmtInfoService assembleDeviceLabelList deviceLabelList:{}, labelMap:{}", JSON.toJSONString(deviceLabelList), JSON.toJSONString(labelMap));
            // key -> labelTypeId value labelTypeName
            Map<Long, String> labelTypeMap = deviceLabelList.stream()
                    .collect(Collectors.toMap(TenantDeviceLabelDetailVO::getLabelTypeId,
                            TenantDeviceLabelDetailVO::getLabelTypeName, (a, b) -> a));
            List<Long> relLabelIdList = Lists.newArrayList();
            if (!StringUtils.isEmpty(deviceCode)) {
                // 查询设备关联的标签id列表
                List<Long> deviceLabelIdList = deviceLabelsRelMapper.selectRelLabelIdByDeviceCode(deviceCode, tenantId, industryCode);
                if (!CollectionUtils.isEmpty(deviceLabelIdList)) {
                    relLabelIdList.addAll(deviceLabelIdList);
                }
            }

            labelMap.forEach((k, v) -> {
                TenantDeviceLabelSelectedVO selectedVO = new TenantDeviceLabelSelectedVO();
                selectedVO.setLabelTypeId(k);
                selectedVO.setLabelTypeName(labelTypeMap.get(k));
                List<TenantDeviceLabelSelectedSubVO> labelInfoList = v.stream()
                        .filter(x -> x.getLabelId() != null)
                        .map(x -> {
                            TenantDeviceLabelSelectedSubVO subVO = new TenantDeviceLabelSelectedSubVO();
                            subVO.setLabelId(x.getLabelId());
                            subVO.setLabelName(x.getLabelName());
                            subVO.setIsRel(relLabelIdList.contains(x.getLabelId()) ? Constants.ONE_NUM : Constants.ZERO_NUM);
                            return subVO;
                        }).collect(Collectors.toList());
                selectedVO.setLabelInfoList(labelInfoList);
                selectedVO.setLabelCount(labelInfoList.size());
                resultList.add(selectedVO);
            });
        }
        return resultList;
    }

    @Override
    public Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode4App(String deviceCode) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        List<TenantDeviceLabelDetailVO> deviceLabelList
                = tenantLabelConfigMapper.selectLabelInfoByTenantIdWithEmptyLabelType(tenantId, industryCode);
        return Result.data(assembleDeviceLabelList(deviceLabelList, tenantId, industryCode, deviceCode));
    }

    @Override
    public Result<String> addDeviceLabelRelBatch(TenantDeviceLabelRelVO param) {
        if (param == null) {
            throw new CustomException("参数不能为空");
        }
        if (StringUtils.isEmpty(param.getDeviceCode())) {
            throw new CustomException("设备编码不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        deviceLabelsRelMapper.deleteDeviceLabelRelByDeviceCode(param.getDeviceCode(), tenantId, industryCode);
        if (!CollectionUtils.isEmpty(param.getLabelIdList())) {

            Date now = new Date();
            List<TenantDeviceLabelVO> list = param.getLabelIdList().stream().map(labelId -> {
                TenantDeviceLabelVO deviceLabelsRel = new TenantDeviceLabelVO();
                deviceLabelsRel.setLabelId(labelId);
                deviceLabelsRel.setDeviceCode(param.getDeviceCode());
                deviceLabelsRel.setTenantId(tenantId);
                deviceLabelsRel.setCreateTime(now);
                return deviceLabelsRel;
            }).collect(Collectors.toList());
            deviceLabelsRelMapper.addDeviceLabelRelBatch(list);
        }
        return Result.success("批量添加客户设备标签关系成功!");
    }

    @Override
    public Result<Long> addDeviceLabelInfo(TenantDeviceLabelInfoVO param) {
        checkDeviceLabelParam(param);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        int labelCount = tenantLabelConfigMapper.countByTenantInfo(tenantId, industryCode, param.getLabelTypeId());
        if (labelCount >= 50) {
            throw new CustomException("同一客户同一行业应用每个标签类型下最多支持新增50个标签");
        }
        param.setTenantId(tenantId);
        param.setIndustryCode(industryCode);
        param.setAppVerCode(user.getAppVerCode());
        param.setCreator(user.getUserName());
        tenantLabelConfigMapper.addDeviceLabel(param);
        return Result.data(param.getLabelId(), "新增标签成功");
    }

    private void checkDeviceLabelParam(TenantDeviceLabelInfoVO param) {
        if (param == null) {
            throw new CustomException("参数不能为空");
        }
        if (param.getLabelTypeId() == null) {
            throw new CustomException(LABEL_TYPE_ID_CAN_NOT_NULL);
        }
        if (StringUtils.isEmpty(param.getLabelName()) || param.getLabelName().length() > 50) {
            throw new CustomException("标签名称最多支持输入50个字");
        }
        // 名称去空格
        param.setLabelName(param.getLabelName().trim());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        int existNameCount = tenantLabelConfigMapper.countByLabelName(tenantId, industryCode, param.getLabelName(), param.getLabelId());
        if (existNameCount > 0) {
            throw new CustomException("标签已存在！");
        }
    }

    @Override
    public Result<String> updateDeviceLabelInfo(TenantDeviceLabelInfoVO param) {
        if (param == null || param.getLabelId() == null) {
            throw new CustomException(LABEL_TYPE_ID_CAN_NOT_NULL);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 校验权限
        checkDeviceLabelPermission(user, param.getLabelId());
        // 校验参数
        checkDeviceLabelParam(param);
        param.setModifier(user.getUserName());
        tenantLabelConfigMapper.updateDeviceLabel(param);
        return Result.success("修改标签成功");
    }

    private void checkDeviceLabelPermission(SysUser user, Long labelId) {
        TenantDeviceLabelInfoVO labelInfo = tenantLabelConfigMapper.getLabelInfoById(labelId);
        if (labelInfo == null) {
            throw new CustomException("标签信息不存在");
        }
        if (!StringUtils.equals(user.getTenantId(), labelInfo.getTenantId())
                || !StringUtils.equals(user.getIndustryCode(), labelInfo.getIndustryCode())) {
            throw new CustomException("当前账号无权操作该标签信息");
        }
    }

    @Override
    public Result<String> deleteDeviceLabelInfo(TenantDeviceLabelInfoVO param) {
        if (param == null || param.getLabelId() == null) {
            throw new CustomException(LABEL_TYPE_ID_CAN_NOT_NULL);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 校验权限
        checkDeviceLabelPermission(user, param.getLabelId());
        // 级联删除设备标签关联关系
        tenantLabelConfigMapper.deleteLabelRelByLabelId(param.getLabelId());
        param.setModifier(user.getUserName());
        tenantLabelConfigMapper.deleteDeviceLabel(param);
        return Result.success("删除标签成功");
    }

    @Override
    public Result<PageInfo<TenantDeviceLabelDetailVO>> queryDeviceLabelPage(TenantDeviceLabelQueryVO param) {
        if (null == param.getPageNum() || param.getPageNum() <= Constants.ZERO_NUM) {
            param.setPageNum(Constants.ONE_NUM);
        }
        if (null == param.getPageSize() || param.getPageSize() <= Constants.ZERO_NUM) {
            param.setPageSize(Constants.TEN_NUM);
        }
        Result<PageInfo<TenantDeviceLabelDetailVO>> result;
        PageMethod.startPage(param.getPageNum(), param.getPageSize());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        List<TenantDeviceLabelDetailVO> resultList = tenantLabelConfigMapper.queryDeviceLabelPage(param);
        PageInfo<TenantDeviceLabelDetailVO> pageInfo = new PageInfo<>(resultList);
        result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    @Override
    public Result<Integer> getLabelCountByLabelTypeId(Long labelTypeId) {
        if (labelTypeId == null) {
            throw new CustomException(LABEL_TYPE_ID_CAN_NOT_NULL);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 校验权限
        checkDeviceLabelTypePermission(user, labelTypeId);
        // 根据标签类型编码查询客户+行业下的标签数量
        return Result.data(tenantLabelConfigMapper.getLabelCountByLabelTypeId(user.getTenantId(),
                user.getIndustryCode(), labelTypeId));
    }


    /**
     * 功能描述: 资源管理-摄像机管理-摄像机操控权限列表查询接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    @DataScope(tenantColumn = "ord.tenant_id", acceptColumn="ord.accept_node",deptColumn = "",industryCode = "ord.industry_code")
    public Map<String,Object> queryDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO) {
        //根据条件分页查询设备信息
        List<DeviceCustomerOptAuthVO> customerOptAuthVOList = deviceInfoMapper.queryDeviceOptAuth(deviceOptAuthVO);
        List<DeviceCustomerOptAuthVO> newOptAuthVOList = new ArrayList<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceOptAuthVO.setTenantId(user.getTenantId());
        deviceOptAuthVO.setIndustryCode(user.getIndustryCode());
        if(CollectionUtil.isNotEmpty(customerOptAuthVOList)){
            //现根据deviceCode查询组织设备关系表，查询归属组织list
            List<String> deptIdList =  deviceInfoMapper.queryDeptIdsList(deviceOptAuthVO);
            // 查询用户对应的权限信息userDeptList
            Map<DeviceCustomerOptAuthVO,Set<String>> map = new HashMap<>();
            for(DeviceCustomerOptAuthVO vo : customerOptAuthVOList){
                SysUser deptUser = new SysUser();
                deptUser.setDeptId(vo.getDeptId());
                deptUser.setIndustryCode(user.getIndustryCode());
                deptUser.setTenantId(user.getTenantId());
                deptUser.setUserId(vo.getUserId());
                //根据userId查询角色相关信息
                List<String> roles = deviceInfoMapper.queryRolesList(vo.getUserId());
                if(CollectionUtil.isNotEmpty(roles)){
                    List<SysRole> roleList =deviceInfoMapper.querySysRoleList(roles,user.getTenantId(),user.getIndustryCode(),user.getAppVerCode());
                    deptUser.setRoles(roleList);
                    Set<String> deptList = getDeptListByUserDataScope(deptUser);
                    map.put(vo,deptList);
                }
            }
            // 筛选 deptIdList 中包含的 deptList 对应的 DeviceCustomerOptAuthVO 对象
            for (Map.Entry<DeviceCustomerOptAuthVO, Set<String>> entry : map.entrySet()) {
                DeviceCustomerOptAuthVO vo = entry.getKey();
                Set<String> deptList = entry.getValue();
                // 检查 deptIdList 中的每个 deptId 是否在 deptList 中

                a:for (String deptId : deptIdList) {
                    if (deptList.contains(deptId)) {
                        newOptAuthVOList.add(vo);
                        break a;
                    }
                }
            }

        }
        // 分页处理
        int pageNum = deviceOptAuthVO.getPageNum() != null ? deviceOptAuthVO.getPageNum() : 1;
        int pageSize = deviceOptAuthVO.getPageSize() != null ? deviceOptAuthVO.getPageSize() : 10;
        // 按创建时间降序排序
        newOptAuthVOList.sort((o1, o2) -> {
            // 假设 getCreateTime() 返回的是 Date 类型
            Date createTime1 = o1.getCreateTime();
            Date createTime2 = o2.getCreateTime();
            return createTime2.compareTo(createTime1); // 降序
        });
        // 计算分页参数
        int start = (pageNum - 1) * pageSize;
        int total = newOptAuthVOList.size();
        int end = Math.min(start + pageSize, total);
        // 获取分页后的列表
        List<DeviceCustomerOptAuthVO> paginatedList = newOptAuthVOList.subList(start, end);
        // 构造返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", paginatedList);
        return map;
    }

    @Override
    @DataScope(tenantColumn = "ord.tenant_id", acceptColumn="ord.accept_node",deptColumn = "",industryCode = "ord.industry_code")
    public List<DeviceCustomerOptAuthVO> queryDeviceOptAuthNew(DeviceOptAuthVO deviceOptAuthVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceOptAuthVO.setTenantId(user.getTenantId());
        deviceOptAuthVO.setIndustryCode(user.getIndustryCode());
        deviceOptAuthVO.setAppVerCode(user.getAppVerCode());
        //现根据deviceCode查询组织设备关系表，查询归属组织list
        List<String> deptIdList =  deviceInfoMapper.queryDeptIdsList(deviceOptAuthVO);
        // 查询租户数据权限
        List<RoleDataRelVO> roleDataRelVOS = deviceInfoMapper.queryRoleDataRelList(deviceOptAuthVO);
        Map<String, List<Long>> scopeRoleMap = roleDataRelVOS.stream().filter(vo -> StringUtils.isNotBlank(vo.getDataScope())).collect(Collectors.groupingBy(RoleDataRelVO::getDataScope, Collectors.mapping(RoleDataRelVO::getRoleId, Collectors.toList())));
        Set<String> userIdList = new HashSet<String>();
        // 查询全部数据权限 1：全部数据权限
        List<Long> roleId1s = scopeRoleMap.computeIfAbsent("1", k -> new ArrayList<>());
        // 默认添加客户管理员权限
        roleId1s.add(2L);
        List<SysUser> allUserList = deviceInfoMapper.queryUserRoleList(roleId1s,deviceOptAuthVO.getTenantId());
        if(CollectionUtil.isNotEmpty(allUserList)){
            List<String> allList = allUserList.stream().map(SysUser::getUserId).distinct().collect(Collectors.toList());
            userIdList.addAll(allList);
        }
        // 查询根据组织编码，查询拥有本组织权限的用户
        List<Long> roleId2s = scopeRoleMap.get("2");
        if(CollectionUtil.isNotEmpty(roleId2s)){
            List<SysUser> list = deviceInfoMapper.queryUserRoleList(roleId2s,deviceOptAuthVO.getTenantId());
            if(CollectionUtil.isNotEmpty(list)){
                List<String> matchedUserIds = list.stream()
                        .filter(sysUser -> {
                            String deptIdStr = sysUser.getDeptId();
                            if (StringUtils.isBlank(deptIdStr)) {
                                return false;
                            }
                            // 拆分逗号分隔的 deptId
                            String[] userDeptIds = deptIdStr.split(",");
                            // 只要有一个在 deptIdSet 中，就匹配成功
                            for (String id : userDeptIds) {
                                if (deptIdList.contains(id.trim())) {
                                    return true;
                                }
                            }
                            return false;
                        }).map(SysUser::getUserId).distinct().collect(Collectors.toList());
                userIdList.addAll(matchedUserIds);
            }
        }

        // 根据组织编码，查询拥有自定义组织权限的用户
        List<Long> roleId3s = scopeRoleMap.get("3");
        if(CollectionUtil.isNotEmpty(roleId3s)){
            List<SysUser> list = deviceInfoMapper.queryCustomizeRoleList(roleId3s);
            if(CollectionUtil.isNotEmpty(list)){
                List<Long> resultRolesIds = list.stream()
                        .filter(sysUser -> {
                            String deptId = sysUser.getDeptId();
                            return deptId != null && deptIdList.contains(deptId.trim());
                        })
                        .map(SysUser::getRoleId)
                        .distinct()
                        .collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(resultRolesIds)){
                    List<SysUser> customizeRoleList = deviceInfoMapper.queryUserRoleList(resultRolesIds,deviceOptAuthVO.getTenantId());
                    if(CollectionUtil.isNotEmpty(customizeRoleList)){
                        List<String> allList = customizeRoleList.stream().map(SysUser::getUserId).distinct().collect(Collectors.toList());
                        userIdList.addAll(allList);
                    }
                }
            }
        }
        // 根据组织编码，查询拥有本组织及以下权限的用户
        List<Long> roleId4s = scopeRoleMap.computeIfAbsent("4", k -> new ArrayList<>());
        // 添加默认管理角色
        Collections.addAll(roleId4s, 4L, 5L, 6L, 7L, 8L);
        List<SysUser> list = deviceInfoMapper.queryUserRoleList(roleId4s,deviceOptAuthVO.getTenantId());
        if(CollectionUtil.isNotEmpty(list)){
            List<String> matchedUserIds = list.stream()
                    .filter(sysUser -> {
                        String deptStr = sysUser.getDeptId();
                        if (StringUtils.isBlank(deptStr)) {
                            return false;
                        }
                        // 拆分用户的多个部门
                        for (String userDept : deptStr.split(",")) {
                            userDept = userDept.trim();
                            // 检查是否属于 allowedDepts 中任意一个组织（自身或子级）
                            for (String permDept : deptIdList) {
                                if (permDept.startsWith(userDept)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }).map(SysUser::getUserId).distinct().collect(Collectors.toList());
            userIdList.addAll(matchedUserIds);
        }
        deviceOptAuthVO.setUserIdSet(userIdList);
        PageHelper.startPage(deviceOptAuthVO.getPageNum(), deviceOptAuthVO.getPageSize());
        List<DeviceCustomerOptAuthVO> customerOptAuthVOList = deviceInfoMapper.queryDeviceOptAuth(deviceOptAuthVO);
        return customerOptAuthVOList;
    }

    public Set<String> getDeptListByUserDataScope(SysUser sysUser) {
        Set<String>  deptList=new HashSet<>();
        deptList.add(sysUser.getDeptId());
        List<SysRole> roles = sysUser.getRoles();
        if(CollectionUtils.isEmpty(roles)){
            return deptList;
        }
        List<String> dataScopes = roles.stream().map(BaseDataScopeEntity::getDataScope).filter(com.chinatower.platform.common.utils.StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if(dataScopes.contains(Constants.DATA_SCOPE_ALL)){
            List<String> treeselectByTenantId = getTenantAllDeptInParameter(sysUser);
            deptList.addAll(treeselectByTenantId);
        }else{
            for (String dataScope : dataScopes) {
                switch (dataScope) {
                    case Constants.DATA_SCOPE_CUSTOM:
                        List<String> sysDepts = getTenantCutomRoleDeptInParameter(sysUser);
                        deptList.addAll(sysDepts);
                        break;
                    case Constants.DATA_SCOPE_DEPT:
                        List<String> sysDepts0 = Collections.singletonList(sysUser.getDeptId());
                        deptList.addAll(sysDepts0);
                        break;
                    case Constants.DATA_SCOPE_DEPT_AND_CHILD:
                        List<String> sysDepts1 = getTenantChildDeptInParameter(sysUser);
                        deptList.addAll(sysDepts1);
                        break;
                    default:
                        break;
                }
            }
        }
        return deptList;
    }

    private List<String> getTenantAllDeptInParameter(SysUser user) {
        return deviceInfoMapper.selectAllTanentDept(user);
    }

    private List<String> getTenantCutomRoleDeptInParameter(SysUser user) {
        return deviceInfoMapper.selectCustomTanentDept(user);
    }

    private List<String> getTenantChildDeptInParameter(SysUser user) {
        return deviceInfoMapper.selectTenantChildDept(user);
    }


    /**
     * 功能描述: 根据客户的摄像机操控权限优先级获取其用户的优先级
     * @param: deviceOptAuthVO
     * @return: Result<List<QueryGetEnumInfoReq>>
     * @auther: wanglei
     * @date: 2024/1/18
     * @version: release_v1.6.40
     */
    @Override
    public Result<QueryOptAuthEnumResponse> getUserDeviceOptAuthEnum(DeviceOptAuthVO deviceOptAuthVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceOptAuthVO.setTenantId(user.getTenantId());
        deviceOptAuthVO.setIndustryCode(user.getIndustryCode());
        deviceOptAuthVO.setConfigType(DeviceSyncConstants.ONE_STR);
        // 查询客户的优先级
        List<DeviceOptAuthVO> authInfo = deviceInfoMapper.queryDeviceOptAuthInfo(deviceOptAuthVO);
        //云台级别
        String priority = !CollectionUtils.isEmpty(authInfo) && StringUtils.isNotEmpty(authInfo.get(0).getPriority()) ?
                authInfo.get(0).getPriority() : VideoConstants.THREE_STR;
        // 预置位级别
        String presetPriority = !CollectionUtils.isEmpty(authInfo) && StringUtils.isNotEmpty(authInfo.get(0).getPresetPriority()) ?
                authInfo.get(0).getPresetPriority() : VideoConstants.THREE_STR;
        // 巡航计划级别
        String patrolPriority = !CollectionUtils.isEmpty(authInfo) && StringUtils.isNotEmpty(authInfo.get(0).getPatrolPriority()) ?
                authInfo.get(0).getPatrolPriority() : VideoConstants.THREE_STR;
        // 看守卫级别
        String guardPriority= !CollectionUtils.isEmpty(authInfo) && StringUtils.isNotEmpty(authInfo.get(0).getGuardPriority()) ?
                authInfo.get(0).getGuardPriority() : VideoConstants.THREE_STR;

        // 创建四个列表
        List<QueryGetEnumInfoReq> cameraList = new ArrayList<>();
        List<QueryGetEnumInfoReq> presetList = new ArrayList<>();
        List<QueryGetEnumInfoReq> patrolList = new ArrayList<>();
        List<QueryGetEnumInfoReq> guardList = new ArrayList<>();

        // 云台级别
        cameraList.addAll(createEnumInfoReqList(priority));
        // 预置位级别
        presetList.addAll(createEnumInfoReqList(presetPriority));
        // 巡航计划级别
        patrolList.addAll(createEnumInfoReqList(patrolPriority));
        // 看守卫级别
        guardList.addAll(createEnumInfoReqList(guardPriority));

        // 封装并返回结果
        QueryOptAuthEnumResponse response = new QueryOptAuthEnumResponse(cameraList, presetList, patrolList, guardList);
        return Result.data(response);
    }

    private List<QueryGetEnumInfoReq> createEnumInfoReqList(String priority) {
        List<QueryGetEnumInfoReq> list = new ArrayList<>();
        QueryGetEnumInfoReq high = new QueryGetEnumInfoReq();
        QueryGetEnumInfoReq medium = new QueryGetEnumInfoReq();
        QueryGetEnumInfoReq low = new QueryGetEnumInfoReq();

        high.setDictLabel(HIGH);
        medium.setDictLabel(CENTRE);
        low.setDictLabel(LOW);

        if (DeviceConstants.NINE_FLAG.equals(priority)) {
            high.setDictValue(DeviceConstants.NINE_FLAG);
            medium.setDictValue(Constants.EIGHT);
            low.setDictValue(Constants.SEVEN);
        } else if (DeviceConstants.SIX_FLAG.equals(priority)) {
            high.setDictValue(Constants.SIX);
            medium.setDictValue(Constants.FIVE);
            low.setDictValue(Constants.FOUR);
        } else if (DeviceConstants.THREE_FLAG.equals(priority)) {
            high.setDictValue(Constants.THREE);
            medium.setDictValue(Constants.TWO);
            low.setDictValue(Constants.ONE);
        } else {
            high.setDictValue(VideoConstants.THREE_STR);
            medium.setDictValue(VideoConstants.THREE_STR);
            low.setDictValue(VideoConstants.THREE_STR);
        }

        list.add(high);
        list.add(medium);
        list.add(low);
        return list;
    }

    /**
     * 功能描述: 资源管理-摄像机管理-新增摄像机操控权限接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    public Result<String> addDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO) {

        log.info("新增摄像机操控权限接口---请求报文------{}", JSON.toJSONString(deviceOptAuthVO));
        try {
            //配置类型：1铁塔2租户
            SysUser user = SecurityUtils.getLoginUser().getUser();
            if (DeviceSyncConstants.TWO_STR.equals(deviceOptAuthVO.getConfigType())) {
                if (StringUtils.isEmpty(deviceOptAuthVO.getUserId())){
                    return Result.fail(DeviceSyncConstants.USERCODE_NOT_NULL);
                }
                deviceOptAuthVO.setIndustryCode(user.getIndustryCode());
                deviceOptAuthVO.setTenantId(user.getTenantId());
            }

            deviceOptAuthVO.setCreator(user.getUserName());
            ObjectMapper mapper2 = new ObjectMapper();
            //操控时间段云台
            if (!CollectionUtils.isEmpty(deviceOptAuthVO.getOperatePeriodList())) {
                deviceOptAuthVO.setOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getOperatePeriodList()));
            }
            // 预置位时间段
            if (!CollectionUtils.isEmpty(deviceOptAuthVO.getPresetOperatePeriodList())) {
                deviceOptAuthVO.setPresetOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getPresetOperatePeriodList()));
            }
            // 巡航计划时间段
            if (!CollectionUtils.isEmpty(deviceOptAuthVO.getPatrolOperatePeriodList())) {
                deviceOptAuthVO.setPatrolOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getPatrolOperatePeriodList()));
            }
            //看守卫时间段
            if (!CollectionUtils.isEmpty(deviceOptAuthVO.getGuardOperatePeriodList())) {
                deviceOptAuthVO.setGuardOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getGuardOperatePeriodList()));
            }


            //查询该设备操控权限是否存在；存在-更新；不存在-新增
            List<DeviceOptAuthVO> authInfo = deviceInfoMapper.queryDeviceOptAuthInfo(deviceOptAuthVO);
            if (CollectionUtils.isEmpty(authInfo)) {
                deviceInfoMapper.addDeviceOptAuth(deviceOptAuthVO);
            } else {
                deviceOptAuthVO.setConfigId(authInfo.get(0).getConfigId());
                deviceInfoMapper.uptDeviceOptAuth(deviceOptAuthVO);
            }
            // 如果铁塔修改了客户的优先级，需要批量修改客户下的用户的优先级
            if (DeviceSyncConstants.ONE_STR.equals(deviceOptAuthVO.getConfigType())) {
                deviceInfoMapper.batchUptDeviceOptAuthUser(deviceOptAuthVO);
            }
        } catch (JsonProcessingException e) {
            log.error(DeviceSyncConstants.ADD_DEVICE_OPT_AUTH_ERROR,e);
        }

        return Result.success("新增成功");
    }
    /**
     * 功能描述: 资源管理-摄像机管理-批量新增摄像机操控权限接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @Override
    public Result<String> batchAddDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO) {
        log.info("新增客户操控权限接口---请求报文------{}", JSON.toJSONString(deviceOptAuthVO));
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            deviceOptAuthVO.setCreator(user.getUserName());
            ObjectMapper mapper2 = new ObjectMapper();
            //操控时间段
            if (!CollectionUtils.isEmpty(deviceOptAuthVO.getCameraOperatePeriodList())&&!CollectionUtils.isEmpty(deviceOptAuthVO.getPresetOperatePeriodList())
                    &&!CollectionUtils.isEmpty(deviceOptAuthVO.getPatrolOperatePeriodList())&&!CollectionUtils.isEmpty(deviceOptAuthVO.getGuardOperatePeriodList())) {
                deviceOptAuthVO.setOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getCameraOperatePeriodList()));
                deviceOptAuthVO.setPriority(deviceOptAuthVO.getCameraPriority());
                deviceOptAuthVO.setPresetOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getPresetOperatePeriodList()));
                deviceOptAuthVO.setGuardOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getGuardOperatePeriodList()));
                deviceOptAuthVO.setPatrolOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getPatrolOperatePeriodList()));
            }

            //查询摄像机操控权限信息
            List<DeviceOptAuthVO> authInfo = deviceInfoMapper.queryDeviceOptAuthInfo(deviceOptAuthVO);
            List<DeviceOptAuthVO> addAuthInfo = new ArrayList<>();
            List<DeviceOptAuthVO> authInfoList = new ArrayList<>();
            List<DeviceOptAuthVO> uptAuthInfo = new ArrayList<>();
            //批量新增入参整理
            for (String dev : deviceOptAuthVO.getDeviceCodeList()) {
                DeviceOptAuthVO authVO = new DeviceOptAuthVO();
                BeanUtils.copyProperties(deviceOptAuthVO,authVO);
                authVO.setDeviceCode(dev);
                authInfoList.add(authVO);
            }

            // 已有权限 Map，key = deviceCode_tenantId_industryCode_configType
            Map<String, DeviceOptAuthVO> existAuthMapTT = new HashMap<>();
            for (DeviceOptAuthVO exist : authInfo) {
                String key = exist.getDeviceCode() + "_" + exist.getTenantId() + "_"
                        + exist.getIndustryCode() + "_" + exist.getConfigType();
                existAuthMapTT.put(key, exist);
            }

            //如果表中不存在摄像机操控权限信息，入参全部新增
            if (CollectionUtils.isEmpty(authInfo)) {
                addAuthInfo.addAll(authInfoList);
            } else {
                //入参与表中数据比对，存在更新，不存在新增
                for (DeviceOptAuthVO pe : authInfoList) {
                    String existKey = pe.getDeviceCode() + "_" + pe.getTenantId() + "_"
                            + pe.getIndustryCode() + "_" + pe.getConfigType();
                    DeviceOptAuthVO existing = existAuthMapTT.get(existKey);
                    if (existing != null) {
                        pe.setConfigId(existing.getConfigId());
                        uptAuthInfo.add(pe);
                    } else {
                        addAuthInfo.add(pe);
                    }
                }
            }

            //批量新增（分批避免SQL过大）
            if (!CollectionUtils.isEmpty(addAuthInfo)) {
                List<List<DeviceOptAuthVO>> batches = Lists.partition(addAuthInfo, AUTH_INSERT_BATCH_SIZE);
                for (List<DeviceOptAuthVO> batch : batches) {
                    deviceInfoMapper.batchAddDeviceOptAuth(new ArrayList<>(batch));
                }
            }
            //批量更新（分批避免SQL过大）
            if (!CollectionUtils.isEmpty(uptAuthInfo)) {
                List<List<DeviceOptAuthVO>> batches = Lists.partition(uptAuthInfo, AUTH_UPDATE_BATCH_SIZE);
                for (List<DeviceOptAuthVO> batch : batches) {
                    deviceInfoMapper.batchUptDeviceOptAuth(new ArrayList<>(batch));
                }
            }
            // 如果铁塔修改了客户的优先级，需要批量修改客户下的用户的优先级
            if (DeviceSyncConstants.ONE_STR.equals(deviceOptAuthVO.getConfigType())) {
                deviceInfoMapper.batchUptDeviceOptAuthUser(deviceOptAuthVO);
            }
        } catch (JsonProcessingException e) {
            log.error(DeviceSyncConstants.BATCH_ADD_DEVICE_OPT_AUTH_ERROR,e);
        }

        return Result.success("批量新增成功");
    }


    //铁塔视角---判断参数是否在list集合中存在
    public int containEntityTT(String deviceCode, String tenantId,String industryCode,String configType,List<DeviceOptAuthVO> list) {
        int res = -1;
        for (int i = 0; i < list.size(); i++) {
            DeviceOptAuthVO vo = list.get(i);
            if (deviceCode.equals(vo.getDeviceCode()) && tenantId.equals(vo.getTenantId())
                    && industryCode.equals(vo.getIndustryCode()) && configType.equals(vo.getConfigType())) {
                res = i;
                break;
            }
        }
        return res;
    }

    @Override
    public Result updateAltitude(int len){
        List<LonLat> objects = deviceInfoMapper.queryNullAltitudeList();
        int size = objects.size();
        log.info("需执行"+objects.size()+"条");

        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<LonLat> subListOK = objects.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            int j = i + 1;
            this.deal(subListOK);
            log.info("批次处理,第"+j+"批,共执行"+subListOK.size());
        }
        return Result.data("执行成功");
    }

    @Override
    public Result<String> updateImageUrl(DeviceInfoVO deviceInfoVO) {
        // 恢复默认图标
        if(StringUtils.isNotEmpty(deviceInfoVO.getDefaultStatus()) && "0".equals(deviceInfoVO.getDefaultStatus())){
            deviceInfoVO.setImageUrl("");
        }
        deviceInfoMapper.updateImageUrl(deviceInfoVO);
        return Result.success("摄像机图标上传成功");
    }

    @Override
    public Result<String> updateBatchImageUrl(DeviceInfoVO deviceInfoVO) {
        deviceInfoMapper.updateBatchImageUrl(deviceInfoVO);
        return Result.success("摄像机图标批量上传成功");
    }

    @Override
    public String getVideoImageUrl(DeviceInfoVO deviceInfoVO) {
        return deviceInfoMapper.getVideoImageUrl(deviceInfoVO);
    }

    private void deal(List<LonLat> list){
        //批量调用gis服务计算海拔
        AltitudesReq req = new AltitudesReq();
        req.setLonLat(list);
        Result<List<AltitudesRes>> listResult = gisPgAPI.altitudeHeightQuerys(req);
        log.info("altitudeHeightQuerys返回报文=" + JSON.toJSONString(listResult));
        if(listResult.getCode() == ResultCode.SUCCESS.getCode()){
            List<AltitudesRes> data = listResult.getData();
            //根据设备编码更新海拔字段
            deviceInfoMapper.updateDeviceInfoBatch(data);
        }
    }

    @Override
    public Result<List<ChannelInfoVO>> getChannelsByDeviceCode(DeviceInfoVO deviceInfoVO){
        return Result.data(deviceInfoMapper.getChannelsByDeviceCode(deviceInfoVO));
    }


    private static final int AUTH_INSERT_BATCH_SIZE = 1000;
    private static final int AUTH_UPDATE_BATCH_SIZE = 500;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addCustomerOptAuth(DeviceOptAuthVO deviceOptAuthVO) {
        log.info("批量新增摄像机操控权限接口---请求报文------{}", JSON.toJSONString(deviceOptAuthVO));
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            deviceOptAuthVO.setCreator(user.getUserName());
            deviceOptAuthVO.setTenantId(user.getTenantId());
            deviceOptAuthVO.setIndustryCode(user.getIndustryCode());
            ObjectMapper mapper2 = new ObjectMapper();
            //操控时间段
            if (!CollectionUtils.isEmpty(deviceOptAuthVO.getCameraOperatePeriodList())&&!CollectionUtils.isEmpty(deviceOptAuthVO.getPresetOperatePeriodList())
                    &&!CollectionUtils.isEmpty(deviceOptAuthVO.getPatrolOperatePeriodList())&&!CollectionUtils.isEmpty(deviceOptAuthVO.getGuardOperatePeriodList())) {
                deviceOptAuthVO.setOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getCameraOperatePeriodList()));
                deviceOptAuthVO.setPriority(deviceOptAuthVO.getCameraPriority());
                deviceOptAuthVO.setPresetOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getPresetOperatePeriodList()));
                deviceOptAuthVO.setGuardOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getGuardOperatePeriodList()));
                deviceOptAuthVO.setPatrolOperatePeriod(mapper2.writeValueAsString(deviceOptAuthVO.getPatrolOperatePeriodList()));
            }

            //查询摄像机操控权限信息
            List<DeviceOptAuthVO> authInfo = deviceInfoMapper.queryDeviceOptAuthInfo(deviceOptAuthVO);
            List<DeviceOptAuthVO> addAuthInfo = new ArrayList<>();
            List<DeviceOptAuthVO> uptAuthInfo = new ArrayList<>();
            //批量新增入参整理
            List<String> userIdList = deviceOptAuthVO.getUserIdList();
            List<String> deviceCodeList = deviceOptAuthVO.getDeviceCodeList();

            // 查询租户的优先级
            DeviceOptAuthVO vo = new DeviceOptAuthVO();
            vo.setTenantId(user.getTenantId());
            vo.setIndustryCode(user.getIndustryCode());
            vo.setConfigType(DeviceSyncConstants.ONE_STR);
            vo.setDeviceCodeList(deviceCodeList);
            List<DeviceOptAuthVO> tenantAuthInfo = deviceInfoMapper.queryDeviceOptAuthInfo(vo);

            // 租户优先级 Map，key = tenantId_industryCode_deviceCode
            Map<String, DeviceOptAuthVO> priorityMap = new HashMap<>();
            for (DeviceOptAuthVO authVO : tenantAuthInfo) {
                String key = authVO.getTenantId() + "_" + authVO.getIndustryCode()+ "_" + authVO.getDeviceCode();
                priorityMap.put(key, authVO);
            }

            // 已有权限 Map，key = deviceCode_tenantId_industryCode_userId_configType
            Map<String, DeviceOptAuthVO> existAuthMap = new HashMap<>();
            for (DeviceOptAuthVO exist : authInfo) {
                String key = exist.getDeviceCode() + "_" + exist.getTenantId() + "_"
                        + exist.getIndustryCode() + "_" + exist.getUserId() + "_" + exist.getConfigType();
                existAuthMap.put(key, exist);
            }

            // 双重循环构建权限列表，使用 HashMap 判断新增/更新
            for (String userId : userIdList) {
                for (String deviceCode : deviceCodeList) {
                    DeviceOptAuthVO authVO = new DeviceOptAuthVO();
                    BeanUtils.copyProperties(deviceOptAuthVO, authVO);
                    //判断用户真实权限
                    String priorityKey = user.getTenantId() + "_" +user.getIndustryCode() + "_" +  deviceCode;
                    DeviceOptAuthVO deviceOptAuthVO1 = priorityMap.get(priorityKey);
                    //云台级别
                    String priority = null != deviceOptAuthVO1 && StringUtils.isNotEmpty(deviceOptAuthVO1.getPriority()) ?
                            deviceOptAuthVO1.getPriority() : VideoConstants.THREE_STR;
                    // 预置位级别
                    String presetPriority = null != deviceOptAuthVO1 && StringUtils.isNotEmpty(deviceOptAuthVO1.getPresetPriority()) ?
                            deviceOptAuthVO1.getPresetPriority() : VideoConstants.THREE_STR;
                    // 巡航计划级别
                    String patrolPriority = null != deviceOptAuthVO1 && StringUtils.isNotEmpty(deviceOptAuthVO1.getPatrolPriority()) ?
                            deviceOptAuthVO1.getPatrolPriority() : VideoConstants.THREE_STR;
                    // 看守卫级别
                    String guardPriority= null != deviceOptAuthVO1 && StringUtils.isNotEmpty(deviceOptAuthVO1.getGuardPriority()) ?
                            deviceOptAuthVO1.getGuardPriority() : VideoConstants.THREE_STR;
                    //设置四种类型权限级别
                    setPriorities(authVO,priority,presetPriority,patrolPriority,guardPriority);
                    authVO.setUserId(userId);
                    authVO.setDeviceCode(deviceCode);

                    String existKey = deviceCode + "_" + user.getTenantId() + "_"
                            + user.getIndustryCode() + "_" + userId + "_" + authVO.getConfigType();
                    DeviceOptAuthVO existing = existAuthMap.get(existKey);
                    if (existing != null) {
                        // 已存在 → 更新，携带 configId
                        authVO.setConfigId(existing.getConfigId());
                        uptAuthInfo.add(authVO);
                    } else {
                        addAuthInfo.add(authVO);
                    }
                }
            }

            //批量新增（分批避免SQL过大）
            if (!CollectionUtils.isEmpty(addAuthInfo)) {
                List<List<DeviceOptAuthVO>> batches = Lists.partition(addAuthInfo, AUTH_INSERT_BATCH_SIZE);
                for (List<DeviceOptAuthVO> batch : batches) {
                    deviceInfoMapper.batchAddDeviceOptAuth(new ArrayList<>(batch));
                }
            }
            //批量更新（分批避免SQL过大）
            if (!CollectionUtils.isEmpty(uptAuthInfo)) {
                List<List<DeviceOptAuthVO>> batches = Lists.partition(uptAuthInfo, AUTH_UPDATE_BATCH_SIZE);
                for (List<DeviceOptAuthVO> batch : batches) {
                    deviceInfoMapper.batchUptDeviceOptAuth(new ArrayList<>(batch));
                }
            }
        } catch (JsonProcessingException e) {
            log.error(DeviceSyncConstants.BATCH_ADD_DEVICE_OPT_AUTH_ERROR, e);
            throw new RuntimeException("批量新增失败：" + e.getMessage(), e);
        }
        return Result.success("批量新增成功");
    }

    // 定义一个辅助方法，用于更新优先级，现在包含authVO参数
    private void updatePriority(DeviceOptAuthVO authVO, String currentPriority, String flag, Consumer<String> setter) {
        switch (currentPriority) {
            case Constants.ONE:
                if (DeviceConstants.NINE_FLAG.equals(flag)) {
                    setter.accept(Constants.SEVEN);
                } else if (DeviceConstants.SIX_FLAG.equals(flag)) {
                    setter.accept(Constants.FOUR);
                } else if (DeviceConstants.THREE_FLAG.equals(flag)) {
                    setter.accept(Constants.ONE);
                }
                break;
            case Constants.TWO:
                if (DeviceConstants.NINE_FLAG.equals(flag)) {
                    setter.accept(Constants.EIGHT);
                } else if (DeviceConstants.SIX_FLAG.equals(flag)) {
                    setter.accept(Constants.FIVE);
                } else if (DeviceConstants.THREE_FLAG.equals(flag)) {
                    setter.accept(Constants.TWO);
                }
                break;
            case Constants.THREE:
                if (DeviceConstants.NINE_FLAG.equals(flag)) {
                    setter.accept(Constants.NINE);
                } else if (DeviceConstants.SIX_FLAG.equals(flag)) {
                    setter.accept(Constants.SIX);
                } else if (DeviceConstants.THREE_FLAG.equals(flag)) {
                    setter.accept(Constants.THREE);
                }
                break;
            default:
                // 处理未定义的优先级情况
                break;
        }
    }

    // 使用上述辅助方法来更新不同的优先级
    private void setPriorities(DeviceOptAuthVO authVO, String priority, String presetPriority, String patrolPriority, String guardPriority) {
        // 云台级别
        updatePriority(authVO, authVO.getPriority(), priority, authVO::setPriority);

        // 预置位级别
        updatePriority(authVO, authVO.getPresetPriority(), presetPriority, authVO::setPresetPriority);

        // 巡航计划级别
        updatePriority(authVO, authVO.getPatrolPriority(), patrolPriority, authVO::setPatrolPriority);

        // 看守卫级别
        updatePriority(authVO, authVO.getGuardPriority(), guardPriority, authVO::setGuardPriority);
    }


        @Override
    public Result<DeviceOptAuthVO> checkDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //先查询租户状态
        deviceOptAuthVO.setTenantId(user.getTenantId());
        deviceOptAuthVO.setIndustryCode(user.getIndustryCode());
        deviceOptAuthVO.setConfigType(Constants.ONE);
        deviceOptAuthVO.setUserId(null);
        DeviceOptAuthVO deviceTenantOptAuthVO = deviceInfoMapper.checkDeviceOptAuth(deviceOptAuthVO);
        //查询客户状态
        SysUser user1 = deviceInfoMapper.selectUserOnlyByUserName(user.getUserName());
        deviceOptAuthVO.setUserId(user1.getUserId());
        deviceOptAuthVO.setConfigType(Constants.TWO);
        DeviceOptAuthVO deviceUserOptAuthVO = deviceInfoMapper.checkDeviceOptAuth(deviceOptAuthVO);
        DeviceOptAuthVO vo = getDeviceOptAuthVO(deviceTenantOptAuthVO,deviceUserOptAuthVO,user1.getUserId()) ;
        return Result.data(vo);
    }

    @Override
    public Result<List<DeviceOptAuthVO>> getDeviceOptAuthList(DeviceOptAuthVO deviceOptAuthVO) {
        List<String> deviceCodeList = deviceOptAuthVO.getDeviceCodeList();
        if (CollectionUtils.isEmpty(deviceCodeList)) {
            return Result.data(Collections.emptyList());
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        SysUser user1 = deviceInfoMapper.selectUserOnlyByUserName(user.getUserName());
        String userId = user1 != null ? user1.getUserId() : null;
        // 1. 构建查询条件
        DeviceOptAuthVO query = new DeviceOptAuthVO();
        query.setTenantId(tenantId);
        query.setIndustryCode(industryCode);
        query.setDeviceCodeList(deviceCodeList);

        // 2. 批量查询：租户权限 (configType = 1)
        query.setConfigType(Constants.ONE);
        query.setUserId(null);
        List<DeviceOptAuthVO> tenantAuthList = deviceInfoMapper.getDeviceOptAuthBatch(query);

        // 3. 批量查询：用户权限 (configType = 2)
        query.setConfigType(Constants.TWO);
        query.setUserId(userId);
        List<DeviceOptAuthVO> userAuthList = deviceInfoMapper.getDeviceOptAuthBatch(query);

        // 4. 构建 Map，用于快速查找
        Map<String, DeviceOptAuthVO> tenantAuthMap = tenantAuthList.stream()
                .collect(Collectors.toMap(DeviceOptAuthVO::getDeviceCode, Function.identity(), (a, b) -> a));

        Map<String, DeviceOptAuthVO> userAuthMap = userAuthList.stream()
                .collect(Collectors.toMap(DeviceOptAuthVO::getDeviceCode, Function.identity(), (a, b) -> a));

        // 5. 开始循环直接从 Map 取值
        List<DeviceOptAuthVO> list = new ArrayList<>();
        for (String dto : deviceCodeList) {
            // 直接从 Map 中获取，不再调用数据库
            DeviceOptAuthVO deviceTenantOptAuthVO = tenantAuthMap.get(dto); // 租户权限
            DeviceOptAuthVO deviceUserOptAuthVO = userAuthMap.get(dto); // 用户权限
            DeviceOptAuthVO vo = getDeviceOptAuthVO(deviceTenantOptAuthVO,deviceUserOptAuthVO,userId) ;
            vo.setDeviceCode(dto);
            list.add(vo);
        }
        return Result.data(list);
    }


    private DeviceOptAuthVO getDeviceOptAuthVO(DeviceOptAuthVO deviceTenantOptAuthVO,DeviceOptAuthVO deviceUserOptAuthVO,String userId){
        DeviceOptAuthVO vo = new DeviceOptAuthVO();
        //用户未配置权限
        if(null==deviceUserOptAuthVO){
            log.info("用户未配置权限");
            if(null != deviceTenantOptAuthVO){
                log.info("租户配置权限");
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getCameraStatus())){
                    vo.setCameraStatus(Constants.ONE);
                }else{
                    if(ifPeriodAuth(deviceTenantOptAuthVO.getOperatePeriod())){
                        vo.setCameraStatus(Constants.ZERO);
                    }else{
                        vo.setCameraStatus(Constants.ONE);
                    }
                }
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getPresetStatus())){
                    vo.setPresetStatus(Constants.ONE);
                }else{
                    if(ifPeriodAuth(deviceTenantOptAuthVO.getPresetOperatePeriod())){
                        vo.setPresetStatus(Constants.ZERO);
                    }else{
                        vo.setPresetStatus(Constants.ONE);
                    }
                }
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getPatrolStatus())){
                    vo.setPatrolStatus(Constants.ONE);
                }else{
                    if(ifPeriodAuth(deviceTenantOptAuthVO.getPatrolOperatePeriod())){
                        vo.setPatrolStatus(Constants.ZERO);
                    }else{
                        vo.setPatrolStatus(Constants.ONE);
                    }
                }
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getGuardStatus())){
                    vo.setGuardStatus(Constants.ONE);
                }else{
                    if(ifPeriodAuth(deviceTenantOptAuthVO.getGuardOperatePeriod())){
                        vo.setGuardStatus(Constants.ZERO);
                    }else{
                        vo.setGuardStatus(Constants.ONE);
                    }
                }
            }else{
                // 查看历史数据是否有权限
                log.info("查看历史数据是否有权限");
                List<String> userHisAuth = deviceInfoMapper.queryHisAuthList(userId);
                if(CollectionUtil.isNotEmpty(userHisAuth)){
                    if (userHisAuth.contains(Constants.ONE)) {
                        vo.setCameraStatus(Constants.ONE);
                    } else {
                        vo.setCameraStatus(Constants.ZERO);
                    }
                    if (userHisAuth.contains(Constants.TWO)) {
                        vo.setPresetStatus(Constants.ONE);
                    } else {
                        vo.setPresetStatus(Constants.ZERO);
                    }
                    if (userHisAuth.contains(Constants.THREE)) {
                        vo.setPatrolStatus(Constants.ONE);
                    } else {
                        vo.setPatrolStatus(Constants.ZERO);
                    }
                    if (userHisAuth.contains(Constants.FOUR)) {
                        vo.setGuardStatus(Constants.ONE);
                    } else {
                        vo.setGuardStatus(Constants.ZERO);
                    }
                }else{
                    vo.setCameraStatus(Constants.ZERO);
                    vo.setPresetStatus(Constants.ZERO);
                    vo.setPatrolStatus(Constants.ZERO);
                    vo.setGuardStatus(Constants.ZERO);
                }
            }
        }else{
            //用户已配置权限
            log.info("用户配置权限");
            if(null != deviceTenantOptAuthVO){
                log.info("铁塔配置权限1");
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getCameraStatus())){
                    vo.setCameraStatus(Constants.ONE);
                }else{
                    if(Constants.ONE.equals(deviceUserOptAuthVO.getCameraStatus())){
                        vo.setCameraStatus(Constants.ONE);
                    }else{
                        //验证时间范围
                        if(ifPeriodAuth(deviceUserOptAuthVO.getOperatePeriod())){
                            vo.setCameraStatus(Constants.ZERO);
                        }else{
                            vo.setCameraStatus(Constants.ONE);
                        }
                    }
                }
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getPresetStatus())){
                    vo.setPresetStatus(Constants.ONE);
                }else{
                    //验证时间范围
                    if(Constants.ONE.equals(deviceUserOptAuthVO.getPresetStatus())) {
                        vo.setPresetStatus(Constants.ONE);
                    }else{
                        if(ifPeriodAuth(deviceUserOptAuthVO.getPresetOperatePeriod())){
                            vo.setPresetStatus(Constants.ZERO);
                        }else{
                            vo.setPresetStatus(Constants.ONE);
                        }
                    }
                }
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getPatrolStatus())){
                    vo.setPatrolStatus(Constants.ONE);
                }else{
                    //验证时间范围
                    if(Constants.ONE.equals(deviceUserOptAuthVO.getPatrolStatus())) {
                        vo.setPatrolStatus(Constants.ONE);
                    }else{
                        if(ifPeriodAuth(deviceUserOptAuthVO.getPatrolOperatePeriod())){
                            vo.setPatrolStatus(Constants.ZERO);
                        }else{
                            vo.setPatrolStatus(Constants.ONE);
                        }
                    }
                }
                if(Constants.ONE.equals(deviceTenantOptAuthVO.getGuardStatus())){
                    vo.setGuardStatus(Constants.ONE);
                }else{
                    if(Constants.ONE.equals(deviceUserOptAuthVO.getGuardStatus())) {
                        vo.setGuardStatus(Constants.ONE);
                    }else{
                        //验证时间范围
                        if(ifPeriodAuth(deviceUserOptAuthVO.getGuardOperatePeriod())){
                            vo.setGuardStatus(Constants.ZERO);
                        }else{
                            vo.setGuardStatus(Constants.ONE);
                        }
                    }
                }
            }else{
                // 先查用户状态
                if(Constants.ONE.equals(deviceUserOptAuthVO.getCameraStatus())) {
                    vo.setCameraStatus(Constants.ONE);
                }else{
                    //验证云台时间范围
                    if(ifPeriodAuth(deviceUserOptAuthVO.getOperatePeriod())){
                        vo.setCameraStatus(Constants.ZERO);
                    }else{
                        vo.setCameraStatus(Constants.ONE);
                    }
                }

                if(Constants.ONE.equals(deviceUserOptAuthVO.getPresetStatus())) {
                    vo.setPresetStatus(Constants.ONE);
                }else{
                    //验证预置位时间范围
                    if(ifPeriodAuth(deviceUserOptAuthVO.getPresetOperatePeriod())){
                        vo.setPresetStatus(Constants.ZERO);
                    }else{
                        vo.setPresetStatus(Constants.ONE);
                    }
                }
                if(Constants.ONE.equals(deviceUserOptAuthVO.getPatrolStatus())) {
                    vo.setPatrolStatus(Constants.ONE);
                }else{
                    //验证巡航计划时间范围
                    if(ifPeriodAuth(deviceUserOptAuthVO.getPatrolOperatePeriod())){
                        vo.setPatrolStatus(Constants.ZERO);
                    }else{
                        vo.setPatrolStatus(Constants.ONE);
                    }
                }
                if(Constants.ONE.equals(deviceUserOptAuthVO.getGuardStatus())) {
                    vo.setGuardStatus(Constants.ONE);
                }else{
                    //验证看守卫时间范围
                    if(ifPeriodAuth(deviceUserOptAuthVO.getGuardOperatePeriod())){
                        vo.setGuardStatus(Constants.ZERO);
                    }else{
                        vo.setGuardStatus(Constants.ONE);
                    }
                }
            }
        }
        return vo;
    }


    private boolean ifPeriodAuth(String period){
        boolean resFlag = true;
        try {
            List<String> operatePeriodList = JSONArray.parseArray(period,String.class);
            String nowTimeStr = DateUtils.getTime();
            String startTimeStr = DateUtils.getDate() + VideoConstants.BLANK_SPACE + operatePeriodList.get(VideoConstants.ZERO);
            String endTimeStr = DateUtils.getDate() + VideoConstants.BLANK_SPACE + operatePeriodList.get(VideoConstants.ONE);
            resFlag = DateUtils.isEffectiveDateHMS(nowTimeStr, startTimeStr, endTimeStr);
        } catch (Exception e) {
            log.error(VideoConstants.TIME_PARSE_ERROR, e);
        }
        return resFlag;
    }



    /**
     * 用户操控权限列表批量保存
     * @param deviceOptAuthVOList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchCustomerOptAuth(List<DeviceOptAuthVO> deviceOptAuthVOList) {
        if(CollectionUtil.isNotEmpty(deviceOptAuthVOList)){
            List<DeviceOptAuthVO> optAuthList = new ArrayList<>();
            Set<String> tenantIdList = new HashSet<>();
            Set<String> industryCodeList = new HashSet<>();
            List<String> deviceCodeList = new ArrayList<>();
            List<String> userIdList = new ArrayList<>();
            ObjectMapper mapper2 = new ObjectMapper();
            SysUser user = SecurityUtils.getLoginUser().getUser();
            try {
                for(DeviceOptAuthVO optAuthVO :deviceOptAuthVOList){
                    DeviceOptAuthVO vo= new DeviceOptAuthVO();
                    BeanUtils.copyProperties(optAuthVO, vo);
                    vo.setOperatePeriod(mapper2.writeValueAsString(optAuthVO.getCameraOperatePeriodList()));
                    vo.setPriority(optAuthVO.getCameraPriority());
                    vo.setPresetOperatePeriod(mapper2.writeValueAsString(optAuthVO.getPresetOperatePeriodList()));
                    vo.setGuardOperatePeriod(mapper2.writeValueAsString(optAuthVO.getGuardOperatePeriodList()));
                    vo.setPatrolOperatePeriod(mapper2.writeValueAsString(optAuthVO.getPatrolOperatePeriodList()));
                    vo.setCreator(user.getUserName());
                    tenantIdList.add(optAuthVO.getTenantId());
                    industryCodeList.add(optAuthVO.getIndustryCode());
                    deviceCodeList.add(optAuthVO.getDeviceCode());
                    userIdList.add(optAuthVO.getUserId());
                    optAuthList.add(vo);
                }

                //查询摄像机操控权限信息
                DeviceOptAuthVO deviceOptAuthVO = new DeviceOptAuthVO();
                deviceOptAuthVO.setConfigType("2");
                deviceOptAuthVO.setIndustryCodeList(industryCodeList);
                deviceOptAuthVO.setTenantIdList(tenantIdList);
                deviceOptAuthVO.setDeviceCodeList(deviceCodeList);
                deviceOptAuthVO.setUserIdList(userIdList);
                List<DeviceOptAuthVO> authInfo = deviceInfoMapper.queryDeviceOptAuthInfo(deviceOptAuthVO);
                List<DeviceOptAuthVO> addAuthInfo = new ArrayList<>();
                List<DeviceOptAuthVO> uptAuthInfo = new ArrayList<>();

                // 已有权限 Map，key = deviceCode_tenantId_industryCode_userId_configType
                Map<String, DeviceOptAuthVO> existAuthMap = new HashMap<>();
                for (DeviceOptAuthVO exist : authInfo) {
                    String key = exist.getDeviceCode() + "_" + exist.getTenantId() + "_"
                            + exist.getIndustryCode() + "_" + exist.getUserId() + "_" + exist.getConfigType();
                    existAuthMap.put(key, exist);
                }

                //如果表中不存在摄像机操控权限信息，入参全部新增
                if (CollectionUtils.isEmpty(authInfo)) {
                    addAuthInfo.addAll(optAuthList);
                } else {
                    //入参与表中数据比对，存在更新，不存在新增
                    for (DeviceOptAuthVO pe : optAuthList) {
                        String existKey = pe.getDeviceCode() + "_" + pe.getTenantId() + "_"
                                + pe.getIndustryCode() + "_" + pe.getUserId() + "_" + pe.getConfigType();
                        DeviceOptAuthVO existing = existAuthMap.get(existKey);
                        if (existing != null) {
                            pe.setConfigId(existing.getConfigId());
                            uptAuthInfo.add(pe);
                        } else {
                            addAuthInfo.add(pe);
                        }
                    }
                }

                //批量新增（分批避免SQL过大）
                if (!CollectionUtils.isEmpty(addAuthInfo)) {
                    List<List<DeviceOptAuthVO>> batches = Lists.partition(addAuthInfo, AUTH_INSERT_BATCH_SIZE);
                    for (List<DeviceOptAuthVO> batch : batches) {
                        deviceInfoMapper.batchAddDeviceOptAuth(new ArrayList<>(batch));
                    }
                }
                //批量更新（分批避免SQL过大）
                if (!CollectionUtils.isEmpty(uptAuthInfo)) {
                    List<List<DeviceOptAuthVO>> batches = Lists.partition(uptAuthInfo, AUTH_UPDATE_BATCH_SIZE);
                    for (List<DeviceOptAuthVO> batch : batches) {
                        deviceInfoMapper.batchUptDeviceOptAuth(new ArrayList<>(batch));
                    }
                }
            } catch (JsonProcessingException e) {
                log.error(DeviceSyncConstants.ADD_DEVICE_OPT_AUTH_ERROR, e);
                throw new RuntimeException("批量操作失败：" + e.getMessage(), e);
            }
        }
        return Result.success("批量操作成功");
    }

    @Override
    public Result<List<String>> compareDeviceCodeList(DeviceOptAuthVO deviceOptAuthVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        DeviceReqDTO dto = new DeviceReqDTO();
        dto.setTenantId(currentUser.getUser().getTenantId());
        dto.setIndustryCode(currentUser.getUser().getIndustryCode());
        dto.setDeptIds(deviceOptAuthVO.getDeptIdList());
        List<String> deviceCodeDeptIds = deviceInfoMapper.queryDeviceCodeList(dto);
        List<String> intersectionList = new ArrayList<>( deviceOptAuthVO.getDeviceCodeList());
        if(!CollectionUtils.isEmpty(deviceCodeDeptIds)){
            intersectionList.retainAll(deviceCodeDeptIds);
        }
        return Result.data(intersectionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> insertHisUserAuth() {
        //云台权限
        String menuCameraId = deviceInfoMapper.querySysMenuId(MENU_NAME_CAMERA);
        if(StringUtil.isNotBlank(menuCameraId)){
            List<String> userIdList = deviceInfoMapper.queryHisUserAuth(menuCameraId);
            if(!CollectionUtils.isEmpty(userIdList)){
                List<List<String>> partitionList = Lists.partition(userIdList,FIVE_THOUSAND);
                partitionList.forEach(item ->
                        deviceInfoMapper.insertNotUserAuth(item,Constants.ONE)
                );
            }
        }

       //预置位权限
        String menuPresetId = deviceInfoMapper.querySysMenuId(MENU_NAME_PRESET);
        if(StringUtil.isNotBlank(menuPresetId)){
            List<String> userIdPresetList = deviceInfoMapper.queryHisUserAuth(menuPresetId);
            if(!CollectionUtils.isEmpty(userIdPresetList)){
                List<List<String>> partitionList = Lists.partition(userIdPresetList,FIVE_THOUSAND);
                partitionList.forEach(item ->
                        deviceInfoMapper.insertNotUserAuth(item,Constants.TWO)
                );
            }
        }

        //巡航计划
        String menuPatrolId = deviceInfoMapper.querySysMenuId(MENU_NAME_PATROL);
        if (StringUtil.isNotBlank(menuPatrolId)){
            List<String> userIdPatrolList = deviceInfoMapper.queryHisUserAuth(menuPatrolId);
            if(!CollectionUtils.isEmpty(userIdPatrolList)){
                List<List<String>> partitionList = Lists.partition(userIdPatrolList,FIVE_THOUSAND);
                partitionList.forEach(item ->
                        deviceInfoMapper.insertNotUserAuth(item,Constants.THREE)
                );
            }
        }


        //看守卫
        String menuGuardId = deviceInfoMapper.querySysMenuId(MENU_NAME_GUARD);
        if(StringUtil.isNotBlank(menuGuardId)){
            List<String> userIdGuardList = deviceInfoMapper.queryHisUserAuth(menuGuardId);
            if(!CollectionUtils.isEmpty(userIdGuardList)){
                List<List<String>> partitionList = Lists.partition(userIdGuardList,FIVE_THOUSAND);
                partitionList.forEach(item ->
                        deviceInfoMapper.insertNotUserAuth(item,Constants.FOUR)
                );
            }
        }

        return Result.success("保存成功");
    }

    /**
     * 租户操控权限列表批量保存
     * @param deviceOptAuthVOList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchTenantOptAuth(List<DeviceOptAuthVO> deviceOptAuthVOList) {
        if(CollectionUtil.isNotEmpty(deviceOptAuthVOList)){
            List<DeviceOptAuthVO> optAuthList = new ArrayList<>();
            Set<String> tenantIdList = new HashSet<>();
            Set<String> industryCodeList = new HashSet<>();
            List<String> deviceCodeList = new ArrayList<>();
            ObjectMapper mapper2 = new ObjectMapper();
            SysUser user = SecurityUtils.getLoginUser().getUser();
            try {
                for(DeviceOptAuthVO optAuthVO :deviceOptAuthVOList){
                    DeviceOptAuthVO vo= new DeviceOptAuthVO();
                    BeanUtils.copyProperties(optAuthVO, vo);
                    vo.setOperatePeriod(mapper2.writeValueAsString(optAuthVO.getCameraOperatePeriodList()));
                    vo.setPriority(optAuthVO.getCameraPriority());
                    vo.setPresetOperatePeriod(mapper2.writeValueAsString(optAuthVO.getPresetOperatePeriodList()));
                    vo.setGuardOperatePeriod(mapper2.writeValueAsString(optAuthVO.getGuardOperatePeriodList()));
                    vo.setPatrolOperatePeriod(mapper2.writeValueAsString(optAuthVO.getPatrolOperatePeriodList()));
                    vo.setCreator(user.getUserName());
                    tenantIdList.add(optAuthVO.getTenantId());
                    industryCodeList.add(optAuthVO.getIndustryCode());
                    deviceCodeList.add(optAuthVO.getDeviceCode());
                    optAuthList.add(vo);
                }

                //查询摄像机操控权限信息
                DeviceOptAuthVO deviceOptAuthVO = new DeviceOptAuthVO();
                deviceOptAuthVO.setConfigType(Constants.ONE);
                deviceOptAuthVO.setIndustryCodeList(industryCodeList);
                deviceOptAuthVO.setTenantIdList(tenantIdList);
                deviceOptAuthVO.setDeviceCodeList(deviceCodeList);
                List<DeviceOptAuthVO> authInfo = deviceInfoMapper.queryDeviceOptAuthInfo(deviceOptAuthVO);
                List<DeviceOptAuthVO> addAuthInfo = new ArrayList<>();
                List<DeviceOptAuthVO> uptAuthInfo = new ArrayList<>();

                // 已有权限 Map，key = deviceCode_tenantId_industryCode_configType
                Map<String, DeviceOptAuthVO> existAuthMapTT = new HashMap<>();
                for (DeviceOptAuthVO exist : authInfo) {
                    String key = exist.getDeviceCode() + "_" + exist.getTenantId() + "_"
                            + exist.getIndustryCode() + "_" + exist.getConfigType();
                    existAuthMapTT.put(key, exist);
                }

                //如果表中不存在摄像机操控权限信息，入参全部新增
                if (CollectionUtils.isEmpty(authInfo)) {
                    addAuthInfo.addAll(optAuthList);
                } else {
                    //入参与表中数据比对，存在更新，不存在新增
                    for (DeviceOptAuthVO pe : optAuthList) {
                        String existKey = pe.getDeviceCode() + "_" + pe.getTenantId() + "_"
                                + pe.getIndustryCode() + "_" + pe.getConfigType();
                        DeviceOptAuthVO existing = existAuthMapTT.get(existKey);
                        if (existing != null) {
                            pe.setConfigId(existing.getConfigId());
                            uptAuthInfo.add(pe);
                        } else {
                            addAuthInfo.add(pe);
                        }
                    }
                }

                //批量新增（分批避免SQL过大）
                if (!CollectionUtils.isEmpty(addAuthInfo)) {
                    List<List<DeviceOptAuthVO>> batches = Lists.partition(addAuthInfo, AUTH_INSERT_BATCH_SIZE);
                    for (List<DeviceOptAuthVO> batch : batches) {
                        deviceInfoMapper.batchAddDeviceOptAuth(new ArrayList<>(batch));
                    }
                }
                //批量更新（分批避免SQL过大）
                if (!CollectionUtils.isEmpty(uptAuthInfo)) {
                    List<List<DeviceOptAuthVO>> batches = Lists.partition(uptAuthInfo, AUTH_UPDATE_BATCH_SIZE);
                    for (List<DeviceOptAuthVO> batch : batches) {
                        deviceInfoMapper.batchUptDeviceOptAuth(new ArrayList<>(batch));
                    }
                }
                //需要批量修改客户下的用户的优先级
                deviceInfoMapper.batchUptDeviceOptAuthUserList(optAuthList);
            } catch (JsonProcessingException e) {
                log.error(DeviceSyncConstants.ADD_DEVICE_OPT_AUTH_ERROR, e);
                throw new RuntimeException("批量操作失败：" + e.getMessage(), e);
            }
        }
        return Result.success("批量操作成功");
    }

    /**
     * 查询摄像机节点可见性
     * @param deviceInfoVO
     * @return
     */
    @Override
    public Result<String> deviceNodeVisible(DeviceInfoVO deviceInfoVO) {
        String deviceNodeVisible = deviceInfoMapper.queryDeviceNodeVisible(deviceInfoVO);
        return Result.data(deviceNodeVisible);
    }
    public int containEntity(String deviceCode, String tenantId,String industryCode, String userId,String configType,List<DeviceOptAuthVO> list) {
        int res = -1;
        for (int i = 0; i < list.size(); i++) {
            DeviceOptAuthVO vo = list.get(i);
            if (deviceCode.equals(vo.getDeviceCode()) && tenantId.equals(vo.getTenantId())
                    && industryCode.equals(vo.getIndustryCode())  && userId.equals(vo.getUserId())&& configType.equals(vo.getConfigType())) {
                res = i;
                break;
            }
        }
        return res;
    }

    @Override
    public Result<String> keepRegistrationStart(DeviceInfoVO deviceInfoVO) {
        String token = RedisUtil.hGet(VideoConstants.TJH_LOGIN_TOKEN, VideoConstants.TOKEN, RedisDBType.PRIMARY);
        if(StringUtils.isBlank(token)){
            token = this.getToken();
        }
        return Result.data(this.addByDeviceCode(deviceInfoVO, token, true));
    }

    private String addByDeviceCode(DeviceInfoVO deviceInfoVO, String token, boolean retryCall){
        String taskId = null;
        try {
            NetConnectUtils.RequestParamter<Object> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TJH_ADD_BY_DEVICECODE);
            path.setDeviceCode(deviceInfoVO.getDeviceCode());
            // 接口路径参数
            paramter.setPath(path);
            ResultEntity cr = new ResultEntity();
            try {
                cr = connectUtils.getTjh(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.Authorization.of(token));
            }catch (Exception e){
                if(e.getMessage().contains(Constants.CODE_401)){
                    cr.setReturnCode(Constants.CODE_401);
                }else{
                    throw e;
                }
            }
            if ("0000".equals(cr.getReturnCode())) {
                taskId = String.valueOf(cr.getData());
            } else if (Constants.CODE_401.equals(cr.getReturnCode())){
                if(retryCall){
                    //token失效，调用天际航登录接口
                    this.addByDeviceCode(deviceInfoVO, this.getToken(), false);
                }
            }
        } catch (Exception e) {
            log.error("配准请求失败", e);
        }
        return taskId;
    }

    private String getToken(){
        String token = null;
        try {
            NetConnectUtils.RequestParamter<TjhTokenReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(new TjhTokenReq(appKey, secretKey));
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TJH_GET_TOKEN);
            paramter.setPath(path);
            TjhTokenRes.TjhTokenResEntity cr = connectUtils.postTjh(paramter, TjhTokenRes.TjhTokenResEntity.class);
            if ("0000".equals(cr.getReturnCode())) {
                TjhTokenRes data = cr.getData();
                token = data.getToken();
                RedisUtil.hset(VideoConstants.TJH_LOGIN_TOKEN, VideoConstants.URL, data.getUrl(), RedisDBType.PRIMARY);
                RedisUtil.hset(VideoConstants.TJH_LOGIN_TOKEN, VideoConstants.TOKEN, data.getToken(), RedisDBType.PRIMARY);
                RedisUtil.expire(VideoConstants.TJH_LOGIN_TOKEN, VideoConstants.FIVE, TimeUnit.MINUTES, RedisDBType.PRIMARY);
            }
        } catch (Exception e) {
            log.error("天际航登录失败", e);
        }
        return token;
    }

    @Override
    public Result<GetStatusRes> queryKeepRegistration(GetStatusReq getStatusReq){
        String token = RedisUtil.hGet(VideoConstants.TJH_LOGIN_TOKEN, VideoConstants.TOKEN, RedisDBType.PRIMARY);
        if(StringUtils.isBlank(token)){
            token = this.getToken();
        }
        return Result.data(this.getStatusByTaskId(getStatusReq, token, true));
    }

    private GetStatusRes getStatusByTaskId(GetStatusReq getStatusReq, String token, boolean retryCall){
        GetStatusRes getStatusRes = null;
        try {
            NetConnectUtils.RequestParamter<Object> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TJH_GET_STATUS_BY_TASKID);
            path.setTaskId(getStatusReq.getTaskId());
            // 接口路径参数
            paramter.setPath(path);
            GetStatusRes.GetStatusResEntity cr = new GetStatusRes.GetStatusResEntity();
            try {
                cr = connectUtils.getTjh(paramter, GetStatusRes.GetStatusResEntity.class,
                        NetConnectUtils.HeaderKeyEnum.Authorization.of(token));
            }catch (Exception e){
                if(e.getMessage().contains(Constants.CODE_401)){
                    cr.setReturnCode(Constants.CODE_401);
                }else{
                    throw e;
                }
            }
            if ("0000".equals(cr.getReturnCode())) {
                getStatusRes = cr.getData();
            } else if (Constants.CODE_401.equals(cr.getReturnCode())){
                if(retryCall){
                    //token失效，调用天际航登录接口
                    this.getStatusByTaskId(getStatusReq, this.getToken(), false);
                }
            }
        } catch (Exception e) {
            log.error("维持配准状态查询失败", e);
        }
        return getStatusRes;
    }

    @Override
    public Result<String> registrationStatusBack(DeviceRegistrationVO deviceRegistrationVO) {
        log.info("摄像机配准状态回传入参：" + JSON.toJSONString(deviceRegistrationVO));
        if(Constants.ONE.equals(deviceRegistrationVO.getRegistrationStatus())){
            VideoDirectionalInfo registrationInfo = deviceRegistrationVO.getRegistrationInfo();
            if(ObjectUtil.isNotEmpty(registrationInfo)){
                deviceRegistrationVO.setRegistrationInfoStr(JSON.toJSONString(registrationInfo));
            }
            CoordinateSystem coordinateSystem = deviceRegistrationVO.getCoordinateSystem();
            TjhExtendJson extendJson = new TjhExtendJson();
            if(ObjectUtil.isNotEmpty(coordinateSystem)){
                extendJson.setCoordinateSystem(coordinateSystem);
                deviceRegistrationVO.setExtendJson(JSON.toJSONString(extendJson));
            }
            if(ObjectUtil.isNotEmpty(deviceRegistrationVO.getPresetId())){
                deviceInfoMapper.updateDeviceRegistrationByPreset(deviceRegistrationVO);
            }else{
                deviceInfoMapper.updateDeviceRegistration(deviceRegistrationVO);
            }
        }
        return Result.success("请求成功！");
    }

    @Override
    public DeviceChannelInfo info(DeviceChannelInfo deviceChannelInfo) {
        return deviceInfoMapper.findDeviceChannel(deviceChannelInfo);
    }

    /**
     * 保存用户操作设备数量信息
     *
     * @param deviceInfoVO 设备信息对象，包含需要保存的设备信息
     * @return 返回操作结果，这里返回null表示尚未实现具体逻辑
     */
    @Override
    public Result saveUserOptDeviceNum(DeviceInfoVO deviceInfoVO) {
        // 构建Redis键，使用前缀加上当前操作员的标识
        String key = CAMERA_USER_OPS_PREFIX + SecurityUtils.getOperator();
        // 构建设备成员字符串，格式为设备编码:通道编码
        String cameraMember = deviceInfoVO.getDeviceCode()+":"+deviceInfoVO.getChannelCode();
        // 在Redis中对指定键的有序集合进行增量操作，增加指定成员的分数
        RedisUtil.zIncrementScore(key, cameraMember, 1.0, RedisDBType.PRIMARY);
        // 返回操作成功的结果
        return Result.success("记录成功");
    }

    @Override
    public Result saveUserViewHistory(DeviceInfoVO deviceInfoVO) {
        String key = CAMERA_USER_HISTORY_PREFIX + SecurityUtils.getOperator();
        RedisUtil.set(key,deviceInfoVO.getDeviceInfoList(),RedisDBType.PRIMARY);
        return Result.success("浏览记忆记录保存成功");
    }

    /**
     * 获取用户操作的设备通道信息
     *
     * @param deviceInfoVO 设备信息视图对象，提供设备名称和通道名称等附加信息
     * @return Result 包含设备通道信息列表的结果对象
     */
    @Override
    public Result getUserOptDeviceNum(DeviceInfoVO deviceInfoVO) {
        // 构造Redis键名，前缀加上当前操作员的信息
        String key = CAMERA_USER_OPS_PREFIX + SecurityUtils.getOperator();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //是否是铁塔账号 0否 1是
        String isTower = Constants.ZERO;
        if (!SecurityUtils.isSwitch()) {
            if (Constants.TWO.equals(user.getUserType())) {
                deviceInfoVO.setIndustryCode(user.getIndustryCode());
                deviceInfoVO.setTenantId(user.getTenantId());
            } else {
                isTower = Constants.ONE;
            }
        }else{
            isTower = Constants.ONE;
        }
        deviceInfoVO.setUserId(user.getUserId());
        deviceInfoVO.setIsTower(isTower);
        //查询配置路数
        String configValues = deviceInfoMapper.getConfigByType("video_preload");
        Integer channelNum = StringUtils.isEmpty(configValues) ? 16 : Integer.parseInt(configValues);
        Integer preloaderNum = deviceInfoMapper.getVideoPreloaderNum(deviceInfoVO);
        if(null != preloaderNum && preloaderNum < channelNum){
            channelNum = preloaderNum;
        }
        Set<String> results = RedisUtil.zReverseRangeByScore(key, Double.MIN_VALUE, Double.MAX_VALUE, 0, channelNum, RedisDBType.PRIMARY);
        // 创建一个列表来存储设备通道信息
        List<Map<String, String>> deviceChannelList = new ArrayList<>();
        // 遍历从Redis获取的结果集
        if(!CollectionUtils.isEmpty(results)){
            for (String item : results) {
                // 使用冒号分割字符串，得到设备编码和通道编码
                String[] parts = item.split(":");
                // 检查分割后的数组长度是否为2，确保数据格式正确
                if (parts.length == 2) {
                    // 创建一个新的Map来存储单个设备的通道信息
                    Map<String, String> map = new HashMap<>();
                    // 将设备编码存入Map
                    map.put("deviceCode", parts[0]);
                    // 将通道编码存入Map
                    map.put("channelCode", parts[1]);
                    // 将包含设备通道信息的Map添加到列表中
                    deviceChannelList.add(map);
                }
            }
        }
        // 返回封装了设备通道信息列表的结果对象
        return Result.data(deviceChannelList);
    }

    @Override
    public Result getUserViewHistory(DeviceInfoVO deviceInfoVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 构造Redis键名，前缀加上当前操作员的信息
        String key = CAMERA_USER_HISTORY_PREFIX + SecurityUtils.getOperator();
        //是否是铁塔账号 0否 1是
        String isTower = Constants.ZERO;
        if (!SecurityUtils.isSwitch()) {
            if (Constants.TWO.equals(user.getUserType())) {
                deviceInfoVO.setIndustryCode(user.getIndustryCode());
                deviceInfoVO.setTenantId(user.getTenantId());
            } else {
                isTower = Constants.ONE;
            }
        }else{
            isTower = Constants.ONE;
        }
        deviceInfoVO.setUserId(user.getUserId());
        deviceInfoVO.setIsTower(isTower);
        //查询配置路数
        String configValues = deviceInfoMapper.getConfigByType("video_preload");
        Integer channelNum = StringUtils.isEmpty(configValues) ? 16 : Integer.parseInt(configValues);
        Integer preloaderNum = deviceInfoMapper.getVideoPreloaderNum(deviceInfoVO);
        if(null != preloaderNum && preloaderNum < channelNum){
            channelNum = preloaderNum;
        }
        // 修改这里：直接从Redis获取List类型数据
        List<Map> results = RedisUtil.getList(key, Map.class, RedisDBType.PRIMARY);
        // 创建一个列表来存储设备通道信息
        List<Map<String, String>> deviceChannelList = new ArrayList<>();

        // 遍历从Redis获取的结果集
        if(!CollectionUtils.isEmpty(results)){
            // 这里可以添加处理逻辑，例如限制返回数量
            deviceChannelList = results.stream()
                    .limit(channelNum)
                    .map(map -> (Map<String, String>)map)  // 显式类型转换
                    .collect(Collectors.toList());
        }

        // 返回封装了设备通道信息列表的结果对象
        return Result.data(deviceChannelList);
    }

    private void buildParam(DeviceAIVO deviceAIVO){
        String allArea = deviceAIVO.getAllArea();
        String provinceName = deviceAIVO.getProvinceName();
        String cityName = deviceAIVO.getCityName();
        String countyName = deviceAIVO.getCountyName();
        if(!Constants.ONE.equals(allArea)){
            if(!StringUtils.isEmpty(provinceName)){
                deviceAIVO.setProvinceId(deviceInfoMapper.getAreaCodeByName(provinceName, Constants.TWO));
            }
            if(!StringUtils.isEmpty(cityName)){
                deviceAIVO.setCityId(deviceInfoMapper.getAreaCodeByName(cityName, Constants.THREE));
            }
            if(!StringUtils.isEmpty(countyName)){
                deviceAIVO.setCountyId(deviceInfoMapper.getAreaCodeByName(countyName, Constants.FOUR));
            }
        }
    }

    @Override
    public Result<ComprehensiveQueryAIResult> comprehensiveQueryAITower(DeviceAIVO deviceAIVO){
        this.buildParam(deviceAIVO);
        deviceAIVO.setAdCode(this.getAdCode(deviceAIVO));
        log.info("comprehensiveQueryAITower入参：{}", JSON.toJSONString(deviceAIVO));
        return towerDeviceService.comprehensiveQueryAI(deviceAIVO);
    }

    @DataScope(tenantColumn = "od.tenant_id", acceptColumn="od.accept_node", deviceColumn = "od.device_code",industryCode = "od.industry_code")
    @Override
    public Result<ComprehensiveQueryAIResult> comprehensiveQueryAITenant(DeviceAIVO deviceAIVO){
        ComprehensiveQueryAIResult cResult = new ComprehensiveQueryAIResult();
        long s1 = Calendar.getInstance().getTimeInMillis();
        this.buildParam(deviceAIVO);
        log.info("comprehensiveQueryAITenant入参：{}", JSON.toJSONString(deviceAIVO));
        // 获取传入的adCode（行政区划编码）
        String adCode = this.getAdCode(deviceAIVO);
        String location = deviceAIVO.getLocation();
        Integer radius = deviceAIVO.getRadius();
        // 如果adCode不为空，则根据adCode查询区域信息
        if(!org.apache.commons.lang3.StringUtils.isEmpty(adCode) && !org.apache.commons.lang3.StringUtils.isEmpty(location) && radius != null){
            deviceAIVO.setRadius(radius * 1000);
            try {
                GisPoiKeyWordQueryReq req = new GisPoiKeyWordQueryReq();
                req.setAdCode(adCode);
                req.setKeyWord(location);
                req.setPageNum(Constants.ONE_NUM);
                req.setPageSize(Constants.TWO_NUM);
                log.info("综合查询摄像机列表,调用poiKeyWordQuery接口入参：{}", JSON.toJSONString(req));
                Result<PoiKeyWordQueryRes> result = gisMapApi.poiKeyWordQuery(req);
                log.info("综合查询摄像机列表,调用poiKeyWordQuery接口返回：{}", JSON.toJSONString(result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && result.getData() != null) {
                    List<GisPoiKeyWordQueryRes> list = result.getData().getList();
                    if(com.alibaba.nacos.common.utils.CollectionUtils.isNotEmpty(list)){
                        GisPoiKeyWordQueryRes gisPoiKeyWordQueryRes = list.get(Constants.ZERO_NUM);
                        List<Pois> pois = gisPoiKeyWordQueryRes.getPois();
                        if(com.alibaba.nacos.common.utils.CollectionUtils.isNotEmpty(pois)){
                            Pois pois1 = pois.get(Constants.ZERO_NUM);
                            String location1 = pois1.getLocation();
                            String[] split = location1.split(Constants.COMMA);
                            deviceAIVO.setLongitude(split[0]);
                            deviceAIVO.setLatitude(split[1]);
                        }
                    }
                }
            }catch (Exception e){
                log.error("综合查询摄像机列表,调用poiKeyWordQuery接口异常：", e);
            }

            if(org.apache.commons.lang3.StringUtils.isEmpty(deviceAIVO.getLongitude()) || org.apache.commons.lang3.StringUtils.isEmpty(deviceAIVO.getLatitude())){
                // 根据adCode获取区域信息
                AreaInfoNew areaInfoByCode = deviceInfoMapper.getAreaInfoByCode(adCode);
                // 如果查询到区域信息，则设置经度和纬度到deviceAIVO对象中
                if(!ObjectUtils.isEmpty(areaInfoByCode)){
                    try {
                        String l = areaInfoByCode.getLongitude() + Constants.COMMA + areaInfoByCode.getLatitude();
                        String s = PositionUtil.convertLocation(l, true, false);
                        log.info("经纬度转换成功：" + s);
                        String[] split = s.split(Constants.COMMA);
                        deviceAIVO.setLongitude(split[0]);
                        deviceAIVO.setLatitude(split[1]);
                    } catch (Exception e) {
                        log.error("经纬度转换异常", e);
                    }
                }else {
                    // 如果未查询到区域信息，直接返回空的treeVOS列表
                    return Result.data(cResult);
                }
            }
        }

        int limitCount = 200;
        try{
            AjaxResult ajaxResult = systemFeignClient.getConfValue("compQueryLimitCount");
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                limitCount = Integer.parseInt(sysConfig.getConfigValue());
            }
        }catch (Exception e){
            log.error("获取综合查询摄像机列表限制数量异常", e);
        }
        log.info("limitCount="+limitCount);
        deviceAIVO.setLimitCount(limitCount);

        // 返回最终的设备树信息列表
        int total = deviceInfoMapper.comprehensiveQueryAICount(deviceAIVO);
        if(total == Constants.ZERO_NUM){
            return Result.data(cResult);
        }
        cResult.setTotal(total);

        List<DeviceTreeNewVO> deviceTreeNewVOS = deviceInfoMapper.comprehensiveQueryAI(deviceAIVO);
        deviceAIVO.setDeviceCodeList(deviceTreeNewVOS.stream().map(DeviceTreeNewVO::getDeviceCode).collect(Collectors.toList()));

        List<DeviceTreeNewVO> channelListNew = deviceInfoMapper.queryChannelListNew(deviceAIVO);
        Map<String, List<DeviceTreeNewVO>> devChanMap = channelListNew.stream().collect(Collectors.groupingBy(DeviceTreeNewVO::getDeviceCode));

        for (DeviceTreeNewVO deviceTreeNewVO : deviceTreeNewVOS) {
            deviceTreeNewVO.setChannelList(devChanMap.get(deviceTreeNewVO.getDeviceCode()));
        }
        cResult.setList(deviceTreeNewVOS);
        long s2 = Calendar.getInstance().getTimeInMillis();
        log.info("综合查询摄像机列表,耗时"+ (s2 - s1) +"毫秒！");
        return Result.data(cResult);
    }

    private String getAdCode(DeviceAIVO deviceAIVO){
        String adCode = null;
        if(Constants.ONE.equals(deviceAIVO.getAllArea())){
            adCode = "100000";
        }else if (StringUtils.isNotBlank(deviceAIVO.getCountyId())){
            adCode = deviceAIVO.getCountyId();
        }else if (StringUtils.isNotBlank(deviceAIVO.getCityId())){
            adCode = deviceAIVO.getCityId();
        }else if (StringUtils.isNotBlank(deviceAIVO.getProvinceId())){
            adCode = deviceAIVO.getProvinceId();
        }
        return adCode;
    }

    @Override
    @DataScope(tenantColumn="od.tenant_id",deviceColumn = "od.device_code", industryCode = "od.industry_code",alarmSourceVal = "4",acceptColumn="od.accept_node")
    public Result<List<ChannelInfoVO>> getAllChannelInfo(DeviceInfoVO deviceInfoVO) {
        return Result.data(channelInfoMapper.getAllChannelInfo(deviceInfoVO));
    }

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    public List<DeviceInfoVO> queryOrderDeviceByDevices(DeviceInfoVO deviceInfoVO) {
        List<DeviceInfoVO> list = Lists.newArrayList();
        //查询订购的摄像机客户
        List<DeviceInfoVO> deviceInfoVOList = deviceInfoMapper.queryOrderDeviceByDevices(deviceInfoVO);
        //查询订购的物联设备、雷达的用户
        List<DeviceInfoVO> iotVOList = deviceInfoMapper.queryOrderIotDeviceByDevices(deviceInfoVO);
        list.addAll(deviceInfoVOList);
        list.addAll(iotVOList);
        return list;
    }
}
