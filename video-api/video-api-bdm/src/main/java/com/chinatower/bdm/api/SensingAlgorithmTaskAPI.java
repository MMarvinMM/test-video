package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.AlgorithmPicResultVO;
import com.chinatower.bdm.api.factory.SensingAlgorithmTaskFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "SensingAlgorithmTaskAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = SensingAlgorithmTaskFallbackFactory.class)
public interface SensingAlgorithmTaskAPI {

    /**
     * 更新算法仓图片识别结果
     *
     * @return 是否成功
     */
    @PostMapping("/sensingAlgorithmTaskInfo/updateAlgorithmPicResult")
    Result<Boolean> updateAlgorithmPicResult(@RequestBody AlgorithmPicResultVO algorithmPicResultVO);
}
