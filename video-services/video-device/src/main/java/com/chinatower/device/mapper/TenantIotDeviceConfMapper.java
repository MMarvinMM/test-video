package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.TenantIotDeviceConfDO;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;

import java.util.List;

/**
 * <p>
 * 物联设备联动配置表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
public interface TenantIotDeviceConfMapper extends BaseMapper<TenantIotDeviceConfDO> {
    /**
     * 获取联动配置
     * @param: qryCond
     * @return: TenantIotDeviceConfVO
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<TenantIotDeviceConfVO> getTenantIotDevConf(TenantIotDeviceConfVO qryCond);

    List<TenantIotDeviceConfVO> getTenantIotDevConfV2(TenantIotDeviceConfVO qryCond);

    List<TenantIotConfRelVO> getTenantIotRelConf(TenantIotDeviceConfVO qryCond);
}
