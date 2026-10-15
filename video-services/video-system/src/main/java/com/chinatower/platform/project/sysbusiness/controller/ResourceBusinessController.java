package com.chinatower.platform.project.sysbusiness.controller;

import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * video-system
 * 资源相关业务
 *
 * @author zhaol
 * @since 2022-02-14 12:03:37
 */
@RestController
@Slf4j
@RequestMapping("/resource/business")
public class ResourceBusinessController {

    @Resource
    private FileFeignClient fileFeignClient;

    /**
     * 上传
     */
    @ApiOperation(value = "上传")
    @TowerLog(title = "资源图标", businessType = BusinessType.UPDATE)
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        String avatar = null;
        if (!file.isEmpty()) {
            try {
                AjaxResult ajaxResult = fileFeignClient.uploadFile(file);
                if (ajaxResult != null && ajaxResult.ok()) {
                    SysFileInfo sysFileInfo = ajaxResult.parseData(SysFileInfo.class);
                    avatar = sysFileInfo.getFileUrl();
                }else{
                    return Result.fail("资源图标上传失败");
                }
            } catch (Exception e) {
                log.error("资源图标上传失败", e);
                return Result.fail(e.getMessage());
            }
        }
        return Result.data(avatar);
    }


}
