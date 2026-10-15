package com.chinatower.platform.project.system.controller;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.system.domain.SysTutorialUser;
import com.chinatower.platform.project.system.domain.vo.TutorialIndustryVO;
import com.chinatower.platform.project.system.service.ISysTutorialUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.content.SysConstants;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * video-system
 * @description 用户教程
 * @author luo_ji
 * @date 2022-12-15
 */
@Slf4j
@RestController
@RequestMapping(value = "/sysTutorialUser")
public class SysTutorialUserController extends TowerBaseController {

    @Autowired
    private ISysTutorialUserService sysTutorialUserService;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 新增操作手册
     * @author luo_ji
     * @date 2022/12/15
     **/
    @PostMapping("/insert")
    public Result insert(@RequestBody SysTutorialUser sysTutorialUser){
        if (ObjectUtil.isNotEmpty(sysTutorialUser) && StringUtils.isNotEmpty(sysTutorialUser.getFileUrl())){
            sysTutorialUser.setFileUrl(StringUtils.substringBefore(sysTutorialUser.getFileUrl(),"?"));
        }
        //用户手册名称重复校验
        if (!StringUtils.isEmpty(sysTutorialUserService.checkRepeat(sysTutorialUser))) {
            return Result.fail(Constants.TUTORAIL_EXIST);
        }
        if (sysTutorialUser.getTutorailTitle().length() > Constants.THIRTY_NUM){
            return Result.fail(Constants.TUTORAIL_MORE_THAN_30);
        }
        return toAjax(sysTutorialUserService.insert(sysTutorialUser));
    }

    /**
     * 刪除操作手册
     * @author luo_ji
     * @date 2022/12/15
     **/
    @GetMapping("/delete/{tutorialId}")
    public Result delete(@PathVariable("tutorialId") Long tutorialId){
        return toAjax(sysTutorialUserService.delete(tutorialId));
    }

    /**
     * 更新操作手册
     * @author luo_ji
     * @date 2022/12/15
     **/
    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysTutorialUser sysTutorialUser){
        if (ObjectUtil.isNotEmpty(sysTutorialUser) && StringUtils.isNotEmpty(sysTutorialUser.getFileUrl())){
            sysTutorialUser.setFileUrl(StringUtils.substringBefore(sysTutorialUser.getFileUrl(),"?"));
        }
        //用户手册名称重复校验
        if (!StringUtils.isEmpty(sysTutorialUserService.checkRepeat(sysTutorialUser))) {
            return Result.fail(Constants.TUTORAIL_EXIST);

        }
        if (sysTutorialUser.getTutorailTitle().length() > Constants.THIRTY_NUM){
            return Result.fail(Constants.TUTORAIL_MORE_THAN_30);
        }
        return toAjax(sysTutorialUserService.update(sysTutorialUser));
    }

    /**
     * 查询 根据主键 id 查询
     * @author luo_ji
     * @date 2022/12/15
     **/
    @GetMapping("/detail/{tutorialId}")
    public Result<SysTutorialUser> load(@PathVariable("tutorialId") Long tutorialId){
        SysTutorialUser tutorialUser = sysTutorialUserService.load(tutorialId);
        if (ObjectUtil.isNotEmpty(tutorialUser) && StringUtils.isNotEmpty(tutorialUser.getFileUrl())){
            String url = fileUrlEncryptUtil.dealObsFileUrl(tutorialUser.getFileUrl());
            tutorialUser.setFileUrl(url);
        }
        return Result.data(tutorialUser);
    }

    /**
     * 查询 分页查询
     * @author luo_ji
     * @date 2022/12/15
     **/
    @PostMapping("/list")
    public TableDataInfo pageList(@RequestBody SysTutorialUser sysTutorialUser) {
        if (null == sysTutorialUser.getPageSize() || null == sysTutorialUser.getPageNum()) {
            sysTutorialUser.setPageNum(Constants.ONE);
            sysTutorialUser.setPageSize(Constants.TEN);
        }
        PageHelper.startPage(sysTutorialUser.getPageNum(), sysTutorialUser.getPageSize());
        List<SysTutorialUser> list = sysTutorialUserService.pageList(sysTutorialUser);
        if (list != null && list.size() > 0) {
            List<String> collect = list.stream().map(SysTutorialUser::getFileUrl).collect(Collectors.toList());
            Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(collect);
            list.forEach(r -> r.setFileUrl(map.get(r.getFileUrl())));
        }
        return getDataTable(list);
    }

    /**
     * 查询所属行业接口
     * @param tutorialIndustryVO
     * @return
     */
    @PostMapping(value = "/getIndustryList")
    public Result<List<TutorialIndustryVO>> getIndustryList(@RequestBody TutorialIndustryVO tutorialIndustryVO)
    {
        return Result.data(sysTutorialUserService.getIndustryList(tutorialIndustryVO));
    }




    public Boolean check (){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String userType = currentUser.getUser().getUserType();
        if (userType.equals(SysConstants.USET_TYPE_2)) {
            return false;
        }else {
            return true;
        }
    }

}