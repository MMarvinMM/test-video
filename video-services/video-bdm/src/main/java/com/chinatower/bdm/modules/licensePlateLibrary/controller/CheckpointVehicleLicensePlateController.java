package com.chinatower.bdm.modules.licensePlateLibrary.controller;

import cn.hutool.core.collection.CollUtil;
import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.bdm.api.entity.GridKeeperExportVO;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.licensePlateLibrary.entity.CheckpointVehicleLicensePlateImportVO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chinatower.bdm.modules.licensePlateLibrary.service.CheckpointVehicleLicensePlateService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.chinatower.bdm.common.Constants.EXCEL_EXPORT_LIMIT;

/**
 * video-bdm
 */
@Api(tags = "车牌照黑白名单管理")
@RestController
@RequestMapping("/vehicleLicensePlate")
public class CheckpointVehicleLicensePlateController extends TowerBaseController {

    @Autowired
    private CheckpointVehicleLicensePlateService checkpointVehicleLicensePlateService;

    @Resource
    ISysImportService importService;

    /**
     * 黑白名单新增修改接口
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping("/insertOrUpdateVehicleLicensePlate")
    public Result<String> insertOrUpdateVehicleLicensePlate(@RequestBody CheckpointVehicleLicensePlate checkpointVehicleLicensePlate) {
        return checkpointVehicleLicensePlateService.insertOrUpdateVehicleLicensePlate(checkpointVehicleLicensePlate);
    }

    /**
     * 黑白名单删除接口
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping("/deleteVehicleLicensePlate")
    public Result<String> deleteVehicleLicensePlate(@RequestBody CheckpointVehicleLicensePlate checkpointVehicleLicensePlate) {
        return checkpointVehicleLicensePlateService.deleteVehicleLicensePlate(checkpointVehicleLicensePlate);
    }


    /**
     * 获取黑白名单信息详情
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @GetMapping("/getVehicleLicensePlateDetail")
    public Result<CheckpointVehicleLicensePlate> getVehicleLicensePlateDetail(String id) {
        return checkpointVehicleLicensePlateService.getVehicleLicensePlateDetail(id);
    }

    /**
     * 根据条件查询列表
     *
     * @param checkpointVehicleLicensePlate
     * @return
     */
    @PostMapping("/listVehicleLicensePlate")
    public TableDataInfo listVehicleLicensePlate(@RequestBody CheckpointVehicleLicensePlate checkpointVehicleLicensePlate) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointVehicleLicensePlate.setTenantId(user.getTenantId());
        checkpointVehicleLicensePlate.setIndustryCode(user.getIndustryCode());
        checkpointVehicleLicensePlate.setPlatformCode(user.getPlatformCode());
        if(checkpointVehicleLicensePlate.getPageSize() != null && checkpointVehicleLicensePlate.getPageNum() != null) {
            startPage();
        }
        try {
            List<CheckpointVehicleLicensePlate> list = checkpointVehicleLicensePlateService.listVehicleLicensePlateByCondition(checkpointVehicleLicensePlate);
            return getDataTable(list);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new TableDataInfo();
    }

    /**
     * 黑白名单导入
     *
     * @param file
     * @param blackOrWhite
     * @param importType
     * @param fileUrl
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "黑白名单导入")
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file,
                                     @RequestParam("blackOrWhite") Integer blackOrWhite,
                                     @RequestParam("importType") String importType,
                                     @RequestParam("fileUrl") String fileUrl) throws Exception {
        List<CheckpointVehicleLicensePlateImportVO> list = new ExcelUtil<>(CheckpointVehicleLicensePlateImportVO.class).importExcel(file.getInputStream());
        if (CollUtil.isEmpty(list)) {
            return Result.fail(Constants.IMPORT_DATA_NOT_NULL);
        }
        ImportParams<CheckpointVehicleLicensePlateImportVO> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put(Constants.LOGIN_USER,SecurityUtils.getLoginUser().getUser());
        params.put("blackOrWhite", blackOrWhite);
        return importService.autoThreadImport(params, p -> checkpointVehicleLicensePlateService.importData(p));
    }
}
