package com.chinatower.video.modules.videopreset.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.AssembleUrlUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.UpdateEntity;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.entity.SnapshotBackVO;
import com.chinatower.video.modules.query.dao.YjbSnapshotTaskMongoDAO;
import com.chinatower.video.modules.videopreset.dataobject.YjbSnapshotTaskMongo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class YjbSnapshotCallbackService {
    private static final String COLLECTION = "vm_device_snapshot_task";
    private static final String LOCK_KEY = "yjb_snapshot";
    private static final String STATUS_CALLBACK_FAILED = "CALLBACK_FAILED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class);

    @Resource
    private YjbSnapshotTaskMongoDAO taskDAO;
    @Resource
    private SystemFeignClient systemFeignClient;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private RedisTemplate redisTemplate;

    public Result<Map<String, String>> callback(String uniqueId, SnapshotBackVO callback) {
        log.info("应急部抓图回调, uniqueId={}, callback={}", uniqueId, JSON.toJSONString(callback));
        if (StringUtils.isEmpty(uniqueId) || callback == null) {
            return Result.fail("回调参数不能为空");
        }
        YjbSnapshotTaskMongo item = findByUniqueId(uniqueId);
        if (item == null) {
            return Result.fail("未找到抓图明细");
        }
        if (!"0".equals(callback.getError_code())) {
            updateFailure(uniqueId, "基础平台抓图失败，errorCode=" + callback.getError_code());
            return Result.fail("基础平台抓图失败");
        }
        String imageUrl = callback.getImagePath();
        if (StringUtils.isEmpty(imageUrl)) {
            updateFailure(uniqueId, "基础平台回调图片地址为空");
            return Result.fail("回调图片地址不能为空");
        }
        String originUrl = AssembleUrlUtil.getAssembleUrl(callback.getAccessNode(), imageUrl, isCorrectLocation());
        Map<String, String> encryptedUrls = fileUrlEncryptUtil.dealObsFileUrlBatch(Collections.singletonList(originUrl));
        String encryptedUrl = encryptedUrls.get(originUrl);
        UpdateEntity update = new UpdateEntity();
        update.set("imgUrl", originUrl);
        update.set("updateTime", new Date());
        update.set("snapshotStatus", STATUS_COMPLETED);
        update.set("errorMessage", null);
        taskDAO.updateMulti(queryByUniqueId(uniqueId), update, MongoDBType.PRIMARY);
        releaseTaskLockIfCompleted(item.getTaskId());
        Map<String, String> result = new HashMap<>(2);
        result.put("fileUrl", encryptedUrl);
        result.put("originUrl", originUrl);
        return Result.data(result);
    }

    private boolean isCorrectLocation() {
        AjaxResult configResult = systemFeignClient.getConfValue("dependence_manage");
        if (!"200".equals(String.valueOf(configResult.get(AjaxResult.CODE_TAG)))) {
            return true;
        }
        SysConfig config = JSON.parseObject(JSON.toJSONString(configResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
        return config == null || !"0".equals(config.getConfigValue());
    }

    private YjbSnapshotTaskMongo findByUniqueId(String uniqueId) {
        return taskDAO.get(queryByUniqueId(uniqueId), MongoDBType.PRIMARY);
    }

    private QueryEntity queryByUniqueId(String uniqueId) {
        QueryEntity query = new QueryEntity(COLLECTION, YjbSnapshotTaskMongo.class);
        query.eq("uniqueId", uniqueId);
        return query;
    }

    private void updateFailure(String uniqueId, String errorMessage) {
        UpdateEntity update = new UpdateEntity();
        update.set("snapshotStatus", STATUS_CALLBACK_FAILED);
        update.set("errorMessage", errorMessage);
        update.set("updateTime", new Date());
        taskDAO.updateMulti(queryByUniqueId(uniqueId), update, MongoDBType.PRIMARY);
    }

    private void releaseTaskLockIfCompleted(String taskId) {
        QueryEntity query = new QueryEntity(COLLECTION, YjbSnapshotTaskMongo.class);
        query.eq("taskId", taskId);
        java.util.List<YjbSnapshotTaskMongo> items = taskDAO.findList(query, null, MongoDBType.PRIMARY);
        if (!items.isEmpty()
                && items.stream()
                .allMatch(item -> !StringUtils.isEmpty(item.getImgUrl()))) {
            redisTemplate.execute(RELEASE_LOCK_SCRIPT, Collections.singletonList(LOCK_KEY), taskId);
        }
    }
}
