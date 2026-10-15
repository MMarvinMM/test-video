package com.chinatower.video.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.ArCruisePlanReq;
import com.chinatower.video.api.entity.DoLinkageReq;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName ArCruisePlanCustomAPI
 * @Desc TODO
 * @Author songchunlin
 * @Date 2023/4/18 10:43
 * @Version 1.0
 **/
public interface ArCruisePlanCustomAPI {

    @PostMapping("/arCruisePlan/addOrUpdate")
    Result<String> addOrUpdate(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

    @PostMapping("/arCruisePlan/list")
    Result<Page<ArCruisePlan>> list(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

    @PostMapping("/arCruisePlan/delete")
    Result<String> delete(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

    @PostMapping("/arCruisePlan/details")
    Result<ArCruisePlan> details(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

    @PostMapping("/arCruisePlan/executionDetails")
    Result<ArCruisePlan> executionDetails(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

    @PostMapping("/arCruisePlan/startCruise")
    Result<List<ArCruisePlan>> startCruise(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

    @PostMapping("/arCruisePlan/stopCruise")
    Result<String> stopCruise(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

    @PostMapping("/arCruisePlan/doLinkage")
    Result doLinkage(@Validated @RequestBody DoLinkageReq doLinkageReq);

    @PostMapping("/arCruisePlan/getCruisePlanCount")
    Result getCruisePlanCount(@Validated @RequestBody ArCruisePlanReq arCruisePlanReq);

}
