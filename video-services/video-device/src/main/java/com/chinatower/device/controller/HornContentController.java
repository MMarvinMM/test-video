package com.chinatower.device.controller;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.AudioDetailResponseDTO;
import com.chinatower.device.dto.TextDetailResponseDTO;
import com.chinatower.device.entity.dto.VmHornAuditionRequestDTO;
import com.chinatower.device.entity.dto.VmHornContentConfDTO;
import com.chinatower.device.entity.dto.VmHornContentConfRequestListDTO;
import com.chinatower.device.entity.dto.VmHornContentRequestDTO;
import com.chinatower.device.entity.vo.HornAuditionResultVO;
import com.chinatower.device.entity.vo.VmHornContentVO;
import com.chinatower.device.service.IVmHornContentService;
import com.chinatower.platform.constants.CommonConstants;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-device
 */
@RestController
@Api(value = "广播管理-内容管理")
@RequestMapping("/horn/content")
@Slf4j
public class HornContentController extends BaseController {
    @Autowired
    private IVmHornContentService vmHornContentService;

    /**
     * 广播内容列表
     *
     * @return
     */
    @PostMapping("/list")
    public Result<PageInfo<VmHornContentConfDTO>> list(@RequestBody VmHornContentConfRequestListDTO param) {
        return vmHornContentService.list(param);
    }

    /**
     * 查询广播内容详情， baseFileUrl需加密返回
     *
     * @param hornContentConfDTO
     * @return
     */
    @PostMapping("/detail")
    public Result<VmHornContentConfDTO> detail(@RequestBody VmHornContentConfRequestListDTO hornContentConfDTO) {
        if (ObjectUtil.isEmpty(hornContentConfDTO) || ObjectUtil.isEmpty(hornContentConfDTO.getId())) {
            return Result.fail("广播内容ID不能为空");
        }
        return vmHornContentService.detail(hornContentConfDTO);
    }

    /**
     * 新增广播内容
     *
     * @param param
     * @return
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody VmHornContentVO param) {
        if (ObjectUtil.isEmpty(param)) {
            return Result.fail(CommonConstants.NOT_EMPTY_MSG);
        }
        return vmHornContentService.add(param);
    }

    /**
     * 修改广播内容
     *
     * @param param
     * @return
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody VmHornContentVO param) {
        if (ObjectUtil.isEmpty(param)) {
            return Result.fail(CommonConstants.NOT_EMPTY_MSG);
        }
        return vmHornContentService.update(param);
    }

    /**
     * 审核广播内容
     *
     * @param param
     * @return
     */
    @PostMapping("/audit")
    public Result<String> audit(@RequestBody VmHornContentVO param) {
        if (ObjectUtil.isEmpty(param)) {
            return Result.fail(CommonConstants.NOT_EMPTY_MSG);
        }
        return vmHornContentService.audit(param);
    }

    /**
     * 删除广播内容
     *
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody List<String> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return Result.fail(CommonConstants.NOT_EMPTY_MSG);
        }
        return vmHornContentService.delete(ids);
    }

    /**
     * 根据内容类型及查询条件获取广播内容分页结果
     *
     * @return
     */
    @PostMapping("/getListByContentType")
    public Result<PageInfo<VmHornContentConfDTO>> getListByContentType(@RequestBody VmHornContentRequestDTO param) {
        return vmHornContentService.getListByContentType(param);
    }

    /**
     * 广播内容试听
     *
     * @return
     */
    @PostMapping("/audition")
    public Result<HornAuditionResultVO> audition(@RequestBody VmHornAuditionRequestDTO param) {
        return vmHornContentService.audition(param);
    }

    /**
     * 查询文本详情
     *
     * @param txtId
     * @return
     */
    @PostMapping("/textDetail")
    public Result<TextDetailResponseDTO> textDetail(@RequestParam("txtId") String txtId) {
        return vmHornContentService.textDetail(txtId);
    }

    /**
     * 查询音频详情
     *
     * @param audioId
     * @return
     */
    @PostMapping("/audioDetail")
    public Result<AudioDetailResponseDTO> audioDetail(@RequestParam("audioId") String audioId) {
        return vmHornContentService.audioDetail(audioId);
    }
}
