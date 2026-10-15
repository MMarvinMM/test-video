package com.chinatower.event.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.common.core.vo.TowerCommunicationVo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.api.model.ApiInterceptOrderEntity;
import com.chinatower.event.api.model.ApiOperateOrderEntity;
import com.chinatower.event.api.model.ApiWarningOrderEntity;
import com.chinatower.event.api.model.SatelliteAlarmApiReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.AiJudgeAlarmVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderFallbackFactory.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/21 10:23
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Component
public class TowerEventWarningFallbackFactory implements FallbackFactory<ITowerEventWarningService> {

    @Override
    public ITowerEventWarningService create(Throwable throwable) {
        return new ITowerEventWarningService() {

            @Override
            public TableDataInfo getOrderListImgs(AlarmDownloadReq dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 查询告警事件列表(提供给君南)
             * @param dto
             * @return
             */
            @Override
            public TableDataInfo getOrderList(TowerAlarmOrderDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 查询运营研判事件列表(提供给君南)
             * @param dto
             * @return
             */
            @Override
            public TableDataInfo getOrderListForOperate(AlarmOperateDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 查询运营研判事件列表
             * @param dto
             * @return
             */
            @Override
            public TableDataInfo getOrderListForOperateApi(AlarmOperateDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getOrderCountForOperate(AlarmOperateDTO dto) {
                return Result.fail(throwable.getMessage());
            }

            @Override
            public Result aiJudgeAlarmAndSave(ReceiveAlarmVO alarmVO) {
                return Result.fail(throwable.getMessage());
            }

            @Override
            public Result<List<AiJudgeAlarmVO>> selectAiJudgeResultList(AiJudgeResultDTO dto) {
                return Result.fail("查询AI研判列表失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getInterceptListImgs(AlarmDownloadReq dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result qryWarningOrderCollectionImgs(AlarmDownloadReq dto) {
                return Result.fail("我的收藏:" + throwable.getMessage());
            }

            @Override
            public Result getSatelliteOrderList(SatelliteAlarmApiReq dto) {
                return Result.fail("卫星二级菜单查询:" + throwable.getMessage());
            }

            /**
             * 查询告警图层
             *
             * @param dto
             * @return
             */
            @Override
            public Result getWarningOrderLayer(WarningOrderLayerDTO dto) {
                return Result.fail("查询告警图层失败:" + throwable.getMessage());
            }

            @Override
            public Result getOperateListApi(ApiOperateOrderEntity dto) {
                return Result.fail("属地化云隐呀挠你查询失败:" + throwable.getMessage());
            }

            @Override
            public Result getInterceptListApi(ApiInterceptOrderEntity dto) {
                return Result.fail("属地化回收站查询失败:" + throwable.getMessage());
            }

            @Override
            public Result getOrderListApi(ApiWarningOrderEntity dto) {
                return Result.fail("属地化事件查询失败:" + throwable.getMessage());
            }

            @Override
            public Result getOrderInfoApi(ExportOrderInfoReq dto) {
                return Result.fail("获取运营研判详情失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryList(TowerCommunicationVo communicationVo) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }
        };
    }
}
