package com.chinatower.video.modules.arcruiseplan.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.ArCruisePlanCustomAPI;
import com.chinatower.video.api.entity.ArCruisePlanReq;
import com.chinatower.video.api.entity.DoLinkageReq;
import com.chinatower.video.api.entity.VideoConstants;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanRel;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanVo;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanRelService;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanService;
import com.chinatower.video.modules.artags.dataobject.ArTagsInfo;
import com.chinatower.video.modules.artags.service.IArTagsInfoService;
import com.chinatower.video.modules.constants.ArConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * AR巡航计划 前端控制器
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
@RestController
@Slf4j
@Api(value = "AR巡航计划", tags = "AR巡航计划")
@SuppressWarnings("all")
public class ArCruisePlanController implements ArCruisePlanCustomAPI {

    @Autowired
    private IArCruisePlanService arCruisePlanService;

    @Autowired
    private IArCruisePlanRelService arCruisePlanRelService;

    @Autowired
    private IArTagsInfoService arTagsInfoService;

    private final String NOT_PLAN_EXCUTE = "没有计划在进行中";

    private final String THIS_PLAN_DO_NOT_HAVE_TAG = "当前巡航计划下无标签";

    @ApiOperation(value = "增加/修改AR巡航计划", notes = "增加/修改AR巡航计划")
    @PostMapping("/arCruisePlan/addOrUpdate")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> addOrUpdate(ArCruisePlanReq arCruisePlanReq) {
        log.info("增加AR巡航计划入参:{}", arCruisePlanReq);
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getArViewId() == null || StringUtils.isEmpty(arCruisePlanReq.getArViewId())) {
            return Result.fail("AR实景ID不能为空");
        }
        if (arCruisePlanReq.getChannelCode() == null || StringUtils.isEmpty(arCruisePlanReq.getChannelCode())) {
            return Result.fail("通道编码不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 新增、查询增加tenantId、industryCode
        // 如果巡航计划名称为空，自动生成，如果不为空，校验该设备下是否重复，并验证长度最大20
        if (StringUtils.isEmpty(arCruisePlanReq.getCruisePlanName())) {
            // 查询巡航计划数量
            QueryWrapper<ArCruisePlan> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ArConstants.AR_VIEW_ID, arCruisePlanReq.getArViewId());
            queryWrapper.eq(ArConstants.CHANNEL_CODE, arCruisePlanReq.getChannelCode());
            queryWrapper.eq(ArConstants.TENANT_ID, currentUser.getTenantId());
            queryWrapper.eq(ArConstants.INDUSTRY_CODE, currentUser.getIndustryCode());
            queryWrapper.eq(ArConstants.DELETE_FLAG, "0");
            int count = arCruisePlanService.count(queryWrapper);
            List<ArCruisePlan> arCruisePlanList = arCruisePlanService.list(queryWrapper);
            String cruisePlanName = "巡航计划" + (count + 1);
            // 检查是否重复
            if (arCruisePlanList != null && arCruisePlanList.size() > 0) {
                for (ArCruisePlan arCruisePlan : arCruisePlanList) {
                    if (cruisePlanName.equals(arCruisePlan.getCruisePlanName())) {
                        count++;
                        cruisePlanName = "巡航计划" + (count + 1);
                    }
                }
            }
            arCruisePlanReq.setCruisePlanName(cruisePlanName);
        } else {
            QueryWrapper<ArCruisePlan> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ArConstants.CRUISE_PLAN_NAME, arCruisePlanReq.getCruisePlanName());
            queryWrapper.eq(ArConstants.AR_VIEW_ID, arCruisePlanReq.getArViewId());
            queryWrapper.eq(ArConstants.CHANNEL_CODE, arCruisePlanReq.getChannelCode());
            queryWrapper.eq(ArConstants.TENANT_ID, currentUser.getTenantId());
            queryWrapper.eq(ArConstants.INDUSTRY_CODE, currentUser.getIndustryCode());
            queryWrapper.eq(ArConstants.DELETE_FLAG, "0");
            int count = arCruisePlanService.count(queryWrapper);
            if (count > 0) {
                List<ArCruisePlan> arCruisePlanList = arCruisePlanService.list(queryWrapper);
                if (arCruisePlanList != null && arCruisePlanList.size() > 0 && !arCruisePlanList.get(0).getId().equals(arCruisePlanReq.getId())) {
                    return Result.fail("巡航计划名称已存在");
                }
            }
            if (arCruisePlanReq.getCruisePlanName().length() > 20) {
                return Result.fail("巡航计划名称长度不能超过20个字符");
            }
        }

        ArCruisePlan arCruisePlan = new ArCruisePlan();
        BeanUtils.copyBeanProp(arCruisePlan, arCruisePlanReq);
        List<ArCruisePlanRel> arCruisePlanRelList = new ArrayList<>();
        if (arCruisePlanReq.getArCruisePlanRelList() != null) {
            for (ArCruisePlanReq.ArCruisePlanRelReq arCruisePlanRelReq : arCruisePlanReq.getArCruisePlanRelList()) {
                ArCruisePlanRel arCruisePlanRel = new ArCruisePlanRel();
                BeanUtils.copyBeanProp(arCruisePlanRel, arCruisePlanRelReq);
                arCruisePlanRelList.add(arCruisePlanRel);
            }
        }
        arCruisePlan.setTenantId(currentUser.getTenantId());
        arCruisePlan.setIndustryCode(currentUser.getIndustryCode());
        arCruisePlan.setAppVerCode(currentUser.getAppVerCode());
        if (arCruisePlanReq.getId() != null && arCruisePlanReq.getId() > 0) {
            // 校验巡航计划是否执行中
            ArCruisePlan arCruisePlan1 = arCruisePlanService.getById(arCruisePlanReq.getId());
            if (arCruisePlan1 != null && "1".equals(arCruisePlan1.getExecutionStatus())) {
                return Result.fail("巡航计划已执行，不允许修改");
            }
            // 更新
            arCruisePlan.setModifier(currentUser.getUserName());
            arCruisePlan.setUpdateTime(new Date());
            // 更新巡航计划
            arCruisePlanService.updateById(arCruisePlan);
            // 删除巡航计划关联
            QueryWrapper<ArCruisePlanRel> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq(ArConstants.CRUISE_PLAN_ID, arCruisePlanReq.getId());
            arCruisePlanRelService.remove(deleteWrapper);
            // 新增巡航计划关联
            for (ArCruisePlanRel arCruisePlanRel : arCruisePlanRelList) {
                arCruisePlanRel.setCruisePlanId(arCruisePlan.getId());
                arCruisePlanRel.setCreator(currentUser.getUserName());
                arCruisePlanRel.setCreateTime(new Date());
            }
            arCruisePlanRelService.saveBatch(arCruisePlanRelList);
            return Result.success("修改AR巡航计划成功");
        } else {
            // 新增
            arCruisePlan.setCreator(currentUser.getUserName());
            arCruisePlan.setCreateTime(new Date());
            // 新增巡航计划
            arCruisePlan.setDeleteFlag(0);
            // 如果为空，默认未执行
            if (StringUtils.isEmpty(arCruisePlan.getExecutionStatus())) {
                arCruisePlan.setExecutionStatus("0");
            }
            arCruisePlanService.save(arCruisePlan);
            // 用ID生成9位巡航计划编号,不足的前面补0
            String cruisePlanNo = String.format("%09d", arCruisePlan.getId());
            arCruisePlan.setCruisePlanNumber(cruisePlanNo);
            arCruisePlanService.updateById(arCruisePlan);
            // 新增巡航计划关联
            for (ArCruisePlanRel arCruisePlanRel : arCruisePlanRelList) {
                arCruisePlanRel.setCruisePlanId(arCruisePlan.getId());
                arCruisePlanRel.setCreator(currentUser.getUserName());
                arCruisePlanRel.setCreateTime(new Date());
            }
            arCruisePlanRelService.saveBatch(arCruisePlanRelList);
            return Result.success("新增AR巡航计划成功");
        }
    }

    @ApiOperation(value = "查询AR巡航计划列表", notes = "查询AR巡航计划列表")
    @PostMapping("/arCruisePlan/list")
    @Override
    public Result<Page<ArCruisePlan>> list(ArCruisePlanReq arCruisePlanReq) {
        log.info("查询AR巡航计划列表入参:{}", arCruisePlanReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getArViewId() == null || StringUtils.isEmpty(arCruisePlanReq.getArViewId())) {
            return Result.fail("AR实景ID不能为空");
        }
        if (arCruisePlanReq.getChannelCode() == null || StringUtils.isEmpty(arCruisePlanReq.getChannelCode())) {
            return Result.fail("通道编码不能为空");
        }
        Page<ArCruisePlan> page = new Page<>(arCruisePlanReq.getPageNum(), arCruisePlanReq.getPageSize());
        if (arCruisePlanReq.getIsTowerView() == 0){
            arCruisePlanReq.setTenantId(currentUser.getTenantId());
            arCruisePlanReq.setIndustryCode(currentUser.getIndustryCode());
        }
        if (arCruisePlanReq.getIsTowerView() == 0) {
            PageHelper.startPage(arCruisePlanReq.getPageNum(), arCruisePlanReq.getPageSize());
            PageInfo<ArCruisePlan> page1 = new PageInfo<ArCruisePlan>(arCruisePlanService.pageList(arCruisePlanReq));
            PageHelper.clearPage();
            page.setRecords(page1.getList());
            page.setTotal(page1.getTotal());
        } else {
            ArCruisePlanVo arCruisePlanVo = new ArCruisePlanVo();
            arCruisePlanVo.setArViewId(arCruisePlanReq.getArViewId());
            arCruisePlanVo.setCruisePlanName(arCruisePlanReq.getCruisePlanName());
            arCruisePlanVo.setExecutionStatus(arCruisePlanReq.getExecutionStatus());
            arCruisePlanVo.setQueryTime(arCruisePlanReq.getQueryTime());
            arCruisePlanVo.setChannelCode(arCruisePlanReq.getChannelCode());
            PageHelper.startPage(arCruisePlanReq.getPageNum(), arCruisePlanReq.getPageSize());
            PageInfo<ArCruisePlan> page1 = new PageInfo<ArCruisePlan>(arCruisePlanService.ttList(arCruisePlanVo));
            PageHelper.clearPage();
            page.setRecords(page1.getList());
            page.setTotal(page1.getTotal());
        }
        List<ArCruisePlan> arCruisePlanList = page.getRecords();
        if (arCruisePlanList != null && arCruisePlanList.size() > 0) {
            for (ArCruisePlan arCruisePlan : arCruisePlanList) {
                arCruisePlan.setCreatorName(arCruisePlan.getCreator());
                arCruisePlan.setModifierName(arCruisePlan.getModifier());
            }
        }
        return Result.data(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "删除AR巡航计划", notes = "删除AR巡航计划")
    @PostMapping("/arCruisePlan/delete")
    @Override
    public Result<String> delete(ArCruisePlanReq arCruisePlanReq) {
        log.info("删除AR巡航计划入参:{}", arCruisePlanReq);
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getId() == null || arCruisePlanReq.getId() <= 0) {
            return Result.fail("巡航计划ID不能为空");
        }
        ArCruisePlan arCruisePlan = arCruisePlanService.getById(arCruisePlanReq.getId());
        // 如果巡航计划已经执行，不允许删除
        if (arCruisePlan != null && !"0".equals(arCruisePlan.getExecutionStatus())) {
            return Result.fail("该巡航计划正在执行中，无法删除，请巡航结束后再试");
        }
        // 逻辑删除
        arCruisePlan.setDeleteFlag(1);
        arCruisePlanService.updateById(arCruisePlan);
        // 删除巡航计划关联
        QueryWrapper<ArCruisePlanRel> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq(ArConstants.CRUISE_PLAN_ID, arCruisePlanReq.getId());
        arCruisePlanRelService.remove(deleteWrapper);
        return Result.success("删除AR巡航计划成功");
    }

    @ApiOperation(value = "获取AR巡航计划详情", notes = "获取AR巡航计划详情")
    @PostMapping("/arCruisePlan/details")
    @Override
    public Result<ArCruisePlan> details(ArCruisePlanReq arCruisePlanReq) {
        log.info("获取AR巡航计划详情入参:{}", arCruisePlanReq);
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getId() == null || arCruisePlanReq.getId() <= 0) {
            return Result.fail("巡航计划ID不能为空");
        }
        ArCruisePlan arCruisePlan = arCruisePlanService.getById(arCruisePlanReq.getId());
        if (arCruisePlan == null || arCruisePlan.getDeleteFlag() == 1) {
            return Result.fail("巡航计划不存在");
        }
        // 查询巡航计划关联
        QueryWrapper<ArCruisePlanRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ArConstants.CRUISE_PLAN_ID, arCruisePlanReq.getId());
        List<ArCruisePlanRel> arCruisePlanRelList = arCruisePlanRelService.list(queryWrapper);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 查询巡航计划关联的巡航点的标签信息
        QueryWrapper<ArTagsInfo> tagsInfoQueryWrapper = new QueryWrapper<>();
        tagsInfoQueryWrapper.in(ArConstants.ID, arCruisePlanRelList.stream().map(ArCruisePlanRel::getTagId).collect(Collectors.toList()));
        List<ArTagsInfo> arTagsInfoList = arTagsInfoService.list(tagsInfoQueryWrapper);
        List<String> arTagsInfoListMark = arTagsInfoService.listMark(currentUser.getUserName());
        for (ArCruisePlanRel arCruisePlanRel : arCruisePlanRelList) {
            ArTagsInfo arTagsInfo = arTagsInfoList.stream().filter(arTagsInfo1 -> arTagsInfo1.getId().equals(arCruisePlanRel.getTagId())).findFirst().orElse(null);
            if (arTagsInfo == null) {
                continue;
            }
            arTagsInfo.setMarkFlag(0);
            if (arTagsInfoListMark != null && arTagsInfoListMark.size() > 0) {
                for (String tagsId : arTagsInfoListMark) {
                    if (String.valueOf(arTagsInfo.getId()).equals(tagsId)) {
                        arTagsInfo.setMarkFlag(1);
                        break;
                    }
                }
            }
            arCruisePlanRel.setArTagsInfo(arTagsInfo);
        }
        arCruisePlan.setArCruisePlanRelList(arCruisePlanRelList);
        return Result.data(arCruisePlan);
    }

    @ApiOperation(value = "获取AR巡航计划执行详情", notes = "获取AR巡航计划执行详情")
    @PostMapping("/arCruisePlan/executionDetails")
    @Override
    public Result<ArCruisePlan> executionDetails(ArCruisePlanReq arCruisePlanReq) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        log.info("获取AR巡航计划执行详情-userName:{}-入参:{}", user.getUserName(), arCruisePlanReq);
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getArViewId() == null || arCruisePlanReq.getArViewId().length() == 0) {
            return Result.fail("AR实景ID不能为空");
        }
        QueryWrapper<ArCruisePlan> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(ArConstants.AR_VIEW_ID, arCruisePlanReq.getArViewId());
        queryWrapper1.eq(ArConstants.CHANNEL_CODE, arCruisePlanReq.getChannelCode());
        queryWrapper1.eq(ArConstants.EXECUTION_STATUS, "1");
        List<ArCruisePlan> arCruisePlanList = arCruisePlanService.list(queryWrapper1);
        if (arCruisePlanList == null || arCruisePlanList.size() == 0) {
            log.info(NOT_PLAN_EXCUTE);
            return Result.fail(NOT_PLAN_EXCUTE);
        }
        String executionStatus = arCruisePlanRelService.doPlan(arCruisePlanReq);
        if (ArConstants.PLAN_ZERO.equals(executionStatus)) {
            log.info(THIS_PLAN_DO_NOT_HAVE_TAG);
            return Result.fail(THIS_PLAN_DO_NOT_HAVE_TAG);
        } else {
            //返回当前执行的标签信息
            ArCruisePlan res = arCruisePlanRelService.findTagAndOrderNum(arCruisePlanReq);
            return Result.data(res);
        }
    }

    @PostMapping("/arCruisePlan/startCruise")
    @ApiOperation(value = "启动AR巡航计划", notes = "启动AR巡航计划")
    @Override
    public Result<List<ArCruisePlan>> startCruise(ArCruisePlanReq arCruisePlanReq) {
        log.info("启动AR巡航计划入参:{}", arCruisePlanReq);
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getId() == null || arCruisePlanReq.getId() <= 0) {
            return Result.fail("巡航计划ID不能为空");
        }
        // 查询巡航计划
        ArCruisePlan arCruisePlan = arCruisePlanService.getById(arCruisePlanReq.getId());
        if (arCruisePlan == null) {
            return Result.fail("巡航计划不存在");
        }
        if ("1".equals(arCruisePlan.getExecutionStatus())) {
            return Result.fail("巡航计划已经启动");
        }
        // 查询该实景下所有巡航计划
        QueryWrapper<ArCruisePlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ArConstants.AR_VIEW_ID, arCruisePlan.getArViewId());
        queryWrapper.eq(ArConstants.CHANNEL_CODE, arCruisePlan.getChannelCode());
        queryWrapper.eq(ArConstants.EXECUTION_STATUS, "1");
        List<ArCruisePlan> arCruisePlanList = arCruisePlanService.list(queryWrapper);
        if (arCruisePlanList != null && arCruisePlanList.size() > 0) {
            return Result.fail("该摄像机已有其他巡航计划在执行，请将其停用后再试。");
        }
        // 查询巡航路径信息整合表，获取计划执行状态
        int count = arCruisePlanRelService.countPlanByDeviceCode(arCruisePlan.getArViewId());
        if (count > 0) {
            return Result.fail("该摄像机已有其他巡航计划在执行，请将其停用后再试。");
        }

        //查询巡航
        int id = arCruisePlanRelService.findFirstPlan(arCruisePlanReq);
        if (0 == id) {
            return Result.fail("该摄像机巡航计划无标签");
        }
        // 更新巡航计划状态为执行中
        arCruisePlan.setExecutionStatus("1");
        arCruisePlan.setExecutionStep(String.valueOf(id));
        arCruisePlanService.updateById(arCruisePlan);
        log.info("巡航开始,计划id:{},修改计划:{}修改计划时间:{}", arCruisePlan.getId(), arCruisePlan.getExecutionStep(), DateUtil.now());

        //执行巡航计划
        arCruisePlanReq.setArViewId(arCruisePlan.getArViewId());
        arCruisePlanReq.setChannelCode(arCruisePlan.getChannelCode());
        arCruisePlanRelService.doPlan(arCruisePlanReq);
        List<ArCruisePlan> list = arCruisePlanRelService.findTags(arCruisePlanReq);
        return Result.data(list, "启动AR巡航计划成功");
    }

    @PostMapping("/arCruisePlan/stopCruise")
    @ApiOperation(value = "停止AR巡航计划", notes = "停止AR巡航计划")
    @Override
    public Result<String> stopCruise(ArCruisePlanReq arCruisePlanReq) {
        log.info("停止AR巡航计划入参:{}", arCruisePlanReq);
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getId() == null || arCruisePlanReq.getId() <= 0) {
            return Result.fail("巡航计划ID不能为空");
        }
        // 查询巡航计划
        ArCruisePlan arCruisePlan = arCruisePlanService.getById(arCruisePlanReq.getId());
        if (arCruisePlan == null) {
            return Result.fail("巡航计划不存在");
        }
        // 更新巡航计划状态为未执行
        arCruisePlan.setExecutionStatus("0");
        arCruisePlan.setExecutionStep("0");
        arCruisePlanService.updateById(arCruisePlan);

        RedisUtil.zRemove("ar:plan:expire:set", RedisDBType.PRIMARY, arCruisePlan.getArViewId());
        RedisUtil.delete(arCruisePlan.getArViewId(),RedisDBType.PRIMARY);

        return Result.success("停止AR巡航计划成功");
    }

    @ApiOperation(value = "枪球联动", notes = "枪球联动")
    @PostMapping("/arCruisePlan/doLinkage")
    @Override
    public Result doLinkage(DoLinkageReq doLinkageReq){
        Result res;
        log.info("枪球联动" + VideoConstants.CONTROLLERREQ + doLinkageReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info("枪球联动" + SecurityUtils.getOperator());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE, currentUser.getUser().getTenantId());
            res = arCruisePlanService.doLinkage(doLinkageReq, headerMap, false);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + "枪球联动失败");
            log.error("枪球联动失败" , e);
        }
        return res;
    }

    @ApiOperation(value = "获取计划数量", notes = "获取计划数量")
    @PostMapping("/arCruisePlan/getCruisePlanCount")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result getCruisePlanCount(ArCruisePlanReq arCruisePlanReq) {
        log.info("获取计划数量入参:{}", arCruisePlanReq);
        if (arCruisePlanReq == null) {
            return Result.fail("参数不能为空");
        }
        if (arCruisePlanReq.getArViewId() == null || StringUtils.isEmpty(arCruisePlanReq.getArViewId())) {
            return Result.fail("AR实景ID不能为空");
        }
        if (arCruisePlanReq.getChannelCode() == null || StringUtils.isEmpty(arCruisePlanReq.getChannelCode())) {
            return Result.fail("通道编码不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 查询巡航计划数量
        QueryWrapper<ArCruisePlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ArConstants.AR_VIEW_ID, arCruisePlanReq.getArViewId());
        queryWrapper.eq(ArConstants.CHANNEL_CODE, arCruisePlanReq.getChannelCode());
        queryWrapper.eq(ArConstants.TENANT_ID, currentUser.getTenantId());
        queryWrapper.eq(ArConstants.INDUSTRY_CODE, currentUser.getIndustryCode());
        queryWrapper.eq(ArConstants.DELETE_FLAG, "0");
        int count = arCruisePlanService.count(queryWrapper);
        return Result.data(count);
    }
}

