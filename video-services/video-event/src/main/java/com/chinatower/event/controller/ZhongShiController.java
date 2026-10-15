package com.chinatower.event.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.entity.Result;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.zhongshi.AlgorithmDto;
import com.chinatower.event.dto.zhongshi.FileDto;
import com.chinatower.event.dto.zhongshi.ProgressDto;
import com.chinatower.event.dto.zhongshi.StatisticsDto;
import com.chinatower.event.service.AlgorithmService;
import com.chinatower.event.service.FileService;

import com.chinatower.event.service.ProgressService;
import com.chinatower.event.service.StatisticsService;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/29 14:15
 */
@RestController
@Slf4j
public class ZhongShiController {

    @Autowired
    private FileService fileService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/selectStatistics")
    public Result selectStatistics() {
        QueryWrapper<StatisticsDto> queryWrapper = new QueryWrapper();
        queryWrapper.eq("delete_flag","0");
        queryWrapper.orderByDesc("create_time");
        return Result.data(statisticsService.list(queryWrapper));
    }

    @PostMapping("/insertStatistics")
    public Result insertStatistics(@RequestBody StatisticsDto req){
        req.setCreateTime(new Date());
        req.setDelete_flag(Constants.ZERO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        req.setCreateUser(loginUser.getUser().getUserName());
        return Result.data(statisticsService.save(req));
    }


    @PostMapping("/updateStatistics")
    public Result updateStatistics(@RequestBody StatisticsDto req){
        req.setUpdateTime(new Date());
        req.setDelete_flag(Constants.ZERO);
        return Result.data(statisticsService.updateById(req));
    }

    @EventObsEncrypt
    @PostMapping("/selectFile")
    public Result selectFile() {
        QueryWrapper<FileDto> queryWrapper = new QueryWrapper();
        queryWrapper.eq("delete_flag","0");
        queryWrapper.orderByDesc("create_time");
        return Result.data(fileService.list(queryWrapper));
    }

    @PostMapping("/insertFile")
    public Result insertFile(@RequestBody FileDto req){
        req.setCreateTime(new Date());
        req.setDelete_flag(Constants.ZERO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        req.setCreateUser(loginUser.getUser().getUserName());
        return Result.data(fileService.save(req));
    }


    @PostMapping("/delFile")
    public Result delFile(@RequestBody FileDto req){
        return Result.data(fileService.removeById(req.getId()));
    }

    @PostMapping("/insertProgress")
    public Result insertProgress(@RequestBody ProgressDto req){
        req.setCreateTime(new Date());
        req.setDelete_flag(Constants.ZERO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        req.setCreateUser(loginUser.getUser().getUserName());
        return Result.data(progressService.save(req));
    }

    @PostMapping("/updateProgress")
    public Result updateProgress(@RequestBody ProgressDto req){
        req.setUpdateTime(new Date());
        req.setDelete_flag(Constants.ZERO);
        return Result.data(progressService.updateById(req));
    }

    @PostMapping("/delProgress")
    public Result delProgress(@RequestBody ProgressDto req){
        return Result.data(progressService.removeById(req.getId()));
    }

    @PostMapping("/selectProgress")
    public Result selectProgress() {
        QueryWrapper<ProgressDto> queryWrapper = new QueryWrapper();
        queryWrapper.eq("delete_flag","0");
        queryWrapper.orderByDesc("create_time");
        return Result.data(progressService.list(queryWrapper));
    }

    @PostMapping("/insertAlgorithm")
    public Result insertAlgorithm(@RequestBody AlgorithmDto req){
        req.setCreateTime(new Date());
        req.setDelete_flag(Constants.ZERO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        req.setCreateUser(loginUser.getUser().getUserName());
        return Result.data(algorithmService.save(req));
    }

    @PostMapping("/updateAlgorithm")
    public Result updateAlgorithm(@RequestBody AlgorithmDto req){
        req.setUpdateTime(new Date());
        req.setDelete_flag(Constants.ZERO);
        return Result.data(algorithmService.updateById(req));
    }

    @PostMapping("/delAlgorithm")
    public Result delAlgorithm(@RequestBody AlgorithmDto req){
        return Result.data(algorithmService.removeById(req.getId()));
    }

    @PostMapping("/selectAlgorithm")
    public Result selectAlgorithm() {
        QueryWrapper<AlgorithmDto> queryWrapper = new QueryWrapper();
        queryWrapper.eq("delete_flag","0");
        queryWrapper.orderByDesc("create_time");
        return Result.data(algorithmService.list(queryWrapper));
    }
}
