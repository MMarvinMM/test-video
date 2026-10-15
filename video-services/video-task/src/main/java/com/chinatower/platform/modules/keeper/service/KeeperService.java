package com.chinatower.platform.modules.keeper.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.entity.request.MsgSwitchRequest;
import com.chinatower.platform.entity.request.SysMsgSwitch;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.modules.keeper.dao.KeeperDAO;
import com.chinatower.platform.modules.keeper.entity.BmAttendanceLineTaskVO;
import com.chinatower.platform.modules.keeper.entity.InspectionConfPeriodVO;
import com.chinatower.platform.modules.keeper.entity.InspectionTaskVO;
import com.chinatower.platform.modules.keeper.entity.KeeperVO;
import com.chinatower.platform.utils.DataUtil;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 网格员考勤、巡查定时
 */
@Component
@Slf4j
public class KeeperService {
    @Resource
    private KeeperDAO keeperDAO;

    @Autowired
    private BmKeeperTrailService bmKeeperTrailService;

    @Autowired
    private MsgFeignClient msgFeignClient;

    /**
     * 网格员巡查任务定时
     * 用于生成巡查任务，将任务存入 Redis，并更新巡查配置状态。
     *
     * @param param 任务执行参数（未使用）
     * @return 返回任务执行结果，成功返回 ReturnT.SUCCESS
     */
    @XxlJob("inspectionTaskHandler")
    @DS("base")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> inspectionTaskHandler(String param) {
        log.info(Constants.GRID_KEEPER_INSPECTION_TASK_HANDLER);
        XxlJobLogger.log(Constants.GRID_KEEPER_INSPECTION_TASK_HANDLER);
        // 获取巡查配置数据
        List<InspectionTaskVO> list = keeperDAO.getInspectionConfList();
        List<InspectionTaskVO> periodList = new ArrayList<>();
        // 遍历巡查配置数据，生成巡查任务
        if(!CollectionUtils.isEmpty(list)){
            for (InspectionTaskVO vo : list) {
                // 检查是否包含巡查时间段配置
                if (!StringUtils.isEmpty(vo.getInspectionPeriod())) {
                    // 将巡查时间段配置转换为对象列表
                    List<InspectionConfPeriodVO> confPeriodList = JSON.parseArray(vo.getInspectionPeriod(),InspectionConfPeriodVO.class);
                    // 生成多条任务
                    for (InspectionConfPeriodVO periodVO : confPeriodList) {
                        InspectionTaskVO dto = new InspectionTaskVO();
                        BeanUtils.copyProperties(vo,dto);
                        dto.setInspectionStartTime(periodVO.getInspectionStartTime());
                        dto.setInspectionEndTime(periodVO.getInspectionEndTime());
                        // 生成唯一任务 ID
                        dto.setTaskId(UUID.randomUUID().toString().replace(Constants.LINE_CENTER, Constants.EMPTY_STR));
                        periodList.add(dto);
                    }
                } else {
                    // 如果没有时间段配置，则直接生成任务
                    vo.setTaskId(UUID.randomUUID().toString().replace(Constants.LINE_CENTER, Constants.EMPTY_STR));
                    periodList.add(vo);
                }
            }

            // 批量插入巡查任务
            if (!CollectionUtils.isEmpty(periodList)){
                keeperDAO.insertInspectionTask(periodList);
            }

            // 准备存储到 Redis 的任务数据
            List<String> userList = new ArrayList<>();
            List<InspectionTaskVO> argList = new ArrayList<>();
            for (InspectionTaskVO inspectionTaskVO : periodList) {
                String keeperIds = inspectionTaskVO.getKeeperId();
                //根据keeperId查出userId
                List<String> keeperList = Arrays.asList(keeperIds.split(Constants.COMMA));
                userList.addAll(keeperList);

                // 为每个网格员生成一条任务数据
                if (!CollectionUtils.isEmpty(keeperList)){
                    for (String keeperId : keeperList) {
                        //把task任务存在redis里 需要的4个值
                        InspectionTaskVO taskVO = new InspectionTaskVO();
                        taskVO.setKeeperId(keeperId);
                        taskVO.setTaskId(inspectionTaskVO.getTaskId());
                        taskVO.setTaskName(inspectionTaskVO.getCnofName());
                        taskVO.setCreateTime(inspectionTaskVO.getCreateTime());
                        taskVO.setConfDesc(inspectionTaskVO.getConfDesc());
                        taskVO.setIndustryCode(inspectionTaskVO.getIndustryCode());
                        argList.add(taskVO);
                    }
                }
            }

            // 查询网格员信息，并将用户 ID 匹配到任务数据中
            List<KeeperVO> keeperList = keeperDAO.getUserIdByKeerperList(userList);  // 根据 keeperId 查询网格员信息
            if (!CollectionUtils.isEmpty(argList) && !CollectionUtils.isEmpty(keeperList)){
                List<InspectionTaskVO> resList = argList.stream().map(item -> {
                    keeperList.stream().filter(keeper-> Objects.equals(item.getKeeperId(),keeper.getKeeperId())).forEach(keeper-> {
                        item.setUserId(keeper.getStaffId());
                    });
                    return item;
                }).collect(Collectors.toList());
                // 将任务数据存入 Redis
                if (!CollectionUtils.isEmpty(resList)){
                    List<String> inspectList = new ArrayList<>();
                    for (InspectionTaskVO inspectionTaskVO : resList) {
                        inspectList.add(JSON.toJSONString(inspectionTaskVO));
                    }
                    log.info("存入redis巡查任务对象：{}", JSON.toJSONString(inspectList));
                    RedisUtil.lRightPushAll("task_job",inspectList,RedisDBType.PRIMARY);
                }
            }

            //更新巡查配置
            keeperDAO.updateNoRepeatConfStatus();
            //更新巡查完成状态
            keeperDAO.updateRepeatConfStatus();
        }
        return ReturnT.SUCCESS;
    }


    /**
     * 网格员巡查任务获取对象发消息定时
     * 用于从 Redis 中读取巡查任务数据，推送消息至用户，并更新任务状态。
     *
     * @param param 任务执行参数（未使用）
     * @return 返回任务执行结果，成功返回 ReturnT.SUCCESS
     */
    @XxlJob("inspectionTaskMassage")
    @DS("base")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> inspectionTaskMassage(String param) {
        log.info("网格员巡查任务获取对象发消息定时");
        XxlJobLogger.log("网格员巡查任务获取对象发消息定时");
        //获取当前时间，精确到分钟的时间戳
        long nowTime = DataUtil.getTimeMills();
        //加锁
        final String lockKey = "TaskOneTask--Lock";
        //锁过期时间
        final long expireTimeMin = 1L;
        final String lockValue = "lock";

        // 尝试获取 Redis 锁
        if (!RedisUtil.setIfAbsent(lockKey, lockValue, expireTimeMin, TimeUnit.MINUTES, RedisDBType.PRIMARY)) {
            //如果重复，说明当前锁被占用，该定时不执行
            log.info("其他线程获取到列表，请稍等");
            ReturnT<String> objectReturnT = new ReturnT<>();
            objectReturnT.setMsg(Constants.OTHER_RUN);
            objectReturnT.setCode(200);
            objectReturnT.setContent(Constants.SUCCESS);
            return objectReturnT;
        }
        try{
            // 查询 Redis 列表中的巡查任务数据
            List<String> strings = RedisUtil.lRange(Constants.TASK_JOB, 0, -1, RedisDBType.PRIMARY);
            //list判空
            if (CollectionUtils.isEmpty(strings)){
                log.info("task_job为key的redis列表数据为空");
                // 没有数据时释放锁并退出任务
                RedisUtil.delete(lockKey, RedisDBType.PRIMARY);
                ReturnT<String> objectReturnT = new ReturnT<>();
                objectReturnT.setMsg(Constants.NO_KEY);
                objectReturnT.setCode(200);
                objectReturnT.setContent(Constants.SUCCESS);
                return objectReturnT;
            }
            List<Map<Integer,String>> indexList = new ArrayList<>();
            List<String> taskIdList = new ArrayList<>();
            List<JgPushRequest> pushNews = new ArrayList<>();
            List<MsgSwitchRequest> userList = new ArrayList<>();
            log.info("inspectionTaskMassage接口缓存值为"+JSON.toJSONString(strings));
            for (int i = 0; i < strings.size(); i++) {
                String string = strings.get(i);
                InspectionTaskVO inspectionTaskVO = JSON.parseObject(string,InspectionTaskVO.class);

                //32.查询用户列表中各通知开关状态/msg/switch/queryByUserList入参
                MsgSwitchRequest msgSwitchRequest = new MsgSwitchRequest();
                msgSwitchRequest.setUserId(inspectionTaskVO.getUserId());
                msgSwitchRequest.setIndustryCode(inspectionTaskVO.getIndustryCode());
                userList.add(msgSwitchRequest);


                //把下发时间转化为精确到秒的时间戳
                long compareTime = DataUtil.formatTimeMills(inspectionTaskVO.getCreateTime());
                //判断下发时间之前的数据
                if (nowTime >= compareTime){
                    Map<Integer, String> indexMap = new HashMap<>(16);
                    indexMap.put(i,string);
                    indexList.add(indexMap);
                    // 构建极光推送请求对象
                    JgPushRequest jgPushRequest = new JgPushRequest();
                    jgPushRequest.setAlias(inspectionTaskVO.getUserId());
                    jgPushRequest.setNotificationTitle(Constants.PENDING_PROCESSED);
                    jgPushRequest.setMsgTitle(inspectionTaskVO.getTaskName());
                    jgPushRequest.setMsgContent(inspectionTaskVO.getConfDesc());
                    jgPushRequest.setIndustryCode(inspectionTaskVO.getIndustryCode());
                    jgPushRequest.setTenantId(inspectionTaskVO.getTenantId());
                    jgPushRequest.setExtrasparam(JSON.toJSONString(inspectionTaskVO));
                    pushNews.add(jgPushRequest);
                    taskIdList.add(inspectionTaskVO.getTaskId());
                }
            }
            //调用32.查询用户列表中各通知开关状态接口
            log.info("inspectionTaskMassage调用queryByUserList接口入参为："+JSON.toJSONString(userList));
            Result<Map<String, SysMsgSwitch>> mapResult = msgFeignClient.queryByUserList(userList);
            log.info("inspectionTaskMassage调用queryByUserList接口返回为："+JSON.toJSONString(mapResult));
            Map<String, SysMsgSwitch> data = mapResult.getData();


            // 设置推送消息的消息类型
            for (JgPushRequest pushNew : pushNews) {
                String userIndust = pushNew.getAlias()+Constants.UNDERLINE+pushNew.getIndustryCode();
                if (ObjectUtils.isNotEmpty(data.get(userIndust)) && ObjectUtils.isNotEmpty(data.get(userIndust).getOtherSwitch())) {
                    log.info("inspectionTaskMassage调用queryByUserList接口返回对象为："+JSON.toJSONString(data.get(userIndust)));
                    pushNew.setMsgType(data.get(userIndust).getOtherSwitch() + "");
                } else {
                    pushNew.setMsgType(Constants.ONE);
                }
                pushNew.setType(Constants.FOUR);
            }
            log.info("调用极光批量接接口发送入参-{}",JSON.toJSONString(pushNews));
            //调用极光推送批量接口
            msgFeignClient.batchPushNews(pushNews);


            //改状态
            if (!CollectionUtils.isEmpty(taskIdList)){
                keeperDAO.updateTaskWatchStatus(taskIdList);
            }
            //删除下标为i的值
            if (!CollectionUtils.isEmpty(indexList)){
                for (Map<Integer, String> integerStringMap : indexList) {
                    for (Map.Entry<Integer, String> integerStringEntry : integerStringMap.entrySet()) {
                        log.info("发完消息要被处理的对象-{}",integerStringEntry.getValue());
                        //存历史消息信息
//                        RedisUtil.lRightPush(Constants.TASK_HISTORY_JOB,integerStringEntry.getValue(), RedisDBType.PRIMARY);
                        //删本表redis
                        RedisUtil.lRemove(Constants.TASK_JOB,integerStringEntry.getKey(),integerStringEntry.getValue(),RedisDBType.PRIMARY);
                    }
                }
                //处理完数据 释放锁
                RedisUtil.delete(lockKey, RedisDBType.PRIMARY);
            }
        }catch (Exception e){
            log.error("巡查发消息任务异常:{}", e);
        }finally {
            //释放锁
            RedisUtil.delete(lockKey, RedisDBType.PRIMARY);
        }

        return ReturnT.SUCCESS;
    }

    /**
     * 网格员考勤定时
     */
    @XxlJob("attendanceLineHandler")
    @DS("base")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> attendanceLineHandler(String param) {
        log.info("网格员考勤定时");
        XxlJobLogger.log("网格员考勤定时");
        //sign_in_status 签到状态,1正常,2迟到,3未签到7缺勤
        //把昨天 sign_in_status 签到状态是null的网格员的 sign_in_status状态改为3 未签到
        keeperDAO.updateStatusIn();
        //sign_out_status 签退状态,4正常,5早退,6未签退
        //把昨天 sign_out_status 签退状态是null的网格员的 sign_out_status状态改为6 未签退
        keeperDAO.updateStatusOut();
        //获取今天星期几
        String week = getWeek(new Date());
        //查枚举值表
        String weekDay =  keeperDAO.selectDictDataByType(Constants.SYS_DICT_DATA,week);
        //过滤掉不是工作日的数据
        List<BmAttendanceLineTaskVO> list = keeperDAO.getKeeperInfo(weekDay);
        if (!CollectionUtils.isEmpty(list)){
            keeperDAO.insertAttendanceLineInfo(list);
        }
//        keeperDAO.insertAttendanceLine();
        return ReturnT.SUCCESS;
    }

    /**
     * 获取今天日期获取对应枚举值
     * @author zl
     * @param date
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员考勤
     */
    private static String getWeek(Date date){
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int weekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(weekIndex<0){
            weekIndex = 0;
        }
        return weeks[weekIndex];
    }

    /**
     * 网格员轨迹清除定时
     */
    @XxlJob("keeperTrailsRemoveHandler")
    public ReturnT<String> keeperTrailsRemoveHandler(String param) {
        log.info("网格员轨迹清除定时");
        XxlJobLogger.log("网格员轨迹清除定时");
        try {
            bmKeeperTrailService.removeTrail(MongoDBType.PRIMARY);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ReturnT.SUCCESS;
    }


}
