package com.chinatower.platform.project.service;

import com.chinatower.platform.entity.request.MsgSwitchRequest;
import com.chinatower.platform.entity.request.SysMsgSwitch;
import com.chinatower.platform.project.vo.MsgSwitchVO;

import java.util.List;
import java.util.Map;

public interface MsgSwitchService {

    SysMsgSwitch findByUser();

    void updateByUser(MsgSwitchVO msgSwitchVO);

    Map<String, SysMsgSwitch> queryByUserList(List<MsgSwitchRequest> list);
}
