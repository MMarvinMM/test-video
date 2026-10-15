package com.chinatower.platform.project.system.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.custom.common.Constants;
import com.chinatower.platform.project.system.domain.vo.SysVideoTutorialVO;
import com.chinatower.platform.project.system.service.ISysTutorialService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * video-system
 * @ClassName SysTutorialController
 * @Description 系统引导设置
 * @Author D
 * @Date 2022/2/23 16:40
 */
@Slf4j
@RestController
@RequestMapping("/base/system/tutorial")
public class SysTutorialController extends TowerBaseController {


    @Autowired
    private ISysTutorialService iSysTutorialService;


    /**
     * 保存视频系统教程
     * @param sysVideoTutorialVO
     * @return
     */
    @PostMapping("/saveVideoTutorial")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveVideoTutorial(@Validated @RequestBody SysVideoTutorialVO sysVideoTutorialVO){
        return iSysTutorialService.save(sysVideoTutorialVO);
    }


    /**
     * 编辑视频系统教程
     * @author liuxt
     * @param sysVideoTutorialVO
     */
    @PostMapping(value = "/updateVideoTutorial")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateVideoTutorial(@Validated @RequestBody SysVideoTutorialVO sysVideoTutorialVO) {
        return iSysTutorialService.updateVideoTutorial(sysVideoTutorialVO);
    }




    /**
     * 查询视频系统教程
     * @Description 查询视频系统教程
     * @author ll
     * @Date 15:38 2022/08/19
     * @Param [sysVideoTutorialVO]
     **/
    @PostMapping("/getVideoTutorial")
    public Result<PageInfo<SysVideoTutorialVO>> getVideoTutorial(@RequestBody SysVideoTutorialVO sysVideoTutorialVO){
        log.info(Constants.GET_VIDEO_TUTORIAL_LIST_REQ + JSON.toJSONString(sysVideoTutorialVO));
        if (null == sysVideoTutorialVO.getPageSize() || null == sysVideoTutorialVO.getPageNum()) {
            sysVideoTutorialVO.setPageNum(Constants.ONE_NUM);
            sysVideoTutorialVO.setPageSize(Constants.TEN_NUM);
        }
        PageHelper.startPage(sysVideoTutorialVO.getPageNum(), sysVideoTutorialVO.getPageSize());
        Result<PageInfo<SysVideoTutorialVO>> result = iSysTutorialService.getVideoTutorial(sysVideoTutorialVO);
        return result;
    }



    /**
     * 根据教程ID获取视频系统教程详情
     */
    @PostMapping(value = "/getVideoTutorialInfo")
    public Result<SysVideoTutorialVO> getVideoTutorialInfo(@RequestBody SysVideoTutorialVO mallBaseVO)
    {
        return Result.data(iSysTutorialService.getVideoTutorialInfo(mallBaseVO));
    }




    /**
     * 删除视频系统教程
     * @param sysVideoTutorialVO
     * @return
     */
    @PostMapping("/deleteVideoTutorial")
    public Result<Integer> deleteVideoTutorial(@RequestBody SysVideoTutorialVO sysVideoTutorialVO){
        return toAjax(iSysTutorialService.delete(sysVideoTutorialVO));
    }














}
