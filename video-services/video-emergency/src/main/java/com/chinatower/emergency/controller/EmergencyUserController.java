package com.chinatower.emergency.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.emergency.dto.*;
import com.chinatower.common.entity.Result;
import com.chinatower.emergency.service.EmergencyUserService;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * video-emergency
 * @author Liqi
 */
@RestController
@Slf4j
@RequestMapping("/emergency/emergencyUser")
public class EmergencyUserController extends BaseController {

    @Autowired
    private EmergencyUserService emergencyUserService;

    /**
     * 功能描述：选择指挥官/新增用户
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日11:03:28
     */
    @PostMapping("/addEmergencyUser")
    public Result addCommander(@RequestBody EmergencyUserDTO dto) {
        return emergencyUserService.addCommander(dto);
    }

    /**
     * 功能描述：更换指挥官
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日11:03:28
     */
    @PostMapping("/updateCommander")
    public Result<String> updateCommander(@RequestBody EmergencyUserDTO dto) {
        return Result.data(emergencyUserService.updateCommander(dto));
    }


    /**
     * 功能描述：查询指挥官/查询用户
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日11:03:28
     */
    @PostMapping("/getEmergencyUser")
    public Result getEmergencyUser(@RequestBody EmergencyUserDTO dto) {
        return Result.data(PageInfo.of(emergencyUserService.getEmergencyUser(dto)));
    }

    /**
     * 功能描述：删除用户
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日11:03:28
     */
    @PostMapping("/delEmergencyUser")
    public Result<String> delEmergencyUser(@RequestBody EmergencyUserDTO dto) {
        return Result.data(emergencyUserService.delEmergencyUser(dto));
    }


    /**
     * 功能描述：判断当前用户是否配置融合通信号码 app用
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月30日15:28:19
     */
    @PostMapping(value = "/checkUser")
    public Result<EmergencyUserVO> checkUser() {
        return Result.data(emergencyUserService.checkUser());
    }

    /**
     * 功能描述：用户打点
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月30日15:28:19
     */
    @PostMapping(value = "/saveLngLatByUser")
    public Result saveLngLatByUser(@RequestBody @Validated SaveLngLatParam dto) {
        return emergencyUserService.saveLngLatByUser(dto,1);
    }

    /**
     * 功能描述：用户打点（批量）
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月30日15:28:19
     */
    @PostMapping(value = "/saveLngLatBatchByUser")
    public Result saveLngLatBatchByUser(@RequestBody SaveLngLatParam dto) {
        return emergencyUserService.saveLngLatByUser(dto,2);
    }

    /**
     * 功能描述：轨迹获取
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月30日17:57:03
     */
    @PostMapping(value = "/getUserTrail")
    public Result getUserTrail(@RequestBody  QueryTrailsParam queryTrailsParam) {
        return emergencyUserService.getUserTrail(queryTrailsParam);
    }


    /**
     * 功能描述：查询用户打点
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月31日10:25:30
     */
    @PostMapping(value = "/getUserPosition")
    public Result<List<EmergencyUserLngLatDTO>> getUserPosition(@RequestBody SaveLngLatParam dto) {
        return emergencyUserService.getUserPosition(dto);
    }

    /**
     * 功能描述：查询用户打点
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月31日10:25:30
     */
    @PostMapping(value = "/getAllUserByEventCode")
    public TableDataInfo getAllUserByEventCode(@RequestBody EmergencyEventInfoVO emergencyEventInfo) {
        return getDataTable(emergencyUserService.getAllUserByEventCode(emergencyEventInfo));
    }
}
