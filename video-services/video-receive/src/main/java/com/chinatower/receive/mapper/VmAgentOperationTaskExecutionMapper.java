package com.chinatower.receive.mapper;

import com.chinatower.receive.vo.entity.VmAgentOperationTaskExecutionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 智能研判任务执行过程记录表Mapper接口
 */
@Mapper
public interface VmAgentOperationTaskExecutionMapper {

    /**
     * 根据用户ID查询最新的执行记录ID
     * 通过关联查询获取用户相关任务的最新执行记录
     * 
     * @param taskId 用户任务id
     * @return 最新的执行记录ID
     */
    Long getLatestExecutionId(@Param("taskId") Long taskId,@Param("groupId") String groupId);
    
    /**
     * 根据任务ID列表查询最新的执行记录ID
     * 
     * @param taskIds 任务ID列表
     * @return 最新的执行记录ID
     */
    Long getLatestExecutionIdByTaskIds(@Param("taskIds") List<Long> taskIds);

    /**
     * 插入执行记录
     * 
     * @param execution 执行记录
     * @return 影响行数
     */
    Long insertExecution(VmAgentOperationTaskExecutionDO execution);

    VmAgentOperationTaskExecutionDO getLatestEndTaskExecutionByUserId(@Param("userId") String userId);

    List<VmAgentOperationTaskExecutionDO> getTaskExecutionByGroupId(@Param("groupId") String groupId);
}
