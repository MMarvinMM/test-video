package com.chinatower.bdm.modules.timeline.mapper;

import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoDto;
import com.chinatower.bdm.modules.timeline.vo.VmTimelineDetailVo;
import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoQueryDto;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineInfo;
import com.chinatower.bdm.modules.timeline.vo.VmTimelineInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 时间轴基础表(VmTimelineInfo)表数据库访问层
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
public interface VmTimelineInfoMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param timelineId 主键
     * @return 实例对象
     */
    VmTimelineInfo queryById(Long timelineId);

    /**
     * 查询指定行数据
     *
     * @param vmTimelineInfoQueryDto 查询条件
     * @return 对象列表
     */
    List<VmTimelineInfoVo> queryAllByLimit(VmTimelineInfoQueryDto vmTimelineInfoQueryDto);

    /**
     * 查询指定行数据
     *
     * @param vmTimelineInfoQueryDto 查询条件
     * @return 对象列表
     */
    List<VmTimelineDetailVo> queryAllDetail(VmTimelineInfoQueryDto vmTimelineInfoQueryDto);

    /**
     * 新增数据
     *
     * @param vmTimelineInfo 实例对象
     * @return 影响行数
     */
    int insert(VmTimelineInfo vmTimelineInfo);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<VmTimelineInfo> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<VmTimelineInfo> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<VmTimelineInfo> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<VmTimelineInfo> entities);

    /**
     * 修改数据
     *
     * @param vmTimelineInfo 实例对象
     * @return 影响行数
     */
    int update(VmTimelineInfo vmTimelineInfo);

    /**
     * 通过主键删除数据
     *
     * @param timelineId 主键
     * @return 影响行数
     */
    int deleteById(Long timelineId);

    /**
     * 取消所有时间轴的默认状态
     *
     * @return 影响行数
     */
    int updateAllDefaultFlagToZero(VmTimelineInfoDto vmTimelineInfoDto);

    VmTimelineInfo queryDefaultTimeLine(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 查询所有时间轴（用于初始化老数据的排序号）
     *
     * @return 时间轴列表
     */
    List<VmTimelineInfo> queryAllForSortInit(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 查询sortNum为NULL的时间轴记录（用于初始化老数据）
     *
     * @return 时间轴列表（只包含sortNum为NULL的记录）
     */
    List<VmTimelineInfo> queryNullSortNumList();

    /**
     * 查询最小排序值
     *
     * @return 最小排序值
     */
    Integer queryMinSortNum(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 统计sortNum为NULL的数量
     *
     * @return 数量
     */
    int countNullSortNum(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 统计时间轴数量
     *
     * @return 数量
     */
    int countAllTimelines(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 将指定范围内的排序值+1（用于向上移动，腾出位置）
     *
     * @param start 起始排序值
     * @param end 结束排序值
     * @param excludeId 排除的时间轴ID
     * @return 影响行数
     */
    int incrementSortNumRange(@Param("start") Integer start,
                              @Param("end") Integer end,
                              @Param("excludeId") Long excludeId,
                              @Param("data") VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 将指定范围内的排序值-1（用于向后移动，填补空位）
     *
     * @param start 起始排序值
     * @param end 结束排序值
     * @param excludeId 排除的时间轴ID
     * @return 影响行数
     */
    int decrementSortNumRange(@Param("start") Integer start,
                              @Param("end") Integer end,
                              @Param("excludeId") Long excludeId,
                              @Param("data") VmTimelineInfoDto vmTimelineInfoDto);

    void updateSortNum(@Param("updateInfo") VmTimelineInfo updateInfo,@Param("data") VmTimelineInfoDto vmTimelineInfoDto);
}

