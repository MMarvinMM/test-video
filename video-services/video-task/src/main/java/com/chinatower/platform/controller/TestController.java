package com.chinatower.platform.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.service.*;
import com.chinatower.platform.modules.alarm.task.JudgeSendMsgTask;
import com.chinatower.platform.modules.alarm.task.OperationAiJudgeNotificationTask;
import com.chinatower.platform.modules.dept.service.DeptService;
import com.chinatower.platform.modules.gis.service.LayerService;
import com.chinatower.platform.modules.keeper.dao.KeeperDAO;
import com.chinatower.platform.modules.keeper.entity.InspectionTaskVO;
import com.chinatower.platform.modules.keeper.service.KeeperService;
import com.chinatower.platform.modules.message.task.MessageSendInfoTask;
import com.chinatower.platform.modules.statistics.service.CheckpointStatisticsService;
import com.chinatower.platform.modules.user.service.UserService;
import com.chinatower.platform.modules.video.service.VideoService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * video-task
 * @ClassName GuardController
 * @Description 看守卫服务
 * @Author chenshaowu
 * @Date 2022/2/21
 **/
@RestController
@Slf4j
@Api(value = "task测试模块",tags = "task测试模块")
public class TestController {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private KeeperService keeperService;

    @Autowired
    private KeeperDAO keeperDAO;

    @Autowired
    private CheckpointStatisticsService checkpointStatisticsService;

    @Resource
    MessageSendInfoTask messageSendInfoTask;

    @Resource
    private UserService userService;

    @Resource
    private DeptService deptService;

    @Resource
    private VideoService videoService;

    @Resource
    private AlarmVideoUpdateService alarmVideoUpdateService;

    @Autowired
    private OperationAiJudgeNotificationService operationAiJudgeNotificationService;

    private static final String SUCCESS = "success";



    //生产测试接口
    @PostMapping("/test/presetSecondRecong")
    public Result presetSecondRecong(@RequestBody Map<String, Object> o) {
        log.info("presetSecondRecong入参原始值{}", JSON.toJSONString(o));
        return  alarmVideoUpdateService.presetSecondRecong(o);
    }
    //测试接口
    @ApiOperation(value = "syncAlgorithmWeight")
    @PostMapping("/test/syncAlgorithmWeight")
    public Result syncAlgorithmWeight() {
        operationAiJudgeNotificationService.syncAlgorithmWeight("4c60a56516504719a1b5e35dc6a33c534658");
        return Result.success("调用成功");
    }
    /**
     * 功能描述: 运营管理统计分析定时任务
     */
    @ApiOperation(value = "运营管理统计分析定时任务")
    @PostMapping("/task/test/eventJudgeAlarmHandler")
    public String  eventJudgeAlarmHandler(String methodName, String param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InvocationTargetException {

        Class<?> cls = Class.forName("com.chinatower.platform.modules.statistics.service.StatisticsService");
        // 获取spring中的bean对象
        Object bean = applicationContext.getBean(cls);
        // 获取mybatis方法.形参是待执行方法名
        Method method = cls.getMethod(methodName, String.class);
        // 执行方法
        method.invoke(bean, param);

        return SUCCESS;
    }

    /**
     * 功能描述：触发铁塔视角-统计分析定时任务
     * @param methodName
     * @param param
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @PostMapping("/task/test/eventJudgeAlarmHandlers")
    public String  eventJudgeAlarmHandlers(String methodName, String param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InvocationTargetException {

        Class<?> cls = Class.forName("com.chinatower.platform.modules.statistics.service.TowerStatisticsService");
        // 获取spring中的bean对象
        Object bean = applicationContext.getBean(cls);
        // 获取mybatis方法.形参是待执行方法名
        Method method = cls.getMethod(methodName, String.class);
        // 执行方法
        method.invoke(bean, param);

        return SUCCESS;
    }

    /**
     * 功能描述：触发统计分析定时任务
     * @param methodName
     * @param param
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @PostMapping("/task/test/eventJudgeAlarmHandlerss")
    public String  eventJudgeAlarmHandlerss(String methodName, String param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InvocationTargetException {

        Class<?> cls = Class.forName("com.chinatower.platform.modules.statistics.task.StatisticsAlarmTask");
        // 获取spring中的bean对象
        Object bean = applicationContext.getBean(cls);
        // 获取mybatis方法.形参是待执行方法名
        Method method = cls.getMethod(methodName, String.class);
        // 执行方法
        method.invoke(bean, param);

        return SUCCESS;
    }

    /**
     * 功能描述：卡口设备告警统计定时任务
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/checkpoint")
    public String  saveVideoPlanHisTaskHandler(@RequestBody String param) throws Exception {
        checkpointStatisticsService.checkpointEventByDayHandler(param);
        checkpointStatisticsService.checkpointEventByHourHandler(param);
        checkpointStatisticsService.checkpointEventByMonthHandler(param);
        checkpointStatisticsService.checkpointEventByYearHandler(param);
        return SUCCESS;
    }

    @Autowired
    private JudgeSendMsgTask judgeSendMsgTask;

    /**
     * 功能描述：研判环节告警短信统计下发
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/judgeSendMsg")
    public String  judgeSendMsg(@RequestBody String param) throws Exception {
        judgeSendMsgTask.judgeSendMsgHandler(param);
        return SUCCESS;
    }

    @Autowired
    private LayerService layerService;

    /**
     * 功能描述：创建“其他类型”数据类型的表信息
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/createQtTypeInfo")
    public String createQtTypeInfo(@RequestBody String param) throws Exception {
        layerService.createQtTypeInfo(param);
        return SUCCESS;
    }

    @Autowired
    private AlarmTransferService alarmTransferService;

    /**
     * 功能描述：测试告警转移定时任务
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/test2")
    public String  alarmTransfer(@RequestBody String param) throws Exception {
        alarmTransferService.alarmTransfer();
        return SUCCESS;
    }

    @Autowired
    private OverdueRemindSendService overdueRemindSendService;
    /**
     * 功能描述：测试超期提醒定时任务
     */
    @PostMapping("/task/test/overdueRemindSendMsg")
    public String  overdueRemindSendMsg(@RequestBody String param) throws Exception {
        overdueRemindSendService.overdueRemindSendMsg();
        return SUCCESS;
    }

    @Autowired
    private DelaySendMsgService delaySendMsgService;

    @Autowired
    private RemoveDelayMsgService removeDelayMsgService;

    /**
     * 功能描述：延时消息推送
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/delaySendMsg")
    public String  delaySendMsg(@RequestBody String param) throws Exception {
        delaySendMsgService.sendDelaySendMsg(param);
        return SUCCESS;
    }

    /**
     * 功能描述：清除延时消息推送记录
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/RemoveDelayMsgTask")
    public String  RemoveDelayMsgTask(@RequestBody String param) throws Exception {
        removeDelayMsgService.removeDelayMsg();
        return SUCCESS;
    }


    /**
     * 功能描述：手动执行网格员巡查任务创建
     * @return
     */
    @GetMapping("/keeper/{date}")
    public String testKeeper(@PathVariable("date") String date) {
        DateTime now = DateUtil.parse(date);
        DateTime tom = DateUtil.offsetDay(now, 1);
        String format = DateUtil.format(tom, "yyyy-MM-dd");
        log.info("手动网格员巡查任务定时");
        List<InspectionTaskVO> list = keeperDAO.getInspectionConfListTest(format);
        if(!CollectionUtils.isEmpty(list)){
            keeperDAO.insertInspectionTask(list);
            keeperDAO.updateNoRepeatConfStatus();
            keeperDAO.updateRepeatConfStatus();
        }
        return SUCCESS;
    }

    /**
     * 功能描述：网格员巡查任务定时
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/inspectionTaskHandler")
    public String  inspectionTaskHandler(@RequestBody String param) throws Exception {
        keeperService.inspectionTaskHandler(param);
        return SUCCESS;
    }

    /**
     * 功能描述：删除一个月前的消息数据
     * @return
     */
    @GetMapping("/task/test/delDataMonthAgo")
    public String delDataMonthAgo() {
        messageSendInfoTask.delDataMonthAgo(null);
        return SUCCESS;
    }

    /**
     * 功能描述：网格员考勤定时
     * @return
     */
    @GetMapping("/task/test/attendanceLineHandler")
    public String attendanceLineHandler() {
        keeperService.attendanceLineHandler(null);
        return SUCCESS;
    }

    /**
     * 功能描述：用户停用定时
     * 针对到了账号有效期的铁塔用户
     * @return
     */
    @GetMapping("/task/test/uptUserStatusHandler")
    public String uptUserStatusHandler() {
        userService.uptUserStatusHandler(null);
        return SUCCESS;
    }

    /**
     * 功能描述：删除录像下载记录
     * 删除15天前的录像下载记录，并且在每月的最后一天创建下个月的下载表
     * @return
     */
    @GetMapping("/task/test/recordDownloadTaskHandler")
    public String recordDownloadTaskHandler() {
        videoService.recordDownloadTaskHandler(null);
        return SUCCESS;
    }

    /**
     * 功能描述：同步更新组织管理区域定时任务
     * @return
     */
    @GetMapping("/task/test/dealDeptStreetHandler")
    public String dealDeptStreetHandler() {
        deptService.dealDeptStreetHandler(null);
        return SUCCESS;
    }

    @PostMapping("/task/alarm/test")
    public String sinkingNodeAlarmHandler(@RequestBody String param) {
        RedisUtil.set(Constants.ALARM_PUSH_TASK_KEY, param, RedisDBType.PRIMARY);
        return RedisUtil.get(Constants.ALARM_PUSH_TASK_KEY, RedisDBType.PRIMARY);
    }
}
