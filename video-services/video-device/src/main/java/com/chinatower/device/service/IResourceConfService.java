package com.chinatower.device.service;

import com.chinatower.device.dto.ResourceConfVO;

import java.util.List;

public interface IResourceConfService {

    List<ResourceConfVO> selectResourceList(ResourceConfVO resourceConfVO);
}
