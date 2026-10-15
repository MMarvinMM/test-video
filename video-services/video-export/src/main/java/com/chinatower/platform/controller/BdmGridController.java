package com.chinatower.platform.controller;


import com.chinatower.bdm.api.entity.*;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.service.BdmGridService;
import com.chinatower.platform.utils.SecurityUtils;
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
 * @Author：zl
 * @Description：网格管理接口
 * @Date： 2022/8/9
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__网格管理
 */
@Api(value = "网格管理接口-Api",tags = "网格管理菜单后端接口")
@Slf4j
@RestController
@RequestMapping("/grid")
public class BdmGridController extends TowerBaseController {
    @Resource
    BdmGridService bdmGridService;


    /**
     * 网格导出
     * 功能描述：网格导出
     * @Author：zl
     * @param vo
     * @param response
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "网格导出")
    @PostMapping("/export")
    public void export(@RequestBody GridParam vo, HttpServletResponse response) {
        vo.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<GridExportVO> list = bdmGridService.getGridExportList(vo);
        byte[] data =new ExcelUtil<>(GridExportVO.class).exportBytes(list);
        FileUtils.download(response, data);
    }




}
