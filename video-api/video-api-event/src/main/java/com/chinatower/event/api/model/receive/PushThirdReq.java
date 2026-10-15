package com.chinatower.event.api.model.receive;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/27 17:06
 */

@Data
public class PushThirdReq {

    private String originalJson;
    private List<ReceiveAlarmVO> list;
}
