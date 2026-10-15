package com.chinatower.platform.service;

import com.chinatower.event.entity.dto.IntercepiResultVo;
import com.chinatower.event.entity.dto.WarningInterceptDTO;
import com.chinatower.event.entity.vo.InterceptVO;

public interface InterceptService {

    IntercepiResultVo getInterceptList(WarningInterceptDTO dto);

}
