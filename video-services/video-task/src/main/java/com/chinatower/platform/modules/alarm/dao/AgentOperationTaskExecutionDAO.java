package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.VmJudgeTaskExecutionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS(EbikeDataBaseConstant.PLATFORM)
public interface AgentOperationTaskExecutionDAO {

    /**
     * 查询用户是否有执行中的任务
     * @param userId
     * @return
     */
    List<VmJudgeTaskExecutionDO> selectByUserId(String userId);

    /**
     * 插入研判任务执行记录
     * @param vmJudgeTaskExecutionDO
     * @return
     */
    Long insertExecution(VmJudgeTaskExecutionDO vmJudgeTaskExecutionDO);

    /**
     * 更新研判任务执行记录
     * @param updateExecution
     * @return
     */
    int updateExecution(VmJudgeTaskExecutionDO updateExecution);

    /**
     * 批量查询用户最新AI研判执行的记录
     * @param closeTaskUser
     * @return
     */
    List<VmJudgeTaskExecutionDO> selectByUserIds(@Param("closeTaskUser") List<String> closeTaskUser,@Param("executionStatus") Integer executionStatus ,@Param("taskExecutionType") String taskExecutionType);


}
