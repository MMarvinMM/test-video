package com.chinatower.analysis.modules.statistic.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.modules.common.CommonUtils;
import com.chinatower.analysis.modules.common.Constants;
import com.chinatower.analysis.modules.statistic.dao.DeviceStatisticsDAO;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 设备统计
 * @author fzw
 * @version 1.0
 * @date 2023/4/8 19:45
 */
@Service
@Slf4j
public class DeviceStatisticsService {

    @Resource
    DeviceStatisticsDAO deviceStatisticsDAO;
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private SystemFeignClient systemFeignClient;

    /**
     * 摄像机数量区域排行(租户)
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",industryCode="dataTable.industry_code",deviceColumn = "dataTable.device_code",acceptColumn="dataTable.accept_node")
    public List<Map<String,String>> getCameraNumByArea(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return new ArrayList<>();
        }
        if(ObjectUtil.isNull(analysisVO.getAreaBoxNum())){ //默认省维度统计
            analysisVO.setAreaBoxNum(0);
        }
        if (ObjectUtil.isNotNull(analysisVO.getAreaDeviceSelect())){
            analysisVO.setAreaBoxNum(analysisVO.getAreaDeviceSelect());
        }

        List<Map<String,String>> deviceStatisticsVOList = deviceStatisticsDAO.getCameraNumByArea(analysisVO);
        log.info("getCameraNumByArea result:{} ", JSON.toJSONString(deviceStatisticsVOList));
        return deviceStatisticsVOList;
    }
    /**
     * 摄像机订购算法排行
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",industryCode="dataTable.industry_code",deviceColumn = "dataTable.device_code",acceptColumn="dataTable.accept_node")
    public List<Map<String,String>> getOrderAlgorithmRank(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return new ArrayList<>();
        }
        List<Map<String,String>> deviceStatisticsVOList  = deviceStatisticsDAO.getOrderAlgorithmRank(analysisVO);
        log.info("getOrderAlgorithmRank result:{} ", JSON.toJSONString(deviceStatisticsVOList));
        return deviceStatisticsVOList;
    }

    /**
     * 通道配置前端算法任务排行
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="ag.tenant_id",industryCode="ag.industry_code",deviceColumn = "ag.device_code",acceptColumn="od.accept_node")
    public List<Map<String,String>> getForeAlgorithmTaskRank(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return new ArrayList<>();
        }
        List<Map<String,String>> deviceStatisticsVOList  = deviceStatisticsDAO.getForeAlgorithmTaskRank(analysisVO);
        log.info("getForeAlgorithmTaskRank result:{} ", JSON.toJSONString(deviceStatisticsVOList));
        return deviceStatisticsVOList;
    }
    /**
     * 通道配置后端算法任务排行
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="ac.tenant_id",industryCode="ac.industry_code",deviceColumn = "ac.device_code",acceptColumn="od.accept_node")
    public List<Map<String,String>> getAfterAlgorithmTaskRank(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return new ArrayList<>();
        }
        List<Map<String,String>> deviceStatisticsVOList = getLocalizationFlag()
                ? deviceStatisticsDAO.getAfterAlgorithmTaskRankLocal(analysisVO)
                : deviceStatisticsDAO.getAfterAlgorithmTaskRank(analysisVO);
        log.info("getAfterAlgorithmTaskRank result:{} ", JSON.toJSONString(deviceStatisticsVOList));
        return deviceStatisticsVOList;
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

                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
                String localFlag = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);

                return "1".equals(localFlag);
            }
        } catch (Exception e) {
            // 记录异常日志
            log.error("获取本地化标志配置失败", e);
        }

        return false;
    }

    /**
     * 通道启用前端算法任务排行
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="ag.tenant_id",industryCode="ag.industry_code",deviceColumn = "ag.device_code",acceptColumn="od.accept_node")
    public List<Map<String,String>> getStartForeAlgorithmTaskRank(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return new ArrayList<>();
        }
        List<Map<String,String>> deviceStatisticsVOList  = deviceStatisticsDAO.getStartForeAlgorithmTaskRank(analysisVO);
        log.info("getStartForeAlgorithmTaskRank result:{} ", JSON.toJSONString(deviceStatisticsVOList));
        return deviceStatisticsVOList;
    }


    /**
     * 通道启用后端算法任务排行
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="ac.tenant_id",industryCode="ac.industry_code",deviceColumn = "ac.device_code",acceptColumn="od.accept_node")
    public List<Map<String,String>> getStartAfterAlgorithmTaskRank(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return new ArrayList<>();
        }
        List<Map<String,String>> deviceStatisticsVOList = getLocalizationFlag()
                ? deviceStatisticsDAO.getStartAfterAlgorithmTaskRankLocal(analysisVO)
                : deviceStatisticsDAO.getStartAfterAlgorithmTaskRank(analysisVO);
        log.info("getStartAfterAlgorithmTaskRank result:{} ", JSON.toJSONString(deviceStatisticsVOList));
        return deviceStatisticsVOList;
    }

    /**
     * 算法任务数量统计
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="ac.tenant_id",industryCode="ac.industry_code",deviceColumn = "ac.device_code",acceptColumn="od.accept_node")
    public Map getAlgorithmTaskNum(AnalysisVO analysisVO){
        AnalysisVO vo = new AnalysisVO();
        Map algorithmTaskTotalNum = this.getOrderAlgorithmTaskNum(vo);
        //通道启用后端算法任务总数量及后端算法任务总数量
        Map afterTotalNum = getLocalizationFlag()
                ? deviceStatisticsDAO.getStartAfterAlgorithmTaskTotalNumLocal(analysisVO)
                : deviceStatisticsDAO.getStartAfterAlgorithmTaskTotalNum(analysisVO);
        //通道启用前端算法任务总数量
        Map foreTotalNum = deviceStatisticsDAO.getStartForeAlgorithmTaskTotalNum(analysisVO);
        afterTotalNum.put("orderAlgorithmTaskNum",algorithmTaskTotalNum.get("orderAlgorithmTaskNum"));
        afterTotalNum.put("startForeAlgorithmTaskNum",foreTotalNum.get("startForeAlgorithmTaskNum"));
        log.info("getAlgorithmTaskNum result:{} ", JSON.toJSONString(afterTotalNum));
        return afterTotalNum;
    }

    /**
     * 订购算法总数量
     */
    public Map getOrderAlgorithmTaskNum(AnalysisVO analysisVO){
        Map algorithmTaskTotalNum = deviceStatisticsDAO.getAlgorithmTaskTotalNum(analysisVO);
        return algorithmTaskTotalNum;
    }

    /**
     * 算法名称列表
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",industryCode="dataTable.industry_code",deviceColumn = "dataTable.device_code",acceptColumn="dataTable.accept_node")
    public List<Map<String,String>> getAlgorithmTaskList(AnalysisVO analysisVO){
        List<Map<String,String>> algorithmTaskList = deviceStatisticsDAO.getAlgorithmTaskList(analysisVO);
        log.info("getAlgorithmTaskList result:{} ", JSON.toJSONString(algorithmTaskList));
        return algorithmTaskList;
    }

    /**
     * 告警事件中拥有订购摄像机数量
     */
    public List<String> getDeviceCodeByUser(AnalysisVO analysisVO){
        //查询用户下订购设备编码
        List<String> deviceCodeByUser = deviceStatisticsDAO.getDeviceCodeByUser(analysisVO);
        return deviceCodeByUser;
    }

    /**
     * 告警事件中拥有订购摄像机数量
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<Map> getDeviceNumInEvent(AnalysisVO analysisVO){
        //查询用户下订购设备编码
        AnalysisVO vo = new AnalysisVO();
        List<String> deviceCodeByUser = this.getDeviceCodeByUser(vo);
        analysisVO.setDeviceCodeList(deviceCodeByUser);
        List<Map> deviceNumInEvent = deviceStatisticsDAO.getDeviceNumInEvent(analysisVO);
        log.info("deviceNumInEvent result:{} ", JSON.toJSONString(deviceNumInEvent));
        return deviceNumInEvent;
    }
}
