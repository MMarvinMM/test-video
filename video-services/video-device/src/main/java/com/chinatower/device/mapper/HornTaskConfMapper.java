package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dto.HornTaskPlayParamDTO;
import com.chinatower.device.entity.dto.VmHornTaskConfDO;
import com.chinatower.device.entity.dto.VmHornTaskDeviceRelDO;
import com.chinatower.device.entity.dto.VmHornTaskDiffNodeRelDO;
import com.chinatower.device.entity.dto.VmHornTaskStrategyConfDO;
import com.chinatower.device.entity.dto.VmHornTaskContentRelDO;
import com.chinatower.device.entity.vo.HornTaskConfVO;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 广播任务mapper
 */
@Mapper
public interface HornTaskConfMapper extends BaseMapper<VmHornTaskConfDO> {

    /**
     * 根据条件查询广播任务列表
     *
     * @param hornTaskConfVO 广播任务配置对象
     * @return 返回符合条件的广播任务列表
     */
    List<HornTaskConfVO> getListByCondition(HornTaskConfVO hornTaskConfVO);

    /**
     * 新增广播任务
     *
     * @param hornTaskConfVO 广播任务配置信息
     * @return 返回新增任务配置的ID
     */
    int addTaskConf(HornTaskConfVO hornTaskConfVO);

    int batchInsertContentRel(@Param("list") List<VmHornTaskContentRelDO> contentIdList, @Param("taskId") Long taskId);

    int batchInsertDeviceRel(@Param("list") List<String> deviceCodeList, @Param("taskId") Long taskId);

    int batchInsertStrategyRel(@Param("list") List<VmHornTaskStrategyConfDO> strategyConfVOList, @Param("taskId") Long taskId);

    /**
     * 修改广播任务
     *
     * @param hornTaskConfVO 广播任务配置信息
     * @return 返回修改结果，成功为1，失败为0
     */
    int modifyTaskConf(HornTaskConfVO hornTaskConfVO);

    List<VmHornTaskContentRelDO> selectContentRelByTaskId(@Param("taskId") Long taskId);

    List<VmHornTaskDeviceRelDO> selectDeviceRelByTaskId(@Param("taskId") Long taskId);

    List<VmHornTaskStrategyConfDO> selectStrategyRelByTaskId(@Param("taskId") Long taskId);

    int deleteContentRelByTaskId(@Param("taskId") Long taskId);

    int deleteDeviceRelByTaskId(@Param("taskId") Long taskId);

    int deleteStrategyRelByTaskId(@Param("taskId") Long taskId);

    void setHornContentRelTxtIdByTaskId(@Param("taskId") Long taskId, @Param("txtId") String txtId);

    VmHornTaskConfDO selectByTaskId(@Param("taskId") Long taskId);

    int updateByTaskId(VmHornTaskConfDO vmHornTaskConfDO);

    VmHornTaskConfDO selectTaskByName(@Param("taskName") String taskName, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    int addTaskPlayParam(@Param("list") List<HornTaskPlayParamDTO> list);

    List<HornTaskPlayParamDTO> selectTaskPlayParam(@Param("fileIdList") List<String> fileIdList);

    int deleteTaskPlayParam(@Param("fileIdList") List<String> fileIdList);

    List<DeviceInfoVO> getDeviceInfoByDeviceCodeList(@Param("deviceCodeList") List<String> deviceCodeList);

    void saveHornTaskDiffNodeRelBatch(@Param("list") List<VmHornTaskDiffNodeRelDO> list, @Param("taskId") Long taskId);

    VmHornTaskContentRelDO selectHornTaskContentRelByTxtId(@Param("txtId") String txtId);

    List<VmHornTaskDiffNodeRelDO> selectDiffNodeRelDOsByTaskId(@Param("taskId") Long taskId);

    void updateDiffNodeRelStatus(@Param("hornTaskId") String hornTaskId, @Param("executStatus") Integer executStatus);

    int updateHornTaskDiffNodeHornTaskId(@Param("taskId") Long taskId, @Param("hornTaskId") String hornTaskId, @Param("accessNode") String accessNode);

    void deleteDiffNodeRelByTaskId(@Param("taskId") Long taskId);

    void moveDiffNodeRelToHis(@Param("taskId") Long taskId, @Param("batchId") String batchId);

    void saveFailReason(@Param("taskId") Long taskId, @Param("pushFailReason") String pushFailReason, @Param("accessNode") String accessNode);

    SysIndustryConf getByIndustryCode(@Param("industryCode") String industryCode);

    List<HornTaskConfVO> getAllHornTask(HornTaskConfVO hornTaskConfVO);

    VmHornTaskConfDO selectById(@Param("taskId") Long taskId);
}
