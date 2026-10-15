package com.chinatower.bdm.modules.timeline.mapper;

import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoQueryDto;
import com.chinatower.bdm.modules.timeline.entity.VmSensingAlgorithmTaskInfo;
import com.chinatower.bdm.modules.timeline.vo.VmSensingAlgorithmTaskInfoVo;

import java.util.List;

/**
 * 遥感算法任务信息表(VmSensingAlgorithmTaskInfo)表数据库访问层
 *
 * @author makejava
 * @since 2024-05-27
 */
public interface VmSensingAlgorithmTaskInfoMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    VmSensingAlgorithmTaskInfo queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param queryDto 查询条件
     * @return 对象列表
     */
    List<VmSensingAlgorithmTaskInfoVo> queryAllByLimit(VmSensingAlgorithmTaskInfoQueryDto queryDto);

    /**
     * 新增数据
     *
     * @param taskInfo 实例对象
     * @return 影响行数
     */
    int insert(VmSensingAlgorithmTaskInfo taskInfo);

    /**
     * 修改数据
     *
     * @param taskInfo 实例对象
     * @return 影响行数
     */
    int update(VmSensingAlgorithmTaskInfo taskInfo);

    /**
     * 通过主键删除数据（软删除）
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 取消所有任务的默认状态
     *
     * @return 影响行数
     */
    int updateAllDefaultFlagToZero(VmSensingAlgorithmTaskInfoDto taskInfoDto);

}
