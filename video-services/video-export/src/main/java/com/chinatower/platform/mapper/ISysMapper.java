package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基础库查询mapper
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:28
 */
@Mapper
@DS(TableEnum.BASE)
public interface ISysMapper {

    /**
     * 根据组织查询用户
     * @param deptId
     * @return
     */
    List<SysUser> selectUserByDeptId(String deptId);

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    SysUser selectUserOnlyByUserName(String userName);

    /**
     * 根据行业编码查询
     * @param industryCode
     * @return
     */
    SysIndustryConf getByIndustryCode(@Param("industryCode") String industryCode, @Param("localNode") String localNode);

}
