package com.chinatower.gis.api.factory;

import com.chinatower.common.core.dto.bdm.UpdateAreaDTO;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.api.entity.gis.EditLayerVO;
import com.chinatower.gis.api.entity.layer.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@Component
public class GisGuotuFallbackFactory implements FallbackFactory<GisLayerAPI> {
    private static final Logger log = LoggerFactory.getLogger(GisGuotuFallbackFactory.class);

    @Override
    public GisLayerAPI create(Throwable throwable) {
        log.error("调用地图查询接口失败:{}", throwable.getMessage());
        return new GisLayerAPI() {
            @Override
            public Result taoheAnalysisLayerTable(TaoheAnalysisLayerTableReq taoheAnalysisLayerTableReq) {
                return Result.fail("查询套合明细的结果列表方法失败:" + throwable.getMessage());
            }

            @Override
            public Result gisCalculateArea(GisCalculateAeaReq gisCalculateAeaReq) {
                return Result.fail("计算输入几何多边形的面积失败:" + throwable.getMessage());
            }

            @Override
            public Result taoheAnalysisLayerStatistics(TaoheAnalysisLayerStatisticsReq taoheAnalysisLayerStatisticsReq) {
                return Result.fail("查询套合明细的饼图方法失败:" + throwable.getMessage());
            }

            @Override
            public Result taoheAnalysisAll(TaoheAnalysisAllReq taoheAnalysisAllReq) {
                return Result.fail("查询套合结果方法失败:" + throwable.getMessage());
            }

            @Override
            public Result intersectAnalysis(IntersectAnalysisReq intersectAnalysisReq) throws Exception {
                return Result.fail("相交分析失败:" + throwable.getMessage());
            }

            @Override
            public Result taoheAnalysisDefaultLayers(TaoheAnalysisAllReq taoheAnalysisAllReq) throws Exception {
                return Result.fail("查询默认套盒图层方法失败:" + throwable.getMessage());
            }

            @Override
            public Result taoheAnalysisSetOptions(TaoheAnalysisSetOptionsReq taoheAnalysisSetOptionsReq) throws Exception {
                return Result.fail("设置参数方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> deleteLayer(DeleteLayerReq req) {
                return Result.fail("删除图层失败:" + throwable.getMessage());
            }

            @Override
            public Result createLayerInfo(LayerPGDTO pgdto) {
                return Result.fail("创建模板表方法失败:" + throwable.getMessage());
            }

            @Override
            public Result createModelLayerTable(LayerPGDTO pgdto) {
                return Result.fail("创建模板表方法失败:" + throwable.getMessage());
            }

            @Override
            public Result insertLayerInfo(LayerPGDTO pgdto) {
                return Result.fail("插入图层信息表方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<UpdateAreaDTO> layerArea(LayerPGDTO pgdto) {
                return Result.fail("计算图斑面积方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<LayerPGRes> addLayer(LayerPGDTO pgdto) {
                return Result.fail("新增PG图层失败:" + throwable.getMessage());
            }

            @Override
            public Result statisticGdxzmj(StatisticGdxzmjReq statisticGdxzmjReq) {
                return Result.fail("根据id，行业编码进行耕地现状统计的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<GeojsonStruckRes> getXlsjFeatures(GetXlsjFeaturesReq getXlsjFeaturesReq) {
                return Result.fail("获取下拉数据要素列表的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result statisticDkmj(StatisticDkmjReq statisticDkmjReq) {
                return Result.fail("根据ids进行图斑面积统计的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<GeojsonStruckRes> getXjxlFeaturesByIds(GetXjxlFeaturesByIdsReq getXjxlFeaturesByIdsReq) {
                return Result.fail("根据ids查询巡检路线 Features 的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getFeaturesByFilter(GetFeaturesByFilterReq getFeaturesByFilterReq) {
                return Result.fail("根据过滤条件查询 Features 的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getFeaturesByWkt(GetFeaturesByWktReq getFeaturesByWktReq) {
                return Result.fail("根据空间范围过滤条件查询 Features 的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result searchSTBHHX(SearchSTBHHXReq searchSTBHHXReq) {
                return Result.fail("查询生态保护红线方法失败:" + throwable.getMessage());
            }

            @Override
            public Result searchJBNT(SearchJBNTReq searchJBNTReq) {
                return Result.fail("查询永久基本农田的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result searchXZTB(SearchXZTBReq searchXZTBReq) {
                return Result.fail("查询土地利用现状的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result searcJSYDGZQ(SearcJSYDGZQReq searcJSYDGZQReq) {
                return Result.fail("查询土地利用总体规划的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result searcTDBP(SearchTDBPTReq searchTDBPTReq) {
                return Result.fail("查询土地报批查询的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result searchOther(SearcOtherReq searcOtherReq) {
                return Result.fail("地块查询其他类型的方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<GeometrysRes>> getGeometrys(GeometrysReq req) {
                return Result.fail("查询坐标数据失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo forestLayerXmList(ForestLayerXmReq req) {
                TableDataInfo info = new TableDataInfo();
                info.setMsg("小班列表查询失败:" + throwable.getMessage());
                info.setCode(500);
                return info;
            }

            @Override
            public Result<List<ForestLayerLegendRes>> forestLayerLegendRes(ForestLayerLegendReq req) {
                return Result.fail("图例统计失败:" + throwable.getMessage());
            }

            @Override
            public Result<Map<String, Object>> forestLayerXmInfo(ForestLayerXmInfoReq req) {
                return Result.fail("小班详情查询失败:" + throwable.getMessage());
            }

            @Override
            public Result layerParcelQuery(LayerParcelQueryDTO filter) {
                return Result.fail("地块查询:" + throwable.getMessage());
            }

            @Override
            public Result<LayerInfoAddDTO> calculateLayer(MineralsLayerReq filter) {
                return Result.fail("计算探而未采图层错误:" + throwable.getMessage());
            }

            @Override
            public Result updateExpire() {
                return Result.fail("修改探矿权、采矿权、探而未采过期状态错误" + throwable.getMessage());
            }

            @Override
            public Result<Map<String, String>>  searchTdbpState(SearchTdbpStateReq searchTdbpStateReq) {
                return Result.fail("查询土地报批状态错误:" + throwable.getMessage());
            }

            @Override
            public Result<CalInOutBalanceRes> calInOutBalance(CalInOutBalanceReq calInOutBalanceReq) {
                return Result.fail("启动耕地的进出平衡计算错误:" + throwable.getMessage());
            }

            @Override
            public Result<GetInOutBalanceRes> getInOutBalance(GetInOutBalanceReq getInOutBalanceReq) {
                return Result.fail("获取耕地的进出平衡的计算结果错误:" + throwable.getMessage());
            }

            @Override
            public Result editLayer(EditLayerVO vo) {
                return Result.fail("备份图层表:" + throwable.getMessage());
            }

            @Override
            public Result editBackLayer(EditLayerVO vo) {
                return Result.fail("还原图层表:" + throwable.getMessage());
            }

            @Override
            public Result autoCompareLayerFeatures(CalInOutBalanceReq calcAutoCompareLayerFeaturesReq) {
                return Result.fail("自动比较图层图斑不同的计算错误:" + throwable.getMessage());
            }

            @Override
            public Result<LayerHasCustomBsmFieldRes> isLayerHasCustomBsmField(String layerId) {
                return Result.fail("表里是否有bsm字段的处理错误:" + throwable.getMessage());
            }

            @Override
            public Result getIntersectingPlot(GetIntersectingPlotReq getIntersectingPlotReq) {
                return Result.fail(Constants.CALCULATE_INTERSECTINGPLOT_ERROR + ":" + throwable.getMessage());
            }

            @Override
            public Result getIntersectingLayers(GetIntersectingPlotReq getIntersectingPlotReq) {
                return Result.fail(Constants.CALCULATE_INTERSECTINGLAYERS_ERROR + ":" + throwable.getMessage());
            }

        };
    }
}
