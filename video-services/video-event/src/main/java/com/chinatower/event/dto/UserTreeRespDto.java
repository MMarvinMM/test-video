package com.chinatower.event.dto;

import com.chinatower.event.entity.vo.SysInfoVo;
import lombok.Data;

@Data
public class UserTreeRespDto {

    /**
     * 所有有权限用户
     */
    private SysInfoVo sysPowerInfo;
    /**
     * 所有配置用户
     */
    private SysInfoVo sysAllInfo;
}
