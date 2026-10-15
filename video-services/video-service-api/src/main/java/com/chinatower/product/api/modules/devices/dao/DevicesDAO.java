package com.chinatower.product.api.modules.devices.dao;

import com.chinatower.product.api.modules.videosquare.entity.PlatformControlVO;

import java.util.List;
import java.util.Map;

public interface DevicesDAO {

    int insertLog(Map map);

    List<PlatformControlVO> queryPlatformControl(PlatformControlVO platformControlVO);

    int addPlatformControl(PlatformControlVO platformControlVO);

    String getUUID();

}
