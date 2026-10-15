package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.AiJudgePendingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警分级研判定时任务 DAO
 */
@Mapper
public interface AiJudgeAlarmDAO {

    /**
     * 查询未研判告警（按告警时间升序）
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AiJudgePendingVO> selectPendingList(@Param("tableName") String tableName,
                                             @Param("limit") Integer limit);

    /**
     * 乐观更新：未研判 → 研判中
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateStatusToProcessing(@Param("tableName") String tableName,
                                 @Param("warningOrderId") String warningOrderId,
                                 @Param("fromStatus") Integer fromStatus,
                                 @Param("toStatus") Integer toStatus);

    /**
     * 更新研判状态（失败/重置等）
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateAiJudgeStatus(@Param("tableName") String tableName,
                            @Param("warningOrderId") String warningOrderId,
                            @Param("aiJudgeStatus") Integer aiJudgeStatus);

    /**
     * 将长时间停留在研判中的数据重置为未研判，避免服务挂掉后消息丢失且状态卡死
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int resetStuckProcessing(@Param("tableName") String tableName,
                             @Param("processingStatus") Integer processingStatus,
                             @Param("pendingStatus") Integer pendingStatus,
                             @Param("timeoutMinutes") Integer timeoutMinutes);

    /**
     * 调 Dify 前写入气象等到智能体扩展字段
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateAgentExtendField(@Param("tableName") String tableName,
                               @Param("warningOrderId") String warningOrderId,
                               @Param("agentExtendField") String agentExtendField);
}
