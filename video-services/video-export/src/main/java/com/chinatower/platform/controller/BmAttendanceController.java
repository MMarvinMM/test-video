package com.chinatower.platform.controller;

import cn.hutool.core.collection.ListUtil;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.service.BmAttendanceService;
import com.chinatower.platform.utils.JNConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 *
 * video-export
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
     *
     * 导出考勤列表
     * 功能说明:导出考勤列表
     * @author zl
     * @param bmAttendanceParam
     * @param response
     */
    @ApiOperation(value = "导出考勤列表")
    @PostMapping("/export")
    public void export(@RequestBody BmAttendanceParam bmAttendanceParam, HttpServletResponse response) {
        List<BmAttendanceListVO> list = bmAttendanceService.getList(bmAttendanceParam);
        if(list.size() > JNConstants.EXCEL_EXPORT_LIMIT){
            throw new CustomException(JNConstants.EXCEL_MORE);
        } else {
            byte[] data =new ExcelUtil<>(BmAttendanceListVO.class).exportBytes(list);
            FileUtils.download(response, data);
        }
    }

    /**
     *
     * 导出历史打卡记录
     * 功能说明:导出历史打卡记录
     * @author zl
     * @param bmAttendanceHistoryParam
     * @param response
     */
    @ApiOperation(value = "导出历史打卡记录")
    @PostMapping("/history/export")
    public void historyExport(@RequestBody BmAttendanceHistoryParam bmAttendanceHistoryParam, HttpServletResponse response) {
        Integer pageNum = bmAttendanceHistoryParam.getPageNum();
        Integer pageSize = bmAttendanceHistoryParam.getPageSize();
        //不使用pageHelper 分页 删除实体类中的pageNum 和pageSize
        bmAttendanceHistoryParam.setPageNum(null);
        bmAttendanceHistoryParam.setPageSize(null);
        List<BmAttendanceHistoryListVO> list = bmAttendanceService.getHistoryList(bmAttendanceHistoryParam);
        //操作逻辑中需要删除元素，需要进行手动分页
        List<BmAttendanceHistoryListVO> result = ListUtil.page(pageNum - 1, pageSize, list);
        if(result.size() > JNConstants.EXCEL_EXPORT_LIMIT){
            throw new CustomException(JNConstants.EXCEL_MORE);
        } else {
            byte[] data =new ExcelUtil<>(BmAttendanceHistoryListVO.class).exportBytes(result);
            FileUtils.download(response, data);
        }
    }


}
