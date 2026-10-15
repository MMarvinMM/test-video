package com.chinatower.bdm.modules.timeline.service;

import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoQueryDto;
import com.chinatower.bdm.modules.timeline.vo.VmTimelineDetailVo;
import com.chinatower.bdm.modules.timeline.vo.VmTimelineInfoVo;

import java.util.List;

/**
 * 时间轴基础表(VmTimelineInfo)表服务接口
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
public interface VmTimelineInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param timelineId 主键
     * @return 实例对象
     */
    VmTimelineInfoVo queryById(Long timelineId);

    /**
     * 分页查询
     *
     * @param vmTimelineInfoQueryDto 筛选条件
     * @return 查询结果
     */
    List<VmTimelineInfoVo> queryByPage(VmTimelineInfoQueryDto vmTimelineInfoQueryDto);

    /**
     * 分页查询带有时间轴详情
     *
     * @param vmTimelineInfoQueryDto 筛选条件
     * @return 查询结果
     */
    List<VmTimelineDetailVo> queryWithDetail(VmTimelineInfoQueryDto vmTimelineInfoQueryDto);

    /**
     * 新增数据
     *
     * @param vmTimelineInfoDto 实例对象
     * @return 实例对象
     */
    int insert(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 修改数据
     *
     * @param vmTimelineInfoDto 实例对象
     * @return 实例对象
     */
    int update(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 通过主键删除数据
     *
     * @param timelineId 主键
     * @return 是否成功
     */
    boolean deleteById(Long timelineId);

    /**
     * 设置默认时间轴
     *
     * @param timelineId 时间轴id
     * @return 是否成功
     */
    boolean updateDefaultTimelineById(VmTimelineInfoDto vmTimelineInfoDto);

    /**
     * 修改时间轴排序
     * 兼容老数据：如果数据库中存在sortNum为NULL的记录，会自动初始化排序号
     *
     * @param timelineInfoDto 时间轴信息
     * @return 是否成功
     */
    Boolean updateSortNumTimelineById(VmTimelineInfoDto timelineInfoDto);

    /**
     * 获取默认时间轴
     *
     * @return 时间轴
     */
    VmTimelineInfoVo queryDefaultTimeline(VmTimelineInfoDto vmTimelineInfoDto);

}
