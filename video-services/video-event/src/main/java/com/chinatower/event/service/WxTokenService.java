package com.chinatower.event.service;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.ReceiveAlarmVOReq;
import com.chinatower.event.vo.GetWeChatSignatureReq;

import java.util.Map;

/**
 * @Description 告警相关服务接口
 * @Author guyuefei
 * @Date 2022/7/11
 * @Version release_v1.5
 **/
public interface WxTokenService {

    Map xcxToken(GetWeChatSignatureReq req);
}
