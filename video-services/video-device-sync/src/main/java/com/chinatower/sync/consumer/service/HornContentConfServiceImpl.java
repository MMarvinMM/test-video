package com.chinatower.sync.consumer.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IHornDeviceService;
import com.chinatower.sync.consumer.constants.HornTaskEnum;
import com.chinatower.sync.consumer.dao.HornContentConfDAO;
import com.chinatower.sync.consumer.item.HornTxtFileVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author willem
 * @date 2024/10/21
 */
@Slf4j
@Component
public class HornContentConfServiceImpl {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    @Autowired
    HornContentConfDAO hornContentConfDAO;

    @Resource
    private IHornDeviceService iHornDeviceService;


    /**
     * 更新文件url
     * @param params 广播平台回调
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFileUrlByAudioId(List<String> params){
        log.info("从kafka中获取到的文件信息结果为：{}",params);
        if (CollectionUtil.isEmpty(params)) {
            return;
        }
        try {
            List<Map<String, String>> maps = new ArrayList<>();
            params.forEach(param->{
                Map map = JSON.parseObject(param, Map.class);
                maps.add(map);
            });
            scheduler.schedule(() -> {
                log.info("从kafka中获取到的广播消息结果为：{}",maps);
                hornContentConfDAO.updateAudioInfoByAudioId(maps);
                log.info("从kafka中获取到的广播消息处理成功");
            }, 3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("更新文件url异常:", e);
        }
    }

    public void updateFileUrlByTxtId(List<String> params) {
        log.info("从kafka中获取到的文本信息结果为：{}",params);
        if (CollectionUtil.isEmpty(params)) {
            return;
        }
        try {
            List<String> fileIdList = Lists.newArrayList();
            List<HornTxtFileVO> hornTxtFileVOList = Lists.newArrayList();
            params.forEach(param->{
                HornTxtFileVO txtFileVO = JSON.parseObject(param, HornTxtFileVO.class);
                if (txtFileVO != null) {
                    log.info("当前创建文本id：{},创建文本状态:{}", txtFileVO.getTxtId(), txtFileVO.getFileStatus());
                    if (StringUtils.isNotEmpty(txtFileVO.getTxtId())
                            && StringUtils.equals(HornTaskEnum.FileStatus.FILE_STATUS_SUCCEED.getFileStatusValue(), txtFileVO.getFileStatus())) {
                        fileIdList.add(txtFileVO.getTxtId());
                        hornTxtFileVOList.add(txtFileVO);
                    }
                }
            });
            hornContentConfDAO.updateTxtInfoByTxtIdBatch(hornTxtFileVOList);
            scheduler.schedule(() -> {
                log.info("文转语成功 fileIdList:{}", fileIdList);
                Result<String> result = iHornDeviceService.callHornTaskPlay(fileIdList);
                log.info("文转语成功调用接口启动任务或播放, result:{}", JSON.toJSONString(result));
            }, 3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("更新文本url异常:", e);
        }
    }
}
