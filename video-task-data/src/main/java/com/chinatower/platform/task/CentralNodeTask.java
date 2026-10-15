package com.chinatower.platform.task;

import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.service.CentralNodeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/7 15:12
 */
@Component
@Slf4j
public class CentralNodeTask {

    @Autowired
    private CentralNodeService centralNodeService;

    @XxlJob("centralNodeHandler")
    public ReturnT<String> centralNodeHandler(String param) {
        long startTime = System.currentTimeMillis();
        log.info("CentralNodeHandler-开始时间:{}",startTime);
        XxlJobLogger.log("CentralNodeHandler开始执行-开始时间为:{}", startTime);

        centralNodeService.pushEsWarningOrderByOfs(param);

        long endTime = System.currentTimeMillis();
        log.info("CentralNodeHandler开始执行-结束时间:{}",endTime - startTime);
        XxlJobLogger.log("CentralNodeHandler执行完成-耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
