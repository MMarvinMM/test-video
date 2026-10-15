package com.chinatower.device.repository;



import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.UavSecureStrategyInfoVO;



/**
 * 无人机安全策略服务接口
 */
public interface UavSecureStrategyService {

    /**
     * 查询无人机安全策略详情
     * @param param 无人机编码
     * @return 无人机安全策略详情信息
     */
    UavSecureStrategyInfoVO queryUavSecureStrategyDetail(UavDeviceDTO param);

    /**
     * 批量更新安全策略
     * @param param
     * @return
     */
    Result<String> batchModifyUavSecureStrategy(UavSecureStrategyInfoVO param);
}
