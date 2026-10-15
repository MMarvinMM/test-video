package com.chinatower.emergency.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.emergency.service.impl.TencentOperatorService;
import com.chinatower.emergency.service.impl.TranslateHelper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

/**
 * video-bdm
 * @ClassName QueryAnswerController
 * @Deacription 数字人问答模块
 * @Author chenmf
 * @Date 2024/06/27
 * @Version 1.0
 **/
@Api(tags = "腾讯大模型知识库调用")
@RestController
@Slf4j
public class QueryAnswerController {

    @Autowired
    private TencentOperatorService tencentOperatorService;

    @Autowired
    private TranslateHelper translateHelper;

    @PostMapping("/chat/vlb")
    public Object vlb(@RequestBody JSONObject content) {
        try {

            JSONObject params = new JSONObject();

            params.put("messages",content.get("messages"));

            return  Result.data(tencentOperatorService.reply2VLB(params));
        }catch (Exception e){
            log.error("多模态接口报错{0}",e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/chat/prompt")
    public Result<JSONObject> prompt(@RequestBody JSONObject content) {
        try {

            log.info("开始" + content.toJSONString());

            OkHttpClient client0 = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).callTimeout(15, TimeUnit.SECONDS).build();

            okhttp3.RequestBody requestBody = okhttp3.RequestBody
                    .create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), content.toJSONString());
            Request request0 = new Request.Builder()
                    .post(requestBody)
                    .url("http://10.37.1.27:32767/prompt")
                    .build();

            Call call = client0.newCall(request0);

            Response response = call.execute();

            assert response.body() != null;

            String body = response.body().string();

            log.info("结束====");

            log.info(body);

            return Result.data(JSON.parseObject(body));
        }catch (Exception e){
            log.error("画图报错{0}",e);
            return Result.fail(e.getMessage());
        }
    }


    /**
     * 腾讯工作流接口
     * @return 返回工作流实体
     */
    @PostMapping("/chat/translate")
    public Result<String> translate(@RequestBody @Validated String content) {
        try {
            return  Result.data(translateHelper.translateV2(content));
        }catch (Exception e){
            log.error("翻译报错{0}",e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 腾讯工作流接口
     * @return 返回工作流实体
     */
    @PostMapping("/chat/tencentOp")
    public Result<JSONObject> tencentOp(@RequestBody @Validated String content) {

        try {

            JSONObject result = tencentOperatorService.reply2Tencent(content,1);

            return Result.data(result);
        }catch (Exception e){
            // 如果报错继续走百度数字人流程
            log.error("大模型报错{0}",e);
            return Result.fail(e.getMessage());
        }

    }
}
