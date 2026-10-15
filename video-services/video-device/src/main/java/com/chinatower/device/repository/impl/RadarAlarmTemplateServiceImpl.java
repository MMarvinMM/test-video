package com.chinatower.device.repository.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.enums.RadarAlarmTypeEnum;
import com.chinatower.common.core.enums.RadarTargetTypeEnum;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.IndErrorMsgConstants;
import com.chinatower.device.constants.RadarAlarmConstants;
import com.chinatower.device.dataobject.RadarAlarmRegionDO;
import com.chinatower.device.dataobject.RadarAlarmTemplateDO;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.RadarAlarmTemplateDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.RadarAlarmBlackWhiteListMapper;
import com.chinatower.device.mapper.RadarAlarmRegionMapper;
import com.chinatower.device.mapper.RadarAlarmTemplateMapper;
import com.chinatower.device.repository.IRadarAlarmRegionService;
import com.chinatower.device.repository.IRadarAlarmTemplateService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 雷达告警模板接口实现类
 */
@Service
@Slf4j
public class RadarAlarmTemplateServiceImpl extends ServiceImpl<RadarAlarmTemplateMapper, RadarAlarmTemplateDO> implements IRadarAlarmTemplateService {


    @Autowired
    private RadarAlarmTemplateMapper alarmTemplateMapper;
    @Autowired
    private RadarAlarmBlackWhiteListMapper radarAlarmWhiteListMapper;
    @Autowired
    private RadarAlarmRegionMapper radarAlarmRegionMapper;
    @Autowired
    private IRadarAlarmRegionService radarAlarmRegionService;

    @Resource
    private NetConnectUtils connectUtils;

    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    public Result<PageInfo<RadarAlarmTemplateVO>> queryRadarAlarmTemplate(RadarAlarmTemplateDTO dto) {
        try{
            if (null == dto.getPageNum() || dto.getPageNum() <= 0) {
                dto.setPageNum(1);
            }
            if (null == dto.getPageSize() || dto.getPageSize() <= 0) {
                dto.setPageSize(10);
            }
            Result<PageInfo<RadarAlarmTemplateVO>> result;
            PageMethod.startPage(dto.getPageNum(), dto.getPageSize());
            List<RadarAlarmTemplateVO> templateList = alarmTemplateMapper.queryAlarmTemplateList(dto);
            //当模板数据不为空时--查找其关联的区域
            if (CollectionUtils.isNotEmpty(templateList)){
                //获取所有模板ID
                List<Long> templateIdList = templateList.stream().map(RadarAlarmTemplateVO::getTemplateId).collect(Collectors.toList());
                //根据所有模板ID查出所有的关联区域
                List<RadarAlarmRegionVo> regionList = alarmTemplateMapper.getRegionListByTemplateId(templateIdList);

                for (RadarAlarmTemplateVO templateVO:templateList){
                    List<RadarAlarmRegionVo> regionVoList = regionList.stream().filter(regionVo -> Objects.equals(regionVo.getTemplateId(), templateVO.getTemplateId())).collect(Collectors.toList());
                    templateVO.setUnionRegionList(regionVoList);
                }
            }
            PageInfo<RadarAlarmTemplateVO> pageInfo = new PageInfo<>(templateList);
            result = Result.success("查询成功");
            result.setData(pageInfo);
            return result;
        }catch (Exception e) {
            log.info("查询模板列表异常：",e);
            return Result.fail("模板查询异常");
        }
    }

    /**
     * 同步雷达告警模板
     */
    public ResultEntity syncAlarm(RadarAlarmTemplateVO templateVO) throws Exception {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String platformCode = user.getPlatformCode();

        SyncAlarmDTO param = new SyncAlarmDTO();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        NetConnectUtils.RequestParamter<SyncAlarmDTO> parameter = new NetConnectUtils.RequestParamter<>();
        //若为 1 打开模板请求  0 关闭模板请求
        if (templateVO.getTemplateStatus().equals(RadarAlarmConstants.COMMON_STR_1)) {
            //同步添加
            param.setType(RadarAlarmConstants.ADD);
            //拼接参数
            joinParam(param, templateVO, platformCode);
        } else if (templateVO.getTemplateStatus().equals(RadarAlarmConstants.COMMON_STR_0)) {
            //同步删除
            param.setType(RadarAlarmConstants.DELETE);
            //拼接参数
            List<String> eventIdList = new ArrayList<>();
            eventIdList.add(templateVO.getTaskId());
            param.setEventIdList(eventIdList);
        }
        //设置传参实体
        parameter.setBody(param);
        //设置接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.RADAR_ALARM_SYNC);
        parameter.setPath(path);
        log.info("雷达告警同步接口入参{}", JSON.toJSONString(parameter));
        ResultEntity resultEntity = connectUtils.post(parameter, ResultEntity.class);
        //获取返回日志
        log.info("雷达告警同步接口返回报文" + JSON.toJSONString(resultEntity));
        return resultEntity;
    }

    //区域参数拼接
    public void addAreaInfo(List<AreaInfo> areaList,RadarAlarmTemplateVO templateVO){
        AreaInfo areaInfo;
        List<RadarAlarmRegionVo> unionRegionList = templateVO.getUnionRegionList();
        if (CollectionUtils.isNotEmpty(unionRegionList)) {
            if (RadarAlarmTypeEnum.GX_ALARM.getAlarmTypeValue().equals(templateVO.getAlarmTypeValue())){
                for (RadarAlarmRegionVo regionVo : unionRegionList) {
                    areaInfo= new AreaInfo();
                    areaInfo.setAreaId(regionVo.getRegionId());
                    areaInfo.setAreaName(regionVo.getRegionName());
                    areaInfo.setAreaShape(RadarAlarmConstants.LINE_STRING);
                    if (!StringUtils.isEmpty(regionVo.getRegionJson())) {
                        RadarAlarmRegionJson mapInfo = JSON.parseObject(regionVo.getRegionJson(), RadarAlarmRegionJson.class);
                        areaInfo.setLineCoords(mapInfo.getRegionRange());
                    }
                    if (regionVo.getRadarAlarmRegionJson() != null) {
                        areaInfo.setLineCoords(regionVo.getRadarAlarmRegionJson().getRegionRange());
                    }
                    areaList.add(areaInfo);
                }
            }else {
                for (RadarAlarmRegionVo regionVo : unionRegionList) {
                    areaInfo = new AreaInfo();
                    areaInfo.setAreaId(regionVo.getRegionId());
                    areaInfo.setAreaName(regionVo.getRegionName());
                    areaInfo.setAreaShape(RadarAlarmConstants.POLYGON);
                    if (!StringUtils.isEmpty(regionVo.getRegionJson())) {
                        RadarAlarmRegionJson mapInfo = JSON.parseObject(regionVo.getRegionJson(), RadarAlarmRegionJson.class);
                        areaInfo.setPolygonCoords(mapInfo.getRegionRange());
                    }
                    if (regionVo.getRadarAlarmRegionJson() != null) {
                        areaInfo.setPolygonCoords(regionVo.getRadarAlarmRegionJson().getRegionRange());
                    }
                    areaList.add(areaInfo);
                }
            }
        }
    }

    //黑白名单信息拼接
    public void addBlackWhiteInfo(AlarmTemplateInfo alarmTemplateInfo, RadarAlarmTemplateVO templateVO){
        List<RadarAlarmBlackWhiteVO> unionBlackWhiteList = templateVO.getUnionBlackWhiteList();
        if (CollectionUtils.isNotEmpty(unionBlackWhiteList)){
            List<RadarAlarmBlackWhiteVO> alarmWhiteLists = unionBlackWhiteList.stream().filter(radarAlarmBlackWhiteVO -> radarAlarmBlackWhiteVO.getListType().equals("1")).collect(Collectors.toList());
            List<RadarAlarmBlackWhiteVO> blackWhiteListList = unionBlackWhiteList.stream().filter(radarAlarmBlackWhiteVO -> radarAlarmBlackWhiteVO.getListType().equals("2")).collect(Collectors.toList());
            RadarAlarmBlackWhiteVO blackWhiteVO;
            List<RadarAlarmBlackWhiteVO> blackList =new ArrayList<>();
            List<RadarAlarmBlackWhiteVO> whiteList =new ArrayList<>();
            if (CollectionUtils.isNotEmpty(blackWhiteListList)){
                for (RadarAlarmBlackWhiteVO alarmBlackVO:blackWhiteListList){
                    blackWhiteVO = new RadarAlarmBlackWhiteVO();
                    blackWhiteVO.setShipName(alarmBlackVO.getShipName());
                    blackWhiteVO.setMmsi(alarmBlackVO.getMmsi());
                    blackList.add(blackWhiteVO);
                }
            }
            if (CollectionUtils.isNotEmpty(alarmWhiteLists)){
                for (RadarAlarmBlackWhiteVO alarmWhiteVO:alarmWhiteLists){
                    blackWhiteVO = new RadarAlarmBlackWhiteVO();
                    blackWhiteVO.setShipName(alarmWhiteVO.getShipName());
                    blackWhiteVO.setMmsi(alarmWhiteVO.getMmsi());
                    whiteList.add(blackWhiteVO);
                }
            }
            alarmTemplateInfo.setBlackWhiteListList(blackList);
            alarmTemplateInfo.setAlarmWhiteLists(whiteList);
        }
    }

    //特有属性拼接
    private void addJsonInfo(TemplateRelInfo templateRelInfo,AlarmTemplateInfo alarmTemplateInfo){
        if (templateRelInfo != null) {
            if (CollectionUtils.isNotEmpty(templateRelInfo.getShipType())) {
                alarmTemplateInfo.setShipType(templateRelInfo.getShipType());
            }
            if (CollectionUtils.isNotEmpty(templateRelInfo.getTargetType())) {
                alarmTemplateInfo.setTargetType(templateRelInfo.getTargetType());
            }
            if (CollectionUtils.isNotEmpty(templateRelInfo.getTargetStatus())) {
                alarmTemplateInfo.setTargetStateList(templateRelInfo.getTargetStatus());
            }
            if (CollectionUtils.isNotEmpty(templateRelInfo.getShipFrom())) {
                alarmTemplateInfo.setNationalityList(templateRelInfo.getShipFrom());
            }
            if (Objects.equals(alarmTemplateInfo.getAlarmCode(),RadarAlarmConstants.COMMON_NUM_13) || Objects.equals(alarmTemplateInfo.getAlarmCode(),RadarAlarmConstants.COMMON_NUM_17)){
                if (!StringUtils.isEmpty(templateRelInfo.getMaxSpeed())) {
                    alarmTemplateInfo.setSpeedMin(templateRelInfo.getMaxSpeed());
                }
                if (!StringUtils.isEmpty(templateRelInfo.getMinSpeed())) {
                    alarmTemplateInfo.setSpeedMax(templateRelInfo.getMinSpeed());
                }
            }else {
                if (!StringUtils.isEmpty(templateRelInfo.getMaxSpeed())) {
                    alarmTemplateInfo.setSpeedMax(templateRelInfo.getMaxSpeed());
                }
                if (!StringUtils.isEmpty(templateRelInfo.getMinSpeed())) {
                    alarmTemplateInfo.setSpeedMin(templateRelInfo.getMinSpeed());
                }
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMinShipLength())) {
                alarmTemplateInfo.setMinLen(templateRelInfo.getMinShipLength());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMaxShipLength())) {
                alarmTemplateInfo.setMaxLen(templateRelInfo.getMaxShipLength());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMinMeetDistance())) {
                alarmTemplateInfo.setDcpa(templateRelInfo.getMinMeetDistance());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMeetTime())) {
                alarmTemplateInfo.setTcpa(templateRelInfo.getMeetTime());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMinParallelValue())) {
                alarmTemplateInfo.setTargetAngleMin(templateRelInfo.getMinParallelValue());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMaxParallelValue())) {
                alarmTemplateInfo.setTargetAngleMax(templateRelInfo.getMaxParallelValue());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMinDistance())) {
                alarmTemplateInfo.setTargetDistanceMin(templateRelInfo.getMinDistance());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMaxDistance())) {
                alarmTemplateInfo.setTargetDistanceMax(templateRelInfo.getMaxDistance());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getKeepTime())) {
                alarmTemplateInfo.setMinMovingDuration(templateRelInfo.getKeepTime());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMinMoveRange())) {
                alarmTemplateInfo.setMinMovingDistance(templateRelInfo.getMinMoveRange());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getStartCourse())) {
                alarmTemplateInfo.setMinHeading(templateRelInfo.getStartCourse());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getEndCourse())) {
                alarmTemplateInfo.setMaxHeading(templateRelInfo.getEndCourse());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMinStopRange())) {
                alarmTemplateInfo.setMoveRangeMin(templateRelInfo.getMinStopRange());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMaxStopRange())){
                alarmTemplateInfo.setMoveRangeMax(templateRelInfo.getMaxStopRange());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getCloseTime())){
                alarmTemplateInfo.setCloseTime(templateRelInfo.getCloseTime());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getMinStopTime())){
                alarmTemplateInfo.setTimePeriod(templateRelInfo.getMinStopTime());
            }
            if (!StringUtils.isEmpty(templateRelInfo.getCrossDir())){
                alarmTemplateInfo.setLineTest(templateRelInfo.getCrossDir());
            }
            if (templateRelInfo.getTargetFollowInfo() != null){
                TargetFollowInfo targetFollowInfo = templateRelInfo.getTargetFollowInfo();
                if (!StringUtils.isEmpty(targetFollowInfo.getCameraFollow())){
                    alarmTemplateInfo.setCameraFollow(Integer.parseInt(targetFollowInfo.getCameraFollow()));
                }
                if (!StringUtils.isEmpty(targetFollowInfo.getAutoPhoto())){
                    alarmTemplateInfo.setAutoPhoto(Integer.parseInt(targetFollowInfo.getAutoPhoto()));
                }
            }
        }
    }

    //拼接参数
    private void joinParam(SyncAlarmDTO param, RadarAlarmTemplateVO templateVO, String platformCode) {

        //-----拼接区域信息参数-----
        List<AreaInfo> areaList = new ArrayList<>();
        addAreaInfo(areaList,templateVO);
        param.setAreaList(areaList);
        //-----区域信息参数封着完成-----

        //------拼接模板信息参数-----
        AlarmTemplateInfo alarmTemplateInfo = new AlarmTemplateInfo();
        alarmTemplateInfo.setAlarmName(templateVO.getTemplateName());
        RadarAlarmTypeEnum[] values = RadarAlarmTypeEnum.values();
        for (RadarAlarmTypeEnum radarAlarmTypeEnum : values) {
            if (radarAlarmTypeEnum.getAlarmTypeValue().equals(templateVO.getAlarmTypeValue())) {
                alarmTemplateInfo.setAlarmCode(radarAlarmTypeEnum.getAlarmCode());
            }
        }

        //拼接黑白名单信息
        addBlackWhiteInfo(alarmTemplateInfo,templateVO);

        //拼接模板特有属性信息
        TemplateRelInfo templateRelInfo = new TemplateRelInfo();
        if (!StringUtils.isEmpty(templateVO.getTemplateJson())){
            templateRelInfo = JSON.parseObject(templateVO.getTemplateJson(), TemplateRelInfo.class);
        }else if (templateVO.getTemplateRelInfo() != null){
            templateRelInfo = templateVO.getTemplateRelInfo();
        }
        addJsonInfo(templateRelInfo,alarmTemplateInfo);

        //模板开始结束时间参数
        if (!StringUtils.isEmpty(templateVO.getValidStartDate())) {
            alarmTemplateInfo.setStartDate(templateVO.getValidStartDate());
        }
        if (!StringUtils.isEmpty(templateVO.getValidEndDate())) {
            alarmTemplateInfo.setEndDate(templateVO.getValidEndDate());
        }
        if (!StringUtils.isEmpty(templateVO.getValidStartTime())) {
            alarmTemplateInfo.setStartTime(templateVO.getValidStartTime());
        }
        if (!StringUtils.isEmpty(templateVO.getValidEndTime())) {
            alarmTemplateInfo.setEndTime(templateVO.getValidEndTime());
        }

        // 增加接入节点、受理节点参数
        alarmTemplateInfo.setAcceptNode(platformCode);
        alarmTemplateInfo.setAccessNode(platformCode);

        param.setAlarmTemplate(alarmTemplateInfo);
        //-----模板参数封装完成-----
    }

    @Override
    public Result<String> updateTemplateStatusById(RadarAlarmTemplateVO dto) {
        try {
            RadarAlarmTemplateDTO radarAlarmTemplateDTO = new RadarAlarmTemplateDTO();
            radarAlarmTemplateDTO.setTemplateId(dto.getTemplateId());
            //根据ID查询当前模板的详情
            Result<RadarAlarmTemplateVO> resultData = this.getTemplateInfoById(radarAlarmTemplateDTO);
            RadarAlarmTemplateVO templateVO = resultData.getData();
            if (!StringUtils.isEmpty(templateVO.getTemplateJson())) {
                templateVO.setTemplateRelInfo(JSON.parseObject(templateVO.getTemplateJson(), TemplateRelInfo.class));
            }
            //获取当前传过来的状态值
            templateVO.setTemplateStatus(dto.getTemplateStatus());
            //同步之前先判断传过来的状态是否和数据库当前状态一致---一致不做修改
            RadarAlarmTemplateDO alarmTemplateDO = alarmTemplateMapper.selectById(dto.getTemplateId());
            if (String.valueOf(alarmTemplateDO.getTemplateStatus()).equals(dto.getTemplateStatus())){
                return Result.success("模板状态修改成功!");
            }
            //调用同步接口
            ResultEntity syncResult = this.syncAlarm(templateVO);
            if (syncResult != null && RadarAlarmConstants.COMMON_STR_0.equals(syncResult.getCode()) && !StringUtils.isEmpty(syncResult.getData())) {
                radarAlarmTemplateDTO.setTemplateStatus(dto.getTemplateStatus());
                radarAlarmTemplateDTO.setSyncStatus(RadarAlarmConstants.COMMON_NUM_1);
                if (RadarAlarmConstants.COMMON_STR_1.equals(dto.getTemplateStatus())) {
                    //如果是打开模板  同步成功  则修改对应字段的值
                    radarAlarmTemplateDTO.setTaskId(syncResult.getData());
                } else if (RadarAlarmConstants.COMMON_STR_0.equals(dto.getTemplateStatus())) {
                    //关闭模板 同步成功 修改模板状态、同步状态、删除任务ID
                    radarAlarmTemplateDTO.setTaskId("");
                }
            } else {
                //如果同步失败  则模板状态不变 同步状态变 0
                radarAlarmTemplateDTO.setSyncStatus(RadarAlarmConstants.COMMON_NUM_0);
            }
            radarAlarmTemplateDTO.setModifier(dto.getModifier());
            radarAlarmTemplateDTO.setUpdateTime(dto.getUpdateTime());
            //修改告警模板
            alarmTemplateMapper.updateTemplateStatusById(radarAlarmTemplateDTO);
            if (syncResult != null && RadarAlarmConstants.COMMON_STR_0.equals(syncResult.getCode())) {
                return Result.success("模板状态修改成功!");
            } else {
                return Result.fail("雷达平台同步失败！" + (syncResult != null ? syncResult.getMsg() : ""));
            }
        } catch (Exception e) {
            log.error("模板状态修改失败:", e);
            return Result.fail("模板状态修改失败！" + e.getMessage());
        }
    }

    @Override
    public Result<RadarAlarmTemplateVO> getTemplateInfoById(RadarAlarmTemplateDTO dto) {
        RadarAlarmTemplateVO templateInfo = alarmTemplateMapper.getTemplateInfoById(dto);
        if (templateInfo != null) {
            List<RadarAlarmRegionVo> unionRegionList = alarmTemplateMapper.getRegionInfoByTemplateId(dto.getTemplateId());
            templateInfo.setUnionRegionList(unionRegionList);
            List<RadarAlarmBlackWhiteVO> alarmBlackWhiteListDOList = radarAlarmWhiteListMapper.queryByTemplateId(dto.getTemplateId(), null);
            templateInfo.setUnionBlackWhiteList(alarmBlackWhiteListDOList);
            if (StringUtils.isNotEmpty(templateInfo.getTemplateJson())) {
                templateInfo.setTemplateRelInfo(JSON.parseObject(templateInfo.getTemplateJson(), TemplateRelInfo.class));
            }
        }

        return Result.data(templateInfo);
    }

    private Boolean checkedTemplateName(String templateName, Long templateId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        //查询客户行业应用下所有告警模板
        List<String> templateNameList = alarmTemplateMapper.getTemplateNameList(templateId, tenantId, industryCode);
        return templateNameList.contains(templateName);
    }

    private Result<String> checkedRegionName(List<String> regionNameList) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        //查询客户行业应用下所有告警区域
        QueryWrapper<RadarAlarmRegionDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(RadarAlarmRegionDO::getTenantId, tenantId)
                .eq(RadarAlarmRegionDO::getIndustryCode, industryCode)
                .eq(RadarAlarmRegionDO::getDeleteFlag,RadarAlarmConstants.COMMON_NUM_0)
                .in(RadarAlarmRegionDO::getRegionName, regionNameList);
        List<RadarAlarmRegionDO> selectList = radarAlarmRegionMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(selectList)) {
            String nameStr = selectList.stream().map(RadarAlarmRegionDO::getRegionName)
                    .distinct().collect(Collectors.joining(","));
            return Result.fail("告警区域名称:" + nameStr + "已存在");
        }
        return Result.success("校验通过");
    }

    private Result<String> checkedParam(RadarAlarmTemplateReq radarAlarmTemplateReq) {
        if (StringUtils.isEmpty(radarAlarmTemplateReq.getTemplateName())) {
            return Result.fail("告警模板名称不能为空");
        }
        //检查告警模板名称是否重复
        if (Boolean.TRUE.equals(checkedTemplateName(radarAlarmTemplateReq.getTemplateName(), radarAlarmTemplateReq.getTemplateId()))) {
            return Result.fail("告警模板名称不能重复");
        }
        if (radarAlarmTemplateReq.getTemplateName().length() > RadarAlarmConstants.COMMON_NUM_15){
            return Result.fail("告警模板名称长度不能超过15个字！");
        }
        if (StringUtils.isEmpty(radarAlarmTemplateReq.getAlarmTypeValue())) {
            return Result.fail("告警类型不能为空！");
        }
        String alarmTypeValue = radarAlarmTemplateReq.getAlarmTypeValue();
        TemplateRelInfo templateRelInfo = radarAlarmTemplateReq.getTemplateRelInfo();
        if (!alarmTypeValue.equals(RadarAlarmTypeEnum.TPC_ALARM.getAlarmTypeValue())
                && !alarmTypeValue.equals(RadarAlarmTypeEnum.AIS_CLOSE_ALARM.getAlarmTypeValue())
                && !alarmTypeValue.equals(RadarAlarmTypeEnum.AIS_CHANGE_ALARM.getAlarmTypeValue())
                && !alarmTypeValue.equals(RadarAlarmTypeEnum.AIS_ADD_ALARM.getAlarmTypeValue())
                && !alarmTypeValue.equals(RadarAlarmTypeEnum.HMD_ALARM.getAlarmTypeValue())
                && !alarmTypeValue.equals(RadarAlarmTypeEnum.DT_ALARM.getAlarmTypeValue())
        ){
            if (CollectionUtils.isEmpty(templateRelInfo.getTargetType())){
                return Result.fail("目标类型不能为空！");
            }
            if (templateRelInfo.getTargetType().contains(RadarTargetTypeEnum.AIS_A.getTargetType())
                    ||templateRelInfo.getTargetType().contains(RadarTargetTypeEnum.AIS_B.getTargetType())
                    ||templateRelInfo.getTargetType().contains(RadarTargetTypeEnum.RADAR_AIS_A.getTargetType())
                    ||templateRelInfo.getTargetType().contains(RadarTargetTypeEnum.RADAR_AIS_B.getTargetType())){
                if (CollectionUtils.isEmpty(templateRelInfo.getShipType())){
                    return Result.fail("当目标类型参数包含AIS_A、AIS_B及AIS融合目标时，船舶类型为必填");
                }
                if (CollectionUtils.isEmpty(templateRelInfo.getShipFrom())){
                    return Result.fail("当目标类型参数包含AIS_A、AIS_B及AIS融合目标时，船籍为必填");
                }
            }
            if (CollectionUtils.isEmpty(templateRelInfo.getTargetStatus())){
                return Result.fail("目标状态不能为空！");
            }
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.GX_ALARM.getAlarmTypeValue()) && (StringUtils.isEmpty(templateRelInfo.getCrossDir()))) {
            return Result.fail("当告警类型为过线告警时，过线方向不能为空！");
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.ZL_ALARM.getAlarmTypeValue()) || alarmTypeValue.equals(RadarAlarmTypeEnum.ST_ALARM.getAlarmTypeValue())) {
            if (StringUtils.isEmpty(templateRelInfo.getMinSpeed())) {
                return Result.fail("当告警类型为滞留告警或双拖告警时，最小航速不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMaxSpeed())) {
                return Result.fail("当告警类型为滞留告警或双拖告警时，最大航速不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMinShipLength())) {
                return Result.fail("当告警类型为滞留告警或双拖告警时，最小船长不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMaxShipLength())) {
                return Result.fail("当告警类型为滞留告警或双拖告警时，最大船长不能为空！");
            }
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.ZL_ALARM.getAlarmTypeValue()) && StringUtils.isEmpty(templateRelInfo.getMinStopTime())) {
            return Result.fail("当告警类型为滞留告警时，最小滞留时长不能为空！");
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.ZL_ALARM.getAlarmTypeValue()) && StringUtils.isEmpty(templateRelInfo.getMaxStopRange())) {
            return Result.fail("当告警类型为滞留告警时，最大滞留范围不能为空！");
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.DS_ALARM.getAlarmTypeValue()) && StringUtils.isEmpty(templateRelInfo.getMinSpeed())) {
            return Result.fail("当告警类型为低速告警时，最小航速不能为空！");
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.GS_ALARM.getAlarmTypeValue()) && StringUtils.isEmpty(templateRelInfo.getMaxSpeed())) {
            return Result.fail("当告警类型为高速告警时，最大航速不能为空！");
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.ST_ALARM.getAlarmTypeValue())) {
            if (StringUtils.isEmpty(templateRelInfo.getMinDistance())) {
                return Result.fail("当告警类型为双拖告警时，最小两船距离不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMaxDistance())) {
                return Result.fail("当告警类型为双拖告警时，最大两船距离不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMinParallelValue())) {
                return Result.fail("当告警类型为双拖告警时，最小轨迹并行值不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMaxParallelValue())) {
                return Result.fail("当告警类型为双拖告警时，最大轨迹并行值不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMinStopRange())){
                return Result.fail("当告警类型为双拖告警时，最小滞留范围不能为空！");
            }
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.PZ_ALARM.getAlarmTypeValue())) {
            if (StringUtils.isEmpty(templateRelInfo.getMeetTime())) {
                return Result.fail("当告警类型为碰撞告警时，会遇时间不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getMinMeetDistance())) {
                return Result.fail("当告警类型为碰撞告警时，会遇距离不能为空！");
            }
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.AIS_CLOSE_ALARM.getAlarmTypeValue())){
            if (CollectionUtils.isEmpty(templateRelInfo.getTargetType())){
                return Result.fail("当告警类型为AIS关闭告警时，目标类型不能为空！");
            }
            if (CollectionUtils.isEmpty(templateRelInfo.getShipType())){
                return Result.fail("当告警类型为AIS关闭告警时，船舶类型不能为空！");
            }
            if (StringUtils.isEmpty(templateRelInfo.getCloseTime())){
                return Result.fail("当告警类型为AIS关闭告警时,最小关闭时间不能为空！");
            }
            if (CollectionUtils.isEmpty(templateRelInfo.getShipFrom())){
                return Result.fail("当告警类型为AIS关闭告警时,船籍不能为空！");
            }
        }
        if (CollectionUtils.isEmpty(radarAlarmTemplateReq.getUnionRegionList())) {
            return Result.fail("关联区域不能为空！");
        }else {
            if (radarAlarmTemplateReq.getUnionRegionList().stream().anyMatch(region ->
                    StringUtils.isEmpty(region.getRegionName()))) {
                return Result.fail("告警区域名称不能为空!");
            }
            if (radarAlarmTemplateReq.getUnionRegionList().stream().anyMatch(region ->
                    StringUtils.isNotEmpty(region.getRegionName()) && region.getRegionName().length() > RadarAlarmConstants.COMMON_NUM_15)) {
                return Result.fail("告警区域名称不能超过15个字!");
            }
            List<String> newRegionNameList = radarAlarmTemplateReq.getUnionRegionList().stream().filter(region ->
                    region.getRegionId() == null || region.getRegionId() == 0).map(RadarAlarmRegionVo::getRegionName).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(newRegionNameList)) {
                Set<String> nameList = new HashSet<>(newRegionNameList);
                if (nameList.size() != newRegionNameList.size()){
                    return Result.fail("区域名称不能相同！");
                }
                Result<String> result = checkedRegionName(newRegionNameList);
                if (result.getCode() != ResultCode.SUCCESS.getCode()) {
                    return result;
                }
            }
        }
        if (alarmTypeValue.equals(RadarAlarmTypeEnum.HMD_ALARM.getAlarmTypeValue())){
            if (CollectionUtils.isEmpty(radarAlarmTemplateReq.getUnionBlackWhiteList())) {
                return Result.fail("当告警类型为黑名单告警时，黑名单列表不能为空！");
            }
            if (CollectionUtils.isEmpty(radarAlarmTemplateReq.getUnionBlackWhiteList().
                    stream().filter(item -> RadarAlarmConstants.COMMON_STR_2.equals(item.getListType())).collect(Collectors.toList()))) {
                return Result.fail("当告警类型为黑名单告警时，黑名单列表不能为空！");
            }
        }
        if (CollectionUtils.isNotEmpty(radarAlarmTemplateReq.getUnionBlackWhiteList())) {
            if (radarAlarmTemplateReq.getUnionBlackWhiteList().stream().anyMatch(region ->
                    StringUtils.isEmpty(region.getMmsi()))) {
                return Result.fail("名单mmsi必填!");
            }
            if (radarAlarmTemplateReq.getUnionBlackWhiteList().stream().anyMatch(region ->
                    StringUtils.isEmpty(region.getListType()))) {
                return Result.fail("名单类型不能为空！");
            }
        }
        return Result.success("校验通过");
    }

    //新增或修改模板信息时区域信息处理方法
    public void addNewRegionAndRelRegion(RadarAlarmTemplateReq radarAlarmTemplateReq){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //---先将新增的区域插入区域表--获取区域ID---
        //全部区域数据
        List<RadarAlarmRegionVo> unionRegionList = radarAlarmTemplateReq.getUnionRegionList();
        //新增的区域数据---需要插入区域表获取区域ID
        List<RadarAlarmRegionVo> newRegionInfoList = unionRegionList.stream().filter(radarAlarmRegionVo ->
                radarAlarmRegionVo.getRegionId() == null).collect(Collectors.toList());
        //可以直接插入模板区域关联表的数据--已存在的区域
        List<RadarAlarmRegionVo> existRegionList = unionRegionList.stream().filter(radarAlarmRegionVo ->
                radarAlarmRegionVo.getRegionId() != null).collect(Collectors.toList());
        //新的区域数据全量集合-带区域ID
        List<RadarAlarmRegionVo> newRegionVoList = new ArrayList<>();
        //新增之后带区域ID的数据集合
        List<RadarAlarmRegionDO> radarAlarmRegionList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(newRegionInfoList)){
            for (RadarAlarmRegionVo radarAlarmRegionVo : newRegionInfoList) {
                RadarAlarmRegionDO radarAlarmRegion = new RadarAlarmRegionDO();
                BeanUtils.copyProperties(radarAlarmRegionVo, radarAlarmRegion);
                radarAlarmRegion.setTenantId(loginUser.getUser().getTenantId());
                radarAlarmRegion.setIndustryCode(loginUser.getUser().getIndustryCode());
                radarAlarmRegion.setAppVerCode(loginUser.getUser().getAppVerCode());
                radarAlarmRegion.setRegionJson(radarAlarmRegionVo.getRegionJson());
                radarAlarmRegion.setCreator(loginUser.getUsername());
                radarAlarmRegion.setCreateTime(DateUtils.getNowDate());
                radarAlarmRegionList.add(radarAlarmRegion);
            }
            //插入区域表
            radarAlarmRegionService.saveBatch(radarAlarmRegionList);
            //拼接新的区域全量数据--带区域ID
            for (RadarAlarmRegionDO radarAlarmRegionDO:radarAlarmRegionList){
                RadarAlarmRegionVo regionVo = new RadarAlarmRegionVo();
                regionVo.setRegionId(radarAlarmRegionDO.getId());
                regionVo.setRegionName(radarAlarmRegionDO.getRegionName());
                regionVo.setRegionJson(radarAlarmRegionDO.getRegionJson());
                newRegionVoList.add(regionVo);
            }
        }
        newRegionVoList.addAll(existRegionList);

        //区域数据处理完毕--重新赋值给新增模板入参--同步时用到
        radarAlarmTemplateReq.setUnionRegionList(newRegionVoList);

        //-----再将所有区域数据插入关联表vm_radar_alarm_template_region_rel-----
        List<TemplateRegionRelDTO> relInfoList = new ArrayList<>();
        for (RadarAlarmRegionVo radarAlarmRegionVo:newRegionVoList){
            TemplateRegionRelDTO templateRegionRel = new TemplateRegionRelDTO();
            templateRegionRel.setTemplateId(radarAlarmTemplateReq.getTemplateId());
            templateRegionRel.setRegionId(radarAlarmRegionVo.getRegionId());
            templateRegionRel.setTenantId(loginUser.getUser().getTenantId());
            templateRegionRel.setIndustryCode(loginUser.getUser().getIndustryCode());
            relInfoList.add(templateRegionRel);
        }
        if (CollectionUtils.isNotEmpty(relInfoList)) {
            alarmTemplateMapper.addRelInfo(relInfoList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addTemplateInfo(RadarAlarmTemplateReq radarAlarmTemplateReq) {
        //参数校验
        Result<String> stringResult = checkedParam(radarAlarmTemplateReq);
        if (!Objects.equals(stringResult.getCode(),RadarAlarmConstants.COMMON_NUM_200)) {
            return stringResult;
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        String creator = loginUser.getUsername();
        String appVerCode = loginUser.getUser().getAppVerCode();


        //先插入告警模板信息表vm_radar_alarm_template--获取模板ID
        RadarAlarmTemplateDO radarAlarmTemplate = new RadarAlarmTemplateDO();
        //默认下发失败
        radarAlarmTemplate.setTaskId("");
        radarAlarmTemplate.setSyncStatus(RadarAlarmConstants.COMMON_NUM_0);
        //默认开启状态
        radarAlarmTemplate.setTemplateStatus(RadarAlarmConstants.COMMON_NUM_1);
        radarAlarmTemplate.setTemplateName(radarAlarmTemplateReq.getTemplateName());
        radarAlarmTemplate.setAlarmTypeValue(radarAlarmTemplateReq.getAlarmTypeValue());
        radarAlarmTemplate.setTenantId(tenantId);
        radarAlarmTemplate.setIndustryCode(industryCode);
        radarAlarmTemplate.setAppVerCode(appVerCode);
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidStartDate())) {
            radarAlarmTemplate.setValidStartDate(DateUtil.parse(radarAlarmTemplateReq.getValidStartDate(), Constants.YYYY_MM_DD));
        }
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidEndDate())) {
            radarAlarmTemplate.setValidEndDate(DateUtil.parse(radarAlarmTemplateReq.getValidEndDate(), Constants.YYYY_MM_DD));
        }
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidStartTime())) {
            radarAlarmTemplate.setValidStartTime(radarAlarmTemplateReq.getValidStartTime());
        }
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidEndTime())) {
            radarAlarmTemplate.setValidEndTime(radarAlarmTemplateReq.getValidEndTime());
        }
        radarAlarmTemplate.setTemplateJson(JSON.toJSONString(radarAlarmTemplateReq.getTemplateRelInfo()));
        radarAlarmTemplate.setDeleteFlag(RadarAlarmConstants.COMMON_NUM_0);
        radarAlarmTemplate.setCreator(creator);
        radarAlarmTemplate.setCreateTime(DateUtils.getNowDate());
        alarmTemplateMapper.insert(radarAlarmTemplate);

        //获取新增模板的模板ID--插入区域关联表用到
        radarAlarmTemplateReq.setTemplateId(radarAlarmTemplate.getId());

        //处理区域信息
        addNewRegionAndRelRegion(radarAlarmTemplateReq);


        //插入黑白名单表vm_radar_alarm_black_white_list
        List<RadarAlarmBlackWhiteVO> unionBlackWhiteList = radarAlarmTemplateReq.getUnionBlackWhiteList();
        if (!unionBlackWhiteList.isEmpty()) {
            for (RadarAlarmBlackWhiteVO radarAlarmBlackWhiteVO : unionBlackWhiteList) {
                radarAlarmBlackWhiteVO.setTemplateId(radarAlarmTemplate.getId());
                radarAlarmBlackWhiteVO.setTenantId(tenantId);
                radarAlarmBlackWhiteVO.setIndustryCode(industryCode);
                radarAlarmBlackWhiteVO.setCreator(creator);
                radarAlarmBlackWhiteVO.setCreateTime(DateUtils.getNowDate());
            }
            alarmTemplateMapper.addWhiteInfo(unionBlackWhiteList);
        }

        //----模板同步开始----
        RadarAlarmTemplateVO templateVO = new RadarAlarmTemplateVO();
        //告警同步 --新增默认打开
        templateVO.setTemplateStatus(RadarAlarmConstants.COMMON_STR_1);
        BeanUtils.copyProperties(radarAlarmTemplateReq, templateVO);
        //调用同步接口
        log.info("新增模板调用同步告警接口入参{}", JSON.toJSONString(templateVO));
        ResultEntity resultEntity = null;
        try {
            resultEntity = this.syncAlarm(templateVO);
        } catch (Exception e) {
            log.info("调用雷达同步告警异常：", e);
        }
        log.info("新增模板调用同步告警返回参数{}", JSON.toJSONString(resultEntity));
        //同步完成后修改模板数据
        RadarAlarmTemplateDO alarmTemplateDO = new RadarAlarmTemplateDO();
        alarmTemplateDO.setId(radarAlarmTemplate.getId());
        if (resultEntity != null && RadarAlarmConstants.COMMON_STR_0.equals(resultEntity.getCode()) && !StringUtils.isEmpty(resultEntity.getData())) {

            //同步状态--成功 1
            alarmTemplateDO.setSyncStatus(RadarAlarmConstants.COMMON_NUM_1);
            //获取任务ID
            alarmTemplateDO.setTaskId(resultEntity.getData());
        } else {
            //同步失败--模板状态关闭 0
            alarmTemplateDO.setTemplateStatus(RadarAlarmConstants.COMMON_NUM_0);
        }
        //----模板同步结束----
        //根据同步结果修改模板同步状态
        alarmTemplateMapper.updateById(alarmTemplateDO);

        return Result.success("新增成功!");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateTemplateInfo(RadarAlarmTemplateReq radarAlarmTemplateReq) {
        //校验参数
        Result<String> stringResult = checkedParam(radarAlarmTemplateReq);
        if (!Objects.equals(stringResult.getCode(),RadarAlarmConstants.COMMON_NUM_200)) {
            return stringResult;
        }
        //检查告警模板状态--开启的模板不能编辑
        RadarAlarmTemplateDO alarmTemplateDO = alarmTemplateMapper.selectById(radarAlarmTemplateReq.getTemplateId());
        if (Objects.equals(alarmTemplateDO.getTemplateStatus(),RadarAlarmConstants.COMMON_NUM_1)){
            return Result.fail("已开启的告警模板不能编辑！");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        String modifier = loginUser.getUsername();
        //先修改告警模板表中的信息vm_radar_alarm_template
        RadarAlarmTemplateDO radarAlarmTemplate = new RadarAlarmTemplateDO();
        radarAlarmTemplate.setId(radarAlarmTemplateReq.getTemplateId());
        radarAlarmTemplate.setTemplateName(radarAlarmTemplateReq.getTemplateName());
        radarAlarmTemplate.setAlarmTypeValue(radarAlarmTemplateReq.getAlarmTypeValue());
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidStartDate())) {
            radarAlarmTemplate.setValidStartDate(DateUtil.parse(radarAlarmTemplateReq.getValidStartDate(), Constants.YYYY_MM_DD));
        }
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidEndDate())) {
            radarAlarmTemplate.setValidEndDate(DateUtil.parse(radarAlarmTemplateReq.getValidEndDate(), Constants.YYYY_MM_DD));
        }
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidStartTime())) {
            radarAlarmTemplate.setValidStartTime(radarAlarmTemplateReq.getValidStartTime());
        }
        if (StringUtils.isNotEmpty(radarAlarmTemplateReq.getValidEndTime())) {
            radarAlarmTemplate.setValidEndTime(radarAlarmTemplateReq.getValidEndTime());
        }
        radarAlarmTemplate.setTemplateJson(JSON.toJSONString(radarAlarmTemplateReq.getTemplateRelInfo()));
        radarAlarmTemplate.setUpdateTime(DateUtils.getNowDate());
        radarAlarmTemplate.setModifier(modifier);
        alarmTemplateMapper.updateById(radarAlarmTemplate);
        //关联区域 覆盖 先删后增
        radarAlarmRegionMapper.deleteRegionRelByTempId(radarAlarmTemplateReq.getTemplateId());

        //告警区域信息处理
        addNewRegionAndRelRegion(radarAlarmTemplateReq);

        //关联黑白名单覆盖 先删后增
        radarAlarmWhiteListMapper.deleteWhileBlackListByTempId(radarAlarmTemplateReq.getTemplateId());
        //插入黑白名单表vm_radar_alarm_black_white_list
        List<RadarAlarmBlackWhiteVO> unionBlackWhiteList = radarAlarmTemplateReq.getUnionBlackWhiteList();
        if (!unionBlackWhiteList.isEmpty()) {
            for (RadarAlarmBlackWhiteVO radarAlarmBlackWhiteVO : unionBlackWhiteList) {
                radarAlarmBlackWhiteVO.setTemplateId(radarAlarmTemplate.getId());
                radarAlarmBlackWhiteVO.setTenantId(tenantId);
                radarAlarmBlackWhiteVO.setIndustryCode(industryCode);
                radarAlarmBlackWhiteVO.setCreator(modifier);
                radarAlarmBlackWhiteVO.setCreateTime(DateUtils.getNowDate());
            }
            alarmTemplateMapper.addWhiteInfo(unionBlackWhiteList);
        }
        return Result.success("修改成功!");
    }

    @Override
    public Result<RadarRegionSelectedVO> queryAlarmRegionInfoByTemplateId(Long templateId, String alarmTypeValue) {
        RadarAlarmTemplateDO radarAlarmTemplate = alarmTemplateMapper.selectById(templateId);
        if (radarAlarmTemplate == null) {
            throw new CustomException("模板编码对应的模板不存在");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //
        if (StringUtils.isEmpty(alarmTypeValue)) {
            alarmTypeValue = radarAlarmTemplate.getAlarmTypeValue();
        }
        // 若告警模板处选择的告警类型为“过线告警”，则该列表只显示“线性区域”的数据。若告警模板处选择的告警类型为“除过线告警外的其他告警类型”，则该列表只显示“圆形和多边形区域”的数据
        List<Integer> regionTypeList;
        if (StringUtils.equals(alarmTypeValue, RadarAlarmTypeEnum.GX_ALARM.getAlarmTypeValue())) {
            regionTypeList = Lists.newArrayList(RadarAlarmConstants.REGION_TYPE_LINE);
        } else {
            regionTypeList = Lists.newArrayList(RadarAlarmConstants.REGION_TYPE_CIRCLE, RadarAlarmConstants.REGION_TYPE_POLYGON);
        }
        List<RadarRegionBriefVO> regionBriefVOList
                = radarAlarmRegionMapper.selectByTenantIndustryInfo(user.getTenantId(), user.getIndustryCode(), regionTypeList);

        List<Long> relateRegionIdList = radarAlarmRegionMapper.selectRelateRegionIdByTemplateId(templateId);
        List<RadarRegionBriefVO> selectedRadarRegionList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(relateRegionIdList)) {
            selectedRadarRegionList = regionBriefVOList.stream().filter(x -> relateRegionIdList.contains(x.getRegionId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(selectedRadarRegionList)) {
                regionBriefVOList.removeAll(selectedRadarRegionList);
            }
        }

        RadarRegionSelectedVO radarRegionSelectedVO = new RadarRegionSelectedVO();
        radarRegionSelectedVO.setSelectedRadarRegionList(selectedRadarRegionList);
        radarRegionSelectedVO.setUnselectedRadarRegionList(regionBriefVOList);
        return Result.data(radarRegionSelectedVO);
    }

    @Override
    @Transactional
    public Result<String> deleteTemplateById(RegionTemplateRelDTO dto) {

        RadarAlarmTemplateDO alarmTemplateDO = alarmTemplateMapper.selectById(dto.getTemplateId());
        if (alarmTemplateDO.getTemplateStatus() == 1) {
            return Result.fail("该模板为开启状态，不能删除");
        } else {
            CancelUnionRegionDTO cancelUnionRegionDTO = new CancelUnionRegionDTO();
            cancelUnionRegionDTO.setTemplateId(dto.getTemplateId());
            cancelUnionRegionDTO.setTenantId(dto.getTenantId());
            cancelUnionRegionDTO.setIndustryCode(dto.getIndustryCode());
            //删除告警模板表信息-逻辑删除
            int tempResult = alarmTemplateMapper.deleteTemplateById(dto);

            //删除告警模板区域关联表信息-物理删除
            int relResult = alarmTemplateMapper.deleteTemplateRegionRelInfo(cancelUnionRegionDTO);

            //删除告警模板关联白面单信息--物理删除
            int blackWhiteResult = radarAlarmWhiteListMapper.deleteTemplateWhiteRelInfo(dto);

            if (tempResult >= 0 && relResult >= 0 && blackWhiteResult >= 0) {
                return Result.success("删除成功!");
            } else {
                return Result.fail("删除失败！");
            }
        }
    }

    @Override
    public Result<String> getAutoConfigName() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        return Result.data(alarmTemplateMapper.getAutoConfigName(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode()));
    }
}