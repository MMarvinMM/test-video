package com.chinatower.platform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chinatower.common.core.cache.MongoTemplateDelegate;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.model.AiAlgorithmConfigReq;
import com.chinatower.event.api.model.AlarmOrderQueryDTO;
import com.chinatower.event.api.model.LocalImgReq;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.util.CommonUtils;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.util.subscenes.SubScenesOrderTableHelper;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.file.request.SysFileRequest;
import com.chinatower.file.request.SysFileUrlRequest;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constant.TableConstant;
import com.chinatower.platform.core.flow.VmFlowLinkService;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.platform.dto.Param;
import com.chinatower.platform.dto.ReceiveSatelliteAlarmDto;
import com.chinatower.platform.entity.SatelliteExtendFieldVo;
import com.chinatower.platform.entity.WatermarkFormatVo;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.mapper.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.repository.IAlarmTypeRepository;
import com.chinatower.platform.repository.IDeviceRepository;
import com.chinatower.platform.repository.ISysAlarmTagsRepository;
import com.chinatower.platform.repository.ISysService;
import com.chinatower.platform.service.EventExportService;
import com.chinatower.platform.service.ExportWarningInfoService;
import com.chinatower.platform.service.InterceptService;
import com.chinatower.platform.utils.*;
import com.chinatower.platform.dto.ByteArrayMultipartFile;
import com.chinatower.platform.dto.CaptureHttpServletResponseWrapper;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.VideoPresetDO;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.chinatower.event.constants.Constants.WARNING_SOURCE_1;

/**
 * 功能说明: export服务导出实现类
 *
 * @author lyy
 * @date 2021-12-09 15:39
 * @requestID
 */
@Service
@Slf4j
public class EventExportServiceImpl implements EventExportService {

    @Autowired
    private IWarningOrderMapper warningOrderMapper;

    @Autowired
    private IWarningOrderCollectionMapper warningOrderCollectionMapper;

    @Autowired
    private VmFlowLinkService flowLinkService;

    @Autowired
    private ISysService sysService;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private ISysAlarmTagsRepository sysAlarmTagsRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IAlarmTypeRepository alarmTypeRepository;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysDeptDeviceMapper sysDeptDeviceMapper;

    @Autowired
    private InterceptService interceptService;

    @Autowired
    private IAlarmTypeMapper alarmTypeMapper;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private MongoTemplateDelegate mongoTemplate;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private ExportWarningInfoService exportWarningInfoService;

    @Resource
    private com.chinatower.platform.utils.CommonUtils fileTool;
    @Autowired
    private DownloadUtil downloadUtil;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Value("${path.temp}")
    private String path;
    private static final DecimalFormat format = new DecimalFormat("#.000000");

    private static List<String> orderStatusList = new ArrayList<>();

    {
        orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
        orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
    }

    private static final String TARGET_DATE_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒";
    private static final String SOURCE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String TFF_PATH ="/root/font/HarmonyOS_Sans_SC_Bold.ttf";
    /**
     * 获取告警事件导出数据
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = "db1",deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,12",areaPro = "dataTable.province_code"
            ,areaCity = "dataTable.city_code",areaCode = "dataTable.county_code",processIdColumn = "dataTable.process_id"
            ,typeValueColumn="dataTable.warning_type_id")
    public List<WarningOrderExport> exportWarningOrderList(AlarmOrderDTO dto) {

        dto.setExport(true);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long[] roleIds = loginUser.getUser().getRoleIds();
        List<String> roleIdList = new ArrayList();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleIdList.add(String.valueOf(roleId));
            }
        }
        dto.setRoleIdList(roleIdList);
        // 来源：0铁塔 2租户
        String source = loginUser.getUser().getUserType();

        dto.setSource(source);
        dto.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
        //根据用户id和角色id查询流程环节信息
        SysUser sysUser = loginUser.getUser();
        String flowLinkIds = flowLinkService.getFlowLinkAttrByUser(sysUser, roleIdList);
        this.handleOrderReq(dto,flowLinkIds);
        if(!CollectionUtils.isEmpty(dto.getOrderStatusList()) &&dto.getOrderStatusList().contains(Constants.NINE)){
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        String userId = sysUser.getUserId();
        dto.setUserId(userId);
        //当前用户组织ID与上级所有组织ID
        dto.setOrgIds(CommonUtils.getOrgIds(loginUser.getUser().getDeptId()));
        List<WarningOrderListVO> resultList = new ArrayList<>();

        // 查询开始结束时间
        Date startDate = dto.getAlarmTimeStart();
        Date endDate = dto.getAlarmTimeEnd();
        dto.setSysUser(loginUser.getUser());
        //处理传入的组织条件
        handleOrgIdList(dto);
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        //跨库获取页面查询设备信息关联数据
        Function<AlarmOrderDTO,List<AlarmOrderByDeviceDTO>> fun1= this::getAlarmOrderByDeviceDTOS;
        paramMap.put(fun1,dto);
        dto.setDevList(dto.getDevList());
        //告警类型标签
        Param param = new Param();
        param.setDto(dto);
        param.setInfo(tableDataInfo);
        param.setUser(loginUser);
        Function<Param,Param> fun2= this::getTableDataInfo;
        paramMap.put(fun2,param);
        //告警标签
        Function<AlarmOrderDTO,List<AlarmLableRelDTO>> fun3= this::getAlarmLableRelDTOS;
        paramMap.put(fun3,dto);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc,f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
        dto.setWarningTypeIdList(dto.getWarningTypeIdList());
        //查询用户列表
        Map<String, String> allUser = selectAllUser();
        boolean stop = false;
        // 从最后一个表开始向前获取
        while (!stop) {
            //正序从开始获取
            String tableName = null;
            String flowTableName = null;
            if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                tableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_TEBLE);
                flowTableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_PROCESS_TEBLE);
            }
            else {
                tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
                flowTableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_PROCESS_TEBLE);
            }
            dto.setTableName(tableName);
            dto.setFlowTableName(flowTableName);
            String yearMonth = tableName.substring(tableName.length() - 6, tableName.length());
            //告警标签
            List<String> warningOrderIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                List<AlarmLableRelDTO> firstALarmLable= dto.getAlarmLableRelDTOList().stream().filter(item -> yearMonth.equals(item.getTableTime())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(firstALarmLable)) {
                    firstALarmLable.forEach(item -> {
                        warningOrderIds.add(item.getWaringOrderId());
                    });
                    dto.setWarningOrderIds(warningOrderIds);
                }else {
                    stop = DateUtils.sameMouth(startDate, endDate);
                    if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                        // 转到下月
                        String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                        startDate = DateUtils.stringToDateYM(nextMonth);
                    }
                    else {
                        // 转到上月
                        String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                        endDate = DateUtils.stringToDateYM(lastMonth);
                    }
                    continue;
                }
            }
            List<WarningOrderListVO> queryResult = warningOrderMapper.queryAlarmOrder(dto);
            this.jumpChcek(dto,queryResult,allUser);

            for (WarningOrderListVO orderVo : queryResult) {
                orderVo.setHoriAzimuthAngle(AngleUtils.transformAngle(orderVo.getHoriAzimuthAngle()));
            }
            resultList.addAll(queryResult);
            stop = DateUtils.sameMouth(startDate, endDate);
            if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                // 转到下月
                String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                startDate = DateUtils.stringToDateYM(nextMonth);
            }
            else {
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }
        }
        //组装description
        Map<String, String> aiDescMap = getAiAlgorithmConfigDescMap(
                "exportWarningOrderList", resultList,
                "orderId", "warningSource", "taskId", "tenantId",
                "deviceCode", "channelCode", "algorithmCode");
        resultList.stream().filter(r -> WARNING_SOURCE_1.equals(r.getWarningSource())).forEach(r -> r.setDescription(aiDescMap.getOrDefault(r.getOrderId(), "")));
        this.resultHandle(resultList, dto, userId,flowLinkIds);

        List<WarningOrderExport> exportList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            //获取设备、通道别名
            this.getStaticDeviceRel(resultList);
            //获取工单ID集合
            List<String> orderIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getOrderId()))
                    .map(item -> item.getOrderId()).distinct().collect(Collectors.toList());
            //获取预置位ID、摄像机编码集合（去重）
            List<String> presetIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getPresetId()))
                    .map(item -> item.getPresetId()).distinct().collect(Collectors.toList());
            List<String> deviceCodeList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getDeviceCode()))
                    .map(item -> item.getDeviceCode()).distinct().collect(Collectors.toList());
            //v1.7.00 获取组织信息
            List<OrganInfoReq> paramList = resultList.stream().map(item -> {
                OrganInfoReq reqDTO = new OrganInfoReq();
                reqDTO.setWarningOrderId(item.getOrderId());
                reqDTO.setWarningSource(item.getWarningSource());
                reqDTO.setDeviceCode(item.getDeviceCode());
                reqDTO.setProvinceId(item.getProvinceId());
                reqDTO.setCityId(item.getCityId());
                reqDTO.setCountyId(item.getCountyId());
                reqDTO.setFindName(item.getFindName());
                reqDTO.setTenantId(sysUser.getTenantId());
                reqDTO.setIndustryCode(sysUser.getIndustryCode());
                return reqDTO;
            }).collect(Collectors.toList());
            List<Map<String, String>> orgInfoList = iTradeWarningOrderService.getOrgInfoList(paramList);
            List<VideoPresetDO> videoPresetList = null;
            List<DeviceSiteVO> deviceSiteVOList = null;
            List<DeviceSiteVO> iotDeviceSiteVOList = null;
            List<AlarmLableRelDTO> alarmLableRelList = null;
            //获取预置位名称
            if (!CollectionUtils.isEmpty(presetIdList)) {
                videoPresetList =deviceRepository.selectVideoPreById(presetIdList,deviceCodeList);
            }
            //增加摄像机地址，转义PTZ值
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                deviceSiteVOList = deviceRepository.queryDevice(deviceCodeList);
                //物联告警
                iotDeviceSiteVOList = deviceRepository.queryIotDevice(deviceCodeList);
            }
            //获取关联的告警标签
            if (!CollectionUtils.isEmpty(orderIdList)) {
                alarmLableRelList = alarmTypeMapper.getAlarmLableByOrderIds(orderIdList);
            }
            for (WarningOrderListVO m : resultList) {
                WarningOrderExport warningOrderExport = new WarningOrderExport();
                BeanUtils.copyProperties(m, warningOrderExport);
                warningOrderExport.setRemark(m.getCheckRemark());
                if (!StringUtils.isEmpty(warningOrderExport.getJudgeResult())) {
                    if (Constants.JUDGE_RESULT_0.equals(warningOrderExport.getJudgeResult())) {
                        warningOrderExport.setJudgeResultName(Constants.FALSE_WARING);
                    } else if (Constants.JUDGE_RESULT_1.equals(warningOrderExport.getJudgeResult())) {
                        warningOrderExport.setJudgeResultName(Constants.TRUE_WARING);
                    } else if (Constants.JUDGE_RESULT_2.equals(warningOrderExport.getJudgeResult())) {
                        warningOrderExport.setJudgeResultName(Constants.REPEAT_WARING);
                    }
                }
                //匹配经纬度、PTZ、预置位信息
                warningOrderExport = handleExportParamsNew(m, videoPresetList, deviceSiteVOList, iotDeviceSiteVOList, orgInfoList, warningOrderExport);
                //告警标签
                if (!CollectionUtils.isEmpty(alarmLableRelList)) {
                    WarningOrderExport finalWarningOrderExport = warningOrderExport;
                    alarmLableRelList.forEach(item -> {
                        if(m.getOrderId().equals(item.getWaringOrderId())){
                            finalWarningOrderExport.setAlarmLableName(item.getLableName());
                        }
                    });
                }
                warningOrderExport.setFileImgUrlSrc(m.getImgUrl());
                exportList.add(warningOrderExport);
            }
            //处理摄像机标签
            warningOrderUtil.handleDeviceLabel(exportList,loginUser);
        }
        return exportList;
    }

    List<AlarmOrderByDeviceDTO> getAlarmOrderByDeviceDTOS(AlarmOrderDTO dto) {
        List<AlarmOrderByDeviceDTO> devList = deviceRepository.queryAlarmOrderByDevice(dto);
        dto.setDevList(devList);
        return devList;
    }

    private List<AlarmLableRelDTO> getAlarmLableRelDTOS(AlarmOrderDTO dto) {
        List<AlarmLableRelDTO> alarmLableRelDTOList = alarmTypeRepository.getAlarmLableByIds(dto.getLableIdList(), dto.getOrderId());
        dto.setAlarmLableRelDTOList(alarmLableRelDTOList);
        return alarmLableRelDTOList;
    }

    private Param getTableDataInfo(Param param) {
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

    /**
     * 我的事件-我的收藏导出功能
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = "db1",deviceColumn="t.device_code",tenantColumn="t.tenant_id",
            industryCode="t.industry_code", alarmSourceColumn="t.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,12",areaPro = "t.province_code"
            ,areaCity = "t.city_code",areaCode = "t.county_code",processIdColumn = "t.process_id"
            ,typeValueColumn="t.warning_type_id")
    public List<WarningOrderExport> exportWarningOrderCollectionList(AlarmOrderDTO dto) {
        dto.setExport(true);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            throw new CustomException(Constants.NO_USER_INFO);
        }
        if(!CollectionUtils.isEmpty(dto.getOrderStatusList()) &&dto.getOrderStatusList().contains(Constants.NINE)){
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        String loginUserId = sysService.getLoginUserId();
        dto.setCollectionCreator(loginUserId);
        dto.setRoleIds(loginUser.getUser().getRoleIds());
        dto.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
        dto.setTenantId(loginUser.getUser().getTenantId());
        long start = System.currentTimeMillis();
        String flowTableName = TableUtil.getTableName(new Date(), TableConstant.WARN_ORDER_PROCESS_TEBLE);
        dto.setFlowTableName(flowTableName);
        dto.setSysUser(loginUser.getUser());
        //处理传入的组织条件
        handleOrgIdList(dto);
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        //跨库获取页面查询设备信息关联数据
        Function<AlarmOrderDTO,List<AlarmOrderByDeviceDTO>> fun1= this::getAlarmOrderByDeviceDTOS;
        paramMap.put(fun1,dto);
        //告警类型标签
        Param param = new Param();
        AlarmOrderDTO orderDTO = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto,orderDTO);
        param.setDto(orderDTO);
        param.setInfo(null);
        param.setUser(loginUser);
        Function<Param, Param> fun2= this::getTableDataInfo;
        paramMap.put(fun2,param);
        //告警标签
        Function<AlarmOrderDTO,List<AlarmLableRelDTO>> fun3= this:: getAlarmLableRelDTOS;
        paramMap.put(fun3,orderDTO);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc, f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_INTERCEPT);
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
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(dto.getDevList()) && (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList()) || !StringUtils.isEmpty(dto.getModelId()))) {
            return new ArrayList<>();
        }
        dto.setDevList(dto.getDevList());
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if((!CollUtil.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode()) )|| (StringUtil.isNotEmpty(dto.getOrderStatus())&&EnumUtil.FlowNode.END.getCode().contains(dto.getOrderStatus()) )){
            List<String> orderStatusList = new ArrayList<String>();
            if((StringUtil.isNotEmpty(dto.getOrderStatus()))&&(dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))){
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            }else{
                orderStatusList=dto.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            dto.setOrderStatusList(orderStatusList);
        }
        List<WarningOrderListVO> resultList ;
        resultList = warningOrderCollectionMapper.qryWarningOrderCollection(dto);
        //查询用户列表
        Map<String, String> allUser = selectAllUser();
        this.jumpChcek(dto,resultList,allUser);
        //告警结果集
        List<Map> warningOrderList = new ArrayList<>();
        //告警数据月份结果集
        List<String> warningOrderYMList = new ArrayList<>();
        //告警数据月份数据结果集
        Map<String, List<String>> map = new HashMap<>();
        for (WarningOrderListVO orderListVO : resultList) {
            String orderId = String.valueOf(orderListVO.getOrderId());
            String warningOrderYM = orderId.substring(0, 6);
            if (CollectionUtils.isEmpty(map.get(warningOrderYM))) {
                List<String> list = new ArrayList<>();
                list.add(orderId);
                map.put(warningOrderYM, list);
                warningOrderYMList.add(warningOrderYM);
            }
            else {
                List<String> list = map.get(warningOrderYM);
                list.add(orderId);
                map.put(warningOrderYM, list);
            }
        }
        for (String warningOrderYM : warningOrderYMList) {
            String tableName = "";
            try {
                // 获取表名
                tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderYM;
                String processTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderYM;
                Map reqMap = new HashMap();
                reqMap.put(Constants.PARAM_TABLE_NAME, tableName);
                reqMap.put(Constants.PARAM_PROCESS_TABLE_NAME, processTableName);
                reqMap.put(Constants.PARAM_WARNING_ORDER_ID_LIST, map.get(warningOrderYM));
                List<Map> resList = warningOrderMapper.getWarningOrderList(reqMap);
                if (!CollectionUtils.isEmpty(resList)) {
                    warningOrderList.addAll(resList);
                }
            }
            catch (Exception e) {
                log.error("我的收藏查询告警表:{},异常：", tableName, e);
            }
        }
        //组装description
        Map<String, String> aiDescMap = getAiAlgorithmConfigDescMap(
                "exportWarningOrderCollectionList", resultList,
                Constants.RES_WARNING_ORDER_ID, Constants.RES_WARNING_SOURCE,
                Constants.INDEXFIELD_TASK_ID, Constants.RES_TENANT_ID,
                "device_code", "channel_code",
                Constants.PARAM_ALGORITHM_CODE_FIELD);
        for (WarningOrderListVO vo : resultList) {
            //从工单表获取透传字段
            warningOrderList.forEach(item -> {
                if (vo.getOrderId().equals(item.get(Constants.RES_WARNING_ORDER_ID))) {
                    vo.setExtraField(String.valueOf(item.get(Constants.WARNING_ORDER_EXTRA_FIELD)));
                    vo.setDeleteFlag(Integer.parseInt(item.get(Constants.PARAM_DELETE_FLAG_FIELD).toString()));
                    //填充description字段 AI告警
                    if (WARNING_SOURCE_1.equals(String.valueOf(item.get(Constants.RES_WARNING_SOURCE)))) {
                        vo.setDescription(aiDescMap.getOrDefault(vo.getOrderId(), ""));
                    }
                }
            });
        }
        resultHandle(resultList, dto, dto.getCollectionCreator(),"");
        List<VideoPresetDO> videoPresetList = null;
        List<DeviceSiteVO> deviceSiteVOList = null;
        List<DeviceSiteVO> iotDeviceSiteVOList = null;
        List<AlarmLableRelDTO> alarmLableRelList = null;
        List<Map<String, String>> orgInfoList = null;
        if (!CollectionUtils.isEmpty(resultList)) {
            //获取工单ID集合
            List<String> orderIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getOrderId()))
                    .map(item -> item.getOrderId()).distinct().collect(Collectors.toList());
            //获取预置位ID、摄像机编码集合（去重）
            List<String> presetIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getPresetId()))
                    .map(item -> item.getPresetId()).distinct().collect(Collectors.toList());
            List<String> deviceCodeList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getDeviceCode()))
                    .map(item -> item.getDeviceCode()).distinct().collect(Collectors.toList());
            //获取预置位名称
            if (!CollectionUtils.isEmpty(presetIdList)) {
                videoPresetList =deviceRepository.selectVideoPreById(presetIdList,deviceCodeList);
            }
            //v1.7.00 获取组织信息
            List<OrganInfoReq> paramList = resultList.stream().map(item -> {
                OrganInfoReq reqDTO = new OrganInfoReq();
                reqDTO.setWarningOrderId(item.getOrderId());
                reqDTO.setWarningSource(item.getWarningSource());
                reqDTO.setDeviceCode(item.getDeviceCode());
                reqDTO.setProvinceId(item.getProvinceId());
                reqDTO.setCityId(item.getCityId());
                reqDTO.setCountyId(item.getCountyId());
                reqDTO.setFindName(item.getFindName());
                reqDTO.setTenantId(loginUser.getUser().getTenantId());
                reqDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
                return reqDTO;
            }).collect(Collectors.toList());
            orgInfoList = iTradeWarningOrderService.getOrgInfoList(paramList);
            //增加摄像机地址，转义PTZ值
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                deviceSiteVOList = deviceRepository.queryDevice(deviceCodeList);
                //物联告警
                iotDeviceSiteVOList = deviceRepository.queryIotDevice(deviceCodeList);
            }
            //获取关联的告警标签
            if (!CollectionUtils.isEmpty(orderIdList)) {
                alarmLableRelList = alarmTypeMapper.getAlarmLableByOrderIds(orderIdList);
            }
            //获取设备、通道别名
            this.getStaticDeviceRel(resultList);
        }
        List<VideoPresetDO> finalVideoPresetList = videoPresetList;
        List<DeviceSiteVO> finalDeviceSiteVOList = deviceSiteVOList;
        List<DeviceSiteVO> finalIotDeviceSiteVOList = iotDeviceSiteVOList;
        List<AlarmLableRelDTO> finalAlarmLableRelList = alarmLableRelList;
        List<Map<String, String>> finalOrgInfoList = orgInfoList;
        List<WarningOrderExport> exportList = resultList.stream().map(e -> {
            WarningOrderExport export = new WarningOrderExport();
            BeanUtils.copyProperties(e, export);
            export.setRemark(e.getCheckRemark());
            if (!StringUtils.isEmpty(export.getJudgeResult())) {
                if ("0".equals(export.getJudgeResult())) {
                    export.setJudgeResultName("误报");
                } else if ("1".equals(export.getJudgeResult())) {
                    export.setJudgeResultName("真警");
                } else if ("2".equals(export.getJudgeResult())) {
                    export.setJudgeResultName("重复告警");
                }
            }
            //匹配经纬度、PTZ、预置位信息
            export = handleExportParamsNew(e, finalVideoPresetList, finalDeviceSiteVOList, finalIotDeviceSiteVOList, finalOrgInfoList, export);
            //告警标签
            if (!CollectionUtils.isEmpty(finalAlarmLableRelList)) {
                WarningOrderExport finalWarningOrderExport = export;
                finalAlarmLableRelList.forEach(item -> {
                    if(e.getOrderId().equals(item.getWaringOrderId())){
                        finalWarningOrderExport.setAlarmLableName(item.getLableName());
                    }
                });
            }
            export.setFileImgUrlSrc(e.getImgUrl());
            return export;
        }).collect(Collectors.toList());
        //处理摄像机标签
        warningOrderUtil.handleDeviceLabel(exportList,loginUser);
        log.info("导出我的收藏-查询列表耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));
        return exportList;
    }

    /**
     * 匹配设备、通道别名
     * @param resultList
     */
    private void getStaticDeviceRel(List<WarningOrderListVO> resultList) {
        List<TenantDeviceRelDTO> tenantDeviceRelDTOList = new ArrayList<>();
        for (WarningOrderListVO orderListVO : resultList) {
            TenantDeviceRelDTO tenantDeviceRelDTO = new TenantDeviceRelDTO();
            tenantDeviceRelDTO.setDeviceCode(orderListVO.getDeviceCode());
            tenantDeviceRelDTO.setTenantId(orderListVO.getTenantId());
            tenantDeviceRelDTO.setChannelCode(orderListVO.getChannelCode());
            tenantDeviceRelDTOList.add(tenantDeviceRelDTO);
        }
        if(!CollectionUtils.isEmpty(tenantDeviceRelDTOList)){
            List<TenantDeviceEventRelVO> tenantDeviceEventRelVOList = deviceRepository.getDeviceRel(tenantDeviceRelDTOList);
            if (!CollectionUtils.isEmpty(tenantDeviceEventRelVOList)) {
                for (TenantDeviceEventRelVO vo:tenantDeviceEventRelVOList) {
                    for (WarningOrderListVO warningOrderListVO : resultList) {
                        if (vo != null && vo.getDeviceCode().equals(warningOrderListVO.getDeviceCode())) {
                            warningOrderListVO.setDeviceName(vo.getDeviceName());
                        }
                        if(vo != null && vo.getChannelCode().equals(warningOrderListVO.getChannelCode())){
                            warningOrderListVO.setChannelName(vo.getChannelName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 导出领导批示列表
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = "db1",deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,12",areaPro = "dataTable.province_code"
            ,areaCity = "dataTable.city_code",areaCode = "dataTable.county_code")
    public List<LeaderExport> exportLeaderAuditList(LeaderAuidtListDTO dto) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(dto.getTenantId())) {
            dto.setTenantId(loginUser.getUser().getTenantId());
        }
        dto.setUserId(sysService.getLoginUserId());
        // 获取当前用户角色
        Long[] roleId = loginUser.getUser().getRoleIds();
        //来源：0铁塔 2租户
        String source = loginUser.getUser().getUserType();
        dto.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
        dto.setSource(source);
        // 设置导出状态
        dto.setExport(true);
        if(!dto.isAdmin() && dto.getDataScope()!=null){
            String dataScopeTemp = dto.getDataScope().substring(dto.getDataScope().indexOf("and")+4);
            dto.setDataScopeTemp(dataScopeTemp);
        }
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        AlarmOrderDTO orderDTO = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto,orderDTO);
        //告警类型标签
        Param param = new Param();
        param.setDto(orderDTO);
        param.setInfo(new TableDataInfo());
        param.setUser(loginUser);
        Function<Param,Param> fun1= this::getTableDataInfo;
        paramMap.put(fun1,param);
        Function<AlarmOrderDTO,List<AlarmLableRelDTO>> fun2= this:: getAlarmLableRelDTOS;
        paramMap.put(fun2,orderDTO);
        //跨库获取页面查询设备信息关联数据
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        alarmOrderDTO.setDeviceLabelId(dto.getDeviceLabelId());
        alarmOrderDTO.setDeviceLabelIdList(dto.getDeviceLabelIdList());
        alarmOrderDTO.setOrgId(dto.getOrgId());
        alarmOrderDTO.setOrgIdList(dto.getOrgIdList());
        alarmOrderDTO.setModelId(dto.getModelId());
        alarmOrderDTO.setTenantId(loginUser.getUser().getTenantId());
        alarmOrderDTO.setSysUser(loginUser.getUser());
        //处理传入的组织条件
        handleOrgIdList(alarmOrderDTO);
        Function<AlarmOrderDTO,List<AlarmOrderByDeviceDTO>> fun3= this::getAlarmOrderByDeviceDTOS;
        paramMap.put(fun3,alarmOrderDTO);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc,f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_LEADER);
        if (CollectionUtils.isEmpty(alarmOrderDTO.getDevList())
                && (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(dto.getModelId()))) {
            return new ArrayList<>();
        }
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
            return new ArrayList<>();
        }
        dto.setDeviceCodeList(alarmOrderDTO.getDeviceCodeList());
        dto.setSatelliteList(alarmOrderDTO.getSatelliteList());
        dto.setUserIdList(alarmOrderDTO.getUserIdList());
        dto.setDevList(alarmOrderDTO.getDevList());
        dto.setOrgUserListString(alarmOrderDTO.getOrgUserListString());
        // 领导批示表无 sub_scenes：按告警时间展开工单月表，供 EXISTS 过滤下游任务
        if (!SubScenesOrderTableHelper.fillWarningOrderTableList(
                dto.getSubSceneIdList(), dto.getAlarmTimeStart(), dto.getAlarmTimeEnd(),
                dto::setWarningOrderTableList)) {
            return new ArrayList<>();
        }
        List<LeaderAuidtListVO> resultList = warningOrderMapper.getLeaderAuditList(dto);
        List<String> userIdList = new ArrayList<>();
        for (LeaderAuidtListVO vo : resultList) {
            userIdList.add(vo.getLeaderId());
            userIdList.add(vo.getApplyUserId());
        }
        List<SysUserVO> userList = deviceRepository.getUserInfoByUserIds(userIdList);
        List<LeaderExport> exportList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultList)) {

            List<TenantDeviceRelDTO> tenantDeviceRelDTOList = new ArrayList<>();
            for (LeaderAuidtListVO leaderAuidtListVO : resultList) {
                boolean leaderFlag = false;
                boolean applyFlag = false;
                for (SysUserVO vo : userList) {
                    if (!StringUtils.isEmpty(leaderAuidtListVO.getLeaderId()) && leaderAuidtListVO.getLeaderId().equals(String.valueOf(vo.getUserId()))) {
                        leaderAuidtListVO.setLeaderName(vo.getUserName());
                        leaderFlag = true;
                    }
                    if (!StringUtils.isEmpty(leaderAuidtListVO.getApplyUserId()) && leaderAuidtListVO.getApplyUserId().equals(String.valueOf(vo.getUserId()))) {
                        leaderAuidtListVO.setApplyUserName(vo.getUserName());
                        applyFlag = true;
                    }
                    if (leaderFlag && applyFlag) {
                        break;
                    }
                }
                TenantDeviceRelDTO deviceRelDTO = new TenantDeviceRelDTO();
                deviceRelDTO.setDeviceCode(leaderAuidtListVO.getDeviceCode());
                deviceRelDTO.setChannelCode(leaderAuidtListVO.getChannelCode());
                deviceRelDTO.setTenantId(leaderAuidtListVO.getTenantId());
                tenantDeviceRelDTOList.add(deviceRelDTO);
            }
            List<TenantDeviceEventRelVO> tenantDeviceEventRelVOList = deviceRepository.getDeviceRel(tenantDeviceRelDTOList);

            //查询告警类型名称
            List<Map> alramTypeList = getAlarmTypeList();
            // 告警等级名称
            List<SysDictDataResponse> emergencyLevelEnum = this.getEnumList(Constants.EMERGENCY_LEVEL);

            // 重要等级
            List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(Constants.ALARM_LEVEL).getData();

            // 告警来源枚举
            List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
            //查询 告警来源、事件描述、研判结果、研判人、研判完成时间
            //告警结果集
            List<Map> warningOrderList = new ArrayList<>();
            //领导批示 告警数据月份结果集
            List<String> warningOrderYMList = new ArrayList<>();
            //领导批示 告警数据月份数据结果集
            Map<String, List<String>> map = new HashMap<>();
            for (LeaderAuidtListVO m : resultList) {
                if (!CollectionUtils.isEmpty(tenantDeviceEventRelVOList)) {
                    boolean deviceFlag = false;
                    boolean channelFlag = false;
                    for (TenantDeviceEventRelVO tenantDeviceEventRelVO : tenantDeviceEventRelVOList) {
                        if (!StringUtils.isEmpty(m.getDeviceCode()) && m.getDeviceCode().equals(tenantDeviceEventRelVO.getDeviceCode())) {
                            m.setDeviceName(tenantDeviceEventRelVO.getDeviceName());
                            deviceFlag = true;
                        }
                        if (!StringUtils.isEmpty(m.getChannelCode()) && m.getChannelCode().equals(tenantDeviceEventRelVO.getChannelCode())) {
                            m.setChannelName(tenantDeviceEventRelVO.getChannelName());
                            channelFlag = true;
                        }
                        if (deviceFlag && channelFlag) {
                            break;
                        }
                    }
                }

                String orderId = String.valueOf(m.getOrderId());
                String warningOrderYM = orderId.substring(0, 6);
                if (CollectionUtils.isEmpty(map.get(warningOrderYM))) {
                    List<String> list = new ArrayList<>();
                    list.add(orderId);
                    map.put(warningOrderYM, list);
                    warningOrderYMList.add(warningOrderYM);
                } else {
                    List<String> list = map.get(warningOrderYM);
                    list.add(orderId);
                    map.put(warningOrderYM, list);
                }
            }
            //查询用户列表
            Map<String, String> allUser = selectAllUser();
            for (String warningOrderYM : warningOrderYMList) {
                String tableName = "";
                try {
                    // 获取表名
                    tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderYM;
                    String processTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderYM;
                    Map reqMap = new HashMap();
                    reqMap.put(Constants.PARAM_TABLE_NAME, tableName);
                    reqMap.put(Constants.PARAM_PROCESS_TABLE_NAME, processTableName);
                    reqMap.put(Constants.PARAM_WARNING_ORDER_ID_LIST, map.get(warningOrderYM));
                    List<Map> resList = warningOrderMapper.getWarningOrderList(reqMap);
                    //查询研判人
                    if (dto.isExport() && !CollectionUtils.isEmpty(resList)) {
                        List<String> orderIdList = new ArrayList<>();
                        resList.forEach(itemRe ->{
                            orderIdList.add(String.valueOf(itemRe.get(Constants.RES_WARNING_ORDER_ID)));
                        });
                        List<FlowRecordVO>  flowRecordVOS = warningOrderMapper.queryAlarmOrderFlowRecord(processTableName,orderIdList);
                        resList.forEach(item ->{
                            flowRecordVOS.forEach(item2 ->{
                                if (item.get(Constants.RES_WARNING_ORDER_ID).equals(item2.getOrderId())&& !StringUtils.isEmpty(item2.getRealLinkType())) {
                                    handlingInstructionsMap(item,item2,allUser);
                                }
                            });
                        });
                    }
                    if (!CollectionUtils.isEmpty(resList)) {
                        warningOrderList.addAll(resList);
                    }
                } catch (Exception e) {
                    log.error("导出领导批示查询告警表:{},异常：", tableName, e);
                }
            }
            //组装description
            Map<String, String> aiDescMap = getAiAlgorithmConfigDescMap(
                    "exportLeaderAuditList", warningOrderList,
                    Constants.RES_WARNING_ORDER_ID, Constants.RES_WARNING_SOURCE,
                    Constants.INDEXFIELD_TASK_ID, Constants.RES_TENANT_ID,
                    "device_code", "channel_code",
                    Constants.PARAM_ALGORITHM_CODE_FIELD);
            //获取预置位ID集合
            List<String> presetIdList = new ArrayList<>();
            resultList.forEach(m -> {
                warningOrderList.forEach(order -> {
                    if (m.getOrderId().equals(order.get(Constants.RES_WARNING_ORDER_ID))) {
                        if (ObjectUtils.isNotEmpty(order.get(Constants.PARAM_PRESET_ID))) {
                            presetIdList.add(String.valueOf(order.get(Constants.PARAM_PRESET_ID)));
                        }
                        if(ObjectUtils.isNotEmpty(order.get("alarmReason"))){
                            m.setAlarmReason(String.valueOf(order.get("alarmReason")));
                        }
                        //填充description字段 AI告警
                        if (WARNING_SOURCE_1.equals(String.valueOf(order.get(Constants.RES_WARNING_SOURCE)))) {
                            order.put(Constants.PARAM_DESCRIPTION, aiDescMap.getOrDefault(m.getOrderId(), ""));
                        }
                    }
                });
            });
            //获取摄像机编码集合
            List<String> deviceCodeList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getDeviceCode()))
                    .map(item -> item.getDeviceCode()).distinct().collect(Collectors.toList());
            List<VideoPresetDO> videoPresetList = null;
            List<DeviceSiteVO> deviceSiteVOList = null;
            List<DeviceSiteVO> iotDeviceSiteVOList = null;
            //获取预置位名称
            if (!CollectionUtils.isEmpty(presetIdList)) {
                videoPresetList =deviceRepository.selectVideoPreById(presetIdList,deviceCodeList);
            }
            //v1.7.00 获取组织名称
            List<OrganInfoReq> paramList = resultList.stream().map(item -> {
                OrganInfoReq reqDTO = new OrganInfoReq();
                reqDTO.setWarningOrderId(item.getOrderId());
                reqDTO.setWarningSource(item.getWarningSource());
                reqDTO.setDeviceCode(item.getDeviceCode());
                reqDTO.setProvinceId(item.getProvinceId());
                reqDTO.setCityId(item.getCityId());
                reqDTO.setCountyId(item.getCountyId());
                reqDTO.setFindName(item.getFindName());
                reqDTO.setTenantId(loginUser.getUser().getTenantId());
                reqDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
                return reqDTO;
            }).collect(Collectors.toList());
            List<Map<String, String>> orgInfoList = iTradeWarningOrderService.getOrgInfoList(paramList);
            //增加摄像机地址，转义PTZ值
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                deviceSiteVOList = deviceRepository.queryDevice(deviceCodeList);
                //物联告警
                iotDeviceSiteVOList = deviceRepository.queryIotDevice(deviceCodeList);
            }
            // 告警状态名称
            for (LeaderAuidtListVO m : resultList) {
                for (Map order : warningOrderList) {
                    if (m.getOrderId().equals(order.get(Constants.RES_WARNING_ORDER_ID))) {
                        //事件描述
                        String description;
                        String sourceValue = String.valueOf(order.get(Constants.RES_WARNING_SOURCE));
                        if (ObjectUtils.isNotEmpty(order.get(Constants.PARAM_DESCRIPTION)) && !Constants.FOUR.equals(sourceValue) && !Constants.ONE.equals(sourceValue)) {
                            description = String.valueOf(order.get(Constants.PARAM_DESCRIPTION));
                        } else {
                            //根据扩展字段拼接事件描述
                            description = this.splicingEventDescription(String.valueOf(order.get(Constants.PARAM_DESCRIPTION)),String.valueOf(order.get(Constants.WARNING_ORDER_EXTRA_FIELD)), String.valueOf(order.get(Constants.RES_WARNING_SOURCE)));
                        }
                        m.setDescription(description);
                        if (ObjectUtils.isNotEmpty(order.get(Constants.WARNING_ORDER_EXTRA_FIELD))) {
                            m.setExtraField(String.valueOf(order.get(Constants.WARNING_ORDER_EXTRA_FIELD)));
                        }
                        //研判结果
                        if (!StringUtils.isEmpty(String.valueOf(order.get(Constants.RES_JUDGE_RESULT)))) {
                            if (Constants.JUDGE_RESULT_0.equals(String.valueOf(order.get(Constants.RES_JUDGE_RESULT)))) {
                                m.setJudgeResultName(Constants.FALSE_WARING);
                            } else if (Constants.JUDGE_RESULT_1.equals(String.valueOf(order.get(Constants.RES_JUDGE_RESULT)))) {
                                m.setJudgeResultName(Constants.TRUE_WARING);
                            } else if (Constants.JUDGE_RESULT_2.equals(String.valueOf(order.get(Constants.RES_JUDGE_RESULT)))) {
                                m.setJudgeResultName(Constants.REPEAT_WARING);
                            }
                        }
                        //研判人
                        if (ObjectUtils.isNotEmpty(order.get(Constants.RES_PROCESS_NAME))) {
                            m.setCheckMan(String.valueOf(order.get(Constants.RES_PROCESS_NAME)));
                        }
                        //研判人账号
                        if (ObjectUtils.isNotEmpty(order.get(Constants.RES_PROCESS_USER_NAME))) {
                            m.setCheckManUserName(String.valueOf(order.get(Constants.RES_PROCESS_USER_NAME)));
                        }
                        //研判完成时间
                        if (ObjectUtils.isNotEmpty(order.get(Constants.RES_MODIFIER_DATE))) {
                            m.setCheckTime(String.valueOf(order.get(Constants.RES_MODIFIER_DATE)));
                        }
                        //研判说明
                        if (ObjectUtils.isNotEmpty(order.get(Constants.REMARK))) {
                            m.setRemark(String.valueOf(order.get(Constants.REMARK)));
                        }
                        //PTZ值
                        if (ObjectUtils.isNotEmpty(order.get(Constants.PARAM_PTZ_P))) {
                            m.setPtzP(String.valueOf(order.get(Constants.PARAM_PTZ_P)));
                        }
                        if (ObjectUtils.isNotEmpty(order.get(Constants.PARAM_PTZ_T))) {
                            m.setPtzT(String.valueOf(order.get(Constants.PARAM_PTZ_T)));
                        }
                        if (ObjectUtils.isNotEmpty(order.get(Constants.PARAM_PTZ_Z))) {
                            m.setPtzZ(String.valueOf(order.get(Constants.PARAM_PTZ_Z)));
                        }
                        //预置位ID
                        if (ObjectUtils.isNotEmpty(order.get(Constants.PARAM_PRESET_ID))) {
                            m.setPresetId(String.valueOf(order.get(Constants.PARAM_PRESET_ID)));
                        }
                        //乡镇名称
                        if(ObjectUtils.isNotEmpty(order.get(Constants.PARAM_TOWN_NAME_FIELD))){
                            m.setTownName(String.valueOf(order.get(Constants.PARAM_TOWN_NAME_FIELD)));
                        }
                        //所属网格
                        if(ObjectUtils.isNotEmpty(order.get(Constants.GRID_NAME))){
                            m.setGridName(String.valueOf(order.get(Constants.GRID_NAME)));
                        }
                        break;
                    }
                }

                LeaderExport leaderExport = new LeaderExport();
                BeanUtils.copyProperties(m, leaderExport);
                if (!CollectionUtils.isEmpty(alramTypeList)) {
                    alramTypeList.forEach(item -> {
                        if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(m.getWarningTypeId())) {
                            leaderExport.setWarningName((String) item.get(Constants.PARAM_TYPENAME));
                        }
                    });
                }
                if (!CollectionUtils.isEmpty(emergencyLevelEnum)) {
                    emergencyLevelEnum.forEach(item -> {
                        if (item.getDictValue().equals(leaderExport.getEmergencyLevel())) {
                            leaderExport.setEmergencyLevelName(item.getDictLabel());
                        }
                    });
                }
                if (!CollectionUtils.isEmpty(emergencyLevelEnum)) {
                    warningSourceEnum.forEach(item -> {
                        if (item.getDictValue().equals(String.valueOf(leaderExport.getWarningSource()))) {
                            leaderExport.setWarningSourceName(item.getDictLabel());
                        }
                    });
                }

                importanceLevelEnum.forEach(item -> {
                    if (item.getDictValue().equals(leaderExport.getImportanceLevel())) {
                        leaderExport.setImportanceLevelName(item.getDictLabel());
                    }
                });
                //匹配经纬度、PTZ值、预置位信息
                WarningOrderListVO warningOrderListVO = new WarningOrderListVO();
                BeanUtils.copyProperties(m, warningOrderListVO);
                WarningOrderExport warningOrderExport = new WarningOrderExport();
                BeanUtils.copyProperties(leaderExport, warningOrderExport);
                warningOrderExport = handleExportParamsNew(warningOrderListVO, videoPresetList, deviceSiteVOList, iotDeviceSiteVOList, orgInfoList, warningOrderExport);
                BeanUtils.copyProperties(warningOrderExport, leaderExport);
                exportList.add(leaderExport);
            }
        }
        warningOrderUtil.leaderHandleDeviceLabel(exportList,loginUser);
        return exportList;
    }

    /**
     * 导出回收站列表
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = "db1",deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,12",areaPro = "dataTable.province_code"
            ,areaCity = "dataTable.city_code",areaCode = "dataTable.county_code")
    public List<AlarmInterceptExport> exportInterceptList(WarningInterceptDTO dto) {

        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //来源：0铁塔 2租户
        dto.setSource(loginUser.getUser().getUserType());
        // 设置导出标识
        dto.setExport(true);
        //跨库获取页面查询设备信息关联数据
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        alarmOrderDTO.setDeviceLabelId(dto.getDeviceLabelId());
        alarmOrderDTO.setDeviceLabelIdList(dto.getDeviceLabelIdList());
        alarmOrderDTO.setOrgId(dto.getOrgId());
        alarmOrderDTO.setOrgIdList(dto.getOrgIdList());
        alarmOrderDTO.setModelId(dto.getModelId());
        alarmOrderDTO.setTenantId(loginUser.getUser().getTenantId());
        alarmOrderDTO.setSysUser(loginUser.getUser());
        //处理传入的组织条件
        handleOrgIdList(alarmOrderDTO);
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        Function<AlarmOrderDTO,List<AlarmOrderByDeviceDTO>> fun1= this::getAlarmOrderByDeviceDTOS;
        paramMap.put(fun1,alarmOrderDTO);
        //告警类型标签
        Param param = new Param();
        AlarmOrderDTO orderDTO = new AlarmOrderDTO();
        BeanUtils.copyProperties(dto,orderDTO);
        param.setDto(orderDTO);
        param.setInfo(null);
        param.setUser(loginUser);
        Function<Param, Param> fun2= this::getTableDataInfo;
        paramMap.put(fun2,param);
        //告警标签
        Function<AlarmOrderDTO,List<AlarmLableRelDTO>> fun3= this:: getAlarmLableRelDTOS;
        paramMap.put(fun3,orderDTO);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc, f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_INTERCEPT);
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
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(alarmOrderDTO.getDevList())
                && (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(dto.getModelId()))) {
            return new ArrayList<>();
        }
        dto.setDeviceCodeList(alarmOrderDTO.getDeviceCodeList());
        dto.setSatelliteList(alarmOrderDTO.getSatelliteList());
        dto.setDevList(alarmOrderDTO.getDevList());
        IntercepiResultVo result = interceptService.getInterceptList(dto);
        List<InterceptVO> resultList = result.getResultList();
        List<AlarmInterceptExport> exportList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            //告警类型
            List<Map> alarmTypeList = getAlarmTypeList();
            //重要等级
            List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(Constants.ALARM_LEVEL).getData();
            //告警来源
            List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
            // AI告警类型（noticeResource）
            List<SysDictDataResponse> noticeResoureEnum = getEnumList(Constants.NOTICE_RESOURCE);
            // 获取 告警压制原因字段
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.DICT_TYPE_ALARM_PRESSING_REASON);

            List<TenantDeviceRelDTO> tenantDeviceRelDTOList = new ArrayList<>();

            //组装description
            Map<String, String> aiDescMap = getAiAlgorithmConfigDescMap(
                    "exportInterceptList", resultList,
                    "warningOrderId", "warningSource", "taskId", "tenantId",
                    "deviceCode", "channelCode", "algorithmCode");
            for (OrderInfoVO orderInfoVO : resultList) {
                if (WARNING_SOURCE_1.equals(orderInfoVO.getWarningSource())) {
                    //组装AI告警description字段
                    orderInfoVO.setDescription(aiDescMap.getOrDefault(orderInfoVO.getWarningOrderId(), ""));
                }
                TenantDeviceRelDTO deviceRelDTO = new TenantDeviceRelDTO();
                deviceRelDTO.setDeviceCode(orderInfoVO.getDeviceCode());
                deviceRelDTO.setChannelCode(orderInfoVO.getChannelCode());
                deviceRelDTO.setTenantId(orderInfoVO.getTenantId());
                tenantDeviceRelDTOList.add(deviceRelDTO);
            }
            List<TenantDeviceEventRelVO> tenantDeviceEventRelVOList = deviceRepository.getDeviceRel(tenantDeviceRelDTOList);
            // 告警等级名称
            List<SysDictDataResponse> emergencyLevelEnum = getEnumList(Constants.EMERGENCY_LEVEL);

            //获取预置位ID、摄像机编码集合（去重）
            List<String> presetIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getPresetId()))
                    .map(item -> item.getPresetId()).distinct().collect(Collectors.toList());
            List<String> deviceCodeList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getDeviceCode()))
                    .map(item -> item.getDeviceCode()).distinct().collect(Collectors.toList());
            List<VideoPresetDO> videoPresetList = null;
            List<DeviceSiteVO> deviceSiteVOList = null;
            List<DeviceSiteVO> iotDeviceSiteVOList = null;
            //获取预置位名称
            if (!CollectionUtils.isEmpty(presetIdList)) {
                videoPresetList =deviceRepository.selectVideoPreById(presetIdList,deviceCodeList);
            }
            //v1.7.00 获取组织信息
            List<OrganInfoReq> paramList = resultList.stream().map(item -> {
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
            List<Map<String, String>> orgInfoList = iTradeWarningOrderService.getOrgInfoList(paramList);
            //增加摄像机地址，转义PTZ值
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                deviceSiteVOList = deviceRepository.queryDevice(deviceCodeList);
                //物联告警
                iotDeviceSiteVOList = deviceRepository.queryIotDevice(deviceCodeList);
            }
            for (OrderInfoVO vo : resultList) {
                AlarmInterceptExport export = new AlarmInterceptExport();
                BeanUtils.copyProperties(vo, export);
                alarmTypeList.forEach(item -> {
                    if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(vo.getWarningTypeId())) {
                        export.setWarningTypeName((String) item.get(Constants.PARAM_TYPENAME));
                    }
                });

                //设置告警压制原因
                if (suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                    suppressDictData.getData().forEach(e -> {
                        if (org.springframework.util.ObjectUtils.nullSafeEquals(vo.getIntercept(), e.getDictValue())) {
                            export.setInterceptName(e.getDictLabel());
                        }
                    });
                }

                importanceLevelEnum.forEach(item -> {
                    if (item.getDictValue().equals(vo.getImportanceLevel())) {
                        export.setImportanceLevelName(item.getDictLabel());
                    }
                });

                warningSourceEnum.forEach(item -> {
                    if (item.getDictValue().equals(String.valueOf(vo.getWarningSource()))) {
                        export.setWarningSourceName(item.getDictLabel());
                    }
                });
                if (!CollectionUtils.isEmpty(noticeResoureEnum)) {
                    noticeResoureEnum.forEach(item -> {
                        if (item.getDictValue().equals(vo.getNoticeResource())) {
                            export.setNoticeResourceName(item.getDictLabel());
                        }
                    });
                }

                if (!CollectionUtils.isEmpty(tenantDeviceEventRelVOList)) {
                    for (TenantDeviceEventRelVO tenantDeviceEventRelVO : tenantDeviceEventRelVOList) {
                        if (vo.getDeviceCode().equals(tenantDeviceEventRelVO.getDeviceCode())) {
                            export.setDeviceName(tenantDeviceEventRelVO.getDeviceName());
                            if("10".equals(vo.getWarningSource())){
                                /*export.setChannelCode(tenantDeviceEventRelVO.getChannelCode());
                                export.setChannelName(tenantDeviceEventRelVO.getChannelName());*/
                                export.setDeviceName(tenantDeviceEventRelVO.getDeviceName());
                            }
                        }
                        if (vo.getChannelCode().equals(tenantDeviceEventRelVO.getChannelCode())) {
                            export.setChannelName(tenantDeviceEventRelVO.getChannelName());
                        }
                    }
                }
                //事件描述
                if(StringUtils.isEmpty(vo.getDescription()) || Constants.ONE.equals(vo.getWarningSource())){
                    String description = this.splicingEventDescription(vo.getDescription(),vo.getExtraField(), vo.getWarningSource());
                    export.setDescription(description);
                }
                //匹配经纬度、PTZ值、预置位信息
                WarningOrderListVO warningOrderListVO = new WarningOrderListVO();
                BeanUtils.copyProperties(vo, warningOrderListVO);
                warningOrderListVO.setOrderId(vo.getWarningOrderId());
                WarningOrderExport warningOrderExport = new WarningOrderExport();
                BeanUtils.copyProperties(export, warningOrderExport);
                warningOrderExport = handleExportParamsNew(warningOrderListVO, videoPresetList, deviceSiteVOList, iotDeviceSiteVOList, orgInfoList, warningOrderExport);
                BeanUtils.copyProperties(warningOrderExport, export);
                exportList.add(export);
            }
        }
        warningOrderUtil.interceptHandleDeviceLabel(exportList,loginUser);
        return exportList;
    }

    /**
     * 获取经纬度、预置位、PTZ信息
     * @param m
     * @param videoPresetList
     * @param deviceSiteVOList
     * @param iotDeviceSiteVOList
     * @param orgInfoList
     * @param warningOrderExport
     * @return
     */
    private WarningOrderExport handleExportParamsNew(WarningOrderListVO m, List<VideoPresetDO> videoPresetList, List<DeviceSiteVO> deviceSiteVOList,
                                                     List<DeviceSiteVO> iotDeviceSiteVOList, List<Map<String, String>> orgInfoList, WarningOrderExport warningOrderExport) {
        // 经纬度
        StringBuilder longitudeAndLatitude = new StringBuilder();
        String longitude;
        String latitude;
        if (!StringUtils.isEmpty(m.getLongitude()) && !StringUtils.isEmpty(m.getLatitude())) {
            //截取小数点后6位
            //判断是否包含小数点、小数点后位数
            if (m.getLongitude().indexOf(".") > Constants.ZERO_NUM && m.getLongitude().substring(m.getLongitude().indexOf("."), m.getLongitude().length()).length() > 7) {
                longitude = m.getLongitude().substring(0, (m.getLongitude().indexOf(".") + 7));
            } else {
                longitude = m.getLongitude();
            }
            if (m.getLatitude().indexOf(".") > Constants.ZERO_NUM && m.getLatitude().substring(m.getLatitude().indexOf("."), m.getLatitude().length()).length() > 7) {
                latitude = m.getLatitude().substring(0, (m.getLatitude().indexOf(".") + 7));
            } else {
                latitude = m.getLatitude();
            }
            longitudeAndLatitude.append(longitude).append(Constants.COMMA).append(latitude);
        }
        warningOrderExport.setLongitudeAndLatitude(longitudeAndLatitude.toString());
        //获取预置位名称
        if (!CollectionUtils.isEmpty(videoPresetList)) {
            videoPresetList.forEach(videoPresetDO -> {
                if (!StringUtils.isEmpty(m.getPresetId())) {
                    if (m.getPresetId().equals(String.valueOf(videoPresetDO.getPresetId()))) {
                        warningOrderExport.setPresetName(videoPresetDO.getPresetName());
                    }
                }
            });
        }
        //v1.7.00 展示所属组织
        if (!CollectionUtils.isEmpty(orgInfoList)) {
            for (Map<String, String> map : orgInfoList) {
                String orderId = m.getOrderId();
                if (map.containsKey(orderId)) {
                    warningOrderExport.setOrgName(map.get(orderId));
                    break;
                }
            }
        }
        //增加摄像机地址，转义PTZ值
        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
            deviceSiteVOList.forEach(deviceSiteVO -> {
                m.setModelId(deviceSiteVO.getModelId());
                m.setFocusRange(deviceSiteVO.getFocusRange());
                m.setVertiDownNeg(deviceSiteVO.getVertiDownNeg());
            });
        }
        if (WarningSourceEnum.IOT_ALARM.getTypeValue().equals(m.getWarningSource())) {
            if (!CollectionUtils.isEmpty(iotDeviceSiteVOList)) {
                iotDeviceSiteVOList.forEach(deviceSiteVO -> {
                    m.setModelId(deviceSiteVO.getModelId());
                    m.setFocusRange(deviceSiteVO.getFocusRange());
                    m.setVertiDownNeg(deviceSiteVO.getVertiDownNeg());
                });
            }
        }
        //转义PTZ
        if (!StringUtils.isEmpty(m.getPtzP()) && !StringUtils.isEmpty(m.getPtzT()) && !StringUtils.isEmpty(m.getPtzZ())){
            try{
                //获取转义前原始的PTZ
                warningOrderExport.setPtzInitial("P:"+m.getPtzP() + " T:"+m.getPtzT() + " Z:"+m.getPtzZ());
                DeviceModelVO deviceModel = new DeviceModelVO();
                //获取摄像设备厂家编码
                deviceModel.setMfgrCode(m.getModelId());
                //获取摄像机类型的焦距范围
                if (Constants.MODELID_DAHUA.equals(m.getModelId()) || Constants.MODELID_DHUA.equals(m.getModelId())){
                    deviceModel.setFocusRange(m.getFocusRange());
                }
                //摄像机水平向下是否为负
                deviceModel.setVertiDownNeg(m.getVertiDownNeg());

                String pNewValue = LongLatAngleUtils.paramTrans(m.getPtzP(), deviceModel, "P");
                String tNewValue = LongLatAngleUtils.paramTrans(m.getPtzT(), deviceModel, "T");
                String zNewValue = LongLatAngleUtils.paramTrans(m.getPtzZ(), deviceModel, "Z");
                //获取转义后的PTZ
                warningOrderExport.setPtzTrans("P:"+pNewValue + " T:"+tNewValue + " Z:"+zNewValue);
            }catch(Exception e){
                log.info("告警工单ptz转义异常，异常：{}", e);
            }
        }
        warningOrderExport.setIsSecondJudge(getAlarmSecondJudgeNameByCode(warningOrderExport.getIsSecondJudge()));
        warningOrderExport.setSecondJudgeResult(getAlarmSecondJudgeResultNameByCode(warningOrderExport.getSecondJudgeResult()));
        return warningOrderExport;
    }

    public String getAlarmSecondJudgeNameByCode(String code){
        for (EnumUtil.AlarmSecondJudge alarmSecondJudge : EnumUtil.AlarmSecondJudge.values()) {
            if (alarmSecondJudge.getCode().equals(code)) {
                return alarmSecondJudge.getName();
            }
        }
        return "";
    }

    public String getAlarmSecondJudgeResultNameByCode(String code){
        for (EnumUtil.AlarmSecondJudgeResult alarmSecondJudgeResult : EnumUtil.AlarmSecondJudgeResult.values()) {
            if (alarmSecondJudgeResult.getCode().equals(code)) {
                return alarmSecondJudgeResult.getName();
            }
        }
        return "";
    }

    /**
     * 根据枚举类型获取枚举列表
     *
     * @param enumTypeId
     * @return
     */
    @Override
    public List<SysDictDataResponse> getEnumList(int enumTypeId) {
        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(String.valueOf(enumTypeId));
        List<SysDictDataResponse> dictList = res.getData();
        return dictList;
    }

    /**
     * 判断是否有编辑权限 1:可编辑   0：不可编辑
     * @param isAdmin
     * @param userId
     * @param vo
     * @return
     */
    @Override
    public int ifEditHandle(boolean isAdmin, String userId, WarningOrderListVO vo,String flowLinkIds) {
        if (isAdmin) {
            //编辑权限
            return 1;
        } else if (!StringUtils.isEmpty(vo.getProcessId()) && vo.getProcessId().contains(userId)) {
            return 1;
        } else if (!StringUtils.isEmpty(vo.getFlowId()) && !StringUtils.isEmpty(flowLinkIds)
                && flowLinkIds.contains(vo.getFlowId()+"-"+vo.getLinkId()) && Constants.BEFORE_DISPATCH_FLAG_0.equals(vo.getBeforeDispatchFlag())) {
            //根据用户id和角色id查询流程环节信息
            // 环节和环节配置id关联上即有编辑权限
            return 1;
        }
        return 0;
    }

    /**
     * 功能描述:
     * 处理工单列表入参
     *
     * @param:
     * @return:
     * @auther: yuexuebin
     * @date: 2021/11/22 22:39
     */
    public void handleOrderReq(AlarmOrderDTO dto, String flowLinkIds) {
        //兼容查询误报，重复告警
        //2022-01-13 工作台-事件管理-告警事件展示全量告警事件，包括重复告警、误报的事件。
        //app查询代表数据过滤掉假警完结、重复告警完结数据
        if ("app".equals(dto.getClienttype())) {
            String orderStatus = dto.getOrderStatus();
            if (!(EnumUtil.FlowNode.MISINFORMATION.getCode().equals(orderStatus) ||
                    EnumUtil.FlowNode.DUPLICATION.getCode().equals(orderStatus))) {
                List<String> notInOrderStatus = dto.getNotInOrderStatus();
                if (CollectionUtils.isEmpty(notInOrderStatus)) {
                    notInOrderStatus = new ArrayList<>();
                    notInOrderStatus.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
                    notInOrderStatus.add(EnumUtil.FlowNode.DUPLICATION.getCode());
                    dto.setNotInOrderStatus(notInOrderStatus);
                }
            }
        }
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if((!CollUtil.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode()) )|| (StringUtil.isNotEmpty(dto.getOrderStatus())&&EnumUtil.FlowNode.END.getCode().contains(dto.getOrderStatus()) )){
            List<String> orderStatusList = new ArrayList<String>();
            if((StringUtil.isNotEmpty(dto.getOrderStatus()))&&(dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))){
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            }else{
                orderStatusList=dto.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            dto.setOrderStatusList(orderStatusList);
        }
        // 已办查询 根据当前用户的用户id和告警表的user_record（逗号拼接）模糊关联
        // 待办查询 根据当前用户的角色、id和环节配置表的处理角色和用户关联，取环节配置id和环节id字段做过滤
        if (Constants.SEARCH_TYPE_1.equals(dto.getSearchType()) && !StringUtils.isEmpty(flowLinkIds)) {
            dto.setFlowLinkIds(flowLinkIds);
            //待办过滤完结数据
            List<String> orderStatuses = new ArrayList<>();
            orderStatuses.add(EnumUtil.FlowNode.END.getCode());
            orderStatuses.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            orderStatuses.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            dto.setNotInOrderStatus(orderStatuses);
        }
    }
    /**
     * 处理传入的组织条件
     * @param dto
     */
    public void handleOrgIdList(AlarmOrderDTO dto){
        if(!CollUtil.isEmpty(dto.getOrgIdList())){
            SysDept dept = new SysDept();
            dept.setDeptIdList(dto.getOrgIdList());
            dept.setTenantId(dto.getSysUser().getTenantId());
            dept.setIndustryCode(dto.getSysUser().getIndustryCode());
            List<String> userIds = sysDeptDeviceMapper.getUserNameByDeptId(dept);
            String userIdsString = "";
            if (userIds != null && !userIds.isEmpty()) {
                userIdsString = String.join(Constants.COMMA, userIds);
            }
            dto.setOrgUserListString(userIdsString);
            List<SysDeptDeviceVO> cameras = sysDeptDeviceMapper.getCameras(dept);
            List<SysDeptDeviceVO> sensors = sysDeptDeviceMapper.getSensors(dept);
            List<SysDeptDeviceVO> satellites = sysDeptDeviceMapper.getSatellites(dept);
            List<SysDeptDeviceVO> uavs = sysDeptDeviceMapper.getUavs(dept);
            // 雷达走雷达设备的省市区权限
            List<SysDeptDeviceVO> radars = sysDeptDeviceMapper.getRaders(dept);
            List<String> deviceCodes = new ArrayList<>();
            List<SysDeptDeviceVO> satelliteList = new ArrayList<>();

            //摄像机、雷达、物联、无人机设备编码集合
            deviceCodes.addAll(cameras.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
            //deviceCodes.addAll(radars.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
            deviceCodes.addAll(sensors.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
            deviceCodes.addAll(uavs.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));

            //卫星区域集合（去重）
            satelliteList.addAll(satellites.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + "-" + o.getCityId()+"-" + o.getCountyId()))), ArrayList::new)));

            if(!CollectionUtils.isEmpty(satelliteList)){
                dto.setSatelliteList(satelliteList);
            }
            if(!CollectionUtils.isEmpty(deviceCodes)){
                dto.setDeviceCodeList(deviceCodes);
            }
            //雷达区域集合（去重）
            List<SysDeptDeviceVO> radarList = new ArrayList<>(radars.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + "-" + o.getCityId() + "-" + o.getCountyId()))), ArrayList::new)));
            if(!CollectionUtils.isEmpty(radarList)){
                dto.setRadarList(radarList);
            }
        }
    }

    public void jumpChcek(AlarmOrderDTO dto,List<WarningOrderListVO> queryResult,Map<String, String> allUser) {
        //查询研判人
        if (dto.isExport() && !CollectionUtils.isEmpty(queryResult)) {
            List<String> orderIdList = queryResult.stream().filter(alarmVO -> !StringUtils.isEmpty(alarmVO.getOrderId())).map(WarningOrderListVO::getOrderId).collect(Collectors.toList());
            List<FlowRecordVO>  flowRecordVOS = warningOrderMapper.queryAlarmOrderFlowRecord(dto.getFlowTableName(),orderIdList);
            //获取处理人ID集合
            List<String> userIdList = new ArrayList<>();
            queryResult.forEach(warningOrderListVO -> {
                if(!StringUtils.isEmpty(warningOrderListVO.getProcessId())){
                    String[] processIdList = warningOrderListVO.getProcessId().split(Constants.COMMA);
                    for (String userId : processIdList) {
                        userIdList.add(userId);
                    }
                }
            });
            //获取环节处理人姓名
            List<UserInfoDto> processUserAllList = null;
            if(!CollectionUtils.isEmpty(userIdList)){
                processUserAllList = sysUserMapper.queryUserListInfoByUserNameList(userIdList);
            }
            if (!CollectionUtils.isEmpty(queryResult)) {
                List<UserInfoDto> finalProcessUserAllList = processUserAllList;
                queryResult.forEach(item -> {
                    flowRecordVOS.forEach(item2 -> {
                        if (item.getOrderId().equals(item2.getOrderId()) && !StringUtils.isEmpty(item2.getRealLinkType())) {
                            handlingInstructions(item,item2,allUser);
                        }
                    });
                    //导出-当前环节处理人
                    if (!StringUtils.isEmpty(item.getProcessId())) {
                        String[] processIdList = item.getProcessId().split(Constants.COMMA);
                        List<String> userNameList = new ArrayList<>();
                        for (String userId : processIdList) {
                            finalProcessUserAllList.forEach(processUserDto -> {
                                if(userId.equals(processUserDto.getUserId())){
                                    userNameList.add(processUserDto.getNickName());
                                }
                            });
                        }
                        item.setProcessName(String.join(",",userNameList));
                    }
                });
            }
        }
    }

    /**
     * 导出办理说明拼接
     * */
    public void handlingInstructions (WarningOrderListVO item,FlowRecordVO item2,Map<String, String> allUser) {
        //各环节处理状态
        String status = "";
        String remark = "";
        String userName = "";
        userName = allUser.get(item2.getProcessId());
        //环节类型处理状态转派,回退,完成,未完成判断
        if (!Constants.SIX.equals(item2.getRealLinkType()) && !Constants.SEVEN.equals(item2.getRealLinkType()) && !Constants.ZERO.equals(item2.getRealLinkType())){
            if (Constants.ONE.equals(item2.getEnableAcceptanceMode())){
                status = Constants.PENDING_ACCEPTANCE;
            } else if (Constants.TWO.equals(item2.getEnableAcceptanceMode())){
                status = Constants.ACCEPTANCE_COMPLETED;
            }else if (Constants.ONE.equals(item2.getFlowLinkType())){
                status = Constants.REASSIGNMENT;
            }else if (Constants.TWO.equals(item2.getFlowLinkType())){
                status = Constants.FALLBACK;
            }else {
                if (Constants.ZERO.equals(item2.getIfCurrentLink())){
                    status=Constants.COMPLETE;
                }else {
                    status=Constants.NOCOMPLETE;
                }
            }
        }
        //研判环节类型处理状态误报，真警，重复告警判断
        if (Constants.ONE.equals(item2.getRealLinkType())){
            //处理研判人 研判人名称
            item.setCheckMan(userName);
            item.setCheckManUserName(item2.getProcessName());
            if (Constants.ZERO.equals(item2.getFlowLinkType())){
                if (Constants.ZERO.equals(item2.getNextLinkId())){
                    status = Constants.FALSEALARM;
                }else if (Constants.SEVEN.equals(item2.getNextLinkId())){
                    status = Constants.REPEATEDALARM;
                }else if (!StringUtils.isEmpty(item2.getNextLinkId()) && !Constants.SEVEN.equals(item2.getNextLinkId()) && (!Constants.SIX.equals(item2.getNextLinkId()) || (Constants.SIX.equals(item2.getNextLinkId()) && !Constants.CLOSE_ALARM.equals(item2.getRemark())) ) && !Constants.ZERO.equals(item2.getNextLinkId())){
                    status = Constants.TRUEPOLICE;
                }
            }
        }
        //拼接办理说明
        StringBuilder builder = new StringBuilder();
        if (Constants.SIX.equals(item2.getRealLinkType()) || Constants.SEVEN.equals(item2.getRealLinkType()) || Constants.ZERO.equals(item2.getRealLinkType())){
            //完结环节拼接
            remark =  builder.append(item2.getFlowNodeTime()).append(" ").append(Constants.CLOSED).toString();
        }else {
            remark = builder.append(item2.getFlowNodeTime()).append(" ").append(item2.getLinkName()).append("-").append(status).append("\r\n").append(Constants.HANDLEDBY).append(userName==null?"":userName).append("\r\n").append(Constants.HANDLINGINSTRUCTIONS).append(item2.getRemark()==null?"":item2.getRemark()).append("\r\n").toString();
        }
        if (item.getCheckRemark()==null)item.setCheckRemark("");
        item.setCheckRemark(item.getCheckRemark()+remark);

    }

    //查询所有用户
    public Map<String,String> selectAllUser () {
        SysUser user = new SysUser();
        List<SysUser> userList = sysUserMapper.selectUser(user);
        Map<String, String> userMap = userList.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getNickName, (key1, key2) -> key2));
        return userMap;
    }

    /**
     * 导出办理说明拼接
     * */
    public void handlingInstructionsMap (Map item,FlowRecordVO item2,Map<String, String> allUser) {
        //各环节处理状态
        String status = "";
        String remark = "";
        String userName = "";
        userName = allUser.get(item2.getProcessId());
        //环节类型处理状态转派,回退,完成,未完成判断
        if (!Constants.SIX.equals(item2.getRealLinkType()) && !Constants.SEVEN.equals(item2.getRealLinkType()) && !Constants.ZERO.equals(item2.getRealLinkType())){
            if (Constants.ONE.equals(item2.getEnableAcceptanceMode())){
                status = Constants.PENDING_ACCEPTANCE;
            }else if (Constants.TWO.equals(item2.getEnableAcceptanceMode())){
                status = Constants.ACCEPTANCE_COMPLETED;
            }else if (Constants.ONE.equals(item2.getFlowLinkType())){
                status = Constants.REASSIGNMENT;
            }else if (Constants.TWO.equals(item2.getFlowLinkType())){
                status = Constants.FALLBACK;
            }else {
                if (Constants.ZERO.equals(item2.getIfCurrentLink())){
                    status=Constants.COMPLETE;
                }else {
                    status=Constants.NOCOMPLETE;
                }
            }
        }
        //研判环节类型处理状态误报，真警，重复告警判断
        if (Constants.ONE.equals(item2.getRealLinkType())){
            item.put(Constants.RES_MODIFIER_DATE,item2.getFlowEndTime());
            item.put(Constants.RES_PROCESS_NAME,userName);
            item.put(Constants.RES_PROCESS_USER_NAME,item2.getProcessName());
            if (Constants.ZERO.equals(item2.getFlowLinkType())){
                if (Constants.ZERO.equals(item2.getNextLinkId())){
                    status = Constants.FALSEALARM;
                }else if (Constants.SEVEN.equals(item2.getNextLinkId())){
                    status = Constants.REPEATEDALARM;
                }else if (!StringUtils.isEmpty(item2.getNextLinkId()) && !Constants.SEVEN.equals(item2.getNextLinkId()) && (!Constants.SIX.equals(item2.getNextLinkId()) || (Constants.SIX.equals(item2.getNextLinkId()) && !Constants.CLOSE_ALARM.equals(item2.getRemark())) ) && !Constants.ZERO.equals(item2.getNextLinkId())){
                    status = Constants.TRUEPOLICE;
                }
            }
        }
        //拼接办理说明
        StringBuilder builder = new StringBuilder();
        if (Constants.SIX.equals(item2.getRealLinkType()) || Constants.SEVEN.equals(item2.getRealLinkType()) || Constants.ZERO.equals(item2.getRealLinkType())){
            //完结环节拼接
            remark =  builder.append(item2.getFlowNodeTime()).append(" ").append(Constants.CLOSED).toString();
        }else {
            remark = builder.append(item2.getFlowNodeTime()).append(" ").append(item2.getLinkName()).append("-").append(status).append("\r\n").append(Constants.HANDLEDBY).append(userName==null?"":userName).append("\r\n").append(Constants.HANDLINGINSTRUCTIONS).append(item2.getRemark()==null?"":item2.getRemark()).append("\r\n").toString();
        }
        if (item.get(Constants.REMARK)==null) item.put(Constants.REMARK,"");
        item.put(Constants.REMARK,item.get(Constants.REMARK)+remark);
    }

    @Override
    public void resultHandle(List<WarningOrderListVO> resultList, AlarmOrderDTO alarmOrderListReq, String userId, String flowLinkIds) {
        //返回领导批示数量
        List<OrderLeaderNumDTO> auditNum = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            auditNum = warningOrderMapper.getLeaderAuditNum(resultList);
        }
        // 查询告警类型名称
        List<Map> alramTypeList = this.getAlarmTypeList();
        // 告警等级名称
        List<SysDictDataResponse> emergencyLevelEnum = this.getEnumList(Constants.EMERGENCY_LEVEL);
        // 告警状态名称
        List<SysDictDataResponse> orderStatusEnum = this.getEnumList(Constants.ORDER_STATUS);
        // AI告警类型名称
        List<SysDictDataResponse> noticeResourceEnum = this.getEnumList(Constants.NOTICE_RESOURCE);

        // 告警来源枚举
        List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();

        // 重要等级
        List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(Constants.ALARM_LEVEL).getData();
        //查询当前用户收藏事件
        AlarmOrderDTO dto = new AlarmOrderDTO();
        dto.setCollectionCreator(userId);
        List<WarningOrderListVO> warningOrderCollectionList = warningOrderCollectionMapper.qryWarningOrderCollection(dto);

        // 处理返回结果集,设置接收时间，是否超期，处理时长,是否可编辑
        for (WarningOrderListVO vo : resultList) {
            //查询返回工单得下个环节是否为多个
            vo.setMoreNextFlag(Constants.ZERO);
            if(!StringUtils.isEmpty(vo.getLinkId()) && !StringUtils.isEmpty(vo.getFlowTemplate())  && !StringUtils.isEmpty(vo.getFlowId())){
                AlarmFlowConfigModel model = new AlarmFlowConfigModel();
                model.setLinkId(Long.valueOf(vo.getLinkId()));
                model.setTemplateId(Long.valueOf(vo.getFlowTemplate()));
                model.setTenantId(vo.getTenantId());
                model.setId(Long.valueOf(vo.getFlowId()));
                model.setWarningSource(vo.getWarningSource());
                List<FlowModel> flowModelList = flowLinkService.getFlowLink(model);
                if(!CollectionUtils.isEmpty(flowModelList)){
                    if(!StringUtils.isEmpty(flowModelList.get(0).getNextLink()) && flowModelList.get(0).getNextLink().contains(",")){
                        vo.setMoreNextFlag(Constants.ONE);
                    }
                }
            }
            vo.setReciveTime(vo.getCreateDate());
            vo.setIfTimeout(0);
            if (Objects.nonNull(vo.getFlowNodeTime()) && !orderStatusList.contains(vo.getOrderStatus())) {
                vo.setDuration(DateUtils.getTimeDifference(new Date(), vo.getFlowNodeTime()));
                // timeLimit 为-1 表示不超期
                if (Objects.nonNull(vo.getTimeLimit())
                        && !EnumUtil.FlowNode.END.getCode().equals(vo.getOrderStatus())
                        && vo.getTimeLimit() != -1) {
                    vo.setIfTimeout(DateUtils.checkIfTimeOut(vo.getTimeLimit(), vo.getFlowNodeTime()));
                    if (vo.getIfTimeout() == 0) {
                        // 未超期
                        vo.setShowTime("剩余" + DateUtils.getTimeDifference(DateUtils.subtractTimeMinutes(vo.getFlowNodeTime(), vo.getTimeLimit()), new Date()));
                    } else {
                        // 超期
                        vo.setShowTime("超期" + DateUtils.getTimeDifference(new Date(), DateUtils.subtractTimeMinutes(vo.getFlowNodeTime(), vo.getTimeLimit())));
                    }
                }
            }
            vo.setIfEdit(ifEditHandle(alarmOrderListReq.isAdmin(), userId, vo,flowLinkIds));

            auditNum.forEach(item -> {
                if (item.getOrderId().equals(vo.getOrderId())) {
                    vo.setLeaderNum(item.getLeaderNum());
                }
            });
            // 设置图标,告警名称，工单环境名称
            alramTypeList.forEach(item -> {
                if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(vo.getWarningTypeId())) {
                    vo.setIcon(String.valueOf(item.get(Constants.PARAM_ICON)));
                    vo.setWarningTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
                }
            });
            emergencyLevelEnum.forEach(item -> {
                if (item.getDictValue().equals(vo.getEmergencyLevel())) {
                    vo.setEmergencyLevelName(item.getDictLabel());
                }
            });
            //如果开启了受理，则设置事件状态为待受理
            if (Constants.ONE.equals(vo.getEnableAcceptanceMode())) {
                vo.setOrderStatusName(Constants.PENDING_ACCEPTANCE);
            } else {
                orderStatusEnum.forEach(item -> {
                    if (item.getDictValue().equals(vo.getOrderStatus())) {
                        vo.setOrderStatusName(item.getDictLabel());
                    }
                });
            }
            if(EnumUtil.FlowNode.MISINFORMATION.getCode().equals(vo.getOrderStatus())
                    || EnumUtil.FlowNode.DUPLICATION.getCode().equals(vo.getOrderStatus())
                    || EnumUtil.FlowNode.END.getCode().equals(vo.getOrderStatus())){
                vo.setOrderStatusName(EnumUtil.FlowNode.END.getName());
            }
            noticeResourceEnum.forEach(item -> {
                if (item.getDictValue().equals(vo.getNoticeResource())) {
                    vo.setNoticeResourceName(item.getDictLabel());
                }
            });
            importanceLevelEnum.forEach(item -> {
                if (item.getDictValue().equals(vo.getImportanceLevel())) {
                    vo.setImportanceLevelName(item.getDictLabel());
                }
            });

            // 20211127 zzh 处理图片，多张图片取第一张
            String imgUrl = vo.getImgUrl();
            if (!StringUtils.isEmpty(imgUrl) && imgUrl.contains(Constants.COMMA)) {
                vo.setImgUrl(imgUrl.split(Constants.COMMA)[0]);
            }

            String imgUrlIcon = vo.getFileImgUrlIcon();
            // 处理图片，多张图片取第一张
            if (!StringUtils.isEmpty(imgUrlIcon) && imgUrlIcon.contains(Constants.COMMA)) {
                vo.setFileImgUrlIcon(String.valueOf(vo.getFileImgUrlIcon().split(Constants.COMMA)[0]));
            }

            //置信度 取最大置信度展示  -- 新增综合置信度，若存在综合置信度则展示综合置信度
            if (!StringUtils.isEmpty(vo.getCompConfidenceLevel())) {
                vo.setConfidenceLevel(vo.getCompConfidenceLevel());
            }
            else{
                String confidenceLevel = vo.getConfidenceLevel();
                if(!StringUtils.isEmpty(confidenceLevel)){
                    String[] confidenceLevelList = confidenceLevel.split(Constants.COMMA);
                    String confidenceLevelNew =Arrays.stream(confidenceLevelList).max(String::compareTo).get();
                    vo.setConfidenceLevel(confidenceLevelNew);
                }
            }

            warningSourceEnum.forEach(item -> {
                if (item.getDictValue().equals(vo.getWarningSource())) {
                    vo.setWarningSourceName(item.getDictLabel());
                }
            });

            //执法人信息前面多,去掉首尾多余的逗号
            if (!StringUtils.isEmpty(vo.getProcessName())) {
                String processName = vo.getProcessName();
                processName = processName.startsWith(Constants.COMMA) ? processName.substring(1) : processName;
                processName = processName.endsWith(Constants.COMMA) ? processName.substring(0, processName.length() - 1) : processName;
                vo.setProcessName(processName);
            }

            //行业调用时处理
            if (Constants.YES_FLAG.equals(alarmOrderListReq.getIsTrade())) {
                // 获取网格员
                if (!StringUtils.isEmpty(vo.getGridId())) {
                    vo.setKeeperName(deviceRepository.getGridKeeperByGridId(vo.getGridId()));
                }
                List<AlarmOrderByDeviceDTO> devList = alarmOrderListReq.getDevList();
                if (!CollectionUtils.isEmpty(devList)) {
                    for (AlarmOrderByDeviceDTO deviceDTO : devList) {
                        if (!StringUtils.isEmpty(vo.getDeviceCode()) && vo.getDeviceCode().equals(deviceDTO.getDeviceCode())) {
                            vo.setLocation(deviceDTO.getLocation());
                            vo.setModelCode(deviceDTO.getModelCode());
                            vo.setHeight(deviceDTO.getHeight());
                            break;
                        }
                    }
                }
            }
            //是否收藏
            vo.setIsCollection(Constants.ZERO);
            for(WarningOrderListVO item : warningOrderCollectionList){
                if(item.getOrderId().equals(vo.getOrderId())){
                    vo.setIsCollection(Constants.ONE);
                    break;
                }
            }
            //v1.6.50 根据扩展字段内容拼接事件描述
            if(StringUtils.isEmpty(vo.getDescription()) || Constants.ONE.equals(vo.getWarningSource())){
                String description = this.splicingEventDescription(vo.getDescription(),vo.getExtraField(), vo.getWarningSource());
                vo.setDescription(description);
            }
        }
    }

    /**
     * 导出-拼接事件描述
     * @param extraField
     * @param warningSource
     * @return
     */
    private String splicingEventDescription(String alarmDescription,String extraField, String warningSource) {
        //事件描述（1-AI、4-摄像机、8-物联、10-雷达展示透传字段extraField的值）
        AtomicReference<String> description = new AtomicReference<>("");
        if(!StringUtils.isEmpty(extraField) || Constants.WARNING_SOURCE_1.equals(warningSource)){
            StringBuilder builder = new StringBuilder();
            if (Constants.WARNING_SOURCE_1.equals(warningSource) || Constants.WARNING_SOURCE_4.equals(warningSource)) {
                List<Map<String, Object>> extraFieldMap = JSON.parseObject(extraField, new TypeReference<List<Map<String, Object>>>() {});
                List<String> attrOrder = Arrays.asList(  //创建一个顺序列表
                        Constants.CAR_TYPE,
                        Constants.CAR_NUMBER,
                        Constants.PLATE_COLOR,
                        Constants.OBJECT_ID,
                        Constants.OBJECT_COUNT,
                        Constants.OBJECT_TIME,
                        Constants.ALARM_CAUSE,
                        Constants.VEHICLE_NUMBER,
                        Constants.VEHICLE_TYPE,
                        Constants.VEHICLENUM_COLOR,
                        Constants.VEHICLECOLOR
                );
                if (!CollectionUtils.isEmpty(extraFieldMap)) {
                    extraFieldMap.stream()
                            .findFirst()
                            .map(item1 -> (List<Map<String, String>>) item1.get("attrList"))
                            .orElse(new ArrayList<>())
                            .stream()
                            .sorted(Comparator.comparingInt(attr -> attrOrder.indexOf(attr.get("attrType"))))
                            .forEach(attr -> {
                                String attrType = attr.get("attrType");
                                String attrValue = attr.get("attrValue");
                                String attrRemark = "";
                                switch (attrType) {
                                    case Constants.CAR_TYPE:
                                        attrRemark = Constants.CAR_TYPE_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.CAR_NUMBER:
                                        attrRemark = Constants.CAR_NUMBER_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.PLATE_COLOR:
                                        attrRemark = Constants.PLATE_COLOR_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.OBJECT_ID:
                                        attrRemark = Constants.OBJECT_ID_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.OBJECT_COUNT:
                                        attrRemark = Constants.OBJECT_COUNT_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.OBJECT_TIME:
                                        attrRemark = Constants.OBJECT_TIME_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.ALARM_CAUSE:
                                        attrRemark = Constants.ALARM_CAUSE_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.VEHICLE_NUMBER:
                                        attrRemark = Constants.VEHICLE_NUMBER_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.VEHICLE_TYPE:
                                        attrRemark = Constants.VEHICLE_TYPE_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.VEHICLENUM_COLOR:
                                        attrRemark = Constants.VEHICLE_NUM_COLOR_COLON + attrValue + Constants.CRLF;
                                        break;
                                    case Constants.VEHICLECOLOR:
                                        attrRemark = Constants.VEHICLE_COLOR_COLON + attrValue + Constants.CRLF;
                                        break;
                                    default:
                                        break;
                                }
                                builder.append(attrRemark);
                            });
                    if (!StringUtils.isEmpty(builder.toString()) && Constants.WARNING_SOURCE_1.equals(warningSource)){
                        builder.insert(0,"算法识别结果"+Constants.CRLF);
                    }
                }
                if (Constants.WARNING_SOURCE_1.equals(warningSource) && !StringUtils.isEmpty(alarmDescription)){
                    JSONArray alarmArray = JSON.parseArray(alarmDescription);
                    InfoAlgorithmConfigExportParam params = new InfoAlgorithmConfigExportParam();
                    if (alarmArray != null && !alarmArray.isEmpty()) {
                        params = JSONObject.parseObject(JSON.toJSONString(alarmArray.get(0)), InfoAlgorithmConfigExportParam.class);
                    }
                    if (ObjectUtil.isNotNull(params)){
                        alarmDescription ="";
                        String timeUnit = "";
                        String time = "";
                        String timeInterval = "";
                        String pipeline = "";
                        if (!StringUtils.isEmpty(params.getTimeInterval())){
                            if ("1".equals(params.getTimeIntervalUnit())){
                                timeUnit = "小时";
                            }else if ("2".equals(params.getTimeIntervalUnit())){
                                timeUnit = "分钟";
                            }else if ("3".equals(params.getTimeIntervalUnit())){
                                timeUnit = "秒";
                            }
                            time = "识别频率:"+params.getTimeInterval()+timeUnit+Constants.CRLF;
                        }
                        if (!CollectionUtils.isEmpty(params.getAnalysisPeriod()) && params.getAnalysisPeriod().size()>0){
                            timeInterval = timeInterval+"算法运行时段:";
                            for (InfoAlgorithmConfigExportParam.AnalysisPeriod analysisPeriod : params.getAnalysisPeriod()) {
                                timeInterval = timeInterval+analysisPeriod.getStartTime()+"-"+analysisPeriod.getEndTime()+",";
                            }
                            if (timeInterval.length() > 0){
                                timeInterval = timeInterval.substring(0, timeInterval.length() - 1);
                            }

                            timeInterval = timeInterval+Constants.CRLF;
                        }
                        if (!CollectionUtils.isEmpty(params.getModelConfig()) && params.getModelConfig().size()>0){
                            for (InfoAlgorithmConfigExportParam.ModelConfig modelConfig : params.getModelConfig()) {
                                if (!CollectionUtils.isEmpty(modelConfig.getDictList()) && modelConfig.getDictList().size()>0){
                                    pipeline = pipeline+modelConfig.getName()+":"+ (StringUtils.isEmpty(modelConfig.getSelectValue()) ? modelConfig.getValue() : modelConfig.getSelectValue()) +Constants.CRLF;
                                }else if (Constants.VISUAL_PARAMETERS.equals(modelConfig.getName())){
                                    pipeline = pipeline+modelConfig.getName()+":"+(StringUtils.isEmpty(modelConfig.getSelectValue()) ? modelConfig.getValue() : modelConfig.getSelectValue()) +Constants.CRLF;
                                } else {
                                    pipeline = pipeline+modelConfig.getName()+":"+modelConfig.getValue()+Constants.CRLF;
                                }
                            }
                        }

                        alarmDescription = timeInterval+time+pipeline;
                        if (!StringUtils.isEmpty(alarmDescription)){
                            alarmDescription = "算法配置参数"+Constants.CRLF+alarmDescription;
                        }
                        builder.append(alarmDescription);
                    }

                }
                description.set(builder.toString());
            }
            if(Constants.WARNING_SOURCE_8.equals(warningSource)){
                Map<String, Object> extraFieldMap = JSON.parseObject(extraField, Map.class);
                List<Map<String, Object>> monitorList = JSON.parseObject(extraFieldMap.get("monitors").toString(), new TypeReference<List<Map<String, Object>>>() {});
                monitorList.stream().flatMap(item2 -> ((List<Map<String, String>>) item2.get("newMonitors")).stream())
                        .forEach(monitor -> {
                            String itemCode = monitor.get("itemCode");
                            String itemName = monitor.get("itemName");
                            if(!com.chinatower.common.core.utils.StringUtils.isEmpty(itemCode) && !com.chinatower.common.core.utils.StringUtils.isEmpty(itemName)){
                                String monitorRemark = itemName + "：" + itemCode + Constants.CRLF;
                                builder.append(monitorRemark);
                            }
                        });
                description.set(builder.toString());
            }
            if(Constants.WARNING_SOURCE_10.equals(warningSource)){
                Map<String, Object> extraFieldMap = JSON.parseObject(extraField, Map.class);
                if(extraFieldMap.containsKey(Constants.TARGET_ID)){
                    builder.append(Constants.TARGET_ID_COLON).append(extraFieldMap.get(Constants.TARGET_ID)).append(Constants.CRLF);
                }
                if(extraFieldMap.containsKey(Constants.TARGET_TYPE)){
                    builder.append(Constants.TARGET_TYPE_COLON).append(extraFieldMap.get(Constants.TARGET_TYPE)).append(Constants.CRLF);
                }
                if(extraFieldMap.containsKey(Constants.TARGET_MMSI)){
                    builder.append(Constants.TARGET_MMSI_COLON).append(extraFieldMap.get(Constants.TARGET_MMSI)).append(Constants.CRLF);
                }
                if(extraFieldMap.containsKey(Constants.TARGET_NAME)){
                    builder.append(Constants.TARGET_NAME_COLON).append(extraFieldMap.get(Constants.TARGET_NAME)).append(Constants.CRLF);
                }
                if(extraFieldMap.containsKey(Constants.SPEED)){
                    builder.append(Constants.SPEED_COLON).append(extraFieldMap.get(Constants.SPEED)).append(Constants.CRLF);
                }
                if(extraFieldMap.containsKey(Constants.COURSE)){
                    builder.append(Constants.COURSE_COLON).append(extraFieldMap.get(Constants.COURSE)).append(Constants.CRLF);
                }
                if(extraFieldMap.containsKey(Constants.TARGET_LENGTH)){
                    builder.append(Constants.TARGET_LENGTH_COLON).append(extraFieldMap.get(Constants.TARGET_LENGTH)).append(Constants.CRLF);
                }
                description.set(builder.toString());
            }
        }
        return description.toString();
    }

    @Override
    public void downloadImgsTower(HttpServletResponse response, HttpServletRequest request, AlarmDownloadReq req) throws Exception {

        List<String> tableNames = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getWarningOrderIds())) {
            if(Constants.TWO.equals(req.getOrderIdDownLoadType())){
                //回收站
                tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
            }else if(Constants.FOUR.equals(req.getOrderIdDownLoadType())){
                //运营研判
                tableNames.add(TableConstant.WARN_ALARM_OPERATE);
            }else if(Constants.FIVE.equals(req.getOrderIdDownLoadType())){
                // 获取表名
                for (String warningOrderId : req.getWarningOrderIds()) {
                    String tableName = TableConstant.VM_LOCAL_ALARM_OPERATE + warningOrderId.substring(0, 6);
                    if (!tableNames.contains(tableName)) {
                        tableNames.add(tableName);
                    }
                }
            } else{
                if(Constants.THREE.equals(req.getWarningOrderIds())){
                    tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
                }
                for (String warningOrderId : req.getWarningOrderIds()) {
                    // 获取表名
                    String tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
                    if (!tableNames.contains(tableName)) {
                        tableNames.add(tableName);
                    }
                }
            }
        }
        List<com.chinatower.event.entity.vo.WarningOrderListVO> orderList = new ArrayList<>();
        AlarmOrderDTO dto = new AlarmOrderDTO();
        dto.setWarningOrderIds(req.getWarningOrderIds());
        if(!Constants.FIVE.equals(req.getOrderIdDownLoadType())){
            for (String tableName : tableNames) {
                dto.setTableName(tableName);
                List<com.chinatower.event.entity.vo.WarningOrderListVO> queryResult = new ArrayList<>();
                if(Constants.FOUR.equals(req.getOrderIdDownLoadType())){
                    queryResult = warningOrderMapper.queryOperateByWarningOrderIds(dto);
                }else{
                    queryResult = warningOrderMapper.queryAlarmOrderByWarningOrderIds(dto);
                }
                orderList.addAll(queryResult);
            }
        }else{
            List<com.chinatower.event.entity.vo.WarningOrderListVO> queryResult = new ArrayList<>();
            for (String tableName : tableNames) {
                Query query = new Query().addCriteria(Criteria.where("warning_order_id").in(req.getWarningOrderIds()));

                List<OperateMongo> operateVOList = mongoTemplate.find(query,OperateMongo.class,tableName);
                for (OperateMongo mongo:operateVOList) {
                    com.chinatower.event.entity.vo.WarningOrderListVO vo = new WarningOrderListVO();
                    vo.setFileImgUrlIcon(mongo.getFileImgUrlIcon());
                    vo.setImgUrl(mongo.getFileImgUrlSrc());
                    vo.setFrontAlarmPicUrl(mongo.getFrontAlarmPicUrl());
                    vo.setExtraField(mongo.getExtraField());
                    vo.setOrderId(mongo.getWarningOrderId());
                    vo.setWarningSource(mongo.getWarningSource());
                    queryResult.add(vo);
                }
                orderList.addAll(queryResult);
            }

        }

        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();
        for (com.chinatower.event.entity.vo.WarningOrderListVO vo : orderList) {
            int serNumber = 0;
            //卫星告警图片从透传字段取
            if (Constants.NINE.equals(vo.getWarningSource())) {
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(vo.getExtraField(), SatelliteExtendFieldVo.class);
                if (!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())) {
                    serNumber = this.addAlarmOrderImgDtoList(imgUrlList, extendFieldVo.getRelativePathMir(), vo.getOrderId(), serNumber,vo.getWarningSource());
                }
                /*if (!StringUtils.isEmpty(extendFieldVo.getRelativePathPose())) {
                    serNumber = this.addAlarmOrderImgDtoList(imgUrlList, extendFieldVo.getRelativePathPose(), vo.getOrderId(), serNumber);
                }*/
                serNumber = this.addImgUrls(imgUrlList, vo.getImgUrl(), vo.getOrderId(), serNumber,vo.getWarningSource());
                if (!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())) {
                    serNumber = this.addAlarmOrderImgDtoList(imgUrlList, extendFieldVo.getRelativePathThem(), vo.getOrderId(), serNumber,vo.getWarningSource());
                }
            } else {
                serNumber = this.addImgUrls(imgUrlList, vo.getFileImgUrlIcon(), vo.getOrderId(), serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList, vo.getImgUrl(), vo.getOrderId(), serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList, vo.getFrontAlarmPicUrl(), vo.getOrderId(), serNumber,vo.getWarningSource());
            }
        }
        //原始图片路径集合
        List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //调用obs加密方法获取加密后的路径
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
        for (String key : map.keySet()) {
            for (AlarmOrderImgDto imgDto : imgUrlList) {
                if (key.equals(imgDto.getImgUrl())) {
                    if(!StringUtils.isEmpty(map.get(key))){
                        // 仅加密成功  使用原地址进行处理
                        imgDto.setImgUrl(map.get(key));
                    }
                }
            }
        }
        DownloadUtil.downLoad(imgUrlList, req.getName(), response);
        //return Result.success("");
    }

    private BasicDBList createCriteriaList(Criteria[] criteria) {
        BasicDBList bsonList = new BasicDBList();
        Criteria[] var3 = criteria;
        int var4 = criteria.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Criteria c = var3[var5];
            bsonList.add(c.getCriteriaObject());
        }

        return bsonList;
    }

    @Override
    public void downloadImgs(HttpServletResponse response, HttpServletRequest request, AlarmDownloadReq req) throws Exception {

        List<String> tableNames = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getWarningOrderIds())) {
            if (Constants.TWO.equals(req.getOrderIdDownLoadType())) {
                //回收站
                tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
            } else {
                if (Constants.THREE.equals(req.getOrderIdDownLoadType())) {
                    //回收站
                    tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
                }
                //工单表及我的收藏
                for (String warningOrderId : req.getWarningOrderIds()) {
                    // 获取表名
                    String tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
                    if (!tableNames.contains(tableName)) {
                        tableNames.add(tableName);
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(req.getSearchOrderList())){
            AlarmOrderDTO dto = new AlarmOrderDTO();
            dto.setWarningOrderIds(req.getWarningOrderIds());
            for (String tableName:tableNames) {
                dto.setTableName(tableName);
                List<WarningOrderListVO> queryResult = warningOrderMapper.queryAlarmOrderByWarningOrderIds(dto);
                req.getSearchOrderList().addAll(queryResult);
            }
        }

        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();
        for (WarningOrderListVO vo:req.getSearchOrderList()) {
            int serNumber =0;
            //卫星告警图片从透传字段取
            if(Constants.NINE.equals(vo.getWarningSource())){
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(vo.getExtraField(),SatelliteExtendFieldVo.class);
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathMir(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
                /*if(!StringUtils.isEmpty(extendFieldVo.getRelativePathPose())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathPose(),vo.getOrderId(),serNumber);
                }*/
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathThem(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
            }else{
                serNumber = this.addImgUrls(imgUrlList,vo.getFileImgUrlIcon(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getFrontAlarmPicUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
            }
        }
        //原始图片路径集合
        List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //调用obs加密方法获取加密后的路径
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
        for (String key: map.keySet()) {
            for (AlarmOrderImgDto imgDto:imgUrlList) {
                if (key.equals(imgDto.getImgUrl())) {
                    if(!StringUtils.isEmpty(map.get(key))){
                        // 仅加密成功  使用原地址进行处理
                        imgDto.setImgUrl(map.get(key));
                    }
                }
            }
        }
        DownloadUtil.downLoad(imgUrlList,req.getName(),response);
        //return Result.success("");
    }

    @Override
    public ResponseEntity<byte[]> downloadImgsStream(HttpServletRequest request, AlarmDownloadReq req) {

        List<String> tableNames = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getWarningOrderIds())) {
            if (Constants.TWO.equals(req.getOrderIdDownLoadType())) {
                //回收站
                tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
            } else {
                if (Constants.THREE.equals(req.getOrderIdDownLoadType())) {
                    //回收站
                    tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
                }
                //工单表及我的收藏
                for (String warningOrderId : req.getWarningOrderIds()) {
                    // 获取表名
                    String tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
                    if (!tableNames.contains(tableName)) {
                        tableNames.add(tableName);
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(req.getSearchOrderList())){
            AlarmOrderDTO dto = new AlarmOrderDTO();
            dto.setWarningOrderIds(req.getWarningOrderIds());
            for (String tableName:tableNames) {
                dto.setTableName(tableName);
                List<WarningOrderListVO> queryResult = warningOrderMapper.queryAlarmOrderByWarningOrderIds(dto);
                req.getSearchOrderList().addAll(queryResult);
            }
        }

        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();
        for (WarningOrderListVO vo:req.getSearchOrderList()) {
            int serNumber =0;
            //卫星告警图片从透传字段取
            if(Constants.NINE.equals(vo.getWarningSource())){
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(vo.getExtraField(),SatelliteExtendFieldVo.class);
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathMir(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
                /*if(!StringUtils.isEmpty(extendFieldVo.getRelativePathPose())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathPose(),vo.getOrderId(),serNumber);
                }*/
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathThem(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
            }else{
                serNumber = this.addImgUrls(imgUrlList,vo.getFileImgUrlIcon(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getFrontAlarmPicUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
            }
        }
        //原始图片路径集合
        List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //调用obs加密方法获取加密后的路径
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
        for (String key: map.keySet()) {
            for (AlarmOrderImgDto imgDto:imgUrlList) {
                if (key.equals(imgDto.getImgUrl())) {
                    if(!StringUtils.isEmpty(map.get(key))){
                        // 仅加密成功  使用原地址进行处理
                        imgDto.setImgUrl(map.get(key));
                    }
                }
            }
        }
        return DownloadUtil.downLoadStream(imgUrlList,req.getName());
    }

    @Override
    public HttpServletResponse downloadImgsResponse(HttpServletResponse response, HttpServletRequest request, AlarmDownloadReq req) {

        List<String> tableNames = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getWarningOrderIds())) {
            if (Constants.TWO.equals(req.getOrderIdDownLoadType())) {
                //回收站
                tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
            } else {
                if (Constants.THREE.equals(req.getOrderIdDownLoadType())) {
                    //回收站
                    tableNames.add(TableConstant.WARN_ALARM_INTERCEPT);
                }
                //工单表及我的收藏
                for (String warningOrderId : req.getWarningOrderIds()) {
                    // 获取表名
                    String tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
                    if (!tableNames.contains(tableName)) {
                        tableNames.add(tableName);
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(req.getSearchOrderList())){
            AlarmOrderDTO dto = new AlarmOrderDTO();
            dto.setWarningOrderIds(req.getWarningOrderIds());
            for (String tableName:tableNames) {
                dto.setTableName(tableName);
                List<WarningOrderListVO> queryResult = warningOrderMapper.queryAlarmOrderByWarningOrderIds(dto);
                req.getSearchOrderList().addAll(queryResult);
            }
        }

        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();
        for (WarningOrderListVO vo:req.getSearchOrderList()) {
            int serNumber =0;
            //卫星告警图片从透传字段取
            if(Constants.NINE.equals(vo.getWarningSource())){
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(vo.getExtraField(),SatelliteExtendFieldVo.class);
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathMir(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
                /*if(!StringUtils.isEmpty(extendFieldVo.getRelativePathPose())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathPose(),vo.getOrderId(),serNumber);
                }*/
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathThem(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
            }else{
                serNumber = this.addImgUrls(imgUrlList,vo.getFileImgUrlIcon(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getFrontAlarmPicUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
            }
        }
        //原始图片路径集合
        List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //调用obs加密方法获取加密后的路径
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
        for (String key: map.keySet()) {
            for (AlarmOrderImgDto imgDto:imgUrlList) {
                if (key.equals(imgDto.getImgUrl())) {
                    if(!StringUtils.isEmpty(map.get(key))){
                        // 仅加密成功  使用原地址进行处理
                        imgDto.setImgUrl(map.get(key));
                    }
                }
            }
        }
        return DownloadUtil.downLoadResponse(response,imgUrlList,req.getName());
    }

    @Override
    public void downloadVideosByFfmpeg(HttpServletResponse response, HttpServletRequest request, DownloadVideoReq req) throws Exception {
        // 获取当前的用户
        String tenantName = "";
        if (!StringUtils.isEmpty(req.getName())) {
            tenantName = req.getName();
        } else {
            // req.getName()有就是铁塔视角，没有则自行判断
            LoginUser loginUser = SecurityUtils.getLoginUser();
            // 铁塔视角
            if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())){
                tenantName = "中国铁塔";
            }else {
                // 客户视角判断是否为铁塔切租户
                boolean switchFlag = SecurityUtils.isSwitch();
                //客户视角切换：已切换-sql后面拼接租户ID,未切换-
                if(switchFlag) {
                    //已切换，水印设置为铁塔
                    tenantName = "中国铁塔";
                }else{
                    // 未切换，水印为租户名称
                    tenantName = loginUser.getUser().getTenantInfo().getTenantName();
                }
            }
        }
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(req.getVideoUrls());
        List<String> realUrls = new ArrayList<>();
        int j = 1;
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("目录创建成功！");
            } else {
                log.info("目录创建失败！");
            }
        } else {
            log.info("目录已存在！");
        }
        for (String url:req.getVideoUrls()) {
            String beforPath = path + "/" + req.getWarningOrderId() + "old" + j + ".mp4";
            this.downloadPic(url, beforPath);
            realUrls.add(beforPath);
            j++;
        }
       /* for (String key : map.keySet()) {
            String beforPath = path + "/" + req.getWarningOrderId() + "old" + j + ".mp4";
            if (!StringUtils.isEmpty(map.get(key))) {
                // 仅加密成功  使用加密后地址进行处理
                //realUrls.add(map.get(key));
                this.downloadPic(map.get(key), beforPath);
            } else {
                //无需加密 使用当前地址
                this.downloadPic(key, beforPath);
            }
            realUrls.add(beforPath);
            j++;
        }*/
        int i = 1;
        // 创建 ZIP 输出流
        //将生成的带水印视频加入到zip中并删除所生成视频
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        try {
            WatermarkFormatVo watermarkFormatVo = DownloadUtil.handleWatermarkFormat();
            String fontFilePath = TFF_PATH;
            for (String videoUrl : realUrls) {
                String newFilePath = path + "告警视频-" + req.getWarningOrderId() + "-" + i + ".mp4";
                //this.getVideo(videoUrl,newFilePath,tenantName);
                i++;
               /* String[] command = {"ffmpeg", "-i", videoUrl, "-vf",
                        "drawtext=text='" + tenantName + "':fontfile='" + fontFilePath + "':fontsize=" + fontSize +
                                ":fontcolor=" + fontColor + ":box=1:boxcolor=black@0.5:boxborderw=5:x=10:y=10",
                        newFilePath};

                // 执行命令行
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                pb.start();*/
                // 第二象限+第四象限增加水印
                downloadUtil.waterPrintByGpu(tenantName,videoUrl,newFilePath,null,watermarkFormatVo);
                // 将新文件添加到 ZIP 包中并删除源文件
                File file = new File(newFilePath);
                if (file.exists()) {
                    try {
                        addToZip(file, file.getName(), zos);
                        // 删除原始文件
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("新文件已成功添加到 ZIP 包并被删除。");
                        } else {
                            log.info("无法删除新文件。");
                        }
                    } catch (Exception e) {
                        log.error("将文件添加到ZIP包中异常:" + e);
                    } finally {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                //将原文件删除
                File fileOld = new File(videoUrl);
                if (fileOld.exists()) {
                    try {
                        boolean deletedOld = fileOld.delete();
                        if (deletedOld) {
                            log.info("原文件已并被删除。");
                        } else {
                            log.info("无法删除原文件。");
                        }
                    } catch (Exception e) {
                        log.error("删除源文件异常");
                    } finally {
                        if (fileOld.exists()) {
                            fileOld.delete();
                        }
                    }
                }

            }
        }catch(Exception e){
            log.error("合成视频文件异常"+e);
        }finally {
            zos.flush();
            zos.close();
            for (String videoUrl : realUrls) {
                if(!StringUtils.isEmpty(videoUrl)){
                    //将原文件删除
                    File fileOld = new File(videoUrl);
                    if (fileOld.exists()) {
                        fileOld.delete();
                    }
                }
            }
            log.info("合成视频文件完成");
        }
    }

    @Override
    public void downloadImgsLocal(HttpServletResponse response, HttpServletRequest request, List<LocalImgReq> list) throws Exception {
        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();

        log.info("downloadImgsLocal-list:{}",JSON.toJSONString(list));
        for (LocalImgReq req:list) {
            int serNumber = 0;
            for (String url:req.getPicUrl()) {
                /*String beforUrl =url;
                //调用ofs获取可直接访问路径
                if(url.contains("http") || url.contains("https")){
                    //将属地编码拼接在域名之后
                    int thirdSlashIndex = url.indexOf('/', url.indexOf('/', url.indexOf('/') + 1) + 1);
                    // 拼接字符串，在第三个斜杠之后
                    StringBuilder result = new StringBuilder(url.substring(0, thirdSlashIndex + 1));
                    result.append(req.getPlatformCode()+"/");
                    result.append(url.substring(thirdSlashIndex + 1));
                    url = result.toString();
                }
                log.info("downloadImgsLocal-list-befor:{}",url);
                url = getRealUrl(req.getPlatformCode(),url);
                if(StringUtils.isEmpty(url) || "null".equals(url)){
                    url = beforUrl;
                }*/
                serNumber = this.addImgUrlsLocal(imgUrlList, url, req.getWarningOrderId(), serNumber,req.getWarningSource(),req.getPlatformCode());
            }
        }
        DownloadUtil.downLoad(imgUrlList, Constants.TOWER_NAME, response);
        //return Result.success("");
    }

    @Override
    public void downloadLocalVideosByFfmpeg(HttpServletResponse response, HttpServletRequest request, LocalDownloadVideoReq req)throws Exception  {
        // 获取当前的用户
        String tenantName = "中国铁塔";

        List<String> realUrls = new ArrayList<>();
        int j = 1;
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("目录创建成功！");
            } else {
                log.info("目录创建失败！");
            }
        } else {
            log.info("目录已存在！");
        }
        for (String url : req.getVideoUrls()) {
            String beforPath = path + "/" + req.getWarningOrderId() + "old" + j + ".mp4";
            //调用ofs获取可直接访问路径
            //url = getRealUrl(req.getPlatformCode(),url);
            this.downloadPic(url, beforPath);
            realUrls.add(beforPath);
            j++;
        }
        int i = 1;
        // 创建 ZIP 输出流
        //将生成的带水印视频加入到zip中并删除所生成视频
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        try {
            WatermarkFormatVo watermarkFormatVo = DownloadUtil.handleWatermarkFormat();
            String fontFilePath = TFF_PATH;
            for (String videoUrl : realUrls) {
                String newFilePath = path + "告警视频-" + req.getWarningOrderId() + "-" + i + ".mp4";
                //this.getVideo(videoUrl,newFilePath,tenantName);
                i++;
                // 第二象限+第四象限增加水印
                downloadUtil.waterPrintByGpu(tenantName,videoUrl,newFilePath,"0",watermarkFormatVo);
                // 将新文件添加到 ZIP 包中并删除源文件
                File file = new File(newFilePath);
                if (file.exists()) {
                    try {
                        addToZip(file, file.getName(), zos);
                        // 删除原始文件
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("新文件已成功添加到 ZIP 包并被删除。");
                        } else {
                            log.info("无法删除新文件。");
                        }
                    } catch (Exception e) {
                        log.error("将文件添加到ZIP包中异常:" + e);
                    } finally {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                //将原文件删除
                File fileOld = new File(videoUrl);
                if (fileOld.exists()) {
                    try {
                        boolean deletedOld = fileOld.delete();
                        if (deletedOld) {
                            log.info("原文件已并被删除。");
                        } else {
                            log.info("无法删除原文件。");
                        }
                    } catch (Exception e) {
                        log.error("删除源文件异常");
                    } finally {
                        if (fileOld.exists()) {
                            fileOld.delete();
                        }
                    }
                }

            }
        }catch(Exception e){
            log.error("合成视频文件异常"+e);
        }finally {
            zos.flush();
            zos.close();
            for (String videoUrl : realUrls) {
                if(!StringUtils.isEmpty(videoUrl)){
                    //将原文件删除
                    File fileOld = new File(videoUrl);
                    if (fileOld.exists()) {
                        fileOld.delete();
                    }
                }
            }
            log.info("合成视频文件完成");
        }
    }



    /**
     * 导出告警详情环节信息及附件
     *
     * @param dto
     * @return
     */
    @Override
    public void exportEventInfo(ExportOrderInfoReq dto,MultipartFile file,HttpServletResponse response) throws IOException {
        // 获取程序开始时间
        long startTime = System.currentTimeMillis();
        // 获取资源文件的输入流
        XWPFDocument doc = null;
        InputStream inputStream = null;
        ByteArrayOutputStream bos = null;
        ZipOutputStream zipOut = null;
        try {

            AlarmOrderInfoExportDTO data = exportWarningInfoService.getExportWarningDto(dto);
            log.info("getExportWarningDto返回参数:{}", JSON.toJSONString(data));
            // 读入Word模板
            // 1. 获取模版
            String templateName = "alarm_info_one_template";
            if ("1".equals(data.getWarningSource()) || "1".equals(data.getRealAlarmSource())){
                // 替换AI告警所有表格中的文本
                templateName = "alarm_info_one_template";
            }else if ("2".equals(data.getWarningSource()) || "2".equals(data.getRealAlarmSource())){
                templateName = "alarm_info_two_template";
            }else if ("3".equals(data.getWarningSource()) || "3".equals(data.getRealAlarmSource())){
                templateName = "alarm_info_three_template";
            }else if ("8".equals(data.getWarningSource()) || "8".equals(data.getRealAlarmSource())){
                templateName = "alarm_info_eight_template";
            }else if (("9".equals(data.getWarningSource())|| "9".equals(data.getRealAlarmSource())) && "4110102".equals(data.getWarningTypeId())){
                //矿产
                templateName = "alarm_info_nine_minerals_template";
            }else if (("9".equals(data.getWarningSource())|| "9".equals(data.getRealAlarmSource())) && "4110101".equals(data.getWarningTypeId())){
                //耕地
                templateName = "alarm_info_nine_land_template";
            }else if (("9".equals(data.getWarningSource())|| "9".equals(data.getRealAlarmSource())) && "4110103".equals(data.getWarningTypeId())){
                //非农化
                templateName = "alarm_info_nine_agro_template";
            }else if (("9".equals(data.getWarningSource())|| "9".equals(data.getRealAlarmSource())) && "4110104".equals(data.getWarningTypeId())){
                //非粮化
                templateName = "alarm_info_nine_grain_template";
            }else if ("9".equals(data.getWarningSource()) || "9".equals(data.getRealAlarmSource())){
                templateName = "alarm_info_nine_template";
            }else if ("10".equals(data.getWarningSource()) || "10".equals(data.getRealAlarmSource())){
                templateName = "alarm_info_ten_template";
            }else if ("4".equals(data.getWarningSource()) || "4".equals(data.getRealAlarmSource())){
                templateName = "alarm_info_four_template";
            }else if ("12".equals(data.getWarningSource()) || "12".equals(data.getRealAlarmSource())){
                templateName = "alarm_info_twelve_template";
            }
            SysConfig sysConfig = fileTool.selectConfigByKey(templateName);
            if(ObjectUtil.isNull(sysConfig)){
                throw new CustomException("未找到该系统参数配置："+templateName);
            }
            String fileUrl = fileUrlEncryptUtil.dealObsFileUrl(sysConfig.getConfigValue());
            URL url = new URL(fileUrl);
            // 打开连接并设置超时时间
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000); // 设置连接超时时间为5秒
            connection.setReadTimeout(5000);   // 设置读取超时时间为10秒
            inputStream = connection.getInputStream();

            doc = new XWPFDocument(inputStream);
            boolean isTower = false;
            LoginUser loginUser = SecurityUtils.getLoginUser();
            // 铁塔视角
            if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())){
                isTower = true;
            }
            if ("1".equals(data.getWarningSource()) || "1".equals(data.getRealAlarmSource())){
                // 替换AI告警所有表格中的文本
                replaceTableInfoOne(doc,data,dto,isTower);
            }else if ("2".equals(data.getWarningSource()) || "2".equals(data.getRealAlarmSource())){
                // 替换人工告警所有表格中的文本
                replaceTableInfoTwo(doc,data,dto,isTower);
            }else if ("3".equals(data.getWarningSource()) || "3".equals(data.getRealAlarmSource())){
                // 替换一键告警所有表格中的文本
                replaceTableInfoThree(doc,data,dto,isTower);
            }else if ("8".equals(data.getWarningSource()) || "8".equals(data.getRealAlarmSource())){
                // 替换物联告警所有表格中的文本
                replaceTableInfoEight(doc,data,dto,isTower);
            }else if (("9".equals(data.getWarningSource()) || "9".equals(data.getRealAlarmSource())) && "4110102".equals(data.getWarningTypeId())){
                // 替换卫星告警 - 矿产卫片所有表格中的文本
                replaceTableInfoFourMinerals(doc,data,dto,isTower);
            }else if (("9".equals(data.getWarningSource())|| "9".equals(data.getRealAlarmSource())) && "4110101".equals(data.getWarningTypeId())){
                // 替换卫星告警 - 耕地卫片所有表格中的文本
                replaceTableInfoFourLand(doc,data,dto,isTower);
            }else if (("9".equals(data.getWarningSource())|| "9".equals(data.getRealAlarmSource())) && "4110103".equals(data.getWarningTypeId())){
                // 替换卫星告警 - 非农化所有表格中的文本
                replaceTableInfoFourAgro(doc,data,dto,isTower);
            }else if (("9".equals(data.getWarningSource())|| "9".equals(data.getRealAlarmSource())) && "4110104".equals(data.getWarningTypeId())){
                // 替换卫星告警 - 非粮化所有表格中的文本
                replaceTableInfoFourGrain(doc,data,dto,isTower);
            }else if ("9".equals(data.getWarningSource()) || "9".equals(data.getRealAlarmSource())){
                // 替换卫星告警所有表格中的文本
                replaceTableInfoNine(doc,data,dto,isTower);
            }else if ("10".equals(data.getWarningSource()) || "10".equals(data.getRealAlarmSource())){
                // 替换雷达告警所有表格中的文本
                replaceTableInfoTen(doc,data,dto,isTower);
            }else if ("4".equals(data.getWarningSource()) || "4".equals(data.getRealAlarmSource())){
                // 替换摄像机告警所有表格中的文本
                replaceTableInfoFour(doc,data,dto,isTower);
            }else if ("12".equals(data.getWarningSource()) || "12".equals(data.getRealAlarmSource())){
                // 替换摄像机告警所有表格中的文本
                replaceTableInfoTwelve(doc,data,dto,isTower);
            }
            //替换所有文本内容
            replaceTextInfo(doc,data);

            //获取模版插入图标
            SysConfig redPic = fileTool.selectConfigByKey("alarm_info_red_template");
            if(ObjectUtil.isNull(redPic)){
                throw new CustomException("未找到该系统参数配置：alarm_info_red_template");
            }
            String redPicFileUrl = fileUrlEncryptUtil.dealObsFileUrl(redPic.getConfigValue());
            List<Map> recordData = new ArrayList<>();
            if (dto.getExportSource() == 1){
                //查询流程信息
                AlarmOrderQueryDTO queryDTO = new AlarmOrderQueryDTO();
                queryDTO.setOrderId(data.getWarningOrderId());
                queryDTO.setSort("ASC");
                Result<Map> record = iTradeWarningOrderService.getRecordByOrderId(queryDTO);
                recordData = (List<Map>) record.getData().get("rows");
            }
            if (dto.getExportSource() == 4){
                recordData = data.getFlowLinkList();
            }
            log.info("getExportWarningDto返回流程信息参数:{}", JSON.toJSONString(recordData));
            //插入环节信息表格
            insertTableInfo(doc,data,redPicFileUrl,recordData);
            //插入转移信息表格
            if (dto.getExportType() !=null && dto.getExportType() == 1){
                insertTableTransferInfo(doc,data,redPicFileUrl);
            }
            //插入删除信息表格
            if (dto.getExportType() !=null && dto.getExportType() == 2){
                insertTableDeleteInfo(doc,data,redPicFileUrl);
            }
            // 插入告警图片
            if (!CollectionUtils.isEmpty(data.getAlarmPicList())){
                boolean addPicBoolean = false;
                for (Map map : data.getAlarmPicList()) {
                    String picUrl = String.valueOf(map.get("fileUrl"));
                    if (!StringUtils.isEmpty(picUrl)){
                        addPicBoolean = true;
                    }
                }
                if (addPicBoolean){
                    insertTitleInfo(doc," 告警图片",redPicFileUrl);
                    insertPicInfo(doc,data);
                }

            }
            //插入地图
            if (file != null){
                insertTitleInfo(doc," 告警地图",redPicFileUrl);
                insertPlatPicInfo(doc,file);
            }

            bos = new ByteArrayOutputStream();
            doc.write(bos);
            byte[] bytes = bos.toByteArray();
        /*    FileUtils.download(response, bytes, "");
            System.out.println("转换成功！");
*/
            //告警时间
            String alarmTime = StringUtils.isEmpty(data.getAlarmTime())?"":data.getAlarmTime();
            //修改时间格式
            SimpleDateFormat sourceSdf = new SimpleDateFormat(SOURCE_DATE_FORMAT);
            Date sourceDate = sourceSdf.parse(alarmTime);
            SimpleDateFormat targetSdf = new SimpleDateFormat(TARGET_DATE_FORMAT);
            String targetDateStr = targetSdf.format(sourceDate);
            //事件标题
            String alarmBody = StringUtils.isEmpty(data.getAlarmBody())?"":data.getAlarmBody();
             zipOut = new ZipOutputStream(response.getOutputStream());
            ZipEntry zipEntry = new ZipEntry(targetDateStr+" "+alarmBody+".docx");
            zipOut.putNextEntry(zipEntry);
            zipOut.write(bytes);
            zipOut.closeEntry();
            if (!CollectionUtils.isEmpty(recordData)){
                for (Map map : recordData) {
                    Object fileObject = null;
                    //处置 files
                    if ("4".equals(String.valueOf(map.get(Constants.PARAM_REALLINK_TYPE)))){
                        fileObject = map.get("files");
                    }else {
                        fileObject = map.get("orderFileList");
                    }
                    if (fileObject != null){
                        List<Map> fileMapList = (List<Map>) fileObject;
                        if (!CollectionUtils.isEmpty(fileMapList) && fileMapList.size()>0){
                            //环节名称 + 处理时间
                            String linkName = String.valueOf(map.get(Constants.PARAM_LINK_NAME));
                            String flowEndTime = String.valueOf(map.get(Constants.PARAM_FLOWEND_TIME));
                            //修改时间格式
                            sourceDate = sourceSdf.parse(flowEndTime);
                            targetDateStr = targetSdf.format(sourceDate);
                            // 创建文件夹
                            int fileNum = 1;
                            for (Map fileMap : fileMapList) {
                                    String resourceUrl = String.valueOf(fileMap.get("resourceUrl"));
                                    String fileName = String.valueOf(fileMap.get("fileName"));
                                try {
                                    if (!StringUtils.isEmpty(resourceUrl)){
                                        URL filesUrl = new URL(resourceUrl);
                                        // 打开连接并设置超时时间
                                        URLConnection fileConnection = filesUrl.openConnection();
                                        fileConnection.setConnectTimeout(5000); // 设置连接超时时间为5秒
                                        fileConnection.setReadTimeout(5000);   // 设置读取超时时间为10秒
                                        InputStream stream = fileConnection.getInputStream();

                                        byte[] buffer = IOUtils.toByteArray(stream);
                                        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
                                        // 获取文件名中点（.）之前的部分
                                        String newFileName = fileName.substring(0, fileName.lastIndexOf("."));
                                        zipOut.putNextEntry(new ZipEntry(linkName+" "+targetDateStr + "/"+newFileName+"-"+fileNum+"."+suffix));
                                        zipOut.write(buffer);
                                        zipOut.closeEntry();
                                        fileNum++;
                                    }
                                } catch (Exception e) {
                                    log.error("插入附件失败",e);
                                    log.info("插入附件失败:{}", fileName);
                                }
                            }
                        }

                    }

                }
            }

            // 此时压缩包的字节数组已经生成在 zipBytes 中
            log.info("压缩包创建成功，并已将 Word 流放入其中。");
            // 获取程序结束时间
            long endTime = System.currentTimeMillis();
            log.info("导出压缩包消耗时间（毫秒）：" + (startTime-endTime));
        } catch (Exception e) {
            log.error(Constants.CHECK_EXPORT_FAIL,e);
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var21) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var21);
                }
            }
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException var21) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var21);
                }
            }
            if (zipOut != null) {
                try {
                    zipOut.close();
                } catch (IOException var20) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var20);
                }
            }

            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException var20) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var20);
                }
            }
        }

    }

    //事件详情导出 修改无人机告警表格内容
    private void replaceTableInfoTwelve(XWPFDocument doc, AlarmOrderInfoExportDTO order, ExportOrderInfoReq dto, boolean isTower) {
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //无人机设备以及机巢信息
        UavAlarmSourceVo uavDeviceInfoVO = order.getUavDevInfoList() != null ? order.getUavDevInfoList() : null;
        if (uavDeviceInfoVO != null) {
            if (uavDeviceInfoVO.getDeviceCode() == null)uavDeviceInfoVO.setDeviceCode("");
            if (uavDeviceInfoVO.getDeviceName() == null)uavDeviceInfoVO.setDeviceName("");
            if (uavDeviceInfoVO.getModelFactoryName() == null)uavDeviceInfoVO.setModelFactoryName("");
            if (uavDeviceInfoVO.getDeviceModelCode() == null)uavDeviceInfoVO.setDeviceModelCode("");
            if (order.getConfidenceLevel() == null)order.setConfidenceLevel("");
            if (uavDeviceInfoVO.getLocation() == null)uavDeviceInfoVO.setLocation("");
            if (uavDeviceInfoVO.getLongitude() == null || uavDeviceInfoVO.getLatitude() == null){
                uavDeviceInfoVO.setLongitude("");
            }else {
                uavDeviceInfoVO.setLongitude(format.format(Double.valueOf(uavDeviceInfoVO.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(uavDeviceInfoVO.getLatitude())));
            }
            for (UavNestDeviceVo uavNestDeviceVo : uavDeviceInfoVO.getUavNestDeviceList()) {
                if (uavNestDeviceVo.getUavNestCode() == null)uavNestDeviceVo.setUavNestCode("");
                if (uavNestDeviceVo.getUavNestName() == null)uavNestDeviceVo.setUavNestName("");
                if (uavNestDeviceVo.getModelCodeName() == null)uavNestDeviceVo.setModelCodeName("");
                if (uavNestDeviceVo.getDeviceModel() == null)uavNestDeviceVo.setDeviceModel("");
            }
        }
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_DEVICECODE)) {
                            fullText = fullText.replace(Constants.EXP_DEVICECODE, uavDeviceInfoVO.getDeviceCode());
                        }
                        if (fullText.contains(Constants.EXP_DEVICENAME)) {
                            fullText = fullText.replace(Constants.EXP_DEVICENAME, uavDeviceInfoVO.getDeviceName());
                        }
                        if (fullText.contains("{{modelFactoryName}}")) {
                            fullText = fullText.replace("{{modelFactoryName}}", uavDeviceInfoVO.getModelFactoryName());
                        }
                        if (fullText.contains("{{deviceModelCode}}")) {
                            fullText = fullText.replace("{{deviceModelCode}}", uavDeviceInfoVO.getDeviceModelCode());
                        }
                        if (fullText.contains(Constants.EXP_DEVICELOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_DEVICELOTANDLAT, uavDeviceInfoVO.getLongitude());
                        }
                        if (fullText.contains(Constants.EXP_CONFIDENCELEVEL)) {
                            fullText = fullText.replace(Constants.EXP_CONFIDENCELEVEL, order.getConfidenceLevel());
                        }
                        if (fullText.contains(Constants.EXP_DEVICEADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEADDRESS, uavDeviceInfoVO.getLocation());
                        }
                        if (fullText.contains("{{uavNestCode}}")) {
                            if (uavDeviceInfoVO.getUavNestDeviceList().size() >= 1){
                                fullText = fullText.replace("{{uavNestCode}}", uavDeviceInfoVO.getUavNestDeviceList().get(0).getUavNestCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{uavNestName}}")) {
                            if (uavDeviceInfoVO.getUavNestDeviceList().size() >= 1){
                                fullText = fullText.replace("{{uavNestName}}", uavDeviceInfoVO.getUavNestDeviceList().get(0).getUavNestName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{modelCodeName}}")) {
                            if (uavDeviceInfoVO.getUavNestDeviceList().size() >= 1){
                                fullText = fullText.replace("{{modelCodeName}}", uavDeviceInfoVO.getUavNestDeviceList().get(0).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceModel}}")) {
                            if (uavDeviceInfoVO.getUavNestDeviceList().size() >= 1){
                                fullText = fullText.replace("{{deviceModel}}", uavDeviceInfoVO.getUavNestDeviceList().get(0).getDeviceModel());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())){
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        }else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }

    //事件详情导出 插入环节信息表格
    public  void insertTableInfo(XWPFDocument doc,AlarmOrderInfoExportDTO data,String redPicUrl,List<Map> recordData){
        try {
            for (Map recordDatum : recordData) {
                if (!StringUtils.isEmpty(String.valueOf(recordDatum.get(Constants.PARAM_FLOWEND_TIME)))){
                    if ("1".equals(String.valueOf(recordDatum.get(Constants.PARAM_REALLINK_TYPE)))){
                        insertTitleInfo(doc," 研判信息",redPicUrl);
                    }else if ("2".equals(String.valueOf(recordDatum.get(Constants.PARAM_REALLINK_TYPE)))){
                        insertTitleInfo(doc," 调度信息",redPicUrl);
                    }else if ("4".equals(String.valueOf(recordDatum.get(Constants.PARAM_REALLINK_TYPE)))){
                        insertTitleInfo(doc," 处置信息",redPicUrl);
                    }else if ("5".equals(String.valueOf(recordDatum.get(Constants.PARAM_REALLINK_TYPE)))){
                        insertTitleInfo(doc," 核实信息",redPicUrl);
                    }

                    if ("1".equals(String.valueOf(recordDatum.get(Constants.PARAM_REALLINK_TYPE)))){
                        insertJudgeTable(doc,recordDatum);
                    }else if (!"-1".equals(String.valueOf(recordDatum.get(Constants.PARAM_REALLINK_TYPE)))){
                        insertoNotJudgeTable(doc,recordDatum);
                    }
                }
            }

        }catch (Exception e){
            log.error("导出详情插入环节信息异常", e);
        }

    }

    //事件详情导出 插入环节信息表格

    public void insertJudgeTable(XWPFDocument doc,Map map){
        //创建表格
        XWPFTable table = doc.createTable(5, 4);

        int tableWidth = (int) (20.08 * 705);
        CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
        // 关闭表格的自动调整大小功能
        CTTblLayoutType tableLayout = table.getCTTbl().getTblPr().addNewTblLayout();
        tableLayout.setType(STTblLayoutType.FIXED);
        //设置表格左对齐缩进
        CTTblWidth tblInd = table.getCTTbl().getTblPr().getTblInd();
        if(null == tblInd){
            tblInd = table.getCTTbl().getTblPr().addNewTblInd();
        }
        //缩进距离
        tblInd.setW(BigInteger.valueOf(200));
        table.getCTTbl().getTblPr().setTblInd(tblInd);
        width.setW(BigInteger.valueOf(tableWidth));
        table.setTableAlignment(TableRowAlign.LEFT);
        String linkType = String.valueOf(map.get("flowLinkType"));
        String nextLinkId = String.valueOf(map.get("nextLinkId"));
        String remark = String.valueOf(map.get(Constants.PARAM_REMARK));


        String status = String.valueOf(map.get("status"));
        if ("0".equals(status)){
            status = "误报";
        }else if ("2".equals(status)){
            status = "重复告警";
        }else if ("1".equals(status)){
            status = "真警";
        }else {
            status ="";
        }

        if ("6".equals(nextLinkId) && "关闭告警".equals(remark)){
            linkType ="关闭";
        }else if ("0".equals(linkType)){
            linkType ="正常流转";
        }else if ("1".equals(linkType)){
            linkType ="转派";
            status ="";
        }else if ("2".equals(linkType)){
            linkType ="回退";
            status ="";
        }else if ("3".equals(linkType)){
            linkType ="受理";
            map.put(Constants.PARAM_NEXT_LINK_NAME,"");
            map.put(Constants.NEXT_PROCESS_NAME,"");
            map.put(Constants.PARAM_REMARK,"");
            status ="";
        }
        //合并单元格
        table.getRow(3).getCell(1).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        table.getRow(3).getCell(2).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(3).getCell(3).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(4).getCell(1).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        table.getRow(4).getCell(2).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(4).getCell(3).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        List<List<String>> data = Arrays.asList(
                Arrays.asList("处理环节", String.valueOf(map.get(Constants.PARAM_LINK_NAME)), "处理人",String.valueOf(map.get("processName"))),
                Arrays.asList("环节类型", linkType, "处理时间",String.valueOf(map.get(Constants.PARAM_FLOWEND_TIME))),
                Arrays.asList("研判结果", status, "选择环节",String.valueOf(map.get(Constants.PARAM_NEXT_LINK_NAME))),
                Arrays.asList("选择处理人", String.valueOf(map.get(Constants.NEXT_PROCESS_NAME))),
                Arrays.asList("办理意见", String.valueOf(map.get(Constants.PARAM_REMARK)))
        );
        // 重新设置单元格宽度
        for (int i = 1; i <= 3; i++) {
            long resetWidth = (long) (6.68 * 531.5);
            CTTblWidth cellWidth = table.getRow(3).getCell(i).getCTTc().addNewTcPr().addNewTcW();
            CTTblWidth cellWidth1 = table.getRow(4).getCell(i).getCTTc().addNewTcPr().addNewTcW();
            cellWidth.setW(BigInteger.valueOf(resetWidth));
            cellWidth1.setW(BigInteger.valueOf(resetWidth));
        }
        for (int j = 0; j < data.size(); j++) {
            List<String> rowData = data.get(j);
            XWPFTableRow row = table.getRow(j);

            for (int i = 0; i < rowData.size(); i++) {
                String cellData = rowData.get(i);
                XWPFTableCell cell = row.getCell(i);
                if (i == 0 || i == 2){
                    CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                    CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                    cellWidth.setW(BigInteger.valueOf((long) (4.92 * 567)));
                }else {
                    CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                    CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                    cellWidth.setW(BigInteger.valueOf((long) (7.6 * 567)));
                }

                XWPFParagraph paragraph1 = cell.getParagraphArray(0);
                XWPFRun run1 = paragraph1.createRun();
                if ((i+1) % 2 == 0) {
                    setCellFontLeftStyle(run1, paragraph1);
                } else {
                    setCellFontRightStyle(run1, paragraph1);
                }
                run1.setText(cellData);

            }
        }
    }

    public void insertoNotJudgeTable(XWPFDocument doc,Map map){
        //创建表格
        XWPFTable table = doc.createTable(5, 4);
        int tableWidth = (int) (20.08 * 705);
        CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
        width.setW(BigInteger.valueOf(tableWidth));
        table.setTableAlignment(TableRowAlign.LEFT);
        // 关闭表格的自动调整大小功能
        CTTblLayoutType tableLayout = table.getCTTbl().getTblPr().addNewTblLayout();
        tableLayout.setType(STTblLayoutType.FIXED);
        //设置表格左对齐缩进
        CTTblWidth tblInd = table.getCTTbl().getTblPr().getTblInd();
        if(null == tblInd){
            tblInd = table.getCTTbl().getTblPr().addNewTblInd();
        }
        //缩进距离
        tblInd.setW(BigInteger.valueOf(200));
        table.getCTTbl().getTblPr().setTblInd(tblInd);
        String linkType = String.valueOf(map.get("flowLinkType"));
        String nextLinkId = String.valueOf(map.get("nextLinkId"));
        String remark = String.valueOf(map.get("remark"));
        if ("6".equals(nextLinkId) && "关闭告警".equals(remark)){
            linkType ="关闭";
        }else if ("0".equals(linkType)){
            linkType ="正常流转";
        }else if ("1".equals(linkType)){
            linkType ="转派";
        }else if ("2".equals(linkType)){
            linkType ="回退";
        }else if ("3".equals(linkType)){
            linkType ="受理";
            map.put(Constants.PARAM_NEXT_LINK_NAME,"");
            map.put(Constants.NEXT_PROCESS_NAME,"");
            map.put(Constants.PARAM_REMARK,"");
        }
        //合并单元格
        table.getRow(2).getCell(1).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        table.getRow(2).getCell(2).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(2).getCell(3).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(3).getCell(1).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        table.getRow(3).getCell(2).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(3).getCell(3).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(4).getCell(1).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        table.getRow(4).getCell(2).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        table.getRow(4).getCell(3).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        List<List<String>> data = Arrays.asList(
                Arrays.asList("处理环节", String.valueOf(map.get(Constants.PARAM_LINK_NAME)), "处理人",String.valueOf(map.get("processName"))),
                Arrays.asList("环节类型", linkType, "处理时间",String.valueOf(map.get(Constants.PARAM_FLOWEND_TIME))),
                Arrays.asList("选择环节",String.valueOf(map.get(Constants.PARAM_NEXT_LINK_NAME))),
                Arrays.asList("选择处理人", String.valueOf(map.get(Constants.NEXT_PROCESS_NAME))),
                Arrays.asList("办理意见", String.valueOf(map.get(Constants.PARAM_REMARK)))
        );
        // 重新设置单元格宽度
        for (int i = 1; i <= 3; i++) {
            long resetWidth = (long) (6.68 * 531.5);
            CTTblWidth cellWidth1 = table.getRow(2).getCell(i).getCTTc().addNewTcPr().addNewTcW();
            CTTblWidth cellWidth2 = table.getRow(3).getCell(i).getCTTc().addNewTcPr().addNewTcW();
            CTTblWidth cellWidth3 = table.getRow(4).getCell(i).getCTTc().addNewTcPr().addNewTcW();
            cellWidth1.setW(BigInteger.valueOf(resetWidth));
            cellWidth2.setW(BigInteger.valueOf(resetWidth));
            cellWidth3.setW(BigInteger.valueOf(resetWidth));
        }
        for (int j = 0; j < data.size(); j++) {
            List<String> rowData = data.get(j);
            XWPFTableRow row = table.getRow(j);

            for (int i = 0; i < rowData.size(); i++) {
                String cellData = rowData.get(i);
                XWPFTableCell cell = row.getCell(i);
                if (i == 0 || i == 2){
                    CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                    CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                    cellWidth.setW(BigInteger.valueOf((long) (4.92 * 567)));
                }else {
                    CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                    CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                    cellWidth.setW(BigInteger.valueOf((long) (7.6 * 567)));
                }

                XWPFParagraph paragraph1 = cell.getParagraphArray(0);
                XWPFRun run1 = paragraph1.createRun();
                if ((i+1) % 2 == 0) {
                    setCellFontLeftStyle(run1, paragraph1);
                } else {
                    setCellFontRightStyle(run1, paragraph1);
                }
                run1.setText(cellData);

            }
        }
    }

    //事件详情导出 插入转移信息表格
    public  void insertTableTransferInfo(XWPFDocument doc,AlarmOrderInfoExportDTO datas,String redPicUrl){
        try {
            insertTitleInfo(doc," 转移记录",redPicUrl);
            //创建表格
            XWPFTable table = doc.createTable(1, 4);
            int tableWidth = (int) (20.08 * 705);
            CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
            width.setW(BigInteger.valueOf(tableWidth));
            table.setTableAlignment(TableRowAlign.LEFT);
            // 关闭表格的自动调整大小功能
            CTTblLayoutType tableLayout = table.getCTTbl().getTblPr().addNewTblLayout();
            tableLayout.setType(STTblLayoutType.FIXED);
            //设置表格左对齐缩进
            CTTblWidth tblInd = table.getCTTbl().getTblPr().getTblInd();
            if(null == tblInd){
                tblInd = table.getCTTbl().getTblPr().addNewTblInd();
            }
            //缩进距离
            tblInd.setW(BigInteger.valueOf(200));
            table.getCTTbl().getTblPr().setTblInd(tblInd);
            List<List<String>> data = Arrays.asList(
                    Arrays.asList("操作内容", "告警转移", "转移时间",datas.getModifierDate())

            );
            for (int j = 0; j < data.size(); j++) {
                List<String> rowData = data.get(j);
                XWPFTableRow row = table.getRow(j);

                for (int i = 0; i < rowData.size(); i++) {
                    String cellData = rowData.get(i);
                    XWPFTableCell cell = row.getCell(i);
                    if (i == 0 || i == 2){
                        CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                        CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                        cellWidth.setW(BigInteger.valueOf((long) (4.92 * 567)));
                    }else {
                        CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                        CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                        cellWidth.setW(BigInteger.valueOf((long) (7.6 * 567)));
                    }

                    XWPFParagraph paragraph1 = cell.getParagraphArray(0);
                    XWPFRun run1 = paragraph1.createRun();
                    if ((i+1) % 2 == 0) {
                        setCellFontLeftStyle(run1, paragraph1);
                    } else {
                        setCellFontRightStyle(run1, paragraph1);
                    }
                    run1.setText(cellData);

                }
            }
        }catch (Exception e){
            log.error("导出详情插入转移信息异常", e);
        }

    }

    //事件详情导出 插入删除信息表格
    public  void insertTableDeleteInfo(XWPFDocument doc,AlarmOrderInfoExportDTO datas,String redPicUrl){
        try {
            insertTitleInfo(doc," 删除信息",redPicUrl);
            //创建表格
            XWPFTable table = doc.createTable(3, 4);
            int tableWidth = (int) (20.08 * 705);
            CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
            width.setW(BigInteger.valueOf(tableWidth));
            table.setTableAlignment(TableRowAlign.LEFT);
            // 关闭表格的自动调整大小功能
            CTTblLayoutType tableLayout = table.getCTTbl().getTblPr().addNewTblLayout();
            tableLayout.setType(STTblLayoutType.FIXED);
            //设置表格左对齐缩进
            CTTblWidth tblInd = table.getCTTbl().getTblPr().getTblInd();
            if(null == tblInd){
                tblInd = table.getCTTbl().getTblPr().addNewTblInd();
            }
            //缩进距离
            tblInd.setW(BigInteger.valueOf(200));
            table.getCTTbl().getTblPr().setTblInd(tblInd);
            //合并单元格
            table.getRow(1).getCell(1).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            table.getRow(1).getCell(2).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            table.getRow(1).getCell(3).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            table.getRow(2).getCell(1).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            table.getRow(2).getCell(2).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            table.getRow(2).getCell(3).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);

            List<List<String>> data = Arrays.asList(
                    Arrays.asList("删除人", datas.getDeleteUser(), "删除时间",datas.getModifierDate()),
                    Arrays.asList("操作内容", "告警删除"),
                    Arrays.asList("删除说明", datas.getDeleteRemark())
            );
            // 重新设置单元格宽度
            for (int i = 1; i <= 3; i++) {
                long resetWidth = (long) (6.68 * 531.5);
                CTTblWidth cellWidth = table.getRow(1).getCell(i).getCTTc().addNewTcPr().addNewTcW();
                CTTblWidth cellWidth1 = table.getRow(2).getCell(i).getCTTc().addNewTcPr().addNewTcW();
                cellWidth.setW(BigInteger.valueOf(resetWidth));
                cellWidth1.setW(BigInteger.valueOf(resetWidth));
            }
            for (int j = 0; j < data.size(); j++) {
                List<String> rowData = data.get(j);
                XWPFTableRow row = table.getRow(j);

                for (int i = 0; i < rowData.size(); i++) {
                    String cellData = rowData.get(i);
                    XWPFTableCell cell = row.getCell(i);
                    if (i == 0 || i == 2){
                        CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                        CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                        cellWidth.setW(BigInteger.valueOf((long) (4.92 * 567)));
                    }else {
                        CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                        CTTblWidth cellWidth = tcpr.isSetTcW() ? tcpr.getTcW() : tcpr.addNewTcW();
                        cellWidth.setW(BigInteger.valueOf((long) (7.6 * 567)));
                    }

                    XWPFParagraph paragraph1 = cell.getParagraphArray(0);
                    XWPFRun run1 = paragraph1.createRun();
                    if ((i+1) % 2 == 0) {
                        setCellFontLeftStyle(run1, paragraph1);
                    } else {
                        setCellFontRightStyle(run1, paragraph1);
                    }
                    run1.setText(cellData);

                }
            }
        }catch (Exception e){
            log.error("导出详情插入删除信息异常", e);
        }

    }

    //事件详情导出 修改AI告警表格内容
    public void replaceTableInfoOne(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //告警压制原因
        String interceptName = StringUtils.isEmpty(order.getInterceptName())?"":order.getInterceptName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //事发地点到摄像机距离
        String incidentDistance = StringUtils.isEmpty(order.getIncidentDistance())?"":order.getIncidentDistance();
        //摄像机俯仰角
        String devicePitch = StringUtils.isEmpty(dto.getDevicePitch())?"":dto.getDevicePitch();
        //摄像机水平角
        String deviceHorizontal = StringUtils.isEmpty(dto.getDeviceHorizontal())?"":dto.getDeviceHorizontal();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //电子围栏  图层名称  图斑编码
        List<Map> polygonOperate = CollectionUtils.isEmpty(order.getPolygonOperate()) ? null : order.getPolygonOperate();
        int polygonSize = 0;
        if (!CollectionUtils.isEmpty(polygonOperate)){
            for (Map map : polygonOperate) {
                if (map.get(Constants.PARAM_POLYGON_NAME) == null)map.put(Constants.PARAM_POLYGON_NAME,"");
                if (map.get(Constants.PARAM_LAYER_NAME) == null)map.put(Constants.PARAM_LAYER_NAME,"");
                if (map.get("tbbh") == null)map.put("tbbh","");
            }
            polygonSize = polygonOperate.size();
        }
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //算法配置-事件描述
        String algorithmDescription = StringUtils.isEmpty(order.getAlgorithmDescription())?"":order.getAlgorithmDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //摄像机编码
        String deviceCode = StringUtils.isEmpty(order.getDeviceCode())?"":order.getDeviceCode();
        //摄像机名称
        String deviceName = StringUtils.isEmpty(order.getDeviceName())?"":order.getDeviceName();
        String deviceLabelString = "";
        //摄像机标签
        if (!CollectionUtils.isEmpty(order.getDeviceLabel())){
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < order.getDeviceLabel().size(); i++) {
                if (i+1 == order.getDeviceLabel().size()){
                    stringBuffer.append(order.getDeviceLabel().get(i));
                }else{
                    stringBuffer.append(order.getDeviceLabel().get(i)).append(Constants.COMMA);
                }
            }
            deviceLabelString = stringBuffer.toString();
        }
        //通道名称
        String channelName = StringUtils.isEmpty(order.getChannelName())?"":order.getChannelName();
        //摄像机厂家
        String modelName = StringUtils.isEmpty(order.getModelFactoryName())?"":order.getModelFactoryName();
        //摄像机经纬度
        String videoLotAndLat = StringUtils.isEmpty(order.getDeviceLotAndLat())?"":order.getDeviceLotAndLat();
        //摄像机挂高
        String height = StringUtils.isEmpty(order.getHeight())?"":order.getHeight();
        //摄像机地址
        String deviceAddress =StringUtils.isEmpty(order.getDeviceAddress())?"":order.getDeviceAddress();
        //置信度
        String confidenceLevel = StringUtils.isEmpty(order.getConfidenceLevel())?"":order.getConfidenceLevel();
        //水平方位角
        String horiAzimuthAngle = StringUtils.isEmpty(dto.getHoriAzimuthAngle())?"":dto.getHoriAzimuthAngle();
        //预置位ID
        String presetId = StringUtils.isEmpty(order.getPresetId())?"":order.getPresetId();
        //预置位名称
        String presetName = StringUtils.isEmpty(order.getPresetName())?"":order.getPresetName();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        //是否添加 算法配置-事件描述
        boolean algorithmTrue = false;
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        algorithmTrue = false;
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INTERCEPTNAME)) {
                            if (dto.getExportSource() ==2 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_INTERCEPTNAME, interceptName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE,alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID,warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL,levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME,alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE,sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE,warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON,alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS,address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT,lotAndLat);
                        }
                        if (fullText.contains("事发地点到摄像机距离")) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_ALARMDISTANCE)) {
                            if (!StringUtils.isEmpty(incidentDistance)){
                                fullText = fullText.replace(Constants.EXP_ALARMDISTANCE,incidentDistance);
                            }else {
                                xwpfParagraphList.add(paragraph);
                                xwpfParagraphList.add(xwpfParagraph);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMPITCHANGLE)) {
                            if (StringUtil.isNotEmpty(devicePitch)){
                                fullText = fullText.replace(Constants.EXP_ALARMPITCHANGLE,devicePitch);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMHORANGLE)) {
                            if (StringUtil.isNotEmpty(deviceHorizontal)){
                                fullText = fullText.replace(Constants.EXP_ALARMHORANGLE,deviceHorizontal);
                            }
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID,gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER,keeperName);
                        }
                        if (fullText.contains("{{alarmFence1}}")) {
                            if (polygonSize >= 1){
                                fullText = fullText.replace("{{alarmFence1}}", String.valueOf(polygonOperate.get(0).get(Constants.PARAM_POLYGON_NAME)));
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{alarmLayer1}}")) {
                            if (polygonSize >= 1){
                                fullText = fullText.replace("{{alarmLayer1}}", String.valueOf(polygonOperate.get(0).get(Constants.PARAM_LAYER_NAME)));
                            }
                        }
                        if (fullText.contains("{{alarmFreckle1}}")) {
                            if (polygonSize >= 1){
                                fullText = fullText.replace("{{alarmFreckle1}}", String.valueOf(polygonOperate.get(0).get("tbbh")));
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{alarmFence2}}")) {
                            if (polygonSize >= 2){
                                fullText = fullText.replace("{{alarmFence2}}", String.valueOf(polygonOperate.get(1).get(Constants.PARAM_POLYGON_NAME)));
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{alarmLayer2}}")) {
                            if (polygonSize >= 2){
                                fullText = fullText.replace("{{alarmLayer2}}", String.valueOf(polygonOperate.get(1).get(Constants.PARAM_LAYER_NAME)));
                            }
                        }
                        if (fullText.contains("{{alarmFreckle2}}")) {
                            if (polygonSize >= 2){
                                fullText = fullText.replace("{{alarmFreckle2}}", String.valueOf(polygonOperate.get(1).get("tbbh")));
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{alarmFence3}}")) {
                            if (polygonSize >= 3){
                                fullText = fullText.replace("{{alarmFence3}}", String.valueOf(polygonOperate.get(2).get(Constants.PARAM_POLYGON_NAME)));
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{alarmLayer3}}")) {
                            if (polygonSize >= 3){
                                fullText = fullText.replace("{{alarmLayer3}}", String.valueOf(polygonOperate.get(2).get(Constants.PARAM_LAYER_NAME)));
                            }
                        }
                        if (fullText.contains("{{alarmFreckle3}}")) {
                            if (polygonSize >= 3){
                                fullText = fullText.replace("{{alarmFreckle3}}", String.valueOf(polygonOperate.get(2).get("tbbh")));
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE,description);
                            algorithmTrue = true;
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK,remark);
                        }
                        if (fullText.contains(Constants.EXP_DEVICECODE)) {
                            fullText = fullText.replace(Constants.EXP_DEVICECODE,deviceCode);
                        }
                        if (fullText.contains(Constants.EXP_DEVICENAME)) {
                            fullText = fullText.replace(Constants.EXP_DEVICENAME,deviceName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELABEL)) {
                            if (StringUtil.isNotEmpty(deviceLabelString)){
                                fullText = fullText.replace(Constants.EXP_DEVICELABEL,deviceLabelString);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_CHANNELNAME)) {
                            fullText = fullText.replace(Constants.EXP_CHANNELNAME,channelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEFACTORY)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEFACTORY,modelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_DEVICELOTANDLAT,videoLotAndLat);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEHIGH)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEHIGH,height);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEADDRESS,deviceAddress);
                        }
                        if (fullText.contains(Constants.EXP_CONFIDENCELEVEL)) {
                            fullText = fullText.replace(Constants.EXP_CONFIDENCELEVEL,confidenceLevel);
                        }
                        if (fullText.contains("水平方位角")) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_HORIZONTALAZIMUTH)) {
                            if (StringUtil.isNotEmpty(horiAzimuthAngle)){
                                fullText = fullText.replace(Constants.EXP_HORIZONTALAZIMUTH,horiAzimuthAngle);
                            }else {
                                xwpfParagraphList.add(paragraph);
                                xwpfParagraphList.add(xwpfParagraph);
                            }
                        }
                        if (fullText.contains("{{persetId}}")) {
                            if (!StringUtils.isEmpty(presetId)){
                                fullText = fullText.replace("{{persetId}}",presetId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{persetName}}")) {
                            if (!StringUtils.isEmpty(presetId)){
                                fullText = fullText.replace("{{persetName}}",presetName);
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())){

                            if (algorithmTrue){
                                XWPFRun newRun1 = paragraph.createRun();
                                // 创建一个新运行，并将更新后的文本添加到该运行中
                                newRun1.setText("算法识别结果", 0);
                                newRun1.addBreak(); // 添加换行符
                                newRun1.setFontSize(12);
                                newRun1.setFontFamily("宋体");
                            }

                            XWPFRun newRun = paragraph.createRun();
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                            if (!StringUtils.isEmpty(algorithmDescription) && algorithmTrue){
                                newRun.addBreak();
                            }

                        }else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                        if (!StringUtils.isEmpty(algorithmDescription) && algorithmTrue){
                            XWPFRun newRun2 = paragraph.createRun();
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            newRun2.setText("算法配置参数", 0);
                            newRun2.addBreak(); // 添加换行符
                            newRun2.setFontSize(12);
                            newRun2.setFontFamily("宋体");
                            XWPFRun newRun3 = paragraph.createRun();
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            newRun3.setText(algorithmDescription, 0);
                            newRun3.setFontSize(12);
                            newRun3.setFontFamily("宋体");

                        }

                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }

    //事件详情导出 修改人工告警表格内容
    public void replaceTableInfoTwo(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
         //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //告警人员
        String findName = StringUtils.isEmpty(order.getFindName()) ? "" : order.getFindName();
        //联系方式
        String findMobile = StringUtils.isEmpty(order.getFindMobile())?"":order.getFindMobile();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE,alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID,warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL,levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME,alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE,sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE,warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON,alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS,address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT,lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID,gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER,keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE,description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK,remark);
                        }
                        if (fullText.contains("{{findName}}")) {
                            fullText = fullText.replace("{{findName}}",findName);
                        }

                        if (fullText.contains("{{findMobile}}")) {
                            fullText = fullText.replace("{{findMobile}}",findMobile);
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())){
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        }else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
    }


    //事件详情导出 修改一键告警表格内容
    public void replaceTableInfoThree(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //告警压制原因
        String interceptName = StringUtils.isEmpty(order.getInterceptName())?"":order.getInterceptName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //摄像机编码
        String deviceCode = StringUtils.isEmpty(order.getDeviceCode())?"":order.getDeviceCode();
        //摄像机名称
        String deviceName = StringUtils.isEmpty(order.getDeviceName())?"":order.getDeviceName();
        String deviceLabelString = "";
        //摄像机标签
        if (!CollectionUtils.isEmpty(order.getDeviceLabel())){
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < order.getDeviceLabel().size(); i++) {
                if (i+1 == order.getDeviceLabel().size()){
                    stringBuffer.append(order.getDeviceLabel().get(i));
                }else{
                    stringBuffer.append(order.getDeviceLabel().get(i)).append(Constants.COMMA);
                }
            }
            deviceLabelString = stringBuffer.toString();
        }
        //通道名称
        String channelName = StringUtils.isEmpty(order.getChannelName())?"":order.getChannelName();
        //摄像机厂家
        String modelName = StringUtils.isEmpty(order.getModelFactoryName())?"":order.getModelFactoryName();
        //摄像机经纬度
        String videoLotAndLat = StringUtils.isEmpty(order.getDeviceLotAndLat())?"":order.getDeviceLotAndLat();
        //摄像机挂高
        String height = StringUtils.isEmpty(order.getHeight())?"":order.getHeight();
        //摄像机地址
        String deviceAddress =StringUtils.isEmpty(order.getDeviceAddress())?"":order.getDeviceAddress();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INTERCEPTNAME)) {
                            if (dto.getExportSource() ==2 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_INTERCEPTNAME, interceptName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_DEVICECODE)) {
                            fullText = fullText.replace(Constants.EXP_DEVICECODE, deviceCode);
                        }
                        if (fullText.contains(Constants.EXP_DEVICENAME)) {
                            fullText = fullText.replace(Constants.EXP_DEVICENAME, deviceName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELABEL)) {
                            if (StringUtil.isNotEmpty(deviceLabelString)){
                                fullText = fullText.replace(Constants.EXP_DEVICELABEL,deviceLabelString);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_CHANNELNAME)) {
                            fullText = fullText.replace(Constants.EXP_CHANNELNAME, channelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEFACTORY)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEFACTORY, modelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_DEVICELOTANDLAT, videoLotAndLat);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEHIGH)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEHIGH, height);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEADDRESS, deviceAddress);
                        }
                            // 删除原始段落中的所有运行
                            while (paragraph.getRuns().size() > 0) {
                                paragraph.removeRun(0);
                            }
                            if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                                // 创建一个新运行，并将更新后的文本添加到该运行中
                                XWPFRun newRun = paragraph.createRun();
                                newRun.setText(fullText, 0);
                                newRun.setFontSize(12);
                                newRun.setFontFamily("宋体");
                            } else {
                                // 创建一个新运行，并将更新后的文本添加到该运行中
                                XWPFRun newRun1 = paragraph.createRun();
                                newRun1.setText(fullText, 0);
                                newRun1.setFontSize(12);
                                newRun1.setBold(true);
                                newRun1.setFontFamily("宋体");
                            }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
    }


    //事件详情导出 修改第三方告警表格内容
    public void replaceTableInfoEleven(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //告警压制原因
        String interceptName = StringUtils.isEmpty(order.getInterceptName())?"":order.getInterceptName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //摄像机编码
        String deviceCode = StringUtils.isEmpty(order.getDeviceCode())?"":order.getDeviceCode();
        //摄像机名称
        String deviceName = StringUtils.isEmpty(order.getDeviceName())?"":order.getDeviceName();
        //通道名称
        String channelName = StringUtils.isEmpty(order.getChannelName())?"":order.getChannelName();
        //摄像机厂家
        String modelName = StringUtils.isEmpty(order.getModelFactoryName())?"":order.getModelFactoryName();
        //摄像机经纬度
        String videoLotAndLat = StringUtils.isEmpty(order.getDeviceLotAndLat())?"":order.getDeviceLotAndLat();
        //摄像机挂高
        String height = StringUtils.isEmpty(order.getHeight())?"":order.getHeight();
        //摄像机地址
        String deviceAddress =StringUtils.isEmpty(order.getDeviceAddress())?"":order.getDeviceAddress();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INTERCEPTNAME)) {
                            if (dto.getExportSource() ==2 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_INTERCEPTNAME, interceptName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_DEVICECODE)) {
                            fullText = fullText.replace(Constants.EXP_DEVICECODE, deviceCode);
                        }
                        if (fullText.contains(Constants.EXP_DEVICENAME)) {
                            fullText = fullText.replace(Constants.EXP_DEVICENAME, deviceName);
                        }
                        if (fullText.contains(Constants.EXP_CHANNELNAME)) {
                            fullText = fullText.replace(Constants.EXP_CHANNELNAME, channelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEFACTORY)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEFACTORY, modelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_DEVICELOTANDLAT, videoLotAndLat);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEHIGH)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEHIGH, height);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEADDRESS, deviceAddress);
                        }
                            // 删除原始段落中的所有运行
                            while (paragraph.getRuns().size() > 0) {
                                paragraph.removeRun(0);
                            }
                            if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                                // 创建一个新运行，并将更新后的文本添加到该运行中
                                XWPFRun newRun = paragraph.createRun();
                                newRun.setText(fullText, 0);
                                newRun.setFontSize(12);
                                newRun.setFontFamily("宋体");
                            } else {
                                // 创建一个新运行，并将更新后的文本添加到该运行中
                                XWPFRun newRun1 = paragraph.createRun();
                                newRun1.setText(fullText, 0);
                                newRun1.setFontSize(12);
                                newRun1.setBold(true);
                                newRun1.setFontFamily("宋体");
                            }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
    }

    //事件详情导出 修改物联告警表格内容
    public void replaceTableInfoEight(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //告警压制原因
        String interceptName = StringUtils.isEmpty(order.getInterceptName())?"":order.getInterceptName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //物联设备编号
        String deviceCode = StringUtils.isEmpty(order.getDeviceCode())?"":order.getDeviceCode();
        //物联设备名称
        String deviceName = StringUtils.isEmpty(order.getDeviceName())?"":order.getDeviceName();
        //物联设备厂家
        String modelName = StringUtils.isEmpty(order.getModelFactoryName())?"":order.getModelFactoryName();
        //物联设备型号
        String deviceModelCode = StringUtils.isEmpty(order.getDeviceModelCode())?"":order.getDeviceModelCode();
        //物联设备经纬度
        String videoLotAndLat = StringUtils.isEmpty(order.getDeviceLotAndLat())?"":order.getDeviceLotAndLat();
         //物联设备地址
        String deviceAddress =StringUtils.isEmpty(order.getDeviceAddress())?"":order.getDeviceAddress();
        //二次识别结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondIdentifyResult()) ? "" : order.getSecondIdentifyResult();
        //是否二次识别
        String isSecondIdentify = StringUtils.isEmpty(order.getIsSecondIdentify()) ? "" : order.getIsSecondIdentify();
        //联动算法名称
        String linkageAlgorithm = StringUtils.isEmpty(order.getLinkageAlgorithm()) ? "" : order.getLinkageAlgorithm();
        //物联设备及联动摄像机
        List<IotDeviceInfoVO> iotDeviceInfoVOS = CollectionUtils.isEmpty(order.getIotDevInfoList()) ? null : order.getIotDevInfoList();
        int iotDeviceSize = 0;
        if (!CollectionUtils.isEmpty(iotDeviceInfoVOS)){
            for (IotDeviceInfoVO iotDeviceInfoVO : iotDeviceInfoVOS) {
                if (iotDeviceInfoVO.getDeviceCode() == null)iotDeviceInfoVO.setDeviceCode("");
                if (iotDeviceInfoVO.getDeviceName() == null)iotDeviceInfoVO.setDeviceName("");
                if (iotDeviceInfoVO.getModelFactoryName() == null)iotDeviceInfoVO.setModelFactoryName("");
                if (iotDeviceInfoVO.getDeviceModelCode() == null)iotDeviceInfoVO.setDeviceModelCode("");
                if (iotDeviceInfoVO.getDeviceAddress() == null)iotDeviceInfoVO.setDeviceAddress("");
                if (iotDeviceInfoVO.getDeviceLongitude() == null || iotDeviceInfoVO.getDeviceLatitude() == null){
                    iotDeviceInfoVO.setDeviceLongitude("");
                }else {
                    iotDeviceInfoVO.setDeviceLongitude(format.format(Double.valueOf(iotDeviceInfoVO.getDeviceLongitude()))+ Constants.COMMA+format.format(Double.valueOf(iotDeviceInfoVO.getDeviceLatitude())));
                }
                for (IotRelDeviceVo iotRelDeviceVo : iotDeviceInfoVO.getIotDeviceList()) {
                    if (iotRelDeviceVo.getModelCode() == null)iotRelDeviceVo.setModelCode("");
                    if (iotRelDeviceVo.getLocation() == null)iotRelDeviceVo.setLocation("");
                    if (iotRelDeviceVo.getDeviceCode() == null)iotRelDeviceVo.setDeviceCode("");
                    if (iotRelDeviceVo.getModelCodeName() == null)iotRelDeviceVo.setModelCodeName("");
                    if (iotRelDeviceVo.getDeviceName() == null)iotRelDeviceVo.setDeviceName("");
                    String deviceLabelString = "";
                    //摄像机标签
                    if (!CollectionUtils.isEmpty(iotRelDeviceVo.getDeviceLabel())){
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < iotRelDeviceVo.getDeviceLabel().size(); i++) {
                            if (i+1 == iotRelDeviceVo.getDeviceLabel().size()){
                                stringBuffer.append(iotRelDeviceVo.getDeviceLabel().get(i));
                            }else{
                                stringBuffer.append(iotRelDeviceVo.getDeviceLabel().get(i)).append(Constants.COMMA);
                            }
                        }
                        deviceLabelString = stringBuffer.toString();
                        iotRelDeviceVo.setDeviceLabelString(deviceLabelString);
                    }
                    if (iotRelDeviceVo.getHeight() == null){
                        iotRelDeviceVo.setHeight("");
                    }else {
                        iotRelDeviceVo.setHeight(iotRelDeviceVo.getHeight()+"m");
                    }
                    if (iotRelDeviceVo.getLinkageAlgorithm() == null)iotRelDeviceVo.setLinkageAlgorithm("");
                    if(!StringUtils.isEmpty(iotRelDeviceVo.getIsSecondIdentify())){
                        if (Constants.ONE.equals(iotRelDeviceVo.getIsSecondIdentify())){
                            iotRelDeviceVo.setIsSecondIdentify(Constants.RESULT_PARAM_YES);
                        }else if (Constants.ZERO.equals(iotRelDeviceVo.getIsSecondIdentify())){
                            iotRelDeviceVo.setIsSecondIdentify(Constants.RESULT_PARAM_NO);
                        }
                    }else {
                        iotRelDeviceVo.setIsSecondIdentify("");
                    }
                    if(!StringUtils.isEmpty(iotRelDeviceVo.getSecondIdentifyResult())){
                        if (Constants.ZERO.equals(iotRelDeviceVo.getSecondIdentifyResult())){
                            iotRelDeviceVo.setSecondIdentifyResult(Constants.RESULT_PARAM_FALSE);
                        }else if (Constants.ONE.equals(iotRelDeviceVo.getSecondIdentifyResult())){
                            iotRelDeviceVo.setSecondIdentifyResult(Constants.RESULT_PARAM_TRUE);
                        }else if (Constants.TWO.equals(iotRelDeviceVo.getSecondIdentifyResult())){
                            iotRelDeviceVo.setSecondIdentifyResult(Constants.SECOND_JUDGE_RESULT_3);
                        }
                    }else {
                        iotRelDeviceVo.setSecondIdentifyResult("");
                    }
                    if (iotRelDeviceVo.getLongitude() == null || iotRelDeviceVo.getLatitude() == null){
                        iotRelDeviceVo.setLongitude("");
                    }else {
                        iotRelDeviceVo.setLongitude(format.format(Double.valueOf(iotRelDeviceVo.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(iotRelDeviceVo.getLatitude())));
                    }

                }
            }
            iotDeviceSize = iotDeviceInfoVOS.size();
        }
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_INTERCEPTNAME)) {
                            if (dto.getExportSource() ==2 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_INTERCEPTNAME, interceptName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains("{{deviceCode1}}")) {
                            if (iotDeviceSize >= 1){
                                fullText = fullText.replace("{{deviceCode1}}", iotDeviceInfoVOS.get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceName1}}")) {
                            if (iotDeviceSize >= 1){
                                fullText = fullText.replace("{{deviceName1}}", iotDeviceInfoVOS.get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{deviceFactory1}}")) {
                            if (iotDeviceSize >= 1){
                                fullText = fullText.replace("{{deviceFactory1}}", iotDeviceInfoVOS.get(0).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceModelCode1}}")) {
                            if (iotDeviceSize >= 1){
                                fullText = fullText.replace("{{deviceModelCode1}}", iotDeviceInfoVOS.get(0).getDeviceModelCode());
                            }
                        }
                        if (fullText.contains("{{deviceLotAndLat1}}")) {
                            if (iotDeviceSize >= 1){
                                fullText = fullText.replace("{{deviceLotAndLat1}}", iotDeviceInfoVOS.get(0).getDeviceLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceAddress1}}")) {
                            if (iotDeviceSize >= 1){
                                fullText = fullText.replace("{{deviceAddress1}}", iotDeviceInfoVOS.get(0).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify1}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() < 1 && !StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace("{{isSecondIdentify1}}", "否");
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceCode11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceName11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceFactory11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceFactory11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceHigh11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceLotAndLat11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress11}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceAddress11}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(0).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceCode12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceName12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceFactory12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceHigh12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceLotAndLat12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress12}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceAddress12}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(1).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceCode13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceName13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceFactory13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceFactory13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceHigh13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceLotAndLat13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress13}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceAddress13}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(2).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceCode14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceName14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceFactory14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceHigh14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceLotAndLat14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress14}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceAddress14}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(3).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceCode15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceName15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceFactory15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceHigh15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceLotAndLat15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress15}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceAddress15}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(4).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceCode16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceName16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceFactory16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceHigh16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceLotAndLat16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress16}}")) {
                            if (iotDeviceSize >= 1 && iotDeviceInfoVOS.get(0).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceAddress16}}", iotDeviceInfoVOS.get(0).getIotDeviceList().get(5).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceCode2}}")) {
                            if (iotDeviceSize >= 2){
                                fullText = fullText.replace("{{deviceCode2}}", iotDeviceInfoVOS.get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceName2}}")) {
                            if (iotDeviceSize >= 2){
                                fullText = fullText.replace("{{deviceName2}}", iotDeviceInfoVOS.get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{deviceFactory2}}")) {
                            if (iotDeviceSize >= 2){
                                fullText = fullText.replace("{{deviceFactory2}}", iotDeviceInfoVOS.get(1).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceModelCode2}}")) {
                            if (iotDeviceSize >= 2){
                                fullText = fullText.replace("{{deviceModelCode2}}", iotDeviceInfoVOS.get(1).getDeviceModelCode());
                            }
                        }
                        if (fullText.contains("{{deviceLotAndLat2}}")) {
                            if (iotDeviceSize >= 2){
                                fullText = fullText.replace("{{deviceLotAndLat2}}", iotDeviceInfoVOS.get(1).getDeviceLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceAddress2}}")) {
                            if (iotDeviceSize >= 2){
                                fullText = fullText.replace("{{deviceAddress2}}", iotDeviceInfoVOS.get(1).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify2}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() < 1 && !StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace("{{isSecondIdentify2}}", "否");
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceCode21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceName21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceFactory21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceHigh21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceLotAndLat21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress21}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceAddress21}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(0).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceCode22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceName22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceName22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceFactory22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceHigh22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceLotAndLat22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress22}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceAddress22}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(1).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceCode23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceName23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceFactory23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceHigh23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceLotAndLat23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress23}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceAddress23}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(2).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceCode24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceName24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceFactory24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceHigh24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceLotAndLat24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress24}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceAddress24}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(3).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceCode25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceName25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceFactory25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceFactory25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceHigh25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceLotAndLat25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress25}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceAddress25}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(4).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceCode26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceName26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceFactory26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceHigh26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceLotAndLat26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress26}}")) {
                            if (iotDeviceSize >= 2 && iotDeviceInfoVOS.get(1).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceAddress26}}", iotDeviceInfoVOS.get(1).getIotDeviceList().get(5).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceCode3}}")) {
                            if (iotDeviceSize >= 3){
                                fullText = fullText.replace("{{deviceCode3}}", iotDeviceInfoVOS.get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceName3}}")) {
                            if (iotDeviceSize >= 3){
                                fullText = fullText.replace("{{deviceName3}}", iotDeviceInfoVOS.get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{deviceFactory3}}")) {
                            if (iotDeviceSize >= 3){
                                fullText = fullText.replace("{{deviceFactory3}}", iotDeviceInfoVOS.get(2).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceModelCode3}}")) {
                            if (iotDeviceSize >= 3){
                                fullText = fullText.replace("{{deviceModelCode3}}", iotDeviceInfoVOS.get(2).getDeviceModelCode());
                            }
                        }
                        if (fullText.contains("{{deviceLotAndLat3}}")) {
                            if (iotDeviceSize >= 3){
                                fullText = fullText.replace("{{deviceLotAndLat3}}", iotDeviceInfoVOS.get(2).getDeviceLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceAddress3}}")) {
                            if (iotDeviceSize >= 3){
                                fullText = fullText.replace("{{deviceAddress3}}", iotDeviceInfoVOS.get(2).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify3}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() < 1 && !StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace("{{isSecondIdentify3}}", "否");
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceCode31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceName31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceFactory31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceHigh31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceLotAndLat31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress31}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceAddress31}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(0).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceCode32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceName32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceFactory32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceHigh32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceLotAndLat32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress32}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceAddress32}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(1).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceCode33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceName33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceFactory33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceHigh33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceLotAndLat33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress33}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceAddress33}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(2).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceCode34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceName34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceFactory34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceHigh34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceLotAndLat34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress34}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceAddress34}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(3).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceCode35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceName35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceFactory35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceHigh35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceLotAndLat35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress35}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceAddress35}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(4).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceCode36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceName36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceFactory36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceHigh36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceLotAndLat36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress36}}")) {
                            if (iotDeviceSize >= 3 && iotDeviceInfoVOS.get(2).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceAddress36}}", iotDeviceInfoVOS.get(2).getIotDeviceList().get(5).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceCode4}}")) {
                            if (iotDeviceSize >= 4){
                                fullText = fullText.replace("{{deviceCode4}}", iotDeviceInfoVOS.get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceName4}}")) {
                            if (iotDeviceSize >= 4){
                                fullText = fullText.replace("{{deviceName4}}", iotDeviceInfoVOS.get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{deviceFactory4}}")) {
                            if (iotDeviceSize >= 4){
                                fullText = fullText.replace("{{deviceFactory4}}", iotDeviceInfoVOS.get(3).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceModelCode4}}")) {
                            if (iotDeviceSize >= 4){
                                fullText = fullText.replace("{{deviceModelCode4}}", iotDeviceInfoVOS.get(3).getDeviceModelCode());
                            }
                        }
                        if (fullText.contains("{{deviceLotAndLat4}}")) {
                            if (iotDeviceSize >= 4){
                                fullText = fullText.replace("{{deviceLotAndLat4}}", iotDeviceInfoVOS.get(3).getDeviceLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceAddress4}}")) {
                            if (iotDeviceSize >= 4){
                                fullText = fullText.replace("{{deviceAddress4}}", iotDeviceInfoVOS.get(3).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify4}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() < 1 && !StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace("{{isSecondIdentify4}}", "否");
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceCode41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceName41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceFactory41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceHigh41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceLotAndLat41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress41}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceAddress41}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(0).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceCode42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceName42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceFactory42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceHigh42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceLotAndLat42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress42}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceAddress42}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(1).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceCode43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceName43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceFactory43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceHigh43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceLotAndLat43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress43}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceAddress43}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(2).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceCode44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceName44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceFactory44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceHigh44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceLotAndLat44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress44}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceAddress44}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(3).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceCode45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceName45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceFactory45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceHigh45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceLotAndLat45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress45}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceAddress45}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(4).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceCode46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceName46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceFactory46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceHigh46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceLotAndLat46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress46}}")) {
                            if (iotDeviceSize >= 4 && iotDeviceInfoVOS.get(3).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceAddress46}}", iotDeviceInfoVOS.get(3).getIotDeviceList().get(5).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceCode5}}")) {
                            if (iotDeviceSize >= 5){
                                fullText = fullText.replace("{{deviceCode5}}", iotDeviceInfoVOS.get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceName5}}")) {
                            if (iotDeviceSize >= 5){
                                fullText = fullText.replace("{{deviceName5}}", iotDeviceInfoVOS.get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{deviceFactory5}}")) {
                            if (iotDeviceSize >= 5){
                                fullText = fullText.replace("{{deviceFactory5}}", iotDeviceInfoVOS.get(4).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceModelCode5}}")) {
                            if (iotDeviceSize >= 5){
                                fullText = fullText.replace("{{deviceModelCode5}}", iotDeviceInfoVOS.get(4).getDeviceModelCode());
                            }
                        }
                        if (fullText.contains("{{deviceLotAndLat5}}")) {
                            if (iotDeviceSize >= 5){
                                fullText = fullText.replace("{{deviceLotAndLat5}}", iotDeviceInfoVOS.get(4).getDeviceLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{deviceAddress5}}")) {
                            if (iotDeviceSize >= 5){
                                fullText = fullText.replace("{{deviceAddress5}}", iotDeviceInfoVOS.get(4).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify5}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() < 1 && !StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace("{{isSecondIdentify5}}", "否");
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceCode51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceName51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceFactory51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceHigh51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceLotAndLat51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress51}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 1){
                                fullText = fullText.replace("{{relDeviceAddress51}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(0).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceCode52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceName52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceFactory52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceHigh52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceLotAndLat52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress52}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 2){
                                fullText = fullText.replace("{{relDeviceAddress52}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(1).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceCode53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceName53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceFactory53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceHigh53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceLotAndLat53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress53}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 3){
                                fullText = fullText.replace("{{relDeviceAddress53}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(2).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceCode54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceName54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceFactory54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceHigh54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceLotAndLat54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress54}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 4){
                                fullText = fullText.replace("{{relDeviceAddress54}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(3).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceCode55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceName55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains("{{relDeviceFactory55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceFactory55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceHigh55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceLotAndLat55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress55}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 5){
                                fullText = fullText.replace("{{relDeviceAddress55}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(4).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{isSecondIdentify56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{isSecondIdentify56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getIsSecondIdentify());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondIdentifyResult56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getIsSecondIdentify())){
                                if ("是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getIsSecondIdentify())){
                                    fullText = fullText.replace("{{secondIdentifyResult56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getSecondIdentifyResult());
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains("{{linkageAlgorithm56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6 && !StringUtils.isEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getIsSecondIdentify()) && "是".equals(iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getIsSecondIdentify())){
                                fullText = fullText.replace("{{linkageAlgorithm56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getLinkageAlgorithm());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceCode56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceCode56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceName56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceName56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getDeviceName());
                            }
                        }
                        if (fullText.contains("{{relDeviceLabel56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6 && StringUtil.isNotEmpty(iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getDeviceLabelString())){
                                fullText = fullText.replace("{{relDeviceLabel56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceFactory56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceFactory56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getModelCodeName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceHigh56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceHigh56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getHeight());
                            }
                        }
                        if (fullText.contains("{{relDeviceLotAndLat56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceLotAndLat56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getLongitude());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("{{relDeviceAddress56}}")) {
                            if (iotDeviceSize >= 5 && iotDeviceInfoVOS.get(4).getIotDeviceList().size() >= 6){
                                fullText = fullText.replace("{{relDeviceAddress56}}", iotDeviceInfoVOS.get(4).getIotDeviceList().get(5).getLocation());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        // 删除原始段落中的所有运行
                            while (paragraph.getRuns().size() > 0) {
                                paragraph.removeRun(0);
                            }
                            if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                                // 创建一个新运行，并将更新后的文本添加到该运行中
                                XWPFRun newRun = paragraph.createRun();
                                newRun.setText(fullText, 0);
                                newRun.setFontSize(12);
                                newRun.setFontFamily("宋体");
                            } else {
                                // 创建一个新运行，并将更新后的文本添加到该运行中
                                XWPFRun newRun1 = paragraph.createRun();
                                newRun1.setText(fullText, 0);
                                newRun1.setFontSize(12);
                                newRun1.setBold(true);
                                newRun1.setFontFamily("宋体");
                            }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }


    //事件详情导出 修改卫星告警表格内容
    public void replaceTableInfoNine(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //告警压制原因
        String interceptName = StringUtils.isEmpty(order.getInterceptName())?"":order.getInterceptName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //卫星设备名称
        String deviceName = StringUtils.isEmpty(order.getDeviceName())?"":order.getDeviceName();
        //下垫面类型
        String underlyingSurfaceType = StringUtils.isEmpty(order.getUnderlyingSurfaceType())?"":order.getUnderlyingSurfaceType();
        //火区面积
        String fireArea = StringUtils.isEmpty(order.getFireArea())?"":order.getFireArea();
        //置信度
        String confidenceLevel = StringUtils.isEmpty(order.getConfidenceLevel())?"":order.getConfidenceLevel();
        //二次识别结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondIdentifyResult()) ? "" : order.getSecondIdentifyResult();
        //是否二次识别
        String isSecondIdentify = StringUtils.isEmpty(order.getIsSecondIdentify()) ? "" : order.getIsSecondIdentify();
        //联动算法名称
        String linkageAlgorithm = StringUtils.isEmpty(order.getLinkageAlgorithm()) ? "" : order.getLinkageAlgorithm();
        //联动卫星摄像机
        List<RelDeviceVo> relDeviceVoList = CollectionUtils.isEmpty(order.getRelDeviceList()) ? null : order.getRelDeviceList();
        int relDeviceSize = 0;
        if (!CollectionUtils.isEmpty(relDeviceVoList)){
            for (RelDeviceVo relDeviceVo : relDeviceVoList) {
                if (relDeviceVo.getDeviceCode() == null)relDeviceVo.setDeviceCode("");
                if (relDeviceVo.getDeviceName() == null)relDeviceVo.setDeviceName("");
                if (relDeviceVo.getModelFactoryName() == null)relDeviceVo.setModelFactoryName("");
                if (relDeviceVo.getHeight() == null)relDeviceVo.setHeight("");
                if (relDeviceVo.getDeviceLotAndLat() == null)relDeviceVo.setDeviceLotAndLat("");
                if (relDeviceVo.getDeviceAddress() == null)relDeviceVo.setDeviceAddress("");
                if (relDeviceVo.getSnapShotTime() == null)relDeviceVo.setSnapShotTime("");
                if (relDeviceVo.getDeviceLabel() == null)relDeviceVo.setDeviceLabelString("");
                String deviceLabelString = "";
                //摄像机标签
                if (!CollectionUtils.isEmpty(relDeviceVo.getDeviceLabel())){
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < relDeviceVo.getDeviceLabel().size(); i++) {
                        if (i+1 == relDeviceVo.getDeviceLabel().size()){
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i));
                        }else{
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i)).append(Constants.COMMA);
                        }
                    }
                    deviceLabelString = stringBuffer.toString();
                    relDeviceVo.setDeviceLabelString(deviceLabelString);
                }
            }
            relDeviceSize = relDeviceVoList.size();
        }
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INTERCEPTNAME)) {
                            if (dto.getExportSource() ==2 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_INTERCEPTNAME, interceptName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_DEVICECODE)) {
                            fullText = fullText.replace(Constants.EXP_DEVICECODE, deviceName);
                        }
                        if (fullText.contains("{{underlyingSurfaceType}}")) {
                            fullText = fullText.replace("{{underlyingSurfaceType}}", underlyingSurfaceType);
                        }
                        if (fullText.contains("{{fireArea}}")) {
                            fullText = fullText.replace("{{fireArea}}", fireArea);
                        }
                        if (fullText.contains(Constants.EXP_CONFIDENCELEVEL)) {
                            fullText = fullText.replace(Constants.EXP_CONFIDENCELEVEL, confidenceLevel);
                        }
                        if (fullText.contains(Constants.EXP_ISSECONDIDENTIFY)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_ISSECONDIDENTIFY, isSecondIdentify);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_SECONDIDENTIFYRESULT)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                if ("是".equals(isSecondIdentify)){
                                    fullText = fullText.replace(Constants.EXP_SECONDIDENTIFYRESULT, secondJudgeResult);
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains(Constants.EXP_LINKAGEALGORITHM)) {
                            if (!StringUtils.isEmpty(isSecondIdentify) && "是".equals(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_LINKAGEALGORITHM, linkageAlgorithm);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE1, relDeviceVoList.get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME1, relDeviceVoList.get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL1)) {
                            if (relDeviceSize >= 1 && StringUtil.isNotEmpty(relDeviceVoList.get(0).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL1, relDeviceVoList.get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY1, relDeviceVoList.get(0).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH1, relDeviceVoList.get(0).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT1, relDeviceVoList.get(0).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME1, relDeviceVoList.get(0).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS1, relDeviceVoList.get(0).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE2, relDeviceVoList.get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME2, relDeviceVoList.get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL2)) {
                            if (relDeviceSize >= 2 && StringUtil.isNotEmpty(relDeviceVoList.get(1).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL2, relDeviceVoList.get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY2, relDeviceVoList.get(1).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH2, relDeviceVoList.get(1).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT2, relDeviceVoList.get(1).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME2, relDeviceVoList.get(1).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS2, relDeviceVoList.get(1).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE3, relDeviceVoList.get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME3, relDeviceVoList.get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL3)) {
                            if (relDeviceSize >= 3 && StringUtil.isNotEmpty(relDeviceVoList.get(2).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL3, relDeviceVoList.get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY3, relDeviceVoList.get(2).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH3, relDeviceVoList.get(2).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT3, relDeviceVoList.get(2).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME3, relDeviceVoList.get(2).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS3, relDeviceVoList.get(2).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE4, relDeviceVoList.get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME4, relDeviceVoList.get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL4)) {
                            if (relDeviceSize >= 4 && StringUtil.isNotEmpty(relDeviceVoList.get(3).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL4, relDeviceVoList.get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY4, relDeviceVoList.get(3).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH4, relDeviceVoList.get(3).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT4, relDeviceVoList.get(3).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME4, relDeviceVoList.get(3).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS4, relDeviceVoList.get(3).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE5, relDeviceVoList.get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME5, relDeviceVoList.get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL5)) {
                            if (relDeviceSize >= 5 && StringUtil.isNotEmpty(relDeviceVoList.get(4).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL5, relDeviceVoList.get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY5, relDeviceVoList.get(4).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH5, relDeviceVoList.get(4).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT5, relDeviceVoList.get(4).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME5, relDeviceVoList.get(4).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS5, relDeviceVoList.get(4).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE6, relDeviceVoList.get(5).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME6, relDeviceVoList.get(5).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL6, relDeviceVoList.get(5).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY6, relDeviceVoList.get(5).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH6, relDeviceVoList.get(5).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT6, relDeviceVoList.get(5).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME6, relDeviceVoList.get(5).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS6)) {
                            if (relDeviceSize >= 6){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS6, relDeviceVoList.get(5).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE7, relDeviceVoList.get(6).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME7, relDeviceVoList.get(6).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL7, relDeviceVoList.get(6).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY7, relDeviceVoList.get(6).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH7, relDeviceVoList.get(6).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT7, relDeviceVoList.get(6).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME7, relDeviceVoList.get(6).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS7)) {
                            if (relDeviceSize >= 7){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS7, relDeviceVoList.get(6).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE8, relDeviceVoList.get(7).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME8, relDeviceVoList.get(7).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL8, relDeviceVoList.get(7).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY8, relDeviceVoList.get(7).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH8, relDeviceVoList.get(7).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT8, relDeviceVoList.get(7).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME8, relDeviceVoList.get(7).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS8)) {
                            if (relDeviceSize >= 8){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS8, relDeviceVoList.get(7).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE9, relDeviceVoList.get(8).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME9, relDeviceVoList.get(8).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL9, relDeviceVoList.get(8).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY9, relDeviceVoList.get(8).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH9, relDeviceVoList.get(8).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT9, relDeviceVoList.get(8).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME9, relDeviceVoList.get(8).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS9)) {
                            if (relDeviceSize >= 9){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS9, relDeviceVoList.get(8).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE10, relDeviceVoList.get(9).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME10, relDeviceVoList.get(9).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL10, relDeviceVoList.get(9).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY10, relDeviceVoList.get(9).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH10, relDeviceVoList.get(9).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT10, relDeviceVoList.get(9).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME10, relDeviceVoList.get(9).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS10)) {
                            if (relDeviceSize >= 10){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS10, relDeviceVoList.get(9).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        } else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来的包含"{{alarmDistance}}"文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }

    //事件详情导出 修改雷达告警表格内容
    public void replaceTableInfoTen(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
         //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //雷达设备编号
        String deviceCode = StringUtils.isEmpty(order.getDeviceCode())?"":order.getDeviceCode();
        //雷达设备名称
        String deviceName = StringUtils.isEmpty(order.getDeviceName())?"":order.getDeviceName();
        //雷达设备厂家
        String modelName = StringUtils.isEmpty(order.getModelFactoryName())?"":order.getModelFactoryName();
        //雷达设备经纬度
        String videoLotAndLat = StringUtils.isEmpty(order.getDeviceLotAndLat())?"":order.getDeviceLotAndLat();
        //雷达设备地址
        String deviceAddress =StringUtils.isEmpty(order.getDeviceAddress())?"":order.getDeviceAddress();
        //二次识别结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondIdentifyResult()) ? "" : order.getSecondIdentifyResult();
        //是否二次识别
        String isSecondIdentify = StringUtils.isEmpty(order.getIsSecondIdentify()) ? "" : order.getIsSecondIdentify();
        //联动算法名称
        String linkageAlgorithm = StringUtils.isEmpty(order.getLinkageAlgorithm()) ? "" : order.getLinkageAlgorithm();
        //联动雷达摄像机
        List<RelDeviceVo> relDeviceVoList = CollectionUtils.isEmpty(order.getRelDeviceList()) ? null : order.getRelDeviceList();
        int relDeviceSize = 0;
        if (!CollectionUtils.isEmpty(relDeviceVoList)){
            for (RelDeviceVo relDeviceVo : relDeviceVoList) {
                if (relDeviceVo.getDeviceCode() == null)relDeviceVo.setDeviceCode("");
                if (relDeviceVo.getDeviceName() == null)relDeviceVo.setDeviceName("");
                if (relDeviceVo.getModelFactoryName() == null)relDeviceVo.setModelFactoryName("");
                if (relDeviceVo.getHeight() == null)relDeviceVo.setHeight("");
                if (relDeviceVo.getDeviceLotAndLat() == null)relDeviceVo.setDeviceLotAndLat("");
                if (relDeviceVo.getDeviceAddress() == null)relDeviceVo.setDeviceAddress("");
                if (relDeviceVo.getDeviceLabel() == null)relDeviceVo.setDeviceLabelString("");
                String deviceLabelString = "";
                //摄像机标签
                if (!CollectionUtils.isEmpty(relDeviceVo.getDeviceLabel())){
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < relDeviceVo.getDeviceLabel().size(); i++) {
                        if (i+1 == relDeviceVo.getDeviceLabel().size()){
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i));
                        }else{
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i)).append(Constants.COMMA);
                        }
                    }
                    deviceLabelString = stringBuffer.toString();
                    relDeviceVo.setDeviceLabelString(deviceLabelString);
                }
            }
            relDeviceSize = relDeviceVoList.size();
        }
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_DEVICECODE)) {
                            fullText = fullText.replace(Constants.EXP_DEVICECODE, deviceCode);
                        }
                        if (fullText.contains(Constants.EXP_DEVICENAME)) {
                            fullText = fullText.replace(Constants.EXP_DEVICENAME, deviceName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEFACTORY)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEFACTORY, modelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_DEVICELOTANDLAT, videoLotAndLat);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEADDRESS, deviceAddress);
                        }
                        if (fullText.contains(Constants.EXP_ISSECONDIDENTIFY)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_ISSECONDIDENTIFY, isSecondIdentify);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_SECONDIDENTIFYRESULT)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                if ("是".equals(isSecondIdentify)){
                                    fullText = fullText.replace(Constants.EXP_SECONDIDENTIFYRESULT, secondJudgeResult);
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains(Constants.EXP_LINKAGEALGORITHM)) {
                            if (!StringUtils.isEmpty(isSecondIdentify) && "是".equals(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_LINKAGEALGORITHM, linkageAlgorithm);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE1, relDeviceVoList.get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME1, relDeviceVoList.get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL1)) {
                            if (relDeviceSize >= 1 && StringUtil.isNotEmpty(relDeviceVoList.get(0).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL1, relDeviceVoList.get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY1, relDeviceVoList.get(0).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH1, relDeviceVoList.get(0).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT1, relDeviceVoList.get(0).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS1, relDeviceVoList.get(0).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        } else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来的包含"{{alarmDistance}}"文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }

    //修改摄像机告警 表格数据
    public void replaceTableInfoFour(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //告警压制原因
        String interceptName = StringUtils.isEmpty(order.getInterceptName())?"":order.getInterceptName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //事发地点到摄像机距离
        String incidentDistance = StringUtils.isEmpty(order.getIncidentDistance())?"":order.getIncidentDistance();
        //摄像机俯仰角
        String devicePitch = StringUtils.isEmpty(dto.getDevicePitch())?"":dto.getDevicePitch();
        //摄像机水平角
        String deviceHorizontal = StringUtils.isEmpty(dto.getDeviceHorizontal())?"":dto.getDeviceHorizontal();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //摄像机编码
        String deviceCode = StringUtils.isEmpty(order.getDeviceCode())?"":order.getDeviceCode();
        //摄像机名称
        String deviceName = StringUtils.isEmpty(order.getDeviceName())?"":order.getDeviceName();
        String deviceLabelString = "";
        //摄像机标签
        if (!CollectionUtils.isEmpty(order.getDeviceLabel())){
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < order.getDeviceLabel().size(); i++) {
                if (i+1 == order.getDeviceLabel().size()){
                    stringBuffer.append(order.getDeviceLabel().get(i));
                }else{
                    stringBuffer.append(order.getDeviceLabel().get(i)).append(Constants.COMMA);
                }
            }
            deviceLabelString = stringBuffer.toString();
        }
        //通道名称
        String channelName = StringUtils.isEmpty(order.getChannelName())?"":order.getChannelName();
        //摄像机厂家
        String modelName = StringUtils.isEmpty(order.getModelFactoryName())?"":order.getModelFactoryName();
        //摄像机经纬度
        String videoLotAndLat = StringUtils.isEmpty(order.getDeviceLotAndLat())?"":order.getDeviceLotAndLat();
        //摄像机挂高
        String height = StringUtils.isEmpty(order.getHeight())?"":order.getHeight();
        //摄像机地址
        String deviceAddress =StringUtils.isEmpty(order.getDeviceAddress())?"":order.getDeviceAddress();
        //置信度
        String confidenceLevel = StringUtils.isEmpty(order.getConfidenceLevel())?"":order.getConfidenceLevel();
        //水平方位角
        String horiAzimuthAngle = StringUtils.isEmpty(dto.getHoriAzimuthAngle())?"":dto.getHoriAzimuthAngle();
        //是否二次研判
        String isSecondJudge = StringUtils.isEmpty(order.getIsSecondJudge())?"":order.getIsSecondJudge();
        //二次研判结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondJudgeResult())?"":order.getSecondJudgeResult();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INTERCEPTNAME)) {
                            if (dto.getExportSource() ==2 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_INTERCEPTNAME, interceptName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE,alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID,warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL,levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME,alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE,sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE,warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON,alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS,address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT,lotAndLat);
                        }
                        if (fullText.contains("事发地点到摄像机距离")) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_ALARMDISTANCE)) {
                            if (!StringUtils.isEmpty(incidentDistance)){
                                fullText = fullText.replace(Constants.EXP_ALARMDISTANCE,incidentDistance);
                            }else {
                                xwpfParagraphList.add(paragraph);
                                xwpfParagraphList.add(xwpfParagraph);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMPITCHANGLE)) {
                            if (StringUtil.isNotEmpty(devicePitch)){
                                fullText = fullText.replace(Constants.EXP_ALARMPITCHANGLE,devicePitch);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMHORANGLE)) {
                            if (StringUtil.isNotEmpty(deviceHorizontal)){
                                fullText = fullText.replace(Constants.EXP_ALARMHORANGLE,deviceHorizontal);
                            }
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID,gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER,keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE,description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK,remark);
                        }
                        if (fullText.contains(Constants.EXP_DEVICECODE)) {
                            fullText = fullText.replace(Constants.EXP_DEVICECODE,deviceCode);
                        }
                        if (fullText.contains(Constants.EXP_DEVICENAME)) {
                            fullText = fullText.replace(Constants.EXP_DEVICENAME,deviceName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELABEL)) {
                            if (StringUtil.isNotEmpty(deviceLabelString)){
                                fullText = fullText.replace(Constants.EXP_DEVICELABEL,deviceLabelString);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_CHANNELNAME)) {
                            fullText = fullText.replace(Constants.EXP_CHANNELNAME,channelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEFACTORY)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEFACTORY,modelName);
                        }
                        if (fullText.contains(Constants.EXP_DEVICELOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_DEVICELOTANDLAT,videoLotAndLat);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEHIGH)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEHIGH,height);
                        }
                        if (fullText.contains(Constants.EXP_DEVICEADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_DEVICEADDRESS,deviceAddress);
                        }
                        if (fullText.contains(Constants.EXP_CONFIDENCELEVEL)) {
                            fullText = fullText.replace(Constants.EXP_CONFIDENCELEVEL,confidenceLevel);
                        }
                        if (fullText.contains("水平方位角")) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_HORIZONTALAZIMUTH)) {
                            if (StringUtil.isNotEmpty(horiAzimuthAngle)){
                                fullText = fullText.replace(Constants.EXP_HORIZONTALAZIMUTH,horiAzimuthAngle);
                            }else {
                                xwpfParagraphList.add(paragraph);
                                xwpfParagraphList.add(xwpfParagraph);
                            }
                        }
                        if (fullText.contains("{{isSecondJudge}}")) {
                            if (!StringUtils.isEmpty(isSecondJudge)){
                                fullText = fullText.replace("{{isSecondJudge}}",isSecondJudge);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains("二次研判结果")) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains("{{secondJudgeResult}}")) {
                            if (!StringUtils.isEmpty(isSecondJudge)){
                                if ("是".equals(isSecondJudge)){
                                    fullText = fullText.replace("{{secondJudgeResult}}",secondJudgeResult);
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())){
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        }else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }

    //修改卫星告警 - 矿产卫片
    public void replaceTableInfoFourMinerals(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //图斑编号
        String spotNo = StringUtils.isEmpty(order.getSpotNo())?"-":order.getSpotNo();
        //城市名称
        String cityName = StringUtils.isEmpty(order.getCityName())?"-":order.getCityName();
        //区县名称
        String countyName = StringUtils.isEmpty(order.getCountyName())?"-":order.getCountyName();
        //乡镇街道
        String townName = StringUtils.isEmpty(order.getTownName())?"-":order.getTownName();
        //村名称
        String villageName = StringUtils.isEmpty(order.getVillageName()) ? "-" : order.getVillageName();
        //卫片来源
        String regionSource = StringUtils.isEmpty(order.getRegionSource()) ? "-" : order.getRegionSource();
        //图斑面积
        String spotArea = StringUtils.isEmpty(order.getSpotArea()) ? "-" : order.getSpotArea();
        //图斑周长
        String spotPerimeter = StringUtils.isEmpty(order.getSpotPerimeter()) ? "-" : order.getSpotPerimeter();
        //二次识别结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondIdentifyResult()) ? "" : order.getSecondIdentifyResult();
        //是否二次识别
        String isSecondIdentify = StringUtils.isEmpty(order.getIsSecondIdentify()) ? "" : order.getIsSecondIdentify();
        //联动算法名称
        String linkageAlgorithm = StringUtils.isEmpty(order.getLinkageAlgorithm()) ? "" : order.getLinkageAlgorithm();
        //联动卫星摄像机
        List<RelDeviceVo> relDeviceVoList = CollectionUtils.isEmpty(order.getRelDeviceList()) ? null : order.getRelDeviceList();
        int relDeviceSize = 0;
        if (!CollectionUtils.isEmpty(relDeviceVoList)){
            for (RelDeviceVo relDeviceVo : relDeviceVoList) {
                if (relDeviceVo.getDeviceCode() == null)relDeviceVo.setDeviceCode("");
                if (relDeviceVo.getDeviceName() == null)relDeviceVo.setDeviceName("");
                if (relDeviceVo.getModelFactoryName() == null)relDeviceVo.setModelFactoryName("");
                if (relDeviceVo.getHeight() == null)relDeviceVo.setHeight("");
                if (relDeviceVo.getDeviceLotAndLat() == null)relDeviceVo.setDeviceLotAndLat("");
                if (relDeviceVo.getDeviceAddress() == null)relDeviceVo.setDeviceAddress("");
                if (relDeviceVo.getSnapShotTime() == null)relDeviceVo.setSnapShotTime("");
                if (relDeviceVo.getDeviceLabel() == null)relDeviceVo.setDeviceLabelString("");
                String deviceLabelString = "";
                //摄像机标签
                if (!CollectionUtils.isEmpty(relDeviceVo.getDeviceLabel())){
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < relDeviceVo.getDeviceLabel().size(); i++) {
                        if (i+1 == relDeviceVo.getDeviceLabel().size()){
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i));
                        }else{
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i)).append(Constants.COMMA);
                        }
                    }
                    deviceLabelString = stringBuffer.toString();
                    relDeviceVo.setDeviceLabelString(deviceLabelString);
                }

            }
            relDeviceSize = relDeviceVoList.size();
        }
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_SPOTNO)) {
                            fullText = fullText.replace(Constants.EXP_SPOTNO, spotNo);
                        }
                        if (fullText.contains(Constants.EXP_CITYNAME)) {
                            fullText = fullText.replace(Constants.EXP_CITYNAME, cityName);
                        }
                        if (fullText.contains(Constants.EXP_COUNTYNAME)) {
                            fullText = fullText.replace(Constants.EXP_COUNTYNAME, countyName);
                        }
                        if (fullText.contains(Constants.EXP_TOWNNAME)) {
                            fullText = fullText.replace(Constants.EXP_TOWNNAME, townName);
                        }
                        if (fullText.contains(Constants.EXP_VILLAGENAME)) {
                            fullText = fullText.replace(Constants.EXP_VILLAGENAME, villageName);
                        }
                        if (fullText.contains(Constants.EXP_REGIONSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_REGIONSOURCE, regionSource);
                        }
                        if (fullText.contains(Constants.EXP_SPOTAREA)) {
                            fullText = fullText.replace(Constants.EXP_SPOTAREA, spotArea);
                        }
                        if (fullText.contains(Constants.EXP_SPOTPERIMETER)) {
                            fullText = fullText.replace(Constants.EXP_SPOTPERIMETER, spotPerimeter);
                        }
                        if (fullText.contains(Constants.EXP_ISSECONDIDENTIFY)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_ISSECONDIDENTIFY, isSecondIdentify);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_SECONDIDENTIFYRESULT)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                if ("是".equals(isSecondIdentify)){
                                    fullText = fullText.replace(Constants.EXP_SECONDIDENTIFYRESULT, secondJudgeResult);
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains(Constants.EXP_LINKAGEALGORITHM)) {
                            if (!StringUtils.isEmpty(isSecondIdentify) && "是".equals(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_LINKAGEALGORITHM, linkageAlgorithm);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE1, relDeviceVoList.get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME1, relDeviceVoList.get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL1)) {
                            if (relDeviceSize >= 1 && StringUtil.isNotEmpty(relDeviceVoList.get(0).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL1, relDeviceVoList.get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY1, relDeviceVoList.get(0).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH1, relDeviceVoList.get(0).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT1, relDeviceVoList.get(0).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME1, relDeviceVoList.get(0).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS1, relDeviceVoList.get(0).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE2, relDeviceVoList.get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME2, relDeviceVoList.get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL2)) {
                            if (relDeviceSize >= 2 && StringUtil.isNotEmpty(relDeviceVoList.get(1).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL2, relDeviceVoList.get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY2, relDeviceVoList.get(1).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH2, relDeviceVoList.get(1).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT2, relDeviceVoList.get(1).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME2, relDeviceVoList.get(1).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS2, relDeviceVoList.get(1).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE3, relDeviceVoList.get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME3, relDeviceVoList.get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL3)) {
                            if (relDeviceSize >= 3 && StringUtil.isNotEmpty(relDeviceVoList.get(2).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL3, relDeviceVoList.get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY3, relDeviceVoList.get(2).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH3, relDeviceVoList.get(2).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT3, relDeviceVoList.get(2).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME3, relDeviceVoList.get(2).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS3, relDeviceVoList.get(2).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE4, relDeviceVoList.get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME4, relDeviceVoList.get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL4)) {
                            if (relDeviceSize >= 4 && StringUtil.isNotEmpty(relDeviceVoList.get(3).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL4, relDeviceVoList.get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY4, relDeviceVoList.get(3).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH4, relDeviceVoList.get(3).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT4, relDeviceVoList.get(3).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME4, relDeviceVoList.get(3).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS4, relDeviceVoList.get(3).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE5, relDeviceVoList.get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME5, relDeviceVoList.get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL5)) {
                            if (relDeviceSize >= 5 && StringUtil.isNotEmpty(relDeviceVoList.get(4).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL5, relDeviceVoList.get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY5, relDeviceVoList.get(4).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH5, relDeviceVoList.get(4).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT5, relDeviceVoList.get(4).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME5, relDeviceVoList.get(4).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS5, relDeviceVoList.get(4).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        } else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }

    //修改卫星告警 - 耕地卫片
    public void replaceTableInfoFourLand(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //图斑编号
        String spotNo = StringUtils.isEmpty(order.getSpotNo())?"-":order.getSpotNo();
        //城市名称
        String cityName = StringUtils.isEmpty(order.getCityName())?"-":order.getCityName();
        //区县名称
        String countyName = StringUtils.isEmpty(order.getCountyName())?"-":order.getCountyName();
        //乡镇街道
        String townName = StringUtils.isEmpty(order.getTownName())?"-":order.getTownName();
        //村名称
        String villageName = StringUtils.isEmpty(order.getVillageName()) ? "-" : order.getVillageName();
        //卫片来源
        String regionSource = StringUtils.isEmpty(order.getRegionSource()) ? "-" : order.getRegionSource();
        //前类型
        String preType = StringUtils.isEmpty(order.getPreType()) ? "-" : order.getPreType();
        //后类型
        String postType = StringUtils.isEmpty(order.getPostType()) ? "-" : order.getPostType();
        //前时相
        String preChronology = StringUtils.isEmpty(order.getPreChronology()) ? "-" : order.getPreChronology();
        //后时相
        String postChronology = StringUtils.isEmpty(order.getPostChronology()) ? "-" : order.getPostChronology();
        //图斑面积
        String spotArea = StringUtils.isEmpty(order.getSpotArea()) ? "-" : order.getSpotArea();
        //图斑周长
        String spotPerimeter = StringUtils.isEmpty(order.getSpotPerimeter()) ? "-" : order.getSpotPerimeter();
        //二次识别结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondIdentifyResult()) ? "" : order.getSecondIdentifyResult();
        //是否二次识别
        String isSecondIdentify = StringUtils.isEmpty(order.getIsSecondIdentify()) ? "" : order.getIsSecondIdentify();
        //联动算法名称
        String linkageAlgorithm = StringUtils.isEmpty(order.getLinkageAlgorithm()) ? "" : order.getLinkageAlgorithm();
        //联动卫星摄像机
        List<RelDeviceVo> relDeviceVoList = CollectionUtils.isEmpty(order.getRelDeviceList()) ? null : order.getRelDeviceList();
        int relDeviceSize = 0;
        if (!CollectionUtils.isEmpty(relDeviceVoList)){
            for (RelDeviceVo relDeviceVo : relDeviceVoList) {
                if (relDeviceVo.getDeviceCode() == null)relDeviceVo.setDeviceCode("");
                if (relDeviceVo.getDeviceName() == null)relDeviceVo.setDeviceName("");
                if (relDeviceVo.getModelFactoryName() == null)relDeviceVo.setModelFactoryName("");
                if (relDeviceVo.getHeight() == null)relDeviceVo.setHeight("");
                if (relDeviceVo.getDeviceLotAndLat() == null)relDeviceVo.setDeviceLotAndLat("");
                if (relDeviceVo.getDeviceAddress() == null)relDeviceVo.setDeviceAddress("");
                if (relDeviceVo.getSnapShotTime() == null)relDeviceVo.setSnapShotTime("");
                if (relDeviceVo.getDeviceLabel() == null)relDeviceVo.setDeviceLabelString("");
                String deviceLabelString = "";
                //摄像机标签
                if (!CollectionUtils.isEmpty(relDeviceVo.getDeviceLabel())){
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < relDeviceVo.getDeviceLabel().size(); i++) {
                        if (i+1 == relDeviceVo.getDeviceLabel().size()){
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i));
                        }else{
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i)).append(Constants.COMMA);
                        }
                    }
                    deviceLabelString = stringBuffer.toString();
                    relDeviceVo.setDeviceLabelString(deviceLabelString);
                }

            }
            relDeviceSize = relDeviceVoList.size();
        }
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_SPOTNO)) {
                            fullText = fullText.replace(Constants.EXP_SPOTNO, spotNo);
                        }
                        if (fullText.contains(Constants.EXP_CITYNAME)) {
                            fullText = fullText.replace(Constants.EXP_CITYNAME, cityName);
                        }
                        if (fullText.contains(Constants.EXP_COUNTYNAME)) {
                            fullText = fullText.replace(Constants.EXP_COUNTYNAME, countyName);
                        }
                        if (fullText.contains(Constants.EXP_TOWNNAME)) {
                            fullText = fullText.replace(Constants.EXP_TOWNNAME, townName);
                        }
                        if (fullText.contains(Constants.EXP_VILLAGENAME)) {
                            fullText = fullText.replace(Constants.EXP_VILLAGENAME, villageName);
                        }
                        if (fullText.contains(Constants.EXP_REGIONSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_REGIONSOURCE, regionSource);
                        }
                        if (fullText.contains("{{preType}}")) {
                            fullText = fullText.replace("{{preType}}", preType);
                        }
                        if (fullText.contains("{{postType}}")) {
                            fullText = fullText.replace("{{postType}}", postType);
                        }
                        if (fullText.contains("{{preChronology}}")) {
                            fullText = fullText.replace("{{preChronology}}", preChronology);
                        }
                        if (fullText.contains("{{postChronology}}")) {
                            fullText = fullText.replace("{{postChronology}}", postChronology);
                        }
                        if (fullText.contains(Constants.EXP_SPOTAREA)) {
                            fullText = fullText.replace(Constants.EXP_SPOTAREA, spotArea);
                        }
                        if (fullText.contains(Constants.EXP_SPOTPERIMETER)) {
                            fullText = fullText.replace(Constants.EXP_SPOTPERIMETER, spotPerimeter);
                        }
                        if (fullText.contains(Constants.EXP_ISSECONDIDENTIFY)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_ISSECONDIDENTIFY, isSecondIdentify);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_SECONDIDENTIFYRESULT)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                if ("是".equals(isSecondIdentify)){
                                    fullText = fullText.replace(Constants.EXP_SECONDIDENTIFYRESULT, secondJudgeResult);
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains(Constants.EXP_LINKAGEALGORITHM)) {
                            if (!StringUtils.isEmpty(isSecondIdentify) && "是".equals(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_LINKAGEALGORITHM, linkageAlgorithm);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE1, relDeviceVoList.get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME1, relDeviceVoList.get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL1)) {
                            if (relDeviceSize >= 1 && StringUtil.isNotEmpty(relDeviceVoList.get(0).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL1, relDeviceVoList.get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY1, relDeviceVoList.get(0).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH1, relDeviceVoList.get(0).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT1, relDeviceVoList.get(0).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME1, relDeviceVoList.get(0).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS1, relDeviceVoList.get(0).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE2, relDeviceVoList.get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME2, relDeviceVoList.get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL2)) {
                            if (relDeviceSize >= 2 && StringUtil.isNotEmpty(relDeviceVoList.get(1).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL2, relDeviceVoList.get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY2, relDeviceVoList.get(1).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH2, relDeviceVoList.get(1).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT2, relDeviceVoList.get(1).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME2, relDeviceVoList.get(1).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS2, relDeviceVoList.get(1).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE3, relDeviceVoList.get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME3, relDeviceVoList.get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL3)) {
                            if (relDeviceSize >= 3 && StringUtil.isNotEmpty(relDeviceVoList.get(2).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL3, relDeviceVoList.get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY3, relDeviceVoList.get(2).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH3, relDeviceVoList.get(2).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT3, relDeviceVoList.get(2).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME3, relDeviceVoList.get(2).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS3, relDeviceVoList.get(2).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE4, relDeviceVoList.get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME4, relDeviceVoList.get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL4)) {
                            if (relDeviceSize >= 4 && StringUtil.isNotEmpty(relDeviceVoList.get(3).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL4, relDeviceVoList.get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY4, relDeviceVoList.get(3).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH4, relDeviceVoList.get(3).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT4, relDeviceVoList.get(3).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME4, relDeviceVoList.get(3).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS4, relDeviceVoList.get(3).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE5, relDeviceVoList.get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME5, relDeviceVoList.get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL5)) {
                            if (relDeviceSize >= 5 && StringUtil.isNotEmpty(relDeviceVoList.get(4).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL5, relDeviceVoList.get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY5, relDeviceVoList.get(4).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH5, relDeviceVoList.get(4).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT5, relDeviceVoList.get(4).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME5, relDeviceVoList.get(4).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS5, relDeviceVoList.get(4).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        } else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }

    //修改卫星告警 - 非农化
    public void replaceTableInfoFourAgro(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //城市名称
        String cityName = StringUtils.isEmpty(order.getCityName())?"-":order.getCityName();
        //区县名称
        String countyName = StringUtils.isEmpty(order.getCountyName())?"-":order.getCountyName();
        //乡镇街道
        String townName = StringUtils.isEmpty(order.getTownName())?"-":order.getTownName();
        //村名称
        String villageName = StringUtils.isEmpty(order.getVillageName()) ? "-" : order.getVillageName();
        //变化类型
        String changeType = StringUtils.isEmpty(order.getChangeType()) ? "-" : order.getChangeType();
        //面积
        String area = StringUtils.isEmpty(order.getArea()) ? "-" : order.getArea();
        //土地利用类型
        String landUseType = StringUtils.isEmpty(order.getLandUseType()) ? "-" : order.getLandUseType();
        //影像时间
        String imageTime = StringUtils.isEmpty(order.getImageTime()) ? "-" : order.getImageTime();
        //是否已质检
        String detectionFlag = StringUtils.isEmpty(order.getDetectionFlag()) ? "-" : order.getDetectionFlag();
        //二次识别结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondIdentifyResult()) ? "" : order.getSecondIdentifyResult();
        //是否二次识别
        String isSecondIdentify = StringUtils.isEmpty(order.getIsSecondIdentify()) ? "" : order.getIsSecondIdentify();
        //联动算法名称
        String linkageAlgorithm = StringUtils.isEmpty(order.getLinkageAlgorithm()) ? "" : order.getLinkageAlgorithm();
        //联动卫星摄像机
        List<RelDeviceVo> relDeviceVoList = CollectionUtils.isEmpty(order.getRelDeviceList()) ? null : order.getRelDeviceList();
        int relDeviceSize = 0;
        if (!CollectionUtils.isEmpty(relDeviceVoList)){
            for (RelDeviceVo relDeviceVo : relDeviceVoList) {
                if (relDeviceVo.getDeviceCode() == null)relDeviceVo.setDeviceCode("");
                if (relDeviceVo.getDeviceName() == null)relDeviceVo.setDeviceName("");
                if (relDeviceVo.getModelFactoryName() == null)relDeviceVo.setModelFactoryName("");
                if (relDeviceVo.getHeight() == null)relDeviceVo.setHeight("");
                if (relDeviceVo.getDeviceLotAndLat() == null)relDeviceVo.setDeviceLotAndLat("");
                if (relDeviceVo.getDeviceAddress() == null)relDeviceVo.setDeviceAddress("");
                if (relDeviceVo.getSnapShotTime() == null)relDeviceVo.setSnapShotTime("");
                if (relDeviceVo.getDeviceLabel() == null)relDeviceVo.setDeviceLabelString("");
                String deviceLabelString = "";
                //摄像机标签
                if (!CollectionUtils.isEmpty(relDeviceVo.getDeviceLabel())){
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < relDeviceVo.getDeviceLabel().size(); i++) {
                        if (i+1 == relDeviceVo.getDeviceLabel().size()){
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i));
                        }else{
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i)).append(Constants.COMMA);
                        }
                    }
                    deviceLabelString = stringBuffer.toString();
                    relDeviceVo.setDeviceLabelString(deviceLabelString);
                }
            }
            relDeviceSize = relDeviceVoList.size();
        }
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_CITYNAME)) {
                            fullText = fullText.replace(Constants.EXP_CITYNAME, cityName);
                        }
                        if (fullText.contains(Constants.EXP_COUNTYNAME)) {
                            fullText = fullText.replace(Constants.EXP_COUNTYNAME, countyName);
                        }
                        if (fullText.contains(Constants.EXP_TOWNNAME)) {
                            fullText = fullText.replace(Constants.EXP_TOWNNAME, townName);
                        }
                        if (fullText.contains(Constants.EXP_VILLAGENAME)) {
                            fullText = fullText.replace(Constants.EXP_VILLAGENAME, villageName);
                        }
                        if (fullText.contains(Constants.EXP_CHANGETYPE)) {
                            fullText = fullText.replace(Constants.EXP_CHANGETYPE, changeType);
                        }
                        if (fullText.contains(Constants.EXP_AREA)) {
                            fullText = fullText.replace(Constants.EXP_AREA, area);
                        }
                        if (fullText.contains(Constants.EXP_LANDUSETYPE)) {
                            fullText = fullText.replace(Constants.EXP_LANDUSETYPE, landUseType);
                        }
                        if (fullText.contains(Constants.EXP_IMAGETIME)) {
                            fullText = fullText.replace(Constants.EXP_IMAGETIME, imageTime);
                        }
                        if (fullText.contains(Constants.EXP_DETECTIONFLAG)) {
                            fullText = fullText.replace(Constants.EXP_DETECTIONFLAG, detectionFlag);
                        }
                        if (fullText.contains(Constants.EXP_ISSECONDIDENTIFY)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_ISSECONDIDENTIFY, isSecondIdentify);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_SECONDIDENTIFYRESULT)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                if ("是".equals(isSecondIdentify)){
                                    fullText = fullText.replace(Constants.EXP_SECONDIDENTIFYRESULT, secondJudgeResult);
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains(Constants.EXP_LINKAGEALGORITHM)) {
                            if (!StringUtils.isEmpty(isSecondIdentify) && "是".equals(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_LINKAGEALGORITHM, linkageAlgorithm);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE1, relDeviceVoList.get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME1, relDeviceVoList.get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL1)) {
                            if (relDeviceSize >= 1 && StringUtil.isNotEmpty(relDeviceVoList.get(0).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL1, relDeviceVoList.get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY1, relDeviceVoList.get(0).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH1, relDeviceVoList.get(0).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT1, relDeviceVoList.get(0).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME1, relDeviceVoList.get(0).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS1, relDeviceVoList.get(0).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE2, relDeviceVoList.get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME2, relDeviceVoList.get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL2)) {
                            if (relDeviceSize >= 2 && StringUtil.isNotEmpty(relDeviceVoList.get(1).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL2, relDeviceVoList.get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY2, relDeviceVoList.get(1).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH2, relDeviceVoList.get(1).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT2, relDeviceVoList.get(1).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME2, relDeviceVoList.get(1).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS2, relDeviceVoList.get(1).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE3, relDeviceVoList.get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME3, relDeviceVoList.get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL3)) {
                            if (relDeviceSize >= 3 && StringUtil.isNotEmpty(relDeviceVoList.get(2).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL3, relDeviceVoList.get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY3, relDeviceVoList.get(2).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH3, relDeviceVoList.get(2).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT3, relDeviceVoList.get(2).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME3, relDeviceVoList.get(2).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS3, relDeviceVoList.get(2).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE4, relDeviceVoList.get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME4, relDeviceVoList.get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL4)) {
                            if (relDeviceSize >= 4 && StringUtil.isNotEmpty(relDeviceVoList.get(3).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL4, relDeviceVoList.get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY4, relDeviceVoList.get(3).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH4, relDeviceVoList.get(3).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT4, relDeviceVoList.get(3).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME4, relDeviceVoList.get(3).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS4, relDeviceVoList.get(3).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE5, relDeviceVoList.get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME5, relDeviceVoList.get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL5)) {
                            if (relDeviceSize >= 5 && StringUtil.isNotEmpty(relDeviceVoList.get(4).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL5, relDeviceVoList.get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY5, relDeviceVoList.get(4).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH5, relDeviceVoList.get(4).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT5, relDeviceVoList.get(4).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME5, relDeviceVoList.get(4).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS5, relDeviceVoList.get(4).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        } else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }


    //修改卫星告警 - 非粮化
    public void replaceTableInfoFourGrain(XWPFDocument doc,AlarmOrderInfoExportDTO order,ExportOrderInfoReq dto,boolean isTower){
        //租户id
        String tenantId = StringUtils.isEmpty(order.getTenantId())?"":order.getTenantId();
        //租户名称
        String tenantName = StringUtils.isEmpty(order.getTenantName())?"":order.getTenantName();
        //行业应用
        String industryName = StringUtils.isEmpty(order.getIndustryName())?"":order.getIndustryName();
        //行业应用
        String appVerCode = StringUtils.isEmpty(order.getAppVerCode())?"":order.getAppVerCode();
        //节点编码
        String acceptNode = StringUtils.isEmpty(order.getAcceptNode())?"":order.getAcceptNode();
        //节点编码名称
        String acceptNodeName = StringUtils.isEmpty(order.getAcceptNodeName())?"":order.getAcceptNodeName();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();
        //事件编号
        String warningOrderId = StringUtils.isEmpty(order.getWarningOrderId())?"":order.getWarningOrderId();
        //重要等级
        String levelName = StringUtils.isEmpty(order.getImportanceLevelName())?"":order.getImportanceLevelName();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //告警来源
        String sourceName = StringUtils.isEmpty(order.getWarningSourceName())?"":order.getWarningSourceName();
        //告警类型
        String warningTypeName = StringUtils.isEmpty(order.getWarningTypeName())?"":order.getWarningTypeName();
        //告警原因
        String alarmReason = StringUtils.isEmpty(order.getAlarmReason())?"":order.getAlarmReason();
        //事发地点
        String address = StringUtils.isEmpty(order.getAddress())?"":order.getAddress();
        //经纬度
        String lotAndLat = StringUtils.isEmpty(order.getLotAndLat())?"":order.getLotAndLat();
        //所属网格
        String gridName = StringUtils.isEmpty(order.getGridName())?"":order.getGridName();
        //所属网格员
        String keeperName = StringUtils.isEmpty(order.getKeeperName())?"":order.getKeeperName();
        //事件描述
        String description = StringUtils.isEmpty(order.getDescription())?"":order.getDescription();
        //备注
        String remark = StringUtils.isEmpty(order.getRemark())?"":order.getRemark();
        //城市名称
        String cityName = StringUtils.isEmpty(order.getCityName())?"-":order.getCityName();
        //区县名称
        String countyName = StringUtils.isEmpty(order.getCountyName())?"-":order.getCountyName();
        //乡镇街道
        String townName = StringUtils.isEmpty(order.getTownName())?"-":order.getTownName();
        //村名称
        String villageName = StringUtils.isEmpty(order.getVillageName()) ? "-" : order.getVillageName();
        //变化类型
        String changeType = StringUtils.isEmpty(order.getChangeType()) ? "-" : order.getChangeType();
        //面积
        String area = StringUtils.isEmpty(order.getArea()) ? "-" : order.getArea();
        //土地利用类型
        String landUseType = StringUtils.isEmpty(order.getLandUseType()) ? "-" : order.getLandUseType();
        //影像时间
        String imageTime = StringUtils.isEmpty(order.getImageTime()) ? "-" : order.getImageTime();
        //是否已质检
        String detectionFlag = StringUtils.isEmpty(order.getDetectionFlag()) ? "-" : order.getDetectionFlag();
        //二次识别结果
        String secondJudgeResult = StringUtils.isEmpty(order.getSecondIdentifyResult()) ? "" : order.getSecondIdentifyResult();
        //是否二次识别
        String isSecondIdentify = StringUtils.isEmpty(order.getIsSecondIdentify()) ? "" : order.getIsSecondIdentify();
        //联动算法名称
        String linkageAlgorithm = StringUtils.isEmpty(order.getLinkageAlgorithm()) ? "" : order.getLinkageAlgorithm();
        //联动卫星摄像机
        List<RelDeviceVo> relDeviceVoList = CollectionUtils.isEmpty(order.getRelDeviceList()) ? null : order.getRelDeviceList();
        int relDeviceSize = 0;
        if (!CollectionUtils.isEmpty(relDeviceVoList)){
            for (RelDeviceVo relDeviceVo : relDeviceVoList) {
                if (relDeviceVo.getDeviceCode() == null)relDeviceVo.setDeviceCode("");
                if (relDeviceVo.getDeviceName() == null)relDeviceVo.setDeviceName("");
                if (relDeviceVo.getModelFactoryName() == null)relDeviceVo.setModelFactoryName("");
                if (relDeviceVo.getHeight() == null)relDeviceVo.setHeight("");
                if (relDeviceVo.getDeviceLotAndLat() == null)relDeviceVo.setDeviceLotAndLat("");
                if (relDeviceVo.getDeviceAddress() == null)relDeviceVo.setDeviceAddress("");
                if (relDeviceVo.getSnapShotTime() == null)relDeviceVo.setSnapShotTime("");
                if (relDeviceVo.getDeviceLabel() == null)relDeviceVo.setDeviceLabelString("");
                String deviceLabelString = "";
                //摄像机标签
                if (!CollectionUtils.isEmpty(relDeviceVo.getDeviceLabel())){
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < relDeviceVo.getDeviceLabel().size(); i++) {
                        if (i+1 == relDeviceVo.getDeviceLabel().size()){
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i));
                        }else{
                            stringBuffer.append(relDeviceVo.getDeviceLabel().get(i)).append(Constants.COMMA);
                        }
                    }
                    deviceLabelString = stringBuffer.toString();
                    relDeviceVo.setDeviceLabelString(deviceLabelString);
                }
            }
            relDeviceSize = relDeviceVoList.size();
        }
        XWPFParagraph xwpfParagraph = null;
        List<XWPFParagraph> xwpfParagraphList = new ArrayList<>();
        List<XWPFTableRow> rowsToDelete = new ArrayList<>();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        // 获取段落中所有的文本块
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null) {
                                sb.append(text);
                            }
                        }

                        String fullText = sb.toString();
                        String tableTitle = "";
                        // 如果文本块包含要替换的文本
                        // 替换文本
                        //总体概述
                        if (fullText.contains(Constants.EXP_TENANDID)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDID, tenantId);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_TENANDNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_TENANDNAME, tenantName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODE)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODE, acceptNode);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ACCEPTNODENAME)) {
                            if (dto.getExportSource() ==4 || dto.getExportSource() ==5){
                                fullText = fullText.replace(Constants.EXP_ACCEPTNODENAME, acceptNodeName);
                            }
                        }
                        if (fullText.contains(Constants.EXP_INDUSTRYNAME)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_INDUSTRYNAME, industryName);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_APPVERCODE)) {
                            if (isTower == true){
                                fullText = fullText.replace(Constants.EXP_APPVERCODE, appVerCode);
                            }
                        }
                        if (fullText.contains(Constants.EXP_ALARMTITLE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTITLE, alarmBody);
                        }
                        if (fullText.contains(Constants.EXP_ALARMID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMID, warningOrderId);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLEVEL)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLEVEL, levelName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTIME)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTIME, alarmTime);
                        }
                        if (fullText.contains(Constants.EXP_ALARMSOURCE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMSOURCE, sourceName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMTYPE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMTYPE, warningTypeName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREASON)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREASON, alarmReason);
                        }
                        if (fullText.contains(Constants.EXP_ALARMADDRESS)) {
                            fullText = fullText.replace(Constants.EXP_ALARMADDRESS, address);
                        }
                        if (fullText.contains(Constants.EXP_ALARMLOTANDLAT)) {
                            fullText = fullText.replace(Constants.EXP_ALARMLOTANDLAT, lotAndLat);
                        }

                        if (fullText.contains(Constants.EXP_ALARMGRID)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRID, gridName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMGRIDKEPPER)) {
                            fullText = fullText.replace(Constants.EXP_ALARMGRIDKEPPER, keeperName);
                        }
                        if (fullText.contains(Constants.EXP_ALARMDESCRIBE)) {
                            fullText = fullText.replace(Constants.EXP_ALARMDESCRIBE, description);
                        }
                        if (fullText.contains(Constants.EXP_ALARMREMARK)) {
                            fullText = fullText.replace(Constants.EXP_ALARMREMARK, remark);
                        }
                        if (fullText.contains(Constants.EXP_CITYNAME)) {
                            fullText = fullText.replace(Constants.EXP_CITYNAME, cityName);
                        }
                        if (fullText.contains(Constants.EXP_COUNTYNAME)) {
                            fullText = fullText.replace(Constants.EXP_COUNTYNAME, countyName);
                        }
                        if (fullText.contains(Constants.EXP_TOWNNAME)) {
                            fullText = fullText.replace(Constants.EXP_TOWNNAME, townName);
                        }
                        if (fullText.contains(Constants.EXP_VILLAGENAME)) {
                            fullText = fullText.replace(Constants.EXP_VILLAGENAME, villageName);
                        }
                        if (fullText.contains(Constants.EXP_CHANGETYPE)) {
                            fullText = fullText.replace(Constants.EXP_CHANGETYPE, changeType);
                        }
                        if (fullText.contains(Constants.EXP_AREA)) {
                            fullText = fullText.replace(Constants.EXP_AREA, area);
                        }
                        if (fullText.contains(Constants.EXP_LANDUSETYPE)) {
                            fullText = fullText.replace(Constants.EXP_LANDUSETYPE, landUseType);
                        }
                        if (fullText.contains(Constants.EXP_IMAGETIME)) {
                            fullText = fullText.replace(Constants.EXP_IMAGETIME, imageTime);
                        }
                        if (fullText.contains(Constants.EXP_DETECTIONFLAG)) {
                            fullText = fullText.replace(Constants.EXP_DETECTIONFLAG, detectionFlag);
                        }
                        if (fullText.contains(Constants.EXP_ISSECONDIDENTIFY)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_ISSECONDIDENTIFY, isSecondIdentify);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.SECOND_JUDEG_RESULT)) {
                            xwpfParagraph = paragraph;
                        }
                        if (fullText.contains(Constants.EXP_SECONDIDENTIFYRESULT)) {
                            if (!StringUtils.isEmpty(isSecondIdentify)){
                                if ("是".equals(isSecondIdentify)){
                                    fullText = fullText.replace(Constants.EXP_SECONDIDENTIFYRESULT, secondJudgeResult);
                                }else {
                                    xwpfParagraphList.add(paragraph);
                                    xwpfParagraphList.add(xwpfParagraph);
                                }
                            }
                        }
                        if (fullText.contains(Constants.EXP_LINKAGEALGORITHM)) {
                            if (!StringUtils.isEmpty(isSecondIdentify) && "是".equals(isSecondIdentify)){
                                fullText = fullText.replace(Constants.EXP_LINKAGEALGORITHM, linkageAlgorithm);
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE1, relDeviceVoList.get(0).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME1, relDeviceVoList.get(0).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL1)) {
                            if (relDeviceSize >= 1 && StringUtil.isNotEmpty(relDeviceVoList.get(0).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL1, relDeviceVoList.get(0).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY1, relDeviceVoList.get(0).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH1, relDeviceVoList.get(0).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT1, relDeviceVoList.get(0).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME1, relDeviceVoList.get(0).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS1)) {
                            if (relDeviceSize >= 1){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS1, relDeviceVoList.get(0).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE2, relDeviceVoList.get(1).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME2, relDeviceVoList.get(1).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL2)) {
                            if (relDeviceSize >= 2 && StringUtil.isNotEmpty(relDeviceVoList.get(1).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL2, relDeviceVoList.get(1).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY2, relDeviceVoList.get(1).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH2, relDeviceVoList.get(1).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT2, relDeviceVoList.get(1).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME2, relDeviceVoList.get(1).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS2)) {
                            if (relDeviceSize >= 2){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS2, relDeviceVoList.get(1).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE3, relDeviceVoList.get(2).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME3, relDeviceVoList.get(2).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL3)) {
                            if (relDeviceSize >= 3 && StringUtil.isNotEmpty(relDeviceVoList.get(2).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL3, relDeviceVoList.get(2).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY3, relDeviceVoList.get(2).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH3, relDeviceVoList.get(2).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT3, relDeviceVoList.get(2).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME3, relDeviceVoList.get(2).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS3)) {
                            if (relDeviceSize >= 3){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS3, relDeviceVoList.get(2).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE4, relDeviceVoList.get(3).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME4, relDeviceVoList.get(3).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL4)) {
                            if (relDeviceSize >= 4 && StringUtil.isNotEmpty(relDeviceVoList.get(3).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL4, relDeviceVoList.get(3).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY4, relDeviceVoList.get(3).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH4, relDeviceVoList.get(3).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT4, relDeviceVoList.get(3).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME4, relDeviceVoList.get(3).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS4)) {
                            if (relDeviceSize >= 4){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS4, relDeviceVoList.get(3).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICECODE5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICECODE5, relDeviceVoList.get(4).getDeviceCode());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICENAME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICENAME5, relDeviceVoList.get(4).getDeviceName());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELABEL5)) {
                            if (relDeviceSize >= 5 && StringUtil.isNotEmpty(relDeviceVoList.get(4).getDeviceLabelString())){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELABEL5, relDeviceVoList.get(4).getDeviceLabelString());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }

                        if (fullText.contains(Constants.EXP_RELDEVICEFACTORY5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEFACTORY5, relDeviceVoList.get(4).getModelFactoryName());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEHIGH5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEHIGH5, relDeviceVoList.get(4).getHeight());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICELOTANDLAT5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICELOTANDLAT5, relDeviceVoList.get(4).getDeviceLotAndLat());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        if (fullText.contains(Constants.EXP_SNAPSHOTTIME5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_SNAPSHOTTIME5, relDeviceVoList.get(4).getSnapShotTime());
                            }
                        }
                        if (fullText.contains(Constants.EXP_RELDEVICEADDRESS5)) {
                            if (relDeviceSize >= 5){
                                fullText = fullText.replace(Constants.EXP_RELDEVICEADDRESS5, relDeviceVoList.get(4).getDeviceAddress());
                            }else {
                                rowsToDelete.add(row);
                            }
                        }
                        // 删除原始段落中的所有运行
                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }
                        if (!StringUtil.isEmpty(fullText) && !fullText.equals(sb.toString())) {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(fullText, 0);
                            newRun.setFontSize(12);
                            newRun.setFontFamily("宋体");
                        } else {
                            // 创建一个新运行，并将更新后的文本添加到该运行中
                            XWPFRun newRun1 = paragraph.createRun();
                            newRun1.setText(fullText, 0);
                            newRun1.setFontSize(12);
                            newRun1.setBold(true);
                            newRun1.setFontFamily("宋体");
                        }
                    }
                }
            }
        }
        // 删除记录下来需要删除文本的行
        for (XWPFTableRow row : rowsToDelete) {
            XWPFTable table = row.getTable();
            table.removeRow(table.getRows().indexOf(row));
        }
        //删除记录下来需要删除的文本内容
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
        }
    }


    //事件详情导出 插入模块标题
    public void insertTitleInfo(XWPFDocument doc,String text,String redPicUrl) throws IOException, InvalidFormatException {
        InputStream imageStream = null;
        try {


        // 创建一个段落
        XWPFParagraph paragraph = doc.createParagraph();
        //设置左缩进
        paragraph.setIndentFromLeft(420);
        // 设置第一个段落的行间距
        paragraph.setVerticalAlignment(TextAlignment.CENTER);
        // 获取段落的CTPPr对象（CTPPr表示段落的属性）
        CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr == null) {
            ppr = paragraph.getCTP().addNewPPr();
        }
        // 创建一个新的缩进属性对象
        CTInd indentation = ppr.addNewInd();
        // 设置缩进距离为1
        indentation.setFirstLineChars(BigInteger.ONE);
        int imgFormat = XWPFDocument.PICTURE_TYPE_JPEG; // 图片格式
        // 创建一个矩形形状
        XWPFRun run = paragraph.createRun();
        URL url = new URL(redPicUrl);
        // 打开连接并设置超时时间
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(5000); // 设置连接超时时间为5秒
        connection.setReadTimeout(5000);   // 设置读取超时时间为10秒
        imageStream = connection.getInputStream();

        run.addPicture(imageStream, imgFormat, "", Units.toEMU(7), Units.toEMU(18));
        run.setText(text);
        run.setFontFamily("宋体");
        run.setFontSize(14);
        run.setBold(true);

        // 设置第一个段落与上一个段落之间的行距
        paragraph.setSpacingAfter(720);
        paragraph.setSpacingBefore(720);
        }catch (Exception e){
            log.error("导出详情插入模块标题异常", e);
        }finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException var21) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var21);
                }
            }
        }
    }

    //事件详情导出 插入告警图片
    public void insertPicInfo(XWPFDocument doc,AlarmOrderInfoExportDTO data){
        InputStream imageStream = null;
        try {
            //设置首行缩进为0
            XWPFParagraph targetParagraph = doc.createParagraph();
            CTPPr ppr = targetParagraph.getCTP().addNewPPr();
            CTInd indentation = ppr.addNewInd();
            indentation.setFirstLineChars(BigInteger.ZERO); // 设置首行缩进为零
            // 设置段落的首行缩进为零，以去除左边距
            targetParagraph.setIndentationFirstLine(0);
            if (targetParagraph != null) {
                //  创建一个段落，并在段落中插入图片
                XWPFRun run = targetParagraph.createRun();
                int imgFormat = XWPFDocument.PICTURE_TYPE_JPEG; // 图片格式
                for (Map map : data.getAlarmPicList()) {
                    URL url = new URL(String.valueOf(map.get("fileUrl")));
                    // 打开连接并设置超时时间
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(5000); // 设置连接超时时间为5秒
                    connection.setReadTimeout(5000);   // 设置读取超时时间为10秒
                    imageStream = connection.getInputStream();
                    imageStream = url.openStream();

                    run.addPicture(imageStream, imgFormat, "", Units.toEMU(679), Units.toEMU(374));

                }

            }
        }catch (Exception e){
            log.error("导出详情插入图片异常", e);
        }finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException var21) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var21);
                }
            }
        }

    }

    //事件详情导出 插入地图图片
    public void insertPlatPicInfo(XWPFDocument doc,MultipartFile file){
        try {
            XWPFParagraph targetParagraph = doc.createParagraph();
            CTPPr ppr = targetParagraph.getCTP().addNewPPr();
            CTInd indentation = ppr.addNewInd();
            indentation.setFirstLineChars(BigInteger.ZERO); // 设置首行缩进为零
            // 设置段落的首行缩进为零，以去除左边距
            targetParagraph.setIndentationFirstLine(0);
            if (targetParagraph != null) {
                //  创建一个段落，并在段落中插入图片
                XWPFRun run = targetParagraph.createRun();
                int imgFormat = XWPFDocument.PICTURE_TYPE_JPEG; // 图片格式
                    byte[] bytes = file.getBytes();
                    // 将Blob文件转换为InputStream
                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    // 添加图片到文档中
                    run.addPicture(inputStream, imgFormat, "", Units.toEMU(679), Units.toEMU(400));
            }
        }catch (Exception e){
            log.error("导出详情插入图片异常", e);
        }

    }

    // 设置单元格字体样式 右对齐加粗
    private  void setCellFontRightStyle(XWPFRun run,XWPFParagraph paragraph) {
        // 获取段落的CTPPr对象（CTPPr表示段落的属性）
        CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr == null) {
            ppr = paragraph.getCTP().addNewPPr();
        }
        // 创建一个新的缩进属性对象
        CTInd indentation = ppr.addNewInd();
        // 设置缩进距离为1
        indentation.setFirstLineChars(BigInteger.ONE);
        // 设置字体样式
        paragraph.setIndentationFirstLine(0); // 设置首行缩进为0
        run.setFontFamily("宋体");
        run.setFontSize(12);
        run.setBold(true);
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
    }

    // 设置单元格字体样式  左对齐
    private  void setCellFontLeftStyle(XWPFRun run,XWPFParagraph paragraph) {
        // 获取段落的CTPPr对象（CTPPr表示段落的属性）
        CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr == null) {
            ppr = paragraph.getCTP().addNewPPr();
        }
        // 创建一个新的缩进属性对象
        CTInd indentation = ppr.addNewInd();
        // 设置缩进距离为1
        indentation.setFirstLineChars(BigInteger.ONE);
        // 设置字体样式
        paragraph.setIndentationFirstLine(0); // 设置首行缩进为0

        run.setFontFamily("宋体");
        run.setFontSize(12);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
    }


    //事件详情导出 修改替换文本内容
    public void replaceTextInfo(XWPFDocument doc,AlarmOrderInfoExportDTO order){
        // 遍历所有段落，但不在遍历时修改它们
        // 创建一个列表来存储需要更新的段落和新文本
        List<XWPFParagraph> paragraphsToUpdate = new ArrayList<>();
        List<String> newTexts = new ArrayList<>();
        //告警时间
        String alarmTime = StringUtils.isEmpty(order.getAlarmTime())?"":order.getAlarmTime();
        //事件标题
        String alarmBody = StringUtils.isEmpty(order.getAlarmBody())?"":order.getAlarmBody();

        String title = order.getAlarmTime()+" "+order.getAlarmBody();
        Date date = new Date();
        //拼接 时间范围
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //导出时间
        String exportTime = outputFormat.format(date);
        for (XWPFParagraph p : doc.getParagraphs()) {
            String text = p.getText();
            if (text.contains("{{title}}")) {
                // 存储需要更新的段落和替换后的文本
                paragraphsToUpdate.add(p);
                newTexts.add(text.replace("{{title}}",title));
            }
            if (text.contains("{{exportDate}}")) {
                // 存储需要更新的段落和替换后的文本
                paragraphsToUpdate.add(p);
                newTexts.add(text.replace("{{exportDate}}", exportTime));
            }


        }

        // 在遍历后更新段落
        for (int i = 0; i < paragraphsToUpdate.size(); i++) {
            XWPFParagraph p = paragraphsToUpdate.get(i);
            String newText = newTexts.get(i);

            // 清除原有的所有 Run
            int runsSize = p.getRuns().size();
            for (int j = 0; j < runsSize; j++) {
                p.removeRun(0);
            }

            // 添加新的文本
            p.createRun().setText(newText);
        }
    }
    public String getRealUrl(String platformCode,String fileName){
        log.info("CentralNodeServiceImplgetRealUrl-req:{},{}",platformCode,fileName);
        if(fileName.contains("http")){
            //通过oldUrl获取文件
            SysFileUrlRequest request = new SysFileUrlRequest();
            request.setColonyCode(platformCode);
            request.setOldUrl(fileName);
            request.setUseOfs(true);
            Result<String> result = fileFeignClient.getFileUrlByOldUrl(request);
            log.info("CentralNodeServiceImplgetRealUrl-resp:",JSON.toJSONString(result));
            if(result != null){
                return String.valueOf(result.getData());
            }
        }else{
            SysFileRequest request = new SysFileRequest();
            request.setColonyCode(platformCode);
            request.setFileName(fileName);
            request.setUseOfs(true);
            Result<String> result =  fileFeignClient.getFileUrlByFileName(request);
            log.info("CentralNodeServiceImplgetRealUrl-resp:",JSON.toJSONString(result));
            if(result != null){
                return String.valueOf(result.getData());
            }
        }
        return null;
    }
    /**
     * 下载图片到临时路径
     * @param pidUrl 图片地址
     * @param path 下载到的路径
     */
    public void downloadPic(String pidUrl,String path) throws IOException {
        InputStream input =null;
        FileOutputStream output = null;
        try {
            // 打开连接
            URL url = new URL(pidUrl);
            URLConnection connection = url.openConnection();
            // 设置请求超时为5秒
            connection.setConnectTimeout(5 * 1000);
            // 读取数据流并保存到本地
            input = connection.getInputStream();
            byte[] data = new byte[1024];
            int len;
            output = new FileOutputStream(path);
            while ((len = input.read(data)) != -1) {
                output.write(data, 0, len);
            }
            log.info("图片保存成功：" + path);
        } catch (IOException e) {
            log.error("图片保存失败：" + e);
        }finally {
            if(output != null){
                output.close();
            }
            if(input != null){
                input.close();
            }
        }

    }

    public int addImgUrlsLocal(List<AlarmOrderImgDto> imgUrlList,String url,String orderId,int serNumber,String alarmSource,String platformCode){
        if(!StringUtils.isEmpty(url)){
            String[] imgs = url.split(",");
            for (String img:imgs) {
                serNumber = serNumber+1;
                AlarmOrderImgDto dto = new AlarmOrderImgDto();
                dto.setOrderId(orderId);
                dto.setImgUrl(img);
                dto.setSerNumber(serNumber);
                dto.setAlarmSource(alarmSource);
                dto.setPlatformCode(platformCode);
                imgUrlList.add(dto);
            }
        }
        return serNumber;
    }

    private static void addToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        FileInputStream fis =null;
        BufferedInputStream bis = null;
        try{
            byte[] buffer = new byte[1024];
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            // 添加 ZIP 包条目
            zos.putNextEntry(new ZipEntry(fileName));

            // 将文件内容写入 ZIP 包
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
        }catch (Exception e){
            log.error("addToZip");
        }finally {
            // 关闭文件输入流和 ZIP 包条目
            bis.close();
            fis.close();
        }
    }



    public int addAlarmOrderImgDtoList(List<AlarmOrderImgDto> imgUrlList,String url,String orderId,int serNumber,String alarmSource){
        serNumber = serNumber+1;
        AlarmOrderImgDto dto = new AlarmOrderImgDto();
        dto.setOrderId(orderId);
        dto.setImgUrl(url);
        dto.setSerNumber(serNumber);
        dto.setAlarmSource(alarmSource);
        imgUrlList.add(dto);
        return serNumber;
    }

    public int addImgUrls(List<AlarmOrderImgDto> imgUrlList,String url,String orderId,int serNumber,String alarmSource){
        if(!StringUtils.isEmpty(url)){
            String[] imgs = url.split(",");
            for (String img:imgs) {
                serNumber = serNumber+1;
                AlarmOrderImgDto dto = new AlarmOrderImgDto();
                dto.setOrderId(orderId);
                dto.setImgUrl(img);
                dto.setSerNumber(serNumber);
                dto.setAlarmSource(alarmSource);
                imgUrlList.add(dto);
            }
        }
        return serNumber;
    }
    /**
     * 获取告警类型名称列表
     *
     * @return
     */
    @Override
    public List<Map> getAlarmTypeList() {
        List<AlarmTypeDO> orderAlarm = alarmTypeRepository.findOrderAlarmAll(new AlarmTypeDTO());
        List<Map> alarmTypeList = orderAlarm.stream().map(type -> {
            Map<String, Object> map = new HashMap<>();
            map.put("typeValue", type.getTypeValue());
            map.put("typeName", type.getTypeName());
            map.put("icon", type.getIcon());
            return map;
        }).collect(Collectors.toList());
        return alarmTypeList;
    }

    @Override
    public void downloadImgsAndVideos(HttpServletResponse response, HttpServletRequest request, DownloadImgsAndVideoReq req) {
        //水印
        // 获取当前的用户
        String tenantName = "";
        // req.getName()有就是铁塔视角，没有则自行判断
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 铁塔视角
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())){
            tenantName = "中国铁塔";
        }else {
            // 客户视角判断是否为铁塔切租户
            boolean switchFlag = SecurityUtils.isSwitch();
            //客户视角切换：已切换-sql后面拼接租户ID,未切换-
            if (switchFlag) {
                //已切换，水印设置为铁塔
                tenantName = "中国铁塔";
            } else {
                // 未切换，水印为租户名称
                tenantName = loginUser.getUser().getTenantInfo().getTenantName();
            }
        }
        String tableName = "";
        if (!StringUtils.isEmpty(req.getWarningOrderId())) {
            //工单表及我的收藏
            // 获取表名
            tableName = TableConstant.WARN_ORDER_TEBLE + req.getWarningOrderId().substring(0, 6);
        }
        List<String> warningOrderIds = new ArrayList<>();
        warningOrderIds.add(req.getWarningOrderId());
        String videoUrl = "";
        String frontAlarmVideoUrl = "";
        AlarmOrderDTO dto = new AlarmOrderDTO();
        dto.setWarningOrderIds(warningOrderIds);
        dto.setTableName(tableName);
        List<WarningOrderListVO> queryResult = warningOrderMapper.queryAlarmOrderByWarningOrderIds(dto);
        if (!CollectionUtils.isEmpty(queryResult)){
            videoUrl = queryResult.get(0).getVideoUrl();
            frontAlarmVideoUrl = queryResult.get(0).getFrontAlarmVideoUrl();
        }
        List<String> videoUrlList = new ArrayList<>();
        // 使用 Lambda 表达式按逗号分割 videoUrl 字符串并存储到列表中
        if (!StringUtils.isEmpty(videoUrl)) {
            videoUrlList = Arrays.stream(videoUrl.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(frontAlarmVideoUrl)) {
            videoUrlList.addAll(Arrays.stream(frontAlarmVideoUrl.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }
        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();
        for (WarningOrderListVO vo:queryResult) {
            int serNumber =0;
            //卫星告警图片从透传字段取
            if(Constants.NINE.equals(vo.getWarningSource())){
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(vo.getExtraField(),SatelliteExtendFieldVo.class);
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathMir(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                if(!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())){
                    serNumber =this.addAlarmOrderImgDtoList(imgUrlList,extendFieldVo.getRelativePathThem(),vo.getOrderId(),serNumber,vo.getWarningSource());
                }
            }else{
                serNumber = this.addImgUrls(imgUrlList,vo.getFileImgUrlIcon(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getImgUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
                serNumber = this.addImgUrls(imgUrlList,vo.getFrontAlarmPicUrl(),vo.getOrderId(),serNumber,vo.getWarningSource());
            }
        }
        // 遍历 imgUrlList 列表并为每个元素添加 type = 1 标记为图片
        imgUrlList.forEach(img -> img.setType("1"));
        //原始图片路径集合
        List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(videoUrlList)){
            imageUrls.addAll(videoUrlList);
        }
        //调用obs加密方法获取加密后的路径
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
        for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            for (AlarmOrderImgDto imgDto : imgUrlList) {
                if (key.equals(imgDto.getImgUrl())) {
                    if (!StringUtils.isEmpty(map.get(key))) {
                        // 仅加密成功  使用原地址进行处理
                        imgDto.setImgUrl(map.get(key));
                        iterator.remove(); // 移除当前迭代的元素 imgurl
                    }else {
                        imgDto.setImgUrl(key);
                        iterator.remove(); // 移除当前迭代的元素 imgurl
                    }
                }
            }
        }
        videoUrlList.clear();
        // 遍历 map 的值，将每个加密videourl值添加到 videoUrlList 列表中
        if (!CollectionUtils.isEmpty(map)){
            map.entrySet().stream()
                    .map(entry -> entry.getValue() != null ? entry.getValue() : entry.getKey())
                    .forEach(videoUrlList::add);
            // 遍历 videoUrlList 列表，并为每个元素创建 AlarmOrderImgDto 对象并添加到 imgUrlList 列表中
            AlarmOrderImgDto alarmOrderImgDto = new AlarmOrderImgDto();
            alarmOrderImgDto.setType("2");
            alarmOrderImgDto.setOrderId(req.getWarningOrderId());
            alarmOrderImgDto.setVideoUrls(videoUrlList);
            imgUrlList.add(alarmOrderImgDto);
        }
        try {
            log.info("downloadSingleImgOrVideo-下载告警工单或视频地址：{}, 租户:{}", JSON.toJSONString(imgUrlList), tenantName);
            downloadUtil.downLoadImgsAndVideo(imgUrlList,tenantName,response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载告警工单的单个图片或者视频
     *
     * @param response 响应参数
     * @param request  请求参数
     * @param req      传入参数
     */
    @Override
    public void downloadSingleImgOrVideo(HttpServletResponse response, HttpServletRequest request, DownloadSingleImgOrVideoReq req) {
        //水印
        // 获取当前的用户
        String tenantName = "";
        // req.getName()有就是铁塔视角，没有则自行判断
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 铁塔视角
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())){
            tenantName = "中国铁塔";
        }else {
            // 客户视角判断是否为铁塔切租户
            boolean switchFlag = SecurityUtils.isSwitch();
            //客户视角切换：已切换-sql后面拼接租户ID,未切换-
            if (switchFlag) {
                //已切换，水印设置为铁塔
                tenantName = "中国铁塔";
            } else {
                // 未切换，水印为租户名称
                tenantName = loginUser.getUser().getTenantInfo().getTenantName();
            }
        }
        //判断工单是否在系统中
        if (StringUtil.isEmpty(req.getWarningOrderId())) {
            throw new CustomException("工单编号不能为空");
        }
        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();
        //不能同时传入图片和视频地址
        if (StringUtil.isNotEmpty(req.getImgUrl()) && StringUtil.isNotEmpty(req.getVideoUrl())) {
            throw new CustomException("工单的图片地址和视频地址不能同时存在");
        }

        // 使用 Lambda 表达式按逗号分割 videoUrl 字符串并存储到列表中
        if (StringUtil.isNotEmpty(req.getImgUrl())) {
            int serNumber = Objects.isNull(req.getSerNumber()) ? 0 : req.getSerNumber();
            for (String imgUrl : req.getImgUrl().split(",")) {
                serNumber = addAlarmOrderImgDtoList(imgUrlList, imgUrl.trim(), req.getWarningOrderId(), serNumber, req.getAlarmSource());
            }
        }
        // 遍历 imgUrlList 列表并为每个元素添加 type = 1 标记为图片
        imgUrlList.forEach(img -> img.setType("1"));

        List<String> videoUrlList = new ArrayList<>();
        // 使用 Lambda 表达式按逗号分割 videoUrl 字符串并存储到列表中
        if (StringUtil.isNotEmpty(req.getVideoUrl())) {
            videoUrlList = Arrays.stream(req.getVideoUrl().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        //原始图片路径集合
        List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(videoUrlList)){
            imageUrls.addAll(videoUrlList);
        }
        //调用obs加密方法获取加密后的路径
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
        for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            for (AlarmOrderImgDto imgDto : imgUrlList) {
                if (key.equals(imgDto.getImgUrl())) {
                    if (!StringUtils.isEmpty(map.get(key))) {
                        // 仅加密成功  使用原地址进行处理
                        imgDto.setImgUrl(map.get(key));
                    }else {
                        imgDto.setImgUrl(key);
                    }
                    iterator.remove(); // 移除当前迭代的元素 imgurl
                }
            }
        }
        videoUrlList.clear();
        // 遍历 map 的值，将每个加密videourl值添加到 videoUrlList 列表中
        if (!CollectionUtils.isEmpty(map)){
            map.entrySet().stream()
                    .map(entry -> entry.getValue() != null ? entry.getValue() : entry.getKey())
                    .forEach(videoUrlList::add);
            // 遍历 videoUrlList 列表，并为每个元素创建 AlarmOrderImgDto 对象并添加到 imgUrlList 列表中
            AlarmOrderImgDto alarmOrderImgDto = new AlarmOrderImgDto();
            alarmOrderImgDto.setType("2");
            alarmOrderImgDto.setOrderId(req.getWarningOrderId());
            alarmOrderImgDto.setVideoUrls(videoUrlList);
            int serNumber = Objects.isNull(req.getSerNumber()) ? 0 : req.getSerNumber();
            alarmOrderImgDto.setSerNumber(serNumber + 1);
            imgUrlList.add(alarmOrderImgDto);
        }

        try {
            downloadUtil.downLoadImgsAndVideo(imgUrlList, tenantName, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadVideoByFlush(HttpServletResponse response, HttpServletRequest request, InputStream videoStream,String fileName,String isH265) {
        String waterPrintn = this.getWaterPritn();
        downloadUtil.downLoadVideoByFlush(videoStream, waterPrintn, response,fileName,isH265);
    }

    @Override
    public void downloadPhotoByFlush(HttpServletResponse response, HttpServletRequest request, InputStream videoStream,String fileName) {
        String waterPrintn = this.getWaterPritn();
        downloadUtil.downLoadPhotoByFlush(videoStream, waterPrintn, response,fileName);
    }

    @Override
    public String downloadVideoByUrlAndUpload(HttpServletResponse response, HttpServletRequest request, String url, String isH265) {
        if (StrUtil.isBlank(url)) {
            throw new IllegalArgumentException("url不能为空");
        }
        byte[] sourceBytes = null;
        try {
            sourceBytes = downloadVideoBytes(url);
        } catch (Exception e) {
            log.error("downloadVideoByUrlAndUpload-downloadBytesFail url={}", url, e);
            throw new IllegalArgumentException("downloadVideoBytes失败");
        }

        try (InputStream sourceStream = new ByteArrayInputStream(sourceBytes)) {
            String originName = buildFileNameFromUrl(url);
            String ext = Optional.ofNullable(FilenameUtils.getExtension(originName)).filter(StrUtil::isNotBlank).orElse("mp4");
            String uploadName = IdUtil.simpleUUID() + "." + ext;
            log.info("downloadVideoByUrlAndUpload-start url={},originName={},uploadName={},isH265={},sourceBytesLen={}",
                    url, originName, uploadName, isH265, sourceBytes == null ? -1 : sourceBytes.length);

            CaptureHttpServletResponseWrapper capture = new CaptureHttpServletResponseWrapper(response);
            this.downloadVideoByFlush(capture, request, sourceStream, originName, isH265);

            byte[] watermarked = capture.toByteArray();
            log.info("downloadVideoByUrlAndUpload-watermarkedBytes uploadName={},len={}", uploadName, watermarked == null ? -1 : watermarked.length);
            if (watermarked == null || watermarked.length == 0) {
                throw new IllegalArgumentException("watermarked为空");
            }

            MultipartFile uploadFile = new ByteArrayMultipartFile("file", uploadName, guessContentType(uploadName), watermarked);
            AjaxResult ajaxResult = fileFeignClient.uploadFile(uploadFile);
            if (ajaxResult == null || !ajaxResult.ok()) {
                throw new IllegalArgumentException("上传加水印视频文件失败");
            }
            log.info("uploadFile-成功 uploadName={},ajaxResult={}", uploadName,JSON.toJSONString(ajaxResult));
            SysFileInfo sysFileInfo = ajaxResult.parseData(SysFileInfo.class);
            if (sysFileInfo == null) {
                throw new IllegalArgumentException("获取上传结果为空");
            }
            log.info("downloadVideoByUrlAndUpload-done uploadName={},fileUrl={}", uploadName, StrUtil.blankToDefault(sysFileInfo.getFileUrl(), ""));
            return StrUtil.blankToDefault(sysFileInfo.getFileUrl(), "");
        } catch (Exception e) {
            log.error("downloadVideoByUrlAndUpload-失败 url={}", url, e);
            throw new IllegalArgumentException("downloadVideoByUrlAndUpload失败");
        }
    }

    private static byte[] downloadVideoBytes(String url) throws IOException {
        try {
            return HttpUtil.downloadBytes(url);
        } catch (Exception e) {
            try (InputStream in = new URL(url).openStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                return out.toByteArray();
            }
        }
    }

    private static String buildFileNameFromUrl(String url) {
        try {
            String raw = url.split("\\?")[0];
            String name = raw.substring(raw.lastIndexOf('/') + 1);
            name = java.net.URLDecoder.decode(name, StandardCharsets.UTF_8.name());
            return StrUtil.isBlank(name) ? "video.mp4" : name;
        } catch (Exception ignored) {
            return "video.mp4";
        }
    }

    private static String guessContentType(String filename) {
        try {
            String type = new MimetypesFileTypeMap().getContentType(filename);
            return StrUtil.isBlank(type) ? "application/octet-stream" : type;
        } catch (Exception ignored) {
            return "application/octet-stream";
        }
    }

    @Override
    public void downloadImgsSatellite(HttpServletResponse response, HttpServletRequest request, List<ReceiveSatelliteAlarmDto> list) throws Exception {
        List<AlarmOrderImgDto> imgUrlList = new ArrayList<>();
        for (ReceiveSatelliteAlarmDto dto: list) {
            int serNumber = 0;
            SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(dto.getExtendField(), SatelliteExtendFieldVo.class);
            if (!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())) {
                serNumber = this.addAlarmOrderImgDtoList(imgUrlList, extendFieldVo.getRelativePathMir(), dto.getAlarmCode(), serNumber,Constants.NINE);
            }
            if (!StringUtils.isEmpty(extendFieldVo.getRelativePathPose())) {
                serNumber = this.addAlarmOrderImgDtoList(imgUrlList, extendFieldVo.getRelativePathPose(), dto.getAlarmCode(), serNumber,Constants.NINE);
            }
            if (!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())) {
                serNumber = this.addAlarmOrderImgDtoList(imgUrlList, extendFieldVo.getRelativePathThem(), dto.getAlarmCode(), serNumber,Constants.NINE);
            }
        }
        if(CollectionUtils.isEmpty(imgUrlList)){
            //没有图片下载直接返回
            return;
        }
        //原始图片路径集合
        List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //调用obs加密方法获取加密后的路径
        Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
        for (String key : map.keySet()) {
            for (AlarmOrderImgDto imgDto : imgUrlList) {
                if (imgDto.getImgUrl().equals(key)) {
                    if(!StringUtils.isEmpty(map.get(key))){
                        // 仅加密成功  使用原地址进行处理
                        imgDto.setImgUrl(map.get(key));
                    }
                }
            }
        }
        DownloadUtil.downLoad(imgUrlList, Constants.TOWER_NAME, response);
    }

    /**
     * 获取水印字符串
     * @return
     */
    private String getWaterPritn(){
        //水印
        // 获取当前的用户
        String tenantName = "";
        // req.getName()有就是铁塔视角，没有则自行判断
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 铁塔视角
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())){
            tenantName = "中国铁塔";
        }else {
            // 客户视角判断是否为铁塔切租户
            boolean switchFlag = SecurityUtils.isSwitch();
            //客户视角切换：已切换-sql后面拼接租户ID,未切换-
            if (switchFlag) {
                //已切换，水印设置为铁塔
                tenantName = "中国铁塔";
            } else {
                // 未切换，水印为租户名称
                tenantName = loginUser.getUser().getTenantInfo().getTenantName();
            }
        }
        return tenantName;
    }

    /**
     * 调用api接口，获取AI算法配置参数
     *
     * @param methodName 方法名称
     * @param o 参数
     * @return 算法参数描述
     */
    private Map<String, String> getAiAlgorithmConfigDescMap(String methodName, Object o,
                                                            String orderIdAlias, String orderSourceAlias,
                                                            String taskIdAlias, String teanatIdAlias,
                                                            String deviceCodeAlias, String channelCodeAlias,
                                                            String algorithmCodeAlias) {
        Map<String, String> map = Maps.newHashMap();
        try {
            AiAlgorithmConfigReq req = new AiAlgorithmConfigReq();
            req.setParams(o);
            req.setOrderIdAlias(orderIdAlias);
            req.setOrderSourceAlias(orderSourceAlias);
            req.setTaskIdAlias(taskIdAlias);
            req.setTeanatIdAlias(teanatIdAlias);
            req.setDeviceCodeAlias(deviceCodeAlias);
            req.setChannelCodeAlias(channelCodeAlias);
            req.setAlgorithmCodeAlias(algorithmCodeAlias);
            map = iTradeWarningOrderService.buildAlgorithmConfigDescFieldMap(req).getData();
        } catch (Exception e) {
            log.error("导出事件，获取AI算法配置列表异常，方法：{}", methodName, e);
        }
        return map;
    }
}




