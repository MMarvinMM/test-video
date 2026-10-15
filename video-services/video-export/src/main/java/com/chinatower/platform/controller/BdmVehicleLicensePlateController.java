package com.chinatower.platform.controller;

import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.BdmCheckpointVehicleLicensePlateService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import org.apache.poi.hssf.record.DVALRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.chinatower.platform.utils.JNConstants.EXCEL_EXPORT_LIMIT;

/**
 *
 * video-export
 * @program: video-platform
 * @ClassName: BdmVehicleLicensePlateController
 * @description: 车牌号导出控制器
 * @author: fengty
 * @create: 2024-01-31 15:38
 */
@Api(tags = "车牌照黑白名单导出")
@RestController
@RequestMapping("/vehicleLicensePlate")
public class BdmVehicleLicensePlateController {

    @Autowired
    private BdmCheckpointVehicleLicensePlateService checkpointVehicleLicensePlateService;

    /**
     *  导出Excel 黑白名单
     * @param checkpointVehicleLicensePlate
     * @param response
     * @return
     */
    @PostMapping("/export")
    public Result<String> exportVehicleLicensePlat(@RequestBody CheckpointVehicleLicensePlate checkpointVehicleLicensePlate, HttpServletResponse response) {
        try{
            SysUser user = SecurityUtils.getLoginUser().getUser();
            checkpointVehicleLicensePlate.setTenantId(user.getTenantId());
            checkpointVehicleLicensePlate.setIndustryCode(user.getIndustryCode());
            checkpointVehicleLicensePlate.setAcceptNode(user.getPlatformCode());
        } catch (NullPointerException nullPointerException) {
            return Result.fail("请您重新登录！");
        }

        // 文件名称
        String fileName = "黑名单车牌号导出文件";
        if(checkpointVehicleLicensePlate.getBlackOrWhite() == null) {
            Result.fail("黑白名单类型为必选条件!");
        }
        try {
            List<CheckpointVehicleLicensePlate> list = checkpointVehicleLicensePlateService
                    .listVehicleLicensePlateByCondition(checkpointVehicleLicensePlate);
            int total = list.size();
            if(total > EXCEL_EXPORT_LIMIT){
                return Result.fail("数据量过多，请按查询条件导出!");
            } else {
                if ("2".equals(checkpointVehicleLicensePlate.getBlackOrWhite())) {
                    fileName = "白名单车牌号导出文件";
                }
                byte[] data =new ExcelUtil<>(CheckpointVehicleLicensePlate.class).exportBytes(list);
                FileUtils.download(response, data, fileName);
                return Result.success("导出卡口设备数据成功!");
            }
        } catch (Exception e) {
            if("黑白名单类型为必选条件".equals(e.getMessage())) {
                return Result.fail(e.getMessage());
            }
            return Result.fail("导出时查询出错，请稍后重试！");
        }
    }
}
