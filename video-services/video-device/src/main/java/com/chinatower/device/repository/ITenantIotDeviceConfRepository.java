package com.chinatower.device.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.dataobject.TenantIotDeviceConfDO;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;

import java.util.List;

/**
 * <p>
 * 物联设备联动配置表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
public interface ITenantIotDeviceConfRepository extends IService<TenantIotDeviceConfDO> {

    /**
     * 获取联动配置
     * @param: qryCond
     * @return: TenantIotDeviceConfVO
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    TenantIotDeviceConfVO getTenantIotDevConf(TenantIotDeviceConfVO qryCond);


    /**
     * 批量查询物联设备数据
     * @param qryCond
     * @return
     */
    List<TenantIotDeviceConfVO> getTenantIotDevConfBatch(TenantIotDeviceConfVO qryCond);

    /**
     * 批量查询物联设备数据
     * @param qryCond
     * @return
     */
    List<TenantIotDeviceConfVO> getTenantIotDevConfBatchV2(TenantIotDeviceConfVO qryCond);

    List<TenantIotConfRelVO> getTenantIotRelConf(TenantIotDeviceConfVO qryCond);

    /**
     * 保存联动配置
     * @param: tenIotDevConf
     * @return: TenantIotDeviceConfVO
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    void saveTenantIotDevConf(TenantIotDeviceConfVO tenIotDevConf);
}
