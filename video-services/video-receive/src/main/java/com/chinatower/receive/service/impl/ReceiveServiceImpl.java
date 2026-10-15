package com.chinatower.receive.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.GsonUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.utils.GsonUtil;
import com.chinatower.event.api.IReceiveApiService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.model.receive.PushThirdReq;
import com.chinatower.event.api.model.receive.WarningLinkInfoReq;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.outdata.api.SendKafkaAPI;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.platform.service.WarningOrderFlowService;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.alarm.util.SubScenesWriteHelper;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.mapper.AlarmOperateMapper;
import com.chinatower.receive.mapper.WarningInterceptMapper;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.receive.model.TranslnglatReq;
import com.chinatower.receive.mongodb.InterceptMongoMapper;
import com.chinatower.receive.mongodb.OperateMongoMapper;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.receive.asyncTask.AsyncCommon;
import com.chinatower.receive.service.AiJudgeService;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.mapper.ThirdPartyAlarmMapper;
import com.chinatower.receive.mapper.WarningOrderMapper;
import com.chinatower.receive.service.*;
import com.chinatower.receive.util.*;
import com.chinatower.event.api.model.receive.MessagePushVo;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.*;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.VideoConstants;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 告警接收实现类
 *
 * @version release_v1.6.90
 * @Author: songxinfeng
 * @Date: 2024/6/27 10:33
 */

@Service
@Slf4j
public class ReceiveServiceImpl implements ReceiveService {

    @Autowired
    private SysService sysService;

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private ThirdPartyAlarmMapper thirdPartyAlarmMapper;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private IOrderPackageRepository orderPackageRepository;

    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private AiJudgeService aiJudgeService;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private ISuppressStrategyService suppressStrategyService;

    @Autowired
    private IAlarmSuppressConfigRepository alarmSuppressConfigRepository;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private WarningOrderFlowService warningOrderFlowService;

    @Autowired
    private GisUtil gisUtil;

    @Autowired
    private CommonService commonService;


    /**
     * 引入所有告警策略处理类
     **/
    @Autowired
    private List<AlarmBaseStategy> alarmStrategyList;

    @Autowired
    private IOrderPackageRepository iOrderPackageRepository;

    @Autowired
    private SendLocalOkhttp sendLocalOkhttp;

    @Autowired
    private SendKafkaAPI sendKafkaAPI;


    /**
     * 人工上报
     *
     * @param receiveAlarmVO
     * @return
     */
    @Override
    public Result addAlarmOrder(ReceiveAlarmVO receiveAlarmVO) {
        String originalJson = JSON.toJSONString(receiveAlarmVO);
        log.info("**************添加报警事件-人工上报**************：{}", originalJson);
        if (StringUtils.isEmpty(receiveAlarmVO.getImportanceLevel()) || (Constants.PARAM_NULL).equals(receiveAlarmVO.getImportanceLevel())) {
            receiveAlarmVO.setImportanceLevel(Constants.IMPORTANCE_LEVEL_1);
        }
        if (StringUtils.isEmpty(receiveAlarmVO.getAlarmTime()) || judgeAlarmTime(receiveAlarmVO.getAlarmTime()) > 0) {
            return Result.fail(Constants.ERROR_TIME_MESSAGE);
        }
        String warningOrderId = "";
        try {
            String alarmTime = getAlarmTime(receiveAlarmVO.getAlarmTime());
            // ###1、初始化告警数据，如：获取省市区编码、网格信息、图片和视频处理等。
            initializeReceiveAlarmVO(receiveAlarmVO, alarmTime);
            //warningOrderId = IPKUtils.getWarnOrderId(receiveAlarmVO.getTenantId(), alarmTime);

            // ###2、创建流程
            List<ReceiveAlarmVO> alarms = getAlarmFlowParam(receiveAlarmVO);
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);
            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms, recordList);

            // ###3、告警入库
            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));
            ReceiveAlarmVO receiveAlarmTemp = alarms.get(0);
            if (receiveAlarmTemp != null && StringUtils.isEmpty(receiveAlarmTemp.getJudgeType())) {
                receiveAlarmTemp.setJudgeType(Constants.JUDGE_TYPE_COMMON);
            }
            receiveAlarmVODB.setList(Collections.singletonList(receiveAlarmTemp));
            commonService.saveAlarm(receiveAlarmVODB, alarmTime, null, JSON.toJSONString(receiveAlarmVODB.getList()), null);

            // ###4、推送消息
            commonService.batchPushMessage(alarms);
        } catch (Exception e) {
            log.error("人工上报异常: " + e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
        return Result.success(receiveAlarmVO.getWarningOrderId());
    }

    private void initializeReceiveAlarmVO(ReceiveAlarmVO receiveAlarmVO, String alarmTime) {
        String userId = "";
        String userName = "";
        String mobilePhone = "";
        String tenantId = "";
        String tenantName = "";
        LoginUser loginUser = new LoginUser();
        if (Constants.WARNING_SOURCE_11.equals(receiveAlarmVO.getWarningSource())) {
            tenantId = receiveAlarmVO.getTenantId();
            SysUser user = new SysUser();
            user.setTenantId(receiveAlarmVO.getTenantId());
            user.setIndustryCode(receiveAlarmVO.getIndustryCode());
            user.setAppVerCode(receiveAlarmVO.getAppVerCode());
            tenantName = receiveAlarmVO.getTenantName();
            userName = receiveAlarmVO.getFindName();
            mobilePhone = receiveAlarmVO.getFindMobile();
            loginUser.setUser(user);
            if (!ObjectUtil.isEmpty(receiveAlarmVO.getExtendField())) {
                receiveAlarmVO.setExtraField(JSON.toJSONString(receiveAlarmVO.getExtendField()));
            }
        } else {
            // 获取当前登录人
            loginUser = SecurityUtils.getLoginUser();
            receiveAlarmVO.setCreateUser(sysService.getLoginUserId());
            if (SecurityUtils.isSwitch()) {
                loginUser = loginUser.getSwitchdLoginUser();
            }
            userId = loginUser.getUser().getUserId();
            userName = loginUser.getUsername();
            mobilePhone = loginUser.getUser().getPhonenumber();
            loginUser = SecurityUtils.getLoginUser();
            receiveAlarmVO.setOrgId(loginUser.getUser().getDeptId());
            receiveAlarmVO.setIndustryCode(loginUser.getUser().getIndustryCode());
            receiveAlarmVO.setAppVerCode(loginUser.getUser().getAppVerCode());
            tenantId = loginUser.getUser().getTenantId();
            if (loginUser.getUser().getTenantInfo() != null) {
                tenantName = loginUser.getUser().getTenantInfo().getTenantName();
            }
        }
        receiveAlarmVO.setTenantId(tenantId);
        receiveAlarmVO.setTenantName(tenantName);
        receiveAlarmVO.setUserName(userName);
        receiveAlarmVO.setFindName(userName);
        receiveAlarmVO.setFindMobile(mobilePhone);
        receiveAlarmVO.setUserId(userId);
        receiveAlarmVO.setWarningOrderId(IPKUtils.getWarnOrderId(tenantId, alarmTime));
        if (!StringUtils.isEmpty(receiveAlarmVO.getWarningTypeId())) {
            receiveAlarmVO.setAlarmType(receiveAlarmVO.getWarningTypeId());
        }
        // 如果分月表不存在，则根据告警时间按创建分月表
        createNewTable(alarmTime);

        // APP上报/第三方告警上报时根据经纬度获取省市区编码
        setGeographicInformation(receiveAlarmVO);

        if (!Constants.WARNING_SOURCE_11.equals(receiveAlarmVO.getWarningSource())) {
            // 根据经纬度获取网格信息
            setGridInformation(receiveAlarmVO, loginUser);
        }
        // 处理图片和视频
        handleVideoAndImg(receiveAlarmVO);
    }

    /**
     * 根据经纬度获取省市区编码
     *
     * @param receiveAlarmVO
     */
    private void setGeographicInformation(ReceiveAlarmVO receiveAlarmVO) {
        if (Constants.TWO_FLAG.equals(receiveAlarmVO.getReportType()) ||
                (Constants.WARNING_SOURCE_11.equals(receiveAlarmVO.getWarningSource()) && !StringUtils.isEmpty(receiveAlarmVO.getLongitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLatitude()))) {
            try {
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(String.format(Constants.FORMAT_S, receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude()));
                log.info("逆地理编码查询接口poiContraryGeocodingQuery req:{}" + JSONObject.toJSONString(req));
                Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("逆地理编码查询接口poiContraryGeocodingQuery resp:{}" + JSONObject.toJSONString(result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                    try {
                        updateGeographicInfoFromResult(receiveAlarmVO, result.getData().get(0));
                    } catch (Exception e) {
                        log.error("addAlarmOrder gisMapApi poiContraryGeocodingQuery error, e:", e);
                    }
                }
            } catch (Exception e) {
                log.error("通过经纬度获取省市区编码异常：" + e);
            }
        } else {
            receiveAlarmVO.setReportType(Constants.ONE);
            receiveAlarmVO.setProvinceCode(receiveAlarmVO.getProvinceId());
            receiveAlarmVO.setCityCode(receiveAlarmVO.getCityId());
            receiveAlarmVO.setCountyCode(receiveAlarmVO.getCountyId());
            // 从redis获取行政区划列表
            List<String> list = Arrays.asList(receiveAlarmVO.getProvinceId(), receiveAlarmVO.getCityId(), receiveAlarmVO.getCountyId(), receiveAlarmVO.getTownCode());
            Map<String, String> areaRedisMap = deviceRepository.getAreaNameCaffeineMap(list);
            if (MapUtils.isNotEmpty(areaRedisMap)) {
                receiveAlarmVO.setProvinceName(MapUtils.getString(areaRedisMap, receiveAlarmVO.getProvinceId(), ""));
                receiveAlarmVO.setCityName(MapUtils.getString(areaRedisMap, receiveAlarmVO.getCityId(), ""));
                receiveAlarmVO.setCountyName(MapUtils.getString(areaRedisMap, receiveAlarmVO.getCountyId(), ""));
                receiveAlarmVO.setTownName(MapUtils.getString(areaRedisMap, receiveAlarmVO.getTownCode(), ""));
            }
        }
    }

    private void updateGeographicInfoFromResult(ReceiveAlarmVO receiveAlarmVO, PoiContraryGeocodingQueryRes data) {
        if (StringUtils.isEmpty(receiveAlarmVO.getAddress())) {
            receiveAlarmVO.setAddress(data.getAddress());
        }
        if (data.getRegion() != null) {
            Region region = data.getRegion();
            if (!StringUtils.isEmpty(region.getProvinceCode())) {
                receiveAlarmVO.setProvinceCode(region.getProvinceCode());
            }
            if (!StringUtils.isEmpty(region.getCityCode())) {
                receiveAlarmVO.setCityCode(region.getCityCode());
            }
            if (!StringUtils.isEmpty(region.getCountyCode())) {
                receiveAlarmVO.setCountyCode(region.getCountyCode());
            }
            if (!StringUtils.isEmpty(region.getTownCode())) {
                receiveAlarmVO.setTownCode(region.getTownCode());
            }
            if (!StringUtils.isEmpty(region.getProvince())) {
                receiveAlarmVO.setProvinceName(region.getProvince());
            }
            if (!StringUtils.isEmpty(region.getCity())) {
                receiveAlarmVO.setCityName(region.getCity());
            }
            if (!StringUtils.isEmpty(region.getCounty())) {
                receiveAlarmVO.setCountyName(region.getCounty());
            }
            if (!StringUtils.isEmpty(region.getTown())) {
                receiveAlarmVO.setTownName(region.getTown());
            }
        }
    }

    /**
     * 获取网格信息
     *
     * @param receiveAlarmVO
     * @param loginUser
     */
    private void setGridInformation(ReceiveAlarmVO receiveAlarmVO, LoginUser loginUser) {
        String latitude = receiveAlarmVO.getLatitude();
        String longitude = receiveAlarmVO.getLongitude();
        if (!StringUtils.isEmpty(latitude) && !StringUtils.isEmpty(longitude)) {
            Result<List<GridByLngLat>> result = this.getMessageByLngLat(loginUser, latitude, longitude);
            if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                List<GridByLngLat> gridList = result.getData();
                if (!CollectionUtils.isEmpty(gridList)) {
                    LoginUser finalLoginUser = loginUser;
                    String finalTenantId = receiveAlarmVO.getTenantId();
                    GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(finalTenantId, grid.getTenantId()) && Objects.equals(finalLoginUser.getUser().getIndustryCode(), grid.getIndustryCode()))
                            .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                    receiveAlarmVO.setGridId(gridInfo.getGridId());
                    receiveAlarmVO.setGridName(gridInfo.getGridName());
                    receiveAlarmVO.setGridUserList(gridInfo.getUserList());
                }
            }
        }
    }

    /**
     * 处理告警图片和视频
     *
     * @param receiveAlarmVO
     */
    private void handleVideoAndImg(ReceiveAlarmVO receiveAlarmVO) {
        // 人工上报默认保存第一张图片作为告警列表缩略图
        List<AlarmImgVO> imgs = receiveAlarmVO.getImgs();
        //将app拍摄的水印图片整合到imgs --用于事件列表&详情中展示
        if (!CollectionUtils.isEmpty(receiveAlarmVO.getOrderFileList())) {
            receiveAlarmVO.getOrderFileList().stream()
                    .map(item -> {
                        AlarmImgVO alarmImgVO = new AlarmImgVO();
                        alarmImgVO.setTagPictureUrl(item.getResourceUrl());
                        return alarmImgVO;
                    }).forEach(imgs::add);
        }
        if (!CollectionUtils.isEmpty(imgs)) {
            receiveAlarmVO.setAlarmPictureUrl(imgs.get(0).getTagPictureUrl());

            String alarmPictureUrl = "";
            for (int i = 1; i < imgs.size(); i++) {
                alarmPictureUrl += imgs.get(i).getTagPictureUrl() + Constants.COMMA;
            }
            if (!StringUtils.isEmpty(alarmPictureUrl)) {
                receiveAlarmVO.setOriginalPictureUrl(alarmPictureUrl.substring(0, alarmPictureUrl.length() - 1));
            }

        }

        // 保存图片和视频
        List<AlarmVideoVO> videos = receiveAlarmVO.getVideos();
        if (!CollectionUtils.isEmpty(videos)) {
            String alarmVideoUrl = "";
            for (int i = 0; i < videos.size(); i++) {
                alarmVideoUrl += videos.get(i).getFileVideoUrl() + Constants.COMMA;
            }
            if (!StringUtils.isEmpty(alarmVideoUrl)) {
                receiveAlarmVO.setAlarmVideoUrl(alarmVideoUrl.substring(0, alarmVideoUrl.length() - 1));
            }
        }
    }

    /**
     * 设置创建流程前置参数
     *
     * @param receiveAlarmVO
     * @return
     */
    private List<ReceiveAlarmVO> getAlarmFlowParam(ReceiveAlarmVO receiveAlarmVO) {
        List<ReceiveAlarmVO> alarms = new ArrayList<>();
        // 默认流程，第一个节点为研判环节 flowId = -1
        AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
        alarmFlowConfigModel.setTenantId(receiveAlarmVO.getTenantId());
        alarmFlowConfigModel.setAlarmCode(receiveAlarmVO.getWarningTypeId());

        if (StringUtils.isEmpty(receiveAlarmVO.getWarningSource())
                || (EnumUtil.WarningSource.PEOPLE.getCode()).equals(receiveAlarmVO.getWarningSource())) {
            receiveAlarmVO.setLinkName(EnumUtil.WarningSource.PEOPLE.getName());
            alarmFlowConfigModel.setWarningSource(EnumUtil.WarningSource.PEOPLE.getCode());
            receiveAlarmVO.setWarningSource(EnumUtil.WarningSource.PEOPLE.getCode());
        } else if (EnumUtil.WarningSource.THIRD.getCode().equals(receiveAlarmVO.getWarningSource())) {
            receiveAlarmVO.setLinkName(receiveAlarmVO.getWarningSourceName());
            alarmFlowConfigModel.setWarningSource(EnumUtil.WarningSource.THIRD.getCode());
            receiveAlarmVO.setWarningSource(EnumUtil.WarningSource.THIRD.getCode());
        } else {
            receiveAlarmVO.setLinkName(EnumUtil.WarningSource.ONEKEY.getName());
            alarmFlowConfigModel.setWarningSource(EnumUtil.WarningSource.ONEKEY.getCode());
            receiveAlarmVO.setWarningSource(EnumUtil.WarningSource.ONEKEY.getCode());
        }
        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        alarms.add(receiveAlarmVO);
        return alarms;
    }

    private String getAlarmTime(String alarmTime) {
        return alarmTime.contains(":") ? alarmTime : alarmTime + " 00:00:00";
    }

    @Override
    public Result addThirdAlarmOrder(List<ReceiveAlarmVO> receiveAlarmVO) {
        String cachePtKey = Constants.ALARM_THIRD_KEY + receiveAlarmVO.get(0).getTenantId() + receiveAlarmVO.get(0).getIndustryCode();

        if (!StringUtils.isEmpty(RedisUtil.get(cachePtKey, RedisDBType.PRIMARY))) {
            log.info("该租户已在配置时间段上报过告警");
            return Result.fail("该租户已在配置时间段上报过告警");
        }
        //查询数据字典配置得调用频次，用redis判断是否可调用
        //默认60S调用一次，每次接收10条告警
        String time = "60";
        String count = "10";
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.THIRD_ALARM_TIME);
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            time = suppressDictData.getData().get(0).getDictValue();
        }
        Result<List<SysDictDataResponse>> suppressDictData2 = systemFeignClient.dictDataType(Constants.THIRD_ALARM_COUNT);
        if (suppressDictData2 != null && suppressDictData2.getData() != null && !suppressDictData2.getData().isEmpty()) {
            count = suppressDictData2.getData().get(0).getDictValue();
        }

        //该租户是否开启第三方告警
        Integer openThirdPartyAlarm = thirdPartyAlarmMapper.isOpenThirdPartyAlarm(receiveAlarmVO.get(0).getTenantId(), receiveAlarmVO.get(0).getIndustryCode());
        List<ThirdPartyAlarmVo> alarmList = thirdPartyAlarmMapper.details(receiveAlarmVO.get(0).getTenantId(), receiveAlarmVO.get(0).getIndustryCode());
        if (Constants.WARNING_SOURCE_11.equals(receiveAlarmVO.get(0).getWarningSource())) {
            if (openThirdPartyAlarm != null && openThirdPartyAlarm > 0) {
                if (!CollectionUtils.isEmpty(alarmList)) {
                    receiveAlarmVO.forEach(r -> {
                        r.setWarningSourceName(alarmList.get(0).getWarningSourceName());
                    });

                    if (!CollectionUtils.isEmpty(receiveAlarmVO)) {
                        //根据租户id查询租户名称
                        List<String> list = new ArrayList<>();
                        list.add(receiveAlarmVO.get(0).getTenantId());
                        List<SysTenantInfo> tenantInfoList = sysService.queryTenantList(list);

                        String tenantName = "";
                        if (!CollectionUtils.isEmpty(tenantInfoList)) {
                            tenantName = tenantInfoList.get(0).getTenantName();
                        }
                        if (receiveAlarmVO.size() > Integer.parseInt(count)) {
                            return Result.fail("一次性上传报文数量超过配置峰值");
                        }
                        String finalTenantName = tenantName;
                        ThreadParallelUtil.run(receiveAlarmVO, r -> {
                            r.setTenantName(finalTenantName);
                            addAlarmOrder(r);
                            return null;
                        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
                    }
                } else {
                    return Result.fail("该租户未配置第三方告警");
                }
            } else {
                return Result.fail("该租户未开启第三方告警");
            }
        }
        RedisUtil.set(cachePtKey, receiveAlarmVO.get(0).getAlarmTime(), Long.parseLong(time), RedisDBType.PRIMARY);
        return Result.success("接受告警成功");
    }

    @Override
    public Result receiveSatelliteAlarm(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos) {
        try {
            ReceiveAlarmDTO receiveAlarmDTO = new ReceiveAlarmDTO();
            receiveAlarmDTO.setReceiveSatelliteAlarmVos(receiveSatelliteAlarmVos);
            ReceiveAlarmVO vo = new ReceiveAlarmVO();
            vo.setAlarmSource(Constants.NINE);

            for (AlarmBaseStategy alarmStrategy : alarmStrategyList) {
                if (alarmStrategy.jdugeAlarmType(vo)) {
                    alarmStrategy.receiveAlarm(receiveAlarmDTO);
                }
            }

        } catch (Exception e) {
            log.error("receiveSatelliteAlarm-异常:" + e);
        }
        return Result.success("接受卫星告警成功");
    }

    //@Override
    public Result receiveSatelliteAlarm2(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos) {
        log.info("====== 接收卫星火情告警 传入参数：{} ======", JSON.toJSONString(receiveSatelliteAlarmVos));
        //20230727 v1.6.10 对星地通上报的卫星告警进行处理，使用统一上传的三张图片地址赋值给当前批次的所有告警
        List<String> relativePathMirList = receiveSatelliteAlarmVos.stream()
                .map(vo -> vo.getExtendField().getRelativePathMir())
                .filter(relativePathMir -> !StringUtils.isEmpty(relativePathMir))
                .collect(Collectors.toList());
        List<String> relativePathPostList = receiveSatelliteAlarmVos.stream()
                .map(vo -> vo.getExtendField().getRelativePathPose())
                .filter(relativePathPost -> !StringUtils.isEmpty(relativePathPost))
                .collect(Collectors.toList());
        List<String> relativePathThemList = receiveSatelliteAlarmVos.stream()
                .map(vo -> vo.getExtendField().getRelativePathThem())
                .filter(relativePathThem -> !StringUtils.isEmpty(relativePathThem))
                .collect(Collectors.toList());
        String relativePathMir = null;
        String relativePathPost = null;
        String relativePathThem = null;
        if (!CollectionUtils.isEmpty(relativePathMirList)) {
            relativePathMir = getObsPicUrl(relativePathMirList.get(0));
        }
        if (!CollectionUtils.isEmpty(relativePathPostList)) {
            relativePathPost = getObsPicUrl(relativePathPostList.get(0));
        }
        if (!CollectionUtils.isEmpty(relativePathThemList)) {
            relativePathThem = getObsPicUrl(relativePathThemList.get(0));
        }
        //卫星告警得时间基本所传时间增加8小时，世界时转北京时
        String finalRelativePathMir = relativePathMir;
        String finalRelativePathPost = relativePathPost;
        String finalRelativePathThem = relativePathThem;
        String localNode = "";
        //获取当前节点
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult resultNode = systemFeignClient.getConfigInfo(sysConfig);
        if (resultNode != null && 200 == Integer.parseInt(String.valueOf(resultNode.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(resultNode.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                localNode = sysConfigInfo.getConfigValue();
            }
        }
        String finalLocalNode = localNode;
        receiveSatelliteAlarmVos.forEach(alarmVo -> {
            alarmVo.setLocalNode(finalLocalNode);
            alarmVo.setAlarmTime(DateUtils.dateToStringNoMS(DateUtils.dateLaterString(alarmVo.getAlarmTime(), Constants.EIGHT_NUM)));
            if (alarmVo.getExtendField() != null) {
                alarmVo.getExtendField().setDataSource(alarmVo.getDataSource());
            }
            try {
                SatelliteExtendFieldApiVo extendFieldVo = alarmVo.getExtendField();
                log.info("====== 获取图片信息 start ：{}======", alarmVo.getExtendField());
                extendFieldVo.setFireArea(alarmVo.getExtendField().getFireArea());
                extendFieldVo.setFireAreaId(alarmVo.getExtendField().getFireAreaId());
                //Eagle-Fire 星地通  njht_fires 航天宏图
                if ("Eagle-Fire".equals(alarmVo.getDataSource())) {
                    extendFieldVo.setRelativePathMir(!StringUtils.isEmpty(extendFieldVo.getRelativePathMir()) ? finalRelativePathMir : "");
                    extendFieldVo.setRelativePathPose(!StringUtils.isEmpty(extendFieldVo.getRelativePathPose()) ? finalRelativePathPost : "");
                    extendFieldVo.setRelativePathThem(!StringUtils.isEmpty(extendFieldVo.getRelativePathThem()) ? finalRelativePathThem : "");
                } else {
                    if (!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())) {
                        extendFieldVo.setRelativePathMir(getObsPicUrl(extendFieldVo.getRelativePathMir()));
                    }
                    if (!StringUtils.isEmpty(extendFieldVo.getRelativePathPose())) {
                        extendFieldVo.setRelativePathPose(getObsPicUrl(extendFieldVo.getRelativePathPose()));
                    }
                    if (!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())) {
                        extendFieldVo.setRelativePathThem(getObsPicUrl(extendFieldVo.getRelativePathThem()));
                    }
                }
                extendFieldVo.setDataSource(alarmVo.getExtendField().getDataSource());
                extendFieldVo.setFireArea(extendFieldVo.getFireArea().replace("公顷", "平方公里"));
                extendFieldVo.setUnderlyingSurfaceType(alarmVo.getExtendField().getUnderlyingSurfaceType());
                alarmVo.setExtendField(extendFieldVo);
            } catch (Exception e) {
                log.error("获取卫星火情图片失败，参数：{}", alarmVo.getExtendField(), e);
            }

            //接收卫星数据，将告警经纬度转换为火星坐标系
            String latitude = alarmVo.getLatitude();
            String longitude = alarmVo.getLongitude();
            if (StringUtil.isNotEmpty(latitude) && StringUtil.isNotEmpty(longitude)) {
                //通过经纬度调用高德API得到火星坐标系，保留9位小数
                try {
                    if (StringUtils.isEmpty(alarmVo.getProvince()) || StringUtils.isEmpty(alarmVo.getCity()) || StringUtils.isEmpty(alarmVo.getCounty())) {
                        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                        req.setLocation(String.format(Constants.FORMAT_S, longitude, latitude));
                        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                        log.info("poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                            try {
                                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                                if (data.getRegion() != null) {
                                    Region region = data.getRegion();
                                    if (!StringUtils.isEmpty(region.getProvinceCode())) {
                                        alarmVo.setProvince(region.getProvinceCode());
                                    }
                                    if (!StringUtils.isEmpty(region.getCityCode())) {
                                        alarmVo.setCity(region.getCityCode());
                                    }
                                    if (!StringUtils.isEmpty(region.getCountyCode())) {
                                        alarmVo.setCounty(region.getCountyCode());
                                    }
                                }
                            } catch (Exception e) {
                                log.error("receiveSatelliteAlarm gisMapApi poiContraryGeocodingQuery error, e:", e);
                            }
                        }
                        log.info("poiContraryGeocodingQuery receiveSatelliteAlarm:{}", JSON.toJSONString(alarmVo));
                    }

                } catch (Exception e) {
                    log.error(MessageConstants.AMP_COORDINATE_CONVERT_MESSAGE, e);
                }
            }
            log.info("卫星告警告警编码:{},卫星原始84经纬度:{},转换后的火星经纬度:{}", alarmVo.getAlarmCode(), alarmVo.getLatitude() + Constants.COMMA + alarmVo.getLongitude(), latitude + Constants.COMMA + longitude);
            alarmVo.setLatitude(latitude);
            alarmVo.setLongitude(longitude);
            //替换经纬度中的逗号
            if (!StringUtils.isEmpty(alarmVo.getLatitude()) && !StringUtils.isEmpty(alarmVo.getLongitude())) {
                alarmVo.setLatitude(alarmVo.getLatitude().replaceAll(Constants.COMMA, ""));
                alarmVo.setLongitude(alarmVo.getLongitude().replaceAll(Constants.COMMA, ""));
            }

        });
        String originalJson = JSON.toJSONString(receiveSatelliteAlarmVos);
        if (CollectionUtils.isEmpty(receiveSatelliteAlarmVos)) {
            return Result.fail(MessageConstants.ALARM_NO_PARAMS);
        }
        // 根据林业版本编码获取租户信息
//        List<OrderCustomerVO> customersByProductCode = orderPackageRepository.getCustomersByProductCode(Constants.PRODUCT_CODE_FOREST);
        //release_v1.5 订购添加卫星订购，订购约束关系为卫星区域。查询租户订购区域向租户发送告警信息
        List<ReceiveAlarmVO> receiveAlarmVOList = new ArrayList<>();
        List<ReceiveAlarmVO> alarms = new ArrayList<>();
        List<OrderCustomerVO> customerAllList = new ArrayList<>();
        receiveSatelliteAlarmVos.forEach(receiveSatelliteAlarmVo -> {
            Result<List<GridByLngLat>> result = null;
            receiveSatelliteAlarmVo.setAlarmBody(receiveSatelliteAlarmVo.getAlarmBody().replace("卫星预警", "卫星告警"));
            List<OrderCustomerVO> customersByProductCode = orderPackageRepository.getCustomersByOrderAreaCode(receiveSatelliteAlarmVo);
            customerAllList.addAll(customersByProductCode);
            log.info("====== 接收卫星火情告警 租户信息：{} ======", JSON.toJSONString(customersByProductCode));
            if (!CollectionUtils.isEmpty(customersByProductCode)) {
                receiveSatelliteAlarmVo.setPushCustomerFlag(Constants.ONE);
                if (Objects.nonNull(receiveSatelliteAlarmVo.getExtendField())) {
                    try {
                        if (!StringUtils.isEmpty(receiveSatelliteAlarmVo.getLatitude()) && !StringUtils.isEmpty(receiveSatelliteAlarmVo.getLongitude())) {
                            List<String> tenantIdList = new ArrayList<>();
                            // 根据经纬度获取网格信息，预处理获取当前告警所有租户网格信息
                            GridByLngLatParam param = new GridByLngLatParam();
                            param.setLatitude(receiveSatelliteAlarmVo.getLatitude());
                            param.setLongitude(receiveSatelliteAlarmVo.getLongitude());
                            List<BaseIndustryEntity> tenantList = new ArrayList<>();
                            customersByProductCode.forEach(item -> {
                                BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                                baseIndustryEntity.setIndustryCode(item.getIndustryCode());
                                baseIndustryEntity.setTenantId(item.getTenantId());
                                tenantList.add(baseIndustryEntity);
                            });
                            param.setTenantList(tenantList);
                            result = gridAPI.getMessageByLngLatNew(param);
                            log.info("通过经纬度获取网格，返回结果： {}", result);
                        }
                    } catch (Exception e) {
                        log.error("========== 卫星告警获取网格信息异常：longitude={},latitude={},异常原因：{}", receiveSatelliteAlarmVo.getLongitude(), receiveSatelliteAlarmVo.getLatitude(), e);
                    }
                }
                customerAllList.addAll(customersByProductCode);
                // 根据订购租户生成报警对象
                Result<List<GridByLngLat>> finalResult = result;
                customersByProductCode.forEach(customer -> {
                    if (Objects.nonNull(customer)) {
                        try {
                            buildReceiveAlarmVOList(receiveSatelliteAlarmVo, customer, receiveAlarmVOList, finalResult);
                        } catch (Exception e) {
                            log.error("生成卫星报警对象异常,参数:{},订购租户信息:{}", receiveSatelliteAlarmVo, customer, e);
                        }
                    }
                });


            } else {
                //不推送客户
                receiveSatelliteAlarmVo.setPushCustomerFlag(Constants.ZERO);
            }
        });
        //入库mongo
        asyncCommon.saveMongoDb(receiveSatelliteAlarmVos);
        if (!CollectionUtils.isEmpty(receiveAlarmVOList)) {
            // 处理生成告警逻辑
            try {
                fillSysAreaBack(receiveSatelliteAlarmVos, receiveAlarmVOList); // 填充告警点省市区信息
                alarms = addAiAlarm2(receiveAlarmVOList, customerAllList);
            } catch (Exception e) {
                log.error("接收卫星火情告警处理异常，传入参数：{}", receiveSatelliteAlarmVos, e);
            }

            if (CollectionUtils.isEmpty(alarms)) {
                return Result.fail(MessageConstants.INTERCEPT_FAIL_MESSAGE);
            }

            // 告警推送
            try {
                batchPushMessage(alarms);
            } catch (Exception e) {
                log.error("====== 告警推送失败,异常原因", e);
            }
            //异步推送第三方
            try {
                PushThirdReq pushThirdReq = new PushThirdReq();
                pushThirdReq.setList(alarms);
                pushThirdReq.setOriginalJson(originalJson);
                iTradeWarningOrderService.pushThirdOrder(pushThirdReq);
            } catch (Exception e) {
                log.error("人工上报推送第三方异常", e);
            }
            return Result.success(ResultCode.SUCCESS.getMsg());
        }
        return Result.fail(MessageConstants.ALARM_NO_TENANT);
    }

    /**
     * 批量发送告警推送
     *
     * @param alarms
     */
    public void batchPushMessage(List<ReceiveAlarmVO> alarms) {
        log.info("====== 告警推送开始，{}", JSON.toJSONString(alarms));
        for (ReceiveAlarmVO alarm : alarms) {

            MessagePushVo messagePushDTO = new MessagePushVo();
            messagePushDTO.setRegionAlarmFlag(alarm.getRegionAlarmFlag());
            messagePushDTO.setOrderId(alarm.getWarningOrderId());
            messagePushDTO.setMsgId(alarm.getWarningOrderId());
            messagePushDTO.setTenantId(alarm.getTenantId());
            messagePushDTO.setAlarmTime(alarm.getAlarmTime());
            messagePushDTO.setAlarmFlowId(alarm.getFlowId());
            messagePushDTO.setFlowTemplate(alarm.getFlowTemplate());
            messagePushDTO.setIndustryCode(alarm.getIndustryCode());
            messagePushDTO.setAppVerCode(alarm.getAppVerCode());
            messagePushDTO.setLinkId(alarm.getLinkId());
            messagePushDTO.setNotifyType(alarm.getNotifyType());
            messagePushDTO.setCreator("");
            messagePushDTO.setNotifyTitle(alarm.getAlarmBody());
            messagePushDTO.setTypeValue(String.valueOf(alarm.getAlarmType()));
            messagePushDTO.setAddress(alarm.getAddress());
            messagePushDTO.setUserType(alarm.getUserType());
            messagePushDTO.setMsgNotifyType(alarm.getMsgNotifyType());
            messagePushDTO.setEnableAcceptanceMode(alarm.getEnableAcceptanceMode());
            String processId = alarm.getProcessId();
            if (!StringUtils.isEmpty(processId)) {
                if (processId.startsWith(Constants.COMMA)) {
                    processId = processId.substring(1, processId.length());
                }
                if (processId.endsWith(Constants.COMMA)) {
                    processId = processId.substring(0, processId.length() - 1);
                }
            }
            messagePushDTO.setUserId(processId);
            String roleId = alarm.getRoleId();
            if (!StringUtils.isEmpty(roleId)) {
                if (roleId.startsWith(Constants.COMMA)) {
                    roleId = roleId.substring(1, roleId.length());
                }
                if (roleId.endsWith(Constants.COMMA)) {
                    roleId = roleId.substring(0, roleId.length() - 1);
                }
            }
            messagePushDTO.setRoleId(roleId);
            messagePushDTO.setOrderStatus(alarm.getOrderStatus());
            messagePushDTO.setAppVerCode(alarm.getAppVerCode());
            messagePushDTO.setIndustryCode(alarm.getIndustryCode());
            messagePushDTO.setCountyId(alarm.getCountyCode());
            messagePushDTO.setCityId(alarm.getCityCode());
            messagePushDTO.setProvinceId(alarm.getProvinceCode());
            messagePushDTO.setWarningSource(alarm.getWarningSource());
            messagePushDTO.setDeviceCode(alarm.getDeviceCode());
            messagePushDTO.setRealLinkType(alarm.getRealLinkType());
            messagePushDTO.setGridId(alarm.getGridId());
            messagePushDTO.setExtraField(alarm.getExtraField());
            //this.messagePush(messagePushDTO);
            asyncCommon.messagePush(messagePushDTO);

            log.info("====== 发送告警推送成功");
        }
    }

    /**
     * 填充省市区详细信息
     *
     * @param receiveAlarmVOList
     */
    private void fillSysAreaBack(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos, List<ReceiveAlarmVO> receiveAlarmVOList) {
        if (!CollectionUtils.isEmpty(receiveAlarmVOList) && !CollectionUtils.isEmpty(receiveSatelliteAlarmVos)) {
            Set<String> areaCodeList = new HashSet<>();
            receiveSatelliteAlarmVos.forEach(receiveAlarmVO -> {
                areaCodeList.add(receiveAlarmVO.getProvince());
                areaCodeList.add(receiveAlarmVO.getCity());
                areaCodeList.add(receiveAlarmVO.getCounty());
            });

            List<SysArea> sysAreaListByCodes = sysService.getSysAreaListByCodes(areaCodeList);
            receiveAlarmVOList.parallelStream().forEach(receiveAlarmVO -> {
                sysAreaListByCodes.forEach(sysArea -> {
                    if (sysArea.getAreaCode().equals(receiveAlarmVO.getProvinceCode())) {
                        receiveAlarmVO.setProvinceId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setProvinceName(sysArea.getAreaName());
                    } else if (sysArea.getAreaCode().equals(receiveAlarmVO.getCityCode())) {
                        receiveAlarmVO.setCityId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setCityName(sysArea.getAreaName());
                    } else if (sysArea.getAreaCode().equals(receiveAlarmVO.getCountyCode())) {
                        receiveAlarmVO.setCountyId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setCountyName(sysArea.getAreaName());
                    }
                });
            });

        }

    }


    private List<ReceiveAlarmVO> addAiAlarm2(List<ReceiveAlarmVO> receiveAlarmVOList, List<OrderCustomerVO> customerAllList) throws Exception {

        ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
        String alarmTime = receiveAlarmVOList.get(0).getAlarmTime();
        receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

        //如果分月表不存在，则根据告警时间按创建分月表
        this.createNewTable(alarmTime);

        // 接收告警集合
        List<ReceiveAlarmVO> alarms = new LinkedList<>();

        // 运营研判告警集合
        List<ReceiveAlarmVO> operations = new LinkedList<>();
        // 告警压制集合
        List<ReceiveAlarmVO> intercepts = new LinkedList<>();
        //List<String>  tenantIdList = customerAllList.stream().map(OrderCustomerVO::getTenantId).collect(Collectors.toList());
        for (ReceiveAlarmVO receiveAlarmVO : receiveAlarmVOList) {
            /*if (StringUtils.isEmpty(receiveAlarmVO.getImportanceLevel()) || Constants.PARAM_NULL.equals(receiveAlarmVO.getImportanceLevel())) {
                receiveAlarmVO.setImportanceLevel(Constants.IMPORTANCE_LEVEL_1);
            }*/

            // 判断是否传入详细地址信息，若未传，则调用接口获取
            setAddressIfAbsent(receiveAlarmVO);

            //更改工单编码生成规则 add by hued 20211103
            String tenantId = receiveAlarmVO.getTenantId();
            String warningOrderId = IPKUtils.getWarnOrderId(tenantId, receiveAlarmVO.getAlarmTime());
            receiveAlarmVO.setWarningOrderId(warningOrderId);
            // 告警压制开关是否开启
            String isIntercept = warningOrderUtil.getInterceptSwitch();
            //联合告警压制需求代码合并
            Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap = getAlarmSuppressStrategy(receiveAlarmVO, customerAllList, isIntercept);
            //获取需要特殊处理行业版本
            List<SysDictDataResponse> dictList = this.getDict(Constants.APP_VER_CODE_TOPIC);
            //先把涉及的租户的雨天告警压制配置数据查询出来,防止后面循环查询
            Map<String, AlarmSuppressConfigDTO> configDTOMap = getAlarmSuppressConfig(customerAllList);

            //处理特殊行业版本
            if (warningOrderUtil.judgeSpecialAppVer(dictList, receiveAlarmVO)) {
                continue;
            }

            //判断并处理告警压制
            if (judgeIntercept(receiveAlarmVO, isIntercept, strategyMap, configDTOMap, intercepts)) {
                continue;
            }

            //处理运营研判,首先判断是否开启研判，并行研判
            if (warningOrderUtil.doOperate(receiveAlarmVO, alarms, operations)) {
                continue;
            }
            if (StringUtils.isEmpty(receiveAlarmVO.getJudgeType())) {
                receiveAlarmVO.setJudgeType(Constants.JUDGE_TYPE_COMMON);
            }


            alarms.add(receiveAlarmVO);
        }

        if (!CollectionUtils.isEmpty(intercepts)) {
            //卫星告警入库告警压制表
            log.info("批量保存压制数据数据信息入参：{}", JSON.toJSONString(intercepts));
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(intercepts);
            // 写 Mongo 前补齐 subScenes
            intercepts.forEach(SubScenesWriteHelper::fillSubScenes);
            int i = warningInterceptMapper.insertInterceptAlarm(intercepts);
            try {
                interceptMongoMapper.insertIntercept(intercepts, alarmTime);
            } catch (Exception e) {
                log.info("回收站插入mongo失败:", e);
            }
            // 异步尝试获取联动摄像机抓图
            if (i > 0) asyncCommon.satelliteSnapShot(intercepts, Constants.ONE);
        }

        // 批量保存告警信息
        List<ReceiveAlarmVO> successDatas = batchCreateWarningOrder(receiveAlarmVODB, alarms);
        // 批量保存运营研判数据
        if (!CollectionUtils.isEmpty(operations)) {
            log.info("批量保存运营研判数据信息入参：{}", JSON.toJSONString(operations));
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(operations);
            // 写 Mongo 前补齐 subScenes（与 CommonServiceImpl.saveOperate 一致）
            operations.forEach(SubScenesWriteHelper::fillSubScenes);
            int i = alarmOperateMapper.insertAlarmOperates(operations);
            operations.forEach(item -> {
                if (!StringUtils.isEmpty(item.getOperationNode()) && !item.getOperationNode().contains(item.getAlarmNode())) {
                    //将此运营研判数据推同步送到指定受理节点
                    asyncCommon.sync(item);
                    /*log.info("推送属地运营研判开始:{}", JSON.toJSONString(item));
                    try {
                        Result result = RemoteNodeUtils.create(item.getOperationNode())
                                .getFeign(IReceiveApiService.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                                .syncAlarm(item);
                        log.info("推送属地运营研判上报告警结束:{}",JSON.toJSONString(result));
                    } catch (Exception e) {
                        log.error("推送属地运营研判开始异常:{}", e);
                    }*/
                }
            });

            try {
                operateMongoMapper.insertOperate(operations, operations.get(0).getAlarmTime());
            } catch (Exception e) {
                log.info("运营研判插入mongo失败:", e);
            }
            if (i > 0) {
                //将最新告警时间保存到缓存中，用于压制判断
                setAlarmTimeToRedis(operations);
                // 异步尝试获取联动摄像机抓图
                asyncCommon.satelliteSnapShot(operations, Constants.THREE);
            }
        }
        // 回填研判信息
        successDatas.addAll(operations);
        return successDatas;
    }

    /**
     * 批量保存告警信息
     *
     * @param receiveAlarmVODB,alarms
     */
    private List<ReceiveAlarmVO> batchCreateWarningOrder(ReceiveAlarmDTO receiveAlarmVODB, List<ReceiveAlarmVO> alarms) {
        if (!CollectionUtils.isEmpty(alarms)) {
            // 创建流程
            List<Map> recordList = iTradeWarningOrderService.createRecord(alarms);

            WarningLinkInfoReq req = new WarningLinkInfoReq();
            req.setAlarms(alarms);
            req.setRecordList(recordList);
            // 回填当前环节相关字段
            iTradeWarningOrderService.setWarningLinkInfo(req);
            req.getAlarms().forEach(item -> {
                List<AlarmOriginalAreaVO> alarmAreaList = item.getAlarmArea();
                if (!CollectionUtils.isEmpty(alarmAreaList)) {
                    // 置信度取AlarmArea集合里面得ConfidenceLevel置信度，然后逗号隔开
                    item.setConfidenceLevel(alarmAreaList.stream().map(AlarmOriginalAreaVO::getConfidenceLevel).collect(Collectors.joining(Constants.COMMA)));
                    item.setConfidenceLevelMin(alarmAreaList.stream().min(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
                    item.setConfidenceLevelMax(alarmAreaList.stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
                }
            });
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(req.getAlarms());
            // 入库前填充 subScenes（与 CommonServiceImpl.saveOrder 一致）
            req.getAlarms().forEach(SubScenesWriteHelper::fillSubScenes);
            receiveAlarmVODB.setList(req.getAlarms());
            int i = warningOrderMapper.insertWarningOrders(receiveAlarmVODB);

            if (i > 0) {
                //将最新告警时间保存到缓存中，用于压制判断
                setAlarmTimeToRedis(req.getAlarms());

                // 异步尝试获取联动摄像机抓图
                asyncCommon.satelliteSnapShot(req.getAlarms(), Constants.TWO);
            }
            log.info("====== 保存告警数据,成功 {} 条", i);

            return req.getAlarms();
        }
        return new ArrayList<>();
    }

    /**
     * 判断是否符合告警压制的条件，放入告警压制集合
     * 判断告警等级、压制规则
     *
     * @param alarmVO
     * @param isIntercept
     * @param strategyMap
     * @param configDTOMap
     * @param intercepts
     * @return
     */
    private boolean judgeIntercept(ReceiveAlarmVO alarmVO, String isIntercept, Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap,
                                   Map<String, AlarmSuppressConfigDTO> configDTOMap, List<ReceiveAlarmVO> intercepts) {

        String tenantId = alarmVO.getTenantId();
        String deviceCode = alarmVO.getDeviceCode();
        String mapKey = tenantId + "_" + deviceCode;
        String alarmTime = alarmVO.getAlarmTime();
        String alarmType = String.valueOf(alarmVO.getAlarmType());

        if (Constants.IMPORTANCE_LEVEL_2.equals(alarmVO.getImportanceLevel())) {
            // 如果告警重要等级为二级，直接落告警压制表
            intercepts.add(alarmVO);
            return true;
        } else if ("1".equals(isIntercept) && MapUtils.isNotEmpty(strategyMap)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyTenantList = strategyMap.get(alarmVO.getTenantId() + alarmVO.getIndustryCode());
            if (strategyTenantList != null && !strategyTenantList.isEmpty()) {  //没有查询到匹配得告警压制策略
                boolean interceptFlag = suppressStrategyService.doStrategyIntercept(alarmVO, strategyTenantList);
                if (interceptFlag) {
                    // 压制成功，放入压制集合
                    intercepts.add(alarmVO);
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * 获取告警压制配置
     *
     * @param customers
     * @return
     */
    private Map<String, AlarmSuppressConfigDTO> getAlarmSuppressConfig(List<OrderCustomerVO> customers) {
        List<String> tenantIdsList = customers.stream().map(OrderCustomerVO::getTenantId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(tenantIdsList)) {
            List<AlarmSuppressConfigDTO> configDTOList = alarmSuppressConfigRepository.querySuppressConfigByTenantIds(tenantIdsList);
            return configDTOList.stream().collect(Collectors.toMap(k -> k.getTenantId() + "_" + k.getDeviceCode(), configDTO -> configDTO));
        }
        return null;
    }

    /**
     * 获取字典值
     *
     * @param dataType
     * @return
     */
    public List<SysDictDataResponse> getDict(String dataType) {
        //获取需要特殊处理行业版本
        List<SysDictDataResponse> dictList = new ArrayList<>();
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(dataType);
        if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
            dictList.addAll(sysResult.getData());
        }

        return dictList;
    }

    /**
     * 获取联合告警压制策略
     *
     * @return
     */
    private Map<String, List<VmAlarmSuppressStrategyVO>> getAlarmSuppressStrategy(ReceiveAlarmVO receiveAlarmVO, List<OrderCustomerVO> customerAllList, String isIntercept) {
        // 告警压制开关是否开启
        if (Constants.YES_FLAG.equals(isIntercept)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyVoList = suppressStrategyService.queryStrategyList(receiveAlarmVO, customerAllList);
            if (strategyVoList != null && !strategyVoList.isEmpty()) {
                return strategyVoList.stream().filter(item -> item.getTenantId() != null && !"".equals(item.getTenantId())).collect(Collectors.groupingBy(temp -> temp.getTenantId() + temp.getIndustryCode()));
            }
        }
        return null;
    }

    private void setAddressIfAbsent(ReceiveAlarmVO receiveAlarmVO) {
        if (StringUtils.isEmpty(receiveAlarmVO.getAddress())) {
            try {
                receiveAlarmVO.setAddress(this.getAddressFromAmap(receiveAlarmVO).getAddress());
            } catch (Exception e) {
                log.error("========== 获取经纬度异常：longitude={},latitude={},异常原因：{}", receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude(), e);
            }
        }
    }


    /**
     * 图片地址转换成obs地址格式
     *
     * @param relativeImgPath
     * @author lyy
     * @date 2022/10/26
     */
    private HttpURLConnection httpUrl = null;

    private String getObsPicUrl(String relativeImgPath) {
        // 避免重复上传
        if (relativeImgPath.contains(Constants.HTTPS_CODE) || relativeImgPath.contains("/apiImg")) {
            return relativeImgPath;
        }
        //从URL读取图片,转为Base64
        URL url = null;
        InputStream in = null;
        String base64 = "";
        //默认为原始路径，调用成功会覆盖为新路径
        String postPicUrl = relativeImgPath;
        try {
            url = new URL(relativeImgPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10 * 1000);
            in = connection.getInputStream();
            byte[] bytes = IOUtils.toByteArray(in);
            base64 = Base64.getEncoder().encodeToString(bytes);
            //获取到图片，转成MultipartFile流
            String relativePath = "data:image/png;base64," + base64;
            MultipartFile mPathPose = BASE64DecodedMultipartFile.base64ToMultipart(relativePath);
            //上传图片地址到obs
            AjaxResult postResult = fileFeignClient.uploadFile(mPathPose);
            Map postFileInfo = (Map) postResult.get(AjaxResult.DATA_TAG);
            if (postFileInfo != null) {
                //得到obs地址入库
                postPicUrl = (String) postFileInfo.get(VideoConstants.ORIGIN_URL);
                if (!StringUtils.isEmpty(postPicUrl) && postPicUrl.contains(Constants.QUESTION_MARK)) {
                    postPicUrl = postPicUrl.substring(Constants.ZERO_NUM, postPicUrl.indexOf(Constants.QUESTION_MARK));
                }
            }
        } catch (Exception e) {
            log.error("图片上传obs失败！", e);
        }
        //如果卫星告警图片是原始图片，那么根据来源替换图片地址域名
        if (!StringUtils.isEmpty(postPicUrl) && !postPicUrl.contains(Constants.HTTPS_CODE)) {
            //从字典获取要替换的域名
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.DOMAIN_PRE_FIX);
            String domainPrefix = null;
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                domainPrefix = suppressDictData.getData().get(0).getDictValue();
            }
            if (domainPrefix != null) {
                //判断不同的告警推送来源（星地通/航天宏图）匹配替换不同的路径
                if (postPicUrl.contains(Constants.FIRE_IMAGE_CODE) || postPicUrl.contains(Constants.SERVICE_CODE)) {
                    String urlBefore = "";
                    if (postPicUrl.contains(Constants.EAGLE_CODE) && postPicUrl.contains(Constants.SERVICE_CODE)) {
                        int index = postPicUrl.indexOf(Constants.SERVICE_CODE);
                        urlBefore = postPicUrl.substring(index);
                        postPicUrl = domainPrefix + Constants.API_IMG_CODE + Constants.TWO + urlBefore;
                    } else if (postPicUrl.contains(Constants.NJHT_CODE) && postPicUrl.contains(Constants.FIRE_IMAGE_CODE)) {
                        int index = postPicUrl.indexOf(Constants.FIRE_IMAGE_CODE);
                        urlBefore = postPicUrl.substring(index);
                        postPicUrl = domainPrefix + Constants.API_IMG_CODE + urlBefore;
                    }
                }
            }
        }
        return postPicUrl;
    }

    /**
     * 构建卫星告警参数
     *
     * @param receiveSatelliteAlarmVo，customers，receiveAlarmVOList
     */
    private List<ReceiveAlarmVO> buildReceiveAlarmVOList(ReceiveSatelliteAlarmApiVo receiveSatelliteAlarmVo, OrderCustomerVO customer, List<ReceiveAlarmVO> receiveAlarmVOList, Result<List<GridByLngLat>> result) {
        ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
        BeanUtil.copyProperties(receiveSatelliteAlarmVo, receiveAlarmVO);
        receiveAlarmVO.setExtraField(JSON.toJSONString(receiveSatelliteAlarmVo.getExtendField()));
        if (StringUtils.isEmpty(receiveAlarmVO.getAlarmBody())) {
            receiveAlarmVO.setAlarmBody("火点面积:" + Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                    orElse(new SatelliteExtendFieldApiVo()).
                    getFireArea());
        } else {
            receiveAlarmVO.setAlarmBody(receiveAlarmVO.getAlarmBody() + " 火点面积:" + Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                    orElse(new SatelliteExtendFieldApiVo()).
                    getFireArea());
        }

        receiveAlarmVO.setWarningSource(String.valueOf(receiveSatelliteAlarmVo.getAlarmSource()));
        //receiveAlarmVO.setAppVerCode(Constants.PRODUCT_CODE_FOREST);
        receiveAlarmVO.setAppVerCode(customer.getAppVerCode());
        receiveAlarmVO.setIndustryCode(customer.getIndustryCode());
        receiveAlarmVO.setTenantId(customer.getTenantId());
        receiveAlarmVO.setTenantName(customer.getCustomerName());
        receiveAlarmVO.setIsOperation(customer.getIsOperation());
        receiveAlarmVO.setOperationNode(customer.getOperationNode());
        receiveAlarmVO.setParallelOperationNode(customer.getParallelOperationNode());
        receiveAlarmVO.setAlarmNode(getPlatformNode());
        receiveAlarmVO.setOperationNodeName(deviceRepository.getPlatformNameByPlatformCode(receiveAlarmVO.getAlarmNode()));
        if (!com.chinatower.common.core.utils.StringUtils.isEmpty(customer.getOperationDept())) {
            receiveAlarmVO.setOperationDept(customer.getOperationDept() + Constants.COMMA);
        }
        if (!com.chinatower.common.core.utils.StringUtils.isEmpty(customer.getParallelOperationDept())) {
            receiveAlarmVO.setParallelOperationDept(customer.getParallelOperationDept() + Constants.COMMA);
        }
        receiveAlarmVO.setAlarmNode(getPlatformNode());
        receiveAlarmVO.setOperationNodeName(deviceRepository.getPlatformNameByPlatformCode(receiveAlarmVO.getAlarmNode()));

        receiveAlarmVO.setWarningTypeId(String.valueOf(receiveAlarmVO.getAlarmType()));
        receiveAlarmVO.setOperationType(customer.getOperationType());
        receiveAlarmVO.setSerialAlarmTypeStr(customer.getSerialAlarmTypeStr());
        receiveAlarmVO.setParallelAlarmTypeStr(customer.getParallelAlarmTypeStr());
        receiveAlarmVO.setOperationAlarmSource(customer.getOperationAlarmSource());
        receiveAlarmVO.setEmergencyLevel(EnumUtil.AlarmLevel.GENERALLY.getCode());
        //receiveAlarmVO.setLinkName(EnumUtil.WarningSource.AI.getName());// 此处是否应该改为行业告警？
        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        receiveAlarmVO.setNotifyType(Constants.YES_FLAG);

        receiveAlarmVO.setProvinceCode(receiveSatelliteAlarmVo.getProvince());
        receiveAlarmVO.setCityCode(receiveSatelliteAlarmVo.getCity());
        receiveAlarmVO.setCountyCode(receiveSatelliteAlarmVo.getCounty());

        receiveAlarmVO.setFileImgUrlSrc(Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                orElse(new SatelliteExtendFieldApiVo()).getRelativePathPose());
        receiveAlarmVO.setOriginalPictureUrl(Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                orElse(new SatelliteExtendFieldApiVo()).getRelativePathPose());

        if (!StringUtils.isEmpty(receiveSatelliteAlarmVo.getConfidenceLevel())) {
            receiveAlarmVO.setConfidenceLevelMax(receiveSatelliteAlarmVo.getConfidenceLevel());
        }

        //2022-06-10 guyuefei： 航天宏图传入的地址按省市区地址存放，我们需要详细地址，根据经纬度调用高德接口获取详细地址填充
        try {
            receiveAlarmVO.setAddress(this.getAddressFromAmap(receiveAlarmVO).getAddress());

            // mongo事发地点同步更新
            receiveSatelliteAlarmVo.setAddress(receiveAlarmVO.getAddress());

        } catch (Exception e) {
            log.error("========== 获取经纬度异常：longitude={},latitude={},异常原因：{}", receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude(), e);
        }

        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
            List<GridByLngLat> gridList = result.getData();
            if (!CollectionUtils.isEmpty(gridList)) {
                GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(customer.getTenantId(), grid.getTenantId()) && Objects.equals(customer.getIndustryCode(), grid.getIndustryCode()))
                        .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                receiveAlarmVO.setGridId(gridInfo.getGridId());
                receiveAlarmVO.setGridName(gridInfo.getGridName());
                receiveAlarmVO.setGridUserList(gridInfo.getUserList());
            }
        }
        receiveAlarmVOList.add(receiveAlarmVO);
        return receiveAlarmVOList;
    }

    /**
     * 判断告警时间是否是2021-07-01之前
     *
     * @param alarmTime
     * @return
     */
    public int judgeAlarmTime(String alarmTime) {
        int number1 = 0;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_YM);
            Date date2, date3;
            date2 = formatter.parse(Constants.ALRAM_LIMIT_TIME);
            date3 = formatter.parse(alarmTime);
            number1 = date2.compareTo(date3);
        } catch (Exception e) {
            log.error("人工上报获取告警时间异常: ", e);
        }
        return number1;
    }

    /**
     * 获取当前环境节点
     *
     * @return
     */
    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if (Objects.nonNull(configInfo.get(Constants.RESULT_DATA))) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }

    /**
     * 调用高德API 通过经纬度返回地址
     *
     * @param receiveAlarmVO
     */
    private ReceiveAlarmVO getAddressFromAmap(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format(Constants.FORMAT_S, receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude()));
        log.info("逆地理编码查询接口poiContraryGeocodingQuery req:{}" + JSONObject.toJSONString(req));
        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
        log.info("逆地理编码查询接口poiContraryGeocodingQuery resp:{}" + JSONObject.toJSONString(result));
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                receiveAlarmVO.setAddress(data.getAddress());
                if (data.getRegion() != null) {
                    Region region = data.getRegion();
                    if (!StringUtils.isEmpty(region.getTownCode())) {
                        receiveAlarmVO.setTownCode(region.getTownCode());
                    }
                    if (!StringUtils.isEmpty(region.getTown())) {
                        receiveAlarmVO.setTownName(region.getTown());
                    }
                }
            } catch (Exception e) {
                log.error("getAddressFromAmap gisMapApi poiContraryGeocodingQuery error, e:", e);
            }
        }
        log.info("AI上报通过经纬度返回地址：{};poiContraryGeocodingQuery接口返回：{}", JSON.toJSONString(receiveAlarmVO), JSON.toJSONString(result));
        return receiveAlarmVO;
    }

    /**
     * 获取网格信息
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public Result<List<GridByLngLat>> getMessageByLngLat(LoginUser loginUser, String latitude, String longitude) {
        Result<List<GridByLngLat>> result = null;
        try {
            GridByLngLatParam param = new GridByLngLatParam();
            param.setLatitude(latitude);
            param.setLongitude(longitude);
            List<BaseIndustryEntity> tenantList = new ArrayList<>();
            BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
            baseIndustryEntity.setIndustryCode(loginUser.getUser().getIndustryCode());
            baseIndustryEntity.setTenantId(loginUser.getUser().getTenantId());
            tenantList.add(baseIndustryEntity);
            param.setTenantList(tenantList);
            log.info(MessageConstants.GRID_REQ_MESSAGE, JSON.toJSONString(param));
            result = gridAPI.getMessageByLngLatNew(param);
            log.info(MessageFormat.format(MessageConstants.GRID_MESSAGE, result));
            return result;
        } catch (Exception e) {
            log.info(MessageFormat.format(MessageConstants.GRID_ERROR_MESSAGE, e));
            return result;
        }

    }

    /**
     * 将最新的告警时间放入Redis中 20211223 zzh
     *
     * @param list
     */
    @Override
    public void setAlarmTimeToRedis(List<ReceiveAlarmVO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (ReceiveAlarmVO vo : list) {
                // 若是并行研判数据则跳过，因为在事件集合中已经set过了
                if (Constants.TWO_FLAG.equals(vo.getIsOperation()) && Constants.TWO_FLAG.equals(vo.getNotifyType())) {
                    continue;
                }

                if (vo.getOrderTimeLimit() != null) {
                    reSetRedisPtList(vo); //设置并更新历史redis中告警点PT信息
                    reSetRedisositionList(vo);
                }

                String value = vo.getAlarmTime();
                log.info("告警编码:{},存储的redis值:{}", vo.getAlarmCode(), value);
                String key = Constants.ALARM_TIME_KEY + vo.getTenantId() + vo.getIndustryCode() + vo.getWarningTypeId() + vo.getDeviceCode();
                if (RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
                    Date oldTime = DateUtils.string2Date((String) RedisUtil.get(key, RedisDBType.PRIMARY));
                    Date newTime = DateUtils.string2Date(value);
                    if (oldTime.before(newTime)) {
                        // 如果传入的时间比Redis中时间晚，更新成最新的时间
                        RedisUtil.delete(key, RedisDBType.PRIMARY);
                        RedisUtil.set(key, value, RedisDBType.PRIMARY);
                    }
                } else {
                    RedisUtil.set(key, value, RedisDBType.PRIMARY);
                }
            }
        }
    }

    @Override
    public Result receiveAlarm(ReceiveAlarmVOReq receiveAlarmVOReq) {
        log.info("告警接收入参：{}", JSON.toJSONString(receiveAlarmVOReq));
        /**
         * 根据传入参数条件判断告警类型，执行策略处理执行器
         */
        String alarmType = receiveAlarmVOReq.getData().getAlarmType();
        Assert.notNull(alarmType, "告警类型不能为空！");
        String localNode = "";
        //获取当前节点
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if (result != null && 200 == Integer.parseInt(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = GsonUtils.parseObject(GsonUtils.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                localNode = sysConfigInfo.getConfigValue();
            }
        }
        // 卡口告警没有受理节点，暂时放开(告警来源是卡口告警“5” 或者 是摄像机告警来源的卡口告警类型)
        if ((receiveAlarmVOReq.getData().getAccessNode() != null && localNode.equals(receiveAlarmVOReq.getData().getAcceptNode()))
                || Constants.FIVE.equals(receiveAlarmVOReq.getData().getAlarmSource())
                || StringUtils.isEmpty(receiveAlarmVOReq.getData().getAccessNode())) {
            log.info("工单成功上报节点无误");
        } else {
            return Result.fail("受理节点与当前节点不一致或没有接入节点");
        }
        receiveAlarmVOReq.getData().setLocalNode(localNode);
        try {
            for (AlarmBaseStategy alarmStrategy : alarmStrategyList) {
                if (alarmStrategy.jdugeAlarmType(receiveAlarmVOReq.getData())) {
                    log.info("告警接收data入参：{}", JSON.toJSONString(receiveAlarmVOReq));
                    ReceiveAlarmDTO receiveAlarmDTO = new ReceiveAlarmDTO();
                    ReceiveAlarmVO receiveAlarmVO = montageUrl(receiveAlarmVOReq.getData().getAccessNode(), receiveAlarmVOReq.getData());
                    receiveAlarmDTO.setReceiveAlarmVO(receiveAlarmVO);
                    receiveAlarmDTO.setReceiveIotAlarmVOList(receiveAlarmVOReq.getReceiveIotAlarmVOList());
                    receiveAlarmDTO.setReceiveUavAlarmVOS(receiveAlarmVOReq.getReceiveUavAlarmVO());

                    alarmStrategy.receiveAlarm(receiveAlarmDTO);
                    // 告警上报成功后：命中配置则标记研判状态=未研判，由定时任务拉取发送Kafka
                    aiJudgeService.markPendingAfterAlarm(receiveAlarmDTO.getList());
                    return Result.success("接收告警成功");
                }
            }
        } catch (Exception e) {
            log.error("接收告警失败，参数：{},异常原因：{}", receiveAlarmVOReq, e);
        }
        return Result.success("没有相应的告警类型处理策略");
    }

    @Override
    public Result receiveRegionAlarm(List<ReceiveRegionAlarmDto> reqList) {
        log.info("卫片执法上报告警入参:{}", JSON.toJSONString(reqList));
        //根据租户id查询租户名称
        List<String> list = reqList.stream().map(ReceiveRegionAlarmDto::getTenantId).collect(Collectors.toList());
        List<SysTenantInfo> tenantInfoList = sysService.queryTenantList(list);
        if (!CollectionUtils.isEmpty(tenantInfoList)) {
            for (SysTenantInfo tenantInfo : tenantInfoList) {
                for (ReceiveRegionAlarmDto alarmDto : reqList) {
                    if (tenantInfo.getTenantId().equals(alarmDto.getTenantId())) {
                        alarmDto.setTenantName(tenantInfo.getTenantName());
                    }
                }
            }
        }
        //批量异步上报卫星执法告警
        ThreadParallelUtil.run(reqList, o -> {
            saveReceiveRegionAlarm(o);
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_CODE);
        return Result.success("告警上报成功");
    }

    @Override
    public TranslnglatVO getAngleByDeviceParam(TranslnglatReq req) {
        SysLntlagConfigVO config = deviceRepository.getLntlagConfig(req.getDeviceCode());
        if (Objects.isNull(config)) {
            log.error("设备参数信息为空,无法获取经纬度定位");
            throw new CustomException("设备参数信息为空,无法获取角度信息");
        }
        DeviceSiteReceiveVO deviceSiteReceiveVO = deviceRepository.queryDevice(req.getDeviceCode(), null);
        String height = deviceSiteReceiveVO == null ? null : deviceSiteReceiveVO.getHeight();
        if (StringUtils.isEmpty(height)) {
            throw new CustomException("设备高度为空,法获取角度信息");
        }
        TranslnglatDTO lnglat = new TranslnglatDTO();
        BeanUtils.copyProperties(req, lnglat);
        lnglat.setUx("0.50");
        lnglat.setVy("0.50");
        lnglat.setC(config.getC());
        lnglat.setD(config.getD());
        lnglat.setF0(config.getF0());
        lnglat.setM(config.getM());
        lnglat.setN(config.getN());
        lnglat.setH(height);
        // 处理水平角偏差和俯仰角偏差   通过有效参考点进行计算
        List<DeviceRefPointVO> pointsValid = deviceRepository.getDevicePointsValid(req.getDeviceCode());
        DeviceModelVO deviceModelVO = new DeviceModelVO();
        deviceModelVO.setRealPan(req.getPan());
        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.SYS_DEVICE_HORIZONTAL);
        List<SysDictDataResponse> enumList = res.getData();
        List<SysDictData> enumListNew = new ArrayList<>();
        enumList.forEach(item -> {
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item, sysDictData);
            enumListNew.add(sysDictData);
        });
        deviceModelVO = LongLatAngleUtils.getValidOffset(pointsValid, deviceModelVO, enumListNew);
        lnglat.setP0(deviceModelVO.getP0() == null ? null : deviceModelVO.getP0().toPlainString());
        lnglat.setT0(deviceModelVO.getT0() == null ? null : deviceModelVO.getT0().toPlainString());
        log.info("getAngleByDeviceParam-开始获取摄像机角度req:{}", JSON.toJSONString(lnglat));
        TranslnglatVO translnglatRep = AlarmLocationUtils.getAlarmAngle(lnglat);
        log.info("getAngleByDeviceParam-开始获取摄像机角度resp:{}", JSON.toJSONString(translnglatRep));
        return translnglatRep;
    }

    @Override
    public void sendLocalSatelliteAlarm(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos) {
        //查询字典中是否有推送要推送得属地
        String localNode = "";
        //获取当前节点
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.SEND_LOCAL_SATELLITE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        log.info("查询字典中是否有推送要推送得属地:{}", JSON.toJSONString(result));
        if (result != null && 200 == Integer.parseInt(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                localNode = sysConfigInfo.getConfigValue();
            }
        }
        List<String> satelliteNodes = new ArrayList<>();
        if (!StringUtils.isEmpty(localNode)) {
            satelliteNodes = Arrays.asList(localNode.split(Constants.COMMA));
            log.info("过滤要推送的属地编码 {}", satelliteNodes);
        }
        Map<String, List<ReceiveSatelliteAlarmApiVo>> map = receiveSatelliteAlarmVos.stream().collect(Collectors.groupingBy(vo ->
                vo.getProvince() + "-" + vo.getCity() + "-" + vo.getCounty()
        ));
        log.info("根据省市区拆分后的数据分组:{}", JSON.toJSONString(map));
        for (String key : map.keySet()) {
            try {
                String[] areCodes = key.split("-");
                List<String> nodes = iOrderPackageRepository.getPlatformCodeByAre(areCodes[0], areCodes[1], areCodes[2], satelliteNodes);
                if (!CollectionUtils.isEmpty(nodes)) {
                    log.info("该批告警符合推送属地-要推送得属地编码为:{}", JSON.toJSONString(nodes));
                    //推送对应属地
                    this.sendSatelliteAlarmByLocalNode(nodes, map.get(key));
                } else {
                    log.info("该批告警不符合推送属地-告警为:{}", JSON.toJSONString(map.get(key)));
                }
            } catch (Exception e) {
                log.info("查询卫星告警要推送的属地异常:" + e);
            }
        }

    }

    @Override
    public boolean isCenterNode() {
        return Constants.CENTER_CODE.equals(getPlatformNode());
    }

    public void sendSatelliteAlarmByLocalNode(List<String> nodes, List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos) {
        for (String node : nodes) {
            try {
                NetConnectUtils.RequestParamter<List<ReceiveSatelliteAlarmApiVo>> paramterEdit = new NetConnectUtils.RequestParamter<>();
                paramterEdit.setBody(receiveSatelliteAlarmVos);
                NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
                pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.RECEIVE_SATELLITE_ALARM);
                paramterEdit.setPath(pathEdit);
                ResultEntity resultEntity = sendLocalOkhttp.doPostGateWay(paramterEdit, node, "10000000001");
                log.info("addOrUpdate-resultEntity-resp:{},node:{}", JSON.toJSONString(resultEntity),node);
            } catch (Exception e) {
                log.info("推送属地异常:" + e);
            }
        }
    }


    /**
     * 查询参数配置中是否推送属地卫星告警配置
     *
     * @return
     */
    private String getConfigParam() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if (Objects.nonNull(configInfo.get(Constants.RESULT_DATA))) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }


    public void saveReceiveRegionAlarm(ReceiveRegionAlarmDto req) {
        log.info("卫片执法上报告警入参-saveReceiveRegionAlarm:{}", JSON.toJSONString(req));
        try {
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            //告警实体
            ReceiveAlarmVO vo = new ReceiveAlarmVO();
            BeanUtil.copyProperties(req, vo);
            //卫片透传字段实体
            RegionAlarmExtraField regionAlarmExtraField = new RegionAlarmExtraField();
            BeanUtil.copyProperties(req, regionAlarmExtraField);
            if (!StringUtils.isEmpty(regionAlarmExtraField.getDetectionFlag())) {
                DecimalFormat df = new DecimalFormat("0");
                Double b = Double.parseDouble(regionAlarmExtraField.getDetectionFlag());
                regionAlarmExtraField.setDetectionFlag(df.format(b));
            }
            String alarmBody = "";
            if (req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_GDWP)) {
                //耕地卫片执法
                alarmBody = req.getPreType() + Constants.PARAM_CHANGE + req.getPostType();
            } else if (req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_KCWP)) {
                //矿产卫片执法
                alarmBody = "矿产卫片执法";
            } else if (req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_FNHWP) || req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_FLHWP)) {
                //非农化卫片执法&非粮化卫片执法
                alarmBody = req.getChangeType();
            }
            //如果是否二次识别为是，默认赋值为0否，二次识别成功后改为1是
            if (Constants.ONE.equals(req.getIsSecondIdentify())) {
                vo.setIsSecondIdentify(Constants.ZERO);
            }
            vo.setLinkageCamera(req.getLinkageCamera());
            vo.setLinkageAction(req.getLinkageAction());
            vo.setSelectAlgorithm(req.getSelectAlgorithm());
            vo.setAlarmBody(alarmBody);
            vo.setLongitude(req.getSpotCenterLongitude());
            vo.setLatitude(req.getSpotCenterLatitude());
            vo.setImportanceLevel(Constants.ONE);
            vo.setWarningSource(req.getAlarmSource());
            vo.setWarningOrderId(IPKUtils.getWarnOrderId(vo.getTenantId(), vo.getAlarmTime()));
            vo.setFlowNodeTime(DateUtils.date2Str(new Date(), "yyyy-MM-dd HH:mm:ss"));
            vo.setAlarmType(req.getWarningTypeId());
            //设置来源于卫片，后续根据这个字段匹配人时不过滤数据权限
            vo.setRegionAlarmFlag(Constants.ONE);
            gisUtil.getAddressFromAmap(vo, null);
            //BeanUtil.copyProperties(vo,regionAlarmExtraField);
            regionAlarmExtraField.setProvinceName(vo.getProvinceName());
            regionAlarmExtraField.setCityName(vo.getCityName());
            regionAlarmExtraField.setCountyName(vo.getCountyName());
            regionAlarmExtraField.setTownName(vo.getTownName());
            vo.setExtraField(JSON.toJSONString(regionAlarmExtraField));
            // 根据经纬度获取网格信息
            if (!StringUtils.isEmpty(vo.getLatitude()) && !StringUtils.isEmpty(vo.getLongitude())) {
                try {
                    GridByLngLatParam param = new GridByLngLatParam();
                    param.setLatitude(vo.getLatitude());
                    param.setLongitude(vo.getLongitude());
                    List<BaseIndustryEntity> tenantList = new ArrayList<>();
                    BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                    baseIndustryEntity.setIndustryCode(vo.getIndustryCode());
                    baseIndustryEntity.setTenantId(vo.getTenantId());
                    tenantList.add(baseIndustryEntity);
                    param.setTenantList(tenantList);
                    log.info("setAlarmGridInfo-getMessageByLngLatNew-req:{}", JSON.toJSONString(param));
                    Result<List<GridByLngLat>> result = gridAPI.getMessageByLngLatNew(param);

                    log.info(MessageFormat.format(MessageConstants.GRID_MESSAGE, result));
                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                        List<GridByLngLat> gridList = result.getData();
                        if (!CollectionUtils.isEmpty(gridList)) {
                            GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(vo.getTenantId(), grid.getTenantId()) && Objects.equals(vo.getIndustryCode(), grid.getIndustryCode()))
                                    .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                            vo.setGridId(gridInfo.getGridId());
                            vo.setGridName(gridInfo.getGridName());
                            vo.setGridUserList(gridInfo.getUserList());
                        }
                    }
                } catch (Exception e) {
                    log.error(MessageConstants.GRID_ERROR_MESSAGE, e);
                }
            }
            //匹配告警流程相关
            alarms.add(vo);
            // 创建流程
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);
            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms, recordList);
            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(vo.getAlarmTime()), TableConstant.WARN_ORDER_TEBLE));

            receiveAlarmVODB.setList(alarms);
            //告警入库
            commonService.saveAlarm(receiveAlarmVODB, null, null, JSON.toJSONString(receiveAlarmVODB.getList()), null);
            //消息推送
            commonService.batchPushMessage(alarms);
        } catch (Exception e) {
            log.error("卫片执法上报告警异常:" + e);
        }

    }


    /**
     * 对工单图片进行拼接（非中心节点&受理节点与当前节点不一致得单子进行操作，其余保持原有路径）
     *
     * @param accessNode
     * @param receiveAlarmVO
     * @return
     */
    public ReceiveAlarmVO montageUrl(String accessNode, ReceiveAlarmVO receiveAlarmVO) {
        if (StringUtils.isEmpty(accessNode)) {
            log.info("接入节点为空-入参:{}", JSON.toJSONString(receiveAlarmVO));
            return receiveAlarmVO;
        }
        //中心节点文件地址无需拼接属地编码
        if ("10000000001".equals(accessNode)) {
            return receiveAlarmVO;
        }
        //纯属化不拼接节点编码
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey("dependence_manage");
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        log.info("获取当前环境字段返回:{}", JSON.toJSONString(result));
        if (result != null && 200 == Integer.parseInt(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null && Constants.ZERO.equals(sysConfigInfo.getConfigValue())) {
                return receiveAlarmVO;
            }
        }
        receiveAlarmVO.setFileImgUrlIcon(getUrl(accessNode, receiveAlarmVO.getFileImgUrlIcon()));
        receiveAlarmVO.setFileImgUrlSrc(getUrl(accessNode, receiveAlarmVO.getFileImgUrlSrc()));
        receiveAlarmVO.setAlarmVideoUrl(getUrl(accessNode, receiveAlarmVO.getAlarmVideoUrl()));
        receiveAlarmVO.setAlarmPictureUrl(getUrl(accessNode, receiveAlarmVO.getAlarmPictureUrl()));
        receiveAlarmVO.setOriginalPictureUrl(getUrl(accessNode, receiveAlarmVO.getOriginalPictureUrl()));
        return receiveAlarmVO;
    }

    public String getUrl(String accessNode, String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        String[] urls = url.split(Constants.COMMA);
        String resUrl = "";
        for (String realUrl : urls) {
            if (realUrl.contains("http") || realUrl.contains("https")) {
                //将属地编码拼接在域名之后
                int thirdSlashIndex = realUrl.indexOf('/', realUrl.indexOf('/', realUrl.indexOf('/') + 1) + 1);
                // 拼接字符串，在第三个斜杠之后
                StringBuilder result = new StringBuilder(realUrl.substring(0, thirdSlashIndex + 1));
                result.append(accessNode + "/");
                result.append(realUrl.substring(thirdSlashIndex + 1));
                realUrl = result.toString();
            } else {
                if(!realUrl.contains(accessNode)){
                    realUrl = "/" + accessNode + realUrl;
                }
            }
            if (StringUtils.isEmpty(resUrl)) {
                resUrl = realUrl;
            } else {
                resUrl += Constants.COMMA + realUrl;
            }
        }
        return resUrl;
    }


    private void reSetRedisositionList(ReceiveAlarmVO receiveAlarmVO) {
        try {

            String cachePositionKey = Constants.ALARM_POSITION_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId() + receiveAlarmVO.getDeviceCode();

            String value = RedisUtil.get(cachePositionKey, RedisDBType.PRIMARY);

            log.info("告警编码：{}，redisKey：{}，处理前redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePositionKey, value);

            List<RedisPositionVo> redisPositionList = StringUtils.isEmpty(value) ? new ArrayList<>() : JSON.parseArray(value, RedisPositionVo.class);
            if (!StringUtils.isEmpty(receiveAlarmVO.getLongitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLatitude())) {
                RedisPositionVo redisPositionVo = new RedisPositionVo();
                redisPositionVo.setAlarmTime(receiveAlarmVO.getAlarmTime());
                redisPositionVo.setLatitude(receiveAlarmVO.getLatitude());
                redisPositionVo.setLongitude(receiveAlarmVO.getLongitude());
                redisPositionList.add(redisPositionVo);
            }

            if (!CollectionUtils.isEmpty(redisPositionList)) {
                // 过滤掉redis中已经超出时间限制的事件的redis
                redisPositionList = redisPositionList.stream().filter(temp ->
                        DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAlarmTime()).getTime() <= receiveAlarmVO.getOrderTimeLimit()
                ).collect(Collectors.toList());
            }

            dealRedisPtList(new ArrayList<>(), redisPositionList);

            log.info("告警编码：{}，redisKey：{}，处理后redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePositionKey, JSON.toJSONString(redisPositionList));

            // 更新redis，清楚历史已经失效的，同时新增本次新增的
            RedisUtil.setEx(cachePositionKey, JSON.toJSONString(redisPositionList), Constants.ONE_NUM, TimeUnit.DAYS, RedisDBType.PRIMARY);
        } catch (Exception e) {
            log.error("告警上报设置Redis信息异常,告警编码：{},接口调用异常:{}", receiveAlarmVO.getAlarmCode(), e);
        }
    }

    private void reSetRedisPtList(ReceiveAlarmVO receiveAlarmVO) {
        try {

            String cachePtKey = Constants.ALARM_PT_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId() + receiveAlarmVO.getDeviceCode();

            String value = RedisUtil.get(cachePtKey, RedisDBType.PRIMARY);

            log.info("告警编码：{}，redisKey：{}，处理前redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePtKey, value);

            List<RedisPTVo> redisPTList = StringUtils.isEmpty(value) ? new ArrayList<>() : JSON.parseArray(value, RedisPTVo.class);

            RedisPTVo redisPTVo = new RedisPTVo();
            // 没有告警区域则不进行PT转义
            if (receiveAlarmVO.getAlarmArea() != null && !receiveAlarmVO.getAlarmArea().isEmpty()) {
                ReceiveAlarmVO alarmVO = suppressStrategyService.dealPtzByIconArea(receiveAlarmVO);
                if (!StringUtils.isEmpty(alarmVO.getPan()) && !StringUtils.isEmpty(alarmVO.getTilt())) {
                    // 本次告警事件正常拿到告警点PT值，进行累加
                    redisPTVo.setP(alarmVO.getPan());
                    redisPTVo.setT(alarmVO.getTilt());
                    redisPTVo.setAt(receiveAlarmVO.getAlarmTime());
                    redisPTList.add(redisPTVo);
                }
            }

            if (!ArrayUtil.isEmpty(redisPTList) && !redisPTList.isEmpty()) {
                // 过滤掉redis中已经超出时间限制的事件的redis
                redisPTList = redisPTList.stream().filter(temp ->
                        DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAt()).getTime() <= receiveAlarmVO.getOrderTimeLimit()
                ).collect(Collectors.toList());
            }

            dealRedisPtList(redisPTList, new ArrayList<>());

            log.info("告警编码：{}，redisKey：{}，处理后redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePtKey, JSON.toJSONString(redisPTList));

            // 更新redis，清楚历史已经失效的，同时新增本次新增的
            RedisUtil.setEx(cachePtKey, JSON.toJSONString(redisPTList), Constants.ONE_NUM, TimeUnit.DAYS, RedisDBType.PRIMARY);
        } catch (Exception e) {
            log.error("告警上报设置Redis信息异常,告警编码：{},接口调用异常:{}", receiveAlarmVO.getAlarmCode(), e);
        }
    }

    private void dealRedisPtList(List<RedisPTVo> redisPTList, List<RedisPositionVo> redisPositionList) {
        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.WARNING_ORDER_REDIS_MAX);
        List<SysDictDataResponse> enumList = res.getData();
        List<SysDictData> enumListMax = new ArrayList<>();
        enumList.forEach(item -> {
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item, sysDictData);
            enumListMax.add(sysDictData);
        });
        Result<List<SysDictDataResponse>> res2 = systemFeignClient.dictDataType(Constants.WARNING_ORDER_REDIS_NUM);
        List<SysDictDataResponse> enumList2 = res2.getData();
        List<SysDictData> enumListNum = new ArrayList<>();
        enumList2.forEach(item -> {
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item, sysDictData);
            enumListNum.add(sysDictData);
        });
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理前的redisPTList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPTList));
        if (!CollectionUtils.isEmpty(redisPTList) && !CollectionUtils.isEmpty(enumListMax) && !CollectionUtils.isEmpty(enumListNum) && redisPTList.size() > Integer.parseInt(enumListMax.get(0).getDictValue()) &&
                Integer.parseInt(enumListMax.get(0).getDictValue()) > Integer.parseInt(enumListNum.get(0).getDictValue())) {
            redisPTList = redisPTList.subList(Integer.parseInt(enumListNum.get(0).getDictValue()), redisPTList.size());
        }
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理后的redisPTList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPTList));
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理前的redisPositionList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPositionList));
        if (!CollectionUtils.isEmpty(redisPositionList) && !CollectionUtils.isEmpty(enumListMax) && !CollectionUtils.isEmpty(enumListNum) && redisPositionList.size() > Integer.parseInt(enumListMax.get(0).getDictValue()) &&
                Integer.parseInt(enumListMax.get(0).getDictValue()) > Integer.parseInt(enumListNum.get(0).getDictValue())) {
            redisPositionList = redisPositionList.subList(Integer.parseInt(enumListNum.get(0).getDictValue()), redisPositionList.size());
        }
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理后的redisPositionList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPositionList));

    }

    /**
     * 根据告警时间按创建分月表
     *
     * @param alarmTime
     */
    @Override
    public void createNewTable(String alarmTime) {
        Date date = DateUtils.string2Date(alarmTime);
        String oldYM = DateUtils.dateToStringYM(date);
        String newYM = DateUtils.dateToStringYM(DateUtils.getDateAfter(date, 1));
        // 如果新年月和旧年月不同，则创建表
        if (!(oldYM).equalsIgnoreCase(newYM)) {
            warningOrderMapper.createNewTable(TableConstant.WARN_ORDER_TEBLE + newYM, TableConstant.WARN_ORDER_TEBLE + oldYM);
            warningOrderMapper.createNewTable(TableConstant.WARN_ORDER_PROCESS_TEBLE + newYM, TableConstant.WARN_ORDER_PROCESS_TEBLE + oldYM);
            try {
               /* interceptMongoMapper.mongoCreateTable(TableConstant.WARN_ALARM_INTERCEPT+"_"+newYM, MongoDBType.PRIMARY);
                //回收站表创建索引
                this.checkIndexsExist(TableConstant.WARN_ALARM_INTERCEPT+"_"+newYM, newYM);
                operateMongoMapper.mongoCreateTable(TableConstant.WARN_ALARM_OPERATE+"_"+newYM,MongoDBType.PRIMARY);
                //运营研判表创建索引
                this.checkIndexsExist(TableConstant.WARN_ALARM_OPERATE+"_"+newYM, newYM);
                operateMongoMapper.mongoCreateTable(TableConstant.WARN_LOCAL_ALARM_OPERATE+"_"+newYM,MongoDBType.PRIMARY);
                //属地运营研判表创建索引
                this.checkIndexsExist(TableConstant.WARN_LOCAL_ALARM_OPERATE+"_"+newYM, newYM);*/
            } catch (Exception e) {
                log.info("创建回收站运营研判mongo表失败:", e);
            }
        }
    }
}
