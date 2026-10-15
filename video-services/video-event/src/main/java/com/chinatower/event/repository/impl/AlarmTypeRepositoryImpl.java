package com.chinatower.event.repository.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.GsonUtils;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.ScreenDataConfigParticipationDTO;
import com.chinatower.event.entity.dto.AlarmTypeTagInfoDTO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
import com.chinatower.event.mapper.AlarmTypeLevelMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.vo.AlarmLevelVO;
import com.chinatower.industry.api.IndustryAlgorithmService;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.ModelConfigVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeDTO;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.event.mapper.AlarmTypeMapper;
import com.chinatower.event.vo.AlarmTypeClassVO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.vo.SysAlarmTypeIndustryRelVo;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import io.seata.common.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 告警类型表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
@Service
@Slf4j
@DS(EbikeDataBaseConstant.BASE)
public class AlarmTypeRepositoryImpl extends ServiceImpl<AlarmTypeMapper, AlarmTypeDO> implements IAlarmTypeRepository {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private AlarmTypeMapper alarmTypeMapper;

    @Autowired
    private AlarmTypeLevelMapper alarmTypeLevelMapper;

    @Autowired
    private ISysAlarmTagsTypeRelRepository sysAlarmTagsTypeRelRepository;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private ISysService sysService;

    @Autowired
    private VmAlarmFlowConfigService vmAlarmFlowConfigService;

    @Autowired
    private IndustryAlgorithmService industryAlgorithmService;

    @Autowired
    private SystemFeignClient systemFeignClient;


    /**
     * @param body
     * @Description 查询订购类型
     * @author
     * @Date 2022-01-26
     * @Param [body]
     */
    @Override
    public List<AlarmTypeDO> findOrderAlarm(AlarmTypeDTO body) {
        List<AlarmTypeDO> alarmTypeList = Lists.newArrayList();
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(body.getTenantId())) {
            body.setTenantId(loginUser.getUser().getTenantId());
        }
        body.setAppVerCode(loginUser.getUser().getAppVerCode());
        //  查询客户的告警来源
        List<String> alarmSourceList = findAlarmSource();
        body.setAlarmSources(alarmSourceList);
        if (StrUtil.isEmpty(body.getIndustryCode())) {
            body.setIndustryCode(loginUser.getUser().getIndustryCode());
        }

        // 如果需要过滤产生过告警事件的告警类型
        if (BooleanUtils.isTrue(body.getFilterAlarmEvent())) {
            if (body.getAlarmTimeStart() == null || body.getAlarmTimeEnd() == null) {
                throw new CustomException("告警时间范围不能为空");
            }
            AlarmOrderDTO alarmParam = new AlarmOrderDTO();
            alarmParam.setTenantId(body.getTenantId());
            alarmParam.setIndustryCode(body.getIndustryCode());
            alarmParam.setAlarmTimeStart(body.getAlarmTimeStart());
            alarmParam.setAlarmTimeEnd(body.getAlarmTimeEnd());
            List<String> typeValueList = warningOrderRepository.findAlarmTypesByTime(alarmParam);
            log.info("AlarmTypeRepositoryImpl findAlarmSourceSubType alarmParam:{}, typeValueList:{}",
                    JSON.toJSONString(alarmParam), JSON.toJSONString(typeValueList));
            if (CollectionUtils.isEmpty(typeValueList)) {
                return alarmTypeList;
            }
            body.setTypeValueList(typeValueList);
        }
        alarmTypeList = alarmTypeMapper.findOrderAlarm(body);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(alarmTypeList)) {
            // 卫星告警只查询【卫星火点】告警
            if (alarmSourceList.contains(WarningSourceEnum.SATELLITE_ALARM.getTypeValue())
                    && BooleanUtils.isTrue(body.getOnlySatelliteFirePointFlag())) {
                // 过滤所有非卫星告警来源 以及 卫星告警来源中卫星火点类型
                alarmTypeList = alarmTypeList.stream()
                        .filter(x -> !Objects.equals(x.getAlarmSource(), WarningSourceEnum.SATELLITE_ALARM.getTypeValue())
                                || Objects.equals(x.getTypeValue(), Constants.SATELLITE_FIRE_POINT_TYPE_VALUE)).collect(Collectors.toList());
            }
        }

        // 如果含算法模型配置，则查询算法模型配置信息加入返回结果
        if (BooleanUtils.isTrue(body.getWithAlgorithmModelConfig())) {
            AlarmSourceSubTypeDTO param = new AlarmSourceSubTypeDTO();
            param.setTenantId(body.getTenantId());
            param.setIndustryCode(body.getIndustryCode());
            param.setAppVerCode(body.getAppVerCode());
            // 大模型算法列表
            Map<String, String> algorithmCodeMap = getModelAlgorithmCodeMap();
            param.setModelAlgorithmCodeList(Lists.newArrayList(algorithmCodeMap.keySet()));
            List<AlarmTypeVO> modelConfigAlarmTypeList = queryCustomModelAlarmTypeInfo(param);
            log.info("AlarmTypeRepositoryImpl findOrderAlarm param：{} modelConfigAlarmTypeList:{}", GsonUtils.toJSONString(param), GsonUtils.toJSONString(modelConfigAlarmTypeList));
            if (CollectionUtils.isNotEmpty(modelConfigAlarmTypeList)) {
                // 将查询到的结果填充到AI告警类型中
                alarmTypeList.addAll(modelConfigAlarmTypeList.stream().map(x -> {
                    AlarmTypeDO data = new AlarmTypeDO();
                    data.setTypeValue(x.getTypeValue());
                    data.setTypeName(x.getTypeName());
                    data.setAlarmSource("1");
                    return data;
                }).collect(Collectors.toList()));
            }
        }
        return alarmTypeList;
    }

    @Override
    public Result<PageInfo<AlarmTypeDO>> findOrderAlarmBySource(AlarmTypeDTO body) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(body.getTenantId())) {
            body.setTenantId(loginUser.getUser().getTenantId());
        }
        body.setAppVerCode(loginUser.getUser().getAppVerCode());
        if (StrUtil.isEmpty(body.getIndustryCode())) {
            body.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        // 如果需要查询全部数据，则不分页
        if (BooleanUtils.isNotTrue(body.getSearchAllFlag())) {
            PageMethod.startPage(body.getPageNum(), body.getPageSize());
        }
        List<AlarmTypeDO> resultList = alarmTypeMapper.findOrderAlarmBySource(body);
        PageInfo<AlarmTypeDO> pageInfo = new PageInfo<>(resultList);
        Result<PageInfo<AlarmTypeDO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    /**
     * @param body
     * @Description 查询所有订购类型
     * @Param [body]
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AlarmTypeDO> findOrderAlarmAll(AlarmTypeDTO body) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(body.getTenantId())) {
            body.setTenantId(loginUser.getUser().getTenantId());
        }
        if(StringUtils.isEmpty(body.getAppVerCode())){
            body.setAppVerCode(loginUser.getUser().getAppVerCode());
        }
        if(StringUtils.isEmpty(body.getIndustryCode())){
            body.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        //  查询客户的告警来源
        body.setAlarmSources(findAlarmSource());
        return alarmTypeMapper.findOrderAlarmAll(body);
    }

    /**
     * 告警类型列表查询
     *
     * @param dto
     * @return
     */
    @EventObsEncrypt
    @Override
    public List<AlarmTypeClassVO> findList(AlarmTypeClassDTO dto) {


        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        dto.setTenantId(tenantId);
        dto.setAppVerCode(loginUser.getUser().getAppVerCode());
        dto.setIndustryCode(industryCode);
        //查询条件租户ID优先级高于顶层租户过滤条件
        if (StringUtils.isNotEmpty(dto.getTenantor())) {
            dto.setTenantId(dto.getTenantor());
        }
        //  查询客户的告警来源
        dto.setAlarmSources(findAlarmSource());

        Map<String, String> algorithmCodeMap = getModelAlgorithmCodeMap();
        List<String> modelAlgorithmCodeList = Lists.newArrayList(algorithmCodeMap.keySet());
        dto.setModelAlgorithmCodeList(modelAlgorithmCodeList);
        log.info("AlarmTypeRepositoryImpl findList modelAlgorithmCodeList:{}", GsonUtils.toJSONString(modelAlgorithmCodeList));
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());

        //查询租户订购的告警类型
        List<AlarmTypeClassVO> list = alarmTypeMapper.findOrderAlarmList(dto);
        List<String> iconUrlList = list.stream().filter(alarmTypeClassVO -> StringUtils.isNotEmpty(alarmTypeClassVO.getIcon())
                        && (StringUtils.indexOf(alarmTypeClassVO.getIcon(), "http") > -1 || alarmTypeClassVO.getIcon().startsWith("/")))
                .map(AlarmTypeClassVO::getIcon).collect(Collectors.toList());
        Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(iconUrlList);
        list.stream().forEach(alarmTypeClassVO -> {
            if (StrUtil.isEmpty(alarmTypeClassVO.getIsVisible())) {
                alarmTypeClassVO.setIsVisible("1000");
            }
            if (StringUtils.isNotEmpty(alarmTypeClassVO.getIcon())
                    && (StringUtils.indexOf(alarmTypeClassVO.getIcon(), "http") > -1 || alarmTypeClassVO.getIcon().startsWith("/"))) {
                alarmTypeClassVO.setIcon(stringStringMap.get(alarmTypeClassVO.getIcon()));
            }
        });
        //拼装告警类型标签数据
        Set<String> areaCodes= Sets.newHashSet();
        list.stream().forEach(x ->{
            areaCodes.add(x.getCityId());
            areaCodes.add(x.getProvinceId());
        });
        Map<String, String> areaRedisMap = null;
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(areaCodes)){
            areaRedisMap = deviceRepository.getAreaNameCaffeineMap(new ArrayList<>(areaCodes));
        }

        List<String> typeValueList = list.stream().map(AlarmTypeClassVO::getTypeValue).collect(Collectors.toList());
        Map<String, List<AlarmTagsTypeRelDTO>> typeValueTagListMap = null;
        if (CollectionUtils.isNotEmpty(typeValueList)) {
            List<AlarmTagsTypeRelDTO> allTagList = sysAlarmTagsTypeRelRepository.getTagsByAlarmType(null, typeValueList, dto.getTenantId(), dto.getIndustryCode());
            if (!CollectionUtils.isEmpty(allTagList)){
                List<AlarmTagsTypeRelDTO> tagInfoList = allTagList.stream().filter(item -> item.getIsRel().equals(Constants.ONE)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(tagInfoList)){
                    typeValueTagListMap = tagInfoList.stream().collect(Collectors.groupingBy(AlarmTagsTypeRelDTO::getTypeValue));
                }
            }
        }
        for (AlarmTypeClassVO atc : list) {
            StringBuilder sb = new StringBuilder();
//            List<AlarmTagsTypeRelDTO> tagList = sysAlarmTagsTypeRelRepository.getTagsByAlarmType(atc.getTypeValue(), dto.getTenantId(), dto.getIndustryCode());
            if (MapUtils.isNotEmpty(typeValueTagListMap)){
                List<AlarmTagsTypeRelDTO> tagList = typeValueTagListMap.get(atc.getTypeValue());
                if (CollectionUtils.isNotEmpty(tagList)) {
                    for (AlarmTagsTypeRelDTO alarmTagsTypeRelDTO : tagList) {
                        //前面已经过滤了isRel为0的数据
                        sb.append(alarmTagsTypeRelDTO.getTagName()).append(",");
                    }

                    String tagNames = sb.toString();
                    if (!StringUtils.isEmpty(tagNames)) {
                        atc.setTagNames(tagNames.substring(0, tagNames.length() - 1));
                    }
                }
            }
            if (MapUtils.isNotEmpty(areaRedisMap)) {
                atc.setProvinceName(MapUtils.getString(areaRedisMap, atc.getProvinceId(), ""));
                atc.setCityName(MapUtils.getString(areaRedisMap, atc.getCityId(), ""));
            }
        }


        return list;
    }

    /**
     * 获取所有租户的压制配置
     *
     * @return
     * @author chenmf
     * @date 2022/3/19
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AlgorithmEventVO> getAlgorithm(List<String> tenantIdList, String alarmType) {
        return alarmTypeMapper.getAlgorithm(tenantIdList, alarmType);
    }

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, typeValueColumn = "sat.type_value")
    public List<AlarmTypeTagInfoVO> selectRelateTagInfoByAlarmType(AlarmTypeTagInfoDTO param) {
        if (param == null || CollectionUtils.isEmpty(param.getTypeValueList())) {
            throw new CustomException(MessageConstants.ALARM_NO_PARAMS);
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || loginUser.getUser() == null) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        return sysAlarmTagsTypeRelRepository.selectRelateTagInfoByAlarmType(tenantId, industryCode, param.getTypeValueList(), param.getDataScope());
    }

    @Override
    public List<AlarmTypeTagInfoVO> selectTagInfoByTenant() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || loginUser.getUser() == null) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        return sysAlarmTagsTypeRelRepository.selectTagInfoByTenant(tenantId, industryCode);
    }

    /**
     * 查询客户的告警来源及告警类型
     *
     * @param body
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, typeValueColumn = "sat.type_value")
    public List<AlarmSourceSubTypeVO> findAlarmSourceSubType(AlarmSourceSubTypeDTO body) {
        List<String> alarmSources = findAlarmSource(body);
        //4、算法告警及非算法告警
        body.setAlarmSources(alarmSources);
        List<AlarmTypeVO> alarmTypeVOList = alarmTypeMapper.findAlarmTypeByOrder(body);
        List<AlarmTypeVO> sourceAlarmTypeList = alarmTypeMapper.findAlarmTypeBySource(body);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(sourceAlarmTypeList)) {
            // 卫星告警只查询【卫星火点】告警
            if (alarmSources.contains(WarningSourceEnum.SATELLITE_ALARM.getTypeValue())
                    && BooleanUtils.isTrue(body.getOnlySatelliteFirePointFlag())) {
                // 过滤所有非卫星告警来源 以及 卫星告警来源中卫星火点类型
                sourceAlarmTypeList = sourceAlarmTypeList.stream()
                        .filter(x -> !Objects.equals(x.getAlarmSource(), WarningSourceEnum.SATELLITE_ALARM.getTypeValue())
                                || Objects.equals(x.getTypeValue(), Constants.SATELLITE_FIRE_POINT_TYPE_VALUE)).collect(Collectors.toList());
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(sourceAlarmTypeList)) {
                alarmTypeVOList.addAll(sourceAlarmTypeList);
            }
        }
        Map<String, List<AlarmTypeVO>> groupMap = alarmTypeVOList.stream().collect(Collectors.groupingBy(vo -> vo.getAlarmSource()));
        List<AlarmSourceSubTypeVO> alarmSourceSubTypeVOList = new ArrayList<>(groupMap.size());
        groupMap.forEach((k, v) -> {
            AlarmSourceSubTypeVO subTypeVO = new AlarmSourceSubTypeVO();
            subTypeVO.setAlarmSource(v.get(0).getAlarmSource());
            subTypeVO.setAlarmSourceName(v.get(0).getAlarmSourceName());
            subTypeVO.setAlarmTypeVOList(v);
            alarmSourceSubTypeVOList.add(subTypeVO);
        });

        // 如果包含第三方告警类型，则查询第三方告警类型信息并加入返回结果
        if (BooleanUtils.isTrue(body.getWithThirdAlarmType())) {
            List<AlarmSourceSubTypeVO> thirdPartyAlarmTypeList = vmAlarmFlowConfigService.getThirdPartyAlarmTypes(body);
            log.info("AlarmTypeRepositoryImpl findAlarmSourceSubType getThirdPartyAlarmTypes param:{}, result:{}",
                    JSON.toJSONString(body), JSON.toJSONString(thirdPartyAlarmTypeList));
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(thirdPartyAlarmTypeList)) {
                alarmSourceSubTypeVOList.addAll(thirdPartyAlarmTypeList);
            }
        }

        // 如果含算法模型配置，则查询算法模型配置信息加入返回结果
        if (BooleanUtils.isTrue(body.getWithAlgorithmModelConfig())) {
            // 大模型算法列表
            Map<String, String> algorithmCodeMap = getModelAlgorithmCodeMap();
            List<String> modelAlgorithmCodeList = Lists.newArrayList(algorithmCodeMap.keySet());
            body.setModelAlgorithmCodeList(modelAlgorithmCodeList);
            List<AlarmTypeVO> modelConfigAlarmTypeList = queryCustomModelAlarmTypeInfo(body);
            log.info("AlarmTypeRepositoryImpl tenantId:{}, industryCode:{} modelAlgorithmCodeList:{} modelConfigAlarmTypeList size:{}",
                    body.getTenantId(), body.getIndustryCode(), GsonUtils.toJSONString(modelAlgorithmCodeList), modelConfigAlarmTypeList.size());
            if (CollectionUtils.isNotEmpty(modelConfigAlarmTypeList)) {
                modelConfigAlarmTypeList.forEach(x -> {
                    x.setModelTypeName(algorithmCodeMap.get(x.getAlgorithmCode()));
                });
                // 将查询到的结果填充到AI告警类型中
                for (AlarmSourceSubTypeVO alarmSourceVO : alarmSourceSubTypeVOList) {
                    // 处理AI告警
                    if (StringUtils.equals(alarmSourceVO.getAlarmSource(), "1")) {
                        List<AlarmTypeVO> aiAlarmTypeList = alarmSourceVO.getAlarmTypeVOList();
                        if (aiAlarmTypeList != null) {

                            // 设置为大模型父模型类型
                            for (AlarmTypeVO alarmTypeVO : aiAlarmTypeList) {
                                if (modelAlgorithmCodeList.contains(alarmTypeVO.getAlgorithmCode())) {
                                    alarmTypeVO.setModelAlgorithmType(1);
                                }
                            }

                            modelConfigAlarmTypeList.forEach(x -> {
                                x.setAlarmSource(alarmSourceVO.getAlarmSource());
                                x.setAlarmSourceName(alarmSourceVO.getAlarmSourceName());
                            });
                            aiAlarmTypeList.addAll(modelConfigAlarmTypeList);
                        }
                        break;
                    }
                }
            }
        }
        return alarmSourceSubTypeVOList;
    }

    private List<AlarmTypeVO> queryCustomModelAlarmTypeInfo(AlarmSourceSubTypeDTO body) {
        return alarmTypeMapper.queryCustomModelAlarmTypeInfo(body.getTenantId(), body.getIndustryCode(), body.getModelAlgorithmCodeList());
    }

    private Map<String, String> getModelAlgorithmCodeMap() {
        Map<String, String> algorithmCodeMap = Maps.newHashMap();
        List<AlgorithmInfoDO> modelAlgorithmInfoList = alarmTypeMapper.selectModelAlgorithmInfoList();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(modelAlgorithmInfoList)) {
            algorithmCodeMap = modelAlgorithmInfoList.stream().collect(Collectors.toMap(AlgorithmInfoDO::getAlgorithmCode, AlgorithmInfoDO::getAlgorithmName, (a, b) -> a));;
        }
        return algorithmCodeMap;
    }

    @Override
    public List<AlgorithmInfoDO> queryModelAlgorithmInfoList() {
        return alarmTypeMapper.selectModelAlgorithmInfoList();
    }

    /**
     * 客户下游任务下拉。
     * 曾经订购过大模型（当前订购表 ∪ 历史订购表，含退订/到期）即展示平台 model_config 下游目录；
     * 不依赖租户场景配置（退订清场景后仍展示）。
     * 平台无大模型算法编码时返回空列表（前端可隐藏下拉）。
     */
    @Override
    public List<AlarmTypeDO> findOrderSubSceneAlarm(AlarmTypeDTO body) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(DeviceNorthIntfConstants.GET_USER_INFO_ERROR);
        }
        if (StringUtils.isEmpty(body.getTenantId())) {
            body.setTenantId(loginUser.getUser().getTenantId());
        }
        if (StrUtil.isEmpty(body.getIndustryCode())) {
            body.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        // 无大模型算法编码则直接返回空（前端隐藏下拉）
        Map<String, String> algorithmCodeMap = getModelAlgorithmCodeMap();
        if (algorithmCodeMap.isEmpty()) {
            return Lists.newArrayList();
        }
        body.setAlgorithmCodes(Lists.newArrayList(algorithmCodeMap.keySet()));
        return alarmTypeMapper.findOrderSubSceneAlarm(body);
    }

    @Override
    public List<AlarmTypeVO> findAlarmTypeByTenant(AlarmSourceSubTypeDTO body) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 获取当前登录人
        if (loginUser == null) {
            throw new CustomException(DeviceNorthIntfConstants.GET_USER_INFO_ERROR);
        }
        body.setTenantId(loginUser.getUser().getTenantId());
        body.setIndustryCode(loginUser.getUser().getIndustryCode());
        List<String> alarmSources = findAlarmSourceBySource(body);
        //4、算法告警及非算法告警
        body.setAlarmSources(alarmSources);
        List<AlarmTypeVO> alarmTypeVOList = alarmTypeMapper.findAlarmTypeByOrder(body);
        if (CollectionUtils.isNotEmpty(alarmSources)){
            alarmTypeVOList.addAll(alarmTypeMapper.findAlarmTypeBySource(body));
        }

        return alarmTypeVOList;
    }

    /**
     * 查询客户的告警来源
     *
     * @param body
     * @return
     */
    @Override
    public List<String> findAlarmSource(AlarmSourceSubTypeDTO body) {
        List<String> alarmSources = new ArrayList<>();
        //人工上报和一件告警为固定告警来源
        alarmSources.add(WarningSourceEnum.ARTIFICIAL.getTypeValue());
        alarmSources.add(WarningSourceEnum.UAV_ALARM.getTypeValue());
        //1、是否有摄像机告警来源
        if (alarmTypeMapper.devByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.CAMERA_ALARM.getTypeValue());
        }
        // 是否有无人机告警来源
        if (alarmTypeMapper.uavByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue());
        }
        //2、是否有物联设备、雷达告警来源
        body.setCategoryCode(String.valueOf(OrderConstants.ORDER_SENSOR_CODE));
        if (alarmTypeMapper.iotDevByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.IOT_ALARM.getTypeValue());
        }
        body.setCategoryCode(String.valueOf(OrderConstants.ORDER_RADAR_CODE));
        if (alarmTypeMapper.iotDevByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.RADAR_ALARM.getTypeValue());
        }

        //3、是否有卫星告警来源 跟志伟和学斌总确认，卫星告警类型查询不卡订购 0516
//        if (alarmTypeMapper.serviceByTenantIdCount(body) > 0) {
        alarmSources.add(WarningSourceEnum.SATELLITE_ALARM.getTypeValue());
//        }
        return alarmSources;
    }

    /**
     * 查询客户的ai，卫星，摄像机
     *
     * @param body
     * @return
     */
    public List<String> findAlarmSourceBySource(AlarmSourceSubTypeDTO body) {
        List<String> alarmSources = new ArrayList<>();
        //1、是否有摄像机告警来源
        Integer count = alarmTypeMapper.devByTenantIdCount(body);
        if (alarmTypeMapper.devByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.CAMERA_ALARM.getTypeValue());
        }
        //3、是否有卫星告警来源
        if (alarmTypeMapper.serviceByTenantIdCount(body) > 0) {
            alarmSources.add(WarningSourceEnum.SATELLITE_ALARM.getTypeValue());
        }
        return alarmSources;
    }

    @Override
    public List<AlarmTypeDO> findAlgorithmAlarm(AlarmTypeDTO body) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(body.getTenantId())) {
            body.setTenantId(loginUser.getUser().getTenantId());
        }
        body.setAppVerCode(loginUser.getUser().getAppVerCode());
        return alarmTypeMapper.findAlgorithmAlarm(body);
    }


    /**
     * @Description 查询客户的告警来源
     */
    private List<String> findAlarmSource() {
        AlarmSourceSubTypeDTO body = new AlarmSourceSubTypeDTO();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        body.setTenantId(loginUser.getUser().getTenantId());
        body.setUserId(sysService.getLoginUserId());
        body.setIndustryCode(loginUser.getUser().getIndustryCode());
        body.setAppVerCode(loginUser.getUser().getAppVerCode());
        return findAlarmSource(body);
    }

    @Override
    public List<Map> getAlarmIconList() {
        List<Map> alarmIconList = alarmTypeMapper.getAlarmIconList();
        return alarmIconList;
    }

    @Override
    public Result modifyIsVisible(AlarmTypeClassDTO body) {
        int count = alarmTypeMapper.checkSysAlarmTypeIndustryRelVoCount(body);
        SysAlarmTypeIndustryRelVo relVo = new SysAlarmTypeIndustryRelVo();
        relVo.setAlarmTypeId(body.getAlarmTypeId());
        relVo.setIndustryCode(body.getIndustryCode());
        relVo.setIsVisible(body.getIsVisible());
        relVo.setTenantId(body.getTenantId());
        relVo.setAppVerCode(body.getAppVerCode());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        relVo.setModifier(loginUser.getUser().getUserId());
        relVo.setTypeValue(body.getTypeValue());
        int i = 0;
        if (count > 0) {
            relVo.setCreator(loginUser.getUser().getUserId());
            relVo.setCreateTime(new Date());
            i = alarmTypeMapper.modifyIsVisible(relVo);
        } else {
            relVo.setUpdateTime(new Date());
            i = alarmTypeMapper.createIsVisible(relVo);
        }


        if (i == 0) {
            return Result.fail("操作失败！");
        }
        return Result.success("操作成功！");
    }


    @Override
    public Result<String> addAlarmType(List<AlarmTypeDO> alarmTypeList) {
        if (CollectionUtils.isEmpty(alarmTypeList)) {
            return Result.fail("告警类型不能为空！");
        }
        List<AlarmTypeDO> collect = alarmTypeList.stream().filter(alarmType -> alarmType.getTypeName().length() > 10).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(collect)){
            return Result.fail("告警类型名称不能超过十个字！");
        }
        List<AlarmTypeDO> iconList = alarmTypeList.stream().filter(alarmType -> StringUtils.isEmpty(alarmType.getIcon())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(iconList)){
            return Result.fail("请上传告警图标");
        }
        //判断改客户行业下是否已经存在同名的告警类型、判断是否与该客户订购的告警类型名称重复
        List<String> existTypeNameList = checkedName(alarmTypeList);
        String existTypeName = StringUtils.joinWith(",", existTypeNameList);
        if (!CollectionUtils.isEmpty(existTypeNameList)){
            return Result.fail(existTypeName + "已存在！");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        alarmTypeList.forEach(alarmTypeDO -> alarmTypeDO.setCreator(loginUser.getUsername()));
        int i = alarmTypeMapper.addAlarmType(alarmTypeList);
        if (i < 0){
            return Result.fail("新增告警类型失败！");
        }
        return Result.success("新增告警类型成功！");
    }

    @Override
    public Result<Integer> checkAlarmType(AlarmTypeDO param) {
        if (param == null || StringUtils.isEmpty(param.getTypeName())) {
            throw new CustomException("告警类型名称不能为空！");
        }
        List<AlarmTypeDO> alarmTypeList = Lists.newArrayList(param);
        List<String> existTypeNameList = checkedName(alarmTypeList);
        return Result.data(CollectionUtils.isNotEmpty(existTypeNameList) ? 1 : 0);
    }

    private List<String> checkedName(List<AlarmTypeDO> alarmTypeList){
        List<String> typeNameList = alarmTypeList.stream().map(AlarmTypeDO::getTypeName).collect(Collectors.toList());
        AlarmTypeClassDTO alarmTypeClassDTO = new AlarmTypeClassDTO();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || loginUser.getUser() == null) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysUser user = loginUser.getUser();
        alarmTypeClassDTO.setTenantId(user.getTenantId());
        alarmTypeClassDTO.setIndustryCode(user.getIndustryCode());
        alarmTypeClassDTO.setAppVerCode(user.getAppVerCode());
        // 查询客户的告警来源
        alarmTypeClassDTO.setAlarmSources(findAlarmSource());
        //查询租户订购的告警类型和人工上报的告警类型
        List<AlarmTypeClassVO> list = alarmTypeMapper.findOrderAlarmList(alarmTypeClassDTO);
        List<String> exitTypeNameList = list.stream().map(AlarmTypeClassVO::getAlarmTypeName).collect(Collectors.toList());
        return exitTypeNameList.stream().filter(typeNameList::contains).collect(Collectors.toList());
    }

    @Override
    public void delAlarmTypeBatch(List<Long> list) {
        alarmTypeMapper.delAlarmTypeBatch(list);
    }

    @Override
    public AlarmLevelVO getAlarmLevelByTypeValue(AlarmTypeLevelDO body) {
        if (body == null || StringUtils.isEmpty(body.getTypeValue())) {
            return new AlarmLevelVO();
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        body.setTenantId(user.getTenantId());
        body.setIndustryCode(user.getIndustryCode());
        return alarmTypeLevelMapper.getAlarmLevelByTypeValue(body);
    }

    @Override
    public Result<String> updateAlarmLevelByTypeValue(AlarmTypeLevelDO body) {
        if (body == null || StringUtils.isEmpty(body.getTypeValue())) {
            return Result.fail("告警类型编码不能为空");
        }
        if (StringUtils.isEmpty(body.getAlarmLevel())) {
            return Result.fail("告警等级不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        body.setModifier(user.getUserName());
        body.setTenantId(user.getTenantId());
        body.setIndustryCode(user.getIndustryCode());
        body.setUpdateTime(new Date());
        // 先查询告警类型是否存在
        AlarmLevelVO alarmType = alarmTypeLevelMapper.getAlarmLevelByTypeValue(body);
        if (alarmType == null) {
            // 不存在进行新增操作
            body.setAppVerCode(user.getAppVerCode());
            body.setCreateTime(new Date());
            body.setCreator(user.getUserName());
            body.setDeleteFlag(0);
            int count = alarmTypeLevelMapper.insertAlarmLevel(body);
            if (count < 0) {
                return Result.fail("更新告警等级失败");
            }
            return Result.success("更新告警等级成功");
        }
        int count = alarmTypeLevelMapper.updateAlarmLevelByTypeValue(body);
        if (count < 0) {
            return Result.fail("更新告警等级失败");
        }
        return Result.success("更新告警等级成功");
    }

    @Override
    public List<ScreenDataConfigParticipationDTO> selectScreenDataConfig(Long screenDataConfigId) {
        return alarmTypeMapper.selectScreenDataConfig(screenDataConfigId);
    }

    @Override
    public Long selectScreenDataConfigCount(String screenId, String screenType) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Long screenConfigId = alarmTypeMapper.selectScreenDataConfigCountByIdAndType(screenId, screenType, user.getTenantId(), user.getIndustryCode(), user.getAppVerCode());
        return screenConfigId;
    }
}
