package com.chinatower.platform.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.request.MsgSwitchRequest;
import com.chinatower.platform.entity.request.SysMsgSwitch;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface MsgSwitchMapper extends BaseMapper<SysMsgSwitch> {
    List<SysMsgSwitch> queryByUserList(List<MsgSwitchRequest> userList);
}
