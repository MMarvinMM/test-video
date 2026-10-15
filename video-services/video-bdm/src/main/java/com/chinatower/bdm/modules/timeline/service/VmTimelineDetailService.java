package com.chinatower.bdm.modules.timeline.service;

import com.chinatower.bdm.modules.timeline.dto.VmTimelineDetailQueryDto;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineDetail;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineInfo;

import java.util.List;

/**
 * 时间轴详情表(VmTimelineDetail)表服务接口
 *
 * @author makejava
 * @since 2024-05-27 17:16:16
 */
public interface VmTimelineDetailService {

    /**
     * 通过ID查询单条数据
     *
     * @param timelineDetailId 主键
     * @return 实例对象
     */
    VmTimelineDetail queryById(Long timelineDetailId);

    /**
     * 分页查询
     *
     * @param vmTimelineDetailQueryDto 筛选条件
     * @return 查询结果
     */
    List<VmTimelineDetail> queryByPage(VmTimelineDetailQueryDto vmTimelineDetailQueryDto);

    /**
     * 根据时间轴表ID批量查询时间轴详情
     *
     * @param timelineInfoIds 时间轴表ID列表
     * @return 查询结果
     */
    List<VmTimelineDetail> queryByTimelineInfoIds(List<Long> timelineInfoIds);

    /**
     * 新增数据
     *
     * @param vmTimelineDetail 实例对象
     * @return 实例对象
     */
    int insert(VmTimelineDetail vmTimelineDetail);

    /**
     * 新增数据
     *
     * @param vmTimelineInfo 时间轴对象
     * @param vmTimelineDetails 时间轴详情列表
     * @return 实例对象
     */
    int insertBatch(VmTimelineInfo vmTimelineInfo, List<VmTimelineDetail> vmTimelineDetails);

    /**
     * 修改数据
     *
     * @param vmTimelineDetail 实例对象
     * @return 实例对象
     */
    int update(VmTimelineDetail vmTimelineDetail);

    /**
     * 通过主键删除数据
     *
     * @param timelineDetailId 主键
     * @return 是否成功
     */
    boolean deleteById(Long timelineDetailId);

    /**
     * 通过主键删除数据
     *
     * @param timelineId 主键
     * @return 是否成功
     */
    boolean deleteByTimeLineId(Long timelineId);

}
