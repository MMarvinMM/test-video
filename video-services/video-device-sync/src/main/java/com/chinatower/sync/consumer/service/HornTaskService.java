package com.chinatower.sync.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.device.api.IHornDeviceService;
import com.chinatower.device.entity.dto.VmHornTaskConfDO;
import com.chinatower.device.entity.dto.VmHornTaskDiffNodeRelDO;
import com.chinatower.sync.consumer.constants.HornTaskEnum;
import com.chinatower.sync.consumer.dao.HornTaskConfDAO;
import com.chinatower.sync.consumer.item.HornTaskStatusReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HornTaskService {

    @Autowired
    private HornTaskConfDAO hornTaskConfDAO;

//    @Resource
//    private IHornDeviceService iHornDeviceService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateHornTaskStatus(List<String> messages) {
        log.info("从TOPIC:smartbc_task_status中，广播任务状态消费信息:{}, size:{}", messages, messages.size());
        List<HornTaskStatusReq> hornTaskStatusList = new ArrayList<>();
        for (String message : messages) {
            HornTaskStatusReq item = JSON.parseObject(message, HornTaskStatusReq.class);
            item.setStatus((Objects.equals(item.getStatus(), HornTaskEnum.TaskStatus.TASK_STATUS_2.getHornTaskStatus())
                    || Objects.equals(item.getStatus(), HornTaskEnum.TaskStatus.TASK_STATUS_3.getHornTaskStatus()))
                    ? HornTaskEnum.ExecutStatus.EXECUT_STATUS_1.getExecutStatusValue() : HornTaskEnum.ExecutStatus.EXECUT_STATUS_0.getExecutStatusValue());
            hornTaskStatusList.add(item);
        }
        hornTaskConfDAO.updateHornTaskRelStatus(hornTaskStatusList);
        List<VmHornTaskDiffNodeRelDO> vmHornTaskDiffNodeRelDOList = hornTaskConfDAO.selectVmHornTaskDiffNodeRelByHornTaskId(hornTaskStatusList);
        if (CollectionUtils.isNotEmpty(vmHornTaskDiffNodeRelDOList)) {
            Map<String, List<VmHornTaskDiffNodeRelDO>> map = vmHornTaskDiffNodeRelDOList.stream().collect(Collectors.groupingBy(VmHornTaskDiffNodeRelDO::getTaskId));
            List<VmHornTaskConfDO> vmHornTaskConfDOList = Lists.newArrayList();
            map.forEach((taskId, groupedList) -> {
                int mainTaskStatus;
                VmHornTaskConfDO updateOne = new VmHornTaskConfDO();
                long countOfOnes = groupedList.stream()
                        .map(VmHornTaskDiffNodeRelDO::getExecutStatus)
                        .filter(status -> status == 1)
                        .count();
                if (countOfOnes == 0) {
                    mainTaskStatus = 0; // 全为0
                } else if (countOfOnes == groupedList.size()) {
                    mainTaskStatus = 1; // 全为1
                } else {
                    mainTaskStatus = 2; // 至少有一个1，但不是全为1
                }
                updateOne.setExecutStatus(mainTaskStatus);
                updateOne.setId(Long.valueOf(taskId));
                vmHornTaskConfDOList.add(updateOne);
            });
            hornTaskConfDAO.updateHornTaskStatus(vmHornTaskConfDOList);

//            try {
//                for (VmHornTaskConfDO playParam : vmHornTaskConfDOList) {
//                    iHornDeviceService.syncTaskInfoToAlgorithmMongo(playParam.getId());
//                }
//            }
//            catch (Exception e) {
//                log.error("同步算法仓失败e:", e);
//            }
        }
    }
}
