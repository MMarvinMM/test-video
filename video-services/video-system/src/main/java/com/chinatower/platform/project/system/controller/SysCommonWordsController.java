package com.chinatower.platform.project.system.controller;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.system.service.ISysCommonWordsService;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.system.api.model.VmCommonWordsVO;
import com.chinatower.system.api.request.words.wordsRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * video-system
 *
 */
@Api(tags = "常用语")
@Slf4j
@RestController
@RequestMapping("/system/commonWords")
public class SysCommonWordsController extends TowerBaseController {
    @Resource
    private ISysCommonWordsService iVmCommonWordsService;
    @Resource
    ISysImportService importService;

    /**
     * 常用语查询
     */
    @ApiOperation(value = "常用语查询",notes = "常用语查询")
    @PostMapping("/getCommonWords")
    public Result<List<VmCommonWordsVO>> getCommonWords(@RequestBody wordsRequest wordsVO){
        List<VmCommonWordsVO> list = iVmCommonWordsService.getCommonWords(wordsVO);
        return Result.data(list);
    }

    /**
     * 新增常用语
     */
    @ApiOperation(value = "新增常用语",notes = "新增常用语")
    @PostMapping("/addCommonWords")
    public Result add(@RequestBody wordsRequest vo){
        return iVmCommonWordsService.add(vo);
    }

    /**
     * 删除常用语
     */
    @ApiOperation(value = "删除常用语")
    @PostMapping("/deleteCommonWordsByWordsId")
    public Result deleteCommonWordsByWordsId(@RequestBody wordsRequest vo) {
        return toAjax(iVmCommonWordsService.deleteCommonWordsByWordsId(vo));
    }
    /**
     * 批量删除常用语
     */
    @ApiOperation(value = "批量删除常用语")
    @PostMapping("/deleteCommonWordsByWordsIdBatch")
    public Result deleteCommonWordsByWordsIdBatch(@RequestBody wordsRequest vo) {
        return toAjax(iVmCommonWordsService.deleteCommonWordsByWordsIdBatch(vo));
    }

    /**
     * 修改常用语
     */
    @ApiOperation(value = "修改常用语")
    @PostMapping(value = "/updateCommonWords")
    public Result<Integer> updateCommonWords(@RequestBody wordsRequest vo) {
        return toAjax(iVmCommonWordsService.updateCommonWords(vo));
    }

    /**
     * 批量修改常用语
     */
    @ApiOperation(value = "批量修改常用语")
    @PostMapping(value = "/updateCommonWordsBatch")
    public Result<String> updateCommonWordsBatch(@RequestBody wordsRequest vo) {
        iVmCommonWordsService.updateCommonWordsBatch(vo);

        return Result.success("批量修改常用语成功!");
    }

    /**
     * 查询失败描述
     */
    @ApiOperation(value = "查询失败描述")
    @PostMapping(value = "/queryFalseWords")
    public Result<String> queryFalseWords(@RequestBody String falseCode) {
        return Result.data(iVmCommonWordsService.queryFalseWords(falseCode));
    }

    /**
     * 巡查任务常用语查询
     */
    @ApiOperation(value = "巡查任务常用语查询",notes = "巡查任务常用语查询")
    @PostMapping("/getCommonWordsForInspection")
    public Result<List<VmCommonWordsVO>> getCommonWordsForInspection(@RequestBody wordsRequest wordsVO){
        List<VmCommonWordsVO> list = iVmCommonWordsService.getCommonWordsForInspection(wordsVO);
        return Result.data(list);
    }

    /**
     * 编辑保存巡查任务常用语
     * @param commonWordsDTOList
     * @return Result
     */
    @PostMapping("/updateProcessCommonWordsForInspection")
    public Result updateProcessCommonWords(@RequestBody List<wordsRequest> commonWordsDTOList) {
        try {
            iVmCommonWordsService.updateCommonWordsForInspection(commonWordsDTOList);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(Constants.EVENT_PROCESS_COMMONWORDS_MSG);
    }


    /**
     * 批量新增常用语
     */
    @ApiOperation(value = "批量新增常用语")
    @PostMapping(value = "/addCommonWordsBatch")
    public Result<String> addCommonWordsBatch(@RequestBody List<wordsRequest> commonWordsDTOList) {
        iVmCommonWordsService.addCommonWordsBatch(commonWordsDTOList);
        return Result.success("批量新增常用语成功!");
    }

}
