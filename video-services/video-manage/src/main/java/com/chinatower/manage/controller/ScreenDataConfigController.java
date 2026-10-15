package com.chinatower.manage.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.manage.dto.ScreenDataConfigCountVO;
import com.chinatower.manage.dto.ScreenDataConfigVO;
import com.chinatower.manage.repository.IScreenDataConfigService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName: 大屏数据配置控制器
 * @Author: wangyt
 * @CreateTime: 2025-01-06  09:51
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/screenDataConfig")
@Api(tags = {"/screenDataConfig - API接口管理"})
@Slf4j
public class ScreenDataConfigController extends BaseController {

    @Autowired
    private IScreenDataConfigService screenDataConfigService;

    /**
     * @Description: 分页查询配置数量信息
     * @Author: wangyt
     * @Date: 2025/1/6
     * @param screenDataConfigCount
     * @return com.chinatower.common.entity.Result
     **/
    @PostMapping("/selectConfigCount")
    public Result selectScreenDataConfigCount(@RequestBody ScreenDataConfigCountVO screenDataConfigCount) {
        return Result.data(getDataTable(screenDataConfigService.selectScreenDataConfigCount(screenDataConfigCount)));
    }

    /**
     * @Description: 查询配置信息详情
     * @Author: wangyt
     * @Date: 2025/1/7
     * @param screenDataConfig
     * @return com.chinatower.common.entity.Result
     **/
    @PostMapping("/selectConfigById")
    public Result selectConfigById(@RequestBody ScreenDataConfigVO screenDataConfig) {
        return screenDataConfigService.selectConfigById(screenDataConfig);
    }

    /**
     * @Description: 新增大屏配置（订购同步使用）
     * @Author: wangyt
     * @Date: 2025/1/7
     * @param screenDataConfigList
     * @return com.chinatower.common.entity.Result
     **/
    @PostMapping("/addConfig")
    public Result addConfig(@RequestBody List<ScreenDataConfigVO> screenDataConfigList) {
        return screenDataConfigService.addConfig(screenDataConfigList);
    }

    /**
     * @Description: 第三方告警同步
     * @Author: wangyt
     * @Date: 2025/1/21
     * @param screenDataConfigList
     * @return com.chinatower.common.entity.Result
     **/
    @PostMapping("/editThirdPartyAlarmConfig")
    public Result editThirdPartyAlarmConfig(@RequestBody List<ScreenDataConfigVO> screenDataConfigList) {
        return screenDataConfigService.editThirdPartyAlarmConfig(screenDataConfigList);
    }

    /**
     * @Description: 编辑大屏配置
     * @Author: wangyt
     * @Date: 2025/1/7
     * @param screenDataConfig
     * @return com.chinatower.common.entity.Result
     **/
    @PostMapping("/editConfig")
    public Result editConfig(@RequestBody ScreenDataConfigVO screenDataConfig) {
        return screenDataConfigService.editConfig(screenDataConfig);
    }
}
