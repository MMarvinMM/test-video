package com.chinatower.video.modules.arButton.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.video.modules.arButton.entity.ArButtonInfo;
import com.chinatower.video.modules.arButton.service.ButtonService;
import com.chinatower.video.modules.constants.ArConstants;
import com.chinatower.video.modules.constants.IndErrorMsgConstants;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program: video-platform
 * @ClassName: ArButtonController
 * @description: AR大屏，配置按钮控制器
 * @author: fengty
 * @create: 2024-04-07 10:59
 */
@Slf4j
@RestController
@RequestMapping("/arButton")
public class ArButtonController {

    @Autowired
    private ButtonService buttonService;



    // 新增按钮
    @PostMapping("/add")
    public Result<String> addButton(@Validated @RequestBody ArButtonInfo buttonInfo) {
        Map<String, Object> successMap = null;
        try {
            successMap = buttonService.addButton(buttonInfo);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        if((boolean)successMap.get(ArConstants.SUCCESS_KEY)) {
            return Result.success((String)successMap.get(ArConstants.MSG_KEY));
        } else {
            return Result.fail((String)successMap.get(ArConstants.MSG_KEY));
        }
    }

    // 删除按钮
    @PostMapping("/delete")
    public Result<String> deleteButton(@RequestBody ArButtonInfo buttonInfo) {
        if (buttonInfo.getId() == null) {
            return Result.fail(IndErrorMsgConstants.AR_BUTTON_SELECT_NULL);
        }
        Map<String, Object> successMap;
        try {
            successMap = buttonService.deleteButton(buttonInfo.getId());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        if ((boolean) successMap.get(ArConstants.SUCCESS_KEY)) {
            return Result.success((String) successMap.get(ArConstants.MSG_KEY));
        } else {
            return Result.fail((String) successMap.get(ArConstants.MSG_KEY));
        }
    }

    // 更新按钮
    @PostMapping("/update")
    public Result<String> updateButton(@RequestBody ArButtonInfo buttonInfo) {
        if(buttonInfo == null || buttonInfo.getId() == null) {
            return Result.fail(IndErrorMsgConstants.AR_BUTTON_SELECT_NULL);
        }
        Map<String, Object> successMap;
        try {
            successMap = buttonService.updateButton(buttonInfo);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        if ((boolean) successMap.get(ArConstants.SUCCESS_KEY)) {
            return Result.success((String) successMap.get(ArConstants.MSG_KEY));
        } else {
            return Result.fail((String) successMap.get(ArConstants.MSG_KEY));
        }
    }

    @PostMapping("/turnButtonStatus")
    public Result<String> turnButtonStatus(@RequestBody ArButtonInfo buttonInfo) {
        Map<String, Object> successMap;
        try {
            successMap = buttonService.turnButtonStatus(buttonInfo);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        if ((boolean) successMap.get(ArConstants.SUCCESS_KEY)) {
            return Result.success((String) successMap.get(ArConstants.MSG_KEY));
        } else {
            return Result.fail((String) successMap.get(ArConstants.MSG_KEY));
        }
    }

    // 获取按钮详情
    @GetMapping("/getButtonById/{id}")
    public Result<ArButtonInfo> getButtonInfo(@PathVariable Long id) {
        ArButtonInfo arButtonInfo = buttonService.getButtonById(id);
        if(arButtonInfo != null) {
            return Result.data(arButtonInfo);
        }
        return Result.fail("查询失败！");
    }

    @PostMapping("/pageArButtonInfo")
    public Result<TableDataInfo> pageArButtonInfo(@RequestBody ArButtonInfo buttonInfo) {
        List<ArButtonInfo> arTagsInfoList = buttonService.pageArButtonInfo(buttonInfo);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(arTagsInfoList);
        rspData.setTotal((new PageInfo(arTagsInfoList)).getTotal());
        return Result.data(rspData);
    }

    @GetMapping("/getArButtonInfoByDeviceCode/{deviceCode}")
    public Result<List<ArButtonInfo>> getArButtonInfoByDeviceCode(@PathVariable String deviceCode) {
        List<ArButtonInfo> list = buttonService.getArButtonInfoByDeviceCode(deviceCode);
        return Result.data(list);
    }
}
