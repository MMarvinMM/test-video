package com.chinatower.event.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.event.dto.zhongshi.FunctionValidationDTO;
import com.chinatower.event.dto.zhongshi.LargeModelValidationDTO;
import com.chinatower.event.dto.zhongshi.ModelValidationDTO;
import com.chinatower.event.dto.zhongshi.ZhongShiDateRangeDTO;
import com.chinatower.event.mapper.FunctionValidationMapper;
import com.chinatower.event.mapper.LargeModelValidationMapper;
import com.chinatower.event.mapper.ModelValidationMapper;
import com.chinatower.event.service.ZhongShiScreenStatisticsService;
import com.chinatower.event.vo.zhongshi.*;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ZhongShiScreenStatisticsServiceImpl implements ZhongShiScreenStatisticsService {

    private static final int YES = 1;
    private static final int NOT_DELETE = 0;
    private static final String COMPLETED = "已完成";
    private static final String IN_PROGRESS = "进行中";

    @Autowired
    private FunctionValidationMapper functionValidationMapper;

    @Autowired
    private ModelValidationMapper modelValidationMapper;

    @Autowired
    private LargeModelValidationMapper largeModelValidationMapper;

    /**
     * 功能模块统计：
     * 1. 验证功能点数量 = function_point_count 求和
     * 2. 首次通过率 = test_times=1 的记录数 / 总记录数
     * 3. 最终通过率 = is_passed=1 的记录数 / 总记录数
     */
    @Override
    public FunctionModuleStatsVO queryFunctionModuleStats(ZhongShiDateRangeDTO req) {
        Date[] dateRange = buildDateRange(req);
        List<FunctionValidationDTO> records = listFunctionModules(dateRange[0], dateRange[1]);

        int total = records.size();
        long functionPointCount = records.stream()
                .map(FunctionValidationDTO::getFunctionPointCount)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum);
        long firstPassCount = records.stream()
                .filter(item -> Objects.equals(1L, item.getTestTimes()) && Objects.equals(YES, item.getPassed()))
                .count();
        long finalPassCount = records.stream()
                .filter(item -> Objects.equals(YES, item.getPassed()))
                .count();

        FunctionModuleStatsVO result = new FunctionModuleStatsVO();
        result.setFunctionPointCount(functionPointCount);
        result.setFirstPassRate(calculateRate(firstPassCount, total));
        result.setFinalPassRate(calculateRate(finalPassCount, total));
        result.setDetailList(records);
        log.info("查询功能模块统计完成，时间范围:[{}, {}]，总记录数:{}，功能点总数:{}，首次通过数量:{}，最终通过数量:{}",
                dateRange[0], dateRange[1], total, functionPointCount, firstPassCount, finalPassCount);
        return result;
    }

    /**
     * 小模型统计
     */
    @Override
    public SmallModelStatsVO querySmallModelStats(ZhongShiDateRangeDTO req) {
        Date[] dateRange = buildDateRange(req);
        List<ModelValidationDTO> records = listSmallModel(dateRange[0], dateRange[1]);
        int total = records.size();

        List<ModelValidationDTO> completedList = records.stream()
                .filter(item -> COMPLETED.equals(item.getTestStatus()))
                .collect(Collectors.toList());
        List<ModelValidationDTO> inProgressList = records.stream()
                .filter(item -> IN_PROGRESS.equals(item.getTestStatus()))
                .collect(Collectors.toList());

        SmallModelStatsVO result = new SmallModelStatsVO();
        result.setCompleted(buildSmallModelTab(completedList, total));
        result.setInProgress(buildSmallModelTab(inProgressList, total));
        log.info("查询小模型统计完成，时间范围:[{}, {}]，总记录数:{}，已完成数量:{}，进行中数量:{}",
                dateRange[0], dateRange[1], total, completedList.size(), inProgressList.size());
        return result;
    }

    /**
     * 大模型统计：
     * 父级按 model_name 分组，子级按 downstream_algorithm 分组
     */
    @Override
    public LargeModelStatsVO queryLargeModelStats(ZhongShiDateRangeDTO req) {
        Date[] dateRange = buildDateRange(req);
        List<LargeModelValidationDTO> records = listLargeModel(dateRange[0], dateRange[1]);

        Map<String, List<LargeModelValidationDTO>> modelMap = records.stream()
                .collect(Collectors.groupingBy(LargeModelValidationDTO::getModelName,
                        LinkedHashMap::new, Collectors.toList()));

        List<LargeModelParentStatsVO> parentList = new ArrayList<>();
        for (Map.Entry<String, List<LargeModelValidationDTO>> entry : modelMap.entrySet()) {
            List<LargeModelValidationDTO> modelRecords = entry.getValue();
            int total = modelRecords.size();
            long firstPassCount = modelRecords.stream()
                    .filter(item -> Objects.equals(1L, item.getTestTimes()) && Objects.equals(YES, item.getPassed()))
                    .count();
            long finalPassCount = modelRecords.stream()
                    .filter(item -> Objects.equals(YES, item.getPassed()))
                    .count();

            LargeModelParentStatsVO parent = new LargeModelParentStatsVO();
            parent.setModelName(modelRecords.get(0).getModelName());
            parent.setDownstreamAlgorithmCount(total);
            parent.setFirstPassRate(calculateRate(firstPassCount, total));
            parent.setFinalPassRate(calculateRate(finalPassCount, total));
            parent.setChildren(buildLargeModelChildren(modelRecords));
            parentList.add(parent);
        }

        LargeModelStatsVO result = new LargeModelStatsVO();
        result.setModelList(parentList);
        log.info("查询大模型统计完成，时间范围:[{}, {}]，总记录数:{}，模型数量:{}",
                dateRange[0], dateRange[1], records.size(), parentList.size());
        return result;
    }

    /**
     * 查询时间范围内功能模块验证数据
     */
    private List<FunctionValidationDTO> listFunctionModules(Date startTime, Date endTime) {
        SysUser user = getCurrentUser();
        QueryWrapper<FunctionValidationDTO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(FunctionValidationDTO::getTenantId, user.getTenantId())
                .eq(FunctionValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(FunctionValidationDTO::getDeleteFlag, NOT_DELETE)
                .between(FunctionValidationDTO::getTestDate, startTime, endTime)
                .orderByDesc(FunctionValidationDTO::getTestDate);
        return functionValidationMapper.selectList(wrapper);
    }

    /**
     * 查询时间范围内小模型验证数据
     */
    private List<ModelValidationDTO> listSmallModel(Date startTime, Date endTime) {
        SysUser user = getCurrentUser();
        QueryWrapper<ModelValidationDTO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ModelValidationDTO::getTenantId, user.getTenantId())
                .eq(ModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(ModelValidationDTO::getDeleteFlag, NOT_DELETE)
                .between(ModelValidationDTO::getTestDate, startTime, endTime)
                .orderByDesc(ModelValidationDTO::getTestDate);
        return modelValidationMapper.selectList(wrapper);
    }

    /**
     * 查询时间范围内大模型验证数据
     */
    private List<LargeModelValidationDTO> listLargeModel(Date startTime, Date endTime) {
        SysUser user = getCurrentUser();
        QueryWrapper<LargeModelValidationDTO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(LargeModelValidationDTO::getTenantId, user.getTenantId())
                .eq(LargeModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(LargeModelValidationDTO::getDeleteFlag, NOT_DELETE)
                .between(LargeModelValidationDTO::getTestDate, startTime, endTime)
                .orderByAsc(LargeModelValidationDTO::getModelName)
                .orderByAsc(LargeModelValidationDTO::getDownstreamAlgorithm)
                .orderByDesc(LargeModelValidationDTO::getTestDate);
        return largeModelValidationMapper.selectList(wrapper);
    }

    /**
     * 构建小模型统计对象
     */
    private SmallModelTabStatsVO buildSmallModelTab(List<ModelValidationDTO> list, int denominator) {
        SmallModelTabStatsVO tab = new SmallModelTabStatsVO();
        if (CollectionUtils.isEmpty(list)) {
            tab.setAlgorithmTotal(0);
            tab.setFirstPassRate(BigDecimal.ZERO);
            tab.setFinalPassRate(BigDecimal.ZERO);
            tab.setAlgorithmNames(Collections.emptyList());
            return tab;
        }

        long firstPassCount = list.stream()
                .filter(item -> Objects.equals(1L, item.getTestTimes()) && Objects.equals(YES, item.getPassed()))
                .count();
        long finalPassCount = list.stream()
                .filter(item -> Objects.equals(YES, item.getPassed()))
                .count();

        tab.setAlgorithmTotal(list.size());
        tab.setFirstPassRate(calculateRate(firstPassCount, denominator));
        tab.setFinalPassRate(calculateRate(finalPassCount, denominator));
        tab.setAlgorithmNames(list.stream()
                .map(ModelValidationDTO::getAlgorithmName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        return tab;
    }

    /**
     * 构建大模型子表统计
     */
    private List<LargeModelChildStatsVO> buildLargeModelChildren(List<LargeModelValidationDTO> modelRecords) {
        Map<String, List<LargeModelValidationDTO>> childMap = modelRecords.stream()
                .collect(Collectors.groupingBy(LargeModelValidationDTO::getDownstreamAlgorithm,
                        LinkedHashMap::new, Collectors.toList()));

        List<LargeModelChildStatsVO> children = new ArrayList<>();
        for (Map.Entry<String, List<LargeModelValidationDTO>> childEntry : childMap.entrySet()) {
            List<LargeModelValidationDTO> childRecords = childEntry.getValue();

            LargeModelChildStatsVO child = new LargeModelChildStatsVO();
            child.setDownstreamAlgorithmName(childRecords.get(0).getDownstreamAlgorithm());
            child.setFirstPassRate(childRecords.get(0).getFirstPassRate());
            child.setFinalPassRate(childRecords.get(0).getFinalPassRate());
            children.add(child);
        }
        return children;
    }

    /**
     * 构建查询时间范围
     */
    private Date[] buildDateRange(ZhongShiDateRangeDTO req) {
        Date start = startOfDay(req.getStartDate());
        Date end = endOfDay(req.getEndDate());
        log.info("构建查询时间范围，开始时间:{}，结束时间:{}", start, end);
        return new Date[]{start, end};
    }

    /**
     * 转换为当天开始时间
     */
    private Date startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 转换为当天结束时间
     */
    private Date endOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 百分比计算，保留两位小数。
     */
    private BigDecimal calculateRate(long numerator, int denominator) {
        if (denominator <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取当前登录用户
     */
    private SysUser getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            throw new CustomException("获取用户信息失败", HttpStatus.BAD_REQUEST);
        }
        return loginUser.getUser();
    }
}
