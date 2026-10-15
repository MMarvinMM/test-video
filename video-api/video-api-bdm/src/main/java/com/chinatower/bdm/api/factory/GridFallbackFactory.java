package com.chinatower.bdm.api.factory;

import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
public class GridFallbackFactory implements FallbackFactory<GridAPI> {
    @Override
    public GridAPI create(Throwable throwable) {
        return new GridAPI() {
            @Override
            public Result<List<GridByLngLat>> getMessageByLngLat(GridByLngLatParam param) {
                return Result.fail("网格图层调用失败"+throwable.getMessage());
            }

            @Override
            public Result<List<GridByLngLat>> getMessageByLngLatNew(GridByLngLatParam param) {
                return Result.fail("根据经纬度获取网格 for 微创告警调用"+throwable.getMessage());
            }

            @Override
            public AjaxResult getGridTree(CommonFeignEntity vo) {
                return AjaxResult.error("网格树形结构数据"+throwable.getMessage());
            }

            @Override
            public Result<GridVO> findGridInfo(GridInfoParam param) {
                return Result.fail("网格单条数据查询"+throwable.getMessage());
            }

            @Override
            public Result<GridKeeperVO> findGridKeeperInfo(GridKeeperInfoParam param) {
                return Result.fail("网格员单条数据查询"+throwable.getMessage());
            }

            @Override
            public Result<List<GridKeeperVO>> findGridKeeperAll(CommonFeignEntity param) {
                return Result.fail("根据租户查询网格员列表"+throwable.getMessage());
            }

            @Override
            public Result<TableDataInfo> searchGridKeeper(GridKeeperSearchParam param) {
                return Result.fail("网格员检索"+throwable.getMessage());
            }

            @Override
            public Result<List<GridKeeperVO>> getKeeperByPeriphery(KeeperPeripheryParam vo) {
                return Result.fail("周边网格员搜索"+throwable.getMessage());
            }

            @Override
            public AjaxResult getGridKeeperTree(@RequestBody KeeperTreeSelectParam vo) {
                return AjaxResult.error("网格员树"+throwable.getMessage());
            }

            @Override
            public Result<List<GridKeeperListVO>> getGridKeeperByLayer(GridKeeperLayerParam gridKeeperLayerParam) {
                return Result.fail("租户网格员图层"+throwable.getMessage());
            }

            @Override
            public Result<List<GridKeeperListVO>> getGridKeeperByLayerNew(GridKeeperLayerParam gridKeeperLayerParam) {
                return Result.fail("新租户网格员图层"+throwable.getMessage());
            }

            @Override
            public Result<List<GridListVO>> getGridListByLayer(GridLayerParam gridLayerParam) {
                return Result.fail("租户网格图层"+throwable.getMessage());
            }
            @Override
            public Result getKeeperTrail(QueryTrailsParam queryTrailsParam){
                return Result.fail("网格员轨迹"+throwable.getMessage());
            }
            @Override
            public Result findOneNoEncryptionSdk(GridKeeperInfoParam param){
                return Result.fail("网格无脱敏详情"+throwable.getMessage());
            }
            @Override
            public Result getGridListByTenantId(GridAllParam param){
                return Result.fail("网格全部数据获取"+throwable.getMessage());
            }

            @Override
            public Result<TableDataInfo> getTaskListExtend(InspectionTaskParamExtend inspectionTaskParam) {
                return Result.fail("巡查历史列表数据(扩展)"+throwable.getMessage());
            }

            @Override
            public Result<List<GridVO>> getGridNameByGridId(List<Long> list) {
                return Result.fail("调用根据网格id批量获取网格名称出错"+throwable.getMessage());
            }

            @Override
            public Result addFirstGrid(List<GridVO> gridVOList) {
                return Result.fail("调用新增最高级网格出错"+throwable.getMessage());
            }

            @Override
            public Result inspectionCountTrend(QueryCountTrend queryCountTrend) {
                return Result.fail("按日期统计用户巡查次数趋势"+throwable.getMessage());
            }

            @Override
            public Result getGridKeeper(String industryCode, String userId, String tenantId) {
                return Result.fail("根据用户ID查询其网格员ID"+throwable.getMessage());
            }

            @Override
            public Result getGridByKeeperNew(GridKeeperVO gridKeeperVO) {
                return Result.fail("根据网格员获取所属网格(新)"+throwable.getMessage());
            }


            @Override
            public Result<List<TreeSelect>> getGridTreeCurUser(GridVO vo) {
                return Result.fail("查询当前用户网格树形结构数据"+throwable.getMessage());
            }


        };

    }
}
