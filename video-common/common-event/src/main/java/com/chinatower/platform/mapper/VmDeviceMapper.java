package com.chinatower.platform.mapper;

import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.platform.entity.VmDeviceSiteVO;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 15:15
 */
public interface VmDeviceMapper {


    /**
     * 通过网格id获取网格员对象
     * @param gridId
     * @return
     */
    String getGridUserByGridId(String gridId);

    List<SysUser> getUserInfoByLongRoleIdsAndIndustryCode(@Param("roleIdList")List<Long> roleIdList, @Param("tenantId")String tenantId, @Param("industryCode")String industryCode);


    /**
     * 查询摄像机信息
     * @param deviceCodeList
     * @return
     */
    List<VmDeviceSiteVO> queryDeviceList(@Param("deviceCodeList") List<String> deviceCodeList);


    /**
     * 根据用户ID查询用户信息
     * @param userIdList
     * @return
     */
    List<SysUserVO> getUserInfoByUserIds(@Param("userIdList")List<String> userIdList);

    String getWarningTypeName(@Param("warningTypeId") String warningTypeId);
}
