package com.chinatower.event.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.api.model.*;
import com.chinatower.event.entity.dto.AlarmDownloadReq;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.entity.dto.TowerAlarmOrderDTO;
import com.chinatower.event.entity.dto.WarningOrderLayerDTO;
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
public class TowerEventApiFallbackFactory implements FallbackFactory<ITowerEventApiService> {

    @Override
    public ITowerEventApiService create(Throwable throwable) {
        return new ITowerEventApiService() {
            @Override
            public Result syncCheck(SyncCheckReq req) {
                return Result.fail("同步运营研判结果异常"+throwable.getMessage());
            }
        };
    }
}
