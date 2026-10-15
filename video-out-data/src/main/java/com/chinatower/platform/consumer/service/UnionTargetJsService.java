package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.platform.consumer.item.UnionTargetJsReport;
import com.chinatower.platform.provider.VideoKafkaController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

/**
 * 目标实时动态数据监视
 * Created by wanglei on 2022/4/26.
 * @author li.xufu
 */
@Slf4j
@Component
public class UnionTargetJsService {

    @Autowired
    private VideoKafkaController videoKafkaController;

    public void dealMsg(String message) {
        log.info("目标实时动态数据监视消费信息:{}", message);


        UnionTargetJsReport unionTargetJsReport = JSONArray.parseObject(message, UnionTargetJsReport.class);
        this.dealTargetData(unionTargetJsReport);

        log.info("目标实时动态数据监视处理完成");
    }

    public void dealTargetData(UnionTargetJsReport unionTargetJsReport) {
        ModelMap model = new ModelMap();
        model.addAttribute("topic", "pushRadarMonitorPointToFishery");
        model.addAttribute("message", JSON.toJSONString(unionTargetJsReport));
        videoKafkaController.sendMessage(model);
    }
}
