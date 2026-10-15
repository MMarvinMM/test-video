package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.FlowLinkConfigModel;
import com.chinatower.event.service.WxTokenService;
import com.chinatower.event.vo.GetWeChatSignatureReq;
import com.chinatower.platform.constants.Constants;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * video-event
 * 告警加密分享控制器
 *
 * @author yhb
 * @version 1.0
 * @date 2023/01/04 09:52
 */
@RestController
@Slf4j
public class WxTokenController {

    @Autowired
    private WxTokenService wxTokenService;

    /**
     * 功能描述：小程序登录(示例)
     *
     * @param
     * @return 结果
     */
    @ApiOperation("小程序登录(示例)")
    @PostMapping("/xcxToken")
    public Result<Map> xcxToken(@RequestBody GetWeChatSignatureReq req) {
        Result<Map> result = null;
        // 生成令牌
        try {
            Map resultMap = wxTokenService.xcxToken(req);
            result = Result.success("操作成功");
            result.setData(resultMap);
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
}
