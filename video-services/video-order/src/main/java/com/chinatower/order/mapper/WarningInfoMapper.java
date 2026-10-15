package com.chinatower.order.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
@DS("platform")
public interface WarningInfoMapper {

    void callTransferIndustryWarnInfo(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                                      @Param("targetIndustryCode") String targetIndustryCode);

    int transferOverDueRemindInfo(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                                  @Param("targetIndustryCode") String targetIndustryCode);

    void transferIndustryOrderInfo(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                                   @Param("targetIndustryCode") String targetIndustryCode,
                                   @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    int transferAlarmFlowConfig(@Param("tenantId") String tenantId,
                                @Param("sourceIndustryCode") String sourceIndustryCode,
                                @Param("sourceAppVerCode") String sourceAppVerCode,
                                @Param("targetIndustryCode") String targetIndustryCode,
                                @Param("targetAppVerCode") String targetAppVerCode);
}
