package com.chinatower.event.dto.ThreadDto;

import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.platform.model.dto.LoginUser;
import lombok.Data;

@Data
public class Param {
    private AlarmOrderDTO dto;
    private TableDataInfo info;
    private LoginUser user;
}
