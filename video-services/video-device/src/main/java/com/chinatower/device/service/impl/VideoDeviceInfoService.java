package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.DeviceStatisticsVO;
import com.chinatower.analysis.api.entity.UavDeviceStatisticsVo;
import com.chinatower.bdm.api.LayerApi;
import com.chinatower.common.core.dto.bdm.LayerCountRes;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.device.dto.ComprehensiveDeviceStatisticsVO;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.mapper.ChannelInfoMapper;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmOrderParmaDto;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.apache.commons.collections4.MapUtils;
import com.chinatower.analysis.api.AnalysisExportAPI;
import com.chinatower.analysis.api.entity.EventNumVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.DeviceInfoDO;
import com.chinatower.device.dto.AnalysisDTO;
import com.chinatower.device.dto.ApiInfoDTO;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.mapper.AnalysisMapper;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.repository.UavDeviceService;
import com.chinatower.device.service.IRecorderDeviceService;
import com.chinatower.device.service.IVideoDeviceInfoService;
import com.chinatower.device.utils.ApiInfoUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.gis.api.GisAPI;
import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.order.api.IVideoOrderService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.QueryStorageDayVO;
import com.chinatower.platform.entity.order.VideoOrderReq;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.VideoConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chinatower.device.utils.PinYinUtils.convertHanziToPinyin;
import static com.chinatower.device.utils.PinYinUtils.getFirstLetterOfPinyin;

/**
 * @ClassName VideoDeviceInfoService
 * @Description 设备服务类
 * @Author chenmf
 * @Date 2022/1/18
 */
@Slf4j
@Service
public class VideoDeviceInfoService implements IVideoDeviceInfoService {

    @Resource
    private IVideoOrderService videoOrderService;

    @Resource
    private IDeviceInfoRepository deviceInfoRepository;

    @Autowired
    private ApiInfoUtils apiInfoUtils;

    @Resource
    SystemFeignClient systemFeignClient;

    @Resource
    private AnalysisMapper analysisMapper;

    @Autowired
    private GisAPI gisAPI;

    @Resource
    private NetConnectUtils connectUtils;

    @Resource
    ICasCadeInfoService iCasCadeInfoService;

    @Autowired
    private UavDeviceService uavDeviceService;

    @Autowired
    private IHornDeviceRepository hornDeviceRepository;

    @Autowired
    private IIotDeviceRepository iotDeviceRepository;

    @Autowired
    private IRecorderDeviceService recorderDeviceService;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Resource
    AnalysisExportAPI analysisExportAPI;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    GisMapApi gisMapApi;

    @Resource
    private ChannelInfoMapper channelInfoMapper;

    @Resource
    private LayerApi layerApi;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    /**
     * 功能描述: 视频树查询
     *
     * @param: deviceReqDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chensw
     * @date: 2022/1/27
     * @version: 2022.2.28
     */
    @ApiOperation("视频设备树查询")
    @Override
    public List<DeviceTreeVO>  getMonitorTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> monitorTree = deviceInfoRepository.getMonitorTree(deviceReqDTO);
        // 补充根节点
        String code = DeviceConstants.TOWER_ROOT_AREA;
        String name = DeviceConstants.TOWER_ROOT_AREA_NAME;
        SysArea sysArea = deviceInfoMapper.getAreaRootInfo();
        if(!ObjectUtils.isEmpty(sysArea)){
            code = sysArea.getAreaCode();
            name = sysArea.getAreaName();
        }
        DeviceTreeVO root = new DeviceTreeVO();
        root.setCode(code);
        root.setName(name);
        root.setList(monitorTree);
        root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
        root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
        root.setVideoNum(root.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if( monitorTree != null && monitorTree.size() > DeviceConstants.ZERO_NUM )
        {
        	rootTree.add(root);
        }
        return rootTree;
    }

    /**
     * 功能描述: 视频组织树查询
     *
     * @param: deviceReqDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chensw
     * @date: 2022/2/9
     * @version: 2022.2.28
     */
    @ApiOperation("视频组织树查询")
    @Override
    public List<DeviceTreeVO> getMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
        return deviceInfoRepository.getMonitorOrgTree(deviceReqDTO);
    }


    /**
     * 功能描述
     * 设备/通道/预置点/监控区域/算法关系推送
     *
     * @return
     * @author chenmf
     * @date 2022/2/9
     * @version
     */
    @ApiOperation("设备/通道/预置点/监控区域/算法关系推送")
    @Override
    public Result<JSONObject> synchDeviceAlgorithms(List<AlgorithmParamBodyVO> param) {
        Result<JSONObject> res = null;
        String tenantId = param.get(0).getTenantId();
        String deviceCode = param.get(0).getDeviceCode();
        String channelCode = param.get(0).getChannelCode();
        JSONArray jsonObject = JSON.parseArray(JSON.toJSONString(param, SerializerFeature.DisableCircularReferenceDetect));
        String inJsonStr = jsonObject.toJSONString();

        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_SYNCH_DEVICE_ALGORITHMS);
        apiInfoDTO.setDeviceCode(deviceCode);
        apiInfoDTO.setInJsonStr(inJsonStr);
        apiInfoDTO.setTowerCode("");
        apiInfoDTO.setOrderCode("");
        apiInfoDTO.setChannelCode(channelCode);
        apiInfoDTO.setUserCode("");
        apiInfoDTO.setTenantId(tenantId);

        try {
            NetConnectUtils.RequestParamter<ApiInfoDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(apiInfoDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNCH_DEVICE_ALGORITHMS);
            // 接口路径参数
            paramter.setPath(path);

            //属地化数据统一改造 add by wangyt 20240116
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(deviceCode);
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }

            // 调用统一网关接口
            log.info(VideoConstants.SYNCH_DEVICE_ALGORITHMS_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.SYNCH_DEVICE_ALGORITHMS_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.SYNCH_DEVICE_ALGORITHMS_ERROR +"===:{}", apiInfoDTO);
        }

        return res;
    }

    /**
     * 批量保存和更新设备信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    @ApiOperation("批量保存和更新设备信息")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchSaveOrUpdateDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList) {
        log.info("开始批量保存和更新设备信息");
        if (!CollectionUtils.isEmpty(deviceDTOList)) {
            //更新设备列表
            List<DeviceInfoDO> updateDeviceDTOList = new ArrayList<>();
            //新增设备列表
            List<DeviceInfoDO> addDeviceDTOList = new ArrayList<>();

            Iterator<SaveOrUpdateDeviceDTO> it = deviceDTOList.iterator();
            while (it.hasNext()) {
                SaveOrUpdateDeviceDTO deviceDTO = it.next();

                GisKeyWordQueryReq gisKeyWordQueryReq = new GisKeyWordQueryReq();
                gisKeyWordQueryReq.setLat(deviceDTO.getLatitude());
                gisKeyWordQueryReq.setLon(deviceDTO.getLongitude());
                gisKeyWordQueryReq.setMapType(DeviceConstants.MAP_TYPE_AMAP);
                Result result = gisAPI.codeToAdress(gisKeyWordQueryReq);
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                    Map<String, Object> resMap = (Map) result.getData();
                    deviceDTO.setProvinceId((String) resMap.get("provinceCode"));
                    deviceDTO.setCityId((String) resMap.get("cityCode"));
                    deviceDTO.setCountyId((String) resMap.get("adcode"));
                    deviceDTO.setTownCode((String) resMap.get("towncode"));
                }

                //查询设备是否存在
                DeviceInfoDO deviceInfoDO = deviceInfoRepository.getDeviceByCode(deviceDTO.getDeviceCode());
                if (null != deviceInfoDO) {
                    // 应用修改后，只能修改状态
                    if (DeviceConstants.YES_FLAG.equals(deviceInfoDO.getAppModFlag())) {
                        deviceInfoDO = new DeviceInfoDO();
                        deviceInfoDO.setDeviceStatus(deviceDTO.getDeviceStatus());
                        deviceInfoDO.setModifierDate(new Date());
                        updateDeviceDTOList.add(deviceInfoDO);
                    } else {
                        BeanUtils.copyProperties(deviceDTO, deviceInfoDO);
                        updateDeviceDTOList.add(deviceInfoDO);
                    }
                } else {
                    deviceInfoDO = new DeviceInfoDO();
                    BeanUtils.copyProperties(deviceDTO, deviceInfoDO);
                    deviceInfoDO.setCreatorDate(new Date());
                    deviceInfoDO.setModifierDate(new Date());
                    deviceInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                    addDeviceDTOList.add(deviceInfoDO);
                }

            }
            deviceInfoRepository.saveBatch(addDeviceDTOList);
            deviceInfoRepository.updateBatchById(updateDeviceDTOList);
            log.info("结束批量保存和更新设备信息");
        }
    }

    /**
     * 功能描述: 查询租户管理区域
     *
     * @param deviceReqDTO
     * @param: deviceReqDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/17
     * @version: 1.0
     */
    @ApiOperation("查询租户管理区域")
    @Override
    public List<DeviceTreeVO> getTenantMonitorLevel(DeviceReqDTO deviceReqDTO) {
        return deviceInfoRepository.getTenantMonitorLevel(deviceReqDTO);
    }


    /**
     * 根据行业版本编码查询设备信息
     *
     * @param pageDeviceVO 平台编码
     * @return 设备数据和分页结果
     * @author chenmf
     * @date 2022/1/27
     */
    @Override
    public List<DeviceByPlatformVO> pageDeviceByPlatform(PageDeviceVO pageDeviceVO) {
        PageMethod.startPage(pageDeviceVO.getPageNum(), pageDeviceVO.getPageSize());
        //查询用户订购订单列表
        List<DeviceByPlatformVO> list = deviceInfoRepository.pageDeviceByPlatform(pageDeviceVO);
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
    public String queryRecordPlanStorageDay(QueryStorageDayVO queryStorageDayVO) {

        LoginUser currentUser = SecurityUtils.getLoginUser();
        log.info("获取视频存储时长:" + SecurityUtils.getOperator());
        VideoOrderReq videoOrderReq = new VideoOrderReq();
        videoOrderReq.setDeviceCode(queryStorageDayVO.getDeviceCode());
        videoOrderReq.setChannelCode(queryStorageDayVO.getChannelCode());
        videoOrderReq.setTenantId(currentUser.getUser().getTenantId());
        videoOrderReq.setIndustryCode(currentUser.getUser().getIndustryCode());
        Result<VideoOrderResp> respResult = videoOrderService.getVideoOrderInfo(videoOrderReq);
        if (!ObjectUtils.isEmpty(respResult) && !ObjectUtils.isEmpty(respResult.getData())
                && !ObjectUtils.isEmpty(respResult.getCode())
                && !StringUtils.isEmpty(respResult.getData().getOrderCode())
                && ResultCode.SUCCESS.getCode() == respResult.getCode()) {
            queryStorageDayVO.setOrderCode(respResult.getData().getOrderCode());
        } else {
            // 返回错误 无权限使用
            log.info(DeviceConstants.DOES_NOT_RENT_THIS_DEVICE);
            throw new CustomException(DeviceConstants.DOES_NOT_RENT_THIS_DEVICE);
        }
        List<String> storageDayList = deviceInfoRepository.queryRecordPlanStorageDay(queryStorageDayVO);
        if (!CollectionUtils.isEmpty(storageDayList)) {
            return storageDayList.get(0);
        }
        return "0";
    }

    /**
     * 获取设备标签树
     *
     * @param deviceReqDTO
     * @return List<DeviceTreeVO>
     * @author trk
     * @date 2022/3/22
     */
    @Override
    public List<DeviceTreeVO> getMonitorLabelTree(DeviceReqDTO deviceReqDTO) {
        return deviceInfoRepository.getMonitorLabelTree(deviceReqDTO);
    }
    @Override
    public List<DeviceTreeVO> getMonitorLabelTreeNew(DeviceReqDTO deviceReqDTO) {
        return deviceInfoRepository.getMonitorLabelTreeNew(deviceReqDTO);
    }

    @DataScope(tenantColumn = "ord.tenant_id", deviceColumn = "ord.device_code",industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    @Override
    public List<DeviceInfoVO> linkageDevice(DeviceReqDTO deviceReqDTO) {
        return deviceInfoRepository.linkageDevice(deviceReqDTO);
    }

    @Override
    public DeviceDetailVO queryVideoDetail(DeviceTreeVO deviceTreeVO,LoginUser loginUser) {
    	DeviceDetailVO deviceDetailVO = null;
    	String channelCode = deviceTreeVO.getChannelCode();
    	if( channelCode != null && channelCode.length() > 0 )
    	{
    		// 通道编码有值则按设备和通道编码查详情
    		deviceDetailVO = deviceInfoRepository.queryVideoDetail(deviceTreeVO);
    	}
    	else
    	{
    		// 通道编码没值则只按设备编码查设备详情
    		deviceDetailVO = deviceInfoRepository.queryVideoDeviceDetail(deviceTreeVO);
    	}
        if (deviceDetailVO != null) {
            deviceDetailVO.setLablelList(deviceInfoRepository.getLableByDeviceCode(deviceTreeVO.getDeviceCode(),
                    loginUser.getUser().getTenantId()));
            deviceDetailVO.setCategoryName(getNameByKey("47",deviceDetailVO.getCategoryCode()));
            deviceDetailVO.setModelName(getNameByKey("103",deviceDetailVO.getModelCode()));
        }
        return deviceDetailVO;
    }

    @Override
    public Map<String, Object> queryDeviceStatistics(AnalysisDTO analysisDTO) {
        Map<String, Object> resultMap = new HashMap<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        analysisDTO.setTenantId(user.getTenantId());
        analysisDTO.setIndustryCode(user.getIndustryCode());

        Map devMap = analysisMapper.getStatisticsDevicesUser(analysisDTO);
        int devCountOnline = Integer.parseInt(devMap.get("devCountOnline").toString());
        int deviceCountTotal = Integer.parseInt(devMap.get("deviceCountTotal").toString());
        resultMap.put(DeviceConstants.DEV_COUNT_OFFLINE, Integer.parseInt(devMap.get("devCountOffline").toString()));
        resultMap.put(DeviceConstants.DEV_COUNT_ONLINE, devCountOnline);
        resultMap.put(DeviceConstants.DEVICE_COUNT_TOTAL, deviceCountTotal);

        Map channelMap = analysisMapper.getStatisticsChannelUser(analysisDTO);
        int channelCountOnline = Integer.parseInt(channelMap.get("channelCountOnline").toString());
        int channelCountTotal = Integer.parseInt(channelMap.get("channelCountTotal").toString());
        resultMap.put(DeviceConstants.CHANNEL_COUNT_OFFLINE, Integer.parseInt(channelMap.get("channelCountOffline").toString()));
        resultMap.put(DeviceConstants.CHANNEL_COUNT_ONLINE, channelCountOnline);
        resultMap.put(DeviceConstants.CHANNEL_COUNT_TOTAL, channelCountTotal);

        String online = "0";
        if (0 != channelCountTotal) {
            online = String.valueOf(String.format("%.2f", (double) channelCountOnline / channelCountTotal));
        }
        resultMap.put("onlinePercentage", online);

        String onlineDev = "0";
        if (0 != deviceCountTotal) {
            onlineDev = String.valueOf(String.format("%.2f", (double) devCountOnline / deviceCountTotal));
        }
        resultMap.put("onlinePercentageDev", onlineDev);
        return resultMap;
    }

    private String getNameByKey(String dictType,String key){
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        Result<List<SysDictDataResponse>> result = systemFeignClient.dictDataType(dictType);
        if (result.getData() != null) {
            Optional<SysDictDataResponse> optional = result.getData().stream().filter(sysDictDataResponse ->
                    key.equals(sysDictDataResponse.getDictValue())).findFirst();
            if (optional.isPresent()) {
                return optional.get().getDictLabel();
            }
        }
        return "";
    }


    @Override
    public DeviceInfoDO queryDeviceInfo(DeviceReqDTO deviceReqDTO) {
        return deviceInfoRepository.getDeviceByCode(deviceReqDTO.getDeviceCode());
    }

    /**
     * 设备综合查询，根据类型区分查询摄像机、无人机、雷达、物联设备、大喇叭
     * @param deviceComprehensiveReq
     * @return
     */
    @Override
    public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceComprehensive(DeviceComprehensiveReq deviceComprehensiveReq) {
        Result<PageInfo<DeviceComprehensiveVO>> result = null;
        switch(deviceComprehensiveReq.getQueryType()) {
            //摄像机
            case Constants.ONE:
                result = deviceInfoRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //雷达
            case Constants.TWO:
                deviceComprehensiveReq.setCategoryCode(Constants.ONE_NUM);
                result = iotDeviceRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //物联设备
            case Constants.THREE:
                deviceComprehensiveReq.setCategoryCode(Constants.TWO_NUM);
                result = iotDeviceRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //无人机
            case Constants.FOUR:
                result = uavDeviceService.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //大喇叭
            case Constants.FIVE:
                result = hornDeviceRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //执法记录仪
            case Constants.SIX:
                result = recorderDeviceService.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     *
     * 查询范围内距离最近的设备，以树的形式返回
     * @param deviceReqDTO
     * @return
     */
    @DataScope(tenantColumn = "ord.tenant_id", deviceColumn = "ord.device_code",industryCode = "ord.industry_code", acceptColumn = "ord.accept_node")
    @Override
    public List<DeviceTreeVO> linkageDeviceTree(DeviceReqDTO deviceReqDTO) {
        return deviceInfoRepository.linkageDeviceTree(deviceReqDTO);
    }

    @Override
    public List<DeviceIndDTO> getDeviceListByCodes(List<String> deviceCodes) {
        List<DeviceIndDTO> list = deviceInfoMapper.getDeviceListByCodes(deviceCodes);
        return list;
    }

    @Override
    public List<DeviceTreeVO> getMonitorList(DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> monitorTree = deviceInfoRepository.getMonitorTreeWithOutUserId(deviceReqDTO);
        // 补充根节点
        String code = DeviceConstants.TOWER_ROOT_AREA;
        String name = DeviceConstants.TOWER_ROOT_AREA_NAME;
        SysArea sysArea = deviceInfoMapper.getAreaRootInfo();
        if(!ObjectUtils.isEmpty(sysArea)){
            code = sysArea.getAreaCode();
            name = sysArea.getAreaName();
        }

        DeviceTreeVO root = new DeviceTreeVO();
        root.setCode(code);
        root.setName(name);
        root.setList(monitorTree);
        root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
        root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
        root.setVideoNum(root.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if( monitorTree != null && monitorTree.size() > DeviceConstants.ZERO_NUM )
        {
            rootTree.add(root);
        }
        return rootTree;
    }

    @Override
    @DataScope(industryCode = "a.industry_code",tenantColumn = "a.tenant_id",deviceColumn = "d.device_code",alarmSourceVal = "4",acceptColumn="a.accept_node")
    public Result<List<TreeSelect>> getDeviceTreeForestry(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        List<TreeSelect> tree = new ArrayList<>();
        List<DeviceTreeForestryVO> monitorTree = deviceInfoMapper.getDeviceTreeForestry(woodProcessingPlantDeivceVO);
        Set<DeviceTreeForestryVO> areaSet = new HashSet<>();
        List<String> areaCodes = monitorTree.stream().filter(Objects::nonNull)
                .flatMap(x -> Stream.of(com.chinatower.platform.common.utils.StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY)))
                .distinct().collect(Collectors.toList());
        List<DeviceTreeForestryVO> arealist = deviceInfoMapper.selectSysArea(areaCodes);
        List<DeviceTreeForestryVO> removeArealist = new ArrayList<>();
        for (DeviceTreeForestryVO deviceTreeForestryVO : arealist) {
            for (DeviceTreeForestryVO vo : monitorTree) {
                if(StringUtils.isEmpty(vo.getProvinceId())||StringUtils.isEmpty(vo.getCityId())||StringUtils.isEmpty(vo.getCountyId())){
                    removeArealist.add(vo);
                } else if (vo.getProvinceId().equals(deviceTreeForestryVO.getAreaCode()) || vo.getCityId().equals(deviceTreeForestryVO.getAreaCode()) || vo.getCountyId().equals(deviceTreeForestryVO.getAreaCode())) {
                    areaSet.add(deviceTreeForestryVO);
                }
            }
        }
        monitorTree.removeAll(removeArealist);
        monitorTree.addAll(areaSet);

        for (DeviceTreeForestryVO vo : monitorTree) {
            tree.add(new TreeSelect(vo.getAreaCode(), vo.getAreaName(), vo.getAreaParentCode(), vo.getAreaLevel()));
        }
        if(!CollectionUtils.isEmpty(tree)) {
            // 增加全国节点
            TreeSelect china = new TreeSelect("100000", "全国", "1");
            tree.add(0, china);
        }
        return Result.data(TreeUtils.listToTreeSelect(tree));
    }

    @Override
    public List<WoodProcessingPlantDeivceVO> getWoodProcessingPlantDeivceInfo(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        try{
            List<WoodProcessingPlantDeivceVO> list = deviceInfoMapper.getWoodProcessingPlantDeivceInfo(woodProcessingPlantDeivceVO);
            log.info("林业查询木材加工厂摄像机查询结果:{}", list);
            return list;
        } catch (Exception e) {
            log.error("林业查询木材加工厂摄像机详情报错:{}", e);
            throw e;
        }
    }

    @Override
    public List<WoodProcessingPlantDeivceVO> getAlarmNumByDevice(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        try {
            StatisticsVO statisticsVO = new StatisticsVO();
            statisticsVO.setSelectDeviceCodeList(woodProcessingPlantDeivceVO.getDeviceCodes());
            statisticsVO.setProvinceId(woodProcessingPlantDeivceVO.getProvinceId());
            statisticsVO.setCityId(woodProcessingPlantDeivceVO.getCityId());
            statisticsVO.setCountyId(woodProcessingPlantDeivceVO.getCountryId());
            statisticsVO.setTownId(woodProcessingPlantDeivceVO.getTownId());
            statisticsVO.setDeviceCode(woodProcessingPlantDeivceVO.getDeviceCode());
            statisticsVO.setQueryStartTime(woodProcessingPlantDeivceVO.getQueryStartTime());
            statisticsVO.setQueryEndTime(woodProcessingPlantDeivceVO.getQueryEndTime());
            log.info("木材加工厂请求设备告警数量入参：{}", JSONObject.toJSONString(statisticsVO));
            TableDataInfo tableDataInfo = analysisExportAPI.getDeviceNumAll(statisticsVO);
            // String json = "{\"code\":200,\"rows\":[{\"todayAll\":0,\"todayProcess\":0,\"todayComplete\":0,\"totalProcess\":0,\"totalComplete\":0,\"penddingJudged\":0,\"pendingDispatched\":0,\"peddingDisposal\":0,\"pendingApprove\":0,\"penddingLeaderApprove\":0,\"overtime\":0,\"judgeNum\":12,\"dispatchNum\":0,\"handleNum\":0,\"approveNum\":0,\"orderCompleteNum\":0,\"allNum\":12,\"realNum\":0,\"repeatCompleteNum\":0,\"falseNum\":0,\"processNum\":12,\"completionRate\":\"0\",\"accuracys\":\"0.0000\",\"completionRates\":\"0.0000\",\"suppressNum\":0,\"acceptanceNum\":0,\"avgNum\":4,\"deviceName\":\"舟曲县白龙江香椿沟村段非法采砂监控点\",\"deviceCode\":\"46900200001327000005\"}],\"total\":1}";
            // TableDataInfo tableDataInfo = JSON.parseObject (json, TableDataInfo.class);
            log.info("木材加工厂请求设备告警数量出参：{}", JSONObject.toJSONString(tableDataInfo));
            List<WoodProcessingPlantDeivceVO> woodProcessingPlantDeivceVOS = new ArrayList<>();
            if(!CollectionUtils.isEmpty(tableDataInfo.getRows())) {
                for (Object row : tableDataInfo.getRows()) {
                    try {
                        WoodProcessingPlantDeivceVO element = new WoodProcessingPlantDeivceVO();
                        String jsonString = JSON.toJSONString(row);
                        EventNumVO eventNumVO = JSON.parseObject(jsonString, EventNumVO.class);
                        element.setCount(eventNumVO.getAllNum());
                        element.setDeviceCode(eventNumVO.getDeviceCode());
                        element.setDeviceName(eventNumVO.getDeviceName());
                        woodProcessingPlantDeivceVOS.add(element);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return woodProcessingPlantDeivceVOS;
        } catch (Exception e) {
            log.error("根据设备编号取设备告警数量接口报错：{}", e);
        }
        return null;
    }

    @Override
    @DataScope(tenantColumn="t.tenant_id", deviceColumn = "t.device_code",  industryCode = "t.industry_code", acceptColumn="t.accept_node")
    public PageInfo<WoodProcessingPlantDeivceVO> getWoodProcessingPlantDeivceInfoPage(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        if(woodProcessingPlantDeivceVO.getPageNum() != null && woodProcessingPlantDeivceVO.getPageSize() != null) {
            PageHelper.startPage(woodProcessingPlantDeivceVO.getPageNum(), woodProcessingPlantDeivceVO.getPageSize());
        }
        try{
            List<WoodProcessingPlantDeivceVO> list = deviceInfoMapper.getWoodProcessingPlantDeivceInfoPage(woodProcessingPlantDeivceVO);
            log.info("林业查询木材加工厂列表查询结果:{}", list);
            PageInfo<WoodProcessingPlantDeivceVO> pageInfo = new PageInfo<>(list);
            return pageInfo;
        } catch (Exception e) {
            log.error("林业查询木材加工厂列表查询结果:{}", e);
            throw e;
        }
    }

    @Override
    public List<String> queryDeviceCodeList(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        deviceReqDTO.setTenantId(currentUser.getUser().getTenantId());
        deviceReqDTO.setIndustryCode(currentUser.getUser().getIndustryCode());
        List<String> list = deviceInfoMapper.queryDeviceCodeList(deviceReqDTO);
        return list;
    }

    @Override
    @DataScope(tenantColumn="m.tenant_id", deviceColumn = "m.device_code",  industryCode = "m.industry_code", acceptColumn="m.accept_node")
    public List<DeviceTreeVO> getMonitorTreeStepOne(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        //省、市、区、乡镇、通道状态以及对应设备数量；同一省市区乡镇不同状态对应两条数据
        long startMillis1 = Calendar.getInstance().getTimeInMillis();
        List<DeviceTreeVO> areaList = new ArrayList<>();
        Map<String, Integer> stringIntegerMap = new HashMap<>();
        Integer deviceOrChannel = deviceReqDTO.getDeviceOrChannel();
        if (Constants.ZERO.equals(deviceReqDTO.getIsMonitorFlag())) {
            areaList = deviceInfoMapper.getIsMonitorDeviceTree(deviceReqDTO);
            if (Objects.nonNull(deviceOrChannel) && deviceOrChannel.intValue() == Constants.ONE_NUM) {
                List<DeviceTreeVO> monitorDeviceGroup = deviceInfoMapper.getIsMonitorDeviceGroup(deviceReqDTO);
                stringIntegerMap = this.buildDeviceVideoMap(monitorDeviceGroup);
            }
        } else {
            areaList = deviceInfoMapper.getDeviceTree(deviceReqDTO);
            if (Objects.nonNull(deviceOrChannel) && deviceOrChannel.intValue() == Constants.ONE_NUM) {
                List<DeviceTreeVO> deviceGroup = deviceInfoMapper.getDeviceGroup(deviceReqDTO);
                stringIntegerMap = this.buildDeviceVideoMap(deviceGroup);
            }
        }
        log.info("getMonitorTree getDeviceTree sql结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis1) +"毫秒！");

        //将同一省市区乡镇不同状态的数据数量合并后，得到 chanMap
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        Map<String,DeviceTreeVO> chanMap = assembleMap(areaList);
        log.info("getMonitorTree assembleMap结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis2) +"毫秒！");

        //查询省市区乡镇对应的收藏数量
        long startMillis3 = Calendar.getInstance().getTimeInMillis();
        List<DeviceTreeVO> collList = deviceInfoMapper.getDevicCollInfo(deviceReqDTO);
        log.info("getMonitorTree getDeviceCollInfo sql结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis3) +"毫秒！");

        //将 collList 转化为 collMap
        Map<String,DeviceTreeVO> collMap = new HashMap<>();
        for (DeviceTreeVO devVO : collList) {
            String code = "";
            if (!org.apache.commons.lang3.StringUtils.isEmpty(devVO.getTownCode())) {
                code = devVO.getProvinceId() + Constants.UNDER_LINE + devVO.getCityId() + Constants.UNDER_LINE + devVO.getCountyId() + Constants.UNDER_LINE + devVO.getTownCode();
            } else {
                code = devVO.getProvinceId() + Constants.UNDER_LINE + devVO.getCityId() + Constants.UNDER_LINE + devVO.getCountyId();
            }
            collMap.put(code,devVO);
        }

        // chanMap 与 collMap 比较，更新收藏数量字段
        List<DeviceTreeVO> flagList = new ArrayList<>();
        for (String key : chanMap.keySet()) {
            if (collMap.keySet().contains(key)) {
                chanMap.get(key).setIsMonitorNum(collMap.get(key).getIsMonitorNum());
            }
            flagList.add(chanMap.get(key));
        }

        //flagList 集合按照省市区乡镇排序
        flagList = flagList.stream().sorted(Comparator.comparing(DeviceTreeVO::getProvinceId).
                        thenComparing(DeviceTreeVO::getCityId).thenComparing(DeviceTreeVO::getCountyId).
                        thenComparing(DeviceTreeVO::getTownCode, Comparator.nullsFirst(String::compareTo))).
                collect(Collectors.toList());

        //将 flagList 转化成树形结构 treeList
        long startMillis4 = Calendar.getInstance().getTimeInMillis();
        List<DeviceTreeVO> treeList = getTreeList(flagList,false,stringIntegerMap);
        log.info("getMonitorTree getTreeList结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis4) +"毫秒！");

        //获取行政区划全部数据
        long startMillis5 = Calendar.getInstance().getTimeInMillis();
        Map<Object,Object> areaMap = getRedisMap();
        //将树形集合 treeList 中设备数量、在线数量、收藏数量做累加；并比较收藏数量与通道数量是否相等，相等则为收藏状态；
        dealTreeList(areaMap, treeList);
        log.info("getMonitorTree dealTreeList结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis5) +"毫秒！");
        return treeList;
    }

    private Map<String,DeviceTreeVO> assembleMap(List<DeviceTreeVO> areaList) {
        Map<String,DeviceTreeVO> chanMap = new HashMap<>();
        for (DeviceTreeVO devVO : areaList) {
            String code = "";
            if (!org.apache.commons.lang3.StringUtils.isEmpty(devVO.getTownCode())) {
                //乡镇层级的合并
                code = devVO.getProvinceId() + Constants.UNDER_LINE + devVO.getCityId() + Constants.UNDER_LINE + devVO.getCountyId() + Constants.UNDER_LINE + devVO.getTownCode();
            } else {
                //乡镇为空，区县层级的合并
                code = devVO.getProvinceId() + Constants.UNDER_LINE + devVO.getCityId() + Constants.UNDER_LINE + devVO.getCountyId();
            }
            DeviceTreeVO deviceVO = chanMap.get(code);
            if (ObjectUtils.isEmpty(deviceVO)) {
                deviceVO = devVO;
            }
            deviceVO.setNum(deviceVO.getNum() + devVO.getChannelStatusCount());
            if (Constants.ZERO.equals(devVO.getChannelStatus())) {
                deviceVO.setOnlineNum(deviceVO.getOnlineNum() + devVO.getChannelStatusCount());
            }
            chanMap.put(code,deviceVO);
        }
        return chanMap;
    }

    /**
     * 功能描述: 将List集合转化成树形结构 treeList
     * @param: needDevice 属地化部署查询设备参数
     * @return:
     * @auther: zhaozj
     * @date: 2022/11/02
     * @version: release_v1.5.10
     * @requestID: 中国铁塔_铁塔视联平台__视频管理
     */
    private List<DeviceTreeVO> getTreeList(List<DeviceTreeVO> flagList, boolean needDevice, Map<String, Integer> stringIntegerMap) {
        List<DeviceTreeVO> treeList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        flagList.forEach(vo -> {
            String province = vo.getProvinceId();
            Integer provinceIndex = map.get(province);
            // provinceIndex 不是 null，说明已在list里
            if (ObjectUtils.isEmpty(provinceIndex)) {
                DeviceTreeVO tree = new DeviceTreeVO();
                tree.setCode(province);
                tree.setLevel(Constants.ONE);
                tree.setUniqueCode(province);
                tree.setProvinceId(province);
                treeList.add(tree);
                provinceIndex = treeList.size() - Constants.ONE_NUM;
                map.put(province, provinceIndex);
            }

            String city = vo.getCityId();
            String cityKey = province + Constants.UNDER_LINE + city;
            Integer cityIndex = map.get(cityKey);
            if (ObjectUtils.isEmpty(cityIndex)) {
                List<DeviceTreeVO> list = treeList.get(provinceIndex).getList();
                DeviceTreeVO tree = new DeviceTreeVO();
                tree.setCode(city);
                tree.setLevel(Constants.TWO);
                tree.setParentCode(vo.getProvinceId());
                tree.setUniqueCode(cityKey);
                tree.setProvinceId(province);
                tree.setCityId(city);
                list.add(tree);
                cityIndex = list.size() - Constants.ONE_NUM;
                map.put(cityKey, cityIndex);
            }

            String county = vo.getCountyId();
            String countyKey = province + Constants.UNDER_LINE + city + Constants.UNDER_LINE + county;
            Integer countyIndex = map.get(countyKey);
            if (ObjectUtils.isEmpty(countyIndex)) {
                List<DeviceTreeVO> list = treeList.get(provinceIndex).getList().get(cityIndex).getList();
                DeviceTreeVO tree = new DeviceTreeVO();
                tree.setCode(county);
                tree.setLevel(Constants.THREE);
                tree.setParentCode(vo.getCityId());
                if (org.apache.commons.lang3.StringUtils.isEmpty(vo.getTownCode())) {
                    tree.setNum(vo.getNum());
                    tree.setOnlineNum(vo.getOnlineNum());
                    tree.setIsMonitorNum(vo.getIsMonitorNum());
                    tree.setVideoNum(stringIntegerMap.getOrDefault(countyKey,0));
                }
                tree.setUniqueCode(countyKey);
                tree.setProvinceId(province);
                tree.setCityId(city);
                tree.setCountyId(county);
                list.add(tree);
                countyIndex = list.size() - Constants.ONE_NUM;
                map.put(countyKey, countyIndex);
            } else if (org.apache.commons.lang3.StringUtils.isEmpty(vo.getTownCode())) {
                DeviceTreeVO deviceTreeVO = treeList.get(provinceIndex).getList().get(cityIndex).getList().get(countyIndex);
                if (deviceTreeVO != null) {
                    deviceTreeVO.setNum(vo.getNum());
                    deviceTreeVO.setOnlineNum(vo.getOnlineNum());
                    deviceTreeVO.setIsMonitorNum(vo.getIsMonitorNum());
                    deviceTreeVO.setVideoNum(stringIntegerMap.getOrDefault(countyKey,0));
                }
            }
            Integer townCodeIndex = null;
            if (!org.apache.commons.lang3.StringUtils.isEmpty(vo.getTownCode())) {
                String townCode = vo.getTownCode();
                String townCodeKey = province + Constants.UNDER_LINE + city + Constants.UNDER_LINE + county + Constants.UNDER_LINE + townCode;
                townCodeIndex = map.get(townCodeKey);
                if (ObjectUtils.isEmpty(townCodeIndex)) {
                    List<DeviceTreeVO> list = treeList.get(provinceIndex).getList().get(cityIndex).getList().get(countyIndex).getList();
                    DeviceTreeVO tree = new DeviceTreeVO();
                    tree.setCode(townCode);
                    tree.setLevel(Constants.FOUR);
                    tree.setNum(vo.getNum());
                    tree.setOnlineNum(vo.getOnlineNum());
                    tree.setIsMonitorNum(vo.getIsMonitorNum());
                    tree.setParentCode(vo.getCountyId());
                    tree.setUniqueCode(townCodeKey);
                    tree.setProvinceId(province);
                    tree.setCityId(city);
                    tree.setCountyId(county);
                    tree.setTownCode(townCode);
                    tree.setVideoNum(stringIntegerMap.getOrDefault(townCodeKey,0));
                    list.add(tree);
                    townCodeIndex = list.size() - Constants.ONE_NUM;
                    map.put(townCodeKey, townCodeIndex);
                }
            }
            if(needDevice) {
                //乡镇编码是空的
                DeviceTreeVO deviceTreeVO ;
                String deviceLevel = Constants.FOUR;
                if(ObjectUtils.isEmpty(townCodeIndex)){
                    //挂在区下
                    deviceTreeVO = treeList.get(provinceIndex).getList().get(cityIndex).getList().get(countyIndex);
                }else{
                    deviceLevel = Constants.FIVE;
                    //挂在乡下
                    deviceTreeVO = treeList.get(provinceIndex).getList().get(cityIndex).getList().get(countyIndex).getList().get(townCodeIndex);
                }
                if (deviceTreeVO != null) {
                    DeviceTreeVO deviceVO = new DeviceTreeVO();
                    String deviceCode = vo.getDeviceCode();
                    deviceVO.setDeviceCode(deviceCode);
                    deviceVO.setDeviceName(vo.getDeviceName());
                    deviceVO.setName(vo.getDeviceName());
                    deviceVO.setCode(deviceCode);
                    deviceVO.setProvinceId(vo.getProvinceId());
                    deviceVO.setCityId(vo.getCityId());
                    deviceVO.setCountyId(vo.getCountyId());
                    deviceVO.setTownCode(vo.getTownCode());
                    deviceVO.setLevel(deviceLevel);
                    deviceVO.setUniqueCode(deviceCode);
                    deviceTreeVO.getList().add(deviceVO);
                }
            }
        });

        return treeList;
    }

    /**
     * 功能描述: 获取行政区划全部数据
     * @param:
     * @return:
     * @auther: zhaozj
     * @date: 2022/11/02
     * @version: release_v1.5.10
     * @requestID: 中国铁塔_铁塔视联平台__视频管理
     */
    private Map getRedisMap() {
        Result<Map<String, String>> result = systemFeignClient.getAreaNameCaffeineMap(new ArrayList<>());
        if(MapUtils.isNotEmpty(result.getData())){
            return result.getData();
        }
        return Maps.newHashMap();
    }

    /**
     * 功能描述: 将树形集合 treeList 中设备数量、在线数量、收藏数量做累加；并比较收藏数量与通道数量是否相等，相等则为收藏状态；
     * @param:
     * @return:
     * @auther: zhaozj
     * @date: 2022/11/02
     * @version: release_v1.5.10
     * @requestID: 中国铁塔_铁塔视联平台__视频管理
     */
    private List<DeviceTreeVO> dealTreeList(Map<Object,Object> areaMap, List<DeviceTreeVO> treeList) {
        treeList.forEach(pro -> {
            pro.getList().forEach(city -> {
                city.getList().forEach(country -> {
                    for (DeviceTreeVO town : country.getList()) {
                        if (town.getNum() == town.getIsMonitorNum()) {
                            town.setIsMonitor(Constants.ONE);
                        }
                        town.setName(NullProcessUtil.nvlToString(areaMap.get(town.getCode()),""));
                    }
                    country.setOnlineNum(country.getOnlineNum() + country.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    country.setNum(country.getNum() + country.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    country.setVideoNum(country.getVideoNum() + country.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                    //子节点都收藏标记上级节点为收藏
                    int monitorNum = country.getIsMonitorNum() + country.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                    country.setIsMonitorNum(monitorNum);
                    if (monitorNum == country.getNum()) {
                        country.setIsMonitor(Constants.ONE);
                    }
                    country.setName(NullProcessUtil.nvlToString(areaMap.get(country.getCode()),""));
                });
                city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                city.setVideoNum(city.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
                int monitorNum = city.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                //子节点都收藏标记上级节点为收藏
                city.setIsMonitorNum(monitorNum);
                if (city.getIsMonitorNum() == city.getNum()) {
                    city.setIsMonitor(Constants.ONE);
                }
                city.setName(NullProcessUtil.nvlToString(areaMap.get(city.getCode()),""));
            });
            pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
            pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
            pro.setVideoNum(pro.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
            int monitorNum = pro.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
            //子节点都收藏标记上级节点为收藏
            pro.setIsMonitorNum(monitorNum);
            if (pro.getIsMonitorNum() == pro.getNum()) {
                pro.setIsMonitor(Constants.ONE);
            }
            pro.setName(NullProcessUtil.nvlToString(areaMap.get(pro.getCode()),""));
        });

        return treeList;
    }

    @Override
    @DataScope(tenantColumn="ord.tenant_id", deviceColumn = "ord.device_code",  industryCode = "ord.industry_code", acceptColumn="ord.accept_node")
    public List<DeviceTreeVO> queryDevicesByAreaParam(@RequestBody DeviceTreeVO deviceTreeVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceTreeVO.setUserId(user.getUserId());
        }
        long startMillis1 = Calendar.getInstance().getTimeInMillis();
        List<DeviceTreeVO> list = deviceInfoMapper.queryDevicesByAreaParam(deviceTreeVO);
        if (Objects.nonNull(deviceTreeVO.getIsDeviceIcon()) && deviceTreeVO.getIsDeviceIcon().intValue() == Constants.ONE_NUM) {
            list.forEach(treeVO -> {
                treeVO.setList(null);
                treeVO.setNum(null);
                treeVO.setOnlineNum(null);
                treeVO.setVideoNum(null);
                treeVO.setTagCollect(null);
            });
            return list;
        }
        log.info("queryDevicesByAreaParam sql结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis1) +"毫秒！");

        //摄像机图标加密
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        this.setImageUrl(list);
        log.info("queryDevicesByAreaParam setImageUrl结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis2) +"毫秒！");

        list.forEach(dev -> {
            Long onlineNumLong = dev.getList().stream().filter(deviceTree -> Constants.ZERO.equals(deviceTree.getStatus())).count();
            dev.setOnlineNum(onlineNumLong.intValue());
            Long numLong = dev.getList().stream().count();
            dev.setNum(numLong.intValue());
            int monitorNum = dev.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
            dev.setIsMonitorNum(monitorNum);
            //子节点都收藏标记上级节点为收藏
            if (dev.getNum() == monitorNum) {
                dev.setIsMonitor(Constants.ONE);
            }
        });

        return list;
    }

    private void setImageUrl(List<DeviceTreeVO> treeList){
        Set<String> fileUrls = new HashSet<>();
        for (DeviceTreeVO dev : treeList) {
            if(StringUtils.isNotBlank(dev.getImageUrl())){
                fileUrls.add(dev.getImageUrl());
            }
        }
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        for (DeviceTreeVO dev : treeList) {
            dev.setImageUrl(result.get(dev.getImageUrl()));
        }
    }

    @Override
    @DataScope(tenantColumn="vod.tenant_id", deviceColumn = "vod.device_code",  industryCode = "vod.industry_code", acceptColumn="vod.accept_node")
    public List<DeviceTreeVO> getLabelTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        long startTime = System.currentTimeMillis();
        log.info("{}--获取设备标签树接口请求报文------{}", startTime, JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> resList = new ArrayList<>();
        List<DeviceTreeVO> list = deviceInfoMapper.getLabelTree(deviceReqDTO);
        list.forEach(item -> {
            item.setLabelTypeNameOrd(getFirstLetterOfPinyin(convertHanziToPinyin(item.getLabelTypeName(),true)));
            item.getList().forEach(e->{
                e.setLabelNameOrd(getFirstLetterOfPinyin(convertHanziToPinyin(e.getLabelName(),true)));
            });
            item.setList(item.getList().stream().sorted(Comparator.comparing(DeviceTreeVO::getLabelNameOrd)).collect(Collectors.toList()));
        });
        list = list.stream()
                .sorted(Comparator.comparing(DeviceTreeVO::getLabelTypeNameOrd))
                .collect(Collectors.toList());
        long sqlEndTime = System.currentTimeMillis();
        log.info("{}--请求sql完成,耗时:{}", sqlEndTime, sqlEndTime - startTime);
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
                        if (Constants.ONE.equals(child.getIsMonitor())) {
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
                        item.setIsMonitor(Constants.ONE);
                    }
                    item.setList(childList);
                });
                label.setList(null);
                label.setOnlineNum(labelNumMap.get(DeviceNorthIntfConstants.LABEL_NUM));
                label.setNum(labelNumMap.get(DeviceNorthIntfConstants.NUM));
                label.setIsMonitorNum(labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM));
                //子节点都收藏标记上级节点为收藏
                if (label.getNum().equals(labelNumMap.get(DeviceNorthIntfConstants.IS_MONITOR_NUM))) {
                    label.setIsMonitor(Constants.ONE);
                }
            });

            long typeNum = labelTypeList.stream().filter(item -> Objects.equals(Constants.ZERO, item.getStatus())).count();
            labelType.setOnlineNum((int) typeNum);
            labelType.setNum(labelTypeList.size());
            long typeIsMonitorNum = labelTypeList.stream().filter(item -> Constants.ONE.equals(item.getIsMonitor())).count();
            labelType.setIsMonitorNum((int) typeIsMonitorNum);
            //子节点都收藏标记上级节点为收藏
            if (labelType.getNum() == labelType.getIsMonitorNum()) {
                labelType.setIsMonitor(Constants.ONE);
            }

            allLabelList.addAll(labelTypeList);
        });
        if (!CollectionUtils.isEmpty(list)) {
            DeviceTreeVO resLabelVO = new DeviceTreeVO();
            resLabelVO.setCode(IndustryConstants.LABEL_ROOT_CODE);
            resLabelVO.setName(IndustryConstants.LABEL_ROOT_NAME);
            resLabelVO.setList(list);
            long typeNum = allLabelList.stream().filter(item -> Objects.equals(Constants.ZERO, item.getStatus())).count();
            resLabelVO.setOnlineNum((int) typeNum);
            resLabelVO.setNum(allLabelList.size());
            resLabelVO.setFirst(1);
            resList.add(resLabelVO);
        }
        long dataEndTime = System.currentTimeMillis();
        log.info("{}--处理数据完成,耗时:{}", dataEndTime, dataEndTime - sqlEndTime);
        return resList;
    }

    @Override
    @DataScope(tenantColumn="vod.tenant_id", deviceColumn = "vod.device_code",  industryCode = "vod.industry_code", acceptColumn="vod.accept_node")
    public List<DeviceTreeVO> queryDevicesByLabelParam(DeviceTreeVO deviceTreeVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceTreeVO.setUserId(user.getUserId());
        }
        log.info("查询标签树具体设备---请求报文------" + JSON.toJSONString(deviceTreeVO));
        List<DeviceTreeVO> list = deviceInfoMapper.queryDevicesByLabelParam(deviceTreeVO);
        log.info("查询标签树具体设备---返回报文------" + JSON.toJSONString(list));
        //摄像机图标加密
        this.setImageUrl(list);

        list.forEach(dev -> {
            Long onlineNumLong = dev.getList().stream().filter(deviceTree -> Constants.ZERO.equals(deviceTree.getStatus())).count();
            dev.setOnlineNum(onlineNumLong.intValue());
            Long numLong = dev.getList().stream().count();
            dev.setNum(numLong.intValue());
            int monitorNum = dev.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
            dev.setIsMonitorNum(monitorNum);
            //子节点都收藏标记上级节点为收藏
            if (dev.getNum() == monitorNum) {
                dev.setIsMonitor(Constants.ONE);
            }
        });
        return list;
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    @Override
    public List<IndustryDeviceTree> queryDevicesForGlobal(DevicesForGlobalReq devicesForGlobalReq) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(!StringUtils.isEmpty(devicesForGlobalReq.getTownCode())){
            List<String> areaCodes = new ArrayList<>();
            areaCodes.add(devicesForGlobalReq.getTownCode());
            List<BasicDeviceDTO> basicDeviceDTOS = deviceInfoMapper.queryAreaInfo(areaCodes);
            if (!CollectionUtils.isEmpty(basicDeviceDTOS)) {
                devicesForGlobalReq.setCountyId(basicDeviceDTOS.get(Constants.ZERO_NUM).getAreaParentCode());
            }
        }
        devicesForGlobalReq.setTenantId(user.getTenantId());
        devicesForGlobalReq.setIndustryCode(user.getIndustryCode());
        devicesForGlobalReq.setPlatformCode(user.getPlatformCode());
        List<IndustryDeviceTree> industryDeviceTrees = deviceInfoMapper.queryDevicesForGlobal(devicesForGlobalReq);

        if(!CollectionUtils.isEmpty(industryDeviceTrees)){
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
            alarmOrderParmaDto.setTenantId(user.getTenantId());
            alarmOrderParmaDto.setIndustryCode(user.getIndustryCode());
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
        }
        return industryDeviceTrees;
    }

    @Override
    public List<DevicesForGlobalPoiRes> queryDevicesForGlobalPoi(DevicesForGlobalPoiReq devicesForGlobalPoiReq) {
        log.info("综合查询摄像机列表,调用queryDevicesForGlobalPoi接口入参：{}", JSON.toJSONString(devicesForGlobalPoiReq));
        devicesForGlobalPoiReq.setPageNum(Constants.ONE_NUM);
        devicesForGlobalPoiReq.setPageSize(Constants.FIVE_NUM);
        List<DevicesForGlobalPoiRes> resultList = new ArrayList<>();
        String keyWord = devicesForGlobalPoiReq.getKeyWord();
        if(!StringUtils.isEmpty(keyWord)){
            String regex = "^[0-9,，. ]+$";
            if (keyWord.matches(regex)) {
                log.info("doPoiContraryGeocodingQuery");
                devicesForGlobalPoiReq.setKeyWord(keyWord.replace("，", ",").replace(" ", ""));
                resultList.addAll(this.doPoiContraryGeocodingQuery(devicesForGlobalPoiReq));
            }else {
                log.info("doPoiKeyWordQuery");
                resultList.addAll(this.doPoiKeyWordQuery(devicesForGlobalPoiReq));
            }
        }
        devicesForGlobalPoiReq.setPageSize(Constants.TEN_NUM - resultList.size());
        for (int i = 2; i <= 7; i++) {
            resultList.addAll(this.doDeviceQuery(devicesForGlobalPoiReq, i));
            if (resultList.size() >= 10) {
                resultList = resultList.subList(0, 10);
                break;
            }
        }
        return resultList;
    }

    /**
     * 逆地理编码接口，查询前5条
     * @param devicesForGlobalPoiReq
     * @return
     */
    private List<DevicesForGlobalPoiRes> doPoiKeyWordQuery(DevicesForGlobalPoiReq devicesForGlobalPoiReq){
        List<DevicesForGlobalPoiRes> resultList = new ArrayList<>();
        GisPoiKeyWordQueryReq req = new GisPoiKeyWordQueryReq();
        BeanUtils.copyProperties(devicesForGlobalPoiReq, req);
        log.info("综合查询摄像机列表,调用poiKeyWordQuery接口入参：{}", JSON.toJSONString(req));
        Result<PoiKeyWordQueryRes> result = gisMapApi.poiKeyWordQuery(req);
        log.info("综合查询摄像机列表,调用poiKeyWordQuery接口返回：{}", JSON.toJSONString(result));
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && result.getData() != null) {
            List<GisPoiKeyWordQueryRes> list = result.getData().getList();
            if(!CollectionUtils.isEmpty(list)){
                log.info("searchType=1,size="+list.size());
                for (GisPoiKeyWordQueryRes gisPoiKeyWordQueryRes : list) {
                    Region region = gisPoiKeyWordQueryRes.getRegion();
                    List<Pois> pois = gisPoiKeyWordQueryRes.getPois();
                    if(!ObjectUtils.isEmpty(region) && !CollectionUtils.isEmpty(pois)){
                        DevicesForGlobalPoiRes devicesForGlobalPoiRes = new DevicesForGlobalPoiRes();
                        Pois pois1 = pois.get(Constants.ZERO_NUM);
                        String location1 = pois1.getLocation();
                        String[] split = location1.split(Constants.COMMA);
                        devicesForGlobalPoiRes.setSearchType(Constants.ONE);
                        devicesForGlobalPoiRes.setLongitude(split[0]);
                        devicesForGlobalPoiRes.setLatitude(split[1]);
                        devicesForGlobalPoiRes.setCode(pois1.getNid());
                        devicesForGlobalPoiRes.setName(pois1.getName());
                        devicesForGlobalPoiRes.setAddress(
                                region.getProvince() +
                                region.getCity() +
                                region.getCounty() +
                                region.getTown() +
                                pois1.getAddress());
                        resultList.add(devicesForGlobalPoiRes);
                    }
                }
            }
        }
        return resultList;
    }

    public List<DevicesForGlobalPoiRes> doPoiContraryGeocodingQuery(DevicesForGlobalPoiReq devicesForGlobalPoiReq) {
        List<DevicesForGlobalPoiRes> resultList = new ArrayList<>();
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(devicesForGlobalPoiReq.getKeyWord());
        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
        log.info("gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                if (data.getRegion() != null && data.getPois() != null) {
                    DevicesForGlobalPoiRes devicesForGlobalPoiRes = new DevicesForGlobalPoiRes();
                    String location = data.getLocation();
                    List<Pois> poisList = data.getPois();
                    Pois pois = poisList.get(Constants.ZERO_NUM);
                    String[] split = location.split(Constants.COMMA);
                    devicesForGlobalPoiRes.setSearchType(Constants.ONE);
                    devicesForGlobalPoiRes.setLongitude(split[0]);
                    devicesForGlobalPoiRes.setLatitude(split[1]);
                    devicesForGlobalPoiRes.setCode(pois.getNid());
                    devicesForGlobalPoiRes.setName(pois.getName());
                    devicesForGlobalPoiRes.setAddress(data.getAddress());
                    resultList.add(devicesForGlobalPoiRes);
                }
            } catch (Exception e) {
                log.error("gisMapApi poiContraryGeocodingQuery error, e:", e);
            }
        }
        return resultList;
    }


    private List<DevicesForGlobalPoiRes> doDeviceQuery(DevicesForGlobalPoiReq devicesForGlobalPoiReq, int type){
        List<DevicesForGlobalPoiRes> resultList = new ArrayList<>();
        DeviceComprehensiveReq deviceComprehensiveReq = new DeviceComprehensiveReq();
        BeanUtils.copyProperties(devicesForGlobalPoiReq, deviceComprehensiveReq);

        String keyWord = devicesForGlobalPoiReq.getKeyWord();
        if(!StringUtils.isEmpty(keyWord)){
            if (keyWord.contains(Constants.COMMA)) {
                String[] split = keyWord.split(Constants.COMMA);
                if(split.length == 2){
                    deviceComprehensiveReq.setLongitude(split[0]);
                    deviceComprehensiveReq.setLatitude(split[1]);
                    deviceComprehensiveReq.setKeyWord(null);
                }else{
                    return resultList;
                }
            }
        }

        log.info("综合查询摄像机列表,doDeviceQuery入参：{}", JSON.toJSONString(deviceComprehensiveReq));
        Result<PageInfo<DeviceComprehensiveVO>> result = null;
        String searchType = String.valueOf(type);
        switch(searchType) {
            //摄像机
            case Constants.TWO:
                result = deviceInfoRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //无人机
            case Constants.THREE:
                result = uavDeviceService.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //物联设备
            case Constants.FOUR:
                deviceComprehensiveReq.setCategoryCode(Constants.TWO_NUM);
                result = iotDeviceRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //雷达
            case Constants.FIVE:
                deviceComprehensiveReq.setCategoryCode(Constants.ONE_NUM);
                result = iotDeviceRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //大喇叭
            case Constants.SIX:
                result = hornDeviceRepository.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            //执法记录仪
            case Constants.SEVEN:
                result = recorderDeviceService.queryDeviceListByKeyWord(deviceComprehensiveReq);
                break;
            default:
                break;
        }

        if(result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
            PageInfo<DeviceComprehensiveVO> data = result.getData();
            List<DeviceComprehensiveVO> list = data.getList();
            log.info("searchType="+searchType+",size="+list.size());
            for (DeviceComprehensiveVO deviceComprehensiveVO : list) {
                DevicesForGlobalPoiRes devicesForGlobalPoiRes = new DevicesForGlobalPoiRes();
                devicesForGlobalPoiRes.setSearchType(searchType);
                devicesForGlobalPoiRes.setLongitude(deviceComprehensiveVO.getLongitude());
                devicesForGlobalPoiRes.setLatitude(deviceComprehensiveVO.getLatitude());
                devicesForGlobalPoiRes.setCode(deviceComprehensiveVO.getDeviceCode());
                devicesForGlobalPoiRes.setName(deviceComprehensiveVO.getDeviceName());
                devicesForGlobalPoiRes.setAddress(deviceComprehensiveVO.getLocation());
                resultList.add(devicesForGlobalPoiRes);
            }
        }

        return resultList;
    }

    @Override
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    public List<IndustryDeviceTree> queryDevicesForGlobalSurround(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq) {
        return deviceInfoMapper.queryDevicesForGlobalSurround(devicesForGlobalSurroundReq);
    }

    @Override
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    public List<IndustryDeviceTree> queryDevicesForGlobalSurroundList(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq) {
        if(devicesForGlobalSurroundReq.getPageNum() != null && devicesForGlobalSurroundReq.getPageSize() != null) {
            PageHelper.startPage(devicesForGlobalSurroundReq.getPageNum(), devicesForGlobalSurroundReq.getPageSize());
        }
        return deviceInfoMapper.queryDevicesForGlobalSurroundList(devicesForGlobalSurroundReq);
    }

    @Override
    public List<DevicesForGlobalPoiRes> queryHisForGlobalPoi(){
        SysUser tokenUser = SecurityUtils.getLoginUser().getUser();
        String key = this.getPoiSearchKey(tokenUser);
        return RedisUtil.getList(key, DevicesForGlobalPoiRes.class, RedisDBType.PRIMARY);
    }

    @Override
    public String addHisForGlobalPoi(DevicesForGlobalPoiRes devicesForGlobalPoiRes){
        SysUser tokenUser = SecurityUtils.getLoginUser().getUser();
        String key = this.getPoiSearchKey(tokenUser);
        List<DevicesForGlobalPoiRes> list = RedisUtil.getList(key, DevicesForGlobalPoiRes.class, RedisDBType.PRIMARY);
        if(CollectionUtils.isEmpty(list)){
            list = new ArrayList<>();
        }else {
            String code = devicesForGlobalPoiRes.getCode();
            if (list.stream().anyMatch(forGlobalPoiRes -> code.equals(forGlobalPoiRes.getCode()))) {
                return ResultCode.SUCCESS.getMsg();
            }
            if(list.size() >= 10){
                list = list.subList(0, 9);
            }
        }
        devicesForGlobalPoiRes.setUuid(IdUtils.fastSimpleUUID());
        list.add(Constants.ZERO_NUM, devicesForGlobalPoiRes);
        RedisUtil.set(key, list, RedisDBType.PRIMARY);
        return ResultCode.SUCCESS.getMsg();
    }

    @Override
    public String delHisForGlobalPoi(DevicesForGlobalPoiRes devicesForGlobalPoiRes){
        SysUser tokenUser = SecurityUtils.getLoginUser().getUser();
        String key = this.getPoiSearchKey(tokenUser);
        String isClear = devicesForGlobalPoiRes.getIsClear();
        if(Constants.ONE.equals(isClear)){
            RedisUtil.delete(key, RedisDBType.PRIMARY);
        }else{
            List<DevicesForGlobalPoiRes> list = RedisUtil.getList(key, DevicesForGlobalPoiRes.class, RedisDBType.PRIMARY);
            if(!CollectionUtils.isEmpty(list)){
                list.removeIf(device -> device.getUuid().equals(devicesForGlobalPoiRes.getUuid()));
                RedisUtil.set(key, list, RedisDBType.PRIMARY);
            }
        }
        return ResultCode.SUCCESS.getMsg();
    }

    private String getPoiSearchKey(SysUser tokenUser){
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
        String userId = user.getUserId();
        String industryCode = tokenUser.getIndustryCode();
        return "poi_search_" + userId + "_" +industryCode;
    }

    @Override
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    public List<IndustryDeviceTree> queryDevicesForGlobalUav(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq){
        if(devicesForGlobalSurroundReq.getPageNum() != null && devicesForGlobalSurroundReq.getPageSize() != null) {
            PageHelper.startPage(devicesForGlobalSurroundReq.getPageNum(), devicesForGlobalSurroundReq.getPageSize());
        }
        List<IndustryDeviceTree> industryDeviceTrees = deviceInfoMapper.queryDevicesForGlobalUav(devicesForGlobalSurroundReq);

        if(!CollectionUtils.isEmpty(industryDeviceTrees)){
            List<String> deviceCodeList = industryDeviceTrees.stream().map(IndustryDeviceTree::getDeviceCode).collect(Collectors.toList());

            SysUser user = SecurityUtils.getLoginUser().getUser();
            DeviceInfoVO qryDevOrg = new DeviceInfoVO();
            qryDevOrg.setDeviceCodeList(deviceCodeList);
            qryDevOrg.setTenantId(user.getTenantId());
            qryDevOrg.setIndustryCode(user.getIndustryCode());
            // 补充通道数据
            List<ChannelInfoVO> channelList = channelInfoMapper.listChannelInfoForCustDev(qryDevOrg);
            if (!CollectionUtils.isEmpty(channelList)) {
                Map<String, List<ChannelInfoVO>> channelMap = channelList.stream()
                        .collect(Collectors.groupingBy(ChannelInfoVO::getParentDeviceCode));

                for (IndustryDeviceTree deviceInfo : industryDeviceTrees) {
                    List<ChannelInfoVO> channels = channelMap.getOrDefault(deviceInfo.getDeviceCode(), Collections.emptyList());
                    List<IndustryDeviceTree> channelInfoVOs = channels.stream()
                            .map(channelInfoVO -> {
                                IndustryDeviceTree industryDeviceTree = new IndustryDeviceTree();
                                BeanUtils.copyProperties(channelInfoVO, industryDeviceTree);
                                return industryDeviceTree;
                            })
                            .collect(Collectors.toList());

                    deviceInfo.setList(channelInfoVOs);
                }
            }
        }
        return industryDeviceTrees;
    }

    /**
     * 设备统计接口、摄像机、卫星影像、机场总数、无人机执行任务中的数量
     */
    @Override
    public ComprehensiveDeviceStatisticsVO queryComprehensiveDeviceStatistics() {

        //卫星影像非矢量图层数量
        int satelliteLayerNum = 0;
        //摄像机数量
        int deviceNum = 0;
        //在线摄像机数量
        int onlineDeviceNum = 0;
        //机场数量
        int uavNestNum = 0;
        //无人机数量
        int uavNum = 0;

        //获取卫星影像非矢量图层
        LayerQueryDTO dto = new LayerQueryDTO();
        dto.setCategory(Constants.TWO);

        //查询非矢量图层
        Result<List<LayerCountRes>> result = layerApi.getLayerCount(dto);
        List<LayerCountRes> list = result.getData();
        if (!CollectionUtils.isEmpty(list)) {
            satelliteLayerNum = list.get(0).getLayerCount();
        }

        //查询执行任务的无人机数量
        UavDeviceDTO param = new UavDeviceDTO();
        int taskingUavNum = uavDeviceService.getFlyingUavDeviceCount(param);

        //查询摄像机数量
        AnalysisVO analysisVO = new AnalysisVO();
        Result<DeviceStatisticsVO> deviceStatistics = analysisExportAPI.getDeviceStatistics(analysisVO);
        DeviceStatisticsVO deviceStatisticsVO = deviceStatistics.getData();
        if (Objects.nonNull(deviceStatisticsVO)) {
            deviceNum = deviceStatisticsVO.getDeviceCountTotal();
            onlineDeviceNum = deviceStatisticsVO.getDevCountOnline();
        }

        //查询机场数量
        AnalysisVO uavDeviceParam = new AnalysisVO();
        Result<UavDeviceStatisticsVo> uavDeviceStatistics = analysisExportAPI.getUavDeviceStatistics(uavDeviceParam);
        UavDeviceStatisticsVo uavDeviceStatisticsVo = uavDeviceStatistics.getData();
        if (Objects.nonNull(uavDeviceStatisticsVo)) {
            uavNestNum = uavDeviceStatisticsVo.getUavNestDeviceCount();
            uavNum = uavDeviceStatisticsVo.getUavDeviceCount();
        }

        //查询机场数量
        //综合统计返回体
        ComprehensiveDeviceStatisticsVO comprehensiveDeviceStatisticsVO = new ComprehensiveDeviceStatisticsVO();
        //摄像机数量
        comprehensiveDeviceStatisticsVO.setDeviceNum(deviceNum);
        //在线摄像机数量
        comprehensiveDeviceStatisticsVO.setOnlineDeviceNum(onlineDeviceNum);
        //卫星影像非矢量图层数量
        comprehensiveDeviceStatisticsVO.setSatelliteLayerNum(satelliteLayerNum);
        //机场数量
        comprehensiveDeviceStatisticsVO.setUavNestNum(uavNestNum);
        //无人机数量
        comprehensiveDeviceStatisticsVO.setUavNum(uavNum);
        //执行任务中的无人机数量
        comprehensiveDeviceStatisticsVO.setTaskingUavNum(taskingUavNum);
        return comprehensiveDeviceStatisticsVO;
    }

    public Map<String, Integer> buildDeviceVideoMap(List<DeviceTreeVO> monitorDeviceGroup) {
        // 定义Map，Key为自定义的复合键，Value为videoNum累加值
        Map<String, Integer> videoNumMap = new HashMap<>();

        // 遍历分组数据，按复合键聚合videoNum
        for (DeviceTreeVO deviceVO : monitorDeviceGroup) {
            // 1. 构建当前数据的复合键
            String key = deviceVO.getProvinceId() + Constants.UNDER_LINE +
                    deviceVO.getCityId() + Constants.UNDER_LINE +
                    deviceVO.getCountyId();
            if(StringUtils.isNotBlank(deviceVO.getTownCode())){
                key = key + Constants.UNDER_LINE + deviceVO.getTownCode();
            }
            // 2. 累加videoNum（若Key已存在，取原有值+当前值；否则取当前值）
            Integer currentVideoNum = deviceVO.getVideoNum() == null ? 0 : deviceVO.getVideoNum();
            videoNumMap.put(key, videoNumMap.getOrDefault(key, 0) + currentVideoNum);
        }
        return videoNumMap;
    }

    @Override
    @DataScope(tenantColumn="dd.tenant_id",deptColumn="dd.dept_id",industryCode="dd.industry_code",acceptColumn="vod.accept_node", deviceColumn = "dd.device_code")
    public List<DeviceTreeVO> getMonitorDeptTreeStepOne(DeviceReqDTO deviceReqDTO){
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info("getMonitorDeptTreeStepOne开始时间"+ startMillis);
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceReqDTO.setUserId(user.getUserId());
        }
        log.info("查询摄像头组织树---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        List<DeviceTreeVO> monitorList;
        if (Constants.ZERO.equals(deviceReqDTO.getIsMonitorFlag())) {
            monitorList = deviceInfoMapper.getIsMonitorDeviceDeptTree(deviceReqDTO);
        } else {
            monitorList = deviceInfoMapper.getMonitorDeptTreeStepOne(deviceReqDTO);
        }
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        log.info("查询摄像头组织树结束,耗时"+ (startMillis2 - startMillis) +"毫秒！");

        //查询省市区乡镇对应的收藏数量
        List<DeviceTreeVO> collList = deviceInfoMapper.getDeviceCollInfoByDept(deviceReqDTO);
        monitorList.forEach(deviceTreeVO ->
                collList.stream()
                        .filter(treeVO -> deviceTreeVO.getCode().equalsIgnoreCase(treeVO.getCode()))
                        .findFirst()
                        .ifPresent(treeVO -> deviceTreeVO.setIsMonitorNum(treeVO.getIsMonitorNum()))
        );

        long startMillis3 = Calendar.getInstance().getTimeInMillis();
        log.info("getDeviceCollInfoByDept结束,耗时"+ (startMillis3 - startMillis2) +"毫秒！");

        List<DeviceTreeVO> treeList = this.formatOrgTreeList(monitorList);
        long startMillis4 = Calendar.getInstance().getTimeInMillis();
        log.info("formatOrgTreeList结束,耗时"+ (startMillis4 - startMillis3) +"毫秒！");
        log.info("getMonitorDeptTreeStepOne结束,整体耗时"+ (startMillis4 - startMillis) +"毫秒！");
        return treeList;
    }

    /**
     * 格式化设备组织树
     * @param monitorList
     * @return
     */
    private List<DeviceTreeVO> formatOrgTreeList(List<DeviceTreeVO> monitorList) {
        List<DeviceTreeVO> treeList;
        Set<String> orgList = new HashSet<>();
        //获取有设备权限的所有组织编码
        monitorList.forEach(e -> orgList.add(e.getCode()));
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
            int num = 0;
            int onlineNum = 0;
            int isMonitorNum = 0;
            for (DeviceTreeVO deviceTreeVO : resList) {
                num = num + deviceTreeVO.getNum();
                onlineNum = onlineNum + deviceTreeVO.getOnlineNum();
                isMonitorNum = isMonitorNum + deviceTreeVO.getIsMonitorNum();
            }
            sortVO.setNum(num);
            sortVO.setOnlineNum(onlineNum);
            sortVO.setIsMonitorNum(isMonitorNum);
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

    @Override
    @DataScope(tenantColumn="ord.tenant_id", deviceColumn = "ord.device_code",  industryCode = "ord.industry_code", acceptColumn="ord.accept_node")
    public List<DeviceTreeVO> getMonitorDeptTreeStepTwo(@RequestBody DeviceTreeVO deviceTreeVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceTreeVO.setUserId(user.getUserId());
        }
        long startMillis1 = Calendar.getInstance().getTimeInMillis();
        List<DeviceTreeVO> list = deviceInfoMapper.getMonitorDeptTreeStepTwo(deviceTreeVO);
        log.info("getMonitorDeptTreeStepTwo sql结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis1) +"毫秒！");

        //摄像机图标加密
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        this.setImageUrl(list);
        log.info("getMonitorDeptTreeStepTwo setImageUrl结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis2) +"毫秒！");

        list.forEach(dev -> {
            Long onlineNumLong = dev.getList().stream().filter(deviceTree -> Constants.ZERO.equals(deviceTree.getStatus())).count();
            dev.setOnlineNum(onlineNumLong.intValue());
            Long numLong = dev.getList().stream().count();
            dev.setNum(numLong.intValue());
            int monitorNum = dev.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
            dev.setIsMonitorNum(monitorNum);
            //子节点都收藏标记上级节点为收藏
            if (dev.getNum() == monitorNum) {
                dev.setIsMonitor(Constants.ONE);
            }
        });

        return list;
    }

    @Override
    @DataScope(tenantColumn="ord.tenant_id", deviceColumn = "ord.device_code",  industryCode = "ord.industry_code", acceptColumn="ord.accept_node")
    public List<DeviceTreeVO> getMonitorTreeStepTwoPage(@RequestBody DeviceTreeVO deviceTreeVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceTreeVO.setUserId(user.getUserId());
            deviceTreeVO.setTenantId(tokenUser.getTenantId());
            deviceTreeVO.setIndustryCode(tokenUser.getIndustryCode());
        }

        int pageNum = 1;
        int pageSize = 100;
        if (StringUtils.isNotNull(deviceTreeVO.getPageNum()) && StringUtils.isNotNull(deviceTreeVO.getPageSize())) {
            pageNum = deviceTreeVO.getPageNum();
            pageSize = deviceTreeVO.getPageSize();
        }
        PageHelper.startPage(pageNum, pageSize);
        long startMillis1 = Calendar.getInstance().getTimeInMillis();
        List<DeviceTreeVO> list = deviceInfoMapper.getMonitorTreeStepTwoPage(deviceTreeVO);
        log.info("getMonitorTreeStepTwoPage sql结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis1) +"毫秒！");

        if(!CollectionUtils.isEmpty(list)){
            long startMillis3 = Calendar.getInstance().getTimeInMillis();
            List<String> deviceCodeList = list.stream().map(DeviceTreeVO::getDeviceCode).collect(Collectors.toList());
            List<DeviceTreeVO> channelList = deviceInfoMapper.getChannelListForTwoPage(deviceCodeList, deviceTreeVO.getUserId());
            log.info("getChannelListForTwoPage sql结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis3) +"毫秒！");

            long startMillis4 = Calendar.getInstance().getTimeInMillis();
            List<DeviceTreeVO> devChaAlias = deviceInfoMapper.getDevChaAlias(channelList, deviceTreeVO.getTenantId(), deviceTreeVO.getIndustryCode());
            log.info("getDevChaAlias sql结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis4) +"毫秒！");

            // 转换数据
            long startMillis5 = Calendar.getInstance().getTimeInMillis();
            Map<String, String>[] result = convertToMaps(devChaAlias);
            Map<String, String> deviceMap = result[0];
            Map<String, String> channelMap = result[1];

            // 处理别名
            fillDeviceName(Constants.ONE, list, deviceMap);
            fillDeviceName(Constants.TWO, channelList, deviceMap);
            fillChannelName(channelList, channelMap);

            //整合设备通道集合
            this.associateDeviceAndChannel(list, channelList);
            log.info("处理数据结构结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis5) +"毫秒！");

            //摄像机图标加密
            long startMillis2 = Calendar.getInstance().getTimeInMillis();
            this.setImageUrl(list);
            log.info("setImageUrl结束,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis2) +"毫秒！");

            list.forEach(dev -> {
                Long onlineNumLong = dev.getList().stream().filter(deviceTree -> Constants.ZERO.equals(deviceTree.getStatus())).count();
                dev.setOnlineNum(onlineNumLong.intValue());
                Long numLong = dev.getList().stream().count();
                dev.setNum(numLong.intValue());
                int monitorNum = dev.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum();
                dev.setIsMonitorNum(monitorNum);
                //子节点都收藏标记上级节点为收藏
                if (dev.getNum() == monitorNum) {
                    dev.setIsMonitor(Constants.ONE);
                }
            });
        }
        return list;
    }

    // 处理设备名称映射
    private void fillDeviceName(String type, List<DeviceTreeVO> targetList, Map<String, String> deviceMap) {
        if (CollectionUtils.isEmpty(targetList) || CollectionUtils.isEmpty(deviceMap)) {
            return; // 快速返回，避免无效循环
        }

        for (DeviceTreeVO treeVO : targetList) {
            String deviceCode = treeVO.getDeviceCode();
            // 只调用一次get，提升性能
            String deviceName = deviceMap.get(deviceCode);
            if (deviceName != null) {
                // 根据不同列表类型设置不同字段
                if (Constants.ONE.equals(type)) { // 主设备列表：设置3个字段
                    treeVO.setName(deviceName);
                    treeVO.setDeviceName(deviceName);
                    treeVO.setDeviceAlias(deviceName);
                } else if (Constants.TWO.equals(type)) { // 通道列表：仅设置deviceName
                    treeVO.setDeviceName(deviceName);
                }
            }
        }
    }

    // 处理通道名称映射
    private void fillChannelName(List<DeviceTreeVO> channelList, Map<String, String> channelMap) {
        if (CollectionUtils.isEmpty(channelList) || CollectionUtils.isEmpty(channelMap)) {
            return; // 快速返回
        }

        for (DeviceTreeVO treeVO : channelList) {
            String channelCode = treeVO.getChannelCode();
            String channelName = channelMap.get(channelCode);
            if (channelName != null) {
                treeVO.setName(channelName);
                treeVO.setChannelName(channelName);
            }
        }
    }

    /**
     * 核心方法：将通道列表按deviceCode关联到设备列表的子级中
     * @param devlist 设备列表（主列表）
     * @param channelList 通道列表（需要关联的子列表）
     */
    public void associateDeviceAndChannel(List<DeviceTreeVO> devlist, List<DeviceTreeVO> channelList) {
        // 步骤1：判空处理（避免空指针）
        if (devlist == null || devlist.isEmpty() || channelList == null || channelList.isEmpty()) {
            return;
        }

        // 步骤2：将通道列表按deviceCode分组，生成Map（key: deviceCode，value: 该设备下的所有通道）
        Map<String, List<DeviceTreeVO>> channelGroupMap = new HashMap<>();
        for (DeviceTreeVO channel : channelList) {
            String deviceCode = channel.getDeviceCode();
            // 跳过deviceCode为空的通道（无效数据）
            if (deviceCode == null || deviceCode.trim().isEmpty()) {
                continue;
            }
            // 按deviceCode分组，不存在则创建空列表
            channelGroupMap.computeIfAbsent(deviceCode.trim(), k -> new ArrayList<>()).add(channel);
        }

        // 步骤3：遍历设备列表，将对应通道赋值到子级children属性
        for (DeviceTreeVO device : devlist) {
            String deviceCode = device.getDeviceCode();
            if (deviceCode == null || deviceCode.trim().isEmpty()) {
                continue; // 跳过无编码的无效设备
            }
            // 获取当前设备对应的通道列表，赋值到children属性
            List<DeviceTreeVO> childChannels = channelGroupMap.get(deviceCode.trim());
            if (childChannels != null && !childChannels.isEmpty()) {
                device.getList().addAll(childChannels); // 批量添加通道
            }
        }
    }

    /**
     * 将 DeviceTreeVO 列表转换为两个 Map
     * @param devChaAlias 原始数据列表
     * @return 返回包含两个 Map 的数组：[设备Map, 通道Map]
     */
    private Map<String, String>[] convertToMaps(List<DeviceTreeVO> devChaAlias) {
        // 1. 初始化两个 Map
        Map<String, String> deviceMap = new HashMap<>();  // key: deviceCode, value: deviceName
        Map<String, String> channelMap = new HashMap<>(); // key: channelCode, value: channelName

        // 2. 判空处理（避免空指针异常）
        if (devChaAlias == null || devChaAlias.isEmpty()) {
            return new Map[]{deviceMap, channelMap};
        }

        // 3. 遍历列表，填充 Map
        for (DeviceTreeVO vo : devChaAlias) {
            // 设备Map：仅当 deviceCode 非空时才放入（避免key为null）
            if (vo.getDeviceCode() != null && !vo.getDeviceCode().trim().isEmpty()) {
                deviceMap.put(vo.getDeviceCode().trim(), vo.getDeviceName());
            }
            // 通道Map：仅当 channelCode 非空时才放入
            if (vo.getChannelCode() != null && !vo.getChannelCode().trim().isEmpty()) {
                channelMap.put(vo.getChannelCode().trim(), vo.getChannelName());
            }
        }

        // 4. 返回两个 Map
        return new Map[]{deviceMap, channelMap};
    }
}
