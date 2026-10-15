package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.request.Page;
import com.chinatower.video.api.IVideoPresetService;
import com.chinatower.video.api.entity.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName VideoPresetFallbackFactory
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/11
 **/
@Component
public class VideoPresetFallbackFactory implements FallbackFactory<IVideoPresetService> {
    @Override
    public IVideoPresetService create(Throwable throwable) {
        return new IVideoPresetService() {
            @Override
            public Result addCameraPreset(AddPresetReq addPresetReq) {
                return Result.fail("增加预置点失败:" + throwable.getMessage());
            }

            @Override
            public Result delCameraPreset(DelPresetReq presetDTO) {
                return Result.fail("删除预置点失败:" + throwable.getMessage());
            }

            @Override
            public Result batchDelCameraPreset(BatchDelPresetReq presetDTO) {
                return Result.fail("批量、停用、启用、删除、预置点失败:" + throwable.getMessage());
            }

            @Override
            public Result updatePresetInfo(ModPresetReq modPresetReq) {
                return Result.fail("修改预置点失败:" + throwable.getMessage());
            }
    
            @Override
            public Result<List<PresetDTO>> queryOrderPresetInfo(QueryOrderDeviceSetDTO presetDTO) {
                return Result.fail("查询预置点信息失败:" + throwable.getMessage());
            }

            @Override
            public Result getPresetImg(PresetDTO presetDTO) {
                return Result.fail("获得预置位图片失败:" + throwable.getMessage());
            }

            @Override
            public List<AlgorithmParamPresetVO>  selectPTZByIdList(List<AlgorithmConfigVO> pos) {
                return null;
            }

            @Override
            public VideoPresetDO selectById( VideoPresetDO videoPresetDO) {
                return null;
            }

			@Override
			public Result<Page<VmVideoPresetSnapshotMongoVO>> queryPresetListImgJob(
					PresetListImgJobDTO presetListImgJobDTO) {
				return Result.fail("分页查询预置位定时抓的图片失败:" + throwable.getMessage());
			}

            @Override
            public Result<Page<VmVideoPresetSnapshotMongo>> queryPresetImgJob(PresetImgJobDTO presetImgJobDTO){
                return Result.fail("分页查询预置位定时抓的图片失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> updateWarningOrderId(@RequestBody PresetImgVO presetImgVO){
                return Result.fail("单次识别预置位抓图，更新告警编码失败:" + throwable.getMessage());
            }
        };
    }
}
