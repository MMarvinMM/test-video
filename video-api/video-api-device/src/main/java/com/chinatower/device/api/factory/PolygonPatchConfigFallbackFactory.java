package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.deprecated.IPolygonPatchConfigServiceMigrate;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author yy
 */
@Component
public class PolygonPatchConfigFallbackFactory implements FallbackFactory<IPolygonPatchConfigServiceMigrate> {
    @Override
    public IPolygonPatchConfigServiceMigrate create(Throwable throwable) {
        return new IPolygonPatchConfigServiceMigrate() {
            @Override
            public Result<Integer> isExist(PolygonPatchVo polygonPatchVo) {
                return Result.fail("查询图斑失败" + throwable.getMessage());
            }

            @Override
            public Result<Integer> isExistByLayer(PolygonPatchVo polygonPatchVo) {
                return Result.fail("查询图斑失败" + throwable.getMessage());
            }

            @Override
            public Result<PolygonPatchConfig> queryPolygonPatch(PolygonPatchVo polygonPatchVo) {
                return Result.fail("查询图斑绑定的电子围栏失败" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryPolygonPatchList(PolygonPatchVo polygonPatchVo) {
                TableDataInfo failResult = new TableDataInfo();
                failResult.setCode(ResultCode.FAILURE.getCode());
                return failResult;
            }
        };
    }
}
