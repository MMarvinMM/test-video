package com.chinatower.receive.util;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.vo.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 10:52
 */

@Slf4j
@Component
public class SendLocalOkhttp {

    @Autowired
    private NetConnectUtils connectUtils;

    /**
     * 运管调用方式
     *
     * @param
     * @param
     * @return
     */
    public ResultEntity doPostGateWay(NetConnectUtils.RequestParamter requestParamter, String toPlatformCode, String platformCode) {
        ResultEntity cr = new ResultEntity();
        try {
            cr = connectUtils.post(requestParamter, ResultEntity.class, NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toPlatformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("newlandBootInner"),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(requestParamter)));
            log.info("调用网关结果：" + JSONObject.toJSONString(cr));
        } catch (Exception e) {
            log.error("调用网关异常:{}", e);
        }
        return cr;
    }

}
