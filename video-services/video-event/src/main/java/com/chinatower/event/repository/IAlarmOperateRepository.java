package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.core.domain.AlarmOperateDO;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.OrderInfoVO;

import java.util.Map;

/**
 * <p>
 * 运营研判表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
public interface IAlarmOperateRepository extends IService<AlarmOperateDO> {

    /**
     * 调用6.13接口获取图片详情
     * @param orderInfoVO
     * @return
     */
    void getAlarmImgs(OrderInfoVO orderInfoVO);

    /**
     * 研判：假警、真警、重复告警处理
     * @param map
     * @return
     */
    Result checkAlarmOrder(Map map);
}
