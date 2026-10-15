package com.chinatower.product.api.modules.controller;

import cn.hutool.core.util.HexUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.ITowerDeviceService;
import com.chinatower.device.entity.vo.DeviceYjbReq;
import com.chinatower.device.entity.vo.DeviceYjbRsp;
import com.chinatower.device.entity.vo.YjbSnapshotTaskReq;
import com.chinatower.device.entity.vo.YjbSnapshotTaskRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * video-service-api
 */
@RestController
public class DeviceController {

    @Resource
    ITowerDeviceService towerDeviceService;

    @Autowired
    private HttpServletRequest req;

    @Value("${api.sk}")
    private String secretKey;

    /**
     * 摄像机查询接口 for 应急部
     */
    @PostMapping("/device/getDeviceListForYJB")
    public Result<List<DeviceYjbRsp>> getDeviceListForYJB(@RequestBody DeviceYjbReq deviceYjbReq) {
        return towerDeviceService.getDeviceListForYJB(deviceYjbReq);
    }

    /**
     * 应急部创建摄像机抓图任务。
     *
     * @param request 设备通道组合列表
     * @return 任务编码及下发失败明细
     */
    @PostMapping("/device/createSnapshotTaskForYJB")
    public Result<YjbSnapshotTaskRsp> createSnapshotTaskForYJB(@RequestBody YjbSnapshotTaskReq request) {
        return towerDeviceService.createSnapshotTaskForYJB(request);
    }

    /**
     * 应急部查询摄像机抓图任务进度及已完成图片。
     *
     * @param request 服务端返回的任务编码
     * @return 任务进度及已完成抓图明细
     */
    @PostMapping("/device/querySnapshotTaskForYJB")
    public Result<YjbSnapshotTaskRsp> querySnapshotTaskForYJB(@RequestBody YjbSnapshotTaskReq request) {
        return towerDeviceService.querySnapshotTaskForYJB(request);
    }

    @PostMapping("/device/getSignatureForYJB")
    public Result<String> getSignatureForYJB() {
        String xRequestTime = req.getHeader("X-RequestTime");
        String xAppKey = req.getHeader("X-AppKey");
        String xMsgId = req.getHeader("X-MsgId");
        String url = req.getRequestURI();

        String signContext = getSignContext(url, xAppKey, xMsgId, xRequestTime);
        String sign = getGenerateSign(secretKey, signContext);
        return Result.data(sign);
    }
    /**
     * 签名字符串生成规则.
     *
     * @param appkey .
     * @return 待签名字符串
     */
    private static String getSignContext(String path, String appkey, String msgId, String requestTime) {
        StringBuilder append = new StringBuilder(path)
                .append(appkey)
                .append(msgId)
                .append(requestTime);
        System.out.println(append);
        return append.toString().toUpperCase();
    }
    /**
     * 签名
     *
     * @param secret
     * @param signContext
     * @return
     */
    public static String getGenerateSign(final String secret, final String signContext) {
        String sign = null;
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA1");
            byte[] keyBytes = secret.getBytes();
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA1"));
            sign = HexUtil.encodeHexStr(hmacSha256.doFinal(signContext.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }
}
