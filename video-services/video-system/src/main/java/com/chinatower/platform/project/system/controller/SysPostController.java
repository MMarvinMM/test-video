package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;

import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.SysPost;
import com.chinatower.platform.project.system.service.ISysPostService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * video-system
 * 岗位信息操作处理
 *
 * @author iwhalecloud
 */
@Api(tags = "岗位信息操作处理")
@RestController
@RequestMapping("/system/post")
public class SysPostController extends TowerBaseController
{
    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @ApiOperation(value = "获取岗位列表")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post)
    {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }

    /**
     * 岗位管理导出
     */
    @ApiOperation(value = "岗位管理导出")
    @TowerLog(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody  SysPost post, HttpServletResponse response)
    {
        List<SysPost> list = postService.selectPostList(post);
        byte[] data =new ExcelUtil<>(SysPost.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @ApiOperation(value = "根据岗位编号获取详细信息")
    @GetMapping(value = "/detail/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        return AjaxResult.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @ApiOperation(value = "新增岗位")
    @TowerLog(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody SysPost post)
    {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return Result.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return Result.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreator(SecurityUtils.getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @ApiOperation(value = "修改岗位")
    @TowerLog(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody SysPost post)
    {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return Result.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return Result.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setModifier(SecurityUtils.getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @ApiOperation(value = "删除岗位")
    @TowerLog(title = "岗位管理", businessType = BusinessType.DELETE)
    @GetMapping("/del/{postIds}")
    public Result remove(@PathVariable Long[] postIds)
    {
        try {
            return toAjax(postService.deletePostByIds(postIds));
        } catch (Exception e) {
            logger.error("删除岗位失败，{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取岗位选择框列表
     */
    @ApiOperation(value = "获取岗位选择框列表")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return AjaxResult.success(posts);
    }
}
