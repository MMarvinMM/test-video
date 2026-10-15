package com.chinatower.bdm.modules.inspection.dao;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperStr;
import com.chinatower.bdm.modules.inspection.entity.DeviceGridAreaTreeVO;
import com.chinatower.bdm.modules.inspection.entity.DeviceGridReqDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BdmInspectionDAO {
    InspectionConfVO findOne(String confId);

    int deleteConf(InspctionConfStatusParam vo);

    String checkConf(InspectionConfVO vo);

    int insertConfInfoList(List<InspectionConfVO> inspectionList);

    int insertConfInfo(InspectionConfVO vo);

    int updateConfInfo(InspectionConfVO vo);

    List<InspectionTaskListVO> getTaskList(InspectionTaskParam vo);

    List<InspectionTaskListVO> getTaskListByCondition(InspectionTaskNoPageParam vo);

    List<InspectionTaskListVO> getToDoTaskList(InspectionTaskNoPageParam vo);

    List<GridKeeperVO> getKeeperInfo(GridKeeperVO vo);

    List<InspectionConfListVO> getConfList(InspectionConfParam param);

    InspectionTaskVO getTaskInfo(String taskId);

    InspectionTaskVO getTaskInfoByCondition(InspectionTaskNoPageParam inspectionTaskNoPageParam);

    int updateTask(InspectionTaskVO vo);

    GridKeeperStr selectKeeperIdStr(InspectionConfVO inspectionConfVO);

    void updateConfStatus(String confId);

    List<InspectionTaskParamVO> getTaskStatus(String taskId);

    GridVO getParentGridInfo(Long gridId);

    List<InspectionTaskVO> CheckTaskByConfId(String confId);

    /**
     * 批量插入任务
     * @return
     */
    int batchInsertTaskInfo( @Param("list") List<InspectionConfVO> list);

    void updateRepeatConfStatus(InspectionConfVO inspectionConfVO);

    InspectionConfVO getConfStatus(String confId);


    void delTask(List<String> list);

    List<InspectionTaskVO> getTaskInfoByConfId(String confId);

    List<DeviceGridAreaTreeVO> queryGridAreaTree(DeviceGridReqDTO deviceGridReqDTO);

    String getTaskStatusName(@Param("taskStatus") String taskStatus,@Param("dictType") String dictType);

    List<String> getUserIdByKeerperList(List<String> list);

    List<InspectionTaskListVOExtend> getTaskListExtend(InspectionTaskParam vo);

    @MapKey("label")
    Map inspectionCountTrend(QueryCountTrend queryCountTrend);


    String getUUID();

    int deleteTask(InspctionConfStatusParam vo);


    List<InspectionTaskVO> getConfKeeperList(InspectionTaskVO vo);
    int updateConfKeeperList(List<InspectionTaskVO> list);
    List<InspectionTaskVO> getTaskKeeperList(InspectionTaskVO vo);
    int updateTaskKeeperList(List<InspectionTaskVO> list);

    List<InspectionTaskVO> getUserInfoList(InspectionTaskVO vo);
}
