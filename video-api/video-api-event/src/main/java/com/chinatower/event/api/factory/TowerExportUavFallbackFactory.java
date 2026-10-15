package com.chinatower.event.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.UavCameraModelQryReq;
import com.chinatower.device.entity.dto.UavMountModelQueryDTO;
import com.chinatower.device.entity.vo.UavModelVO;
import com.chinatower.device.entity.vo.UavNestModelVO;
import com.chinatower.event.api.ITowerExportUavService;
import com.chinatower.platform.entity.device.UavNestDeviceInfoVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TowerExportUavFallbackFactory.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2025/07/14 16:47
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Component
public class TowerExportUavFallbackFactory implements FallbackFactory<ITowerExportUavService> {

    @Override
    public ITowerExportUavService create(Throwable throwable) {
        return new ITowerExportUavService() {

            @Override
            public void exportUavNestModel(UavNestModelVO uavNestModelVO) {
                log.info("调用ttview-export无人机机巢型号列表导出失败！");
            }

            @Override
            public void exportUavNestDeviceList(UavNestDeviceInfoVO deviceVO) {
                log.info("调用ttview-export无人机机场导出失败！");
            }

            @Override
            public Result<String> exportCameraModelList(UavCameraModelQryReq qryReq) {
                return Result.fail("调用ttview-export无人机相机型号列表导出失败:" + throwable.getMessage());
            }

            @Override
            public void exportUavModel(UavModelVO uavModelVO) {
                log.info("调用ttview-export无人机型号列表导出失败！");
            }

            @Override
            public void exportUavMountModelList(UavMountModelQueryDTO param) {
                log.info("调用ttview-export无人机挂载型号导出失败！");
            }
        };
    }
}
