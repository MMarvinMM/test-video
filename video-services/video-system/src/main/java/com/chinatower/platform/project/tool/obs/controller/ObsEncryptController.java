package com.chinatower.platform.project.tool.obs.controller;

import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.framework.web.controller.BaseController;
import com.chinatower.system.api.request.file.ObsEncryptRequest;
import com.chinatower.system.api.resp.ObsEncryptResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @className: ObsEncryptController
 * @description: obs加密方法
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Api(tags = "OBS管理")
@RestController
@RequestMapping("/file/obsFiles")
@SuppressWarnings("all")
public class ObsEncryptController extends BaseController {


    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 5.16	 获取加密后的OBS文件地址
     */
    @ApiOperation(value = "OBS加密url获取")
    @PostMapping(value = "/getObsEncryptUrls")
    public ApiResult<List<ObsEncryptResponse>> getObsEncryptUrls(@RequestBody ObsEncryptRequest obsEncryptRequest) {
        try {
            List<String> fileUrls = obsEncryptRequest.getFileUrls();
            if (fileUrls == null || fileUrls.isEmpty()) {
                return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG, ApiResult.ERROR_CODE_1001, "原始路径fileUrls不可为空");
            }
            List<ObsEncryptResponse> list = new ArrayList<>();
            Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrls);
            for (String url : fileUrls) {
                ObsEncryptResponse urlData = new ObsEncryptResponse();
                urlData.setFileUrl(url);
                String encryptUrl = map.get(url);
                urlData.setEncryptUrl(encryptUrl);
                list.add(urlData);
            }
            return ApiResult.SUCCESS(list, ApiResult.RETURN_CODE_SUCCESS_MSG);
        } catch (Exception e) {
            logger.error("obs加密异常：{}", e);
            return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG, ApiResult.ERROR_CODE_1004, "内部服务异常");
        }
    }
}
