package com.chinatower.receive.service;

import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.VmAlarmAreaSuppressVO;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 17:01
 */
public interface StrategyService {

    void groupPT(VmAlarmAreaSuppressVO areaTemp);

}
