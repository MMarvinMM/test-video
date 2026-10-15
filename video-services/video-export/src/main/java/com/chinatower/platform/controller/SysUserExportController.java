package com.chinatower.platform.controller;


import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * video-export
 * 系统管理-用户列表导出
 */
@Slf4j
@RestController
public class SysUserExportController {
    @Resource
     private SysUserService userService;

    /**
     * 用户列表导出
     * @param user
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "用户列表导出")
    @TowerLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/exportSysUser")
    public void export(@RequestBody SysUser user, HttpServletResponse response)throws Exception {
        List<SysUser> list = userService.selectUserList(user);
        /*if (!CollectionUtils.isEmpty(list)){
            if (list.size()>5000){
               throw new CustomException("导出数据量最多5000条，请按照查询条件导出");
            }
        }*/
        byte[] data =new ExcelUtil<>(SysUser.class).exportBytes(list);
        FileUtils.download(response, data);
    }
}
