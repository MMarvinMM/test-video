package com.chinatower.system.api.feign.factory;

import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.feign.client.NoticeRuleClient;
import com.chinatower.system.api.model.NoticeRuleFilterResultVO;
import com.chinatower.system.api.model.NoticeRuleFilterVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @auther hued
 * @date 2022/2/15
 */
@Slf4j
@Component
public class NoticeRuleClientFallbackFactory implements FallbackFactory<NoticeRuleClient> {
    @Override
    public NoticeRuleClient create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new NoticeRuleClient()
        {
            /**
             * 筛选用户通知规则 for微创
             */
            @Override
            public Result<List<NoticeRuleFilterResultVO>> filterUserList(List<NoticeRuleFilterVO> noticeRuleFilterVOList){
                log.error("调用筛选用户通知规则:{}", throwable.getMessage());
                return Result.fail(ApiResult.RETURN_CODE_ERR_MSG + ApiResult.ERROR_CODE_1004 + "内部服务调用异常");
            }
        };
    }

}
