package com.chinatower.platform.ecmwf.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.EcmwfInfoParam;
import com.chinatower.outdata.api.entity.EcmwfVO;
import com.chinatower.platform.ecmwf.service.ISysEcmwfService;
import com.xxl.job.core.biz.model.ReturnT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * video-out-data
 * @ClassName SysEcmwfController
 * @Description 气象信息记录
 */
@Api(tags = "气象信息记录")
@Slf4j
@RestController
@RequestMapping("/outdata/ecmwf")
public class SysEcmwfController  {
    @Resource
    private ISysEcmwfService iSysEcmwfService;


    /**
     * 读取csv文件
     * @param filePath
     */
    @RequestMapping("/readCsv")
    public void readCsv(@RequestBody String filePath){

        iSysEcmwfService.readCsv(filePath);
    }

    /**
     * 气象信息查询
     * @param vo
     * @return
     */
    @ApiOperation(value = "气象信息查询",notes = "气象信息查询")
    @PostMapping("/getEcmwf")
    public Result<EcmwfInfoParam> getEcmwf(@RequestBody EcmwfVO vo){
        EcmwfInfoParam list = iSysEcmwfService.getEcmwf(vo);
        return Result.data(list);
    }


    /**
     * 定时任务获取气象数据
     * @param vo
     * @return
     */
    @ApiOperation(value = "气象信息查询",notes = "气象信息查询")
    @PostMapping("/runGetEcmwfInfoHandler")
    public ReturnT<String> runGetEcmwfInfoHandler(@RequestBody EcmwfVO vo){
        String param = "D:\\ecmwf\\run.bat,D:\\ecmwf\\tp\\";
        ReturnT<String> b = iSysEcmwfService.runGetEcmwfInfoHandler(param );
        return b;
    }
}
