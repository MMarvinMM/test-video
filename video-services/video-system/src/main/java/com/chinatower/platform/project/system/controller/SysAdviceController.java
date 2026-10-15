package com.chinatower.platform.project.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.domain.SysAdvice;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.service.ISysAdviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * video-system
 *
 */
@Api(tags = "意见反馈信息")
@Slf4j
@RestController
@RequestMapping("/system/advice")
public class SysAdviceController extends TowerBaseController {

    @Autowired
    private ISysAdviceService adviceService;



    /**
     * 新增意见反馈
     */
    @ApiOperation(value = "新增意见反馈")
    @PostMapping("/addAdvice")
    public AjaxResult addAdvice(@RequestBody SysAdvice sysAdvice){
        if (StringUtils.isEmpty(sysAdvice.getAdviceType())||StringUtils.isEmpty(sysAdvice.getAdviceText())){
            return AjaxResult.error("必填参数不能为空");
        }
        return AjaxResult.success(adviceService.insert(sysAdvice));
    }


    /**
     * 多文件上传功能
     */
    @PostMapping("/orderSystemUpload")
    public AjaxResult upload(MultipartFile[] multipartFiles) {
        for (MultipartFile multipartFile : multipartFiles) {
            System.out.println(multipartFile.getOriginalFilename());
        }
        return AjaxResult.success();
    }
    @PostMapping("/adviceSyncOrderSystem")
    public Result adviceSyncOrderSystem(@RequestBody JSONObject sysAdvice){
        log.info("意见反馈信息同步订单系统参数：{}",sysAdvice);
       return adviceService.adviceSyncOrderSystem(sysAdvice);
    }
}
