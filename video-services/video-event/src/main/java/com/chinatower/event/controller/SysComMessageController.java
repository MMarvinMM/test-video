package com.chinatower.event.controller;

import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.event.repository.SysComMessageService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author WLF
 * @description 融合通信模块
 * @date 2024/10/21 9:42
 */

@Api(tags = "融合通信模块")
@RestController
@Slf4j
public class SysComMessageController extends BaseController {
    @Resource
    private SysComMessageService sysComMessageService;

    /**
     * 查询融合通信列表
     *
     * @param communicationVo
     * @return
     */
    @PostMapping("/communication/queryList")
    public TableDataInfo queryList(@RequestBody CommunicationVo communicationVo) {
        return getDataTable(sysComMessageService.queryList(communicationVo));
    }

    /**
     * 编辑融合通信信息
     *
     * @param communicationVo
     * @return
     */
    @PostMapping("/communication/edit")
    public Result editCommunicationInfo(@RequestBody CommunicationVo communicationVo) {
        return sysComMessageService.editCommunicationInfo(communicationVo);
    }

    /**
     * 通过号码查询用户信息
     *
     * @param communicationVo
     * @return
     */
    @PostMapping("/communication/queryInfoByTel")
    public Result queryInfoByTel(@RequestBody CommunicationVo communicationVo) {
        return Result.data(sysComMessageService.queryInfoByTel(communicationVo));
    }
}
