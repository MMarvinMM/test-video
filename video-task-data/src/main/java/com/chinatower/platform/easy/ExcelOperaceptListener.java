package com.chinatower.platform.easy;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.easy.dto.OperateOrderEntityVo;
import com.chinatower.platform.elasticsearch.repository.OperateRepository;
import com.chinatower.platform.modules.AlarmSyncEsErrorCollector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/30 20:58
 */
@Slf4j
public class ExcelOperaceptListener extends AnalysisEventListener<OperateOrderEntityVo> {

    private static final int BATCH_SIZE = 5000;
    private ArrayList<OperateOrderEntityVo> salariesList = new ArrayList<>(BATCH_SIZE);
    private final OperateRepository operateRepository;

    private final AlarmSyncEsErrorCollector errorCollector;

    public ExcelOperaceptListener(OperateRepository operateRepository, AlarmSyncEsErrorCollector errorCollector) {
        this.operateRepository = operateRepository;
        this.errorCollector = errorCollector;
    }
    @Override
    public void invoke(OperateOrderEntityVo operateOrderEntityVo, AnalysisContext analysisContext) {
        salariesList.add(operateOrderEntityVo);
        if (salariesList.size()  >= BATCH_SIZE) {
            batchProcess();
            salariesList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!salariesList.isEmpty())  {
            batchProcess();
        }
    }

    private void batchProcess() {
        log.info("ExcelOperaceptListener数据总量:{}",salariesList.size());
        for (OperateOrderEntityVo vo:salariesList) {
            if(!StringUtils.isEmpty(vo.getModifierTime())){
                vo.setModifierTimeDate(DateUtil.parseDateTime(vo.getModifierTime()));
            }
            if(!StringUtils.isEmpty(vo.getAlarmTime())){
                vo.setAlarmTimeDate(DateUtil.parseDateTime(vo.getAlarmTime()));
            }
            if(!StringUtils.isEmpty(vo.getCompConfidenceLevel())){
                vo.setConfidenceLevelMax(vo.getCompConfidenceLevel());
            }
        }
        // 过滤空工单ID，避免 terms 查询无值异常
        List<String> orderIdList = salariesList.stream()
                .map(OperateOrderEntityVo::getWarningOrderId)
                .filter(id -> !StringUtils.isEmpty(id))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderIdList)) {
            QueryBuilder query = QueryBuilders.boolQuery();
            query = ((BoolQueryBuilder) query).must(QueryBuilders.termsQuery(Constants.PARAM_WARN_ORDER_ID, orderIdList));
            //删除包含此次新增数据中的历史数据，查询最新数据
            try {
                operateRepository.deleteAll(operateRepository.search(query));
            } catch (Exception e) {
                log.error("CentralNodeServiceImpl-删除运营研判单失败", e);
                try {
                    operateRepository.deleteAll(operateRepository.search(query));
                } catch (Exception e2) {
                    log.error("CentralNodeServiceImpl-删除运营研判单失败2", e2);
                    recordEsError(e2);
                }
            }
        }

        try {
            operateRepository.saveAll(salariesList);
        } catch (Exception e) {
            log.error("CentralNodeServiceImpl-插入运营研判失败", e);
            recordEsError(e);
        }
    }

    private void recordEsError(Exception error) {
        if (errorCollector != null) {
            errorCollector.recordError(error);
        }
    }
}
