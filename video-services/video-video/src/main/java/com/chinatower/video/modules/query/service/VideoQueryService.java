package com.chinatower.video.modules.query.service;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmAreaUpdateReq;
import com.chinatower.event.entity.dto.DevicePointTurn;
import com.chinatower.event.entity.dto.DevicePointVo;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO;
import com.chinatower.platform.entity.device.AlgorithmMonitorPointVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.dto.PTZPointDto;
import com.chinatower.platform.model.dto.PercentPointDto;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.entity.ResultEntity;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.VideoCommonUtil;
import com.chinatower.video.modules.query.dao.VideoPtzQueryDAO;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.query.dto.AlarmAreaPoint;
import com.chinatower.video.modules.query.dto.QueryDevicePointLonLatDto;
import com.chinatower.video.modules.query.entity.*;
import com.chinatower.video.modules.query.vo.*;
import com.chinatower.video.modules.query.vo.AlgorithmAreaVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 视频查询服务类
 * Created by ldl on 2022/1/27.
 */
@Service
@Slf4j
public class VideoQueryService{
    @Autowired
    private VideoCommonService videoCommonService;
    @Autowired
    VideoQueryDAO videoQueryDAO;
    @Autowired
    VideoPtzQueryDAO videoPtzQueryDAO;

    @Value("${device.modelCode:dhua,haohai,hikvision,huawei,youhua}")
    private String deviceModelCodeStr;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private NetConnectUtils connectUtils;
    @Resource
    ICasCadeQueryService iCasCadeQueryService;
    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Resource
    private VideoPtzQueryService videoPtzQueryService;

    private final String ALGO_TYPE = "1";

    private final String ALARM_TYPE = "2";

    public static final String LINE_SPLIT = "-line-";
    public static final String DIRECTION_SPLIT = "-direction-";

    public static final String ALARM_PRE = "AREA_";

    public Result<QueryRoleReq> selectRoleById(QueryRoleReq queryRoleReq,LoginUser currentUser) {
        Result<QueryRoleReq> res;
        QueryRoleReq cloudMap = new QueryRoleReq();
        if (StringUtils.isEmpty(queryRoleReq.getRoleId())) {
            List<SysRole> roles = currentUser.getUser().getRoles();
            if (roles == null || roles.isEmpty()){
                return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.SELECT_ROLE_BY_ID_ERRO + currentUser);
            }
            // 检测是否具备云台控制权限1
            //todo: 批量获取云台控制权限
            List<QueryRoleReq> list = videoQueryDAO.getQueryRolesByRoleIds(roles);
            if(!CollectionUtils.isEmpty(list)){
                for(QueryRoleReq role: list) {
                    cloudMap.setCloudControl(role.getCloudControl());
                    if("0".equals(role.getCloudControl())){
                        break;
                    }
                }
            }

//            for(SysRole role: roles) {
//                queryRoleReq.setRoleId(String.valueOf(role.getRoleId()));
//                QueryRoleReq roleCloud = videoQueryDAO.getRoleById(queryRoleReq);
//                cloudMap.setCloudControl(roleCloud.getCloudControl());
//                if("0".equals(roleCloud.getCloudControl())){
//                    break;
//                }
//            }
            log.info(VideoConstants.SELECT_ROLE_BY_ID_OP + VideoConstants.SELECT_LOG + cloudMap);
            res = Result.data(cloudMap);
        }else{
            QueryRoleReq roleCloud = videoQueryDAO.getRoleById(queryRoleReq);
            log.info(VideoConstants.SELECT_ROLE_BY_ID_OP + VideoConstants.SELECT_LOG + roleCloud);
            res = Result.data(roleCloud);
        }
        return res;
    }

    /**
     * 获取字典（视频格式）
     * @param queryGetEnumInfoReq
     * @return
     */
    public Result getEnumInfo(QueryGetEnumInfoReq queryGetEnumInfoReq) {
        PageMethod.startPage(queryGetEnumInfoReq.getPageNum(), queryGetEnumInfoReq.getPageSize());
        List<QueryGetEnumInfoReq> list = videoQueryDAO.getEnumInfo(queryGetEnumInfoReq);
        log.info(VideoConstants.SELECT_ENUM_INFO_OP + VideoConstants.SELECT_LOG + list);
        PageInfo<QueryGetEnumInfoReq> pageInfo = new PageInfo<>(list);
        return Result.data(pageInfo);
    }

    @DataScope(deviceColumn="t1.device_code", tenantColumn="t1.tenant_id",alarmSourceVal = "4", industryCode="t1.industry_code",acceptColumn="d.accept_node")
    public Result queryRecordPlanForPage(VideoChannelVideoPlanVO channelVideoPlanVO) {
        Result res;
        List<VideoChannelVideoPlanVO> channelVideoPlanVOList;
        if("1".equals(channelVideoPlanVO.getQueryFlag())){
            PageHelper.startPage(channelVideoPlanVO.getPageNum(), channelVideoPlanVO.getPageSize());
            channelVideoPlanVOList = videoQueryDAO.queryVideoPlanInfo(channelVideoPlanVO);
            PageInfo<VideoChannelVideoPlanVO> pageInfo = new PageInfo<VideoChannelVideoPlanVO>(channelVideoPlanVOList);
//            VideoPageInfo<VideoChannelVideoPlanVO> pageInfo = new VideoPageInfo<>(channelVideoPlanVOList,channelVideoPlanVO.getPageNum(), channelVideoPlanVO.getPageSize());
            //查询通道录像计划日志
            res = Result.data(pageInfo);
        }else{
            channelVideoPlanVOList = videoQueryDAO.queryVideoPlanInfo(channelVideoPlanVO);
            if (!channelVideoPlanVOList.isEmpty()) {
                List<VideoTimeSectionVO> timeSectionList = videoQueryDAO.queryTimeSectionList(channelVideoPlanVOList);
                Map<String, List<VideoTimeSectionVO>> map = new HashMap<>();
                timeSectionList.forEach(item ->
                        map.computeIfAbsent(item.getRecordPlanId(), k -> new ArrayList<>()).add(item)
                );
                channelVideoPlanVOList.forEach(item -> {
                    List<VideoTimeSectionVO> videoTimeSectionVOS = map.get(item.getRecordPlanId());
                    item.setTimeSections(videoTimeSectionVOS);
                });
//                for (VideoChannelVideoPlanVO c:channelVideoPlanVOList) {
//                    VideoTimeSectionVO timeSectionVO = new VideoTimeSectionVO();
//                    timeSectionVO.setRecordPlanId(c.getRecordPlanId());
//                    timeSectionVO.setDeleteFlag("0");
//                    List<VideoTimeSectionVO> timeSections = videoQueryDAO.queryTimeSectionInfo(timeSectionVO);
//                    c.setTimeSections(timeSections);
//                }
            }
            res = Result.data(channelVideoPlanVOList);
        }
        return res;
    }


    public Result<List<QueryRecordPlanData>> queryRecordPlan(VideoBaseReq videoBaseReq,Map<String, String> headerMap) {
        Result<List<QueryRecordPlanData>> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_RECORD_PLAN_ERRO);
        try{
            NetConnectUtils.RequestParamter<HashMap> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数；这里为空HashMap
            paramter.setBody(new HashMap<>());
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_RECORD_PLAN);
            // 接口路径参数
            path.setDeviceCode(videoBaseReq.getDeviceCode());
            path.setChannelCode(videoBaseReq.getChannelCode());
            paramter.setPath(path);

            //属地化数据统一改造 add by wangyt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseReq.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);

            QueryRecordPlanData.QueryRecordPlanDataA cr = connectUtils.post(paramter, QueryRecordPlanData.QueryRecordPlanDataA.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + cr);

            if (VideoCommonUtil.crIsSuccess(cr)) {
                res = Result.data(cr.getData());
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.QUERY_RECORD_PLAN_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.QUERY_RECORD_PLAN_ERRO + VideoCommonUtil.getLogData(videoBaseReq),e);
        }
        return res;
    }

    @DataScope(deviceColumn="t.device_code", acceptColumn = "vod.accept_node", tenantColumn="t.tenant_id",alarmSourceVal = "4", industryCode="t.industry_code")
    public Result<List<QueryPatrolData>> queryPatrol(VideoQueryPatrolData videoQueryPatrol) {
        Result<List<QueryPatrolData>> res = Result.fail(VideoConstants.ERROR_PRE + "查询失败！");
        try{

            VideoPatrolVO queryPatrolVO = new VideoPatrolVO();
            BeanUtils.copyProperties(videoQueryPatrol,queryPatrolVO);
            queryPatrolVO.setCustomerCode(videoQueryPatrol.getTenantId());
            queryPatrolVO.setDeleteFlag(Integer.valueOf(VideoConstants.VALID_STATE));

            List<QueryPatrolData> videoPatrolVOList = videoQueryDAO.getPatrolList(queryPatrolVO);
            if( ! CollectionUtils.isEmpty(videoPatrolVOList) )
            {
                // 查询当前页巡航计划关联的所有预置位列表
                List<QueryPatrolListData> presetList = videoQueryDAO.getPatrolAllPresetList(videoPatrolVOList);
                if( ! CollectionUtils.isEmpty(presetList) )
                {
                    // 按照巡航计划id分组
                    Map<Long, List<QueryPatrolListData>> presetMap = presetList.stream().collect(Collectors.groupingBy(QueryPatrolListData::getPatrolId));
                    for(QueryPatrolData qpVo:videoPatrolVOList){
                        qpVo.setPresets(presetMap.get(qpVo.getPatrolId()));
                        if (!StringUtils.isEmpty(qpVo.getStartTimePeriod())){
                            String[] split = qpVo.getStartTimePeriod().split(VideoConstants.COMMA);
                            //是否兼容app旧版本 0：否，1：是
                            if (VideoConstants.ONE_STR.equals(videoQueryPatrol.getIfAppOld())) {
                                //兼容1.7.20之前的版本需要将分钟格式的时段，处理为小时时段
                                qpVo.setStartTimePeriodArr(this.getNewHourList(split));
                                qpVo.setStartTimePeriodArrNew(split);
                            }else{
                                qpVo.setStartTimePeriodArr(split);
                            }

                        }
                    }
                }
            }
            res = Result.data(videoPatrolVOList);

        } catch (Exception e) {
            log.error(VideoConstants.QUERY_PATROL_ERRO + VideoCommonUtil.getLogData(videoQueryPatrol),e);
        }
        return res;
    }

    /**
     * 功能描述: 分页查询巡航计划
     *
     * @param: videoQueryPatrol
     * @return: com.chinatower.common.entity.Result
     * @auther: wanglei
     * @date: 2023/6/12
     * @version: 1.5
     */
    @DataScope(deviceColumn="t.device_code", acceptColumn = "vod.accept_node", tenantColumn="t.tenant_id",alarmSourceVal = "4", industryCode="t.industry_code")
    public Result<PageInfo<QueryPatrolData>> queryPatrolPage(VideoQueryPatrolData videoQueryPatrol) {
        Result<PageInfo<QueryPatrolData>> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_FAIL);
        try{
            VideoPatrolVO queryPatrolVO = new VideoPatrolVO();
            BeanUtils.copyProperties(videoQueryPatrol,queryPatrolVO);
            queryPatrolVO.setCustomerCode(videoQueryPatrol.getTenantId());
            queryPatrolVO.setDeleteFlag(Integer.valueOf(VideoConstants.VALID_STATE));
            // 分页查询巡航计划列表
            PageHelper.startPage(videoQueryPatrol.getPageNum(), videoQueryPatrol.getPageSize());
            List<QueryPatrolData> videoPatrolVOList = videoQueryDAO.getPatrolList(queryPatrolVO);
            if( ! CollectionUtils.isEmpty(videoPatrolVOList) )
            {
                // 查询当前页巡航计划关联的所有预置位列表
                List<QueryPatrolListData> presetList = videoQueryDAO.getPatrolAllPresetList(videoPatrolVOList);
                if( ! CollectionUtils.isEmpty(presetList) )
                {
                    // 按照巡航计划id分组
                    Map<Long, List<QueryPatrolListData>> presetMap = presetList.stream().collect(Collectors.groupingBy(QueryPatrolListData::getPatrolId));
                    for(QueryPatrolData qpVo:videoPatrolVOList){
                        qpVo.setPresets(presetMap.get(qpVo.getPatrolId()));
                        if (!StringUtils.isEmpty(qpVo.getStartTimePeriod())){
                            String[] split = qpVo.getStartTimePeriod().split(VideoConstants.COMMA);
                            //兼容1.7.20之前的版本需要将分钟格式的时段，处理为小时时段
                            qpVo.setStartTimePeriodArr(this.getNewHourList(split));
                            qpVo.setStartTimePeriodArrNew(split);
                        }
                    }
                }
            }
            PageInfo<QueryPatrolData> pageInfo = new PageInfo<QueryPatrolData>(videoPatrolVOList);
            res = Result.data(pageInfo);
        } catch (Exception e) {
            log.error(VideoConstants.QUERY_PATROL_ERRO + VideoCommonUtil.getLogData(videoQueryPatrol),e);
        }
        return res;
    }

    /**
     * 分钟转为小时
     */
    public String[] getNewHourList(String[] time){
        Set<String> newTime = new HashSet<>();
        for (String s : time) {
            newTime.add(s.split(":")[0]);
        }
        List<String> list = new ArrayList<>(newTime);
        list.sort(Comparator.comparing(String::toString));
        return list.toArray(new String[0]);
    }

    public Result<List<QueryPatrolData>> queryPatrolNorth(VideoQueryPatrolData videoQueryPatrol) {
        Result<List<QueryPatrolData>> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_PATROL_ERRO);
        try{
            NetConnectUtils.RequestParamter<VideoQueryPatrolData> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(videoQueryPatrol);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PATROL);
            // 接口路径参数
            path.setDeviceCode(videoQueryPatrol.getDeviceCode());
            path.setChannelCode(videoQueryPatrol.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by wangyt 20240129
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoQueryPatrol.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.REQUEST_LOG + paramter);
//            QueryPatrolData.QueryPatrolDataEntity cr = connectUtils.post(paramter, QueryPatrolData.QueryPatrolDataEntity.class,
//                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(videoQueryPatrol.getTenantId()));
            QueryPatrolData.QueryPatrolDataEntity cr = connectUtils.post(paramter, QueryPatrolData.QueryPatrolDataEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.RESPONSE_LOG + cr);

            if (VideoCommonUtil.crIsSuccess(cr)) {
                List<QueryPatrolData> resultList = cr.getData();
                res = Result.data(resultList);
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.QUERY_PATROL_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.QUERY_PATROL_ERRO + VideoCommonUtil.getLogData(videoQueryPatrol), e);
        }
        return res;
    }

    public Result<List<QueryPatrolData>> queryPatrolNorth(VideoQueryPatrolData videoQueryPatrol, DeviceInfoVO deviceInfoVO) {
        Result<List<QueryPatrolData>> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_PATROL_ERRO);
        try{
            NetConnectUtils.RequestParamter<VideoQueryPatrolData> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(videoQueryPatrol);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PATROL);
            // 接口路径参数
            path.setDeviceCode(videoQueryPatrol.getDeviceCode());
            path.setChannelCode(videoQueryPatrol.getChannelCode());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            QueryPatrolData.QueryPatrolDataEntity cr = connectUtils.post(paramter, QueryPatrolData.QueryPatrolDataEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(deviceInfoVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(deviceInfoVO.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.RESPONSE_LOG + cr);

            if (VideoCommonUtil.crIsSuccess(cr)) {
                List<QueryPatrolData> resultList = cr.getData();
                res = Result.data(resultList);
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.QUERY_PATROL_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.QUERY_PATROL_ERRO + VideoCommonUtil.getLogData(videoQueryPatrol), e);
        }
        return res;
    }

    public Result<QueryGuardListData> queryGuardList(VideoBaseReq videoBaseReq,Map<String, String> headerMap) {
        Result<QueryGuardListData> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_GUARD_LIST_ERRO);
        // 请求参数
//        Map<String,String> paramMap = new HashMap<>();
        try{
//            NetConnectUtils.RequestParamter<Map<String,String>> paramter = new NetConnectUtils.RequestParamter<>();
//            // 请求参数
//            paramter.setBody(paramMap);
//            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
//            // 获取接口地址
//            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_GUARD_LIST);
//            // 接口路径参数
//            path.setDeviceCode(videoBaseReq.getDeviceCode());
//            path.setChannelCode(videoBaseReq.getChannelCode());
//            paramter.setPath(path);
//            //属地化数据统一改造 add by liuxt 20240116
//            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseReq.getDeviceCode());
//            // 调用统一网关接口
//            log.info(VideoConstants.QUERY_GUARD_LIST_OP + VideoConstants.REQUEST_LOG + paramter);
//            QueryGuardListData.QueryGuardListDataEntity cr = connectUtils.post(paramter, QueryGuardListData.QueryGuardListDataEntity.class,
//                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
//                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
//                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
//                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
//                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
//            log.info(VideoConstants.QUERY_GUARD_LIST_OP + VideoConstants.RESPONSE_LOG + cr);
//
//            if (VideoCommonUtil.crIsSuccess(cr)) {
//                if(cr.getData() != null && cr.getData().getGuardId()> 0) {
//                    // 无效该设备的其他看守位
//                    videoQueryDAO.invalidGuardById(String.valueOf(cr.getData().getGuardId()), videoBaseReq.getDeviceCode());
//                }else{
//                    // 无看守位则置无效该设备的所有看守位
//                    videoQueryDAO.invalidGuard(videoBaseReq.getDeviceCode());
//                }
//            }
            QueryGuardListData guardData = videoQueryDAO.getGuard(headerMap.get(VideoConstants.CUSTOMERCODE), videoBaseReq.getDeviceCode(), videoBaseReq.getChannelCode());
            res = Result.data(guardData);
        } catch (Exception e) {
            log.error(VideoConstants.QUERY_GUARD_LIST_ERRO + VideoCommonUtil.getLogData(videoBaseReq),e);
        }
        return res;
    }

    public Result<List<QueryPresetListData>> queryPresetList(VideoBaseReq videoBaseReq,Map<String, String> headerMap) {
        Result<List<QueryPresetListData>> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_PRESET_LIST_ERRO);
        // 请求参数
        Map<String,String> paramMap = new HashMap<>();
        try{
            NetConnectUtils.RequestParamter<Map<String,String>> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(paramMap);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PRESET_LIST);
            // 接口路径参数
            path.setDeviceCode(videoBaseReq.getDeviceCode());
            path.setChannelCode(videoBaseReq.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseReq.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.QUERYPRESETLIST + VideoConstants.REQUEST_LOG + paramter);
            QueryPresetListData.QueryPresetListDataEntity cr = connectUtils.post(paramter, QueryPresetListData.QueryPresetListDataEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.QUERYPRESETLIST + VideoConstants.RESPONSE_LOG + cr);
            if (VideoCommonUtil.crIsSuccess(cr)) {
                VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
                videoPtzQueryReq.setDeviceCode(videoBaseReq.getDeviceCode());
                DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
                List<QueryPresetListData> presetList = videoQueryDAO.queryPreset(headerMap.get(VideoConstants.CUSTOMERCODE),videoBaseReq.getIndustryCode(), deviceModel.getDeviceCode());
                List<QueryPresetListData> finalResult = new ArrayList<>();
                //整改for循环 modify by liuxt 20240806
                Set<String> fileUrls = new HashSet<>();
                for(QueryPresetListData x:cr.getData()){
                    for (QueryPresetListData l:presetList){
                        if (x.getPresetId() == l.getPresetId()){
                            //add by liuxt video-file改造
                            fileUrls.add(l.getImgUrl());
                        }
                    }
                }
                //整改for循环 modify by liuxt 20240806
                if(!fileUrls.isEmpty()){
                    // 处理Obs文件URL加密返回
                    Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
                    for(QueryPresetListData x:cr.getData()){
                        for (QueryPresetListData l:presetList){
                            if (x.getPresetId() == l.getPresetId()){
                                //add by liuxt video-file改造
                                x.setImgUrl(result.get(l.getImgUrl()));
                                x.setPresetName(l.getPresetName());
                                x.setCreateTime(l.getCreateTime());
                                x.setPan(ptzEscape(x.getPreset().getPan(), deviceModel, VideoConstants.PTZ_P));
                                x.setTilt(ptzEscape(x.getPreset().getTilt(), deviceModel, VideoConstants.PTZ_T));
                                x.setZoom(ptzEscape(x.getPreset().getZoom(), deviceModel, VideoConstants.PTZ_Z));
                                finalResult.add(x);
                            }
                        }
                    }
                }
                res = Result.data(finalResult);
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.QUERY_PRESET_LIST_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.QUERY_PRESET_LIST_ERRO + VideoCommonUtil.getLogData(videoBaseReq),e);
        }
        return res;
    }

    /**
     * ptz转义有异常处理
     * @param value 原始值
     * @param deviceModel
     * @param type ptz的类型
     * @return 转义后的值
     */
    public float ptzEscape(float value, DeviceModelVO deviceModel, String type) {
        try
        {
            return Float.valueOf(LongLatAngleUtils.paramTrans(String.valueOf(value), deviceModel, type));
        }
        catch (Exception e) {
            log.error(VideoConstants.PRESET_LIST_PTZ_PARSE_FAIL + type, e);
            return value;
        }
    }

    public Result<PresetData> queryPtzPosition(VideoBaseReq videoBaseReq,Map<String, String> headerMap) {
        Result<PresetData> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_PTZ_POSITION_ERRO);
        // 请求参数
        Map<String,String> paramMap = new HashMap<>();
        try{
            NetConnectUtils.RequestParamter<Map<String,String>> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(paramMap);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PTZ_POSITION);
            // 接口路径参数
            path.setDeviceCode(videoBaseReq.getDeviceCode());
            path.setChannelCode(videoBaseReq.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by liuxt 20240116
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(videoBaseReq.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.QUERY_PTZ_POSITION_OP + VideoConstants.REQUEST_LOG + paramter);
            PresetData.PresetDataEntity cr = connectUtils.post(paramter, PresetData.PresetDataEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.QUERY_PTZ_POSITION_OP + VideoConstants.RESPONSE_LOG + cr);

            if (VideoCommonUtil.crIsSuccess(cr)) {
                PresetData preset = cr.getData();
                if(preset != null){
                    VideoPtzQueryReq videoPtzQueryReq = new VideoPtzQueryReq();
                    videoPtzQueryReq.setDeviceCode(videoBaseReq.getDeviceCode());
                    DeviceModelVO deviceModel = videoPtzQueryDAO.getDeviceModel(videoPtzQueryReq);
                    preset.setPan(Float.valueOf(LongLatAngleUtils.paramTrans(String.valueOf(preset.getPan()), deviceModel, "P")));
                    preset.setTilt(Float.valueOf(LongLatAngleUtils.paramTrans(String.valueOf(preset.getTilt()), deviceModel, "T")));
                    preset.setZoom(Float.valueOf(LongLatAngleUtils.paramTrans(String.valueOf(preset.getZoom()), deviceModel, "Z")));
                }
                res = Result.data(cr.getData());
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.QUERY_PTZ_POSITION_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.QUERY_PTZ_POSITION_ERRO + VideoCommonUtil.getLogData(videoBaseReq),e);
        }
        return res;
    }

    public void setUserHabit(HabitVO userHabit) {
        //判定之前是否有值
        Integer habit = videoQueryDAO.checkUserHabit(userHabit);
        if (habit > 0) {
            videoQueryDAO.updateUserHabit(userHabit);
        } else {
            videoQueryDAO.setUserHabit(userHabit);
        }
    }

    public String getUserHabit(HabitVO userHabit) {
        String habitValue = videoQueryDAO.getUserHabit(userHabit);
        return habitValue == null ? "" : habitValue;
    }

    public List<HabitVO> getUserHabitBatch(List<String> list) {
        return videoQueryDAO.getUserHabitBatch(list);
    }

    public Result<List<PercentPointDto>> queryPercentPoints(QueryPresentPointVo queryPresentPointVo, Map<String, String> headerMap) {

        //查询当前设备的所有顶点集合
        PointsAndDeviceModelDto pointsAndDeviceModel = videoQueryDAO.getPointsByDeviceAndChannel(queryPresentPointVo);
        if (null != pointsAndDeviceModel) {
            String msg = "";
            boolean processType = true;

            /*
            如果摄像机不支持获取PTZ，则通过百分比坐标显示，提示：“该摄像机不支持获取PTZ，当前显示的电子围栏可能与实际标绘的物理位置不符。”

            如果摄像机没有配置厂家和型号，在算法配置中，只能展示百分比坐标系的识别区域。给出提示：“该摄像机没有配置厂家和型号，当前显示的标绘区域可能与实际标绘的物理位置不符，”

            如果摄像机厂家不是限定的五个厂家，则通过百分比坐标显示，提示：“该摄像机品牌小众，当前显示的电子围栏可能与实际标绘的物理位置不符。”
             */
            PresetData centerPtz = new PresetData();
            if (StringUtils.isEmpty(pointsAndDeviceModel.getModelCode()) || StringUtils.isEmpty(pointsAndDeviceModel.getDeviceModelCode())) {
                msg = "该摄像机没有配置厂家和型号，当前显示的标绘区域可能与实际标绘的物理位置不符。";
                processType = false;
            } else if (!deviceModelCodeStr.contains(pointsAndDeviceModel.getModelCode())) {
                msg = "该摄像机品牌小众，当前显示的电子围栏可能与实际标绘的物理位置不符。";
                processType = false;
            } else {
                //获取p't'z'
                VideoBaseReq videoBaseReq = new VideoBaseReq();
                videoBaseReq.setDeviceCode(pointsAndDeviceModel.getDeviceCode());
                videoBaseReq.setChannelCode(pointsAndDeviceModel.getChannelCode());
                Result<PresetData> presetDataResult = queryPtzPosition(videoBaseReq, headerMap);
                if (null == presetDataResult.getData()) {
                    msg = "该摄像机获取PTZ失败，当前显示的电子围栏可能与实际标绘的物理位置不符。";
                    processType = false;
                } else {
                    centerPtz = presetDataResult.getData();
                }
            }

            //解析point
            String pointsStr = pointsAndDeviceModel.getPoints();
            log.info(pointsStr);
            JSONArray points = JSONArray.parseArray(pointsStr);
            JSONObject point =(JSONObject) points.get(0);

            List<PercentPointDto> percentPoints = new ArrayList<>();
            if (processType) {
                //获取PTZ->获取PT->计算百分比
                PTZPointDto centerPoint = new PTZPointDto();
                BeanUtils.copyProperties(centerPtz, centerPoint);
                log.info("中心点ptz:{}",centerPoint);
                DeviceModelVO deviceModelVO = new DeviceModelVO();
                deviceModelVO.setPicSensorLength(pointsAndDeviceModel.getPicSensorLength());
                deviceModelVO.setPicSensorWidth(pointsAndDeviceModel.getPicSensorWidth());
                deviceModelVO.setMinFocus(pointsAndDeviceModel.getMinFocus());
                //PT点
                String ptPointList = point.getString("ptPointList");
                if (!StringUtils.isEmpty(ptPointList)) {
                    List<PTZPointDto> ptzPoints = JSONArray.parseArray(ptPointList, PTZPointDto.class);
                    for (PTZPointDto ptzPoint : ptzPoints) {
                        log.info("pt点: {}",ptzPoint);
                        PercentPointDto percentPointDto = LongLatAngleUtils.ptTransPercent(deviceModelVO, ptzPoint, centerPoint);
                        percentPoints.add(percentPointDto);
                    }
                }
            } else {
                //直接返回百分比
                String moniPointList = point.getString("moniPointList");
                if (!StringUtils.isEmpty(moniPointList)) {
                    percentPoints = JSONArray.parseArray(moniPointList, PercentPointDto.class);
                } else {
                    msg = "该摄像机未保存百分比点，请确认数据准确性。";
                }
            }

            return Result.data(percentPoints, msg);

        }
        return Result.fail("未找到该设备");
    }

    public Result<PresetData> updateHistoryPoints(Map<String, String> headerMap) {
        //List<PointsAndDeviceModelDto> historyPoints = videoQueryDAO.getHistoryPoints();
        //
        //List<PointsAndDeviceModelDto> updates = new ArrayList<>();
        //historyPoints.forEach(item -> {
        //    if (!StringUtils.isEmpty(item.getModelCode()) && !StringUtils.isEmpty(item.getDeviceModelCode())) {
        //        //获取p't'z'
        //        VideoBaseReq videoBaseReq = new VideoBaseReq();
        //        videoBaseReq.setDeviceCode(item.getDeviceCode());
        //        videoBaseReq.setChannelCode(item.getChannelCode());
        //        Result<PresetData> presetDataResult = queryPtzPosition(videoBaseReq, headerMap);
        //        if (null != presetDataResult.getData()) {
        //            PresetData centerPtz = presetDataResult.getData();
        //
        //            PTZPointDto centerPoint = new PTZPointDto();
        //            BeanUtils.copyProperties(centerPtz, centerPoint);
        //            DeviceModelVO deviceModelVO = new DeviceModelVO();
        //            deviceModelVO.setPicSensorLength(item.getPicSensorLength());
        //            deviceModelVO.setPicSensorWidth(item.getPicSensorWidth());
        //            deviceModelVO.setMinFocus(item.getMinFocus());
        //            //解析point
        //            String pointsStr = item.getPoints();
        //            JSONArray points = JSONArray.parseArray(pointsStr);
        //            if (points.size() > 0) {
        //                JSONObject point =(JSONObject) points.get(0);
        //                String monitorAreaId = point.getString("monitorAreaId");
        //                //百分比点
        //                String moniPointList = point.getString("moniPointList");
        //                List<PTZPointDto> ptzPointList = new ArrayList<>();
        //                PointsDto pointsDto = new PointsDto();
        //                pointsDto.setMonitorAreaId(monitorAreaId);
        //
        //                if (!StringUtils.isEmpty(moniPointList)) {
        //                    List<PercentPointDto> percentPoints = JSONArray.parseArray(moniPointList, PercentPointDto.class);
        //                    percentPoints.forEach(percentPoint -> {
        //                        log.info("百分比点: {}",percentPoint);
        //                        PTZPointDto ptzPointDto = LongLatAngleUtils.percentTransPt(deviceModelVO, percentPoint, centerPoint);
        //                        log.info("转换后的PT坐标: {}", ptzPointDto);
        //                        ptzPointList.add(ptzPointDto);
        //                    });
        //                    pointsDto.setMoniPointList(percentPoints);
        //                    pointsDto.setPtPointList(ptzPointList);
        //                }
        //                List<PointsDto> list = new ArrayList<>();
        //                list.add(pointsDto);
        //                String updatePoints = JSON.toJSONString(list);
        //                PointsAndDeviceModelDto updateAlgorithm = new PointsAndDeviceModelDto();
        //                updateAlgorithm.setConfigId(item.getConfigId());
        //                updateAlgorithm.setPoints(updatePoints);
        //                updates.add(updateAlgorithm);
        //            }
        //        }
        //    }
        //});
        //if (!updates.isEmpty()) {
        //    //批量更新
        //    videoQueryDAO.updateAlgorithm(updates);
        //}
        return Result.success("更新成功");
    }

    public ResultEntity getVideoRealtimeUrls(List list) {
        List resultData = new ArrayList();
        try{
            String platformCode = "";
            SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
            if(!ObjectUtils.isEmpty(config)) {
                platformCode = config.getConfigValue();
            }
            final String fromCode = platformCode;

            //根据to-code对入参分组 分组后格式：[{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
            GroupByNodeVO groupByNodeVO = new GroupByNodeVO();
            groupByNodeVO.setParams(list);
            groupByNodeVO.setItemExpression("#item.get('deviceCode')");
            groupByNodeVO.setBatchQuery(iCasCadeQueryService::getCasCadeMapByDeviceCodeBatch);
            List<Map> threadList = iCasCadeQueryService.groupByNode(groupByNodeVO);

            //多线程处理
            List<Object> data = ThreadParallelUtil.run(threadList, it -> {
                log.info("getVideoRealtimeUrls线程处理数据量:{}", it.size());
                return sendGetVideoRealtimeUrls(it, fromCode);
            }, ThreadParallelUtil.ConfigFactory.GET_VIDEO_REALTIME_URLS);

            //融合结果
            if(!CollectionUtils.isEmpty(data)){
                for (Object datum : data) {
                    resultData.addAll((List)datum);
                }
            }
            log.info("getVideoRealtimeUrls.resultData="+JSONObject.toJSON(resultData));
        } catch (Exception e) {
            log.error(VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE , e);
            return ResultEntity.fail(VideoConstants.FAIL_CODE,VideoConstants.ERROR_PRE + VideoConstants.GETVIDEOREALTIMEURLS_ERRO);
        }
        return ResultEntity.success(resultData);
    }

    private Object sendGetVideoRealtimeUrls(Map map,String fromCode){
        try {
            NetConnectUtils.RequestParamter<List> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody((List)map.get(VideoConstants.VALUE));
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_VIDEO_REALTIME_URLS);
            paramter.setPath(path);

            String toCode = (String)map.get(VideoConstants.KEY);
            // 调用统一网关接口
            log.info(toCode + VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE + VideoConstants.REQUEST_LOG + JSONObject.toJSON(paramter));
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(fromCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(toCode + VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE + VideoConstants.RESPONSE_LOG + JSONObject.toJSON(cr));
            return cr.getData();
        } catch (Exception e) {
            log.error(VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE , e);
            throw new RuntimeException(e);
        }
    }

    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(Set<PlayerGetVideoRealtimeUrlData> reqList) {
        log.info("getVideoRealtimeUrlsForComponent入参" + JSON.toJSONString(reqList));
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String userId = currentUser.getUser().getUserId();
        List<String> habitKeyList = new ArrayList<>();
        for (PlayerGetVideoRealtimeUrlData data : reqList) {
            habitKeyList.add(this.getHabitKey(userId,data.getDeviceCode(),data.getChannelCode()));
        }

        if(!CollectionUtils.isEmpty(habitKeyList)){
            List<HabitVO> userHabitList = videoQueryDAO.getUserHabitList(habitKeyList);
            Map<String,String> habitVOMap = new HashMap<>();
            for (HabitVO habitVO : userHabitList) {
                habitVOMap.put(habitVO.getHabitKey(),habitVO.getHabitValue());
            }

            int netType = this.getNetType();
            int streamType = Constants.ONE_NUM;
            int protocolType = this.getProtocolType();

            List<Map> list = new ArrayList<>();
            for (PlayerGetVideoRealtimeUrlData deviceInfo : reqList) {
                String value = habitVOMap.get(this.getHabitKey(userId, deviceInfo.getDeviceCode(), deviceInfo.getChannelCode()));
                if(!StringUtils.isEmpty(value)){
                    String[] split = value.split(Constants.COMMA);
                    try{
                        streamType = Integer.parseInt(split[Constants.ZERO_NUM]);
                        protocolType = Integer.parseInt(split[Constants.ONE_NUM]);
                    }catch (Exception e){
                        log.error("视频记忆功能参数错误：" + value);
                    }
                }
                //构造返回参数用到
                deviceInfo.setStreamType(streamType);
                deviceInfo.setProtocolType(protocolType);

                Map map = new HashMap();
                map.put(Constants.DEVICECODE, deviceInfo.getDeviceCode());
                map.put(DeviceConstants.CHANNELCODE, deviceInfo.getChannelCode());
                map.put(DeviceConstants.STREAM_TYPE, streamType);
                map.put(Constants.STREAM_UUID, IdUtils.fastSimpleUUID());
                map.put(Constants.PROTOCOL_TYPE, protocolType);
                map.put(Constants.NET_TYPE,netType);
                list.add(map);
            }

            log.info("getVideoRealtimeUrls入参" + JSON.toJSONString(list));
            ResultEntity res = this.getVideoRealtimeUrls(list);
            log.info("getVideoRealtimeUrls出参" + JSON.toJSONString(res));
            if(Constants.SUCCESS_CODE.equals(res.getReturnCode())){
                Object data = res.getData();
                List jsonArray = (List) data;
                for (PlayerGetVideoRealtimeUrlData d : reqList) {
                    d.setStreamSwitchFlag(Constants.ONE_NUM);
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = (JSONObject) o;
                        if(d.getDeviceCode().equals(jsonObject.get(Constants.DEVICECODE))
                                && d.getChannelCode().equals(jsonObject.get(VideoConstants.CHANNELCODE)) ){
                            d.setStreamUrl((String)jsonObject.get(Constants.STREAM_URL));
                            d.setStreamUuid((String)jsonObject.get(Constants.STREAM_UUID));
                            d.setReturnMsg((String)jsonObject.get(Constants.RETURN_MSG));
                        }
                    }
                }
                return Result.data(reqList);
            }else{
                return Result.fail(res.getReturnMsg() + res.getErrorMsg());
            }
        }
        return Result.data(null);
    }

    private String getHabitKey(String userId,String deviceCode,String channelCode ){
        return userId +
                VideoConstants.UNDERLINE_STR + "real" +
                VideoConstants.UNDERLINE_STR + deviceCode +
                VideoConstants.UNDERLINE_STR + channelCode;
    }

    private int getNetType(){
        int netType = Constants.TWO_NUM;
        // 网域类型字段
        AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.NETTYPE_PARAM);
        if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if(!ObjectUtils.isEmpty(sysConfig)){
                netType = Integer.parseInt(sysConfig.getConfigValue());
            }
        }
        return netType;
    }

    private int getProtocolType(){
        int protocolType = Constants.ZERO_NUM;
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType("99");
        if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
            List<SysDictDataResponse> data = sysResult.getData();
            if(!CollectionUtils.isEmpty(data)){
                for (int i = 0; i < data.size(); i++) {
                    int max = Integer.parseInt(data.get(i).getDictValue());
                    if (max > protocolType) {
                        protocolType = max;
                    }
                }
            }else {
                protocolType = Constants.FIVE_NUM;
            }
        }else{
            protocolType = Constants.FIVE_NUM;
        }
        return protocolType;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AlgoPresentVo> getAlgoPresent(List<AlgoPresentVo> list) {

        //最后返回的数据
        List<AlgoPresentVo> result = new ArrayList<>();

        //获取所有的点位
        List<String> configIds = new ArrayList<>();
        /*
         * 设备，通道，p,t,z 对应的点位集合
         */
        Map<String, QueryDevicePointLonLatDto> map = new HashMap<>();

        /*
         * 设备通道 对应的 点位 （经纬度转百分比）
         */
        Map<String, VideoPtzReq> presentByLonLatDtoMap = new HashMap<>();


        //需要更新的设备屏蔽区域
        List<AlgoPresentVo> alarmAreaPointConvert = new ArrayList<>();
        //需要更新的算法相关区域节点
        List<AlgorithmAreaVO> algorithmAreaConvert = new ArrayList<>();




        for (AlgoPresentVo algoPresentVo : list) {
            if (ALGO_TYPE.equals(algoPresentVo.getType())) {
                configIds.addAll(algoPresentVo.getIds());
            } else {
                //result
                AlgoPresentVo algoPresent = new AlgoPresentVo();
                algoPresent.setType(ALARM_TYPE);
                algoPresent.setAreaId(algoPresentVo.getAreaId());
                //告警屏蔽区域
                if (!StringUtils.isEmpty(algoPresentVo.getPositionInformation())) {

                    //不需要转经纬度的点位
                    String positionInformation = algoPresentVo.getPositionInformation();
                    List<AlarmAreaPoint> pointsAlarmLonLat = JSON.parseArray(positionInformation, AlarmAreaPoint.class);

                    //result
                    algoPresent.setPositionInformationList(JSONArray.parseArray(positionInformation));

                    String key = algoPresentVo.getDeviceCode() + algoPresentVo.getChannelCode();
                    List<LongitudeLatitudeVO> pointsLonLat = new ArrayList<>();
                    for (int i = 0; i < pointsAlarmLonLat.size(); i++) {
                        LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                        longitudeLatitudeVO.setId(ALARM_PRE + algoPresentVo.getAreaId() + i);
                        longitudeLatitudeVO.setPointLon(Double.parseDouble(pointsAlarmLonLat.get(i).getLon()));
                        longitudeLatitudeVO.setPointLat(Double.parseDouble(pointsAlarmLonLat.get(i).getLat()));
                        pointsLonLat.add(longitudeLatitudeVO);
                    }
                    setQueryPercentMap(presentByLonLatDtoMap, pointsLonLat, key, algoPresentVo.getDeviceCode(), algoPresentVo.getChannelCode());
                    log.info("无需转换经纬度的点位:{}", JSON.toJSONString(algoPresent));
                    result.add(algoPresent);
                } else {
                    //需要转经纬度的点位
                    String pointsAlarmArea = algoPresentVo.getPoints();
                    List<AlarmAreaPoint> pointsAlarm = JSON.parseArray(pointsAlarmArea, AlarmAreaPoint.class);

                    String key = algoPresentVo.getDeviceCode()
                            + algoPresentVo.getChannelCode()
                            + algoPresentVo.getPointsP()
                            + algoPresentVo.getPointsT()
                            + algoPresentVo.getPointsZ();
                    List<DevicePointVo> devicePoints = new ArrayList<>();
                    for (int i = 0; i < pointsAlarm.size(); i++) {
                        DevicePointVo devicePointVo = new DevicePointVo();
                        devicePointVo.setPercentX(pointsAlarm.get(i).getPercentX());
                        devicePointVo.setPercentY(pointsAlarm.get(i).getPercentY());
                        devicePointVo.setFlag(ALARM_PRE + algoPresentVo.getAreaId() + i);
                        devicePoints.add(devicePointVo);
                    }
                    alarmAreaPointConvert.add(algoPresentVo);
                    if (map.containsKey(key)) {
                        QueryDevicePointLonLatDto queryDevicePointLonLatDto = map.get(key);
                        List<DevicePointVo> points1 = queryDevicePointLonLatDto.getPoints();
                        points1.addAll(devicePoints);
                    } else {
                        QueryDevicePointLonLatDto queryDevicePointLonLatDto = new QueryDevicePointLonLatDto();
                        queryDevicePointLonLatDto.setDeviceCode(algoPresentVo.getDeviceCode());
                        queryDevicePointLonLatDto.setChannelCode(algoPresentVo.getChannelCode());
                        queryDevicePointLonLatDto.setPan(algoPresentVo.getPointsP());
                        queryDevicePointLonLatDto.setTilt(algoPresentVo.getPointsT());
                        queryDevicePointLonLatDto.setZoom(algoPresentVo.getPointsZ());
                        queryDevicePointLonLatDto.setPoints(devicePoints);
                        map.put(key, queryDevicePointLonLatDto);
                    }
                }
            }
        }
        if (!configIds.isEmpty()) {
            //查询算法相关信息
            List<AlgorithmAreaVO> algorithmAreaVO = videoQueryDAO.findAlgoByIds(configIds);

            for (AlgorithmAreaVO areaVO : algorithmAreaVO) {

                //result
                AlgoPresentVo algoPresent = new AlgoPresentVo();
                algoPresent.setConfigId(areaVO.getConfigId());
                algoPresent.setType(ALGO_TYPE);


                AlgorithmAreaVO algo = new AlgorithmAreaVO();
                algo.setConfigId(areaVO.getConfigId());
                algo.setDeviceCode(areaVO.getDeviceCode());
                algo.setChannelCode(areaVO.getChannelCode());

                if (StringUtils.isEmpty(areaVO.getPresetId())) {
                    continue;
                }

                //查询预置位
                PresetData presetData = videoQueryDAO.findPreset(areaVO.getPresetId(), areaVO.getDeviceCode(), areaVO.getChannelCode());
                log.info("预置位:{}", JSON.toJSONString(presetData));

                //---------------无经纬度，需要转换经纬度的点位---------------

                //----------------------算法识别区域，识别等------------------------
                if (StringUtils.isEmpty(areaVO.getPointsLonLat())) {
                    String algoPoints = areaVO.getPoints();
                    List<com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO> algorithmMonitorAreaVO = JSON.parseArray(algoPoints, com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO.class);
                    //添加到更新列表
                    algo.setMonitorArea(algorithmMonitorAreaVO);

                    if (null == presetData) {
                        continue;
                    }

                    String key = areaVO.getDeviceCode()
                            + areaVO.getChannelCode()
                            + presetData.getPan() + presetData.getTilt() + presetData.getZoom();
                    List<DevicePointVo> devicePoints = new ArrayList<>();
                    for (com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO monitorAreaVO : algorithmMonitorAreaVO) {
                        for (com.chinatower.platform.entity.device.AlgorithmMonitorPointVO algorithmMonitorPointVO : monitorAreaVO.getMoniPointList()) {
                            DevicePointVo devicePointVo = new DevicePointVo();
                            devicePointVo.setPercentX(String.valueOf(algorithmMonitorPointVO.getPercentX()));
                            devicePointVo.setPercentY(String.valueOf(algorithmMonitorPointVO.getPercentY()));
                            devicePointVo.setFlag(monitorAreaVO.getMonitorAreaId() + "-" + algorithmMonitorPointVO.getMonitorPointId());
                            devicePoints.add(devicePointVo);
                        }
                    }
                    setQueryLonLatMap(map, areaVO, presetData, key, devicePoints);
                } else {
                    //有经纬度，直接添加到根据经纬度查询百分比

                    List<LongitudeLatitudeVO> pointsLonLat = new ArrayList<>();

                    String algoPointsLonLat = areaVO.getPointsLonLat();
                    List<com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO> algorithmMonitorAreaVO = JSON.parseArray(algoPointsLonLat, com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO.class);

                    algoPresent.setMonitorArea(algorithmMonitorAreaVO);

                    String key = areaVO.getDeviceCode()
                            + areaVO.getChannelCode();
                    for (com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO monitorAreaVO : algorithmMonitorAreaVO) {
                        for (com.chinatower.platform.entity.device.AlgorithmMonitorPointVO algorithmMonitorPointVO : monitorAreaVO.getMoniPointList()) {
                            LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                            longitudeLatitudeVO.setId(monitorAreaVO.getMonitorAreaId() + "-" + algorithmMonitorPointVO.getMonitorPointId());
                            longitudeLatitudeVO.setPointLon(Double.parseDouble(algorithmMonitorPointVO.getLon()));
                            longitudeLatitudeVO.setPointLat(Double.parseDouble(algorithmMonitorPointVO.getLat()));
                            pointsLonLat.add(longitudeLatitudeVO);
                        }
                    }
                    setQueryPercentMap(presentByLonLatDtoMap, pointsLonLat, key, areaVO.getDeviceCode(), areaVO.getChannelCode());
                }

                //--------------------绊线------------------------
                if (StringUtils.isEmpty(areaVO.getTripThreadLonLat())) {
                    String tripThread = areaVO.getTripThread();
                    if (StringUtils.isNotEmpty(tripThread)) {
                        List<TripThreadVO> tripThreadList = JSON.parseArray(tripThread, TripThreadVO.class);

                        //添加到更新列表
                        algo.setTripThreadList(tripThreadList);

                        List<DevicePointVo> devicePoints = new ArrayList<>();
                        String key = areaVO.getDeviceCode()
                                + areaVO.getChannelCode()
                                + presetData.getPan() + presetData.getTilt() + presetData.getZoom();
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
                                    devicePoints.add(devicePointVo);
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
                                    devicePoints.add(devicePointVo);
                                });
                            }
                        });
                        setQueryLonLatMap(map, areaVO, presetData, key, devicePoints);
                    }
                } else {
                    //有经纬度
                    List<LongitudeLatitudeVO> pointsLonLat = new ArrayList<>();

                    String tripThreadLonLat = areaVO.getTripThreadLonLat();
                    List<TripThreadVO> tripThreadList = JSON.parseArray(tripThreadLonLat, TripThreadVO.class);

                    algoPresent.setTripThreadList(tripThreadList);


                    String key = areaVO.getDeviceCode()
                            + areaVO.getChannelCode();
                    for (TripThreadVO tripThreadVO : tripThreadList) {
                        for (ThreadLineVO threadLineVO : tripThreadVO.getThreadLine()) {
                            LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                            longitudeLatitudeVO.setId(tripThreadVO.getThreadId() + LINE_SPLIT + threadLineVO.getPointId());
                            longitudeLatitudeVO.setPointLon(Double.parseDouble(threadLineVO.getLon()));
                            longitudeLatitudeVO.setPointLat(Double.parseDouble(threadLineVO.getLat()));
                            pointsLonLat.add(longitudeLatitudeVO);
                        }
                        for (DirectionVO directionVO : tripThreadVO.getDirection()) {
                            LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                            longitudeLatitudeVO.setId(tripThreadVO.getThreadId() + DIRECTION_SPLIT + directionVO.getPointId());
                            longitudeLatitudeVO.setPointLon(Double.parseDouble(directionVO.getLon()));
                            longitudeLatitudeVO.setPointLat(Double.parseDouble(directionVO.getLat()));
                            pointsLonLat.add(longitudeLatitudeVO);
                        }

                    }
                    setQueryPercentMap(presentByLonLatDtoMap, pointsLonLat, key, areaVO.getDeviceCode(), areaVO.getChannelCode());
                }


                //--------------------管线------------------------
                if (StringUtils.isEmpty(areaVO.getOilGasPipelinesLonLat())) {
                    String oilGasPipelines = areaVO.getOilGasPipelines();
                    if (StringUtils.isNotEmpty(oilGasPipelines)) {
                        List<OilGasPipelinesVO> oilGasPipelinesList = JSON.parseArray(oilGasPipelines, OilGasPipelinesVO.class);

                        //添加到更新列表
                        algo.setOilGasPipelinesList(oilGasPipelinesList);

                        String key = areaVO.getDeviceCode()
                                + areaVO.getChannelCode()
                                + presetData.getPan() + presetData.getTilt() + presetData.getZoom();

                        List<DevicePointVo> devicePoints = new ArrayList<>();

                        oilGasPipelinesList.forEach(item -> {
                            //---------------------读取所有的管线经纬度坐标--------------------------------------
                            List<DirectionVO> direction = item.getDirection();

                            if (!CollectionUtils.isEmpty(direction)) {
                                direction.forEach(point -> {
                                    DevicePointVo devicePointVo = new DevicePointVo();
                                    devicePointVo.setPercentX(String.valueOf(point.getPercentX()));
                                    devicePointVo.setPercentY(String.valueOf(point.getPercentY()));
                                    //唯一标识，方便回填数据
                                    devicePointVo.setFlag(item.getOilGasId() + "-" + point.getPointId());
                                    devicePoints.add(devicePointVo);
                                });
                            }
                        });
                        setQueryLonLatMap(map, areaVO, presetData, key, devicePoints);
                    }
                } else {
                    List<LongitudeLatitudeVO> pointsLonLat = new ArrayList<>();
                    //---------------------读取所有绊线经纬度--------------------------------------
                    String oilGasPipelinesLonLat = areaVO.getOilGasPipelinesLonLat();
                    if (StringUtils.isNotEmpty(oilGasPipelinesLonLat)) {
                        List<OilGasPipelinesVO> oilGasPipelinesList = JSON.parseArray(oilGasPipelinesLonLat, OilGasPipelinesVO.class);
                        //result
                        algoPresent.setOilGasPipelinesList(oilGasPipelinesList);
                        String key = areaVO.getDeviceCode()
                                + areaVO.getChannelCode();
                        oilGasPipelinesList.forEach(item -> {
                            //---------------------读取所有的管线经纬度坐标--------------------------------------
                            List<DirectionVO> direction = item.getDirection();

                            if (!CollectionUtils.isEmpty(direction)) {
                                direction.forEach(point -> {
                                    LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                                    longitudeLatitudeVO.setId(item.getOilGasId() + "-" + point.getPointId());
                                    longitudeLatitudeVO.setPointLon(Double.parseDouble(point.getLon()));
                                    longitudeLatitudeVO.setPointLat(Double.parseDouble(point.getLat()));
                                    pointsLonLat.add(longitudeLatitudeVO);
                                });
                            }
                        });
                        setQueryPercentMap(presentByLonLatDtoMap, pointsLonLat, key, areaVO.getDeviceCode(), areaVO.getChannelCode());
                    }
                }

                //--------------------文字------------------------
                if (StringUtils.isEmpty(areaVO.getTripThreadLonLat())) {
                    String textLabels = areaVO.getTextLabels();
                    if (StringUtils.isNotEmpty(textLabels)) {
                        List<TextLabelVO> textLabelList = JSON.parseArray(textLabels, TextLabelVO.class);

                        //添加到更新列表
                        algo.setTextLabelList(textLabelList);

                        String key = areaVO.getDeviceCode()
                                + areaVO.getChannelCode()
                                + presetData.getPan() + presetData.getTilt() + presetData.getZoom();
                        List<DevicePointVo> devicePoints = new ArrayList<>();
                        textLabelList.forEach(item -> {
                            String point = item.getPoint();
                            if (!point.isEmpty()) {
                                String[] split = point.split(",");
                                DevicePointVo devicePointVo = new DevicePointVo();
                                devicePointVo.setPercentX(split[0]);
                                devicePointVo.setPercentY(split[1]);
                                //唯一标识，方便回填数据
                                devicePointVo.setFlag(item.getPoint());
                                devicePoints.add(devicePointVo);
                            }
                        });
                        setQueryLonLatMap(map, areaVO, presetData, key, devicePoints);
                    }
                } else {
                    List<LongitudeLatitudeVO> pointsLonLat = new ArrayList<>();
                    //---------------------读取所有文本百分比点位--------------------------------------
                    String textLabel = areaVO.getTextLabelsLonLat();
                    if (StringUtils.isNotEmpty(textLabel)) {
                        List<TextLabelVO> textLabelList = JSON.parseArray(textLabel, TextLabelVO.class);
                        algoPresent.setTextLabelList(textLabelList);

                        String key = areaVO.getDeviceCode()
                                + areaVO.getChannelCode();
                        //添加所有的监控、屏蔽、基准区域百分比点位
                        textLabelList.forEach(item -> {
                            LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                            longitudeLatitudeVO.setId(item.getPoint());
                            longitudeLatitudeVO.setPointLon(Double.parseDouble(item.getLon()));
                            longitudeLatitudeVO.setPointLat(Double.parseDouble(item.getLat()));
                            pointsLonLat.add(longitudeLatitudeVO);
                        });
                        setQueryPercentMap(presentByLonLatDtoMap, pointsLonLat, key, areaVO.getDeviceCode(), areaVO.getChannelCode());
                    }
                }
                if (null != algo.getMonitorArea()
                        || null != algo.getTripThreadList()
                        || null != algo.getOilGasPipelinesList()
                        || null != algo.getTextLabelList()) {
                    algorithmAreaConvert.add(algo);
                }
                if (null != algoPresent.getMonitorArea()
                        || null != algoPresent.getTripThreadList()
                        || null != algoPresent.getOilGasPipelinesList()
                        || null != algo.getTextLabelList()) {
                    result.add(algoPresent);
                }
            }
        }



        Map<String, DevicePointVo> flagLonLat = new HashMap<>();
        if (!map.isEmpty()) {
            map.forEach((key, value) -> {
                //根据百分比坐标点反查经纬度
                DevicePointTurn devicePointTurn = new DevicePointTurn();
                devicePointTurn.setDeviceCode(value.getDeviceCode());
                devicePointTurn.setChannelCode(value.getChannelCode());

                devicePointTurn.setPoints(value.getPoints());
                //查询预置位ptz
                devicePointTurn.setParamPan(value.getPan());
                devicePointTurn.setParamTitl(value.getTilt());
                devicePointTurn.setParamZoom(value.getZoom());
                log.info("根据百分比坐标查询返回经纬度入参:{}", JSON.toJSONString(devicePointTurn));
                Result<DevicePointTurn> devicePointTurnResult = iTradeWarningOrderService.turnLatitudeByPoint(devicePointTurn);
                log.info("根据百分比坐标查询返回经纬度返回:{}", JSON.toJSONString(devicePointTurnResult));
                if (200 == (devicePointTurnResult.getCode())) {
                    DevicePointTurn data = devicePointTurnResult.getData();
                    List<DevicePointVo> pointsLatLon = data.getPoints();
                    if (!pointsLatLon.isEmpty()) {
                        //转换map结构
                        Map<String, DevicePointVo> flagLonLatMap = pointsLatLon.stream().collect(Collectors.toMap(DevicePointVo::getFlag, devicePointVo -> devicePointVo));
                        flagLonLat.putAll(flagLonLatMap);
                    }
                }
            });
        }
        log.info("转换后的经纬度:{}", JSON.toJSONString(flagLonLat));

        if (!alarmAreaPointConvert.isEmpty()) {

            //获取到经纬度后
            // 告警的数据反写到告警屏蔽区域表
            List<AlarmAreaUpdateReq> updateAreaPosition = new ArrayList<>();
            //设置告警屏蔽区域的经纬度到 经纬度转换百分比 map
            alarmAreaPointConvert.forEach(item -> {

                //result
                AlgoPresentVo algoPresent = new AlgoPresentVo();
                algoPresent.setAreaId(item.getAreaId());
                algoPresent.setType(ALARM_TYPE);

                JSONArray jsonArray = JSON.parseArray(item.getPoints());
                for (int i = 0; i < jsonArray.size(); i++) {
                    List<LongitudeLatitudeVO> pointsLonLat = new ArrayList<>();
                    String key = item.getDeviceCode() + item.getChannelCode();
                    String flag = ALARM_PRE + item.getAreaId() + i;
                    if (flagLonLat.containsKey(flag)) {
                        DevicePointVo devicePointVo = flagLonLat.get(flag);
                        Object o = jsonArray.get(i);
                        JSONObject parse = JSON.parseObject(String.valueOf(o));
                        parse.put("lon", devicePointVo.getLongitude());
                        parse.put("lat", devicePointVo.getLatitude());
                        jsonArray.set(i, parse);
                        //设置presentByLonLatDtoMap

                        LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                        longitudeLatitudeVO.setId(flag);
                        longitudeLatitudeVO.setPointLon(Double.parseDouble(devicePointVo.getLongitude()));
                        longitudeLatitudeVO.setPointLat(Double.parseDouble(devicePointVo.getLatitude()));
                        pointsLonLat.add(longitudeLatitudeVO);
                    }
                    setQueryPercentMap(presentByLonLatDtoMap, pointsLonLat, key, item.getDeviceCode(), item.getChannelCode());
                }
                String positionInformation = JSON.toJSONString(jsonArray);
                item.setPositionInformation(positionInformation);
                AlarmAreaUpdateReq alarmAreaUpdateReq = new AlarmAreaUpdateReq();
                alarmAreaUpdateReq.setAreaId(item.getAreaId());
                alarmAreaUpdateReq.setPositionInformation(positionInformation);
                updateAreaPosition.add(alarmAreaUpdateReq);

                //result
                algoPresent.setPositionInformationList(jsonArray);
                result.add(algoPresent);

            });

            //更新告警区域表
            log.info("更新告警区域表数据入参:{}", JSON.toJSONString(updateAreaPosition));
            Result resultAreaPosition = iTradeWarningOrderService.updateAreaPosition(updateAreaPosition);
            log.info("更新告警区域表数据返回:{}", JSON.toJSONString(resultAreaPosition));
        }


        //设置算法相关坐标，保存
        batchSetAlgoAndUpdate(algorithmAreaConvert, flagLonLat, presentByLonLatDtoMap, result);


        Map<String, PercentVO> percentMap = new HashMap<>();
        //根据经纬度返回百分比坐标
        if (!presentByLonLatDtoMap.isEmpty()) {
            presentByLonLatDtoMap.forEach((k,v) -> {
                log.info("根据经纬度查询百分比getPercentPtzByMassPointBatchNew入参:{}", JSON.toJSONString(v));
                VideoPtzRes percentPtzByMassPointBatchNew = videoPtzQueryService.getPercentPtzByMassPointBatchNew(v);
                log.info("根据经纬度查询百分比getPercentPtzByMassPointBatchNew返回:{}", JSON.toJSONString(percentPtzByMassPointBatchNew));
                //u=x  v=y
                List<PercentVO> vertexPercentList = percentPtzByMassPointBatchNew.getVertexPercentList();
                if (!vertexPercentList.isEmpty()) {
                    //转换map结构
                    Map<String, PercentVO> precentByLonLatMap = vertexPercentList.stream().collect(Collectors.toMap(PercentVO::getId, percentVO -> percentVO));
                    percentMap.putAll(precentByLonLatMap);
                }
            });
        }

        log.info("经纬度转换后的百分比:{}", JSON.toJSONString(percentMap));
        //数据回填

        result.forEach(item -> {
            if (ALGO_TYPE.equals(item.getType())) {
                if (null != item.getMonitorArea()) {
                    List<AlgorithmMonitorAreaVO> monitorAreaLonLat = item.getMonitorArea();
                    monitorAreaLonLat.forEach(monitorArea -> {
                        List<AlgorithmMonitorPointVO> moniPointList = monitorArea.getMoniPointList();
                        if (!moniPointList.isEmpty()) {
                            moniPointList.forEach(point -> {
                                String id = monitorArea.getMonitorAreaId() + "-" + point.getMonitorPointId();
                                if (percentMap.containsKey(id)) {
                                    point.setPercentX(Float.parseFloat(percentMap.get(id).getPercentU()));
                                    point.setPercentY(Float.parseFloat(percentMap.get(id).getPercentV()));
                                }
                            });
                        }
                    });
                    item.setPointsLonLat(JSON.toJSONString(monitorAreaLonLat));
                }
                //放置管线经纬度
                if (null != item.getOilGasPipelinesList()) {
                    List<OilGasPipelinesVO> oilGasLonLat = item.getOilGasPipelinesList();
                    oilGasLonLat.forEach(oilGas -> {
                        List<DirectionVO> direction = oilGas.getDirection();
                        if (!direction.isEmpty()) {

                            direction.forEach(point -> {
                                String id = oilGas.getOilGasId() + "-" + point.getPointId();
                                if (percentMap.containsKey(id)) {
                                    point.setPercentX(Float.parseFloat(percentMap.get(id).getPercentU()));
                                    point.setPercentY(Float.parseFloat(percentMap.get(id).getPercentV()));

                                }
                            });
                        }
                    });
                    item.setOilGasPipelinesLonLat(JSON.toJSONString(oilGasLonLat));
                }
                //放置绊线
                if (null != item.getTripThreadList()) {
                    List<TripThreadVO> tripThreadListLonLat = item.getTripThreadList();
                    tripThreadListLonLat.forEach(tripThread -> {
                        List<ThreadLineVO> threadLine = tripThread.getThreadLine();
                        if (!threadLine.isEmpty()) {
                            threadLine.forEach(point -> {
                                String id = tripThread.getThreadId() + LINE_SPLIT + point.getPointId();
                                if (percentMap.containsKey(id)) {
                                    point.setPercentX(Float.parseFloat(percentMap.get(id).getPercentU()));
                                    point.setPercentY(Float.parseFloat(percentMap.get(id).getPercentV()));

                                }
                            });
                        }
                        List<DirectionVO> direction = tripThread.getDirection();
                        if (!direction.isEmpty()) {
                            direction.forEach(point -> {
                                String id = tripThread.getThreadId() + DIRECTION_SPLIT + point.getPointId();
                                if (percentMap.containsKey(id)) {
                                    point.setPercentX(Float.parseFloat(percentMap.get(id).getPercentU()));
                                    point.setPercentY(Float.parseFloat(percentMap.get(id).getPercentV()));

                                }
                            });
                        }
                    });
                    item.setTripThreadLonLat(JSON.toJSONString(tripThreadListLonLat));
                }
                //放置文本
                if (null != item.getTextLabelList()) {
                    List<TextLabelVO> textLabelListLonLat = item.getTextLabelList();
                    textLabelListLonLat.forEach(textLabel -> {
                        if (null != textLabel) {
                            String id = textLabel.getPoint();
                            if (percentMap.containsKey(id)) {
                                String percentU = percentMap.get(id).getPercentU();
                                String percentV = percentMap.get(id).getPercentV();
                                textLabel.setPoint(percentU + "," + percentV);
                            }
                        }
                    });
                    //转json 保存
                    item.setTextLabelsLonLat(JSON.toJSONString(textLabelListLonLat));
                }
            } else {
                if (null != item.getPositionInformationList()) {
                    JSONArray jsonArray = item.getPositionInformationList();
                    item.setPositionInformationOld(JSON.toJSONString(jsonArray));
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String id = ALARM_PRE + item.getAreaId() + i;
                        if (percentMap.containsKey(id)) {
                            PercentVO percentVO = percentMap.get(id);
                            Object o = jsonArray.get(i);
                            JSONObject parse = JSON.parseObject(String.valueOf(o));
                            parse.put("percentX", percentVO.getPercentU());
                            parse.put("percentY", percentVO.getPercentV());
                            jsonArray.set(i, parse);
                        }
                    }
                    item.setPositionInformation(JSON.toJSONString(jsonArray));
                }
            }
        });
       return result;
    }

    /**
     * 设置算法百分比转换经纬度后的值
     *
     * result 保存最后返回前端的值，等待匹配百分比
     * @param algorithmAreaConvert
     * @param flagLonLat
     * @param presentByLonLatDtoMap
     */
    private void batchSetAlgoAndUpdate(List<AlgorithmAreaVO> algorithmAreaConvert,
                                       Map<String, DevicePointVo> flagLonLat,
                                       Map<String, VideoPtzReq> presentByLonLatDtoMap,
                                       List<AlgoPresentVo> result) {
        // 设置算法相关坐标 经纬度转换百分比map
        algorithmAreaConvert.forEach(item -> {
            //result
            AlgoPresentVo algoPresent = new AlgoPresentVo();
            algoPresent.setConfigId(item.getConfigId());
            algoPresent.setType(ALGO_TYPE);

            String key = item.getDeviceCode() + item.getChannelCode();
            List<LongitudeLatitudeVO> pointsLonLat = new ArrayList<>();
            if (null != item.getMonitorArea()) {
                List<AlgorithmMonitorAreaVO> monitorAreaLonLat = item.getMonitorArea();
                monitorAreaLonLat.forEach(monitorArea -> {
                    List<AlgorithmMonitorPointVO> moniPointList = monitorArea.getMoniPointList();
                    if (!moniPointList.isEmpty()) {
                        moniPointList.forEach(point -> {
                            String flag = monitorArea.getMonitorAreaId() + "-" + point.getMonitorPointId();
                            if (flagLonLat.containsKey(flag)) {
                                DevicePointVo pointLonLat = flagLonLat.get(flag);
                                point.setLat(pointLonLat.getLatitude());
                                point.setLon(pointLonLat.getLongitude());

                                LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                                longitudeLatitudeVO.setId(flag);
                                longitudeLatitudeVO.setPointLon(Double.parseDouble(pointLonLat.getLongitude()));
                                longitudeLatitudeVO.setPointLat(Double.parseDouble(pointLonLat.getLatitude()));
                                pointsLonLat.add(longitudeLatitudeVO);
                            }
                        });
                    }
                });
                //转json 保存
                item.setPointsLonLat(JSON.toJSONString(monitorAreaLonLat));
                algoPresent.setMonitorArea(monitorAreaLonLat);
            }
            //放置管线经纬度
            if (null != item.getOilGasPipelinesList()) {
                List<OilGasPipelinesVO> oilGasLonLat = item.getOilGasPipelinesList();
                oilGasLonLat.forEach(oilGas -> {
                    List<DirectionVO> direction = oilGas.getDirection();
                    if (!direction.isEmpty()) {

                        direction.forEach(point -> {
                            String flag = oilGas.getOilGasId() + "-" + point.getPointId();
                            if (flagLonLat.containsKey(flag)) {
                                DevicePointVo pointLonLat = flagLonLat.get(flag);
                                point.setLat(pointLonLat.getLatitude());
                                point.setLon(pointLonLat.getLongitude());

                                LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                                longitudeLatitudeVO.setId(flag);
                                longitudeLatitudeVO.setPointLon(Double.parseDouble(pointLonLat.getLongitude()));
                                longitudeLatitudeVO.setPointLat(Double.parseDouble(pointLonLat.getLatitude()));
                                pointsLonLat.add(longitudeLatitudeVO);

                            }
                        });
                    }
                });
                //转json 保存
                item.setOilGasPipelinesLonLat(JSON.toJSONString(oilGasLonLat));
                algoPresent.setOilGasPipelinesList(oilGasLonLat);
            }
            //放置绊线
            if (null != item.getTripThreadList()) {
                List<TripThreadVO> tripThreadListLonLat = item.getTripThreadList();
                tripThreadListLonLat.forEach(tripThread -> {
                    List<ThreadLineVO> threadLine = tripThread.getThreadLine();
                    if (!threadLine.isEmpty()) {
                        threadLine.forEach(point -> {
                            String flag = tripThread.getThreadId() + LINE_SPLIT + point.getPointId();
                            if (flagLonLat.containsKey(flag)) {
                                DevicePointVo pointLonLat = flagLonLat.get(flag);
                                point.setLat(pointLonLat.getLatitude());
                                point.setLon(pointLonLat.getLongitude());

                                LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                                longitudeLatitudeVO.setId(flag);
                                longitudeLatitudeVO.setPointLon(Double.parseDouble(pointLonLat.getLongitude()));
                                longitudeLatitudeVO.setPointLat(Double.parseDouble(pointLonLat.getLatitude()));
                                pointsLonLat.add(longitudeLatitudeVO);
                            }
                        });
                    }
                    List<DirectionVO> direction = tripThread.getDirection();
                    if (!direction.isEmpty()) {
                        direction.forEach(point -> {
                            String flag = tripThread.getThreadId() + DIRECTION_SPLIT + point.getPointId();
                            if (flagLonLat.containsKey(flag)) {
                                DevicePointVo pointLonLat = flagLonLat.get(flag);
                                point.setLat(pointLonLat.getLatitude());
                                point.setLon(pointLonLat.getLongitude());

                                LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                                longitudeLatitudeVO.setId(flag);
                                longitudeLatitudeVO.setPointLon(Double.parseDouble(pointLonLat.getLongitude()));
                                longitudeLatitudeVO.setPointLat(Double.parseDouble(pointLonLat.getLatitude()));
                                pointsLonLat.add(longitudeLatitudeVO);
                            }
                        });
                    }
                });
                //转json 保存
                item.setTripThreadLonLat(JSON.toJSONString(tripThreadListLonLat));
                algoPresent.setTripThreadList(tripThreadListLonLat);
            }
            //放置文本
            if (null != item.getTextLabelList()) {
                List<TextLabelVO> textLabelListLonLat = item.getTextLabelList();
                textLabelListLonLat.forEach(textLabel -> {
                    if (null != textLabel) {
                        String flag = textLabel.getPoint();
                        if (flagLonLat.containsKey(flag)) {
                            DevicePointVo pointLonLat = flagLonLat.get(flag);
                            textLabel.setLat(pointLonLat.getLatitude());
                            textLabel.setLon(pointLonLat.getLongitude());

                            LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
                            longitudeLatitudeVO.setId(flag);
                            longitudeLatitudeVO.setPointLon(Double.parseDouble(pointLonLat.getLongitude()));
                            longitudeLatitudeVO.setPointLat(Double.parseDouble(pointLonLat.getLatitude()));
                            pointsLonLat.add(longitudeLatitudeVO);
                        }
                    }
                });
                //转json 保存
                item.setTextLabelsLonLat(JSON.toJSONString(textLabelListLonLat));
                algoPresent.setTextLabelList(textLabelListLonLat);
            }
            setQueryPercentMap(presentByLonLatDtoMap, pointsLonLat, key, item.getDeviceCode(), item.getChannelCode());
            result.add(algoPresent);
        });
        //批量更新算法经纬度
        if (!algorithmAreaConvert.isEmpty()) {
            videoQueryDAO.batchUpdateAlgo(algorithmAreaConvert);
        }
    }

    private void setQueryPercentMap(Map<String, VideoPtzReq> presentByLonLatDtoMap, List<LongitudeLatitudeVO> pointsLonLat, String key, String deviceCode, String channelCode) {
        if (presentByLonLatDtoMap.containsKey(key)) {
            VideoPtzReq videoPtzReq = presentByLonLatDtoMap.get(key);
            List<LongitudeLatitudeVO> vertexList = videoPtzReq.getVertexList();
            vertexList.addAll(pointsLonLat);
        } else {
            VideoPtzReq videoPtzReq = new VideoPtzReq();
            videoPtzReq.setDeviceCode(deviceCode);
            videoPtzReq.setChannelCode(channelCode);
            videoPtzReq.setVertexList(pointsLonLat);
            presentByLonLatDtoMap.put(key, videoPtzReq);
        }
    }

    private void setQueryLonLatMap(Map<String, QueryDevicePointLonLatDto> map, AlgorithmAreaVO areaVO, PresetData presetData, String key, List<DevicePointVo> devicePoints) {
        if (map.containsKey(key)) {
            QueryDevicePointLonLatDto queryDevicePointLonLatDto = map.get(key);
            List<DevicePointVo> points1 = queryDevicePointLonLatDto.getPoints();
            points1.addAll(devicePoints);
        } else {
            QueryDevicePointLonLatDto queryDevicePointLonLatDto = new QueryDevicePointLonLatDto();
            queryDevicePointLonLatDto.setDeviceCode(areaVO.getDeviceCode());
            queryDevicePointLonLatDto.setChannelCode(areaVO.getChannelCode());
            queryDevicePointLonLatDto.setPan(String.valueOf(presetData.getPan()));
            queryDevicePointLonLatDto.setTilt(String.valueOf(presetData.getTilt()));
            queryDevicePointLonLatDto.setZoom(String.valueOf(presetData.getZoom()));
            queryDevicePointLonLatDto.setPoints(devicePoints);
            map.put(key, queryDevicePointLonLatDto);
        }
    }

    public List<OrderDevicesVO> getOrderDevices(VideoBaseReq videoBaseReq) {
        return videoQueryDAO.getOrderDevices(videoBaseReq);
    }

}