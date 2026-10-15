package com.chinatower.event.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.vo.ExtraInfoVO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.constant.AlarmConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.util.SystemUtil;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dataobject.WarningInterceptDO;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.ThreadDto.InterceptOrderResultDto;
import com.chinatower.event.dto.ThreadDto.OrderInfoFuncDto;
import com.chinatower.event.dto.ThreadDto.Param;
import com.chinatower.event.entity.dto.WarningOrderDTO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.lntlagservice.AngleUtils;
import com.chinatower.event.mapper.*;
import com.chinatower.event.mongodb.InterceptMongoMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.service.InterceptService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.DistanceUtils;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.event.vo.AlarmOrderRecordVO;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.event.vo.SatelliteExtendFieldVo;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.VideoPresetDO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.event.constants.Constants.WARNING_SOURCE_1;

/**
 * <p>
 * 告警拦截信息表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
@Service
@Slf4j
public class WarningInterceptRepositoryImpl extends ServiceImpl<WarningInterceptMapper, WarningInterceptDO> implements IWarningInterceptRepository {

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IAlarmOperateRepository alarmOperateRepository;

    @Autowired
    private ProcessOrderMapper processOrderMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private ISysAlarmTagsRepository sysAlarmTagsRepository;

    @Autowired
    private IAlarmLableRelRepository alarmLableRelRepository;

    @Autowired
    private IVideoDeviceService videoDeviceService;

    @Autowired
    private IAlarmTypeRepository iAlarmTypeRepository;

    @Autowired
    private ISysService sysService;

    @Autowired
    private FlowLinkService flowLinkService;

    @Lazy
    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private VmAlarmSuppressStrategyMapper strategyDao;

    @Autowired
    private PushThirdService pushThirdService;

    @Autowired
    private VideoPresetMapper videoPresetMapper;

    @Autowired
    private ICommonRepository iCommonRepository;

    @Autowired
    private IWarningOrderRepository iWarningOrderRepository;

    @Autowired
    private SystemUtil systemUtil;

    @Autowired
    private InterceptService interceptService;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * 功能描述: 回收站列表
     *
     * @param dto
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,11,12",areaPro = "dataTable.province_code"
            ,areaCity = "dataTable.city_code",areaCode = "dataTable.county_code",typeValueColumn = "dataTable.warning_type_id")
    public IntercepiResultVo getInterceptList(WarningInterceptDTO dto) {

        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (dto.getPageNum() == null) {
            dto.setPageNum(Constants.PAGE_NUM);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(Constants.PAGE_SIZE);
        }
        List<DeviceInfoVO> deviceInfoList = null;
        // 通过摄像机厂家查询数据
        /*if (!StringUtils.isEmpty(dto.getModelId())) {
            deviceInfoList = deviceRepository.getDeviceInfoList(new DeviceInfoReq().setDataScope(dto.getDataScope()).setModelId(dto.getModelId()));
            if (deviceInfoList.isEmpty()) {
                return new ArrayList<Map<String, Object>>();
            }
            // 将查询出的设备编码放入请求参数中
            dto.setDeviceCodes(deviceInfoList.stream().map(DeviceInfoVO::getDeviceCode).collect(Collectors.toList()));
        }*/
        //跨库获取页面查询设备信息关联数据
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        alarmOrderDTO.setDeviceLabelId(dto.getDeviceLabelId());
        alarmOrderDTO.setDeviceLabelIdList(dto.getDeviceLabelIdList());
        alarmOrderDTO.setOrgId(dto.getOrgId());
        alarmOrderDTO.setOrgIdList(dto.getOrgIdList());
        //摄像厂家id多选时用modelIds接收
        alarmOrderDTO.setModelIds(dto.getModelIds());
        alarmOrderDTO.setModelId(dto.getModelId());
        alarmOrderDTO.setTenantId(loginUser.getUser().getTenantId());
        alarmOrderDTO.setSysUser(loginUser.getUser());
        alarmOrderDTO.setDeviceName(dto.getDeviceName());
        alarmOrderDTO.setIotDeviceCodeOrName(dto.getIotDeviceCodeOrName());
        //处理传入的组织条件
        warningOrderRepository.handleOrgIdList(alarmOrderDTO);
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        //跨库获取页面查询设备信息关联数据
        Function<AlarmOrderDTO,List<AlarmOrderByDeviceDTO>> fun1= this::getAlarmOrderByDeviceDTOS;
        paramMap.put(fun1,alarmOrderDTO);

        dto.setDeviceCodeList(alarmOrderDTO.getDeviceCodeList());
        dto.setSatelliteList(alarmOrderDTO.getSatelliteList());
        dto.setRadarList(alarmOrderDTO.getRadarList());
        dto.setUserIdList(alarmOrderDTO.getUserIdList());
        //告警类型标签
        Param param = new Param();
        AlarmOrderDTO orderDTO = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto,orderDTO);
        param.setDto(orderDTO);
        param.setInfo(null);
        param.setUser(loginUser);
        Function<Param, Param> fun2= this::getTableDataInfo;
        paramMap.put(fun2,param);
        List<AlarmLableRelDTO> alarmLableAllList = alarmLableRelRepository.getAlarmLableByIds(new ArrayList<>(),"");

        //告警标签
        Function<AlarmOrderDTO,List<AlarmLableRelDTO>> fun3= this:: getAlarmLableRelDTOS;
        paramMap.put(fun3,orderDTO);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc, f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_INTERCEPT);
        if (CollectionUtils.isEmpty(alarmOrderDTO.getDevList())
                && (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(dto.getModelId()) || !StringUtils.isEmpty(dto.getDeviceName()) ||
                !CollectionUtils.isEmpty(dto.getModelIds()) || !StringUtils.isEmpty(dto.getIotDeviceCodeOrName()))) {
            return new IntercepiResultVo();
        }
        dto.setDevList(alarmOrderDTO.getDevList());
        dto.setWarningTypeIdList(orderDTO.getWarningTypeIdList());
        if (!CollectionUtils.isEmpty(orderDTO.getAlarmLableRelDTOList())) {
            List<String> warningOrderIds = new ArrayList<>();
            orderDTO.getAlarmLableRelDTOList().forEach(item -> {
                if (!StringUtils.isEmpty(item.getWaringOrderId())) {
                    warningOrderIds.add(item.getWaringOrderId());
                }
            });
            if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                dto.setWarningOrderIds(warningOrderIds);
            }
        }
        else if(!CollectionUtils.isEmpty(dto.getLableIdList())) {
            return new IntercepiResultVo();
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        //List<InterceptVO> resultList = warningInterceptMapper.getInterceptList(dto);
        //List<InterceptVO> resultList = iAlarmService.getInterceptList(dto);
        IntercepiResultVo resultVo = interceptService.getInterceptList(dto);
        List<InterceptVO> resultList = resultVo.getResultList();
        if (!CollectionUtils.isEmpty(resultList)) {
            // 查询告警类型名称
            List<Map> alarmTypeList = warningOrderRepository.getAlarmTypeList();
            //获取事件类型图片（APP）
            List<Map> alarmIconList = iAlarmTypeRepository.getAlarmIconList();

            List<String> paramList = new ArrayList<>();
            paramList.add(Constants.ALARM_LEVEL);
            paramList.add(Constants.AI_NOTICE_RESOURCE);
            paramList.add(Constants.DICT_TYPE_ALARM_PRESSING_REASON);
            paramList.add(Constants.WARNING_SOURCE_COMMON);
            // 重要等级
            AtomicReference<List<SysDictDataResponse>> importanceLevelEnum = new AtomicReference<>();
            // 获取 AI告警来源
            AtomicReference<List<SysDictDataResponse>> dictData = new AtomicReference<>();
            // 获取 告警压制原因字段
            AtomicReference<List<SysDictDataResponse>> suppressDictData = new AtomicReference<>();
            // 告警来源枚举
            AtomicReference<List<SysDictDataResponse>> warningSourceEnum = new AtomicReference<>();
            ThreadParallelUtil.run(paramList,o->{
                if(Constants.ALARM_LEVEL.equals(String.valueOf(o))){
                    importanceLevelEnum.set(systemUtil.getEnum(o));
                }else if(Constants.AI_NOTICE_RESOURCE.equals(String.valueOf(o))){
                    dictData.set(systemUtil.getEnum(o));
                }else if(Constants.DICT_TYPE_ALARM_PRESSING_REASON.equals(String.valueOf(o))){
                    suppressDictData.set(systemUtil.getEnum(o));
                }
                else if(Constants.WARNING_SOURCE_COMMON.equals(String.valueOf(o))){
                    warningSourceEnum.set(systemUtil.getEnum(o));
                }
                return null;
            }, ThreadParallelUtil.ConfigFactory.WARNING_CODE);
            // 获取摄像机列表信息
            if (deviceInfoList == null) {
                deviceInfoList = deviceRepository.getDeviceInfoList(new DeviceInfoReq().setDeviceCodes(
                        resultList.stream().map(e -> String.valueOf(e.getDeviceCode())).collect(Collectors.toList())
                ));
            }
            //v1.7.00 查询组织信息
            List<OrganInfoReq> params = resultList.stream().map(item -> {
                OrganInfoReq reqDTO = new OrganInfoReq();
                reqDTO.setWarningOrderId(item.getWarningOrderId());
                reqDTO.setWarningSource(item.getWarningSource());
                reqDTO.setDeviceCode(item.getDeviceCode());
                reqDTO.setProvinceId(item.getProvinceCode());
                reqDTO.setCityId(item.getCityCode());
                reqDTO.setCountyId(item.getCountyCode());
                reqDTO.setTenantId(loginUser.getUser().getTenantId());
                reqDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
                return reqDTO;
            }).collect(Collectors.toList());
            List<Map<String, String>> orgInfoList = warningOrderRepository.getOrgInfoList(params);
           List<InterceptOrderResultDto> resultDtoList = new ArrayList<>();
            List<TenantDeviceRelDTO> tenantDeviceRelDTOList = new ArrayList<>();
            for (InterceptVO orderInfoVO : resultList) {
                TenantDeviceRelDTO tenantDeviceRelDTO = new TenantDeviceRelDTO();
                tenantDeviceRelDTO.setDeviceCode(orderInfoVO.getDeviceCode());
                tenantDeviceRelDTO.setTenantId(orderInfoVO.getTenantId());
                tenantDeviceRelDTO.setChannelCode(orderInfoVO.getChannelCode());
                tenantDeviceRelDTOList.add(tenantDeviceRelDTO);
            }
            if(!CollectionUtils.isEmpty(tenantDeviceRelDTOList)){
                List<TenantDeviceEventRelVO> tenantDeviceEventRelVOList = deviceRepository.getDeviceRel(tenantDeviceRelDTOList);
                if (!CollectionUtils.isEmpty(tenantDeviceEventRelVOList)) {
                    for (TenantDeviceEventRelVO vo:tenantDeviceEventRelVOList) {
                        for (InterceptVO orderInfoVO : resultList) {
                            if (vo != null && vo.getDeviceCode().equals(orderInfoVO.getDeviceCode())) {
                                orderInfoVO.setDeviceName(vo.getDeviceName());
                            }
                            if(vo != null && vo.getChannelCode().equals(orderInfoVO.getChannelCode())){
                                orderInfoVO.setChannelName(vo.getChannelName());
                            }
                        }
                    }
                }
            }
            for (InterceptVO orderInfoVO : resultList){
                if(!StringUtils.isEmpty(dto.getUserLatitude()) && !StringUtils.isEmpty(dto.getUserLongitude())
                && !StringUtils.isEmpty(orderInfoVO.getLatitude()) && !StringUtils.isEmpty(orderInfoVO.getLongitude())){
                    double dis = DistanceUtils.getDistance(Double.parseDouble(dto.getUserLongitude()),Double.parseDouble(dto.getUserLatitude()),
                            Double.parseDouble(orderInfoVO.getLongitude()),Double.parseDouble(orderInfoVO.getLatitude()));
                    orderInfoVO.setUserDistance(String.valueOf(dis));
                }
                InterceptOrderResultDto resultDto = new InterceptOrderResultDto();
                resultDto.setDeviceInfoList(deviceInfoList);
                resultDto.setAlarmLableAllList(alarmLableAllList);
                resultDto.setAlarmTypeList(alarmTypeList);
                resultDto.setAlarmIconList(alarmIconList);
                resultDto.setImportanceLevelEnum(importanceLevelEnum.get());
                resultDto.setDictData(dictData.get());
                resultDto.setSuppressDictData(suppressDictData.get());
                resultDto.setWarningSourceEnum(warningSourceEnum.get());
                resultDto.setOrderInfoVO(orderInfoVO);
                resultDtoList.add(resultDto);

                //获取卫星告警专题图和红外图
                if (Constants.NINE.equals(orderInfoVO.getWarningSource())){
                    if (!StringUtil.isEmpty(orderInfoVO.getExtraField()) && !"null".equals(orderInfoVO.getExtraField())){
                        SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(orderInfoVO.getExtraField(),SatelliteExtendFieldVo.class);
                        orderInfoVO.setRelativePathThem(extendFieldVo.getRelativePathThem());
                        orderInfoVO.setRelativePathMir(extendFieldVo.getRelativePathMir());
                    }
                }
                //v1.7.00 展示所属组织
                if (!CollectionUtils.isEmpty(orgInfoList)) {
                    for (Map<String, String> map : orgInfoList) {
                        String orderId = orderInfoVO.getWarningOrderId();
                        if (map.containsKey(orderId)) {
                            orderInfoVO.setOrgName(map.get(orderId));
                            break;
                        }
                    }
                }
            }
            ThreadParallelUtil.run(resultDtoList,f->{
                extracted(f);
                return null;
            }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
        }
        resultVo.setResultList(resultList);
        return resultVo;
    }

    List<AlarmOrderByDeviceDTO> getAlarmOrderByDeviceDTOS(AlarmOrderDTO dto) {
        List<AlarmOrderByDeviceDTO> devList;
        if(!StringUtils.isEmpty(dto.getIotDeviceCodeOrName())){
            devList = deviceRepository.getAlarmOrderByIotDevice(dto);
        } else {
            devList = deviceRepository.queryAlarmOrderByDevice(dto);
        }
        if(!CollectionUtils.isEmpty(devList)){
            dto.setDevList(devList);
        }
        return devList;
    }

    List<AlarmLableRelDTO> getAlarmLableRelDTOS(AlarmOrderDTO dto) {
        List<AlarmLableRelDTO> alarmLableRelDTOList = alarmLableRelRepository.getAlarmLableByIds(dto.getLableIdList(), dto.getOrderId());
        dto.setAlarmLableRelDTOList(alarmLableRelDTOList);
        return alarmLableRelDTOList;
    }

    Param getTableDataInfo(Param param) {
        List<AlarmTagsTypeRelDTO> alarmTagsTypeRelDTOList = sysAlarmTagsRepository.getAlarmTypeByIds(param.getDto().getTagIdList(), param.getUser().getUser().getTenantId());

        List<String> warningTypeIdList = new ArrayList<>();
        alarmTagsTypeRelDTOList.forEach(alarmTagsTypeRelDTO -> {
            if (!StringUtils.isEmpty(alarmTagsTypeRelDTO.getTypeValue())) {
                warningTypeIdList.add(alarmTagsTypeRelDTO.getTypeValue());
            }
        });
        if (!CollectionUtils.isEmpty(param.getDto().getWarningTypeIdList())) {
            warningTypeIdList.addAll(param.getDto().getWarningTypeIdList());
        }
        param.getDto().setWarningTypeIdList(warningTypeIdList);
        return param;
    }
    private void extracted(InterceptOrderResultDto resultDto) {

        if(!StringUtils.isEmpty(resultDto.getOrderInfoVO().getUserDistance())){
            String userDistance = resultDto.getOrderInfoVO().getUserDistance();
            String cleanedUserDistance = userDistance.replaceAll("[^\\d.]", ""); // 移除非数字字符
            double num = Double.parseDouble(cleanedUserDistance);
            DecimalFormat df = new DecimalFormat("#.00");
            String realDistance = "";
            if(num>1000){
                //如果大于一千米，单位为千米保留两位小数
                double result = num / 1000.0;
                realDistance = df.format(result);
                resultDto.getOrderInfoVO().setUserDistance(realDistance+"千米");
            }else{
                BigDecimal decimal = new BigDecimal(cleanedUserDistance);
                int rounded = decimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                resultDto.getOrderInfoVO().setUserDistance(String.valueOf(rounded)+"米");
            }
        }
        // 设置 置信度
        /// 置信度 取最大置信度展示 -- 新增综合置信度，若存在综合置信度则展示综合置信度
        if (!StringUtils.isEmpty(resultDto.getOrderInfoVO().getCompConfidenceLevel())) {
            resultDto.getOrderInfoVO().setConfidenceLevel(resultDto.getOrderInfoVO().getCompConfidenceLevel());
        }
        else {
            String confidenceLevel = resultDto.getOrderInfoVO().getConfidenceLevel();
            if (!StringUtils.isEmpty(confidenceLevel)) {
                String[] confidenceLevelList = confidenceLevel.split(",");
                String confidenceLevelNew = Arrays.stream(confidenceLevelList).max(String::compareTo).get();
                resultDto.getOrderInfoVO().setConfidenceLevel(confidenceLevelNew);
            }
        }

        String imgUrl  = resultDto.getOrderInfoVO().getImgUrl();
        // 处理图片，多张图片取第一张
        if (!StringUtils.isEmpty(imgUrl) && imgUrl.indexOf(",") > -1) {
            resultDto.getOrderInfoVO().setImgUrl(resultDto.getOrderInfoVO().getImgUrl().split(",")[0]);
        }

        String imgUrlIcon = resultDto.getOrderInfoVO().getFileImgUrlIcon();
        // 处理图片，多张图片取第一张
        if (!StringUtils.isEmpty(imgUrlIcon) && imgUrlIcon.indexOf(",") > -1) {
            resultDto.getOrderInfoVO().setFileImgUrlIcon(resultDto.getOrderInfoVO().getFileImgUrlIcon().split(",")[0]);
        }
        // 设置 AI 告警来源
        if (resultDto.getDictData() != null && !resultDto.getDictData().isEmpty()) {
            resultDto.getDictData().forEach(e -> {
                if (ObjectUtils.nullSafeEquals(resultDto.getOrderInfoVO().getNoticeResource(), e.getDictValue())) {
                    resultDto.getOrderInfoVO().setNoticeResource(e.getDictLabel());
                }
            });
        }
        // 设置告警来源
        if (!CollectionUtils.isEmpty(resultDto.getWarningSourceEnum())) {
            resultDto.getWarningSourceEnum().forEach(item -> {
                if (ObjectUtils.nullSafeEquals(resultDto.getOrderInfoVO().getWarningSource(), item.getDictValue())) {
                    resultDto.getOrderInfoVO().setWarningSourceName(item.getDictLabel());
                }
            });
        }

        //设置告警压制原因
        if (resultDto.getSuppressDictData() != null && !resultDto.getSuppressDictData().isEmpty()) {
            resultDto.getSuppressDictData().forEach(e -> {
                if (ObjectUtils.nullSafeEquals(resultDto.getOrderInfoVO().getIntercept(), e.getDictValue())) {
                    resultDto.getOrderInfoVO().setInterceptName(e.getDictLabel());
                }
            });
        }

        //设置告警压制原因
        if (resultDto.getSuppressDictData() != null && !resultDto.getSuppressDictData().isEmpty()) {
            resultDto.getSuppressDictData().forEach(e -> {
                if (ObjectUtils.nullSafeEquals(resultDto.getOrderInfoVO().getIntercept(), e.getDictValue())) {
                    resultDto.getOrderInfoVO().setInterceptName(e.getDictLabel());
                }
            });
        }

        // 设置 摄像机信息 挂高、经纬度、厂商
        if (!CollectionUtils.isEmpty(resultDto.getDeviceInfoList())) {
            resultDto.getDeviceInfoList().forEach(e -> {
                if (e.getDeviceCode().equals(String.valueOf(resultDto.getOrderInfoVO().getDeviceCode()))) {
                    resultDto.getOrderInfoVO().setHeight(e.getHeight());
                    resultDto.getOrderInfoVO().setVideoLongitude(e.getLatitude());
                    resultDto.getOrderInfoVO().setVideoLongitude(e.getLongitude());
                    resultDto.getOrderInfoVO().setModelName(e.getModelName());
                    resultDto.getOrderInfoVO().setModelId(e.getModelId());
                }
            });
        }

        //获取设备通道别名
        //warningOrderRepository.getDeviceRel(resultDto.getOrderInfoVO());

        resultDto.getAlarmTypeList().forEach(item -> {
            if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(resultDto.getOrderInfoVO().getWarningTypeId())) {
                resultDto.getOrderInfoVO().setWarningTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
            }
        });
        //获取事件类型图片（APP）
        resultDto.getAlarmIconList().forEach(item -> {
            if(StringUtil.isNotEmpty(item.get(Constants.PARAM_TYPE_VALUE).toString())) {
                if (item.get(Constants.PARAM_TYPE_VALUE).toString().equals(resultDto.getOrderInfoVO().getWarningTypeId())) {
                    resultDto.getOrderInfoVO().setIcon(String.valueOf(item.get(Constants.PARAM_ICON)));
                }
            }
        });

        resultDto.getImportanceLevelEnum().forEach(item -> {
            if (item.getDictValue().equals(resultDto.getOrderInfoVO().getImportanceLevel())) {
                resultDto.getOrderInfoVO().setImportanceLevelName(item.getDictLabel());
            }
        });

        //是否二次研判、二次研判结果翻译
        resultDto.getOrderInfoVO().setIsSecondJudge(getAlarmSecondJudgeNameByCode(resultDto.getOrderInfoVO().getIsSecondJudge()));
        resultDto.getOrderInfoVO().setSecondJudgeResult(getAlarmSecondJudgeResultNameByCode(resultDto.getOrderInfoVO().getSecondJudgeResult()));
                /*if (!CollectionUtils.isEmpty(deviceNames)) {
                    deviceNames.forEach(item -> {
                        if (item.get(Constants.PARAM_DEVICE_CODE).toString().equals(m.get(Constants.PARAM_DEVICE_CODE))) {
                            m.put(Constants.PARAM_DEVICE_NAME, item.get(Constants.PARAM_DEVICE_NAME));
                        }
                    });
                }*/

        //告警标签回显
        List<AlarmLableRelAPIDTO> alarmLableRelDTOSNew = new ArrayList<>();
        List<AlarmLableRelDTO> alarmLableRelAPIDTOs= resultDto.getAlarmLableAllList().stream().filter(item -> resultDto.getOrderInfoVO().getWarningOrderId().equals(item.getWaringOrderId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(alarmLableRelAPIDTOs)){
            alarmLableRelAPIDTOs.forEach(itemRe ->{
                AlarmLableRelAPIDTO relAPIDTO = new AlarmLableRelAPIDTO();
                BeanUtils.copyProperties(itemRe,relAPIDTO);
                alarmLableRelDTOSNew.add(relAPIDTO);
            });
        }
        resultDto.getOrderInfoVO().setAlarmLableRelDTOS(alarmLableRelDTOSNew);
    }

    /**
     * 功能说明：获取二次研判结果值
     *
     * @author: zhang.xionghui
     * @param: code
     * @return: String
     * @date: 2022/8/10 10:12
     * @requestID  需求单号及需求名称
     */
    public String getAlarmSecondJudgeResultNameByCode(String code){
        for (EnumUtil.AlarmSecondJudgeResult alarmSecondJudgeResult : EnumUtil.AlarmSecondJudgeResult.values()) {
            if (alarmSecondJudgeResult.getCode().equals(code)) {
                return alarmSecondJudgeResult.getName();
            }
        }
        return "";
    }
    /**
     * 功能说明：获取是否二次研判值
     *
     * @author: zhang.xionghui
     * @param: code
     * @return: String
     * @date: 2022/8/10 10:12
     * @requestID  需求单号及需求名称
     */
    public String getAlarmSecondJudgeNameByCode(String code){
        for (EnumUtil.AlarmSecondJudge alarmSecondJudge : EnumUtil.AlarmSecondJudge.values()) {
            if (alarmSecondJudge.getCode().equals(code)) {
                return alarmSecondJudge.getName();
            }
        }
        return "";
    }

    /**
     * 详情
     *
     * @param dto
     * @return
     */
    @Override
    public List<Map> getInterceptInfo(WarningInterceptDTO dto) {

        // 如果是行业调用接口，且有传userid,使用行业传入userid, 否则用户登入信息中获取
        if (!Constants.YES_FLAG.equals(dto.getIsTrade()) || StringUtils.isEmpty(dto.getUserId())) {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            dto.setUserId(sysService.getLoginUserId());
            dto.setTradeId(loginUser.getUser().getIndustryCode());
        }
        //OrderInfoVO orderInfoVO = warningInterceptMapper.getInterceptInfo(dto.getOrderId(),dto.getUserId());
        InterceptVO vo = interceptService.getInterceptInfo(dto.getOrderId(),dto.getUserId());

        if (null == vo) {
            return null;
        }
        //保存原有属性，作为返回值
        InterceptVO interceptVO = new InterceptVO();
        BeanUtil.copyProperties(vo, interceptVO);
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        BeanUtil.copyProperties(vo,orderInfoVO);
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        Function<OrderInfoVO,OrderInfoVO> fun1= this::getPresetName;
        paramMap.put(fun1,orderInfoVO);
        //置信度 取最大置信度展示  -- 新增综合置信度，若存在综合置信度则展示综合置信度
        if (!StringUtils.isEmpty(orderInfoVO.getCompConfidenceLevel())) {
            orderInfoVO.setConfidenceLevel(orderInfoVO.getCompConfidenceLevel());
        }
        else {
            String confidenceLevel = orderInfoVO.getConfidenceLevel();
            if (!StringUtils.isEmpty(confidenceLevel)) {
                String[] confidenceLevelList = confidenceLevel.split(",");
                String confidenceLevelNew = Arrays.stream(confidenceLevelList).max(String::compareTo).get();
                orderInfoVO.setConfidenceLevel(confidenceLevelNew);
            }
        }
        String fileImgUrlSrc = orderInfoVO.getFileImgUrlSrc();
        String fileImgUrlIcon = orderInfoVO.getFileImgUrlIcon();
        String videoUrl = orderInfoVO.getVideoUrl();
        String frontAlarmPicUrl = orderInfoVO.getFrontAlarmPicUrl();
        String frontAlarmVedioUrl = orderInfoVO.getFrontAlarmVedioUrl();
        OrderInfoFuncDto funcDto = new OrderInfoFuncDto();
        funcDto.setOrderInfoVO(orderInfoVO);
        funcDto.setFileImgUrlIcon(fileImgUrlSrc);
        funcDto.setFileImgUrlIcon(fileImgUrlIcon);
        funcDto.setVideoUrl(videoUrl);
        Function<OrderInfoFuncDto, OrderInfoVO> fun2= this::doGetAlarmImgs;
        paramMap.put(fun2,funcDto);
        //获取图斑
        Function<OrderInfoVO, OrderInfoVO> fun3= this::getPolygonOperate;
        paramMap.put(fun3,orderInfoVO);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc,f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_INTERCEPT);
        // 告警来源枚举
        List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
        warningSourceEnum.forEach(item -> {
            if (item.getDictValue().equals(orderInfoVO.getWarningSource())) {
                orderInfoVO.setWarningSourceName(item.getDictLabel());
            }
        });
        // 告警类型名称
        List<Map> alramTypeList = warningOrderRepository.getAlarmTypeList();
        List<String> paramList = new ArrayList<>();
        paramList.add(Constants.ALARM_LEVEL);
        paramList.add(String.valueOf(Constants.NOTICE_RESOURCE));
        paramList.add(String.valueOf(Constants.MODEL_ID));
        paramList.add(Constants.IOT_MODEL_ID);
        paramList.add(Constants.DICT_TYPE_ALARM_PRESSING_REASON);
        paramList.add(String.valueOf(Constants.UAV_MANUFACTURER));
        paramList.add(String.valueOf(Constants.UAV_NEST_MANUFACTURER));
        // 告警重要等级名称
        AtomicReference<List<SysDictDataResponse>> importanceLevelEnum = new AtomicReference<>();
        //摄像机厂商
        AtomicReference<List<SysDictDataResponse>> noticeResourceEnum = new AtomicReference<>();
        // 物联设备厂商
        AtomicReference<List<SysDictDataResponse>> modelIdEnum = new AtomicReference<>();
        // 消息类型
        AtomicReference<List<SysDictDataResponse>> iotModelIdEnum = new AtomicReference<>();
        // 通知类型
        AtomicReference<List<SysDictDataResponse>> suppressDictData = new AtomicReference<>();
        //无人机厂家
        AtomicReference<List<SysDictDataResponse>> UavIdEnum = new AtomicReference<>();
        //机巢厂家
        AtomicReference<List<SysDictDataResponse>> UavNestIdEnum = new AtomicReference<>();
        ThreadParallelUtil.run(paramList,o->{
            if(Constants.ALARM_LEVEL.equals(String.valueOf(o))){
                importanceLevelEnum.set(systemUtil.getEnum(o));
            }else if(String.valueOf(Constants.NOTICE_RESOURCE).equals(String.valueOf(o))){
                noticeResourceEnum.set(systemUtil.getEnum(o));
            }else if(String.valueOf(Constants.MODEL_ID).equals(String.valueOf(o))){
                modelIdEnum.set(systemUtil.getEnum(o));
            }else if(Constants.IOT_MODEL_ID.equals(String.valueOf(o))){
                iotModelIdEnum.set(systemUtil.getEnum(o));
            }else if(Constants.DICT_TYPE_ALARM_PRESSING_REASON.equals(String.valueOf(o))){
                suppressDictData.set(systemUtil.getEnum(o));
            }else if(Constants.UAV_MANUFACTURER.equals(String.valueOf(o))){
                UavIdEnum.set(systemUtil.getEnum(o));
            }else if(Constants.UAV_NEST_MANUFACTURER.equals(String.valueOf(o))){
                UavNestIdEnum.set(systemUtil.getEnum(o));
            }
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_CODE);
        // 设置 AI 告警来源
        if (!CollectionUtils.isEmpty(noticeResourceEnum.get())) {
            noticeResourceEnum.get().forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getNoticeResource())) {
                    orderInfoVO.setNoticeResourceName(item.getDictLabel());
                }
            });
        }
        // 获取网格员
        if (!StringUtils.isEmpty(orderInfoVO.getGridId())) {
            orderInfoVO.setKeeperName(deviceRepository.getGridKeeperByGridId(orderInfoVO.getGridId()));
            //获取网格信息
            GridInfo gridInfo = warningOrderUtil.getGrifInfo(Long.valueOf(orderInfoVO.getGridId()));
            orderInfoVO.setGridInfo(gridInfo);
        }
        if (!CollectionUtils.isEmpty(importanceLevelEnum.get())) {
            importanceLevelEnum.get().forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getImportanceLevel())) {
                    orderInfoVO.setImportanceLevelName(item.getDictLabel());
                }
            });
        }
        if (!CollectionUtils.isEmpty(alramTypeList)) {
            // 设置图标
            alramTypeList.forEach(item -> {
                if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(orderInfoVO.getWarningTypeId())) {
                    orderInfoVO.setWarningTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
                    orderInfoVO.setIcon((String) item.get(Constants.PARAM_ICON));

                }
            });
        }

        // 告警等级名称
        List<SysDictDataResponse> emergencyLevelEnum = warningOrderRepository.getEnumList(Constants.EMERGENCY_LEVEL);
        if (!CollectionUtils.isEmpty(emergencyLevelEnum)) {
            emergencyLevelEnum.forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getEmergencyLevel())) {
                    orderInfoVO.setEmergencyLevelName(item.getDictLabel());
                }
            });
        }

        //设置告警压制原因
        if (suppressDictData.get() != null && !suppressDictData.get().isEmpty()) {
            suppressDictData.get().forEach(e -> {
                if (ObjectUtils.nullSafeEquals(orderInfoVO.getIntercept(), e.getDictValue())) {
                    orderInfoVO.setInterceptName(e.getDictLabel());
                }
            });
        }

        //查询联动算法名称
        if(!StringUtils.isEmpty(orderInfoVO.getLinkageAlgorithm())){
            orderInfoVO.setLinkageAlgorithm(deviceMapper.getAlgorithmName(orderInfoVO.getLinkageAlgorithm()));
        }

        List<Map> imgs = warningOrderUtil.processImgsOrder(orderInfoVO, dto.getUserId());
        Map imgUrl;
        /*if (!StringUtils.isEmpty(orderInfoVO.getFileImgUrlIcon())) {
            String[] imgIcons = orderInfoVO.getFileImgUrlIcon().split(Constants.COMMA);
            if (!StringUtils.isEmpty(imgIcons) && imgIcons.length > 0) {
                for (String img : imgIcons) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    imgs.add(imgUrl);
                }
            }
        }*/

        // 设置多组物联设备信息
        warningOrderUtil.buildMultIotDeviceInfo(orderInfoVO, iotModelIdEnum, modelIdEnum);

        //无人机告警来源
        warningOrderUtil.uavAlarmSource(orderInfoVO,UavIdEnum,UavNestIdEnum);

        //物联设备告警
        List<SensorMonitorVo> monitors = new ArrayList<>();
        IotRelDeviceVo reldevice = new IotRelDeviceVo();
        if(AlarmConstants.ALARM_SOURCE_8.equals(orderInfoVO.getWarningSource()) && !StringUtils.isEmpty(orderInfoVO.getDeviceCode())){
            try{
                monitors = JSON.parseObject(orderInfoVO.getExtraField()).getJSONArray("monitors").toJavaList(SensorMonitorVo.class);
                //查询物联设备关联摄像机信息
                TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
                qryCond.setDeviceCode(orderInfoVO.getDeviceCode());
                qryCond.setTenantId(orderInfoVO.getTenantId());
                qryCond.setIndustryCode(orderInfoVO.getIndustryCode());
                try{
                    Result<TenantIotDeviceConfVO> result = videoDeviceService.getTenantIotDevConf(qryCond);
                    if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                        List<TenantIotConfRelVO> iotDeviceConf = result.getData().getConfRels();
                        if(iotDeviceConf!=null&&!iotDeviceConf.isEmpty()){
                            TenantIotConfRelVO iotConfRelVO = iotDeviceConf.get(0);
                            BeanUtil.copyProperties(iotConfRelVO,reldevice);
                        }
                    }
                }catch(Exception e){
                    log.error("物联设备关联摄像机信息查询异常，工单号：{}，异常：{}", orderInfoVO.getOrderId(), e);
                }
            }catch(Exception e){
                log.error("物联设备因子和关联摄像机信息查询异常，工单号：{}，异常：{}", orderInfoVO.getOrderId(), e);
            }
        }
        SatelliteRelDeviceVo radarDevice = new SatelliteRelDeviceVo();
        if (WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
            if(!StringUtils.isEmpty(orderInfoVO.getDeviceCode())){
                DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(orderInfoVO.getDeviceCode(),null);
                if(deviceSiteVO != null){
                    BeanUtil.copyProperties(deviceSiteVO,radarDevice);
                    radarDevice.setDeviceCode(orderInfoVO.getDeviceCode());
                    //摄像机厂商
                    if (!CollectionUtils.isEmpty(modelIdEnum.get())) {
                        modelIdEnum.get().forEach(item -> {
                            if (Objects.equals(deviceSiteVO.getModelId(), item.getDictValue())) {
                                radarDevice.setModelCodeName(item.getDictLabel());
                            }
                        });
                    }
                }
            }
            orderInfoVO.setRadarDevice(radarDevice);
            //雷达告警
            List<DeviceSiteInfoVO> deviceSiteList = new ArrayList<>();
            //扩展字段
            String extraField = orderInfoVO.getExtraField();
            //雷达设备编码
            Object[] radarCodes = null;
            if(!StringUtils.isEmpty(extraField)){
                try {
                    JSONObject jsonExtraField = JSONObject.parseObject(extraField);
                    try {
                        if (jsonExtraField.containsKey(Constants.RADAR_CODES)) {
                            radarCodes=jsonExtraField.getJSONArray(Constants.RADAR_CODES).toArray();
                        }
                    } catch (Exception e) {
                        log.error("获取雷达详情信息失败，参数：{}", extraField, e);
                    }
                } catch (Exception e) {
                    log.error("orderInfoVO.getExtraField()转换JSON异常，参数：{}", extraField, e);
                }
            }
            String tenantId = orderInfoVO.getTenantId();
            String industryCode = orderInfoVO.getTradeId();
            DeviceSiteVO deviceSiteVO = new DeviceSiteVO();
            if(radarCodes != null) {
                for (Object radarCode : radarCodes) {
                    DeviceSiteInfoVO deviceSite = new DeviceSiteInfoVO();
                    deviceSiteVO = deviceRepository.queryRadarDevice(tenantId, industryCode, radarCode.toString());
                    if (deviceSiteVO != null) {
                        //实体转换
                        BeanUtils.copyProperties(deviceSiteVO, deviceSite);
                        //雷达设备厂商
                        if (!CollectionUtils.isEmpty(iotModelIdEnum.get())) {
                            iotModelIdEnum.get().forEach(item -> {
                                if (Objects.equals(deviceSite.getModelId(), item.getDictValue())) {
                                    deviceSite.setModelName(item.getDictLabel());
                                }
                            });
                        }
                        deviceSiteList.add(deviceSite);
                    }
                }
            }
            orderInfoVO.setDeviceSiteList(deviceSiteList);
        }
        /*if (!StringUtils.isEmpty(orderInfoVO.getFileImgUrlSrc())) {
            String[] imgSrcs = (orderInfoVO.getFileImgUrlSrc()).split(Constants.COMMA);
            if (!StringUtils.isEmpty(imgSrcs) && imgSrcs.length > 0) {
                for (String img : imgSrcs) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    imgs.add(imgUrl);
                }
            }
        }*/
        //添加 卫星告警信息--火区面积、下垫面类型字段
        // 卫星火情告警       //技术思考、沟通思路 、主动性、第一行代码的重要性
        List<SatelliteRelDeviceVo> satelliteRelDeviceVos = new ArrayList<>();
        if (AlarmConstants.ALARM_SOURCE_9.equals(orderInfoVO.getWarningSource())
                && Objects.nonNull(orderInfoVO.getExtraField())) {
            try {
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(orderInfoVO.getExtraField(),SatelliteExtendFieldVo.class);
                try {
                    if (!StringUtils.isEmpty(extendFieldVo.getFireArea())){
                        orderInfoVO.setFireArea(extendFieldVo.getFireArea());
                    }
                    if (!StringUtils.isEmpty(extendFieldVo.getUnderlyingSurfaceType())){
                        orderInfoVO.setUnderlyingSurfaceType(extendFieldVo.getUnderlyingSurfaceType());
                    }
                    log.info("====== 获取图片信息 start ：{}======", orderInfoVO.getExtraField());
                    String relativePathPose = extendFieldVo.getRelativePathPose();
                    String relativePathThem = extendFieldVo.getRelativePathThem();
                    String relativePathMir = extendFieldVo.getRelativePathMir();
                    String snapShotTime = extendFieldVo.getSnapShotTime();
                   //卫星关联摄像机设备信息
                    String realDeviceCode = extendFieldVo.getRealDeviceCode();
                    if(!StringUtils.isEmpty(realDeviceCode)){
                        List<String> deviceCodeArray = Arrays.asList(realDeviceCode.split(Constants.COMMA));
                        List<DeviceSiteVO> deviceSiteVOList = deviceRepository.queryDeviceList(deviceCodeArray);
                        //查询设备收藏表
                        List<String> deviceCollectList = deviceMapper.getDeviceCodeCollectByDeviceCodes(deviceCodeArray, "5", String.valueOf(dto.getUserId()));
                        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
                            for (DeviceSiteVO deviceSiteVO : deviceSiteVOList) {
                                SatelliteRelDeviceVo satelliteRelDevice = new SatelliteRelDeviceVo();
                                BeanUtil.copyProperties(deviceSiteVO,satelliteRelDevice);
                                //摄像机厂商
                                if (!CollectionUtils.isEmpty(modelIdEnum.get())) {
                                    modelIdEnum.get().forEach(item -> {
                                        if (Objects.equals(deviceSiteVO.getModelId(), item.getDictValue())) {
                                            satelliteRelDevice.setModelCodeName(item.getDictLabel());
                                        }
                                    });
                                }
                                //设备是否收藏标识（0-否、1-是）
                                satelliteRelDevice.setDeviceCodeCollect(Constants.ZERO);
                                if(!CollectionUtils.isEmpty(deviceCollectList)){
                                    deviceCollectList.forEach(item -> {
                                        if(deviceSiteVO.getDeviceCode().equals(item)){
                                            satelliteRelDevice.setDeviceCodeCollect(Constants.ONE);
                                        }
                                    });
                                }
                                if (!StringUtils.isEmpty(snapShotTime)) {
                                    satelliteRelDevice.setSnapShotTime(snapShotTime);
                                }
                                if(satelliteRelDevice != null){
                                    satelliteRelDeviceVos.add(satelliteRelDevice);
                                }
                            }
                        }
                    }
                    imgs.clear();
                    warningOrderUtil.addImageMap(Optional.ofNullable(relativePathThem).orElse(""), imgs);
                    warningOrderUtil.addImageMap(Optional.ofNullable(relativePathMir).orElse(""), imgs);
                    warningOrderUtil.addImageMap(Optional.ofNullable(orderInfoVO.getFileImgUrlSrc()).orElse(""), imgs);
                    log.info("====== 获取图片信息 end ：{}======", imgs);
                } catch (Exception e) {
                    log.error("获取卫星详情信息失败，参数：{}", orderInfoVO.getExtraField(), e);
                }
            }catch (Exception e){
                log.error("orderInfoVO.getExtraField()转换JSON异常，参数：{}", orderInfoVO.getExtraField(), e);
            }
        }
        orderInfoVO.setSatelliteDeviceList(satelliteRelDeviceVos);

        // 增加摄像机地址
        if (!StringUtils.isEmpty(orderInfoVO.getDeviceCode()) && !WarningSourceEnum.SATELLITE_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
            DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(orderInfoVO.getDeviceCode(),null);
            if (deviceSiteVO != null) {
                orderInfoVO.setDeviceAddress(deviceSiteVO.getLocation());
                orderInfoVO.setModelId(deviceSiteVO.getModelId());
                orderInfoVO.setHeight(deviceSiteVO.getHeight());
                orderInfoVO.setVideoLatitude(deviceSiteVO.getLatitude());
                orderInfoVO.setVideoLongitude(deviceSiteVO.getLongitude());
                // 摄像机告警定位配置判断
                if (Constants.YES_FLAG.equals(deviceSiteVO.getAlarmLocation())) {
                    orderInfoVO.setAlarmLocation(deviceSiteVO.getAlarmLocation());
                }
            }
            if (WarningSourceEnum.IOT_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
                //物联告警
                deviceSiteVO = deviceRepository.queryIotDevice(orderInfoVO.getDeviceCode());
                if (deviceSiteVO != null) {
                    orderInfoVO.setDeviceAddress(deviceSiteVO.getLocation());
                    orderInfoVO.setModelId(deviceSiteVO.getModelId());
                    orderInfoVO.setHeight(deviceSiteVO.getHeight());
                    orderInfoVO.setVideoLatitude(deviceSiteVO.getLatitude());
                    orderInfoVO.setVideoLongitude(deviceSiteVO.getLongitude());
                    orderInfoVO.setDeviceLongitude(deviceSiteVO.getLongitude());
                    orderInfoVO.setDeviceLatitude(deviceSiteVO.getLatitude());
                    orderInfoVO.setDeviceModelCode(deviceSiteVO.getDeviceModelCode());
                }
            }
            //物联设备厂商
            if (WarningSourceEnum.IOT_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource()) || WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
                if (!CollectionUtils.isEmpty(iotModelIdEnum.get())) {
                    iotModelIdEnum.get().forEach(item -> {
                        if (Objects.equals(orderInfoVO.getModelId(), item.getDictValue())) {
                            orderInfoVO.setModelFactoryName(item.getDictLabel());
                        }
                    });
                }
            }
            if (!CollectionUtils.isEmpty(modelIdEnum.get())) {
                modelIdEnum.get().forEach(item -> {
                    if (Objects.equals(orderInfoVO.getModelId(), item.getDictValue())) {
                        orderInfoVO.setModelFactoryName(item.getDictLabel());
                    }
                });
            }
            // 判断设备是收藏
            String deviceCodeCollect = deviceRepository.getDeviceCodeCollectByDeviceCodeAndObjType(orderInfoVO.getDeviceCode(), "5", String.valueOf(dto.getUserId()));
            orderInfoVO.setDeviceCodeCollect(deviceCodeCollect);
        }

        //增加摄像机到事发地点距离
        if (!StringUtils.isEmpty(orderInfoVO.getLongitude()) && !StringUtils.isEmpty(orderInfoVO.getLatitude())
                && !StringUtils.isEmpty(orderInfoVO.getVideoLongitude()) && !StringUtils.isEmpty(orderInfoVO.getVideoLatitude())) {
            //根据设备的经纬度和事发地点的经纬度计算距离位置
            double distance = DistanceUtils.getDistance(Double.parseDouble(orderInfoVO.getLongitude()), Double.parseDouble(orderInfoVO.getLatitude()),
                    Double.parseDouble(orderInfoVO.getVideoLongitude()), Double.parseDouble(orderInfoVO.getVideoLatitude()));
            // 事发地点距离
            orderInfoVO.setIncidentDistance(String.format("%.2f", distance));
        }

        //告警点和设备点水平方位角处理转换
        orderInfoVO.setHoriAzimuthAngle(AngleUtils.transformAngle(orderInfoVO.getHoriAzimuthAngle()));

        //获取登入用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = loginUser.getUser().getTenantId();
        // 获取设备名称别名
        List<OrderInfoVO> resultList = new ArrayList<>();
        resultList.add(orderInfoVO);
        List<Map> deviceNames = deviceRepository.getDeviceRel(resultList, tenantId);
        if (!CollectionUtils.isEmpty(deviceNames)) {
            deviceNames.forEach(item -> {
                if (item.get(Constants.PARAM_DEVICE_CODE).toString().equals(orderInfoVO.getDeviceCode())) {
                    orderInfoVO.setDeviceName(String.valueOf(item.get(Constants.PARAM_DEVICE_NAME)));
                }
            });
        }

        // 通道别名
        List<Map> channelNames = deviceRepository.getChannelRel(resultList, tenantId);
        if (!CollectionUtils.isEmpty(channelNames)) {
            channelNames.forEach(item -> {
                if (item.get(Constants.PARAM_CHANNEL_CODE).toString().equals(orderInfoVO.getChannelCode())) {
                    orderInfoVO.setChannelName(String.valueOf(item.get(Constants.PARAM_CHANNEL_NAME)));
                }
            });
        }

        // 可见光视频处理
        List<Map> videos = new ArrayList<>();
        if (!StringUtils.isEmpty(orderInfoVO.getVideoUrl())
            || !StringUtils.isEmpty(orderInfoVO.getVideoUrl())) {
            String videoUrls = orderInfoVO.getVideoUrl() == null ? orderInfoVO.getVideoUrl() : orderInfoVO.getVideoUrl();
            String[] videoUrlStr = videoUrls.split(Constants.COMMA);
            for (String video : videoUrlStr) {
                imgUrl = new HashMap();
                imgUrl.put("src", video);
                videos.add(imgUrl);
            }
        }
        // 热成像视频处理
        if (!StringUtils.isEmpty(orderInfoVO.getFrontAlarmVedioUrl())) {
            String videoUrls = orderInfoVO.getFrontAlarmVedioUrl();
            String[] videoUrlStr = videoUrls.split(Constants.COMMA);
            for (String video : videoUrlStr) {
                imgUrl = new HashMap();
                imgUrl.put("src", video);
                videos.add(imgUrl);
            }
        }

        this.getALarmLables(orderInfoVO);

        // 按照之前的参数格式进行封装
        Map result = new HashMap();
        //拼装事件描述
        if (WARNING_SOURCE_1.equals(orderInfoVO.getWarningSource())) {
            orderInfoVO.setDescription(warningOrderRepository.buildAlgorithmConfigDescField(orderInfoVO));
        }
        warningOrderUtil.handleDeviceLabel(orderInfoVO);
        warningOrderUtil.getVisiblePtzConfig(orderInfoVO);
        //更改返回值为告警压制实体类
        BeanUtil.copyProperties(orderInfoVO, interceptVO);
        result.put(Constants.PARAM_ORDER, interceptVO);
        result.put(Constants.PARAM_IMGS, imgs);
        result.put(Constants.PARAM_VIDEO, videos);
        if(monitors!=null&&!monitors.isEmpty()){
            result.put(Constants.PARAM_MONITORS, monitors);
        }
        if(reldevice!=null){
            result.put(Constants.PARAM_RELDEVICE, reldevice);
        }
        List<Map> resList = new ArrayList<>();
        resList.add(result);
        return resList;
    }

    private OrderInfoVO getPolygonOperate(OrderInfoVO orderInfoVO) {
        log.info("回收站告警压制详情查询图斑入参:{}",JSON.toJSONString(orderInfoVO));
        if(!StringUtils.isEmpty(orderInfoVO.getTaskId())){
            PolygonOperateDTO polygonOperateDTO = new PolygonOperateDTO();
            polygonOperateDTO.setTaskId(orderInfoVO.getTaskId());
            polygonOperateDTO.setTenantId(orderInfoVO.getTenantId());
            polygonOperateDTO.setDeviceCode(orderInfoVO.getDeviceCode());
            polygonOperateDTO.setChannelCode(orderInfoVO.getChannelCode());
            polygonOperateDTO.setPresetId(orderInfoVO.getPresetId());
            Result result = warningOrderRepository.getPolygonOperate(polygonOperateDTO);
            if(result != null && result.getData()!=null){
                List<Map> polygonOperate = JSONArray.parseArray(JSON.toJSONString(result.getData()),Map.class);
                orderInfoVO.setPolygonOperate(polygonOperate);
            }
        }
        return orderInfoVO;
    }

    private OrderInfoVO doGetAlarmImgs(OrderInfoFuncDto funcDto) {
        if ((Constants.YES_FLAG.equals(funcDto.getOrderInfoVO().getWarningSource()) || Constants.FOUR.equals(funcDto.getOrderInfoVO().getWarningSource())) && (StringUtils.isEmpty(funcDto.getFileImgUrlIcon())
                || StringUtils.isEmpty(funcDto.getVideoUrl()) || StringUtils.isEmpty(funcDto.getFileImgUrlSrc()))) {
            // AI告警调用6.13接口
            try {
                alarmOperateRepository.getAlarmImgs(funcDto.getOrderInfoVO());
            } catch (Exception e) {
                log.error("调用6.13异常: ", e);
            }
        }
        return funcDto.getOrderInfoVO();
    }

    private OrderInfoVO getPresetName(OrderInfoVO orderInfoVO) {
        if (!StringUtils.isEmpty(orderInfoVO.getPresetId())) {
            VideoPresetDO videoPresetDO =videoPresetMapper.selectVideoPreById(orderInfoVO.getPresetId(),orderInfoVO.getDeviceCode());
            if (Objects.nonNull(videoPresetDO)) {
                orderInfoVO.setPresetName(videoPresetDO.getPresetName());
            }
        }
        return orderInfoVO;
    }

    /**
     * 获取告警id的告警标签
     */
    public OrderInfoVO getALarmLables(OrderInfoVO tempOrderInfoVO){
        //告警标签回显
        List<AlarmLableRelDTO> alarmLableRelDTOList = alarmLableRelRepository.getTagsByAlarmType(tempOrderInfoVO.getWarningOrderId());
        List<AlarmLableRelAPIDTO> alarmLableRelAPIDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alarmLableRelDTOList)) {
            alarmLableRelDTOList.forEach(item ->{
                AlarmLableRelAPIDTO alarmLableRelAPIDTO = new AlarmLableRelAPIDTO();
                BeanUtils.copyProperties(item,alarmLableRelAPIDTO);
                alarmLableRelAPIDTOS.add(alarmLableRelAPIDTO);
            });
            tempOrderInfoVO.setAlarmLableRelDTOS(alarmLableRelAPIDTOS);
        }
        return tempOrderInfoVO;
    }

    /**
     * 真警处理
     *
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateIntercept(InterceptDTO dto) {
        try {
            log.info("updateIntercept-req:{}",JSON.toJSONString(dto));
            //获取登入用户信息
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == dto) {
                return Result.fail(Constants.PARAM_ERROR);
            }
            String orderId = dto.getOrderId();
            if (StringUtils.isEmpty(orderId)) {
                return Result.fail(Constants.PARAM_ERROR);
            }

            InterceptVO info = warningInterceptMapper.getInterceptInfo(orderId,null);
            if (null == info) {
                return Result.fail(MessageFormat.format(Constants.ALARM_NO_DATA, orderId));
            }

            if (!StringUtils.isEmpty(info.getJudgeResult())) {
                // 状态不为空，已经处理过，容错
                return Result.fail(Constants.ORDER_JUDGED);
            }

            // 设置为真警
            dto.setJudgeResult(Constants.YES_FLAG);
            // 更新回收站
            int result = warningInterceptMapper.updateIntercept(dto);
            if (result == 0) {
                return Result.fail(Constants.INTERCEPT_FAIL_MESSAGE);
            }
            try{
                interceptMongoMapper.updateIntercept(dto);
            }catch (Exception e){
                log.error("restoreWarningOrder-mongo修改异常:", e);
            }

            // 真警处理
            ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();

            info.setFlowId(dto.getFlowId());
            info.setJudgeResult(dto.getJudgeResult());
            info.setLinkId(dto.getLinkId());
            info.setLinkName(dto.getLinkName());
            info.setTimeLimit(dto.getTimeLimit());
            if (!StringUtils.isEmpty(dto.getWarningTypeId())) {
                info.setWarningTypeId(dto.getWarningTypeId());
            }
            if (!StringUtils.isEmpty(dto.getAddress())) {
                info.setAddress(dto.getAddress());
            }
            if (!StringUtils.isEmpty(dto.getLongitude())) {
                info.setLongitude(dto.getLongitude());
            }
            if (!StringUtils.isEmpty(dto.getLatitude())) {
                info.setLatitude(dto.getLatitude());
            }
            if(Constants.ONE.equals(dto.getCheckGridFlag())){
                info.setGridId(dto.getGridId());
                info.setGridName(dto.getGridName());
            }
            if (!StringUtils.isEmpty(dto.getProvinceCode())) {
                info.setProvinceId(dto.getProvinceCode());
            }
            if (!StringUtils.isEmpty(dto.getProvinceName())) {
                info.setProvinceName(dto.getProvinceName());
            }
            if (!StringUtils.isEmpty(dto.getCityCode())) {
                info.setCityId(dto.getCityCode());
            }
            if (!StringUtils.isEmpty(dto.getCityName())) {
                info.setCityName(dto.getCityName());
            }
            if (!StringUtils.isEmpty(dto.getCountyName())) {
                info.setCountyName(dto.getCountyName());
            }
            if (!StringUtils.isEmpty(dto.getCountyCode())) {
                info.setCountyId(dto.getCountyCode());
            }
            if (!StringUtils.isEmpty(dto.getHoriAzimuthAngle())) {
                info.setHoriAzimuthAngle(dto.getHoriAzimuthAngle());
            }
            if (!StringUtils.isEmpty(dto.getImportanceLevel())) {
                info.setImportanceLevel(dto.getImportanceLevel());
            }


            BeanUtils.copyProperties(info, receiveAlarmVO);
            // 封装参数
            receiveAlarmVO.setTaskId(info.getTaskId());
            receiveAlarmVO.setEnableAcceptanceMode(dto.getEnableAcceptanceMode());
            receiveAlarmVO.setTimeLimitAcceptance(dto.getTimeLimitAcceptance());
            receiveAlarmVO.setFlowAttrId(dto.getAttrId());
            receiveAlarmVO.setOrderStatus(dto.getRealLinkType());
            receiveAlarmVO.setRealLinkType(dto.getRealLinkType());
            receiveAlarmVO.setFlowLinkType(dto.getFlowLinkType());
            receiveAlarmVO.setWarningSource(info.getWarningSource());
            receiveAlarmVO.setWarningOrderId(info.getOrderId());
            receiveAlarmVO.setAlarmType(receiveAlarmVO.getWarningTypeId());
            receiveAlarmVO.setProvinceCode(info.getProvinceId());
            receiveAlarmVO.setCityCode(info.getCityId());
            receiveAlarmVO.setCountyCode(info.getCountyId());
            receiveAlarmVO.setOriginalPictureUrl(info.getFileImgUrlSrc());
            receiveAlarmVO.setAlarmPictureUrl(receiveAlarmVO.getFileImgUrlIcon());
            receiveAlarmVO.setAlarmVideoUrl(receiveAlarmVO.getVideoUrl());
            receiveAlarmVO.setIndustryCode(loginUser.getUser().getIndustryCode());
            receiveAlarmVO.setAppVerCode(loginUser.getUser().getAppVerCode());
            receiveAlarmVO.setFlowTemplate(dto.getTemplateId());
            receiveAlarmVO.setDevicePitch(dto.getDevicePitch());
            receiveAlarmVO.setAlarmReason(dto.getAlarmReason());
            receiveAlarmVO.setPitchAngle(info.getPitchAngle());
            receiveAlarmVO.setHoriFieldAngle(info.getHoriFieldAngle());
            receiveAlarmVO.setVerticalFieldAngle(info.getVerticalFieldAngle());
            if(!StringUtils.isEmpty(dto.getWarningTypeId())){
                receiveAlarmVO.setWarningTypeId(dto.getWarningTypeId());
            }

            String processIds = receiveAlarmVO.getProcessId();
            String processNames = receiveAlarmVO.getProcessName();
            String groupIds = receiveAlarmVO.getGroupId();
            List<ProcessDTO> processDTOS = new ArrayList<>();
            if (!StringUtils.isEmpty(processIds)) {
                if (processIds.contains(Constants.COMMA)) {
                    String[] processIdArray = processIds.split(Constants.COMMA);
                    String[] processNameArray = processNames.split(Constants.COMMA);
                    String[] groupIdArray = groupIds.split(Constants.COMMA);
                    for (int i = 0; i < processIdArray.length; i++) {
                        ProcessDTO processDTO = new ProcessDTO();
                        processDTO.setProcessId(processIdArray[i]);
                        processDTO.setProcessName(processNameArray[i]);
                        processDTO.setGroupId(groupIdArray[i]);
                        processDTOS.add(processDTO);
                    }
                }
                else {
                    ProcessDTO processDTO = new ProcessDTO();
                    processDTO.setProcessId(processIds);
                    processDTO.setProcessName(processNames);
                    processDTO.setGroupId(groupIds);
                    processDTOS.add(processDTO);
                }
            }

            if (!StringUtils.isEmpty(receiveAlarmVO.getRoleId())) {
                receiveAlarmVO.setRoleId(Constants.COMMA + receiveAlarmVO.getRoleId() + Constants.COMMA);
            }


            receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
            receiveAlarmVO.setCreateUser(sysService.getLoginUserId());
            if(StringUtils.isEmpty(receiveAlarmVO.getJudgeType())){
                receiveAlarmVO.setJudgeType(Constants.JUDGE_TYPE_COMMON);
            }
            String processOrderId = null;

            //处理算法透传字段
            if (!ObjectUtils.isEmpty(info.getMsgId()) || !ObjectUtils.isEmpty(info.getNoticeResource())
                    || !ObjectUtils.isEmpty(info.getFrontAlarmPicUrl()) || !ObjectUtils.isEmpty(info.getFrontAlarmVedioUrl())) {
                ExtraInfoVO extraInfoVO = new ExtraInfoVO();
                extraInfoVO.setMsgId(info.getMsgId());
                extraInfoVO.setNoticeResource(Integer.valueOf(info.getNoticeResource()));
                extraInfoVO.setFrontAlarmPicUrl(info.getFrontAlarmPicUrl());
                extraInfoVO.setFrontAlarmVedioUrl(info.getFrontAlarmVedioUrl());
                extraInfoVO.setPresetId(info.getPresetId());
                receiveAlarmVO.setExtraInfo(extraInfoVO);
            }

            // 保存告警信息
            ReceiveAlarmDTO receiveAlarmDTO = new ReceiveAlarmDTO();
            receiveAlarmDTO.setTableName(TableUtil.getTableName(DateUtils.string2Date(receiveAlarmVO.getAlarmTime()), TableConstant.WARN_ORDER_TEBLE));
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            //设置处理人
            this.getProcessInfoNew(receiveAlarmVO);
            alarms.add(receiveAlarmVO);
            receiveAlarmDTO.setList(alarms);
            warningOrderMapper.insertWarningOrders(receiveAlarmDTO);
            //插入受理超期提醒表
            iWarningOrderRepository.saveOverdueRemindData(alarms);
            //异步推送工单告警
            pushThirdService.pushThirdOrder(JSON.toJSONString(receiveAlarmVO),alarms);

            // 保存环节信息
            List<AlarmOrderRecordVO> recordList = new ArrayList<>();
            AlarmOrderRecordVO recordVO = new AlarmOrderRecordVO();
            // 第一个环节AI上报
            recordVO.setOrderFlowId(PKUtils.getFlowOrderId(receiveAlarmVO.getTenantId(), receiveAlarmVO.getWarningOrderId().substring(0, 6)));
            recordVO.setDeleteFlag(Constants.VALID_STATE);
            recordVO.setFlowRecordName("");
            recordVO.setIfTimeout(Constants.VALID_STATE);
            recordVO.setLinkId(Constants.FIRST_LINK);
            recordVO.setRealLinkType(Constants.FIRST_LINK);
            recordVO.setLinkName(EnumUtil.WarningSource.AI.getName());
            recordVO.setOrderId(receiveAlarmVO.getWarningOrderId());
            recordVO.setIfCurrentLink(Constants.VALID_STATE);
            recordVO.setFlowNodeTime(DateUtils.string2Date(receiveAlarmVO.getAlarmTime()));
            recordVO.setFlowEndTime(DateUtils.string2Date(receiveAlarmVO.getAlarmTime()));
            recordList.add(recordVO);

            //todo 有研判环节插入研判数据，没有不插入研判数据???
            // 第二个环节研判
           /* recordVO = new AlarmOrderRecordVO();
            recordVO.setOrderFlowId(PKUtils.getFlowOrderId(receiveAlarmVO.getTenantId(), receiveAlarmVO.getWarningOrderId().substring(0, 6)));
            recordVO.setDeleteFlag(Constants.VALID_STATE);
            recordVO.setFlowRecordName("");
            recordVO.setIfTimeout(Constants.VALID_STATE);
            //初始化的研判环节id不是之前的默认值1，考虑前端透传当前的研判环节id
            recordVO.setLinkId(dto.getLinkId());
            recordVO.setLinkName(EnumUtil.FlowNode.PENDING_JUDGE.getName());
            recordVO.setProcessId(sysService.getLoginUserId());
            recordVO.setNextLinkId(String.valueOf(dto.getLinkId()));
            recordVO.setNextLinkName(dto.getLinkName());
            recordVO.setProcessName(String.valueOf(loginUser.getUser().getNickName()));
            recordVO.setOrderId(receiveAlarmVO.getWarningOrderId());
            recordVO.setIfCurrentLink(Constants.VALID_STATE);
            recordVO.setFlowNodeTime(DateUtils.string2Date(receiveAlarmVO.getFlowNodeTime()));
            recordVO.setFlowEndTime(DateUtils.string2Date(receiveAlarmVO.getFlowNodeTime()));
            recordVO.setRemark(receiveAlarmVO.getRemark());
            recordVO.setRealLinkType(dto.getRealLinkType());
            recordVO.setFlowLinkType(dto.getFlowLinkType());
            recordList.add(recordVO);*/

            // 最后一个环节
            recordVO = new AlarmOrderRecordVO();
            recordVO.setOrderFlowId(PKUtils.getFlowOrderId(receiveAlarmVO.getTenantId(), receiveAlarmVO.getWarningOrderId().substring(0, 6)));
            recordVO.setDeleteFlag(Constants.VALID_STATE);
            recordVO.setIfTimeout(Constants.VALID_STATE);
            recordVO.setLinkId(receiveAlarmVO.getLinkId());
            recordVO.setLinkName(receiveAlarmVO.getLinkName());
            recordVO.setOrderId(receiveAlarmVO.getWarningOrderId());
            recordVO.setIfCurrentLink(Constants.YES_FLAG);
            recordVO.setTimeLimit(receiveAlarmVO.getTimeLimit() != null?Integer.valueOf(receiveAlarmVO.getTimeLimit()):null);
            recordVO.setFlowNodeTime(new Date());
            recordVO.setProcessOrderId(processOrderId);
            recordVO.setRealLinkType(dto.getRealLinkType());
            if (!CollectionUtils.isEmpty(processDTOS)) {
                for (ProcessDTO processDTO : processDTOS) {
                    recordVO.setProcessId(processDTO.getProcessId());
                    recordVO.setProcessName(processDTO.getProcessName());
                    recordVO.setGroupId(processDTO.getGroupId());
                    recordList.add(recordVO);
                }
            }
            else {

                recordList.add(recordVO);
            }
            AlarmOrderRecordDTO alarmOrderRecordDTO = new AlarmOrderRecordDTO();
            alarmOrderRecordDTO.setTableName(TableUtil.getTableName(DateUtils.string2Date(receiveAlarmVO.getAlarmTime()), TableConstant.WARN_ORDER_PROCESS_TEBLE));
            alarmOrderRecordDTO.setList(recordList);
            orderFlowRecordMapper.insertAlarmOrderRecordList(alarmOrderRecordDTO);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(Constants.DEFAULT_ERROR_MESSAGE, e);
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
        return Result.success("");
    }

    private void getProcessInfoNew(ReceiveAlarmVO customer) {
        log.info("getProcessInfoNew-req:{}",JSON.toJSONString(customer));
        //查询流程中是否要网格相关配置标记
        String gridFlag = null;
        //网格员userId
        List<String> gridUserList = customer.getGridUserList();
        if (StringUtils.isEmpty(customer.getGridUserList()) && !StringUtils.isEmpty(customer.getGridId())) {
            String userList = deviceRepository.getGridUserByGridId(customer.getGridId());
            if (!StringUtils.isEmpty(userList)) {
                gridUserList = Arrays.asList(userList.split(Constants.COMMA));
            }
        }
        //真实返回的配置用户
        List<String> resultUserList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(gridUserList)) {
                gridFlag = Constants.ONE;
            }
            //将所有配置人员返回，并将工单便指定人标记为置为1
            AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
            configModel.setId(customer.getFlowId() == null ? null : Long.valueOf(customer.getFlowId()));
            configModel.setLinkId(Long.valueOf(customer.getLinkId()));
            configModel.setGridFlag(gridFlag);
            configModel.setTemplateId(customer.getFlowTemplate() == null ? null : Long.valueOf(customer.getFlowTemplate()));
            configModel.setAppVerCode(customer.getAppVerCode());
            configModel.setIndustryCode(customer.getIndustryCode());
            configModel.setWarningSource(customer.getWarningSource());
            List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
            FlowModel flowLinkAttrModel = flowModelList.get(0);
            String flowUserList = "";
            String flowRoleList = "";
            //用户
            if (!StringUtils.isEmpty(flowLinkAttrModel.getUserId())) {
                flowUserList = flowLinkAttrModel.getUserId();
            }
            //角色
            if (!StringUtils.isEmpty(flowLinkAttrModel.getRoleId())) {
                flowRoleList = flowLinkAttrModel.getRoleId();
            }
            //网格员
            if (flowLinkAttrModel.getUserType().contains(Constants.THREE) && gridFlag != null) {
                if (StringUtils.isEmpty(flowUserList)) {
                    flowUserList = String.join(Constants.COMMA, gridUserList);
                } else {
                    flowUserList += Constants.COMMA + String.join(Constants.COMMA, gridUserList);
                }
            }
            List<String> userIdList = new ArrayList<>();
            List<String> userIdRoleList = new ArrayList<>();
            //流程返回有角色配置时，根据租户和角色查到对应的userId
            if (!StringUtils.isEmpty(flowRoleList)) {
                List<String> roleIdList = Arrays.asList(flowRoleList.split(Constants.COMMA));
                List<Long> roles = new ArrayList<>();
                for (String role:roleIdList) {
                    if(!StringUtils.isEmpty(role)){
                        roles.add(Long.parseLong(role));
                    }
                }
                List<SysUser> userRolerList = deviceRepository.getUserInfoByLongRoleIdsAndIndustryCode(roles, customer.getTenantId(),customer.getIndustryCode());
                userIdRoleList = userRolerList.stream().map(CommonFeignEntity::getUserId).collect(Collectors.toList());
            }
            if (!StringUtils.isEmpty(flowUserList)) {
                userIdList = new ArrayList<>(Arrays.asList(flowUserList.split(Constants.COMMA)));
            }
            userIdList.addAll(userIdRoleList);
            userIdList = userIdList.stream().distinct().collect(Collectors.toList());
            //需要过滤数据权限
            if (Constants.ONE.equals(flowLinkAttrModel.getDataFilterPermiss())) {
                if (!CollectionUtils.isEmpty(userIdList)) {
                    String flag = Constants.ONE;
                    try {
                        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
                        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                            flag = suppressDictData.getData().get(0).getDictValue();
                        }
                        //调用华苏接口过滤网格员数据权限
                        if (Constants.ONE.equals(flag) && !StringUtils.isEmpty(customer.getWarningSource())) {
                            //调用接口获取有摄像机权限的客户
                            Result<List<SysUser>> reponse = iCommonRepository.getUserByDeptAndDeviceScope(userIdList, customer.getTenantId(), customer.getDeviceCode(),
                                    customer.getIndustryCode(), customer.getAppVerCode(), customer.getWarningSource(),
                                    customer.getCityCode(), customer.getCountyCode(), customer.getProvinceCode(), customer.getAlarmType());
                            if (reponse != null && ResultCode.SUCCESS.getCode() == reponse.getCode() && reponse.getData() != null) {
                                JSONArray arrayList = JSON.parseArray(JSON.toJSONString(reponse.getData()));
                                for (Object object : arrayList) {
                                    SysUser user = (JSON.parseObject(JSON.toJSONString(object), SysUser.class));
                                    resultUserList.add(user.getUserId());
                                }
                            }
                        } else {
                            resultUserList = userIdList;
                        }
                    } catch (Exception e) {
                        log.error("getProcessInfoNew根据设备编号获取有权限的用户列表error,返回原有用户,错误信息:" + e);
                        resultUserList = userIdList;
                    }
                }
            } else {
                resultUserList = userIdList;
            }
            String userList = String.join(Constants.COMMA, resultUserList);
            customer.setProcessId(userList);
            customer.setBeforeDispatchFlag(Constants.ONE);
    }

    /**
     * 获取流程模板下一环节
     *
     * @param currentFlow
     * @param flowTemplate
     * @return
     * @since 2021-06-21
     */
    public String getNextFlow(String currentFlow, String flowTemplate) {
        if (StringUtils.isEmpty(flowTemplate) || StringUtils.isEmpty(currentFlow)) {
            return null;
        }
        String[] flows = flowTemplate.split(Constants.COMMA);
        for (int i = 0; i < flows.length; i++) {
            if (currentFlow.equals(flows[i])) {
                return flows[i + 1];
            }
        }
        return null;
    }

    /**
     * 真警处理(还原告警事件)
     *
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result restoreWarningOrder(InterceptDTO dto) {
        try {
            //获取登入用户信息
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == dto) {
                return Result.fail(Constants.PARAM_ERROR);
            }
            String orderId = dto.getOrderId();
            if (StringUtils.isEmpty(orderId)) {
                return Result.fail(Constants.PARAM_ERROR);
            }
            InterceptVO info = interceptService.getInterceptInfo(orderId,null);
            if (null == info) {
                return Result.fail(MessageFormat.format(Constants.ALARM_NO_DATA, orderId));
            }
            //判断告警表是否存在该条告警
            AlarmOrderDTO orderDTO = new AlarmOrderDTO();
            orderDTO.setOrderId(orderId);
            orderDTO.setTableName(TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6));
            OrderInfoVO orderInfo = warningOrderMapper.getOrderInfo(orderDTO);
            //不存在则同步
            if(ObjectUtil.isNull(orderInfo)){
                ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
                // 构建告警事件信息
                this.buildReceiveAlarmVO(info,dto,loginUser,receiveAlarmVO);
                // 创建流程
                List<ReceiveAlarmVO> alarms = new ArrayList<>();
                alarms.add(receiveAlarmVO);
                List<Map> recordList = warningOrderRepository.createRecord(alarms);
                // 回填当前环节相关字段
                warningOrderRepository.setWarningLinkInfo(alarms, recordList);
                // 保存告警信息
                ReceiveAlarmDTO receiveAlarmDTO = new ReceiveAlarmDTO();
                receiveAlarmDTO.setTableName(TableUtil.getTableName(DateUtils.string2Date(receiveAlarmVO.getAlarmTime()), TableConstant.WARN_ORDER_TEBLE));
                receiveAlarmDTO.setList(alarms);
                warningOrderMapper.insertWarningOrders(receiveAlarmDTO);
                //异步推送工单告警
                pushThirdService.pushThirdOrder(JSON.toJSONString(receiveAlarmVO), alarms);
            }else{
                //存在告警，则还原告警数据状态
                WarningOrderDTO warningOrderDTO = new WarningOrderDTO();
                warningOrderDTO.setDeleteFlag(Constants.ZERO_NUM);
                warningOrderDTO.setWarningOrderId(orderId);
                warningOrderDTO.setTableName(TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6));
                warningOrderMapper.updateDeleteFlagById(warningOrderDTO);
                //还原流程记录数据状态
                OrderFlowRecordDTO flowRecordDTO = new OrderFlowRecordDTO();
                flowRecordDTO.setDeleteFlag(Constants.ZERO_NUM);
                flowRecordDTO.setOrderId(orderId);
                flowRecordDTO.setTableName(TableConstant.WARN_ORDER_PROCESS_TEBLE + orderId.substring(0, 6));
                orderFlowRecordMapper.updateDeleteFlagByOrderId(flowRecordDTO);
            }
            // 设置为真警
            dto.setJudgeResult(Constants.YES_FLAG);
            // 更新回收站
            int result = warningInterceptMapper.updateIntercept(dto);
            if (result == 0) {
                return Result.fail(Constants.INTERCEPT_FAIL_MESSAGE);
            }
            try{
                interceptMongoMapper.updateIntercept(dto);
            }catch (Exception e){
                log.error("restoreWarningOrder-mongo修改异常:", e);
            }
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(Constants.DEFAULT_ERROR_MESSAGE, e);
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
        return Result.success("");
    }

    /**
     * 构建告警信息
     * @param info
     * @param dto
     * @param loginUser
     * @param receiveAlarmVO
     */
    private  void buildReceiveAlarmVO(InterceptVO info,InterceptDTO dto,LoginUser loginUser,ReceiveAlarmVO receiveAlarmVO){
//        info.setFlowId(dto.getFlowId());
//        info.setJudgeResult(dto.getJudgeResult());
//        info.setLinkId(dto.getLinkId());
//        info.setLinkName(dto.getLinkName());
//        info.setTimeLimit(dto.getTimeLimit());
//        if (!StringUtils.isEmpty(dto.getWarningTypeId())) {
//            info.setWarningTypeId(dto.getWarningTypeId());
//        }
//        if (!StringUtils.isEmpty(dto.getAddress())) {
//            info.setAddress(dto.getAddress());
//        }
//        if (!StringUtils.isEmpty(dto.getLongitude())) {
//            info.setLongitude(dto.getLongitude());
//        }
//        if (!StringUtils.isEmpty(dto.getLatitude())) {
//            info.setLatitude(dto.getLatitude());
//        }
//        if (!StringUtils.isEmpty(dto.getProvinceCode())) {
//            info.setProvinceCode(dto.getProvinceCode());
//        }
//        if (!StringUtils.isEmpty(dto.getProvinceName())) {
//            info.setProvinceName(dto.getProvinceName());
//        }
//        if (!StringUtils.isEmpty(dto.getCityCode())) {
//            info.setCityCode(dto.getCityCode());
//        }
//        if (!StringUtils.isEmpty(dto.getCityName())) {
//            info.setCityName(dto.getCityName());
//        }
//        if (!StringUtils.isEmpty(dto.getProvinceCode())) {
//            info.setProvinceCode(dto.getProvinceCode());
//        }
//        if (!StringUtils.isEmpty(dto.getCountyCode())) {
//            info.setCountyName(dto.getCountyCode());
//        }
//        if (!StringUtils.isEmpty(dto.getHoriAzimuthAngle())) {
//            info.setHoriAzimuthAngle(dto.getHoriAzimuthAngle());
//        }

        BeanUtils.copyProperties(info, receiveAlarmVO);
        // 封装参数
        receiveAlarmVO.setOrderStatus(receiveAlarmVO.getLinkId());
        receiveAlarmVO.setTransparentFields(info.getTransparentFields());
        receiveAlarmVO.setWarningSource(info.getWarningSource());
        info.setOrderId(info.getWarningOrderId());
        receiveAlarmVO.setWarningOrderId(info.getOrderId());
        if(!StringUtils.isEmpty(dto.getWarningTypeId())){
            receiveAlarmVO.setAlarmType(dto.getWarningTypeId());
        }else{
            receiveAlarmVO.setAlarmType(receiveAlarmVO.getWarningTypeId());
        }
        receiveAlarmVO.setAlarmReason(dto.getAlarmReason());
        if(!StringUtils.isEmpty(dto.getDeviceHorizontal())){
            receiveAlarmVO.setDeviceHorizontal(dto.getDeviceHorizontal());
        }
        if(!StringUtils.isEmpty(dto.getDevicePitch())){
            receiveAlarmVO.setDevicePitch(dto.getDevicePitch());
        }
        if(StringUtils.isEmpty(receiveAlarmVO.getProvinceCode())){
            receiveAlarmVO.setProvinceCode(info.getProvinceId());
        }
        if(StringUtils.isEmpty(receiveAlarmVO.getCityId())){
            receiveAlarmVO.setCityCode(info.getCityId());
        }
        if(StringUtils.isEmpty(receiveAlarmVO.getCountyId())){
            receiveAlarmVO.setCountyCode(info.getCountyId());
        }
        receiveAlarmVO.setOriginalPictureUrl(info.getFileImgUrlSrc());
        receiveAlarmVO.setAlarmPictureUrl(receiveAlarmVO.getFileImgUrlIcon());
        receiveAlarmVO.setAlarmVideoUrl(receiveAlarmVO.getVideoUrl());
        receiveAlarmVO.setIndustryCode(loginUser.getUser().getIndustryCode());
        receiveAlarmVO.setAppVerCode(loginUser.getUser().getAppVerCode());
        if(!StringUtils.isEmpty(dto.getImportanceLevel())){
            receiveAlarmVO.setImportanceLevel(dto.getImportanceLevel());
        }
        if(!StringUtils.isEmpty(dto.getAddress())){
            receiveAlarmVO.setAddress(dto.getAddress());
        }
        if(!StringUtils.isEmpty(dto.getLatitude())){
            receiveAlarmVO.setLatitude(dto.getLatitude());
        }
        if(!StringUtils.isEmpty(dto.getLongitude())){
            receiveAlarmVO.setLongitude(dto.getLongitude());
        }
        if(Constants.ONE.equals(dto.getCheckGridFlag())){
            receiveAlarmVO.setGridId(dto.getGridId());
            receiveAlarmVO.setGridName(dto.getGridName());
        }
        if (!StringUtils.isEmpty(receiveAlarmVO.getRoleId())) {
            receiveAlarmVO.setRoleId(Constants.COMMA + receiveAlarmVO.getRoleId() + Constants.COMMA);
        }
        if(!StringUtils.isEmpty(dto.getHoriAzimuthAngle())){
            receiveAlarmVO.setHoriAzimuthAngle(dto.getHoriAzimuthAngle());
        }

        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        receiveAlarmVO.setCreateUser(sysService.getLoginUserId());

        if(StringUtils.isEmpty(receiveAlarmVO.getJudgeType())){
            receiveAlarmVO.setJudgeType(Constants.JUDGE_TYPE_COMMON);
        }
        //处理算法透传字段
        if (!ObjectUtils.isEmpty(info.getMsgId()) || !ObjectUtils.isEmpty(info.getNoticeResource())
                || !ObjectUtils.isEmpty(info.getFrontAlarmPicUrl()) || !ObjectUtils.isEmpty(info.getFrontAlarmVedioUrl())) {
            ExtraInfoVO extraInfoVO = new ExtraInfoVO();
            extraInfoVO.setMsgId(info.getMsgId());
            extraInfoVO.setNoticeResource(Integer.valueOf(info.getNoticeResource()));
            extraInfoVO.setFrontAlarmPicUrl(info.getFrontAlarmPicUrl());
            extraInfoVO.setFrontAlarmVedioUrl(info.getFrontAlarmVedioUrl());
            extraInfoVO.setPresetId(info.getPresetId());
            receiveAlarmVO.setExtraInfo(extraInfoVO);
        }
    }
}
