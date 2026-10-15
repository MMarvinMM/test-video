package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dataobject.AgentOperationTaskReminderDO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS(EbikeDataBaseConstant.PLATFORM)
public interface AgentOperationTaskReminderDAO {


    /**
     * 插入记录
     */
    int insert(AgentOperationTaskReminderDO record);

    /**
     * 批量插入记录
     */
    int batchInsert(@Param("list") List<AgentOperationTaskReminderDO> list);


    /**
     * 更新发送状态
     */
    int updateSendStatus(@Param("reminderId") Long reminderId,
                         @Param("sendStatus") Integer sendStatus,
                         @Param("errorMessage") String errorMessage);

    /**
     * 批量更新发送状态
     */
    int batchUpdateSendStatus(@Param("list") List<AgentOperationTaskReminderDO> list);

    /**
     * 查询通知记录消息
     * @param historyList
     * @return
     */
    List<AgentOperationTaskReminderDO> selectReminderList(@Param("historyList") List<Long> historyList);
}
