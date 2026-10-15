package com.chinatower.cascade.modules.config.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.cascade.api.entity.AllPlatformQueryVO;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.cascade.api.entity.PlatformDataVO;
import com.chinatower.cascade.modules.config.service.ICascadeConfigService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.dto.ColonyRelationDto;
import com.chinatower.platform.model.CascadeResult;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * video-cascade-config
 * 功能说明：平台配置信息controller
 *
 * @author hued
 * @date 2023/10/24
 * @requestID
 */
@Api(tags = "平台配置信息")
@RestController
@RequestMapping("/platformData")
@Slf4j
public class CascadeConfigController extends TowerBaseController {
    @Autowired
    ICascadeConfigService cascadeConfigService;

    /**
     * 平台配置信息同步
     * @author hued
     * @param
     * @return
     * @throws
     * @requestID
     */
    @ApiOperation(value = "平台配置信息同步")
    @PostMapping("/sync")
    public CascadeResult sync(@RequestBody ColonyRelationDto colonyRelationDto) {
        log.info("平台配置信息同步入参：" + JSON.toJSONString(colonyRelationDto));
        return cascadeConfigService.sync(colonyRelationDto);
    }

    /**
     * 平台配置信息详情查询
     *
     * @author hued
     * @param
     * @return
     * @throws
     * @requestID
     */
    @ApiOperation(value = "平台配置信息详情查询")
    @PostMapping("/getInfo")
    public Result<List<PlatformDataConfigVO>> getInfo(@RequestBody List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Result.fail("请求参数错误！");
        }
        return cascadeConfigService.getInfo(list);
    }

    /**
     * 平台配置信息树形结构查询
     *
     * @author hued
     * @param
     * @return
     * @throws
     * @requestID
     */
    @ApiOperation(value = "平台配置信息树形结构查询")
    @PostMapping("/queryAllTree")
    public Result<List<TreeSelect>> getPlatformTree() {
        return cascadeConfigService.getPlatformTree();
    }

    /**
     * 平台配置信息列表查询
     *
     * @author hued
     * @param
     * @return
     * @throws
     * @requestID
     */
    @ApiOperation(value = "平台配置信息列表查询")
    @PostMapping("/queryListByKey")
    public Result<List<PlatformDataVO>> queryListByKey(@Valid @RequestBody PlatformDataVO vo) {
        return cascadeConfigService.queryListByKey(vo.getKey());
    }

    /**
     * 查询全部平台信息，供app打开使用
     *
     * @author hued
     * @param
     * @return
     * @throws
     * @requestID
     */
    @ApiOperation(value = "平台配置信息列表查询")
    @PostMapping("/queryAll")
    public Result<String> queryAll(@RequestBody AllPlatformQueryVO allPlatformQueryVO) {
        String encStr = cascadeConfigService.queryAll(allPlatformQueryVO.getToken());
        return Result.data(encStr);
    }


    /**
     * 根据节点编码查询平台配置信息
     *
     * @param vo
     * @return
     */
    @ApiOperation(value = "根据节点编码查询平台配置信息")
    @PostMapping("/exist")
    public Result<Boolean> queryByPlatformCode(@RequestBody PlatformDataVO vo) {
        return Result.data(cascadeConfigService.queryByPlatformCode(vo.getPlatformCode()));
    }
    public static void main(String[] args) {
        String str = UUID.randomUUID().toString().replace("-", "");
        String time = "20240111085530";
        String sk = "2178eh23iudh287932d123cd";
        String token = AESUtil.encryptBase64(str + "-" + time + "-" + sk);
        System.out.println(token);


        String s = "aqvxK8cNJXjhpQehKc73PEsCUtKzeXIsNOqkcVSrnINML56qMIlAFa6M0ydEIlEACzMhnj4sFmG6r2OCNIf/qFOacEATdzGmURDSEc6lCtYRHSQvyBU6Dw3nBYJHp4p3UJ/hy13/5M/fcElWkV5LD04mhPaYGB70ivdgqiJRNvNs9J47B5EaR4lVcy1nDDfcTsrDll2fD+h24DP9RYixQpIH65ztUiFFkdfx/v+o9tIip+n26cINAgkNkUcgD2AzMFtC/VB9OnqiYFhjELmTJkkFBKC7CIGcfILVKtbw9GxRVR/bQ2nL7L+eziO30MrUkI03MpHESNSLH+7+hLY7djDbegD+2RhRA6qxTJQoulHpK4gyq1APruwb46p7D830lb9LuBGwFqsi710GWYkBLEkFBKC7CIGcfILVKtbw9GwKLVznZgqNUUuQe2N3ejSDmLnQ6aKgJq3f7KW2r3GPGOZNEcyCrRLd7l504mexeoDpK4gyq1APruwb46p7D830lb9LuBGwFqsi710GWYkBLEkFBKC7CIGcfILVKtbw9Gy/I7Vdx+ak1f91M1uxpXvJAV0GBxOxPpFzItsw2pVSvvwkAaTkd0k+ANH13FGezMP67lV4INihECzga+hzSEMTcPDLR0Lb99NdXwS1h1DY2nMQoUsn9rY9fInVbj4b2wlRdg8+Xe/Betn1NQiKlq/d/HJplPf1F+W5NKc7TgFMGieEq+ksO0sGUja6bFicLLsKH5yjxFLi+obXJc4ZCn2j6SuIMqtQD67sG+Oqew/N9I9RntfmnZoNzyu3KS7iAW/ssniMHQVyoKWUpfY5/gd32NSw/hOLhg5pta5TYo3zr+FRcqnP/3L58jl8ngK0V8JcG2TFgzB304XU+Og0UrirQh09ekEjeL1y/pjauUSKK4d+AMVXCQB6by6UaXVthvvZCGSJeV4tF/8/62m/JcnPdrrcWMFg+TKbT/pY21YFqQ==";
        String s1 = AESUtil.decryptBase64(s);
        System.out.println(s1);
    }
}
