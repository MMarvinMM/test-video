package com.chinatower.analysis.modules.workbench.service;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.modules.workbench.dao.CheckpointDAO;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CheckpointService {
    @Resource
    private CheckpointDAO checkpointDao;

    private static final String DIMENSION_FLAG_YEAR = "1";
    private static final String DIMENSION_FLAG_MONTH = "2";
    private static final String DIMENSION_FLAG_DAY = "3";
    private static final String DIMENSION_FLAG_ERROR = "维度标识有误";

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public Result<String> getCheckpointEventNumByDay(CheckpointConditionVO checkpointConditionVO){
        checkpointConditionVO.setValidDeviceCodes(getCheckpointDeviceList());
        return Result.data(checkpointDao.getCheckpointEventNumByDay(checkpointConditionVO));
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public List<CheckpointAlarmVO> getCaptureRecord(CheckpointBatchVO checkpointBatchVO){
        checkpointBatchVO.setValidDeviceCodes(getCheckpointDeviceList());
        if (CollectionUtils.isEmpty(checkpointBatchVO.getValidDeviceCodes())) {
            return Collections.emptyList();
        }
        return checkpointDao.getCaptureRecord(checkpointBatchVO);
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        List<CheckpointTrendDataVO> trendList;
        checkpointStatisticsConditionVO.setValidDeviceCodes(getCheckpointDeviceList());
        if (CollectionUtils.isEmpty(checkpointStatisticsConditionVO.getValidDeviceCodes())) {
            return Result.data(Collections.emptyList());
        }
        if(DIMENSION_FLAG_YEAR.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            trendList = checkpointDao.getTrafficTrendStatisticsForYear(checkpointStatisticsConditionVO);
        } else if (DIMENSION_FLAG_MONTH.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            trendList = checkpointDao.getTrafficTrendStatisticsForMonth(checkpointStatisticsConditionVO);
        } else {
            return Result.fail(DIMENSION_FLAG_ERROR);
        }

        return Result.data(trendList);
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatisticsByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        List<CheckpointTrendDataVO> trendList;
        //如果前端查询数据中存在checkpointStatisticsConditionVO.getValidDeviceCodes()，取出该租户下的所有设备，并计算交集
        List<String> validDeviceCodesByTenantId = getCheckpointDeviceList();
        if (CollectionUtils.isNotEmpty(checkpointStatisticsConditionVO.getValidDeviceCodes())) {
            //计算交集
            checkpointStatisticsConditionVO.setValidDeviceCodes(
                    Lists.newArrayList(CollectionUtils.intersection(
                            validDeviceCodesByTenantId, checkpointStatisticsConditionVO.getValidDeviceCodes())));
            log.info("get traffic trend statistics by time 计算交集");
        } else {
            //如果没有查询数据，就查询当前租户下所有设备
            checkpointStatisticsConditionVO.setValidDeviceCodes(validDeviceCodesByTenantId);
            log.info("get traffic trend statistics by time 无查询条件，查询当前租户所有设备");
        }
        if (CollectionUtils.isEmpty(checkpointStatisticsConditionVO.getValidDeviceCodes())) {
            return Result.data(Collections.emptyList());
        }
        if(DIMENSION_FLAG_YEAR.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            trendList = checkpointDao.getTrafficTrendStatisticsForYearByTime(checkpointStatisticsConditionVO);
        } else if (DIMENSION_FLAG_MONTH.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            trendList = checkpointDao.getTrafficTrendStatisticsForMonthByTime(checkpointStatisticsConditionVO);
        }else if (DIMENSION_FLAG_DAY.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            trendList = checkpointDao.getTrafficTrendStatisticsForDayByTime(checkpointStatisticsConditionVO);
        } else {
            return Result.fail(DIMENSION_FLAG_ERROR);
        }

        return Result.data(trendList);
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        checkpointStatisticsConditionVO.setValidDeviceCodes(getCheckpointDeviceList());
        if (CollectionUtils.isEmpty(checkpointStatisticsConditionVO.getValidDeviceCodes())) {
            return Result.data(Collections.emptyList());
        }
        return Result.data(checkpointDao.getHighFrequencyPeriodStatistics(checkpointStatisticsConditionVO));
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatisticsByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        checkpointStatisticsConditionVO.setValidDeviceCodes(getCheckpointDeviceList());
        if (CollectionUtils.isEmpty(checkpointStatisticsConditionVO.getValidDeviceCodes())) {
            return Result.data(Collections.emptyList());
        }
        return Result.data(checkpointDao.getHighFrequencyPeriodStatisticsByTime(checkpointStatisticsConditionVO));
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public Result<List<CheckpointDeviceRankingDataVO>> getTrafficFlowRankingStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        List<CheckpointDeviceRankingDataVO> rankingList;
        checkpointStatisticsConditionVO.setValidDeviceCodes(getCheckpointDeviceList());
        if (CollectionUtils.isEmpty(checkpointStatisticsConditionVO.getValidDeviceCodes())) {
            return Result.data(Collections.emptyList());
        }
        if(DIMENSION_FLAG_YEAR.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            rankingList = checkpointDao.getTrafficFlowRankingStatisticsForYear(checkpointStatisticsConditionVO);
        } else if (DIMENSION_FLAG_MONTH.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            rankingList = checkpointDao.getTrafficFlowRankingStatisticsForMonth(checkpointStatisticsConditionVO);
        } else if (DIMENSION_FLAG_DAY.equals(checkpointStatisticsConditionVO.getDimensionFlag())){
            rankingList = checkpointDao.getTrafficFlowRankingStatisticsForDay(checkpointStatisticsConditionVO);
        } else {
            return Result.fail(DIMENSION_FLAG_ERROR);
        }

        return Result.data(rankingList);
    }

    private List<String> getCheckpointDeviceList() {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        return checkpointDao.getCheckpointDeviceList(currentUser.getUser().getTenantId());
    }
}
