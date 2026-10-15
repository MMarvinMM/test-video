package com.chinatower.bdm.modules.licensePlateLibrary.controller;

import com.chinatower.bdm.api.entity.CheckpointVehicleTypeVO;
import com.chinatower.bdm.modules.licensePlateLibrary.service.CheckpointVehicleTypeService;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * video-bdm
 * <p>
 * 车辆类型管理 前端控制器
 * </p>
 *
 * @author Liqi
 * @since 2024-01-25
 */
@Api(tags = "车辆类型管理")
@RestController
@RequestMapping("/vehicleType")
public class CheckpointVehicleTypeController extends TowerBaseController {

    @Autowired
    private CheckpointVehicleTypeService checkpointVehicleTypeService;

    /**
     * 根据客户+行业查询车辆类型
     *
     * @return List<CheckpointVehicleTypeVO>
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @GetMapping("/getVehicleTypeList")
    public TableDataInfo getVehicleTypeList(CheckpointVehicleTypeVO checkpointVehicleTypeVO) {
        return checkpointVehicleTypeService.getVehicleTypeList(checkpointVehicleTypeVO);
    }

    /**
     * 车辆类型新增接口
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping("/insertOrUpdateVehicleType")
    public Result<String> insertOrUpdateVehicleType(@RequestBody CheckpointVehicleTypeVO checkpointVehicleTypeVO) {
        return checkpointVehicleTypeService.insertOrUpdateVehicleType(checkpointVehicleTypeVO);
    }

    /**
     * 车辆类型删除接口
     *
     * @return string
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping("/deleteVehicleType")
    public Result<String> deleteVehicleType(@RequestBody CheckpointVehicleTypeVO checkpointVehicleTypeVO) {
        return checkpointVehicleTypeService.deleteVehicleType(checkpointVehicleTypeVO);
    }


}
