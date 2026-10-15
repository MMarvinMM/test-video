package com.chinatower.device.schedule;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.device.dto.HornLockInfo;
import com.chinatower.device.entity.vo.LockResultVo;
import com.chinatower.device.entity.dto.TalkReq;
import com.chinatower.device.mapper.HornDeviceMapper;
import com.chinatower.device.repository.impl.IHornDeviceRepositoryImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class HornDeviceSchedule {

    @Autowired
    private IHornDeviceRepositoryImpl iHornDeviceRepository;
    @Autowired
    private HornDeviceMapper hornDeviceMapper;

    @XxlJob("releaseHornLock")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> releaseLock(String param) {
        try{
            XxlJobLogger.log("开始执行定时任务！");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowDate = DateUtils.getNowDate();
            //十分钟的毫秒
            long time = 10 * 60 * 1000;
            Date tenBefore = new Date(nowDate.getTime() - time);
            String format = sdf.format(tenBefore);
            ////查询设备和锁关系表中所有时间大于十分钟且状态为非工作中的设备
            List<HornLockInfo> hornLockInfoList = hornDeviceMapper.queryLockInfoList(format);
            XxlJobLogger.log("需要释放的锁集合{}",JSON.toJSONString(hornLockInfoList));
            if (!CollectionUtils.isEmpty(hornLockInfoList)) {
                NetConnectUtils.RequestParamter<TalkReq> paramter = new NetConnectUtils.RequestParamter<>();
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                TalkReq talkReq;
                List<TalkReq> talkReqList = new ArrayList<>();
                for (HornLockInfo hornLockInfo:hornLockInfoList){
                    talkReq = new TalkReq();
                    BeanUtils.copyProperties(hornLockInfo,talkReq);
                    talkReqList.add(talkReq);
                    XxlJobLogger.log("调用释放锁接口入参{}",JSON.toJSONString(talkReq));
                    LockResultVo lockResultVo = iHornDeviceRepository.releaseLock(talkReq, paramter, path);
                    XxlJobLogger.log("调用释放锁接口返回结果{}",JSON.toJSONString(lockResultVo));
                }
                //释放完锁ID删除设备锁表中对应数据
                hornDeviceMapper.deleteLockInfo(talkReqList);
            }
            XxlJobLogger.log("定时任务执行完毕！");
            return ReturnT.SUCCESS;
        }catch (Exception e){
            XxlJobLogger.log("定时任务释放锁异常:",e);
            return ReturnT.FAIL;
        }
    }

}
