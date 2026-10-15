package com.chinatower.device.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.AnalysisDTO;
import com.chinatower.device.service.IVideoDeviceInfoService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * video-device
 * @ClassName AnalysisController
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/16
 **/

@RestController
@Slf4j
public class AnalysisController extends BaseController {

    @Resource
    private IVideoDeviceInfoService iVideoDeviceInfoService;

    /**
     * 统计监控设备数量
     *
     * @param analysisDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/16
     * @version: 1.0
     */
    @Deprecated
    @PostMapping("/video/device/queryDeviceStatistics")
    @DataScope(tenantColumn = "o.tenant_id", acceptColumn="o.accept_node", industryCode = "o.industry_code", deviceColumn = "o.device_code")
    public Result<Map<String, Object>> queryDeviceStatistics(@RequestBody AnalysisDTO analysisDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        analysisDTO.setTenantId(user.getTenantId());
        analysisDTO.setIndustryCode(user.getIndustryCode());
        Map<String, Object> resultMap = iVideoDeviceInfoService.queryDeviceStatistics(analysisDTO);
        return Result.data(resultMap);
    }

}
