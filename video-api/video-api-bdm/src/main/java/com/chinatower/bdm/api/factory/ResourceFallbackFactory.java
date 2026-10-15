package com.chinatower.bdm.api.factory;


import com.chinatower.bdm.api.ResourceAPI;
import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
public class ResourceFallbackFactory implements FallbackFactory<ResourceAPI> {

    @Override
    public ResourceAPI create(Throwable throwable) {
        return new ResourceAPI() {
            @Override
            public Result<List<ResourceConf>> selectResource(ResourceConf resourceConf) {
                return Result.fail("资源获取调用失败"+throwable.getMessage());
            }

            @Override
            public TableDataInfo selectResourceConfList(ResourceConf resourceConf) {
                return null;
            }

            @Override
            public Result<ResourceConf> getResourceInfoDetail(ResourceConf resourceConf) {
                return Result.fail("资源详情获取调用失败"+throwable.getMessage());
            }

            @Override
            public AjaxResult getResourceTree(CommonFeignEntity vo) {
                return AjaxResult.error("资源树获取调用失败"+throwable.getMessage());
            }
        };
    }
}
