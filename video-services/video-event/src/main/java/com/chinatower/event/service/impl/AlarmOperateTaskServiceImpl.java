package com.chinatower.event.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.OperationTaskVO;
import com.chinatower.event.service.AlarmOperateTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * @Description 告警研判任务相关服务实现类
 * @Author hyq
 * @Date 2022/7/11
 * @Version release_v1.8.00
 **/
@Service
@Slf4j
public class AlarmOperateTaskServiceImpl implements AlarmOperateTaskService {

    @Value("${agent.operation.urlPrefix}")
    private String agentOperationUrlPrefix;
    @Value("${agent.operation.listUrl}")
    private String listUrl;
    @Value("${agent.operation.modifyUrl}")
    private String modifyUrl;



    @Override
    public Result<List<OperationTaskVO>> getTaskList(AlarmOperateTaskDTO dto) {
        try {
            String url = agentOperationUrlPrefix + listUrl;
            String requestBody = JSONUtil.toJsonStr(dto);
            log.info("调用操作任务列表接口，URL: {}, 请求参数: {}", url, requestBody);

            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .timeout(30000)
                    .execute();

            if (response.getStatus() == 200) {
                JSONObject responseBody = JSONObject.parseObject(response.body());
                log.info("操作任务列表接口响应: {}", responseBody);
                List<OperationTaskVO> taskList = JSONObject.parseArray(responseBody.getString("data"), OperationTaskVO.class);
                return Result.data(taskList);
            } else {
                log.error("调用操作任务列表接口失败，状态码: {}, 响应: {}", response.getStatus(), response.body());
            }

        } catch (Exception e) {
            log.error("调用操作任务列表接口异常", e);
        }
        return Result.fail("调用操作任务列表接口失败");
    }

    @Override
    public Result modifyTask(AlarmOperateTaskDTO dto) {
        try {
            String url = agentOperationUrlPrefix + modifyUrl;
            String requestBody = JSONUtil.toJsonStr(dto);
            log.info("调用操作任务列表接口，URL: {}, 请求参数: {}", url, requestBody);

            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .timeout(30000)
                    .execute();

            if (response.getStatus() == 200) {
                JSONObject responseBody = JSONObject.parseObject(response.body());
                log.info("操作任务列表接口响应: {}", responseBody);
                return Result.data(responseBody.get("data"));
            } else {
                log.error("调用操作任务列表接口失败，状态码: {}, 响应: {}", response.getStatus(), response.body());
            }

        } catch (Exception e) {
            log.error("调用操作任务列表接口异常", e);
        }
        return Result.fail("调用操作任务列表接口失败");
    }


}
