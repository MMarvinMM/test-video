package com.chinatower.event.mapper;

import com.chinatower.event.dto.BatchDelWarningOrderDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderBatchMapper.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/29 17:02
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Mapper
public interface WarningOrderBatchMapper {

    /**
     * 按月批量删除告警数据
     * @param dto
     */
    int batchUpdateWarningOrder(@Param("param") BatchDelWarningOrderDTO dto);

    /**
     * 按月批量删除告警流程数据
     * @param dto
     * @return
     */
    int batchUpdateFlowRecord(@Param("param") BatchDelWarningOrderDTO dto);

    /**
     * 数据批量插入到回收站
     * @param dto
     * @return
     */
    int batchAddIntercept(@Param("param") BatchDelWarningOrderDTO dto);

    /**
     * 回收站数据批量删除
     * @param dto
     * @return
     */
    int batchDeleteIntercept(@Param("param") BatchDelWarningOrderDTO dto);

    /**
     * 领导批示数据批量删除
     * @param dto
     * @return
     */
    int batchDeleteLeaderAudit(@Param("param") BatchDelWarningOrderDTO dto);

    /**
     * 根据工单ID批量更新收藏表告警状态
     */
    int batchUpdateWarningOrderCollection(@Param("warningOrderIdList") List<String> warningOrderIdList, @Param("alarmReason") String alarmReason);
}
