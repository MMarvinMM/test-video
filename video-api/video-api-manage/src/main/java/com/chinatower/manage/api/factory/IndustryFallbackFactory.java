package com.chinatower.manage.api.factory;

import com.chinatower.common.core.dto.IndustryReqModel;
import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.IIndustryService;
import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 行业相关降级接口
 * @author chmod
 * @version 1.0
 * @date 2022/3/16 18:04
 */
@Component
public class IndustryFallbackFactory implements FallbackFactory<IIndustryService> {

    private static final Logger log = LoggerFactory.getLogger(IndustryFallbackFactory.class);

    @Override
    public IIndustryService create(Throwable throwable) {
        log.error("行业服务调用失败:{}", throwable.getMessage());
        return new IIndustryService() {
            @Override
            public Result<List<String>> getAuthUrlList(IndustryReqModel model) {
                return Result.fail("获取鉴权url失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SysIndustryConfigInfoModel>> queryIndustryConfigInfo(SysIndustryConfigInfoModel param) {
                return Result.fail("获取行业配置信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<SysIndustryConfigInfoModel> queryByIndustryCode(SysIndustryConfigInfoModel param) {
                return Result.fail("获取行业主题信息失败:" + throwable.getMessage());
            }
        };
    }
}
