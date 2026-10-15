package com.chinatower.device.controller;

import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.device.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.device.service.IReservePlanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import static com.chinatower.common.core.utils.StringUtils.getDataTable;


/**
 * video-device
 * @ClassName ReservePlanController
 * @Deacription 视频预案相关
 * @Author chenmf
 * @Date 2022/4/18 15:00
 * @Version 1.0
 **/
@RestController
@Api(value = "视频预案模块",tags = "视频预案模块")
public class ReservePlanController {
    
    @Autowired
    private IReservePlanService reservePlanService;
    
    
    /**
     * 增加预案信息
     * @Author chenmf
     * @param reservePlanDTO
     * @return
     */
    @ApiOperation(value = "增加预案信息")
    @PostMapping("/video/reservePlan/addReservePlan")
    public Result<String> addReservePlan(@Validated @RequestBody AddReservePlanDTO reservePlanDTO) {
        reservePlanService.addReservePlan(reservePlanDTO);
        return Result.success("增加预案信息成功!");
    }
    
    /**
     * 删除预案信息
     * @Author chenmf
     * @param reservePlanDTO
     * @return
     */
    @ApiOperation(value = "删除预案信息")
    @PostMapping("/video/reservePlan/delReservePlan")
    public Result<String> delReservePlan(@Validated @RequestBody DelReservePlanDTO reservePlanDTO) {
        reservePlanService.delReservePlan(reservePlanDTO.getPlanId());
        return Result.success("删除预案信息成功!");
    }
    
    
    /**
     * 修改预案信息
     * @Author chenmf
     * @param reservePlanDTO
     * @return
     */
    @ApiOperation(value = "修改预案信息")
    @PostMapping("/video/reservePlan/updateReservePlan")
    public Result<String> updateReservePlan(@Validated @RequestBody ModifyReservePlanDTO reservePlanDTO) {
        reservePlanService.updateReservePlan(reservePlanDTO);
        return Result.success("修改预案信息成功!");
    }
    
    /**
     * 查询预案列表信息
     * @Author chenmf
     * @param
     * @return
     */
    @ApiOperation(value = "查询预案列表信息")
    @PostMapping("/video/reservePlan/queryReservePlan")
    public Result<ReservePlanVO> queryReservePlan() {
        ReservePlanVO reservePlanVO = reservePlanService.queryReservePlan(new ReservePlanDTO());
        return Result.data(reservePlanVO);
    }

    /**
     * 分页查询预案列表信息
     * @Author chenmf
     * @param
     * @return
     */
    @ApiOperation(value = "分页查询预案列表信息")
    @PostMapping("/video/reservePlan/queryReservePlanList")
    public TableDataInfo queryReservePlanList(@RequestBody ReservePlanDTO reservePlanDTO) {
        List<ReservePlanDTO> reservePlanList = reservePlanService.queryReservePlanList(reservePlanDTO);
        return getDataTable(reservePlanList);
    }

    /**
     * 分页查询预案列表信息详情
     * @Author chenmf
     * @param
     * @return
     */
    @ApiOperation(value = "分页查询预案列表信息详情")
    @PostMapping("/video/reservePlan/queryReservePlanDetail")
    public TableDataInfo queryReservePlanDetail(@RequestBody ReservePlanDTO reservePlanDTO) {
        List<ReservePlanChannelDTO> reservePlanList = reservePlanService.queryReservePlanDetail(reservePlanDTO);
        return getDataTable(reservePlanList);
    }

}
