package com.chinatower.receive.core.alarm;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.event.entity.vo.SatelliteExtendFieldApiVo;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.service.WarningOrderFlowService;
import com.chinatower.receive.asyncTask.AsyncCommon;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.util.AlarmStrategyEnum;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.service.*;
import com.chinatower.receive.util.BASE64DecodedMultipartFile;
import com.chinatower.receive.util.IPKUtils;
import com.chinatower.receive.util.TableUtil;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.VideoConstants;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 卫星告警接收策略实现类
 * @Author: songxinfeng
 * @Date: 2024/8/6 10:30
 * @version release_v1.6.90
 */

@Component("satelliteAlarmStrategy")
@Slf4j
public class SatelliteAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private IOrderPackageRepository orderPackageRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private SysService sysService;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private ISuppressStrategyService suppressStrategyService;

    @Autowired
    private IAlarmSuppressConfigRepository alarmSuppressConfigRepository;

    @Autowired
    private WarningOrderFlowService warningOrderFlowService;

    @Autowired
    private CommonService commonService;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return WarningSourceEnum.SATELLITE_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        //获取卫星上报入参
        List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos = receiveAlarmDTO.getReceiveSatelliteAlarmVos();
        //去掉dataSource是航天宏图njht_fires的对象
        receiveSatelliteAlarmVos.removeIf(vo -> "njht_fires".equals(vo.getDataSource()));
        log.info("接收卫星火情告警-SatelliteAlarmStrategy:{}", JSON.toJSONString(receiveSatelliteAlarmVos));
        //卫星告警处理卫星图片及告警经纬度转换火星坐标系
        receiveSatelliteAlarmVos = this.getSatelliteParam(receiveSatelliteAlarmVos);
        String originalJson = JSON.toJSONString(receiveSatelliteAlarmVos);
        //处理卫星告警租户问题
        List<ReceiveAlarmVO> receiveAlarmVOList = new ArrayList<>();
        List<ReceiveAlarmVO> alarms = new ArrayList<>();
        List<OrderCustomerVO> customerAllList = new ArrayList<>();
        getCustomers(receiveSatelliteAlarmVos,customerAllList,receiveAlarmVOList);
        //入库mongo
        asyncCommon.saveMongoDb(receiveSatelliteAlarmVos);
        if(!CollectionUtils.isEmpty(receiveAlarmVOList)){
            // 处理生成告警逻辑
            try {
                fillSysAreaBack(receiveSatelliteAlarmVos,receiveAlarmVOList); // 填充告警点省市区信息
                alarms = addAlarm(receiveAlarmVOList,customerAllList,originalJson);
            } catch (Exception e) {
                log.error("接收卫星火情告警处理异常，传入参数：{}",receiveSatelliteAlarmVos,e);
            }

            if (CollectionUtils.isEmpty(alarms)) {
                log.info(MessageConstants.INTERCEPT_FAIL_MESSAGE);
                return;
            }


        }
    }

    public void getCustomers(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos,List<OrderCustomerVO> customerAllList,List<ReceiveAlarmVO> receiveAlarmVOList ){
        receiveSatelliteAlarmVos.forEach(receiveSatelliteAlarmVo -> {
            Result<List<GridByLngLat>> result =null;
            receiveSatelliteAlarmVo.setAlarmBody(receiveSatelliteAlarmVo.getAlarmBody().replace("卫星预警","卫星告警"));
            List<OrderCustomerVO> customersByProductCode = orderPackageRepository.getCustomersByOrderAreaCode(receiveSatelliteAlarmVo);
            customerAllList.addAll(customersByProductCode);
            log.info("====== 接收卫星火情告警 租户信息：{} ======",JSON.toJSONString(customersByProductCode));
            if(! CollectionUtils.isEmpty(customersByProductCode)) {
                receiveSatelliteAlarmVo.setPushCustomerFlag(Constants.ONE);
                if (Objects.nonNull(receiveSatelliteAlarmVo.getExtendField())) {
                    try{
                        if (!StringUtils.isEmpty(receiveSatelliteAlarmVo.getLatitude()) && !StringUtils.isEmpty(receiveSatelliteAlarmVo.getLongitude())) {
                            List<String> tenantIdList = new ArrayList<>();
                            // 根据经纬度获取网格信息，预处理获取当前告警所有租户网格信息
                            GridByLngLatParam param = new GridByLngLatParam();
                            param.setLatitude(receiveSatelliteAlarmVo.getLatitude());
                            param.setLongitude(receiveSatelliteAlarmVo.getLongitude());
                            List<BaseIndustryEntity> tenantList = new ArrayList<>();
                            customersByProductCode.forEach(item ->{
                                BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                                baseIndustryEntity.setIndustryCode(item.getIndustryCode());
                                baseIndustryEntity.setTenantId(item.getTenantId());
                                tenantList.add(baseIndustryEntity);
                            });
                            param.setTenantList(tenantList);
                            result = gridAPI.getMessageByLngLatNew(param);
                            log.info("通过经纬度获取网格，返回结果： {}", result);
                        }
                    }catch (Exception e){
                        log.error("========== 卫星告警获取网格信息异常：longitude={},latitude={},异常原因：{}",receiveSatelliteAlarmVo.getLongitude(),receiveSatelliteAlarmVo.getLatitude(),e);
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


            }else{
                //不推送客户
                receiveSatelliteAlarmVo.setPushCustomerFlag(Constants.ZERO);
            }
        });
    }

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        return null;
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        if(StringUtils.isEmpty(receiveAlarmVO.getAddress())){
            try{
                receiveAlarmVO.setAddress(this.getAddressFromAmap(receiveAlarmVO).getAddress());
            }catch (Exception e){
                log.error("========== 获取经纬度异常：longitude={},latitude={},异常原因：{}",receiveAlarmVO.getLongitude(),receiveAlarmVO.getLatitude(),e);
            }
        }
    }

    @Override
    public String getStrategyName() {
        return AlarmStrategyEnum.SATELLITE_ALARM.getAlarmStrategyBean();
    }




    /**
     * 批量保存告警信息
     * @param alarms
     */
    private List<ReceiveAlarmVO> batchCreateWarningOrder(List<ReceiveAlarmVO> alarms){
        if (! CollectionUtils.isEmpty(alarms)) {
            // 创建流程
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);

            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms,recordList);
            /*alarms.forEach(item -> {
                List<AlarmOriginalAreaVO> alarmAreaList = item.getAlarmArea();
                if (!CollectionUtils.isEmpty(alarmAreaList)) {
                    // 置信度取AlarmArea集合里面得ConfidenceLevel置信度，然后逗号隔开
                    item.setConfidenceLevel(alarmAreaList.stream().map(AlarmOriginalAreaVO::getConfidenceLevel).collect(Collectors.joining(Constants.COMMA)));
                    item.setConfidenceLevelMin(alarmAreaList.stream().min(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
                    item.setConfidenceLevelMax(alarmAreaList.stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
                }
            });*/
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(alarms);
            return alarms;
        }
        return new ArrayList<>();
    }

    /**
     * 告警、运营研判、压制数据封装及入库，推送第三方
     * @param receiveAlarmVOList
     * @param customerAllList
     * @param originalJson
     * @return
     * @throws Exception
     */
    private List<ReceiveAlarmVO> addAlarm(List<ReceiveAlarmVO> receiveAlarmVOList,List<OrderCustomerVO> customerAllList,String originalJson) throws Exception {

        ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
        String alarmTime = receiveAlarmVOList.get(0).getAlarmTime();
        receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

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
            getAddress(receiveAlarmVO);

            //更改工单编码生成规则 add by hued 20211103
            String tenantId = receiveAlarmVO.getTenantId();
            String warningOrderId = IPKUtils.getWarnOrderId(tenantId,receiveAlarmVO.getAlarmTime());
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
            if (this.judgeIntercept(receiveAlarmVO, isIntercept, strategyMap, configDTOMap, intercepts)) {
                continue;
            }
            //处理运营研判,首先判断是否开启研判，并行研判
            if (warningOrderUtil.doOperate(receiveAlarmVO, alarms, operations)) {
                continue;
            }
            if(StringUtils.isEmpty(receiveAlarmVO.getJudgeType())){
                receiveAlarmVO.setJudgeType(Constants.JUDGE_TYPE_COMMON);
            }
            alarms.add(receiveAlarmVO);
        }
        receiveAlarmVODB.setIntercepts(intercepts);

        // 批量保存告警信息
        List<ReceiveAlarmVO> successDatas = batchCreateWarningOrder(alarms);
        receiveAlarmVODB.setList(alarms);
        receiveAlarmVODB.setOperations(operations);
        commonService.saveAlarm(receiveAlarmVODB,alarmTime,Constants.NINE,originalJson,null);
        // 回填研判信息
        successDatas.addAll(operations);

        // 告警推送
        try{
            commonService.batchPushMessage(alarms);
            commonService.batchPushMessage(operations);
        }catch (Exception e){
            log.error("====== 告警推送失败,异常原因",e);
        }
        return successDatas;
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
     * 获取告警压制配置
     *
     * @param customers
     * @return
     */
    private Map<String, AlarmSuppressConfigDTO> getAlarmSuppressConfig(List<OrderCustomerVO> customers) {
        List<String> tenantIdsList = customers.stream().collect(Collectors.mapping(OrderCustomerVO::getTenantId, Collectors.toList()));
        if (!CollectionUtils.isEmpty(tenantIdsList)) {
            List<AlarmSuppressConfigDTO> configDTOList = alarmSuppressConfigRepository.querySuppressConfigByTenantIds(tenantIdsList);
            return configDTOList.stream().collect(Collectors.toMap(k -> k.getTenantId() + "_" + k.getDeviceCode(), configDTO -> configDTO));
        }
        return null;
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
                return strategyVoList.stream().filter(item -> item.getTenantId() != null && !"".equals(item.getTenantId())).collect(Collectors.groupingBy(temp -> temp.getTenantId()+temp.getIndustryCode()));
            }
        }
        return null;
    }
    /**
     * 填充省市区详细信息
     * @param receiveAlarmVOList
     */
    private void fillSysAreaBack(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos,List<ReceiveAlarmVO> receiveAlarmVOList){
        if(! CollectionUtils.isEmpty(receiveAlarmVOList) && ! CollectionUtils.isEmpty(receiveSatelliteAlarmVos)){
            Set<String> areaCodeList = new HashSet<>();
            receiveSatelliteAlarmVos.forEach(receiveAlarmVO -> {
                areaCodeList.add(receiveAlarmVO.getProvince());
                areaCodeList.add(receiveAlarmVO.getCity());
                areaCodeList.add(receiveAlarmVO.getCounty());
            });

            List<SysArea> sysAreaListByCodes = sysService.getSysAreaListByCodes(areaCodeList);
            receiveAlarmVOList.parallelStream().forEach(receiveAlarmVO -> {
                sysAreaListByCodes.forEach(sysArea -> {
                    if(sysArea.getAreaCode().equals(receiveAlarmVO.getProvinceCode())){
                        receiveAlarmVO.setProvinceId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setProvinceName(sysArea.getAreaName());
                    }else if(sysArea.getAreaCode().equals(receiveAlarmVO.getCityCode())){
                        receiveAlarmVO.setCityId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setCityName(sysArea.getAreaName());
                    }else if(sysArea.getAreaCode().equals(receiveAlarmVO.getCountyCode())){
                        receiveAlarmVO.setCountyId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setCountyName(sysArea.getAreaName());
                    }
                });
            });

        }

    }

    /**
     * 填充卫星告警图片信息及转换火星经纬度
     * @param receiveSatelliteAlarmVos
     * @return
     */
    public List<ReceiveSatelliteAlarmApiVo> getSatelliteParam(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos){
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
        String localNode="";
        //获取当前节点
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult resultNode = systemFeignClient.getConfigInfo(sysConfig);
        if(resultNode != null && 200 == Integer.valueOf(String.valueOf(resultNode.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(resultNode.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                localNode = sysConfigInfo.getConfigValue();
            }
        }
        String finalLocalNode = localNode;
        receiveSatelliteAlarmVos.forEach(alarmVo -> {
            alarmVo.setLocalNode(finalLocalNode);
            alarmVo.setAlarmTime(DateUtils.dateToStringNoMS(DateUtils.dateLaterString(alarmVo.getAlarmTime(), Constants.EIGHT_NUM)));
            if(alarmVo.getExtendField() != null){
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
                extendFieldVo.setFireArea(extendFieldVo.getFireArea().replace("公顷","平方公里"));
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
                    if(StringUtils.isEmpty(alarmVo.getProvince()) || StringUtils.isEmpty(alarmVo.getCity()) || StringUtils.isEmpty(alarmVo.getCounty())){
                        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                        req.setLocation(String.format("%s,%s", longitude, latitude));
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

                }
                catch (Exception e) {
                    log.error(MessageConstants.AMP_COORDINATE_CONVERT_MESSAGE, e);
                }
            }
            log.info("卫星告警告警编码:{},卫星原始84经纬度:{},转换后的火星经纬度:{}",alarmVo.getAlarmCode(),alarmVo.getLatitude()+Constants.COMMA+alarmVo.getLongitude(),latitude+Constants.COMMA+longitude);
            alarmVo.setLatitude(latitude);
            alarmVo.setLongitude(longitude);
            //替换经纬度中的逗号
            if(!StringUtils.isEmpty(alarmVo.getLatitude()) && !StringUtils.isEmpty(alarmVo.getLongitude())){
                alarmVo.setLatitude(alarmVo.getLatitude().replaceAll(Constants.COMMA,""));
                alarmVo.setLongitude(alarmVo.getLongitude().replaceAll(Constants.COMMA,""));
            }
        });
        return receiveSatelliteAlarmVos;
    }


    /**
     * 构建卫星告警参数
     * @param receiveSatelliteAlarmVo，customers，receiveAlarmVOList
     */
    private  List<ReceiveAlarmVO> buildReceiveAlarmVOList(ReceiveSatelliteAlarmApiVo receiveSatelliteAlarmVo, OrderCustomerVO customer, List<ReceiveAlarmVO> receiveAlarmVOList, Result<List<GridByLngLat>> result){
        ReceiveAlarmVO receiveAlarmVO =new ReceiveAlarmVO();
        BeanUtil.copyProperties(receiveSatelliteAlarmVo,receiveAlarmVO);
        receiveAlarmVO.setExtraField(JSON.toJSONString(receiveSatelliteAlarmVo.getExtendField()));
        if(StringUtils.isEmpty(receiveAlarmVO.getAlarmBody())){
            receiveAlarmVO.setAlarmBody("火点面积:" + Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                    orElse(new SatelliteExtendFieldApiVo()).
                    getFireArea());
        }else{
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
        if(!com.chinatower.common.core.utils.StringUtils.isEmpty(customer.getOperationDept())){
            receiveAlarmVO.setOperationDept(customer.getOperationDept()+Constants.COMMA);
        }
        if(!com.chinatower.common.core.utils.StringUtils.isEmpty(customer.getParallelOperationDept())){
            receiveAlarmVO.setParallelOperationDept(customer.getParallelOperationDept()+Constants.COMMA);
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
        try{
            receiveAlarmVO.setAddress(this.getAddressFromAmap(receiveAlarmVO).getAddress());

            // mongo事发地点同步更新
            receiveSatelliteAlarmVo.setAddress(receiveAlarmVO.getAddress());

        }catch (Exception e){
            log.error("========== 获取经纬度异常：longitude={},latitude={},异常原因：{}",receiveAlarmVO.getLongitude(),receiveAlarmVO.getLatitude(),e);
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
     * 调用高德API 通过经纬度返回地址
     * @param receiveAlarmVO
     */
    private ReceiveAlarmVO getAddressFromAmap(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude()));
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
     * 获取当前环境节点
     * @return
     */
    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if(Objects.nonNull(configInfo.get(Constants.RESULT_DATA))){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }

    /**
     * 图片地址转换成obs地址格式
     * @param relativeImgPath
     * @author lyy
     * @date 2022/10/26
     */
    private HttpURLConnection httpUrl = null;
    private String getObsPicUrl(String relativeImgPath) {
        // 避免重复上传
        if(relativeImgPath.contains(Constants.HTTPS_CODE) || relativeImgPath.contains("/apiImg")){
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
        if(!StringUtils.isEmpty(postPicUrl) && !postPicUrl.contains(Constants.HTTPS_CODE)){
            //从字典获取要替换的域名
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.DOMAIN_PRE_FIX);
            String domainPrefix =null;
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                domainPrefix= suppressDictData.getData().get(0).getDictValue();
            }
            if(domainPrefix != null){
                //判断不同的告警推送来源（星地通/航天宏图）匹配替换不同的路径
                if(postPicUrl.contains(Constants.FIRE_IMAGE_CODE) || postPicUrl.contains(Constants.SERVICE_CODE)){
                    String urlBefore = "";
                    if(postPicUrl.contains(Constants.EAGLE_CODE) && postPicUrl.contains(Constants.SERVICE_CODE)){
                        int index = postPicUrl.indexOf(Constants.SERVICE_CODE);
                        urlBefore = postPicUrl.substring(index);
                        postPicUrl=domainPrefix+Constants.API_IMG_CODE+Constants.TWO+urlBefore;
                    }else if(postPicUrl.contains(Constants.NJHT_CODE) && postPicUrl.contains(Constants.FIRE_IMAGE_CODE)){
                        int index = postPicUrl.indexOf(Constants.FIRE_IMAGE_CODE);
                        urlBefore = postPicUrl.substring(index);
                        postPicUrl=domainPrefix+Constants.API_IMG_CODE+urlBefore;
                    }
                }
            }
        }
        return postPicUrl;
    }

}
