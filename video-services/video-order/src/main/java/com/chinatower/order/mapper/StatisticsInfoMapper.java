package com.chinatower.order.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@DS("statistic")
public interface StatisticsInfoMapper {

    void callTransferIndustryStatisticsInfo(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                                      @Param("targetIndustryCode") String targetIndustryCode);
}
