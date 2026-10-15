package com.chinatower.platform.modules.message.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.modules.message.mapper.MessageMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定期删除 vm_message_send_info_t表数据
 */
@Component
@Slf4j
public class MessageSendInfoTask {


    @Resource
    MessageMapper messageMapper;

    /**
     * 删除一个月前的消息数据
     */
    @XxlJob("deleteDataMonthAgo")
    @DS("base")
    public ReturnT<String> delDataMonthAgo(String param) {

        if (StringUtils.isEmpty(param)) {
            param = "30";
        }
        int days = Integer.parseInt(param);
        DateTime now = DateUtil.date();
        if (days > 0) {
            DateTime dateTime = DateUtil.offsetDay(now, -days);
            log.info("--每日删除一个月前消息的定时开始l--");
            String month = DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss");
            log.info("--当前时间:{}, {}天前时间:{}--", now, param, month);
            int count = messageMapper.delDataMonthAgo(month);
            log.info("--每日删除一个月前消息的定时结束 删除条数:{}--", count);
        } else {
            log.info("--删除一个月前消息的定时参数错误--{}", param);
        }
        return ReturnT.SUCCESS;
    }
}
