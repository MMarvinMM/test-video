package com.chinatower.bdm.modules.timeline.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.timeline.mapper.VmTimelineInfoMapper;
import com.chinatower.bdm.modules.timeline.vo.VmTimelineDetailVo;
import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoQueryDto;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineDetail;
import com.chinatower.bdm.modules.timeline.entity.VmTimelineInfo;
import com.chinatower.bdm.modules.timeline.service.VmTimelineDetailService;
import com.chinatower.bdm.modules.timeline.service.VmTimelineInfoService;
import com.chinatower.bdm.modules.timeline.vo.VmTimelineInfoVo;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 时间轴基础表(VmTimelineInfo)表服务实现类
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
@Slf4j
@Service("vmTimelineInfoService")
public class VmTimelineInfoServiceImpl implements VmTimelineInfoService {
    @Resource
    private VmTimelineInfoMapper vmTimelineInfoMapper;

    @Resource
    private VmTimelineDetailService vmTimelineDetailService;

    /**
     * 通过ID查询单条数据
     *
     * @param timelineId 主键
     * @return 实例对象
     */
    @Override
    public VmTimelineInfoVo queryById(Long timelineId) {
        VmTimelineInfoVo vmTimelineInfoVo = new VmTimelineInfoVo();
        VmTimelineInfo vmTimelineInfo = vmTimelineInfoMapper.queryById(timelineId);
        if (Objects.isNull(vmTimelineInfo)) {
            throw new CustomException(Constants.TIME_LINE_EXCEPTION.TIMELINE_NOT_FOUND);
        }
        //复制数据库的值到VO对象
        BeanUtils.copyBeanProp(vmTimelineInfoVo, vmTimelineInfo);
        //查询详情
        vmTimelineInfoVo.setTimelineDetailList(
                vmTimelineDetailService.queryByTimelineInfoIds(Collections.singletonList(timelineId)).stream()
                        //根据sortNum排序
                        .sorted(Comparator.comparing(s -> Integer.parseInt(s.getSortNum()))).collect(Collectors.toList())
        );
        return vmTimelineInfoVo;
    }

    /**
     * 分页查询
     *
     * @param vmTimelineInfoQueryDto 筛选条件
     * @return 查询结果
     */
    @Override
    public List<VmTimelineInfoVo> queryByPage(VmTimelineInfoQueryDto vmTimelineInfoQueryDto) {
        //获取分页
        Integer pageNum = vmTimelineInfoQueryDto.getPageNum();
        Integer pageSize = vmTimelineInfoQueryDto.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<VmTimelineInfoVo> vmTimelineInfos = vmTimelineInfoMapper.queryAllByLimit(vmTimelineInfoQueryDto);
        //拼装时间轴详情
        if (CollectionUtils.isEmpty(vmTimelineInfos)) {
            return Lists.newArrayList();
        }
        if (Constants.TIME_LIME_CONSTANT.SHOW_DETAIL_N.equals(vmTimelineInfoQueryDto.getShowDetail())) {
            return vmTimelineInfos;
        }
        //把列表组装成map, key->timelineInfoId 时间轴ID, value->时间轴详情列表
        Map<Long, List<VmTimelineDetail>> detailMap = vmTimelineDetailService.queryByTimelineInfoIds(
                vmTimelineInfos.stream().map(VmTimelineInfoVo::getTimelineId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(
                        VmTimelineDetail::getTimelineId, v -> {
                            List<VmTimelineDetail> vmTimelineInfos1 = Lists.newArrayList();
                            vmTimelineInfos1.add(v);
                            return vmTimelineInfos1;
                        },
                        (v1, v2) -> {
                            v1.addAll(v2);
                            //根据sortNum排序
                            v1.sort(Comparator.comparing(s -> Integer.parseInt(s.getSortNum())));
                            return v1;
                        }
                ));
        //把获取的数据拼装到列表中
        vmTimelineInfos.forEach(vmTimelineInfo1 -> vmTimelineInfo1.setTimelineDetailList(detailMap.getOrDefault(vmTimelineInfo1.getTimelineId(), Lists.newArrayList())));
        return vmTimelineInfos;
    }

    /**
     * 分页查询带有时间轴详情
     *
     * @param vmTimelineInfoQueryDto 筛选条件
     * @return 查询结果
     */
    @Override
    public List<VmTimelineDetailVo> queryWithDetail(VmTimelineInfoQueryDto vmTimelineInfoQueryDto) {
        //获取分页
        Integer pageNum = vmTimelineInfoQueryDto.getPageNum();
        Integer pageSize = vmTimelineInfoQueryDto.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        return vmTimelineInfoMapper.queryAllDetail(vmTimelineInfoQueryDto);
    }

    /**
     * 新增数据
     * 新增时自动初始化排序号为最小值（0），确保新数据排在最前面
     *
     * @param vmTimelineInfoDto 实例对象
     * @return 实例对象
     */
    @Transactional
    @Override
    public int insert(VmTimelineInfoDto vmTimelineInfoDto) {
        log.info("timeline info add: {}", JSON.toJSONString(vmTimelineInfoDto));
        //校验时间轴信息
        checkTimelineInfo(vmTimelineInfoDto, Constants.TIME_LIME_CONSTANT.ADD);
        VmTimelineInfo vmTimelineInfo = new VmTimelineInfo();
        BeanUtils.copyBeanProp(vmTimelineInfo, vmTimelineInfoDto);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        vmTimelineInfo.setTenantId(loginUser.getUser().getTenantId());
        vmTimelineInfo.setTenantName(loginUser.getUser().getTenantName());
        vmTimelineInfo.setAppVerCode(loginUser.getUser().getAppVerCode());
        vmTimelineInfo.setIndustryCode(loginUser.getUser().getIndustryCode());
        vmTimelineInfo.setDeleteFlag(0);
        vmTimelineInfo.setCreator(loginUser.getUsername());
        vmTimelineInfo.setCreateTime(DateUtils.getNowDate());
        // 新增数据时，将现有数据整体后移一位，保证新数据排在最前面
        vmTimelineInfoMapper.incrementSortNumRange(1, null, null,vmTimelineInfoDto);
        vmTimelineInfo.setSortNum(1);
        vmTimelineInfo.setUpdateTime(DateUtils.getNowDate());
        vmTimelineInfo.setModifier(loginUser.getUsername());
        log.info("新增时间轴，初始化排序号: sortNum={}", 1);
        int insertCount = vmTimelineInfoMapper.insert(vmTimelineInfo);
        if (insertCount > 0) {
            vmTimelineDetailService.insertBatch(vmTimelineInfo, vmTimelineInfoDto.getTimelineDetailList());
        }
        return insertCount;
    }

    /**
     *  校验时间轴信息
     *
     * @param vmTimelineInfoDto 时间轴对象
     */
    private void checkTimelineInfo(VmTimelineInfoDto vmTimelineInfoDto, String addOrUpdate) {
        if (Objects.isNull(vmTimelineInfoDto)) {
            throw new CustomException(Constants.TIME_LINE_EXCEPTION.TIMELINE_CONFIG_EMPTY);
        }
        //校验时间轴详情列表，修改不用校验详情是否为空
        if (Constants.TIME_LIME_CONSTANT.ADD.equals(addOrUpdate)
                && CollectionUtils.isEmpty(vmTimelineInfoDto.getTimelineDetailList())) {
            throw new CustomException(Constants.TIME_LINE_EXCEPTION.TIMELINE_DETAIL_LIST_EMPTY);
        }
        if (StringUtils.isEmpty(vmTimelineInfoDto.getTimelineName())) {
            throw new CustomException(Constants.TIME_LINE_EXCEPTION.TIMELINE_NAME_EMPTY);
        }
        if (CollectionUtils.isEmpty(vmTimelineInfoDto.getTimelineDetailList())) {
            //时间轴详情为空，跳过
            return;
        }
        int timelineCount = 0;
        for (VmTimelineDetail timelineDetail : vmTimelineInfoDto.getTimelineDetailList()) {
            ++timelineCount;
            if (StringUtils.isEmpty(timelineDetail.getLayer())) {
                throw new CustomException(String.format(Constants.TIME_LINE_EXCEPTION.LAYER_INFO_EMPTY, timelineCount));
            }
            if (StringUtils.isEmpty(timelineDetail.getYear())) {
                throw new CustomException(String.format(Constants.TIME_LINE_EXCEPTION.YEAR_INFO_EMPTY, timelineCount));
            }
            if (!timelineDetail.getYear().matches(Constants.TIME_LIME_CONSTANT.PATTERN_YEAR)) {
                //校验年份1900-2099
                throw new CustomException(
                        String.format(Constants.TIME_LINE_EXCEPTION.YEAR_FORMAT_ERROR, timelineCount, timelineDetail.getYear()));
            }
            if (StringUtils.isNotEmpty(timelineDetail.getMonth())
                    && !timelineDetail.getMonth().matches(Constants.TIME_LIME_CONSTANT.PATTERN_MONTH)) {
                //校验月份1-12 01-09， 1-9， 10， 11， 12
                throw new CustomException(
                        String.format(Constants.TIME_LINE_EXCEPTION.MONTH_FORMAT_ERROR, timelineCount, timelineDetail.getMonth()));
            }
            if (StringUtils.isNotEmpty(timelineDetail.getDay())
                    && !timelineDetail.getDay().matches(Constants.TIME_LIME_CONSTANT.PATTERN_DAY)) {
                //校验月份1-31 格式 01-09,1-9，10-19,20-29,30,31
                throw new CustomException(
                        String.format(Constants.TIME_LINE_EXCEPTION.DAY_FORMAT_ERROR, timelineCount, timelineDetail.getDay()));
            }
        }
    }

    /**
     * 修改数据
     *
     * @param vmTimelineInfoDto 实例对象
     * @return 实例对象
     */
    @Transactional
    @Override
    public int update(VmTimelineInfoDto vmTimelineInfoDto) {
        log.info("timeline info update: {}", JSON.toJSONString(vmTimelineInfoDto));
        //校验时间轴信息
        checkTimelineInfo(vmTimelineInfoDto, Constants.TIME_LIME_CONSTANT.UPDATE);
        if (Objects.isNull(vmTimelineInfoDto.getTimelineId())) {
            throw new CustomException(Constants.TIME_LINE_EXCEPTION.UPDATE_ID_EMPTY);
        }
        VmTimelineInfo timelineInfoDb = vmTimelineInfoMapper.queryById(vmTimelineInfoDto.getTimelineId());
        if (Objects.isNull(timelineInfoDb)) {
            throw new CustomException(Constants.TIME_LINE_EXCEPTION.UPDATE_DATA_NOT_EXIST);
        }
        VmTimelineInfo vmTimelineInfo = new VmTimelineInfo();
        BeanUtils.copyBeanProp(vmTimelineInfo, vmTimelineInfoDto);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        vmTimelineInfo.setModifier(loginUser.getUsername());
        vmTimelineInfo.setUpdateTime(DateUtils.getNowDate());
        int upCount = vmTimelineInfoMapper.update(vmTimelineInfo);
        if (upCount > 0 && !CollectionUtils.isEmpty(vmTimelineInfoDto.getTimelineDetailList())) {
            //先删除该时间轴下的详情
            vmTimelineDetailService.deleteByTimeLineId(vmTimelineInfo.getTimelineId());
            //再批量插入详情
            vmTimelineDetailService.insertBatch(vmTimelineInfo, vmTimelineInfoDto.getTimelineDetailList());
        }
        return upCount;
    }

    /**
     * 通过主键删除数据
     *
     * @param timelineId 主键
     * @return 是否成功
     */
    @Transactional
    @Override
    public boolean deleteById(Long timelineId) {
        log.info("timeline info delete: {}", timelineId);
        int delCount = vmTimelineInfoMapper.deleteById(timelineId);
        if (delCount > 0) {
            //删除该时间轴下的详情
            vmTimelineDetailService.deleteByTimeLineId(timelineId);
        }
        return delCount > 0;
    }

    /**
     * 设置默认时间轴
     * 只能有一个时间轴为默认，设置新的默认时间轴时，会先将所有时间轴的默认状态取消
     *
     * @return 是否成功
     */
    @Transactional
    @Override
    public boolean updateDefaultTimelineById(VmTimelineInfoDto vmTimelineInfoDto) {
        Long timelineId = vmTimelineInfoDto.getTimelineId();
        log.info("设置默认时间轴: {}", timelineId);
        // 校验时间轴ID
        if (Objects.isNull(timelineId)) {
            throw new CustomException(Constants.TIME_LINE_ID_NOT_EXIST);
        }
        // 查询时间轴是否存在且未删除
        VmTimelineInfo timelineInfo = vmTimelineInfoMapper.queryById(timelineId);
        if (Objects.isNull(timelineInfo)) {
            throw new CustomException(Constants.TIME_LINE_NOT_EXIST);
        }
        // 先将所有时间轴的默认状态取消
        vmTimelineInfoMapper.updateAllDefaultFlagToZero(vmTimelineInfoDto);
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        VmTimelineInfo vmTimelineInfo = new VmTimelineInfo();
        vmTimelineInfo.setModifier(loginUser.getUsername());
        vmTimelineInfo.setUpdateTime(DateUtils.getNowDate());
        vmTimelineInfo.setTimelineId(timelineId);
        vmTimelineInfo.setDefaultFlag(1);
        int upCount = vmTimelineInfoMapper.update(vmTimelineInfo);
        return upCount > 0;
    }

    /**
     * 修改时间轴排序号（单条）
     * 用于拖拽排序功能，更新指定时间轴的sortNum字段
     * 兼容老数据：如果数据库中的sortNum为NULL或最小排序值不为1，会自动初始化排序号（从1开始）
     * 然后根据目标位置对其后的记录进行+1（或-1）处理
     *
     * @param vmTimelineInfoDto 时间轴信息
     * @return 是否成功
     */
    @Transactional
    @Override
    public Boolean updateSortNumTimelineById(VmTimelineInfoDto vmTimelineInfoDto) {
        if (Objects.isNull(vmTimelineInfoDto) || Objects.isNull(vmTimelineInfoDto.getTimelineId())) {
            throw new CustomException(Constants.TIME_LINE_ID_NOT_EXIST);
        }
        if (StringUtils.isEmpty(vmTimelineInfoDto.getSortNum())) {
            throw new CustomException(Constants.SORT_NUM_NOT_EXIST);
        }
        Long timelineId = vmTimelineInfoDto.getTimelineId();
        VmTimelineInfo timelineInfo = vmTimelineInfoMapper.queryById(timelineId);
        if (Objects.isNull(timelineInfo)) {
            throw new CustomException(Constants.TIME_LINE_NOT_EXIST);
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Date updateTime = DateUtils.getNowDate();
        String modifier = loginUser.getUsername();
        // 初始化老数据
        normalizeSortNumIfNeeded(vmTimelineInfoDto);
        timelineInfo = vmTimelineInfoMapper.queryById(timelineId);
        if (Objects.isNull(timelineInfo)) {
            throw new CustomException(Constants.TIME_LINE_NOT_EXIST);
        }
        Integer oldSortNum = timelineInfo.getSortNum();
        if (Objects.isNull(oldSortNum)) {
            oldSortNum = vmTimelineInfoMapper.countAllTimelines(vmTimelineInfoDto);
        }
        int targetSort;
        try {
            targetSort = Integer.parseInt(vmTimelineInfoDto.getSortNum());
        } catch (NumberFormatException e) {
            throw new CustomException(Constants.SORT_NUM_FORMAT_ERROR);
        }
        if (targetSort < 1) {
            targetSort = 1;
        }
        int totalCount = vmTimelineInfoMapper.countAllTimelines(vmTimelineInfoDto);
        if (targetSort > totalCount) {
            targetSort = totalCount;
        }
        if (Objects.equals(oldSortNum, targetSort)) {
            log.info("排序值未变化，无需更新: timelineId={}, sortNum={}", timelineId, targetSort);
            return true;
        }
        if (targetSort < oldSortNum) {
            vmTimelineInfoMapper.incrementSortNumRange(targetSort, oldSortNum - 1, timelineId,vmTimelineInfoDto);
        } else {
            vmTimelineInfoMapper.decrementSortNumRange(oldSortNum + 1, targetSort, timelineId,vmTimelineInfoDto);
        }
        VmTimelineInfo updateInfo = new VmTimelineInfo();
        updateInfo.setTimelineId(timelineId);
        updateInfo.setSortNum(targetSort);
        updateInfo.setModifier(modifier);
        updateInfo.setUpdateTime(updateTime);
        int upCount = vmTimelineInfoMapper.update(updateInfo);
        log.info("更新时间轴排序号完成: timelineId={}, oldSortNum={}, newSortNum={}", timelineId, oldSortNum, targetSort);
        return upCount > 0;
    }

    @Override
    public VmTimelineInfoVo queryDefaultTimeline(VmTimelineInfoDto vmTimelineInfoDto) {
        VmTimelineInfoVo vmTimelineInfoVo = new VmTimelineInfoVo();
        VmTimelineInfo vmTimelineInfo = vmTimelineInfoMapper.queryDefaultTimeLine(vmTimelineInfoDto);
        if (Objects.isNull(vmTimelineInfo)) {
            throw new CustomException(Constants.DEFAULT_TIME_LINE_NOT_EXIST);
        }
        //复制数据库的值到VO对象
        BeanUtils.copyBeanProp(vmTimelineInfoVo, vmTimelineInfo);
        //查询详情
        vmTimelineInfoVo.setTimelineDetailList(
                vmTimelineDetailService.queryByTimelineInfoIds(Collections.singletonList(vmTimelineInfo.getTimelineId())).stream()
                        //根据sortNum排序
                        .sorted(Comparator.comparing(s -> Integer.parseInt(s.getSortNum()))).collect(Collectors.toList())
        );
        return vmTimelineInfoVo;
    }

    /**
     * 如果存在老数据（sortNum为NULL或者最小排序值不是1），则重新按照排序值顺序重排，从1开始
     *
     */
    private void normalizeSortNumIfNeeded(VmTimelineInfoDto vmTimelineInfoDto) {
        int nullCount = vmTimelineInfoMapper.countNullSortNum(vmTimelineInfoDto);
        Integer minSortNum = vmTimelineInfoMapper.queryMinSortNum(vmTimelineInfoDto);
        if (nullCount == 0 && Objects.nonNull(minSortNum) && minSortNum == 1) {
            return;
        }
        List<VmTimelineInfo> allTimelineList = vmTimelineInfoMapper.queryAllForSortInit(vmTimelineInfoDto);
        if (CollectionUtils.isEmpty(allTimelineList)) {
            return;
        }
        log.info("开始重新初始化排序号，从1开始: total={}", allTimelineList.size());
        int sortNum = 1;
        for (VmTimelineInfo timelineInfo : allTimelineList) {
            VmTimelineInfo updateInfo = new VmTimelineInfo();
            updateInfo.setTimelineId(timelineInfo.getTimelineId());
            updateInfo.setSortNum(sortNum++);
            vmTimelineInfoMapper.updateSortNum(updateInfo,vmTimelineInfoDto);
        }
    }
}
