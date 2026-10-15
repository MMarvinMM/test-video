package com.chinatower.bdm.modules.bmattendance.controller;

import cn.hutool.core.collection.ListUtil;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.bmattendance.service.BmAttendanceService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * video-bdm
 * 功能描述: 网格员考勤
 * @author zl
 * @date 2022/8/9
 * @version release_v1.5
 *
 */
@Api(value = "网格员考勤-Api",tags = "网格员考勤后端接口")
@Slf4j
@RestController
@RequestMapping("/bmattendanceset")
public class BmAttendanceController extends TowerBaseController {
    @Resource
    private BmAttendanceService bmAttendanceService;

    /**
     * 查询历史打卡记录 （pc 历史打卡记录）
     * @author zl
     * @param bmAttendanceHistoryParam
     * @return  TableDataInfo
     */
    @ApiOperation(value = "历史打卡记录",notes = "历史打卡记录")
    @PostMapping("/getListDetail")
    public TableDataInfo getListDetail(@RequestBody BmAttendanceHistoryParam bmAttendanceHistoryParam){
        Integer pageNum = bmAttendanceHistoryParam.getPageNum();
        Integer pageSize = bmAttendanceHistoryParam.getPageSize();
        //不使用pageHelper 分页 删除实体类中的pageNum 和pageSize
        bmAttendanceHistoryParam.setPageNum(null);
        bmAttendanceHistoryParam.setPageSize(null);
        List<BmAttendanceHistoryListVO> historyList = bmAttendanceService.getHistoryList(bmAttendanceHistoryParam);
        int pageNumNew = pageHandle(historyList.size(),pageSize,pageNum);
        //操作逻辑中需要删除元素，需要进行手动分页
        List<BmAttendanceHistoryListVO> result = ListUtil.page(pageNumNew - 1, pageSize, historyList);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(result);
        rspData.setTotal(historyList.size());
        return rspData;
    }

    private int pageHandle(int total,int pageSize,int pageNum){
        int pageNumNew;
        int pages;
        if (pageSize > Constants.NUMBER_ZERO) {
            pages = total / pageSize + ((total % pageSize == Constants.NUMBER_ZERO) ? Constants.NUMBER_ZERO :  Constants.NUMBER_ONE);
        }else{
            pages = Constants.NUMBER_ZERO;
        }
        if (pageNum > pages ){
            pageNum = pages;
        }
        pageNumNew = pageNum;
        return pageNumNew;
    }

    /**
     * 保存考勤设置 （pc）
     *  @author zl
     * @param bmAttendanceSetVO
     * @return Result
     */
    @ApiOperation(value = "保存考勤设置",notes = "保存考勤设置")
    @PostMapping("/doSaveInfo")
    public Result doSaveInfo(@RequestBody BmAttendanceSetVO bmAttendanceSetVO){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        bmAttendanceSetVO.setTenantId(currentUser.getUser().getTenantId());
        bmAttendanceSetVO.setCreator(currentUser.getUser().getUserId());
        bmAttendanceSetVO.setAppVerCode(currentUser.getUser().getAppVerCode());
        bmAttendanceSetVO.setIndustryCode(currentUser.getUser().getIndustryCode());
        return bmAttendanceService.doSaveSetData(bmAttendanceSetVO);
    }

    /**
     * 获取考勤设置 (pc 查询考勤设置)
     *  @author zl
     * @param tenantId 租户id
     * @return Result<BmAttendanceSetVO>
     */
    @ApiOperation(value = "获取考勤设置",notes = "获取考勤设置")
    @GetMapping("/findOneMsg")
    public Result<BmAttendanceSetVO> findOneMsg(String tenantId){
        BmAttendanceSetVO bmAttendanceSetVO = new BmAttendanceSetVO();
        LoginUser currentUser = SecurityUtils.getLoginUser();
        bmAttendanceSetVO.setTenantId(currentUser.getUser().getTenantId());
        bmAttendanceSetVO.setAppVerCode(currentUser.getUser().getAppVerCode());
        bmAttendanceSetVO.setIndustryCode(currentUser.getUser().getIndustryCode());
        bmAttendanceSetVO.setTenantId(tenantId);
        return bmAttendanceService.findOneSetData(bmAttendanceSetVO);
    }

    /**
     * 网格考勤列表查询 (pc查询列表)
     *  @author zl
     * @param bmAttendanceParam
     * @return TableDataInfo
     */
    @ApiOperation(value = "网格考勤列表查询",notes = "网格考勤列表查询")
    @PostMapping("/getList")
    public TableDataInfo getList(@RequestBody BmAttendanceParam bmAttendanceParam){
        Integer pageNum = bmAttendanceParam.getPageNum();
        Integer pageSize = bmAttendanceParam.getPageSize();
        //不使用pageHelper 分页 删除实体类中的pageNum 和pageSize
        bmAttendanceParam.setPageNum(null);
        bmAttendanceParam.setPageSize(null);
        List<BmAttendanceListVO> list = bmAttendanceService.getList(bmAttendanceParam);
        int pageNumNew = pageHandle(list.size(),pageSize,pageNum);
        //操作逻辑中需要删除元素，需要进行手动分页
        List<BmAttendanceListVO> result = ListUtil.page(pageNumNew - 1, pageSize, list);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(result);
        rspData.setTotal(list.size());
        return rspData;
    }



    /**
     * 获取考勤设置 (app)
     * @param vo
     * @return Result
     */
    @ApiOperation(value = "获取考勤设置",notes = "获取考勤设置")
    @PostMapping("/findOneAttendanceByKeeper")
    public Result findOneAttendanceByKeeper(@RequestBody BmAttendanceLineVO vo){
        return bmAttendanceService.findOneAttendanceByKeeper(vo);
    }

    /**
     * 网格员签到
     * @param vo
     * @return Result
     */
    @ApiOperation(value = "网格员签到")
    @PostMapping("/signIn")
    public Result doSignIn(@RequestBody @Validated AttendanceSignParam vo){
        return bmAttendanceService.doSignIn(vo);
    }

    /**
     * 网格员签退
     * @param vo
     * @return Result
     */
    @ApiOperation(value = "网格员签退")
    @PostMapping("/signOut")
    public Result doSignOut(@RequestBody @Validated AttendanceSignParam vo){
        return bmAttendanceService.doSignOut(vo);
    }

    /**
     * 根据网格员获取考勤配置数据（app 考勤查询）
     * @return Result<BmAttendanceSetVO>
     */
    @ApiOperation(value = "根据网格员获取考勤配置数据")
    @PostMapping("/findOneSetDataByKeeper")
    public Result<BmAttendanceSetVO> findOneSetDataByKeeper(@RequestBody BmAttendanceSetVO bmAttendanceSetVO){
        return bmAttendanceService.findOneSetDataByKeeper(bmAttendanceSetVO);
    }

    /**
     * 月考勤数据（app 打卡记录）
     * @param param
     * @return
     */
    @ApiOperation(value = "月考勤数据")
    @PostMapping("/getAttendanceStatistics")
    public Result<AttendanceStatisticsVO> getAttendanceStatistics(@RequestBody AttendanceStatisticsParam param){
        return bmAttendanceService.getAttendanceStatistics(param);
    }



    /**
     * 获取考勤用户信息（app 查询考勤头部用户信息）
     * @return Result<BmAttendanceUserVO>
     */
    @ApiOperation(value = "获取考勤用户信息")
    @PostMapping("/getAttendanceUserInfo")
    public Result<BmAttendanceUserVO> getAttendanceUserInfo(){
        BmAttendanceUserVO bmAttendanceUserVO = new BmAttendanceUserVO();
        return bmAttendanceService.getAttendanceUserInfo(bmAttendanceUserVO);
    }



    /**
     * 当月考勤数据（app 考勤页面统计）
     * @param param
     * @return
     */
    @ApiOperation(value = "当月考勤数据")
    @PostMapping("/getCurrentMonthStatistics")
    public Result<AttendanceCurrentMonthStatisticsVO> getCurrentMonthStatistics(@RequestBody AttendanceStatisticsParam param){
        return bmAttendanceService.getCurrentMonthStatistics(param);
    }

    /**
     * 插入考勤数据
     * @param param
     * @return
     */
    @ApiOperation(value = "insertAttendanceLine")
    @PostMapping("/insertAttendanceLine")
    public Result insertAttendanceLine(@RequestBody AttendanceStatisticsParam param){
        bmAttendanceService.insertAttendanceLine(Long.valueOf(param.getKeeperId()));
        return Result.data(null);
    }
}
