package com.chinatower.gis.api.factory;

import com.chinatower.common.core.dto.bdm.LayerClassifyAreaStatisticsVO;
import com.chinatower.common.core.dto.bdm.LayerSlzyStatisticsDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.CameraCoordRes;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.AltitudesReq;
import com.chinatower.gis.api.entity.AltitudesRes;
import com.chinatower.gis.api.entity.CamaraDeviceParamsInfoVO;
import com.chinatower.gis.api.entity.TableNameListReq;
import com.chinatower.gis.api.entity.gis.GisFireCombustibleRes;
import com.chinatower.gis.api.entity.gis.GisFireGradeRes;
import com.chinatower.gis.api.entity.gis.GisSlopeAspectRes;
import com.chinatower.gis.api.entity.gis.GisVegetationDataRes;
import com.chinatower.gis.api.entity.layer.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @desription: gis pg库的SDK
 * @data 创建时间：2024/1/9 17:49
 * @author chuan hao
 * @version 1.0
 *
 */
@Component
public class GisPgFallbackFactory implements FallbackFactory<GisPgAPI> {

    @Override
    public GisPgAPI create(Throwable throwable) {
        return new GisPgAPI() {
            @Override
            public Result<Integer> altitudeHeightQuery(String lon, String lat) {
                return Result.fail("海拔高度查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<AltitudesRes>> altitudeHeightQuerys(AltitudesReq req) {
                return Result.fail("海拔高度查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<Map<String, Object>>> tableNameList(TableNameListReq req) {
                return Result.fail("查询图层列表数据错误:" + throwable.getMessage());
            }

            /**
             * @param req 参数
             * @return Result
             * @apiNote 提供视频视图联动功能实时视频画面中增加测高度
             * @apiVersion 6.4.3
             * @requestDemo '{
             * "deviceCode": "11010800001327000075",
             * "channelCode": "",
             * "percentX": "1",
             * "percentY": "1"
             * }'
             * @responseDemo '
             * <p>
             * '
             * @tag gis 能力
             */
            @Override
            public Result<CameraCoordRes> getCameraCoord(CamaraDeviceParamsInfoVO req) {
                return Result.fail("提供视频视图联动功能实时视频画面中增加测高度错误:" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(LayerClassifyAreaStatisticsVO vo) {
                return Result.fail("查询所有表按code分类的面积统计错误:" + throwable.getMessage());

            }

            @Override
            public Result<List<LayerXztbDlbmAreaRes>> layerXztbDlbmAreaRes(LayerYearTableName vo) {
                return Result.fail("耕地类型面积统计:" + throwable.getMessage());
            }

            @Override
            public Result<LayerXztbGddbAreaRes> layerXztbGddbAreaRes(LayerYearTableName vo) {
                return Result.fail("耕地等别面积比例统计:" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerAnnualChangesRes>> layerAnnualChangesRes(LayerAnnualChangesReq vo) {

                return Result.fail("年度变化:" + throwable.getMessage());
            }

            @Override
            public Result<GisSlopeAspectRes> GisSlopeAspectRes(String lon, String lat) {
                return Result.fail("坡度坡向查询错误:" + throwable.getMessage());
            }

            @Override
            public Result<GisFireCombustibleRes> gisFireCombustibleRes(String lon, String lat) {
                return Result.fail("火势蔓延查询错误:" + throwable.getMessage());
            }

            @Override
            public Result<GisFireGradeRes> gisFireGradeRes(String lon, String lat) {
                return Result.fail("火灾等级错误:" + throwable.getMessage());
            }

            @Override
            public Result<List<GisVegetationDataRes>> gisVegetationDataRes(String lon, String lat, Integer distance) {
                return Result.fail("查询植被覆盖错误:" + throwable.getMessage());
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
    }
}
