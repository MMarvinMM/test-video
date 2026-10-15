package com.chinatower.bdm.modules.timeline.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.modules.timeline.dto.VmTimelineDetailQueryDto;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineDetail;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineInfo;
import com.chinatower.bdm.modules.timeline.mapper.VmTimelineDetailMapper;
import com.chinatower.bdm.modules.timeline.service.VmTimelineDetailService;
import com.chinatower.common.core.exception.BaseException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 时间轴详情表(VmTimelineDetail)表服务实现类
 *
 * @author makejava
 * @since 2024-05-27 17:16:16
 */
@Slf4j
@Service("vmTimelineDetailService")
public class VmTimelineDetailServiceImpl implements VmTimelineDetailService {
    @Resource
    private VmTimelineDetailMapper vmTimelineDetailMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param timelineDetailId 主键
     * @return 实例对象
     */
    @Override
    public VmTimelineDetail queryById(Long timelineDetailId) {
        return vmTimelineDetailMapper.queryById(timelineDetailId);
    }

    /**
     * 分页查询
     *
     * @param vmTimelineDetailQueryDto 筛选条件
     * @return 查询结果
     */
    @DataScope(tenantColumn = "t.tenant_id",industryCode = "t.industry_code")
    @Override
    public List<VmTimelineDetail> queryByPage(VmTimelineDetailQueryDto vmTimelineDetailQueryDto) {
        //获取分页
        Integer pageNum = vmTimelineDetailQueryDto.getPageNum();
        Integer pageSize = vmTimelineDetailQueryDto.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        return vmTimelineDetailMapper.queryAllByLimit(vmTimelineDetailQueryDto);
    }

    /**
     * 根据时间轴表ID批量查询时间轴详情
     *
     * @param timelineInfoIds 时间轴表ID列表
     * @return 查询结果
     */
    @Override
    public List<VmTimelineDetail> queryByTimelineInfoIds(List<Long> timelineInfoIds) {
        if (CollectionUtils.isEmpty(timelineInfoIds)) {
            return Lists.newArrayList();
        }
        return vmTimelineDetailMapper.queryByTimelineInfoIds(timelineInfoIds);
    }

    /**
     * 新增数据
     *
     * @param vmTimelineDetail 实例对象
     * @return 实例对象
     */
    @Transactional
    @Override
    public int insert(VmTimelineDetail vmTimelineDetail) {
        log.info("timeline detail info add: {}", JSON.toJSONString(vmTimelineDetail));

        LoginUser loginUser = SecurityUtils.getLoginUser();
        vmTimelineDetail.setTenantId(loginUser.getUser().getTenantId());
        vmTimelineDetail.setTenantName(loginUser.getUser().getTenantName());
        vmTimelineDetail.setAppVerCode(loginUser.getUser().getAppVerCode());
        vmTimelineDetail.setIndustryCode(loginUser.getUser().getIndustryCode());
        vmTimelineDetail.setDeleteFlag(0);
        vmTimelineDetail.setCreator(loginUser.getUsername());
        vmTimelineDetail.setCreateTime(DateUtils.getNowDate());
        return vmTimelineDetailMapper.insert(vmTimelineDetail);
    }

    /**
     * 新增数据
     *
     * @param vmTimelineDetails 实例对象
     * @return 实例对象
     */
    @Transactional
    @Override
    public int insertBatch(VmTimelineInfo vmTimelineInfo, List<VmTimelineDetail> vmTimelineDetails) {
        log.info("timeline detail info add batch: {}", JSON.toJSONString(vmTimelineDetails));
        if (CollectionUtils.isEmpty(vmTimelineDetails)) {
            log.info("timeline detail info add batch vmTimelineDetails is null");
            return -1;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //防止详情表和主表冗余字段不一样，做处理
        for (VmTimelineDetail vmTimelineDetail : vmTimelineDetails) {
            vmTimelineDetail.setTimelineId(vmTimelineInfo.getTimelineId());
            vmTimelineDetail.setTenantId(StringUtils.isNotEmpty(vmTimelineInfo.getTenantId())
                    ? vmTimelineInfo.getTenantId() : loginUser.getUser().getTenantId());
            vmTimelineDetail.setTenantName(StringUtils.isNotEmpty(vmTimelineInfo.getTenantName())
                    ? vmTimelineInfo.getTenantName() : loginUser.getUser().getTenantName());
            vmTimelineDetail.setAppVerCode(StringUtils.isNotEmpty(vmTimelineInfo.getAppVerCode())
                    ? vmTimelineInfo.getAppVerCode() : loginUser.getUser().getAppVerCode());
            vmTimelineDetail.setIndustryCode(StringUtils.isNotEmpty(vmTimelineInfo.getIndustryCode())
                    ? vmTimelineInfo.getIndustryCode() : loginUser.getUser().getIndustryCode());
            vmTimelineDetail.setDeleteFlag(0);
            vmTimelineDetail.setCreator(loginUser.getUsername());
            vmTimelineDetail.setCreateTime(DateUtils.getNowDate());
        }
        return vmTimelineDetailMapper.insertBatch(vmTimelineDetails);
    }
    /**
     * 修改数据
     *
     * @param vmTimelineDetail 实例对象
     * @return 实例对象
     */
    @Transactional
    @Override
    public int update(VmTimelineDetail vmTimelineDetail) {
        log.info("timeline detail info add: {}", JSON.toJSONString(vmTimelineDetail));
        if (Objects.isNull(vmTimelineDetail.getTimelineDetailId())) {
            throw new BaseException("修改数据失败，主键为空");
        }
        VmTimelineDetail timelineDetailDb = vmTimelineDetailMapper.queryById(vmTimelineDetail.getTimelineDetailId());
        if (Objects.isNull(timelineDetailDb)) {
            throw new BaseException("修改失败，数据不存在");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        vmTimelineDetail.setModifier(loginUser.getUsername());
        vmTimelineDetail.setUpdateTime(DateUtils.getNowDate());
        return vmTimelineDetailMapper.update(vmTimelineDetail);
    }

    /**
     * 通过主键删除数据
     *
     * @param timelineDetailId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long timelineDetailId) {
        return vmTimelineDetailMapper.deleteById(timelineDetailId) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param timelineId 主键
     * @return 是否成功
     */
    @Transactional
    @Override
    public boolean deleteByTimeLineId(Long timelineId) {
        return vmTimelineDetailMapper.deleteByTimeLineId(timelineId) > 0;
    }
}
