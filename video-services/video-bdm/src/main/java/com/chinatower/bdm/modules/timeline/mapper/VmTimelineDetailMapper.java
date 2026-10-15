package com.chinatower.bdm.modules.timeline.mapper;

import com.chinatower.bdm.modules.timeline.dto.VmTimelineDetailQueryDto;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 时间轴详情表(VmTimelineDetail)表数据库访问层
 *
 * @author makejava
 * @since 2024-05-27 17:16:15
 */
public interface VmTimelineDetailMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param timelineDetailId 主键
     * @return 实例对象
     */
    VmTimelineDetail queryById(Long timelineDetailId);

    /**
     * 查询指定行数据
     *
     * @param vmTimelineDetailQueryDto 查询条件
     * @return 对象列表
     */
    List<VmTimelineDetail> queryAllByLimit(VmTimelineDetailQueryDto vmTimelineDetailQueryDto);

    /**
     * 根据时间轴表ID批量查询时间轴详情
     *
     * @param timelineInfoIds 时间轴表ID列表
     * @return 对象列表
     */
    List<VmTimelineDetail> queryByTimelineInfoIds(List<Long> timelineInfoIds);

    /**
     * 新增数据
     *
     * @param vmTimelineDetail 实例对象
     * @return 影响行数
     */
    int insert(VmTimelineDetail vmTimelineDetail);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<VmTimelineDetail> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<VmTimelineDetail> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<VmTimelineDetail> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<VmTimelineDetail> entities);

    /**
     * 修改数据
     *
     * @param vmTimelineDetail 实例对象
     * @return 影响行数
     */
    int update(VmTimelineDetail vmTimelineDetail);

    /**
     * 通过主键删除数据
     *
     * @param timelineDetailId 主键
     * @return 影响行数
     */
    int deleteById(Long timelineDetailId);

    /**
     * 通过主键删除数据
     *
     * @param timelineId 主键
     * @return 影响行数
     */
    int deleteByTimeLineId(Long timelineId);

}

