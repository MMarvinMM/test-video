package com.chinatower.event.api.model.receive;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/27 17:09
 */

@Data
public class WarningLinkInfoReq {

    private List<ReceiveAlarmVO> alarms;
    private List<Map> recordList;

}
