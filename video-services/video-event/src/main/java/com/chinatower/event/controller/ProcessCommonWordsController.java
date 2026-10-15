package com.chinatower.event.controller;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.entity.dto.ProcessCommonWordsDTO;
import com.chinatower.event.repository.IProcessCommonWordsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-event
 * <p>
 * 事件处理常用语 前端控制器
 * </p>
 * @ClassName ProcessCommonWordsController
 * @author lyy
 * @Date 2023/01/29
 * @Version 1.0
 */
@Api(value = "事件处理常用语-Api", tags = "事件处理常用语请求接口")
@Slf4j
@RestController
@RequestMapping("/commonWords")
public class ProcessCommonWordsController extends TowerBaseController {

    @Autowired
    private IProcessCommonWordsService processCommonWordsService;

    /**
     * 功能描述：查询事件处理常用语
     * @return Result<List<ProcessCommonWordsDTO>>
     */
    @PostMapping("/getProcessCommonWords")
    public Result<List<ProcessCommonWordsDTO>> getProcessCommonWords(){
        List<ProcessCommonWordsDTO> list = processCommonWordsService.getProcessCommonWords();
        return Result.data(list);
    }

    /**
     * 功能描述：编辑保存事件处理常用语
     * @param commonWordsDTOList
     * @return Result
     */
    @PostMapping("/updateProcessCommonWords")
    public Result updateProcessCommonWords(@RequestBody List<ProcessCommonWordsDTO> commonWordsDTOList) {
        try {
            processCommonWordsService.updateProcessCommonWords(commonWordsDTOList);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(MessageConstants.EVENT_PROCESS_COMMONWORDS_MSG);
    }
}
