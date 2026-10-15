package com.chinatower.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.admin.SysInterfaceInfo;
import com.chinatower.system.api.resp.SysUserResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysInterfaceConfInfoMapper extends BaseMapper<SysInterfaceInfo> {
    List<SysUserResponse> getApiUserInfo(SysUserResponse sysUserResponse);
}
