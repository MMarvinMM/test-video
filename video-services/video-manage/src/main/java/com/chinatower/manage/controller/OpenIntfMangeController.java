package com.chinatower.manage.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.manage.dto.open.OpenIntfWholeInfoModel;
import com.chinatower.manage.dto.open.OpenManageInfoModel;
import com.chinatower.manage.repository.IOpenIntfMangeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-manage
 */
@RestController
@RequestMapping("/OpenIntfMange")
@Api(tags = {"/OpenIntfMange - API接口管理"})
@Slf4j
public class OpenIntfMangeController extends BaseController {

    @Autowired
    private IOpenIntfMangeService iOpenIntfMangeService;


    /**
     * 开放平台API接口列表
     */
    @PostMapping("api/queryOpenPlatformInfo")
    public Result<Object> queryOpenPlatformInfo(@RequestBody OpenManageInfoModel model) {
        return iOpenIntfMangeService.queryOpenPlatformInfo(model);
    }

    /**
     * 开放平台API接口详情
     */
    @PostMapping("api/queryOpenPlatformByIntfId")
    public Result<OpenIntfWholeInfoModel> queryOpenPlatformByIntfId(@RequestBody OpenManageInfoModel model) {
        Result<OpenIntfWholeInfoModel> result = null;
        try {
            result = iOpenIntfMangeService.queryOpenIntfWholeInfoByApiIntfId(Long.parseLong(model.getOpenIntfId()),model.getType());
        } catch (Exception e) {
            log.error("查询API接口全部信息失败！", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "查询API接口全部信息失败！");
        }
        return result;
    }

}
