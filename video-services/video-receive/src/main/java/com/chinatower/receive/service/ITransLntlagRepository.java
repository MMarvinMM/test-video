package com.chinatower.receive.service;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.entity.TranslnglatVO;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TransLntlagRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 19:36
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface ITransLntlagRepository {

    /**
     * 查询真实经纬度
     * @param orderId
     * @return
     */
    TranslnglatVO getRelaLntlag(String orderId);

    /**
     * 获取真实经纬度
     * @param param
     * @return
     */
    TranslnglatVO getAiRelaLntlag(ReceiveAlarmVO param);
}
