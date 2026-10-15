package com.chinatower.device.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.algorithm.mapper.AlgorithmInfoMapper;
import com.chinatower.algorithm.mapper.SysSceneAlarmMapper;
import com.chinatower.cascade.api.CascadeConfigAPI;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.AssembleUrlUtil;
import com.chinatower.common.core.utils.GsonUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.dataobject.AlgorithmConfigDO;
import com.chinatower.device.dataobject.VideoPresetDO;
import com.chinatower.device.dto.AlgorithmAppDTO;
import com.chinatower.device.dto.AlgorithmRankDto;
import com.chinatower.device.dto.AlgorithmRankVo;
import com.chinatower.device.dto.FunctionAlgorthmVO;
import com.chinatower.device.entity.dto.ExtendJson;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.ExtendJsonAlgo;
import com.chinatower.device.mapper.AlgorithmConfigMapper;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.mapper.PolygonPatchAlgoAreaMapper;
import com.chinatower.device.repository.IAlgorithmConfigRepository;
import com.chinatower.device.service.IAlgorithmConfigInfoService;
import com.chinatower.device.service.IPolygonPatchService;
import com.chinatower.device.utils.ApiInfoUtils;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.DevicePointTurn;
import com.chinatower.event.entity.dto.DevicePointVo;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.industry.dto.DeviceIndReqDTO;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import com.chinatower.order.api.IVideoOrderService;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.order.CustomerDTO;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import com.chinatower.platform.model.dto.PTZPointDto;
import com.chinatower.platform.model.dto.PercentPointDto;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.IVideoPresetService;
import com.chinatower.video.api.VideoQueryAPI;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.PresetData;
import com.chinatower.video.api.entity.VideoBaseReq;
import com.chinatower.video.api.entity.VideoConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * @ClassName AlgorithmConfigService
 * @Description
 * @Author chenshaowu
 * @Date 2022/1/27
 **/
@Service
@Slf4j
public class AlgorithmConfigInfoService extends ServiceImpl<AlgorithmConfigMapper, AlgorithmConfigDO> implements IAlgorithmConfigInfoService {

    @Resource
    IAlgorithmConfigRepository algorithmConfigRepository;
    @Resource
    AlgorithmConfigMapper algorithmConfigMapper;

    @Resource
    AlgorithmInfoMapper algorithmInfoMapper;
    @Resource
    IVideoPresetService iVideoPresetServicei;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    ApiInfoUtils apiInfoUtils;
    @Resource
    IVideoOrderService iVideoOrderService;

    @Resource
    VideoQueryAPI videoQueryAPI;
    @Resource
    AlgorithmGiveConfigService algorithmGiveConfigService;

    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    PolygonPatchAlgoAreaMapper polygonMapper;
    @Autowired
    IPolygonPatchService polygonPatchService;
    @Autowired
    AlgoPolygonAreaService algoPolygonAreaService;
    @Resource
    private NetConnectUtils connectUtils;
    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    @Resource
    private ICasCadeInfoService iCasCadeInfoService;

    @Resource
    private ITradeWarningOrderService iTradeWarningOrderService;

    public static final Integer PRESET_TYPE = 2;

    public static final String LINE_SPLIT = "-line-";
    public static final String DIRECTION_SPLIT = "-direction-";
    @Resource
    private CascadeConfigAPI cascadeConfigAPI;

    public static final String MODE_TYPE = "modetype";


    public static final String POINTS = "points";

    @Resource
    private SysSceneAlarmMapper sysSceneAlarmMapper;

    /**
     * 列表分页查询算法配置
     *
     * @param algorithmConfigDTO
     * @return
     */
    @Override
    public List<AlgorithmListVO> getAlgorithmConfigList(AlgorithmConfigDTO algorithmConfigDTO) {
        return algorithmConfigRepository.getAlgorithmConfigList(algorithmConfigDTO);
    }

    /**
     * 列表分页查询算法配置
     *
     * @param algorithmConfigDTO
     * @return
     */
    @Override
    public List<AlgorithmListVO> getAlgorithmConfigList2(AlgorithmConfigDTO algorithmConfigDTO) {
        log.info("getAlgorithmConfigList2请求报文:" + JSONObject.toJSONString(algorithmConfigDTO));
        List<AlgorithmListVO> list = algorithmConfigRepository.getAlgorithmConfigList2(algorithmConfigDTO);
        log.info("getAlgorithmConfigList2返回报文:" + JSONObject.toJSONString(list));
        LambdaQueryWrapper<AlgorithmConfigDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlgorithmConfigDO::getDeleteFlag, 0);
        if (list != null && !list.isEmpty()) {
            Map<String, AlgorithmListVO> listMap = new HashMap<>();
            list.forEach(item -> {
                item.setPositions(new ArrayList<>());
                String key = item.getDeviceCode() + DeviceSyncConstants.HORIZON_LINE + item.getChannelCode();
                listMap.put(key, item);
                //添加算法数量
                wrapper.eq(AlgorithmConfigDO::getTenantId, item.getTenantId())
                        .eq(AlgorithmConfigDO::getDeviceCode, item.getDeviceCode())
                        .eq(AlgorithmConfigDO::getChannelCode, item.getChannelCode())
                        .eq(AlgorithmConfigDO::getIndustryCode, item.getIndustryCode());
                item.setAlgorithmCount(algorithmConfigRepository.count(wrapper));
            });
            List<AlgorithmConfigVO> configList = algorithmConfigMapper.getAlgorithmConfigRow2(algorithmConfigDTO.getTenantId(), algorithmConfigDTO.getIndustryCode(), list);
            if (configList != null && !configList.isEmpty()) {
                Map<String, AlgorithmConfigVO> configMap = new HashMap<>();
                configList.forEach(item -> {
                    item.setAlgorithms(new ArrayList<>());
                    String key = item.getDeviceCode() + DeviceSyncConstants.HORIZON_LINE + item.getChannelCode();
                    listMap.get(key).getPositions().add(item);
                    configMap.put(item.getConfigId(), item);
                });
                List<AlgorithmAreaVO> areas = algorithmConfigMapper.getAlgorithmAreaRow2(algorithmConfigDTO.getIndustryCode(), configList);
                if (areas != null && !areas.isEmpty()) {
                    areas.forEach(item -> configMap.get(item.getConfigId()).getAlgorithms().add(item));
                }

            }
        }
        return list;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result save(AlgorithmListVO algorithmListVO) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        //漏洞改造 20221118 铁塔用户算法配置新增按钮置灰，无法进行操作
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            return Result.fail("铁塔账号无法进行算法配置新增操作");
        }
        String industryCode = sysUser.getIndustryCode();
        algorithmListVO.setIndustryCode(industryCode);
        log.info("算法配置新增入参:" + GsonUtils.toJSONString(algorithmListVO));
        List<AlgorithmConfigVO> pos = algorithmListVO.getPositions();
        List<com.chinatower.video.api.entity.AlgorithmConfigVO> newPos = new ArrayList<>();
        BeanUtil.copyProperties(pos, newPos);
        List<AlgorithmParamPresetVO> ptzMapList = new ArrayList<>();
        //set设备编码
        newPos.forEach(e -> e.setDeviceCode(algorithmListVO.getDeviceCode()));
        BeanUtil.copyProperties(iVideoPresetServicei.selectPTZByIdList(newPos), ptzMapList);

        for (AlgorithmConfigVO algo : pos) {
            if (algo.getPosType() == AlgorithmParamBodyVO.POSTYPE_PRESET) {
                AlgorithmParamPresetVO algo2 = new AlgorithmParamPresetVO();
                algo2.setPresetId(algo.getPresetId());
                algo2.setPresetName(algo.getPresetName());
                if (!CollectionUtils.isEmpty(ptzMapList)) {
                    for (AlgorithmParamPresetVO vo : ptzMapList) {
                        if (vo.getPresetId() == algo.getPresetId()) {
                            algo2.setPan(vo.getPan());
                            algo2.setTilt(vo.getTilt());
                            algo2.setZoom(vo.getZoom());
                            break;
                        }
                    }
                }
                algo.setPreset(algo2);
            }
        }
        String tenantId;
        // 判断住户id 是否为空 如果传入未空就从登入人获取
        if (StringUtils.isBlank(algorithmListVO.getTenantId())) {
            tenantId = sysUser.getTenantId();
        } else {
            tenantId = algorithmListVO.getTenantId();
        }
        String deviceCode = algorithmListVO.getDeviceCode();
        String channelCode = algorithmListVO.getChannelCode();

        //查询设备接入节点
        DeviceIndReqDTO deviceIndReqDTO = new DeviceIndReqDTO();
        deviceIndReqDTO.setDeviceCode(deviceCode);
        DeviceIndDTO deviceBasicInfo = deviceInfoMapper.getDeviceBasicInfo(deviceIndReqDTO);

        //查询大模型算法
        List<String> largeModelAlgorithms = algorithmConfigMapper.getLargeModelAlgorithms();

        //处理新增的报文
        algorithmListVO.getPositions().forEach(position -> position.getAlgorithms().forEach(item -> {
            if ((item.getTaskId() == null || StringUtils.isBlank(item.getTaskId())) && null != item.getOptType() && item.getOptType() == AlgorithmParamBodyVO.NEW_OPTTYPE) {
                item.setTaskId(getUUID());
            }
        }));

        // 写新数据
        List<AlgorithmConfigDO> algorithmConfigDOList = new ArrayList<>();
        List<AlgorithmConfigVO> algoCheckList = new ArrayList<>();
        CheckAlgorithmConfigVO checkAlgorithmConfigVO = new CheckAlgorithmConfigVO();
        checkAlgorithmConfigVO.setTenantId(tenantId);
        checkAlgorithmConfigVO.setIndustryCode(sysUser.getIndustryCode());
        checkAlgorithmConfigVO.setDeviceCode(deviceCode);
        checkAlgorithmConfigVO.setChannelCode(channelCode);
        List<AlgorithmConfigVO> algorithmConfigList = algorithmConfigMapper.getAlgorithmConfig(checkAlgorithmConfigVO);
        algoCheckList.addAll(algorithmConfigList);

        List<AlgorithmAreaVO> polygonAreaList = new ArrayList<>();

        ObjectMapper mapper2 = new ObjectMapper();
        // for循环调用优化
        String tenantName = algorithmConfigMapper.selectTenantName(sysUser.getTenantId());
        AlgorithmConfigDO configDO1 = new AlgorithmConfigDO();
        configDO1.setDeviceCode(deviceCode);
        configDO1.setTenantId(tenantId);
        configDO1.setIndustryCode(industryCode);
        OrderAlgorithmsVO orderAlgorithmsVO = algorithmConfigMapper.selectOrderCode(configDO1);
        for (AlgorithmConfigVO conf : algorithmListVO.getPositions()) {
            for (AlgorithmAreaVO confArea : conf.getAlgorithms()) {
                if (confArea.getOptType() == null || confArea.getOptType() != AlgorithmParamBodyVO.NEW_OPTTYPE) {
                    continue;
                }
                confArea.getMonitorArea().forEach(item -> {
                    String monitorAreaId = item.getMonitorAreaId();
                    if (StringUtils.isEmpty(monitorAreaId)) {
                        item.setMonitorAreaId(IdUtils.simpleUUID());
                    }
                });

                AlgorithmConfigDO configDO = new AlgorithmConfigDO();
                configDO.setTenantId(tenantId);
                configDO.setDeviceCode(deviceCode);
                configDO.setChannelCode(channelCode);
                configDO.setSceneCode(confArea.getSceneCode());
                try {
                    //拆分点位
                    List<DevicePointVo> points = new ArrayList<>();

                    //经纬度json
                    List<AlgorithmMonitorAreaVO> monitorAreaLonLat = new ArrayList<>();

                    List<OilGasPipelinesVO> oilGasLonLat = new ArrayList<>();

                    List<TripThreadVO> tripThreadListLonLat = new ArrayList<>();

                    List<TextLabelVO> textLabelListLonLat = new ArrayList<>();


                    if (!CollectionUtils.isEmpty(confArea.getMonitorArea())) {
                        confArea.setPoints(mapper2.writeValueAsString(confArea.getMonitorArea()));//对象序列化为json

                        //---------------------读取所有监控、屏蔽、基准区域百分比点位--------------------------------------
                        if (PRESET_TYPE.equals(conf.getPosType())) {
                            List<AlgorithmMonitorAreaVO> monitorArea = confArea.getMonitorArea();

                            monitorAreaLonLat = new ArrayList<>(monitorArea);
                            //添加所有的监控、屏蔽、基准区域百分比点位
                            monitorArea.forEach(item -> {
                                List<AlgorithmMonitorPointVO> moniPointList = item.getMoniPointList();
                                if (null != moniPointList) {
                                    moniPointList.forEach(point -> {
                                        DevicePointVo devicePointVo = new DevicePointVo();
                                        devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                        devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                        //唯一标识，方便回填数据
                                        devicePointVo.setFlag(item.getMonitorAreaId() + "-" + point.getMonitorPointId());
                                        points.add(devicePointVo);
                                    });
                                }
                            });
                        }
                    } else {//该字段不能为空 所以赋予空数组字符串
                        confArea.setPoints(DeviceSyncConstants.EMPTY_ARR);//对象序列化为json
                    }
                    if (!CollectionUtils.isEmpty(confArea.getModelConfigList())) {
                        List<ModelConfigVO> modelConfigList = confArea.getModelConfigList();
                        //大模型高级参数处理了逻辑
                        if(!StringUtils.isEmpty(confArea.getSceneCode())){
                            this.dealModeConfig(modelConfigList);
                        }
                        confArea.setModelConfig(mapper2.writeValueAsString(modelConfigList));
                    }

                    if (!CollectionUtils.isEmpty(confArea.getOilGasPipelinesList())) {
                        oilGasLonLat = new ArrayList<>(confArea.getOilGasPipelinesList());

                        for (int i = 0; i < confArea.getOilGasPipelinesList().size(); i++) {
                            //取出数组的每一个对象，如果管线粗细，箭头颜色，箭头粗细为空，初始化"width":"12","arrowColor":"rgba(255,255,255,1)","arrowWidth":"1"这三个默认值（兼容老版本app）
                            OilGasPipelinesVO tempOilGasPipelinesVO = confArea.getOilGasPipelinesList().get(i);
                            if (StringUtils.isEmpty(tempOilGasPipelinesVO.getWidth())) {
                                tempOilGasPipelinesVO.setWidth(Constants.TWELVE);
                            }
                            if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowColor())) {
                                tempOilGasPipelinesVO.setArrowColor(Constants.RGBA);
                            }
                            if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowWidth())) {
                                tempOilGasPipelinesVO.setArrowWidth(Constants.ONE);
                            }
                            if (PRESET_TYPE.equals(conf.getPosType())) {
                                //---------------------读取所有的管线百分比坐标--------------------------------------
                                List<DirectionVO> direction = tempOilGasPipelinesVO.getDirection();

                                if (!CollectionUtils.isEmpty(direction)) {
                                    direction.forEach(point -> {
                                        DevicePointVo devicePointVo = new DevicePointVo();
                                        devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                        devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                        //唯一标识，方便回填数据
                                        devicePointVo.setFlag(tempOilGasPipelinesVO.getOilGasId() + "-" + point.getPointId());
                                        points.add(devicePointVo);
                                    });
                                }
                            }

                        }
                        confArea.setOilGasPipelines(mapper2.writeValueAsString(confArea.getOilGasPipelinesList()));
                    }
                    if (!CollectionUtils.isEmpty(confArea.getTripThreadList())) {
                        confArea.setTripThread(mapper2.writeValueAsString(confArea.getTripThreadList()));

                        if (PRESET_TYPE.equals(conf.getPosType())) {
                            //---------------------读取所有绊线百分比点位--------------------------------------
                            List<TripThreadVO> tripThreadList = confArea.getTripThreadList();
                            tripThreadListLonLat = new ArrayList<>(tripThreadList);
                            //添加所有的监控、屏蔽、基准区域百分比点位
                            tripThreadList.forEach(item -> {
                                List<ThreadLineVO> threadLine = item.getThreadLine();
                                if (null != threadLine) {
                                    threadLine.forEach(point -> {
                                        DevicePointVo devicePointVo = new DevicePointVo();
                                        devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                        devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                        //唯一标识，方便回填数据
                                        devicePointVo.setFlag(item.getThreadId() + LINE_SPLIT + point.getPointId());
                                        points.add(devicePointVo);
                                    });
                                }
                                List<DirectionVO> direction = item.getDirection();
                                if (null != direction) {
                                    direction.forEach(point -> {
                                        DevicePointVo devicePointVo = new DevicePointVo();
                                        devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                        devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                        //唯一标识，方便回填数据
                                        devicePointVo.setFlag(item.getThreadId() + DIRECTION_SPLIT + point.getPointId());
                                        points.add(devicePointVo);
                                    });
                                }
                            });
                        }


                    }
                    //add by liuxt 20250304 增加文本标签
                    if (!CollectionUtils.isEmpty(confArea.getTextLabelList())) {
                        confArea.setTextLabels(mapper2.writeValueAsString(confArea.getTextLabelList()));
                        if (PRESET_TYPE.equals(conf.getPosType())) {
                            //---------------------读取所有文本百分比点位--------------------------------------
                            List<TextLabelVO> textLabelList = confArea.getTextLabelList();
                            textLabelListLonLat = new ArrayList<>(textLabelList);
                            //添加所有的监控、屏蔽、基准区域百分比点位
                            textLabelList.forEach(item -> {
                                String point = item.getPoint();
                                if (!point.isEmpty()) {
                                    String[] split = point.split(",");
                                    DevicePointVo devicePointVo = new DevicePointVo();
                                    devicePointVo.setPercentX(split[0]);
                                    devicePointVo.setPercentY(split[1]);
                                    //唯一标识，方便回填数据
                                    devicePointVo.setFlag(item.getPoint());
                                    points.add(devicePointVo);
                                }

                            });
                        }
                    }
                    if (!CollectionUtils.isEmpty(confArea.getAnalysisPeriodList())) {
                        confArea.setAnalysisPeriod(mapper2.writeValueAsString(confArea.getAnalysisPeriodList()));
                    }
                    //-----------------------根据百分比坐标点反查经纬度-----------------------
                    if (PRESET_TYPE.equals(conf.getPosType()) && !CollectionUtils.isEmpty(points)) {
                        //根据百分比坐标点反查经纬度
                        DevicePointTurn devicePointTurn = new DevicePointTurn();
                        devicePointTurn.setDeviceCode(deviceCode);
                        devicePointTurn.setChannelCode(channelCode);

                        devicePointTurn.setPoints(points);

                        //查询预置位ptz
                        PresetData presetData = deviceInfoMapper.findPreset(conf.getPresetId(), deviceCode, channelCode);

                        if (null != presetData) {
                            devicePointTurn.setParamPan(String.valueOf(presetData.getPan()));
                            devicePointTurn.setParamTitl(String.valueOf(presetData.getTilt()));
                            devicePointTurn.setParamZoom(String.valueOf(presetData.getZoom()));
                            log.info("根据百分比坐标查询返回经纬度入参:{}", GsonUtils.toJSONString(devicePointTurn));
                            Result<DevicePointTurn> devicePointTurnResult = iTradeWarningOrderService.turnLatitudeByPoint(devicePointTurn);
                            log.info("根据百分比坐标查询返回经纬度返回:{}", GsonUtils.toJSONString(devicePointTurnResult));
                            if (200 == (devicePointTurnResult.getCode())) {
                                DevicePointTurn data = devicePointTurnResult.getData();
                                List<DevicePointVo> pointsLatLon = data.getPoints();
                                if (!pointsLatLon.isEmpty()) {
                                    //转换map结构
                                    Map<String, DevicePointVo> flagLonLatMap = pointsLatLon.stream().collect(Collectors.toMap(DevicePointVo::getFlag, devicePointVo -> devicePointVo));
                                    //放置屏蔽区域，基准区域，监控区域的经纬度
                                    if (!monitorAreaLonLat.isEmpty()) {
                                        monitorAreaLonLat.forEach(monitorArea -> {
                                            List<AlgorithmMonitorPointVO> moniPointList = monitorArea.getMoniPointList();
                                            if (!moniPointList.isEmpty()) {
                                                moniPointList.forEach(point -> {
                                                    String flag = monitorArea.getMonitorAreaId() + "-" + point.getMonitorPointId();
                                                    if (flagLonLatMap.containsKey(flag)) {
                                                        DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                                        point.setLat(pointLonLat.getLatitude());
                                                        point.setLon(pointLonLat.getLongitude());
                                                    }
                                                });
                                            }
                                        });
                                        //转json 保存
                                        confArea.setPointsLonLat(mapper2.writeValueAsString(monitorAreaLonLat));

                                    }
                                    //放置管线经纬度
                                    if (!oilGasLonLat.isEmpty()) {
                                        oilGasLonLat.forEach(oilGas -> {
                                            List<DirectionVO> direction = oilGas.getDirection();
                                            if (!direction.isEmpty()) {
                                                direction.forEach(point -> {
                                                    String flag = oilGas.getOilGasId() + "-" + point.getPointId();
                                                    if (flagLonLatMap.containsKey(flag)) {
                                                        DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                                        point.setLat(pointLonLat.getLatitude());
                                                        point.setLon(pointLonLat.getLongitude());
                                                    }
                                                });
                                            }
                                        });
                                        //转json 保存
                                        confArea.setOilGasPipelinesLonLat(mapper2.writeValueAsString(oilGasLonLat));
                                    }
                                    //放置绊线
                                    if (!tripThreadListLonLat.isEmpty()) {
                                        tripThreadListLonLat.forEach(tripThread -> {
                                            List<ThreadLineVO> threadLine = tripThread.getThreadLine();
                                            if (!threadLine.isEmpty()) {
                                                threadLine.forEach(point -> {
                                                    String flag = tripThread.getThreadId() + LINE_SPLIT + point.getPointId();
                                                    if (flagLonLatMap.containsKey(flag)) {
                                                        DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                                        point.setLat(pointLonLat.getLatitude());
                                                        point.setLon(pointLonLat.getLongitude());
                                                    }
                                                });
                                            }
                                            List<DirectionVO> direction = tripThread.getDirection();
                                            if (!direction.isEmpty()) {
                                                direction.forEach(point -> {
                                                    String flag = tripThread.getThreadId() + DIRECTION_SPLIT + point.getPointId();
                                                    if (flagLonLatMap.containsKey(flag)) {
                                                        DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                                        point.setLat(pointLonLat.getLatitude());
                                                        point.setLon(pointLonLat.getLongitude());
                                                    }
                                                });
                                            }
                                        });
                                        //转json 保存
                                        confArea.setTripThreadLonLat(mapper2.writeValueAsString(tripThreadListLonLat));
                                    }
                                    //放置文本
                                    if (!textLabelListLonLat.isEmpty()) {
                                        textLabelListLonLat.forEach(textLabel -> {
                                            if (null != textLabel) {
                                                String flag = textLabel.getPoint();
                                                if (flagLonLatMap.containsKey(flag)) {
                                                    DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                                    textLabel.setLat(pointLonLat.getLatitude());
                                                    textLabel.setLon(pointLonLat.getLongitude());
                                                }
                                            }
                                        });
                                        //转json 保存
                                        confArea.setTextLabelsLonLat(mapper2.writeValueAsString(textLabelListLonLat));
                                    }
                                }
                            }
                        }

                    }


                    if (!StringUtils.isEmpty(confArea.getIsLinkageAlgorithm())) {
                        if (Constants.ZERO.equals(confArea.getIsLinkageAlgorithm())) {
                            confArea.setLinkageAlgorithm(null);
                        } else {
                            if (!ObjectUtils.isEmpty(confArea.getLinkageAlgorithmVO())) {
                                confArea.setLinkageAlgorithm(mapper2.writeValueAsString(confArea.getLinkageAlgorithmVO()));
                            }
                        }
                    } else {
                        confArea.setIsLinkageAlgorithm(Constants.ZERO);
                    }

                    if (!StringUtils.isEmpty(confArea.getStatus())) {
                        confArea.setStatus(confArea.getStatus());
                    } else {
                        confArea.setStatus(AlgorithmAreaVO.ALGORITHMCLOSESTATUS);//新增状态为启用  2022/6/21 状态为算法任务状态：0:启用 1:停用
                    }
                } catch (Exception e) {
                    log.error(DeviceSyncConstants.ADD_ALGO_ERROR, e);
                    return Result.fail(DeviceSyncConstants.ADD_ALGO_ERROR);
                }
                configDO.setAlgorithmCode(confArea.getAlgorithmCode());
                if (!StringUtils.isEmpty(confArea.getPoints())) {
                    configDO.setPoints(confArea.getPoints());
                }
                if (!StringUtils.isEmpty(confArea.getOilGasPipelines())) {
                    configDO.setOilGasPipelines(confArea.getOilGasPipelines());
                }
                //add by liuxt 20250304 增加文本标签
                if (!StringUtils.isEmpty(confArea.getTextLabels())) {
                    configDO.setTextLabels(confArea.getTextLabels());
                }

                //-------------------增加经纬度保存------------------------
                if (!StringUtils.isEmpty(confArea.getPointsLonLat())) {
                    configDO.setPointsLonLat(confArea.getPointsLonLat());
                }
                if (!StringUtils.isEmpty(confArea.getOilGasPipelinesLonLat())) {
                    configDO.setOilGasPipelinesLonLat(confArea.getOilGasPipelinesLonLat());
                }
                if (!StringUtils.isEmpty(confArea.getTextLabelsLonLat())) {
                    configDO.setTextLabelsLonLat(confArea.getTextLabelsLonLat());
                }
                if (!StringUtils.isEmpty(confArea.getTripThreadLonLat())) {
                    configDO.setTripThreadLonLat(confArea.getTripThreadLonLat());
                }

                configDO.setIsLinkageAlgorithm(confArea.getIsLinkageAlgorithm());
                configDO.setLinkageAlgorithm(confArea.getLinkageAlgorithm());


                //增加置信度、配置参数、伴线存表
                if (!StringUtils.isEmpty(confArea.getModelConfig())) {
                    configDO.setModelConfig(confArea.getModelConfig());
                }
                if (!StringUtils.isEmpty(confArea.getTripThread())) {
                    configDO.setTripThread(confArea.getTripThread());
                }
                //增加算法分析时段存表
                configDO.setConfigId(getUUID());
                if (!StringUtils.isEmpty(confArea.getAnalysisPeriod())) {
                    configDO.setAnalysisPeriod(confArea.getAnalysisPeriod());
                }
                //增加任务抽帧频率
                Integer timeIntervalUnit = confArea.getTimeIntervalUnit();
                configDO.setTimeInterval(this.timeUnitConvert(timeIntervalUnit, confArea.getTimeInterval()));
                configDO.setTimeIntervalUnit(timeIntervalUnit);

                if (!StringUtils.isEmpty(confArea.getAlarmFrameColor()) || !StringUtils.isEmpty(confArea.getDoubleImgAnalysis())) {
                    ExtendJsonAlgo extendJsonAlgo = new ExtendJsonAlgo();
                    extendJsonAlgo.setAlarmFrameColor(confArea.getAlarmFrameColor());
                    extendJsonAlgo.setAlarmFrameWidth(confArea.getAlarmFrameWidth());
                    extendJsonAlgo.setDoubleImgAnalysis(confArea.getDoubleImgAnalysis());
                    configDO.setExtendJsonAlgo(JSON.toJSONString(extendJsonAlgo));
                }

                //图斑与识别区域数据处理
                confArea.setConfigId(configDO.getConfigId());
                if (!CollectionUtils.isEmpty(confArea.getPolygonAreaList())) {
                    confArea.setDeviceCode(deviceCode);
                    confArea.setChannelCode(channelCode);
                    confArea.setPresetId(String.valueOf(conf.getPresetId()));
                    confArea.setPresetName(conf.getPresetName());
                    polygonAreaList.add(confArea);
                }

                configDO.setImg(this.getImgWithNode(deviceCode, confArea.getImg()));
                configDO.setAlgorithmName(confArea.getAlgorithmName());
                configDO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
                configDO.setTaskId(confArea.getTaskId());
                configDO.setStatus(confArea.getStatus());
                configDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                configDO.setCreateTime(DateUtils.getTime());
                configDO.setCreator(SecurityUtils.getOperator());
                configDO.setAppVerCode(sysUser.getAppVerCode());
                configDO.setPosType(conf.getPosType());
                configDO.setDeviceName(algorithmListVO.getDeviceName());
                configDO.setChannelName(algorithmListVO.getChannelName());
                configDO.setAlarmInterval(confArea.getAlarmInterval());
                // for循环调用优化
                configDO.setTenantName(tenantName);
                //新增算法行业编码
                if (!industryCode.isEmpty()) {
                    configDO.setIndustryCode(industryCode);
                    configDO.setIndustryCodeHis(configDO.getIndustryCode());
                }
                if (null != conf.getPresetId()) {
                    configDO.setPresetId(conf.getPresetId().intValue());
                }
                //查询订单以及设备受理节点
                // for 循环调用优化
                if (null != orderAlgorithmsVO) {
                    configDO.setOrderCode(orderAlgorithmsVO.getOrderCode());
                }
                configDO.setAcceptNode(sysUser.getPlatformCode());
                configDO.setAccessNode(deviceBasicInfo.getAccessNode());
                configDO.setPresetName(conf.getPresetName());

                //判断algoCheckList中，是否有重复数据：同一通道配置同一算法或同一预置位配置同一算法
                boolean booChan = algoCheckList.stream().anyMatch(algo ->
                        algo.getPosType() == DeviceConstants.ONE_NUM
                                && algo.getPosType().equals(configDO.getPosType()) &&
                                configDO.getAlgorithmCode().equals(algo.getAlgorithmCode()));
                boolean booPre = algoCheckList.stream().anyMatch(algo ->
                        algo.getPosType() == DeviceConstants.TWO_NUM
                                && configDO.getAlgorithmCode().equals(algo.getAlgorithmCode()) &&
                                Objects.equals(configDO.getPosType(), algo.getPosType()) &&
                                configDO.getPresetId() == algo.getPresetId().intValue());
                if (booChan || booPre) {
                    return Result.fail(DeviceSyncConstants.ADD_EXIST_ALGO);
                }

                //如果某个算法先被配置为通道算法，然后又被配置为预置位算法，则默认后配置的预置位算法状态为停用
                boolean ifAlgo = algoCheckList.stream().anyMatch(algo ->
                        configDO.getAlgorithmCode().equals(algo.getAlgorithmCode()) &&
                                DeviceConstants.ZERO.equals(algo.getStatus())
                );
                if (conf.getPosType() == DeviceConstants.ONE_NUM && ifAlgo && !largeModelAlgorithms.contains(configDO.getAlgorithmCode())) {
                    configDO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
                    configDO.setStatus(AlgorithmAreaVO.INVOKESTATUS);
                }

                boolean ifPre = algoCheckList.stream().anyMatch(algo ->
                        algo.getPosType() == DeviceConstants.ONE_NUM &&
                                DeviceConstants.ZERO.equals(algo.getStatus()) &&
                                configDO.getAlgorithmCode().equals(algo.getAlgorithmCode()));
                if (conf.getPosType() == DeviceConstants.TWO_NUM && ifPre && !largeModelAlgorithms.contains(configDO.getAlgorithmCode())) {
                    configDO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
                    configDO.setStatus(AlgorithmAreaVO.INVOKESTATUS);
                }

                AlgorithmConfigVO algoConfigVO = new AlgorithmConfigVO();
                BeanUtils.copyProperties(configDO, algoConfigVO);
                if (ObjectUtil.isNotEmpty(configDO.getPresetId())) {
                    algoConfigVO.setPresetId(configDO.getPresetId().longValue());
                }
                algoCheckList.add(algoConfigVO);
                algorithmConfigDOList.add(configDO);
            }
        }
        if (!CollectionUtils.isEmpty(algorithmConfigDOList)) {
            this.saveBatch(algorithmConfigDOList);
        }

        //批量处理图斑与识别区域数据
        if (!CollectionUtils.isEmpty(polygonAreaList)) {
            this.handlePolygonArea(polygonAreaList);
        }

        return Result.success("算法配置新增成功!");
    }


    /**
     * 后端算法 通道 批量新增  -----------------大屏算法组件使用
     *
     * @param list list
     * @return Result
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveAlgorithm(List<AlgorithmListVO> list) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            throw new CustomException("铁塔账号无法进行算法配置新增操作", 400);
        }
        String industryCode = sysUser.getIndustryCode();

        List<CheckAlgorithmConfigVO> checks = new ArrayList<>();
        list.forEach(item -> {
            item.setIndustryCode(industryCode);
            //设置id
            item.getPositions().forEach(position -> {
                position.getAlgorithms().forEach(algo -> {
                    if ((algo.getTaskId() == null || StringUtils.isBlank(algo.getTaskId())) && null != algo.getOptType() && algo.getOptType() == AlgorithmParamBodyVO.NEW_OPTTYPE) {
                        algo.setTaskId(getUUID());
                    }

                    CheckAlgorithmConfigVO checkAlgorithmConfigVO = new CheckAlgorithmConfigVO();
                    checkAlgorithmConfigVO.setAlgorithmCode(algo.getAlgorithmCode());
                    checkAlgorithmConfigVO.setChannelCode(item.getChannelCode());
                    checkAlgorithmConfigVO.setDeviceCode(item.getDeviceCode());
                    checkAlgorithmConfigVO.setTenantId(sysUser.getTenantId());
                    checkAlgorithmConfigVO.setIndustryCode(sysUser.getIndustryCode());
                    checks.add(checkAlgorithmConfigVO);
                });
            });
        });
        log.info("算法配置新增入参:" + GsonUtils.toJSONString(list));


        //查询设备接入节点
        List<DeviceIndDTO> devices = deviceInfoMapper.getDeviceBasicInfoBatch(list);
        Map<String, String> deviceNodeMap = devices.stream().collect(Collectors.toMap(DeviceIndDTO::getDeviceCode, DeviceIndDTO::getAccessNode));


        CheckAlgorithmConfigVO checkAlgorithmConfigVO = new CheckAlgorithmConfigVO();
        checkAlgorithmConfigVO.setTenantId(sysUser.getTenantId());
        checkAlgorithmConfigVO.setIndustryCode(sysUser.getIndustryCode());

        //查询对应设备、通道、算法 是否 配置预置位算法
        List<AlgorithmConfigVO> presetAlgorithmChecks = algorithmConfigMapper.getAlgorithmByList(checks, sysUser.getIndustryCode(), sysUser.getTenantId(), Constants.TWO_NUM, Constants.ZERO);

        Map<String, AlgorithmConfigVO> presetAlgorithmChecksMap = presetAlgorithmChecks.stream().collect(Collectors.toMap(t -> t.getChannelCode() + "-" + t.getAlgorithmCode(), t -> t));


        String tenantName = algorithmConfigMapper.selectTenantName(sysUser.getTenantId());


        List<OrderAlgorithmsVO> orderList = algorithmConfigMapper.selectOrderCodeList(checks, sysUser.getTenantId(), sysUser.getIndustryCode());
        Map<String, String> deviceOrderMap = orderList.stream().collect(Collectors.toMap(OrderAlgorithmsVO::getDeviceCode, OrderAlgorithmsVO::getOrderCode));

        // 写新数据
        List<AlgorithmConfigDO> algorithmConfigDOList = new ArrayList<>();

        list.forEach(item -> item.getPositions().forEach(pos -> {
            for (AlgorithmAreaVO confArea : pos.getAlgorithms()) {
                if (confArea.getOptType() == null || confArea.getOptType() != AlgorithmParamBodyVO.NEW_OPTTYPE) {
                    continue;
                }
                AlgorithmConfigDO configDO = new AlgorithmConfigDO();
                configDO.setIsLinkageAlgorithm(Constants.ZERO);
                configDO.setTenantId(sysUser.getTenantId());
                configDO.setDeviceCode(item.getDeviceCode());
                configDO.setChannelCode(item.getChannelCode());
                try {
                    if (!CollectionUtils.isEmpty(confArea.getMonitorArea())) {
                        confArea.setPoints(JSON.toJSONString(confArea.getMonitorArea()));//对象序列化为json
                    } else {
                        confArea.setPoints(DeviceSyncConstants.EMPTY_ARR);
                    }
                    if (!CollectionUtils.isEmpty(confArea.getModelConfigList())) {
                        confArea.setModelConfig(JSON.toJSONString(confArea.getModelConfigList()));
                    }
                    if (!CollectionUtils.isEmpty(confArea.getOilGasPipelinesList())) {
                        confArea.setOilGasPipelines(JSON.toJSONString(confArea.getOilGasPipelinesList()));
                    }
                    if (!CollectionUtils.isEmpty(confArea.getTripThreadList())) {
                        confArea.setTripThread(JSON.toJSONString(confArea.getTripThreadList()));
                    }
                    if (!CollectionUtils.isEmpty(confArea.getAnalysisPeriodList())) {
                        confArea.setAnalysisPeriod(JSON.toJSONString(confArea.getAnalysisPeriodList()));
                    }

                    if (!StringUtils.isEmpty(confArea.getStatus())) {
                        confArea.setStatus(confArea.getStatus());
                    } else {
                        confArea.setStatus(AlgorithmAreaVO.ALGORITHMCLOSESTATUS);//新增状态为启用  2022/6/21 状态为算法任务状态：0:启用 1:停用
                    }
                } catch (Exception e) {
                    log.error(DeviceSyncConstants.ADD_ALGO_ERROR, e);
                    continue;
                }
                configDO.setAlgorithmCode(confArea.getAlgorithmCode());
                configDO.setPoints(confArea.getPoints());
                configDO.setOilGasPipelines(confArea.getOilGasPipelines());

                //增加置信度、配置参数、伴线存表
                configDO.setModelConfig(confArea.getModelConfig());
                configDO.setTripThread(confArea.getTripThread());

                //增加算法分析时段存表
                configDO.setConfigId(getUUID());
                configDO.setAnalysisPeriod(confArea.getAnalysisPeriod());

                configDO.setImg(confArea.getImg());
                configDO.setAlgorithmName(confArea.getAlgorithmName());
                configDO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
                configDO.setTaskId(confArea.getTaskId());
                configDO.setStatus(confArea.getStatus());
                configDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                configDO.setCreateTime(DateUtils.getTime());
                configDO.setCreator(SecurityUtils.getOperator());
                configDO.setAppVerCode(sysUser.getAppVerCode());
                configDO.setPosType(pos.getPosType());
                configDO.setDeviceName(item.getDeviceName());
                configDO.setChannelName(item.getChannelName());
                configDO.setTenantName(tenantName);
                configDO.setAlarmInterval(confArea.getAlarmInterval());
                //新增算法行业编码
                if (!industryCode.isEmpty()) {
                    configDO.setIndustryCode(industryCode);
                    configDO.setIndustryCodeHis(configDO.getIndustryCode());
                }
                if (null != pos.getPresetId()) {
                    configDO.setPresetId(pos.getPresetId().intValue());
                    configDO.setPresetName(pos.getPresetName());
                }
                //订单以及设备受理节点
                configDO.setOrderCode(deviceOrderMap.get(item.getDeviceCode()));
                configDO.setAcceptNode(sysUser.getPlatformCode());
                configDO.setAccessNode(deviceNodeMap.get(item.getDeviceCode()));

                //如果包含，代表该算法已经配置过预置位任务，则通道任务设置为 未启用
                if (presetAlgorithmChecksMap.containsKey(item.getChannelCode() + "-" + confArea.getAlgorithmCode())) {
                    configDO.setStatus(AlgorithmAreaVO.INVOKESTATUS);
                    configDO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
                }

                algorithmConfigDOList.add(configDO);
            }
        }));
        if (!algorithmConfigDOList.isEmpty()) {
            this.saveBatch(algorithmConfigDOList);
        }
    }

    /**
     * 功能描述 算法配置查看
     *
     * @param algorithmConfigDTO
     * @return
     * @author: chenshaowu
     * @date: 2022/2/11
     */
    @Override
    @DataPermissions(layerColumn = "pc.layer_id")
    public List<AlgorithmConfigVO> queryById(AlgorithmConfigDTO algorithmConfigDTO) {
        if (StringUtils.isEmpty(algorithmConfigDTO.getConfigId())
                && CollectionUtils.isEmpty(algorithmConfigDTO.getConfigIdList())) {
            throw new CustomException("configId不能为空");
        }
        algorithmConfigDTO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        List<AlgorithmConfigVO> configList = algorithmConfigRepository.queryalgorithmConfig(algorithmConfigDTO);
        if (!CollectionUtils.isEmpty(configList)) {
            // 处理Obs文件URL加密返回
            configList.stream().forEach(algoVO -> algoVO.setImg(fileUrlEncryptUtil.dealObsFileUrl(algoVO.getImg(), true, null)));

            // 任务抽帧频率，单位转换
            configList.stream().forEach(algoVO -> algoVO.setTimeInterval(this.timeUnitConvertInverse(algoVO.getTimeIntervalUnit(), algoVO.getTimeInterval())));

            //查询图斑信息
            List<String> collect = configList.stream().map(AlgorithmConfigVO::getConfigId).collect(Collectors.toList());
            PolygonPatchAlgoAreaVO polygonPatchAlgoAreaVO = new PolygonPatchAlgoAreaVO();
            polygonPatchAlgoAreaVO.setDataPermissions(algorithmConfigDTO.getDataPermissions());
            polygonPatchAlgoAreaVO.setConfigIdList(collect);

            List<PolygonPatchAlgoAreaVO> polygonList = polygonMapper.getPolygonAlgoAreaInfo(polygonPatchAlgoAreaVO);
            // 1. 先将polygonList转换为Map，key为configId，value为对应的PolygonPatchAlgoAreaVO列表
            Map<String, List<PolygonPatchAlgoAreaVO>> configAreaMap = new HashMap<>();
            for (PolygonPatchAlgoAreaVO patchAlgoAreaVO : polygonList) {
                // 按configId分组存放
                configAreaMap.computeIfAbsent(patchAlgoAreaVO.getConfigId(), k -> new ArrayList<>())
                        .add(patchAlgoAreaVO);
            }

            // 2. 遍历配置列表，直接从Map中获取对应的数据
            for (AlgorithmConfigVO algorithmConfigVO : configList) {
                // 如果没有对应的数据，返回空列表而不是null
                List<PolygonPatchAlgoAreaVO> areaList = configAreaMap.getOrDefault(algorithmConfigVO.getConfigId(),
                        Collections.emptyList());
                algorithmConfigVO.setPolygonAreaList(areaList);
            }

            //解析扩展字段
            configList.forEach(item -> {
                String extendJson = item.getExtendJsonAlgo();
                if (!StringUtils.isEmpty(extendJson)) {
                    ExtendJsonAlgo ej = JSONObject.parseObject(extendJson, ExtendJsonAlgo.class);
                    if (!ObjectUtil.isEmpty(ej)) {
                        item.setAlarmFrameColor(ej.getAlarmFrameColor());
                        item.setAlarmFrameWidth(ej.getAlarmFrameWidth());
                        item.setDoubleImgAnalysis(ej.getDoubleImgAnalysis());
                    }
                }
            });
        }
        return configList;
    }

    /**
     * 功能描述: 算法配置编辑
     *
     * @param: algorithmConfigDTO
     * @return: Result
     * @auther: chenshaowu
     * @date: 2022/2/14
     * @version:
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(AlgorithmConfigDTO algorithmConfigDTO) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        //漏洞改造 20221118 铁塔用户算法配置编辑按钮置灰，无法进行操作
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            throw new CustomException("铁塔账号无法进行算法配置编辑操作");
        }
        String industryCode = sysUser.getIndustryCode();
        if (algorithmConfigDTO.getPreset() == null && algorithmConfigDTO.getPosType() == AlgorithmParamBodyVO.POSTYPE_PRESET) {
            AlgorithmParamPresetVO a = new AlgorithmParamPresetVO();
            a.setPresetId(algorithmConfigDTO.getPresetId());
            a.setPresetName(algorithmConfigDTO.getPresetName());
            VideoPresetDO presetDO = new VideoPresetDO();
            com.chinatower.video.api.entity.VideoPresetDO videoPresetDO = new com.chinatower.video.api.entity.VideoPresetDO();
            videoPresetDO.setPresetId(algorithmConfigDTO.getPresetId());
            videoPresetDO.setDeviceCode(algorithmConfigDTO.getDeviceCode());
            BeanUtil.copyProperties(iVideoPresetServicei.selectById(videoPresetDO), presetDO);

            if (presetDO != null) {
                a.setPan(presetDO.getPan());
                a.setTilt(presetDO.getTilt());
                a.setZoom(presetDO.getZoom());
            }
            algorithmConfigDTO.setPreset(a);
        }
        // 更新数据
        List<AlgorithmAreaVO> areaVOS = algorithmConfigDTO.getAlgorithms();
        if (areaVOS != null && areaVOS.size() == 1) {
            ObjectMapper mapper = new ObjectMapper();
            AlgorithmAreaVO vo = new AlgorithmAreaVO();
            vo.setConfigId(areaVOS.get(0).getConfigId());
            vo.setImg(this.getImgWithNode(algorithmConfigDTO.getDeviceCode(), areaVOS.get(0).getImg()));
            //0:启用 1:停用
            vo.setStatus(AlgorithmAreaVO.ALGORITHMCLOSESTATUS);
            vo.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
            vo.setIndustryCode(industryCode);
            vo.setModifier(SecurityUtils.getOperator());
            vo.setAlarmInterval(areaVOS.get(0).getAlarmInterval());
            vo.setSceneCode(areaVOS.get(0).getSceneCode());
            try {
                //拆分点位
                List<DevicePointVo> points = new ArrayList<>();

                //经纬度json
                List<AlgorithmMonitorAreaVO> monitorAreaLonLat = new ArrayList<>();

                List<OilGasPipelinesVO> oilGasLonLat = new ArrayList<>();

                List<TripThreadVO> tripThreadListLonLat = new ArrayList<>();

                List<TextLabelVO> textLabelListLonLat = new ArrayList<>();

                if (!CollectionUtils.isEmpty(areaVOS.get(0).getMonitorArea())) {
                    List<AlgorithmMonitorAreaVO> monitorArea = areaVOS.get(0).getMonitorArea();
                    vo.setPoints(mapper.writeValueAsString(monitorArea));
                    monitorAreaLonLat = new ArrayList<>(monitorArea);
                    //添加所有的监控、屏蔽、基准区域百分比点位
                    monitorArea.forEach(item -> {
                        List<AlgorithmMonitorPointVO> moniPointList = item.getMoniPointList();
                        if (null != moniPointList) {
                            moniPointList.forEach(point -> {
                                DevicePointVo devicePointVo = new DevicePointVo();
                                devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                //唯一标识，方便回填数据
                                devicePointVo.setFlag(item.getMonitorAreaId() + "-" + point.getMonitorPointId());
                                points.add(devicePointVo);
                            });
                        }
                    });

                } else {
                    vo.setPoints(DeviceSyncConstants.EMPTY_ARR);
                }
                //增加置信度、配置参数、伴线、算法运行时间、油气管线存表
                if (!CollectionUtils.isEmpty(areaVOS.get(0).getModelConfigList())) {
                    List<ModelConfigVO> modelConfigList = areaVOS.get(0).getModelConfigList();
                    //大模型高级参数处理了逻辑
                    if(!StringUtils.isEmpty(areaVOS.get(0).getSceneCode())){
                        this.dealModeConfig(modelConfigList);
                    }
                    vo.setModelConfig(mapper.writeValueAsString(modelConfigList));
                } else {
                    vo.setModelConfig(null);
                }
                if (!CollectionUtils.isEmpty(areaVOS.get(0).getTripThreadList())) {
                    List<TripThreadVO> tripThreadList = areaVOS.get(0).getTripThreadList();
                    vo.setTripThread(mapper.writeValueAsString(areaVOS.get(0).getTripThreadList()));
                    //---------------------读取所有绊线百分比点位--------------------------------------
                    tripThreadListLonLat = new ArrayList<>(tripThreadList);
                    //添加所有的监控、屏蔽、基准区域百分比点位
                    tripThreadList.forEach(item -> {
                        List<ThreadLineVO> threadLine = item.getThreadLine();
                        if (null != threadLine) {
                            threadLine.forEach(point -> {
                                DevicePointVo devicePointVo = new DevicePointVo();
                                devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                //唯一标识，方便回填数据
                                devicePointVo.setFlag(item.getThreadId() + LINE_SPLIT + point.getPointId());
                                points.add(devicePointVo);
                            });
                        }
                        List<DirectionVO> direction = item.getDirection();
                        if (null != direction) {
                            direction.forEach(point -> {
                                DevicePointVo devicePointVo = new DevicePointVo();
                                devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                //唯一标识，方便回填数据
                                devicePointVo.setFlag(item.getThreadId() + DIRECTION_SPLIT + point.getPointId());
                                points.add(devicePointVo);
                            });
                        }
                    });
                } else {
                    vo.setTripThread(null);
                }
                if (!CollectionUtils.isEmpty(areaVOS.get(0).getOilGasPipelinesList())) {
                    for (int i = 0; i < areaVOS.get(0).getOilGasPipelinesList().size(); i++) {
                        //取出数组的每一个对象，如果管线粗细，箭头颜色，箭头粗细为空，初始化"width":"12","arrowColor":"rgba(255,255,255,1)","arrowWidth":"1"这三个默认值（兼容老版本app）
                        OilGasPipelinesVO tempOilGasPipelinesVO = areaVOS.get(0).getOilGasPipelinesList().get(i);
                        if (StringUtils.isEmpty(tempOilGasPipelinesVO.getWidth())) {
                            tempOilGasPipelinesVO.setWidth(Constants.TWELVE);
                        }
                        if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowColor())) {
                            tempOilGasPipelinesVO.setArrowColor(Constants.RGBA);
                        }
                        if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowWidth())) {
                            tempOilGasPipelinesVO.setArrowWidth(Constants.ONE);
                        }

                        List<OilGasPipelinesVO> oilGasPipelinesList = areaVOS.get(0).getOilGasPipelinesList();
                        oilGasLonLat = new ArrayList<>(areaVOS.get(0).getOilGasPipelinesList());

                        //取出数组的每一个对象，如果管线粗细，箭头颜色，箭头粗细为空，初始化"width":"12","arrowColor":"rgba(255,255,255,1)","arrowWidth":"1"这三个默认值（兼容老版本app）
                        if (StringUtils.isEmpty(tempOilGasPipelinesVO.getWidth())) {
                            tempOilGasPipelinesVO.setWidth(Constants.TWELVE);
                        }
                        if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowColor())) {
                            tempOilGasPipelinesVO.setArrowColor(Constants.RGBA);
                        }
                        if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowWidth())) {
                            tempOilGasPipelinesVO.setArrowWidth(Constants.ONE);
                        }
                        if (PRESET_TYPE.equals(algorithmConfigDTO.getPosType())) {
                            //---------------------读取所有的管线百分比坐标--------------------------------------
                            List<DirectionVO> direction = tempOilGasPipelinesVO.getDirection();

                            if (!CollectionUtils.isEmpty(direction)) {
                                direction.forEach(point -> {
                                    DevicePointVo devicePointVo = new DevicePointVo();
                                    devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                    devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                    //唯一标识，方便回填数据
                                    devicePointVo.setFlag(tempOilGasPipelinesVO.getOilGasId() + "-" + point.getPointId());
                                    points.add(devicePointVo);
                                });
                            }
                        }

                    }
                    vo.setOilGasPipelines(mapper.writeValueAsString(areaVOS.get(0).getOilGasPipelinesList()));
                } else {
                    vo.setOilGasPipelines(null);
                }
                //add by liuxt 20250304 增加文本标签
                if (!CollectionUtils.isEmpty(areaVOS.get(0).getTextLabelList())) {
                    vo.setTextLabels(mapper.writeValueAsString(areaVOS.get(0).getTextLabelList()));
                    List<TextLabelVO> textLabelList = areaVOS.get(0).getTextLabelList();
                    if (PRESET_TYPE.equals(algorithmConfigDTO.getPosType())) {
                        //---------------------读取所有文本百分比点位--------------------------------------
                        textLabelListLonLat = new ArrayList<>(textLabelList);
                        //添加所有的监控、屏蔽、基准区域百分比点位
                        textLabelList.forEach(item -> {
                            String point = item.getPoint();
                            if (!point.isEmpty()) {
                                String[] split = point.split(",");
                                DevicePointVo devicePointVo = new DevicePointVo();
                                devicePointVo.setPercentX(split[0]);
                                devicePointVo.setPercentY(split[1]);
                                //唯一标识，方便回填数据
                                devicePointVo.setFlag(item.getPoint());
                                points.add(devicePointVo);
                            }

                        });
                    }

                } else {
                    vo.setTextLabels(null);
                }
                if (!CollectionUtils.isEmpty(areaVOS.get(0).getAnalysisPeriodList())) {
                    vo.setAnalysisPeriod(mapper.writeValueAsString(areaVOS.get(0).getAnalysisPeriodList()));
                } else {
                    vo.setAnalysisPeriod(null);
                }

                if (!StringUtils.isEmpty(areaVOS.get(0).getIsLinkageAlgorithm())) {
                    vo.setIsLinkageAlgorithm(areaVOS.get(0).getIsLinkageAlgorithm());
                    if (Constants.ZERO.equals(areaVOS.get(0).getIsLinkageAlgorithm())) {
                        vo.setLinkageAlgorithm(null);
                    } else {
                        if (!ObjectUtils.isEmpty(areaVOS.get(0).getLinkageAlgorithmVO())) {
                            vo.setLinkageAlgorithm(mapper.writeValueAsString(areaVOS.get(0).getLinkageAlgorithmVO()));
                        }
                    }
                } else {
                    vo.setIsUpdateLinkageAlgorithm(Constants.ZERO);
                }

                //增加任务抽帧频率
                Integer timeIntervalUnit = areaVOS.get(0).getTimeIntervalUnit();
                vo.setTimeInterval(this.timeUnitConvert(timeIntervalUnit, areaVOS.get(0).getTimeInterval()));
                vo.setTimeIntervalUnit(timeIntervalUnit);

                //查询数据库中的数据
                ExtendJsonAlgo ej = this.backExtendJson(algorithmConfigDTO);
                if (!StringUtils.isEmpty(areaVOS.get(0).getAlarmFrameColor())) {
                    ej.setAlarmFrameColor(areaVOS.get(0).getAlarmFrameColor());
                    ej.setAlarmFrameWidth(areaVOS.get(0).getAlarmFrameWidth());
                }
                if (!StringUtils.isEmpty(areaVOS.get(0).getDoubleImgAnalysis())) {
                    ej.setDoubleImgAnalysis(areaVOS.get(0).getDoubleImgAnalysis());
                }
                vo.setExtendJsonAlgo(JSON.toJSONString(ej));

                //------------------根据百分比计算经纬度并且反填-------------------------
                setLonLatByAlgo(points,
                        monitorAreaLonLat,
                        oilGasLonLat,
                        tripThreadListLonLat,
                        textLabelListLonLat,
                        algorithmConfigDTO.getPosType(),
                        algorithmConfigDTO.getPresetId(),
                        algorithmConfigDTO.getDeviceCode(),
                        algorithmConfigDTO.getChannelCode(),
                        vo,
                        mapper);


                //图斑与识别区域数据处理
                //识别区域无图斑信息,删除vm_polygon_patch_algo_area_rela表中该算法所有数据
                List<String> idList = new ArrayList<>();
                idList.add(areaVOS.get(0).getConfigId());
                polygonMapper.delPolygonPatchAlgoAreaBatch(idList);
                //识别区域有图斑信息;先删除vm_polygon_patch_algo_area_rela表中该算法所有数据，处理算法与图斑识别区域数据
                if (!CollectionUtils.isEmpty(areaVOS.get(0).getPolygonAreaList())) {
                    List<AlgorithmAreaVO> polygonAreaList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(areaVOS.get(0).getPolygonAreaList())) {
                        areaVOS.get(0).setDeviceCode(algorithmConfigDTO.getDeviceCode());
                        areaVOS.get(0).setChannelCode(algorithmConfigDTO.getChannelCode());
                        areaVOS.get(0).setPresetId(String.valueOf(algorithmConfigDTO.getPresetId()));
                        areaVOS.get(0).setPresetName(algorithmConfigDTO.getPresetName());
                        polygonAreaList.add(areaVOS.get(0));
                    }
                    //批量处理图斑与识别区域数据
                    if (!CollectionUtils.isEmpty(polygonAreaList)) {
                        this.handlePolygonArea(polygonAreaList);
                    }
                }
            } catch (Exception e) {
                log.error("更新后端算法异常", e);
                throw new CustomException(e.getMessage());
            }
            this.backFillColor(algorithmConfigDTO, areaVOS.get(0), vo);
            //新增算法行业编码
            algorithmConfigMapper.updAlgorithmArea(vo);
        } else {
            throw new CustomException("数据错误");
        }
    }

    private ExtendJsonAlgo backExtendJson(AlgorithmConfigDTO algorithmConfigDTO) {
        ExtendJsonAlgo ej = new ExtendJsonAlgo();
        List<AlgorithmConfigVO> configList = new ArrayList<>();
        AlgorithmConfigVO algorithmConfigVO = new AlgorithmConfigVO();
        algorithmConfigVO.setConfigId(algorithmConfigDTO.getConfigId());
        configList.add(algorithmConfigVO);
        List<AlgorithmAreaVO> areas = algorithmConfigMapper.getAlgorithmAreaRow2(null, configList);
        if (!CollectionUtils.isEmpty(areas) && areas.size() == 1) {
            AlgorithmAreaVO algorithmAreaVO = areas.get(0);
            String extendJson = algorithmAreaVO.getExtendJsonAlgo();
            if (!StringUtils.isEmpty(extendJson)) {
                ej = JSONObject.parseObject(extendJson, ExtendJsonAlgo.class);
            }
        }
        return ej;
    }

    private void backFillColor(AlgorithmConfigDTO algorithmConfigDTO, AlgorithmAreaVO areaVO, AlgorithmAreaVO vo) {
        //是否新版本请求 1是0否
        String isNewVersion = algorithmConfigDTO.getIsNewVersion();
        //旧版本app请求无法传递points.color、points.frameWidth、points.frameColor、alarmFrameColor、alarmFrameWidth字段
        //需要将原有数据的字段填充到更新实体里
        if (!Constants.ONE.equals(isNewVersion)) {
            List<AlgorithmConfigVO> configList = new ArrayList<>();
            AlgorithmConfigVO algorithmConfigVO = new AlgorithmConfigVO();
            algorithmConfigVO.setConfigId(algorithmConfigDTO.getConfigId());
            configList.add(algorithmConfigVO);
            List<AlgorithmAreaVO> areas = algorithmConfigMapper.getAlgorithmAreaRow2(null, configList);
            if (!CollectionUtils.isEmpty(areas) && areas.size() == 1) {
                AlgorithmAreaVO algorithmAreaVO = areas.get(0);
                if (StringUtils.isNotEmpty(algorithmAreaVO.getPoints())) {
                    // 解析JSON并按monitorType分组
                    List<AlgorithmMonitorAreaVO> dbMonitorArea = JSON.parseArray(algorithmAreaVO.getPoints(),
                            AlgorithmMonitorAreaVO.class);

                    // 使用Map存储monitorType到样式配置的映射
                    Map<Integer, AlgorithmMonitorAreaVO> styleMap = dbMonitorArea.stream()
                            .collect(Collectors.toMap(
                                    AlgorithmMonitorAreaVO::getMonitorType,
                                    style -> style,
                                    (existing, replacement) -> existing // 保留第一个出现的样式
                            ));

                    // 直接从Map中查找匹配的样式配置
                    List<AlgorithmMonitorAreaVO> reqMonitorArea = areaVO.getMonitorArea();
                    for (AlgorithmMonitorAreaVO req : reqMonitorArea) {
                        AlgorithmMonitorAreaVO dbStyle = styleMap.get(req.getMonitorType());
                        if (dbStyle != null) {
                            req.setColor(dbStyle.getColor());
                            req.setFrameColor(dbStyle.getFrameColor());
                            req.setFrameWidth(dbStyle.getFrameWidth());
                        }
                    }

                    // JSON序列化优化
                    try {
                        vo.setPoints(new ObjectMapper().writeValueAsString(reqMonitorArea));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("监控区域JSON序列化失败", e);
                    }
                }
                vo.setExtendJsonAlgo(algorithmAreaVO.getExtendJsonAlgo());
            }
        }
    }

    public void setLonLatByAlgo(List<DevicePointVo> points,
                                List<AlgorithmMonitorAreaVO> monitorAreaLonLat,
                                List<OilGasPipelinesVO> oilGasLonLat,
                                List<TripThreadVO> tripThreadListLonLat,
                                List<TextLabelVO> textLabelListLonLat,
                                Integer posType,
                                Long presetId,
                                String deviceCode,
                                String channelCode,
                                AlgorithmAreaVO confArea,
                                ObjectMapper mapper2) {
        try {
            //-----------------------根据百分比坐标点反查经纬度-----------------------
            if (PRESET_TYPE.equals(posType) && !CollectionUtils.isEmpty(points)) {
                //根据百分比坐标点反查经纬度
                DevicePointTurn devicePointTurn = new DevicePointTurn();
                devicePointTurn.setDeviceCode(deviceCode);
                devicePointTurn.setChannelCode(channelCode);

                devicePointTurn.setPoints(points);

                //查询预置位ptz
                PresetData presetData = deviceInfoMapper.findPreset(presetId, deviceCode, channelCode);
                devicePointTurn.setParamPan(String.valueOf(presetData.getPan()));
                devicePointTurn.setParamTitl(String.valueOf(presetData.getTilt()));
                devicePointTurn.setParamZoom(String.valueOf(presetData.getZoom()));
                log.info("根据百分比坐标查询返回经纬度入参:{}", GsonUtils.toJSONString(devicePointTurn));
                Result<DevicePointTurn> devicePointTurnResult = iTradeWarningOrderService.turnLatitudeByPoint(devicePointTurn);
                log.info("根据百分比坐标查询返回经纬度返回:{}", GsonUtils.toJSONString(devicePointTurnResult));
                //todo 接收经纬度，替换到之前的对象上
                if (200 == (devicePointTurnResult.getCode())) {
                    DevicePointTurn data = devicePointTurnResult.getData();
                    List<DevicePointVo> pointsLatLon = data.getPoints();
                    if (!pointsLatLon.isEmpty()) {
                        //转换map结构
                        Map<String, DevicePointVo> flagLonLatMap = pointsLatLon.stream().collect(Collectors.toMap(DevicePointVo::getFlag, devicePointVo -> devicePointVo));
                        //放置屏蔽区域，基准区域，监控区域的经纬度
                        if (!monitorAreaLonLat.isEmpty()) {
                            monitorAreaLonLat.forEach(monitorArea -> {
                                List<AlgorithmMonitorPointVO> moniPointList = monitorArea.getMoniPointList();
                                if (!moniPointList.isEmpty()) {
                                    moniPointList.forEach(point -> {
                                        String flag = monitorArea.getMonitorAreaId() + "-" + point.getMonitorPointId();
                                        if (flagLonLatMap.containsKey(flag)) {
                                            DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                            point.setLat(pointLonLat.getLatitude());
                                            point.setLon(pointLonLat.getLongitude());
                                        }
                                    });
                                }
                            });
                            //转json 保存
                            confArea.setPointsLonLat(mapper2.writeValueAsString(monitorAreaLonLat));

                        }
                        //放置管线经纬度
                        if (!oilGasLonLat.isEmpty()) {
                            oilGasLonLat.forEach(oilGas -> {
                                List<DirectionVO> direction = oilGas.getDirection();
                                if (!direction.isEmpty()) {
                                    direction.forEach(point -> {
                                        String flag = oilGas.getOilGasId() + "-" + point.getPointId();
                                        if (flagLonLatMap.containsKey(flag)) {
                                            DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                            point.setLat(pointLonLat.getLatitude());
                                            point.setLon(pointLonLat.getLongitude());
                                        }
                                    });
                                }
                            });
                            //转json 保存
                            confArea.setOilGasPipelinesLonLat(mapper2.writeValueAsString(oilGasLonLat));
                        }
                        //放置绊线
                        if (!tripThreadListLonLat.isEmpty()) {
                            tripThreadListLonLat.forEach(tripThread -> {
                                List<ThreadLineVO> threadLine = tripThread.getThreadLine();
                                if (!threadLine.isEmpty()) {
                                    threadLine.forEach(point -> {
                                        String flag = tripThread.getThreadId() + LINE_SPLIT + point.getPointId();
                                        if (flagLonLatMap.containsKey(flag)) {
                                            DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                            point.setLat(pointLonLat.getLatitude());
                                            point.setLon(pointLonLat.getLongitude());
                                        }
                                    });
                                }
                                List<DirectionVO> direction = tripThread.getDirection();
                                if (!direction.isEmpty()) {
                                    direction.forEach(point -> {
                                        String flag = tripThread.getThreadId() + DIRECTION_SPLIT + point.getPointId();
                                        if (flagLonLatMap.containsKey(flag)) {
                                            DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                            point.setLat(pointLonLat.getLatitude());
                                            point.setLon(pointLonLat.getLongitude());
                                        }
                                    });
                                }
                            });
                            //转json 保存
                            confArea.setTripThreadLonLat(mapper2.writeValueAsString(tripThreadListLonLat));
                        }
                        //放置文本
                        if (!textLabelListLonLat.isEmpty()) {
                            textLabelListLonLat.forEach(textLabel -> {
                                if (null != textLabel) {
                                    String flag = textLabel.getPoint();
                                    if (flagLonLatMap.containsKey(flag)) {
                                        DevicePointVo pointLonLat = flagLonLatMap.get(flag);
                                        textLabel.setLat(pointLonLat.getLatitude());
                                        textLabel.setLon(pointLonLat.getLongitude());
                                    }
                                }
                            });
                            //转json 保存
                            confArea.setTextLabelsLonLat(mapper2.writeValueAsString(textLabelListLonLat));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("百分比坐标转换经纬度异常", e);
        }

    }

    /**
     * 后端算法 通道 批量修改  -----------------大屏算法组件使用
     *
     * @param list list
     */
    public void batchUpdateAlgorithm(List<AlgorithmConfigDTO> list) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        //漏洞改造 20221118 铁塔用户算法配置编辑按钮置灰，无法进行操作
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            throw new CustomException("铁塔账号无法进行算法配置编辑操作");
        }
        log.info("算法配置批量修改入参:" + GsonUtils.toJSONString(list));
        //查询对应设备、通道、算法 是否 配置预置位算法
        List<CheckAlgorithmConfigVO> checks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (AlgorithmConfigDTO vo : list) {
                List<AlgorithmAreaVO> algorithmAreaVOList = vo.getAlgorithms();
                if (!CollectionUtils.isEmpty(algorithmAreaVOList)) {
                    for (AlgorithmAreaVO algVO : algorithmAreaVOList) {
                        CheckAlgorithmConfigVO checkAlgorithmConfigVO = new CheckAlgorithmConfigVO();
                        checkAlgorithmConfigVO.setAlgorithmCode(algVO.getAlgorithmCode());
                        checkAlgorithmConfigVO.setChannelCode(algVO.getChannelCode());
                        checkAlgorithmConfigVO.setDeviceCode(algVO.getDeviceCode());
                        checkAlgorithmConfigVO.setTenantId(sysUser.getTenantId());
                        checkAlgorithmConfigVO.setIndustryCode(sysUser.getIndustryCode());
                        checks.add(checkAlgorithmConfigVO);
                    }
                }
            }
        }
        List<AlgorithmConfigVO> presetAlgorithmChecks = algorithmConfigMapper.getAlgorithmByList(checks, sysUser.getIndustryCode(), sysUser.getTenantId(), Constants.TWO_NUM, Constants.ZERO);
        Map<String, AlgorithmConfigVO> presetAlgorithmChecksMap = presetAlgorithmChecks.stream().collect(Collectors.toMap(t -> t.getChannelCode() + "-" + t.getAlgorithmCode(), t -> t));
        // 更新数据
        List<AlgorithmAreaVO> areaList = new ArrayList<>();
        List<AlgorithmConfigVO> configList = new ArrayList<>();
        list.forEach(item -> item.getAlgorithms().forEach(algo -> {
            //如果包含，代表该算法已经配置过预置位任务，则通道任务设置为 未启用
            if (presetAlgorithmChecksMap.containsKey(algo.getChannelCode() + "-" + algo.getAlgorithmCode())) {
                algo.setStatus(AlgorithmAreaVO.INVOKESTATUS);
            } else {
                algo.setStatus(AlgorithmAreaVO.ALGORITHMCLOSESTATUS);
            }
            algo.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
            algo.setIndustryCode(sysUser.getIndustryCode());
            algo.setModifier(SecurityUtils.getOperator());
            if (!CollectionUtils.isEmpty(algo.getMonitorArea())) {
                algo.setPoints(JSON.toJSONString(algo.getMonitorArea()));
            } else {
                algo.setPoints(DeviceSyncConstants.EMPTY_ARR);
            }
            //增加置信度、配置参数、伴线、算法运行时间、油气管线存表
            if (!CollectionUtils.isEmpty(algo.getModelConfigList())) {
                algo.setModelConfig(JSON.toJSONString(algo.getModelConfigList()));
            } else {
                algo.setModelConfig(null);
            }
            if (!CollectionUtils.isEmpty(algo.getTripThreadList())) {
                algo.setTripThread(JSON.toJSONString(algo.getTripThreadList()));
            } else {
                algo.setTripThread(null);
            }
            if (!CollectionUtils.isEmpty(algo.getOilGasPipelinesList())) {
                algo.setOilGasPipelines(JSON.toJSONString(algo.getOilGasPipelinesList()));
            } else {
                algo.setOilGasPipelines(null);
            }
            if (!CollectionUtils.isEmpty(algo.getAnalysisPeriodList())) {
                algo.setAnalysisPeriod(JSON.toJSONString(algo.getAnalysisPeriodList()));
            } else {
                algo.setAnalysisPeriod(null);
            }
            if (!CollectionUtils.isEmpty(algo.getTextLabelList())) {
                algo.setTextLabels(JSON.toJSONString(algo.getTextLabelList()));
            } else {
                algo.setTextLabels(null);
            }
            areaList.add(algo);
            AlgorithmConfigVO algorithmConfigVO = new AlgorithmConfigVO();
            algorithmConfigVO.setConfigId(algo.getConfigId());
            configList.add(algorithmConfigVO);
        }));

        List<AlgorithmAreaVO> areas = algorithmConfigMapper.getAlgorithmAreaRow2(null, configList);
        if (!CollectionUtils.isEmpty(areas)) {
            // 构建配置ID到AlgorithmAreaVO的映射，避免第一层嵌套循环
            Map<String, AlgorithmAreaVO> configIdToAreaMap = areas.stream()
                    .filter(area -> StringUtils.isNotEmpty(area.getPoints()))
                    .collect(Collectors.toMap(AlgorithmAreaVO::getConfigId, area -> area, (a, b) -> a));

            ObjectMapper mapper = new ObjectMapper();
            for (AlgorithmAreaVO algorithmAreaReq : areaList) {
                AlgorithmAreaVO algorithmAreaDB = configIdToAreaMap.get(algorithmAreaReq.getConfigId());
                if (algorithmAreaDB != null) {
                    // 提前解析JSON，避免重复解析
                    List<AlgorithmMonitorAreaVO> monitorAreaDB = JSON.parseArray(algorithmAreaDB.getPoints(), AlgorithmMonitorAreaVO.class);
                    // 构建监控类型到样式配置的映射，避免内层嵌套循环
                    Map<Integer, AlgorithmMonitorAreaVO> monitorTypeToStyleMap = monitorAreaDB.stream()
                            .collect(Collectors.toMap(AlgorithmMonitorAreaVO::getMonitorType, style -> style, (a, b) -> a));
                    List<AlgorithmMonitorAreaVO> monitorAreaReq = algorithmAreaReq.getMonitorArea();
                    for (AlgorithmMonitorAreaVO req : monitorAreaReq) {
                        AlgorithmMonitorAreaVO dbStyle = monitorTypeToStyleMap.get(req.getMonitorType());
                        if (dbStyle != null) {
                            req.setColor(dbStyle.getColor());
                            req.setFrameColor(dbStyle.getFrameColor());
                            req.setFrameWidth(dbStyle.getFrameWidth());
                        }
                    }

                    try {
                        algorithmAreaReq.setPoints(mapper.writeValueAsString(monitorAreaReq));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("JSON序列化失败", e);
                    }
                }
            }
        }

        if (!areaList.isEmpty()) {
            algorithmConfigMapper.updateAlgorithmAreaBatch(areaList);
        }
    }

    /**
     * 根据设备编号  通道编号 获取设备PTZ
     *
     * @param deviceCode  设备编号
     * @param channelCode 通道编号
     * @return ptz
     */
    private PTZPointDto getPtzByDeviceCodeAndChannelCode(String deviceCode, String channelCode) {

        VideoBaseReq videoBaseReq = new VideoBaseReq();
        videoBaseReq.setChannelCode(channelCode);
        videoBaseReq.setDeviceCode(deviceCode);
        Result<PresetData> presetDataResult = videoQueryAPI.queryPtzPosition(videoBaseReq);
        PresetData data = presetDataResult.getData();

        PTZPointDto centerPoint = new PTZPointDto();
        BeanUtils.copyProperties(data, centerPoint);
        return centerPoint;
    }

    /**
     * 根据moniPointList获取ptPoint
     *
     * @param moniPointList 百分比坐标点
     * @return PT坐标点
     */
    private List<PTZPointDto> getPtPoints(List<AlgorithmMonitorPointVO> moniPointList, String deviceCode, PTZPointDto centerPoint) {
        List<PTZPointDto> ptzPointDtoList = new ArrayList<>();
        //查询设备信息
        DeviceModelVO deviceModelVO = algorithmConfigMapper.getDeviceParam(deviceCode);
        moniPointList.forEach(item -> {
            PercentPointDto percentPointDto = new PercentPointDto();
            BeanUtils.copyProperties(item, percentPointDto);
            PTZPointDto ptzPointDto = LongLatAngleUtils.percentTransPt(deviceModelVO, percentPointDto, centerPoint);
            ptzPointDtoList.add(ptzPointDto);
        });
        return ptzPointDtoList;
    }


    /**
     * 功能描述: 算法配置启用停用接口
     *
     * @param: algorithmConfigDTO
     * @return:
     * @auther: zhaozj
     * @date: 2022/12/14
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void uptAlgorithmStatus(AlgorithmConfigDTO algorithmConfigDTO) {
        log.info("算法配置启用停用接口入参:" + JSON.toJSONString(algorithmConfigDTO));
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        //漏洞改造 20221118 铁塔用户算法配置启用按钮置灰，无法进行操作
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            throw new CustomException("铁塔账号无法进行算法配置启用停用操作");
        }

        if (algorithmConfigDTO.getConfigIdList().size() > DeviceSyncConstants.INT_50) {
            throw new CustomException(DeviceSyncConstants.BATCH_NUM_LIMIT_50);
        }
        if (CollectionUtils.isEmpty(algorithmConfigDTO.getConfigIdList())) {
            throw new CustomException(DeviceSyncConstants.PARAM_DATA_ERROR);
        }
        algorithmConfigDTO.setTenantId(sysUser.getTenantId());

        List<AlgorithmConfigVO> alfoList = algorithmConfigMapper.getAlgoConfigInfo(algorithmConfigDTO);
        if (!CollectionUtils.isEmpty(alfoList)) {
            List<AlgorithmConfigVO> foreEndList = alfoList.stream().filter(algo -> DeviceSyncConstants.TWO_STR.equals(algo.getTaskType())).collect(Collectors.toList());
            List<AlgorithmConfigVO> backEndList = alfoList.stream().filter(algo -> DeviceSyncConstants.ONE_STR.equals(algo.getTaskType())).collect(Collectors.toList());
            AlgorithmConfigDTO algoConfigVO = new AlgorithmConfigDTO();
            algoConfigVO.setModifier(SecurityUtils.getOperator());
            algoConfigVO.setStatus(algorithmConfigDTO.getStatus()); //0:启用 1:停用
            algoConfigVO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
            if (!CollectionUtils.isEmpty(backEndList)) {
                List<String> configIdList = backEndList.stream().map(AlgorithmConfigVO::getConfigId).collect(Collectors.toList());
                List<String> idList = new ArrayList<>(configIdList);
                String posType = "";

                Set<String> stopConfigIdList = new HashSet<>();
                //只有启用时，才需判断
                if (AlgorithmAreaVO.ALGORITHMCLOSESTATUS.equals(algorithmConfigDTO.getStatus())) {
                    //需要处理的算法信息详情
                    CheckAlgorithmConfigVO checkAlgorithmConfigVO = new CheckAlgorithmConfigVO();
                    checkAlgorithmConfigVO.setConfigIdList(configIdList);
                    List<AlgorithmConfigVO> algoConfigList = algorithmConfigMapper.getAlgorithmConfig(checkAlgorithmConfigVO);

                    if (Constants.ONE.equals(algorithmConfigDTO.getSingleFlag())) {
                        posType = algoConfigList.get(0).getPosType() + "";
                    }

                    //查询该租户、行业下所有的算法信息
                    checkAlgorithmConfigVO.setTenantId(sysUser.getTenantId());
                    checkAlgorithmConfigVO.setIndustryCode(sysUser.getIndustryCode());
                    checkAlgorithmConfigVO.setConfigIdList(null);
                    List<AlgorithmConfigVO> allConfigList = algorithmConfigMapper.getAlgorithmConfig(checkAlgorithmConfigVO);

                    configIdList = new ArrayList<>();
                    for (AlgorithmConfigVO configDO : algoConfigList) {
                        //当前算法为通道算法，若存在同一设备、通道、算法编码的预置位算法为启用，则该算法不可启用
                        if (configDO.getPosType() == DeviceConstants.ONE_NUM) {
                            boolean ifAlgo = allConfigList.stream().filter(algo ->
                                    configDO.getDeviceCode().equals(algo.getDeviceCode()) &&
                                            configDO.getChannelCode().equals(algo.getChannelCode()) &&
                                            configDO.getAlgorithmCode().equals(algo.getAlgorithmCode()) &&
                                            DeviceConstants.TWO_NUM == algo.getPosType() &&
                                            DeviceSyncConstants.ZERO_STR.equals(algo.getStatus())
                            ).findAny().isPresent();
                            if (!ifAlgo) {
                                configIdList.add(configDO.getConfigId());
                                configDO.setStatus(DeviceConstants.ZERO);
                                allConfigList.add(configDO);
                            }
                        }

                        //当前算法为预置位算法，若存在同一设备、通道、算法编码的通道算法为启用，则该算法不可启用
                        if (configDO.getPosType() == DeviceConstants.TWO_NUM) {
                            if (Constants.ONE.equals(algorithmConfigDTO.getIsAiSave())) {
                                // 停用相同算法编码的通道算法任务
                                AlgorithmConfigVO vo = allConfigList.stream()
                                        .filter(algo ->
                                                configDO.getDeviceCode().equals(algo.getDeviceCode()) &&
                                                        configDO.getChannelCode().equals(algo.getChannelCode()) &&
                                                        configDO.getAlgorithmCode().equals(algo.getAlgorithmCode()) &&
                                                        DeviceConstants.ONE_NUM == algo.getPosType() &&
                                                        DeviceSyncConstants.ZERO_STR.equals(algo.getStatus()))
                                        .findFirst().orElse(null);
                                if (vo != null) {
                                    //停用通道算法，启用预置位算法
                                    stopConfigIdList.add(vo.getConfigId());
                                }
                                configIdList.add(configDO.getConfigId());
                                configDO.setStatus(DeviceConstants.ZERO);
                                allConfigList.add(configDO);
                            } else {
                                boolean ifPre = allConfigList.stream().filter(algo ->
                                        configDO.getDeviceCode().equals(algo.getDeviceCode()) &&
                                                configDO.getChannelCode().equals(algo.getChannelCode()) &&
                                                configDO.getAlgorithmCode().equals(algo.getAlgorithmCode()) &&
                                                DeviceConstants.ONE_NUM == algo.getPosType() &&
                                                DeviceSyncConstants.ZERO_STR.equals(algo.getStatus())
                                ).findAny().isPresent();

                                if (!ifPre) {
                                    configIdList.add(configDO.getConfigId());
                                    configDO.setStatus(DeviceConstants.ZERO);
                                    allConfigList.add(configDO);
                                }
                            }
                        }
                    }

                    //查询启用的任务中，是否存在大模型算法任务
                    List<String> strings = algorithmConfigMapper.addLargeModelAlgorithms(idList);
                    if(!CollectionUtils.isEmpty(strings)){
                        configIdList.addAll(strings);
                    }
                }

                //停用通道算法，启用预置位算法
                if (!CollectionUtils.isEmpty(stopConfigIdList)) {
                    AlgorithmConfigDTO algoVO = new AlgorithmConfigDTO();
                    algoVO.setStatus(AlgorithmAreaVO.INVOKESTATUS);
                    algoVO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
                    algoVO.setConfigIdList(new ArrayList<>(stopConfigIdList));
                    algoVO.setModifier(SecurityUtils.getOperator());
                    algorithmConfigMapper.uptBackStatusBatch(algoVO);
                }

                algoConfigVO.setConfigIdList(configIdList);
                //批量更新后端算法信息
                if (!CollectionUtils.isEmpty(configIdList)) {
                    algorithmConfigMapper.uptBackStatusBatch(algoConfigVO);
                }

                if (Constants.ONE.equals(algorithmConfigDTO.getSingleFlag()) && CollectionUtils.isEmpty(configIdList)) {
                    if (Constants.ONE.equals(posType)) {
                        throw new CustomException(DeviceSyncConstants.EXIST_CHAN_ALGO_CANNOT_ENABLE);
                    } else {
                        throw new CustomException(DeviceSyncConstants.EXIST_PRESET_ALGO_CANNOT_ENABLE);
                    }
                }
            }

            if (!CollectionUtils.isEmpty(foreEndList)) {
                algoConfigVO.setConfigIdList(foreEndList.stream().map(AlgorithmConfigVO::getConfigId).collect(Collectors.toList()));
                //批量更新前端算法信息
                algorithmConfigMapper.uptForeStatusBatch(algoConfigVO);
                //前端算法整合方法
                Set<DeviceChannelVO> devSet = new HashSet<>();
                for (AlgorithmConfigVO item : foreEndList) {
                    DeviceChannelVO vo = new DeviceChannelVO();
                    vo.setChannelCode(item.getChannelCode());
                    vo.setDeviceCode(item.getDeviceCode());
                    devSet.add(vo);
                }
                List<DeviceChannelVO> deviceChannelList = new ArrayList<>();
                deviceChannelList.addAll(devSet);
                algorithmGiveConfigService.algorithmGiveConfigMerge(deviceChannelList);
            }
        }
    }


    /**
     * 功能描述: 批量删除算法信息
     *
     * @param: algorithmConfigDTO
     * @return:
     * @auther: zhaozj
     * @date: 2022/12/14
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> delete(AlgorithmConfigDTO algorithmConfigDTO) {
        log.info("算法配置删除入参:" + JSON.toJSONString(algorithmConfigDTO));
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        //漏洞改造 20221118 铁塔用户删除按钮置灰，无法进行操作
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            return Result.fail("铁塔账号无法进行算法配置删除操作");
        }
        if (algorithmConfigDTO.getConfigIdList().size() > DeviceSyncConstants.INT_50) {
            return Result.fail(DeviceSyncConstants.BATCH_NUM_LIMIT_50);
        }
        if (CollectionUtils.isEmpty(algorithmConfigDTO.getConfigIdList())) {
            return Result.fail(DeviceSyncConstants.PARAM_DATA_ERROR);
        }
        algorithmConfigDTO.setTenantId(sysUser.getTenantId());

        List<AlgorithmConfigVO> alfoList = algorithmConfigMapper.getAlgoConfigInfo(algorithmConfigDTO);
        if (!CollectionUtils.isEmpty(alfoList)) {
            //判断是否存在启用状态得算法
            boolean delFlag = alfoList.stream().filter(item -> item.getStatus().equals(DeviceSyncConstants.ZERO_STR)).findAny().isPresent();
            //只保留停用得算法，进行删除
            List<AlgorithmConfigVO> stopList = alfoList.stream().filter(algo -> DeviceSyncConstants.ONE_STR.equals(algo.getStatus())).collect(Collectors.toList());

            List<AlgorithmConfigVO> foreEndList = stopList.stream().filter(algo -> DeviceSyncConstants.TWO_STR.equals(algo.getTaskType())).collect(Collectors.toList());
            List<AlgorithmConfigVO> backEndList = stopList.stream().filter(algo -> DeviceSyncConstants.ONE_STR.equals(algo.getTaskType())).collect(Collectors.toList());
            AlgorithmConfigDTO algoConfigVO = new AlgorithmConfigDTO();
            algoConfigVO.setModifier(SecurityUtils.getOperator());
            if (!CollectionUtils.isEmpty(backEndList)) {
                List<String> configIdList = backEndList.stream().map(AlgorithmConfigVO::getConfigId).collect(Collectors.toList());
                algoConfigVO.setConfigIdList(configIdList);
                //批量删除后端算法信息
                algorithmConfigMapper.delBackConfigBatch(algoConfigVO);

                //批量删除后端算法对应的图斑关系数据,vm_polygon_patch_algo_area_rela表
                polygonMapper.delPolygonPatchAlgoAreaBatch(configIdList);
            }

            if (!CollectionUtils.isEmpty(foreEndList)) {
                algoConfigVO.setConfigIdList(foreEndList.stream().map(AlgorithmConfigVO::getConfigId).collect(Collectors.toList()));
                //批量删除前端算法信息
                algorithmConfigMapper.delForeConfigBatch(algoConfigVO);
            }

            if (delFlag) {
                return Result.fail("请停用算法任务后再进行删除。");
            }
        } else {
            return Result.fail(DeviceSyncConstants.PARAM_DATA_ERROR);
        }

        return Result.success("算法配置删除成功!");
    }


    @Override
    public List<AlgorithmAreaVO> queryAlgorithm(AlgorithmConfigDTO algorithmConfigDTO) {
        if (StringUtils.isBlank(algorithmConfigDTO.getDeviceCode()) && StringUtils.isBlank(algorithmConfigDTO.getChannelCode())) {
            throw new CustomException("设备及通道编码不得为空");
        }
        if (ObjectUtils.isNotEmpty(algorithmConfigDTO.getPageNum()) && ObjectUtils.isNotEmpty(algorithmConfigDTO.getPageSize())) {
            PageMethod.startPage(algorithmConfigDTO.getPageNum(), algorithmConfigDTO.getPageSize());
        }
        if(algorithmConfigDTO.getIsHasBigModel() == Constants.ZERO_NUM){
            List<String> bigModelAlgList = this.getBigModelAlgList();
            algorithmConfigDTO.setBigModelAlgList(bigModelAlgList);
        }
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String industryCode = sysUser.getIndustryCode();
        algorithmConfigDTO.setIndustryCode(industryCode);
        algorithmConfigDTO.setTenantId(sysUser.getTenantId());
        List<AlgorithmAreaVO> areas = algorithmConfigMapper.getAlgorithmAreaRow(algorithmConfigDTO);
        if (!CollectionUtils.isEmpty(areas)) {
            Set<String> fileUrls = areas.stream().map(AlgorithmAreaVO::getImg).collect(Collectors.toSet());
            // 处理Obs文件URL加密返回
            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrls));
            String fileUrl;
            for (AlgorithmAreaVO f : areas) {
                fileUrl = f.getImg();
                if (StringUtils.isNotEmpty(fileUrl)) {
                    f.setImg(result.get(fileUrl));
                }
            }
        }
        return areas;
    }

    @Override
    public List<AlgorithmGroupDeviceVO> queryAlgorithmBatch(AlgorithmConfigDTO algorithmConfigDTO) {
        List<AlgorithmGroupDeviceVO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(algorithmConfigDTO.getDeviceChannelList())) {
            throw new CustomException("设备及通道编码不得为空");
        }
        Integer pageSize = ObjectUtils.isEmpty(algorithmConfigDTO.getPageSize()) ? 10 : algorithmConfigDTO.getPageSize();
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String industryCode = sysUser.getIndustryCode();
        algorithmConfigDTO.setIndustryCode(industryCode);
        algorithmConfigDTO.setTenantId(sysUser.getTenantId());
        List<AlgorithmAreaVO> areas = algorithmConfigMapper.getAlgorithmAreaRow(algorithmConfigDTO);
        if (!CollectionUtils.isEmpty(areas)) {
            Set<String> fileUrls = areas.stream().map(AlgorithmAreaVO::getImg).collect(Collectors.toSet());
            // 处理Obs文件URL加密返回
            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrls));
            String fileUrl;
            for (AlgorithmAreaVO f : areas) {
                fileUrl = f.getImg();
                if (StringUtils.isNotEmpty(fileUrl)) {
                    f.setImg(result.get(fileUrl));
                }
            }
        }
        Map<String, List<AlgorithmAreaVO>> map = areas.stream().collect(Collectors.groupingBy(
                item -> item.getDeviceCode() + Constants.UNDER_LINE + item.getChannelCode()));
        for (Map.Entry<String, List<AlgorithmAreaVO>> stringListEntry : map.entrySet()) {
            String[] split = stringListEntry.getKey().split(Constants.UNDER_LINE);
            AlgorithmGroupDeviceVO algorithmGroupDeviceVO = new AlgorithmGroupDeviceVO();
            algorithmGroupDeviceVO.setDeviceCode(split[Constants.ZERO_NUM]);
            algorithmGroupDeviceVO.setChannelCode(split[Constants.ONE_NUM]);

            List<AlgorithmAreaVO> value = stringListEntry.getValue();
            //按照创建时间排序
            Collections.sort(value, Comparator.comparing(e ->
                    com.chinatower.event.util.DateUtils.string2Date(e.getCreateTime()).getTime()));
            //倒序处理
            Collections.reverse(value);
            algorithmGroupDeviceVO.setTotal(value.size());
            algorithmGroupDeviceVO.setRows(value.subList(0, Math.min(value.size(), pageSize)));
            resultList.add(algorithmGroupDeviceVO);
        }

        return resultList;
    }


    @Override
    public Result synchAlgorithmConfigStatus(String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            throw new CustomException("任务编码不得为空");
        }
        Result<JSONObject> res = null;
        try {
            NetConnectUtils.RequestParamter<String> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody("");
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNCH_ALGORITHM_CONFIG_STATUS);
            // 接口路径参数
            path.setTaskId(taskId);
            paramter.setPath(path);
            // 属地化数据统一改造 add by wangyt 20240118
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByTaskId(taskId);
            log.info(VideoConstants.DO_SELECT_FROM_TO_BY_TASK_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if (casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED) {
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(VideoConstants.SYNCH_ALGORITHM_CONFIG_STATUS_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.SYNCH_ALGORITHM_CONFIG_STATUS_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        } catch (Exception e) {
            log.error(VideoConstants.SYNCH_ALGORITHM_CONFIG_STATUS_ERROR + "===:{}", e);
        }
        if (ResultCode.SUCCESS.getCode() == res.getCode()) {
            return Result.data(res.getData().get("data"));
        } else {
            throw new CustomException(res.getMsg());
        }
    }

    @Override
    public void delChannelByCodes(List<String> delCollect) {
        algorithmConfigMapper.delChannelByCodes(delCollect);
    }


    @Override
    public List<AlgorithmRankDto> getRank(AlgorithmRankVo algorithmRankVo) {
        return algorithmConfigMapper.getRank(algorithmRankVo);
    }

    @Override
    public Result<AlgorithmAppDTO> queryAlgorithmApp(AlgorithmConfigDTO algorithmConfigDTO) {
        algorithmConfigDTO.setTaskType(DeviceSyncConstants.ONE_STR);
        List<AlgorithmAreaVO> areas = queryAlgorithm(algorithmConfigDTO);
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setDeviceCode(algorithmConfigDTO.getDeviceCode());
        customerDTO.setTenantId(algorithmConfigDTO.getTenantId());
        customerDTO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        Result<List<OrderAlgorithmsVO>> listResult = iVideoOrderService.queryAlgorithmList(customerDTO);
        List<OrderAlgorithmsVO> orderAlgorithmsVOList = listResult.getData();
        List<OrderAlgorithmsVO> orderAlgorithmsVOListResult = new ArrayList<>();
        for (OrderAlgorithmsVO vo : orderAlgorithmsVOList) {
            boolean flag = true;
            for (AlgorithmAreaVO ao : areas) {
                if (vo.getAlgorithmCode().equals(ao.getAlgorithmCode())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                orderAlgorithmsVOListResult.add(vo);
            }
        }

        //组装算法编码集合
        Set<String> set = new HashSet<>();
        if (!CollectionUtils.isEmpty(areas)) {
            set.addAll(areas.stream().map(AlgorithmAreaVO::getAlgorithmCode).collect(Collectors.toSet()));
        }
        if (!CollectionUtils.isEmpty(orderAlgorithmsVOListResult)) {
            set.addAll(orderAlgorithmsVOListResult.stream().map(OrderAlgorithmsVO::getAlgorithmCode).collect(Collectors.toSet()));
        }

        if (!CollectionUtils.isEmpty(set)) {
            //查询算法对应的描述、图标信息
            List<FunctionAlgorthmVO> functionInfo = algorithmConfigMapper.getFunctionInfo(set);

            //已配置-匹配算法描述、图标code、图标颜色
            areas.stream().map(item -> {
                functionInfo.stream().filter(area -> Objects.equals(item.getAlgorithmCode(), area.getAlgorithmCode())
                ).forEach(area -> {
                    item.setAlgorithmDescription(area.getAlgorithmDescription());
                    item.setIconCode(area.getIconCode());
                    item.setIconColour(area.getIconColour());
                });
                return item;
            }).collect(Collectors.toList());

            //未配置-匹配算法描述、图标code、图标颜色
            orderAlgorithmsVOListResult.stream().map(item -> {
                functionInfo.stream().filter(area -> Objects.equals(item.getAlgorithmCode(), area.getAlgorithmCode())
                ).forEach(area -> {
                    item.setAlgorithmDescription(area.getAlgorithmDescription());
                    item.setIconCode(area.getIconCode());
                    item.setIconColour(area.getIconColour());
                });
                return item;
            }).collect(Collectors.toList());
        }

        AlgorithmAppDTO algorithmAppDTO = new AlgorithmAppDTO();
        algorithmAppDTO.setAlgorithmAreaVOS(areas);
        algorithmAppDTO.setOrderAlgorithmsVOS(orderAlgorithmsVOListResult);
        return Result.data(algorithmAppDTO);
    }


    /**
     * 生成32位UUID
     *
     * @return
     * @author: liujb ,wangsw
     * @date: 2021/7/6
     */
    String getUUID() {
        return UUID.randomUUID().toString().replace(DeviceSyncConstants.HORIZON_LINE, "");
    }

    /**
     * 功能描述: 算法配置--查询算法数量详细信息
     *
     * @param algorithmConfigDTO
     * @return List<AlgorithmListVO>
     * @auther: zhaozj
     * @date: 2022/12/08
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    @Override
    public List<OrderAlgorithmsVO> getAlgoCountInfo(AlgorithmConfigDTO algorithmConfigDTO) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        algorithmConfigDTO.setTenantId(sysUser.getTenantId());
        algorithmConfigDTO.setIndustryCode(sysUser.getIndustryCode());
        algorithmConfigDTO.setPlatformCode(sysUser.getPlatformCode());

        PageHelper.startPage(algorithmConfigDTO.getPageNum(), algorithmConfigDTO.getPageSize());
        //1已订购算法数量;2已配置算法数量;3待配置算法数量
        if (Constants.ONE.equals(algorithmConfigDTO.getCountType())) {
            return algorithmConfigMapper.getOrderAlgorithmInfo(algorithmConfigDTO);
        }
        if (Constants.TWO.equals(algorithmConfigDTO.getCountType())) {
            return algorithmConfigMapper.getAlgorithmConfigInfo(algorithmConfigDTO);
        }
        if (Constants.THREE.equals(algorithmConfigDTO.getCountType())) {
            return algorithmConfigMapper.getToBeConfigInfo(algorithmConfigDTO);
        }
        return null;
    }

    /**
     * 功能描述 查询订购算法列表
     *
     * @param customerDTO
     * @return
     * @author chensw
     * @date 2022/2/11
     */
    @Override
    public List<OrderAlgorithmsVO> queryAlgorithmList(CustomerDTO customerDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String industryCode = user.getIndustryCode();
        String platformCode = user.getPlatformCode();
        String tenantId = customerDTO.getTenantId();
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = user.getTenantId();
        }
        List<OrderAlgorithmsVO> resultList = new ArrayList<>();
        List<OrderAlgorithmsVO> algList = algorithmConfigMapper.getCustomerAlgorithm(
                tenantId,
                customerDTO.getDeviceCode(),
                industryCode,
                platformCode,
                customerDTO.getAlgorithmName());
        List<String> enumCodeList = new ArrayList<>();
        //获取赠送算法
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(OrderConstants.SYS_GIVE_ALGORITHM);
        List<SysDictDataResponse> dictList = sysResult.getData();
        if (!CollectionUtils.isEmpty(dictList)) {
            for (SysDictDataResponse sysDictData : dictList) {
                enumCodeList.add(sysDictData.getDictValue());
            }
        }
        //过滤前端算法编码
        for (OrderAlgorithmsVO vo : algList) {
            String algCode = vo.getAlgorithmCode();
            if (!org.springframework.util.StringUtils.isEmpty(algCode) && !enumCodeList.contains(algCode.substring(0, 2))) {
                resultList.add(vo);
            }
        }

        //判断算法是否是联动算法
        List<LinkageAlgorithmMappingVO> linkageAlgorithmMappingVOS = this.queryLinkageAlgorithmList(new LinkageAlgorithmMappingVO());
        if (!CollectionUtils.isEmpty(linkageAlgorithmMappingVOS)) {
            resultList.stream().map(item -> {
                linkageAlgorithmMappingVOS.stream().filter(area -> Objects.equals(item.getAlgorithmCode(), area.getAlgorithmCode())
                ).forEach(area -> {
                    item.setIsLinkageAlgorithm(Constants.ONE);
                });
                return item;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    @Override
    public List<OrderAlgorithmsVO> queryBaselineAlgorithmList(CustomerDTO customerDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String industryCode = user.getIndustryCode();
        String platformCode = user.getPlatformCode();
        String tenantId = customerDTO.getTenantId();
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = user.getTenantId();
        }
        List<OrderAlgorithmsVO> resultList = algorithmConfigMapper.getCustomerAlgorithm(
                tenantId,
                customerDTO.getDeviceCode(),
                industryCode,
                platformCode,
                null);
        Iterator<OrderAlgorithmsVO> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            OrderAlgorithmsVO orderAlgorithmsVO = iterator.next();
            String extendJson = orderAlgorithmsVO.getExtendJson();
            if (!StringUtils.isEmpty(extendJson)) {
                ExtendJson ej = JSONObject.parseObject(extendJson, ExtendJson.class);
                if (!ObjectUtils.isEmpty(ej)) {
                    if (!Constants.ONE.equals(ej.getSupportBaselinePic())) {
                        iterator.remove();
                    }
                }
            } else {
                iterator.remove();
            }
        }
        return resultList;
    }

    @Override
    public Map<String, List<OrderAlgorithmsVO>> queryBaselineAlgorithmListBatch(List<String> deviceCodeList) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<OrderAlgorithmsVO> resultList = algorithmConfigMapper.getCustomerAlgorithmBatch(user.getTenantId(), deviceCodeList, user.getIndustryCode(), user.getPlatformCode());
        Map<String, List<OrderAlgorithmsVO>> map = resultList.stream().collect(Collectors.groupingBy(
                OrderAlgorithmsVO::getDeviceCode));

        map.forEach((k, v) -> {
            List<OrderAlgorithmsVO> list = v.stream().filter(item -> {
                String extendJson = item.getExtendJson();
                if (!StringUtils.isEmpty(extendJson)) {
                    ExtendJson ej = JSONObject.parseObject(extendJson, ExtendJson.class);
                    if (!ObjectUtils.isEmpty(ej)) {
                        return Constants.ONE.equals(ej.getSupportBaselinePic());
                    }
                }
                return false;
            }).collect(Collectors.toList());
            map.put(k, list);
        });
        return map;
    }

    @Override
    public Result<String> checkAlgorithmConfig(CheckAlgorithmConfigVO checkAlgorithmConfigVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkAlgorithmConfigVO.setTenantId(user.getTenantId());
        checkAlgorithmConfigVO.setIndustryCode(user.getIndustryCode());
        List<AlgorithmConfigVO> algorithmConfigList = algorithmConfigMapper.getAlgorithmConfig(checkAlgorithmConfigVO);
        if (!CollectionUtils.isEmpty(algorithmConfigList)) {
            List<String> presetIdList = checkAlgorithmConfigVO.getPresetIdList();
            //算法归属：通道
            if (CollectionUtils.isEmpty(presetIdList)) {
                boolean booChan = algorithmConfigList.stream().filter(algo -> algo.getPosType() == DeviceConstants.ONE_NUM).findAny().isPresent();
                if (booChan) {
                    return Result.fail("已存在该算法的配置，请不要重复配置");
                }
            }
            //算法归属：预置位
            else {
                boolean isSame = false;
                for (AlgorithmConfigVO algorithmConfigVO : algorithmConfigList) {
                    if (algorithmConfigVO.getPosType() == DeviceConstants.TWO_NUM
                            && presetIdList.contains(String.valueOf(algorithmConfigVO.getPresetId()))) {
                        isSame = true;
                        break;
                    }
                }
                if (isSame) {
                    return Result.fail("该算法已配置了该预置位算法任务，无法再配置该预置位算法任务，请删除该预置位算法任务后再试。");
                }
            }
        }
        return Result.success("校验通过");
    }

    @Override
    @DataScope(tenantColumn = "t1.tenant_id", acceptColumn = "t1.accept_node", industryCode = "t1.industry_code", alarmSourceColumn = "t4.alarm_source", typeValueColumn = "t4.type_value")
    public List<IndustryAlgorithmDTO> queryByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        List<IndustryAlgorithmDTO> algorithms;
        if (null == industryAlgorithmQueryVo.getPageSize() || null == industryAlgorithmQueryVo.getPageNum()) {
            industryAlgorithmQueryVo.setPageNum(1);
            industryAlgorithmQueryVo.setPageSize(10);
        }
        PageHelper.startPage(industryAlgorithmQueryVo.getPageNum(), industryAlgorithmQueryVo.getPageSize());
        if ((StringUtils.isEmpty(industryAlgorithmQueryVo.getDeviceCode()) && null == industryAlgorithmQueryVo.getDeviceCodeList())
                || !StringUtils.isEmpty(industryAlgorithmQueryVo.getAppScenarios())) {
            algorithms = algorithmInfoMapper.queryByTenantId(industryAlgorithmQueryVo);
        } else {
            algorithms = algorithmInfoMapper.queryByTenantIdAndDevice(industryAlgorithmQueryVo);
        }
        return algorithms;
    }

    @Override
    @DataScope(tenantColumn = "t5.tenant_id", industryCode = "t5.industry_code")
    public Map<String, List<IndustryAlgorithmDTO>> queryByTenantIdAndDevices(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        //修改为查询绑定的后端算法 + 前端算法
        List<IndustryAlgorithmDTO> algorithms = queryAllByTenantId(industryAlgorithmQueryVo);
        Map<String, List<IndustryAlgorithmDTO>> map = new HashMap<>();
        algorithms.forEach(item -> {
            if (map.containsKey(item.getDeviceCode())) {
                map.get(item.getDeviceCode()).add(item);
            } else {
                List<IndustryAlgorithmDTO> list = new ArrayList<>();
                list.add(item);
                map.put(item.getDeviceCode(), list);
            }
        });
        return map;
    }

    @Override
    public List<IndustryAlgorithmDTO> queryAllByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        List<IndustryAlgorithmDTO> configList = queryConfigByTenantId(industryAlgorithmQueryVo);
        List<IndustryAlgorithmDTO> givenList = queryGivenByTenantId(industryAlgorithmQueryVo);
        configList.addAll(givenList);
        return configList;
    }

    @Override
    @DataScope(tenantColumn = "t5.tenant_id", acceptColumn = "t1.accept_node", industryCode = "t5.industry_code")
    public List<IndustryAlgorithmDTO> queryConfigByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        return algorithmInfoMapper.queryConfigByTenantId(industryAlgorithmQueryVo);
    }

    @Override
    @DataScope(tenantColumn = "t5.tenant_id", acceptColumn = "t1.accept_node", industryCode = "t5.industry_code")
    public List<IndustryAlgorithmDTO> queryGivenByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        return algorithmInfoMapper.queryGivenByTenantId(industryAlgorithmQueryVo);
    }

    @Override
    public Set<OrderAlgorithmsVO> intersection(AlgorithmIntersectionVO intersection) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        intersection.setIndustryCode(user.getIndustryCode());
        intersection.setTenantId(user.getTenantId());
        intersection.setAcceptNode(user.getPlatformCode());

        List<OrderAlgorithmsVO> algorithms = algorithmConfigMapper.getAlgorithmConfigByDeviceList(intersection);
        List<String> enumCodeList = new ArrayList<>();
        //获取赠送算法
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(OrderConstants.SYS_GIVE_ALGORITHM);
        List<SysDictDataResponse> dictList = sysResult.getData();
        if (!CollectionUtils.isEmpty(dictList)) {
            for (SysDictDataResponse sysDictData : dictList) {
                enumCodeList.add(sysDictData.getDictValue());
            }
        }
        //过滤前端算法编码
        Iterator<OrderAlgorithmsVO> iterator = algorithms.iterator();
        if (iterator.hasNext()) {
            OrderAlgorithmsVO next = iterator.next();
            String algCode = next.getAlgorithmCode();
            if (!org.springframework.util.StringUtils.isEmpty(algCode) && enumCodeList.contains(algCode.substring(0, 2))) {
                iterator.remove();
            }
        }
        Map<String, List<OrderAlgorithmsVO>> map = new HashMap<>();
        // 先用请求的设备编码初始化map，确保无算法的设备也存在空列表，
        // 这样求交集时只要有一方为空，结果即为空（[x,y] ∩ [] = []）
        if (intersection.getDeviceCodes() != null) {
            intersection.getDeviceCodes().forEach(code -> map.putIfAbsent(code, new ArrayList<>()));
        }
        algorithms.forEach(item -> map.computeIfAbsent(item.getDeviceCode(), k -> new ArrayList<>()).add(item));
        log.info("map->{}", JSON.toJSONString(map));
        ArrayList<List<OrderAlgorithmsVO>> values = new ArrayList<>(map.values());

        Set<OrderAlgorithmsVO> result = getIntersection(values, OrderAlgorithmsVO::getAlgorithmCode);
        // 排除大模型算法，只返回小模型（参考 queryAlgorithmList）
        List<String> bigModelAlgList = this.getBigModelAlgList();
        if (!CollectionUtils.isEmpty(bigModelAlgList)) {
            result.removeIf(vo -> bigModelAlgList.contains(vo.getAlgorithmCode()));
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> batchAddOrUpdate(List<AlgorithmListVO> list) {

        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<CheckAlgorithmConfigVO> checks = new ArrayList<>();
        //获取所有算法
        list.forEach(item -> item.getPositions().forEach(position -> {
            //算法
            position.getAlgorithms().forEach(algo -> {
                CheckAlgorithmConfigVO checkAlgorithmConfigVO = new CheckAlgorithmConfigVO();
                checkAlgorithmConfigVO.setAlgorithmCode(algo.getAlgorithmCode());
                checkAlgorithmConfigVO.setChannelCode(item.getChannelCode());
                checkAlgorithmConfigVO.setDeviceCode(item.getDeviceCode());
                checkAlgorithmConfigVO.setTenantId(user.getTenantId());
                checkAlgorithmConfigVO.setIndustryCode(user.getIndustryCode());
                checks.add(checkAlgorithmConfigVO);
            });
        }));

        if (CollectionUtil.isNotEmpty(checks)) {

            //获取参数，构建数据
            List<AlgorithmInfoDO> algorithmInfos = algorithmConfigMapper.getAlgorithmInfoBatch(checks);
            Map<String, AlgorithmInfoDO> algorithmInfoMap = algorithmInfos.stream().collect(Collectors.toMap(AlgorithmInfoDO::getAlgorithmCode, item -> item));


            Map<String, CheckAlgorithmConfigVO> imgMap = checks.stream().filter(distinctByKey(CheckAlgorithmConfigVO::getChannelCode))
                    .collect(Collectors.toMap(CheckAlgorithmConfigVO::getChannelCode, item -> item));
            //多线程提前获取抓图
            List<CheckAlgorithmConfigVO> values = new ArrayList<>();
            imgMap.forEach((key1, value) -> values.add(value));

            ThreadParallelUtil.run(values, it -> {
                com.chinatower.video.api.entity.PresetDTO preset = new com.chinatower.video.api.entity.PresetDTO();
                preset.setChannelCode(it.getChannelCode());
                preset.setDeviceCode(it.getDeviceCode());
                Result<com.chinatower.video.api.entity.PresetVO> presetImg = iVideoPresetServicei.getPresetImg(preset);
                if (presetImg.getCode() == ResultCode.SUCCESS.getCode()) {
                    com.chinatower.video.api.entity.PresetVO data = presetImg.getData();
                    imgMap.get(it.getChannelCode()).setImg(data.getImgUrl());
                }
                return null;
            }, ThreadParallelUtil.ConfigFactory.ALGORITHM_CONFIG_STATUS);

            log.info("img map:{}", JSON.toJSONString(imgMap));

            //判断 ------------------------------新增 删除
            List<AlgorithmListVO> addList = new ArrayList<>();

            List<AlgorithmConfigDTO> updateList = new ArrayList<>();


            List<AlgorithmConfigVO> algorithmConfigList = algorithmConfigMapper.getAlgorithmByList(checks, user.getIndustryCode(), user.getTenantId(), 1, "");

            Map<String, AlgorithmConfigVO> algoMap = algorithmConfigList.stream()
                    .collect(Collectors.toMap(item -> item.getDeviceCode() + "-" + item.getChannelCode() + "-" + item.getAlgorithmCode(), item -> item));


            //获取所有算法
            list.forEach(item -> {
                //新增
                AlgorithmListVO addAlgorithm = new AlgorithmListVO();
                BeanUtils.copyProperties(item, addAlgorithm, "positions");


                List<AlgorithmConfigVO> positions = new ArrayList<>();
                item.getPositions().forEach(position -> {


                    List<AlgorithmAreaVO> addAlgorithms = new ArrayList<>();

                    //算法
                    position.getAlgorithms().forEach(algo -> {
                        String key = item.getDeviceCode() + "-" + item.getChannelCode() + "-" + algo.getAlgorithmCode();
                        AlgorithmAreaVO algorithmArea = new AlgorithmAreaVO();
                        BeanUtils.copyProperties(algo, algorithmArea);
                        //设置新增
                        algorithmArea.setOptType(1);
                        //设置图片
                        algorithmArea.setImg(imgMap.get(item.getChannelCode()).getImg());

                        //--------设置算法参数
                        AlgorithmInfoDO algorithmInfoDO = algorithmInfoMap.get(algorithmArea.getAlgorithmCode());
                        //设置算法运行时段
                        if (1 == algorithmInfoDO.getAnalyType()) {
                            List<AnalysisPeriodVO> analysisPeriodList = new ArrayList<>();
                            AnalysisPeriodVO analysisPeriod = new AnalysisPeriodVO();

                            String idEnd = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                            analysisPeriod.setPeriodId(System.currentTimeMillis() + idEnd);
                            analysisPeriod.setStartTime("00:00:00");
                            analysisPeriod.setEndTime("23:59:59");
                            analysisPeriodList.add(analysisPeriod);
                            algorithmArea.setAnalysisPeriodList(analysisPeriodList);
                        }
                        //算法参数
                        if (StringUtils.isNotEmpty(algorithmInfoDO.getModelConfig())) {
                            List<ModelConfigVO> modelConfigList = JSONObject.parseArray(algorithmInfoDO.getModelConfig(), ModelConfigVO.class);
                            algorithmArea.setModelConfigList(modelConfigList);
                        } else {
                            algorithmArea.setModelConfigList(new ArrayList<>());
                        }

                        algorithmArea.setTripThreadList(new ArrayList<>());
                        algorithmArea.setOilGasPipelinesList(new ArrayList<>());

                        //monitor area
                        if (ObjectUtil.isEmpty(algorithmArea.getMonitorArea())) {
                            AlgorithmMonitorAreaVO algorithmMonitorArea = new AlgorithmMonitorAreaVO();
                            algorithmMonitorArea.setMonitorType(0);
                            String idEnd = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                            algorithmMonitorArea.setMonitorAreaId(System.currentTimeMillis() + "999" + idEnd);
                            algorithmMonitorArea.setMoniPointList(getAlgorithmMonitorPointInit());
                            List<AlgorithmMonitorAreaVO> monitorAreaList = Collections.singletonList(algorithmMonitorArea);
                            algorithmArea.setMonitorArea(monitorAreaList);
                        }

                        if (algoMap.containsKey(key)) {
                            List<AlgorithmAreaVO> updateAlgorithms = new ArrayList<>();
                            //需要修改的数据
                            algorithmArea.setConfigId(algoMap.get(key).getConfigId());
                            algorithmArea.setDeviceCode(item.getDeviceCode());
                            algorithmArea.setChannelCode(item.getChannelCode());
                            updateAlgorithms.add(algorithmArea);
                            AlgorithmConfigDTO updateAlgorithm = new AlgorithmConfigDTO();
                            updateAlgorithm.setPosType(1);
                            updateAlgorithm.setAlgorithms(updateAlgorithms);

                            updateList.add(updateAlgorithm);
                        } else {
                            //新增
                            addAlgorithms.add(algorithmArea);
                        }
                    });
                    AlgorithmConfigVO pos = new AlgorithmConfigVO();
                    pos.setPosType(1);
                    if (!addAlgorithms.isEmpty()) {
                        pos.setAlgorithms(addAlgorithms);
                        positions.add(pos);
                    }

                });
                if (!positions.isEmpty()) {
                    addAlgorithm.setPositions(positions);
                    addList.add(addAlgorithm);
                }
            });

            //多线程操作
            //新增
            if (CollectionUtil.isNotEmpty(addList)) {
                log.info("批量操作-新增的算法:{}", JSON.toJSONString(addList));
                batchSaveAlgorithm(addList);
            }
            //更新
            if (CollectionUtil.isNotEmpty(updateList)) {
                log.info("批量操作-更新的算法:{}", JSON.toJSONString(updateList));
                batchUpdateAlgorithm(updateList);
            }
        }
        return Result.success("提交成功");
    }


    /**
     * 构建默认标绘区域
     *
     * @return List<AlgorithmMonitorPointVO>
     */
    private List<AlgorithmMonitorPointVO> getAlgorithmMonitorPointInit() {
        AlgorithmMonitorPointVO point1 = new AlgorithmMonitorPointVO();
        point1.setMonitorPointId("1");
        point1.setPercentX(0.001F);
        point1.setPercentY(0.001F);
        AlgorithmMonitorPointVO point2 = new AlgorithmMonitorPointVO();
        point2.setMonitorPointId("2");
        point2.setPercentX(0.999F);
        point2.setPercentY(0.001F);
        AlgorithmMonitorPointVO point3 = new AlgorithmMonitorPointVO();
        point3.setMonitorPointId("3");
        point3.setPercentX(0.999F);
        point3.setPercentY(0.999F);
        AlgorithmMonitorPointVO point4 = new AlgorithmMonitorPointVO();
        point4.setMonitorPointId("4");
        point4.setPercentX(0.001F);
        point4.setPercentY(0.999F);
        return Arrays.asList(point1, point2, point3, point4);
    }

    /**
     * 多个list 根据方法求交集
     *
     * @param lists        list
     * @param keyExtractor func
     * @return intersection
     */
    public static <T> Set<T> getIntersection(List<List<T>> lists, Function<T, Object> keyExtractor) {
        if (lists == null || lists.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Object> allKeys = lists.stream()
                .flatMap(List::stream)
                .map(keyExtractor)
                .collect(Collectors.toSet());

        Set<T> intersection = new HashSet<>(lists.get(0));
        for (List<T> list : lists) {
            Set<Object> currentKeys = list.stream()
                    .map(keyExtractor)
                    .collect(Collectors.toSet());
            intersection.removeIf(item -> !currentKeys.contains(keyExtractor.apply(item)));
        }

        return intersection;
    }

    /**
     * 功能描述：批量处理算法与图斑、识别区域数据方法
     *
     * @param polygonAreaList
     * @return
     * @author: zhaozj
     * @date: 2022/4/11
     */
    public void handlePolygonArea(List<AlgorithmAreaVO> polygonAreaList) {
        try {
            log.info("批量处理图斑与识别区域数据方法--handlePolygonArea入参：" + JSON.toJSONString(polygonAreaList));
            //调用图斑与识别区域处理方法，入参：configId、图斑与识别区域信息；返回：configId、polygon_id、monitor_area_id集合
            List<PolygonPatchConfig> polyList = new ArrayList<>();
            for (AlgorithmAreaVO aDo : polygonAreaList) {
                for (PolygonPatchConfig config : aDo.getPolygonAreaList()) {
                    config.setConfigId(aDo.getConfigId());
                    config.setDeviceCode(aDo.getDeviceCode());
                    config.setChannelCode(aDo.getChannelCode());
                    config.setPresetId(aDo.getPresetId());
                    config.setPresetName(aDo.getPresetName());
                    config.setCreateBy(SecurityUtils.getOperator());
                    polyList.add(config);
                }
            }

            //过滤未修改识别区域的    isUpdate：1-修改；2-未修改；3-新增
            List<PolygonPatchConfig> patchList = polyList.stream().filter(poly ->
                    StringUtils.isNotEmpty(poly.getIsUpdate()) &&
                            !DeviceSyncConstants.TWO_STR.equals(poly.getIsUpdate())).collect(Collectors.toList());

            //未修改识别区域的不传
            List<PolygonPatchConfig> patchResList = polygonPatchService.dealPolygonPatchByAlgorithm(patchList);
            if (!CollectionUtils.isEmpty(patchResList)) {
                //对已绑定算法的图斑识别区域修改处理方法
                algoPolygonAreaService.handlePolygonAreaBindAlgo(patchResList);
            }

            //isUpdate：1-修改；2-未修改；3-新增都需要落表vm_polygon_patch_algo_area_rela
            patchResList.addAll(polyList.stream().filter(poly ->
                    StringUtils.isEmpty(poly.getIsUpdate()) ||
                            DeviceSyncConstants.TWO_STR.equals(poly.getIsUpdate())).collect(Collectors.toList()));

            //落表： vm_polygon_patch_algo_area_rela
            if (!CollectionUtils.isEmpty(patchResList)) {
                polygonMapper.addPolygonPatchAlgoAreaBatch(patchResList);
            }
        } catch (Exception e) {
            log.error("批量处理算法与图斑、识别区域数据方法异常", e);
        }
    }

    /**
     * 根据时间单位，转换为秒
     *
     * @param timeIntervalUnit 抽帧频率单位,1:小时 2:分钟 3:秒
     * @param timeInterval
     * @return
     */
    private Integer timeUnitConvert(Integer timeIntervalUnit, Integer timeInterval) {
        Integer result = null;
        if (!ObjectUtil.isEmpty(timeIntervalUnit)) {
            switch (timeIntervalUnit) {
                case Constants.ONE_NUM:
                    result = timeInterval * 60 * 60;
                    break;
                case Constants.TWO_NUM:
                    result = timeInterval * 60;
                    break;
                case Constants.THREE_NUM:
                    result = timeInterval;
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * 根据时间单位，转换为秒（逆转）
     *
     * @param timeIntervalUnit 抽帧频率单位,1:小时 2:分钟 3:秒
     * @param timeInterval
     * @return
     */
    private Integer timeUnitConvertInverse(Integer timeIntervalUnit, Integer timeInterval) {
        Integer result = null;
        if (!ObjectUtil.isEmpty(timeIntervalUnit)) {
            switch (timeIntervalUnit) {
                case Constants.ONE_NUM:
                    result = timeInterval / 60 / 60;
                    break;
                case Constants.TWO_NUM:
                    result = timeInterval / 60;
                    break;
                case Constants.THREE_NUM:
                    result = timeInterval;
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * 集合 根据 key去重
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private String getImgWithNode(String deviceCode, String url) {
        String picUrl = url;
        if (fileUrlEncryptUtil.isObs(url)) {
            return url;
        }
        try {
            //0-私有化  1-云化 2-下沉节点
            boolean correctLocationFlag = true;
            AjaxResult ajaxResult = systemFeignClient.getConfValue("dependence_manage");
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if (sysConfig.getConfigValue().equals(Constants.ZERO)) {
                    correctLocationFlag = false;
                }
            }
            log.info("correctLocationFlag=" + correctLocationFlag);

            DeviceIndReqDTO deviceIndReqDTO = new DeviceIndReqDTO();
            deviceIndReqDTO.setDeviceCode(deviceCode);
            DeviceIndDTO deviceBasicInfo = deviceInfoMapper.getDeviceBasicInfo(deviceIndReqDTO);

            if (!ObjectUtil.isEmpty(deviceBasicInfo)) {
                if (!url.contains(deviceBasicInfo.getAccessNode())) {
                    //图片url拼接接入节点
                    picUrl = AssembleUrlUtil.getAssembleUrl(deviceBasicInfo.getAccessNode(), url, correctLocationFlag);
                }
            }
        } catch (Exception e) {
            log.error("处理算法图片url字段异常", e);
        }
        log.info("picUrl=" + picUrl);
        return picUrl;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> dealHisAlgorithm() {
        //查出oil_gas_pipelines（油气管线）字段值不为“[]”和“null”字符串，且不为空的算法列表list
        List<AlgorithmConfigVO> list = algorithmConfigMapper.getAlgorithmPipelines();
        log.info("待更新" + list.size() + Constants.TIAO_SUANFA);
        if (!CollectionUtils.isEmpty(list)) {
            //遍历该list,将每个对象中的oil_gas_pipelines（油气管线）字段值转为数组
            for (int i = 0; i < list.size(); i++) {
                AlgorithmConfigVO vo = list.get(i);
                String oilGasPipelines = vo.getOilGasPipelines();
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<OilGasPipelinesVO> oilGasPipelinesVOList = objectMapper.readValue(oilGasPipelines, new TypeReference<List<OilGasPipelinesVO>>() {
                    });
                    if (!CollectionUtils.isEmpty(oilGasPipelinesVOList)) {
                        for (int j = 0; j < oilGasPipelinesVOList.size(); j++) {
                            //取出数组的每一个对象，放入管线粗细，箭头颜色，箭头粗细，"width":"12","arrowColor":"rgba(255,255,255,1)","arrowWidth":"1"这三个默认值
                            oilGasPipelinesVOList.get(j).setWidth(Constants.TWELVE);
                            oilGasPipelinesVOList.get(j).setArrowColor(Constants.RGBA);
                            oilGasPipelinesVOList.get(j).setArrowWidth(Constants.ONE);
                        }
                    }
                    String oilGasPipelinesVOListStr = objectMapper.writeValueAsString(oilGasPipelinesVOList);
                    vo.setOilGasPipelines(oilGasPipelinesVOListStr);
                } catch (Exception e) {
                    log.error(vo.getConfigId() + "算法oilGasPipelines转换异常", e);
                }
            }
            //再根据算法表的配置id：config_id批量更新算法表的oil_gas_pipelines（油气管线）字段
            List<List<AlgorithmConfigVO>> partitionList = Lists.partition(list, Constants.TWO_HUNDRED);
            partitionList.forEach(item -> {
                algorithmConfigMapper.updateAlgorithmPipelinesBatch(item);
                try {
                    Thread.sleep(Constants.TEN_THOUSAND);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return Result.success(Constants.UPDATE_SUCCESS);
    }

    @Override
    public List<LinkageAlgorithmMappingVO> queryLinkageAlgorithmList(LinkageAlgorithmMappingVO linkageAlgorithmMappingVO) {
        List<LinkageAlgorithmMappingVO> mappingList = new ArrayList<>();
        AjaxResult ajaxResult = systemFeignClient.getConfValue("linkageAlgorithmMap");
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (!StringUtils.isEmpty(sysConfig.getConfigValue())) {
                mappingList = JSON.parseArray(sysConfig.getConfigValue(), LinkageAlgorithmMappingVO.class);
            }
        }
        if (!StringUtils.isEmpty(linkageAlgorithmMappingVO.getAlgorithmCode())) {
            mappingList = mappingList.stream().filter(e -> e.getAlgorithmCode().equals(linkageAlgorithmMappingVO.getAlgorithmCode())).collect(Collectors.toList());
        }
        return mappingList;
    }

    @Override
    @Transactional
    public Result<List<AlgorithmConfigVO>> insertAlgoConfigMCP(List<AlgorithmConfigAdd> algorithmConfigAdd) {
        log.info("insertAlgoConfigMCP入参：{}", JSONObject.toJSONString(algorithmConfigAdd));
        List<AlgorithmConfigVO> algorithmAreaVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(algorithmConfigAdd)) {
            return Result.fail("请求参数错误！");
        }
        AlgorithmConfigAdd vo = algorithmConfigAdd.get(0);

        //校验告警类型是否已配置算法
        //{"deviceCode":"11010800831327000003","channelCode":"11010800001310000051","presetIdList":[61211,61185,61184],"algorithmCode":"ZGTT-1212112001"}
        CheckAlgorithmConfigVO checkAlgorithmConfigVO = new CheckAlgorithmConfigVO();
        BeanUtils.copyProperties(vo, checkAlgorithmConfigVO);
        List<String> presetIdList = algorithmConfigAdd.stream().map(e -> String.valueOf(e.getPresetId())).collect(Collectors.toList());
        checkAlgorithmConfigVO.setPresetIdList(presetIdList);
        List<String> list = this.checkAlgorithmConfigMcp(checkAlgorithmConfigVO);
        if (CollectionUtils.isEmpty(list)) {
            return Result.fail("入参预置位均存在后端算法任务！");
        }

        algorithmConfigAdd = algorithmConfigAdd.stream()
                .filter(e -> list.contains(String.valueOf(e.getPresetId())))
                .collect(Collectors.toList());

        List<PresetVO> presetImgList = algorithmConfigMapper.getPresetImgList(algorithmConfigAdd);

        for (AlgorithmConfigAdd configAdd : algorithmConfigAdd) {
            for (PresetVO presetVO : presetImgList) {
                if (configAdd.getDeviceCode().equals(presetVO.getDeviceCode())
                        && Objects.equals(configAdd.getPresetId(), presetVO.getPresetId())) {
                    configAdd.setImg(presetVO.getImgUrl());
                    configAdd.setPresetName(presetVO.getPresetName());
                }
            }
        }

        algorithmConfigAdd = algorithmConfigAdd.stream().filter(
                e -> !ObjectUtils.isEmpty(e.getPresetName())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(algorithmConfigAdd)) {
            return Result.fail("预置位匹配失败！");
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();
        String industryCode = user.getIndustryCode();
        String appVerCode = user.getAppVerCode();
        String userName = user.getUserName();
        String platformCode = user.getPlatformCode();
        String tenantId = user.getTenantId();
        SysTenantInfo tenantInfo = user.getTenantInfo();
        String tenantName = tenantInfo.getTenantName();

        //查询设备接入节点
        String deviceCode = vo.getDeviceCode();
        String channelCode = vo.getChannelCode();
        DeviceIndReqDTO deviceIndReqDTO = new DeviceIndReqDTO();
        deviceIndReqDTO.setChannelCode(channelCode);
        DeviceIndDTO channelBasicInfo = deviceInfoMapper.getChannelBasicInfo(deviceIndReqDTO);

        AlgorithmInfoDO algorithmInfoDO = new AlgorithmInfoDO();
        algorithmInfoDO.setAlgorithmCode(vo.getAlgorithmCode());
        List<AlgorithmInfoDO> algorithmInfo = algorithmInfoMapper.getAlgorithmInfo(algorithmInfoDO);

        AlgorithmConfigDO configDO = new AlgorithmConfigDO();
        configDO.setDeviceCode(deviceCode);
        configDO.setTenantId(tenantId);
        configDO.setIndustryCode(industryCode);
        OrderAlgorithmsVO orderAlgorithmsVO = algorithmConfigMapper.selectOrderCode(configDO);

        List<String> configIds = new ArrayList<>();
        List<AlgorithmConfigDO> algorithmConfigDOList = new ArrayList<>();
        for (AlgorithmConfigAdd configAdd : algorithmConfigAdd) {
            AlgorithmConfigDO algorithmConfigDO = new AlgorithmConfigDO();
            BeanUtils.copyProperties(configAdd, algorithmConfigDO);
            String uuid = getUUID();
            configIds.add(uuid);
            algorithmConfigDO.setConfigId(uuid);
            algorithmConfigDO.setDeviceName(channelBasicInfo.getDeviceName());
            algorithmConfigDO.setChannelName(channelBasicInfo.getChannelName());
            if (!CollectionUtils.isEmpty(algorithmInfo)) {
                algorithmConfigDO.setAlgorithmName(algorithmInfo.get(Constants.ZERO_NUM).getAlgorithmName());
            }
            algorithmConfigDO.setPresetId(configAdd.getPresetId().intValue());
            algorithmConfigDO.setTenantId(tenantId);
            algorithmConfigDO.setTenantName(tenantName);
            algorithmConfigDO.setIndustryCode(industryCode);
            algorithmConfigDO.setAppVerCode(appVerCode);
            algorithmConfigDO.setTaskId(getUUID());
            algorithmConfigDO.setStatus(Constants.ZERO);
            algorithmConfigDO.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
            algorithmConfigDO.setCreator(userName);
            algorithmConfigDO.setCreateTime(DateUtils.getTime());
            algorithmConfigDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
            algorithmConfigDO.setPosType(Constants.TWO_NUM);
            algorithmConfigDO.setAcceptNode(platformCode);
            algorithmConfigDO.setAccessNode(channelBasicInfo.getAccessNode());
            algorithmConfigDO.setOrderCode(orderAlgorithmsVO.getOrderCode());
            algorithmConfigDO.setIsLinkageAlgorithm(StringUtils.isEmpty(configAdd.getIsLinkageAlgorithm()) ? Constants.ZERO : configAdd.getIsLinkageAlgorithm());
            algorithmConfigDO.setExtendJsonAlgo(StringUtils.isEmpty(configAdd.getExtendJsonAlgo()) ? null : configAdd.getExtendJsonAlgo());
            algorithmConfigDOList.add(algorithmConfigDO);
        }

        CheckAlgorithmConfigVO checkVO = new CheckAlgorithmConfigVO();
        checkVO.setTenantId(tenantId);
        checkVO.setIndustryCode(industryCode);
        checkVO.setDeviceCode(deviceCode);
        checkVO.setChannelCode(channelCode);
        List<AlgorithmConfigVO> algoCheckList = algorithmConfigMapper.getAlgorithmConfig(checkVO);

        // 停用相同算法编码的通道算法任务
        AlgorithmConfigVO algorithmConfigVO = algoCheckList.stream()
                .filter(algo -> DeviceConstants.ONE_NUM == algo.getPosType()
                        && DeviceConstants.ZERO.equals(algo.getStatus())
                        && vo.getAlgorithmCode().equals(algo.getAlgorithmCode()))
                .findFirst().orElse(null);
        if (algorithmConfigVO != null) {
            AlgorithmConfigDTO algorithmConfig = new AlgorithmConfigDTO();
            List<String> configIdList = new ArrayList<>();
            configIdList.add(algorithmConfigVO.getConfigId());
            algorithmConfig.setConfigIdList(configIdList);
            algorithmConfig.setSingleFlag(Constants.ONE);
            algorithmConfig.setStatus(AlgorithmAreaVO.ALGORITHMOPENSTATUS);
            this.uptAlgorithmStatus(algorithmConfig);
        }

        if (!CollectionUtils.isEmpty(algorithmConfigDOList)) {
            this.saveBatch(algorithmConfigDOList);
        }

        //根据configIds查询后端算法配置
        if (!CollectionUtils.isEmpty(configIds)) {
            log.info("configIds:{}", configIds);
            AlgorithmConfigDTO algorithmConfigDTO = new AlgorithmConfigDTO();
            algorithmConfigDTO.setConfigIdList(configIds);
            algorithmAreaVOS = this.queryById(algorithmConfigDTO);
        }
        return Result.data(algorithmAreaVOS);
    }


    public List<String> checkAlgorithmConfigMcp(CheckAlgorithmConfigVO checkAlgorithmConfigVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkAlgorithmConfigVO.setTenantId(user.getTenantId());
        checkAlgorithmConfigVO.setIndustryCode(user.getIndustryCode());
        log.info("industryCode：" + user.getIndustryCode());
        List<AlgorithmConfigVO> algorithmConfigList = algorithmConfigMapper.getAlgorithmConfig(checkAlgorithmConfigVO);
        List<String> presetIdList = checkAlgorithmConfigVO.getPresetIdList();
        if (!CollectionUtils.isEmpty(algorithmConfigList)) {
            // 提取algorithmConfigList中已存在的presetId
            Set<Long> existingPresetIds = algorithmConfigList.stream()
                    .map(AlgorithmConfigVO::getPresetId)  // 假设AlgorithmConfigVO有getPresetId()方法
                    .collect(Collectors.toSet());

            // 筛选出presetIdList中不在existingPresetIds中的元素
            List<String> notExistsPresetIds = presetIdList.stream()
                    .filter(presetId -> !existingPresetIds.contains(Long.valueOf(presetId)))
                    .collect(Collectors.toList());
            return notExistsPresetIds;
        }
        return presetIdList;
    }

    @Override
    public Boolean checkChannelAttr(DeviceInfoVO deviceInfoVO) {
        boolean flag = false;
        List<ChannelInfoVO> channelsByDeviceCode = deviceInfoMapper.getChannelsByDeviceCode(deviceInfoVO);
        if (!CollectionUtils.isEmpty(channelsByDeviceCode) && channelsByDeviceCode.size() > Constants.ONE_NUM) {
            String channelCode = channelsByDeviceCode.get(Constants.ZERO_NUM).getChannelCode();
            if (deviceInfoVO.getChannelCode().equals(channelCode)) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    @DataScope(tenantColumn = "t1.tenant_id", acceptColumn = "t1.accept_node", industryCode = "t1.industry_code", alarmSourceColumn = "t4.alarm_source", typeValueColumn = "t4.type_value")
    public List<IndustryAlgorithmDTO> querySensingAlgorithm(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        if (null == industryAlgorithmQueryVo.getPageSize() || null == industryAlgorithmQueryVo.getPageNum()) {
            industryAlgorithmQueryVo.setPageNum(1);
            industryAlgorithmQueryVo.setPageSize(10);
        }
        PageHelper.startPage(industryAlgorithmQueryVo.getPageNum(), industryAlgorithmQueryVo.getPageSize());
        log.info("querySensingAlgorithm param:{}", JSONUtil.toJsonStr(industryAlgorithmQueryVo));
        List<IndustryAlgorithmDTO> algorithms = algorithmInfoMapper.queryByTenantIdWithExtendJson(industryAlgorithmQueryVo);
        log.info("querySensingAlgorithm result:{}", JSONUtil.toJsonStr(algorithms));
        return algorithms.stream().filter(algo -> {
            String extendJson = algo.getExtendJson();
            if (StrUtil.isNotBlank(extendJson)) {
                ExtendJson extendJsonBO = JSONObject.parseObject(extendJson, ExtendJson.class);
                return StrUtil.isNotBlank(extendJsonBO.getGraphics()) && Arrays.asList(extendJsonBO.getGraphics().split(",")).contains("2");
            }
            return false;
        }).collect(Collectors.toList());
    }
    private void dealModeConfig(List<ModelConfigVO> modelConfigList){
        ObjectMapper mapper2 = new ObjectMapper();
        try{
            for (ModelConfigVO modelConfigVO : modelConfigList) {
                if(modelConfigVO.getCode().toLowerCase().contains(MODE_TYPE)){
                    modelConfigVO.setDictList(null);
                }
                List<PointsRelationVO> pointsRelation = modelConfigVO.getPointsRelation();
                if(!CollectionUtils.isEmpty(pointsRelation)){
                    for (PointsRelationVO pointsRelationVO : pointsRelation) {
                        if (!CollectionUtils.isEmpty(pointsRelationVO.getPrMonitorArea())) {
                            pointsRelationVO.setPoints(mapper2.writeValueAsString(pointsRelationVO.getPrMonitorArea()));//对象序列化为json
                            pointsRelationVO.setPrMonitorArea(null);
                        } else {//该字段不能为空 所以赋予空数组字符串
                            pointsRelationVO.setPoints(null);//对象序列化为json
                        }
                        if (!CollectionUtils.isEmpty(pointsRelationVO.getPrOilGasPipelinesList())) {
                            List<OilGasPipelinesVO> prOilGasPipelinesList = pointsRelationVO.getPrOilGasPipelinesList();
                            for (OilGasPipelinesVO tempOilGasPipelinesVO : prOilGasPipelinesList) {
                                //取出数组的每一个对象，如果管线粗细，箭头颜色，箭头粗细为空，初始化"width":"12","arrowColor":"rgba(255,255,255,1)","arrowWidth":"1"这三个默认值（兼容老版本app）
                                if (StringUtils.isEmpty(tempOilGasPipelinesVO.getWidth())) {
                                    tempOilGasPipelinesVO.setWidth(Constants.TWELVE);
                                }
                                if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowColor())) {
                                    tempOilGasPipelinesVO.setArrowColor(Constants.RGBA);
                                }
                                if (StringUtils.isEmpty(tempOilGasPipelinesVO.getArrowWidth())) {
                                    tempOilGasPipelinesVO.setArrowWidth(Constants.ONE);
                                }
                            }
                            pointsRelationVO.setOilGasPipelines(mapper2.writeValueAsString(prOilGasPipelinesList));
                            pointsRelationVO.setPrOilGasPipelinesList(null);
                        } else {
                            pointsRelationVO.setOilGasPipelines(null);
                        }
                        if (!CollectionUtils.isEmpty(pointsRelationVO.getPrTripThreadList())) {
                            pointsRelationVO.setTripThread(mapper2.writeValueAsString(pointsRelationVO.getPrTripThreadList()));
                            pointsRelationVO.setPrTripThreadList(null);
                        } else {
                            pointsRelationVO.setTripThread(null);
                        }
                        if (!CollectionUtils.isEmpty(pointsRelationVO.getPrTextLabelList())) {
                            pointsRelationVO.setTextLabels(mapper2.writeValueAsString(pointsRelationVO.getPrTextLabelList()));
                            pointsRelationVO.setPrTextLabelList(null);
                        } else {
                            pointsRelationVO.setTextLabels(null);
                        }

                        if (!CollectionUtils.isEmpty(pointsRelationVO.getPrAnalysisPeriodList())) {
                            pointsRelationVO.setAnalysisPeriod(mapper2.writeValueAsString(pointsRelationVO.getPrAnalysisPeriodList()));
                            pointsRelationVO.setPrAnalysisPeriodList(null);
                        } else {
                            pointsRelationVO.setAnalysisPeriod(null);
                        }
                    }
                }
            }
        }catch (Exception e) {
            log.error("dealModeConfig异常：", e);
        }
    }
    public List<String> getBigModelAlgList(){
        return sysSceneAlarmMapper.getLargeModelAlgorithms();
    }
    @Override
    @DataScope(tenantColumn = "t1.tenant_id", acceptColumn = "t1.accept_node", industryCode = "t1.industry_code")
    public List<AlgAndSceResultVO> getAlgAndSceList(AlgAndSceVO algAndSceVO){
        List<AlgAndSceResultVO> resultList = new ArrayList<>();
        List<AlgorithmAreaVO> algorithms = algorithmInfoMapper.getAlgAndSceList(algAndSceVO);
        List<String> sceneCodeList = algorithms.stream().filter(vo -> vo.getSceneCode() != null && !vo.getSceneCode().trim().isEmpty())
                .flatMap(vo -> {
                    String[] sceneCodes = vo.getSceneCode().split(",");
                    // 转换为流，并去除每个sceneCode的前后空格
                    return java.util.Arrays.stream(sceneCodes)
                            .map(String::trim)
                            // 过滤掉拆分后为空的字符串（比如"111,,222"拆分出的空值）
                            .filter(code -> !code.isEmpty());
                }).distinct().collect(Collectors.toList());
        Map<String, String> sceneMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(sceneCodeList)){
            List<AlgAndSceSceVO> sceneInfoList = algorithmInfoMapper.getSceneInfoList(sceneCodeList);
            sceneMap = sceneInfoList.stream()
                    // 过滤无效数据：sceneId为null/空 或 sceneName为null的VO（根据业务调整）
                    .filter(vo -> vo.getSceneId() != null && !vo.getSceneId().trim().isEmpty() && vo.getSceneName() != null)
                    // 去除sceneId前后空格（避免" 111 "这类带空格的key）
                    .collect(Collectors.toMap(
                            vo -> vo.getSceneId().trim(),  // Map的key：处理后的sceneId
                            AlgAndSceSceVO::getSceneName, // Map的value：sceneName
                            // 解决重复key的冲突策略：保留第一个出现的value（也可改为保留最后一个：(v1, v2) -> v2）
                            (existingValue, newValue) -> existingValue
                    ));
        }
        // 过滤小模型算法，并按algorithmCode分组（algorithmCode唯一，关联algorithmName）
        Map<String, List<AlgorithmAreaVO>> groupByAlgorithmCode0 = algorithms.stream().filter(e -> StringUtils.isEmpty(e.getSceneCode()))
                .collect(Collectors.groupingBy(AlgorithmAreaVO::getAlgorithmCode));
        // 过滤大模型算法，并按algorithmCode分组（algorithmCode唯一，关联algorithmName）
        Map<String, List<AlgorithmAreaVO>> groupByAlgorithmCode1 = algorithms.stream().filter(e -> !StringUtils.isEmpty(e.getSceneCode()))
                .collect(Collectors.groupingBy(AlgorithmAreaVO::getAlgorithmCode));
        // 查询小模型算法
        List<AlgAndSceVO> result0 = groupByAlgorithmCode0.entrySet().stream()
                .map(entry -> {
                    // 分组键：algorithmCode
                    String algorithmCode = entry.getKey();
                    // 该分组下的所有原始数据
                    List<AlgorithmAreaVO> groupItems = entry.getValue();
                    // 构建外层VO
                    AlgAndSceVO vo = new AlgAndSceVO();
                    vo.setAlgorithmCode(algorithmCode);
                    // 取分组中第一个元素的algorithmName（同一algorithmCode的name应相同）
                    vo.setAlgorithmName(groupItems.get(0).getAlgorithmName());
                    // 构建内层configlist
                    List<AlgAndSceConfigVO> configlist = groupItems.stream()
                            .map(doItem -> {
                                AlgAndSceConfigVO configItem = new AlgAndSceConfigVO();
                                configItem.setConfigId(doItem.getConfigId());
                                configItem.setPresetId(doItem.getPresetId());
                                return configItem;
                            })
                            .collect(Collectors.toList());
                    vo.setConfigList(configlist);
                    return vo;
                })
                .collect(Collectors.toList());
        AlgAndSceResultVO algAndSceResultVO0 = new AlgAndSceResultVO();
        algAndSceResultVO0.setModelType(Constants.ZERO_NUM);
        algAndSceResultVO0.setList(result0);
        resultList.add(algAndSceResultVO0);
        // 查询大模型算法
        List<AlgAndSceVO> result1 = new ArrayList<>();
        for (Map.Entry<String, List<AlgorithmAreaVO>> algorithmEntry : groupByAlgorithmCode1.entrySet()) {
            String algorithmCode = algorithmEntry.getKey();
            List<AlgorithmAreaVO> algorithmItems = algorithmEntry.getValue();
            // 构建外层AlgorithmVO
            AlgAndSceVO algorithmVO = new AlgAndSceVO();
            algorithmVO.setAlgorithmCode(algorithmCode);
            // 取第一个元素的algorithmName（确保同一algorithmCode对应唯一name）
            algorithmVO.setAlgorithmName(algorithmItems.get(0).getAlgorithmName());
            // 4. 第二步处理：解析scene_code并按sceneId分组
            Map<String, List<AlgAndSceConfigVO>> sceneConfigMap = new HashMap<>();
            for (AlgorithmAreaVO configDO : algorithmItems) {
                // 拆分逗号分隔的sceneCode为单个sceneId
                String[] sceneIds = configDO.getSceneCode() == null ? new String[0] : configDO.getSceneCode().split(",");
                for (String sceneId : sceneIds) {
                    sceneId = sceneId.trim(); // 去除空格，避免空字符串/空格干扰
                    if (sceneId.isEmpty()) {
                        continue; // 跳过空的sceneId
                    }
                    // 构建ConfigVO
                    AlgAndSceConfigVO configVO = new AlgAndSceConfigVO();
                    configVO.setConfigId(configDO.getConfigId());
                    configVO.setPresetId(configDO.getPresetId());
                    // 按sceneId聚合configList
                    sceneConfigMap.computeIfAbsent(sceneId, k -> new ArrayList<>()).add(configVO);
                }
            }
            // 5. 组装SceneVO列表
            List<AlgAndSceSceVO> sceneVOList = new ArrayList<>();
            for (Map.Entry<String, List<AlgAndSceConfigVO>> sceneEntry : sceneConfigMap.entrySet()) {
                String sceneId = sceneEntry.getKey();
                List<AlgAndSceConfigVO> configList = sceneEntry.getValue();
                AlgAndSceSceVO sceneVO = new AlgAndSceSceVO();
                sceneVO.setSceneId(sceneId);
                sceneVO.setSceneName(sceneMap.get(sceneId)); // 获取场景名称
                sceneVO.setConfigList(configList);
                sceneVOList.add(sceneVO);
            }
            algorithmVO.setSceneList(sceneVOList);
            result1.add(algorithmVO);
        }
        AlgAndSceResultVO algAndSceResultVO1 = new AlgAndSceResultVO();
        algAndSceResultVO1.setModelType(Constants.ONE_NUM);
        algAndSceResultVO1.setList(result1);
        resultList.add(algAndSceResultVO1);
        return resultList;
    }
    @Override
    public String updateAlgorithmHis(AlgorithmConfigDTO vo){
//        SysUser user = SecurityUtils.getLoginUser().getUser();
//        if(!user.getUserName().equals("hued-501")){
//            return "无权限操作！";
//        }
        log.info("更新历史配置 start");
        long startTime = System.currentTimeMillis();
        List<AlgorithmConfigDO> updateList = new ArrayList<>();
        List<AlgorithmConfigDO> hisConfigList = algorithmInfoMapper.getHisConfigList(vo);
        log.info("查询到历史配置数量：" + hisConfigList.size());
        List<AlgorithmConfigDO> typeValueList = new ArrayList<>();
        for (AlgorithmConfigDO algorithmConfigVO : hisConfigList) {
            AlgorithmConfigDO updateVo = new AlgorithmConfigDO();
            updateVo.setConfigId(algorithmConfigVO.getConfigId());
            updateVo.setTenantId(algorithmConfigVO.getTenantId());
            updateVo.setIndustryCode(algorithmConfigVO.getIndustryCode());

            Integer timeInterval = algorithmConfigVO.getTimeInterval();
            Integer timeIntervalUnit = algorithmConfigVO.getTimeIntervalUnit();
            Integer i = timeUnitConvert(timeIntervalUnit, timeInterval);

            String modelConfig = algorithmConfigVO.getModelConfig();
            List<ModelConfigVO> modelConfigList = JSONObject.parseArray(modelConfig, ModelConfigVO.class);
            boolean flag = false;
            for (ModelConfigVO modelConfigVO : modelConfigList) {
                if (modelConfigVO.getCode().toLowerCase().contains(MODE_TYPE)) {
                    List<DictListVO> dictList = modelConfigVO.getDictList();
                    Map<String,String> map = new HashMap<>();
                    for (DictListVO dictListVO : dictList) {
                        map.put(dictListVO.getItemCode(), dictListVO.getItemName());
                    }
                    modelConfigVO.setDictList(null);
                    String value = modelConfigVO.getValue();
                    if (!StringUtils.isEmpty(value)) {
                        String[] split = value.split(",");
                        List<PointsRelationVO> pointsRelation = new ArrayList<>();
                        for (String s : split) {
                            AlgorithmConfigDO algorithmConfigDO = new AlgorithmConfigDO();
                            algorithmConfigDO.setTenantId(algorithmConfigVO.getTenantId());
                            algorithmConfigDO.setIndustryCode(algorithmConfigVO.getIndustryCode());
                            algorithmConfigDO.setTypeValue(s);
                            typeValueList.add(algorithmConfigDO);
                            PointsRelationVO pointsRelationVO = new PointsRelationVO();
                            pointsRelationVO.setItemCode(s);
                            pointsRelationVO.setPoints(algorithmConfigVO.getPoints());
                            pointsRelationVO.setOilGasPipelines(algorithmConfigVO.getOilGasPipelines());
                            pointsRelationVO.setTripThread(algorithmConfigVO.getTripThread());
                            pointsRelationVO.setTextLabels(algorithmConfigVO.getTextLabels());
                            pointsRelationVO.setPointId(String.valueOf(System.currentTimeMillis()));
                            pointsRelationVO.setItemValue(map.get(s));
                            pointsRelationVO.setTimeInterval(Long.valueOf(i));
                            pointsRelationVO.setAnalysisPeriod(algorithmConfigVO.getAnalysisPeriod());
                            pointsRelation.add(pointsRelationVO);
                        }
                        modelConfigVO.setPointsRelation(pointsRelation);
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                updateVo.setModelConfig(JSON.toJSONString(modelConfigList));
                updateVo.setPoints(null);
                updateVo.setOilGasPipelines(null);
                updateVo.setTripThread(null);
                updateVo.setTextLabels(null);
                updateList.add(updateVo);
            }
        }
        List<AlgorithmConfigDO> distinctList = new ArrayList<>(typeValueList.stream()
                .collect(Collectors.toMap(
                        config -> Arrays.asList(
                                config.getTenantId(),
                                config.getIndustryCode(),
                                config.getTypeValue()
                        ),
                        Function.identity(),
                        (existing, replacement) -> existing  // 如果 key 重复，保留现有的（第一个）
                ))
                .values());
//        for (int i = 0; i < distinctList.size(); i++) {
//            AlgorithmConfigDO algorithmConfigDO = distinctList.get(i);
//            //INSERT INTO `video_platform_base`.`sys_scene_config`(`scene_id`, `scene_name`, `type_value`, `algorithm_code`, `creator`, `create_time`, `modifier`, `update_time`, `delete_flag`, `app_ver_code`, `tenant_id`, `industry_code`) VALUES ('11111', '场景1', '123445', NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL);
//            System.out.println("INSERT INTO `video_platform_base`.`sys_scene_config`(`scene_id`, `scene_name`, `type_value`, `algorithm_code`, `creator`, `create_time`, `modifier`, `update_time`, `delete_flag`, `app_ver_code`, `tenant_id`, `industry_code`) " +
//                    "VALUES ('" + IdUtils.fastSimpleUUID() + "','场景"+i + "','" + algorithmConfigDO.getTypeValue() + "',NULL,'admin',now(),'admin',now(),0,NULL,'" +
//                    algorithmConfigDO.getTenantId() + "' , '" + algorithmConfigDO.getIndustryCode() + "');");
//        }
        List<AlgAndSceSceVO> sceneInfoList = algorithmInfoMapper.getSceneInfoListByTenant(distinctList);
        Map<String, String> sceneIdMap = new HashMap<>();
        for (AlgAndSceSceVO sceneVO : sceneInfoList) {
            String key = sceneVO.getTenantId() + "_" + sceneVO.getIndustryCode() + "_" + sceneVO.getTypeValue();
            sceneIdMap.put(key, sceneVO.getSceneId());
        }
        for (AlgorithmConfigDO configDO : updateList) {
            String modelConfig = configDO.getModelConfig();
            List<ModelConfigVO> modelConfigList = JSONObject.parseArray(modelConfig, ModelConfigVO.class);
            StringBuilder sceneCode = new StringBuilder();
            for (ModelConfigVO modelConfigVO : modelConfigList) {
                if (modelConfigVO.getCode().toLowerCase().contains(MODE_TYPE)) {
                    String value = modelConfigVO.getValue();
                    if (!StringUtils.isEmpty(value)) {
                        String[] split = value.split(",");
                        for (String s : split) {
                            String key = configDO.getTenantId() + "_" + configDO.getIndustryCode() + "_" + s;
                            sceneCode.append(sceneIdMap.getOrDefault(key, "")).append(",");
                        }
                    }
                    break;
                }
            }
            configDO.setSceneCode(sceneCode.substring(0, sceneCode.length()-1));
        }
        if (!CollectionUtils.isEmpty(updateList)) {
            algorithmInfoMapper.updateAlgorithmHisBatch(updateList);
        }
        return "修复成功，条数：" + updateList.size() + "，耗时：" + (System.currentTimeMillis() - startTime);
    }

    @Override
    public String getProvinceByPlatform(){
        //查询本级平台编码
        String provinceName = null;
        AjaxResult ajaxResult = systemFeignClient.getConfValue("platform_code");
        if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if(!org.springframework.util.ObjectUtils.isEmpty(sysConfig)){
                String platformCode = sysConfig.getConfigValue();
                if(!StringUtils.isEmpty(platformCode) && !"10000000001".equals(platformCode)){
                    //查询平台级别
                    List<String> list = new ArrayList<>();
                    list.add(platformCode);
                    Result<List<PlatformDataConfigVO>> result = cascadeConfigAPI.getInfo(list);
                    if(ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                        PlatformDataConfigVO platformDataConfigVO = result.getData().get(Constants.ZERO_NUM);
                        String admCode = platformDataConfigVO.getAdmCode();
                        if(StringUtils.isNotEmpty(admCode)){
                            String provinceCode  = admCode.substring(0, 2) + "0000";
                            provinceName = algorithmInfoMapper.getAreaNameByCode(provinceCode);
                        }
                    }
                }
            }
        }
        return provinceName;
    }

    @Override
    public Result<String> fixModelConfig() {
        // 1. 查询 scene_code 不为 NULL 且 delete_flag = 0 的大模型算法数据
        List<AlgorithmConfigDO> list = algorithmConfigMapper.selectModelConfigData();
        if (CollectionUtils.isEmpty(list)) {
            return Result.success("无需要修复的数据");
        }
        log.info("fixModelConfig 查询到待修复数据 {} 条", list.size());

        int fixedCount = 0;
        Set<String> usedAreaIds = new HashSet<>();

        for (AlgorithmConfigDO record : list) {
            try {
                String modelConfig = record.getModelConfig();
                if (StringUtils.isEmpty(modelConfig)) {
                    continue;
                }
                // 2. 解析 model_config 为 JSONArray
                com.alibaba.fastjson.JSONArray modelConfigArray = JSON.parseArray(modelConfig);
                if (modelConfigArray == null || modelConfigArray.isEmpty()) {
                    continue;
                }

                for (int i = 0; i < modelConfigArray.size(); i++) {
                    JSONObject modelObj = modelConfigArray.getJSONObject(i);
                    if (modelObj == null) {
                        continue;
                    }
                    // 3. 获取 pointsRelation 字段
                    com.alibaba.fastjson.JSONArray pointsRelation = modelObj.getJSONArray("pointsRelation");
                    if (pointsRelation == null || pointsRelation.isEmpty()) {
                        continue;
                    }

                    // 4. 获取第一个 pointsRelation 的 points 字段（JSON字符串）
                    JSONObject firstRelation = pointsRelation.getJSONObject(0);
                    String pointsStr = firstRelation.getString(POINTS);
                    if (StringUtils.isEmpty(pointsStr)) {
                        continue;
                    }

                    // 5. 解析 points 字符串为 JSONArray
                    com.alibaba.fastjson.JSONArray pointsArray = JSON.parseArray(pointsStr);
                    if (pointsArray == null || pointsArray.isEmpty()) {
                        continue;
                    }

                    // 6. 遍历 points[] 数组，添加 monitorAreaId
                    for (int j = 0; j < pointsArray.size(); j++) {
                        JSONObject pointObj = pointsArray.getJSONObject(j);
                        if (pointObj == null) {
                            continue;
                        }
                        // 生成随机不重复的16位数字
                        String monitorAreaId = generateUniqueMonitorAreaId(usedAreaIds);
                        pointObj.put("monitorAreaId", monitorAreaId);
                    }

                    // 7. 将更新后的 points 序列化回字符串
                    String updatedPointsStr = pointsArray.toJSONString();
                    // 更新第一个 pointsRelation 的 points
                    firstRelation.put(POINTS, updatedPointsStr);
                    // 8. 将该 pointsRelation 的其他 points 对象复制第一个的内容
                    for (int k = 1; k < pointsRelation.size(); k++) {
                        JSONObject otherRelation = pointsRelation.getJSONObject(k);
                        otherRelation.put(POINTS, updatedPointsStr);
                    }
                }

                // 9. 更新 model_config 和 algorithm_status
                record.setModelConfig(modelConfigArray.toJSONString());
                record.setAlgorithmStatus("3");
                fixedCount++;
            } catch (Exception e) {
                log.error("fixModelConfig 处理 configId={}, taskId={} 时异常", record.getConfigId(), record.getTaskId(), e);
            }
        }

        // 10. 批量更新（每批200条）
        List<AlgorithmConfigDO> needUpdateList = list.stream()
                .filter(r -> "3".equals(r.getAlgorithmStatus()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(needUpdateList)) {
            List<List<AlgorithmConfigDO>> partitionList = Lists.partition(needUpdateList, Constants.TWO_HUNDRED);
            for (List<AlgorithmConfigDO> batch : partitionList) {
                updateBatchById(batch, Constants.TWO_HUNDRED);
            }
        }

        log.info("fixModelConfig 完成：修复 {} 条", fixedCount);
        return Result.success("修复完成：修复 " + fixedCount + " 条");
    }

    /**
     * 生成随机不重复的16位数字 monitorAreaId
     */
    private String generateUniqueMonitorAreaId(Set<String> usedIds) {
        String id;
        do {
            // 使用 UUID 生成 128位随机数，提取部分作为数字
            UUID uuid = UUID.randomUUID();
            // 组合两个 64位随机数，取绝对值并截取16位
            long combined = Math.abs(uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits());
            // 确保是16位数
            id = String.format("%016d", combined % 10000000000000000L);
        } while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }
}




