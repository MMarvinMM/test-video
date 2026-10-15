package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.IVideoPatrolPlanService;
import com.chinatower.video.api.entity.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName VideoPatrolPlanFallbackFactory
 * @Description 巡航路径服务降级处理
 * @Author chenshaowu
 * @Date 2022/2/11
 **/
@Component
public class VideoPatrolPlanFallbackFactory implements FallbackFactory<IVideoPatrolPlanService> {
    @Override
    public IVideoPatrolPlanService create(Throwable throwable) {
        return new IVideoPatrolPlanService() {
            @Override
            public Result addCruisePatrol(AddPatrolReq patrolDTO) {
                return Result.fail("增加巡航路径失败:" + throwable.getMessage());
            }

            @Override
            public Result delPatrol(DelPatrolReq patrolDTO) {
                return Result.fail("删除巡航路径失败:" + throwable.getMessage());
            }

            @Override
            public Result batchDelPatrol(BatchDelPatrolReq patrolDTO) {
                return Result.fail("批量删除巡航路径失败:" + throwable.getMessage());
            }

            @Override
            public Result updatePatrol(ModPatrolReq patrolDTO) {
                return Result.fail("修改巡航路径失败:" + throwable.getMessage());
            }

            @Override
            public Result doPatrol(SttPatrolReq patrolDTO) {
                return Result.fail("停用/启用巡航路径失败:" + throwable.getMessage());
            }
    
            @Override
            public Result<List<PatrolDTO>> queryOrderPatrolInfo(QueryOrderDeviceSetDTO presetDTO) {
                return Result.fail("查询订单巡航路径失败:" + throwable.getMessage());
            }

            @Override
            public Result queryPatrol(QueryPatrolReq queryPatrolReq) {
                return Result.fail("查询巡航路径列表失败:" + throwable.getMessage());
            }

			@Override
			public Result<String> batchStartEndPatrol(List<SttPatrolReq> patrolDTOList) {
				return Result.fail("批量停用/启用巡航计划失败:" + throwable.getMessage());
			}

            @Override
            public Result pauseResumePatrol(SttPatrolPauseReq patrolDTO){
                return Result.fail("暂停恢复巡航路径失败:" + throwable.getMessage());
            }
        };
    }
}
