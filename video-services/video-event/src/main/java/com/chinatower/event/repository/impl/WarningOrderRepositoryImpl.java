package com.chinatower.event.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.SensingAlgorithmTaskAPI;
import com.chinatower.bdm.api.entity.AlgorithmPicResultVO;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.core.cache.MongoTemplateDelegate;
import com.chinatower.common.core.dto.*;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.AssembleUrlUtil;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.vo.AlarmClassTimeVO;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.IPolygonPatchConfigService;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.model.AiAlgorithmConfigReq;
import com.chinatower.event.api.model.IntelligentJudgeTaskRequest;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.asyncTask.AsyncCommon;
import com.chinatower.event.constant.AlarmConstants;
import com.chinatower.event.constant.CommonConstant;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constant.BigModelDescConstants;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.util.AiAgentExtendFieldUtil;
import com.chinatower.event.core.alarm.util.AiExtraFieldUtil;
import com.chinatower.event.core.alarm.util.SystemUtil;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dataobject.*;
import com.chinatower.event.dto.AlarmOrderDuplicateDTO;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.ThreadDto.OrderInfoFuncDto;
import com.chinatower.event.dto.ThreadDto.Param;
import com.chinatower.event.dto.WarningOrderDTO;
import com.chinatower.event.dto.ThreadDto.WarningOrderResultDto;
import com.chinatower.event.dto.gridinfo.GridInfoReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.kafka.KafkaProducer;
import com.chinatower.event.lntlagservice.AngleUtils;
import com.chinatower.event.mapper.*;
import com.chinatower.event.mongodb.InterceptMongoMapper;
import com.chinatower.event.mongodb.OperateMongoMapper;
import com.chinatower.event.mongodb.VmFlowHandleRecordDAO;
import com.chinatower.event.mongodb.WarnOrderMongoDAO;
import com.chinatower.event.repository.*;
import com.chinatower.event.service.InterceptService;
import com.chinatower.event.service.OperateService;
import com.chinatower.event.service.OrderDelayMessageService;
import com.chinatower.event.util.CommonUtils;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.subscenes.SubScenesOrderTableHelper;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.AlarmLocationUtils;
import com.chinatower.event.utils.DistanceUtils;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.event.vo.*;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.CamaraDeviceParamsInfoVO;
import com.chinatower.gis.api.entity.layer.GeojsonStruckRes;
import com.chinatower.gis.api.entity.layer.GetXjxlFeaturesByIdsReq;
import com.chinatower.gis.api.entity.layer.StatisticDkmjReq;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.common.core.vo.VmDeviceStatisticsVO;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.platform.service.ObsClientService;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysDeptDetailVO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.VideoQueryAPI;
import com.chinatower.video.api.entity.PresetData;
import com.chinatower.video.api.entity.VideoBaseReq;
import com.chinatower.video.api.entity.VideoPresetDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.page.PageMethod;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import io.swagger.annotations.ApiOperation;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.event.constants.Constants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-20
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class WarningOrderRepositoryImpl extends ServiceImpl<WarningOrderMapper, WarningOrderDO> implements IWarningOrderRepository {

    /**
     * 获取摄像机高度信息size参数
     */
    @Value("${get-camera.page-size:100}")
    private Integer getCameraPageSize;

    /**
     * 获取摄像机高度信息radius参数
     */
    @Value("${get-camera.radius:100}")
    private Double getCameraRadius;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private OrderLeaderAuditMapper orderLeaderAuditMapper;

    @Autowired
    private WarningOrderCollectionMapper warningOrderCollectionMapper;

    @Autowired
    private IAlarmOperateRepository alarmOperateRepository;

    @Autowired
    private IAlarmFlowConfigService alarmFlowConfigService;

    @Autowired
    private IVideoDeviceService videoDeviceService;

    @Autowired
    private WarnOrderMongoDAO warnOrderMongoDAO;

    @Autowired
    private VmFlowHandleRecordDAO handleRecordDAO;

    @Autowired
    private MongoTemplateDelegate mongoTemplate;

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private OrderFlowRecordMapper orderFlowRecordMapper;

    @Autowired
    private ProcessOrderMapper processOrderMapper;

    @Autowired
    private ProcessApproveMapper processApproveMapper;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private IReceiveRepository receiveRepository;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IFlowTemplateService flowTemplateService;

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private ILinkUserDealService linkUserDealService;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private OrderFileMapper orderFileMapper;

    @Autowired
    private ObsClientService obsClientService;

    @Autowired
    private IWarningOrderCollectionRepository iWarningOrderCollectionRepository;

    @Autowired
    private ISysAlarmTagsRepository sysAlarmTagsRepository;

    @Autowired
    private IAlarmLableRelRepository alarmLableRelRepository;

    @Autowired
    private WarningOriginalPointMapper warningOriginalPointMapper;

    @Resource
    private WarningSensorMonitorMapper warningSensorMonitorMapper;

    @Autowired
    private ITransLntlagRepository transLntlagRepository;

    @Autowired
    private ISysService sysService;

    @Autowired
    private IWarningInterceptRepository warningInterceptRepository;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private VmAlarmFlowConfigMapper vmAlarmFlowConfigMapper;
    @Autowired
    private IAlarmTypeRepository alarmTypeRepository;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Autowired
    private IWarningOrderRepository iWarningOrderRepository;

    @Autowired
    private SysMapper sysMapper;

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
    private AlarmShareMapper alarmShareMapper;

    @Autowired
    private AlarmLableRelMapper alarmLableRelMapper;

    @Autowired
    private ICommonRepository iCommonRepository;

    @Autowired
    private OrderDelayMessageService orderDelayMessageService;

    @Autowired
    private OverdueRemindMapper overdueRemindMapper;

    @Autowired
    private IPolygonPatchConfigService iPolygonPatchConfigService;

    @Autowired
    private SystemUtil systemUtil;

    @Autowired
    private SysDeptDeviceMapper sysDeptDeviceMapper;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private OperateService operateService;

    @Autowired
    private GisLayerAPI gisLayerAPI;

    @Autowired
    private VideoQueryAPI videoQueryAPI;

    @Autowired
    private InterceptService interceptService;

    @Autowired
    private ThirdPartyAlarmService thirdPartyAlarmService;

    @Autowired
    private IFlowLinkAttrService flowLinkAttrService;

    @Autowired
    private ThirdPartyAlarmMapper thirdPartyAlarmMapper;

    @Autowired
    private SupervisionMsgSendService supervisionMsgSendService;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private AlarmTypeMapper alarmTypeMapper;

    @Autowired
    private SensingAlgorithmTaskAPI sensingAlgorithmTaskAPI;
    @Autowired
    private KafkaProducer kafkaProducer;

    private static final String DEFAULT_IMAGE_TYPE = "data:image/png;base64,";


    private final String hms = " 23:59:59";

    private final String RESULT_LIST = "resultList";
    private final String ALL_COUNT = "allCount";
    private static final String REGEX = "[^\\d.]";

    @Autowired
    private AsyncCommon asyncCommon;
    private static List<String> orderStatusList = new ArrayList<>();

    {
        orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
        orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
    }


    @Override
    public TableDataInfo getNoScopeOrderList(AlarmOrderDTO dto, ServletRequest request) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        List<WarningOrderListVO> result = new ArrayList<>();
        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            //如果未传告警时间,设置默认时间范围（半年内）- 兼容APP调用
            startDate = DateUtils.getForeSixMonth(new Date());
        } else {
            startDate = dto.getAlarmTimeStart();
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = dto.getAlarmTimeEnd();
        }
        dto.setAlarmTimeStart(startDate);
        dto.setAlarmTimeEnd(endDate);
        int errCount = 0;
        boolean stop = false;
        // 根据排序规则判断从最后一个表开始向前获取或从开始向后获取
        while (!stop) {
            try {
                //正序从开始获取
                String tableName = null;
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                    tableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_TEBLE);
                } else {
                    tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
                }
                dto.setTableName(tableName);
                List<WarningOrderListVO> queryResult = warningOrderMapper.queryAlarmOrderNoScope(dto);
                if (!CollectionUtils.isEmpty(queryResult)) {
                    result.addAll(queryResult);
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                    // 转到下月
                    String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                    startDate = DateUtils.stringToDateYM(nextMonth);
                } else {
                    // 转到上月
                    String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                    endDate = DateUtils.stringToDateYM(lastMonth);
                }
            } catch (Exception e) {
                log.error("查询告警事件不过滤权限列表错误：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if (errCount > 3) {
                    return tableDataInfo;
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }

        }
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        this.resultHandle(result, dto, loginUser.getUser().getUserId(), "");
        tableDataInfo.setRows(result);
        tableDataInfo.setTotal(result.size());
        tableDataInfo.setCode(Constants.CODE_200);
        return tableDataInfo;
    }


    /**
     * 功能描述:
     * 工作台工单列表查询
     *
     * @param dto
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getOrderList(AlarmOrderDTO dto, ServletRequest request) {
        long start = System.currentTimeMillis();
        log.info("事件列表查询入参：{}，开始时间：{}", JSON.toJSONString(dto), start);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        String token = getToken(request);
        HttpServletRequest req = (HttpServletRequest) request;
        if (!StringUtils.isEmpty(req.getHeader(Constants.PARAM_CLIENTTYPE))) {
            dto.setClienttype(req.getHeader(Constants.PARAM_CLIENTTYPE));
        }
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();


        Long[] roleIds = loginUser.getUser().getRoleIds();
        dto.setAdmin(Constants.YES_FLAG.equals(loginUser.getUser().getIsTenantAdmin()));
        dto.setOrgIds(CommonUtils.getOrgIds(loginUser.getUser().getDeptId()));
        dto.setTenantId(loginUser.getUser().getTenantId());
        //不是租户管理员查询行业数据；  是租户管理员且是行业调用，查询行业数据
        if (!Constants.YES_FLAG.equals(loginUser.getUser().getIsTenantAdmin())
                || (Constants.YES_FLAG.equals(loginUser.getUser().getIsTenantAdmin()) && Constants.YES_FLAG.equals(dto.getIsTrade()))) {
            dto.setAppVerCode(loginUser.getUser().getAppVerCode());
        }
        //告警类型标签
        Param param = new Param();
        param.setDto(dto);
        param.setInfo(tableDataInfo);
        param.setUser(loginUser);

        //告警标签

        Map<Function, Object> paramMap = new ConcurrentHashMap<>();
        Function<Param, Param> fun1 = this::getTableDataInfo;
        paramMap.put(fun1, param);
        Function<AlarmOrderDTO, List<AlarmLableRelDTO>> fun2 = this::getAlarmLableRelDTOS;
        paramMap.put(fun2, dto);
        List<String> roleIdList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleIdList.add(String.valueOf(roleId));
            }
        }
        dto.setRoleIdList(roleIdList);
        //根据用户id和角色id查询流程环节信息
        SysUser sysUser = loginUser.getUser();
        //查询用户环节权限废弃
        //String flowLinkIds = flowLinkService.getFlowLinkAttrByUser(sysUser, roleIdList);
        String flowLinkIds = "";
        String userId = sysUser.getUserId();

        // 铁塔切租户则查询租户管理员的已办
        if (SecurityUtils.isSwitch()) {
            SysUser user = sysMapper.selectUserOnlyByUserName(sysUser.getUserName());
            userId = user.getUserId();
        }
        dto.setUserId(userId);

        this.handleOrderReq(dto, flowLinkIds);
        if (!CollectionUtils.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(Constants.NINE)) {
            dto.setEnableAcceptanceMode(Constants.ONE);
            //dto.getOrderStatusList().clear();
        }

        /**
         * 行业调用时传入网格员ID使用网格员ID查询数据
         */
        if (!StringUtils.isEmpty(dto.getGridUserId())) {
            dto.setUserId(dto.getGridUserId());
        }

        List<WarningOrderListVO> resultList = new ArrayList<>();
        // 总数
        int allCount = 0;
        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        // 需要查询的个数
        int limit = dto.getPageSize();
        // 分页开始的数
        int offset = (dto.getPageNum() - 1) * limit;
        // 当前表查询开始的数
        int tableStartNum = 0;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            //如果未传告警时间,设置默认时间范围（半年内）- 兼容APP调用
            startDate = DateUtils.getForeSixMonth(new Date());
        } else {
            startDate = dto.getAlarmTimeStart();
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = dto.getAlarmTimeEnd();
        }
        dto.setAlarmTimeStart(startDate);
        dto.setAlarmTimeEnd(endDate);
        int count = 0;
        boolean stop = false;
        boolean cacheFlag = false;
        //处理传入的组织条件
        dto.setSysUser(sysUser);
        handleOrgIdList(dto);
        //跨库获取页面查询设备信息关联数据
        Function<AlarmOrderDTO, List<AlarmOrderByDeviceDTO>> fun3 = this::getAlarmOrderByDeviceDTOS;
        paramMap.put(fun3, dto);
        //查询告警类型关键字模糊查询到的告警类型id
        Function<AlarmOrderDTO, List<String>> fun4 = this::getWarningTypeIdListByKeyWord;
        paramMap.put(fun4, dto);

        Function<AlarmOrderDTO, PoiGeocodingQueryRes> fun5 = this::getLocationByKeyWord;
        paramMap.put(fun5, dto);
        List<Function> listFunc = Arrays.asList(fun1, fun2, fun3, fun4, fun5);
        ThreadParallelUtil.run(listFunc, f -> {
            Object o = paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER_LIST);

        if (CollectionUtils.isEmpty(dto.getDevList())
                && (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(dto.getModelId()) || !StringUtils.isEmpty(dto.getDeviceName()) ||
                !CollectionUtils.isEmpty(dto.getModelIds()) || !StringUtils.isEmpty(dto.getIotDeviceCodeOrName()))) {
            return tableDataInfo;
        }

        log.info("事件列表查询条件处理入参：{}，耗时：{}", JSON.toJSONString(dto), (System.currentTimeMillis() - start));

        // 防止时间传反，导致gc
        if (ObjectUtil.isNotNull(dto.getAlarmTimeStart()) && ObjectUtil.isNotNull(dto.getAlarmTimeEnd()) && dto.getAlarmTimeStart().after(dto.getAlarmTimeEnd()))
            return tableDataInfo;

        int errCount = 1;

        // 根据排序规则判断从最后一个表开始向前获取或从开始向后获取
        while (!stop) {
            try {
                //正序从开始获取
                String tableName = null;
                //如果按照importanceLevel，默认从结束日期查询，有其他排序可以改成list包含
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort()) && !PARAM_IMPORTANCE_LEVEL.equals(dto.getSortName())) {
                    tableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_TEBLE);
                } else {
                    tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
                }
                String yearMonth = tableName.substring(tableName.length() - 6, tableName.length());
                if (!CollectionUtils.isEmpty(dto.getNotInWarningOrderIdList())) {
                    List<Map<String, List<String>>> notInList = dto.getNotInWarningOrderIdList();
                    for (Map<String, List<String>> map : notInList) {
                        if (!CollectionUtils.isEmpty(map.get(yearMonth))) {
                            dto.setNotInWarningOrderIds(map.get(yearMonth));
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(dto.getWarningOrderIdList())) {
                    List<Map<String, List<String>>> inList = dto.getWarningOrderIdList();
                    for (Map<String, List<String>> map : inList) {
                        if (!CollectionUtils.isEmpty(map.get(yearMonth))) {
                            dto.setWarningOrderIds(map.get(yearMonth));
                        }
                    }
                }

                dto.setTableName(tableName);
                // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态，OrderStatus由6变为空
                if ((StringUtil.isNotEmpty(dto.getOrderStatus())) && (dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))) {
                    dto.setOrderStatus("");
                }
                //告警标签
                List<String> warningOrderIds = new ArrayList<>();
                if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                    List<AlarmLableRelDTO> firstALarmLable = dto.getAlarmLableRelDTOList().stream().filter(item -> yearMonth.equals(item.getTableTime())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(firstALarmLable)) {
                        firstALarmLable.forEach(item -> {
                            warningOrderIds.add(item.getWaringOrderId());
                        });
                        dto.setWarningOrderIds(warningOrderIds);
                    } else {
                        stop = DateUtils.sameMouth(startDate, endDate);
                        //如果按照importanceLevel，默认从结束日期查询，有其他排序可以改成list包含
                        if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort()) && !PARAM_IMPORTANCE_LEVEL.equals(dto.getSortName())) {
                            // 转到下月
                            String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                            startDate = DateUtils.stringToDateYM(nextMonth);
                        } else {
                            // 转到上月
                            String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                            endDate = DateUtils.stringToDateYM(lastMonth);
                        }
                        continue;
                    }
                }
                count = getLocalizationFlag()
                        ? warningOrderMapper.queryAlarmOrderCountLocal(dto)
                        : warningOrderMapper.queryAlarmOrderCount(dto);
                // 累计总数
                allCount += count;
                if (allCount > offset && count > 0 && limit > 0) {
                    tableStartNum = offset - (allCount - count);
                    if (tableStartNum < 0) {
                        tableStartNum = 0;
                    }
                    if (count > limit) {
                        dto.setLimit(limit);
                    } else {
                        dto.setLimit(count);
                    }
                    dto.setOffset(tableStartNum);
                    List<WarningOrderListVO> queryResult = getLocalizationFlag()
                            ? warningOrderMapper.queryAlarmOrderLocal(dto) : warningOrderMapper.queryAlarmOrder(dto);
                    for (WarningOrderListVO orderVo : queryResult) {
                        orderVo.setHoriAzimuthAngle(AngleUtils.transformAngle(orderVo.getHoriAzimuthAngle()));
                    }
                    resultList.addAll(queryResult);
                    limit = limit - queryResult.size();
                    if (limit == 0) {
                        Object cacheCount = RedisUtil.get(Constants.ORDER_LIST_QUERY + token + dto.getQueryValue(), RedisDBType.PRIMARY);
                        if (Objects.nonNull(cacheCount)) {
                            allCount = Integer.valueOf(String.valueOf(cacheCount));
                            break;
                        } else {
                            cacheFlag = true;
                        }
                    }
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                //如果按照importanceLevel，默认从结束日期查询，有其他排序可以改成list包含
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort()) && !PARAM_IMPORTANCE_LEVEL.equals(dto.getSortName())) {
                    // 转到下月
                    String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                    startDate = DateUtils.stringToDateYM(nextMonth);
                } else {
                    // 转到上月
                    String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                    endDate = DateUtils.stringToDateYM(lastMonth);
                }
            } catch (Exception e) {
                log.error("查询告警事件列表错误：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if (errCount > 3) {
                    return tableDataInfo;
                }

                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }

        }
        log.info("事件列表查询条件结束耗时：{}", (System.currentTimeMillis() - start));
        if (cacheFlag || (offset == allCount && allCount != 0)) {
            RedisUtil.set(Constants.ORDER_LIST_QUERY + token + dto.getQueryValue(), String.valueOf(allCount), 300L, RedisDBType.PRIMARY);
        }
        // 2022-01-11 sutn抽出方法
        this.resultHandle(resultList, dto, userId, flowLinkIds);
        //20230109 抽方法
        this.getALarmLableList(resultList);
        log.info("查询告警工单列表最终耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));
        tableDataInfo.setTotal(allCount);
        tableDataInfo.setRows(resultList);
        return tableDataInfo;
    }

    /**
     * 获取是否为国产化标识
     *
     * @return
     */
    private boolean getLocalizationFlag() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.LOCALIZATION_FLAG);

        try {
            AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);

            if (Objects.nonNull(configInfo) && Objects.nonNull(configInfo.get(Constants.RESULT_DATA))) {

                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
                String localFlag = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);

                return "1".equals(localFlag);
            }
        } catch (Exception e) {
            log.error("获取国产化标识配置失败", e);
        }

        return false;
    }

    /**
     * 根据网格id获取当天的告警数量（提供给君南）
     *
     * @param dto
     * @return String
     * @version v1.6.10
     */
    @Override
    public String getOrderListCount(AlarmOrderDTO dto) {
        String tableName = TableUtil.getTableName(dto.getAlarmTimeStart(), TableConstant.WARN_ORDER_TEBLE);
        dto.setTableName(tableName);
        Integer count = getLocalizationFlag()
                ? warningOrderMapper.queryAlarmOrderCountLocal(dto)
                : warningOrderMapper.queryAlarmOrderCount(dto);
        return String.valueOf(count);
    }

    @Override
    public LabelAndColStatusResp getLabelAndColStatusByOrders(LabelAndColStatusReq req) {
        if (CollectionUtils.isEmpty(req.getOrderIds())) {
            return null;
        }
        LabelAndColStatusResp resp = new LabelAndColStatusResp();
        try {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            //查询工单对应标签
            List<AlarmLableRelDTO> alarmLableAllList = alarmLableRelMapper.getAlarmLableByOrdId(req.getOrderIds());
            //查询工单是否被收藏
            AlarmOrderDTO dto = new AlarmOrderDTO();
            dto.setCollectionCreator(loginUser.getUser().getUserId());
            dto.setWarningOrderIds(req.getOrderIds());
            List<WarningOrderListVO> warningOrderCollectionList = warningOrderCollectionMapper.qryWarningOrderCollection(dto);
            List<LabelAndColStatusResp.LabelAndColStatusDto> resList = new ArrayList<>();
            for (String orderId : req.getOrderIds()) {
                LabelAndColStatusResp.LabelAndColStatusDto labelAndColStatusDto = new LabelAndColStatusResp.LabelAndColStatusDto();
                //收藏状态默认不收藏（0：不收藏  1；收藏）
                labelAndColStatusDto.setStatus(Constants.ZERO);
                labelAndColStatusDto.setOrderId(orderId);
                List<LabelAndColStatusResp.LabelDto> labelList = new ArrayList<>();
                for (AlarmLableRelDTO label : alarmLableAllList) {
                    if (orderId.equals(label.getWaringOrderId())) {
                        LabelAndColStatusResp.LabelDto labeVo = new LabelAndColStatusResp.LabelDto();
                        labeVo.setLabelId(String.valueOf(label.getLableId()));
                        labeVo.setLabelName(label.getLableName());
                        labelList.add(labeVo);
                    }
                }
                //放置告警标签
                labelAndColStatusDto.setLabelList(labelList);
                for (WarningOrderListVO vo : warningOrderCollectionList) {
                    if (orderId.equals(vo.getOrderId())) {
                        labelAndColStatusDto.setStatus(Constants.ONE);
                        break;
                    }
                }
                resList.add(labelAndColStatusDto);
            }
            resp.setResList(resList);
        } catch (Exception e) {
            log.error("getLabelAndColStatusByOrders异常:{}", e);
        }
        return resp;
    }

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getOrderListByDeviceChannelAndPresetId(AlarmOrderParmaDto dto) {
        log.info("getOrderListByDeviceChannelAndPresetId:{}", JSON.toJSONString(dto));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        boolean adminFlag = Constants.YES_FLAG.equals(loginUser.getUser().getIsTenantAdmin());

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        List<WarningOrderResultListVo> resultList = new ArrayList<>();
        // 总数
        int allCount = 0;
        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        // 需要查询的个数
        int limit = dto.getPageSize();
        // 分页开始的数
        int offset = (dto.getPageNum() - 1) * limit;
        // 当前表查询开始的数
        int tableStartNum = 0;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            //如果未传告警时间,设置默认时间范围（半年内）- 兼容APP调用
            startDate = DateUtils.getForeSixMonth(new Date());
        } else {
            startDate = dto.getAlarmTimeStart();
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = dto.getAlarmTimeEnd();
        }
        dto.setAlarmTimeStart(startDate);
        dto.setAlarmTimeEnd(endDate);
        int count = 0;
        boolean stop = false;
        int errCount = 1;
        if (StringUtil.isNotEmpty(dto.getOrderStatus()) && dto.getOrderStatus().equals(Constants.NINE)) {
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        if (!CollectionUtils.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(Constants.NINE)) {
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if ((!CollUtil.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode())) || (StringUtil.isNotEmpty(dto.getOrderStatus()) && EnumUtil.FlowNode.END.getCode().contains(dto.getOrderStatus()))) {
            List<String> orderStatusList = new ArrayList<String>();
            if ((StringUtil.isNotEmpty(dto.getOrderStatus())) && (dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))) {
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            } else {
                orderStatusList = dto.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            dto.setOrderStatusList(orderStatusList);
        }
        // 根据排序规则判断从最后一个表开始向前获取或从开始向后获取
        while (!stop) {
            try {
                //正序从开始获取
                String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);

                dto.setTableName(tableName);
                count = warningOrderMapper.queryAlarmOrderCountTwo(dto);
                // 累计总数
                allCount += count;
                if (allCount > offset && count > 0 && limit > 0) {
                    tableStartNum = offset - (allCount - count);
                    if (tableStartNum < 0) {
                        tableStartNum = 0;
                    }
                    if (count > limit) {
                        dto.setLimit(limit);
                    } else {
                        dto.setLimit(count);
                    }
                    dto.setOffset(tableStartNum);
                    List<WarningOrderResultListVo> queryResult = warningOrderMapper.queryAlarmOrderTwo(dto);
                    resultList.addAll(queryResult);
                    limit = limit - queryResult.size();
                }
                stop = DateUtils.sameMouth(startDate, endDate);

                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);

            } catch (Exception e) {
                log.error("getOrderListByDeviceChannelAndPresetId：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if (errCount > 3) {
                    return tableDataInfo;
                }

                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }

        }
        // 查询告警类型名称
        List<Map> alramTypeList = this.getAlarmTypeList();
        //告警状态
        List<SysDictDataResponse> orderStatusEnum = this.getEnumList(Constants.ORDER_STATUS);
        // 告警来源枚举
        List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
        for (WarningOrderResultListVo vo : resultList) {
            //类型
            alramTypeList.forEach(item -> {
                if (item.get(com.chinatower.event.constants.Constants.PARAM_TYPE_VALUE_).toString().equals(vo.getWarningTypeId())) {
                    vo.setWarningTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
                }
            });
            //状态
            orderStatusEnum.forEach(item -> {
                if (item.getDictValue().equals(vo.getOrderStatus())) {
                    vo.setOrderStatusName(item.getDictLabel());
                }
            });
            //来源
            warningSourceEnum.forEach(item -> {
                if (item.getDictValue().equals(vo.getWarningSource())) {
                    vo.setWarningSourceName(item.getDictLabel());
                }
            });
            WarningOrderListVO orderVo = new WarningOrderListVO();
            orderVo.setProcessId(vo.getProcessId());
            vo.setIfEdit(ifEditHandle(adminFlag, loginUser.getUser().getUserId(), orderVo, null));
        }
        tableDataInfo.setTotal(allCount);
        tableDataInfo.setRows(resultList);
        return tableDataInfo;
    }

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", typeValueColumn = "dataTable.warning_type_id")
    public List<WarningOrderResultCountVo> getOrderListByDeviceChannelAndPresetIdGroup(AlarmOrderParmaDto dto) {
        log.info("getOrderListByDeviceChannelAndPresetIdGroup:{}", JSON.toJSONString(dto));
        // 获取当前的用户
        List<WarningOrderResultCountVo> resultList = new ArrayList<>();
        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        // 当前表查询开始的数
        int tableStartNum = 0;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            //如果未传告警时间,设置默认时间范围（半年内）- 兼容APP调用
            startDate = DateUtils.getForeSixMonth(new Date());
        } else {
            startDate = dto.getAlarmTimeStart();
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = dto.getAlarmTimeEnd();
        }
        dto.setAlarmTimeStart(startDate);
        dto.setAlarmTimeEnd(endDate);
        boolean stop = false;
        int errCount = 1;
        // 根据排序规则判断从最后一个表开始向前获取或从开始向后获取
        while (!stop) {
            try {
                //正序从开始获取
                String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);

                dto.setTableName(tableName);
                List<WarningOrderResultCountVo> list = warningOrderMapper.queryAlarmOrderGroup(dto);
                resultList.addAll(list);
                stop = DateUtils.sameMouth(startDate, endDate);

                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);

            } catch (Exception e) {
                log.error("getOrderListByDeviceChannelAndPresetIdGroup：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if (errCount > 3) {
                    return new ArrayList<>();
                }

                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }

        }
        List<WarningOrderResultCountVo> realList = new ArrayList<>();
        Map<String, WarningOrderResultCountVo> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            for (WarningOrderResultCountVo vo : resultList) {
                if (map.containsKey(vo.getDeviceCode() + vo.getWarningSource())) {
                    vo.setSum(map.get(vo.getDeviceCode() + vo.getWarningSource()).getSum() + vo.getSum());
                    map.put(vo.getDeviceCode() + vo.getWarningSource(), vo);
                } else {
                    map.put(vo.getDeviceCode() + vo.getWarningSource(), vo);
                }
            }
            for (Map.Entry<String, WarningOrderResultCountVo> entry : map.entrySet()) {
                realList.add(entry.getValue());
            }
        }
        return realList;
    }

    @Override
    public List<WarningOrderResultCountVo> getOrderListByLot(AlarmOrderParmaDto dto) {
        log.info("getOrderListByDeviceChannelAndPresetIdGroup:{}", JSON.toJSONString(dto));
        // 获取当前的用户
        List<WarningOrderResultCountVo> resultList = new ArrayList<>();
        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        // 当前表查询开始的数
        int tableStartNum = 0;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            //如果未传告警时间,设置默认时间范围（半年内）- 兼容APP调用
            startDate = DateUtils.getForeSixMonth(new Date());
        } else {
            startDate = dto.getAlarmTimeStart();
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = dto.getAlarmTimeEnd();
        }
        dto.setAlarmTimeStart(startDate);
        dto.setAlarmTimeEnd(endDate);
        boolean stop = false;
        int errCount = 1;
        // 根据排序规则判断从最后一个表开始向前获取或从开始向后获取
        while (!stop) {
            try {
                //正序从开始获取
                String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);

                dto.setTableName(tableName);
                List<WarningOrderResultCountVo> list = warningOrderMapper.queryAlarmOrderLot(dto);
                resultList.addAll(list);
                stop = DateUtils.sameMouth(startDate, endDate);

                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);

            } catch (Exception e) {
                log.error("getOrderListByDeviceChannelAndPresetIdGroup：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if (errCount > 3) {
                    return new ArrayList<>();
                }

                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }

        }
        List<WarningOrderResultCountVo> realList = new ArrayList<>();
        Map<String, WarningOrderResultCountVo> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            for (WarningOrderResultCountVo vo : resultList) {
                if (map.containsKey(vo.getDeviceCode() + vo.getWarningSource())) {
                    vo.setSum(map.get(vo.getDeviceCode() + vo.getWarningSource()).getSum() + vo.getSum());
                    map.put(vo.getDeviceCode() + vo.getWarningSource(), vo);
                } else {
                    map.put(vo.getDeviceCode() + vo.getWarningSource(), vo);
                }
            }
            for (Map.Entry<String, WarningOrderResultCountVo> entry : map.entrySet()) {
                realList.add(entry.getValue());
            }
        }
        return realList;
    }

    private List<AlarmOrderByDeviceDTO> getAlarmOrderByDeviceDTOS(AlarmOrderDTO dto) {
        List<AlarmOrderByDeviceDTO> devList;
        if (!StringUtils.isEmpty(dto.getIotDeviceCodeOrName())) {
            devList = deviceRepository.getAlarmOrderByIotDevice(dto);
        } else {
            devList = deviceRepository.queryAlarmOrderByDevice(dto);
        }
        if (!CollectionUtils.isEmpty(devList)) {
            dto.setDevList(devList);
        }
        return devList;
    }

    /**
     * 根据告警类型关键字查询告警类型id
     *
     * @param dto
     * @return
     */
    private List<String> getWarningTypeIdListByKeyWord(AlarmOrderDTO dto) {
        List<String> warningTypeIds = new ArrayList<>();
        if (!StringUtils.isEmpty(dto.getWarningTypeNameKeyWord())) {
            //根据告警类型名称查询告警类型id集合
            warningTypeIds = alarmTypeMapper.findOrderAlarmByKeyWord(dto.getWarningTypeNameKeyWord());
            if (!CollectionUtils.isEmpty(warningTypeIds)) {
                dto.setWarningTypeIdList(warningTypeIds);
            }
        }
        return warningTypeIds;
    }

    /**
     * 根据地址关键词查询对应的经纬度
     *
     * @param dto
     * @return
     */
    private PoiGeocodingQueryRes getLocationByKeyWord(AlarmOrderDTO dto) {
        PoiGeocodingQueryRes queryRes = new PoiGeocodingQueryRes();
        if (!StringUtils.isEmpty(dto.getAddressKeyWord())) {
            PoiGeocodingQueryReq req = new PoiGeocodingQueryReq();
            req.setAddress(dto.getAddressKeyWord());
            //根据地址信息查询对应经纬度
            log.info("getLocationByKeyWord-req:{}", JSON.toJSONString(req));
            Result<List<PoiGeocodingQueryRes>> res = gisMapApi.poiGeocodingQuery(req);
            log.info("getLocationByKeyWord-resp:{}", JSON.toJSONString(res));
            if (res != null && !CollectionUtils.isEmpty(res.getData())) {
                String location = res.getData().get(0).getLocation();
                if (!StringUtils.isEmpty(location)) {
                    String[] loc = location.split(COMMA);
                    dto.setLongitude(loc[0]);
                    dto.setLatitude(loc[1]);
                    dto.setRadius("3000");
                }
                return res.getData().get(0);
            }
        }
        return queryRes;
    }

    private List<AlarmLableRelDTO> getAlarmLableRelDTOS(AlarmOrderDTO dto) {
        List<AlarmLableRelDTO> alarmLableRelDTOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
            alarmLableRelDTOList = alarmLableRelRepository.getAlarmLableByIds(dto.getLableIdList(), dto.getOrderId());
        }
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
        //如果有告警类型标签查询，并且结果为空，走此逻辑
        if (!CollectionUtils.isEmpty(param.getDto().getTagIdList()) && CollectionUtils.isEmpty(alarmTagsTypeRelDTOList)) {
            warningTypeIdList.add(CommonConstant.NO_TAG_DATA);
        }
        if (!CollectionUtils.isEmpty(param.getDto().getWarningTypeIdList())) {
            warningTypeIdList.addAll(param.getDto().getWarningTypeIdList());
        }
        param.getDto().setWarningTypeIdList(warningTypeIdList);
        return param;
    }

    @Override
    public String getIfEdit(IfEditReq dto) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回不可编辑
            return Constants.ZERO;
        }
        if (Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin())) {
            return Constants.ONE;
        }
        dto.setUserId(loginUser.getUser().getUserId());
        String alarmTableName = TableConstant.WARN_ORDER_TEBLE + dto.getWarningOrderId().substring(0, 6);
        dto.setTableName(alarmTableName);
        String warningOrderId = warningOrderMapper.getIfEdit(dto);
        if (StringUtils.isEmpty(warningOrderId)) {
            return Constants.ZERO;
        } else {
            return Constants.ONE;
        }
    }

    /**
     * 根据工单ID查询事件，返回收藏的事件来源
     *
     * @param dto
     * @return
     */
    @Override
    public Map<String, Object> getCollectionSourceByOrderId(AlarmOrderDTO dto) {
        Map<String, Object> mapResult = new HashMap<>();
        String tableName = TableConstant.WARN_ORDER_TEBLE + dto.getOrderId().substring(0, 6);
        dto.setTableName(tableName);
        //收藏事件来源：0：事件查询、1—回收站
        String isCollectionSource = "";
        int count = warningOrderMapper.selectWarningOrderCount(dto);
        if (count > 0) {
            isCollectionSource = Constants.ZERO;
        } else {
            InterceptVO interceptVO = interceptService.getInterceptInfo(dto.getOrderId(), null);
            if (interceptVO != null) {
                isCollectionSource = Constants.ONE;
            }
        }
        mapResult.put("isCollectionSource", isCollectionSource);
        return mapResult;
    }

    public void getALarmLableList(List<WarningOrderListVO> orderListVO) {
        if (!CollectionUtils.isEmpty(orderListVO)) {
            List<String> ordIdList = orderListVO.stream().filter(item -> !StringUtils.isEmpty(item.getOrderId())).map(WarningOrderListVO::getOrderId).collect(Collectors.toList());
            List<AlarmLableRelDTO> alarmLableAllList = alarmLableRelMapper.getAlarmLableByOrdId(ordIdList);
            for (WarningOrderListVO orderVo : orderListVO) {
                //告警标签回显
                List<AlarmLableRelAPIDTO> alarmLableRelDTOS = new ArrayList<>();
                List<AlarmLableRelDTO> alarmLableRelAPIDTOs = alarmLableAllList.stream().filter(item -> orderVo.getOrderId().equals(item.getWaringOrderId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(alarmLableRelAPIDTOs)) {
                    alarmLableRelAPIDTOs.forEach(itemRe -> {
                        AlarmLableRelAPIDTO relAPIDTO = new AlarmLableRelAPIDTO();
                        BeanUtils.copyProperties(itemRe, relAPIDTO);
                        alarmLableRelDTOS.add(relAPIDTO);
                    });
                }
                orderVo.setAlarmLableRelDTOList(alarmLableRelDTOS);
            }
        }
    }

    /**
     * 处理传入的组织条件
     *
     * @param dto
     */
    public void handleOrgIdList(AlarmOrderDTO dto) {
        if (!CollUtil.isEmpty(dto.getOrgIdList())) {
            SysDept dept = new SysDept();
            dept.setDeptIdList(dto.getOrgIdList());
            dept.setTenantId(dto.getSysUser().getTenantId());
            dept.setIndustryCode(dto.getSysUser().getIndustryCode());
            //查询组织下所有用户
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

            //摄像机、雷达、物联、无人机设备编码集合
            deviceCodes.addAll(cameras.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
            //deviceCodes.addAll(radars.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
            deviceCodes.addAll(sensors.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
            deviceCodes.addAll(uavs.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));

            //卫星区域集合（去重）
            List<SysDeptDeviceVO> satelliteList = new ArrayList<>(satellites.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + "-" + o.getCityId() + "-" + o.getCountyId()))), ArrayList::new)));
            if (!CollectionUtils.isEmpty(satelliteList)) {
                dto.setSatelliteList(satelliteList);
            }
            if (!CollectionUtils.isEmpty(deviceCodes)) {
                dto.setDeviceCodeList(deviceCodes);
            }

            //雷达区域集合（去重）
            List<SysDeptDeviceVO> radarList = new ArrayList<>(radars.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + "-" + o.getCityId() + "-" + o.getCountyId()))), ArrayList::new)));
            if (!CollectionUtils.isEmpty(radarList)) {
                dto.setRadarList(radarList);
            }

        }
    }

    @Override
    public GridInfo getGridInfoByLongitudeAndLatitude(GridInfoReq req) {
        log.info("据位置及租户id行业版本获取网格信息入参：{}", JSON.toJSONString(req));
        // 根据经纬度获取网格信息
        GridInfo returnGridInfo = new GridInfo();
        try {
            // 获取当前登录人
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (!StringUtils.isEmpty(req.getLatitude()) && !StringUtils.isEmpty(req.getLongitude())) {
                Result<List<GridByLngLat>> result = this.getMessageByLngLat(loginUser, req.getLatitude(), req.getLongitude());
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                    List<GridByLngLat> gridList = result.getData();
                    if (!CollectionUtils.isEmpty(gridList)) {
                        GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(loginUser.getUser().getIndustryCode(), grid.getIndustryCode()))
                                .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                        returnGridInfo = warningOrderUtil.getGrifInfo(Long.valueOf(gridInfo.getGridId()));
                        if (returnGridInfo != null && !StringUtils.isEmpty(returnGridInfo.getGridId())) {
                            List<Map> keepers = deviceRepository.getGridKeeperByGridIds(String.valueOf(returnGridInfo.getGridId()));
                            if (!StringUtils.isEmpty(keepers) && keepers.size() > 0) {
                                //网格长加网格员名称
                                String name = keepers.stream().map(r -> String.valueOf(r.get("nikeName"))).collect(Collectors.joining(","));
                                //网格长名称
                                String names = keepers.stream().filter(r -> r.get("keeperType").equals("1")).map(n -> String.valueOf(n.get("nikeName"))).collect(Collectors.joining(","));
                                returnGridInfo.setKeeperName(name);
                                returnGridInfo.setGridKeeperName(names);
                            }
                        }
                    }
                }
            }
            if (!StringUtils.isEmpty(req.getLongitude()) && !StringUtils.isEmpty(req.getLatitude())
                    && !StringUtils.isEmpty(req.getDeviceLongitude()) && !StringUtils.isEmpty(req.getDeviceLatitude())) {
                //根据设备的经纬度和事发地点的经纬度计算距离位置
                double distance = DistanceUtils.getDistance(Double.parseDouble(req.getLongitude()), Double.parseDouble(req.getLatitude()),
                        Double.parseDouble(req.getDeviceLongitude()), Double.parseDouble(req.getDeviceLatitude()));
                // 事发地点距离
                returnGridInfo.setIncidentDistance(String.format("%.2f", distance));
            }
        } catch (Exception e) {
            log.error("getGridInfoByAddress错误:", e);
        }
        return returnGridInfo;
    }

    /**
     * 功能描述:
     * 获取领导批示列表
     *
     * @param dto
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", typeValueColumn = "dataTable.warning_type_id")
    public List<LeaderAuidtListVO> getLeaderAuditList(LeaderAuidtListDTO dto) {
        long start = System.currentTimeMillis();
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(dto.getTenantId())) {
            dto.setTenantId(loginUser.getUser().getTenantId());
        }
        dto.setUserId(sysService.getLoginUserId());
        dto.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
        Map<Function, Object> paramMap = new ConcurrentHashMap<>();
        AlarmOrderDTO orderDTO = new AlarmOrderDTO();
        orderDTO.setTagIdList(dto.getTagIdList());
        orderDTO.setWarningTypeIdList(dto.getWarningTypeIdList());
        //告警类型标签
        Param param = new Param();
        param.setDto(orderDTO);
        param.setInfo(new TableDataInfo());
        param.setUser(loginUser);
        //告警类型标签
        Function<Param, Param> fun1 = this::getTableDataInfo;
        paramMap.put(fun1, param);
        AlarmOrderDTO orderDto = new AlarmOrderDTO();
        orderDto.setLableIdList(dto.getLableIdList());
        orderDto.setOrderId(dto.getOrderId());
        Function<AlarmOrderDTO, List<AlarmLableRelDTO>> fun2 = this::getAlarmLableRelDTOS;
        paramMap.put(fun2, orderDto);
        //排序字段
        if (!StringUtils.isEmpty(dto.getSortName()) && !Constants.PARAM_AUDIT_TIME.equals(dto.getSortName())
                && !Constants.PARAM_ALARM_TIME.equals(dto.getSortName()) && !Constants.PARAM_APPLY_TIME.equals(dto.getSortName())
                && !Constants.PARAM_IMPORTANCE_LEVEL.equals(dto.getSortName())) {
            dto.setSortName(null);
        }
        //排序规则
        if (!StringUtils.isEmpty(dto.getSort()) && !EnumUtil.Order.ASC.getName().equals(dto.getSort()) && !EnumUtil.Order.DESC.getName().equals(dto.getSort())) {
            dto.setSort(EnumUtil.Order.DESC.getName());
        }
        //如果没传告警时间，设置默认时间范围（半年内）-兼容App调用
        if (Objects.isNull(dto.getAlarmTimeStart()) || Objects.isNull(dto.getAlarmTimeEnd())) {
            dto.setAlarmTimeStart(DateUtils.dateToStringNoMS(DateUtils.getForeSixMonth(new Date())));
            dto.setAlarmTimeEnd(DateUtils.dateToStringNoMS(new Date()));
        }

        //跨库获取页面查询设备信息关联数据
        AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
        alarmOrderDTO.setDeviceLabelId(dto.getDeviceLabelId());
        alarmOrderDTO.setDeviceLabelIdList(dto.getDeviceLabelIdList());
        alarmOrderDTO.setOrgId(dto.getOrgId());
        alarmOrderDTO.setOrgIdList(dto.getOrgIdList());
        alarmOrderDTO.setModelId(dto.getModelId());
        alarmOrderDTO.setModelIds(dto.getModelIds());
        alarmOrderDTO.setTenantId(dto.getTenantId());
        alarmOrderDTO.setSysUser(loginUser.getUser());
        alarmOrderDTO.setDeviceName(dto.getDeviceName());
        alarmOrderDTO.setIotDeviceCodeOrName(dto.getIotDeviceCodeOrName());
        //处理传入的组织条件
        handleOrgIdList(alarmOrderDTO);
        //跨库获取页面查询设备信息关联数据
        Function<AlarmOrderDTO, List<AlarmOrderByDeviceDTO>> fun3 = this::getAlarmOrderByDeviceDTOS;
        paramMap.put(fun3, alarmOrderDTO);
        List<Function> listFunc = Arrays.asList(fun1, fun2, fun3);
        ThreadParallelUtil.run(listFunc, f -> {
            Object o = paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_LEADER);

        if (CollectionUtils.isEmpty(alarmOrderDTO.getDevList()) && (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(dto.getModelId()) || !StringUtils.isEmpty(dto.getDeviceName()) ||
                !CollectionUtils.isEmpty(dto.getModelIds()) || !StringUtils.isEmpty(dto.getIotDeviceCodeOrName()))) {
            return new ArrayList<>();
        }

        dto.setWarningTypeIdList(orderDTO.getWarningTypeIdList());
        if (!CollectionUtils.isEmpty(orderDto.getAlarmLableRelDTOList())) {
            List<String> warningOrderIds = new ArrayList<>();
            orderDto.getAlarmLableRelDTOList().forEach(item -> {
                if (!StringUtils.isEmpty(item.getWaringOrderId())) {
                    warningOrderIds.add(item.getWaringOrderId());
                }
            });
            if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                dto.setWarningOrderIds(warningOrderIds);
            }
        } else if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
            return new ArrayList<>();
        }
        dto.setDevList(alarmOrderDTO.getDevList());
        dto.setDeviceCodeList(alarmOrderDTO.getDeviceCodeList());
        List<SysDeptDeviceVO> satelliteList = new ArrayList<>();
        BeanUtil.copyProperties(alarmOrderDTO.getSatelliteList(), satelliteList);
        dto.setSatelliteList(satelliteList);
        dto.setRadarList(alarmOrderDTO.getRadarList());
        dto.setUserIdList(alarmOrderDTO.getUserIdList());
        dto.setOrgUserListString(alarmOrderDTO.getOrgUserListString());

        // 领导批示表无 sub_scenes：按告警时间展开工单月表，供 EXISTS 过滤下游任务
        if (!SubScenesOrderTableHelper.fillWarningOrderTableList(
                dto.getSubSceneIdList(), dto.getAlarmTimeStart(), dto.getAlarmTimeEnd(),
                dto::setWarningOrderTableList)) {
            return new ArrayList<>();
        }

        PageMethod.startPage(dto.getPageNum(), dto.getPageSize());
        if (!dto.isAdmin() && dto.getDataScope() != null) {
            String dataScopeTemp = dto.getDataScope().substring(dto.getDataScope().indexOf("and") + 4);
            dto.setDataScopeTemp(dataScopeTemp);
        }
        List<LeaderAuidtListVO> resultList = warningOrderMapper.getLeaderAuditList(dto);
        List<String> userIdList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_YMDHMS);
        //告警结果集
        List<Map> warningOrderList = new ArrayList<>();
        //领导批示 告警数据月份结果集
        List<String> warningOrderYMList = new ArrayList<>();
        //领导批示 告警数据月份数据结果集
        Map<String, List<String>> map = new HashMap<>();
        for (LeaderAuidtListVO vo : resultList) {
            userIdList.add(vo.getLeaderId());
            userIdList.add(vo.getApplyUserId());
            //告警标签回显
            List<AlarmLableRelDTO> alarmLableRelAPIDTOs = orderDto.getAlarmLableRelDTOList().stream().filter(item -> vo.getOrderId().equals(item.getWaringOrderId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(alarmLableRelAPIDTOs)) {
                vo.setAlarmLableRelDTOList(alarmLableRelAPIDTOs);
            }

            String orderId = String.valueOf(vo.getOrderId());
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
            } catch (Exception e) {
                log.error("领导批示查询告警表:{},异常：", tableName, e);
            }
        }
        List<SysUserVO> userList = deviceRepository.getUserInfoByUserIds(userIdList);
        if (!CollectionUtils.isEmpty(resultList)) {
            // 查询告警类型名称
            List<Map> alramTypeList = getAlarmTypeList();
            // 告警来源枚举
            List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
            // 告警等级枚举
            List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(ALARM_LEVEL).getData();
            // 第三方告警来源
            List<AlarmTypeVO> alarmTypeVOList = vmAlarmFlowConfigMapper.getThirdPartyAlarmTypes(null, null);
            Map<String, String> thirdPartySourceMap = alarmTypeVOList.stream().collect(Collectors.toMap(item -> item.getTenantId() + item.getIndustryCode(),
                    item -> item.getAlarmSourceName(), (o, n) -> n, HashMap::new));
            // 督办记录
            List<String> orderIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getOrderId()))
                    .map(item -> item.getOrderId()).distinct().collect(Collectors.toList());
            List<HandleRecordVO> handleRecordList = getHandleRecordResult(orderIdList);
            //v1.7.00 查询组织信息
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
            List<Map<String, String>> orgInfoList = this.getOrgInfoList(paramList);
            for (LeaderAuidtListVO m : resultList) {
                warningSourceEnum.forEach(item -> {
                    if (item.getDictValue().equals(m.getWarningSource())) {
                        m.setWarningSourceName(item.getDictLabel());
                    }
                });
                if (!StringUtils.isEmpty(m.getUserDistance())) {
                    String userDistance = m.getUserDistance();
                    String cleanedUserDistance = userDistance.replaceAll(REGEX, ""); // 移除非数字字符
                    double num = Double.parseDouble(cleanedUserDistance);
                    DecimalFormat df = new DecimalFormat("#.00");
                    String realDistance = "";
                    if (num > 1000) {
                        //如果大于一千米，单位为千米保留两位小数
                        double result = num / 1000.0;
                        realDistance = df.format(result);
                        m.setUserDistance(realDistance + "千米");
                    } else {
                        BigDecimal decimal = new BigDecimal(cleanedUserDistance);
                        int rounded = decimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                        m.setUserDistance(String.valueOf(rounded) + "米");
                    }
                }
                //置信度 取最大置信度展示  -- 新增综合置信度，若存在综合置信度则展示综合置信度
                if (!StringUtils.isEmpty(m.getCompConfidenceLevel())) {
                    m.setConfidenceLevel(m.getCompConfidenceLevel());
                } else {
                    String confidenceLevel = m.getConfidenceLevel();
                    if (!StringUtils.isEmpty(confidenceLevel)) {
                        String[] confidenceLevelList = confidenceLevel.split(Constants.COMMA);
                        Optional<String> confidenceLevelOpt = Arrays.stream(confidenceLevelList).max(String::compareTo);
                        if (confidenceLevelOpt.isPresent()) {
                            String confidenceLevelNew = confidenceLevelOpt.get();
                            m.setConfidenceLevel(confidenceLevelNew);
                        }
                    }
                }
                boolean leaderFlag = false;
                boolean applyFlag = false;
                for (SysUserVO vo : userList) {
                    if (String.valueOf(vo.getUserId()).equals(m.getLeaderId())) {
                        m.setLeaderName(vo.getNickName());
                        leaderFlag = true;
                    }
                    if (String.valueOf(vo.getUserId()).equals(m.getApplyUserId())) {
                        m.setApplyUserName(vo.getNickName());
                        applyFlag = true;
                    }
                    if (leaderFlag && applyFlag) {
                        break;
                    }
                }
                // 设置图标
                if (!CollectionUtils.isEmpty(alramTypeList)) {
                    alramTypeList.forEach(item -> {
                        if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(m.getWarningTypeId())) {
                            m.setWarningName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
                            m.setIcon(String.valueOf(item.get(Constants.PARAM_ICON)));
                        }
                    });
                }
                //从工单表获取透传字段
                warningOrderList.forEach(item -> {
                    if (m.getOrderId().equals(item.get(Constants.RES_WARNING_ORDER_ID))) {
                        m.setExtraField(String.valueOf(item.get(Constants.WARNING_ORDER_EXTRA_FIELD)));
                        // 通过工单表当前的状态以及时限展示剩余超期时长
                        String flowNodeTime = MapUtils.getString(item, Constants.PARAM_FLOW_NODE_TIME_DB);
                        if (Objects.nonNull(flowNodeTime) && !orderStatusList.contains(MapUtils.getString(item, Constants.PARAM_ORDER_STATUS_DB))) {
                            try {
                                Date nodeTime = format.parse(flowNodeTime);
                                m.setDuration(DateUtils.getTimeDifference2(new Date(), nodeTime));
                                if (Objects.nonNull(MapUtils.getString(item, Constants.PARAM_TIME_LIMIT_DB)) && Integer.valueOf(MapUtils.getString(item, Constants.PARAM_TIME_LIMIT_DB)) != -1 && !EnumUtil.FlowNode.END.getCode().equals(MapUtils.getString(item, Constants.PARAM_ORDER_STATUS_DB))) {
                                    m.setIfTimeout(DateUtils.checkIfTimeOut(Integer.valueOf(MapUtils.getString(item, Constants.PARAM_TIME_LIMIT_DB)), nodeTime));
                                    if (m.getIfTimeout() == 0) {
                                        m.setShowTime("剩余" + DateUtils.getTimeDifference(DateUtils.subtractTimeMinutes(nodeTime, Integer.valueOf(MapUtils.getString(item, Constants.PARAM_TIME_LIMIT_DB))), new Date()));
                                    } else {
                                        // 超期
                                        m.setShowTime("超期" + DateUtils.getTimeDifference(new Date(), DateUtils.subtractTimeMinutes(nodeTime, Integer.valueOf(MapUtils.getString(item, Constants.PARAM_TIME_LIMIT_DB)))));
                                    }
                                }
                            } catch (Exception e) {
                                log.error("获取领导批示时间showTime duration 字段出错");
                            }
                        }
                        // 领导批示列表实时展示事件当时的事发地点
                        if (ObjectUtil.isNotNull(item.get(Constants.PARAM_ADDRESS))) {
                            m.setAddress(String.valueOf(item.get(Constants.PARAM_ADDRESS)));
                        }
                        //实时同步经纬度，供APP计算用户离事发地点距离使用
                        if (!StringUtils.isEmpty(item.get(Constants.PARAM_LONGITUDE))) {
                            m.setLongitude(item.get(Constants.PARAM_LONGITUDE).toString());
                        }
                        if (!StringUtils.isEmpty(item.get(Constants.PARAM_LATITUDE))) {
                            m.setLatitude(item.get(Constants.PARAM_LATITUDE).toString());
                        }
                        //v1.6.10 实时展示告警事件状态名称，供APP展示
                        if (item.get(Constants.PARAM_ENABLE_ACCEPTANCE_MODE).equals(Constants.ONE)) {
                            //如果开启了受理，则设置告警状态为‘待受理’
                            m.setOrderStatusName(Constants.PENDING_ACCEPTANCE);
                            m.setOrderStatus(item.get(Constants.PARAM_ORDER_STATUS_DB).toString());
                            m.setEnableAcceptanceMode(item.get(Constants.PARAM_ENABLE_ACCEPTANCE_MODE).toString());
                        } else {
                            m.setEnableAcceptanceMode(item.get(Constants.PARAM_ENABLE_ACCEPTANCE_MODE).toString());
                            if (ObjectUtil.isNotNull(item.get(Constants.PARAM_ORDER_STATUS_DB))) {
                                List<SysDictDataResponse> orderStatusEnum = this.getEnumList(Constants.ORDER_STATUS);
                                if (!CollectionUtils.isEmpty(orderStatusEnum)) {
                                    orderStatusEnum.forEach(o -> {
                                        if (o.getDictValue().equals(item.get(Constants.PARAM_ORDER_STATUS_DB))) {
                                            m.setOrderStatus(o.getDictValue());
                                            m.setOrderStatusName(o.getDictLabel());
                                        }
                                    });
                                }
                            }
                        }
                        //v1.6.10 重要等级名称
                        if (ObjectUtil.isNotNull(item.get(Constants.PARAM_IMPORTANCE_LEVEL_DB))) {
                            if (!CollectionUtils.isEmpty(importanceLevelEnum)) {
                                importanceLevelEnum.forEach(o -> {
                                    if (o.getDictValue().equals(item.get(Constants.PARAM_IMPORTANCE_LEVEL_DB))) {
                                        m.setImportanceLevel(o.getDictValue());
                                        m.setImportanceLevelName(o.getDictLabel());
                                    }
                                });
                            }
                        }
                        //v1.6.40 第三方告警来源名称
                        if (Constants.WARNING_SOURCE_11.equals(m.getWarningSource())) {
                            m.setWarningSourceName(thirdPartySourceMap.get(item.get(Constants.RES_TENANT_ID).toString() + item.get(Constants.PARAM_INDUSTRY_CODE).toString()));
                        }
                        //v1.6.80 是否显示督办提示图标
                        m.setIsHandleDisplay(Constants.ZERO);
                        if (!CollectionUtils.isEmpty(handleRecordList)) {
                            handleRecordList.forEach(handleRecordVO -> {
                                if (m.getOrderId().equals(handleRecordVO.getOrderId()) && item.get(Constants.PARAM_RES_LINK_ID).equals(handleRecordVO.getLinkId())) {
                                    m.setIsHandleDisplay(Constants.ONE);
                                }
                            });
                        }
                        //v1.7.00 展示所属组织
                        if (!CollectionUtils.isEmpty(orgInfoList)) {
                            for (Map<String, String> orgMap : orgInfoList) {
                                String orderId = m.getOrderId();
                                if (orgMap.containsKey(orderId)) {
                                    m.setOrgName(orgMap.get(orderId));
                                    break;
                                }
                            }
                        }
                    }
                });
                //获取卫星告警专题图和红外图
                if (Constants.NINE.equals(m.getWarningSource())) {
                    if (!StringUtil.isEmpty(m.getExtraField()) && !"null".equals(m.getExtraField())) {
                        SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(m.getExtraField(), SatelliteExtendFieldVo.class);
                        m.setRelativePathThem(extendFieldVo.getRelativePathThem());
                        m.setRelativePathMir(extendFieldVo.getRelativePathMir());
                    }
                }

                // 获取表名
                // String tableName = TableConstant.WARN_ORDER_TEBLE +
                // m.get(Constants.PARAM_ORDER_ID).toString().substring(0,6);

                // 查询告警详情
                // Map param = new HashMap();
                // param.put(Constants.PARAM_TABLE_NAME,tableName);
                // param.put(Constants.PARAM_ORDER_ID,m.get(Constants.PARAM_ORDER_ID));
                //
                // Map alarm = warningOrderDAO.getOrderInfo(param);
                // if(MapUtils.isNotEmpty(alarm)){
                // m.put("address",alarm.get("address"));
                // // 20211127 zzh 多张图片取第一张
                // String imgUrl = (String) alarm.get(Constants.PARAM_FILE_IMG_URL_SRC);
                // m.put(Constants.PARAM_IMG_URL,imgUrl);
                // if(!StringUtils.isEmpty(imgUrl) && imgUrl.indexOf(Constants.COMMA) > 0){
                // m.put(Constants.PARAM_IMG_URL,imgUrl.split(Constants.COMMA)[0]);
                // }
                // }
                String imgUrl = m.getFileImgUrlSrc();
                if (!StringUtils.isEmpty(imgUrl) && imgUrl.contains(Constants.COMMA)) {
                    m.setFileImgUrlSrc(imgUrl.split(Constants.COMMA)[0]);
                }
            }
        }
        log.info("获取领导批示列表查询耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));
        return resultList;
    }

    /**
     * 获取告警类型名称列表
     *
     * @return
     */
    @Override
    public List<Map> getAlarmTypeList() {
        // 从redis获取告警类型名称列表

        // 临时注释，告警类型回显有问题
        //List<Map> alarmTypeList = JSONArray.parseArray((String) RedisUtil.get(Constants.PARAM_ALARM_TYPE_LIST, RedisDBType.PRIMARY), Map.class);
        //if (CollectionUtils.isEmpty(alarmTypeList)) {
//            alarmTypeList = deviceRepository.getAlarmType();
        List<AlarmTypeDO> orderAlarm = alarmTypeRepository.findOrderAlarmAll(new AlarmTypeDTO());
        List<Map> alarmTypeList = orderAlarm.stream().map(type -> {
            Map<String, Object> map = new HashMap<>();
            map.put("typeValue", type.getTypeValue());
            map.put("typeName", type.getTypeName());
            map.put("icon", type.getIcon());
            return map;
        }).collect(Collectors.toList());
        //RedisUtil.set(Constants.PARAM_ALARM_TYPE_LIST, JSON.toJSONString(alarmTypeList), RedisDBType.PRIMARY);
        //}
        return alarmTypeList;
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
     * 获取图斑
     *
     * @param polygonOperateDTO
     * @return
     */
    @Override
    @DataPermissions(layerColumn = "l.id", dbType = "base")
    public Result getPolygonOperate(PolygonOperateDTO polygonOperateDTO) {
        if (StringUtils.isEmpty(polygonOperateDTO.getDeviceCode()) ||
                StringUtils.isEmpty(polygonOperateDTO.getChannelCode()) ||
                StringUtils.isEmpty(polygonOperateDTO.getPresetId())) {
            return Result.data(null);
        }
        if (StringUtils.isEmpty(polygonOperateDTO.getTenantId())) {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            polygonOperateDTO.setTenantId(loginUser.getUser().getTenantId());
        }
        List<Map<String, Object>> polygonOperate = warningOrderMapper.getPolygonOperate(polygonOperateDTO);
        log.info("获取图层信息getPolygonOperate resp:{}" + JSONObject.toJSONString(polygonOperate));
        if (CollectionUtils.isEmpty(polygonOperate)) {
            return Result.data(null);
        }
        String layerId = String.valueOf(polygonOperate.get(0).get("layerId")); //图层ID
        List<String> featureIds = polygonOperate.stream().map(item -> String.valueOf(item.get(Constants.PARAM_FEATURE_ID)))
                .filter(com.chinatower.common.core.utils.StringUtils::isNotEmpty).distinct().collect(Collectors.toList()); //图斑ID
        //通过图层id、图斑ids 调用api接口获取geometry（几何实体）、geoType（点线面类型）
        Map<String, String> geoInfoMap = getGeometry(layerId, featureIds);
        Map geoMap = JSON.parseObject(geoInfoMap.get(Constants.PARAM_GEOMETRY), Map.class);
        //获取图斑面积（提供给国土行业）
        String apotArea = getApotArea(layerId, featureIds);

        for (Map<String, Object> item : polygonOperate) {
            polygonOperateDTO.setMonitorAreaId(String.valueOf(item.get("monitorAreaId")));
            polygonOperateDTO.setIndustryCode(String.valueOf(item.get("industryCode")));
            PolygonPatchVo vo = new PolygonPatchVo();
            BeanUtils.copyProperties(polygonOperateDTO, vo);
            try {
                log.info("获取电子围栏名称queryPolygonPatch req:{}" + JSONObject.toJSONString(vo));
                Result<PolygonPatchConfig> result = iPolygonPatchConfigService.queryPolygonPatch(vo);
                log.info("获取电子围栏名称queryPolygonPatch resp:{}" + JSONObject.toJSONString(result));
                if (!StringUtils.isEmpty(result.getData())) {
                    item.put("polygonName", result.getData().getPolygonName());
                }
            } catch (Exception e) {
                log.error("查询电子围栏名称:{},异常：", vo, e);
            }
            item.put(Constants.PARAM_GEOMETRY, MapUtils.isNotEmpty(geoMap) ? geoMap.get(item.get(Constants.PARAM_FEATURE_ID)) : "");
            item.put(Constants.PARAM_GEOMETRY_TYPE, MapUtils.isNotEmpty(geoInfoMap) ? geoInfoMap.get(Constants.PARAM_GEOMETRY_TYPE) : "");
            item.put("apotArea", apotArea);
        }
        return Result.data(polygonOperate);
    }

    private Map<String, String> getGeometry(String layerId, List<String> featureIds) {
        GetXjxlFeaturesByIdsReq getXjxlFeaturesByIdsReq = new GetXjxlFeaturesByIdsReq();
        getXjxlFeaturesByIdsReq.setLayerID(layerId);
        getXjxlFeaturesByIdsReq.setTbbhs(featureIds);
        getXjxlFeaturesByIdsReq.setReturnGeometry(true);
        log.info("调用gisLayerAPI.getXjxlFeaturesByIds  req:{}", JSON.toJSONString(getXjxlFeaturesByIdsReq));
        Map<String, String> geoMap = new HashMap<>();
        try {
            Result<GeojsonStruckRes> result = gisLayerAPI.getXjxlFeaturesByIds(getXjxlFeaturesByIdsReq);
            log.info("调用gisLayerAPI.getXjxlFeaturesByIds  resp:{}", JSON.toJSONString(result));
            if (Objects.nonNull(result.getData())) {
                List<Map<String, Object>> geomeryList = new ArrayList<>();
                String geoType = null;
                if (Objects.nonNull(result.getData().getGeoData())) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(JSON.toJSONString(result.getData().getGeoData()));
                    JsonNode featuresNode = rootNode.path(Constants.PARAM_FEATURES);
                    if (featuresNode.isArray()) {
                        for (JsonNode feature : featuresNode) {
                            Map<String, Object> map = new HashMap<>();
                            JsonNode coordinatesNode = feature.path(Constants.PARAM_GEOMETRY).path("coordinates");
                            map.put(Constants.PARAM_GEOMETRY, coordinatesNode);
                            JsonNode tbbhNode = feature.path("properties").path("bsm");
                            map.put(Constants.PARAM_FEATURE_ID, tbbhNode.asText().replaceAll("\"", ""));
                            geomeryList.add(map);
                        }
                    }
                }
                if (Objects.nonNull(result.getData().getGeoType())) {
                    String geometryType = JSON.toJSONString(result.getData().getGeoType());
                    geoType = geometryType.substring(1, geometryType.length() - 1);
                }
                Map<String, String> geomeryMap = geomeryList.stream().collect(Collectors.toMap(p -> p.get(Constants.PARAM_FEATURE_ID).toString(), p -> p.get(Constants.PARAM_GEOMETRY).toString(), (o, n) -> n, HashMap::new));
                geoMap.put(Constants.PARAM_GEOMETRY, JSON.toJSONString(geomeryMap));
                geoMap.put(Constants.PARAM_GEOMETRY_TYPE, geoType);
            }
        } catch (Exception e) {
            log.error("====调用getXjxlFeaturesByIds方法 error", e);
        }
        return geoMap;
    }

    private String getApotArea(String layerId, List<String> featureIds) {
        StatisticDkmjReq statisticDkmjReq = new StatisticDkmjReq();
        statisticDkmjReq.setLayerID(layerId);
        ArrayList<String> featureIdsList = new ArrayList<>(featureIds);
        statisticDkmjReq.setFeatureIDs(featureIdsList);
        String apotArea = null;
        try {
            log.info("获取图斑面积接口statisticDkmj req:{}" + JSONObject.toJSONString(statisticDkmjReq));
            Result reqResult = gisLayerAPI.statisticDkmj(statisticDkmjReq);
            log.info("获取图斑面积接口statisticDkmj resp:{}" + JSONObject.toJSONString(reqResult));
            if (reqResult != null && ResultCode.SUCCESS.getCode() == reqResult.getCode() && !StringUtils.isEmpty(reqResult.getData())) {
                apotArea = reqResult.getData().toString();
            }
        } catch (Exception e) {
            log.error("getPolygonOperate gisLayerApi statisticDkmj error, e:", e);
        }
        return apotArea;
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
        if ((!CollUtil.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode())) || (StringUtil.isNotEmpty(dto.getOrderStatus()) && EnumUtil.FlowNode.END.getCode().contains(dto.getOrderStatus()))) {
            List<String> orderStatusList = new ArrayList<String>();
            if ((StringUtil.isNotEmpty(dto.getOrderStatus())) && (dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))) {
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            } else {
                orderStatusList = dto.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            dto.setOrderStatusList(orderStatusList);
        }
        // 已办查询 根据当前用户的用户id和告警表的user_record（逗号拼接）模糊关联
        // 待办查询 根据当前用户的角色、id和环节配置表的处理角色和用户关联，取环节配置id和环节id字段做过滤
        if (Constants.SEARCH_TYPE_1.equals(dto.getSearchType())) {
            dto.setFlowLinkIds(flowLinkIds);
            //待办过滤完结数据
            List<String> orderStatuses = new ArrayList<>();
            orderStatuses.add(EnumUtil.FlowNode.END.getCode());
            orderStatuses.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            orderStatuses.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            dto.setNotInOrderStatus(orderStatuses);
        }
    }

    @EventObsEncrypt
    @Override
    public void resultHandle(List<WarningOrderListVO> resultList, AlarmOrderDTO alarmOrderListReq, String userId, String flowLinkIds) {
        // 智能体扩展字段拆分为独立属性返回前端
        AiAgentExtendFieldUtil.fillAndClearList(resultList);
        //返回领导批示数量
        List<OrderLeaderNumDTO> auditNum = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            auditNum = warningOrderMapper.getLeaderAuditNum(resultList);
        }
        // 查询告警类型名称
        List<Map> alramTypeList = this.getAlarmTypeList();

        List<String> paramList = new ArrayList<>();
        paramList.add(String.valueOf(Constants.EMERGENCY_LEVEL));
        paramList.add(String.valueOf(Constants.ORDER_STATUS));
        paramList.add(String.valueOf(Constants.NOTICE_RESOURCE));
        paramList.add(Constants.WARNING_SOURCE_COMMON);
        paramList.add(Constants.ALARM_LEVEL);
        // 告警等级名称
        AtomicReference<List<SysDictDataResponse>> emergencyLevelEnum = new AtomicReference<>();
        // 告警状态名称
        AtomicReference<List<SysDictDataResponse>> orderStatusEnum = new AtomicReference<>();
        // AI告警类型名称
        AtomicReference<List<SysDictDataResponse>> noticeResourceEnum = new AtomicReference<>();
        // 告警来源枚举
        AtomicReference<List<SysDictDataResponse>> warningSourceEnum = new AtomicReference<>();
        // 重要等级
        AtomicReference<List<SysDictDataResponse>> importanceLevelEnum = new AtomicReference<>();
        ThreadParallelUtil.run(paramList, o -> {
            if (String.valueOf(Constants.EMERGENCY_LEVEL).equals(String.valueOf(o))) {
                emergencyLevelEnum.set(systemUtil.getEnum(o));
            } else if (String.valueOf(Constants.ORDER_STATUS).equals(String.valueOf(o))) {
                orderStatusEnum.set(systemUtil.getEnum(o));
            } else if (String.valueOf(Constants.NOTICE_RESOURCE).equals(String.valueOf(o))) {
                noticeResourceEnum.set(systemUtil.getEnum(o));
            } else if (Constants.WARNING_SOURCE_COMMON.equals(String.valueOf(o))) {
                warningSourceEnum.set(systemUtil.getEnum(o));
            } else if (Constants.ALARM_LEVEL.equals(String.valueOf(o))) {
                importanceLevelEnum.set(systemUtil.getEnum(o));
            }
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER_LIST);

        //查询当前用户收藏事件
        AlarmOrderDTO dto = new AlarmOrderDTO();
        dto.setCollectionCreator(userId);
        List<WarningOrderListVO> warningOrderCollectionList = warningOrderCollectionMapper.qryWarningOrderCollection(dto);
        //查询督办记录
        List<String> orderIdList = resultList.stream().filter(item -> !StringUtils.isEmpty(item.getOrderId()))
                .map(item -> item.getOrderId()).distinct().collect(Collectors.toList());
        List<HandleRecordVO> handleRecordList = getHandleRecordResult(orderIdList);
        //v1.7.00 查询组织信息
        List<OrganInfoReq> params = resultList.stream().map(item -> {
            OrganInfoReq reqDTO = new OrganInfoReq();
            reqDTO.setWarningOrderId(item.getOrderId());
            reqDTO.setWarningSource(item.getWarningSource());
            reqDTO.setDeviceCode(item.getDeviceCode());
            reqDTO.setProvinceId(item.getProvinceId());
            reqDTO.setCityId(item.getCityId());
            reqDTO.setCountyId(item.getCountyId());
            reqDTO.setFindName(item.getFindName());
            reqDTO.setTenantId(alarmOrderListReq.getSysUser().getTenantId());
            reqDTO.setIndustryCode(alarmOrderListReq.getSysUser().getIndustryCode());
            return reqDTO;
        }).collect(Collectors.toList());
        List<Map<String, String>> orgInfoList = this.getOrgInfoList(params);
        List<WarningOrderResultDto> resultDtoList = new ArrayList<>();
        // 处理返回结果集,设置接收时间，是否超期，处理时长,是否可编辑
        for (WarningOrderListVO vo : resultList) {
            WarningOrderResultDto resultDto = new WarningOrderResultDto();
            resultDto.setAlarmOrderListReq(alarmOrderListReq);
            resultDto.setUserId(userId);
            resultDto.setFlowLinkIds(flowLinkIds);
            resultDto.setAuditNum(auditNum);
            resultDto.setAlramTypeList(alramTypeList);
            resultDto.setEmergencyLevelEnum(emergencyLevelEnum.get());
            resultDto.setOrderStatusEnum(orderStatusEnum.get());
            resultDto.setNoticeResourceEnum(noticeResourceEnum.get());
            resultDto.setWarningSourceEnum(warningSourceEnum.get());
            resultDto.setImportanceLevelEnum(importanceLevelEnum.get());
            resultDto.setWarningOrderCollectionList(warningOrderCollectionList);
            resultDto.setHandleRecordList(handleRecordList);
            resultDto.setOrgInfoList(orgInfoList);
            resultDto.setVo(vo);
            resultDtoList.add(resultDto);
        }
        ThreadParallelUtil.run(resultDtoList, f -> {
            long startTime = System.currentTimeMillis(); // 记录任务开始时间
            extracted(f);
            long endTime = System.currentTimeMillis(); // 记录任务结束时间
            long duration = endTime - startTime; // 计算任务处理时间
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER_COMPLETED);
    }

    private void extracted(WarningOrderResultDto resultDto) {

        String deleteUser = "";
        //删除告警需要查询删除人
        if (resultDto.getAlarmOrderListReq().getDeleteFlag() != null && resultDto.getAlarmOrderListReq().getDeleteFlag() == 1) {
            if (!StringUtils.isEmpty(resultDto.getVo().getUserRecord())) {
                if (resultDto.getVo().getUserRecord().contains(Constants.COMMA)) {
                    int index = resultDto.getVo().getUserRecord().lastIndexOf(Constants.COMMA);
                    deleteUser = resultDto.getVo().getUserRecord().substring(index + 1);
                } else {
                    deleteUser = resultDto.getVo().getUserRecord();
                }
            }
            if (!StringUtils.isEmpty(deleteUser)) {
                SysUser user = sysMapper.selectUserOnlyByUserId(deleteUser);
                if (user != null) {
                    resultDto.getVo().setDeleteUser(user.getNickName());
                    resultDto.getVo().setDeleteUserInfo(user);
                }
            }
        }
        //查询返回工单得下个环节是否为多个
        resultDto.getVo().setMoreNextFlag(Constants.ZERO);
        if (!StringUtils.isEmpty(resultDto.getVo().getLinkId()) && !StringUtils.isEmpty(resultDto.getVo().getFlowTemplate()) && !StringUtils.isEmpty(resultDto.getVo().getFlowId())) {
            AlarmFlowConfigModel model = new AlarmFlowConfigModel();
            model.setLinkId(Long.valueOf(resultDto.getVo().getLinkId()));
            model.setTemplateId(Long.valueOf(resultDto.getVo().getFlowTemplate()));
            model.setTenantId(resultDto.getVo().getTenantId());
            model.setId(Long.valueOf(resultDto.getVo().getFlowId()));
            model.setWarningSource(resultDto.getVo().getWarningSource());
            List<FlowModel> flowModelList = flowLinkService.getFlowLink(model);
            if (!CollectionUtils.isEmpty(flowModelList)) {
                if (!StringUtils.isEmpty(flowModelList.get(0).getNextLink()) && flowModelList.get(0).getNextLink().contains(Constants.COMMA)) {
                    resultDto.getVo().setMoreNextFlag(Constants.ONE);
                }
            }
        }
        if (!StringUtils.isEmpty(resultDto.getVo().getUserDistance())) {
            String userDistance = resultDto.getVo().getUserDistance();
            String cleanedUserDistance = userDistance.replaceAll(REGEX, ""); // 移除非数字字符
            double num = Double.parseDouble(cleanedUserDistance);
            String realDistance = "";
            if (num > 1000) {
                //如果大于一千米，单位为千米保留两位小数
                double result = num / 1000.0;
                DecimalFormat df = new DecimalFormat("#.00");
                realDistance = df.format(result);
                resultDto.getVo().setUserDistance(realDistance + "千米");
            } else {
                BigDecimal decimal = new BigDecimal(cleanedUserDistance);
                int rounded = decimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                resultDto.getVo().setUserDistance(String.valueOf(rounded) + "米");
            }
        }
        resultDto.getVo().setReciveTime(resultDto.getVo().getCreateDate());
        resultDto.getVo().setIfTimeout(0);
        if (Objects.nonNull(resultDto.getVo().getFlowNodeTime()) && !orderStatusList.contains(resultDto.getVo().getOrderStatus())) {
            resultDto.getVo().setDuration(DateUtils.getTimeDifference2(new Date(), resultDto.getVo().getFlowNodeTime()));
            //受理状态的工单超期展示受理超期
            if (Constants.ONE.equals(resultDto.getVo().getEnableAcceptanceMode())) {
                if (StringUtils.isEmpty(resultDto.getVo().getTimeLimitAcceptance())) {
                    resultDto.getVo().setTimeLimit(null);
                } else {
                    resultDto.getVo().setTimeLimit(Integer.valueOf(resultDto.getVo().getTimeLimitAcceptance()));
                }
            }
            // timeLimit 为-1 表示不超期
            if (Objects.nonNull(resultDto.getVo().getTimeLimit())
                    && !EnumUtil.FlowNode.END.getCode().equals(resultDto.getVo().getOrderStatus())
                    && resultDto.getVo().getTimeLimit() != -1) {
                resultDto.getVo().setIfTimeout(DateUtils.checkIfTimeOut(resultDto.getVo().getTimeLimit(), resultDto.getVo().getFlowNodeTime()));
                if (resultDto.getVo().getIfTimeout() == 0) {
                    // 未超期
                    resultDto.getVo().setShowTime("剩余" + DateUtils.getTimeDifference(DateUtils.subtractTimeMinutes(resultDto.getVo().getFlowNodeTime(), resultDto.getVo().getTimeLimit()), new Date()));
                } else {
                    // 超期
                    resultDto.getVo().setShowTime("超期" + DateUtils.getTimeDifference(new Date(), DateUtils.subtractTimeMinutes(resultDto.getVo().getFlowNodeTime(), resultDto.getVo().getTimeLimit())));
                }
            }
        }
//            if (!StringUtils.isEmpty(roleId)) {
//                for (String role : roleId.split(Constants.COMMA)) {
//                    if (Constants.ONE_NUM == OrderEditEnum.ifEdit(role,vo.getOrderStatus())) {
//                        vo.setIfEdit(Constants.ONE_NUM);
//                        break;
//                    }
//                }
//            }
        //处置角色编辑自己的
//            if (!StringUtils.isEmpty(roleId)) {
//                for (String role : roleId.split(Constants.COMMA)) {
//                    if (role.equals(EnumUtil.BaseRole.HANDLE_MAN.getCode()) &&
//                            vo.getOrderStatus().equals(EnumUtil.FlowNode.PENDING_HANDLE.getCode()) ) {
//                        if (userId.equals(vo.getProcessId())) {
//                            vo.setIfEdit(Constants.ONE_NUM);
//                        } else {
//                            vo.setIfEdit(Constants.ZERO_NUM);
//                        }
//                    }
//                }
//            }
        // 修改处置角色判断逻辑，只有处置角色并且不是管理员时才校验
//            if(!alarmOrderListReq.isAdmin() && CommonUtils.ifHandleRole(roleId)){
//                // 20220106 zzh 处置人员具有本部门的待认领和自己的待处置权限
//                if (vo.getOrderStatus().equals(EnumUtil.FlowNode.PENDING_HANDLE.getCode()) && userId.equals(vo.getProcessId())) {
//                    vo.setIfEdit(Constants.ONE_NUM);
//                } else if(EnumUtil.FlowNode.PENDING_CLAIM.getCode().equals(vo.getOrderStatus()) && alarmOrderListReq.getOrgIds().contains(vo.getGroupId())){
//                    vo.setIfEdit(Constants.ONE_NUM);
//                } else {
//                    vo.setIfEdit(Constants.ZERO_NUM);
//                }
//            }
        resultDto.getVo().setIfEdit(ifEditHandle(resultDto.getAlarmOrderListReq().isAdmin(), resultDto.getUserId(), resultDto.getVo(), resultDto.getFlowLinkIds()));

        resultDto.getAuditNum().forEach(item -> {
            if (item.getOrderId().equals(resultDto.getVo().getOrderId())) {
                resultDto.getVo().setLeaderNum(item.getLeaderNum());
            }
        });
        // 设置图标,告警名称，工单环境名称
        resultDto.getAlramTypeList().forEach(item -> {
            if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(resultDto.getVo().getWarningTypeId())) {
                resultDto.getVo().setIcon(String.valueOf(item.get(Constants.PARAM_ICON)));
                resultDto.getVo().setWarningTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
            }
        });
        resultDto.getEmergencyLevelEnum().forEach(item -> {
            if (item.getDictValue().equals(resultDto.getVo().getEmergencyLevel())) {
                resultDto.getVo().setEmergencyLevelName(item.getDictLabel());
            }
        });
        resultDto.getOrderStatusEnum().forEach(item -> {
            if (item.getDictValue().equals(resultDto.getVo().getOrderStatus())) {
                resultDto.getVo().setOrderStatusName(item.getDictLabel());
            }
        });
        if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(resultDto.getVo().getOrderStatus())
                || EnumUtil.FlowNode.DUPLICATION.getCode().equals(resultDto.getVo().getOrderStatus())
                || EnumUtil.FlowNode.END.getCode().equals(resultDto.getVo().getOrderStatus())) {
            resultDto.getVo().setOrderStatusName(EnumUtil.FlowNode.END.getName());
        }
        resultDto.getNoticeResourceEnum().forEach(item -> {
            if (item.getDictValue().equals(resultDto.getVo().getNoticeResource())) {
                resultDto.getVo().setNoticeResourceName(item.getDictLabel());
            }
        });
        resultDto.getImportanceLevelEnum().forEach(item -> {
            if (item.getDictValue().equals(resultDto.getVo().getImportanceLevel())) {
                resultDto.getVo().setImportanceLevelName(item.getDictLabel());
            }
        });

        // 20211127 zzh 处理图片，多张图片取第一张
        String imgUrl = resultDto.getVo().getImgUrl();
        if (!StringUtils.isEmpty(imgUrl) && imgUrl.contains(Constants.COMMA)) {
            resultDto.getVo().setImgUrl(imgUrl.split(Constants.COMMA)[0]);
        }

        String imgUrlIcon = resultDto.getVo().getFileImgUrlIcon();
        // 处理图片，多张图片取第一张
        if (!StringUtils.isEmpty(imgUrlIcon) && imgUrlIcon.contains(Constants.COMMA)) {
            resultDto.getVo().setFileImgUrlIcon(String.valueOf(resultDto.getVo().getFileImgUrlIcon().split(Constants.COMMA)[0]));
        }

        //置信度 取最大置信度展示  -- 新增综合置信度，若存在综合置信度则展示综合置信度
        if (!StringUtils.isEmpty(resultDto.getVo().getCompConfidenceLevel())) {
            resultDto.getVo().setConfidenceLevel(resultDto.getVo().getCompConfidenceLevel());
        } else {
            String confidenceLevel = resultDto.getVo().getConfidenceLevel();
            if (!StringUtils.isEmpty(confidenceLevel)) {
                String[] confidenceLevelList = confidenceLevel.split(Constants.COMMA);
                Optional<String> confidenceLevelOpt = Arrays.stream(confidenceLevelList).max(String::compareTo);
                if (confidenceLevelOpt.isPresent()) {
                    String confidenceLevelNew = confidenceLevelOpt.get();
                    resultDto.getVo().setConfidenceLevel(confidenceLevelNew);
                }
            }
        }

        resultDto.getWarningSourceEnum().forEach(item -> {
            if (item.getDictValue().equals(resultDto.getVo().getWarningSource())) {
                resultDto.getVo().setWarningSourceName(item.getDictLabel());
            }
        });
        //查询第三方告警来源名称
        if (Constants.WARNING_SOURCE_11.equals(resultDto.getVo().getWarningSource())) {
            List<AlarmTypeVO> alarmTypeVOList = vmAlarmFlowConfigMapper.getThirdPartyAlarmTypes(null, null);
            Map<String, String> thirdPartySourceMap = alarmTypeVOList.stream().collect(Collectors.toMap(item -> item.getTenantId() + item.getIndustryCode(),
                    item -> item.getAlarmSourceName(), (o, n) -> n, HashMap::new));
            resultDto.getVo().setWarningSourceName(thirdPartySourceMap.get(resultDto.getVo().getTenantId() + resultDto.getVo().getIndustryCode()));
        }

        //执法人信息前面多,去掉首尾多余的逗号
        if (!StringUtils.isEmpty(resultDto.getVo().getProcessName())) {
            String processName = resultDto.getVo().getProcessName();
            processName = processName.startsWith(Constants.COMMA) ? processName.substring(1) : processName;
            processName = processName.endsWith(Constants.COMMA) ? processName.substring(0, processName.length() - 1) : processName;
            resultDto.getVo().setProcessName(processName);
        }

        //行业调用时处理
        if (Constants.YES_FLAG.equals(resultDto.getAlarmOrderListReq().getIsTrade())) {
            // 获取网格员
            if (!StringUtils.isEmpty(resultDto.getVo().getGridId())) {
                resultDto.getVo().setKeeperName(deviceRepository.getGridKeeperByGridId(resultDto.getVo().getGridId()));
            }
            List<AlarmOrderByDeviceDTO> devList = resultDto.getAlarmOrderListReq().getDevList();
            if (!CollectionUtils.isEmpty(devList)) {
                for (AlarmOrderByDeviceDTO deviceDTO : devList) {
                    if (!StringUtils.isEmpty(resultDto.getVo().getDeviceCode()) && resultDto.getVo().getDeviceCode().equals(deviceDTO.getDeviceCode())) {
                        resultDto.getVo().setLocation(deviceDTO.getLocation());
                        resultDto.getVo().setModelCode(deviceDTO.getModelCode());
                        resultDto.getVo().setHeight(deviceDTO.getHeight());
                        break;
                    }
                }
            }
        }
        //是否收藏
        resultDto.getVo().setIsCollection(Constants.ZERO);
        for (WarningOrderListVO item : resultDto.getWarningOrderCollectionList()) {
            if (item.getOrderId().equals(resultDto.getVo().getOrderId())) {
                resultDto.getVo().setIsCollection(Constants.ONE);
                break;
            }
        }
        //v1.6.80 是否显示督办提示图标
        resultDto.getVo().setIsHandleDisplay(Constants.ZERO);
        List<HandleRecordVO> handleRecordList = resultDto.getHandleRecordList();
        if (!CollectionUtils.isEmpty(handleRecordList)) {
            handleRecordList.forEach(item -> {
                if (resultDto.getVo().getOrderId().equals(item.getOrderId()) && resultDto.getVo().getLinkId().equals(item.getLinkId())) {
                    resultDto.getVo().setIsHandleDisplay(Constants.ONE);
                }
            });
        }
        //v1.7.00 展示所属组织
        List<Map<String, String>> orgInfoList = resultDto.getOrgInfoList();
        if (!CollectionUtils.isEmpty(orgInfoList)) {
            for (Map<String, String> map : orgInfoList) {
                String orderId = resultDto.getVo().getOrderId();
                if (map.containsKey(orderId)) {
                    resultDto.getVo().setOrgName(map.get(orderId));
                    break;
                }
            }
        }
        //获取卫星告警专题图和红外图
        if (Constants.NINE.equals(resultDto.getVo().getWarningSource())) {
            if (!StringUtils.isEmpty(resultDto.getVo().getExtraField()) && !"null".equals(resultDto.getVo().getExtraField())) {
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(resultDto.getVo().getExtraField(), SatelliteExtendFieldVo.class);
                resultDto.getVo().setRelativePathThem(extendFieldVo.getRelativePathThem());
                resultDto.getVo().setRelativePathMir(extendFieldVo.getRelativePathMir());
            }
        }
        // AI 告警：在外层 attrList 末尾追加实际触发区域与数量（保留原 extraField 结构）
        if (AlarmConstants.ALARM_SOURCE_1.equals(resultDto.getVo().getWarningSource())) {
            resultDto.getVo().setExtraField(AiExtraFieldUtil.enrichExtraField(
                    resultDto.getVo().getWarningSource(), resultDto.getVo().getExtraField()));
        }
    }

    private List<HandleRecordVO> getHandleRecordResult(List<String> orderIdList) {
        Query query = new Query();
        query.addCriteria(Criteria.where(PARAM_ORDER_ID).in(orderIdList));
        query.addCriteria(Criteria.where(PARAM_DELETE_FLAG).is(ZERO));
        List<HandleRecordVO> handleRecordList = mongoTemplate.find(query, HandleRecordVO.class, TableConstant.VM_FLOW_HANDLE_RECORD_T);
        return handleRecordList;
    }

    /**
     * 通过摄像机查询预置位&告警数量列表
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public List<OrderInfoVO> getPresetListAndEventCount(AlarmOrderDTO dto) {
        List<OrderInfoVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(dto.getDeviceCode())) {
            return result;
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            throw new com.chinatower.platform.common.exception.CustomException("获取用户信息失败", HttpStatus.HTTP_BAD_REQUEST);
        }
        SysUser currentUser = loginUser.getUser();
        dto.setIndustryCode(currentUser.getIndustryCode());
        dto.setTenantId(currentUser.getTenantId());
        List<OrderInfoVO> presetList = warningOrderMapper.getPresetListByDevice(dto);

        if (CollectionUtils.isEmpty(presetList)) {
            return result;
        }

        List<String> presetIdList = presetList.stream().map(OrderInfoVO::getPresetId).collect(Collectors.toList());
        dto.setPresetIdList(presetIdList);

        if (!CollectionUtils.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(Constants.NINE)) {
            dto.setEnableAcceptanceMode(Constants.ONE);
        }

        // 查询开始结束时间
        Date startDate = dto.getAlarmTimeStart();
        Date endDate = dto.getAlarmTimeEnd();
        int errCount = 0;
        boolean stop = false;
        // 根据排序规则判断从最后一个表开始向前获取或从开始向后获取
        while (!stop) {
            try {
                //正序从开始获取
                String tableName = null;
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                    tableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_TEBLE);
                } else {
                    tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
                }
                dto.setTableName(tableName);
                List<OrderInfoVO> queryResult = warningOrderMapper.queryAlarmGroupByPreset(dto);
                if (!CollectionUtils.isEmpty(queryResult)) {
                    result.addAll(queryResult);
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                    // 转到下月
                    String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                    startDate = DateUtils.stringToDateYM(nextMonth);
                } else {
                    // 转到上月
                    String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                    endDate = DateUtils.stringToDateYM(lastMonth);
                }
            } catch (Exception e) {
                log.error("通过摄像机查询预置位&告警数量列表：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if (errCount > 3) {
                    return result;
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }

        }

        presetList.forEach(preSetInfo -> {
            if (StringUtils.isEmpty(preSetInfo.getPresetAlarmCount())) {
                preSetInfo.setPresetAlarmCount("0");
            }
            result.forEach(countInfo -> {
                if (preSetInfo.getPresetId().equals(countInfo.getPresetId())) {
                    preSetInfo.setPresetAlarmCount(String.valueOf(Integer.valueOf(preSetInfo.getPresetAlarmCount()) + Integer.valueOf(countInfo.getPresetAlarmCount())));
                }
            });
        });
        return presetList;
    }


    /**
     * 获取告警详情
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "a.device_code", tenantColumn = "a.tenant_id",
            industryCode = "a.industry_code", alarmSourceColumn = "a.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "a.province_code"
            , areaCity = "a.city_code", areaCode = "a.county_code", processIdColumn = "a.process_id")
    public WarningOrderInfoVO getOrderInfo(AlarmOrderDTO dto) throws Exception {
        //判定是否加密orderId
        if (!StringUtils.isEmpty(dto.getDeOrderId())) {
            String shareId = dto.getDeOrderId();
            AlarmShareDTO alarmShareDTO = alarmShareMapper.getByIds(shareId);
            if (ObjectUtil.isEmpty(alarmShareDTO)) {
                return null;
            }
            dto.setOrderId(alarmShareDTO.getAlarmShare());
            dto.setPowerFlag(Constants.ONE);
        }

        // 获取表名
        String tableName = TableConstant.WARN_ORDER_TEBLE + dto.getOrderId().substring(0, 6);

        // 查询告警详情
        dto.setTableName(tableName);
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 如果是行业调用接口，且有传userid,使用行业传入userid, 否则用户登入信息中获取
        if (!Constants.YES_FLAG.equals(dto.getIsTrade()) || StringUtils.isEmpty(dto.getUserId())) {
            dto.setUserId(sysService.getLoginUserId());
            dto.setTradeId(loginUser.getUser().getIndustryCode());
        }
        List<DelayMessageDto> delayMessageDtoList = new ArrayList<>();
        DelayMessageDto delayMessageDto = new DelayMessageDto();
        delayMessageDto.setOrderId(dto.getOrderId());
        delayMessageDto.setIndustryCode(loginUser.getUser().getIndustryCode());
        delayMessageDto.setUserId(loginUser.getUser().getUserId());
        delayMessageDtoList.add(delayMessageDto);
        log.info("getOrderInfo流程流转更新延迟消息表入参:" + JSON.toJSONString(delayMessageDtoList));
        orderDelayMessageService.updateOrderDelayList(delayMessageDtoList);
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        OrderInfoVO tempOrderInfoVO = warningOrderMapper.getOrderInfo(dto);
        Map<Function, Object> paramMap = new ConcurrentHashMap<>();
        //Function<OrderInfoVO, OrderInfoVO> fun1= this::doGetFindName;
        if (tempOrderInfoVO != null && !StringUtils.isEmpty(tempOrderInfoVO.getFindName())) {
            SysUser user = sysMapper.selectUserOnlyByUserName(tempOrderInfoVO.getFindName());
            if (user != null) {
                tempOrderInfoVO.setFindName(user.getNickName());
            }
        }
        //paramMap.put(fun1,tempOrderInfoVO);
        if (null == tempOrderInfoVO) {
            //开启权限调用且工单表未查到数据，返回空
            if (Constants.ONE.equals(dto.getPowerFlag())) {
                return null;
            }
            dto.setTableName(TableConstant.WARN_ALARM_OPERATE);
            tempOrderInfoVO = warningOrderMapper.getOrderInfo4AlarmOperate(dto);
            if (null == tempOrderInfoVO) {
                WarningInterceptDTO warningInterceptDTO = new WarningInterceptDTO();
                warningInterceptDTO.setOrderId(dto.getOrderId());
                List<Map> interceptInfo = warningInterceptRepository.getInterceptInfo(warningInterceptDTO);
                if (CollUtil.isEmpty(interceptInfo)) {
                    return null;
                }
                WarningOrderInfoVO result = new WarningOrderInfoVO();
                for (Map map : interceptInfo) {
                    OrderInfoVO InterceptInfo = (OrderInfoVO) map.get(Constants.PARAM_ORDER);
                    List<Map> videos = (List<Map>) map.get(Constants.PARAM_VIDEO);
                    List<Map> imgs = (List<Map>) map.get(Constants.PARAM_IMGS);

                    Map<String, Object> video = new HashMap<>(3);
                    // 兼容APP旧版本 故此处理
                    video.put(Constants.PARAM_FILE_IMAGE_URL, InterceptInfo.getVideoUrl());
                    video.put(Constants.PARAM_T_I_VIDEO, InterceptInfo.getFrontAlarmVedioUrl());
                    video.put(Constants.PARAM_VIDEO_LIST, videos);

                    this.getALarmLables(InterceptInfo);


                    result.setOrder(InterceptInfo);
                    result.setVideo(video);
                    result.setImgs(imgs);
                }
                return result;
            }
        }
        BeanUtils.copyProperties(tempOrderInfoVO, orderInfoVO);
       /*
        if(!StringUtils.isEmpty(orderInfoVO.getTaskId())){
            PolygonOperateDTO polygonOperateDTO = new PolygonOperateDTO();
            polygonOperateDTO.setTaskId(orderInfoVO.getTaskId());
            polygonOperateDTO.setTenantId(loginUser.getUser().getTenantId());
            Result result =getPolygonOperate(polygonOperateDTO);
            if(result != null && result.getData()!=null){
                Map<String,Object> polygonOperate = JSONObject.parseObject(JSON.toJSONString(result.getData()),Map.class);
                orderInfoVO.setPolygonOperate(polygonOperate);
            }
        }*/
        // 获取当前用户角色
        Long[] roleIds = loginUser.getUser().getRoleIds();
        List<String> roleIdList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleIdList.add(String.valueOf(roleId));
            }
        }
        String flowLinkIds = "";
        boolean admin = (OrderConstants.IS_TENANT_ADMIN.equals(loginUser.getUser().getIsTenantAdmin()));
        WarningOrderListVO vo = new WarningOrderListVO();
        BeanUtils.copyProperties(orderInfoVO, vo);
        //v1.6.80 督办提示图标
        orderInfoVO.setIsHandleDisplay(Constants.ZERO);
        List<HandleRecordVO> handleRecordVOS = this.getHandleRecordList(dto.getOrderId(), ONE);
        if (!CollectionUtils.isEmpty(handleRecordVOS)) {
            handleRecordVOS.forEach(item -> {
                if (orderInfoVO.getLinkId().equals(item.getLinkId())) {
                    orderInfoVO.setIsHandleDisplay(Constants.ONE);
                }
            });
        }
        log.info("工单详情查看事件是否可编辑权限入参:" + JSON.toJSONString(vo) + "====可操作的流程权限" + JSON.toJSONString(flowLinkIds));
        int ifEdit = iWarningOrderRepository.ifEditHandle(admin, loginUser.getUser().getUserId(), vo, flowLinkIds);
        orderInfoVO.setIfEdit(ifEdit);
        Function<OrderInfoVO, OrderInfoVO> fun2 = this::getALarmLables;
        paramMap.put(fun2, orderInfoVO);
        //行业应用名称
        Function<OrderInfoVO, OrderInfoVO> fun3 = this::getIndustryName;
        paramMap.put(fun3, orderInfoVO);
        //置信度 取最大置信度展示  -- 新增综合置信度，若存在综合置信度则展示综合置信度
        if (!StringUtils.isEmpty(orderInfoVO.getCompConfidenceLevel())) {
            orderInfoVO.setConfidenceLevel(orderInfoVO.getCompConfidenceLevel());
        } else {
            String confidenceLevel = orderInfoVO.getConfidenceLevel();
            if (!StringUtils.isEmpty(confidenceLevel)) {
                String[] confidenceLevelList = confidenceLevel.split(Constants.COMMA);
                String confidenceLevelNew = Arrays.stream(confidenceLevelList).max(String::compareTo).get();
                orderInfoVO.setConfidenceLevel(confidenceLevelNew);
            }
        }
        // 当前环节停留时长wait
        if (!StringUtils.isEmpty(orderInfoVO.getFlowNodeTime())) {
            orderInfoVO.setWaitTime(DateUtils.getTimeDifference(new Date(), DateUtils.string2Date(orderInfoVO.getFlowNodeTime())));
        } else {
            orderInfoVO.setWaitTime("");
        }
        //剩余时长
        if (!StringUtils.isEmpty(orderInfoVO.getTimeLimit()) && !StringUtils.isEmpty(orderInfoVO.getFlowNodeTime())) {
            orderInfoVO.setIfTimeout(DateUtils.checkIfTimeOut(Integer.valueOf(orderInfoVO.getTimeLimit()), DateUtils.string2Date(orderInfoVO.getFlowNodeTime())));
            StringBuilder showTime = new StringBuilder();
            if (orderInfoVO.getIfTimeout() == 0) {
                // 未超期
                showTime.append("剩余").append(DateUtils.getTimeDifference(
                        DateUtils.subtractTime(DateUtils.string2Date(orderInfoVO.getFlowNodeTime()), Integer.valueOf(orderInfoVO.getTimeLimit()) * 1000), new Date()));
            } else {
                // 超期
                showTime.append("超期")
                        .append(DateUtils.getTimeDifference(new Date(),
                                DateUtils.subtractTime(DateUtils.string2Date(orderInfoVO.getFlowNodeTime()), Integer.valueOf(orderInfoVO.getTimeLimit()) * 1000)));
            }
            orderInfoVO.setShowTime(showTime.toString());
        }

        if (!StringUtils.isEmpty(orderInfoVO.getPresetId())) {
            VideoPresetDO videoPresetDO = videoPresetMapper.selectVideoPreById(orderInfoVO.getPresetId(), orderInfoVO.getDeviceCode());
            if (Objects.nonNull(videoPresetDO)) {
                orderInfoVO.setPresetName(videoPresetDO.getPresetName());
            }
        }
        Function<OrderInfoVO, OrderInfoVO> fun4 = this::getStaticDeviceRel;
        paramMap.put(fun4, orderInfoVO);
        String fileImgUrlSrc = orderInfoVO.getFileImgUrlSrc();
        String fileImgUrlIcon = orderInfoVO.getFileImgUrlIcon();
        String videoUrl = orderInfoVO.getVideoUrl();
        String frontAlarmPicUrl = orderInfoVO.getFrontAlarmPicUrl();
        String frontAlarmVedioUrl = orderInfoVO.getFrontAlarmVedioUrl();
        OrderInfoFuncDto funcDto = new OrderInfoFuncDto();
        funcDto.setOrderInfoVO(orderInfoVO);
        funcDto.setFileImgUrlSrc(fileImgUrlSrc);
        funcDto.setFileImgUrlIcon(fileImgUrlIcon);
        funcDto.setVideoUrl(videoUrl);
        Function<OrderInfoFuncDto, OrderInfoVO> fun5 = this::doGetAlarmImgs;
        paramMap.put(fun5, funcDto);
        orderInfoVO.setPolygonOperate(null);
        //获取图版
        Function<OrderInfoVO, OrderInfoVO> fun6 = this::getPolygonOperate;
        paramMap.put(fun6, orderInfoVO);
        //查询各环节上传的带水印的附件
        Function<OrderInfoVO, OrderInfoVO> fun7 = this::getOrderFileList;
        paramMap.put(fun7, orderInfoVO);
        List<Function> listFunc = Arrays.asList(fun2, fun3, fun4, fun5, fun6, fun7);
        ThreadParallelUtil.run(listFunc, f -> {
            long startTime = System.currentTimeMillis(); // 记录任务开始时间
            Object o = paramMap.get(f);
            long endTime = System.currentTimeMillis(); // 记录任务结束时间
            long duration = endTime - startTime; // 计算任务处理时间
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
        if (!StringUtils.isEmpty(orderInfoVO.getGridId())) {
            orderInfoVO.setKeeperName(deviceRepository.getGridKeeperByGridId(orderInfoVO.getGridId()));
            //获取网格信息
            GridInfo gridInfo = warningOrderUtil.getGrifInfo(Long.valueOf(orderInfoVO.getGridId()));
            orderInfoVO.setGridInfo(gridInfo);
        }

        // 告警类型名称
        List<Map> alramTypeList = getAlarmTypeList();
        if (!CollectionUtils.isEmpty(alramTypeList)) {
            // 设置图标
            alramTypeList.forEach(item -> {
                if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(orderInfoVO.getWarningTypeId())) {
                    orderInfoVO.setWarningTypeName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
                    orderInfoVO.setIcon(String.valueOf(item.get(Constants.PARAM_ICON)));
                }
            });
        }

        List<String> paramList = new ArrayList<>();
        paramList.add(Constants.ALARM_LEVEL);
        paramList.add(String.valueOf(Constants.MODEL_ID));
        paramList.add(Constants.IOT_MODEL_ID);
        paramList.add(String.valueOf(Constants.MSG_TYPE));
        paramList.add(String.valueOf(Constants.NOTICE_RESOURCE));
        paramList.add(String.valueOf(Constants.EMERGENCY_LEVEL));
        paramList.add(String.valueOf(Constants.ORDER_STATUS));
        paramList.add(String.valueOf(Constants.UAV_MANUFACTURER));
        paramList.add(String.valueOf(Constants.UAV_NEST_MANUFACTURER));
        // 告警重要等级名称
        AtomicReference<List<SysDictDataResponse>> importanceLevelEnum = new AtomicReference<>();
        //摄像机厂商
        AtomicReference<List<SysDictDataResponse>> modelIdEnum = new AtomicReference<>();
        // 物联设备厂商
        AtomicReference<List<SysDictDataResponse>> iotModelIdEnum = new AtomicReference<>();
        // 消息类型
        AtomicReference<List<SysDictDataResponse>> msgTypeEnum = new AtomicReference<>();
        // 通知类型
        AtomicReference<List<SysDictDataResponse>> noticeResourceEnum = new AtomicReference<>();
        //告警等级名称
        AtomicReference<List<SysDictDataResponse>> emergencyLevelEnum = new AtomicReference<>();
        // 告警状态名称
        AtomicReference<List<SysDictDataResponse>> orderStatusEnum = new AtomicReference<>();
        //无人机厂家
        AtomicReference<List<SysDictDataResponse>> UavIdEnum = new AtomicReference<>();
        //机巢厂家
        AtomicReference<List<SysDictDataResponse>> UavNestIdEnum = new AtomicReference<>();
        ThreadParallelUtil.run(paramList, o -> {
            if (Constants.ALARM_LEVEL.equals(String.valueOf(o))) {
                importanceLevelEnum.set(systemUtil.getEnum(o));
            } else if (String.valueOf(Constants.MODEL_ID).equals(String.valueOf(o))) {
                modelIdEnum.set(systemUtil.getEnum(o));
            } else if (Constants.IOT_MODEL_ID.equals(String.valueOf(o))) {
                iotModelIdEnum.set(systemUtil.getEnum(o));
            } else if (String.valueOf(Constants.MSG_TYPE).equals(String.valueOf(o))) {
                msgTypeEnum.set(systemUtil.getEnum(o));
            } else if (String.valueOf(Constants.NOTICE_RESOURCE).equals(String.valueOf(o))) {
                noticeResourceEnum.set(systemUtil.getEnum(o));
            } else if (String.valueOf(Constants.EMERGENCY_LEVEL).equals(String.valueOf(o))) {
                emergencyLevelEnum.set(systemUtil.getEnum(o));
            } else if (String.valueOf(Constants.ORDER_STATUS).equals(String.valueOf(o))) {
                orderStatusEnum.set(systemUtil.getEnum(o));
            } else if (Constants.UAV_MANUFACTURER.equals(String.valueOf(o))) {
                UavIdEnum.set(systemUtil.getEnum(o));
            } else if (Constants.UAV_NEST_MANUFACTURER.equals(String.valueOf(o))) {
                UavNestIdEnum.set(systemUtil.getEnum(o));
            }
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_CODE);

        if (!CollectionUtils.isEmpty(emergencyLevelEnum.get())) {
            emergencyLevelEnum.get().forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getEmergencyLevel())) {
                    orderInfoVO.setEmergencyLevelName(item.getDictLabel());
                }
            });
        }
        if (!CollectionUtils.isEmpty(importanceLevelEnum.get())) {
            importanceLevelEnum.get().forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getImportanceLevel())) {
                    orderInfoVO.setImportanceLevelName(item.getDictLabel());
                }
            });
        }
        if (!CollectionUtils.isEmpty(msgTypeEnum.get())) {
            msgTypeEnum.get().forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getMsgType())) {
                    orderInfoVO.setMsgTypeName(item.getDictLabel());
                }
            });
        }
        if (!CollectionUtils.isEmpty(noticeResourceEnum.get())) {
            noticeResourceEnum.get().forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getNoticeResource())) {
                    orderInfoVO.setNoticeResourceName(item.getDictLabel());
                }
            });
        }

        if (!CollectionUtils.isEmpty(orderStatusEnum.get())) {
            orderStatusEnum.get().forEach(item -> {
                if (item.getDictValue().equals(orderInfoVO.getOrderStatus())) {
                    orderInfoVO.setOrderStatusName(item.getDictLabel());
                }
            });
        }
        if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(orderInfoVO.getOrderStatus())
                || EnumUtil.FlowNode.DUPLICATION.getCode().equals(orderInfoVO.getOrderStatus())
                || EnumUtil.FlowNode.END.getCode().equals(orderInfoVO.getOrderStatus())) {
            orderInfoVO.setOrderStatusName(EnumUtil.FlowNode.END.getName());
        }

        Map imgUrl;
        // 按照顺序配置给图片排序
        List<Map> imgs = warningOrderUtil.processImgsOrder(orderInfoVO, dto.getUserId());
        /*if (!StringUtils.isEmpty(orderInfoVO.getFileImgUrlIcon()) || !StringUtils.isEmpty(fileImgUrlIcon)) {
            String[] imgIcons = StringUtils.isEmpty(orderInfoVO.getFileImgUrlIcon()) ? fileImgUrlIcon.split(Constants.COMMA):((String) orderInfoVO.getFileImgUrlIcon()).split(Constants.COMMA);
            if (!StringUtils.isEmpty(imgIcons) && imgIcons.length > 0) {
                for (String img : imgIcons) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    imgs.add(imgUrl);
                }
            }
        }
        if (!StringUtils.isEmpty(orderInfoVO.getFileImgUrlSrc()) || !StringUtils.isEmpty(fileImgUrlSrc)) {
            String[] imgSrcs = StringUtils.isEmpty(orderInfoVO.getFileImgUrlSrc())?fileImgUrlSrc.split(Constants.COMMA):orderInfoVO.getFileImgUrlSrc().split(Constants.COMMA);
            if (!StringUtils.isEmpty(imgSrcs) && imgSrcs.length > 0) {
                for (String img : imgSrcs) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    if(!imgs.contains(imgUrl)){
                        imgs.add(imgUrl);
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(orderInfoVO.getFrontAlarmPicUrl()) || !StringUtils.isEmpty(frontAlarmPicUrl)) {
            String[] imgSrcs = StringUtils.isEmpty(orderInfoVO.getFrontAlarmPicUrl()) ? frontAlarmPicUrl.split(Constants.COMMA):orderInfoVO.getFrontAlarmPicUrl().split(Constants.COMMA);
            if (!StringUtils.isEmpty(imgSrcs) && imgSrcs.length > 0) {
                for (String img : imgSrcs) {
                    imgUrl = new HashMap();
                    imgUrl.put(Constants.PARAM_FILE_URL, img);
                    imgs.add(imgUrl);
                }
            }
        }*/
        //处理第三方告警中真实告警
        warningOrderUtil.handleRealAlarmInThirdParty(orderInfoVO);
        // 设置多组物联设备信息
        warningOrderUtil.buildMultIotDeviceInfo(orderInfoVO, iotModelIdEnum, modelIdEnum);

        //无人机告警来源
        warningOrderUtil.uavAlarmSource(orderInfoVO, UavIdEnum, UavNestIdEnum);

        //物联设备告警
        List<SensorMonitorVo> monitors = new ArrayList<>();
        IotRelDeviceVo reldevice = new IotRelDeviceVo();
        List<IotRelDeviceVo> iotRelDeviceVos = new ArrayList<>();
        if (AlarmConstants.ALARM_SOURCE_8.equals(orderInfoVO.getWarningSource()) && !StringUtils.isEmpty(orderInfoVO.getDeviceCode())) {
            try {
                monitors = JSON.parseObject(orderInfoVO.getExtraField()).getJSONArray("monitors").toJavaList(SensorMonitorVo.class);
                //查询物联设备关联摄像机信息
                TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
                qryCond.setDeviceCode(orderInfoVO.getDeviceCode());
                qryCond.setTenantId(orderInfoVO.getTenantId());
                qryCond.setIndustryCode(orderInfoVO.getIndustryCode());
                try {
                    Result<TenantIotDeviceConfVO> result = videoDeviceService.getTenantIotDevConf(qryCond);
                    if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                        List<TenantIotConfRelVO> iotDeviceConf = result.getData().getConfRels();
                        if (iotDeviceConf != null && !iotDeviceConf.isEmpty()) {
                            List<String> deviceCodeArray = iotDeviceConf.stream().map(TenantIotConfRelVO::getDeviceCode).collect(Collectors.toList());
                            //查询设备收藏表
                            List<String> deviceCollectList = deviceMapper.getDeviceCodeCollectByDeviceCodes(deviceCodeArray, "5", String.valueOf(dto.getUserId()));
                            for (TenantIotConfRelVO deviceSiteVO : iotDeviceConf) {
                                IotRelDeviceVo iotRelDevice = new IotRelDeviceVo();
                                BeanUtil.copyProperties(deviceSiteVO, iotRelDevice);
                                //摄像机厂商
                                if (!CollectionUtils.isEmpty(modelIdEnum.get())) {
                                    modelIdEnum.get().forEach(item -> {
                                        if (Objects.equals(deviceSiteVO.getModelCode(), item.getDictValue())) {
                                            iotRelDevice.setModelCodeName(item.getDictLabel());
                                        }
                                    });
                                }
                                if (iotRelDevice != null) {
                                    iotRelDeviceVos.add(iotRelDevice);
                                }
                                //设备是否收藏标识（0-否、1-是）
                                iotRelDevice.setDeviceCodeCollect(Constants.ZERO);
                                if (!CollectionUtils.isEmpty(deviceCollectList)) {
                                    deviceCollectList.forEach(item -> {
                                        if (deviceSiteVO.getDeviceCode().equals(item)) {
                                            iotRelDevice.setDeviceCodeCollect(Constants.ONE);
                                        }
                                    });
                                }

                            }
                            //兼容老版本物联设备联动一个摄像机信息
                            TenantIotConfRelVO iotConfRelVO = iotDeviceConf.get(0);
                            BeanUtil.copyProperties(iotConfRelVO, reldevice);
                        }
                    }
                } catch (Exception e) {
                    log.error("物联设备关联摄像机信息查询异常，工单号：{}，异常：{}", orderInfoVO.getOrderId(), e);
                }
            } catch (Exception e) {
                log.error("物联设备因子和关联摄像机信息查询异常，工单号：{}，异常：{}", orderInfoVO.getOrderId(), e);
            }
        }
        // 卫星火情告警
        List<SatelliteRelDeviceVo> satelliteRelDeviceVos = new ArrayList<>();
        if (AlarmConstants.ALARM_SOURCE_9.equals(orderInfoVO.getWarningSource())
                && Objects.nonNull(orderInfoVO.getExtraField())) {
            try {
                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(orderInfoVO.getExtraField(), SatelliteExtendFieldVo.class);
                try {
                    log.info("====== 获取图片信息 start ：{}======", orderInfoVO.getExtraField());
                    String relativePathPose = extendFieldVo.getRelativePathPose();
                    String relativePathThem = extendFieldVo.getRelativePathThem();
                    String relativePathMir = extendFieldVo.getRelativePathMir();
                    String snapShotTime = extendFieldVo.getSnapShotTime();
                    //卫星关联摄像机设备信息
                    String realDeviceCode = extendFieldVo.getRealDeviceCode();
                    if (!StringUtils.isEmpty(realDeviceCode)) {
                        List<String> deviceCodeArray = Arrays.asList(realDeviceCode.split(Constants.COMMA));
                        List<DeviceSiteVO> deviceSiteVOList = deviceRepository.queryDeviceList(deviceCodeArray);
                        //查询设备收藏表
                        List<String> deviceCollectList = deviceMapper.getDeviceCodeCollectByDeviceCodes(deviceCodeArray, "5", String.valueOf(dto.getUserId()));
                        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
                            for (DeviceSiteVO deviceSiteVO : deviceSiteVOList) {
                                SatelliteRelDeviceVo satelliteRelDevice = new SatelliteRelDeviceVo();
                                BeanUtil.copyProperties(deviceSiteVO, satelliteRelDevice);
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
                                if (!CollectionUtils.isEmpty(deviceCollectList)) {
                                    deviceCollectList.forEach(item -> {
                                        if (deviceSiteVO.getDeviceCode().equals(item)) {
                                            satelliteRelDevice.setDeviceCodeCollect(Constants.ONE);
                                        }
                                    });
                                }
                                if (!StringUtils.isEmpty(snapShotTime)) {
                                    satelliteRelDevice.setSnapShotTime(snapShotTime);
                                }
                                if (satelliteRelDevice != null) {
                                    satelliteRelDeviceVos.add(satelliteRelDevice);
                                }
                            }
                        }
                    }
                    warningOrderUtil.addImageMap(Optional.ofNullable(relativePathThem).orElse(""), imgs);
                    warningOrderUtil.addImageMap(Optional.ofNullable(relativePathMir).orElse(""), imgs);
                    log.info("====== 获取图片信息 end ：{}======", imgs);
                } catch (Exception e) {
                    log.error("获取卫星火情图片失败，参数：{}", orderInfoVO.getExtraField(), e);
                }

                try {
                    //告警类型（耕地卫片执法：4110101  矿产卫片执法：4110102  非农化卫片执法：4110103  非粮化卫片执法：4110104）
                    String[] arr = {"4110101", "4110102", "4110103", "4110104"};
                    if (Arrays.asList(arr).contains(orderInfoVO.getWarningTypeId())) {
                        ReceiveRegionAlarmDto spot = JSONObject.parseObject(orderInfoVO.getExtraField(), ReceiveRegionAlarmDto.class);
                        orderInfoVO.setSpot(spot);
                    }
                } catch (Exception e) {
                    log.error("获取卫星告警详情失败，参数：{}", orderInfoVO.getExtraField(), e);
                }

                //添加卫星告警信息--火区面积、下垫面类型字段
                try {
                    if (!StringUtils.isEmpty(extendFieldVo.getFireArea())) {
                        orderInfoVO.setFireArea(extendFieldVo.getFireArea());
                    }
                    if (!StringUtils.isEmpty(extendFieldVo.getUnderlyingSurfaceType())) {
                        orderInfoVO.setUnderlyingSurfaceType(extendFieldVo.getUnderlyingSurfaceType());
                    }
                } catch (Exception e) {
                    log.error("获取卫星详情信息失败，参数：{}", orderInfoVO.getExtraField(), e);
                }
            } catch (Exception e) {
                log.error("orderInfoVO.getExtraField()转换JSON异常，参数：{}", orderInfoVO.getExtraField(), e);
            }

        }

        // 增加摄像机地址
        if (!StringUtils.isEmpty(orderInfoVO.getDeviceCode()) && !WarningSourceEnum.SATELLITE_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
            DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(orderInfoVO.getDeviceCode(), null);
            if (deviceSiteVO != null) {
                orderInfoVO.setDeviceAddress(deviceSiteVO.getLocation());
                orderInfoVO.setModelId(deviceSiteVO.getModelId());
                orderInfoVO.setHeight(deviceSiteVO.getHeight());
                orderInfoVO.setVideoLatitude(deviceSiteVO.getLatitude());
                orderInfoVO.setVideoLongitude(deviceSiteVO.getLongitude());
                orderInfoVO.setDeviceLongitude(deviceSiteVO.getLongitude());
                orderInfoVO.setDeviceLatitude(deviceSiteVO.getLatitude());
                orderInfoVO.setFocusRange(deviceSiteVO.getFocusRange());
                orderInfoVO.setVertiDownNeg(deviceSiteVO.getVertiDownNeg());
                orderInfoVO.setDeviceImageUrl(deviceSiteVO.getDeviceImageUrl());
                // 摄像机告警定位配置判断
                if (Constants.YES_FLAG.equals(deviceSiteVO.getAlarmLocation())) {
                    orderInfoVO.setAlarmLocation(deviceSiteVO.getAlarmLocation());
                }
            }
            if (WarningSourceEnum.THIRD_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
                //第三方告警有通道code查询通道名称，没有的只回显摄像机名称
                if (!StringUtils.isEmpty(orderInfoVO.getChannelCode())) {
                    DeviceSiteVO deviceInfoVo = deviceRepository.queryDevice(orderInfoVO.getDeviceCode(), orderInfoVO.getChannelCode());
                    if (deviceInfoVo != null) {
                        orderInfoVO.setDeviceName(deviceInfoVo.getDeviceName());
                        orderInfoVO.setChannelName(deviceInfoVo.getChannelName());
                        orderInfoVO.setDeviceImageUrl(deviceInfoVo.getDeviceImageUrl());
                    }
                } else {
                    if (deviceSiteVO != null) {
                        orderInfoVO.setDeviceName(deviceSiteVO.getDeviceName());
                    }
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
                    orderInfoVO.setFocusRange(deviceSiteVO.getFocusRange());
                    orderInfoVO.setVertiDownNeg(deviceSiteVO.getVertiDownNeg());
                }

            }

            // 判断设备是收藏
            String deviceCodeCollect = deviceRepository.getDeviceCodeCollectByDeviceCodeAndObjType(orderInfoVO.getDeviceCode(), "5", String.valueOf(dto.getUserId()));
            orderInfoVO.setDeviceCodeCollect(deviceCodeCollect);
            //转义PTZ
            if (!StringUtils.isEmpty(orderInfoVO.getPan())
                    && !StringUtils.isEmpty(orderInfoVO.getTilt())
                    && !StringUtils.isEmpty(orderInfoVO.getZoom()) && !StringUtils.isEmpty(orderInfoVO.getModelId())) {
                try {
                    String pValue = orderInfoVO.getPan();
                    String tValue = orderInfoVO.getTilt();
                    String zValue = orderInfoVO.getZoom();
                    //获取摄像设备厂家编码
                    String modelId = orderInfoVO.getModelId();
                    //获取摄像机类型的焦距范围
                    String deviceFocusRange = orderInfoVO.getFocusRange();
                    //摄像机水平向下是否为负
                    String vertiDownNeg = orderInfoVO.getVertiDownNeg();
                    DeviceModelVO deviceModel = new DeviceModelVO();
                    deviceModel.setMfgrCode(modelId);
                    deviceModel.setVertiDownNeg(vertiDownNeg);
                    if (Constants.MODELID_DAHUA.equals(modelId) || Constants.MODELID_DHUA.equals(modelId)) {
                        deviceModel.setFocusRange(deviceFocusRange);
                    }
                    String pNewValue = LongLatAngleUtils.paramTrans(pValue, deviceModel, "P");
                    String tNewValue = LongLatAngleUtils.paramTrans(tValue, deviceModel, "T");
                    String zNewValue = LongLatAngleUtils.paramTrans(zValue, deviceModel, "Z");
                    orderInfoVO.setTransPan(pNewValue);
                    orderInfoVO.setTransTitle(tNewValue);
                    orderInfoVO.setTransZoom(zNewValue);
                } catch (Exception e) {
                    log.error("告警工单ptz转义异常，工单号：{}，异常：{}", orderInfoVO.getOrderId(), e);
                }
            }
        }

        SatelliteRelDeviceVo radarDevice = new SatelliteRelDeviceVo();
        if (WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
            if (!StringUtils.isEmpty(orderInfoVO.getDeviceCode())) {
                DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(orderInfoVO.getDeviceCode(), null);
                if (deviceSiteVO != null) {
                    BeanUtil.copyProperties(deviceSiteVO, radarDevice);
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
            //雷达告警
            List<DeviceSiteInfoVO> deviceSiteList = new ArrayList<>();
            //扩展字段
            String extraField = orderInfoVO.getExtraField();
            //雷达设备编码
            Object[] radarCodes = null;
            if (!StringUtils.isEmpty(extraField)) {
                try {
                    JSONObject jsonExtraField = JSON.parseObject(extraField);
                    try {
                        if (jsonExtraField.containsKey(Constants.RADAR_CODES)) {
                            radarCodes = jsonExtraField.getJSONArray(Constants.RADAR_CODES).toArray();
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
            if (radarCodes != null) {
                for (Object radarCode : radarCodes) {
                    DeviceSiteInfoVO deviceSite = new DeviceSiteInfoVO();
                    if (ObjectUtil.isEmpty(radarCode)) {
                        continue;
                    }
                    DeviceSiteVO deviceSiteVO = deviceRepository.queryRadarDevice(tenantId, industryCode, radarCode.toString());
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

        //物联设备厂商
        if (WarningSourceEnum.IOT_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource()) || WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(orderInfoVO.getWarningSource())) {
            if (!CollectionUtils.isEmpty(iotModelIdEnum.get())) {
                iotModelIdEnum.get().forEach(item -> {
                    if (Objects.equals(orderInfoVO.getModelId(), item.getDictValue())) {
                        orderInfoVO.setModelFactoryName(item.getDictLabel());
                    }
                });
            }
        } else {
            //摄像机厂商
            if (!CollectionUtils.isEmpty(modelIdEnum.get())) {
                modelIdEnum.get().forEach(item -> {
                    if (Objects.equals(orderInfoVO.getModelId(), item.getDictValue())) {
                        orderInfoVO.setModelFactoryName(item.getDictLabel());
                    }
                });
            }
        }

        //告警点和设备点水平方位角处理转换
        orderInfoVO.setHoriAzimuthAngle(AngleUtils.transformAngle(orderInfoVO.getHoriAzimuthAngle()));

        List<Map> videos = new ArrayList<>();
        if (!StringUtils.isEmpty(orderInfoVO.getVideoUrl()) || !StringUtils.isEmpty(orderInfoVO.getVideoUrl())) {
            String videoUrls = orderInfoVO.getVideoUrl() == null ? orderInfoVO.getVideoUrl() : orderInfoVO.getVideoUrl();
            String[] videoUrlStr = videoUrls.split(Constants.COMMA);
            for (String video : videoUrlStr) {
                imgUrl = new HashMap();
                imgUrl.put(Constants.PARAM_SRC, video);
                videos.add(imgUrl);
            }
        }
        if (!StringUtils.isEmpty(orderInfoVO.getFrontAlarmVedioUrl())) {
            String videoUrls = orderInfoVO.getFrontAlarmVedioUrl();
            String[] videoUrlStr = videoUrls.split(Constants.COMMA);
            for (String video : videoUrlStr) {
                imgUrl = new HashMap();
                imgUrl.put(Constants.PARAM_SRC, video);
                videos.add(imgUrl);
            }
        }
        // 告警来源枚举
        List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
        warningSourceEnum.forEach(item -> {
            if (item.getDictValue().equals(orderInfoVO.getWarningSource())) {
                orderInfoVO.setWarningSourceName(item.getDictLabel());
            }
        });
        if (Constants.WARNING_SOURCE_11.equals(orderInfoVO.getWarningSource())) {
            //获取第三方告警来源名称
            List<AlarmTypeVO> alarmTypeVOList = vmAlarmFlowConfigMapper.getThirdPartyAlarmTypes(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode());
            if (!CollectionUtils.isEmpty(alarmTypeVOList)) {
                orderInfoVO.setWarningSourceName(alarmTypeVOList.get(0).getAlarmSourceName());
            }
        }
        // 查询联动算法名称
        if (!StringUtils.isEmpty(orderInfoVO.getLinkageAlgorithm()) && !AlarmConstants.ALARM_SOURCE_11.equals(orderInfoVO.getWarningSource())) {
            orderInfoVO.setLinkageAlgorithm(deviceMapper.getAlgorithmName(orderInfoVO.getLinkageAlgorithm()));
        }

        if (!StringUtils.isEmpty(orderInfoVO.getLongitude()) && !StringUtils.isEmpty(orderInfoVO.getLatitude())
                && !StringUtils.isEmpty(orderInfoVO.getDeviceLongitude()) && !StringUtils.isEmpty(orderInfoVO.getDeviceLatitude())) {
            //根据设备的经纬度和事发地点的经纬度计算距离位置
            double distance = DistanceUtils.getDistance(Double.parseDouble(orderInfoVO.getLongitude()), Double.parseDouble(orderInfoVO.getLatitude()),
                    Double.parseDouble(orderInfoVO.getDeviceLongitude()), Double.parseDouble(orderInfoVO.getDeviceLatitude()));
            // 事发地点距离
            orderInfoVO.setIncidentDistance(String.format("%.2f", distance));
        }
        // 按照之前的参数格式进行封装
        WarningOrderInfoVO result = new WarningOrderInfoVO();
        String deleteUser = "";
        if (!StringUtils.isEmpty(orderInfoVO.getUserRecord())) {
            if (orderInfoVO.getUserRecord().contains(Constants.COMMA)) {
                int index = orderInfoVO.getUserRecord().lastIndexOf(Constants.COMMA);
                deleteUser = orderInfoVO.getUserRecord().substring(index + 1);
            } else {
                deleteUser = orderInfoVO.getUserRecord();
            }
        }
        if (!StringUtils.isEmpty(deleteUser)) {
            SysUser user = sysMapper.selectUserOnlyByUserId(deleteUser);
            if (user != null) {
                orderInfoVO.setDeleteUser(user.getNickName());
                orderInfoVO.setDeleteUserInfo(user);
            }
        }
        //拼装事件描述
        if (WARNING_SOURCE_1.equals(orderInfoVO.getWarningSource())) {
            orderInfoVO.setDescription(buildAlgorithmConfigDescField(orderInfoVO));
        }
        result.setImgs(imgs);
        Map<String, Object> video = new HashMap<>(3);
        // 兼容APP旧版本 故此处理
        video.put(Constants.PARAM_FILE_IMAGE_URL, orderInfoVO.getVideoUrl() == null ? orderInfoVO.getVideoUrl().split(Constants.COMMA)[0] : orderInfoVO.getVideoUrl().split(Constants.COMMA)[0]);
        video.put(Constants.PARAM_T_I_VIDEO, orderInfoVO.getFrontAlarmVedioUrl());
        video.put(Constants.PARAM_VIDEO_LIST, videos);
        if (monitors != null && !monitors.isEmpty()) {
            video.put(Constants.PARAM_MONITORS, monitors);
        }
        if (reldevice != null) {
            video.put(Constants.PARAM_RELDEVICE, reldevice);
            if (!CollectionUtils.isEmpty(modelIdEnum.get())) {
                modelIdEnum.get().forEach(item -> {
                    if (Objects.equals(reldevice.getModelCode(), item.getDictValue())) {
                        reldevice.setModelCodeName(item.getDictLabel());
                    }
                });
            }
            // 丢一份到orderInfoVO里面，
            orderInfoVO.setRelDevice(reldevice);
        }
        if (AlarmConstants.ALARM_SOURCE_8.equals(orderInfoVO.getWarningSource())) {
            orderInfoVO.setIotDeviceList(iotRelDeviceVos);
        }
        if (AlarmConstants.ALARM_SOURCE_9.equals(orderInfoVO.getWarningSource())) {
            orderInfoVO.setSatelliteDeviceList(satelliteRelDeviceVos);
        }
        if (AlarmConstants.ALARM_SOURCE_10.equals(orderInfoVO.getWarningSource())) {
            orderInfoVO.setRadarDevice(radarDevice);
        }
        if (!StringUtils.isEmpty(orderInfoVO.getUserDistance())) {
            String userDistance = orderInfoVO.getUserDistance();
            String cleanedUserDistance = userDistance.replaceAll(REGEX, ""); // 移除非数字字符
            double num = Double.parseDouble(cleanedUserDistance);
            String realDistance = "";
            if (num > 1000) {
                //如果大于一千米，单位为千米保留两位小数
                double res = num / 1000.0;
                DecimalFormat df = new DecimalFormat("#.00");
                realDistance = df.format(res);
                orderInfoVO.setUserDistance(realDistance + "千米");
            } else {
                BigDecimal decimal = new BigDecimal(cleanedUserDistance);
                int rounded = decimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                orderInfoVO.setUserDistance(String.valueOf(rounded) + "米");
            }
        }
        warningOrderUtil.handleDeviceLabel(orderInfoVO);
        warningOrderUtil.getVisiblePtzConfig(orderInfoVO);
        // AI 告警：在外层 attrList 末尾追加实际触发区域与数量（保留原 extraField 结构）
        if (AlarmConstants.ALARM_SOURCE_1.equals(orderInfoVO.getWarningSource())) {
            orderInfoVO.setExtraField(AiExtraFieldUtil.enrichExtraField(
                    orderInfoVO.getWarningSource(), orderInfoVO.getExtraField()));
        }
        // 智能体扩展字段拆分为独立属性返回前端（含调 Dify 前落库的气象字段）
        AiAgentExtendFieldUtil.fillAndClear(orderInfoVO);
        result.setOrder(orderInfoVO);
        result.setVideo(video);

        return result;
    }

    private OrderInfoVO getOrderFileList(OrderInfoVO orderInfoVO) {
        if (!StringUtils.isEmpty(orderInfoVO.getWarningOrderId())) {
            //获取表名
            String processTableName = TableUtil.getTableName(DateUtils.stringToDateYM(orderInfoVO.getWarningOrderId().substring(0, 6)),
                    TableConstant.WARN_ORDER_PROCESS_TEBLE);
            List<OrderFileDO> fileDOList = warningOrderMapper.getOrderFileList(orderInfoVO.getWarningOrderId(), processTableName);
            //根据经纬度和拍摄角度进行分组,过滤经纬度为空的附件
            Map<String, List<OrderFileDO>> groupedFiles = fileDOList.stream().filter(file -> {
                Map<String, String> fileWaterMarkMap = JSONObject.parseObject(file.getFileWaterMark(), Map.class);
                String longitude = fileWaterMarkMap.get(Constants.PARAM_LONGITUDE);
                String latitude = fileWaterMarkMap.get(Constants.PARAM_LATITUDE);
                return !StringUtils.isEmpty(longitude) && !StringUtils.isEmpty(latitude);
            }).collect(Collectors.groupingBy(file -> {
                //将fileWaterMark字符串解析成一个map对象
                Map<String, String> fileWaterMarkMap = JSONObject.parseObject(file.getFileWaterMark(), Map.class);
                String longitude = fileWaterMarkMap.get(Constants.PARAM_LONGITUDE);
                String latitude = fileWaterMarkMap.get(Constants.PARAM_LATITUDE);
                String shootingAngle = fileWaterMarkMap.get("shootingAngle");
                return String.join(Constants.SPLIT_SIGN, longitude, latitude, shootingAngle);
            }));
            //组装结果列表[{"longitude": "116.3357674","latitude": "39.939674","shootingAngle": "180","orderFiles":[{}]}]
            List<Map<String, Object>> result = groupedFiles.entrySet().stream().map(entry -> {
                String[] keyParts = entry.getKey().split(Constants.SPLIT_SIGN);
                String longitude = keyParts[0];
                String latitude = keyParts[1];
                String shootingAngle = keyParts[2];
                List<OrderFileDO> files = entry.getValue();
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put(Constants.PARAM_LONGITUDE, longitude);
                resultMap.put(Constants.PARAM_LATITUDE, latitude);
                resultMap.put("shootingAngle", shootingAngle);
                resultMap.put("orderFiles", files);
                return resultMap;
            }).collect(Collectors.toList());
            orderInfoVO.setFiles(result);
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

    private OrderInfoVO getIndustryName(OrderInfoVO orderInfoVO) {
        if (!StringUtils.isEmpty(orderInfoVO.getTradeId())) {
            String localNode = "";
            //获取当前节点
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigKey(com.chinatower.event.constants.Constants.PLATFORM_CODE);
            AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
            if (result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
                SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
                if (sysConfigInfo != null) {
                    localNode = sysConfigInfo.getConfigValue();
                }
            }
            SysIndustryConf sysIndustryConf = sysMapper.getByIndustryCode(orderInfoVO.getTradeId(), localNode);
            if (ObjectUtil.isNotNull(sysIndustryConf)) {
                orderInfoVO.setIndustryName(sysIndustryConf.getIndustryName());
            }
        }
        return orderInfoVO;
    }

    private OrderInfoVO doGetFindName(OrderInfoVO tempOrderInfoVO) {
        if (tempOrderInfoVO != null && !StringUtils.isEmpty(tempOrderInfoVO.getFindName())) {
            SysUser user = sysMapper.selectUserOnlyByUserName(tempOrderInfoVO.getFindName());
            if (user != null) {
                tempOrderInfoVO.setFindName(user.getNickName());
            }
        }
        return tempOrderInfoVO;
    }

    private OrderInfoVO getPolygonOperate(OrderInfoVO orderInfoVO) {
        log.info("详情查询图版入参:{}", JSON.toJSONString(orderInfoVO));
        if (!StringUtils.isEmpty(orderInfoVO.getTaskId())) {
            PolygonOperateDTO polygonOperateDTO = new PolygonOperateDTO();
            polygonOperateDTO.setTaskId(orderInfoVO.getTaskId());
            polygonOperateDTO.setTenantId(orderInfoVO.getTenantId());
            polygonOperateDTO.setDeviceCode(orderInfoVO.getDeviceCode());
            polygonOperateDTO.setChannelCode(orderInfoVO.getChannelCode());
            polygonOperateDTO.setPresetId(orderInfoVO.getPresetId());
            Result result = getPolygonOperate(polygonOperateDTO);
            if (result != null && result.getData() != null) {
                List<Map> polygonOperate = JSONArray.parseArray(JSON.toJSONString(result.getData()), Map.class);
                orderInfoVO.setPolygonOperate(polygonOperate);
            }
        }
        return orderInfoVO;
    }

    /**
     * 获取告警id的告警标签
     */
    public OrderInfoVO getALarmLables(OrderInfoVO tempOrderInfoVO) {
        //告警标签回显
        List<AlarmLableRelDTO> alarmLableRelDTOList = alarmLableRelRepository.getTagsByAlarmType(tempOrderInfoVO.getWarningOrderId());
        List<AlarmLableRelAPIDTO> alarmLableRelAPIDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alarmLableRelDTOList)) {
            alarmLableRelDTOList.forEach(item -> {
                AlarmLableRelAPIDTO alarmLableRelAPIDTO = new AlarmLableRelAPIDTO();
                BeanUtils.copyProperties(item, alarmLableRelAPIDTO);
                alarmLableRelAPIDTOS.add(alarmLableRelAPIDTO);
            });
            tempOrderInfoVO.setAlarmLableRelDTOS(alarmLableRelAPIDTOS);
        }
        return tempOrderInfoVO;
    }

    /**
     * 获取环节选择处理人
     */
    public String getProcessNamaList(List<UserInfoDto> userAllList, Map record) {
        String processNameList = "";
        if (!StringUtils.isEmpty(record.get(Constants.PROCESS_NAME_LIST))) {
            List<String> thisUserIdList = Arrays.asList(record.get(Constants.PROCESS_NAME_LIST).toString().split(Constants.COMMA));
            for (String userId : thisUserIdList) {
                for (UserInfoDto sysInfo : userAllList) {
                    if (userId.equals(sysInfo.getUserId())) {
                        if (StringUtils.isEmpty(processNameList)) {
                            processNameList = !StringUtils.isEmpty(sysInfo.getNickName()) ? sysInfo.getNickName() : sysInfo.getUserName();
                        } else {
                            processNameList += Constants.COMMA + (!StringUtils.isEmpty(sysInfo.getNickName()) ? sysInfo.getNickName() : sysInfo.getUserName());
                        }
                    }
                }
            }
        }
        return processNameList;
    }

    /**
     * 通过告警工单ID获取处理环节信息
     *
     * @param dto
     * @return
     */
    @Override
    public List<Map> getRecordByOrderId(AlarmOrderDTO dto) {
        String tableName = TableUtil.getTableName(DateUtils.stringToDateYM(dto.getOrderId().substring(0, 6)), TableConstant.WARN_ORDER_PROCESS_TEBLE);
        String warningTableName = TableConstant.WARN_ORDER_TEBLE + dto.getOrderId().substring(0, 6);
        List<Map> records = new ArrayList<>();
        if (Constants.SORT_TYPE_ASC.equals(dto.getSort())) {
            //升序
            records = warningOrderMapper.getRecordByOrderIdAsc(dto.getOrderId(), tableName);
        } else {
            //降序
            records = warningOrderMapper.getRecordByOrderId(dto.getOrderId(), tableName);
        }
        //根据查询该工单所有环节选择下一环节id查询对应用户信息
        List<String> userNameAllList = new ArrayList<>();
        List<String> processNameAllList = new ArrayList<>();
        for (Map record : records) {
            if (!StringUtils.isEmpty(record.get(Constants.PROCESS_NAME_LIST))) {
                userNameAllList.addAll(Arrays.asList(record.get(Constants.PROCESS_NAME_LIST).toString().split(Constants.COMMA)));
            }
            if (!StringUtils.isEmpty(record.get(Constants.RES_PROCESS_NAME_))) {
                processNameAllList.add((String) record.get(Constants.RES_PROCESS_NAME_));
            }
        }
        List<UserInfoDto> userAllList = new ArrayList<>();
        List<UserInfoDto> processUserAllList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userNameAllList)) {
            userAllList = sysMapper.queryUserListInfo(userNameAllList);
        }
        if (!CollectionUtils.isEmpty(processNameAllList)) {
            processUserAllList = sysMapper.queryUserListInfoByUserNameList(processNameAllList);
        }
        int judgeNum = 0;
        if (!CollectionUtils.isEmpty(records)) {
            List<String> resList = new ArrayList<>();
            resList.add(Constants.RES_MOBILE_PHONE);
            this.handleUserInfo(records, Constants.RES_PROCESS_ID, resList);
            Map param = new HashMap();
            param.put(Constants.PARAM_ORDER_ID, dto.getOrderId());
            List<String> processOrderIdList = new ArrayList();
            List<String> approveOrderIdList = new ArrayList();
            List<String> orderFlowIdList = new ArrayList();
            for (Map record : records) {
                if (ObjectUtils.isEmpty(record.get(Constants.RES_ACCEPT_PROCESS_ID))) {
                    //没有受理人的话，设置受理人值为空
                    record.put(Constants.RES_ACCEPT_PROCESS_NAME, "");
                }
                //获取环节选择所有处理人姓名
                String processNameList = this.getProcessNamaList(userAllList, record);
                //获取当前环节处理人姓名
                if (!StringUtils.isEmpty(record.get(Constants.RES_PROCESS_NAME_))) {
                    for (UserInfoDto processNameDto : processUserAllList) {
                        if (processNameDto.getUserName().equals(String.valueOf(record.get(Constants.RES_PROCESS_NAME_)))) {
                            record.put(Constants.RES_PROCESS_NAME_, processNameDto.getNickName());
                        }
                    }
                }
                //环节选择的所有人
                record.put(Constants.DISPATCH_PROCESS_NAME, processNameList);
                record.put(Constants.NEXT_PROCESS_NAME, processNameList);
                if (!StringUtils.isEmpty(record.get(Constants.PARAM_PROCESS_ORDER_ID))) {
                    if (record.get(Constants.PARAM_PROCESS_ORDER_ID).toString().contains(Constants.COMMA)) {
                        String[] processOrderId = record.get(Constants.PARAM_PROCESS_ORDER_ID).toString().split(Constants.COMMA);
                        processOrderIdList.addAll(Arrays.asList(processOrderId));
                    } else {
                        processOrderIdList.add((String) record.get(Constants.PARAM_PROCESS_ORDER_ID));
                    }
                    // 如果是处置环节，获取处置结果
                    // record.put(Constants.PARAM_STATUS,alarmOrderNewDAO.getProcessStatusById((String)
                    record.put(Constants.PARAM_FILES, processOrderMapper.getPhotos((String) record.get(Constants.PARAM_PROCESS_ORDER_ID)));

                    ProcessOrderDTO processOrderDTO = new ProcessOrderDTO();
                    processOrderDTO.setWarningOrderId(dto.getOrderId());
                    processOrderDTO.setProcessOrderId((String) record.get(Constants.PARAM_PROCESS_ORDER_ID));
                    ProcessOrderVO processOrderVO = processOrderMapper.getProcessOrder(processOrderDTO);
                    if (null != processOrderVO) {
                        record.put("ext1", processOrderVO.getExt1());
                    }
                } else if (!StringUtils.isEmpty(record.get(Constants.PARAM_APPROVE_ORDER_ID))
                        && EnumUtil.FlowNode.PENDING_CHECK.getCode().equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))) {
                    if (record.get(Constants.PARAM_APPROVE_ORDER_ID).toString().contains(Constants.COMMA)) {
                        String[] approveOrderId = record.get(Constants.PARAM_APPROVE_ORDER_ID).toString().split(Constants.COMMA);
                        approveOrderIdList.addAll(Arrays.asList(approveOrderId));
                    } else {
                        processOrderIdList.add((String) record.get(Constants.PARAM_APPROVE_ORDER_ID));
                    }
                    // 如果是核实环节，获取核实结果
                    // record.put(Constants.PARAM_STATUS,alarmOrderNewDAO.getApproveStatusById((String)
                } else if (EnumUtil.FlowNode.PENDING_JUDGE.getCode().equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))) {
                    //当前环节标记未完成
                    if (Constants.YES_FLAG.equals(record.get(Constants.PARAM_IF_CURRENT_LINK))) {
                        record.put(Constants.PARAM_STATUS, Constants.THREE);
                    } else if ("0".equals(record.get(Constants.PARAM_NEXT_LINK_ID))) {
                        //误报
                        record.put(Constants.PARAM_STATUS, Constants.ZERO);
                    } else if ("7".equals(record.get(Constants.PARAM_NEXT_LINK_ID))) {
                        //误报
                        record.put(Constants.PARAM_STATUS, Constants.TWO);
                    } else if ("6".equals(record.get(Constants.PARAM_NEXT_LINK_ID))) {
                        // 研判环节直接呗关闭 完成
                        record.put(Constants.PARAM_STATUS, Constants.FOUR);
                    } else {
                        // 已经经过研判环节说明为真警
                        record.put(Constants.PARAM_STATUS, Constants.ONE);
                    }
                    // 研判环节，获取研判结果
                    // 查询告警详情
                    param.put(Constants.PARAM_TABLE_NAME, warningTableName);
                    dto.setTableName(warningTableName);
                    /*OrderInfoVO orderInfoVO = warningOrderMapper.getOrderInfo(dto);
                    if (null != orderInfoVO) {
                        if( (orderInfoVO.getJudgeResult()==null || "".equals(orderInfoVO.getJudgeResult()) ) && Constants.ORDER_STATUS_6.equals(orderInfoVO.getOrderStatus())){
                            record.put(Constants.PARAM_STATUS, "3"); //研判环节如果直接进行了关闭的，以工单状态为主
                        }
                        //第一个进入判断的为最后一个研判记录数据,若为重复告警、误报时标记真实研判结果
                        if((Constants.TWO.equals(orderInfoVO.getJudgeResult()) || Constants.ZERO.equals(orderInfoVO.getJudgeResult()))&& judgeNum == 0){
                            record.put(Constants.PARAM_STATUS, orderInfoVO.getJudgeResult());
                        }
                    }*/
                    judgeNum++;
                } else if (Constants.VALID_STATE.equals(record.get(Constants.PARAM_IF_CURRENT_LINK))) {
                    record.put(Constants.PARAM_STATUS, Constants.ONE);
                } else {
                    record.put(Constants.PARAM_STATUS, "");
                }
                //判断是否为初始环节
                if (Constants.FIRST_LINK.equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))) {
                    AlarmOrderDTO orderDTO = new AlarmOrderDTO();
                    orderDTO.setOrderId(dto.getOrderId());
                    orderDTO.setTableName(warningTableName);
                    OrderInfoVO orderInfo = warningOrderMapper.getOrderInfo(orderDTO);
                    // 告警来源枚举
                    List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
                    for (SysDictDataResponse item : warningSourceEnum) {
                        if (orderInfo != null && orderInfo.getWarningSource().equals(item.getDictValue())) {
                            record.put(Constants.PARAM_LINK_NAME, item.getDictLabel());
                            break;
                        }
                    }
                    if (Constants.WARNING_SOURCE_11.equals(orderInfo.getWarningSource())) {
                        //获取第三方告警来源名称
                        List<AlarmTypeVO> alarmTypeVOList = vmAlarmFlowConfigMapper.getThirdPartyAlarmTypes(orderInfo.getTenantId(), orderInfo.getTradeId());
                        if (!CollectionUtils.isEmpty(alarmTypeVOList)) {
                            record.put(Constants.PARAM_LINK_NAME, alarmTypeVOList.get(0).getAlarmSourceName());
                        }
                    }
                }
                //假警完结和重复告警完结都显示已完结
                if (EnumUtil.FlowNode.DUPLICATION.getCode().equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))
                        || EnumUtil.FlowNode.MISINFORMATION.getCode().equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))
                        || EnumUtil.FlowNode.END.getCode().equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))) {
                    record.put(Constants.PARAM_LINK_NAME, EnumUtil.FlowNode.END.getName());
                }
                // 领导批示默认值
                record.put(Constants.PARAM_LEADERS, new ArrayList());
                orderFlowIdList.add((String) record.get(Constants.ORDER_FLOW_ID));

                //附件
                QueryWrapper<OrderFileDO> wrapper = new QueryWrapper();
                wrapper.eq(Constants.RES_WARNING_ORDER_ID, dto.getOrderId());
                wrapper.eq(Constants.PARAM_ORDER_FLOW_ID, record.get(Constants.ORDER_FLOW_ID));
                record.put(Constants.PARAM_ORDER_FILE_LIST, orderFileMapper.selectList(wrapper));

                //如果是调度获取处置人  林业需求
                if (EnumUtil.FlowNode.PENDING_DISPATCH.getCode().equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))) {
                    OrderFlowRecordDTO orderFlowRecordDTO = new OrderFlowRecordDTO();
                    orderFlowRecordDTO.setTableName(tableName);
                    orderFlowRecordDTO.setOrderId(dto.getOrderId());
                    orderFlowRecordDTO.setOrderFlowId(String.valueOf(record.get(Constants.ORDER_FLOW_ID)));
                    if (StringUtils.isEmpty((String) record.get(Constants.PROCESS_NAME_LIST))) {
                        OrderFlowRecordDO orderFlowRecordDO = orderFlowRecordMapper.getNextRecordByCurentLink(orderFlowRecordDTO);
                        if (null != orderFlowRecordDO) {
                            record.put(Constants.PARAM_HANDLE_PROCESS_ID, orderFlowRecordDO.getProcessId());
                            record.put(Constants.PARAM_HANDLE_PROCESS_NAME, orderFlowRecordDO.getProcessName());
                        }
                    } else {
                        record.put(Constants.PARAM_HANDLE_PROCESS_NAME, (String) record.get(Constants.PROCESS_NAME_LIST));
                    }
                }

            }
            param.put(Constants.PARAM_ORDER_FLOW_ID_LIST, orderFlowIdList);
            List<Map> leaderResult = orderLeaderAuditMapper.getLeaderListByFlow(param);
            if (!CollectionUtils.isEmpty(leaderResult)) {
                List<String> list = new ArrayList<>();
                list.add(Constants.RES_LEADER_NAME);
                list.add(Constants.RES_MOBILE_PHONE);
                this.handleUserInfo(leaderResult, Constants.RES_LEADER_ID, list);
                records = records.stream().map(item -> {
                    List leadersList = new ArrayList();
                    leaderResult.stream()
                            .filter(area -> Objects.equals(item.get(Constants.ORDER_FLOW_ID), area.get(Constants.ORDER_FLOW_ID)))
                            .forEach(area -> {
                                leadersList.add(area);
                            });
                    item.put(Constants.PARAM_LEADERS, leadersList);
                    return item;
                }).collect(Collectors.toList());
            }

            //督办记录
            List<HandleRecordVO> handleRecordResult = this.getHandleRecordList(dto.getOrderId(), ZERO);
            if (!CollectionUtils.isEmpty(handleRecordResult)) {
                records = records.stream().map(item -> {
                    List<HandleRecordVO> handleRecordList = new ArrayList<>();
                    handleRecordResult.stream()
                            .filter(handleRecordVO -> Objects.equals(item.get(Constants.ORDER_FLOW_ID), handleRecordVO.getOrderFlowId()))
                            .forEach(handleRecordVO -> {
                                handleRecordList.add(handleRecordVO);
                            });
                    item.put("handleRecordList", handleRecordList);
                    return item;
                }).collect(Collectors.toList());
            }

            if (!CollectionUtils.isEmpty(processOrderIdList)) {
                List<Map> proResult = warningOrderMapper.getProcessStatusById(processOrderIdList);
                records = records.stream().map(item -> {
                    proResult.stream()
                            .filter(area -> Objects.equals(item.get(Constants.PARAM_PROCESS_ORDER_ID), area.get(Constants.PARAM_PROCESS_ORDER_ID)))
                            .forEach(area -> {
                                item.put(Constants.PARAM_STATUS, area.get(Constants.PARAM_ORDER_STATUS));
                                if (Constants.ORDER_STATUS_6.equals(String.valueOf(item.get(Constants.PARAM_NEXT_LINK_ID)))) {
                                    item.put(Constants.PARAM_STATUS, Constants.ONE);
                                }
                            });
                    return item;
                }).collect(Collectors.toList());
            }

            //if (!CollectionUtils.isEmpty(approveOrderIdList)) {
            //List<Map> approveResult = warningOrderMapper.getApproveStatusById(approveOrderIdList);
            for (Map record : records) {
                if (EnumUtil.FlowNode.PENDING_CHECK.getCode().equals(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)))) {
                    if (Constants.VALID_STATE.equals(record.get(Constants.PARAM_IF_CURRENT_LINK))) {
                        record.put(Constants.PARAM_STATUS, Constants.TWO);
                    } else {
                        record.put(Constants.PARAM_STATUS, Constants.ONE);
                    }
                }
                if ("6".equals(record.get(Constants.PARAM_NEXT_LINK_ID))) {
                    // 环节直接呗关闭 完成
                    record.put(Constants.PARAM_STATUS, Constants.FOUR);
                }
                    /*for (Map approve:approveResult) {
                        //如果已经审批过，将审批状态返回，否则返回当前审批状态
                        if(!StringUtils.isEmpty(record.get(Constants.PARAM_APPROVE_ORDER_ID))){
                            if((record.get(Constants.PARAM_APPROVE_ORDER_ID).toString().contains((String)approve.get(Constants.PARAM_APPROVE_ID)))
                                    && Constants.TWO.equals(approve.get(Constants.PARAM_ORDER_STATUS))){
                                record.put(Constants.PARAM_STATUS, approve.get(Constants.PARAM_ORDER_STATUS));
                                break;
                            }else{
                                record.put(Constants.PARAM_STATUS, approve.get(Constants.PARAM_ORDER_STATUS));
                            }
                        }
                    }*/
                //}
                /*records = records.stream().map(item -> {
                    approveResult.stream()
                            .filter(area -> Objects.equals(item.get(Constants.PARAM_APPROVE_ORDER_ID), area.get(Constants.PARAM_APPROVE_ID)))
                            .forEach(area -> {
                                item.put(Constants.PARAM_STATUS, area.get(Constants.PARAM_ORDER_STATUS));
                            });
                    return item;
                }).collect(Collectors.toList());*/
            }

        }
        return records;
    }

    @Override
    public List<Map> getRecordByOrderIdAsc(AlarmOrderDTO dto) {
        log.info("getRecordByOrderIdAsc-req:{}", JSON.toJSONString(dto));
        dto.setSort(Constants.SORT_TYPE_ASC);
        List<Map> resultMap = this.getRecordByOrderId(dto);
        /*for (Map map:resultMap) {
            if(!StringUtils.isEmpty(map.get(Constants.PARAM_FILES))){
                map.put("orderFileList",map.get(Constants.PARAM_FILES));
            }
        }*/
        return resultMap;
    }

    /**
     * 获取从告警数据列表
     *
     * @param dto
     * @return
     * @author: chenmf
     * @date: 2021/12/02
     */
    @Override
    public List<WarningOrderVO> warningOrderList(WarningOrderDTO dto) {
        int pageNo = 0;
        int pageSize = 10;
        /*if (NullProcessUtil.isNull(dto.getMainAlarmId())) {
            return new ProcessResult(ProcessResult.ERROR, "主告警Id不能为空", Collections.emptyList());
        }*/
        String mainAlarmId = dto.getMainAlarmId();
        if (null != dto.getPageNum()) {
            pageNo = dto.getPageNum();
        }

        if (null != dto.getPageSize()) {
            if (dto.getPageSize() > 1000) {
                pageSize = 1000;
            } else {
                pageSize = dto.getPageSize();
            }
        }

        Page page = new Page(pageNo, pageSize);
        SortEntity sortEntity = new SortEntity();
        sortEntity.desc("alarmTime");
        page.setSortEntity(sortEntity);

        Set<String> columns = new HashSet<>();
        columns.add(Constants.PARAM_WARNING_ORDER_ID);
        columns.add(Constants.PARAM_MAIN_ALARM_ID);
        columns.add(Constants.PARAM_DEVICE_CODE);
        columns.add(Constants.PARAM_DEVICE_NAME);
        columns.add(Constants.PARAM_CHANNEL_CODE);
        columns.add(Constants.PARAM_CHANNEL_NAME);
        columns.add(Constants.PARAM_ALARM_TYPE);

        QueryEntity queryEntity = new QueryEntity("vm_warning_order_t", WarningOrderVO.class);
        queryEntity.eq(Constants.PARAM_MAIN_ALARM_ID, mainAlarmId);
        queryEntity.eq(Constants.PARAM_ALARM_FLAG, AlarmConstants.ALARM_FLAG_1);

        Page<WarningOrderVO> warningOrderList = warnOrderMongoDAO.findPage(columns, queryEntity, page, MongoDBType.PRIMARY);
        return warningOrderList.getList();
    }

    /**
     * 事件处理时间轴
     *
     * @param dto
     * @return
     */
    @Override
    public List<Map> getTimeAxis(AlarmOrderDTO dto) {
        // 获取表名
        String tableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + dto.getOrderId().substring(0, 6);
        // 封装查询参数
        dto.setTableName(tableName);
        List<Map> list = warningOrderMapper.getTimeAxis(dto);
        return list;
    }

    public void getNextLinkProcessNameList(List<FlowRecordVO> result) {
        for (FlowRecordVO vo : result) {
            if (!StringUtils.isEmpty(vo.getDispatchProcessName())) {
                List<String> thisUserIdList = Arrays.asList(vo.getDispatchProcessName().split(Constants.COMMA));
                List<UserInfoDto> userList = sysMapper.queryUserListInfo(thisUserIdList);
                for (UserInfoDto user : userList) {
                    if (StringUtils.isEmpty(user.getAvatar())) {
                        user.setAvatar("");
                    }
                }
                //返回该环节所有选择处理人信息
                vo.setUserInfoList(userList);
                String nextLinkProcessName = "";
                for (UserInfoDto user : userList) {
                    if (StringUtils.isEmpty(nextLinkProcessName)) {
                        nextLinkProcessName = !StringUtils.isEmpty(user.getNickName()) ? user.getNickName() : user.getUserName();
                    } else {
                        nextLinkProcessName += Constants.COMMA + (!StringUtils.isEmpty(user.getNickName()) ? user.getNickName() : user.getUserName());
                    }
                }
                vo.setNextLinkProcessName(nextLinkProcessName);
            }
        }
    }

    /**
     * 获取流程流转过程数据
     *
     * @param dto orderId
     *            linkId
     * @return
     */
    @Override
    public List<FlowRecordVO> getRecordByType(AlarmOrderDTO dto) {
        List<FlowRecordVO> result = new ArrayList<>();

        //add by lijiwei
        String tableName = TableUtil.getTableName(DateUtils.stringToDateYM(dto.getOrderId().substring(0, 6)),
                TableConstant.WARN_ORDER_PROCESS_TEBLE);
        //add by lijiwei

        if (EnumUtil.FlowNode.PENDING_JUDGE.getCode().equals(dto.getRealLinkType())) {
            // 研判过程
            Map mapIn = new HashMap();
            mapIn.put(Constants.PARAM_ORDER_ID, dto.getOrderId());
            mapIn.put(Constants.PARAM_TABLE_NAME, tableName);
            mapIn.put(Constants.PARAM_WARNING_ORDER_TABLE, TableUtil.getTableName(DateUtils.stringToDateYM(dto.getOrderId().substring(0, 6)), TableConstant.WARN_ORDER_TEBLE));
            result = warningOrderMapper.getPendingJudgeList(mapIn);
            //获取mobilePhone
            this.getUserInfo(result, dto.getRealLinkType());
            //int judgeNum = 0;
            this.getNextLinkProcessNameList(result);
            for (FlowRecordVO mapResult : result) {
                mapResult.setStatus("");
                if (Constants.VALID_STATE.equals(mapResult.getIfCurrentLink())) {
                    mapResult.setStatus(Constants.ONE);
                }
                String duplicateOrderId = mapResult.getDuplicateOrderId();
                if (duplicateOrderId != null && !"".equals(duplicateOrderId)) {
                    Map duplicateMap = new HashMap();
                    duplicateMap.put(Constants.PARAM_TABLE_NAME, TableUtil.getTableName(DateUtils.stringToDateYM(duplicateOrderId.substring(0, 6)), TableConstant.WARN_ORDER_TEBLE));
                    duplicateMap.put(Constants.PARAM_ORDER_ID, duplicateOrderId);
                    OrderInfoVO orderInfoVO = warningOrderMapper.getAlarmBody(duplicateMap);
                    mapResult.setDuplicateAlarmBody(orderInfoVO.getAlarmBody());
                }
                //如果是当前环节研判结果特殊处理(回显为空)
                if (Constants.ONE.equals(mapResult.getIfCurrentLink())) {
                    mapResult.setJudgeResult(Constants.THREE);
                } else if ("0".equals(mapResult.getNextLinkId())) {
                    //误报
                    mapResult.setJudgeResult(Constants.ZERO);
                } else if ("7".equals(mapResult.getNextLinkId())) {
                    mapResult.setJudgeResult(Constants.TWO);
                } else if ("6".equals(mapResult.getNextLinkId())) {
                    mapResult.setJudgeResult("");
                } else {
                    //真警
                    mapResult.setJudgeResult(Constants.ONE);
                }
                //judgeNum++;
            }
        } else if (EnumUtil.FlowNode.PENDING_DISPATCH.getCode().equals(dto.getRealLinkType())) {
            // 调度过程
            try {
                Map mapIn = new HashMap();
                mapIn.put(Constants.PARAM_ORDER_ID, dto.getOrderId());
                mapIn.put(Constants.PARAM_TABLE_NAME, tableName);
                result = warningOrderMapper.getPendingDispatchByParam(mapIn);
                this.getNextLinkProcessNameList(result);
                /*for (FlowRecordVO flowRecordVO : result) {
                    flowRecordVO.setOrderId(dto.getOrderId());
                }*/
                // 调度环节新增处置人员
                OrderFlowRecordDTO flowRecordDTO = new OrderFlowRecordDTO();
                flowRecordDTO.setOrderId(dto.getOrderId());
                flowRecordDTO.setTableName(tableName);

                this.getUserInfo(result, dto.getRealLinkType());

                for (FlowRecordVO flowRecord : result) {
                    if (result != null && result.size() > 0) {
                        flowRecord.setStatus("");
                        if (Constants.VALID_STATE.equals(flowRecord.getIfCurrentLink())) {
                            flowRecord.setStatus(Constants.ONE);
                        }
                        if (StringUtils.isEmpty(flowRecord.getDispatchProcessName())) {
                            if (Constants.ONE.equals(flowRecord.getIfCurrentLink())) {
                                //如果该调度为当前环节，所选处置人为空
                                flowRecord.setJudgedName("");
                            } else {
                                //环节信息指定处置人为空时用处置环节处理人展示
                                List<FlowRecordVO> resultHandle = warningOrderMapper.getPendingHandle(flowRecordDTO);
                                if (resultHandle != null && resultHandle.size() > 0) {
                                    //返回处置人员
                                    //flowRecord.setHandleProcessName(resultHandle.get(0).getProcessName());
                                    flowRecord.setJudgedName(resultHandle.get(0).getProcessName());
                                }
                            }
                        } else {
                            //环节信息指定处置人不为空时用处置环节处理人展示
                            flowRecord.setJudgedName(flowRecord.getDispatchProcessName());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info(e.getMessage());
            }
            // result = alarmOrderNewDAO.getPendingDispatch(orderId);
        } else if (EnumUtil.FlowNode.PENDING_HANDLE.getCode().equals(dto.getRealLinkType())) {
            // 处置过程
            OrderFlowRecordDTO flowRecordDTO = new OrderFlowRecordDTO();
            flowRecordDTO.setOrderId(dto.getOrderId());
            flowRecordDTO.setTableName(tableName);
            result = warningOrderMapper.getPendingHandle(flowRecordDTO);
            this.getNextLinkProcessNameList(result);
            if (result != null) {
                for (FlowRecordVO flowRecordVO : result) {
                    String attachments = "0";
                    String processOrderId4 = flowRecordVO.getProcessOrderId();
                    if (processOrderId4 != null) {
                        List<Map> mapTemp4 = processOrderMapper.getPhotos(processOrderId4);
                        //处置环节新增返回处置附件信息
                        flowRecordVO.setOrderHandleFileList(mapTemp4);
                        attachments = mapTemp4 != null && mapTemp4.size() > 0 ? "1" : "0";
                    }
                    if (!Constants.TWO.equals(flowRecordVO.getEnableAcceptanceMode())) {
                        flowRecordVO.setIfAttachment(attachments);
                    }
                }
            }
            //获取mobilePhone
            this.getUserInfo(result, dto.getRealLinkType());
        } else if (EnumUtil.FlowNode.PENDING_CHECK.getCode().equals(dto.getRealLinkType())) {
            // 核实过程
            try {
                OrderFlowRecordDTO flowRecordDTO = new OrderFlowRecordDTO();
                flowRecordDTO.setOrderId(dto.getOrderId());
                flowRecordDTO.setTableName(tableName);
                Map mapIn = new HashMap();
                mapIn.put(Constants.PARAM_ORDER_ID, dto.getOrderId());
                mapIn.put(Constants.PARAM_TABLE_NAME, tableName);
                //是否查询列表 空查询一条  1：查询多条
                List<String> processOrderIdList = new ArrayList<>();
                if (StringUtils.isEmpty(dto.getQueryList())) {
                    List<FlowRecordVO> res = warningOrderMapper.getPendingHandle(flowRecordDTO);
                    if (!CollectionUtils.isEmpty(res)) {
                        for (FlowRecordVO vo : res) {
                            if (!StringUtils.isEmpty(vo.getProcessOrderId())) {
                                processOrderIdList.add(vo.getProcessOrderId());
                            }
                        }
                        mapIn.put(Constants.PARAM_PROCESS_ORDER_ID, processOrderIdList);
                    }
                }
                result = warningOrderMapper.getPendingCheckByParam(mapIn);
                this.getNextLinkProcessNameList(result);
                //获取mobilePhone、processName
                this.getUserInfo(result, dto.getRealLinkType());
                // 添加研判信息
                List<FlowRecordVO> dispatchList = warningOrderMapper.getPendingDispatchByParam(mapIn);
                this.getUserInfo(result, EnumUtil.FlowNode.PENDING_DISPATCH.getCode());
                this.getUserInfo(result, EnumUtil.FlowNode.PENDING_HANDLE.getCode());
                if (!CollectionUtils.isEmpty(dispatchList)) {
                    for (FlowRecordVO map : result) {
                        for (FlowRecordVO item : dispatchList) {
                            if (DateUtils.string2Date((String) item.getFlowNodeTime())
                                    .before(DateUtils.string2Date((String) map.getFlowNodeTime()))) {
                                map.setDispatchProcessName(item.getProcessName());
                                map.setDispatchTime(item.getFlowNodeTime());
                                map.setDispatchRemark(item.getRemark());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("getRecordByType异常:{}", e);
            }
        }
        //查询附件
        if (!CollectionUtils.isEmpty(result)) {
            result.forEach(res -> {
                OrderFileDO orderFileDO = new OrderFileDO();
                orderFileDO.setOrderFlowId(res.getOrderFlowId());
                orderFileDO.setWarningOrderId(dto.getOrderId());
                QueryWrapper<OrderFileDO> wrapper = new QueryWrapper();
                wrapper.eq(Constants.RES_WARNING_ORDER_ID, dto.getOrderId());
                wrapper.eq(Constants.PARAM_ORDER_FLOW_ID, res.getOrderFlowId());
                res.setOrderFileList(orderFileMapper.selectList(wrapper));
            });
        }


        return result;
    }

    /**
     * 处理用户信息
     *
     * @param result
     * @param realLinkType
     */
    public void getUserInfo(List<FlowRecordVO> result, String realLinkType) {
        if (!CollectionUtils.isEmpty(result)) {
            List<String> userIdList = new ArrayList<>();
            List<String> deptIdList = new ArrayList<>();
            for (FlowRecordVO vo : result) {
                if (!StringUtils.isEmpty(vo.getProcessId())) {
                    userIdList.add(vo.getProcessId());
                }
                if (!StringUtils.isEmpty(vo.getAcceptProcessId())) {
                    userIdList.add(vo.getAcceptProcessId());
                }
                if (EnumUtil.FlowNode.PENDING_DISPATCH.getCode().equals(realLinkType)
                        && !StringUtils.isEmpty(vo.getWarningProcessId())) {
                    userIdList.add(vo.getWarningProcessId());
                }
                if (EnumUtil.FlowNode.PENDING_CHECK.getCode().equals(realLinkType)
                        && !StringUtils.isEmpty(vo.getApprovePerson())) {
                    userIdList.add(vo.getApprovePerson());
                }
                if (!ObjectUtils.isNotEmpty(vo.getGroupId())) {
                    deptIdList.add(vo.getGroupId());
                }
                if (EnumUtil.FlowNode.SCHEDULING_COMMAND.getCode().equals(realLinkType)
                        && !StringUtils.isEmpty(vo.getPendingHandleId())) {
                    userIdList.add(vo.getPendingHandleId());
                }
            }
            if (!CollectionUtils.isEmpty(userIdList)) {
                List<SysUserVO> sysUserList = deviceRepository.getUserInfoByUserIds(userIdList);
                List<SysDept> sysDeptList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(deptIdList)) {
                    sysDeptList = deviceRepository.getSysDeptByDeptIds(deptIdList);
                }
                for (FlowRecordVO flowRecordVO : result) {
                    for (SysUserVO sysUser : sysUserList) {
                        if (!StringUtils.isEmpty(flowRecordVO.getAcceptProcessId()) && flowRecordVO.getAcceptProcessId().equals(sysUser.getUserId())) {
                            flowRecordVO.setAcceptProcessName(!StringUtils.isEmpty(sysUser.getNickName()) ? sysUser.getNickName() : sysUser.getUserName());
                            flowRecordVO.setAcceptProcessPhone(sysUser.getPhonenumber());
                        }
                        if (!StringUtils.isEmpty(flowRecordVO.getProcessId()) && flowRecordVO.getProcessId().equals(sysUser.getUserId())) {
                            flowRecordVO.setProcessName(sysUser.getNickName());
                            if (EnumUtil.FlowNode.PENDING_JUDGE.getCode().equals(realLinkType)
                                    || EnumUtil.FlowNode.PENDING_HANDLE.getCode().equals(realLinkType)) {
                                flowRecordVO.setMobilePhone(sysUser.getPhonenumber());
                            } else if (EnumUtil.FlowNode.PENDING_DISPATCH.getCode().equals(realLinkType)) {
                                if (!StringUtils.isEmpty(sysUser.getPhonenumber())) {
                                    flowRecordVO.setJudgedPhone(sysUser.getPhonenumber());
                                } else {
                                    flowRecordVO.setJudgedPhone(sysUser.getTelPhone());
                                }
                                //flowRecordVO.setJudgedName(sysUser.getUserName());
                                flowRecordVO.setJudgeDeptName(sysUser.getDeptName());
                                /*String tableName = TableUtil.getTableName(
                                        DateUtils.stringToDateYM(flowRecordVO.getOrderId().substring(0, 6)),
                                        TableConstant.WARN_ORDER_TEBLE);
                                AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
                                alarmOrderDTO.setTableName(tableName);
                                alarmOrderDTO.setOrderId(flowRecordVO.getOrderId());
                                OrderInfoVO orderInfo = warningOrderMapper.getOrderInfo(alarmOrderDTO);
                                flowRecordVO.setProcessName(orderInfo.getNextProcessName());*/
                            } else if (EnumUtil.FlowNode.PENDING_CHECK.getCode().equals(realLinkType)) {
                                flowRecordVO.setMobilePhone(sysUser.getPhonenumber());
                                flowRecordVO.setProcessName(!StringUtils.isEmpty(sysUser.getNickName()) ? sysUser.getNickName() : sysUser.getUserName());
                            }
                        }
                        if (!StringUtils.isEmpty(flowRecordVO.getWarningProcessId())
                                && flowRecordVO.getWarningProcessId().equals(sysUser.getUserId())) {
                            if (!StringUtils.isEmpty(sysUser.getPhonenumber())) {
                                flowRecordVO.setMobilePhone(sysUser.getPhonenumber());
                            } else {
                                flowRecordVO.setMobilePhone(sysUser.getTelPhone());
                            }
                        }
                        // 核实会签时返回核实人姓名
                        if (EnumUtil.FlowNode.PENDING_CHECK.getCode().equals(realLinkType)
                                && !StringUtils.isEmpty(flowRecordVO.getApprovePerson())
                                && flowRecordVO.getApprovePerson().equals(sysUser.getUserId())) {
                            flowRecordVO.setProcessName(!StringUtils.isEmpty(sysUser.getNickName()) ? sysUser.getNickName() : sysUser.getUserName());
                        }
                        if (EnumUtil.FlowNode.SCHEDULING_COMMAND.getCode().equals(realLinkType)
                                && !StringUtils.isEmpty(flowRecordVO.getPendingHandleId())
                                && flowRecordVO.getPendingHandleId().equals(sysUser.getUserId())) {
                            flowRecordVO.setPendingHandleName(!StringUtils.isEmpty(sysUser.getNickName()) ? sysUser.getNickName() : sysUser.getUserName());
                        }
                    }

                    if (!CollectionUtils.isEmpty(sysDeptList)) {
                        for (SysDept sysDept : sysDeptList) {
                            if (!StringUtils.isEmpty(flowRecordVO.getGroupId())) {
                                if (sysDept.getDeptId().equals(flowRecordVO.getGroupId())) {
                                    flowRecordVO.setJudgedName(sysDept.getDeptName());
                                    flowRecordVO.setOrgName(sysDept.getDeptName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过告警工单id获取领导批示列表
     *
     * @param dto orderId
     * @return
     */
    @Override
    public List<LeaderInfoByOrderIdVO> getLeaderListByOrderId(AlarmOrderDTO dto) {
        //通过告警工单Id生成月份分表表名
        String tableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + dto.getOrderId().substring(0, 6);

        //获取当前工单行业编码
        dto.setTableName(TableConstant.WARN_ORDER_TEBLE + dto.getOrderId().substring(0, 6));
        OrderInfoVO orderInfo = warningOrderMapper.getOrderInfo(dto);
        String industryCode = orderInfo.getTradeId();

        //通过告警工单id获取领导批示列表
        List<LeaderInfoByOrderIdVO> list = warningOrderMapper.getLeaderAuditListByOrderId(tableName, dto.getOrderId());
        if (!CollectionUtils.isEmpty(list)) {
            //构造用户信息列表
            Set<String> leaderIdSet = list.stream().filter(item -> !item.getLeaderId().equals("")).map(item -> item.getLeaderId()).collect(Collectors.toSet());
            Set<String> userIdSet = list.stream().filter(item -> !item.getUserId().equals("")).map(item -> item.getUserId()).collect(Collectors.toSet());
            List<String> userIdList = new LinkedList<>(userIdSet);
            userIdList.addAll(leaderIdSet);
            if (!CollectionUtils.isEmpty(userIdList)) {
                //查询用户信息
                List<SysUserVO> userList = deviceRepository.getUserInfoByUserIdsAndIndustryCode(userIdList, industryCode);
                //查询部门信息
                List<String> deptIdList = userList.stream().filter(sysUserVO -> !StringUtils.isEmpty(sysUserVO.getDeptId())).map(SysUserVO::getDeptId).collect(Collectors.toList());
                List<SysDept> sysDeptList = deviceRepository.getSysDeptByDeptIds(deptIdList);
                //解决map中key相同的问题：使用的是两个key 相同时，保留先存进去的那个元素
                Map<String, String> sysDeptMap = sysDeptList.stream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (item1, item2) -> item1));
                //填充告警状态名称
                List<SysDictDataResponse> orderStatusEnum = this.getEnumList(Constants.ORDER_STATUS);
                list.forEach(map -> {
                    if (!CollectionUtils.isEmpty(orderStatusEnum)) {
                        orderStatusEnum.forEach(item -> {
                            if (item.getDictValue().equals(map.getOrderStatus())) {
                                map.setOrderStatusName(item.getDictLabel());
                            }
                        });
                    }
                    if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(map.getOrderStatus())
                            || EnumUtil.FlowNode.DUPLICATION.getCode().equals(map.getOrderStatus())
                            || EnumUtil.FlowNode.END.getCode().equals(map.getOrderStatus())) {
                        map.setOrderStatusName(EnumUtil.FlowNode.END.getName());
                    }
                    Object leaderId = map.getLeaderId();
                    Object userId = map.getUserId();
                    //填充用户名称、所属组织名称、手机号码
                    userList.forEach(sysUserVO -> {
                        if (String.valueOf(sysUserVO.getUserId()).equals(String.valueOf(leaderId))) {
                            map.setLeaderName(sysUserVO.getNickName());
                            map.setMobilePhone(sysUserVO.getPhonenumber());
                            map.setLeaderOrgName(sysDeptMap.get(sysUserVO.getDeptId()));
                        }
                        if (String.valueOf(sysUserVO.getUserId()).equals(String.valueOf(userId))) {
                            map.setUserName(sysUserVO.getNickName());
                            map.setUserMobilePhone(sysUserVO.getPhonenumber());
                            map.setUserOrgName(sysDeptMap.get(sysUserVO.getDeptId()));
                        }
                    });
                });
            }
        }
        return list;
    }

    /**
     * 大屏查询结果转化
     *
     * @param resultList
     */
    @EventObsEncrypt
    @Override
    public void resultMapHandle(List<Map> resultList) {
        if (!CollectionUtils.isEmpty(resultList)) {
            // 查询告警类型名称
            List<Map> alramTypeList = getAlarmTypeList();
            // 告警等级名称
            List<SysDictDataResponse> emergencyLevelEnum = getEnumList(Constants.EMERGENCY_LEVEL);
            // 告警状态名称
            List<SysDictDataResponse> orderStatusEnum = getEnumList(Constants.ORDER_STATUS);
            // 告警来源枚举
            List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
            // 重要等级
            List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(Constants.ALARM_LEVEL).getData();
            for (Map m : resultList) {
                m.put(Constants.PARAM_IF_TIMEOUT, 0);
                if (Objects.nonNull(m.get(Constants.PARAM_FLOW_NODE_TIME)) && !orderStatusList.contains(String.valueOf(m.get(Constants.PARAM_ORDER_STATUS)))) {
                    m.put("duration", DateUtils.getTimeDifference2(new Date(), DateUtils.string2Date(String.valueOf(m.get(Constants.PARAM_FLOW_NODE_TIME)))));
                    //受理状态的工单超期展示受理超期
                    if (Constants.ONE.equals(String.valueOf(m.get("enableAcceptanceMode")))) {
                        if (StringUtils.isEmpty(String.valueOf(m.get("timeLimitAcceptance")))) {
                            m.put(Constants.PARAM_TIME_LIMIT, null);
                        } else {
                            m.put(Constants.PARAM_TIME_LIMIT, Integer.valueOf(String.valueOf(m.get("timeLimitAcceptance"))));
                        }
                    }
                    // timeLimit 为-1 表示不超期
                    if (Objects.nonNull(m.get(Constants.PARAM_TIME_LIMIT))
                            && !EnumUtil.FlowNode.END.getCode().equals(String.valueOf(m.get(Constants.PARAM_ORDER_STATUS)))
                            && !"-1".equals(String.valueOf(m.get(Constants.PARAM_TIME_LIMIT)))) {
                        m.put(Constants.PARAM_IF_TIMEOUT, DateUtils.checkIfTimeOut(Integer.valueOf(String.valueOf(m.get(Constants.PARAM_TIME_LIMIT))), DateUtils.string2Date(String.valueOf(m.get(Constants.PARAM_FLOW_NODE_TIME)))));
                        if (Integer.valueOf(String.valueOf(m.get(Constants.PARAM_IF_TIMEOUT))) == 0) {
                            // 未超期
                            m.put("showTime", "剩余" + DateUtils.getTimeDifference(DateUtils.subtractTimeMinutes(DateUtils.string2Date(String.valueOf(m.get(Constants.PARAM_FLOW_NODE_TIME))), Integer.valueOf(String.valueOf(m.get(Constants.PARAM_TIME_LIMIT)))), new Date()));
                        } else {
                            // 超期
                            m.put("showTime", "超期" + DateUtils.getTimeDifference(new Date(), DateUtils.subtractTimeMinutes(DateUtils.string2Date(String.valueOf(m.get(Constants.PARAM_FLOW_NODE_TIME))), Integer.valueOf(String.valueOf(m.get(Constants.PARAM_TIME_LIMIT))))));

                        }
                    }
                }


                if (!CollectionUtils.isEmpty(alramTypeList)) {
                    // 设置图标
                    alramTypeList.forEach(item -> {
                        if (item.get(Constants.PARAM_TYPE_VALUE_).toString()
                                .equals(m.get(Constants.PARAM_WARNING_TYPE_ID))) {
                            m.put(Constants.PARAM_ICON, item.get(Constants.PARAM_ICON));
                            m.put(Constants.PARAM_WARNING_TYPE_NAME, item.get(Constants.PARAM_TYPENAME));
                        }
                    });
                }

                if (!CollectionUtils.isEmpty(emergencyLevelEnum)) {
                    emergencyLevelEnum.forEach(item -> {
                        if (item.getDictValue().equals(m.get(Constants.PARAM_EMERGENCY_LEVEL))) {
                            m.put(Constants.PARAM_EMERGENCY_LEVEL_NAME, item.getDictLabel());
                        }
                    });
                }

                if (!CollectionUtils.isEmpty(orderStatusEnum)) {
                    orderStatusEnum.forEach(item -> {
                        if (item.getDictValue().equals(m.get(Constants.PARAM_ORDER_STATUS))) {
                            m.put(Constants.PARAM_ORDER_STATUS_NAME, item.getDictLabel());
                        }
                    });
                }
                if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(m.get(Constants.PARAM_ORDER_STATUS)) || EnumUtil.FlowNode.DUPLICATION.getCode().equals(m.get(Constants.PARAM_ORDER_STATUS))) {
                    m.put(Constants.PARAM_ORDER_STATUS_NAME, EnumUtil.FlowNode.END.getName());
                }

                // 设置告警来源
                if (!CollectionUtils.isEmpty(warningSourceEnum)) {
                    warningSourceEnum.forEach(item -> {
                        if (item.getDictValue().equals(m.get(Constants.PARAM_WARNING_SOURCE))) {
                            m.put(Constants.PARAM_WARNING_SOURCE_NAME, item.getDictLabel());
                        }
                    });
                }

                // 20211127 zzh 处理图片，多张图片取第一张
                String imgUrl = (String) m.get(Constants.PARAM_IMG_URL);
                if (!StringUtils.isEmpty(imgUrl) && imgUrl.contains(Constants.COMMA)) {
                    m.put(Constants.PARAM_IMG_URL, imgUrl.split(Constants.COMMA)[0]);
                }

                // 设置重要等级名称
                if (!CollectionUtils.isEmpty(importanceLevelEnum)) {
                    importanceLevelEnum.forEach(item -> {
                        if (item.getDictValue().equals(m.get(Constants.PARAM_IMPORTANCE_LEVEL))) {
                            m.put(Constants.PARAM_IMPORTANCE_LEVEL_NAME, item.getDictLabel());
                        }
                    });
                }
            }
        }
    }

    /**
     * 获取领导批示详情
     *
     * @param dto
     * @return
     */
    @Override
    public Map getLeaderAufitInfo(OrderLeaderAuditDO dto) throws Exception {

        Map map = orderLeaderAuditMapper.getLeaderAuditInfo(dto.getAuditId());
        if (MapUtils.isEmpty(map)) {
            throw new Exception("领导批示信息不存在");
        }
        Map<Function, Object> paramMap = new ConcurrentHashMap<>();
        Function<Map, Map> fun1 = this::getApplyUser;
        paramMap.put(fun1, map);
        Function<Map, Map> fun2 = this::getLeaderInfo;
        paramMap.put(fun2, map);
        List<Function> listFunc = Arrays.asList(fun1, fun2);
        ThreadParallelUtil.run(listFunc, f -> {
            Object o = paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_LEADER);

        // 告警类型名称
        List<Map> alramTypeList = getAlarmTypeList();
        if (!CollectionUtils.isEmpty(alramTypeList)) {
            // 设置图标
            alramTypeList.forEach(item -> {
                if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(map.get(Constants.PARAM_WARNING_TYPE_ID))) {
                    map.put(Constants.PARAM_WARING_NAME, item.get(Constants.PARAM_TYPENAME));
                    map.put(Constants.PARAM_ICON, item.get(Constants.PARAM_ICON));
                }
            });
        }

        // linkName
        List<SysDictDataResponse> list = getEnumList(Constants.ENUM_FLOW);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(item -> {
                if (item.getDictValue().equals(map.get(Constants.PARAM_ORDER_STATUS))) {
                    map.put(Constants.PARAM_LINK_NAME, item.getDictLabel());
                }
            });
        }

        // v1.6.80 返回与流程配置中一致的linkName（用于小程序，告警详情办理进度处展示）
        String warningOrderId = map.get(Constants.PARAM_ORDER_ID).toString();
        String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderId.substring(0, 6);
        List<Map> records = warningOrderMapper.getRecordByOrderId(warningOrderId, recordTableName);
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(item -> {
                if (map != null && map.containsKey(Constants.ORDER_FLOW_ID) && map.get(Constants.ORDER_FLOW_ID).equals(item.get(Constants.ORDER_FLOW_ID))) {
                    map.put("flowLinkName", item.get(Constants.PARAM_LINK_NAME));
                }
            });
        }
        return map;
    }

    private Map getLeaderInfo(Map map) {
        Map<String, String> leader = deviceRepository.getUserInfoById(String.valueOf(map.get(Constants.PARAM_LEADER_ID)));
        if (MapUtils.isNotEmpty(leader)) {
            map.put(Constants.PARAM_LEADER_NAME, leader.get(Constants.NICKNAME));
            map.put(Constants.PARAM_LEADER_PHONE, leader.get(Constants.MOBILE_PHONE));
            map.put(Constants.PARAM_LEADER_ORG_NAME, leader.get(Constants.ORG_NAME));
        }
        return map;
    }

    private Map getApplyUser(Map map) {
        Map<String, String> user = deviceRepository.getUserInfoById(String.valueOf(map.get(Constants.PARAM_APPLY_USER_ID)));
        if (MapUtils.isNotEmpty(user)) {
            map.put(Constants.PARAM_APPLY_USER_NAME, user.get(Constants.NICKNAME));
            map.put(Constants.PARAM_APPLY_USER_PHONE, user.get(Constants.MOBILE_PHONE));
            map.put(Constants.PARAM_APPLY_USER_ORG_NAME, user.get(Constants.ORG_NAME));
        }
        return map;
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
     * 添加报警事件-人工上报
     *
     * @param receiveAlarmVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
            String userId = "";
            String userName = "";
            String mobilePhone = "";
            String tenantId = "";
            String tenantName = "";
            LoginUser loginUser = new LoginUser();
            if ("11".equals(receiveAlarmVO.getWarningSource())) {
                tenantId = receiveAlarmVO.getTenantId();
                SysUser user = new SysUser();
                user.setTenantId(receiveAlarmVO.getTenantId());
                user.setIndustryCode(receiveAlarmVO.getIndustryCode());
                user.setAppVerCode(receiveAlarmVO.getAppVerCode());
                tenantName = receiveAlarmVO.getTenantName();
                loginUser.setUser(user);
                if (!ObjectUtil.isEmpty(receiveAlarmVO.getExtendField())) {
                    receiveAlarmVO.setExtraField(JSON.toJSONString(receiveAlarmVO.getExtendField()));
                }
                // 摄像机告警定位配置判断
                /*DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(receiveAlarmVO.getDeviceCode(),receiveAlarmVO.getChannelCode());
                //设置告警点相关信息
                warningOrderUtil.setAlarmLocation(deviceSiteVO, receiveAlarmVO);*/
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

            // 删除无用代码
//            String parentOrgId = "";
//            String parentOrgId = currentUser.getString("parentOrgId");// 直属组织编码（非部门）

            // String orgId = currentUser.getString("orgId");//登陆人所属部门编码
            String alarmTime = receiveAlarmVO.getAlarmTime();
            if (!alarmTime.contains(":")) {
                alarmTime += " 00:00:00";
            }
            warningOrderId = PKUtils.getWarnOrderId(tenantId, alarmTime);
            String alarmType = receiveAlarmVO.getWarningTypeId();

            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

            // 如果分月表不存在，则根据告警时间按创建分月表
            createNewTable(alarmTime);

            receiveAlarmVO.setTenantId(tenantId);
            receiveAlarmVO.setTenantName(tenantName);
            receiveAlarmVO.setFindName(userName);
            receiveAlarmVO.setUserName(userName);
            receiveAlarmVO.setFindMobile(mobilePhone);
            receiveAlarmVO.setUserId(userId);
            receiveAlarmVO.setWarningOrderId(warningOrderId);
            if (!StringUtils.isEmpty(alarmType)) {
                receiveAlarmVO.setAlarmType(alarmType);
            }
            // 删除无用代码
//            receiveAlarmVO.setOrgId(parentOrgId);

            String latitude = receiveAlarmVO.getLatitude();
            String longitude = receiveAlarmVO.getLongitude();
            // APP上报时根据经纬度获取省市区编码  第三方告警上报也是如此
            if (Constants.TWO_FLAG.equals(receiveAlarmVO.getReportType()) ||
                    ("11".equals(receiveAlarmVO.getWarningSource()) && !StringUtils.isEmpty(receiveAlarmVO.getLongitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLatitude()))) {
                try {
                    PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                    req.setLocation(String.format(Constants.FORMAT_S, receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude()));
                    log.info("逆地理编码查询接口poiContraryGeocodingQuery req:{}" + JSONObject.toJSONString(req));
                    Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                    log.info("逆地理编码查询接口poiContraryGeocodingQuery resp:{}" + JSONObject.toJSONString(result));
                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                        try {
                            PoiContraryGeocodingQueryRes data = result.getData().get(0);
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
                        } catch (Exception e) {
                            log.error("addAlarmOrder gisMapApi poiContraryGeocodingQuery error, e:", e);
                        }
                    }
                } catch (Exception e) {
                    log.error("通过经纬度获取省市区编码异常");
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

            // 根据经纬度获取网格信息
            if (!StringUtils.isEmpty(latitude) && !StringUtils.isEmpty(longitude)) {
                Result<List<GridByLngLat>> result = this.getMessageByLngLat(loginUser, latitude, longitude);
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                    List<GridByLngLat> gridList = result.getData();
                    if (!CollectionUtils.isEmpty(gridList)) {
                        LoginUser finalLoginUser = loginUser;
                        String finalTenantId = tenantId;
                        GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(finalTenantId, grid.getTenantId()) && Objects.equals(finalLoginUser.getUser().getIndustryCode(), grid.getIndustryCode()))
                                .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                        receiveAlarmVO.setGridId(gridInfo.getGridId());
                        receiveAlarmVO.setGridName(gridInfo.getGridName());
                        receiveAlarmVO.setGridUserList(gridInfo.getUserList());
                    }
                }
            }
            // 2021-06-21 end
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

            // 默认流程，第一个节点为研判环节
            //tenantId、warningSource、alarmType
            //tenantId
            //flowId = -1
            AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
            alarmFlowConfigModel.setTenantId(tenantId);
            alarmFlowConfigModel.setAlarmCode(alarmType);

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

            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
            alarms.add(receiveAlarmVO);
            // 创建流程
            List<Map> recordList = createRecord(alarms);
            // 回填当前环节相关字段
            this.setWarningLinkInfo(alarms, recordList);

            List<ReceiveAlarmVO> list = new ArrayList<>();
            ReceiveAlarmVO receiveAlarmTemp = alarms.get(0);
            if (receiveAlarmTemp != null && StringUtils.isEmpty(receiveAlarmTemp.getJudgeType())) {
                receiveAlarmTemp.setJudgeType(Constants.JUDGE_TYPE_COMMON);
            }
            list.add(receiveAlarmTemp);
            receiveAlarmVODB.setList(list);
            int i = warningOrderMapper.insertWarningOrders(receiveAlarmVODB);
            //异步推送第三方
            try {
                pushThirdService.pushThirdOrder(originalJson, list);
            } catch (Exception e) {
                log.error("人工上报推送第三方异常:", e);
            }

            if (i <= 0) {
                return Result.fail("保存告警数据失败");
            }
            // 20210626 消息推送
            MessagePushDTO messagePushDTO = new MessagePushDTO();
            messagePushDTO.setOrderId(warningOrderId);
            messagePushDTO.setMsgId(warningOrderId);
            messagePushDTO.setTenantId(tenantId);
            messagePushDTO.setAlarmFlowId(alarms.get(0).getFlowId());
            messagePushDTO.setLinkId(alarms.get(0).getLinkId());
            messagePushDTO.setNotifyType(Constants.ONE);
            messagePushDTO.setCreator(userId);
            messagePushDTO.setNotifyTitle(alarms.get(0).getAlarmBody());
            messagePushDTO.setTypeValue(String.valueOf(alarms.get(0).getAlarmType()));
            messagePushDTO.setAddress(alarms.get(0).getAddress());
            messagePushDTO.setUserType(alarms.get(0).getUserType());
            messagePushDTO.setMsgNotifyType(alarms.get(0).getMsgNotifyType());
            String processId = alarms.get(0).getProcessId();
            if (!StringUtils.isEmpty(processId)) {
                if (processId.startsWith(Constants.COMMA)) {
                    processId = processId.substring(1, processId.length());
                }
                if (processId.endsWith(Constants.COMMA)) {
                    processId = processId.substring(0, processId.length() - 1);
                }
            }
            messagePushDTO.setUserId(processId);
            String roleId = alarms.get(0).getRoleId();
            if (!StringUtils.isEmpty(roleId)) {
                if (roleId.startsWith(Constants.COMMA)) {
                    roleId = roleId.substring(1, roleId.length());
                }
                if (roleId.endsWith(Constants.COMMA)) {
                    roleId = roleId.substring(0, roleId.length() - 1);
                }
            }
            messagePushDTO.setRoleId(roleId);
            messagePushDTO.setNotifyType(Constants.SYS_MSG_TYPE_1);
            messagePushDTO.setOrderStatus(alarms.get(0).getOrderStatus());
            messagePushDTO.setAlarmTime(alarms.get(0).getAlarmTime());
            messagePushDTO.setAppVerCode(alarms.get(0).getAppVerCode());
            messagePushDTO.setIndustryCode(alarms.get(0).getIndustryCode());
            messagePushDTO.setFlowTemplate(alarms.get(0).getFlowTemplate());
            messagePushDTO.setWarningSource(alarms.get(0).getWarningSource());
            messagePushDTO.setCityId(alarms.get(0).getCityCode());
            messagePushDTO.setCountyId(alarms.get(0).getCountyCode());
            messagePushDTO.setProvinceId(alarms.get(0).getProvinceCode());
            messagePushDTO.setDeviceCode(alarms.get(0).getDeviceCode());
            messagePushDTO.setRealLinkType(alarms.get(0).getRealLinkType());
            messagePushDTO.setGridId(alarms.get(0).getGridId());
            messagePushDTO.setEnableAcceptanceMode(alarms.get(0).getEnableAcceptanceMode());
            receiveRepository.messagePush(messagePushDTO);

        } catch (Exception e) {
            log.error("人工上报异常: ", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
        return Result.success(warningOrderId);
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
        boolean openThirdPartyAlarm = thirdPartyAlarmService.isOpenThirdPartyAlarm(receiveAlarmVO.get(0).getTenantId(), receiveAlarmVO.get(0).getIndustryCode());
        //第三方告警配置表中 该租户是否有第三方告警数据
        //boolean tenantThirdPartyAlarm = thirdPartyAlarmService.tenantThirdPartyAlarm(receiveAlarmVO.get(0).getTenantId(),receiveAlarmVO.get(0).getIndustryCode());
//        List<ThirdAlarmVo> alarmList = thirdPartyAlarmService.details(receiveAlarmVO.get(0).getTenantId(),receiveAlarmVO.get(0).getIndustryCode());
        List<ThirdPartyAlarmVo> alarmList = thirdPartyAlarmMapper.details(receiveAlarmVO.get(0).getTenantId(), receiveAlarmVO.get(0).getIndustryCode());
        if ("11".equals(receiveAlarmVO.get(0).getWarningSource())) {
            if (openThirdPartyAlarm) {
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
                        if (receiveAlarmVO.size() > Integer.valueOf(count)) {
                            return Result.fail("一次性上传报文数量超过配置峰值");
                        }
                        String finalTenantName = tenantName;
                        ThreadParallelUtil.run(receiveAlarmVO, r -> {
                            long startTime = System.currentTimeMillis(); // 记录任务开始时间
                            r.setTenantName(finalTenantName);
                            addAlarmOrder(r);
                            long endTime = System.currentTimeMillis(); // 记录任务结束时间
                            long duration = endTime - startTime; // 计算任务处理时间
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
        RedisUtil.set(cachePtKey, receiveAlarmVO.get(0).getAlarmTime(), Long.valueOf(time), RedisDBType.PRIMARY);
        return Result.success("接受告警成功");
    }

    /**
     * 插入环节/受理超期提醒表
     *
     * @param alarms
     */
    @Override
    public void saveOverdueRemindData(List<ReceiveAlarmVO> alarms) {
        try {
            log.info("saveOverdueRemindData-插入环节/受理超期提醒表-req:{}", JSON.toJSONString(alarms));
            List<OverdueRemindDto> overdueRemindDtoList = new ArrayList<>();
            for (ReceiveAlarmVO vo : alarms) {
                if (!StringUtils.isEmpty(vo.getFlowAttrId())) {
                    //查询该流程环节配置的所有超期提醒
                    log.info("查询该流程环节配置的所有超期提醒req:{}", vo.getFlowAttrId());
                    List<FlowOverdueRemind> flowOverdueRemindList = flowLinkService.getFlowOverdueRemindByAttrId(vo.getFlowAttrId());
                    //如果不是待受理环节，只需要查询环节超期推送相关信息
                    if (!Constants.ONE.equals(vo.getEnableAcceptanceMode())) {
                        flowOverdueRemindList = flowOverdueRemindList.stream().filter(item -> Objects.equals(Constants.ZERO, item.getOverdueRemindSign())).collect(Collectors.toList());
                    } else {
                        flowOverdueRemindList = flowOverdueRemindList.stream().filter(item -> Objects.equals(Constants.ONE, item.getOverdueRemindSign())).collect(Collectors.toList());
                    }
                    log.info("查询该流程环节配置的所有超期提醒resp:{}", JSON.toJSONString(flowOverdueRemindList));
                    for (FlowOverdueRemind overdueRemind : flowOverdueRemindList) {
                        if (!StringUtils.isEmpty(overdueRemind.getTimeLimitRemind())) {
                            OverdueRemindDto overdueRemindDto = new OverdueRemindDto();
                            String timeLimit = "";
                            //环节超期提醒
                            if (Constants.ZERO.equals(overdueRemind.getOverdueRemindSign()) && !StringUtils.isEmpty(vo.getTimeLimit())) {
                                timeLimit = String.valueOf(Integer.parseInt(vo.getTimeLimit()) + Integer.parseInt(overdueRemind.getTimeLimitRemind()));
                                overdueRemindDto.setOverdueRemindSign(Constants.ZERO);
                            }
                            //受理超期提醒
                            if (Constants.ONE.equals(overdueRemind.getOverdueRemindSign()) && Constants.ONE.equals(vo.getEnableAcceptanceMode()) && !StringUtils.isEmpty(vo.getTimeLimitAcceptance())) {
                                timeLimit = String.valueOf(Integer.parseInt(vo.getTimeLimitAcceptance()) + Integer.parseInt(overdueRemind.getTimeLimitRemind()));
                                overdueRemindDto.setOverdueRemindSign(Constants.ONE);
                            }
                            String remindTime = DateUtils.getExceedTime(new Date(), timeLimit);
                            BeanUtil.copyProperties(vo, overdueRemindDto);
                            overdueRemindDto.setAlarmTime(vo.getAlarmTime());
                            overdueRemindDto.setRemindTime(remindTime);
                            overdueRemindDto.setRemindProcessId(overdueRemind.getTimeLimitRemindName());
                            overdueRemindDto.setCreateTime(new Date());
                            overdueRemindDtoList.add(overdueRemindDto);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(overdueRemindDtoList)) {
                //插入超期提醒表
                overdueRemindMapper.insertOverdueRemindList(overdueRemindDtoList);
            }
        } catch (Exception e) {
            log.error("saveOverdueRemindData插入超期表异常:", e);
        }
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
     * 获取告警事件导出数据
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", typeValueColumn = "dataTable.warning_type_id")
    public List<WarningOrderExport> exportWarningOrderList(AlarmOrderDTO dto) {

        dto.setExport(true);
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
        this.handleOrderReq(dto, flowLinkIds);
        String userId = sysUser.getUserId();
        dto.setUserId(userId);
        //当前用户组织ID与上级所有组织ID
        dto.setOrgIds(CommonUtils.getOrgIds(loginUser.getUser().getDeptId()));
        List<WarningOrderListVO> resultList = new ArrayList<>();
        dto.setSysUser(sysUser);
        // 查询开始结束时间
        Date startDate = dto.getAlarmTimeStart();
        Date endDate = dto.getAlarmTimeEnd();
        //处理传入的组织条件
        handleOrgIdList(dto);
        //跨库获取页面查询设备信息关联数据
        List<AlarmOrderByDeviceDTO> devList = deviceRepository.queryAlarmOrderByDevice(dto);
        dto.setDevList(devList);
        //告警类型标签
        if (!CollectionUtils.isEmpty(dto.getTagIdList())) {
            List<AlarmTagsTypeRelDTO> alarmTagsTypeRelDTOList = sysAlarmTagsRepository.getAlarmTypeByIds(dto.getTagIdList(), loginUser.getUser().getTenantId());
            if (!CollectionUtils.isEmpty(alarmTagsTypeRelDTOList)) {
                List<String> warningTypeIdList = new ArrayList<>();
                alarmTagsTypeRelDTOList.forEach(alarmTagsTypeRelDTO -> {
                    if (!StringUtils.isEmpty(alarmTagsTypeRelDTO.getTypeValue())) {
                        warningTypeIdList.add(alarmTagsTypeRelDTO.getTypeValue());
                    }
                });
                if (!CollectionUtils.isEmpty(dto.getWarningTypeIdList())) {
                    warningTypeIdList.addAll(dto.getWarningTypeIdList());
                }
                dto.setWarningTypeIdList(warningTypeIdList);
            } else {
                return new ArrayList<>();
            }
        }
        boolean stop = false;
        // 从最后一个表开始向前获取
        while (!stop) {
            //正序从开始获取
            String tableName = null;
            String flowTableName = null;
            if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                tableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_TEBLE);
                flowTableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_PROCESS_TEBLE);
            } else {
                tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
                flowTableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_PROCESS_TEBLE);
            }
            dto.setTableName(tableName);
            dto.setFlowTableName(flowTableName);
            List<WarningOrderListVO> queryResult = getLocalizationFlag() ? warningOrderMapper.queryAlarmOrderLocal(dto) : warningOrderMapper.queryAlarmOrder(dto);
            for (WarningOrderListVO orderVo : queryResult) {
                orderVo.setHoriAzimuthAngle(AngleUtils.transformAngle(orderVo.getHoriAzimuthAngle()));
            }
            resultList.addAll(queryResult);
            stop = DateUtils.sameMouth(startDate, endDate);
            if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                // 转到下月
                String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                startDate = DateUtils.stringToDateYM(nextMonth);
            } else {
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }
        }
        this.resultHandle(resultList, dto, userId, flowLinkIds);

        List<WarningOrderExport> exportList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultList)) {
            for (WarningOrderListVO m : resultList) {
                WarningOrderExport warningOrderExport = new WarningOrderExport();
                BeanUtils.copyProperties(m, warningOrderExport);
                if (!StringUtils.isEmpty(warningOrderExport.getJudgeResult())) {
                    if (Constants.JUDGE_RESULT_0.equals(warningOrderExport.getJudgeResult())) {
                        warningOrderExport.setJudgeResultName(Constants.FALSE_WARING);
                    } else if (Constants.JUDGE_RESULT_1.equals(warningOrderExport.getJudgeResult())) {
                        warningOrderExport.setJudgeResultName(Constants.TRUE_WARING);
                    } else if (Constants.JUDGE_RESULT_2.equals(warningOrderExport.getJudgeResult())) {
                        warningOrderExport.setJudgeResultName(Constants.REPEAT_WARING);
                    }
                }

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

                warningOrderExport.setFileImgUrlSrc(m.getImgUrl());
                warningOrderExport.setIsSecondJudge(getAlarmSecondJudgeNameByCode(warningOrderExport.getIsSecondJudge()));
                warningOrderExport.setSecondJudgeResult(getAlarmSecondJudgeResultNameByCode(warningOrderExport.getSecondJudgeResult()));
                exportList.add(warningOrderExport);
            }
        }
        return exportList;
    }

    public String getAlarmSecondJudgeResultNameByCode(String code) {
        for (EnumUtil.AlarmSecondJudgeResult alarmSecondJudgeResult : EnumUtil.AlarmSecondJudgeResult.values()) {
            if (alarmSecondJudgeResult.getCode().equals(code)) {
                return alarmSecondJudgeResult.getName();
            }
        }
        return "";
    }

    public String getAlarmSecondJudgeNameByCode(String code) {
        for (EnumUtil.AlarmSecondJudge alarmSecondJudge : EnumUtil.AlarmSecondJudge.values()) {
            if (alarmSecondJudge.getCode().equals(code)) {
                return alarmSecondJudge.getName();
            }
        }
        return "";
    }

    /**
     * @Author：hued
     * @Description：获取登录token
     * @Date：2021/12/6 18:27
     */
    public String getToken(ServletRequest request) {
        String token = null;
        // 获取cookie中的token
        HttpServletRequest req = (HttpServletRequest) request;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        return token;
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
            orderFlowRecordMapper.createNewTable(TableConstant.WARN_ORDER_PROCESS_TEBLE + newYM, TableConstant.WARN_ORDER_PROCESS_TEBLE + oldYM);
            try {
                interceptMongoMapper.mongoCreateTable(TableConstant.WARN_ALARM_INTERCEPT + "_" + newYM, MongoDBType.PRIMARY);
                //回收站表创建索引
                this.checkIndexsExist(TableConstant.WARN_ALARM_INTERCEPT + "_" + newYM, newYM);
                operateMongoMapper.mongoCreateTable(TableConstant.WARN_ALARM_OPERATE + "_" + newYM, MongoDBType.PRIMARY);
                //运营研判表创建索引
                this.checkIndexsExist(TableConstant.WARN_ALARM_OPERATE + "_" + newYM, newYM);
                operateMongoMapper.mongoCreateTable(TableConstant.WARN_LOCAL_ALARM_OPERATE + "_" + newYM, MongoDBType.PRIMARY);
                //属地运营研判表创建索引
                this.checkIndexsExist(TableConstant.WARN_LOCAL_ALARM_OPERATE + "_" + newYM, newYM);
            } catch (Exception e) {
                log.info("创建回收站运营研判mongo表失败:", e);
            }
        }
    }

    /**
     * 检查索引是否存在
     *
     * @param tableName
     * @param dateYM
     */
    private void checkIndexsExist(String tableName, String dateYM) {
        try {
            //先检查索引是否已存在
            boolean listIndexs = checkListIndexs(tableName, MongoDBType.PRIMARY);
            if (!listIndexs) {
                //需要创建索引的集合
                List<IndexModel> indexModels = getIndexModels(tableName, dateYM);
                log.info("mongo表创建索引,获取索引列集合 {}", JSONObject.toJSONString(indexModels));
                interceptMongoMapper.mongoCreateIndexs(tableName, MongoDBType.PRIMARY, indexModels);
            }
        } catch (Exception e) {
            log.error("WarningOrderRepositoryImpl ===> checkIndexsExist 创建索引失败！", e.getMessage());
            return;
        }
    }

    /**
     * 查询索引列
     *
     * @param tableName
     * @param primary
     * @return
     */
    private boolean checkListIndexs(String tableName, MongoDBType primary) {
        boolean success = false;
        try {
            ListIndexesIterable<Document> indexList = interceptMongoMapper.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    if (keyDocument.containsKey(Constants.INDEXFIELD_WARNING_ORDER_ID)) {
                        success = true;
                    }
                }
            }
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    /**
     * 获取索引列
     *
     * @param table
     * @return
     */
    private List<IndexModel> getIndexModels(String table, String date) {
        List<IndexModel> indexModels = new ArrayList<>();
        String[] str = new String[0];
        if (table.equals(TableConstant.WARN_ALARM_OPERATE + "_" + date)) {
            //运营研判表索引
            str = new String[]{Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_TENANT_ID, Constants.INDEXFIELD_PROVINCE_ID,
                    Constants.INDEXFIELD_CITY_ID, Constants.INDEXFIELD_COUNTY_ID, Constants.INDEXFIELD_CREATE_USER, Constants.INDEXFIELD_WARNING_TYPE_ID,
                    Constants.INDEXFIELD_EMERGENCY_LEVEL, Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_DUPLICATE_ORDER_ID,
                    Constants.INDEX_DEL_TIME_RESULT, Constants.INDEX_DEL_ALARMTIME_TYPE, Constants.INDEX_TIME_RESULT_SOURCE, Constants.INDEX_TIME_TENANTID_RESULT, Constants.INDEX_TIME_DEVICE_RESULT,
                    Constants.INDEX_TIME_TYPE_RESULT, Constants.INDEX_TIME_PROVINCEID_CITYID_COUNTYID_RESULT};
        } else if (table.equals(TableConstant.WARN_ALARM_INTERCEPT + "_" + date)) {
            //告警拦截表索引
            str = new String[]{Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_PROVINCE_CODE, Constants.INDEXFIELD_TENANT_ID,
                    Constants.INDEXFIELD_TASK_ID, Constants.INDEXFIELD_CITY_CODE, Constants.INDEXFIELD_COUNTY_CODE, Constants.INDEXFIELD_PROVINCE_CITY,
                    Constants.INDEXFIELD_WARNING_TYPE_ID, Constants.INDEXFIELD_LONGITUDE_LATITUDE, Constants.INDEXFIELD_DEVICE_CODE, Constants.INDEXFIELD_ALARM_BODY,
                    Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_CREATE_TIME, Constants.INDEX_TENANTID_TIME_FLAG_RESULT, Constants.INDEX_TIME_FLAG_RESULT,
                    Constants.INDEX_TIME_RESULT, Constants.INDEX_TIME_RESULT_SOURCE, Constants.INDEX_TIME_TYPE_RESULT, Constants.INDEX_TIME_DEVICE_RESULT,
                    Constants.INDEX_TIME_PROVINCE_CITY_COUNTY_RESULT, Constants.INDEX_CONFIDENCE_LEVEL_MAX, Constants.INDEX_INTERCEPT, Constants.INDEX_ENLARGE_FLAG};
        }
        BasicDBObject indexObj;
        for (String index : str) {
            IndexOptions aIndexOptions;
            aIndexOptions = new IndexOptions().background(true).name(index);
            indexObj = new BasicDBObject();
            if (index.contains(Constants.COMMA)) {
                String[] str1 = index.split(Constants.COMMA);
                for (String index1 : str1) {
                    indexObj.put(index1, Constants.INDEXASC_ONE);
                }
            } else {
                indexObj.put(index, Constants.INDEXASC_ONE);
            }
            indexModels.add(new IndexModel(indexObj, aIndexOptions));
        }
        return indexModels;
    }

    /**
     * 根据告警时间按创建分月表
     *
     * @param alarmTimeList
     */
    @Override
    public void createNewTable(List<String> alarmTimeList) {
        for (String alarmTime : alarmTimeList) {
            Date date = DateUtils.stringToDateYMDHMS(alarmTime);
            String oldYM = DateUtils.dateToStringYM(date);
            String newYM = DateUtils.dateToStringYM(DateUtils.getDateAfter(date, 1));
            // 如果新年月和旧年月不同，则创建表
            if (!(oldYM).equalsIgnoreCase(newYM)) {
                warningOrderMapper.createNewTable(TableConstant.WARN_ORDER_TEBLE + newYM, TableConstant.WARN_ORDER_TEBLE + oldYM);
                orderFlowRecordMapper.createNewTable(TableConstant.WARN_ORDER_PROCESS_TEBLE + newYM, TableConstant.WARN_ORDER_PROCESS_TEBLE + oldYM);
                // 此表已废弃，物联告警相关信息已存入告警表透传字段内
                //warningSensorMonitorMapper.createNewTable(TableConstant.WARN_ALARM_SENSOR + newYM, TableConstant.WARN_ALARM_SENSOR + oldYM);
            }
        }
    }

    /**
     * 创建流程
     * add by hued 20211103
     */
    @Override
    public List<Map> createRecord(List<ReceiveAlarmVO> customers) {
        log.info("创建流程信息入参：{}", JSON.toJSONString(customers));
        List<Map> customerList = new ArrayList<>();
        String yearMonth = DateUtils.date2Str(new Date(), "yyyyMM");
        List<AlarmOrderRecordVO> recordList = new ArrayList<>();
        AlarmOrderRecordDTO alarmOrderRecordVODB = new AlarmOrderRecordDTO();
        for (ReceiveAlarmVO customer : customers) {
            String tenantId = customer.getTenantId();
            String warningOrderId = customer.getWarningOrderId();
            String alarmType = String.valueOf(customer.getAlarmType());
            String warningSource = customer.getWarningSource();
            String alarmTime = customer.getAlarmTime();
            String appVerCode = customer.getAppVerCode();
            String industryCode = customer.getIndustryCode();
            //是否组装过流程环节人信息
            boolean buildProcessFlag = false;
            // 如果没有流程信息获取流程信息，创建流程， 如果有流程信息，不调用getAlarmFlowConfig接口
            if (StringUtils.isEmpty(customer.getFlowId())) {
                // 调用流程接口获取流程信息
                AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
                alarmFlowConfigModel.setTenantId(tenantId);
                alarmFlowConfigModel.setWarningSource(warningSource);
                alarmFlowConfigModel.setAlarmCode(alarmType);
                VmFlowTemplateLinkDto startFlowLink = flowLinkService.getStartFlowLink(appVerCode, industryCode, tenantId, warningSource, alarmType);
                this.setLinkInfo(startFlowLink, customer);
                String userType = startFlowLink.getUserType();
                if (StringUtils.isEmpty(userType)) {
                    userType = customer.getUserType();
                }
                // 组装环节处理人 processDTOList
                //this.dealProcessUserAndRole(customer, startFlowLink);
                this.getProcessInfoNew(userType, customer, startFlowLink.getDataFilterPermiss());
                buildProcessFlag = true;
            }
            String flowId = customer.getFlowId();
            String orderStatus = customer.getOrderStatus();
            String flowName = customer.getFlowName();
            String orgId = customer.getOrgId();
            String linkName = customer.getLinkName();
            String userId = customer.getUserId();
            String userName = customer.getUserName();
            String flowNodeTime = customer.getFlowNodeTime();
            String linkId = customer.getLinkId();

            alarmOrderRecordVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_PROCESS_TEBLE));

            Map customerMap = new HashMap();
            customerMap.put(Constants.PARAM_TENANT_ID, tenantId);
            log.info("保存事件，租户id： {}", tenantId);
            // 2021-06-21 start
            // 流程处理参数
            /*String processId = null;
            String processName = null;*/
            String groupId = orgId;
            String processOrderId = null;
            String roleId = customer.getRoleId();
            String userType = customer.getUserType();
            String msgNotifyType = customer.getMsgNotifyType();
            String notifyAdminFlag = customer.getNotifyAdminFlag();

            /*StringBuffer processIds = new StringBuffer();
            StringBuffer processNames = new StringBuffer();
            StringBuffer groupIds = new StringBuffer();*/

            List<ProcessDTO> processDTOList = customer.getProcessDTOList();
            // 流程处理人信息
            if (!buildProcessFlag && CollectionUtils.isEmpty(processDTOList)) {
                processDTOList = new ArrayList<>();
                /*Long attrId = null;
                String processRoleId = null;*/
                String dataFilterPermiss = null;
                VmFlowLinkAttrDto vmFlowLinkAttrDto = flowLinkService.getFlowLinkAttrByAlarmFlowId(tenantId, null, Long.parseLong(flowId), Integer.parseInt(linkId));
                ////vmFlowLinkDto 为空说明flowId是模板id而非告警流程实例id
                if (vmFlowLinkAttrDto == null) {
                    VmFlowTemplateLinkDto flowTemplateLinkDto = flowLinkService.getFlowTemplateLink(Long.valueOf(flowId), null, null, Integer.parseInt(linkId), industryCode, appVerCode);
                    userType = flowTemplateLinkDto.getUserType();
                    dataFilterPermiss = flowTemplateLinkDto.getDataFilterPermiss();
                    /*attrId = flowTemplateLinkDto.getAttrId();
                    processRoleId = flowTemplateLinkDto.getRoleId();*/
                } else {
                    userType = vmFlowLinkAttrDto.getUserType();
                    dataFilterPermiss = vmFlowLinkAttrDto.getDataFilterPermiss();
                    /*attrId = vmFlowLinkAttrDto.getAttrId();
                    processRoleId = "roleId";*/
                }
                //this.getProcessInfo(userType, attrId, tenantId, processRoleId, processDTOList);
                this.getProcessInfoNew(userType, customer, dataFilterPermiss);
            }

           /* for (ProcessDTO processDTO : processDTOList) {
                processIds.append(Constants.COMMA).append(processDTO.getProcessId());
                processNames.append(Constants.COMMA).append(processDTO.getProcessName());
                if (!StringUtils.isEmpty(processDTO.getGroupId()) && !String.valueOf(groupIds).contains(processDTO.getGroupId())) {
                    groupIds.append(Constants.COMMA).append(processDTO.getGroupId());
                }
            }

            if (processIds.length() > 1) {
                processId = String.valueOf(processIds.append(Constants.COMMA));
            }
            if (processNames.length() > 1) {
                processName = String.valueOf(processNames.append(Constants.COMMA));
            }
            if (groupIds.length() > 1) {
                groupId = String.valueOf(groupIds.append(Constants.COMMA));
            }*/

            //组装流程记录开始环节信息
            recordList.add(this.buildStartAlarmOrderRecord(tenantId, userId, warningOrderId, yearMonth, linkName, userName, flowNodeTime, customer.getRealLinkType(), customer.getFlowLinkType()));
            //组装流程记录下一环节信息
            recordList.add(this.buildNextAlarmOrderRecord(customer, warningOrderId, flowNodeTime, processOrderId, tenantId, yearMonth, customer.getRealLinkType(), customer.getFlowLinkType(), customer.getEnableAcceptanceMode()));


            customerMap.put(Constants.PARAM_FLOW, flowId);
            customerMap.put(Constants.PARAM_ORDER_STATUS, orderStatus);
            customerMap.put(Constants.PARAM_LINK_ID, linkId);
            customerMap.put(Constants.PARAM_LINK_NAME, flowName);
            customerMap.put(Constants.PARAM_FLOW_NODE_TIME, flowNodeTime);
            /*customerMap.put(Constants.RES_PROCESS_ID, processId);
            customerMap.put(Constants.PARAM_PROCESS_NAME, processName);*/
            customerMap.put(Constants.RES_GROUP_ID, groupId);
            customerMap.put(Constants.PARAM_ROLE_ID, Constants.COMMA + roleId + Constants.COMMA);
            customerMap.put(Constants.PARAM_TIME_LIMIT, customer.getTimeLimit());

            customerMap.put(Constants.PARAM_USER_TYPE, userType);
            customerMap.put(Constants.PARAM_MSG_NOTIFY_TYPE, msgNotifyType);
            customerMap.put(Constants.PARAM_NOTIFY_ADMIN_TYPE, notifyAdminFlag);
            customerMap.put(Constants.PARAM_APP_VER_CODE, appVerCode);
            customerMap.put(Constants.PARAM_INDUSTRY_CODE, industryCode);
            customerMap.put(Constants.PARAM_ALARM_TYPE, alarmType);
            customerMap.put(Constants.PARAM_REAL_LINK_TYPE, customer.getRealLinkType());
            customerList.add(customerMap);
            alarmOrderRecordVODB.setList(recordList);
        }
        if (!CollectionUtils.isEmpty(recordList)) {
            orderFlowRecordMapper.insertAlarmOrderRecordList(alarmOrderRecordVODB);
            //保存上传的带水印附件信息
            this.addWaterMarkFile(customers, recordList);
        }

        return customerList;
    }

    private Result addWaterMarkFile(List<ReceiveAlarmVO> alarmVOList, List<AlarmOrderRecordVO> recordList) {
        try {
            //取出开始环节信息
            AlarmOrderRecordVO recordVO = recordList.stream()
                    .filter(item -> Constants.FIRST_LINK.equals(item.getLinkId()))
                    .findFirst()
                    .orElse(null);
            if (!CollectionUtils.isEmpty(alarmVOList)) {
                for (ReceiveAlarmVO alarmVO : alarmVOList) {
                    List<OrderFileDO> orderFileDTOList = alarmVO.getOrderFileList();
                    if (!CollectionUtils.isEmpty(orderFileDTOList)) {
                        // 添加附件
                        for (OrderFileDO orderFileDO : orderFileDTOList) {
                            orderFileDO.setOrderFileId(
                                    PKUtils.getOrderFileId(alarmVO.getTenantId(), alarmVO.getWarningOrderId().substring(0, 6)));
                            orderFileDO.setCreateBy(sysService.getLoginUserId());
                            orderFileDO.setCreateDate(new Date());
                            orderFileDO.setWarningOrderId(alarmVO.getWarningOrderId());
                            orderFileDO.setOrderFlowId(recordVO.getOrderFlowId());
                            orderFileDO.setLinkId(recordVO.getLinkId());
                            orderFileDO.setProcessId(recordVO.getProcessId());
                            orderFileDO.setProcessName(recordVO.getProcessName());
                            orderFileDO.setDeleteFlag(Constants.DELETE_FLAG);
                            orderFileMapper.insert(orderFileDO);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(Constants.LOG_FILE_INFO + e.getMessage());
            return Result.fail(Constants.LOG_FILE_INFO + e.getMessage());
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 组装默认的开始流程记录信息
     *
     * @param tenantId
     * @param userId
     * @param warningOrderId
     * @param yearMonth
     * @param linkName
     * @param userName
     * @param flowNodeTime
     * @return 流程记录开始环节信息
     */
    private AlarmOrderRecordVO buildStartAlarmOrderRecord(String tenantId, String userId, String warningOrderId, String yearMonth, String linkName, String userName, String flowNodeTime, String realiknType, String flowLinkType) {
        AlarmOrderRecordVO start = new AlarmOrderRecordVO();
        start.setOrderFlowId(PKUtils.getFlowOrderId(tenantId, yearMonth));
        start.setDeleteFlag(Constants.DELETE_FLAG);
        start.setFlowRecordName("");
        start.setIfTimeout(Constants.ZERO);
        start.setLinkId("-1");
        start.setLinkName(linkName);
        start.setProcessId(userId);
        start.setProcessName(userName);
        start.setOrderId(warningOrderId);
        start.setIfCurrentLink(Constants.ZERO);
        start.setFlowNodeTime(DateUtils.string2Date(flowNodeTime));
        start.setFlowEndTime(DateUtils.string2Date(flowNodeTime));
        start.setRealLinkType("-1");
        start.setFlowLinkType(flowLinkType);
        start.setEnableAcceptanceMode(Constants.ZERO);
        return start;
    }

    /**
     * 组装流程环节记录表下一环节信息
     *
     * @param customer
     * @param warningOrderId
     * @param flowNodeTime
     * @param processOrderId
     * @param tenantId
     * @param yearMonth
     * @return
     */
    private AlarmOrderRecordVO buildNextAlarmOrderRecord(ReceiveAlarmVO customer, String warningOrderId, String flowNodeTime, String processOrderId, String tenantId, String yearMonth, String realiknType, String flowLinkType, String enableAcceptanceMode) {
        // 下一环节需要通过接口判断
        AlarmOrderRecordVO next = new AlarmOrderRecordVO();

        next.setDeleteFlag(Constants.DELETE_FLAG);
        next.setFlowRecordName("");
        next.setIfTimeout(Constants.ZERO);
        // 2021-06-21 start
        if (StringUtils.isEmpty(customer.getCurrentLinkId())) {
            //后续考虑删除，下一环节id为必传字段
            next.setLinkId(EnumUtil.FlowNode.PENDING_JUDGE.getCode());
            next.setLinkName(EnumUtil.FlowNode.PENDING_JUDGE.getName());
        } else {
            next.setLinkId(customer.getCurrentLinkId());
            next.setLinkName(customer.getCurrentLinkName());
        }
        // 2021-06-21 end
        next.setOrderId(warningOrderId);
        next.setIfCurrentLink(Constants.ONE);
        if (!StringUtils.isEmpty(customer.getTimeLimit()) && !Constants.PARAM_NULL.equals(customer.getTimeLimit())) {
            next.setTimeLimit(Integer.valueOf(customer.getTimeLimit()));
        }

        next.setFlowNodeTime(DateUtils.string2Date(flowNodeTime));
        next.setProcessOrderId(processOrderId);
        // 2021-06-21 end
        next.setOrderFlowId(PKUtils.getFlowOrderId(tenantId, yearMonth));
        next.setRealLinkType(realiknType);
        next.setFlowLinkType(flowLinkType);
        next.setEnableAcceptanceMode(enableAcceptanceMode);
        //开启受理模式时，下一环节为当前环节
        if (Constants.ONE.equals(enableAcceptanceMode)) {
            next.setNextLinkId(next.getLinkId());
            next.setNextLinkName(next.getLinkName());
        }
        return next;
    }

    /**
     * 组装环节信息，需要适配，有些地方调用设置了信息，这里就不需要重新设置
     *
     * @param startFlowLink
     * @param customer
     */
    private void setLinkInfo(VmFlowTemplateLinkDto startFlowLink, ReceiveAlarmVO customer) {
        customer.setRoleId(startFlowLink.getRoleId());
        customer.setRealLinkType(String.valueOf(startFlowLink.getRealLinkType()));
        customer.setFlowAttrId(startFlowLink.getFlowAttrId());
        if (StringUtils.isEmpty(customer.getFlowId())) {
            Long flowId = null != startFlowLink.getAlarmFlowId() ? startFlowLink.getAlarmFlowId() : startFlowLink.getTemplateId();
            customer.setFlowId(flowId == null ? null : String.valueOf(flowId));
        }
        customer.setFlowTemplate(String.valueOf(startFlowLink.getTemplateId()));
        if (StringUtils.isEmpty(customer.getFlowName())) {
            customer.setFlowName(startFlowLink.getLinkName());
        }
        if (StringUtils.isEmpty(customer.getNotifyAdminFlag())) {
            customer.setNotifyAdminFlag(startFlowLink.getNotifyAdminFlag());
        }
        if (StringUtils.isEmpty(customer.getCurrentLinkId())) {
            customer.setCurrentLinkId(startFlowLink.getCurLink() == null ? null : String.valueOf(startFlowLink.getCurLink()));
        }
        if (StringUtils.isEmpty(customer.getCurrentLinkName())) {
            customer.setCurrentLinkName(startFlowLink.getLinkName());
        }
        if (StringUtils.isEmpty(customer.getLinkId())) {
            customer.setLinkId(startFlowLink.getCurLink() == null ? null : String.valueOf(startFlowLink.getCurLink()));
        }
        if (StringUtils.isEmpty(customer.getOrderStatus())) {
            customer.setOrderStatus(startFlowLink.getCurLink() == null ? null : String.valueOf(startFlowLink.getCurLink()));
        }
        if (StringUtils.isEmpty(customer.getMsgNotifyType())) {
            customer.setMsgNotifyType(startFlowLink.getMsgNotifyType());
        }
        if (StringUtils.isEmpty(customer.getTimeLimit())) {
            customer.setTimeLimit(StringUtils.isEmpty(startFlowLink.getTimeLimit()) ? null : startFlowLink.getTimeLimit());
        }
        if (StringUtils.isEmpty(customer.getTimeLimitAcceptance())) {
            customer.setTimeLimitAcceptance(StringUtils.isEmpty(startFlowLink.getTimeLimitAcceptance()) ? null : startFlowLink.getTimeLimitAcceptance());
        }
        if (StringUtils.isEmpty(customer.getEnableAcceptanceMode())) {
            customer.setEnableAcceptanceMode(startFlowLink.getEnableAcceptanceMode());
        }
        if (StringUtils.isEmpty(customer.getUserType())) {
            if (StringUtils.isEmpty(startFlowLink.getUserType())) {
                customer.setUserType(Constants.USER_TYPE_2);
            } else {
                customer.setUserType(startFlowLink.getUserType());
            }
        }
    }

    /**
     * 处理获取环节处理人信息
     *
     * @param customer
     * @param linkDto
     */
    private void dealProcessUserAndRole(ReceiveAlarmVO customer, VmFlowTemplateLinkDto linkDto) {
        if (customer == null || linkDto == null) {
            return;
        }
        List<ProcessDTO> processDTOList = customer.getProcessDTOList();
        if (!CollectionUtils.isEmpty(processDTOList)) {
            return;
        } else {
            processDTOList = new ArrayList<>();
        }
        String userType = linkDto.getUserType();
        String tenantId = customer.getTenantId();
        Long attrId = linkDto.getAttrId();
        String roleId = linkDto.getRoleId();
        this.getProcessInfo(userType, attrId, tenantId, roleId, processDTOList);
        customer.setProcessDTOList(processDTOList);
    }


    /**
     * 组装环节处理人新
     */
    private void getProcessInfoNew(String userType, ReceiveAlarmVO customer, String dataFilterPermiss) {
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
        if (!StringUtils.isEmpty(userType)) {
            if (userType.contains(Constants.THREE) && !CollectionUtils.isEmpty(gridUserList)) {
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
            if (userType.contains(Constants.THREE) && gridFlag != null) {
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
                for (String role : roleIdList) {
                    if (!StringUtils.isEmpty(role)) {
                        roles.add(Long.parseLong(role));
                    }
                }
                List<SysUser> userRolerList = deviceRepository.getUserInfoByLongRoleIdsAndIndustryCode(roles, customer.getTenantId(), customer.getIndustryCode());
                userIdRoleList = userRolerList.stream().map(CommonFeignEntity::getUserId).collect(Collectors.toList());
            }
            if (!StringUtils.isEmpty(flowUserList)) {
                userIdList = new ArrayList<>(Arrays.asList(flowUserList.split(Constants.COMMA)));
            }
            userIdList.addAll(userIdRoleList);
            userIdList = userIdList.stream().distinct().collect(Collectors.toList());
            //需要过滤数据权限
            if (Constants.ONE.equals(dataFilterPermiss)) {
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
    }


    /**
     * 组装环节处理人信息
     *
     * @param userType
     * @param attrId
     * @param tenantId
     * @param processDTOList
     */
    private void getProcessInfo(String userType, Long attrId, String tenantId, String roleId, List<ProcessDTO> processDTOList) {
        if (Constants.USER_TYPE_1.equals(userType)) {
            List<VmFlowLinkUserDO> linkUserList = vmAlarmFlowConfigMapper.queryFlowLinkUserList(attrId);
            List<String> userIdList = new ArrayList<>();

            for (VmFlowLinkUserDO linkUser : linkUserList) {
                userIdList.add(linkUser.getUserId());
            }
            if (!CollectionUtils.isEmpty(userIdList)) {
                List<SysUser> sysUserList = sysService.queryUsers(userIdList);
                for (SysUser sysUser : sysUserList) {
                    ProcessDTO processDTO = new ProcessDTO();
                    processDTO.setProcessId(sysUser.getUserId());
                    processDTO.setProcessName(sysUser.getUserName());
                    for (VmFlowLinkUserDO linkUser : linkUserList) {
                        if (linkUser.getUserId().equals(sysUser.getUserId())) {
                            processDTO.setGroupId(linkUser.getOrgId());
                            continue;
                        }
                    }
                    processDTOList.add(processDTO);
                }
            }
            //租户告警配置流程配置角色或者没有配置告警流程使用模板方式走流程
        } else if (Constants.USER_TYPE_2.equals(userType)) {
            List<String> roleIdList = null;
            if (Constants.USER_TYPE_2.equals(userType)) {
                List<VmFlowLinkRoleDO> linkRoleList = vmAlarmFlowConfigMapper.queryFlowLinkRoleList(attrId);
                roleIdList = linkRoleList.stream().distinct().map(VmFlowLinkRoleDO::getRoleId).distinct().collect(Collectors.toList());
            }

            if (!CollectionUtils.isEmpty(roleIdList)) {
                List<Long> roles = new ArrayList<>();
                for (String role : roleIdList) {
                    if (!StringUtils.isEmpty(role)) {
                        roles.add(Long.parseLong(role));
                    }
                }
                List<SysUser> sysUserList = deviceRepository.getUserInfoByLongRoleIds(roles, tenantId);
                if (!CollectionUtils.isEmpty(sysUserList)) {
                    for (SysUser sysUser : sysUserList) {
                        ProcessDTO processDTO = new ProcessDTO();
                        processDTO.setProcessId(sysUser.getUserId());
                        processDTO.setProcessName(sysUser.getUserName());
                        processDTOList.add(processDTO);
                    }
                }
            }
        } else if (!StringUtils.isEmpty(roleId)) {
            //拼接默认模板的处理角色用户
            try {
                String[] roleIArr = roleId.split(Constants.COMMA);
                List<String> roletemp = Arrays.asList(roleIArr).stream().filter(item -> !StringUtils.isEmpty(item)).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(roletemp)) {
                    List<Long> roles = new ArrayList<>();
                    for (String role : roletemp) {
                        if (!StringUtils.isEmpty(role)) {
                            roles.add(Long.parseLong(role));
                        }
                    }
                    List<SysUser> sysUserList = deviceRepository.getUserInfoByLongRoleIds(roles, tenantId);
                    if (!CollectionUtils.isEmpty(sysUserList)) {
                        for (SysUser sysUser : sysUserList) {
                            ProcessDTO processDTO = new ProcessDTO();
                            processDTO.setProcessId(sysUser.getUserId());
                            processDTO.setProcessName(sysUser.getUserName());
                            processDTOList.add(processDTO);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("处理默认的角色信息异常：{}", e);
            }
        }

    }

    /**
     * 功能描述:
     * 回填告警表当前环节相关字段
     *
     * @param: [receiveAlarmVO]
     * @return:
     * @auther: hued
     * @date: 2021年11月24日14:37:50
     */
    @Override
    public void setWarningLinkInfo(List<ReceiveAlarmVO> alarms, List<Map> recordList) {
        if (!CollectionUtils.isEmpty(recordList)) {
            alarms = alarms.stream().map(item -> {
                recordList.stream().filter(record -> Objects.equals(item.getTenantId(), record.get(Constants.PARAM_TENANT_ID))
                        && Objects.equals(item.getAlarmType(), record.get(Constants.PARAM_ALARM_TYPE)) && Objects.equals(item.getIndustryCode(), record.get(Constants.PARAM_INDUSTRY_CODE))).forEach(record -> {
                    item.setLinkId((String) record.get(Constants.PARAM_LINK_ID));
                    item.setLinkName((String) record.get(Constants.PARAM_LINK_NAME));
                    item.setFlowNodeTime((String) record.get(Constants.PARAM_FLOW_NODE_TIME));
                    /*item.setProcessId((String) record.get(Constants.RES_PROCESS_ID));
                    item.setProcessName((String) record.get(Constants.PARAM_PROCESS_NAME));*/
                    item.setGroupId((String) record.get(Constants.RES_GROUP_ID));
                    //item.setFlowTemplate((String) (record.get(Constants.PARAM_FLOW_TEMPLATE)));
                    item.setFlowId((String) record.get(Constants.PARAM_FLOW));
                    if (!StringUtils.isEmpty(record.get(Constants.PARAM_TIME_LIMIT))) {
                        item.setTimeLimit(String.valueOf(record.get(Constants.PARAM_TIME_LIMIT)));
                    }
                    item.setOrderStatus(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)));
                    if (!StringUtils.isEmpty(record.get(Constants.PARAM_ROLE_ID))) {
                        item.setRoleId(String.valueOf(record.get(Constants.PARAM_ROLE_ID)));
                    }
                    item.setUserType(String.valueOf(record.get(Constants.PARAM_USER_TYPE)));
                    item.setMsgNotifyType(String.valueOf(record.get(Constants.PARAM_MSG_NOTIFY_TYPE)));
                    item.setNotifyAdminFlag(String.valueOf(record.get(Constants.PARAM_NOTIFY_ADMIN_TYPE)));
                    item.setRealLinkType(String.valueOf(record.get(Constants.PARAM_REAL_LINK_TYPE)));
                });
                return item;
            }).collect(Collectors.toList());
        }
        //插入环节超期/受理超期提醒表
        this.saveOverdueRemindData(alarms);
    }

    /**
     * 通过部门ID获取部门下处置角色的人员，没有数据或者查出多条，返回null
     *
     * @param orgId
     * @return
     */
    public String getUserByOrg(String orgId) {
        UsersVO usersVO = new UsersVO();
        usersVO.setOrgId(orgId);
        if (StringUtils.isEmpty(orgId)) {
            return null;
        }
        List<UsersVO> list = warningOrderMapper.queryUserByOrgId(usersVO);
        if (!CollectionUtils.isEmpty(list) && list.size() == 1) {
            // 如果只有一条，直接返回处理人
            return (String) list.get(0).getUserId();
        }
        return null;
    }

    /**
     * 获取流程节点处理时限
     *
     * @param alarmClassTimeVO
     * @return
     * @since 2021-06-21
     */
    public Integer getTimeLimit(AlarmClassTimeVO alarmClassTimeVO) {
        Integer timeLimit = null;
        List<Map<String, Object>> result = warningOrderMapper.getTimeLimitByInfo(alarmClassTimeVO);
        if (!CollectionUtils.isEmpty(result)) {
            Map timeMap = result.get(0);
            timeLimit = (Integer) timeMap.get(Constants.PARAM_TIME_LIMIT);
        }
        return timeLimit;
    }

    @Override
    public String getOrderStatusSql(String userId, Long[] roleIds) {
        if (null == roleIds) {
            return null;
        }

        StringBuilder sql = new StringBuilder();

        sql.append(" AND (dataTable.order_status in (");
        int i = 0;
        for (Long roleId : roleIds) {
            sql.append("'").append(EnumUtil.OrderStatusByRole.getName(roleId)).append("'");
            i++;
            if (i < roleIds.length) {
                sql.append(Constants.COMMA);
            }
        }
        sql.append(")");
        if (CommonUtils.ifHandleRole(roleIds)) {
            // 处置角色单独拼装待处置状态并且指派给自己的告警
            sql.append(" OR (dataTable.order_status = '4' OR dataTable.process_id = '").append(userId).append("') ");
        }
        sql.append(")");
        return sql.toString();
    }

    /**
     * 查询用户代办事项数量
     *
     * @param req
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code")
    public Result getTodoNum(AppOrderListByUserDTO req) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || StringUtils.isEmpty(loginUser.getUser())) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail("无法获取当前登录用户信息，请重新登陆");
        }
        SysUser sysUser = loginUser.getUser();
        if (req.getUserId() == null || "".equals(req.getUserId())) {
            req.setUserId(sysUser.getUserId());
        }
        if (req.getTenantId() == null || "".equals(req.getTenantId())) {
            req.setTenantId(sysUser.getTenantId());
        }
        if (req.getProvinceId() == null || "".equals(req.getProvinceId())) {
            req.setProvinceId("");
        }
        if (req.getCityId() == null || "".equals(req.getCityId())) {
            req.setCityId("");
        }
        if (req.getCountyId() == null || "".equals(req.getCountyId())) {
            req.setCountyId("");
        }
        if (req.getOrderStatus() == null || "".equals(req.getOrderStatus())) {
            req.setOrderStatus("");
        }
        if (req.getImportanceLevel() == null || "".equals(req.getImportanceLevel())) {
            req.setImportanceLevel("");
        }
        if (req.getKeyword() == null || "".equals(req.getKeyword())) {
            req.setKeyword("");
        }

        //来源：0铁塔 2租户
        req.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()) || Constants.VALID_STATE.equals(loginUser.getUser().getUserType()));
        req.setOrgIds(CommonUtils.getOrgIds(loginUser.getUser().getDeptId()));
        //告警类型标签
        if (!CollectionUtils.isEmpty(req.getTagIdList())) {
            List<AlarmTagsTypeRelDTO> alarmTagsTypeRelDTOList = sysAlarmTagsRepository.getAlarmTypeByIds(req.getTagIdList(), loginUser.getUser().getTenantId());
            if (!CollectionUtils.isEmpty(alarmTagsTypeRelDTOList)) {
                List<String> warningTypeIdList = new ArrayList<>();
                alarmTagsTypeRelDTOList.forEach(alarmTagsTypeRelDTO -> {
                    if (!StringUtils.isEmpty(alarmTagsTypeRelDTO.getTypeValue())) {
                        warningTypeIdList.add(alarmTagsTypeRelDTO.getTypeValue());
                    }
                });
                if (!CollectionUtils.isEmpty(req.getWarningTypeIdList())) {
                    warningTypeIdList.addAll(req.getWarningTypeIdList());
                }
                req.setWarningTypeIdList(warningTypeIdList);
            }
        }

        // 总待办数
        int allCount = 0;
        //查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        /*if (StringUtils.isEmpty(req.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = DateUtil.string2Date(req.getAlarmTimeEnd());
        }
        if (StringUtils.isEmpty(req.getAlarmTimeStart())) {
            startDate = DateUtil.stringToDateYM(TableUtil.getMinTableDate());
        } else {
            startDate = DateUtil.string2Date(req.getAlarmTimeStart());
        }*/
        if (Objects.isNull(req.getAlarmTimeStart())) {
            startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDate());
        } else {
            startDate = DateUtils.stringToDateYM(req.getAlarmTimeStart());
            int sd = Integer.parseInt(DateUtils.dateToStringYM(startDate));
            if (sd < Integer.parseInt(TableUtil.getMinTableDate())) {
                startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDate());
            }
        }
        if (Objects.isNull(req.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = DateUtils.stringToDateYMDHMS(req.getAlarmTimeEnd());
            int se = Integer.parseInt(DateUtils.dateToStringYM(endDate));
            if (se < Integer.parseInt(TableUtil.getMinTableDate()) || se > Integer.parseInt(TableUtil.getMinTableDate()) + 5) {
                endDate = new Date();
            }
        }


        // 封装查询告警待办参数
        OrderListDTO orderListReq = new OrderListDTO();
        BeanUtils.copyProperties(req, orderListReq);
//        //不查询完结状态告警
        /*List<String> orderStatuses = new ArrayList<>();
        orderStatuses.add(EnumUtil.FlowNode.END.getCode());
        orderStatuses.add(EnumUtil.FlowNode.DUPLICATION.getCode());
        orderStatuses.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
        orderListReq.setOrderStatuses(orderStatuses);*/
        orderListReq.setAlarmTimeStart(DateUtils.dateToStringNoMS(startDate));
        orderListReq.setAlarmTimeEnd(DateUtils.dateToStringNoMS(endDate));
//        if (!req.isAdmin()) {
//            // 不是管理员，需要按照角色查询对应的待办告警
//            orderListReq.setOrderStatusSql(getOrderStatusSql(req.getUserId(), sysUser.getRoleIds()));
//        }
        //获取角色
        Long[] roleIds = loginUser.getUser().getRoleIds();
        List<String> roleIdList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleIdList.add(String.valueOf(roleId));
            }
        }
        orderListReq.setRoleIdList(roleIdList);
        //处理传入的组织条件
        if (!CollUtil.isEmpty(orderListReq.getOrgIdList())) {
            List<String> orgIdList = orderListReq.getOrgIdList();
            List<String> deviceCodes = new ArrayList<>();
            List<SysDeptDeviceVO> satelliteList = new ArrayList<>();
            List<SysUser> userList = new ArrayList<>();
            Result<SysDeptDetailVO> deptDeviceDetail = systemFeignClient.deptDeviceDetailBatch(orgIdList);
            if (!ObjectUtil.isNull(deptDeviceDetail) && 200 == deptDeviceDetail.getCode() && !ObjectUtil.isNull(deptDeviceDetail.getData())) {
                SysDeptDetailVO data = deptDeviceDetail.getData();
                List<SysDeptDeviceVO> cameras = data.getCameras();
                List<SysDeptDeviceVO> radars = data.getRadars();
                List<SysDeptDeviceVO> sensors = data.getSensors();
                List<SysDeptDeviceVO> satellites = data.getSatellites();
                //摄像机、雷达、物联设备编码集合
                deviceCodes.addAll(cameras.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
                deviceCodes.addAll(radars.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
                deviceCodes.addAll(sensors.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));

                //卫星区域集合（去重）
                satelliteList.addAll(satellites.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + "-" + o.getCityId() + "-" + o.getCountyId()))), ArrayList::new)));
                if (!CollUtil.isEmpty(satelliteList)) {
                    orderListReq.setSatelliteList(satelliteList);
                }
                if (!CollUtil.isEmpty(deviceCodes)) {
                    orderListReq.setDeviceCodeList(deviceCodes);
                }
            }

            for (String orgId : orgIdList) {
                List<SysUser> sysUsers = sysService.selectUserByDeptId(orgId);
                userList.addAll(sysUsers);
                if (!CollUtil.isEmpty(userList)) {
                    List<String> userIdList = userList.stream().distinct().map(o -> o.getUserId()).collect(Collectors.toList());
                    orderListReq.setUserIdList(userIdList);
                }
            }
        }
        // 待办查询 根据当前用户的角色、id和环节配置表的处理角色和用户关联，取环节配置id和环节id字段做过滤
        //根据用户id和角色id查询流程环节信息
        String flowLinkIds = flowLinkService.getFlowLinkAttrByUser(sysUser, roleIdList);
        req.setUserId(sysUser.getUserId());
        if (!StringUtils.isEmpty(flowLinkIds)) {
            orderListReq.setFlowLinkIds(flowLinkIds);
            orderListReq.setSearchType(Constants.ONE);
        }
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if ((!CollUtil.isEmpty(orderListReq.getOrderStatusList()) && orderListReq.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode())) || (StringUtil.isNotEmpty(orderListReq.getOrderStatus()) && EnumUtil.FlowNode.END.getCode().contains(orderListReq.getOrderStatus()))) {
            List<String> orderStatusList = new ArrayList<String>();
            if ((StringUtil.isNotEmpty(orderListReq.getOrderStatus())) && (orderListReq.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))) {
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            } else {
                orderStatusList = orderListReq.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            orderListReq.setOrderStatusList(orderStatusList);
        }

        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态，OrderStatus由6变为空
        if ((StringUtil.isNotEmpty(req.getOrderStatus())) && (req.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))) {
            orderListReq.setOrderStatus("");
        }
        // 已办查询 根据当前用户的用户id和告警表的user_record（逗号拼接）模糊关联
        // 待办查询 根据当前用户的角色、id和环节配置表的处理角色和用户关联，取环节配置id和环节id字段做过滤
        //待办数量查询：SearchType=1
        if (orderListReq.getSearchType() == null || StringUtils.isEmpty(orderListReq.getSearchType())) {
            orderListReq.setSearchType("1");
        }
        if (Constants.SEARCH_TYPE_1.equals(orderListReq.getSearchType()) && !StringUtils.isEmpty(flowLinkIds)) {
            orderListReq.setFlowLinkIds(flowLinkIds);
            //待办过滤完结数据
            List<String> orderStatuses = new ArrayList<>();
            orderStatuses.add(EnumUtil.FlowNode.END.getCode());
            orderStatuses.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            orderStatuses.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderListReq.setNotInOrderStatus(orderStatuses);
        }

        int count = 0;
        boolean stop = false;
        String tableName = null;
        //从最后一个表开始向前获取
        while (!stop) {
            tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
            orderListReq.setTableName(tableName);
            log.info("待办事件数量查询请求参数：{},入参：{}", System.currentTimeMillis(), JSON.toJSONString(orderListReq));
            count = warningOrderMapper.getOrderCount(orderListReq);

            //累计总数
            allCount += count;
            stop = DateUtils.sameMouth(startDate, endDate);
            //转到上月
            String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
            endDate = DateUtils.stringToDateYM(lastMonth);

        }
        //领导待批示数量
        List<String> leaderStatus = new ArrayList<String>();
        leaderStatus.add(Constants.VALID_STATE);
        orderListReq.setLeaderStatus(leaderStatus);
        orderListReq.setLeaderId(req.getUserId());
        if (!req.isAdmin() && req.getDataScope() != null) {
            String dataScopeTemp = req.getDataScope().substring(req.getDataScope().indexOf("and") + 4);
            orderListReq.setDataScopeTemp(dataScopeTemp);
        }
        // 领导批示表无 sub_scenes：按告警时间展开工单月表
        if (!SubScenesOrderTableHelper.fillWarningOrderTableList(
                orderListReq.getSubSceneIdList(), orderListReq.getAlarmTimeStart(), orderListReq.getAlarmTimeEnd(),
                orderListReq::setWarningOrderTableList)) {
            Map emptyLeader = new HashMap();
            emptyLeader.put(Constants.PARAM_EVENT_NUM, allCount);
            emptyLeader.put(Constants.PARAM_LEANDER_NUM, 0);
            return Result.data(emptyLeader);
        }
        log.info("领导批示待办数量查询请求参数：{},入参：{}", System.currentTimeMillis(), JSON.toJSONString(orderListReq));
        int allLeanderCount = warningOrderMapper.getTodoNumLeader(orderListReq);

        Map data = new HashMap();
        data.put(Constants.PARAM_EVENT_NUM, allCount);
        data.put(Constants.PARAM_LEANDER_NUM, allLeanderCount);

        return Result.data(data);
    }

    /**
     * 获取流程
     * 1、tenantId、warningSource、alarmType
     * 2、tenantId
     * 3、flowId = -1
     *
     * @param alarmFlowConfigModel
     * @return
     */
    @Override
    public FlowModel getAlarmFlowConfig(AlarmFlowConfigModel alarmFlowConfigModel) {
        //tenantId、warningSource、alarmType
        try {
            Result result = alarmFlowConfigService.queryFlowStartLink(alarmFlowConfigModel);
            if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                FlowModel flowModel = (FlowModel) result.getData();
                if (flowModel != null) {
                    return flowModel;
                }
            } else {
                log.info("获取流程失败，调用创建流程接口：{}", JSON.toJSONString(alarmFlowConfigModel));
                FlowModel model = new FlowModel();
                model.setTenantId(alarmFlowConfigModel.getTenantId());
                Result<Map<String, Long>> res = flowTemplateService.genDefaultFlowInfo(model);
                if (ResultCode.SUCCESS.getCode() == res.getCode()) {

                    String key = alarmFlowConfigModel.getTenantId() + "_" + alarmFlowConfigModel.getWarningSource();
                    if (ObjectUtils.isNotEmpty(res.getData().get(key))) {
                        AlarmFlowConfigModel flowConfigModel = new AlarmFlowConfigModel();
                        flowConfigModel.setId(res.getData().get(key));
                        Result<List<FlowModel>> listResult = alarmFlowConfigService.queryFlowLink(flowConfigModel);
                        if (ResultCode.SUCCESS.getCode() == listResult.getCode()) {
                            List<FlowModel> list = listResult.getData();
                            for (FlowModel flowModel : list) {
                                //linkType 1：开始环节 2：中间环节 3：结束环节
                                if (Constants.YES_FLAG.equals(flowModel.getLinkType())) {
                                    return flowModel;
                                }
                            }
                        }
                    }
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        /*
        //tenantId
        AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
        configModel.setTenantId(alarmFlowConfigModel.getTenantId());
        Result res = alarmFlowConfigService.queryFlowStartLink(configModel);
        if (res != null && ResultCode.SUCCESS.getCode() == res.getCode()) {
            FlowModel flowModel = (FlowModel) res.getData();
            if (flowModel != null) {
                return flowModel;
            }
        }
        //flowId = -1
        AlarmFlowConfigModel model = new AlarmFlowConfigModel();
        model.setFlowId(-1L);
        Result rescom = alarmFlowConfigService.queryFlowStartLink(model);
        if (rescom != null && ResultCode.SUCCESS.getCode() == rescom.getCode()) {
            FlowModel flowModel = (FlowModel) rescom.getData();
            if (flowModel != null) {
                return flowModel;
            }
        }*/
        return null;
    }

    /**
     * 获取流程（新）
     * 1、tenantId、warningSource、alarmType
     * 2、tenantId
     * 3、flowId = -1
     *
     * @param alarmFlowConfigModel
     * @return
     */
    @Override
    public FlowModel getAlarmFlowConfigNew(AlarmFlowConfigModel alarmFlowConfigModel) {
        //tenantId、warningSource、alarmType
        try {
            Result result = alarmFlowConfigService.queryFlowStartLinkNew(alarmFlowConfigModel);
            if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                FlowModel flowModel = (FlowModel) result.getData();
                if (flowModel != null) {
                    return flowModel;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 查询流程环节信息
     *
     * @param alarmFlowConfigModel
     * @return
     */
    @Override
    public FlowModel getFlowLink(AlarmFlowConfigModel alarmFlowConfigModel) {
        Result result = alarmFlowConfigService.queryFlowLink(alarmFlowConfigModel);
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
            List<FlowModel> list = (List<FlowModel>) result.getData();
            if (!CollectionUtils.isEmpty(list)) {
                return list.get(0);
            }
        }
        return null;
    }

    /**
     * 告警事件调度
     *
     * @param dto
     * @return
     */
    @Override
    public Result sendAlarmOrder(SendAlarmOrderDTO dto) {
        //兼容未更新版本的行业默认流程
        if (StringUtils.isEmpty(dto.getRealLinkType()) && Constants.REAL_LINK_TYPE_ALL.contains(dto.getLinkId())) {
            dto.setRealLinkType(dto.getLinkId());
        }
        dto.setFlowLinkType(Constants.ZERO);
        if (StringUtils.isEmpty(dto.getRealLinkType())) {
            return Result.fail(Constants.MESS_4);
        }
        //校验告警单表
        //校验工单表：校验工单表中当面工单是否为待派单状态
        String orderId = dto.getOrderId();
        String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);

        //校验工单状态是否异常
        WarnOrderStatusDTO warnOrderStatusDTO = this.checkOrderStatus(orderId, alarmTableName, EnumUtil.FlowNode.PENDING_DISPATCH.getCode(), null);
        if (null == warnOrderStatusDTO) {
            return Result.fail(Constants.MESS_3);
        }
        dto.setOrderNumber(dto.getOrderNumber() + 1);
        dto.setSendType(Constants.VALID_STATE);

        if (!StringUtils.isEmpty(dto.getOperator())) {
            // 指派给个人
            dto.setOrderStatus(dto.getRealLinkType());
        }

        if (!StringUtils.isEmpty(warnOrderStatusDTO.getFlowId())) {
            dto.setFlowId(warnOrderStatusDTO.getFlowId());
        }
        //当前环节ID
        dto.setCurrentLink(warnOrderStatusDTO.getLinkId());
        dto.setTenantId(warnOrderStatusDTO.getTenantId());
        dto.setAlarmBody(warnOrderStatusDTO.getAlarmBody());
        dto.setWarningTypeId(warnOrderStatusDTO.getWarningTypeId());
        dto.setUserRecord(warnOrderStatusDTO.getUserRecord());
        dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
        if (!ObjectUtils.isEmpty(warnOrderStatusDTO.getOrgRecord())) {
            dto.setOrgRecord(String.valueOf(warnOrderStatusDTO.getOrgRecord()));
        }
        //当前是调度操作标志
        //dto.setBeforeDispatchFlag("1");
        return this.updateAlarmOrder(dto);
    }

    @Override
    public Result reassignment(TradeReassignmenOrdertDto dto) {
        if (StringUtils.isEmpty(dto.getRealLinkType())) {
            return Result.fail(Constants.MESS_4);
        }
        SendAlarmOrderDTO orderDTO = new SendAlarmOrderDTO();
        //校验工单表是否有该工单诗句
        String orderId = dto.getWarningOrderId();
        String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);

        //校验工单状态是否异常
        WarnOrderStatusDTO warnOrderStatusDTO = this.checkOrderStatus(orderId, alarmTableName, null, dto.getBeforLink());
        if (null == warnOrderStatusDTO) {
            return Result.fail(Constants.MESS_3);
        }

        if (!StringUtils.isEmpty(warnOrderStatusDTO.getFlowId())) {
            orderDTO.setFlowId(dto.getAlarmFlowId());
        }
        //当前环节ID
        orderDTO.setCurrentLink(dto.getCurLink());
        orderDTO.setTenantId(warnOrderStatusDTO.getTenantId());
        orderDTO.setAlarmBody(warnOrderStatusDTO.getAlarmBody());
        orderDTO.setWarningTypeId(warnOrderStatusDTO.getWarningTypeId());
        orderDTO.setUserRecord(warnOrderStatusDTO.getUserRecord());
        if (!ObjectUtils.isEmpty(warnOrderStatusDTO.getOrgRecord())) {
            orderDTO.setOrgRecord(String.valueOf(warnOrderStatusDTO.getOrgRecord()));
        }
        orderDTO.setRealLinkType(dto.getRealLinkType());
        orderDTO.setLinkName(dto.getLinkName());
        orderDTO.setFlowTemplate(dto.getFlowTemplate());
        orderDTO.setRemark(dto.getRemark());
        orderDTO.setProcessDTOList(dto.getProcessDTOList());
        orderDTO.setBeforLink(dto.getBeforLink());
        orderDTO.setMsgNotifyType(dto.getMsgNotifyType());
        orderDTO.setOrderId(orderId);
        orderDTO.setLinkId(dto.getCurLink());
        orderDTO.setFlowLinkType(Constants.ONE);
        orderDTO.setVoice(dto.getVoice());
        orderDTO.setAddress(dto.getAddress());
        orderDTO.setLatitude(dto.getLatitude());
        orderDTO.setLongitude(dto.getLongitude());
        //默认有流转下个环节
        orderDTO.setIsNextLink(Constants.ONE);
        orderDTO.setGridId(dto.getGridId());
        orderDTO.setGridName(dto.getGridName());
        orderDTO.setHoriAzimuthAngle(dto.getHoriAzimuthAngle());
        orderDTO.setCheckGridFlag(dto.getCheckGridFlag());
        orderDTO.setWarningTypeId(dto.getWarningTypeId());
        orderDTO.setAttrId(dto.getAttrId());
        orderDTO.setEnableAcceptanceMode(dto.getEnableAcceptanceMode());
        orderDTO.setTimeLimitAcceptance(dto.getTimeLimitAcceptance());
        orderDTO.setNextEnableAcceptanceMode(dto.getNextEnableAcceptanceMode());
        orderDTO.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
        if (StringUtils.isEmpty(dto.getTimeLimit())) {
            AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
            configModel.setId(Long.valueOf(dto.getAlarmFlowId()));
            configModel.setLinkId(Long.valueOf(dto.getCurLink()));
            if (StringUtils.isEmpty(dto.getAppVerCode()) || StringUtils.isEmpty(dto.getIndustryCode())) {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                SysUser currentUser = loginUser.getUser();
                configModel.setAppVerCode(currentUser.getAppVerCode());
                configModel.setIndustryCode(currentUser.getIndustryCode());
            } else {
                configModel.setAppVerCode(dto.getAppVerCode());
                configModel.setIndustryCode(dto.getIndustryCode());
            }
            // 获取表名
            String tableName = TableConstant.WARN_ORDER_TEBLE + dto.getWarningOrderId().substring(0, 6);

            // 查询告警详情
            AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
            alarmOrderDTO.setTableName(tableName);
            alarmOrderDTO.setOrderId(dto.getWarningOrderId());

            OrderInfoVO orderInfoVO = warningOrderMapper.getOrderInfo(alarmOrderDTO);
            if (orderInfoVO != null) {
                configModel.setWarningSource(orderInfoVO.getWarningSource());
            }
            configModel.setTemplateId(Long.valueOf(dto.getFlowTemplate()));
            List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
            if (!CollectionUtils.isEmpty(flowModelList)) {
                FlowModel flowLinkAttrModel = flowModelList.get(0);
                if (flowLinkAttrModel.getTimeLimit() != null) {
                    orderDTO.setTimeLimit(flowLinkAttrModel.getTimeLimit().intValue());
                }
            }
        } else {
            orderDTO.setTimeLimit(Integer.valueOf(dto.getTimeLimit()));
        }
        orderDTO.setCityId(dto.getCityId());
        orderDTO.setCountyId(dto.getCountyId());
        orderDTO.setProvinceId(dto.getProvinceId());
        orderDTO.setDevicePitch(dto.getDevicePitch());
        orderDTO.setDeviceHorizontal(dto.getDeviceHorizontal());
        orderDTO.setAlarmReason(dto.getAlarmReason());
        orderDTO.setOrderFileDTOList(dto.getOrderFileDTOList());
        orderDTO.setProcessOrderVOList(dto.getProcessOrderVOList());
        orderDTO.setImportanceLevel(dto.getImportanceLevel());
        return this.updateAlarmOrder(orderDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result accept(TradeAcceptOrderDto dto) {
        try {
            log.info("accept-工单受理入参req:{}", JSON.toJSONString(dto));
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (SecurityUtils.isSwitch()) {
                loginUser = loginUser.getSwitchdLoginUser();
            }
            if (null == loginUser || null == loginUser.getUser()) {
                return Result.fail(Constants.NO_USER_INFO);
            }
            if (StringUtils.isEmpty(dto.getIndustryCode())) {
                dto.setIndustryCode(loginUser.getUser().getIndustryCode());
            }
            if (StringUtils.isEmpty(dto.getAppVerCode())) {
                dto.setAppVerCode(loginUser.getUser().getAppVerCode());
            }
            //校验工单表是否有该工单诗句
            String orderId = dto.getWarningOrderId();
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
            String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + orderId.substring(0, 6);
            // 校验环节表:判断当前工单是否在受理环节
            AlarmOrderRecordVO alarmOrderRecordVO = new AlarmOrderRecordVO();
            alarmOrderRecordVO.setOrderId(orderId);
            alarmOrderRecordVO.setIfCurrentLink(Constants.YES_FLAG);
            alarmOrderRecordVO.setEnableAcceptanceMode(Constants.ONE);
            alarmOrderRecordVO.setTableName(recordTableName);
            AlarmOrderRecordVO orderRecordInfo = orderFlowRecordMapper.getAlarmOrderRecordInfo(alarmOrderRecordVO);
            if (null == orderRecordInfo) {
                return Result.fail(Constants.MESS_3);
            }

            AlarmOrderDTO orderVO = new AlarmOrderDTO();
            orderVO.setOrderId(orderId);
            orderVO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));
            orderVO.setTableName(alarmTableName);
            WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(orderVO);
            SendAlarmOrderDTO alarmOrderVO = new SendAlarmOrderDTO();
            if (warnOrderStatusDTO != null && !StringUtils.isEmpty(warnOrderStatusDTO.getUserRecord())) {
                alarmOrderVO.setUserRecord(warnOrderStatusDTO.getUserRecord() + "," + loginUser.getUser().getUserId());
            } else {
                alarmOrderVO.setUserRecord(loginUser.getUser().getUserId());
            }
            alarmOrderVO.setTableName(alarmTableName);
            alarmOrderVO.setOrderId(orderId);
            alarmOrderVO.setWarningOrderId(orderId);
            //受理状态变更已受理
            alarmOrderVO.setEnableAcceptanceMode(Constants.TWO);
            alarmOrderVO.setProcessId(loginUser.getUser().getUserId());
            alarmOrderVO.setFlowLinkType(Constants.THREE);
            alarmOrderVO.setLinkId(dto.getLinkId());
            alarmOrderVO.setTenantId(loginUser.getUser().getTenantId());
            if (StringUtils.isEmpty(dto.getTimeLimit())) {
                alarmOrderVO.setTimeLimit(null);
            } else {
                alarmOrderVO.setTimeLimit(Integer.valueOf(dto.getTimeLimit()));
            }
            alarmOrderVO.setLinkName(dto.getLinkName());
            alarmOrderVO.setFlowNodeTime(DateUtils.currentDateToString());
            alarmOrderVO.setDevicePitch(dto.getDevicePitch());
            alarmOrderVO.setDeviceHorizontal(dto.getDeviceHorizontal());
            alarmOrderVO.setAlarmReason(dto.getAlarmReason());
            alarmOrderVO.setImportanceLevel(dto.getImportanceLevel());
            alarmOrderVO.setTenantId(loginUser.getUser().getTenantId());
            warningOrderMapper.updateByPrimaryKeySelective(alarmOrderVO);
            //清空该工单所有受理提醒数据
//            overdueRemindMapper.deleteOverdueRemind(alarmOrderVO.getOrderId(),Constants.ONE);
            asyncCommon.deleteOverdueRemind(alarmOrderVO, Constants.ONE);
            //新增该工单环节超期数据
            //查询该流程环节配置的所有超期提醒
            String attrId = flowLinkService.getAttrId(dto.getFlowId(), dto.getLinkId());
            log.info("accept-查询该流程环节配置的环节超期提醒req:{}", attrId);
            if (!StringUtils.isEmpty(attrId)) {
                List<FlowOverdueRemind> flowOverdueRemindList = flowLinkService.getFlowOverdueRemindByAttrId(attrId);
                //如果不是待受理环节，只需要查询环节超期推送相关信息
                flowOverdueRemindList = flowOverdueRemindList.stream().filter(item -> Objects.equals(Constants.ZERO, item.getOverdueRemindSign())).collect(Collectors.toList());
                List<OverdueRemindDto> overdueRemindDtoList = new ArrayList<>();
                for (FlowOverdueRemind overdueRemind : flowOverdueRemindList) {
                    if (!StringUtils.isEmpty(overdueRemind.getTimeLimitRemind())) {
                        OverdueRemindDto overdueRemindDto = new OverdueRemindDto();
                        //环节超期提醒
                        if (!StringUtils.isEmpty(dto.getTimeLimit())) {
                            String timeLimit = String.valueOf(Integer.parseInt(dto.getTimeLimit()) + Integer.parseInt(overdueRemind.getTimeLimitRemind()));
                            overdueRemindDto.setOverdueRemindSign(Constants.ZERO);
                            String remindTime = DateUtils.getExceedTime(new Date(), timeLimit);
                            BeanUtil.copyProperties(dto, overdueRemindDto);
                            overdueRemindDto.setRemindTime(remindTime);
                            overdueRemindDto.setRemindProcessId(overdueRemind.getTimeLimitRemindName());
                            overdueRemindDto.setCreateTime(new Date());
                            overdueRemindDtoList.add(overdueRemindDto);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(overdueRemindDtoList)) {
                    //插入超期提醒表
                    overdueRemindMapper.insertOverdueRemindList(overdueRemindDtoList);
                }
            }
            try {
                List<DelayMessageDto> delayMessageDtoList = new ArrayList<>();
                DelayMessageDto delayMessageDto = new DelayMessageDto();
                delayMessageDto.setOrderId(alarmOrderVO.getOrderId());
                //delayMessageDto.setIndustryCode(alarmOrderVO.getIndustryCode());
                delayMessageDtoList.add(delayMessageDto);
                log.info("流程流转更新延迟消息表入参:" + JSON.toJSONString(delayMessageDtoList));
                orderDelayMessageService.updateOrderDelayList(delayMessageDtoList);
            } catch (Exception e) {
                log.error("流程流转更改未处理消息异常:" + e);
            }

            //如果有加入收藏则跟新收藏的数据
            AlarmOrderDTO orderDto = new AlarmOrderDTO();
            orderDto.setTableName(TableConstant.WAR_ORD_COLL);
            orderDto.setOrderId(alarmOrderVO.getWarningOrderId());
            orderDto.setEnableAcceptanceMode(Constants.TWO);
            List<WarningOrderCollectionDO> warningOrderCollection = warningOrderCollectionMapper.getWarningOrderByParam(orderDto);
            if (!CollUtil.isEmpty(warningOrderCollection)) {
                //未查到数据，不做收藏表修改
                iWarningOrderCollectionRepository.updateWarningOrderCollectionForBigScreen(alarmOrderVO);
            }
            //更新领导批示表工单受理状态
            LeaderAuidtListDTO leaderDto = new LeaderAuidtListDTO();
            leaderDto.setOrderId(alarmOrderVO.getWarningOrderId());
            List<LeaderAuidtListVO> resultList = warningOrderMapper.getLeaderAuditListOnlyByOrderId(leaderDto);
            if (!CollectionUtils.isEmpty(resultList)) {
                OrderLeaderAuditDO auditInfo = new OrderLeaderAuditDO();
                auditInfo.setOrderId(alarmOrderVO.getWarningOrderId());
                //auditInfo.setEnableAcceptanceMode(Constants.TWO);
                if (alarmOrderVO.getAlarmReason() != null) {
                    auditInfo.setAlarmReason(alarmOrderVO.getAlarmReason());
                    orderLeaderAuditMapper.updateLeaderAuditOrderStatus(auditInfo);
                }
            }

            //更新流程环节表当前环节数据
            AlarmOrderRecordVO updateRecored = new AlarmOrderRecordVO();
            updateRecored.setProcessNameList(loginUser.getUser().getUserId());
            updateRecored.setAcceptProcessId(loginUser.getUser().getUserId());
            this.updateRecoredHandle(alarmOrderVO, updateRecored, loginUser);
            //当前环节变更为已受理状态
            updateRecored.setEnableAcceptanceMode(Constants.TWO);
            //更新vm_order_flow_record_t_YYYYMM表对应处理人流程记录
            orderFlowRecordMapper.updateAlarmOrderRecord(updateRecored);

            //插入流程环节表下一环节数据
            AlarmOrderRecordVO insertRecored = new AlarmOrderRecordVO();
            this.insertRecoredHandle(alarmOrderVO, insertRecored);
            insertRecored.setFlowNodeTime(new Date());

            //下已环节为受理完成
            insertRecored.setEnableAcceptanceMode(Constants.THREE);
            AlarmOrderRecordDTO alarmOrderRecordDTO = new AlarmOrderRecordDTO();
            alarmOrderRecordDTO.setTableName(recordTableName);
            List<AlarmOrderRecordVO> recordList = new ArrayList<>();
            insertRecored.setLinkName(orderRecordInfo.getLinkName());
            insertRecored.setRealLinkType(orderRecordInfo.getRealLinkType());
            insertRecored.setProcessOrderId(orderRecordInfo.getProcessOrderId());
            insertRecored.setApproveOrderId(orderRecordInfo.getApproveOrderId());
            insertRecored.setAcceptProcessId(loginUser.getUser().getUserId());
            insertRecored.setOrderFlowId(PKUtils.getFlowOrderId(alarmOrderVO.getTenantId(), alarmOrderVO.getWarningOrderId().substring(0, 6)));
            recordList.add(insertRecored);
            alarmOrderRecordDTO.setList(recordList);
            orderFlowRecordMapper.insertAlarmOrderRecordList(alarmOrderRecordDTO);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("受理失败失败！原因：", e);
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }

    /**
     * 研判：假警、真警、重复告警处理
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result checkAlarmOrder(JudgeAlarmOrderDTO dto) {
        try {
            //兼容未更新版本的行业默认流程
            if (StringUtils.isEmpty(dto.getRealLinkType()) && Constants.REAL_LINK_TYPE_ALL.contains(dto.getLinkId())) {
                dto.setRealLinkType(dto.getLinkId());
            }
            dto.setFlowLinkType(Constants.ZERO);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == loginUser || null == loginUser.getUser()) {
                return Result.fail(Constants.NO_USER_INFO);
            }
            // 校验告警单表
            String warningOrderId = dto.getOrderId();
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
            String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderId.substring(0, 6);

            // 校验工单表：校验工单表中当面工单是否为待研判状态
            WarnOrderStatusDTO warnOrderStatusDTO = this.checkOrderStatus(warningOrderId, alarmTableName, EnumUtil.FlowNode.PENDING_JUDGE.getCode(), null);
            if (null == warnOrderStatusDTO) {
                return Result.fail(Constants.MESS_3);
            }
            dto.setAlarmBody(warnOrderStatusDTO.getAlarmBody());
            dto.setJudgeResult(Constants.JUDGE_RESULT_1);
            //误报重复告警linkId与realLinkType值一样
            if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(dto.getLinkId())) {
                dto.setLinkName(EnumUtil.FlowNode.MISINFORMATION.getName());
                dto.setJudgeResult(Constants.JUDGE_RESULT_0);
            }
            if (EnumUtil.FlowNode.DUPLICATION.getCode().equals(dto.getLinkId())) {
                dto.setLinkName(EnumUtil.FlowNode.DUPLICATION.getName());
                dto.setJudgeResult(Constants.JUDGE_RESULT_2);
            }
            // 校验环节表:判断当前工单是否在研判环节
            AlarmOrderRecordVO alarmOrderRecordVO = new AlarmOrderRecordVO();
            alarmOrderRecordVO.setOrderId(warningOrderId);
            alarmOrderRecordVO.setTableName(recordTableName);
            alarmOrderRecordVO.setIfCurrentLink(Constants.ONE);
            List<AlarmOrderRecordVO> recordVOList = orderFlowRecordMapper.getAlarmOrderRecordInfoNew(alarmOrderRecordVO);
            // 环节表为空，则创建流程
            if (CollectionUtils.isEmpty(recordVOList)) {
                // 创建流程
                List<ReceiveAlarmVO> alarms = new ArrayList<>();
                ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
                BeanUtils.copyProperties(warnOrderStatusDTO, receiveAlarmVO);
                receiveAlarmVO.setWarningOrderId(warnOrderStatusDTO.getWarningOrderId());
                receiveAlarmVO.setAlarmType(receiveAlarmVO.getWarningTypeId());
                receiveAlarmVO.setProvinceCode(receiveAlarmVO.getProvinceId());
                receiveAlarmVO.setCityCode(receiveAlarmVO.getCityId());
                receiveAlarmVO.setCountyCode(receiveAlarmVO.getCountyId());
                receiveAlarmVO.setLinkName(EnumUtil.WarningSource.AI.getName());
                receiveAlarmVO.setFlowId(dto.getFlowId());
                receiveAlarmVO.setFlowName(warnOrderStatusDTO.getLinkName());
                receiveAlarmVO.setLinkId(dto.getLinkId());
                receiveAlarmVO.setOrderStatus(dto.getOrderStatus());
                // 人工上报、一键告警历史数据会有流程环节，保持现状
                receiveAlarmVO.setFlowNodeTime(receiveAlarmVO.getCreateTime());
                receiveAlarmVO.setProcessDTOList(dto.getProcessDTOList());
                //是否会签0:不会签  1:会签
                receiveAlarmVO.setIsSign(dto.getIsSign());
                //下一环节信息为必传字段
                receiveAlarmVO.setCurrentLinkId(dto.getLinkId());
                receiveAlarmVO.setCurrentLinkName(warnOrderStatusDTO.getLinkName());
                receiveAlarmVO.setAppVerCode(loginUser.getUser().getAppVerCode());
                receiveAlarmVO.setIndustryCode(loginUser.getUser().getIndustryCode());
                receiveAlarmVO.setFlowLinkType(Constants.ZERO);
                receiveAlarmVO.setRealLinkType(dto.getRealLinkType());
                alarms.add(receiveAlarmVO);
                List<Map> recordList = this.createRecord(alarms);
                // 回填当前环节相关字段
                this.setWarningLinkInfo(alarms, recordList);
                dto.setFlowId(String.valueOf(recordList.get(0).get(Constants.PARAM_FLOW)));

            } else {
                if (!StringUtils.isEmpty(warnOrderStatusDTO.getFlowId())) {
                    dto.setFlowId(warnOrderStatusDTO.getFlowId());
                }
                boolean flag = false;
                for (AlarmOrderRecordVO vo : recordVOList) {
                    // 当前环节为待研判
                    if ((Constants.YES_FLAG).equalsIgnoreCase(vo.getIfCurrentLink()) && (EnumUtil.FlowNode.PENDING_JUDGE.getCode()).equalsIgnoreCase(vo.getRealLinkType())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return Result.fail(Constants.MESS_3);
                }
            }
            // 从redis获取行政区划列表
            List<String> list = Arrays.asList(dto.getProvinceId(), dto.getCityId(), dto.getCountyId());
            Map<String, String> areaRedisMap = deviceRepository.getAreaNameCaffeineMap(list);
            if (MapUtils.isNotEmpty(areaRedisMap)) {
                dto.setProvinceName(MapUtils.getString(areaRedisMap, dto.getProvinceId(), ""));
                dto.setCityName(MapUtils.getString(areaRedisMap, dto.getCityId(), ""));
                dto.setCountyName(MapUtils.getString(areaRedisMap, dto.getCountyId(), ""));
            }
            // R-ZBZB-202112310835395 20220105 zzh
            //AlarmOperateDTO alarmOperateDTO = alarmOperateMapper.getAlarmOperate(warningOrderId);
            AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(warningOrderId, null);
            if (null != alarmOperateDTO
                    && Constants.TWO_FLAG.equals(alarmOperateDTO.getOperateType())
                    && Constants.JUDGE_RESULT_3.equals(alarmOperateDTO.getJudgeResult())) {
                // 运营研判为并行并且待研判状态
                String originalJson = JSON.toJSONString(dto);
                Map param = JSON.parseObject(originalJson, Map.class);
                param.put(Constants.PARAM_WARNING_ORDER_ID, warningOrderId);
                // 来源：客户
                param.put(Constants.PARAM_SOURCE, Constants.YES_FLAG);
                if (!StringUtils.isEmpty(dto.getEmergencyLevel())) {
                    List<SysDictDataResponse> emergencyLevelEnum = this.getEnumList(Constants.EMERGENCY_LEVEL);
                    if (!CollectionUtils.isEmpty(emergencyLevelEnum)) {
                        emergencyLevelEnum.forEach(item -> {
                            if (item.getDictValue().equals(param.get(Constants.PARAM_EMERGENCY_LEVEL))) {
                                param.put(Constants.PARAM_EMERGENCY_LEVEL_NAME, item.getDictLabel());
                            }
                        });
                    }
                }
                if (!StringUtils.isEmpty(dto.getWarningTypeId())) {
                    List<Map> alramTypeList = this.getAlarmTypeList();
                    if (!CollectionUtils.isEmpty(alramTypeList)) {
                        alramTypeList.forEach(item -> {
                            if (item.get(Constants.PARAM_TYPE_VALUE_).toString()
                                    .equals(param.get(Constants.PARAM_WARNING_TYPE_ID))) {
                                param.put(Constants.PARAM_WARNING_TYPE_NAME, item.get(Constants.PARAM_TYPENAME));
                            }
                        });
                    }
                }
                //Result result = alarmOperateRepository.checkAlarmOrder(param);

                param.put(Constants.PARAM_CREATE_USER, sysService.getLoginUserId());
                param.put(Constants.PARAM_CREATE_USER_NAME, loginUser.getUser().getUserName());
                // R-ZBZB-202112310835395 20220105 zzh 来源为铁塔
                if (StringUtils.isEmpty(param.get(Constants.PARAM_SOURCE))) {
                    param.put(Constants.PARAM_SOURCE, Constants.VALID_STATE);
                }
                alarmOperateMapper.updateAlarmOperate(param);
                try {
                    OperateMongo mongo = JSON.parseObject(JSON.toJSONString(param), OperateMongo.class);
                    List<OperateMongo> operateMongoList = new ArrayList<>();
                    mongo.setModifierTime(DateUtils.currentDateToString());
                    operateMongoList.add(mongo);
                    operateMongoMapper.updateOperate(operateMongoList);
                } catch (Exception e) {
                    log.error("checkAlarmOrder-修改运营研判mongo异常:", e);
                }
                /*if (ResultCode.FAILURE.getCode() == result.getCode()) {
                    // 调用接口异常，回滚
                    throw new Exception((String) result.getData());
                }*/
            }

            if (StringUtils.isEmpty(dto.getWarningTypeId())) {
                dto.setWarningTypeId(warnOrderStatusDTO.getWarningTypeId());
            }
            //当前环节ID
            dto.setCurrentLink(warnOrderStatusDTO.getLinkId());
            dto.setTenantId(warnOrderStatusDTO.getTenantId());
            dto.setAlarmBody(warnOrderStatusDTO.getAlarmBody());
            dto.setUserRecord(warnOrderStatusDTO.getUserRecord());
            dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
            dto.setFlowLinkType(Constants.ZERO);
            if (!ObjectUtils.isEmpty(warnOrderStatusDTO.getOrgRecord())) {
                dto.setOrgRecord(warnOrderStatusDTO.getOrgRecord());
            }
            try {
                //v1.7.10 研判环节-关联告警标签
                if (!EnumUtil.FlowNode.DUPLICATION.getCode().equals(dto.getLinkId())) {
                    AddLableRelationDTO lableRelationDTO = new AddLableRelationDTO();
                    lableRelationDTO.setWaringOrderId(warningOrderId);
                    lableRelationDTO.setLables(dto.getLables());
                    log.info("研判-告警关联标签保存入参:" + JSON.toJSONString(lableRelationDTO));
                    iTradeWarningOrderService.saveTagRelation(lableRelationDTO);
                }
            } catch (Exception e) {
                log.error("研判-保存关联的告警标签异常:" + e);
            }
            return this.updateAlarmOrder(dto);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("研判失败！原因：", e);
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }

    /**
     * 督办
     *
     * @param dto
     * @return Result
     * @author lyy
     * @date 2024/06/25
     * @version v1.6.80
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result urgeToHandleAlarmOrder(SendSupervisionMsgDTO dto) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || (loginUser != null && loginUser.getUser() == null)) {
                return Result.fail(Constants.NO_USER_INFO);
            }
            String warningOrderId = dto.getOrderId();
            if (StringUtils.isEmpty(warningOrderId)) {
                return Result.fail("告警编码不能为空，督办失败！");
            }
            String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderId.substring(0, 6);
            //查询当前环节流程
            AlarmOrderRecordVO alarmOrderRecordVO = new AlarmOrderRecordVO();
            alarmOrderRecordVO.setOrderId(warningOrderId);
            alarmOrderRecordVO.setTableName(recordTableName);
            alarmOrderRecordVO.setIfCurrentLink(Constants.ONE);
            List<AlarmOrderRecordVO> recordVOList = orderFlowRecordMapper.getAlarmOrderRecordInfoNew(alarmOrderRecordVO);
            if (!CollectionUtils.isEmpty(recordVOList)) {
                HandleRecordMongo handleRecordMongo = new HandleRecordMongo();
                handleRecordMongo.setOrderId(warningOrderId);
                handleRecordMongo.setOrderFlowId(recordVOList.get(0).getOrderFlowId());
                handleRecordMongo.setLinkId(recordVOList.get(0).getLinkId());
                handleRecordMongo.setHandleLinkName(recordVOList.get(0).getLinkName());
                handleRecordMongo.setHandleRemark(dto.getRemark());
                handleRecordMongo.setHandleTime(DateUtil.format(new Date(), Constants.DATE_YMDHMS));
                handleRecordMongo.setHandleUserId(loginUser.getUser().getUserId());
                handleRecordMongo.setDeleteFlag(Constants.DELETE_FLAG);
                handleRecordMongo.setTenantId(loginUser.getUser().getTenantId());
                handleRecordMongo.setIndustryCode(loginUser.getUser().getIndustryCode());
                handleRecordDAO.mongoInsert(handleRecordMongo, MongoDBType.PRIMARY, TableConstant.VM_FLOW_HANDLE_RECORD_T);

                //------ 发送督办消息提醒 ------
                String alarmTableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
                AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
                alarmOrderDTO.setOrderId(warningOrderId);
                alarmOrderDTO.setTableName(alarmTableName);
                WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderDTO);
                if (warnOrderStatusDTO != null) {
                    MessagePushDTO messagePushDTO = new MessagePushDTO();
                    messagePushDTO.setOrderId(warningOrderId);
                    messagePushDTO.setMsgId(warningOrderId);
                    messagePushDTO.setDeviceCode(warnOrderStatusDTO.getDeviceCode());
                    messagePushDTO.setWarningSource(warnOrderStatusDTO.getWarningSource());
                    messagePushDTO.setTenantId(loginUser.getUser().getTenantId());
                    messagePushDTO.setAlarmFlowId(warnOrderStatusDTO.getFlowId());
                    messagePushDTO.setLinkId(warnOrderStatusDTO.getLinkId());
                    messagePushDTO.setCreator(loginUser.getUser().getUserId());
                    messagePushDTO.setHandleUserName(loginUser.getUser().getNickName());
                    messagePushDTO.setNotifyTitle(warnOrderStatusDTO.getAlarmBody());
                    messagePushDTO.setAddress(warnOrderStatusDTO.getAddress());
                    String processId = warnOrderStatusDTO.getProcessId();
                    if (StringUtils.isEmpty(processId)) {
                        //环节处理人为空，推送给租户管理员
                        SysInfo adminUser = deviceRepository.queryAdminUser(messagePushDTO.getTenantId());
                        if (adminUser != null) {
                            messagePushDTO.setUserId(adminUser.getUserId());
                        }
                    } else {
                        if (processId.startsWith(Constants.COMMA)) {
                            processId = processId.substring(1, processId.length());
                        }
                        if (processId.endsWith(Constants.COMMA)) {
                            processId = processId.substring(0, processId.length() - 1);
                        }
                        messagePushDTO.setUserId(processId);
                    }
                    messagePushDTO.setNotifyType(Constants.SYS_MSG_TYPE_1);
                    messagePushDTO.setOrderStatus(warnOrderStatusDTO.getOrderStatus());
                    messagePushDTO.setFlowTemplate(warnOrderStatusDTO.getFlowTemplate());
                    messagePushDTO.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
                    messagePushDTO.setAppVerCode(loginUser.getUser().getAppVerCode());
                    messagePushDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
                    messagePushDTO.setTypeValue(warnOrderStatusDTO.getWarningTypeId());
                    String warningTypeId = warnOrderStatusDTO.getWarningTypeId();
                    messagePushDTO.setWarningTypeId(warningTypeId);
                    if (!StringUtils.isEmpty(warningTypeId)) {
                        AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
                        messagePushDTO.setWarningTypeName(alarmTypeInfo != null ? alarmTypeInfo.getTypeName() : null);
                    } else {
                        messagePushDTO.setWarningTypeName("");
                    }
                    messagePushDTO.setRealLinkType(warnOrderStatusDTO.getRealLinkType());
                    messagePushDTO.setEnableAcceptanceMode(warnOrderStatusDTO.getEnableAcceptanceMode());
                    messagePushDTO.setFlowOverTime(DateUtils.date2Str(recordVOList.get(0).getFlowEndTime(), Constants.DATE_YMDHMS));
                    supervisionMsgSendService.supervisionSendMsg(messagePushDTO);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("督办失败！原因：", e);
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 根据工单ID查询督办记录
     *
     * @param dto
     * @return List<HandleRecordVO>
     * @author lyy
     * @version v1.6.80
     */
    @Override
    public List<HandleRecordVO> getHandleRecordByOrderId(AlarmOrderDTO dto) {
        log.info("查询督办记录入参，告警编码：{}", dto.getOrderId());
        return this.getHandleRecordList(dto.getOrderId(), ZERO);
    }

    private List<HandleRecordVO> getHandleRecordList(String orderId, String flag) {
        if (StringUtils.isEmpty(orderId)) {
            throw new CustomException("参数错误");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.PARAM_ORDER_ID).is(orderId));
        if (ONE.equals(flag)) {
            query.addCriteria(Criteria.where(Constants.PARAM_DELETE_FLAG).is(ZERO));
        }
        query.with(Sort.by(Sort.Direction.DESC, "handleTime"));
        List<HandleRecordVO> handleRecordList = mongoTemplate.find(query, HandleRecordVO.class, TableConstant.VM_FLOW_HANDLE_RECORD_T);
        if (!CollectionUtils.isEmpty(handleRecordList)) {
            List<String> userIdList = handleRecordList.stream().map(HandleRecordVO::getHandleUserId).distinct().collect(Collectors.toList());
            List<SysUserVO> sysUserList = deviceRepository.getUserInfoByUserIds(userIdList);
            if (!CollectionUtils.isEmpty(sysUserList)) {
                for (HandleRecordVO recordVO : handleRecordList) {
                    for (SysUser sysUser : sysUserList) {
                        if (!StringUtils.isEmpty(recordVO.getHandleUserId()) && recordVO.getHandleUserId().equals(sysUser.getUserId())) {
                            recordVO.setHandleUserName(!StringUtils.isEmpty(sysUser.getNickName()) ? sysUser.getNickName() : sysUser.getUserName());
                            recordVO.setPhoneNumber(sysUser.getPhonenumber());
                        }
                    }
                }
            }
        }
        return handleRecordList;
    }

    /**
     * @Author：hued
     * @Description：校验工单状态是否异常
     * @Date：2021/12/24 10:02
     */
    @Override
    public WarnOrderStatusDTO checkOrderStatus(String orderId, String tableName, String orderStatus, String linkId) {
        AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
        alarmOrderVO.setOrderId(orderId);
        alarmOrderVO.setOrderStatus(orderStatus);
        alarmOrderVO.setLinkId(linkId);
        alarmOrderVO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));
        alarmOrderVO.setTableName(tableName);
        return warningOrderMapper.getAlarmOrder(alarmOrderVO);
    }

    @Override
    public List<WarnOrderStatusDTO> selectCheckOrderStatus(List<String> orderIds, String tableName) {
        AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
        alarmOrderVO.setWarningOrderIds(orderIds);
        alarmOrderVO.setTableName(tableName);
        return warningOrderMapper.getAlarmOrderListByJudge(alarmOrderVO);
    }

    @Override
    public WarnOrderStatusDTO checkOrderAccept(String orderId, String tableName, String linkId, String enableAcceptanceMode) {
        AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
        alarmOrderVO.setOrderId(orderId);
        alarmOrderVO.setLinkId(linkId);
        alarmOrderVO.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));
        alarmOrderVO.setTableName(tableName);
        alarmOrderVO.setEnableAcceptanceMode(enableAcceptanceMode);
        return warningOrderMapper.getAlarmOrder(alarmOrderVO);
    }

    @ApiOperation(tags = "更新工单状态", value = "")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result updateAlarmOrder(@RequestBody SendAlarmOrderDTO alarmOrderVO) {
        try {
            //兼容代码
            if (StringUtils.isEmpty(alarmOrderVO.getNextEnableAcceptanceMode())) {
                alarmOrderVO.setNextEnableAcceptanceMode(Constants.ZERO);
            }
            if (StringUtils.isEmpty(alarmOrderVO.getEnableAcceptanceMode())) {
                alarmOrderVO.setEnableAcceptanceMode(Constants.ZERO);
            }
            //查看入参是否有指定环节处理人，有则记录标记位为后续流转表记录指定人合集
            if (alarmOrderVO.getProcessDTOList() != null && alarmOrderVO.getProcessDTOList().size() > 0) {
                alarmOrderVO.setBeforeDispatchFlag(Constants.BEFORE_DISPATCH_FLAG_1);
            } else {
                alarmOrderVO.setBeforeDispatchFlag(Constants.BEFORE_DISPATCH_FLAG_0);
            }
            //当前登入人信息
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (SecurityUtils.isSwitch()) {
                loginUser = loginUser.getSwitchdLoginUser();
            }
            String warningOrderId = alarmOrderVO.getOrderId();
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
            String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderId.substring(0, 6);
            alarmOrderVO.setWarningOrderId(warningOrderId);

            String userId = loginUser.getUser().getUserId();
            String userName = loginUser.getUser().getUserName();
            String orgId = loginUser.getUser().getDeptId();

            StringBuffer processIds = new StringBuffer();
            StringBuffer processNames = new StringBuffer();

            //下一环节相关信息
            String linkId = alarmOrderVO.getLinkId();
            String linkName = alarmOrderVO.getLinkName();
            if (StringUtils.isEmpty(linkName)) {
                if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(linkId)) {
                    linkName = EnumUtil.FlowNode.MISINFORMATION.getName();
                } else if (EnumUtil.FlowNode.END.getCode().equals(linkId)) {
                    linkName = EnumUtil.FlowNode.END.getName();
                } else if (EnumUtil.FlowNode.DUPLICATION.getCode().equals(linkId)) {
                    linkName = EnumUtil.FlowNode.DUPLICATION.getName();
                }
            }
            alarmOrderVO.setOrderStatus(alarmOrderVO.getRealLinkType());
            alarmOrderVO.setLinkName(linkName);
            alarmOrderVO.setFlowNodeTime(DateUtils.currentDateToString());
            alarmOrderVO.setTableName(alarmTableName);
            //已处理人
            String userRecord = alarmOrderVO.getUserRecord();
            if (!StringUtils.isEmpty(userRecord)) {
                userRecord = userRecord + Constants.COMMA + userId;
            } else {
                userRecord = userId;
            }
            alarmOrderVO.setUserRecord(userRecord);
            //已处理组织
            String orgRecord = alarmOrderVO.getOrgRecord();
            if (!StringUtils.isEmpty(orgRecord) && !StringUtils.isEmpty(orgId)) {
                orgRecord = orgRecord + Constants.COMMA + orgId;
            } else {
                orgRecord = orgId;
            }
            alarmOrderVO.setOrgRecord(orgRecord);

            //插入流程环节表下一环节数据
            AlarmOrderRecordVO insertRecored = new AlarmOrderRecordVO();
            this.insertRecoredHandle(alarmOrderVO, insertRecored);

            //更新流程环节表当前环节数据
            AlarmOrderRecordVO updateRecored = new AlarmOrderRecordVO();
            //调度环节新增update流程表写入指定得所有处理人
            if (Constants.BEFORE_DISPATCH_FLAG_1.equals(alarmOrderVO.getBeforeDispatchFlag())) {
                String processNameList = "";
                for (ProcessDTO processDTO : alarmOrderVO.getProcessDTOList()) {
                    if (StringUtils.isEmpty(processNameList)) {
                        processNameList = processDTO.getProcessId();
                    } else {
                        processNameList += Constants.COMMA + processDTO.getProcessId();
                    }
                }
                if (StringUtils.isEmpty(processNameList)) {
                    processNameList = alarmOrderVO.getProcessId();
                }
                updateRecored.setProcessNameList(processNameList);
            }
            this.updateRecoredHandle(alarmOrderVO, updateRecored, loginUser);

            //保存环节上传的附件
            this.addOrderFile(alarmOrderVO);

            //处理当前环节
            //是否流转流程0：不流转（需要会签）  当前环节是会签且会签未完成时,流程环节不流转
            if (Constants.VALID_STATE.equals(alarmOrderVO.getIsNextLink())) {
                //是否会签0:不会签  1:会签    会签时根据处理人更新vm_order_flow_record_t_YYYYMM
                //1、当前环节是处置时(updateProcessOrder中进行判断是否为处置环节)，更新vm_process_order_t表数据
                this.handlePendingHandle(alarmOrderVO, updateRecored, insertRecored);

                updateRecored.setIfCurrentLink(null);

                //3、更新vm_process_approve_t表数据
                this.handlePendingCheck(alarmOrderVO, updateRecored);
                //更新vm_order_flow_record_t_YYYYMM表对应处理人流程记录
                orderFlowRecordMapper.updateAlarmOrderRecord(updateRecored);
            }
            //1：流转下个流程环节(会签环节结束/其他流程环节结束)
            else {
                //1、更新vm_order_flow_record_t_YYYYMM表对应处理人流程记录
                //2、插入下一环节vm_order_flow_record_t_YYYYMM数据
                //3、更新vm_warning_order_t_YYYYMM表处理人（process_id、process_name、group_id）、已处理人（user_record）、组织/部门id流转记录（org_record）
                //4、更新vm_process_order_t表数据
                //5、更新vm_process_approve_t表数据
                //当前环节是处置环节时，更新vm_process_order_t表数据,vm_process_approve_t表插入数据，如果会签插入多条，否则插入一条
                this.handlePendingHandle(alarmOrderVO, updateRecored, insertRecored);

                //下一环节是处置环节时，插入vm_process_order_t表数据，如果会签插入多条，否则插入一条
                this.handleProcessOrder(alarmOrderVO, updateRecored, insertRecored);

                //当前环节是核实环节
                this.handlePendingCheck(alarmOrderVO, updateRecored);

                //如果下个环节是会签vm_order_flow_record_t_YYYYMM表插入多条数据
                AlarmOrderRecordDTO alarmOrderRecordDTO = new AlarmOrderRecordDTO();
                alarmOrderRecordDTO.setTableName(recordTableName);
                List<AlarmOrderRecordVO> recordList = new ArrayList<>();
                List<ProcessDTO> processDTOList = alarmOrderVO.getProcessDTOList();

                //处理人不为空取接口传入的处理人
                if (!CollectionUtils.isEmpty(processDTOList)) {
                    for (ProcessDTO processDTO : processDTOList) {
                        processIds.append(Constants.COMMA).append(processDTO.getProcessId());
                        processNames.append(Constants.COMMA).append(processDTO.getProcessName());
                    }
                    if (processIds.length() > 1) {
                        processIds.append(Constants.COMMA);
                    }
                    if (processNames.length() > 1) {
                        processNames.append(Constants.COMMA);
                    }
                    alarmOrderVO.setProcessId(String.valueOf(processIds));
                    alarmOrderVO.setProcessName(String.valueOf(processNames));
                }
                //处理人为空取流程配置的处理人
           /*     else {
                    AlarmFlowConfigModel model = new AlarmFlowConfigModel();
                    model.setFlowId(!StringUtils.isEmpty(alarmOrderVO.getFlowId())? Long.valueOf(alarmOrderVO.getFlowId()):null);
                    model.setLinkId(!StringUtils.isEmpty(alarmOrderVO.getLinkId())? Long.valueOf(alarmOrderVO.getLinkId()):null);
                    if(!StringUtils.isEmpty(alarmOrderVO.getWarningSource())){
                        model.setWarningSource(alarmOrderVO.getWarningSource());
                    }
                    if(!StringUtils.isEmpty(alarmOrderVO.getFlowTemplate())){
                        model.setTemplateId(Long.valueOf(alarmOrderVO.getFlowTemplate()));
                    }
                    Result<List<FlowModel>> result = alarmFlowConfigService.queryFlowLink(model);
                    if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                        if (!CollectionUtils.isEmpty(result.getData())) {
                            FlowModel flowModel = result.getData().get(0);
                            if (null != flowModel) {
                                if (Constants.USER_TYPE_1.equals(flowModel.getUserType())) {
                                    alarmOrderVO.setProcessId(Constants.COMMA + flowModel.getUserId() + Constants.COMMA);
                                    alarmOrderVO.setProcessName(Constants.COMMA + flowModel.getUserName() + Constants.COMMA);
                                } else if (Constants.USER_TYPE_2.equals(flowModel.getUserType())) {
                                    alarmOrderVO.setRoleId(flowModel.getRoleId());
                                }
                            }
                        }
                    }
                }*/
                //更新vm_order_flow_record_t_YYYYMM表对应处理人流程记录
                orderFlowRecordMapper.updateAlarmOrderRecord(updateRecored);

                if (EnumUtil.FlowNode.END.getCode().equals(alarmOrderVO.getLinkId()) || EnumUtil.FlowNode.MISINFORMATION.getCode().equals(alarmOrderVO.getLinkId()) || EnumUtil.FlowNode.DUPLICATION.getCode().equals(alarmOrderVO.getLinkId())) {
                    insertRecored.setProcessId(userId);
                    insertRecored.setProcessName(userName);
                }
                //插入下一个环节得受理状态
                insertRecored.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
                insertRecored.setOrderFlowId(PKUtils.getFlowOrderId(alarmOrderVO.getTenantId(), alarmOrderVO.getWarningOrderId().substring(0, 6)));
                recordList.add(insertRecored);
                alarmOrderRecordDTO.setList(recordList);
                orderFlowRecordMapper.insertAlarmOrderRecordList(alarmOrderRecordDTO);

                //更新、插入流程环节用户处理表
                this.linkUserDealHandle(alarmOrderVO);
                //工单表当前工单受理状态更改，下个环节没有得时候取当前状态得受理环节，当前是2（已受理时），修改为3
               /* if(StringUtils.isEmpty(alarmOrderVO.getNextEnableAcceptanceMode())){
                    if(Constants.TWO.equals(alarmOrderVO.getEnableAcceptanceMode())){
                        alarmOrderVO.setEnableAcceptanceMode(Constants.THREE);
                    }else{
                        alarmOrderVO.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
                    }
                }else{
                    alarmOrderVO.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
                }*/
                alarmOrderVO.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
                warningOrderMapper.updateByPrimaryKeySelective(alarmOrderVO);
                try {
                    List<DelayMessageDto> delayMessageDtoList = new ArrayList<>();
                    DelayMessageDto delayMessageDto = new DelayMessageDto();
                    delayMessageDto.setOrderId(alarmOrderVO.getOrderId());
                    //delayMessageDto.setIndustryCode(alarmOrderVO.getIndustryCode());
                    delayMessageDtoList.add(delayMessageDto);
                    log.info("流程流转更新延迟消息表入参:" + JSON.toJSONString(delayMessageDtoList));
                    orderDelayMessageService.updateOrderDelayList(delayMessageDtoList);
                } catch (Exception e) {
                    log.error("流程流转更改未处理消息异常:" + e);
                }
            }
            //流程环节流转推送消息
            if (Constants.ONE.equals(alarmOrderVO.getIsNextLink())
                    && !(EnumUtil.FlowNode.END.getCode().equals(alarmOrderVO.getLinkId())
                    || EnumUtil.FlowNode.MISINFORMATION.getCode().equals(alarmOrderVO.getLinkId())
                    || EnumUtil.FlowNode.DUPLICATION.getCode().equals(alarmOrderVO.getLinkId()))) {
                // 20210625 消息推送
                alarmOrderVO.setFlowOverTime(DateUtils.date2Str(updateRecored.getFlowEndTime(), Constants.DATE_YMDHMS));
                log.info("时间流转环节结束时间:{}", JSON.toJSONString(alarmOrderVO));
                this.messagePush(alarmOrderVO, loginUser);
                log.info("查询异常:{}", JSON.toJSONString(alarmOrderVO));
            }
            //如果有加入收藏则跟新收藏的数据
            AlarmOrderDTO dto = new AlarmOrderDTO();
            dto.setTableName(TableConstant.WAR_ORD_COLL);
            dto.setOrderId(alarmOrderVO.getWarningOrderId());
            List<WarningOrderCollectionDO> warningOrderCollection = warningOrderCollectionMapper.getWarningOrderByParam(dto);
            if (!CollUtil.isEmpty(warningOrderCollection)) {
                //未查到数据，不做收藏表修改
                iWarningOrderCollectionRepository.updateWarningOrderCollectionForBigScreen(alarmOrderVO);
            }
            log.info("updateWarningOrderCollectionForBigScreen时间");
            //更新领导批示表工单状态
            LeaderAuidtListDTO leaderDto = new LeaderAuidtListDTO();
            leaderDto.setOrderId(alarmOrderVO.getWarningOrderId());
            List<LeaderAuidtListVO> resultList = warningOrderMapper.getLeaderAuditListOnlyByOrderId(leaderDto);
            if (!CollectionUtils.isEmpty(resultList)) {
                OrderLeaderAuditDO auditInfo = new OrderLeaderAuditDO();
                auditInfo.setOrderId(alarmOrderVO.getWarningOrderId());
                /*auditInfo.setOrderStatus(alarmOrderVO.getOrderStatus());
                auditInfo.setAddress(alarmOrderVO.getAddress());
                auditInfo.setLatitude(alarmOrderVO.getLatitude());
                auditInfo.setLongitude(alarmOrderVO.getLongitude());
                auditInfo.setImportanceLevel(alarmOrderVO.getImportanceLevel());*/
                Boolean updateFlag = Boolean.FALSE;
                //增加告警重要等级更新，防止领导批示列表出现排序问题
                if (alarmOrderVO.getImportanceLevel() != null) {
                    updateFlag = Boolean.TRUE;
                    auditInfo.setImportanceLevel(alarmOrderVO.getImportanceLevel());
                }
                if (alarmOrderVO.getAlarmReason() != null) {
                    updateFlag = Boolean.TRUE;
                    auditInfo.setAlarmReason(alarmOrderVO.getAlarmReason());
                }
                if (updateFlag) {
                    orderLeaderAuditMapper.updateLeaderAuditOrderStatus(auditInfo);
                }
            }

            //清空该工单所有超期及受理提醒数据
            //overdueRemindMapper.deleteOverdueRemind(alarmOrderVO.getOrderId(),null);
            asyncCommon.deleteOverdueRemind(alarmOrderVO, null);
            if (!StringUtils.isEmpty(alarmOrderVO.getAttrId())) {
                List<ReceiveAlarmVO> alarms = new ArrayList<>();
                ReceiveAlarmVO vo = new ReceiveAlarmVO();
                BeanUtil.copyProperties(alarmOrderVO, vo);
                vo.setFlowAttrId(alarmOrderVO.getAttrId());
                alarms.add(vo);
                log.info("时间流转插入超期表数据req:{}", JSON.toJSONString(alarms));
                this.saveOverdueRemindData(alarms);
            }

            //更新督办记录表
            Query updateQuery = Query.query(Criteria.where(Constants.PARAM_ORDER_ID).is(alarmOrderVO.getOrderId()));
            Update updateEntity = new Update()
                    .set(Constants.PARAM_DELETE_FLAG, Constants.ONE);
            handleRecordDAO.mongoUpdate(updateQuery, updateEntity, MongoDBType.PRIMARY, TableConstant.VM_FLOW_HANDLE_RECORD_T, true);

            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            log.error("更新工单状态异常", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }


    /**
     * 处置进入核实环节时
     * 判断下个是否会签
     * 如果是会签，查询流程获取会签处理人，插入多条待审核数据
     * 如果不是会签，插入一条待审核数据
     *
     * @param alarmOrderVO
     */
    public void handleSign(SendAlarmOrderDTO alarmOrderVO) {
        boolean flag = false;
        String processOrderId = "";
        List<ProcessOrderDTO> processOrderVOList = alarmOrderVO.getProcessOrderVOList();
        if (!CollectionUtils.isEmpty(processOrderVOList)) {
            ProcessOrderDTO processOrderDTO = processOrderVOList.get(0);
            processOrderId = processOrderDTO.getProcessOrderId();
        }
        ProcessApproveDTO orderApproveVO = new ProcessApproveDTO();
        orderApproveVO.setOrderId(alarmOrderVO.getOrderId());
        orderApproveVO.setProcessOrderId(processOrderId);
        //待审核
        orderApproveVO.setApproveStatus(Constants.YES_FLAG);
        //查询下一环节数据，如果环节是会签，插入所有会签数据
        AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
        alarmFlowConfigModel.setId(Long.valueOf(alarmOrderVO.getFlowId()));
        alarmFlowConfigModel.setLinkId(Long.valueOf(alarmOrderVO.getLinkId()));
        FlowModel flowLink = this.getFlowLink(alarmFlowConfigModel);
        //会签
        if (flowLink != null) {
            List<FlowLinkAttrModel> linkAttrModels = flowLink.getData();
            if (!CollectionUtils.isEmpty(linkAttrModels)) {
                FlowLinkAttrModel attrModel = linkAttrModels.get(0);
                if (attrModel != null && Constants.SIGN_FLAG_1.equals(attrModel.getSignFlag()) && !StringUtils.isEmpty(attrModel.getUserId())) {
                    //获取环节可处理人员
                    String userId = attrModel.getUserId();
                    String[] userIds = userId.split(Constants.COMMA);
                    for (String approvePerson : userIds) {
                        //插入审核单表
                        orderApproveVO.setApprovePerson(approvePerson);
                        processApproveMapper.insertOrderApprove(orderApproveVO);
                        flag = true;
                    }
                }
            }
        }
        //不会签
        if (!flag) {
            //插入审核单表
            processApproveMapper.insertOrderApprove(orderApproveVO);
        }
    }

    /**
     * 判断环节是否会签
     *
     * @param flowId
     * @param linkId
     * @return
     */
    public boolean isSign(String flowId, String linkId) {
        AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
        alarmFlowConfigModel.setId(Long.valueOf(flowId));
        alarmFlowConfigModel.setLinkId(Long.valueOf(linkId));
        FlowModel flowLink = this.getFlowLink(alarmFlowConfigModel);
        //会签
        if (flowLink != null) {
            if (Constants.SIGN_FLAG_1.equals(flowLink.getSignFlag())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断会签是否完成,所有会签都通过
     *
     * @param warningOrderId
     * @return
     */
    @Override
    public boolean finishSign(String warningOrderId) {
        ProcessApproveDTO dto = new ProcessApproveDTO();
        //查询vm_process_order_t表获取最新数据
        ProcessOrderDTO processOrderDTO = new ProcessOrderDTO();
        processOrderDTO.setWarningOrderId(warningOrderId);
        ProcessOrderVO processOrderVO = processOrderMapper.getProcessOrder(processOrderDTO);
        if (processOrderVO != null) {
            dto.setProcessOrderId(processOrderVO.getProcessOrderId());
        }
        dto.setOrderId(warningOrderId);
        dto.setDeleteFlag(Constants.VALID_STATE);
        List<ProcessApproveDTO> list = processApproveMapper.getOrderApproveList(dto);

        if (!CollectionUtils.isEmpty(list)) {
            for (ProcessApproveDTO processApproveDTO : list) {
                if (!Constants.APPROVE_STATUS_2.equals(processApproveDTO.getApproveStatus())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 研判为重复告警时，获取事件列表
     *
     * @param dto
     * @return
     */
    @Override
//    @DataScope(dbType="db1",deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id", industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",deptColumn="dataTable.dept_id")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getOrderListForDuplication(AlarmOrderDuplicateDTO dto) {
        long start = System.currentTimeMillis();
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        try {
            List<WarningOrderListVO> resultList = new ArrayList<>();
            // 总数
            int allCount = 0;
            // 查询开始结束时间
            Date startDate = null;
            Date endDate = null;
            if (dto.getPageNum() == null) {
                dto.setPageNum(Constants.PAGE_NUM);
            }
            if (dto.getPageSize() == null) {
                dto.setPageSize(Constants.PAGE_SIZE);
            }
            // 需要查询的个数
            int limit = dto.getPageSize();
            // 分页开始的数
            int offset = (dto.getPageNum() - 1) * limit;
            // 当前表查询开始的数
            int tableStartNum = 0;
            if (Objects.isNull(dto.getAlarmTimeStart())) {
                startDate = DateUtils.dateLaterString(DateUtils.dateToString(new Date()), -6);
            } else {
                startDate = dto.getAlarmTimeStart();
            }
            if (Objects.isNull(dto.getAlarmTimeEnd())) {
                endDate = new Date();
            } else {
                endDate = dto.getAlarmTimeEnd();
            }

            // 20211214 zzh 过滤掉误报和待研判告警
            List<String> notInOrderStatus = new ArrayList<>();
            notInOrderStatus.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            notInOrderStatus.add(EnumUtil.FlowNode.PENDING_JUDGE.getCode());
            dto.setNotInOrderStatus(notInOrderStatus);

            AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
            alarmOrderDTO.setDeviceLabelIdList(dto.getDeviceLabelIdList());
            alarmOrderDTO.setOrgIdList(dto.getOrgIdList());
            alarmOrderDTO.setModelIds(dto.getModelIds());
            alarmOrderDTO.setDeviceName(dto.getDeviceName());
            alarmOrderDTO.setTenantId(loginUser.getUser().getTenantId());
            alarmOrderDTO.setSysUser(loginUser.getUser());
            //处理传入的组织条件
            iWarningOrderRepository.handleOrgIdList(alarmOrderDTO);
            //跨库获取页面查询设备信息关联数据
            List<AlarmOrderByDeviceDTO> devList = deviceRepository.queryAlarmOrderByDevice(alarmOrderDTO);
            if (CollectionUtils.isEmpty(devList)
                    && (!CollectionUtils.isEmpty(dto.getDeviceLabelIdList()) || !StringUtils.isEmpty(dto.getDeviceName()) ||
                    !CollectionUtils.isEmpty(dto.getModelIds()))) {
                return tableDataInfo;
            }
            dto.setDevList(devList);
            dto.setDeviceCodeList(alarmOrderDTO.getDeviceCodeList());
            dto.setSatelliteList(alarmOrderDTO.getSatelliteList());
            dto.setRadarList(alarmOrderDTO.getRadarList());
            //20221203 告警类型标签
            if (!CollectionUtils.isEmpty(dto.getTagIdList())) {
                List<AlarmTagsTypeRelDTO> alarmTagsTypeRelDTOList = sysAlarmTagsRepository.getAlarmTypeByIds(dto.getTagIdList(), loginUser.getUser().getTenantId());
                if (!CollectionUtils.isEmpty(alarmTagsTypeRelDTOList)) {
                    List<String> warningTypeIdList = new ArrayList<>();
                    alarmTagsTypeRelDTOList.forEach(alarmTagsTypeRelDTO -> {
                        if (!StringUtils.isEmpty(alarmTagsTypeRelDTO.getTypeValue())) {
                            warningTypeIdList.add(alarmTagsTypeRelDTO.getTypeValue());
                        }
                    });
                    if (!CollectionUtils.isEmpty(dto.getWarningTypeIdList())) {
                        warningTypeIdList.addAll(dto.getWarningTypeIdList());
                    }
                    dto.setWarningTypeIdList(warningTypeIdList);
                } else {
                    return tableDataInfo;
                }
            }
            //PT范围查询同一摄像机的AI告警、摄像机告警且PTZ有值
            if (!StringUtils.isEmpty(dto.getAlarmRangeP()) || !StringUtils.isEmpty(dto.getAlarmRangeT())) {
                if ((Constants.ONE.equals(dto.getWarnSource()) || Constants.FOUR.equals(dto.getWarnSource())) &&
                        (StringUtils.isEmpty(dto.getTransPan()) || StringUtils.isEmpty(dto.getTransTitle())
                                || StringUtils.isEmpty(dto.getTransZoom()))) {
                    return tableDataInfo;
                } else {
                    try {
                        //获取摄像设备厂家编码
                        String modelId = dto.getModelId();
                        //获取摄像机类型的焦距范围
                        String deviceFocusRange = dto.getFocusRange();
                        //摄像机水平向下是否为负
                        String vertiDownNeg = dto.getVertiDownNeg();
                        DeviceModelVO deviceModel = new DeviceModelVO();
                        deviceModel.setMfgrCode(modelId);
                        deviceModel.setVertiDownNeg(vertiDownNeg);
                        if (MODELID_DAHUA.equals(modelId) || MODELID_DHUA.equals(modelId)) {
                            deviceModel.setFocusRange(deviceFocusRange);
                        }
                        //PT值判断
                        if (!StringUtils.isEmpty(dto.getAlarmRangeP())) {
                            BigDecimal transPan = new BigDecimal(dto.getTransPan());
                            BigDecimal alarmRangeP = new BigDecimal(dto.getAlarmRangeP());
                            BigDecimal pMax = transPan.add(alarmRangeP);
                            //加P值后，如果值大于360 减360，小于360 赋值0，得到新的P值范围，用于sql中p值判断
                            BigDecimal reAlarmRangePMax = pMax.compareTo(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)) > 0 ?
                                    pMax.subtract(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)) : new BigDecimal(Constants.ZERO_NUM);
                            //将P范围最大值360进行逆转义，用于sql中p值判断
                            dto.setRangePMax(LongLatAngleUtils.paramTransReverse(String.valueOf(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)), deviceModel, "P"));
                            //加p值之后进行逆转义
                            dto.setAlarmRangeMaxP(LongLatAngleUtils.paramTransReverse(String.valueOf(pMax), deviceModel, "P"));
                            //将新的最大P值进行逆转义
                            dto.setReAlarmRangePMax(LongLatAngleUtils.paramTransReverse(String.valueOf(reAlarmRangePMax), deviceModel, "P"));

                            BigDecimal pMin = transPan.subtract(alarmRangeP);
                            BigDecimal reAlarmRangePMin = pMin.compareTo(new BigDecimal(Constants.ZERO_NUM)) < 0 ?
                                    pMin.add(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)) : new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360);
                            //将P范围最小值0进行逆转义，用于sql中p值判断
                            dto.setRangePMin(LongLatAngleUtils.paramTransReverse(String.valueOf(new BigDecimal(Constants.ZERO_NUM)), deviceModel, "P"));
                            //减p值之后进行逆转义
                            dto.setAlarmRangeMinP(LongLatAngleUtils.paramTransReverse(String.valueOf(pMin), deviceModel, "P"));
                            //将新的最小P值进行逆转义
                            dto.setReAlarmRangePMin(LongLatAngleUtils.paramTransReverse(String.valueOf(reAlarmRangePMin), deviceModel, "P"));
                        }
                        if (!StringUtils.isEmpty(dto.getAlarmRangeT())) {
                            BigDecimal transTitle = new BigDecimal(dto.getTransTitle());
                            BigDecimal alarmRangeT = new BigDecimal(dto.getAlarmRangeT());
                            BigDecimal tMax = transTitle.add(alarmRangeT);
                            dto.setAlarmRangeMaxT(LongLatAngleUtils.paramTransReverse(tMax.toString(), deviceModel, "T"));
                            BigDecimal tMin = transTitle.subtract(alarmRangeT);
                            dto.setAlarmRangeMinT(LongLatAngleUtils.paramTransReverse(tMin.toString(), deviceModel, "T"));
                        }
                    } catch (Exception e) {
                        log.error("重复告警ptz逆转义异常", e);
                    }
                }
                if (!Constants.ONE.equals(dto.getWarnSource()) && !Constants.FOUR.equals(dto.getWarnSource())) {
                    return tableDataInfo;
                }
            }
            // 防止时间传反，导致gc
            if (ObjectUtil.isNotNull(dto.getAlarmTimeStart()) && ObjectUtil.isNotNull(dto.getAlarmTimeEnd()) && dto.getAlarmTimeStart().after(dto.getAlarmTimeEnd()))
                return tableDataInfo;

            int errCount = 1;
            int count = 0;
            boolean stop = false;
            // 从最后一个表开始向前获取
            while (!stop) {
                try {
                    String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);

                    dto.setTableName(tableName);
                    count = warningOrderMapper.getOrderListForDuplicationCOUNT(dto);
                    // 累计总数
                    allCount += count;
                    if (allCount > offset && count > 0 && limit > 0) {
                        tableStartNum = offset - (allCount - count);
                        if (tableStartNum < 0) {
                            tableStartNum = 0;
                        }
                        if (count > limit) {
                            dto.setLimit(limit);
                        } else {
                            dto.setLimit(count);
                        }
                        dto.setOffset(tableStartNum);
                        log.info("研判为重复告警时，获取事件列表耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));
                        List<WarningOrderListVO> queryResult = warningOrderMapper.getOrderListForDuplication(dto);
                        resultList.addAll(queryResult);
                        limit = limit - queryResult.size();
                        log.info("getOrderListForDuplication查询重复告警返回：{}", JSON.toJSONString(resultList));
                    }
                } catch (Exception e) {
                    log.error("getOrderListForDuplication查询失败：" + e.getMessage());
                    errCount++;
                    if (errCount > 3) {
                        return tableDataInfo;
                    }
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }

            List<Map> mapList = this.getAlarmTypeList();
            List<SysDictDataResponse> dictList = this.getEnumList(Constants.ORDER_STATUS_3);
            // 重要等级
            List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(Constants.ALARM_LEVEL).getData();
            if (!CollectionUtils.isEmpty(resultList)) {
                for (WarningOrderListVO vo : resultList) {
                    for (Map alarm : mapList) {
                        if (vo.getWarningTypeId().equals(String.valueOf(alarm.get(Constants.PARAM_TYPE_VALUE_)))) {
                            vo.setWarningTypeName(String.valueOf(alarm.get(Constants.PARAM_TYPENAME)));
                            vo.setIcon(String.valueOf(alarm.get(Constants.PARAM_ICON)));
                            break;
                        }
                    }
                    if (!CollectionUtils.isEmpty(dictList)) {
                        for (SysDictDataResponse dictData : dictList) {
                            if (vo.getOrderStatus().equals(dictData.getDictValue())) {
                                vo.setOrderStatusName(dictData.getDictLabel());
                                break;
                            }
                        }
                    }
                    if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(vo.getOrderStatus())
                            || EnumUtil.FlowNode.DUPLICATION.getCode().equals(vo.getOrderStatus())
                            || EnumUtil.FlowNode.END.getCode().equals(vo.getOrderStatus())) {
                        vo.setOrderStatusName(EnumUtil.FlowNode.END.getName());
                    }
                    // 设置重要等级名称
                    if (!CollectionUtils.isEmpty(importanceLevelEnum)) {
                        importanceLevelEnum.forEach(item -> {
                            if (item.getDictValue().equals(vo.getImportanceLevel())) {
                                vo.setImportanceLevelName(item.getDictLabel());
                            }
                        });
                    }
                    //置信度 取最大置信度展示  -- 新增综合置信度，若存在综合置信度则展示综合置信度
                    if (!StringUtils.isEmpty(vo.getCompConfidenceLevel())) {
                        vo.setConfidenceLevel(vo.getCompConfidenceLevel());
                    } else {
                        String confidenceLevel = vo.getConfidenceLevel();
                        if (!StringUtils.isEmpty(confidenceLevel)) {
                            String[] confidenceLevelList = confidenceLevel.split(Constants.COMMA);
                            Optional<String> confidenceLevelOpt = Arrays.stream(confidenceLevelList).max(String::compareTo);
                            if (confidenceLevelOpt.isPresent()) {
                                String confidenceLevelNew = confidenceLevelOpt.get();
                                vo.setConfidenceLevel(confidenceLevelNew);
                            }
                        }
                    }
                }
            }
            tableDataInfo.setRows(resultList);
            tableDataInfo.setTotal(allCount);
        } catch (Exception e) {
            log.error("getOrderListForDuplication查询失败：" + e.getMessage());
        }
        return tableDataInfo;
    }

    /**
     * 下一环节是处置环节时，处理单表插入数据
     *
     * @param alarmOrderVO
     * @param insertRecored
     * @return
     */
    private void handleProcessOrder(SendAlarmOrderDTO alarmOrderVO, AlarmOrderRecordVO updateRecored, AlarmOrderRecordVO insertRecored) {

        List<String> processOrderIds = new ArrayList<>();
        if (EnumUtil.FlowNode.PENDING_HANDLE.getCode().equals(alarmOrderVO.getRealLinkType())) {
            if (!StringUtils.isEmpty(alarmOrderVO.getSendRemark())) {
                updateRecored.setRemark(alarmOrderVO.getSendRemark());
            }
            List<ProcessOrderDTO> processOrderDTOS = new ArrayList<>();
            List<ProcessDTO> processDTOS = alarmOrderVO.getProcessDTOList();
            // 插入处理单表
            ProcessOrderDTO processOrderVO = new ProcessOrderDTO();
            processOrderVO.setProcessOrderId(warningInterceptMapper.getUUID());
            processOrderVO.setCurrenAreaId("");
            processOrderVO.setDeleteFlag(Constants.VALID_STATE);
            processOrderVO.setOrderId(alarmOrderVO.getOrderId());
            processOrderVO.setWarningOrderId(alarmOrderVO.getWarningOrderId());
            processOrderVO.setOrderStatus(Constants.VALID_STATE);
            processOrderVO.setSendType(Constants.VALID_STATE);
            processOrderDTOS.add(processOrderVO);
            processOrderIds.add(processOrderVO.getProcessOrderId());

            if (!CollectionUtils.isEmpty(processOrderDTOS)) {
                // 插入数据
                processOrderMapper.insertProcessOrderList(processOrderDTOS);
                insertRecored.setProcessOrderId(processOrderVO.getProcessOrderId());
            }
        }
    }

    /**
     * 当前环节是处置环节时，更新vm_process_order_t表数据,vm_process_approve_t表插入数据，如果会签插入多条，否则插入一条
     *
     * @param alarmOrderVO
     * @param updateRecored
     * @return
     */
    private void handlePendingHandle(SendAlarmOrderDTO alarmOrderVO, AlarmOrderRecordVO updateRecored, AlarmOrderRecordVO insertRecored) {
        String userId = updateRecored.getProcessId();
        StringBuffer stringBuffer = new StringBuffer();
        if (!CollectionUtils.isEmpty(alarmOrderVO.getProcessOrderVOList())) {
            //更新处理单表
            List<ProcessOrderDTO> list = alarmOrderVO.getProcessOrderVOList();
            ProcessOrderDTO po = list.get(0);
            // 处理意见
            updateRecored.setRemark(po.getProcessOpinion());
            po.setOrderId(alarmOrderVO.getOrderId());
            po.setWarningOrderId(alarmOrderVO.getWarningOrderId());

            ProcessOrderVO processOrderVO = processOrderMapper.getProcessOrderPendding(po);
            if (processOrderVO == null) {
                // 插入处理单表
                List<ProcessOrderDTO> processOrderDTOS = new ArrayList<>();
                ProcessOrderDTO processOrderVONew = new ProcessOrderDTO();
                processOrderVONew.setProcessOrderId(warningInterceptMapper.getUUID());
                processOrderVONew.setCurrenAreaId("");
                processOrderVONew.setDeleteFlag(Constants.VALID_STATE);
                processOrderVONew.setOrderId(alarmOrderVO.getOrderId());
                processOrderVONew.setWarningOrderId(alarmOrderVO.getWarningOrderId());
                processOrderVONew.setSendType(Constants.VALID_STATE);
                //1:处理完成
                processOrderVONew.setOrderStatus(po.getOrderStatus());
                processOrderVONew.setProcessOpinion(po.getProcessOpinion());
                processOrderVONew.setProcessId(userId);
                // 增加语音
                processOrderVONew.setVoice(alarmOrderVO.getVoice());
                if (!StringUtils.isEmpty(po.getExt1())) {
                    processOrderVONew.setExt1(po.getExt1());
                }
                processOrderDTOS.add(processOrderVONew);

                if (!CollectionUtils.isEmpty(processOrderDTOS)) {
                    // 插入数据
                    processOrderMapper.insertProcessOrderList(processOrderDTOS);
                    updateRecored.setProcessOrderId(processOrderVONew.getProcessOrderId());
                }
                processOrderVO = new ProcessOrderVO();
                processOrderVO.setProcessOrderId(processOrderVONew.getProcessOrderId());
            } else {
                //1:处理完成
                processOrderVO.setOrderStatus(po.getOrderStatus());
                processOrderVO.setProcessOpinion(po.getProcessOpinion());
                processOrderVO.setProcessId(userId);
                // 增加语音
                processOrderVO.setVoice(alarmOrderVO.getVoice());
                if (!StringUtils.isEmpty(po.getExt1())) {
                    processOrderVO.setExt1(po.getExt1());
                }
                processOrderMapper.updateProcessOrder(processOrderVO);
            }

            //插入处理照片视频表
            if (!CollectionUtils.isEmpty(po.getProcessImgList()) && (po.getProcessImgList()).size() > 0) {
                List listImg = po.getProcessImgList();
                for (int i = 0; i < listImg.size(); i++) {
                    Map map = (Map) listImg.get(i);
                    map.put(Constants.PARAM_PROCESS_ORDER_ID, processOrderVO.getProcessOrderId());
                    processOrderMapper.insertPhotoOrVideo(map);
                }
            }
            //插入审核单表
            List<ProcessDTO> processDTOS = alarmOrderVO.getProcessDTOList();
            List<ProcessApproveDTO> processApproveDTOS = new ArrayList<>();
            ProcessApproveDTO orderApproveVO = new ProcessApproveDTO();
            orderApproveVO.setApproveId(warningInterceptMapper.getUUID());
            orderApproveVO.setOrderId(alarmOrderVO.getOrderId());
            orderApproveVO.setProcessOrderId(processOrderVO.getProcessOrderId());
            //待审核
            orderApproveVO.setApproveStatus(Constants.YES_FLAG);
            orderApproveVO.setDeleteFlag(Constants.DELETE_FLAG);
            processApproveDTOS.add(orderApproveVO);
            stringBuffer.append(orderApproveVO.getApproveId());
            if (stringBuffer.toString().endsWith(Constants.COMMA)) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }
            insertRecored.setApproveOrderId(stringBuffer.toString());
            processApproveMapper.insertOrderApproveList(processApproveDTOS);
        }
    }

    /**
     * 当前环节是核实环节
     *
     * @param alarmOrderVO
     * @param updateRecored
     */
    private void handlePendingCheck(SendAlarmOrderDTO alarmOrderVO, AlarmOrderRecordVO updateRecored) {
        String userId = updateRecored.getProcessId();
        if (!CollectionUtils.isEmpty(alarmOrderVO.getOrderApproveVOList())) {
            List<ProcessApproveDTO> list = alarmOrderVO.getOrderApproveVOList();
            ProcessApproveDTO oa = list.get(0);
            updateRecored.setRemark(oa.getApproveResult());

            oa.setOrderId(alarmOrderVO.getOrderId());
            //如果是会签根据处理人查询数据
            oa.setIsSign(alarmOrderVO.getIsSign());
            oa.setApprovePerson(userId);

            ProcessApproveDTO orderApproveVO = processApproveMapper.getOrderApprove(oa);
            if (orderApproveVO != null) {
                List<SysDictDataResponse> sysDictDataResponseList = this.getEnumList(Integer.valueOf(Constants.APPROVE_STATUS_2));
                for (SysDictDataResponse sysDictDataResponse : sysDictDataResponseList) {
                    if (sysDictDataResponse.getDictValue().equals(orderApproveVO.getApproveStatus())) {
                        orderApproveVO.setApproveStatusName(sysDictDataResponse.getDictLabel());
                        break;
                    }
                }
                orderApproveVO.setApprovePerson(userId);
                //审核状态 0:审批失败 1:待审批 2:审批通过
                orderApproveVO.setApproveStatus(oa.getApproveStatus());
                orderApproveVO.setApproveResult(oa.getApproveResult());
                processApproveMapper.updateOrderApprove(orderApproveVO);
            } else {
                //无数据则新增数据
                List<ProcessApproveDTO> approveVOList = new ArrayList<>();
                ProcessApproveDTO approveVO = new ProcessApproveDTO();
                approveVO.setApproveId(warningInterceptMapper.getUUID());
                updateRecored.setApproveOrderId(approveVO.getApproveId());
                approveVO.setOrderId(alarmOrderVO.getOrderId());
                approveVO.setApprovePerson(userId);
                //审核状态 0:审批失败 1:待审批 2:审批通过
                approveVO.setApproveStatus(oa.getApproveStatus());
                approveVO.setApproveResult(oa.getApproveResult());
                approveVO.setDeleteFlag(Constants.DELETE_FLAG);
                approveVOList.add(approveVO);
                processApproveMapper.insertOrderApproveList(approveVOList);
            }
        }
    }

    public OrderInfoVO getStaticDeviceRel(OrderInfoVO orderInfoVO) {
        List<TenantDeviceRelDTO> tenantDeviceRelDTOList = new ArrayList<>();
        TenantDeviceRelDTO tenantDeviceRelDTO = new TenantDeviceRelDTO();
        tenantDeviceRelDTO.setDeviceCode(orderInfoVO.getDeviceCode());
        tenantDeviceRelDTO.setTenantId(orderInfoVO.getTenantId());
        tenantDeviceRelDTO.setChannelCode(orderInfoVO.getChannelCode());
        tenantDeviceRelDTOList.add(tenantDeviceRelDTO);
        List<TenantDeviceEventRelVO> tenantDeviceEventRelVOList = deviceRepository.getDeviceRel(tenantDeviceRelDTOList);
        if (!CollectionUtils.isEmpty(tenantDeviceEventRelVOList)) {
            TenantDeviceEventRelVO tenantDeviceEventRelVO = tenantDeviceEventRelVOList.get(0);
            if (tenantDeviceEventRelVO != null) {
                if (!StringUtils.isEmpty(tenantDeviceEventRelVO.getDeviceName())) {
                    orderInfoVO.setDeviceName(tenantDeviceEventRelVO.getDeviceName());
                }
                if (!StringUtils.isEmpty(tenantDeviceEventRelVO.getChannelName())) {
                    orderInfoVO.setChannelName(tenantDeviceEventRelVO.getChannelName());
                }
            }
        }
        return orderInfoVO;
    }

    /**
     * 获取设备、通道别名
     *
     * @param orderInfoVO
     * @return
     */
    @Override
    public void getDeviceRel(OrderInfoVO orderInfoVO) {
        List<TenantDeviceRelDTO> tenantDeviceRelDTOList = new ArrayList<>();
        TenantDeviceRelDTO tenantDeviceRelDTO = new TenantDeviceRelDTO();
        tenantDeviceRelDTO.setDeviceCode(orderInfoVO.getDeviceCode());
        tenantDeviceRelDTO.setTenantId(orderInfoVO.getTenantId());
        tenantDeviceRelDTO.setChannelCode(orderInfoVO.getChannelCode());
        tenantDeviceRelDTOList.add(tenantDeviceRelDTO);
        List<TenantDeviceEventRelVO> tenantDeviceEventRelVOList = deviceRepository.getDeviceRel(tenantDeviceRelDTOList);
        if (!CollectionUtils.isEmpty(tenantDeviceEventRelVOList)) {
            TenantDeviceEventRelVO tenantDeviceEventRelVO = tenantDeviceEventRelVOList.get(0);
            if (tenantDeviceEventRelVO != null) {
                if (!StringUtils.isEmpty(tenantDeviceEventRelVO.getDeviceName())) {
                    orderInfoVO.setDeviceName(tenantDeviceEventRelVO.getDeviceName());
                }
                if (!StringUtils.isEmpty(tenantDeviceEventRelVO.getChannelName())) {
                    orderInfoVO.setChannelName(tenantDeviceEventRelVO.getChannelName());
                }
            }
        }

    }

    @Override
//    @TenantForDataScope(dbTypeAlias = "db1", personAlias = "dataTable.town_code", deptAlias = "dataTable.group_id",
//            tenantAlias = "dataTable.tenant_id", deviceAlias = "dataTable.device_code", dataColumnAlias = "dataTable.warning_source", dataAlias = "1,2,3,8,9,10")
//    @WarningOrderDataScope(userAlias = "dataTable", userColumn = "process_id")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getAlarmListForBigScreen(BigScreenOrderListDTO req) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getUser();
        //来源：0铁塔 2租户
        String source = sysUser.getUserType();
        String userId = sysService.getLoginUserId();  //获取当前登录用户id(切换后取对应账号的id)
        req.setUserId(userId);
        req.setTenantId(sysUser.getTenantId());
        req.setAdmin(Constants.ONE.equals(sysUser.getIsTenantAdmin()) || Constants.VALID_STATE.equals(source));
        req.setSource(source);
        if (Constants.NINE.equals(req.getOrderStatus())) {
            req.setEnableAcceptanceMode(Constants.ONE);
            req.setOrderStatus("");
        }
        if (!CollectionUtils.isEmpty(req.getOrderStatusList()) && req.getOrderStatusList().contains(Constants.NINE)) {
            req.setEnableAcceptanceMode(Constants.ONE);
        }
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if ((!CollUtil.isEmpty(req.getOrderStatusList()) && req.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode())) || (StringUtil.isNotEmpty(req.getOrderStatus()) && EnumUtil.FlowNode.END.getCode().contains(req.getOrderStatus()))) {
            List<String> orderStatusList = new ArrayList<String>();
            if ((StringUtil.isNotEmpty(req.getOrderStatus())) && (req.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))) {
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            } else {
                orderStatusList = req.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            req.setOrderStatusList(orderStatusList);
        }
        if (!req.isAdmin() && StringUtils.isEmpty(req.getSearchType())) {
            // 不是管理员，需要按照角色查询对应的待办告警
            req.setSearchType(Constants.ONE);
        }
        Long[] roleIds = sysUser.getRoleIds();
        List<String> roleIdList = new ArrayList();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long id : roleIds) {
                roleIdList.add(String.valueOf(id));
            }
        }
        // 待办查询 根据当前用户的角色、id和环节配置表的处理角色和用户关联，取环节配置id和环节id字段做过滤
        if (Constants.SEARCH_TYPE_1.equals(req.getSearchType())) {
            //根据用户id和角色id查询流程环节信息
            String flowLinkIds = flowLinkService.getFlowLinkAttrByUser(sysUser, roleIdList);
            if (!StringUtils.isEmpty(flowLinkIds)) {
                req.setFlowLinkIds(flowLinkIds);
            }
        }
        //告警标签
        List<AlarmLableRelDTO> alarmLableRelDTOList;
        alarmLableRelDTOList = alarmLableRelRepository.getAlarmLableByIds(req.getLableIdList(), req.getWarningOrderId());
        List<AlarmLableRelDTO> alarmLableAllList = alarmLableRelRepository.getAlarmLableByIds(new ArrayList<>(), "");
        if (CollectionUtils.isEmpty(alarmLableRelDTOList) && !CollectionUtils.isEmpty(req.getLableIdList())) {
            return tableDataInfo;
        }
        // 分页
        if (req.getPageNum() == null) {
            req.setPageNum(Constants.PAGE_NUM);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(Constants.PAGE_SIZE);
        }

        Date endDate = null;
        Date startDate = null;

        //add by hued 20211129 R-ZBZB-202111180812188 增加支持查询今日、3日内、7日内、更多时间段告警事件 start
        String webStartDate = req.getWebStartDate();
        String webEndDate = req.getWebEndDate();
        if (!StringUtils.isEmpty(webStartDate) && !StringUtils.isEmpty(webEndDate)) {
            startDate = DateUtils.stringToDateYMDHMS(webStartDate);
            endDate = DateUtils.string2Date(webEndDate + hms);
        }
        req.setWebStartDate(DateUtils.dateToStringYMD(startDate));
        req.setWebEndDate(DateUtils.dateToStringNoMS(endDate));
        //add by hued 20211129 R-ZBZB-202111180812188 增加支持查询今日、3日内、7日内、更多时间段告警事件 end

        //查询结果集
        Map map = this.getResult(req, startDate, endDate, alarmLableRelDTOList, alarmLableAllList);
        List<Map> resultList = (List<Map>) map.get(RESULT_LIST);

        tableDataInfo.setRows(resultList);
        tableDataInfo.setTotal((int) map.get(ALL_COUNT));
        return tableDataInfo;
    }

    /**
     * 根据租户按照经纬度递归查询告警信息
     *
     * @param dto
     * @param request
     * @return
     */
    @Override
//    @TenantForDataScope(dbTypeAlias = "db1", personAlias = "dataTable.town_code",
//            tenantAlias = "dataTable.tenant_id", deviceAlias = "dataTable.device_code", dataColumnAlias = "dataTable.warning_source", dataAlias = "1,2,3,8,9,10")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public Result searchOrderNumByLongitude(SearchOrderDTO dto, ServletRequest request) {
        log.info("searchOrderByLongitude请求参数为：" + JSON.toJSONString(dto));
        String cityId = null;

        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (CommonConstant.USER_TYPE_TIETA.equals(loginUser.getUser().getUserType())) {
            String token = null;
            // 获取cookie中的token
            HttpServletRequest req = (HttpServletRequest) request;
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (Constants.AUTH_TOKEN.equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
            }
            String redisCityId = null;
            boolean judge = !NullProcessUtil.isNull(dto.getCityId());
            // 根据经纬度查询行政区划
            if (!judge) {
                if (NullProcessUtil.isNull(dto.getLatitude())
                        || NullProcessUtil.isNull(dto.getLongitude())) {
                    return Result.fail(Constants.MESS_1);
                }
                PoiContraryGeocodingQueryReq queryReq = new PoiContraryGeocodingQueryReq();
                queryReq.setLocation(String.format(Constants.FORMAT_S, dto.getLongitude(), dto.getLatitude()));
                log.info("逆地理编码查询接口poiContraryGeocodingQuery req:{}" + JSONObject.toJSONString(queryReq));
                Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(queryReq);
                log.info("逆地理编码查询接口poiContraryGeocodingQuery resp:{}" + JSONObject.toJSONString(result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                    if (!CollectionUtils.isEmpty(result.getData())) {
                        PoiContraryGeocodingQueryRes data = result.getData().get(0);
                        if (data.getRegion() != null) {
                            Region region = data.getRegion();
                            if (!StringUtils.isEmpty(region.getCityCode())) {
                                dto.setCityId(region.getCityCode());
                            }
                        }
                    }
                    redisCityId = RedisUtil.get(Constants.KEY_ORDER + token, RedisDBType.PRIMARY);
                    RedisUtil.set(Constants.KEY_ORDER + token, dto.getCityId(), RedisDBType.PRIMARY);
                    RedisUtil.expire(Constants.KEY_ORDER + token, Constants.REDIS_EXPIRE, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                }
            }
            cityId = dto.getCityId();
            if (StringUtils.isEmpty(cityId)) {
                return Result.fail(Constants.MESS_2);
            }

            if (judge || NullProcessUtil.isNull(redisCityId) || !redisCityId.equals(cityId)) {

            } else {
                return Result.success("");
            }
        }
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if (!CollUtil.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(Constants.SIX)) {
            List<String> orderStatusList = new ArrayList<String>();
            orderStatusList = dto.getOrderStatusList();
            orderStatusList.add(Constants.SEVEN);
            orderStatusList.add(Constants.ZERO);
            dto.setOrderStatusList(orderStatusList);
        }
        // 场景（摄像机标签ID List）
        if (CollUtil.isNotEmpty(dto.getScene())) {
            List<AlarmLableRelDTO> alarmLableRelDTOList = alarmLableRelRepository.getAlarmLableByIds(dto.getScene(), null);
            List<String> waringOrderIds = alarmLableRelDTOList.stream().map(AlarmLableRelDTO::getWaringOrderId).collect(Collectors.toList());
            log.info("根据摄像机标签ID: {}, List查询对应的告警单ID List为：{}", dto.getScene(), waringOrderIds);
            dto.setWarningOrderIdList(waringOrderIds);
        }
        // 所选组织ID List
        if (!CollUtil.isEmpty(dto.getOrganization())) {
            SysDept dept = new SysDept();
            dept.setDeptIdList(dto.getOrganization());
            dept.setTenantId(dto.getTenantId());
            dept.setIndustryCode(dto.getIndustryCode());
            List<SysDeptDeviceVO> cameras = sysDeptDeviceMapper.getCameras(dept);
            log.info("所选组织ID List: {}, 获取组织下所有摄像机设备编码为：{}", dto.getOrganization(), cameras);
            List<String> deviceCodes = new ArrayList<>();
            //摄像机设备编码集合
            deviceCodes.addAll(cameras.stream().map(SysDeptDeviceVO::getDeviceCode).collect(Collectors.toList()));
            dto.setDeviceCode(deviceCodes);
        }
        Map<String, Object> resultList = new HashMap<>();
        if ((CollUtil.isNotEmpty(dto.getScene()) && CollUtil.isEmpty(dto.getWarningOrderIdList())) ||
                CollUtil.isNotEmpty(dto.getOrganization()) && CollUtil.isEmpty(dto.getDeviceCode())) {
            return Result.data(resultList);
        }
        List<Map> siteTmpList = new ArrayList();
        Date endDate;
        Date startDate;
        String webStartDate = dto.getWebStartDate();
        String webEndDate = dto.getWebEndDate();
        if (!StringUtils.isEmpty(webStartDate) && !StringUtils.isEmpty(webEndDate)) {
            startDate = DateUtils.stringToDateYMDHMS(webStartDate);
            endDate = DateUtils.string2Date(webEndDate + Constants.HMS);
        } else {
            endDate = new Date();
            startDate = new Date();
        }
        dto.setWebStartDate(DateUtil.format(DateUtil.beginOfDay(startDate), Constants.DATE_YMDHMS));
        dto.setWebEndDate(DateUtils.dateToStringNoMS(endDate));
        // add by hued 20211129 R-ZBZB-202111180812188 增加支持查询今日、3日内、7日内、更多时间段告警事件 end

        boolean stop = false;
        if (!CollectionUtils.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(Constants.NINE)) {
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        // 从最后一个表开始向前获取
        while (!stop) {
            String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
            dto.setTableName(tableName);
            // 累计总数
            siteTmpList.addAll(warningOrderMapper.searchOrderByLongitude(dto));
            stop = DateUtils.sameMouth(startDate, endDate);
            // 转到上月
            String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
            endDate = DateUtils.stringToDateYM(lastMonth);
        }

        /*for (Map result : siteTmpList) {
            if (StringUtils.isEmpty(MapUtils.getString(result, Constants.PARAM_LONGITUDE)) || StringUtils.isEmpty(MapUtils.getString(result, Constants.PARAM_LATITUDE))
                    || "NaN".equals(MapUtils.getString(result, Constants.PARAM_LONGITUDE)) || "NaN".equals(MapUtils.getString(result, Constants.PARAM_LATITUDE))) {
                continue;
            }
            Map resultMap = new HashMap();
            long cnt = 0;
            for (Map resultCopy : siteTmpList) {
                if ((result.get(Constants.PARAM_LONGITUDE)).equals(resultCopy.get(Constants.PARAM_LONGITUDE))
                        && (result.get(Constants.PARAM_LATITUDE)).equals(resultCopy.get(Constants.PARAM_LATITUDE))) {
                    cnt += (long) resultCopy.get(Constants.NUM);
                }
            }
            resultMap.put(Constants.PARAM_LONGITUDE, result.get(Constants.PARAM_LONGITUDE));
            resultMap.put(Constants.PARAM_LATITUDE, result.get(Constants.PARAM_LATITUDE));
            // 返回最新的一条告警编码
            resultMap.put(Constants.PARAM_WARNING_ORDER_ID, result.get(Constants.INDEXFIELD_WARNING_ORDER_ID));
            resultMap.put(Constants.NUM, cnt);
            siteList.add(resultMap);
        }*/
        // 告警等级枚举
        List<SysDictDataResponse> importanceLevelEnum = systemFeignClient.dictDataType(ALARM_LEVEL).getData();
        Map<String, String> dictMap = importanceLevelEnum.stream()
                .collect(Collectors.toMap(
                        SysDictDataResponse::getDictValue,
                        SysDictDataResponse::getDictLabel
                ));
        Map<List<Object>, Map<String, Object>> resultMapMap = siteTmpList.stream()
                .filter(result ->
                        !"NaN".equals(MapUtils.getString(result, Constants.PARAM_LONGITUDE)) &&
                                !"NaN".equals(MapUtils.getString(result, Constants.PARAM_LATITUDE))
                )
                .collect(Collectors.groupingBy(
                        result -> Arrays.asList(
                                result.get(Constants.PARAM_LONGITUDE),
                                result.get(Constants.PARAM_LATITUDE)
                        ),
                        Collectors.collectingAndThen(Collectors.toList(), results -> {
                            Map<String, Object> resultMap = new HashMap<>();
                            resultMap.put(Constants.PARAM_LONGITUDE, results.get(0).get(Constants.PARAM_LONGITUDE));
                            resultMap.put(Constants.PARAM_LATITUDE, results.get(0).get(Constants.PARAM_LATITUDE));
                            resultMap.put(PARAM_IMPORTANCE_LEVEL_NAME, dictMap.get(results.get(0).get(PARAM_IMPORTANCE_LEVEL_DB)));
                            resultMap.put(Constants.PARAM_WARNING_ORDER_ID, results.get(0).get(Constants.RES_WARNING_ORDER_ID));
                            resultMap.put(Constants.NUM, results.stream().mapToLong(result -> (long) result.get(Constants.NUM)).sum());
                            return resultMap;
                        })
                ));
        List<Map> siteList = new ArrayList<>(resultMapMap.values());
        resultList.put(Constants.PARAM_LIST, siteList);
        resultList.put(Constants.CITY_ID, cityId);
        return Result.data(resultList);
    }

    /**
     * 获取告警列表-告警事件大屏左侧查询
     *
     * @param dto
     * @return
     */
    @Override
//    @TenantForDataScope(dbTypeAlias = "db1", personAlias = "dataTable.town_code", deptAlias = "dataTable.group_id",
//            tenantAlias = "dataTable.tenant_id", deviceAlias = "dataTable.device_code", dataColumnAlias = "dataTable.warning_source", dataAlias = "1,2,3,8,9,10")
//    @WarningOrderDataScope(userAlias = "dataTable", userColumn = "process_id")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getOrderListForBigScreen(BigScreenOrderListDTO dto) {
        long start = System.currentTimeMillis();
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getUser();
        //查询全部
        dto.setSearchType(Constants.JUDGE_RESULT_0);
        dto.setUserId(sysUser.getUserId());
        dto.setTenantId(sysUser.getTenantId());
        //0：铁塔  2：租户
        dto.setSource(sysUser.getUserType());

        // 分页
        if (dto.getPageNum() == null) {
            dto.setPageNum(Constants.PAGE_NUM);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(Constants.PAGE_SIZE);
        }

        //查询开始结束时间
        Date endDate = new Date();
        //查询24小时内的告警
        Date startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDate());

        //查询结果集
        List<AlarmLableRelDTO> alarmLableRelDTOList = new ArrayList<>();
        Map map = this.getResult(dto, startDate, endDate, alarmLableRelDTOList, alarmLableRelDTOList);

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setRows((List<Map>) map.get(RESULT_LIST));
        tableDataInfo.setTotal((int) map.get(ALL_COUNT));
        log.info("获取告警列表-告警事件大屏左侧查询耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));

        return tableDataInfo;
    }

    /**
     * 根据时间范围获取告警事件总数（提供给国土行业，用于告警热力图展示）
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getAlarmListCount(BigScreenOrderListDTO dto) {
        dto.setPageNum(1);
        dto.setPageSize(50);
        //查询结果集
        Map map = this.getOrderResult(dto);
        // 总数
        int allCount = Integer.parseInt(map.get(ALL_COUNT).toString());
        //从字典表获取最大告警数量
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(Constants.WARNING_ORDER_NUMBER);
        List<SysDictDataResponse> dictList = sysResult.getData();
        if (allCount >= Integer.parseInt(dictList.get(0).getDictValue())) {
            allCount = Integer.parseInt(dictList.get(0).getDictValue());
        }
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setTotal(allCount);
        return tableDataInfo;
    }

    /**
     * 根据设备编码集合查询设备是否有未完结告警（提供给浩鲸，用于地图打点）
     *
     * @param dto
     * @return
     */
    @Override
    public Result<List<OrderCompleteVO>> getOrderNumByDeviceCode(AlarmOrderParmaDto dto) {
        log.info("getOrderNumByDeviceCode-查询设备是否有未完结告警入参-req:{}", JSON.toJSONString(dto));

        List<OrderCompleteVO> resultList = new ArrayList<>();
        try {
            //生成表名，例如：vm_device_unfinished_warnings_202501
            String yyyyMM = DateUtils.date2Str(new Date(), Constants.YY_MM);
            String tableName = "vm_device_unfinished_warnings_" + yyyyMM;

            //构建查询条件
            Query query = new Query();
            if (!StringUtils.isEmpty(dto.getTenantId())) {
                query.addCriteria(Criteria.where(RES_TENANT_ID).is(dto.getTenantId()));
            }
            if (!StringUtils.isEmpty(dto.getIndustryCode())) {
                query.addCriteria(Criteria.where(PARAM_INDUSTRY_CODE_FIELD).is(dto.getIndustryCode()));
            }
            if (!CollectionUtils.isEmpty(dto.getDeviceCodes())) {
                query.addCriteria(Criteria.where(RES_DEVICE_CODE).in(dto.getDeviceCodes()));
            }

            List<VmDeviceStatisticsVO> queryResult = mongoTemplate.find(query, VmDeviceStatisticsVO.class, tableName);
            log.info("getOrderNumByDeviceCode-查询表: {}, 返回结果resp: {}", tableName, JSON.toJSONString(queryResult));

            queryResult.forEach(statisticsVO -> resultList.add(
                    new OrderCompleteVO(
                            statisticsVO.getDeviceCode(),
                            statisticsVO.getOrderUnCompleteNum() != null && statisticsVO.getOrderUnCompleteNum() > 0 ? Constants.ONE : Constants.ZERO
                    )
            ));
        } catch (Exception e) {
            log.error("getOrderNumByDeviceCode查询失败：{}", e);
        }
        return Result.data(resultList);
    }

    /**
     * 根据时间范围、分页获取经纬度列表 （提供给国土行业，用于告警热力图展示）
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getOrderLongitudeList(BigScreenOrderListDTO dto) {
        List<Map> longitudeList = new ArrayList();
        //查询结果集
        Map map = this.getOrderResult(dto);
        List<BigScreenOrderListVO> orderListVOList = (List<BigScreenOrderListVO>) map.get(RESULT_LIST);
        if (!CollectionUtils.isEmpty(orderListVOList)) {
            orderListVOList.forEach(item -> {
                Map m = new HashMap();
                m.put(Constants.PARAM_WARNING_ORDER_ID, item.getOrderId());
                m.put(Constants.PARAM_LONGITUDE, item.getLongitude());
                m.put(Constants.PARAM_LATITUDE, item.getLatitude());
                longitudeList.add(m);
            });
        }
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setTotal((int) map.get(ALL_COUNT));
        tableDataInfo.setRows(longitudeList);
        return tableDataInfo;
    }

    /**
     * 查询结果集
     *
     * @param dto
     * @return
     */
    private Map getOrderResult(BigScreenOrderListDTO dto) {
        List<BigScreenOrderListVO> resultList = new ArrayList<>();
        Map map = new HashMap();
        Date startDate;
        Date endDate;
        String webStartDate = dto.getWebStartDate();
        String webEndDate = dto.getWebEndDate();
        if (!StringUtils.isEmpty(webStartDate) && !StringUtils.isEmpty(webEndDate)) {
            startDate = DateUtils.stringToDateYMDHMS(webStartDate);
            endDate = DateUtils.string2Date(webEndDate + hms);
        } else {
            endDate = new Date();
            startDate = new Date();
        }
        dto.setWebStartDate(DateUtils.dateToStringNoMS(startDate));
        dto.setWebEndDate(DateUtils.dateToStringNoMS(endDate));
        if (Constants.NINE.equals(dto.getOrderStatus())) {
            dto.setEnableAcceptanceMode(Constants.ONE);
            dto.setOrderStatus("");
        }
        if (!CollectionUtils.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(Constants.NINE)) {
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if ((!CollUtil.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode())) || (StringUtil.isNotEmpty(dto.getOrderStatus()) && EnumUtil.FlowNode.END.getCode().contains(dto.getOrderStatus()))) {
            List<String> orderStatusList = new ArrayList<String>();
            if ((StringUtil.isNotEmpty(dto.getOrderStatus())) && (dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))) {
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            } else {
                orderStatusList = dto.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            dto.setOrderStatusList(orderStatusList);
        }
        //总数
        int allCount = 0;
        // 需要查询的个数
        int limit = dto.getPageSize();
        // 分页开始的数
        int offset = (dto.getPageNum() - 1) * limit;
        // 当前表查询开始的数
        int tableStartNum = 0;
        int count = 0;
        boolean stop = false;
        String tableName;
        // 从最后一个表开始向前获取
        while (!stop) {
            tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
            dto.setTableName(tableName);
            log.info("大屏查询告警入参：{}", JSON.toJSONString(dto));
            count = warningOrderMapper.getOrderListForBigScreenCount(dto);
            // 累计总数
            allCount += count;
            if (allCount > offset && count > 0 && limit > 0) {
                tableStartNum = offset - (allCount - count);
                if (tableStartNum < 0) {
                    tableStartNum = 0;
                }
                if (count > limit) {
                    dto.setLimit(limit);
                } else {
                    dto.setLimit(count);
                }
                dto.setOffset(tableStartNum);
                log.info("大屏查询告警入参2：{}", JSON.toJSONString(dto));
                List<BigScreenOrderListVO> queryResult = warningOrderMapper.getOrderListForBigScreen(dto);
                resultList.addAll(queryResult);
                limit = limit - queryResult.size();
            }
            stop = DateUtils.sameMouth(startDate, endDate);
            // 转到上月
            String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
            endDate = DateUtils.stringToDateYM(lastMonth);
        }
        map.put(ALL_COUNT, allCount);
        map.put(RESULT_LIST, resultList);
        return map;
    }

    /**
     * APP周边检索
     *
     * @param dto
     * @return
     */
    @Override
//    @TenantForDataScope(dbTypeAlias = "db1", personAlias = "dataTable.town_code", deptAlias = "dataTable.group_id",
//            tenantAlias = "dataTable.tenant_id", deviceAlias = "dataTable.device_code", dataColumnAlias = "dataTable.warning_source", dataAlias = "1,2,3,8,9,10")
//    @WarningOrderDataScope(userAlias = "dataTable", userColumn = "process_id")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", processIdColumn = "dataTable.process_id", typeValueColumn = "dataTable.warning_type_id")
    public List<WarningOrderListVO> getOrderListByPeriphery(AlarmOrderDTO dto) {

        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();

        String userId = user.getUserId();
        Long[] roleIds = loginUser.getUser().getRoleIds();
        dto.setAdmin(OrderConstants.IS_TENANT_ADMIN.equals(user.getIsTenantAdmin()));
        List<String> roleIdList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleIdList.add(String.valueOf(roleId));
            }
        }
        dto.setRoleIdList(roleIdList);

        // 查询周边搜索半径
        String radius = Constants.DEFAULT_RADIUS;
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(Constants.RADIUS);
        List<SysDictDataResponse> dictList = sysResult.getData();
        if (!CollectionUtils.isEmpty(dictList)) {
            radius = dictList.get(0).getDictValue();
        }
        dto.setRadius(radius);
        //默认查询14天内数据
        long day = 13;
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.RADIUS_ALARM_DAY);
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            day = Long.valueOf(suppressDictData.getData().get(0).getDictValue());
        }
        if (!dto.isAdmin() && !dto.getIsLeader()) {
            // 不是管理员，需要按照角色查询对应的待办告警
            dto.setSearchType(Constants.ONE);
        }
        //根据用户id和角色id查询流程环节信息
        SysUser sysUser = loginUser.getUser();
        String flowLinkIds = "";
        this.handleOrderReq(dto, flowLinkIds);
        dto.setUserId(sysUser.getUserId());
        dto.setOrgIds(CommonUtils.getOrgIds(user.getDeptId()));
        List<WarningOrderListVO> resultList = new ArrayList<>();

        // 总数
        int allCount = 0;
        // 20211219 zzh 不查询完结状态告警
        List<String> orderStatuses = new ArrayList<>();
        orderStatuses.add(EnumUtil.FlowNode.END.getCode());
        orderStatuses.add(EnumUtil.FlowNode.DUPLICATION.getCode());
        orderStatuses.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
        dto.setNotInOrderStatus(orderStatuses);

        //是否是管理员
        dto.setAdmin((CommonUtils.ifLeaderRole(roleIds) || CommonConstant.USER_TYPE_TIETA.equals(user.getUserType())));
        //是否是领导
        dto.setIsLeader(CommonUtils.ifLeaderRole(roleIds));


        // 查询开始结束时间
        // 默认查询近30天的告警
        Date startDate = DateUtils.getDateAfter(new Date(), -14);
        Date endDate = null;
        // 需要查询的个数
        int limit = dto.getPageSize();
        // 分页开始的数
        int offset = 0;
        // 当前表查询开始的数
        int tableStartNum = 0;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDateForDay(day));
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 减去14天
            LocalDate fourteenDaysAgo = currentDate.minusDays(day);
            dto.setAlarmTimeStart(Date.from(fourteenDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            startDate = dto.getAlarmTimeStart();
            int sd = Integer.parseInt(DateUtils.dateToStringYM(startDate));
            if (sd < Integer.parseInt(TableUtil.getMinTableDate())) {
                startDate = DateUtils.stringToDateYM(TableUtil.getMinTableDate());
            }
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
            dto.setAlarmTimeEnd(endDate);
        } else {
            endDate = dto.getAlarmTimeEnd();
        }
        int count = 0;
        boolean stop = false;
        //处理传入的组织条件
        dto.setSysUser(sysUser);
        //handleOrgIdList(dto);
        //跨库获取页面查询设备信息关联数据
        List<AlarmOrderByDeviceDTO> devList = deviceRepository.queryAlarmOrderByDevice(dto);
        dto.setDevList(devList);
        // 从最后一个表开始向前获取
        while (!stop) {
            String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
            dto.setTableName(tableName);
            count = getLocalizationFlag() ? warningOrderMapper.queryAlarmOrderCountLocal(dto) : warningOrderMapper.queryAlarmOrderCount(dto);
            // 累计总数
            allCount += count;
            if (allCount > offset && count > 0 && limit > 0) {
                tableStartNum = offset - (allCount - count);
                if (tableStartNum < 0) {
                    tableStartNum = 0;
                }
                if (count > limit) {
                    dto.setLimit(limit);
                } else {
                    dto.setLimit(count);
                }
                dto.setOffset(tableStartNum);
                List<WarningOrderListVO> queryResult = warningOrderMapper.getOrderListByPeriphery(dto);
                resultList.addAll(queryResult);
                limit = limit - queryResult.size();
            }
            stop = DateUtils.sameMouth(startDate, endDate);
            // 转到上月
            String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
            endDate = DateUtils.stringToDateYM(lastMonth);
        }
        // 2022-01-11 sutn抽出方法
        this.resultHandle(resultList, dto, userId, flowLinkIds);
        return resultList;
    }


    /**
     * 根据经纬度递归查询告警信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/18
     * @version
     */
    @Override
//    @TenantForDataScope(dbTypeAlias = "db1", personAlias = "dataTable.town_code", deptAlias = "dataTable.group_id",
//            tenantAlias = "dataTable.tenant_id", deviceAlias = "dataTable.device_code", dataColumnAlias = "dataTable.warning_source", dataAlias = "1,2,3,8,9,10")
//    @WarningOrderDataScope(userAlias = "dataTable", userColumn = "process_id")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10,11,12", areaPro = "dataTable.province_code"
            , areaCity = "dataTable.city_code", areaCode = "dataTable.county_code", typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getOrderByLongitude(BigScreenOrderListDTO dto) {

        long start = System.currentTimeMillis();
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getUser();
        //查询全部
        dto.setSearchType(Constants.JUDGE_RESULT_0);
        dto.setUserId(sysUser.getUserId());
        dto.setTenantId(sysUser.getTenantId());
        //0：铁塔  2：租户
        dto.setSource(sysUser.getUserType());

        // 分页
        if (dto.getPageNum() == null) {
            dto.setPageNum(Constants.PAGE_NUM);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(Constants.PAGE_SIZE);
        }

        //不查询完结状态告警
        setOrderStatuses(dto);
        Date endDate;
        Date startDate;
        String webStartDate = dto.getWebStartDate();
        String webEndDate = dto.getWebEndDate();
        if (!StringUtils.isEmpty(webStartDate) && !StringUtils.isEmpty(webEndDate)) {
            startDate = DateUtils.stringToDateYMDHMS(webStartDate);
            endDate = DateUtils.string2Date(webEndDate + hms);
        } else {
            endDate = new Date();
            startDate = new Date();
        }
        dto.setWebStartDate(DateUtils.dateToStringYMD(startDate));
        dto.setWebEndDate(DateUtils.dateToStringNoMS(endDate));

        //查询结果集
        List<AlarmLableRelDTO> alarmLableRelDTOList = new ArrayList<>();
        Map map = this.getResult(dto, startDate, endDate, alarmLableRelDTOList, alarmLableRelDTOList);

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setRows((List<Map>) map.get(RESULT_LIST));
        tableDataInfo.setTotal((int) map.get(ALL_COUNT));
        log.info("获取告警列表-告警事件大屏左侧查询耗时：{},入参：{}", (System.currentTimeMillis() - start), JSON.toJSONString(dto));

        return tableDataInfo;

    }

    /**
     * @Author：hued
     * @Description：不查询完结状态告警
     * @Date：2021/12/15 16:49
     */
    public void setOrderStatuses(BigScreenOrderListDTO req) {
        List<String> orderStatuses = new ArrayList<>();
        orderStatuses.add(EnumUtil.FlowNode.END.getCode());
        orderStatuses.add(EnumUtil.FlowNode.DUPLICATION.getCode());
        orderStatuses.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
        req.setOrderStatuses(orderStatuses);
    }

    /**
     * @Author：hued
     * @Description：查询结果集
     * @Date：2021/12/16 14:31
     */
    public Map getResult(BigScreenOrderListDTO req, Date startDate, Date endDate, List<AlarmLableRelDTO> alarmLableRelDTOList, List<AlarmLableRelDTO> alarmLableAllList) {
        Map map = new HashMap();
        List<BigScreenOrderListVO> resultList = new ArrayList<>();
        int allCount = 0;
        // 需要查询的个数
        int limit = req.getPageSize();
        // 分页开始的数
        int offset = (req.getPageNum() - 1) * limit;
        // 当前表查询开始的数
        int tableStartNum = 0;
        int count = 0;
        boolean stop = false;

        String tableName;
        // 从最后一个表开始向前获取
        while (!stop) {
            tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
            req.setTableName(tableName);
            //告警标签
            String yearMonth = tableName.substring(tableName.length() - 6, tableName.length());
            List<String> warningOrderIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(req.getLableIdList())) {
                List<AlarmLableRelDTO> firstALarmLable = alarmLableRelDTOList.stream().filter(item -> yearMonth.equals(item.getTableTime())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(firstALarmLable)) {
                    firstALarmLable.forEach(item -> {
                        if (!StringUtils.isEmpty(item.getWaringOrderId())) {
                            warningOrderIds.add(item.getWaringOrderId());
                        }
                    });
                    req.setWarningOrderIds(warningOrderIds);
                } else {
                    stop = DateUtils.sameMouth(startDate, endDate);
                    // 转到上月
                    String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                    endDate = DateUtils.stringToDateYM(lastMonth);
                    continue;
                }
            }
            log.info("大屏查询告警入参：{}", JSON.toJSONString(req));
            count = warningOrderMapper.getOrderListForBigScreenCount(req);
            // 累计总数
            allCount += count;
            if (allCount > offset && count > 0 && limit > 0) {
                tableStartNum = offset - (allCount - count);
                if (tableStartNum < 0) {
                    tableStartNum = 0;
                }
                if (count > limit) {
                    req.setLimit(limit);
                } else {
                    req.setLimit(count);
                }
                req.setOffset(tableStartNum);
                log.info("大屏查询告警入参2：{}", JSON.toJSONString(req));
                List<BigScreenOrderListVO> queryResult = warningOrderMapper.getOrderListForBigScreen(req);
                resultList.addAll(queryResult);
                limit = limit - queryResult.size();
            }
            stop = DateUtils.sameMouth(startDate, endDate);
            // 转到上月
            String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
            endDate = DateUtils.stringToDateYM(lastMonth);
        }

        // 抽出方法
        this.resultHandle(resultList);
        this.getALarmLableBigList(resultList);
        map.put(ALL_COUNT, allCount);
        map.put(RESULT_LIST, resultList);
        return map;
    }

    public void getALarmLableBigList(List<BigScreenOrderListVO> orderListVO) {
        if (!CollectionUtils.isEmpty(orderListVO)) {
            List<String> ordIdList = orderListVO.stream().filter(item -> !StringUtils.isEmpty(item.getOrderId())).map(BigScreenOrderListVO::getOrderId).collect(Collectors.toList());
            List<AlarmLableRelDTO> alarmLableAllList = alarmLableRelMapper.getAlarmLableByOrdId(ordIdList);
            for (BigScreenOrderListVO orderVo : orderListVO) {
                //告警标签回显
                List<AlarmLableRelAPIDTO> alarmLableRelDTOS = new ArrayList<>();
                List<AlarmLableRelDTO> alarmLableRelAPIDTOs = alarmLableAllList.stream().filter(item -> orderVo.getOrderId().equals(item.getWaringOrderId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(alarmLableRelAPIDTOs)) {
                    alarmLableRelAPIDTOs.forEach(itemRe -> {
                        AlarmLableRelAPIDTO relAPIDTO = new AlarmLableRelAPIDTO();
                        BeanUtils.copyProperties(itemRe, relAPIDTO);
                        alarmLableRelDTOS.add(relAPIDTO);
                    });
                }
                orderVo.setAlarmLableRelDTOList(alarmLableRelDTOS);
            }
        }
    }


    public void resultHandle(List<BigScreenOrderListVO> resultList) {
        if (!CollectionUtils.isEmpty(resultList)) {
            // 查询告警类型名称
            List<Map> alramTypeList = getAlarmTypeList();
            // 告警等级名称
            AtomicReference<List<SysDictDataResponse>> emergencyLevelEnum = new AtomicReference<>();
            // 告警状态名称
            AtomicReference<List<SysDictDataResponse>> orderStatusEnum = new AtomicReference<>();
            // 告警来源枚举
            AtomicReference<List<SysDictDataResponse>> warningSourceEnum = new AtomicReference<>();
            // 重要等级
            AtomicReference<List<SysDictDataResponse>> importanceLevelEnum = new AtomicReference<>();
            List<String> paramList = new ArrayList<>();
            paramList.add(String.valueOf(Constants.EMERGENCY_LEVEL));
            paramList.add(String.valueOf(Constants.ORDER_STATUS));
            paramList.add(Constants.WARNING_SOURCE_COMMON);
            paramList.add(Constants.ALARM_LEVEL);

            ThreadParallelUtil.run(paramList, o -> {
                if (Constants.ALARM_LEVEL.equals(String.valueOf(o))) {
                    importanceLevelEnum.set(systemUtil.getEnum(o));
                } else if (String.valueOf(Constants.EMERGENCY_LEVEL).equals(String.valueOf(o))) {
                    emergencyLevelEnum.set(systemUtil.getEnum(o));
                } else if (String.valueOf(Constants.ORDER_STATUS).equals(String.valueOf(o))) {
                    orderStatusEnum.set(systemUtil.getEnum(o));
                } else if (Constants.WARNING_SOURCE_COMMON.equals(String.valueOf(o))) {
                    warningSourceEnum.set(systemUtil.getEnum(o));
                }
                return null;
            }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);


            String userId = sysService.getLoginUserId();
            //查询当前用户收藏事件
            AlarmOrderDTO dto = new AlarmOrderDTO();
            dto.setCollectionCreator(userId);
            List<WarningOrderListVO> warningOrderCollectionList = warningOrderCollectionMapper.qryWarningOrderCollection(dto);
            List<TenantDeviceRelDTO> tenantDeviceRelDTOList = new ArrayList<>();
            for (BigScreenOrderListVO orderInfoVO : resultList) {
                TenantDeviceRelDTO deviceRelDTO = new TenantDeviceRelDTO();
                deviceRelDTO.setDeviceCode(orderInfoVO.getDeviceCode());
                deviceRelDTO.setChannelCode(orderInfoVO.getChannelCode());
                deviceRelDTO.setTenantId(orderInfoVO.getTenantId());
                tenantDeviceRelDTOList.add(deviceRelDTO);
            }
            List<TenantDeviceEventRelVO> tenantDeviceEventRelVOList = deviceRepository.getDeviceRel(tenantDeviceRelDTOList);

            for (BigScreenOrderListVO bigScreenOrderListVO : resultList) {
                bigScreenOrderListVO.setIfTimeout(0);
                if (Objects.nonNull(bigScreenOrderListVO.getFlowNodeTime()) && !orderStatusList.contains(bigScreenOrderListVO.getOrderStatus())) {
                    bigScreenOrderListVO.setDuration(DateUtils.getTimeDifference2(new Date(), DateUtils.string2Date(bigScreenOrderListVO.getFlowNodeTime())));
                    //受理状态的工单超期展示受理超期
                    if (Constants.ONE.equals(bigScreenOrderListVO.getEnableAcceptanceMode())) {
                        if (StringUtils.isEmpty(bigScreenOrderListVO.getTimeLimitAcceptance())) {
                            bigScreenOrderListVO.setTimeLimit(null);
                        } else {
                            bigScreenOrderListVO.setTimeLimit(bigScreenOrderListVO.getTimeLimitAcceptance());
                        }
                    }
                    // timeLimit 为-1 表示不超期
                    if (Objects.nonNull(bigScreenOrderListVO.getTimeLimit())
                            && !EnumUtil.FlowNode.END.getCode().equals(bigScreenOrderListVO.getOrderStatus())
                            && !"-1".equals(bigScreenOrderListVO.getTimeLimit())) {
                        bigScreenOrderListVO.setIfTimeout(DateUtils.checkIfTimeOut(Integer.valueOf(bigScreenOrderListVO.getTimeLimit()), DateUtils.string2Date(bigScreenOrderListVO.getFlowNodeTime())));
                        if (bigScreenOrderListVO.getIfTimeout() == 0) {
                            // 未超期
                            bigScreenOrderListVO.setShowTime("剩余" + DateUtils.getTimeDifference(DateUtils.subtractTimeMinutes(DateUtils.string2Date(bigScreenOrderListVO.getFlowNodeTime()), Integer.valueOf(bigScreenOrderListVO.getTimeLimit())), new Date()));
                        } else {
                            // 超期
                            bigScreenOrderListVO.setShowTime("超期" + DateUtils.getTimeDifference(new Date(), DateUtils.subtractTimeMinutes(DateUtils.string2Date(bigScreenOrderListVO.getFlowNodeTime()), Integer.valueOf(bigScreenOrderListVO.getTimeLimit()))));
                        }
                    }
                }

                bigScreenOrderListVO.setIfEdit(1);
                //别名处理
                if (!CollectionUtils.isEmpty(tenantDeviceEventRelVOList)) {
                    boolean deviceFlag = false;
                    boolean channelFlag = false;
                    for (TenantDeviceEventRelVO tenantDeviceEventRelVO : tenantDeviceEventRelVOList) {
                        if (!StringUtils.isEmpty(bigScreenOrderListVO.getDeviceCode()) && bigScreenOrderListVO.getDeviceCode().equals(tenantDeviceEventRelVO.getDeviceCode())) {
                            bigScreenOrderListVO.setDeviceName(tenantDeviceEventRelVO.getDeviceName());
                            deviceFlag = true;
                        }
                        if (!StringUtils.isEmpty(bigScreenOrderListVO.getChannelCode()) && bigScreenOrderListVO.getChannelCode().equals(tenantDeviceEventRelVO.getChannelCode())) {
                            bigScreenOrderListVO.setChannelName(tenantDeviceEventRelVO.getChannelName());
                            channelFlag = true;
                        }
                        if (deviceFlag && channelFlag) {
                            break;
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(alramTypeList)) {
                    // 设置图标
                    alramTypeList.forEach(item -> {
                        if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(bigScreenOrderListVO.getWarningTypeId())) {
                            bigScreenOrderListVO.setIcon(MapUtils.getString(item, Constants.PARAM_ICON));
                            bigScreenOrderListVO.setWarningTypeName(item.get(Constants.PARAM_TYPENAME).toString());
                        }
                    });
                }

                if (!CollectionUtils.isEmpty(emergencyLevelEnum.get())) {
                    emergencyLevelEnum.get().forEach(item -> {
                        if (item.getDictValue().equals(bigScreenOrderListVO.getEmergencyLevel())) {
                            bigScreenOrderListVO.setEmergencyLevelName(item.getDictLabel());
                        }
                    });
                }

                if (!CollectionUtils.isEmpty(orderStatusEnum.get())) {
                    orderStatusEnum.get().forEach(item -> {
                        if (item.getDictValue().equals(bigScreenOrderListVO.getOrderStatus())) {
                            bigScreenOrderListVO.setOrderStatusName(item.getDictLabel());
                        }
                    });
                }
                if (EnumUtil.FlowNode.MISINFORMATION.getCode().equals(bigScreenOrderListVO.getOrderStatus())
                        || EnumUtil.FlowNode.DUPLICATION.getCode().equals(bigScreenOrderListVO.getOrderStatus())
                        || EnumUtil.FlowNode.END.getCode().equals(bigScreenOrderListVO.getOrderStatus())) {
                    bigScreenOrderListVO.setOrderStatusName(EnumUtil.FlowNode.END.getName());
                }

                // 设置告警来源
                if (!CollectionUtils.isEmpty(warningSourceEnum.get())) {
                    warningSourceEnum.get().forEach(item -> {
                        if (org.springframework.util.ObjectUtils.nullSafeEquals(bigScreenOrderListVO.getWarningSource(), item.getDictValue())) {
                            bigScreenOrderListVO.setWarningSourceName(item.getDictLabel());
                        }
                    });
                }
                // 设置重要等级名称
                if (!CollectionUtils.isEmpty(importanceLevelEnum.get())) {
                    importanceLevelEnum.get().forEach(item -> {
                        if (item.getDictValue().equals(bigScreenOrderListVO.getImportanceLevel())) {
                            bigScreenOrderListVO.setImportanceLevelName(item.getDictLabel());
                        }
                    });
                }

                // 20211127 zzh 处理图片，多张图片取第一张
                String imgUrl = bigScreenOrderListVO.getImgUrl();
                if (!StringUtils.isEmpty(imgUrl) && imgUrl.contains(Constants.COMMA)) {
                    bigScreenOrderListVO.setImgUrl(imgUrl.split(Constants.COMMA)[0]);
                }
                //是否收藏
                bigScreenOrderListVO.setIsCollection(Constants.ZERO);
                for (WarningOrderListVO item : warningOrderCollectionList) {
                    if (item.getOrderId().equals(bigScreenOrderListVO.getOrderId())) {
                        bigScreenOrderListVO.setIsCollection(Constants.ONE);
                        break;
                    }
                }
            }
        }
    }


    /**
     * 处理用户信息
     *
     * @param mapList
     * @param param
     * @param resList
     */
    public void handleUserInfo(List<Map> mapList, String param, List<String> resList) {
        if (!CollectionUtils.isEmpty(mapList)) {
            List<String> userIdList = new ArrayList<>();
            for (Map map : mapList) {
                if (ObjectUtils.isNotEmpty(map.get(param))) {
                    userIdList.add(String.valueOf(map.get(param)));
                }
                if (ObjectUtils.isNotEmpty(map.get(Constants.RES_ACCEPT_PROCESS_ID))) {
                    userIdList.add(String.valueOf(map.get(Constants.RES_ACCEPT_PROCESS_ID)));
                }
            }

            List<SysUserVO> sysUserVOList = deviceRepository.getUserInfoByUserIds(userIdList);
            if (!CollectionUtils.isEmpty(sysUserVOList)) {
                for (Map map : mapList) {
                    for (SysUserVO sysUserVO : sysUserVOList) {
                        if (String.valueOf(map.get(param)).equals(String.valueOf(sysUserVO.getUserId()))) {
                            for (String res : resList) {
                                if (Constants.RES_MOBILE_PHONE.equals(res)) {
                                    if (!StringUtils.isEmpty(sysUserVO.getPhonenumber())) {
                                        map.put(Constants.RES_MOBILE_PHONE, sysUserVO.getPhonenumber());
                                    } else {
                                        map.put(Constants.RES_MOBILE_PHONE, sysUserVO.getPhone());
                                    }
                                }
                                if (Constants.RES_LEADER_NAME.equals(res)) {
                                    map.put(Constants.RES_LEADER_NAME, sysUserVO.getNickName());
                                }
                            }
                        }
                        if (ObjectUtils.isNotEmpty(map.get(Constants.RES_ACCEPT_PROCESS_ID)) && String.valueOf(map.get(Constants.RES_ACCEPT_PROCESS_ID)).equals(sysUserVO.getUserId())) {
                            map.put(Constants.RES_ACCEPT_PROCESS_NAME, sysUserVO.getNickName());
                            map.put(Constants.RES_ACCEPT_PROCESS_PHONE, sysUserVO.getPhonenumber());
                        }
                    }
                }
            }
        }
    }


    /**
     * 更新告警信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/9
     * @version
     */
    public void updateAlarmImg(String warningOrderId, String tableName, String fileImgUrlSrc) {
        boolean flag = false;
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        orderInfoVO.setFileImgUrlSrc(fileImgUrlSrc);
        orderInfoVO.setWarningOrderId(warningOrderId);
        if (!StringUtils.isEmpty(fileImgUrlSrc)) {
            flag = true;
        }
        if (flag) {
            warningOrderMapper.updateIotAlarmImg(orderInfoVO, tableName);
        }
    }

    /**
     * 获取告警数据
     *
     * @return
     * @author chenmf
     * @date 2022/5/9
     * @version
     */
    @Override
    public OrderInfoVO getWarningOrderById(String warningOrderId, String tableName) {
        AlarmOrderDTO dto = new AlarmOrderDTO();
        // 查询告警详情
        dto.setOrderId(warningOrderId);
        dto.setTableName(tableName);
        List<OrderInfoVO> orderAlarmInfo = warningOrderMapper.getOrderAlarmInfo(dto);
        if (!CollectionUtils.isEmpty(orderAlarmInfo)) {
            return orderAlarmInfo.get(0);
        }
        return new OrderInfoVO();
    }

    /**
     * 批量查询所有告警数据
     *
     * @return
     * @author fanzw
     * @version
     */
    @Override
    public List<OrderInfoVO> getWarningOrderById(String[] warningOrderId, String tableName) {
        // 批量查询所有告警数据
        AlarmOrderDTO dto = new AlarmOrderDTO();
        dto.setOrderIds(warningOrderId);
        dto.setTableName(tableName);
        return warningOrderMapper.getOrderAlarmInfo(dto);
    }

    /**
     * 保存附件
     *
     * @param dto
     * @return
     */
    private Result addOrderFile(SendAlarmOrderDTO dto) {
        List<OrderFileDTO> orderFileDTOList = dto.getOrderFileDTOList();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        try {
            if (!CollectionUtils.isEmpty(orderFileDTOList)) {
                // 获取表名
                String tableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + dto.getOrderId().substring(0, 6);
                Map param = new HashMap();
                param.put(Constants.PARAM_TABLE_NAME, tableName);
                param.put(Constants.PARAM_ORDER_ID, dto.getOrderId());
                String orderFlowId = orderFlowRecordMapper.getIdByOrderId(param);

                StringBuffer processId = new StringBuffer();
                StringBuffer processName = new StringBuffer();
                List<ProcessDTO> processDTOList = dto.getProcessDTOList();
                if (!CollectionUtils.isEmpty(processDTOList)) {
                    processDTOList.forEach(processDTO -> {
                        processId.append(processDTO.getProcessId()).append(Constants.COMMA);
                        processName.append(processDTO.getProcessName()).append(Constants.COMMA);
                    });
                    if (processId.length() > 1) {
                        processId.deleteCharAt(processId.length() - 1).toString();
                    }
                    if (processName.length() > 1) {
                        processName.deleteCharAt(processName.length() - 1).toString();
                    }
                }
                // 添加附件
                for (OrderFileDTO orderFileDTO : orderFileDTOList) {
                    orderFileDTO.setOrderFileId(
                            PKUtils.getOrderFileId(dto.getTenantId(), dto.getWarningOrderId().substring(0, 6)));
                    orderFileDTO.setCreateBy(sysService.getLoginUserId());
                    orderFileDTO.setCreateDate(new Date());
                    orderFileDTO.setWarningOrderId(dto.getWarningOrderId());
                    orderFileDTO.setOrderFlowId(orderFlowId);
                    orderFileDTO.setLinkId(dto.getCurrentLink());
                    orderFileDTO.setDeleteFlag(Constants.DELETE_FLAG);
                    orderFileDTO.setProcessId(processId.toString());
                    orderFileDTO.setProcessName(processName.toString());
                    OrderFileDO orderFileDO = new OrderFileDO();
                    BeanUtils.copyProperties(orderFileDTO, orderFileDO);
                    orderFileMapper.insert(orderFileDO);
                }
            }
        } catch (Exception e) {
            log.error(Constants.LOG_FILE_INFO + e.getMessage());
            return Result.fail(Constants.LOG_FILE_INFO + e.getMessage());
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 判断是否有编辑权限 1:可编辑   0：不可编辑
     *
     * @param isAdmin
     * @param userId
     * @param vo
     * @return
     */
    @Override
    public int ifEditHandle(boolean isAdmin, String userId, WarningOrderListVO vo, String flowLinkIds) {
        if (isAdmin) {
            //编辑权限
            return 1;
        } else if (!StringUtils.isEmpty(vo.getProcessId()) && vo.getProcessId().contains(userId)) {
            return 1;
        } else if (!StringUtils.isEmpty(vo.getFlowId()) && !StringUtils.isEmpty(flowLinkIds)
                && flowLinkIds.contains(vo.getFlowId() + "-" + vo.getLinkId()) && Constants.BEFORE_DISPATCH_FLAG_0.equals(vo.getBeforeDispatchFlag())) {
            //根据用户id和角色id查询流程环节信息
            // 环节和环节配置id关联上即有编辑权限
            return 1;
        }
        return 0;
    }

    /**
     * 物联告警事件图片更新
     *
     * @return
     * @author chenmf
     * @date 2022/6/6
     * @version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarningOrderImg(String warningOrderId, JSONObject data, String tableType, String tableName) {
        log.info("=========上传物联网告警图片请求报文:" + data.toJSONString() + "=========上传物联网告警图片请求参数:warningOrderId:" + warningOrderId);
        if (!DeviceConstants.ZERO.equals(data.getString("error_code"))) {
            log.error("========上传物联网告警图片失败error_code!=0");
            throw new CustomException("上传失败");
        }
        String imageUrl = data.get("imagePath").toString();
        String accessNode = data.get("accessNode").toString();
        String deviceCode = data.get("deviceCode").toString();
        try {
            OrderInfoVO orderInfoVO = this.getWarningOrderById(warningOrderId, tableName);
            if (null == orderInfoVO) {
                log.error(Constants.UPLOAD_FAIL_MSG + warningOrderId + Constants.NOT_EXIST_MSG);
                throw new CustomException(Constants.UPLOAD_FAIL_MSG + warningOrderId + Constants.NOT_EXIST_MSG);
            }
            //异步查询二次识别配置 判断是否二次识别任务下发
            if (!StringUtils.isEmpty(imageUrl)) {
                asyncCommon.secondRecogIfExecute(orderInfoVO, imageUrl, deviceCode, tableType);
            }
            // 是否纯属地化
            boolean isDependenceFlag = this.isDependenceFlag();
            String url = AssembleUrlUtil.getAssembleUrl(accessNode, imageUrl, !isDependenceFlag);
            //更新原图
            String fileImgUrlSrc = orderInfoVO.getFileImgUrlSrc();
            if (com.chinatower.common.core.utils.StringUtils.isNotEmpty(fileImgUrlSrc)) {
                fileImgUrlSrc = fileImgUrlSrc + Constants.COMMA + url;
            } else {
                fileImgUrlSrc = url;
            }
            this.updateAlarmImg(warningOrderId, tableName, fileImgUrlSrc);
            if ("1".equals(tableType)) {
                try {
                    log.info("联动抓图更新mongo:{}", JSON.toJSONString(warningOrderId));
                    //回收站表
                    InterceptDTO vo = new InterceptDTO();
                    vo.setOrderId(warningOrderId);
                    vo.setFileImgUrlSrc(fileImgUrlSrc);
                    interceptMongoMapper.updateIntercept(vo);
                } catch (Exception e) {
                    log.error("联动抓图更新mongo异常:", e);
                }

            }
            log.info("========上传物联网告警图片返回报文：uploadResult:" + imageUrl);
        } catch (Exception e) {
            log.error("========上传物联网告警图片失败", e);
            throw new CustomException("上传物联网告警图片失败");
        }
    }


    /**
     * 卫星告警事件图片更新
     *
     * @return
     * @author fanzw
     * @version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSatelliteWarningImg(String[] warningOrderId, String tableName, JSONObject data, String tableType, String selectAlgorithm) {
        log.info("=========上传卫星告警联动图片摄像机编码:" + data.get(Constants.PARAM_DEVICE_CODE) + "=========上传卫星告警联动图片请求参数:param:" + JSON.toJSONString(warningOrderId) + Constants.SPLIT_SIGN + Constants.SPLIT_SIGN + tableName);
        if (!DeviceConstants.ZERO.equals(data.getString("error_code"))) {
            log.error("========上传卫星告警联动图片失败error_code!=0");
            throw new CustomException("上传失败");
        }
        String imageUrl = data.get("imagePath").toString();
        String accessNode = data.get("accessNode").toString();
        try {

            List<OrderInfoVO> orderInfoVOs = this.getWarningOrderById(warningOrderId, tableName);

            if (CollectionUtils.isEmpty(orderInfoVOs)) {

                log.error(Constants.UPLOAD_FAIL_MSG + JSON.toJSONString(warningOrderId) + Constants.NOT_EXIST_MSG);
                throw new CustomException(Constants.UPLOAD_FAIL_MSG + JSON.toJSONString(warningOrderId) + Constants.NOT_EXIST_MSG);
            }

            if (!StringUtils.isEmpty(imageUrl)) {
                if (StringUtil.isNotEmpty(selectAlgorithm)) {
                    //卫片异步查询二次识别配置 判断是否二次识别任务下发
                    asyncCommon.secondRecogIfExecuteBySatelliteImages(orderInfoVOs, imageUrl, tableType, selectAlgorithm);
                } else {
                    //卫星异步查询二次识别配置 判断是否二次识别任务下发
                    asyncCommon.secondRecogIfExecuteBySatellite(orderInfoVOs, imageUrl, tableType);
                }
            }
            // 是否纯属地化
            boolean isDependenceFlag = this.isDependenceFlag();
            String url = AssembleUrlUtil.getAssembleUrl(accessNode, imageUrl, !isDependenceFlag);
            orderInfoVOs.forEach(orderInfoVO -> {
                //更新原图
                String fileImgUrlSrc = orderInfoVO.getFileImgUrlSrc();
                if (com.chinatower.common.core.utils.StringUtils.isNotEmpty(fileImgUrlSrc)) {
                    fileImgUrlSrc = fileImgUrlSrc + Constants.COMMA + url;
                } else {
                    fileImgUrlSrc = url;
                }

                orderInfoVO.setFileImgUrlSrc(fileImgUrlSrc);

                SatelliteExtendFieldVo extendFieldVo = JSON.parseObject(orderInfoVO.getExtraField(), SatelliteExtendFieldVo.class);
                //拼接联动摄像机编码
                StringJoiner deviceCodeJoiner = new StringJoiner(Constants.COMMA);
                String deviceCode = String.valueOf(data.get(Constants.PARAM_DEVICE_CODE));
                if (!StringUtils.isEmpty(deviceCode)) {
                    deviceCodeJoiner.add(deviceCode);
                }
                if (StringUtils.isEmpty(extendFieldVo.getRealDeviceCode())) {
                    extendFieldVo.setRealDeviceCode(deviceCodeJoiner.toString());
                } else {
                    extendFieldVo.setRealDeviceCode(extendFieldVo.getRealDeviceCode() + "," + deviceCodeJoiner.toString());
                }
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_YMDHMS);
                extendFieldVo.setSnapShotTime(sdf.format(new Date()));

                orderInfoVO.setExtraField(JSON.toJSONString(extendFieldVo));
            });

            log.info("========上传卫星告警联动图片结果:" + JSON.toJSONString(orderInfoVOs));

            this.updateSatelliteAlarmImg(orderInfoVOs, tableName);
            try {
                log.info("联动抓图更新mongo:{}", JSON.toJSONString(warningOrderId));
                if (TableConstant.WARN_ALARM_INTERCEPT.equals(tableName)) {
                    //回收站表
                    InterceptDTO vo = new InterceptDTO();
                    vo.setOrderIds(Arrays.asList(warningOrderId));
                    vo.setFileImgUrlSrc(orderInfoVOs.get(0).getFileImgUrlSrc());
                    vo.setExtraField(orderInfoVOs.get(0).getExtraField());
                    interceptMongoMapper.updateIntercept(vo);

                } else if (TableConstant.WARN_ALARM_OPERATE.equals(tableName)) {
                    //运营研判表
                    List<OperateMongo> operateMongoList = new ArrayList<>();
                    for (String orderId : warningOrderId) {
                        OperateMongo mongo = new OperateMongo();
                        mongo.setWarningOrderId(orderId);
                        mongo.setFileImgUrlSrc(orderInfoVOs.get(0).getFileImgUrlSrc());
                        mongo.setExtraField(orderInfoVOs.get(0).getExtraField());
                        operateMongoList.add(mongo);
                    }
                    operateMongoMapper.updateOperate(operateMongoList);
                }
            } catch (Exception e) {
                log.error("联动抓图更新mongo异常:", e);
            }


        } catch (Exception e) {
            log.error("========上传卫星告警联动图片失败", e);
            throw new CustomException("上传卫星告警联动图片失败");
        }
    }


    /**
     * 更新卫星告警信息
     *
     * @return
     * @author fanzw
     * @version
     */
    @Override
    public void updateSatelliteAlarmImg(List<OrderInfoVO> orderInfoVOs, String tableName) {
        warningOrderMapper.updateSatelliteAlarmImg(orderInfoVOs, tableName);
    }

    /**
     * 二次识别结果更新
     *
     * @return
     * @author cuisj
     * @version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSecondRecogResult(List<SecondRecogBackVO> list) {
        try {
            //表名
            String tableName = "";
            //上传图片
            String picUrl = "";
            //更新入参
            List<SecondRecogBackVO> updateList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(list) && list.size() > 0) {
                String tableType = list.get(0).getTaskId().split("/")[1];
                if (Constants.ONE.equals(tableType)) {
                    tableName = TableConstant.WARN_ALARM_INTERCEPT;
                } else if (Constants.TWO.equals(tableType)) {
                    tableName = TableConstant.WARN_ORDER_TEBLE + list.get(0).getTaskId().split("/")[0].substring(0, 6);
                } else if (Constants.THREE.equals(tableType)) {
                    tableName = TableConstant.WARN_ALARM_OPERATE;
                }
                //告警id集合
                String[] orderIds = list.stream().map(v -> v.getTaskId().split("/")[0]).toArray(String[]::new);
                //根据告警id查询
                List<OrderInfoVO> orderInfoVOs = this.getWarningOrderById(orderIds, tableName);
                if (CollectionUtils.isEmpty(orderInfoVOs)) {
                    log.error("相关告警不存在");
                    throw new CustomException("相关告警不存在");
                }
                Map<String, OrderInfoVO> resultMap = orderInfoVOs.stream()
                        .collect(Collectors.toMap(OrderInfoVO::getWarningOrderId, item -> item));
                for (SecondRecogBackVO backVO : list) {
                    String algorithmCode = backVO.getTaskId().split("/")[2];
                    String orderId = backVO.getTaskId().split("/")[0];
                    //节点编码
                    String acceptNode = backVO.getTaskId().split("/")[3];
                    //图片不为空 上传图片
                    OrderInfoVO vo = MapUtils.getObject(resultMap, orderId);
                    //处理物联告警透传字段中 二次识别信息
                    this.handleIotExtraField(vo, backVO);
                    //算法识别结果只要有一个为真，则二次识别结果即为真
                    if ("1".equals(vo.getSecondIdentifyResult())) {
                        backVO.setSecondJudgeResult("1");
                    }
                    //组装二次识别结果信息
                    SecondRecogBackVO secondRecogBackVO = new SecondRecogBackVO();
                    secondRecogBackVO.setIfSecondJudge("1");
                    secondRecogBackVO.setSecondRecogTableType(tableType);
                    secondRecogBackVO.setSecondJudgeResult(backVO.getSecondJudgeResult());
                    if ("-1".equals(backVO.getSecondJudgeResult())) {
                        secondRecogBackVO.setSecondJudgeResult("2");
                    }
                    if (!StringUtils.isEmpty(backVO.getAlarmPictureUrl())) {
                        String url = this.montageUrl(acceptNode, backVO);
                        backVO.setAlarmPictureUrl(url);
                    }
                    secondRecogBackVO.setSelectAlgorithm(algorithmCode);
                    if (!StringUtils.isEmpty(backVO.getAlarmPictureUrl()) && !StringUtils.isEmpty(orderInfoVOs.get(0).getFileImgUrlIcon())) {
                        picUrl = orderInfoVOs.get(0).getFileImgUrlIcon() + Constants.COMMA + backVO.getAlarmPictureUrl();
                    } else {
                        picUrl = backVO.getAlarmPictureUrl();
                    }
                    secondRecogBackVO.setAlarmPictureUrl(picUrl);
                    secondRecogBackVO.setTaskId(orderId);
                    secondRecogBackVO.setSecondRecogTableName(tableName);
                    secondRecogBackVO.setExtraFieldAlarm(vo.getExtraField());
                    updateList.add(secondRecogBackVO);
                }

                int i = warningOrderMapper.updateSecondRecogResult(updateList);
                //回收站数据
                List<SecondRecogBackVO> interList = updateList.stream().filter(v -> "1".equals(v.getSecondRecogTableType())).collect(Collectors.toList());
                //研判数据
                List<SecondRecogBackVO> opList = updateList.stream().filter(v -> "3".equals(v.getSecondRecogTableType())).collect(Collectors.toList());
                try {
                    if (!CollectionUtils.isEmpty(interList)) {
                        //回收站表
                        List<InterceptDTO> interceptMongoList = new ArrayList<>();
                        for (SecondRecogBackVO vo : interList) {
                            InterceptDTO mongo = new InterceptDTO();
                            mongo.setOrderId(vo.getTaskId());
                            mongo.setIsSecondIdentify("1");
                            mongo.setSecondIdentifyResult(vo.getSecondJudgeResult());
                            if ("-1".equals(vo.getSecondJudgeResult())) {
                                mongo.setSecondIdentifyResult("2");
                            }
                            mongo.setLinkageAlgorithm(vo.getSelectAlgorithm());
                            mongo.setFileImgUrlIcon(vo.getAlarmPictureUrl());
                            mongo.setExtraField(vo.getExtraFieldAlarm());
                            interceptMongoList.add(mongo);
                        }
                        log.info("二次识别回收站结果信息更新mongo:{}", JSON.toJSONString(interceptMongoList));
                        interceptMongoMapper.updateInterceptList(interceptMongoList);

                    }
                    if (!CollectionUtils.isEmpty(opList)) {
                        //运营研判表
                        List<OperateMongo> operateMongoList = new ArrayList<>();
                        for (SecondRecogBackVO vo : opList) {
                            OperateMongo mongo = new OperateMongo();
                            mongo.setWarningOrderId(vo.getTaskId());
                            mongo.setIsSecondIdentify("1");
                            mongo.setSecondIdentifyResult(vo.getSecondJudgeResult());
                            if ("-1".equals(vo.getSecondJudgeResult())) {
                                mongo.setSecondIdentifyResult("2");
                            }
                            mongo.setLinkageAlgorithm(vo.getSelectAlgorithm());
                            mongo.setFileImgUrlIcon(vo.getAlarmPictureUrl());
                            operateMongoList.add(mongo);
                        }
                        log.info("二次识别研判表结果信息更新mongo:{}", JSON.toJSONString(operateMongoList));
                        operateMongoMapper.updateOperate(operateMongoList);
                    }
                } catch (Exception e) {
                    log.error("二次配置识别结果更新mongo异常:", e);
                }
            }
        } catch (Exception e) {
            log.error("========二次配置识别结果回调失败", e);
        }
    }

    private void dutySensingAlgorithmTask(List<SecondRecogBackVO> list) {
        list.forEach(vo -> {
            String taskId = vo.getTaskId();
            String[] split = taskId.split("/");
            AlgorithmPicResultVO algorithmPicResultVO = new AlgorithmPicResultVO();
            algorithmPicResultVO.setTaskInfoId(Long.parseLong(split[2]));
            algorithmPicResultVO.setSecondRecogResult(JSONUtil.toJsonStr(vo));
            log.info("遥感算法识别修改基准图参数 param:{}", JSON.toJSONString(algorithmPicResultVO));
            Result<Boolean> booleanResult = sensingAlgorithmTaskAPI.updateAlgorithmPicResult(algorithmPicResultVO);
            log.info("遥感算法识别修改基准图结果:{}", JSON.toJSONString(booleanResult));
        });
    }

    /**
     * 二次识别结果更新
     *
     * @return
     * @author cuisj
     * @version
     */
    @Override
    public List<SecondRecogBackVO> alarmByTaskBatchId(String taskBatchId, String platFormCode, String toPlatformCode, String type) {
        return asyncCommon.alarmByTaskBatchId(taskBatchId, platFormCode, toPlatformCode, type);
    }

    /**
     * 二次识别下发
     *
     * @return
     * @author cuisj
     * @version
     */
    @Override
    public String synAnalysisTask(List<SecondRecogSendVO> list) {
        return asyncCommon.synAnalysisTask(list);
    }

    /**
     * 无人机二次识别下发
     *
     * @return
     * @author cuisj
     * @version
     */
    @Override
    public String uavSynAnalysisTask(List<SecondRecogSendVO> list) {
        //处理图片加密
        this.handlePicUrl(list);
        return asyncCommon.uavSynAnalysisTask(list);
    }


    public void handlePicUrl(List<SecondRecogSendVO> list) {
        //文件中的图片&视频进行再次加密处理
        List<String> fileList = new ArrayList<>();
        for (SecondRecogSendVO vo : list) {
            if (vo.getFileInfo() != null) {
                {
                    if (!StringUtils.isEmpty(vo.getFileInfo().getImgUrls())) {
                        fileList.add(vo.getFileInfo().getImgUrls());
                    }
                    if (!StringUtils.isEmpty(vo.getFileInfo().getVideoUrls())) {
                        fileList.add(vo.getFileInfo().getVideoUrls());
                    }
                    if (!StringUtils.isEmpty(vo.getFileInfo().getBaselinePicUrl())) {
                        fileList.add(vo.getFileInfo().getBaselinePicUrl());
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(fileList)) {
            Map<String, String> fileMap = fileFeignClient.encryptBatch(fileList);
            fileMap.forEach((key, value) -> {
                log.info("uavSynAnalysisTask二次下发加密前后路径-Original URL: {}, Encrypted URL: {}", key, value);
                for (SecondRecogSendVO vo : list) {
                    if (vo.getFileInfo() != null) {
                        {
                            if (!StringUtils.isEmpty(vo.getFileInfo().getImgUrls()) && !StringUtils.isEmpty(value)) {
                                if (vo.getFileInfo().getImgUrls().equals(key)) {
                                    vo.getFileInfo().setImgUrls(value);
                                }
                            }
                            if (!StringUtils.isEmpty(vo.getFileInfo().getVideoUrls()) && !StringUtils.isEmpty(value)) {
                                if (vo.getFileInfo().getVideoUrls().equals(key)) {
                                    vo.getFileInfo().setVideoUrls(value);
                                }
                            }
                            if (!StringUtils.isEmpty(vo.getFileInfo().getBaselinePicUrl()) && !StringUtils.isEmpty(value)) {
                                if (vo.getFileInfo().getBaselinePicUrl().equals(key)) {
                                    vo.getFileInfo().setBaselinePicUrl(value);
                                }
                            }
                        }
                    }
                }
            });
        }
    }


    /**
     * 公共二次识别下发
     *
     * @return
     * @author cuisj
     * @version
     */
    @Override
    public String publicSynAnalysisTask(List<SecondRecogSendVO> list) {
        //处理图片加密
        this.handlePicUrl(list);
        String taskBatchId = asyncCommon.publicSynAnalysisTask(list);
        log.info("公共二次识别下发结果taskBatchId{}", taskBatchId);
        return taskBatchId;
    }


    //处理物联告警透传字段中 二次识别信息
    public void handleIotExtraField(OrderInfoVO vo, SecondRecogBackVO backVO) {
        //把对应设备的二次结果 更新到ExtraField字段
        if (!StringUtils.isEmpty(vo.getExtraField()) && "8".equals(vo.getWarningSource())) {
            String algorithmCode = backVO.getTaskId().split("/")[2];
            Map map = JSONObject.parseObject(vo.getExtraField(), Map.class);
            List<IotDeviceExtraFieldVO> extraFieldVOS = new ArrayList<>();
            if (map != null && map.get(Constants.PARAM_DEVICEINFO) != null) {
                extraFieldVOS = JSONArray.parseArray(String.valueOf(map.get(Constants.PARAM_DEVICEINFO)), IotDeviceExtraFieldVO.class);

            }
            if (!CollectionUtils.isEmpty(extraFieldVOS)) {
                for (IotDeviceExtraFieldVO extraFieldVO : extraFieldVOS) {
                    for (IotDeviceExtraFieldVO.RelDeviceInfo relDeviceInfo : extraFieldVO.getRelDevice()) {
                        //联动设备编码 是否与二次识别返回设备编码相同
                        if (backVO.getExtraFieldInfo() != null) {
                            if (backVO.getExtraFieldInfo().getDeviceCode().equals(relDeviceInfo.getDeviceCode())) {
                                relDeviceInfo.setSecondIdentifyResult(backVO.getSecondJudgeResult());
                                relDeviceInfo.setIsSecondIdentify("1");
                                relDeviceInfo.setLinkageAlgorithm(algorithmCode);
                            }
                        }
                    }
                }
                map.put(Constants.PARAM_DEVICEINFO, extraFieldVOS);
                vo.setExtraField(JSON.toJSONString(map));
            }
        }
    }

    public boolean isDependenceFlag() {
        // 是否属地化
        Boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(Constants.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            log.info("DEPENDENCE_MANAGE====>{}", sysConfig);
            if (sysConfig != null && sysConfig.getConfigValue().equals(Constants.ZERO)) {
                isDependenceFlag = true;
            }
        }
        return isDependenceFlag;
    }

    /**
     * 对工单图片进行拼接（非中心节点&受理节点与当前节点不一致得单子进行操作，其余保持原有路径）
     *
     * @param accessNode
     * @param
     * @return
     */
    public String montageUrl(String accessNode, SecondRecogBackVO backVO) {
        if (StringUtils.isEmpty(accessNode)) {
            log.info("接入节点为空-入参:{}", JSON.toJSONString(backVO));
            return backVO.getAlarmPictureUrl();
        }
        //中心节点文件地址无需拼接属地编码
        if ("10000000001".equals(accessNode)) {
            return backVO.getAlarmPictureUrl();
        }
        return this.getUrl(accessNode, backVO.getAlarmPictureUrl());
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
                realUrl = "/" + accessNode + realUrl;
            }
            if (StringUtils.isEmpty(resUrl)) {
                resUrl = realUrl;
            } else {
                resUrl += Constants.COMMA + realUrl;
            }
        }
        return resUrl;
    }

    @Override
    public List<WarnOrderStatusDTO> checkOrderStatusList(AlarmOrderDTO alarmOrderDTO) {
        return warningOrderMapper.getAlarmOrderList(alarmOrderDTO);
    }

    /**
     * 功能描述：更新、插入流程环节用户处理表
     *
     * @return void
     * @params [alarmOrderVO]
     * @author su.tingning
     * @date 2022/7/5 14:12
     */
    private void linkUserDealHandle(SendAlarmOrderDTO alarmOrderVO) {
        //更新流程环节用户处理表
        LinkUserDealModel model = new LinkUserDealModel();
        model.setWarningOrderId(alarmOrderVO.getWarningOrderId());
        model.setTableSuffix(alarmOrderVO.getWarningOrderId().substring(0, 6));
        model.setLinkId(Long.valueOf(StringUtils.isEmpty(alarmOrderVO.getCurrentLink()) ? alarmOrderVO.getCurLink() : alarmOrderVO.getCurrentLink()));
        //1结束 0未结束
        model.setStatus(Constants.LINK_USER_DEAL_STATUS_1);

        linkUserDealService.modifyLinkUserDeal(model);
        //调用流程环节用户处理表 保存下个环节流程操作用户
        if (!StringUtils.isEmpty(alarmOrderVO.getProcessId()) && alarmOrderVO.getProcessId().length() > 2) {
            model.setUserId(alarmOrderVO.getProcessId().substring(1, alarmOrderVO.getProcessId().length() - 1));
            model.setLinkId(Long.valueOf(alarmOrderVO.getLinkId()));
            //1结束 0未结束
            model.setStatus(Constants.LINK_USER_DEAL_STATUS_0);
            linkUserDealService.addLinkUserDeal(model);
        }
    }

    /**
     * 功能描述：插入流程环节表下一环节数据
     *
     * @return void
     * @params [alarmOrderVO, insertRecored]
     * @author su.tingning
     * @date 2022/7/5 14:35
     */
    private void insertRecoredHandle(SendAlarmOrderDTO alarmOrderVO, AlarmOrderRecordVO insertRecored) {
        String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + alarmOrderVO.getWarningOrderId().substring(0, 6);
        insertRecored.setTableName(recordTableName);
        insertRecored.setOrderId(alarmOrderVO.getWarningOrderId());
        insertRecored.setDeleteFlag(Constants.VALID_STATE);
        insertRecored.setIfTimeout(Constants.VALID_STATE);
        insertRecored.setIfCurrentLink(Constants.YES_FLAG);
        // 2021-06-21 获取下一环节处置时限
        Integer timeLimit = alarmOrderVO.getTimeLimit();
        // 2021-06-21 end
        insertRecored.setTimeLimit(timeLimit);
        insertRecored.setFlowNodeTime(DateUtils.string2Date(alarmOrderVO.getFlowNodeTime()));
        //环节ID
        insertRecored.setLinkId(alarmOrderVO.getLinkId());
        //处理环节
        insertRecored.setLinkName(alarmOrderVO.getLinkName());
        insertRecored.setFlowRecordName("");
        insertRecored.setRealLinkType(alarmOrderVO.getRealLinkType());
        if (!StringUtils.isEmpty(alarmOrderVO.getNextEnableAcceptanceMode())) {
            insertRecored.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
            if (Constants.ONE.equals(alarmOrderVO.getNextEnableAcceptanceMode())) {
                //下个环节为待受理时，下个环节得下一环节信息为下个环节的当前环节
                insertRecored.setNextLinkId(alarmOrderVO.getLinkId());
                insertRecored.setNextLinkName(alarmOrderVO.getLinkName());
            }
        } else {
            //兼容受理模式为空的情况按非受理处置
            insertRecored.setEnableAcceptanceMode(Constants.ZERO);
        }
    }

    /**
     * 功能描述：更新流程环节表当前环节数据
     *
     * @return void
     * @params [alarmOrderVO, updateRecored, loginUser]
     * @author su.tingning
     * @date 2022/7/5 14:42
     */
    private void updateRecoredHandle(SendAlarmOrderDTO alarmOrderVO, AlarmOrderRecordVO updateRecored, LoginUser loginUser) {
        String recordTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + alarmOrderVO.getWarningOrderId().substring(0, 6);
        updateRecored.setTableName(recordTableName);
        updateRecored.setOrderId(alarmOrderVO.getWarningOrderId());
        updateRecored.setProcessId(loginUser.getUser().getUserId());
        updateRecored.setProcessName(loginUser.getUser().getUserName());
        updateRecored.setGroupId(alarmOrderVO.getGroupId() == null ? "" : alarmOrderVO.getGroupId());
        updateRecored.setIfCurrentLink(Constants.VALID_STATE);
        updateRecored.setDeleteFlag(Constants.VALID_STATE);
        updateRecored.setFlowEndTime(DateUtils.string2Date(alarmOrderVO.getFlowNodeTime()));
        if (StringUtils.isEmpty(updateRecored.getProcessNameList())) {
            if (!StringUtils.isEmpty(alarmOrderVO.getProcessId())) {
                updateRecored.setProcessNameList(alarmOrderVO.getProcessId());
            }
        }
        // 20210721 增加语音
        updateRecored.setVoice(alarmOrderVO.getVoice());
        updateRecored.setRemark(alarmOrderVO.getRemark());
        //流转类型
        updateRecored.setFlowLinkType(alarmOrderVO.getFlowLinkType());
        updateRecored.setNextLinkId(alarmOrderVO.getLinkId());
        updateRecored.setNextLinkName(alarmOrderVO.getLinkName());
        if (!StringUtils.isEmpty(alarmOrderVO.getSendRemark())) {
            updateRecored.setRemark(alarmOrderVO.getSendRemark());
        }
       /* if(!StringUtils.isEmpty(alarmOrderVO.getEnableAcceptanceMode())){
            updateRecored.setEnableAcceptanceMode(alarmOrderVO.getEnableAcceptanceMode());
        }*/
    }

    /**
     * 功能描述：流程流转消息推送
     *
     * @return void
     * @params [alarmOrderVO, loginUser]
     * @author su.tingning
     * @date 2022/7/6 11:19
     */
    private void messagePush(SendAlarmOrderDTO alarmOrderVO, LoginUser loginUser) {
        MessagePushDTO messagePushDTO = new MessagePushDTO();
        messagePushDTO.setOrderId(alarmOrderVO.getWarningOrderId());
        messagePushDTO.setMsgId(alarmOrderVO.getWarningOrderId());
        messagePushDTO.setTenantId(alarmOrderVO.getTenantId());
        messagePushDTO.setAlarmFlowId(alarmOrderVO.getFlowId());
        messagePushDTO.setLinkId(alarmOrderVO.getLinkId());
        messagePushDTO.setNotifyType(Constants.YES_FLAG);
        messagePushDTO.setCreator(sysService.getLoginUserId());
        messagePushDTO.setNotifyTitle(alarmOrderVO.getAlarmBody());
        messagePushDTO.setTypeValue(String.valueOf(alarmOrderVO.getAlarmType()));
        messagePushDTO.setAddress(alarmOrderVO.getAddress());
        messagePushDTO.setUserType(alarmOrderVO.getUserType());
        messagePushDTO.setMsgNotifyType(alarmOrderVO.getMsgNotifyType());
        String processId = alarmOrderVO.getProcessId();
        if (!StringUtils.isEmpty(processId)) {
            if (processId.startsWith(Constants.COMMA)) {
                processId = processId.substring(1, processId.length());
            }
            if (processId.endsWith(Constants.COMMA)) {
                processId = processId.substring(0, processId.length() - 1);
            }
        }
        messagePushDTO.setUserId(processId);
        messagePushDTO.setFlowFlag(Constants.ONE);
        String roleId = alarmOrderVO.getRoleId();
        if (!StringUtils.isEmpty(roleId)) {
            if (roleId.startsWith(Constants.COMMA)) {
                roleId = roleId.substring(1, roleId.length());
            }
            if (roleId.endsWith(Constants.COMMA)) {
                roleId = roleId.substring(0, roleId.length() - 1);
            }
        }
        messagePushDTO.setRoleId(roleId);
        messagePushDTO.setNotifyType(Constants.SYS_MSG_TYPE_1);
        messagePushDTO.setOrderStatus(alarmOrderVO.getOrderStatus());
        messagePushDTO.setFlowTemplate(alarmOrderVO.getFlowTemplate());
        messagePushDTO.setAlarmTime(alarmOrderVO.getAlarmTime());
        messagePushDTO.setAppVerCode(loginUser.getUser().getAppVerCode());
        messagePushDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        messagePushDTO.setWarningTypeId(alarmOrderVO.getWarningTypeId());
        messagePushDTO.setRealLinkType(alarmOrderVO.getRealLinkType());
        messagePushDTO.setFlowOverTime(alarmOrderVO.getFlowOverTime());
        messagePushDTO.setEnableAcceptanceMode(alarmOrderVO.getNextEnableAcceptanceMode());
        receiveRepository.messagePush(messagePushDTO);
    }


    /**
     * 获取音频文件播放时长
     *
     * @param voiceDurationDTO
     * @return
     */
    @Override
    public Result getDurationByAddress(VoiceDurationDTO voiceDurationDTO) {
        if (StringUtils.isEmpty(voiceDurationDTO.getVoiceUrl())) {
            return Result.fail("音频文件地址不可为空");
        }
        Bitstream bt = null;
        BufferedInputStream bis = null;
        URLConnection con = null;
        try {
            // 获取最新的文件加密地址
            String resetVoiceUrl = fileUrlEncryptUtil.dealObsFileUrl(voiceDurationDTO.getVoiceUrl());
            URL urlfile = new URL(resetVoiceUrl);

            con = urlfile.openConnection();

            if (!ObjectUtil.isNull(con)) {
                int site = 1000;

                // 创建输入流
                bis = new BufferedInputStream(urlfile.openStream());

                // 创建比特流
                bt = new Bitstream(bis);

                // 获取头信息
                Header header = bt.readFrame();

                // 获取声道数
                int channels = header.mode() == Header.SINGLE_CHANNEL ? 1 : 2;

                // 判断声道数 1 单声道 2立体声  -- 对于小程序历史的48码率、单声道音频兼容处理
                if (channels == 2 || header.bitrate_string().equals("48 kbit/s")) {
                    site = 500;
                }

                int b = con.getContentLength();
                int time = (int) header.total_ms(b);
                voiceDurationDTO.setVoiceDuration(String.valueOf(time / site));
                log.info("获取音频文件播放时长: {}", JSON.toJSONString(voiceDurationDTO));
            }
        } catch (Exception e) {
            log.info("获取音频文件播放时长异常：", e);
        } finally {
            try {
                if (bt != null) {
                    bt.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) {
                log.info("获取音频文件播放时长异常：", e);
            }
        }
        return Result.data(voiceDurationDTO);
    }


    /**
     * 功能描述 根据设备、通道、有效参考点、型号信息等参数将百分比坐标集合转化为经纬度坐标集合返回
     *
     * @param devicePointTurn
     * @return
     */
    @Override
    public Result<DevicePointTurn> turnLatitudeByPoint(DevicePointTurn devicePointTurn) {
        log.info("WarningOrderRepositoryImpl-turnLatitudeByPoint-入参:{}", JSON.toJSONString(devicePointTurn));
        // 获取当前登录人
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user == null || StringUtils.isEmpty(user.getUserId())) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        // 查询型号信息
        SysLntlagConfigVO config = deviceRepository.getLntlagConfig(devicePointTurn.getDeviceCode());
        if (Objects.isNull(config)) {
            return Result.fail("摄像机关联型号信息为空,无法进行经纬度转换");
        }
        // 查询设备经纬度 挂高
        DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(devicePointTurn.getDeviceCode(), null);
        String height = deviceSiteVO == null ? null : deviceSiteVO.getHeight();
        if (StringUtils.isEmpty(height)) {
            return Result.fail("摄像机挂高为空,无法进行经纬度转换");
        }
        // 设置摄像机经纬度，挂高
        devicePointTurn.setLatitude(deviceSiteVO.getLatitude());
        devicePointTurn.setLongitude(deviceSiteVO.getLongitude());
        devicePointTurn.setHeight(deviceSiteVO.getHeight());

        // 处理水平角偏差和俯仰角偏差   通过有效参考点进行计算
        List<DeviceRefPointVO> pointsValid = deviceRepository.getDevicePointsValid(devicePointTurn.getDeviceCode());
        if (CollectionUtils.isEmpty(pointsValid)) {
            return Result.fail("摄像机未配置有效参考点,无法进行经纬度转换");
        }

        PresetData resultPtz = new PresetData();
        //入参有自定义PTZ时使用入参所传的PTZ，否则获取设备当前PTZ
        if (!StringUtils.isEmpty(devicePointTurn.getParamPan()) && !StringUtils.isEmpty(devicePointTurn.getParamTitl())
                && !StringUtils.isEmpty(devicePointTurn.getParamTitl())) {
            resultPtz.setPan(Float.parseFloat(devicePointTurn.getParamPan()));
            resultPtz.setTilt(Float.parseFloat(devicePointTurn.getParamTitl()));
            resultPtz.setZoom(Float.parseFloat(devicePointTurn.getParamZoom()));
        } else {
            //调用6.15查询当前设备当前PTZ
            VideoBaseReq videoBaseReq = new VideoBaseReq();
            videoBaseReq.setDeviceCode(devicePointTurn.getDeviceCode());
            videoBaseReq.setChannelCode(devicePointTurn.getChannelCode());
            videoBaseReq.setTenantId(user.getTenantId());
            Result<PresetData> ptzResult = videoQueryAPI.queryPtzPosition(videoBaseReq);
            if (ResultCode.SUCCESS.getCode() != ptzResult.getCode()) {
                return Result.fail(ptzResult.getMsg());
            }
            resultPtz = ptzResult.getData();
        }

        if (ObjectUtil.isNotNull(resultPtz.getPan()) && ObjectUtil.isNotNull(resultPtz.getTilt()) && ObjectUtil.isNotNull(resultPtz.getZoom())) {
            TranslnglatDTO lnglat = new TranslnglatDTO();
            lnglat.setPan(String.valueOf(resultPtz.getPan()));
            lnglat.setTilt(String.valueOf(resultPtz.getTilt()));
            lnglat.setZoom(String.valueOf(resultPtz.getZoom()));
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            deviceModelVO.setRealPan(lnglat.getPan());
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

            lnglat.setC(config.getC());
            lnglat.setD(config.getD());
            lnglat.setF0(config.getF0());
            lnglat.setM(config.getM());
            lnglat.setN(config.getN());
            lnglat.setH(height);
            if (!StringUtils.isEmpty(devicePointTurn.getParamLatitude()) && !StringUtils.isEmpty(devicePointTurn.getParamLongitude())) {
                //入参有自定义经纬度时用入参的经纬度，否则用设备的经纬度
                lnglat.setLatitude(devicePointTurn.getParamLatitude());
                lnglat.setLongitude(devicePointTurn.getParamLongitude());
            } else {
                lnglat.setLatitude(deviceSiteVO.getLatitude());
                lnglat.setLongitude(deviceSiteVO.getLongitude());
            }

            for (DevicePointVo devicePointVo : devicePointTurn.getPoints()) {
                try {
                    lnglat.setUx(devicePointVo.getPercentX());
                    lnglat.setVy(devicePointVo.getPercentY());
                    log.info("百分比坐标集合转化为经纬度坐标集合：入参：{}", JSON.toJSONString(lnglat));
                    TranslnglatVO translnglatRep = AlarmLocationUtils.getAlarmLocation(lnglat);
                    log.info("百分比坐标集合转化为经纬度坐标集合：返回：{}", translnglatRep.toString());
                    devicePointVo.setLatitude(translnglatRep.getLatitude());
                    devicePointVo.setLongitude(translnglatRep.getLongitude());
                } catch (Exception e) {
                    log.error("百分比坐标集合转化为经纬度异常:" + e);
                }
            }
        }
        log.info("WarningOrderRepositoryImpl-turnLatitudeByPoint-出参:{}", JSON.toJSONString(devicePointTurn));
        return Result.data(devicePointTurn);
    }

    @Resource
    private GisPgAPI gisPgAPI;

    /**
     * 获取设备参数信息
     *
     * @param camaraDeviceParamsQueryDTO 查询参数
     */
    @Override
    public Result<CameraCoordRes> getCamaraDeviceParamsInfo(CamaraDeviceParamsQueryDTO camaraDeviceParamsQueryDTO) {
        //组装查询参数
        DevicePointTurn devicePointTurnQuery = new DevicePointTurn();
        devicePointTurnQuery.setDeviceCode(camaraDeviceParamsQueryDTO.getDeviceCode());
        devicePointTurnQuery.setChannelCode(camaraDeviceParamsQueryDTO.getChannelCode());
        DevicePointVo devicePointVoQuery = new DevicePointVo();
        devicePointVoQuery.setPercentX(camaraDeviceParamsQueryDTO.getPercentX());
        devicePointVoQuery.setPercentY(camaraDeviceParamsQueryDTO.getPercentY());
        devicePointTurnQuery.setPoints(Lists.newArrayList(devicePointVoQuery));
        log.info("get camera coord 查询receiveAlarmService.turnLatitudeByPoint 传参:{}", devicePointTurnQuery);
        Result<DevicePointTurn> result = turnLatitudeByPoint(devicePointTurnQuery);
        log.info("get camera coord 查询receiveAlarmService.turnLatitudeByPoint 对应结果:{}", result);
        DevicePointTurn devicePointTurnResult = result.getData();
        if (ObjectUtil.isNull(devicePointTurnResult)) {
            log.error("get camera coord result 接口查询返回data为空");
            return Result.fail(result.getMsg());
        }
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(devicePointTurnResult.getPoints())) {
            log.error("get camera coord points is null 未查询到设备对应点位信息");
            return Result.fail("点位坐标信息查询为空");
        }
        //组装返回参数
        DevicePointVo devicePointVo =
                devicePointTurnResult.getPoints().stream().filter(devicePointVo1 -> devicePointVo1.getPercentX().equals(camaraDeviceParamsQueryDTO.getPercentX()) && devicePointVo1.getPercentY().equals(camaraDeviceParamsQueryDTO.getPercentY())).findAny().orElse(null);
        log.info("get camera coord 查询deviceCode:{}设备对应点位信息:{}", camaraDeviceParamsQueryDTO.getDeviceCode(), devicePointVo);
        if (ObjectUtil.isNull(devicePointVo)) {
            log.error("get camera coord " + camaraDeviceParamsQueryDTO.getDeviceCode() + "未查询到设备对应点位信息");
            return Result.fail("没有符合的点位坐标信息");
        }
        CamaraDeviceParamsInfoVO camaraDeviceParamsInfoVO = new CamaraDeviceParamsInfoVO();
        camaraDeviceParamsInfoVO.setLongitude(Double.valueOf(devicePointTurnResult.getLongitude()));
        camaraDeviceParamsInfoVO.setLatitude(Double.valueOf(devicePointTurnResult.getLatitude()));
        camaraDeviceParamsInfoVO.setLocationLongitude(Double.valueOf(devicePointVo.getLongitude()));
        camaraDeviceParamsInfoVO.setLocationLatitude(Double.valueOf(devicePointVo.getLatitude()));
        camaraDeviceParamsInfoVO.setHeight(Double.valueOf(devicePointTurnResult.getHeight()));
        camaraDeviceParamsInfoVO.setPageSize(getCameraPageSize);
        camaraDeviceParamsInfoVO.setRadius(getCameraRadius);
        return gisPgAPI.getCameraCoord(camaraDeviceParamsInfoVO);
    }

    /**
     * 针对告警来源为“AI告警”
     * 在“事件描述”字段中展示相应算法配置的参数
     * （即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
     *
     * @param orderInfoVO 传入参数 格式
     */
    @Override
    public String buildAlgorithmConfigDescField(OrderInfoVO orderInfoVO) {
        if (StringUtils.isEmpty(orderInfoVO.getWarningSource())) {
            log.error("生成告警事-匹配算法，不存在waringSource, warningOrderId:{}", orderInfoVO.getWarningOrderId());
            return "";
        }
        if (StringUtils.isEmpty(orderInfoVO.getTaskId())) {
            log.error("生成告警事-匹配算法，AI告警的taskId为空，返回空，warningOrderId:{}", orderInfoVO.getWarningOrderId());
            return "";
        }
        //只处理AI告警
        if (!WARNING_SOURCE_1.equals(orderInfoVO.getWarningSource())) {
            log.error("生成告警事-匹配算法，不是AI告警，返回空，warningOrderId:{}, warningSource:{}", orderInfoVO.getWarningOrderId(), orderInfoVO.getWarningSource());
            return "";
        }
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtil.isNotEmpty(orderInfoVO.getDeviceCode())) {
            params.put(DESC_DEVICE_CODE_PARAM, Lists.newArrayList(orderInfoVO.getDeviceCode()));
        }
        if (StringUtil.isNotEmpty(orderInfoVO.getChannelCode())) {
            params.put(DESC_CHANNEL_CODE_PARAM, Lists.newArrayList(orderInfoVO.getChannelCode()));
        }
        if (StringUtil.isNotEmpty(orderInfoVO.getTaskId())) {
            params.put(DESC_TASK_ID_PARAM, Lists.newArrayList(orderInfoVO.getTaskId()));
        }
        if (StringUtil.isNotEmpty(orderInfoVO.getAlgorithmCode())) {
            params.put(DESC_ALGORITHM_CODE_PARAM, Lists.newArrayList(orderInfoVO.getAlgorithmCode()));
        }
        if (StringUtil.isNotEmpty(orderInfoVO.getTenantId())) {
            params.put(DESC_TENANT_ID_PARAM, Lists.newArrayList(orderInfoVO.getTenantId()));
        }
        return buildDescriptionField(orderInfoVO, getDbAlgorithmConfigVOs(params));
    }

    /**
     * 从数据库查询算法配置
     *
     * @param maps 入参
     */
    private List<AlarmAlgorithmConfigVO> getDbAlgorithmConfigVOs(Map<String, Object> maps) {
        //根据摄像机查询算法
        Map<String, Object> params = Maps.newHashMap();
        params.put(DESC_DEVICE_CODE_PARAM, MapUtils.getObject(maps, DESC_DEVICE_CODE_PARAM));
        params.put(DESC_CHANNEL_CODE_PARAM, MapUtils.getObject(maps, DESC_CHANNEL_CODE_PARAM));
        params.put(DESC_TASK_ID_PARAM, MapUtils.getObject(maps, DESC_TASK_ID_PARAM));
        params.put(DESC_ALGORITHM_CODE_PARAM, MapUtils.getObject(maps, DESC_ALGORITHM_CODE_PARAM));
        params.put(DESC_TENANT_ID_PARAM, MapUtils.getObject(maps, DESC_TENANT_ID_PARAM));
        return warningOrderMapper.queryAlgorithmConfig(params);
    }


    /**
     *
     * @param orderInfoVO        告警对象
     * @param algorithmConfigVOS 配置算法列表
     */
    private String buildDescriptionField(OrderInfoVO orderInfoVO, List<AlarmAlgorithmConfigVO> algorithmConfigVOS) {
        log.info("生成告警事-匹配算法，warningOrderId:{} 匹配到的算法为: {}", orderInfoVO.getWarningOrderId(), algorithmConfigVOS);
        if (CollectionUtils.isEmpty(algorithmConfigVOS)) {
            log.info("生成告警事-匹配算法-没有匹配到算法 warningOrderId:{}", orderInfoVO.getWarningOrderId());
            return "";
        }
        //根据device_code，channel_code，algorithm_code，task_id查询出来唯一的算法配置
        //防止传参问题，匹配多个算法
        List<Map<String, Object>> descList = algorithmConfigVOS.stream().map(algorithmConfigVO -> {
            Map<String, Object> map = Maps.newHashMap();
            // 大模型按新规则组装高参
            if (algorithmConfigVO.getModelType() != null
                    && algorithmConfigVO.getModelType() == com.chinatower.platform.constants.Constants.ONE_NUM) {
                buildBigModelDescMap(orderInfoVO, algorithmConfigVO, map);
            } else {
                // 非大模型保持原有结构，兼容旧前端
                map.put(DESC_ANALYSIS_PERIOD_RESP, GsonUtils.getGson().fromJson(algorithmConfigVO.getAnalysisPeriod(), Object.class));
                map.put(DESC_TIMEINTERVAL_RESP, timeUnitConvertInverse(algorithmConfigVO.getTimeIntervalUnit(), algorithmConfigVO.getTimeInterval()));
                map.put(DESC_TIMEINTERVAL_UNIT_RESP, algorithmConfigVO.getTimeIntervalUnit());
                map.put(DESC_MODEL_CONFIG_RESP, GsonUtils.getGson().fromJson(algorithmConfigVO.getModelConfig(), Object.class));
            }
            return map;
        }).collect(Collectors.toList());
        //组装json，塞入description字段，转换为JSON时保存null
        log.info("生成告警事-匹配算法，warningOrderId:{} 匹配到的算法description: {}", orderInfoVO.getWarningOrderId(), GsonUtils.toJSONString(descList));
        return GsonUtils.toJSONString(descList);
    }

    /**
     * 按大模型算法规则构建描述字段
     *
     * @param orderInfoVO       告警对象
     * @param algorithmConfigVO 算法配置
     * @param map               输出 map
     */
    private void buildBigModelDescMap(OrderInfoVO orderInfoVO, AlarmAlgorithmConfigVO algorithmConfigVO, Map<String, Object> map) {
        // 解析 modelConfig 高参
        JsonArray configArray = GsonUtils.parseJsonArray(algorithmConfigVO.getModelConfig());
        if (configArray == null || configArray.size() == 0) {
            return;
        }

        String province = "";
        // 汇总所有区域的分析时间段，保持 periodId/startTime/endTime 结构
        List<JsonObject> analysisPeriodList = new ArrayList<>();
        // 通道场景下的识别频率列表，每个元素包含 timeInterval 与 timeIntervalUnit
        List<Map<String, Object>> timeIntervalList = new ArrayList<>();
        // 通道场景下用于展示的识别频率文案列表
        List<String> timeIntervalTextList = new ArrayList<>();
        // 密度阈值文案列表（应急视觉/农业视觉）
        List<String> triggerNumberTextList = new ArrayList<>();
        // 密度阈值展示名称，随 code 类型区分车辆/人员
        String triggerNumberDisplayName = null;
        // 从字典读取大模型视觉分析 modelConfig code 列表
        Set<String> cvModeConfigCodes = new HashSet<>(getBigModelCvModeConfigCodes());
        log.info("buildBigModelDescMap-大模型视觉配置code,warningOrderId:{},cvModeConfigCodes:{}",
                orderInfoVO.getWarningOrderId(), cvModeConfigCodes);

        for (JsonElement element : configArray) {
            if (element == null || element.isJsonNull() || !element.isJsonObject()) {
                continue;
            }
            JsonObject obj = element.getAsJsonObject();
            String code = obj.has(BigModelDescConstants.JsonField.CODE) && !obj.get(BigModelDescConstants.JsonField.CODE).isJsonNull()
                    ? obj.get(BigModelDescConstants.JsonField.CODE).getAsString() : null;
            if (StringUtils.isEmpty(code)) {
                continue;
            }
            // 省份：dict-province
            if (BigModelDescConstants.ModelConfigCode.PROVINCE.equals(code)) {
                // 兼容 value / selectValue 两种取值
                String value = obj.has(BigModelDescConstants.JsonField.SELECT_VALUE) && !obj.get(BigModelDescConstants.JsonField.SELECT_VALUE).isJsonNull()
                        ? obj.get(BigModelDescConstants.JsonField.SELECT_VALUE).getAsString() : "";
                if (StringUtils.isEmpty(value) && obj.has(BigModelDescConstants.JsonField.VALUE) && !obj.get(BigModelDescConstants.JsonField.VALUE).isJsonNull()) {
                    value = obj.get(BigModelDescConstants.JsonField.VALUE).getAsString();
                }
                province = value;
            }
            // 大模型场景高参：从字典 large_model_draw_area_config_code 读取 modelConfig code
            else if (cvModeConfigCodes.contains(code)) {
                // 应急/农业视觉需同步收集 triggerNumber，展示名称按场景区分
                String currentTriggerNumberDisplayName = resolveTriggerNumberDisplayName(code);
                JsonArray pointsRelation = obj.has(BigModelDescConstants.JsonField.POINTS_RELATION)
                        && obj.get(BigModelDescConstants.JsonField.POINTS_RELATION).isJsonArray()
                        ? obj.getAsJsonArray(BigModelDescConstants.JsonField.POINTS_RELATION) : null;
                if (pointsRelation == null || pointsRelation.size() == 0) {
                    continue;
                }
                // 用于过滤重复的时间段，按 periodId 去重；识别频率与运行时段一一对应，同步去重
                Set<String> analysisPeriodKeySet = new HashSet<>();
                for (JsonElement prElem : pointsRelation) {
                    if (prElem == null || prElem.isJsonNull() || !prElem.isJsonObject()) {
                        continue;
                    }
                    JsonObject prObj = prElem.getAsJsonObject();
                    // 运行时段：analysisPeriod 为 JSON 字符串，解析后直接保留原始结构
                    if (prObj.has(BigModelDescConstants.JsonField.ANALYSIS_PERIOD)
                            && !prObj.get(BigModelDescConstants.JsonField.ANALYSIS_PERIOD).isJsonNull()) {
                        String apStr = prObj.get(BigModelDescConstants.JsonField.ANALYSIS_PERIOD).getAsString();
                        JsonArray apArray = GsonUtils.parseJsonArray(apStr);
                        if (apArray != null && apArray.size() > 0) {
                            for (JsonElement apElem : apArray) {
                                if (apElem == null || apElem.isJsonNull() || !apElem.isJsonObject()) {
                                    continue;
                                }
                                JsonObject apObj = apElem.getAsJsonObject();
                                String key = apObj.has(BigModelDescConstants.JsonField.PERIOD_ID)
                                        && !apObj.get(BigModelDescConstants.JsonField.PERIOD_ID).isJsonNull()
                                        ? apObj.get(BigModelDescConstants.JsonField.PERIOD_ID).getAsString() : "";
                                if (!analysisPeriodKeySet.contains(key)) {
                                    analysisPeriodKeySet.add(key);
                                    analysisPeriodList.add(apObj);
                                    // 应急/农业视觉：与运行时段同步收集密度阈值
                                    if (currentTriggerNumberDisplayName != null
                                            && prObj.has(BigModelDescConstants.JsonField.TRIGGER_NUMBER)
                                            && !prObj.get(BigModelDescConstants.JsonField.TRIGGER_NUMBER).isJsonNull()) {
                                        String triggerNumber = prObj.get(BigModelDescConstants.JsonField.TRIGGER_NUMBER).getAsString();
                                        if (!StringUtils.isEmpty(triggerNumber)) {
                                            triggerNumberDisplayName = currentTriggerNumberDisplayName;
                                            triggerNumberTextList.add(triggerNumber);
                                        }
                                    }
                                    // 识别频率与运行时段一致：每增加一条运行时段（按 periodId 去重），同步增加该条 pointsRelation 的识别频率（仅通道）
                                    if (algorithmConfigVO.getPosType() != null
                                            && algorithmConfigVO.getPosType() == com.chinatower.platform.constants.Constants.ONE_NUM
                                            && prObj.has(BigModelDescConstants.JsonField.TIME_INTERVAL)
                                            && !prObj.get(BigModelDescConstants.JsonField.TIME_INTERVAL).isJsonNull()) {
                                        int intervalSec = prObj.get(BigModelDescConstants.JsonField.TIME_INTERVAL).getAsInt();
                                        if (intervalSec > 0) {
                                            int unit;
                                            int value;
                                            if (intervalSec >= 3600) {
                                                value = intervalSec / 3600;
                                                unit = com.chinatower.platform.constants.Constants.ONE_NUM;
                                            } else if (intervalSec >= 60) {
                                                value = intervalSec / 60;
                                                unit = com.chinatower.platform.constants.Constants.TWO_NUM;
                                            } else {
                                                value = intervalSec;
                                                unit = com.chinatower.platform.constants.Constants.THREE_NUM;
                                            }
                                            Map<String, Object> intervalMap = new HashMap<>(2);
                                            intervalMap.put(DESC_TIMEINTERVAL_RESP, value);
                                            intervalMap.put(DESC_TIMEINTERVAL_UNIT_RESP, unit);
                                            timeIntervalList.add(intervalMap);
                                            String unitLabel = unit == com.chinatower.platform.constants.Constants.ONE_NUM
                                                    ? BigModelDescConstants.TimeIntervalUnitLabel.HOUR
                                                    : (unit == com.chinatower.platform.constants.Constants.TWO_NUM
                                                    ? BigModelDescConstants.TimeIntervalUnitLabel.MINUTE
                                                    : BigModelDescConstants.TimeIntervalUnitLabel.SECOND);
                                            timeIntervalTextList.add(value + unitLabel);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 省分
        Map<String, String> provinceMap = new HashMap<>();
        provinceMap.put(com.chinatower.platform.constants.Constants.NAME, BigModelDescConstants.DisplayName.PROVINCE);
        provinceMap.put(com.chinatower.platform.constants.Constants.VALUE, province);
        // 使用可变列表，便于后续追加识别频率等配置项
        List<Map<String, String>> modelConfigList = new ArrayList<>();
        modelConfigList.add(provinceMap);
        // 通道场景时需要返回识别频率，结构为 [{timeInterval:xxx,timeIntervalUnit:xxx},...]
        if (algorithmConfigVO.getPosType() != null
                && algorithmConfigVO.getPosType() == com.chinatower.platform.constants.Constants.ONE_NUM) {
            Map<String, String> timeIntervalMap = new HashMap<>();
            timeIntervalMap.put(com.chinatower.platform.constants.Constants.NAME, BigModelDescConstants.DisplayName.TIME_INTERVAL);
            // 将多个识别频率拼接为字符串，以英文逗号分隔
            timeIntervalMap.put(com.chinatower.platform.constants.Constants.VALUE, String.join(",", timeIntervalTextList));
            modelConfigList.add(timeIntervalMap);
        }
        // 应急/农业视觉场景返回密度阈值
        if (!triggerNumberTextList.isEmpty() && !StringUtils.isEmpty(triggerNumberDisplayName)) {
            Map<String, String> triggerNumberMap = new HashMap<>();
            triggerNumberMap.put(com.chinatower.platform.constants.Constants.NAME, triggerNumberDisplayName);
            triggerNumberMap.put(com.chinatower.platform.constants.Constants.VALUE, String.join(",", triggerNumberTextList));
            modelConfigList.add(triggerNumberMap);
        }
        map.put(DESC_MODEL_CONFIG_RESP, modelConfigList);
        // 算法运行时段，直接返回 periodId/startTime/endTime 的数组结构
        map.put(DESC_ANALYSIS_PERIOD_RESP, analysisPeriodList);
    }

    /**
     * 从字典读取大模型视觉分析 modelConfig code 列表
     */
    private List<String> getBigModelCvModeConfigCodes() {
        List<SysDictDataResponse> dictList = warningOrderUtil.getDict(BigModelDescConstants.LARGE_MODEL_DRAW_AREA_CONFIG_CODE);
        if (CollectionUtils.isEmpty(dictList)) {
            log.info("大模型视觉配置字典为空,dictType:{}, 使用默认值", BigModelDescConstants.LARGE_MODEL_DRAW_AREA_CONFIG_CODE);
            return getDefaultBigModelCvModeConfigCodes();
        }
        List<String> configCodes = dictList.stream()
                .map(SysDictDataResponse::getDictValue)
                .filter(code -> !StringUtils.isEmpty(code))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configCodes)) {
            log.info("大模型视觉配置字典值为空,dictType:{}, 使用默认值", BigModelDescConstants.LARGE_MODEL_DRAW_AREA_CONFIG_CODE);
            return getDefaultBigModelCvModeConfigCodes();
        }
        return configCodes;
    }

    /**
     * 字典未配置时的默认 modelConfig code 列表
     */
    private List<String> getDefaultBigModelCvModeConfigCodes() {
        return Arrays.asList(
                BigModelDescConstants.ModelConfigCode.CV_MODE_TYPE,
                BigModelDescConstants.ModelConfigCode.EMERGENCY_CV_MODE_TYPE,
                BigModelDescConstants.ModelConfigCode.AGRICULTURE_CV_MODE_TYPE,
                BigModelDescConstants.ModelConfigCode.WATER_CV_MODE_TYPE
        );
    }

    /**
     * 根据 modelConfig code 解析密度阈值展示名称
     *
     * @param code modelConfig 配置项 code
     * @return 展示名称；无需收集 triggerNumber 时返回 null
     */
    private String resolveTriggerNumberDisplayName(String code) {
        if (BigModelDescConstants.ModelConfigCode.EMERGENCY_CV_MODE_TYPE.equals(code)) {
            return BigModelDescConstants.DisplayName.VEHICLE_DENSITY_THRESHOLD;
        }
        if (BigModelDescConstants.ModelConfigCode.AGRICULTURE_CV_MODE_TYPE.equals(code)) {
            return BigModelDescConstants.DisplayName.PERSON_DENSITY_THRESHOLD;
        }
        return null;
    }

    /**
     * 根据时间单位，转换为秒（逆转）
     *
     * @param timeIntervalUnit 抽帧频率单位,1:小时 2:分钟 3:秒
     * @param timeInterval     时间
     */
    private Integer timeUnitConvertInverse(Integer timeIntervalUnit, Integer timeInterval) {
        Integer result = null;
        if (!ObjectUtil.isEmpty(timeIntervalUnit)) {
            switch (timeIntervalUnit) {
                case com.chinatower.platform.constants.Constants.ONE_NUM:
                    result = timeInterval / 60 / 60;
                    break;
                case com.chinatower.platform.constants.Constants.TWO_NUM:
                    result = timeInterval / 60;
                    break;
                case com.chinatower.platform.constants.Constants.THREE_NUM:
                    result = timeInterval;
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * 针对告警来源为“AI告警” 列表
     * 在“事件描述”字段中展示相应算法配置的参数
     * （即在“配置管理-算法配置”功能中配置的算法参数名称与参数阈值）
     *
     * @param req 传入参数 格式
     */
    @Override
    public Map<String, String> buildAlgorithmConfigDescFieldMap(AiAlgorithmConfigReq req) {
        Map<String, String> map = Maps.newHashMap();
        Object obj = req.getParams();
        if (!(obj instanceof List)) {
            return map;
        }
        JSONArray orderInfos = JSON.parseArray(JSON.toJSONString(obj));
        if (Objects.isNull(orderInfos) || orderInfos.isEmpty()) {
            return map;
        }
        String orderIdAlias = req.getOrderIdAlias();
        String orderSourceAlias = req.getOrderSourceAlias();
        String taskIdAlias = req.getTaskIdAlias();
        String teanatIdAlias = req.getTeanatIdAlias();
        String deviceCodeAlias = req.getDeviceCodeAlias();
        String channelCodeAlias = req.getChannelCodeAlias();
        String algorithmCodeAlias = req.getAlgorithmCodeAlias();
        List<String> taskIdList = Lists.newArrayList();
        List<String> tenantIdList = Lists.newArrayList();
        List<String> deviceCodeList = Lists.newArrayList();
        List<String> channelCodeList = Lists.newArrayList();
        List<String> algorithmCodeList = Lists.newArrayList();
        for (Object orderInfo : orderInfos) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(orderInfo));
            //只处理AI告警
            if (!WARNING_SOURCE_1.equals(jsonObject.getString(orderSourceAlias))) {
                continue;
            }
            if (StringUtils.isEmpty(jsonObject.getString(orderIdAlias))) {
                log.error("生成告警事-匹配算法，不存在waringSource, warningOrderId:{}", jsonObject.getString(orderIdAlias));
                continue;
            }
            if (!jsonObject.containsKey(taskIdAlias) && StringUtils.isEmpty(jsonObject.getString(taskIdAlias))) {
                log.error("生成告警事-匹配算法，AI告警的taskId为空，返回空，warningOrderId:{}", jsonObject.getString(orderIdAlias));
                continue;
            }
            taskIdList.add(jsonObject.getString(taskIdAlias));
            tenantIdList.add(jsonObject.getString(teanatIdAlias));
            if (jsonObject.containsKey(deviceCodeAlias) && !StringUtils.isEmpty(jsonObject.getString(deviceCodeAlias))) {
                deviceCodeList.add(jsonObject.getString(deviceCodeAlias));
            }
            if (jsonObject.containsKey(channelCodeAlias) && !StringUtils.isEmpty(jsonObject.getString(channelCodeAlias))) {
                channelCodeList.add(jsonObject.getString(channelCodeAlias));
            }
            if (jsonObject.containsKey(algorithmCodeAlias) && !StringUtils.isEmpty(jsonObject.getString(algorithmCodeAlias))) {
                algorithmCodeList.add(jsonObject.getString(algorithmCodeAlias));
            }
        }
        if (CollectionUtils.isEmpty(taskIdList)) {
            //任务ID为空，不查询
            return Maps.newHashMap();
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put(DESC_TASK_ID_PARAM, taskIdList);
        params.put(DESC_TENANT_ID_PARAM, tenantIdList);
        params.put(DESC_DEVICE_CODE_PARAM, deviceCodeList);
        params.put(DESC_CHANNEL_CODE_PARAM, channelCodeList);
        params.put(DESC_ALGORITHM_CODE_PARAM, algorithmCodeList);
        List<AlarmAlgorithmConfigVO> algorithmConfigVOS = getDbAlgorithmConfigVOs(params);
        //根据taskId&tenantId&deviceCode&channelCode分组
        Map<String, List<AlarmAlgorithmConfigVO>> algorithmMap =
                algorithmConfigVOS.stream().collect(Collectors.groupingBy(
                        key -> key.getTaskId() + SPLIT_AND + key.getTenantId()
                                + SPLIT_AND + key.getDeviceCode() + SPLIT_AND + key.getChannelCode()
                                + SPLIT_AND + key.getAlgorithmCode()
                ));
        String key;
        List<AlarmAlgorithmConfigVO> tempAlgorithmList;
        String warningOrderId, deviceCode, channelCode, taskId, algorithmCode, tenantId;
        OrderInfoVO orderInfoVO;
        for (Object orderInfo : orderInfos) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(orderInfo));
            //只处理AI告警
            if (!WARNING_SOURCE_1.equals(jsonObject.getString(orderSourceAlias))) {
                continue;
            }
            warningOrderId = jsonObject.getString(orderIdAlias);
            taskId = jsonObject.getString(taskIdAlias);
            tenantId = jsonObject.getString(teanatIdAlias);
            deviceCode = jsonObject.getString(deviceCodeAlias);
            channelCode = jsonObject.getString(channelCodeAlias);
            algorithmCode = jsonObject.getString(algorithmCodeAlias);
            key = taskId + SPLIT_AND + tenantId + SPLIT_AND + deviceCode + SPLIT_AND + channelCode + SPLIT_AND + algorithmCode;
            tempAlgorithmList = algorithmMap.get(key);
            if (CollectionUtils.isEmpty(tempAlgorithmList)) {
                //空，返回空
                map.put(warningOrderId, "");
            } else {
                orderInfoVO = new OrderInfoVO();
                orderInfoVO.setWarningOrderId(warningOrderId);
                map.put(warningOrderId, buildDescriptionField(orderInfoVO, tempAlgorithmList));
            }
        }
        return map;
    }

    public List<Map<String, String>> getOrgInfoList(List<OrganInfoReq> reqList) {
        return warningOrderUtil.getOrgInfoList(reqList);
    }

    /**
     * 根据时间查询该租户行业下产生过告警的告警类型
     *
     * @param dto
     * @return
     */
    @Override
    public List<String> findAlarmTypesByTime(AlarmOrderDTO dto) {
        log.info("findAlarmTypesByTime param:{}", JSON.toJSONString(dto));
        if (StringUtils.isEmpty(dto.getTenantId())) {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            dto.setTenantId(user.getTenantId());
            dto.setIndustryCode(user.getIndustryCode());
        }
        // 查询开始结束时间
        Date startDate = null;
        Date endDate = null;
        if (Objects.isNull(dto.getAlarmTimeStart())) {
            //如果未传告警时间,设置默认时间范围（半年内）
            startDate = DateUtils.getForeSixMonth(new Date());
        } else {
            startDate = dto.getAlarmTimeStart();
        }
        if (Objects.isNull(dto.getAlarmTimeEnd())) {
            endDate = new Date();
        } else {
            endDate = dto.getAlarmTimeEnd();
        }
        dto.setAlarmTimeStart(startDate);
        dto.setAlarmTimeEnd(endDate);
        int errCount = 0;
        boolean stop = false;

        Set<String> alarmTypeSet = new HashSet<>();
        // 根据排序规则判断从最后一个表开始向前获取或从开始向后获取
        while (!stop) {
            try {
                //正序从开始获取
                String tableName = null;
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                    tableName = TableUtil.getTableName(startDate, TableConstant.WARN_ORDER_TEBLE);
                } else {
                    tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ORDER_TEBLE);
                }
                dto.setTableName(tableName);
                List<String> queryResult = warningOrderMapper.findAlarmTypesByTime(dto);
                if (!CollectionUtils.isEmpty(queryResult)) {
                    alarmTypeSet.addAll(queryResult);
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                if (!StringUtils.isEmpty(dto.getSortName()) && EnumUtil.Order.ASC.getName().equals(dto.getSort())) {
                    // 转到下月
                    String nextMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(startDate));
                    startDate = DateUtils.stringToDateYM(nextMonth);
                } else {
                    // 转到上月
                    String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                    endDate = DateUtils.stringToDateYM(lastMonth);
                }
            } catch (Exception e) {
                log.error("查询租户行业下产生过告警的告警类型编码错误：" + e.getMessage());

                // 防止死循环  报错三次返回
                errCount++;
                if (errCount > 3) {
                    log.warn("查询告警类型编码错误次数超过3次，终止查询");
                    break;
                }
                stop = DateUtils.sameMouth(startDate, endDate);
                // 转到上月
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(endDate));
                endDate = DateUtils.stringToDateYM(lastMonth);
            }
        }
        return new ArrayList<>(alarmTypeSet);
    }

    /**
     * 查询顺序配置
     *
     * @param configVo
     * @return
     */
    @Override
    public Result queryOrderConfig(AlarmDetailConfigVo configVo) {
        // 获取当前登录人
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user == null || StringUtils.isEmpty(user.getUserId())) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        String userId = configVo.getUserId();
        if (StringUtils.isEmpty(userId)) {
            userId = user.getUserId();
        }

        List<AlarmDetailConfigVo> configList = warningOrderMapper.queryOrderConfig(userId);

        if (CollectionUtils.isEmpty(configList)) {
            //如果没有，生成默认初始化配置数据
            configList = generateDefaultConfig(user);

            //插入默认数据
            warningOrderMapper.insertDefaultConfig(configList);
        }
        return Result.data(configList);
    }

    //生成默认顺序配置
    private List<AlarmDetailConfigVo> generateDefaultConfig(SysUser user) {
        List<AlarmDetailConfigVo> defaultConfig = new ArrayList<>();
        String[] displayContents = {Constants.PARAM_FILE_IMG_URL_SRC, Constants.PARAM_FILE_IMG_URL_ICON, Constants.PARAM_VIDEO_URL};
        for (int i = 0; i < displayContents.length; i++) {
            AlarmDetailConfigVo config = new AlarmDetailConfigVo();
            config.setUserId(user.getUserId());
            config.setTenantId(user.getTenantId());
            config.setIndustryCode(user.getIndustryCode());
            config.setAppVerCode(user.getAppVerCode());
            config.setDisplayContent(displayContents[i]);
            config.setDisplayOrder(i + 1);
            config.setCreator(user.getUserName());
            defaultConfig.add(config);
        }
        log.info("queryOrderConfig-默认顺序配置：{}", JSON.toJSONString(defaultConfig));
        return defaultConfig;
    }

    /**
     * 更新顺序配置
     *
     * @param list
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateOrderConfig(List<AlarmDetailConfigVo> list) {
        log.info("updateOrderConfig-修改顺序配置：入参：{}", JSON.toJSONString(list));
        try {
            warningOrderMapper.updateOrderConfig(list);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            log.error("修改顺序配置失败：{}", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(ResultCode.ERROR, e.getMessage());
        }
    }

    @Override
    public JSONObject synIntelligentJudgeTask(IntelligentJudgeTaskRequest taskRequest) {
        return asyncCommon.intelligentJudgeTask(taskRequest);
    }

    @Override
    public void updateSecondRecogResultV2(List<SecondRecogBackVO> list) {
        try {
            List<SecondRecogBackVO> updateList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(list)) {
                for(SecondRecogBackVO backVO : list ) {
                    String [] args = backVO.getTaskId().split("/");
                    String tableType = args[1];
                    if (Constants.FOUR.equals(tableType)) {
                        // 处理遥感算法任务图片识别
                        updateList.add(backVO);
                    } else if(Constants.FIVE.equals(tableType)) {
                        //单期遥感解析推送结果信息至kafka
                        AlgorithmPicResultVO algorithmPicResultVO = new AlgorithmPicResultVO();
                        algorithmPicResultVO.setTaskInfoId(Long.parseLong(args[2]));
                        algorithmPicResultVO.setSecondRecogResult(JSONUtil.toJsonStr(backVO));
                        log.info("单期遥感解析推送结果信息至 {}",JSON.toJSONString(algorithmPicResultVO));
                        kafkaProducer.sendToKafka2(Constants.EPOCHAL_GEO_ANALYTICS_RESULT,JSON.toJSONString(algorithmPicResultVO));
                    }
                }
                if(!CollectionUtils.isEmpty(updateList)) {
                    dutySensingAlgorithmTask(list);
                }
            }
        } catch (Exception e) {
            log.error("========二次配置识别结果回调失败", e);
        }
    }
}
