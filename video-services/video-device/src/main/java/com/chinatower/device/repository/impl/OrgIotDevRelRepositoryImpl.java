package com.chinatower.device.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.device.dataobject.OrganDeviceRelDO;
import com.chinatower.device.dataobject.OrganIotDevRelDO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.mapper.OrgIotDevRelMapper;
import com.chinatower.device.repository.IOrgIotDevRelRepository;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 租户物联设备关系表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-08
 */
@Service
public class OrgIotDevRelRepositoryImpl extends ServiceImpl<OrgIotDevRelMapper, OrganIotDevRelDO> implements IOrgIotDevRelRepository {

    @Override
    public List<OrganDeviceRelVO> queryOrgIotDevRels(IotDeviceVO deviceReq) {
        return this.baseMapper.queryOrgIotDevRels(deviceReq);
    }
}
