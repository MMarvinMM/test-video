package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 16:55
 */
@Mapper
public interface ISysUserMapper {


    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SysUser selectUserOnlyByUserName(String userName);
}
