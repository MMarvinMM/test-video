package com.chinatower.analysis.modules.workbench.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.GsonUtils;
import com.google.gson.JsonObject;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.modules.common.CommonUtils;
import com.chinatower.analysis.modules.common.Constants;
import com.chinatower.analysis.modules.statistic.dao.DeviceStatisticsDAO;
import com.chinatower.analysis.modules.workbench.dao.WorkbenchDAO;
import com.chinatower.analysis.modules.workbench.util.StatDimensionConstants;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.Collator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class WorkbenchService extends TowerBaseController {
    @Resource
    private WorkbenchDAO workbenchDAO;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    private CommonUtils commonUtils;

    @Resource
    private DeviceStatisticsDAO deviceStatisticsDAO;

    /**
     * 获取事件统计数量
     *
     * @param analysisVO (create_time 创建时间)
     * @return EventNumVO (todayAll 今日事件总数)
     * @return EventNumVO (todayProcess 今日处理中数量)
     * @return EventNumVO (todayComplete 今日已完结数量)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="t.device_code",tenantColumn="t.tenant_id",
            industryCode="t.industry_code", alarmSourceColumn="t.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "t.province_id"
            ,areaCity = "t.city_id",areaCode = "t.county_id")
    public Result<EventNumVO> getEventNum(AnalysisVO analysisVO) {
        EventNumVO eventNum = workbenchDAO.getEventNum(analysisVO);
        // 获取当前时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 获取六个月前的时间
        LocalDateTime sixMonthsAgoDateTime = currentDateTime.minus(6, ChronoUnit.MONTHS).plusDays(1);
        // 格式化输出为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.YYYY_MM_DD);
        String dateStr = sixMonthsAgoDateTime.format(formatter)+" 00:00:00";
        String dateEnd = currentDateTime.format(formatter)+" 23:59:59";
        analysisVO.setQueryStartTime(dateStr);
        analysisVO.setQueryEndTime(dateEnd);
        analysisVO.setDimensionFlag("3");
        EventNumVO eventNumTotal = workbenchDAO.getEventNumTotal(analysisVO);
        eventNum.setTotalComplete(eventNumTotal.getTotalComplete());
        eventNum.setTotalProcess(eventNumTotal.getTotalProcess());
        return Result.data(eventNum);
    }


    /**
     * 获取事件统计数量
     *
     * @param analysisVO
     * @return EventNumVO (todayAll 事件总数)
     * @return EventNumVO (totalProcess 处理中数量)
     * @return EventNumVO (totalProcess 已完结数量)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="t.device_code",tenantColumn="t.tenant_id",
            industryCode="t.industry_code", alarmSourceColumn="t.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "t.province_id"
            ,areaCity = "t.city_id",areaCode = "t.county_id")
    public Result<EventNumVO> getEventNumApp(AnalysisVO analysisVO) {
        log.info("查询租户事件状态排行,入参{}",analysisVO.toString());
        try {
            analysisVO.setDimensionFlag("3");
            EventNumVO total = workbenchDAO.getEventNumTotal(analysisVO);
            total.setTotalProcess(total.getTotalProcess());
            total.setTotalComplete(total.getTotalComplete());
            return Result.data(total);
        }catch (Exception e){
            log.error("查询租户事件状态排行异常：", e);
            return Result.fail("查询租户事件状态排行异常");
        }
    }


    /**
     * 获取事件统计数量
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="t.device_code",tenantColumn="t.tenant_id",
            industryCode="t.industry_code", alarmSourceColumn="t.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "t.province_id"
            ,areaCity = "t.city_id",areaCode = "t.county_id")
    public List<Map> getEventNumDevApp(StatisticsVO statisticsVO) {
        log.info("查询租户摄像机告警排行,入参{}",statisticsVO.toString());
        try {
            List<Map> eventNumDev = workbenchDAO.getEventNumDev(statisticsVO);
            StatisticsVO vo = new StatisticsVO();
            List<String> deviceCode = eventNumDev.stream().map(s ->String.valueOf(s.get("deviceCode"))).distinct().collect(Collectors.toList());
            vo.setDeviceCodeList(deviceCode);
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();
            if (ObjectUtil.isNotNull(user)){
                String tenantId = user.getTenantId();
                String industryCode = user.getIndustryCode();
                vo.setTenantId(tenantId);
                vo.setIndustryCode(industryCode);
            }
            List<Map> devices = workbenchDAO.getDevices(vo);
            for (Map map : eventNumDev) {
                    for (Map item : devices) {
                        //遍历判断摄像机编码是否有与告警数量最高五位的摄像机编码相同
                        if(item.get(Constants.DEVICE_CODE).equals(map.get(Constants.DEVICE_CODE))){
                            map.put(Constants.DEVICE_NAME,item.get(Constants.DEVICE_NAME));
                            break;
                        }
                    }
            }
            return eventNumDev;
        }catch (Exception e){
            log.error("查询租户摄像机告警排行异常：", e);
            List<Map> list = new ArrayList<>();
            return list;
        }
    }

    /**
     * 统计监控设备数量
     * @param analysisVO (year 年 month 月 day 日)
     *
     * @return deviceCountTotal 摄像机总数
     * @return devCountOnline 摄像机在线数
     * @return devCountOffline 摄像机离线数
     * @return channelCountTotal 通道总数
     * @return channelCountOnline   通道在线数
     * @return channelCountOffline 通道离线数
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",industryCode="dataTable.industry_code",deviceColumn = "dataTable.device_code",acceptColumn="dataTable.accept_node")
    public Result<DeviceStatisticsVO> getDeviceStatistics(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);

        DeviceStatisticsVO vo = new DeviceStatisticsVO();
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return Result.data(vo);
        }

        Map devList = workbenchDAO.getStatisticsDevicesUserCount(analysisVO);
        vo.setDevCountOffline(Integer.parseInt(devList.get("devCountOffline").toString()));
        vo.setDevCountOnline(Integer.parseInt(devList.get("devCountOnline").toString()));
        vo.setDeviceCountTotal(Integer.parseInt(devList.get("deviceCountTotal").toString()));
        Map channelList = workbenchDAO.getStatisticsChannelUserCount(analysisVO);
        vo.setChannelCountOffline(Integer.parseInt(channelList.get("channelCountOffline").toString()));
        vo.setChannelCountOnline(Integer.parseInt(channelList.get("channelCountOnline").toString()));
        vo.setChannelCountTotal(Integer.parseInt(channelList.get("channelCountTotal").toString()));
        AnalysisVO algorithmVO = new AnalysisVO();
        BeanUtils.copyProperties(analysisVO,algorithmVO);
        boolean localizationFlag = getLocalizationFlag();
        //如果是国产化
        if (localizationFlag){
            Map foreAlgorithm = workbenchDAO.getStatisticsChannelForeAlgorithm(analysisVO);
            Map afterAlgorithm = workbenchDAO.getStatisticsChannelAfterAlgorithm(analysisVO);
            int foreNum = 0;
            int foreEnableNum = 0;
            int afterNum = 0;
            int afterEnableNum = 0;
            int totalNum = 0;
            int totalEnableNum = 0;
            if (ObjectUtil.isNotNull(foreAlgorithm)){
                foreNum = Integer.parseInt(String.valueOf(foreAlgorithm.get(Constants.NUM)));
                foreEnableNum = Integer.parseInt(String.valueOf(foreAlgorithm.get(Constants.ENABLE_NUM)));
            }
            if (ObjectUtil.isNotNull(afterAlgorithm)){
                afterNum = Integer.parseInt(String.valueOf(afterAlgorithm.get(Constants.NUM)));
                afterEnableNum = Integer.parseInt(String.valueOf(afterAlgorithm.get(Constants.ENABLE_NUM)));
            }
            totalNum =  foreNum + afterNum;
            totalEnableNum = foreEnableNum + afterEnableNum;
            vo.setDisAlgorithmTaskNum(totalNum);
            vo.setUpAlgorithmTaskNum(totalEnableNum);
        }else {
            Map algorithmList = workbenchDAO.getStatisticsChannelAlgorithm(analysisVO);
            vo.setDisAlgorithmTaskNum(Integer.parseInt(String.valueOf(algorithmList.get(Constants.ALL_NUM))));
            vo.setUpAlgorithmTaskNum(Integer.parseInt(String.valueOf(algorithmList.get(Constants.ENABLE_NUM))));
        }


        if (vo.getChannelCountTotal() > 0) {
            vo.setOnlinePercentage(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getChannelCountOnline())/vo.getChannelCountTotal())));
        }

        if (vo.getDeviceCountTotal() > 0) {
            vo.setOnlinePercentageDev(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getDevCountOnline())/vo.getDeviceCountTotal())));
        }
        log.info("getDeviceStatistics result:{} ", JSON.toJSONString(vo));
        return Result.data(vo);
    }

    /**
     * 统计无人机设备数量
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="od.tenant_id",industryCode="od.industry_code",deviceColumn = "uav.device_code", alarmSourceVal = "11",acceptColumn="od.accept_node")
    public Result<UavDeviceStatisticsVo> getUavDeviceStatistics(AnalysisVO analysisVO){
        UavDeviceStatisticsVo vo = workbenchDAO.getUavDeviceStatistics(analysisVO);
        UavDeviceStatisticsVo nestVo = workbenchDAO.getUavNestStatistics(analysisVO);
        vo.setUavNestDeviceCount(nestVo.getUavNestDeviceCount());
        vo.setUavNestDeviceOnlineCount(nestVo.getUavNestDeviceOnlineCount());
        vo.setUavNestDeviceOfflineCount(nestVo.getUavNestDeviceOfflineCount());
        if (vo.getUavDeviceCount() > 0) {
            vo.setUavDeviceOnlinePercent(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getUavDeviceOnlineCount())/vo.getUavDeviceCount())));
        }
        if (vo.getUavNestDeviceCount() > 0) {
            vo.setUavNestDeviceOnlinePercent(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getUavNestDeviceOnlineCount())/vo.getUavNestDeviceCount())));
        }

        return Result.data(vo);
    }

    /**
     * 统计无人机设备数量（按区域维度）
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "uav.device_code", alarmSourceVal = "11")
    public List<Map> getUavDeviceStatisticsByArea(AnalysisVO analysisVO) {
        if(ObjectUtil.isNull(analysisVO.getAreaBoxNum())){ //默认省维度统计
            analysisVO.setAreaBoxNum(0);
        }
        if (ObjectUtil.isNotNull(analysisVO.getAreaUavSelect())){
            analysisVO.setAreaBoxNum(analysisVO.getAreaUavSelect());
        }
        return  workbenchDAO.getUavDeviceStatisticsByArea(analysisVO);
    }

    /**
     * 统计喇叭设备数量
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "12",acceptColumn="od.accept_node")
    public Result<HornDeviceStatisticsVo> getHornDeviceStatistics(AnalysisVO analysisVO) {
        HornDeviceStatisticsVo vo = workbenchDAO.getHornDeviceStatistics(analysisVO);
        if (vo.getHornDeviceCount() > 0) {
            vo.setHornDeviceOnlinePercent(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getHornDeviceOnlineCount())/vo.getHornDeviceCount())));
        }

        return Result.data(vo);
    }

    /**
     * 统计喇叭设备数量（按区域维度）
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "12",acceptColumn="od.accept_node")
    public List<Map> getHornDeviceStatisticsByArea(AnalysisVO analysisVO) {
        if(ObjectUtil.isNull(analysisVO.getAreaBoxNum())){ //默认省维度统计
            analysisVO.setAreaBoxNum(0);
        }
        if (ObjectUtil.isNotNull(analysisVO.getAreaHornSelect())){
            analysisVO.setAreaBoxNum(analysisVO.getAreaHornSelect());
        }
        return  workbenchDAO.getHornDeviceStatisticsByArea(analysisVO);
    }

    /**
     * 统计物联设备数量
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "8,10",acceptColumn="od.accept_node")
    public Result<IotDeviceStatisticsVo> getIotDeviceStatistics(AnalysisVO analysisVO) {
        IotDeviceStatisticsVo vo = workbenchDAO.getIotDeviceStatistics(analysisVO);
        if (vo.getIotDeviceCount() > 0) {
            vo.setIotDeviceOnlinePercent(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getIotDeviceOnlineCount())/vo.getIotDeviceCount())));
        }
        return Result.data(vo);
    }

    /**
     * 统计物联设备数量（按区域维度）
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "8,10",acceptColumn="od.accept_node")
    public List<Map> getIotDeviceStatisticsByArea(AnalysisVO analysisVO) {
        if(ObjectUtil.isNull(analysisVO.getAreaBoxNum())){ //默认省维度统计
            analysisVO.setAreaBoxNum(0);
        }
        if (ObjectUtil.isNotNull(analysisVO.getAreaIotSelect())){
            analysisVO.setAreaBoxNum(analysisVO.getAreaIotSelect());
        }

        return  workbenchDAO.getIotDeviceStatisticsByArea(analysisVO);
    }

    /**
     * 统计雷达设备数量
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "8,10",acceptColumn="od.accept_node")
    public Result<RadarDeviceStatisticsVo> getRadarDeviceStatistics(AnalysisVO analysisVO) {
        RadarDeviceStatisticsVo vo = workbenchDAO.getRadarDeviceStatistics(analysisVO);
        if (vo.getRadarDeviceCount() > 0) {
            vo.setRadarDeviceOnlinePercent(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getRadarDeviceOnlineCount())/vo.getRadarDeviceCount())));
        }

        return Result.data(vo);
    }

    /**
     * 统计雷达设备数量（按区域维度）
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "8,10",acceptColumn="od.accept_node")
    public List<Map> getRadarDeviceStatisticsByArea(AnalysisVO analysisVO) {
        if(ObjectUtil.isNull(analysisVO.getAreaBoxNum())){ //默认省维度统计
            analysisVO.setAreaBoxNum(0);
        }
        if (ObjectUtil.isNotNull(analysisVO.getAreaRadarSelect())){
            analysisVO.setAreaBoxNum(analysisVO.getAreaRadarSelect());
        }

        return  workbenchDAO.getRadarDeviceStatisticsByArea(analysisVO);
    }

    /**
     * 近7日事件数量
     *
     * @param analysisVO create_date 创建时间
     * @return eventDate 创建时间
     * @return name 名称
     * @return num 数量
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumBySource(AnalysisVO analysisVO){
        //获取告警来源枚举值
        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.WARNING_SOURCE);
        List<SysDictDataResponse> dictList = res.getData();
        List<Map> list = workbenchDAO.getEventNumBySource(analysisVO);
        List<List<Map>> newList = new ArrayList<>();
        //按照日期分组排序
        Map<Object,List<Map>> eventMap= list.stream()
                .collect(Collectors.groupingBy(i->i.get("eventDate")));
        List<String> keySet=eventMap.keySet().stream().map(i->String.valueOf(i)).
                sorted(Comparator.comparing(i->i)).collect(Collectors.toList());
        for (String str:keySet) {
            newList.add(eventMap.get(str));
        }
        for(int i=0; i<newList.size();i++){
            List<Map> listMaps = newList.get(i);
            for(Map valueMap : listMaps) {
                for(int j=0; j<dictList.size(); j++) {
                    //通过warningSource从字典表获取对应告警来源name
                    if (valueMap.get("name").equals(dictList.get(j).getDictValue())){
                        valueMap.put("name",dictList.get(j).getDictLabel());
                    }
                }
            }
            newList.set(i,listMaps);
        }
        return Result.data(newList);
    }

    /**
     * 事件高发区域
     *
     * @param analysisVO
     * @return analysisVO（provinceId:省份编码 ）
     * @return analysisVO（cityId:市编码）
     * @return analysisVO（countyId:区县编码）
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEventNumByArea(AnalysisVO analysisVO){
        if (analysisVO.getAreaBoxNum() == null) {
            analysisVO.setAreaBoxNum(0);
        }
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> eventNumByArea = workbenchDAO.getEventNumByArea(analysisVO);
        if (CollectionUtils.isEmpty(eventNumByArea)) {
            return eventNumByArea;
        }
       this.areaCodeHandle(eventNumByArea,analysisVO);
        return eventNumByArea;
    }

    /**
     * 告警数量按照区县及告警类型排行
     *
     * @param analysisVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<AnalysisAreaTypeVO> getEventNumByAreaAndType(AnalysisVO analysisVO){//处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<AnalysisAreaTypeVO> eventNumByArea = workbenchDAO.getEventNumByAreaAndType(analysisVO);

        System.out.println(JSON.toJSONString(eventNumByArea));
        if (CollectionUtils.isEmpty(eventNumByArea)) {
            return eventNumByArea;
        }
        List<String> countyIdList = eventNumByArea.stream().map(v -> v.getCountyId()).collect(toList());
        List<Map<String, String>>alarmByCode = workbenchDAO.getAlarmByCodes(countyIdList);
        Map<String, String> codeList = alarmByCode.stream().collect( Collectors.toMap( t -> t.get(Constants.AREA_CODE), t -> t.get(Constants.AREA_NAME), (o, n) -> n, HashMap::new));
        for (AnalysisAreaTypeVO vo : eventNumByArea) {
            String countyName = codeList.get(vo.getCountyId());
            vo.setCountyName(countyName);
        }
        return eventNumByArea;
    }

    /**
     * 查询告警数量按告警来源排行
     * @param statisticsVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEventAlarmNumSource(StatisticsVO statisticsVO) {
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        List<Map> statisticsList = workbenchDAO.getEventAlarmNumSource(statisticsVO);
        return statisticsList;
    }

    /**
     * 查询告警来源（按行业维度）
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public List<Map> getEventAlarmNumSourceIndustry(AnalysisVO analysisVO) {
        StatDimensionConstants.normalizeScreenAllFilters(analysisVO);
        List<Map> statisticsList = workbenchDAO.getEventAlarmNumSourceIndustry(analysisVO);
        if (!CollectionUtils.isEmpty(statisticsList)) {
            List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
            Map<String, String> sourceMap = warningSourceEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel));
            for (Map map : statisticsList) {
                map.put("warningSourceName",MapUtil.getStr(sourceMap,map.get("warningSource")));
            }
        }
        return statisticsList;
    }

    /**
     * 查询告警类型（按行业维度）
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public List<Map> getEventAlarmNumTypeIndustry(AnalysisVO analysisVO) {
        StatDimensionConstants.normalizeScreenAllFilters(analysisVO);
        List<Map> statisticsList = workbenchDAO.getEventAlarmNumTypeIndustry(analysisVO);
        // SUB_TASK：下游 + 小模型剔场景；TYPE 不变
        statisticsList = enrichAnalysisSubTaskWithSmallModels(analysisVO, statisticsList, false);
        if (!CollectionUtils.isEmpty(statisticsList)) {
            //告警类型
            List<Map<String,String>> alarmTypeList = workbenchDAO.getAlarmTypeIndustry();
            Map<String, String> typeMap = alarmTypeList.stream().collect( Collectors.toMap( t -> t.get(Constants.TYPE_VALUE), t -> t.get(Constants.TYPE_NAME), (o, n) -> n, HashMap::new));
            for (Map map : statisticsList) {
                if (map.get(Constants.TYPE_NAME) == null) {
                    map.put(Constants.TYPE_NAME,MapUtil.getStr(typeMap,map.get(Constants.TYPE_VALUE)));
                }
            }
        }
        return statisticsList;
    }

    /**
     * 按年或月查询告警事件类型
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Object getDevTypeYearMonth(StatisticsVO statisticsVO) {
        // SUB_TASK 需 enrich 后再分页：清空 pageNum/pageSize，避免 PageHelper/数据权限拦截器按页截断 SQL，
        // 再与 typeTagCommon 的 Java skip 叠切导致第 2 页 data 为空、total 变小
        PageHelper.clearPage();
        if (statisticsVO != null
                && statisticsVO.getPageNum() != null && statisticsVO.getPageSize() != null) {
            statisticsVO.setPageNums(statisticsVO.getPageNum());
            statisticsVO.setPageSizes(statisticsVO.getPageSize());
            statisticsVO.setPageNum(null);
            statisticsVO.setPageSize(null);
        }
        List<Map> statisticsMap = workbenchDAO.getInitDevTypeYearMonth(statisticsVO);
        // SUB_TASK：下游任务 + 小模型，剔场景；TYPE 告警类型排行不变
        statisticsMap = enrichStatisticsSubTaskWithSmallModels(statisticsVO, statisticsMap, false);

        //告警类型
        List<Map> alarmTypeList = workbenchDAO.getAlarmType();

        Map type = typeTagCommon(statisticsMap, alarmTypeList, Constants.TYPE,statisticsVO);
        List<Map> resList = (List<Map>) type.get(Constants.RES_LIST);
        int sum = Integer.parseInt(type.get(Constants.SUM).toString()) ;

        if (sum > 0) {
            Map otherMap = new HashMap();
            otherMap.put(Constants.NUM,sum);
            otherMap.put(Constants.TYPE_VALUE,Constants.AAA_SIX);
            otherMap.put(Constants.TYPE_NAME,Constants.OTHER);
            resList.add(otherMap);
        }
        // 传了分页参数时：保留 data（兼容旧前端）同时带 total，否则分页组件会把 data.length 当总数导致无法进第 2 页
        boolean doPage = statisticsVO != null
                && statisticsVO.getPageNums() != null && statisticsVO.getPageSizes() != null;
        if (doPage) {
            long total = type.get(Constants.TOTAL) != null
                    ? Long.parseLong(type.get(Constants.TOTAL).toString()) : resList.size();
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("code", 200);
            resp.put("msg", "操作成功");
            resp.put("time", System.currentTimeMillis());
            resp.put("data", resList);
            resp.put("total", total);
            return resp;
        }
        return Result.data(resList);
    }

    /**
     * 按年或月查询告警事件类型
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getDevTypeYearMonthApp(StatisticsVO statisticsVO) {
        log.info("查询租户告警类型排行,入参{}",statisticsVO.toString());
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        // TYPE：告警类型排行含场景；SUB_TASK：下游任务 + 小模型，不带场景。
        List<Map> statisticsMap = workbenchDAO.getInitDevTypeYearMonthApp(statisticsVO);
        statisticsMap = enrichStatisticsSubTaskWithSmallModels(statisticsVO, statisticsMap, true);
        log.info("查询租户告警类型排行,结果{}", JSONUtil.toJsonStr(statisticsMap));
        return statisticsMap;
    }


    /**
     * 查询告警事件类型top10(大屏)
     *
     * @param analysisVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getDevTypeYearMonthAppTop(AnalysisVO analysisVO) {
        log.info("查询告警事件类型top10(大屏),入参{}",analysisVO.toString());
        // 全选状态/来源 ≡ 未筛选；SUB_TASK 含不可映射状态时回退 warning_num
        StatDimensionConstants.normalizeScreenAllFilters(analysisVO);
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> statisticsMap = workbenchDAO.getEventAlarmNumTypeIndustry(analysisVO);
        // SUB_TASK：下游 + 小模型剔场景；TYPE 不变
        statisticsMap = enrichAnalysisSubTaskWithSmallModels(analysisVO, statisticsMap, false);
        //告警类型（SUB_TASK 时 SQL 已带 typeName，仅空时回填字典）
        List<Map<String,String>> alarmTypeList = workbenchDAO.getAlarmTypeIndustry();
        Map<String, String> typeMap = alarmTypeList.stream().collect( Collectors.toMap( t -> t.get(Constants.TYPE_VALUE), t -> t.get(Constants.TYPE_NAME), (o, n) -> n, HashMap::new));
        for (Map map : statisticsMap) {
            if (map.get(Constants.TYPE_NAME) == null) {
                map.put(Constants.TYPE_NAME,MapUtil.getStr(typeMap,map.get(Constants.TYPE_VALUE)));
            }
        }
        //大于10  第十位及之后为其他
        if ( statisticsMap.size()>10){
            List<Map> resList = new ArrayList<>();
            int count = 0;
            int sum = 0;
            for (Map map : statisticsMap) {
                count++;
                if (count <= 9) {
                    resList.add(map);
                } else {
                    //其他类型的总数
                    sum += (new BigDecimal(map.get(Constants.NUM).toString())).longValue();
                }
            }
            Map otherMap = new HashMap();
            otherMap.put(Constants.NUM,sum);
            otherMap.put(Constants.TYPE_VALUE,Constants.AAA_SIX);
            otherMap.put(Constants.TYPE_NAME,Constants.OTHER);
            resList.add(otherMap);
            TableDataInfo dataTable = getDataTable(resList);
            dataTable.setTotal(resList.size());
            return dataTable;
        }
        TableDataInfo dataTable = getDataTable(statisticsMap);
        dataTable.setTotal(statisticsMap.size());
        return dataTable;
    }

    /**
     * 查询告警事件类型(大屏)
     *
     * @param analysisVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo getDevTypeYearMonthAppScreen(AnalysisVO analysisVO) {
        log.info("查询告警事件类型(大屏),入参{}",analysisVO.toString());
        // 全选状态/来源 ≡ 未筛选；SUB_TASK 含不可映射状态时回退 warning_num
        StatDimensionConstants.normalizeScreenAllFilters(analysisVO);
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> statisticsMap = workbenchDAO.getEventAlarmNumTypeIndustry(analysisVO);
        // SUB_TASK：下游 + 小模型剔场景；TYPE 告警类型排行不变
        statisticsMap = enrichAnalysisSubTaskWithSmallModels(analysisVO, statisticsMap, false);
        //告警类型（SUB_TASK 时 SQL 已带 typeName=sub_scene_name，仅空时回填字典）
        List<Map<String,String>> alarmTypeList = workbenchDAO.getAlarmTypeIndustry();
        Map<String, String> typeMap = alarmTypeList.stream().collect( Collectors.toMap( t -> t.get(Constants.TYPE_VALUE), t -> t.get(Constants.TYPE_NAME), (o, n) -> n, HashMap::new));
        for (Map map : statisticsMap) {
            if (map.get(Constants.TYPE_NAME) == null) {
                map.put(Constants.TYPE_NAME,MapUtil.getStr(typeMap,map.get(Constants.TYPE_VALUE)));
            }
        }
        statisticsMap = statisticsMap.stream().filter(map -> map.get(Constants.TYPE_NAME) != null).collect(toList());
        TableDataInfo dataTable = getDataTable(statisticsMap);
        dataTable.setTotal(statisticsMap.size());
        return dataTable;
    }

    /**
     * 获取事件统计数量
     *
     * @param analysisVO (create_time 创建时间)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result getEventNumListApp(AnalysisVO analysisVO) {
        log.info("查询租户事件状态排行折线图,入参{}",analysisVO.toString());
        try {
            //处理传入的组织条件
            commonUtils.handleOrgIdList(analysisVO,null);
            List<Map> list = workbenchDAO.getEventNumListApp(analysisVO);
            // SUB_TASK 点进小模型行：下游表无量时回退 TYPE（num_day）
            if (needSubTaskSmallModelDrillDownFallback(analysisVO, list)) {
                String dim = analysisVO.getStatDimension();
                analysisVO.setStatDimension(StatDimensionConstants.TYPE);
                try {
                    PageHelper.clearPage();
                    list = workbenchDAO.getEventNumListApp(analysisVO);
                } finally {
                    analysisVO.setStatDimension(dim);
                }
            }
            return Result.data(list);
        }catch (Exception e){
            log.error("查询租户事件状态排行折线图异常：", e);
            return Result.fail("查询租户事件状态排行折线图异常");
        }
    }


    /**
     * 获取事件统计数量小时/日期折线图(含告警等级对应数量)
     *
     * @param analysisVO (create_time 创建时间)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result getEventNumLevelList(AnalysisVO analysisVO) {
        log.info("查询租户事件状态排行折线图getEventNumLevelList,入参{}",analysisVO.toString());
        try {
            //处理传入的组织条件
            commonUtils.handleOrgIdList(analysisVO,null);
            if (StringUtils.isEmpty(analysisVO.getDateType())){
                analysisVO.setDateType(Constants.ONE_STRING);
            }
            List<Map> list = new ArrayList<>();
            if (Constants.ONE_STRING.equals(analysisVO.getDateType())){
                list = workbenchDAO.getEventNumLevelDayList(analysisVO);
            }else {
                list = workbenchDAO.getEventNumLevelHourList(analysisVO);
            }
            return Result.data(list);
        }catch (Exception e){
            log.error("查询租户事件状态排行折线图getEventNumLevelList异常：", e);
            return Result.fail("查询租户事件状态排行折线图getEventNumLevelList异常");
        }
    }

    /**
     * 获取事件统计数量小时/日期折线图(含告警类型对应数量)
     *
     * @param analysisVO (create_time 创建时间)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result<List<AlarmNumByDateAndTypeVO>> getEventNumTypeList(AnalysisVO analysisVO) {
        log.info("查询租户事件状态排行折线图getEventNumTypeList,入参{}",analysisVO.toString());
        try {
            //处理传入的组织条件
            commonUtils.handleOrgIdList(analysisVO,null);
            if (StringUtils.isEmpty(analysisVO.getDateType())){
                analysisVO.setDateType(Constants.ONE_STRING);
            }
            List<AlarmNumByDateAndTypeVO> list = new ArrayList<>();
            if (Constants.ONE_STRING.equals(analysisVO.getDateType())){
                list = workbenchDAO.getEventNumTypeDayList(analysisVO);
                handleTypeByDateGroup(list);
            }else {
                list = workbenchDAO.getEventNumTypeHourList(analysisVO);
                handleTypeByDateGroup(list);
            }
            return Result.data(list);
        }catch (Exception e){
            log.error("查询租户事件状态排行折线图getEventNumTypeList异常：", e);
            return Result.fail("查询租户事件状态排行折线图getEventNumTypeList异常");
        }
    }

    //根据告警类型分组整合在同一日期
    public void handleTypeByDateGroup(List<AlarmNumByDateAndTypeVO> list){
        if (!CollectionUtils.isEmpty(list)){
            //告警类型
            List<Map<String,String>> alarmTypeList = workbenchDAO.getAlarmTypeIndustry();
            Map<String, String> typeMap = alarmTypeList.stream().collect( Collectors.toMap( t -> t.get(Constants.TYPE_VALUE), t -> t.get(Constants.TYPE_NAME), (o, n) -> n, HashMap::new));
            for (AlarmNumByDateAndTypeVO numByDateAndTypeVO : list) {
                int num = 0;
                if (!CollectionUtils.isEmpty(numByDateAndTypeVO.getNumByTypeList())){
                    for (NumByTypeVo numByTypeVo : numByDateAndTypeVO.getNumByTypeList()) {
                        num = num+numByTypeVo.getNum();
                        numByTypeVo.setTypeName(typeMap.get(numByTypeVo.getTypeId()));
                    }
                }
                numByDateAndTypeVO.setTotalNum(num);
            }
        }
    }


    /**
     * 查询设备未处理最高告警等级getDeviceMaxLevelList
     *
     * @param analysisVO (create_time 创建时间)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result getDeviceMaxLevelList(AnalysisVO analysisVO) {
        log.info("查询设备未处理最高告警等级getDeviceMaxLevelList,入参{}",analysisVO.toString());
        try {
            //处理传入的组织条件
            commonUtils.handleOrgIdList(analysisVO,null);
            List<Map> list = workbenchDAO.getDeviceMaxLevelList(analysisVO);
            return Result.data(list);
        }catch (Exception e){
            log.error("查询设备未处理最高告警等级getDeviceMaxLevelList：", e);
            return Result.fail("查询设备未处理最高告警等级getDeviceMaxLevelList");
        }
    }
    /**
     * 告警统计月份折线图
     *
     * @param analysisVO (create_time 创建时间)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumListAppMonth(AnalysisVO analysisVO) {
        log.info("查询租户事件状态排行月份折线图,入参{}",analysisVO.toString());
        try {
            //处理传入的组织条件
            commonUtils.handleOrgIdList(analysisVO,null);
            List<Map> list = workbenchDAO.getEventNumListAppMonth(analysisVO);
            return Result.data(list);
        }catch (Exception e){
            log.error("查询租户事件状态排行月份折线图异常：", e);
            return Result.fail("查询租户事件状态排行月份折线图异常");
        }
    }

    /**
     * 今日告警等级
     *
     * @param analysisVO  (year 年 month 月 day 日)
     * @return emergency_level 等级
     * @return num 数量
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumByLevel(AnalysisVO analysisVO) {
        List<Map> list = workbenchDAO.getEventNumByLevel(analysisVO);
        // 告警等级字典
        List<Map> emergencyLevelList = workbenchDAO.getEmergencyLevel();
        if(!CollectionUtils.isEmpty(emergencyLevelList)){
            for(Map map : emergencyLevelList){
                map.put(Constants.NUM,0);
                list.forEach(item -> {
                    if(item.get(Constants.EMERGENCY_LEVEL).toString().equals(map.get(Constants.ENUM_CODE))){
                        map.put(Constants.NUM,item.get(Constants.NUM));
                    }
                });
            }
        }
        return Result.data(emergencyLevelList);
    }

    /**
     * 今日事件高发时段
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumByHour(AnalysisVO analysisVO) {
        log.info("查询租户24H分布,入参{}",analysisVO.toString());
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        return Result.data(workbenchDAO.getEventNumByHour(analysisVO));
    }

    /**
     * 今日事件高发时段
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result getAlarmEventTrendNumByHour(AnalysisVO analysisVO) {
        log.info("查询租户24H分布,入参{}",analysisVO.toString());
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        return Result.data(workbenchDAO.getAlarmEventTrendNumByHour(analysisVO));
    }

    /**
     * 24小时告警趋势
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumDetailByHour(AnalysisVO analysisVO) {
        log.info("查询租户24小时告警趋势,入参{}",analysisVO.toString());
        return Result.data(workbenchDAO.getEventNumDetailByHour(analysisVO));
    }

    /**
     * 24h告警压制统计
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumByInterceptHour(AnalysisVO analysisVO) {
        log.info("24h告警压制统计-租户,入参{}",analysisVO.toString());
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        return Result.data(workbenchDAO.getEventNumByInterceptHour(analysisVO));
    }

    /**
     * 24h告警压制统计
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumDetailByInterceptHour(AnalysisVO analysisVO) {
        log.info("24h告警压制统计-租户,入参{}",analysisVO.toString());
        return Result.data(workbenchDAO.getEventNumDetailByInterceptHour(analysisVO));
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumForApp(AnalysisVO analysisVO){
        return Result.data(workbenchDAO.getEventNumForApp(analysisVO));
    }
    /**
     * 算法任务数量
     * @param analysisVO
     * @return AlgorithmStatisticsVO(totalNum:算法任务数量)
     * @return AlgorithmStatisticsVO(enableNum:启用算法任务数量)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id",industryCode="a.industry_code")
    public Result<AlgorithmStatisticsVO> getAlgorithmNum(AnalysisVO analysisVO){
        return Result.data(workbenchDAO.getAlgorithmNum(analysisVO));
    }

    /**
     * 按年或月查询告警标签事件
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getAlarmTagYearMonth(StatisticsVO statisticsVO) {
        List<Map> statisticsMap = workbenchDAO.getAlarmTagYearMonth(statisticsVO);
        if (statisticsVO.getIsTotal()!= null && statisticsVO.getIsTotal() == 1){
            return Result.data(statisticsMap);
        }
        Map type = typeTagCommon(statisticsMap, null, "tag",statisticsVO);
        List<Map> resList = (List<Map>) type.get(Constants.RES_LIST);
        int sum = Integer.parseInt(type.get(Constants.SUM).toString()) ;

        if (sum > 0) {
            Map otherMap = new HashMap();
            otherMap.put(Constants.NUM,sum);
            otherMap.put(Constants.TAG_ID,Constants.AAA_SIX);
            otherMap.put(Constants.TAG_NAME,Constants.OTHER);
            resList.add(otherMap);
        }
        return Result.data(resList);
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public List<StatisticsResp> getAlarmNumByLabel(StatisticsVO statisticsVO) {
        List<StatisticsResp> statisticsMap = workbenchDAO.getAlarmNumByLabel(statisticsVO);
        if(!CollectionUtils.isEmpty(statisticsMap)){
            List<String> grids = statisticsMap.stream().map(s ->s.getGridId()).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(grids)) {
                //网格名称
                List<Map<String, String>> gridAll = workbenchDAO.getGridsByGrid(grids);
                Map<String, String> gridMap = gridAll.stream().collect(
                        Collectors.toMap(
                                t -> String.valueOf(t.get(Constants.GRID_ID)),
                                t -> String.valueOf(t.get(Constants.GRID_NAME)),
                                (o, n) -> n,
                                HashMap::new
                        )
                );
                for (StatisticsResp res : statisticsMap) {
                    String gridName = MapUtil.getStr(gridMap, res.getGridId());
                    if (StringUtils.isNotEmpty(gridName)){
                        res.setGridName(gridName);
                    }
                }
            }

        }
        return statisticsMap;
    }


    /**
     * 查询告警总览
     *
     * @param statisticsVO
     */
    public Result getNumAll(StatisticsVO statisticsVO) {
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        log.info("查询告警总览getNumAll-处理组织筛选返回：{}", JSON.toJSONString(statisticsVO));

        // 处理摄像机标签筛选
        EventNumVO emptyResult = this.processDeviceLabelFilter(statisticsVO);
        if (emptyResult != null) {
            return Result.data(emptyResult);
        }

        StatisticsVO vo = new StatisticsVO();
        BeanUtils.copyProperties(statisticsVO, vo);
        EventNumVO numAll = workbenchDAO.getNumAll(statisticsVO);
        EventNumVO suppress = getSuppressNum(vo);
        numAll.setSuppressNum(suppress.getSuppressNum());
        return Result.data(numAll);
    }

    /**
     * 处理摄像机标签筛选并返回空结果（如果筛选为空）
     * @param statisticsVO 统计VO
     * @return EventNumVO 空结果或null（继续执行）
     */
    private EventNumVO processDeviceLabelFilter(StatisticsVO statisticsVO) {
        //1.8.00 全域2.0 摄像机标签筛选（君南使用）
        if (StringUtils.isNotNull(statisticsVO) && !CollectionUtils.isEmpty(statisticsVO.getDeviceLabelIdList())) {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null == loginUser || null == loginUser.getUser()) {
                throw new CustomException("获取用户信息失败", HttpStatus.BAD_REQUEST);
            }
            statisticsVO.setTenantId(loginUser.getUser().getTenantId());

            List<String> devList = deviceStatisticsDAO.getDeviceCodeByLabelIds(statisticsVO);
            log.info("摄像机标签筛选返回：{}", JSON.toJSONString(devList));
            statisticsVO.setDeviceCodeIndustryList(devList);

            if (CollectionUtils.isEmpty(devList)) {
                log.info("标签筛选结果为空，返回0");
                EventNumVO emptyResult = new EventNumVO();
                emptyResult.setAllNum(Constants.ZERO);
                emptyResult.setProcessNum(Constants.ZERO);
                return emptyResult;
            }
        }
        return null;
    }


    /**
     * 查询告警总览
     *
     * @param statisticsVO 查询参数
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result getAlarmStatisticsProcessingNum(StatisticsVO statisticsVO) {
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        AlarmStatisticsProcessingNumVO numAll = workbenchDAO.getAlarmStatisticsProcessingNum(statisticsVO);
        return Result.data(numAll);
    }


    /**
     * 查询告警总览
     *
     * @param statisticsVO 查询参数
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public List<DeviceAreaDistributionNumVO> getCamaraDeviceAreaDistributionNum(StatisticsVO statisticsVO) {
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        List<DeviceAreaDistributionNumVO> deviceNumAll = workbenchDAO.getCamaraDeviceAreaDistributionNum(statisticsVO);
        StatisticsVO vo = new StatisticsVO();
        List<String> deviceCode = deviceNumAll.stream().map(DeviceAreaDistributionNumVO::getDeviceCode).distinct().collect(Collectors.toList());
        vo.setDeviceCodeList(deviceCode);
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (ObjectUtil.isNotNull(user)){
            String tenantId = user.getTenantId();
            String industryCode = user.getIndustryCode();
            vo.setTenantId(tenantId);
            vo.setIndustryCode(industryCode);
        }
        List<Map> devices = workbenchDAO.getDevices(vo);
        //根据物联设备编码匹配物联设备名称
        for (DeviceAreaDistributionNumVO deviceAreaDistributionNumVO : deviceNumAll) {
            for (Map item : devices) {
                if(deviceAreaDistributionNumVO.getDeviceCode().equals(item.get(Constants.DEVICE_CODE))){
                    deviceAreaDistributionNumVO.setDeviceName(String.valueOf(item.get(Constants.DEVICE_NAME)));
                    break;
                }
            }
        }
        return deviceNumAll;
    }


    /**
     * 查询告警总览
     *
     * @param statisticsVO 查询参数
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<DeviceAreaDistributionNumVO> getIotDeviceAreaDistributionNum(StatisticsVO statisticsVO) {
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        List<DeviceAreaDistributionNumVO> deviceNumAll = workbenchDAO.getIotDeviceAreaDistributionNum(statisticsVO);
        StatisticsVO vo = new StatisticsVO();
        List<String> deviceCode = deviceNumAll.stream().map(DeviceAreaDistributionNumVO::getDeviceCode).distinct().collect(Collectors.toList());
        vo.setDeviceCodeList(deviceCode);
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (ObjectUtil.isNotNull(user)){
            String tenantId = user.getTenantId();
            String industryCode = user.getIndustryCode();
            vo.setTenantId(tenantId);
            vo.setIndustryCode(industryCode);
        }
        List<Map> devices = workbenchDAO.getIotDevices(vo);
        //根据物联设备编码匹配物联设备名称
        for (DeviceAreaDistributionNumVO deviceAreaDistributionNumVO : deviceNumAll) {
            for (Map item : devices) {
                if(deviceAreaDistributionNumVO.getDeviceCode().equals(item.get(Constants.DEVICE_CODE))){
                    deviceAreaDistributionNumVO.setDeviceName(String.valueOf(item.get(Constants.DEVICE_NAME)));
                    break;
                }
            }
        }
        return deviceNumAll;
    }

    /**
     * 查询摄像机告警统计
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public TableDataInfo getDeviceNumAll(StatisticsVO statisticsVO) {
        //分页处理
        if (StringUtils.isNotNull(statisticsVO.getPageNum()) && StringUtils.isNotNull(statisticsVO.getPageSize())){
            statisticsVO.setPageNums(statisticsVO.getPageNum());
            statisticsVO.setPageSizes(statisticsVO.getPageSize());
            statisticsVO.setPageNum(null);
            statisticsVO.setPageSize(null);
        }
        //获取时间范围天数
        Date dateStart = DateUtil.parse(statisticsVO.getQueryStartTime(), Constants.YYYY_MM_DD);
        Date dateEnd = DateUtil.offsetDay(DateUtil.parse(statisticsVO.getQueryEndTime(), Constants.YYYY_MM_DD),1);
        long betweenDay = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        statisticsVO.setDayNum(betweenDay);
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        List<EventNumVO> deviceSuppressNum = getDeviceSuppressNum(statisticsVO);
        List<EventNumVO> deviceNumAll = workbenchDAO.getDeviceNumAll(statisticsVO);
        long total = new PageInfo(deviceNumAll).getTotal();
        StatisticsVO vo = new StatisticsVO();
        List<String> deviceCode = deviceNumAll.stream().map(EventNumVO::getDeviceCode).distinct().collect(Collectors.toList());
        vo.setDeviceCodeList(deviceCode);
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (ObjectUtil.isNotNull(user)){
            String tenantId = user.getTenantId();
            String industryCode = user.getIndustryCode();
            vo.setTenantId(tenantId);
            vo.setIndustryCode(industryCode);
        }
        List<Map> devices = workbenchDAO.getDevices(vo);
        //根据物联设备编码匹配物联设备名称
        for (EventNumVO eventNumVO : deviceNumAll) {
            for (Map item : devices) {
                if(eventNumVO.getDeviceCode().equals(item.get(Constants.DEVICE_CODE))){
                    eventNumVO.setDeviceName(String.valueOf(item.get(Constants.DEVICE_NAME)));
                    break;
                }
            }
        }
        //根据物联设备编码匹配压制数量
        for (EventNumVO eventNumVO : deviceNumAll) {
            for (EventNumVO deviceVo : deviceSuppressNum) {
                if(eventNumVO.getDeviceCode().equals(deviceVo.getDeviceCode())){
                    eventNumVO.setSuppressNum(deviceVo.getSuppressNum());
                    break;
                }else {
                    eventNumVO.setSuppressNum(0);
                }
            }
        }
        if ("desc".equals(statisticsVO.getSortOrder()) && "accuracys".equals(statisticsVO.getSort())) {
            deviceNumAll = deviceNumAll.stream().sorted(Comparator.comparing((EventNumVO e) -> e.getAccuracy() == null ? 0 : 1).thenComparing(EventNumVO::getAccuracys,Comparator.comparingDouble(Double::parseDouble)).reversed().thenComparing(EventNumVO::getDeviceName, Collator.getInstance(Locale.CHINA))).collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(statisticsVO.getSort()) && Constants.SUPPRESS_NUM.equals(statisticsVO.getSort())){
            if ("desc".equals(statisticsVO.getSortOrder())){
                deviceNumAll = deviceNumAll.stream().sorted(Comparator.comparing(EventNumVO::getSuppressNum).reversed()).collect(Collectors.toList());
            }else if ("asc".equals(statisticsVO.getSortOrder())){
                deviceNumAll = deviceNumAll.stream().sorted(Comparator.comparing(EventNumVO::getSuppressNum)).collect(Collectors.toList());
            }
        }
        //分页处理
        if (StringUtils.isNotNull(statisticsVO.getPageNums()) && StringUtils.isNotNull(statisticsVO.getPageSizes())){
            int skipNum = statisticsVO.getPageSizes() * (statisticsVO.getPageNums() - 1);
            deviceNumAll= deviceNumAll.stream().skip(skipNum).limit(statisticsVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(deviceNumAll);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * 查询物联告警统计
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public TableDataInfo getIotDeviceNumAll(StatisticsVO statisticsVO) {
        //分页处理
        if (StringUtils.isNotNull(statisticsVO.getPageNum()) && StringUtils.isNotNull(statisticsVO.getPageSize())){
            statisticsVO.setPageNums(statisticsVO.getPageNum());
            statisticsVO.setPageSizes(statisticsVO.getPageSize());
            statisticsVO.setPageNum(null);
            statisticsVO.setPageSize(null);
        }
        //获取时间范围天数
        Date dateStart = DateUtil.parse(statisticsVO.getQueryStartTime(), Constants.YYYY_MM_DD);
        Date dateEnd = DateUtil.offsetDay(DateUtil.parse(statisticsVO.getQueryEndTime(), Constants.YYYY_MM_DD),1);
        long betweenDay = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        statisticsVO.setDayNum(betweenDay);
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        List<EventNumVO> deviceNumAll = workbenchDAO.getIotDeviceNumAll(statisticsVO);
        long total = new PageInfo(deviceNumAll).getTotal();
        StatisticsVO vo = new StatisticsVO();
        List<String> deviceCode = deviceNumAll.stream().map(EventNumVO::getDeviceCode).distinct().collect(Collectors.toList());
        vo.setDeviceCodeList(deviceCode);
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (ObjectUtil.isNotNull(user)){
            String tenantId = user.getTenantId();
            String industryCode = user.getIndustryCode();
            vo.setTenantId(tenantId);
            vo.setIndustryCode(industryCode);
        }
        List<Map> devices = workbenchDAO.getIotDevices(vo);
        //根据物联设备编码匹配物联设备名称
        for (EventNumVO eventNumVO : deviceNumAll) {
            for (Map item : devices) {
                if(eventNumVO.getDeviceCode().equals(item.get(Constants.DEVICE_CODE))){
                    eventNumVO.setDeviceName(String.valueOf(item.get(Constants.DEVICE_NAME)));
                    break;
                }
            }
        }
        if ("desc".equals(statisticsVO.getSortOrder()) && "accuracys".equals(statisticsVO.getSort())) {
            deviceNumAll = deviceNumAll.stream().sorted(Comparator.comparing((EventNumVO e) -> e.getAccuracy() == null ? 0 : 1).thenComparing(EventNumVO::getAccuracys,Comparator.comparingDouble(Double::parseDouble)).reversed().thenComparing(EventNumVO::getDeviceName, Collator.getInstance(Locale.CHINA))).collect(Collectors.toList());
        }
        //分页处理
        if (StringUtils.isNotNull(statisticsVO.getPageNums()) && StringUtils.isNotNull(statisticsVO.getPageSizes())){
            int skipNum = statisticsVO.getPageSizes() * (statisticsVO.getPageNums() - 1);
            deviceNumAll= deviceNumAll.stream().skip(skipNum).limit(statisticsVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(deviceNumAll);
        dataTable.setTotal(total);
        return dataTable;
    }


    /**
     * 查询设备昼夜数量(物联设备)
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getDeviceDayAndNightNum(StatisticsVO statisticsVO) {
        List<Map> dayAndNightNum = workbenchDAO.getDeviceDayAndNightNum(statisticsVO);
        List<String> deviceCodeList = dayAndNightNum.stream().map(v -> String.valueOf(v.get(Constants.DEVICE_CODE))).collect(toList());
        statisticsVO.setDeviceCodeList(deviceCodeList);
        List<Map> devices = workbenchDAO.getIotDevices(statisticsVO);
        //根据物联设备编码匹配物联设备名称
        for (Map map : dayAndNightNum) {
            for (Map item : devices) {
                if(map.get(Constants.DEVICE_CODE).equals(item.get(Constants.DEVICE_CODE))){
                    map.put(Constants.DEVICE_NAME,String.valueOf(item.get(Constants.DEVICE_NAME)));
                    break;
                }
            }
        }
        return dayAndNightNum;
    }

    /**
     * 查询告警压制数量
     *
     * @param statisticsVO
     */
    public EventNumVO getSuppressNum(StatisticsVO statisticsVO) {
        return  workbenchDAO.getSuppressNum(statisticsVO);
    }

    /**
     * 查询告警压制数量
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<EventNumVO> getDeviceSuppressNum(StatisticsVO statisticsVO) {
        return  workbenchDAO.getDeviceSuppressNum(statisticsVO);
    }

    /**
     * 查询告警压制数量
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<EventNumVO> getIotDeviceSuppressNum(StatisticsVO statisticsVO) {
        return  workbenchDAO.getIotDeviceSuppressNum(statisticsVO);
    }


    /**
     * 获取压制事件统计数量
     *
     * @param analysisVO (create_time 创建时间)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventInterceptNumApp(AnalysisVO analysisVO) {
        log.info("查询租户压制事件状态排行折线图,入参{}",analysisVO.toString());
        try {
            //处理传入的组织条件
            commonUtils.handleOrgIdList(analysisVO,null);
            List<Map> list = workbenchDAO.getEventInterceptNumApp(analysisVO);
            return Result.data(list);
        }catch (Exception e){
            log.error("查询租户压制事件状态排行折线图异常：", e);
            return Result.fail("查询租户压制事件状态排行折线图异常");
        }
    }

    /**
     * 获取压制事件统计数量按照来源类型查询
     *
     * @param analysisVO (create_time 创建时间)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventDetailInterceptNumApp(AnalysisVO analysisVO) {
        log.info("查询租户压制事件状态排行折线图,入参{}",analysisVO.toString());
        try {
            List<Map> list = workbenchDAO.getEventDetailInterceptNumApp(analysisVO);
            return Result.data(list);
        }catch (Exception e){
            log.error("查询租户压制事件状态排行折线图异常：", e);
            return Result.fail("查询租户压制事件状态排行折线图异常");
        }
    }

    /**
     * 查询告警准确率
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEvenAccuracy(StatisticsVO statisticsVO) {
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        EventNumVO evenAccuracy = workbenchDAO.getEvenAccuracy(statisticsVO);
        return Result.data(evenAccuracy);
    }


    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEvenAreaAccuracy(AnalysisVO analysisVO){
        if (analysisVO.getAreaBoxNum() == null) {
            analysisVO.setAreaBoxNum(0);
        }
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> eventNumByArea = workbenchDAO.getEvenAreaAccuracy(analysisVO);
        if (CollectionUtils.isEmpty(eventNumByArea)) {
            return eventNumByArea;
        }
        this.areaCodeHandle(eventNumByArea,analysisVO);
        return eventNumByArea;
    }


    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public TableDataInfo getEvenTypeAccuracy(AnalysisVO analysisVO){
        // SUB_TASK 需 enrich 后再分页，避免 PageHelper 截断 SQL；清空 pageNum 防止拦截器误分页
        PageHelper.clearPage();
        Integer pageNum = analysisVO.getPageNum();
        Integer pageSize = analysisVO.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            analysisVO.setPageNums(pageNum);
            analysisVO.setPageSizes(pageSize);
            analysisVO.setPageNum(null);
            analysisVO.setPageSize(null);
        } else {
            pageNum = analysisVO.getPageNums();
            pageSize = analysisVO.getPageSizes();
        }

        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        // TYPE：告警类型准确率含场景；SUB_TASK：下游任务准确率 = 下游 + 小模型，不带场景。
        List<Map> eventNumByType = workbenchDAO.getEvenTypeAccuracy(analysisVO);
        eventNumByType = enrichAnalysisSubTaskWithSmallModels(analysisVO, eventNumByType, true);
        if (CollectionUtils.isEmpty(eventNumByType)) {
            TableDataInfo empty = getDataTable(Collections.emptyList());
            empty.setTotal(0);
            return empty;
        }
        int total = eventNumByType.size();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize) && pageNum > 0 && pageSize > 0) {
            int skipNum = pageSize * (pageNum - 1);
            eventNumByType = eventNumByType.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(eventNumByType);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * StatisticsVO 版：SUB_TASK 排行并入小模型并剔除场景；TYPE 原样返回。
     *
     * @param useAppSql true=走 getInitDevTypeYearMonthApp（含 warningTypeList）；
     *                  false=走 getInitDevTypeYearMonth（无类型列表过滤，Java 侧剔场景）
     */
    private List<Map> enrichStatisticsSubTaskWithSmallModels(StatisticsVO statisticsVO, List<Map> subTaskRows,
                                                            boolean useAppSql) {
        if (statisticsVO == null || !StatDimensionConstants.isSubTask(statisticsVO.getStatDimension())) {
            return subTaskRows;
        }
        Set<String> sceneIds = loadSceneIdSet();
        Set<String> downstreamIds = loadDownstreamAlarmTypeIdSet();
        // 下游表结果：纯场景剔除，与场景 type_value 重叠的下游码保留
        List<Map> merged = filterPureSceneRankRows(subTaskRows, sceneIds, downstreamIds);
        // TYPE 侧只并入纯小模型（非场景且非下游），避免与下游表重复
        List<String> smallModelTypes = loadSmallModelAlarmTypeValues(sceneIds, downstreamIds);
        // 有 warningTypeList 时只并入交集，避免把未选中的小模型并回排行
        smallModelTypes = intersectWithCodeFilter(smallModelTypes, statisticsVO.getWarningTypeList());
        if (CollectionUtils.isEmpty(smallModelTypes) && useAppSql) {
            merged = filterRankRowsBySelectedCodes(merged, statisticsVO.getWarningTypeList(), null);
            sortCustomerRankRows(merged, false);
            return merged;
        }
        String dim = statisticsVO.getStatDimension();
        List<String> prevTypes = statisticsVO.getWarningTypeList();
        statisticsVO.setStatDimension(StatDimensionConstants.TYPE);
        if (useAppSql) {
            statisticsVO.setWarningTypeList(smallModelTypes);
        }
        try {
            PageHelper.clearPage();
            List<Map> smallRows = useAppSql
                    ? workbenchDAO.getInitDevTypeYearMonthApp(statisticsVO)
                    : workbenchDAO.getInitDevTypeYearMonth(statisticsVO);
            merged = mergeRankRowsById(merged,
                    filterPureSceneRankRows(smallRows, sceneIds, downstreamIds));
        } finally {
            statisticsVO.setStatDimension(dim);
            statisticsVO.setWarningTypeList(prevTypes);
        }
        merged = filterRankRowsBySelectedCodes(merged, prevTypes, null);
        sortCustomerRankRows(merged, false);
        return merged;
    }

    /**
     * AnalysisVO 版：SUB_TASK 排行/准确率并入小模型并剔除场景；TYPE 原样返回。
     *
     * @param accuracyMode true=按准确率排序；false=按 num 排序
     */
    private List<Map> enrichAnalysisSubTaskWithSmallModels(AnalysisVO analysisVO, List<Map> subTaskRows,
                                                          boolean accuracyMode) {
        if (analysisVO == null || !StatDimensionConstants.isSubTask(analysisVO.getStatDimension())) {
            return subTaskRows;
        }
        Set<String> sceneIds = loadSceneIdSet();
        Set<String> downstreamIds = loadDownstreamAlarmTypeIdSet();
        List<Map> merged = filterPureSceneRankRows(subTaskRows, sceneIds, downstreamIds);
        List<String> smallModelTypes = loadSmallModelAlarmTypeValues(sceneIds, downstreamIds);
        // 有筛选时：小模型只并入与筛选码的交集，避免把未选中的小模型并回排行
        smallModelTypes = intersectWithCodeFilter(smallModelTypes, analysisVO.getSubSceneIdList());
        smallModelTypes = intersectWithCodeFilter(smallModelTypes, analysisVO.getWarningTypeList());
        if (CollectionUtils.isEmpty(smallModelTypes)) {
            merged = filterRankRowsBySelectedCodes(merged,
                    analysisVO.getWarningTypeList(), analysisVO.getSubSceneIdList());
            sortCustomerRankRows(merged, accuracyMode);
            return merged;
        }
        String dim = analysisVO.getStatDimension();
        List<String> prevTypes = analysisVO.getWarningTypeList();
        List<String> prevSubScenes = analysisVO.getSubSceneIdList();
        analysisVO.setStatDimension(StatDimensionConstants.TYPE);
        analysisVO.setWarningTypeList(smallModelTypes);
        // TYPE 表无 sub_scene_code；清空以免误伤（当前 TYPE SQL 虽未用，保持语义干净）
        analysisVO.setSubSceneIdList(null);
        try {
            PageHelper.clearPage();
            List<Map> smallRows = accuracyMode
                    ? workbenchDAO.getEvenTypeAccuracy(analysisVO)
                    : workbenchDAO.getEventAlarmNumTypeIndustry(analysisVO);
            merged = mergeRankRowsById(merged,
                    filterPureSceneRankRows(smallRows, sceneIds, downstreamIds));
        } finally {
            analysisVO.setStatDimension(dim);
            analysisVO.setWarningTypeList(prevTypes);
            analysisVO.setSubSceneIdList(prevSubScenes);
        }
        // 排行 typeId 只保留用户勾选的告警类型/下游任务，避免 warning_type_id 父筛带出未选子码
        merged = filterRankRowsBySelectedCodes(merged, prevTypes, prevSubScenes);
        sortCustomerRankRows(merged, accuracyMode);
        return merged;
    }

    /**
     * 筛选生效时将候选编码限制在 filterCodes 内；无筛选则原样返回。
     */
    private static List<String> intersectWithCodeFilter(List<String> candidates, List<String> filterCodes) {
        if (CollectionUtils.isEmpty(candidates) || CollectionUtils.isEmpty(filterCodes)) {
            return candidates;
        }
        Set<String> filter = new HashSet<>(filterCodes);
        return candidates.stream().filter(filter::contains).collect(Collectors.toList());
    }

    /**
     * 有 warningTypeList / subSceneIdList 时，排行只保留勾选编码对应行（二者并集）。
     */
    private static List<Map> filterRankRowsBySelectedCodes(List<Map> rows, List<String> warningTypeList,
                                                           List<String> subSceneIdList) {
        boolean hasType = !CollectionUtils.isEmpty(warningTypeList);
        boolean hasSub = !CollectionUtils.isEmpty(subSceneIdList);
        if (!hasType && !hasSub) {
            return rows;
        }
        Set<String> allowed = new HashSet<>();
        if (hasType) {
            allowed.addAll(warningTypeList);
        }
        if (hasSub) {
            allowed.addAll(subSceneIdList);
        }
        if (CollectionUtils.isEmpty(rows)) {
            return new ArrayList<>();
        }
        List<Map> kept = new ArrayList<>();
        for (Map row : rows) {
            String id = rankRowId(row);
            if (StringUtils.isNotEmpty(id) && allowed.contains(id)) {
                kept.add(row);
            }
        }
        return kept;
    }

    private Set<String> loadSceneIdSet() {
        List<String> sceneIdList = workbenchDAO.getSceneIds();
        if (CollectionUtils.isEmpty(sceneIdList)) {
            return Collections.emptySet();
        }
        return sceneIdList.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<String> loadDownstreamAlarmTypeIdSet() {
        List<String> ids = workbenchDAO.getDownstreamAlarmTypeIds();
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        return ids.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * 小模型 = 告警类型中既非纯场景、也非下游任务编码（下游由 sub_scene 表提供）。
     */
    private List<String> loadSmallModelAlarmTypeValues(Set<String> sceneIds, Set<String> downstreamIds) {
        List<Map<String, String>> alarmTypeList = workbenchDAO.getAlarmTypeIndustry();
        if (CollectionUtils.isEmpty(alarmTypeList)) {
            return Collections.emptyList();
        }
        Set<String> scenes = sceneIds == null ? Collections.emptySet() : sceneIds;
        Set<String> downstream = downstreamIds == null ? Collections.emptySet() : downstreamIds;
        return alarmTypeList.stream()
                .map(item -> item.get(Constants.TYPE_VALUE))
                .filter(StringUtils::isNotEmpty)
                .filter(typeId -> !downstream.contains(typeId))
                .filter(typeId -> !scenes.contains(typeId))
                .collect(Collectors.toList());
    }

    /**
     * 剔除纯场景；编码若同时是场景 type_value 与下游 alarm_type（如 9212102），按下游保留。
     */
    private static boolean keepSubTaskCustomerRankId(String typeId, Set<String> sceneIds,
                                                     Set<String> downstreamIds) {
        if (StringUtils.isEmpty(typeId)) {
            return false;
        }
        if (downstreamIds != null && downstreamIds.contains(typeId)) {
            return true;
        }
        return sceneIds == null || !sceneIds.contains(typeId);
    }

    private static List<Map> filterPureSceneRankRows(List<Map> rows, Set<String> sceneIds,
                                                     Set<String> downstreamIds) {
        if (CollectionUtils.isEmpty(rows)) {
            return new ArrayList<>();
        }
        List<Map> kept = new ArrayList<>();
        for (Map row : rows) {
            String id = rankRowId(row);
            if (keepSubTaskCustomerRankId(id, sceneIds, downstreamIds)) {
                kept.add(row);
            }
        }
        return kept;
    }

    private static List<Map> mergeRankRowsById(List<Map> primary, List<Map> secondary) {
        LinkedHashMap<String, Map> byId = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(primary)) {
            for (Map row : primary) {
                String id = rankRowId(row);
                if (StringUtils.isNotEmpty(id)) {
                    byId.putIfAbsent(id, row);
                }
            }
        }
        if (!CollectionUtils.isEmpty(secondary)) {
            for (Map row : secondary) {
                String id = rankRowId(row);
                if (StringUtils.isNotEmpty(id)) {
                    byId.putIfAbsent(id, row);
                }
            }
        }
        return new ArrayList<>(byId.values());
    }

    private static String rankRowId(Map row) {
        if (row == null) {
            return null;
        }
        Object id = row.get(Constants.TYPE_VALUE);
        if (id == null) {
            id = row.get("warningTypeId");
        }
        return id == null ? null : String.valueOf(id);
    }

    private static void sortCustomerRankRows(List<Map> rows, boolean accuracyMode) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        if (accuracyMode) {
            rows.sort(Comparator
                    .comparing((Map m) -> m.get("accuracy") == null ? 1 : 0)
                    .thenComparing((Map m) -> toSortDouble(m.get("accuracys")), Comparator.reverseOrder())
                    .thenComparing(m -> String.valueOf(m.getOrDefault("typeName", "")),
                            Collator.getInstance(Locale.CHINA)));
        } else {
            rows.sort(Comparator
                    .comparing((Map m) -> toSortLong(m.get(Constants.NUM)), Comparator.reverseOrder())
                    .thenComparing(m -> String.valueOf(rankRowId(m)), Comparator.reverseOrder()));
        }
    }

    private static long toSortLong(Object v) {
        if (v == null) {
            return 0L;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        try {
            return new BigDecimal(String.valueOf(v)).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    private static double toSortDouble(Object v) {
        if (v == null) {
            return 0D;
        }
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(v));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * SUB_TASK 折线：有单码过滤且下游表汇总为 0 时，视为小模型点进，需回退 TYPE。
     */
    private boolean needSubTaskSmallModelDrillDownFallback(AnalysisVO analysisVO, List<Map> trendRows) {
        if (analysisVO == null
                || !StatDimensionConstants.isSubTask(analysisVO.getStatDimension())
                || StringUtils.isEmpty(analysisVO.getWarningTypeId())) {
            return false;
        }
        if (CollectionUtils.isEmpty(trendRows)) {
            return true;
        }
        long sum = 0L;
        for (Map row : trendRows) {
            sum += toSortLong(row.get(Constants.NUM));
        }
        return sum == 0L;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEvenSourceAccuracy(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> eventNumBySource = workbenchDAO.getEvenSourceAccuracy(analysisVO);
        if (CollectionUtils.isEmpty(eventNumBySource)) {
            return eventNumBySource;
        }
        return eventNumBySource;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public List<Map> getEvenGridCompletion(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> gridCompletion = workbenchDAO.getEvenGridCompletion(analysisVO);
        if (CollectionUtils.isEmpty(gridCompletion)) {
            return gridCompletion;
        }
        //网格名称
        List<Map<String, String>> grids = workbenchDAO.getGrids();
        Map<String, String> gridMap = grids.stream().collect(
                Collectors.toMap(
                        t -> String.valueOf(t.get(Constants.GRID_ID)),
                        t -> String.valueOf(t.get(Constants.GRID_NAME)),
                        (o, n) -> n,
                        HashMap::new
                )
        );
        //查询最新网格名称
        for (Map map : gridCompletion) {
            String gridName = MapUtil.getStr(gridMap, map.get(Constants.GRID_ID));
            if (StringUtils.isNotEmpty(gridName)){
                map.put(Constants.GRID_NAME,gridName);
            }
            //处理平均处理时长
            String avgTime ="-";
            if (!"0".equals(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM)))){
                avgTime = handleAvgTime(String.valueOf(map.get(Constants.AVG_TIME)));
            }
            map.put(Constants.AVG_TIME_DAY,avgTime);
        }
        return gridCompletion;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public TableDataInfo getEvenGridCompletionApp(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> gridCompletion = workbenchDAO.getEvenGridCompletion(analysisVO);
        //网格名称
        List<Map<String, String>> grids = workbenchDAO.getGrids();
        Map<String, String> gridMap = grids.stream().collect(
                Collectors.toMap(
                        t -> String.valueOf(t.get(Constants.GRID_ID)),
                        t -> String.valueOf(t.get(Constants.GRID_NAME)),
                        (o, n) -> n,
                        HashMap::new
                )
        );
        //查询最新网格名称
        for (Map map : gridCompletion) {
            String gridName = MapUtil.getStr(gridMap, map.get(Constants.GRID_ID));
            if (StringUtils.isNotEmpty(gridName)){
                map.put(Constants.GRID_NAME,gridName);
            }
            //处理平均处理时长
            String avgTime ="-";
            if (!"0".equals(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM)))){
                avgTime = handleAvgTime(String.valueOf(map.get(Constants.AVG_TIME)));
            }
            map.put(Constants.AVG_TIME_DAY,avgTime);
        }
        gridCompletion = gridCompletion.stream().sorted(Comparator.comparing((Map m) ->Double.parseDouble(String.valueOf(m.get("completionRates")))).reversed().thenComparing((Map m) ->Integer.parseInt(String.valueOf(m.get(Constants.GRID_LEVEL)))).thenComparing((Map m) -> m.get(Constants.GRID_NAME),Collator.getInstance(Locale.CHINA))).collect(Collectors.toList());
        long total = gridCompletion.size();
        //分页处理
        if (StringUtils.isNotNull(analysisVO.getPageNums()) && StringUtils.isNotNull(analysisVO.getPageSizes())){
            int skipNum = analysisVO.getPageSizes() * (analysisVO.getPageNums() - 1);
            gridCompletion= gridCompletion.stream().skip(skipNum).limit(analysisVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(gridCompletion);
        dataTable.setTotal(total);
        return dataTable;
    }


    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEvenAreaCompletion(AnalysisVO analysisVO){
        if (analysisVO.getAreaBoxNum() == null) {
            analysisVO.setAreaBoxNum(0);
        }
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> evenAreaCompletion = workbenchDAO.getEvenAreaCompletion(analysisVO);
        if (CollectionUtils.isEmpty(evenAreaCompletion)) {
            return evenAreaCompletion;
        }
        for (int i = 0; i < evenAreaCompletion.size(); i++) {
            String avgTime ="-";
            if (!"0".equals(String.valueOf(evenAreaCompletion.get(i).get(Constants.ORDER_COMPLETE_NUM)))){
                avgTime = handleAvgTime(String.valueOf(evenAreaCompletion.get(i).get(Constants.AVG_TIME)));
            }
                evenAreaCompletion.get(i).put(Constants.AVG_TIME_DAY,avgTime);
        }
        this.areaCodeHandle(evenAreaCompletion,analysisVO);
        return evenAreaCompletion;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public List<Map> getEvenAreaCompletionIndustry(AnalysisVO analysisVO){
        if (analysisVO.getAreaBoxNum() == null) {
            analysisVO.setAreaBoxNum(0);
        }
        StatDimensionConstants.normalizeScreenAllFilters(analysisVO);
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        List<Map> evenAreaCompletion = workbenchDAO.getEvenAreaCompletionIndustry(analysisVO);
        if (CollectionUtils.isEmpty(evenAreaCompletion)) {
            return evenAreaCompletion;
        }
        this.areaCodeHandle(evenAreaCompletion,analysisVO);
        return evenAreaCompletion;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEventDetailBySource(AnalysisVO analysisVO){
        List<Map> eventDetailBySource = workbenchDAO.getEventDetailBySource(analysisVO);
        if (CollectionUtils.isEmpty(eventDetailBySource)) {
            return eventDetailBySource;
        }
        for (int i = 0; i < eventDetailBySource.size(); i++) {
            String avgTime ="-";
            if (!"0".equals(String.valueOf(eventDetailBySource.get(i).get(Constants.ORDER_COMPLETE_NUM)))){
                avgTime = handleAvgTime(String.valueOf(eventDetailBySource.get(i).get(Constants.AVG_TIME)));
            }
            eventDetailBySource.get(i).put(Constants.AVG_TIME_DAY,avgTime);
        }
        return eventDetailBySource;
    }


    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEventDetailByType(AnalysisVO analysisVO){
        List<Map> eventDetailByType = workbenchDAO.getEventDetailByType(analysisVO);
        // SUB_TASK 点进小模型行：下游表无数据时回退 TYPE（num_day）
        if (CollectionUtils.isEmpty(eventDetailByType)
                && StatDimensionConstants.isSubTask(analysisVO.getStatDimension())
                && StringUtils.isNotEmpty(analysisVO.getWarningTypeId())) {
            String dim = analysisVO.getStatDimension();
            analysisVO.setStatDimension(StatDimensionConstants.TYPE);
            try {
                PageHelper.clearPage();
                eventDetailByType = workbenchDAO.getEventDetailByType(analysisVO);
            } finally {
                analysisVO.setStatDimension(dim);
            }
        }
        if (CollectionUtils.isEmpty(eventDetailByType)) {
            return eventDetailByType;
        }
        for (int i = 0; i < eventDetailByType.size(); i++) {
            String avgTime ="-";
            if (!"0".equals(String.valueOf(eventDetailByType.get(i).get(Constants.ORDER_COMPLETE_NUM)))){
                avgTime = handleAvgTime(String.valueOf(eventDetailByType.get(i).get(Constants.AVG_TIME)));
            }
            eventDetailByType.get(i).put(Constants.AVG_TIME_DAY,avgTime);
        }
        return eventDetailByType;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getEvenAreaCompletionPc(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        if (!CollectionUtils.isEmpty(analysisVO.getAreaCodeLists())){
            analysisVO.setAreaList("'"+String.join("','",analysisVO.getAreaCodeLists())+"'");
        }
        List<Map> evenAreaCompletion = workbenchDAO.getEvenAreaCompletionPc(analysisVO);
        if (CollectionUtils.isEmpty(evenAreaCompletion)) {
            return evenAreaCompletion;
        }
        List<String> areaList = evenAreaCompletion.stream().map(t -> String.valueOf(t.get("areaId"))).collect( Collectors.toList());
        List<Map<String, String>>alarmByCode = workbenchDAO.getAlarmByCodes(areaList);
        Map<String, String> codeList = alarmByCode.stream().collect( Collectors.toMap( t -> t.get(Constants.AREA_CODE), t -> t.get(Constants.AREA_NAME), (o, n) -> n, HashMap::new));
        for (Map<String,String> map : evenAreaCompletion) {
            map.put(Constants.REGION_NAME,codeList.get(map.get("areaId")));
        }
        return evenAreaCompletion;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public TableDataInfo getEvenDeptCompletion(AnalysisVO analysisVO){
        //手动分页
        if (StringUtils.isNotNull(analysisVO.getPageNum()) && StringUtils.isNotNull(analysisVO.getPageSize())){
            analysisVO.setPageNums(analysisVO.getPageNum());
            analysisVO.setPageSizes(analysisVO.getPageSize());
            analysisVO.setPageNum(null);
            analysisVO.setPageSize(null);
        }
        //处理传入的组织条件
        commonUtils.handleOrgIdList(analysisVO,null);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        analysisVO.setTenantId(tenantId);
        analysisVO.setIndustryCode(industryCode);
        //雷达设备的省市区权限
        List<SysDeptDeviceVO> radars = new ArrayList<>();
        //卫星设备的省市区权限
        List<SysDeptDeviceVO> satellites = new ArrayList<>();
        //查询用户下拥有的雷达,卫星权限
        handleDeptOrgIdList(analysisVO,radars,satellites);
        //总数据集
        List<Map> list = new ArrayList<>();
        //异步处理
        List<Integer> paramList = Arrays.asList(1,2,3,4);
        ThreadParallelUtil.run(paramList, o->{
            this.completMethodAll(analysisVO,o,satellites,radars);
            return null;
        }, ThreadParallelUtil.ConfigFactory.EVENT_EXPORT_CODE);
        if (!CollectionUtils.isEmpty(analysisVO.getCompleteList1())){
            list.addAll(analysisVO.getCompleteList1());
        }
        if (!CollectionUtils.isEmpty(analysisVO.getCompleteList2())){
            list.addAll(analysisVO.getCompleteList2());
        }
        if (!CollectionUtils.isEmpty(analysisVO.getCompleteList3())){
            list.addAll(analysisVO.getCompleteList3());
        }
        if (!CollectionUtils.isEmpty(analysisVO.getCompleteList4())){
            list.addAll(analysisVO.getCompleteList4());
        }
        //分页前总数
        int total = 0;
        //根据deptId聚合
        if (!CollectionUtils.isEmpty(list)){
            list = list.stream().filter(map ->
                    null != map.get(Constants.DEPT_ID) && !"".equals(String.valueOf(map.get(Constants.DEPT_ID)))
            ).collect(Collectors.toList());

            List<Map> completeList = new ArrayList<>();
            list.stream()
                    .collect(Collectors.groupingBy(
                            item -> item.get(Constants.DEPT_ID),Collectors.toList()
                    ))
                    .forEach((id, transfer) -> {
                        Map result = transfer.stream()
                                .reduce((a, b) -> {
                                    Map newMap = new HashMap<>(a);
                                    newMap.put(Constants.ALL_NUM, Math.round(Double.parseDouble(String.valueOf(a.get(Constants.ALL_NUM))) + Double.parseDouble(String.valueOf(b.get(Constants.ALL_NUM)))));
                                    newMap.put(Constants.JUDGR_NUM,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.JUDGR_NUM))) + Double.parseDouble(String.valueOf(b.get(Constants.JUDGR_NUM)))));
                                    newMap.put(Constants.DISPATCH_NUM,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.DISPATCH_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.DISPATCH_NUM)))));
                                    newMap.put(Constants.HANDLE_NUM, Math.round(Double.parseDouble(String.valueOf(a.get(Constants.HANDLE_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.HANDLE_NUM)))));
                                    newMap.put(Constants.APPROVE_NUM, Math.round(Double.parseDouble(String.valueOf(a.get(Constants.APPROVE_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.APPROVE_NUM)))));
                                    newMap.put(Constants.ORDER_COMPLETE_NUM,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.ORDER_COMPLETE_NUM))) + Double.parseDouble(String.valueOf(b.get(Constants.ORDER_COMPLETE_NUM)))));
                                    newMap.put(Constants.REAL_NUM, Math.round(Double.parseDouble(String.valueOf(a.get(Constants.REAL_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.REAL_NUM)))));
                                    newMap.put(Constants.FALSE_NUM,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.FALSE_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.FALSE_NUM)))));
                                    newMap.put(Constants.REPEAT_COMPLETE_NUM,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.REPEAT_COMPLETE_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.REPEAT_COMPLETE_NUM)))));
                                    newMap.put(Constants.PROCESS_NUM,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.PROCESS_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.PROCESS_NUM)))));
                                    newMap.put(Constants.ACCEPTANCE_NUM,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.ACCEPTANCE_NUM))) +  Double.parseDouble(String.valueOf(b.get(Constants.ACCEPTANCE_NUM)))));
                                    newMap.put(Constants.DURATION,Math.round(Double.parseDouble(String.valueOf(a.get(Constants.DURATION))) +  Double.parseDouble(String.valueOf(b.get(Constants.DURATION)))));
                                    return newMap;
                                })
                                .orElse(new HashMap<>());

                        completeList.add(result);
                    });
            list = completeList;

            list = list.stream().filter(v -> !"0".equals(String.valueOf(v.get(Constants.ALL_NUM)))).collect(toList());
        //查询组织 回显组织名称 处理计算值
        List<Map<String, String>> maps = workbenchDAO.queryDepts(tenantId, industryCode);
        Map<String, String> deptList = maps.stream().collect( Collectors.toMap( t -> t.get(Constants.DEPT_ID), t -> t.get("deptName"), (o, n) -> n, HashMap::new));
        for (Map map : list) {
            map.put("completionRate",new BigDecimal(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM))).divide(new BigDecimal(String.valueOf(map.get(Constants.ALL_NUM))), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(0,BigDecimal.ROUND_HALF_UP));
            map.put("completionRates",new BigDecimal(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM))).divide(new BigDecimal(String.valueOf(map.get(Constants.ALL_NUM))), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(4,BigDecimal.ROUND_HALF_UP));
           if (!"0".equals(String.valueOf(map.get(Constants.REAL_NUM))) || !"0".equals(String.valueOf(map.get(Constants.REPEAT_COMPLETE_NUM))) || !"0".equals(String.valueOf(map.get(Constants.FALSE_NUM)))){
               map.put("accuracy",(new BigDecimal(String.valueOf(map.get(Constants.REAL_NUM))).add(new BigDecimal(String.valueOf(map.get(Constants.REPEAT_COMPLETE_NUM))))).divide(new BigDecimal(String.valueOf(map.get(Constants.REAL_NUM))).add(new BigDecimal(String.valueOf(map.get(Constants.REPEAT_COMPLETE_NUM)))).add(new BigDecimal(String.valueOf(map.get(Constants.FALSE_NUM)))), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(0,BigDecimal.ROUND_HALF_UP));
           }
           if (null==map.get(Constants.ORDER_COMPLETE_NUM) || "0".equals(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM)))){
               map.put("avgTime","0");
               map.put("avgTimes","0");
            }else {
               map.put("avgTime",new BigDecimal(String.valueOf(map.get(Constants.DURATION))).divide(new BigDecimal(60), 4, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM))), 4, BigDecimal.ROUND_HALF_UP).setScale(0,BigDecimal.ROUND_HALF_UP));
               map.put("avgTimes",new BigDecimal(String.valueOf(map.get(Constants.DURATION))).divide(new BigDecimal(60), 4, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM))), 4, BigDecimal.ROUND_HALF_UP).setScale(4,BigDecimal.ROUND_HALF_UP));
           }
              map.put("deptName",deptList.get(map.get(Constants.DEPT_ID)));
            //处理平均处理时长
            String avgTime ="-";
            if (!"0".equals(String.valueOf(map.get(Constants.ORDER_COMPLETE_NUM)))){
                avgTime = handleAvgTime(String.valueOf(map.get(Constants.AVG_TIME)));
            }
            map.put(Constants.AVG_TIME_DAY,avgTime);
        }
        //排序处理
        if (StringUtils.isNotEmpty(analysisVO.getSort()) && StringUtils.isNotEmpty(analysisVO.getSortOrder())){
            if ("asc".equals(analysisVO.getSortOrder())){
                list = list.stream().sorted(Comparator.comparing((Map m) ->Double.parseDouble(String.valueOf(m.get(analysisVO.getSort())))).thenComparing(Comparator.comparing((Map m)->String.valueOf(m.get(Constants.DEPT_ID))))).collect(Collectors.toList());
            } else if ("desc".equals(analysisVO.getSortOrder())){
                list = list.stream().sorted(Comparator.comparing((Map m) ->Double.parseDouble(String.valueOf(m.get(analysisVO.getSort())))).reversed().thenComparing(Comparator.comparing((Map m)->String.valueOf(m.get(Constants.DEPT_ID))))).collect(Collectors.toList());
            }
        }else {
            list = list.stream().sorted(Comparator.comparing((Map m) ->String.valueOf(m.get(Constants.DEPT_ID)))).collect(Collectors.toList());
        }
             total = list.size();
            //分页处理
        if (StringUtils.isNotNull(analysisVO.getPageNums()) && StringUtils.isNotNull(analysisVO.getPageSizes())){
            int skipNum = analysisVO.getPageSizes() * (analysisVO.getPageNums() - 1);
            list= list.stream().skip(skipNum).limit(analysisVO.getPageSizes()).collect(Collectors.toList());
        }
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(total);
        return dataTable;
    }

    private void completMethodAll(AnalysisVO analysisVO,Integer num,List<SysDeptDeviceVO> satellites,List<SysDeptDeviceVO> radars){
        switch (num){
            case 1:
                //1.添加AI,一键,物联,摄像机告警数据集
                List<Map> eventNumByOrgan = workbenchDAO.getEvenDeptCompletion(analysisVO);
                if(!CollectionUtils.isEmpty(eventNumByOrgan)){
                    analysisVO.setCompleteList1(eventNumByOrgan);
                }
                break;
            case 2:
                //2.添加人工告警数据集
                List<Map> completionManual = workbenchDAO.getEvenDeptCompletionManual(analysisVO);
                if(!CollectionUtils.isEmpty(completionManual)){
                    SysUser sysUser = new SysUser();
                    //查询组织id 通过findName回显组织id
                    List<Map<String, String>> maps = workbenchDAO.queryDeptIds(sysUser);
                    Map<String, String> deptIdList = maps.stream().collect( Collectors.toMap( t -> t.get("userName"), t -> t.get(Constants.DEPT_ID), (o, n) -> n, HashMap::new));
                    for (Map map : completionManual) {
                        map.put(Constants.DEPT_ID,deptIdList.get(map.get("findName")));
                    }
                    if (!CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
                        completionManual = completionManual.stream().filter(map ->
                                analysisVO.getOrgIdList().contains(String.valueOf(map.get(Constants.DEPT_ID)))
                        ).collect(Collectors.toList());
                    }
                    if (StringUtils.isNotEmpty(analysisVO.getDeptId())){
                        completionManual = completionManual.stream().filter(map ->
                                analysisVO.getDeptId().equals(String.valueOf(map.get(Constants.DEPT_ID)))
                        ).collect(Collectors.toList());
                    }
                    if(!CollectionUtils.isEmpty(completionManual)){
                        analysisVO.setCompleteList2(completionManual);
                    }
                }
                break;
            case 3:
                //3.添加卫星数据集
                if(!CollectionUtils.isEmpty(satellites)){
                    List<Map> list3 = new ArrayList<>();
                    ThreadParallelUtil.run(satellites, f->{
                        f.setCountyIds(Arrays.asList(f.getCountyId().split(",")));
                        List<Map> satellite1 = workbenchDAO.getEvenDeptCompletionSatellite(analysisVO, f);
                        list3.addAll(satellite1);
                        return null;
                    }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
                    analysisVO.setCompleteList3(list3);
                }
                break;
            case 4:
                //4.添加雷达数据集
                if(!CollectionUtils.isEmpty(radars)){
                    List<Map> list4 = new ArrayList<>();
                    ThreadParallelUtil.run(radars, f->{
                        f.setCountyIds(Arrays.asList(f.getCountyId().split(",")));
                        List<Map> radars1 = workbenchDAO.getEvenDeptCompletionRadars(analysisVO, f);
                        list4.addAll(radars1);
                        return null;
                    }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
                    analysisVO.setCompleteList4(list4);
                }
                break;
            default: break;
        }
    }


    /**
     * 查询用户下拥有权限
     * @param dto
     */
    public void handleDeptOrgIdList(AnalysisVO dto,List<SysDeptDeviceVO> radars,List<SysDeptDeviceVO> satellites){
        SysDept vo = new SysDept();
        List<String> orgIdList = workbenchDAO.selectDepts(vo);
        if(!CollUtil.isEmpty(orgIdList)){
            SysDept dept = new SysDept();
            dept.setDeptIdList(orgIdList);
            dept.setTenantId(dto.getTenantId());
            dept.setIndustryCode(dto.getIndustryCode());
            //卫星设备的省市区权限
            satellites.addAll(workbenchDAO.getSatellites(dept));
            // 雷达走雷达设备的省市区权限
            radars.addAll(workbenchDAO.getRaders(dept));
        }
    }


    public Map typeTagCommon(List<Map> statisticsMap,List<Map> tagsList,String type,StatisticsVO statisticsVO){
        List<Map> resList = new ArrayList<>();
        Map mapList = new HashMap<>();
        int sum = 0;
        boolean exportAll = statisticsVO != null && "total".equals(statisticsVO.getTotalRemark());
        // 兼容 pageNum/pageSize 与历史 pageNums/pageSizes
        if (statisticsVO != null) {
            if (statisticsVO.getPageNums() == null && statisticsVO.getPageNum() != null) {
                statisticsVO.setPageNums(statisticsVO.getPageNum());
            }
            if (statisticsVO.getPageSizes() == null && statisticsVO.getPageSize() != null) {
                statisticsVO.setPageSizes(statisticsVO.getPageSize());
            }
        }
        Integer pageNum = statisticsVO != null ? statisticsVO.getPageNums() : null;
        Integer pageSize = statisticsVO != null ? statisticsVO.getPageSizes() : null;
        boolean doPage = pageNum != null && pageSize != null && pageNum > 0 && pageSize > 0;

        if (doPage || exportAll) {
            // 导出全量 / 真分页：先补名称再切片；分页模式不再聚合「其他」
            for (Map map : statisticsMap) {
                fillTypeOrTagName(map, tagsList, type);
                resList.add(map);
            }
            int total = resList.size();
            if (doPage && !exportAll) {
                int skipNum = pageSize * (pageNum - 1);
                resList = resList.stream().skip(skipNum).limit(pageSize).collect(Collectors.toList());
            }
            mapList.put(Constants.RES_LIST, resList);
            mapList.put(Constants.SUM, 0);
            mapList.put(Constants.TOTAL, total);
            return mapList;
        }

        // 兼容旧图表：Top5 + 其余汇总为「其他」
        int count = 0;
        for (Map map : statisticsMap) {
            count++;
            if (count <= 5) {
                fillTypeOrTagName(map, tagsList, type);
                resList.add(map);
            } else {
                sum += (new BigDecimal(map.get(Constants.NUM).toString())).longValue();
            }
        }
        mapList.put(Constants.RES_LIST,resList);
        mapList.put(Constants.SUM,sum);
        mapList.put(Constants.TOTAL, resList.size() + (sum > 0 ? 1 : 0));
        return mapList;
    }

    private void fillTypeOrTagName(Map map, List<Map> tagsList, String type) {
        if (tagsList == null || map == null) {
            return;
        }
        tagsList.forEach(item -> {
            if (Constants.TYPE.equals(type)) {
                if (item.get(Constants.TYPE_VALUE) != null
                        && item.get(Constants.TYPE_VALUE).toString().equals(String.valueOf(map.get(Constants.TYPE_VALUE)))) {
                    map.put(Constants.TYPE_NAME, item.get(Constants.TYPE_NAME));
                }
            } else if ("tag".equals(type)) {
                if (item.get(Constants.TAG_ID) != null
                        && item.get(Constants.TAG_ID).toString().equals(String.valueOf(map.get(Constants.TAG_ID)))) {
                    map.put(Constants.TAG_NAME, item.get(Constants.TAG_NAME));
                }
            }
        });
    }

    public void areaCodeHandle(List<Map> eventNumByArea,AnalysisVO analysisVO){
        String totalName = "";
        List<String> areaList = new ArrayList<>();
        for (Map map : eventNumByArea) {
            switch (analysisVO.getAreaBoxNum()){
                case 0:
                    areaList.add(String.valueOf(map.get(Constants.PROVINCE_ID)));
                    break;
                case 1:
                    areaList.add(String.valueOf(map.get(Constants.PROVINCE_ID)));
                    areaList.add(String.valueOf(map.get(Constants.CITY_ID)));
                    break;
                case 2:
                    areaList.add(String.valueOf(map.get(Constants.PROVINCE_ID)));
                    areaList.add(String.valueOf(map.get(Constants.CITY_ID)));
                    areaList.add(String.valueOf(map.get(Constants.COUNTY_ID)));
                    break;
                case 3:
                    areaList.add(String.valueOf(map.get(Constants.PROVINCE_ID)));
                    areaList.add(String.valueOf(map.get(Constants.CITY_ID)));
                    areaList.add(String.valueOf(map.get(Constants.COUNTY_ID)));
                    if (StringUtils.isNotEmpty(String.valueOf(map.get(Constants.TOWN_ID))) && !"null".equals(String.valueOf(map.get(Constants.TOWN_ID)))){
                        areaList.add(String.valueOf(map.get(Constants.TOWN_ID)));
                    }else {
                        areaList.add(String.valueOf(map.get(Constants.STREET_ID)));
                    }
                    break;
                default:
                    break;
            }
        }
        areaList= areaList.stream().distinct().collect(Collectors.toList());
        List<Map<String, String>>alarmByCode = workbenchDAO.getAlarmByCodes(areaList);
        Map<String, String> codeList = alarmByCode.stream().collect( Collectors.toMap( t -> t.get(Constants.AREA_CODE), t -> t.get(Constants.AREA_NAME), (o, n) -> n, HashMap::new));

        for (Map<String,String> map : eventNumByArea) {
            if ("其它".equals(map.get(Constants.REGION_NAME))){
                continue;
            }
            String provinceName = codeList.get(map.get("provinceId"));
            String cityName = codeList.get(map.get("cityId"));
            String countyName = codeList.get(map.get("countyId"));
            String streetName = "";
            if (StringUtils.isNotEmpty(String.valueOf(map.get(Constants.TOWN_ID))) && !"null".equals(String.valueOf(map.get(Constants.TOWN_ID)))){
                 streetName = codeList.get(map.get(Constants.TOWN_ID));
            }else {
                 streetName = codeList.get(map.get(Constants.STREET_ID));
            }
            if (provinceName == null){ provinceName = ""; }
            if (cityName == null){ cityName = ""; }
            if (countyName == null){ countyName = ""; }
            if (streetName == null){ streetName = ""; }
            switch (analysisVO.getAreaBoxNum()){
                case 0:
                    totalName = provinceName;
                    map.put(Constants.REGION_NAME,totalName);
                    map.put(Constants.PROVINCE_NAME,provinceName);
                    break;
                case 1:
                    totalName = provinceName+cityName;
                    map.put(Constants.REGION_NAME,totalName);
                    map.put(Constants.PROVINCE_NAME,provinceName);
                    map.put(Constants.CITY_NAME,cityName);
                    break;
                case 2:
                    totalName = provinceName+cityName+countyName;
                    map.put(Constants.REGION_NAME,totalName);
                    map.put(Constants.PROVINCE_NAME,provinceName);
                    map.put(Constants.CITY_NAME,cityName);
                    map.put(Constants.COUNTY_NAME,countyName);
                    break;
                case 3:
                    totalName = provinceName+cityName+countyName+streetName;
                    map.put(Constants.REGION_NAME,totalName);
                    map.put(Constants.PROVINCE_NAME,provinceName);
                    map.put(Constants.CITY_NAME,cityName);
                    map.put(Constants.COUNTY_NAME,countyName);
                    map.put(Constants.STREET_NAME,streetName);
                    break;
                default:
                    break;
            }
        }
    }

    //处理完结事件平均处理时长
    private String handleAvgTime(String time){
        String avgTime = "";
        double timeInSeconds = Double.parseDouble(time) * 60;
        long seconds = Math.round(timeInSeconds);
        if (seconds < 60){
            avgTime = "0分";
        }else {
            long days = TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);
            long minutes = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
            if (days == 0) {
                if (hours == 0) {
                    avgTime = minutes + "分";
                } else {
                    avgTime = hours + "时" + minutes + "分";
                }
            } else {
                avgTime = days + "天" + hours + "时" + minutes + "分";
            }
        }
        return avgTime;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "m.device_code", alarmSourceVal = "13",acceptColumn="od.accept_node")
    public List<Map> getRecorderDeviceStatisticsByArea(AnalysisVO analysisVO) {
        if(ObjectUtil.isNull(analysisVO.getAreaBoxNum())){ //默认省维度统计
            analysisVO.setAreaBoxNum(0);
        }
        if (ObjectUtil.isNotNull(analysisVO.getAreaRecorderSelect())){
            analysisVO.setAreaBoxNum(analysisVO.getAreaRecorderSelect());
        }
        return  workbenchDAO.getRecorderDeviceStatisticsByArea(analysisVO);
    }

    /**
     * 执法记录仪设备总览
     * @param analysisVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="od.tenant_id",industryCode="od.industry_code",deviceColumn = "recorder.device_code", alarmSourceVal = "13",acceptColumn="od.accept_node")
    public Result<DeviceStatisticsVO> getRecorderDeviceStatistics(AnalysisVO analysisVO){
        DeviceStatisticsVO vo = workbenchDAO.getRecorderDeviceStatistics(analysisVO);
        DeviceStatisticsVO channelVo = workbenchDAO.getRecorderChannelStatistics(analysisVO);
        vo.setChannelCountOffline(channelVo.getChannelCountOffline());
        vo.setChannelCountOnline(channelVo.getChannelCountOnline());
        vo.setChannelCountTotal(channelVo.getChannelCountTotal());
        if (vo.getDeviceCountTotal() > 0) {
            vo.setOnlinePercentageDev(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getDevCountOnline())/vo.getDeviceCountTotal())));
        }
        if (vo.getChannelCountTotal() > 0) {
            vo.setOnlinePercentage(String.format("%.2f", Double.valueOf(Double.valueOf(vo.getChannelCountOnline())/vo.getChannelCountTotal())));
        }

        return Result.data(vo);
    }

    /**
     * 获取是否为国产化标识
     * @return
     */
    private boolean getLocalizationFlag() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.LOCALIZATION_FLAG);

        try {
            AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);

            if (Objects.nonNull(configInfo) && Objects.nonNull(configInfo.get(Constants.RESULT_DATA))) {

                JsonObject jsonObject = GsonUtils.parseJsonObject(GsonUtils.toJSONString(configInfo.get(Constants.RESULT_DATA)));
                String localFlag = jsonObject != null && jsonObject.has(Constants.PARAM_CONFIG_VALUE) && !jsonObject.get(Constants.PARAM_CONFIG_VALUE).isJsonNull() ? jsonObject.get(Constants.PARAM_CONFIG_VALUE).getAsString() : null;

                return "1".equals(localFlag);
            }
        } catch (Exception e) {
            // 记录异常日志
            log.error("获取本地化标志配置失败", e);
        }

        return false;
    }

    /**
     * 查询无人机设备区域分布排行
     * @param statisticsVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="12",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public List<DeviceAreaDistributionNumVO> getUavAreaDistributionNum(StatisticsVO statisticsVO) {
        //处理传入的组织条件
        commonUtils.handleOrgIdList(null,statisticsVO);
        List<DeviceAreaDistributionNumVO> deviceNumAll = workbenchDAO.getUavAreaDistributionNum(statisticsVO);
        StatisticsVO vo = new StatisticsVO();
        List<String> deviceCode = deviceNumAll.stream().map(DeviceAreaDistributionNumVO::getDeviceCode).distinct().collect(Collectors.toList());
        vo.setDeviceCodeList(deviceCode);
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (ObjectUtil.isNotNull(user)){
            String tenantId = user.getTenantId();
            String industryCode = user.getIndustryCode();
            vo.setTenantId(tenantId);
            vo.setIndustryCode(industryCode);
        }
        List<Map> devices = workbenchDAO.getUavDevices(vo);
        //根据物联设备编码匹配物联设备名称
        for (DeviceAreaDistributionNumVO deviceAreaDistributionNumVO : deviceNumAll) {
            for (Map item : devices) {
                if(deviceAreaDistributionNumVO.getDeviceCode().equals(item.get(Constants.DEVICE_CODE))){
                    deviceAreaDistributionNumVO.setDeviceName(String.valueOf(item.get(Constants.DEVICE_NAME)));
                    break;
                }
            }
        }
        return deviceNumAll;
    }

    /**
     * 点位告警统计
     * @param statisticsVO
     * @return
     */
    public Map<String, Object> getPointAlarmNum(StatisticsVO statisticsVO) {
        log.info("查询点位告警统计,入参{}", statisticsVO.toString());
        Map<String, Object> result = new HashMap<>();
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (ObjectUtil.isNotNull(user)){
            String tenantId = user.getTenantId();
            String industryCode = user.getIndustryCode();
            statisticsVO.setTenantId(tenantId);
            statisticsVO.setIndustryCode(industryCode);
        }
        result.put(Constants.HIGH_POINT_NUM, Constants.ZERO);
        result.put(Constants.LOW_POINT_NUM, Constants.ZERO);

        List<Map<String, Object>> labelInfos = null;
        if (!CollectionUtils.isEmpty(statisticsVO.getDeviceLabelIdList())) {
            //根据标签id查询标签信息
            labelInfos = workbenchDAO.getLabelInfoByIds(statisticsVO);
            log.info("点位告警统计-查询标签信息: {}", JSON.toJSONString(labelInfos));
            if (CollectionUtils.isEmpty(labelInfos)) {
                return result;
            }
        }

        //处理组织筛选条件
        commonUtils.handleOrgIdList(null, statisticsVO);
        if (StringUtils.isNotNull(statisticsVO) &&
                !CollectionUtils.isEmpty(statisticsVO.getOrgIdList())) {

            boolean hasDataSource = false;
            if (!CollectionUtils.isEmpty(statisticsVO.getDeviceCodeList())) {
                hasDataSource = true;
            }
            if (!CollectionUtils.isEmpty(statisticsVO.getSatelliteList())) {
                hasDataSource = true;
            }
            if (!CollectionUtils.isEmpty(statisticsVO.getRadarList())) {
                hasDataSource = true;
            }
            if (!hasDataSource) {
                return result;
            }
        }

        // 如果没有传入标签ID，直接返回所有点位类型统计
        if (CollectionUtils.isEmpty(statisticsVO.getDeviceLabelIdList())) {
            return getAllPointAlarmStat(statisticsVO);
        }

        // 处理传入的标签信息
        boolean hasHighPoint = false;
        boolean hasLowPoint = false;
        List<String> highPointDevCodes = new ArrayList<>();
        List<String> lowPointDevCodes = new ArrayList<>();

        for (Map<String, Object> labelInfo : labelInfos) {
            String labelName = String.valueOf(labelInfo.get(Constants.PARAM_LABEL_NAME));
            String deviceCode = String.valueOf(labelInfo.get(Constants.DEVICE_CODE));
            if (labelName != null && deviceCode != null) {
                if (labelName.startsWith("高点")) {
                    hasHighPoint = true;
                    highPointDevCodes.add(deviceCode);
                } else if (labelName.startsWith("低点")) {
                    hasLowPoint = true;
                    lowPointDevCodes.add(deviceCode);
                }
            }
        }

        // 如果同时包含高点和低点标签，返回所有点位类型
        if (hasHighPoint && hasLowPoint) {
            return getAllPointAlarmStat(statisticsVO);
        }

        // 根据标签类型返回对应的告警数量
        if (hasHighPoint) {
            statisticsVO.setDeviceCodeList(highPointDevCodes);
            Integer highCount = workbenchDAO.getAlarmCountByDevices(statisticsVO);
            result.put(Constants.HIGH_POINT_NUM, highCount);
            result.put(Constants.LOW_POINT_NUM, Constants.ZERO);
        } else if (hasLowPoint) {
            statisticsVO.setDeviceCodeList(lowPointDevCodes);
            Integer lowCount = workbenchDAO.getAlarmCountByDevices(statisticsVO);
            result.put(Constants.HIGH_POINT_NUM, Constants.ZERO);
            result.put(Constants.LOW_POINT_NUM, lowCount);
        }

        return result;
    }

    //获取所有点位类型的统计
    private Map<String, Object> getAllPointAlarmStat(StatisticsVO statisticsVO) {
        Map<String, Object> result = new HashMap<>();
        log.info("点位告警统计-开始查询所有点位类型告警数量");

        // 查询所有高点位设备告警数量
        List<String> allHighPointDevices = workbenchDAO.getAllHighPointDevices(
                statisticsVO.getTenantId(), statisticsVO.getIndustryCode());
        log.info("点位告警统计-查询到高点位设备数量: {}", allHighPointDevices.size());
        Integer highCount = Constants.ZERO;
        if (!CollectionUtils.isEmpty(allHighPointDevices)) {
            // 临时设置设备编码列表
            statisticsVO.setDeviceCodeList(allHighPointDevices);
            highCount = workbenchDAO.getAlarmCountByDevices(statisticsVO);
            // 查询完后清空设备编码列表
            statisticsVO.setDeviceCodeList(null);
        }

        // 查询所有低点位设备告警数量
        List<String> allLowPointDevices = workbenchDAO.getAllLowPointDevices(
                statisticsVO.getTenantId(), statisticsVO.getIndustryCode());
        log.info("点位告警统计-查询到低点位设备数量: {}", allHighPointDevices.size());
        Integer lowCount = Constants.ZERO;
        if (!CollectionUtils.isEmpty(allLowPointDevices)) {
            statisticsVO.setDeviceCodeList(allLowPointDevices);
            lowCount = workbenchDAO.getAlarmCountByDevices(statisticsVO);
            statisticsVO.setDeviceCodeList(null);
        }

        result.put(Constants.HIGH_POINT_NUM, highCount);
        result.put(Constants.LOW_POINT_NUM, lowCount);
        return result;
    }
}
