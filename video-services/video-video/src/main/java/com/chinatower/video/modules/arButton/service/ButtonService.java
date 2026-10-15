package com.chinatower.video.modules.arButton.service;

import com.chinatower.video.modules.arButton.entity.ArButtonInfo;

import java.util.List;
import java.util.Map;

/**
 * @program: video-platform
 * @ClassName: ButtonService
 * @description: AR大屏，配置按钮服务层接口
 * @author: fengty
 * @create: 2024-04-07 11:02
 */
public interface ButtonService {

    Map<String, Object> addButton(ArButtonInfo buttonInfo) throws Exception;

    Map<String, Object> deleteButton(Long id) throws Exception;

    Map<String, Object> updateButton(ArButtonInfo buttonInfo) throws Exception;

    public ArButtonInfo getButtonById(Long id);

    public int getCountByConditions(ArButtonInfo arButtonInfo);

    List<ArButtonInfo> pageArButtonInfo(ArButtonInfo buttonInfo);

    Map<String, Object> turnButtonStatus(ArButtonInfo buttonInfo) throws Exception;

    List<ArButtonInfo> getArButtonInfoByDeviceCode(String deviceCode);
}
