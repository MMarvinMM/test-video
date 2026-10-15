package com.chinatower.platform.consumer.service;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.feign.client.AlarmFlowDeployFeign;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.SecondRecogFileVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.consumer.entity.EpochalGeoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class EpochalGeoAnalyticsService {

    @Resource
    private AlarmFlowDeployFeign alarmFlowDeployFeign;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 推送算法仓标识
     */
    private static final String EPOCHAL_GEO_ANALYTICS = "t_epochal_geo_analytics";

    /**
     * 单期遥感解析 处理服务
     * @param voList 待推送算法仓数据集合
     */
    public void analytics(List<EpochalGeoVo> voList) {
        voList.forEach(taskInfo->{
//            CompletableFuture.runAsync(() -> synAnalysisTask(taskInfo));
            synAnalysisTask(taskInfo);
        });
    }

    public void synAnalysisTask(EpochalGeoVo taskInfo) {
        List<SecondRecogSendVO> params = new ArrayList<>();
        params.add(convertTaskInfoParam(taskInfo));
        log.info("单期遥感解析调用算法仓接口参数: {}", JSON.toJSONString(params));
        Result<String> result = alarmFlowDeployFeign.synAnalysisTask(params);
        log.info("单期遥感解析调用算法仓接口返回结果: {}", JSON.toJSONString(result));
    }

    /**
     * 转换参数
     * @param taskInfo
     * @return
     */
    private SecondRecogSendVO convertTaskInfoParam(EpochalGeoVo taskInfo) {
        SecondRecogSendVO secondRecogSendVO = new SecondRecogSendVO();
        secondRecogSendVO.setTaskId(EPOCHAL_GEO_ANALYTICS + "/" + Constants.FIVE + "/" + taskInfo.getId().toString() + "/" + UUID.fastUUID());
        secondRecogSendVO.setAlgorithmCode(taskInfo.getAlgorithmCode());
        SecondRecogFileVO fileVO = new SecondRecogFileVO();
        fileVO.setType("image");
        String encryptUrl = fileUrlEncryptUtil.dealObsFileUrl(taskInfo.getOriginUrl());
        log.info("遥感算法originUrl={}, 加密后url:{}", taskInfo.getOriginUrl(), encryptUrl);
        String imgUrl = StringUtils.isNotEmpty(encryptUrl) ? encryptUrl : taskInfo.getOriginUrl();
        fileVO.setImgUrls(imgUrl);
        String baseLineEncryptUrl = fileUrlEncryptUtil.dealObsFileUrl(taskInfo.getBaseLineOriginUrl());
        log.info("遥感算法baseLineOriginUrl={}, 加密后url:{}", taskInfo.getBaseLineOriginUrl(), baseLineEncryptUrl);
        String baseLineUrl = StringUtils.isNotEmpty(baseLineEncryptUrl) ? baseLineEncryptUrl : taskInfo.getBaseLineOriginUrl();
        fileVO.setBaselinePicUrl(baseLineUrl);
        secondRecogSendVO.setFileInfo(fileVO);
        return secondRecogSendVO;
    }
}
