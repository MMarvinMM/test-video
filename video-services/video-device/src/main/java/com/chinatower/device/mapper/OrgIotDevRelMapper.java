package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.OrganDeviceRelDO;
import com.chinatower.device.dataobject.OrganIotDevRelDO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;

import java.util.List;


/**
 * <p>
 * 物联设备部门关系表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-08
 */
public interface OrgIotDevRelMapper extends BaseMapper<OrganIotDevRelDO> {

    /**
     * 获取租户物联设备部门
     * @param: deviceReq
     * @return: List<OrgIotDevRelVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<OrganDeviceRelVO> queryOrgIotDevRels(IotDeviceVO deviceReq);
}
