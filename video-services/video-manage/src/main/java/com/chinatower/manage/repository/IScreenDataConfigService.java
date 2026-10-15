package com.chinatower.manage.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.dto.ScreenDataConfigCountVO;
import com.chinatower.manage.dto.ScreenDataConfigVO;

import java.util.List;

public interface IScreenDataConfigService {
    List<ScreenDataConfigCountVO> selectScreenDataConfigCount(ScreenDataConfigCountVO screenDataConfigCount);

    Result selectConfigById(ScreenDataConfigVO screenDataConfig);

    Result addConfig(List<ScreenDataConfigVO> screenDataConfigList);

    Result editConfig(ScreenDataConfigVO screenDataConfig);

    Result editThirdPartyAlarmConfig(List<ScreenDataConfigVO> screenDataConfigList);
}
