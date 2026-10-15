package com.chinatower.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.chinatower.common.core.annotation.Trace;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.outdata.api.RadarMsg;
import com.chinatower.platform.consumer.dao.MongoUtilDAO;
import com.chinatower.platform.consumer.dao.VmOrderSensorDevicesDAO;
import com.chinatower.platform.consumer.service.RadarMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.chinatower.message.api.model.RadarObject.SOURCE;
import static com.chinatower.message.api.model.RadarObject.SOURCE_TYPE;
import static com.chinatower.platform.consumer.service.RadarMsgHandler.RadarMsgMongoModel.UPDATE_TIMESTAMP;

@Slf4j
@Validated
@RestController
public class RadarMsgController {
    private static final String PREFIX_LIST = "radarRegisterList";
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    MongoUtilDAO<RadarMsgHandler.RadarMsgMongoModel> mongoUtilDAO;
    @Resource
    VmOrderSensorDevicesDAO vmOrderSensorDevicesDAO;

    @Trace
    @PostMapping("/radarMsg/register")
    public Result<Boolean> register(@RequestBody RadarMsg.Req<String> req) {

        try {
            //tenantId:用户名:行业编码
            String valueContent = req.getReqBody();
            //放入redis列表中
            registerInRedis(valueContent);

            return Result.success("成功");
        } catch (Exception e) {
            log.error("操作失败: ", e);
            return Result.fail(e.getMessage());
        }
    }

    @Trace
    @PostMapping("/radarMsg/showCurrentTimeTargetList")
    Result<List<RadarMsg.TargetResponse>> showCurrentTimeTargetList(@RequestBody RadarMsg.TargetReq req) {

        List<String> list =
                vmOrderSensorDevicesDAO.seleceDeviceCodeListByTenantId(req.getTenantId(), req.getIndustryCode());
        log.info("list = {}", list);
        DateTime before1min = DateUtil.offset(DateUtil.date(), DateField.MINUTE, -1);
        log.info("before1min = {}", before1min.getTime());
        Query query = new Query(
                Criteria
                        .where(SOURCE).in(list)
                        .and(UPDATE_TIMESTAMP).gt(before1min.getTime())
        );
        List<RadarMsgHandler.RadarMsgMongoModel> modelList = mongoUtilDAO.queryMany(MongoDBType.PRIMARY, query,
                RadarMsgHandler.RadarMsgMongoModel.class, RadarMsgHandler.RadarMsgMongoModel.COLLECTION);
        if (CollectionUtils.isNotEmpty(modelList)) {
            return Result.data(modelList.stream().map(it -> {
                RadarMsg.TargetResponse response = new RadarMsg.TargetResponse();
                BeanUtil.copyProperties(it,response);
                response.setId(it.getTargetId());
                return response;
            }).collect(Collectors.toList()));
        }
        return Result.success("ok");
    }

    private void registerInRedis(String valueContent) {

        DateTime after30s = DateUtil.offset(new Date(), DateField.SECOND, 30);
        redisTemplate.opsForHash().put(PREFIX_LIST, valueContent, after30s.getTime());
    }


}
