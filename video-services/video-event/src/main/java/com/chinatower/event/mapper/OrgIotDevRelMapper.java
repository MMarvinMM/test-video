package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;

import java.util.List;

/**
 * <p>
 * 租户物联设备关系表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-08
 */
@DS(EbikeDataBaseConstant.BASE)
public interface OrgIotDevRelMapper {

    /**
     * 查找物联设备组织关系
     * @param: deviceReq
     * @return: List<OrgIotDevRelVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<OrganDeviceRelVO> queryOrgIotDevRels(IotDeviceVO deviceReq);
}
