package com.chinatower.industry.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.industry.api.IndustryAlgorithmService;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class IndustryAlgorithmFallbackFactory implements FallbackFactory<IndustryAlgorithmService> {

    @Override
    public IndustryAlgorithmService create(Throwable throwable) {
        return new IndustryAlgorithmService() {
            @Override
            public TableDataInfo queryByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public Result<Map<String, List<IndustryAlgorithmDTO>>> queryByTenantIdAndDevices(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
                return Result.fail("调用查询行业摄像机对应的算法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndustryAlgorithmDTO>> queryConfigByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
                return Result.fail("调用查询行业摄像机绑定的后端算法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<AlgorithmInfoDO>> getAlgorithmInfoBatch(List<String> list) {
                return Result.fail("根据算法CODE列表批量获取算法详情信息失败:" + throwable.getMessage());
            }
        };
    }
}
