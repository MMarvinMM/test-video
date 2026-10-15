package com.chinatower.bdm.modules.timeline.service;

import com.chinatower.bdm.api.entity.AlgorithmPicResultVO;
import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoQueryDto;
import com.chinatower.bdm.modules.timeline.vo.VmSensingAlgorithmTaskInfoVo;
import com.chinatower.event.entity.vo.SecondRecogBackVO;

import java.util.List;

/**
 * 遥感算法任务信息表(VmSensingAlgorithmTaskInfo)表服务接口
 *
 * @author makejava
 * @since 2024-05-27
 */
public interface VmSensingAlgorithmTaskInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    VmSensingAlgorithmTaskInfoVo queryById(Long id);

    /**
     * 分页查询
     *
     * @param queryDto 筛选条件
     * @return 查询结果
     */
    List<VmSensingAlgorithmTaskInfoVo> queryByPage(VmSensingAlgorithmTaskInfoQueryDto queryDto);

    /**
     * 新增数据
     *
     * @param taskInfoDto 实例对象
     * @return 影响行数
     */
    int insert(VmSensingAlgorithmTaskInfoDto taskInfoDto);

    /**
     * 修改数据
     *
     * @param taskInfoDto 实例对象
     * @return 影响行数
     */
    int update(VmSensingAlgorithmTaskInfoDto taskInfoDto);

    /**
     * 通过主键删除数据（软删除）
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 设置默认任务
     *
     * @return 是否成功
     */
    boolean updateDefaultTaskById(VmSensingAlgorithmTaskInfoDto taskInfoDto);

    /**
     * 更新算法仓图片识别结果
     *
     * @return 是否成功
     */
    boolean updateAlgorithmPicResult(AlgorithmPicResultVO algorithmPicResultVO);
}
