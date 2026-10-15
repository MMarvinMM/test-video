package com.chinatower.platform.project.node.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.project.node.entity.PlatFormVO;
import com.chinatower.platform.project.node.service.INodeService;
import com.chinatower.system.api.model.ReplaceTokenVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


/**
 * video-system
 *
 */
@Slf4j
@RestController
@RequestMapping("/system")
public class NodeController extends TowerBaseController {

    @Resource
    INodeService iNoteService;

    /**
     * 根据客户编码查询节点列表
     *
     * @param:
     * @return:
     * @author:
     * @date:
     * @version:
     */
    @ApiOperation(value = "根据客户编码查询节点列表")
    @PostMapping("/selectNodeByTenantId")
    public Result<List<PlatFormVO>> selectNodeByTenantId(@RequestBody PlatFormVO platFormVO) {
        return Result.data(iNoteService.selectNodeByTenantId(platFormVO));
    }

    /**
     * 生成目标客户token
     *
     * @param:
     * @return:
     * @author:
     * @date:
     * @version:
     */
    @ApiOperation(value = "生成目标客户token")
    @PostMapping("/replaceToken")
    public Result<String> replaceToken(@Valid @RequestBody ReplaceTokenVO replaceTokenVO) {
        return iNoteService.replaceToken(replaceTokenVO);
    }
}
