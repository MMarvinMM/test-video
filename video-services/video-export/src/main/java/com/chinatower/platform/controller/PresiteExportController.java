package com.chinatower.platform.controller;

import com.chinatower.platform.entity.PresiteVO;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.PresiteExportService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * video-export
 * @program: video-platform
 * @ClassName: PresiteExportController
 * @description: 预置位导出控制器
 * @author: fengty
 * @create: 2024-07-10 10:30
 */

@RestController
@Slf4j
@RequestMapping("/presiteExport")
public class PresiteExportController {

    @Autowired
    PresiteExportService presiteExportService;

    /**
     * 视频管理预置位
     */
    @PostMapping("/downloadPresiteImg")
    public void downloadPresiteImg(HttpServletResponse response, HttpServletRequest request, @RequestBody PresiteVO presiteVO) {
        try {
            if(StringUtils.isEmpty(presiteVO.getName())){
                // 获取当前的用户
                LoginUser loginUser = SecurityUtils.getLoginUser();
                // 铁塔视角
                if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())){
                    presiteVO.setName("中国铁塔");
                }else {
                    // 客户视角判断是否为铁塔切租户
                    boolean switchFlag = SecurityUtils.isSwitch();
                    //客户视角切换：已切换-sql后面拼接租户ID,未切换-
                    if(switchFlag) {
                        //已切换，水印设置为铁塔
                        presiteVO.setName("中国铁塔");
                    }else{
                        // 未切换，水印为租户名称
                        presiteVO.setName(loginUser.getUser().getTenantInfo().getTenantName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("预置位抓图，切换视角报错：{}", e);
        }
        presiteExportService.downloadPresiteImg(response, request, presiteVO);
    }


}
