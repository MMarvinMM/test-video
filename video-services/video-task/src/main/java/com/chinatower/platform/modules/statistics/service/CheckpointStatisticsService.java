package com.chinatower.platform.modules.statistics.service;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.statistics.dao.CheckpointStatisticsDAO;
import com.chinatower.platform.modules.statistics.entity.CheckpointDateVO;
import com.chinatower.platform.utils.DataUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class CheckpointStatisticsService {
    @Resource
    private CheckpointStatisticsDAO checkpointStatisticsDAO;

    /**
     * 卡口设备告警统计-小时
     */
    @XxlJob("checkpointEventByHourHandler")
    public ReturnT<String> checkpointEventByHourHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.BAYONET_DEVICE_HOUR_MSG+dates[i]);
                    XxlJobLogger.log(Constants.BAYONET_DEVICE_HOUR_MSG+dates[i]);
                    //初始化执行时间
                    CheckpointDateVO dateVO = DataUtil.setCheckpointDateVOS(dates[i]);
                    List<CheckpointDateVO> list = getCheckpointEventByHourLists(dateVO);
                        DataUtil.returnString(initCheckpointEventByHour(dateVO, list));
                }
            }
        } else {
            log.info(Constants.BAYONET_DEVICE_HOUR_MSG);
            XxlJobLogger.log(Constants.BAYONET_DEVICE_HOUR_MSG);
            //初始化执行时间
            CheckpointDateVO dateVO = DataUtil.setCheckpointDateVO();
            List<CheckpointDateVO> list = getCheckpointEventByHourList(dateVO);
                return DataUtil.returnString(initCheckpointEventByHour(dateVO, list));
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 获取当前小时的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByHourList(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByHourList(dateVO);
    }

    /**
     * 获取当前小时的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByHourLists(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByHourLists(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean initCheckpointEventByHour(CheckpointDateVO dateVO, List<CheckpointDateVO> list) {
        try {
            //删除当天的数据
            checkpointStatisticsDAO.deleteCheckpointEventByHour(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                //插入当天的数据
                checkpointStatisticsDAO.insertCheckpointEventByHour(list);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 卡口设备告警统计-当天
     */
    @XxlJob("checkpointEventByDayHandler")
    public ReturnT<String> checkpointEventByDayHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.BAYONET_DEVICE_DAY_MSG+dates[i]);
                    XxlJobLogger.log(Constants.BAYONET_DEVICE_DAY_MSG+dates[i]);
                    //初始化执行时间
                    CheckpointDateVO dateVO = DataUtil.setCheckpointDateVOS(dates[i]);
                    List<CheckpointDateVO> list = getCheckpointEventByDayLists(dateVO);
                        DataUtil.returnString(initCheckpointEventByDay(dateVO, list));
                }
            }
        } else {
            log.info(Constants.BAYONET_DEVICE_DAY_MSG);
            XxlJobLogger.log(Constants.BAYONET_DEVICE_DAY_MSG);
            //初始化执行时间
            CheckpointDateVO dateVO = DataUtil.setCheckpointDateVO();
            List<CheckpointDateVO> list = getCheckpointEventByDayList(dateVO);
                return DataUtil.returnString(initCheckpointEventByDay(dateVO, list));
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 获取当天的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByDayList(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByDayList(dateVO);
    }

    /**
     * 获取当天的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByDayLists(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByDayLists(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean initCheckpointEventByDay(CheckpointDateVO dateVO, List<CheckpointDateVO> list) {
        try {
            //删除当天的数据
            checkpointStatisticsDAO.deleteCheckpointEventByDay(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                //插入当天的数据
                checkpointStatisticsDAO.insertCheckpointEventByDay(list);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 卡口设备告警统计-当月
     */
    @XxlJob("checkpointEventByMonthHandler")
    public ReturnT<String> checkpointEventByMonthHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.BAYONET_DEVICE_MONTH_MSG+dates[i]);
                    XxlJobLogger.log(Constants.BAYONET_DEVICE_MONTH_MSG+dates[i]);
                    //初始化执行时间
                    CheckpointDateVO dateVO = DataUtil.setCheckpointDateVOS(dates[i]);
                    List<CheckpointDateVO> list = getCheckpointEventByMonthLists(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(initCheckpointEventByMonth(dateVO, list));
                    }
                }
            }
        } else {
            log.info(Constants.BAYONET_DEVICE_MONTH_MSG);
            XxlJobLogger.log(Constants.BAYONET_DEVICE_MONTH_MSG);
            //初始化执行时间
            CheckpointDateVO dateVO = DataUtil.setCheckpointDateVO();
            List<CheckpointDateVO> list = getCheckpointEventByMonthList(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(initCheckpointEventByMonth(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 获取当月的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByMonthList(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByMonthList(dateVO);
    }

    /**
     * 获取当月的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByMonthLists(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByMonthLists(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean initCheckpointEventByMonth(CheckpointDateVO dateVO, List<CheckpointDateVO> list) {
        try {
            //删除当月的数据
            checkpointStatisticsDAO.deleteCheckpointEventByMonth(dateVO);
            //插入当月的数据
            checkpointStatisticsDAO.insertCheckpointEventByMonth(list);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 卡口设备告警统计-当年
     */
    @XxlJob("checkpointEventByYearHandler")
    public ReturnT<String> checkpointEventByYearHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=4) {
                    String year = dates[i].substring(0,4);
                    log.info(Constants.BAYONET_DEVICE_YEAR_MSG+dates[i]);
                    XxlJobLogger.log(Constants.BAYONET_DEVICE_YEAR_MSG+dates[i]);
                    //初始化执行时间
                    CheckpointDateVO dateVO = DataUtil.setCheckpointDateVOYear(dates[i]);
                    List<CheckpointDateVO> list = getCheckpointEventByYearLists(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(initCheckpointEventByYear(dateVO, list));
                    }
                }
            }
        } else {
            log.info(Constants.BAYONET_DEVICE_YEAR_MSG);
            XxlJobLogger.log(Constants.BAYONET_DEVICE_YEAR_MSG);
            //初始化执行时间
            CheckpointDateVO dateVO = DataUtil.setCheckpointDateVO();
            List<CheckpointDateVO> list = getCheckpointEventByYearList(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(initCheckpointEventByYear(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 获取当年的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByYearList(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByYearList(dateVO);
    }

    /**
     * 获取当年的数据
     * @param dateVO
     * @return
     */
    private List<CheckpointDateVO> getCheckpointEventByYearLists(CheckpointDateVO dateVO) {
        return checkpointStatisticsDAO.getCheckpointEventByYearLists(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean initCheckpointEventByYear(CheckpointDateVO dateVO, List<CheckpointDateVO> list) {
        try {
            //删除当年的数据
            checkpointStatisticsDAO.deleteCheckpointEventByYear(dateVO);
            //插入当年的数据
            checkpointStatisticsDAO.insertCheckpointEventByYear(list);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

}
