package com.chinatower.video.api.factory;


import com.chinatower.common.entity.Result;
import com.chinatower.video.api.IVideoRecordPlanService;
import com.chinatower.video.api.entity.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 视频通道录像计划降级处理
 *
 * @author chenmf
 */
@Component
public class VideoRecordPlanFallbackFactory implements FallbackFactory<IVideoRecordPlanService>
{
    
    private static final Logger log = LoggerFactory.getLogger(VideoRecordPlanFallbackFactory.class);
    
    
    @Override
    public IVideoRecordPlanService create(Throwable throwable) {
    
        return new IVideoRecordPlanService() {
            @Override
            public Result addRecordPlan(ChannelVideoPlanRequestVO channelVideoPlanRequestVO) {
        
                return Result.fail("新增通道录像计划失败:" + throwable.getMessage());
            }
    
            @Override
            public Result updateRecordPlan(ChannelVideoPlanVO channelVideoPlanVO) {
                return Result.fail("修改通道录像计划失败:" + throwable.getMessage());
            }
    
            @Override
            public Result deleteRecordPlan(DeleteChannelVideoPlanVO deleteChannelVideoPlanVO) {
                return Result.fail("删除通道录像计划失败:" + throwable.getMessage());
            }
    
            @Override
            public Result batchDeleteRecordPlan(BatchDeleteChannelVideoPlanVO batchDeleteChannelVideoPlanVO) {
                return Result.fail("批量删除通道录像计划失败:" + throwable.getMessage());
            }
    
            @Override
            public Result batchStartOrEndRecordPlan(BatchStartOrEndRecordPlanVO batchStartOrEndRecordPlanVO) {
                return Result.fail("批量停用/启用通道录像计划失败:" + throwable.getMessage());
            }
    
            @Override
            public Result<List<ChannelVideoPlanVO>> queryOrderRecordPlanInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {
                return Result.fail("查询订单通道录像计划失败:" + throwable.getMessage());
            }

			@Override
			public Result<String> batchStartEndRecordPlan(ChannelVideoPlanRequestVO channelVideoPlanRequestVO) {
				return Result.fail("批量停用/启用通道录像计划失败:" + throwable.getMessage());
			}
        };
    }
}
