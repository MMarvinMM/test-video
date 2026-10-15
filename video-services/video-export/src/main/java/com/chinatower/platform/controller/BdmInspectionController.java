package com.chinatower.platform.controller;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.service.BdmInspectionService;
import com.chinatower.platform.utils.JNConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.chinatower.platform.entity.InspectionConfVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * video-export
 * @Author：zl
 * @Description：巡查任务相关接口
 * @Date： 2022/8/9
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
 */
@Api(value = "巡查任务相关接口-Api",tags = "巡查任务菜单后端接口")
@Slf4j
@RestController
public class BdmInspectionController extends TowerBaseController {
    @Resource
    BdmInspectionService bdmInspectionService;



    /**
     *
     * 导出巡查历史
     * 功能描述：response
     * @Author：zl
     * @param inspectionTaskParam
     * @param response
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "导出巡查历史")
    @PostMapping("/inspectionTask/export")
    public void export(@RequestBody InspectionTaskParam inspectionTaskParam, HttpServletResponse response) {
        List<InspectionTaskListVO> list = bdmInspectionService.getTaskList(inspectionTaskParam);
        if (list.size() > JNConstants.FIVE_THOUSAND){
            throw new CustomException(JNConstants.CHECK_EXPORT_NUMS);
        }
        byte[] data =new ExcelUtil<>(InspectionTaskListVO.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     *
     * 导出巡查计划
     * 功能描述：response
     * @Author：zhaozj
     * @param inspectionConfParam
     * @param response
     * @version release_v1.5.85
     * @requestID  中国铁塔_铁塔视联平台_巡查计划相关接口
     */
    @ApiOperation(value = "导出巡查计划")
    @PostMapping("/inspectionConf/export")
    public void export(@RequestBody InspectionConfParam inspectionConfParam, HttpServletResponse response) {
        List<InspectionConfVO> list = bdmInspectionService.getConfList(inspectionConfParam);
        if (list.size() > JNConstants.FIVE_THOUSAND){
            throw new CustomException(JNConstants.CHECK_EXPORT_NUMS);
        }
        byte[] data =new ExcelUtil<>(InspectionConfVO.class).exportBytes(list);
        FileUtils.download(response, data);
    }


}
