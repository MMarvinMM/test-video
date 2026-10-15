package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.UavCameraModelQryReq;
import com.chinatower.device.entity.dto.UavMountModelQueryDTO;
import com.chinatower.device.entity.vo.UavModelVO;
import com.chinatower.device.entity.vo.UavNestModelVO;
import com.chinatower.event.api.factory.TowerExportUavFallbackFactory;
import com.chinatower.platform.entity.device.UavNestDeviceInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "towerExportUavService", value = ServiceNameConstants.TTVIEW_EXPORT_SERVICE, fallbackFactory = TowerExportUavFallbackFactory.class)
public interface ITowerExportUavService {

    /**
     * 无人机机巢型号列表导出
     *
     * @param uavNestModelVO 包含查询条件的机巢型号VO
     */
    @PostMapping("/device/exportUavNestModel")
    void exportUavNestModel(@RequestBody UavNestModelVO uavNestModelVO);

    /**
     * 铁塔视角-无人机机场导出
     * @param deviceVO
     */
    @PostMapping("/device/exportUavNestDeviceList")
    void exportUavNestDeviceList(@RequestBody UavNestDeviceInfoVO deviceVO);

    /**
     * 无人机相机型号列表导出
     * @param qryReq 请求参数（用来查询哪些数据需要导出）
     * @return 导出结果
     */
    @PostMapping("/device/exportCameraModelList")
    Result<String> exportCameraModelList(@RequestBody UavCameraModelQryReq qryReq);

    /**
     * 无人机型号列表导出
     *
     * @param uavModelVO 包含查询条件的无人机型号VO
     */
    @PostMapping("/device/exportUavModel")
    void exportUavModel(@RequestBody UavModelVO uavModelVO);

    /**
     * 无人机挂载型号导出
     * @param param
     */
    @PostMapping("/device/exportUavMountModelList")
    void exportUavMountModelList(@RequestBody UavMountModelQueryDTO param);
}
