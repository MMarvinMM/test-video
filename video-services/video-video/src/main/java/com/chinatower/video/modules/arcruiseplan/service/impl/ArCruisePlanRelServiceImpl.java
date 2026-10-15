package com.chinatower.video.modules.arcruiseplan.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.entity.ArCruisePlanReq;
import com.chinatower.video.api.entity.ArPlanDTO;
import com.chinatower.video.api.entity.DoControlPresetReq;
import com.chinatower.video.api.entity.VideoConstants;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanRel;
import com.chinatower.video.modules.arcruiseplan.mapper.ArCruisePlanMapper;
import com.chinatower.video.modules.arcruiseplan.mapper.ArCruisePlanRelMapper;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanRelService;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanService;
import com.chinatower.video.modules.artags.dataobject.ArChannelInfo;
import com.chinatower.video.modules.artags.dataobject.ArTagsInfo;
import com.chinatower.video.modules.artags.mapper.ArTagsInfoMapper;
import com.chinatower.video.modules.constants.ArConstants;
import com.chinatower.video.modules.query.dao.VideoPtzQueryDAO;
import com.chinatower.video.modules.query.entity.ArTagMoveRes;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * AR巡航计划表 服务实现类
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
@Service
@Slf4j
public class ArCruisePlanRelServiceImpl extends ServiceImpl<ArCruisePlanRelMapper, ArCruisePlanRel> implements IArCruisePlanRelService {



    @Resource
    ArCruisePlanMapper arCruisePlanMapper;

    @Resource
    IArCruisePlanService arCruisePlanService;

    @Resource
    VideoControlService videoControlService;

    @Autowired
    VideoPtzQueryDAO videoPtzQueryDAO;

    @Autowired
    ArTagsInfoMapper arTagsInfoMapper;

    final String arPlanExpireFaultToleranceKey = "ar:plan:expire:set";


    @Override
    public int countPlanByDeviceCode(String arViewId) {
        return baseMapper.countPlanByDeviceCode(arViewId);
    }

    @Override
    public String doPlan(ArCruisePlanReq arCruisePlanReq) {

        if (ObjectUtil.isEmpty(arCruisePlanReq.getOrderNum()) || 0 == arCruisePlanReq.getOrderNum()) {
            arCruisePlanReq.setOrderNum(1);
        }
        //更新表实体
        ArCruisePlan arCruisePlan = new ArCruisePlan();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();

        /*
        获取当前的计划时长
        如果不是最后一个标签 则返回下一个标签
        如果最后一个标签，则返回第一个标签
         */
        ArPlanDTO arPlan = arCruisePlanMapper.getArCruisePlanByArViewId(arCruisePlanReq);
        if (ObjectUtil.isEmpty(arPlan)) {
            //标签为Null
            return ArConstants.PLAN_ZERO;
        }
        arCruisePlan.setId(arPlan.getId());
        log.info("获取当前标签计划:{}", arPlan);
        //加锁 锁存在，保持摄像头；锁不存在，则转动摄像头
        boolean lockFromRedis = isLockFromRedis(String.valueOf(arPlan.getId()), arPlan.getStayDuration());
        if (!lockFromRedis) {
            //增加redis 值  容错处理
            long now = System.currentTimeMillis();
            long faultToleranceTime = 10000;

            long score = now + arPlan.getStayDuration() * 1000 + faultToleranceTime;
            RedisUtil.zAdd(arPlanExpireFaultToleranceKey, String.valueOf(arPlan.getId()), score, RedisDBType.PRIMARY);
            log.info("添加过期zset key:{},value:{},score:{}", arPlanExpireFaultToleranceKey, arPlan.getId(), score);
            //如果是刚开始执行巡航，则修改巡航状态为1
            if (1 == arCruisePlanReq.getOrderNum()) {
                arCruisePlan.setExecutionStatus(ArConstants.PLAN_ONE);
            }
            //修改mysql
            arCruisePlan.setExecutionStep(String.valueOf(arPlan.getExecutionStep()));
            // 更新计划信息
            arCruisePlanService.updateById(arCruisePlan);
            log.info("计划id:{},修改计划:{}修改计划时间:{}", arCruisePlan.getId(), arCruisePlan.getExecutionStep(), DateUtil.now());
            // 调用控制接口
            arPlan.setTenantId(currentUser.getTenantId());
            arPlan.setIndustryCode(currentUser.getIndustryCode());

            if(this.isFacade3(arPlan)){
                arCruisePlanService.doPlanByGateway(arPlan, currentUser);
            }

            // 如果标签存在预置点，调用转到预置点方法
            if (null != arPlan.getPresetId()) {
                log.info("标签绑定了预置点:{},调用转到预置位接口", arPlan.getPresetId());
                doControlPreset(arPlan, currentUser.getTenantId());
            }
        }
        return ArConstants.PLAN_ONE;

    }

    /**
     * 判断视频主画面通道是否是枪机通道 true:是，false:否
     * @param arPlan
     * @return
     */
    private boolean isFacade3(ArPlanDTO arPlan){
        boolean flag = true;
        Set<String> deviceCodeList = new HashSet<>();
        deviceCodeList.add(arPlan.getArViewId());
        List<ArChannelInfo> arChannelInfo = arTagsInfoMapper.getArChannelInfo(deviceCodeList);
        List<ArChannelInfo> collect = arChannelInfo.stream().filter(
                e -> e.getChannelCode().equals(arPlan.getChannelCode())).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(collect) && !Constants.THREE.equals(collect.get(0).getFacade())){
            flag = false;
        }
        return flag;
    }

    private void doControlPreset(ArPlanDTO arPlan, String tenantId) {
        DoControlPresetReq doControlPresetReq = new DoControlPresetReq();
        doControlPresetReq.setDeviceCode(arPlan.getArViewId());
        doControlPresetReq.setChannelCode(arPlan.getChannelCode());
        doControlPresetReq.setPresetId(arPlan.getPresetId());
        doControlPresetReq.setTenantId(tenantId);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE, tenantId);
        videoControlService.doControlPreset(doControlPresetReq, headerMap, false);
    }


    @Override
    public ArCruisePlan findTagAndOrderNum(ArCruisePlanReq arCruisePlanReq) {
        ArCruisePlan tagAndOrderNum = arCruisePlanMapper.findTagAndOrderNum(arCruisePlanReq);
//        if (null == tagAndOrderNum) {
//            return null;
//        }
//        List<ArTagsInfo> tagsList = new ArrayList<>();
//        //查询巡航所有标签
//        List<ArCruisePlanRel> curTagList = arCruisePlanMapper.findTagsByCurId(tagAndOrderNum.getId());
//        Map<String, List<ArCruisePlanRel>> operateMap = curTagList.stream().collect(Collectors.groupingBy(ArCruisePlanRel::getTagClass));
//        for (Map.Entry<String, List<ArCruisePlanRel>> ole : operateMap.entrySet()) {
//            String tagClass = ole.getKey();
//            ArTagFilterVO arTagFilterVO = new ArTagFilterVO();
//            arTagFilterVO.setTagIdList(ole.getValue().stream().map(ArCruisePlanRel::getTagId).collect(Collectors.toList()));
//            switch (tagClass)
//            {
//                case VideoConstants.TAG_CLASS_101:
//                case VideoConstants.TAG_CLASS_102:
//                    List<ArTagsInfo> tags = arCruisePlanMapper.findTagsByArViewId(arCruisePlanReq);
//                    tagsList.addAll(tags);
//                    break;
//                case VideoConstants.TAG_CLASS_103:
//                    List<ArTagMoveRes> arTagByDevice = videoPtzQueryDAO.getArTagByDevice(arTagFilterVO);
//                    tagsList.addAll(this.buildVo(arTagByDevice, arCruisePlanReq));
//                    break;
//                case VideoConstants.TAG_CLASS_105:
//                    List<ArTagMoveRes> arTagByIot = videoPtzQueryDAO.getArTagByIot(arTagFilterVO);
//                    tagsList.addAll(this.buildVo(arTagByIot, arCruisePlanReq));
//                    break;
//                case VideoConstants.TAG_CLASS_106:
//                    List<ArTagMoveRes> arTagByUav = videoPtzQueryDAO.getArTagByUav(arTagFilterVO);
//                    tagsList.addAll(this.buildVo(arTagByUav, arCruisePlanReq));
//                    break;
//                case VideoConstants.TAG_CLASS_107:
//                    List<ArTagMoveRes> arTagBySensor = videoPtzQueryDAO.getArTagBySensor(arTagFilterVO);
//                    tagsList.addAll(this.buildVo(arTagBySensor, arCruisePlanReq));
//                    break;
//                case VideoConstants.TAG_CLASS_108:
//                    List<ArTagMoveRes> arTagByHorn = videoPtzQueryDAO.getArTagByHorn(arTagFilterVO);
//                    tagsList.addAll(this.buildVo(arTagByHorn, arCruisePlanReq));
//                    break;
//                default:
//                    break;
//            }
//        }
//        tagAndOrderNum.setTagsList(tagsList);
        return tagAndOrderNum;
    }

    private List<ArTagsInfo> buildVo(List<ArTagMoveRes> list, ArCruisePlanReq arCruisePlanReq){
        List<ArTagsInfo> result = new ArrayList<>();
        for (ArTagMoveRes arTagMoveRes : list) {
            ArTagsInfo arTagsInfo = new ArTagsInfo();
            arTagsInfo.setArViewId(arCruisePlanReq.getArViewId());
            arTagsInfo.setChannelCode(arCruisePlanReq.getChannelCode());
            arTagsInfo.setTagId(arTagMoveRes.getTagId());
            arTagsInfo.setTagsName(arTagMoveRes.getTagsName());
            arTagsInfo.setTagClass(arTagMoveRes.getTagClass());
            result.add(arTagsInfo);
        }
        return result;
    }

    @Override
    public int findFirstPlan(ArCruisePlanReq arCruisePlanReq) {
        return arCruisePlanMapper.findFirstPlan(arCruisePlanReq);
    }

    @Override
    public List<ArCruisePlan> findTags(ArCruisePlanReq arCruisePlanReq) {
        return arCruisePlanMapper.findTags(arCruisePlanReq);
    }


    private boolean isLockFromRedis(String key, long expire) {
        //锁过期时间
        final String lockValue = "lock";
        //判断是否上锁
        if (!RedisUtil.setIfAbsent(key, lockValue, expire, TimeUnit.SECONDS, RedisDBType.PRIMARY)) {
            //如果重复，说明  当前锁被占用，该定时不执行
            log.info("当前标签巡航未结束--{},剩余时间：{}s", key, RedisUtil.getExpire(key, RedisDBType.PRIMARY));
            return true;
        } else {
            return false;
        }
    }
}
